# LandParcel.java - 111 lines · 11 methods · 0 constants · model

`ham/citybuildersim/LandParcel.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> One plot on the market, as the land office lists it.
> 
> WHAT IT IS FOR
> 
> Land used to be one fungible number bought a block at a time at a price that
> rose 2% per block. That is a slider, not a market: there was never a decision
> to make beyond "yes" or "later".
> 
> A listing is a decision. Ten plots are on offer at once, all different sizes,
> all differently priced, and some of them have iron under them. Buying the
> cheap one and buying the one with the ore are different moves, and the player
> has to weigh them against a treasury.
> 
> WHAT IT IS NOT
> 
> Not a location. Nothing here has coordinates and no building sits on a
> specific parcel - the square footage joins the city's pool on purchase,
> exactly as an annexed block used to. This is a market, not a map.
> 
> A parcel is IMMUTABLE once listed. What is listed stays listed at the price
> it was listed at: the city can save up for the big one without it drifting
> out of reach, and a reload cannot reroll the offers into something better.

**Uses:** [LandManager](LandManager.md) (1)

**Used by (9):** [Game](Game.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMarket](LandMarket.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MiningCheck](MiningCheck.md), [MoneyCheck](MoneyCheck.md), [TreasuryCheck](TreasuryCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 29 | `private final int id` |  |
| 30 | `private final double sizeSqFt` |  |
| 33 | `private final double price` | What the city pays, in thousands. |
| 42 | `private final double ironTonnes` | Iron ore in the ground, in tonnes, across all of this parcel's deposits. |
| 55 | `private final int deposits` | How many separate deposit sites are on this plot. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 27 | 85 | **type** `public final class LandParcel` | One plot on the market, as the land office lists it. |
| 57 | 8 | `public LandParcel(int id, double sizeSqFt, double price, double ironTonnes, int deposits)` |  |
| 72 | 3 | `public LandParcel(int id, double sizeSqFt, double price, double ironTonnes)` | Pre-multi-deposit parcels: any ore at all meant exactly one site. |
| 76 | 1 | `public int getId()` |  |
| 77 | 1 | `public double getSizeSqFt()` |  |
| 78 | 1 | `public double getPrice()` |  |
| 79 | 1 | `public double getIronTonnes()` |  |
| 80 | 1 | `public int getDeposits()` |  |
| 82 | 1 | `public boolean hasIron()` |  |
| 85 | 3 | `public double getPricePerSqFt()` | For comparing offers, which is the whole point of listing ten at once. |
| 90 | 3 | `public double getBlocks()` | How many city blocks' worth, for a player who thinks in blocks. |
| 100 | 11 | `public String describe()` | A one-line label for the listing. |

