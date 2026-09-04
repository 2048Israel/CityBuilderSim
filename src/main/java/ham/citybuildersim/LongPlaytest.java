package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A city played for four thousand months, the way a person plays.
 *
 * WHY NOT JUST simulateMonths(4000)
 *
 * A single long skip is one input. It exercises the month loop and almost
 * nothing else: the player never intervenes, so the city settles into whatever
 * equilibrium it finds in the first fifty months and then repeats it. Every bug
 * that lives in the interaction between DECIDING and SIMULATING - the ones that
 * need something to be built, taxed, borrowed against, saved or reloaded partway
 * through - is invisible to it.
 *
 * So this alternates. Short hands-on stretches where an advisor looks at the
 * city and does something about it, long skips where it just runs, policy
 * changes, borrowing, land purchases, demolitions, and save/reload round trips
 * partway through. Roughly the shape of a real session, repeated a hundred and
 * forty times.
 *
 * WHAT IT IS LOOKING FOR
 *
 * Not "did it finish". A simulation that runs forever while quietly printing
 * nonsense is worse than one that throws. Every month is audited against a list
 * of things that must be true of any city in any state (below), and every
 * reload is checked against the game it came from to the cent. Findings are
 * deduplicated and reported with the month they first appeared, because a
 * problem that starts at month 900 and a problem that starts at month 3 are
 * different problems.
 */
public class LongPlaytest {

    /** Where the report goes. The game's own console output is discarded. */
    static PrintStream out;

    static final int TARGET_MONTHS = 4000;

    /* ===================================================================
       FINDINGS

       Deduplicated by message. A broken invariant usually breaks every month
       after it first breaks, and four thousand identical lines hide the second
       problem underneath the first.
       =================================================================== */

    static final Map<String, Finding> findings = new LinkedHashMap<>();

    static final class Finding {
        int count;
        int firstMonth;
        int lastMonth;
        String worst;
    }

    static void flag(int month, String what, String detail) {
        Finding f = findings.get(what);
        if (f == null) {
            f = new Finding();
            f.firstMonth = month;
            f.worst = detail;
            findings.put(what, f);
        }
        f.count++;
        f.lastMonth = month;
    }

    /* ===================================================================
       THE AUDIT

       Things that have to be true of a city in ANY state. Not balance
       opinions - if one of these is false, something is broken.
       =================================================================== */

    static void audit(Game g) {

        int month = g.getMonth();
        EconomyManager e = g.getEconomyManager();
        PopulationManager p = g.getPopulationManager();
        BuildingManager b = g.getBuildingManager();
        LandManager l = g.getLandManager();
        InfrastructureManager roads = g.getInfrastructureManager();

        finite(month, "cash", g.getCash());
        finite(month, "next-month income", g.getIncome());
        finite(month, "monthly GDP", e.getMonthGdp());
        finite(month, "annual GDP", e.getNationalAccounts().getAnnualGdp());
        finite(month, "total wage", p.getTotalWage());
        finite(month, "food price", e.getIndustrialHandler().getFoodPrice());
        finite(month, "materials price", b.getConstructionMaterialPrice());
        finite(month, "land price", l.getPricePerSqFt());

        if (p.getPopulation() < 0) {
            flag(month, "negative population", "" + p.getPopulation());
        }

        // updatePop() caps population at household capacity, so exceeding it
        // means somebody is housing people in buildings that do not exist.
        if (p.getPopulation() > b.getTotalHouseCapacity()) {
            flag(month, "more residents than housing",
                    p.getPopulation() + " in " + b.getTotalHouseCapacity());
        }

        if (p.getWorkforce() > p.getPopulation()) {
            flag(month, "more workers than residents",
                    p.getWorkforce() + " of " + p.getPopulation());
        }

        // Backlog item 7: getStoreIncome() sells without capping at stock.
        if (e.getStoreInventory() < 0) {
            flag(month, "negative store inventory", "" + e.getStoreInventory());
        }

        if (e.getIndustryFoodInventory() < 0) {
            flag(month, "negative food inventory", "" + e.getIndustryFoodInventory());
        }

        // Land committed can never exceed land owned; the difference is what a
        // build order is allowed to draw on.
        if (l.getAllocatedSqFt() > l.getOwnedSqFt() + 1e-6) {
            flag(month, "more land committed than owned",
                    String.format("%.0f of %.0f", l.getAllocatedSqFt(), l.getOwnedSqFt()));
        }

        if (g.getDebtManager().getAllPrincipal() < 0) {
            flag(month, "negative city debt", "" + g.getDebtManager().getAllPrincipal());
        }

        if (roads.getCapacity() < InfrastructureManager.BASE_CAPACITY - 1e-9) {
            flag(month, "road capacity below the base network", "" + roads.getCapacity());
        }
        if (roads.getLoad() < 0) {
            flag(month, "negative road load", "" + roads.getLoad());
        }
        double ratio = roads.getThroughputRatio();
        if (ratio < InfrastructureManager.MIN_THROUGHPUT - 1e-9 || ratio > 1 + 1e-9) {
            flag(month, "road throughput outside its bounds", "" + ratio);
        }

        double energy = g.getEnergyRatio();
        double water = g.getWaterRatio();
        if (energy < 0 || energy > 1 + 1e-9) {
            flag(month, "energy ratio outside 0..1", "" + energy);
        }
        if (water < 0 || water > 1 + 1e-9) {
            flag(month, "water ratio outside 0..1", "" + water);
        }

        for (BuildingsTemplate t : b.getTemplates()) {
            if (b.getQuantity(t.getId()) < 0) {
                flag(month, "negative building count", t.getName());
            }
        }

        for (String sector : BusinessDebtManager.SECTORS) {
            BusinessDebtManager credit = e.getBusinessDebtManager();
            double principal = credit.getPrincipal(sector);
            finite(month, "business debt (" + sector + ")", principal);
            finite(month, "business rate (" + sector + ")", credit.getRate(sector));
            finite(month, "business leverage (" + sector + ")", credit.getLeverage(sector));
            if (principal < -1e-6) {
                flag(month, "negative business debt", sector + " " + principal);
            }
        }

        /*
         * Everything the save actually writes.
         *
         * Gson refuses to serialise NaN or Infinity - it throws
         * IllegalArgumentException rather than writing it - so ONE field going
         * non-finite anywhere in the city means every save from that moment on
         * fails, including the autosave, which fires inside a skip where the
         * player cannot see the error. Finding the first month it happens and
         * the name of the field is the whole job; a save that throws is not a
         * degraded save, it is no save at all.
         */
        finite(month, "SAVE: construction cash",
                g.getServicesManager().getConstructionHandler().getCash());
        finite(month, "SAVE: construction unearned revenue",
                g.getServicesManager().getConstructionHandler().getUnearnedRevenue());
        finite(month, "SAVE: construction backlog",
                g.getServicesManager().getConstructionHandler().getBacklogPoints());
        finite(month, "SAVE: commercial cash", e.getCommercialHandler().getCommercialCash());
        finite(month, "SAVE: real estate cash", e.getCommercialHandler().getRealEstateCash());
        finite(month, "SAVE: industrial cash", e.getIndustrialCash());
        finite(month, "SAVE: heavy industry cash", e.getHeavyIndustryHandler().getCash());
        finite(month, "SAVE: household savings", g.getHouseholds().getCumulativeSaving());
        finite(month, "SAVE: land owned", l.getOwnedSqFt());
        finite(month, "SAVE: land price", l.getPricePerSqFt());
        finite(month, "SAVE: property tax charged", e.getTotalPropertyTax());
        finite(month, "SAVE: accrued city interest", e.getExpenses());
        finite(month, "SAVE: retail cost of goods", e.getRetailCostOfGoods());
        finite(month, "SAVE: retail fill basis", e.getRetailFillBasis());
        finite(month, "SAVE: retail import tax", e.getRetailImportTax());
        finite(month, "SAVE: industry demand", e.getIndustryDemand());

        finiteArray(month, "SAVE: property tax charges", e.getPropertyTaxCharges());
        finiteArray(month, "SAVE: interest charges", e.getInterestCharges());
        finiteArray(month, "SAVE: national accounts", e.getNationalAccountsState());
        finiteArray(month, "SAVE: commercial report", e.getCommercialReportState());
        finiteArray(month, "SAVE: industrial report", e.getIndustrialReportState());
        finiteArray(month, "SAVE: heavy industry report", e.getHeavyIndustryReportState());
        finiteArray(month, "SAVE: mining report", e.getMiningReportState());
        finiteArray(month, "SAVE: land listing", l.getMarket().getListingState());

        finite(month, "ore price", e.getIronMarket().getLocalPrice());
        finite(month, "iron reserves", l.getIronReserveTonnes());
        finite(month, "mining cash", e.getMiningHandler().getCash());

        if (l.getIronReserveTonnes() < 0) {
            flag(month, "negative iron reserves", "" + l.getIronReserveTonnes());
        }
        if (g.minesCommitted() > l.getIronDeposits()) {
            flag(month, "more mines than deposits",
                    g.minesCommitted() + " on " + l.getIronDeposits());
        }
        if (l.getListing().size() != LandMarket.LISTING_SIZE) {
            flag(month, "the land office window is the wrong size",
                    "" + l.getListing().size());
        }

        double ore = e.getIronMarket().getLocalPrice();
        if (ore < e.getIronMarket().getExportPrice() - 1e-9
                || ore > e.getIronMarket().getScrapPrice() + 1e-9) {
            flag(month, "ore price outside its band", "" + ore);
        }
    }

    static void finiteArray(int month, String what, double[] values) {
        if (values == null) return;
        for (int i = 0; i < values.length; i++) {
            finite(month, what + "[" + i + "]", values[i]);
        }
    }

    static void finite(int month, String what, double value) {
        if (Double.isNaN(value)) {
            flag(month, what + " went NaN", "NaN");
        } else if (Double.isInfinite(value)) {
            flag(month, what + " went infinite", "" + value);
        }
    }

    /* ===================================================================
       THE ADVISOR

       What a player would do on looking at the city. One or two moves per
       stop, in priority order, because that is what a person does - they fix
       the thing that is obviously wrong and press on.
       =================================================================== */

    static String advise(Game g) {

        EconomyManager e = g.getEconomyManager();
        PopulationManager p = g.getPopulationManager();
        BuildingManager b = g.getBuildingManager();
        InfrastructureManager roads = g.getInfrastructureManager();

        /*
         * 0. Room to grow.
         *
         * WHY THIS RULE HAD TO EXIST. The advisor only ever bought land as a
         * side effect of wanting to build something specific, so a city that was
         * merely FULL asked for nothing: every rule below is phrased as "is
         * something short", and running out of ground is not short of anything
         * yet. Traced month by month, the city ran itself down to 8,000 spare
         * square feet with $106M in the bank, sat there while private investment
         * had nowhere to go, and then the construction sector - with nothing
         * left to build - shed itself from 2,900 capacity to 100 over four
         * months, taking half the jobs with it.
         *
         * That collapse is NOT new. It happens identically under the old parcel
         * sizes, at the same months, to the same depth. What changed is that the
         * city used to blunder into enough surplus land to recover and now does
         * not, which makes this a fixture that was always wrong and only
         * sometimes lucky.
         *
         * A player watching their city suffocate at 98% with a full treasury
         * buys land. That is the entire reason the land listing exists.
         */
        LandManager land = g.getLandManager();
        double utilisation = (land.getOwnedSqFt() > 0)
                ? land.getAllocatedSqFt() / land.getOwnedSqFt() : 1;

        if (utilisation > .90) {
            // Best value per square foot, not cheapest - a city buying room to
            // grow wants the most ground per dollar, and the price of land is
            // driven by how much you already own, so a scrappy purchase costs
            // nearly as much in future pricing as a good one.
            LandParcel room = land.getMarket().bestValue();
            if (room != null && room.getPrice() < g.getCash() * .30
                    && g.buyLandParcel(room.getId())) {
                return "bought room to grow";
            }
        }

        // 1. Keep the lights on. A brownout throttles everything.
        if (g.getEnergyRatio() < .999 && qty(g, "Coal Power Plant") < 6) {
            if (build(g, "Coal Power Plant", 1)) return "power plant";
        }

        // 2. Water, same reason.
        if (g.getWaterRatio() < .999 && qty(g, "Water Treatment Plant") < 6) {
            if (build(g, "Water Treatment Plant", 1)) return "water plant";
        }

        // 3. Roads, once traffic is actually costing something.
        if (roads.isCongested() && qty(g, "Road Network") < 40) {
            if (build(g, "Road Network", 2)) return "roads";
        }

        // 4. Somewhere to live, if jobs are going unfilled for want of people.
        if (p.getPopulation() >= b.getTotalHouseCapacity() - 4) {
            if (build(g, "Low-Rise Apartments", 2)) return "apartments";
            if (build(g, "House", 25)) return "houses";
        }

        // 5. Somewhere to shop. Coverage below population means unmet demand.
        if (b.getTotalStoreCoverage() < p.getPopulation()) {
            if (build(g, "Small Grocery Store", 1)) return "grocery";
            if (build(g, "Convience Store", 3)) return "shops";
        }

        // 6. Food, if the shops are importing rather than buying local.
        if (e.getIndustryFoodInventory() < p.getPopulation() * 2
                && qty(g, "Food Processing Plant") < 8) {
            if (build(g, "Food Processing Plant", 1)) return "food plant";
        }

        /*
         * 7. Builders, and the retainer that keeps them - IN THAT ORDER, and
         *    without the retainer costing a move.
         *
         * The single most expensive lesson of the previous playtest: buying
         * depots and then letting the sector scrap them in the next lull cost
         * the city everything it had just gained, 187 times over. The fixture
         * learned that and then implemented it the wrong way round, which cost
         * a second playtest.
         *
         * TWO FAULTS, AND THEY COMPOUNDED.
         *
         * The retainer sat BELOW the depot rule and returned. So while capacity
         * was under 1,500 the advisor said "depot" on every move it had, the
         * retainer line was never reached, and the depots it had just bought
         * shed in the next lull - which kept capacity under 1,500, which kept
         * the advisor on the depot rule. A city that fell to 100 capacity could
         * never climb out: three moves buy three depots, +400 each, and 100+1200
         * is still short of the threshold it needs to clear to reach any rule
         * below it. Observed: 160 depots bought across 4,000 months, capacity
         * pinned at 1,300, population frozen at 1,118 for three centuries with
         * $100M in the bank.
         *
         * And the retainer was scaled to the capacity STANDING, so exactly when
         * the sector had collapsed - and needed paying to come back - it offered
         * 100 x .06 = six dollars. It is scaled to the capacity the city is
         * trying to have now.
         *
         * Setting it no longer returns, because paying a retainer is not a
         * month's work. It is a decision you make while doing something else.
         */
        double wantedCapacity = Math.max(b.getTotalConstructionCapacity(), 1500);
        double wantedSubsidy = Math.min(wantedCapacity * .06,
                Math.max(0, g.getCash()) * .02);
        if (wantedSubsidy > g.getConstructionSubsidy() * 1.25) {
            g.setConstructionSubsidy(wantedSubsidy);
        }

        if (b.getTotalConstructionCapacity() < 1500) {
            if (build(g, "Construction Depot", 1)) return "depot";
        }

        // 8. Ore. A deposit is the one thing the private sector cannot buy for
        // itself, and a mine is the biggest employer in the game.
        if (g.getLandManager().getIronDeposits() <= g.minesCommitted()) {
            LandParcel deposit = g.getLandManager().getMarket().richestDeposit();
            if (deposit != null && deposit.getPrice() < g.getCash() * .25
                    && g.buyLandParcel(deposit.getId())) {
                return "bought a deposit";
            }
        }
        if (g.getLandManager().hasUnminedDeposit(g.minesCommitted())) {
            if (build(g, "Iron Mine", 1)) return "mine";
        }
        if (qty(g, "Construction Materials Plant") < 3
                && b.getConstructionMaterials() < 4000) {
            if (build(g, "Construction Materials Plant", 1)) return "materials plant";
        }

        // 9. Jobs for their own sake.
        if (p.getWorkforce() > p.getTotalJobs()) {
            if (build(g, "Steel Foundry", 1)) return "foundry";
            if (build(g, "Texttile Mill", 1)) return "mill";
        }

        return null;
    }

    static int qty(Game g, String name) {
        BuildingsTemplate t = template(g, name);
        return t == null ? 0 : g.getBuildingManager().getQuantity(t.getId());
    }

    /**
     * Orders a building the way the screens do: check land, buy some if short,
     * borrow if the treasury cannot cover it, then place the order.
     */
    static boolean build(Game g, String name, int quantity) {

        BuildingsTemplate t = template(g, name);
        if (t == null) return false;

        // Land first, exactly as buildStack() checks it first.
        int guard = 0;
        while (g.getLandManager().getAvailableSqFt() < t.getLandSqFt() * quantity
                && guard++ < 60) {
            if (!g.buyLandBlock()) break;
        }

        Game.BuildResult result = g.buildStack(t, quantity, false);

        if (result == Game.BuildResult.NO_LAND) {
            refusal(name + ": no land");
        }

        if (result == Game.BuildResult.NEEDS_FUNDING) {
            // A city borrows for capital projects. Long bonds, because the
            // whole point of the instrument is small monthly payments.
            double needed = Math.max(t.getCashCost() * quantity * 1.6, 5000);
            if (canService(g, needed)) {
                g.handleLongBondLogic(needed, 20, 100);
                result = g.buildStack(t, quantity, false);
            }
        }

        if (result == Game.BuildResult.NEEDS_FUNDING) {
            refusal(name + ": no money");
        } else if (result == Game.BuildResult.SUCCESS) {
            refusal(name + ": BUILT");
        }

        return result == Game.BuildResult.SUCCESS;
    }

    /**
     * Whether the advisor can afford the PAYMENTS, not whether it likes the size.
     *
     * WHY THE PRINCIPAL LIMIT HAD TO GO
     *
     * This used to be "borrow while principal < 60 months of tax revenue".
     * Before the debt market was repriced, that was a workable proxy, because
     * every loan cost about the same: a city with no debt was quoted 1% whatever
     * it asked for. It is meaningless now. The same principal can cost 1% or
     * 20% depending on how large it is relative to the city, so a limit written
     * in units of principal says nothing about whether the city can pay.
     *
     * Left alone through the repricing it did what you would expect: the advisor
     * kept borrowing five years of revenue at rates approaching the ceiling, the
     * interest ate the budget, and the 4,000-month run finished at 6,372 people
     * and 3,453 months in emergency funding - against 39,875 before. Every
     * number in that run was about this line.
     *
     * A debt-service ratio is what a real municipal borrower actually tests, and
     * it self-scales: as the quoted rate rises, the amount that passes falls.
     * A quarter of revenue going to interest is at the aggressive end of what
     * real cities carry, which suits an advisor that is supposed to push.
     */
    static final double DEBT_SERVICE_LIMIT = .25;

    static boolean canService(Game g, double extra) {

        double monthlyRevenue = Math.max(g.getEconomyManager().getTaxIncome(), 0);
        if (monthlyRevenue <= 0) {
            return false;   // nothing to service it with
        }

        DebtManager market = g.getDebtManager();

        // Quoted WITH the new loan in it, which is the whole point of the
        // repricing: ask what this specific borrowing would cost, not what the
        // balance sheet happens to look like before the money arrives.
        double rate = market.quoteRate(extra);
        double projected = market.getAllPrincipal() + extra;
        double monthlyInterest = projected * rate / 12.0;

        return monthlyInterest <= monthlyRevenue * DEBT_SERVICE_LIMIT;
    }


    /**
     * Why the advisor could not do the thing it wanted to.
     *
     * A city that stops growing is the single most interesting thing a long run
     * can show, and "it stopped" is not a diagnosis. Counting the refusals says
     * whether it ran out of land, money, or ideas.
     */
    static final Map<String, Integer> refusals = new LinkedHashMap<>();

    static void refusal(String why) {
        refusals.merge(why, 1, Integer::sum);
    }

    static BuildingsTemplate template(Game g, String name) {
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        return null;
    }

    /* ===================================================================
       RUNNING MONTHS

       Every month is audited whether it was played by hand or inside a skip.
       A hundred-month skip that quietly breaks something at month 40 and
       repairs it by month 100 would otherwise look identical to one that
       never had a problem - which is the same reason TimeSkipReport samples
       month by month instead of diffing the endpoints.
       =================================================================== */

    static int refusedSkips = 0;

    static void run(Game g, int months) {
        for (int i = 0; i < months; i++) {

            int before = g.getMonth();
            g.simulateMonths(1);

            if (g.getMonth() == before) {
                /*
                 * simulateMonths() refuses to run at all while cash <= 0, but
                 * the Next Month button calls nextMonth() directly, which
                 * issues emergency debt and carries on. So a broke player can
                 * step but cannot skip - and stepping is exactly what they
                 * would do next. Doing the same here rather than giving up,
                 * because a city that cannot pay its bills is a state the game
                 * has to keep working in, not one to stop testing at.
                 */
                refusedSkips++;
                g.toggleNextMonth();

                if (g.getMonth() == before) {
                    flag(before, "the city could not advance even one month",
                            "cash " + g.getCash());
                    return;
                }
            }
            audit(g);
        }
    }

    /* ===================================================================
       SAVE / RELOAD, MID-RUN

       The strongest single check here. Everything the city holds has to
       survive a round trip, on whatever state a real run happens to be in -
       which is a far wider range of states than any fixture builds
       deliberately.
       =================================================================== */

    static void roundTrip(Game g, GameFiles files, int slot) {

        double cash = g.getCash();
        double income = g.getIncome();
        int pop = g.getPopulationManager().getPopulation();
        int workforce = g.getPopulationManager().getWorkforce();
        double gdp = g.getEconomyManager().getMonthGdp();
        double retail = g.getEconomyManager().getCommercialHandler().getGrossRevenue();
        double interest = g.getEconomyManager().getExpenses();
        int month = g.getMonth();

        if (!g.saveGame(slot, "playtest m" + month).ok) {
            flag(month, "a save failed", "slot " + slot);
            return;
        }

        Game back = new Game(files);
        back.loadGameSave(slot);

        if (back.getLoadFailure() != null) {
            flag(month, "a load failed", back.getLoadFailure());
            return;
        }

        same(month, "cash across a save", back.getCash(), cash);
        same(month, "next-month income across a save", back.getIncome(), income);
        same(month, "monthly GDP across a save", back.getEconomyManager().getMonthGdp(), gdp);
        same(month, "retail revenue across a save",
                back.getEconomyManager().getCommercialHandler().getGrossRevenue(), retail);
        same(month, "accrued city interest across a save",
                back.getEconomyManager().getExpenses(), interest);

        // Component by component, so a drift names itself instead of being
        // reported as "the income moved" three thousand months from its cause.
        EconomyManager was = g.getEconomyManager();
        EconomyManager now = back.getEconomyManager();
        same(month, "  ...business tax", now.getBusinessTax(), was.getBusinessTax());
        same(month, "  ...wage tax", now.getWageTax(), was.getWageTax());
        same(month, "  ...sales tax", now.getSalesTax(), was.getSalesTax());
        same(month, "  ...property tax", now.getTotalPropertyTax(), was.getTotalPropertyTax());
        same(month, "  ...industrial tax", now.getIndustrialTax(), was.getIndustrialTax());
        same(month, "  ...utility income",
                back.getServicesManager().getServiceNetIncome(),
                g.getServicesManager().getServiceNetIncome());
        same(month, "  ...mining net income",
                now.getMiningHandler().getReportNetIncome(),
                was.getMiningHandler().getReportNetIncome());
        same(month, "  ...heavy industry net income",
                now.getHeavyIndustryHandler().getReportNetIncome(),
                was.getHeavyIndustryHandler().getReportNetIncome());
        same(month, "  ...ore price",
                now.getIronMarket().getLocalPrice(), was.getIronMarket().getLocalPrice());

        if (back.getPopulationManager().getPopulation() != pop) {
            flag(month, "population across a save",
                    back.getPopulationManager().getPopulation() + " != " + pop);
        }
        if (back.getPopulationManager().getWorkforce() != workforce) {
            flag(month, "workforce across a save",
                    back.getPopulationManager().getWorkforce() + " != " + workforce);
        }
    }

    static void same(int month, String what, double actual, double expected) {
        if (Math.round(actual * 10000) != Math.round(expected * 10000)) {
            flag(month, what, String.format("%.4f != %.4f", actual, expected));
        }
    }

    /* =================================================================== */

    public static void main(String[] args) throws Exception {

        out = System.out;

        Path root = Files.createTempDirectory("playtest");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        // The game narrates every month. Four thousand months of it is neither
        // fast nor readable, so it goes nowhere.
        PrintStream quiet = new PrintStream(new OutputStream() {
            @Override public void write(int b) { }
            @Override public void write(byte[] b, int off, int len) { }
        });

        Game g = new Game(files);
        List<String> log = new ArrayList<>();
        long started = System.currentTimeMillis();

        System.setOut(quiet);
        try {
            g.run();

            /* ---------- founding: a few months at a time, by hand ---------- */
            build(g, "House", 40);
            build(g, "Convience Store", 3);
            run(g, 3);
            build(g, "House", 20);
            run(g, 4);
            build(g, "Convience Store", 2);
            build(g, "Construction Depot", 1);
            run(g, 5);
            advise(g);
            run(g, 6);

            log.add(era(g, "founded, played by hand"));

            /* ---------- then the real rhythm ---------- */
            int stop = 0;
            int slot = 1;
            int nextCheckpoint = 250;

            while (g.getMonth() < TARGET_MONTHS) {

                stop++;

                // A skip, of the length a person actually clicks: mostly a
                // year or a decade, occasionally a century.
                int skip = switch (stop % 6) {
                    case 0 -> 100;
                    case 1 -> 12;
                    case 2 -> 24;
                    case 3 -> 60;
                    case 4 -> 6;
                    default -> 120;
                };
                run(g, Math.min(skip, TARGET_MONTHS - g.getMonth()));

                // Look at the city, fix the worst thing, then a few hands-on
                // months watching what that did - the way anyone plays.
                for (int move = 0; move < 3; move++) {
                    if (advise(g) == null) break;
                    run(g, 1);
                }
                run(g, 2);

                // Policy changes, occasionally, the way a player fiddles.
                if (stop % 11 == 0) {
                    TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
                    double income = tax.getIncomeTaxRate();
                    tax.setIncomeTaxRate(income > .25 ? .12 : income + .06);
                    log.add(String.format("  m%-5d income tax -> %.0f%%",
                            g.getMonth(), tax.getIncomeTaxRate() * 100));
                }
                if (stop % 17 == 0) {
                    TaxPolicy tax = g.getEconomyManager().getTaxPolicy();
                    double prop = tax.getPropertyTaxRate();
                    tax.setPropertyTaxRate(prop > .03 ? .01 : prop + .01);
                    log.add(String.format("  m%-5d property tax -> %.1f%%",
                            g.getMonth(), tax.getPropertyTaxRate() * 100));
                }
                // Land price used to be a dial the player turned. It is the
                // market's now, so what a player actually does instead is go
                // and buy some - which changes the price by changing how full
                // the city is.
                if (stop % 13 == 0) {
                    LandParcel spare = g.getLandManager().getMarket().bestValue();
                    if (spare != null && spare.getPrice() < g.getCash() * .1) {
                        g.buyLandParcel(spare.getId());
                    }
                }

                // Scrap something now and then. Demolition is the one thing a
                // player does that makes the city smaller, and the load path
                // has been wrong about it before.
                if (stop % 19 == 0) {
                    BuildingsTemplate house = template(g, "House");
                    if (house != null && g.getBuildingManager().getQuantity(house.getId()) > 60) {
                        g.getBuildingManager().retire(house, 10);
                    }
                }

                // Save and reload against a live city, on whatever state the
                // run happens to be in.
                if (stop % 7 == 0) {
                    roundTrip(g, files, slot);
                    slot = (slot % 9) + 1;
                }

                if (g.getMonth() >= nextCheckpoint) {
                    log.add(era(g, "checkpoint"));
                    nextCheckpoint += 250;
                }
            }

            log.add(era(g, "final"));

        } catch (Throwable t) {
            System.setOut(out);
            out.println("\n!!! THREW at month " + g.getMonth() + ": " + t);
            for (StackTraceElement s : t.getStackTrace()) {
                out.println("      " + s);
                if (s.getClassName().startsWith("ham.")) { }
            }
            flag(g.getMonth(), "the simulation threw", t.toString());
        } finally {
            System.setOut(out);
        }

        long seconds = (System.currentTimeMillis() - started) / 1000;

        /* ---------------------------- the report ---------------------------- */

        out.println("=================================================================");
        out.println("  LONG PLAYTEST - " + g.getMonth() + " months ("
                + (g.getMonth() / 12) + " years) in " + seconds + "s");
        out.println("=================================================================\n");

        for (String line : log) {
            out.println(line);
        }

        out.println();
        out.println("  months a skip refused to run (treasury empty, stepped instead): "
                + refusedSkips);

        out.println("\n--- what the advisor tried, and what happened ---\n");
        refusals.entrySet().stream()
                .sorted((x, y) -> y.getValue() - x.getValue())
                .limit(24)
                .forEach(en -> out.printf("  %-46s %6d%n", en.getKey(), en.getValue()));

        out.println("\n--- findings ---\n");
        if (findings.isEmpty()) {
            out.println("  Nothing. Every month passed the audit and every reload matched.");
        } else {
            for (Map.Entry<String, Finding> entry : findings.entrySet()) {
                Finding f = entry.getValue();
                out.printf("  %-46s %5d time(s), months %d-%d%n",
                        entry.getKey(), f.count, f.firstMonth, f.lastMonth);
                if (f.worst != null) {
                    out.println("        first seen as: " + f.worst);
                }
            }
        }

        cleanUp(root);
        System.exit(findings.isEmpty() ? 0 : 1);
    }

    static String era(Game g, String label) {
        EconomyManager e = g.getEconomyManager();
        PopulationManager p = g.getPopulationManager();
        BuildingManager b = g.getBuildingManager();
        InfrastructureManager roads = g.getInfrastructureManager();

        return String.format(
                "m%-5d %-22s pop %-7d cash %-14s GDP/mo %-11s jobs %-6d "
                + "fill %3.0f%% roads %3.0f%% power %3.0f%% water %3.0f%% "
                + "cityDebt %-12s bizDebt %-11s land %3.0f%% mines %d/%d ore $%.2f",
                g.getMonth(), label,
                p.getPopulation(),
                money(g.getCash()),
                money(e.getMonthGdp()),
                p.getTotalJobs(),
                (p.getTotalJobs() > 0
                        ? 100.0 * Math.min(p.getWorkforce(), p.getTotalJobs()) / p.getTotalJobs()
                        : 100),
                roads.getThroughputRatio() * 100,
                g.getEnergyRatio() * 100,
                g.getWaterRatio() * 100,
                money(g.getDebtManager().getAllPrincipal()),
                money(e.getBusinessDebtManager().getTotalPrincipal()),
                g.getLandManager().getUtilisation() * 100,
                g.minesCommitted(), g.getLandManager().getIronDeposits(),
                e.getIronMarket().getLocalPrice());
    }

    static String money(double v) {
        double abs = Math.abs(v);
        if (abs >= 1e9) return String.format("$%.1fT", v / 1e9);
        if (abs >= 1e6) return String.format("$%.1fB", v / 1e6);
        if (abs >= 1e3) return String.format("$%.1fM", v / 1e3);
        return String.format("$%.0fk", v);
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                try { Files.deleteIfExists(path); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
