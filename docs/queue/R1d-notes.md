# R1d — Research: the 1900 city switched on, with R0d's 1901 wages

A Codex builder built this from main 32869da28 (0.9.123) until its plan
ran out at 02:16 on 2026-11-16 (d5fbfe8ed, and 4b8311a85, its work as it
stood). A Claude session (R1dc) then judged, mended and finished it, from
0a42ef8de on. Main was merged at 504e4251e (0.9.124: FA1, SR1).

## What the batch does

- **The 1900 city is on offer.** The founding screen has a chip pair,
  "1900" and "All fitted", and opens on "All fitted" (★2). A 1900 city has
  the research known as of 1900, the known equipment unfitted, R0d's 1901
  wage ladder, the dated arrivals and basket, and the dated money (R0f) and
  licences. `Game.run()` lays the ladder and basket and unfits the founding
  once, on its first start. A save keeps its own flag
  (`DataSave.getFounding()` reads a missing key as false).
- **The playtest plays it under a flag:** `-Dplaytest.founded=1900`
  (`LongPlaytest.played()`, which `main()`, `probeCity()` and the ensemble
  found on). With the flag unset, the gate's playtest is today's city, as
  on main.
- **The rule waits for the bank.** A 1900 city's central bank opens in
  March 1935 (R0f). The rule a game founds on (0.7.13) waits for it
  (`DebtManager.isAutopilotAtOpening()`) and takes the dial that month,
  unless the player's hand took the Advance Rate (August 1914) first. The
  save's `policyAutopilot` key carries the waiting rule, with no new field.
- **Fittings:** a building scrapped while its fitting is on site, the
  builders credited with what was paid, the fitting's sales tax and the
  owner's credit share, and lead times that count the fitting points
  (★ 3, 4, 9, 10).
- **Steam in the power ratio** (R0c ★8): `Sector.getPowerRatio()`, and the
  readers that value the grid by the electric share only (`SectorFlow`,
  `WorksValue`, `AdviceValueOracle`, through `Sector.perPointOfGrid()`).
- **The electric drive's fitting work**, the **tractor's gain** and the
  **carters' team hire** (★ 6, 7, 8); **1901's wages in the census dollar**
  (★15).

## Design calls

1. ★ **Every founding stays all fitted unless it asks for 1900 by name**
   (revised; the orchestrator took it at 04:23). This covers
   `Founding.defaults()`, every preset and the custom founding, as on main.
   A city is 1900 only through `withUnfitted(true)`: the founding screen's
   chip, the playtest's flag, and the harnesses written for 1900. The
   reason is that the builder made every founding 1900, and the discovery
   dry run at 82ba734c5 then went red in 847 lines across 90 harnesses.
   Each of those cities is written on today's city: wind farms, rail, a
   floating D$, euros in the vault, a desalination plant. A harness city
   keeps the premise it was written on.
2. ★ **The founding screen opens on "All fitted", with "1900" one chip
   away, and the gate's playtest stays today's city** (revised; the
   orchestrator's call A at 04:52: "a stall at 160-470 people for 80 years
   cannot go to players"). The reason is that the 1900 city stalls (see
   *The 1900 city, judged* below). R1g makes it grow and then switches both
   to it.
3. ★ **A building scrapped while its fitting is on site gets no refund, and
   the job is cancelled only when it is orphaned.** A piece's jobs on site
   above the template's unfitted buildings left are cancelled, newest first
   (`Fittings.scrapped()`, called by `BuildingsStacks` once its counts are
   lowered). A job that still has an unfitted building to fit goes on with
   its points. Reason: the fitting was paid and booked at the order, as the
   builders' revenue and the owner's investment that month. Unlike a site's
   CANCEL (a termination under the FAR), there is no unearned balance to
   return. The most a scrap can lose is one fitting job, and only when the
   last unfitted building goes. The builder's first version cancelled the
   oldest job at every scrap, throwing away part-worked points that another
   unfitted building could still take. `FittingCheck` 13 causes both cases
   with a real scrap.
4. ★ **The builders are credited what `treasuryPays()` paid**, not the
   quote. This holds at `fit()`, a restart, a paving and a build order; the
   gap is at most `SHORTFALL_DUST`. `FittingCheck` 14's fixture puts the
   cash inside that gap.
5. ★ **Steam survives a brownout**: only the electric share is multiplied
   by the grid ratio, because a works burning coal in its own engine does
   not wait on the grid. The readers that priced a point of grid by the
   whole value added would have counted the steam share as the grid's, so
   they now take the electric share alone.
6. ★ **The electric drive takes 1 setup point plus 10 points a tonne** of
   motor, shafting and switchgear (`Equipment.DRIVE_WORK_PER_TONNE`). This
   replaces a tenth of the factory's own construction work; the Bakery's
   70 points for 0.3 t is now 4. A balance call for Jerus.
7. ★ **The tractor's gain is the horse form's feed and hands less its
   fuel** (R2-R). It is counted on Agriculture's land-weighted horse share
   and whole posts, and its patent rent on the same comparison.
8. ★ **Carters keep their nameplate sizing, and a team is hired.** It costs
   $300 of 1900 money per pair and wagon (`Haulage.TEAM_CAPITAL_1900`),
   over 120 months, at the fleet's `TARGET_RETURN`, booked as the imported
   service "Horse teams hired". The feed and the carters' wages stay
   separate. Reason: it prices the team without inventing a traded good.
   The builder's figure divided by `PriceLevel.FACTOR` alone, which made it
   about 38.6 times too dear (about $236 a team a month against a carter's
   $35 or so). It now converts as the gold par does,
   `/ HISTORICAL_FACTOR × FACTOR`, at the month's struck rate, and
   `Forms1900Check` asserts it against the constants. The capital and the
   life are balance calls for Jerus.
9. ★ **A fitting is taxed as building work is**: the builders' sales tax is
   in the quote, their suppliers' input credits are netted, and the owner's
   capital purchase is recorded at its recovered share
   (`EconomyManager.taxRecoveredShare()`, as a build order does).
10. ★ **Lead times count the fittings' points**, as a finite priority queue
    ahead of the sites (`BuildingManager`'s priority wait), once at every
    public wait. Foreign crews keep working the sites while local ones fit.
11. ★ **The rule asked for at a 1900 founding waits for the bank** and
    takes the dial when it opens, unless the hand took the dial first.
    Reason: Jerus's 0.7.13 ("not your hand"); a rule dropped at the founding
    would leave a 1900 city's dial to nobody from 1935. `NewGameCheck` 12
    founds the screen's own city with the "1900" chip.
12. ★ **The harness cities are main's again.** This replaces the first
    version's `withUnfitted(false)` on the harness cities written on
    today's city, which call 1 made needless. The fixtures written for 1900 name it:
    `Forms1900Check`, `FittingCheck`'s 1900 villages, `NewGameCheck` 12 and
    `ReadPathCheck`'s own city. Two of main's fixtures assumed a 1900 town
    that R1d now builds differently, and are mended to it:
    - `ResearchViewCheck` 6's town has a Fabrication Shop by month 14
      (Manufacturing, its pieces fitted at completion) and orders its
      Bakery's drive itself (4 points of work, and it pays).
    - `EquipmentCheck` 11's twins buy their drives, so the steam share is
      read after the first month rather than the sixth.
13. ★ **R2e-PE4, read here** (the Cement Plant's rotary-first
    calibration). Every 1900 Cement Plant starts in the partial state,
    rotary known and PE4 not, so the long kiln is refused and earns nothing
    until E2 (6,160 t of coal against 7,106 t with PE4 alone). Recommended,
    as R2e did: define PE4 against the rotary state. The row stays in
    Pending, because it recalibrates R2d's and R1c's figures and R2e's
    controls, which is a batch of its own.
14. ★ **The drive keeps its form's gain** (the steam coal saved) even
    though it is the Industrial Bakery's recipe piece (R1c; `RecipeCheck`
    6, main's text). The builder had sent every recipe piece through the
    recipe comparison. Now only the tractor goes that way, because its
    form's gain is a recipe change.
15. ★ **1901's wages are in the census dollar that the price level
    strikes**: `WageLadder.FOUNDING_PER_CENSUS_DOLLAR` is
    `PriceLevel.FACTOR / HISTORICAL_FACTOR`, exactly 1 in the game.
    `PriceLevelCheck`'s twin at `cbs.priceLevel=1` found the 1901 ladder
    fixed in 1900 dollars, 38.6 times under every other anchor. Nothing
    moves in a played city.

## The 1900 city, judged

The 2,400-month playtest on the 1900 city (run 7, `played()` hard-wired to
1900; that is the founding `-Dplaytest.founded=1900` gives). The control is
the same city on today's wage ladder (`WageLadder.of()` forced to `TODAY`
for one run and then removed). Population:

| month | today's city (main) | the 1900 city | 1900 on today's wages |
|---|---|---|---|
| 60 | 1,674 | 472 | 709 |
| 900 | 23,180 | 159 | 518 |
| 1200 | 24,889 | 1,581 | 379 |
| 2400 | 465,165 | 19,616 | 130 |

GDP at month 2400 was 6.5e9 against 6.7e6. The advisor tried 20 builds in
2,400 months (8 refused for want of money), the construction sector was
idle from month 55, nothing was borrowed before month 534, and
unemployment ran at 32-38% from month 90 to month 240. The control grows
faster for 20 years and then shrinks for good, so 1901's wages alone are
not the cause. R0d-R's "grows slower" is a stall over 2,400 months.
Queued as **R1g** (`docs/queue/R1g.md`), whose done is the playtest
growing on 1900, then the screen and the playtest switched to it, with the
ensemble.

While every founding was 1900, the harnesses that borrow the playtest's
founding found three things a 1900 city does that no harness asserts. The
D$'s six moves do not add up to the month's move, and the 1900 trace drops
33.8% in the month of the August 1914 suspension (month 176). A K-scaled
1900 city drifts 3-15% from K times the city in six months. `ScaleCheck`'s posts identity leaves out
R0c's horse hands. Queued as **R1f** (`docs/queue/R1f.md`).

The playtest's report counted the refiners by template index, and the
count ran past the list at month 157 once a 1900 city's research added
templates. It now counts them by id (`LongPlaytest.countRefiners`).

## Calls for Jerus

- The drive's 10 points a tonne (★6).
- The team's $300 (1900) and 120 months (★8).
- How fast the 1900 city must grow before the screen opens on it (R1g's
  done; his call, with its figures).

## Pending rows "For R1d"

- **R0e-R:** the year's basket is active for a 1900 city and drawn on
  People's household money page (its ★8). Still open: firewood (there is no
  such good) and the billing of the heat and light that replace it; the
  lamp and coal quantities and the present-year end stay as they are.
- **R0d-R:** judged on the 1900 playtest: the city stalls (above; R1g).
  Its estimates (★2-★6) stay as they are, since the stall is not the ladder
  alone (the control).
- **R2-R:** done (★7).
- **R1c-R:** the binder is on main (R2d); the X-ray and antitoxin rows are
  R2f's; `PULP` is still one good. The weakest estimates stay unmeasured,
  because the 1900 city builds too little to read them (R1g).
- **R1b-R:** the founding option (★2), the fitting's tax (★9) and the lead
  times (★10) are done. The shell price stays open: equipment is chosen at
  completion while the order is priced at the quote, so a discount for an
  absent piece could charge for one plant and deliver another. Queued as
  R1e (`docs/queue/R1e.md`), after QN1, whose proposal value it extends.
- **R2e-PE4:** read (★13); it stays in Pending.

## Review

The builder's Codex reviewer read the diff at d5fbfe8e with its working
changes (fingerprint 173e918d82a9c624fb14c5d893644dfd745cab41). Its two
findings (the tractor's horse share; the fitting waits) were mended, and a
proposed PE14 royalty finding was withdrawn (PE14 has `fee:false`).

REV-R1d-1 (the orchestrator's stranger, at 82ba734c5) found money, saves,
month order and threading clean. Its findings were mended as follows:

1. `SaveFileCheck`'s virgin city against `reused.newGame()`: the file is
   main's again, so both are `Founding.defaults()`, one founding.
2. `EquipmentCheck` 11's `holdAll()` is dropped. The twins are still equal
   to the bit with the investment round on; the steam share is read after
   the first month (★12).
3. `WageLadderCheck`, `ArrivalMixCheck` and `BasketYearCheck` are main's
   again: `DEFAULT` is today's founding, so their labels are true, and the
   1900 city is `THE_1900_WAY` as on main.
- Minor: `SaveFileCheck`'s switches went with the revert. The "everything"
  city round-trips false, its scaffold's default, as on main; the flag's
  true round trip is `FittingCheck` 3's ("...and loaded: the flag is
  back") and `WageLadderCheck`'s.

REV-R1d-2 reads the delta from 82ba734c5.

## Checks

Named runs on the final approach: 18 harnesses with `LongPlaytest` and its
trace (exit 3, known red only), then 15 of the discovery run's unnamed
reds (`WaterCheck`, `PartnersCheck`, `VaultMoneysCheck`, `BuildCardCheck`,
`ResearchViewCheck`, `CentralBankCheck` and others), green but for
`ResearchViewCheck` 6, which is now mended and green (★12).

## The playtest and the ensemble

The gate's playtest is today's city, as on main; its trace should stay
main's (the final dry run checks this). There is no ensemble in R1d (the
orchestrator, 04:52): the ensemble on 1900 moves to R1g.

## The dry runs

Discovery, at 82ba734c5 with every founding 1900: exit 1, 847 lines in 90
harnesses, 1,821 s (`cbs-gate-R1d-rM7GeEb2`). This led to call 1's
revision.

## Process lines

None of the process files moved; `tools/ensemble/Ensemble.java` changed
+1 -1 (the ensemble founds on `played()`).
