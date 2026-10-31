#!/bin/sh
# matrix.sh <seed> <from> <keys...>: one run per key (a key may be a,b for several at once), armed from <from>, two at a time.
S=$(dirname "$0"); SEED=$1; F=$2; shift 2
run1() { sh "$S/run.sh" "s$SEED-f$F-$(echo "$1" | tr ',' '+')" sw "$SEED" "-Dxs1.off=$1" "-Dxs1.from=$F"; }
n=0; for k in "$@"; do run1 "$k" & n=$((n+1)); [ $((n % 2)) -eq 0 ] && wait; done; wait
