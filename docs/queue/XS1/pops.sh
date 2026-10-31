#!/bin/sh
# pops.sh [-r ref.csv] csv... : population at checkpoints, month reaching 40k, final; with -r, the first month a run's row differs from ref's
REF=""; if [ "$1" = "-r" ]; then REF=$2; shift 2; fi
printf "%-40s %7s %7s %7s %7s %7s %7s %7s %7s %7s %6s %6s\n" run m600 m800 m900 m1000 m1200 m1500 m1800 m2100 end m40k fork
for c in "$@"; do
  n="$(basename "$c" .csv)"
  d=""; if [ -n "$REF" ]; then d=$(cmp "$REF" "$c" 2>/dev/null | grep -o 'line [0-9]*' | grep -o '[0-9]*'); [ -n "$d" ] && d=$((d - 1)); fi
  awk -F, -v n="$n" -v d="$d" 'NR>1{m=$1+0; p=$3+0; if(m==600||m==800||m==900||m==1000||m==1200||m==1500||m==1800||m==2100) v[m]=p; last=p; if(!k && p>=40000) k=m} END{printf "%-40s %7d %7d %7d %7d %7d %7d %7d %7d %7d %6d %6s\n", n, v[600],v[800],v[900],v[1000],v[1200],v[1500],v[1800],v[2100],last,k,d}' "$c"
done
