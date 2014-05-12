(ns rete.core
  (:use clojure.java.io)
  (:import java.util.HashMap)
  (:gen-class
    :name rete.core
    :methods [#^{:static true} [reteApp [String String] void]
              #^{:static true} [reteAppString [String String] void]
              #^{:static true} [reteAppFacts [String String String] void]
              #^{:static true} [reteAppStringFacts [String String String] void]
              #^{:static true} [allFacts [] java.util.HashMap]
              #^{:static true} [factsOfType [String] clojure.lang.Cons]
              #^{:static true} [assertFact [String java.util.HashMap] void]
              #^{:static true} [fireAll [] void]
              #^{:static true} [fire [int] void]
              #^{:static true} [trace [] void]
              #^{:static true} [untrace [] void]]))

(declare =TEMPL= ACNT ANET)

(def TRACE nil)

(defn throw-mess [mess]
  "Throw message"
  (Throwable. (str mess)))

(defn pred [condition]
  "Get predicate of condition"
  (first condition))

(defn args [condition]
  "Get arguments of condition"
  (rest condition))

(defn vari? [x]
  "Is x variable?"
  (and (symbol? x) (.startsWith (name x) "?")))

(defn func? [sym]
  "Is symbol function?"
  (if (resolve sym) sym))

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

(defn acnt
  "Alpha memory count calculated during creation of the alpha net and used for creation of the alpha memory"
  []
  (let [ac @ACNT]
    (swap! ACNT inc)
    ac))

(defn merge-lst [ls1 ls2]
  "Merge two lists"
  (seq (set (concat ls1 ls2))))

(defn template
  "Select template part of condition"
  [condition]
  (if (even? (count condition))
	(butlast condition)
	condition))

(defn mk-typmap [frame]
  "Create Typmap (list of type and map) from frame (list of type and keys with values)"
  (let [[typ & rst] frame
        mp (apply hash-map rst)]
    (list typ mp)))

(defn add-anet-entry
  "If condition in a left hand side of a rule is a pattern (not test with a function on the predicate place)
   adds a new entry to the map representing the alpha net.
   If test contains call to 'not-exist', for its args also adds entries"
  ([condition]
    ;;(println [:ADD-ANET-ENTRY :COND condition])
    (let [[typ mp] (mk-typmap (template condition))
          msk (=TEMPL= typ)
          ant (or (get @ANET typ) {})]
      (if (seq msk)
        (swap! ANET assoc typ (add-anet-entry msk ant mp)) ) ))
  ([msk ant mp]
    ;;(println [:ADD-ANET-ENTRY msk ant mp])
    (if (seq msk)
      (let [val (mp (first msk))
            k (if (or (nil? val) (vari? val)) '? val)
            ant2 (or (ant k) {})
            msk2 (rest msk)]
        (assoc ant k
          (if (seq msk2)
            (add-anet-entry msk2 ant2 mp)
            (acnt))) ) )))

(defn anet-for-pset
  "Build the alpha net for the given production set (rule set) <pset> as a map"
  [pset]
  (doseq [pp pset]
    (if TRACE (println [:PRODUCTION pp]))
    (doseq [condition (lhs pp)]
		(if TRACE (println [:condition condition]))
	    (add-anet-entry condition)) ))

(defn a-indexof-pattern
  "Find an alpha memory cell index for a pattern from a left hand side of some rule"
  ([pattern]
    ;;(println [:A-INDEXOF-PATTERN :PATTERN pattern])
    (let [[typ mp] pattern
          msk (=TEMPL= typ)
          ant (get @ANET typ)]
      (a-indexof-pattern msk ant mp)))
  ([msk ant mp]
    (if (seq msk)
      (let [val (mp (first msk))
            k (if (or (nil? val) (vari? val)) '? val)
            ant2 (ant k)
            msk2 (rest msk)]
        (cond
          (and (number? ant2) (empty? msk2))
            ant2
          (and (map? ant2) (seq msk2))
            (a-indexof-pattern msk2 ant2 mp))) )))

(defn collect-vars
  "Returns vector of variables in expression"
  ([ex]
   (vec (set (collect-vars ex nil))))
  ([ex yet]
   (cond
    (and (or (seq? ex) (vector? ex) (= (type ex) clojure.lang.PersistentHashSet))
         (not (empty? ex)))
      (collect-vars (first ex) (collect-vars (rest ex) yet))
    (vari? ex) (cons ex yet)
    true yet)))

(defn qq [aa vars]
  "Add call to quote for symbols not variables"
  (let [f1 (fn [x]
             (if (and (symbol? x)
                      (not (or (vari? x) (some #{x} vars))))
               (list 'quote x)
               x))]
    (map f1 aa)))

(defn and-or [x]
  "Translate list-vector form of condition to and-or form"
  (cond
   (list? x)
     (cond
      (= (first x) 'not-exists) (cons 'rete.core/not-exists (qq (rest x) nil))
      (symbol? (first x)) (cons (first x) (qq (rest x) nil))
      true (cons 'and (map and-or x)))
   (vector? x) (cons 'or (map and-or x))
   true x))

(defn mk-test-func [tst vrs]
  (let [aof (and-or tst)
        df (list 'fn vrs aof)]
    (if TRACE (println [:TEST-FUNCTION df]))
    (let [cf (eval df)]
      (if TRACE (println [:COMPILED cf]))
      cf)))

(defn mk-pattern-and-test [condition]
  "Make pattern or test"
  ;;(println [:MK-PATTERN-AND-TEST condition])
  (let [[p & aa] condition]
    (let [[frame test]
            (if (even? (count condition))
              [(butlast condition) (last condition)]
              [condition nil])
          rst (if test
                (let [vrs (collect-vars test)]
                  [vrs (mk-test-func test vrs)])
                [nil nil])
          patt (concat (mk-typmap frame) rst)
          apid (a-indexof-pattern patt)]
        (list apid patt))))

(defn enl [lst]
  "Add numbers to lists"
  (map cons (range (count lst)) lst))

(defn mk-rhs-func [vrs rhs]
  "Create function from vector of variables and right hand side"
  (let [df (cons 'fn (cons vrs rhs))]
    (if TRACE (println [:RHS-FUNCTION df]))
    (let [cf (eval df)]
      (if TRACE (println [:COMPILED cf]))
      cf)))

(defn beta-net-plan
  "Create a plan of the beta net that will be used to its building.
   The plan describes the beta net as a list, mean while the real beta net is an array.
   The plan is the list of lists each of which represents one cell of the beta memory.
   First element of each list is an index of the beta memory, rest of each list is a content of the corresponding cell"
  ([pset]
    (enl (mapcat
           #(beta-net-plan
             (prod-name %)
             (salience %)
             (lhs %)
             (rhs %))
           pset)))
  ([pname sal lhs rhs]
    (if TRACE (println [:PRODUCTION pname]))
    (let [pts (map mk-pattern-and-test lhs)
          fir (concat (first pts) [pname])
          mid (butlast (rest pts))
          vrs (collect-vars rhs)
          las (concat (last pts)
                      (list pname sal vrs (mk-rhs-func vrs rhs)))]
      (if (= (count lhs) 1)
        (list (cons 'ex las))
        (concat (list (cons 'e fir))
                (map #(cons 'i %) mid)
                (list (cons 'x las)) )) )))

(defn fill-ablink
  "Fill alpha-beta links table from beta net plan"
  ([ablink bplan]
    (dotimes [i (count ablink)]
      (fill-ablink ablink bplan i)))
  ([ablink bplan i]
    (let [flt (filter #(= (nth % 2) i) bplan)]
      (aset ablink i (map first flt)) )))

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

(defn log-rete [anet bplan ablink]
  "Log RETE"
  (str
    (log-hm "alpha-net-plan.txt" anet)
    (log-lst "beta-net-plan.txt" bplan)
    (log-array "alpha-beta-links.txt" ablink)))

(declare reset)

(defn create-rete
  "Create RETE from a production set and reset"
  [tset pset]
  (try
    (def GLOMEM (HashMap.))
    (def ANET (atom {}))
    (def ACNT (atom 0))
    (if TRACE (println ".... Creating TEMPLATES for Pset ...."))
    (def =TEMPL=
      (apply hash-map (mapcat #(list (first %) (rest %)) tset)))
    (if TRACE (println ".... Creating ANET PLAN for Pset ...."))
    (anet-for-pset pset)
    (if TRACE (println ".... Creating BNET PLAN for Pset ...."))
    (def =BPLAN= (beta-net-plan pset))
    (def =ABLINK= (object-array @ACNT))
    (def =BCNT= (count =BPLAN=))
    (def =BNET= (object-array =BCNT=))
    (fill-bnet =BNET= =BPLAN=)
    (fill-ablink =ABLINK= =BPLAN=)
    (reset)
    (when TRACE
      (log-rete @ANET =BPLAN= =ABLINK=)
      (println ".... Log Files Created ....")
      (println ".... RETE Created and Reset ...."))
    [@ACNT =BCNT=]
    (catch Throwable twe
      (println twe)
      nil)))

(defn create-fmem [templs]
  (let [fmem (HashMap.)]
    (doseq [[k v] (map #(list (first %) (HashMap.)) (seq templs))]
      (.put fmem k v))
    fmem))

(defn reset []
  "Reset: clear and initialize all memories"
  (def =AMEM= (object-array @ACNT))
  (def =BMEM= (object-array =BCNT=))
  (def =FMEM= (create-fmem =TEMPL=))
  (def =FMMB= (create-fmem =TEMPL=))
  (def =FIDS= (HashMap.))
  (def CFSET (atom nil))
  (def FCNT (atom 0)))

(defn mk-fact
  "Make fact"
  ([typ mp]
    ;;(println [:MK-FACT typ mp])
    (let [msk (=TEMPL= typ)
          fmem (.get =FMEM= typ)
          f-cnt @FCNT]
      (mk-fact typ mp msk fmem nil)
      (let [fcnt2 @FCNT]
        (if (> fcnt2 f-cnt)
          (concat (list typ mp) (list (dec fcnt2))) ) )))
  ([typ mp msk fmem bkhm]
    ;;(println [:MK-FACT typ mp msk fmem bhm])
    (let [val (mp (first msk))
          msk2 (rest msk)
          fmem2 (.get fmem val)]
      (cond
        (empty? msk2)
          (if (nil? fmem2)
            (let [k (or val '?)
                  fid @FCNT
                  bmem (.get =FMMB= typ)]
              (swap! FCNT inc)
              (.put fmem k fid)
              (.put bmem fid (cons [k fmem] bkhm))))
        (nil? val)
          (if-let [fmem3 (.get fmem '?)]
            (mk-fact typ mp msk2 fmem3 (cons ['? fmem] bkhm))
            (let [fmem3 (HashMap.)]
              (.put fmem '? fmem3)
              (mk-fact typ mp msk2 fmem3 (cons ['? fmem] bkhm))))
        (nil? fmem2)
          (let [fmem3 (HashMap.)
                k (or val '?)]
            (.put fmem k fmem3)
            (mk-fact typ mp msk2 fmem3 (cons [k fmem] bkhm)))
        true
          (mk-fact typ mp msk2 fmem2 (cons [val fmem] bkhm))))))

(defn amem
  "Return an alpha memory cell for a given index <i>"
  [i]
  (aget =AMEM= i))

(defn set-amem [i v]
  "Set an alpha memory cell with a value <v> for a given index <i>"
  (aset =AMEM= i v))

(defn bnet
  "Returns a beta net cell (beta node) for a given index <i>"
  [i]
  (aget =BNET= i))

(defn bmem
  "Returns a beta memory cell for a given index <i>"
  [i]
  (aget =BMEM= i))

(defn set-bmem [i v]
  "Set a beta memory cell with a value <v> for a given index <i>"
  (aset =BMEM= i v))

(defn var-vals [mp vals]
  "take values from map mp in order of list of keys"
  (map #(mp %) vals))

(defn match [p f ctx]
  "Match two atoms against context"
  ;;(println [:MATCH p f ctx])
  (if (nil? p)
    ctx
    (if (vari? p)
      (if-let [v (ctx p)]
        (if (= f v)
          ctx)
        (assoc ctx p f))
      (if (= p f)
        ctx)) ))

(defn match-ctx [fact pattern ctx bi]
  "Match fact with pattern with respect to context"
  ;;(println [:MATCH-CTX :FACT fact :PATTERN pattern :CTX ctx :BI bi])
  (let [[ftyp fmp fid] fact
        [ptyp pmp vrs func] pattern]
    (if (= ftyp ptyp)
      (if-let [ctx2 (loop [msk (=TEMPL= ftyp) xtc ctx]
                      (if (and xtc (seq msk))
                        (let [ms1 (first msk)]
                          (recur (rest msk) (match (pmp ms1) (fmp ms1) xtc)))
                        xtc))]
        (if (or (nil? func) (apply func (var-vals ctx2 vrs)))
          (let [ctx3 (assoc ctx2 '?fids (cons fid ('?fids ctx2)))
                fids (.get =FIDS= fid)]
            (if (not (some #{bi} fids))
              (.put =FIDS= fid (cons bi fids)))
            ctx3)) )) ))

(defn match-ctx-list [facts pattern ctx bi]
  "Match list of facts with pattern with respect to context"
  (map #(match-ctx % pattern ctx bi) facts))

(defn fact-id [fact]
  "Get id of fact"
  (nth fact 2))

(defn add-to-confset
  "Make from an activated production (rule) <aprod> and a list of contexts,
   that have activated this production <match-list>,
   a list of activations and concatenate them to the conflict set"
  [aprod match-list]
  ;;(println [:ADD-TO-CONFSET :APROD aprod]) ;; :MATCH-LIST match-list])
  (let [alist (map #(list aprod %) match-list)]
    (swap! CFSET concat alist)))

(defn only-old-facts [ai new-fid]
  "Take alpha memory cell content and excludes new fact if appropriate"
  ;;(println [:ONLY-OLD-FACTS ai new-fid])
  (let [ofacts (amem ai)]
    (cond
      (nil? new-fid) ofacts
      (= new-fid (fact-id (first ofacts))) (rest ofacts)
      true ofacts)))

(defn mk-match-list [ofacts pattern ctx-list bi]
  "Make match-list"
  (filter seq (mapcat #(match-ctx-list ofacts pattern % bi) ctx-list)))

(defn activate-b
  "Activate beta net cell of index <bi> with respect to a list of contexts
   already activated by a new fact with an index <new-fid>"
  [bi ctx-list new-fid]
  ;;(println [:ACTIVATE-B :BI bi :CTX-LIST ctx-list :NEW-FID new-fid])
  (let [bnode (bnet bi)
        [eix bal pattern & tail] bnode]
    (let [ofacts (only-old-facts bal new-fid)
          ml (mk-match-list ofacts pattern ctx-list bi)]
      ;;(println [:ML ml])
      (if (seq ml)
        (condp = eix
          'x (add-to-confset tail ml)
          'i (do
               (set-bmem bi (concat ml (bmem bi)))
               (activate-b (inc bi) ml nil))) ) )))

(defn entry-a-action [bi pattern b-mem a-mem]
  "Entry alpha activation"
  ;;(println [:ENTRY-A-ACTION :BI bi :PATTERN pattern :BMEM b-mem :AMEM a-mem])
  (let [new-fact (first a-mem)
        ctx (match-ctx new-fact pattern {} bi)]
    (set-bmem bi (cons ctx b-mem))
    (activate-b (inc bi) (list ctx) (fact-id new-fact))))

(defn inter-a-action [bi pattern b-mem a-mem]
  "Intermediate alpha activation"
  ;;(println [:INTER-A-ACTION :BI bi :PATTERN pattern :BMEM b-mem :AMEM a-mem])
  (let [ctx-list (bmem (dec bi))]
    (if (seq ctx-list)
      (let [new-fact (first a-mem)
            ml (filter seq (map #(match-ctx new-fact pattern % bi) ctx-list))]
        (when (seq ml)
          ;; remember matching context for both a-nodes and n-nodes
          (set-bmem bi (concat ml b-mem)))
          ;; activate only a-nodes, not n-nodes
          (activate-b (inc bi) ml (fact-id new-fact))) )))

(defn exit-a-action [bi pattern tail b-mem a-mem]
  "Exit alpha activation"
  ;;(println [:EXIT-A-ACTION :BI bi :PATTERN pattern :TAIL tail :AMEM a-mem])
  (let [ctx-list (bmem (dec bi))]
    (if (seq ctx-list)
      (let [ml (filter seq (map #(match-ctx (first a-mem) pattern % bi) ctx-list))]
        (when (seq ml)
          ;; remember matching context for both a-nodes and n-nodes, not for f-nodes
          (set-bmem bi (concat ml b-mem)))
          ;; add to conflicting set only for a-nodes and f-nodes, not for n-nodes
          (add-to-confset tail ml)) ) ))

(defn enex-a-action [bi pattern tail a-mem]
  "Entry and exit alpha activation (for LHS with 1 pattern)"
  ;;(println [:ENEX-A-ACTION :PATTERN pattern :TAIL tail :AMEM a-mem])
  (if-let [ctx (match-ctx (first a-mem) pattern {} bi)]
    (add-to-confset tail (list ctx))))

(defn activate-a
  "Activate alpha net cells for index list <ais>"
  [ais]
  ;;(println [:ACTIVATE-A :AIS ais])
  (doseq [ai ais]
    (let [ablinks (aget =ABLINK= ai)
          bnms (map #(list % (bnet %) (bmem %)) ablinks)
          a-mem (amem ai)]
      (doseq [[bi [eix bal pattern & tail] b-mem] bnms]
        (condp = eix
          'e (entry-a-action bi pattern b-mem a-mem)
          'i (inter-a-action bi pattern b-mem a-mem)
          'x (exit-a-action bi pattern tail b-mem a-mem)
          'ex (enex-a-action bi pattern tail a-mem))) )))

(defn fire-resolved [reso]
  "Fire resolved production"
  ;;(println [:FIRE-RESOLVED reso])
  (let [[[pn sal vars func] ctx] reso]
    (if TRACE (println [:FIRE pn :CONTEXT ctx]))
    (apply func (var-vals ctx vars))))

(defn a-indices
  "For an asserted typmap find all suitable alpha memory cells"
  ([typ mp]
    (let [msk (=TEMPL= typ)
          ant (get @ANET typ)]
      (set (a-indices msk ant mp))))
  ([msk ant mp]
    ;;(println [:A-INDICES msk ant mp])
    (cond
      (number? ant) (list ant)
      (not (or (empty? msk) (empty? ant)))
        (let [val (mp (first msk))
              msk2 (rest msk)
              ant2 (ant val)
              ant3 (ant '?)]
          ;;(println [:ANT2 ant2 :ANT3 ant3])
          (concat (a-indices msk2 ant2 mp)
                  (a-indices msk2 ant3 mp))) )))

(defn fact-exists?
  "Find existing fact id for arbitrary typmap"
  ([typmap]
    (let [[typ mp] typmap
          msk (=TEMPL= typ)
          fmem (.get =FMEM= typ)]
      (fact-exists? msk fmem mp)))
  ([msk fmem mp]
    (cond
      (and (empty? mp) (not (nil? fmem)))
        true
      (and (seq msk) (not (nil? fmem)))
        (let [k (first msk)]
          (if-let [val (mp k)]
            (fact-exists? (rest msk) (.get fmem val) (dissoc mp k))
            (some #(fact-exists? (rest msk) % mp) (.values fmem))) ) )))

(defn remove-ctx-with [fid ctxlist]
  "Remove context for given fact id"
  ;;(println [:REMOVE-CTX-WITH :FID fid :CTXLIST ctxlist])
  (filter #(not (some #{fid} ('?fids %))) ctxlist))

(defn retract-b [fid bis]
  "Retract fact id from the beta memory"
  ;;(println [:RETRACT-B :FID fid :BIS bis])
  (doseq [bi bis]
    (loop [i bi]
      (set-bmem i (remove-ctx-with fid (bmem i)))
      (let [ni (inc i)]
        (if (< ni =BCNT=)
          (let [eix (first (bnet ni))]
            (if (or (= eix 'i) (= eix 'x))
              (recur ni))) )) )))

(defn frame-by-id [fid]
  "Extracts frame for fact id from facts memory"
  (loop [ks (.keySet =FMMB=)]
    (if (seq ks)
      (let [typ (first ks)
            ff (.get =FMMB= typ)]
        (if-let [bkhm (.get ff fid)]
          (let [vv (map first bkhm)]
            (cons typ (interleave (=TEMPL= typ) (reverse vv))))
          (recur (rest ks))) ) )))

(defn typmapfids
  "List of facts [for given type] in form: (typ {slot-value-map} fact-id)"
  ([]
   (mapcat typmapfids (.keySet =FMMB=)))
  ([typ]
   (for [[fid bkhm] (.get =FMMB= typ)]
     (let [vv (map first bkhm)]
       (list typ (zipmap (=TEMPL= typ) (reverse vv)) fid)) ) ))

(defn remove-fmem [fid]
  "Remove fact from facts memory by fact id.
   Returns typmap of removed fact"
  (loop [ks (.keySet =FMMB=)]
    (if (seq ks)
      (let [typ (first ks)
            ff (.get =FMMB= typ)]
        (if-let [bkhm (.get ff fid)]
          (let [[k hm] (first bkhm)]
            (.remove hm k)
            (loop [khm (rest bkhm) vv (list k)]
              (if (seq khm)
                (let [[k hm] (first khm)]
                  (if (.isEmpty (.get hm k))
                    (.remove hm k))
                  (recur (rest khm) (cons k vv)))
                (do (.remove ff fid)
                  (list typ (zipmap (=TEMPL= typ) vv))))))
          (recur (rest ks))) ) )))

(defn retract-fact [fid]
  "Retract fact for given fact-id by removing it from alpha, beta and fact memory,
  and also by removing from conflict set activations, containing this fact-id"
  ;;(println [:RETRACT-FACT fid])
  (if-let [frame (remove-fmem fid)]
    (let [[typ mp] frame
          ais (a-indices typ mp)]
      (if TRACE (println [:<== [typ mp] :id fid]))
      (doseq [ai ais]
        (set-amem ai (doall (remove #(= (fact-id %) fid) (amem ai)) ) ))
      (retract-b fid (.get  =FIDS= fid))
      (reset! CFSET (filter #(not (some #{fid} ('?fids (second %)))) @CFSET))
      (.remove  =FIDS= fid)
      frame)))

(defn ais-for-frame
  "Create fact from frame, add it to alpha memory
   and returns active list of  alpha memory cells"
  ([frame]
    ;;(println [:AIS-FOR-FRAME frame])
    (let [[typ & rst] frame
          mp (apply hash-map rst)]
      (ais-for-frame typ mp)))
  ([typ mp]
    ;;(println [:AIS-FOR-FRAME typ mp])
    (if-let [fact (mk-fact typ mp)]
      (when-let [ais (a-indices typ mp)]
        (if TRACE (println [:==> [typ mp] :id (fact-id fact)]))
        ;; fill alpha nodes
        (doseq [ai ais]
          (set-amem ai (cons fact (amem ai)) ))
        ais))))

(defn assert-frame [frame]
  "Assert frame and activate corresponding alpha nodes"
  (activate-a (ais-for-frame frame)))

(defn modify-fact [fid mmp]
  "Modify fact for given fact-id by retracting it and asserting,
   modified frame"
  ;; (println [:MODIFY-FACT fid mmp])
  (if-let [frame (retract-fact fid)]
    (let [[typ mp] frame
          mp2 (merge mp mmp)]
      (activate-a (ais-for-frame typ mp2)) ) ))

(defn assert-list
  "Function for assertion a list of triples or object descriptions (see comments on the function 'asser').
   For the use outside of the right hand side of rules"
  [lst]
  (activate-a (mapcat ais-for-frame lst)))

(defn fire
  "Fire!"
  ([]
    (while (not (empty? @CFSET))
      (fire 1)))
  ([n]
    (dotimes [i n]
      (if (not (empty? @CFSET))
        (let [[reso & remain] (sort-by #(- (salience (first %))) @CFSET)]
          (reset! CFSET remain)
          (fire-resolved reso)) ) )))

(defn asser
  "Function for the facts assertion that can be used in the right hand side of the rule.
   It has arbitrary number of arguments that as a whole represent a frame"
  [& args]
  (assert-frame args))

(defn not-exists
  "Function for using in left hand side to find out if some fact exists"
  [& args]
  ;;(println [:NOT-EXISTS args])
  (not (fact-exists? (mk-typmap args))))

(defn retract [fids & indices]
  "Function for the facts retraction that can be used in the right hand side of the rule.
   Retract facts for indices of patterns in left hand side of rule"
  ;;(println [:RETRACT fids indices])
  (let [fids (reverse fids)]
    (doseq [idx indices]
      (retract-fact (nth fids idx)))))

(defn modify [fids idx & svals]
  "Function for the fact modification that can be used in the right hand side of the rule.
   Modify fact for given index of pattern in left hand side of rule"
  ;;(println [:MODIFY fids idx svals])
  (let [fids (reverse fids)]
    (modify-fact (nth fids idx) (apply hash-map svals))))

(defn fact-list []
  "List of facts"
  (filter #(not= (second %) nil)
          (for [i (range @FCNT)](cons i (frame-by-id i)))))

(defn facts []
  "Prints facts"
  (let [fl (fact-list)]
    (doall (map println fl))
    (count fl)))

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

(defn run-synch
 [tset pset fset]
  "Create RETE for pset and assert and fire facts from fset synchronously"
  (create-rete tset pset)
  (doseq [f fset]
    (assert-frame f)
    (fire)))

(defn run-asynch
 [tset pset fset]
  "Create RETE for pset and assert facts from fset.
   After that issue 'Fire!'"
  (create-rete tset pset)
  (assert-list fset)
  (fire))

(defmacro rutime [expr]
  "Calculate and print time of calculation expression.
   Returns result of calculation"
  `(let [start# (. System (nanoTime))
         ret# ~expr]
     (println (str "Elapsed time: " (/ (double (- (. System (nanoTime)) start#)) 1000000.0) " msecs"))
     ret#))

(defn trace []
  "Begins tracing of translation and execution"
  (def TRACE true))

(defn untrace []
  "Ends tracing of translation and execution"
  (def TRACE nil))

(defn trans-lhs
  "Translate left hand side of rule by removing statement labels and put them into map.
    Returns map of statement labels with statement indexes"
  [lhs]
  (loop [i 0 ss lhs mp {} nlhs []]
    (if (seq ss)
      (let [los (first ss)]
        (if (symbol? los)
          (recur (inc i) (nnext ss) (assoc mp los i) (conj nlhs (first (next ss))))
          (recur (inc i) (next ss) mp (conj nlhs los))))
      [nlhs mp])))

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

(defn trans-rule
  "Translate rule by translating lhs and rhs of rule"
  [rule]
  (let [nam (prod-name rule)
        sal (salience rule)
        lsd (lhs rule)
        rsd (rhs rule)
        [lsd2 mp] (trans-lhs lsd)
        rsd2 (trans-rhs rsd nil mp)]
    (concat [nam] [sal] lsd2 ['=>] rsd2)))

(declare RULES FACTS)

(defn run-with
  [modes temps rules facts]
  ;; (println [:RUN-WITH modes temps rules facts])
  (if (= modes "step:asynch")
    (let [trules (map trans-rule rules)]
      (trace)
      (create-rete temps trules)
      (assert-list facts)
      (fire 1))
    (let [[m1 m2] (seq (.split modes ":"))
          trules (map trans-rule rules)]
      (if(condp = m1
           "trace" (do (trace) true)
           "run" (do (untrace) true)
           (do (println (str "Wrong mode: " m1)) nil))
        (condp = m2
          "synch" (rutime (run-synch temps trules facts))
          "asynch" (rutime (run-asynch temps trules facts))
          (println (str "Wrong mode: " m2))) ) )))

(defn run-with-modes
  ([modes trff]
  	;; (println [:RUN-WITH-MODES modes trff])
    (if (= (first (nth trff 3)) 'facts)
      (run-with-modes modes (butlast trff) (rest (last trff)))
      (println (str "Wrong file format file!"))))
  ([modes trufs facts]
  	;; (println [:RUN-WITH-MODES modes trufs facts])
    (if (and (= (first (nth trufs 0)) 'templates)
    		 (= (first (nth trufs 1)) 'rules)
             (= (first (nth trufs 2)) 'functions))
      (let [temps (rest (nth trufs 0))
        	rules (rest (nth trufs 1))
        	ons (ns-name *ns*)]
        (eval (cons 'do (rest (nth trufs 2))))
        (in-ns ons)
        (run-with modes temps rules facts))
      (println (str "Wrong file format!")) ) ))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (condp = (count args)
    3 (let [trufs (read-string (slurp (nth args 1)))
		    facts (read-string (slurp (nth args 2)))]
        (run-with-modes (first args) trufs facts))
    2 (run-with-modes (first args) (read-string (slurp (second args))))
    (println "Number of arguments: 2 or 3, see documentation!")))

(defn frames-of-type [typ]
  "Extracts frames for type of fact from facts memory"
  (let [ff (.get =FMMB= typ)
        ks (.keySet ff)]
    (for [k ks]
      (let [bkhm (.get ff k)
            vv (map first bkhm)]
        (cons typ (interleave (=TEMPL= typ) (reverse vv)))))))

(defn symbol-if [s]
  "Convert string into symbol if it begins with quote symbol"
  (if (.startsWith s "'")
    (symbol (.substring s 1))
    s))

;;------------------------ Java Interface ------------------------------;;

(defn -reteApp [modes trff-path-url]
  "Callable from Java function - run rete4frames with modes on templates, rules, functions and facts from file on trff-path or -url"
  (let [trff (slurp trff-path-url)]
    (run-with-modes modes (read-string trff))))

(defn -reteAppString [modes trff]
  "Callable from Java function - run rete4frames with modes on templates, rules, functions and facts from string trff"
  (run-with-modes modes (read-string trff)))

(defn -reteAppFacts [modes trf-path-url f-path-url]
  "Callable from Java function - run rete4frames with modes on templates, rules and functions from file on trf-path or -url, facts from f-path or -url"
  (let [trf (slurp trf-path-url)
        f (slurp f-path-url)]
    (run-with-modes modes (read-string trf) (read-string f))))

(defn -reteAppStringFacts [modes trf f-path-url]
  "Callable from Java function - run rete4frames with modes on templates, rules and functions from string trf, facts from from f-path or -url"
  (let [f (slurp f-path-url)]
    (run-with-modes modes (read-string trf) (read-string f))))

(defn -assertFact [typ slot-value-hm]
  "Callable from Java function - assert fact in form of type and HashMaps of slot values"
  (let [mp (into {} slot-value-hm)
        mp2 (reduce-kv #(assoc %1 (symbol %2) (symbol-if %3)) {} mp)]
    (activate-a (ais-for-frame (symbol typ) mp2))))

(defn -fireAll []
  "Callable from Java function - fire rules while exist activations"
  (fire))

(defn -fire [n]
  "Callable from Java function - fire rules n times"
  (fire n))

(defn -trace []
  "Callable from Java function - swith on tracing"
  (trace))

(defn -untrace []
  "Callable from Java function - swith on tracing"
  (untrace))

(defn -factsOfType [typ]
  "Callable from Java function - collection of HashMaps representing facts of specific type"
  (seq (for [fot (frames-of-type (symbol typ))]
    (let [hm (HashMap.)]
      (doseq [[k v] (partition 2 (rest fot))]
        (.put hm (name k) v))
      hm))))

(defn -allFacts []
  "Callable from Java function - HashMap with keys of existing facts and vlues of collection of HashMaps representing facts of those type"
  (let [ks (.keySet =FMMB=)
        ksn (map name ks)
        hm (HashMap.)]
    (doseq [kn ksn]
      (if-let [fot (seq (-factsOfType kn))]
        (.put hm kn fot)))
    hm))




