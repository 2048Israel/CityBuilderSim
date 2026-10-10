# Refining.java - 1,251 lines · 73 methods · 14 constants · sectors

`ham/citybuildersim/sectors/Refining.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The refinery. THE SEVENTEENTH SECTOR (0.7.62, batch K; the project's
> spec-land.md 2.7), and since 0.7.76 a crude unit that cuts a barrel into
> what a real one does (batch O1; runs/spec-oil.md 2.1 and 2.3) - and since
> 0.7.80 a campus: crude units, and the conversion units behind them that
> turn the cheap cuts into dear products (batch O4; THE FLOW, below).
> 
> HEAVY INDUSTRY'S SHAPE, ON OIL. A refinery buys crude - the city's wells'
> first, the world's for the rest (CRUDE is importable, so the shortfall is
> the template's) - and makes the products of its slate (THE SLATE, below)
> into its tanks: petrol, diesel, jet fuel, naphtha, petroleum gas and fuel
> oil - and since 0.7.80 its conversion units' lubricants, bitumen and coke.
> The city's drivers (Motoring, at 6d; through the grocers' pumps since
> 0.7.83) draw the petrol, and its railway (Rail.haul()) and since 0.7.83
> its vans the diesel, off those tanks before the world, as the factories
> do the lubricants and the builders the bitumen (0.7.83); the rest leaves
> by the export-bound line until something here buys it.
> 
> WHY. Until 0.7.76 it made FUEL, a thousand litres from a tonne - 86% of a
> barrel as transport fuels and the rest left out - so one refinery was
> 8.3M litres of whatever the city burned, and its gate asked for a whole
> plant's worth of that. A tonne of medium crude makes 70 L of petrol and
> 169 L of diesel, and the rest is products the city does not burn.
> 
> WHAT TO BUILD is the spread planner's since 0.7.82 (batch O5; THE SPREAD
> PLANNER, below, and the shared SpreadPlanner): of the refiners' buildings
> that pass the gates - feed, ground, staff, money - the one that earns most
> on its cost at the city's own prices: a conversion unit on its spread on
> the stream it would find spare, a crude unit (for the city's own petrol
> and diesel or its wells' spare crude) with the units its cuts would feed.
> Until then it was one building, the Oil Refinery, for a whole plant's
> worth of the city's own petrol and diesel or of its wells' spare crude,
> on the investors' estimate over its slate (K's rule, 0.7.62 to 0.7.81).
> 
> AND ITS TANK FARM (0.7.85, batch O8; spec-oil 2.8; THE TANK FARM, below):
> with one standing the refiners keep a month of their crude on hand and
> run on what they have and what they bought.

**Uses:** [Good](Good.md) (70), [RefineryFlow](RefineryFlow.md) (45), [SpreadPlanner](SpreadPlanner.md) (39), [BuildingsTemplate](BuildingsTemplate.md) (28), [Deposit](Deposit.md) (15), [BusinessInvestment](BusinessInvestment.md) (13), [Sector](Sector.md) (6), [Game](Game.md) (5), [Ports](Ports.md) (5), [GoodsMarket](GoodsMarket.md) (4), [Sectors](Sectors.md) (3), [Oil](Oil.md) (2), [Formats](Formats.md) (2), [BuildingType](BuildingType.md) (1), [Trade](Trade.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [LandManager](LandManager.md) (1)

**Used by (19):** [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingVisual](BuildingVisual.md), [LongPlaytest](LongPlaytest.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PortCheck](PortCheck.md), [Ports](Ports.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryFlow](RefineryFlow.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [StrategicReserve](StrategicReserve.md)

## Sections

| line | section |
|---:|---|
| 60 | THE SLATE (0.7.76, batch O1; spec-oil 2.2 and 2.3) |
| 255 | THE MONTH'S CRUDE MIX (0.7.79, batch O3; spec-oil 2.2) |
| 373 | · THE MONTH AS IT RAN (0.7.95, batch O11; runs/spec-oil.md 2.12) |
| 400 | THE FLOW (0.7.80, batch O4; spec-oil 2.3) |
| 478 | · the reads, off the flow (spec-oil 2.3, 6) |
| 650 | THE TANK FARM (0.7.85, batch O8; spec-oil 2.8, the research's 4.5) |
| 844 | THE SPREAD PLANNER (0.7.82, batch O5; spec-oil 2.4) |
| 1212 | · idle, then shed |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 107 | `Refining.CRUDE_LITRES_PER_TONNE` | `1165` | Litres in a tonne of crude [P35]: the slate is struck in litres, the crude bought in tonnes. |
| 114 | `Refining.MEDIUM_VOL_PCT` | `{ 1.5, 6.0, 12.0, 12.0, 22.0, 24.0, 22.5 }` | The straight-run cuts of a MEDIUM crude, vol% (the research's 1.1, the blend): gas, light naphtha, heavy naphtha, kerosene, diesel, gas oil and residue, in that order (the CUT_ indexes); normalised to 1 in MEDIUM_CUTS. |
| 117 | `Refining.MEDIUM_CUTS` | `normalised(MEDIUM_VOL_PCT)` | ...each a share of the barrel, MEDIUM_VOL_PCT over its sum (the column normalised, as the prototype does). |
| 120 | `Refining.CUT_GAS` | `0, CUT_LIGHT_NAPHTHA = 1, CUT_HEAVY_NAPHTHA = 2, CUT_KEROSENE = 3, CUT_DIESEL...` | The cuts' places in MEDIUM_CUTS. |
| 124 | `Refining.LIGHT_VOL_PCT` | `{ 4.1, 8.4, 15.9, 13.9, 25.6, 21.3, 11.4 }` | The straight-run cuts of a LIGHT crude, vol%, in the CUT_ order: Brent, 38 degrees API [R1] (the research's 1.1; they sum to 100.6). |
| 127 | `Refining.HEAVY_VOL_PCT` | `{ 0.3, 5.1, 10.2, 13.8, 9.4, 24.3, 36.9 }` | ...of a HEAVY crude: Maya, 21.5 degrees API [R2], its 15.3 of naphtha split one to two light to heavy as the blend's is (spec-oil 2.2). |
| 130 | `Refining.CUTS` | `{ normalised(LIGHT_VOL_PCT), MEDIUM_CUTS, normalised(HEAVY_VOL_PCT) }` | Each grade's cuts, in Deposit.Grade's order, each column normalised to one (0.7.79): LIGHT_VOL_PCT's, MEDIUM_CUTS itself, HEAVY_VOL_PCT's. |
| 133 | `Refining.MEDIUM_MIX` | `mixOf(Deposit.Grade.MEDIUM)` | A run all of MEDIUM crude, the research's blend: the mix of imports, of a refinery that took no crude, and of every crude unit until 0.7.79. |
| 143 | `Refining.RESIDUE_PER_DIESEL` | `3` | Litres of residue a litre of diesel cuts into fuel oil (Q5): three to one, so four litres of fuel oil. |
| 146 | `Refining.PRODUCTS` | `{ Good.PETROL, Good.DIESEL, Good.LPG, Good.NAPHTHA, Good.JET, Good.FUEL_OIL, ...` | The products a crude unit makes, in the order a screen lists them: the two the city burns first. |
| 150 | `Refining.BOUGHT_HERE` | `{ Good.PETROL, Good.DIESEL }` | The products something in the city buys (spec-oil 2.5, O1): the drivers' petrol and the railway's diesel - what the gate is struck on. |
| 688 | `Refining.CRUDE_COVER_MONTHS` | `1` | Months of the crude units' run kept on hand in a Tank Farm's room (★ spec-oil 2.8): one, a month's run. |
| 904 | `Refining.PACKAGE_MOST_UNITS` | `12` | The most conversion units a crude unit is weighed with: the prototype's twelve rounds (spread.py, its package) - more than one of each of the seven kinds. |
| 907 | `Refining.KIND_NAMES` |  | Each kind's name, in Kind's order: what the idle months are saved by. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 278 | `private final double[] crudeMix` | The crude units' mix this month, each grade's share of their run in Deposit.Grade's order: last month's purchases. |
| 383 | `private RefineryFlow.Flow monthsFlow` | The flow at nameplate the month's products were made on; null before a month has run. |
| 386 | `private double[] monthsMix` | ...the crude mix it was struck on, each grade's share in Deposit.Grade's order; null before a month has run. |
| 389 | `private double monthsRate` | ...and the operating rate the month's production ran at; NaN before a month has run. |
| 439 | `private RefineryFlow.Flow flowSolved` | The flow last solved, and what it was solved on. |
| 440 | `private double[] flowFeed, flowMix, flowPrices` |  |
| 441 | `private double flowCrude` |  |
| 807 | `private boolean crudeCleared` | Whether the month's crude has cleared with a store kept, so the rate is held to the crude (afterClearing()); until the month's end. |
| 915 | `private final SpreadPlanner planner` | The refiners' spread planner: their money gate and their kinds' idle months. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 1194 | **type** `public final class Refining extends Sector` | The refinery. |

### THE SLATE (0.7.76, batch O1; spec-oil 2.2 and 2.3) (lines 60-254)

| line | len | member | says |
|---:|---:|---|---|
| 136 | 5 | `public static double[] mixOf(Deposit.Grade g)` | A run all of one grade, as a mix in Deposit.Grade's order. |
| 152 | 7 | `private static double[] normalised(double[] pct)` |  |
| 161 | 4 | **type** `public record Slate(Map<Good, Double> products, double burned)` | One run's products, in litres (BITUMEN and COKE in tonnes), and the residue burned for want of diesel to cut it. |
| 163 | 1 | `public double of(Good g)` _(in Refining.Slate)_ | What the run makes of one product; 0 for one it makes none of. |
| 171 | 3 | `public static Slate slate(double tonnes)` | What `tonnes` of medium crude a month makes (pure): its cuts at MEDIUM_CUTS, each to its product, the residue cut three to one with the diesel and the rest burned. |
| 183 | 33 | `public static Slate slate(double tonnes, double[] mix)` | What `tonnes` of crude a month makes at a mix of the grades (pure, 0.7.79): each grade's share of the run cut by its column of CUTS, summed in Deposit.Grade's order, the heavy crude's residue kept apart and added to t... |
| 218 | 3 | `public static boolean isCrudeUnit(BuildingsTemplate t)` | Whether a building is a crude unit (0.7.76): one of the refiners' that takes crude - its products are the slate of it. |
| 223 | 3 | `public static boolean isConversionUnit(BuildingsTemplate t)` | Whether a building is one of the refiners' conversion units (0.7.80): a reformer, a cracking unit and the rest, fed a stream of the crude units' run (BuildingsTemplate.refineryUnit()). |
| 234 | 5 | `public static Map<Good, Double> madeBy(BuildingsTemplate t)` | What a building makes a month at nameplate, by good (pure, 0.7.76): a crude unit's slate of its crude, a conversion unit's products of its whole feed with nothing downstream to take them (0.7.80, RefineryFlow.madeAlon... |
| 241 | 4 | `public static double feedValueOf(BuildingsTemplate t, double[] value)` | What a conversion unit's whole feed is worth a month with no unit to take it, at `value` (RefineryFlow.values()): what its value added is struck against. |
| 246 | 8 | `public Refining()` |  |

### THE MONTH'S CRUDE MIX (0.7.79, batch O3; spec-oil 2.2) (lines 255-372)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 1 | `public double[] getCrudeMix()` | The crude mix the month's slate is struck on (a copy), each grade's share in Deposit.Grade's order. |
| 284 | 3 | `public void setCrudeMixForTest(double[] mix)` | A fixture's crude mix, each grade's share in Deposit.Grade's order (SaveFileCheck's city, which runs no refinery): what endOfMonth() would strike. |
| 289 | 1 | `public Slate slateOf(double tonnes)` | What `tonnes` of crude make at this month's mix: slate(tonnes, getCrudeMix()). |
| 297 | 15 | `public static double[] monthsMix(double local, double[] liftedByGrade, double imported)` | The month's mix of what a refinery bought (pure, 0.7.79): `local` tonnes at the lift's mix (`liftedByGrade`, tonnes by grade; MEDIUM when the wells lifted none) and `imported` at MEDIUM, each a share of the two; MEDIU... |
| 320 | 16 | `public void endOfMonth(Game game)` | The month's markets have cleared: its crude mix struck from what the crude units bought, at home and abroad - and (0.7.82) each kind of unit standing counted idle or working in the month's flow, for the spread planner... |
| 338 | 4 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 345 | 16 | `protected void restoreExtras(Map<String, Double> extras)` | A save from before 0.7.79, or one whose mix is not a mix, reads MEDIUM. |
| 363 | 9 | `public void reset()` |  |

### THE MONTH AS IT RAN (0.7.95, batch O11; runs/spec-oil.md 2.12) (lines 373-399)

| line | len | member | says |
|---:|---:|---|---|
| 392 | 1 | `public RefineryFlow.Flow monthsFlow()` | The flow at nameplate this month's products were made on (THE MONTH AS IT RAN); null before a month has run since the city was founded or loaded. |
| 395 | 1 | `public double[] monthsMix()` | ...its crude mix, a copy, each grade's share in Deposit.Grade's order; null before a month has run. |
| 398 | 1 | `public double monthsRate()` | ...and the operating rate it ran at; NaN before a month has run. |

### THE FLOW (0.7.80, batch O4; spec-oil 2.3) (lines 400-477)

| line | len | member | says |
|---:|---:|---|---|
| 444 | 9 | `public double[] unitFeed(boolean onSite)` | Litres a month of feed each kind of conversion unit could take, by RefineryFlow.Kind's ordinal: its buildings standing, and with `onSite` those on site too. |
| 455 | 7 | `public double[] flowValues()` | The values the flow's spreads are struck at, by Good's ordinal: each product market's local price (★ O4-1); nothing for one with none. |
| 464 | 13 | `public RefineryFlow.Flow flow()` | This month's flow at nameplate: the standing crude units' crude, at the month's crude mix, through the standing conversion units, at the city's prices. |

### the reads, off the flow (spec-oil 2.3, 6) (lines 478-649)

| line | len | member | says |
|---:|---:|---|---|
| 482 | 3 | `public double getCapacity(Good g)` | Nameplate output of a product a month: the flow's (0.7.80) - with no conversion unit the slate of the crude its standing crude units take, at the month's crude mix (0.7.79). |
| 495 | 8 | `public double getPipeline(Good g)` | ...and what the units on site will add to it, which counts as supply for the planner: the flow with them less the flow without, never below nothing (a reformer on site makes more petrol and less naphtha). |
| 515 | 26 | `public double getStockCapacity(Good g)` | Tank room for a product: the tankage of its buildings (each template's `stock`, litres) times the product's share of the slate - an Oil Refinery's 25M L shared as its run is, so each product has the room its share of ... |
| 560 | 8 | `public void produceStock(Good g, GoodsMarket market)` | The month's production into the tanks - after shipping, from the tanks, whatever of a product they no longer have room for (0.7.76, ★ O1). |
| 604 | 5 | `public double getMarginalCostPerUnit(Good g)` | A product's marginal cost to sell or ship: its share of the month's power and water over what the line makes at the rate - not of the crude, which is the run's (THE RUN'S PRODUCTS ALL LEAVE). |
| 611 | 3 | `private double crudeOnSite()` | The crude the crude units on site will take, in tonnes a month. |
| 616 | 3 | `public double getCrudeDemand()` | Tonnes of crude the refineries want this month, at the rate they are running. |
| 628 | 4 | `public double unitsOf(BuildingsTemplate t)` | What a building is measured in, for the retirement rules: a crude unit's crude it takes (its products are its slate), and since 0.7.82 a conversion unit's feed, the litres a month of its stream it takes - so the spare... |
| 634 | 5 | `public static double boughtHere(Slate s)` | The products the city buys, a month, at a crude unit's nameplate: its petrol and its diesel. |
| 644 | 5 | `public double spareCrude()` | The crude the city's wells could lift this month that no refinery standing or on site will take, in tonnes (never below nothing). |

### THE TANK FARM (0.7.85, batch O8; spec-oil 2.8, the research's 4.5) (lines 650-843)

| line | len | member | says |
|---:|---:|---|---|
| 691 | 4 | `public static boolean isTankFarm(BuildingsTemplate t)` | Whether a building is a Tank Farm (0.7.85): one of the refiners' that runs nothing and has tanks - no crude unit, no conversion unit. |
| 697 | 3 | `public double tankFarmRoom()` | Litres of tank room the refiners' Tank Farms standing give. |
| 702 | 5 | `public double crudeKept()` | Tonnes of crude the refiners keep on hand: CRUDE_COVER_MONTHS of their crude units' run at nameplate, as far as their Tank Farms' room holds it; none without a farm. |
| 709 | 3 | `public boolean keepsCrude()` | Whether they keep crude: a Tank Farm stands, or crude is left on hand from one. |
| 727 | 7 | `public double crudeFreightSavedByShip()` | Freight a tonne of crude brought in would save going by sea, in city money: crude's lorry freight at the band's and the railway's shares in force, less the ship's (Ports.SEA_FREIGHT_SHARE_LIQUID). |
| 736 | 6 | `public double crudeHeldBackForThem(ham.citybuildersim.Ports ports)` | Tonnes a month of the refiners' own imported crude a port holds back for room: their share of the crude across the boundary, of what is held back. |
| 744 | 5 | `public static SpreadPlanner.Candidate farmEstimate(BuildingsTemplate farm, double tonnes, double saved, SpreadPlanner.City city)` | A Tank Farm weighed (pure): `tonnes` of crude a month at `saved` a tonne, less its running and standing; refused at its feed with nothing held back. |
| 751 | 14 | `public SpreadPlanner.Candidate farmCandidate(BusinessInvestment plans, Game game)` | The Tank Farm's candidate this month: null with no farm in the catalogue, one standing or on site, or none of their crude held back. |
| 774 | 17 | `public List<Sector.Line> ownLines(Game game)` | ...AND THE PORT'S TANKERS LAND IN IT (0.7.86, batch O9; spec-oil 2.9): a tanker berth takes crude only while the farms have room free for at least an MR's cargo (ham.citybuildersim.Ports.freeCrudeRoom()). |
| 794 | 3 | `public boolean buysAhead(Good g)` | Their crude, while they keep it, is stock (Sector.buysAhead()). |
| 800 | 5 | `public double bid(Good g)` | ...and their order for it is the month's run at the rate, and what brings the store back to crudeKept(). |
| 810 | 3 | `protected void afterClearing(Good g)` |  |
| 815 | 4 | `double monthsCrudeFill()` | The crude bought this month, at home and from the world, in tonnes. |
| 825 | 4 | `public static double crudeRunCap(double onHand, double fill, double nameplate)` | The share of nameplate the crude on hand and the month's fill will run (pure): the two over the crude units' nameplate run, in tonnes; no cap (infinite) with no crude unit. |
| 831 | 3 | `public static double crudeAfterRun(double onHand, double fill, double run)` | What is left on hand after the month's run (pure): what it had and the fill, less the run, never below nothing. |
| 837 | 6 | `public double getOperatingRate()` | The template's rate - and, once crude has cleared with a store kept, no more than the crude will run (crudeRunCap()). |

### THE SPREAD PLANNER (0.7.82, batch O5; spec-oil 2.4) (lines 844-1211)

| line | len | member | says |
|---:|---:|---|---|
| 908 | 5 | `static { ... }` |  |
| 918 | 1 | `public SpreadPlanner planner()` | The refiners' spread planner (a probe sets its money gate for the counterfactual). |
| 921 | 3 | `public double[] cityValues()` | Each product at the city's own price (SpreadPlanner.cityValue()), by Good's ordinal: what the planner's spreads are struck at. |
| 939 | 2 | **type** `public record Outlook(double[] feed, double crude, double[] mix, double[] values, double room, double spare...` | What the planner reads of the city (spec-oil 2.4). |
| 943 | 10 | `public Outlook outlook(BusinessInvestment plans)` | This month's outlook, as plan() reads it. |
| 962 | 10 | `double[] wellsGradesAhead()` | The grades the wells lift next: their month's potential laid along the city's oil from what has been lifted (LandManager.oilRuns()); MEDIUM with no wells. |
| 979 | 17 | `public static double[] gradesAhead(double[][] runs, double from, double tonnes)` | The grades of the city's oil from `from` to `from + tonnes` tonnes, as it is worked out (pure): each run's part of it ({end, grade's ordinal}, LandManager.oilRuns()), past the last run MEDIUM, as a mix in Deposit.Grad... |
| 998 | 10 | `static double[] blend(double[] a, double wa, double[] b, double wb)` | Two runs' mixes as one: `a` over `wa` tonnes and `b` over `wb`, each grade's share of the two; MEDIUM_MIX for none. |
| 1010 | 7 | `public static double spareFeed(RefineryFlow.Flow f, RefineryFlow.Kind k)` | The feed a kind of unit would find spare in a flow (the prototype's): its stream's; the coker's the residue and the heavy residue; a hydrocracker's no more gas oil than the spare hydrogen treats. |
| 1019 | 4 | `static String feedWords(RefineryFlow.Kind k)` | What a kind's feed is called in a refusal: its stream, and a hydrocracker's the hydrogen with it. |
| 1030 | 9 | `public static List<SpreadPlanner.Candidate> appraise(Outlook o, List<BuildingsTemplate> templates, SpreadPlanner.City city)` | Every building of the refiners' weighed at the gates, in `templates`' order (pure in its arguments): each conversion unit on its spread, each crude unit as a package. |
| 1041 | 9 | `public static SpreadPlanner.Candidate unitEstimate(RefineryFlow.Flow flow, BuildingsTemplate t, SpreadPlanner.City city)` | One conversion unit weighed (pure): its spread on the feed it would find spare in `flow`, at most its own, at the rate less its costs; its feed gate FEED_GATE of one. |
| 1062 | 44 | `public static SpreadPlanner.Candidate packageEstimate(Outlook o, RefineryFlow.Flow before, BuildingsTemplate t, List<BuildingsT...` | One crude unit weighed as a package (pure; spec-oil 2.4, rule 6 and the package): a candidate only while the city's petrol and diesel short on the trend come to FEED_GATE of what it would make of them, or the wells' s... |
| 1108 | 7 | `static int sizeOf(BuildingsTemplate t, List<BuildingsTemplate> templates)` | A crude unit's size among the crude units of `templates`: 0 for the one taking the least crude, and so on. |
| 1117 | 7 | `static BuildingsTemplate unitOfSize(RefineryFlow.Kind k, int size, List<BuildingsTemplate> templates)` | A kind's unit of a size: its templates by feed, the smallest first, the one at `size` (its largest past them); null for a kind with none. |
| 1126 | 3 | `static String a(String name)` | "a Small Coker", "an Oil Refinery". |
| 1131 | 11 | `static String named(SpreadPlanner.Candidate c)` | A candidate named, with the units it was weighed with: "an Oil Refinery with a Small Lube Plant and a Small Coker behind it". |
| 1150 | 36 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Whether to build, and what (THE SPREAD PLANNER): one order in flight; of the refiners' buildings that pass the gates, the one that earns most on its cost; with none, ground if ground was all that stood in the way, els... |
| 1188 | 7 | `public SpreadPlanner.Candidate appraise(BuildingsTemplate t, BusinessInvestment plans)` | One building of the refiners' weighed now (THE SPREAD PLANNER), at this month's outlook and the game's gates. |
| 1203 | 8 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more building of the refiners' would earn its owner a month (THE SPREAD PLANNER): a conversion unit's earnings on the feed it would find, and a crude unit's share of its package's earnings by cost - what Game... |

### idle, then shed (lines 1212-1251)

| line | len | member | says |
|---:|---:|---|---|
| 1215 | 9 | `private void noteIdleUnits()` | The month's end for the units: each kind standing counted idle (no feed in the month's flow) or working. |
| 1226 | 8 | `public double idleFeed()` | Litres a month the kinds idle long enough to shed would take: their units standing. |
| 1236 | 1 | `public int idleMonths(RefineryFlow.Kind k)` | Months a kind of unit has stood idle running (extras idleMonths.<KIND>). |
| 1240 | 4 | `public boolean mayRetire(BuildingsTemplate t)` | A conversion unit only once its kind has stood idle long enough; a crude unit only while no kind has (THE SPREAD PLANNER). |
| 1247 | 4 | `public double[] retirementDemandAndCapacity(Game game)` | The spare-capacity rule's measure: {0, the idle kinds' feed} while a kind stands idle long enough to shed; otherwise none - a price-taking exporter shrinks on distress only (HeavyIndustry's rule). |

