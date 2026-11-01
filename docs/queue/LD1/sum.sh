#!/bin/sh
# sum.sh <tsv...> : holds by class and pay; the short holds' months with the city taking migrants (gap>0)
awk -F'\t' '
$2=="MONTH"{ split($8,a," "); gap[FILENAME" "$1]=a[2]; split($4,u," "); split($5,w," "); ur[FILENAME" "$1]=(w[2]>0?u[2]/w[2]:0) }
$2=="HOLD"{ h[FILENAME" "$1]=h[FILENAME" "$1]"|"$3"\t"$8"\t"$9 }
END{
  for (k in h) { n=split(h[k],xs,"|"); for(i=2;i<=n;i++){ split(xs[i],f,"\t"); c[f[2]" "f[3]]++; if (f[2]=="short" && f[3]=="pays") { sp++; if (gap[k]>0) spg++; if (ur[k]<0.10) spl++ } tot++ } }
  printf "holds %d\n", tot; for (x in c) printf "  %-12s %d (%.0f%%)\n", x, c[x], 100*c[x]/tot;
  printf "  short+pays in months with gap>0: %d of %d; with unemployment <10%%: %d\n", spg, sp, spl
}' "$@"
