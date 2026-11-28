package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Four findings from Jerus's Ashbourne (slot 2, months 193-241), each caused on a fixture of this harness's own (ASH4;
 * docs/queue/ASH-findings.md, docs/queue/ASH4-notes.md).
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. FINDING 5 - THE HOUSEHOLDS' DEPOSITS INTO ONE BOND (a mend). In Ashbourne six owner families held 70% of the households'
 *      deposits with a take-home of 0 (OwnerHousehold.earningWeight() is 0: they live on dividends), so the old cushion (three months
 *      of take-home) was 0 and one 30-year bond took 91% of the deposits. The cushion is now three months of what a household
 *      takes home or plans to spend, the greater (HouseholdBalance.cushion()): a saver's is what it was, the owners' is no longer
 *      nothing, and no cell pays a bond out of its cushion, through the production sale and the production settlement.
 *
 *   2. FINDING 12 - THE YEN BOND BOUGHT BACK AT 27.37 (no mend: the price is the yen curve's; the yen RATE is the stand-in
 *      century's, which EP1b retires). The piece is booked in yen, valued on the yen's curve at the yen's rate plus the country
 *      premium once, its flows and its principal convert at one rate (so a move of the D$ does not move its price per 100), and
 *      the level of that price is the rate's alone: 13% (the stand-in's January 1917) prices a 3.5% bond of 312 months where
 *      Ashbourne saw it, 5.11% (the Bank of Japan's discount rate of 1917) prices it over 60.
 *
 *   3. FINDING 11 - THE LOAD FELL WHILE THE TOWN GREW (real: two Lime Kilns closed; the load is the buildings' draw, one unit).
 *      The city's draw is the sum of its buildings' powerBought(), in kW in both months; and the struck price on the founding grid
 *      alone is the scarcity price on the half-blocks the wires leave unserved, so 13% less load is 77% less price when it
 *      brings a block under the wires.
 *
 *   4. FINDING 16 - HOUSEHOLDS MERGE 8% WHEN A BOOM ENDS (the cause found; the mend is a queued batch, HF1: the families keep
 *      their out-of-work adults). The families are built from the adults who WORK, so a laid-off adult leaves the family the
 *      month the job goes and becomes a one-adult household looking for a door: the whole move comes in one month at full pace,
 *      and comes back in one when the jobs do.
 *
 * Every fixture CAUSES its condition; every figure is read off the model's own getters and constants.
 */
public class AshbourneCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-72s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-72s %s  %,.9f against %,.9f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static BuildingsTemplate template(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        if (t == null) throw new IllegalStateException("no template named " + name);
        return t;
    }

    /** AuctionCheck's lineup city, this harness's own copy: given the money and the ground, a bank and three hundred houses, sixty months played. */
    static Game town() {
        Game city = new Game(GameFiles.scratch("ashbournecheck"));
        quietly(() -> {
            city.run();
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 50_000_000L);
            city.getForeignAccounts().pinRate(1.0);
            city.buildStack(template(city, "Gravel Road"), 6, true);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Commercial Bank"), 1, false);
            city.simulateMonths(60);
        });
        return city;
    }

    /** The production settlement of the households' part of a sale (Game.householdsTakeTheirShare, private), what it took from them. */
    static double settle(Game g) throws Exception {
        java.lang.reflect.Method settle = Game.class.getDeclaredMethod("householdsTakeTheirShare");
        settle.setAccessible(true);
        return (Double) settle.invoke(g);
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(OutputStream.nullOutputStream());

        Game g = town();
        section1(g);
        section2();
        section3(g);
        section4();

        out.println();
        if (fails == 0) {
            out.println("Ashbourne's four findings, each on a fixture of its own, hold.");
        } else {
            out.println(fails + " check(s) failed.");
            System.exit(1);
        }
    }

    /* ============== 1. finding 5: the households' deposits into one bond ============== */
    static void section1(Game g) throws Exception {
        out.println("--- 1. finding 5: the owners' deposits are not spare, whatever they take home ---");
        HouseholdBalance hb = g.getHouseholdBalance();
        OwnerHousehold owners = hb.owners();
        double cushion = HouseholdBalance.cushion(owners);
        out.printf("   m%d: owners %,.2f households, savings %,.2f each, disposable %,.2f, planned %,.2f, cushion %,.2f%n",
                g.getMonth(), owners.households, owners.savings, owners.disposable, owners.planned, cushion);

        assertTrue("fixture: owner families, who take home nothing and plan to spend something",
                owners.households > 0 && owners.disposable == 0 && owners.planned > 0);
        assertTrue("fixture: and hold more than their cushion", owners.savings > cushion);

        close("the owners' cushion is the months' cushion of what they plan to spend", cushion,
                HouseholdBalance.SHARE_CUSHION_MONTHS * owners.planned, 0);
        int savers = 0;
        boolean saversAsBefore = true, neverLess = true;
        for (Household c : hb.cellsForMarket()) {
            double takeHome = HouseholdBalance.SHARE_CUSHION_MONTHS * Math.max(0, c.disposable);
            neverLess &= HouseholdBalance.cushion(c) >= takeHome;
            if (c.disposable >= c.planned) {
                savers++;
                saversAsBefore &= HouseholdBalance.cushion(c) == takeHome;
            }
        }
        out.printf("   %d of %d cells take home at least what they plan to spend%n", savers, hb.cellsForMarket().length);
        assertTrue("fixture: there are savers to hold the old rule against", savers > 0);
        assertTrue("a saver's cushion is what it was: the months' cushion of its take-home, exactly", saversAsBefore);
        assertTrue("no cell's cushion is under the months' cushion of its take-home", neverLess);

        double takeHomeAlone = HouseholdBalance.SHARE_CUSHION_MONTHS * Math.max(0, owners.disposable);
        double spare = hb.bondSpare(owners);
        double ceiling = (owners.savings - cushion) * owners.households;
        out.printf("   take-home alone: cushion %,.2f of %,.2f deposits; the owners' spare for a bond %,.2f of %,.2f%n",
                takeHomeAlone, owners.savings, spare / owners.households, owners.savings);
        assertTrue("take-home alone would have left the owners no cushion at all", takeHomeAlone == 0);
        assertTrue("fixture: the owners may buy paper, and have something spare", spare > 0);
        assertTrue("their spare for a bond is at most what is past the cushion, in all", spare <= ceiling * (1 + 1e-9));

        /* THE SALE, through the production path: a 30-year bond big enough that the households' bid is capped by their spare. */
        Household[] cells = hb.cellsForMarket();
        double[] before = new double[cells.length], cushions = new double[cells.length];
        for (int i = 0; i < cells.length; i++) { before[i] = cells[i].savings; cushions[i] = HouseholdBalance.cushion(cells[i]); }
        double spareAll = hb.spareForPaper();
        double ask = 4 * spareAll;
        double ownersBefore = owners.savings;
        quietly(() -> g.handleLongBondLogic(ask, 30, 100 * PriceLevel.FACTOR));
        Debt bond = g.getDebtManager().benchmarkLine(30);
        assertTrue("fixture: the 30-year bond is on the books", bond instanceof LongTermBond && bond.getDuration() == 360);
        double allotFace = bond.allotHouseholdsFace(), allotCash = bond.allotHouseholdsCash();
        out.printf("   the ask $%,.0fk is four times the households' spare $%,.0fk; the households are allotted face %,.0fk for %,.0fk%n",
                ask, spareAll, allotFace, allotCash);
        assertTrue("the households were allotted face", allotFace > 0 && allotCash > 0);
        assertTrue("...for no more than their spare", allotCash <= spareAll * (1 + 1e-9));

        double paid = settle(g);
        double owed = 0;
        boolean cushionsKept = true;
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            double floor = Math.min(before[i], cushions[i]) - 1e-9 * Math.max(1, before[i]);
            cushionsKept &= c.savings >= floor;
            owed += (before[i] - c.savings) * c.households;
        }
        out.printf("   the owners' savings %,.2f -> %,.2f (cushion %,.2f); the households paid %,.0fk of the %,.0fk allotted%n",
                ownersBefore, owners.savings, cushion, paid, allotCash);
        double deposits = 0;
        for (int i = 0; i < cells.length; i++) deposits += before[i] * cells[i].households;
        out.printf("   the owners held %.1f%% of the households' deposits and paid %.1f%% of what the households paid%n",
                100 * ownersBefore * owners.households / deposits, 100 * (ownersBefore - owners.savings) * owners.households / paid);
        assertTrue("no cell paid out of its cushion: each ends at least at the lesser of what it held and its cushion", cushionsKept);
        assertTrue("the owners paid something", owners.savings < ownersBefore);
        assertTrue("...and kept at least their cushion", owners.savings >= Math.min(ownersBefore, cushion) - 1e-9 * Math.max(1, ownersBefore));
        assertTrue("what the cells paid, in all, is what the settlement took, to the cent", Math.abs(owed - paid) <= MoneyAudit.CENT);

        quietly(() -> g.simulateMonths(1));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        out.printf("   the month after: moved %,.0fk, residual %,.6fk%n", audit.moved(), audit.residual);
        assertTrue("the month's money audit closes to the cent", Math.abs(audit.residual) <= MoneyAudit.CENT);
    }

    /* ============== 2. finding 12: the yen bond bought back at 27.37 ============== */

    /** A bond of this face paying this coupon a year, monthly, for this many months and the face with the last: the shape Debt.remainingCashFlows() returns. */
    static double[] bullet(double face, double couponAYear, int months) {
        double[] flows = new double[months];
        for (int m = 0; m < months; m++) flows[m] = face * couponAYear / 12;
        flows[months - 1] += face;
        return flows;
    }

    /** The D$ keeps this much of its worth in US$ in the fall section 2 moves it by (DebtMoneysCheck.KEEPS's, 30% rather than 10%). */
    static final double KEEPS_30 = 0.70;

    /** Ashbourne's yen policy rate in January 1917, the stand-in century's (worldMoney.policyJPY in its history). */
    static final double STAND_IN_YEN = .13;

    /** The Bank of Japan's discount rate of 1917, 1.4 sen a day (the US: New York Fed 3.75% in 1917, FRED M13009USM156NNBR). */
    static final double BOJ_1917 = .0511;

    /** What Ashbourne's buyback paid, per 100 (docs/queue/ASH-findings.md, 12). */
    static final double ASHBOURNE_PER_100 = 27.37;

    static void section2() {
        out.println("--- 2. finding 12: the yen bond is the yen curve's, and its level is the rate's ---");
        DebtMoneysCheck.out = out;
        DebtMoneysCheck.quiet = quiet;
        Game w = DebtMoneysCheck.town("ashbourne-yen", false);
        DebtManager dm = w.getDebtManager();
        ForeignAccounts fx = w.getForeignAccounts();
        DebtMoneysCheck.open(w);
        assertTrue("fixture: the world's moneys, the yen offered, and nothing owed abroad (the premium is nothing)",
                w.getWorldMoneys() != null && dm.offers("JPY") && dm.getForeignPrincipal() == 0 && dm.countryPremium() == 0);

        /* THE LEVEL IS THE RATE'S: 100 yen, 3.5% a year, 312 months, bullet, priced on the yen's curve at the rate. */
        double[] flows = bullet(100, .035, 312);
        double[] books = new LongTermBond(100, 312, 0, .035).remainingCashFlows();
        boolean sameShape = books.length == flows.length;
        for (int i = 0; sameShape && i < flows.length; i++) sameShape = Math.abs(books[i] - flows[i]) <= 1e-12 * Math.max(1, flows[i]);
        assertTrue("the flows built here are the shape a piece of the books returns", sameShape);

        double[] handed = new double[WorldMoneys.EIGHT.size()];
        for (int i = 0; i < handed.length; i++) handed[i] = dm.worldRate(WorldMoneys.EIGHT.get(i));
        double standing = handed[WorldMoneys.EIGHT.indexOf("JPY")];
        double[] standIn = handed.clone(), boj = handed.clone();
        standIn[WorldMoneys.EIGHT.indexOf("JPY")] = STAND_IN_YEN;
        boj[WorldMoneys.EIGHT.indexOf("JPY")] = BOJ_1917;
        double premium = dm.countryPremium();
        dm.setMoneyRates(standIn);
        double at13 = dm.worldCurveAt("JPY", dm.foreignRate("JPY")).pv(flows);
        dm.setMoneyRates(boj);
        double at511 = dm.worldCurveAt("JPY", dm.foreignRate("JPY")).pv(flows);
        dm.setMoneyRates(handed);
        out.printf("   100 yen, 3.5%%, 312 months, premium %.4f: %.2f at the stand-in's %.2f%%, %.2f at the Bank of Japan's %.2f%% (Ashbourne's buyback: %.2f)%n",
                premium, at13, 100 * STAND_IN_YEN, at511, 100 * BOJ_1917, ASHBOURNE_PER_100);
        if (premium == 0) {
            assertTrue("at the stand-in's 13% the price is Ashbourne's 27.37, to within 3 points", Math.abs(at13 - ASHBOURNE_PER_100) <= 3);
        } else {
            assertTrue("at the stand-in's 13% and the premium the price is under 35", at13 < 35);
        }
        assertTrue("at the Bank of Japan's 5.11% the same bond is worth over 60", at511 > 60);
        assertTrue("...so the per-100 is the rate's, and nothing in the conversion", at13 < ASHBOURNE_PER_100 + 3 && at511 > at13 + 30);

        /* THE SALE: a 30-year bond in yen. */
        Set<Debt> had = new HashSet<>(DebtMoneysCheck.foreignPieces(w));
        DebtMoneysCheck.sell(w, "Term", "JPY", DebtMoneysCheck.askIn(w, "JPY"), 30, false);
        Debt piece = DebtMoneysCheck.newest(w, had);
        assertTrue("fixture: the piece is on the books, foreign, in yen",
                piece != null && dm.getDebt().contains(piece) && piece.isForeign() && "JPY".equals(piece.getMoney()));
        if (piece == null) return;

        double principal = piece.getOustandingPrincipal();
        double rate = dm.foreignRate("JPY");
        close("the yen's rate is its bank's plus the country premium, once", rate, dm.worldRate("JPY") + dm.countryPremium(), 0);
        Buyback.Result quote = w.quoteBuybackAtRest(piece, principal, Double.NaN);
        double fair = quote.fairPer100();
        double byMarketValue = dm.marketValue(piece) / principal * 100;
        double byCurve = dm.worldCurveAt("JPY", rate).pv(dm.pricingFlows(piece)) / principal * 100;
        out.printf("   the piece: face %,.0fk, yen rate %.4f%% (premium %.4f%%), fair %.4f per 100, bought back at %.4f%n",
                principal, 100 * rate, 100 * dm.countryPremium(), fair, quote.averagePer100());
        close("the buyback's fair per 100 is the market value over the principal", fair, byMarketValue, 1e-12);
        close("...and the yen curve at the yen's rate pricing the flows", fair, byCurve, 1e-12);
        close("...which the world's offer is paid at, the whole piece", quote.averagePer100(), fair, 1e-9);

        /* THE CONVERSION: the D$ 30% weaker; the flows and the principal convert at one rate. */
        YieldCurve atRate = dm.worldCurveAt("JPY", rate);
        double[] flowsThen = dm.pricingFlows(piece);
        double fixedThen = atRate.pv(flowsThen) / principal * 100;
        double piecesRateThen = piece.getExchangeRate(), d$Then = fx.getRate(), premiumThen = dm.countryPremium();
        fx.pinRate(d$Then / KEEPS_30);
        dm.revalueForeign(fx.getRate());
        double d$Move = fx.getRate() / d$Then;
        double principalNow = piece.getOustandingPrincipal();
        double fixedNow = atRate.pv(dm.pricingFlows(piece)) / principalNow * 100;
        double fairNow = dm.marketValue(piece) / principalNow * 100;
        out.printf("   the D$ x%.4f: principal %,.0fk -> %,.0fk, fair at the unmoved rate %.6f -> %.6f; premium %.4f%% -> %.4f%%, fair %.4f -> %.4f%n",
                d$Move, principal, principalNow, fixedThen, fixedNow, 100 * premiumThen, 100 * dm.countryPremium(), fair, fairNow);
        close("the D$ moved by the fall the fixture meant", d$Move, 1 / KEEPS_30, 1e-12);
        close("the principal in D$ moved by the cross's ratio", principalNow / principal, d$Move, 1e-9);
        close("...which is the piece's own rate's", principalNow / principal, piece.getExchangeRate() / piecesRateThen, 1e-9);
        close("the fair per 100 at the same rate did not move: flows and principal convert at one rate", fixedNow, fixedThen, 1e-9);
        close("...and what the price did move by is the premium's alone",
                fairNow, dm.worldCurveAt("JPY", dm.foreignRate("JPY")).pv(dm.pricingFlows(piece)) / principalNow * 100, 1e-12);

        /* THE MONTH: the stand-in century's. */
        int jan1917 = (1917 - Nations.FIRST_YEAR) * 12;
        out.printf("   January 1917 is month %d from January %d (Ashbourne's month %d); PolicyRates' table starts at month %d%n",
                jan1917, Nations.FIRST_YEAR, jan1917 + 1, PolicyRates.TABLE_FIRST_MONTH);
        assertTrue("Ashbourne's month 205 is January 1917, and it is before PolicyRates' table start: the rate is the stand-in's",
                jan1917 + 1 == 205 && jan1917 < PolicyRates.TABLE_FIRST_MONTH);
    }

    /* ============== 3. finding 11: the load fell while the town grew ============== */

    /** The draw Ashbourne's dispatch read in month 193, kW: its underlying draw before the losses and the season. */
    static final double DRAW_193 = 9_354;

    /** ...and in month 205, after the two Lime Kilns closed. */
    static final double DRAW_205 = 8_146;

    /** How many blocks of the month's three the wires leave short at this draw, off the dispatch's own half-blocks. */
    static int blocksOver(PowerDispatch.Month m, double wires) {
        int n = 0;
        for (int b = 0; b < PowerDispatch.BLOCKS; b++) if (m.load[2 * b] > wires) n++;
        return n;
    }

    static PowerDispatch.Month struck(double draw, double season, double wires) {
        List<PowerDispatch.Row> rows = List.of(PowerDispatch.Row.fuelled("grid", wires, 0, null, 0, 0));
        return PowerDispatch.dispatch(rows, draw, season, PowerDispatch.VOLL_A_KWH, wires);
    }

    static void section3(Game g) {
        out.println("--- 3. finding 11: the load follows the buildings, and the price follows the wires ---");
        BuildingManager bm = g.getBuildingManager();
        ServicesManager services = g.getServicesManager();
        UtilitiesHandler grid = services.getUtilitiesHandler();
        BuildingsTemplate kiln = template(g, "Lime Kiln"), house = template(g, "House");
        double[] draw = new double[1];
        Runnable read = () -> {
            quietly(services::updateServices);
            draw[0] = grid.getConsumption();
        };

        read.run();
        double d0 = draw[0];
        quietly(() -> bm.addStack(kiln, 2, true));
        read.run();
        double d1 = draw[0];
        long houses = bm.getQuantity(house.getId());
        long added = Math.round(.19 * houses);
        quietly(() -> {
            bm.retire(kiln, 2);
            bm.addStack(house, added, true);
        });
        read.run();
        double d2 = draw[0];
        out.printf("   kiln %,.0f kW, house %,.0f kW; the draw %,.1f kW, with two kilns %,.1f kW, with %d more houses and no kilns %,.1f kW%n",
                kiln.powerBought(), house.powerBought(), d0, d1, added, d2);
        assertTrue("fixture: the kilns stood and were taken down, and the houses rose by 19%", bm.getQuantity(kiln.getId()) == 0 && added > 0 && 100 * added >= 18 * houses);
        close("two kilns standing draw two kilns' powerBought", d1 - d0, 2 * kiln.powerBought(), 1e-9);
        close("the draw moved by the kilns lost and the houses gained, each at its own powerBought", d2 - d1,
                -2 * kiln.powerBought() + added * house.powerBought(), 1e-9);
        assertTrue("the load follows the buildings, in kW in both months: it fell while the houses rose",
                d2 < d1 && added * house.powerBought() > 0 && 2 * kiln.powerBought() > added * house.powerBought());

        /* THE STRUCK PRICE: the founding grid alone, its wires carrying all of it. */
        double wires = grid.getFoundingGridKw();
        double season = PowerDispatch.season(1);
        double voll = PowerDispatch.VOLL_A_KWH;
        assertTrue("fixture: the founding grid is the 10,000 kW the wires carry", wires == 10_000);
        PowerDispatch.Month own = struck(d2, season, wires);
        boolean none = true;
        for (int s = 0; s < PowerDispatch.HALVES; s++) none &= own.load[s] <= wires && own.price[s] == 0 && own.unserved[s] == 0;
        out.printf("   this town's %,.1f kW: peak half-block load %,.1f kW under the wires, struck price %.6f%n",
                d2, own.load[PowerDispatch.HALVES - 1], own.energyPrice);
        assertTrue("every block's load is under the wires, and the price is 0", none && own.energyPrice == 0);

        /* The draws at which the peak, then the shoulder, then the base block pass the wires, off the dispatch's own constants. */
        double[] passes = new double[PowerDispatch.BLOCKS];
        for (int b = 0; b < PowerDispatch.BLOCKS; b++) passes[b] = wires * (1 - PowerDispatch.GRID_LOSSES) / (season * PowerDispatch.BLOCK_LOAD[b]);
        double[] prices = new double[PowerDispatch.BLOCKS + 1];
        int[] over = new int[PowerDispatch.BLOCKS + 1];
        double[] draws = {
            passes[PowerDispatch.PEAK] * .98,
            (passes[PowerDispatch.PEAK] + passes[PowerDispatch.SHOULDER]) / 2,
            (passes[PowerDispatch.SHOULDER] + passes[PowerDispatch.BASE]) / 2,
            passes[PowerDispatch.BASE] * 1.02};
        boolean scarcity = true;
        for (int i = 0; i < draws.length; i++) {
            PowerDispatch.Month m = struck(draws[i], season, wires);
            prices[i] = m.energyPrice;
            over[i] = blocksOver(m, wires);
            for (int s = 0; s < PowerDispatch.HALVES; s++) scarcity &= m.price[s] == (m.unserved[s] > 0 ? voll : 0);
            out.printf("   at %,.0f kW: %d block(s) over the wires, struck price %.6f%n", draws[i], over[i], prices[i]);
        }
        assertTrue("with no block over the wires the price is 0; with the peak, the shoulder or the base over it is positive",
                over[0] == 0 && prices[0] == 0 && over[1] == 1 && over[2] == 2 && over[3] == 3 && prices[1] > 0);
        assertTrue("...and higher the more blocks are over", prices[1] < prices[2] && prices[2] < prices[3]);
        assertTrue("the price is the scarcity price: VOLL on the half-blocks the wires leave unserved, 0 on the rest", scarcity);

        PowerDispatch.Month before = struck(DRAW_193, season, wires), after = struck(DRAW_205, season, wires);
        double loadFell = 1 - DRAW_205 / DRAW_193, priceFell = 1 - after.energyPrice / before.energyPrice;
        out.printf("   Ashbourne's draws, January: %,.0f kW has %d block(s) over, price %.6f; %,.0f kW has %d, price %.6f; the load fell %.1f%%, the price %.1f%%%n",
                DRAW_193, blocksOver(before, wires), before.energyPrice, DRAW_205, blocksOver(after, wires), after.energyPrice,
                100 * loadFell, 100 * priceFell);
        assertTrue("month 193's draw leaves two blocks over the wires and month 205's one",
                blocksOver(before, wires) == 2 && blocksOver(after, wires) == 1);
        assertTrue("...so the price fell by a good deal more than the load did", priceFell > 3 * loadFell && after.energyPrice > 0);
    }

    /* ============== 4. finding 16: households merge when a boom ends ============== */

    static double[] jobs(double unskilled) {
        double[] jobs = new double[PayTier.values().length];
        jobs[PayTier.UNSKILLED.ordinal()] = unskilled;
        jobs[PayTier.SKILLED.ordinal()] = 900;
        jobs[jobs.length - 1] = 300;
        return jobs;
    }

    static double sum(double[] a) {
        double t = 0;
        for (double v : a) t += v;
        return t;
    }

    /** The adults the families hold: every shape's households times the adults in one. */
    static double familyAdults(FamilyModel f) {
        double adults = 0;
        for (FamilyStructure shape : FamilyStructure.values()) adults += f.totalOf(shape) * shape.membersOf(AgeBand.ADULT);
        return adults;
    }

    /** The month the boom ends, the months the jobs stay gone and the month they are back. */
    static final int END = 12, BACK = 20, MONTHS = 24;

    /** Adults of the town out of work the months the boom is off: 400 unskilled jobs gone, 16% of the town's 2,500. */
    static final double LAID = 400;

    static void section4() {
        out.println("--- 4. finding 16: a lost job is a household split, at full pace ---");
        PopulationCohorts cohorts = HouseholdMemoryCheck.pyramid(600, 1100, 650, 4300, 1000);
        FamilyModel f = new FamilyModel();
        f.rememberHouseholds(true);
        double[] families = new double[MONTHS + 1], seeking = new double[MONTHS + 1], jobCount = new double[MONTHS + 1];
        double[] adultsIn = new double[MONTHS + 1], adultsOut = new double[MONTHS + 1];
        double people = cohorts.get(AgeBand.ADULT);
        boolean samePeople = true;
        for (int m = 0; m <= MONTHS; m++) {
            double laid = m >= END && m < BACK ? LAID : 0;
            double[] jobs = jobs(1300 - laid);
            double outside = 300 + laid;
            f.rebuild(cohorts, jobs, 0, outside, 0);
            f.setSeekers(outside, 0);
            samePeople &= cohorts.get(AgeBand.ADULT) == people;
            families[m] = f.totalHouseholds();
            seeking[m] = f.householdsSeekingDoors();
            jobCount[m] = sum(jobs);
            adultsIn[m] = familyAdults(f);
            adultsOut[m] = f.getOutsideAdults();
        }
        for (int m : new int[] {END - 1, END, END + 1, BACK - 1, BACK}) {
            out.printf("   month %2d: jobs %,.0f, families %,.1f (%+.2f%%), households seeking a door %,.1f%n",
                    m, jobCount[m], families[m], 100 * (families[m] / families[m - 1] - 1), seeking[m]);
        }

        assertTrue("fixture: the same people every month", samePeople);
        assertTrue("...and the jobs fell 16% at month 12", Math.abs(1 - jobCount[END] / jobCount[END - 1] - .16) < 1e-9);
        assertTrue("the condition: the households fall at least 5% in the month the jobs go", families[END] <= .95 * families[END - 1]);

        boolean still = true;
        for (int m = END + 1; m < BACK; m++) still &= Math.abs(families[m] / families[m - 1] - 1) < .001;
        assertTrue("the whole move came in the one month: months 13-19 move the households under 0.1% a month (no pace)", still);
        assertTrue("...and when the jobs return the households rise at least 5% in the one month", families[BACK] >= 1.05 * families[BACK - 1]);

        assertTrue("the households looking for a door ROSE in month 12: a lost job is a household split, "
                + "the defect HF1 mends, the opposite of real headship, which moves over years", seeking[END] > seeking[END - 1]);

        double worst = 0;
        for (int m = 0; m <= MONTHS; m++) worst = Math.max(worst, Math.abs(adultsIn[m] + adultsOut[m] - people) / people);
        close("nobody is lost in month 12: the families' adults and the outside adults are the cohorts'", adultsIn[END] + adultsOut[END], people, 1e-6);
        close("...in every month of the run (the worst, relative)", worst, 0, 1e-6);
        assertTrue("...and the adults outside the families rose by the laid-off at month 12: they left the families the month the jobs went",
                Math.abs((adultsOut[END] - adultsOut[END - 1]) - LAID) < 1e-6 && adultsIn[END] < adultsIn[END - 1]);
    }
}
