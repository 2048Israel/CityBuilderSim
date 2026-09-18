package ham.citybuildersim;

import ham.citybuildersim.sectors.FoodProcessing;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The third of the shelf that arrives already made.
 *
 * WHAT THIS HAS TO PROVE. Not that the eleventh sector makes money - whether it
 * does depends on the city, which is the point of it, and the run that shows
 * one is in claude/. What is checkable is the four things that cost this batch
 * its four calibration passes, each of which was a real fault that a green
 * suite did not catch:
 *
 *   1. ALL THREE PLANTS CAN BE BUILT. The generic planner sizes a sector by one
 *      good and skips every template that does not make it, so two of the three
 *      were invisible: not refused, not scored badly, never considered. The
 *      test is that the sector's own planner reaches each of them.
 *
 *   2. A PLANT'S OUTPUT IS PRICED AT WHAT IT WILL FETCH ONCE IT IS RUNNING,
 *      not at the ceiling a good with no domestic maker stands at. The first
 *      Meat Works was financed against $8.90 processed meat and opened into
 *      $7.70.
 *
 *   3. THE SALES TAX IS IN THE ESTIMATE. It is a VAT with no credit behind an
 *      import, so a plant on imported meat pays it on the whole ticket - $5k a
 *      month against $1k of operating income, five times the profit, invisible
 *      to the generic estimate.
 *
 *   4. THE PLANTS EARN A WAGE. Revenue per worker is the one ratio that decides
 *      whether a maker survives a city growing up, because the wage is the cost
 *      that follows the city and the price is not. The first calibration had
 *      these three at $6.0k-$10.7k a head against the bakeries' $13.5k-$17.8k,
 *      and every one of them died.
 *
 * Every fixture CAUSES its condition: the meat price is set, not waited for.
 */
public class FoodProcessingCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-72s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-72s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static final String MEAT_WORKS = "Meat Works";
    static final String SNACKS     = "Snack & Oils Plant";
    static final String BOTTLING   = "Bottling Plant";

    static double jobsOf(BuildingsTemplate t) {
        double n = 0;
        for (JobType job : JobType.values()) n += t.getJobs(job);
        return n;
    }

    static double payrollOf(BuildingsTemplate t) {
        double n = 0;
        for (JobType job : JobType.values()) n += t.getJobs(job) * PayTier.of(job).getMonthlyWage();
        return n;
    }

    /** A template's nameplate valued at the middle of each good's world band, in thousands. */
    static double midBandRevenue(BuildingsTemplate t) {
        double worth = 0;
        for (Good g : Good.values()) {
            double made = t.makes(g);
            if (made <= 0 || !g.traded()) continue;
            worth += made * (g.worldImportPrice() + g.worldExportPrice()) / 2;
        }
        return worth;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ================= 1. the five goods it exists for ================= */
        out.println("--- the third of the shelf ---");

        Good[] made = { Good.PROCESSED_MEAT, Good.READY_MEALS, Good.SNACKS, Good.FATS, Good.DRINKS };
        for (Good g : made) {
            assertTrue(g.label() + " clears on a band with a real ceiling",
                    g.traded() && g.importable() && g.exportable()
                            && g.worldImportPrice() > g.worldExportPrice());
        }

        Path root = Files.createTempDirectory("fpcheck");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game game = new Game(files);
        quietly(game::run);

        FoodProcessing fp = game.getSectors().foodProcessing();
        BuildingManager bm = game.getBuildingManager();
        Markets markets = game.getEconomyManager().getMarkets();

        assertTrue("the sector is registered under its saved name",
                game.getSectors().byKey("Food Processing") == fp);
        for (Good g : made) {
            assertTrue("...and claims " + g.label() + ", so the market brings it to clearing",
                    fp.goodsMade().contains(g));
        }
        /*
         * IT DOES NOT USE WHAT IT MAKES. Crisps are fried in the oil this
         * sector presses, and putting FATS on the input list would hand the
         * other four outputs a share of that line's bill through the joint-cost
         * split. See the sector's header and Sector.costShareOf().
         */
        for (Good g : made) {
            assertTrue("...and does not buy back its own " + g.label(),
                    !fp.goodsUsed().contains(g));
        }

        /* ============ 2. three plants, and all three reachable ============ */
        out.println("\n--- three plants, five goods, and a planner that can see all of them ---");

        BuildingsTemplate meat   = bm.getTemplateByName(MEAT_WORKS);
        BuildingsTemplate snack  = bm.getTemplateByName(SNACKS);
        BuildingsTemplate bottle = bm.getTemplateByName(BOTTLING);
        assertTrue("the catalogue has all three", meat != null && snack != null && bottle != null);

        /*
         * THE FAULT THIS TEST IS FOR. BusinessInvestment.planMaker() skips
         * every template that does not make Sector.planningGood(), which here
         * is processed meat - so on the generic path the Snack & Oils Plant and
         * the Bottling Plant could never be ordered by anybody at all. Measured
         * over four hundred months: one Meat Works, zero of the other two.
         *
         * The sector's own planner iterates its whole catalogue, which is what
         * this asserts - by the plainest route available, that each template
         * belongs to the sector and the planner's own loop is over that list.
         */
        for (BuildingsTemplate t : new BuildingsTemplate[] { meat, snack, bottle }) {
            assertTrue(t.getName() + " is the sector's, so its own planner reaches it",
                    bm.getTemplatesBySector("Food Processing").contains(t));
        }
        assertTrue("the generic planner would only ever have seen one of them",
                fp.planningGood() != null && meat.makes(fp.planningGood()) > 0
                        && snack.makes(fp.planningGood()) <= 0
                        && bottle.makes(fp.planningGood()) <= 0);

        /* ============ 3. what a plant earns per person who runs it ============ */
        out.println("\n--- revenue per worker, against the bakeries next door ---");

        /*
         * THE RATIO THAT DECIDES WHETHER A MAKER SURVIVES THE CITY GROWING UP.
         * Wages follow the city; the world price does not. A plant whose payroll
         * is most of its revenue at founding wages is underwater at grown ones,
         * and the first calibration of these three was exactly that: $6.0k,
         * $6.3k and $10.7k a head, payroll 35-61% of revenue, all three dead
         * inside thirty months of opening.
         *
         * The bar is the game's own food industry, which survives: the Bakery
         * at $17.8k a head and 22% payroll, the Industrial Bakery at $13.5k and
         * 28%. Anything in that family is calibrated; anything under $12k a
         * head is the mistake this batch made once.
         */
        BuildingsTemplate bakery = bm.getTemplateByName("Bakery");
        double bakeryPerHead = midBandRevenue(bakery) / jobsOf(bakery);
        for (BuildingsTemplate t : new BuildingsTemplate[] { meat, snack, bottle }) {
            double perHead = midBandRevenue(t) / jobsOf(t);
            double payShare = payrollOf(t) / midBandRevenue(t);
            report(t.getName() + ": revenue a worker against the Bakery's",
                    perHead >= 12.0,
                    String.format("$%.1fk a head (Bakery $%.1fk), payroll %.0f%% of revenue",
                            perHead, bakeryPerHead, payShare * 100));
            report("...and its payroll is a maker's share of what it sells",
                    payShare <= .35,
                    String.format("%.0f%%", payShare * 100));
        }

        /*
         * AND THE INPUT BILL IS THE OTHER HALF OF THE STORY. The Meat Works is
         * meat; the other two are barely their crops. That is the design - a
         * city priced out of one rung can still build another - so it is
         * asserted rather than left to be noticed.
         */
        double meatShare = meat.uses(Good.MEAT)
                * (Good.MEAT.worldImportPrice() + Good.MEAT.worldExportPrice()) / 2
                / midBandRevenue(meat);
        report("the Meat Works is meat: the input bill is most of what it sells",
                meatShare > .30, String.format("%.0f%% of revenue", meatShare * 100));
        double bottleShare = bottle.uses(Good.CROPS)
                * (Good.CROPS.worldImportPrice() + Good.CROPS.worldExportPrice()) / 2
                / midBandRevenue(bottle);
        report("the Bottling Plant is wages and water: its crop bill is a rounding error",
                bottleShare < .10, String.format("%.0f%% of revenue", bottleShare * 100));
        assertTrue("...and its water bill is not - it is the heaviest per dollar in the game",
                bottle.getWaterConsumption() / midBandRevenue(bottle)
                        > bakery.getWaterConsumption() / midBandRevenue(bakery));

        /* ====== 4. the estimate: the price it leaves behind, and the tax ====== */
        out.println("\n--- what the planner thinks a plant would earn ---");

        /*
         * AND FOR THIS HALF THE FIXTURE HAS TO HAVE BEEN LIVED IN. The first
         * draft asked these questions of a city at month zero and all three
         * were meaningless there: no month had cleared, so every market read
         * zero demand and priced every plant's whole nameplate at the export
         * floor, and the wage schedule the running cost is read off was still
         * empty - a Meat Works came out earning $15.8k a month with no payroll
         * in it at all. A planning rule about prices and wages cannot be tested
         * in a city that has neither.
         *
         * AND IT RUNS UNTIL THE SECTOR IS ABOUT TO BUILD, not for a fixed
         * number of months, which is the second thing this fixture got wrong.
         * A fixed count is a bet on what the investor will have done by then,
         * and at a hundred and sixty months it had already put up three
         * Bottling Plants - so "a first plant is not valued at the ceiling it
         * will destroy" was being asked of a FOURTH plant, in a city whose
         * drinks were already covered, where the generic estimate and this
         * sector's agree exactly and correctly. The test passed nothing and
         * failed for the right reason.
         *
         * plan() is a pure reading, so asking it each month costs nothing and
         * stops the city at the largest it can be while this sector still owns
         * NOTHING - no plant standing, none in the pipeline, every one of its
         * five goods still arriving by ship. That is the state the two rules
         * below are about, and it is reached by construction rather than by
         * counting on the calendar.
         */
        /*
         * AND THE SECTOR IS HELD WHILE THE CITY GROWS (2026-09-17), which is
         * what the paragraph above was reaching for and did not quite get.
         *
         * The loop below grows the city until this sector SAYS it wants a
         * plant, and then asserts that it still owns nothing. Nothing stopped
         * the investment loop building one first: plan() is only asked at the
         * top of each iteration, so a month in which the sector both became
         * willing and was funded left the fixture asking its question of a
         * sector that already had a pipeline. It survived on timing, and the
         * day households started spending their dividends the timing moved and
         * it went red - on a change that has nothing to do with food.
         *
         * holdSector() takes the investment loop out of it. plan() is a pure
         * reading and still answers, so the loop below is unaffected; what
         * cannot happen any more is the sector acting on the answer. "Reached
         * by construction rather than by counting on the calendar" - now true.
         */
        game.getBusinessInvestment().holdSector(fp.key());

        quietly(() -> {
            LongPlaytest.build(game, "House", 40);
            LongPlaytest.build(game, "Convenience Store", 3);
            LongPlaytest.build(game, "Mixed Farm", 2);
            LongPlaytest.run(game, 3);
            LongPlaytest.build(game, "House", 20);
            LongPlaytest.run(game, 4);
            LongPlaytest.build(game, "Convenience Store", 2);
            LongPlaytest.build(game, "Construction Depot", 1);
            LongPlaytest.run(game, 5);
            for (int i = 0; i < 400; i++) {
                if (fp.plan(game.getBusinessInvestment(), game).build) break;
                LongPlaytest.advise(game);
                LongPlaytest.run(game, 1);
            }
        });
        BusinessInvestment.Decision first = fp.plan(game.getBusinessInvestment(), game);
        report("the fixture is a city that has been lived in",
                markets.get(Good.DRINKS).getDemandTrend() > 0
                        && game.getEconomyManager().getWageRates()[JobType.NO_DIPLOMA.ordinal()] > 0,
                String.format("month %d, %d people, drinks trend %,.0f kg/mo, unskilled wage %s",
                        game.getMonth(), game.getPopulationManager().getPopulation(),
                        markets.get(Good.DRINKS).getDemandTrend(),
                        Formats.INSTANCE.cash(
                                game.getEconomyManager().getWageRates()[JobType.NO_DIPLOMA.ordinal()])));
        boolean empty = true;
        for (Good g : made) empty &= fp.getCapacity(g) <= 0 && fp.getPipeline(g) <= 0;
        report("...and the sector still owns nothing in it, which is what these two rules are about",
                empty && first.build,
                first.build ? "it wants its first " + first.template.getName() : "it wants nothing: " + first.reason);

        /*
         * THE FIRST OF THE TWO THINGS THE GENERIC ESTIMATE GETS WRONG HERE. A
         * good nobody in the city makes stands at its import ceiling, correctly
         * - every kilo came off a ship. The first plant is then valued at the
         * ceiling it is about to destroy. This city has drunk imported bottles
         * for a hundred and sixty months and has no plant of its own, so the
         * generic estimate is at its most wrong, and the sector's own has to
         * come out lower.
         */
        BuildingsTemplate wanted = first.build ? first.template : bottle;
        double generic = game.getBusinessInvestment().estimatedMakerProfit(fp, wanted);
        double own     = fp.estimatedMonthlyProfit(wanted, game.getBusinessInvestment());
        report("a first " + wanted.getName() + " is not valued at the ceiling it will destroy",
                own < generic,
                String.format("sector %s a month against the generic %s",
                        Formats.INSTANCE.cash(own), Formats.INSTANCE.cash(generic)));

        /*
         * AND THE SECOND. The sales tax is a VAT: charged on what you sell,
         * credited for what your SUPPLIERS remitted. An import carries no such
         * credit - SalesTaxLedger.chargeImport() puts the same figure on both
         * sides of the row and it nets to nothing - so a plant on imported meat
         * pays the tax on its whole ticket. Measured on a real run: $5k a month
         * against $1k of operating income.
         *
         * Raising the rate has to move the estimate. If it does not, the tax is
         * not in there.
         */
        TaxPolicy tax = game.getEconomyManager().getTaxPolicy();
        double before = fp.estimatedMonthlyProfit(wanted, game.getBusinessInvestment());
        double wasRate = tax.getIncomeTaxRate();
        tax.setIncomeTaxRate(wasRate + .20);
        double after = fp.estimatedMonthlyProfit(wanted, game.getBusinessInvestment());
        tax.setIncomeTaxRate(wasRate);
        report("twenty points on the sales tax moves what a plant is thought to earn",
                after < before - 1e-9,
                String.format("%s a month becomes %s",
                        Formats.INSTANCE.cash(before), Formats.INSTANCE.cash(after)));
        report("...and putting the rate back puts the estimate back",
                Math.abs(fp.estimatedMonthlyProfit(wanted, game.getBusinessInvestment()) - before) < 1e-9,
                "");

        /* ============ 5. the meat price is the bet, and it is real ============ */
        out.println("\n--- the Meat Works is a bet on the meat price ---");

        /*
         * THE WHOLE SECTOR IN ONE MEASUREMENT. Raw meat lands at $7.00 and
         * processed meat ships at $5.60; a plant that buys at the world's
         * ceiling and sells at the world's floor clears about twenty-seven
         * cents a tonne before a wage. On a city's own herd the same building
         * is an ordinary business.
         *
         * The fixture CAUSES both - the meat price is set here rather than
         * waited for - and what has to be true is that the planner can tell
         * them apart.
         */
        GoodsMarket meatMarket = markets.get(Good.MEAT);
        meatMarket.setLocalPrice(Good.MEAT.worldImportPrice());
        double imported = fp.estimatedMonthlyProfit(meat, game.getBusinessInvestment());
        meatMarket.setLocalPrice(Good.MEAT.worldExportPrice());
        double homegrown = fp.estimatedMonthlyProfit(meat, game.getBusinessInvestment());
        report("meat at a farm's floor is worth more to a Meat Works than meat off a ship",
                homegrown > imported,
                String.format("%s a month against %s",
                        Formats.INSTANCE.cash(homegrown), Formats.INSTANCE.cash(imported)));
        /*
         * AND THE GAP IS MOST OF WHAT THE PLANT EARNS, which is the claim the
         * sector's header makes and the only form of it that survives a city
         * whose processed meat is ALSO at the ceiling because nobody here makes
         * any. In that city even dear meat leaves a little - the plant is
         * selling into a shortage - and an absolute "there is nothing left in
         * it" would be measuring the shortage, not the meat. What is true in
         * every city is that the same building is worth several times more on a
         * herd than on a ship.
         */
        report("...and the difference is most of what a Meat Works is worth",
                homegrown > imported * 2,
                String.format("%.1fx", imported > 0 ? homegrown / imported : Double.POSITIVE_INFINITY));

        /*
         * AND THE PLANNER ACTS ON IT. Not "refuses to build anything" - the
         * other two rungs do not touch meat and a city that wants crisps should
         * still get a snack plant while the herds are being raised, which is
         * the whole reason this sector has three buildings. What has to be true
         * is that nobody finances a MEAT WORKS when meat is priced out of it.
         */
        meatMarket.setLocalPrice(Good.MEAT.worldImportPrice() * 4);
        BusinessInvestment.Decision dear = fp.plan(game.getBusinessInvestment(), game);
        report("nobody finances a Meat Works on meat nothing could pay for",
                !(dear.build && dear.template == meat),
                dear.build ? "it would build a " + dear.template.getName() : "it says: " + dear.reason);

        /*
         * AND IT SAYS SO IN WORDS, because a refusal a player cannot read is a
         * sector that mysteriously does nothing. Every path out of plan() that
         * does not build has to name what stopped it.
         */
        BusinessInvestment.Decision no = fp.plan(game.getBusinessInvestment(), game);
        assertTrue("and every decision comes with a reason",
                no.reason != null && !no.reason.isBlank());
        out.printf("    it says: \"%s\"%n", no.reason);
        meatMarket.setLocalPrice(Good.MEAT.worldImportPrice());

        /* ============ 6. it survives a save, by name ============ */
        out.println("\n--- and it survives a save ---");

        /*
         * ELEVEN SECTORS INTO A TEN-SECTOR SAVE. Nothing in the save is keyed
         * by a sector's INDEX - SectorState by name, equityKeys by name,
         * household cells by name - which is why appending to Sectors.KEYS was
         * safe and SAVE_FORMAT did not move. Proven end to end elsewhere
         * against the previous build; what is provable here is the round trip.
         */
        fp.addCash(1234.5);
        quietly(() -> game.saveGame(1, "fpcheck"));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(1));
        assertTrue("the save loaded at all", back.getLoadFailure() == null);
        FoodProcessing again = back.getSectors().foodProcessing();
        report("the sector's till survives the round trip",
                Math.abs(again.getCash() - fp.getCash()) < 1e-9,
                String.format("%.4f vs %.4f", again.getCash(), fp.getCash()));
        assertTrue("...and it is still the eleventh name in the equity register",
                Equity.indexOf("Food Processing") >= 0
                        && Equity.COMPANIES[Equity.COMPANIES.length - 1].equals("Bank"));

        out.println();
        if (fails == 0) {
            out.println("Food Processing works: three plants the planner can all see, each "
                    + "earning a maker's wage, priced at what it will get rather than what "
                    + "it is about to destroy - and the meat price decides the meat works.");
        } else {
            out.println(fails + " check(s) failed.");
        }
        System.exit(fails);
    }
}
