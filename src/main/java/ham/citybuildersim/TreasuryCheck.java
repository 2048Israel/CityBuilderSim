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
 * The four things it will not let past:
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
        for (PolicySector sector : PolicySector.values()) {
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
           THE REPORT.

           Not an assertion. The residual row is real money the budget does not
           describe, and how often it is big is a fact about the model that is
           worth printing every time this runs.
           =================================================================== */
        System.out.printf("%n%d of 120 months moved the balance by something other than "
                + "the budget and its borrowing.%n", monthsWithRest);
        System.out.printf("The largest was %,.2f thousand.%n", biggestRest);

        System.out.println(fails == 0
                ? "\nThe treasury bridge closes, foots and survives a save."
                : "\n" + fails + " FAILED");

        if (fails > 0) System.exit(1);
    }
}
