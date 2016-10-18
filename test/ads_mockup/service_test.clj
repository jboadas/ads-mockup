(ns ads-mockup.service-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [ads-mockup.service :as service]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

;; test the base route /
(deftest home-page-test
  (is (=
       (:status (response-for service :get "/"))
       200))
  (is (=
       (:headers (response-for service :get "/"))
       {"Content-Type" "application/json;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

;; test a random page to get 404 response
(deftest not-found
  (is (=
       (:status (response-for service :get "/any-page"))
       404))
  (is (=
       (:headers (response-for service :get "/any-page"))
       {"Content-Type" "text/plain"})))

(deftest api-endpoint
  (is (=
       (:status (response-for service :get "/api"))
       200))
  (is (=
       (:headers (response-for service :get "/api"))
       {"Content-Type" "application/json;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

(deftest list-all-ads
  (is (=
       (:status (response-for service :get "/api/ads"))
       200))
  (is (=
       (:headers (response-for service :get "/api/ads"))
       {"Content-Type" "application/json;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

(deftest list-all-channels
  (is (=
       (:status (response-for service :get "/api/channels"))
       200))
  (is (=
       (:headers (response-for service :get "/api/channels"))
       {"Content-Type" "application/json;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

(deftest get-ad-channel
  (is (=
       (:status (response-for service :get "/api?channel=www.news.com"))
       200))
  (is (=
       (:headers (response-for service :get "/api?channel=www.news.com"))
       {"Content-Type" "application/json;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

(deftest get-ad-no-channel-parameter
  (is (=
       (:status (response-for service :get "/api?parameter=www.news.com"))
       200))
  (is (=
       (:headers (response-for service :get "/api?parameter=www.news.com"))
       {"Content-Type" "application/json;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

(deftest get-ad-no-existing-channel
  (is (=
       (:status (response-for service :get "/api?channel=www.nochannel.com"))
       200))
  (is (=
       (:headers (response-for service :get "/api?channel=www.nochannel.com"))
       {"Content-Type" "application/json;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

(deftest get-ad-malformed-uri
  (is (=
       (:status (response-for service :get "/api?abcd&dfgh=123"))
       200))
  (is (=
       (:headers (response-for service :get "/api?abcd&dfgh=123"))
       {"Content-Type" "application/json;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

(deftest get-ad-unused-parameters
  (is (=
       (:status (response-for service :get "/api?channel=www.news.com?par=123"))
       200))
  (is (=
       (:headers (response-for service :get "/api?channel=www.news.com?par=123"))
       {"Content-Type" "application/json;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

(deftest get-ad-channel-and-country
  (is (=
       (:status (response-for service :get "/api?channel=www.news.com&country=US"))
       200))
  (is (=
       (:headers (response-for service :get "/api?channel=www.news.com&country=US"))
       {"Content-Type" "application/json;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

(deftest get-ad-channel-and-lang
  (is (=
       (:status (response-for service :get "/api?channel=www.news.com&lang=en"))
       200))
  (is (=
       (:headers (response-for service :get "/api?channel=www.news.com&lang=en"))
       {"Content-Type" "application/json;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

(deftest get-ad-channel-and-gender
  (is (=
       (:status (response-for service :get "/api?channel=www.news.com&gender=F"))
       200))
  (is (=
       (:headers (response-for service :get "/api?channel=www.news.com&gender=F"))
       {"Content-Type" "application/json;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

(deftest get-ad-channel-and-age
  (is (=
       (:status (response-for service :get "/api?channel=www.news.com&age=33"))
       200))
  (is (=
       (:headers (response-for service :get "/api?channel=www.news.com&age=33"))
       {"Content-Type" "application/json;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))

(deftest get-ad-all-params
  (is (=
       (:status (response-for service :get "/api?channel=www.cooking.com&country=US&lang=en&gender=F&age=16"))
       200))
  (is (=
       (:headers (response-for service :get "/api?channel=www.cooking.com&country=US&lang=en&gender=F&age=16"))
       {"Content-Type" "application/json;charset=UTF-8"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"})))
