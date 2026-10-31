#!/bin/sh
# trap.sh csv... : the idle-crews trap's months in a run.sh CSV (from m300): the builders idle at the core crew (offered <= 0.25),
# their laid-off posts at least half the unemployed, and unemployment at least 10%. Its months, its longest spell (from-to),
# the population then, the month the run passed 40,000 and its end.
printf "%-28s %6s %6s %6s %6s %8s %8s %6s %8s\n" run months spell from to pop0 pop1 m40k end
for c in "$@"; do
  awk -F, -v n="$(basename "$c" .csv)" '
    NR==1 { for (i = 1; i <= NF; i++) col[$i] = i; next }
    { m = $1 + 0; p = $3 + 0; pop[m] = p; last = p; if (!k && p >= 40000) k = m
      u = $(col["unemployment"]) + 0; w = $(col["withheld"]) + 0; j = $(col["unemp"]) + 0; o = $(col["offered"]) + 0
      if (m >= 300 && o <= .25 + 1e-9 && w > 0 && w >= .5 * j && u >= .10) { t++; if (!run) st = m; run++; if (run > best) { best = run; bs = st; be = m } } else run = 0 }
    END { printf "%-28s %6d %6d %6d %6d %8d %8d %6d %8d\n", n, t, best, bs, be, pop[bs], pop[be], k, last }' "$c"
done
