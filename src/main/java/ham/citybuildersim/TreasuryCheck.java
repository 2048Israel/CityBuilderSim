package ham.citybuildersim;

/**
 * Plays a city and audits what the screens say the treasury did. Not part of
 * the game.
 *
 * WHY THIS EXISTS. Jerus: "show how much was the actual month change, like in
 * the next month button it shows 3k but sometimes cause of land buybacks or
 * sales it was actually more or less." The dome and the Government Overview now
 * both print a measured cash movement, and a measured figure that is measured
 * wrongly is worse than the estimate it replaced - it looks authoritative.
 *
 * The six things it will not let past:
 *
 *   1. THE WINDOW CLOSES. Each month's opening balance is the previous month's
 *      closing balance, with no gap. If it ever is not, a month of the player's
 *      own land and building decisions has fallen down the crack between two
 *      windows and the figure on the button is understated by exactly that.
 *
 *   2. THE CLOSING BALANCE IS THE CASH. Whatever the recorder wrote down is
 *      what getCash() says a moment later.
 *
 *   3. THE BRIDGE FOOTS. Surplus, plus paper issued, less principal repaid,
 *      plus the "everything else" row, equals the change. That is an identity
 *      by construction - which is the point: if it ever breaks, the screen is
 *      drawing four rows that do not add to the total printed under them.
 *
 *   4. IT SURVIVES A SAVE. The reconciliation is on the first screen a
 *      returning player opens, so it has to be there before a month is played.
 *
 *   5. AND ON A CITY NOBODY TOUCHED, THE RESIDUAL ROW IS EMPTY. This one is
 *      new, and it is the assertion this harness was built to earn the right
 *      to make. The "everything else" row is real money in a played city -
 *      reserves bought, bonds retired early, capital put into the bank - but
 *      none of those happen on their own, so a hands-off run has nothing to put
 *      in it and every dollar must be described by the budget or its borrowing.
 *
 *      It did not used to be. Three things were wrong, all found by asking why
 *      this row was not zero:
 *
 *        - the government's books were struck from a stash taken half a tick
 *          early, so 116 of 120 months reported the PREVIOUS month's land and
 *          buildings, largest $118.42k
 *        - the bank's profit tax reached the treasury's cash and appeared on no
 *          budget, which was the whole of what was left: 110 months adrift by
 *          exactly the bank's tax and by nothing else
 *        - a subsidy left the treasury on the spot and was on no budget either,
 *          which nothing caught because the dial is off by default. Hence the
 *          second city below, which turns it on.
 *
 *   6. AND THE ROW OPENS. Since 2026-09-18 the "everything else" row is a
 *      TreasuryJournal - capital into the bank, reserves, a buyback, the
 *      students' loans, and the two lines the budget balance omits - with a
 *      "Not accounted for" line under it. The third city below does each of
 *      those things between two presses and asserts the line it left, that
 *      what IS on the budget (land, a building) is not named twice, that the
 *      bridge still foots with the journal and the residual in it, that the
 *      residual is smaller than what the journal explained - and what it is:
 *      the first coupon, booked the month it is charged and paid the month
 *      after - and that the whole journal comes back from a save line for
 *      line. The "Raised by issuing paper" row is asserted here too, because
 *      until this batch it read $0 on every month the city borrowed.
 *
 * @author Jerus
 */
public class TreasuryCheck {

    static int fails = 0;

    /** Everything here is in thousands, so a tenth of a cent is plenty. */
    static final double TOLERANCE = 1e-6;

    static void near(String what, int month, double actual, double expected) {
        if (Math.abs(actual - expected) <= TOLERANCE) return;
        if (fails < 12) {
            System.out.printf("  FAIL  %-28s month %3d: %,.6f, expected %,.6f%n",
                    what, month, actual, expected);
        }
        fails++;
    }

    /** A fact that is either so or not, printed either way so the run reads as a list. */
    static void check(String what, boolean ok) {
        if (!ok) fails++;
        System.out.printf("  %-62s %s%n", what, ok ? "OK" : "FAIL");
    }

    /** The journal line with this label, or null when the month has none. */
    static TreasuryJournal.Entry line(java.util.List<TreasuryJournal.Entry> journal, String label) {
        for (TreasuryJournal.Entry e : journal) if (e.label().equals(label)) return e;
        return null;
    }

    /** The amount on the journal line with this label, or 0 when there is none. */
    static double amount(java.util.List<TreasuryJournal.Entry> journal, String label) {
        TreasuryJournal.Entry e = line(journal, label);
        return e == null ? 0 : e.amount();
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    public static void main(String[] args) {

        Game game = new Game(GameFiles.scratch("treasury-check"));
        game.newGame();

        System.out.println("Playing 120 months and auditing the treasury bridge.");

        double previousClosing = Double.NaN;
        double biggestRest = 0;
        int monthsWithRest = 0;

        for (int i = 0; i < 120; i++) {

            game.toggleNextMonth();
            int month = game.getMonth();

            /* ------------------- 1. no gap between windows ------------------- */
            if (!Double.isNaN(previousClosing)) {
                near("window opens where it closed", month,
                        game.getTreasuryOpening(), previousClosing);
            }

            /* ------------------- 2. the closing IS the cash ------------------- */
            near("closing balance is the cash", month,
                    game.getTreasuryClosing(), game.getCash());

            /* ---------------------- 3. the bridge foots ---------------------- */
            near("the bridge foots", month,
                    game.getTreasurySurplus()
                            + game.getTreasuryRaised()
                            - game.getTreasuryRepaid()
                            + game.getTreasuryUnexplained(),
                    game.getTreasuryChange());

            /* -------- 5. and on a hands-off city there is nothing in it -------- */
            double rest = game.getTreasuryUnexplained();
            near("nothing the budget cannot explain", month, rest, 0);
            if (Math.abs(rest) > .5) {
                monthsWithRest++;
                if (Math.abs(rest) > Math.abs(biggestRest)) biggestRest = rest;
            }

            previousClosing = game.getTreasuryClosing();
        }

        /* ===================================================================
           AND AGAIN WITH THE SUBSIDY DIAL ON.

           A subsidy is the one budget line that moves cash from inside a method
           nobody would think to look in: paySubsidyIfOwed() does `cash -= owed`
           as it decides, months before anything strikes a book. It was on no
           budget at all until this run was written, and the reason nobody
           noticed is directly above - the dial is off unless a player turns it
           on, so the hands-off city could not see it.
           =================================================================== */
        System.out.println("\nAnd again with every sector auto-subsidised.");

        Game funded = new Game(GameFiles.scratch("treasury-subsidy"));
        funded.newGame();
        for (String sector : Sectors.KEYS) {
            funded.setAutoSubsidised(sector, true);
        }

        double paidOut = 0;
        double previousFunded = Double.NaN;
        for (int i = 0; i < 120; i++) {
            funded.toggleNextMonth();
            int month = funded.getMonth();
            paidOut += funded.getTotalSubsidyPaid();

            if (!Double.isNaN(previousFunded)) {
                near("subsidised: window opens where it closed", month,
                        funded.getTreasuryOpening(), previousFunded);
            }
            near("subsidised: the bridge foots", month,
                    funded.getTreasurySurplus()
                            + funded.getTreasuryRaised()
                            - funded.getTreasuryRepaid()
                            + funded.getTreasuryUnexplained(),
                    funded.getTreasuryChange());
            near("subsidised: nothing the budget cannot explain", month,
                    funded.getTreasuryUnexplained(), 0);
            previousFunded = funded.getTreasuryClosing();
        }
        System.out.printf("   the dial cost the city $%,.0fk over 120 months, "
                + "and every dollar of it is on a budget line.%n", paidOut);
        if (paidOut <= 0) {
            System.out.println("  FAIL  fixture: nothing was ever subsidised, "
                    + "so this proves nothing");
            fails++;
        }

        /* ===================================================================
           AND IT HAS TO SURVIVE A SAVE.

           The bridge is on the Government Overview page, which is one of the
           first things a returning player opens. Blank until you have played a
           month is blank exactly when it is wanted.
           =================================================================== */
        System.out.println("\nSaving and reloading...");

        double savedChange = game.getTreasuryChange();
        double savedClosing = game.getTreasuryClosing();
        game.saveGame(1);

        Game reloaded = new Game(game.getGameFiles());
        reloaded.loadGame(1);

        if (!reloaded.hasTreasuryMonth()) {
            System.out.println("  FAIL  no treasury month after a reload");
            fails++;
        }
        near("survives a save", game.getMonth(),
                reloaded.getTreasuryChange(), savedChange);
        near("...and its closing balance", game.getMonth(),
                reloaded.getTreasuryClosing(), savedClosing);

        /* ---- and so does the budget the bridge is measured against ----

           The surplus row is NationalAccounts.getBalance(), which is struck
           inside the tick from four accumulators that are zeroed the instant it
           is struck. A reloaded city cannot rebuild it from them - they are
           empty - so the four figures are carried in the save and handed back to
           refreshGovernmentAccounts(). Without that the returning player's first
           screen showed a month with no land, no buildings and no interest in
           it, and a bridge whose named rows did not describe its own total. */
        near("...and the budget behind it", game.getMonth(),
                reloaded.getEconomyManager().getNationalAccounts().getBalance(),
                game.getEconomyManager().getNationalAccounts().getBalance());
        near("...including what the city spent on buildings", game.getMonth(),
                reloaded.getEconomyManager().getNationalAccounts().getCapitalSpending(),
                game.getEconomyManager().getNationalAccounts().getCapitalSpending());
        near("...and what it paid in interest", game.getMonth(),
                reloaded.getEconomyManager().getNationalAccounts().getInterestExpense(),
                game.getEconomyManager().getNationalAccounts().getInterestExpense());

        /* ------------- and the first month back still has no gap ------------- */
        reloaded.toggleNextMonth();
        near("window survives a reload", reloaded.getMonth(),
                reloaded.getTreasuryOpening(), savedClosing);

        /* ===================================================================
           AND THE ROW OPENS.

           Jerus: "it just says 'everything else' - that should be expandable,
           cause a lot of times that's where a bunch of important things
           happen." A third city does, between two presses, every one of the
           things that row is for, and the month is read line by line.

           TICKED ONCE FIRST, on purpose: a new city's first window opens at
           the cash as it stands at the top of its first tick, so anything
           bought before that tick is on the first month's budget and not in
           its window - a residual of exactly the purchase, once, on every new
           city. Not this section's question; noted in the batch's hand-back.
           =================================================================== */
        System.out.println("\n--- and the row that says \"everything else\" opens into lines ---");

        Game opened = new Game(GameFiles.scratch("treasury-journal"));
        opened.newGame();
        opened.toggleNextMonth();
        opened.getLandManager().setOwnedSqFt(opened.getLandManager().getOwnedSqFt() + 50_000_000L);

        // ...between the presses: the player's month.
        LandParcel plot = opened.getLandListing().get(0);
        // In local money at the day's rate: the plot is priced in dollars (0.7.6).
        double landCharged = plot.localPrice(opened.getForeignAccounts().getRate());
        check("fixture: the land office listed a plot the city can afford",
                landCharged > 0 && landCharged < opened.getCash() && opened.buyLandParcel(plot.getId()));

        BuildingsTemplate house = template(opened, "House");
        double buildingCharged = house.getCashCost() * 10;
        check("fixture: the city paid for ten houses",
                opened.buildStack(house, 10, true) == Game.BuildResult.SUCCESS);

        double capitalPut = opened.recapitaliseBank(5_000);
        double reservesBought = opened.buyForeignCurrency(2_000) + opened.buyForeignCurrency(1_000);
        double reservesSold = opened.sellForeignCurrency(500);
        check("fixture: capital went into the bank and reserves were bought twice and sold once",
                capitalPut == 5_000 && reservesBought == 3_000 && reservesSold == 500);

        // Two issues, so the raised row has something to carry and the
        // buyback has a bond to retire that is not the one paying the coupon.
        double raisedForReal = 0;
        double cashBeforeNote = opened.getCash();
        opened.handleTBillLogic(10_000, 6, 1000);
        raisedForReal += opened.getCash() - cashBeforeNote;
        double cashBeforeBond = opened.getCash();
        opened.handleMediumBondLogic(20_000, 10, 1000);
        raisedForReal += opened.getCash() - cashBeforeBond;
        check("fixture: the city issued a note and a serial bond", raisedForReal > 0);

        Debt note = null;
        for (Debt d : opened.getDebtManager().getDebt()) if (d instanceof ShortTermTBill) note = d;
        double boughtBack = note == null ? 0 : opened.repurchaseDebt(note);
        check("fixture: and bought the note straight back", boughtBack > 0);

        opened.toggleNextMonth();
        int m = opened.getMonth();
        java.util.List<TreasuryJournal.Entry> journal = opened.getTreasuryJournal();
        NationalAccounts books = opened.getEconomyManager().getNationalAccounts();
        for (TreasuryJournal.Entry e : journal) {
            System.out.printf("     %-40s %,14.4f%n", e.label(), e.amount());
        }
        System.out.printf("     %-40s %,14.4f%n", "Not accounted for", opened.getTreasuryResidual());

        /* ---- what is on the budget is carried by the bridge's first row, not named twice ---- */
        near("the land is on the budget's own line", m, books.getLandPurchases(), landCharged);
        check("...so the journal does not name it a second time",
                line(journal, "Bought land") == null);
        near("the houses are on the budget's own line", m, books.getCapitalSpending(), buildingCharged);
        check("...so the journal does not name them a second time",
                line(journal, "Paid for buildings") == null);

        /* ---- what is not on the budget is in the journal, by name, signed as the treasury saw it ---- */
        near("the journal carries the capital put into the bank", m,
                amount(journal, "Put capital into the bank"), -capitalPut);
        near("...the reserves bought, two purchases folded into one line", m,
                amount(journal, "Bought reserves"), -reservesBought);
        near("...the reserves sold, on a line of their own", m,
                amount(journal, "Sold reserves"), reservesSold);
        near("...and the bond bought back, for what it cost", m,
                amount(journal, "Bought back a bond"), -boughtBack);
        check("...and nothing the treasury did not do",
                line(journal, "Lent to students, net of repayments") == null);

        /* ---- the raised row carries the issues, which until this batch it did not ---- */
        near("the paper raised is on the bridge's own row, not in the journal", m,
                opened.getTreasuryRaised(), raisedForReal);

        /* ---- and the bridge foots, with the journal and the residual in it ---- */
        double explained = 0;
        for (TreasuryJournal.Entry e : journal) explained += e.amount();
        near("the bridge foots through the journal", m,
                opened.getTreasurySurplus() + opened.getTreasuryRaised() - opened.getTreasuryRepaid()
                        + explained + opened.getTreasuryResidual(),
                opened.getTreasuryChange());
        check("the journal explained more than it left over",
                Math.abs(opened.getTreasuryResidual()) < Math.abs(explained));
        /*
         * AND WHAT IS LEFT OVER IS NAMED HERE TOO. The books charge the first
         * coupon in the month the bond is issued - processAllDebts() runs after
         * the cash is struck - and the cash pays it the month after, through
         * getExpenses(). So the residual on an issue month is the interest
         * line, exactly, and nothing else: the timing difference the note
         * under the bridge talks about, measured rather than assumed.
         *
         * ...THE BANK'S SHARE OF IT (0.7.3). Since 0.7.1 the households and
         * the central bank are paid their share of a coupon in the month
         * (Game, THE HOLDERS ARE PAID), so only the bank's share is a month
         * late. This fixture's households took none of the paper until
         * 0.7.3 only because the founding bank quoted them 7,567% on their
         * deposits, which no issue could beat; with the quote held to what
         * the bank charges they take a sliver at the settle, and their
         * coupon is not part of the timing.
         */
        near("...and what it left over is the first coupon's timing, to the cent: the bank's share of it", m,
                opened.getTreasuryResidual(), books.getInterestExpense()
                        - opened.getCouponsToHouseholds() - opened.getCentralBank().getPaperCoupons());

        /* ---- and a month that has no journal has no lines, so the row stays shut ---- */
        Game plain = new Game(GameFiles.scratch("treasury-plain"));
        plain.newGame();
        plain.toggleNextMonth();
        plain.toggleNextMonth();
        check("a city that did nothing has an empty journal, and the row stays a row",
                plain.getTreasuryJournal().isEmpty() && !plain.getTreasuryJournalBook().hasLines());

        /* ---- and it comes back from a save, line for line ---- */
        // A movement between the strike and the save, so the month in
        // progress has something to lose too - and a note, so the raised
        // counter has.
        double lateReserves = opened.buyForeignCurrency(700);
        double cashBeforeLateNote = opened.getCash();
        opened.handleTBillLogic(5_000, 6, 1000);
        double lateRaised = opened.getCash() - cashBeforeLateNote;
        opened.saveGame(2);
        Game backAgain = new Game(opened.getGameFiles());
        backAgain.loadGame(2);
        java.util.List<TreasuryJournal.Entry> was = journal;
        java.util.List<TreasuryJournal.Entry> now = backAgain.getTreasuryJournal();
        boolean lineForLine = was.size() == now.size();
        for (int i = 0; lineForLine && i < was.size(); i++) {
            lineForLine = was.get(i).label().equals(now.get(i).label())
                    && Math.abs(was.get(i).amount() - now.get(i).amount()) <= TOLERANCE;
        }
        check("last month's journal survives a save, line for line, in order", lineForLine && !now.isEmpty());
        near("...and so does the residual under it", m,
                backAgain.getTreasuryResidual(), opened.getTreasuryResidual());
        near("...and the paper raised on the row above", m,
                backAgain.getTreasuryRaised(), opened.getTreasuryRaised());
        near("...and the month in progress, which the next strike will count", m,
                amount(backAgain.getTreasuryJournalBook().thisMonth(), "Bought reserves"), -lateReserves);
        backAgain.toggleNextMonth();
        near("...so the reserves bought before the save are on the next month's line", backAgain.getMonth(),
                amount(backAgain.getTreasuryJournal(), "Bought reserves"), -lateReserves);
        near("...and the note issued before the save is on its raised row", backAgain.getMonth(),
                backAgain.getTreasuryRaised(), lateRaised);

        // An older save carries no journal at all, and has to load as an empty one.
        backAgain.getTreasuryJournalBook().restore(null, null, null, null);
        check("a save from before the journal loads with an empty journal, not a broken one",
                backAgain.getTreasuryJournal().isEmpty()
                        && backAgain.getTreasuryJournalBook().thisMonth().isEmpty());

        /* ===================================================================
           THE REPORT.

           Not an assertion. The residual row is real money the budget does not
           describe, and how often it is big is a fact about the model that is
           worth printing every time this runs.
           =================================================================== */
        System.out.printf("%n%d of 120 months moved the balance by something other than "
                + "the budget and its borrowing.%n", monthsWithRest);
        System.out.printf("The largest was %,.2f thousand.%n", biggestRest);

        System.out.println(fails == 0
                ? "\nThe treasury bridge closes, foots, opens and survives a save."
                : "\n" + fails + " FAILED");

        if (fails > 0) System.exit(1);
    }
}
