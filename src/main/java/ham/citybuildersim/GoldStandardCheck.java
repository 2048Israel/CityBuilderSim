package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The gold standard (batch GD3; spec-gold-money 4.3): the D$ on Gold, a mint price that keeps its value when it is entered, the rate held inside the gold points of the
 * centre that moves with gold, the sale paid in gold first and the purchase made into gold, the cover that breaks it, doubt that grows as the cover thins, the central bank
 * defending the gold, the US$'s three dates that open a decision, the notice that stands while one is open, and the save that carries the mint.
 *
 * WHY. Gold is the only regime whose centre is not a number the player typed: it is the mint price over a price the world moves every month, so the rate can be held
 * where it was at a centre that is falling, and the vault that pays for it is the one thing the law counts. A mint struck a digit off would change the D$'s value on the
 * day it is entered; a centre that did not move with gold would hold the rate to a price that is gone; a sale paid out of the dollars would leave the gold the law counts
 * where it was; a cover floor read against the wrong side, or one that also stopped a purchase, would break a city for filling its vault; a doubt that did not thin with
 * the cover would break a fully covered city or never break a bare one; and an answer to a date that did not reach the dial, the notice, the log and the save would be a
 * button that did nothing. Each section says against which constant or getter it is judged, and a fixture CAUSES the condition it asserts: the trade that weakens or
 * strengthens the shadow, the gold bought into the vault and forgotten as a set-up, the money base that puts the cover on the right side of the floor, and a town played
 * to the month the dollar left gold.
 *
 * What this has to prove:
 *   1. ENTERING GOLD KEEPS THE D$'S VALUE: the mint price is the day's rate times gold's US$ price (D$20.6718 an ounce at a rate of 1.00), the mint rate and the centre
 *      are the day's rate; refused where gold has no price; setMintPrice() moves the centre to the typed price over gold's; leaving Gold keeps the gold in the vault.
 *   2. THE GOLD POINTS HOLD THE RATE; GOLD IS PAID FIRST AND BOUGHT: past the weak edge the rate is goldWeakEdge() and the sale comes out of the ounces, the US$ untouched;
 *      past the strong edge it is goldStrongEdge() and the purchase goes into the ounces; inside the points nothing is dealt; a band of GOLD_POINTS deals the same dollars.
 *   3. THE CENTRE MOVES WITH GOLD, FOLLOWING HOLDS IT: the centre each month is the mint price over that month's price, by February 1934 the March 1933 centre times
 *      PAR_1900 / PAR_1934; following keeps it at the mint rate; staying leaves the mint price where it was.
 *   4. THE COVER BREAKS IT: a sale that would leave the cover under COVER_RATIO breaks the month (BreakCause.COVER, Float, where breakLanding() says, nothing sold) and one
 *      that leaves it just over holds; a purchase under the floor holds; the vault's VAULT_FLOOR does not break Gold.
 *   5. DOUBT IS SCALED BY THE COVER: goldHazard() is hazard() at the legal cover, none at a full cover, half way between at the midpoint; counted over seeded twins.
 *   6. DEFEND THE GOLD SETS THE DIAL (a bare DebtManager): goldSpread(), goldRate() under each aim and the autopilot off, dropGoldAim() writing nothing, tiedRate().
 *   7. A TOWN ON GOLD THROUGH 1933 AND 1934 (the full file only): one town with gold in its vault played to March 1933, saved, loaded three times to answer the US$'s
 *      April 1933 date (stay, follow, leave).
 *   8. THE NOTICE: raised the month the date opens, standing while it is open, settled the month after it is answered; none in a city that is not on gold.
 *   9. THE DIAL IN THE TOWN: goldRate() as Game struck it, the inflation rule under that aim, and a break that drops the aim without a decision line.
 *  10. SAVED MID-GOLD: an event open and following the dollar, slots 152-155, the regime and the aim as they were, and the next twelve months to the bit.
 *  11. GD2'S TWO FINDINGS: a price that is NaN is no throw in Game.priceOrNaN(); a money that is gold's code is none of the eight's, and gold gets no share of the trade.
 *  12. LEAN IS THE DEFAULT: a new town has no mint, no event, no gold, and deals none.
 *
 * Money is in thousands, as everywhere; gold is in thousands of ounces, as the vault keeps it.
 */
public class GoldStandardCheck {

    static int fails = 0;
    static final PrintStream out = System.out;

    static final ForeignAccounts.Regime LEAN = ForeignAccounts.Regime.LEAN, FLOAT = ForeignAccounts.Regime.FLOAT, BAND = ForeignAccounts.Regime.BAND,
            PEG = ForeignAccounts.Regime.PEG, ON_GOLD = ForeignAccounts.Regime.GOLD;
    static final ForeignAccounts.GoldEvent LEFT_1933 = ForeignAccounts.GoldEvent.LEFT_1933, PAR_1934 = ForeignAccounts.GoldEvent.PAR_1934,
            WINDOW_1971 = ForeignAccounts.GoldEvent.WINDOW_1971, NO_EVENT = ForeignAccounts.GoldEvent.NONE;

    /** Gold's index in the vault, and the trade a quiet currency settles on, and a vault no fixture of these can empty. */
    static final int GOLD = ForeignAccounts.GOLD;
    static final double TRADE = CurrencyRegimeCheck.TRADE, DEEP = CurrencyRegimeCheck.DEEP;

    /** Gold's price while the statute fixes it at the first par, US$ an ounce: the price the bare currencies are handed unless a section moves it. */
    static final double PRICE = GoldPrice.PAR_1900;

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

    /** Whether a figure is within a share of another: the loops' accumulator. */
    static boolean within(double actual, double expected, double tol) { return Math.abs(actual - expected) <= tol * Math.abs(expected); }

    /** Runs a piece of work with the game's narration sent nowhere. */
    static void quietly(Runnable work) { CurrencyRegimeCheck.quietly(work); }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        CurrencyRegimeCheck.quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        CurrencyRegimeCheck.out = out;

        enteringGoldKeepsTheValue();
        theGoldPointsHoldTheRate();
        theCentreMovesWithGold();
        theCoverBreaksIt();
        doubtIsScaledByTheCover();
        defendTheGoldSetsTheDial();
        aTownOnGoldThroughNineteenThirtyThreeAndFour();
        theDialInTheTown();
        savedMidGold();
        gdTwosTwoFindings();
        leanIsTheDefault();

        out.printf("%nGoldStandardCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       THE FIXTURES
       ===================================================================== */

    /** The eight's rates (VaultMoneysCheck's cross rates) with gold at this price an ounce as the ninth. */
    static double[] rates(double goldUsd) {
        double[] r = new double[ForeignAccounts.MONEYS];
        System.arraycopy(PegAnchorCheck.R, 0, r, 0, PegAnchorCheck.R.length);
        r[GOLD] = goldUsd;
        return r;
    }

    /**
     * A settled currency on this trade whose vault holds usdK in dollars and goldK in gold (bought by name at PAR_1900, then forgotten as a set-up and not a deal, as the
     * vault of every fixture is), put on Gold at the rate of the day and with the money base at which the gold covers it this many times.
     */
    static ForeignAccounts onGold(double exports, double imports, double usdK, double goldK, long seed, double cover) {
        ForeignAccounts fx = CurrencyRegimeCheck.book(exports, imports, usdK, seed, LEAN);
        fx.setMoneyRates(rates(PRICE));
        fx.buyReserves(goldK * fx.getRate(), "XAU");
        fx.forgetDealing();
        fx.setRegime(ON_GOLD);
        fx.setMoneyBase(fx.goldAtMint() / cover);
        return fx;
    }

    /** One month of this trade with gold at this price handed first. */
    static void month(ForeignAccounts fx, double goldUsd, double exports, double imports) {
        fx.setMoneyRates(rates(goldUsd));
        CurrencyRegimeCheck.month(fx, exports, imports);
    }

    /** A band of the gold points' width centred on the rate of the day, on a deep vault of dollars: the twin whose rule Gold's hold is. */
    static ForeignAccounts bandOfThePoints() {
        ForeignAccounts b = CurrencyRegimeCheck.book(TRADE, TRADE, DEEP, 0, LEAN);
        b.setRegime(BAND);
        b.setBand(b.getRate(), 0, ForeignAccounts.GOLD_POINTS);
        return b;
    }

    /* =====================================================================
       1. ENTERING GOLD KEEPS THE D$'S VALUE
       ===================================================================== */

    static void enteringGoldKeepsTheValue() {
        out.println("--- 1. entering Gold keeps the D$'s value: the mint price is the day's rate times gold's price ---");
        ForeignAccounts fx = CurrencyRegimeCheck.book(TRADE, TRADE, DEEP, 0, LEAN);
        fx.setMoneyRates(rates(PRICE));
        double rate = fx.getRate();
        assertTrue("fixture: a settled currency on Lean at a rate of 1.00, gold rated at GoldPrice.PAR_1900, no mint struck", Math.abs(rate - 1) < 1e-9 && fx.hasRate(GOLD)
                && fx.getRegime() == LEAN && fx.getMintPrice() == 0 && fx.getMintRate() == 0 && !fx.isMintFollowing() && fx.getGoldEvent() == NO_EVENT);
        fx.setRegime(ON_GOLD);
        assertTrue("setRegime(GOLD) enters Gold", fx.getRegime() == ON_GOLD);
        same("the mint price is the day's rate times gold's US$ price", fx.getMintPrice(), rate * PRICE);
        same("...the mint rate is the day's rate", fx.getMintRate(), rate);
        close("...and the centre is the day's rate: goldCentre() is getRate()", fx.goldCentre(), rate, 1e-12);
        assertTrue("at a rate of 1.00 the mint price is D$20.6718 an ounce (480 / 23.22 US$ to the ounce: the US$'s own 1.504631 g of fine gold)",
                Math.round(fx.getMintPrice() * 1e4) / 1e4 == 20.6718);
        close("the weak edge is the centre plus GOLD_POINTS", fx.goldWeakEdge() / fx.goldCentre() - 1, ForeignAccounts.GOLD_POINTS, 1e-9);
        close("...and the strong edge the centre less GOLD_POINTS", 1 - fx.goldStrongEdge() / fx.goldCentre(), ForeignAccounts.GOLD_POINTS, 1e-9);
        assertTrue("entering Gold leaves the mint not following and no date open", !fx.isMintFollowing() && fx.getGoldEvent() == NO_EVENT);

        // Refused where gold has no price: never handed any rate, handed the eight alone, or handed a price that is no number.
        ForeignAccounts never = CurrencyRegimeCheck.book(TRADE, TRADE, DEEP, 0, LEAN);
        never.setRegime(ON_GOLD);
        assertTrue("a currency never handed a gold price cannot enter Gold: setRegime(GOLD) is refused, it stays on Lean and no mint is struck",
                never.getRegime() == LEAN && never.getMintPrice() == 0 && never.getMintRate() == 0 && !never.hasRate(GOLD));
        ForeignAccounts eight = CurrencyRegimeCheck.book(TRADE, TRADE, DEEP, 0, LEAN);
        eight.setMoneyRates(Arrays.copyOf(rates(PRICE), WorldMoneys.EIGHT.size()));
        eight.setRegime(ON_GOLD);
        assertTrue("...nor one handed the eight's rates and not gold's", eight.getRegime() == LEAN && !eight.hasRate(GOLD) && eight.getMintPrice() == 0);
        ForeignAccounts nan = CurrencyRegimeCheck.book(TRADE, TRADE, DEEP, 0, LEAN);
        nan.setMoneyRates(rates(Double.NaN));
        nan.setRegime(ON_GOLD);
        assertTrue("...nor one handed a gold price that is no number", nan.getRegime() == LEAN && !nan.hasRate(GOLD) && nan.getMintPrice() == 0);
        nan.setMoneyRates(rates(PRICE));
        nan.setRegime(ON_GOLD);
        assertTrue("...and the refusal cleared nothing: once the price is handed it enters", nan.getRegime() == ON_GOLD && nan.getMintPrice() > 0);

        // setMintPrice(): the typed price over gold's is the centre.
        double typed = 30;
        fx.setMintPrice(typed);
        close("setMintPrice(30) under Gold: the mint price is the typed price", fx.getMintPrice(), typed, 1e-12);
        close("...the centre is the typed price over gold's price", fx.goldCentre(), typed / PRICE, 1e-12);
        close("...and the mint rate is that centre", fx.getMintRate(), typed / PRICE, 1e-12);
        double struck = fx.getMintPrice();
        for (double nonsense : new double[] { 0, -5, Double.NaN, Double.POSITIVE_INFINITY }) fx.setMintPrice(nonsense);
        same("...a price that is zero, negative or no number changes nothing", fx.getMintPrice(), struck);
        ForeignAccounts lean = CurrencyRegimeCheck.book(TRADE, TRADE, DEEP, 0, LEAN);
        lean.setMoneyRates(rates(PRICE));
        lean.setMintPrice(typed);
        lean.openGoldEvent(LEFT_1933);
        assertTrue("outside Gold setMintPrice() does nothing, and no date can be opened", lean.getMintPrice() == 0 && lean.getMintRate() == 0 && lean.getGoldEvent() == NO_EVENT);

        // The mint that follows the dollar stops following when the player types a price; leaving Gold clears the following and the date, and keeps the gold.
        ForeignAccounts held = onGold(TRADE, TRADE, 1_000, 20_000, 0, 1.0);
        double ounces = held.getHolding(GOLD);
        assertTrue("fixture: a currency on Gold whose vault holds gold bought by name", ounces > 0 && held.getRegime() == ON_GOLD);
        held.openGoldEvent(LEFT_1933);
        held.answerGoldEvent(ForeignAccounts.GoldChoice.FOLLOW);
        assertTrue("answering FOLLOW closes the date and keeps the mint following the dollar", held.getGoldEvent() == NO_EVENT && held.isMintFollowing());
        held.setMintPrice(25);
        assertTrue("typing a mint price stops the mint following the dollar", !held.isMintFollowing());
        held.answerGoldEvent(ForeignAccounts.GoldChoice.FOLLOW);
        assertTrue("an answer with no date open does nothing", !held.isMintFollowing() && held.getGoldEvent() == NO_EVENT);
        held.openGoldEvent(PAR_1934);
        held.answerGoldEvent(ForeignAccounts.GoldChoice.FOLLOW);
        held.openGoldEvent(WINDOW_1971);
        assertTrue("fixture: following the dollar with a date open", held.isMintFollowing() && held.getGoldEvent() == WINDOW_1971);
        held.setRegime(LEAN);
        assertTrue("leaving Gold clears the following and the open date", held.getRegime() == LEAN && !held.isMintFollowing() && held.getGoldEvent() == NO_EVENT);
        same("...and keeps the gold in the vault: the ounces are what they were", held.getHolding(GOLD), ounces);
        same("...worth gold's price an ounce", held.getHoldingUsd(GOLD), ounces * PRICE);
    }

    /* =====================================================================
       2. THE GOLD POINTS HOLD THE RATE; GOLD IS PAID FIRST AND BOUGHT
       ===================================================================== */

    static void theGoldPointsHoldTheRate() {
        out.println("--- 2. the gold points hold the rate: the sale is paid in gold, the purchase made into gold, the US$ untouched; a band of the same width deals the same ---");
        final double usdK = 40_000, goldK = 60_000;
        final int months = 12;

        // The weak edge: a deficit (exports .8 of imports) weakens the shadow past it.
        ForeignAccounts weak = onGold(.8 * TRADE, TRADE, usdK, goldK, 0, 1.0);
        ForeignAccounts bandWeak = bandOfThePoints();
        ForeignAccounts bandDeficit = CurrencyRegimeCheck.book(.8 * TRADE, TRADE, DEEP, 0, LEAN);
        bandDeficit.setRegime(BAND);
        bandDeficit.setBand(bandDeficit.getRate(), 0, ForeignAccounts.GOLD_POINTS);
        assertTrue("fixture: a gold currency and a band of GOLD_POINTS on the same centre and trade, the edges the same", weak.getRegime() == ON_GOLD
                && bandDeficit.getRegime() == BAND && within(bandDeficit.bandWeakEdge(), weak.goldWeakEdge(), 1e-12) && within(bandDeficit.bandStrongEdge(), weak.goldStrongEdge(), 1e-12)
                && bandWeak.getRegime() == BAND);
        int pastEdge = 0, atEdge = 0, usdKept = 0, goldPaid = 0, sold = 0, sameAsBand = 0, held = 0;
        double worstOunces = 0, dealt = 0;
        for (int m = 0; m < months; m++) {
            double usdBefore = weak.getHolding(0), ouncesBefore = weak.getHolding(GOLD);
            month(weak, PRICE, .8 * TRADE, TRADE);
            CurrencyRegimeCheck.month(bandDeficit, .8 * TRADE, TRADE);
            if (weak.getShadowRate() > weak.goldWeakEdge()) pastEdge++;
            if (Double.compare(weak.getRate(), weak.goldWeakEdge()) == 0) atEdge++;
            if (Double.compare(weak.getHolding(0), usdBefore) == 0) usdKept++;
            double paid = (ouncesBefore - weak.getHolding(GOLD)) * PRICE;
            worstOunces = Math.max(worstOunces, Math.abs(paid - weak.getDefenceUsd()));
            if (within(paid, weak.getDefenceUsd(), 1e-9)) goldPaid++;
            if (weak.getDefenceUsd() > 0) sold++;
            if (within(weak.getDefenceUsd(), bandDeficit.getDefenceUsd(), 1e-9)) sameAsBand++;
            if (weak.getRegime() == ON_GOLD) held++;
            dealt += weak.getDefenceUsd();
        }
        out.printf("   %d months of a deficit: the gold currency sold US$%,.1fk, all out of its US$%,.0fk of gold (the first month's US$%,.1fk), the band US$%,.1fk%n", months, dealt, goldK,
                bandDeficit.getDefenceUsdLifetime() > 0 ? bandDeficit.getDefenceUsdLifetime() / months : 0, bandDeficit.getDefenceUsdLifetime());
        assertTrue("fixture: the shadow stands past the weak edge in every month (" + pastEdge + " of " + months + ") and the currency stays on Gold", pastEdge == months && held == months);
        assertTrue("past the weak edge the rate is goldWeakEdge() to the bit, every month (" + atEdge + " of " + months + ")", atEdge == months);
        assertTrue("...the hold sold dollars in every month (" + sold + " of " + months + ")", sold == months);
        assertTrue("...paid out of gold: the ounces fell by getDefenceUsd() over gold's price, every month (worst gap US$" + String.format("%.2e", worstOunces) + "k)", goldPaid == months);
        assertTrue("...and the US$ balance was untouched, to the bit, every month (" + usdKept + " of " + months + ")", usdKept == months);
        assertTrue("a band of GOLD_POINTS on the same centre sold the same dollars, every month (" + sameAsBand + " of " + months + "): Gold's hold is the band's rule", sameAsBand == months);

        // The strong edge: a surplus (exports 1.2 of imports) strengthens the shadow past it.
        ForeignAccounts strong = onGold(1.2 * TRADE, TRADE, usdK, goldK, 0, 1.0);
        ForeignAccounts bandSurplus = CurrencyRegimeCheck.book(1.2 * TRADE, TRADE, DEEP, 0, LEAN);
        bandSurplus.setRegime(BAND);
        bandSurplus.setBand(bandSurplus.getRate(), 0, ForeignAccounts.GOLD_POINTS);
        pastEdge = atEdge = usdKept = goldPaid = sameAsBand = 0;
        int bought = 0, soldAny = 0;
        double buys = 0;
        for (int m = 0; m < months; m++) {
            double usdBefore = strong.getHolding(0), ouncesBefore = strong.getHolding(GOLD);
            month(strong, PRICE, 1.2 * TRADE, TRADE);
            CurrencyRegimeCheck.month(bandSurplus, 1.2 * TRADE, TRADE);
            if (strong.getShadowRate() < strong.goldStrongEdge()) pastEdge++;
            if (Double.compare(strong.getRate(), strong.goldStrongEdge()) == 0) atEdge++;
            if (Double.compare(strong.getHolding(0), usdBefore) == 0) usdKept++;
            if (within((strong.getHolding(GOLD) - ouncesBefore) * PRICE, strong.getEdgeBoughtUsd(), 1e-9)) goldPaid++;
            if (strong.getEdgeBoughtUsd() > 0) bought++;
            if (strong.getDefenceUsd() > 0) soldAny++;
            if (within(strong.getEdgeBoughtUsd(), bandSurplus.getEdgeBoughtUsd(), 1e-9)) sameAsBand++;
            buys += strong.getEdgeBoughtUsd();
        }
        out.printf("   %d months of a surplus: the gold currency bought US$%,.1fk into gold, the band US$%,.1fk%n", months, buys, bandSurplus.getEdgeBoughtUsdLifetime());
        assertTrue("fixture: the shadow stands past the strong edge in every month (" + pastEdge + " of " + months + ")", pastEdge == months);
        assertTrue("past the strong edge the rate is goldStrongEdge() to the bit, every month (" + atEdge + " of " + months + ")", atEdge == months);
        assertTrue("...the hold bought dollars in every month and sold in none (" + bought + ", " + soldAny + ")", bought == months && soldAny == 0);
        assertTrue("...into gold: the ounces rose by getEdgeBoughtUsd() over gold's price, every month (" + goldPaid + " of " + months + ")", goldPaid == months);
        assertTrue("...and the US$ balance was untouched, to the bit, every month (" + usdKept + " of " + months + ")", usdKept == months);
        assertTrue("a band of GOLD_POINTS on the same centre bought the same dollars, every month (" + sameAsBand + " of " + months + ")", sameAsBand == months);

        // Inside the points: a small deficit and a small surplus put the shadow between the edges, and nothing is dealt.
        for (double exports : new double[] { .99 * TRADE, 1.01 * TRADE }) {
            ForeignAccounts inside = onGold(exports, TRADE, usdK, goldK, 0, 1.0);
            int quiet = 0, between = 0, free = 0;
            for (int m = 0; m < months; m++) {
                double usdBefore = inside.getHolding(0), ouncesBefore = inside.getHolding(GOLD);
                month(inside, PRICE, exports, TRADE);
                if (inside.getShadowRate() < inside.goldWeakEdge() && inside.getShadowRate() > inside.goldStrongEdge()) between++;
                if (inside.getDefenceUsd() == 0 && inside.getEdgeBoughtUsd() == 0 && Double.compare(inside.getHolding(0), usdBefore) == 0
                        && Double.compare(inside.getHolding(GOLD), ouncesBefore) == 0) quiet++;
                if (Double.compare(inside.getRate(), inside.getShadowRate()) == 0 && !inside.isHeld()) free++;
            }
            assertTrue("exports " + exports / TRADE + " of imports: the shadow stands between the edges in every month (" + between + " of " + months + ")", between == months);
            assertTrue("...nothing is dealt in any, the vault is where it was to the bit (" + quiet + " of " + months + "), and the rate is the shadow's (" + free + ")",
                    quiet == months && free == months);
        }
    }

    /* =====================================================================
       3. THE CENTRE MOVES WITH GOLD; FOLLOWING HOLDS IT
       ===================================================================== */

    /** The months of the US$'s leaving gold: March 1933, the last month at the first par, to February 1934, the first at the second. */
    static final int FIRST = GoldPrice.FLOAT_START - 1, LAST = GoldPrice.FLOAT_START + GoldPrice.FLOAT_STEPS;

    static void theCentreMovesWithGold() {
        out.println("--- 3. the centre moves with gold: the mint price over the month's price; following holds it at the mint rate, staying leaves the mint price ---");
        // A deep vault of gold, so the sales that hold the rate at a falling centre are small beside it, and a cover of 1 to start from.
        final double goldK = 5_000_000;
        ForeignAccounts stay = onGold(TRADE, TRADE, 1_000, goldK, 0, 1.0);
        ForeignAccounts follow = onGold(TRADE, TRADE, 1_000, goldK, 0, 1.0);
        ForeignAccounts alone = onGold(TRADE, TRADE, 1_000, goldK, 0, 1.0);
        double mint = stay.getMintPrice(), mintRate = stay.getMintRate();
        double centreMarch = stay.goldCentre();
        assertTrue("fixture: three gold currencies struck alike in March 1933 (month " + FIRST + "), the price the statute's PAR_1900", stay.getRegime() == ON_GOLD
                && Double.compare(follow.getMintPrice(), mint) == 0 && Double.compare(alone.getMintPrice(), mint) == 0 && GoldPrice.official(FIRST) == PRICE);

        int centreRight = 0, mintStays = 0, inPoints = 0, fell = 0, months = 0, followCentre = 0, followMint = 0, stayAnswered = 0;
        double before = Double.NaN, febCentre = Double.NaN, followFeb = Double.NaN;
        for (int t = FIRST; t <= LAST; t++) {
            double price = GoldPrice.official(t);
            month(alone, price, TRADE, TRADE);
            month(stay, price, TRADE, TRADE);
            if (t == GoldPrice.FLOAT_START) {
                // April 1933: the dollar leaves gold, the date opens for the city on gold, and is answered after the month.
                stay.openGoldEvent(LEFT_1933);
                follow.openGoldEvent(LEFT_1933);
            }
            month(follow, price, TRADE, TRADE);
            if (t == GoldPrice.FLOAT_START) {
                stay.answerGoldEvent(ForeignAccounts.GoldChoice.STAY);
                follow.answerGoldEvent(ForeignAccounts.GoldChoice.FOLLOW);
                if (stay.getGoldEvent() == NO_EVENT && follow.getGoldEvent() == NO_EVENT) stayAnswered++;
                close("answering FOLLOW re-strikes the mint price at once: the mint rate times the month's price", follow.getMintPrice(), mintRate * price, 1e-12);
            }
            months++;
            if (within(stay.goldCentre(), mint / price, 1e-12)) centreRight++;
            if (Double.compare(stay.getMintPrice(), mint) == 0 && Double.compare(alone.getMintPrice(), mint) == 0) mintStays++;
            if (stay.getRate() <= stay.goldWeakEdge() * (1 + 1e-12) && stay.getRate() >= stay.goldStrongEdge() * (1 - 1e-12)) inPoints++;
            if (t > FIRST && t < LAST && stay.goldCentre() < before) fell++;
            if (t > GoldPrice.FLOAT_START && within(follow.goldCentre(), mintRate, 1e-12)) followCentre++;
            if (t > GoldPrice.FLOAT_START && within(follow.getMintPrice(), mintRate * price, 1e-12)) followMint++;
            before = stay.goldCentre();
            if (t == LAST) {
                febCentre = stay.goldCentre();
                followFeb = follow.goldCentre();
            }
        }
        out.printf("   March 1933's centre %.6f, February 1934's %.6f (the D$ %.1f%% dearer against the US$); following, February's is %.6f%n", centreMarch, febCentre,
                100 * (GoldPrice.PAR_1934 / GoldPrice.PAR_1900 - 1), followFeb);
        assertTrue("every month from March 1933 to February 1934 the centre is the mint price over that month's price (" + centreRight + " of " + months + ")", centreRight == months);
        assertTrue("...the mint price itself never moves, answered STAY or never answered, to the bit (" + mintStays + " of " + months + ")", mintStays == months);
        assertTrue("...and the rate is held inside the gold points of the centre, every month (" + inPoints + " of " + months + ")", inPoints == months);
        assertTrue("...the centre falls in each of the " + (GoldPrice.FLOAT_STEPS) + " months the price climbs (" + fell + ")", fell == GoldPrice.FLOAT_STEPS);
        close("by February 1934 the centre is March 1933's times PAR_1900 / PAR_1934", febCentre, centreMarch * GoldPrice.PAR_1900 / GoldPrice.PAR_1934, 1e-12);
        close("...the D$ 69.3% dearer: PAR_1934 over PAR_1900 less one", GoldPrice.PAR_1934 / GoldPrice.PAR_1900 - 1, .693, 1e-3);
        close("...which is the centre's fall: March's over February's centre less one", centreMarch / febCentre - 1, GoldPrice.PAR_1934 / GoldPrice.PAR_1900 - 1, 1e-12);
        assertTrue("answering STAY closes the date (and FOLLOW does too), and the mint stops following", stayAnswered == 1 && !stay.isMintFollowing() && follow.isMintFollowing());
        assertTrue("the twin that answered FOLLOW keeps the centre at the mint rate in every month after (" + followCentre + " of " + (LAST - GoldPrice.FLOAT_START) + ")",
                followCentre == LAST - GoldPrice.FLOAT_START && followMint == LAST - GoldPrice.FLOAT_START);
        close("...so in February 1934 its centre is the mint rate, not March's times the ratio", followFeb, mintRate, 1e-12);
        same("the twin never asked has the same centre as the one that answered STAY, to the bit", alone.goldCentre(), stay.goldCentre());
        same("...and the same rate", alone.getRate(), stay.getRate());
    }

    /* =====================================================================
       4. THE COVER BREAKS IT
       ===================================================================== */

    static void theCoverBreaksIt() {
        out.println("--- 4. the cover breaks it: a sale that would leave it under COVER_RATIO breaks the month, a purchase never does, the vault's floor is not asked ---");
        final double deficit = .8 * TRADE;
        // The sale one month of this deficit asks does not depend on the vault: a probe with a full cover reads it.
        ForeignAccounts probe = onGold(deficit, TRADE, 40_000, 5_000_000, 0, 1.0);
        month(probe, PRICE, deficit, TRADE);
        double sale = probe.getDefenceUsd();
        assertTrue("fixture: a deficit month's hold sells US$" + String.format("%,.1fk", sale) + " with a full cover", sale > 0 && probe.getRegime() == ON_GOLD);

        // Gold twenty times that sale, so the sale is a twentieth of the gold; the money base puts the cover after the sale on either side of the law's.
        final double goldK = 20 * sale;
        ForeignAccounts shape = onGold(deficit, TRADE, 40_000, goldK, 0, 1.0);
        double ounces = shape.getHolding(GOLD), mintPrice = shape.getMintPrice();
        double left = ounces - sale / PRICE;
        double under = left * mintPrice / (ForeignAccounts.COVER_RATIO * .99), over = left * mintPrice / (ForeignAccounts.COVER_RATIO * 1.01);

        ForeignAccounts breaks = onGold(deficit, TRADE, 40_000, goldK, 0, 1.0), holds = onGold(deficit, TRADE, 40_000, goldK, 0, 1.0);
        breaks.setMoneyBase(under);
        holds.setMoneyBase(over);
        breaks.openGoldEvent(LEFT_1933);
        double usdBefore = breaks.getHolding(0), ouncesBefore = breaks.getHolding(GOLD);
        assertTrue("fixture: before the month both covers stand over the law's (" + String.format("%.4f", breaks.goldCover(under)) + " and " + String.format("%.4f", holds.goldCover(over))
                + " against " + ForeignAccounts.COVER_RATIO + "), and the sale would leave one 1% under it and the other 1% over", breaks.goldCover(under) > ForeignAccounts.COVER_RATIO
                && holds.goldCover(over) > ForeignAccounts.COVER_RATIO && breaks.getRegime() == ON_GOLD && holds.getRegime() == ON_GOLD
                && within(breaks.goldCover(under) * (1 - sale / PRICE / ounces), ForeignAccounts.COVER_RATIO * .99, 1e-9)
                && within(holds.goldCover(over) * (1 - sale / PRICE / ounces), ForeignAccounts.COVER_RATIO * 1.01, 1e-9));
        month(breaks, PRICE, deficit, TRADE);
        month(holds, PRICE, deficit, TRADE);
        assertTrue("a sale that would leave the cover under COVER_RATIO breaks the month: BreakCause.COVER, and the regime is Float", breaks.brokeThisMonth()
                && breaks.getLastBreakCause() == ForeignAccounts.BreakCause.COVER && breaks.getRegime() == FLOAT && breaks.getBreaks() == 1);
        assertTrue("...nothing was sold: no dollars dealt, the ounces and the US$ where they were, to the bit", breaks.getDefenceUsd() == 0
                && Double.compare(breaks.getHolding(GOLD), ouncesBefore) == 0 && Double.compare(breaks.getHolding(0), usdBefore) == 0);
        close("...the rate landed where breakLanding() says: the twin that held would have broken at " + String.format("%.6f", holds.breakLanding()), breaks.getRate(), holds.breakLanding(), 1e-12);
        assertTrue("...past the shadow on the weak side, the shadow the gold held it under", breaks.getRate() > breaks.getLastBreakShadow() && breaks.getLastBreakShadow() > breaks.goldWeakEdge());
        assertTrue("...and the mint stops following and the open date closes with the gold", !breaks.isMintFollowing() && breaks.getGoldEvent() == NO_EVENT);
        assertTrue("the twin whose sale leaves the cover just over COVER_RATIO holds: Gold, the rate at the weak edge, no break", !holds.brokeThisMonth() && holds.getRegime() == ON_GOLD
                && holds.getLastBreakCause() == ForeignAccounts.BreakCause.NONE && Double.compare(holds.getRate(), holds.goldWeakEdge()) == 0);
        close("...it sold what the probe sold, out of the gold", holds.getDefenceUsd(), sale, 1e-9);
        assertTrue("...and the cover it is left with is over the law's (" + String.format("%.4f", holds.goldCover(over)) + ")", holds.goldCover(over) >= ForeignAccounts.COVER_RATIO);

        // A purchase never breaks it, whatever the cover.
        ForeignAccounts buys = onGold(1.2 * TRADE, TRADE, 40_000, 60_000, 0, 1.0);
        buys.setMoneyBase(buys.goldAtMint() / (ForeignAccounts.COVER_RATIO / 4));
        assertTrue("fixture: a cover far under COVER_RATIO (" + String.format("%.3f", buys.goldCover(buys.goldAtMint() / (ForeignAccounts.COVER_RATIO / 4))) + ")",
                buys.goldCover(buys.goldAtMint() / (ForeignAccounts.COVER_RATIO / 4)) < ForeignAccounts.COVER_RATIO / 2);
        double ouncesBought = buys.getHolding(GOLD);
        month(buys, PRICE, 1.2 * TRADE, TRADE);
        assertTrue("a purchase at the strong edge with the cover under the floor holds: Gold, the rate at the strong edge, gold bought, no break", !buys.brokeThisMonth()
                && buys.getRegime() == ON_GOLD && Double.compare(buys.getRate(), buys.goldStrongEdge()) == 0 && buys.getEdgeBoughtUsd() > 0 && buys.getHolding(GOLD) > ouncesBought);

        // The vault's floor (VAULT_FLOOR months of imports) does not break Gold: a vault under it with a full cover holds, the same vault on a peg breaks.
        // A vault of four fifths of one month of imports: thin, so that the shadow it weakens (the import cover is a term of it) asks a sale, and the gold holds enough to pay it.
        final double thinK = .8 * CurrencyRegimeCheck.book(TRADE, TRADE, 0, 0, LEAN).vaultFloorUsd();
        ForeignAccounts thin = onGold(TRADE, TRADE, 0, thinK, 0, 3.0);
        ForeignAccounts peg = CurrencyRegimeCheck.book(TRADE, TRADE, thinK, 0, PEG);
        assertTrue("fixture: a vault of US$" + String.format("%,.0fk", thin.getReservesUsd()) + " under one month of imports (US$" + String.format("%,.0fk", thin.vaultFloorUsd())
                + "), the gold's cover 3, and the same vault pegged", thin.getReservesUsd() < ForeignAccounts.VAULT_FLOOR * thin.vaultFloorUsd() && thin.goldCover(thin.goldAtMint() / 3.0) > 1
                && peg.getReservesUsd() < peg.vaultFloorUsd() && peg.getRegime() == PEG);
        month(thin, PRICE, TRADE, TRADE);
        month(peg, PRICE, TRADE, TRADE);
        assertTrue("the peg on that vault breaks for want of it: BreakCause.VAULT", peg.brokeThisMonth() && peg.getLastBreakCause() == ForeignAccounts.BreakCause.VAULT);
        assertTrue("...the gold on it holds, and sold (US$" + String.format("%,.1fk", thin.getDefenceUsd()) + ") out of its gold", !thin.brokeThisMonth() && thin.getRegime() == ON_GOLD
                && thin.getDefenceUsd() > 0 && thin.getReservesUsd() < thin.vaultFloorUsd());
    }

    /* =====================================================================
       5. DOUBT IS SCALED BY THE COVER
       ===================================================================== */

    /** How many seeded currencies and months the doubt is counted over. */
    static final int SEEDS = 100, MONTHS = 12;

    /** The legal cover a sale does not break: just over COVER_RATIO, by more than a month's sale of this deep a vault takes off it. */
    static final double LEGAL = ForeignAccounts.COVER_RATIO * 1.001;

    /** The doubt over SEEDS seeded gold currencies held at a gap of about a quarter above the centre, each kept at this cover: breaks by doubt, by cover, the expected and its sd, the months' hazard range. */
    static double[] doubtRun(double cover) {
        int doubt = 0, byCover = 0;
        double expected = 0, variance = 0, hMin = Double.POSITIVE_INFINITY, hMax = 0, months = 0;
        for (int s = 0; s < SEEDS; s++) {
            ForeignAccounts fx = onGold(TRADE, TRADE, 40_000, 5e7, CurrencyRegimeCheck.SEED + s, cover);
            fx.setMintPrice(Math.exp(-.25) * PRICE);   // a centre a quarter under the shadow's rate of 1: the D$ stands at a gap of about +.25 from it
            for (int m = 0; m < MONTHS && fx.getRegime() == ON_GOLD; m++) {
                fx.setMoneyBase(fx.goldAtMint() / cover);
                month(fx, PRICE, TRADE, TRADE);
                double h = fx.getLastHazard();
                expected += h;
                variance += h * (1 - h);
                hMin = Math.min(hMin, h);
                hMax = Math.max(hMax, h);
                months++;
                if (fx.brokeThisMonth()) {
                    if (fx.getLastBreakCause() == ForeignAccounts.BreakCause.DOUBT) doubt++;
                    else if (fx.getLastBreakCause() == ForeignAccounts.BreakCause.COVER) byCover++;
                }
            }
        }
        return new double[] { doubt, byCover, expected, Math.sqrt(variance), hMin, hMax, months };
    }

    static void doubtIsScaledByTheCover() {
        out.println("--- 5. doubt is scaled by the cover: the peg's chance at the legal cover, none at a full one ---");
        double gap = ForeignAccounts.BREAK_GAP + .1;
        double peg = ForeignAccounts.hazard(gap);
        assertTrue("fixture: a gap of " + gap + " has a hazard of several percent a month (" + String.format("%.4f", peg) + ")", peg > .03 && peg < .1);
        same("goldHazard(gap, COVER_RATIO) is hazard(gap): the peg's own chance at the legal cover", ForeignAccounts.goldHazard(gap, ForeignAccounts.COVER_RATIO), peg);
        same("...and under the legal cover too", ForeignAccounts.goldHazard(gap, ForeignAccounts.COVER_RATIO / 2), peg);
        same("...and at no cover at all", ForeignAccounts.goldHazard(gap, 0), peg);
        same("goldHazard(gap, 1) is nothing: a full cover never doubts", ForeignAccounts.goldHazard(gap, 1), 0);
        same("...nor over a full one", ForeignAccounts.goldHazard(gap, 5), 0);
        close("half way between the legal and the full cover it is half of the peg's", ForeignAccounts.goldHazard(gap, (1 + ForeignAccounts.COVER_RATIO) / 2), peg / 2, 1e-12);
        same("a gap under the centre counts as none: goldHazard(-.3, COVER_RATIO) is BREAK_BASE", ForeignAccounts.goldHazard(-.3, ForeignAccounts.COVER_RATIO), ForeignAccounts.BREAK_BASE);

        double mid = (1 + LEGAL) / 2;
        double[] full = doubtRun(1.0), half = doubtRun(mid), legal = doubtRun(LEGAL);
        out.printf("   %d seeded currencies, %d months, the centre a quarter under the shadow: by doubt, a full cover %d (expected %.1f), the midpoint's %d (expected %.1f), the legal"
                + " cover's %d (expected %.1f, sd %.1f); by cover %d, %d, %d; the legal one's hazard %.3f to %.3f over %.0f months%n", SEEDS, MONTHS, (int) full[0], full[2],
                (int) half[0], half[2], (int) legal[0], legal[2], legal[3], (int) full[1], (int) half[1], (int) legal[1], legal[4], legal[5], legal[6]);
        assertTrue("fixture: held at a gap whose hazard is several percent a month, on the legal twins (a month's hazard averages " + String.format("%.3f", legal[2] / legal[6]) + ", the largest " + String.format("%.3f", legal[5]) + ")",
                legal[2] / legal[6] > .02 && legal[2] / legal[6] < .1 && legal[5] < .15 && legal[6] > SEEDS * MONTHS / 2);
        assertTrue("fixture: none of the three broke by the cover: the sales are small beside the gold, only doubt is counted (" + (int) (full[1] + half[1] + legal[1]) + ")", full[1] + half[1] + legal[1] == 0);
        assertTrue("the currencies at a full cover never break by doubt, and their hazard is nothing every month (" + (int) full[0] + ", the largest " + full[5] + ")", full[0] == 0 && full[5] == 0);
        assertTrue("the ones at the legal cover do (" + (int) legal[0] + " of " + SEEDS + "), as many as the hazard each month puts them at (within four sd of " + String.format("%.1f", legal[2]) + ")",
                legal[0] > 0 && Math.abs(legal[0] - legal[2]) <= 4 * legal[3]);
        assertTrue("the ones half way between break as often as half the legal chance puts them at (" + (int) half[0] + " against " + String.format("%.1f", half[2]) + ")",
                half[0] > 0 && Math.abs(half[0] - half[2]) <= 4 * Math.max(1, Math.sqrt(half[2])));
        assertTrue("...fewer than the legal ones and more than the full ones (" + (int) full[0] + " < " + (int) half[0] + " < " + (int) legal[0] + ")", full[0] < half[0] && half[0] < legal[0]);
    }

    /* =====================================================================
       6. DEFEND THE GOLD SETS THE DIAL
       ===================================================================== */

    static void defendTheGoldSetsTheDial() {
        out.println("--- 6. Defend the gold sets the dial: a bare DebtManager, the spread the cover asks, the aims, and the aim that goes with a break ---");
        final double floor = ForeignAccounts.COVER_RATIO, from = DebtManager.GOLD_DEFENCE_FROM, defence = DebtManager.GOLD_DEFENCE;
        same("goldSpread() is nothing at GOLD_DEFENCE_FROM", DebtManager.goldSpread(from), 0);
        same("...and over it", DebtManager.goldSpread(from + 1), 0);
        same("...and with the gold at infinity over a base of none", DebtManager.goldSpread(Double.POSITIVE_INFINITY), 0);
        same("goldSpread() is GOLD_DEFENCE at COVER_RATIO", DebtManager.goldSpread(floor), defence);
        same("...and under it", DebtManager.goldSpread(floor / 2), defence);
        same("...and with no cover at all", DebtManager.goldSpread(0), defence);
        close("half way between the two it is half of GOLD_DEFENCE", DebtManager.goldSpread((from + floor) / 2), defence / 2, 1e-12);
        boolean rises = true;
        double last = -1;
        for (double c = from; c >= floor - 1e-12; c -= (from - floor) / 8) {
            double spread = DebtManager.goldSpread(c);
            rises &= spread > last || (last < 0 && spread == 0);
            last = spread;
        }
        assertTrue("it rises as the cover falls between: each step of an eighth of the way down asks more than the one before", rises && last == defence);
        assertTrue("GOLD_DEFENCE_FROM is twice the legal floor: nothing asked at twice the floor", from == 2 * floor);

        DebtManager dm = new DebtManager();
        DecisionLog log = new DecisionLog();
        dm.recordTo(log);
        dm.setWorldRate(.04);
        boolean[] tied = { true };
        String[] code = { "USD" };
        dm.setTiedSource(() -> tied[0]);
        dm.setTieMoneySource(() -> code[0]);
        dm.setPegSpread(.005);
        dm.setAutopilot(true);
        dm.setAim(DebtManager.Aim.GOLD);
        double cover = (from + floor) / 2, gap = .2;
        double bound = Math.max(DebtManager.MIN_POLICY_RATE, Math.min(DebtManager.MAX_POLICY_RATE, dm.tieRate() + DebtManager.goldSpread(cover)));
        same("with the autopilot aiming at the gold, goldRate() is the tie rate plus the spread the cover asks", dm.goldRate(gap, cover), bound);
        assertTrue("...the US's rate (the tie money is the US$) and not the gap's: the gap changes nothing", dm.tieRate() == dm.worldRate() && Double.compare(dm.goldRate(.9, cover), dm.goldRate(-.9, cover)) == 0);
        dm.setAim(DebtManager.Aim.CURRENCY);
        same("aiming at the currency it is the tie rate plus CURRENCY_WEIGHT x the gap", dm.goldRate(gap, cover),
                Math.max(DebtManager.MIN_POLICY_RATE, Math.min(DebtManager.MAX_POLICY_RATE, dm.tieRate() + DebtManager.CURRENCY_WEIGHT * gap)));
        dm.setAim(DebtManager.Aim.MIX);
        same("...and aiming at a mix", dm.goldRate(gap, cover), Math.max(DebtManager.MIN_POLICY_RATE, Math.min(DebtManager.MAX_POLICY_RATE, dm.tieRate() + DebtManager.CURRENCY_WEIGHT * gap)));
        dm.setAim(DebtManager.Aim.GOLD);
        dm.setAutopilot(false);
        same("with the autopilot off it is the player's spread over the tie rate whatever the aim", dm.goldRate(gap, cover),
                Math.max(DebtManager.MIN_POLICY_RATE, Math.min(DebtManager.MAX_POLICY_RATE, dm.tieRate() + dm.getPegSpread())));
        dm.setAutopilot(true);
        dm.setWorldRate(.95);
        same("the dial stays inside its bounds: the spread over a US rate of 95% is clamped to MAX_POLICY_RATE", dm.goldRate(gap, floor), DebtManager.MAX_POLICY_RATE);
        dm.setWorldRate(.04);

        // tiedRate(), a peg's dial, reads the gold aim as the player's spread (the gold aim means nothing under a peg).
        same("tiedRate() with the autopilot aiming at the gold reads the player's spread", dm.tiedRate(gap),
                Math.max(DebtManager.MIN_POLICY_RATE, Math.min(DebtManager.MAX_POLICY_RATE, dm.tieRate() + dm.getPegSpread())));
        // dropGoldAim() writes nothing.
        int lines = log.size();
        dm.dropGoldAim();
        assertTrue("dropGoldAim() makes the aim Inflation and writes no decision line (" + lines + " lines before and after)", dm.getAim() == DebtManager.Aim.INFLATION && log.size() == lines);
        dm.setAim(DebtManager.Aim.CURRENCY);
        lines = log.size();
        dm.dropGoldAim();
        assertTrue("...and does nothing to another aim", dm.getAim() == DebtManager.Aim.CURRENCY && log.size() == lines);
        dm.setAim(DebtManager.Aim.GOLD);
        assertTrue("setAim() does write its line: \"" + (log.last() == null ? "" : log.last().label()) + "\"", log.last() != null && log.last().label().equals("Central bank aims at Defend the gold"));
        tied[0] = false;
        assertTrue("untied, the tie money is none and the tie rate the US's: the autopilot aiming at inflation is the rule's", !dm.isTied() && dm.tieMoney() == null && dm.tieRate() == dm.worldRate());
    }

    /* =====================================================================
       THE TOWN (7-10): one town played to March 1933, saved once, loaded for each
       ===================================================================== */

    /** The slot the shared town is saved to, in the harness's own scratch folder (GameFiles.scratch): never one of the nine. */
    static final int SLOT = 2;

    /** The founders' money put into gold by name, D$ thousands: with less the cover (gold at the mint price over M0) falls under the floor on the way to 1933 and the city is off gold before the dates. */
    static final double GOLD_LOCAL = 40_000;

    /** The cover section 9 sets the town's gold to, between the floor and the defence's own: where the autopilot's spread is neither nothing nor the whole. */
    static final double MIDWAY = (ForeignAccounts.COVER_RATIO + DebtManager.GOLD_DEFENCE_FROM) / 2;

    static Game shared;
    static double entryRate, coverMarch1933, centreMarch1933;

    static Game reload(Game from, int slot) {
        Game back = new Game(from.getGameFiles());
        quietly(() -> back.loadGameSave(slot));
        return back;
    }

    static int world(Game g) { return g.getWorldMoneys().month(); }

    static boolean said(Game g, int from, String text) {
        List<DecisionLog.Entry> all = g.getDecisions().entries();
        for (int i = from; i < all.size(); i++) if (all.get(i).label().toLowerCase().contains(text.toLowerCase())) return true;
        return false;
    }

    /** One answer's run from March 1933 to February 1934 (or the month the city came off gold). */
    static final class Run {
        Game g;
        ForeignAccounts fx;
        boolean leftOpen, noticeStood, noticeResolved, parOpen, parNoticeStood, wordedAnswer, centreHeld = true, inPoints = true;
        int raised, resolved, answeredIn, onGoldMonths;
        double centreFeb, rateFeb, worstOff;
    }

    static Run through(Game from, ForeignAccounts.GoldChoice answer, String said) {
        Run r = new Run();
        r.g = reload(from, SLOT);
        r.fx = r.g.getForeignAccounts();
        play(r.g, 1);
        r.leftOpen = r.fx.getGoldEvent() == LEFT_1933;
        Notice open = r.g.getInbox().live("gold:" + LEFT_1933.name());
        r.noticeStood = open != null;
        r.raised = open == null ? 0 : open.getRaised();
        r.answeredIn = r.g.getMonth();
        int before = r.g.getDecisions().size();
        r.g.answerGoldEvent(answer);
        r.wordedAnswer = said(r.g, before, said);
        double mintRate = r.fx.getMintRate();
        while (world(r.g) < LAST && r.fx.getRegime() == ON_GOLD) {
            play(r.g, 1);
            if (r.fx.getRegime() != ON_GOLD) break;
            r.onGoldMonths++;
            double off = Math.abs(r.fx.getRate() / r.fx.goldCentre() - 1);
            r.worstOff = Math.max(r.worstOff, off);
            if (off > ForeignAccounts.GOLD_POINTS + 1e-9) r.inPoints = false;
            if (answer == ForeignAccounts.GoldChoice.FOLLOW && !within(r.fx.goldCentre(), mintRate, 1e-9)) r.centreHeld = false;
            if (world(r.g) == GoldPrice.FLOAT_START + 1) {
                Notice done = r.g.getInbox().latest("gold:" + LEFT_1933.name());
                r.noticeResolved = done != null && done.isResolved();
                r.resolved = done == null ? 0 : done.getResolved();
            }
        }
        if (r.resolved == 0) {   // a leaver's loop never ran: the month after its answer is played here
            if (world(r.g) == GoldPrice.FLOAT_START) play(r.g, 1);
            Notice done = r.g.getInbox().latest("gold:" + LEFT_1933.name());
            r.noticeResolved = done != null && done.isResolved();
            r.resolved = done == null ? 0 : done.getResolved();
        }
        r.centreFeb = r.fx.goldCentre();
        r.rateFeb = r.fx.getRate();
        r.parOpen = r.fx.getGoldEvent() == PAR_1934;
        r.parNoticeStood = r.g.getInbox().live("gold:" + PAR_1934.name()) != null;
        return r;
    }

    static void aTownOnGoldThroughNineteenThirtyThreeAndFour() {
        out.println("\n--- 7. a town on gold through 1933 and 1934 ---");
        Game g = CurrencyRegimeCheck.town("gd3-gold", Founding.defaults());
        ForeignAccounts fx = g.getForeignAccounts();
        play(g, 1);
        quietly(() -> g.buyForeignCurrency(GOLD_LOCAL, "XAU"));
        fx.forgetDealing();
        entryRate = fx.getRate();
        quietly(() -> g.setCurrencyRegime(ON_GOLD));
        assertTrue("fixture: the town is on Gold, its aim Defend the gold, struck at the D$'s rate of the day times gold's price (D$" + String.format("%.4f", fx.getMintPrice()) + " an ounce)",
                fx.getRegime() == ON_GOLD && g.getDebtManager().getAim() == DebtManager.Aim.GOLD && within(fx.getMintPrice(), entryRate * PRICE, 1e-9) && g.getGoldCover() > 1);
        double lowest = g.getGoldCover();
        while (world(g) < FIRST && fx.getRegime() == ON_GOLD) {
            play(g, 1);
            lowest = Math.min(lowest, g.getGoldCover());
            if (world(g) % 120 == 0) out.printf("   world month %d: cover %.2f, rate %.4f, gold %,.0f thousand oz%n", world(g), g.getGoldCover(), fx.getRate(), fx.getHolding(GOLD));
        }
        coverMarch1933 = g.getGoldCover();
        centreMarch1933 = fx.goldCentre();
        out.printf("   March 1933 (world month %d): cover %.3f (lowest on the way %.3f), centre %.6f, rate %.4f%n", world(g), coverMarch1933, lowest, centreMarch1933, fx.getRate());
        assertTrue("the town is still on Gold in March 1933, with a cover over the law's floor " + ForeignAccounts.COVER_RATIO + " all the way", fx.getRegime() == ON_GOLD && lowest > ForeignAccounts.COVER_RATIO);
        assertTrue("...its event none, and the centre the entry rate: the mint price over the statute's price (the D$ where it was struck)", fx.getGoldEvent() == NO_EVENT && within(centreMarch1933, entryRate, 1e-9));
        assertTrue("saved to the harness's scratch folder", g.saveGame(SLOT, "gd3-march-1933").ok);
        shared = g;

        Run stay = through(g, ForeignAccounts.GoldChoice.STAY, "stayed");
        Run follow = through(g, ForeignAccounts.GoldChoice.FOLLOW, "moved the mint price");
        Run leave = through(g, ForeignAccounts.GoldChoice.LEAVE, "left gold");
        for (Run r : new Run[] { stay, follow }) {
            assertTrue("the event of April 1933 is open that month, and the answer is written in the decision log (" + (r == stay ? "stay" : "follow") + ")", r.leftOpen && r.wordedAnswer);
        }
        assertTrue("leave: the event opens, the answer is written, and the regime goes to Lean with the aim Inflation",
                leave.leftOpen && leave.wordedAnswer && leave.fx.getRegime() == LEAN && leave.g.getDebtManager().getAim() == DebtManager.Aim.INFLATION);
        assertTrue("stay: still on Gold in February 1934 (world month " + world(stay.g) + ", " + stay.onGoldMonths + " months after the date), the rate inside the gold points of its centre every month (the worst "
                + String.format("%.4f%%", 100 * stay.worstOff) + " of " + 100 * ForeignAccounts.GOLD_POINTS + "%)", stay.fx.getRegime() == ON_GOLD && stay.inPoints && world(stay.g) == LAST);
        double dearer = PRICE / GoldPrice.PAR_1934;
        out.printf("   February 1934: the centre %.6f (March 1933's %.6f, times %.6f = the D$ %.1f%% dearer), the rate %.4f%n", stay.centreFeb, centreMarch1933, dearer, 100 * (1 / dearer - 1), stay.rateFeb);
        close("stay: the centre in February 1934 is March 1933's times PAR_1900 / PAR_1934", stay.centreFeb, centreMarch1933 * dearer, 1e-9 * centreMarch1933);
        assertTrue("stay: in February 1934 the PAR_1934 event opens", stay.parOpen);
        assertTrue("follow: still on Gold, its centre at the mint rate every month, the rate inside the points, and no PAR_1934 event (it follows the dollar already)",
                follow.fx.getRegime() == ON_GOLD && follow.centreHeld && follow.inPoints && !follow.parOpen && world(follow.g) == LAST && follow.fx.isMintFollowing());
        close("follow: the centre in February 1934 is March 1933's", follow.centreFeb, centreMarch1933, 1e-9 * centreMarch1933);

        out.println("\n--- 8. the notice ---");
        assertTrue("the notice 'gold:LEFT_1933' is raised the month the event opens (month " + stay.answeredIn + ") and stands while it is open",
                stay.noticeStood && stay.raised == stay.answeredIn);
        assertTrue("...and is resolved the month after it is answered (raised " + stay.raised + ", resolved " + stay.resolved + ")", stay.noticeResolved && stay.resolved == stay.answeredIn + 1);
        assertTrue("...a leaver's too, the month after it left gold", leave.noticeStood && leave.noticeResolved && leave.resolved == leave.answeredIn + 1);
        assertTrue("the notice 'gold:PAR_1934' stands in February 1934 for the city that stayed, and is never raised for the one that followed",
                stay.parNoticeStood && !follow.parNoticeStood && follow.g.getInbox().latest("gold:" + PAR_1934.name()) == null);
        Game lean = reload(g, SLOT);
        quietly(() -> lean.setCurrencyRegime(LEAN));
        play(lean, 1);
        assertTrue("a city not on gold in April 1933 has no event and no notice", lean.getGoldEvent() == NO_EVENT && lean.getInbox().latest("gold:" + LEFT_1933.name()) == null);
    }

    static void theDialInTheTown() {
        out.println("\n--- 9. the dial in the town ---");
        Game g = reload(shared, SLOT);
        ForeignAccounts fx = g.getForeignAccounts();
        DebtManager dm = g.getDebtManager();
        // The cover is brought to MIDWAY by selling gold by name (what the treasury may do): the fixture that gives the spread something to ask.
        double keep = MIDWAY * g.getCentralBank().m0() / fx.getMintPrice();
        double sold = 1 - keep / fx.getHolding(GOLD);
        quietly(() -> g.sellForeignCurrency(sold * fx.getHolding(GOLD) * fx.getMoneyUsd(GOLD) * fx.getRate(), "XAU"));
        fx.forgetDealing();
        quietly(() -> dm.setAutopilot(true));   // the autopilot aiming at the gold is the fixture: with it off the dial asks the player's spread, nothing by default
        double cover = g.getGoldCover();
        assertTrue("fixture: on Gold, the autopilot on and aiming at the gold, the cover " + String.format("%.3f", cover) + " between the floor and the defence's own",
                fx.getRegime() == ON_GOLD && dm.isAutopilot() && dm.getAim() == DebtManager.Aim.GOLD && cover > ForeignAccounts.COVER_RATIO && cover < DebtManager.GOLD_DEFENCE_FROM);
        double gap = fx.currencyGap();
        play(g, 1);
        double after = g.getGoldCover();
        double lo = Math.min(dm.goldRate(gap, cover), dm.goldRate(fx.currencyGap(), after)), hi = Math.max(dm.goldRate(gap, cover), dm.goldRate(fx.currencyGap(), after));
        out.printf("   aiming at the gold: policy rate %.6f, goldRate at the cover the month read (%.3f) %.6f, at the cover after (%.3f) %.6f, the tie %.6f%n", dm.getPolicyRate(), cover,
                dm.goldRate(gap, cover), after, dm.goldRate(fx.currencyGap(), after), dm.tieRate());
        assertTrue("the month's policy rate is goldRate(currencyGap(), getGoldCover()) as Game struck it, from the cover the month read", dm.getPolicyRate() >= lo - 1e-9 && dm.getPolicyRate() <= hi + 1e-9);
        assertTrue("...above the tie (the spread asks something at this cover) and the dial is tied", dm.getPolicyRate() > dm.tieRate() && dm.isTied());
        quietly(() -> dm.setAim(DebtManager.Aim.INFLATION));
        play(g, 1);
        assertTrue("aiming at inflation the dial is the inflation rule's, untied: isTied() false, and not the gold's", !dm.isTied() && Math.abs(dm.getPolicyRate() - dm.goldRate(fx.currencyGap(), g.getGoldCover())) > 1e-9);
        quietly(() -> { dm.setAim(DebtManager.Aim.GOLD); dm.setAutopilot(false); dm.setPegSpread(.02); });
        play(g, 1);
        close("the autopilot off, the dial is the tie plus the player's spread (not the cover's)", dm.getPolicyRate(), dm.tieRate() + .02, 1e-9);
        quietly(() -> dm.setAutopilot(true));
        // Forced break: the gold sold by name, the cover at nothing, so that any month's sale breaks it by the cover (any month's hazard is the peg's own).
        quietly(() -> g.sellForeignCurrency(fx.getHolding(GOLD) * fx.getMoneyUsd(GOLD) * fx.getRate() * 2, "XAU"));
        fx.forgetDealing();
        int before = g.getDecisions().size();
        int months = 0;
        while (fx.getRegime() == ON_GOLD && months < 36) {
            play(g, 1);
            months++;
        }
        out.printf("   no gold: the D$ came off gold in month %d of the run, by %s, the aim now %s%n", months, fx.getLastBreakCause(), dm.getAim());
        assertTrue("forced break: the regime is Float, the cause the cover or doubt, and the aim Inflation", fx.getRegime() == FLOAT && fx.getBreaks() > 0
                && (fx.getLastBreakCause() == ForeignAccounts.BreakCause.COVER || fx.getLastBreakCause() == ForeignAccounts.BreakCause.DOUBT) && dm.getAim() == DebtManager.Aim.INFLATION);
        assertTrue("...and the decision log gained no line about the aim (the city's fate, not a decision)", !said(g, before, "aim"));
    }

    static void savedMidGold() {
        out.println("\n--- 10. saved mid-gold ---");
        Game live = reload(shared, SLOT);
        play(live, 1);
        ForeignAccounts fx = live.getForeignAccounts();
        assertTrue("fixture: on Gold with the April 1933 event open, the mint price not following", fx.getRegime() == ON_GOLD && fx.getGoldEvent() == LEFT_1933 && !fx.isMintFollowing());
        for (int round = 0; round < 2; round++) {
            assertTrue("saved to the scratch folder", live.saveGame(3, "gd3-mid-gold-" + round).ok);
            Game back = reload(live, 3);
            ForeignAccounts bfx = back.getForeignAccounts();
            assertTrue("round " + round + ": the loaded city keeps the regime, the aim, the mint price, the mint rate, following and the open event (slots 152-155), to the bit",
                    sameBits(CurrencyRegimeCheck.regimeFields(fx), CurrencyRegimeCheck.regimeFields(bfx))
                            && sameBits(new double[] { fx.getMintPrice(), fx.getMintRate(), fx.isMintFollowing() ? 1 : 0, fx.getGoldEvent().ordinal(), fx.getHolding(GOLD) },
                                    new double[] { bfx.getMintPrice(), bfx.getMintRate(), bfx.isMintFollowing() ? 1 : 0, bfx.getGoldEvent().ordinal(), bfx.getHolding(GOLD) })
                            && back.getDebtManager().getAim() == live.getDebtManager().getAim());
            if (round == 1) assertTrue("...and the second round follows the dollar with no event open", bfx.isMintFollowing() && bfx.getGoldEvent() == NO_EVENT);
            int apart = 0, regimeApart = 0;
            double worst = 0;
            for (int m = 1; m <= 12; m++) {
                if (round == 0 && m == 1) {   // both answer the same, in the same month: the second round is saved following
                    quietly(() -> live.answerGoldEvent(ForeignAccounts.GoldChoice.FOLLOW));
                    quietly(() -> back.answerGoldEvent(ForeignAccounts.GoldChoice.FOLLOW));
                }
                play(live, 1);
                play(back, 1);
                double[] a = figures(live), b = figures(back);
                for (int k = 0; k < a.length; k++) worst = Math.max(worst, Math.abs(a[k] - b[k]) / Math.max(1, Math.abs(a[k])));
                if (!sameBits(a, b)) apart++;
                if (fx.getRegime() != bfx.getRegime()) regimeApart++;
            }
            out.printf("   round %d, 12 months on: %d differ to the bit; the largest relative gap %.2e; the regime %s%n", round, apart, worst, fx.getRegime());
            assertTrue("round " + round + ": the next 12 months equal the twin that never saved, the regime the same every month, to the bit", apart == 0 && regimeApart == 0 && worst < 1e-9);
        }
    }

    static double[] figures(Game g) { return CurrencyRegimeCheck.figures(g); }

    static boolean sameBits(double[] a, double[] b) { return CurrencyRegimeCheck.sameBits(a, b); }

    static void play(Game g, int months) { CurrencyRegimeCheck.play(g, months); }

    /* =====================================================================
       11 and 12
       ===================================================================== */

    static void gdTwosTwoFindings() {
        out.println("\n--- 11. GD2's two findings ---");
        int bad = (1990 - Nations.FIRST_YEAR) * 12, good = (1940 - Nations.FIRST_YEAR) * 12;
        GoldPrice broken = new GoldPrice(Nations.Path.RANDOM, 1L, year -> year >= 1980 ? Double.NaN : 1.0);
        boolean threw = false;
        try {
            broken.usdPerOunce(bad);
        } catch (IllegalStateException e) {
            threw = true;
        }
        assertTrue("fixture: a GoldPrice on a US price level that is NaN from 1980 throws on its own at 1990 (the Random rule, from August 1971, walks on it)", threw);
        assertTrue("(a) Game.priceOrNaN() gives NaN there, no throw, and the price where the level is fine (1940)",
                Double.isNaN(Game.priceOrNaN(broken, bad)) && Game.priceOrNaN(broken, good) == broken.usdPerOunce(good) && Game.priceOrNaN(broken, good) > 0
                        && Double.isNaN(Game.priceOrNaN(null, good)) && Double.isNaN(Game.priceOrNaN(broken, -1)));
        WorldMoneys moneys = shared.getWorldMoneys();
        int idx = Game.eightOf(moneys, "XAU");
        assertTrue("(b) Game.eightOf(moneys, \"XAU\") is not the vault's gold (" + GOLD + "): a money anchored to gold lands on none of the eight (" + idx + ")", idx != GOLD);
        Nations nations = shared.getNations();
        double[] weight = new double[nations.count() + 1];
        Arrays.fill(weight, 1);
        double[] shares = Game.tradeSharesByMoney(moneys, nations, 1900, weight);
        double eight = 0;
        for (int k = 0; k < GOLD; k++) eight += shares[k];
        assertTrue("...and tradeSharesByMoney never gives gold a share (" + shares[GOLD] + "), the eight's summing to one (" + eight + ")", shares[GOLD] == 0 && within(eight, 1, 1e-9));
    }

    static void leanIsTheDefault() {
        out.println("\n--- 12. Lean is the default ---");
        Game g = CurrencyRegimeCheck.town("gd3-lean", Founding.defaults());
        ForeignAccounts fx = g.getForeignAccounts();
        assertTrue("a new town: Lean, the aim Inflation, no mint price, no event, not tied",
                fx.getRegime() == LEAN && g.getDebtManager().getAim() == DebtManager.Aim.INFLATION && !(fx.getMintPrice() > 0) && fx.getGoldEvent() == NO_EVENT && !g.getDebtManager().isTied());
        play(g, 24);
        assertTrue("24 months on: no gold in the vault, no gold event, no mint price, nothing dealt in gold, still Lean and untied",
                fx.getRegime() == LEAN && fx.getHolding(GOLD) == 0 && fx.getGoldEvent() == NO_EVENT && !(fx.getMintPrice() > 0) && !g.getDebtManager().isTied()
                        && g.getInbox().latest("gold:" + LEFT_1933.name()) == null);
    }
}
