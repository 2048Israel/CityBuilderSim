#!/bin/sh
# setup.sh <name> <main|ld1> : snapshot this tree's target/classes as $LD1_DIR/<name> and build the probe driver
# $LD1_DIR/driver-<name>: tools/ensemble/Ensemble.java with one line added (LD1Hook.month(g) after each month's row) and
# the hook from main/ (any tree) or ld1/ (a tree with WantedPosts). $LD1_DIR defaults to target/ld1; it needs
# classpath.txt (batch-check.sh writes target/classpath.txt; copy it there).
HERE=$(cd "$(dirname "$0")" && pwd -W)
W=$(cd "$HERE/../../.." && pwd -W)
S=${LD1_DIR:-$W/target/ld1}
N=$1; H=$2
mkdir -p "$S/src-$N/ham/citybuildersim"
sed 's/^\(        write(columns(g, ph, Adapter.findingCount() - flagsBefore, names.toString()));\)$/\1\n        LD1Hook.month(g);/' \
    "$W/tools/ensemble/Ensemble.java" > "$S/src-$N/ham/citybuildersim/Ensemble.java"
cp "$HERE/$H/LD1Hook.java" "$S/src-$N/ham/citybuildersim/"
rm -rf "$S/$N" "$S/driver-$N"; cp -r "$W/target/classes" "$S/$N"
DEPS=$(tr -d '\r\n' < "$S/classpath.txt")
javac -nowarn -d "$S/driver-$N" -cp "$S/$N;$DEPS" "$S/src-$N/ham/citybuildersim/Ensemble.java" "$S/src-$N/ham/citybuildersim/LD1Hook.java" && echo "setup $N ok"
