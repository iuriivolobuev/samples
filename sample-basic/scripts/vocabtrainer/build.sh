#!/bin/bash

cd "$(dirname "$0")"
rm -rf ~/apps/vocabtrainer/
mkdir -p ~/apps/vocabtrainer/
cp run.sh ~/apps/vocabtrainer/

cd ../../src/main/java/sample/basic/util
javac -d lib VocabTrainer.java
mv lib/ ~/apps/vocabtrainer/
