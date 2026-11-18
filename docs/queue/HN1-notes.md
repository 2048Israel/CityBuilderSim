# HN1 - households housed before the matching homes finish

A model change: `Game.considerOnMortgage()` and `doorTrimmed()` (+68 -1), `BusinessDebtManager.takeBackCapitalRefusal()`
(+11), and a new harness, `HousingDoorCheck` (+323), with its line in `AllChecks` and `docs/tree.md`. The default trace moves (section 4), so the ensemble runs on the final model.

## 1. What was found

**The evidence, rebuilt.** R0h's `target/r0h-homeless/` was gone, so the run was made again: the ensemble's driver
(`tools/ensemble/`, AUTO2 = the autopilot at a 2% target) on R0h's tree 8e09577ab, seed 12, founded and played to
m748 with a probe reading the housing, the landlords' tills, the plan's order and the lender's door every month. It
reproduces R0h's finding: households with no door at m744-747, none at m748. Played straight through, as the ensemble
plays, it left 3.5928, 5.8487, 8.4962 and 13.2172 with no door. At m744 that is R0h's 3.592771... to the digit, the
same path to the bit. R0h's 7.85, 12.49 and 17.20 for m745-747 were played from its saved m743 checkpoint (the brief:
"from their valid month-743 checkpoint"), not straight through. The gaps, 2.00, 3.99 and 3.98 households, are about the
2 and then 4 more homes this run finished at m745 and m746 (3,044, 3,046, 3,048; 3,112 at m748 against R0h's 3,107).
Not misquoted, then, but a different start point from m745; that checkpoint went with R0h's folder, so whether the
reload parts from the straight run's path there cannot be checked again.

**What the shortage was.** It was not placement, the forecast, the building choice or the queue. The landlords' plan
(`RealEstate.plan()`, off the family-door shortfall) asked for 26-35 Low-Rise Apartments (size 4, the family home)
every month from m699 to m746, and `Mortgage.decide()` passed the order on the till and the lender's test. Then the
door (`buildFor()` -> `canBorrow()` -> `BusinessDebtManager.canFundMortgage()`) said no to the whole order, every
month: "the bank is short of capital" (the leverage ratio binds, so insured mortgages are rationed) or "needs more of
its own for the 15% down payment" (the door prices the purchase at `quoteBuild()` + the land, 2-5% above the plan's
`getCostOf()`, so the down payment raised to the plan's figure falls short at the door). Nothing was built. The till
held up to 32.8k, against a Low-Rise at about 6.3k: five blocks it could have bought outright. The family-door
shortfall rose from 2,257 to 2,870, doubling up filled to its ceiling (2,959 of 5,918 households, the ceiling half
of all households plus one), and from m744 what lay past both valves had no door. Studios stood empty beside them
(a household with dependants never takes one): the matching size was the one held.

**Today's main.** c17b92f0f (0.9.148) does not reach the ceiling on seed 12, nor does 6114b00f: their paths part from
R0h's. But both hold the same way at the door. Seed 12 on today's main holds the whole order in 23 of the 35 months
of m430-500 that had an order at the door, and today's default playtest has "the landlords held for the bank's
leverage in 34 month(s)" (its report's own count; the leverage ratio is the larger requirement in 1,412 of 2,404
months). The fault is main's, latent wherever the leverage ratio binds or the door's price runs above the plan's.

## 2. What changed and why

`Game.considerOnMortgage()`: when the door refuses the order, it asks again with half the order, down to one. A
smaller order needs a smaller loan, and one the landlord's own till covers needs no loan at all, which is what a
banned landlord may build too (`consider()`). A failed `buildFor()` changes nothing before `canBorrow()` says no, so a
retry starts from the same state. It retries on any refusal at the door (capital, down payment, the default point,
a failed bank), since buying outright needs no lender. An order the door cut down is reported in the advisor's line
by `doorTrimmed()`, which gives the door's first refusal: "Built 1 Low-Rise Apartments on an insured mortgage (trimmed
from 3 - its own funds put the 15% down on 1)", "(trimmed from 4 - the bank is short of capital for more)". The
plan, the lender's test, the capital rule and every dial are untouched.

After the stranger's review (REV-HN1-1, SHIP, no P1), two report-only mends:

- A trimmed build takes back the capital marks the larger try left (`BusinessDebtManager.takeBackCapitalRefusal()`,
  restoring them as they stood before the first try). Otherwise LongPlaytest counted the month as "held for the bank's
  leverage", and `consider()`'s next line could blame capital.
- A trimmed order bought outright says so ("trimmed from 5 - the bank is short of capital for a mortgage, so what its
  own funds buy outright"), not "15% down".

Neither moves the model: the marks are read only by the advisor's words and the playtest's count. The traced default
playtest run again after them is identical to the one before in all eight CSVs, the advisor's words included (section 4).

On R0h's tree with the mend: no finding in 748 months. At m743 there are 6,560 homes against 3,044, and 28,018 people
against 19,745. Nobody is past the valves.

## 3. The regression: HousingDoorCheck

**The fixture differs from the brief's (REV-HN1-1 asked that it be said).** The brief asked for the m743 checkpoint,
played through m744-748. That checkpoint went with R0h's `target/r0h-homeless/`, and a save would not outlive the next
`SAVE_FORMAT` raise. Neither today's main nor 6114b00f reaches the shortage on seed 12 from founding: their paths part
from R0h's. So, as the orchestrator's brief allows, the harness causes the same state on today's main. Over m430-500,
the window in which today's seed 12 holds orders, the landlords' segment is short of the homes its households need
while other homes stand, and the auto-builder plays as normal.

Its red on main is section 2, the door's own rule (a till that holds the door's price of one, on ground for one, builds
it), not section 4. LongPlaytest's >1 watch passes on main in that window, because doubling up is still under its
ceiling there. It cannot be compiled on 6114b00f, which has no `LongPlaytest.played(null)`, so its red is on today's
main before the mend, the brief's second case.

It plays the ensemble's AUTO2 seed 12 on today's main (the same founding, policy and seed; a scratch city from
`GameFiles.scratch`), watching m430-500 through `Game.watchOrders`'s `mortgaged()` hook. Each month it records the
order the plan passed, the landlord's till, the door's price of one (`quoteBuild(t, 1)` + `LandManager.priceFor`),
whether there is ground for one (`canAllocate`), the family-door shortfall (`RealEstate.doorShortfall`), what was
built and the homes finished by size.

1. The fixture: the plan passed an order the door did not write whole, while the segment was short of doors.
2. No month holds a landlord whose till buys one of the homes the plan passed outright, on ground for one, while its
   segment is short of doors.
3. Every order the door cut is the plan's order halved down to one, and the advisor's line says it was trimmed. Since
   REV-HN1-1, the fixture also holds an order the door trimmed after a first no for the bank's capital (m495: "trimmed
   from 4 - the bank is short of capital for more"), and no month the door trimmed ends marked as refused a mortgage
   for capital (`wasMortgageRefusedForCapital`, what LongPlaytest counts). With the take-back commented out that line
   is red ("1 left marked"); with it, green.
4. LongPlaytest's watch: no month leaves more than one household with no door.

On c17b92f0f without the mend: **FAIL in section 2**. 5 months stranded, the first m472: Low-Rise Apartments, a till of
2,497.3 against the door's 1,920.2 for one, "Holding: needs more of its own for the 15% down payment on 8 Low-Rise
Apartments". 23 orders held whole, 0 cut; 441 family homes finished in the window. With the mend: all pass, 3 cut, 8
held whole (each the bank's capital, with less than the door's price of one in the till: the capital rule doing its
work), and 1,071 family homes finished. LongPlaytest's watch, its seed,
founding and policy are unchanged, and no floor moved.

## 4. How it moves the playtest

The default playtest was run with `-Dplaytest.trace` on main c17b92f0f and on this branch (557d6c0bb), one at a time.
The kept FB4 trace (10cc0ca2) predates main's last model changes (Sector, WorksValue, CopperRefining, Migration), so
main's was made fresh, as the orchestrator allowed; it is in `target/hn1/tr-c17/`, and the branch's is in
`target/hn1/tr-hn1/`.

- All eight CSVs are identical through m1683. At m1684 the door, which held the order whole on main, writes 2 of 5 Low-Rise
  Apartments ("trimmed from 5 - its own funds put the 15% down on 2"). That is the landlords' 35th mortgage, and the paths
  part there.
- Over the run: the landlords are held for the bank's leverage in 3 months (main 34). This figure stands after
  REV-HN1-1's take-back, run again: none of the run's door trims followed a no for the capital (all 486 lines that
  say "its own funds put the 15% down" are down-payment trims or the lender's own), so no trim was counted as held. They write 551 mortgages (main
  355), 223 owed at the end (main 47). The leverage ratio is the larger requirement in 1,163 months (main 1,412).
- Both find nothing ("Every month passed the audit and every reload matched"). No household is past both valves in any
  month of either (`stillUnplaced` 0 throughout). At its worst, 48,423 of 149,047 households doubled up (m2363), against
  main's 75,584 of 306,178 (m2405).
- The mended city ends smaller: 539,318 people at m2402 against 1,053,251 at m2405. Its exchange rate is 312 at m2400
  (trace-month's `rate`) against 75, and its city debt 34.8T against 8.1T. This is one path, 720 months after a split
  made by two homes. City debt in years of GDP runs below main's for most of the path (0.29 against 1.69 at m2000,
  0.09 against 1.20 at m2200) and swings above it at the end (3.04 against 1.36 at m2400). Insurance claims are $0 in
  both. The capital rule refused other sectors a building's loan in 501 sector-months against 453, while it was on in
  116 months against 182 (REV-HN1-1's (5a): a trimmed mortgage uses capital room that sectors asking later in the month
  would have had).

**The ensemble** (`docs/ensemble/HN1.md`, 80 of 80 runs on 2c205e493, the merged tree, against `0.9.146.md`, CPB1's):
batch verdict PASS, hard criteria 10/10. M1 is 0 months on every policy, and F1 is 0/16 on every policy.

| policy | final population (T2), median [range] | baseline 0.9.146 |
|---|---|---|
| AUTO2 | 2,656,362 [1,392,715 to 5,882,191] | 2,335,590 [1,242,257 to 4,873,682] |
| ZERO_SLACK | 1,735,856 [391,013 to 4,172,643] | 1,426,515 [419,665 to 2,975,422] |
| ZERO_HOT | 7,859 [1,697 to 20,661] | 6,308 [2,940 to 13,996] |
| SHOCK | 36,114 [32,420 to 318,562] | 34,922 [26,060 to 208,272] |
| HIGH | 26,540 [16,912 to 80,231] | 28,496 [18,298 to 79,350] |

- T1: every AUTO2 seed reaches 40,000 people, at m820 [542 to 1,536], against m1,030 [584 to 1,416].
- Across the runs the cities are larger, not smaller. The default path's smaller end is that path's.
- One changed verdict, info only: ZERO_HOT H2, PASS to FAIL. It has 11 of 16 seeds below the second half's excess,
  where 12 are needed; the median is -0.56% against -0.60%.
- The other info rows move within their ranges: A1 3.10% against 3.16%, A2 24 against 23 months, S1 2.33% against
  3.04%.
- **Paired with main** (the orchestrator's ask; the seed-by-seed table is in `docs/ensemble/HN1.md`). AUTO2's 16 seeds
  were run on main's merge base, da6f5347e, with the same driver and data, in a detached worktree removed afterwards.
  HN1's 16 seeds were run again beside them, and they match its ensemble pop for pop. Both sides read claims, premiums,
  mortgages and the capital rule's refusals through `-Densemble.extra`.
  - Debt in years of GDP: 0.14 against 0.17 at the end (HN1 higher in 6 of 16), 0.16 against 0.18 at m2000.
  - Claims: $0.1k against $3.0k (HN1 higher in 3 of 16).
  - Neither rises beyond the seeds' spread.
  - What rises is the capital rule's no to the other sectors' building loans: 842 against 562 sector-months, higher in
    13 of 16 seeds. The landlords' holds for capital fall from 168 to 26 months in every seed, and the mortgages
    written rise from 411 to 488. The landlords now use capital room that a held order left to the sectors asking
    after them, REV-HN1-1's (5a).
- What the kept baseline does not carry: the city's debt and the insurance claims. Neither is in 0.9.146.md, and the
  driver's CSV has no claims by default, hence the paired runs above. On HN1's own runs, ZERO_SLACK's debt ends at a
  median of 0.15 years of GDP [0.00 to 0.67].
- The run went past the 2-hour limit of the shell that started it, so it was stopped at 62 of 80 with
  `tools/ensemble/ensemble.sh --stop HN1` and resumed under the same label ("18 of 80 runs to do", the snapshot kept).
  The helper's first stop left the run's `xargs` (from 09:21), which started five runs at 11:12. A second `--stop`
  ended them (16 processes), and nothing of the run was left before the resume. The helper's header says it stops
  xargs; on this run it did so only at the second call (Pending HN1-C).

## 5. Checks run

- `./batch-check.sh --fast`: exit 0 (compile, maps, Stale, tree).
- After REV-HN1-1's mends: `--fast` exit 0; HousingDoorCheck exit 0 (and exit 4, the take-back line red, with the
  take-back commented out); MortgageCheck 0; CreditCheck 0; the traced default playtest identical to the run before.
- Before them, one at a time, with the data named: HousingDoorCheck exit 0 (40 s), MortgageCheck 0, BuildCardCheck 0, CreditCheck
  0, BankFailureCheck 0, LongPlaytest exit 0 (501 s, "Nothing. Every month passed
  the audit and every reload matched").
- The two traced default playtests above (main and branch), one JVM at a time.
- `sh tools/pick.sh origin/main HEAD`: ALL 221 harnesses (AllChecks.java runs the suite), so the full pick is the dry
  run's. Review: "not needed - no money, saves or threading in the diff".
- The dry run (`/tmp/cbs-gate-HN1-7jjAaTes`, 32138d2b2: main d6844edc4 merged, 0.9.151): the suite ran 221 harnesses in
  2,513 s and the gate stopped, exit 1, on 30 unnamed lines in three harnesses (5a). Each is main's code on HN1's moved
  path, none of them the door's, and each is named in the ship file's `## known-reds`. Judged again with those rows
  (`KnownReds`, the kept `suite.txt`, main's `gate.txt`): "known red only - 12 harnesses", no NEW line. The ship file's
  first line names the run for the gate to reuse.

## 5a. The dry run's three reds, traced

Each was traced on the branch with scratch probes, one JVM at a time. The probes were in `target/hn1/probe`, with an
instrumented copy of `OrderBook` on their own classpath, so the tree was not edited.

**DenominationCheck, a decade on: 8 lines, 3.5e-8 to 1.3e-6 apart against 1e-9.** The plain city and the one reformed
a hundred to one at m121 part first at m179, in the share book.
- This fixture's desk is full (the harness says so itself). So the desk's room is `Exchange.deskLimits()`'s book limit,
  `(BOOK_LIMIT * equity - bookAtFair) / fair`: the difference of two equal money sums.
- That room, in Retail at the end of each month, plain city against reformed one: m176 2.2e-10 against 1.7e-10, m177
  8.2e-10 against 8.0e-10, m178 1.05934e-9 against 6.82e-10. `OrderBook.DUST` is 1e-9 shares.
- In m179, a household's sale of Retail fills the plain desk's bid for exactly 1.0593e-9 shares. It passes the reformed
  desk's by.
- The reformed desk's room then takes 6.3e-9 shares of Real Estate that the plain desk does not, at its bid, and that
  fill becomes the Real Estate book's last price.
- So the household's next ask is 2% apart (0.0044016 against 0.0043145) and Real Estate's shares in issue 3,073,062
  against 3,074,618 by the month's end. Construction's bond buys are 2.7e-9 apart at m181 (201.0278366 against
  201.0278733), and the headline figures 1e-7 a decade on. The fills moved about 4e-11 of money.
- The door is not in it. Real Estate's books agree to 1e-12 through m180, and neither twin writes a mortgage in m181 or m182.
  What HN1 changed is the path: the door trims in this fixture from m124, nearly every month to m137, and on that path
  the desk sits full at the dust. Main's path keeps the twins to 1e-14.
- Queued: **DK1** (the ship file's `## queued`). The desk's room under the arithmetic's resolution of the two sums
  reads as none.

**PriceLevelCheck section 8, the played twin's history: 11 lines and their summary.** Every line is past the twin's
horizon.
- The nudge parts the played twin at 105 of 120 months, and `Child.prefix()` keeps the first 106 entries of every series
  by index.
- The history's lists do not start at month 1. `h.month` runs from m2 to m121, and a company's lists start at its
  listing: Food Processing m62, Mining m43, Manufacturing m17, Industry m10, Luxury Retail and Restaurants m7.
- So index 57 of Food Processing's share price is m119. Mining's 64 to 76 are m107 to m119, Manufacturing's 90 and 92
  are m107 and m109, Industry's and Luxury Retail's 102 and 105 are m109 to m112, and `h.m2`'s 105 is m107.
- The two children were run alone, one at a time (`PriceLevelCheck --twin-played`, at the factor and at today's
  prices). Every share price and share value in the history agrees to 1.8e-16 through m105, and parts from m107 at the
  earliest.
- The class is HP1's third item (`Child.prefix()` keeps the `reload.*` series whole past the horizon). The lines are
  named HP1, whose trim by the month, not the index, covers the history's late-starting lists if HP1's row says so.

**KeptCheck section 63: 4 lines.** WG1-R's fixture lets the fund's rule go for a month, so that its buys settle
holdings the fund did not hold.
- On HN1's path it settled none. The fund held 46 lots before and 43 after.
- Its cash stood over 3 months' transfer in every month watched (0 under), so WG1-R's top-up held, and cash is not why.
- On main's default floor it settled 9 (WG1-R's notes).
- Nothing in the fixture makes a security the fund does not hold exist for the rule to buy: it leans on main's path for
  one.
- Not traced further: the city is KeptCheck's 2,500-month path, 10 to 35 minutes a run to section 63.
- Named **KFP1** (KeptCheck's lines written on its path). Recommended: widen it so the fixture makes that holding exist,
  for instance with one lot sold by hand before the rule is let go.

## 6. Left open, each with a recommendation (the ship file's Pending rows)

- **HN1-A, the plan's price under the door's.** `BusinessInvestment.getCostOf()` prices the materials at
  `getConstructionMaterialPrice()`, and the door at the market draw's import cost, 2-5% higher when the city imports.
  `consider()` raises equity to the plan's down payment exactly, so an order sized at the edge fails at the door even
  for one: seed 12 on today's main with the mend, played to m748, holds one Low-Rise "for the 15% down payment" at
  m267, m356, m376, m700, m704 and m706, all outside the harness's window. Recommended: size the order (and the equity raise) at the door's own
  price, `quoteBuild()` + the land, as the door reads it.
- **HN1-B, the project path's door.** `Game.consider()`'s path for an order with no mortgage is all or nothing at the
  door on the bank's capital too. Recommended: the same halving, once a fixture shows it holding a sector's whole
  order with the till for part of it.

- **HN1-C, the ensemble's stop.** `ensemble.sh --stop` left the run's xargs at its first call (section 4).
  Recommended: `stop.ps1` looks once more after its two passes and says what of the run is left, so a resume never
  starts beside survivors.

## 7. Process

No process file changed. The coding was done in-session, not by a Sonnet slice: the diff is small, and it was tied
closely to the diagnosis's runs. Scratch runs lived in `target/hn1/` with detached checkouts of main for the diagnosis
and of the merge base for the paired ensemble, each removed when its runs ended.
