# WN4b - the D$ pegged to the euro or another of the eight (0.9.59, 2026-11-08)

*The batch's record. The brief is WN4's notes, section 3 (the study's XR3 row: "the euro and the eight as
anchors for XR2's peg"), with XR2's peg and XR5's hold as they stand. Lane B of the night line of 2026-11-08,
after CD9, built on main 0.9.57 (1751f426). Opus wrote the design and reviewed each diff; Sonnet 5.5 slices at
xhigh wrote the code (A the model, B the screens).*

## WHERE IT STANDS

Built and checked, ready for the gate. The D$ can be pegged to any of the eight moneys with a rate this month;
under a peg to the US$ (and on every other regime) nothing moves: the default playtest's trace is byte-identical
to main's, so no ensemble. Two slices on main 0.9.57: A the model, the save and `PegAnchorCheck` (b7be3c95), B the
screens (ee108c6d); 12 source files, +951/-94. Five ★ calls, each the recommended way (section 3). Nothing left
for Pending.

## 1. What it builds

- **The peg is kept in its anchor's money** (`ForeignAccounts`, THE REGIMES, THE PEG'S ANCHOR). `pegRate` is D$
  per unit of the anchor, one of the eight (`WorldMoneys.EIGHT`; the US$ by default). `getPegPerAnchor()` is
  that figure, `getPegAnchor()` / `getPegAnchorCode()` the money.
- **The held rate is the peg over the anchor's US$ price**: `getPegRate()` is now the D$ per US$ the peg holds
  this month, `pegRate / usdPer[anchor]`, so under a euro peg the D$ moves against the US$ as the euro does,
  and its price in euros never moves. Every reader that compared the rate with `getPegRate()` stays true.
- **The shadow's gap is against the held rate**: the gap, the hazard, the hold's lift (`interventionLocalFor()`
  of the log of the held rate over free), the break's overshoot, `currencyGap()`, `breakLanding()` and
  `rateAfterDeal()` all read it. The shadow is the D$'s own rule, in D$ per US$: it does not follow the euro, so a
  peg to the euro also holds the euro's swing against the US$.
- **Choosing the anchor** (`setPegAnchor(k)`; Game's `setPegAnchor(code)`, logged): only a money with a rate this
  month. Under a peg in force it re-pegs at the day's cross (the held rate does not move that month, and from the
  next it follows the new anchor), as Denmark's krone moved from the mark to the euro in 1999. Outside a peg it is
  the kept setting, and entering the peg pegs at today's rate in that money. `repeg()` takes the anchor's money.
- **The dial ties to the anchor's rate** (`DebtManager.tieRate()`, `tieMoney()`; Game hands the anchor's code):
  under a peg to the euro the policy rate is the ECB's (PolicyRates, XR4) plus the spread, and the spread's bounds
  are over it. A band stays around the US$ and its aim on the US's rate.
- **The save**: `ForeignAccounts` slot 148 is the anchor's index (149 slots). No `SAVE_FORMAT` bump: an older save
  has none and reads the US$, which is what it was.
- **The screens** (slice B): Policy › Money › The currency's picker of the eight in THE PEG, the peg in the
  anchor's money with today's D$ per US$ under it, the tie's words naming the anchor's rate; Trade's peg lines.

## 2. What moves for a city on the default regime, and under a peg to the US$: nothing

- **The code.** Every changed expression is `pegAnchor == 0 ? the old one : the new one` (the held rate is
  `pegRate` itself, a peg is struck at `rate`, a repeg is `clampRate(newPeg)`), and the tie reads
  `worldRate(tieMoney())`, which is `worldRate()` for null and "USD". Outside a peg `tieMoney()` is null.
- **The trace.** The default playtest ran traced (`-Dplaytest.trace`, 2,400 months, both data files named) on main
  1751f426 and on slice A's tree (b7be3c95), each built apart: all eight CSVs byte-identical, the logs differing in
  their run times alone. Slice B is the screens. So no ensemble (EN1).
- **What a player sees change under a peg to the US$**: the regime's chip reads "Peg" (it was "Peg to the US$"),
  and the decision log's line "Currency regime to Peg to the US dollar" (it was "...Peg to the US$").

## 3. The calls (★; each takes the recommended way, BP1)

1. ★ **The dial ties to the anchor's rate**, not the US's (XR2's ★5 said "the US's" because the only peg was to
   the US$): pegs with open capital accounts take their anchor's rate (the study's R30; Denmark's follows the
   ECB's). **Taken.**
2. ★ **The hold sells from the largest holding**, not from the anchor's money (WN4's ★7, one rule for every sale).
   A real central bank defends in its anchor's money; on the default all-US$ mix the vault holds only dollars
   anyway. **Recommended: keep WN4's rule**; selling the anchor first is one branch in `sellToDefend()`.
3. ★ **Re-anchoring keeps the day's rate** (re-pegged at the cross of the day), so the switch itself costs the
   vault nothing; the D$'s figure in the new money is whatever that cross makes it. **Taken.**
4. ★ **The shadow does not follow the anchor**: it is the D$'s rule in D$ per US$, so a city whose own
   fundamentals do not track the euro pays the euro's own swing (2.0% a month, half-life 3.5 years) to hold a peg
   to it. That is the cost of pegging to a money one does not trade in. **Recommended: keep.**
5. ★ **The thin vault's warning does not count the anchor's swing** (`monthsHoldingGap()` projects a steady gap):
   it stays the upper bound it was. **Recommended: keep.**

## 4. Review hints (money and the central bank: the gate's stranger's review)

1. **`ForeignAccounts.repriceCurrency()`, the PEG branch** (~1090-1125): `held` (`heldRate()`) stands where
   `pegRate` stood in the gap, the lift and the rate. Check that the US$ path is the old expression and that the
   hold's identity (the rate is the shadow plus `holdFelt`) holds for a moving held rate (PegAnchorCheck 3).
2. **`setPegAnchor()`** re-strikes the peg at the day's cross, so the held rate does not move that month and the
   hold buys no lift for the switch itself; from the next month the lift includes the anchor's own move.
3. **The held rate moves every month with the anchor**, so XR8's uncapped hold is easier to reach under a euro peg:
   a euro 10% up against the US$ is a 10% gap the vault pays, whatever the city's own fundamentals do. XR8 stands.
4. **The guards.** `repeg()` clamps the held equivalent at the day's cross; as the anchor moves the held rate can
   later leave `[minRate, maxRate]`, and the reprice then puts the rate at the guard while `holdFelt` takes the lift
   to the unclamped held rate. A US$ peg cannot get there (its held rate is the clamped peg). The guards are far
   from any rate a city reaches.
5. **`restore()`, slot 148**, is read after the eight's rates (slots 113-120). A non-US anchor with no saved rate
   reads the US$ and keeps `pegRate` in the old money's units (only an edited save gets there: `setMoneyRates()`
   never zeroes a rate).
6. **The tie is a month behind the anchor's rate** (found by slice A's city section): Game sets the tied dial
   before it hands the eight's policy rates for the month, so the dial stands over last month's rate. It was so
   for the US$ before this batch; not changed here.
7. **The vault sells from its largest holding** under any anchor (★2): a euro peg on the default all-US$ mix
   defends with dollars.

## 5. The checks

- **`PegAnchorCheck`** (new, 9 sections, 115 assertions): a US$ anchor is the peg as it was, to the bit; the held
  rate is the peg over the anchor's US$ price; the shadow's gap and the hold against the held rate (a euro twin at
  2.0 then 2.2, powers of two so the comparisons hold to the bit); choosing the anchor (only a money with a rate,
  the re-strike at the day's cross); a devaluation in the anchor's money; the break under a euro peg; the tie in a
  bare `DebtManager`; the save (149 slots; a 148-slot save reads the US$); in a city (`InterestAbroadCheck.town`,
  SKIP without the world file).
- **The save**: `SaveFileCheck` asserts slot 148 is `getPegAnchor()`; `ReadPathCheck` reads the new getters;
  `CurrencyRegimeCheck`, `InterestAbroadCheck` and `VaultMoneysCheck` count 149 slots.
- **The trace**: section 2.
- **`--pick`** on the branch (main 0.9.57 unmoved), both data files named: **exit 3**, 47 of 187 harnesses picked
  (`Game.java` left to the gate, `LongPlaytest` in its place); the only red is main's recorded `KeptCheck`; Stale 0
  firm; review needed (money: `DebtManager.java`).

**To look at by eye** (Policy › Money › The currency, under THE PEG): the picker shows only when the world file
gives the eight their rates; it reads PEGGED TO (in force) or TO BE PEGGED TO, the money kept marked. A re-anchor
under a peg in force hides the stepper and shows "Staged: re-peg to ..." with its Apply bar. Under a euro peg the
figure and the stepper are in D$ per €, with today's D$ per US$ below; Trade's peg line adds the anchor's figure.
Under a US$ peg the page reads as before but the chip ("Peg").

## The slices

| slice | what | by | commit |
|---|---|---|---|
| A | the model, the save, the tie, `PegAnchorCheck`, the slot counts | Sonnet 5.5 xhigh | b7be3c95 |
| B | Policy › The currency's picker and the peg's figures in the anchor's money; Trade's peg lines | Sonnet 5.5 xhigh | ee108c6d |

## 6. One in, one out

Not a process batch: no script, check rule or session rule changed.
