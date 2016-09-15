(ns ru.rules
(:require
  [protege.core :as p]
  [rete.core :as rete])
(:import
  edu.stanford.smi.protege.ui.DisplayUtilities
  javax.swing.JOptionPane))

(def LOGS (atom {}))
(defn mk-templates [clss]
  (letfn [(mk-tpl [cls]
	(cons (symbol (.getName cls))
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

(defn single-value [v]
  (if (string? v)
  (if (> (count (clojure.string/split v #"\s")) 1)
    v
    (symbol v))
  v))

(defn mk-frame [ins]
  (letfn [(sval [slt ins]
	(if (.getAllowsMultipleValues slt)
	  (map single-value (.getOwnSlotValues ins slt))
                          (if-let [v (.getOwnSlotValue ins slt)]
                            (single-value v)
                            '?)))]
  (let [typ (.getDirectType ins)
        slots (.getTemplateSlots typ)
        svs (mapcat #(list (symbol (.getName %)) (sval % ins)) slots)]
    (cons (symbol (.getName typ)) svs))))

(defn facts-from-classes [fcs]
  (mapcat #(.getInstances %) fcs))

(defn run-engine
  ([title]
  (when-let [ins (p/fifos "Run" "title" title)]
    (run-engine title
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
  (println [:RUN tit])
    (let [ffc (facts-from-classes fcs)
           fts (concat ffc ffs)
           fts (map mk-frame fts)
           tps (mapcat #(p/svs % "templates") rss)
           tps (mk-templates tps)
           rls (mapcat #(p/svs % "rules") rss)
           rls (map #(mk-rule % trc) rls)
           rls (map rete/trans-rule rls)
           mod (if (p/is? trc) "trace" "run")]
       (println (str "Mode: " mod))
       (println (str "Templates: " (count tps)))
       (println (str "Rules: " (count rls)))
       (println (str "Facts: " (count fts)))
       (rete/run-with mod tps rls fts))))

(defn assert-instances [inss]
  (doseq [ins inss]
  (rete/assert-frame (mk-frame ins))))

(defn ass-inss [hm inst]
  (let [mp (into {} hm)
      clw (mp "clsWidget")
      sel (.getSelection (.getSlotWidget clw (p/slt "instances")))]
  (if (seq sel)
    (assert-instances sel))))

(defn pp [typ]
  ;; pretty print facts to REPL
;; typ - type of facts (symbol with ' prefix) or :all
(let [all (rete/fact-list)
      sel (if (= typ :all) all (filter #(= (second %) typ) all))]
  (doseq [fact sel]
    (p/ctpl "")
    (let [[[n typ] & rp] (partition-all 2 fact)]
      (p/ctpl (str "Fact" n " " typ))
      (doseq [sv rp]
        (p/ctpl (str "  " (first sv) " " (second sv))) ) ))))

(defn sp [typ]
  ;; short print facts to REPL
;; typ - type of facts (symbol with ' prefix) or :all
(let [all (rete/fact-list)
      sel (if (= typ :all) all (filter #(= (second %) typ) all))]
  (def k 0)
  (doseq [fact sel]
    (let [[n typ & rp] fact
          mp (apply hash-map rp)]
      (p/ctpl (str k " " typ " " (or (mp 'title) (mp 'label)) " status: " (mp 'status) " fact: " n))
      (def k (inc k)) ) )))

(defn lp [typ]
  ;; 1 line full print facts to REPL
;; typ - type of facts (symbol with ' prefix) or :all
(let [all (rete/fact-list)
      sel (if (= typ :all) all (filter #(= (second %) typ) all))]
  (doseq [fact sel]
    (p/ctpl "")
    (p/ctpl fact))))

(defn cv [val]
  ;; print fact numbers containing val to REPL
(let [all (rete/fact-list)
      sel (filter #(some #{val} %) all)]
  (def k 0)
  (doseq [fact sel]
    (let [[n typ & rp] fact
          mp (apply hash-map rp)]
      (p/ctpl (str k " " typ " " (or (mp 'title) (mp 'label)) " status: " (mp 'status) " fact: " n))
      (def k (inc k)) ) )))

(defn f [n]
  ;; print fact by number to REPL
(let [all (rete/fact-list)
      fact (first (filter #(= (first %) n) all))]
  (if fact
    (let [[[n typ] & rp] (partition-all 2 fact)]
      (p/ctpl (str "Fact" n " " typ))
      (doseq [sv rp]
        (p/ctpl (str "  " (first sv) " " (second sv))) ) ))))

(defn dr []
  ;; write rules to file Rules.clj
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
  ;; fact types statistics
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

(defn bnp []
  ;; create file with beta net plan
(rete/log-lst "beta-net-plan.txt" rete/BPLAN))

(defn clear-display []
  (if-let [dss (seq (p/cls-instances "Display"))]
   (p/ssv (first dss) "source" "<html>")))

(defn display [mess]
  (let [dis (if-let [dss (seq (p/cls-instances "Display"))]
                (first dss)
                (let [d (p/crin "Display")]
                  (clear-display)
                  d))
       src (p/sv dis "source")]
  (p/ssv dis "source" (str src mess "<br>"))
  (.show p/*prj* dis)))

(defn select [question answers]
  (DisplayUtilities/pickSymbol nil question (first answers) answers))

(defn confirm [question]
  (let [ans (JOptionPane/showConfirmDialog nil question)]
  (condp = ans
    JOptionPane/YES_OPTION true
    JOptionPane/NO_OPTION false
    nil)))

(defn input [question default]
  (DisplayUtilities/editString nil question default nil))

