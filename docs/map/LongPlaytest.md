# LongPlaytest.java - 9,923 lines · 184 methods · 146 constants · harnesses

`ham/citybuildersim/LongPlaytest.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> A city played for 2,400 months at the gate and 4,000 once a night on main
> (tools/nightly.sh), the way a person plays.
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
> partway through. Roughly the shape of a real session, repeated about once
> every thirty months.
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

**Uses:** [Game](Game.md) (175), [MoneyAudit](MoneyAudit.md) (107), [BuildAdvice](BuildAdvice.md) (58), [Good](Good.md) (49), [Sector](Sector.md) (46), [BusinessDebtManager](BusinessDebtManager.md) (39), [Sectors](Sectors.md) (39), [Equity](Equity.md) (39), [BuildingsTemplate](BuildingsTemplate.md) (38), [CityMap](CityMap.md) (35), [Ports](Ports.md) (30), [Exchange](Exchange.md) (28), [Founding](Founding.md) (24), [CareType](CareType.md) (23), [GameFiles](GameFiles.md) (21), [Bank](Bank.md) (20), [LandManager](LandManager.md) (19), [PriceLevel](PriceLevel.md) (18), [TreasuryFund](TreasuryFund.md) (15), [AgeBand](AgeBand.md) (14), [WageBand](WageBand.md) (14), [SpreadPlanner](SpreadPlanner.md) (14), [Crime](Crime.md) (13), [DebtManager](DebtManager.md) (13), [LandParcel](LandParcel.md) (13), [BuildingManager](BuildingManager.md) (12), [OldMetal](OldMetal.md) (12), [CityNeeds](CityNeeds.md) (11), [JobType](JobType.md) (11), [TaxPolicy](TaxPolicy.md) (11)... and 89 more

**Used by (79):** [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [BondCheck](BondCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildingStackCheck](BuildingStackCheck.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CapitalGainsCheck](CapitalGainsCheck.md), [CarCheck](CarCheck.md), [CardCheck](CardCheck.md), [CausesOfDeathCheck](CausesOfDeathCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChildcareCheck](ChildcareCheck.md), [ClockCheck](ClockCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CreditCrunchCheck](CreditCrunchCheck.md), [DenominationCheck](DenominationCheck.md), [DeterminismCheck](DeterminismCheck.md), [ER1Check](ER1Check.md), [EarthFoundingCheck](EarthFoundingCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [FollowBench](FollowBench.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [GdpCheck](GdpCheck.md), [GovernmentPlayerCheck](GovernmentPlayerCheck.md), [GroceryCheck](GroceryCheck.md), [HarbourRenders](HarbourRenders.md), [HomeRailCheck](HomeRailCheck.md), [HouseholdFlowCheck](HouseholdFlowCheck.md), [HouseholdStageCheck](HouseholdStageCheck.md), [HousingDoorCheck](HousingDoorCheck.md), [HousingMarketCheck](HousingMarketCheck.md), [IndustryViewCheck](IndustryViewCheck.md), [InvestCheck](InvestCheck.md), [IslandLinkCheck](IslandLinkCheck.md), [KeptCheck](KeptCheck.md), [LandCheck](LandCheck.md), [LandClassRenders](LandClassRenders.md), [LandmassCheck](LandmassCheck.md), [LuxuryRetailCheck](LuxuryRetailCheck.md), [MapCheck](MapCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyWordsCheck](MoneyWordsCheck.md), [MortgageCheck](MortgageCheck.md), [OilCheck](OilCheck.md), [OilViewCheck](OilViewCheck.md), [OrderDoorCheck](OrderDoorCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OwnerCheck](OwnerCheck.md), [PlanCheck](PlanCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PortCheck](PortCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [RankingsCheck](RankingsCheck.md), [RefineryViewCheck](RefineryViewCheck.md), [ReloadCheck](ReloadCheck.md), [ReplayCheck](ReplayCheck.md), [ReplayMoveProbe](ReplayMoveProbe.md), [ResearchCheck](ResearchCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [RoadCheck](RoadCheck.md), [RunRenders](RunRenders.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorStatementCheck](SectorStatementCheck.md), [ShadowBasket](ShadowBasket.md), [ShortSellingCheck](ShortSellingCheck.md), [SicknessCheck](SicknessCheck.md), [SlopeCheck](SlopeCheck.md), [SlopeRenders](SlopeRenders.md), [SubstationCheck](SubstationCheck.md), [UiProbe](UiProbe.md), [WellCheck](WellCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 53 | FINDINGS |
| 72 | · `worst` MEANT `first`, AND THE PRINTOUT SAID SO (2026-09-09). |
| 114 | THE AUDIT |
| 185 | · A mechanic that is not counted over a real run is a mechanic nobody |
| 416 | RETIREMENT'S POSTS OVER A YEAR (RT3) |
| 1313 | · THE CONSERVATION LAWS, ON EVERY MONTH OF THE RUN (GT1, 2026-11-02) |
| 1344 | THE HOUSEHOLDS' OWN LINES, OVER THE RUN (HL1, 2026-11-11) |
| 1662 | · · AND NOTHING MOVED AFTER THE AUDIT STRUCK. |
| 2335 | THE ADVISOR |
| 2353 | WHO IS PLAYING (2026-09-17) |
| 2418 | THE CITY'S ESSENTIAL WORKS (AW1, 2026-11-12) |
| 2517 | THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green |
| 2556 | THE FOUNDING, AS A CHOICE (0.7.10) |
| 2600 | THE WORLD BEYOND THE CITY (WN1; spec-nations 7.3) |
| 2620 | ON THE REAL EARTH (0.7.121, batch E4; the project's spec-earth.md 8) |
| 2708 | THE TRACE, BESIDE THE REPORT (0.7.10) |
| 3127 | THE CITY'S FUND AND THE BANK'S RESCUE, STATED (0.7.14) |
| 3168 | WAGES AGAINST THE INDEX (2026-09-21) |
| 3192 | THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21) |
| 3211 | THE HOLDINGS DIAL, HELD (0.7.1) |
| 3228 | THE CEILING, SET (0.7.2) |
| 3243 | THE TARGET, SET (0.7.4) |
| 3627 | · THE TWO THINGS THAT ARE NOT PURCHASES, done first and for free. |
| 3682 | · AND EVERYTHING THAT IS A PURCHASE. |
| 4050 | · AND THE BEST OF THEM WINS. |
| 4284 | THE PORTS (0.7.86, batch O9; docs/studies/spec-oil.md 5's O9 row) |
| 5887 | RUNNING MONTHS |
| 6196 | SAVE / RELOAD, MID-RUN |
| 6508 | (untitled) |
| 6516 | · what the accounts added back (0.7.136, batch FX1) |
| 6541 | · the city's fund and the bank's rescue (0.7.14) |
| 6642 | · the fund's cost basis (0.7.39) |
| 6690 | · an Insane founding (0.7.14) |
| 6796 | · · founding: a few months at a time, by hand |
| 6893 | · · then the real rhythm |
| 7026 | · · the report |
| 7146 | · · BUSINESS SERVICES - and the point of printing it is the MECHANISM, |
| 8714 | THE DOOR FOR ui.UiProbe (UP1, 2026-10-23) |
| 9067 | · 1. a city that prospers |
| 9108 | · AND A BUS, WHICH IS THE WHOLE REASON THIS LINE EXISTS (2026-09-16). |
| 9159 | · AND LAND PAID FOR OUT OF THE VAULT (0.7.6). |
| 9188 | · 2. a city under stress |
| 9251 | · AND NOTHING MOVES AFTER THE AUDIT HAS STRUCK. |
| 9300 | · 4. AND A WORKS THAT MAKES ITS OWN POWER AND BUYS ITS CONSUMABLES |
| 9355 | · OLD STEEL (0.7.123, batch I4; OldMetal): a Scrap Yard buys the |
| 9431 | · OLD COPPER (0.7.133, batch I8; OldMetal.copper(), the spec's 2.9): |
| 9494 | · THE CITY'S PAPER ON ITS BOOKS (CD3, spec-city-debt.md section 5; |
| 9609 | · 12. the services' upkeep is bought of Business Services |
| 9703 | · 13. the households' books close |
| 9727 | · 14. a cent that vanishes from a household is seen |
| 9754 | · 15. a bond's face that no cell holds is named |
| 9858 | · 16. what comes off the payslips is in transit a press |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 51 | `LongPlaytest.TARGET_MONTHS` | `Integer.getInteger("playtest.months", 2400)` | The run's length: 2,400 months at the gate, 4,000 nightly (GT1, 2026-11-02: what 2026-11-01 found showed between months 1,100 and 1,800), or -Dplaytest.months=N for a look at one mechanic (a trace of its own, not the ... |
| 61 | `LongPlaytest.findings` | `new LinkedHashMap<>()` |  |
| 167 | `LongPlaytest.illnessDeathsByBand` | `new double [ AgeBand.values().length ]` | The long sick (2026-09-11): who died of staying sick, by band, and the most ever ill past two months. |
| 169 | `LongPlaytest.deathsByBandRun` | `new double [ AgeBand.values().length ]` | Everyone who died, by band, and the orphans and the unhoused among them (2026-09-11). |
| 234 | `LongPlaytest.portBilledMonths` | `new int [ Ports.Cargo.values().length ]` | THE PORTS DIVISION OVER THE RUN (FR3, docs/queue/FR3.md 6 item 10): by kind of cargo, the months the division billed handling, the tonnes it handled and the last quote it struck; the handling billed and the port dues ... |
| 235 | `LongPlaytest.portTonnesRun` | `new double [ Ports.Cargo.values().length ]` |  |
| 236 | `LongPlaytest.portLastQuote` | `new double [ Ports.Cargo.values().length ]` |  |
| 260 | `LongPlaytest.BY_YEAR` | `{ "Gravel Road", "Paved Road", "Elevated Highway", "Rail Depot", "Rail Termin...` | THE ROADS AND THE RAILS BY YEAR (FR7, 2026-10-18; its brief's 5: "count in a real run how many highways, paved and gravel roads a long playtest builds, and in which years; the same for the rail rungs"): for each of th... |
| 263 | `LongPlaytest.byYearFirst` | `new int [ BY_YEAR.length ]` | ...the first month each stood, 0 for never. |
| 265 | `LongPlaytest.byYearAt` | `new long [ BY_YEAR.length ] [ 3 ]` | ...and how many stood at months 1,000, 2,000 and 3,000. |
| 270 | `LongPlaytest.FAR_FROM_PARITY` | `2.0` | How far from parity, as a multiple of it, a weak currency has to be to count as far from it. |
| 286 | `LongPlaytest.dialPath` | `new java.util.ArrayList<>()` | Every month's policy rate, for the dial's min, median and max over the run. |
| 288 | `LongPlaytest.inflationPath` | `new java.util.ArrayList<>()` | Every month's inflation reading, once the index has a year to read, for its median. |
| 297 | `LongPlaytest.spendPath` | `new java.util.ArrayList<>()` | Every month's spend factor (0.7.3): the share of their spending above subsistence the households planned at, on the month's real deposit rate - HouseholdBalance.getSpendFactor() after the month. |
| 315 | `LongPlaytest.bankNii` | `new double [ 12 ], bankFees = new double [ 12 ], bankOther = new double [ 12 ...` | THE BANK AS A BUSINESS (0.7.7): a trailing year of its statement, for the checkpoint line that prints its price build-up beside its margin, its cost ratio, its fee share and its return on equity - and the run's deposi... |
| 332 | `LongPlaytest.bankYears` | `new java.util.ArrayList<>()` | THE BANK'S CAPITAL, YEAR BY YEAR (0.7.8): its capital ratio and its own target averaged over each year, its return on the equity it opened the year with, its provisions over the loans it made (the businesses' and the ... |
| 355 | `LongPlaytest.watchSpell` | `new java.util.HashMap<>()` |  |
| 368 | `LongPlaytest.refusedOnPrice` | `new java.util.TreeMap<>()` |  |
| 378 | `LongPlaytest.houseReasons` | `new java.util.TreeMap<>()` |  |
| 413 | `LongPlaytest.salvageSpareBySector` | `new java.util.TreeMap<>()` | ...the spare-capacity rule's by the seller (RT1: how often a farm or a food plant retires in a real run, which until RT1 no city at the founding's level did). |
| 496 | `LongPlaytest.openedByStance` | `new java.util.TreeMap<>()` |  |
| 651 | `LongPlaytest.deskOverEquity` | `new java.util.ArrayList<>()` |  |
| 663 | `LongPlaytest.sharePriceOverFair` | `new java.util.ArrayList<>()` | 0.7.12 ROUND 2: the shares on the book, the dividends, and the default point - month by month, for the report's round-2 lines. |
| 667 | `LongPlaytest.refusedAtLineBySector` | `new java.util.LinkedHashMap<>()` |  |
| 702 | `LongPlaytest.deskBookAtFair` | `new java.util.ArrayList<>(), deskLargestPosition = new java.util.ArrayList<>()` |  |
| 782 | `LongPlaytest.companyTillsRun` | `new java.util.ArrayList<>()` | ---- 0.7.12 round 4: the companies' cash, the desk's excess, and can't pay means default ---- |
| 793 | `LongPlaytest.interimSeen` | `new java.util.IdentityHashMap<>()` |  |
| 794 | `LongPlaytest.interimLost` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` |  |
| 795 | `LongPlaytest.cannotPayBySector` | `new java.util.TreeMap<>()` |  |
| 796 | `LongPlaytest.cannotPayForgivenBySector` | `new java.util.TreeMap<>()` |  |
| 797 | `LongPlaytest.cannotPayByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 798 | `LongPlaytest.cannotPayForgivenByReason` | `new java.util.EnumMap<>(BusinessDebtManager.ShortReason.class)` |  |
| 799 | `LongPlaytest.cannotPaySpell` | `new java.util.HashMap<>()` |  |
| 802 | `LongPlaytest.cannotPaySpellBanned` | `new java.util.HashMap<>()` |  |
| 805 | `LongPlaytest.limitedBySector` | `new java.util.TreeMap<>()` | ---- round 6: buy only what it can pay for ---- |
| 806 | `LongPlaytest.forgoneBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 807 | `LongPlaytest.shelfShortBySector` | `new java.util.TreeMap<>()` | stock, inputs, fleet |
| 808 | `LongPlaytest.limitedLastMonth` | `new java.util.HashSet<>()` | all, after a cut |
| 811 | `LongPlaytest.defaultsOnStockBySector` | `new java.util.TreeMap<>()` |  |
| 812 | `LongPlaytest.defaultsAfterCutBySector` | `new java.util.TreeMap<>()` |  |
| 813 | `LongPlaytest.luxuryLeverage` | `new java.util.ArrayList<>()` |  |
| 814 | `LongPlaytest.RETAILERS` | `java.util.Set.of("Retail", "Restaurants", "Luxury Retail")` |  |
| 877 | `LongPlaytest.wageSpell` | `new java.util.HashMap<>()` | ---- round 7: a sector paying out more in wages than it takes in, on interim loans; a new sector's first year ---- |
| 878 | `LongPlaytest.wageSpells` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 879 | `LongPlaytest.firstPlant` | `new java.util.TreeMap<>()` | spells of a year or more, longest, its start |
| 880 | `LongPlaytest.bannedFirstYear` | `new java.util.TreeMap<>()` |  |
| 882 | `LongPlaytest.firstBillDefault` | `new java.util.TreeMap<>()` | ---- round 8: a cash-flow default on a new sector's first bill: {the month, 1 if it was lent in the interim} ---- |
| 886 | `LongPlaytest.lastProject` | `new java.util.HashMap<>()` | ...a ceiling default in the month after the sector bought plant with a loan; and a shell: a sector holding no plant, nothing built and nothing on site, that owes something - its defaults, what they left unpaid and wha... |
| 888 | `LongPlaytest.shellDefaults` | `new java.util.TreeMap<>()` |  |
| 889 | `LongPlaytest.shellSpell` | `new java.util.HashMap<>()` |  |
| 1041 | `LongPlaytest.MARKET_MONTHS` | `{ 600, 1200, 2400 }` | The months the MARKET line prints: EM1's before-and-after (its brief), and the run's last. |
| 1044 | `LongPlaytest.marketCum` | `new java.util.HashMap<>()` |  |
| 1090 | `LongPlaytest.BUBBLE_RATIO` | `10` | A share's last trade this many times its best bid, or its quote this many times its fair value, is a bubble month. |
| 1280 | `LongPlaytest.OLD_DIAL_STOP` | `.25` | The dial's stop before 0.7.2, for counting the months the uncapped dial spends past it. |
| 1365 | `LongPlaytest.OWN_BOUND_UNFIRED` | `1e-7` | The part in ten million the identity itself allows (audit()'s 1e-7 of what moved), the bound of a line that fired in neither run it was set from. |
| 1367 | `LongPlaytest.OWN_BOUND_UNFUNDED` | `9e-3` | Unfunded: a mean monthly share of 2.77e-3 of what moved in the gate's run (2,400 months; 1.66e-3 in the nightly's 4,000), three times the larger rounded up. |
| 1369 | `LongPlaytest.OWN_BOUND_UNSETTLED_INCOME` | `5e-3` | UnsettledIncome: a mean monthly share of 1.53e-3 of what moved in the gate's run (2,400 months; 9.19e-4 in the nightly's 4,000), three times the larger rounded up. |
| 1371 | `LongPlaytest.OWN_BOUND_UNSETTLED_RENT` | `5e-4` | UnsettledRent: a mean monthly share of 1.45e-4 of what moved in the gate's run (2,400 months; 8.69e-5 in the nightly's 4,000), three times the larger rounded up. |
| 1373 | `LongPlaytest.OWN_BOUND_UNSETTLED_FEES` | `3e-5` | UnsettledFees: a mean monthly share of 7.77e-6 of what moved in the gate's run (2,400 months; 4.67e-6 in the nightly's 4,000), three times the larger rounded up. |
| 1375 | `LongPlaytest.OWN_BOUND_UNSETTLED_SHOPPING` | `7e-5` | UnsettledShopping: a mean monthly share of 2.06e-5 of what moved in the gate's run (2,400 months; 1.24e-5 in the nightly's 4,000), three times the larger rounded up. |
| 1377 | `LongPlaytest.OWN_BOUND_CENSUS_STRANDED` | `2e-7` | CensusStranded: a mean monthly share of 6.21e-8 of what moved in the gate's run (2,400 months; 3.73e-8 in the nightly's 4,000), three times the larger rounded up. |
| 1379 | `LongPlaytest.OWN_BOUND_CENSUS_CREATED` | `7e-8` | CensusCreated: a mean monthly share of 2.16e-8 of what moved in the gate's run (2,400 months; 1.29e-8 in the nightly's 4,000), three times the larger rounded up. |
| 1381 | `LongPlaytest.OWN_BOUND_BOND_FACE_ORPHANED` | `OWN_BOUND_UNFIRED` | BondFaceOrphaned: fired in neither run (no month over a cent in the gate's 2,400 months or the nightly's 4,000; the mean monthly share 1.4e-17 and 8.4e-18), so held to OWN_BOUND_UNFIRED. |
| 1383 | `LongPlaytest.OWN_BOUND_PUBLIC_WAGES_RESTRUCK` | `4e-4` | PublicWagesRestruck: a mean monthly share of 1.32e-4 of what moved in the gate's run (2,400 months; 7.95e-5 in the nightly's 4,000), three times the larger rounded up. |
| 1385 | `LongPlaytest.OWN_BOUNDS` | `{ OWN_BOUND_UNFUNDED, OWN_BOUND_UNSETTLED_INCOME, OWN_BOUND_UNSETTLED_RENT, O...` | The nine bounds on the mean monthly share, in the order of MoneyAudit.OWN_LINES. |
| 1395 | `LongPlaytest.ownNet` | `new double [ MoneyAudit.OWN_LINES.length ], ownAbs = new double [ MoneyAudit....` | Per own line, in MoneyAudit.OWN_LINES' order: the net sum as it enters own, the sum of absolute amounts, the sum over the months of the month's absolute amount over what moved that month (ownShare; over the months str... |
| 1397 | `LongPlaytest.ownFired` | `new int [ MoneyAudit.OWN_LINES.length ], ownLargestMonth = new int [ MoneyAud...` |  |
| 1460 | `LongPlaytest.FOOD_BROKEN` | `"food was not conserved"` |  |
| 1461 | `LongPlaytest.POWER_BROKEN` | `"the utility's electricity revenue is not what the sectors were charged"` |  |
| 1462 | `LongPlaytest.WATER_BROKEN` | `"the water utility's revenue is not what the sectors were charged"` |  |
| 2383 | `LongPlaytest.ATTENTIVE` | `"attentive".equalsIgnoreCase(System.getProperty("playtest.player", "occasiona...` | True when this run is played by somebody paying attention. |
| 2390 | `LongPlaytest.SCHOOLS` | `Boolean.getBoolean("playtest.schools")` | -Dplaytest.schools=true: the city builds schools, which the advisor never does. |
| 2393 | `LongPlaytest.CHILDCARE` | `Boolean.getBoolean("playtest.childcare")` | -Dplaytest.childcare=true (0.7.71, batch N2): the city builds childcare by the build advice's own card, which the advisor never does. |
| 2423 | `LongPlaytest.WORKS_LOW` | `.20` | A service under a fifth served is counted each month, including the founding. |
| 2425 | `LongPlaytest.worksLow` | `new LinkedHashMap<>()` |  |
| 2426 | `LongPlaytest.worksMin` | `new LinkedHashMap<>()` |  |
| 2427 | `LongPlaytest.worksReasons` | `new LinkedHashMap<>()` |  |
| 2428 | `LongPlaytest.worksPowerOrders` | `new LinkedHashMap<>()` |  |
| 2429 | `LongPlaytest.worksLowLater` | `new LinkedHashMap<>()` |  |
| 2431 | `LongPlaytest.WORKS_NEAR` | `.90` | Jerus's HW1 bar: near full service, with the length and cause of every dip visible. |
| 2432 | `LongPlaytest.worksUnder90` | `new LinkedHashMap<>(), worksSpell = new LinkedHashMap<>(), worksLongest = new...` |  |
| 2434 | `LongPlaytest.worksSpellLead` | `new LinkedHashMap<>(), worksLongestLead = new LinkedHashMap<>()` |  |
| 2435 | `LongPlaytest.worksSpellReasons` | `new LinkedHashMap<>(), worksLongestReasons = new LinkedHashMap<>()` |  |
| 2541 | `LongPlaytest.OWNERS` | `System.getProperty("playtest.owners") = = null ? null : "on".equals(System.ge...` | The owner line under -Dplaytest.owners=on\|off (BO1-S), or null for the city's own dial (OwnerHousehold.IN_CITIES); set, the report prints OwnerCheck's probe row at its months (OWNERS m...), so the line is measured on ... |
| 2545 | `LongPlaytest.POLICY_RATE` | `System.getProperty("playtest.policyRate") = = null ? null : Double.valueOf(Sy...` | The rate the dial is held at under -Dplaytest.policyRate, or null when the advisor sets it. |
| 2549 | `LongPlaytest.HELD_AFTER` | `24` | The month the held runs start after: the currency's settling months until XR1, kept at 24 so the held runs and the ensemble start where they always did. |
| 2576 | `LongPlaytest.FOUNDING` | `Founding.Preset.valueOf(System.getProperty("playtest.founding", "standard").t...` | The founding preset under -Dplaytest.founding, standard when unset. |
| 2586 | `LongPlaytest.FOUNDED_1900` | `"1900".equals(System.getProperty("playtest.founded", "today").trim())` | Whether the run founds its city the 1900 way, under -Dplaytest.founded=1900 (R1d): today's city when unset. |
| 2613 | `LongPlaytest.PATH` | `System.getProperty("playtest.world") = = null ? Nations.DEFAULT_PATH : Nation...` | The path under -Dplaytest.world, Nations.DEFAULT_PATH when unset. |
| 2617 | `LongPlaytest.PATH_SEED` | `Long.parseLong(System.getProperty("playtest.pathSeed", String.valueOf(Nations...` | The path's seed under -Dplaytest.pathSeed, Nations.DEFAULT_SEED when unset. |
| 2767 | `LongPlaytest.TRACE` | `System.getProperty("playtest.trace")` | The trace's prefix under -Dplaytest.trace, or null. |
| 2770 | `LongPlaytest.paperSeen` | `java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>())` | The paper already written to the borrow file, by identity. |
| 3116 | `LongPlaytest.AUTOPILOT` | `Boolean.getBoolean("playtest.autopilot")` | -Dplaytest.autopilot=true (0.7.0): the rule holds the dial from founding, through the game's own autopilot (DebtManager), and the advisor keeps its hands off it. |
| 3124 | `LongPlaytest.ROLLOVER` | `Rollover.Mode.valueOf(System.getProperty("playtest.rollover", "SAME_STRUCTURE...` | -Dplaytest.rollover=MANUAL\|SAME_STRUCTURE\|TWELVE_MONTH_BILL (0.7.13): the treasury's rollover for the run (Rollover). |
| 3151 | `LongPlaytest.RESCUE_AUTO` | `! "BUTTON".equalsIgnoreCase(System.getProperty("playtest.rescue", "AUTO").tri...` | The rescue setting under -Dplaytest.rescue, AUTO when unset. |
| 3154 | `LongPlaytest.PREFERRED_ACCEPT` | `! "DECLINE".equalsIgnoreCase(System.getProperty("playtest.preferred", "ACCEPT...` | The answer to the bank's offer under -Dplaytest.preferred, ACCEPT when unset. |
| 3157 | `LongPlaytest.SHORTS` | `! "off".equalsIgnoreCase(System.getProperty("playtest.shorts", "on").trim())` | Whether the world sells short under -Dplaytest.shorts, on when unset; "off" is the counterfactual (EM2): the market without them, to see what they change. |
| 3160 | `LongPlaytest.FUND_DIAL` | `fundDial(System.getProperty("playtest.fund", "0"))` | The fund's dial under -Dplaytest.fund, 0 when unset. |
| 3190 | `LongPlaytest.WAGES` | `Boolean.getBoolean("playtest.wages")` | -Dplaytest.wages=true: the wage index, the price index and the lag-implied level at each checkpoint. |
| 3209 | `LongPlaytest.BORROW_AT_HOME` | `Boolean.getBoolean("playtest.borrowAtHome")` | -Dplaytest.borrowAtHome=true: the advisor's borrowing goes to the city's own term bonds, never abroad. |
| 3225 | `LongPlaytest.QE_SHARE` | `System.getProperty("playtest.qeShare") = = null ? null : Double.valueOf(Syste...` | The holdings dial under -Dplaytest.qeShare, or null when nobody sets it. |
| 3240 | `LongPlaytest.ADVANCES_MONTHS` | `System.getProperty("playtest.advancesMonths") = = null ? null : Double.valueO...` | The advances ceiling under -Dplaytest.advancesMonths, in months of revenue, or null for the default. |
| 3265 | `LongPlaytest.MONEY_GATE` | `System.getProperty("playtest.moneyGate", "").trim()` | THE MONEY GATE, FOR THE COUNTERFACTUAL (0.7.82, batch O5; turned round at 0.7.112): since 0.7.112 the refiners' spread planner tests every order on its whole cost, whoever pays (SpreadPlanner.ON_ITS_WHOLE_COST, Jerus'... |
| 3268 | `LongPlaytest.REFINERY_TRACE` | `Boolean.getBoolean("playtest.refinery")` | -Dplaytest.refinery=true (0.7.82): the refiners' planner yearly - its outlook and its best candidates under both money gates (refineryLine()). |
| 3271 | `LongPlaytest.INFLATION_TARGET` | `System.getProperty("playtest.inflationTarget") = = null ? null : Double.value...` | The inflation target under -Dplaytest.inflationTarget, a fraction a year, or null for the default. |
| 3299 | `LongPlaytest.FLOAT_FROM_MONTH` | `60` | THE D$ OVER THE RUN (XR1, 2026-10-21): how the float behaved in this city, counted a month at a time (countTheFloat()) and printed as one line beside the currency's (docs/studies/spec-exchange-rates.md, 1.1: "volatili... |
| 3494 | `LongPlaytest.CHILDCARE_SEEN` | `{ 1_000, 2_000, 3_000, 4_000 }` | CHILDCARE ON THE MAP (0.7.139, batch CC1). |
| 3497 | `LongPlaytest.childcareSeen` | `new java.util.ArrayList<>()` | ...what it said at each, in order. |
| 3519 | `LongPlaytest.BUDGET_EVERY` | `Integer.getInteger("playtest.budget", 0)` | -Dplaytest.budget=N (0.7.139, batch CC1): every N months the treasury's month, line by line - what came in and what went out, care by kind and childcare's own running (its centres' posts and upkeep) beside it. |
| 3544 | `LongPlaytest.childcareBuilt` | `new java.util.TreeMap<>()` | ...the buildings, by name. |
| 3547 | `LongPlaytest.schoolsOrdered` | `new java.util.HashMap<>()` | What the flag has ordered of each school, so one under construction is not ordered twice. |
| 4188 | `LongPlaytest.OIL_FUEL_IMPORTS_SHARE` | `.01` | The share of a month's GDP the fuel bought abroad has to pass before the test player buys oil (0.7.62): spec-land 3's K entry, 1%. |
| 4218 | `LongPlaytest.refinedRun` | `new java.util.EnumMap<>(Good.class)` | ...and the refinery's products over the run (0.7.76, batch O1): made, by product, and what of them its tanks could not hold (spec-oil 6's joint-products risk). |
| 4323 | `LongPlaytest.terminalsOrdered` | `new java.util.TreeMap<>()` | Terminals the test player ordered, by name; the looks that found a berth's worth with no coast; the first terminal's month. |
| 4332 | `LongPlaytest.PORTS_TRACE` | `Boolean.getBoolean("playtest.ports")` | -Dplaytest.ports=true (0.7.86): the ports yearly - each kind's tonnes across the boundary, its berths, its share at sea and the month's calls (portsLine()). |
| 4421 | `LongPlaytest.refinersOpened` | `new java.util.TreeMap<>(), refinersSold = new java.util.TreeMap<>()` | THE SPREAD PLANNER'S BUILDINGS OVER THE RUN (0.7.82, batch O5): each of the refiners' buildings opened and sold back, and the crude units and the conversion units standing at most. |
| 4427 | `LongPlaytest.refinersLast` | `new java.util.HashMap<>()` | Each refiner's count last month, by template id (R1d): the refiners' known templates grow as a 1900 city researches them, so a count kept by the list's index ran past the first month's list (month 157 of the default p... |
| 4487 | `LongPlaytest.GROWTH_DISCOUNT` | `.15` | How much of a gain arrives later rather than now. |
| 4494 | `LongPlaytest.GROUND_AHEAD_CASH_SHARE` | `.10` | The share of the treasury's cash one look spends keeping ground ahead (0.7.58, J1d): a tenth, the share the war chest tops the reserves up from and the every-13th-stop purchase is held under. |
| 4707 | `LongPlaytest.mapSidecarMonths` | `new ArrayList<>()` | Actual played-city months whose saved sidecars were accepted directly, including every source journal (DM2). |
| 4754 | `LongPlaytest.LAYOUT_WINDOW` | `13` | How many months a window of the layout's watch is. |
| 4756 | `LongPlaytest.LAYOUT_STARTS` | `layoutStarts(TARGET_MONTHS)` | The months each window follows: TARGET_MONTHS / 4, / 2, 3 / 4 and TARGET_MONTHS - LAYOUT_WINDOW, those that fit in the run and clear the window before. |
| 4789 | `LongPlaytest.layoutSpillByMonth` | `new java.util.TreeMap<>()` | The spill's moves by the month they were found in (the month pair ending there): how many pairs had any, and when. |
| 4793 | `LongPlaytest.layoutOldBoxes` | `new java.util.HashMap<>()` | ...each district's industry boxes, by its key... |
| 4795 | `LongPlaytest.layoutOldDims` | `new java.util.HashMap<>()` | ...and each of those boxes' size (LayoutSnap.dims), in the same order. |
| 4797 | `LongPlaytest.layoutOldCounts` | `new java.util.HashMap<>()` | ...and each district's count of boxes by type. |
| 4799 | `LongPlaytest.layoutOldModel` | `new java.util.HashMap<>()` | ...and each district's model count of every type (District.count()) then and the lowest it has been in any month since: a finding's detail (an old works that left in a dip of its type's count did not move), not the test. |
| 4808 | `LongPlaytest.layoutOldSpill` | `new java.util.HashSet<>()` | The districts a spill reached when the oldest works were kept (a carry in), and the old works in a district a spill reached then or at the end, counted apart: a district joining the map's order ahead of them hands the... |
| 4821 | `LongPlaytest.layoutOldLive` | `new java.util.HashMap<>(), layoutOldSent = new java.util.HashMap<>()` | THE WORKS SENT ON (RN1-H-M, 2026-11-20). |
| 5145 | `LongPlaytest.layoutBandsAt` | `new ArrayList<>()` | How many boxes each window's first month holds in the homes' band, in industry and in the outer kinds, one phrase a window: what the oldest works are read against. |
| 5802 | `LongPlaytest.DEBT_SERVICE_LIMIT` | `.25` | Whether the advisor can afford the PAYMENTS, not whether it likes the size. |
| 5808 | `LongPlaytest.ABROAD_SHARE` | `.8` | CC1-S: the share of the city's own quote for a sale that the dollar quote for it must come under for the build to borrow abroad. |
| 5811 | `LongPlaytest.QUOTED_BORROWING` | `Boolean.parseBoolean(System.getProperty("playtest.quotedBorrowing", "true"))` | The build compares quotes for the sale by default (QB1); -Dplaytest.quotedBorrowing=false keeps the old rates-before-sale counterfactual. |
| 5867 | `LongPlaytest.refusals` | `new LinkedHashMap<>()` | Why the advisor could not do the thing it wanted to. |
| 6519 | `LongPlaytest.ADDED_BACK_TERMS` | `{ "food", "materials", "luxuries", "held goods" }` | The inventory terms in NationalAccounts.getWrittenOffAddedBack()'s order. |
| 6521 | `LongPlaytest.addedBackMonths` | `new int [ ADDED_BACK_TERMS.length ], addedBackFirst = { - 1, - 1, - 1, - 1 },...` | Months each term added a write-off back, the first, and the largest with its month. |
| 6523 | `LongPlaytest.addedBackWorst` | `new double [ ADDED_BACK_TERMS.length ]` |  |
| 6525 | `LongPlaytest.EVENTS_PRINTED` | `12` | The events printed as they happen, each term's first few: enough to say where a trace moved. |
| 6578 | `LongPlaytest.stakes` | `new ArrayList<>()` |  |
| 6586 | `LongPlaytest.mostHeld` | `new double [ Equity.COMPANIES.length ]` |  |
| 6737 | `LongPlaytest.COPPER_WORKS` | `{ "Copper Smelter", "Electrolytic Refinery", "Scrap Copper Refinery", "Rod Mi...` | The copper works the summary counts at the end (CU2), by their names in buildings.json; CopperRefiningCheck's played count reads the line. |
| 8577 | `LongPlaytest.DISTINCT_ROUNDS` | `64` | The most letters distinctCuts() adds to a name: past the longest key there is nothing left to add. |
| 8726 | `LongPlaytest.PROBE_QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } ...` |  |
| 8732 | `LongPlaytest.PROBE_MONTH_NANOS` | `new long [ 20_000 ]` | Per-month wall time of the probe's run(g, 1), nanoseconds, by month index. |
| 8837 | `LongPlaytest.CLEAN_UP_WALKS` | `3` | The most times cleanUp() walks a scratch tree to have it gone: a file renamed or deleted under one walk is found, or not, by the next (RN1-H-M). |
| 8897 | `LongPlaytest.MoneyFixtures.OWN_LINES` | `MoneyAudit.OWN_LINES` | The households' own lines (HL1), by the label MoneyAudit.households() notes them under. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `static PrintStream out` | Where the report goes. |
| 64 | `int count` |  |
| 65 | `int firstMonth` |  |
| 66 | `int lastMonth` |  |
| 67 | `String worst` |  |
| 68 | `double worstSize` |  |
| 69 | `int worstMonth` |  |
| 122 | `static double worstCrowding` | How far past the buildings' comfortable capacity the city was pulled. |
| 123 | `static int worstCrowdingMonth` |  |
| 142 | `static double worstUnemployment` | Unemployment, watched rather than asserted. |
| 143 | `static double lastUnemployment` |  |
| 145 | `static double totalEvicted` | The people outside the families, over the run (2026-09-11). |
| 153 | `static double usedOffered` | The second-hand car market over the run (2026-09-17). |
| 154 | `static int worstUnhousedMonth` |  |
| 156 | `static int monthsAnyTierDeclining` |  |
| 157 | `static int monthsPeopleLeft` |  |
| 162 | `static int outbreaks` | Sickness. |
| 163 | `static int monthsInOutbreak` |  |
| 164 | `static boolean wasInOutbreak` |  |
| 165 | `static double worstSickRate` |  |
| 170 | `static double orphanDeathsRun` |  |
| 171 | `static double allDeaths` |  |
| 172 | `static int worstLongSickMonth` |  |
| 179 | `static double personMonths` | The price at the clinic door (2026-09-19): person-months lived, so the deaths can be read per thousand a year; the people the fee turned away, summed for the mean; and the fees the households skipped, summed over the ... |
| 181 | `static double studentInterestSum` | The price of a place (2026-09-21): the interest the graduates paid and the grants paid, summed over the run. |
| 183 | `static double crimeVsSum` | Crime (2026-09-11): the rate against Canada's summed for the mean, the worst, and what it did over the run. |
| 192 | `static double peakSeats` |  |
| 193 | `static int peakSeatsMonth` |  |
| 194 | `static int monthsWithSeats` |  |
| 195 | `static int lastSeatMonth` |  |
| 196 | `static double serviceExportsRun` |  |
| 197 | `static double caughtRun` |  |
| 204 | `static double peakRate` | The currency over the run (2026-09-21): its dearest dollar and when, and how many months it spent far from parity. |
| 205 | `static int peakRateMonth` |  |
| 221 | `static int haulShortMonths` | FREIGHT'S TRUCKS OVER THE RUN (FR2): the months Haulage's trucks carried less than the city's legs and how short at worst, the fleet and the depots' room at their most, and what the run billed for how many tonnes. |
| 222 | `static double haulWorstShare` |  |
| 223 | `static String haulWorstSector` |  |
| 237 | `static double portHandlingRun` |  |
| 238 | `static int portBestUncoveredMonth` |  |
| 250 | `static int homeRailMonths` | THE CITY'S OWN BULK ON THE RAILWAY OVER THE RUN (FR6, docs/queue/FR6.md 5 item 3): the months the railway carried a share of what the city's works bought at home (the home share above zero) and the first of them, the ... |
| 251 | `static double homeRailedRun` |  |
| 267 | `static int needsLooks` | ...and NEEDS YOU read once a year: the looks, those listing the streets, the highways, and both (FR7 2.1). |
| 284 | `static int monthsAtGuard` | THE CURRENCY'S GUARD AND THE DIAL'S PATH, over the run (0.7.2). |
| 299 | `static double spendLow` | ...its lowest and highest, and the first month each was struck in. |
| 300 | `static int spendLowMonth` |  |
| 302 | `static int depositCappedMonths` | Months the bank's interest margin could not pay the deposit rate it chose, and paid what the margin had (Bank.isDepositPayoutHeld(), 0.7.7), and the first and last of them. |
| 318 | `static int bankMonths` |  |
| 319 | `static double shareSum` |  |
| 320 | `static int shareMonths` |  |
| 333 | `static double yrRatio, yrTarget, yrNet, yrProv, yrBook, yrDiv, yrBuyback, yrIssued, yrOpenEquity, yrOpenPop` |  |
| 334 | `static int yrMonths, yrFailuresOpen, yrFrozen, yrBranchMonths` |  |
| 335 | `static int rationedMonths, keepGoingMonths, payingMonths, returningMonths, rebuildingMonths` |  |
| 336 | `static double runDividends, runBuybacks, runIssued, runProvisions` |  |
| 338 | `static double runLoanMonths, runWrittenOff` | Every month's loans (the businesses' and the families') summed, and every write-off: the run's provisions over its average book, through the cycle rather than over the growth years alone. |
| 351 | `static int backstops, sliceMonths, newsMonths, pastWatchMonths, pastTriggerMonths, longestWatchSpell, longe...` | HOW THE DEFAULTS ARRIVE (0.7.8, a sector defaults a slice at a time): the backstop's whole-sector write-downs; the months any slice was written off, and the months one was news (BusinessDebtManager .defaultsAreNews())... |
| 353 | `static String longestWatchSector` |  |
| 354 | `static double sliceWrittenOff, worstWriteOffShare, worstWriteOff, lentPastWatch, runAllowanceUsed` |  |
| 356 | `static Notice lastDefaultNotice` |  |
| 366 | `static int loansPastWatch, loansPastOne, loansPastT, projectsPastOne` | ROUND 2 (0.7.8): the price off the curve and the plant sold as materials. |
| 367 | `static double lentPastOne, lentPastT, rateSumPastOne, rateMaxPastOne, rateSumPastT, rateMaxPastT` |  |
| 376 | `static int mortgagesWritten` | THE LANDLORDS' MORTGAGES (0.7.11): what they wrote, what the insurance took and paid, and every month's reason the landlords built or did not, counted by its words (houseReason()) - the batch's ensembles were read off... |
| 377 | `static double mortgagesLent, premiumsRun, claimsRun` |  |
| 411 | `static long salvageBuildingsDistress, salvageBuildingsSpare` |  |
| 414 | `static double salvageUnits, salvageUnitsBought, salvagePaid, salvagePaidDistress, salvageUsed` |  |
| 419 | `static RetirementWatch retirementWatch` |  |
| 422 | `final long[] opening` |  |
| 423 | `int months` |  |
| 424 | `long total` |  |
| 425 | `boolean overFifth` |  |
| 439 | `final Map<String, RetirementYear> years` |  |
| 440 | `final Map<String, Long> opening` |  |
| 441 | `final Map<String, Integer> episodes` |  |
| 442 | `int windows, overFifth` |  |
| 443 | `double worst` |  |
| 444 | `String worstSector` |  |
| 445 | `int worstMonth` |  |
| 495 | `static int leverageMonths, levBankMonths, branchesOpened, lastBranches` | THE LEVERAGE RATIO AND THE BRANCHES (0.7.11, round 2): how often the leverage requirement was the one that bound, the ratio at its lowest, and the branches opened - by where the bank's capital stood the month each one... |
| 495 | `static long branchesClosedSeen` |  |
| 497 | `static double lowestLeverage` |  |
| 498 | `static int lowestLeverageMonth` |  |
| 499 | `static int mortgagesRefusedForCapital` |  |
| 555 | `static double bondRunLoansDefaulted, bondRunLoansLost, bondRunBondsDefaulted, bondRunBondsLost` | THE BONDS (0.7.12): what defaulted in each class and what it lost - the recoveries the sources are read against - the months a sector owed bonds, and the peak share of the businesses' debt in bonds. |
| 556 | `static double bondPeakShare` |  |
| 557 | `static int bondPeakShareMonth, bondMonthsWithBonds` |  |
| 650 | `static int deskOverEquityMaxMonth` | ROUND 4 (0.7.8): the desk's inventory at the close against the bank's equity, month by month while a bank stands out of resolution - one in it holds its old inventory against the few dollars it has earned back, which ... |
| 652 | `static double deskOverEquityMax` |  |
| 664 | `static int shareListedMonths, shareTradedMonths, deskInventoryMonths, refusedAtLineMonths, projectsRefusedA...` |  |
| 665 | `static double deskPnlRun, deskInventorySum, ordinaryDividendsRun` |  |
| 666 | `static double refusedAtLineRun, notRenewedAtLineRun` |  |
| 778 | `static int landWaitMonths, landWaitNotices` | The months a sector waited on ground, and the "nowhere to build" notices raised (PT1-M2). |
| 779 | `static Notice lastLandWaitNotice` |  |
| 783 | `static double companyDepositInterestRun, companiesSoldShortRun, buybackRestingRun` |  |
| 784 | `static int overdraftMonths, overdraftNoLenderMonths, overdraftProjectMonths` |  |
| 785 | `static double overdraftSum, overdraftNoLenderSum, overdraftMax, overdraftProjectSum` |  |
| 786 | `static int cannotPayMonths, cannotPayBankUnderLine` |  |
| 787 | `static double cannotPayForgivenRun, cannotPayDebtDefaultedRun, cannotPayForgivenBanned` |  |
| 789 | `static int ruleOnMonths, projectRefusedForCapital, mortgageRefusedForCapital, lineRationedMonths` | ---- round 5: the lines that stay open, the growth doors the capital rule still shuts, and interim financing ---- |
| 790 | `static double lineLentRationedRun, lineLentPastOldRuleRun` |  |
| 791 | `static int interimWritten, interimRepaid, interimDefaultedAgain, interimRefusedPast, interimRefusedShut, ba...` |  |
| 792 | `static double interimWrittenSum, interimRepaidSum, interimWrittenOffSum, backstopInBanSum` |  |
| 800 | `static int longestCannotPaySpell, longestCannotPayStart, longestCannotPayBanned` |  |
| 801 | `static String longestCannotPaySector` |  |
| 809 | `static int defaultsAfterCut, defaultsOnStock, defaultsOnBondCost` |  |
| 810 | `static double defaultsAfterCutSum, defaultsOnStockSum, defaultsOnBondCostSum` |  |
| 887 | `static int afterProjectCeiling` |  |
| 890 | `static double shellUnpaid, shellInterim` |  |
| 891 | `static int shellMonths, shellLongest, shellLongestFrom` |  |
| 892 | `static String shellLongestWho` |  |
| 1043 | `static double buybackFilledRun, buybackLeftRun` | Over the run, month by month: the companies' buybacks filled (their statements), the bids left resting at the month's end, the book's turnover. |
| 1092 | `static int[] lastMonths, lastSpell, lastLongest, quoteMonths, quoteSpell, quoteLongest` | Per company, the company-months a last trade stood over BUBBLE_RATIO times the best bid, and the quote over BUBBLE_RATIO times fair value: the months, the spell running now and the longest. |
| 1289 | `static int worstCrimeMonth` |  |
| 1290 | `static double lastSickRate` |  |
| 1296 | `static int monthsUnburied` | DEATH CARE FIRST WHILE THE DEAD WAIT (0.7.129; BuildingManager): the months a month ended with anybody unburied, and the months the advance served the city's death care first - the first and the last of them. |
| 1303 | `static int monthsBuildersFirst` | THE BUILDERS' OWN DEPOTS FIRST (0.7.130; BuildingManager): the months the advance served a depot on site off the top of the month - the first and the last of them, those with the death care beside it, and the points t... |
| 1304 | `static double buildersFirstPoints` |  |
| 1305 | `static double workLostToIllness` |  |
| 1306 | `static int monthsObserved` |  |
| 1307 | `static double totalDepartures` |  |
| 1308 | `static double totalArrivals` |  |
| 1311 | `static double lastM0` | M0 at the last audit, so the next can say what it moved by. |
| 1332 | `static double breadOpening` | The bread in the industry's shed before the month about to be audited ran; NaN when none is waiting. |
| 1334 | `static int foodMonths` | Months the food law was checked, the bread made and sold over them, and the range the closing stock covered. |
| 1335 | `static double breadMadeRun` |  |
| 1336 | `static double breadLow` |  |
| 1338 | `static int unbilledMonths` | Months the utility had draw nobody is invoiced for - the premise of the electricity law. |
| 1341 | `static int seaMonths` | PW1-W (REV-PW1-W 1): months the sea delivered water, the months it delivered under SEA_MARGIN_SHARE of its output (its power spread over the water served), the months it was under a tenth of the water served, and its ... |
| 1342 | `static double seaShareLow` |  |
| 1399 | `static double ownMoved` | What moved between the pools and the households over the run (the sum of Households.moved()), and the months struck. |
| 1400 | `static int ownMonths` |  |
| 1402 | `static MoneyAudit.Households ownLast` | The audit last summed, so a month audit() reads twice is summed once. |
| 1532 | `static int landLedgerMonths, landLedgerDiffMonths, landLedgerWorstMonth` | AW2: actual played months whose land ledger differs from the catalogue footprint, counted without changing play. |
| 1533 | `static double landLedgerWorstSqFt` |  |
| 2407 | `static boolean AUTOBUILD` | -Dplaytest.autobuild=true (0.7.73, batch N4): the player turns automatic building on at the founding (AutoBuilder, at its defaults) and leaves it the city's works - the advisor's power, water, road and transit, health... |
| 2424 | `static int worksMonths, worksOffMonths, worksDebtIdleMonths, worksLowMonths` |  |
| 2515 | `static boolean educationSet` | Whether any education dial or the schools flag was set, so the summary says what they did. |
| 2634 | `static String EARTH` | The spot under --earth or -Dplaytest.earth: a place's name or "lat,lon"; null for the default world. |
| 2657 | `static String earthPlace` | The place --earth named, for the report; null for a spot given as figures. |
| 2768 | `static java.io.PrintWriter traceMonths, traceBorrow, traceHouse, tracePop, traceBank, traceBonds, traceBuil...` |  |
| 3281 | `static int monthsDefended, peakDefenceMonth` | THE DEFENCE OVER THE RUN (0.7.2): the dollars the central bank sold defending the currency, in total and in its biggest month, and how many months it sold anything. |
| 3282 | `static double defendedUsdRun, peakDefenceUsd` |  |
| 3300 | `static int floatMonths, floatRun, floatRunLongest, floatSign, floatFarFromParity, floatDealt, floatMarketOver2` |  |
| 3301 | `static double floatSum, floatSum2, floatBiggest` |  |
| 3303 | `static double floatDealtBefore` | What the treasury had dealt since the last reprice when the month began: run() reads it before the month runs. |
| 3359 | `static int monthsLandBought` | THE LAND OFFICE OVER THE RUN (0.7.6): months the city bought land abroad and what it cost in local money at the day's rates - against the dollars ForeignAccounts counts, which is what the same land would have cost at ... |
| 3360 | `static double landLocalRun` |  |
| 3362 | `static int vaultLowMonth, halfGoneMonth, emptyMonth` | The vault's life: its lowest reading and when, and the first month the founders' dollars were half gone and all but gone - the question the defence's dials are asked. |
| 3363 | `static double vaultLow` |  |
| 3366 | `static double lagImplied` | The lag-implied wage index, walked month by month beside the game's own; NaN until the first month. |
| 3482 | `static int autoBuildGroundBought` | Offers bought for automatic building's ground (the autobuild flag). |
| 3542 | `static int childcareOrders` | What the childcare flag has ordered: orders, and buildings by name. |
| 4171 | `static int ironFieldsBought, ironFieldsOnBond, firstIronNeedMonth, firstIronMonth, ironCouldNotPay, ironNon...` | Over the run (0.7.64): iron offers bought, how many on the funding page's bond, the first look that needed iron, the first purchase's month and words, and the looks that needed iron and could not pay for the cheapest ... |
| 4172 | `static String firstIron` |  |
| 4175 | `static int ironDoesNotPay` | Over the run (0.7.67, M3b): the looks that needed iron whose cheapest field would not pay itself back (fieldEarnings() under fieldPayment()). |
| 4191 | `static int oilBought` | Offers with oil the test player bought over the run (0.7.62). |
| 4210 | `static double fuelBillRun, fuelAbroadRun` | Over the run (0.7.62): the drivers' and the railway's fuel, what of it was bought abroad, the first well and refinery, and the most refineries standing. |
| 4211 | `static int firstWellMonth, firstRefineryMonth` |  |
| 4211 | `static long mostRefineries` |  |
| 4214 | `static double crudeLiftedRun` | ...and the wells' decline over the run (0.7.84, batch O7; spec-oil 2.6): the crude they lifted and the most standing. |
| 4215 | `static long mostWells` |  |
| 4219 | `static double refinedWrittenOff` |  |
| 4230 | `static double vanDieselRun, vanDieselLitresRun, payrollRun, pumpBillRun, pumpWholesaleRun, pumpLitresRun, q...` | THE PHASE-1 BUYERS OVER THE RUN (0.7.83, batch O6; spec-oil 2.5 and 6's measure-first 4 and 5): the vans' diesel against the businesses' payrolls; the drivers' petrol at the pump against what it cost at wholesale, and... |
| 4231 | `static double lubricantsBoughtRun, lubricantsImportedRun, crudeRefinedLitresRun` |  |
| 4232 | `static int mostStations, firstStationMonth` |  |
| 4324 | `static int portsNoCoast, firstTerminalMonth` |  |
| 4327 | `static long callsRun` | The boats over the run: calls, the most in a month and when; the tonnes by sea and across the boundary; months crude was held back for room. |
| 4328 | `static int mostCalls, mostCallsMonth, lastCalls, heldBackMonths` |  |
| 4329 | `static double seaTonnesRun, boundaryTonnesRun` |  |
| 4428 | `static long mostCrudeUnits, mostConversionUnits` |  |
| 4428 | `static int firstRefinerMonth` |  |
| 4497 | `static int groundAheadBought, groundAheadShort` | Over the run: offers bought to keep ground ahead, their dry square feet and US dollars (thousands), and the looks the cash share stopped short. |
| 4498 | `static double groundAheadSqFt, groundAheadUsd` |  |
| 4545 | `static int roomMovesBought` | Over the run (0.7.67, M3b): offers the room-to-grow move bought. |
| 4592 | `static int islandBridges, islandPorts, islandLinksHeld` | Over the run (AL1): the bridges and the sea terminals the test player ordered to link an island, and the links it could not order (none can be made, or an order was refused). |
| 4695 | `static int freshBought, coastsBought, waterLandOffered` | Over the run: offers bought for their lakes and river past the fresh water limit, offers bought for a coast, and the moves that offered either (0.7.59). |
| 4705 | `static int mapMonths, mapMismatches, mapReloads, mapReloadsSame` | THE CITY MAP, WATCHED (0.7.60, batch J3): the run asks for its city's map at the founding, so every month keeps it up (Game.reconcileMap()) and every save writes its sidecar - nothing in the model reads it, so the tra... |
| 4752 | `static boolean layoutOn` | ===== THE LAYOUT, WATCHED (batch CL3, 2026-10-21) ===== |
| 4772 | `long[] boxes` |  |
| 4773 | `int[] dims` |  |
| 4774 | `int[] byType` |  |
| 4775 | `int[] carryIn` |  |
| 4776 | `int[] carryLog` |  |
| 4777 | `int[] counted` |  |
| 4781 | `static Map<Long, LayoutSnap> layoutPrev` | The month before's snapshot of every district, by layoutKey(), and its month (the window's first month has none). |
| 4782 | `static int layoutPrevMonth` |  |
| 4784 | `static int layoutMonths, layoutPairs` | Over the run: months planned, month pairs compared, boxes compared, boxes that moved outside the spill's districts and inside them, and the nanoseconds the windows took. |
| 4785 | `static long layoutBoxes, layoutMoved, layoutMovedInSpill, layoutNanos` |  |
| 4787 | `static long layoutMovedOffRun` | ...and the boxes that left a district in a month pair because a run laid in that month lies across them or beside them (MF1): moved off it, counted apart and not flagged. |
| 4791 | `static int layoutOldMonth` | The first window's first month: its month, each district's industry boxes (sorted keys) and its count of boxes by type. |
| 4801 | `static double[] layoutRing` | The ring at the last window's last month and its month: {homes km, homes boxes, industry km, industry boxes, outer km, outer boxes}; null until then. |
| 4802 | `static int layoutRingMonth` |  |
| 4804 | `static long layoutOldN, layoutOldStand, layoutOldHeldN, layoutOldHeldStand` | The old works at the last window's last month: how many there were, how many stand where they stood, and the same for the types whose count held. |
| 4806 | `static long layoutOldMovedOff` | ...and how many of them are not where they stood because a run laid after the oldest works were kept lies across them or beside them: moved off it (MF1), counted apart and not flagged. |
| 4809 | `static long layoutOldSpillN, layoutOldSpillStand` |  |
| 4823 | `static long layoutOldSentOn` | ...and how many of the old works the counts kept were instances sent on, over the districts the watch judged (not those a spill reached): the term at work, counted. |
| 4825 | `static boolean layoutCountsSentOn` | Whether the watch counts the works sent on (true in play): a harness sets it false to judge a city by the law as it stood before RN1-H-M. |
| 5560 | `static int pavings` | Over the run (0.7.70): the pavings ordered and the gravel roads in them. |
| 5560 | `static long roadsPaved` |  |
| 5820 | `static int bondsAtHome, bondsAbroad` | CC1-S: the build's bonds over the run, taken at home and abroad, and the dearest rate (marketRate) it took abroad. |
| 5821 | `static double dearestAbroad` |  |
| 5897 | `static int refusedSkips` |  |
| 5912 | `static int monthsOwing, monthsOwingAtHome, worstStrainMonth, peakCityDebtMonth` | THE CITY'S PAPER AND THE BANK THAT HOLDS IT, over the run (2026-09-21). |
| 5913 | `static int strainMonths, monthsWithoutCapacity` |  |
| 5914 | `static double strainSum, worstStrain, peakCityDebt, paperSettledRun` |  |
| 5922 | `static int monthsAtCeiling, monthsOnAdvances, peakAdvancesMonth, peakArrearsMonth, firstAdvanceMonth` | THE CENTRAL BANK OVER THE RUN (0.7.0): the most the treasury owed it, how long the ceiling bound, and the most the arrears rule left unpaid. |
| 5923 | `static double peakAdvances, peakArrears` |  |
| 5932 | `static int monthsHouseholdsBought, monthsDeskBought, monthsCentralBankTraded` | WHO HELD THE CITY'S PAPER OVER THE RUN (0.7.1): what the households paid at the settles and in how many months, what the bank's desk bought back from them and in how many, and what the central bank bought and sold - c... |
| 5939 | `static int monthsStudioLiftWaived, monthsFamilyLiftWaived` | THE LANDLORDS' GLUT (RN1, 2026-11-09): the months a leg of the rent was under GLUT_PRESSURE while a carry lift was waiting, so its rent was left at its own cost times its scarcity. |
| 5940 | `static double householdsBoughtRun, deskBoughtRun, couponsToHouseholdsRun` |  |
| 5942 | `static int monthsFirmsBought` | ...and the businesses' (CD2, slice 4). |
| 5943 | `static double couponsToFirmsRun` |  |
| 5945 | `static int monthsWorldBought, monthsWorldSold` | ...and the world's (CD2, slice 5): the months it bought, the months its paper went home through the desk, and what it was paid. |
| 5946 | `static double couponsToWorldRun, principalToWorldRun, peakWorldPaper` |  |
| 5955 | `static int peakCompressionMonth` | The most the central bank's holdings took off the long end, and when: the twenty-year paper a borrowing seed sells matures inside the run, so by month 4,002 the central bank usually holds none of it and the endpoint's... |
| 5956 | `static double peakCompression, longRateAtPeak, heldShareAtPeak` |  |
| 5997 | `static int monthsCurveInverted, monthsCurveSteep` | THE CURVE, COUNTED (CD1, 2026-10-20). |
| 5998 | `static double widestExpectedMove` |  |
| 6048 | `private static boolean pw5DistrictReported` | Harness-only first-district census, enabled with -Dcbs.pw5.firstDistrict=true. |
| 6510 | `static double lifetimeWriteOffs` |  |
| 6512 | `static double lowestPaidIn` | The bank's paid-in capital at its lowest, the month, and the most its two parts were ever off its equity (0.7.13, round 2). |
| 6513 | `static int lowestPaidInMonth` |  |
| 6514 | `static double lifetimeHouseholdWriteOffs` |  |
| 6544 | `static int buttonPresses` | Presses of the Bank tab's button that resolved the bank (-Dplaytest.rescue=BUTTON). |
| 6546 | `static int offersFunded` | Offers accepted after borrowing for them on the funding page's bond, and what that bond raised. |
| 6547 | `static double offerFundingRaised` |  |
| 6573 | `int month` |  |
| 6574 | `double at` |  |
| 6575 | `int under50` |  |
| 6576 | `double lowest` |  |
| 6579 | `static int resolutionsSeen` |  |
| 6581 | `static double fundPeak, fundCashShareSum` | The fund's value, its cash and the most of any company its market book held, over the run. |
| 6582 | `static int fundMonths` |  |
| 6584 | `static double warrantSharesSeen, warrantValueTaken` | The warrants' shares taken at expiry, at the price the month they were taken (share counts move with the exchange's splits, so the count alone does not add up across a run). |
| 6585 | `static int warrantExercises` |  |
| 6588 | `static int overCapCompanyMonths, companyMonthsWithShares, idleTransferMonths` | The fund against its cap (RB1): company-months its market book held over OWNERSHIP_LIMIT, of company-months with shares; the months it paid anything of its transfer from idle cash, and that money. |
| 6589 | `static double idleTransferPaid` |  |
| 6653 | `static double[] ledgerBefore` | THE FUND'S LEDGER, MONTH BY MONTH (FundLedger; the project's spec-fund-0739.md, 3.7, rule 3 - count a mechanic in a real run): the identity FundLedgerCheck holds on a fixture - realized + the change in unrealized = th... |
| 6654 | `static double ledgerWorst` |  |
| 6655 | `static int ledgerRuleRows, ledgerHandRows, ledgerMatured, ledgerWrittenDown, ledgerRescues` |  |
| 6693 | `static DebtQuote dayZeroBond, dayZeroNote` | What an Insane city was quoted on day 0 for its village, and what it borrowed in its first year. |
| 6694 | `static double villageInvoice, firstYearBorrowed` |  |
| 6695 | `static int insaneBorrowings` |  |
| 8734 | `private static int probeStop` |  |
| 8872 | `static int fails` |  |
| 8893 | `static double worstDrift` | The worst post-audit drift any month of the last play() saw. |
| 8894 | `static String worstDriftPool` |  |
| 8901 | `int months` |  |
| 8903 | `double seized` | What the bankrupt's savings repaid the bank with over the run (HouseholdBalance.getSeizedAtBankruptcy()). |
| 8904 | `MoneyAudit.Households worst` |  |
| 8905 | `final java.util.Map<String, Integer> fired` |  |
| 8907 | `static HouseholdsRun lastRun` |  |
| 8927 | `private final Game city` |  |
| 8928 | `private double previous` |  |
| 8929 | `int months` |  |
| 8930 | `double worstResidual` |  |
| 8932 | `double movedMostBy` | The press the amount in transit moved most in: by how much (signed) and the audit struck that month. |
| 8933 | `MoneyAudit.Households movedMostIn` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 45 | 9879 | **type** `public class LongPlaytest` | A city played for 2,400 months at the gate and 4,000 once a night on main (tools/nightly.sh), the way a person plays. |

### FINDINGS (lines 53-71)

| line | len | member | says |
|---:|---:|---|---|
| 63 | 8 | **type** `static final class Finding` |  |

### `worst` MEANT `first`, AND THE PRINTOUT SAID SO (2026-09-09). (lines 72-113)

| line | len | member | says |
|---:|---:|---|---|
| 94 | 3 | `static void flag(int month, String what, String detail)` |  |
| 98 | 15 | `static void flag(int month, String what, String detail, double size)` |  |

### THE AUDIT (lines 114-184)

### A mechanic that is not counted over a real run is a mechanic nobody (lines 185-415)

| line | len | member | says |
|---:|---:|---|---|
| 381 | 21 | `static String houseReason(String line)` | The landlords' month in a word, off the advisor's line: what built it, or what stopped it. |
| 403 | 8 | `static void countMortgages(Game g)` |  |

### RETIREMENT'S POSTS OVER A YEAR (RT3) (lines 416-1312)

| line | len | member | says |
|---:|---:|---|---|
| 421 | 16 | **type** `static final class RetirementYear` |  |
| 427 | 9 | `double month(long posts, long shed)` _(in LongPlaytest.RetirementYear)_ |  |
| 438 | 49 | **type** `static final class RetirementWatch` |  |
| 447 | 7 | `void before(Game g)` _(in LongPlaytest.RetirementWatch)_ |  |
| 455 | 26 | `void after(Game g)` _(in LongPlaytest.RetirementWatch)_ |  |
| 482 | 4 | `void report(PrintStream out)` _(in LongPlaytest.RetirementWatch)_ |  |
| 501 | 47 | `static void countLeverage(Game g)` |  |
| 559 | 53 | `static void countBonds(Game g)` |  |
| 613 | 27 | `static void countPriceAndPlant(Game g)` |  |
| 669 | 1 | `static double pct(double part, double whole)` |  |
| 671 | 30 | `static void countShares(Game g)` |  |
| 704 | 10 | `static void countCapitalLimits(Game g)` |  |
| 716 | 5 | `static double maxOf(java.util.List<Double> v)` | The largest of a list, 0 when it is empty. |
| 723 | 6 | `static double quantile(java.util.List<Double> v, double p)` | The p-th quantile of a list, 0 when it is empty. |
| 730 | 46 | `static void countDefaults(Game g)` |  |
| 816 | 59 | `static void countRound6(Game g)` |  |
| 894 | 42 | `static void countRound7(Game g)` |  |
| 937 | 101 | `static void countRound4(Game g)` |  |
| 1046 | 41 | `static void countMarket(Game g)` |  |
| 1094 | 27 | `static void countBubbles(Game g, int month)` |  |
| 1123 | 9 | `static String mostOf(int[] months, int[] longest)` | The company-months in all (the bank's own apart), and the company with the most of them and its longest spell. |
| 1134 | 4 | `static String spread(java.util.List<Double> v)` | Lowest, median and highest of a sorted list, or "none". |
| 1140 | 5 | `static double forgivenTotal(EconomyManager em)` | Every overdraft forgiven over the run: the backstop's, the one path left that forgives (round 4's cash-flow test forgave too; since round 5 it lends the rest as interim financing). |
| 1147 | 6 | `static double shareOver(java.util.List<Double> v, double line)` | The share of a list's entries over a line, percent. |
| 1154 | 113 | `static void bankYear(Game g)` |  |
| 1269 | 9 | `static String yearSpread(int col, double scale)` | The median, least and most of one column over the growth years, as "m% (lo-hi%)". |
| 1283 | 6 | `static double median(java.util.List<Double> path)` | The median of a path, or zero for an empty one. |

### THE CONSERVATION LAWS, ON EVERY MONTH OF THE RUN (GT1, 2026-11-02) (lines 1313-1343)

### THE HOUSEHOLDS' OWN LINES, OVER THE RUN (HL1, 2026-11-11) (lines 1344-2334)

| line | len | member | says |
|---:|---:|---|---|
| 1409 | 22 | `static void noteOwnLines(int month, MoneyAudit.Households hh)` | Sums the month's nine own lines into the run's, and checks the nine are all of own: their signed figures add to hh.own to a cent or a part in a thousand million of what moved, else a tenth line was added to households... |
| 1436 | 23 | `static void ownLinesOverTheRun(PrintStream out, int endMonth)` | The run's table of the households' own lines, one row each, and the finding for every line past its bound. |
| 1464 | 66 | `static void conservation(Game g, int month)` |  |
| 1535 | 784 | `static void audit(Game g)` |  |
| 2320 | 6 | `static void finiteArray(int month, String what, double[] values)` |  |
| 2327 | 7 | `static void finite(int month, String what, double value)` |  |

### THE ADVISOR (lines 2335-2352)

| line | len | member | says |
|---:|---:|---|---|
| 2351 | 1 | **type** `record Move(String label, double lost, java.util.function.BooleanSupplier act)` | ONE THING THE CITY COULD DO, AND WHAT IT IS WORTH. |

### WHO IS PLAYING (2026-09-17) (lines 2353-2417)

| line | len | member | says |
|---:|---:|---|---|
| 2387 | 1 | `static int longestSkip()` | Months the player will let pass before looking, at most. |
| 2410 | 7 | `static void startAutomaticWorks(Game g)` | The standalone test city's works, selected once at founding by both normal entry points (AW1). |

### THE CITY'S ESSENTIAL WORKS (AW1, 2026-11-12) (lines 2418-2516)

| line | len | member | says |
|---:|---:|---|---|
| 2439 | 17 | `static Map<String, Double> worksShares(Game g)` | Staffed supply over today's demand, capped at full service, with no demand fully served. |
| 2458 | 55 | `static void countWorks(Game g, boolean on, boolean over, int ordersBefore)` | Counts what the month's pass could do, then reads the service the completed month delivered. |

### THE RATE, HELD (2026-09-21) - the measurement 7.0 turns green (lines 2517-2555)

| line | len | member | says |
|---:|---:|---|---|
| 2552 | 3 | `static boolean holdsPolicyRate(Game g)` | True once the dial is the flag's rather than the advisor's: the flag is set and the held months have begun. |

### THE FOUNDING, AS A CHOICE (0.7.10) (lines 2556-2599)

| line | len | member | says |
|---:|---:|---|---|
| 2580 | 4 | `static Founding founding()` | The city the run founds: Danzik, on the flag's preset, in the default world, on the flag's path (see THE WORLD BEYOND THE CITY). |
| 2595 | 4 | `static Founding played(String earthSpot)` | The city the playtest itself plays - main(), probeCity() and the ensemble: founding(), or earthFounding() round {@code earthSpot} when it is not null, founded the 1900 way under FOUNDED_1900 (research known as of 1900... |

### THE WORLD BEYOND THE CITY (WN1; spec-nations 7.3) (lines 2600-2619)

### ON THE REAL EARTH (0.7.121, batch E4; the project's spec-earth.md 8) (lines 2620-2707)

| line | len | member | says |
|---:|---:|---|---|
| 2637 | 18 | `static Founding earthFounding(String spot)` | The founding on the real Earth round a spot (see the banner); throws, saying why, when the data or the spot is not to be had. |
| 2666 | 41 | `public static Game earthCity(String spot, int months, GameFiles files, PrintStream silent)` | A city founded on the real Earth round a spot and played `months` months as the playtest plays it (ET2: the run SlopeCheck 9 counts, for tools.SlopeRenders, which draws the map of the city this returns): the village's... |

### THE TRACE, BESIDE THE REPORT (0.7.10) (lines 2708-3126)

| line | len | member | says |
|---:|---:|---|---|
| 2777 | 15 | `static void notePaper(Game g, String purpose)` | Writes every piece of paper not yet written, with what it paid for - called the moment the advisor borrows, so the purpose is its own, and at the end of every month for whatever the game issued by itself. |
| 2793 | 57 | `static void traceOpen()` |  |
| 2851 | 57 | `static void traceMonth(Game g)` |  |
| 2918 | 43 | `static void traceLabour(Game g)` | The labour market's month, one row of <prefix>-labour.csv (0.7.18). |
| 2969 | 54 | `static void traceBuild(Game g)` | The builders, the doors and the wage bill, one row of <prefix>-build.csv (0.7.17). |
| 3025 | 7 | `static double cityOrderTax(Game g)` | The builders' tax in the city's orders still on site - paid with the order, not yet billed (revised 0.7.19). |
| 3034 | 5 | `static double sectorsCash(Game g)` | Every sector's till, summed (HD1): the cash half of the businesses' hoard. |
| 3041 | 5 | `static double sectorsPreTax(Game g)` | Every sector's month before profit tax, summed - what Luxury's share of the city's profit is read against (0.7.19). |
| 3053 | 17 | `static void tracePop(Game g)` | The people's month, one row of <prefix>-pop.csv (0.7.11, round 2): who lives here by age, what migration aimed at and did and why, and the two housing markets - the trace that asks why a city with the same jobs holds ... |
| 3072 | 24 | `static void traceHouse(Game g)` | The landlords' month, one row of <prefix>-house.csv (0.7.11). |
| 3097 | 11 | `static synchronized void traceClose()` |  |

### THE CITY'S FUND AND THE BANK'S RESCUE, STATED (0.7.14) (lines 3127-3167)

| line | len | member | says |
|---:|---:|---|---|
| 3162 | 5 | `static double fundDial(String s)` |  |

### WAGES AGAINST THE INDEX (2026-09-21) (lines 3168-3191)

### THE CITY'S OWN PAPER, FOR THE ENSEMBLE (2026-09-21) (lines 3192-3210)

### THE HOLDINGS DIAL, HELD (0.7.1) (lines 3211-3227)

### THE CEILING, SET (0.7.2) (lines 3228-3242)

### THE TARGET, SET (0.7.4) (lines 3243-4283)

| line | len | member | says |
|---:|---:|---|---|
| 3306 | 4 | `static void resetTheFloat()` | Clears THE D$ OVER THE RUN: a run's counts start from nothing (main() at its start; a harness before it plays a city of its own). |
| 3312 | 19 | `static void countTheFloat(Game g)` | The D$'s month, for THE D$ OVER THE RUN: called once a month from audit(), after the defence is counted. |
| 3333 | 5 | `static double floatVolatility()` | The D$'s volatility a year over the run from FLOAT_FROM_MONTH: the standard deviation of the monthly log move times the root of twelve; 0 before it has months. |
| 3346 | 5 | `public static void seedMarket(Game g, int seed)` | Seeds the D$'s market from the ensemble seed (XR1): Game founds every city's market from its world seed (Game.marketSeedFor()), which is the same for the ensemble's sixteen cities, so each takes a market of its own - ... |
| 3369 | 5 | `static void walkLag(Game g, double indexHanded)` | One month of the lag, on the index that month was handed. |
| 3376 | 9 | `static String wageEra(Game g)` | The three figures on one line, for a checkpoint or the end. |
| 3404 | 9 | `static void ensureSchools(Game g)` | Under the schools flag: the basic ladder, then a college, then a university, each when the city is big enough to carry it, and more of each as it grows - asked at every stop, so the schools arrive with the people rath... |
| 3436 | 3 | `static void childcareWhenNeeded(Game g)` | Under the childcare flag (0.7.71, batch N2): when NEEDS YOU lists childcare, the build advice's own card for it - the size that fits the need (since 0.7.101 its order over its life for the places it will fill, BuildAd... |
| 3441 | 12 | `static BuildAdvice.Suggestion orderChildcare(Game g)` | The rule itself, flag or none (ChildcareCheck plays it): the advice's childcare card when NEEDS YOU lists childcare, placed; that suggestion, or null when nothing was ordered. |
| 3465 | 15 | `static void groundForAutoBuild(Game g)` | Under the autobuild flag (0.7.73, N4): the ground automatic building said it was held back for, bought as the inbox's notice asks ("Buy land at the land office") - each such order's ground less what is free, by the bu... |
| 3499 | 12 | `static void noteChildcare(Game g, int month)` |  |
| 3521 | 19 | `static void budgetLine(Game g, int month)` |  |
| 3549 | 4 | `static void ensure(Game g, String name, int want)` |  |
| 3555 | 1 | `static int movesPerLook()` | ...and how many things it will fix when it does look. |
| 3615 | 446 | `static String advise(Game g)` | WHAT THE CITY DOES NEXT, AND WHY THIS IS NOT A LIST OF RULES ANY MORE. |
| 4119 | 31 | `static void ironWhenNeeded(Game g)` | IRON, A WHOLE FIELD AT A TIME (0.7.64, batch L). |
| 4158 | 11 | `static String buyWhole(Game g, LandParcel p, String purpose)` | Buys an offer as the land office does (0.7.64): out of the cash when it covers it - "cash" - or, converting and short, the funding page's BUILD_BOND_YEARS bond for the gap when canService() carries it, then the offer ... |
| 4178 | 3 | `static double[] fieldEarnings(Game g, int sites)` | What a field of `sites` iron sites would earn the city's mines a month (0.7.67, M3b): {mines, their monthly profit} - the build advice's since 0.7.101 (BuildAdvice.fieldEarnings(), moved there whole so Build's no-depo... |
| 4183 | 3 | `static double fieldPayment(Game g, LandParcel field)` | The month's payment that repays a field's price here over Game.BUILD_BOND_YEARS (0.7.67, M3b) - the build advice's since 0.7.101 (BuildAdvice.fieldPayment()). |
| 4202 | 6 | `static double fuelBoughtAbroad(Game g)` | The fuel the world sold the city last month (0.7.62): its drivers' (Game.getHouseholdFuelImports()) and its railway's (Freight.getFuelImported()) - their petrol and its diesel since 0.7.76 - and since 0.7.83 (batch O6... |
| 4234 | 49 | `static void countFuel(Game g)` |  |

### THE PORTS (0.7.86, batch O9; docs/studies/spec-oil.md 5's O9 row) (lines 4284-5886)

| line | len | member | says |
|---:|---:|---|---|
| 4334 | 22 | `static void portsWhenNeeded(Game g)` |  |
| 4358 | 5 | `static long terminalsStanding(Game g)` | The sea terminals standing, all kinds. |
| 4377 | 3 | `static BuildingsTemplate terminalFor(Game g, Ports.Cargo k, double uncovered, double carriable, double dial)` | The catalogue's terminal to order for a kind of cargo, or null (FR3, docs/queue/FR3.md 5.1; SL1, the size by the kind's whole trade): null with no need - no terminal of the kind that pays back (Ports.berthPaysBack()) ... |
| 4382 | 14 | `static void countPorts(Game g)` | The month's boats and sea tonnes, counted (the calls without their quays: the map places them, since 0.7.97 on its sea routes). |
| 4398 | 12 | `static String portsLine(Game g, int calls)` | The ports this month, a line: by kind, tonnes in and out across the boundary, the berths standing, the share in force and the sea tonnes; the calls. |
| 4430 | 14 | `static void countRefiners(Game g)` |  |
| 4446 | 31 | `static String refineryLine(Game g)` | The refiners' planner this month, a line: what is short and spare, and the best-earning candidate (and the Oil Refinery's package) with the hurdle each money gate asks of it. |
| 4507 | 3 | `static double groundAheadUtilisation(Game g)` | The most of its dry ground a look leaves built on (0.7.58, J1d): the ground in use grown BuildAdvice.HORIZON months by the businesses' growthFactor(), with BuildAdvice.SLACK past it - the build advice's own sizing (0.... |
| 4535 | 8 | `static double roomToGrow(Game g)` | ROOM TO GROW, PRICED FROM THE LINE (0.7.67, batch M3b): the output a look's room-to-grow move is weighed at - none while no more of the dry ground is built on than groundAheadUtilisation(), rising to the whole month's... |
| 4554 | 36 | `static void keepGroundAhead(Game g)` | GROUND KEPT AHEAD (see advise()): buys the best value for room while more of the dry ground is built on than groundAheadUtilisation(), the offers together costing no more than GROUND_AHEAD_CASH_SHARE of the cash at th... |
| 4603 | 28 | `static void linkIslands(Game g)` | THE ISLAND LINK (AL1, 2026-11-09): the test player links an island as the AutoBuilder does, by the same rule - BuildAdvice.linkFor() - when its own measure of the ground running low says so (keepGroundAhead()). |
| 4637 | 3 | `static boolean desalinationHasCoast(Game g)` | Whether the city has a coast for a desalination plant: it owns sea and the model would stand a plant on it (ET5). |
| 4656 | 37 | `static void addWater(java.util.List<Move> moves, Game g, double lost)` | WATER, AND WHERE IT COMES FROM (0.7.59, batch J2; spec-land 3, J2's playtest rule). |
| 4758 | 11 | `static int[] layoutStarts(int target)` |  |
| 4771 | 8 | **type** `static final class LayoutSnap` | One district in one month: its boxes {type << 40 \| x << 20 \| y} sorted and each one's size (dims: w - 1 << 5 \| h - 1, MF1), its count of boxes by type, what was carried in to it by type (null for none) and as the {mon... |
| 4828 | 12 | `static void layoutOldForget()` | The old works' state forgotten, so a harness can keep and judge a city of its own: what layoutKeepOldWorks() keeps and layoutFinish() counts, and the finding it may have flagged. |
| 4842 | 15 | `static int[] layoutLiveMonths(CityMap.District d, int t)` | District d's own live instances of type t, by the month each came in (ascending): its log read as CL3 reads it, each departure taking the newest. |
| 4859 | 15 | `static int[] layoutSentMonths(int[] pairs, int t)` | The months the instances of type t a carry's {month, code} pairs bring came in that have not left again (a pair ~k takes the k-th pair's back), ascending. |
| 4880 | 14 | `static int layoutSentAmongOldest(int[] live, int[] sent, int low)` | Of the `low` oldest of a district's own live instances of a type (live: the months they came in, ascending), how many its plan had sent on (sent: their months, ascending). |
| 4896 | 5 | `static boolean layoutCarries(int[] carryIn)` | Whether a carry in, counted by type, holds anything. |
| 4903 | 6 | `static int layoutWindowOf(int month)` | Which window a month falls in (its index in LAYOUT_STARTS), -1 for none. |
| 4911 | 3 | `static long layoutKey(CityMap.District d)` | A district's place in a map, the same in every month: its column and row from the founding site's. |
| 4916 | 26 | `static void layoutMonth(Game g)` | What the playtest's month asks of the watch, after the month ran: nothing outside a window, else the month planned, compared with the month before, and the first and last months' counts. |
| 4944 | 47 | `static Map<Long, LayoutSnap> layoutSnapshot(CityMap m)` | Every district planned through the map's own chain, then each one's boxes decoded (CL3Probe's way: tile k, x = b & 31, y = (b >> 5) & 31, type = b >>> BOX_TYPE_SHIFT), the packed ones left out. |
| 5001 | 6 | `static boolean layoutSameCarry(int[] a, int[] b)` | Whether two carries in leave a district the same history (none is an empty list): the same {month, code} pairs in the same order, or the one the other with pairs appended - CL3's promise is that an arrival appended mo... |
| 5009 | 4 | `static String layoutName(Game g, int type)` | A building type's words for a finding: its name by id, else its id. |
| 5015 | 61 | `static void layoutCompare(Game g, CityMap m, int month, Map<Long, LayoutSnap> before, Map<Long, LayoutSnap> now)` | Two months' snapshots compared district by district (the districts in both): the moves, as the section above counts them. |
| 5082 | 21 | `static boolean[] layoutRunMarks(CityMap m, CityMap.District d, int low, int high)` | The plots the runs laid in the months low to high put a run on or, for a highway, beside it (the plots a building moves off, MF1: DistrictPlan's BUILDINGS MOVE OFF A RUN), a flag a plot of district d's frame; null for... |
| 5105 | 5 | `static boolean layoutBoxHit(boolean[] marks, long key, int dims)` | Whether the box {key, dims} of a LayoutSnap has a plot among the marks (layoutRunMarks()). |
| 5112 | 26 | `static double[] layoutRingOf(CityMap m)` | The mean distance from the founding site of the boxes of each use (CL3Probe's measure: a box's middle from the founding site, in the district's frame, in km). |
| 5140 | 3 | `static int layoutBand(BuildingVisual.Type t)` | A use's band: 2 the outer kinds, 1 industry (not outer), 0 the homes band (the rest). |
| 5147 | 8 | `static String layoutBandsOf(CityMap m, int month, Map<Long, LayoutSnap> now)` |  |
| 5161 | 46 | `static void layoutKeepOldWorks(CityMap m, int month, Map<Long, LayoutSnap> now)` | The oldest works: every district's industry boxes and its count of boxes by type, kept at the first window's first month that has industry on the map (the playtest's city has none at m1001, a village of 176 boxes: kep... |
| 5209 | 7 | `static void layoutTrackLow(CityMap m)` | Every month since the oldest works were kept: each district's lowest model count of every type so far. |
| 5218 | 64 | `static void layoutFinish(Game g, CityMap m, int month, Map<Long, LayoutSnap> now)` | The last window's last month: the ring, and the oldest works found again where they stood. |
| 5284 | 38 | `static List<String> layoutLines(long runSeconds)` | The report's lines for the watch: the windows, the moves a month, the ring, the oldest works; the run's seconds beside the windows'. |
| 5334 | 148 | `static void addRoadThrottle(java.util.List<Move> moves, Game g, double lost)` | The road constraint, and every way there is of easing it. |
| 5493 | 18 | `static java.util.List<BuildAdvice.Suggestion> roadsByNeed(Game g, BuildAdvice.Measure roadM, double free, java.util.Set<String>...` | The roads by need (0.7.103, batch P4): the build advice's card for the road (BuildAdvice.suggestFor(), no NEEDS YOU row, the cash and the free ground as they stand, its own slack) among the road kinds named and the ci... |
| 5520 | 17 | `static java.util.List<BuildAdvice.Suggestion> powerByNeed(Game g, BuildAdvice.Measure powerM, double free)` | The power by need (PW1, 2026-11-11): the build advice's card for power (BuildAdvice.suggestFor(), no NEEDS YOU row, the cash and the free ground as they stand, its own slack) among the generators, every other building... |
| 5543 | 15 | `static boolean pave(Game g, long n)` | Paves n gravel roads (0.7.70, N1): out of the cash, or, short of it, with the long bond build() borrows on, sized to what the cash is short of, when the city can service it (canService()). |
| 5571 | 4 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost)` | Adds one building as a way of relieving a constraint worth `lost` a month. |
| 5580 | 22 | `static void addThrottle(java.util.List<Move> moves, Game g, String name, String label, double lost, double gap)` | how many of the building to order |
| 5614 | 16 | `static int linesThatPay(Game g, BuildingsTemplate t, int most)` | The most of these transit lines, up to most, that pay their way this month (0.7.49, D3): the output the road gives back with them standing (the month's GDP times the throughput they add, on the network as it would be ... |
| 5632 | 9 | `static double runningCost(Game g, BuildingsTemplate t)` | What one of these costs the city a month to run, fully staffed at today's wages. |
| 5643 | 4 | `static boolean wouldPay(Game g, String name, String sector)` | Whether one of these would clear its own running costs, on the private sector's screen. |
| 5648 | 4 | `static long qty(Game g, String name)` |  |
| 5654 | 3 | `static long qtyOf(Game g, int id)` | ...of a building by its permanent id (0.7.71: the childcare ids, renamed). |
| 5659 | 4 | `static String nameOf(Game g, int id)` | A building's name by its permanent id, null with none (0.7.120, batch I3; spec-materials 4): the player finds the Iron Mine and the Electric Arc Furnace by their ids, so a rename - the Steel Foundry's at I3 - cannot l... |
| 5665 | 7 | `static long homeBuildings(Game g)` | The city's home buildings standing, every RESIDENTIAL type (0.7.71: what childcare is counted against). |
| 5674 | 3 | `static boolean villageBuild(Game g, String name, int quantity)` | The founding village's builds: build(), and on an Insane founding the funding page's bond for a stage the treasury is short of (0.7.14). |
| 5682 | 3 | `static boolean build(Game g, String name, long quantity)` | Orders a building the way the screens do: check land, buy some if short, borrow if the treasury cannot cover it, then place the order. |
| 5687 | 91 | `static boolean build(Game g, String name, long quantity, boolean buyGround)` | ...without the linked ground bought first (`buyGround` false): an island link's terminal stands on the island's own room, and no ground of the linked pool is wanted for it (AL1). |
| 5805 | 1 | `static double fxRate(Game g)` | Local per USD, for turning a local-money need into a dollar ask. |
| 5814 | 4 | `static boolean abroadByTheRatesBefore(Game g)` | The build's rule before QB1: abroad when the world's rate before the sale is under ABROAD_SHARE of the city's own. |
| 5824 | 3 | `static DebtQuote abroadQuote(Game g, double needed)` | The 20-year dollar term bond for a local-money need, as the build would sell it; books nothing. |
| 5833 | 6 | `static boolean borrowsAbroad(Game g, double needed)` | Whether the build's borrowing for this sale goes abroad: the window open and the quote for THIS sale, with the sale in it, under ABROAD_SHARE of the city's own quote for it. |
| 5840 | 18 | `static boolean canService(Game g, double extra)` |  |
| 5869 | 3 | `static void refusal(String why)` |  |
| 5873 | 8 | `static BuildingsTemplate template(Game g, String name)` |  |
| 5883 | 3 | `static String byId(int id)` | A building named by its permanent id in a fixture's list of orders, "#11", which template() reads (0.7.120, batch I3): the lists name the arc furnaces and the Iron Mine so, a name being a label the chains rename. |

### RUNNING MONTHS (lines 5887-6195)

| line | len | member | says |
|---:|---:|---|---|
| 5958 | 5 | `static void countTheGlut(Game g)` |  |
| 5964 | 24 | `static void countTheHolders(Game g)` |  |
| 6000 | 8 | `static void countTheCurve(Game g)` |  |
| 6009 | 10 | `static void countTheCentralBank(Game g)` |  |
| 6020 | 13 | `static void countTheCitysPaper(Game g)` |  |
| 6034 | 3 | `static void run(Game g, int months)` |  |
| 6043 | 3 | `static void runPlayer(Game g, int months)` | The automated player's months, with its January wealth decision (GV1). |
| 6050 | 145 | `private static void run(Game g, int months, boolean playGovernmentPolicy)` |  |

### SAVE / RELOAD, MID-RUN (lines 6196-6507)

| line | len | member | says |
|---:|---:|---|---|
| 6205 | 296 | `static void roundTrip(Game g, GameFiles files, int slot)` |  |
| 6502 | 5 | `static void same(int month, String what, double actual, double expected)` |  |

### (untitled) (lines 6508-6515)

### what the accounts added back (0.7.136, batch FX1) (lines 6516-6540)

| line | len | member | says |
|---:|---:|---|---|
| 6528 | 12 | `static void countAddedBack(Game g)` | The month's write-offs the accounts added back, counted term by term, and each term's first few printed - nothing printed for a harness that borrows run() with no report to print to. |

### the city's fund and the bank's rescue (0.7.14) (lines 6541-6641)

| line | len | member | says |
|---:|---:|---|---|
| 6555 | 10 | `static void acceptTheOffer(Game g)` | ACCEPTS THE BANK'S OFFER, the way the page lets a player: a treasury short of it takes the funding page's first offer - the build screen's twenty-year bond, sized to the gap - and then pays. |
| 6572 | 6 | **type** `static final class Stake` | THE CITY'S STAKE IN ITS BANK AFTER EACH RESCUE: at the resolution, then five, ten and twenty-five years on, and the first months it is under half and under a tenth - until the next resolution takes it back to everything. |
| 6591 | 50 | `static void watchTheStake(Game g)` |  |

### the fund's cost basis (0.7.39) (lines 6642-6689)

| line | len | member | says |
|---:|---:|---|---|
| 6657 | 7 | `static double[] ledgerSnap(Game g)` |  |
| 6665 | 21 | `static void watchTheLedger(Game g)` |  |
| 6688 | 1 | `static String pct(double v)` | A stake, in words: "84.4%", or "-" before it was read. |

### an Insane founding (0.7.14) (lines 6690-8713)

| line | len | member | says |
|---:|---:|---|---|
| 6704 | 10 | `static void borrowForTheVillage(Game g)` | THE PLAYER OF AN INSANE CITY BORROWS FIRST: the build screen's twenty-year bond, sized to the village's invoice at a new city's prices (Founding.whatItBuys()), before it places a house - the treasury holds nothing and... |
| 6722 | 13 | `static boolean buildOnTheFundingPage(Game g, String name, int quantity)` | ...AND BUILDS THE REST OF IT THE SAME WAY: a stage the treasury is short of is borrowed for on the funding page's bond, sized to the gap (Game.buildFundingGap()), and placed - the page a player is shown, where the adv... |
| 6739 | 1729 | `public static void main(String[] args) throws Exception` |  |
| 8475 | 4 | `static String landmassesLine(LandManager ground)` | The report's line for the city's ground by landmass (ET5), printed after ITS GROUND AT THE END: "  ITS LANDMASSES AT THE END: Home 177.7 km2, Island 2 3.1 km2 (not linked); terminals off home 0" (LandmassView.reportLi... |
| 8486 | 20 | `static void keepCity(Game g, Path root, Path keep)` | The run's last city saved as slot 10 and copied into keep/data/saves (FR7, 2026-10-18), for FR7's headless renders of a played city's map (FR7's render tool, beside its notes in docs/queue/FR7/). |
| 8519 | 5 | `static String shortTag(String key)` | A sector's name in three or four characters, for the checkpoint line. |
| 8526 | 11 | `static String tagAt(String key, int extra)` | shortTag()'s rule with the first word `extra` letters longer: the initials of a name of several words, the first three letters (and `extra` more) of one. |
| 8546 | 4 | `static String cut(String key, int letters)` | A company's name cut to `letters` characters for a trace line (0.7.136, batch FX1): the whole of a shorter one - "Oil", which the bank trace's substring(0, 4) threw on - and, where two of Equity.COMPANIES (the sectors... |
| 8556 | 19 | `static Map<String, String> distinctCuts(String[] keys, java.util.function.BiFunction<String, Integer, String> cut)` | Each key's cut, `cut.apply(key, extra)`, its `extra` raised a step at a time for every key whose cut another shares, until no two are alike - or the cuts stop growing, after a key's whole length (0.7.136). |
| 8587 | 17 | `static String creditEra(Game g)` | The business economy at a checkpoint, on one line: per sector its cash, write-downs and months of ban left, then hunger and the shelf. |
| 8613 | 33 | `static String bankEra(Game g)` | The bank as a business at a checkpoint, on one line (0.7.7): its price build-up at the dial - funds-transfer price, running costs, expected loss and capital charge, adding to prime - then what it paid savers and what ... |
| 8648 | 7 | `static double depositBeta()` | The run's deposit rate regressed on the dial: the share of a move in the policy rate savers saw, over every month. |
| 8656 | 57 | `static String era(Game g, String label)` |  |

### THE DOOR FOR ui.UiProbe (UP1, 2026-10-23) (lines 8714-9923)

| line | len | member | says |
|---:|---:|---|---|
| 8742 | 34 | `public static Game probeCity(GameFiles files, String earthSpot)` | For ui.UiProbe: the city main() founds - Danzik on the flag's preset, or the real Earth round {@code earthSpot} when it is not null - with the first builds and six months played, on {@code files}. |
| 8782 | 26 | `public static void probePlay(Game g, int toMonth)` | For ui.UiProbe: the playtest's loop (long skips, the advisor's looks and moves) played on {@code g} until it reaches {@code toMonth}; each month is timed for probeMonthMs(). |
| 8810 | 8 | `public static double probeMonthMs(int from, int to)` | For ui.UiProbe: the mean model month, in milliseconds, over the months from (inclusive) to (exclusive) that probePlay() has timed; NaN when none. |
| 8819 | 8 | `private static void probeRun(Game g, int months)` |  |
| 8828 | 7 | `static String money(double v)` |  |
| 8845 | 9 | `static void cleanUp(Path root)` | Deletes a scratch tree, as far as it can, and never throws. |
| 8870 | 1053 | **type** `static final class MoneyFixtures` | Money is conserved: every dollar that leaves a pool arrives in another, or crosses the city's boundary in a way the audit can name. |
| 8874 | 4 | `static void assertTrue(String label, boolean ok)` _(in LongPlaytest.MoneyFixtures)_ |  |
| 8879 | 5 | `static void quietly(Runnable work)` _(in LongPlaytest.MoneyFixtures)_ |  |
| 8885 | 6 | `static BuildingsTemplate t(Game g, String name)` _(in LongPlaytest.MoneyFixtures)_ |  |
| 8900 | 7 | **type** `static final class HouseholdsRun` _(in LongPlaytest.MoneyFixtures)_ | The households' identity (HL1) over one play(): the months struck, the worst, the ones that did not close, and how often each own line fired. |
| 8910 | 3 | `static boolean householdsClose(MoneyAudit.Households h)` _(in LongPlaytest.MoneyFixtures)_ | The households' identity closes by the test the pools' does (LongPlaytest.audit()): a cent, or a part in ten million of what moved. |
| 8915 | 3 | `static boolean closes(double residual, MoneyAudit.Households h)` _(in LongPlaytest.MoneyFixtures)_ | The same test for a residual that is not the households' own: `residual` read against what the month moved, a cent or a part in ten million. |
| 8926 | 34 | **type** `static final class OldMetalTransit` _(in LongPlaytest.MoneyFixtures)_ | HL1's old-metal fixtures, press by press: what a yard's pending ledger holds of the households' old metal at the press's end (MoneyAudit.oldMetalInTransit(): in their savings, not yet out of the pools) and how the hou... |
| 8936 | 4 | `OldMetalTransit(Game city)` _(in LongPlaytest.MoneyFixtures.OldMetalTransit)_ | Read once the yard is built and before the first press: the amount in transit then is the first press's opening figure. |
| 8942 | 12 | `void pressed()` _(in LongPlaytest.MoneyFixtures.OldMetalTransit)_ | Read at the end of every press. |
| 8956 | 3 | `double residualWithoutCarrier()` _(in LongPlaytest.MoneyFixtures.OldMetalTransit)_ | The residual of the press the amount moved most in, had the carrier not been counted. |
| 8962 | 32 | `static MoneyAudit.Result play(String label, Game g, int months, boolean verbose)` _(in LongPlaytest.MoneyFixtures)_ | Runs `months` and returns the worst relative residual seen, printing the worst month. |
| 8996 | 27 | `static Game upkeepCity(GameFiles files, boolean office)` _(in LongPlaytest.MoneyFixtures)_ | HL1's fixture: a played city with care, schools, police, a bus network and three bank branches (the bank's charter is exempt from its operating cost) put up for the last month and held, and Contact Centres if `office`. |
| 9030 | 26 | `static Game foundingCity(GameFiles files)` _(in LongPlaytest.MoneyFixtures)_ | HL1's bond fixture: section 1's founding city rebuilt (the build list the first play() of this file starts from) and played 120 months, the town whose sectors have issued bonds the households hold, and whose household... |
| 9058 | 4 | `static double upkeepBills(Game c)` _(in LongPlaytest.MoneyFixtures)_ | The five upkeeps the treasury paid and the bank's operating cost, each off the getter the audit reads (Game.buyUpkeep()). |
| 9063 | 859 | `static void run(String[] args)` _(in LongPlaytest.MoneyFixtures)_ |  |

