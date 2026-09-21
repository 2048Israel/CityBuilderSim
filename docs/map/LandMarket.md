# LandMarket.java - 680 lines · 24 methods · 17 constants · model

`ham/citybuildersim/LandMarket.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The land office's window: ten plots on offer, and what the next one costs.
> 
> WHY A LISTING RATHER THAN A PRICE
> 
> Buying land used to be a button with a number on it. There was no decision in
> it - the price only went up, so the answer was always "buy now or buy later",
> and the only thing the player could get wrong was timing.
> 
> Ten plots at once is a decision. They are different sizes at different prices,
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

**Uses:** [LandParcel](LandParcel.md) (20), [LandManager](LandManager.md) (3)

**Used by (5):** [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md)

## Sections

| line | section |
|---:|---|
| 56 | · what the city pays |
| 69 | · THE TWO PREMIUMS ADD, THEY DO NOT MULTIPLY |
| 157 | · what businesses pay |
| 209 | · how big a plot is |
| 248 | · the parcels |
| 267 | PRICING |
| 348 | GENERATING A PARCEL |
| 473 | THE LISTING |
| 538 | SAVE AND RESTORE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 54 | `LandMarket.LISTING_SIZE` | `9` | Plots on offer at any one time. |
| 64 | `LandMarket.BASE_PRICE_PER_SQ_FT` | `.0007` | Ground price per square foot before any premium, in thousands. |
| 122 | `LandMarket.PREMIUM_PER_BLOCK_OWNED` | `.008` | Each block already owned makes the next offer this much dearer. |
| 125 | `LandMarket.PREMIUM_PER_1000_PEOPLE` | `.05` | ...and so does each thousand residents. |
| 137 | `LandMarket.IRON_PRICE_PER_TONNE` | `.0004` | What the seller charges for the ore, per tonne in the ground, in thousands. |
| 149 | `LandMarket.SQ_FT_PER_DEPOSIT` | `400_000` | Land a single mine occupies, and therefore the room one deposit needs. |
| 152 | `LandMarket.EXTRA_DEPOSIT_CHANCE` | `.28` | Chance that a parcel with ore has one MORE site, each time it is asked. |
| 155 | `LandMarket.MAX_DEPOSITS` | `4` | However big the tract, this many sites is the most it will ever carry. |
| 192 | `LandMarket.SCARCITY_FLOOR` | `.65` | Multiplier on acquisition cost when land is abundant. |
| 195 | `LandMarket.SCARCITY_CEILING` | `1.90` | Multiplier when there is effectively nothing left. |
| 207 | `LandMarket.SCARCITY_MIDPOINT` | `4.0` | Pressure at which the curve is half way up. |
| 221 | `LandMarket.MIN_BLOCKS` | `1` | The smallest thing the land office will sell, ever: one city block. |
| 236 | `LandMarket.BLOCKS_PER_FLOOR_STEP` | `40` | Blocks the city must already own before the floor rises another block. |
| 243 | `LandMarket.MAX_MIN_BLOCKS` | `15` | A ceiling on the floor. |
| 256 | `LandMarket.SEED` | `705_398_211_733L` | Fixed seed. |
| 549 | `LandMarket.FIELDS_PER_PARCEL` | `5` | Fields written per parcel. |
| 564 | `LandMarket.LISTING_FORMAT_MARKER` | `- FIELDS_PER_PARCEL` | Marks a listing written with deposit counts, and says how wide it is. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 67 | `private double basePricePerSqFt` | The same, in today's money - reformed with every other price. |
| 140 | `private double ironPricePerTonne` | The same, in today's money. |
| 246 | `private double minBlocks` | Smallest parcel currently on offer, in blocks. |
| 258 | `private final List<LandParcel> listing` |  |
| 259 | `private int nextId` |  |
| 262 | `private double marketPricePerSqFt` | Ground price per square foot right now, before any parcel's ore premium. |
| 265 | `private double salePricePerSqFt` | What businesses are charged. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 641 | **type** `public class LandMarket` | The land office's window: ten plots on offer, and what the next one costs. |

### what the city pays (lines 56-68)

### THE TWO PREMIUMS ADD, THEY DO NOT MULTIPLY (lines 69-156)

### what businesses pay (lines 157-208)

### how big a plot is (lines 209-247)

### the parcels (lines 248-266)

### PRICING (lines 267-347)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 21 | `public void update(double ownedSqFt, double allocatedSqFt, int population)` | Re-prices the market and tops the listing back up to ten. |
| 323 | 18 | `public double scarcityMultiplier(double ownedSqFt, double allocatedSqFt)` | How dear inside land is, as a multiple of what the ground cost outside. |
| 343 | 1 | `public double getMarketPricePerSqFt()` | Ground price per square foot the city would pay today, in thousands. |
| 346 | 1 | `public double getSalePricePerSqFt()` | What a business pays the city per square foot, in thousands. |

### GENERATING A PARCEL (lines 348-472)

| line | len | member | says |
|---:|---:|---|---|
| 357 | 17 | `private LandParcel generate(int id)` |  |
| 390 | 12 | `private double rollSize(Random random)` | Plot sizes, as multiples of whatever the current floor is. |
| 415 | 15 | `private int rollDeposits(Random random, double sizeSqFt)` | How many separate deposit sites are under this plot, if any. |
| 439 | 8 | `private double rollTonnes(Random random, int deposits)` | Ore in the ground, in tonnes, pooled across the parcel's deposits. |
| 448 | 3 | `private double round(double sqFt)` |  |
| 466 | 6 | `private static long scramble(long value)` | Spreads consecutive ids into unrelated seeds. |

### THE LISTING (lines 473-537)

| line | len | member | says |
|---:|---:|---|---|
| 478 | 3 | `public List<LandParcel> getListing()` | The plots on offer, in the order they were listed. |
| 482 | 6 | `public LandParcel find(int id)` |  |
| 490 | 9 | `public LandParcel cheapest()` | The cheapest thing on offer, for a caller that just wants some land. |
| 501 | 10 | `public LandParcel bestValue()` | The best value per square foot that carries no ore premium. |
| 513 | 10 | `public LandParcel richestDeposit()` | The listed deposit with the most ore, or null if none is on offer. |
| 530 | 7 | `public LandParcel take(int id)` | Removes a parcel from the window. |

### SAVE AND RESTORE (lines 538-680)

| line | len | member | says |
|---:|---:|---|---|
| 566 | 16 | `public double[] getListingState()` |  |
| 593 | 27 | `public boolean restoreListingState(double[] state)` | Restores a listing written by this build OR by one before deposits existed. |
| 622 | 1 | `public double getMinBlocks()` | Smallest parcel the office is currently willing to sell, in blocks. |
| 637 | 3 | `public double[] getPriceState()` | THE OFFICE'S PRICES ARE STATE, and the listing above did not carry them. |
| 641 | 6 | `public void restorePriceState(double[] state)` |  |
| 648 | 7 | `public void reset()` |  |
| 665 | 7 | `public void redenominate(double scale)` | Land prices in the new unit, and a fresh board at the land office. |
| 675 | 4 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

