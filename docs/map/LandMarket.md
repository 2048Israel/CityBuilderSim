# LandMarket.java - 803 lines · 26 methods · 18 constants · model

`ham/citybuildersim/LandMarket.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The land office's window: nine plots on offer, and what the next one costs.
> 
> WHY A LISTING RATHER THAN A PRICE
> 
> Buying land used to be a button with a number on it. There was no decision in
> it - the price only went up, so the answer was always "buy now or buy later",
> and the only thing the player could get wrong was timing.
> 
> Nine plots at once is a decision. They are different sizes at different prices,
> and roughly one in five has iron under it, which is worth far more than the
> ground but costs more up front. Buying the cheap one, buying the big one and
> buying the one with the ore are three different plays.
> 
> TWO PRICES, MOVED BY DIFFERENT THINGS
> 
> What the CITY pays for a new parcel is a function of the city's size - a
> bigger city annexes further out and is negotiating from a weaker position, so
> every block owned and every thousand residents makes the next offer slightly
> dearer. It does not care whether the city is full.
> 
> What BUSINESSES pay the city per square foot is the opposite: pure supply
> against demand inside the city limits. A city with empty blocks sells cheap; a
> city with nothing spare sells dear. The player no longer sets this by hand -
> it is a market now, and the way to make land cheap is to go and buy some.
> 
> WHAT IS LISTED STAYS LISTED
> 
> A parcel's price is fixed the moment it appears and never moves, so a player
> can save up for the expensive one without it drifting away. Each parcel is
> generated from its own id, so reloading a save cannot reroll the offers into
> something better either.
> 
> WHAT THE CITY PAYS IS US DOLLARS (0.7.6)
> 
> Jerus: "when you buy land, make it so that it costs USD not domestic
> currency". The world sells the city its ground, as it always implicitly
> did, and is paid in its own money: the base and the premiums are dollar
> figures now, a parcel's DOLLAR price is what is frozen at listing, and
> what the treasury pays is that times the rate on the day it buys
> (LandParcel.localPrice(), Game.buyLandParcel()). At the founding rate of
> 1.00 every number is what it was. What BUSINESSES pay the city stays local
> money and is struck exactly as before (basePricePerSqFt), and a currency
> reform no longer touches the listing (redenominate()).

**Uses:** [LandParcel](LandParcel.md) (22), [LandManager](LandManager.md) (3), [ForeignAccounts](ForeignAccounts.md) (1)

**Used by (5):** [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md)

## Sections

| line | section |
|---:|---|
| 68 | · what the city pays |
| 95 | · THE TWO PREMIUMS ADD, THEY DO NOT MULTIPLY |
| 183 | · what businesses pay |
| 235 | · how big a plot is |
| 274 | · the parcels |
| 304 | PRICING |
| 396 | GENERATING A PARCEL |
| 522 | THE LISTING |
| 587 | SAVE AND RESTORE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 66 | `LandMarket.LISTING_SIZE` | `9` | Plots on offer at any one time. |
| 80 | `LandMarket.BASE_PRICE_PER_SQ_FT` | `.0007` | Ground price per square foot before any premium, in thousands of US dollars. |
| 148 | `LandMarket.PREMIUM_PER_BLOCK_OWNED` | `.008` | Each block already owned makes the next offer this much dearer. |
| 151 | `LandMarket.PREMIUM_PER_1000_PEOPLE` | `.05` | ...and so does each thousand residents. |
| 166 | `LandMarket.IRON_PRICE_PER_TONNE` | `.0004` | What the seller charges for the ore, per tonne in the ground, in thousands of US dollars. |
| 175 | `LandMarket.SQ_FT_PER_DEPOSIT` | `400_000` | Land a single mine occupies, and therefore the room one deposit needs. |
| 178 | `LandMarket.EXTRA_DEPOSIT_CHANCE` | `.28` | Chance that a parcel with ore has one MORE site, each time it is asked. |
| 181 | `LandMarket.MAX_DEPOSITS` | `4` | However big the tract, this many sites is the most it will ever carry. |
| 218 | `LandMarket.SCARCITY_FLOOR` | `.65` | Multiplier on acquisition cost when land is abundant. |
| 221 | `LandMarket.SCARCITY_CEILING` | `1.90` | Multiplier when there is effectively nothing left. |
| 233 | `LandMarket.SCARCITY_MIDPOINT` | `4.0` | Pressure at which the curve is half way up. |
| 247 | `LandMarket.MIN_BLOCKS` | `1` | The smallest thing the land office will sell, ever: one city block. |
| 262 | `LandMarket.BLOCKS_PER_FLOOR_STEP` | `40` | Blocks the city must already own before the floor rises another block. |
| 269 | `LandMarket.MAX_MIN_BLOCKS` | `15` | A ceiling on the floor. |
| 282 | `LandMarket.SEED` | `705_398_211_733L` | Fixed seed. |
| 598 | `LandMarket.FIELDS_PER_PARCEL` | `5` | Fields written per parcel. |
| 614 | `LandMarket.LISTING_FORMAT_MARKER` | `- FIELDS_PER_PARCEL` | Marks a listing written with deposit counts, and says how wide it is - in LOCAL money, as every listing was until 0.7.6 (USD_LISTING_MARKER). |
| 622 | `LandMarket.USD_LISTING_MARKER` | `- 100 - FIELDS_PER_PARCEL` | Marks a listing whose prices are US DOLLARS (0.7.6), as wide as the one before it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 93 | `private double basePricePerSqFt` | The same base in LOCAL money, reformed with every other price - what the inside price is struck from, and nothing else. |
| 272 | `private double minBlocks` | Smallest parcel currently on offer, in blocks. |
| 284 | `private final List<LandParcel> listing` |  |
| 285 | `private int nextId` |  |
| 292 | `private double marketPricePerSqFt` | Ground price per square foot right now, before any parcel's ore premium, in LOCAL money at the founding rate - the anchor the inside price is struck from (basePricePerSqFt), and since 0.7.6 nothing else. |
| 299 | `private double groundUsdPerSqFt` | ...and what the world asks for the same ground, in thousands of US dollars (0.7.6): every parcel listed from now on is priced off this, and it is the figure the history keeps as landPrice. |
| 302 | `private double salePricePerSqFt` | What businesses are charged. |
| 629 | `private final java.util.Set<Integer> localIds` | Ids restored from an older listing whose prices are still LOCAL money, waiting for settleLocalPrices() to read them as dollars at the loading rate - which the load path has only once the foreign accounts are back. |
| 632 | `private boolean groundFromLocal` | True when the price state came back without a dollar ground price (an older save). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 52 | 752 | **type** `public class LandMarket` | The land office's window: nine plots on offer, and what the next one costs. |

### what the city pays (lines 68-94)

### THE TWO PREMIUMS ADD, THEY DO NOT MULTIPLY (lines 95-182)

### what businesses pay (lines 183-234)

### how big a plot is (lines 235-273)

### the parcels (lines 274-303)

### PRICING (lines 304-395)

| line | len | member | says |
|---:|---:|---|---|
| 318 | 25 | `public void update(double ownedSqFt, double allocatedSqFt, int population)` | Re-prices the market and tops the listing back up to ten. |
| 364 | 18 | `public double scarcityMultiplier(double ownedSqFt, double allocatedSqFt)` | How dear inside land is, as a multiple of what the ground cost outside. |
| 388 | 1 | `public double getMarketPricePerSqFt()` | The inside price's anchor: the ground price per square foot in local money at the founding rate, in thousands. |
| 391 | 1 | `public double getGroundUsdPerSqFt()` | Ground price per square foot the world asks today, in thousands of US dollars (0.7.6). |
| 394 | 1 | `public double getSalePricePerSqFt()` | What a business pays the city per square foot, in thousands. |

### GENERATING A PARCEL (lines 396-521)

| line | len | member | says |
|---:|---:|---|---|
| 406 | 17 | `private LandParcel generate(int id)` |  |
| 439 | 12 | `private double rollSize(Random random)` | Plot sizes, as multiples of whatever the current floor is. |
| 464 | 15 | `private int rollDeposits(Random random, double sizeSqFt)` | How many separate deposit sites are under this plot, if any. |
| 488 | 8 | `private double rollTonnes(Random random, int deposits)` | Ore in the ground, in tonnes, pooled across the parcel's deposits. |
| 497 | 3 | `private double round(double sqFt)` |  |
| 515 | 6 | `private static long scramble(long value)` | Spreads consecutive ids into unrelated seeds. |

### THE LISTING (lines 522-586)

| line | len | member | says |
|---:|---:|---|---|
| 527 | 3 | `public List<LandParcel> getListing()` | The plots on offer, in the order they were listed. |
| 531 | 6 | `public LandParcel find(int id)` |  |
| 539 | 9 | `public LandParcel cheapest()` | The cheapest thing on offer, for a caller that just wants some land. |
| 550 | 10 | `public LandParcel bestValue()` | The best value per square foot that carries no ore premium. |
| 562 | 10 | `public LandParcel richestDeposit()` | The listed deposit with the most ore, or null if none is on offer. |
| 579 | 7 | `public LandParcel take(int id)` | Removes a parcel from the window. |

### SAVE AND RESTORE (lines 587-803)

| line | len | member | says |
|---:|---:|---|---|
| 634 | 16 | `public double[] getListingState()` |  |
| 667 | 30 | `public boolean restoreListingState(double[] state)` | Restores a listing written by this build OR by one before deposits existed. |
| 710 | 15 | `public int settleLocalPrices(double rate)` | AN OLDER SAVE'S PRICES WERE LOCAL MONEY, and this reads them as US dollars at the rate of the day the save is loaded (0.7.6): each listed parcel's price over the rate, so the local cost the player saw is exactly what ... |
| 727 | 1 | `public double getMinBlocks()` | Smallest parcel the office is currently willing to sell, in blocks. |
| 746 | 3 | `public double[] getPriceState()` | THE OFFICE'S PRICES ARE STATE, and the listing above did not carry them. |
| 750 | 12 | `public void restorePriceState(double[] state)` |  |
| 763 | 10 | `public void reset()` |  |
| 788 | 5 | `public void redenominate(double scale)` | The office's LOCAL prices in the new unit - the inside price and the anchor it is struck from - and nothing else. |
| 799 | 3 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

