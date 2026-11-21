#!/usr/bin/env bash
# The luxury-budget study's runs (docs/studies/luxury-budget.md), LS1's run.sh with LQ1's probe.
#
#   docs/studies/luxury-budget/run.sh OUT NAME [LongPlaytest's arguments...]
#
# Plays LongPlaytest itself (the default playtest, or --earth <city>) with
# LuxuryProbe called after every month, so the city measured is the one the
# gate plays. The probe is one line added to a copy of LongPlaytest.java in
# OUT, after g.simulateMonths(1) in run(); the tree is not touched. MONTHS
# (default 4000) is -Dplaytest.months, SEED (default 0) -Dplaytest.seed;
# CBS_EARTH and CBS_WORLD name the baked data files when the checkout has none.
# PATCH names a unified diff of the game's sources (a prototype, proto-*.patch):
# the files it touches are copied out, patched, compiled (a resource copied)
# and put first on the class path, so the tree's own classes are never changed; JOPTS adds -D flags.
# Writes OUT/NAME-city.csv,
# OUT/NAME-cells.csv and OUT/NAME.log. Run from the checkout's root after
# batch-check.sh (target/classes and target/classpath.txt).
#
# The city is founded in a TempDirs folder by LongPlaytest, never the save folder.
set -euo pipefail
OUT=$1; NAME=$2; shift 2
MONTHS=${MONTHS:-4000}; SEED=${SEED:-0}
SEP=":"; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";"; mkdir -p "$OUT"; OUT=$(cygpath -m "$OUT");; esac
HERE=docs/studies/luxury-budget
mkdir -p "$OUT/classes" "$OUT/probe" "$OUT/src" "$OUT/tmp/$NAME"
DEPS=$(cat target/classpath.txt)
# A snapshot, so compiling the tree while the runs go cannot reach them.
[ -f "$OUT/classes/ham/citybuildersim/Game.class" ] || cp -r target/classes/. "$OUT/classes/"
CP="$OUT/classes$SEP$DEPS"
if [ -n "${PATCH:-}" ]; then
    if [ ! -d "$OUT/proto" ]; then
        rm -rf "$OUT/proto-src"; mkdir -p "$OUT/proto-src"
        for f in $(sed -n 's|^+++ b/||p' "$PATCH"); do mkdir -p "$OUT/proto-src/$(dirname "$f")"; cp "$f" "$OUT/proto-src/$f"; done
        patch -s -p1 -d "$OUT/proto-src" < "$PATCH"
        mkdir -p "$OUT/proto"
        J=$(find "$OUT/proto-src" -name '*.java'); [ -z "$J" ] || javac -nowarn -d "$OUT/proto" -cp "$CP" $J
        # ...and a patched resource (buildings.json) goes to the class path's root, ahead of the snapshot's.
        [ ! -d "$OUT/proto-src/src/main/resources" ] || cp -r "$OUT/proto-src/src/main/resources/." "$OUT/proto/"
    fi
    CP="$OUT/proto$SEP$CP"
fi
if [ ! -f "$OUT/probe/ham/citybuildersim/LuxuryProbe.class" ]; then
    SRC=src/main/java/ham/citybuildersim/LongPlaytest.java
    [ "$(grep -c '^            g.simulateMonths(1);$' "$SRC")" = 1 ] || { echo "the hook line moved"; exit 2; }
    sed 's/^            g.simulateMonths(1);$/&\n            LuxuryProbe.month(g);/' "$SRC" > "$OUT/src/LongPlaytest.java"
    javac -nowarn -d "$OUT/probe" -cp "$CP" "$OUT/src/LongPlaytest.java" "$HERE/LuxuryProbe.java"
fi
DATA=""
[ -n "${CBS_EARTH:-}" ] && DATA="$DATA -Dcbs.earth=$CBS_EARTH"
[ -n "${CBS_WORLD:-}" ] && DATA="$DATA -Dcbs.world=$CBS_WORLD"
rc=0
java -Xmx3g "-Djava.io.tmpdir=$OUT/tmp/$NAME" $DATA ${JOPTS:-} -Dplaytest.months="$MONTHS" -Dplaytest.seed="$SEED" \
    -Dluxury.out="$OUT/$NAME" -cp "$OUT/probe$SEP$CP" ham.citybuildersim.LongPlaytest "$@" \
    > "$OUT/$NAME.log" 2>&1 || rc=$?
echo "$NAME exit $rc"
