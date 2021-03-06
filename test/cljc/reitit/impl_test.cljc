(ns reitit.impl-test
  (:require [clojure.test :refer [deftest testing is are]]
            [reitit.impl :as impl]))

(deftest segments-test
  (is (= ["" "api" "ipa" "beer" "craft" "bisse"]
         (into [] (impl/segments "/api/ipa/beer/craft/bisse"))))
  (is (= ["" "a" "" "b" "" "c" ""]
         (into [] (impl/segments "/a//b//c/")))))

(deftest strip-nils-test
  (is (= {:a 1, :c false} (impl/strip-nils {:a 1, :b nil, :c false}))))

(deftest url-encode-and-decode-test
  (is (= "reitit.impl-test%2Fkikka" (-> ::kikka
                                        impl/into-string
                                        impl/url-encode)))
  (is (= ::kikka (-> ::kikka
                     impl/into-string
                     impl/url-encode
                     impl/url-decode
                     keyword))))

(deftest path-params-test
  (is (= {:n "1"
          :n1 "-1"
          :n2 "1"
          :n3 "1"
          :n4 "1"
          :n5 "1"
          :d "2.2"
          :b "true"
          :s "kikka"
          :u "c2541900-17a7-4353-9024-db8ac258ba4e"
          :k "kikka"
          :qk "reitit.impl-test%2Fkikka"
          :nil nil}
         (impl/path-params {:n 1
                            :n1 -1
                            :n2 (long 1)
                            :n3 (int 1)
                            :n4 (short 1)
                            :n5 (byte 1)
                            :d 2.2
                            :b true
                            :s "kikka"
                            :u #uuid "c2541900-17a7-4353-9024-db8ac258ba4e"
                            :k :kikka
                            :qk ::kikka
                            :nil nil}))))

(deftest query-params-test
  (are [x y]
    (= (impl/query-string x) y)
    {:a "b"} "a=b"
    {"a" "b"} "a=b"
    {:a 1} "a=1"
    {:a nil} "a="
    {:a :b :c "d"} "a=b&c=d"
    {:a "b c"} "a=b%20c"))

; TODO: support seq values?
;{:a ["b" "c"]} "a=b&a=c"
;{:a ["c" "b"]} "a=c&a=b"
;{:a (seq [1 2])} "a=1&a=2"
;{:a #{"c" "b"}} "a=b&a=c"
