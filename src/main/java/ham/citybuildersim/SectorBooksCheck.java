package ham.citybuildersim;

/**
 * Plays a city and audits every sector's statements, every month. Not part of
 * the game.
 *
 * WHAT THIS IS FOR. BooksCheck already proves the food industry's statement is
 * right against hand arithmetic on one fixture. This proves something different
 * and, for a screen, more important: that the SAME three statements hold for
 * all six sectors, on a city that is actually running, for a hundred months
 * together - because a reporting layer that is correct in isolation and wrong
 * on a live city is a reporting layer that lies to the player.
 *
 * The three things it will not let past:
 *
 *   1. ASSETS = LIABILITIES + EQUITY. It is a plug in this model, so this
 *      catches arithmetic rather than accounting - but a plug that does not
 *      plug means a figure moved between being read and being used.
 *
 *   2. THE INCOME STATEMENT ADDS UP, top to bottom: revenue less the operating
 *      lines is operating income, less property tax, interest and sales tax is
 *      pre-tax, less profit tax is what the sector kept. Every line on the
 *      screen is one of these, so a break here is a screen that does not foot.
 *
 *      The sales tax joined that chain on 2026-09-09. It used to be remitted
 *      out of cash and appear on no statement, so this harness had to carry it
 *      as a cash-flow movement to make the month close - and the screen printed
 *      a red block saying the profit above it was overstated by that much,
 *      because it was.
 *
 *   3. THE CASH FLOW CLOSES. Opening cash plus what it kept plus what it
 *      borrowed less what it repaid plus what the city paid in equals closing
 *      cash. This is the one that would actually catch a bug: if any other part
 *      of the game moves a sector's cash, the residual appears here first.
 *
 * @author Jerus
 */
public class SectorBooksCheck {

    static int fails = 0;
    static int months = 0;
    static int statements = 0;

    /** Everything here is in thousands, so a tenth of a cent is plenty. */
    static final double TOLERANCE = 1e-6;

    static void near(String what, PolicySector sector, int month,
                     double actual, double expected) {
        if (Math.abs(actual - expected) <= TOLERANCE) return;
        if (fails < 12) {
            System.out.printf("  FAIL  %-14s %-16s month %3d: %,.6f, expected %,.6f%n",
                    sector.creditName(), what, month, actual, expected);
        }
        fails++;
    }

    public static void main(String[] args) {

        Game game = new Game(GameFiles.scratch("sector-books-check"));
        game.newGame();

        SectorBooks books = game.getSectorBooks();

        System.out.println("Playing 120 months and auditing six sets of books a month.");

        for (int i = 0; i < 120; i++) {

            game.toggleNextMonth();
            months++;

            for (PolicySector sector : PolicySector.values()) {

                SectorBooks.SectorMonth m = books.get(sector);
                if (m.isEmpty()) continue;
                statements++;

                /* ------------------------ the sheet ------------------------ */
                near("balance sheet", sector, m.month(),
                        m.totalAssets(), m.bondsPayable() + m.equity());

                /* --------------------- the income statement --------------------- */
                near("operating", sector, m.month(),
                        m.operatingIncome(),
                        m.revenue() - m.operatingCost());
                near("pre-tax", sector, m.month(),
                        m.preTaxIncome(),
                        m.operatingIncome() - m.propertyTax() - m.interest()
                                - m.salesTaxPaid());
                near("net", sector, m.month(),
                        m.netIncome(), m.preTaxIncome() - m.tax());

                /* ------------------------ the cash flow ------------------------ */
                near("cash flow", sector, m.month(), m.unexplained(), 0);

                /*
                 * AND THE TAX IS NEVER A REFUND. Every handler clamps it at
                 * zero on a loss-making month, and the screen prints it as a
                 * deduction - so a negative here would draw a credit the city
                 * never gave.
                 */
                if (m.tax() < -TOLERANCE) {
                    System.out.printf("  FAIL  %-14s negative tax in month %d: %,.6f%n",
                            sector.creditName(), m.month(), m.tax());
                    fails++;
                }
            }
        }

        /* ===================================================================
           AND IT HAS TO SURVIVE A SAVE.

           The comparative column is the whole reason these are kept, and a
           comparative that is blank after a reload is the trap the schools fell
           into. So: save, reload, and check the reloaded city reports the same
           month the saved one did.
           =================================================================== */
        System.out.println("\nSaving and reloading...");

        SectorBooks.SectorMonth beforeSave = books.get(PolicySector.RETAIL);
        game.saveGame(1);

        Game reloaded = new Game(game.getGameFiles());
        reloaded.loadGame(1);
        SectorBooks.SectorMonth afterLoad =
                reloaded.getSectorBooks().get(PolicySector.RETAIL);

        near("survives a save", PolicySector.RETAIL, beforeSave.month(),
                afterLoad.netIncome(), beforeSave.netIncome());
        near("...and its cash", PolicySector.RETAIL, beforeSave.month(),
                afterLoad.cash(), beforeSave.cash());
        if (afterLoad.month() != beforeSave.month()) {
            System.out.printf("  FAIL  reloaded month %d, saved month %d%n",
                    afterLoad.month(), beforeSave.month());
            fails++;
        }
        if (!reloaded.getSectorBooks().hasComparatives()) {
            System.out.println("  FAIL  no comparative column after a reload");
            fails++;
        }

        /* ---------------------- and the month after ---------------------- */
        reloaded.toggleNextMonth();
        SectorBooks.SectorMonth next = reloaded.getSectorBooks().get(PolicySector.RETAIL);
        near("cash flow across a reload", PolicySector.RETAIL, next.month(),
                next.unexplained(), 0);

        System.out.printf("%n%,d statements over %,d months: %s%n",
                statements, months,
                fails == 0 ? "every one of them balanced." : fails + " FAILED");

        if (fails > 0) System.exit(1);
    }
}
