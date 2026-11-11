# VaultMoneysCheck.java - 1,075 lines · 26 methods · 6 constants · harnesses

`ham/citybuildersim/VaultMoneysCheck.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> The vault in eight moneys (batch WN4, slice C; spec-nations 5.2-5.4): ForeignAccounts keeps a balance in each of the eight the IMF's
> COFER names, takes a dollar in by the reserve mix and gives one up from the largest holding, revalues across the moneys when the
> eight move against each other, and carries all of it in its save - while a city that never touches the mix plays as it did. And, from
> GD2 (spec-gold-money 4.2), gold the ninth: XAU, at the end of the vault's moneys, which only the player's choice puts anything in.
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
>   8. GOLD, THE NINTH (GD2): a purchase into "XAU" at a handed price holds its US$ over the price, getHoldingUsd is that times the price and
>      getReservesUsd includes it; WORLD_MIX (COFER counts no gold) and MATCH_TRADE (no nation trades in it) put nothing in it, nor does the
>      default mix; CHOSEN with a gold share puts that share in it; with no gold price this month a purchase into "XAU" goes to the US$.
>   9. GOLD EARNS NOTHING: earnVaultInterest with a finite rate at every index, gold's included, leaves its balance as it was, and the
>      yield is struck on the balances that earned, gold left out.
>  10. GOLD IS REVALUED: a new price handed moves getLastVaultCrossUsd() by ounces x (new - old), and the driver is "XAU" when gold moved
>      most (the euro when it moved more); a named sale of gold comes from gold alone and clamps at what gold holds; an unnamed sale
>      takes the largest holding, gold when it is.
>  11. THE SAVE, WITH GOLD: restore(toSaveArray()) gives back gold's balance, price and chosen share to the bit (slots 149-151); an array
>      cut to 149 (a save from before GD2) loads with no gold and no gold rate, the eight's balances, rates and chosen shares as they
>      were; gold cannot anchor a peg (setPegAnchor(GOLD) is refused; a saved slot 148 of 8 reads the US$).
>  12. GOLD IN A CITY (the full file only): after a month the vault's gold price is GoldPrice's for the month the moneys stand in, with a
>      rate; a default city holds no gold through its months; the debt offers no paper in "XAU"; a named purchase of gold holds ounces.
> 
> Every fixture causes its condition: a settled currency on a peg that the hold has to sell for (the peg revalued under the rate it
> stands at, so the lift is a sale: the defence sells) or buy for (devalued, or a surplus: the edge buys), holdings built by named
> purchases at known rates, and rates handed by hand. XR5 (THE HOLD PAYS THE PRESSURE, 2026-11-06): until it the peg met the month's
> balance, so a deficit month sold and a surplus month bought and a peg moved to 1.6 on balanced trade dealt nothing; now the hold
> deals the lift the peg and the shadow ask, so the fixtures move the peg to cause the sale or the purchase they stand on.

**Uses:** [ForeignAccounts](ForeignAccounts.md) (126), [CurrencyRegimeCheck](CurrencyRegimeCheck.md) (15), [WorldMoneys](WorldMoneys.md) (13), [WorldData](WorldData.md) (11), [WorldMoneysCheck](WorldMoneysCheck.md) (7), [Game](Game.md) (5), [GoldPrice](GoldPrice.md) (4), [Nations](Nations.md) (2), [CurrencyCheck](CurrencyCheck.md) (1), [PartnerTrade](PartnerTrade.md) (1)

## Sections

| line | section |
|---:|---|
| 164 | 1. ALL US$ IS THE OLD VAULT |
| 271 | 2. A BUY GOES IN BY THE MIX |
| 396 | 3. A SALE COMES FROM THE LARGEST |
| 498 | 4. THE CROSS REVALUATION |
| 572 | 5. MATCH_TRADE'S MAPPING - on the full file |
| 629 | 6. THE SAVE |
| 736 | 7. IN A CITY |
| 799 | 8. GOLD, THE NINTH |
| 872 | 9. GOLD EARNS NOTHING |
| 906 | 10. GOLD IS REVALUED |
| 960 | 11. THE SAVE, WITH GOLD |
| 1034 | 12. GOLD IN A CITY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `VaultMoneysCheck.out` | `System.out` |  |
| 64 | `VaultMoneysCheck.R` | `{ 1, 1.10, 0.0070, 1.27, 0.14, 0.73, 0.65, 1.12 }` | The cross rates the sections hand: US$ per unit of the US$, euro, yen, pound, yuan, Canadian and Australian dollar and franc. |
| 67 | `VaultMoneysCheck.SLOTS` | `156` | The length of the foreign accounts' array: slots 0 to 141 and, after them, what the vault earned (142-146, XR4; InterestAbroadCheck's section 7 asserts them), the hold's support (147, XR5; CurrencyRegimeCheck 10), the... |
| 70 | `VaultMoneysCheck.GOLD` | `ForeignAccounts.GOLD` | Gold's index in the vault (ForeignAccounts.GOLD), and the two prices the gold sections hand it, US$ an ounce: the 1900 statute's par and Proclamation 2072's (GoldPrice). |
| 71 | `VaultMoneysCheck.OLD_PRICE` | `GoldPrice.PAR_1900, NEW_PRICE = GoldPrice.PAR_1934` |  |
| 73 | `VaultMoneysCheck.ALL_USD` | `ForeignAccounts.Mix.ALL_USD, MATCH_TRADE = ForeignAccounts.Mix.MATCH_TRADE, W...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 60 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 1018 | **type** `public class VaultMoneysCheck` | The vault in eight moneys (batch WN4, slice C; spec-nations 5.2-5.4): ForeignAccounts keeps a balance in each of the eight the IMF's COFER names, takes a dollar in by the reserve mix and gives one up from the largest ... |
| 76 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 82 | 5 | `static void same(String label, double actual, double expected)` | Equal to the bit (and so a -0.0 is not a 0.0). |
| 89 | 5 | `static void near(String label, double actual, double expected, double tol)` | Equal within a share of the expected (or an absolute 1e-12 about zero). |
| 95 | 1 | `static boolean sameArray(double[] a, double[] b)` |  |
| 98 | 5 | `static double beyondTheDollar(ForeignAccounts fx)` | What the vault holds beyond the US$, in each money's own units, added up (gold's too): 0 only if it holds nothing but dollars. |
| 105 | 5 | `static double coferTotal()` | The sum of the COFER dial, which the mix normalises by. |
| 112 | 5 | `static double[] allUsd()` | The shares of the default mix: all in the US$, the vault's nine long. |
| 119 | 5 | `static double[] withGold(double usdAnOunce)` | The cross rates R with gold's price an ounce as the ninth entry. |
| 126 | 6 | `static ForeignAccounts ratedWithGold()` | An empty vault, a D$ a US$, handed the eight's rates and gold at the 1900 price. |
| 134 | 3 | `static ForeignAccounts peg()` | A settled currency on a peg at its own rate, no vault, no seed (so no doubt can break it): the fixture a month's deficit sells from and a month's surplus buys into. |
| 139 | 5 | `static void pegMonth(ForeignAccounts fx, double exportShare)` | One month of the peg fixture's trade at this share of its imports, repriced; the deficit or surplus dealt is the vault's. |
| 145 | 18 | `public static void main(String[] args) throws Exception` |  |

### 1. ALL US$ IS THE OLD VAULT (lines 164-270)

| line | len | member | says |
|---:|---:|---|---|
| 168 | 102 | `static void allUsdIsTheOldVault()` |  |

### 2. A BUY GOES IN BY THE MIX (lines 271-395)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 6 | `static ForeignAccounts rated()` |  |
| 282 | 113 | `static void aBuyGoesInByTheMix()` |  |

### 3. A SALE COMES FROM THE LARGEST (lines 396-497)

| line | len | member | says |
|---:|---:|---|---|
| 401 | 7 | `static ForeignAccounts three()` | A vault of 50,000, 30,000 and 20,000 US$ worth in euros, pounds and Canadian dollars, bought at a rate of 1 so local money and US$ are one number. |
| 409 | 88 | `static void aSaleComesFromTheLargest()` |  |

### 4. THE CROSS REVALUATION (lines 498-571)

| line | len | member | says |
|---:|---:|---|---|
| 502 | 69 | `static void theCrossRevaluation()` |  |

### 5. MATCH_TRADE'S MAPPING - on the full file (lines 572-628)

| line | len | member | says |
|---:|---:|---|---|
| 576 | 52 | `static void theMapping() throws Exception` |  |

### 6. THE SAVE (lines 629-735)

| line | len | member | says |
|---:|---:|---|---|
| 633 | 102 | `static void theSave()` |  |

### 7. IN A CITY (lines 736-798)

| line | len | member | says |
|---:|---:|---|---|
| 740 | 58 | `static void inACity()` |  |

### 8. GOLD, THE NINTH (lines 799-871)

| line | len | member | says |
|---:|---:|---|---|
| 803 | 68 | `static void goldTheNinth()` |  |

### 9. GOLD EARNS NOTHING (lines 872-905)

| line | len | member | says |
|---:|---:|---|---|
| 876 | 29 | `static void goldEarnsNothing()` |  |

### 10. GOLD IS REVALUED (lines 906-959)

| line | len | member | says |
|---:|---:|---|---|
| 910 | 49 | `static void goldIsRevalued()` |  |

### 11. THE SAVE, WITH GOLD (lines 960-1033)

| line | len | member | says |
|---:|---:|---|---|
| 964 | 69 | `static void theSaveWithGold()` |  |

### 12. GOLD IN A CITY (lines 1034-1075)

| line | len | member | says |
|---:|---:|---|---|
| 1038 | 37 | `static void goldInACity()` |  |

