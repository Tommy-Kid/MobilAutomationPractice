#!/bin/zsh
# Interaktív tesztindító: a device-farmból lekért eszközök közül számmal választasz, majd tag(ek)et.
#   ./run-tests.sh            interaktív
#   APPIUM_PORT=4723          a farm portja
# Az Appium szervernek előtte futnia kell (start-appium.sh), az emulátorokat/szimulátorokat előbb el kell indítani.
DIR="${0:A:h}"
ROOT="${DIR:h}"
source "$DIR/lib/env.sh"
URL="http://127.0.0.1:${APPIUM_PORT:-4723}"

curl -sf -m 3 "$URL/status" >/dev/null || { echo "Nem fut az Appium ($URL). Előbb: $DIR/start-appium.sh" >&2; exit 1; }

# ---------- Eszköz választás ----------
# A végpont egyes számú: /device-farm/api/device (a /devices 404-et ad).
# A leállított (Shutdown) szimulátorokat kihagyjuk, csak a futó eszközök jelennek meg.
devices=("${(@f)$(curl -sf -m 5 "$URL/device-farm/api/device" | jq -r '
  (if type=="array" then . else (.devices // []) end)[]
  | select((.offline // false | not) and (.state != "Shutdown"))
  | select((.state // "") != "Shutdown")
  | [.udid, (.platform // "?"), (.deviceType // "?"), (.name // .udid), (if (.busy // false) then "FOGLALT" else "szabad" end)]
  | @tsv')}")
devices=(${devices:#})
if (( ${#devices} == 0 )); then
  echo "A farm nem lát eszközt. Indíts emulátort: $DIR/emulator.sh start <avd>" >&2; exit 1
fi

echo "Eszközök:"
i=1
for d in $devices; do
  f=("${(@s:	:)d}")
  printf "  %2d) %-18s %-8s %-9s %s [%s]\n" $i "$f[1]" "$f[2]" "$f[3]" "$f[4]" "$f[5]"
  (( i++ ))
done
echo "   p) párhuzamosan, Android + iOS kiegyensúlyozva (mindkét platformon legalább 1 szál)"
echo -n "Melyik eszközön? (szám, p = párhuzamos, Enter = bármelyik szabad): "
read choice
udid_arg=(); platform_arg=(); parallel_arg=()
if [[ "$choice" == p ]]; then
  echo -n "Hány szál? (min. 2, alapért. 2): "
  read threads; threads="${threads:-2}"
  [[ "$threads" == <-> && $threads -ge 2 ]] || { echo "Legalább 2 szál kell, hogy mindkét platform kapjon egyet" >&2; exit 1; }
  platform_arg=(-Dplatform=any)
  parallel_arg=(-Djunit.jupiter.execution.parallel.config.fixed.parallelism="$threads")
elif [[ -n "$choice" ]]; then
  [[ "$choice" == <-> && $choice -ge 1 && $choice -le ${#devices} ]] || { echo "Érvénytelen szám" >&2; exit 1; }
  f=("${(@s:	:)devices[$choice]}")
  udid_arg=(-Dudid="$f[1]"); platform_arg=(-Dplatform="${f[2]:l}")
  parallel_arg=(-Djunit.jupiter.execution.parallel.config.fixed.parallelism=1)
else
  echo -n "Platform? (android/ios, Enter = config alapértelmezett): "
  read p; [[ -n "$p" ]] && platform_arg=(-Dplatform="$p")
fi

# ---------- Tag választás ----------
tags=("${(@f)$(grep -rhoE '@Tag\( *"[^"]+" *\)' "$ROOT/appium/src/test" | sed -E 's/@Tag\( *"(.*)" *\)/\1/' | sort -u)}")
tags=(${tags:#})
echo
echo "Tagek:"
echo "   0) mind (nincs szűrés)"
i=1
for t in $tags; do printf "  %2d) %s\n" $i "$t"; (( i++ )); done
echo -n "Melyik(ek)? (szám(ok) szóközzel/vesszővel, Enter = mind): "
read sel
groups_arg=()
sel="${sel//,/ }"
if [[ -n "${sel// /}" && "$sel" != "0" ]]; then
  picked=()
  for n in ${=sel}; do
    [[ "$n" == <-> && $n -ge 1 && $n -le ${#tags} ]] || { echo "Érvénytelen tag szám: $n" >&2; exit 1; }
    picked+=("$tags[$n]")
  done
  groups_arg=(-Dgroups="${(j:,:)picked}")   # vessző = VAGY
fi

echo
echo ">> mvn -f appium/pom.xml verify $platform_arg $udid_arg $parallel_arg $groups_arg"
exec mvn -f "$ROOT/appium/pom.xml" verify $platform_arg $udid_arg $parallel_arg $groups_arg
