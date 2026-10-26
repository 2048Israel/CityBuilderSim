# LongPlaytest.java - 7,645 lines · 139 methods · 102 constants · harnesses

`ham/citybuildersim/LongPlaytest.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> A city played for four thousand months, the way a person plays.
> 
> WHY NOT JUST simulateMonths(4000)
> 
> A single long skip is one input. It exercises the month loop and almost
> nothing else: the player never intervenes, so the city settles into whatever
> equilibrium it finds in the first fifty months and then repeats it. Every bug
> that lives in the interaction between DECIDING and SIMULATING - the ones that
> need something to be built, taxed, borrowed against, saved or reloaded partway
> through - is invisible to it.
> 
> So this alternates. Short hands-on stretches where an advisor looks at the
> city and does something about it, long skips where it just runs, policy
> changes, borrowing, land purchases, demolitions, and save/reload round trips
> partway through. Roughly the shape of a real session, repeated a hundred and
> forty times.
> 
> WHAT IT IS LOOKING FOR
> 
> Not "did it finish". A simulation that runs forever while quietly printing
> nonsense is worse than one that throws. Every month is audited against a list
> of things that must be true of any city in any state (below), and every
> reload is checked against the game it came from to the cent. Findings are
> deduplicated and reported with the month they first appeared, because a
> problem that starts at month 900 and a problem that starts at month 3 are
> different problems.

**Uses:** [Game](Game.md) (128), [Good](Good.md) (45), [BusinessDebtManager](BusinessDebtManager.md) (38), [Ports](Ports.md) (29), [BuildingsTemplate](BuildingsTemplate.md) (29), [CityMap](CityMap.md) (29), [Sector](Sector.md) (28), [Equity](Equity.md) (26), [Sectors](Sectors.md) (24), [Exchange](Exchange.md) (24), [BuildAdvice](BuildAdvice.md) (24), [CareType](CareType.md) (23), [Bank](Bank.md) (20), [LandManager](LandManager.md) (19), [AgeBand](AgeBand.md) (14), [WageBand](WageBand.md) (14), [SpreadPlanner](SpreadPlanner.md) (14), [Crime](Crime.md) (13), [Founding](Founding.md) (13), [DebtManager](DebtManager.md) (12), [JobType](JobType.md) (11), [LandParcel](LandParcel.md) (11), [BuildingManager](BuildingManager.md) (10), [CityNeeds](CityNeeds.md) (10), [TreasuryFund](TreasuryFund.md) (10), [InfrastructureManager](InfrastructureManager.md) (9), [BuildingType](BuildingType.md) (9), [World](World.md) (9), [FundLedger](FundLedger.md) (9), [EconomyManager](EconomyManager.md) (8)... and 76 more

**Used by (48):** [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [BondCheck](BondCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [CarCheck](CarCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChildcareCheck](ChildcareCheck.md), [ClockCheck](ClockCheck.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [GroceryCheck](GroceryCheck.md), [HomeRailCheck](HomeRailCheck.md), [IndustryViewCheck](IndustryViewCheck.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandClassRenders](LandClassRenders.md), [LandmassCheck](LandmassCheck.md), [MapCheck](MapCheck.md), [MonetaryCheck](MonetaryCheck.md), [MortgageCheck](MortgageCheck.md), [OilCheck](OilCheck.md), [OilViewCheck](OilViewCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [PlanCheck](PlanCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PortCheck](PortCheck.md), [RefineryViewCheck](RefineryViewCheck.md), [ReloadCheck](ReloadCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [RoadCheck](RoadCheck.md), [RunRenders](RunRenders.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorStatementCheck](SectorStatementCheck.md), [ShadowBasket](ShadowBasket.md), [SlopeCheck](SlopeCheck.md), [SlopeRenders](SlopeRenders.md), [UiProbe](UiProbe.md), [WellCheck](WellCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 51 | FINDINGS |
| 70 | · `worst` MEANT `first`, AND THE PRINTOUT SAID SO (2026-09-09). |
| 112 | THE AUDIT |
| 183 | · A mechanic that is not counted over a real run is a mechanic nobody |
| 1227 | · · AND NOTHING MOVED AFTER THE AUDIT STRUCK. |
| 1871 | THE ADVISOR |
| 1889 | WHO IS PLAYING (2026-09-17) |
| 1947 | THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green |
| 1979 | THE FOUNDING, AS A CHOICE (0.7.10) |
| 2007 | ON THE REAL EARTH (0.7.121, batch E4; the project's spec-earth.md 8) |
| 2095 | THE TRACE, BESIDE THE REPORT (0.7.10) |
| 2500 | THE CITY'S FUND AND THE BANK'S RESCUE, STATED (0.7.14) |
| 2538 | WAGES AGAINST THE INDEX (2026-09-21) |
| 2562 | THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21) |
| 2581 | THE HOLDINGS DIAL, HELD (0.7.1) |
| 2598 | THE CEILING, SET (0.7.2) |
| 2613 | THE TARGET, SET (0.7.4) |
| 2996 | · THE TWO THINGS THAT ARE NOT PURCHASES, done first and for free. |
| 3051 | · AND EVERYTHING THAT IS A PURCHASE. |
| 3396 | · AND THE BEST OF THEM WINS. |
| 3630 | THE PORTS (0.7.86, batch O9; docs/studies/spec-oil.md 5's O9 row) |
| 4871 | RUNNING MONTHS |
| 5118 | SAVE / RELOAD, MID-RUN |
| 5421 | (untitled) |
| 5429 | · what the accounts added back (0.7.136, batch FX1) |
| 5454 | · the city's fund and the bank's rescue (0.7.14) |
| 5542 | · the fund's cost basis (0.7.39) |
| 5590 | · an Insane founding (0.7.14) |
| 5688 | · · founding: a few months at a time, by hand |
| 5785 | · · then the real rhythm |
| 5933 | · · the report |
| 6042 | · · BUSINESS SERVICES - and the point of printing it is the MECHANISM, |
| 7516 | THE DOOR FOR ui.UiProbe (UP1, 2026-10-23) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `LongPlaytest.TARGET_MONTHS` | `Integer.getInteger("playtest.months", 4000)` | The run's length: 4,000 months, or -Dplaytest.months=N for a short look at one mechanic (a trace of its own, not the baseline). |
| 59 | `LongPlaytest.findings` | `new LinkedHashMap<>()` |  |
| 165 | `LongPlaytest.illnessDeathsByBand` | `new double [ AgeBand.values().length ]` | The long sick (2026-09-11): who died of staying sick, by band, and the most ever ill past two months. |
| 167 | `LongPlaytest.deathsByBandRun` | `new double [ AgeBand.values().length ]` | Everyone who died, by band, and the orphans and the unhoused among them (2026-09-11). |
| 223 | `LongPlaytest.portBilledMonths` | `new int [ Ports.Cargo.values().length ]` | THE PORTS DIVISION OVER THE RUN (FR3, docs/queue/FR3.md 6 item 10): by kind of cargo, the months the division billed handling, the tonnes it handled and the last quote it struck; the handling billed and the port dues ... |
| 224 | `LongPlaytest.portTonnesRun` | `new double [ Ports.Cargo.values().length ]` |  |
| 225 | `LongPlaytest.portLastQuote` | `new double [ Ports.Cargo.values().length ]` |  |
| 249 | `LongPlaytest.BY_YEAR` | `{ "Gravel Road", "Paved Road", "Elevated Highway", "Rail Depot", "Rail Termin...` | THE ROADS AND THE RAILS BY YEAR (FR7, 2026-10-18; its brief's 5: "count in a real run how many highways, paved and gravel roads a long playtest builds, and in which years; the same for the rail rungs"): for each of th... |
| 252 | `LongPlaytest.byYearFirst` | `new int [ BY_YEAR.length ]` | ...the first month each stood, 0 for never. |
| 254 | `LongPlaytest.byYearAt` | `new int [ BY_YEAR.length ] [ 3 ]` | ...and how many stood at months 1,000, 2,000 and 3,000. |
| 259 | `LongPlaytest.FAR_FROM_PARITY` | `2.0` | How far from parity, as a multiple of it, a weak currency has to be to count as far from it. |
| 275 | `LongPlaytest.dialPath` | `new java.util.ArrayList<>()` | Every month's policy rate, for the dial's min, median and max over the run. |
| 277 | `LongPlaytest.inflationPath` | `new java.util.ArrayList<>()` | Every month's inflation reading, once the index has a year to read, for its median. |
| 286 | `LongPlaytest.spendPath` | `new java.util.ArrayList<>()` | Every month's spend factor (0.7.3): the share of their spending above subsistence the households planned at, on the month's real deposit rate - HouseholdBalance.getSpendFactor() after the month. |
| 304 | `LongPlaytest.bankNii` | `new double [ 12 ], bankFees = new double [ 12 ], bankOther = new double [ 12 ...` | THE BANK AS A BUSINESS (0.7.7): a trailing year of its statement, for the checkpoint line that prints its price build-up beside its margin, its cost ratio, its fee share and its return on equity - and the run's deposi... |
| 321 | `LongPlaytest.bankYears` | `new java.util.ArrayList<>()` | THE BANK'S CAPITAL, YEAR BY YEAR (0.7.8): its capital ratio and its own target averaged over each year, its return on the equity it opened the year with, its provisions over the loans it made (the businesses' and the ... |
| 344 | `LongPlaytest.watchSpell` | `new java.util.HashMap<>()` |  |
| 357 | `LongPlaytest.refusedOnPrice` | `new java.util.TreeMap<>()` |  |
| 367 | `LongPlaytest.houseReasons` | `new java.util.TreeMap<>()` |  |
| 411 | `LongPlaytest.openedByStance` | `new java.util.TreeMap<>()` |  |
| 566 | `LongPlaytest.deskOverEquity` | `new java.util.ArrayList<>()` |  |
| 578 | `LongPlaytest.sharePriceOverFair` | `new java.util.ArrayList<>()` | 0.7.12 ROUND 2: the shares on the book, the dividends, and the default point - month by month, for the report's round-2 lines. |
| 582 | `LongPlaytest.refusedAtLineBySector` | `new java.util.LinkedHashMap<>()` |  |
| 617 | `LongPlaytest.deskBookAtFair` | `new java.util.ArrayList<>(), deskLargestPosition = new java.util.ArrayList<>()` |  |
| 697 | `LongPlaytest.companyTillsRun` | `new java.util.ArrayList<>()` | ---- 0.7.12 round 4: the companies' cash, the desk's excess, and can't pay means default ---- |
| 708 | `LongPlaytest.interimSeen` | `new java.util.IdentityHashMap<>()` |  |
| 709 | `LongPlaytest.interimLost` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` |  |
| 710 | `LongPlaytest.cannotPayBySector` | `new java.util.TreeMap<>()` |  |
| 711 | `LongPlaytest.cannotPayForgivenBySector` | `new java.util.TreeMap<>()` |  |
| 712 | `LongPlaytest.cannotPayByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 713 | `LongPlaytest.cannotPayForgivenByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 714 | `LongPlaytest.cannotPaySpell` | `new java.util.HashMap<>()` |  |
| 717 | `LongPlaytest.cannotPaySpellBanned` | `new java.util.HashMap<>()` |  |
| 720 | `LongPlaytest.limitedBySector` | `new java.util.TreeMap<>()` | ---- round 6: buy only what it can pay for ---- |
| 721 | `LongPlaytest.forgoneBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 722 | `LongPlaytest.shelfShortBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 723 | `LongPlaytest.limitedLastMonth` | `new java.util.HashSet<>()` | all, after a cut |
| 726 | `LongPlaytest.defaultsOnStockBySector` | `new java.util.TreeMap<>()` |  |
| 727 | `LongPlaytest.defaultsAfterCutBySector` | `new java.util.TreeMap<>()` |  |
| 728 | `LongPlaytest.luxuryLeverage` | `new java.util.ArrayList<>()` |  |
| 729 | `LongPlaytest.RETAILERS` | `java.util.Set.of("Retail", "Restaurants", "Luxury Retail")` |  |
| 792 | `LongPlaytest.wageSpell` | `new java.util.HashMap<>()` | ---- round 7: a sector paying out more in wages than it takes in, on interim loans; a new sector's first year ---- |
| 793 | `LongPlaytest.wageSpells` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 794 | `LongPlaytest.firstPlant` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 795 | `LongPlaytest.bannedFirstYear` | `new java.util.TreeMap<>()` |  |
| 797 | `LongPlaytest.firstBillDefault` | `new java.util.TreeMap<>()` | ---- round 8: a cash-flow default on a new sector's first bill: {the month, 1 if it was lent in the interim} ---- |
| 801 | `LongPlaytest.lastProject` | `new java.util.HashMap<>()` | ...a ceiling default in the month after the sector bought plant with a loan; and a shell: a sector holding no plant, nothing built and nothing on site, that owes something - its defaults, what they left unpaid and wha... |
| 803 | `LongPlaytest.shellDefaults` | `new java.util.TreeMap<>()` |  |
| 804 | `LongPlaytest.shellSpell` | `new java.util.HashMap<>()` |  |
| 1094 | `LongPlaytest.OLD_DIAL_STOP` | `.25` | The dial's stop before 0.7.2, for counting the months the uncapped dial spends past it. |
| 1919 | `LongPlaytest.ATTENTIVE` | `"attentive".equalsIgnoreCase(System.getProperty("playtest.player", "occasiona...` | True when this run is played by somebody paying attention. |
| 1926 | `LongPlaytest.SCHOOLS` | `Boolean.getBoolean("playtest.schools")` | -Dplaytest.schools=true: the city builds schools, which the advisor never does. |
| 1929 | `LongPlaytest.CHILDCARE` | `Boolean.getBoolean("playtest.childcare")` | -Dplaytest.childcare=true (0.7.71, batch N2): the city builds childcare by the build advice's own card, which the advisor never does. |
| 1968 | `LongPlaytest.POLICY_RATE` | `System.getProperty("playtest.policyRate") = = null ? null : Double.valueOf(Sy...` | The rate the dial is held at under -Dplaytest.policyRate, or null when the advisor sets it. |
| 1972 | `LongPlaytest.HELD_AFTER` | `24` | The month the held runs start after: the currency's settling months until XR1, kept at 24 so the held runs and the ensemble start where they always did. |
| 1999 | `LongPlaytest.FOUNDING` | `Founding.Preset.valueOf(System.getProperty("playtest.founding", "standard").t...` | The founding preset under -Dplaytest.founding, standard when unset. |
| 2154 | `LongPlaytest.TRACE` | `System.getProperty("playtest.trace")` | The trace's prefix under -Dplaytest.trace, or null. |
| 2157 | `LongPlaytest.paperSeen` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` | The paper already written to the borrow file, by identity. |
| 2489 | `LongPlaytest.AUTOPILOT` | `Boolean.getBoolean("playtest.autopilot")` | -Dplaytest.autopilot=true (0.7.0): the rule holds the dial from founding, through the game's own autopilot (DebtManager), and the advisor keeps its hands off it. |
| 2497 | `LongPlaytest.ROLLOVER` | `Rollover.Mode.valueOf(System.getProperty("playtest.rollover", "SAME_STRUCTURE...` | -Dplaytest.rollover=MANUAL\|SAME_STRUCTURE\|TWELVE_MONTH_BILL (0.7.13): the treasury's rollover for the run (Rollover). |
| 2524 | `LongPlaytest.RESCUE_AUTO` | `! "BUTTON".equalsIgnoreCase(System.getProperty("playtest.rescue", "AUTO").tri...` | The rescue setting under -Dplaytest.rescue, AUTO when unset. |
| 2527 | `LongPlaytest.PREFERRED_ACCEPT` | `! "DECLINE".equalsIgnoreCase(System.getProperty("playtest.preferred", "ACCEPT...` | The answer to the bank's offer under -Dplaytest.preferred, ACCEPT when unset. |
| 2530 | `LongPlaytest.FUND_DIAL` | `fundDial(System.getProperty("playtest.fund", "0"))` | The fund's dial under -Dplaytest.fund, 0 when unset. |
| 2560 | `LongPlaytest.WAGES` | `Boolean.getBoolean("playtest.wages")` | -Dplaytest.wages=true: the wage index, the price index and the lag-implied level at each checkpoint. |
| 2579 | `LongPlaytest.BORROW_AT_HOME` | `Boolean.getBoolean("playtest.borrowAtHome")` | -Dplaytest.borrowAtHome=true: the advisor's borrowing goes to the city's own term bonds, never abroad. |
| 2595 | `LongPlaytest.QE_SHARE` | `System.getProperty("playtest.qeShare") = = null ? null : Double.valueOf(Syste...` | The holdings dial under -Dplaytest.qeShare, or null when nobody sets it. |
| 2610 | `LongPlaytest.ADVANCES_MONTHS` | `System.getProperty("playtest.advancesMonths") = = null ? null : Double.valueO...` | The advances ceiling under -Dplaytest.advancesMonths, in months of revenue, or null for the default. |
| 2635 | `LongPlaytest.MONEY_GATE` | `System.getProperty("playtest.moneyGate", "").trim()` | THE MONEY GATE, FOR THE COUNTERFACTUAL (0.7.82, batch O5; turned round at 0.7.112): since 0.7.112 the refiners' spread planner tests every order on its whole cost, whoever pays (SpreadPlanner.ON_ITS_WHOLE_COST, Jerus'... |
| 2638 | `LongPlaytest.REFINERY_TRACE` | `Boolean.getBoolean("playtest.refinery")` | -Dplaytest.refinery=true (0.7.82): the refiners' planner yearly - its outlook and its best candidates under both money gates (refineryLine()). |
| 2641 | `LongPlaytest.INFLATION_TARGET` | `System.getProperty("playtest.inflationTarget") = = null ? null : Double.value...` | The inflation target under -Dplaytest.inflationTarget, a fraction a year, or null for the default. |
| 2669 | `LongPlaytest.FLOAT_FROM_MONTH` | `60` | THE D$ OVER THE RUN (XR1, 2026-10-21): how the float behaved in this city, counted a month at a time (countTheFloat()) and printed as one line beside the currency's (docs/studies/spec-exchange-rates.md, 1.1: "volatili... |
| 2863 | `LongPlaytest.CHILDCARE_SEEN` | `{ 1_000, 2_000, 3_000, 4_000 }` | CHILDCARE ON THE MAP (0.7.139, batch CC1). |
| 2866 | `LongPlaytest.childcareSeen` | `new java.util.ArrayList<>()` | ...what it said at each, in order. |
| 2888 | `LongPlaytest.BUDGET_EVERY` | `Integer.getInteger("playtest.budget", 0)` | -Dplaytest.budget=N (0.7.139, batch CC1): every N months the treasury's month, line by line - what came in and what went out, care by kind and childcare's own running (its centres' posts and upkeep) beside it. |
| 2913 | `LongPlaytest.childcareBuilt` | `new java.util.TreeMap<>()` | ...the buildings, by name. |
| 2916 | `LongPlaytest.schoolsOrdered` | `new java.util.HashMap<>()` | What the flag has ordered of each school, so one under construction is not ordered twice. |
| 3534 | `LongPlaytest.OIL_FUEL_IMPORTS_SHARE` | `.01` | The share of a month's GDP the fuel bought abroad has to pass before the test player buys oil (0.7.62): spec-land 3's K entry, 1%. |
| 3564 | `LongPlaytest.refinedRun` | `new java.util.EnumMap<>(Good.class)` | ...and the refinery's products over the run (0.7.76, batch O1): made, by product, and what of them its tanks could not hold (spec-oil 6's joint-products risk). |
| 3656 | `LongPlaytest.terminalsOrdered` | `new java.util.TreeMap<>()` | Terminals the test player ordered, by name; the looks that found a berth's worth with no coast; the first terminal's month. |
| 3665 | `LongPlaytest.PORTS_TRACE` | `Boolean.getBoolean("playtest.ports")` | -Dplaytest.ports=true (0.7.86): the ports yearly - each kind's tonnes across the boundary, its berths, its share at sea and the month's calls (portsLine()). |
| 3748 | `LongPlaytest.refinersOpened` | `new java.util.TreeMap<>(), refinersSold = new java.util.TreeMap<>()` | THE SPREAD PLANNER'S BUILDINGS OVER THE RUN (0.7.82, batch O5): each of the refiners' buildings opened and sold back, and the crude units and the conversion units standing at most. |
| 3812 | `LongPlaytest.GROWTH_DISCOUNT` | `.15` | How much of a gain arrives later rather than now. |
| 3819 | `LongPlaytest.GROUND_AHEAD_CASH_SHARE` | `.10` | The share of the treasury's cash one look spends keeping ground ahead (0.7.58, J1d): a tenth, the share the war chest tops the reserves up from and the every-13th-stop purchase is held under. |
| 4006 | `LongPlaytest.LAYOUT_WINDOW` | `13` | How many months a window of the layout's watch is. |
| 4008 | `LongPlaytest.LAYOUT_STARTS` | `layoutStarts(TARGET_MONTHS)` | The months each window follows: TARGET_MONTHS / 4, / 2, 3 / 4 and TARGET_MONTHS - LAYOUT_WINDOW, those that fit in the run and clear the window before. |
| 4038 | `LongPlaytest.layoutSpillByMonth` | `new java.util.TreeMap<>()` | The spill's moves by the month they were found in (the month pair ending there): how many pairs had any, and when. |
| 4042 | `LongPlaytest.layoutOldBoxes` | `new java.util.HashMap<>()` | ...each district's industry boxes, by its key... |
| 4044 | `LongPlaytest.layoutOldCounts` | `new java.util.HashMap<>()` | ...and each district's count of boxes by type. |
| 4046 | `LongPlaytest.layoutOldModel` | `new java.util.HashMap<>()` | ...and each district's model count of every type (District.count()) then and the lowest it has been in any month since: a finding's detail (an old works that left in a dip of its type's count did not move), not the test. |
| 4053 | `LongPlaytest.layoutOldSpill` | `new java.util.HashSet<>()` | The districts a spill reached when the oldest works were kept (a carry in), and the old works in a district a spill reached then or at the end, counted apart: a district joining the map's order ahead of them hands the... |
| 4250 | `LongPlaytest.layoutBandsAt` | `new ArrayList<>()` | How many boxes each window's first month holds in the homes' band, in industry and in the outer kinds, one phrase a window: what the oldest works are read against. |
| 4819 | `LongPlaytest.DEBT_SERVICE_LIMIT` | `.25` | Whether the advisor can afford the PAYMENTS, not whether it likes the size. |
| 4851 | `LongPlaytest.refusals` | `new LinkedHashMap<>()` | Why the advisor could not do the thing it wanted to. |
| 5432 | `LongPlaytest.ADDED_BACK_TERMS` | `{ "food", "materials", "luxuries", "held goods" }` | The inventory terms in NationalAccounts.getWrittenOffAddedBack()'s order. |
| 5434 | `LongPlaytest.addedBackMonths` | `new int [ ADDED_BACK_TERMS.length ], addedBackFirst = { - 1, - 1, - 1, - 1 },...` | Months each term added a write-off back, the first, and the largest with its month. |
| 5436 | `LongPlaytest.addedBackWorst` | `new double [ ADDED_BACK_TERMS.length ]` |  |
| 5438 | `LongPlaytest.EVENTS_PRINTED` | `12` | The events printed as they happen, each term's first few: enough to say where a trace moved. |
| 5491 | `LongPlaytest.stakes` | `new ArrayList<>()` |  |
| 5499 | `LongPlaytest.mostHeld` | `new double [ Equity.COMPANIES.length ]` |  |
| 7379 | `LongPlaytest.DISTINCT_ROUNDS` | `64` | The most letters distinctCuts() adds to a name: past the longest key there is nothing left to add. |
| 7528 | `LongPlaytest.PROBE_QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } ...` |  |
| 7534 | `LongPlaytest.PROBE_MONTH_NANOS` | `new long [ 20_000 ]` | Per-month wall time of the probe's run(g, 1), nanoseconds, by month index. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `static PrintStream out` | Where the report goes. |
| 62 | `int count` |  |
| 63 | `int firstMonth` |  |
| 64 | `int lastMonth` |  |
| 65 | `String worst` |  |
| 66 | `double worstSize` |  |
| 67 | `int worstMonth` |  |
| 120 | `static double worstCrowding` | How far past the buildings' comfortable capacity the city was pulled. |
| 121 | `static int worstCrowdingMonth` |  |
| 140 | `static double worstUnemployment` | Unemployment, watched rather than asserted. |
| 141 | `static double lastUnemployment` |  |
| 143 | `static double totalEvicted` | The people outside the families, over the run (2026-09-11). |
| 151 | `static double usedOffered` | The second-hand car market over the run (2026-09-17). |
| 152 | `static int worstUnhousedMonth` |  |
| 154 | `static int monthsAnyTierDeclining` |  |
| 155 | `static int monthsPeopleLeft` |  |
| 160 | `static int outbreaks` | Sickness. |
| 161 | `static int monthsInOutbreak` |  |
| 162 | `static boolean wasInOutbreak` |  |
| 163 | `static double worstSickRate` |  |
| 168 | `static double orphanDeathsRun` |  |
| 169 | `static double allDeaths` |  |
| 170 | `static int worstLongSickMonth` |  |
| 177 | `static double personMonths` | The price at the clinic door (2026-09-19): person-months lived, so the deaths can be read per thousand a year; the people the fee turned away, summed for the mean; and the fees the households skipped, summed over the ... |
| 179 | `static double studentInterestSum` | The price of a place (2026-09-21): the interest the graduates paid and the grants paid, summed over the run. |
| 181 | `static double crimeVsSum` | Crime (2026-09-11): the rate against Canada's summed for the mean, the worst, and what it did over the run. |
| 190 | `static double peakSeats` |  |
| 191 | `static int peakSeatsMonth` |  |
| 192 | `static int monthsWithSeats` |  |
| 193 | `static int lastSeatMonth` |  |
| 194 | `static double serviceExportsRun` |  |
| 195 | `static double caughtRun` |  |
| 202 | `static double peakRate` | The currency over the run (2026-09-21): its dearest dollar and when, and how many months it spent far from parity. |
| 203 | `static int peakRateMonth` |  |
| 211 | `static int haulShortMonths` | FREIGHT'S TRUCKS OVER THE RUN (FR2): the months Haulage's trucks carried less than the city's legs and how short at worst, the fleet and the depots' room at their most, and what the run billed for how many tonnes. |
| 212 | `static double haulWorstShare` |  |
| 226 | `static double portHandlingRun` |  |
| 227 | `static int portBestUncoveredMonth` |  |
| 239 | `static int homeRailMonths` | THE CITY'S OWN BULK ON THE RAILWAY OVER THE RUN (FR6, docs/queue/FR6.md 5 item 3): the months the railway carried a share of what the city's works bought at home (the home share above zero) and the first of them, the ... |
| 240 | `static double homeRailedRun` |  |
| 256 | `static int needsLooks` | ...and NEEDS YOU read once a year: the looks, those listing the streets, the highways, and both (FR7 2.1). |
| 273 | `static int monthsAtGuard` | THE CURRENCY'S GUARD AND THE DIAL'S PATH, over the run (0.7.2). |
| 288 | `static double spendLow` | ...its lowest and highest, and the first month each was struck in. |
| 289 | `static int spendLowMonth` |  |
| 291 | `static int depositCappedMonths` | Months the bank's interest margin could not pay the deposit rate it chose, and paid what the margin had (Bank.isDepositPayoutHeld(), 0.7.7), and the first and last of them. |
| 307 | `static int bankMonths` |  |
| 308 | `static double shareSum` |  |
| 309 | `static int shareMonths` |  |
| 322 | `static double yrRatio, yrTarget, yrNet, yrProv, yrBook, yrDiv, yrBuyback, yrIssued, yrOpenEquity, yrOpenPop` |  |
| 323 | `static int yrMonths, yrFailuresOpen, yrFrozen, yrBranchMonths` |  |
| 324 | `static int rationedMonths, keepGoingMonths, payingMonths, returningMonths, rebuildingMonths` |  |
| 325 | `static double runDividends, runBuybacks, runIssued, runProvisions` |  |
| 327 | `static double runLoanMonths, runWrittenOff` | Every month's loans (the businesses' and the families') summed, and every write-off: the run's provisions over its average book, through the cycle rather than over the growth years alone. |
| 340 | `static int backstops, sliceMonths, newsMonths, pastWatchMonths, pastTriggerMonths, longestWatchSpell, longe...` | HOW THE DEFAULTS ARRIVE (0.7.8, a sector defaults a slice at a time): the backstop's whole-sector write-downs; the months any slice was written off, and the months one was news (BusinessDebtManager .defaultsAreNews())... |
| 342 | `static String longestWatchSector` |  |
| 343 | `static double sliceWrittenOff, worstWriteOffShare, worstWriteOff, lentPastWatch, runAllowanceUsed` |  |
| 345 | `static Notice lastDefaultNotice` |  |
| 355 | `static int loansPastWatch, loansPastOne, loansPastT, projectsPastOne` | ROUND 2 (0.7.8): the price off the curve and the plant sold as materials. |
| 356 | `static double lentPastOne, lentPastT, rateSumPastOne, rateMaxPastOne, rateSumPastT, rateMaxPastT` |  |
| 365 | `static int mortgagesWritten` | THE LANDLORDS' MORTGAGES (0.7.11): what they wrote, what the insurance took and paid, and every month's reason the landlords built or did not, counted by its words (houseReason()) - the batch's ensembles were read off... |
| 366 | `static double mortgagesLent, premiumsRun, claimsRun` |  |
| 400 | `static int salvageBuildingsDistress, salvageBuildingsSpare` |  |
| 401 | `static double salvageUnits, salvageUnitsBought, salvagePaid, salvagePaidDistress, salvageUsed` |  |
| 410 | `static int leverageMonths, levBankMonths, branchesOpened, branchesClosedSeen, lastBranches` | THE LEVERAGE RATIO AND THE BRANCHES (0.7.11, round 2): how often the leverage requirement was the one that bound, the ratio at its lowest, and the branches opened - by where the bank's capital stood the month each one... |
| 412 | `static double lowestLeverage` |  |
| 413 | `static int lowestLeverageMonth` |  |
| 414 | `static int mortgagesRefusedForCapital` |  |
| 470 | `static double bondRunLoansDefaulted, bondRunLoansLost, bondRunBondsDefaulted, bondRunBondsLost` | THE BONDS (0.7.12): what defaulted in each class and what it lost - the recoveries the sources are read against - the months a sector owed bonds, and the peak share of the businesses' debt in bonds. |
| 471 | `static double bondPeakShare` |  |
| 472 | `static int bondPeakShareMonth, bondMonthsWithBonds` |  |
| 565 | `static int deskOverEquityMaxMonth` | ROUND 4 (0.7.8): the desk's inventory at the close against the bank's equity, month by month while a bank stands out of resolution - one in it holds its old inventory against the few dollars it has earned back, which ... |
| 567 | `static double deskOverEquityMax` |  |
| 579 | `static int shareListedMonths, shareTradedMonths, deskInventoryMonths, refusedAtLineMonths, projectsRefusedA...` |  |
| 580 | `static double deskPnlRun, deskInventorySum, ordinaryDividendsRun` |  |
| 581 | `static double refusedAtLineRun, notRenewedAtLineRun` |  |
| 693 | `static int landWaitMonths, landWaitNotices` | The months a sector waited on ground, and the "nowhere to build" notices raised (PT1-M2). |
| 694 | `static Notice lastLandWaitNotice` |  |
| 698 | `static double companyDepositInterestRun, companiesSoldShortRun, buybackRestingRun` |  |
| 699 | `static int overdraftMonths, overdraftNoLenderMonths, overdraftProjectMonths` |  |
| 700 | `static double overdraftSum, overdraftNoLenderSum, overdraftMax, overdraftProjectSum` |  |
| 701 | `static int cannotPayMonths, cannotPayBankUnderLine` |  |
| 702 | `static double cannotPayForgivenRun, cannotPayDebtDefaultedRun, cannotPayForgivenBanned` |  |
| 704 | `static int ruleOnMonths, projectRefusedForCapital, mortgageRefusedForCapital, lineRationedMonths` | ---- round 5: the lines that stay open, the growth doors the capital rule still shuts, and interim financing ---- |
| 705 | `static double lineLentRationedRun, lineLentPastOldRuleRun` |  |
| 706 | `static int interimWritten, interimRepaid, interimDefaultedAgain, interimRefusedPast, interimRefusedShut, ba...` |  |
| 707 | `static double interimWrittenSum, interimRepaidSum, interimWrittenOffSum, backstopInBanSum` |  |
| 715 | `static int longestCannotPaySpell, longestCannotPayStart, longestCannotPayBanned` |  |
| 716 | `static String longestCannotPaySector` |  |
| 724 | `static int defaultsAfterCut, defaultsOnStock, defaultsOnBondCost` |  |
| 725 | `static double defaultsAfterCutSum, defaultsOnStockSum, defaultsOnBondCostSum` |  |
| 802 | `static int afterProjectCeiling` |  |
| 805 | `static double shellUnpaid, shellInterim` |  |
| 806 | `static int shellMonths, shellLongest, shellLongestFrom` |  |
| 807 | `static String shellLongestWho` |  |
| 1103 | `static int worstCrimeMonth` |  |
| 1104 | `static double lastSickRate` |  |
| 1110 | `static int monthsUnburied` | DEATH CARE FIRST WHILE THE DEAD WAIT (0.7.129; BuildingManager): the months a month ended with anybody unburied, and the months the advance served the city's death care first - the first and the last of them. |
| 1117 | `static int monthsBuildersFirst` | THE BUILDERS' OWN DEPOTS FIRST (0.7.130; BuildingManager): the months the advance served a depot on site off the top of the month - the first and the last of them, those with the death care beside it, and the points t... |
| 1118 | `static double buildersFirstPoints` |  |
| 1119 | `static double workLostToIllness` |  |
| 1120 | `static int monthsObserved` |  |
| 1121 | `static double totalDepartures` |  |
| 1122 | `static double totalArrivals` |  |
| 1125 | `static double lastM0` | M0 at the last audit, so the next can say what it moved by. |
| 1942 | `static boolean AUTOBUILD` | -Dplaytest.autobuild=true (0.7.73, batch N4): the player turns automatic building on at the founding (AutoBuilder, at its defaults) and leaves it the city's works - the advisor's power, water, road and transit, health... |
| 1945 | `static boolean educationSet` | Whether any education dial or the schools flag was set, so the summary says what they did. |
| 2021 | `static String EARTH` | The spot under --earth or -Dplaytest.earth: a place's name or "lat,lon"; null for the default world. |
| 2044 | `static String earthPlace` | The place --earth named, for the report; null for a spot given as figures. |
| 2155 | `static java.io.PrintWriter traceMonths, traceBorrow, traceHouse, tracePop, traceBank, traceBonds, traceBuil...` |  |
| 2651 | `static int monthsDefended, peakDefenceMonth` | THE DEFENCE OVER THE RUN (0.7.2): the dollars the central bank sold defending the currency, in total and in its biggest month, and how many months it sold anything. |
| 2652 | `static double defendedUsdRun, peakDefenceUsd` |  |
| 2670 | `static int floatMonths, floatRun, floatRunLongest, floatSign, floatFarFromParity, floatDealt, floatMarketOver2` |  |
| 2671 | `static double floatSum, floatSum2, floatBiggest` |  |
| 2673 | `static double floatDealtBefore` | What the treasury had dealt since the last reprice when the month began: run() reads it before the month runs. |
| 2729 | `static int monthsLandBought` | THE LAND OFFICE OVER THE RUN (0.7.6): months the city bought land abroad and what it cost in local money at the day's rates - against the dollars ForeignAccounts counts, which is what the same land would have cost at ... |
| 2730 | `static double landLocalRun` |  |
| 2732 | `static int vaultLowMonth, halfGoneMonth, emptyMonth` | The vault's life: its lowest reading and when, and the first month the founders' dollars were half gone and all but gone - the question the defence's dials are asked. |
| 2733 | `static double vaultLow` |  |
| 2736 | `static double lagImplied` | The lag-implied wage index, walked month by month beside the game's own; NaN until the first month. |
| 2851 | `static int autoBuildGroundBought` | Offers bought for automatic building's ground (the autobuild flag). |
| 2911 | `static int childcareOrders` | What the childcare flag has ordered: orders, and buildings by name. |
| 3517 | `static int ironFieldsBought, ironFieldsOnBond, firstIronNeedMonth, firstIronMonth, ironCouldNotPay, ironNon...` | Over the run (0.7.64): iron offers bought, how many on the funding page's bond, the first look that needed iron, the first purchase's month and words, and the looks that needed iron and could not pay for the cheapest ... |
| 3518 | `static String firstIron` |  |
| 3521 | `static int ironDoesNotPay` | Over the run (0.7.67, M3b): the looks that needed iron whose cheapest field would not pay itself back (fieldEarnings() under fieldPayment()). |
| 3537 | `static int oilBought` | Offers with oil the test player bought over the run (0.7.62). |
| 3556 | `static double fuelBillRun, fuelAbroadRun` | Over the run (0.7.62): the drivers' and the railway's fuel, what of it was bought abroad, the first well and refinery, and the most refineries standing. |
| 3557 | `static int firstWellMonth, firstRefineryMonth, mostRefineries` |  |
| 3560 | `static double crudeLiftedRun` | ...and the wells' decline over the run (0.7.84, batch O7; spec-oil 2.6): the crude they lifted and the most standing. |
| 3561 | `static int mostWells` |  |
| 3565 | `static double refinedWrittenOff` |  |
| 3576 | `static double vanDieselRun, vanDieselLitresRun, payrollRun, pumpBillRun, pumpWholesaleRun, pumpLitresRun, q...` | THE PHASE-1 BUYERS OVER THE RUN (0.7.83, batch O6; spec-oil 2.5 and 6's measure-first 4 and 5): the vans' diesel against the businesses' payrolls; the drivers' petrol at the pump against what it cost at wholesale, and... |
| 3577 | `static double lubricantsBoughtRun, lubricantsImportedRun, crudeRefinedLitresRun` |  |
| 3578 | `static int mostStations, firstStationMonth` |  |
| 3657 | `static int portsNoCoast, firstTerminalMonth` |  |
| 3660 | `static long callsRun` | The boats over the run: calls, the most in a month and when; the tonnes by sea and across the boundary; months crude was held back for room. |
| 3661 | `static int mostCalls, mostCallsMonth, lastCalls, heldBackMonths` |  |
| 3662 | `static double seaTonnesRun, boundaryTonnesRun` |  |
| 3749 | `static int[] refinersLast` |  |
| 3750 | `static int mostCrudeUnits, mostConversionUnits, firstRefinerMonth` |  |
| 3822 | `static int groundAheadBought, groundAheadShort` | Over the run: offers bought to keep ground ahead, their dry square feet and US dollars (thousands), and the looks the cash share stopped short. |
| 3823 | `static double groundAheadSqFt, groundAheadUsd` |  |
| 3870 | `static int roomMovesBought` | Over the run (0.7.67, M3b): offers the room-to-grow move bought. |
| 3954 | `static int freshBought, coastsBought, waterLandOffered` | Over the run: offers bought for their lakes and river past the fresh water limit, offers bought for a coast, and the moves that offered either (0.7.59). |
| 3964 | `static int mapMonths, mapMismatches, mapReloads, mapReloadsSame` | THE CITY MAP, WATCHED (0.7.60, batch J3): the run asks for its city's map at the founding, so every month keeps it up (Game.reconcileMap()) and every save writes its sidecar - nothing in the model reads it, so the tra... |
| 4004 | `static boolean layoutOn` | ===== THE LAYOUT, WATCHED (batch CL3, 2026-10-21) ===== |
| 4024 | `long[] boxes` |  |
| 4025 | `int[] byType` |  |
| 4026 | `int[] carryIn` |  |
| 4027 | `int[] carryLog` |  |
| 4028 | `int[] counted` |  |
| 4032 | `static Map<Long, LayoutSnap> layoutPrev` | The month before's snapshot of every district, by layoutKey(), and its month (the window's first month has none). |
| 4033 | `static int layoutPrevMonth` |  |
| 4035 | `static int layoutMonths, layoutPairs` | Over the run: months planned, month pairs compared, boxes compared, boxes that moved outside the spill's districts and inside them, and the nanoseconds the windows took. |
| 4036 | `static long layoutBoxes, layoutMoved, layoutMovedInSpill, layoutNanos` |  |
| 4040 | `static int layoutOldMonth` | The first window's first month: its month, each district's industry boxes (sorted keys) and its count of boxes by type. |
| 4048 | `static double[] layoutRing` | The ring at the last window's last month and its month: {homes km, homes boxes, industry km, industry boxes, outer km, outer boxes}; null until then. |
| 4049 | `static int layoutRingMonth` |  |
| 4051 | `static long layoutOldN, layoutOldStand, layoutOldHeldN, layoutOldHeldStand` | The old works at the last window's last month: how many there were, how many stand where they stood, and the same for the types whose count held. |
| 4054 | `static long layoutOldSpillN, layoutOldSpillStand` |  |
| 4601 | `static int pavings, roadsPaved` | Over the run (0.7.70): the pavings ordered and the gravel roads in them. |
| 4881 | `static int refusedSkips` |  |
| 4896 | `static int monthsOwing, monthsOwingAtHome, worstStrainMonth, peakCityDebtMonth` | THE CITY'S PAPER AND THE BANK THAT HOLDS IT, over the run (2026-09-21). |
| 4897 | `static int strainMonths, monthsWithoutCapacity` |  |
| 4898 | `static double strainSum, worstStrain, peakCityDebt, paperSettledRun` |  |
| 4906 | `static int monthsAtCeiling, monthsOnAdvances, peakAdvancesMonth, peakArrearsMonth, firstAdvanceMonth` | THE CENTRAL BANK OVER THE RUN (0.7.0): the most the treasury owed it, how long the ceiling bound, and the most the arrears rule left unpaid. |
| 4907 | `static double peakAdvances, peakArrears` |  |
| 4916 | `static int monthsHouseholdsBought, monthsDeskBought, monthsCentralBankTraded` | WHO HELD THE CITY'S PAPER OVER THE RUN (0.7.1): what the households paid at the settles and in how many months, what the bank's desk bought back from them and in how many, and what the central bank bought and sold - c... |
| 4917 | `static double householdsBoughtRun, deskBoughtRun, couponsToHouseholdsRun` |  |
| 4919 | `static int monthsFirmsBought` | ...and the businesses' (CD2, slice 4). |
| 4920 | `static double couponsToFirmsRun` |  |
| 4922 | `static int monthsWorldBought, monthsWorldSold` | ...and the world's (CD2, slice 5): the months it bought, the months its paper went home through the desk, and what it was paid. |
| 4923 | `static double couponsToWorldRun, principalToWorldRun, peakWorldPaper` |  |
| 4932 | `static int peakCompressionMonth` | The most the central bank's holdings took off the long end, and when: the twenty-year paper a borrowing seed sells matures inside the run, so by month 4,002 the central bank usually holds none of it and the endpoint's... |
| 4933 | `static double peakCompression, longRateAtPeak, heldShareAtPeak` |  |
| 4968 | `static int monthsCurveInverted, monthsCurveSteep` | THE CURVE, COUNTED (CD1, 2026-10-20). |
| 4969 | `static double widestExpectedMove` |  |
| 5423 | `static double lifetimeWriteOffs` |  |
| 5425 | `static double lowestPaidIn` | The bank's paid-in capital at its lowest, the month, and the most its two parts were ever off its equity (0.7.13, round 2). |
| 5426 | `static int lowestPaidInMonth` |  |
| 5427 | `static double lifetimeHouseholdWriteOffs` |  |
| 5457 | `static int buttonPresses` | Presses of the Bank tab's button that resolved the bank (-Dplaytest.rescue=BUTTON). |
| 5459 | `static int offersFunded` | Offers accepted after borrowing for them on the funding page's bond, and what that bond raised. |
| 5460 | `static double offerFundingRaised` |  |
| 5486 | `int month` |  |
| 5487 | `double at` |  |
| 5488 | `int under50` |  |
| 5489 | `double lowest` |  |
| 5492 | `static int resolutionsSeen` |  |
| 5494 | `static double fundPeak, fundCashShareSum` | The fund's value, its cash and the most of any company its market book held, over the run. |
| 5495 | `static int fundMonths` |  |
| 5497 | `static double warrantSharesSeen, warrantValueTaken` | The warrants' shares taken at expiry, at the price the month they were taken (share counts move with the exchange's splits, so the count alone does not add up across a run). |
| 5498 | `static int warrantExercises` |  |
| 5553 | `static double[] ledgerBefore` | THE FUND'S LEDGER, MONTH BY MONTH (FundLedger; the project's spec-fund-0739.md, 3.7, rule 3 - count a mechanic in a real run): the identity FundLedgerCheck holds on a fixture - realized + the change in unrealized = th... |
| 5554 | `static double ledgerWorst` |  |
| 5555 | `static int ledgerRuleRows, ledgerHandRows, ledgerMatured, ledgerWrittenDown, ledgerRescues` |  |
| 5593 | `static DebtQuote dayZeroBond, dayZeroNote` | What an Insane city was quoted on day 0 for its village, and what it borrowed in its first year. |
| 5594 | `static double villageInvoice, firstYearBorrowed` |  |
| 5595 | `static int insaneBorrowings` |  |
| 7536 | `private static int probeStop` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 43 | 7603 | **type** `public class LongPlaytest` | A city played for four thousand months, the way a person plays. |

### FINDINGS (lines 51-69)

| line | len | member | says |
|---:|---:|---|---|
| 61 | 8 | **type** `static final class Finding` |  |

### `worst` MEANT `first`, AND THE PRINTOUT SAID SO (2026-09-09). (lines 70-111)

| line | len | member | says |
|---:|---:|---|---|
| 92 | 3 | `static void flag(int month, String what, String detail)` |  |
| 96 | 15 | `static void flag(int month, String what, String detail, double size)` |  |

### THE AUDIT (lines 112-182)

### A mechanic that is not counted over a real run is a mechanic nobody (lines 183-1870)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 21 | `static String houseReason(String line)` | The landlords' month in a word, off the advisor's line: what built it, or what stopped it. |
| 392 | 8 | `static void countMortgages(Game g)` |  |
| 416 | 47 | `static void countLeverage(Game g)` |  |
| 474 | 53 | `static void countBonds(Game g)` |  |
| 528 | 27 | `static void countPriceAndPlant(Game g)` |  |
| 584 | 1 | `static double pct(double part, double whole)` |  |
| 586 | 30 | `static void countShares(Game g)` |  |
| 619 | 10 | `static void countCapitalLimits(Game g)` |  |
| 631 | 5 | `static double maxOf(java.util.List<Double> v)` | The largest of a list, 0 when it is empty. |
| 638 | 6 | `static double quantile(java.util.List<Double> v, double p)` | The p-th quantile of a list, 0 when it is empty. |
| 645 | 46 | `static void countDefaults(Game g)` |  |
| 731 | 59 | `static void countRound6(Game g)` |  |
| 809 | 42 | `static void countRound7(Game g)` |  |
| 852 | 101 | `static void countRound4(Game g)` |  |
| 955 | 5 | `static double forgivenTotal(EconomyManager em)` | Every overdraft forgiven over the run: the backstop's, the one path left that forgives (round 4's cash-flow test forgave too; since round 5 it lends the rest as interim financing). |
| 962 | 6 | `static double shareOver(java.util.List<Double> v, double line)` | The share of a list's entries over a line, percent. |
| 969 | 112 | `static void bankYear(Game g)` |  |
| 1083 | 9 | `static String yearSpread(int col, double scale)` | The median, least and most of one column over the growth years, as "m% (lo-hi%)". |
| 1097 | 6 | `static double median(java.util.List<Double> path)` | The median of a path, or zero for an empty one. |
| 1127 | 728 | `static void audit(Game g)` |  |
| 1856 | 6 | `static void finiteArray(int month, String what, double[] values)` |  |
| 1863 | 7 | `static void finite(int month, String what, double value)` |  |

### THE ADVISOR (lines 1871-1888)

| line | len | member | says |
|---:|---:|---|---|
| 1887 | 1 | **type** `record Move(String label, double lost, java.util.function.BooleanSupplier act)` | ONE THING THE CITY COULD DO, AND WHAT IT IS WORTH. |

### WHO IS PLAYING (2026-09-17) (lines 1889-1946)

| line | len | member | says |
|---:|---:|---|---|
| 1923 | 1 | `static int longestSkip()` | Months the player will let pass before looking, at most. |

### THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green (lines 1947-1978)

| line | len | member | says |
|---:|---:|---|---|
| 1975 | 3 | `static boolean holdsPolicyRate(Game g)` | True once the dial is the flag's rather than the advisor's: the flag is set and the held months have begun. |

### THE FOUNDING, AS A CHOICE (0.7.10) (lines 1979-2006)

| line | len | member | says |
|---:|---:|---|---|
| 2003 | 3 | `static Founding founding()` | The city the run founds: Danzik, on the flag's preset, in the default world. |

### ON THE REAL EARTH (0.7.121, batch E4; the project's spec-earth.md 8) (lines 2007-2094)

| line | len | member | says |
|---:|---:|---|---|
| 2024 | 18 | `static Founding earthFounding(String spot)` | The founding on the real Earth round a spot (see the banner); throws, saying why, when the data or the spot is not to be had. |
| 2053 | 41 | `public static Game earthCity(String spot, int months, GameFiles files, PrintStream silent)` | A city founded on the real Earth round a spot and played `months` months as the playtest plays it (ET2: the run SlopeCheck 9 counts, for tools.SlopeRenders, which draws the map of the city this returns): the village's... |

### THE TRACE, BESIDE THE REPORT (0.7.10) (lines 2095-2499)

| line | len | member | says |
|---:|---:|---|---|
| 2164 | 15 | `static void notePaper(Game g, String purpose)` | Writes every piece of paper not yet written, with what it paid for - called the moment the advisor borrows, so the purpose is its own, and at the end of every month for whatever the game issued by itself. |
| 2180 | 56 | `static void traceOpen()` |  |
| 2237 | 51 | `static void traceMonth(Game g)` |  |
| 2298 | 43 | `static void traceLabour(Game g)` | The labour market's month, one row of <prefix>-labour.csv (0.7.18). |
| 2349 | 54 | `static void traceBuild(Game g)` | The builders, the doors and the wage bill, one row of <prefix>-build.csv (0.7.17). |
| 2405 | 7 | `static double cityOrderTax(Game g)` | The builders' tax in the city's orders still on site - paid with the order, not yet billed (revised 0.7.19). |
| 2414 | 5 | `static double sectorsPreTax(Game g)` | Every sector's month before profit tax, summed - what Luxury's share of the city's profit is read against (0.7.19). |
| 2426 | 17 | `static void tracePop(Game g)` | The people's month, one row of <prefix>-pop.csv (0.7.11, round 2): who lives here by age, what migration aimed at and did and why, and the two housing markets - the trace that asks why a city with the same jobs holds ... |
| 2445 | 24 | `static void traceHouse(Game g)` | The landlords' month, one row of <prefix>-house.csv (0.7.11). |
| 2470 | 11 | `static synchronized void traceClose()` |  |

### THE CITY'S FUND AND THE BANK'S RESCUE, STATED (0.7.14) (lines 2500-2537)

| line | len | member | says |
|---:|---:|---|---|
| 2532 | 5 | `static double fundDial(String s)` |  |

### WAGES AGAINST THE INDEX (2026-09-21) (lines 2538-2561)

### THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21) (lines 2562-2580)

### THE HOLDINGS DIAL, HELD (0.7.1) (lines 2581-2597)

### THE CEILING, SET (0.7.2) (lines 2598-2612)

### THE TARGET, SET (0.7.4) (lines 2613-3629)

| line | len | member | says |
|---:|---:|---|---|
| 2676 | 4 | `static void resetTheFloat()` | Clears THE D$ OVER THE RUN: a run's counts start from nothing (main() at its start; a harness before it plays a city of its own). |
| 2682 | 19 | `static void countTheFloat(Game g)` | The D$'s month, for THE D$ OVER THE RUN: called once a month from audit(), after the defence is counted. |
| 2703 | 5 | `static double floatVolatility()` | The D$'s volatility a year over the run from FLOAT_FROM_MONTH: the standard deviation of the monthly log move times the root of twelve; 0 before it has months. |
| 2716 | 5 | `public static void seedMarket(Game g, int seed)` | Seeds the D$'s market from the ensemble seed (XR1): Game founds every city's market from its world seed (Game.marketSeedFor()), which is the same for the ensemble's sixteen cities, so each takes a market of its own - ... |
| 2739 | 5 | `static void walkLag(Game g, double indexHanded)` | One month of the lag, on the index that month was handed. |
| 2746 | 9 | `static String wageEra(Game g)` | The three figures on one line, for a checkpoint or the end. |
| 2774 | 9 | `static void ensureSchools(Game g)` | Under the schools flag: the basic ladder, then a college, then a university, each when the city is big enough to carry it, and more of each as it grows - asked at every stop, so the schools arrive with the people rath... |
| 2806 | 3 | `static void childcareWhenNeeded(Game g)` | Under the childcare flag (0.7.71, batch N2): when NEEDS YOU lists childcare, the build advice's own card for it - the size that fits the need (since 0.7.101 its order over its life for the places it will fill, BuildAd... |
| 2811 | 12 | `static BuildAdvice.Suggestion orderChildcare(Game g)` | The rule itself, flag or none (ChildcareCheck plays it): the advice's childcare card when NEEDS YOU lists childcare, placed; that suggestion, or null when nothing was ordered. |
| 2835 | 14 | `static void groundForAutoBuild(Game g)` | Under the autobuild flag (0.7.73, N4): the ground automatic building said it was held back for, bought as the inbox's notice asks ("Buy land at the land office") - each such order's ground less what is free, by the bu... |
| 2868 | 12 | `static void noteChildcare(Game g, int month)` |  |
| 2890 | 19 | `static void budgetLine(Game g, int month)` |  |
| 2918 | 4 | `static void ensure(Game g, String name, int want)` |  |
| 2924 | 1 | `static int movesPerLook()` | ...and how many things it will fix when it does look. |
| 2984 | 423 | `static String advise(Game g)` | WHAT THE CITY DOES NEXT, AND WHY THIS IS NOT A LIST OF RULES ANY MORE. |
| 3465 | 31 | `static void ironWhenNeeded(Game g)` | IRON, A WHOLE FIELD AT A TIME (0.7.64, batch L). |
| 3504 | 11 | `static String buyWhole(Game g, LandParcel p, String purpose)` | Buys an offer as the land office does (0.7.64): out of the cash when it covers it - "cash" - or, converting and short, the funding page's BUILD_BOND_YEARS bond for the gap when canService() carries it, then the offer ... |
| 3524 | 3 | `static double[] fieldEarnings(Game g, int sites)` | What a field of `sites` iron sites would earn the city's mines a month (0.7.67, M3b): {mines, their monthly profit} - the build advice's since 0.7.101 (BuildAdvice.fieldEarnings(), moved there whole so Build's no-depo... |
| 3529 | 3 | `static double fieldPayment(Game g, LandParcel field)` | The month's payment that repays a field's price here over Game.BUILD_BOND_YEARS (0.7.67, M3b) - the build advice's since 0.7.101 (BuildAdvice.fieldPayment()). |
| 3548 | 6 | `static double fuelBoughtAbroad(Game g)` | The fuel the world sold the city last month (0.7.62): its drivers' (Game.getHouseholdFuelImports()) and its railway's (Freight.getFuelImported()) - their petrol and its diesel since 0.7.76 - and since 0.7.83 (batch O6... |
| 3580 | 49 | `static void countFuel(Game g)` |  |

### THE PORTS (0.7.86, batch O9; docs/studies/spec-oil.md 5's O9 row) (lines 3630-4870)

| line | len | member | says |
|---:|---:|---|---|
| 3667 | 19 | `static void portsWhenNeeded(Game g)` |  |
| 3688 | 5 | `static int terminalsStanding(Game g)` | The sea terminals standing, all kinds. |
| 3704 | 3 | `static BuildingsTemplate terminalFor(Game g, Ports.Cargo k, double uncovered, double dial)` | The catalogue's terminal to order for a kind of cargo, or null (FR3, docs/queue/FR3.md 5.1): the LARGEST of the kind's terminals that pays back (Ports.berthPaysBack()) at `uncovered` tonnes a month and the port dues d... |
| 3709 | 14 | `static void countPorts(Game g)` | The month's boats and sea tonnes, counted (the calls without their quays: the map places them, since 0.7.97 on its sea routes). |
| 3725 | 12 | `static String portsLine(Game g, int calls)` | The ports this month, a line: by kind, tonnes in and out across the boundary, the berths standing, the share in force and the sea tonnes; the calls. |
| 3752 | 17 | `static void countRefiners(Game g)` |  |
| 3771 | 31 | `static String refineryLine(Game g)` | The refiners' planner this month, a line: what is short and spare, and the best-earning candidate (and the Oil Refinery's package) with the hurdle each money gate asks of it. |
| 3832 | 3 | `static double groundAheadUtilisation(Game g)` | The most of its dry ground a look leaves built on (0.7.58, J1d): the ground in use grown BuildAdvice.HORIZON months by the businesses' growthFactor(), with BuildAdvice.SLACK past it - the build advice's own sizing (0.... |
| 3860 | 8 | `static double roomToGrow(Game g)` | ROOM TO GROW, PRICED FROM THE LINE (0.7.67, batch M3b): the output a look's room-to-grow move is weighed at - none while no more of the dry ground is built on than groundAheadUtilisation(), rising to the whole month's... |
| 3879 | 21 | `static void keepGroundAhead(Game g)` | GROUND KEPT AHEAD (see advise()): buys the best value for room while more of the dry ground is built on than groundAheadUtilisation(), the offers together costing no more than GROUND_AHEAD_CASH_SHARE of the cash at th... |
| 3916 | 36 | `static void addWater(java.util.List<Move> moves, Game g, double lost)` | WATER, AND WHERE IT COMES FROM (0.7.59, batch J2; spec-land 3, J2's playtest rule). |
| 4010 | 11 | `static int[] layoutStarts(int target)` |  |
| 4023 | 7 | **type** `static final class LayoutSnap` | One district in one month: its boxes {type << 40 \| x << 20 \| y} sorted, its count of boxes by type, what was carried in to it by type (null for none) and as the {month, code} pairs it came in (carryLog, null for none)... |
| 4057 | 5 | `static boolean layoutCarries(int[] carryIn)` | Whether a carry in, counted by type, holds anything. |
| 4064 | 6 | `static int layoutWindowOf(int month)` | Which window a month falls in (its index in LAYOUT_STARTS), -1 for none. |
| 4072 | 3 | `static long layoutKey(CityMap.District d)` | A district's place in a map, the same in every month: its column and row from the founding site's. |
| 4077 | 26 | `static void layoutMonth(Game g)` | What the playtest's month asks of the watch, after the month ran: nothing outside a window, else the month planned, compared with the month before, and the first and last months' counts. |
| 4105 | 42 | `static Map<Long, LayoutSnap> layoutSnapshot(CityMap m)` | Every district planned through the map's own chain, then each one's boxes decoded (CL3Probe's way: tile k, x = b & 31, y = (b >> 5) & 31, type = b >>> BOX_TYPE_SHIFT), the packed ones left out. |
| 4157 | 6 | `static boolean layoutSameCarry(int[] a, int[] b)` | Whether two carries in leave a district the same history (none is an empty list): the same {month, code} pairs in the same order, or the one the other with pairs appended - CL3's promise is that an arrival appended mo... |
| 4165 | 4 | `static String layoutName(Game g, int type)` | A building type's words for a finding: its name by id, else its id. |
| 4171 | 44 | `static void layoutCompare(Game g, CityMap m, int month, Map<Long, LayoutSnap> before, Map<Long, LayoutSnap> now)` | Two months' snapshots compared district by district (the districts in both): the moves, as the section above counts them. |
| 4217 | 26 | `static double[] layoutRingOf(CityMap m)` | The mean distance from the founding site of the boxes of each use (CL3Probe's measure: a box's middle from the founding site, in the district's frame, in km). |
| 4245 | 3 | `static int layoutBand(BuildingVisual.Type t)` | A use's band: 2 the outer kinds, 1 industry (not outer), 0 the homes band (the rest). |
| 4252 | 8 | `static String layoutBandsOf(CityMap m, int month, Map<Long, LayoutSnap> now)` |  |
| 4266 | 25 | `static void layoutKeepOldWorks(CityMap m, int month, Map<Long, LayoutSnap> now)` | The oldest works: every district's industry boxes and its count of boxes by type, kept at the first window's first month that has industry on the map (the playtest's city has none at m1001, a village of 176 boxes: kep... |
| 4293 | 7 | `static void layoutTrackLow(CityMap m)` | Every month since the oldest works were kept: each district's lowest model count of every type so far. |
| 4302 | 49 | `static void layoutFinish(Game g, CityMap m, int month, Map<Long, LayoutSnap> now)` | The last window's last month: the ring, and the oldest works found again where they stood. |
| 4353 | 36 | `static List<String> layoutLines(long runSeconds)` | The report's lines for the watch: the windows, the moves a month, the ring, the oldest works; the run's seconds beside the windows'. |
| 4401 | 148 | `static void addRoadThrottle(java.util.List<Move> moves, Game g, double lost)` | The road constraint, and every way there is of easing it. |
| 4560 | 18 | `static java.util.List<BuildAdvice.Suggestion> roadsByNeed(Game g, BuildAdvice.Measure roadM, double free, java.util.Set<String>...` | The roads by need (0.7.103, batch P4): the build advice's card for the road (BuildAdvice.suggestFor(), no NEEDS YOU row, the cash and the free ground as they stand, its own slack) among the road kinds named and the ci... |
| 4584 | 15 | `static boolean pave(Game g, int n)` | Paves n gravel roads (0.7.70, N1): out of the cash, or, short of it, with the long bond build() borrows on, sized to what the cash is short of, when the city can service it (canService()). |
| 4612 | 4 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost)` | Adds one building as a way of relieving a constraint worth `lost` a month. |
| 4621 | 22 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost, double gap)` | how many of the building to order |
| 4655 | 16 | `static int linesThatPay(Game g, BuildingsTemplate t, int most)` | The most of these transit lines, up to most, that pay their way this month (0.7.49, D3): the output the road gives back with them standing (the month's GDP times the throughput they add, on the network as it would be ... |
| 4673 | 9 | `static double runningCost(Game g, BuildingsTemplate t)` | What one of these costs the city a month to run, fully staffed at today's wages. |
| 4684 | 4 | `static boolean wouldPay(Game g, String name, String sector)` | Whether one of these would clear its own running costs, on the private sector's screen. |
| 4689 | 4 | `static int qty(Game g, String name)` |  |
| 4695 | 3 | `static int qtyOf(Game g, int id)` | ...of a building by its permanent id (0.7.71: the childcare ids, renamed). |
| 4700 | 4 | `static String nameOf(Game g, int id)` | A building's name by its permanent id, null with none (0.7.120, batch I3; spec-materials 4): the player finds the Iron Mine and the Electric Arc Furnace by their ids, so a rename - the Steel Foundry's at I3 - cannot l... |
| 4706 | 7 | `static int homeBuildings(Game g)` | The city's home buildings standing, every RESIDENTIAL type (0.7.71: what childcare is counted against). |
| 4715 | 3 | `static boolean villageBuild(Game g, String name, int quantity)` | The founding village's builds: build(), and on an Insane founding the funding page's bond for a stage the treasury is short of (0.7.14). |
| 4723 | 72 | `static boolean build(Game g, String name, int quantity)` | Orders a building the way the screens do: check land, buy some if short, borrow if the treasury cannot cover it, then place the order. |
| 4822 | 1 | `static double fxRate(Game g)` | Local per USD, for turning a local-money need into a dollar ask. |
| 4824 | 18 | `static boolean canService(Game g, double extra)` |  |
| 4853 | 3 | `static void refusal(String why)` |  |
| 4857 | 8 | `static BuildingsTemplate template(Game g, String name)` |  |
| 4867 | 3 | `static String byId(int id)` | A building named by its permanent id in a fixture's list of orders, "#11", which template() reads (0.7.120, batch I3): the lists name the arc furnaces and the Iron Mine so, a name being a label the chains rename. |

### RUNNING MONTHS (lines 4871-5117)

| line | len | member | says |
|---:|---:|---|---|
| 4935 | 24 | `static void countTheHolders(Game g)` |  |
| 4971 | 8 | `static void countTheCurve(Game g)` |  |
| 4980 | 10 | `static void countTheCentralBank(Game g)` |  |
| 4991 | 13 | `static void countTheCitysPaper(Game g)` |  |
| 5005 | 112 | `static void run(Game g, int months)` |  |

### SAVE / RELOAD, MID-RUN (lines 5118-5420)

| line | len | member | says |
|---:|---:|---|---|
| 5127 | 287 | `static void roundTrip(Game g, GameFiles files, int slot)` |  |
| 5415 | 5 | `static void same(int month, String what, double actual, double expected)` |  |

### (untitled) (lines 5421-5428)

### what the accounts added back (0.7.136, batch FX1) (lines 5429-5453)

| line | len | member | says |
|---:|---:|---|---|
| 5441 | 12 | `static void countAddedBack(Game g)` | The month's write-offs the accounts added back, counted term by term, and each term's first few printed - nothing printed for a harness that borrows run() with no report to print to. |

### the city's fund and the bank's rescue (0.7.14) (lines 5454-5541)

| line | len | member | says |
|---:|---:|---|---|
| 5468 | 10 | `static void acceptTheOffer(Game g)` | ACCEPTS THE BANK'S OFFER, the way the page lets a player: a treasury short of it takes the funding page's first offer - the build screen's twenty-year bond, sized to the gap - and then pays. |
| 5485 | 6 | **type** `static final class Stake` | THE CITY'S STAKE IN ITS BANK AFTER EACH RESCUE: at the resolution, then five, ten and twenty-five years on, and the first months it is under half and under a tenth - until the next resolution takes it back to everything. |
| 5501 | 40 | `static void watchTheStake(Game g)` |  |

### the fund's cost basis (0.7.39) (lines 5542-5589)

| line | len | member | says |
|---:|---:|---|---|
| 5557 | 7 | `static double[] ledgerSnap(Game g)` |  |
| 5565 | 21 | `static void watchTheLedger(Game g)` |  |
| 5588 | 1 | `static String pct(double v)` | A stake, in words: "84.4%", or "-" before it was read. |

### an Insane founding (0.7.14) (lines 5590-7515)

| line | len | member | says |
|---:|---:|---|---|
| 5604 | 10 | `static void borrowForTheVillage(Game g)` | THE PLAYER OF AN INSANE CITY BORROWS FIRST: the build screen's twenty-year bond, sized to the village's invoice at a new city's prices (Founding.whatItBuys()), before it places a house - the treasury holds nothing and... |
| 5622 | 13 | `static boolean buildOnTheFundingPage(Game g, String name, int quantity)` | ...AND BUILDS THE REST OF IT THE SAME WAY: a stage the treasury is short of is borrowed for on the funding page's bond, sized to the gap (Game.buildFundingGap()), and placed - the page a player is shown, where the adv... |
| 5636 | 1634 | `public static void main(String[] args) throws Exception` |  |
| 7277 | 4 | `static String landmassesLine(LandManager ground)` | The report's line for the city's ground by landmass (ET5), printed after ITS GROUND AT THE END: "  ITS LANDMASSES AT THE END: Home 177.7 km2, Island 2 3.1 km2 (not linked); terminals off home 0" (LandmassView.reportLi... |
| 7288 | 20 | `static void keepCity(Game g, Path root, Path keep)` | The run's last city saved as slot 10 and copied into keep/data/saves (FR7, 2026-10-18), for FR7's headless renders of a played city's map (FR7's render tool, beside its notes in docs/queue/FR7/). |
| 7321 | 5 | `static String shortTag(String key)` | A sector's name in three or four characters, for the checkpoint line. |
| 7328 | 11 | `static String tagAt(String key, int extra)` | shortTag()'s rule with the first word `extra` letters longer: the initials of a name of several words, the first three letters (and `extra` more) of one. |
| 7348 | 4 | `static String cut(String key, int letters)` | A company's name cut to `letters` characters for a trace line (0.7.136, batch FX1): the whole of a shorter one - "Oil", which the bank trace's substring(0, 4) threw on - and, where two of Equity.COMPANIES (the sectors... |
| 7358 | 19 | `static Map<String, String> distinctCuts(String[] keys, java.util.function.BiFunction<String, Integer, String> cut)` | Each key's cut, `cut.apply(key, extra)`, its `extra` raised a step at a time for every key whose cut another shares, until no two are alike - or the cuts stop growing, after a key's whole length (0.7.136). |
| 7389 | 17 | `static String creditEra(Game g)` | The business economy at a checkpoint, on one line: per sector its cash, write-downs and months of ban left, then hunger and the shelf. |
| 7415 | 33 | `static String bankEra(Game g)` | The bank as a business at a checkpoint, on one line (0.7.7): its price build-up at the dial - funds-transfer price, running costs, expected loss and capital charge, adding to prime - then what it paid savers and what ... |
| 7450 | 7 | `static double depositBeta()` | The run's deposit rate regressed on the dial: the share of a move in the policy rate savers saw, over every month. |
| 7458 | 57 | `static String era(Game g, String label)` |  |

### THE DOOR FOR ui.UiProbe (UP1, 2026-10-23) (lines 7516-7645)

| line | len | member | says |
|---:|---:|---|---|
| 7544 | 34 | `public static Game probeCity(GameFiles files, String earthSpot)` | For ui.UiProbe: the city main() founds - Danzik on the flag's preset, or the real Earth round {@code earthSpot} when it is not null - with the first builds and six months played, on {@code files}. |
| 7584 | 26 | `public static void probePlay(Game g, int toMonth)` | For ui.UiProbe: the playtest's loop (long skips, the advisor's looks and moves) played on {@code g} until it reaches {@code toMonth}; each month is timed for probeMonthMs(). |
| 7612 | 8 | `public static double probeMonthMs(int from, int to)` | For ui.UiProbe: the mean model month, in milliseconds, over the months from (inclusive) to (exclusive) that probePlay() has timed; NaN when none. |
| 7621 | 8 | `private static void probeRun(Game g, int months)` |  |
| 7630 | 7 | `static String money(double v)` |  |
| 7638 | 7 | `static void cleanUp(Path root)` |  |

