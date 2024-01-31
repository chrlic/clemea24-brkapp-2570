__='
   This is the default license template.
   
   File: client.sh
   Author: mdivis
   Copyright (c) 2024 mdivis
   
   To edit this license information: Press Ctrl+Shift+P and press 'Create new License Template...'.
'

#!/bin/bash

CUST_ID=1000
ORDER_ID=2000
INVOICE_ID=3000
VENDOR_ID=100
PRODUCT_ID=200

CUST_CNT=20
ORDER_CNT=20
INVOICE_CNT=20
VENDOR_CNT=20
PRODUCT_CNT=20

SLEEP_INT=0.5

REPS=5

for ((rcnt=0; rcnt<REPS; rcnt++))
do
    for ((cid=$CUST_ID; cid<$((CUST_ID+CUST_CNT)); cid++))
    do
        for ((oid=$((ORDER_ID)); oid<$((ORDER_ID+ORDER_CNT)); oid++))
        do
            url="http://localhost:8765/api/customer/$cid/order/$oid"
            curl $url
            echo ""
            sleep $SLEEP_INT
        done
        for ((iid=$((INVOICE_ID)); iid<$((INVOICE_ID+INVOICE_CNT)); iid++))
        do
            url="http://localhost:8765/api/customer/$cid/invoice/$iid"
            curl $url
            echo ""
            sleep $SLEEP_INT
        done
    done

    for ((vid=$VENDOR_ID; vid<$((VENDOR_ID+VENDOR_CNT)); vid++))
    do
        for ((pid=$((PRODUCT_ID)); pid<$((PRODUCT_ID+PRODUCT_CNT)); pid++))
        do
            url="http://localhost:8765/api/vendor/$vid/product/$pid"
            curl $url
            echo ""
            sleep $SLEEP_INT
        done
        for ((cid=$CUST_ID; cid<$((CUST_ID+CUST_CNT)); cid++))
        do
            url="http://localhost:8765/api/vendor/$vid/customer/$cid"
            curl $url
            echo ""
            sleep $SLEEP_INT
        done
    done
done