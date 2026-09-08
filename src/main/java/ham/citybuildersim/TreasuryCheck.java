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
 * AND IT MEASURES THE GAP rather than asserting it away. The "everything else"
 * row is not expected to be zero in this model - see the report it prints, and
 * the note in claude/simulation-findings.md about the government's books dating
 * land, buildings and interest a month behind the money.
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

            /* ---------------------- ...and how big it is ---------------------- */
            double rest = game.getTreasuryUnexplained();
            if (Math.abs(rest) > .5) {
                monthsWithRest++;
                if (Math.abs(rest) > Math.abs(biggestRest)) biggestRest = rest;
            }

            previousClosing = game.getTreasuryClosing();
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
