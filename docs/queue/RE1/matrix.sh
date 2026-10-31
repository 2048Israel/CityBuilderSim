#!/bin/sh
# matrix.sh <from> <months> <keys...>: one run per key (armed from <from>), three at a time, then the summary.
S=$(dirname "$0"); F=$1; M=$2; shift 2
run1() { PROBE=1 sh "$S/run.sh" "f$F-$1" hd1sw "$M" "-Dre1.off=$1" "-Dre1.from=$F" "-Dprobe.file=$S/out/f$F-$1.tsv" -Dprobe.from=900; }
n=0; for k in "$@"; do run1 "$k" & n=$((n+1)); [ $((n % 3)) -eq 0 ] && wait; done; wait
