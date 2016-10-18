(ns ads-mockup.ads
  (:require [io.pedestal.http :as http]
            [ads-mockup.ads-data :as data]
            [clojure.tools.logging :as log]
            [clj-time.core :as t]
            [clj-time.format :as f]))


;; sends a json response
(defn send-response [response]
  (log/info (str "*** send-response"))  
  (http/json-response response))

;; sends a response when no suitable ad is found
(defn no-suitable-ad []
  (log/info (str "*** no-suitable-ad"))  
  (send-response {"Info" "No Suitable ad"}))

;; try to convert a text into a Long  
(defn parse-long [text-num]
  (log/info (str "*** parse-long"))  
  (try
    (Long/parseLong text-num)
    (catch Throwable e
      (log/error (str "*** ERROR: connot convert to number" e))
      -1)))

;; filter an ad by using its id as the filter
(defn get-ad-by-id [id]
  (log/info (str "*** get-as-by-id"))  
  (let [ad (first (filter #(= (parse-long id) (:id %)) @data/ADS))]
    (if (empty? ad)
      {"Info" "ad not found"}
      ad)))

;; get a channel by its url
(defn get-channel-by-uri [uri]
  (log/info (str "*** get-channel-by-uri"))  
  (let [curr-channel (first (filter #(= uri (:url %)) data/channels))]
    curr-channel))

;; get a list of all ads
(defn get-ads [request]
  (log/info (str "*** get-ads"))  
  (send-response @data/ADS))

;; get an ad by its id
(defn get-ad [request]
  (log/info (str "*** get-ad"))  
  (let [ad-id (get-in request [:path-params :id])]
    (send-response (get-ad-by-id ad-id))))

;; get a list of all channels
(defn get-channels [request]
  (log/info (str "*** get-channels"))  
  (send-response data/channels))

(defn get-channel-by-id [request]
  (log/info (str "*** get-channel"))  
  (let [channel-url (get-in request [:path-params :url])]
    (log/info (str "*** channel-url : " channel-url))  
    (if-let [curr-channel (get-channel-by-uri channel-url)]
      (send-response (get-channel-by-uri channel-url))
      (send-response {"Info" "Channel doesn't exist"}))))

;; filter the ads by age
(defn filter-ages [age filtered-ads]
  (log/info (str "*** filter-ages "))
  (let [lage (parse-long age)]
    (filter
     #(and
       (< (compare (:min (:age %)) lage) 0)
       (> (compare (:max (:age %)) lage) 0))
     filtered-ads))
  )

;; filter the ads by date, this filter get a date and if the date
;; is between the ad date range, the filter returns the ad
(defn filter-dates [curr-date filtered-ads]
  (log/info (str "*** filter-dates: " curr-date))
  (filter
   #(and
     (t/after? (data/parse-date (:end-date %)) curr-date)
     (t/before? (data/parse-date (:start-date %)) curr-date))
   filtered-ads))

(defn dec-channels [data id channel]
  (log/info (str "***** DECCHANNELS ***** ID : " id " CHANNEL : " channel))
  (mapv (fn [m] (if (= (:id m) id)(update-in m [:limits-per-channel channel] (fn [v] (if (> v 0)(dec v) v))) m)) data))
  
(defn dec-views [data id]
  (log/info (str "***** DECVIEWS ***** " id))
  (mapv (fn [m] (update-in m [:limits-of-views] (fn [v] (if (= (:id m) id) (if (> v 0) (dec v)) v)))) data))

;; updates the views available for the ad being served
(defn dec-ad-views [channel ad-id]
  (log/info "***** DEC-AD-VIEWS BEFORE*****")
  (log/info (str "SERVING AD - CHANNEL : " channel " AD ID : " ad-id))
  (swap! data/ADS dec-views ad-id)
  (if-let [ad (get (:limits-per-channel (first (filter #(= ad-id (:id %)) @data/ADS))) channel)]
    (swap! data/ADS dec-channels ad-id channel))
  )

;; this function checks how many results we have, if we have one result
;; it serve the single result otherwise if we have more than one suitable
;; ad, we order the results and serve the ad with more amount of views
;; available.
(defn choose-ad-to-serve [filtered-ads channel]
  (log/info "***** CHOOSE-AD-TO-SERVE BEFORE*****")
  (log/info (with-out-str (clojure.pprint/pprint filtered-ads)))
  (let [ad-to-serve (last (sort-by :limits-of-views filtered-ads)) ad-id (:id ad-to-serve)]
    (dec-ad-views channel ad-id)
    (send-response ad-to-serve)))

;;checks if the current channel have limited availability
;;with the current filtered ads
(defn get-channel-ads-limits [filtered-ads channel]
  (log/info "***** GET-CHANNEL-ADS-LIMITS BEFORE *****")
  (log/info (with-out-str (clojure.pprint/pprint filtered-ads)))
  (let [with-limits-no-channel (filter #(= nil (get (:limits-per-channel %) channel)) filtered-ads)
        with-limits-gt-cero (filter #(> (compare (get (:limits-per-channel %) channel) 0) 0) filtered-ads)
        channel-ads-limits (flatten (merge with-limits-no-channel with-limits-gt-cero))]
    (log/info "######### el merge : " channel-ads-limits)
    (if (empty? channel-ads-limits)
      (no-suitable-ad)
      (choose-ad-to-serve filtered-ads channel))))

;; get all available ads, every ad have a limit of views in general
;; and available here means that the ad still have views available
(defn get-ads-limits [filtered-ads channel]
  (log/info "***** GET-ADS-LIMITS BEFORE *****")
  (log/info (with-out-str (clojure.pprint/pprint filtered-ads)))
  (let [ads-limits (filter #(< 0 (:limits-of-views %)) filtered-ads)]
    (if (empty? ads-limits)
      (no-suitable-ad)
      (get-channel-ads-limits ads-limits channel))))

;; get the ads between a date range, every ad have defined a date interval
;; when the ad could be served
(defn get-ads-by-date [filtered-ads channel]
  (log/info (str "*** get-ads-by-date"))
  (log/info (with-out-str (clojure.pprint/pprint filtered-ads)))
  (let [curr-date (t/now) ads-by-date (filter-dates curr-date filtered-ads)]
    (if (empty? ads-by-date)
      (no-suitable-ad)
      (get-ads-limits ads-by-date channel)))
  )

;; every add have an age range defined if the age parameter is passed
;; to the query parameters this function uses the age range to filter
;; these ads that have a concident age between the age interval
(defn get-ads-by-age [params filtered-ads channel]
  (log/info (str "*** get-ads-by-age "))
  (log/info (with-out-str (clojure.pprint/pprint filtered-ads)))
  (if-let [age (params :age)]
    (let [ads-by-age (filter-ages age filtered-ads)]
      (if (empty? ads-by-age)
        (no-suitable-ad)
        (do (let [nparams (dissoc params :age)]
              (when (not-empty nparams)
                (log/warn (str "*** Warning Unnecesary Params")))
              (get-ads-by-date ads-by-age channel)))))
    (get-ads-by-date filtered-ads channel)))

;; filter the ads by gender, the genders could be M or F
;; the target gender of the advertising is defined on each ad
(defn get-ads-by-gender [params filtered-ads channel]
  (log/info (str "*** get-ads-by-gender "))
  (log/info (with-out-str (clojure.pprint/pprint filtered-ads)))
  (if-let [gender (params :gender)]
    (let [ads-by-gender (filter #(= gender (:gender %)) filtered-ads)]
      (if (empty? ads-by-gender)
        (no-suitable-ad)
        (let [nparams (dissoc params :gender)]
          (if (empty? nparams)
            (get-ads-by-date ads-by-gender channel)
            (get-ads-by-age nparams ads-by-gender channel)))))
    (get-ads-by-age params filtered-ads channel)))

;; filter the ads by lang if the parameter is provided
;; if the parameter isn't provided we continue checkin parameters
(defn get-ads-by-lang [params filtered-ads channel]
  (log/info (str "*** get-ads-by-lang "))
  (log/info (with-out-str (clojure.pprint/pprint filtered-ads)))
  (if-let [lang (params :lang)]
    (let [ads-by-lang (filter #(= lang (:lang %)) filtered-ads)]
      (if (empty? ads-by-lang)
        (no-suitable-ad)
        (let [nparams (dissoc params :lang)]
          (if (empty? nparams)
            (get-ads-by-date ads-by-lang channel)
            (get-ads-by-gender nparams ads-by-lang channel)))))
    (get-ads-by-gender params filtered-ads channel)))

;; filter the ads by country if the parameter is provided
;; if the parameter isn't provided we continue checkin parameters
(defn get-ads-by-country [params filtered-ads channel]
  (log/info (str "*** get-ads-by-country "))
  (log/info (with-out-str (clojure.pprint/pprint filtered-ads)))
  (if-let [country (params :country)]
    (let [ads-by-country (filter #(= country (:country %)) filtered-ads)]
      (if (empty? ads-by-country)
        (no-suitable-ad)
        (let [nparams (dissoc params :country)]
          (if (empty? nparams)
            (get-ads-by-date ads-by-country channel)
            (get-ads-by-lang nparams ads-by-country channel)))))
    (get-ads-by-lang params filtered-ads channel)))

;; filter the ads by the type of the channel
;; only we can serve ads on a channel of the same type of the ad
(defn get-ads-by-type [type params channel]
  (log/info (str "*** get-ads-by-type "))
  (if-let [ads-by-type (filter #(= type (:type %)) @data/ADS)]
    (do
      (log/info (str "***** FILTER DATA/ADS ******"))
      (log/info (with-out-str (clojure.pprint/pprint ads-by-type)))
      (if (empty? params)
        (get-ads-by-date ads-by-type channel)
        (get-ads-by-country params ads-by-type channel)))))

;; Verify if there is a channel parameter and if the parameter exists then
;; find the channel in the defined channels to check thet it is valid
(defn get-channel [params]
  (log/info (str "*** get-channel "))
  (if-let [channel (:channel params)]
    (if-let [curr-channel (get-channel-by-uri channel)]
      (get-ads-by-type (:type curr-channel) (dissoc params :channel) channel)
      (send-response {"Warning" "Channel doesn't exist"}))
    (send-response {"Warning" "need to pass a channel parameter"})))

;; The api handler is where we request the ads
;; by default the response from the api is going to be json
(defn api [request]
  (log/info (str "*** api process the request "))
  (let [query-params (:query-params request)]
    (if (= (count query-params) 0)
      (send-response {"Info" "Api endpoint"})
      (get-channel query-params)
      )))
