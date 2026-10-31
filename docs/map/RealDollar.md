# RealDollar.java - 85 lines · 7 methods · 0 constants · model

`ham/citybuildersim/RealDollar.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The real US dollar, constant 2015 dollars, and what the city's money is against it (WN4, D29): the D$ per real US$, the city's real GDP in it, and
> the real effective rate. Three pure functions of their inputs, and nothing in the model reads any of them: they answer "is my money dear?", which
> the gauge against a parity made of rent answered after a fashion (docs/studies/spec-exchange-rates.md 3.9; WN6 shows them).
> 
> WHY THIS EXISTS. Jerus, 2026-10-21: "lets keep something, the real usd dollar, aka two us dollars, one is the in game us dollar which inflates over
> time, and the other is the real us dollar, and that one is what real gdp is shown in, as well as an exchange rate with it, purely just for
> comparision purposes"; the base year "2015, cause later the start date will move back anyways". The in-game US$ is the US's money and inflates on
> its path (WorldEconomy, THE US DOLLAR ON THE US'S PATH), so a figure in it moves with its prices and says nothing across the years; a real US$ is
> what the World Bank's gdp_2015usd is in, and what the nations' output is already in.
> 
> THE BRIDGE (the leaderboard's D1, Jerus 2026-10-21: "2015 so that its calibrated to the other"): a founding game dollar counts as one real 2015 US
> dollar, whatever year the calendar starts, so the US's price level in 2015 over its level at founding is 1 and the real US$ is the in-game US$
> deflated by its own level since founding. The game's prices stay as struck from 2025's.
> 
> Every figure is in the city's money of today, so a currency reform (Denomination) changes nothing: the caller hands the founding's rate in today's
> units (Denomination.money(ForeignAccounts.OPENING_RATE)), and the D$ per real US$ is read in today's D$.

**Used by (2):** [Game](Game.md), [RealDollarCheck](RealDollarCheck.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 63 | **type** `public final class RealDollar` | The real US dollar, constant 2015 dollars, and what the city's money is against it (WN4, D29): the D$ per real US$, the city's real GDP in it, and the real effective rate. |
| 25 | 1 | `private RealDollar()` |  |
| 32 | 5 | **type** `public record Partner(double weight, double moneyPerUsdNow, double moneyPerUsdAtFounding, double pricesSinc...` | What the city's money is worth against a partner, for the real effective rate: the weight it trades with (any positive scale: the weights are shares of their sum), the partner's money per US$ now and in the founding's... |
| 33 | 3 | `boolean usable()` _(in RealDollar.Partner)_ |  |
| 38 | 1 | `private static boolean positive(double v)` |  |
| 41 | 3 | `public static List<Partner> usAlone(double usPricesSinceFounding)` | The US alone, as the one partner (w = 1): its money is the unit, so e and e0 are 1, and its prices are the in-game US$'s level since founding. |
| 50 | 3 | `public static double dollarsPer2015Usd(double ratePerUsd, double usPricesSinceFounding)` | D$ per real 2015 US$: the rate (D$ per US$) times the US$'s price level since founding. |
| 59 | 3 | `public static double realGdp2015Usd(double gdp, double priceIndex, double foundingRatePerUsd)` | The city's GDP in real 2015 US$, the World Bank's way: the GDP over the city's price index (YearBook.realGdp's division, which carries it back to the D$ of founding, and is the same through a currency reform) over the... |
| 71 | 14 | `public static double realEffectiveRate(double ratePerUsd, double foundingRatePerUsd, double cityPricesSinceFounding, List<Partn...` | The real effective rate, the BIS's way: 100 x exp(sum of w_j x [ln(e_j / e_j0) - ln(rate / rate0) + ln P_city - ln(P_j / P_j0)]) over the partners, with e_j the partner's money per US$ (now, and in the founding's mont... |

