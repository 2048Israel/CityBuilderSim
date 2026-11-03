#!/usr/bin/env bash
# The wages study's runs (docs/studies/spec-wages.md, section 5).
#
#   docs/studies/wages/run.sh OUT MONTHS SEEDS VARIANT...
#
# OUT is a folder outside the repository; MONTHS the months each city plays;
# SEEDS a list such as "0 1 2 3"; each VARIANT is
#   name:kind:floor:floorAt[:ripple[:key=value,key=value]]
# with kind "today" (the tree as it is), "proto" (both patches applied, each
# neutral until a property asks: real-minimum.patch, migration-target.patch) or
# "real" (proto with the floor a real minimum); floor the floor as a multiple
# of the founding unskilled rung, floorAt the month it is set; ripple the share
# of its step the band above a held band keeps; the last field more of the
# patches' properties, each passed as -Dwages.key=value (seniorPull=1.3,
# pullsOnHomes=true, slack=.05). One CSV per run: OUT/<name>-s<seed>.csv. JOBS
# runs at once (default 4). Run from the checkout's root after `mvn -q compile`
# with target/classpath.txt written (batch-check.sh writes it).
#
# Every city is founded in a TempDirs folder by WageProbe, never the save folder.
set -euo pipefail
OUT=$1; MONTHS=$2; SEEDS=$3; shift 3
JOBS=${JOBS:-4}
SEP=":"; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";";; esac
HERE=docs/studies/wages
mkdir -p "$OUT/classes" "$OUT/today" "$OUT/proto" "$OUT/src/ham/citybuildersim" "$OUT/tmp"
DEPS=$(cat target/classpath.txt)
# A snapshot, so compiling the tree while the runs go cannot reach them.
[ -f "$OUT/classes/ham/citybuildersim/Game.class" ] || cp -r target/classes/. "$OUT/classes/"
CP="$OUT/classes$SEP$DEPS"
javac -nowarn -d "$OUT/today" -cp "$CP" "$HERE/WageProbe.java"
for f in LabourMarket Migration; do cp "src/main/java/ham/citybuildersim/$f.java" "$OUT/src/ham/citybuildersim/"; done
for p in real-minimum migration-target; do
    ( cd "$OUT/src" && sed 's#src/main/java/##g' "$OLDPWD/$HERE/$p.patch" | patch -s -p1 )
done
javac -nowarn -d "$OUT/proto" -cp "$CP" "$OUT/src/ham/citybuildersim/LabourMarket.java" \
    "$OUT/src/ham/citybuildersim/Migration.java" "$HERE/WageProbe.java"

one() {
    IFS=: read -r name kind floor at ripple extra <<< "$1"
    local seed=$2 dir=$kind
    local props="-Dwages.floor=$floor -Dwages.floorAt=$at"
    [ "$kind" = real ] && { dir=proto; props="$props -Dwages.real=true -Dwages.ripple=${ripple:-0}"; }
    for kv in ${extra//,/ }; do props="$props -Dwages.$kv"; done
    java -Xmx2g "-Djava.io.tmpdir=$OUT/tmp" $props -cp "$OUT/$dir$SEP$CP" \
        ham.citybuildersim.WageProbe "$seed" "$MONTHS" "$OUT/$name-s$seed.csv" > "$OUT/$name-s$seed.log" 2>&1
    tail -1 "$OUT/$name-s$seed.log" | sed "s/^/$name /"
}
export -f one; export OUT MONTHS SEP CP
for v in "$@"; do for s in $SEEDS; do echo "$v $s"; done; done \
    | xargs -P "$JOBS" -L 1 bash -c 'one "$0" "$1"'
