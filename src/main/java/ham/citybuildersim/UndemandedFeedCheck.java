package ham.citybuildersim;

import ham.citybuildersim.sectors.CopperRefining;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * No feed is bought for undemanded output that cannot cover it (CPB2).
 *
 * WHY. CPB1 gated an undemanded stock buffer on its recipe's input cost, and
 * REV-CPB1-1 found the two ways round the gate: the buffer it refused came
 * back as export spare, priced on the joint bill's share by sales value,
 * which charged cathode beside a rod mill less than its own anode; and for a
 * good the world will not buy the net export price is NaN, and `NaN < cost`
 * refused nothing, so a van plant nobody had asked for filled its shed on
 * imported machinery. Building it found a third: committed recovered copper
 * runs its refinery whatever the order share, and the share was still spread
 * over the whole nameplate, so the anode refinery beside a rod mill was
 * ordered as if the scrap did not cover any of it. All three are caused here
 * in real sectors through Markets.clearMonth and the strike and the accounts.
 *
 * What this has to prove:
 *   1. cathode beside a rod mill, undemanded, its net export price over its
 *      allocated marginal cost and under its anode: no anode is ordered or
 *      bought for the spare, none of it ships, the accounts add up;
 *   2. kept beside it: a full shed's profitable spare is still ordered whole and
 *      made, and recovered copper already paid for still runs and ships at the
 *      marginal cost, its feed sunk;
 *   3. recovered copper committed beside a demanded rod mill: the anode
 *      refinery is ordered only for the rod mill's cathode the scrap does not
 *      make, so none ships on imported anode below its cost;
 *   4. every real plant of a stockable good the world will not buy, with no
 *      demand here: no buffer, no feed, nothing made, the accounts add up;
 *   5. kept beside it: demanded vans make their cover and buy their feed,
 *      profitable cars run beside an idle locomotive works, and vans already
 *      in the shed sell to a real fleet with no new feed.
 */
public class UndemandedFeedCheck {
    static int fails;
    static final PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-110s %s%n", label, ok ? "OK" : "FAIL");
    }

    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static Game city(String label) {
        System.setOut(quiet);
        try {
            Game g = new Game(GameFiles.scratch("undemandedfeed-" + label));
            g.newGame();
            g.playOnLiterals();
            return g;
        } finally { System.setOut(out); }
    }

    static void build(Game g, BuildingsTemplate t) {
        System.setOut(quiet);
        try { g.getBuildingManager().addStack(t, 1, true); }
        finally { System.setOut(out); }
    }

    static void clear(Game g) {
        System.setOut(quiet);
        try { g.getMarkets().clearMonth(g.getSectors(), g); }
        finally { System.setOut(out); }
    }

    static NationalAccounts accounts(Game g) {
        System.setOut(quiet);
        try {
            EconomyManager e = g.getEconomyManager();
            e.strikeSectors();
            e.updateNationalAccounts(0, 0, 0, 0, 0, 0, 0);
        } finally { System.setOut(out); }
        return g.getEconomyManager().getNationalAccounts();
    }

    static boolean identity(NationalAccounts na) {
        return near(na.getGdp(), na.getConsumption() + na.getInvestment() + na.getGovernment() + na.getNetExports());
    }

    /* ----- 1 to 3: a refinery beside a rod mill ----- */

    /**
     * A refinery and a rod mill, anode at 11/10, cathode at (sale + 1)/sale
     * and rod at its own band; with `rodWanted` the rod mill's whole nameplate
     * of rod is demanded at home, so its cathode is captive.
     */
    static Game copperPair(String label, double sale, double rodAsk, double rodBid, boolean rodWanted) {
        Game g = city(label);
        BuildingManager b = g.getBuildingManager();
        build(g, b.getTemplate(CopperRefining.ELECTROLYTIC_REFINERY));
        build(g, b.getTemplate(CopperRefining.ROD_MILL));
        g.getSectors().copperRefining().setCash(1e9);
        Markets m = g.getMarkets();
        m.get(Good.ANODE).setWorldPrices(11, 10);
        m.get(Good.COPPER).setWorldPrices(sale + 1, sale);
        m.get(Good.COPPER_ROD).setWorldPrices(rodAsk, rodBid);
        m.get(Good.ANODE).strike(0, 0, 0);
        m.get(Good.COPPER).strike(0, 0, 0);
        m.get(Good.COPPER_ROD).strike(0, 0, rodWanted ? b.getTemplate(CopperRefining.ROD_MILL).makes(Good.COPPER_ROD) : 0);
        return g;
    }

    static void allocatedSpare() {
        out.println("--- 1. cathode beside a rod mill: the spare a refused buffer leaves is not ordered below its anode ---");
        Game g = copperPair("spare", 9, 10.75, 10.25, true);
        BuildingManager b = g.getBuildingManager();
        CopperRefining cr = g.getSectors().copperRefining();
        GoodsMarket cathode = g.getMarkets().get(Good.COPPER), rod = g.getMarkets().get(Good.COPPER_ROD);
        double nameplate = cr.madeAtRate(Good.COPPER), own = cr.captiveUnits(Good.COPPER);
        double net = cathode.netExportPrice(), marginal = cr.getMarginalCostPerUnit(Good.COPPER), recipe = cr.flowCostPerUnit(Good.COPPER);
        out.printf("cathode nameplate %.6f, rod mill's %.6f; allocated marginal %.9f <= net export %.9f < anode a cathode %.9f%n",
                nameplate, own, marginal, net, recipe);
        check("premise: rod demanded at home, cathode not, the joint bill's share under the export price and the anode over it",
                rod.getDemand() > 0 && cathode.getDemand() == 0 && cr.getStock(Good.COPPER) == 0
                && own > 0 && own < nameplate && marginal <= net && net < recipe);
        cr.planOrders();
        double share = cr.orderShare(Good.COPPER), bid = cr.bid(Good.ANODE), planned = cr.getPlannedOutput(Good.COPPER);
        cr.endOrders();
        BuildingsTemplate works = b.getTemplate(CopperRefining.ELECTROLYTIC_REFINERY);
        double ratio = works.uses(Good.ANODE) / works.makes(Good.COPPER);
        out.printf("plan %.6f; order share %.9f; anode bid %.6f (the rod mill's cathode takes %.6f)%n", planned, share, bid, own * ratio);
        check("the refused buffer's spare is not ordered: the cathode's order is the rod mill's alone",
                planned == 0 && near(share, own / nameplate));
        check("the anode bid is the rod mill's cathode at the recipe ratio, none for an export below its cost",
                near(bid, own * ratio));
        clear(g);
        Sector.Output cu = cr.outputRow(Good.COPPER), rods = cr.outputRow(Good.COPPER_ROD);
        Sector.Input anode = cr.inputRow(Good.ANODE);
        double bought = anode.boughtLocal + anode.imported;
        out.printf("cleared: captive %.6f, produced %.6f, export-bound %.6f, exported %.6f; anode bought %.6f%n",
                cu.captive, cu.produced, cu.exportBound, cu.exported, bought);
        check("the real clearing makes the rod mill's cathode and ships none below its anode",
                near(cu.captive, own) && cu.produced == 0 && cu.exportBound == 0 && cu.exported == 0 && cr.getStock(Good.COPPER) == 0);
        check("the anode bought is exactly the cathode made, at the recipe ratio",
                near(bought, (cu.captive + cu.produced + cu.exportBound) * ratio) && near(bought, own * ratio));
        check("the refinery's export takings are the rod's alone",
                near(cr.pending().exports, rods.exported * rod.exportPrice()));
        NationalAccounts na = accounts(g);
        out.printf("GDP %.9f; exports %.9f; raw imports %.9f; inventories %.9f%n",
                na.getGdp(), na.getExports(), na.getImportsRawMaterial(), na.getInvestmentInventories());
        check("the accounts keep GDP = C + I + G + NX", identity(na));
    }

    static void keptBesideTheRodMill() {
        out.println("--- 2. kept: a full shed's profitable spare is still ordered and made, recovered copper still runs at the marginal cost ---");
        Game g = copperPair("pays", 12, 10.75, 10.25, true);
        CopperRefining cr = g.getSectors().copperRefining();
        GoodsMarket cathode = g.getMarkets().get(Good.COPPER);
        cr.setStock(Good.COPPER, cr.getStockCapacity(Good.COPPER) * Sector.DUMP_THRESHOLD);
        double spare = cr.madeAtRate(Good.COPPER) - cr.captiveUnits(Good.COPPER);
        check("premise: a shed at the dump line, and cathode's net export price over its anode",
                cathode.getDemand() == 0 && spare > 0 && cathode.netExportPrice() >= cr.flowCostPerUnit(Good.COPPER)
                && cathode.netExportPrice() >= cr.getMarginalCostPerUnit(Good.COPPER));
        cr.planOrders();
        double share = cr.orderShare(Good.COPPER), bid = cr.bid(Good.ANODE);
        cr.endOrders();
        check("a profitable spare is still ordered whole", share == 1
                && bid == cr.getInputAtCapacity(Good.ANODE) * cr.getOperatingRate());
        clear(g);
        Sector.Output cu = cr.outputRow(Good.COPPER);
        out.printf("cleared: captive %.6f, planned %.6f, produced %.6f, export-bound %.6f of spare %.6f, idled %.6f, exported %.6f, held %.6f%n",
                cu.captive, cu.planned, cu.produced, cu.exportBound, spare, cu.idled, cu.exported, cr.getStock(Good.COPPER));
        // The shed sells its stock at the profitable price, so the run refills it.
        check("the real clearing makes the whole profitable run to the bit", cu.produced + cu.exportBound == spare && cu.exported > 0);

        // Recovered copper is bought outside the goods market before any
        // cathode is made (CPB1's committed feed). The rod here is worth less
        // abroad than its cathode, so the rod mill idles - and still halves
        // cathode's share of the joint bill.
        Game r = copperPair("recovered", 9, 9.5, 9.0, false);
        BuildingsTemplate scrap = r.getBuildingManager().getTemplate(CopperRefining.SCRAP_REFINERY);
        build(r, scrap);
        GoodsMarket feed = r.getMarkets().get(Good.COPPER_SCRAP);
        feed.setWorldPrices(11, 10);
        feed.strike(0, 0, 0);
        CopperRefining rr = r.getSectors().copperRefining();
        double recovered = scrap.uses(Good.COPPER_SCRAP) * rr.getOperatingRate();
        r.getOldMetal().carsScrapped(recovered / OldMetal.COPPER_A_CAR / OldMetal.RECOVERY);
        GoodsMarket rc = r.getMarkets().get(Good.COPPER);
        double fromScrap = recovered * scrap.makes(Good.COPPER) / scrap.uses(Good.COPPER_SCRAP);
        out.printf("recovered %.6f t makes %.6f cathode; allocated marginal %.9f, net export %.9f, recipe %.9f%n",
                recovered, fromScrap, rr.getMarginalCostPerUnit(Good.COPPER), rc.netExportPrice(), rr.flowCostPerUnit(Good.COPPER));
        check("premise: no demand for cathode or rod, imported feed under water, the joint bill's share under the export price",
                rc.getDemand() == 0 && r.getMarkets().get(Good.COPPER_ROD).getDemand() == 0
                && rr.getMarginalCostPerUnit(Good.COPPER) <= rc.netExportPrice() && rc.netExportPrice() < rr.flowCostPerUnit(Good.COPPER));
        clear(r);
        Sector.Output made = rr.outputRow(Good.COPPER);
        out.printf("cleared: taken %.6f; captive %.6f, produced %.6f, export-bound %.6f; anode bid %.6f, scrap bid %.6f%n",
                rr.oldCopperTaken(), made.captive, made.produced, made.exportBound,
                rr.inputRow(Good.ANODE).bid, rr.inputRow(Good.COPPER_SCRAP).bid);
        check("the recovered copper paid for becomes cathode at the scrap recipe ratio, with no anode or scrap imported",
                near(rr.oldCopperTaken(), recovered) && near(made.captive + made.produced + made.exportBound, fromScrap)
                && rr.inputRow(Good.ANODE).bid == 0 && rr.inputRow(Good.ANODE).imported == 0
                && rr.inputRow(Good.COPPER_SCRAP).imported == 0);
        check("its cathode, the feed already sunk, still ships at the marginal cost rather than filling the shed",
                near(made.exportBound, fromScrap) && made.produced == 0 && rr.getStock(Good.COPPER) == 0);
        check("the accounts keep GDP = C + I + G + NX", identity(accounts(r)));
    }

    static void committedBesideTheRodMill() {
        out.println("--- 3. recovered copper committed beside a demanded rod mill: anode only for what the scrap does not cover ---");
        Game r = copperPair("committed-rod", 9, 10.75, 10.25, true);
        BuildingsTemplate scrap = r.getBuildingManager().getTemplate(CopperRefining.SCRAP_REFINERY);
        build(r, scrap);
        GoodsMarket feed = r.getMarkets().get(Good.COPPER_SCRAP);
        feed.setWorldPrices(11, 10);
        feed.strike(0, 0, 0);
        CopperRefining rr = r.getSectors().copperRefining();
        double recovered = scrap.uses(Good.COPPER_SCRAP) * rr.getOperatingRate();
        r.getOldMetal().carsScrapped(recovered / OldMetal.COPPER_A_CAR / OldMetal.RECOVERY);
        BuildingsTemplate works = r.getBuildingManager().getTemplate(CopperRefining.ELECTROLYTIC_REFINERY);
        double ratio = works.uses(Good.ANODE) / works.makes(Good.COPPER);
        double fromScrap = recovered * scrap.makes(Good.COPPER) / scrap.uses(Good.COPPER_SCRAP);
        double own = rr.captiveUnits(Good.COPPER);
        GoodsMarket cathode = r.getMarkets().get(Good.COPPER);
        out.printf("rod mill's cathode %.6f; the committed scrap makes %.6f; anode a cathode %.9f against a net export price of %.9f%n",
                own, fromScrap, rr.flowCostPerUnit(Good.COPPER), cathode.netExportPrice());
        check("premise: rod demanded, cathode not, the committed scrap covering part of the rod mill and imported feed under water",
                cathode.getDemand() == 0 && fromScrap > 0 && fromScrap < own && cathode.netExportPrice() < rr.flowCostPerUnit(Good.COPPER));
        clear(r);
        Sector.Output cu = rr.outputRow(Good.COPPER);
        Sector.Input anode = rr.inputRow(Good.ANODE);
        double made = cu.captive + cu.produced + cu.exportBound;
        out.printf("cleared: taken %.6f; captive %.6f, produced %.6f, export-bound %.6f; anode bid %.6f, bought %.6f%n",
                rr.oldCopperTaken(), cu.captive, cu.produced, cu.exportBound, anode.bid, anode.boughtLocal + anode.imported);
        check("all the recovered copper paid for is taken", near(rr.oldCopperTaken(), recovered));
        check("the anode refinery is ordered only for the rod mill's cathode the scrap does not make",
                near(anode.bid, (own - fromScrap) * ratio) && near(anode.boughtLocal + anode.imported, (own - fromScrap) * ratio));
        check("so the run makes the rod mill's cathode and none ships on imported anode below its cost",
                near(made, own) && near(cu.captive, own) && cu.exportBound < 1e-6 && cu.exported < 1e-6);
        check("the accounts keep GDP = C + I + G + NX", identity(accounts(r)));
    }

    /* ----- 4 and 5: the goods the world will not buy ----- */

    static void noWorldOutlet() {
        out.println("--- 4. every plant of a stockable good the world will not buy, with no demand here: no buffer, no feed ---");
        List<Good> goods = new ArrayList<>();
        for (Good g : Good.values()) if (g.stockable() && g.traded() && !g.exportable()) goods.add(g);
        out.println("stockable traded goods the world will not buy: " + goods);
        check("premise: vans, trucks, rolling stock and luxuries are among them",
                goods.contains(Good.VANS) && goods.contains(Good.TRUCKS) && goods.contains(Good.ROLLING_STOCK) && goods.contains(Good.LUXURIES));
        Game probe = city("templates");
        int plants = 0;
        for (Good good : goods) {
            List<BuildingsTemplate> makers = new ArrayList<>();
            for (BuildingsTemplate t : probe.getBuildingManager().getTemplates()) if (t.makes(good) > 0) makers.add(t);
            if (makers.isEmpty()) {
                // LUXURIES: the boutiques import them; nothing in the city makes one.
                boolean madeBySector = false;
                for (Sector s : probe.getSectors().all()) madeBySector |= s.goodsMade().contains(good);
                out.printf("%s: no plant makes it%n", good);
                check("premise: " + good + " has no plant and no sector making it, so no buffer of it can be ordered",
                        !madeBySector && Double.isNaN(probe.getMarkets().get(good).netExportPrice()));
                continue;
            }
            for (BuildingsTemplate t : makers) {
                plants++;
                Game g = city("none-" + t.getId());
                build(g, t);
                Sector s = g.getSectors().byKey(t.getSector());
                s.setCash(1e9);
                GoodsMarket m = g.getMarkets().get(good);
                m.strike(0, 0, 0);
                String name = t.getName() + " (" + good + ")";
                double cost = s.flowCostPerUnit(good);
                out.printf("%s: nameplate %.6f, room %.6f, net export %s, its inputs a unit %.9f%n",
                        name, s.madeAtRate(good), s.getStockCapacity(good), m.netExportPrice(), cost);
                check("premise: " + name + " stands empty, undemanded, with no world price and a feed that costs money",
                        s.madeAtRate(good) > 0 && s.getStockCapacity(good) > 0 && s.getStock(good) == 0
                        && m.getDemand() == 0 && Double.isNaN(m.netExportPrice()) && cost > 0);
                s.planOrders();
                double planned = s.getPlannedOutput(good), share = s.orderShare(good), bids = 0;
                for (Good in : s.goodsUsed()) if (in.traded() && in != Good.VANS) bids += s.bid(in);
                s.endOrders();
                out.printf("  plan %.6f; order share %.9f; feed bid %.6f%n", planned, share, bids);
                check(name + ": no undemanded buffer is planned or ordered, and no feed bid", planned == 0 && share == 0 && bids == 0);
                clear(g);
                Sector.Output o = s.outputRow(good);
                double fed = 0;
                for (Good in : s.goodsUsed()) {
                    if (!in.traded() || in == Good.VANS) continue;
                    Sector.Input row = s.inputRow(in);
                    fed += row.boughtLocal + row.imported;
                }
                out.printf("  cleared: produced %.6f, held %.6f, feed bought %.6f%n", o.produced, s.getStock(good), fed);
                check(name + ": the real clearing buys no feed and makes nothing to hold", fed == 0 && o.produced == 0 && s.getStock(good) == 0);
                NationalAccounts na = accounts(g);
                double other = s.statement().otherInputs.getOrDefault(Sector.CONSUMABLES, 0.0) + s.getFleetDieselImported();
                out.printf("  struck imports %.9f; consumables and fleet diesel %.9f%n", s.statement().imports, other);
                check(name + ": the struck sector imports only its consumables and its fleet's diesel, and GDP = C + I + G + NX",
                        near(s.statement().imports, other) && identity(na));
            }
        }
        out.printf("%d real plants tried%n", plants);
        check("vans, trucks and rolling stock each had a real plant tried", plants >= 3);
    }

    static Game motorTown(String label, String... plants) {
        Game g = city(label);
        for (String p : plants) build(g, g.getBuildingManager().getTemplateByName(p));
        g.getSectors().byKey(Sectors.AUTOMOTIVE).setCash(1e9);
        for (Good v : new Good[]{Good.CARS, Good.VANS, Good.TRUCKS, Good.ROLLING_STOCK}) g.getMarkets().get(v).strike(0, 0, 0);
        return g;
    }

    static void keptBesideTheIdlePlants() {
        out.println("--- 5. kept: demanded vans, profitable cars and vans already in the shed ---");
        Game g = motorTown("vans-wanted", "Commercial Vehicle Plant");
        Sector au = g.getSectors().byKey(Sectors.AUTOMOTIVE);
        GoodsMarket vans = g.getMarkets().get(Good.VANS);
        double wanted = au.madeAtRate(Good.VANS) / (2 * Sector.STOCK_MONTHS);
        vans.strike(0, 0, wanted);
        double cover = Math.min(au.madeAtRate(Good.VANS), wanted * Sector.STOCK_MONTHS);
        au.planOrders();
        double planned = au.getPlannedOutput(Good.VANS), machinery = au.bid(Good.MACHINERY);
        au.endOrders();
        check("demanded vans still plan their cover and order machinery, with no world price",
                Double.isNaN(vans.netExportPrice()) && near(planned, cover) && machinery > 0);
        clear(g);
        check("the real clearing makes that cover and holds it", near(au.outputRow(Good.VANS).produced, cover)
                && near(au.getStock(Good.VANS), cover));

        Game c = motorTown("cars", "Assembly Plant", "Locomotive Works");
        Sector cars = c.getSectors().byKey(Sectors.AUTOMOTIVE);
        GoodsMarket cm = c.getMarkets().get(Good.CARS);
        check("premise: cars undemanded but their net export price over their inputs, rolling stock undemanded with none",
                cm.getDemand() == 0 && cm.netExportPrice() >= cars.flowCostPerUnit(Good.CARS)
                && Double.isNaN(c.getMarkets().get(Good.ROLLING_STOCK).netExportPrice()));
        cars.planOrders();
        double carShare = cars.orderShare(Good.CARS), locoShare = cars.orderShare(Good.ROLLING_STOCK);
        cars.endOrders();
        out.printf("order shares: cars %.9f, rolling stock %.9f%n", carShare, locoShare);
        check("the profitable car buffer is ordered whole", carShare == 1);
        check("beside it the undemanded locomotive works, with no world price, orders nothing", locoShare == 0);
        clear(c);
        check("the real clearing makes the cars' whole buffer",
                cars.outputRow(Good.CARS).produced == Math.min(cars.madeAtRate(Good.CARS),
                        cars.getStockCapacity(Good.CARS) * Sector.DUMP_THRESHOLD));
        check("and no rolling stock", cars.outputRow(Good.ROLLING_STOCK).produced == 0);

        // Vans already in the shed, and a fleet that wants more: the first
        // clearing hands every sector its opening fleet, then a works opens.
        Game s = motorTown("vans-held", "Commercial Vehicle Plant");
        Sector maker = s.getSectors().byKey(Sectors.AUTOMOTIVE);
        clear(s);
        maker.setStock(Good.VANS, 4);
        double held = maker.getStock(Good.VANS);
        build(s, s.getBuildingManager().getTemplate(CopperRefining.ROD_MILL));
        CopperRefining fleet = s.getSectors().copperRefining();
        fleet.setCash(1e9);
        s.getMarkets().get(Good.VANS).strike(0, 0, 0);
        clear(s);
        double sold = maker.outputRow(Good.VANS).soldLocal;
        out.printf("held %.6f vans; the rod mill's fleet bid %.6f and got %.6f; sold from the shed %.6f; made %.6f%n",
                held, fleet.inputRow(Good.VANS).bid, fleet.inputRow(Good.VANS).boughtLocal, sold, maker.outputRow(Good.VANS).produced);
        check("premise: a real fleet bids for vans the month the shed holds them, with no demand at the last strike",
                fleet.inputRow(Good.VANS).bid > 0 && held > 0);
        check("vans already in the shed all sell to the fleets, that one among them",
                near(sold, held) && fleet.inputRow(Good.VANS).boughtLocal > 0);
        check("and with no demand at the last strike none are made or fed", maker.outputRow(Good.VANS).produced == 0 && maker.inputRow(Good.MACHINERY).bid == 0);
    }

    public static void main(String[] args) {
        allocatedSpare();
        keptBesideTheRodMill();
        committedBesideTheRodMill();
        noWorldOutlet();
        keptBesideTheIdlePlants();
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
