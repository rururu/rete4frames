(ns rete.core
  (:use clojure.java.io))

(declare TEMPL ACNT ANET AMEM)

(def MAXSAL 10) ;; Salience range [-10, 10]

(def STRATEGY 'DEPTH) ;; Alternative 'BREADTH

(def TRACE nil)

(defn vari? [x]
  "Is x variable? Transtime function"
  (and (symbol? x) (.startsWith (name x) "?")))

(defn prod-name [prod]
  "Get name of production"
  (first prod))

(defn salience [prod]
  "Get salience of production"
  (second prod))

(defn lhs [prod]
  "Get left hand side of production"
  (nnext (take-while #(not (=  % '=>)) prod)))

(defn rhs [prod]
  "Get right hand side of production"
  (rest (drop-while #(not (=  % '=>)) prod)))

(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defn templ-map [slots]
  "Created sorted map from list of slots with '? values"
  (let [itl (interleave slots (repeat :?))]
    (apply sorted-map itl)))

(defn to-funarg [typ mp]
  ;;(println [:TO-FUNARG typ mp])
  (cons typ (vals (merge (TEMPL typ) mp))))

(defn tree-put [funarg value mem]
  "Put into tree-like from map made memory"
  (reset! mem (assoc-in @mem funarg value)))

(defn tree-get [funarg mem]
  "Get from tree-like from map made memory"
  (get-in @mem funarg))

(defn tree-rem [funarg mem]
  "Remove from tree-like from map made memory"
  (reset! mem (dissoc-in @mem funarg)))

(defn tree-match [patt t-mem ctx]
  "Search fact in tree-type memory matching to pattern with respect to ctx"
  ;;(println [:TREE-MATCH :PATT patt :T-MEM t-mem :CTX ctx])
  (loop [pp patt mem t-mem]
    (if (number? mem)
      (let [fids (ctx :?fids)]
        (if (not (some #{mem} fids))
          [[mem (assoc ctx :?fids (cons mem fids))]]))
      (if (seq pp)
        (let [p1 (first pp)
              p2 (or (ctx p1) p1)]
          (cond
           (= p2 :?)
           (mapcat #(tree-match (rest pp) (mem %) ctx) (keys mem))
           (keyword? p2)
           (mapcat #(tree-match (rest pp) (mem %) (assoc ctx p2 %)) (keys mem))
           true
           (if-let [mem2 (mem p2)]
             (recur (rest pp) mem2))) )) )))

(defn template
  "Select template part of condition"
  [condition]
  (if (even? (count condition))
    (butlast condition)
    condition))

(defn mk-funarg [frame]
  "Create Funarg (list of type and odered by TEMPL slot values)
   from frame (list of type and keys with values)"
  (let [[typ & rst] frame
        mp (apply hash-map rst)]
    (to-funarg typ mp)))

(defn to-typmap [[typ & vls]]
  "Create Typmap (list of type and map of slots) from funarg"
  (let [kk (keys (TEMPL typ))
        kv (interleave kk vls)
        mp (apply hash-map kv)]
    [typ mp]))

(defn univars [cond]
  "Reduces all different variables to '?"
  (map #(if (vari? %) :? %) cond))

(defn slot-in-templ [pair typ]
  "For pair of slot and value checks if slot is in template of typ.
   If so returns pair of slot and value else nil"
  ;;(println [:SLOT-IN-TEMPL pair typ])
  (let [templ (TEMPL typ)
        slot (first pair)]
    (if (some #{slot} (keys templ))
      pair
      (println (str "Slot " slot " is not in template " typ "!")))))

(defn add-anet-entry [condition]
  "If condition in a left hand side of a rule is a pattern (not test with a function on the predicate place)
  adds a new entry to the map representing the alpha net."
  (let [cnd (if (= (first condition) 'not)
              (rest condition)
              condition)
        typ (first cnd)
        cnd2 (cons typ (apply concat (filter #(slot-in-templ % typ) (partition 2 (rest cnd)))))
        funarg (mk-funarg (univars (template cnd2)))]
    (when (nil? (tree-get funarg ANET))
      (tree-put funarg @ACNT ANET)
      (swap! ACNT inc))))

(defn anet-for-pset
  "Build the alpha net for the given production set (rule set) <pset> as a map"
  [pset]
  (doseq [pp pset]
    (if TRACE (println (str "\n" [:PRODUCTION pp])))
    (doseq [condition (lhs pp)]
      (if TRACE (println [:condition condition]))
      (add-anet-entry condition)) ))

(defn a-indexof-pattern [pattern]
  "Find an alpha memory cell index for a pattern from a left hand side of some rule"
  ;;(println [:A-INDEXOF-PATTERN :PATTERN pattern])
  (let [funarg (mk-funarg (univars pattern))]
    (tree-get funarg ANET)))

(defn collect-vars
  "Returns vector of variables in expression"
  ([ex]
   (vec (set (collect-vars ex nil))))
  ([ex yet]
   (cond
    (and (or (seq? ex) (vector? ex) (= (type ex) clojure.lang.PersistentHashSet))
         (not (empty? ex)))
      (collect-vars (first ex) (collect-vars (rest ex) yet))
    (map? ex) (collect-vars (vals ex) yet)
    (vari? ex) (cons ex yet)
    true yet)))

(defn qq [aa vars]
  "Add call to quote for symbols not variables"
  (let [f1 (fn [x]
             (cond
              (some #{x} vars) x
              (vari? x) x
              (symbol? x) (list 'quote x)
              true  x))]
    (map f1 aa)))

(defn and-or [x vrs]
  "Translate list-vector form of condition to and-or form"
  ;;(println [:AND-OR x vrs])
  (cond
   (list? x)
     (cond
      (symbol? (first x)) (cons (first x) (qq (rest x) vrs))
      true (cons 'and (map #(and-or % vrs) x)))
   (vector? x) (cons 'or (map #(and-or % vrs) x))
   true x))

(defn mk-test-func [tst vrs]
  (let [aof (and-or tst vrs)
        df (list 'fn vrs aof)]
    (if TRACE (println [:TEST-FUNCTION df]))
    (let [cf (eval df)]
      (if TRACE (println [:COMPILED cf]))
      cf)))

(defn var-to-key [e]
  (if (keyword? e)
    (do (println (str "Keywords forbidden in LHS: " e)) (name e))
    (if (and (symbol? e) (.startsWith (name e) "?"))
      (keyword e)
      e)))

(defn mk-pattern-and-test [condition]
  "Make pattern or test"
  ;;(println [:MK-PATTERN-AND-TEST condition])
  (if (= (first condition) 'not)
    (let [[apid patt] (mk-pattern-and-test (rest condition))]
      [apid (cons 'not patt)])
    (let [[frame test]
            (if (even? (count condition))
              [(butlast condition) (last condition)]
              [condition nil])
          rst (if test
                (let [vrs (collect-vars test)
                      vks (map keyword vrs)]
                  [vks (mk-test-func test vrs)])
                [nil nil])
          frame (map var-to-key frame)
          patt (cons (mk-funarg frame) rst)
          apid (a-indexof-pattern (template condition))]
        (list apid patt))))

(defn enl [lst]
  "Add numbers to lists"
  (map cons (range (count lst)) lst))

(defn mk-rhs-func [vrs rhs]
  "Create function from vector of variables and right hand side"
  ;;(println [:MK-RHS-FUNC vrs rhs])
  (let [df (cons 'fn (cons vrs rhs))]
    (if TRACE (println [:RHS-FUNCTION df]))
    (let [cf (eval df)]
      (if TRACE (println [:COMPILED cf]))
      cf)))

(defn check-sal [sal]
  "Check salience in [-MAXSAL, +MAXSAL] and correct to this interval"
  (cond
   (> sal MAXSAL)
     (do (println (str "Salience " sal " is more than maximum " MAXSAL ", corrected to " MAXSAL))
       MAXSAL)
   (< sal (- MAXSAL))
     (do (println (str "Salience " sal " is less than minimum " (- MAXSAL) ", corrected to " (- MAXSAL)))
       (- MAXSAL))
   true sal))

(defn beta-net-plan
  "Create a plan of the beta net that will be used to its building.
   The plan describes the beta net as a list, mean while the real beta net is an array.
   The plan is the list of lists each of which represents one cell of the beta memory.
   First element of each list is an index of the beta memory, rest of each list is a content of the corresponding cell.
   Recalculate salience into positive for using as CFARR index"
  ([pset]
    (enl (mapcat
           #(beta-net-plan
             (prod-name %)
             (+ (check-sal (salience %)) MAXSAL)
             (lhs %)
             (rhs %))
           pset)))
  ([pname sal lhs rhs]
    (if TRACE (println (str "\n" [:PRODUCTION pname])))
    (let [pts (map mk-pattern-and-test lhs)
          fir (concat (first pts) [pname])
          mid (butlast (rest pts))
          vrs (collect-vars rhs)
          las (concat (last pts)
                      (list pname sal (map keyword vrs) (mk-rhs-func vrs rhs)))]
      (if (= (count lhs) 1)
        (list (cons 'ex las))
        (concat (list (cons 'e fir))
                (map #(cons 'i %) mid)
                (list (cons 'x las)) )) )))

(defn fill-ablink-abnotl
  "Fill alpha-beta links table from beta net plan,
  separately alpha-beta links map for negative patterns"
  ([bplan ablink abnotl]
    (dotimes [i (count ablink)]
      (fill-ablink-abnotl bplan ablink abnotl i)))
  ([bplan ablink abnotl i]
    (let [flt (filter #(= (nth % 2) i) bplan)
          ynot (filter #(= (first (nth % 3)) 'not) flt)
          nnot (filter #(not= (first (nth % 3)) 'not) flt)]
      (aset ablink i (map first nnot))
      (if (seq ynot)
        (swap! abnotl assoc i (map first ynot)) )) ))

(defn fill-bnet [bnet bplan]
  "Fill beta net from beta net plan"
  (doseq [[i & v] bplan]
    (aset bnet i v)))

(defn log-lst [path x]
  "Log list"
  (let [fos (writer path)]
    (doall (map #(.write fos (str % "\n")) x))
    (.close fos)))

(defn log-hm [path x]
  "Log HashMap"
  (let [fos (writer path)]
    (doseq [[k v] (into {} x)]
      (.write fos (str k ":" "\n"))
      (doseq [[k2 v2] v]
        (.write fos (str " " k2 ": " v2 "\n"))))
    (.close fos)))

(defn log-array [path a]
  "Log array"
  (let [fos (writer path)]
    (dotimes [i (count a)]
      (.write fos (str i " " (seq (aget a i)) "\n")))
    (.close fos)))

(declare reset)

(defn create-rete
  "Create RETE from a production set and reset"
  [tset pset]
  (try
    (if TRACE (println "\n.... Creating TEMPLATES for Pset ....\n"))
    (def TEMPL
      (apply hash-map (mapcat #(list (first %) (templ-map (rest %))) tset)))
    (if TRACE (println "\n.... Creating ANET PLAN for Pset ...."))
    (def ANET (atom {}))
    (def ACNT (atom 0))
    (anet-for-pset pset)
    (when TRACE
      (log-hm "alpha-net-plan.txt" @ANET)
      (println "\n.... Creating BNET PLAN for Pset ...."))
    (def BPLAN (beta-net-plan pset))
    (when TRACE
      (log-lst "beta-net-plan.txt" BPLAN)
      (println "\n.... Creating BNET ANET LINK PLAN for Pset ....\n"))
    (def ABLINK (object-array @ACNT))
    (def ABNOTL (atom {}))
    (def BCNT (count BPLAN))
    (def BNET (object-array BCNT))
    (fill-bnet BNET BPLAN)
    (fill-ablink-abnotl BPLAN ABLINK ABNOTL)
    (when TRACE
      (log-array "alpha-beta-links.txt" ABLINK)
      (println "\n.... Log Files Created ....\n"))
    (reset)
    (if TRACE (println "\n.... RETE Created and Reset ....\n"))
    [@ACNT BCNT]
    (catch Throwable twe
      (println twe)
      nil)))

(defn reset []
  "Reset: clear and initialize all memories"
  (def AMEM (object-array @ACNT))
  (dotimes [i (alength AMEM)] (aset AMEM i [nil (atom {})]))
  (def BMEM (object-array BCNT))
  (def CFARR (object-array (inc (* 2 MAXSAL))))
  (def IDFACT (atom {}))
  (def FMEM (atom {}))
  (def FCNT (atom 0))
  (def FIDS (atom {})))

(defn mk-fact [funarg]
  "Make fact. Returns new fact [funarg id] id or nil if same fact exists"
  ;;(println [:MK-FACT funarg])
  (if (nil? (tree-get funarg FMEM))
    (let [fid @FCNT]
      (tree-put funarg fid FMEM)
      (swap! IDFACT assoc fid funarg)
      (swap! FCNT inc)
      [funarg fid])))

(defn var-vals [mp vrs]
  "Takes values from context map mp in order of list of variables"
  (map #(mp %) vrs))

(defn try-func-add-fid [func fid ctx vrs bi]
  ;;(println [:TRY-FUNC-ADD-FID func fid ctx vrs bi])
  (if (or (nil? func) (apply func (var-vals ctx vrs)))
    (let [fids (get @FIDS fid)]
      (if (not (some #{bi} fids))
        (reset! FIDS (assoc @FIDS fid (cons bi fids))))
      ctx)))

(defn match-fact-to-pattern [ffuar pfuar]
  "Match funarg of fact to funarg of pattern"
  ;;(println [:match-fact-to-pattern ffuar pfuar])
  (every? #(= % true)
          (map (fn [x y] (or (= x y) (keyword? y)))
               ffuar
               pfuar)))

(defn matched-context [ffuar pfuar ctx]
  "Returns matched context for given fact, pattern and initial context"
  ;;(println [:matched-context ffuar pfuar ctx])
  (let [nfuar (map #(or (ctx %) %) pfuar)]
    (if (match-fact-to-pattern ffuar nfuar)
      (let [new-pairs (fn [x y] (if (and (not= y :?) (not= x :?) (keyword? x))
                                  (assoc {} x y)))
            pairs (filter some? (map new-pairs nfuar ffuar))]
        (if (seq pairs)
          (apply merge (cons ctx pairs))
          ctx)))))

(defn matched-ctx [[ffuar fid] [pfuar vrs func] ctx bi]
  "Match fact with pattern with respect to context"
  ;;(println [:matched-ctx ffuar fid pfun pargs vrs func ctx bi])
  (let [fids (ctx :?fids)]
    (if (not (some #{fid} fids))
      (if-let [ctx2 (matched-context ffuar pfuar ctx)]
        (try-func-add-fid func fid (assoc ctx2 :?fids (cons fid fids)) vrs bi)) ) ))

(defn match-ctx-amem [amem [pfuar vrs func] ctx bi]
  "Match list of facts with pattern with respect to context and beta cell.
  Returns matching contexts"
  ;;(println [:MATCH-CTX-AMEM amem pfuar vrs func ctx bi])
  (if-let [mm (seq (tree-match pfuar @amem ctx))]
    (filter some?
            (for [[fid ctx2] mm]
              (try-func-add-fid func fid ctx2 vrs bi)) ) ))

(defn mk-match-list [amem pattern ctx-list bi]
  "Make match-list of contexts"
  ;;(println [:MK-MATCH-LIST amem pattern ctx-list bi])
  (if (not (empty? @amem))
    (mapcat #(match-ctx-amem amem pattern % bi) ctx-list)))

(defn not-match-ctx-amem [amem [pfuar vrs func] ctx]
  "If not match context alpha memory returns it with remembered what was not existed"
  ;;(println [:NOT-MATCH-CTX-AMEM :AMEM amem :PFUAR pfuar :VRS vrs :FUNC func :CTX ctx])
  (if (or (empty? amem)
          (let [mm (tree-match pfuar amem ctx)]
            (or (empty? mm)
                (and func
                     (empty? (filter #(apply func (var-vals % vrs)) (map second mm)) ))) ))
    (let [ctx2 (if (nil? (ctx :novelty))
                 (assoc ctx :novelty @FCNT)
                 ctx)]
      [(assoc ctx2 :not-existed
         (cons [(cons (first pfuar) (map #(or (ctx %) %) (rest pfuar))) vrs func]
               (:not-existed ctx2)))])))

(defn mk-not-match-list [amem pattern ctx-list]
  "Make match-list of not matching contexts"
  ;;(println [:MK-NOT-MATCH-LIST pattern ctx-list])
  (mapcat #(not-match-ctx-amem @amem pattern %) ctx-list))

(defn sumfids [ctx]
  "Evaluation of activation assesment 'sumfids' depending on strategy"
  (let [fids (ctx :?fids)
        sum (apply + fids)
        k (/ sum (count fids))]
    (if (= STRATEGY 'DEPTH)
      (- k)
      k)))

(defn add-to-confset
  "Add activation to conflict set array"
  [aprod match-list]
  ;;(println [:ADD-TO-CONFSET :APROD aprod :MATCH-LIST (count match-list)])
  ;;(doseq [ctx match-list]
  ;;  (println [:FIDS (ctx :?fids)]))
  (let [sal (salience aprod)
        srt [aprod (sort-by sumfids match-list)]
        do (aget CFARR sal)
        po (if (= STRATEGY 'DEPTH)
             (cons srt do)
             (concat do [srt]))]
    (aset CFARR sal po)))

(declare activate-b)

(defn activate-b-not [bi amem eix pattern tail bi ctx-list]
  "Activate beta net cell for not node"
  ;;(println [:ACTIVATE-B-NOT bi amem eix pattern tail bi (count ctx-list)])
  (let [ml (mk-not-match-list amem pattern ctx-list)]
    (if (seq ml)
      (condp = eix
        'x (add-to-confset tail ml)
        'i (do
             (aset BMEM bi (concat ml (aget BMEM bi)))
             (activate-b (inc bi) ml))) )))

(defn activate-b [bi ctx-list]
  "Activate beta net cell of index <bi> with respect to a list of contexts
  already activated by a new fact with an index <new-fid>"
  ;;(println [:ACTIVATE-B :BI bi :CTX-LIST ctx-list])
  (let [bnode (aget BNET bi)
        [eix bal pattern & tail] bnode]
    (if (= (first pattern) 'not)
      (activate-b-not bi (second (aget AMEM bal)) eix (rest pattern) tail bi ctx-list)
      (let [ml (mk-match-list (second (aget AMEM bal)) pattern ctx-list bi)]
        (if (seq ml)
          (condp = eix
            'x (add-to-confset tail ml)
            'i (do
                 (aset BMEM bi (concat ml (aget BMEM bi)))
                 (activate-b (inc bi) ml))) )) )))

(defn entry-a-action [bi pattern b-mem new-fact]
  "Entry alpha activation"
  ;;(println [:ENTRY-A-ACTION :BI bi :PATTERN pattern :BMEM b-mem :NEW-FACT new-fact])
  (when-let [ctx (matched-ctx new-fact pattern {} bi)]
    (aset BMEM bi (cons ctx b-mem))
    (activate-b (inc bi) (list ctx))))

(defn inter-a-action [bi pattern b-mem new-fact]
  "Intermediate alpha activation"
  ;;(println [:INTER-A-ACTION :BI bi :PATTERN pattern :BMEM b-mem :NEW-FACT new-fact])
  (if-let [ctx-list (seq (aget BMEM (dec bi)))]
    (when-let [ml (seq (keep #(matched-ctx new-fact pattern % bi) ctx-list))]
      (aset BMEM bi (concat ml b-mem))
      (activate-b (inc bi) ml)) ))

(defn exit-a-action [bi pattern tail b-mem new-fact]
  "Exit alpha activation"
  ;;(println [:EXIT-A-ACTION :BI bi :PATTERN pattern :TAIL tail :NEW-FACT new-fact])
  (if-let [ctx-list (seq (aget BMEM (dec bi)))]
    (when-let [ml (seq (keep #(matched-ctx new-fact pattern % bi) ctx-list))]
      (aset BMEM bi (concat ml b-mem))
      (add-to-confset tail ml)) ))

(defn enex-a-action [bi pattern tail new-fact]
  "Entry and exit alpha activation (for LHS with 1 pattern)"
  ;;(println [:ENEX-A-ACTION :PATTERN pattern :TAIL tail :NEW-FACT new-fact])
  (if-let [ctx (matched-ctx new-fact pattern {} bi)]
    (add-to-confset tail (list ctx)) ))

(defn activate-a
  "Activate alpha net cells for index list <ais>"
  [ais]
  ;;(println [:ACTIVATE-A :AIS ais])
  (doseq [ai ais]
    (let [ablinks (aget ABLINK ai)
          bnms (map #(list % (aget BNET %) (aget BMEM %)) ablinks)
          new-fact (first (aget AMEM ai))]
      (doseq [[bi [eix bal pattern & tail] b-mem] bnms]
        (condp = eix
          'e (entry-a-action bi pattern b-mem new-fact)
          'ex (enex-a-action bi pattern tail new-fact)
          'i (inter-a-action bi pattern b-mem new-fact)
          'x (exit-a-action bi pattern tail b-mem new-fact))) )))

(defn a-indices
  "For an asserted funarg find all suitable alpha memory cells"
  ([[fun & args]]
   (if-let [anet (@ANET fun)]
     (a-indices args anet)))
  ([args anet]
   (letfn [(path [args key anet]
                 (if (or (= key :?) (= key (first args)))
                   (a-indices (rest args) (anet key))))]
     (if (number? anet)
       [anet]
       (mapcat #(path args % anet) (keys anet))))))

(defn remove-ctx-with [fid ctxlist]
  "Remove context for given fact id"
  ;;(println [:REMOVE-CTX-WITH :FID fid :CTXLIST ctxlist])
  (doall (filter #(not (some #{fid} (:?fids %))) ctxlist)))

(defn retract-b [fid bis]
  "Retract fact id from the beta memory"
  ;;(println [:RETRACT-B :FID fid :BIS bis])
  (doseq [bi bis]
    (loop [i bi]
      (aset BMEM i (remove-ctx-with fid (aget BMEM i)))
      (let [ni (inc i)]
        (if (< ni BCNT)
          (let [eix (first (aget BNET ni))]
            (if (or (= eix 'i) (= eix 'x))
              (recur ni))) )) )))

(defn remove-fmem [fid]
  "Remove fact from facts memory by fact id.
  Returns funarg of removed fact"
  (let [funarg (@IDFACT fid)]
    (when (seq? funarg)
      (tree-rem funarg FMEM)
      funarg)))

(defn beta-activate-above-not-node [bi funarg]
  "Activate beta-memory above not nodes"
  ;;(println [:BETA-ACTIVATE-ABOVE-NOT-NODE bi funarg])
  (let [[eix bal pattern & tail] (aget BNET bi)
        ctx-list (aget BMEM (dec bi))]
    (if-let [ctxs (seq (filter #(matched-context funarg (second pattern) %) ctx-list))]
      (activate-b-not bi (second (aget AMEM bal)) eix (rest pattern) tail bi ctxs))))

(defn retract-beta-activate [ais funarg]
  "Activate beta-memory above not nodes for list of alpha nodes"
  (doseq [ai ais]
    (doseq [bi (@ABNOTL ai)]
      (beta-activate-above-not-node bi funarg))))

(defn retract-fact [fid beta-flag]
  "Retract fact for given fact-id by removing it from alpha, beta and fact memory,
   and also by removing from conflict set activations, containing this fact-id.
   Also activate beta-memory above not nodes depending on beta flag"
  ;;(println [:RETRACT-FACT fid])
  (if-let [funarg (remove-fmem fid)]
    (let [ais (a-indices funarg)
          amem (atom {})]
      (swap! IDFACT assoc fid :deleted)
      (retract-b fid (get @FIDS fid))
      (reset! FIDS (dissoc @FIDS fid))
      (doseq [ai ais]
        (tree-rem funarg (second (aget AMEM ai)) ))
      (if TRACE (println [:<== :fid fid (to-typmap funarg)]))
      (if beta-flag
        (retract-beta-activate ais funarg))
      funarg)))

(defn ais-for-funarg [funarg]
  "Create fact from funarg and add it to alpha memory.
   Returns list of activated alpha memory cells"
  ;;(println [:AIS-FOR-FUNARG funarg])
  (if-let [fact (mk-fact funarg)]
    (when-let [ais (a-indices funarg)]
      (if TRACE (println [:==> :fid (second fact) (to-typmap funarg)]))
      (doseq [ai ais]
        (let [amem (second (aget AMEM ai))]
          (tree-put funarg (second fact) amem)
          (aset AMEM ai [fact amem])))
      ais)))

(defn assert-frame [frame]
  "Assert frame and activate corresponding alpha nodes"
  (activate-a (ais-for-funarg (mk-funarg frame))))

(defn modify-fact [fid mmp]
  "Modify fact for given fact-id by retracting it and asserting,
   modified frame. Also activate beta memory above corresponding not nodes"
  ;;(println [:MODIFY-FACT fid mmp])
  (if-let [funarg (retract-fact fid false)]
    (let [[typ mp] (to-typmap funarg)
          mp2 (merge mp mmp)
          ais (ais-for-funarg (to-funarg typ mp2))]
      (activate-a ais)
      (retract-beta-activate ais funarg)) ))

(defn assert-list
  "Function for assertion a list of triples or object descriptions (see comments on the function 'asser').
   For the use outside of the right hand side of rules"
  [lst]
  (activate-a (set (mapcat ais-for-funarg lst))))

(defn match-1-not-existed [ffuar [nfuar vars func] ctx]
  "Match funarg of fact to funarg of 1 pattern in a list of not exited"
  ;;(println [:MATCH-1-NOT-EXISTED ffuar nfuar vars func])
  (and (every? #(= % true) (map (fn [x y] (or (= x y) (keyword? y)))
                 ffuar
                 nfuar))
       (or (nil? func)
           (let [hmap (apply hash-map (interleave (rest nfuar) (rest ffuar)))
                 ctx2 (merge hmap ctx)]
             (apply func (var-vals ctx2 vars))))))

(defn match-not-existed [fid not-existed ctx]
  "Match fact of fid with pattern from 'not-existed'"
  (let [ffuar (@IDFACT fid)]
    (if (seq? ffuar)
      (some #(match-1-not-existed ffuar % ctx) not-existed))))

(defn actual [ctx]
  ;;(println [:ACTUAL ctx])
  (if-let [ned (:not-existed ctx)]
    (let [nty (:novelty ctx)
          newf (range nty @FCNT)]
      (not (some #(match-not-existed % ned ctx) newf)))
    true))

(defn not-deleted [ctx]
  (not (some #(= (@IDFACT %) :deleted) (ctx :?fids))))

(defn resolve-for-ctx [ctx-lst]
  "Resolve conflict set context list based on 'novelty' and 'sumfield' assesment.
   Returns vector of context and rest of list or nil"
  ;;(println [:RESOLVE-FOR-CTX ctx-lst newf])
  (loop [ctxs ctx-lst]
    (if (seq ctxs)
      (let [[ctx & rctxs] ctxs]
        (if (and (not-deleted ctx) (actual ctx))
          [ctx rctxs]
          (recur rctxs))) )))

(defn resolve-for-sal [sal alist cfarr]
  "Resolve conflict set for one sailence value.
   Returns resolved context"
  ;;(println [:RESOLVE-FOR-SAL sal alist])
  (loop [srta alist]
    (if (seq srta)
      (let [[[prod ctxs] & rsta] srta]
        (if-let [[ctx rsd] (resolve-for-ctx ctxs)]
          (do
            (if (seq rsd)
              (aset cfarr sal (cons [prod rsd] rsta))
              (aset cfarr sal rsta))
            [prod ctx])
          (recur rsta)))
      (do
        (aset cfarr sal nil)
        nil))))

(defn resolve-conf-set [cfarr]
  "Resolve whole conflict sets array"
  ;;(println [:RESOLVE-CONF-SET])
  (loop [sal (dec (alength cfarr))]
    ;;(println [:SAL sal])
    (if (>= sal 0)
      (if-let [al (seq (aget cfarr sal))]
        (or (resolve-for-sal sal al cfarr)
            (recur (dec sal)))
        (recur (dec sal))))))

(defn fire-resolved [[prod ctx]]
  "Fire resolved production with ctx"
  ;;(println [:FIRE-RESOLVED prod ctx])
  (let [[pnam sal vars func] prod]
    (if TRACE (do (println) (println [:FIRE pnam :CONTEXT ctx])))
    (apply func (var-vals ctx vars))))

(defn fire
  "Fire!"
  ([]
   (while (not (every? empty? (seq CFARR)))
     (fire 1)))
  ([n]
    (dotimes [i n]
      (if-let [reso (resolve-conf-set CFARR)]
        (fire-resolved reso)) ) ))

(defn asser
  "Function for the facts assertion that can be used in the right hand side of the rule.
   It has arbitrary number of arguments that as a whole represent a frame"
  [& args]
  (assert-frame args))

(defn retract [fids & indices]
  "Function for the facts retraction that can be used in the right hand side of the rule.
   Retract facts for indices of patterns in left hand side of rule"
  ;;(println [:RETRACT fids indices])
  (let [fids (reverse fids)]
    (doseq [idx indices]
      (retract-fact (nth fids idx) true))))

(defn modify [fids idx & svals]
  "Function for the fact modification that can be used in the right hand side of the rule.
   Modify fact for given index of pattern in left hand side of rule"
  ;;(println [:MODIFY fids idx svals])
  (let [fids (reverse fids)]
    (modify-fact (nth fids idx) (apply hash-map svals))))

(defn frame-by-id [fid]
  "Extracts frame for fact id from facts memory"
  (let [funarg (@IDFACT fid)]
    (if (not= funarg :deleted)
      (let [[typ mp] (to-typmap funarg)]
        (cons typ (apply concat (seq mp))) ) )))

(defn fact-list
  "List of facts (of some type)"
  ([]
   (filter #(not= (second %) nil)
           (for [i (range @FCNT)](cons i (frame-by-id i)))))
  ([typ]
   (filter #(= (second %) typ) (fact-list))))

(defn facts
  "Prints facts  (of some type)"
  ([]
   (let [fl (fact-list)]
     (doall (map println fl))
     (count fl)))
  ([typ]
   (let [fl (fact-list typ)]
     (doall (map println fl))
     (count fl))))

(defn ppr [typ]
  "Pretty prints facts of type typ or all facts when typ = :all"
  (let [all (fact-list)
        sel (if (= typ :all) all (filter #(= (second %) typ) all))]
    (doseq [fact sel]
      (println)
      (let [[[n typ] & rp] (partition-all 2 fact)]
        (println (str "Fact" n " " typ))
        (doseq [sv rp]
          (println (str "  " (first sv) " " (second sv))) )) )
    (count sel)))

(defn trace []
  "Begins tracing of translation and execution"
  (def TRACE true))

(defn untrace []
  "Ends tracing of translation and execution"
  (def TRACE nil))

(defn trans-asser [x vars]
  (cons 'rete.core/asser (qq (rest x) vars)))

(defn trans-retract [x mp]
  (cons 'rete.core/retract
        (cons '?fids
               (map #(mp %) (rest x)) ) ))

(defn trans-modify [x vars mp]
  (cons 'rete.core/modify
        (cons '?fids
                (cons (mp (first (rest x)))
                      (qq (nnext x) vars)) ) ))
(defn destruct [v]
  "Destructuring support: collect symbols inside vectors"
  (cond
   (vector? v) (mapcat destruct v)
   (= v '&) []
   true [v]))

(declare trans-rhs)

(defn trans-let [x vars mp]
  (let [binds (second x)
        pairs (partition 2 binds)
        vars2 (map first pairs)
        vars3 (mapcat destruct (vec vars2))
        vars4 (concat vars vars3)]
    (cons (first x)
          (cons binds (trans-rhs (nnext x) vars4 mp)))))

(defn trans-lhs [lhs]
  "Translate left hand side of rule by removing statement labels and put them into map.
    Returns map of statement labels with statement indexes"
  (loop [i 0 ss lhs mp {} nlhs []]
    (if (seq ss)
      (let [los (first ss)]
        (if (symbol? los)
          (recur (inc i) (nnext ss) (assoc mp los i) (conj nlhs (first (next ss))))
          (recur (if (not= (first los) 'not) (inc i) i) (next ss) mp (conj nlhs los))))
      [nlhs mp])))

(defn trans-rhs [x vars mp]
  "Translate right hand side of rule by replacing in retract and modify statements
   labels of left hand side statements with their indexes using corresponding map"
  ;;(println [:TRANS-RHS x (type x) vars mp])
  (cond
   (seq? x)
     (condp = (first x)
      'asser (trans-asser x vars)
      'retract (trans-retract x mp)
      'modify (trans-modify x vars mp)
      'let (trans-let x vars mp)
      'if-let (trans-let x vars mp)
      'when-let (trans-let x vars mp)
      'loop (trans-let x vars mp)
      (map #(trans-rhs % vars mp) x))
   (vector? x) (vec (map #(trans-rhs % vars mp) x))
   true x))

(defn trans-rule [rule]
  "Translate rule by translating lhs and rhs of rule"
  (let [nam (prod-name rule)
        sal (salience rule)
        lsd (lhs rule)
        rsd (rhs rule)
        [lsd2 mp] (trans-lhs lsd)
        rsd2 (trans-rhs rsd nil mp)]
    (concat [nam] [sal] lsd2 ['=>] rsd2)))

(def FACTS nil)

(defn step
  "Step through facts in FACTS"
  ([]
   (if (not (every? empty? (seq CFARR)))
     (fire 1)
     (if (seq @FACTS)
       (do (assert-frame (first @FACTS))
         (swap! FACTS rest)
         (fire 1))
       (println "No facts!"))))
  ([n]
   (dotimes [i n]
     (step))))

  (defn run [facts]
    "Run facts (assert all facts and fire)"
    (doseq [f facts]
      (assert-frame f))
    (fire))

(defn run-with
  [mode temps truls facts]
  ;; (println [:RUN-WITH mode temps truls facts])
  (if (condp = mode
        "run" (do (untrace) true)
        "trace" (do (trace) true)
        "step" (do (trace) true)
        (do (println (str "Wrong mode: " mode)) false))
    (do (create-rete temps truls)
      (condp = mode
        "step" (def FACTS (atom facts))
        "trace" (run facts)
        "run" (time (run facts))) )))

(defn run-with-mode
  ([mode trff]
  	;; (println [:RUN-WITH-MODE mode trff])
    (if (= (first (nth trff 3)) 'facts)
      (run-with-mode mode (butlast trff) (rest (last trff)))
      (println (str "Wrong file format file!"))))
  ([mode trufs facts]
    (if (and (= (first (nth trufs 0)) 'templates)
             (= (first (nth trufs 1)) 'rules)
             (= (first (nth trufs 2)) 'functions))
      (let [temps (rest (nth trufs 0))
            rules (rest (nth trufs 1))
            truls (filter some? (map trans-rule rules))
            ons (ns-name *ns*)]
        (eval (cons 'do (rest (nth trufs 2))))
        (in-ns ons)
        (run-with mode temps truls facts))
      (println (str "Wrong file format!")) ) ))

(defn slurp-with-comments [f]
  "Opens a reader on f and reads all its contents, returning a string.
  Skip rest of the line starting from semicolon."
  (let [sb (StringBuilder.)]
    (with-open [^java.io.Reader r (reader f)]
      (loop [c (.read r) comm false]
        (if (neg? c)
          (str sb)
          (let [cc (char c)
                is-comm (or (and (not comm) (= cc \;)) (and comm (not= cc \newline)))]
            (if (not is-comm)
              (.append sb cc))
            (recur (.read r) is-comm)) ))) ))

(defn app
  "rete application function"
  [& args]
  (condp = (count args)
    3 (let [trufs (read-string (slurp-with-comments (nth args 1)))
		    facts (read-string (slurp-with-comments (nth args 2)))]
        (run-with-mode (first args) trufs facts))
    2 (run-with-mode (first args) (read-string (slurp-with-comments (second args))))
    (println "Number of arguments: 2 or 3, see documentation!")))

(defn strategy-depth []
  "Set conflict resolution strategy to depth"
  (def STRATEGY 'DEPTH))

(defn strategy-breadth []
  "Set conflict resolution strategy to breadth"
  (def STRATEGY 'BREADTH))

(defn clear-deleted []
  "Clear map @IDFACT from fact-ids marked as :deleted"
  (doseq [k (keys @IDFACT)]
    (if (= (@IDFACT k) :deleted)
      (swap! IDFACT dissoc k))))

(defn load-facts [path]
  "Load facts from path and assert all of them into working memory"
  (run (read-string (slurp-with-comments path))))
;;  (doseq [fact (read-string (slurp-with-comments path))]
;;    (assert-frame fact)))

(defn save-facts
  "Save all facts or facts of types to a file on a path in a format suitable to load"
  ([path]
   (save-facts :all path (mapcat fact-list (keys TEMPL))))
  ([types path]
   (save-facts :all path (mapcat fact-list types)))
  ([types path facts]
   (let [ffs (map rest facts)]
     (with-open [^java.io.Writer w (writer path)]
       (.write w "(")
       (doseq [f (butlast ffs)]
         (.write w (str f))
         (.newLine w))
       (.write w (str (last ffs)))
       (.write w ")")))))

(defn slot-value [s f]
  "Returns value of slot of fact (as of item of result of the function fact-list)"
  (s (apply hash-map (rest (rest f)))))

(defn facts-with-slot-value
  "Returns list of facts with slot values for which (f slot-value value) = true"
  ([slot f value]
   (facts-with-slot-value :all slot f value (fact-list)))
  ([typ slot f value]
   (facts-with-slot-value typ slot f value (fact-list typ)))
  ([typ slot f value facts]
   (filter #(f (slot-value slot %) value) facts)))


  ;; The End

