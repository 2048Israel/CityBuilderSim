package ham.citybuildersim;

import com.google.gson.Gson;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The city's paper in any of the eight moneys (CD8, Jerus 2026-10-23): the city sells bills and bonds abroad in the euro, the yen, the pound,
 * the yuan, the Canadian and Australian dollars and the franc as well as the US$, each on ITS money's curve - that bank's policy rate (the world's
 * price of money in it) plus the city's country premium plus the term premium - the way it sold only US$ paper before. Each piece keeps its books
 * in its own money and is read in D$ at the month's rate (the D$'s rate to the US$ times the money's US$ per unit); its buyback is CD4's
 * reverse auction as a dollar piece's is; its revaluation is CD7's exchange line, summed over the pieces at their own rates.
 *
 * WHY. WN4 gave the world eight moneys and the vault holds each, XR4 gave each money's bank a policy rate, and CD7 books what a move of the rate
 * does to the paper carried in another money - but the city could only owe the US$, so the one thing a treasury does to manage exchange risk (borrow
 * where it earns, or where the money is cheap, and hold what it borrowed in the money it owes) could not be done. The rule that matters most is the
 * one this proves last: US$ paper behaves as it always did, to the bit, and every other path is new.
 *
 * What this has to prove (the quiet city is the default founding on the goods' literals; the others have the world file):
 *
 *   1. Without the world file only the US$ is offered: a euro quote is empty and a euro sale is refused with a sentence, and the US$ quote
 *      through the new signature is the four-argument quote, to the last bit.
 *   2. With it, each of the eight is offered, and its curve at a tenor is that bank's policy rate plus the country premium plus the term
 *      premium (the US$'s is foreignCurveRate(m), exactly).
 *   3. A ten-year euro bond and a twelve-month yen bill: each piece's money, its quote's rate on that money's curve (the price the bill fetched is
 *      the curve's factor), its books in its own money (what it raised), the treasury's cash up by what it raised at the money's rate, and the
 *      country premium afterwards reading both.
 *   4. A month: each coupon is paid in D$ at the month's rate (its own coupon, the D$'s rate, its US$ per unit) and the money audit closes.
 *   5. A rate move: the D$ pinned 10% weaker for a press, and then the crosses moving alone: the exchange line is the sum over the pieces of the
 *      carrying value in its own money times its rate before less its rate after, each piece's rate after the D$'s times its US$ per unit.
 *   6. Held as reserves: a euro sale held raises the vault's euro holding by what it raised in euros, and no other money's holding moves.
 *   7. The buyback: the euro bond bought back whole at fair - its fair per 100 the value on its money's curve, all of it offered by the world, it
 *      leaves the books, the US$ the city owes falls by its US$ value, and the audit closes the month after.
 *   8. The rollover: a one-month euro bill falling due rolls into a euro bill - the plan's issue says euros and the new piece is written in euros.
 *   9. Across a save and a load: each piece's money, its rate and its carrying value in its own money, and each money's quote, come back the
 *      same, and the load books no exchange move.
 *  10. US$ paper in the same city as before: its rate is the D$'s exactly, it saves with no money, the US$ the city owes counts it at its own-money
 *      principal, and the US$'s every curve and quote is its old expression's.
 */
public class DebtMoneysCheck {

    static int fails = 0;
    static int asserted = 0;
    static PrintStream out;
    static PrintStream quiet;

    /** Everything here is in thousands, so a thousandth of a thousandth is plenty (DebtExchangeCheck.TOLERANCE). */
    static final double TOLERANCE = 1e-6;

    /** The month's exports (thousands of D$) every sale and quote is made against: the town sells nothing abroad, and the window shuts on a city that owes and earns none. */
    static final double EXPORTS = 5_000;

    /** What each piece is worth when it is sold, in thousands of US$ at the day's cross: the amount asked in each money is this over its US$ per unit. */
    static final double USD_ASK = 10_000;

    /** The share of its worth in US$ the D$ keeps in a fall: 0.90 is a D$ 10% weaker (DebtExchangeCheck.KEEPS). */
    static final double KEEPS = 0.90;

    /** The tenors, in months, the curves are read at. */
    static final int[] TENORS = {1, 12, 60, 120, 360};

    static void check(String label, boolean ok) {
        asserted++;
        if (!ok) fails++;
        out.printf("  %-110s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void near(String label, double actual, double expected) {
        near(label, actual, expected, MoneyAudit.tolerance(TOLERANCE, Math.max(Math.abs(actual), Math.abs(expected))));
    }

    static void near(String label, double actual, double expected, double tolerance) {
        asserted++;
        boolean ok = Math.abs(actual - expected) <= tolerance;
        if (!ok) {
            fails++;
            out.printf("  %-110s FAIL  %,.9f, expected %,.9f (+/- %g)%n", label, actual, expected, tolerance);
        } else {
            out.printf("  %-110s OK%n", label);
        }
    }

    static void quietly(Runnable work) {
        PrintStream shown = System.out;
        System.setOut(quiet);
        try { work.run(); } finally { System.setOut(shown); }
    }

    /** One press, its printing thrown away, the window abroad held open for it (the rollover reads it at the top of the press). */
    static void press(Game g) {
        open(g);
        quietly(g::toggleNextMonth);
    }

    /** The month's exports handed to the debt, so the window abroad is open and the country premium is the same figure every read: the press hands the real (nil) ones again. */
    static void open(Game g) {
        g.getDebtManager().setTrade(EXPORTS, 6);
    }

    /** A trading town on the default founding (InterestAbroadCheck.town), played three months so the world's moneys are handed to the debt. */
    static Game town(String label, boolean literals) {
        Game g = InterestAbroadCheck.town(label, false, literals);
        for (int i = 0; i < 3; i++) press(g);
        return g;
    }

    static List<Debt> foreignPieces(Game g) {
        List<Debt> list = new ArrayList<>();
        for (Debt d : g.getDebtManager().getDebt()) if (d.isForeign()) list.add(d);
        return list;
    }

    static double[] ratesOf(List<Debt> pieces) {
        double[] rates = new double[pieces.size()];
        for (int i = 0; i < rates.length; i++) rates[i] = pieces.get(i).getExchangeRate();
        return rates;
    }

    /** The one foreign piece on the books that was not there before. */
    static Debt newest(Game g, Set<Debt> had) {
        Debt found = null;
        for (Debt d : foreignPieces(g)) if (!had.contains(d)) found = d;
        return found;
    }

    /** What the asked amount is in this money's own units: USD_ASK at the day's cross. */
    static double askIn(Game g, String code) {
        return Math.ceil(USD_ASK / g.getDebtManager().usdPer(code));
    }

    static String sell(Game g, String type, String money, double ask, int duration, boolean hold) {
        open(g);
        String[] said = new String[1];
        quietly(() -> said[0] = g.handleForeignLogic(type, money, ask, duration, 100, hold));
        return said[0];
    }

    /** D$ per unit of this money by the vault's own figures: the D$'s rate to the US$ times the money's US$ per unit. */
    static double localPer(Game g, String code) {
        ForeignAccounts fx = g.getForeignAccounts();
        int k = ForeignAccounts.moneyIndex(code);
        return k == 0 ? fx.getRate() : fx.getRate() * fx.getMoneyUsd(k);
    }

    static boolean closes(Game g) {
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        return Math.abs(audit.residual) < MoneyAudit.tolerance(audit.moved());
    }

    /** What a press left that the rate sections read afterwards. */
    static final class Move {
        double line, byRule, rateBefore, rateAfter;
        boolean eachPieceAtItsRate = true;
    }

    /** Moves the D$ by this factor of its rate before the press (pinned, so the reprice leaves it where it is) and presses; the crosses move on their own path. */
    static Move move(Game g, double factorOfTheRate) {
        ForeignAccounts fx = g.getForeignAccounts();
        List<Debt> pieces = foreignPieces(g);
        double[] was = ratesOf(pieces);
        Move m = new Move();
        m.rateBefore = fx.getRate();
        fx.pinRate(m.rateBefore * factorOfTheRate);
        press(g);
        m.rateAfter = fx.getRate();
        for (int i = 0; i < pieces.size(); i++) {
            Debt d = pieces.get(i);
            int k = ForeignAccounts.moneyIndex(d.getMoney());
            double after = k == 0 ? m.rateAfter : m.rateAfter * fx.getMoneyUsd(k);
            m.byRule += d.carryingInCurrency() * (was[i] - after);
            m.eachPieceAtItsRate &= d.getExchangeRate() == after;
        }
        m.line = g.getEconomyManager().getNationalAccounts().getDebtExchangeGain();
        return m;
    }

    public static void main(String[] args) {
        out = System.out;
        quiet = new PrintStream(OutputStream.nullOutputStream());

        /* ============ 1. without the world file ============ */
        out.println("--- without the world file only the US$ is offered ---");

        Game lit = town("debt-moneys-literals", true);
        DebtManager litDm = lit.getDebtManager();
        open(lit);
        check("fixture: the city plays on the literals, with no policy rates and no moneys", lit.isOnLiterals() && lit.getPolicyRates() == null && lit.getWorldMoneys() == null);
        check("the US$ is offered", litDm.offers("USD"));
        boolean noOther = true;
        for (String code : WorldMoneys.EIGHT) if (!"USD".equals(code)) noOther &= !litDm.offers(code);
        check("...and none of the other seven is, nor a code that is none of the eight, nor null", noOther && !litDm.offers("XYZ") && !litDm.offers(null));
        check("a euro quote is empty, a euro piece in the book is not valued", lit.quoteForeign("Term", "EUR", USD_ASK, 10, 100).isEmpty());
        Set<Debt> litHad = new HashSet<>(foreignPieces(lit));
        double litCash = lit.getCash();
        String refused = sell(lit, "Term", "EUR", USD_ASK, 10, false);
        check("a euro sale is refused with a sentence, and books nothing and raises nothing",
                refused.startsWith("No lender abroad will take paper in euros") && newest(lit, litHad) == null && lit.getCash() == litCash);
        DebtQuote litUs = lit.quoteForeign("Term", "USD", USD_ASK, 10, 100);
        check("premise: the US$ quote is not empty (the window is open)", !litUs.isEmpty());
        check("the US$ quote through the new signature is the four-argument quote, to the last bit", litUs.equals(lit.quoteForeign("Term", USD_ASK, 10, 100)));
        check("...and a null money is the US$", lit.quoteForeign("Term", null, USD_ASK, 10, 100).equals(litUs));
        check("...and so is a bill", lit.quoteForeign("Note", "USD", USD_ASK, 6, 100).equals(lit.quoteForeign("Note", USD_ASK, 6, 100)));

        /* ============ 2. the curves ============ */
        out.println("\n--- with it, each of the eight is offered on its own bank's curve ---");

        Game w = town("debt-moneys-world", false);
        DebtManager dm = w.getDebtManager();
        ForeignAccounts fx = w.getForeignAccounts();
        PolicyRates policy = w.getPolicyRates();
        open(w);
        check("fixture: the city has the world's moneys and policy rates", w.getWorldMoneys() != null && policy != null);
        boolean allOffered = true;
        for (String code : WorldMoneys.EIGHT) allOffered &= dm.offers(code);
        check("every one of the eight is offered", allOffered);
        double premium = dm.countryPremium();
        check("fixture: the city owes nothing abroad, so the country premium is nothing", dm.getForeignPrincipal() == 0 && premium == 0);
        for (int k = 0; k < WorldMoneys.EIGHT.size(); k++) {
            String code = WorldMoneys.EIGHT.get(k);
            near(code + "'s world rate is that bank's policy rate", dm.worldRate(code), policy.rate(code));
            near(code + "'s US$ per unit is the vault's", dm.usdPer(code), k == 0 ? 1 : fx.getMoneyUsd(k));
            near(code + "'s D$ per unit is the D$'s rate times its US$ per unit", dm.rateOf(code), localPer(w, code));
            for (int m : TENORS) {
                near(code + " at " + m + " months: that bank's policy rate + the country premium + the term premium", dm.foreignCurveRate(code, m),
                        policy.rate(code) + dm.countryPremium() + DebtManager.termPremium(m));
            }
        }
        boolean usdIdentical = dm.foreignRate("USD") == dm.foreignRate() && dm.worldRate("USD") == dm.worldRate();
        for (int m : TENORS) usdIdentical &= dm.foreignCurveRate("USD", m) == dm.foreignCurveRate(m) && dm.foreignFactor("USD", m) == dm.foreignFactor(m);
        check("the US$'s curve through the new signature is the old expression, exactly, at every tenor", usdIdentical);
        near("the US$'s world rate is the US's policy rate", dm.worldRate(), policy.usRate());
        check("a money that is none of the eight has no US$ per unit", Double.isNaN(dm.usdPer("XYZ")) && Double.isNaN(dm.rateOf("XYZ")));

        /* ============ 3. a sale in each of two moneys ============ */
        out.println("\n--- a ten-year euro bond and a twelve-month yen bill, sold ---");

        Set<Debt> had = new HashSet<>(foreignPieces(w));
        double eurAsk = askIn(w, "EUR");
        DebtQuote eurQuote = w.quoteForeign("Term", "EUR", eurAsk, 10, 100);
        check("the euro quote is not empty (the window is open, the euro is offered)", !eurQuote.isEmpty());
        near("its rate before the sale is the euro's: the ECB's policy rate + the country premium", eurQuote.rateBefore(), policy.rate("EUR") + dm.countryPremium());
        near("the minimum issue in euros is the US$ figure over the euro's US$ per unit", w.minimumIssueSizeIn("EUR"), w.minimumIssueSizeUsd() / dm.usdPer("EUR"));
        check("...and in US$ it is the US$ figure itself", w.minimumIssueSizeIn("USD") == w.minimumIssueSizeUsd());
        double cashBefore = w.getCash();
        double eurLocalPer = localPer(w, "EUR");
        String said = sell(w, "Term", "EUR", eurAsk, 10, false);
        Debt eur = newest(w, had);
        check("the euro bond was issued abroad and converted", said.startsWith("Issued abroad and converted") && eur != null);
        check("the piece is foreign and written in euros", eur != null && eur.isForeign() && "EUR".equals(eur.getMoney()));
        near("it is valued at the D$'s rate times the euro's US$ per unit", eur.getExchangeRate(), eurLocalPer, 1e-12);
        near("its face, in euros, is the quote's", eur.faceInCurrency(), eurQuote.faceValue());
        near("its books open with what it raised, in euros", eur.carryingInCurrency(), eurQuote.cashReceived());
        near("the treasury's cash rose by what it raised in euros times the euro's D$", w.getCash() - cashBefore, eurQuote.cashReceived() * eurLocalPer);
        near("what the city owes abroad in local money is the piece at its own rate", dm.getForeignPrincipal(), eur.principalInCurrency() * eurLocalPer);
        double par = dm.worldCurveAt(dm.foreignRate("EUR")).parYield(120);
        check("its coupon is the par yield of the euro's curve with the piece on the books, rounded down to the coupon step",
                eurQuote.couponRate() <= par + 1e-9 && par - eurQuote.couponRate() < Auction.COUPON_STEP + 1e-9);
        double premiumAfterEur = dm.countryPremium();
        check("premise: the euro bond moved the country premium (it is read off the principal and the year's service)", premiumAfterEur > premium);

        had = new HashSet<>(foreignPieces(w));
        double jpyAsk = askIn(w, "JPY");
        DebtQuote jpyQuote = w.quoteForeign("Note", "JPY", jpyAsk, 12, 100);
        check("the yen quote is not empty", !jpyQuote.isEmpty());
        near("its rate before the sale is the yen's: the BOJ's policy rate + the country premium", jpyQuote.rateBefore(), policy.rate("JPY") + dm.countryPremium());
        cashBefore = w.getCash();
        double jpyLocalPer = localPer(w, "JPY");
        said = sell(w, "Note", "JPY", jpyAsk, 12, false);
        Debt jpy = newest(w, had);
        check("the yen bill was issued abroad and converted", said.startsWith("Issued abroad and converted") && jpy != null && "JPY".equals(jpy.getMoney()));
        near("it is valued at the D$'s rate times the yen's US$ per unit", jpy.getExchangeRate(), jpyLocalPer, 1e-12);
        double fee = w.costOfIssuance(jpyQuote.faceValue()) / jpyLocalPer;
        near("it fetched its face at the yen's curve for twelve months, less the fixed cost in yen (to the cent)", jpyQuote.cashReceived(),
                jpyQuote.faceValue() * dm.foreignFactor("JPY", 12) - fee, .006);
        near("its books open with what it raised, in yen", jpy.carryingInCurrency(), jpyQuote.cashReceived());
        near("the treasury's cash rose by what it raised in yen times the yen's D$", w.getCash() - cashBefore, jpyQuote.cashReceived() * jpyLocalPer);
        double owed = eur.principalInCurrency() * eurLocalPer + jpy.principalInCurrency() * jpyLocalPer;
        near("what the city owes abroad is both pieces, each at its own money's rate", dm.getForeignPrincipal(), owed);
        near("the US$ it owes is each piece's principal in its own money times that money's US$ per unit", dm.getForeignPrincipalUsd(),
                eur.principalInCurrency() * dm.usdPer("EUR") + jpy.principalInCurrency() * dm.usdPer("JPY"));
        near("what it owes in euros is the bond's principal, in yen the bill's", dm.getForeignPrincipalIn("EUR") + dm.getForeignPrincipalIn("JPY"),
                eur.principalInCurrency() + jpy.principalInCurrency(), 1e-9);
        check("...and in US$ it owes nothing but those", dm.getForeignPrincipalIn("USD") == 0 && dm.hasForeignDebtIn("EUR") && dm.hasForeignDebtIn("JPY") && !dm.hasForeignDebtIn("USD"));
        double annual = dm.getMonthlyExports() * 12;
        double service = dm.nextYearService();
        double risk = DebtManager.SOLVENCY_WEIGHT * dm.solvencyStressAt(owed) + DebtManager.SERVICE_WEIGHT * dm.serviceStressAt(service);
        near("the country premium afterwards reads both pieces: the solvency of their principal and the service of their year, no scar",
                dm.countryPremium(), Math.min(DebtManager.MAX_COUNTRY_PREMIUM, DebtManager.MAX_COUNTRY_PREMIUM * risk));
        check("...and it is above what the euro bond alone made of it (the bill's principal and its maturity are in the year), on exports of " + annual,
                dm.countryPremium() > premiumAfterEur);
        near("the euro's curve now reads that premium: the ECB's rate + it + the term premium at ten years", dm.foreignCurveRate("EUR", 120),
                policy.rate("EUR") + dm.countryPremium() + DebtManager.termPremium(120));

        // A US$ bond beside them, for the sections that follow (10 reads it).
        had = new HashSet<>(foreignPieces(w));
        double usdAsk = askIn(w, "USD");
        DebtQuote usdQuote = w.quoteForeign("Term", "USD", usdAsk, 10, 100);
        cashBefore = w.getCash();
        said = sell(w, "Term", "USD", usdAsk, 10, false);
        Debt usd = newest(w, had);
        check("a US$ bond beside them: issued abroad and converted, written in US$, valued at the D$'s rate exactly",
                said.startsWith("Issued abroad and converted") && usd != null && "USD".equals(usd.getMoney()) && usd.getExchangeRate() == fx.getRate());
        near("...and the cash rose by what it raised in US$ times the D$'s rate", w.getCash() - cashBefore, usdQuote.cashReceived() * fx.getRate());

        /* ============ 4. a month ============ */
        out.println("\n--- a month: each coupon paid in D$ at the month's rate ---");

        double eurCoupon = eur.couponInCurrency(), usdCoupon = usd.couponInCurrency();
        check("premise: the bond and the dollar bond owe a coupon this month, the bill none", eurCoupon > 0 && usdCoupon > 0 && jpy.couponInCurrency() == 0);
        fx.pinRate(fx.getRate());
        press(w);
        double rate = fx.getRate();
        double expected = eurCoupon * rate * fx.getMoneyUsd(1) + usdCoupon * rate;
        near("the interest the treasury paid abroad is each coupon in its own money times the D$'s rate and its US$ per unit", w.getForeignInterestPaidThisMonth(), expected);
        check("...and it is not the euro coupon at the D$'s rate alone", Math.abs(w.getForeignInterestPaidThisMonth() - (eurCoupon + usdCoupon) * rate) > 1000 * MoneyAudit.tolerance(TOLERANCE, expected));
        check("the month's money audit closes with paper in three moneys abroad", closes(w));
        near("the US$ the city owes counts the yen bill at its yen principal times the yen's US$ per unit", dm.getForeignPrincipalUsd(),
                eur.principalInCurrency() * dm.usdPer("EUR") + jpy.principalInCurrency() * dm.usdPer("JPY") + usd.principalInCurrency());

        /* ============ 5. a rate move ============ */
        out.println("\n--- the D$ pinned 10% weaker, then the crosses on their own: the exchange line is the sum over the pieces at their own rates ---");

        Move fall = move(w, 1 / KEEPS);
        near("the D$ moved by the factor the fixture meant, through the month's own reprice", fall.rateBefore / fall.rateAfter, KEEPS, 1e-12);
        check("premise: the line is large enough to tell a rule that books it from one that does not", Math.abs(fall.byRule) > 1000 * MoneyAudit.tolerance(TOLERANCE, Math.abs(fall.byRule)));
        near("the month's exchange line is the sum of each piece's carrying value in its own money times its rate before less its rate after", fall.line, fall.byRule);
        check("...each piece is at the D$'s rate times its money's US$ per unit now (the US$ piece at the D$'s rate)", fall.eachPieceAtItsRate);
        check("...and the D$ falling is a loss", fall.line < 0);
        check("the month closes in the audit", closes(w));

        double eurUsdBefore = fx.getMoneyUsd(1);
        double eurWas = eur.getExchangeRate(), jpyWas = jpy.getExchangeRate(), usdWas = usd.getExchangeRate();
        Move cross = move(w, 1.0);
        check("premise: the D$ was held and the euro moved on its own path against the US$", cross.rateAfter == cross.rateBefore && fx.getMoneyUsd(1) != eurUsdBefore);
        check("premise: the cross's move is large enough to tell a rule that books it from one that does not", Math.abs(cross.byRule) > 1000 * MoneyAudit.tolerance(TOLERANCE, Math.abs(cross.byRule)));
        near("the line is the same sum, with only the crosses moving", cross.line, cross.byRule);
        check("...each piece at its rate, and the US$ piece, the D$ held, where it was", cross.eachPieceAtItsRate && usd.getExchangeRate() == usdWas);
        near("...so the line is the euro and yen pieces' alone: each one's carrying value times what its own rate fell by", cross.line,
                eur.carryingInCurrency() * (eurWas - eur.getExchangeRate()) + jpy.carryingInCurrency() * (jpyWas - jpy.getExchangeRate()));
        check("the month closes in the audit", closes(w));

        /* ============ 6. held as reserves ============ */
        out.println("\n--- a euro sale held as reserves goes into the euro: a matched asset ---");

        double[] holdings = new double[ForeignAccounts.MONEYS];
        for (int k = 0; k < holdings.length; k++) holdings[k] = fx.getHolding(k);
        had = new HashSet<>(foreignPieces(w));
        double heldAsk = askIn(w, "EUR");
        open(w);
        DebtQuote heldQuote = w.quoteForeign("Term", "EUR", heldAsk, 5, 100);
        said = sell(w, "Term", "EUR", heldAsk, 5, true);
        Debt held = newest(w, had);
        check("a five-year euro bond sold held as reserves", said.startsWith("Issued abroad; the euros are in reserve") && held != null && "EUR".equals(held.getMoney()));
        near("the vault's euro holding rose by what the bond raised in euros", fx.getHolding(1) - holdings[1], heldQuote.cashReceived(), MoneyAudit.tolerance(TOLERANCE, heldQuote.cashReceived()) * 10);
        boolean others = true;
        for (int k = 0; k < holdings.length; k++) if (k != 1) others &= fx.getHolding(k) == holdings[k];
        check("...and no other money's holding moved", others);

        /* ============ 7. the buyback ============ */
        out.println("\n--- the euro bond bought back whole at fair ---");

        open(w);
        double principal = eur.getOustandingPrincipal();
        double usdOwed = dm.getForeignPrincipalUsd();
        double fairOnCurve = dm.worldCurveAt(dm.foreignRate("EUR")).pv(eur.remainingCashFlows()) / principal * 100;
        Buyback.Result quote = w.quoteBuyback(eur, 1e12, Double.NaN);
        near("its fair per 100 is its value on the euro's curve, not the US$'s", quote.fairPer100(), fairOnCurve, 1e-9);
        check("...which is not what the US$'s curve would have said", Math.abs(fairOnCurve - dm.worldCurveAt(dm.foreignRate()).pv(eur.remainingCashFlows()) / principal * 100) > 1e-6);
        near("the world offers all of it", quote.worldFace(), principal, 1e-6);
        check("...and nobody else does", quote.bankFace() + quote.householdFace() + quote.firmFace() == 0 && quote.takenFace() > 0);
        double eurOwn = eur.principalInCurrency();
        double cash0 = w.getCash();
        Buyback.Result deal = w.buyBack(eur, 1e12, Double.NaN);
        near("it takes the whole piece", deal.takenFace(), principal, 1e-6);
        check("the piece leaves the books", !dm.getDebt().contains(eur));
        near("the cash fell by what was paid", cash0 - w.getCash(), deal.paid(), 1e-6);
        near("the US$ the city owes fell by the piece's US$ value", usdOwed - dm.getForeignPrincipalUsd(), eurOwn * dm.usdPer("EUR"), 1e-6);
        check("the audit closes the month after", closes(playedMonth(w)));

        /* ============ 8. the rollover ============ */
        out.println("\n--- a three-month euro bill falling due rolls into a euro bill ---");

        w.setRolloverMode(Rollover.Mode.SAME_STRUCTURE);
        had = new HashSet<>(foreignPieces(w));
        sell(w, "Note", "EUR", askIn(w, "EUR"), 3, false);
        Debt bill = newest(w, had);
        check("fixture: a three-month euro bill is on the books, written in euros", bill != null && "EUR".equals(bill.getMoney()) && bill.getDuration() == 3);
        press(w);
        press(w);
        check("fixture: it falls due at the next press", dm.getDebt().contains(bill) && bill.principalDueNextMonth() > 0);
        Set<Debt> beforeRoll = new HashSet<>(foreignPieces(w));
        open(w);
        Rollover.Plan plan = w.rolloverPlan();
        Rollover.Issue issue = null;
        for (Rollover.Issue i : plan.issues()) if ("EUR".equals(i.money())) issue = i;
        check("the plan has an issue in euros: a three-month bill, abroad", issue != null && issue.foreign() && "Note".equals(issue.type()) && issue.term() == 3);
        check("...and says so: \"a 3-month bill in euros\"", issue != null && issue.paper().equals("a 3-month bill in euros"));
        press(w);
        Debt rolled = null;
        for (Debt d : foreignPieces(w)) if (!beforeRoll.contains(d) && d instanceof ShortTermTBill) rolled = d;
        check("the bill fell due and left the books", !dm.getDebt().contains(bill));
        check("a new bill was sold in its place, written in euros, valued at the euro's rate",
                rolled != null && "EUR".equals(rolled.getMoney()) && rolled.isForeign() && Math.abs(rolled.getExchangeRate() - localPer(w, "EUR")) < 1e-12);
        check("the month closes in the audit", closes(w));

        /* ============ 9. across a save and a load ============ */
        out.println("\n--- across a save and a load: each piece's money, its rate and its books come back, and the load books no exchange move ---");

        open(w);
        quietly(() -> w.saveGame(2));
        Game back = new Game(w.getGameFiles());
        quietly(() -> back.loadGame(2));
        open(back);
        DebtManager backDm = back.getDebtManager();
        ForeignAccounts backFx = back.getForeignAccounts();
        List<Debt> was = foreignPieces(w), now = foreignPieces(back);
        boolean same = was.size() == now.size() && was.size() >= 4;
        for (int i = 0; same && i < was.size(); i++) {
            Debt a = was.get(i), b = now.get(i);
            same = java.util.Objects.equals(a.getMoney(), b.getMoney()) && a.getExchangeRate() == b.getExchangeRate()
                    && a.carryingInCurrency() == b.carryingInCurrency() && a.principalInCurrency() == b.principalInCurrency();
        }
        if (!same) for (int i = 0; i < Math.min(was.size(), now.size()); i++) {
            Debt a = was.get(i), b = now.get(i);
            out.printf("   piece %d: %s/%s rate %s/%s carrying %s/%s principal %s/%s%n", i, a.getMoney(), b.getMoney(), a.getExchangeRate(), b.getExchangeRate(),
                    a.carryingInCurrency(), b.carryingInCurrency(), a.principalInCurrency(), b.principalInCurrency());
        }
        check("every foreign piece came back with its money, its rate, its carrying value and its principal, in its own money", same);
        boolean quotes = true;
        for (String code : WorldMoneys.EIGHT) {
            double ask = askIn(w, code);
            DebtQuote saved = w.quoteForeign("Term", code, ask, 10, 100);
            quotes &= !saved.isEmpty() && back.quoteForeign("Term", code, ask, 10, 100).equals(saved)
                    && backDm.foreignCurveRate(code, 120) == dm.foreignCurveRate(code, 120)
                    && backDm.offers(code) == dm.offers(code);
        }
        check("each money's quote and curve is the same reloaded, bit for bit: the rates were handed back to the debt", quotes);
        check("the reloaded debt was handed the vault's restored crosses: the euro's US$ per unit and its D$ are the vault's",
                backDm.usdPer("EUR") == backFx.getMoneyUsd(1) && Math.abs(backDm.rateOf("EUR") - localPer(back, "EUR")) < 1e-12 && backDm.usdPer("EUR") == dm.usdPer("EUR"));
        near("the reloaded debt revalues at the vault's rates and books nothing: a load is no revaluation", backDm.revalueForeign(backFx.getRate()), 0, 1e-9);
        near("the US$ it owes is the same", backDm.getForeignPrincipalUsd(), dm.getForeignPrincipalUsd());
        backFx.pinRate(backFx.getRate());
        fx.pinRate(fx.getRate());
        press(back);
        press(w);
        NationalAccounts twin = w.getEconomyManager().getNationalAccounts(), reloaded = back.getEconomyManager().getNationalAccounts();
        near("the reloaded city's next month books the same exchange line as the twin that was never saved", reloaded.getDebtExchangeGain(), twin.getDebtExchangeGain());

        /* ============ 10. US$ paper as before ============ */
        out.println("\n--- US$ paper in the same city is as it was ---");

        Gson gson = new Gson();
        String usdJson = gson.toJson(usd), eurJson = gson.toJson(held);
        check("a US$ piece saves with no money key, exactly as before; a euro piece with its code", !usdJson.contains("\"money\"") && eurJson.contains("\"money\":\"EUR\""));
        open(w);
        check("its rate is the D$'s rate exactly", usd.getExchangeRate() == dm.getExchangeRate() && usd.getExchangeRate() == fx.getRate());
        double owedUsd = 0;
        for (Debt d : foreignPieces(w)) owedUsd += d.principalInCurrency() * ("USD".equals(d.getMoney()) ? 1 : dm.usdPer(d.getMoney()));
        near("the US$ the city owes counts the US$ piece at its own-money principal", dm.getForeignPrincipalUsd(), owedUsd);
        boolean quotesAsBefore = true;
        for (double extra : new double[] {0, 500, 20_000}) {
            quotesAsBefore &= dm.quoteForeignRate("USD", extra) == dm.quoteForeignRate(extra)
                    && dm.quoteForeignRate("USD", extra, 120) == dm.quoteForeignRate(extra, 120);
        }
        for (int m : TENORS) quotesAsBefore &= dm.foreignCurveRate("USD", m) == dm.foreignCurveRate(m);
        check("the US$'s quoted rates and curve are the old expressions', exactly", quotesAsBefore);
        check("the US$ quote through the new signature is the four-argument quote in the world city too",
                w.quoteForeign("Term", "USD", usdAsk, 10, 100).equals(w.quoteForeign("Term", usdAsk, 10, 100)));

        DebtManager bare = new DebtManager();
        Debt bareUsd = bare.addLongTermBond(10_000, 120, 0, .05, true);
        bare.setExchangeRate(1.40);
        check("on a bare ledger the US$ bond is valued at the rate and counted at its own principal, no money having been handed",
                bare.getForeignPrincipalUsd() == 10_000 && bare.getForeignPrincipal() == 10_000 * 1.40 && bare.rateOf("USD") == 1.40 && bare.hasForeignDebtIn("USD")
                        && !bare.offers("EUR"));
        bare.setMoneyUsd(new double[] {1, 1.10, .0075, 1.30, .14, .75, .66, 1.10});
        bare.addLongTermBond(10_000, 120, 0, .05, "EUR");
        Debt bareEur = null;
        for (Debt d : bare.getDebt()) if ("EUR".equals(d.getMoney())) bareEur = d;
        near("...then a euro bond booked in is valued at the D$'s rate times the euro's US$ per unit", bareEur.getExchangeRate(), 1.40 * 1.10, 1e-12);
        bare.setMoneyUsd(new double[] {1, Double.NaN, .0075, 1.30, .14, .75, .66, 1.10});
        check("a money with no rate this month is not offered, and the paper already on the books keeps the last good rate", !bare.offers("EUR") && bare.usdPer("EUR") == 1.10);
        double booked = bare.revalueForeign(1.40 * 1.25);
        near("the bare ledger's exchange line moves a euro piece by the D$'s move times its US$ per unit (the US$ piece by the D$'s)", booked,
                bareUsd.carryingInCurrency() * (1.40 - 1.40 * 1.25) + bareEur.carryingInCurrency() * (1.40 * 1.10 - 1.40 * 1.25 * 1.10), 1e-6);

        out.printf("%n(%d assertions)%n", asserted);
        out.println(fails == 0
                ? "\nThe city sells and owes its paper in any of the eight moneys, on each money's own curve, books it in its own money, reads it in D$ at the month's rates, and its US$ paper is as it was."
                : "\n" + fails + " FAILED");
        if (fails > 0) System.exit(1);
    }

    /** A month played, the audit of it left for the caller to read. */
    static Game playedMonth(Game g) {
        press(g);
        return g;
    }
}
