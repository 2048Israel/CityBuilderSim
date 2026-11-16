# R1d — Research: the 1900 city switched on, with R0d's 1901 wages

Built from main 32869da28 (0.9.123) by a Codex builder until its plan ran
out at 02:16 on 2026-11-16 (d5fbfe8ed, and 4b8311a85, its work as it stood),
then judged, mended and finished by a Claude session (R1dc): 0a42ef8de and
after. Main merged at 504e4251e (0.9.124: FA1, SR1).

## What the batch does

- **The 1900 city is the default.** `Founding`'s `unfitted` is true for
  every founding that does not say otherwise: research known as of 1900,
  the known equipment unfitted, R0d's 1901 wage ladder, the dated arrivals
  and basket, the dated money (R0f) and licences. `Game.run()` lays the
  ladder and basket and unfits the founding once, on its first start.
  The founding screen has a chip pair, "1900" and "All fitted"; a save keeps
  its own flag (`DataSave.getFounding()` reads a missing key as false).
- **The rule waits for the bank.** A 1900 city's central bank opens in
  March 1935 (R0f): the rule a game founds on (0.7.13) waits for it
  (`DebtManager.isAutopilotAtOpening()`) and takes the dial that month,
  unless the player's hand took the Advance Rate (August 1914) first. The
  save's `policyAutopilot` key carries the waiting rule; no new field.
- **Fittings:** a building scrapped while its fitting is on site, the
  builders credited what was paid, the fitting's sales tax and the owner's
  credit share, lead times that count the fitting points (★ 3, 4, 9, 10).
- **Steam in the power ratio** (R0c ★8): `Sector.getPowerRatio()`, and the
  readers that value the grid by the electric share only (`SectorFlow`,
  `WorksValue`, `AdviceValueOracle`, through `Sector.perPointOfGrid()`).
- **The electric drive's fitting work**, the **tractor's gain** and the
  **carters' team hire** (★ 6, 7, 8).

## Design calls

1. ★ **The 1900 founding is the default**, for the player and the playtest
   alike. Reason: the brief ("switched on"), and the played path is the one
   that must exercise research; "All fitted" stays one chip away for a
   player, and a harness whose premise is today's city says so
   (`Founding.defaults().withUnfitted(false)`, ★12).
2. ★ **"1900" and "All fitted" on the founding screen** (R1b ★5), 1900
   chosen. Reason: a player who wants today's equipment from month 1 has it
   without a different game.
3. ★ **A building scrapped while its fitting is on site: no refund, and the
   job is cancelled only when it is orphaned.** A piece's jobs on site above
   the template's unfitted buildings left are cancelled, the newest first
   (`Fittings.scrapped()`, called by `BuildingsStacks` once its counts are
   lowered); a job that still has an unfitted building to fit goes on with
   its points. Reason: the fitting was paid and booked at the order, the
   builders' revenue and the owner's investment that month, so unlike a
   site's CANCEL (a termination under the FAR) there is no unearned balance
   to return; the most a scrap loses is one fitting a job, and only when the
   last unfitted building goes. The builder's first version cancelled the
   oldest job at every scrap, throwing away part-worked points another
   unfitted building could still take. `FittingCheck` 13 causes both cases
   with a real scrap.
4. ★ **The builders are credited what `treasuryPays()` paid**, not the
   quote, at `fit()`, a restart, a paving and a build order; the gap is at
   most `SHORTFALL_DUST`. `FittingCheck` 14's fixture puts the cash inside
   that gap.
5. ★ **Steam survives a brownout**: only the electric share is multiplied by
   the grid ratio. Reason: a works burning coal in its own engine does not
   wait on the grid. The readers that priced a point of grid by the whole
   value added would have counted the steam share as the grid's; they now
   take the electric share alone.
6. ★ **Electric drive: 1 setup point plus 10 points a tonne** of motor,
   shafting and switchgear (`Equipment.DRIVE_WORK_PER_TONNE`), in place of a
   tenth of the factory's own construction work (the Bakery's 70 points for
   0.3 t is now 4). A balance call for Jerus.
7. ★ **The tractor's gain is the horse form's feed and hands less its
   fuel** (R2-R), counted on Agriculture's land-weighted horse share and
   whole posts, and its patent rent on the same comparison.
8. ★ **Carters keep their nameplate sizing; a team is hired.** $300 of 1900
   money a pair and wagon (`Haulage.TEAM_CAPITAL_1900`), over 120 months,
   at the fleet's `TARGET_RETURN`, booked as the imported service "Horse
   teams hired"; the feed and the carters' wages stay apart. Reason: it
   prices the team without inventing a traded good. The builder's figure
   divided by `PriceLevel.FACTOR` alone, about 38.6 times too dear (≈ $236 a
   team a month against a carter's ≈ $35); it now converts as the gold par
   does, `/ HISTORICAL_FACTOR × FACTOR`, at the month's struck rate, and
   `Forms1900Check` asserts it against the constants. The capital and the
   life are balance calls for Jerus.
9. ★ **A fitting is taxed as building work is**: the builders' sales tax in
   the quote, their suppliers' input credits netted, and the owner's
   capital purchase recorded at its recovered share
   (`EconomyManager.taxRecoveredShare()`, as a build order does).
10. ★ **Lead times count the fittings' points** as a finite priority queue
    ahead of the sites (`BuildingManager`'s priority wait), once at every
    public wait; foreign crews keep working the sites while local ones fit.
11. ★ **The rule asked for at a 1900 founding waits for the bank** and takes
    the dial when it opens, unless the hand took the dial first. Reason:
    Jerus's 0.7.13 ("not your hand"); a rule dropped at the founding would
    leave a 1900 city's dial to nobody from 1935. `NewGameCheck` 12.
12. ★ **Harness cities whose premise is today's city are founded so**, with
    `withUnfitted(false)` and the reason on the line: wind farms, rail,
    insured mortgages, a floating rate, Industrial Bakeries, Walk-in Clinics
    and Bus Networks all come after 1900. Their assertions are unchanged.
    The fixtures built for the 1900 path (`Forms1900Check`, `FittingCheck`,
    `ReadPathCheck`'s own city, the playtest) keep it.
13. ★ **R2e-PE4, read here** (the Cement Plant's rotary-first calibration):
    every 1900 Cement Plant starts in the partial state, rotary known and
    PE4 not, so the long kiln is refused and earns nothing until E2 (6,160 t
    of coal against 7,106 t with PE4 alone). Recommended, as R2e did:
    define PE4 against the rotary state. The row stays in Pending: it
    recalibrates R2d's and R1c's figures and R2e's controls, a batch of its
    own.

## Calls for Jerus

- The drive's 10 points a tonne (★6).
- The team's $300 (1900) and 120 months (★8).

## Pending rows "For R1d"

- **R0e-R:** the year's basket is active and drawn on People's household
  money page (its ★8). Open: firewood (no good) and the billed replacement
  heat and light; the lamp and coal quantities and the present-year end
  stay.
- **R0d-R:** the 1901 ladder and dated arrivals are on the played path. Its
  estimates (★2-★6) stay: the switch gives no new observation of them. The
  growth and the lab are read on the playtest's 1900 city below.
- **R2-R:** done (★7).
- **R1c-R:** the binder is on main (R2d); the X-ray and antitoxin rows are
  R2f's; `PULP` is still one good. The weakest estimates stay, unmeasured.
- **R1b-R:** the founding option (★2), the fitting's tax (★9) and the lead
  times (★10) are done. The shell price stays open: equipment is chosen at
  completion while the order is priced at the quote, so a discount for an
  absent piece could charge for one plant and deliver another. Queued as
  R1e (`docs/queue/R1e.md`), after QN1, whose proposal value it extends.

## Checks

Named runs, exit 0: `FittingCheck`, `HaulageCheck`, `Forms1900Check`,
`EquipmentCheck`, `SectorFlowCheck`; then `NewGameCheck`, `SaveFileCheck`,
`ReadPathCheck`, `CurrencyRegimeCheck`, `ConstructionTrapCheck`,
`DeathCareFirstCheck`, `ChildcareCheck`, `EssentialWorksCheck`.
`BuildAdviceCheck`, `WorksFinanceCheck` and `BuildersFirstCheck` were red on
the 1900 default (their cities order Walk-in Clinics, Light Rail and
founding depots in a modern town); their fixture cities are today's now
(★12).

## Review

The builder's Codex reviewer read the diff at d5fbfe8e with its working
changes (fingerprint 173e918d82a9c624fb14c5d893644dfd745cab41): its two
findings (the tractor's horse share; the fitting waits) were mended, and a
proposed PE14 royalty finding withdrawn (PE14 has `fee:false`). Money and
saves moved again since (the team hire, the owner's credit share, the
waiting rule's save key), so the gate's stranger's review is wanted.

## The dry run

(to come)
