#!/bin/bash
#
# Play ストア用の画面を、起動済みのエミュレータで撮る（store-screenshots.yml から呼ぶ）。
# 撮る画面は androidTest の StoreScreenshotTest が決める。出力は store-shots/<locale>/NN-name.png。
#
#   ./scripts/capture-store-screenshots.sh ja-JP en-US

set -euo pipefail
cd "$(dirname "$0")/.."

APP_ID="${APP_ID:?APP_ID を指定する}"
TEST_CLASS="${TEST_CLASS:?TEST_CLASS を指定する}"
APK="${APK:-app/build/outputs/apk/debug/app-debug.apk}"
TEST_APK="${TEST_APK:-app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk}"
OUT="${OUT:-store-shots}"

# ステータスバーを時刻 9:41・電池満タン・通知なしに固定する
adb shell settings put global sysui_demo_allowed 1
# SystemUI が設定を読むまで待たないと demo の指示が無視される
sleep 3
demo() { adb shell am broadcast -a com.android.systemui.demo -e command "$@" >/dev/null; }
demo enter
demo clock -e hhmm 0941
demo battery -e level 100 -e plugged false
demo network -e wifi show -e level 4 -e mobile show -e datatype none -e level 4
demo notifications -e visible false

rm -rf "$OUT"
for locale in "$@"; do
  adb install -r -g "$APK" >/dev/null
  adb install -r -g "$TEST_APK" >/dev/null
  adb shell pm clear "$APP_ID" >/dev/null
  # アプリ単位の言語（API 33+）。端末の言語を変えるより速く、再起動も要らない
  adb shell cmd locale set-app-locales "$APP_ID" --locales "$locale"
  result=$(adb shell am instrument -w -e class "$TEST_CLASS" "$APP_ID.test/androidx.test.runner.AndroidJUnitRunner")
  echo "$result"
  if ! grep -q "^OK (" <<<"$result"; then
    echo "$locale: 撮影のテストが失敗した" >&2
    exit 1
  fi
  mkdir -p "$OUT/$locale"
  adb exec-out run-as "$APP_ID" tar -c -C files store-shots | tar -x -C "$OUT/$locale" --strip-components=1
  ls "$OUT/$locale"
done
