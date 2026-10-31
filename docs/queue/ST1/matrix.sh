#!/bin/sh
# matrix.sh <months> <label=keys[@from]>... : one run.sh run per spec on main's patched tree, two at a time (the PC is memory-bound),
# then summary.sh over them. A spec "base=" runs no switch; "old=075" runs the 0.8.75 tree. E.g. matrix.sh 2500 base= old=075 coupon=coupon gate900=gate@900
S=$(dirname "$0"); M=$1; shift
one() { L=${1%%=*}; K=${1#*=}; F=""; case "$K" in *@*) F=${K#*@}; K=${K%@*};; esac
  if [ "$K" = 075 ]; then sh "$S/run.sh" "$L" 075 "$M"; else sh "$S/run.sh" "$L" main "$M" "$K" "$F"; fi; }
n=0; LS=""; for spec in "$@"; do one "$spec" & n=$((n+1)); LS="$LS ${spec%%=*}"; [ $((n % 2)) -eq 0 ] && wait; done; wait
sh "$S/summary.sh" $LS
