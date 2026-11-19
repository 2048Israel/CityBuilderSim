# BO1-S - the owner line switched on (notes)

The brief is `docs/queue/BO1-S.md`; BO1's measurement it re-takes is
`docs/queue/BO1-notes.md`, section 1.

## 1. The probe

`OwnerCheck` with `-Downer.probe=true` (never the default run): the playtest
city of OwnerCheck's section 9 (`LongPlaytest.founding()`, the auto-builder,
AuctionCheck's founding rhythm), played to month 121 twice, the line on
(`HouseholdBalance.setOwnerLine(true)`) and off, printing at months 1, 13,
37, 61 and 121: the people, the retired's disposable income a household
(`getDisposable(RETIRED_ROW)`), the pension paid
(`EconomyManager.getPensionsPaid`), `careAffordability(SENIOR)`, the
senior-care pull (`Migration.getLastSeniorPull`), the owners' bank savings
against all households' and their money abroad. Month 1 is the founding
month, before the first arrivals. About 15 s for both runs.

Run: `java -Downer.probe=true -cp "target/classes;$(cat target/classpath.txt)" ham.citybuildersim.OwnerCheck`
(with the data files named).

## 2. On 0.9.160 (24b7bfcbc), before OP1

| month | people on / off | retired disp/hh on / off | pension paid on / off | careAff(SENIOR) on / off | senior pull on / off | owners' bank / all bank (on) | owners abroad (on) |
|---|---|---|---|---|---|---|---|
| 1 | 0 / 0 | 0 / 0 | 0 / 0 | 1.00 / 1.00 | 1.000 / 1.000 | 0 / 0 | 0 |
| 13 | 128 / 152 | 0 / 0 | 0 / 0 | 0.01 / 0.83 | 1.004 / 1.249 | 38 / 65 | 11 |
| 37 | 440 / 511 | 0 / 0 | 0 / 0 | 0.04 / 1.00 | 1.013 / 1.300 | 722 / 848 | 537 |
| 61 | 700 / 450 | 0 / 0 | 0 / 0 | 0.06 / 0.98 | 1.019 / 1.294 | 782 / 1,042 | 1,040 |
| 121 | 1,160 / 404 | 0 / 0 | 0 / 0 | 0.05 / 0.44 | 1.016 / 1.133 | 1,061 / 2,017 | 1,747 |

Five owner families from month 13 to the end, none dropped. Line off, the
bank holds 645 at month 37 and 203 at month 121.

- The retired are paid nothing in either run: no pension and no disposable
  income of their own, as BO1 found. With the line on, the senior-care pull
  is almost all gone (`careAffordability(SENIOR)` 0.01 to 0.06), as in BO1.
- The difference from BO1 is the line-off city: on today's model it
  peaks near month 37 (511) and falls to 404 by month 121 (BO1's 0.9.90 had
  3,500). With the line on it keeps growing, to 1,160. So on today's model the
  line does not shrink this city; it ends nearly three times larger.
- The owners' banked dividends (BO1's second channel): 53% of the city's
  bank savings at month 121 (1,061 of 2,017), 1,747 abroad.

## 3. Which ship turned the line-off city (the orchestrator's question, 11-19)

The probe's line-off run on 0.9.158 (ba734bf85, before HS1) and 0.9.159
(7117fef6b, HS1), each in a scratch worktree with the probe patched in:

| month | people off: 0.9.158 / 0.9.159 / 0.9.160 | careAff(SENIOR) off | senior pull off |
|---|---|---|---|
| 13 | 152 / 152 / 152 | 0.83 / 0.83 / 0.83 | 1.249 / 1.249 / 1.249 |
| 37 | 509 / 511 / 511 | 0.83 / 1.00 / 1.00 | 1.250 / 1.300 / 1.300 |
| 61 | 448 / 450 / 450 | 0.83 / 0.98 / 0.98 | 1.250 / 1.294 / 1.294 |
| 121 | 405 / 404 / 404 | 0.63 / 0.44 / 0.44 | 1.190 / 1.133 / 1.133 |

(Line on, 0.9.158: 437, 698, 1,218 at months 37, 61, 121, careAff(SENIOR) 0.00.)
The fall is already on 0.9.158: HS1 did not turn it. HS1 moved senior care's
affordability (0.83 flat to 1.00, then 0.44 at month 121) but not the people.
0.9.160 is 0.9.159 to the figure.

The playtest's own city (`LongPlaytest -Dplaytest.months=130
-Dplaytest.trace`, 0.9.160, its `-pop.csv`): 152, 635, 917, 1,172 at months
13, 37, 61, 121, still growing (jobs flat at 617 from month 91). So the
playtest city does not fall; only the probe's rhythm does (OwnerCheck
section 9's: the auto-builder from month 1 with forty houses at once).
(Run cut at month 130, so its exit 1 is the audit's lines at month 132, the
short run's end.)
