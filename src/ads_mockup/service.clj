(ns ads-mockup.service
  (:require [io.pedestal.http :as http]
[io.pedestal.http.route :as route]
[io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [ads-mockup.ads :as ads]))


;; "/" handler is a simple page with a text message to check that the server is running
(defn home-page
  [request]
  (ring-resp/response {"ads-mockup server" "Running"}))

;; define some common interceptors
;; in this case we only use json-body interceptor
;; these interceptors are like ring middleware 
(def common-interceptors [http/json-body])

;; all routes defined in the rest service
(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/api" :get (conj common-interceptors `ads/api)]
              ["/api/ads" :get (conj common-interceptors `ads/get-ads)]
              ["/api/ads/:id" :get (conj common-interceptors `ads/get-ad)]
              ["/api/channels" :get (conj common-interceptors `ads/get-channels)]
              ["/api/channels/:url" :get (conj common-interceptors `ads/get-channel-by-id)]})

;; Consumed by ads-mockup.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port 8080
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false}})

