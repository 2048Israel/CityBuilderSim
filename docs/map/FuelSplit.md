# FuelSplit.java - 229 lines · 8 methods · 3 constants · model

`ham/citybuildersim/FuelSplit.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> FUEL, split into the PETROL and DIESEL it became: a save written before
> 0.7.76 converted once, on its load, before anything is restored (batch O1;
> runs/spec-oil.md 3).
> 
> WHY. Until 0.7.76 a refinery made FUEL, a litre of whatever a car or a
> locomotive burned (Good's note where FUEL was). Now it makes petrol for the
> drivers, diesel for the railway and the rest of the barrel, and a good is
> saved by its name - so a save from before carries "FUEL" in its markets,
> in its sectors' stock and books, and in its national accounts' goods held,
> and this build knows no such good: Sector.restore() would drop it, and the
> refiners' tanks, the month's sales and the market's demand would go with
> it.
> 
> THE SPLIT. p is the share of the month's litres the drivers burned: their
> litres (DataSave.householdFuel's third figure) over theirs and the
> railway's (Rail's `hauled` tonnes at Rail.FUEL_LITRES_PER_TONNE); with
> neither, p is 1. Every FUEL figure x becomes PETROL x p and DIESEL x less
> that (split()), so each pair sums to x to the bit - the petrol an ulp off
> x p where no diesel could close the sum (measured: m4000's tanks, whose
> 6,581,562.75 L lies where every PETROL + DIESEL rounds to a neighbour).
> Two places are not split
> because what they were is known: the railway's own purchases are all
> DIESEL - it burns nothing else - and the market's price is kept for both.
> 
> NO MONEY MOVES. At 0.7.76 PETROL and DIESEL carry FUEL's band, so a FUEL
> unit is worth what either is: the stock's value, the month's books and the
> market's strike read the same after the load as before it. Nothing here
> touches a cash figure. Since 0.7.78 (batch O2) the two are on the
> wholesale ladder, under FUEL's band: the price is still kept for both, so
> the load still moves nothing, and the month's clearing strikes each in its
> own band.
> 
> WHAT IS CONVERTED, ON NAMES (spec-oil 3):
>   - each market (Markets.State): FUEL's becomes PETROL's (its price; its
>     flow, stock, demand and every month of `taken` x p) and DIESEL's (the
>     price; the rest of each);
>   - each sector's maps keyed "FUEL": stock, pantry, pantryUsed, the
>     ledger's and the struck statement's units and money (unitsSold,
>     unitsBought, sold, bought, each side of a split apart), and the month's
>     carried exported and imported;
>   - the national accounts' goods held (EconomyManager's slots 15 on): a
>     file with FUEL's slot (NationalAccounts.HELD_WITH_FUEL goods) gets
>     NationalAccounts.HELD's, FUEL's units split and the other seven
>     products at a known zero.
> 
> A save of format 34 or later is left alone, and so is anything already in
> the new shape - a map with no FUEL key, a national accounts array of any
> other length - so a later save handed an older format number by a harness
> (SectorStatementCheck 6b) loads as it was written.

**Uses:** [SectorState](SectorState.md) (7), [Markets](Markets.md) (6), [Good](Good.md) (6), [DataSave](DataSave.md) (4), [NationalAccounts](NationalAccounts.md) (3), [Sectors](Sectors.md) (2), [Rail](Rail.md) (1)

**Used by (2):** [Game](Game.md), [OilCheck](OilCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 63 | `FuelSplit.LAST_FUEL_FORMAT` | `33` | The last save format that can carry FUEL: 0.7.75's. |
| 66 | `FuelSplit.FUEL` | `"FUEL"` | FUEL's saved name, which no Good carries any more. |
| 69 | `FuelSplit.HELD_AT` | `15` | Where the national accounts' goods held begin in the saved array (EconomyManager.getNationalAccountsState()). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 60 | 170 | **type** `public final class FuelSplit` | FUEL, split into the PETROL and DIESEL it became: a save written before 0.7.76 converted once, on its load, before anything is restored (batch O1; runs/spec-oil.md 3). |
| 71 | 1 | `private FuelSplit()` |  |
| 74 | 1 | **type** `public record Result(boolean ran, double petrolShare, double driversLitres, double railwayLitres, int figures)` | What a conversion did: whether it ran, the share that became petrol, and how many FUEL figures it split. |
| 80 | 4 | `public static double petrolShare(DataSave s)` | The share of a save's FUEL that becomes PETROL: the drivers' litres over the drivers' and the railway's that month, 1 with neither. |
| 86 | 4 | `static double driversLitres(DataSave s)` | The drivers' litres the month the save was taken: DataSave.householdFuel's third figure, 0 without it. |
| 92 | 9 | `static double railwayLitres(DataSave s)` | ...and the railway's: the tonnes it hauled at FUEL_LITRES_PER_TONNE, 0 without a railway. |
| 110 | 12 | `public static double[] split(double x, double share)` | x split at a share: {petrol, diesel}, the two summing to x to the bit. |
| 124 | 73 | `public static Result convert(DataSave s)` | Converts the save in place, once, if its format can carry FUEL. |
| 204 | 8 | `private static int units(Map<String, Double> m, double share)` | A map of units by good name: its FUEL into PETROL (x share) and DIESEL (the rest), or all into DIESEL at a share of 0 (the railway's); 1 if it held FUEL. |
| 214 | 15 | `private static int money(Map<String, SectorState.SplitState> m, double share)` | ...and a map of money by good name, each side of the split apart. |

