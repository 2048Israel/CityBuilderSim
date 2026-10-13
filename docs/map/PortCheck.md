# PortCheck.java - 873 lines · 21 methods · 7 constants · harnesses

`ham/citybuildersim/PortCheck.java` - generated 2026-10-13 by CodeMap; line numbers are as of that run.

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
>      berths, the city's, on the coast.
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
>      its crews paid with transit's; the calls tonnes over the ship's cargo;
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
> the sea, as WaterCheck buys it), the terminals and the plants stood, and
> the pure sections hand Ports its month's tonnes and berths.

**Uses:** [Ports](Ports.md) (160), [Good](Good.md) (88), [BoatSchedule](BoatSchedule.md) (42), [Game](Game.md) (35), [Traffic](Traffic.md) (32), [BuildingsTemplate](BuildingsTemplate.md) (16), [World](World.md) (11), [Founding](Founding.md) (8), [Refining](Refining.md) (7), [SpreadPlanner](SpreadPlanner.md) (7), [GameFiles](GameFiles.md) (5), [GoodsMarket](GoodsMarket.md) (5), [InfrastructureManager](InfrastructureManager.md) (4), [BuildCard](BuildCard.md) (4), [BuildingManager](BuildingManager.md) (3), [Rail](Rail.md) (3), [HeavyIndustry](HeavyIndustry.md) (3), [MoneyAudit](MoneyAudit.md) (2), [LandManager](LandManager.md) (2), [LandParcel](LandParcel.md) (2), [MiningCheck](MiningCheck.md) (2), [JobType](JobType.md) (2), [Sector](Sector.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [CityLand](CityLand.md) (1), [LongPlaytest](LongPlaytest.md) (1), [BuildingCatalog](BuildingCatalog.md) (1), [BuildingType](BuildingType.md) (1), [Sectors](Sectors.md) (1)

## Sections

| line | section |
|---:|---|
| 111 | · the research's rule (runs/research-freight.md 5), its rows as the research writes them |
| 215 | 1. THE RULE |
| 239 | 2. THE CARGO, THE SHIPS AND THE TERMINALS |
| 307 | 3. THE FACTOR |
| 341 | 4. THE SHARES |
| 416 | 5. NO PORT, THE SAME BAND |
| 458 | 6. A PORT ON A TOWN |
| 568 | 7. CRUDE NEEDS ROOM |
| 685 | 8. THE BOATS |
| 819 | 9. THE SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 109 | `PortCheck.FILES` | `new java.util.IdentityHashMap<>()` | Each fixture town's save folder. |
| 114 | `PortCheck.KM` | `5000` | The distance the shares are struck at, km: the research's headline. |
| 117 | `PortCheck.LORRY_F` | `2.0, LORRY_C =.080, ACCESS_KM = 50` | A lorry: F US$2.0 a tonne, c US$0.080 a tonne-km; and the leg to a quay at each end, 50 km. |
| 120 | `PortCheck.LIQUID_ROWS` | `{ { 4 + 1,.0012 }, { 4 + 2,.0022 }, { 4 + 5,.0035 }, { 4 + 6,.0035 } }` | The sea rows, {F, c}: liquid VLCC, Suezmax, Aframax-LR, MR... |
| 122 | `PortCheck.DRY_ROWS` | `{ { 6 + 1.2,.00075 }, { 6 + 5,.0022 } }` | ...dry Capesize, Panamax... |
| 124 | `PortCheck.BOX_ROWS` | `{ { 24,.0055 } }` | ...deep-sea boxes... |
| 126 | `PortCheck.GENERAL_ROWS` | `{ { 40,.0060 } }` | ...and general cargo. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `static int fails` |  |
| 70 | `static PrintStream out` |  |
| 71 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 67 | 807 | **type** `public class PortCheck` | The city's ports and their boats (0.7.86, batch O9; docs/studies/spec-oil.md 2.9, 2.10 and 4's PortCheck; runs/research-freight.md 5). |
| 73 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 78 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 83 | 5 | `static void quietly(Runnable r)` |  |
| 89 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |
| 96 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 103 | 4 | `static double auditMiss(Game g)` | The month's audit residual as a share of the house's money tolerance at what moved (MoneyAudit.tolerance()): at most 1 closes. |

### the research's rule (runs/research-freight.md 5), its rows as the research writes them (lines 111-214)

| line | len | member | says |
|---:|---:|---|---|
| 129 | 5 | `static double ruleShare(double[][] rows)` | Sea against a lorry, door to door, at KM: each row's, averaged over the kind's rows. |
| 140 | 19 | `static Game town(String label, int houses, double spare)` | A town that pays its people: houses, shops, bakeries to work in, power, water, roads and builders, standing, on ground a quarter more than they take and `spare` more (OilCheck's town). |
| 161 | 29 | `static boolean buyCoast(Game g)` | Buys toward the sea nearest the site, the offer nearest it each time, until one standing runs out to it, and buys that (WaterCheck's way). |
| 191 | 23 | `public static void main(String[] args) throws Exception` |  |

### 1. THE RULE (lines 215-238)

| line | len | member | says |
|---:|---:|---|---|
| 217 | 21 | `static void theRule()` |  |

### 2. THE CARGO, THE SHIPS AND THE TERMINALS (lines 239-306)

| line | len | member | says |
|---:|---:|---|---|
| 241 | 65 | `static void cargoShipsAndTerminals()` |  |

### 3. THE FACTOR (lines 307-340)

| line | len | member | says |
|---:|---:|---|---|
| 309 | 31 | `static void theFactor()` |  |

### 4. THE SHARES (lines 341-415)

| line | len | member | says |
|---:|---:|---|---|
| 343 | 5 | `static double[] tonnes(Object...pairs)` |  |
| 349 | 66 | `static void theShares()` |  |

### 5. NO PORT, THE SAME BAND (lines 416-457)

| line | len | member | says |
|---:|---:|---|---|
| 418 | 39 | `static void noPortTheSameBand()` |  |

### 6. A PORT ON A TOWN (lines 458-567)

| line | len | member | says |
|---:|---:|---|---|
| 460 | 107 | `static Game aPortOnATown()` |  |

### 7. CRUDE NEEDS ROOM (lines 568-684)

| line | len | member | says |
|---:|---:|---|---|
| 570 | 114 | `static void crudeNeedsRoom()` |  |

### 8. THE BOATS (lines 685-818)

| line | len | member | says |
|---:|---:|---|---|
| 687 | 113 | `static void theBoats()` |  |
| 802 | 16 | `static Ports monthAtSea()` | A month's trade by sea on every kind: Ports handed a month with each kind's berths taking its share. |

### 9. THE SAVE (lines 819-873)

| line | len | member | says |
|---:|---:|---|---|
| 821 | 52 | `static void acrossASave(Game g) throws Exception` |  |

