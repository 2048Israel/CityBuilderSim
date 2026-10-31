package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * The vault in eight moneys (batch WN4, slice C; spec-nations 5.2-5.4): ForeignAccounts keeps a balance in each of the eight the IMF's
 * COFER names, takes a dollar in by the reserve mix and gives one up from the largest holding, revalues across the moneys when the
 * eight move against each other, and carries all of it in its save - while a city that never touches the mix plays as it did.
 *
 * WHY. The vault was one figure, reservesUsd, changed in seven places, each with its own clamp (the defence empties it by one expression,
 * the treasury's sale by another, the land office by a third). Splitting it into eight balances could silently change any of them for the
 * default city, move which money a sale takes from, book a purchase's own money as the vault's gain, or lose a balance in the save. Each
 * section says against which constant or which old expression it is judged.
 *
 * What this has to prove:
 *   1. ALL US$ IS THE OLD VAULT: a ForeignAccounts on the default mix, driven through a treasury buy, a defence sale, an edge buy, a
 *      treasury sale, a land payment from the vault, a revaluation and a sale of everything, holds only US$ and its getReservesUsd() is, to
 *      the bit and at every step, what the old field's own expression at that site gives (written here from the old source).
 *   2. A BUY GOES IN BY THE MIX: under WORLD_MIX a purchase puts COFER_SHARES' share of its US$ into each money in its own units; under
 *      CHOSEN the chosen shares; under MATCH_TRADE the trade shares handed; a money with no rate takes nothing and the US$ takes its share;
 *      the strong edge's and a peg's buys go in by the mix; the founders' dollars (bought into "USD") are dollars; a named purchase goes
 *      into the named money only.
 *   3. A SALE COMES FROM THE LARGEST: 50/30/20 in three moneys, a sale of 40 takes 40 from the largest, a sale of 60 all 50 and 10 from the
 *      next; the defence and the land office likewise; a named sale comes from the named money and no other, clamped at what it holds.
 *   4. THE CROSS REVALUATION: a vault of 100 euros at 1.10 US$ the euro moved to 1.00 has a cross move of -10, the euro the driver at
 *      -9.1%; revalueVault() books reservesUsd x (rate - last rate) + cross x last rate, which is V1 r1 - (V1 - cross) r0; a purchase in
 *      the month adds nothing to the cross move; the next month's hand starts a new one.
 *   5. MATCH_TRADE'S MAPPING, on the full file: a nation on the CFA franc maps to the euro, a Gulf state to the US$, Canada to the Canadian
 *      dollar, Mexico to the US$; the shares sum to 1.
 *   6. THE SAVE: a vault in four moneys on a CHOSEN mix with a cross move, saved and restored, holds the same to the bit and revalues the
 *      same next month; a save with only slot 22 reads as all US$.
 *   7. IN A CITY (the full file only): the first month hands the eight their rates (US$ per unit is 1 / the moneys' rate) and a city that
 *      never touches the mix holds only US$ with no cross move; a purchase of euros and one by the world's mix put balances where they
 *      say; and the month's revaluation in local money is the vault's dollars at the rate's move plus the cross move at the last rate.
 *
 * Every fixture causes its condition: a settled currency on a peg whose month is a deficit (the defence sells) or a surplus (the edge
 * buys), holdings built by named purchases at known rates, and rates handed by hand.
 */
public class VaultMoneysCheck {

    static int fails = 0;
    static final PrintStream out = System.out;

    /** The cross rates the sections hand: US$ per unit of the US$, euro, yen, pound, yuan, Canadian and Australian dollar and franc. */
    static final double[] R = {1, 1.10, 0.0070, 1.27, 0.14, 0.73, 0.65, 1.12};

    /** The length of the foreign accounts' array: slots 0 to 141 and, after them, what the vault earned (142-146, XR4; InterestAbroadCheck's section 7 asserts them). */
    static final int SLOTS = 147;

    static final ForeignAccounts.Mix ALL_USD = ForeignAccounts.Mix.ALL_USD, MATCH_TRADE = ForeignAccounts.Mix.MATCH_TRADE,
            WORLD_MIX = ForeignAccounts.Mix.WORLD_MIX, CHOSEN = ForeignAccounts.Mix.CHOSEN;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-130s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Equal to the bit (and so a -0.0 is not a 0.0). */
    static void same(String label, double actual, double expected) {
        boolean ok = Double.doubleToLongBits(actual) == Double.doubleToLongBits(expected);
        if (!ok) fails++;
        out.printf("%-130s %s%s%n", label, ok ? "OK" : "FAIL", ok ? "" : String.format("  %.17g != %.17g", actual, expected));
    }

    /** Equal within a share of the expected (or an absolute 1e-12 about zero). */
    static void near(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol * Math.max(Math.abs(expected), 1e-3);
        if (!ok) fails++;
        out.printf("%-130s %s%s%n", label, ok ? "OK" : "FAIL", ok ? "" : String.format("  %.12g != %.12g", actual, expected));
    }

    static boolean sameArray(double[] a, double[] b) { return a != null && b != null && Arrays.equals(a, b); }

    /** What the vault holds beyond the US$, in the eight's own units, added up: 0 only if it holds nothing but dollars. */
    static double beyondTheDollar(ForeignAccounts fx) {
        double sum = 0;
        for (int k = 1; k < ForeignAccounts.MONEYS; k++) sum += fx.getHolding(k);
        return sum;
    }

    /** The sum of the COFER dial, which the mix normalises by. */
    static double coferTotal() {
        double sum = 0;
        for (double v : ForeignAccounts.COFER_SHARES) sum += v;
        return sum;
    }

    /** A settled currency on a peg at its own rate, no vault, no seed (so no doubt can break it): the fixture a month's deficit sells from and a month's surplus buys into. */
    static ForeignAccounts peg() {
        return CurrencyRegimeCheck.book(CurrencyRegimeCheck.TRADE, CurrencyRegimeCheck.TRADE, 0, 0, ForeignAccounts.Regime.PEG);
    }

    /** One month of the peg fixture's trade at this share of its imports, repriced; the deficit or surplus dealt is the vault's. */
    static void pegMonth(ForeignAccounts fx, double exportShare) {
        fx.startMonth();
        fx.takeMonth(CurrencyCheck.month(exportShare * CurrencyRegimeCheck.TRADE, CurrencyRegimeCheck.TRADE, 0, 0), CurrencyRegimeCheck.GDP);
        fx.repriceCurrency();
    }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        allUsdIsTheOldVault();
        aBuyGoesInByTheMix();
        aSaleComesFromTheLargest();
        theCrossRevaluation();
        theMapping();
        theSave();
        inACity();
        out.printf("%nVaultMoneysCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       1. ALL US$ IS THE OLD VAULT
       ===================================================================== */

    static void allUsdIsTheOldVault() {
        out.println("--- 1. all US$ is the old vault: every site's arithmetic on the US$ balance is what it was on the one field, to the bit ---");
        ForeignAccounts fx = peg();
        assertTrue("fixture: the default mix is all US$, the whole of it in the first money", fx.getReserveMix() == ALL_USD
                && Arrays.equals(fx.mixShares(), new double[] {1, 0, 0, 0, 0, 0, 0, 0}));
        double old = 0;   // the old field, driven by the old source's expressions at each site
        double lastRate = ForeignAccounts.OPENING_RATE;

        // THE TREASURY'S BUY (old: reservesUsd += localAmount / rate), at the opening rate and again at the peg.
        double rate = fx.getRate();
        fx.buyReserves(30_000);
        fx.forgetDealing();
        old += 30_000 / rate;
        same("a treasury purchase adds local / rate to the dollars", fx.getReservesUsd(), old);
        fx.repeg(1.6);
        CurrencyRegimeCheck.month(fx, CurrencyRegimeCheck.TRADE, CurrencyRegimeCheck.TRADE);
        rate = fx.getRate();
        assertTrue("fixture: the peg holds at 1.6 on balanced trade, so nothing is dealt in the month", rate == 1.6 && Double.compare(fx.getReservesUsd(), old) == 0);
        fx.buyReserves(48_000);
        old += 48_000 / rate;
        same("...and again at the peg's rate", fx.getReservesUsd(), old);
        fx.buyReserves(48_000, "USD");
        old += 48_000 / rate;
        same("...a purchase named \"USD\" is the same arithmetic (the founders' dollars are bought so)", fx.getReservesUsd(), old);

        // THE DEFENCE (old: reservesUsd = usd >= reservesUsd ? 0 : reservesUsd - usd).
        pegMonth(fx, .8);
        double sold = fx.getDefenceUsd();
        assertTrue("fixture: the month is a deficit and the peg's vault met it (sold " + String.format("%.4f", sold) + ")", sold > 0 && fx.getRegime() == ForeignAccounts.Regime.PEG);
        old = sold >= old ? 0 : old - sold;
        same("a defence sale takes the dollars sold off the dollars", fx.getReservesUsd(), old);

        // THE STRONG EDGE'S BUY (old: reservesUsd += usd).
        pegMonth(fx, 1.2);
        double bought = fx.getEdgeBoughtUsd();
        assertTrue("fixture: the month is a surplus and the peg's vault bought it (" + String.format("%.4f", bought) + ")", bought > 0);
        old += bought;
        same("an edge buy adds the dollars bought", fx.getReservesUsd(), old);

        // THE MONTH'S REVALUATION (old: lastVaultRevaluation = reservesUsd * (rate - lastVaultRate)).
        fx.revalueVault();
        same("the revaluation is the dollars times the rate's move since the vault was last valued", fx.getLastVaultRevaluation(), old * (rate - lastRate));
        lastRate = rate;

        // THE TREASURY'S SALE (old: held = max(0, reservesUsd * rate); sold = max(0, min(ask, held)); reservesUsd = sold >= held ? 0 : max(0, reservesUsd - sold / rate)).
        double ask = 20_000, held = Math.max(0, old * rate), partly = Math.max(0, Math.min(ask, held));
        double got = fx.sellReserves(ask);
        old = partly >= held ? 0 : Math.max(0, old - partly / rate);
        same("a partial treasury sale returns the old clamp's figure", got, partly);
        same("...and takes sold / rate off the dollars", fx.getReservesUsd(), old);

        // THE LAND OFFICE (old: spent = min(usd, reservesUsd); reservesUsd = spent >= reservesUsd ? 0 : reservesUsd - spent).
        double spentExpected = Math.min(5_000, old);
        double spent = fx.spendReservesOnLand(5_000);
        old = spentExpected >= old ? 0 : old - spentExpected;
        same("land paid from the vault returns the dollars it took", spent, spentExpected);
        same("...and takes them off the dollars", fx.getReservesUsd(), old);
        assertTrue("through all of it the vault held only US$, in the first balance", beyondTheDollar(fx) == 0 && fx.getHolding(0) == fx.getReservesUsd());

        // A land bill bigger than the vault empties it exactly and returns what it had.
        double rest = old;
        double all = fx.spendReservesOnLand(rest * 10);
        same("a land bill bigger than the vault spends all it holds", all, rest);
        same("...and empties it exactly", fx.getReservesUsd(), 0);

        // The treasury's sale of everything, from a vault bought again.
        fx.buyReserves(12_345.678);
        old = 12_345.678 / rate;
        same("a vault bought again holds local / rate", fx.getReservesUsd(), old);
        held = Math.max(0, old * rate);
        got = fx.sellReserves(Double.MAX_VALUE);
        same("a sale of everything raises what the dollars fetch at the rate", got, held);
        same("...and leaves nothing, not even a division's rounding", fx.getReservesUsd(), 0);

        // THE REVALUATION OF AN EMPTY VAULT after the currency fell: 0 x a negative move is -0.0, and stays so.
        fx.pinRate(1.2);
        fx.revalueVault();
        same("an empty vault revalued after the currency fell books the old expression's -0.0", fx.getLastVaultRevaluation(), 0.0 * (1.2 - lastRate));

        // The cross rates change nothing for a vault that holds only dollars, however they move.
        ForeignAccounts plain = new ForeignAccounts();
        plain.pinRate(1.0);
        plain.revalueVault();
        plain.setMoneyRates(R);
        plain.buyReserves(7_000);
        double[] moved = R.clone();
        for (int k = 1; k < moved.length; k++) moved[k] *= 1 + .01 * k;
        plain.setMoneyRates(moved);
        plain.pinRate(1.3);
        plain.revalueVault();
        same("rates that move under an all-US$ vault leave the cross move at 0", plain.getLastVaultCrossUsd(), 0);
        assertTrue("...with no money to name as the driver", plain.getLastVaultCrossDriver() == null);
        same("...and the revaluation the old expression's, with no cross term", plain.getLastVaultRevaluation(), plain.getReservesUsd() * (1.3 - 1.0));
    }

    /* =====================================================================
       2. A BUY GOES IN BY THE MIX
       ===================================================================== */

    static ForeignAccounts rated() {
        ForeignAccounts fx = new ForeignAccounts();
        fx.pinRate(1.0);
        fx.setMoneyRates(R);
        return fx;
    }

    static void aBuyGoesInByTheMix() {
        out.println("--- 2. a buy goes in by the mix: the world's, the chosen, the trade's; a money with no rate takes nothing ---");
        final double usd = 1_000;
        double total = coferTotal();

        ForeignAccounts world = rated();
        world.setReserveMix(WORLD_MIX);
        world.buyReserves(usd);
        boolean each = true;
        for (int k = 0; k < ForeignAccounts.MONEYS; k++) {
            double want = usd * ForeignAccounts.COFER_SHARES[k] / total / R[k];
            each &= Math.abs(world.getHolding(k) - want) <= 1e-12 * want;
        }
        assertTrue("under WORLD_MIX a purchase of 1,000 puts COFER_SHARES' share of its US$ into each money, in the money's own units", each);
        near("...so the vault is worth the 1,000 that was bought", world.getReservesUsd(), usd, 1e-12);
        near("...and the euro holds 20.6 / " + String.format("%.1f", total * 100) + " of it", world.getHoldingUsd(1), usd * .206 / total, 1e-12);

        ForeignAccounts chose = rated();
        double[] mine = {.30, .25, 0, .10, .15, 0, .20, 0};
        chose.setReserveMix(CHOSEN);
        chose.setChosenShares(mine);
        chose.buyReserves(usd);
        each = true;
        for (int k = 0; k < ForeignAccounts.MONEYS; k++) each &= Math.abs(chose.getHolding(k) - usd * mine[k] / R[k]) <= 1e-12 * Math.max(1, usd * mine[k] / R[k]);
        assertTrue("under CHOSEN the player's shares: " + Arrays.toString(mine), each);
        assertTrue("...and a money the player gave nothing holds nothing", chose.getHolding(2) == 0 && chose.getHolding(5) == 0 && chose.getHolding(7) == 0);

        ForeignAccounts trade = rated();
        double[] handed = {3, 2, 1, 0, 4, 0, 0, 0};
        trade.setTradeShares(handed);
        trade.setReserveMix(MATCH_TRADE);
        trade.buyReserves(usd);
        each = true;
        for (int k = 0; k < ForeignAccounts.MONEYS; k++) each &= Math.abs(trade.getHolding(k) - usd * handed[k] / 10 / R[k]) <= 1e-12 * Math.max(1, usd * handed[k] / 10 / R[k]);
        assertTrue("under MATCH_TRADE the trade shares handed, normalised (3, 2, 1, 0, 4 of 10)", each);
        assertTrue("...Game's hand is read back as shares that sum to 1", Math.abs(Arrays.stream(trade.getTradeShares()).sum() - 1) < 1e-12);
        ForeignAccounts none = rated();
        none.setReserveMix(MATCH_TRADE);
        none.buyReserves(usd);
        same("MATCH_TRADE with no trade handed buys all US$", none.getHolding(0), usd);

        // A money with no rate this month takes no share; the US$ takes it.
        double[] noYen = R.clone();
        noYen[2] = Double.NaN;
        ForeignAccounts missing = new ForeignAccounts();
        missing.pinRate(1.0);
        missing.setMoneyRates(noYen);
        missing.setReserveMix(WORLD_MIX);
        missing.buyReserves(usd);
        assertTrue("a yen with no rate holds nothing, whatever the mix says", missing.getHolding(2) == 0 && !missing.hasRate(2) && missing.hasRate(1));
        near("...and the US$ takes the yen's share: its own and the yen's", missing.getHolding(0), usd * (.567 + .050) / total, 1e-12);
        near("...the rest as the mix says", missing.getHolding(1), usd * .206 / total / R[1], 1e-12);
        near("...so the shares in force still sum to 1", Arrays.stream(missing.mixShares()).sum(), 1, 1e-15);
        ForeignAccounts never = new ForeignAccounts();
        never.setReserveMix(WORLD_MIX);
        never.buyReserves(usd);
        same("no rate handed to any money (no world file) buys all US$ under any mix", never.getHolding(0), usd);
        ForeignAccounts lost = rated();
        double[] noEuro = R.clone();
        noEuro[1] = 0;
        lost.setMoneyRates(noEuro);
        assertTrue("a money whose rate is not above zero this month has none", !lost.hasRate(1) && lost.hasRate(2));

        // The chosen shares are kept normalised: negative and NaN read 0, all zero means all US$.
        ForeignAccounts clean = rated();
        clean.setChosenShares(new double[] {-1, 2, Double.NaN, 2, 0, 0, 0, 0});
        assertTrue("chosen shares: a negative or NaN share reads 0 and the rest are scaled to 1 (0, 1/2, 0, 1/2)",
                Arrays.equals(clean.getChosenShares(), new double[] {0, .5, 0, .5, 0, 0, 0, 0}));
        clean.setChosenShares(new double[8]);
        assertTrue("...all zero means all US$", Arrays.equals(clean.getChosenShares(), new double[] {1, 0, 0, 0, 0, 0, 0, 0}));
        clean.shiftChosen(1, .05);
        assertTrue("...a five-point step on the euro takes five points from the US$ (all it had)", Math.abs(clean.getChosenShares()[1] - .05) < 1e-15
                && Math.abs(clean.getChosenShares()[0] - .95) < 1e-15);
        clean.shiftChosen(0, .05);
        assertTrue("...a step on a money keeps the eight at 100% (" + Arrays.toString(clean.getChosenShares()) + ")", Math.abs(Arrays.stream(clean.getChosenShares()).sum() - 1) < 1e-12);

        // The founders' dollars are dollars, whatever the mix; a named purchase goes into the named money only.
        ForeignAccounts founders = rated();
        founders.setReserveMix(WORLD_MIX);
        founders.buyReserves(usd, "USD");
        same("the founders' dollars, bought into \"USD\", are dollars even under the world's mix", founders.getHolding(0), usd);
        assertTrue("...and nothing else", beyondTheDollar(founders) == 0);
        ForeignAccounts named = rated();
        named.buyReserves(usd, "EUR");
        near("a purchase named EUR puts all of it into euros, in euros", named.getHolding(1), usd / R[1], 1e-12);
        assertTrue("...under the default mix, and no other money holds anything", named.getHolding(0) == 0 && named.getReserveMix() == ALL_USD
                && named.getHolding(2) + named.getHolding(3) + named.getHolding(4) + named.getHolding(5) + named.getHolding(6) + named.getHolding(7) == 0);
        ForeignAccounts toYen = new ForeignAccounts();
        toYen.pinRate(1.0);
        toYen.setMoneyRates(noYen);
        toYen.buyReserves(usd, "JPY");
        same("a purchase named for a money with no rate this month goes to the US$", toYen.getHolding(0), usd);
        ForeignAccounts unknown = rated();
        unknown.setReserveMix(WORLD_MIX);
        unknown.buyReserves(usd, "XYZ");
        near("...and one named for a code that is none of the eight goes by the mix", unknown.getHolding(1), usd * .206 / total / R[1], 1e-12);

        // The strong edge's and a peg's buys go in by the mix as well.
        ForeignAccounts pegged = peg();
        pegged.setMoneyRates(R);
        pegged.buyReserves(60_000, "USD");
        pegged.forgetDealing();
        pegged.repeg(1.6);
        CurrencyRegimeCheck.month(pegged, CurrencyRegimeCheck.TRADE, CurrencyRegimeCheck.TRADE);
        pegged.setReserveMix(WORLD_MIX);
        double before = pegged.getReservesUsd();
        pegMonth(pegged, 1.2);
        double edge = pegged.getEdgeBoughtUsd();
        assertTrue("fixture: a surplus month on the peg, the edge bought dollars (" + String.format("%.4f", edge) + ")", edge > 0);
        near("the vault rose by the edge's dollars", pegged.getReservesUsd() - before, edge, 1e-9);
        near("...the euro's share of them went into euros, in euros", pegged.getHolding(1), edge * .206 / total / R[1], 1e-9);
        near("...and the yen's likewise", pegged.getHolding(2), edge * .050 / total / R[2], 1e-9);
    }

    /* =====================================================================
       3. A SALE COMES FROM THE LARGEST
       ===================================================================== */

    /** A vault of 50,000, 30,000 and 20,000 US$ worth in euros, pounds and Canadian dollars, bought at a rate of 1 so local money and US$ are one number. */
    static ForeignAccounts three() {
        ForeignAccounts fx = rated();
        fx.buyReserves(50_000, "EUR");
        fx.buyReserves(30_000, "GBP");
        fx.buyReserves(20_000, "CAD");
        return fx;
    }

    static void aSaleComesFromTheLargest() {
        out.println("--- 3. a sale comes from the largest holding, then the next; a named sale from the named money alone ---");
        ForeignAccounts fx = three();
        near("fixture: 50,000 in euros, 30,000 in pounds and 20,000 in Canadian dollars is a vault of 100,000", fx.getReservesUsd(), 100_000, 1e-12);
        double gbp = fx.getHolding(3), cad = fx.getHolding(5);
        double got = fx.sellReserves(40_000);
        near("a sale of 40,000 sells 40,000", got, 40_000, 1e-12);
        near("...taking all 40,000 from the largest, the euros (10,000 left)", fx.getHoldingUsd(1), 10_000, 1e-9);
        same("...and nothing from the pounds", fx.getHolding(3), gbp);
        same("...nor the Canadian dollars", fx.getHolding(5), cad);

        ForeignAccounts more = three();
        gbp = more.getHolding(3);
        cad = more.getHolding(5);
        got = more.sellReserves(60_000);
        near("a sale of 60,000 sells 60,000", got, 60_000, 1e-12);
        same("...taking all 50,000 of the euros", more.getHolding(1), 0);
        near("...and 10,000 from the next, the pounds (20,000 left)", more.getHoldingUsd(3), 20_000, 1e-9);
        same("...none from the Canadian dollars", more.getHolding(5), cad);

        ForeignAccounts everything = three();
        got = everything.sellReserves(Double.MAX_VALUE);
        near("a sale of everything returns the whole vault", got, 100_000, 1e-12);
        assertTrue("...and empties all three balances exactly", everything.getHolding(1) == 0 && everything.getHolding(3) == 0 && everything.getHolding(5) == 0
                && everything.getReservesUsd() == 0);

        ForeignAccounts named = three();
        double eur = named.getHolding(1);
        gbp = named.getHolding(3);
        got = named.sellReserves(15_000, "CAD");
        near("a sale named for the Canadian dollars sells what was asked", got, 15_000, 1e-12);
        near("...out of the Canadian dollars (5,000 left)", named.getHoldingUsd(5), 5_000, 1e-9);
        assertTrue("...and neither the euros, the largest, nor the pounds gave up a unit", named.getHolding(1) == eur && named.getHolding(3) == gbp);
        got = named.sellReserves(1e9, "CAD");
        near("asked for more than it holds, the named sale is clamped at what the money holds", got, 5_000, 1e-9);
        same("...and empties it exactly", named.getHolding(5), 0);
        assertTrue("...with the others still untouched", named.getHolding(1) == eur && named.getHolding(3) == gbp);
        got = named.sellReserves(1_000, "JPY");
        same("a sale named for a money it holds none of returns 0", got, 0);
        assertTrue("...and moves nothing", named.getHolding(1) == eur && named.getHolding(3) == gbp);
        got = named.sellReserves(10_000, "XYZ");
        near("a code that is none of the eight sells from the largest, as the unnamed sale does", got, 10_000, 1e-12);
        near("...the euros (40,000 left)", named.getHoldingUsd(1), 40_000, 1e-9);
        near("sellableReserves(code) is what that money alone fetches at the rate", three().sellableReserves("GBP"), 30_000, 1e-12);

        // Equal holdings: the earlier money in the eight's order gives first.
        ForeignAccounts tie = new ForeignAccounts();
        tie.pinRate(1.0);
        double[] twos = R.clone();
        twos[1] = 2;
        twos[3] = 2;
        tie.setMoneyRates(twos);
        tie.buyReserves(10_000, "EUR");
        tie.buyReserves(10_000, "GBP");
        tie.sellReserves(4_000);
        near("holdings of equal worth: the earlier in the eight's order, the euro, gives first", tie.getHoldingUsd(1), 6_000, 1e-12);
        near("...and the pound keeps its 10,000", tie.getHoldingUsd(3), 10_000, 1e-12);

        // The defence sells from the largest too.
        ForeignAccounts defence = peg();
        defence.setMoneyRates(R);
        defence.buyReserves(30_000, "EUR");
        defence.buyReserves(30_000, "GBP");
        defence.forgetDealing();
        defence.repeg(1.6);
        CurrencyRegimeCheck.month(defence, CurrencyRegimeCheck.TRADE, CurrencyRegimeCheck.TRADE);
        defence.buyReserves(24_000, "EUR");   // 15,000 US$ more: the euros are the larger by far
        defence.forgetDealing();
        double eurBefore = defence.getHoldingUsd(1), gbpBefore = defence.getHolding(3);
        pegMonth(defence, .8);
        double sold = defence.getDefenceUsd();
        assertTrue("fixture: the peg's vault met a deficit by selling (" + String.format("%.4f", sold) + ")", sold > 0);
        near("the defence sold from the euros, the largest", defence.getHoldingUsd(1), eurBefore - sold, 1e-9);
        same("...and the pounds gave up nothing", defence.getHolding(3), gbpBefore);

        // The land office as well.
        ForeignAccounts land = three();
        gbp = land.getHolding(3);
        double paid = land.spendReservesOnLand(12_000);
        near("land paid from the vault takes the dollars asked", paid, 12_000, 1e-12);
        near("...from the largest, the euros (38,000 left)", land.getHoldingUsd(1), 38_000, 1e-9);
        same("...and not from the pounds", land.getHolding(3), gbp);
        double rest = land.getReservesUsd();
        paid = land.spendReservesOnLand(rest * 2);
        near("a bill bigger than the whole vault spends all of it", paid, rest, 1e-12);
        same("...and empties every balance exactly", land.getReservesUsd(), 0);
    }

    /* =====================================================================
       4. THE CROSS REVALUATION
       ===================================================================== */

    static void theCrossRevaluation() {
        out.println("--- 4. the cross revaluation: the eight moving against each other is a revaluation, booked in local money exactly ---");
        ForeignAccounts fx = new ForeignAccounts();
        fx.pinRate(2.0);
        fx.revalueVault();
        double[] rates = R.clone();
        fx.setMoneyRates(rates);
        fx.buyReserves(100 * 1.10 * 2.0, "EUR");
        near("fixture: 100 euros at 1.10 US$ the euro, bought with D$220 at the rate of 2", fx.getHolding(1), 100, 1e-12);
        fx.revalueVault();
        same("a month with nothing moved books no cross move", fx.getLastVaultCrossUsd(), 0);

        double[] fell = R.clone();
        fell[1] = 1.00;
        fx.setMoneyRates(fell);
        near("the euro moved to 1.00: the cross move is -10", fx.getLastVaultCrossUsd(), -10, 1e-12);
        ForeignAccounts.CrossDriver driver = fx.getLastVaultCrossDriver();
        assertTrue("...the driver is the euro", driver != null && driver.code().equals("EUR") && driver.index() == 1);
        near("...its move against the dollar is -9.1%", driver == null ? 0 : driver.move(), 1.00 / 1.10 - 1, 1e-12);
        near("...so the vault is worth 100 US$ now, not 110", fx.getReservesUsd(), 100, 1e-12);

        // A purchase in the same month, at the new rates, adds nothing to the cross move.
        fx.buyReserves(50 * 1.00 * 2.0, "EUR");
        near("a purchase of 50 more euros in the month, at the new rate, leaves the cross move at -10", fx.getLastVaultCrossUsd(), -10, 1e-12);
        near("...and the vault is worth 150", fx.getReservesUsd(), 150, 1e-12);
        fx.pinRate(2.5);
        fx.revalueVault();
        double v1 = fx.getReservesUsd(), r0 = 2.0, r1 = 2.5, cross = -10;
        near("revalueVault() books reservesUsd x (rate - last rate) + cross x last rate", fx.getLastVaultRevaluation(), v1 * (r1 - r0) + cross * r0, 1e-9);
        assertTrue("...cleared: the cross move is still -10 to read, and booked", Math.abs(fx.getLastVaultCrossUsd() + 10) < 1e-12);
        fx.revalueVault();
        near("a second revaluation in the same month books no cross move again (the move at the rate alone: none)", fx.getLastVaultRevaluation(), 0, 1e-12);

        // The identity V1 r1 - (V1 - cross) r0, with no purchase in the month.
        ForeignAccounts id = new ForeignAccounts();
        id.pinRate(2.0);
        id.revalueVault();
        id.setMoneyRates(R);
        id.buyReserves(100 * 1.10 * 2.0, "EUR");
        id.revalueVault();
        id.setMoneyRates(fell);
        id.pinRate(2.5);
        id.revalueVault();
        double V1 = id.getReservesUsd();
        near("V1 r1 - (V1 - cross) r0: the whole month's move in local money", id.getLastVaultRevaluation(), V1 * 2.5 - (V1 - (-10)) * 2.0, 1e-9);

        // The next month's hand starts a new move: the euro where it is, nothing moved, nothing to name.
        id.setMoneyRates(fell);
        same("a hand in which nothing moved starts a new month at a cross move of 0", id.getLastVaultCrossUsd(), 0);
        assertTrue("...with no driver", id.getLastVaultCrossDriver() == null);
        id.revalueVault();

        // Two moneys moving opposite ways: the cross move is the sum, the driver the larger.
        ForeignAccounts two = new ForeignAccounts();
        two.pinRate(1.0);
        two.setMoneyRates(R);
        two.buyReserves(1_000 * R[1], "EUR");
        two.buyReserves(10_000 * R[2], "JPY");
        two.revalueVault();
        double[] shift = R.clone();
        shift[1] = R[1] * 1.10;
        shift[2] = R[2] * .99;
        two.setMoneyRates(shift);
        double euro = 1_000 * (shift[1] - R[1]), yen = 10_000 * (shift[2] - R[2]);
        near("a euro up 10% (1,000 euros) and a yen down 1% (10,000 yen): the cross move is the sum of the two", two.getLastVaultCrossUsd(), euro + yen, 1e-9);
        ForeignAccounts.CrossDriver larger = two.getLastVaultCrossDriver();
        assertTrue("...the driver is the one that moved the vault the most in US$ (the " + (Math.abs(euro) > Math.abs(yen) ? "euro" : "yen") + ")",
                larger != null && larger.code().equals(Math.abs(euro) > Math.abs(yen) ? "EUR" : "JPY"));
    }

    /* =====================================================================
       5. MATCH_TRADE'S MAPPING - on the full file
       ===================================================================== */

    static void theMapping() throws Exception {
        out.println("--- 5. MATCH_TRADE maps each partner's money to the eight (the full file) ---");
        Path file = WorldData.locate();
        if (file == null) {
            out.println("  world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": the mapping's section did not run");
            return;
        }
        try (WorldData data = WorldData.open(file)) {
            WorldMoneysCheck.Scope sc = new WorldMoneysCheck.Scope("full", data);
            Nations nations = sc.nations(Nations.Path.HISTORICAL, 9_101, null);
            WorldMoneys moneys = sc.moneys(nations);
            final int year = 2015;
            String[][] expect = {
                {"DEU", "EUR"}, {"JPN", "JPY"}, {"GBR", "GBP"}, {"CHN", "CNY"}, {"CAN", "CAD"}, {"AUS", "AUD"}, {"CHE", "CHF"}, {"USA", "USD"},
                {"SEN", "EUR"}, {"CIV", "EUR"}, {"CMR", "EUR"},   // the CFA francs, pegged to the euro
                {"SAU", "USD"}, {"ARE", "USD"}, {"QAT", "USD"},   // the Gulf, pegged to the dollar
                {"MEX", "USD"}, {"BRA", "USD"}, {"ECU", "USD"},   // a float not in the eight, and a nation that uses the dollar
                {"KIR", "AUD"}, {"LIE", "CHF"}};                    // others' moneys: the Australian dollar, the Swiss franc
            int mapped = 0;
            for (String[] pair : expect) {
                int nation = data.numberOf(pair[0]);
                if (nation < 1) {
                    out.println("  " + pair[0] + " is not in the file: skipped");
                    continue;
                }
                double[] weight = new double[nations.count() + 1];
                weight[nation] = 1;
                double[] shares = Game.tradeSharesByMoney(moneys, nations, year, weight);
                int want = ForeignAccounts.moneyIndex(pair[1]);
                boolean ok = shares[want] == 1;
                for (int k = 0; k < shares.length; k++) ok &= k == want || shares[k] == 0;
                assertTrue(pair[0] + " (" + nations.year(nation, year).money() + ") maps to " + pair[1] + ", all of its trade", ok);
                mapped++;
            }
            assertTrue("fixture: most of the nations named are in the file (" + mapped + " of " + expect.length + ")", mapped >= expect.length - 3);
            double[] weight = new double[nations.count() + 1];
            for (int n = 1; n <= nations.count(); n++) weight[n] = 1 + (n * 37L) % 11;
            double[] shares = Game.tradeSharesByMoney(moneys, nations, year, weight);
            near("the whole world's trade, weighted, maps to shares that sum to 1", Arrays.stream(shares).sum(), 1, 1e-12);
            double otherLargest = 0;
            for (int k = 2; k < shares.length; k++) otherLargest = Math.max(otherLargest, shares[k]);
            assertTrue("...the dollar the largest of them (the free floats and the dollar's pegs, " + String.format("%.0f%%", shares[0] * 100) + "), the euro (" + String.format("%.0f%%", shares[1] * 100)
                    + ") the largest of the other seven", shares[0] > shares[1] && shares[1] > otherLargest);
            double[] zero = Game.tradeSharesByMoney(moneys, nations, year, new double[nations.count() + 1]);
            assertTrue("a trade of nothing maps to all zero, which the vault reads as all US$", Arrays.stream(zero).sum() == 0);
            ForeignAccounts fx = rated();
            fx.setTradeShares(zero);
            fx.setReserveMix(MATCH_TRADE);
            fx.buyReserves(1_000);
            same("...and a purchase under MATCH_TRADE with that buys all US$", fx.getHolding(0), 1_000);
        }
    }

    /* =====================================================================
       6. THE SAVE
       ===================================================================== */

    static void theSave() {
        out.println("--- 6. the save: the eight balances, their rates, the mix and the cross move ride the array ---");
        ForeignAccounts a = new ForeignAccounts();
        a.pinRate(1.0);
        a.setMoneyRates(R);
        a.buyReserves(10_000, "USD");
        a.buyReserves(8_000, "EUR");
        a.buyReserves(6_000, "GBP");
        a.buyReserves(4_000, "CHF");
        a.revalueVault();
        a.setReserveMix(CHOSEN);
        a.setChosenShares(new double[] {.4, .3, 0, .2, 0, 0, 0, .1});
        a.setTradeShares(new double[] {5, 3, 1, 1, 0, 0, 0, 0});
        double[] month2 = R.clone();
        month2[1] = 1.04;
        month2[3] = 1.31;
        month2[7] = 1.05;
        a.setMoneyRates(month2);
        assertTrue("fixture: a vault in four moneys with a cross move this month and a driver", beyondTheDollar(a) > 0 && a.getLastVaultCrossUsd() != 0
                && a.getLastVaultCrossDriver() != null && a.getHolding(2) == 0);
        a.pinRate(1.2);
        a.revalueVault();

        double[] saved = a.toSaveArray();
        assertTrue("the array runs to slot 146, the vault's (105-141) and what it earned (142-146) (" + saved.length + " slots)", saved.length == SLOTS);
        same("slot 22 is still the vault's dollars, the total", saved[22], a.getReservesUsd());
        same("...and slot 19 its worth at home", saved[19], a.getReserves());
        boolean eight = true;
        for (int k = 0; k < ForeignAccounts.MONEYS; k++) eight &= saved[105 + k] == a.getHolding(k) && saved[113 + k] == (k == 0 ? 1 : a.getMoneyUsd(k) > 0 ? a.getMoneyUsd(k) : 0);
        assertTrue("slots 105-112 are the eight balances and 113-120 their US$ per unit (1 for the dollar)", eight);
        same("slot 121 is the mix's ordinal", saved[121], CHOSEN.ordinal());
        assertTrue("slots 122-129 are the chosen shares and 130-137 the trade's", Math.abs(saved[122] - .4) < 1e-12 && Math.abs(saved[123] - .3) < 1e-12
                && Math.abs(saved[125] - .2) < 1e-12 && Math.abs(saved[129] - .1) < 1e-12 && Math.abs(saved[130] - .5) < 1e-15);
        same("slot 138 is the month's cross move", saved[138], a.getLastVaultCrossUsd());
        same("slot 139 says it is booked", saved[139], 0);
        same("slot 140 is the driver's index", saved[140], a.getLastVaultCrossDriver().index());
        same("slot 141 its move against the dollar", saved[141], a.getLastVaultCrossDriver().move());

        ForeignAccounts b = new ForeignAccounts();
        b.restore(saved);
        assertTrue("restored, every slot reads back to the bit", sameArray(b.toSaveArray(), saved));
        boolean same = true;
        for (int k = 0; k < ForeignAccounts.MONEYS; k++) same &= Double.compare(a.getHolding(k), b.getHolding(k)) == 0 && Double.compare(a.getHoldingUsd(k), b.getHoldingUsd(k)) == 0
                && a.hasRate(k) == b.hasRate(k);
        assertTrue("...holdings, their US$ worth and which have a rate: the same", same);
        same("...the vault's dollars", b.getReservesUsd(), a.getReservesUsd());
        assertTrue("...the mix, the chosen shares, the trade shares and the shares in force", b.getReserveMix() == CHOSEN && sameArray(b.getChosenShares(), a.getChosenShares())
                && sameArray(b.getTradeShares(), a.getTradeShares()) && sameArray(b.mixShares(), a.mixShares()));
        same("...the cross move read for the page", b.getLastVaultCrossUsd(), a.getLastVaultCrossUsd());
        assertTrue("...and its driver", b.getLastVaultCrossDriver() != null && b.getLastVaultCrossDriver().equals(a.getLastVaultCrossDriver()));

        // The next month in both: the hand, the rate's move, the revaluation.
        double[] month3 = month2.clone();
        month3[1] = 1.01;
        month3[3] = 1.36;
        for (ForeignAccounts fx : new ForeignAccounts[] {a, b}) {
            fx.setMoneyRates(month3);
            fx.pinRate(1.5);
            fx.revalueVault();
        }
        same("the next month revalues the same, to the bit", b.getLastVaultRevaluation(), a.getLastVaultRevaluation());
        same("...with the same cross move", b.getLastVaultCrossUsd(), a.getLastVaultCrossUsd());
        assertTrue("...the same driver, and the same holdings", a.getLastVaultCrossDriver().equals(b.getLastVaultCrossDriver()) && sameArray(a.toSaveArray(), b.toSaveArray()));
        a.buyReserves(2_000);
        b.buyReserves(2_000);
        assertTrue("...and a purchase by the chosen mix goes the same way in both", sameArray(a.toSaveArray(), b.toSaveArray()) && a.getHolding(7) > 0);

        // Saved between the hand and the revaluation (never in a played game, which saves between presses): the move is still to book.
        ForeignAccounts c = new ForeignAccounts();
        c.pinRate(1.0);
        c.setMoneyRates(R);
        c.buyReserves(10_000, "EUR");
        c.revalueVault();
        c.setMoneyRates(month2);
        double[] midMonth = c.toSaveArray();
        same("a save between the hand and the revaluation says the move is still to book (slot 139)", midMonth[139], 1);
        ForeignAccounts d = new ForeignAccounts();
        d.restore(midMonth);
        c.pinRate(1.2);
        d.pinRate(1.2);
        c.revalueVault();
        d.revalueVault();
        same("...and the restored vault books it, as the unsaved one does", d.getLastVaultRevaluation(), c.getLastVaultRevaluation());

        // An older save of this format: slot 22 and nothing after 104 reads as all US$.
        double[] older = Arrays.copyOf(saved, 105);
        ForeignAccounts e = new ForeignAccounts();
        e.restore(older);
        same("a save that has no slot 105 on reads its slot 22 into the US$ balance", e.getHolding(0), saved[22]);
        assertTrue("...nothing in any other money, the default mix, no cross move", beyondTheDollar(e) == 0 && e.getReserveMix() == ALL_USD
                && e.getLastVaultCrossUsd() == 0 && Arrays.equals(e.mixShares(), new double[] {1, 0, 0, 0, 0, 0, 0, 0}));
        assertTrue("...and only the dollar has a rate until Game hands the rest", e.hasRate(0) && !e.hasRate(1));

        // A restore starts from nothing: a vault that held euros comes back without them.
        ForeignAccounts f = rated();
        f.buyReserves(5_000, "EUR");
        f.setReserveMix(WORLD_MIX);
        f.restore(older);
        assertTrue("restoring over a vault that held euros leaves none of them, and the default mix", beyondTheDollar(f) == 0 && f.getReserveMix() == ALL_USD);
        f.reset();
        assertTrue("reset() empties all eight, and sets the default mix and the US$ alone rated", f.getReservesUsd() == 0 && f.getReserveMix() == ALL_USD && !f.hasRate(1));
    }

    /* =====================================================================
       7. IN A CITY
       ===================================================================== */

    static void inACity() {
        out.println("--- 7. in a city (the full file): Game hands the eight their rates; a city that never touches the mix holds only US$ ---");
        if (WorldData.locate() == null) {
            out.println("  world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": the city's section did not run");
            return;
        }
        Game g = WorldMoneysCheck.town("vault-moneys-city");
        ForeignAccounts fx = g.getForeignAccounts();
        assertTrue("fixture: before its first month the city has no moneys and only the dollar has a rate", g.getWorldMoneys() == null && !fx.hasRate(1));
        for (int m = 1; m <= 3; m++) WorldMoneysCheck.play(g);
        WorldMoneys moneys = g.getWorldMoneys();
        assertTrue("fixture: three months on, the city has the world's moneys in its month", moneys != null && moneys.month() == WorldMoneys.monthIndex(g.getMonth()));
        if (moneys == null) return;
        boolean rated = true, cross = true;
        for (int k = 0; k < ForeignAccounts.MONEYS; k++) {
            rated &= fx.hasRate(k);
            cross &= Math.abs(fx.getMoneyUsd(k) * moneys.rate(WorldMoneys.EIGHT.get(k)) - 1) < 1e-12;
        }
        assertTrue("the first month handed all eight their rates", rated);
        assertTrue("...each the US$ per unit, 1 / the moneys' rate of the month", cross);
        assertTrue("a city that has not touched the mix holds only US$, with no cross move and no driver", beyondTheDollar(fx) == 0 && fx.getLastVaultCrossUsd() == 0
                && fx.getLastVaultCrossDriver() == null && fx.getReserveMix() == ALL_USD && fx.getReservesUsd() > 0);
        PartnerTrade.Snapshot snap = g.getPartnerTrade();
        boolean traded = snap != null && (snap.monthTotal() > 0 || !snap.yearCells().isEmpty());
        double[] shares = fx.getTradeShares();
        double sum = Arrays.stream(shares).sum();
        assertTrue("the city's trade by money was handed (the town traded with partners: " + traded + "), shares summing to 1: " + Arrays.toString(Arrays.stream(shares).map(s -> Math.round(s * 1000) / 1000.0).toArray()),
                traded && Math.abs(sum - 1) < 1e-12);

        // A named purchase of euros, and one by the world's mix.
        double cash = g.getCash();
        double eurBefore = fx.getHolding(1);
        double spend = Math.min(60_000, cash / 10);
        double got = g.buyForeignCurrency(spend, "EUR");
        near("a purchase of euros spends the cash it was asked", got, spend, 1e-12);
        near("...and puts spend / rate US$ of it in euros, in euros", fx.getHolding(1) - eurBefore, spend / fx.getRate() / fx.getMoneyUsd(1), 1e-9);
        assertTrue("...the other balances unchanged (still only the US$ and the euros)", fx.getHolding(0) > 0 && fx.getHolding(2) + fx.getHolding(3) + fx.getHolding(4) + fx.getHolding(5)
                + fx.getHolding(6) + fx.getHolding(7) == 0);
        fx.setReserveMix(WORLD_MIX);
        g.buyForeignCurrency(spend);
        boolean everyOne = true;
        for (int k = 0; k < ForeignAccounts.MONEYS; k++) everyOne &= fx.getHolding(k) > 0;
        assertTrue("a purchase by the world's mix puts something in each of the eight", everyOne);
        double usdSold = fx.getHoldingUsd(1);
        double sold = g.sellForeignCurrency(1e12, "EUR");
        near("selling the euros by name raises what they fetch, and no other balance is touched", sold, usdSold * fx.getRate(), 1e-9);
        same("...leaving none", fx.getHolding(1), 0);

        // One more month: the eight moved against each other under the vault.
        double rateBefore = fx.getRate();
        WorldMoneysCheck.play(g);
        double v1 = fx.getReservesUsd(), crossMove = fx.getLastVaultCrossUsd();
        assertTrue("fixture: the vault held seven moneys through the month, and they moved against each other", crossMove != 0 && fx.getLastVaultCrossDriver() != null);
        near("the month's revaluation in local money is the dollars at the rate's move plus the cross move at the last rate", fx.getLastVaultRevaluation(),
                v1 * (fx.getRate() - rateBefore) + crossMove * rateBefore, 1e-9);
        out.printf("   the cross move %+,.3f, driven by %s (%+.2f%% against the dollar); the revaluation in local money %+,.3f%n", crossMove,
                fx.getLastVaultCrossDriver().code(), 100 * fx.getLastVaultCrossDriver().move(), fx.getLastVaultRevaluation());
    }
}
