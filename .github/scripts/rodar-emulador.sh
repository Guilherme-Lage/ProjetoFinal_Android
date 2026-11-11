#!/usr/bin/env bash
# Instala o app, grava a tela enquanto o teste percorre o roteiro e baixa prints e vídeo.
set -u
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk

adb shell screenrecord --size 720x1600 --bit-rate 4000000 --time-limit 180 /sdcard/demo.mp4 &
GRAVACAO=$!
sleep 2

adb shell am instrument -w com.grupo07.pokedex.test/androidx.test.runner.AndroidJUnitRunner | tee instrument.log

sleep 2
adb shell pkill -INT screenrecord || true
wait $GRAVACAO || true
sleep 3

mkdir -p docs/prints
adb pull /sdcard/Android/data/com.grupo07.pokedex/files/prints/. docs/prints/ || true
adb pull /sdcard/demo.mp4 docs/demo.mp4 || true
adb shell cmd connectivity airplane-mode disable || true
