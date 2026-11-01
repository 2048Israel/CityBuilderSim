#!/bin/sh
# med.sh <label...> : population medians [min-max] at m300/600/900/1200/2400, the 40k month median, the Machine Works' longest young hold median, holds
D=${LD1_DIR:-$(cd "$(dirname "$0")/../../.." && pwd)/target/ld1}/out
for L in "$@"; do
  printf "%-5s" $L
  for M in 300 600 900 1200 2400; do
    cat $D/$L-*.tsv | awk -F'\t' -v M=$M '$2=="MONTH" && $1==M {split($3,p," "); print p[2]}' | sort -n | awk -v M=$M '{v[NR]=$1} END{printf " m%s %6.0f [%6.0f-%6.0f]", M, (v[8]+v[9])/2, v[1], v[NR]}'
  done
  for f in $D/$L-*.tsv; do awk -F'\t' '$2=="MONTH"{split($3,p," "); if(p[2]>=40000){print $1; exit}}' $f; done | sort -n | awk '{v[NR]=$1} END{printf " | 40k %.0f [%d-%d]", (v[8]+v[9])/2, v[1], v[NR]}'
  for f in $D/$L-*.tsv; do awk -F'\t' '$2=="HOLD" && $4=="Machine Works" && $8=="short" && $1>=20 && $1<=800 { if ($1!=pv+1) { if (pv-st+1>lg) lg=pv-st+1; st=$1 } pv=$1 } END{ if (pv-st+1>lg) lg=pv-st+1; print lg+0 }' $f; done | sort -n | awk '{v[NR]=$1} END{printf " | MW %.0f [%d-%d]", (v[8]+v[9])/2, v[1], v[NR]}'
  cat $D/$L-*.tsv | awk -F'\t' '$2=="HOLD"{n++; if($8=="short"&&$9=="pays")s++} END{printf " | holds %d, short+pays %d\n", n, s}'
done
