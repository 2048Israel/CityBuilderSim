# TradeBandCheck.java - 534 lines · 19 methods · 1 constants · harnesses

`ham/citybuildersim/TradeBandCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> TradeBand, the model behind the Trade tab's band in legs, held to the
> study's last bullet of section 6 (docs/studies/spec-freight-and-infrastructure.md):
> the freight band split into the world's margin, the long haul by mode (the
> world's lorries, the railway, the ships) and the city leg.
> 
> WHY THIS HARNESS EXISTS. The screen only paints TradeBand's figures, so the
> figures are the finding: a leg the model did not keep, a share worked out in
> the screen or a division by a city's zero would show on a player's page and
> in no other check. It is written from the model's getters, and where the band
> says a figure it must equal the getter that keeps it. Six claims:
> 
>   1. NO TRADE, NO NUMBER. A city that has never traded reads rows of its
>      prices and a whole of zeros; with no railway and no berth the lorries
>      carry the whole freight in the band, and no figure is NaN or infinite.
>   2. THE LEGS SUM TO THE BAND. In a played city the world's margin, the
>      lorries' freight and the ships' sum to the band the price grid shows
>      (importPrice() less exportPrice()); with the railway's charge, the
>      delivered gap (netImportPrice() less netExportPrice()); and each piece is
>      its getter: worldMargin(), railInWedge(), the quay's handling quote and
>      Haulage's leg price.
>   3. THE LORRIES AND THE SHIPS SHARE ONE FIGURE. The freight still paid
>      abroad (freightInBand()) is the lorries' part and the ships' part,
>      the ships' at their kind's sea freight on the berths' share in force
>      (Ports.seaFreightShareOf(), Freight.seaShareInForce()), and the order and
>      the hatching are the Freight page's.
>   4. THE WHOLE IS THE MONTH'S TRADE IN THE SAME PIECES. Each part's money is
>      the goods' crossings at their prices times half the two-way piece (the
>      city leg's the way it was received or made), the money that crossed is
>      the rows' and no more than the books' balance of trade, and the shares
>      of the gap sum to one.
>   5. THE ORDER. Goods that crossed come first, largest first; asking twice
>      gives the same band.
>   6. A RELOAD reads the same band: the shares in force, the quay's quote and
>      the leg prices are saved, so a loaded city's pieces are the live city's,
>      and a month on, with the books struck again, so is the whole.
> 
> FS7: the fixtures. 1 is a founded city. 2, 3, 4 and 5 read a city BUILT for the band, nothing played (built()): the
> Haulage fixture's mines and plant with two Rail Depots, two Rail Spurs, a Truck Depot, a General Cargo Terminal, two
> Tanker Terminals and two Bulk Terminals laid on it, the railway's carried shares, the home and quay shares, the city
> leg's import and export shares and the quay's quotes set on Freight's state (restored), the month's tonnes handed to
> Ports so the berths of each kind take a set half (PortCheck.handMonth), and the month's books - what each good sold and
> bought abroad - set on a sector's statement; TradeBand.of() is pure, so the band follows. 6 stays the played full city
> (full()), saved and loaded.
> 
> It tests the model through its public getters and constants and changes
> nothing in it; where the code and the brief disagree it says FAIL and the
> line says where. Every fixture is a city of its own in GameFiles.scratch().

**Uses:** [Good](Good.md) (20), [Game](Game.md) (17), [HaulageCheck](HaulageCheck.md) (12), [TradeBand](TradeBand.md) (8), [GameFiles](GameFiles.md) (7), [Ports](Ports.md) (7), [GoodsMarket](GoodsMarket.md) (6), [Sector](Sector.md) (5), [Freight](Freight.md) (4), [Traffic](Traffic.md) (4), [FreightView](FreightView.md) (4), [Money](Money.md) (3), [Haulage](Haulage.md) (2), [Sectors](Sectors.md) (2), [Founding](Founding.md) (1), [HomeRailCheck](HomeRailCheck.md) (1), [SectorState](SectorState.md) (1), [PortCheck](PortCheck.md) (1), [Statement](Statement.md) (1)

## Sections

| line | section |
|---:|---|
| 115 | THE FIXTURES |
| 240 | 1. NO TRADE |
| 277 | 2. THE LEGS SUM TO THE BAND |
| 331 | 3. THE LORRIES AND THE SHIPS |
| 374 | 4. THE WHOLE |
| 441 | 5. THE ORDER |
| 463 | 6. A RELOAD |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 150 | `TradeBandCheck.QUOTE` | `.0006` | FS7: the quay's handling quote a tonne, as set on a built city: any figure, the band reads it back. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 65 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 63 | 472 | **type** `public class TradeBandCheck` | TradeBand, the model behind the Trade tab's band in legs, held to the study's last bullet of section 6 (docs/studies/spec-freight-and-infrastructure.md): the freight band split into the world's margin, the long haul b... |
| 67 | 3 | `static void quietly(Runnable r)` |  |
| 71 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 76 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 82 | 3 | `static boolean same(double a, double b)` | Bitwise, not near: the band hands a getter's figure on unchanged. |
| 87 | 3 | `static boolean near(double a, double b)` | Within a rounding: sums the model makes in another order. |
| 92 | 22 | `static String nonFinite(Object o, String path)` | Every double in a record (lists included) is finite; the first that is not, named. |

### THE FIXTURES (lines 115-239)

| line | len | member | says |
|---:|---:|---|---|
| 120 | 8 | `static Game unplayed(String label)` | A founded city that has never been played: no haul has run, nothing has crossed. |
| 137 | 11 | `static Game full(GameFiles files)` | FreightViewCheck's full city, built here from HomeRailCheck's railed one (HaulageCheck's three iron mines and a pelletising plant, played fourteen months) with two Rail Depots, two Rail Spurs and a Bulk Terminal laid,... |
| 160 | 66 | `static Game built(GameFiles files)` | FS7: a city BUILT for the band (see the class header), nothing played: HaulageCheck.base()'s mines and plant, the railway and the berths laid on it (HaulageCheck.lay: instant), then the states the band reads set direc... |
| 228 | 4 | `static Row row(Band b, Good g)` | A row by good. |
| 234 | 5 | `static boolean measured(Game g, Good good)` | The goods the band measures: the ones the city can both buy and sell abroad, at a price. |

### 1. NO TRADE (lines 240-276)

| line | len | member | says |
|---:|---:|---|---|
| 244 | 32 | `static void noTrade()` |  |

### 2. THE LEGS SUM TO THE BAND (lines 277-330)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 49 | `static void legs(Game g, Band b)` |  |

### 3. THE LORRIES AND THE SHIPS (lines 331-373)

| line | len | member | says |
|---:|---:|---|---|
| 335 | 38 | `static void lorriesAndShips(Game g, Band b)` |  |

### 4. THE WHOLE (lines 374-440)

| line | len | member | says |
|---:|---:|---|---|
| 378 | 62 | `static void whole(Game g, Band b)` |  |

### 5. THE ORDER (lines 441-462)

| line | len | member | says |
|---:|---:|---|---|
| 445 | 17 | `static void order(Game g, Band b)` |  |

### 6. A RELOAD (lines 463-534)

| line | len | member | says |
|---:|---:|---|---|
| 467 | 42 | `static void reload(Game g, GameFiles files)` |  |
| 510 | 4 | `static boolean sameGoods(Band a, Band b)` |  |
| 515 | 19 | `public static void main(String[] args) throws Exception` |  |

