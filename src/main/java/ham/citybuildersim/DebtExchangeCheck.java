package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The exchange gain (loss) on the city's foreign-currency debt (CD7, spec-city-debt.md section 5): each dollar
 * piece carried in US$ and read in D$ at the rate it was last valued at, so a month in which the D$ moves moves
 * the D$ value of the debt, and the books say so - a line below the operating lines, in the budget's result and
 * not in its totals, not cash, taken back out by the treasury bridge, and left out of every rule that is about
 * cash.
 *
 * WHY. CD3 put the dollar paper on the books in dollars and CD6 printed what closing a line would book, but the
 * D$'s own move on what is still owed was booked nowhere: the debt screen's figure moved, the budget's did not,
 * and a currency gain would have netted a rollover and paid a fund in with D$ nobody held. The rule is the one
 * ForeignAccounts.revalueVault() values the vault by - the carrying value in its own money times the change in
 * the rate - applied to each piece at the rate it was last valued at.
 *
 * What this has to prove, on a city that borrows in dollars and whose D$ is made to move by exactly the factor
 * the section names (ForeignAccounts.pinRate() before the press, so the move is the reprice's to book):
 *
 *   1. The fixture: dollar paper outstanding with its books open, domestic paper beside it, and a month in which
 *      the D$ does not move books nothing.
 *   2. The D$ falls 10%: the month's line is the sum over the dollar pieces of the carrying value in US$ times
 *      the change in the rate, to the cent; below nothing; the domestic paper adds nothing; the result carries it
 *      and the totals do not; the month after books nothing again.
 *   3. The bridge foots: the not-cash row takes the line out, EARNED walks to the budget with it a step, and the
 *      residuals are what they are in a month with no move.
 *   4. MoneyAudit is untouched: the month closes as it does without a move.
 *   5. The book after the fall: a dollar line sold before it reads -10% of what it is carried at, the domestic
 *      line nothing, the desk its lines' sums.
 *   6. The D$ rises back: a gain, each piece at its own last rate, positive; a piece sold in between is booked
 *      from the rate it was sold at.
 *   7. The book after the rise: two sale rates on two lines.
 *   8. The cash rules leave it out: the rollover's netting, the fund's pay-in and the fund's floor read the
 *      history's surplus less the line, and the history keeps the line so spending is recoverable.
 *   9. Across a save and a load: the line, its history, each sale's rate come back, and the reloaded city books
 *      what an unsaved twin books - nothing at the load, the same in the next move.
 *  10. The rule alone, on a bare ledger: a rate of nothing is refused, each piece moves from its own rate, a
 *      piece sold at two rates remembers their proceeds-weighted mean, and a change of unit scales it.
 *  11. The student grant on a surplus share is a share of the CASH surplus (SG1), on a city with students, a cash
 *      surplus and dollar paper: the month the D$ rises 10% its exchange gain is in the balance and not in the grant,
 *      the month it falls 10% the loss that takes the balance below nothing does not take the grant to nothing, and
 *      the grant is what a month in which the D$ did not move would pay.
 *  12. The same for the gain on paper bought back below its book: the balance holds it, the cash does not (the
 *      price paid was the cash), and the grant is the share of the cash.
 */
public class DebtExchangeCheck {

    static int fails = 0;
    static int asserted = 0;
    static PrintStream out;

    /** The D$'s rate the city's dollar paper is first sold at, D$ per US$ (the fixture pins it there). */
    static final double OPENING = 1.25;

    /** The share of its worth in US$ the D$ keeps in a fall: 0.90 is a D$ 10% weaker; the rise back is the same factor the other way. */
    static final double KEEPS = 0.90;

    /** The share of the surplus the grant is set to in section 11. */
    static final double SHARE = .5;

    /** The dollar bond section 11 sells, in US$ thousands: large beside the surplus, so its exchange line is. */
    static final double GRANT_BOND = 60_000;

    /** The domestic bond section 12 sells and buys back, in thousands: large beside the surplus, so its gain is. */
    static final double BUYBACK_BOND = 100_000;

    /** Everything here is in thousands, so a thousandth of a thousandth is plenty (TreasuryCheck.TOLERANCE). */
    static final double TOLERANCE = 1e-6;

    static void check(String label, boolean ok) {
        asserted++;
        if (!ok) fails++;
        out.printf("  %-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Two figures of the books that must agree to the tolerance MoneyAudit gives at their size, the figures printed on a failure. */
    static void near(String label, double actual, double expected) {
        near(label, actual, expected, MoneyAudit.tolerance(TOLERANCE, Math.max(Math.abs(actual), Math.abs(expected))));
    }

    static void near(String label, double actual, double expected, double tolerance) {
        asserted++;
        boolean ok = Math.abs(actual - expected) <= tolerance;
        if (!ok) {
            fails++;
            out.printf("  %-100s FAIL  %,.9f, expected %,.9f (+/- %g)%n", label, actual, expected, tolerance);
        } else {
            out.printf("  %-100s OK%n", label);
        }
    }

    /** One press, its printing thrown away. */
    static void press(Game g) {
        PrintStream shown = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        try { g.toggleNextMonth(); } finally { System.setOut(shown); }
    }

    static void quietly(Runnable work) {
        PrintStream shown = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        try { work.run(); } finally { System.setOut(shown); }
    }

    static Game founded(String label) {
        Game g = new Game(GameFiles.scratch(label));
        g.newGame();
        return g;
    }

    static List<Debt> foreign(Game g) {
        List<Debt> list = new ArrayList<>();
        for (Debt d : g.getDebtManager().getDebt()) if (d.isForeign()) list.add(d);
        return list;
    }

    static List<Debt> domestic(Game g) {
        List<Debt> list = new ArrayList<>();
        for (Debt d : g.getDebtManager().getDebt()) if (!d.isForeign()) list.add(d);
        return list;
    }

    static NationalAccounts accounts(Game g) {
        return g.getEconomyManager().getNationalAccounts();
    }

    static double sizeOf(Game g) {
        return Math.max(Math.abs(g.getTreasuryOpening()), Math.abs(g.getTreasuryClosing()));
    }

    /** The rates each piece was last valued at, before a press, in the ledger's order. */
    static double[] ratesOf(List<Debt> pieces) {
        double[] rates = new double[pieces.size()];
        for (int i = 0; i < rates.length; i++) rates[i] = pieces.get(i).getExchangeRate();
        return rates;
    }

    /**
     * The month's exchange gain by the rule alone: for each piece, its carrying value in its own money (at the
     * reprice, which nothing after changes) times the rate it was last valued at less the rate now.
     */
    static double ruleGain(List<Debt> pieces, double[] ratesBefore, double rateAfter) {
        double gain = 0;
        for (int i = 0; i < pieces.size(); i++) gain += pieces.get(i).carryingInCurrency() * (ratesBefore[i] - rateAfter);
        return gain;
    }

    /** The same figure said as the D$ carrying value the pieces had at the old rate less the one they have at the new. */
    static double asCarryingMove(List<Debt> pieces, double[] ratesBefore) {
        double gain = 0;
        for (int i = 0; i < pieces.size(); i++) gain += pieces.get(i).carryingInCurrency() * ratesBefore[i] - pieces.get(i).getCarryingValue();
        return gain;
    }

    /**
     * A city with homes and the schools to fill, as the grant's own harness builds it (EducationCheck), so somebody is
     * studying; every tax at its ceiling, because the schools' running costs leave it a deficit otherwise and a deficit
     * pays no grant to tell; the grant a share of the surplus (SHARE); the D$ pinned at the opening rate and two
     * months pressed, the second of them with the rate pinned, as section 1 does.
     */
    static Game schooledCity(String label) {
        Game g = EducationCheck.city(GameFiles.scratch(label));
        quietly(() -> EducationCheck.schools(g));
        TaxPolicy dials = g.getEconomyManager().getTaxPolicy();
        dials.setIncomeTaxRate(TaxPolicy.MAX_INCOME_TAX);
        dials.setProfitTaxRate(TaxPolicy.MAX_INCOME_TAX);
        dials.setSalesTaxRate(TaxPolicy.MAX_INCOME_TAX);
        dials.setPropertyTaxRate(TaxPolicy.MAX_PROPERTY_TAX);
        dials.setGrant(TaxPolicy.GrantBasis.SURPLUS_SHARE, SHARE);
        press(g);
        g.getForeignAccounts().pinRate(OPENING);
        press(g);
        g.getDebtManager().setTrade(10_000_000, 6);   // the window abroad shuts on a city that sells nothing abroad (section 6)
        return g;
    }

    /** Presses, the D$ not moving, until somebody is studying and the month closed on a cash surplus to share; returns that month. */
    static Month untilSharing(Game g) {
        Month month;
        int waited = 0;
        do {
            month = move(g, 1.0);
            waited++;
        } while (waited < 23 && (g.getFamilies().getSeekers(FamilyModel.Seeker.STUDENT) < 1 || cashByRule(g) <= 0));
        return month;
    }

    /**
     * The month's cash surplus by the rule alone: the balance less the lines it books that no cash moves on - the
     * gain on paper retired and the exchange gain - and plus the discount amortised and the uplift, which it charges
     * and the treasury never pays. Read off the accounts, not off the bridge's not-cash row the game reads.
     */
    static double cashByRule(Game g) {
        NationalAccounts na = accounts(g);
        return na.getBalance() - na.getRetiredGain() - na.getDebtExchangeGain() + na.getInterestNotCash() + na.getUpliftNotCash();
    }

    /** The journal's or the walk's entry with this label, or null. */
    static TreasuryJournal.Entry entry(List<TreasuryJournal.Entry> entries, String label) {
        for (TreasuryJournal.Entry e : entries) if (e.label().equals(label)) return e;
        return null;
    }

    /** What a month's press left that the sections read afterwards. */
    static final class Month {
        final double rateBefore, rateAfter, line, gainByRule, gainAsCarrying, domesticWould;
        final MoneyAudit.Result audit;

        Month(Game g, double rateBefore, List<Debt> abroad, double[] ownBefore, List<Debt> home, double[] homeBefore) {
            this.rateBefore = rateBefore;
            this.rateAfter = g.getForeignAccounts().getRate();
            this.line = accounts(g).getDebtExchangeGain();
            this.gainByRule = ruleGain(abroad, ownBefore, rateAfter);
            this.gainAsCarrying = asCarryingMove(abroad, ownBefore);
            // What the same rule would have booked had it counted the domestic paper too (it is carried in D$, and every
            // piece is told the rate): the figure a rule that forgot to ask whether the paper is foreign would add.
            this.domesticWould = ruleGain(home, homeBefore, rateAfter);
            this.audit = g.getLastMoneyAudit();
        }
    }

    /** Moves the D$ by this factor of its rate before the press (pinned, so the reprice leaves it where it is) and presses. */
    static Month move(Game g, double factorOfTheRate) {
        ForeignAccounts fx = g.getForeignAccounts();
        List<Debt> abroad = foreign(g), home = domestic(g);
        double[] own = ratesOf(abroad), homeOwn = ratesOf(home);
        double before = fx.getRate();
        fx.pinRate(before * factorOfTheRate);
        press(g);
        return new Month(g, before, abroad, own, home, homeOwn);
    }

    public static void main(String[] args) {
        out = System.out;

        Game city = founded("debt-exchange");
        ForeignAccounts fx = city.getForeignAccounts();
        DebtManager dm = city.getDebtManager();
        NationalAccounts na = accounts(city);
        List<Double> spent = new ArrayList<>();   // each press's budget expenses, for section 8's independent form of the history

        /* ============ 1. the fixture ============ */
        out.println("--- the fixture: dollar paper outstanding with its books open, domestic paper beside it ---");

        press(city);
        spent.add(na.getTotalExpenses());
        fx.pinRate(OPENING);
        press(city);
        spent.add(na.getTotalExpenses());
        check("fixture: the D$ stands at the opening rate, and the debt has been told it by the month's reprice",
                fx.getRate() == OPENING && dm.getExchangeRate() == OPENING && city.foreignWindowOpen());
        String said = quote(city, "Term", 20_000, 5);
        quietly(() -> {
            city.handleLongBondLogic(30_000, 5, 1000);
            city.handleTBillLogic(10_000, 12, 1000);
        });
        List<Debt> abroad = foreign(city);
        List<Debt> home = domestic(city);
        check("fixture: a five-year dollar bond was issued abroad, and the city has domestic paper too (a term bond and a bill)",
                said.startsWith("Issued abroad") && abroad.size() == 1 && home.size() == 2);
        Debt a = abroad.get(0);
        check("fixture: the dollar bond's books are open (it is carried in dollars) and its sale kept the rate it was made at",
                a.carryingInCurrency() > 0 && a.getSales().size() == 1 && a.getSales().get(0).getRate() == OPENING);
        double homeCarrying = 0;
        for (Debt d : home) homeCarrying += d.getCarryingValue();
        check("fixture: ...the domestic paper is carried at something, so a rule that counted it would book something",
                homeCarrying > 0);
        out.printf("   the dollar bond: US$%,.2fk of face, carried at US$%,.2fk, sold with the D$ at %.4f; D$%,.2fk of domestic paper beside it%n",
                a.faceInCurrency(), a.carryingInCurrency(), OPENING, homeCarrying);

        for (int i = 0; i < 3; i++) {
            Month quiet = move(city, 1.0);
            spent.add(na.getTotalExpenses());
            check("a month in which the D$ does not move books no exchange line, month " + (i + 1) + " of 3",
                    quiet.rateAfter == quiet.rateBefore && quiet.line == 0);
        }
        MoneyAudit.Result quietAudit = city.getLastMoneyAudit();
        double quietResidual = city.getTreasuryResidual(), quietEarned = city.getEarnedResidual();
        double quietSize = sizeOf(city);

        /* ============ 2. the D$ falls 10% ============ */
        out.println("\n--- the D$ falls 10%: the month books what the dollars cost more of it ---");

        Month fall = move(city, 1 / KEEPS);
        spent.add(na.getTotalExpenses());
        out.printf("   the D$ went from %.6f to %.6f a US$ (a factor of %.6f); the month booked D$%,.4fk on the dollar paper%n",
                fall.rateBefore, fall.rateAfter, fall.rateAfter / fall.rateBefore, fall.line);
        check("premise: the D$ moved by the factor the fixture meant - 10% weaker, a US$ dearer by a ninth - through the month's own reprice",
                Math.abs(fall.rateBefore / fall.rateAfter - KEEPS) < 1e-12 && dm.getExchangeRate() == fall.rateAfter
                        && a.getExchangeRate() == fall.rateAfter);
        check("premise: the move is large enough to tell a rule that books it from one that does not",
                Math.abs(fall.gainByRule) > 1000 * MoneyAudit.tolerance(TOLERANCE, Math.abs(fall.gainByRule)));
        near("the month's line is the dollar pieces' carrying value in US$ times the change in the rate, to the cent", fall.line, fall.gainByRule);
        near("...said again as the D$ they were carried at before less the D$ they are carried at now", fall.line, fall.gainAsCarrying);
        check("...and the D$ falling is a LOSS: below nothing", fall.line < 0);
        check("premise: the domestic paper has been told the same rate, and a rule that counted it would have booked D$"
                        + String.format("%,.2fk", fall.domesticWould) + " more",
                Math.abs(fall.domesticWould) > 1000 * MoneyAudit.tolerance(TOLERANCE, Math.abs(fall.domesticWould)));
        check("...and the domestic paper adds nothing: the line is not what counting it too would have booked",
                Math.abs(fall.line - (fall.gainByRule + fall.domesticWould)) > .5 * Math.abs(fall.domesticWould));
        near("the budget's result carries it below the operating lines: balance less (revenue - expenses + gain on debt retired) is the line",
                na.getBalance() - (na.getTotalRevenue() - na.getTotalExpenses() + na.getRetiredGain()), fall.line);
        MoneyAudit.Result fallAudit = fall.audit;

        /* ============ 3. the bridge foots ============ */
        out.println("\n--- and the treasury bridge takes it out again, because no cash moved on it ---");

        near("the not-cash row is the discount amortised and the uplift not paid, less the gain on debt retired and less the exchange line",
                city.getTreasuryNotCash(), na.getInterestNotCash() + na.getUpliftNotCash() - na.getRetiredGain() - fall.line);
        double journal = 0;
        for (TreasuryJournal.Entry e : city.getTreasuryJournal()) journal += e.amount();
        near("the bridge foots with the line in it: surplus + not cash + raised - repaid + journal + residual is the cash's change",
                city.getTreasurySurplus() + city.getTreasuryNotCash() + city.getTreasuryRaised() - city.getTreasuryRepaid()
                        + journal + city.getTreasuryResidual(),
                city.getTreasuryChange(), MoneyAudit.tolerance(TOLERANCE, sizeOf(city)));
        double fallResidual = city.getTreasuryResidual();
        near("...and what it leaves over is what a month with no move left over (the line did not slip into it)", fallResidual, quietResidual,
                MoneyAudit.tolerance(TOLERANCE, Math.max(sizeOf(city), quietSize)) + .05 * Math.abs(quietResidual));
        check("premise: had the bridge not taken the line out, the residual would have carried it and missed by more than that",
                Math.abs(fall.line) > 10 * (MoneyAudit.tolerance(TOLERANCE, Math.max(sizeOf(city), quietSize)) + .05 * Math.abs(quietResidual)));
        TreasuryJournal.Entry walk = entry(city.getEarnedToBudget(), "Exchange gain (loss) on foreign debt");
        check("EARNED walks to the budget with the line as a step of its own", walk != null);
        near("...the step is the line", walk == null ? Double.NaN : walk.amount(), fall.line);
        // What the walk leaves in a city with dollar paper is the dollar coupon, which EARNED and its steps do not carry
        // (it stands at D$ -377 in every month of the fixture whatever the line does, and moves with the D$): a gap the
        // walk had before the line existed. The line must not be in it: the residual of the month of the move is the quiet
        // month's, moved by the D$ as the coupon is (to a ten-thousandth, the amortisation drifting the coupon's expense).
        double scaledQuiet = quietEarned * fall.rateAfter / fall.rateBefore;
        near("...and the walk comes to the budget: what it leaves is a quiet month's dollar coupon at the new D$, and nothing of the line",
                city.getEarnedResidual(), scaledQuiet, 1e-4 * Math.abs(scaledQuiet));
        check("premise: the line is far larger than that tolerance, so a walk that left it in the residual would have missed",
                Math.abs(fall.line) > 100 * 1e-4 * Math.abs(scaledQuiet));

        /* ============ 4. MoneyAudit untouched ============ */
        out.println("\n--- and MoneyAudit does not see it: it is a revaluation, not a flow ---");

        out.printf("   a month with no move: %s%n   the month of the fall:  %s%n", quietAudit, fallAudit);
        check("the month of the fall closes to the cent as a month with no move does",
                Math.abs(fallAudit.residual) < MoneyAudit.tolerance(fallAudit.moved())
                        && Math.abs(quietAudit.residual) < MoneyAudit.tolerance(quietAudit.moved()));
        check("...though the line is far larger than the tolerance: had a pool moved on it, the audit would have said so",
                Math.abs(fall.line) > 100 * MoneyAudit.tolerance(fallAudit.moved()));

        /* ============ 5. the book after the fall ============ */
        out.println("\n--- the book after the fall: a dollar line sold before it reads what the D$'s move has done to what it carries ---");

        BookDesk desk = city.bookDesk();
        BookDesk.Line lineA = BookDesk.line(dm, a);
        double soldAt = a.getSales().get(0).getRate();
        near("the dollar line's exchange share is minus the D$'s fall: -(1 - rate then / rate now)", lineA.exchangeShare(), -(1 - soldAt / fall.rateAfter), 1e-12);
        near("...a D$ 10% weaker reads -10% of what the line is carried at", lineA.exchangeShare(), -(1 - KEEPS), 1e-12);
        near("...and what it has done, in D$, is the carrying value in US$ times the rate then less the rate now",
                lineA.exchangeSinceSold(), a.carryingInCurrency() * (soldAt - fall.rateAfter));
        near("...the piece says the same, and it remembers the rate it was sold at", a.exchangeSinceSold(), lineA.exchangeSinceSold());
        near("...because a piece sold once has one rate: the mean of its sales' is that one", a.soldAtRate(), soldAt, 1e-12);
        double homeLines = 0;
        for (Debt d : home) {
            BookDesk.Line l = BookDesk.line(dm, d);
            homeLines += Math.abs(l.exchangeSinceSold()) + Math.abs(l.exchangeShare());
        }
        check("a domestic line has no exchange: nothing in D$ and nothing as a share, on either of the two", homeLines == 0);
        double sum = 0;
        for (BookDesk.Line l : desk.lines()) sum += l.exchangeSinceSold();
        near("the desk's exchange since the sale is the lines' sums", desk.exchangeSinceSold(), sum);
        near("...and its share is that over what the dollar lines are carried at", desk.exchangeShare(), sum / a.getCarryingValue());

        /* ============ 6. the D$ rises back ============ */
        out.println("\n--- the D$ rises back: a gain, each piece at the rate it was last valued at, and a piece sold in between from its own ---");

        Month quietAfter = move(city, 1.0);
        spent.add(na.getTotalExpenses());
        near("the month after a move books nothing again: the strike opened the line at nothing and no reprice moved it", quietAfter.line, 0, 0);
        near("...and the result is revenue less expenses and the gain on debt retired", na.getBalance(),
                na.getTotalRevenue() - na.getTotalExpenses() + na.getRetiredGain(), 0);

        // The window abroad shuts on a city that owes dollars and sells nothing abroad; the next press would tell the
        // manager the city's exports again, so the fixture tells it now - a city that earned abroad - and sells.
        dm.setTrade(10_000_000, 6);
        String saidB = quote(city, "Term", 8_000, 10);
        List<Debt> two = foreign(city);
        check("fixture: a second dollar bond is sold now, at the D$ the fall left, and keeps that as the rate it was sold at",
                saidB.startsWith("Issued abroad") && two.size() == 2 && two.get(1).getSales().get(0).getRate() == fall.rateAfter
                        && two.get(1).getExchangeRate() == fall.rateAfter);
        Debt b = two.get(1);
        Month rise = move(city, KEEPS);
        spent.add(na.getTotalExpenses());
        out.printf("   the D$ went from %.6f to %.6f a US$ (a factor of %.6f); the month booked D$%,.4fk on the dollar paper%n",
                rise.rateBefore, rise.rateAfter, rise.rateAfter / rise.rateBefore, rise.line);
        check("premise: the D$ moved by the factor the fixture meant - 10% stronger, back to where it was sold - through the month's own reprice",
                Math.abs(rise.rateAfter / rise.rateBefore - KEEPS) < 1e-12 && Math.abs(rise.rateAfter - OPENING) < 1e-12);
        near("the month's line is each dollar piece's carrying value times the change in the rate it was last valued at", rise.line, rise.gainByRule);
        near("...said again as the D$ they were carried at before less the D$ they are carried at now", rise.line, rise.gainAsCarrying);
        check("...and the D$ rising is a GAIN: above nothing", rise.line > 0);
        near("...the two pieces: the first is carried at its US$ times the fall back, the second likewise, each from the rate it was valued at",
                rise.line, (a.carryingInCurrency() + b.carryingInCurrency()) * (rise.rateBefore - rise.rateAfter));
        near("the bridge still takes it out as not cash", city.getTreasuryNotCash(),
                na.getInterestNotCash() + na.getUpliftNotCash() - na.getRetiredGain() - rise.line);
        double journal2 = 0;
        for (TreasuryJournal.Entry e : city.getTreasuryJournal()) journal2 += e.amount();
        near("...and foots", city.getTreasurySurplus() + city.getTreasuryNotCash() + city.getTreasuryRaised() - city.getTreasuryRepaid()
                + journal2 + city.getTreasuryResidual(), city.getTreasuryChange(), MoneyAudit.tolerance(TOLERANCE, sizeOf(city)));
        check("...and the audit closes in the month of the rise too", Math.abs(rise.audit.residual) < MoneyAudit.tolerance(rise.audit.moved()));

        /* ============ 7. the book after the rise ============ */
        out.println("\n--- the book after the rise: two dollar lines sold at two rates ---");

        BookDesk desk2 = city.bookDesk();
        BookDesk.Line lineA2 = BookDesk.line(dm, a), lineB2 = BookDesk.line(dm, b);
        near("the first line was sold at the rate the D$ is back to: nothing since", lineA2.exchangeSinceSold(), 0, MoneyAudit.tolerance(TOLERANCE, a.getCarryingValue()));
        near("the second was sold at the rate before the rise: a gain of its US$ times the difference of the two",
                lineB2.exchangeSinceSold(), b.carryingInCurrency() * (b.getSales().get(0).getRate() - rise.rateAfter));
        near("...as a share of what it is carried at: the D$ having risen 10%, the US$ it owes cost a tenth less, a ninth of what is carried now",
                lineB2.exchangeShare(), 1 / KEEPS - 1, 1e-12);
        near("the desk's exchange since the sale is the lines' sums", desk2.exchangeSinceSold(), lineA2.exchangeSinceSold() + lineB2.exchangeSinceSold());
        near("...its share is that over both dollar lines' carrying value", desk2.exchangeShare(),
                desk2.exchangeSinceSold() / (a.getCarryingValue() + b.getCarryingValue()), 1e-12);

        /* ============ 8. the cash rules leave it out ============ */
        out.println("\n--- the cash rules leave it out: the rollover's netting, the fund's pay-in and the fund's floor ---");

        HistorySave history = city.getHistorySave();
        double[] revenue = history.aligned("revenue"), surplus = history.aligned("surplus");
        double[] exchange = history.aligned("debtExchange"), retired = history.aligned("retiredGain");
        check("the history keeps the line beside the surplus, a month for a month", exchange.length == surplus.length && surplus.length == spent.size());
        double worstRecovery = 0;
        for (int i = 0; i < spent.size(); i++) {
            int at = revenue.length - spent.size() + i;
            worstRecovery = Math.max(worstRecovery, Math.abs(revenue[at] - surplus[at] + retired[at] + exchange[at] - spent.get(i)));
        }
        near("revenue less the surplus plus the gain on debt retired and the exchange line is the month's expenses, every month (to rounding)",
                worstRecovery, 0, .02);
        double windowExchange = 0, windowSurplus = 0;
        int from = Math.max(0, surplus.length - Rollover.NETTING_MONTHS);
        for (int i = from; i < surplus.length; i++) {
            windowExchange += exchange[i];
            windowSurplus += surplus[i];
        }
        check("premise: the year the netting reads holds the line, and it is large beside the surplus it would be netted against",
                surplus.length <= Rollover.NETTING_MONTHS && Math.abs(windowExchange) > 1);
        near("the rollover's netting reads the surplus less the line, over the months the city has lived", city.surplusOverLastYear(),
                windowSurplus - windowExchange);
        check("...which is not the budget's surplus the history keeps", Math.abs(city.surplusOverLastYear() - windowSurplus) > Math.abs(windowExchange) / 2);
        int k = CityCalendar.monthOfYear(city.getMonth());
        double yearSurplus = 0, yearExchange = 0;
        for (int i = Math.max(0, surplus.length - k); i < surplus.length; i++) {
            yearSurplus += surplus[i];
            yearExchange += exchange[i];
        }
        check("premise: this calendar year so far holds the moves", k >= spent.size() && Math.abs(yearExchange) > 1);
        near("the fund's pay-in reads this year's surplus so far less the line", city.surplusThisYearSoFar(), yearSurplus - yearExchange);
        double spending = 0, spendingWithout = 0;
        int months = 0;
        for (int i = from; i < surplus.length; i++) {
            spending += revenue[i] - surplus[i] + retired[i] + exchange[i];
            spendingWithout += revenue[i] - surplus[i] + retired[i];
            months++;
        }
        near("the fund's floor, a month of spending, is the months' expenses recovered from the history: revenue less surplus plus both gains",
                city.monthOfSpending(), Math.max(0, spending / months));
        check("...which is not what it would be without the exchange line", Math.abs(city.monthOfSpending() - Math.max(0, spendingWithout / months)) > .1);

        /* ============ 9. across a save and a load ============ */
        out.println("\n--- across a save and a load: the line, its history and each sale's rate come back, and no move is booked at the load ---");

        quietly(() -> city.saveGame(2));
        Game back = new Game(city.getGameFiles());
        quietly(() -> back.loadGame(2));
        ForeignAccounts backFx = back.getForeignAccounts();
        NationalAccounts backNa = accounts(back);
        near("the month's line came back with the government block", backNa.getDebtExchangeGain(), na.getDebtExchangeGain(), 0);
        check("premise: the line the save carried is the month of the rise, not nothing", backNa.getDebtExchangeGain() == rise.line && rise.line != 0);
        near("...and the result with it", backNa.getBalance(), na.getBalance(), 0);
        near("...and the bridge's not-cash row, which took it out", back.getTreasuryNotCash(), city.getTreasuryNotCash(), 0);
        check("the history's series came back, month for month",
                java.util.Arrays.equals(back.getHistorySave().aligned("debtExchange"), history.aligned("debtExchange")));
        List<Debt> backAbroad = foreign(back);
        boolean sameRates = backAbroad.size() == 2;
        for (int i = 0; sameRates && i < 2; i++) {
            Debt was = foreign(city).get(i), now = backAbroad.get(i);
            sameRates = now.getSales().size() == was.getSales().size() && now.getExchangeRate() == was.getExchangeRate()
                    && now.soldAtRate() == was.soldAtRate() && now.getSales().get(0).getRate() == was.getSales().get(0).getRate();
        }
        check("each dollar piece came back with the rate it was valued at, and with the rate each sale was made at", sameRates);
        near("...so the reloaded desk reads the exchange since the sale as the live one", back.bookDesk().exchangeSinceSold(), city.bookDesk().exchangeSinceSold());
        near("the cash rules read the same reloaded: the rollover's netting", back.surplusOverLastYear(), city.surplusOverLastYear());

        backFx.pinRate(backFx.getRate());
        press(city);
        press(back);
        near("the reloaded city's first month books no move (the load is no revaluation): its line is nothing", accounts(back).getDebtExchangeGain(), 0, 0);
        near("...and the unsaved twin's is nothing too", accounts(city).getDebtExchangeGain(), 0, 0);
        Month twin = move(city, 1 / KEEPS), reloaded = move(back, 1 / KEEPS);
        check("premise: both cities are at the same D$ and move by the same factor", twin.rateBefore == reloaded.rateBefore && twin.rateAfter == reloaded.rateAfter);
        near("the next move books the same in the reloaded city as in the twin that was never saved", reloaded.line, twin.line);
        check("...and it is a loss in both, as the same fall was", reloaded.line < 0 && twin.line < 0);

        /* ============ 10. the rule alone ============ */
        out.println("\n--- the rule alone, on a bare ledger: refused at nothing, each piece from its own rate, a mean of the rates sold at ---");

        DebtManager ledger = new DebtManager();
        Debt first = ledger.addLongTermBond(10_000, 120, 0, .05, true);
        Debt second = ledger.addLongTermBond(4_000, 120, 0, .05, true);
        Debt local = ledger.addLongTermBond(7_000, 120, 0, .05, false);
        ledger.setExchangeRate(1.2);
        second.setExchangeRate(1.1);   // a piece last valued at a rate of its own
        double[] before = { first.getExchangeRate(), second.getExchangeRate(), local.getExchangeRate() };
        near("a rate of nothing is refused: nothing returned", ledger.revalueForeign(0), 0, 0);
        near("...a negative one too", ledger.revalueForeign(-1), 0, 0);
        near("...and one that is no number", ledger.revalueForeign(Double.NaN), 0, 0);
        check("...and no piece was told anything by them", first.getExchangeRate() == before[0] && second.getExchangeRate() == before[1] && local.getExchangeRate() == before[2]);
        double booked = ledger.revalueForeign(1.5);
        near("each dollar piece moves from the rate it was last valued at: the first from 1.2, the second from 1.1, the domestic piece not at all",
                booked, first.carryingInCurrency() * (1.2 - 1.5) + second.carryingInCurrency() * (1.1 - 1.5), 1e-9);
        check("...and every piece is valued at the new rate afterwards, the manager too",
                first.getExchangeRate() == 1.5 && second.getExchangeRate() == 1.5 && local.getExchangeRate() == 1.5 && ledger.getExchangeRate() == 1.5);
        near("...a second press at the same rate books nothing", ledger.revalueForeign(1.5), 0, 0);

        accountsAlone();

        Debt mixed = ledger.addLongTermBond(10_000, 120, 0, .05, true);   // told 1.5 by the ledger
        check("a piece with no sale does not know the rate it was sold at: 0, and nothing since", mixed.soldAtRate() == 0 && mixed.exchangeSinceSold() == 0);
        mixed.setExchangeRate(1.0);
        mixed.noteSale(0, 6_000, 6_000, .05, "one");
        mixed.setExchangeRate(1.5);
        mixed.noteSale(1, 4_000, 4_000, .05, "two");
        mixed.noteSale(2, 1_000, 0, .05, "a gift: it raised nothing");
        check("each sale keeps the rate the piece was valued at when it was made",
                mixed.getSales().get(0).getRate() == 1.0 && mixed.getSales().get(1).getRate() == 1.5 && mixed.getSales().get(2).getRate() == 1.5);
        near("a piece sold at two rates remembers their mean weighted by what each raised (a sale that raised nothing is not counted)",
                mixed.soldAtRate(), (1.0 * 6_000 + 1.5 * 4_000) / 10_000, 1e-12);
        ledger.revalueForeign(1.8);
        near("...and what the exchange has done to what is still carried since is its US$ times the mean less the rate now",
                mixed.exchangeSinceSold(), mixed.carryingInCurrency() * (mixed.soldAtRate() - 1.8), 1e-9);
        near("domestic paper has no exchange since the sale, whatever it was sold at", local.exchangeSinceSold(), 0, 0);
        double sold = mixed.soldAtRate(), saleRate = mixed.getSales().get(1).getRate(), now = mixed.getExchangeRate();
        mixed.redenominate(100);
        check("a change of unit scales the rates the sales were made at as it scales the rate the piece is valued at",
                mixed.getExchangeRate() == now * 100 && mixed.getSales().get(1).getRate() == saleRate * 100 && Math.abs(mixed.soldAtRate() - sold * 100) < 1e-9);

        /* ============ 11. the student grant on a surplus share reads the cash ============ */
        out.println("\n--- the student grant on a surplus share is a share of the cash surplus: the exchange gain on the dollar debt is not paid out ---");

        Game schooled = schooledCity("debt-exchange-grant");
        TaxPolicy dials = schooled.getEconomyManager().getTaxPolicy();
        NationalAccounts gna = accounts(schooled);
        EconomyManager pay = schooled.getEconomyManager();
        schooled.getDebtManager().setTrade(10_000_000, 6);   // the window abroad shuts on a city that sells nothing abroad (section 6)
        String saidG = quote(schooled, "Term", GRANT_BOND, 5);
        check("fixture: dollar paper is outstanding, as it is in section 1, and the window abroad was open to sell it",
                saidG.startsWith("Issued abroad") && foreign(schooled).size() == 1);
        check("fixture: the city's grant is a share of the surplus", dials.getGrantBasis() == TaxPolicy.GrantBasis.SURPLUS_SHARE && dials.getGrantAmount() == SHARE);

        Month still = untilSharing(schooled);
        double stillCash = cashByRule(schooled);
        out.printf("   a quiet month: %,.0f students, the balance $%,.2fk, the cash surplus $%,.2fk, a grant bill of $%,.2fk%n",
                schooled.getFamilies().getSeekers(FamilyModel.Seeker.STUDENT), gna.getBalance(), stillCash, schooled.studentGrantBill());
        check("fixture: somebody is studying and the city ran a cash surplus, so the grant pays something to tell from nothing",
                schooled.getFamilies().getSeekers(FamilyModel.Seeker.STUDENT) >= 1 && stillCash > 0 && schooled.studentGrantBill() > 0);
        check("a month in which the D$ does not move has no exchange line in its balance, so the balance is all cash but the discount's amortising",
                still.line == 0);
        near("...and the grant bill struck on it is the share of the cash surplus, to the cent", schooled.studentGrantBill(), SHARE * stillCash, 1e-9);

        Month up = move(schooled, KEEPS);
        double upBalance = gna.getBalance(), upCash = cashByRule(schooled);
        double upNoMove = gna.getTotalRevenue() - gna.getTotalExpenses() + gna.getInterestNotCash() + gna.getUpliftNotCash();
        out.printf("   the D$ rose 10%%: the balance is $%,.2fk, of which D$%,.2fk is the exchange gain; the cash surplus is $%,.2fk%n", upBalance, up.line, upCash);
        near("the month after a quiet one pays the share of that month's cash surplus", pay.getStudentGrants(), SHARE * stillCash, 1e-9);
        check("premise: the D$ rose 10%, the month booked a gain on the dollar paper, and the city still ran a cash surplus",
                Math.abs(up.rateAfter / up.rateBefore - KEEPS) < 1e-12 && up.line > 0 && upCash > 0);
        check("premise: the gain is large enough to tell a grant on the balance from a grant on the cash",
                SHARE * upBalance - SHARE * upCash > 1000 * MoneyAudit.tolerance(TOLERANCE, upBalance));
        near("the cash surplus is the balance less the gain, which the treasury never received", upCash, upBalance - up.line + gna.getInterestNotCash() + gna.getUpliftNotCash() - gna.getRetiredGain(), 1e-9);
        near("the game's cash surplus is the same figure, from the bridge's own not-cash row", schooled.getTreasuryCashSurplus(), upCash);
        near("...and the grant bill struck on the month is the share of that, not of the balance the gain is in", schooled.studentGrantBill(), SHARE * upCash, 1e-9);
        check("...which is not the share of the balance", Math.abs(schooled.studentGrantBill() - SHARE * upBalance) > 1000 * MoneyAudit.tolerance(TOLERANCE, upBalance));
        near("the grant is what it is in a month the D$ did not move: the share of revenue less expenses and the discount's amortising, the line nowhere in it",
                schooled.studentGrantBill(), SHARE * upNoMove, 1e-9);

        Month down = move(schooled, 1 / KEEPS);
        double downBalance = gna.getBalance(), downCash = cashByRule(schooled);
        out.printf("   the D$ fell 10%%: the balance is $%,.2fk, of which D$%,.2fk is the exchange loss; the cash surplus is $%,.2fk%n", downBalance, down.line, downCash);
        near("the month after the gain pays the share of that month's cash surplus: the gain is not paid out", pay.getStudentGrants(), SHARE * upCash, 1e-9);
        check("...which is less than a grant on the balance with the gain in it would have been", pay.getStudentGrants() < SHARE * upBalance - 1000 * MoneyAudit.tolerance(TOLERANCE, upBalance));
        check("premise: the D$ fell 10%, the month booked a loss on the dollar paper, and the city still ran a cash surplus",
                Math.abs(down.rateBefore / down.rateAfter - KEEPS) < 1e-12 && down.line < 0 && downCash > 0);
        check("premise: the loss is large enough to take the balance below nothing, where a grant on the balance would pay none",
                downBalance < 0);
        near("the loss does not take the grant down: the bill struck on the month is the share of the cash surplus", schooled.studentGrantBill(), SHARE * downCash, 1e-9);
        near("...which is what it is in a month the D$ did not move", schooled.studentGrantBill(),
                SHARE * (gna.getTotalRevenue() - gna.getTotalExpenses() + gna.getInterestNotCash() + gna.getUpliftNotCash()), 1e-9);
        near("the Schools page's preview of what a student gets is the same pool split over the students", schooled.grantPerStudentUnder(TaxPolicy.GrantBasis.SURPLUS_SHARE, SHARE),
                schooled.studentGrantBill() / schooled.getFamilies().getSeekers(FamilyModel.Seeker.STUDENT), 1e-9);
        move(schooled, 1.0);
        near("the month after the loss pays the share of that month's cash surplus too", pay.getStudentGrants(), SHARE * downCash, 1e-9);

        /* ============ 12. ...and a buyback below book ============ */
        out.println("\n--- and the same for the gain on paper bought back below its book: the budget books it and the price paid was the cash ---");

        Game bought = schooledCity("debt-exchange-grant-buyback");
        NationalAccounts bna = accounts(bought);
        quietly(() -> bought.handleLongBondLogic(BUYBACK_BOND, 5, 1000));
        Month buyStill = untilSharing(bought);
        Debt bond = null;
        for (Debt d : bought.getDebtManager().getDebt()) if (d instanceof LongTermBond) bond = d;
        check("fixture: the term bond is on the books, a few months old, with somebody studying and the city sharing a cash surplus",
                bond != null && buyStill.line == 0 && bought.getFamilies().getSeekers(FamilyModel.Seeker.STUDENT) >= 1 && cashByRule(bought) > 0);
        double buyStillCash = cashByRule(bought);
        double price = bought.quoteRepurchase(bond), buyGain = bought.repurchaseGain(bond);
        near("the gain the screen quotes is the carrying value less the price paid", buyGain, bond.getCarryingValue() - price, 1e-9);
        check("premise: the bond is bought back below its book, a gain large enough to tell a grant on the balance from a grant on the cash",
                SHARE * buyGain > 1000 * MoneyAudit.tolerance(TOLERANCE, buyStillCash));
        bought.repurchaseDebt(bond);
        move(bought, 1.0);
        double buyBalance = bna.getBalance(), buyCash = cashByRule(bought);
        out.printf("   a bond bought back below its book: price $%,.2fk, gain $%,.2fk; the balance is $%,.2fk, the cash surplus $%,.2fk%n", price, buyGain, buyBalance, buyCash);
        near("the month books the gain, below the operating lines", bna.getRetiredGain(), buyGain, 1e-9);
        check("premise: the city still ran a cash surplus in the month, and the balance holds the gain", buyCash > 0 && buyBalance > buyCash);
        near("the cash surplus is the balance less the gain, which the treasury never received (the price was the cash)", buyCash,
                buyBalance - buyGain + bna.getInterestNotCash() + bna.getUpliftNotCash() - bna.getDebtExchangeGain(), 1e-9);
        near("the game's cash surplus is the same figure, from the bridge's own not-cash row", bought.getTreasuryCashSurplus(), buyCash);
        near("the grant bill struck on the month is the share of that, not of the balance the gain is in", bought.studentGrantBill(), SHARE * buyCash, 1e-9);
        check("...which is not the share of the balance", Math.abs(bought.studentGrantBill() - SHARE * buyBalance) > 1000 * MoneyAudit.tolerance(TOLERANCE, buyBalance));
        near("the grant is what it is in a month nothing was bought back: the share of revenue less expenses and the discount's amortising, the gain nowhere in it",
                bought.studentGrantBill(), SHARE * (bna.getTotalRevenue() - bna.getTotalExpenses() + bna.getInterestNotCash() + bna.getUpliftNotCash()), 1e-9);
        move(bought, 1.0);
        near("the month after pays the share of the cash surplus, as paid", bought.getEconomyManager().getStudentGrants(), SHARE * buyCash, 1e-9);

        out.printf("%n(%d assertions)%n", asserted);
        out.println(fails == 0
                ? "\nThe exchange on the foreign debt is booked at the reprice, taken out of the cash and left out of the cash rules and the surplus-share grant, and it survives a save."
                : "\n" + fails + " FAILED");
        if (fails > 0) System.exit(1);
    }

    /** The government account's setter alone: a figure that is no number reads nothing, a number is kept. */
    static void accountsAlone() {
        NationalAccounts alone = new NationalAccounts();
        alone.setDebtExchangeGain(Double.NaN);
        boolean nan = alone.getDebtExchangeGain() == 0;
        alone.setDebtExchangeGain(Double.POSITIVE_INFINITY);
        boolean inf = alone.getDebtExchangeGain() == 0;
        alone.setDebtExchangeGain(-12.5);
        check("the account's line reads nothing for a figure that is no number, and what it is told otherwise",
                nan && inf && alone.getDebtExchangeGain() == -12.5 && alone.getBalance() == alone.getTotalRevenue() - alone.getTotalExpenses() - 12.5);
    }

    /** Sells dollar paper through the game's own door: returns what it said. */
    static String quote(Game g, String type, double usd, int years) {
        String[] said = new String[1];
        quietly(() -> said[0] = g.handleForeignLogic(type, usd, years, 100, false));
        return said[0];
    }
}
