#!/bin/sh
# run.sh <classes-name> (setup.sh) <label> <seed...>, extra java options in $EXTRA (e.g. -Dld1.var=short, variants.patch)
# was: run.sh <classes-name> <label> <seed...> : AUTO2 per seed, two at a time; out/<label>-<seed>.{csv,log,tsv}
HERE=$(cd "$(dirname "$0")" && pwd -W)
S=${LD1_DIR:-$(cd "$HERE/../../.." && pwd -W)/target/ld1}
C=$1; L=$2; shift 2
DEPS=$(tr -d '\r\n' < "$S/classpath.txt")
mkdir -p "$S/out" "$S/tmp"
one() {
  java -Xmx2g -Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin \
    "-Djava.io.tmpdir=$S/tmp" "-Dld1.file=$S/out/$L-$1.tsv" $EXTRA -cp "$S/driver-$C;$S/$C;$DEPS" \
    ham.citybuildersim.Ensemble AUTO2 "$1" "$S/out/$L-$1.csv" > "$S/out/$L-$1.log" 2>&1
  echo "$L-$1: $(grep -E '^DONE' "$S/out/$L-$1.log" | tail -1 | cut -c1-120)"
}
set -- "$@"
while [ $# -gt 0 ]; do
  one "$1" & P1=$!
  if [ $# -gt 1 ]; then one "$2" & P2=$!; wait $P2; shift; fi
  wait $P1; shift
done
echo "all done"
