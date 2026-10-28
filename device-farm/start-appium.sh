#!/bin/zsh
# Appium device farm indítása.
#   PLATFORM=ios|android|both   (alapért.: both)
#   APPIUM_PORT=4723            BASE_PATH=/   (a Java keretrendszer a "/" útvonalat várja)
#   IOS_TYPE=real|simulated|both  ANDROID_TYPE=real|emulated|both
#   START_EMULATORS=0 / START_SIMULATORS=0   ne indítsa el az AVD-ket / szimulátorokat
# Pl.: PLATFORM=android ANDROID_TYPE=emulated ./start-appium.sh
DIR="${0:A:h}"
source "$DIR/lib/env.sh"

mkdir -p "$HOME/.appium"

# Az összes létező AVD és iOS szimulátor elindítása, hogy a farm lássa őket (START_EMULATORS=0 / START_SIMULATORS=0 kikapcsolja).
source "$DIR/lib/devices.sh"
start_all_emulators
boot_all_simulators

exec appium server \
    -ka 800 \
    -a 127.0.0.1 \
    -p "${APPIUM_PORT:-4723}" \
    -pa "${BASE_PATH:-/}" \
    --use-plugins device-farm,inspector \
    --plugin-device-farm-platform "${PLATFORM:-both}" \
    --plugin-device-farm-ios-device-type "${IOS_TYPE:-both}" \
    --plugin-device-farm-android-device-type "${ANDROID_TYPE:-both}" \
    --allow-insecure '*:chromedriver_autodownload' \
    --relaxed-security \
    --log "$HOME/.appium/appium.log" \
    --log-level "${LOG_LEVEL:-debug}" \
    --log-timestamp \
    --local-timezone \
    --default-capabilities "$(cat "$DIR/config/capabilities.json")"
