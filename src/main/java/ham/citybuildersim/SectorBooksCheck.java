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
 *      lines is operating income, less property tax, interest, the borrowing
 *      costs (0.7.102) and sales tax is pre-tax, less profit tax is what the
 *      sector kept. Every line on the
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
    static int screenLines = 0;

    /* =====================================================================
       4. AND EVERY FIGURE ON THE PAGE IS A FIGURE (2026-09-16)

       Sector.operations() draws a block per good the SECTOR declares, not per
       good the city has a plant for - so a city with a Grain Farm and no
       Livestock Farm drew MEAT, DAIRY_EGGS, VEGETABLES and FRUIT, each saying
       "Cost to make one: $9,223,372,036,854,775,807". That is
       getCostPerUnit()'s sentinel for "there is no line to cost", rendered.

       Nothing caught it: the books balanced, every harness was green, and the
       only place it existed was a screen. It was found by opening the game and
       clicking on Agriculture. So this walks EVERY sector's page EVERY month
       and refuses a value that is not a figure - a sentinel, an infinity, a
       NaN. A page is part of the product; a page that prints 9.2 quintillion
       dollars is a bug whether or not the ledger under it adds up.
       ===================================================================== */
    static boolean isAFigure(String value) {
        if (value.contains("Infinity") || value.contains("NaN") || value.contains("\u221e")) return false;
        int run = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isDigit(c)) { if (++run > 15) return false; }
            else if (c != ',') run = 0;
        }
        return true;
    }

    static void pageIsReadable(Game game) {
        for (Sector s : game.getSectors().all()) {
            for (Sector.Line line : s.operations(game)) {
                if (line.kind() != Sector.Line.Kind.LINE) continue;
                screenLines++;
                if (isAFigure(line.value())) continue;
                if (fails < 12) {
                    System.out.printf("  FAIL  %-14s month %3d: \"%s\" reads %s%n",
                            s.key(), game.getMonth(), line.label(), line.value());
                }
                fails++;
            }
        }
    }

    /** Everything here is in thousands, so a tenth of a cent is plenty. */
    static final double TOLERANCE = 1e-6;

    /** One labelled assertion, printed either way (0.7.46): the rest of this harness speaks only when it fails. */
    static void check(String label, boolean ok, String detail) {
        if (!ok) fails++;
        System.out.printf("%-86s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static void near(String what, String sector, int month,
                     double actual, double expected) {
        if (Math.abs(actual - expected) <= TOLERANCE) return;
        if (fails < 12) {
            System.out.printf("  FAIL  %-14s %-16s month %3d: %,.6f, expected %,.6f%n",
                    sector, what, month, actual, expected);
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

            for (String sector : Sectors.KEYS) {

                SectorBooks.SectorMonth m = books.get(sector);
                if (m.isEmpty()) continue;
                statements++;

                /* ------------------------ the sheet ------------------------ */
                // ...what it owes is its loans and bonds and, since 0.7.44, its suppliers (SupplierCredit).
                near("balance sheet", sector, m.month(),
                        m.totalAssets(), m.bondsPayable() + m.tradePayables() + m.equity());

                /* --------------------- the income statement --------------------- */
                near("operating", sector, m.month(),
                        m.operatingIncome(),
                        m.revenue() - m.operatingCost());
                // ...less, since 0.7.102 (Jerus's A16), what its borrowing cost it up front, a sixtieth a month:
                // a deductible expense beside the interest. Before: operating - property tax - interest - sales tax.
                near("pre-tax", sector, m.month(),
                        m.preTaxIncome(),
                        m.operatingIncome() - m.propertyTax() - m.interest() - m.borrowingCosts()
                                - m.salesTaxPaid());
                near("net", sector, m.month(),
                        m.netIncome(), m.preTaxIncome() - m.tax());

                /* ------------------------ the cash flow ------------------------ */
                near("cash flow", sector, m.month(), m.unexplained(), 0);

                /*
                 * ------------ AND THE BREAKDOWN ADDS UP TO THE LINE ------------
                 *
                 * The income statement's Revenue and cost-of-sales lines open
                 * into what they are made of, by good, split home and abroad
                 * (2026-09-16, Jerus: "when you click on revenue or cogs, it
                 * expands and shows the individual items"). A breakdown that
                 * does not add up to the figure above it is worse than no
                 * breakdown at all - it reads as an accounting error in the
                 * game rather than a bug in the screen - so it is checked
                 * here, on every sector, every month, along with everything
                 * else that has to sum.
                 *
                 * Revenue also carries whatever did not clear on a goods
                 * market: the builders' recognised work, the landlords' rent.
                 * The per-good part is the rest of it, which is why
                 * otherRevenue is added back rather than ignored.
                 */
                Sector s = game.getSectors().byKey(sector);
                if (s != null) {
                    Sector.Statement st = s.statement();
                    double sold = 0, bought = 0;
                    for (Sector.Split x : st.sold.values())   sold += x.total();
                    for (Sector.Split x : st.bought.values()) bought += x.total();
                    near("revenue by good", sector, m.month(), sold + st.otherRevenue, st.revenue);
                    near("cost of sales by good", sector, m.month(), bought, st.inputs);

                    /*
                     * AND THE HOME/ABROAD SPLIT IS THE SAME SPLIT THE
                     * STATEMENT ALREADY KEEPS, which is what makes the two
                     * indented lines under each good trustworthy: exports on
                     * one side, imports on the other, reached by a different
                     * route (per trade, per good) from the totals the VAT is
                     * struck off.
                     */
                    double abroadSold = 0, abroadBought = 0;
                    for (Sector.Split x : st.sold.values())   abroadSold += x.abroad;
                    for (Sector.Split x : st.bought.values()) abroadBought += x.abroad;
                    near("exports by good", sector, m.month(), abroadSold, st.exports);
                    near("imports by good", sector, m.month(), abroadBought, st.imports);

                    /*
                     * AND THE PART OF REVENUE THAT IS NOT A GOOD SPLITS ALL
                     * THE WAY. Only the builders have any, and they book TWO
                     * things into it - work recognised off the order book, and
                     * the repair bill sent to every owner of a standing
                     * building. Those are two lines on the opened Revenue and
                     * they have to be the whole of it: a sector that names
                     * half its other revenue draws a breakdown that silently
                     * does not add up to its own total, which is the one thing
                     * a statement may never do.
                     */
                    double named = 0;
                    for (double part : s.otherRevenueParts().values()) named += part;
                    near("other revenue named", sector, m.month(), named, st.otherRevenue);
                }

                /*
                 * AND THE TAX IS NEVER A REFUND. Every handler clamps it at
                 * zero on a loss-making month, and the screen prints it as a
                 * deduction - so a negative here would draw a credit the city
                 * never gave.
                 */
                if (m.tax() < -TOLERANCE) {
                    System.out.printf("  FAIL  %-14s negative tax in month %d: %,.6f%n",
                            sector, m.month(), m.tax());
                    fails++;
                }
            }

            pageIsReadable(game);
        }

        /* ===================================================================
           AND IT HAS TO SURVIVE A SAVE.

           The comparative column is the whole reason these are kept, and a
           comparative that is blank after a reload is the trap the schools fell
           into. So: save, reload, and check the reloaded city reports the same
           month the saved one did.
           =================================================================== */
        System.out.println("\nSaving and reloading...");

        SectorBooks.SectorMonth beforeSave = books.get(Sectors.RETAIL);
        game.saveGame(1);

        Game reloaded = new Game(game.getGameFiles());
        reloaded.loadGame(1);
        SectorBooks.SectorMonth afterLoad =
                reloaded.getSectorBooks().get(Sectors.RETAIL);

        near("survives a save", Sectors.RETAIL, beforeSave.month(),
                afterLoad.netIncome(), beforeSave.netIncome());
        near("...and its cash", Sectors.RETAIL, beforeSave.month(),
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
        SectorBooks.SectorMonth next = reloaded.getSectorBooks().get(Sectors.RETAIL);
        near("cash flow across a reload", Sectors.RETAIL, next.month(),
                next.unexplained(), 0);

        /* ===================================================================
           5. CONSTRUCTION'S MATERIALS ARE THE STATEMENT'S MONTH (A5, 0.7.46)

           The materials row is cleared at the strike and filled again as the
           crews draw, so between two presses it is the month in progress - not
           the month the statement beside it struck, which is what the page
           printed beside it. The builder keeps the row as it stood at the
           strike. The city above never outruns its public works yard, so this
           is a town of its own with an order book bigger than the yard holds:
           its crews buy the rest as they build. Nothing in it orders at the
           top of a month, so the row the last press left is the row at the
           strike (in a city whose planner does, part of the struck month is
           drawn in the press before the strike - the research city at 2,400
           months struck 539 units of which the row had shown none). Every
           month, what the builder says it struck must be that row, and the
           statement's month: materials imported exactly when the statement
           booked imports of them, bought here exactly when it booked those.
           Played until a struck month has materials in it, and saved there.
           =================================================================== */
        System.out.println();
        Game building = new Game(GameFiles.scratch("sector-books-materials"));
        building.newGame();
        building.setCashForTest(Founding.WEALTHY_CASH);
        building.getLandManager().setOwnedSqFt(building.getLandManager().getOwnedSqFt() + 50_000_000L);
        building.buildStack(LongPlaytest.template(building, "Construction Depot"), 4, true);
        building.buildStack(LongPlaytest.template(building, "House"), 300, false);
        ham.citybuildersim.sectors.Construction builders = building.getSectors().construction();
        int materialMonths = 0, drewMonths = 0, monthsApart = 0;
        double worstStruck = 0;
        for (int m = 0; m < 36 && !(drewMonths > 1 && builders.getDrawnLocal() + builders.getDrawnImported() > 0); m++) {
            Sector.Input row = builders.inputRow(Good.MATERIALS);
            double localWas = row == null ? 0 : row.boughtLocal, importedWas = row == null ? 0 : row.imported;
            building.toggleNextMonth();
            materialMonths++;
            if (localWas + importedWas > 0) drewMonths++;
            worstStruck = Math.max(worstStruck, Math.abs(builders.getDrawnLocal() - localWas)
                    + Math.abs(builders.getDrawnImported() - importedWas));
            Sector.Split booked = builders.statement().bought.get(Good.MATERIALS);
            double bookedAbroad = booked == null ? 0 : booked.abroad, bookedHome = booked == null ? 0 : booked.atHome;
            if ((builders.getDrawnImported() > 0) != (bookedAbroad > 0) || (builders.getDrawnLocal() > 0) != (bookedHome > 0)) {
                monthsApart++;
            }
        }
        check("fixture: the crews bought materials in more than one month, and in the month last struck",
                drewMonths > 1 && builders.getDrawnLocal() + builders.getDrawnImported() > 0,
                drewMonths + " of " + materialMonths + " months");
        building.saveGame(1);
        Game rebuiltCity = new Game(building.getGameFiles());
        rebuiltCity.loadGame(1);
        ham.citybuildersim.sectors.Construction rebuilt = rebuiltCity.getSectors().construction();
        boolean struckKept = rebuilt.isDrawnKnown()
                && Double.compare(rebuilt.getDrawnLocal(), builders.getDrawnLocal()) == 0
                && Double.compare(rebuilt.getDrawnImported(), builders.getDrawnImported()) == 0;
        check("Construction's struck materials are the row as it stood at the strike, and cross a save",
                worstStruck == 0 && monthsApart == 0 && struckKept,
                String.format("off by at most %s over %d months, %d apart from the statement; %,.1f + %,.1f units struck,"
                        + " %,.1f + %,.1f reloaded", worstStruck, materialMonths, monthsApart, builders.getDrawnLocal(),
                        builders.getDrawnImported(), rebuilt.getDrawnLocal(), rebuilt.getDrawnImported()));
        // ...and a save from before they were kept says it does not know them.
        SectorState older = rebuilt.toState();
        older.extras.remove("drawnLocal");
        older.extras.remove("drawnImported");
        ham.citybuildersim.sectors.Construction fromBefore = new ham.citybuildersim.sectors.Construction();
        fromBefore.restore(older);
        check("...and a save from before them loads them as not counted, not as nothing",
                !fromBefore.isDrawnKnown() && Double.isNaN(fromBefore.getDrawnLocal())
                        && !fromBefore.toState().extras.containsKey("drawnLocal"),
                "known " + fromBefore.isDrawnKnown());

        System.out.printf("%n%,d statements and %,d screen lines over %,d months: %s%n",
                statements, screenLines, months,
                fails == 0 ? "every one of them balanced and read as a figure."
                        : fails + " FAILED");

        if (fails > 0) System.exit(1);
    }
}
