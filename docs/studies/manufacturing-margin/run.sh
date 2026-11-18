#!/usr/bin/env bash
# The manufacturing margin study's runs (docs/studies/manufacturing-margin.md), after LS1's and CU1's run.sh.
#
#   docs/studies/manufacturing-margin/run.sh OUT NAME [LongPlaytest's arguments...]
#
# Plays LongPlaytest itself with MarginProbe.month(g) called after every month: one line added to a
# copy of LongPlaytest.java in OUT, after g.simulateMonths(1) in run(); the tree is not touched.
# MONTHS (default 2400) is -Dplaytest.months, SEED (default 0) -Dplaytest.seed; CBS_EARTH and
# CBS_WORLD name the baked data files when the checkout has none. EXTRA passes more JVM options.
# PATCH names a folder of patched model sources compiled over the snapshot, and a buildings.json
# laid ahead of the snapshot's (a prototype: proto.py writes one).
# Writes OUT/NAME.csv and OUT/NAME.log. Run from the checkout's root after batch-check.sh --fast.
#
# The city is founded in a TempDirs folder by LongPlaytest, never the save folder.
set -euo pipefail
OUT=$1; NAME=$2; shift 2
MONTHS=${MONTHS:-2400}; SEED=${SEED:-0}
SEP=":"; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";"; mkdir -p "$OUT"; OUT=$(cygpath -m "$OUT");; esac
HERE=docs/studies/manufacturing-margin
mkdir -p "$OUT/classes" "$OUT/probe" "$OUT/src" "$OUT/tmp"
DEPS=$(cat target/classpath.txt)
# A snapshot, so compiling the tree while the runs go cannot reach them.
[ -f "$OUT/classes/ham/citybuildersim/Game.class" ] || cp -r target/classes/. "$OUT/classes/"
CP="$OUT/classes$SEP$DEPS"
if [ ! -f "$OUT/probe/ham/citybuildersim/MarginProbe.class" ]; then
    SRC=src/main/java/ham/citybuildersim/LongPlaytest.java
    [ "$(grep -c '^            g.simulateMonths(1);$' "$SRC")" = 1 ] || { echo "the hook line moved"; exit 2; }
    sed 's/^            g.simulateMonths(1);$/&\n            MarginProbe.month(g);/' "$SRC" > "$OUT/src/LongPlaytest.java"
    PATCHED=""
    [ -n "${PATCH:-}" ] && PATCHED=$(ls "$PATCH"/*.java)
    javac -nowarn -encoding UTF-8 -d "$OUT/probe" -cp "$CP" "$OUT/src/LongPlaytest.java" "$HERE/MarginProbe.java" $PATCHED
    # ...and a patched buildings.json ahead of the snapshot's on the classpath (BuildingCatalog reads the first).
    [ -n "${PATCH:-}" ] && [ -f "$PATCH/buildings.json" ] && cp "$PATCH/buildings.json" "$OUT/probe/buildings.json"
fi
DATA=""
[ -n "${CBS_EARTH:-}" ] && DATA="$DATA -Dcbs.earth=$CBS_EARTH"
[ -n "${CBS_WORLD:-}" ] && DATA="$DATA -Dcbs.world=$CBS_WORLD"
rc=0
java -Xmx3g "-Djava.io.tmpdir=$OUT/tmp" $DATA ${EXTRA:-} -Dplaytest.months="$MONTHS" -Dplaytest.seed="$SEED" \
    -Dmargin.out="$OUT/$NAME.csv" -cp "$OUT/probe$SEP$CP" ham.citybuildersim.LongPlaytest "$@" \
    > "$OUT/$NAME.log" 2>&1 || rc=$?
echo "$NAME exit $rc"
