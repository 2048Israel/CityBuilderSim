#!/bin/sh
# wage.sh <tsv...> : at the months of short+pays holds, the COLLEGE and UNIVERSITY band multiples (premium) and the arrivals' college+university share
awk -F'\t' '
$2=="HOLD" && $8=="short" && $9=="pays"{ sp[FILENAME" "$1]=1 }
$2=="MONTH"{ k=FILENAME" "$1; for(i=1;i<=NF;i++){ if ($i ~ /^mult /){ split(substr($i,6),m," "); split(m[3],c,"/"); split(m[4],u,"/"); cm[k]=c[2]; um[k]=u[2] } if ($i ~ /^mix /){ split(substr($i,5),x," "); tot=x[1]+x[2]+x[3]+x[4]; sk[k]=(tot>0?(x[3]+x[4])/tot:0); ar[k]=tot } } }
END{ for (k in sp) { n++; C=cm[k]; U=um[k]; if (C<1.02) cf++; else if (C<1.2) cm2++; else ch++; if (U<1.02) uf++; else if (U<1.2) um2++; else uh++; s+=sk[k]; a+=ar[k] }
 printf "short+pays months %d | COLLEGE premium <1.02: %d, 1.02-1.2: %d, >=1.2: %d | UNIVERSITY <1.02: %d, 1.02-1.2: %d, >=1.2: %d | skilled share of arrivals %.3f, arrivals/mo %.0f\n", n, cf, cm2, ch, uf, um2, uh, s/n, a/n }' "$@"
