#!/usr/bin/env bash
# The labour-share study's runs (docs/studies/spec-labour-share.md).
#
#   docs/studies/labour-share/run.sh OUT NAME [LongPlaytest's arguments...]
#
# Plays LongPlaytest itself (the default playtest, or --earth <city>) with
# LabourProbe called after every month, so the city measured is the one the
# gate plays. The probe is one line added to a copy of LongPlaytest.java in
# OUT, after g.simulateMonths(1) in run(); the tree is not touched. MONTHS
# (default 4000) is -Dplaytest.months, SEED (default 0) -Dplaytest.seed;
# CBS_EARTH and CBS_WORLD name the baked data files when the checkout has none.
# Writes OUT/NAME.csv and OUT/NAME.log. Run from the checkout's root after
# batch-check.sh (target/classes and target/classpath.txt).
#
# The city is founded in a TempDirs folder by LongPlaytest, never the save folder.
set -euo pipefail
OUT=$1; NAME=$2; shift 2
MONTHS=${MONTHS:-4000}; SEED=${SEED:-0}
SEP=":"; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";"; mkdir -p "$OUT"; OUT=$(cygpath -m "$OUT");; esac
HERE=docs/studies/labour-share
mkdir -p "$OUT/classes" "$OUT/probe" "$OUT/src" "$OUT/tmp"
DEPS=$(cat target/classpath.txt)
# A snapshot, so compiling the tree while the runs go cannot reach them.
[ -f "$OUT/classes/ham/citybuildersim/Game.class" ] || cp -r target/classes/. "$OUT/classes/"
CP="$OUT/classes$SEP$DEPS"
if [ ! -f "$OUT/probe/ham/citybuildersim/LabourProbe.class" ]; then
    SRC=src/main/java/ham/citybuildersim/LongPlaytest.java
    [ "$(grep -c '^            g.simulateMonths(1);$' "$SRC")" = 1 ] || { echo "the hook line moved"; exit 2; }
    sed 's/^            g.simulateMonths(1);$/&\n            LabourProbe.month(g);/' "$SRC" > "$OUT/src/LongPlaytest.java"
    javac -nowarn -d "$OUT/probe" -cp "$CP" "$OUT/src/LongPlaytest.java" "$HERE/LabourProbe.java"
fi
DATA=""
[ -n "${CBS_EARTH:-}" ] && DATA="$DATA -Dcbs.earth=$CBS_EARTH"
[ -n "${CBS_WORLD:-}" ] && DATA="$DATA -Dcbs.world=$CBS_WORLD"
rc=0
java -Xmx3g "-Djava.io.tmpdir=$OUT/tmp" $DATA -Dplaytest.months="$MONTHS" -Dplaytest.seed="$SEED" \
    -Dlabour.out="$OUT/$NAME.csv" -cp "$OUT/probe$SEP$CP" ham.citybuildersim.LongPlaytest "$@" \
    > "$OUT/$NAME.log" 2>&1 || rc=$?
echo "$NAME exit $rc"
