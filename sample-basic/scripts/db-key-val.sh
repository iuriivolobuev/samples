#!/bin/bash

#Usage:
#1. source db-key-val.sh
#2. db_set a 15
#3. db_set b 25
#4. db_get a
#5. db_get b

db_set() {
  echo "$1,$2" >> db
}

db_get() {
  grep "^$1," db | sed -e "s/^$1,//" | tail -n 1
}
