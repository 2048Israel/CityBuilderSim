#!/bin/sh
# med.sh prefix... : medians [least-most] of population at m300 m600 m900 m1200, end, and the month past 40,000, over seeds 0-15
for p in "$@"; do
  for s in 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15; do
    awk -F, 'NR>1{m=$1+0; p=$3+0; v[m]=p; last=p; if(!k && p>=40000) k=m} END{printf "%d %d %d %d %d %d\n", v[300], v[600], v[900], v[1200], last, k?k:9999}' ${XS1_DIR:-target/xs1}/out/$p-$s.csv
  done | awk -v n="$p" '{for(i=1;i<=6;i++) a[i,NR]=$i} END{printf "%-6s", n; for(i=1;i<=6;i++){ for(j=1;j<=NR;j++) x[j]=a[i,j]; m=NR; for(j=1;j<=m;j++) for(k=j+1;k<=m;k++) if(x[k]<x[j]){t=x[j];x[j]=x[k];x[k]=t}; printf " %8.0f [%d-%d]", (x[8]+x[9])/2, x[1], x[16]}; print ""}'
done
