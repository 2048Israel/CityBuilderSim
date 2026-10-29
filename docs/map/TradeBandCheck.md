# TradeBandCheck.java - 440 lines · 18 methods · 0 constants · harnesses

`ham/citybuildersim/TradeBandCheck.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

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
> It tests the model through its public getters and constants and changes
> nothing in it; where the code and the brief disagree it says FAIL and the
> line says where. Every fixture is a city of its own in GameFiles.scratch().

**Uses:** [Game](Game.md) (15), [Good](Good.md) (8), [TradeBand](TradeBand.md) (8), [HaulageCheck](HaulageCheck.md) (5), [GameFiles](GameFiles.md) (5), [GoodsMarket](GoodsMarket.md) (5), [FreightView](FreightView.md) (4), [Freight](Freight.md) (3), [Money](Money.md) (3), [Haulage](Haulage.md) (2), [Ports](Ports.md) (2), [Sectors](Sectors.md) (2), [Founding](Founding.md) (1), [HomeRailCheck](HomeRailCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 107 | THE FIXTURES |
| 154 | 1. NO TRADE |
| 191 | 2. THE LEGS SUM TO THE BAND |
| 241 | 3. THE LORRIES AND THE SHIPS |
| 283 | 4. THE WHOLE |
| 349 | 5. THE ORDER |
| 370 | 6. A RELOAD |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 55 | 386 | **type** `public class TradeBandCheck` | TradeBand, the model behind the Trade tab's band in legs, held to the study's last bullet of section 6 (docs/studies/spec-freight-and-infrastructure.md): the freight band split into the world's margin, the long haul b... |
| 59 | 3 | `static void quietly(Runnable r)` |  |
| 63 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 68 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 74 | 3 | `static boolean same(double a, double b)` | Bitwise, not near: the band hands a getter's figure on unchanged. |
| 79 | 3 | `static boolean near(double a, double b)` | Within a rounding: sums the model makes in another order. |
| 84 | 22 | `static String nonFinite(Object o, String path)` | Every double in a record (lists included) is finite; the first that is not, named. |

### THE FIXTURES (lines 107-153)

| line | len | member | says |
|---:|---:|---|---|
| 112 | 8 | `static Game unplayed(String label)` | A founded city that has never been played: no haul has run, nothing has crossed. |
| 129 | 11 | `static Game full(GameFiles files)` | FreightViewCheck's full city, built here from HomeRailCheck's railed one (HaulageCheck's three iron mines and a pelletising plant, played fourteen months) with two Rail Depots, two Rail Spurs and a Bulk Terminal laid,... |
| 142 | 4 | `static Row row(Band b, Good g)` | A row by good. |
| 148 | 5 | `static boolean measured(Game g, Good good)` | The goods the band measures: the ones the city can both buy and sell abroad, at a price. |

### 1. NO TRADE (lines 154-190)

| line | len | member | says |
|---:|---:|---|---|
| 158 | 32 | `static void noTrade()` |  |

### 2. THE LEGS SUM TO THE BAND (lines 191-240)

| line | len | member | says |
|---:|---:|---|---|
| 195 | 45 | `static void legs(Game g, Band b)` |  |

### 3. THE LORRIES AND THE SHIPS (lines 241-282)

| line | len | member | says |
|---:|---:|---|---|
| 245 | 37 | `static void lorriesAndShips(Game g, Band b)` |  |

### 4. THE WHOLE (lines 283-348)

| line | len | member | says |
|---:|---:|---|---|
| 287 | 61 | `static void whole(Game g, Band b)` |  |

### 5. THE ORDER (lines 349-369)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 16 | `static void order(Game g, Band b)` |  |

### 6. A RELOAD (lines 370-440)

| line | len | member | says |
|---:|---:|---|---|
| 374 | 42 | `static void reload(Game g, GameFiles files)` |  |
| 417 | 4 | `static boolean sameGoods(Band a, Band b)` |  |
| 422 | 18 | `public static void main(String[] args) throws Exception` |  |

