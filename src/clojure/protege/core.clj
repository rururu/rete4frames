(ns protege.core
(:use clojure.stacktrace)
(:import
 edu.stanford.smi.protege.ui.ProjectManager
 edu.stanford.smi.protege.model.ValueType
 clojuretab.ClojureTab))

(def ^:dynamic *prj* (.getCurrentProject (ProjectManager/getProjectManager)))
(def ^:dynamic *kb* (.getKnowledgeBase *prj*))
(defn ins [name]
  (.getInstance *kb* name))

(defn cls-instances [cls-name]
  ; Returns instances of cls
(.getInstances (.getCls *kb* cls-name)))

(defn ctp [s]
  ; Print s into REPL and return s
(clojuretab.ClojureTab/replappend (print-str s))
s)

(defn ctpl [s]
  ; Print s as line into REPL and return s
(clojuretab.ClojureTab/replappend (str s "\n"))
s)

(defn ctpls [s]
  (doall (map ctpl s))
s)

(defn cls [name]
  (.getCls *kb* name))

(defn slt [name]
  (.getSlot *kb* name))

(defn sv [instance slot-name]
  ; Return singl slot value of instance
(.getOwnSlotValue instance (.getSlot *kb* slot-name)))

(defn svs [instance slot-name]
  ; Return multiple slot values of instance
(.getOwnSlotValues instance (.getSlot *kb* slot-name)))

(defn ssv [instance slot-name value]
  ; Set singl slot value of instance
(.setOwnSlotValue instance (.getSlot *kb* slot-name) value))

(defn ssvs [instance slot-name values]
  ; Set multiple slot values of instance
(.setOwnSlotValues instance (.getSlot *kb* slot-name) values))

(defn crec [name & parents]
  ; Create and return class with parent classes
(let [prs (if parents
                (map #(.getCls *kb* %) parents)
                (list (.getCls *kb* ":THING")))]
 (.createCls *kb* name prs)))

(defn cres [name & options]
  ; Create and return slot.
; Key parameters: :type, :cardinality, :classes, :default
(let [opts (apply hash-map options)
       typ (condp = (opts :type)
                :instance (ValueType/INSTANCE)
                :float (ValueType/FLOAT)
                :integer (ValueType/INTEGER)
                :boolean (ValueType/BOOLEAN)
                :class (ValueType/CLS)
                :any (ValueType/ANY)
                :symbol (ValueType/SYMBOL)
                :string (ValueType/STRING)
                (ValueType/STRING))
       mlt (condp = (opts :cardinality)
                :multiple true
                false)
       cls (if (opts :classes)
                (map #(.getCls *kb* %) (opts :classes))
                (list (.getCls *kb* ":THING")))
       dfv (if (opts :default)
                (condp = typ
                  :integer (map #(Integer. %) (opts :default))
                  :float (map #(Float. %) (opts :default))
                  (opts :default)))
       slot (.createSlot *kb* name)]
  (.setValueType slot typ)
  (.setAllowsMultipleValues slot mlt)
  (if dfv
      (.setDefaultValues slot dfv))
  (if (= typ (ValueType/INSTANCE))
      (.setAllowedClses slot cls))
  slot))

(defn crin [cls]
  ; Return new instance of class cls
(.createInstance *kb* nil (.getCls *kb* cls)))

(defn delin [instance]
  ; Delete instance
(.deleteInstance *kb* instance))

(defn fifos [cls slot value]
  ;; Find insance of class cls with slot of value, or create it
(ClojureTab/findForSlotValue cls slot value))

(defn foc [cls slot value]
  ;; Find insance of class cls with slot of value, or create it
(let [ins (fifos cls slot value)]
  (or ins (let [ins (crin cls)] (ssv ins slot value) ins))))

(defn see [x]
  (if (or (seq? x) (vector? x) (map? x))
    (do (ctpls x) (count x))
    (do (ctpl x) 1)))

(defn selection [mp slot]
  ;; working inside context containing ClsWidget for corresponding instance
(.getSelection (.getSlotWidget (mp "clsWidget") (slt slot))))

(defmacro picat [code]
  `(try
   ~code
   (catch Throwable th#
      (print-cause-trace th#)
      (println))))

(defmacro dbg [x]
  `(let [x# ~x]
    (println "dbg:" '~x "=" x#)
    x#))

(defn is? [boolslot]
  (not (or (nil? boolslot) 
           (= boolslot Boolean/FALSE)
           (= boolslot '?))))

(defn fainst [inss text]
  ;; Find annotated instance
(let [sfs (.getSystemFrames *kb*)
       acl (.getAnnotationCls sfs)
       tsl (.getAnnotationTextSlot sfs)
       isl (.getAnnotatedInstanceSlot sfs)
       ais (.getInstances acl)]
  (loop [ail ais]
    (if (seq ail)
        (let [ai (first ail)
               txt (.getOwnSlotValue ai tsl)
               ins (.getOwnSlotValue ai isl)]
          (if (and (or (nil? inss) (some #{ins} inss))
	(or (nil? text) (.startsWith txt text)))
               ins
               (recur (rest ail)) ))) )))

