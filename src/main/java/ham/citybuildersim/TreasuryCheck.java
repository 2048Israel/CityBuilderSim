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
 * The seven things it will not let past:
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
 *   7. AND WHAT FALLS DUE ROLLS AS THE SETTING SAYS (0.7.13, section 7).
 *      Rollover.netting() on the brief's own figures; then played cities,
 *      each fixture causing its condition: a city in surplus whose surplus
 *      nets part of a note and the rest rolls into the same note, sized so
 *      its cash covers it and so of a face above that cash - the discount
 *      borrowed into the debt - with the ledger netting a surplus once, and
 *      its twins as 12-month notes and by hand; a city in deficit where
 *      everything rolls, a dollar note at home with the window abroad shut
 *      and a serial's instalment into a new serial of its term; a dollar
 *      note rolled abroad in dollars; every press audited, and the setting,
 *      the ledger and the record through a save.
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

        /* ============ 7. ROLLING WHAT FALLS DUE (0.7.13) ============ */
        rolling();

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

    /* ===================================================================
       7. ROLLING WHAT FALLS DUE (0.7.13).

       Jerus: "the game checks whats going to mature next month, and issues
       what the treasury is lacking" - net of last year's surplus, in the same
       structure or as 12-month notes, or by hand (Rollover, and Game's
       ROLLING WHAT FALLS DUE). First the arithmetic on the brief's own
       figures; then played cities, each fixture causing its condition:
         - a city that ran a surplus, two notes falling due in consecutive
           months: the surplus nets part of the first and the rest rolls into
           the same note, sized so its cash covers it - so its face is more
           than what it replaces, the note's discount borrowed into the debt
           (Jerus's choice, round 2); the second reads the ledger and nets
           nothing the first netted. Its twins, the setting changed: the same
           sum as a 12-month note, and by hand nothing;
         - a city that built roads this year, so its year is a deficit,
           holding a note, a dollar note and a two-year serial: everything
           rolls, its cash covering what fell due and its face more than that
           cash every month - the dollar note, the window abroad shut by its own rule
           (it owes dollars and sells nothing abroad), into local paper of
           its term, which the log says; the serial's instalment into a new
           serial of its original term;
         - the surplus city later, exporting and its year's surplus netted
           already, a dollar note falling due: it rolls abroad, in dollars, of
           a bigger dollar face than the one it replaces;
         - every press closes its audit, the bridge's rows carry what was
           raised and repaid, and the ledger survives a save.
       =================================================================== */

    /** A city founded as a player founds one: newGame(), so it rolls in the same structure. */
    static Game founded(String label) {
        Game g = new Game(GameFiles.scratch(label));
        g.newGame();
        return g;
    }

    /** One press, its printing kept - the log is where a rollover says what it did. */
    static String press(Game g) {
        java.io.PrintStream out = System.out;
        java.io.ByteArrayOutputStream said = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(said));
        try { g.toggleNextMonth(); } finally { System.setOut(out); }
        return said.toString();
    }

    /** Some quiet work: an issue's receipt, a save. */
    static void quietly(Runnable work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { work.run(); } finally { System.setOut(out); }
    }

    /** The piece of paper of this type, term and currency issued in this month, or null. */
    static Debt paper(Game g, String type, int months, int started, boolean foreign) {
        for (Debt d : g.getDebtManager().getDebt()) {
            if (d.getType().equals(type) && d.getDuration() == months && d.getMonthStarted() == started
                    && d.isForeign() == foreign) return d;
        }
        return null;
    }

    /** The press closed its audit, and nothing moved after it struck. */
    static boolean audited(Game g) {
        return Math.abs(g.getLastMoneyAudit().relative()) < 1e-9 && Math.abs(g.getPostAuditDrift()) < 1e-6;
    }

    static void rolling() {
        System.out.println("\n--- 7. the rollover's arithmetic, on the brief's figures ---");

        double nets = Rollover.netting(100_000, 0, 300_000, 1_200_000);
        check("D$1.2B falling due, D$300M in cash, a D$100M surplus: it nets D$100M",
                Math.abs(nets - 100_000) < 1e-9);
        Rollover.Plan brief = new Rollover.Plan(Rollover.Mode.SAME_STRUCTURE, 1_200_000, 0, 100_000, 0,
                300_000, nets, java.util.List.of());
        check("...and rolls D$1.1B", Math.abs(brief.toRoll() - 1_100_000) < 1e-9);
        check("the same surplus, netted already in the year, nets nothing again",
                Rollover.netting(100_000, 100_000, 300_000, 1_200_000) == 0);
        check("a deficit year nets nothing, so everything rolls",
                Rollover.netting(-50_000, 0, 300_000, 1_200_000) == 0);
        check("...a surplus nets no more than the treasury holds",
                Rollover.netting(100_000, 0, 30_000, 1_200_000) == 30_000);
        check("...nor more than falls due", Rollover.netting(100_000, 0, 300_000, 40_000) == 40_000);

        System.out.println("\n--- a city in surplus, two notes falling due in a row ---");

        Game same = founded("treasury-roll-same");
        Game bills = founded("treasury-roll-bills");
        Game byHand = founded("treasury-roll-hand");
        check("fixture: a new game rolls in the same structure",
                same.getRolloverMode() == Rollover.Mode.SAME_STRUCTURE);
        bills.setRolloverMode(Rollover.Mode.TWELVE_MONTH_BILL);
        byHand.setRolloverMode(Rollover.Mode.MANUAL);
        Game[] three = { same, bills, byHand };
        for (Game g : three) for (int i = 0; i < 12; i++) press(g);
        for (Game g : three) quietly(() -> g.handleTBillLogic(20_000, 3, Game.BUILD_NOTE_GRANULE));
        for (Game g : three) press(g);
        for (Game g : three) quietly(() -> g.handleTBillLogic(20_000, 3, Game.BUILD_NOTE_GRANULE));
        for (Game g : three) press(g);

        int m = same.getMonth();
        Debt first = paper(same, "NOTE", 3, m - 2, false), second = paper(same, "NOTE", 3, m - 1, false);
        check("fixture: the first note falls due next month, the second the month after",
                first != null && second != null && first.getRemainingMonths() == 1 && second.getRemainingMonths() == 2);
        Rollover.Plan p1 = same.rolloverPlan();
        System.out.printf("   month %d: %,.2fk falls due; the year's surplus %,.2fk; cash %,.2fk; nets %,.2fk%n",
                m, p1.due(), p1.surplus(), p1.cash(), p1.netted());
        near("what falls due next month is the first note's face", m, p1.due(), first.getFaceValue());
        check("fixture: the city ran a surplus over the year", p1.surplus() > 0);
        near("it nets the surplus not netted yet, capped by the cash and what falls due", m, p1.netted(),
                Rollover.netting(same.surplusOverLastYear(), same.getRollover().usedInYear(m), same.getCash(), p1.due()));
        check("fixture: which is part of what falls due, so the rest rolls", p1.netted() > 0 && p1.netted() < p1.due());
        check("...into one issue: a 3-month note at home, the paper falling due",
                p1.issues().size() == 1 && p1.issues().get(0).type().equals("Note")
                        && p1.issues().get(0).term() == 3 && !p1.issues().get(0).foreign());
        near("...its cash what falls due less what is netted", m, p1.issues().get(0).cash(), p1.due() - p1.netted());
        check("...and the face its quote gives for that cash more than it: a note sells at a discount",
                p1.issues().get(0).face() > p1.issues().get(0).cash());
        Rollover.Plan b1 = bills.rolloverPlan();
        near("12-month notes: the same sum rolls", m, b1.toRoll(), p1.toRoll());
        check("...as one 12-month note at home", b1.issues().size() == 1 && b1.issues().get(0).type().equals("Note")
                && b1.issues().get(0).term() == Rollover.BILL_MONTHS && !b1.issues().get(0).foreign());
        Rollover.Plan h1 = byHand.rolloverPlan();
        near("by hand: the same falls due", m, h1.due(), p1.due());
        check("...and it nets and issues nothing", h1.netted() == 0 && h1.issues().isEmpty());
        int handDebts = byHand.getDebtManager().getDebt().size();
        double owedBefore = same.getDebtManager().getDomesticPrincipal();

        for (Game g : three) press(g);
        m = same.getMonth();
        Rollover roll = same.getRollover();
        Debt rolledNote = paper(same, "NOTE", 3, m - 1, false);
        check("the press rolls it: a new 3-month note, issued the month before the maturity", rolledNote != null);
        check("...whose cash covers what was to roll", roll.getLastRaised() >= p1.toRoll() - 1e-6);
        check("...by less than a granule of face", roll.getLastRaised() - p1.toRoll() <= Game.BUILD_NOTE_GRANULE);
        check("...raised under its face",
                rolledNote != null && roll.getLastRaised() > 0 && roll.getLastRaised() < rolledNote.getFaceValue());
        near("...by exactly its own discount and costs", m, roll.getLastRaised(),
                rolledNote == null ? 0 : rolledNote.getFaceValue() - rolledNote.getIssueDiscount());
        near("the record keeps the face it booked", m, roll.getLastIssued(),
                rolledNote == null ? 0 : rolledNote.getFaceValue());
        check("...and the note that fell due was paid", !same.getDebtManager().getDebt().contains(first));
        double owedAfter = same.getDebtManager().getDomesticPrincipal();
        near("so rolling moved the debt by the new face less the one it paid", m, owedAfter,
                owedBefore - p1.due() + (rolledNote == null ? 0 : rolledNote.getFaceValue()));
        check("...more than the netting alone would leave: the note's discount is borrowed into the debt",
                owedAfter > owedBefore - p1.netted() + 1e-6);
        near("the ledger keeps what it netted, the month it ran", m, roll.usedInYear(m - 1), p1.netted());
        near("the bridge's raised row is what it raised", m, same.getTreasuryRaised(), roll.getLastRaised());
        near("...and its repaid row the note that fell due", m, same.getTreasuryRepaid(), p1.due());
        check("...and the month closes its audit", audited(same));
        Debt bill = paper(bills, "NOTE", Rollover.BILL_MONTHS, m - 1, false);
        check("12-month notes: a 12-month note, the month before the maturity, its cash covering what was to roll",
                bill != null && bills.getRollover().getLastRaised() >= b1.toRoll() - 1e-6);
        check("...and its month closes its audit", audited(bills));
        check("by hand: nothing issued, the note paid out of cash",
                byHand.getDebtManager().getDebt().size() == handDebts - 1 && byHand.getRollover().getLastMonth() < 0);
        check("...and its month closes its audit", audited(byHand));

        Rollover.Plan p2 = same.rolloverPlan();
        System.out.printf("   month %d: %,.2fk falls due; the year's surplus %,.2fk, %,.2fk of it netted; nets %,.2fk%n",
                m, p2.due(), p2.surplus(), p2.used(), p2.netted());
        near("the second note falls due the month after", m, p2.due(), second.getFaceValue());
        near("...and the ledger carries the first netting into its year", m, p2.used(), p1.netted());
        near("...so it nets only the surplus not netted already", m, p2.netted(),
                Rollover.netting(p2.surplus(), p1.netted(), p2.cash(), p2.due()));
        check("the surplus nets once: the two months net no more than a year's surplus between them",
                p1.netted() + p2.netted() <= Math.max(p1.surplus(), p2.surplus()) + 1e-6);
        press(same);
        check("...and that month closes its audit too", audited(same));

        java.util.function.Supplier<Game> reload = () -> {
            quietly(() -> same.saveGame(10));
            Game back = new Game(same.getGameFiles());
            quietly(() -> back.loadGameSave(10));
            return back;
        };
        Game back = reload.get();
        check("the setting survives a save", back.getRolloverMode() == same.getRolloverMode());
        check("...the ledger, month by month",
                java.util.Arrays.equals(back.getRollover().ledgerToSave(), same.getRollover().ledgerToSave())
                        && same.getRollover().ledgerToSave().length > 0);
        check("...and the record", java.util.Arrays.equals(back.getRollover().recordToSave(),
                same.getRollover().recordToSave()));
        near("...so a reloaded city reads the year's netting the live one does", same.getMonth(),
                back.rolloverPlan().used(), same.rolloverPlan().used());

        System.out.println("\n--- a city that built this year: a deficit, and everything rolls ---");

        Game roads = founded("treasury-roll-roads");
        press(roads);
        BuildingsTemplate road = template(roads, "Paved Road");
        roads.getLandManager().setOwnedSqFt(roads.getLandManager().getOwnedSqFt() + road.getLandSqFt() * 10);
        check("fixture: it builds five roads this year", roads.buildStack(road, 5, false) == Game.BuildResult.SUCCESS);
        check("fixture: the window abroad is open while it owes nothing abroad", roads.foreignWindowOpen());
        quietly(() -> roads.handleForeignLogic("Note", 2_000, 6, Game.BUILD_NOTE_GRANULE, false));
        check("fixture: ...and shut once it owes dollars and sells nothing abroad", !roads.foreignWindowOpen());
        quietly(() -> roads.handleTBillLogic(5_000, 3, Game.BUILD_NOTE_GRANULE));
        quietly(() -> roads.handleMediumBondLogic(10_000, 2, Game.BUILD_BOND_GRANULE));
        int issuedAt = roads.getMonth();
        Debt dollars = paper(roads, "NOTE", 6, issuedAt, true);
        Debt serial = paper(roads, "SERIAL", 24, issuedAt, false);

        int rolledMonths = 0;
        boolean allDeficit = true, allRolled = true, allAudited = true, dollarsAtHome = false, logSaid = false;
        boolean serialRolled = false, allCapitalised = true;
        for (int i = 0; i < 12; i++) {
            Rollover.Plan p = roads.rolloverPlan();
            int now = roads.getMonth();
            boolean dollarsDue = dollars != null && roads.getDebtManager().getDebt().contains(dollars)
                    && dollars.getRemainingMonths() == 1;
            boolean serialDue = serial != null && serial.getRemainingMonths() % 12 == 1;
            String log = press(roads);
            if (!(p.due() > 0)) continue;
            rolledMonths++;
            if (!(p.surplus() < 0) || p.netted() != 0) allDeficit = false;
            if (Math.abs(p.toRaise() - p.due()) > 1e-6 || roads.getRollover().getLastRaised() < p.due() - 1e-6) allRolled = false;
            if (!(roads.getRollover().getLastIssued() > roads.getRollover().getLastRaised())) allCapitalised = false;
            if (!audited(roads)) allAudited = false;
            if (dollarsDue) {
                dollarsAtHome = paper(roads, "NOTE", 6, now, false) != null
                        && roads.getDebtManager().getForeignPrincipalUsd() == 0;
                logSaid = log.contains("the window abroad is shut");
            }
            if (serialDue) serialRolled = paper(roads, "SERIAL", 24, now, false) != null;
        }
        System.out.printf("   %d month(s) rolled; the year's surplus %,.2fk at the end%n",
                rolledMonths, roads.surplusOverLastYear());
        check("fixture: something fell due in the deficit year", rolledMonths >= 3);
        check("a deficit year nets nothing", allDeficit);
        check("...so everything that fell due rolled, and its cash covered it", allRolled);
        check("...each month's new face more than the cash it raised: the interest capitalised", allCapitalised);
        check("the dollar note, the window shut, rolled into a local note of its term", dollarsAtHome);
        check("...and the log says why", logSaid);
        check("the serial's instalment rolled into a new serial of its original term", serialRolled);
        check("...and every month closed its audit", allAudited);

        System.out.println("\n--- the surplus city, exporting: a dollar note rolls abroad, in dollars ---");

        while (same.getMonth() < 20) press(same);
        check("fixture: it sells abroad now, and the window is open",
                same.getDebtManager().getMonthlyExports() > 0 && same.foreignWindowOpen());
        quietly(() -> same.handleForeignLogic("Note", 500, 6, Game.BUILD_NOTE_GRANULE, false));
        Debt abroad = paper(same, "NOTE", 6, same.getMonth(), true);
        check("fixture: it owes a dollar note", abroad != null);
        while (abroad != null && abroad.getRemainingMonths() > 1) press(same);
        Rollover.Plan pd = same.rolloverPlan();
        Rollover.Issue inDollars = null;
        for (Rollover.Issue issue : pd.issues()) if (issue.foreign()) inDollars = issue;
        System.out.printf("   month %d: %,.2fk falls due, %,.2fk of it abroad; nets %,.2fk; window %s%n",
                same.getMonth(), pd.due(), pd.dueAbroad(), pd.netted(),
                same.foreignWindowOpen() ? "open" : "shut: " + same.foreignWindowReason());
        check("fixture: the window is open as it falls due", same.foreignWindowOpen());
        check("fixture: the year's surplus is netted already, so it rolls whole", pd.netted() < 1e-6);
        check("the dollar note rolls abroad, as a 6-month dollar note",
                inDollars != null && inDollars.type().equals("Note") && inDollars.term() == 6);
        int at = same.getMonth();
        double usdBefore = same.getDebtManager().getForeignPrincipalUsd();
        press(same);
        Debt rolled = paper(same, "NOTE", 6, at, true);
        check("...issued abroad the month before the one falling due", rolled != null);
        check("...the one falling due paid, and the city owes the new one in dollars",
                !same.getDebtManager().getDebt().contains(abroad) && rolled != null
                        && Math.abs(same.getDebtManager().getForeignPrincipalUsd() - rolled.faceInCurrency()) < 1e-6
                        && usdBefore > 0);
        check("...its cash covering what fell due", same.getRollover().getLastRaised() >= pd.due() - 1e-6);
        check("...so a bigger dollar face than the one it replaced: its discount borrowed too",
                rolled != null && rolled.faceInCurrency() > abroad.faceInCurrency());
        check("...and the month closes its audit", audited(same));
    }
}
