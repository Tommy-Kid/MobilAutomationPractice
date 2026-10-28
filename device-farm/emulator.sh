#!/bin/zsh
# Emulátor kezelés (a Jenkinsfile Windows-os 'Start emulator' lépésének macOS megfelelője).
#   ./emulator.sh create <avd> [system-image]   AVD létrehozása
#   ./emulator.sh start  <avd> [port]           indítás + megvárja a bootot (alapért. port: 5554)
#   ./emulator.sh stop   [port]                 leállítás
#   ./emulator.sh list
source "${0:A:h}/lib/env.sh"
cmd="$1"; shift

case "$cmd" in
  create)
    avd="${1:?avd név kell}"; image="${2:-system-images;android-35;google_apis;arm64-v8a}"
    echo no | avdmanager create avd -n "$avd" -k "$image" -d pixel_8 --force ;;
  start)
    avd="${1:?avd név kell}"; port="${2:-5554}"; serial="emulator-$port"
    nohup emulator -avd "$avd" -port "$port" -no-audio -no-boot-anim >"/tmp/emulator-$port.log" 2>&1 &
    emu_pid=$!
    for i in {1..60}; do
      # ha az emulátor folyamat kilépett (pl. hibás AVD), ne várjunk a végtelenségig
      kill -0 $emu_pid 2>/dev/null || { echo "Az emulátor ($avd) leállt indulás közben:" >&2; grep -E "FATAL|ERROR" "/tmp/emulator-$port.log" | tail -3 >&2; exit 1; }
      if [[ "$(adb -s "$serial" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" == "1" ]]; then
        # boot_completed után a rendszer még percekig terhelt lehet; megvárjuk, hogy a package manager és a wm is válaszoljon,
        # különben az Appium device-farm adb-hívásai (getprop, wm size) 60 s után time-outolnak, és kihagyja az eszközt.
        for j in {1..36}; do
          adb -s "$serial" shell pm path android >/dev/null 2>&1 && adb -s "$serial" shell wm size >/dev/null 2>&1 \
            && { echo "$serial bootolt"; exit 0; }
          sleep 5
        done
        echo "$serial bootolt, de nem válaszol" >&2; exit 1
      fi
      sleep 5
    done
    echo "Az emulátor nem bootolt idő alatt" >&2; exit 1 ;;
  stop)
    adb -s "emulator-${1:-5554}" emu kill ;;
  list)
    emulator -list-avds ;;
  *) sed -n '2,7p' "$0"; exit 1 ;;
esac
