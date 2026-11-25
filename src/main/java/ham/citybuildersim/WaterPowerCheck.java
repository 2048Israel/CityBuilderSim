package ham.citybuildersim;

import java.util.Locale;

/**
 * The water priced from its cost, as PW1 prices power (PW1-W, 2026-11-23; Jerus's Q1 for power: "Market price"), with the licence's
 * power read from the dispatch (PW1-L) and a recipe's own power weighed at the prices the strike pays it (PW1-G), through the game's
 * own month. PublicLicenceCheck holds the patented desalination plant that earns its licence; this holds the price, the bills and the
 * save on both sides of each edge.
 *
 * WHY: the water works drew their power billed to nobody and the water sold at a constant tariff, so once PW1 priced power at the
 * market a Desalination Plant's water no longer covered its power (REV-PW1c); a licence's power was the flat rule's output over demand,
 * which the dispatch no longer serves; and a works' surplus power was weighed at its class's price, which the strike never pays it.
 *
 * What this has to prove:
 *   1. A TOWN IN SURPLUS PRICES ITS WATER AT ITS TARIFF: the founding wells serve the demand with a desalination plant standing, so
 *      the sea delivers nothing and the price is the tariff exactly, which pays the land's works' power; the plant's gain over its
 *      running cost is negative, and the water's books still pay the power's for all the works' draw.
 *   2. A TOWN SHORT OF WATER PRICES IT AT ITS SEA WORKS' POWER: its demand past what every works makes at full staffing, the sea
 *      works deliver their whole output, past SEA_MARGIN_SHARE of it, and the water's price is the tariff and their draw at the struck
 *      commerce price over that output, recomputed from the stacks, the book and UtilitiesHandler.waterOutput(); the sea's water pays
 *      the sea works' power. "At the struck commerce price" is on the kWh served throughout (PW1-R): the price a kW of the year's
 *      mean draw times the season the book's month was dispatched at (UtilitiesHandler.Book.season), as every class is billed.
 *   3. EVERY BILL ONCE: the water's books pay the power's the month's works' draw at the struck commerce price; the electricity's
 *      income gains it and the water's loses it, the services' net and the treasury's line are unmoved by it, the audit's utility
 *      revenue is the sectors' bills, and the money audit closes.
 *   4. THE SAVE: a city saved and loaded prices its water, holds its works' record and its payment as it did; a save without them
 *      (the build before) loads with the record its load rebuilds from the same stock and nothing paid, and prices by the same rule.
 *   5. A LICENCE'S POWER IS THE DISPATCH'S ENERGY SERVED (PW1-L): the dispatch run again on the month's rows serves the month's
 *      demand; in the copy in surplus in every half-block a plant moved adds nothing; in the copy short in every half-block by more
 *      than the move it adds the move's kWh over the kWh asked of the demand, which is not the flat rule's move.
 *   6. A RECIPE'S OWN POWER IS WEIGHED AT WHAT THE STRIKE PAYS IT (PW1-G): in the short copy, whose works price is not its industry
 *      price, a Pulp Mill's surplus at the works price, what Wood Products' statement is paid for it, and not at the industry price;
 *      an Oxygen Steel Shop's own power netted at its class's.
 *   7. THE SEA ON A SLIVER: the two copies, each its fresh works retired for a desalination plant and its demand a little past the
 *      wells, so the sea delivers under SEA_MARGIN_SHARE of its output and the water served is within that share: the price is the
 *      tariff and the sea works' draw spread over the water served, bounded below their draw over the sliver; the water served at it
 *      recovers their power once and the bills no more; and the copy whose homes ask past its plant, its commerce price the higher,
 *      prices the dearer water.
 *   8. THE SEA ON A SLIVER OF A CITY THAT SERVES PAST THAT SHARE: the cheap copy's fresh works stood again and its demand a little
 *      past the land's water: the price is the sea works' draw over that share of their output, what a plant delivering just the
 *      share prices its water at, still below their draw over the sliver.
 *
 * Every fixture is a scratch city (HistoricalLicenceCheck.town(), PowerPriceCheck.town()) and CAUSES its condition through the
 * model's inputs: Pulp Mills for the water's demand, the homes' draw for the power's price. Every expected figure is a getter or a
 * constant of the model.
 */
public class WaterPowerCheck {

    static int fails = 0, checks = 0;

    /** kW a plant is moved by in section 5: well inside every short half-block's shortfall in the dear copy. */
    static final double MOVE_KW = 1000;

    /** The save slot the scratch cities are saved in: past the player's nine. */
    static final int SLOT = 10;

    static void check(String label, boolean ok, String detail) {
        checks++;
        if (!ok) fails++;
        System.out.println((ok ? "  PASS " : "  FAIL ") + label + (detail == null ? "" : " - " + detail));
    }

    static boolean near(double a, double b, double rel) {
        return Math.abs(a - b) <= rel * Math.max(1e-12, Math.max(Math.abs(a), Math.abs(b)));
    }

    static String f(double v) { return String.format("%.9g", v); }

    /** What the water works stand and deliver, worked from the stacks, the templates and waterOutput(): independent of the handler's record. */
    record Works(double freshKw, double seaKw, double served, double withoutSea, double wellsAlone, double seaOutput) {
        double landDelivered() { return withoutSea; }
        double seaDelivered() { return served - withoutSea; }
    }

    /** The sea works' draw a unit, the rule stated from a record's fields: over the sea's water, or the water served under SEA_MARGIN_SHARE of their output. */
    static double kwPerUnit(double seaKw, double land, double sea, double seaOutput) {
        return sea <= 0 ? 0 : seaKw / Math.max(sea, Math.min(land + sea, UtilitiesHandler.SEA_MARGIN_SHARE * seaOutput));
    }

    static Works works(Game g) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double fresh = 0, sea = 0;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) {
            if (t.getCategory() != BuildingType.WATER) continue;
            BuildingsStacks s = g.getBuildingManager().getStack(t);
            if (s == null) continue;
            if (t.isSeaWater()) sea += s.getQuantity() * t.powerBought(); else fresh += s.getQuantity() * t.powerBought();
        }
        long e = g.getBuildingManager().getFoundingEndowments();
        double d = u.getWaterConsumption(), fill = u.getAverageUtilityFill(), cap = u.getFreshCap();
        return new Works(fresh, sea,
                Math.min(d, UtilitiesHandler.waterOutput(u.getFreshNameplate(), u.getDesalNameplate(), fill, cap, e)),
                Math.min(d, UtilitiesHandler.waterOutput(u.getFreshNameplate(), 0, fill, cap, e)),
                Math.min(d, UtilitiesHandler.waterOutput(0, 0, fill, cap, e)), u.getDesalNameplate() * fill);
    }

    /** Pulp Mills past what the city's water works make at full staffing, or past the founding wells alone: how many stood. */
    static int mills(Game g, boolean pastEveryWorks) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        return millsPast(g, pastEveryWorks ? u.getDesalNameplate() : 0, pastEveryWorks ? u.getFreshNameplate() : 0);
    }

    /** Pulp Mills past what the given sea and fresh nameplates make with the wells at full staffing: how many more stood. */
    static int millsPast(Game g, double desal, double fresh) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double ceiling = UtilitiesHandler.waterOutput(fresh, desal, 1, u.getFreshCap(), g.getBuildingManager().getFoundingEndowments());
        BuildingsTemplate mill = g.getBuildingManager().getTemplateByName("Pulp Mill");
        int n = (int) Math.floor(Math.max(0, ceiling - u.getWaterConsumption()) / mill.getWaterConsumption()) + 1;
        BuildingsStacks have = g.getBuildingManager().getStack(mill);
        HistoricalLicenceCheck.stand(g, "Pulp Mill", (int) ((have == null ? 0 : have.getQuantity()) + n));
        return n;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        System.out.println("=== WaterPowerCheck: the water priced from its cost, the licence's power and a recipe's own (PW1-W) ===");
        PublicLicenceCheck.wire();

        // The desalination town: the plant known abroad at its date, standing beside the city's fresh works on the founding wells.
        Game d = HistoricalLicenceCheck.town("wp-desal", true);
        PublicLicenceCheck.allExcept(d, "E6");
        ResearchCheck.atMonth(d, ResearchCatalog.knownFrom("E6"));
        BuildingsTemplate desal = HistoricalLicenceCheck.stand(d, "Desalination Plant", 1);
        BuildingsStacks sea = d.getBuildingManager().getStack(desal);
        PublicLicenceCheck.months(d, 2);
        surplus(d, desal, sea);
        int n = mills(d, true);
        PublicLicenceCheck.months(d, 2);
        short_(d, n);
        money(d);
        save(d);

        // The two copies of PowerPriceCheck's town, a Pulp Mill past the wells in each, one with its homes asking past its plant.
        PowerPriceCheck.City lo = PowerPriceCheck.town("wp-lo", false, 0), hi = PowerPriceCheck.town("wp-hi", false, PowerPriceCheck.SHORT_ASK);
        int nlo = mills(lo.g, false), nhi = mills(hi.g, false);
        PublicLicenceCheck.months(lo.g, 3);
        PublicLicenceCheck.months(hi.g, 3);
        licence(lo.g, hi.g);
        recipes(hi.g, nhi);
        // ...then in each its fresh works retired for a desalination plant, so the sea takes the demand past the wells.
        for (Game g : new Game[] { lo.g, hi.g }) {
            BuildingsTemplate wtp = g.getBuildingManager().getTemplateByName("Water Treatment Plant");
            BuildingsStacks fresh = g.getBuildingManager().getStack(wtp);
            if (fresh != null) g.getBuildingManager().retire(wtp, fresh.getQuantity());
            HistoricalLicenceCheck.stand(g, "Desalination Plant", 1);
            PublicLicenceCheck.months(g, 2);
        }
        dearPower(lo.g, hi.g, nlo, nhi);
        // ...and in the cheap copy its fresh works stood again, and a Pulp Mill more a month until the sea first delivers: the mills' hiring
        // lowers the works' fill as they come, so a count worked at full staffing overshoots into a shortage.
        HistoricalLicenceCheck.stand(lo.g, "Water Treatment Plant", 1);
        PublicLicenceCheck.months(lo.g, 1);
        BuildingsTemplate mill = lo.g.getBuildingManager().getTemplateByName("Pulp Mill");
        int nland = 0;
        while (nland < 12 && works(lo.g).seaDelivered() <= 0) {
            HistoricalLicenceCheck.stand(lo.g, "Pulp Mill", (int) lo.g.getBuildingManager().getStack(mill).getQuantity() + 1);
            nland++;
            PublicLicenceCheck.months(lo.g, 1);
        }
        wideSliver(lo.g, nland);

        System.out.println(fails == 0 ? "WaterPowerCheck: all ok (" + checks + " checks)" : "WaterPowerCheck: " + fails + " FAILED of " + checks);
        System.exit(fails);
    }

    /* ---------- 1 ---------- */

    static void surplus(Game d, BuildingsTemplate desal, BuildingsStacks sea) {
        System.out.println("--- 1. a town in surplus prices its water at its tariff ---");
        UtilitiesHandler u = d.getServicesManager().getUtilitiesHandler();
        Works w = works(d);
        double price = d.getEconomyManager().getPricePerWaterUnit(), tariff = u.getWaterTariff(), c = u.getStruckBook().commercePrice * u.getStruckBook().season;
        check("fixture: a desalination plant stands beside the fresh works, and the founding wells alone serve the city's demand",
                sea != null && sea.getQuantity() >= 1 && w.seaKw() > 0 && w.freshKw() > 0 && w.served() == u.getWaterConsumption() && w.wellsAlone() == w.served() && c > 0,
                "demand " + f(u.getWaterConsumption()) + " wells " + f(w.wellsAlone()) + " sea kW " + f(w.seaKw()) + " commerce " + f(c));
        check("the sea works deliver nothing and the land's water all of it, and the handler's record says so", w.seaDelivered() == 0 && w.landDelivered() == w.served()
                && u.getLatestWaterDraw().seaDelivered() == 0 && u.getLatestWaterDraw().landDelivered() == w.served(), String.valueOf(u.getLatestWaterDraw()));
        check("the water's price, what the statements strike at and what the recipes weigh, is the tariff exactly: the land's water pays its"
                        + " works' power from it, and the plant's power is in no price",
                price == tariff && Recipes.pricesOf(d).pricePerWaterUnit() == tariff && tariff == .05 * PriceLevel.FACTOR,
                f(price) + " against " + f(tariff));
        double gain = PublicLicenceCheck.desalReading(d, desal, sea)[3];
        check("the plant serves no demand: its gain over its running cost is negative", gain < 0, f(gain));
        double owed = (w.freshKw() + w.seaKw()) * c;
        PublicLicenceCheck.months(d, 1);
        check("the water's books still pay the power's for all the works' draw at the struck commerce price, the plant's included",
                owed > 0 && near(u.getWaterPowerPaid(), owed, 1e-12), f(u.getWaterPowerPaid()) + " against " + f(owed));
        Works after = works(d);
        check("...and a month on the wells still serve the demand and the price is still the tariff", after.seaDelivered() == 0 && after.wellsAlone() == after.served()
                && d.getEconomyManager().getPricePerWaterUnit() == tariff, f(d.getEconomyManager().getPricePerWaterUnit()));
    }

    /* ---------- 2 ---------- */

    static void short_(Game d, int n) {
        System.out.println("--- 2. a town short of water prices it at its sea works' power ---");
        UtilitiesHandler u = d.getServicesManager().getUtilitiesHandler();
        Works w = works(d);
        double c = u.getStruckBook().commercePrice * u.getStruckBook().season, tariff = u.getWaterTariff(), price = d.getEconomyManager().getPricePerWaterUnit();
        check("fixture: " + n + " Pulp Mills take the demand past every works at full staffing: the city is short of water",
                u.getWaterRatio() < 1 && w.served() < u.getWaterConsumption() && c > 0, "ratio " + f(u.getWaterRatio()) + " commerce " + f(c));
        check("the sea works deliver their whole output, the desalination nameplate at the fill: past SEA_MARGIN_SHARE of it, the margin alone",
                w.seaDelivered() > 0 && w.seaDelivered() >= UtilitiesHandler.SEA_MARGIN_SHARE * w.seaOutput() && near(w.seaDelivered(), u.getDesalNameplate() * u.getAverageUtilityFill(), 1e-9) && near(u.getLatestWaterDraw().seaDelivered(), w.seaDelivered(), 1e-12),
                f(w.seaDelivered()) + " against " + f(u.getLatestWaterDraw().seaDelivered()));
        double want = tariff + w.seaKw() * c / w.seaDelivered();
        check("the water's price is the tariff and the sea works' draw at the struck commerce price over the water they deliver",
                near(price, want, 1e-12) && near(Recipes.pricesOf(d).pricePerWaterUnit(), want, 1e-12) && price > tariff, f(price) + " against " + f(want));
        check("the sea's water at that price pays the sea works' power: their water's power part is their draw at the commerce price",
                near((price - tariff) * w.seaDelivered(), w.seaKw() * c, 1e-12), f((price - tariff) * w.seaDelivered()) + " against " + f(w.seaKw() * c));
    }

    /* ---------- 7 ---------- */

    static void dearPower(Game lo, Game hi, int nlo, int nhi) {
        System.out.println("--- 7. the sea on a sliver: the price bounded, the sea works' power recovered once, and moved by the price of power ---");
        UtilitiesHandler a = lo.getServicesManager().getUtilitiesHandler(), b = hi.getServicesManager().getUtilitiesHandler();
        Works wa = works(lo), wb = works(hi);
        double ca = a.getStruckBook().commercePrice * a.getStruckBook().season, cb = b.getStruckBook().commercePrice * b.getStruckBook().season, share = UtilitiesHandler.SEA_MARGIN_SHARE;
        double pa = lo.getEconomyManager().getPricePerWaterUnit(), pb = hi.getEconomyManager().getPricePerWaterUnit();
        check("fixture: each copy's fresh works are retired for a desalination plant, and its Pulp Mills (" + nlo + ", " + nhi + ") take its demand"
                        + " a little past the wells: the sea delivers a sliver, under SEA_MARGIN_SHARE of its output at the fill, and the water served is within that share",
                wa.freshKw() == 0 && wb.freshKw() == 0 && wa.seaKw() > 0 && wb.seaKw() > 0 && wa.seaDelivered() > 0 && wb.seaDelivered() > 0
                        && wa.served() <= share * wa.seaOutput() && wb.served() <= share * wb.seaOutput(),
                "sea " + f(wa.seaDelivered()) + ", " + f(wb.seaDelivered()) + " served " + f(wa.served()) + ", " + f(wb.served())
                        + " output " + f(wa.seaOutput()) + ", " + f(wb.seaOutput()));
        check("fixture: the copy whose homes ask past its plant is short of power and its struck commerce price is the higher",
                b.getStruckBook().ratio < 1 && cb > ca && ca > 0, f(cb) + " against " + f(ca));
        double wantA = a.getWaterTariff() + wa.seaKw() * ca / wa.served(), wantB = b.getWaterTariff() + wb.seaKw() * cb / wb.served();
        check("each copy's water is the tariff and its sea works' draw at its own commerce price spread over the water served, not over the sliver",
                near(pa, wantA, 1e-12) && near(pb, wantB, 1e-12), f(pa) + ", " + f(pb) + " against " + f(wantA) + ", " + f(wantB));
        check("the price stays bounded: below the sea works' draw over the sliver, the rule before the bound",
                pa < a.getWaterTariff() + wa.seaKw() * ca / wa.seaDelivered() && pb < b.getWaterTariff() + wb.seaKw() * cb / wb.seaDelivered(), null);
        boolean once = true, billedNoMore = true;
        for (Game g : new Game[] { lo, hi }) {
            UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
            Works w = g == lo ? wa : wb;
            double adder = g.getEconomyManager().getPricePerWaterUnit() - u.getWaterTariff(), power = w.seaKw() * u.getStruckBook().commercePrice * u.getStruckBook().season;
            once &= near(w.served() * adder, power, 1e-12);
            billedNoMore &= u.getBilledWaterDraw() * u.getWaterRatio() * adder <= power * (1 + 1e-12);
        }
        check("the water served at that price recovers the sea works' power once, and the bills, the billed part of it, no more", once && billedNoMore,
                f(wa.served() * (pa - a.getWaterTariff())) + " against " + f(wa.seaKw() * ca));
        check("the dearer power prices the dearer water", pb > pa, f(pb) + " against " + f(pa));
    }

    /* ---------- 8 ---------- */

    static void wideSliver(Game g, int n) {
        System.out.println("--- 8. the sea on a sliver of a city that serves past that share: the price is the plant's power over the share ---");
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        Works w = works(g);
        double c = u.getStruckBook().commercePrice * u.getStruckBook().season, share = UtilitiesHandler.SEA_MARGIN_SHARE, tariff = u.getWaterTariff();
        double price = g.getEconomyManager().getPricePerWaterUnit(), power = w.seaKw() * c;
        check("fixture: the cheap copy's fresh works stood again beside its plant, and " + n + " Pulp Mills more take its demand a little past the land's"
                        + " water: the sea delivers a sliver, under SEA_MARGIN_SHARE of its output at the fill, and the water served passes that share",
                w.freshKw() > 0 && w.seaDelivered() > 0 && w.seaDelivered() < share * w.seaOutput() && w.served() > share * w.seaOutput() && c > 0,
                "sea " + f(w.seaDelivered()) + " served " + f(w.served()) + " output " + f(w.seaOutput()));
        double want = tariff + power / (share * w.seaOutput());
        check("the water's price is the tariff and the sea works' draw at the struck commerce price over that share of their output, what a plant"
                        + " delivering just the share would price its water at", near(price, want, 1e-12) && near(Recipes.pricesOf(g).pricePerWaterUnit(), want, 1e-12),
                f(price) + " against " + f(want));
        check("...bounded below the sea works' draw over the sliver, and the water served pays their power more than once, as the margin's does",
                price < tariff + power / w.seaDelivered() && w.served() * (price - tariff) > power, f(w.served() * (price - tariff)) + " against " + f(power));
    }

    /* ---------- 5 ---------- */

    static void licence(Game lo, Game hi) {
        System.out.println("--- 5. a licence's power is the dispatch's energy served (PW1-L) ---");
        for (Game g : new Game[] { lo, hi }) {
            UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
            PowerDispatch.Month m = u.getLatestMonth();
            BuildingsTemplate coal = g.getBuildingManager().getTemplateByName("Coal Power Plant");
            // PW3 (PW1-P's fifth gap): the moved kW run at the coal plant's own posts' fill, as the merit order runs every plant.
            double base = u.deliveredWith(coal, 0), moved = u.deliveredWith(coal, MOVE_KW), fill = u.plantFill(coal);
            boolean surplus = true, shortBy = true;
            for (int s = 0; s < PowerDispatch.HALVES; s++) {
                surplus &= m.unserved[s] == 0;
                shortBy &= m.unserved[s] > MOVE_KW * fill;
            }
            String who = g == lo ? "the cheap copy" : "the dear copy";
            check(who + ": the dispatch run again on the month's rows serves the month's demand, its draw at its ratio",
                    near(base, u.getConsumption() * m.ratio, 1e-12), f(base) + " against " + f(u.getConsumption() * m.ratio));
            if (g == lo) {
                check("fixture: the cheap copy is in surplus in every half-block", surplus, java.util.Arrays.toString(m.unserved));
                check("...so a coal plant moved by " + (int) MOVE_KW + " kW serves nothing more", moved == base, f(moved) + " against " + f(base));
            } else {
                double want = base + u.getConsumption() * MOVE_KW * fill * PowerDispatch.MONTH_HOURS / m.askedKwh, flat = base + MOVE_KW * fill;
                check("fixture: the dear copy is short in every half-block by more than the move", shortBy, java.util.Arrays.toString(m.unserved));
                check("...so the move serves its every kWh: the demand delivered rises by the move's kWh over the kWh asked",
                        near(moved, want, 1e-9), f(moved) + " against " + f(want));
                check("...which is not the flat rule's whole move: the dispatch carries the grid's losses and the season",
                        !near(moved - base, flat - base, 1e-3), f(moved - base) + " against the flat " + f(flat - base));
            }
        }
    }

    /* ---------- 6 ---------- */

    static void recipes(Game d, int n) {
        System.out.println("--- 6. a recipe's own power is weighed at what the strike pays it (PW1-G) ---");
        BuildingsTemplate mill = d.getBuildingManager().getTemplateByName("Pulp Mill"), oxygen = d.getBuildingManager().getTemplateByName("Oxygen Steel Shop");
        Recipe pulp = Recipe.topLevelOf(mill), steel = Recipe.topLevelOf(oxygen);
        Recipes.Prices pm = Recipes.pricesOf(d, mill);
        Sector wood = d.getSectors().byKey(Sectors.WOOD_PRODUCTS);
        double surplus = wood.getPowerSurplus(), each = pulp.getGen() - pulp.getElectricity();
        check("fixture: the Pulp Mill's recipe makes power past its draw, and Wood Products' surplus is its " + n + " mills'",
                each > 0 && pulp.getGen() == mill.getGen() && pulp.getElectricity() == mill.getElectricityConsumption() && near(surplus, n * each, 1e-12),
                f(surplus) + " against " + n + " x " + f(each));
        check("fixture: the works price the strike pays a surplus is not the industry price a Heavy Industry draw pays",
                pm.worksPricePerWatt() > 0 && pm.pricePerWatt() == d.getEconomyManager().getPowerPriceFor(mill) && pm.worksPricePerWatt() != pm.pricePerWatt(),
                f(pm.worksPricePerWatt()) + " against " + f(pm.pricePerWatt()));
        double weighed = -n * Recipes.powerCost(pulp, pm);
        PublicLicenceCheck.months(d, 1);
        double paid = wood.statement().powerSold;
        check("the mills' recipe weighs their surplus at what Wood Products' statement was then paid for it, at the works price",
                paid > 0 && near(weighed, paid, 1e-12), f(weighed) + " against " + f(paid));
        check("...and not at the industry price, which the whole difference at the class's price would have weighed it at",
                !near(weighed, n * each * pm.pricePerWatt(), 1e-6), f(n * each * pm.pricePerWatt()));
        double net = steel.getElectricity() - steel.getGen();
        Recipes.Prices po = Recipes.pricesOf(d, oxygen);
        check("an Oxygen Steel Shop's own power is netted at its class's price: its draw past what it makes at the industry price",
                steel.getGen() > 0 && net > 0 && near(Recipes.powerCost(steel, po), net * po.pricePerWatt(), 1e-12) && po.pricePerWatt() == d.getEconomyManager().getPowerPriceFor(oxygen),
                f(Recipes.powerCost(steel, po)) + " against " + f(net * po.pricePerWatt()));
    }

    /* ---------- 3 ---------- */

    static void money(Game d) {
        System.out.println("--- 3. every bill once ---");
        UtilitiesHandler u = d.getServicesManager().getUtilitiesHandler();
        Works w = works(d);
        double owed = (w.freshKw() + w.seaKw()) * u.getStruckBook().commercePrice * u.getStruckBook().season;
        PublicLicenceCheck.months(d, 1);
        double paid = u.getWaterPowerPaid();
        check("the water's books paid the power's the month's works' draw at the struck commerce price", paid > 0 && near(paid, owed, 1e-12), f(paid) + " against " + f(owed));
        double e = u.getElectricityIncome(), wi = u.getWaterIncome(), net = d.getServicesManager().getServiceNetIncome();
        u.setWaterPowerPaid(0);
        double e0 = u.getElectricityIncome(), w0 = u.getWaterIncome(), net0 = d.getServicesManager().getServiceNetIncome();
        u.setWaterPowerPaid(paid);
        check("the electricity's income gains it and the water's loses it", near(e - e0, paid, 1e-9) && near(w0 - wi, paid, 1e-9), f(e - e0) + ", " + f(w0 - wi));
        check("...and the services' net, which the treasury takes, is the same with it or without", Math.abs(net - net0) <= 1e-9 * Math.max(1, Math.abs(net)), f(net) + " against " + f(net0));
        check("the treasury's utility line is the services' net", Math.abs(d.getEconomyManager().getUtilityIncome() - net) <= 1e-9 * Math.max(1, Math.abs(net)),
                f(d.getEconomyManager().getUtilityIncome()) + " against " + f(net));
        MoneyAudit.Result r = d.getLastMoneyAudit();
        double revenue = r.figure("chk utility revenue"), bills = r.figure("chk sector utility bills");
        check("the audit's utility revenue is the sectors' bills: the works' power is no customer's", revenue > 0 && near(revenue, bills, 1e-9), f(revenue) + " against " + f(bills));
        check("the money audit closes", Math.abs(r.residual) <= MoneyAudit.tolerance(r.moved()), f(r.residual));
    }

    /* ---------- 4 ---------- */

    static void save(Game d) throws Exception {
        System.out.println("--- 4. the save ---");
        UtilitiesHandler u = d.getServicesManager().getUtilitiesHandler();
        double price = d.getEconomyManager().getPricePerWaterUnit(), paid = u.getWaterPowerPaid();
        UtilitiesHandler.WaterDraw record = u.getLatestWaterDraw();
        GameFiles files = d.getGameFiles();
        boolean written = d.saveGame(SLOT, "water priced from its cost").ok;
        Game back = new Game(files);
        HistoricalRecipeCheck.silent(() -> back.loadGameSave(SLOT));
        UtilitiesHandler b = back.getServicesManager().getUtilitiesHandler();
        check("fixture: the city saved in its scratch folder's slot " + SLOT + ", with a works' record and a payment to keep", written && record != null && record.seaDelivered() > 0 && paid > 0, null);
        check("loaded, it prices its water as it did, holds the same works' record and the same payment",
                back.getEconomyManager().getPricePerWaterUnit() == price && record.equals(b.getLatestWaterDraw()) && b.getWaterPowerPaid() == paid,
                f(back.getEconomyManager().getPricePerWaterUnit()) + " against " + f(price) + "; " + b.getLatestWaterDraw());

        java.nio.file.Path file = files.saveFile(SLOT);
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(file)).getAsJsonObject();
        com.google.gson.JsonObject books = json.getAsJsonObject("powerBooks");
        boolean had = books != null && books.has("pendingWater") && books.has("waterPowerPaid");
        if (books != null) { books.remove("pendingWater"); books.remove("waterPowerPaid"); }
        java.nio.file.Files.writeString(file, new com.google.gson.Gson().toJson(json));
        Game old = new Game(files);
        HistoricalRecipeCheck.silent(() -> old.loadGameSave(SLOT));
        UtilitiesHandler o = old.getServicesManager().getUtilitiesHandler();
        UtilitiesHandler.WaterDraw rebuilt = o.getLatestWaterDraw();
        double c = o.getStruckBook().commercePrice * o.getStruckBook().season;
        double want = rebuilt == null ? Double.NaN : o.getWaterTariff()
                + kwPerUnit(rebuilt.seaKw(), rebuilt.landDelivered(), rebuilt.seaDelivered(), rebuilt.seaOutput()) * c;
        check("fixture: the save carried the record and the payment, struck out of the file to make the save of the build before", had, null);
        check("a save without them loads with nothing paid and the record its load rebuilt from the same stock, which prices the water by the same rule",
                o.getWaterPowerPaid() == 0 && rebuilt != null && near(old.getEconomyManager().getPricePerWaterUnit(), want, 1e-12),
                rebuilt + " priced " + f(old.getEconomyManager().getPricePerWaterUnit()) + " against " + f(want));
    }
}
