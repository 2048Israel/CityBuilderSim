# R1b - research: the fit acted on ({next}, 2026-11-05)

## WHERE IT STANDS

**Ready.** Branch `batch/R1b` from `batch/DT1`'s ship file (RL1); main with
DT1 in (0.9.20, 239a5e60) merged at 584a3027. Slice A is 00bfd107, slice B
f64b5916. `--pick` exit 3 and the full suite main's recorded reds only (§4).
The default playtest is main's to the byte but for its run times, so no
ensemble. What R1d takes from here is R1b-R in the ship file's `## pending`.

## 1. What R1b is

R1-notes §1's second part, with R1's stranger's review (2026-11-05):

- **the fitting ordered**: its goods bought at the city's market and its
  work from the builders, one order at a time, ranked with the sector's new
  buildings in `SpreadPlanner.best()` (research-design §2.2 step 5);
- **new buildings with every piece that pays** (step 6);
- **the Fit action** on a business's buildings;
- **a city founded the 1900 way**, its pieces unfitted, off by default;
- **the review's two**: the shares at rest (an unbroken run and a reloaded
  twin read the same shares at the next press), and `EquipmentForms.driven`
  cleared on New Game and a load.

Nothing moves in a city founded the default way: no building is unfitted, so
no fitting is ranked, ordered or worked, and every new path reads nothing.

How it runs, in a city founded the 1900 way (`Founding.withUnfitted(true)`):

- **The order.** Each month, after a sector's `plan()`, `EquipmentFit.choose()`
  weighs its building (one, at the investors' estimate on its cost) against
  its best fitting that PAYS (R1's verdict, `rank()`), both as
  `SpreadPlanner.Candidate`s in `SpreadPlanner.best()`, the building first so
  a tie keeps it. The fitting is the month's decision when it earns more on
  its cost, or when the plan builds nothing. A sector with a fitting on site
  orders no second.
- **The money and the goods.** `Game.fit()`: the goods drawn at the city's
  market by the builders (local makers' stock first, then imports, as a
  road's bitumen), the work at the builders' price a point; the payer pays
  both (cash, then borrowing the shortfall, asked before anything is drawn);
  the builders earn it (`Construction.earnFitting()`), on the national
  accounts' investment line once. A depot's motor trucks go into Freight's
  fleet.
- **The work.** A job on site (`Fittings`, saved), worked by the builders'
  crews after the repairs and before the sites (`Game.workTheFittings()`,
  where the month strikes the sites' output); the building counts as fitted
  in the month its points are done, and the forms follow at once.
- **New buildings.** A building that finishes comes without each piece whose
  verdict for a new building is not PAYS (`EquipmentFit.verdictForNew()`,
  `Game.fitWhatFinished()`).
- **The Fit action.** `Game.fitForCity()`: the city pays (the treasury, which
  does not borrow for it) for one building of a business's stack, whatever
  the verdict; a pill on the business's Investors page, "ITS BUILDINGS".

## 2. The calls (★)

1. **★ A fitting is the builders' job, worked before it counts.** Ordered and
   paid at once, its goods drawn at the order, its work worked off the top of
   the builders' month after the repairs and before the sites, first ordered
   first; the count falls when its points are done. Points billed to the
   builders with no site would sit in their backlog for ever (their crews
   work only what a stack owes), so a fitting is its own short queue, like
   the repairs. The builders book the whole contract at the order (their
   revenue named "Equipment fitted"), a month or more before the work on a
   large piece is done. Recommended and taken.
2. **★ One fitting on site per sector at a time.** Research-design §2.2's
   "the sector fits one at a time", which is what makes diffusion slow
   without a curve; a building order has had no such rule since BA1. The
   player's Fit is not limited. Recommended and taken.
3. **★ What a new building comes with is decided at its finish, not its
   order, and its order costs the template's whole cost.** The fitting is the
   last thing put in a plant, and at the finish no state on site is needed
   (nothing saved, no shell to carry it). A building that comes without a
   piece still paid for one; R1d, which judges the estimates on a 1900 city,
   may price a building without its pieces. Recommended and taken.
4. **★ A city founded the 1900 way has every piece unfitted, the tractor's
   too.** The row says "the known pieces"; research-1900 §3's known-in-1900
   pieces (the drive, the motor truck) start unfitted, and so does the
   tractor, which a 1900 farm cannot have before its discovery: NOT_KNOWN,
   never paying in this build, so farms stay on horses until R2's catalogue
   reads PE14. Recommended and taken.
5. **★ No founding screen yet.** `Founding.withUnfitted()` is a flag, saved
   with the founding (`DataSave.foundedUnfitted`, a key only when on); R1d
   puts it on the founding screen when it switches it on. Recommended.
6. **★ No sales tax on a fitting** in this batch (a building's contract
   carries the builders' sales tax; a fitting's does not). Recommended: R1d
   or a tax batch, with the fitting's cost on the ledger it belongs to.
7. **★ The planners' lead times do not count the fittings**
   (`getBuildingOutputAtEveryPost()`, `orderWaitOutput()`): a fitting's
   points delay the sites, and the order wait a planner reads is the sites'
   output without them. Small beside the sites in any city; left for R1d to
   measure on a 1900 city. Recommended.

## 3. Slices

| slice | what | state |
|---|---|---|
| A | the review's two; `Founding.withUnfitted()`; new buildings' pieces at the finish; `FittingCheck` 1-4 | 00bfd107 |
| B | `Fittings` (the jobs on site), `Game.fit()`, the work off the builders' month, the round's choice, the player's Fit and its pill, the save; `FittingCheck` 5-12 | f64b5916 |

Both drafted by Sonnet 5.5 at xhigh, reviewed here. What the slices found
and the reviews changed:

- **The shares at rest go right after the sites advance**, not at the end
  of `Game.nextMonth()` as the brief said: the month derives the city's power
  demand and the sectors' grid draw from the shares after the advance, and
  the load re-derives them from the counts at rest; said at the month's end,
  the twins' power demand differed (2,734 against 2,748 units) and the
  treasury parted within two months. `FittingCheck` 1 fails nine assertions
  without the line.
- **New buildings read the month's completions**, not each stack's
  `getLastFinished()`, which keeps last month's figure in a month that
  advances no site.
- **The money is asked on the market's exact quote** (`Markets.quote()`, the
  split a draw makes) before any good is drawn; the city pays through its
  journal (`treasuryPays(BUILDINGS)`, its capital spending), as its own
  buildings do.
- **Review: a plan that held, put aside for a fitting, still says what held
  it** (the posts it wanted priced, the ground it wanted heard by the land
  office), in `runPrivateInvestment()`; the slice's draft dropped both.
- **Review: the fleet's first seed tops up what a fitting delivered**
  (`Freight.seedTrucks()` takes the larger of the two): a 1900 city may fit a
  depot's trucks while every depot is on teams and the fleet has never been
  seeded, and the seed replaced the trucks held.
- **`ui/` lines touched** (for KP4, which keeps the Sectors pages):
  `SectorScreen.buildingsList()`, three lines adding the pill in the row's
  "you have N" cell, and the new `fitPill()` (about 28 lines) after it; no
  CSS. Checked by compile only: no city a player founds has a building
  unfitted, so the pill never shows until R1d.

## 4. Checks

- **`--pick`** on the tree before DT1 landed (773df2d8; 69 of 176 harnesses
  and LongPlaytest, for 53 files changed since DT1's tip), the gate's world
  files named: **exit 3**, "known red only - CentralBankCheck, ScaleCheck,
  MapCheck" (`docs/notes/gate.txt`); two timing lines SOFT (SaveSlotCheck 1,
  MapCheck 1). `pick.sh` asks for the gate's stranger's review of the saves
  (`DataSave`: `foundedUnfitted`, `fittings`; no format bump, each a key only
  where it is so).
- **The full suite** (`AllChecks -f`) on the merged tree (584a3027): 172 of
  176 green; the four red are main's recorded ones, every line `[known]`
  (CentralBankCheck 21, DenominationCheck's decade, ScaleCheck, MapCheck 2d);
  three timing lines SOFT. `FittingCheck` (new, 12 sections) green.
- **The trace.** The default playtest (2,405 months) traced
  (`-Dplaytest.trace`) on main (239a5e60, built apart) and on this branch,
  back to back: all eight CSVs byte-identical, the log identical but for
  "in 45s"/"in 46s" and the layout windows' "1.6 s"/"1.7 s". **No ensemble**
  (batch.md 6): no city a player founds has a building unfitted, so no
  fitting is ranked, ordered or worked.
- **Not harnessed:** the review's fix of the holds a fitting puts aside (§3),
  which only a 1900 city can reach (the land office and the wanted posts hear
  what they heard before the fitting existed); the Fit pill, checked by
  compile only (§3; eye, not pending).
- Not a process batch: no scripts, checks' machinery or rules changed.
