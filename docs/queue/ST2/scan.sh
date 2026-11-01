#!/bin/sh
# scan.sh <label> <from> <to> <every> [more months] : ST2Scan on this worktree's classes, at the factor
# (ST2_LEVEL=1 for today's prices) -> $S/out/<label>.txt (one line a month) and <label>.csv (the trunk's figures).
# Build first: javac -encoding UTF-8 -d $S/probe-classes -cp "target/classes;$(cat target/classpath.txt)" docs/queue/ST2/ST2Scan.java
S=${ST2_SCRATCH:?set ST2_SCRATCH to a scratch folder holding probe-classes}
DATA=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data
L=$1; shift
LEVEL=""; [ -n "$ST2_LEVEL" ] && LEVEL="-Dcbs.priceLevel=$ST2_LEVEL"
mkdir -p "$S/out" "$S/tmp"
MSYS2_ARG_CONV_EXCL='*' java -Xmx2g -Dcbs.earth=$DATA/earth-2.bin -Dcbs.world=$DATA/world-1.bin $LEVEL $ST2_OPTS \
  "-Djava.io.tmpdir=$S/tmp" "-Dst2.csv=$S/out/$L.csv" "-Dst2.dir=$S/out" \
  -cp "$S/probe-classes;target/classes;$(cat target/classpath.txt)" ham.citybuildersim.ST2Scan "$@" > "$S/out/$L.txt" 2>&1
echo "exit $? - $S/out/$L.txt"
