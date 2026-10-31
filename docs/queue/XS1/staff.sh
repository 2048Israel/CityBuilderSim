#!/bin/sh
# staff.sh tsv from to step : withheld, unemployed, workforce, jobs, the gate's spare by band (supply - posts - withheld), Construction's and the gate-held sectors' count
awk -F'\t' -v a="$2" -v b="$3" -v s="$4" '
$1>=a && $1<=b && $2=="STAFF" && ($1-a)%s==0 {
  line=$3; split(line, w, " ");
  wh=w[2]; un=w[4]; wf=w[6]; jb=w[8];
  wn=0; wd=0;
  if (match(line, /withheld-NO_DIPLOMA [0-9]+/)) { wn=substr(line, RSTART+20, RLENGTH-20) }
  if (match(line, /withheld-DIPLOMA [0-9]+/)) { wd=substr(line, RSTART+17, RLENGTH-17) }
  sn=""; sd="";
  if (match(line, /NONE own [0-9]+ posts [0-9]+ supply [0-9]+ spare -?[0-9]+/)) { split(substr(line,RSTART,RLENGTH), q, " "); sn=q[8] }
  if (match(line, /DIPLOMA own [0-9]+ posts [0-9]+ supply [0-9]+ spare -?[0-9]+/)) { split(substr(line,RSTART,RLENGTH), q, " "); sd=q[8] }
  printf "%5d withheld %5d unemployed %5d workforce %6d jobs %6d | gate spare NONE %6d DIPLOMA %6d | held-by-gate %2d constr: %s\n", $1, wh, un, wf, jb, sn-wn, sd-wd, held[$1]+0, con[$1]
}
$2!="STAFF" && $2!="ADVICE" && $3 ~ /could staff/ { held[$1]++ }
$2=="Construction" { con[$1]=substr($3,1,50) }
' "$1"
