(ns ads-mockup.ads-data
  (require  [clj-time.core :as t]
            [clj-time.format :as f]))

(def custom-formatter (f/formatter :date-hour-minute))

(defn unparse-date [date]
  (f/unparse custom-formatter date))

(defn parse-date [date]
  (f/parse custom-formatter date))

(def channels
  [{:name "News Channel"
    :url "www.news.com"
    :type :news}
   {:name "Cooking Channel"
    :url "www.cooking.com"
    :type :cooking}
   {:name "Fashion Channel"
    :url "www.fashion.com"
    :type :fashion}
   {:name "Travel Channel"
    :url "www.travel.com"
    :type :travel}
   ])

(def ADS
  (atom
   [{:id 1
     :ad-text "This is ad for a News Channel USA/English"
     :type :news
     :country "US"
     :lang "en"
     :age {:min 20 :max 30}
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/minutes 5)))
     :limits-of-views 50
     :limits-per-channel
     {"www.times-blog.com" 2
      "www.times-news.com" 4}}
    {:id 2
     :ad-text "This is an ad for a Cooking Channel Spain/Spanish"
     :type :cooking
     :country "ES"
     :lang "es"
     :age {:min 30 :max 40}
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/hours 10)))
     :limits-of-views 10
     :limits-per-channel {"mychannel" 3}
     }
    {:id 3
     :ad-text "This is an ad for a News channel Denmark/Denmark"
     :type :news
     :country "DE"
     :lang "de"
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/hours 10)))
     :limits-of-views 5}
    {:id 4
     :ad-text "This is an ad for a News channel England/English"
     :type :news
     :country "ES"
     :lang "en"
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/hours 10)))
     :limits-of-views 23}
    {:id 5
     :ad-text "This is an ad for a Fashion channel USA/Spanish"
     :type :fashion
     :country "US"
     :lang "es"
     :gender "F"
     :age {:min 30 :max 40}
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/hours 10)))
     :limits-of-views 2}
    {:id 6
     :ad-text "This is an ad for a Fashion channel USA/Spanish"
     :type :fashion
     :country "US"
     :lang "es"
     :gender "M"
     :age {:min 40 :max 50}
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/hours 7)))
     :limits-of-views 2}
    {:id 7
     :ad-text "This is an ad for a Fashion channel Spain/Spanish"
     :type :fashion
     :country "ES"
     :lang "es"
     :gender "F"
     :age {:min 40 :max 50}
     :start-date (unparse-date (t/now))
     :end-date (unparse-date (t/plus (t/now) (t/hours 5)))
     :limits-of-views 2}
    ]))
