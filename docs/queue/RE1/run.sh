#!/bin/sh
# run.sh <label> <classes-folder-name> [months] [extra java opts...] : AUTO2 seed 4, one CSV and log in out/
S=$(dirname "$0"); L=$1; C=$2; M=${3:-2400}; shift 3 2>/dev/null || shift $#
D="$S/driver"; [ -d "$S/driver-$C" ] && D="$S/driver-$C"
[ -n "$PROBE" ] && D="$S/pdriver-$C"
DEPS=$(cat "$S/deps.txt")
java -Xmx2g -Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin "-Djava.io.tmpdir=$S/tmp" -Densemble.months=$M "$@" -cp "$D;$S/$C;$DEPS" \
  ham.citybuildersim.Ensemble AUTO2 4 "$S/out/$L.csv" > "$S/out/$L.log" 2>&1
echo "$L: $(grep -E '^DONE' "$S/out/$L.log" | tail -1)"
