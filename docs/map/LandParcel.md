# LandParcel.java - 132 lines · 12 methods · 0 constants · model

`ham/citybuildersim/LandParcel.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> One plot on the market, as the land office lists it.
> 
> WHAT IT IS FOR
> 
> Land used to be one fungible number bought a block at a time at a price that
> rose 2% per block. That is a slider, not a market: there was never a decision
> to make beyond "yes" or "later".
> 
> A listing is a decision. Nine plots are on offer at once, all different sizes,
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
> 
> PRICED IN US DOLLARS (0.7.6). Jerus: "when you buy land, make it so that it
> costs USD not domestic currency". The seller is the world, as it always
> implicitly was, and the world is paid in its own money: the dollar price is
> what is fixed at listing, and what the treasury pays in local money is
> that price times the rate on the day it buys (localPrice()) - so a weak
> currency makes the same plot dear and a strong one cheap.

**Uses:** [LandManager](LandManager.md) (1)

**Used by (9):** [Game](Game.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMarket](LandMarket.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MiningCheck](MiningCheck.md), [MoneyCheck](MoneyCheck.md), [TreasuryCheck](TreasuryCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 36 | `private final int id` |  |
| 37 | `private final double sizeSqFt` |  |
| 43 | `private final double priceUsd` | What the city pays, in thousands of US DOLLARS since 0.7.6. |
| 52 | `private final double ironTonnes` | Iron ore in the ground, in tonnes, across all of this parcel's deposits. |
| 65 | `private final int deposits` | How many separate deposit sites are on this plot. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 34 | 99 | **type** `public final class LandParcel` | One plot on the market, as the land office lists it. |
| 67 | 8 | `public LandParcel(int id, double sizeSqFt, double priceUsd, double ironTonnes, int deposits)` |  |
| 82 | 3 | `public LandParcel(int id, double sizeSqFt, double priceUsd, double ironTonnes)` | Pre-multi-deposit parcels: any ore at all meant exactly one site. |
| 86 | 1 | `public int getId()` |  |
| 87 | 1 | `public double getSizeSqFt()` |  |
| 89 | 1 | `public double getPriceUsd()` | The listed price, in thousands of US dollars. |
| 90 | 1 | `public double getIronTonnes()` |  |
| 91 | 1 | `public int getDeposits()` |  |
| 93 | 1 | `public boolean hasIron()` |  |
| 99 | 1 | `public double localPrice(double rate)` | What the treasury pays for it in local money at this rate - local money per US dollar, ForeignAccounts.getRate() on the day it buys. |
| 106 | 3 | `public double getUsdPerSqFt()` | For comparing offers, which is the whole point of listing nine at once - in US dollars, like the price; every parcel on the shelf is converted at the same rate, so the ranking is the same in either money. |
| 111 | 3 | `public double getBlocks()` | How many city blocks' worth, for a player who thinks in blocks. |
| 121 | 11 | `public String describe()` | A one-line label for the listing. |

