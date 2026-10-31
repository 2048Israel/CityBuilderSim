#!/bin/sh
# run.sh <label> <main|075> <months> [st1.off keys, comma list] [st1.from] : ScaleCheck section 6's town, one month at a time
# (ST1Probe) -> $SCRATCH/st1-out/<label>.csv, .tsv and .log; prints the wall time and the probe's DONE line.
#
# The trees (set up once; ST1_SCRATCH overrides where they are):
#   git worktree add --detach $SCRATCH/st1-main origin/main && (cd $SCRATCH/st1-main && git apply <this folder>/switches.patch)
#   git worktree add --detach $SCRATCH/st1-075 0.8.75
#   in each: mvn -q -B compile ; mvn -q -B dependency:build-classpath -Dmdep.outputFile=target/classpath.txt   (one Maven at a time)
#            mkdir probe-classes ; javac -encoding UTF-8 -d probe-classes -cp "target/classes;$(cat target/classpath.txt)" <this folder>/ST1Probe.java <this folder>/ST1Reference.java
# The switches are main's alone (0.8.75 has no ST1 class; its st1.off is ignored). One JVM at a time: the PC is memory-bound.
# Examples:  run.sh base main 1200   run.sh nopolice main 1200 police   run.sh nopolice-from600 main 1200 police 600
#            run.sh old 075 1200
# Fidelity: ST1_CLASS=ST1Reference sh run.sh ref main 200 builds the town by OrderSearchCheck.playtestCity() itself; the log's `ref` line
# (population, capacity, homes, cash) must equal the probe's m200 row to the last digit.
SCRATCH=${ST1_SCRATCH:-/c/Users/Jerus/AppData/Local/Temp/claude/C--Users-Jerus-Documents-NetBeansProjects-CityBuilderSim--claude-worktrees-em2-d61787/4a81d472-4582-4010-8acc-ae1ecf1b7b99/scratchpad}
DATA=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data
L=$1; T=$2; M=${3:-1200}; OFF=$4; FROM=$5
[ -n "$L" ] && [ -n "$T" ] || { echo "usage: run.sh <label> <main|075> <months> [st1.off keys] [st1.from]"; exit 2; }
TREE="$SCRATCH/st1-$T"
[ -d "$TREE/probe-classes" ] || { echo "no $TREE/probe-classes: build the tree first (see this file's header)"; exit 2; }
mkdir -p "$SCRATCH/st1-out" "$SCRATCH/tmp"
W=$(cygpath -m "$TREE")
CP="$W/probe-classes;$W/target/classes;$(cat "$TREE/target/classpath.txt")"
OPTS=""
[ -n "$OFF" ] && OPTS="-Dst1.off=$OFF"
[ -n "$FROM" ] && OPTS="$OPTS -Dst1.from=$FROM"
OUT=$(cygpath -m "$SCRATCH/st1-out")
START=$(date +%s)
MSYS2_ARG_CONV_EXCL='*' java -Xmx2g -Dcbs.earth=$DATA/earth-2.bin -Dcbs.world=$DATA/world-1.bin "-Djava.io.tmpdir=$(cygpath -m "$SCRATCH/tmp")" $OPTS \
  -cp "$CP" ham.citybuildersim.${ST1_CLASS:-ST1Probe} "$M" "$OUT/$L" > "$SCRATCH/st1-out/$L.log" 2>&1
CODE=$?
echo "$L ($T, to m$M${OFF:+, off=$OFF}${FROM:+ from $FROM}): exit $CODE, $(( $(date +%s) - START ))s wall; $(grep -E '^DONE' "$SCRATCH/st1-out/$L.log" | tail -1)"
