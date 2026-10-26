# CC1-S - care held essential, and why four auto-built villages stand still ({next}, 2026-10-26)

The row: CC1 (0.7.139, childcare at real demand) left two of the eight auto-built seeds, 4 and 5, standing as
villages "on general and senior care, the school and the pensions against their tax", and asked Jerus P2b's question
again: keep care essential, or weigh general and senior care for their payback too. Jerus, 2026-10-18 ("rec all"):
keep care essential, built and kept even when it does not pay for itself. The batch: hold that in the automatic
builders at its cause, find why the villages stall, count it in a real run, and fix it if the cause is in scope and the
fix is clear.

## 1. Care is essential - and already was

Since 0.7.104 (P2b) `AutoBuilder.essential()` is true for power, water, care of every kind, death care and plots, and
`paysBackFirst()` false for them: care is held by the debt limit, the money and the ground alone, and a deficit to run
it is the city's to carry. Nothing else in the builders ranks or refuses care for money: `BuildAdvice` has no money
rule of its own for care (its card sizes the building to the need), the pass retires nothing, and no funding level
closes a centre. `AutoBuildCheck` 5 asserted it for general care only. **As built:** `AutoBuildCheck` 8 asserts it for
every kind - general care, senior care and childcare, each in a town spending past its revenue and short of that kind,
ordered with no payback weighed - and, as the line Jerus drew, that a school on the same deficit is held by the payback.
The model is unchanged here.

## 2. Why the villages stand still, counted

Main (0.8.54), the eight auto-built seeds (`-Dplaytest.autobuild=true -Dplaytest.seed=N`) to month 900:

| seed | 3,000 at | 10,000 at | months under 3,000 | people at m900 |
|---|---|---|---|---|
| 0 | m378 | m513 | 317 | 65,727 |
| 1 | m376 | m453 | 315 | 120,098 |
| 2 | m263 | m409 | 202 | 65,558 |
| 3 | m189 | m425 | 128 | 106,823 |
| 4 | m370 | m477 | 309 | 96,464 |
| 5 | m376 | m621 | 315 | 74,967 |
| 6 | m233 | m422 | 172 | 157,533 |
| 7 | m175 | m370 | 114 | 87,249 |

Seeds 0, 1, 4 and 5 each spend about 310 months under 3,000 people, at about 2,000 (CC1's "seeds 0, 1 and 7 no
longer stall" no longer holds for 0 and 1). Seed 4, read month by month (`-Dplaytest.trace`, `-Dplaytest.budget=60`):

- **Month 32: a dollar bond at 18.4%.** The test player's build-funding rule (`LongPlaytest.build()`, NEEDS_FUNDING,
  "AND IT TAKES THE CHEAPER MONEY") borrows $28.8M for two Bakeries abroad, 20 years, because it compared the rates
  before the sale: `foreignRate()` 2.00% against `getRate()` 3.00%. The quote for that sale abroad (priced with the
  sale in it, `selfPricedWorldCurve`) was 18.45%, coupon 18.375%; at home 3.03%, and the home market places it (a
  probe: $29.1M at a 3.75% coupon, covered 1.32x). The same bond, same month, in seeds 0 and 5 and in the default
  playtest (no flags, month 33).
- **The coupon eats the tax.** About $440k a month for 20 years: at month 240 interest is $1,027k of a $2,455k month's
  revenue. Care runs $666k (less $131k of fees), the school $332k, the pensions $649k (less $229k of contributions);
  the month ends $357k short. Care is about a fifth of the revenue; the interest is two fifths.
- **No cash, so no ground.** From month 100 the treasury lives on the central bank's advances (at their ceiling 194
  months of the run) and unpaid bills (arrears from $3.4M at month 120 to $83M at 348). The test player keeps its
  ground ahead from 10% of positive cash (`GROUND_AHEAD_CASH_SHARE`): zero. The ground is full by month 140, and from
  then to month 357 the landlords hold every house "no land - needs 5,570 m²; 87.8 m² free"; homes stay at 522, jobs at
  771. In 600 months the cash share stopped the ground short 18 times.
- **The looks are two.** The occasional player's skips run 12, 24, 60, 6, 120, 100 months: it looks at about months
  122, 242 and 342 (a seed shifts them a few months), and at the two inside the plateau the cash was next to nothing
  (none at month 242, $0.8M at 342), a tenth of it short of any offer.
- **The way out is the bond's maturity.** It falls due at month 272 and is rolled at home at 4.3%; interest falls to
  $367k, the month turns a surplus, and the look near month 357 buys ground; the town is 16,106 at month 540.

Seed 5 is the same story: the same bond at month 33, "no land" from 120 to 340, interest from $725k to $281k at the
bond's roll (month 273).

## 3. What was weighed

Two changes to the test player, both measured on the eight seeds to month 900 (one build, each behind a flag):

- **F1, the cheaper money by the sale's quotes:** the foreign quote for this sale against the home quote for it.
- **F2, ground on the bond:** a look the cash share cannot cover borrows the offer on the funding page's bond when the
  test player's borrowing test (`canService()`, interest within 25% of the tax) carries it, as its iron does.

| seed | main: under 3,000 / 10,000 at | F1 (the flag on): under 3,000 / 10,000 at | F2: under 3,000 / 10,000 at | F1 + F2: under 3,000 / 10,000 at |
|---|---|---|---|---|
| 0 | 317 / m513 | 262 / m540 | 308 / m503 | 262 / m542 |
| 1 | 315 / m453 | 157 / m524 | 174 / m430 | 157 / m525 |
| 2 | 202 / m409 | 200 / m476 | 202 / m410 | 200 / m488 |
| 3 | 128 / m425 | 123 / m416 | 128 / m427 | 123 / m427 |
| 4 | 309 / m477 | 322 / m591 | 309 / m503 | 303 / m522 |
| 5 | 315 / m621 | 181 / m400 | 304 / m722 | 181 / m400 |
| 6 | 172 / m422 | 194 / m443 | 172 / m413 | 194 / m439 |
| 7 | 114 / m370 | 150 / m420 | 114 / m370 | 150 / m420 |
| all | 1,872 / 3,690 | 1,589 / 3,810 | 1,711 / 3,778 | 1,570 / 3,763 |

F1 alone shortens the time under 3,000 by 15% over the eight (1,872 months to 1,589), halves it in seeds 1 and 5, and
lengthens it in 4, 6 and 7; six of the eight reach 10,000 later (3,810 months in all against 3,690). The dearest dollar
bond the build takes in the eight under it is 4.59%, where main's took 18.45% in seeds 0, 4 and 5.

F2 alone barely moves the villages: in seeds 0, 4 and 5 the coupon fails the borrowing test (interest is 54% of the
tax in seed 4's plateau), so it cannot borrow either. With F1 the money is there and seed 4 still waits: its landlords
now hold "housing ahead of jobs (838 now; 0 coming)" from month 140 to 240 and again from 300 to 360. Under both, a
village of 2,000 that the player leaves for 120 or 100 months waits for ground or for employers; the four Bakeries are
built either way (months 32-33, at home under F1).

**Built: F1, behind a flag, off.** It is the test player's own rule failing its own words, and it changes no rule of
the game. But on by default it moves the played city that some twenty harnesses build their fixtures from, and that
went red in five of them: three fixtures (`CentralBankCheck` 1 line, `ScaleCheck` 7, `OrderSearchCheck` 1) and two
invariants the moved city breaks (`PlanCheck`: "at every 30th month every district's plan keeps every check", 2 lines;
`HomeRailCheck`: "1 month of a home share with no room"). `PlanCheck` is the district plans' (`DistrictPlan`, the map's
files, which this batch stays off while ET6 may work there), and F1 does not end seed 4's plateau, so it is not the
clear fix of the stall the row asked for. Turning it on is its own batch (CC1-S-B). **Not built: F2.** It barely moves
the seeds, and it would rewrite J1d's rule (`LandCheck` 22: "a look the cash share cannot cover buys nothing"). The rest
of the plateau is the call in 6.

## 4. As built

- `LongPlaytest.borrowsAbroad(g, needed)`: abroad only when the window is open and the foreign quote for the sale is
  under `ABROAD_SHARE` (0.8, the old factor) of the home quote for it, under `-Dplaytest.quotedBorrowing=true`
  (`QUOTED_BORROWING`). Unset, the build borrows by `abroadByTheRatesBefore()`, the rule as it was, to the byte. The
  summary counts the build's bonds at home and abroad and the dearest abroad: the default run says "1 at home, 1
  abroad, the dearest abroad 18.45%". The every-19th-stop dollar borrowing, which is there to exercise the foreign
  instruments, is unchanged.
- `ForeignDebtCheck` 10: in a newly founded city whose world rate is under `ABROAD_SHARE` of its own, a sale of two
  years' tax is quoted 18.45% abroad against 3.09% at home and goes home; a month's tax goes abroad once the city's own
  paper is dear (3.69% against 5.09%); quoting books nothing.
- `AutoBuildCheck` 8: general care, senior care and childcare, each retired to none in a town spending past its
  revenue, are each ordered (a Community Health Centre, a Home Care Service, a Large Childcare Centre) with no payback
  weighed; an elementary school in the same sort of town is held for its payback.

## 5. Checks and the trace

- `./batch-check.sh --pick` with the 21 harnesses that found or play a city through the test player (`LongPlaytest.run`,
  `build`, `villageBuild`, `advise`) named: with F1 on by default, exit 4, five harnesses red (22 lines); with
  it behind the flag, on the final tree (d0b7f302), exit 3: all 24 green but MapCheck's recorded red
  (`docs/notes/gate.txt`). Maps regenerated, Stale 0 firm, the tree clean.
- **The trace did not move.** The default playtest on main's classes (79d7c9b4, built apart) and on the branch's,
  side by side: identical but for the run time and the new summary line ("the build's bonds (CC1-S): 1 at home, 1
  abroad, the dearest abroad 18.45%"). **No ensemble** (EN1: only when the trace moves); one started with F1 on by
  default was stopped when F1 went behind the flag.
- Found on the way, main's: auto-built seed 5 run to month 4,000 ends with LongPlaytest's finding "an old works moved,
  2 time(s), months 4000-4000" (Machine Works in district (0,0)); exit 1 on that run, of main's code, before this
  batch touched anything.

## 6. Left for Jerus

**CC1-S-B, the test player's dollar bond (recommended: yes, as its own batch).** Turn `QUOTED_BORROWING` on by default:
the default playtest and every harness city the test player founds stop taking a 20-year dollar bond at 18.45% for
their first bakeries when home lends at 3%. It moves the trace from month 33, so the batch re-picks the fixtures of
`CentralBankCheck`, `ScaleCheck` and `OrderSearchCheck` by the house rule, finds what breaks `PlanCheck`'s district plans
and `HomeRailCheck`'s home share in the moved city (bugs of their own, now seen), gives `AutoBuildCheck` 7's young city
a shortage of its own cause (with it on, the city is short of nothing at month 60; the slider at `SLACK_MOST` makes the
pass want more road than the pinned limit lends for, and the section passes unchanged), and runs the ensemble. After the map
batches, since `PlanCheck` is the district plans'.

**CC1-S-C, the village that waits (recommended: no).**

Not care's: with care essential, and under F1 with the money mended too, an auto-built village of about 2,000 waits while the
occasional player is away, for ground (no cash to buy it) or for employers (no business grows into a village that is
not growing). Automatic building keeps the city's services and buys their ground, never the homes' ground or
industry, by design (0.7.73). Should it also keep the homes' ground ahead, within the debt limit? Recommended: no.
Ground alone did not move seed 4 (F2, and under F1 it waits on jobs), and buying ground the player did not ask for is a
rule of the game, not of the test player.
