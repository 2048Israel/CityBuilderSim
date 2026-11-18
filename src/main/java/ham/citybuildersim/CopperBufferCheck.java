package ham.citybuildersim;

import ham.citybuildersim.sectors.CopperRefining;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A stock buffer must not buy feed worth more than its undemanded output (CPB1).
 *
 * WHY. WN3 covered a refinery with a full shed; an empty shed still bought
 * imported anode at a loss solely to reach Sector.DUMP_THRESHOLD. Exercise
 * the real market, strike and accounts, keeping the recipe and GDP laws.
 */
public class CopperBufferCheck {
    static int fails;
    static final PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static Game fixture(String label, double feed, double sale) {
        Game g;
        System.setOut(quiet);
        try {
            g = new Game(GameFiles.scratch("copperbuffer-" + label));
            g.newGame();
            g.playOnLiterals();
            BuildingManager b = g.getBuildingManager();
            b.addStack(b.getTemplate(CopperRefining.ELECTROLYTIC_REFINERY), 1, true);
        } finally { System.setOut(out); }
        CopperRefining cr = g.getSectors().copperRefining();
        cr.setCash(1e9);
        GoodsMarket anode = g.getMarkets().get(Good.ANODE), cathode = g.getMarkets().get(Good.COPPER);
        anode.setWorldPrices(feed, feed - 1);
        cathode.setWorldPrices(sale + 1, sale);
        anode.strike(0, 0, 0);
        cathode.strike(0, 0, 0);
        return g;
    }

    static void clear(Game g) {
        System.setOut(quiet);
        try { g.getMarkets().clearMonth(g.getSectors(), g); }
        finally { System.setOut(out); }
    }

    static void accounts(Game g) {
        System.setOut(quiet);
        try {
            EconomyManager e = g.getEconomyManager();
            e.strikeSectors();
            e.updateNationalAccounts(0, 0, 0, 0, 0, 0, 0);
        } finally { System.setOut(out); }
    }

    static void loss() {
        out.println("--- 1. empty refinery yard, no local demand, imported anode dearer than cathode ---");
        Game g = fixture("loss", 11, 9);
        CopperRefining cr = g.getSectors().copperRefining();
        GoodsMarket cathode = g.getMarkets().get(Good.COPPER);
        check("premise: real refinery, empty yard, no cathode demand, negative input spread",
                cr.getCapacity(Good.COPPER) > 0 && cr.getStock(Good.COPPER) == 0 && cathode.getDemand() == 0
                && cr.flowCostPerUnit(Good.COPPER) > cathode.netExportPrice());
        double planned = cr.getPlannedOutput(Good.COPPER);
        cr.planOrders();
        double share = cr.orderShare(Good.COPPER), bid = cr.bid(Good.ANODE);
        out.printf("plan %.9f; share %.9f; anode bid %.9f; input/unit %.9f; export %.9f%n",
                planned, share, bid, cr.flowCostPerUnit(Good.COPPER), cathode.netExportPrice());
        check("no loss-making buffer is planned or ordered", planned == 0 && share == 0 && bid == 0);
        cr.endOrders();
        clear(g);
        Sector.Input in = cr.inputRow(Good.ANODE);
        Sector.Output cu = cr.outputRow(Good.COPPER);
        double made = cu.produced + cu.exportBound + cu.captive;
        double imported = in.imported;
        BuildingsTemplate works = g.getBuildingManager().getTemplate(CopperRefining.ELECTROLYTIC_REFINERY);
        double ratio = works.uses(Good.ANODE) / works.makes(Good.COPPER);
        check("every anode unit received becomes cathode at the real recipe ratio",
                near(in.boughtLocal + in.imported, made * ratio));
        check("the real clearing buys no buffer feed and makes no undemanded cathode",
                in.bid == 0 && in.imported == 0 && in.boughtLocal == 0 && made == 0 && cr.getStock(Good.COPPER) == 0);
        accounts(g);
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        double other = cr.statement().otherInputs.getOrDefault(Sector.CONSUMABLES, 0.0);
        out.printf("made %.9f; anode imported %.9f; raw imports %.9f; inventory %.9f; GDP %.9f%n",
                made, imported, na.getImportsRawMaterial(), na.getInvestmentInventories(), na.getGdp());
        check("the struck refinery imports no anode beyond its separately booked consumables",
                near(cr.statement().imports, other));
        check("no buffer spread loss remains after its independently booked consumables",
                near(na.getGdp() + other, 0));
        check("the accounts keep GDP = C + I + G + NX",
                near(na.getGdp(), na.getConsumption() + na.getInvestment() + na.getGovernment() + na.getNetExports()));
    }

    static void demandAndProfit() {
        out.println("--- 2. domestic demand, captive demand and profitable buffer replenishment survive ---");
        Game g = fixture("home", 11, 9);
        CopperRefining cr = g.getSectors().copperRefining();
        GoodsMarket cathode = g.getMarkets().get(Good.COPPER);
        double demand = cr.madeAtRate(Good.COPPER) / (2 * Sector.STOCK_MONTHS);
        cathode.strike(0, 0, demand);
        double expected = Math.min(cr.madeAtRate(Good.COPPER), demand * Sector.STOCK_MONTHS);
        cr.planOrders();
        check("actual local demand still plans its stock cover and orders anode despite the negative spread",
                near(cr.getPlannedOutput(Good.COPPER), expected) && cr.bid(Good.ANODE) > 0);
        cr.endOrders();
        clear(g);
        Sector.Output home = cr.outputRow(Good.COPPER);
        Sector.Input homeFeed = cr.inputRow(Good.ANODE);
        BuildingsTemplate homeWorks = g.getBuildingManager().getTemplate(CopperRefining.ELECTROLYTIC_REFINERY);
        double expectedFeed = expected * homeWorks.uses(Good.ANODE) / homeWorks.makes(Good.COPPER);
        check("the real clearing makes the demanded stock cover and buys exactly its feed at the negative spread",
                near(home.produced, expected) && home.exportBound == 0 && near(cr.getStock(Good.COPPER), expected)
                && near(homeFeed.bid, expectedFeed) && near(homeFeed.boughtLocal + homeFeed.imported, expectedFeed));

        Game captive = fixture("captive", 11, 9);
        BuildingManager b = captive.getBuildingManager();
        b.addStack(b.getTemplate(CopperRefining.ROD_MILL), 1, true);
        GoodsMarket rod = captive.getMarkets().get(Good.COPPER_ROD);
        double rodDemand = b.getTemplate(CopperRefining.ROD_MILL).makes(Good.COPPER_ROD);
        rod.strike(0, 0, rodDemand);
        CopperRefining chain = captive.getSectors().copperRefining();
        chain.planOrders();
        check("cathode for a domestic rod order is still captive and its refinery still orders feed",
                chain.captiveUnits(Good.COPPER) > 0 && chain.bid(Good.ANODE) > 0);
        chain.endOrders();
        clear(captive);
        Sector.Output cu = chain.outputRow(Good.COPPER);
        Sector.Input anode = chain.inputRow(Good.ANODE);
        BuildingsTemplate refinery = b.getTemplate(CopperRefining.ELECTROLYTIC_REFINERY);
        double used = (cu.captive + cu.produced + cu.exportBound) * refinery.uses(Good.ANODE) / refinery.makes(Good.COPPER);
        check("the partially captive refinery buys exactly the feed it makes, without an unprofitable surplus buffer",
                cu.captive > 0 && cu.produced == 0 && cu.exportBound == 0 && near(anode.imported + anode.boughtLocal, used));

        Game pays = fixture("pays", 2, 9);
        CopperRefining profitable = pays.getSectors().copperRefining();
        double buffer = Math.min(profitable.madeAtRate(Good.COPPER),
                profitable.getStockCapacity(Good.COPPER) * Sector.DUMP_THRESHOLD);
        profitable.planOrders();
        check("a profitable empty buffer keeps the model's full buffer target and feed order",
                buffer > 0 && near(profitable.getPlannedOutput(Good.COPPER), buffer) && profitable.bid(Good.ANODE) > 0);
        profitable.endOrders();
        clear(pays);
        check("the profitable buffer is replenished through the real clearing", profitable.getStock(Good.COPPER) > 0);
        check("a profitable full-rate buffer keeps its full production and input order to the bit",
                profitable.outputRow(Good.COPPER).produced == profitable.madeAtRate(Good.COPPER)
                && profitable.inputRow(Good.ANODE).bid == profitable.getInputAtCapacity(Good.ANODE) * profitable.getOperatingRate());
    }

    static void committedFeed() {
        out.println("--- 4. recovered copper already committed is made, without starting the other refinery ---");
        Game g = fixture("committed", 11, 9);
        BuildingManager b = g.getBuildingManager();
        BuildingsTemplate scrap = b.getTemplate(CopperRefining.SCRAP_REFINERY);
        b.addStack(scrap, 1, true);
        GoodsMarket feed = g.getMarkets().get(Good.COPPER_SCRAP);
        feed.setWorldPrices(11, 10);
        feed.strike(0, 0, 0);
        double recovered = scrap.uses(Good.COPPER_SCRAP) * g.getSectors().copperRefining().getOperatingRate() / 4;
        g.getOldMetal().carsScrapped(recovered / OldMetal.COPPER_A_CAR / OldMetal.RECOVERY);
        CopperRefining cr = g.getSectors().copperRefining();
        clear(g);
        Sector.Output cu = cr.outputRow(Good.COPPER);
        double made = cu.produced + cu.exportBound + cu.captive;
        double ratio = scrap.uses(Good.COPPER_SCRAP) / scrap.makes(Good.COPPER);
        check("premise: the two refinery recipes have no local demand and imported feed loses money",
                g.getMarkets().get(Good.COPPER).getDemand() == 0 && recovered > 0
                && cr.flowCostPerUnit(Good.COPPER) > g.getMarkets().get(Good.COPPER).netExportPrice());
        check("all recovered copper paid for becomes cathode at the scrap recipe ratio",
                near(cr.oldCopperTaken(), recovered) && near(made * ratio, recovered));
        check("committed scrap starts only its own works, with no imported anode or scrap",
                cr.inputRow(Good.ANODE).bid == 0 && cr.inputRow(Good.COPPER_SCRAP).bid == 0
                && cr.inputRow(Good.ANODE).imported == 0 && cr.inputRow(Good.COPPER_SCRAP).imported == 0);
        // The next clearing has no new recovered feed. Last month's purchase
        // must not make its marginal export forecast look profitable.
        clear(g);
        cu = cr.outputRow(Good.COPPER);
        check("last month's recovered feed never permits this month's speculative imports",
                cr.oldCopperTaken() == 0 && cr.inputRow(Good.ANODE).bid == 0 && cr.inputRow(Good.COPPER_SCRAP).bid == 0
                && cu.produced + cu.exportBound + cu.captive == 0);

        Game solo = fixture("committed-last-month", 11, 9);
        BuildingManager sb = solo.getBuildingManager();
        sb.retire(sb.getTemplate(CopperRefining.ELECTROLYTIC_REFINERY), 1);
        sb.addStack(sb.getTemplate(CopperRefining.SCRAP_REFINERY), 1, true);
        feed = solo.getMarkets().get(Good.COPPER_SCRAP);
        feed.setWorldPrices(11, 10);
        feed.strike(0, 0, 0);
        CopperRefining single = solo.getSectors().copperRefining();
        double full = single.getInputAtCapacity(Good.COPPER_SCRAP) * single.getOperatingRate();
        solo.getOldMetal().carsScrapped(full / OldMetal.COPPER_A_CAR / OldMetal.RECOVERY);
        clear(solo);
        check("premise: recovered feed made the standalone scrap refinery's last run profitable",
                single.oldCopperTaken() > 0 && single.getMarginalCostPerUnit(Good.COPPER)
                < solo.getMarkets().get(Good.COPPER).netExportPrice());
        clear(solo);
        check("the standalone refinery costs this month's zero recovered feed before ordering imports",
                single.oldCopperTaken() == 0 && single.inputRow(Good.COPPER_SCRAP).bid == 0
                && single.outputRow(Good.COPPER).produced + single.outputRow(Good.COPPER).exportBound == 0);
    }

    static void existingStock() {
        out.println("--- 3. existing stock still sells and an actual trading loss stays in GDP ---");
        Game g = fixture("stock", 11, 9);
        CopperRefining cr = g.getSectors().copperRefining();
        GoodsMarket market = g.getMarkets().get(Good.COPPER);
        double room = cr.getStockCapacity(Good.COPPER);
        cr.setStock(Good.COPPER, room);
        clear(g);
        Sector.Output sold = cr.outputRow(Good.COPPER);
        Sector.Input stockFeed = cr.inputRow(Good.ANODE);
        check("the real clearing exports existing stock above the dump line at a loss, with no new output or feed",
                near(cr.getStock(Good.COPPER), room * Sector.DUMP_THRESHOLD)
                && near(sold.exported, room * (1 - Sector.DUMP_THRESHOLD)) && sold.produced == 0 && sold.exportBound == 0
                && stockFeed.bid == 0 && stockFeed.boughtLocal + stockFeed.imported == 0
                && near(cr.pending().exports, sold.exported * market.exportPrice()));

        Game raw = fixture("raw-gdp", 11, 9);
        Sector r = raw.getSectors().copperRefining();
        BuildingsTemplate works = raw.getBuildingManager().getTemplate(CopperRefining.ELECTROLYTIC_REFINERY);
        double made = r.madeAtRate(Good.COPPER), feed = made * works.uses(Good.ANODE) / works.makes(Good.COPPER);
        GoodsMarket anode = raw.getMarkets().get(Good.ANODE);
        Trade bought = anode.record(Trade.WORLD, r.key(), feed, anode.importPrice());
        r.bookPurchase(bought);
        r.receiveInput(Good.ANODE, feed);
        r.setStock(Good.COPPER, made);
        accounts(raw);
        NationalAccounts na = raw.getEconomyManager().getNationalAccounts();
        double held = made * EconomyManager.heldPrice(raw.getMarkets().get(Good.COPPER));
        check("a forced loss is still negative GDP, held output less the actual imported feed",
                held < bought.value() && na.getGdp() < 0 && near(na.getGdp(), held - bought.value())
                && near(na.getImportsRawMaterial(), bought.value()) && near(na.getInvestmentInventories(), held));
    }

    public static void main(String[] args) {
        loss();
        demandAndProfit();
        existingStock();
        committedFeed();
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
