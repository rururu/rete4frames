(ns ru.rules
(:require
  [protege.core :as p]
  [rete.core :as rete]))

(def LOGS (atom {}))
(defn mk-templates [clss]
  (letfn [(mk-tpl [cls]
	(concat [(symbol (.getName cls)) 'INSTANCE]
	  (map #(symbol (.getName %)) (.getTemplateSlots cls))))]
  (if (seq? clss)
    (map mk-tpl clss)
    (mk-templates (.getSubclasses clss)) )))

(defn mk-rule [rule trace]
  (when-let [ri (if (string? rule)
	(p/fifos "Rule" "title" rule)
	rule)]
  (let [nm (p/sv ri "title")
         _ (if (p/is? trace) (println [:MK-RULE nm]))
        sal (p/sv ri "salience")
        lhs (read-string (str "(" (p/sv ri "lhs") ")"))
        rhs (read-string (str "(" (p/sv ri "rhs") ")"))]
    (concat [nm sal] lhs ['=>] rhs))))

(defn mk-frame [ins]
  (letfn [(sval [slt ins]
	(if (.getAllowsMultipleValues slt)
	  (.getOwnSlotValues ins slt)
                        (or (.getOwnSlotValue ins slt) '?)))]
  (let [typ (.getDirectType ins)
        slots (.getTemplateSlots typ)
        svs (mapcat #(list (symbol (.getName %)) (sval % ins)) slots)
        svs (cons 'INSTANCE (cons (.getName ins) svs))]
    (cons (symbol (.getName typ)) svs))))

(defn facts-from-classes [fcs]
  (mapcat #(.getInstances %) fcs))

(defn run-engine
  ([insortit]
  (when-let [ins (if (string? insortit)
	     (p/fifos "Run" "title" insortit)
	     insortit)]
    (run-engine (if (string? insortit)
	    insortit
	    (p/sv insortit "title"))
	(p/svs ins "rule-sets")
	(p/svs ins "fact-classes")
	(p/svs ins "facts")
	(p/sv   ins "trace"))))
([hm inst]
  (let [mp (into {} hm)
         tit (mp "title")
         rss (mp "rule-sets")
         fcs (mp "fact-classes")
         ffs (mp "facts")
         trc (mp "trace")]
    (run-engine tit rss fcs ffs trc)))
([tit rss fcs ffs trc]
  (println [:RUN-ENGINE tit])
    (let [ffc (facts-from-classes fcs)
           facts (concat ffc ffs)
           tms (mapcat #(p/svs % "templates") rss)
           rls (mapcat #(p/svs % "rules") rss)
           mts (mk-templates tms)
           mrs (map #(mk-rule % trc) rls)
           mtr (map rete/trans-rule mrs)]
       (println (str "Trace: " trc))
       (println (str "Templates: " (count mts)))
       (println (str "Rules: " (count mtr)))
       (println (str "Facts: " (count facts)))
       (run-engine trc mts mtr facts)))
([trac templs rules facts]
  (time
    (do 
      (if (p/is? trac)
        (rete/trace)
        (rete/untrace))
      (rete/create-rete templs rules)
      (doseq [f facts]
          (rete/assert-frame (mk-frame f))
          (rete/fire)) ) )))

(defn assert-instances [inss]
  (doseq [ins inss]
  (rete/assert-frame (mk-frame ins))))

(defn retract-instances [inss]
  (doseq [ins inss]
  (doseq [fact (rete/facts-with-slot-value 'INSTANCE = (.getName ins))]
    (rete/retract-fact (first fact) true))))

(defn ass-inss [hm inst]
  (let [mp (into {} hm)
      clw (mp "clsWidget")
      sel (.getSelection (.getSlotWidget clw (p/slt "instances")))]
  (if (seq sel)
    (assert-instances sel))))

(defn retr-inss [hm inst]
  (let [mp (into {} hm)
      clw (mp "clsWidget")
      sel (.getSelection (.getSlotWidget clw (p/slt "instances")))]
  (if (seq sel)
    (retract-instances sel))))

(defn pp [typ]
  (let [all (rete/fact-list)
      sel (if (= typ :all) all (filter #(= (second %) typ) all))]
  (doseq [fact sel]
    (p/ctpl "")
    (let [[[n typ] & rp] (partition-all 2 fact)]
      (p/ctpl (str "Fact" n " " typ))
      (doseq [sv rp]
        (p/ctpl (str "  " (first sv) " " (second sv))) ) ))))

(defn sp [typ]
  (let [all (rete/fact-list)
      sel (if (= typ :all) all (filter #(= (second %) typ) all))]
  (def k 0)
  (doseq [fact sel]
    (let [[n typ & rp] fact
          mp (apply hash-map rp)]
      (p/ctpl (str k " " typ " " (or (mp 'title) (mp 'label)) " status: " (mp 'status) " fact: " n))
      (def k (inc k)) ) )))

(defn lp [typ]
  (let [all (rete/fact-list)
      sel (if (= typ :all) all (filter #(= (second %) typ) all))]
  (doseq [fact sel]
    (p/ctpl "")
    (p/ctpl fact))))

(defn cv [val]
  (let [all (rete/fact-list)
      sel (filter #(some #{val} %) all)]
  (def k 0)
  (doseq [fact sel]
    (let [[n typ & rp] fact
          mp (apply hash-map rp)]
      (p/ctpl (str k " " typ " " (or (mp 'title) (mp 'label)) " status: " (mp 'status) " fact: " n))
      (def k (inc k)) ) )))

(defn f [n]
  (let [all (rete/fact-list)
      fact (first (filter #(= (first %) n) all))]
  (if fact
    (let [[[n typ] & rp] (partition-all 2 fact)]
      (p/ctpl (str "Fact" n " " typ))
      (doseq [sv rp]
        (p/ctpl (str "  " (first sv) " " (second sv))) ) ))))

(defn dr []
  (if-let [rr (seq (p/cls-instances "Rule"))]
  (let [fn "Rules.clj"]
    (with-open [wrtr (clojure.java.io/writer fn)]
      (doseq [r rr]
        (.write wrtr (str "(" (p/sv r "title") " " (p/sv r "salience") "\n"))
        (.write wrtr (str (p/sv r "lhs") "\n"))
        (.write wrtr "=>\n")
        (.write wrtr (str (p/sv r "rhs") ")\n\n")) ))
    (str "Written " (count rr) " rules into " fn))))

(defn sts []
  (letfn [(ads [stm [fid typ mp]]
	(if-let [ste (typ stm)]
	  (assoc stm typ (inc ste))
	  (assoc stm typ 1)))]
  (let [fl (rete/fact-list)
         stm (reduce ads {} fl)
         sto (sort-by second (seq stm))
         str (reverse sto)]
    (p/ctpls str)
    (count fl))))

(defn typmap-by-id [fid]
  (let [funarg (@rete/IDFACT fid)]
  (if (not= funarg :deleted)
    (let [[typ mp] (rete/to-typmap funarg)]
      [typ mp fid]))))

(defn typmapfids
  ([]
 (filter #(not= (second %) nil)
          (for [i (range @rete/FCNT)](typmap-by-id i))))
([typ]
 (filter #(= (first %) typ) (typmapfids))))

(defn fire-all-rules [hm inst]
  (rete/fire))

(defn do-reset [hm inst]
  (rete/reset))

(defn bnp []
  (rete/log-lst "beta-net-plan.txt" rete/BPLAN))

