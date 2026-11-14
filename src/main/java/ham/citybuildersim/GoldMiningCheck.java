package ham.citybuildersim;

import ham.citybuildersim.sectors.GoldMining;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Gold Mining from geological fields through the mill to exports and the mint.
 *
 * WHY (GD4, spec-gold-money 4.4). The mint creates domestic reserves, so a
 * test must cause a mint purchase and close the money audit in a played city.
 *
 * WHAT THIS HAS TO PROVE
 * 1. Appended resource, good and sector, geology weights and units.
 * 2. The permanent building row, inputs, site and historical availability.
 * 3. Reproducible grades by geology and depletion at fixed ore throughput.
 * 4. A mill sells abroad, buys chemicals and depletes exactly its output.
 * 5. Under Gold the mint receives ounces and issues M into the miner's books.
 * 6. Depletion, vault and mint issuance survive their saved state round trips.
 * 7. A real city's months exercise the mechanic with a closing money audit.
 */
public final class GoldMiningCheck {
    static int fails;
    static final PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    static void assertTrue(String label, boolean ok) {
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
        if (!ok) fails++;
    }

    static void close(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) <= 1e-7 * Math.max(1, Math.abs(expected));
        out.printf("%-100s %s  %.8g / %.8g%n", label, ok ? "OK" : "FAIL", actual, expected);
        if (!ok) fails++;
    }

    static void quietly(Runnable work) {
        PrintStream was = System.out;
        try { System.setOut(quiet); work.run(); }
        finally { System.setOut(was); }
    }

    static Game town(String label) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> {
            g.newGame();
            LandManager land = g.getLandManager();
            land.setOwnedSqFt(land.getOwnedSqFt() + 400_000_000L);
            land.restoreSites(Resource.GOLD, 4, Resource.GOLD.amountPerSite() * 4);
            g.setCashForTest(Founding.WEALTHY_CASH);
            BuildingManager b = g.getBuildingManager();
            b.addStack(b.getTemplate(GoldMining.MINE_AND_MILL), 1, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
        });
        MaterialsCheck.handWorkers(g);
        return g;
    }

    static void registry() {
        out.println("--- 1. the appended resource, good and sector ---");
        Sectors sectors = new Sectors(new BuildingManager(), new Markets());
        assertTrue("gold is eighth after FOREST, Gold Mining twenty-fifth after Wood Products",
                Resource.GOLD.ordinal() == 7 && Resource.values()[6] == Resource.FOREST
                && Sectors.KEYS.length == 25 && Sectors.KEYS[24].equals(Sectors.GOLD_MINING)
                && sectors.all().get(24) == sectors.goldMining());
        assertTrue("Gold Mining is an extraction sector making fine stockable troy ounces",
                sectors.goldMining().resource() == Resource.GOLD && Resource.GOLD.good() == Good.GOLD
                && Good.GOLD.stockable() && Good.GOLD.unit().equals("troy ounce"));
        close("an ounce's freight mass is its fine-gold mass", Good.GOLD.tonnesPerUnit(),
                GoldPrice.GRAMS_PER_TROY_OUNCE / 1_000_000);
        double[] weights = { 0, 3, 3, .5, 0, .5, 1 };
        boolean same = true;
        for (Geology.Kind k : Geology.Kind.values()) same &= Geology.weight(Resource.GOLD, k) == weights[k.ordinal()];
        assertTrue("gold's class weights follow the USGS geological models in the brief", same);
    }

    static void row() {
        out.println("--- 2. the mine and mill row ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        BuildingsTemplate t = b.getTemplate(GoldMining.MINE_AND_MILL);
        assertTrue("the next permanent id is Gold Mine & Mill, one gold site, available in 1900",
                t != null && t.getName().equals("Gold Mine & Mill") && t.getSector().equals(Sectors.GOLD_MINING)
                && t.site().resource() == Resource.GOLD && t.site().sites() == 1 && t.getSince() == 1900);
        close("nameplate gold is ore milled times the reference grade, in ounces", t.makes(Good.GOLD),
                GoldMining.ORE_PER_MONTH * Deposit.GOLD_REFERENCE_GRADE / GoldPrice.GRAMS_PER_TROY_OUNCE);
        assertTrue("cyanide chemicals and diesel are ore-throughput inputs", t.uses(Good.ACID) > 0 && t.uses(Good.DIESEL) > 0);
        assertTrue("old rows stay permanent, including Engineering Research Lab at 148",
                b.getTemplate(148).getName().equals("Engineering Research Lab"));
    }

    static void grades() {
        out.println("--- 3. geology and the worked field ---");
        double shield = 0, basin = 0;
        boolean deterministic = true;
        for (int i = 0; i < 100; i++) {
            Deposit s = new Deposit(Resource.GOLD, 1, i, 0, 0, 1, Resource.GOLD.amountPerSite(), Geology.Kind.SHIELD);
            Deposit b = new Deposit(Resource.GOLD, 1, i, 0, 0, 1, Resource.GOLD.amountPerSite(), Geology.Kind.BASIN);
            shield += s.goldGrade();
            basin += b.goldGrade();
            deterministic &= s.goldGrade() == new Deposit(Resource.GOLD, 1, i, 0, 0, 1,
                    s.amount(), Geology.Kind.SHIELD).goldGrade();
        }
        assertTrue("one hundred field redraws have the same grades", deterministic);
        assertTrue("shield gold is richer than basin placer gold", shield > basin);
        Game g = town("goldminingcheck-grade");
        LandManager land = g.getLandManager();
        double throughput = 1000, first = land.goldOutput(throughput);
        double before = land.getExtracted(Resource.GOLD);
        land.extract(Resource.GOLD, land.getRemaining(Resource.GOLD) * .5);
        double later = land.goldOutput(throughput);
        assertTrue("the same ore throughput recovers fewer ounces as its field is worked", first > later && later > 0);
        double pure = land.getExtracted(Resource.GOLD);
        land.goldOutput(throughput);
        close("a production forecast leaves saved depletion alone", land.getExtracted(Resource.GOLD), pure);
        land.extract(Resource.GOLD, land.getRemaining(Resource.GOLD));
        close("a worked-out field recovers nothing", land.goldOutput(throughput), 0);
        assertTrue("depletion advanced from the fixture's initial state", pure > before);
    }

    static Game exported() {
        out.println("--- 4. a mill sells its gold abroad ---");
        Game g = town("goldminingcheck-export");
        quietly(() -> g.simulateMonths(3));
        GoldMining mine = g.getSectors().goldMining();
        Sector.Output o = mine.outputRow(Good.GOLD);
        assertTrue("the played mill made and exported gold", o != null && o.exported > 0);
        double before = g.getLandManager().getExtracted(Resource.GOLD);
        quietly(() -> g.simulateMonths(1));
        o = mine.outputRow(Good.GOLD);
        close("the ground gave up exactly the ounces produced", g.getLandManager().getExtracted(Resource.GOLD) - before,
                o.produced + o.exportBound);
        Sector.Split chemicals = mine.statement().bought.get(Good.ACID);
        assertTrue("the mill paid for imported cyanide chemicals", chemicals != null && chemicals.abroad > 0);
        GoodsMarket gold = g.getEconomyManager().getMarkets().get(Good.GOLD);
        close("the export band's nominal gold price follows GoldPrice and the spot rate",
                gold.exportPrice(), g.goldUsdPerOunce(WorldMoneys.monthIndex(g.getMonth()))
                        * .995 * gold.getExchangeRate() / 1000);
        return g;
    }

    static void minted(Game g) {
        out.println("--- 5. the mint makes reserves at home ---");
        ForeignAccounts fa = g.getForeignAccounts();
        fa.buyReserves(1_000_000_000, "XAU");
        fa.setRegime(ForeignAccounts.Regime.GOLD);
        quietly(() -> g.simulateMonths(1));
        SectorState offer = g.getSectors().goldMining().toState();
        double ounces = offer.extras.get("mintOunces"), payment = offer.extras.get("mintPayment");
        close("the accepted offer is valued at M", payment, ounces * fa.getMintPrice() / 1000);
        quietly(() -> g.simulateMonths(1));
        GoldMining mine = g.getSectors().goldMining();
        Sector.Output o = mine.outputRow(Good.GOLD);
        assertTrue("Gold stayed in force and the mint took fine ounces", fa.getRegime() == ForeignAccounts.Regime.GOLD
                && g.getCentralBank().getGoldMinted() > 0 && ounces > 0 && o.soldLocal > 0);
        close("the mint paid M for all the ounces offered", g.getCentralBank().getGoldMinted(), payment);
        ForeignAccounts isolated = new ForeignAccounts();
        isolated.restore(fa.toSaveArray());
        double vaultBefore = isolated.toSaveArray()[149];
        isolated.receiveMinedGold(ounces);
        close("the settled fine ounces enter the vault", isolated.toSaveArray()[149] - vaultBefore, ounces / 1000);
        close("the mint's purchase is a domestic receipt", mine.statement().localSales, g.getCentralBank().getGoldMinted());
        close("the mint's metal was kept off the export ledger", o.exported, 0);
        close("a mint purchase uses no treasury reserve-purchase flow", fa.getBoughtThisMonth(), 0);
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        assertTrue("the actual mint month closes MoneyAudit", audit != null && audit.relative() < 1e-4);
    }

    static void saved(Game g) {
        out.println("--- 6. the saved state carries depletion, vault and mint payment ---");
        Markets markets = new Markets();
        markets.setExchangeRate(123);
        markets.restore(g.getEconomyManager().getMarkets().toState());
        close("gold's saved spot quote survives restore separately from ordinary goods",
                markets.get(Good.GOLD).getExchangeRate(),
                g.getEconomyManager().getMarkets().get(Good.GOLD).getExchangeRate());
        close("ordinary goods retain the handed exchange factor", markets.get(Good.ACID).getExchangeRate(), 123);
        CentralBank cb = new CentralBank();
        cb.restore(g.getCentralBank().toSaveArray());
        close("the saved month's domestic mint payment survives restore", cb.getGoldMinted(), g.getCentralBank().getGoldMinted());
        close("the lifetime mint payment survives restore", cb.getGoldMintedLifetime(), g.getCentralBank().getGoldMintedLifetime());
        GoldMining restored = new GoldMining();
        restored.restore(g.getSectors().goldMining().toState());
        close("the unsettled mint offer survives sector restore", restored.toState().extras.get("mintPayment"),
                g.getSectors().goldMining().toState().extras.get("mintPayment"));
        ForeignAccounts fa = new ForeignAccounts();
        fa.restore(g.getForeignAccounts().toSaveArray());
        close("the vault's minted ounces survive restore", fa.toSaveArray()[149], g.getForeignAccounts().toSaveArray()[149]);
        assertTrue("the resource row is widened and old shapes are refused", CityLand.KINDS == Resource.values().length
                && GameVersion.OLDEST_LOADABLE_FORMAT == GameVersion.SAVE_FORMAT
                && GameVersion.isNoLongerReadable(GameVersion.SAVE_FORMAT - 1));
    }

    static void played(Game g) {
        out.println("--- 7. the mint operates through real months ---");
        int minted = 0, audits = 0;
        for (int i = 0; i < 24; i++) {
            quietly(() -> g.simulateMonths(1));
            if (g.getCentralBank().getGoldMinted() > 0) minted++;
            MoneyAudit.Result a = g.getLastMoneyAudit();
            if (a != null && a.relative() < 1e-4) audits++;
        }
        assertTrue("the mechanic fired in the played run (" + minted + " mint months)", minted > 0);
        assertTrue("all twenty-four money audits close", audits == 24);
        double owed = g.getSectors().goldMining().toState().extras.get("mintPayment");
        g.getForeignAccounts().setRegime(ForeignAccounts.Regime.FLOAT);
        quietly(() -> g.simulateMonths(1));
        close("leaving Gold still pays the already accepted offer", g.getCentralBank().getGoldMinted(), owed);
        MoneyAudit.Result a = g.getLastMoneyAudit();
        assertTrue("the transition's money audit closes", a != null && a.relative() < 1e-4);
        quietly(() -> g.simulateMonths(1));
        close("after that settlement Float makes no mint payment", g.getCentralBank().getGoldMinted(), 0);
    }

    public static void main(String[] args) {
        registry();
        row();
        grades();
        Game g = exported();
        minted(g);
        saved(g);
        played(g);
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
