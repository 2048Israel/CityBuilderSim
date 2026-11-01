#!/bin/sh
# summary.sh <label>... : per run (st1-out/<label>.csv): population at m600/731/900/1075/1200/1500/2000/end, household places at
# m1200 and the end, treasury cash at m900, the first month from m700 the treasury is under nothing, the month it first passes 8,000
# people, businesses' cash at m600, and the police stations standing at the end.
O=${ST1_SCRATCH:-/c/Users/Jerus/AppData/Local/Temp/claude/C--Users-Jerus-Documents-NetBeansProjects-CityBuilderSim--claude-worktrees-em2-d61787/4a81d472-4582-4010-8acc-ae1ecf1b7b99/scratchpad}/st1-out
printf "%-12s %6s %6s %6s %6s %6s %6s %6s %6s | %6s %6s | %8s %5s %5s %7s %3s\n" run m600 m731 m900 m1075 m1200 m1500 m2000 end cap1200 capend cash900 dry m8000 biz600 pol
for L in "$@"; do [ -f "$O/$L.csv" ] || { echo "$L: no csv"; continue; }
  awk -F, -v n="$L" 'NR==1{for(i=1;i<=NF;i++)c[$i]=i; next}
    { m=$1+0; p=$(c["population"])+0; P[m]=p; C[m]=$(c["capacity"])+0; last=m
      if (m==900) cash9=$(c["cash"]); if (m==600) biz=$(c["biz_cash"]); pol=$(c["n_Police_Station"])
      if (m>=700 && !dry && $(c["cash"])<0) dry=m; if (!k && p>=8000) k=m }
    END { printf "%-12s %6d %6d %6d %6d %6d %6d %6d %6d | %6d %6d | %8.0f %5s %5s %7.0f %3d\n", n, P[600],P[731],P[900],P[1075],P[1200],P[1500],P[2000],P[last], C[1200],C[last], cash9, dry?dry:"-", k?k:"-", biz, pol }' "$O/$L.csv"
done
