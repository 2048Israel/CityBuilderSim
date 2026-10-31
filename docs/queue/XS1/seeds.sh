#!/bin/sh
# seeds.sh <prefix> <classes> [java opts...] : AUTO2 seeds 0-15, two at a time, as out/<prefix>-<seed>.csv
S=$(dirname "$0"); P=$1; C=$2; shift 2
n=0; for s in 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15; do sh "$S/run.sh" "$P-$s" "$C" "$s" "$@" & n=$((n+1)); [ $((n % 2)) -eq 0 ] && wait; done; wait
