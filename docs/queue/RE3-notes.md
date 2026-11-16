# RE3 - notes

## Main's table (0.9.126, fb378433), before any code: stopped for the orchestrator's call

RE1's probe (`docs/queue/RE1/probe-main.patch`) on a copy of `tools/ensemble/Ensemble.java`
in target/re3 (uncommitted), AUTO2, one JVM at a time, books from m600; RE2's trap table
(`docs/queue/RE2/trap.py`'s count, target/re3/bin/table.py). "Named" runs name earth-2.bin and
world-1.bin; "unnamed" runs as the ensemble does (the brief's fixture). Books in $k a month.
Started by RE3, finished by RE3c after the PC went offline (08:15).

| run | loss months | down-payment holds (longest) | months 3+ sectors at the gate | m1060 / m1220 | 40,000 at | final |
|---|---|---|---|---|---|---|
| seed 3 unnamed | 0 | 8 (2) | 63 | 29,375 / 32,118 | never | 32,702 |
| seed 3 named | 0 | 7 (3) | 167 | 30,505 / 37,456 | m1239 | 1,522,575 |
| seed 1 unnamed | 0 | 3 (1) | 442 | 38,820 / 36,271 | m1108 | 1,520,619 |
| seed 1 named | 0 | 26 (12, m1089-m1100) | 732 | 22,729 / 35,882 | m1501 | 719,394 |
| seed 11 unnamed | 5 (m1114-m1118) | 0 | 0 | 34,457 / 24,823 | never | 28,668 |
| seed 11 named | 12 (m955-m966) | 33 (11, m1628-m1638) | 58 | 29,885 / 28,318 | m1553 | 1,071,434 |
| seed 13 unnamed | 0 | 0 | 108 | 90,183 / 261,697 | m903 | 2,325,533 |
| seed 13 named | 41 (m1028-39, m1052-55, m1123-47) | 69 (35, m1160-m1194) | 17 | 16,919 / 24,968 | m1394 | 1,339,197 |

RE2's fixture is gone on main: seed 3 unnamed has no loss month and no hold over 2 months
(RE2: 125 and 59 in a row). What holds each town, from the sectors' decision lines:

- **Seed 3:** unnamed stands at 32,000 from m960 to m2400, held by jobs, not the landlords:
  they hold "housing ahead of jobs" 1,225 of 1,321 months from m1080 and are rich (m2000: cash
  43,468, pre-tax 13,822 a month). No deposit, forest or oil; cement, copper refining and heavy
  industry would lose, wood products and manufacturing do not clear their interest, a Grain
  Farm's ground costs $302M. Named grows from m1260.
- **Seed 1:** no loss month either way. Named's 12-month hold is profitable landlords (pre-tax
  about 2,900 a month, cash 2,025 to 48,946) saving the 15% for a block of 19-21 Low-Rises while
  the town grows (24,630 to 28,652), 14 built at m1102. Its lag after m1200 is land: "no land"
  275 of 481 months for the landlords, and the works do not pay.
- **Seed 11:** unnamed stands at 25,000-33,000 from m1080 to m2400, held by jobs as seed 3's
  ("housing ahead of jobs" every month; cement, copper and heavy industry would lose). Named's
  12 loss months come after the town falls (36,000 at m840 to 23,000 at m960), not before it,
  and the landlords hold "housing ahead of jobs" 423 of 561 months from m840 to m1400. Its
  11-month hold is at m1628, while the town grows fast.
- **Seed 13:** unnamed is the fastest town, no trap. Named is the one run near RE1's chain: the
  town falls from 30,000 (m840) to 15,000 (m1135), led by jobs (works losing, the bank
  rebuilding its capital); then 25 loss months (m1123-m1147: interest 14,997, property tax
  12,200, payroll 3,561, the loss 2,886) empty the till, and 35 months held for the down
  payment follow (m1160-m1194), the landlords profitable (pre-tax 427 to 1,011 a month) and
  repaying about 1,000 a month, with demand unhoused (2,958 at m1197). 40,000 at m1394. The
  payroll over each loss stretch (3,561 and 2,890) is more than its loss (2,886 and 1,961), so
  way (a) would reach these months, but the fall that starts them is jobs-led.

Outputs (not committed): target/re3/out/main-{s,n}{1,3,11,13}.{csv,tsv,log}; scripts in
target/re3/bin (run.sh, q1.sh, q2.sh, table.py, why.py, jobs.py, who.py, loss.py).
