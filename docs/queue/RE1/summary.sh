#!/bin/sh
# summary.sh <label...>: population at m1100/1300/1500/1800/2400 and, m1000-1800, the months of the down-payment hold, the landlords' loss months and the staffing holds.
S=$(dirname "$0")
printf "%-18s %7s %7s %7s %7s %7s | %5s %5s %6s\n" run m1100 m1300 m1500 m1800 end downpay loss staff
for L in "$@"; do
  c="$S/out/$L.csv"; t="$S/out/$L.tsv"; [ -f "$c" ] || continue
  p=$(awk -F, 'NR>1 && ($1==1100||$1==1300||$1==1500||$1==1800){printf "%7d ", $3} END{printf "%7d", $3}' "$c")
  d=$(awk -F'\t' '$1>=1000 && $1<=1800 && $2=="Real Estate" && $3 ~ /down payment/' "$t" 2>/dev/null | wc -l)
  l=$(awk -F'\t' '$1>=1000 && $1<=1800 && $2=="BOOKS" {for(i=1;i<NF;i++){}; split($3,a," "); for(i=1;i<=NF;i++){}; for(j=1;j<length(a);j++) if(a[j]=="pre") v=a[j+1]; if (v+0<0) n++} END{print n+0}' "$t" 2>/dev/null)
  s=$(awk -F'\t' '$1>=1000 && $1<=1800 && $3 ~ /could staff/' "$t" 2>/dev/null | wc -l)
  printf "%-18s %s | %5s %5s %6s\n" "$L" "$p" "$d" "$l" "$s"
done
