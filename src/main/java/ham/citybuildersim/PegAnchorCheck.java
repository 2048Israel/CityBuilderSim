package ham.citybuildersim;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The peg's anchor (batch WN4b; ForeignAccounts, THE REGIMES, THE PEG'S ANCHOR): the D$ can be pegged to any of the eight moneys, not
 * only the US$. The peg is kept in the anchor's money (D$ per unit of it); the rate the hold keeps, D$ per US$, is the peg over the anchor's
 * US$ price that month, so it moves with the anchor against the US$; the shadow's gap, the hazard and the lift are measured against that
 * held rate; and under a peg the dial ties to the anchor's central bank rate (DebtManager.tieRate(), as Denmark's follows the ECB's).
 *
 * WHY. Every peg there was is a peg to the US$, and every harness compares getRate() with getPegRate(). Giving the peg a second money could
 * silently change the US$ path (a division by an anchor price that is "1", an anchor field the old saves do not carry), could measure the
 * gap against the wrong rate (the peg in euros read as D$ per US$), could lose the anchor in the save or let a money with no rate anchor a
 * peg, or leave the dial tied to the US's rate under a euro peg. Each section says against which expression or constant it is judged.
 *
 * What this has to prove:
 *   1. A US$ ANCHOR IS THE PEG AS IT WAS: a new ForeignAccounts's anchor is the US$, and two seeded US$ pegs on a deficit trade and a deep
 *      vault, one handed the eight's moving rates every month and one never handed any, have the same rate, sale, support, gap and edge
 *      purchase to the bit every month: the eight's moves do not reach a US$ peg; getPegRate() is the peg itself.
 *   2. THE HELD RATE IS THE PEG OVER THE ANCHOR'S US$ PRICE: pegged to the euro, with the euro moving for two years on balanced trade, the
 *      rate each month is the peg per euro over the euro's price, the peg per euro never moves, the D$ per euro is the peg every month, the
 *      D$ per US$ took many values, and the hold dealt (the euro's swing was what it held).
 *   3. THE SHADOW'S GAP IS AGAINST THE HELD RATE: two quiet twins with equal held rates deal the same; when the euro rises 10% the euro
 *      twin's gap is the US$ twin's plus ln(1.1) on the same shadow, currencyGap() is the shadow over the held rate, and its hold sells
 *      more; the support is the log of the held rate over the shadow.
 *   4. CHOOSING THE ANCHOR: the held rate does not move on the switch, the peg per euro is the day's cross, and the rate follows the
 *      euro from the next month; a money with no rate or none of the eight is refused; outside a peg the setting is kept and the next peg
 *      is struck in it; the regime's words name the anchor.
 *   5. A DEVALUATION IN THE ANCHOR'S MONEY: repeg() is D$ per euro; the held rate is it over the euro's price, and the rate follows it.
 *   6. THE BREAK UNDER A EURO PEG: a euro peg with no vault, revalued, breaks for want of the vault, lands where the shadow plus the
 *      overshoot of the gap against the HELD rate puts it, and keeps its anchor.
 *   7. THE DIAL TIES TO THE ANCHOR'S RATE (a bare DebtManager): tied to the euro the dial is the euro's rate plus the spread, tied to the
 *      US$ it is what it was to the bit, untied it reads the US's; the spread is bounded over the anchor's rate and its decision names it.
 *   8. THE SAVE: slot 148 is the anchor; a restored euro peg is the same peg and plays the same next month; a save cut before slot 148
 *      reads the US$; an anchor with no saved rate reads the US$.
 *   9. IN A CITY (the full file only): a trading town pegged to the euro holds the peg per euro over the euro's price, its dial is the
 *      euro area's rate plus the spread, and the decision log names the euro.
 *  10. THE REVIEW'S TWO LOW NOTES (WN4c): in every month a euro peg holds, for three years of a swinging euro, the hold's support is
 *      the log of the held rate over the shadow, so it needs no clamp; and the gap the dial reads (currencyGap()), after the month's
 *      rates are handed and before the reprice, is the last reprice's shadow over the held rate at THIS month's euro, the same held
 *      rate the reprice then measures its gap against.
 *
 * Every fixture causes its condition: rates handed by hand before each month (the eight's, with the euro moved where a section needs the
 * anchor to move), a deficit trade where the hold has to sell, a vault of none where it cannot, a quiet market (seed 0) where a chance
 * break would blur the arithmetic, and a seeded one where the claim is the very absence of a difference.
 */
public class PegAnchorCheck {

    static int fails = 0;
    static final PrintStream out = System.out;

    static final ForeignAccounts.Regime LEAN = ForeignAccounts.Regime.LEAN, PEG = ForeignAccounts.Regime.PEG, FLOAT = ForeignAccounts.Regime.FLOAT;

    static final double TRADE = CurrencyRegimeCheck.TRADE, DEEP = CurrencyRegimeCheck.DEEP;

    /** The cross rates the sections hand: US$ per unit of the US$, euro, yen, pound, yuan, Canadian and Australian dollar and franc (VaultMoneysCheck's). */
    static final double[] R = {1, 1.10, 0.0070, 1.27, 0.14, 0.73, 0.65, 1.12};

    /** The length of the foreign accounts' array with the peg's anchor in slot 148 (WN4b). */
    static final int SLOTS = 149;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-130s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Equal to the bit (and so a -0.0 is not a 0.0). */
    static void same(String label, double actual, double expected) {
        boolean ok = Double.compare(actual, expected) == 0;
        if (!ok) fails++;
        out.printf("%-130s %s%s%n", label, ok ? "OK" : "FAIL", ok ? "" : String.format("  %.17g != %.17g", actual, expected));
    }

    /** Equal within a share of the expected. */
    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol * Math.abs(expected);
        if (!ok) fails++;
        out.printf("%-130s %s%s%n", label, ok ? "OK" : "FAIL", ok ? "" : String.format("  %.17g != %.17g", actual, expected));
    }

    /** Equal within an absolute amount (for the logs, which sit near zero). */
    static void closeAbs(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        out.printf("%-130s %s%s%n", label, ok ? "OK" : "FAIL", ok ? "" : String.format("  %.17g != %.17g", actual, expected));
    }

    /** Whether a figure is within a share of another: the loops' accumulator. */
    static boolean within(double actual, double expected, double tol) { return Math.abs(actual - expected) <= tol * Math.abs(expected); }

    /** The eight's rates with the euro at this price. */
    static double[] rates(double euro) {
        double[] r = R.clone();
        r[1] = euro;
        return r;
    }

    /** The eight's rates in month m, every one moving (a sine each, out of step with the others). */
    static double[] moving(int m) {
        double[] r = new double[ForeignAccounts.MONEYS];
        r[0] = 1;
        for (int k = 1; k < r.length; k++) r[k] = R[k] * (1 + .06 * Math.sin(1.3 * m + k));
        return r;
    }

    /** The euro's price in month m: 1.10 swinging 6% either side. */
    static double euroAt(int m) { return 1.10 * (1 + .06 * Math.sin(1.3 * m)); }

    /** A settled currency pegged to the euro at today's rate in euros, the euro at this price: rates handed, the anchor chosen, then the peg struck. */
    static ForeignAccounts euroPeg(double exports, double imports, double vaultUsd, long seed, double euro) {
        ForeignAccounts fx = CurrencyRegimeCheck.book(exports, imports, vaultUsd, seed, LEAN);
        fx.setMoneyRates(rates(euro));
        fx.setPegAnchor(1);
        fx.setRegime(PEG);
        return fx;
    }

    /** One month with the euro at this price handed first. */
    static void month(ForeignAccounts fx, double euro, double exports, double imports) {
        fx.setMoneyRates(rates(euro));
        CurrencyRegimeCheck.month(fx, exports, imports);
    }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        aUsdAnchorIsThePegAsItWas();
        theHeldRateIsThePegOverTheAnchorsPrice();
        theShadowsGapIsAgainstTheHeldRate();
        choosingTheAnchor();
        aDevaluationInTheAnchorsMoney();
        theBreakUnderAEuroPeg();
        theDialTiesToTheAnchorsRate();
        theSave();
        inACity();
        theReviewsLowNotes();
        out.printf("%nPegAnchorCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       1. A US$ ANCHOR IS THE PEG AS IT WAS
       ===================================================================== */

    static void aUsdAnchorIsThePegAsItWas() {
        out.println("--- 1. a US$ anchor is the peg as it was: the eight's moves do not reach it, to the bit ---");
        ForeignAccounts fresh = new ForeignAccounts();
        assertTrue("a new ForeignAccounts's anchor is the US$: index 0, code USD", fresh.getPegAnchor() == 0 && "USD".equals(fresh.getPegAnchorCode()));

        double exports = .8 * TRADE;
        ForeignAccounts handed = CurrencyRegimeCheck.book(exports, TRADE, DEEP, CurrencyRegimeCheck.SEED, PEG);
        ForeignAccounts alone = CurrencyRegimeCheck.book(exports, TRADE, DEEP, CurrencyRegimeCheck.SEED, PEG);
        assertTrue("fixture: two seeded pegs to the US$ on a deficit trade (exports .8 of imports) and a deep vault, struck at the same rate",
                handed.getRegime() == PEG && alone.getRegime() == PEG && handed.getPegAnchor() == 0 && alone.getPegAnchor() == 0
                        && handed.isMarketSeeded() && alone.isMarketSeeded() && handed.getReservesUsd() >= DEEP / 2
                        && Double.compare(handed.getPegRate(), alone.getPegRate()) == 0);
        boolean rate = true, sold = true, bought = true, felt = true, gap = true, peg = true, shadow = true, regime = true;
        double dealt = 0;
        Set<Double> euros = new HashSet<>();
        for (int m = 1; m <= 24; m++) {
            double[] eight = moving(m);
            euros.add(eight[1]);
            handed.setMoneyRates(eight);
            CurrencyRegimeCheck.month(handed, exports, TRADE);
            CurrencyRegimeCheck.month(alone, exports, TRADE);
            rate &= Double.compare(handed.getRate(), alone.getRate()) == 0;
            sold &= Double.compare(handed.getDefenceUsd(), alone.getDefenceUsd()) == 0;
            bought &= Double.compare(handed.getEdgeBoughtUsd(), alone.getEdgeBoughtUsd()) == 0;
            felt &= Double.compare(handed.getHoldFelt(), alone.getHoldFelt()) == 0;
            gap &= Double.compare(handed.getLastPegGap(), alone.getLastPegGap()) == 0;
            shadow &= Double.compare(handed.getShadowRate(), alone.getShadowRate()) == 0;
            peg &= Double.compare(handed.getPegRate(), handed.getPegPerAnchor()) == 0 && Double.compare(alone.getPegRate(), alone.getPegPerAnchor()) == 0
                    && Double.compare(handed.getPegRate(), alone.getPegRate()) == 0;
            regime &= handed.getRegime() == PEG && alone.getRegime() == PEG && handed.getPegAnchor() == 0;
            dealt += handed.getDefenceUsd();
        }
        assertTrue("fixture: the eight really moved (the euro took " + euros.size() + " prices), the one was handed them and the other never was, and the hold sold (US$"
                + String.format("%,.0f", dealt) + ") through 24 months in which neither peg broke",
                euros.size() > 12 && handed.hasRate(1) && !alone.hasRate(1) && dealt > 0 && regime);
        assertTrue("the rate is the same every month, to the bit", rate);
        assertTrue("...the hold's sale", sold);
        assertTrue("...the strong edge's purchase", bought);
        assertTrue("...the hold's support", felt);
        assertTrue("...the shadow's gap from the peg", gap);
        assertTrue("...and the shadow", shadow);
        assertTrue("getPegRate() is getPegPerAnchor() exactly, in both, and the same in both", peg);
    }

    /* =====================================================================
       2. THE HELD RATE IS THE PEG OVER THE ANCHOR'S US$ PRICE
       ===================================================================== */

    static void theHeldRateIsThePegOverTheAnchorsPrice() {
        out.println("--- 2. the held rate is the peg over the anchor's US$ price: the D$ per euro is the peg, the D$ per US$ moves with the euro ---");
        ForeignAccounts fx = euroPeg(TRADE, TRADE, DEEP, 0, R[1]);
        double perEuro = fx.getPegPerAnchor();
        assertTrue("fixture: pegged to the euro on a deep vault and a balanced trade, quiet, at today's rate in euros (the rate times the euro's US$ price)",
                fx.getRegime() == PEG && fx.getPegAnchor() == 1 && "EUR".equals(fx.getPegAnchorCode()) && !fx.isMarketSeeded() && fx.getReservesUsd() >= DEEP / 2
                        && within(perEuro, fx.getRate() * R[1], 1e-12));
        boolean heldRate = true, perEuroStill = true, perEuroRate = true, held = true;
        double dealt = 0;
        Set<Double> perUsd = new HashSet<>();
        for (int m = 1; m <= 24; m++) {
            double euro = euroAt(m);
            month(fx, euro, TRADE, TRADE);
            held &= fx.getRegime() == PEG && fx.getPegAnchor() == 1 && fx.getBreaks() == 0;
            heldRate &= within(fx.getRate(), fx.getPegPerAnchor() / euro, 1e-12);
            perEuroStill &= Double.compare(fx.getPegPerAnchor(), perEuro) == 0;
            perEuroRate &= within(fx.getRate() * euro, perEuro, 1e-12);
            perUsd.add(fx.getRate());
            dealt += fx.getDefenceUsd() + fx.getEdgeBoughtUsd();
        }
        assertTrue("every month for two years the peg held and was never broken", held);
        assertTrue("each month the rate is the peg per euro over the euro's US$ price that month, to 1e-12", heldRate);
        assertTrue("...getPegPerAnchor() never moved", perEuroStill);
        assertTrue("...so the D$ per euro (the rate times the euro's price) is the peg every month", perEuroRate);
        assertTrue("...while the D$ per US$ took " + perUsd.size() + " different values", perUsd.size() >= 2);
        assertTrue("...and the hold dealt, US$" + String.format("%,.0f", dealt) + ", on a balanced trade: the euro's swing against the US$ was what it held", dealt > 0);
        close("getPegRate() is the rate held now: the peg per euro over the euro's last price", fx.getPegRate(), perEuro / euroAt(24), 1e-12);
    }

    /* =====================================================================
       3. THE SHADOW'S GAP IS AGAINST THE HELD RATE
       ===================================================================== */

    static void theShadowsGapIsAgainstTheHeldRate() {
        out.println("--- 3. the shadow's gap is against the held rate: a euro that rises takes the D$ off the shadow, and the hold pays for it ---");
        double exports = .8 * TRADE;
        double base = 2.0;   // a power of two: the peg over the price is the rate to the bit
        double up = 1.10;
        ForeignAccounts usd = CurrencyRegimeCheck.book(exports, TRADE, DEEP, 0, PEG);
        ForeignAccounts euro = euroPeg(exports, TRADE, DEEP, 0, base);
        assertTrue("fixture: two quiet twins on the same deficit trade and a deep vault, one pegged to the US$ at r, one to the euro at r x " + base
                + " with the euro at US$" + base + ": the same rate and the same held rate to the bit",
                usd.getPegAnchor() == 0 && euro.getPegAnchor() == 1 && !usd.isMarketSeeded() && !euro.isMarketSeeded()
                        && Double.compare(usd.getRate(), euro.getRate()) == 0 && Double.compare(usd.getPegRate(), euro.getPegRate()) == 0
                        && Double.compare(euro.getPegPerAnchor(), usd.getPegPerAnchor() * base) == 0);

        month(usd, base, exports, TRADE);
        month(euro, base, exports, TRADE);
        assertTrue("fixture: the US$ twin's hold sold dollars that month (US$" + String.format("%,.0f", usd.getDefenceUsd()) + ")", usd.getDefenceUsd() > 0);
        assertTrue("a month at the same euro deals the same in both: the rate, the sale and the support to the bit",
                Double.compare(usd.getRate(), euro.getRate()) == 0 && Double.compare(usd.getDefenceUsd(), euro.getDefenceUsd()) == 0
                        && Double.compare(usd.getHoldFelt(), euro.getHoldFelt()) == 0 && Double.compare(usd.getLastPegGap(), euro.getLastPegGap()) == 0);

        month(usd, base * up, exports, TRADE);
        month(euro, base * up, exports, TRADE);
        assertTrue("fixture: both held a peg that month, and the euro rose " + String.format("%.0f%%", (up - 1) * 100) + " against the US$ between the two",
                usd.getRegime() == PEG && euro.getRegime() == PEG && usd.isHeld() && euro.isHeld());
        assertTrue("the shadow is the D$'s own rule and does not follow the euro: the same in both to the bit", Double.compare(usd.getShadowRate(), euro.getShadowRate()) == 0);
        closeAbs("the euro twin's gap is the US$ twin's plus ln(1.1)", euro.getLastPegGap(), usd.getLastPegGap() + Math.log(up), 1e-12);
        close("currencyGap() is the shadow over the held rate less one, the US$ twin's", usd.currencyGap(), usd.getShadowRate() / usd.getPegRate() - 1, 1e-12);
        close("...and the euro twin's, where the held rate is the one the euro put it at", euro.currencyGap(), euro.getShadowRate() / euro.getPegRate() - 1, 1e-12);
        close("the euro twin holds the D$ a tenth stronger against the US$: its rate is the US$ twin's over 1.1", euro.getRate(), usd.getRate() / up, 1e-12);
        assertTrue("...and that costs the vault: the euro twin sold more (US$" + String.format("%,.0f", euro.getDefenceUsd()) + " against US$"
                + String.format("%,.0f", usd.getDefenceUsd()) + ")", euro.getDefenceUsd() > usd.getDefenceUsd());
        closeAbs("the euro twin's support is the log of the held rate over the shadow: the hold's own identity", euro.getHoldFelt(),
                Math.log(euro.getPegRate() / euro.getShadowRate()), 1e-9);
        closeAbs("...and the US$ twin's likewise", usd.getHoldFelt(), Math.log(usd.getPegRate() / usd.getShadowRate()), 1e-9);
        double landing = euro.breakLanding();
        close("breakLanding() reads the held rate: the shadow plus the overshoot of its gap from it", landing,
                euro.getShadowRate() * Math.exp(ForeignAccounts.OVERSHOOT * Math.log(euro.getShadowRate() / euro.getPegRate())), 1e-12);
        close("rateAfterDeal() under the peg is the held rate", euro.rateAfterDeal(1_000), euro.getPegRate(), 1e-15);
    }

    /* =====================================================================
       4. CHOOSING THE ANCHOR
       ===================================================================== */

    static void choosingTheAnchor() {
        out.println("--- 4. choosing the anchor: the held rate does not move on the switch, a money with no rate is refused, outside a peg the setting is kept ---");
        ForeignAccounts fx = CurrencyRegimeCheck.book(TRADE, TRADE, DEEP, 0, LEAN);
        fx.setMoneyRates(R);
        fx.setRegime(PEG);
        month(fx, R[1], TRADE, TRADE);
        double p = fx.getPegRate();
        assertTrue("fixture: a US$ peg at p = " + String.format("%.6f", p) + ", the rates handed, the peg held", fx.getRegime() == PEG && fx.getPegAnchor() == 0
                && Double.compare(fx.getRate(), p) == 0 && fx.hasRate(1));
        assertTrue("under a US$ peg the words are \"Peg to the US dollar\"", "Peg to the US dollar".equals(fx.regimeWords()));
        fx.setPegAnchor(1);
        assertTrue("setPegAnchor(1) makes the euro the anchor", fx.getPegAnchor() == 1 && "EUR".equals(fx.getPegAnchorCode()));
        close("...the held rate does not move on the switch: getPegRate() is p", fx.getPegRate(), p, 1e-15);
        close("...the peg is struck again at the day's cross: getPegPerAnchor() is p times the euro's price", fx.getPegPerAnchor(), p * R[1], 1e-15);
        assertTrue("...the words name the euro", "Peg to the euro".equals(fx.regimeWords()));
        double euro = 1.25;
        month(fx, euro, TRADE, TRADE);
        assertTrue("fixture: the euro moved and the peg held", fx.getRegime() == PEG && fx.getBreaks() == 0);
        close("the next month the rate follows the euro: the peg per euro over the new price", fx.getRate(), fx.getPegPerAnchor() / euro, 1e-12);
        double before = fx.getPegRate();
        fx.setPegAnchor(0);
        close("back to the US$, the held rate is where it stood", fx.getPegRate(), before, 1e-15);
        close("...and the peg is that rate, D$ per US$", fx.getPegPerAnchor(), before, 1e-15);
        fx.setPegAnchor(0);
        assertTrue("choosing the anchor it already has changes nothing", fx.getPegAnchor() == 0 && Double.compare(fx.getPegPerAnchor(), fx.getPegRate()) == 0);

        // Refused.
        ForeignAccounts none = new ForeignAccounts();
        none.setPegAnchor(1);
        assertTrue("a ForeignAccounts never handed rates refuses the euro: the anchor stays the US$", none.getPegAnchor() == 0 && !none.hasRate(1));
        double[] noYen = R.clone();
        noYen[2] = Double.NaN;
        ForeignAccounts partial = CurrencyRegimeCheck.book(TRADE, TRADE, DEEP, 0, LEAN);
        partial.setMoneyRates(noYen);
        partial.setPegAnchor(2);
        assertTrue("fixture: the yen has no rate this month", !partial.hasRate(2) && partial.hasRate(1));
        assertTrue("a money with no rate this month is refused", partial.getPegAnchor() == 0);
        for (int k : new int[] {-1, ForeignAccounts.MONEYS, 99}) partial.setPegAnchor(k);
        assertTrue("an index outside 0 to 7 is refused", partial.getPegAnchor() == 0);
        partial.setPegAnchor(ForeignAccounts.moneyIndex("XXX"));
        assertTrue("...and so is a code that is none of the eight (moneyIndex() reads -1)", partial.getPegAnchor() == 0 && ForeignAccounts.moneyIndex("XXX") == -1);

        // Outside a peg.
        ForeignAccounts lean = CurrencyRegimeCheck.book(TRADE, TRADE, DEEP, 0, LEAN);
        lean.setMoneyRates(R);
        double rateBefore = lean.getRate();
        lean.setPegAnchor(3);
        assertTrue("outside a peg (Lean), setPegAnchor(3) keeps the setting and does not move the rate or the regime", lean.getPegAnchor() == 3
                && lean.getRegime() == LEAN && Double.compare(lean.getRate(), rateBefore) == 0 && lean.getPegPerAnchor() == 0);
        assertTrue("...and under Lean the words are the regime's own", lean.regimeWords().equals(LEAN.words));
        lean.setRegime(PEG);
        close("entering the peg pegs at the day's rate in pounds: getPegPerAnchor() is the rate times the pound's price", lean.getPegPerAnchor(), lean.getRate() * R[3], 1e-12);
        close("...so the rate held is the rate of the day", lean.getPegRate(), lean.getRate(), 1e-12);
        assertTrue("...and the words are \"Peg to the pound\"", "Peg to the pound".equals(lean.regimeWords()));
        lean.setRegime(FLOAT);
        assertTrue("leaving the peg keeps the anchor", lean.getPegAnchor() == 3 && FLOAT.words.equals(lean.regimeWords()));
        assertTrue("the Peg regime's own words no longer name a money", PEG.words.equals("Peg"));
    }

    /* =====================================================================
       5. A DEVALUATION IN THE ANCHOR'S MONEY
       ===================================================================== */

    static void aDevaluationInTheAnchorsMoney() {
        out.println("--- 5. a devaluation in the anchor's money: repeg() is D$ per euro, the held rate is that over the euro's price ---");
        ForeignAccounts fx = euroPeg(TRADE, TRADE, DEEP, 0, R[1]);
        month(fx, R[1], TRADE, TRADE);
        double was = fx.getPegPerAnchor(), heldWas = fx.getPegRate();
        assertTrue("fixture: a euro peg, held", fx.getRegime() == PEG && fx.getPegAnchor() == 1 && within(fx.getRate(), heldWas, 1e-12));
        double x = was * 1.05;
        fx.repeg(x);
        close("repeg(x) sets the peg per euro to x", fx.getPegPerAnchor(), x, 1e-12);
        close("...and the held rate to x over the euro's price", fx.getPegRate(), x / R[1], 1e-12);
        assertTrue("...a higher x is a higher held rate (a weaker D$)", fx.getPegRate() > heldWas);
        double euro = 1.20;
        month(fx, euro, TRADE, TRADE);
        assertTrue("fixture: the peg held through the month", fx.getRegime() == PEG && fx.getBreaks() == 0);
        close("the next month the rate is the new peg over the euro's price that month", fx.getRate(), fx.getPegPerAnchor() / euro, 1e-12);
        double revalue = fx.getPegPerAnchor() * .9;
        fx.repeg(revalue);
        assertTrue("a revaluation lowers the held rate", fx.getPegRate() < fx.getRate());
        ForeignAccounts lean = CurrencyRegimeCheck.book(TRADE, TRADE, DEEP, 0, LEAN);
        lean.setMoneyRates(R);
        lean.setPegAnchor(1);
        lean.repeg(5);
        assertTrue("outside a peg repeg() does nothing", lean.getPegPerAnchor() == 0);
    }

    /* =====================================================================
       6. THE BREAK UNDER A EURO PEG
       ===================================================================== */

    static void theBreakUnderAEuroPeg() {
        out.println("--- 6. the break under a euro peg: a euro peg with no vault breaks for want of one, at the held rate's gap ---");
        ForeignAccounts fx = euroPeg(TRADE, TRADE, 0, 0, R[1]);
        fx.repeg(.8 * fx.getPegPerAnchor());
        double euro = 1.10;
        double held = fx.getPegPerAnchor() / euro;
        double rateBefore = fx.getRate();
        assertTrue("fixture: a euro peg with no vault, revalued to 0.8 of itself so the hold must sell dollars it has not got, quiet (no chance of doubt)",
                fx.getPegAnchor() == 1 && fx.getRegime() == PEG && fx.getReservesUsd() == 0 && !fx.isMarketSeeded() && held < rateBefore);
        month(fx, euro, TRADE, TRADE);
        assertTrue("the peg broke that month", fx.brokeThisMonth() && fx.getBreaks() == 1);
        assertTrue("...for want of the vault", fx.getLastBreakCause() == ForeignAccounts.BreakCause.VAULT);
        assertTrue("...the regime is Float after it", fx.getRegime() == FLOAT);
        assertTrue("...and the anchor is still the euro, kept when the regime is left", fx.getPegAnchor() == 1 && fx.getPegPerAnchor() > 0);
        double shadow = fx.getLastBreakShadow();
        double gap = Math.log(shadow / held);
        assertTrue("fixture: the shadow was the weaker side of the held rate (" + String.format("%.4f against %.4f", shadow, held) + ")", gap > .01);
        double landing = Math.max(ForeignAccounts.MIN_RATE, Math.min(ForeignAccounts.MAX_RATE, shadow * Math.exp(ForeignAccounts.OVERSHOOT * gap)));
        close("the rate it lands at is the shadow times exp(OVERSHOOT x the gap against the held rate): " + String.format("%.6f", fx.getRate()), fx.getRate(), landing, 1e-9);
    }

    /* =====================================================================
       7. THE DIAL TIES TO THE ANCHOR'S RATE
       ===================================================================== */

    static void theDialTiesToTheAnchorsRate() {
        out.println("--- 7. the dial ties to the anchor's rate: a bare DebtManager, tied to the euro, to the US$, and not at all ---");
        DebtManager dm = new DebtManager();
        DecisionLog log = new DecisionLog();
        dm.recordTo(log);
        dm.setWorldRate(.04);
        double[] annual = new double[ForeignAccounts.MONEYS];
        Arrays.fill(annual, .03);
        annual[1] = .02;
        annual[2] = .001;
        dm.setMoneyRates(annual);
        boolean[] tied = {true};
        String[] code = {"EUR"};
        dm.setTiedSource(() -> tied[0]);
        dm.setTieMoneySource(() -> code[0]);
        assertTrue("fixture: the US's rate is 4%, the euro area's 2% and the yen's 0.1%", dm.worldRate() == .04 && dm.worldRate("EUR") == .02 && dm.worldRate("JPY") == .001);

        assertTrue("tied with the euro the anchor, tieMoney() is EUR", dm.isTied() && "EUR".equals(dm.tieMoney()));
        same("...tieRate() is the euro area's rate", dm.tieRate(), dm.worldRate("EUR"));
        dm.setPegSpread(.005);
        assertTrue("...a spread of half a point is kept", Double.compare(dm.getPegSpread(), .005) == 0);
        same("...and tiedRate(0) is the euro area's rate plus the spread, inside the dial's bounds", dm.tiedRate(0),
                Math.max(DebtManager.MIN_POLICY_RATE, Math.min(DebtManager.MAX_POLICY_RATE, dm.worldRate("EUR") + dm.getPegSpread())));
        assertTrue("...which is not the US's rate plus the spread", Double.compare(dm.tiedRate(0), dm.worldRate() + dm.getPegSpread()) != 0);
        DecisionLog.Entry last = log.last();
        assertTrue("...and the decision names the euro's rate: \"" + (last == null ? "" : last.label()) + "\"",
                last != null && DecisionLog.CENTRAL_BANK.equals(last.kind()) && last.label().startsWith("Spread over the euro's rate to "));
        dm.setPegSpread(1.0);
        same("a spread of 100 points is clamped to the dial's ceiling over the euro area's rate", dm.getPegSpread(), DebtManager.MAX_POLICY_RATE - dm.worldRate("EUR"));
        dm.setPegSpread(-1.0);
        same("...and a spread under it to the floor over the euro area's rate", dm.getPegSpread(), DebtManager.MIN_POLICY_RATE - dm.worldRate("EUR"));
        dm.setPegSpread(.005);

        code[0] = "JPY";
        same("moved to the yen, tieRate() is the yen's rate", dm.tieRate(), .001);

        code[0] = "USD";
        assertTrue("tied with the US$ the anchor, tieMoney() is USD", "USD".equals(dm.tieMoney()));
        same("...tieRate() is worldRate() to the bit", dm.tieRate(), dm.worldRate());
        same("...tiedRate(0) is the old expression: the US's rate plus the spread, inside the bounds, to the bit", dm.tiedRate(0),
                Math.max(DebtManager.MIN_POLICY_RATE, Math.min(DebtManager.MAX_POLICY_RATE, dm.worldRate() + dm.getPegSpread())));
        dm.setPegSpread(.007);
        last = log.last();
        assertTrue("...and the decision reads as it did: \"" + (last == null ? "" : last.label()) + "\"", last != null && last.label().startsWith("Spread over the US rate to 0.70%"));
        dm.setPegSpread(1.0);
        same("...a spread of 100 points is clamped over the US's rate", dm.getPegSpread(), DebtManager.MAX_POLICY_RATE - dm.worldRate());

        // The autopilot's currency aim sets the spread from the gap, over the same rate.
        dm.setAutopilot(true);
        dm.setAim(DebtManager.Aim.CURRENCY);
        code[0] = "EUR";
        same("with the autopilot aiming at the currency under a euro peg, tiedRate(gap) is the euro area's rate plus the gap's quarter point", dm.tiedRate(.1),
                Math.max(DebtManager.MIN_POLICY_RATE, Math.min(DebtManager.MAX_POLICY_RATE, dm.worldRate("EUR") + DebtManager.CURRENCY_WEIGHT * .1)));
        same("...and currencyRate(), a band's aim, stays on the US's rate", dm.currencyRate(.1),
                Math.max(DebtManager.MIN_POLICY_RATE, Math.min(DebtManager.MAX_POLICY_RATE, dm.worldRate() + DebtManager.CURRENCY_WEIGHT * .1)));

        tied[0] = false;
        assertTrue("untied, tieMoney() is null", !dm.isTied() && dm.tieMoney() == null);
        same("...and tieRate() is the US's rate", dm.tieRate(), dm.worldRate());
        DebtManager bare = new DebtManager();
        bare.setWorldRate(.04);
        assertTrue("a manager nobody handed a source is untied, and tieMoney() is null", bare.tieMoney() == null);
        same("...with tieRate() the US's rate", bare.tieRate(), bare.worldRate());
        bare.setTiedSource(() -> true);
        assertTrue("tied with no money source, it is still null and the US's", bare.tieMoney() == null);
        same("...and tieRate() the US's rate", bare.tieRate(), bare.worldRate());
    }

    /* =====================================================================
       8. THE SAVE
       ===================================================================== */

    static void theSave() {
        out.println("--- 8. the save: slot 148 is the anchor; a restored euro peg plays the same, and a save without the slot reads the US$ ---");
        double exports = .9 * TRADE;
        ForeignAccounts fx = euroPeg(exports, TRADE, DEEP, 0, R[1]);
        for (int m = 1; m <= 8; m++) month(fx, euroAt(m), exports, TRADE);
        double[] saved = fx.toSaveArray();
        assertTrue("fixture: a euro peg that has dealt for eight months and carries a support (" + String.format("%.4f", fx.getHoldFelt()) + "), held",
                fx.getRegime() == PEG && fx.getPegAnchor() == 1 && fx.getHoldFelt() != 0 && fx.getDefenceUsdLifetime() > 0);
        assertTrue("the array runs to slot 148, the peg's anchor (" + saved.length + " slots)", saved.length == SLOTS);
        same("slot 148 is the anchor's index, the euro's", saved[148], 1);
        same("slot 147 is still the hold's support", saved[147], fx.getHoldFelt());
        same("slot 76 is still the peg, now D$ per euro", saved[76], fx.getPegPerAnchor());

        ForeignAccounts back = new ForeignAccounts();
        back.restore(saved);
        assertTrue("restored, the anchor is the euro", back.getPegAnchor() == 1 && "EUR".equals(back.getPegAnchorCode()) && back.getRegime() == PEG);
        same("...the peg per euro is the saved one", back.getPegPerAnchor(), fx.getPegPerAnchor());
        same("...and so is the rate held", back.getPegRate(), fx.getPegRate());
        assertTrue("...and every slot reads back to the bit", Arrays.equals(back.toSaveArray(), saved));
        double euro = euroAt(9);
        month(fx, euro, exports, TRADE);
        month(back, euro, exports, TRADE);
        assertTrue("fixture: both held the peg the next month", fx.getRegime() == PEG && back.getRegime() == PEG);
        same("the next month, with the same rates and trade, the rate is the unsaved twin's", back.getRate(), fx.getRate());
        same("...the deal", back.getDefenceUsd(), fx.getDefenceUsd());
        same("...the support", back.getHoldFelt(), fx.getHoldFelt());
        same("...the gap", back.getLastPegGap(), fx.getLastPegGap());
        same("...and the shadow", back.getShadowRate(), fx.getShadowRate());

        ForeignAccounts cut = new ForeignAccounts();
        cut.restore(Arrays.copyOf(saved, 148));
        assertTrue("a save cut to 148 slots, as WN4 wrote it, restores the US$ as the anchor, the peg still held", cut.getPegAnchor() == 0 && cut.getRegime() == PEG && cut.getPegPerAnchor() > 0);
        ForeignAccounts unrated = new ForeignAccounts();
        double[] noEuro = saved.clone();
        noEuro[113 + 1] = 0;
        unrated.restore(noEuro);
        assertTrue("fixture: that save has no rate for the euro", !unrated.hasRate(1));
        assertTrue("a save whose slot 148 is the euro but whose euro rate is 0 restores the US$: a money with no rate cannot anchor a peg", unrated.getPegAnchor() == 0);
        // WN4c (the stranger's review of WN4b): slot 76 is D$ per EURO; on that fallback it must not be read as D$ per US$.
        assertTrue("fixture: the saved peg is D$ per euro, " + String.format("%.4f", saved[76]) + ", not the loaded rate, " + String.format("%.4f", saved[3]),
                Math.abs(saved[76] / saved[3] - 1) > .02);
        assertTrue("...the fallback keeps the peg: still a peg, on the US$", unrated.getRegime() == PEG && unrated.getPegAnchor() == 0);
        same("...its held rate is the loaded rate, not the euro figure", unrated.getPegRate(), saved[3]);
        same("...and the peg per US$ is that rate", unrated.getPegPerAnchor(), saved[3]);
        CurrencyRegimeCheck.month(unrated, exports, TRADE);
        assertTrue("fixture: that peg held the next month", unrated.getRegime() == PEG);
        same("...and held the loaded rate, as a US$ peg of that rate does", unrated.getRate(), saved[3]);
        ForeignAccounts wild = new ForeignAccounts();
        double[] garbled = saved.clone();
        garbled[148] = 9;
        wild.restore(garbled);
        assertTrue("an anchor outside the eight reads the US$", wild.getPegAnchor() == 0);
        garbled[148] = Double.NaN;
        wild.restore(garbled);
        assertTrue("...and so does one that is not a number", wild.getPegAnchor() == 0);
        fx.reset();
        assertTrue("reset() puts the anchor back on the US$", fx.getPegAnchor() == 0);
        back.restore(Arrays.copyOf(saved, 148));
        assertTrue("restoring a shorter save over a euro peg starts from the US$ (restore() resets first)", back.getPegAnchor() == 0);
    }

    /* =====================================================================
       9. IN A CITY
       ===================================================================== */

    /** The decision log's currency lines, oldest first. */
    static List<String> currencyLines(Game g) {
        List<String> lines = new ArrayList<>();
        for (DecisionLog.Entry e : g.getDecisions().entries()) if (DecisionLog.CURRENCY.equals(e.kind())) lines.add(e.label());
        return lines;
    }

    static void inACity() {
        out.println("--- 9. in a city (the full file): a trading town pegged to the euro holds the peg per euro, ties its dial to the euro area and says so ---");
        if (WorldData.locate() == null) {
            out.println("  SKIP: world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": the city's section did not run");
            return;
        }
        Game g = InterestAbroadCheck.town("peg-anchor-city", false, false);
        ForeignAccounts fx = g.getForeignAccounts();
        DebtManager dm = g.getDebtManager();
        for (int m = 0; m < 6 && !fx.hasRate(1); m++) InterestAbroadCheck.play(g);
        assertTrue("fixture: the town has played until the eight have rates, on the US$ for its anchor and Lean", fx.hasRate(1) && fx.getPegAnchor() == 0 && fx.getRegime() == LEAN);

        g.setPegAnchor("EUR");
        g.setCurrencyRegime(PEG);
        assertTrue("fixture: pegged to the euro at the day's cross, the peg per euro the rate times the euro's price",
                fx.getRegime() == PEG && fx.getPegAnchor() == 1 && within(fx.getPegPerAnchor(), fx.getRate() * fx.getMoneyUsd(1), 1e-12));
        List<String> lines = currencyLines(g);
        int n = lines.size();
        assertTrue("the decision log names the euro: \"" + (n >= 2 ? lines.get(n - 2) : "") + "\" and \"" + (n >= 1 ? lines.get(n - 1) : "") + "\"",
                n >= 2 && lines.get(n - 2).equals("The peg's anchor to the euro") && lines.get(n - 1).equals("Currency regime to Peg to the euro"));
        assertTrue("fixture: the dial is on the Inflation aim, so a tied dial is the anchor's rate plus the spread, and the euro area's rate is not the US's",
                dm.getAim() == DebtManager.Aim.INFLATION && Math.abs(dm.worldRate("EUR") - dm.worldRate()) > 1e-6 && "EUR".equals(dm.tieMoney()));

        boolean held = true, rate = true, dial = true, tie = true, money = true;
        for (int m = 1; m <= 3; m++) {
            // The dial is set early in the month, before it hands the banks their new rates (as the US's always was), so it stands over the rate the month began with.
            double gapBefore = fx.currencyGap(), euroRateBefore = dm.worldRate("EUR"), spreadBefore = dm.getPegSpread(), tiedBefore = dm.tiedRate(gapBefore);
            InterestAbroadCheck.play(g);
            double euro = fx.getMoneyUsd(1);
            held &= fx.getRegime() == PEG && fx.getPegAnchor() == 1;
            rate &= within(fx.getRate(), fx.getPegPerAnchor() / euro, 1e-12);
            double expected = Math.max(DebtManager.MIN_POLICY_RATE, Math.min(DebtManager.MAX_POLICY_RATE, euroRateBefore + spreadBefore));
            dial &= Double.compare(dm.getPolicyRate(), expected) == 0 && Double.compare(dm.getPolicyRate(), tiedBefore) == 0;
            tie &= Double.compare(dm.tieRate(), dm.worldRate("EUR")) == 0 && "EUR".equals(dm.tieMoney());
            money &= within(fx.getMoneyUsd(1), g.getWorldMoneys() == null ? Double.NaN : 1 / g.getWorldMoneys().rate("EUR"), 1e-12);
            out.printf("   month %d: euro US$%.4f, peg %.4f D$ per euro, rate %.4f D$ per US$ (held %.4f), dial %.4f against the euro area's %.4f and the US's %.4f%n",
                    m, euro, fx.getPegPerAnchor(), fx.getRate(), fx.getPegRate(), dm.getPolicyRate(), dm.worldRate("EUR"), dm.worldRate());
        }
        assertTrue("three months on, the peg held (not broken)", held);
        assertTrue("each month the rate is the peg per euro over the euro's US$ price", rate);
        assertTrue("...the euro's price is the world's rate for it", money);
        assertTrue("...the dial is the euro area's rate plus the spread, which is what tiedRate() gives", dial);
        assertTrue("...and tieRate() is the euro area's rate and tieMoney() EUR", tie);

        double per = fx.getPegPerAnchor();
        g.repeg(per * 1.03);
        lines = currencyLines(g);
        String lastLine = lines.isEmpty() ? "" : lines.get(lines.size() - 1);
        assertTrue("a devaluation in the euro's money is logged in it: \"" + lastLine + "\"", lastLine.startsWith("Devalued the peg to ") && lastLine.endsWith(" D$ per €"));
        g.setPegAnchor("GBP");
        lines = currencyLines(g);
        lastLine = lines.isEmpty() ? "" : lines.get(lines.size() - 1);
        assertTrue("moving the anchor under a peg re-pegs at the day's cross and says so: \"" + lastLine + "\"",
                lastLine.startsWith("Re-pegged the D$ to the pound at ") && lastLine.endsWith(" D$ per £") && fx.getPegAnchor() == 3);
        close("...the held rate did not move on the switch", fx.getPegRate(), per * 1.03 / fx.getMoneyUsd(1), 1e-12);
        double poundRateBefore = dm.worldRate("GBP"), spreadNow = dm.getPegSpread();
        InterestAbroadCheck.play(g);
        close("...and the next month's rate is the peg per pound over the pound's price", fx.getRate(), fx.getPegPerAnchor() / fx.getMoneyUsd(3), 1e-12);
        same("...with the dial on the pound's rate as the month began with it", dm.getPolicyRate(), Math.max(DebtManager.MIN_POLICY_RATE,
                Math.min(DebtManager.MAX_POLICY_RATE, poundRateBefore + spreadNow)));
    }

    /* =====================================================================
       10. THE REVIEW'S TWO LOW NOTES (WN4c)
       ===================================================================== */

    static void theReviewsLowNotes() {
        out.println("--- 10. the review's two low notes: the hold's support is the held rate's log over the shadow in every held month; the dial's gap is read at this month's euro ---");
        double exports = .9 * TRADE;
        ForeignAccounts fx = euroPeg(exports, TRADE, DEEP, 0, euroAt(0));
        int months = 36, held = 0, up = 0, down = 0;
        double worst = 0, widest = 0;
        for (int m = 1; m <= months; m++) {
            month(fx, euroAt(m), exports, TRADE);
            if (fx.getRegime() != PEG || !fx.isHeld()) continue;
            held++;
            double support = fx.getHoldFelt();
            worst = Math.max(worst, Math.abs(support - Math.log(fx.getPegRate() / fx.getShadowRate())));
            widest = Math.max(widest, Math.abs(support));
            if (support > 0) up++;
            if (support < 0) down++;
        }
        assertTrue("fixture: a quiet euro peg on a deep vault held all " + months + " months of a euro swinging 6% either side (held " + held + ")", held == months);
        assertTrue("fixture: the hold's support took both signs and was never zero for want of work (" + up + " months up, " + down + " down)", up > 0 && down > 0);
        assertTrue("note 1: in every held month the support is the log of the held rate over the shadow to 1e-9 (worst " + String.format("%.2e", worst)
                + "): the hold lifts the rate by exactly the gap, so a clamp on it would only break the peg", worst <= 1e-9);
        assertTrue("...and it is as wide as the gap the hold paid for, inside the euro's whole swing (ln(1.06/.94)), the widest being " + String.format("%.4f", widest), widest > 0 && widest < Math.log(1.06 / .94));

        month(fx, euroAt(months + 1), exports, TRADE);
        assertTrue("fixture: still a held peg for the dial's month", fx.getRegime() == PEG && fx.isHeld());
        double e0 = euroAt(months + 1), e1 = e0 * 1.10;
        double shadow = fx.getShadowRate(), gapAtE0 = fx.currencyGap();
        fx.setMoneyRates(rates(e1));
        double gapAtDial = fx.currencyGap();
        close("note 2: the month's rates handed, the shadow is still the last reprice's", fx.getShadowRate(), shadow, 1e-15);
        close("...and the dial's gap is that shadow over the held rate at the NEW euro: the peg per euro over " + String.format("%.4f", e1),
                gapAtDial, shadow / (fx.getPegPerAnchor() / e1) - 1, 1e-12);
        close("...so a euro that rose a tenth moved the gap's ratio by a tenth", 1 + gapAtDial, (1 + gapAtE0) * 1.10, 1e-12);
        month(fx, e1, exports, TRADE);
        closeAbs("...and the gap the reprice then measures is against that same held rate, with the shadow the reprice found", fx.getLastPegGap(),
                Math.log(fx.getShadowRate() / (fx.getPegPerAnchor() / e1)), 1e-9);
    }
}
