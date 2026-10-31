# PortCheck.java - 1,185 lines · 23 methods · 10 constants · harnesses

`ham/citybuildersim/PortCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The city's ports and their boats (0.7.86, batch O9; docs/studies/spec-oil.md
> 2.9, 2.10 and 4's PortCheck; runs/research-freight.md 5).
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE FREIGHT RULE: each kind's SEA_FREIGHT_SHARE is the research's rule
>      - F + c x d, sea against a lorry door to door, a 50 km lorry leg at
>      each end - at 5,000 km, from its own rows, to its two places; and every
>      kind's is under the railway's floor, so the berths go before it.
> 
>   2. THE CARGO, THE SHIPS AND THE TERMINALS: Good.cargo() as spec-oil 2.1
>      writes it; the ships' cargoes [P30][P40][P42][P46]; a tanker the
>      largest the free room takes, none under an MR; the four terminals'
>      berths, the city's, on the coast. And (FR3, docs/queue/FR3.md 2) the
>      Bulk Berth, id 142, and the Handysize and Supramax it takes: the
>      month's dry bulk calls in the classes of the largest dry bulk berth
>      standing, the Bulk Terminal's Panamax and Capesize as before. And
>      (SL1, 2026-10-28, section 2c) the four terminals as large modern real
>      ones, of several berths, their cost lines moved by their tonnes'
>      ratio; Ports.payingCount(), the terminals the uncovered tonnes pay
>      back, each counted after the ones before it, at its edges and the
>      dial, agreeing with berthPaysBack(); and main's 35 General Cargo
>      Terminals' trade (m3996) carried by the catalogue's 8.
> 
>   3. THE FACTOR equals its formula exactly, and with nothing at sea it is
>      the railway's own 1 - rail, the railway's share its own, to the bit.
> 
>   4. THE SHARES: a kind's berths are shared among its goods by their
>      tonnes; a kind under the quote goes before the railway and takes its
>      tonnes off what the railway is offered; one not under it takes only
>      what the railway leaves; crude with no tank room is held back, and
>      with room goes in the largest class it takes.
> 
>   5. NO PORT GIVES A BIT-IDENTICAL BAND: a town with a railway and no
>      terminal quotes 0.7.85's band, 1 - carried and carried x quote, every
>      good, to the bit, and nothing goes by sea.
> 
>   6. A PORT ON A TOWN: refused NO_COAST without owned sea, allowed with it;
>      its kind's goods then go by sea in its share, each good's band its
>      formula, narrower than the lorries'; the road told the ships' share;
>      its crews paid by Freight (FR3) and its upkeep with transit's; the calls tonnes over the ship's cargo;
>      the audit closing.
> 
>   7. CRUDE NEEDS ROOM: a refinery's crude with a tanker berth and no Tank
>      Farm is held back for room, and its page says so; with a farm it goes
>      by sea in the largest class the free room takes. AND THE NEXT CARGO'S
>      ROOM (0.7.105, Jerus's R2): a Crude Unit's month beside one farm left
>      14,185 t free, under an MR's cargo, so no tanker ever landed; the
>      refiners now keep an MR's cargo free, and its crude goes by sea.
> 
>   8. THE BOATS: whole calls, exact on average; inside their month; pure in
>      (call, t), loaded the right way each leg; a lane straight out away
>      from the site; and a frame touches only the routes on screen, and
>      finds every boat on them.
> 
>   9. THE SAVE: the shares in force and the month cross a save, the band and
>      the road with them, and a month on the city and its reload agree to
>      the bit; a save from before them loads with nothing at sea.
> 
> Every fixture CAUSES its condition: the coast is bought (the offer nearest
> ... (2 more lines in the source)

**Uses:** [Ports](Ports.md) (323), [Good](Good.md) (93), [BoatSchedule](BoatSchedule.md) (54), [Game](Game.md) (35), [BuildingsTemplate](BuildingsTemplate.md) (33), [Traffic](Traffic.md) (32), [BuildingManager](BuildingManager.md) (18), [World](World.md) (11), [Founding](Founding.md) (8), [JobType](JobType.md) (8), [Refining](Refining.md) (7), [SpreadPlanner](SpreadPlanner.md) (7), [GameFiles](GameFiles.md) (5), [TaxPolicy](TaxPolicy.md) (5), [GoodsMarket](GoodsMarket.md) (5), [Freight](Freight.md) (4), [BuildingType](BuildingType.md) (4), [InfrastructureManager](InfrastructureManager.md) (4), [BuildCard](BuildCard.md) (4), [BuildingCatalog](BuildingCatalog.md) (3), [HeavyIndustry](HeavyIndustry.md) (3), [MoneyAudit](MoneyAudit.md) (2), [LandManager](LandManager.md) (2), [LandParcel](LandParcel.md) (2), [MiningCheck](MiningCheck.md) (2), [Sector](Sector.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [CityLand](CityLand.md) (1), [LongPlaytest](LongPlaytest.md) (1), [Sectors](Sectors.md) (1)

**Used by (3):** [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCardCheck](BuildCardCheck.md), [LandlordPortCheck](LandlordPortCheck.md)

## Sections

| line | section |
|---:|---|
| 120 | · the research's rule (runs/research-freight.md 5), its rows as the research writes them |
| 226 | 1. THE RULE |
| 250 | 2. THE CARGO, THE SHIPS AND THE TERMINALS |
| 362 | 2b. THE BULK BERTH (FR3) |
| 433 | 2c. A TERMINAL OF SEVERAL BERTHS, ORDERED BY THE COUNT (SL1) |
| 604 | 3. THE FACTOR |
| 638 | 4. THE SHARES |
| 713 | 5. NO PORT, THE SAME BAND |
| 755 | 6. A PORT ON A TOWN |
| 880 | 7. CRUDE NEEDS ROOM |
| 997 | 8. THE BOATS |
| 1131 | 9. THE SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 118 | `PortCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder. |
| 123 | `PortCheck.KM` | `5000` | The distance the shares are struck at, km: the research's headline. |
| 126 | `PortCheck.LORRY_F` | `2.0, LORRY_C =.080, ACCESS_KM = 50` | A lorry: F US$2.0 a tonne, c US$0.080 a tonne-km; and the leg to a quay at each end, 50 km. |
| 129 | `PortCheck.LIQUID_ROWS` | `{ { 4 + 1,.0012 }, { 4 + 2,.0022 }, { 4 + 5,.0035 }, { 4 + 6,.0035 } }` | The sea rows, {F, c}: liquid VLCC, Suezmax, Aframax-LR, MR... |
| 131 | `PortCheck.DRY_ROWS` | `{ { 6 + 1.2,.00075 }, { 6 + 5,.0022 } }` | ...dry Capesize, Panamax... |
| 133 | `PortCheck.BOX_ROWS` | `{ { 24,.0055 } }` | ...deep-sea boxes... |
| 135 | `PortCheck.GENERAL_ROWS` | `{ { 40,.0060 } }` | ...and general cargo. |
| 436 | `PortCheck.MAIN_GENERAL_BERTH_TONNES_A_YEAR` | `450_000` | Tonnes a year of a General Cargo Terminal on main at 0.8.66, before SL1: the berth the default playtest stood full 35 of at m3996 (544,000 people). |
| 438 | `PortCheck.MAIN_GENERAL_BERTHS_AT_M3996` | `35` | ...and how many it stood, all full, at m3996. |
| 444 | `PortCheck.SPLIT_ROUNDING` | `2` | How far a terminal's cash, points or materials may sit from its ratio's rounding: 2. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 78 | `static int fails` |  |
| 79 | `static PrintStream out` |  |
| 80 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 76 | 1110 | **type** `public class PortCheck` | The city's ports and their boats (0.7.86, batch O9; docs/studies/spec-oil.md 2.9, 2.10 and 4's PortCheck; runs/research-freight.md 5). |
| 82 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 87 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 92 | 5 | `static void quietly(Runnable r)` |  |
| 98 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |
| 105 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 112 | 4 | `static double auditMiss(Game g)` | The month's audit residual as a share of the house's money tolerance at what moved (MoneyAudit.tolerance()): at most 1 closes. |

### the research's rule (runs/research-freight.md 5), its rows as the research writes them (lines 120-225)

| line | len | member | says |
|---:|---:|---|---|
| 138 | 5 | `static double ruleShare(double[][] rows)` | Sea against a lorry, door to door, at KM: each row's, averaged over the kind's rows. |
| 149 | 19 | `static Game town(String label, int houses, double spare)` | A town that pays its people: houses, shops, bakeries to work in, power, water, roads and builders, standing, on ground a quarter more than they take and `spare` more (OilCheck's town). |
| 170 | 29 | `static boolean buyCoast(Game g)` | Buys toward the sea nearest the site, the offer nearest it each time, until one standing runs out to it, and buys that (WaterCheck's way). |
| 200 | 25 | `public static void main(String[] args) throws Exception` |  |

### 1. THE RULE (lines 226-249)

| line | len | member | says |
|---:|---:|---|---|
| 228 | 21 | `static void theRule()` |  |

### 2. THE CARGO, THE SHIPS AND THE TERMINALS (lines 250-361)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 109 | `static void cargoShipsAndTerminals()` |  |

### 2b. THE BULK BERTH (FR3) (lines 362-432)

| line | len | member | says |
|---:|---:|---|---|
| 364 | 68 | `static void theBulkBerth()` |  |

### 2c. A TERMINAL OF SEVERAL BERTHS, ORDERED BY THE COUNT (SL1) (lines 433-603)

| line | len | member | says |
|---:|---:|---|---|
| 446 | 157 | `static void terminalsOfSeveralBerths()` |  |

### 3. THE FACTOR (lines 604-637)

| line | len | member | says |
|---:|---:|---|---|
| 606 | 31 | `static void theFactor()` |  |

### 4. THE SHARES (lines 638-712)

| line | len | member | says |
|---:|---:|---|---|
| 640 | 5 | `static double[] tonnes(Object...pairs)` |  |
| 646 | 66 | `static void theShares()` |  |

### 5. NO PORT, THE SAME BAND (lines 713-754)

| line | len | member | says |
|---:|---:|---|---|
| 715 | 39 | `static void noPortTheSameBand()` |  |

### 6. A PORT ON A TOWN (lines 755-879)

| line | len | member | says |
|---:|---:|---|---|
| 757 | 122 | `static Game aPortOnATown()` |  |

### 7. CRUDE NEEDS ROOM (lines 880-996)

| line | len | member | says |
|---:|---:|---|---|
| 882 | 114 | `static void crudeNeedsRoom()` |  |

### 8. THE BOATS (lines 997-1130)

| line | len | member | says |
|---:|---:|---|---|
| 999 | 113 | `static void theBoats()` |  |
| 1114 | 16 | `static Ports monthAtSea()` | A month's trade by sea on every kind: Ports handed a month with each kind's berths taking its share. |

### 9. THE SAVE (lines 1131-1185)

| line | len | member | says |
|---:|---:|---|---|
| 1133 | 52 | `static void acrossASave(Game g) throws Exception` |  |

