package ham.citybuildersim;

import ham.citybuildersim.sectors.Mining;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A price for each class of ground, checked (ET3, slice A; docs/studies/spec-earth2-terrain.md 5.1 and 5.2): the going
 * price stays the price of flat ground, foothill is that price less its preparation and never under the base price of
 * unwanted ground, steep and very steep ground is the base price - no class dearer than flat. The land office prices
 * its offers by the ground they hold, the pickers rank them by the ground a building can stand on with its preparation
 * counted, and a business pays the city for its plot class by class.
 *
 * WHAT THIS HAS TO PROVE, every fixture causing the condition it tests and every figure worked out from the model's own
 * constants and getters (LandMarket.openingUsdPerSqFt(), SitePreparation, LandManager's square feet) and never typed:
 *
 *   1. THE RULE. LandMarket.reliefPrice() for each class with the going price above the base plus the preparation
 *      (foothill = going less the preparation), between the base and base plus the preparation (foothill = base) and
 *      below the base (every class = going), at a price level other than 1; never above flat.
 *   2. THE OFFICE BUYS EACH CLASS. An offer's price by class: all flat, all foothill, all steep, all very steep, a mix,
 *      water at its shares of the flat price, resources as before, rounded to US$5k; and the city buys an offer of each
 *      class through LandManager.buyParcel(), paying that price at the rate, the class added to its ground.
 *   3. THE PICKERS. Two offers at one dry area, one all flat and one mostly steep and cheaper a dry km2: bestValue()
 *      takes the flat one; an all-foothill offer at the flat one's price loses by its preparation; listings of flat
 *      ground pick exactly as getDryKm2PerUsd() would; listings with no buildable ground still have a best; and Game's
 *      bestOffer() and the auto-builder's shortcut rank by buildable ground and price with the foothill prepared.
 *   4. A BUSINESS BUYS EACH CLASS. On a city that owns flat, foothill and steep ground: a building allocated while flat
 *      is free pays the flat price, once flat is used it pays the foothill price, a works pays the steep price on the
 *      steep, a plot straddling two classes pays the sum by plotByRelief(); the zone's share applies to each class; a
 *      plot given back is bought back by the reverse of the split; and the foothill's preparation the price reads is
 *      the quote's.
 *   5. FLAT WORLDS UNCHANGED. On a city that owns neither foothill nor steep ground every new figure is the old one to
 *      the bit: the price by class against the four-argument price(), buildableKm2PerUsd() against getDryKm2PerUsd(),
 *      priceFor() and buybackPriceFor() with and without a works and a zone against the old expression, the pickers
 *      against the old loops.
 *
 * WHY. ET1 gave every plot a relief and ET2 made buildings stand by it, so a city on hills owns ground that is worth
 * less than the valley floor and was still priced as if it were not: its offers cost as much a square foot on a
 * mountainside as on a plain, the best-value pick bought mountains, and a business paid the same for a pad that had to
 * be cut as for a pad that did not. The arithmetic of each part (the rule, the offer, the picker, the plot) is its own,
 * so each is worked out here from the rule's constants, not from the code under test.
 */
public class LandClassCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-118s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-118s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-118s %s  %,.9f against %,.9f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** What a fixture of figures near one another can be told apart by: a rounding, not ground. */
    static final double TOL = 1e-9;

    /** A part of a plot that should be none is none: a rounding of the split's differences at most, against the plot's size. */
    static boolean zero(double part, double plot) {
        return Math.abs(part) <= TOL * plot;
    }

    /** The two figures are the same double, to the bit. */
    static boolean same(double a, double b) {
        return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
    }

    /** A fixture city's treasury: more than any order here costs, so a refusal is never for cash. */
    static final double CASH = 1e8;

    /** ...and the cash a sector is given to pay a business's order from by fiat. */
    static final double BUSINESS_CASH = 1e6;

    /** The relief classes in World's order (FLAT to VERY_STEEP). */
    static final String[] GROUND = { "flat", "foothill", "steep", "very steep" };

    static final String HOUSE = "House";

    /** An offer's price is rounded to US$5k; a figure worked out from the ground's price can be that far off. */
    static final double ROUNDING = 2.5 + 1e-6;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        theRule();
        theOfficeBuysEachClass();
        thePickers();
        aBusinessBuysEachClass();
        flatWorldsUnchanged();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       fixtures
       ===================================================================== */

    static BuildingsTemplate template(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        if (t == null) throw new IllegalStateException("no template named " + name);
        return t;
    }

    /**
     * A city founded as a harness founds one, its dry ground of the shares given by relief (flat, foothill, steep,
     * very steep, adding up to 1; CityLand.setCentreRelief()), every sector held so no business builds or scraps
     * anything here but by a fixture's hand, the treasury CASH, and all of its ground free.
     */
    static Game city(String label, double flat, double foothill, double steep, double veryStep) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> {
            g.run();
            g.setCashForTest(CASH);
            for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
        });
        double dry = g.getCityLand().totalKm2(CityLand.DRY);
        g.getCityLand().setCentreRelief(new double[] { flat * dry, foothill * dry, steep * dry, veryStep * dry, 0 });
        g.getLandManager().setAllocatedSqFt(0);
        g.getLandManager().restoreWorks(0);
        return g;
    }

    /** Gives the city iron sites by fiat, for the Iron Mine: hematite, the first type. */
    static void ironSites(Game g, int sites) {
        g.getLandManager().restoreSites(Resource.IRON, sites, sites * Resource.IRON.amountPerSite());
    }

    /** The people per square kilometre at which crowding multiplies the ground's price by `premium` (1 to the ceiling): found by halving, on the model's own curve. */
    static double crowdingFor(double premium) {
        double lo = 0, hi = 1e9;
        for (int i = 0; i < 200; i++) {
            double mid = (lo + hi) / 2;
            if (LandMarket.crowdingPremium(mid) < premium) lo = mid; else hi = mid;
        }
        return hi;
    }

    /** A bare land office priced at a crowding premium and a world price level: update() with the people that crowd 100 million square feet to it. */
    static LandMarket market(double premium, double level) {
        LandMarket m = new LandMarket();
        double owned = 1e8;
        m.update(owned, 0, Math.round(crowdingFor(premium) * LandManager.km2(owned)), level);
        return m;
    }

    /** A relief row from shares of the dry ground (flat, foothill, steep, very steep), square kilometres, no shallow sea. */
    static double[] relief(double dry, double flat, double foothill, double steep, double veryStep) {
        return new double[] { flat * dry, foothill * dry, steep * dry, veryStep * dry, 0 };
    }

    /** The five areas of a piece of dry ground with its water: total, dry, fresh, sea, forest. */
    static double[] areas(double dry, double fresh, double sea) {
        return new double[] { dry + fresh + sea, dry, fresh, sea, 0 };
    }

    /** An offer by hand on a bare office: dry ground by relief, no water and no ore, in its own place, at a price. */
    static LandParcel offer(int id, double dry, double[] reliefKm2, double priceUsd) {
        return new LandParcel(id, (id - 1) / LandMarket.OFFERS_A_SIDE, (id - 1) % LandMarket.OFFERS_A_SIDE, 0, 0, 0, 0, 0,
                areas(dry, 0, 0), reliefKm2, new int[CityLand.KINDS], new double[CityLand.KINDS], priceUsd, 0);
    }

    /** An offer's price by class at a market's prices, no ore. */
    static double priced(LandMarket m, double dry, double[] reliefKm2) {
        return LandMarket.price(areas(dry, 0, 0), reliefKm2, new double[CityLand.KINDS], m.getGroundUsdPerSqFt(), m.getUsPriceLevel());
    }

    /** The old picker, copied: the most dry square kilometres a dollar among the offers not mostly sea, the nearer on a tie. */
    static LandParcel oldBest(LandMarket m, List<LandParcel> list) {
        LandParcel best = null;
        for (LandParcel p : list) {
            if (p.isMostlySea()) continue;
            if (best == null || p.getDryKm2PerUsd() > best.getDryKm2PerUsd()
                    || (p.getDryKm2PerUsd() == best.getDryKm2PerUsd() && m.nearer(p, best))) best = p;
        }
        return best;
    }

    /* =====================================================================
       1. THE RULE
       ===================================================================== */

    static void theRule() {
        out.println("--- 1. the rule: foothill is the going price less its preparation, never under the base; steep is the base; none dearer than flat ---");
        double base = LandMarket.openingUsdPerSqFt();
        double prep = LandMarket.prepUsdPerSqFt(1);
        report("fixture: the preparation of a square foot is SitePreparation's dollars a m2, in thousands, over a m2's share of a foot, and is more than the base price",
                prep == SitePreparation.USD_PER_M2 / 1000 * LandManager.SQ_M_PER_SQ_FT && prep > base,
                String.format("preparation %.6f, base %.6f thousand US dollars a sq ft", prep, base));
        close("...and it follows the world's price level: at 3 times the prices it costs 3 times", LandMarket.prepUsdPerSqFt(3), 3 * prep, 1e-12);

        double high = base + 3 * prep;
        check("going above the base and the preparation: flat is the going price", LandMarket.reliefPrice(World.FLAT, high, base, prep) == high);
        check("...foothill is the going price less the preparation, which is above the base",
                LandMarket.reliefPrice(World.FOOTHILL, high, base, prep) == high - prep && high - prep > base);
        check("...steep and very steep are the base price",
                LandMarket.reliefPrice(World.STEEP, high, base, prep) == base && LandMarket.reliefPrice(World.VERY_STEEP, high, base, prep) == base);

        double between = base + prep / 2;
        check("going between the base and the base plus the preparation: flat is the going price, foothill the base price (going less the preparation is under it)",
                LandMarket.reliefPrice(World.FLAT, between, base, prep) == between && between - prep < base
                        && LandMarket.reliefPrice(World.FOOTHILL, between, base, prep) == base);
        check("...steep and very steep the base price too",
                LandMarket.reliefPrice(World.STEEP, between, base, prep) == base && LandMarket.reliefPrice(World.VERY_STEEP, between, base, prep) == base);
        double exact = base + prep;
        check("going exactly the base plus the preparation: foothill is the base price, no step between the two cases",
                LandMarket.reliefPrice(World.FOOTHILL, exact, base, prep) == base);

        double low = base / 2;
        boolean allGoing = true;
        for (int c = World.FLAT; c <= World.VERY_STEEP; c++) allGoing &= LandMarket.reliefPrice(c, low, base, prep) == low;
        check("going below the base price: every class is the going price - no class is ever dearer than flat", allGoing);

        boolean never = true, ordered = true, floor = true, rising = true;
        double lastFoothill = 0;
        for (double going = base / 4; going < 200 * base; going *= 1.07) {
            double flat = LandMarket.reliefPrice(World.FLAT, going, base, prep), foothill = LandMarket.reliefPrice(World.FOOTHILL, going, base, prep);
            double steep = LandMarket.reliefPrice(World.STEEP, going, base, prep), very = LandMarket.reliefPrice(World.VERY_STEEP, going, base, prep);
            never &= foothill <= flat && steep <= flat && very <= flat;
            ordered &= steep <= foothill && steep == very;
            floor &= foothill >= Math.min(going, base) && steep == Math.min(going, base);
            rising &= foothill >= lastFoothill;
            lastFoothill = foothill;
        }
        check("over every going price from a quarter of the base to 200 times it: no class dearer than flat, steep never above foothill", never && ordered);
        check("...foothill never under the base price (or the going price where that is lower), steep exactly the lesser of the two, foothill rising with the going price", floor && rising);
    }

    /* =====================================================================
       2. THE OFFICE BUYS EACH CLASS
       ===================================================================== */

    /** The dry ground the offers of 2 are of, square kilometres. */
    static final double DRY = 0.5;

    static void theOfficeBuysEachClass() {
        out.println("\n--- 2. the office prices an offer by the ground it holds, and the city buys each class at that price ---");
        double level = 1.7;
        LandMarket m = market(60, level);
        double going = m.getGroundUsdPerSqFt(), base = LandMarket.openingUsdPerSqFt() * level, prep = LandMarket.prepUsdPerSqFt(level);
        report("fixture: crowding has lifted the going price past the base plus the preparation, at a price level of " + level,
                going > base + prep && m.getUsPriceLevel() == level,
                String.format("going %.6f, base %.6f, preparation %.6f", going, base, prep));
        close("the office's flat price is the going price it quotes", m.getFlatUsdPerSqFt(), going, 0);
        close("...its foothill price the going price less the preparation", m.getFoothillUsdPerSqFt(), going - prep, 1e-12);
        close("...its steep price the base price at the price level", m.getSteepUsdPerSqFt(), base, 1e-12);
        check("...so flat is dearer than foothill and foothill than steep", m.getFlatUsdPerSqFt() > m.getFoothillUsdPerSqFt()
                && m.getFoothillUsdPerSqFt() > m.getSteepUsdPerSqFt());

        double sqFt = DRY * LandManager.SQ_FT_PER_KM2;
        double[] noOre = new double[CityLand.KINDS];
        double[] rates = { m.getFlatUsdPerSqFt(), m.getFoothillUsdPerSqFt(), m.getSteepUsdPerSqFt(), m.getSteepUsdPerSqFt() };
        double[] price = new double[4];
        for (int c = World.FLAT; c <= World.VERY_STEEP; c++) {
            double[] share = new double[4];
            share[c] = 1;
            double[] row = relief(DRY, share[0], share[1], share[2], share[3]);
            price[c] = LandMarket.price(areas(DRY, 0, 0), row, noOre, going, level);
            report("an offer of " + DRY + " km2 all " + GROUND[c] + " costs its square feet at the " + GROUND[c] + " price, within the US$5k rounding",
                    Math.abs(price[c] - sqFt * rates[c]) <= ROUNDING, String.format("%,.1f against %,.1f thousand", price[c], sqFt * rates[c]));
        }
        check("...flat dearer than foothill, foothill than steep, very steep the steep's", price[0] > price[1] && price[1] > price[2] && price[2] == price[3]);

        double[] mixRow = relief(DRY, 0.5, 0.3, 0.1, 0.1);
        double mix = LandMarket.price(areas(DRY, 0, 0), mixRow, noOre, going, level);
        double mixExpected = DRY * LandManager.SQ_FT_PER_KM2 * (0.5 * rates[0] + 0.3 * rates[1] + 0.2 * rates[2]);
        report("a mixed offer (50% flat, 30% foothill, 10% steep, 10% very steep) is the sum of its classes' square feet at their prices",
                Math.abs(mix - mixExpected) <= ROUNDING, String.format("%,.1f against %,.1f thousand", mix, mixExpected));

        double fresh = 0.2, sea = 0.3;
        double withWater = LandMarket.price(areas(DRY, fresh, sea), mixRow, noOre, going, level);
        double waterExpected = mixExpected + (LandMarket.FRESH_PRICE_SHARE * fresh + LandMarket.SEA_PRICE_SHARE * sea) * LandManager.SQ_FT_PER_KM2 * going;
        report("...with fresh water and sea besides, those at their shares of the FLAT price (0.45 and 0.08), not the hill's",
                Math.abs(withWater - waterExpected) <= ROUNDING, String.format("%,.1f against %,.1f thousand", withWater, waterExpected));

        double unit = Resource.IRON.good() == null ? 0 : Resource.IRON.good().worldExportPrice() * Resource.IRON.inGroundShare() * level;
        double[] ore = new double[CityLand.KINDS];
        ore[Resource.IRON.ordinal()] = unit > 0 ? 1e6 / unit : 0;
        double oreOnHills = LandMarket.price(areas(DRY, 0, 0), mixRow, ore, going, level) - mix;
        double oreOnFlat = LandMarket.price(areas(DRY, 0, 0), relief(DRY, 1, 0, 0, 0), ore, going, level) - price[0];
        report("a resource under the ground is priced as before: its share of its good's price at the price level, on hills as on the plain",
                unit > 0 && Math.abs(oreOnHills - 1e6) <= 2 * ROUNDING && Math.abs(oreOnFlat - 1e6) <= 2 * ROUNDING,
                String.format("%,.1f on hills, %,.1f on the plain, %,.1f thousand expected", oreOnHills, oreOnFlat, 1e6));

        buyingEachClass();
    }

    /** An offer of the real shelf with its ground given by relief and a price (its record, one slot changed), the rest as listed. */
    static LandParcel reliefOf(LandParcel p, double[] shares, double priceUsd) {
        double[] row = p.offerRow();
        row[row.length - 2] = priceUsd;
        return LandParcel.fromOfferRow(row, relief(p.getDryKm2(), shares[0], shares[1], shares[2], shares[3]));
    }

    static void buyingEachClass() {
        Game g = city("landclass-2", 1, 0, 0, 0);
        LandManager lm = g.getLandManager();
        LandMarket m = lm.getMarket();
        double crowded = crowdingFor(60) * LandManager.km2(lm.getOwnedSqFt());
        long people = Math.round(crowded);
        quietly(() -> lm.updateMarket(people));
        double going = m.getGroundUsdPerSqFt();
        double level = m.getUsPriceLevel();
        report("fixture: the city's own office is crowded to a going price above the base plus the preparation",
                going > LandMarket.openingUsdPerSqFt() * level + LandMarket.prepUsdPerSqFt(level), String.format("going %.6f at level %.3f", going, level));

        List<LandParcel> real = new ArrayList<>();
        for (LandParcel p : m.getListing()) if (real.size() < 3 && p.getDryKm2() > 0 && !p.isMostlySea()) real.add(p);
        check("fixture: three real offers with dry ground to give a relief to", real.size() == 3);
        double[][] shares = { { 1, 0, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 1, 0 } };
        String[] names = { "flat", "foothill", "steep" };
        List<LandParcel> variants = new ArrayList<>();
        double[] expectedMore = new double[3];
        for (int i = 0; i < 3; i++) {
            LandParcel p = real.get(i);
            double[] km2 = new double[CityLand.AREAS], ore = new double[CityLand.KINDS];
            for (int a = 0; a < CityLand.AREAS; a++) km2[a] = p.getKm2(a);
            for (Resource r : Resource.values()) ore[r.ordinal()] = p.getAmount(r);
            double[] row = relief(p.getDryKm2(), shares[i][0], shares[i][1], shares[i][2], shares[i][3]);
            double priceUsd = LandMarket.price(km2, row, ore, going, level);
            double asFlat = LandMarket.price(km2, relief(p.getDryKm2(), 1, 0, 0, 0), ore, going, level);
            double classRate = i == 0 ? m.getFlatUsdPerSqFt() : i == 1 ? m.getFoothillUsdPerSqFt() : m.getSteepUsdPerSqFt();
            double dropExpected = p.getSizeSqFt() * (m.getFlatUsdPerSqFt() - classRate);
            report("an offer of the real shelf, its ground made all " + names[i] + ": it costs the flat price less its square feet's difference in price, within the rounding",
                    Math.abs((asFlat - priceUsd) - dropExpected) <= 2 * ROUNDING,
                    String.format("%,.1f less %,.1f thousand, expected %,.1f less", asFlat, priceUsd, dropExpected));
            variants.add(reliefOf(p, shares[i], priceUsd));
            expectedMore[i] = p.getSizeSqFt();
        }
        m.putOffers(variants);
        double rate = g.getForeignAccounts().getRate();
        for (int i = 0; i < 3; i++) {
            LandParcel v = variants.get(i);
            double flat0 = lm.getFlatSqFt(), foothill0 = lm.getFoothillSqFt(), slope0 = lm.getSlopeSqFt();
            double paid = lm.buyParcel(v.getId(), 1e12, people);
            report("the city buys the " + names[i] + " offer: it pays that offer's price at today's rate, in local money", paid > 0 && Math.abs(paid - v.getPriceUsd() * rate) <= 1e-9 * paid,
                    String.format("%,.3f paid, %,.3f thousand US dollars at %.4f", paid, v.getPriceUsd(), rate));
            double dFlat = lm.getFlatSqFt() - flat0, dFoothill = lm.getFoothillSqFt() - foothill0, dSlope = lm.getSlopeSqFt() - slope0;
            double[] gained = { dFlat, dFoothill, dSlope };
            boolean right = true;
            for (int c = 0; c < 3; c++) right &= Math.abs(gained[c] - (c == i ? expectedMore[i] : 0)) <= TOL * expectedMore[i];
            report("...and the city's " + names[i] + " ground grows by that offer's square feet, the other classes by none", right,
                    String.format("flat +%,.0f, foothill +%,.0f, steep +%,.0f sq ft", dFlat, dFoothill, dSlope));
        }
    }

    /* =====================================================================
       3. THE PICKERS
       ===================================================================== */

    static void thePickers() {
        out.println("\n--- 3. the pickers rank by the ground a building can stand on, preparation counted; flat ground ranks as it always did ---");
        double level = 1.0;
        LandMarket m = market(60, level);
        double going = m.getGroundUsdPerSqFt(), prepUsd = LandMarket.prepUsdPerSqFt(level);
        double sqFt = DRY * LandManager.SQ_FT_PER_KM2;

        // 3a. a cheap mountain against the valley floor at the same dry area.
        double flatPrice = priced(m, DRY, relief(DRY, 1, 0, 0, 0));
        double[] mountainRow = relief(DRY, 0.1, 0, 0.9, 0);
        double mountainPrice = priced(m, DRY, mountainRow);
        LandParcel mountain = offer(1, DRY, mountainRow, mountainPrice), valley = offer(2, DRY, relief(DRY, 1, 0, 0, 0), flatPrice);
        report("fixture: two offers of one dry area, a valley floor and a mountain, the mountain cheaper a dry km2 - the old rule's best is the mountain",
                mountain.getDryKm2PerUsd() > valley.getDryKm2PerUsd() && mountain.getDryKm2() == valley.getDryKm2()
                        && oldBest(m, List.of(mountain, valley)) == mountain,
                String.format("%,.1f against %,.1f thousand", mountainPrice, flatPrice));
        m.putOffers(List.of(mountain, valley));
        check("bestValue() takes the valley floor: the buildable ground a dollar is what counts", m.bestValue() == valley);
        close("...the mountain's buildable km2 a dollar is its flat ground over its price", m.buildableKm2PerUsd(mountain), 0.1 * DRY / mountainPrice, 1e-12);
        check("...against the valley floor's, which is its dry km2 a dollar, and the mountain's is the lower",
                m.buildableKm2PerUsd(valley) == valley.getDryKm2PerUsd() && m.buildableKm2PerUsd(mountain) < m.buildableKm2PerUsd(valley));

        // 3b. foothill against flat at the same price: it loses by its preparation.
        LandParcel foothillOffer = offer(1, DRY, relief(DRY, 0, 1, 0, 0), flatPrice), flatOffer = offer(2, DRY, relief(DRY, 1, 0, 0, 0), flatPrice);
        report("fixture: an all-foothill offer and an all-flat offer at the same dry area and price - tied on the dry rule, the foothill one (lower id, the nearer) wins it",
                foothillOffer.getDryKm2PerUsd() == flatOffer.getDryKm2PerUsd() && oldBest(m, List.of(foothillOffer, flatOffer)) == foothillOffer,
                String.format("both %,.1f thousand for %.2f km2", flatPrice, DRY));
        m.putOffers(List.of(foothillOffer, flatOffer));
        check("bestValue() takes the flat one: the foothill offer loses by its preparation", m.bestValue() == flatOffer);
        close("...the foothill offer's buildable km2 a dollar counts the pad: its ground over its price and the preparation of its square feet",
                m.buildableKm2PerUsd(foothillOffer), DRY / (flatPrice + sqFt * prepUsd), 1e-12);
        close("...and its price with the preparation is the price and that much", m.priceWithPrepUsd(foothillOffer), flatPrice + sqFt * prepUsd, 1e-12);
        LandParcel halfPrep = offer(1, DRY, relief(DRY, 0, 1, 0, 0), flatPrice - 0.5 * sqFt * prepUsd);
        m.putOffers(List.of(halfPrep, flatOffer));
        check("a foothill offer cheaper than the flat one by half its preparation still loses to it", m.bestValue() == flatOffer);
        LandParcel twoPreps = offer(1, DRY, relief(DRY, 0, 1, 0, 0), flatPrice - 2 * sqFt * prepUsd);
        m.putOffers(List.of(twoPreps, flatOffer));
        check("...and cheaper by twice its preparation it wins", m.bestValue() == twoPreps);

        // 3c. flat listings pick as they always did.
        List<LandParcel> flats = new ArrayList<>();
        double[] drys = { 0.3, 0.8, 0.5, 0.5, 0.05, 1.2, 0.3, 0.7 };
        double[] costs = { 90_000, 100_000, 100_000, 100_000, 5_000, 400_000, 60_000, 0 };
        for (int i = 0; i < drys.length; i++) flats.add(offer(i + 1, drys[i], relief(drys[i], 1, 0, 0, 0), costs[i]));
        flats.add(offer(drys.length + 1, 0, relief(0, 1, 0, 0, 0), 0));
        m.putOffers(flats);
        boolean sameRate = true;
        for (LandParcel p : flats) sameRate &= same(m.buildableKm2PerUsd(p), p.getDryKm2PerUsd());
        check("a listing of flat ground, free offer and empty offer among it: every offer's buildable km2 a dollar is its dry km2 a dollar, to the bit", sameRate);
        check("...and bestValue() is the old rule's pick, to the offer", m.bestValue() == oldBest(m, flats));
        m.putOffers(flats.subList(1, 4));
        check("...also among offers tied on the rate (the nearer, the lower id, wins as before)", m.bestValue() == oldBest(m, flats.subList(1, 4)));

        // 3d. nothing buildable anywhere.
        List<LandParcel> mountains = new ArrayList<>();
        double[] mdry = { 0.4, 0.9, 0.6 };
        double[] mcost = { 90_000, 100_000, 30_000 };
        for (int i = 0; i < mdry.length; i++) mountains.add(offer(i + 1, mdry[i], relief(mdry[i], 0, 0, i == 1 ? 0.5 : 1, i == 1 ? 0.5 : 0), mcost[i]));
        m.putOffers(mountains);
        boolean none = true;
        for (LandParcel p : mountains) none &= LandMarket.buildableKm2(p) == 0 && m.buildableKm2PerUsd(p) == 0;
        check("fixture: three offers of steep and very steep ground only - no buildable ground in any", none);
        check("a city in the mountains still has a best: bestValue() falls back to the old dry rule and is not null",
                m.bestValue() != null && m.bestValue() == oldBest(m, mountains));

        pickersInTheGame();
    }

    static void pickersInTheGame() {
        Game g = city("landclass-3", 1, 0, 0, 0);
        LandManager lm = g.getLandManager();
        LandMarket m = lm.getMarket();
        long people = Math.round(crowdingFor(60) * LandManager.km2(lm.getOwnedSqFt()));
        quietly(() -> lm.updateMarket(people));
        double level = m.getUsPriceLevel();
        double sqFt = DRY * LandManager.SQ_FT_PER_KM2;
        List<LandParcel> real = new ArrayList<>();
        for (LandParcel p : m.getListing()) if (real.size() < 3 && !p.isMostlySea()) real.add(p);

        double[] mountainRow = relief(DRY, 0.1, 0, 0.9, 0);
        double flatPrice = priced(m, DRY, relief(DRY, 1, 0, 0, 0));
        double footPrice = Math.round(0.97 * flatPrice / 5) * 5.0;
        LandParcel mountain = place(real.get(0), DRY, mountainRow, priced(m, DRY, mountainRow));
        LandParcel valley = place(real.get(1), DRY, relief(DRY, 1, 0, 0, 0), flatPrice);
        LandParcel hill = place(real.get(2), DRY, relief(DRY, 0, 1, 0, 0), footPrice);
        List<LandParcel> listing = List.of(mountain, valley, hill);
        m.putOffers(listing);
        report("fixture: on the city's own shelf a mountain, a valley floor and a foothill offer of one dry area - the mountain cheapest, the foothill under the valley's price but dearer once prepared",
                mountain.getPriceUsd() < hill.getPriceUsd() && hill.getPriceUsd() < valley.getPriceUsd()
                        && m.priceWithPrepUsd(hill) > valley.getPriceUsd() && oldBest(m, listing) == mountain,
                String.format("%,.0f, %,.0f, %,.0f thousand; the foothill's with its pad %,.0f", mountain.getPriceUsd(), valley.getPriceUsd(),
                        hill.getPriceUsd(), m.priceWithPrepUsd(hill)));
        check("Game.bestOffer() for room takes the valley floor, not the mountain the old rule would buy",
                g.bestOffer(Game.LandNeed.room()) == valley && g.canAffordParcel(mountain));
        double lack = 0.5 * sqFt;
        check("...for a shortfall of half the offers' ground it takes the valley floor: the mountain's buildable ground is short of it, and the foothill's price with its pad is over the valley's",
                LandMarket.buildableSqFt(mountain) < lack && mountain.getSizeSqFt() >= lack && g.bestOffer(Game.LandNeed.shortfall(lack)) == valley);
        check("the auto-builder's land shortcut for the same lack takes the valley floor too", AutoBuilder.shortcutOffer(g, lack, Set.of()) == valley);
        check("...and for a lack no offer covers, the best value by buildable ground - the valley floor",
                AutoBuilder.shortcutOffer(g, 10 * sqFt, Set.of()) == valley);
        check("...with the valley floor passed over, the foothill offer (the best buildable ground a dollar left)",
                AutoBuilder.shortcutOffer(g, lack, Set.of(valley.getId())) == hill);
    }

    /** An offer on the real shelf's place and rectangle with ground by relief and no ore, at a price. */
    static LandParcel place(LandParcel at, double dry, double[] reliefKm2, double priceUsd) {
        return new LandParcel(at.getId(), at.getSide(), at.getPlace(), at.getLevel(), at.getX0(), at.getY0(), at.getX1(), at.getY1(),
                areas(dry, 0, 0), reliefKm2, new int[CityLand.KINDS], new double[CityLand.KINDS], priceUsd, at.getListedMonth());
    }

    /* =====================================================================
       4. A BUSINESS BUYS EACH CLASS
       ===================================================================== */

    @SuppressWarnings("deprecation")
    static void setGoing(LandManager lm, double going) { lm.setPricePerSqFt(going); }

    /** The plot's parts times the class prices: what a plot of these square feet on {flat, foothill, slope} costs. */
    static double sum(LandManager lm, double[] parts) {
        return parts[0] * lm.getPricePerSqFt() + parts[1] * lm.getFoothillPricePerSqFt() + parts[2] * lm.getSteepPricePerSqFt();
    }

    static void aBusinessBuysEachClass() {
        out.println("\n--- 4. a business pays the city for its plot class by class: buildings flat first, works steep first, the zone's share on each ---");
        Game g = city("landclass-4", 0.4, 0.3, 0.3, 0);
        LandManager lm = g.getLandManager();
        ironSites(g, 3);
        BuildingsTemplate house = template(g, HOUSE), mine = g.getBuildingManager().getTemplate(Mining.IRON_MINE);
        String houses = house.getSector();
        double base = lm.getMarket().getBasePricePerSqFt(), prep = lm.getPreparationPerSqFt();
        double going = base + 4 * prep;
        setGoing(lm, going);
        double flat0 = lm.getFlatSqFt(), foothill0 = lm.getFoothillSqFt(), slope0 = lm.getSlopeSqFt();
        report("fixture: 40% flat, 30% foothill, 30% steep owned, all free, the business price four preparations over the base - flat dearer than foothill dearer than steep",
                Math.abs(flat0 / lm.getOwnedSqFt() - 0.4) <= 1e-6 && Math.abs(foothill0 / lm.getOwnedSqFt() - 0.3) <= 1e-6 && lm.getAllocatedSqFt() == 0
                        && lm.getPricePerSqFt() > lm.getFoothillPricePerSqFt() && lm.getFoothillPricePerSqFt() > lm.getSteepPricePerSqFt(),
                String.format("flat %.6f, foothill %.6f, steep %.6f a sq ft", lm.getPricePerSqFt(), lm.getFoothillPricePerSqFt(), lm.getSteepPricePerSqFt()));
        check("the classes' prices are the rule's, read at the city's own base: foothill the going price less the preparation, steep the base",
                lm.getFoothillPricePerSqFt() == going - prep && lm.getSteepPricePerSqFt() == base && lm.getPricePerSqFt() == going);
        check("...and reliefPricePerSqFt() answers the three by class, very steep as steep",
                lm.reliefPricePerSqFt(World.FLAT) == going && lm.reliefPricePerSqFt(World.FOOTHILL) == lm.getFoothillPricePerSqFt()
                        && lm.reliefPricePerSqFt(World.STEEP) == base && lm.reliefPricePerSqFt(World.VERY_STEEP) == base);

        // The preparation the price reads is the quote's.
        lm.setAllocatedSqFt(flat0);
        Game.BuildQuote q = g.quoteBuild(house, 3);
        report("the foothill's preparation the price takes off is the quote's own: a House order onto foothill is charged exactly that a square foot",
                q.prepSqFt > 0 && q.prepCost > 0 && Math.abs(q.prepCost / q.prepSqFt - prep) <= 1e-9 * prep
                        && Math.abs(g.preparationCost(g.preparationPoints(1)) - prep) <= 1e-12 * prep,
                String.format("%.9f a sq ft quoted, %.9f in the price", q.prepCost / q.prepSqFt, prep));
        lm.setAllocatedSqFt(0);

        double s = 0.1 * flat0;
        // A building, flat free: the flat price.
        double[] onFlat = lm.plotByRelief(s, false);
        check("a building allocated while flat is free is all flat ground", Math.abs(onFlat[0] - s) <= TOL * s && zero(onFlat[1], s) && zero(onFlat[2], s));
        close("...and pays the flat price", lm.priceFor(s, houses), s * going, 1e-12);

        g.getEconomyManager().setSectorCash(Sectors.REAL_ESTATE, BUSINESS_CASH);
        double each = house.getLandSqFt();
        double sold0 = lm.getLandSalesThisMonth();
        boolean[] ordered = new boolean[1];
        quietly(() -> ordered[0] = g.buildFor(g.getSectorInvestor(Sectors.REAL_ESTATE), house, 1));
        close("a business's House order (buildFor()) pays the city the flat price for its plot", lm.getLandSalesThisMonth() - sold0, each * going, 1e-12);
        check("...and the order went through", ordered[0]);

        // Flat used: the foothill price.
        lm.setAllocatedSqFt(flat0);
        double[] onFoothill = lm.plotByRelief(s, false);
        check("once the flat is used, a building's plot is all foothill", zero(onFoothill[0], s) && Math.abs(onFoothill[1] - s) <= TOL * s && zero(onFoothill[2], s));
        close("...and pays the foothill price - the going price less the preparation", lm.priceFor(s, houses), s * (going - prep), 1e-12);
        sold0 = lm.getLandSalesThisMonth();
        quietly(() -> ordered[0] = g.buildFor(g.getSectorInvestor(Sectors.REAL_ESTATE), house, 1));
        close("a business's House order then pays the foothill price for its plot", lm.getLandSalesThisMonth() - sold0, each * (going - prep), 1e-12);

        // A plot straddling flat and foothill.
        lm.setAllocatedSqFt(flat0 - 0.4 * s);
        double[] straddle = lm.plotByRelief(s, false);
        report("a plot of a building that straddles the end of the flat is 40% flat and 60% foothill, and the parts add up to the plot",
                Math.abs(straddle[0] - 0.4 * s) <= TOL * s && Math.abs(straddle[1] - 0.6 * s) <= TOL * s && zero(straddle[2], s)
                        && Math.abs(straddle[0] + straddle[1] + straddle[2] - s) <= TOL * s,
                String.format("%,.2f flat, %,.2f foothill of %,.2f", straddle[0], straddle[1], s));
        close("...and pays the sum: its flat at the flat price, its foothill at the foothill price", lm.priceFor(s, houses),
                0.4 * s * going + 0.6 * s * (going - prep), 1e-12);

        // A works: steep first.
        lm.setAllocatedSqFt(0);
        lm.restoreWorks(0);
        double w = 0.1 * slope0;
        double[] works = lm.plotByRelief(w, true);
        check("a works's plot on a city with idle steep ground is all steep", zero(works[0], w) && zero(works[1], w) && Math.abs(works[2] - w) <= TOL * w);
        close("...and pays the steep price - the base price", lm.priceFor(w, mine.getSector(), true), w * base, 1e-12);
        close("...while a building of the same size would pay the flat price", lm.priceFor(w, houses, false), w * going, 1e-12);
        g.getEconomyManager().setSectorCash(mine.getSector(), BUSINESS_CASH);
        double mineEach = mine.getLandSqFt();
        sold0 = lm.getLandSalesThisMonth();
        boolean[] mined = new boolean[1];
        quietly(() -> mined[0] = g.buildFor(g.getSectorInvestor(mine.getSector()), mine, 1));
        report("a business's Iron Mine order pays the city the steep price for its plot",
                mined[0] && Math.abs(lm.getLandSalesThisMonth() - sold0 - mineEach * base) <= 1e-12 * mineEach * base,
                String.format("%,.6f paid, %,.6f expected", lm.getLandSalesThisMonth() - sold0, mineEach * base));
        lm.setAllocatedSqFt(0);
        lm.restoreWorks(0);

        // The works' plot straddling the end of the steep, and past it.
        lm.setAllocatedSqFt(slope0 - 0.3 * w);
        lm.restoreWorks(slope0 - 0.3 * w);
        double[] worksOver = lm.plotByRelief(w, true);
        report("a works's plot that straddles the end of the steep is 30% steep and 70% foothill",
                Math.abs(worksOver[2] - 0.3 * w) <= TOL * w && Math.abs(worksOver[1] - 0.7 * w) <= TOL * w && zero(worksOver[0], w),
                String.format("%,.2f steep, %,.2f foothill, %,.2f flat of %,.2f", worksOver[2], worksOver[1], worksOver[0], w));
        close("...and pays the sum, its steep at the base and its foothill at the foothill price", lm.priceFor(w, mine.getSector(), true),
                0.3 * w * base + 0.7 * w * (going - prep), 1e-12);
        lm.setAllocatedSqFt(slope0);
        lm.restoreWorks(slope0);
        close("once the steep is used a works pays the foothill price", lm.priceFor(w, mine.getSector(), true), w * (going - prep), 1e-12);
        lm.setAllocatedSqFt(slope0 + foothill0);
        lm.restoreWorks(slope0 + foothill0);
        close("...and when the foothill is used too, the flat price", lm.priceFor(w, mine.getSector(), true), w * going, 1e-12);
        lm.setAllocatedSqFt(0);
        lm.restoreWorks(0);

        zoneShare(lm, going, prep);
        buyback(lm, houses, mine, going, prep, base);
    }

    static void zoneShare(LandManager lm, double going, double prep) {
        double flat0 = lm.getFlatSqFt();
        double share = 0.5;
        String sector = Sectors.RETAIL;
        lm.getZoning().set(sector, Zoning.Size.SHARE, 0.05, share);
        check("a zone's price share applies to each class's price: the zone's flat, foothill and steep ground are sold at the share of those prices",
                lm.groundPriceFor(sector) == share * going && lm.groundPriceFor(sector, World.FLAT) == share * going
                        && lm.groundPriceFor(sector, World.FOOTHILL) == share * (going - prep)
                        && lm.groundPriceFor(sector, World.STEEP) == share * lm.getSteepPricePerSqFt()
                        && lm.groundPriceFor("Industry", World.FOOTHILL) == going - prep);

        double s = 0.02 * lm.getOwnedSqFt();
        lm.setAllocatedSqFt(flat0 - 0.4 * s);
        double held = lm.heldBy(sector);
        report("fixture: a zone holds more ground than a plot of " + String.format("%,.0f", s) + " sq ft, and the plot straddles the end of the flat", held > s && held > 0,
                String.format("holds %,.0f sq ft", held));
        double[] parts = lm.plotByRelief(s, false);
        close("a plot wholly in the zone pays the share of the sum of its classes' prices", lm.priceFor(s, sector), share * sum(lm, parts), 1e-12);

        double big = 3 * held;
        lm.setAllocatedSqFt(flat0 - 0.4 * big);
        double heldNow = lm.heldBy(sector);
        double[] bigParts = lm.plotByRelief(big, false);
        double f = Math.min(big, heldNow) / big;
        report("a plot larger than the zone holds pays the share on the part the zone holds and the full price on the rest, class by class",
                heldNow > 0 && heldNow < big, String.format("holds %,.0f of %,.0f sq ft", heldNow, big));
        close("...the sum of its classes' prices, the share on the zone's part of each", lm.priceFor(big, sector),
                sum(lm, bigParts) * ((1 - f) + f * share), 1e-12);
        check("...which is between the share's sum and the full one", lm.priceFor(big, sector) < sum(lm, bigParts) && lm.priceFor(big, sector) > share * sum(lm, bigParts));
        lm.getZoning().remove(sector);
        lm.setAllocatedSqFt(0);
    }

    static void buyback(LandManager lm, String houses, BuildingsTemplate mine, double going, double prep, double base) {
        double flat0 = lm.getFlatSqFt(), slope0 = lm.getSlopeSqFt();
        double s = 0.1 * flat0;

        lm.setAllocatedSqFt(flat0 + 0.5 * s);
        lm.restoreWorks(0);
        double[] released = lm.releasedByRelief(s, false);
        report("a building's plot given back comes off the foothill first: half foothill, half flat when half of it stands past the flat",
                Math.abs(released[1] - 0.5 * s) <= TOL * s && Math.abs(released[0] - 0.5 * s) <= TOL * s && zero(released[2], s)
                        && Math.abs(released[0] + released[1] + released[2] - s) <= TOL * s,
                String.format("%,.2f flat, %,.2f foothill of %,.2f", released[0], released[1], s));
        double bought = lm.buybackPriceFor(s, houses);
        close("...and is bought back at the sum of its classes' prices", bought, 0.5 * s * going + 0.5 * s * (going - prep), 1e-12);
        lm.setAllocatedSqFt(flat0 + 0.5 * s - s);
        close("...which is what the same plot would be sold at into the ground it leaves: the buyback is the sale's reverse", lm.priceFor(s, houses), bought, 1e-12);

        lm.setAllocatedSqFt(slope0 + 0.5 * s);
        lm.restoreWorks(slope0 + 0.5 * s);
        double[] worksBack = lm.releasedByRelief(s, true);
        report("a works's plot given back, standing half on the steep and half past it, returns half steep and half foothill",
                Math.abs(worksBack[2] - 0.5 * s) <= TOL * s && Math.abs(worksBack[1] - 0.5 * s) <= TOL * s && zero(worksBack[0], s),
                String.format("%,.2f flat, %,.2f foothill, %,.2f steep", worksBack[0], worksBack[1], worksBack[2]));
        double worksBought = lm.buybackPriceFor(s, mine.getSector(), true);
        close("...and is bought back at the base and the foothill price", worksBought, 0.5 * s * base + 0.5 * s * (going - prep), 1e-12);
        lm.setAllocatedSqFt(slope0 + 0.5 * s - s);
        lm.restoreWorks(slope0 + 0.5 * s - s);
        close("...the same as selling it again into what it leaves", lm.priceFor(s, mine.getSector(), true), worksBought, 1e-12);

        // The zone's share on the buyback.
        String sector = Sectors.RETAIL;
        double share = 0.5;
        lm.getZoning().set(sector, Zoning.Size.SHARE, 0.05, share);
        lm.setAllocatedSqFt(flat0 + 0.5 * s);
        lm.restoreWorks(0);
        lm.getZoning().addUsed(sector, s);
        double[] back = lm.releasedByRelief(s, false);
        close("a plot given back out of its zone's used ground is bought back at the share of its classes' prices", lm.buybackPriceFor(s, sector),
                share * sum(lm, back), 1e-12);
        lm.getZoning().remove(sector);
        lm.setAllocatedSqFt(0);
        lm.restoreWorks(0);
    }

    /* =====================================================================
       5. FLAT WORLDS UNCHANGED
       ===================================================================== */

    static void flatWorldsUnchanged() {
        out.println("\n--- 5. a city of flat ground is priced exactly as before, to the bit ---");
        Game g = city("landclass-5", 1, 0, 0, 0);
        LandManager lm = g.getLandManager();
        LandMarket m = lm.getMarket();
        long people = Math.round(crowdingFor(40) * LandManager.km2(lm.getOwnedSqFt()));
        quietly(() -> lm.updateMarket(people));
        check("fixture: the city owns neither foothill nor steep ground", lm.getFoothillSqFt() == 0 && lm.getSlopeSqFt() == 0);

        // The offers' prices.
        quietly(m::remeasureStanding);
        boolean priceSame = true, rateSame = true;
        int n = 0;
        for (LandParcel p : m.getListing()) {
            n++;
            double[] km2 = new double[CityLand.AREAS], ore = new double[CityLand.KINDS];
            for (int a = 0; a < CityLand.AREAS; a++) km2[a] = p.getKm2(a);
            for (Resource r : Resource.values()) ore[r.ordinal()] = p.getAmount(r);
            double old = LandMarket.price(km2, ore, m.getGroundUsdPerSqFt(), m.getUsPriceLevel());
            priceSame &= same(p.getPriceUsd(), old) && same(LandMarket.price(km2, p.reliefRow(), ore, m.getGroundUsdPerSqFt(), m.getUsPriceLevel()), old)
                    && same(LandMarket.price(km2, null, ore, m.getGroundUsdPerSqFt(), m.getUsPriceLevel()), old);
            rateSame &= same(m.buildableKm2PerUsd(p), p.getDryKm2PerUsd()) && LandMarket.buildableSqFt(p) == p.getSizeSqFt()
                    && m.priceWithPrepUsd(p) == p.getPriceUsd();
        }
        check("fixture: the office has its offers standing", n > 6);
        check("every offer standing, measured again: its price is the four-argument price() of its ground, with its relief row and with none", priceSame);
        check("...its buildable km2 a dollar is its dry km2 a dollar, its buildable square feet its size, its price with the pad its price - to the bit", rateSame);
        List<LandParcel> listing = m.getListing();
        check("bestValue() is the old rule's pick", m.bestValue() == oldBest(m, listing));
        LandParcel oldGame = null;
        for (LandParcel p : listing) {
            if (p.isMostlySea() || !g.canAffordParcel(p)) continue;
            if (oldGame == null || p.getDryKm2PerUsd() > oldGame.getDryKm2PerUsd()
                    || (p.getDryKm2PerUsd() == oldGame.getDryKm2PerUsd() && m.nearer(p, oldGame))) oldGame = p;
        }
        check("...and Game.bestOffer() for room is the old loop's", oldGame != null && g.bestOffer(Game.LandNeed.room()) == oldGame);
        double lack = 0;
        for (LandParcel p : listing) if (lack == 0 && LandMarket.bareGround(p) && p.getSizeSqFt() > 0) lack = 0.5 * p.getSizeSqFt();
        LandParcel oldShort = null;
        for (LandParcel p : listing) {
            if (p.getSizeSqFt() < lack || !LandMarket.bareGround(p)) continue;
            if (oldShort == null || p.getPriceUsd() < oldShort.getPriceUsd()
                    || (p.getPriceUsd() == oldShort.getPriceUsd() && m.nearer(p, oldShort))) oldShort = p;
        }
        check("...and for a shortfall the old loop's cheapest that covers it", oldShort != null && g.bestOffer(Game.LandNeed.shortfall(lack)) == oldShort);

        // What a business pays.
        String sector = Sectors.REAL_ESTATE;
        boolean plain = true, works = true, plot = true;
        double owned = lm.getOwnedSqFt();
        for (double s : new double[] { 1, 12_345.678, 0.05 * owned }) {
            double old = s * lm.getPricePerSqFt();
            plain &= same(lm.priceFor(s, sector), old) && same(lm.priceFor(s), old);
            works &= same(lm.priceFor(s, sector, true), old) && same(lm.priceFor(s, sector, false), old)
                    && same(lm.buybackPriceFor(s, sector), old) && same(lm.buybackPriceFor(s, sector, true), old);
            double[] parts = lm.plotByRelief(s, false), worksParts = lm.plotByRelief(s, true);
            plot &= parts[0] == s && parts[1] == 0 && parts[2] == 0 && worksParts[0] == s && worksParts[1] == 0 && worksParts[2] == 0;
        }
        check("a plot's price, sold and bought back, with and without a works, is its square feet at the going price: the old expression, to the bit", plain && works);
        check("...and the plot falls wholly on flat, whatever it is for", plot);
        check("the classes' prices of a flat city are still defined, and none dearer than flat", lm.getFoothillPricePerSqFt() <= lm.getPricePerSqFt()
                && lm.getSteepPricePerSqFt() <= lm.getPricePerSqFt());

        String zoned = Sectors.RETAIL;
        lm.getZoning().set(zoned, Zoning.Size.SHARE, 0.05, 0.5);
        boolean zoneSame = true;
        double held = lm.heldBy(zoned);
        for (double s : new double[] { 0.5 * held, 3 * held }) {
            double inZone = Math.min(s, lm.heldBy(zoned));
            double old = (s - inZone) * lm.getPricePerSqFt() + inZone * lm.groundPriceFor(zoned);
            zoneSame &= inZone > 0 && same(lm.priceFor(s, zoned), old) && same(lm.priceFor(s, zoned, true), old);
        }
        lm.getZoning().addUsed(zoned, 0.25 * held);
        for (double s : new double[] { 0.1 * held, 2 * held }) {
            double inZone = Math.min(s, lm.getZoning().used(zoned));
            double old = (s - inZone) * lm.getPricePerSqFt() + inZone * lm.groundPriceFor(zoned);
            zoneSame &= inZone > 0 && same(lm.buybackPriceFor(s, zoned), old) && same(lm.buybackPriceFor(s, zoned, true), old);
        }
        check("with a zone, the plot sold and bought back is priced by the old expression, to the bit", zoneSame);
        lm.getZoning().remove(zoned);
    }
}
