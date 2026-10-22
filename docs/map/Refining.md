# Refining.java - 1,918 lines · 108 methods · 19 constants · sectors

`ham/citybuildersim/sectors/Refining.java` - generated 2026-10-22 by CodeMap; line numbers are as of that run.

> The refinery. THE SEVENTEENTH SECTOR (0.7.62, batch K; the project's
> spec-land.md 2.7), and since 0.7.76 a crude unit that cuts a barrel into
> what a real one does (batch O1; docs/studies/spec-oil.md 2.1 and 2.3) - and
> since 0.7.80 a campus: crude units, and the conversion units behind them that
> turn the cheap cuts into dear products (batch O4; THE FLOW, below).
> 
> HEAVY INDUSTRY'S SHAPE, ON OIL. A refinery buys crude - the city's wells'
> first, the world's for the rest (CRUDE is importable, so the shortfall is
> the template's) - and makes the products of its slate (THE SLATE, below)
> into its tanks: petrol, diesel, jet fuel, naphtha, petroleum gas and fuel
> oil - and since 0.7.80 its conversion units' lubricants, bitumen and coke.
> The city's drivers (Motoring, at 6d; through the grocers' pumps since
> 0.7.83) draw the petrol, and its railway (Freight.haul()) and since 0.7.83
> its vans the diesel, off those tanks before the world, as the factories
> do the lubricants and the builders the bitumen (0.7.83); the rest leaves
> by the export-bound line until something here buys it - the lubricants
> since 0.7.103 no more than the world takes (LUBRICANTS_WORLD_SHARE).
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
> on its cost at the city's own prices (at the margin since 0.7.105, THE
> PLANNER'S PRICES AT THE MARGIN): a conversion unit on its spread on
> the stream it would find spare, a crude unit (for the city's own petrol
> and diesel or its wells' spare crude) with the units its cuts would feed.
> Until then it was one building, the Oil Refinery, for a whole plant's
> worth of the city's own petrol and diesel or of its wells' spare crude,
> on the investors' estimate over its slate (K's rule, 0.7.62 to 0.7.81).
> 
> AND ITS TANK FARM (0.7.85, batch O8; spec-oil 2.8; THE TANK FARM, below):
> with one standing the refiners keep a month of their crude on hand (since
> 0.7.105 of their run, an MR's cargo of room kept free: THE NEXT CARGO'S
> ROOM) and run on what they have and what they bought.
> 
> AND ITS RUN (0.7.105, batch P6; THE RUN, below): the crude units run only
> the barrels that pay at what their products would fetch, moving a quarter
> of nameplate a month at most; what that leaves idle is spare, and sold.

**Uses:** [Good](Good.md) (112), [RefineryFlow](RefineryFlow.md) (64), [SpreadPlanner](SpreadPlanner.md) (52), [BuildingsTemplate](BuildingsTemplate.md) (33), [BusinessInvestment](BusinessInvestment.md) (18), [Deposit](Deposit.md) (15), [GoodsMarket](GoodsMarket.md) (11), [Sector](Sector.md) (7), [Ports](Ports.md) (6), [Game](Game.md) (5), [Sectors](Sectors.md) (3), [Oil](Oil.md) (3), [BuildingsStacks](BuildingsStacks.md) (2), [Formats](Formats.md) (2), [BuildingType](BuildingType.md) (1), [Trade](Trade.md) (1), [LandManager](LandManager.md) (1)

**Used by (21):** [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingVisual](BuildingVisual.md), [LongPlaytest](LongPlaytest.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PortCheck](PortCheck.md), [Ports](Ports.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryFlow](RefineryFlow.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [SpreadPlanner](SpreadPlanner.md), [StrategicReserve](StrategicReserve.md)

## Sections

| line | section |
|---:|---|
| 67 | THE SLATE (0.7.76, batch O1; spec-oil 2.2 and 2.3) |
| 262 | THE MONTH'S CRUDE MIX (0.7.79, batch O3; spec-oil 2.2) |
| 404 | · THE MONTH AS IT RAN (0.7.95, batch O11; docs/studies/spec-oil.md 2.12) |
| 437 | THE FLOW (0.7.80, batch O4; spec-oil 2.3) |
| 515 | · the reads, off the flow (spec-oil 2.3, 6) |
| 647 | THE WORLD'S DEMAND FOR LUBRICANTS (0.7.103, batch P4; Jerus's A9, |
| 746 | THE TANK FARM (0.7.85, batch O8; spec-oil 2.8, the research's 4.5) |
| 986 | THE RUN (0.7.105, batch P6; Jerus's A2 (b), 2026-10-10, on the |
| 1217 | · the run in words (THE RUN): the refiners' page, the refinery's picture and the oil page |
| 1272 | THE SPREAD PLANNER (0.7.82, batch O5; spec-oil 2.4) |
| 1866 | · idle, then shed |

## Enum constants

| line | constant | says |
|---:|---|---|
| 1072 | `Refining.RunStop.FIRST` |  |
| 1072 | `Refining.RunStop.PETROL` |  |
| 1072 | `Refining.RunStop.DIESEL` |  |
| 1072 | `Refining.RunStop.WELLS` |  |
| 1072 | `Refining.RunStop.LUBRICANTS` |  |
| 1072 | `Refining.RunStop.NAMEPLATE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 114 | `Refining.CRUDE_LITRES_PER_TONNE` | `1165` | Litres in a tonne of crude [P35]: the slate is struck in litres, the crude bought in tonnes. |
| 121 | `Refining.MEDIUM_VOL_PCT` | `{ 1.5, 6.0, 12.0, 12.0, 22.0, 24.0, 22.5 }` | The straight-run cuts of a MEDIUM crude, vol% (the research's 1.1, the blend): gas, light naphtha, heavy naphtha, kerosene, diesel, gas oil and residue, in that order (the CUT_ indexes); normalised to 1 in MEDIUM_CUTS. |
| 124 | `Refining.MEDIUM_CUTS` | `normalised(MEDIUM_VOL_PCT)` | ...each a share of the barrel, MEDIUM_VOL_PCT over its sum (the column normalised, as the prototype does). |
| 127 | `Refining.CUT_GAS` | `0, CUT_LIGHT_NAPHTHA = 1, CUT_HEAVY_NAPHTHA = 2, CUT_KEROSENE = 3, CUT_DIESEL...` | The cuts' places in MEDIUM_CUTS. |
| 131 | `Refining.LIGHT_VOL_PCT` | `{ 4.1, 8.4, 15.9, 13.9, 25.6, 21.3, 11.4 }` | The straight-run cuts of a LIGHT crude, vol%, in the CUT_ order: Brent, 38 degrees API [R1] (the research's 1.1; they sum to 100.6). |
| 134 | `Refining.HEAVY_VOL_PCT` | `{ 0.3, 5.1, 10.2, 13.8, 9.4, 24.3, 36.9 }` | ...of a HEAVY crude: Maya, 21.5 degrees API [R2], its 15.3 of naphtha split one to two light to heavy as the blend's is (spec-oil 2.2). |
| 137 | `Refining.CUTS` | `{ normalised(LIGHT_VOL_PCT), MEDIUM_CUTS, normalised(HEAVY_VOL_PCT) }` | Each grade's cuts, in Deposit.Grade's order, each column normalised to one (0.7.79): LIGHT_VOL_PCT's, MEDIUM_CUTS itself, HEAVY_VOL_PCT's. |
| 140 | `Refining.MEDIUM_MIX` | `mixOf(Deposit.Grade.MEDIUM)` | A run all of MEDIUM crude, the research's blend: the mix of imports, of a refinery that took no crude, and of every crude unit until 0.7.79. |
| 150 | `Refining.RESIDUE_PER_DIESEL` | `3` | Litres of residue a litre of diesel cuts into fuel oil (Q5): three to one, so four litres of fuel oil. |
| 153 | `Refining.PRODUCTS` | `{ Good.PETROL, Good.DIESEL, Good.LPG, Good.NAPHTHA, Good.JET, Good.FUEL_OIL, ...` | The products a crude unit makes, in the order a screen lists them: the two the city burns first. |
| 157 | `Refining.BOUGHT_HERE` | `{ Good.PETROL, Good.DIESEL }` | The products something in the city buys (spec-oil 2.5, O1): the drivers' petrol and the railway's diesel - what the gate is struck on. |
| 357 | `Refining.RUN_KEY` | `"run.share"` | The run's share in the saved extras (THE RUN). |
| 360 | `Refining.RUN_HISTORY_KEY` | `"run.history."` | ...and each of the last months' shares, the oldest at 0 (THE RUN). |
| 674 | `Refining.LUBRICANTS_WORLD_SHARE` | `.01` | The share of a city's crude, litre for litre, the world takes in lubricants a month: the real share of crude that becomes lubricants, about 1% [R37] (docs/studies/spec-oil.md 6; the research's risk line was 2%). |
| 785 | `Refining.CRUDE_COVER_MONTHS` | `1` | Months of the crude units' run kept on hand in a Tank Farm's room (★ spec-oil 2.8): one, a month's run. |
| 827 | `Refining.CARGO_KEPT_FREE` | `ham.citybuildersim.Ports.Ship.MR.tonnes()` | Tonnes of a farm's room kept free for the next cargo (R2): an MR's, the smallest tanker's (Ports.Ship.MR). |
| 1037 | `Refining.RUN_PACE` | `BusinessInvestment.MAX_RETIREMENT_FRACTION` | The most of nameplate the crude units' run moves in a month (★ the brief's example, not measured): BusinessInvestment.MAX_RETIREMENT_FRACTION, the quarter the spare-capacity rule sheds at most. |
| 1337 | `Refining.PACKAGE_MOST_UNITS` | `12` | The most conversion units a crude unit is weighed with: the prototype's twelve rounds (spread.py, its package) - more than one of each of the seven kinds. |
| 1340 | `Refining.KIND_NAMES` |  | Each kind's name, in Kind's order: what the idle months are saved by. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 285 | `private final double[] crudeMix` | The crude units' mix this month, each grade's share of their run in Deposit.Grade's order: last month's purchases. |
| 414 | `private RefineryFlow.Flow monthsFlow` | The flow at nameplate the month's products were made on; null before a month has run. |
| 417 | `private double[] monthsMix` | ...the crude mix it was struck on, each grade's share in Deposit.Grade's order; null before a month has run. |
| 420 | `private double monthsRate` | ...and the operating rate the month's production ran at; NaN before a month has run. |
| 423 | `private double monthsRun` | ...and the run's share of nameplate it ran (THE RUN); NaN before a month has run. |
| 476 | `private RefineryFlow.Flow flowSolved` | The flow last solved, and what it was solved on. |
| 477 | `private double[] flowFeed, flowMix, flowPrices` |  |
| 478 | `private double flowCrude` |  |
| 945 | `private boolean crudeCleared` | Whether the month's crude has cleared with a store kept, so the rate is held to the crude (afterClearing()); until the month's end. |
| 1040 | `private double runShare` | The share of the crude units' nameplate they run this month (THE RUN): 1 until the rule moves it. |
| 1043 | `private final List<Double> runHistory` | The shares the last months ran at, the oldest first, at most SpreadPlanner.IDLE_MONTHS of them: what the spare is read off. |
| 1348 | `private final SpreadPlanner planner` | The refiners' spread planner: their money gate and their kinds' idle months. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 1854 | **type** `public final class Refining extends Sector` | The refinery. |

### THE SLATE (0.7.76, batch O1; spec-oil 2.2 and 2.3) (lines 67-261)

| line | len | member | says |
|---:|---:|---|---|
| 143 | 5 | `public static double[] mixOf(Deposit.Grade g)` | A run all of one grade, as a mix in Deposit.Grade's order. |
| 159 | 7 | `private static double[] normalised(double[] pct)` |  |
| 168 | 4 | **type** `public record Slate(Map<Good, Double> products, double burned)` | One run's products, in litres (BITUMEN and COKE in tonnes), and the residue burned for want of diesel to cut it. |
| 170 | 1 | `public double of(Good g)` _(in Refining.Slate)_ | What the run makes of one product; 0 for one it makes none of. |
| 178 | 3 | `public static Slate slate(double tonnes)` | What `tonnes` of medium crude a month makes (pure): its cuts at MEDIUM_CUTS, each to its product, the residue cut three to one with the diesel and the rest burned. |
| 190 | 33 | `public static Slate slate(double tonnes, double[] mix)` | What `tonnes` of crude a month makes at a mix of the grades (pure, 0.7.79): each grade's share of the run cut by its column of CUTS, summed in Deposit.Grade's order, the heavy crude's residue kept apart and added to t... |
| 225 | 3 | `public static boolean isCrudeUnit(BuildingsTemplate t)` | Whether a building is a crude unit (0.7.76): one of the refiners' that takes crude - its products are the slate of it. |
| 230 | 3 | `public static boolean isConversionUnit(BuildingsTemplate t)` | Whether a building is one of the refiners' conversion units (0.7.80): a reformer, a cracking unit and the rest, fed a stream of the crude units' run (BuildingsTemplate.refineryUnit()). |
| 241 | 5 | `public static Map<Good, Double> madeBy(BuildingsTemplate t)` | What a building makes a month at nameplate, by good (pure, 0.7.76): a crude unit's slate of its crude, a conversion unit's products of its whole feed with nothing downstream to take them (0.7.80, RefineryFlow.madeAlon... |
| 248 | 4 | `public static double feedValueOf(BuildingsTemplate t, double[] value)` | What a conversion unit's whole feed is worth a month with no unit to take it, at `value` (RefineryFlow.values()): what its value added is struck against. |
| 253 | 8 | `public Refining()` |  |

### THE MONTH'S CRUDE MIX (0.7.79, batch O3; spec-oil 2.2) (lines 262-403)

| line | len | member | says |
|---:|---:|---|---|
| 288 | 1 | `public double[] getCrudeMix()` | The crude mix the month's slate is struck on (a copy), each grade's share in Deposit.Grade's order. |
| 291 | 3 | `public void setCrudeMixForTest(double[] mix)` | A fixture's crude mix, each grade's share in Deposit.Grade's order (SaveFileCheck's city, which runs no refinery): what endOfMonth() would strike. |
| 296 | 1 | `public Slate slateOf(double tonnes)` | What `tonnes` of crude make at this month's mix: slate(tonnes, getCrudeMix()). |
| 304 | 15 | `public static double[] monthsMix(double local, double[] liftedByGrade, double imported)` | The month's mix of what a refinery bought (pure, 0.7.79): `local` tonnes at the lift's mix (`liftedByGrade`, tonnes by grade; MEDIUM when the wells lifted none) and `imported` at MEDIUM, each a share of the two; MEDIU... |
| 327 | 19 | `public void endOfMonth(Game game)` | The month's markets have cleared: its crude mix struck from what the crude units bought, at home and abroad - and (0.7.82) each kind of unit standing counted idle or working in the month's flow, for the spread planner... |
| 348 | 7 | `protected void saveExtras(Map<String, Double> extras)` |  |
| 364 | 25 | `protected void restoreExtras(Map<String, Double> extras)` | A save from before 0.7.79, or one whose mix is not a mix, reads MEDIUM. |
| 391 | 12 | `public void reset()` |  |

### THE MONTH AS IT RAN (0.7.95, batch O11; docs/studies/spec-oil.md 2.12) (lines 404-436)

| line | len | member | says |
|---:|---:|---|---|
| 426 | 1 | `public RefineryFlow.Flow monthsFlow()` | The flow at nameplate this month's products were made on (THE MONTH AS IT RAN); null before a month has run since the city was founded or loaded. |
| 429 | 1 | `public double[] monthsMix()` | ...its crude mix, a copy, each grade's share in Deposit.Grade's order; null before a month has run. |
| 432 | 1 | `public double monthsRate()` | ...and the operating rate it ran at; NaN before a month has run. |
| 435 | 1 | `public double monthsRun()` | ...and the run's share of nameplate in it (0.7.105, THE RUN); NaN before a month has run. |

### THE FLOW (0.7.80, batch O4; spec-oil 2.3) (lines 437-514)

| line | len | member | says |
|---:|---:|---|---|
| 481 | 9 | `public double[] unitFeed(boolean onSite)` | Litres a month of feed each kind of conversion unit could take, by RefineryFlow.Kind's ordinal: its buildings standing, and with `onSite` those on site too. |
| 492 | 7 | `public double[] flowValues()` | The values the flow's spreads are struck at, by Good's ordinal: each product market's local price (★ O4-1); nothing for one with none. |
| 501 | 13 | `public RefineryFlow.Flow flow()` | This month's flow at nameplate: the standing crude units' crude, at the month's crude mix, through the standing conversion units, at the city's prices. |

### the reads, off the flow (spec-oil 2.3, 6) (lines 515-646)

| line | len | member | says |
|---:|---:|---|---|
| 519 | 3 | `public double getCapacity(Good g)` | Nameplate output of a product a month: the flow's (0.7.80) - with no conversion unit the slate of the crude its standing crude units take, at the month's crude mix (0.7.79). |
| 532 | 8 | `public double getPipeline(Good g)` | ...and what the units on site will add to it, which counts as supply for the planner: the flow with them less the flow without, never below nothing (a reformer on site makes more petrol and less naphtha). |
| 552 | 26 | `public double getStockCapacity(Good g)` | Tank room for a product: the tankage of its buildings (each template's `stock`, litres) times the product's share of the slate - an Oil Refinery's 25M L shared as its run is, so each product has the room its share of ... |
| 597 | 8 | `public void produceStock(Good g, GoodsMarket market)` | The month's production into the tanks - after shipping, from the tanks, whatever of a product they no longer have room for (0.7.76, ★ O1). |
| 641 | 5 | `public double getMarginalCostPerUnit(Good g)` | A product's marginal cost to sell or ship: its share of the month's power and water over what the line makes at the rate - not of the crude, which is the run's (THE RUN'S PRODUCTS ALL LEAVE). |

### THE WORLD'S DEMAND FOR LUBRICANTS (0.7.103, batch P4; Jerus's A9, (lines 647-745)

| line | len | member | says |
|---:|---:|---|---|
| 677 | 3 | `public static double lubricantsWorldTakes(double crudeLitres)` | Litres of lubricants the world takes a month of a city whose crude units run `crudeLitres`: LUBRICANTS_WORLD_SHARE of them. |
| 682 | 3 | `public double lubricantsWorldTakes()` | ...this month's: of the crude the crude units run at the rate, in litres. |
| 687 | 4 | `public static double lubricantsRoom(RefineryFlow.Flow flow, double home)` | The lubricants a month the world and the city would still take past what `flow` makes: the world's share of its crude and `home`, less its lubricants; never below nothing. |
| 693 | 4 | `public static double lubricantsPerLitreOfFeed()` | Lubricants a lube unit makes of a litre of its gas oil: its kind's yield (RefineryFlow.Kind.LUBE). |
| 700 | 4 | `public double getExportBoundOutput(Good g)` | What the ship's line takes of a product: the maker's rule (Sector.getExportBoundOutput()), lubricants no more than the world takes this month. |
| 706 | 3 | `private double crudeOnSite()` | The crude the crude units on site will take, in tonnes a month. |
| 711 | 3 | `public double getCrudeDemand()` | Tonnes of crude the refineries want this month, at the rate they are running. |
| 723 | 4 | `public double unitsOf(BuildingsTemplate t)` | What a building is measured in, for the retirement rules: a crude unit's crude it takes (its products are its slate), and since 0.7.82 a conversion unit's feed, the litres a month of its stream it takes - so the spare... |
| 729 | 5 | `public static double boughtHere(Slate s)` | The products the city buys, a month, at a crude unit's nameplate: its petrol and its diesel. |
| 739 | 6 | `public double spareCrude()` | The crude the city's wells could lift this month that no refinery standing or on site will take, in tonnes (never below nothing). |

### THE TANK FARM (0.7.85, batch O8; spec-oil 2.8, the research's 4.5) (lines 746-985)

| line | len | member | says |
|---:|---:|---|---|
| 788 | 4 | `public static boolean isTankFarm(BuildingsTemplate t)` | Whether a building is a Tank Farm (0.7.85): one of the refiners' that runs nothing and has tanks - no crude unit, no conversion unit. |
| 794 | 3 | `public double tankFarmRoom()` | Litres of tank room the refiners' Tank Farms standing give. |
| 804 | 6 | `public double crudeKept()` | Tonnes of crude the refiners keep on hand: CRUDE_COVER_MONTHS of their crude units' run - since 0.7.105 the run's share of nameplate (THE RUN) - as far as their Tank Farms' room holds it with an MR's cargo kept free (... |
| 830 | 3 | `public boolean keepsCrude()` | Whether they keep crude: a Tank Farm stands, or crude is left on hand from one. |
| 848 | 7 | `public double crudeFreightSavedByShip()` | Freight a tonne of crude brought in would save going by sea, in city money: crude's lorry freight at the band's and the railway's shares in force, less the ship's (Ports.SEA_FREIGHT_SHARE_LIQUID). |
| 857 | 6 | `public double crudeHeldBackForThem(ham.citybuildersim.Ports ports)` | Tonnes a month of the refiners' own imported crude a port holds back for room: their share of the crude across the boundary, of what is held back. |
| 865 | 5 | `public static SpreadPlanner.Candidate farmEstimate(BuildingsTemplate farm, double tonnes, double saved, SpreadPlanner.City city)` | A Tank Farm weighed (pure): `tonnes` of crude a month at `saved` a tonne, less its running and standing; refused at its feed with nothing held back. |
| 872 | 14 | `public SpreadPlanner.Candidate farmCandidate(BusinessInvestment plans, Game game)` | The Tank Farm's candidate this month: null with no farm in the catalogue, one standing or on site, or none of their crude held back. |
| 895 | 34 | `public List<Sector.Line> ownLines(Game game)` | ...AND THE PORT'S TANKERS LAND IN IT (0.7.86, batch O9; spec-oil 2.9): a tanker berth takes crude only while the farms have room free for at least an MR's cargo (ham.citybuildersim.Ports.freeCrudeRoom()). |
| 932 | 3 | `public boolean buysAhead(Good g)` | Their crude, while they keep it, is stock (Sector.buysAhead()). |
| 938 | 5 | `public double bid(Good g)` | ...and their order for it is the month's run at the rate, and what brings the store back to crudeKept(). |
| 948 | 3 | `protected void afterClearing(Good g)` |  |
| 953 | 4 | `double monthsCrudeFill()` | The crude bought this month, at home and from the world, in tonnes. |
| 963 | 4 | `public static double crudeRunCap(double onHand, double fill, double nameplate)` | The share of nameplate the crude on hand and the month's fill will run (pure): the two over the crude units' nameplate run, in tonnes; no cap (infinite) with no crude unit. |
| 969 | 3 | `public static double crudeAfterRun(double onHand, double fill, double run)` | What is left on hand after the month's run (pure): what it had and the fill, less the run, never below nothing. |
| 979 | 6 | `public double getOperatingRate()` | The plant's rate times the run (THE RUN, 0.7.105: runShare()) - and, once crude has cleared with a store kept, no more than the crude will run (crudeRunCap()). |

### THE RUN (0.7.105, batch P6; Jerus's A2 (b), 2026-10-10, on the (lines 986-1216)

| line | len | member | says |
|---:|---:|---|---|
| 1046 | 1 | `public double runShare()` | The share of the crude units' nameplate they run this month (THE RUN). |
| 1049 | 1 | `public double plantRate()` | The plant's own rate - staffing, power, water, roads, health and vans (Sector.getOperatingRate()) - without the run: what the planner weighs an order at. |
| 1052 | 3 | `public void setRunShareForTest(double share)` | A fixture's run, as a save would give it (nothing in the game calls it). |
| 1068 | 2 | **type** `public record RunPrices(double[] home, double[] away, double[] take, double lubricantsHome, double wellsCru...` | What a barrel's products fetch, and what its crude costs (THE RUN). |
| 1072 | 1 | **type** `public enum RunStop` | Where the run stops (THE RUN): at no barrel, where the city's take of petrol or diesel is covered, where the wells' crude runs out, where the world's lubricants are taken, or at nameplate. |
| 1083 | 1 | **type** `public record RunVerdict(double share, RunStop stop, double first, double last, double next)` | The run's verdict (runRule()). |
| 1092 | 31 | `public static RunVerdict runRule(RefineryFlow.Flow f, RunPrices p)` | The run (pure; THE RUN): the share of nameplate whose barrels each beat their crude - the flow's products a tonne of its crude at `p`'s prices, the city's take of petrol and diesel at home and the rest away, less the ... |
| 1125 | 4 | `private static double stepAt(List<double[]> steps, double tonnes)` | The step at `tonnes` (the first there, in steps' order). |
| 1131 | 3 | `static double crudeCostAt(double at, RunPrices p)` | A tonne's crude at the `at`-th tonne of the run: the wells' while they last, then the world's. |
| 1136 | 18 | `static double marginAt(double at, double[] q, RunPrices p)` | A tonne's margin at the `at`-th tonne of the run: its products (`q`, a tonne's, by Good's ordinal) at what they fetch there, less its crude and its other cost. |
| 1156 | 30 | `public RunPrices runPrices()` | This month's prices for the run (THE RUN), read after the clearing: petrol and diesel's net import price and the city's take of each on the trend (BusinessInvestment.forecast()), every product's net export price, the ... |
| 1188 | 3 | `public RunVerdict runVerdict()` | The run's verdict this month (runRule() on the campus at nameplate and runPrices()): what the month's end moves the run toward. |
| 1193 | 4 | `public static double paced(double share, double toward)` | The share the run moves to from `share` toward `toward` (pure): at most RUN_PACE either way, within nothing and nameplate. |
| 1205 | 11 | `private void strikeRun()` | The month's end for the run: the share it ran at kept (at most IDLE_MONTHS of them), and next month's struck toward the verdict at the pace. |

### the run in words (THE RUN): the refiners' page, the refinery's picture and the oil page (lines 1217-1271)

| line | len | member | says |
|---:|---:|---|---|
| 1220 | 4 | `static String marginWords(double share)` | A margin, a share of the crude's cost, in words: "loses 1.7%", "earns 2.3%". |
| 1231 | 19 | `public static String runWords(RunVerdict v, double share)` | Why the crude units run what they run, in a sentence or two (THE RUN): where the run stops and what the barrels either side of it earn - and, while the run is still moving there, the share it is heading for. |
| 1252 | 11 | `public static String runShortWords(RunVerdict v)` | ...in a few words, for a table's cell: "every barrel pays", "first barrel loses 1.7%", "past the city's petrol, loses 0.7%". |
| 1265 | 6 | `public double runUsed()` | The most of nameplate the run used over the last IDLE_MONTHS months; NaN until it has run that long (THE RUN: what the spare is read off). |

### THE SPREAD PLANNER (0.7.82, batch O5; spec-oil 2.4) (lines 1272-1865)

| line | len | member | says |
|---:|---:|---|---|
| 1341 | 5 | `static { ... }` |  |
| 1351 | 1 | `public SpreadPlanner planner()` | The refiners' spread planner (a probe sets its money gate for the counterfactual). |
| 1363 | 11 | `public double[] cityValues()` | Each product at the city's own price at the margin (SpreadPlanner. |
| 1376 | 7 | `public double[] awayValues()` | Each product's net export price, by Good's ordinal (0 for one the world will not take): what a litre past the city's imports fetches (R1). |
| 1385 | 8 | `public double importsOf(Good g)` | Litres a month of a product the city imports on the trend past what its refinery makes at nameplate and has coming (R1; the Outlook's room, one product); never below nothing. |
| 1420 | 5 | `static double worth(Outlook o, double[] delta)` | Petrol and diesel past the city's imports at the net export price (R1, pure): what `delta` (litres by Good's ordinal, a change in the products) is worth at the outlook's values, less, for each of the two, the litres p... |
| 1427 | 9 | `static double pastImports(Outlook o, double[] delta)` | ...the part of it lost past the city's imports: petrol and diesel past what it imports, at their value less their net export price; nothing in the prototype's frame. |
| 1438 | 3 | `static double runWith(Outlook o, RefineryFlow.Flow f)` | The share of nameplate the run would take with the campus `f` standing (R1, THE RUN): runRule()'s, or the whole with no run in the outlook (the prototype's frame). |
| 1465 | 23 | **type** `public record Outlook(double[] feed, double crude, double[] mix, double[] values, double room, double spare...` | What the planner reads of the city (spec-oil 2.4). |
| 1470 | 4 | `public Outlook(double[] feed, double crude, double[] mix, double[] values, double room, double spareCrude, double[] spareMix, d...` _(in Refining.Outlook)_ | ...in the prototype's frame (spec-oil 2.4): no cap on lubricants. |
| 1476 | 5 | `public Outlook(double[] feed, double crude, double[] mix, double[] values, double room, double spareCrude, double[] spareMix, d...` _(in Refining.Outlook)_ | ...in the prototype's frame with the lubricants capped (0.7.103): every litre at `values`, no run. |
| 1482 | 5 | `private static double[] endless()` _(in Refining.Outlook)_ |  |
| 1490 | 16 | `public Outlook outlook(BusinessInvestment plans)` | This month's outlook, as plan() reads it. |
| 1515 | 10 | `double[] wellsGradesAhead()` | The grades the wells lift next: their month's potential laid along the city's oil from what has been lifted (LandManager.oilRuns()); MEDIUM with no wells. |
| 1532 | 17 | `public static double[] gradesAhead(double[][] runs, double from, double tonnes)` | The grades of the city's oil from `from` to `from + tonnes` tonnes, as it is worked out (pure): each run's part of it ({end, grade's ordinal}, LandManager.oilRuns()), past the last run MEDIUM, as a mix in Deposit.Grad... |
| 1551 | 10 | `static double[] blend(double[] a, double wa, double[] b, double wb)` | Two runs' mixes as one: `a` over `wa` tonnes and `b` over `wb`, each grade's share of the two; MEDIUM_MIX for none. |
| 1563 | 7 | `public static double spareFeed(RefineryFlow.Flow f, RefineryFlow.Kind k)` | The feed a kind of unit would find spare in a flow (the prototype's): its stream's; the coker's the residue and the heavy residue; a hydrocracker's no more gas oil than the spare hydrogen treats. |
| 1572 | 4 | `static String feedWords(RefineryFlow.Kind k)` | What a kind's feed is called in a refusal: its stream, and a hydrocracker's the hydrogen with it. |
| 1583 | 9 | `public static List<SpreadPlanner.Candidate> appraise(Outlook o, List<BuildingsTemplate> templates, SpreadPlanner.City city)` | Every building of the refiners' weighed at the gates, in `templates`' order (pure in its arguments): each conversion unit on its spread, each crude unit as a package. |
| 1594 | 3 | `public static SpreadPlanner.Candidate unitEstimate(Outlook o, RefineryFlow.Flow flow, BuildingsTemplate t, SpreadPlanner.City c...` | One conversion unit weighed at an outlook (pure): unitEstimate()'s, its lubricants capped at the outlook's, and since 0.7.105 (R1) its petrol and diesel past the city's imports at the net export price, at the run the ... |
| 1599 | 3 | `public static SpreadPlanner.Candidate unitEstimate(RefineryFlow.Flow flow, BuildingsTemplate t, SpreadPlanner.City city)` | One conversion unit weighed (pure): its spread on the feed it would find spare in `flow`, at most its own, at the rate less its costs; its feed gate FEED_GATE of one. |
| 1604 | 4 | `public static SpreadPlanner.Candidate unitEstimate(RefineryFlow.Flow flow, BuildingsTemplate t, SpreadPlanner.City city, double...` | ...a lube unit on no more gas oil than makes the lubricants the world and the city's `lubricantsHome` would still take (0.7.103; lubricantsRoom()). |
| 1610 | 29 | `private static SpreadPlanner.Candidate unitEstimate(RefineryFlow.Flow flow, BuildingsTemplate t, SpreadPlanner.City city, doubl...` | ...and with an outlook (0.7.105, R1), past the city's imports at the net export price and at the run with it standing; none, as before. |
| 1641 | 5 | `static double[] madeOf(RefineryFlow.Kind k, double litres)` | What `litres` of a kind's feed make with nothing downstream, by Good's ordinal (RefineryFlow.madeAlone()). |
| 1658 | 60 | `public static SpreadPlanner.Candidate packageEstimate(Outlook o, RefineryFlow.Flow before, BuildingsTemplate t, List<BuildingsT...` | One crude unit weighed as a package (pure; spec-oil 2.4, rule 6 and the package): a candidate only while the city's petrol and diesel short on the trend come to FEED_GATE of what it would make of them, or the wells' s... |
| 1720 | 6 | `private static double bestGross(RefineryFlow.Flow f, BuildingsTemplate u, Outlook o)` | A unit's gross in a package round: its spread on the feed it would find spare in `f`, at most its own, its lubricants capped at the outlook's (packageEstimate()). |
| 1728 | 7 | `static int sizeOf(BuildingsTemplate t, List<BuildingsTemplate> templates)` | A crude unit's size among the crude units of `templates`: 0 for the one taking the least crude, and so on. |
| 1737 | 7 | `static BuildingsTemplate unitOfSize(RefineryFlow.Kind k, int size, List<BuildingsTemplate> templates)` | A kind's unit of a size: its templates by feed, the smallest first, the one at `size` (its largest past them); null for a kind with none. |
| 1746 | 3 | `static String a(String name)` | "a Small Coker", "an Oil Refinery". |
| 1751 | 11 | `static String named(SpreadPlanner.Candidate c)` | A candidate named, with the units it was weighed with: "an Oil Refinery with a Small Lube Plant and a Small Coker behind it". |
| 1770 | 43 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Whether to build, and what (THE SPREAD PLANNER): one order in flight; of the refiners' buildings that pass the gates, the one that earns most on its cost; with none, ground if ground was all that stood in the way, els... |
| 1832 | 8 | `int crudeUnitsOnSite()` | The crude units on site, anyone's order (R3). |
| 1842 | 7 | `public SpreadPlanner.Candidate appraise(BuildingsTemplate t, BusinessInvestment plans)` | One building of the refiners' weighed now (THE SPREAD PLANNER), at this month's outlook and the game's gates. |
| 1857 | 8 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more building of the refiners' would earn its owner a month (THE SPREAD PLANNER): a conversion unit's earnings on the feed it would find, and a crude unit's share of its package's earnings by cost - what Game... |

### idle, then shed (lines 1866-1918)

| line | len | member | says |
|---:|---:|---|---|
| 1869 | 9 | `private void noteIdleUnits()` | The month's end for the units: each kind standing counted idle (no feed in the month's flow) or working. |
| 1880 | 8 | `public double idleFeed()` | Litres a month the kinds idle long enough to shed would take: their units standing. |
| 1890 | 1 | `public int idleMonths(RefineryFlow.Kind k)` | Months a kind of unit has stood idle running (extras idleMonths.<KIND>). |
| 1894 | 5 | `public boolean mayRetire(BuildingsTemplate t)` | A conversion unit only once its kind has stood idle long enough; a crude unit only while no kind has (THE SPREAD PLANNER); a Tank Farm never (it makes nothing either rule could measure, and neither sold one). |
| 1912 | 6 | `public double[] retirementDemandAndCapacity(Game game)` | The spare-capacity rule's measure: {0, the idle kinds' feed} while a kind stands idle long enough to shed - and otherwise, since 0.7.105 (THE RUN), {what the run used, the crude units' nameplate} in tonnes once the ru... |

