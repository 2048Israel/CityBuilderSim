# HN1 - households housed before the matching homes finish

A model change: `Game.considerOnMortgage()` (+44 -1) and a new harness, `HousingDoorCheck` (+312), with its line in
`AllChecks` and `docs/tree.md`. The default trace moves (section 4), so the ensemble runs on the final model.

## 1. What was found

**The evidence, rebuilt.** R0h's `target/r0h-homeless/` was gone, so the run was made again: the ensemble's driver
(`tools/ensemble/`, AUTO2 = the autopilot at a 2% target) on R0h's tree 8e09577ab, seed 12, founded and played to
m748 with a probe reading the housing, the landlords' tills, the plan's order and the lender's door every month. It
reproduces R0h's finding: households with no door at m744-747 (3.6, 8.5, 13.2 at the most), zero at 748.

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

On R0h's tree with the mend: no finding in 748 months. At m743 there are 6,560 homes against 3,044, and 28,018 people
against 19,745. Nobody is past the valves.

## 3. The regression: HousingDoorCheck

It plays the ensemble's AUTO2 seed 12 on today's main (the same founding, policy and seed; a scratch city from
`GameFiles.scratch`), watching m430-500 through `Game.watchOrders`'s `mortgaged()` hook. Each month it records the
order the plan passed, the landlord's till, the door's price of one (`quoteBuild(t, 1)` + `LandManager.priceFor`),
whether there is ground for one (`canAllocate`), the family-door shortfall (`RealEstate.doorShortfall`), what was
built and the homes finished by size.

1. The fixture: the plan passed an order the door did not write whole, while the segment was short of doors.
2. No month holds a landlord whose till buys one of the homes the plan passed outright, on ground for one, while its
   segment is short of doors.
3. Every order the door cut is the plan's order halved down to one, and the advisor's line says it was trimmed.
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
- Over the run: the landlords are held for the bank's leverage in 3 months (main 34). They write 551 mortgages (main
  355), 223 owed at the end (main 47). The leverage ratio is the larger requirement in 1,163 months (main 1,412).
- Both find nothing ("Every month passed the audit and every reload matched"). No household is past both valves in any
  month of either (`stillUnplaced` 0 throughout). At its worst, 48,423 of 149,047 households doubled up (m2363), against
  main's 75,584 of 306,178 (m2405).
- The mended city ends smaller: 539,318 people at m2402 against 1,053,251 at m2405. Its exchange rate is 312 at m2400
  (trace-month's `rate`) against 75, and its city debt 34.8T against 8.1T. This is one path, 720 months after a split
  made by two homes. Whether the smaller late city is the mend's doing or the path's is for the 80-run ensemble to say
  (`docs/ensemble/HN1.md`, against the newest versioned baseline).

## 5. Checks run

- `./batch-check.sh --fast`: exit 0 (compile, maps, Stale, tree).
- One at a time, with the data named: HousingDoorCheck exit 0 (40 s), MortgageCheck 0, BuildCardCheck 0, CreditCheck
  0, BankFailureCheck 0, LongPlaytest exit 0 (501 s, "Nothing. Every month passed
  the audit and every reload matched").
- The two traced default playtests above (main and branch), one JVM at a time.
- `sh tools/pick.sh origin/main HEAD`: ALL 221 harnesses (AllChecks.java runs the suite), so the full pick is the dry
  run's. Review: "not needed - no money, saves or threading in the diff".

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

## 7. Process

No process file changed. The coding was done in-session, not by a Sonnet slice: the diff is small, and it was tied
closely to the diagnosis's runs. Scratch runs lived in `target/hn1/` with a detached checkout at `target/hn1-main`,
removed at the end.
