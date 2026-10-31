#!/bin/sh
# setup.sh <classes-name> [tree] : snapshot <tree>/target/classes (default: this repository's) as $XS1_DIR/<name> and compile the
# ensemble driver against it into $XS1_DIR/driver-<name>; the probe driver (probe.patch applied to a copy of tools/ensemble/Ensemble.java)
# is compiled the same way by hand. $XS1_DIR is a scratch folder outside the tree (default: this folder's ../../../target/xs1).
# Before the first run, copy deps.txt there: the ensemble's own (target/ensemble/<label>/deps.txt), the jars' classpath.
HERE=$(cd "$(dirname "$0")" && pwd -W)
W=${2:-$(cd "$HERE/../../.." && pwd -W)}
S=${XS1_DIR:-$W/target/xs1}
C=$1
mkdir -p "$S/out" "$S/tmp"
rm -rf "$S/$C" "$S/driver-$C"
cp -r "$W/target/classes" "$S/$C"
DEPS=$(tr -d '\r\n' < "$S/deps.txt")
javac -nowarn -d "$S/driver-$C" -cp "$S/$C;$DEPS" "$W/tools/ensemble/Ensemble.java" && echo "setup $C ok"
