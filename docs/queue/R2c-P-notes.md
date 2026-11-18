# R2c-P — Public works' licence fees

Built from main c17b92f0f (0.9.148) on batch/R2c-P. Initial --fast on the
unchanged branch: exit 0 (target/checks/fast0.txt).

## What the catalogue holds (found)

Payable entries (fee true) on the city's own works, from buildings.json and
research.json:

| work | entry | kind | term (history's month + 240) |
|---|---|---|---|
| Coal Power Plant | PE6 steam turbine, PE16 pulverised coal | fitted pieces with production recipe changes | 1903-10..1923-10, 1918-06..1938-06 |
| Water Treatment Plant | PE5 rapid sand | fitted piece, production recipe change | 1902-06..1922-06 |
| Wind Farm | E16 | the building | 1980-12..2000-12 |
| Desalination Plant | E6 | the building | 1957-06..1977-06 |

No berth uses a payable entry: the Container Terminal's E5 is fee false, the
Bulk Terminal's Hulett piece and recipes date "1900" (not a catalogue code),
the other three terminals carry none. No city work runs a payable recipe.
Outside this scope: the bank's branches (Commercial Bank, owned by no sector
and not the city's: B1/B3 pieces, fee true) pay nothing under R2c or here;
the medical rows (drugs and devices, many fee true) move health constants
and have no money gain in the model.

## Design calls (★, the recommended way; orchestrator told)

- ★ Who pays: a city founded the 1900 way; its own works are the standing
  stacks no sector owns, the bank's branch excepted (Game.isCitysToDemolish).
  Berths are among them. Entries: the template's own building entry, and each
  piece fitted on at least one building of the stack; each code once a stack.
  LicenceFees.payable() and onGain() decide, R2c's SHARE and TERM_MONTHS.
- ★ The public gain is the service delivered, at the tariff the planners
  price it at (Recipes.pricesOf: pricePerWatt, pricePerWaterUnit): delivered
  = min(demand, output) at this month's staffing, demand and fresh-water cap,
  output by UtilitiesHandler's own rule. An entry's gain is delivered with it
  less delivered without it (a piece: every building of the stack lacking it,
  RecipeChanges' own counterfactual; a building: the stack gone, less its
  running cost by BuildAdvice's reading). Surplus capacity adds nothing.
- ★ A berth's gain is the port's public saving and never Freight's: no berth
  entry is payable, so it is not valued; the harness asserts that premise and
  that Freight carries no IP charge, so a future patented berth entry fails it.
- ★ Capture at production, Game.finalUpdateEconomy right after the market
  pass (economyManager.finalEconUpdate), the utilities holding the month's
  output; the accrual month's patent window decides (R2c's ruling). Paid at
  the next strike in startOfMonthUpdate beside settleReserve, through
  treasuryPays(CITY_SERVICES) (a promise: the service is used; no new
  TreasuryLine, whose last-constant premises DiscoveryCheck and PrizeCheck
  hold), inside the MoneyAudit window: "- city IP charges" in TRADE, so
  ForeignAccounts takes it in the same month; the national accounts carry it
  as a budget line ("Patent licences", before Research, whose last place
  KeptCheck holds), as government services at cost and as imports.
- ★ Saved: pending and paid in DataSave and the load path, the two series in
  HistorySave with YearBook flow rules (R2c's YearBook lesson), the budget line
  in the government array (39 -> 40, 39 still read). No SAVE_FORMAT change:
  an old save reads zero, the bill it never accrued.
- ★ Planners net the fee: the public fitting verdict (RecipeChanges.feesAt,
  closed to city works by R2c) charges city works' pieces; a new patented
  public building's running cost (BuildAdvice.runningCostAt, what WorksValue
  and the auto-builder read) adds SHARE of its positive prospective gain.
  HistoricalLicenceCheck's R2d "keeps the whole gross gain" assertion was the
  placeholder for this batch and now asserts the net.

The orchestrator (20261118-074859) kept calls (1)-(3) and asked for three
things, all done: the capture and the payment in the month's one sequence
(Game.nextMonth() is the only caller of startOfMonthUpdate(),
SimulationEngine.simulateMonth() and finalUpdateEconomy(); PublicLicenceCheck
8 plays the month live and loaded), a fixture that saves between the capture
and the payment (8, and saveChecks()), and the out-of-scope rows as one call
row in the ship file's pending (R2c-P-C below).

## What was built (f40c29bc3 the model, 0c9bab7b1 the harness)

- LicenceFees, THE CITY'S OWN WORKS: publicGain(game, stack, piece) is the
  tariff times delivered with less delivered without (min(demand, output) by
  UtilitiesHandler's rules: updateEnergyRatio()'s and waterOutput()); a
  building entry also subtracts the stack's BuildAdvice.rawRunningCostAt().
  capturePublic() sums onGain() over the payable entries of the standing city
  stacks; forNewWork() is one more utility's licence for the planners.
  RecipeChanges.productionWithout/productionFor are apply()'s own sum at given
  absences.
- Game: captured in finalUpdateEconomy() after the utilities' update and their
  prices (the at-rest reading PublicLicenceCheck recomputes equals it to the
  bit); paid by settlePublicLicences() at the strike right after
  settleReserve(), treasuryPays(CITY_SERVICES), cleared as paid. Government
  services at cost add the payment, so GDP is unchanged.
- MoneyAudit "- city IP charges" (TRADE, so ForeignAccounts takes it the same
  month); EconomyManager adds it to the raw imports; NationalAccounts carries
  it in getTotalExpenses() and the government array's slot 39 (40 long, every
  older length still read); GovernmentScreen's "Patent licences" row sits
  before Research.
- Saved: DataSave publicIpPending/publicIpPaid (null when 0, so a city owing
  none writes no key), both load sites, the reset, the redenomination;
  HistorySave "publicIpCharges"/"publicIpAccrued" with YearBook flow rules;
  the load path sets the economy's licence back from the accounts block, as it
  does the reserve's lines (a refresh before the next month struck it as
  nothing otherwise; saveChecks() holds it). No SAVE_FORMAT change: an old save
  reads 0, the bill it never accrued.
- Planners: RecipeChanges.feesAt() is open to the city's works (the bank's
  branch still pays none), so the public fitting verdict nets SHARE;
  BuildAdvice.runningCostAt() is raw plus forNewWork() (utilities only; the
  housing parts untouched, HN1's).

## The harness

PublicLicenceCheck (48 assertions, about 6 s; in AllChecks after
LicenceFeesCheck; saveChecks()/readChecks() from SaveFileCheck and
ReadPathCheck): 1 the catalogue's premise (every payable entry on a city
work sits on a power or water work; no berth carries one; E5 unpatented);
2 rapid sand (PE5) on the water works in a shortage the Pulp Mills cause:
the bill is SHARE x tariff x the water the piece adds, worked independently
(demand 12,860.9, with 12,860.9, without 2,541.49; fee 3.338775576 = pending),
paid at the next strike to the bit, in the closing audit, the foreign
accounts, the budget line, the imports, the history; no firm bill; 3 expiry
at worldMonth + TERM_MONTHS (the capture reads the month after the clock
moves; the last month's accrual is paid, then nothing); 4 a local find pays
nothing where the same works imported paid; 5 a Container Terminal (E5,
unpatented) adds nothing beside a patented desalination plant, Freight none;
6 no gain no fee: the coal plant's PE6 in surplus pays nothing, and pays its
share once Hydrogen Steel Works make the grid short; a desalination plant
serving no demand has a negative gain and pays nothing, and pays SHARE of its
gain over running cost once the city needs its water (there is no tariff
lever: UtilitiesHandler fixes both prices); 7 the planners net the fee the
capture would strike (REV-R2c-P-1, below; 7b the term, 7c the units, 7d
the auto-builder's costs); 8 the month both ways; 9 a city founded today
pays nothing and writes no key.

HistoricalLicenceCheck's R2d section "the public fitting estimate keeps the
whole gross gain" was this batch's placeholder (its title named R2c-P); it
now asserts that the verdict nets exactly the bill the capture would strike
on the new building's piece (gross less billOf() with the piece less
without), printing whether the town is short or in surplus.

Each mend removed, PublicLicenceCheck's failing assertions ("fixture:"
checks not counted): capture 19, payment call 13, audit line 1, imports
line 1, the save's two setters 5, feesAt's guard put back 1, runningCostAt
raw 1, the load path's licence line 1, the getTotalExpenses() term 1. Every
assertion but the guards of sections 1 and 9 fails under at least one.

## The review's mends (REV-R2c-P-1: SHIP, no P1; three P2s)

The reviewer's three planner faults are the brief's own "the public planners
count the gain less the fee". They are mended in one place:
LicenceFees.billOf(game, t, Fitting, running, atMonth, overLife), the bill
of one city utility in one state of its fitting. Fitting.standing is the
stack with given unfitted counts, its own state moving the grid nothing.
Fitting.added is n new buildings beside the city as it stands. The capture
(capturePublic(), publicBillOf(stack)), the public fitting verdict
(RecipeChanges.feesAt()) and the planners' licence (forNewWork()) all read
billOf(), so each bills what the month would. capturePublic() is unchanged
to the bit (section 2's fee 3.338775576 still passes). productionWithout()
and productionFor() had no caller left and are gone.

- P2-1, the verdict's fee is the bill: feesAt() for a city work is billOf()
  in the verdict's state (standing for a refit, one new building for a set),
  on delivered water or power, not on the nameplate gain. In surplus the
  verdict keeps its whole gross: PE6 in 6a's town, and PE5 in section 2's
  served city. In the shortage of waterWorks(..., true), the verdict is the
  gross less the fee worked independently (1.5050, under SHARE x gross).
- P2-2, weighted by the term left: forNewWork() is what running() adds, and
  running() is read as a month on average over a life. Each entry's fee is
  weighted by lifeShare(), the annuity over min(LIFE_MONTHS, term left)
  divided by the annuity over LIFE_MONTHS at lifeFactor()'s rate. That share
  is 0.99383 at 238 months left, 0.03270 at 6 and 0 past the end.
  BuildAdvice.runningChange(), WorksValue's per-interval cost, now charges
  each order the bill of its n units together, at the month WorksValue
  reads (its Ahead.months()), with no weight over a life. One plant is
  charged the fee at 0, 3 and 5 months ahead with 6 months left, and nothing
  at 6, 9 and 30. Three plants in smallShortage() are charged 2.0325, the
  independent bill of three, against 6.5981 for three times one. WorksValue
  takes the month as an interval's middle: power's intervals are one month,
  water's twelve, so a water order's licence ends within six months of the
  patent's end.
- P2-3, it falls out of 1, so it is mended: AutoBuilder.costs() (that method
  only; the city founded today keeps its old line) takes out q x (running()
  less licenceOf()) and the stack's own publicBillOf(), the licence the
  spend holds, not q x one more's fee. In section 2's served city the bill is
  3.0408 (the month's pending bill) while one more's licence is 0. costs()
  is 0.020559 a resident, 0.022311 by the old line. In play today, costs()
  is read only by payback(), and payback() skips the essential measures,
  POWER and WATER among them, the only measures that serve a utility. So
  this changes no auto-builder decision now; it makes the figure true for
  any caller.
- Checked and left alone: the life-cost paths (`running x lifeFactor`:
  lifetime(), pavingLifetime(), orderLife(), pavingOrderLife()) price roads
  and lines, and orderLife() has no caller outside the harnesses.
  payback()'s n x running() never sees a utility (above). A negative change
  in runningChange() keeps the old line, and WorksValue never makes one: its
  `with` is `ready` plus the units.

Each mend removed, PublicLicenceCheck's failing assertions: M10 (feesAt back
to the nameplate path) 3, M11 (forNewWork's weight 1) 3, M12 (runningChange's
month ahead ignored) 1, M13 (n times one plant's fee) 1, M14 (costs() back to
q x running) 2. PublicLicenceCheck is now 67 assertions, about 6 s.

## Checks

- ./batch-check.sh --fast: exit 0 (compile, maps, 0 firm Stale).
- One at a time: PublicLicenceCheck, LicenceFeesCheck, HistoricalLicenceCheck,
  SaveFileCheck, ReadPathCheck, YearBookCheck, DiscoveryCheck, GdpCheck,
  TreasuryCheck, DebtExchangeCheck, FloatLinkCheck, BuybackCheck exit 0;
  PrizeCheck exit 1 on main's own red only (gate.txt's exact line, "never
  stood above the six months of costs it keeps", R3b-R in known-reds).
- pick: ALL (AllChecks changed); review needed (saves).
- After REV-R2c-P-1's mends: --fast exit 0 (0 firm Stale, maps regenerated);
  one at a time, PublicLicenceCheck (67), HistoricalLicenceCheck (80),
  LicenceFeesCheck (43), SaveFileCheck, ReadPathCheck and AutoBuildCheck
  exit 0.
- KeptCheck alone (865 s, beside WC1's dry run): exit 9, nine reds. Eight are
  its timing limits ("within KEPT_*_MS": chrome, Build Overview,
  construction, Sectors, Bank's Lending, Borrow at home, fund Activity,
  Government Overview) on a loaded machine; KEPT_ALL_CHROME_MS is KS1-rest's
  in known-reds. The ninth is section 63's "fixture: the rule held again after
  its month, no lot but the ones it settled (43 lots, then 138; new
  [S:Freight])". KeptCheck's city is LongPlaytest.probeCity, founded today,
  where every new path returns 0 at once and the default trace is unmoved to
  the byte (below); this batch adds one zero row to the Government tab and
  nothing the fund reads, so it is not this batch's code. The gate's run
  judges it; if it reds there it goes in the ship file's known-reds with this
  cause and a queued fix.

## The default trace

Main's default LongPlaytest made fresh at c17b92f0f (FB4's retained trace,
10cc0ca2, predates CPB1, which moved it) and this branch's: both exit 0
(497 s, 489 s), all eight trace files identical to the byte
(target/trace-main, target/trace-r2cp). A city founded today does not move;
no ensemble.

After REV-R2c-P-1's mends, the branch's default LongPlaytest again: exit 0
(713 s), all eight trace files identical to main's c17b92f0f trace, to the
byte (target/trace-mends). WC1, merged since, changed no source under
src/main.

## Process lines

No script, check machinery or session rule changed: one in, one out does not
apply.
