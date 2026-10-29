#!/bin/zsh
# Az összes létező Android AVD és iOS szimulátor elindítása, hogy a device-farm lássa őket.
# Kikapcsolás: START_EMULATORS=0 / START_SIMULATORS=0 (a PLATFORM / *_TYPE beállítást is figyelembe veszi).

start_all_emulators() {
  [[ "${START_EMULATORS:-1}" == 1 && "${PLATFORM:-both}" != ios && "${ANDROID_TYPE:-both}" != real ]] || return 0
  local avds=("${(@f)$(emulator -list-avds 2>/dev/null)}") running=() serial avd port=5554 pids=()
  avds=(${avds:#})
  (( ${#avds} )) || { echo "Nincs AVD."; return 0 }

  for serial in ${(f)"$(adb devices | awk '/^emulator-/{print $1}')"}; do
    running+=("$(adb -s "$serial" emu avd name 2>/dev/null | head -1 | tr -d '\r')")
  done

  for avd in $avds; do
    if (( ${running[(Ie)$avd]} )); then echo "$avd már fut"; continue; fi
    while adb devices | grep -q "^emulator-$port"; do port=$((port + 2)); done
    echo "Emulátor indul: $avd (emulator-$port)"
    # Egymás után indítjuk: párhuzamosan egy MacBook Airen a második emulátor annyira lassú lett,
    # hogy a device-farm adb-hívásai time-outoltak, és kihagyta (emulator-5558).
    "$DIR/emulator.sh" start "$avd" "$port" || echo "Figyelem: $avd nem indult el rendesen" >&2
    port=$((port + 2))
  done
}

boot_all_simulators() {
  [[ "${START_SIMULATORS:-1}" == 1 && "${PLATFORM:-both}" != android && "${IOS_TYPE:-both}" != real ]] || return 0
  command -v xcrun >/dev/null || return 0
  local line udid name booted=()
  # sor: "    iPhone 17 (UDID) (Shutdown)"
  # Csak a kiválasztott szimulátorok indulnak (kiterjesztett regex az eszköznévre). Mind: IOS_SIMULATORS='.'
  local filter="${IOS_SIMULATORS:-^ *(iPhone 18 Pro|iPhone 18 Pro Max|iPhone 17e) \(}"
  for line in "${(@f)$(xcrun simctl list devices available | grep '(Shutdown)' | grep -E "$filter")}"; do
    [[ -n "$line" ]] || continue
    udid="$(print -r -- "$line" | grep -oE '[0-9A-F]{8}(-[0-9A-F]{4}){3}-[0-9A-F]{12}')"
    name="${${line## #}%% \(*}"
    echo "Szimulátor indul: $name ($udid)"
    xcrun simctl boot "$udid" 2>/dev/null && booted+=($udid)
  done
  (( ${#booted} )) && echo "Várok a szimulátorok bootjára (az első indulás percekig tarthat)..."
  for udid in $booted; do xcrun simctl bootstatus "$udid" -b >/dev/null 2>&1; echo "Szimulátor bootolt: $udid"; done
}
