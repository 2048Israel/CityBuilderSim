# PO1 — The path lines as nightly observations

Built from HA1 on main, 0f4fb634, on 2026-11-13. This is a no-tag process
batch. The final ship file records readiness after the green fresh-clone gate
on main's EI1 commit a26b79c3.

## Built and decisions

- ★ Path readings return their actual truth. Each dependent law has an explicit
  guard and a printed skip reason. Reached correctness assertions retain their
  failure counts.
- ★ A compound premise's identities remain separate hard assertions. This
  includes payer records, equal quoted faces, same-line reopening, bank branch
  placement and card/funding-page quote equivalence.
- ★ Timing and observations share a UTF-8 append writer. Its synchronized
  method serializes same-JVM writers and its FileLock serializes child JVMs.
  Timing retains its incomplete-write handling, and observation write failures
  surface as an incomplete harness.
- ★ Synthetic books use the prepared fixture even when the played market lacks
  room for a sale. Each actual-sale experiment guards its own availability.
- ★ Nightly identities stay stable when measured figures in the printed label
  change. A miss in any sample wins over holds of that line that night.
  Per-line histories preserve the last hold through red or incomplete nights.
  First-ever misses explicitly report that an earlier held range is unavailable.
- ★ The town and played price-level comparisons have 120-month maximum horizons.
  Same-level nudges of the factor and today's prices by 1e-12 derive the prefix
  before path separation. The existing comparison tolerances and laws apply in
  that prefix.
- ★ Obsolete fixture-repinning narratives are shortened to their intent
  sentences. Their measurements remain in Git history. This pays for the new
  helper and reporting machinery under one in, one out.
- The game's model, version, save format and default playtest logic have no
  changes. Its trace is unchanged by construction, so the ensemble is skipped.
- HW1 mailed a future change to school payback/eligibility. Lanekeeper confirmed that HW1 will adapt the shared harnesses after PO1 lands. PO1's assertions follow HA1/main, and HW1 retains
  responsibility for its model's changed school laws.

## Per-harness audit

Observed locations below are this branch's source call lines. Retained base
line numbers refer to HA1/main 0f4fb634. The line-list estimates include repeated
samples and the laws guarded by a premise. The source calls are counted here.

| Harness | Observation call lines | Retained laws and caused fixtures |
|---|---|---|
| [ScaleCheck](../../src/main/java/ham/citybuildersim/ScaleCheck.java) | L452, L456, L461, L675, L676, L838, L1212 | Base L724–730 ground, price and rent-pull scaling, L864–870 six-month tracking and register identities, and L889 load success. Rent-pull tolerance needs the calm-source reading. Empty-site availability guards the tracking experiment. |
| [HoldersCheck](../../src/main/java/ham/citybuildersim/HoldersCheck.java) | L432, L435, L438, L663, L683, L689, L692, L1860, L1868, L1880, L2043, L2057, L2061 | Save-twin identities, coupon and holder books, discount allocation, reopened-line identity and settle arithmetic. The whole-book dial clamp runs before its path guards. The capital-flow width reads the model's serialized shape. |
| [SaveFileCheck](../../src/main/java/ham/citybuildersim/SaveFileCheck.java) | L557, L1026, L1029, L1432, L1657, L2500 | Funded order success, safe writes, floor refusal, all available-data load identities and continued play. Hot-money arrival, short-sale stock and fund holdings that the fixture explicitly places remain asserted. Width mirrors read model-produced arrays. |
| [CurrencyRegimeCheck](../../src/main/java/ham/citybuildersim/CurrencyRegimeCheck.java) | L924, L930, L933, L1160, L1551, L1712, L1917, L1998 | Constructed peg/band/float books, the bare credibility dent and synthetic hot-money flow slots. Payer identity, equal purchases and restored state remain hard. Played-city searches guard the dent, payer and save experiments they enable. |
| [AutoBuildCheck](../../src/main/java/ham/citybuildersim/AutoBuildCheck.java) | L468, L470, L479, L487, L813, L889 | Every per-pass audit, settings and save-twin equality, and the room-given payback fixture. Ground-only and borrowing-only laws require those event readings. The share of services at target is observed. |
| [MonetaryCheck](../../src/main/java/ham/citybuildersim/MonetaryCheck.java) | L981, L984 | Base L984 same founding and held dials, L991–998 inflation ordering and transmission floor. The latter require the crew and one-month-delay premises. |
| [BondCheck](../../src/main/java/ham/citybuildersim/BondCheck.java) | L200, L561, L796, L832, L948, L992, L1213 | Bond arithmetic, constructed bookbuilding and lender fixtures, recovery rules, all reached sale and holder laws, and money audits. A missing played bond skips its own experiments while independent arithmetic and quote fixtures run. |
| [FundCheck](../../src/main/java/ham/citybuildersim/FundCheck.java) | L256, L852, L874 | Forced losses and rescues, preferred capital, 70/30 algebra, position caps, withdrawal and idle-cash rules. Pure share-mix algebra runs before the listed-company guard. Rescue share-count equality requires an unchanged unit. |
| [PriceLevelCheck](../../src/main/java/ham/citybuildersim/PriceLevelCheck.java) | L540 | Literal funnels, cross-file ratios, real/nominal equality, integer counts and cutoffs. Four same-level nudge experiments derive each comparable prefix. The comparison laws remain asserted throughout that prefix. |
| [DeskCheck](../../src/main/java/ham/citybuildersim/DeskCheck.java) | L376, L377, L607, L1077 | Book values, coupons, cash-flow identities, curves, remembered figures and save equality. Directional gain claims require the corresponding under-carrying reading. Hand-constructed uplift and books-ahead cases use the prepared city directly. |
| [FittingCheck](../../src/main/java/ham/citybuildersim/FittingCheck.java) | L631, L635, L646 | Directly set coal prices, new-building pieces, payment, fitting choice, ranking ties and on-site exclusion. The two Bakery-profit searches guard their respective relative-ranking laws. |
| [LabourCheck](../../src/main/java/ham/citybuildersim/LabourCheck.java) | L788, L813, L924, L927, L971, L974, L1384, L1411 | Constructed wage ladder, scarcity, floor, migration formula and unit-reform laws. Played-arrival comparisons use their availability flags. Index-chase equations remain hard, and the long-run lag window needs steady inflation. |
| [BuildAdviceCheck](../../src/main/java/ham/citybuildersim/BuildAdviceCheck.java) | L776, L779, L1250, L1251, L1252, L1333 | Categories, NEEDS YOU ordering, per-card projected size, same-price/same-post twins, berth payback and caused care coverage. Staffing and short-care finders guard their own experiments. Projection-coverage counts are observed. |
| [AuctionCheck](../../src/main/java/ham/citybuildersim/AuctionCheck.java) | L787, L811, L835, L841, L1435 | Constructed clearing, bidder rules, caps, coupon rounding, lineup identity, refusal and benchmark laws. Section 11's real-run event count stays asserted. Section 7's independent benchmark arithmetic and 40-year refusal run outside the played lineup's guards. |
| [ConstructionControlCheck](../../src/main/java/ham/citybuildersim/ConstructionControlCheck.java) | L603, L623 | Buy-out quote, compensation, ownership, debts, money conservation and demolition. Full occupancy guards the next-month household-retention claim. The played population-growth outcome is observed. |
| [MapCheck](../../src/main/java/ham/citybuildersim/MapCheck.java) | L1364, L10204, L10357 | All oracle selector comparisons, targeted offer selectors, map/model equality, geometry, sidecars and building-placement laws. Card equivalence is checked against independent covering quotes. Second-block FX9 coverage guards its share comparison. |
| [CentralBankCheck](../../src/main/java/ham/citybuildersim/CentralBankCheck.java) | L1774, L1788, L1791, L2219, L2224, L2277, L2280 | Money creation/destruction, holder books, surplus waterfall, remittance, advances and strictness-rule arithmetic. Section 20's surplus premises guard its 31 dependent laws. Sample median/share, trust and end-of-run inflation are observations. |
| [KeptCheck](../../src/main/java/ham/citybuildersim/KeptCheck.java) | L550, L554, L814, L1092, L1097, L1582, L1591, L1738, L2121, L2722, L2907, L3464, L3510, L3652, L4533, L4911, L4966, L6568, L6572, L6581, L7778, L7782, L7791, L8879, L8884 | Top-node identity, fresh-page equality, pixels, actual count-up behavior, unchanged redraws and surviving Tooltip identity. Quiet-month/retention shares and hover finders are observations. Explicit second unchanged draws exercise chrome, History and page creation. |
| [PolicyPreviewCheck](../../src/main/java/ham/citybuildersim/PolicyPreviewCheck.java) | L453, L456, L562, L575, L630, L650, L669, L695, L892, L907, L909, L926, L953, L977, L997, L1083, L1222, L1227, L1234 | Every applied dial and preview/model equality with its proper input. Bank profit and EI claimants have separate guards. Wage grouping, applied floors, placed berths, policy copies and zero-traffic arithmetic stay hard. Actual traffic and quotes guard their positive and twin laws. |
| [BankCheck](../../src/main/java/ham/citybuildersim/BankCheck.java) | L2998, L3015, L3031, L3037, L3043 | Bank identities, capital, allowance, desk, rescue and deliberately constructed books. Five branches being placed is hard. Played fee coverage and cost-rise availability guard the corresponding branch-rule comparisons. |
| [HealthCheck](../../src/main/java/ham/citybuildersim/HealthCheck.java) | L1399, L1400, L1401, L1429, L1431, L1433, L1435, L1457, L1458, L1467 | Fee arithmetic, who was offered/served/priced out, coverage and illness rules, staffing, mortality on constructed cohorts, funeral books and save equality. The poor played twins' population, death and hunger orderings are observed. |
| [ForeignCheck](../../src/main/java/ham/citybuildersim/ForeignCheck.java) | L840, L946 | Balance-of-payments and synthetic currency/defence laws, the mechanical devaluation price ratio and save identities. Played export coverage is observed and programme comparison requires meaningful trade. |
| [FloatLinkCheck](../../src/main/java/ham/citybuildersim/FloatLinkCheck.java) | L644, L648 | Floater reset, linker uplift, holders, money and save laws. Prepared synthetic books run independently of played-market sale size/room. Reached actual-sale cases retain their effective-rate and pricing checks. |
| [CapitalFlowCheck](../../src/main/java/ham/citybuildersim/CapitalFlowCheck.java) | L342, L599, L666 | Stock, spread, reserves, coupon, statement, tax and cash-flow identities. The order helper deliberately supplies cash, so order success stays hard. Profitable-twin availability guards only positive-profit tax/dividend comparisons. Equal tax rates remain hard. |
| [CallCheck](../../src/main/java/ham/citybuildersim/CallCheck.java) | L442, L448, L589, L599 | Ho–Lee tree, volatility, option cost, quoted face identity and reached call mechanics. Full-market coverage and fair tails are observed separately, and central-bank holder availability guards its waterfall. |
| [HomeRailCheck](../../src/main/java/ham/citybuildersim/HomeRailCheck.java) | L911, L920 | Hand-made capacity and priority cases, each freight leg, room/yard bounds, billing, diesel and save rules. The two played home-rail coverage counts are observations. |
| [HousingCheck](../../src/main/java/ham/citybuildersim/HousingCheck.java) | L894, L900 | The hand-built .4/.2/1 closed-debt examples, rent/carry identities and caused glut fixtures. Played glut occurrence is observed. Closed-home share save comparison requires a nonzero closed share. |
| [CurveCheck](../../src/main/java/ham/citybuildersim/CurveCheck.java) | L545, L659, L708 | Expected-dial path, constructed flat/hiking/QE books, exact curve prices and held-strike identities. Played steepness guards the issue and bond-market experiments. Changed expectation guards only its distinctness claim. |
| [PrizeCheck](../../src/main/java/ham/citybuildersim/PrizeCheck.java) | L1533, L1962 | Research catalogues, prizes, payouts, labs, grants and reached investor ordering/stake laws. Engineering's prize finder guards its own experiment, and the independently founded Medicine case still runs. Section 20 stake claims require a played lab. |

170 observation call sites across all 29 line-list harnesses.

## Known-red entries retired

Exactly these 15 entries leave the repository record because their path
readings, or their precise dependent checks, now use observations. All other
entries remain. The base record had no entries assigned to PO1.

| Harness | Retired line fragment | Previously queued fix |
|---|---|---|
| CentralBankCheck | ...and in at least STRICT_SHARE of them | CB2 |
| CentralBankCheck | very loose costs trust, through the lean measured against holding the target | CB2 |
| HealthCheck | ...and it buries more of its people over the run | HX1 |
| HealthCheck | ...its old first, whom a fee on senior care turns away | HX1 |
| HealthCheck | fixture: so the beds are not what differs: at the dial's top they take no larger a share by more than the fee turns away | HX1 |
| KeptCheck | a bookRow was found on a money page of this city | KS2 |
| KeptCheck | a month landing starts a business's KEPT and the Income page's waterfall counting exactly at the rungs countsUp() allows | KS2 |
| KeptCheck | the BuybackLadder on Close it now: no month draws it again with nothing changed in it | KL1 |
| KeptCheck | the city has a piece of paper to close | KS2 |
| MapCheck | ...and issued, the card's bond buys them all | BQ1 |
| MapCheck | short, the card's bond is the funding page's first: the 20-year term for the gap | BQ1 |
| MonetaryCheck | a month's delay in the hand moves them less than the allowance | MQ1 |
| KeptCheck | a month's redraw after each choice hands back nine in ten of the page's nodes, or more | KBF1 |
| KeptCheck | Household money, all of them: every tooltip a node had and still has is the same Tooltip after a month's kept redraw | KHT1 |
| MapCheck | fixture: the boxes take offers, several at once, and every offer is taken by some and missed by others | M6-F |

## Queued rows for Jerus's review

Queue rows remain in place for his review. The following describes their
path-only portions covered here.

| Queued row | Portion now observed or guarded |
|---|---|
| LD3 | Scale's growth/ground/rent availability, holders' split and surplus searches, and Desk's played gain direction. Its other repairs keep their scope. |
| ST3, HL1-R | Scale's pinned growing-month and free-copy availability. Scaling identities remain asserted. |
| WG2-R | Played-month, order, share-unit and coverage repicks across the listed harnesses. |
| WG1-R | Holders' played reopening/premium availability, housing closures, and central-bank surplus/strictness samples. |
| BK2b, RT1b | Holder-fixture availability is observed. Holder discount and book identities remain hard. |
| DQ1 | Played bank, household purse and company-borrower availability is observed. Its model repairs retain their scope. |
| RT3, SQ1 | Growth-path availability is observed. Their model/scaling repairs retain their scope. |
| HZ1, FW6-C | Played save-fixture availability and literal width mirrors. Explicitly placed hot money/short-sale data and load laws remain hard. |
| QB1 | Auto-build and home-rail event coverage. Caused shortage and remit/rail laws remain asserted. |
| MQ1, CB2, MN1 | Monetary delay/crew availability and central-bank strictness median/share/trust readings. |
| R0d-R | Fitting-profit, lab-prize and lab-stake searches. Its model half retains its scope. |
| LM1-R | Second-block FX9 open/closed-cell coverage. Placement and oracle laws remain asserted. |
| BQ1 | Availability of independently covering card bond/note quotes. Quote equivalence and purchases remain hard when covered. |
| M6-F | Scattered-box coverage of all offers. Exhaustive oracle and targeted selectors remain hard. |
| DM3, R0b-M, DM1-P | Their map correctness repairs retain their scope and hard assertions. |
| UP9-T, HA1 | Timing remains on HA1/GR1's nightly path. HA1 is already on main. |
| KS2 | Moving count-up figures, hovered book rows and a piece of paper to close. Reached animation, pointer and box laws stay hard. |
| KL1 | Played-month unchanged-display frequency. The deliberately unchanged redraw law remains asserted. |
| KBF1, KHT1 | Retention floors and continued tooltip availability. Fresh-page and surviving Tooltip identity remain asserted. |
| KS1-rest | Path retention shares covered here. Its clock and other repairs retain their scope. |
| KS1-Saved, KS1-UI | Forced save/read completion and fresh-page/header equality retain their hard assertions and queued repairs. |
| HX1, RN1-F | The poor played cities' mortality, hunger, bed and treatment ordering readings. |
| BA1-B | Played sale-room/size availability is observed. Its model half retains its scope. |
| PW1 | Played call/sale availability. Tree, option and reached call mechanics stay asserted. |

## Checks

- Fast checks passed after the migration groups, including compilation,
  regenerated maps, zero firm Stale findings and tree validation.
- ObservationCheck, TimingCheck and PriceLevelCheck passed together, 3 harnesses
  in 88 seconds, before the final scope review.
- The nightly self-test passed with five synthetic merges. It covers last-held
  attribution, mixed samples, independent line histories, first misses, recovery
  missing records and an interrupted cold all-miss state write. The log is
  `target/checks/po1-nightly-selftest-final.txt`.
- The full pick selected ALL 197 and finished with exit 3. It ran 196 harnesses
  in 2,522 seconds, with 194 passing. PlanCheck and DeathRecordCheck were the
  two known reds. No marked path line remained red. The suite is retained at
  `target/checks/po1-pick-saved/suite.txt`, with the wrapper log at
  `target/checks/po1-pick.txt`. The final PolicyPreview/Tooltip scope review
  followed this preflight, and the fresh-clone gate verifies the exact tree.
- The final named run passed PolicyPreviewCheck, ObservationCheck and TimingCheck
  with exit 0, including compilation, regenerated maps and zero firm Stale
  findings. Its log is `target/checks/po1-final-named.txt`.
- The first fresh-clone no-tag dry run passed with launcher exit 0 and suite
  exit 3. It ran 196 harnesses in 1,735 seconds, with 194 passing and only
  DeathRecordCheck and PlanCheck known red. Compilation, maps and Stale
  completed. Its kept directory is
  `/c/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/codex-PO1/target/po1-gate-final`,
  with `ship.log` and `checks/suite.txt`. Its branch was
  `5957b87aa5ad311273f4699db3f9668a2b311d39` and main was
  `c5c7a460fd9c50b2070aae009045cbe349586b5c`.
- ★ EI1 advanced main to `a26b79c30f048416cf614440ce88224cdc2e7f6d`
  during that run. It changes the ensemble process, with no Java changes.
  The cache's main-advance rule rejects those scripts, so a second fresh-clone
  dry run verifies the merge on that main. Source remains at the first tested
  commit.
- The final fresh-clone dry run on EI1's main passed with launcher exit 0 and
  suite exit 3. It ran 196 harnesses in 1,633 seconds, with 194 passing and
  only DeathRecordCheck and PlanCheck known red. Compilation, regenerated maps,
  zero firm Stale findings and tree validation completed. The retained run has
  its completion marker and no unnamed reds.
- The final kept run directory is
  `/c/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/.claude/worktrees/codex-PO1/target/po1-gate-main-ei1`.
  Its logs are `ship.log` and `checks/suite.txt`. The tested branch is
  `5957b87aa5ad311273f4699db3f9668a2b311d39` and tested main is
  `a26b79c30f048416cf614440ce88224cdc2e7f6d`. The ship file names this run for reuse.
  After this dry run, the branch advances only through these notes and its
  ship file.

## One in, one out

The authored process change adds 1,198 and removes 1,036 lines, net +162.
The count is `git diff --numstat origin/main` over the files below, excluding
this notes file and the ship file. It includes the helper, runner and contract
check, migrated harnesses, nightly scripts, README, known-reds and tree entries.
Retired entries and superseded fixture-repinning histories supply the removals.
Generated maps and indexes add 8,693 and remove 8,731 lines separately.

```text
5	0	README.md
0	15	docs/notes/known-reds.txt
2	0	docs/tree.md
9	3	src/main/java/ham/citybuildersim/AllChecks.java
35	26	src/main/java/ham/citybuildersim/AuctionCheck.java
25	19	src/main/java/ham/citybuildersim/AutoBuildCheck.java
40	101	src/main/java/ham/citybuildersim/BankCheck.java
39	36	src/main/java/ham/citybuildersim/BondCheck.java
17	20	src/main/java/ham/citybuildersim/BuildAdviceCheck.java
14	7	src/main/java/ham/citybuildersim/CallCheck.java
24	30	src/main/java/ham/citybuildersim/CapitalFlowCheck.java
23	19	src/main/java/ham/citybuildersim/CentralBankCheck.java
6	9	src/main/java/ham/citybuildersim/ConstructionControlCheck.java
35	25	src/main/java/ham/citybuildersim/CurrencyRegimeCheck.java
11	20	src/main/java/ham/citybuildersim/CurveCheck.java
32	38	src/main/java/ham/citybuildersim/DeskCheck.java
12	25	src/main/java/ham/citybuildersim/FittingCheck.java
37	16	src/main/java/ham/citybuildersim/FloatLinkCheck.java
7	13	src/main/java/ham/citybuildersim/ForeignCheck.java
18	28	src/main/java/ham/citybuildersim/FundCheck.java
17	54	src/main/java/ham/citybuildersim/HealthCheck.java
75	105	src/main/java/ham/citybuildersim/HoldersCheck.java
4	6	src/main/java/ham/citybuildersim/HomeRailCheck.java
9	6	src/main/java/ham/citybuildersim/HousingCheck.java
116	32	src/main/java/ham/citybuildersim/KeptCheck.java
34	44	src/main/java/ham/citybuildersim/LabourCheck.java
19	33	src/main/java/ham/citybuildersim/MapCheck.java
16	17	src/main/java/ham/citybuildersim/MonetaryCheck.java
67	0	src/main/java/ham/citybuildersim/Observation.java
97	0	src/main/java/ham/citybuildersim/ObservationCheck.java
94	50	src/main/java/ham/citybuildersim/PolicyPreviewCheck.java
55	47	src/main/java/ham/citybuildersim/PriceLevelCheck.java
40	33	src/main/java/ham/citybuildersim/PrizeCheck.java
45	70	src/main/java/ham/citybuildersim/SaveFileCheck.java
28	83	src/main/java/ham/citybuildersim/ScaleCheck.java
12	3	src/main/java/ham/citybuildersim/Timing.java
30	0	tools/nightly-selftest.sh
30	2	tools/nightly-timing.sh
3	1	tools/nightly.sh
16	0	tools/observations.awk
```
