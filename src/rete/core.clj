(ns rete.core
  (:use clojure.java.io)
  (:import java.util.HashMap)
  (:gen-class))

(declare GLOMEM)
  
(defn get-glo [k]
  "Get from global memory (HashMap cell)"
  (.get GLOMEM k))

(defn put-glo [k v]
  "Put to global memory (HashMap cell)"
  (.put GLOMEM k v))

(defn concat-glo [k v]
  "Concat to global memory (HashMap cell)"
  (.put GLOMEM k (concat v (.get GLOMEM k))))

(defn filter-glo [k fn]
  "Filter global memory (HashMap cell)"
  (.put GLOMEM k (filter fn (.get GLOMEM k))))

(defn inc-glo [k]
  "Add 1 to global memory (HashMap cell)"
  (.put GLOMEM k (inc (.get GLOMEM k))))

(defn assoc-glo [m k v]
  "Assoc with global memory (HashMap cell)"
  (.put GLOMEM m (assoc (.get GLOMEM m) k v)))

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
  (let [ac (get-glo :ACNT)]
    (inc-glo :ACNT)
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

(declare =TEMPL=)
    
(defn add-anet-entry
  "If condition in a left hand side of a rule is a pattern (not test with a function on the predicate place)
   adds a new entry to the map representing the alpha net.
   If test contains call to 'not-exist', for its args also adds entries"
  ([condition]
    ;;(println [:ADD-ANET-ENTRY :COND condition])
    (let [[typ mp] (mk-typmap (template condition))
          msk (=TEMPL= typ)
          ant (or (get (get-glo :ANET) typ) {})]
      (if (seq msk)
        (assoc-glo :ANET typ (add-anet-entry msk ant mp)) ) ))
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
          ant (get (get-glo :ANET) typ)]
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
            
(defn mk-pattern-and-test [condition]
  "Make pattern or test"
  ;;(println [:MK-PATTERN-AND-TEST condition])
  (let [[p & aa] condition] 
    (let [[frame test]
            (if (even? (count condition))
              [(butlast condition) (last condition)]
              [condition nil])
            patt (concat (mk-typmap frame) [test])
            apid (a-indexof-pattern patt)]
        (list apid patt))))

(defn trans-expr [ex]
  "Translate expression"
  ;;(println [:TRANS-EXPR ex ])
  (letfn [(tr-list [car cdr]
                   (cons (or (func? car) car)
                         (map trans-expr cdr)))]
    (cond
      (number? ex) ex
      (symbol? ex) ex
      (string? ex) ex
      (keyword? ex) ex
      (seq? ex) (if (seq? (first ex))
                   (map trans-expr ex)
                   (tr-list (first ex) (rest ex)))
      (vector? ex) (apply vector (map trans-expr ex)))))

(defn enl [lst]
  (map cons (range (count lst)) lst))

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
    (let [pts (map mk-pattern-and-test lhs)
          fir (concat (first pts) [pname])
          mid (butlast (rest pts))
          las (concat (last pts) (list pname sal (trans-expr rhs)))]
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
    (put-glo :ANET {})
    (put-glo :ACNT 0)
    (if TRACE (println ".... Creating TEMPLATES for Pset ...."))
    (def =TEMPL= 
      (apply hash-map (mapcat #(list (first %) (rest %)) tset)))
    (if TRACE (println ".... Creating ANET PLAN for Pset ...."))
    (anet-for-pset pset)
    (if TRACE (println ".... Creating BNET PLAN for Pset ...."))
    (def =BPLAN= (beta-net-plan pset))
    (def =ABLINK= (object-array (get-glo :ACNT)))
    (def =BCNT= (count =BPLAN=))
    (def =BNET= (object-array =BCNT=))
    (fill-bnet =BNET= =BPLAN=)
    (fill-ablink =ABLINK= =BPLAN=)
    (reset)
    (when TRACE
      (log-rete (get-glo :ANET) =BPLAN= =ABLINK=)
      (println ".... Log Files Created ....")
      (println ".... RETE Created and Reset ...."))
    [(get-glo :ACNT) =BCNT=]
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
  (def =AMEM= (object-array (get-glo :ACNT)))
  (def =BMEM= (object-array =BCNT=))
  (def =FMEM= (create-fmem =TEMPL=))
  (def =FMMB= (create-fmem =TEMPL=))
  (def =FIDS= (HashMap.))
  (put-glo :CFSET nil)
  (put-glo :FCNT 0))

(defn mk-fact
  "Make fact"
  ([typ mp]
    ;;(println [:MK-FACT typ mp])
    (let [msk (=TEMPL= typ)
          fmem (.get =FMEM= typ)
          f-cnt (get-glo :FCNT)]
      (mk-fact typ mp msk fmem nil)
      (let [fcnt2 (get-glo :FCNT)] 
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
                  fid (get-glo :FCNT)
                  bmem (.get =FMMB= typ)]
              (inc-glo :FCNT)
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

(declare eval-exp)

(defn andf [test ctx]
  (if (empty? test)
    true
    (if (eval-exp (first test) ctx)
      (andf (rest test) ctx)
      false)))
      
(defn orf [test ctx]
  (if (empty? test)
    false
    (if (eval-exp (first test) ctx)
      true
      (orf (rest test) ctx))))
    
(defn eval-exp [exp ctx]
  "Evaluate expression with respect to ctx = variable-value map"
  ;;(println [:EVAL-EXP exp ctx (seq? exp) (vector? exp)])
  (cond
    (vector? exp)
      (orf exp ctx)
    (seq? exp)
      (let [func (first exp)]
        (if (symbol? func)
          (apply (resolve func) (map #(eval-exp % ctx) (rest exp)))
          (andf exp ctx)))
    (number? exp)
      exp
    true 
      (or (ctx exp) exp)))
    
(defn apply-test 
  "Apply <test> to context <ctx> that is calculate arguments of the test
   with respect to variable values in the context and apply a function
   on predicate place of the test to these arguments"
  [test ctx]
  ;;(println [:APPLY-TEST test :CTX (count ctx)])
  (try
    (eval-exp test ctx)
    (catch Exception ex
      (println [:EXCEPTION-EVAL :TEST test :ON ctx])
      (println ex)
      nil)))
      
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
  ;; (println [:MATCH-CTX :FACT fact]) ;; :PATTERN pattern :CTX ctx :BI bi])
  (let [[ftyp fmp fid] fact
        [ptyp pmp test] pattern]
    (if (= ftyp ptyp)
      (if-let [ctx2 (loop [msk (=TEMPL= ftyp) xtc ctx]
                      (if (and xtc (seq msk))
                        (let [ms1 (first msk)]
                          (recur (rest msk) (match (pmp ms1) (fmp ms1) xtc)))
                        xtc))]
        (if (or (nil? test) (apply-test test ctx2))
          (let [ctx3 (assoc ctx2 :FIDS (cons fid (:FIDS ctx2)))
                fids (.get =FIDS= fid)]
            (if (not (some #{bi} fids))
              (.put =FIDS= fid (cons bi fids)))
            ctx3)) )) ))

(defn match-ctx-list [facts pattern ctx bi]
  "Match list of facts with pattern with respect to context"
  (map #(match-ctx % pattern ctx bi) facts))

(defn fact-id [fact]
  "Get id of fact"
  ;;(println [:FID fact])
  (nth fact 2))

(defn add-to-confset
  "Make from an activated production (rule) <aprod> and a list of contexts,
   that have activated this production <match-list>,
   a list of activations and concatenate them to the conflict set"
  [aprod match-list]
  ;;(println [:ADD-TO-CONFSET :APROD aprod]) ;; :MATCH-LIST match-list])
  (let [alist (map #(list aprod %) match-list)]
    (concat-glo :CFSET alist)))

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
  ;;(println [:ENTRY-A-ACTION :BI bi :AFPAT afpat]) ;; :BMEM b-mem :AMEM a-mem])
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
  ;;(println [:EXIT-A-ACTION :BI bi :AFPAT afpat]) ;; :TAIL tail :AMEM a-mem])
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
  ;;(println [:ENEX-A-ACTION :AFPAT afpat]) ;; :TAIL tail :AMEM a-mem])
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

(defn eval-then-mp [mp expr]
  "Evaluation for right hand side"
  ;;(println [:EVAL-THEN-MP mp expr])
  (if (seq? expr)
    (apply (resolve (first expr)) (map #(eval-then-mp mp %) (rest expr)))
    (or (mp expr) expr)))

(defn fire-resolved [reso]
  "Fire resolved production"
  ;;(println [:FIRE-RESOLVED reso])
  (let [[[pn sal rhs] ctx] reso]
    (if TRACE (println [:FIRE pn :CONTEXT ctx]))
    (doseq [exp rhs]
      (eval-then-mp ctx exp))))

(defn a-indices
  "For an asserted typmap find all suitable alpha memory cells"
  ([typ mp]
    (let [msk (=TEMPL= typ)
          ant (get (get-glo :ANET) typ)]
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
  (filter #(not (some #{fid} (:FIDS %))) ctxlist))

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
      (filter-glo :CFSET #(not (some #{fid} (:FIDS (second %)))))
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
    (while (not (empty? (get-glo :CFSET)))
      (fire 1)))
  ([n]
    (dotimes [i n]
      (if (not (empty? (get-glo :CFSET)))
        (let [[reso & remain] (sort-by #(- (salience (first %))) (get-glo :CFSET))]
          (put-glo :CFSET remain)
          (fire-resolved reso)) ) )))

(defn asser
  "Function for the facts assertion that can be used in the right hand side of the rule.
   It has arbitrary number of arguments that as a whole represent a frame"
  [& args]
  ;;(println [:ASSER args])
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
          (for [i (range (get-glo :FCNT))](cons i (frame-by-id i)))))
          
(defn facts []
  (let [fl (fact-list)]
    (doall (map println fl))
    (count fl))) 

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
      
(defn trans-rhs
  "Translate right hand side of rule by replacing in retract and modify statements
   labels of left hand side statements with their indexes using corresponding map"
   [rhs mp]
   (loop [ss rhs nrhs []]
     (if (seq ss)
       (cond
         (= (ffirst ss) 'asser)
           (recur (next ss) (conj nrhs
             (cons 'rete.core/asser (rest (first ss)))))
         (= (ffirst ss) 'retract)
           (recur (next ss) (conj nrhs 
             (cons 'rete.core/retract (cons ':FIDS 
               (map #(mp %) (rest (first ss))) ) )))
         (= (ffirst ss) 'modify)
           (recur (next ss) (conj nrhs 
             (cons 'rete.core/modify (cons ':FIDS
                (cons (mp (first (rest (first ss))))
                  (nnext (first ss))) ) )))
         true
           (recur (next ss) (conj nrhs 
             (first ss))))
       nrhs)))
       
(defn trans-rule
  "Translate rule by translating lhs and rhs of rule"
  [rule]
  (let [nam (prod-name rule)
        sal (salience rule)
        lsd (lhs rule)
        rsd (rhs rule)
        [lsd2 mp] (trans-lhs lsd)
        rsd2 (trans-rhs rsd mp)]
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

  
