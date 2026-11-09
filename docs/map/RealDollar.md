# RealDollar.java - 137 lines · 9 methods · 1 constants · model

`ham/citybuildersim/RealDollar.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> The real US dollar, constant 2015 dollars, and what the city's money is against it (WN4, D29): the D$ per real US$, the city's real GDP in it, and
> the real effective rate (and, since WN6, the chain that carries a partner's rate across a change of its money). Pure functions of their inputs, and
> nothing in the model reads any of them: they answer "is my money dear?", which the gauge against a parity made of rent answered after a fashion
> (docs/studies/spec-exchange-rates.md 3.9; WN6 shows them).
> 
> WHY THIS EXISTS. Jerus, 2026-10-21: "lets keep something, the real usd dollar, aka two us dollars, one is the in game us dollar which inflates over
> time, and the other is the real us dollar, and that one is what real gdp is shown in, as well as an exchange rate with it, purely just for
> comparision purposes"; the base year "2015, cause later the start date will move back anyways". The in-game US$ is the US's money and inflates on
> its path (WorldEconomy, THE US DOLLAR ON THE US'S PATH), so a figure in it moves with its prices and says nothing across the years; a real US$ is
> what the World Bank's gdp_2015usd is in, and what the nations' output is already in.
> 
> THE BRIDGE (the leaderboard's D1, Jerus 2026-10-21: "2015 so that its calibrated to the other"): a founding game dollar is a US dollar of the
> calendar's first year, and BRIDGE of them is a real 2015 US dollar's worth: the US's price level in 2015 over its level in that year, from the same
> series the founding prices are struck from (PriceLevel; 28.4 for the 1900 start, EP1; it was 1 while the calendar began in 2000 and a founding
> dollar counted as a 2015 one). The real US$ is the in-game US$ deflated by its own level since founding and then bridged; the game's prices stay
> as struck from 2025's (PriceLevel.FACTOR).
> 
> Every figure is in the city's money of today, so a currency reform (Denomination) changes nothing: the caller hands the founding's rate in today's
> units (Denomination.money(ForeignAccounts.OPENING_RATE)), and the D$ per real US$ is read in today's D$.

**Uses:** [Nations](Nations.md) (7), [PriceLevel](PriceLevel.md) (2), [Rankings](Rankings.md) (1), [CityCalendar](CityCalendar.md) (1), [WorldMoneys](WorldMoneys.md) (1)

**Used by (6):** [Game](Game.md), [PriceLevelCheck](PriceLevelCheck.md), [Rankings](Rankings.md), [RankingsCheck](RankingsCheck.md), [RealDollarCheck](RealDollarCheck.md), [TradeScreen](TradeScreen.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 35 | `RealDollar.BRIDGE` | `PriceLevel.usCpi(Rankings.BASE_YEAR) / PriceLevel.usCpi(CityCalendar.EPOCH_YEAR)` | Real 2015 US dollars in a founding US dollar (EP1, study call 12): the US CPI-U of Rankings.BASE_YEAR over that of the calendar's first year, the one place the pivot to 2015 is struck. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 26 | 112 | **type** `public final class RealDollar` | The real US dollar, constant 2015 dollars, and what the city's money is against it (WN4, D29): the D$ per real US$, the city's real GDP in it, and the real effective rate (and, since WN6, the chain that carries a part... |
| 28 | 1 | `private RealDollar()` |  |
| 42 | 5 | **type** `public record Partner(double weight, double moneyPerUsdNow, double moneyPerUsdAtFounding, double pricesSinc...` | What the city's money is worth against a partner, for the real effective rate: the weight it trades with (any positive scale: the weights are shares of their sum), the partner's money per US$ now and in the founding's... |
| 43 | 3 | `boolean usable()` _(in RealDollar.Partner)_ |  |
| 52 | 1 | **type** `public record Weighed(int nation, double share, Partner partner)` | One partner the real effective rate weighs, as the screen names it (WN5b): the nation's number, its share of the usable partners' weights (the shares of a list add to 1) and the Partner the rate is made of. |
| 54 | 1 | `private static boolean positive(double v)` |  |
| 57 | 3 | `public static List<Partner> usAlone(double usPricesSinceFounding)` | The US alone, as the one partner (w = 1): its money is the unit, so e and e0 are 1, and its prices are the in-game US$'s level since founding. |
| 66 | 3 | `public static double dollarsPer2015Usd(double ratePerUsd, double usPricesSinceFounding)` | D$ per real 2015 US$: the rate (D$ per US$) times the US$'s price level since founding, over the bridge. |
| 75 | 3 | `public static double realGdp2015Usd(double gdp, double priceIndex, double foundingRatePerUsd)` | The city's GDP in real 2015 US$, the World Bank's way: the GDP over the city's price index (YearBook.realGdp's division, which carries it back to the D$ of founding, and is the same through a currency reform) over the... |
| 93 | 10 | `public static double moneyChain(Nations nations, int usNation, int nation, int year)` | What a partner's money a US$ now has to be multiplied by to stand in the units of the money its nation used in the first January (WN5-R, WN6): the product, over each year Y after Nations.FIRST_YEAR up to the year aske... |
| 108 | 5 | `public static double moneyAtFounding(Nations nations, WorldMoneys moneys, int nation)` | A partner's money a US$ in the first January, in the money its nation used then (the one moneyChain's units are): that money's rate in the WorldMoneys' opening month, else the nation's own figure for the first year wh... |
| 123 | 14 | `public static double realEffectiveRate(double ratePerUsd, double foundingRatePerUsd, double cityPricesSinceFounding, List<Partn...` | The real effective rate, the BIS's way: 100 x exp(sum of w_j x [ln(e_j / e_j0) - ln(rate / rate0) + ln P_city - ln(P_j / P_j0)]) over the partners, with e_j the partner's money per US$ (now, and in the founding's mont... |

