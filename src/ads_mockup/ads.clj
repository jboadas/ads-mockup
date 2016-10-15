(ns ads-mockup.ads
  (:require [io.pedestal.http :as http]
            [ads-mockup.ads-data :as data]
            [clojure.tools.logging :as log]
            [clj-time.core :as t]
            [clj-time.format :as f]))

;; sends a suitable as as a response
(defn send-response [response]
  (log/info (str "*** send-response"))  
  (http/json-response response))

;; sends a response when no suitable ad is found
(defn no-suitable-ad []
  (log/info (str "*** no-suitable-ad"))  
  (send-response {"Info" "No Suitable ad"}))

;; filter an ad by using its id as the filter
(defn get-ad-by-id [id]
  (log/info (str "*** get-as-by-id"))  
  (first (filter #(= (Long/parseLong id) (:id %)) @data/ADS)))

;; get a channel by its url
(defn get-channel-by-uri [uri]
  (log/info (str "*** get-channel-by-uri"))  
  (let [curr-channel (first (filter #(= uri (:url %)) data/channels))]
    curr-channel))

;; get a list of all ads
(defn get-ads [request]
  (log/info (str "*** get-ads"))  
  (send-response @data/ADS))

;; get a list of all channels
(defn get-channels [request]
  (log/info (str "*** get-channels"))  
  (send-response data/channels))

;; filter the ads by age
(defn filter-ages [age filtered-ads]
  (log/info (str "*** filter-ages "))
  (try
    (let [lage (Long/parseLong age)]
     (filter
      #(and
        (< (compare (:min (:age %)) lage) 0)
        (> (compare (:max (:age %)) lage) 0))
      filtered-ads))
    (catch Throwable e
      (log/error (str "*** ERROR : Age must be a number")))))

;; filter the ads by date, this filter get a date and if the date
;; is between the ad date range, the filter returns the ad
(defn filter-dates [curr-date filtered-ads]
  (log/info (str "*** filter-dates: " curr-date))
  (filter
   #(and
     (t/after? (data/parse-date (:end-date %)) curr-date)
     (t/before? (data/parse-date (:start-date %)) curr-date))
   filtered-ads))

;; get all available ads, every ad have a limit of views in general
;; and available here means that the ad still have views available
(defn get-available-ads [filtered-ads]
  (log/info (str "*** get-available-ads"))
  (let [available-ads  (filter #(< 0 (:limits-of-views %)) filtered-ads)]
    (if (empty? available-ads)
      (no-suitable-ad)
      (send-response available-ads))))

;; get the ads between a date range, every ad have defined a date interval
;; when the ad could be served
(defn get-ads-by-date [filtered-ads]
  (log/info (str "*** get-ads-by-date"))
  (let [curr-date (t/now) ads-by-date (filter-dates curr-date filtered-ads)]
    (if (empty? ads-by-date)
      (no-suitable-ad)
      (get-available-ads ads-by-date)))
  )

;; every add have an age range defined if the age parameter is passed
;; to the query parameters this function uses the age range to filter
;; these ads that have a concident age between the age interval
(defn get-ads-by-age [params filtered-ads]
  (log/info (str "*** get-ads-by-age "))
  (if-let [age (params :age)]
    (let [ads-by-age (filter-ages age filtered-ads)]
      (if (empty? ads-by-age)
        (no-suitable-ad)
        (do (let [nparams (dissoc params :age)]
              (when (not-empty nparams)
                (log/warn (str "*** Warning Unnecesary Params")))
              (get-ads-by-date ads-by-age)))))
    (get-ads-by-date filtered-ads)))

;; filter the ads by gender, the genders could be M or F
;; the target gender of the advertising is defined on each ad
(defn get-ads-by-gender [params filtered-ads]
  (log/info (str "*** get-ads-by-gender "))
  (if-let [gender (params :gender)]
    (let [ads-by-gender (filter #(= gender (:gender %)) filtered-ads)]
      (if (empty? ads-by-gender)
        (no-suitable-ad)
        (let [nparams (dissoc params :gender)]
          (if (empty? nparams)
            (get-ads-by-date ads-by-gender)
            (get-ads-by-age nparams ads-by-gender)))))
    (get-ads-by-age params filtered-ads)))

;; filter the ads by lang if the parameter is provided
;; if the parameter isn't provided we continue checkin parameters
(defn get-ads-by-lang [params filtered-ads]
  (log/info (str "*** get-ads-by-lang "))
  (if-let [lang (params :lang)]
    (let [ads-by-lang (filter #(= lang (:lang %)) filtered-ads)]
      (if (empty? ads-by-lang)
        (no-suitable-ad)
        (let [nparams (dissoc params :lang)]
          (if (empty? nparams)
            (get-ads-by-date ads-by-lang)
            (get-ads-by-gender nparams ads-by-lang)))))
    (get-ads-by-gender params filtered-ads)))

;; filter the ads by country if the parameter is provided
;; if the parameter isn't provided we continue checkin parameters
(defn get-ads-by-country [params filtered-ads]
  (log/info (str "*** get-ads-by-country "))
  (if-let [country (params :country)]
    (let [ads-by-country (filter #(= country (:country %)) filtered-ads)]
      (if (empty? ads-by-country)
        (no-suitable-ad)
        (let [nparams (dissoc params :country)]
          (if (empty? nparams)
            (get-ads-by-date ads-by-country)
            (get-ads-by-lang nparams ads-by-country)))))
    (get-ads-by-lang params filtered-ads)))

;; filter the ads by the type of the channel
;; only we can serve ads on a channel of the same type of the ad
(defn get-ads-by-type [type params]
  (log/info (str "*** get-ads-by-type "))
  (if-let [ads-by-type (filter #(= type (:type %)) @data/ADS)]
    (if (empty? params)
      (get-ads-by-date ads-by-type)
      (get-ads-by-country params ads-by-type))))

;; Verify if there is a channel parameter and if the parameter exists then
;; find the channel in the defined channels to check thet it is valid
(defn get-channel [params]
  (log/info (str "*** get-channel "))
  (if-let [channel (:channel params)]
    (if-let [curr-channel (get-channel-by-uri channel)]
      (get-ads-by-type (:type curr-channel) (dissoc params :channel))
      (send-response {"Warning" "Channel doesn't exist"}))
    (send-response {"Warning" "need to pass a channel parameter"})))

;; The api handler is where we request the ads
;; by default the response from the api is going to be json
(defn api [request]
  (let [query-params (:query-params request)]
    (if (= (count query-params) 0)
      (send-response {"Info" "Api endpoint"})
      (get-channel query-params)
      )))
