package ham.citybuildersim;

import ham.citybuildersim.sectors.BusinessServices;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The sector whose customer is not in the city.
 *
 * WHAT THIS HAS TO PROVE, and why it is mechanism rather than size: the crime
 * batch established that the long-run population of a city moves ten percent on
 * perturbations of one part in a million, so an eight-seed median cannot resolve
 * a ten percent effect and "the city got bigger" is not evidence of anything.
 * What is checkable is whether the thing WORKS:
 *
 *   1. the three goods are export-only and clear at the world's floor
 *   2. the templates are the arithmetic the design doc claims
 *   3. the licence gate refuses, and stops refusing when the engineers exist
 *   4. the wage bill is the brake - a city that pays more stops expanding
 *   5. a strong currency closes the sector and a weak one reopens it
 *   6. it books like every other sector, and survives a reload
 *
 * Every fixture here CAUSES its condition. The licence test grants licences
 * rather than waiting for a school; the currency test moves the rate rather
 * than hoping a run drifts.
 */
public class BusinessServicesCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-62s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void check(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-62s FAIL  %.6f != %.6f%n", label, actual, expected);
        } else {
            out.printf("%-62s OK%n", label);
        }
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** The three rungs, and the band each is meant to employ. */
    static final String CONTACT = "Contact Centre";
    static final String SHARED  = "Shared Services Centre";
    static final String OFFICE  = "Engineering Services Office";

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ============ 1. the goods: export only, and the floor is the price ============ */
        out.println("--- three goods the city cannot buy ---");

        Good[] kinds = { Good.SUPPORT_WORK, Good.BACK_OFFICE_WORK, Good.ENGINEERING_WORK };
        for (Good g : kinds) {
            assertTrue(g.label() + " is not importable - nobody here buys it", !g.importable());
            assertTrue(g.label() + " is exportable", g.exportable());
            assertTrue(g.label() + " does not warehouse - a month is delivered or gone",
                    !g.stockable());
            assertTrue(g.label() + " clears on the band", g.traded());
            assertTrue(g.label() + " is not tax exempt - zero-rated with credits, like steel",
                    !g.taxExempt());
            assertTrue(g.label() + " is priced by the seat-month", "seat-month".equals(g.unit()));
        }
        assertTrue("the rungs pay more per seat the higher they are",
                Good.SUPPORT_WORK.worldExportPrice() < Good.BACK_OFFICE_WORK.worldExportPrice()
                        && Good.BACK_OFFICE_WORK.worldExportPrice() < Good.ENGINEERING_WORK.worldExportPrice());

        // The whole mechanism in four lines: no local demand, so the price is
        // the floor, and the floor is the world's price through the rate.
        GoodsMarket m = new GoodsMarket(Good.SUPPORT_WORK);
        m.strike(300, 0, 0);
        check("nobody here bidding - a seat-month fetches the export floor",
                m.getLocalPrice(), m.floor(), 1e-12);
        check("...which at founding is the world's own price",
                m.floor(), Good.SUPPORT_WORK.worldExportPrice(), 1e-12);
        m.setExchangeRate(.5);
        check("a currency twice as strong halves what the work fetches",
                m.floor(), Good.SUPPORT_WORK.worldExportPrice() * .5, 1e-12);
        m.setExchangeRate(2);
        check("...and a weak one doubles it",
                m.floor(), Good.SUPPORT_WORK.worldExportPrice() * 2, 1e-12);

        /* ================= 2. the templates are the design's arithmetic ================= */
        out.println("\n--- the three buildings ---");

        Path root = Files.createTempDirectory("bscheck");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game plain = new Game(files);
        quietly(plain::run);
        BuildingManager bm = plain.getBuildingManager();

        // Sourced per-seat figures, asserted against the model's own constants
        // rather than against a literal: 165 sq ft of floor at $550 all-in is
        // the cost, 250 sq ft of lot, 0.8 of power and 0.8 of road each.
        double[][] want = {
            // seats, allInThousands, land, power, road
            { 300, 27225, 75000, 240, 240 },
            { 300, 27225, 75000, 240, 240 },
            { 120, 10890, 30000,  96,  96 },
        };
        String[] names = { CONTACT, SHARED, OFFICE };
        Good[] makes = { Good.SUPPORT_WORK, Good.BACK_OFFICE_WORK, Good.ENGINEERING_WORK };

        for (int i = 0; i < names.length; i++) {
            BuildingsTemplate t = bm.getTemplateByName(names[i]);
            assertTrue(names[i] + " exists", t != null);
            if (t == null) continue;
            double seats = want[i][0];
            check(names[i] + ": a seat is a seat-month of work",
                    t.makes(makes[i]), seats, 0);
            check(names[i] + ": capacity is its seats", t.getCapacity(), seats, 0);
            check(names[i] + ": every seat is a job", jobsOf(t), seats, 0);
            check(names[i] + ": all-in cost",
                    t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials(),
                    want[i][1], 1);
            check(names[i] + ": 250 sq ft of lot a seat", t.getLandSqFt(), want[i][2], 1);
            check(names[i] + ": 0.8 power units a seat", t.getElectricityConsumption(), want[i][3], 1);
            check(names[i] + ": 0.8 of road load a seat", t.getRoadLoad(), want[i][4], 1);
            assertTrue(names[i] + " is owned by the sector, not the city",
                    Sectors.BUSINESS_SERVICES.equals(t.getSector()));
            assertTrue(names[i] + " pays no upkeep - it pays maintenance like everyone",
                    t.getUpkeep() == 0);
        }

        /* -------- the trap this batch actually fell into, now a standing check -------- */
        for (String n : names) {
            BuildingsTemplate t = bm.getTemplateByName(n);
            if (t == null) continue;
            for (JobType job : JobType.values()) {
                if (t.getJobs(job) <= 0) continue;
                if (!PopulationManager.isGated(job)) continue;
                assertTrue(n + " only has gated posts it declares a licence for",
                        job == t.getRequiresLicence());
            }
        }
        out.println("   (a gated job in a building with no licence declared is a trap: the seats");
        out.println("    can never be staffed AND the premium takes that wage to four times the");
        out.println("    band. Thirty finance posts in the shared-services centre put payroll at");
        out.println("    95% of revenue and bankrupted the sector nineteen times in 333 years.)");

        // Payroll against what the world pays, at the wages the game ships with.
        for (int i = 0; i < names.length; i++) {
            BuildingsTemplate t = bm.getTemplateByName(names[i]);
            if (t == null) continue;
            double payroll = 0;
            for (JobType job : JobType.values()) {
                payroll += t.getJobs(job) * PayTier.of(job).getMonthlyWage();
            }
            double revenue = want[i][0] * makes[i].worldExportPrice();
            double share = payroll / revenue;
            out.printf("   %-28s payroll is %.0f%% of what the world pays%n", names[i], share * 100);
            assertTrue(names[i] + ": profitable at founding wages, and only just",
                    share > .45 && share < .72);
        }

        /* ====================== 3. the licence gate ====================== */
        out.println("\n--- nobody licensed to practise in it ---");

        BuildingsTemplate office = bm.getTemplateByName(OFFICE);
        assertTrue("the engineering office declares its licence",
                office != null && office.getRequiresLicence() == JobType.UNIV_HIGHTECH_ENG);
        check("...and seventy-eight of its posts need it",
                office == null ? -1 : office.getLicensedPosts(), 78, 0);
        check("half of them is the bar", Game.LICENCE_COVER_TO_OPEN, .5, 0);
        check("...so thirty-nine engineers open one",
                plain.licencesNeededFor(office, 1), 39, 0);

        Game gated = new Game(files);
        quietly(() -> {
            gated.run();
            gated.getLandManager().setOwnedSqFt(gated.getLandManager().getOwnedSqFt() + 50_000_000L);
        });
        assertTrue("a city with no engineers cannot open one",
                !gated.hasLicencesFor(office, 1));
        assertTrue("...and is refused, rather than being offered a bond",
                gated.buildStack(office, 1, true) == Game.BuildResult.NO_LICENCE);

        // CAUSE the condition: hand the city engineers rather than wait for a
        // school. Thirty-eight is one short of the bar, and that is the point.
        double[] grant = new double[JobType.values().length];
        grant[JobType.UNIV_HIGHTECH_ENG.ordinal()] = 38;
        gated.getPopulationManager().addLicences(grant);
        assertTrue("thirty-eight engineers is still one short", !gated.hasLicencesFor(office, 1));
        grant[JobType.UNIV_HIGHTECH_ENG.ordinal()] = 1;
        gated.getPopulationManager().addLicences(grant);
        assertTrue("thirty-nine opens the doors", gated.hasLicencesFor(office, 1));
        Game.BuildResult built = gated.buildStack(office, 1, true);
        assertTrue("...and the office goes up", built == Game.BuildResult.SUCCESS);

        assertTrue("an engineer already at work is not spare",
                gated.getPopulationManager().spareLicences(JobType.UNIV_HIGHTECH_ENG)
                        <= 39);
        assertTrue("a contact centre needs no licence at all",
                gated.hasLicencesFor(bm.getTemplateByName(CONTACT), 1));

        /* ============ 4/5. the wage bill and the currency are the brake ============ */
        out.println("\n--- what closes it ---");

        Game city = new Game(files);
        quietly(() -> {
            city.run();
            BuildingManager b = city.getBuildingManager();
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 200_000_000L);
            // A real city around it, so the seats can be staffed and the books
            // have something to foot against.
            b.addStack(b.getTemplateByName("House"), 2000, true);
            b.addStack(b.getTemplateByName("Convenience Store"), 40, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 2, true);
            b.addStack(b.getTemplateByName("Paved Road"), 30, true);
            b.addStack(b.getTemplateByName("Contact Centre"), 2, true);
            for (int i = 0; i < 24; i++) city.simulateMonths(1);
        });

        BusinessServices bs = city.getSectors().businessServices();
        check("two centres is six hundred seats", bs.getSeats(), 600, 0);
        assertTrue("it billed somebody", bs.statement().revenue > 0);
        assertTrue("...and all of it left the city", bs.statement().exports > 0);
        check("everything it sold, it exported",
                bs.statement().exports, bs.statement().revenue, 1e-6);
        assertTrue("it bought no materials to do it", bs.statement().inputs <= 1e-9);
        double share = bs.payrollShare();
        out.printf("   payroll is %.0f%% of what it billed%n", share * 100);
        assertTrue("payroll is most of the cost and not all of it",
                share > .4 && share < 1.2);

        // THE CURRENCY. Move the rate, not the city: nothing about the seats,
        // the staff or the clients changes, and the sector dies anyway.
        double atPar = bs.priceOfSeat(Good.SUPPORT_WORK);
        city.getEconomyManager().setExchangeRate(.25);
        double strong = bs.priceOfSeat(Good.SUPPORT_WORK);
        city.getEconomyManager().setExchangeRate(3);
        double weak = bs.priceOfSeat(Good.SUPPORT_WORK);
        out.printf("   a seat fetches $%,.0f at par, $%,.0f on a strong currency, $%,.0f on a weak one%n",
                atPar * 1000, strong * 1000, weak * 1000);
        assertTrue("a strong currency cuts what the work fetches", strong < atPar);
        assertTrue("...and a weak one lifts it", weak > atPar);

        // And the brake reads it: the same building is worth building at one
        // rate and not at the other, with no other change anywhere.
        BusinessInvestment plans = city.getBusinessInvestment();
        BuildingsTemplate contact = bm.getTemplateByName(CONTACT);
        city.getEconomyManager().setExchangeRate(3);
        double profitWeak = bs.estimatedMonthlyProfit(contact, plans);
        city.getEconomyManager().setExchangeRate(.25);
        double profitStrong = bs.estimatedMonthlyProfit(contact, plans);
        out.printf("   the same centre: $%,.0fk a month on a weak currency, $%,.0fk on a strong one%n",
                profitWeak, profitStrong);
        assertTrue("the expansion test sees the difference", profitWeak > profitStrong);
        assertTrue("a strong enough currency makes another centre a loss", profitStrong < 0);
        city.getEconomyManager().setExchangeRate(1);

        // THE WAGE BILL. Same trick from the other side - move the floor every
        // wage is built on and the sector stops being worth expanding.
        double profitCheap = bs.estimatedMonthlyProfit(contact, plans);
        double floor = city.getLabourMarket().getMinimumWage();
        quietly(() -> {
            city.getLabourMarket().setMinimumWage(floor * 3);
            for (int i = 0; i < 36; i++) city.simulateMonths(1);
        });
        double profitDear = bs.estimatedMonthlyProfit(contact, plans);
        out.printf("   and on wages: $%,.0fk a month at the founding floor, $%,.0fk at three times it%n",
                profitCheap, profitDear);
        assertTrue("tripling the minimum wage makes another centre worth less",
                profitDear < profitCheap);

        /* ====================== 6. the books, and a reload ====================== */
        out.println("\n--- it books like everybody else ---");

        double worstAudit = 0, worstBooks = 0;
        for (SectorBooks.SectorMonth s : city.getSectorBooks().thisMonth()) {
            worstBooks = Math.max(worstBooks, Math.abs(s.unexplained()));
        }
        worstAudit = city.getLastMoneyAudit().relative();
        assertTrue("every sector's statement still foots", worstBooks < 1e-6);
        assertTrue("and the money identity holds with a new sector in it", worstAudit < 1e-9);

        /*
         * WHERE THIS SECTOR SITS, not how many there are. The first version
         * asserted "eight sectors" and "nine companies" and went red the day
         * Manufacturing was added, which is a harness reporting yesterday's
         * arithmetic as today's fault: nothing about Business Services cares
         * how many sectors come after it. What it does care about is that it
         * is still the EIGHTH - its slot is a saved index, in Equity's company
         * list and in every household's share block - and that the register is
         * still the sectors plus the bank and nothing else.
         */
        assertTrue("Business Services is the eighth sector, wherever the list ends",
                Sectors.KEYS.length > 7 && Sectors.KEYS[7].equals(Sectors.BUSINESS_SERVICES));
        assertTrue("the share register is the sectors and the bank",
                Equity.COMPANIES.length == Sectors.KEYS.length + 1);

        quietly(() -> city.saveGame(10));
        Game back = new Game(files);
        quietly(() -> { back.run(); back.loadGame(10); });
        BusinessServices bsBack = back.getSectors().businessServices();
        check("a reloaded city has the same seats", bsBack.getSeats(), bs.getSeats(), 0);
        check("...the same cash", back.getEconomyManager().getSectorCash(Sectors.BUSINESS_SERVICES),
                city.getEconomyManager().getSectorCash(Sectors.BUSINESS_SERVICES), 1e-9);
        check("...and the same engineering licences",
                back.getPopulationManager().getLicensed(JobType.UNIV_HIGHTECH_ENG),
                city.getPopulationManager().getLicensed(JobType.UNIV_HIGHTECH_ENG), 1e-9);

        /* ---- the save shape, which is the one thing that could corrupt quietly ---- */
        HouseholdBalance cells = city.getHouseholdBalance();
        String[] keys = cells.cellKeys();
        double[] now = cells.toCellSaveArray();
        check("a cell carries eight slots, a holding per company, the dollars abroad, the student debt and the cars",
                (now.length - 3.0) / keys.length, 8 + Equity.COMPANIES.length + 1 + 1 + 1, 0);

        // A save written one company short - which every save from the build
        // before this sector is. Read with the company list it was WRITTEN
        // with it must come back right; read with today's it would have
        // shifted every holding by one. Sized off the list rather than
        // written down, so the tenth sector does not make this red either.
        String[] oneShort = new String[Equity.COMPANIES.length - 1];
        System.arraycopy(Equity.COMPANIES, 0, oneShort, 0, oneShort.length);
        double[] old = new double[keys.length * (8 + oneShort.length + 1 + 1) + 3];
        for (int i = 0; i < old.length; i++) old[i] = i + 1;
        HouseholdBalance older = city.getHouseholdBalance();
        assertTrue("an older save restores against the company list it was written with",
                older.restoreCells(keys, old, oneShort));
        Household first = older.cells().get(0);
        assertTrue("...and owns none of the company that save had never heard of",
                first.shares[Equity.COMPANIES.length - 1] == 0);
        assertTrue("...while its first holding is still its first holding",
                first.shares[0] == 9);
        /*
         * THAT SAME ARRAY IS ALSO A SAVE FROM BEFORE CARS EXISTED - one slot a
         * cell short of today's - and it is filled with 1, 2, 3... so a reader
         * that walked off the end of it would hand this household a car park
         * rather than a zero. The whole of the car step rests on an old save
         * reloading into a city that behaves exactly as it did the day it was
         * written, and "exactly" starts here.
         */
        assertTrue("...and a city from before cars existed owns none",
                first.cars == 0);

        out.println();
        if (fails == 0) {
            out.println("Business Services works: the world pays, the wage bill decides, and the"
                    + " licence gate holds.");
        } else {
            out.println(fails + " check(s) failed.");
        }
        System.exit(fails);
    }

    static double jobsOf(BuildingsTemplate t) {
        double n = 0;
        for (JobType job : JobType.values()) n += t.getJobs(job);
        return n;
    }
}
