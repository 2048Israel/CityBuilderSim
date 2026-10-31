package ham.citybuildersim;

import java.util.List;

/**
 * The real US dollar, constant 2015 dollars, and what the city's money is against it (WN4, D29): the D$ per real US$, the city's real GDP in it, and
 * the real effective rate. Three pure functions of their inputs, and nothing in the model reads any of them: they answer "is my money dear?", which
 * the gauge against a parity made of rent answered after a fashion (docs/studies/spec-exchange-rates.md 3.9; WN6 shows them).
 *
 * WHY THIS EXISTS. Jerus, 2026-10-21: "lets keep something, the real usd dollar, aka two us dollars, one is the in game us dollar which inflates over
 * time, and the other is the real us dollar, and that one is what real gdp is shown in, as well as an exchange rate with it, purely just for
 * comparision purposes"; the base year "2015, cause later the start date will move back anyways". The in-game US$ is the US's money and inflates on
 * its path (WorldEconomy, THE US DOLLAR ON THE US'S PATH), so a figure in it moves with its prices and says nothing across the years; a real US$ is
 * what the World Bank's gdp_2015usd is in, and what the nations' output is already in.
 *
 * THE BRIDGE (the leaderboard's D1, Jerus 2026-10-21: "2015 so that its calibrated to the other"): a founding game dollar counts as one real 2015 US
 * dollar, whatever year the calendar starts, so the US's price level in 2015 over its level at founding is 1 and the real US$ is the in-game US$
 * deflated by its own level since founding. The game's prices stay as struck from 2025's.
 *
 * Every figure is in the city's money of today, so a currency reform (Denomination) changes nothing: the caller hands the founding's rate in today's
 * units (Denomination.money(ForeignAccounts.OPENING_RATE)), and the D$ per real US$ is read in today's D$.
 */
public final class RealDollar {

    private RealDollar() { }

    /**
     * What the city's money is worth against a partner, for the real effective rate: the weight it trades with (any positive scale: the weights are
     * shares of their sum), the partner's money per US$ now and in the founding's month, and the partner's price level now against its level then.
     * A partner with a weight that is not positive, or a figure that is not positive and finite, is left out of the average.
     */
    public record Partner(double weight, double moneyPerUsdNow, double moneyPerUsdAtFounding, double pricesSinceFounding) {
        boolean usable() {
            return weight > 0 && Double.isFinite(weight) && positive(moneyPerUsdNow) && positive(moneyPerUsdAtFounding) && positive(pricesSinceFounding);
        }
    }

    private static boolean positive(double v) { return v > 0 && Double.isFinite(v); }

    /** The US alone, as the one partner (w = 1): its money is the unit, so e and e0 are 1, and its prices are the in-game US$'s level since founding. */
    public static List<Partner> usAlone(double usPricesSinceFounding) {
        return List.of(new Partner(1, 1, 1, usPricesSinceFounding));
    }

    /**
     * D$ per real 2015 US$: the rate (D$ per US$) times the US$'s price level since founding. A real 2015 US$ buys what a dollar bought in 2015 - at
     * founding what a founding US$ buys - and a US$ of today buys less by the level, so it takes that many more US$ for it, each at the rate. At
     * founding the level is 1, so it is the rate. NaN for a rate or a level that is not positive.
     */
    public static double dollarsPer2015Usd(double ratePerUsd, double usPricesSinceFounding) {
        return positive(ratePerUsd) && positive(usPricesSinceFounding) ? ratePerUsd * usPricesSinceFounding : Double.NaN;
    }

    /**
     * The city's GDP in real 2015 US$, the World Bank's way: the GDP over the city's price index (YearBook.realGdp's division, which carries it back to
     * the D$ of founding, and is the same through a currency reform) over the founding's rate in today's money (D$ per US$ then), so the founding's
     * D$ is one real US$ and a month of the same city is the same figure in whatever it is counted. NaN for an index or a rate that is not positive.
     */
    public static double realGdp2015Usd(double gdp, double priceIndex, double foundingRatePerUsd) {
        return positive(priceIndex) && positive(foundingRatePerUsd) && Double.isFinite(gdp) ? gdp / priceIndex / foundingRatePerUsd : Double.NaN;
    }

    /**
     * The real effective rate, the BIS's way: 100 x exp(sum of w_j x [ln(e_j / e_j0) - ln(rate / rate0) + ln P_city - ln(P_j / P_j0)]) over the
     * partners, with e_j the partner's money per US$ (now, and in the founding's month), rate the D$ per US$ (now, and at founding), P_city the city's
     * price index since founding and P_j/P_j0 the partner's price level against its founding's. w_j are the partners' weights as shares of their sum.
     * 100 at founding; above 100 the D$ is dearer in real terms than it was (a stronger D$, the city's prices up against the partners', or both).
     * A partner that is not usable is left out; with none left, the US alone at prices of 1 (the D$ against the US$ and the city's prices only).
     * NaN for a rate, a founding rate or a price index that is not positive.
     */
    public static double realEffectiveRate(double ratePerUsd, double foundingRatePerUsd, double cityPricesSinceFounding, List<Partner> partners) {
        if (!positive(ratePerUsd) || !positive(foundingRatePerUsd) || !positive(cityPricesSinceFounding)) return Double.NaN;
        double total = 0;
        for (Partner p : partners) if (p.usable()) total += p.weight();
        List<Partner> using = total > 0 ? partners : usAlone(1);
        if (!(total > 0)) total = 1;
        double home = -Math.log(ratePerUsd / foundingRatePerUsd) + Math.log(cityPricesSinceFounding);
        double sum = 0;
        for (Partner p : using) {
            if (!p.usable()) continue;
            sum += p.weight() / total * (Math.log(p.moneyPerUsdNow() / p.moneyPerUsdAtFounding()) + home - Math.log(p.pricesSinceFounding()));
        }
        return 100 * Math.exp(sum);
    }
}
