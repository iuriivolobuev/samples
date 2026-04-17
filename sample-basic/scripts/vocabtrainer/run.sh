#!/bin/bash

cd "$(dirname "$0")"
#"rlwrap" is used for better interactions (the left & right arrows to move the cursor, the up arrow to get previous input)
rlwrap java -cp lib sample.basic.util.VocabTrainer ~/Documents/vocab.csv
