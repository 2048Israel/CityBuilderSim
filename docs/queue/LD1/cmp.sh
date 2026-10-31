#!/bin/sh
# cmp.sh <labelA> <labelB> : per seed, population at m300/600/900/1200/end, first month past 40,000, the Machine Works' longest hold in m20-800
D=${LD1_DIR:-$(cd "$(dirname "$0")/../../.." && pwd)/target/ld1}/out
one() {
  awk -F'\t' -v W="$2" '
    $2=="MONTH"{ split($3,p," "); pop=p[2]; m=$1; if(m==300)a=pop; if(m==600)b=pop; if(m==900)c=pop; if(m==1200)d=pop; last=pop; if(pop>=40000 && !f) f=m }
    $2=="HOLD" && $4==W && $8=="short" && $1>=20 && $1<=800 { if ($1!=pv+1) { if (pv-st+1>lg) lg=pv-st+1; st=$1 } pv=$1 }
    END{ if (pv-st+1>lg) lg=pv-st+1; printf "%6.0f %6.0f %6.0f %6.0f %7.0f %5s %4d", a, b, c, d, last, (f?f:"-"), lg }' "$1"
}
printf "seed | %-46s | %-46s\n" "$1: m300 m600 m900 m1200 end 40k MW" "$2"
for s in $(seq 0 15); do
  printf "%4s | %s | %s\n" $s "$(one $D/$1-$s.tsv 'Machine Works')" "$(one $D/$2-$s.tsv 'Machine Works')"
done
