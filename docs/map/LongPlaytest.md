# LongPlaytest.java - 6,632 lines · 111 methods · 84 constants · harnesses

`ham/citybuildersim/LongPlaytest.java` - generated 2026-10-18 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (112), [Good](Good.md) (44), [BusinessDebtManager](BusinessDebtManager.md) (38), [Sector](Sector.md) (28), [BuildingsTemplate](BuildingsTemplate.md) (28), [Equity](Equity.md) (26), [Sectors](Sectors.md) (24), [Exchange](Exchange.md) (24), [CareType](CareType.md) (23), [BuildAdvice](BuildAdvice.md) (22), [Bank](Bank.md) (20), [Ports](Ports.md) (17), [AgeBand](AgeBand.md) (14), [WageBand](WageBand.md) (14), [SpreadPlanner](SpreadPlanner.md) (14), [Crime](Crime.md) (13), [Founding](Founding.md) (13), [DebtManager](DebtManager.md) (11), [JobType](JobType.md) (11), [LandParcel](LandParcel.md) (11), [BuildingManager](BuildingManager.md) (10), [InfrastructureManager](InfrastructureManager.md) (9), [BuildingType](BuildingType.md) (9), [FundLedger](FundLedger.md) (9), [EconomyManager](EconomyManager.md) (8), [HouseholdBalance](HouseholdBalance.md) (8), [TaxPolicy](TaxPolicy.md) (8), [LandManager](LandManager.md) (7), [FamilyModel](FamilyModel.md) (7), [ForeignAccounts](ForeignAccounts.md) (7)... and 67 more

**Used by (35):** [AutoBuildCheck](AutoBuildCheck.md), [BondCheck](BondCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [CarCheck](CarCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChildcareCheck](ChildcareCheck.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [GroceryCheck](GroceryCheck.md), [IndustryViewCheck](IndustryViewCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [MapCheck](MapCheck.md), [MonetaryCheck](MonetaryCheck.md), [MortgageCheck](MortgageCheck.md), [OilCheck](OilCheck.md), [OilViewCheck](OilViewCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [PlanCheck](PlanCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PortCheck](PortCheck.md), [RefineryViewCheck](RefineryViewCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorStatementCheck](SectorStatementCheck.md), [ShadowBasket](ShadowBasket.md), [WellCheck](WellCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 50 | FINDINGS |
| 69 | · `worst` MEANT `first`, AND THE PRINTOUT SAID SO (2026-09-09). |
| 111 | THE AUDIT |
| 182 | · A mechanic that is not counted over a real run is a mechanic nobody |
| 1147 | · · AND NOTHING MOVED AFTER THE AUDIT STRUCK. |
| 1732 | THE ADVISOR |
| 1750 | WHO IS PLAYING (2026-09-17) |
| 1808 | THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green |
| 1837 | THE FOUNDING, AS A CHOICE (0.7.10) |
| 1865 | ON THE REAL EARTH (0.7.121, batch E4; the project's spec-earth.md 8) |
| 1904 | THE TRACE, BESIDE THE REPORT (0.7.10) |
| 2309 | THE CITY'S FUND AND THE BANK'S RESCUE, STATED (0.7.14) |
| 2347 | WAGES AGAINST THE INDEX (2026-09-21) |
| 2371 | THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21) |
| 2390 | THE HOLDINGS DIAL, HELD (0.7.1) |
| 2407 | THE CEILING, SET (0.7.2) |
| 2422 | THE TARGET, SET (0.7.4) |
| 2736 | · THE TWO THINGS THAT ARE NOT PURCHASES, done first and for free. |
| 2791 | · AND EVERYTHING THAT IS A PURCHASE. |
| 3136 | · AND THE BEST OF THEM WINS. |
| 3370 | THE PORTS (0.7.86, batch O9; docs/studies/spec-oil.md 5's O9 row) |
| 4158 | RUNNING MONTHS |
| 4368 | SAVE / RELOAD, MID-RUN |
| 4665 | (untitled) |
| 4673 | · what the accounts added back (0.7.136, batch FX1) |
| 4698 | · the city's fund and the bank's rescue (0.7.14) |
| 4786 | · the fund's cost basis (0.7.39) |
| 4834 | · an Insane founding (0.7.14) |
| 4930 | · · founding: a few months at a time, by hand |
| 5025 | · · then the real rhythm |
| 5173 | · · the report |
| 5273 | · · BUSINESS SERVICES - and the point of printing it is the MECHANISM, |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 48 | `LongPlaytest.TARGET_MONTHS` | `4000` |  |
| 58 | `LongPlaytest.findings` | `new LinkedHashMap<>()` |  |
| 164 | `LongPlaytest.illnessDeathsByBand` | `new double [ AgeBand.values().length ]` | The long sick (2026-09-11): who died of staying sick, by band, and the most ever ill past two months. |
| 166 | `LongPlaytest.deathsByBandRun` | `new double [ AgeBand.values().length ]` | Everyone who died, by band, and the orphans and the unhoused among them (2026-09-11). |
| 205 | `LongPlaytest.FAR_FROM_PARITY` | `2.0` | How far from parity, as a multiple of it, a weak currency has to be to count as far from it. |
| 221 | `LongPlaytest.dialPath` | `new java.util.ArrayList<>()` | Every month's policy rate, for the dial's min, median and max over the run. |
| 223 | `LongPlaytest.inflationPath` | `new java.util.ArrayList<>()` | Every month's inflation reading, once the index has a year to read, for its median. |
| 232 | `LongPlaytest.spendPath` | `new java.util.ArrayList<>()` | Every month's spend factor (0.7.3): the share of their spending above subsistence the households planned at, on the month's real deposit rate - HouseholdBalance.getSpendFactor() after the month. |
| 250 | `LongPlaytest.bankNii` | `new double [ 12 ], bankFees = new double [ 12 ], bankOther = new double [ 12 ...` | THE BANK AS A BUSINESS (0.7.7): a trailing year of its statement, for the checkpoint line that prints its price build-up beside its margin, its cost ratio, its fee share and its return on equity - and the run's deposi... |
| 267 | `LongPlaytest.bankYears` | `new java.util.ArrayList<>()` | THE BANK'S CAPITAL, YEAR BY YEAR (0.7.8): its capital ratio and its own target averaged over each year, its return on the equity it opened the year with, its provisions over the loans it made (the businesses' and the ... |
| 290 | `LongPlaytest.watchSpell` | `new java.util.HashMap<>()` |  |
| 303 | `LongPlaytest.refusedOnPrice` | `new java.util.TreeMap<>()` |  |
| 313 | `LongPlaytest.houseReasons` | `new java.util.TreeMap<>()` |  |
| 357 | `LongPlaytest.openedByStance` | `new java.util.TreeMap<>()` |  |
| 512 | `LongPlaytest.deskOverEquity` | `new java.util.ArrayList<>()` |  |
| 524 | `LongPlaytest.sharePriceOverFair` | `new java.util.ArrayList<>()` | 0.7.12 ROUND 2: the shares on the book, the dividends, and the default point - month by month, for the report's round-2 lines. |
| 528 | `LongPlaytest.refusedAtLineBySector` | `new java.util.LinkedHashMap<>()` |  |
| 563 | `LongPlaytest.deskBookAtFair` | `new java.util.ArrayList<>(), deskLargestPosition = new java.util.ArrayList<>()` |  |
| 633 | `LongPlaytest.companyTillsRun` | `new java.util.ArrayList<>()` | ---- 0.7.12 round 4: the companies' cash, the desk's excess, and can't pay means default ---- |
| 644 | `LongPlaytest.interimSeen` | `new java.util.IdentityHashMap<>()` |  |
| 645 | `LongPlaytest.interimLost` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` |  |
| 646 | `LongPlaytest.cannotPayBySector` | `new java.util.TreeMap<>()` |  |
| 647 | `LongPlaytest.cannotPayForgivenBySector` | `new java.util.TreeMap<>()` |  |
| 648 | `LongPlaytest.cannotPayByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 649 | `LongPlaytest.cannotPayForgivenByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 650 | `LongPlaytest.cannotPaySpell` | `new java.util.HashMap<>()` |  |
| 653 | `LongPlaytest.cannotPaySpellBanned` | `new java.util.HashMap<>()` |  |
| 656 | `LongPlaytest.limitedBySector` | `new java.util.TreeMap<>()` | ---- round 6: buy only what it can pay for ---- |
| 657 | `LongPlaytest.forgoneBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 658 | `LongPlaytest.shelfShortBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 659 | `LongPlaytest.limitedLastMonth` | `new java.util.HashSet<>()` | all, after a cut |
| 662 | `LongPlaytest.defaultsOnStockBySector` | `new java.util.TreeMap<>()` |  |
| 663 | `LongPlaytest.defaultsAfterCutBySector` | `new java.util.TreeMap<>()` |  |
| 664 | `LongPlaytest.luxuryLeverage` | `new java.util.ArrayList<>()` |  |
| 665 | `LongPlaytest.RETAILERS` | `java.util.Set.of("Retail", "Restaurants", "Luxury Retail")` |  |
| 728 | `LongPlaytest.wageSpell` | `new java.util.HashMap<>()` | ---- round 7: a sector paying out more in wages than it takes in, on interim loans; a new sector's first year ---- |
| 729 | `LongPlaytest.wageSpells` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 730 | `LongPlaytest.firstPlant` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 731 | `LongPlaytest.bannedFirstYear` | `new java.util.TreeMap<>()` |  |
| 733 | `LongPlaytest.firstBillDefault` | `new java.util.TreeMap<>()` | ---- round 8: a cash-flow default on a new sector's first bill: {the month, 1 if it was lent in the interim} ---- |
| 737 | `LongPlaytest.lastProject` | `new java.util.HashMap<>()` | ...a ceiling default in the month after the sector bought plant with a loan; and a shell: a sector holding no plant, nothing built and nothing on site, that owes something - its defaults, what they left unpaid and wha... |
| 739 | `LongPlaytest.shellDefaults` | `new java.util.TreeMap<>()` |  |
| 740 | `LongPlaytest.shellSpell` | `new java.util.HashMap<>()` |  |
| 1030 | `LongPlaytest.OLD_DIAL_STOP` | `.25` | The dial's stop before 0.7.2, for counting the months the uncapped dial spends past it. |
| 1780 | `LongPlaytest.ATTENTIVE` | `"attentive".equalsIgnoreCase(System.getProperty("playtest.player", "occasiona...` | True when this run is played by somebody paying attention. |
| 1787 | `LongPlaytest.SCHOOLS` | `Boolean.getBoolean("playtest.schools")` | -Dplaytest.schools=true: the city builds schools, which the advisor never does. |
| 1790 | `LongPlaytest.CHILDCARE` | `Boolean.getBoolean("playtest.childcare")` | -Dplaytest.childcare=true (0.7.71, batch N2): the city builds childcare by the build advice's own card, which the advisor never does. |
| 1829 | `LongPlaytest.POLICY_RATE` | `System.getProperty("playtest.policyRate") = = null ? null : Double.valueOf(Sy...` | The rate the dial is held at under -Dplaytest.policyRate, or null when the advisor sets it. |
| 1857 | `LongPlaytest.FOUNDING` | `Founding.Preset.valueOf(System.getProperty("playtest.founding", "standard").t...` | The founding preset under -Dplaytest.founding, standard when unset. |
| 1963 | `LongPlaytest.TRACE` | `System.getProperty("playtest.trace")` | The trace's prefix under -Dplaytest.trace, or null. |
| 1966 | `LongPlaytest.paperSeen` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` | The paper already written to the borrow file, by identity. |
| 2298 | `LongPlaytest.AUTOPILOT` | `Boolean.getBoolean("playtest.autopilot")` | -Dplaytest.autopilot=true (0.7.0): the rule holds the dial from founding, through the game's own autopilot (DebtManager), and the advisor keeps its hands off it. |
| 2306 | `LongPlaytest.ROLLOVER` | `Rollover.Mode.valueOf(System.getProperty("playtest.rollover", "SAME_STRUCTURE...` | -Dplaytest.rollover=MANUAL\|SAME_STRUCTURE\|TWELVE_MONTH_BILL (0.7.13): the treasury's rollover for the run (Rollover). |
| 2333 | `LongPlaytest.RESCUE_AUTO` | `! "BUTTON".equalsIgnoreCase(System.getProperty("playtest.rescue", "AUTO").tri...` | The rescue setting under -Dplaytest.rescue, AUTO when unset. |
| 2336 | `LongPlaytest.PREFERRED_ACCEPT` | `! "DECLINE".equalsIgnoreCase(System.getProperty("playtest.preferred", "ACCEPT...` | The answer to the bank's offer under -Dplaytest.preferred, ACCEPT when unset. |
| 2339 | `LongPlaytest.FUND_DIAL` | `fundDial(System.getProperty("playtest.fund", "0"))` | The fund's dial under -Dplaytest.fund, 0 when unset. |
| 2369 | `LongPlaytest.WAGES` | `Boolean.getBoolean("playtest.wages")` | -Dplaytest.wages=true: the wage index, the price index and the lag-implied level at each checkpoint. |
| 2388 | `LongPlaytest.BORROW_AT_HOME` | `Boolean.getBoolean("playtest.borrowAtHome")` | -Dplaytest.borrowAtHome=true: the advisor's borrowing goes to the city's own term bonds, never abroad. |
| 2404 | `LongPlaytest.QE_SHARE` | `System.getProperty("playtest.qeShare") = = null ? null : Double.valueOf(Syste...` | The holdings dial under -Dplaytest.qeShare, or null when nobody sets it. |
| 2419 | `LongPlaytest.ADVANCES_MONTHS` | `System.getProperty("playtest.advancesMonths") = = null ? null : Double.valueO...` | The advances ceiling under -Dplaytest.advancesMonths, in months of revenue, or null for the default. |
| 2444 | `LongPlaytest.MONEY_GATE` | `System.getProperty("playtest.moneyGate", "").trim()` | THE MONEY GATE, FOR THE COUNTERFACTUAL (0.7.82, batch O5; turned round at 0.7.112): since 0.7.112 the refiners' spread planner tests every order on its whole cost, whoever pays (SpreadPlanner.ON_ITS_WHOLE_COST, Jerus'... |
| 2447 | `LongPlaytest.REFINERY_TRACE` | `Boolean.getBoolean("playtest.refinery")` | -Dplaytest.refinery=true (0.7.82): the refiners' planner yearly - its outlook and its best candidates under both money gates (refineryLine()). |
| 2450 | `LongPlaytest.INFLATION_TARGET` | `System.getProperty("playtest.inflationTarget") = = null ? null : Double.value...` | The inflation target under -Dplaytest.inflationTarget, a fraction a year, or null for the default. |
| 2603 | `LongPlaytest.CHILDCARE_SEEN` | `{ 1_000, 2_000, 3_000, 4_000 }` | CHILDCARE ON THE MAP (0.7.139, batch CC1). |
| 2606 | `LongPlaytest.childcareSeen` | `new java.util.ArrayList<>()` | ...what it said at each, in order. |
| 2628 | `LongPlaytest.BUDGET_EVERY` | `Integer.getInteger("playtest.budget", 0)` | -Dplaytest.budget=N (0.7.139, batch CC1): every N months the treasury's month, line by line - what came in and what went out, care by kind and childcare's own running (its centres' posts and upkeep) beside it. |
| 2653 | `LongPlaytest.childcareBuilt` | `new java.util.TreeMap<>()` | ...the buildings, by name. |
| 2656 | `LongPlaytest.schoolsOrdered` | `new java.util.HashMap<>()` | What the flag has ordered of each school, so one under construction is not ordered twice. |
| 3274 | `LongPlaytest.OIL_FUEL_IMPORTS_SHARE` | `.01` | The share of a month's GDP the fuel bought abroad has to pass before the test player buys oil (0.7.62): spec-land 3's K entry, 1%. |
| 3304 | `LongPlaytest.refinedRun` | `new java.util.EnumMap<>(Good.class)` | ...and the refinery's products over the run (0.7.76, batch O1): made, by product, and what of them its tanks could not hold (spec-oil 6's joint-products risk). |
| 3388 | `LongPlaytest.terminalsOrdered` | `new java.util.TreeMap<>()` | Terminals the test player ordered, by name; the looks that found a berth's worth with no coast; the first terminal's month. |
| 3397 | `LongPlaytest.PORTS_TRACE` | `Boolean.getBoolean("playtest.ports")` | -Dplaytest.ports=true (0.7.86): the ports yearly - each kind's tonnes across the boundary, its berths, its share at sea and the month's calls (portsLine()). |
| 3472 | `LongPlaytest.refinersOpened` | `new java.util.TreeMap<>(), refinersSold = new java.util.TreeMap<>()` | THE SPREAD PLANNER'S BUILDINGS OVER THE RUN (0.7.82, batch O5): each of the refiners' buildings opened and sold back, and the crude units and the conversion units standing at most. |
| 3536 | `LongPlaytest.GROWTH_DISCOUNT` | `.15` | How much of a gain arrives later rather than now. |
| 3543 | `LongPlaytest.GROUND_AHEAD_CASH_SHARE` | `.10` | The share of the treasury's cash one look spends keeping ground ahead (0.7.58, J1d): a tenth, the share the war chest tops the reserves up from and the every-13th-stop purchase is held under. |
| 4106 | `LongPlaytest.DEBT_SERVICE_LIMIT` | `.25` | Whether the advisor can afford the PAYMENTS, not whether it likes the size. |
| 4138 | `LongPlaytest.refusals` | `new LinkedHashMap<>()` | Why the advisor could not do the thing it wanted to. |
| 4676 | `LongPlaytest.ADDED_BACK_TERMS` | `{ "food", "materials", "luxuries", "held goods" }` | The inventory terms in NationalAccounts.getWrittenOffAddedBack()'s order. |
| 4678 | `LongPlaytest.addedBackMonths` | `new int [ ADDED_BACK_TERMS.length ], addedBackFirst = { - 1, - 1, - 1, - 1 },...` | Months each term added a write-off back, the first, and the largest with its month. |
| 4680 | `LongPlaytest.addedBackWorst` | `new double [ ADDED_BACK_TERMS.length ]` |  |
| 4682 | `LongPlaytest.EVENTS_PRINTED` | `12` | The events printed as they happen, each term's first few: enough to say where a trace moved. |
| 4735 | `LongPlaytest.stakes` | `new ArrayList<>()` |  |
| 4743 | `LongPlaytest.mostHeld` | `new double [ Equity.COMPANIES.length ]` |  |
| 6480 | `LongPlaytest.DISTINCT_ROUNDS` | `64` | The most letters distinctCuts() adds to a name: past the longest key there is nothing left to add. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `static PrintStream out` | Where the report goes. |
| 61 | `int count` |  |
| 62 | `int firstMonth` |  |
| 63 | `int lastMonth` |  |
| 64 | `String worst` |  |
| 65 | `double worstSize` |  |
| 66 | `int worstMonth` |  |
| 119 | `static double worstCrowding` | How far past the buildings' comfortable capacity the city was pulled. |
| 120 | `static int worstCrowdingMonth` |  |
| 139 | `static double worstUnemployment` | Unemployment, watched rather than asserted. |
| 140 | `static double lastUnemployment` |  |
| 142 | `static double totalEvicted` | The people outside the families, over the run (2026-09-11). |
| 150 | `static double usedOffered` | The second-hand car market over the run (2026-09-17). |
| 151 | `static int worstUnhousedMonth` |  |
| 153 | `static int monthsAnyTierDeclining` |  |
| 154 | `static int monthsPeopleLeft` |  |
| 159 | `static int outbreaks` | Sickness. |
| 160 | `static int monthsInOutbreak` |  |
| 161 | `static boolean wasInOutbreak` |  |
| 162 | `static double worstSickRate` |  |
| 167 | `static double orphanDeathsRun` |  |
| 168 | `static double allDeaths` |  |
| 169 | `static int worstLongSickMonth` |  |
| 176 | `static double personMonths` | The price at the clinic door (2026-09-19): person-months lived, so the deaths can be read per thousand a year; the people the fee turned away, summed for the mean; and the fees the households skipped, summed over the ... |
| 178 | `static double studentInterestSum` | The price of a place (2026-09-21): the interest the graduates paid and the grants paid, summed over the run. |
| 180 | `static double crimeVsSum` | Crime (2026-09-11): the rate against Canada's summed for the mean, the worst, and what it did over the run. |
| 189 | `static double peakSeats` |  |
| 190 | `static int peakSeatsMonth` |  |
| 191 | `static int monthsWithSeats` |  |
| 192 | `static int lastSeatMonth` |  |
| 193 | `static double serviceExportsRun` |  |
| 194 | `static double caughtRun` |  |
| 201 | `static double peakRate` | The currency over the run (2026-09-21): its dearest dollar and when, and how many months it spent far from parity. |
| 202 | `static int peakRateMonth` |  |
| 219 | `static int monthsAtGuard` | THE CURRENCY'S GUARD AND THE DIAL'S PATH, over the run (0.7.2). |
| 234 | `static double spendLow` | ...its lowest and highest, and the first month each was struck in. |
| 235 | `static int spendLowMonth` |  |
| 237 | `static int depositCappedMonths` | Months the bank's interest margin could not pay the deposit rate it chose, and paid what the margin had (Bank.isDepositPayoutHeld(), 0.7.7), and the first and last of them. |
| 253 | `static int bankMonths` |  |
| 254 | `static double shareSum` |  |
| 255 | `static int shareMonths` |  |
| 268 | `static double yrRatio, yrTarget, yrNet, yrProv, yrBook, yrDiv, yrBuyback, yrIssued, yrOpenEquity, yrOpenPop` |  |
| 269 | `static int yrMonths, yrFailuresOpen, yrFrozen, yrBranchMonths` |  |
| 270 | `static int rationedMonths, keepGoingMonths, payingMonths, returningMonths, rebuildingMonths` |  |
| 271 | `static double runDividends, runBuybacks, runIssued, runProvisions` |  |
| 273 | `static double runLoanMonths, runWrittenOff` | Every month's loans (the businesses' and the families') summed, and every write-off: the run's provisions over its average book, through the cycle rather than over the growth years alone. |
| 286 | `static int backstops, sliceMonths, newsMonths, pastWatchMonths, pastTriggerMonths, longestWatchSpell, longe...` | HOW THE DEFAULTS ARRIVE (0.7.8, a sector defaults a slice at a time): the backstop's whole-sector write-downs; the months any slice was written off, and the months one was news (BusinessDebtManager .defaultsAreNews())... |
| 288 | `static String longestWatchSector` |  |
| 289 | `static double sliceWrittenOff, worstWriteOffShare, worstWriteOff, lentPastWatch, runAllowanceUsed` |  |
| 291 | `static Notice lastDefaultNotice` |  |
| 301 | `static int loansPastWatch, loansPastOne, loansPastT, projectsPastOne` | ROUND 2 (0.7.8): the price off the curve and the plant sold as materials. |
| 302 | `static double lentPastOne, lentPastT, rateSumPastOne, rateMaxPastOne, rateSumPastT, rateMaxPastT` |  |
| 311 | `static int mortgagesWritten` | THE LANDLORDS' MORTGAGES (0.7.11): what they wrote, what the insurance took and paid, and every month's reason the landlords built or did not, counted by its words (houseReason()) - the batch's ensembles were read off... |
| 312 | `static double mortgagesLent, premiumsRun, claimsRun` |  |
| 346 | `static int salvageBuildingsDistress, salvageBuildingsSpare` |  |
| 347 | `static double salvageUnits, salvageUnitsBought, salvagePaid, salvagePaidDistress, salvageUsed` |  |
| 356 | `static int leverageMonths, levBankMonths, branchesOpened, branchesClosedSeen, lastBranches` | THE LEVERAGE RATIO AND THE BRANCHES (0.7.11, round 2): how often the leverage requirement was the one that bound, the ratio at its lowest, and the branches opened - by where the bank's capital stood the month each one... |
| 358 | `static double lowestLeverage` |  |
| 359 | `static int lowestLeverageMonth` |  |
| 360 | `static int mortgagesRefusedForCapital` |  |
| 416 | `static double bondRunLoansDefaulted, bondRunLoansLost, bondRunBondsDefaulted, bondRunBondsLost` | THE BONDS (0.7.12): what defaulted in each class and what it lost - the recoveries the sources are read against - the months a sector owed bonds, and the peak share of the businesses' debt in bonds. |
| 417 | `static double bondPeakShare` |  |
| 418 | `static int bondPeakShareMonth, bondMonthsWithBonds` |  |
| 511 | `static int deskOverEquityMaxMonth` | ROUND 4 (0.7.8): the desk's inventory at the close against the bank's equity, month by month while a bank stands out of resolution - one in it holds its old inventory against the few dollars it has earned back, which ... |
| 513 | `static double deskOverEquityMax` |  |
| 525 | `static int shareListedMonths, shareTradedMonths, deskInventoryMonths, refusedAtLineMonths, projectsRefusedA...` |  |
| 526 | `static double deskPnlRun, deskInventorySum, ordinaryDividendsRun` |  |
| 527 | `static double refusedAtLineRun, notRenewedAtLineRun` |  |
| 634 | `static double companyDepositInterestRun, companiesSoldShortRun, buybackRestingRun` |  |
| 635 | `static int overdraftMonths, overdraftNoLenderMonths, overdraftProjectMonths` |  |
| 636 | `static double overdraftSum, overdraftNoLenderSum, overdraftMax, overdraftProjectSum` |  |
| 637 | `static int cannotPayMonths, cannotPayBankUnderLine` |  |
| 638 | `static double cannotPayForgivenRun, cannotPayDebtDefaultedRun, cannotPayForgivenBanned` |  |
| 640 | `static int ruleOnMonths, projectRefusedForCapital, mortgageRefusedForCapital, lineRationedMonths` | ---- round 5: the lines that stay open, the growth doors the capital rule still shuts, and interim financing ---- |
| 641 | `static double lineLentRationedRun, lineLentPastOldRuleRun` |  |
| 642 | `static int interimWritten, interimRepaid, interimDefaultedAgain, interimRefusedPast, interimRefusedShut, ba...` |  |
| 643 | `static double interimWrittenSum, interimRepaidSum, interimWrittenOffSum, backstopInBanSum` |  |
| 651 | `static int longestCannotPaySpell, longestCannotPayStart, longestCannotPayBanned` |  |
| 652 | `static String longestCannotPaySector` |  |
| 660 | `static int defaultsAfterCut, defaultsOnStock, defaultsOnBondCost` |  |
| 661 | `static double defaultsAfterCutSum, defaultsOnStockSum, defaultsOnBondCostSum` |  |
| 738 | `static int afterProjectCeiling` |  |
| 741 | `static double shellUnpaid, shellInterim` |  |
| 742 | `static int shellMonths, shellLongest, shellLongestFrom` |  |
| 743 | `static String shellLongestWho` |  |
| 1039 | `static int worstCrimeMonth` |  |
| 1040 | `static double lastSickRate` |  |
| 1046 | `static int monthsUnburied` | DEATH CARE FIRST WHILE THE DEAD WAIT (0.7.129; BuildingManager): the months a month ended with anybody unburied, and the months the advance served the city's death care first - the first and the last of them. |
| 1053 | `static int monthsBuildersFirst` | THE BUILDERS' OWN DEPOTS FIRST (0.7.130; BuildingManager): the months the advance served a depot on site off the top of the month - the first and the last of them, those with the death care beside it, and the points t... |
| 1054 | `static double buildersFirstPoints` |  |
| 1055 | `static double workLostToIllness` |  |
| 1056 | `static int monthsObserved` |  |
| 1057 | `static double totalDepartures` |  |
| 1058 | `static double totalArrivals` |  |
| 1061 | `static double lastM0` | M0 at the last audit, so the next can say what it moved by. |
| 1803 | `static boolean AUTOBUILD` | -Dplaytest.autobuild=true (0.7.73, batch N4): the player turns automatic building on at the founding (AutoBuilder, at its defaults) and leaves it the city's works - the advisor's power, water, road and transit, health... |
| 1806 | `static boolean educationSet` | Whether any education dial or the schools flag was set, so the summary says what they did. |
| 1879 | `static String EARTH` | The spot under --earth or -Dplaytest.earth: a place's name or "lat,lon"; null for the default world. |
| 1902 | `static String earthPlace` | The place --earth named, for the report; null for a spot given as figures. |
| 1964 | `static java.io.PrintWriter traceMonths, traceBorrow, traceHouse, tracePop, traceBank, traceBonds, traceBuil...` |  |
| 2460 | `static int monthsDefended, peakDefenceMonth` | THE DEFENCE OVER THE RUN (0.7.2): the dollars the central bank sold defending the currency, in total and in its biggest month, and how many months it sold anything. |
| 2461 | `static double defendedUsdRun, peakDefenceUsd` |  |
| 2469 | `static int monthsLandBought` | THE LAND OFFICE OVER THE RUN (0.7.6): months the city bought land abroad and what it cost in local money at the day's rates - against the dollars ForeignAccounts counts, which is what the same land would have cost at ... |
| 2470 | `static double landLocalRun` |  |
| 2472 | `static int vaultLowMonth, halfGoneMonth, emptyMonth` | The vault's life: its lowest reading and when, and the first month the founders' dollars were half gone and all but gone - the question the defence's dials are asked. |
| 2473 | `static double vaultLow` |  |
| 2476 | `static double lagImplied` | The lag-implied wage index, walked month by month beside the game's own; NaN until the first month. |
| 2591 | `static int autoBuildGroundBought` | Offers bought for automatic building's ground (the autobuild flag). |
| 2651 | `static int childcareOrders` | What the childcare flag has ordered: orders, and buildings by name. |
| 3257 | `static int ironFieldsBought, ironFieldsOnBond, firstIronNeedMonth, firstIronMonth, ironCouldNotPay, ironNon...` | Over the run (0.7.64): iron offers bought, how many on the funding page's bond, the first look that needed iron, the first purchase's month and words, and the looks that needed iron and could not pay for the cheapest ... |
| 3258 | `static String firstIron` |  |
| 3261 | `static int ironDoesNotPay` | Over the run (0.7.67, M3b): the looks that needed iron whose cheapest field would not pay itself back (fieldEarnings() under fieldPayment()). |
| 3277 | `static int oilBought` | Offers with oil the test player bought over the run (0.7.62). |
| 3296 | `static double fuelBillRun, fuelAbroadRun` | Over the run (0.7.62): the drivers' and the railway's fuel, what of it was bought abroad, the first well and refinery, and the most refineries standing. |
| 3297 | `static int firstWellMonth, firstRefineryMonth, mostRefineries` |  |
| 3300 | `static double crudeLiftedRun` | ...and the wells' decline over the run (0.7.84, batch O7; spec-oil 2.6): the crude they lifted and the most standing. |
| 3301 | `static int mostWells` |  |
| 3305 | `static double refinedWrittenOff` |  |
| 3316 | `static double vanDieselRun, vanDieselLitresRun, payrollRun, pumpBillRun, pumpWholesaleRun, pumpLitresRun, q...` | THE PHASE-1 BUYERS OVER THE RUN (0.7.83, batch O6; spec-oil 2.5 and 6's measure-first 4 and 5): the vans' diesel against the businesses' payrolls; the drivers' petrol at the pump against what it cost at wholesale, and... |
| 3317 | `static double lubricantsBoughtRun, lubricantsImportedRun, crudeRefinedLitresRun` |  |
| 3318 | `static int mostStations, firstStationMonth` |  |
| 3389 | `static int portsNoCoast, firstTerminalMonth` |  |
| 3392 | `static long callsRun` | The boats over the run: calls, the most in a month and when; the tonnes by sea and across the boundary; months crude was held back for room. |
| 3393 | `static int mostCalls, mostCallsMonth, lastCalls, heldBackMonths` |  |
| 3394 | `static double seaTonnesRun, boundaryTonnesRun` |  |
| 3473 | `static int[] refinersLast` |  |
| 3474 | `static int mostCrudeUnits, mostConversionUnits, firstRefinerMonth` |  |
| 3546 | `static int groundAheadBought, groundAheadShort` | Over the run: offers bought to keep ground ahead, their dry square feet and US dollars (thousands), and the looks the cash share stopped short. |
| 3547 | `static double groundAheadSqFt, groundAheadUsd` |  |
| 3593 | `static int roomMovesBought` | Over the run (0.7.67, M3b): offers the room-to-grow move bought. |
| 3677 | `static int freshBought, coastsBought, waterLandOffered` | Over the run: offers bought for their lakes and river past the fresh water limit, offers bought for a coast, and the moves that offered either (0.7.59). |
| 3687 | `static int mapMonths, mapMismatches, mapReloads, mapReloadsSame` | THE CITY MAP, WATCHED (0.7.60, batch J3): the run asks for its city's map at the founding, so every month keeps it up (Game.reconcileMap()) and every save writes its sidecar - nothing in the model reads it, so the tra... |
| 3888 | `static int pavings, roadsPaved` | Over the run (0.7.70): the pavings ordered and the gravel roads in them. |
| 4168 | `static int refusedSkips` |  |
| 4183 | `static int monthsOwing, monthsOwingAtHome, worstStrainMonth, peakCityDebtMonth` | THE CITY'S PAPER AND THE BANK THAT HOLDS IT, over the run (2026-09-21). |
| 4184 | `static int strainMonths, monthsWithoutCapacity` |  |
| 4185 | `static double strainSum, worstStrain, peakCityDebt, paperSettledRun` |  |
| 4193 | `static int monthsAtCeiling, monthsOnAdvances, peakAdvancesMonth, peakArrearsMonth, firstAdvanceMonth` | THE CENTRAL BANK OVER THE RUN (0.7.0): the most the treasury owed it, how long the ceiling bound, and the most the arrears rule left unpaid. |
| 4194 | `static double peakAdvances, peakArrears` |  |
| 4203 | `static int monthsHouseholdsBought, monthsDeskBought, monthsCentralBankTraded` | WHO HELD THE CITY'S PAPER OVER THE RUN (0.7.1): what the households paid at the settles and in how many months, what the bank's desk bought back from them and in how many, and what the central bank bought and sold - c... |
| 4204 | `static double householdsBoughtRun, deskBoughtRun, couponsToHouseholdsRun` |  |
| 4213 | `static int peakCompressionMonth` | The most the central bank's holdings took off the long end, and when: the twenty-year paper a borrowing seed sells matures inside the run, so by month 4,002 the central bank usually holds none of it and the endpoint's... |
| 4214 | `static double peakCompression, longRateAtPeak, heldShareAtPeak` |  |
| 4667 | `static double lifetimeWriteOffs` |  |
| 4669 | `static double lowestPaidIn` | The bank's paid-in capital at its lowest, the month, and the most its two parts were ever off its equity (0.7.13, round 2). |
| 4670 | `static int lowestPaidInMonth` |  |
| 4671 | `static double lifetimeHouseholdWriteOffs` |  |
| 4701 | `static int buttonPresses` | Presses of the Bank tab's button that resolved the bank (-Dplaytest.rescue=BUTTON). |
| 4703 | `static int offersFunded` | Offers accepted after borrowing for them on the funding page's bond, and what that bond raised. |
| 4704 | `static double offerFundingRaised` |  |
| 4730 | `int month` |  |
| 4731 | `double at` |  |
| 4732 | `int under50` |  |
| 4733 | `double lowest` |  |
| 4736 | `static int resolutionsSeen` |  |
| 4738 | `static double fundPeak, fundCashShareSum` | The fund's value, its cash and the most of any company its market book held, over the run. |
| 4739 | `static int fundMonths` |  |
| 4741 | `static double warrantSharesSeen, warrantValueTaken` | The warrants' shares taken at expiry, at the price the month they were taken (share counts move with the exchange's splits, so the count alone does not add up across a run). |
| 4742 | `static int warrantExercises` |  |
| 4797 | `static double[] ledgerBefore` | THE FUND'S LEDGER, MONTH BY MONTH (FundLedger; the project's spec-fund-0739.md, 3.7, rule 3 - count a mechanic in a real run): the identity FundLedgerCheck holds on a fixture - realized + the change in unrealized = th... |
| 4798 | `static double ledgerWorst` |  |
| 4799 | `static int ledgerRuleRows, ledgerHandRows, ledgerMatured, ledgerWrittenDown, ledgerRescues` |  |
| 4837 | `static DebtQuote dayZeroBond, dayZeroNote` | What an Insane city was quoted on day 0 for its village, and what it borrowed in its first year. |
| 4838 | `static double villageInvoice, firstYearBorrowed` |  |
| 4839 | `static int insaneBorrowings` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 43 | 6590 | **type** `public class LongPlaytest` | A city played for four thousand months, the way a person plays. |

### FINDINGS (lines 50-68)

| line | len | member | says |
|---:|---:|---|---|
| 60 | 8 | **type** `static final class Finding` |  |

### `worst` MEANT `first`, AND THE PRINTOUT SAID SO (2026-09-09). (lines 69-110)

| line | len | member | says |
|---:|---:|---|---|
| 91 | 3 | `static void flag(int month, String what, String detail)` |  |
| 95 | 15 | `static void flag(int month, String what, String detail, double size)` |  |

### THE AUDIT (lines 111-181)

### A mechanic that is not counted over a real run is a mechanic nobody (lines 182-1731)

| line | len | member | says |
|---:|---:|---|---|
| 316 | 21 | `static String houseReason(String line)` | The landlords' month in a word, off the advisor's line: what built it, or what stopped it. |
| 338 | 8 | `static void countMortgages(Game g)` |  |
| 362 | 47 | `static void countLeverage(Game g)` |  |
| 420 | 53 | `static void countBonds(Game g)` |  |
| 474 | 27 | `static void countPriceAndPlant(Game g)` |  |
| 530 | 1 | `static double pct(double part, double whole)` |  |
| 532 | 30 | `static void countShares(Game g)` |  |
| 565 | 10 | `static void countCapitalLimits(Game g)` |  |
| 577 | 5 | `static double maxOf(java.util.List<Double> v)` | The largest of a list, 0 when it is empty. |
| 584 | 6 | `static double quantile(java.util.List<Double> v, double p)` | The p-th quantile of a list, 0 when it is empty. |
| 591 | 40 | `static void countDefaults(Game g)` |  |
| 667 | 59 | `static void countRound6(Game g)` |  |
| 745 | 42 | `static void countRound7(Game g)` |  |
| 788 | 101 | `static void countRound4(Game g)` |  |
| 891 | 5 | `static double forgivenTotal(EconomyManager em)` | Every overdraft forgiven over the run: the backstop's, the one path left that forgives (round 4's cash-flow test forgave too; since round 5 it lends the rest as interim financing). |
| 898 | 6 | `static double shareOver(java.util.List<Double> v, double line)` | The share of a list's entries over a line, percent. |
| 905 | 112 | `static void bankYear(Game g)` |  |
| 1019 | 9 | `static String yearSpread(int col, double scale)` | The median, least and most of one column over the growth years, as "m% (lo-hi%)". |
| 1033 | 6 | `static double median(java.util.List<Double> path)` | The median of a path, or zero for an empty one. |
| 1063 | 653 | `static void audit(Game g)` |  |
| 1717 | 6 | `static void finiteArray(int month, String what, double[] values)` |  |
| 1724 | 7 | `static void finite(int month, String what, double value)` |  |

### THE ADVISOR (lines 1732-1749)

| line | len | member | says |
|---:|---:|---|---|
| 1748 | 1 | **type** `record Move(String label, double lost, java.util.function.BooleanSupplier act)` | ONE THING THE CITY COULD DO, AND WHAT IT IS WORTH. |

### WHO IS PLAYING (2026-09-17) (lines 1750-1807)

| line | len | member | says |
|---:|---:|---|---|
| 1784 | 1 | `static int longestSkip()` | Months the player will let pass before looking, at most. |

### THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green (lines 1808-1836)

| line | len | member | says |
|---:|---:|---|---|
| 1833 | 3 | `static boolean holdsPolicyRate(Game g)` | True once the dial is the flag's rather than the advisor's: the flag is set and the currency may move. |

### THE FOUNDING, AS A CHOICE (0.7.10) (lines 1837-1864)

| line | len | member | says |
|---:|---:|---|---|
| 1861 | 3 | `static Founding founding()` | The city the run founds: Danzik, on the flag's preset, in the default world. |

### ON THE REAL EARTH (0.7.121, batch E4; the project's spec-earth.md 8) (lines 1865-1903)

| line | len | member | says |
|---:|---:|---|---|
| 1882 | 18 | `static Founding earthFounding(String spot)` | The founding on the real Earth round a spot (see the banner); throws, saying why, when the data or the spot is not to be had. |

### THE TRACE, BESIDE THE REPORT (0.7.10) (lines 1904-2308)

| line | len | member | says |
|---:|---:|---|---|
| 1973 | 15 | `static void notePaper(Game g, String purpose)` | Writes every piece of paper not yet written, with what it paid for - called the moment the advisor borrows, so the purpose is its own, and at the end of every month for whatever the game issued by itself. |
| 1989 | 56 | `static void traceOpen()` |  |
| 2046 | 51 | `static void traceMonth(Game g)` |  |
| 2107 | 43 | `static void traceLabour(Game g)` | The labour market's month, one row of <prefix>-labour.csv (0.7.18). |
| 2158 | 54 | `static void traceBuild(Game g)` | The builders, the doors and the wage bill, one row of <prefix>-build.csv (0.7.17). |
| 2214 | 7 | `static double cityOrderTax(Game g)` | The builders' tax in the city's orders still on site - paid with the order, not yet billed (revised 0.7.19). |
| 2223 | 5 | `static double sectorsPreTax(Game g)` | Every sector's month before profit tax, summed - what Luxury's share of the city's profit is read against (0.7.19). |
| 2235 | 17 | `static void tracePop(Game g)` | The people's month, one row of <prefix>-pop.csv (0.7.11, round 2): who lives here by age, what migration aimed at and did and why, and the two housing markets - the trace that asks why a city with the same jobs holds ... |
| 2254 | 24 | `static void traceHouse(Game g)` | The landlords' month, one row of <prefix>-house.csv (0.7.11). |
| 2279 | 11 | `static synchronized void traceClose()` |  |

### THE CITY'S FUND AND THE BANK'S RESCUE, STATED (0.7.14) (lines 2309-2346)

| line | len | member | says |
|---:|---:|---|---|
| 2341 | 5 | `static double fundDial(String s)` |  |

### WAGES AGAINST THE INDEX (2026-09-21) (lines 2347-2370)

### THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21) (lines 2371-2389)

### THE HOLDINGS DIAL, HELD (0.7.1) (lines 2390-2406)

### THE CEILING, SET (0.7.2) (lines 2407-2421)

### THE TARGET, SET (0.7.4) (lines 2422-3369)

| line | len | member | says |
|---:|---:|---|---|
| 2479 | 5 | `static void walkLag(Game g, double indexHanded)` | One month of the lag, on the index that month was handed. |
| 2486 | 9 | `static String wageEra(Game g)` | The three figures on one line, for a checkpoint or the end. |
| 2514 | 9 | `static void ensureSchools(Game g)` | Under the schools flag: the basic ladder, then a college, then a university, each when the city is big enough to carry it, and more of each as it grows - asked at every stop, so the schools arrive with the people rath... |
| 2546 | 3 | `static void childcareWhenNeeded(Game g)` | Under the childcare flag (0.7.71, batch N2): when NEEDS YOU lists childcare, the build advice's own card for it - the size that fits the need (since 0.7.101 its order over its life for the places it will fill, BuildAd... |
| 2551 | 12 | `static BuildAdvice.Suggestion orderChildcare(Game g)` | The rule itself, flag or none (ChildcareCheck plays it): the advice's childcare card when NEEDS YOU lists childcare, placed; that suggestion, or null when nothing was ordered. |
| 2575 | 14 | `static void groundForAutoBuild(Game g)` | Under the autobuild flag (0.7.73, N4): the ground automatic building said it was held back for, bought as the inbox's notice asks ("Buy land at the land office") - each such order's ground less what is free, by the bu... |
| 2608 | 12 | `static void noteChildcare(Game g, int month)` |  |
| 2630 | 19 | `static void budgetLine(Game g, int month)` |  |
| 2658 | 4 | `static void ensure(Game g, String name, int want)` |  |
| 2664 | 1 | `static int movesPerLook()` | ...and how many things it will fix when it does look. |
| 2724 | 423 | `static String advise(Game g)` | WHAT THE CITY DOES NEXT, AND WHY THIS IS NOT A LIST OF RULES ANY MORE. |
| 3205 | 31 | `static void ironWhenNeeded(Game g)` | IRON, A WHOLE FIELD AT A TIME (0.7.64, batch L). |
| 3244 | 11 | `static String buyWhole(Game g, LandParcel p, String purpose)` | Buys an offer as the land office does (0.7.64): out of the cash when it covers it - "cash" - or, converting and short, the funding page's BUILD_BOND_YEARS bond for the gap when canService() carries it, then the offer ... |
| 3264 | 3 | `static double[] fieldEarnings(Game g, int sites)` | What a field of `sites` iron sites would earn the city's mines a month (0.7.67, M3b): {mines, their monthly profit} - the build advice's since 0.7.101 (BuildAdvice.fieldEarnings(), moved there whole so Build's no-depo... |
| 3269 | 3 | `static double fieldPayment(Game g, LandParcel field)` | The month's payment that repays a field's price here over Game.BUILD_BOND_YEARS (0.7.67, M3b) - the build advice's since 0.7.101 (BuildAdvice.fieldPayment()). |
| 3288 | 6 | `static double fuelBoughtAbroad(Game g)` | The fuel the world sold the city last month (0.7.62): its drivers' (Game.getHouseholdFuelImports()) and its railway's (Rail.getFuelImported()) - their petrol and its diesel since 0.7.76 - and since 0.7.83 (batch O6) t... |
| 3320 | 49 | `static void countFuel(Game g)` |  |

### THE PORTS (0.7.86, batch O9; docs/studies/spec-oil.md 5's O9 row) (lines 3370-4157)

| line | len | member | says |
|---:|---:|---|---|
| 3399 | 19 | `static void portsWhenNeeded(Game g)` |  |
| 3420 | 5 | `static int terminalsStanding(Game g)` | The sea terminals standing, all kinds. |
| 3427 | 4 | `static BuildingsTemplate terminalFor(Game g, Ports.Cargo k)` | The catalogue's terminal for a kind of cargo, or null. |
| 3433 | 14 | `static void countPorts(Game g)` | The month's boats and sea tonnes, counted (the calls without their quays: the map places them, since 0.7.97 on its sea routes). |
| 3449 | 12 | `static String portsLine(Game g, int calls)` | The ports this month, a line: by kind, tonnes in and out across the boundary, the berths standing, the share in force and the sea tonnes; the calls. |
| 3476 | 17 | `static void countRefiners(Game g)` |  |
| 3495 | 31 | `static String refineryLine(Game g)` | The refiners' planner this month, a line: what is short and spare, and the best-earning candidate (and the Oil Refinery's package) with the hurdle each money gate asks of it. |
| 3556 | 3 | `static double groundAheadUtilisation(Game g)` | The most of its dry ground a look leaves built on (0.7.58, J1d): the ground in use grown BuildAdvice.HORIZON months by the businesses' growthFactor(), with BuildAdvice.SLACK past it - the build advice's own sizing (0.... |
| 3584 | 7 | `static double roomToGrow(Game g)` | ROOM TO GROW, PRICED FROM THE LINE (0.7.67, batch M3b): the output a look's room-to-grow move is weighed at - none while no more of the dry ground is built on than groundAheadUtilisation(), rising to the whole month's... |
| 3602 | 21 | `static void keepGroundAhead(Game g)` | GROUND KEPT AHEAD (see advise()): buys the best value for room while more of the dry ground is built on than groundAheadUtilisation(), the offers together costing no more than GROUND_AHEAD_CASH_SHARE of the cash at th... |
| 3639 | 36 | `static void addWater(java.util.List<Move> moves, Game g, double lost)` | WATER, AND WHERE IT COMES FROM (0.7.59, batch J2; spec-land 3, J2's playtest rule). |
| 3700 | 136 | `static void addRoadThrottle(java.util.List<Move> moves, Game g, double lost)` | The road constraint, and every way there is of easing it. |
| 3847 | 18 | `static java.util.List<BuildAdvice.Suggestion> roadsByNeed(Game g, BuildAdvice.Measure roadM, double free, java.util.Set<String>...` | The roads by need (0.7.103, batch P4): the build advice's card for the road (BuildAdvice.suggestFor(), no NEEDS YOU row, the cash and the free ground as they stand, its own slack) among the road kinds named and the ci... |
| 3871 | 15 | `static boolean pave(Game g, int n)` | Paves n gravel roads (0.7.70, N1): out of the cash, or, short of it, with the long bond build() borrows on, sized to what the cash is short of, when the city can service it (canService()). |
| 3899 | 4 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost)` | Adds one building as a way of relieving a constraint worth `lost` a month. |
| 3908 | 22 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost, double gap)` | how many of the building to order |
| 3942 | 16 | `static int linesThatPay(Game g, BuildingsTemplate t, int most)` | The most of these transit lines, up to most, that pay their way this month (0.7.49, D3): the output the road gives back with them standing (the month's GDP times the throughput they add, on the network as it would be ... |
| 3960 | 9 | `static double runningCost(Game g, BuildingsTemplate t)` | What one of these costs the city a month to run, fully staffed at today's wages. |
| 3971 | 4 | `static boolean wouldPay(Game g, String name, String sector)` | Whether one of these would clear its own running costs, on the private sector's screen. |
| 3976 | 4 | `static int qty(Game g, String name)` |  |
| 3982 | 3 | `static int qtyOf(Game g, int id)` | ...of a building by its permanent id (0.7.71: the childcare ids, renamed). |
| 3987 | 4 | `static String nameOf(Game g, int id)` | A building's name by its permanent id, null with none (0.7.120, batch I3; spec-materials 4): the player finds the Iron Mine and the Electric Arc Furnace by their ids, so a rename - the Steel Foundry's at I3 - cannot l... |
| 3993 | 7 | `static int homeBuildings(Game g)` | The city's home buildings standing, every RESIDENTIAL type (0.7.71: what childcare is counted against). |
| 4002 | 3 | `static boolean villageBuild(Game g, String name, int quantity)` | The founding village's builds: build(), and on an Insane founding the funding page's bond for a stage the treasury is short of (0.7.14). |
| 4010 | 72 | `static boolean build(Game g, String name, int quantity)` | Orders a building the way the screens do: check land, buy some if short, borrow if the treasury cannot cover it, then place the order. |
| 4109 | 1 | `static double fxRate(Game g)` | Local per USD, for turning a local-money need into a dollar ask. |
| 4111 | 18 | `static boolean canService(Game g, double extra)` |  |
| 4140 | 3 | `static void refusal(String why)` |  |
| 4144 | 8 | `static BuildingsTemplate template(Game g, String name)` |  |
| 4154 | 3 | `static String byId(int id)` | A building named by its permanent id in a fixture's list of orders, "#11", which template() reads (0.7.120, batch I3): the lists name the arc furnaces and the Iron Mine so, a name being a label the chains rename. |

### RUNNING MONTHS (lines 4158-4367)

| line | len | member | says |
|---:|---:|---|---|
| 4216 | 17 | `static void countTheHolders(Game g)` |  |
| 4234 | 10 | `static void countTheCentralBank(Game g)` |  |
| 4245 | 13 | `static void countTheCitysPaper(Game g)` |  |
| 4259 | 108 | `static void run(Game g, int months)` |  |

### SAVE / RELOAD, MID-RUN (lines 4368-4664)

| line | len | member | says |
|---:|---:|---|---|
| 4377 | 281 | `static void roundTrip(Game g, GameFiles files, int slot)` |  |
| 4659 | 5 | `static void same(int month, String what, double actual, double expected)` |  |

### (untitled) (lines 4665-4672)

### what the accounts added back (0.7.136, batch FX1) (lines 4673-4697)

| line | len | member | says |
|---:|---:|---|---|
| 4685 | 12 | `static void countAddedBack(Game g)` | The month's write-offs the accounts added back, counted term by term, and each term's first few printed - nothing printed for a harness that borrows run() with no report to print to. |

### the city's fund and the bank's rescue (0.7.14) (lines 4698-4785)

| line | len | member | says |
|---:|---:|---|---|
| 4712 | 10 | `static void acceptTheOffer(Game g)` | ACCEPTS THE BANK'S OFFER, the way the page lets a player: a treasury short of it takes the funding page's first offer - the build screen's twenty-year bond, sized to the gap - and then pays. |
| 4729 | 6 | **type** `static final class Stake` | THE CITY'S STAKE IN ITS BANK AFTER EACH RESCUE: at the resolution, then five, ten and twenty-five years on, and the first months it is under half and under a tenth - until the next resolution takes it back to everything. |
| 4745 | 40 | `static void watchTheStake(Game g)` |  |

### the fund's cost basis (0.7.39) (lines 4786-4833)

| line | len | member | says |
|---:|---:|---|---|
| 4801 | 7 | `static double[] ledgerSnap(Game g)` |  |
| 4809 | 21 | `static void watchTheLedger(Game g)` |  |
| 4832 | 1 | `static String pct(double v)` | A stake, in words: "84.4%", or "-" before it was read. |

### an Insane founding (0.7.14) (lines 4834-6632)

| line | len | member | says |
|---:|---:|---|---|
| 4848 | 10 | `static void borrowForTheVillage(Game g)` | THE PLAYER OF AN INSANE CITY BORROWS FIRST: the build screen's twenty-year bond, sized to the village's invoice at a new city's prices (Founding.whatItBuys()), before it places a house - the treasury holds nothing and... |
| 4866 | 13 | `static boolean buildOnTheFundingPage(Game g, String name, int quantity)` | ...AND BUILDS THE REST OF IT THE SAME WAY: a stage the treasury is short of is borrowed for on the funding page's bond, sized to the gap (Game.buildFundingGap()), and placed - the page a player is shown, where the adv... |
| 4880 | 1529 | `public static void main(String[] args) throws Exception` |  |
| 6422 | 5 | `static String shortTag(String key)` | A sector's name in three or four characters, for the checkpoint line. |
| 6429 | 11 | `static String tagAt(String key, int extra)` | shortTag()'s rule with the first word `extra` letters longer: the initials of a name of several words, the first three letters (and `extra` more) of one. |
| 6449 | 4 | `static String cut(String key, int letters)` | A company's name cut to `letters` characters for a trace line (0.7.136, batch FX1): the whole of a shorter one - "Oil", which the bank trace's substring(0, 4) threw on - and, where two of Equity.COMPANIES (the sectors... |
| 6459 | 19 | `static Map<String, String> distinctCuts(String[] keys, java.util.function.BiFunction<String, Integer, String> cut)` | Each key's cut, `cut.apply(key, extra)`, its `extra` raised a step at a time for every key whose cut another shares, until no two are alike - or the cuts stop growing, after a key's whole length (0.7.136). |
| 6490 | 17 | `static String creditEra(Game g)` | The business economy at a checkpoint, on one line: per sector its cash, write-downs and months of ban left, then hunger and the shelf. |
| 6516 | 33 | `static String bankEra(Game g)` | The bank as a business at a checkpoint, on one line (0.7.7): its price build-up at the dial - funds-transfer price, running costs, expected loss and capital charge, adding to prime - then what it paid savers and what ... |
| 6551 | 7 | `static double depositBeta()` | The run's deposit rate regressed on the dial: the share of a move in the policy rate savers saw, over every month. |
| 6559 | 57 | `static String era(Game g, String label)` |  |
| 6617 | 7 | `static String money(double v)` |  |
| 6625 | 7 | `static void cleanUp(Path root)` |  |

