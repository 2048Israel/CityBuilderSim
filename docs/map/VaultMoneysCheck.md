# VaultMoneysCheck.java - 741 lines · 18 methods · 4 constants · harnesses

`ham/citybuildersim/VaultMoneysCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The vault in eight moneys (batch WN4, slice C; spec-nations 5.2-5.4): ForeignAccounts keeps a balance in each of the eight the IMF's
> COFER names, takes a dollar in by the reserve mix and gives one up from the largest holding, revalues across the moneys when the
> eight move against each other, and carries all of it in its save - while a city that never touches the mix plays as it did.
> 
> WHY. The vault was one figure, reservesUsd, changed in seven places, each with its own clamp (the defence empties it by one expression,
> the treasury's sale by another, the land office by a third). Splitting it into eight balances could silently change any of them for the
> default city, move which money a sale takes from, book a purchase's own money as the vault's gain, or lose a balance in the save. Each
> section says against which constant or which old expression it is judged.
> 
> What this has to prove:
>   1. ALL US$ IS THE OLD VAULT: a ForeignAccounts on the default mix, driven through a treasury buy, a defence sale, an edge buy, a
>      treasury sale, a land payment from the vault, a revaluation and a sale of everything, holds only US$ and its getReservesUsd() is, to
>      the bit and at every step, what the old field's own expression at that site gives (written here from the old source).
>   2. A BUY GOES IN BY THE MIX: under WORLD_MIX a purchase puts COFER_SHARES' share of its US$ into each money in its own units; under
>      CHOSEN the chosen shares; under MATCH_TRADE the trade shares handed; a money with no rate takes nothing and the US$ takes its share;
>      the strong edge's and a peg's buys go in by the mix; the founders' dollars (bought into "USD") are dollars; a named purchase goes
>      into the named money only.
>   3. A SALE COMES FROM THE LARGEST: 50/30/20 in three moneys, a sale of 40 takes 40 from the largest, a sale of 60 all 50 and 10 from the
>      next; the defence and the land office likewise; a named sale comes from the named money and no other, clamped at what it holds.
>   4. THE CROSS REVALUATION: a vault of 100 euros at 1.10 US$ the euro moved to 1.00 has a cross move of -10, the euro the driver at
>      -9.1%; revalueVault() books reservesUsd x (rate - last rate) + cross x last rate, which is V1 r1 - (V1 - cross) r0; a purchase in
>      the month adds nothing to the cross move; the next month's hand starts a new one.
>   5. MATCH_TRADE'S MAPPING, on the full file: a nation on the CFA franc maps to the euro, a Gulf state to the US$, Canada to the Canadian
>      dollar, Mexico to the US$; the shares sum to 1.
>   6. THE SAVE: a vault in four moneys on a CHOSEN mix with a cross move, saved and restored, holds the same to the bit and revalues the
>      same next month; a save with only slot 22 reads as all US$.
>   7. IN A CITY (the full file only): the first month hands the eight their rates (US$ per unit is 1 / the moneys' rate) and a city that
>      never touches the mix holds only US$ with no cross move; a purchase of euros and one by the world's mix put balances where they
>      say; and the month's revaluation in local money is the vault's dollars at the rate's move plus the cross move at the last rate.
> 
> Every fixture causes its condition: a settled currency on a peg whose month is a deficit (the defence sells) or a surplus (the edge
> buys), holdings built by named purchases at known rates, and rates handed by hand.

**Uses:** [ForeignAccounts](ForeignAccounts.md) (77), [CurrencyRegimeCheck](CurrencyRegimeCheck.md) (15), [WorldData](WorldData.md) (8), [WorldMoneysCheck](WorldMoneysCheck.md) (5), [WorldMoneys](WorldMoneys.md) (4), [Game](Game.md) (4), [Nations](Nations.md) (2), [CurrencyCheck](CurrencyCheck.md) (1), [PartnerTrade](PartnerTrade.md) (1)

## Sections

| line | section |
|---:|---|
| 116 | 1. ALL US$ IS THE OLD VAULT |
| 215 | 2. A BUY GOES IN BY THE MIX |
| 340 | 3. A SALE COMES FROM THE LARGEST |
| 441 | 4. THE CROSS REVALUATION |
| 515 | 5. MATCH_TRADE'S MAPPING - on the full file |
| 572 | 6. THE SAVE |
| 679 | 7. IN A CITY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 44 | `VaultMoneysCheck.out` | `System.out` |  |
| 47 | `VaultMoneysCheck.R` | `{ 1, 1.10, 0.0070, 1.27, 0.14, 0.73, 0.65, 1.12 }` | The cross rates the sections hand: US$ per unit of the US$, euro, yen, pound, yuan, Canadian and Australian dollar and franc. |
| 50 | `VaultMoneysCheck.SLOTS` | `142` | The length of the foreign accounts' array: slots 0 to 141. |
| 52 | `VaultMoneysCheck.ALL_USD` | `ForeignAccounts.Mix.ALL_USD, MATCH_TRADE = ForeignAccounts.Mix.MATCH_TRADE, W...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 701 | **type** `public class VaultMoneysCheck` | The vault in eight moneys (batch WN4, slice C; spec-nations 5.2-5.4): ForeignAccounts keeps a balance in each of the eight the IMF's COFER names, takes a dollar in by the reserve mix and gives one up from the largest ... |
| 55 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 61 | 5 | `static void same(String label, double actual, double expected)` | Equal to the bit (and so a -0.0 is not a 0.0). |
| 68 | 5 | `static void near(String label, double actual, double expected, double tol)` | Equal within a share of the expected (or an absolute 1e-12 about zero). |
| 74 | 1 | `static boolean sameArray(double[] a, double[] b)` |  |
| 77 | 5 | `static double beyondTheDollar(ForeignAccounts fx)` | What the vault holds beyond the US$, in the eight's own units, added up: 0 only if it holds nothing but dollars. |
| 84 | 5 | `static double coferTotal()` | The sum of the COFER dial, which the mix normalises by. |
| 91 | 3 | `static ForeignAccounts peg()` | A settled currency on a peg at its own rate, no vault, no seed (so no doubt can break it): the fixture a month's deficit sells from and a month's surplus buys into. |
| 96 | 5 | `static void pegMonth(ForeignAccounts fx, double exportShare)` | One month of the peg fixture's trade at this share of its imports, repriced; the deficit or surplus dealt is the vault's. |
| 102 | 13 | `public static void main(String[] args) throws Exception` |  |

### 1. ALL US$ IS THE OLD VAULT (lines 116-214)

| line | len | member | says |
|---:|---:|---|---|
| 120 | 94 | `static void allUsdIsTheOldVault()` |  |

### 2. A BUY GOES IN BY THE MIX (lines 215-339)

| line | len | member | says |
|---:|---:|---|---|
| 219 | 6 | `static ForeignAccounts rated()` |  |
| 226 | 113 | `static void aBuyGoesInByTheMix()` |  |

### 3. A SALE COMES FROM THE LARGEST (lines 340-440)

| line | len | member | says |
|---:|---:|---|---|
| 345 | 7 | `static ForeignAccounts three()` | A vault of 50,000, 30,000 and 20,000 US$ worth in euros, pounds and Canadian dollars, bought at a rate of 1 so local money and US$ are one number. |
| 353 | 87 | `static void aSaleComesFromTheLargest()` |  |

### 4. THE CROSS REVALUATION (lines 441-514)

| line | len | member | says |
|---:|---:|---|---|
| 445 | 69 | `static void theCrossRevaluation()` |  |

### 5. MATCH_TRADE'S MAPPING - on the full file (lines 515-571)

| line | len | member | says |
|---:|---:|---|---|
| 519 | 52 | `static void theMapping() throws Exception` |  |

### 6. THE SAVE (lines 572-678)

| line | len | member | says |
|---:|---:|---|---|
| 576 | 102 | `static void theSave()` |  |

### 7. IN A CITY (lines 679-741)

| line | len | member | says |
|---:|---:|---|---|
| 683 | 58 | `static void inACity()` |  |

