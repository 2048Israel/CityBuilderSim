package ham.citybuildersim;

import ham.citybuildersim.sectors.CoalMining;
import ham.citybuildersim.sectors.CopperRefining;
import ham.citybuildersim.sectors.ExtractionSector;
import ham.citybuildersim.sectors.Forestry;
import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.ProcessingSector;
import ham.citybuildersim.sectors.Quarrying;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Each materials industry's month as a picture (0.7.141, batch I11;
 * docs/studies/spec-materials.md 2.13): where what it worked came from - its
 * fields by type, filled to what remains, or the forest; its own works, the
 * other industries, the city's old metal, the world - each kind of its
 * buildings with its run, what the last one standing earns and the gate one
 * more would fail, the products it made and who took them, the power its
 * works made, drew and sold, and Forestry's forest: where it settles and the
 * line its planting starts at. The figures, their words and their colours,
 * for each of the nine industries.
 *
 * WHY. The materials chains (batches I1 to I10) gave the city nine
 * industries - Iron Mining, Iron & Steel, Coal Mining, Quarrying, Cement &
 * Concrete, Copper Mining, Copper Refining, Forestry and Wood Products - and
 * their Operations pages draw them as every business is drawn (SectorFlow):
 * goods in, a ring of the rate, goods out. None of what the chains are made
 * of shows there: not the fields by type and what is left in them, not the
 * coke an oven hands its own furnace, not the old steel a yard buys, not why
 * a works stands idle or why no second one is built, not the power a pulp
 * mill sells, not where the forest settles. The refinery has had a picture
 * since 0.7.95 (RefineryView, O11); this is the same for the nine, its
 * figures held by a harness (IndustryViewCheck) rather than worked out in
 * the screen. Since 0.7.143 (batch I12) IndustryPicture lays them out and
 * ui/SectorScreen's OPERATIONS paints that, a roof per industry.
 *
 * WHICH MONTH. The month the production rows were made on (Sector.Output,
 * Sector.Input): what each good made, kept for its own works (captive), sold
 * at home and shipped, and what each feed was bought at home and abroad -
 * who sold it and who bought it off the market's own fills of the month
 * (GoodsMarket.getTrades()). The rows and the fills are not saved, so after
 * a load nothing of the month is counted until one runs (counted(),
 * Game.hasMonthRun() - SectorFlow's rule). The ground, the forest, the old
 * metal's last month, the plan's appraisals and the power are read as they
 * stand.
 *
 * Every figure is a model getter's or a sum of them: a unit's appraisal is
 * its sector's own (ProcessingSector.appraise(), ExtractionSector.appraise()
 * - the plan's gates, in SpreadPlanner's words), the last one's earnings the
 * idle book's (ProcessingSector.lastEarns()), the forest's settling point
 * Forestry.settlesAt()'s.
 *
 * Pure: it reads the city and changes nothing; the rows are read without
 * making any (Sector.outputRow(), inputRow()).
 */
public final class IndustryView {

    private IndustryView() { }

    /* =====================================================================
       THE FIGURES
       ===================================================================== */

    /** The nine industries the view draws, in Sectors.KEYS' order. */
    public static final List<String> INDUSTRIES = List.of(Sectors.HEAVY_INDUSTRY, Sectors.MINING, Sectors.COAL_MINING,
            Sectors.QUARRYING, Sectors.CEMENT_CONCRETE, Sectors.COPPER_MINING, Sectors.COPPER_REFINING, Sectors.FORESTRY,
            Sectors.WOOD_PRODUCTS);

    /** Whether a sector is one of the nine. */
    public static boolean covers(Sector s) {
        return s != null && INDUSTRIES.contains(s.key());
    }

    /** The roof an industry's works are drawn under (the spec's 2.13: furnace, mill, kiln, pit) - and ★ the crews' camp, Forestry's: a forest has no pit. */
    public enum Roof { PIT, CAMP, FURNACE, KILN, MILL }

    /** An industry's roof: the four that dig a pit's, Forestry's camp, the smelters' furnace, the kilns', the mills'. */
    public static Roof roofOf(Sector s) {
        if (s == null) return null;
        return switch (s.key()) {
            case Sectors.FORESTRY -> Roof.CAMP;
            case Sectors.HEAVY_INDUSTRY, Sectors.COPPER_REFINING -> Roof.FURNACE;
            case Sectors.CEMENT_CONCRETE -> Roof.KILN;
            case Sectors.WOOD_PRODUCTS -> Roof.MILL;
            default -> Roof.PIT;
        };
    }

    /** Where a part of what an industry works came from, or where a part of what it made went. */
    public enum Origin {
        /** Its own ground: a field's ore, coal, stone or copper, the forest's wood. */
        FIELDS,
        /** Its own works: a good it makes and uses, to its own need first (Sector, CAPTIVE USE). */
        CAPTIVE,
        /** Bought, or sold, in the city: another industry, the households, the city. */
        HOME,
        /** The city's old steel or old copper (OldMetal), which is not a good. */
        OLD_METAL,
        /** Carried into another of its goods: the limestone no buyer took, into Quarrying's aggregate (Quarrying, THE CARRY). */
        CARRIED,
        /** Landed from the world, or shipped to it. */
        WORLD
    }

    /**
     * A field type's ground, drawn as a vessel filled to what remains.
     *
     * @param resource  the resource
     * @param type      the type (LandManager, THE GROUND BY TYPE); NONE for a resource of one type
     * @param sites     the sites of the type the city owns (LandManager.getSites(r, type)); 0 for the forest
     * @param worked    ...the sites the buildings on the type take, standing, on site or ordered (Game.committedOn(r, type))
     * @param onAny     ...and those the buildings on ANY take of it, where the packing places them (LandManager.packed()
     *                  of Game.claimsOn(r)): Quarrying's quarries on limestone or hard rock
     * @param owned     what the city owns of it, as listed (getOwnedAmount(r, type)) - the vessel
     * @param remaining ...and what remains in it (getRemaining(r, type)) - the fill
     */
    public record Vessel(Resource resource, Deposit.FieldType type, int sites, int worked, int onAny, double owned,
                         double remaining) {
        /** The share of the vessel still in the ground; 0 with none owned. */
        public double filled() { return owned > 0 ? Math.min(1, remaining / owned) : 0; }
        /** What has been taken out of it. */
        public double taken() { return Math.max(0, owned - remaining); }
        /** The sites its works take, on its type and on ANY. */
        public int taking() { return worked + onAny; }
    }

    /**
     * Forestry's forest (the spec's 2.11; Q13).
     *
     * @param owned           the forest's full stock, cubic metres (LandManager.getOwnedAmount(FOREST))
     * @param standing        ...what stands (getRemaining(FOREST))
     * @param protectedWood   ...of it what the law keeps standing (getProtectedWood())
     * @param cuttable        ...and what the crews may cut (cuttableForest())
     * @param cleared         the wood cleared for building, never regrowing (getForestClearedM3())
     * @param cut             the crews' cut this month (Forestry.getCutThisMonth())
     * @param nameplate       ...what the crews standing cut at nameplate (crewsCut(false))
     * @param committed       ...and with those on site (crewsCut(true)) - E's "every crew"
     * @param salvage         the salvage cut sold this month (getSalvageThisMonth())
     * @param regrowth        what the month's end gives back (LandManager.regrowthDue() at the stands' growth)
     * @param plantingLine    the regrowth the planting test weighs the cut against, the stands on site counted as grown
     *                        (regrowthDue(plantedGrowth(true)), Forestry.plant()'s): past it a stand is planted
     * @param plantedGrowth   the planted stands' growth a month (plantedGrowth(false))
     * @param protectedShare  the law's protected share (Zoning.protectedShare())
     * @param settlesWithLaw  where the forest settles, the share of its full stock standing in the long run (Forestry.settlesAt())
     * @param settlesWithout  ...with no law: the depletion the regrowth rule settles at, 240 times the cut (settlesAt(..., 0))
     * @param plantations     Tree Plantations standing
     * @param woodlots        Woodlots standing
     * @param planting        what the month's planting did, in its words (getPlantingWord())
     */
    public record Forest(double owned, double standing, double protectedWood, double cuttable, double cleared,
                         double cut, double nameplate, double committed, double salvage, double regrowth,
                         double plantingLine, double plantedGrowth, double protectedShare, double settlesWithLaw,
                         double settlesWithout, int plantations, int woodlots, String planting) {
        /** Months the forest the crews may cut lasts every crew's cut, on site too; infinite with no crew. */
        public double lastsMonths() { return committed > 0 ? cuttable / committed : Double.POSITIVE_INFINITY; }
        /** Whether the month's cut is past the planting line. */
        public boolean pastPlantingLine() { return cut > plantingLine; }
    }

    /**
     * A part of a feed by where it came from, or of a product by where it went.
     *
     * @param origin where
     * @param party  who: a sector's key, Trade.HOUSEHOLDS or Trade.CITY for HOME; null otherwise
     * @param units  how much
     */
    public record Part(Origin origin, String party, double units) { }

    /**
     * The city's old metal an industry buys (OldMetal; the spec's 2.9): the
     * steel Iron & Steel's yards take, the copper Copper Refining's scrap
     * refineries take, the pool's last month.
     *
     * @param metal    Good.STEEL or Good.COPPER, as a template's oldMetal() names it
     * @param arose    tonnes that arose, recovered (Pool.lastArisen())
     * @param bySource ...by where it came from
     * @param bought   ...the buyers took (lastBought())
     * @param landfill ...and the rest, to landfill (lastLandfill())
     * @param price    what they paid a tonne (lastPrice())
     * @param intake   what the buyers standing take a month at nameplate
     */
    public record Metal(Good metal, double arose, Map<OldMetal.Source, Double> bySource, double bought, double landfill,
                        double price, double intake) { }

    /**
     * A good an industry's works use, by where it came from.
     *
     * @param good     the good
     * @param needed   what its works needed of it at the clearing's rate (Input.needed), its own captive part in it
     * @param captive  made by its own works and used by them (Output.captive)
     * @param oldMetal the city's old metal taken for it (Copper Refining's old copper, for its scrap)
     * @param home     bought in the city (Input.boughtLocal)
     * @param imported landed (Input.imported)
     * @param lorries  of what it bought, the litres its lorries burned (Sector.getFleetDieselLitres()), for DIESEL
     * @param sellers  what it bought in the city, by seller, from the month's fills
     */
    public record Feed(Good good, double needed, double captive, double oldMetal, double home, double imported, double lorries,
                       List<Part> sellers) {
        /** Everything its works received of it: their own, the old metal, bought at home and landed. */
        public double received() { return captive + oldMetal + home + imported; }
        /** ...as tonnes. */
        public double tonnes() { return received() * good.tonnesPerUnit(); }
        /** Its parts in drawing order: its own works, the old metal, each seller in the city, the world. */
        public List<Part> parts() {
            List<Part> out = new ArrayList<>();
            if (captive > 0) out.add(new Part(Origin.CAPTIVE, null, captive));
            if (oldMetal > 0) out.add(new Part(Origin.OLD_METAL, null, oldMetal));
            out.addAll(sellers);
            if (imported > 0) out.add(new Part(Origin.WORLD, null, imported));
            return out;
        }
    }

    /**
     * A good an industry makes, and where it went.
     *
     * @param good       the good
     * @param made       made this month (Output.produced + exportBound + captive) - for Quarrying's limestone with what it
     *                   carried into aggregate, for its aggregate without
     * @param captive    used by its own works (Output.captive)
     * @param home       sold in the city (Output.soldLocal)
     * @param exported   shipped (Output.exported)
     * @param carriedIn  carried in from another of its goods: the limestone Quarrying's aggregate took (Quarrying.carried())
     * @param carriedOut ...and carried out: the limestone carried into aggregate
     * @param lost       lost: a flow good nobody took that could not ship (Output.idled), stock past the warehouse (writtenOff)
     * @param idled      nameplate not made because the warehouse held enough (Output.idled, a stockable good's)
     * @param stock      in its warehouse now (getStock()); 0 for a flow good
     * @param buyers     what it sold in the city, by buyer, from the month's fills
     */
    public record Product(Good good, double made, double captive, double home, double exported, double carriedIn,
                          double carriedOut, double lost, double idled, double stock, List<Part> buyers) {
        /** Into the warehouse this month (below nothing: out of it): made and carried in, less every way it left. */
        public double intoStock() { return made + carriedIn - captive - home - exported - carriedOut - lost; }
        /** Made, as tonnes. */
        public double tonnes() { return made * good.tonnesPerUnit(); }
        /** Its parts in drawing order: to its own works, each buyer in the city, carried into another of its goods, the world. */
        public List<Part> parts() {
            List<Part> out = new ArrayList<>();
            if (captive > 0) out.add(new Part(Origin.CAPTIVE, null, captive));
            out.addAll(buyers);
            if (carriedOut > 0) out.add(new Part(Origin.CARRIED, null, carriedOut));
            if (exported > 0) out.add(new Part(Origin.WORLD, null, exported));
            return out;
        }
    }

    /**
     * One kind of an industry's buildings.
     *
     * @param template   the building
     * @param standing   finished (BuildingManager.getQuantity())
     * @param onSite     on site (getUnderConstructionById())
     * @param rate       the share of nameplate its sector's plant runs at (getOperatingRate())
     * @param feedShare  ...of that, the share its feed or ground let it run: a Scrap Yard's old steel bought, a crew's forest
     *                   cut, a wash plant's raw coal washed, a mine over a worked-out type nothing; 1 for every other
     * @param workedOut  a mine whose type is worked out (ExtractionSector.isWorkedOut())
     * @param lastEarns  what the last one standing earns a month as the idle book counts it (ProcessingSector.lastEarns());
     *                   NaN for a mine, or with none standing
     * @param idleMonths months its kind has stood idle running (ProcessingSector.idleMonths())
     * @param growth     a planted stand's growth a month, cubic metres (Forestry.growthOf() times those standing)
     * @param oneMore    one more of it weighed by its sector's plan, every gate asked (ProcessingSector.appraise(),
     *                   ExtractionSector.appraise()); null for a building the plan does not weigh (a planted stand)
     */
    public record Unit(BuildingsTemplate template, int standing, int onSite, double rate, double feedShare, boolean workedOut,
                       double lastEarns, int idleMonths, double growth, SpreadPlanner.Candidate oneMore) {
        /** The share of nameplate it ran at this month: its sector's rate times its feed's share. */
        public double run() { return rate * feedShare; }
        /** Units of a good its buildings standing make at its run. */
        public double makes(Good g) { return template.makes(g) * standing * run(); }
        /** ...and use. */
        public double uses(Good g) { return template.uses(g) * standing * run(); }
        /** Tonnes of goods and old metal into its buildings standing at its run. */
        public double tonnesIn() {
            double t = 0;
            for (Map.Entry<Good, Double> e : template.goodsUsed().entrySet()) t += e.getValue() * e.getKey().tonnesPerUnit();
            t += template.oldMetal(Good.STEEL) + template.oldMetal(Good.COPPER);
            return t * standing * run();
        }
        /** ...and tonnes of goods out. */
        public double tonnesOut() {
            double t = 0;
            for (Map.Entry<Good, Double> e : template.goodsMade().entrySet()) t += e.getValue() * e.getKey().tonnesPerUnit();
            return t * standing * run();
        }
        /** What a works' recipe loses on the way, tonnes: in less out - the slag, the gas, the water, the bark burned; NaN for a building on a site of the ground (a mine) or with no feed (a crew, a stand, a wash plant). */
        public double loss() { return template.site() == null && tonnesIn() > 0 ? tonnesIn() - tonnesOut() : Double.NaN; }
        /** Whether the plan weighs one more of it. */
        public boolean weighed() { return oneMore != null; }
        /** What one more would earn a month on its cost (the plan's score); NaN for one it does not weigh. */
        public double earnsOnCost() { return oneMore == null ? Double.NaN : oneMore.score(); }
        /** The first gate one more fails; null when it passes them all, or is not weighed. */
        public SpreadPlanner.Gate gate() { return oneMore == null ? null : oneMore.failed(); }
    }

    /**
     * The power an industry's works make, draw and trade with the grid,
     * kW at nameplate (Sector, THE POWER ITS WORKS MAKE THEMSELVES).
     *
     * @param made      what they make (Sector.getPowerMade())
     * @param drawn     what they draw (getPowerDrawn())
     * @param bought    what they draw from the grid past what they make (getGridDraw())
     * @param sold      what they make past their draws, sold to the grid (getPowerSurplus())
     * @param soldValue ...and what it sells for a month at the tariff (getPowerSoldValue())
     */
    public record Power(double made, double drawn, double bought, double sold, double soldValue) {
        /** Whether its works make any power. */
        public boolean makes() { return made > 0; }
    }

    /**
     * The lorries an industry keeps for its tonnes (Sector, THE FLEET).
     *
     * @param vans   vans it owns (vanFleet())
     * @param needed ...it needs for its plant (vansNeeded())
     * @param diesel litres they burned this month (getFleetDieselLitres())
     */
    public record Lorries(double vans, double needed, double diesel) { }

    /**
     * An industry's month.
     *
     * @param sector    the industry
     * @param roof      its roof (roofOf())
     * @param counted   whether the month's rows and fills are this month's: false after a load until a month runs
     * @param rate      its plant's rate (getOperatingRate())
     * @param standing  its buildings standing (buildingsStanding())
     * @param onSite    ...and on site (buildingsOnSite())
     * @param fields    its ground by type, an extraction industry's - the forest as one vessel; empty for a works
     * @param forest    Forestry's forest; null for the rest
     * @param oldMetal  the old metal it buys, Iron & Steel's and Copper Refining's; null for the rest
     * @param wash      Coal Mining's coal through its wash plants, its lift as the ground stands now (CoalMining.lift());
     *                  null for the rest
     * @param carried   Quarrying's limestone carried into aggregate this month (Quarrying.carried()); 0 for the rest
     * @param feeds     the goods its works use, in its declaration's order
     * @param units     its kinds of building, in the catalogue's order
     * @param products  the goods it makes, in its declaration's order
     * @param power     its works' power
     * @param lorries   its lorries
     */
    public record View(Sector sector, Roof roof, boolean counted, double rate, int standing, int onSite, List<Vessel> fields,
                       Forest forest, Metal oldMetal, CoalMining.Flow wash, double carried, List<Feed> feeds, List<Unit> units,
                       List<Product> products, Power power, Lorries lorries) {
        /** Whether there is a month to draw: a building standing, and the month counted. */
        public boolean drawn() { return standing > 0 && counted; }
        /** A feed, or null for a good its works neither use nor bought. */
        public Feed feed(Good g) {
            for (Feed f : feeds) if (f.good() == g) return f;
            return null;
        }
        /** A product, or null for a good it has no plant for, made none of and holds none of. */
        public Product product(Good g) {
            for (Product p : products) if (p.good() == g) return p;
            return null;
        }
        /** A kind of building by its permanent id, or null. */
        public Unit unit(int id) {
            for (Unit u : units) if (u.template().getId() == id) return u;
            return null;
        }
        /** The kinds with a building standing or on site: what the picture draws as works. */
        public List<Unit> built() {
            List<Unit> out = new ArrayList<>();
            for (Unit u : units) if (u.standing() > 0 || u.onSite() > 0) out.add(u);
            return out;
        }
        /** Of the kinds the plan weighs, the one it would order: passing every gate and earning most on its cost (SpreadPlanner.best()); null for none. */
        public Unit best() {
            List<SpreadPlanner.Candidate> all = new ArrayList<>();
            for (Unit u : units) if (u.oneMore() != null) all.add(u.oneMore());
            SpreadPlanner.Candidate b = SpreadPlanner.best(all);
            return b == null ? null : unit(b.template().getId());
        }
        /** Tonnes its works received, every feed. */
        public double tonnesIn() {
            double t = 0;
            for (Feed f : feeds) t += f.tonnes();
            return t;
        }
        /** Tonnes it made, every product. */
        public double tonnesMade() {
            double t = 0;
            for (Product p : products) t += p.tonnes();
            return t;
        }
    }

    /** The month of `sector` in `game` (pure); null for a sector that is not one of the nine. */
    public static View of(Game game, Sector sector) {
        if (game == null || !covers(sector)) return null;
        BuildingManager b = game.getBuildingManager();
        LandManager land = game.getLandManager();
        Markets markets = game.getMarkets();
        BusinessInvestment plans = game.getBusinessInvestment();
        boolean counted = game.hasMonthRun();
        double rate = sector.getOperatingRate();

        /* ----- the sources: the ground, the forest, the old metal ----- */
        List<Vessel> fields = new ArrayList<>();
        Forest forest = null;
        if (sector instanceof Forestry fo) {
            forest = forest(game, fo);
            fields.add(new Vessel(Resource.FOREST, Deposit.FieldType.NONE, 0, 0, 0, forest.owned(), forest.standing()));
        } else if (sector instanceof ExtractionSector ex) {
            Resource r = ex.resource();
            List<LandManager.Claim> claims = game.claimsOn(r);
            int[][] packed = r.hasTypes() ? land.packed(r, claims) : new int[0][0];
            List<Deposit.FieldType> types = r.typesOf();
            for (int k = 0; k < types.size(); k++) {
                Deposit.FieldType type = types.get(k);
                int sites = land.getSites(r, type);
                double owned = land.getOwnedAmount(r, type);
                if (sites <= 0 && !(owned > 0)) continue;
                int onAny = 0;
                for (int i = 0; i < packed.length; i++) if (claims.get(i).type() == null) onAny += packed[i][k];
                fields.add(new Vessel(r, type, sites, game.committedOn(r, type), onAny, owned, land.getRemaining(r, type)));
            }
        }
        Metal metal = metal(game, sector);
        CoalMining.Flow wash = sector instanceof CoalMining cm ? cm.lift() : null;
        double carried = sector instanceof Quarrying q ? q.carried() : 0;

        /* ----- the units ----- */
        List<Unit> units = new ArrayList<>();
        int[] building = b.getUnderConstructionById();
        for (BuildingsTemplate t : b.getTemplatesBySector(sector.key())) {
            boolean stand = sector instanceof Forestry && Forestry.isStand(t);
            boolean weighed = sector instanceof ProcessingSector ps ? ps.isWeighed(t)
                    : sector instanceof ExtractionSector ex && ex.isWeighed(t);
            if (!weighed && !stand) continue;
            int n = b.getQuantity(t.getId());
            int coming = t.getId() < building.length ? building[t.getId()] : 0;
            double lastEarns = Double.NaN, growth = 0;
            int idle = 0;
            boolean workedOut = false;
            SpreadPlanner.Candidate oneMore = null;
            if (sector instanceof ProcessingSector ps) {
                if (n > 0) lastEarns = ps.lastEarns(t);
                idle = ps.idleMonths(t);
                oneMore = ps.appraise(t, plans);
            } else if (sector instanceof ExtractionSector ex) {
                workedOut = ex.isWorkedOut(t);
                oneMore = ex.appraise(t, plans);
            }
            if (stand) growth = Forestry.growthOf(t) * n;
            units.add(new Unit(t, n, coming, rate, feedShare(game, sector, t, rate, workedOut, wash, stand), workedOut,
                    lastEarns, idle, growth, oneMore));
        }

        /* ----- the feeds ----- */
        List<Feed> feeds = new ArrayList<>();
        for (Good g : sector.goodsUsed()) {
            if (g == Good.VANS || g == Good.TRUCKS) continue;
            Sector.Input in = sector.inputRow(g);
            Sector.Output own = sector.captive(g) ? sector.outputRow(g) : null;
            double needed = in == null ? 0 : in.needed, captive = own == null ? 0 : own.captive;
            double old = sector instanceof CopperRefining cr && g == Good.COPPER_SCRAP ? cr.oldCopperTaken() : 0;
            double home = in == null ? 0 : in.boughtLocal, imported = in == null ? 0 : in.imported;
            if (!(sector.getInputAtCapacity(g) > 0) && !(needed > 0) && !(captive > 0) && !(old > 0) && !(home > 0)
                    && !(imported > 0)) continue;
            double lorries = g == Good.DIESEL ? sector.getFleetDieselLitres() : 0;
            feeds.add(new Feed(g, needed, captive, old, home, imported, lorries, parties(markets, g, sector.key(), false)));
        }

        /* ----- the products ----- */
        List<Product> products = new ArrayList<>();
        for (Good g : sector.goodsMade()) {
            Sector.Output o = sector.outputRow(g);
            double stock = g.stockable() ? sector.getStock(g) : 0;
            // ...a good it has no plant for, made, sold, carried and holds none of is no product (SectorFlow's rule).
            boolean moved = o != null && (o.produced + o.exportBound + o.captive != 0 || o.soldLocal > 0 || o.exported > 0
                    || o.writtenOff > 0 || o.idled > 0);
            if (!moved && !(sector.getCapacity(g) > 0) && !(stock > 0)) continue;
            double made = o == null ? 0 : o.produced + o.exportBound + o.captive;
            double in = 0, out = 0;
            if (carried > 0 && g == Good.LIMESTONE) {
                out = carried;
                made += carried;
            } else if (carried > 0 && g == Good.AGGREGATE) {
                in = carried;
                made -= carried;
            }
            double lost = o == null ? 0 : o.writtenOff + (g.stockable() ? 0 : o.idled);
            double idled = o == null || !g.stockable() ? 0 : o.idled;
            products.add(new Product(g, made, o == null ? 0 : o.captive, o == null ? 0 : o.soldLocal,
                    o == null ? 0 : o.exported, in, out, lost, idled, stock, parties(markets, g, sector.key(), true)));
        }

        Power power = new Power(sector.getPowerMade(), sector.getPowerDrawn(), sector.getGridDraw(), sector.getPowerSurplus(),
                sector.getPowerSoldValue());
        Lorries lorries = new Lorries(sector.vanFleet(), sector.vansNeeded(), sector.getFleetDieselLitres());
        return new View(sector, roofOf(sector), counted, rate, sector.buildingsStanding(), sector.buildingsOnSite(),
                Collections.unmodifiableList(fields), forest, metal, wash, carried, Collections.unmodifiableList(feeds),
                Collections.unmodifiableList(units), Collections.unmodifiableList(products), power, lorries);
    }

    /** All nine, in INDUSTRIES' order (pure). */
    public static List<View> all(Game game) {
        List<View> out = new ArrayList<>();
        if (game == null || game.getSectors() == null) return out;
        for (String key : INDUSTRIES) {
            View v = of(game, game.getSectors().byKey(key));
            if (v != null) out.add(v);
        }
        return out;
    }

    /* ----- the forest ----- */

    /** Forestry's forest, every figure its own or the land's read (pure). */
    static Forest forest(Game game, Forestry fo) {
        LandManager land = game.getLandManager();
        BuildingManager b = game.getBuildingManager();
        double full = land.getOwnedAmount(Resource.FOREST), cleared = land.getForestClearedM3();
        double grows = fo.plantedGrowth(false);
        return new Forest(full, land.getRemaining(Resource.FOREST), land.getProtectedWood(), land.cuttableForest(), cleared,
                fo.getCutThisMonth(), fo.crewsCut(false), fo.crewsCut(true), fo.getSalvageThisMonth(),
                land.regrowthDue(grows), land.regrowthDue(fo.plantedGrowth(true)), grows,
                land.getZoning().protectedShare(), fo.settlesAt(),
                Forestry.settlesAt(full, cleared, fo.getCutThisMonth(), grows, 0),
                b.getQuantity(Forestry.TREE_PLANTATION), b.getQuantity(Forestry.WOODLOT), fo.getPlantingWord());
    }

    /* ----- the old metal ----- */

    /** The old metal Iron & Steel's yards or Copper Refining's scrap refineries buy, its pool's last month; null for any other industry. */
    static Metal metal(Game game, Sector sector) {
        OldMetal all = game.getOldMetal();
        if (all == null) return null;
        OldMetal.Pool pool;
        Good metal;
        double intake;
        if (sector instanceof HeavyIndustry hi) {
            pool = all.steel();
            metal = Good.STEEL;
            intake = hi.oldSteelIntake();
        } else if (sector instanceof CopperRefining cr) {
            pool = all.copper();
            metal = Good.COPPER;
            intake = cr.oldCopperIntake();
        } else {
            return null;
        }
        Map<OldMetal.Source, Double> by = new EnumMap<>(OldMetal.Source.class);
        for (OldMetal.Source s : OldMetal.Source.values()) {
            double t = pool.lastArisen(s);
            if (t > 0) by.put(s, t);
        }
        return new Metal(metal, pool.lastArisen(), Collections.unmodifiableMap(by), pool.lastBought(), pool.lastLandfill(),
                pool.lastPrice(), intake);
    }

    /* ----- a unit's share of its run ----- */

    /**
     * The share of its sector's rate a kind ran at for its feed or its ground
     * (pure): a Scrap Yard's, the old steel the yards bought over what they
     * would take at the rate; a crew's, the forest's cut over the crews' ask;
     * a wash plant's, the raw coal washed over its wash; a mine over a
     * worked-out type nothing; every other kind all of it.
     */
    static double feedShare(Game game, Sector sector, BuildingsTemplate t, double rate, boolean workedOut, CoalMining.Flow wash,
                            boolean stand) {
        if (workedOut) return 0;
        if (stand) return 1;
        if (sector instanceof HeavyIndustry hi && t.oldMetal(Good.STEEL) > 0) {
            double ask = hi.oldSteelIntake() * rate;
            return ask > 0 ? Math.min(1, game.getOldMetal().steel().lastBought() / ask) : 0;
        }
        if (sector instanceof Forestry fo && Forestry.isCrew(t)) {
            double ask = fo.crewsCut(false) * rate;
            return ask > 0 ? Math.min(1, fo.getCutThisMonth() / ask) : 0;
        }
        if (wash != null && t.getWash() > 0 && t.makes(Good.THERMAL_COAL) <= 0) {
            return wash.wash() > 0 ? Math.min(1, wash.washed() / wash.wash()) : 0;
        }
        return 1;
    }

    /* ----- who sold a feed, who bought a product ----- */

    /**
     * The month's fills of `g` in the city with `key` on one side, by the
     * party on the other, in Sectors.KEYS' order and then the households and
     * the city (pure): `sold` its sales to buyers here, else its purchases
     * from sellers here; the world's trades are the rows' exported and
     * imported, not parts here.
     */
    static List<Part> parties(Markets markets, Good g, String key, boolean sold) {
        List<Part> out = new ArrayList<>();
        GoodsMarket m = markets == null ? null : markets.get(g);
        if (m == null) return out;
        Map<String, Double> by = new LinkedHashMap<>();
        for (Trade t : m.getTrades()) {
            if (sold ? !key.equals(t.seller()) || t.isExport() : !key.equals(t.buyer()) || t.isImport()) continue;
            by.merge(sold ? t.buyer() : t.seller(), t.units(), Double::sum);
        }
        List<String> order = new ArrayList<>(by.keySet());
        order.sort((a, c) -> Integer.compare(rank(a), rank(c)));
        for (String party : order) {
            double units = by.get(party);
            if (units > 0) out.add(new Part(Origin.HOME, party, units));
        }
        return Collections.unmodifiableList(out);
    }

    /** A party's place: a sector by Sectors.KEYS, then the households, the city, anyone else. */
    private static int rank(String party) {
        for (int i = 0; i < Sectors.KEYS.length; i++) if (Sectors.KEYS[i].equals(party)) return i;
        if (Trade.HOUSEHOLDS.equals(party)) return Sectors.KEYS.length;
        if (Trade.CITY.equals(party)) return Sectors.KEYS.length + 1;
        return Sectors.KEYS.length + 2;
    }

    /* =====================================================================
       THE COLOURS (on the card's raised ground, RefineryView.GROUND)
       ===================================================================== */

    /** Power, a ribbon to or from the grid. */
    public static final String POWER = "#ffd866";
    /** A part from or to the industry's own works. */
    public static final String CAPTIVE = RefineryView.UNIT_EDGE;
    /** A part bought or sold in the city. */
    public static final String HOME = "#6fa8dc";
    /** The city's old metal. */
    public static final String OLD_METAL = "#9a8f7e";
    /** A part from or to the world: imported, drawn hatched (the spec's 2.13), or shipped. */
    public static final String WORLD = RefineryView.HOT;

    /** A good's colour: each chain's in its own family - iron's rust and greys, coal's blacks, stone's sands, copper's coppers and the acid's yellow, wood's browns and the paper's white; the oil products the refinery's. */
    public static String colour(Good g) {
        return switch (g) {
            case IRON -> "#b5654a";
            case PELLETS -> "#c98a6a";
            case PIG_IRON -> "#8c8f99";
            case STEEL -> "#aebfd1";
            case SCRAP -> "#7d8794";
            case MET_COKE -> "#5b6270";
            case THERMAL_COAL -> "#4a4f57";
            case COKING_COAL -> "#727885";
            case LIMESTONE -> "#d9d2b8";
            case LIME -> "#f0ead6";
            case AGGREGATE -> "#a59f8f";
            case DIMENSION_STONE -> "#c9bfa6";
            case CEMENT -> "#bfc4c9";
            case COPPER_CONC -> "#9a7b4f";
            case ANODE -> "#c8743c";
            case COPPER -> "#e08a4a";
            case COPPER_ROD -> "#f0a35e";
            case COPPER_SCRAP -> "#a8693f";
            case ACID -> "#d8e36a";
            case SAWLOGS -> "#8b6a43";
            case PULPWOOD -> "#a88a5c";
            case LUMBER -> "#d9b779";
            case PANELS -> "#c9a46a";
            case RESIDUES -> "#b89a6e";
            case PULP -> "#e8dcc0";
            case PAPER -> "#f5f1e6";
            case WOOD_PELLETS -> "#9c7a4a";
            default -> RefineryView.colour(g);
        };
    }

    /** A part's colour by where it came from or went. */
    public static String colour(Origin o) {
        return switch (o) {
            case FIELDS -> RefineryView.CRUDE;
            case CAPTIVE -> CAPTIVE;
            case HOME -> HOME;
            case OLD_METAL -> OLD_METAL;
            case CARRIED -> colour(Good.AGGREGATE);
            case WORLD -> WORLD;
        };
    }

    /** Whether a part is drawn hatched: what came from the world (the spec's 2.13, "imports hatched"). */
    public static boolean hatched(Origin o) {
        return o == Origin.WORLD;
    }

    /** A field type's colour, its vessel's fill: the ore's own (hematite red, magnetite black), the coals', the stones', the coppers'; the forest's green. */
    public static String colour(Resource r, Deposit.FieldType type) {
        if (type != null) {
            switch (type) {
                case HEMATITE: return "#a0462f";
                case MAGNETITE: return "#3e4450";
                case THERMAL: return colour(Good.THERMAL_COAL);
                case COKING: return colour(Good.COKING_COAL);
                case SULPHIDE: return "#b08d3c";
                case OXIDE: return "#3f9a7a";
                case LIMESTONE: return colour(Good.LIMESTONE);
                case HARD_ROCK: return "#8a8578";
                default: break;
            }
        }
        return r == null ? RefineryView.TEXT_3 : String.format("#%06x", r.colour() & 0xFFFFFF);
    }

    /* =====================================================================
       THE WORDS
       ===================================================================== */

    /** A good's quantity in its own unit, short, for a band or a cell: "1,234 t", "830k m³", "2.00M L". */
    public static String figure(Good g, double units) {
        if (!Double.isFinite(units)) return "—";
        return RefineryView.compact(units) + " " + unitOf(g);
    }

    /** ...and whole: "8,306 t", "1,640 m³", "60,000 L". */
    public static String unitsWords(Good g, double units) {
        if (!Double.isFinite(units)) return "—";
        return String.format("%,.0f %s", units, unitOf(g));
    }

    /** A good's unit, short: "t", "m³", "L". */
    public static String unitOf(Good g) {
        if (Double.isFinite(g.litresPerTonne())) return "L";
        return "cubic metre".equals(g.unit()) ? "m³" : "t";
    }

    /** A resource's quantity in the ground, short: "12.3M t", "830k m³". */
    public static String groundWords(Resource r, double amount) {
        if (!Double.isFinite(amount)) return "—";
        return RefineryView.compact(amount) + ("cubic metre".equals(r.unit()) ? " m³" : " t");
    }

    /** A share, whole: "87%". */
    public static String pct(double share) {
        return Double.isFinite(share) ? String.format("%.0f%%", share * 100) : "—";
    }

    /** kW, whole: "13,500 kW". */
    public static String kw(double kw) {
        return Double.isFinite(kw) ? String.format("%,.0f kW", kw) : "—";
    }

    /** A month's money as the screens write it (Formats.amount()). */
    public static String money(double thousands) {
        return Formats.INSTANCE.amount(thousands);
    }

    /** A type in words: "Hematite", "Hard rock"; the forest "Forest". */
    public static String typeWords(Resource r, Deposit.FieldType type) {
        if (type == null || type == Deposit.FieldType.NONE) return r == null ? "" : r.label();
        String w = type.name().toLowerCase(java.util.Locale.ROOT).replace('_', ' ');
        return Character.toUpperCase(w.charAt(0)) + w.substring(1);
    }

    /** A vessel's line: "Hematite · 3 of 5 sites worked · 12.3M t of 20.0M t left"; the forest's "Forest · 1.23M m³ of 2.34M m³ standing". */
    public static String vesselWords(Vessel v) {
        String left = groundWords(v.resource(), v.remaining()) + " of " + groundWords(v.resource(), v.owned());
        if (v.resource() == Resource.FOREST) return typeWords(v.resource(), v.type()) + " · " + left + " standing";
        return String.format("%s · %,d of %,d site%s worked · %s left", typeWords(v.resource(), v.type()), v.taking(), v.sites(),
                v.sites() == 1 ? "" : "s", left);
    }

    /** A part's words: who it came from or went to - "its own works", "Coal Mining", "the households", "the city's old steel", "imported", "shipped". */
    public static String partWords(Part p, Game game, boolean product) {
        return switch (p.origin()) {
            case FIELDS -> "its fields";
            case CAPTIVE -> product ? "to its own works" : "its own works";
            case OLD_METAL -> "the city's old metal";
            case CARRIED -> "carried into aggregate";
            case WORLD -> product ? "shipped" : "imported";
            case HOME -> partyWords(p.party(), game);
        };
    }

    /** A party in the city by name: a sector's label, "the households", "the city". */
    public static String partyWords(String party, Game game) {
        if (Trade.HOUSEHOLDS.equals(party)) return "the households";
        if (Trade.CITY.equals(party)) return "the city";
        Sector s = game == null || game.getSectors() == null ? null : game.getSectors().byKey(party);
        return s == null ? String.valueOf(party) : s.label();
    }

    /** The old metal's name: "old steel", "old copper". */
    public static String metalName(Metal m) {
        return m.metal() == Good.COPPER ? "old copper" : "old steel";
    }

    /** The old metal's line: "old steel: 12,353 t arose · 10,500 t bought at $210.00 · 1,853 t to landfill". */
    public static String metalWords(Metal m) {
        return String.format("%s: %,.0f t arose · %,.0f t bought at %s · %,.0f t to landfill", metalName(m), m.arose(), m.bought(),
                RefineryView.price(m.price()), m.landfill());
    }

    /** A building's name, plural for more than one: "Coke Ovens", "Quarries", "Sawmills". */
    static String names(String name, int n) {
        if (n == 1 || name.endsWith("s")) return name;
        if (name.endsWith("y") && !name.endsWith("ay") && !name.endsWith("ey")) return name.substring(0, name.length() - 1) + "ies";
        if (name.endsWith("ch") || name.endsWith("sh") || name.endsWith("x")) return name + "es";
        return name + "s";
    }

    /** A kind's line: "2 Coke Ovens · runs 87% · 1 on site"; a mine worked out says so; a stand its growth: "3 Woodlots · grow 500 m³ a month"; with none standing "1 Harvesting Operation on site", "no Sawmill". */
    public static String unitWords(Unit u) {
        String name = u.template().getName();
        if (u.standing() <= 0) {
            return u.onSite() > 0 ? String.format("%,d %s on site", u.onSite(), names(name, u.onSite())) : "no " + name;
        }
        StringBuilder s = new StringBuilder(String.format("%,d %s", u.standing(), names(name, u.standing())));
        if (u.oneMore() == null && u.growth() > 0) {
            s.append(String.format(" · grow %,.0f m³ a month", u.growth()));
        } else if (u.workedOut()) {
            s.append(" · worked out");
        } else if (u.standing() > 0) {
            s.append(" · runs ").append(pct(u.run()));
        }
        if (u.onSite() > 0) s.append(String.format(" · %,d on site", u.onSite()));
        return s.toString();
    }

    /** What the last one standing earns, as the idle book counts it: "the last one earns $12.3k a month", "...loses $4.1k a month · idle 3 months"; "" for a mine or none standing. */
    public static String lastWords(Unit u) {
        if (!Double.isFinite(u.lastEarns())) return "";
        String s = u.lastEarns() >= 0 ? "the last one earns " + money(u.lastEarns()) + " a month"
                : "the last one loses " + money(-u.lastEarns()) + " a month";
        if (u.idleMonths() > 0) s += String.format(" · idle %d month%s", u.idleMonths(), u.idleMonths() == 1 ? "" : "s");
        return s;
    }

    /** One more, in the plan's words: "one more earns $1,234.5k a month on $123,456k at the city's prices, 1.00% a month" (ProcessingSector.plan()'s), or the first gate it fails, its refusal: "one more: would lose $295.8k a month on it"; "" for a kind the plan does not weigh. */
    public static String gateWords(Unit u) {
        SpreadPlanner.Candidate c = u.oneMore();
        if (c == null) return "";
        if (c.passes()) {
            return String.format("one more earns $%,.1fk a month on $%,.0fk at the city's prices, %.2f%% a month", c.earns(), c.cost(),
                    100 * c.score());
        }
        return "one more: " + c.why();
    }

    /** A gate's name, as SpreadPlanner orders them: "its feed" (a mine's ground, a crew's forest), "the ground", "the staff", "the money". */
    public static String gateName(SpreadPlanner.Gate g) {
        if (g == null) return "";
        return switch (g) {
            case FEED -> "its feed";
            case LAND -> "the ground";
            case STAFF -> "the staff";
            case MONEY -> "the money";
        };
    }

    /** The works' power: "makes 8,000 kW, draws 2,200 kW · sells 5,800 kW to the grid"; with none made "draws 750 kW from the grid". */
    public static String powerWords(Power p) {
        if (!p.makes()) return "draws " + kw(p.bought()) + " from the grid";
        String s = "makes " + kw(p.made()) + ", draws " + kw(p.drawn());
        if (p.sold() > 0) s += " · sells " + kw(p.sold()) + " to the grid";
        if (p.bought() > 0) s += " · buys " + kw(p.bought()) + " from it";
        return s;
    }

    /** The forest's lines, top to bottom: what stands and may be cut, the cut against the regrowth and the planting line, and where it settles with the law and without. */
    public static List<String> forestWords(Forest f) {
        List<String> out = new ArrayList<>();
        Resource r = Resource.FOREST;
        out.add(String.format("%s of %s standing (%s) · it may cut %s", groundWords(r, f.standing()), groundWords(r, f.owned()),
                pct(f.owned() > 0 ? f.standing() / f.owned() : 0), groundWords(r, f.cuttable())));
        out.add(String.format("the crews cut %s a month of %s at nameplate · regrowth %s", groundWords(r, f.cut()),
                groundWords(r, f.nameplate()), groundWords(r, f.regrowth())));
        out.add(String.format("planting starts past %s of cut a month%s", groundWords(r, f.plantingLine()),
                f.pastPlantingLine() ? " - past it now" : ""));
        out.add(String.format("settles at %s standing under the law (%s protected) · %s with none",
                pct(f.settlesWithLaw()), pct(f.protectedShare()), pct(f.settlesWithout())));
        return out;
    }

    /** The industry's line: "Iron & Steel · 5 works at 87%", "Forestry · nothing standing"; a city just loaded "...· a month must run". */
    public static String headline(View v) {
        String s = v.sector().label();
        if (v.standing() <= 0) return s + " · nothing standing";
        s += String.format(" · %,d building%s at %s", v.standing(), v.standing() == 1 ? "" : "s", pct(v.rate()));
        if (!v.counted()) s += " · a month must run";
        return s;
    }
}
