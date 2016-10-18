(ns ads-mockup.ads-data
  (require  [clj-time.core :as t]
            [clj-time.format :as f]))

;; the date needs to be converted into a string because the
;; json encoder complains.
;; all dates are in UTC 
;; this is a formater to use with the date/time of the ads
;; the formatter only use the date, hour and minutes 
(def custom-formatter (f/formatter :date-hour-minute))

;; parse a string date into clj-time format
(defn unparse-date [date]
  (f/unparse custom-formatter date))

;; parse a clj-time date into a string
(defn parse-date [date]
  (f/parse custom-formatter date))

;; only these channels can request ads
;; the key of the channel is the URL
;; :name is a name for the channel
;; :url the url for the channel must be unique because is used as id
;; :type the type of channel, only :news, :cooking, :fashion and travel are defined
;; you can add new types and channels as you want just remmember to include the 3 keys
;; and if you want to ad a new type just use a keyword for it
(def channels
  [
   {:name "News Channel"
    :url "www.news.com"
    :type :news}
   {:name "Blog Channel"
    :url "www.blog.com"
    :type :news}
   {:name "Paper News Channel"
    :url "www.paper.com"
    :type :news}
   {:name "Cooking Channel"
    :url "www.cooking.com"
    :type :cooking}
   {:name "Kitchen Channel"
    :url "www.kitchen.com"
    :type :cooking}
   {:name "Fashion Channel"
    :url "www.fashion.com"
    :type :fashion}
   {:name "Beauty Channel"
    :url "www.beauty.com"
    :type :fashion}
   {:name "Travel Channel"
    :url "www.travel.com"
    :type :travel}
   {:name "Air Channel"
    :url "www.air.com"
    :type :travel}
   ])

;; here we define the ads to serve :limits-per-channel is optional
;; all othes keys are mandatory, only a few ads are defined to test
;; just enough the functionality of the code
;; :id                 -> is the id of the ad and must be unique
;; :ad-text            -> is a text that simulate the ad
;; :type               -> the type of the ad to filter with the :type od channel
;; :country            -> country where the ad can be served
;; :lang               -> the language of the ad
;; :gender             -> the target gender of the ad
;; :age                -> the range og age of the target of the ad
;; :start-date         -> the starting date when the ad is going to be available
;; :end-date           -> the date until the ad can be available
;; :limit-of-views     -> global limit of views that an ad can have from any channel
;; :limits-per-channel -> this is an optional key and it define the number of views for an individual channel

;; you can ad more ads adding more maps to the vector of ads, just remember the mandatory fields
(def ADS
  (atom 
   [{:id 1
     :ad-text "News ad en_US - M - 20/30"
     :type :news
     :country "US"
     :lang "en"
     :gender "M"
     :age {:min 20 :max 30}
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/minutes 6)))
     :limits-of-views 10
     :limits-per-channel {"www.blog.com" 2 "www.news.com" 3}
     }
    {:id 2
     :ad-text "News ad es_ES - F - 30/40"
     :type :news
     :country "ES"
     :lang "es"
     :gender "F"
     :age {:min 30 :max 40}
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/minutes 15)))
     :limits-of-views 10
     :limits-per-channel {"www.paper.com" 3}
     }
    {:id 3
     :ad-text "News ad de_DE - M - 0/100"
     :type :news
     :country "DE"
     :lang "de"
     :gender "M"
     :age {:min 0 :max 100}
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/hours 10)))
     :limits-of-views 5
     }
    {:id 4
     :ad-text "Cooking ad en_US - F - 15/20"
     :type :cooking
     :country "US"
     :lang "en"
     :gender "F"
     :age {:min 15 :max 20}
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/hours 10)))
     :limits-of-views 7
     }
    {:id 5
     :ad-text "Fashion ad es_US - F - 30/40"
     :type :fashion
     :country "US"
     :lang "es"
     :gender "F"
     :age {:min 30 :max 40}
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/hours 10)))
     :limits-of-views 2
     :limits-per-channel {"www.beauty.com" 3}
     }
    {:id 6
     :ad-text "Fashion ad en_US - M - 40/50"
     :type :fashion
     :country "US"
     :lang "en"
     :gender "M"
     :age {:min 40 :max 50}
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/hours 7)))
     :limits-of-views 2
     }
    {:id 7
     :ad-text "Travel ad de_DE - F - 0/50"
     :type :travel
     :country "DE"
     :lang "de"
     :gender "F"
     :age {:min 0 :max 50}
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/hours 5)))
     :limits-of-views 2
     }
    {:id 8
     :ad-text "Travel ad en_US - M - 20/50"
     :type :travel
     :country "US"
     :lang "en"
     :gender "M"
     :age {:min 20 :max 50}
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/hours 5)))
     :limits-of-views 20
     :limits-per-channel {"www.air.com" 2}
     }
    ]))
