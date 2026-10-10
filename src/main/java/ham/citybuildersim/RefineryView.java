package ham.citybuildersim;

import ham.citybuildersim.sectors.Oil;
import ham.citybuildersim.sectors.Rail;
import ham.citybuildersim.sectors.RefineryFlow;
import ham.citybuildersim.sectors.RefineryFlow.Kind;
import ham.citybuildersim.sectors.RefineryFlow.Stream;
import ham.citybuildersim.sectors.Refining;
import ham.citybuildersim.sectors.Retail;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The refinery's month as a picture (0.7.95, batch O11; runs/spec-oil.md
 * 2.12): the crude it ran and where that came from, the column's cuts, each
 * conversion unit's run, spread and gate, the products and who took them -
 * home, abroad, into the tanks - with the imports beside them, and the
 * residue burned; and the pictogram those figures draw, laid out to scale.
 *
 * WHY. Refining's Operations page drew the refinery as every business is
 * drawn (SectorFlow): crude in, nine products out, a ring of the rate. Since
 * 0.7.80 a refinery is a campus - crude units cutting the crude, conversion
 * units turning the cheap cuts into dear products (RefineryFlow) - and none
 * of that showed: not the cuts, not which unit made what, not why a unit
 * idles, not the residue burned for want of diesel, not who bought the
 * petrol. Jerus's idea, drawn as the research's mockup 1
 * (claude/oil-and-ports-research.md 6): crude in on the left, the column
 * filled with its cuts, the units, the tank filled by product, the buyers on
 * the right, every ribbon to scale. These are its figures and its layout,
 * held by a harness (RefineryViewCheck) rather than worked out in the
 * screen: the ribbons foot to the flow's litres, the products to the
 * production rows, the buyers to the refinery's sales; the screen
 * (ui/SectorScreen, OPERATIONS · THE REFINERY) only paints them.
 *
 * WHICH MONTH. The flow the month's products were made on, its mix and its
 * rate (Refining.monthsFlow(), THE MONTH AS IT RAN), so each product's run
 * less what idled is the production row's made, to the bit. The takers are
 * the buyers' own rows (Sector.inputRow()): every sector's purchase of a
 * product since the month's strike - the forecourts' petrol (Retail), the
 * vans' diesel, the lubricants, the builders' bitumen - and the railway's
 * diesel, drawn at the top of the month before the strike clears its row,
 * from its own record of the haul (Rail.getFuelLitres()). The crude is the
 * month's run, credited to where the month's crude was bought (crude's
 * clearing, GoodsMarket.getTrades()). After a load nothing is counted until
 * a month runs: the rows are not saved (SectorFlow's rule), and nor is the
 * month's flow.
 *
 * Pure: it reads the city and changes nothing (the planner's appraisals of
 * one more unit are the build card's reads).
 */
public final class RefineryView {

    private RefineryView() { }

    /* =====================================================================
       THE FIGURES
       ===================================================================== */

    /** The products in the tank's order, top to bottom: light to heavy, as the column's cuts boil (mockup 1), and the coker's coke last. */
    public static final Good[] TANK_ORDER = { Good.LPG, Good.NAPHTHA, Good.PETROL, Good.JET, Good.DIESEL, Good.LUBRICANTS,
            Good.FUEL_OIL, Good.BITUMEN, Good.COKE };

    /** The straight-run cuts the column holds, top to bottom: the slate's CUT_ order; the heavy crude's residue is drawn as the residue's. */
    public static final Stream[] COLUMN = { Stream.GAS, Stream.LIGHT_NAPHTHA, Stream.HEAVY_NAPHTHA, Stream.KEROSENE,
            Stream.DIESEL, Stream.GAS_OIL, Stream.RESIDUE };

    /** Litres a tonne of bitumen and coke are drawn at: the residue's, RefineryFlow.RESIDUE_LITRES_PER_TONNE - the weight the flow strikes them at, so a unit's ribbons balance to the litre. */
    public static final double LITRES_A_TONNE_DRAWN = RefineryFlow.RESIDUE_LITRES_PER_TONNE;

    /** A product's units as litres for the picture: a litre good's own, a tonne of bitumen or coke at LITRES_A_TONNE_DRAWN. */
    public static double litresOf(Good g, double units) {
        return Double.isFinite(g.litresPerTonne()) ? units : units * LITRES_A_TONNE_DRAWN;
    }

    /** Where a part of the month's crude came from. */
    public enum SourceKind { LAND_WELLS, PLATFORMS, RESERVE, IMPORTED, TANK_FARM }

    /**
     * A part of the month's run, by where it was bought.
     *
     * @param kind   the city's wells on land or at sea, its strategic reserve, the world - or the refiners' own Tank
     *               Farm when the month bought nothing and ran on what it kept
     * @param count  the wells standing (land or platform wells); 0 for the rest
     * @param bought tonnes bought from it this month
     * @param tonnes tonnes of the month's run credited to it: the run times its share of what was bought (★ O11-2)
     * @param grades its crude's grades, each one's share in Deposit.Grade's order
     */
    public record Source(SourceKind kind, int count, double bought, double tonnes, double[] grades) {
        /** The run's litres credited to it. */
        public double litres() { return tonnes * Refining.CRUDE_LITRES_PER_TONNE; }
    }

    /**
     * One kind of conversion unit, all its buildings together.
     *
     * @param kind       the kind
     * @param standing   its buildings standing, small and large
     * @param onSite     ...and on site
     * @param feed       litres a month its standing buildings could take at nameplate
     * @param run        litres it took this month, at the month's rate; NaN not counted
     * @param share      the share of its nameplate feed the flow gave it; NaN not counted
     * @param spread     its spread a litre of feed at the city's local prices, the month's flow's (what decides whether it runs)
     * @param citySpread ...at the city's own prices, the planner's (SpreadPlanner.cityValue(): what one more would earn)
     * @param gate       one more of its kind weighed by the planner: the size that passes and earns most on its cost, or the
     *                   smallest's refusal; null with no template
     */
    public record Unit(Kind kind, int standing, int onSite, double feed, double run, double share, double spread,
                       double citySpread, SpreadPlanner.Candidate gate) {
        /** Whether it stood and took nothing: no feed spare, or a spread of nothing or less. */
        public boolean idle() { return standing > 0 && Double.isFinite(run) && !(run > 0); }
    }

    /** Who took a product. */
    public enum BuyerKind { CARS, VANS, RAIL, FACTORIES, ROADS, SECTOR, TANKS, IDLED, ABROAD }

    /**
     * A taker of the month's products.
     *
     * @param kind     what it is
     * @param sector   the sector's key for SECTOR (and the buyer whose row it is otherwise); null for the tanks, the
     *                 idled and abroad
     * @param local    units of each product it took from the refinery
     * @param imported ...and from the world
     */
    public record Buyer(BuyerKind kind, String sector, Map<Good, Double> local, Map<Good, Double> imported) {
        /** Units of one product it took, home and abroad. */
        public double of(Good g) { return local.getOrDefault(g, 0.0) + imported.getOrDefault(g, 0.0); }
        /** ...and all it took, as the picture's litres (litresOf()). */
        public double litres() {
            double t = 0;
            for (Good g : TANK_ORDER) t += litresOf(g, of(g));
            return t;
        }
        /** ...of it imported, as litres. */
        public double importedLitres() {
            double t = 0;
            for (Map.Entry<Good, Double> e : imported.entrySet()) t += litresOf(e.getKey(), e.getValue());
            return t;
        }
    }

    /**
     * One product's month.
     *
     * @param good     the product
     * @param run      what the flow made of the month's run: its nameplate at the month's rate (litres; tonnes of
     *                 bitumen and coke) - made and idled together
     * @param made     made, for home and the ship (the production row's produced + exportBound)
     * @param idled    nameplate neither the city nor the world would take (the row's)
     * @param local    taken at home from the refinery's tanks: every buyer's row, and the railway's haul
     * @param imported ...and from the world, by the same buyers
     * @param exported shipped (the row's)
     * @param price    the city's price of a unit today
     */
    public record Product(Good good, double run, double made, double idled, double local, double imported,
                          double exported, double price) {
        /** Into the refiners' tanks (above nothing) or out of them (below): made less what was taken from them. */
        public double tanks() { return made - local - exported; }
        public double intoTanks() { return Math.max(0, tanks()); }
        public double fromTanks() { return Math.max(0, -tanks()); }
        /** Everything the city took of it, home-made and landed. */
        public double used() { return local + imported; }
    }

    /** One end of a traced link: a straight-run cut, a kind of unit, a product, or the furnaces. */
    public record End(Stream cut, Kind unit, Good product) {
        public static final End FURNACES = new End(null, null, null);
        public static End cut(Stream s) { return new End(s, null, null); }
        public static End unit(Kind k) { return new End(null, k, null); }
        public static End product(Good g) { return new End(null, null, g); }
        public boolean furnaces() { return cut == null && unit == null && product == null; }
    }

    /** Litres a month from one end to another, at the month's rate (bitumen and coke at LITRES_A_TONNE_DRAWN). */
    public record Link(End from, End to, double litres) { }

    /**
     * The month.
     *
     * @param standing   whether a crude unit stands
     * @param counted    whether the month's figures are this month's: false after a load until a month runs
     * @param crudeUnits crude units standing
     * @param rate       the month's operating rate; NaN not counted
     * @param flow       the flow at nameplate: the month's when counted, else the one the next month will run
     * @param mix        its crude's grades, each one's share in Deposit.Grade's order
     * @param sources    where the run came from, the world last; empty not counted
     * @param units      each kind of conversion unit with a building standing or on site, in Kind's order
     * @param products   each product the month made, took or shipped, in TANK_ORDER
     * @param buyers     who took them, in BuyerKind's order, ABROAD last
     * @param links      the run traced from cut to unit to product, at the month's rate (trace())
     * @param burned     residue burned for want of diesel to cut it, litres at the rate
     */
    public record View(boolean standing, boolean counted, int crudeUnits, double rate, RefineryFlow.Flow flow,
                       double[] mix, List<Source> sources, List<Unit> units, List<Product> products,
                       List<Buyer> buyers, List<Link> links, double burned) {

        /** The crude the month ran, litres. */
        public double runLitres() { return counted ? flow.crude() * rate : Double.NaN; }

        /** ...and tonnes. */
        public double runTonnes() { return runLitres() / Refining.CRUDE_LITRES_PER_TONNE; }

        /** A straight-run cut of the month's run, litres - the residue with the heavy crude's. */
        public double cut(Stream s) {
            if (!counted) return Double.NaN;
            double c = flow.cut(s);
            if (s == Stream.RESIDUE) c += flow.cut(Stream.HEAVY_RESIDUE);
            return c * rate;
        }

        /** One product's month, or null for one it neither made nor took. */
        public Product product(Good g) {
            for (Product p : products) if (p.good() == g) return p;
            return null;
        }

        /** One kind of unit, or null for a kind with nothing standing or on site. */
        public Unit unit(Kind k) {
            for (Unit u : units) if (u.kind() == k) return u;
            return null;
        }

        /** Whether there is a month to draw: a crude unit standing, counted, that ran something. */
        public boolean drawn() { return standing && counted && runLitres() > 0; }

        /** The picture at a width and a height (THE PICTURE). */
        public Picture picture(double width, double height) { return RefineryView.picture(this, width, height); }
    }

    /** The refinery's month in `game` (pure). */
    public static View of(Game game) {
        Refining r = game.getSectors().refining();
        BuildingManager b = game.getBuildingManager();
        int crudeUnits = 0;
        int[] standing = new int[Kind.values().length], onSite = new int[Kind.values().length];
        Map<Kind, List<BuildingsTemplate>> templates = new EnumMap<>(Kind.class);
        for (BuildingsTemplate t : b.getTemplatesBySector(Sectors.REFINING)) {
            int q = b.getQuantity(t.getId());
            BuildingsStacks stack = b.getStack(t);
            int site = stack == null ? 0 : stack.getUnderConstruction();
            if (Refining.isCrudeUnit(t)) crudeUnits += q;
            else if (Refining.isConversionUnit(t)) {
                Kind k = t.refineryUnit();
                standing[k.ordinal()] += q;
                onSite[k.ordinal()] += site;
                templates.computeIfAbsent(k, x -> new ArrayList<>()).add(t);
            }
        }
        RefineryFlow.Flow months = r.monthsFlow();
        boolean counted = months != null;
        RefineryFlow.Flow flow = counted ? months : r.flow();
        double rate = counted ? r.monthsRate() : Double.NaN;
        double[] mix = counted ? r.monthsMix() : r.getCrudeMix();

        /* ----- the units, and one more of each kind weighed ----- */
        double[] feed = r.unitFeed(false);
        double[] cityValues = r.cityValues();
        List<Unit> units = new ArrayList<>();
        for (Kind k : Kind.values()) {
            int i = k.ordinal();
            if (standing[i] == 0 && onSite[i] == 0) continue;
            double run = counted ? flow.run(k) * rate : Double.NaN;
            double share = counted && feed[i] > 0 ? flow.run(k) / feed[i] : Double.NaN;
            units.add(new Unit(k, standing[i], onSite[i], feed[i], run, share, flow.spread(k),
                    RefineryFlow.spread(k, cityValues), oneMore(r, game, templates.get(k))));
        }

        if (!counted) {
            return new View(crudeUnits > 0, false, crudeUnits, Double.NaN, flow, mix, List.of(),
                    Collections.unmodifiableList(units), List.of(), List.of(), List.of(), Double.NaN);
        }

        /* ----- the crude: where it was bought, the run credited to it ----- */
        double wells = 0, reserve = 0, world = 0;
        GoodsMarket crude = game.getMarkets().get(Good.CRUDE);
        if (crude != null) {
            for (Trade t : crude.getTrades()) {
                if (!Sectors.REFINING.equals(t.buyer())) continue;
                if (t.isImport()) world += t.units();
                else if (Trade.CITY.equals(t.seller())) reserve += t.units();
                else wells += t.units();
            }
        }
        double bought = wells + reserve + world, runTonnes = flow.crude() * rate / Refining.CRUDE_LITRES_PER_TONNE;
        Oil oil = game.getSectors().oil();
        double ground = Math.max(0, oil.getLiftedOnGround()), sea = Math.max(0, oil.getLiftedAtSea());
        double seaShare = ground + sea > 0 ? sea / (ground + sea) : 0;
        double[] wellGrades = wellsGrades(r.getCrudeMix(), wells + reserve, world);
        List<Source> sources = new ArrayList<>();
        if (bought > 0) {
            double land = wells * (1 - seaShare), atSea = wells - land;
            addSource(sources, SourceKind.LAND_WELLS, oil.landWellsStanding(), land, runTonnes * land / bought, wellGrades);
            addSource(sources, SourceKind.PLATFORMS, oil.platformWellsStanding(), atSea, runTonnes * atSea / bought, wellGrades);
            addSource(sources, SourceKind.RESERVE, 0, reserve, runTonnes * reserve / bought, Refining.MEDIUM_MIX);
            addSource(sources, SourceKind.IMPORTED, 0, world, runTonnes * world / bought, Refining.MEDIUM_MIX);
        } else if (runTonnes > 0) {
            sources.add(new Source(SourceKind.TANK_FARM, 0, 0, runTonnes, mix.clone()));
        }

        /* ----- who took the products, off their own rows ----- */
        Map<String, Map<Good, double[]>> took = new LinkedHashMap<>();
        Map<String, BuyerKind> kinds = new LinkedHashMap<>();
        // ...the refiners' own among them: their vans run on their diesel (Sector.runFleet()).
        for (Sector s : game.getSectors().all()) {
            for (Good g : TANK_ORDER) {
                Sector.Input in = s.inputRow(g);
                if (in == null || !(in.boughtLocal > 0 || in.imported > 0)) continue;
                BuyerKind kind = kindOf(s, g);
                String key = kind == BuyerKind.SECTOR ? kind + ":" + s.key() : kind.name();
                kinds.putIfAbsent(key, kind);
                double[] v = took.computeIfAbsent(key, x -> new EnumMap<>(Good.class)).computeIfAbsent(g, x -> new double[2]);
                v[0] += Math.max(0, in.boughtLocal);
                v[1] += Math.max(0, in.imported);
            }
        }
        // ...and the railway's diesel, drawn at the top of the month before the strike cleared its row (Rail.haul()).
        Rail rail = game.getSectors().rail();
        if (rail != null && rail.getFuelLitres() > 0) {
            kinds.putIfAbsent(BuyerKind.RAIL.name(), BuyerKind.RAIL);
            double[] v = took.computeIfAbsent(BuyerKind.RAIL.name(), x -> new EnumMap<>(Good.class))
                    .computeIfAbsent(Good.DIESEL, x -> new double[2]);
            double imported = Math.max(0, Math.min(rail.getFuelLitres(), rail.getFuelLitresImported()));
            v[0] += rail.getFuelLitres() - imported;
            v[1] += imported;
        }

        /* ----- each product's month ----- */
        List<Product> products = new ArrayList<>();
        Map<Good, Double> into = new EnumMap<>(Good.class), idledAll = new EnumMap<>(Good.class), shipped = new EnumMap<>(Good.class);
        for (Good g : TANK_ORDER) {
            Sector.Output o = r.outputRow(g);
            double made = o == null ? 0 : o.produced + o.exportBound;
            double idled = o == null ? 0 : o.idled, exported = o == null ? 0 : o.exported;
            double local = 0, imported = 0;
            for (Map<Good, double[]> m : took.values()) {
                double[] v = m.get(g);
                if (v == null) continue;
                local += v[0];
                imported += v[1];
            }
            double run = flow.of(g) * rate;
            if (!(run > 0 || made > 0 || local > 0 || imported > 0 || exported > 0)) continue;
            GoodsMarket m = game.getMarkets().get(g);
            Product p = new Product(g, run, made, idled, local, imported, exported, m == null ? Double.NaN : m.getLocalPrice());
            products.add(p);
            if (p.intoTanks() > 0) into.put(g, p.intoTanks());
            if (idled > 0) idledAll.put(g, idled);
            if (exported > 0) shipped.put(g, exported);
        }

        /* ----- the buyers, in BuyerKind's order ----- */
        List<Buyer> buyers = new ArrayList<>();
        for (BuyerKind kind : BuyerKind.values()) {
            switch (kind) {
                case TANKS -> { if (!into.isEmpty()) buyers.add(new Buyer(kind, null, Collections.unmodifiableMap(into), Map.of())); }
                case IDLED -> { if (!idledAll.isEmpty()) buyers.add(new Buyer(kind, null, Collections.unmodifiableMap(idledAll), Map.of())); }
                case ABROAD -> { if (!shipped.isEmpty()) buyers.add(new Buyer(kind, null, Collections.unmodifiableMap(shipped), Map.of())); }
                default -> {
                    for (Map.Entry<String, BuyerKind> e : kinds.entrySet()) {
                        if (e.getValue() != kind) continue;
                        Map<Good, Double> local = new EnumMap<>(Good.class), imported = new EnumMap<>(Good.class);
                        for (Map.Entry<Good, double[]> v : took.get(e.getKey()).entrySet()) {
                            if (v.getValue()[0] > 0) local.put(v.getKey(), v.getValue()[0]);
                            if (v.getValue()[1] > 0) imported.put(v.getKey(), v.getValue()[1]);
                        }
                        String sector = kind == BuyerKind.SECTOR ? e.getKey().substring(kind.name().length() + 1) : null;
                        buyers.add(new Buyer(kind, sector, Collections.unmodifiableMap(local), Collections.unmodifiableMap(imported)));
                    }
                }
            }
        }

        return new View(crudeUnits > 0, true, crudeUnits, rate, flow, mix, Collections.unmodifiableList(sources),
                Collections.unmodifiableList(units), Collections.unmodifiableList(products),
                Collections.unmodifiableList(buyers), trace(flow, rate), flow.burned() * rate);
    }

    private static void addSource(List<Source> to, SourceKind kind, int count, double bought, double tonnes, double[] grades) {
        if (bought > 0) to.add(new Source(kind, count, bought, tonnes, grades.clone()));
    }

    /**
     * The grades of the crude bought at home this month (pure): what the
     * month's purchases' mix (Refining.monthsMix() of them, getCrudeMix() at
     * the month's end) holds once the imports' MEDIUM is taken out; MEDIUM
     * with none bought at home.
     */
    static double[] wellsGrades(double[] purchases, double local, double imported) {
        if (!(local > 0)) return Refining.MEDIUM_MIX.clone();
        double[] g = new double[Deposit.Grade.values().length];
        double sum = 0;
        for (int i = 0; i < g.length; i++) {
            g[i] = Math.max(0, purchases[i] * (local + imported) - (i == Deposit.Grade.MEDIUM.ordinal() ? imported : 0));
            sum += g[i];
        }
        if (!(sum > 0)) return Refining.MEDIUM_MIX.clone();
        for (int i = 0; i < g.length; i++) g[i] /= sum;
        return g;
    }

    /** What a sector's purchase of a product is, as a taker: the forecourts' petrol the cars', diesel the vans' (the railway's its own), lubricants the factories', bitumen the roads'. */
    static BuyerKind kindOf(Sector s, Good g) {
        if (g == Good.PETROL && s instanceof Retail) return BuyerKind.CARS;
        if (g == Good.DIESEL) return s instanceof Rail ? BuyerKind.RAIL : BuyerKind.VANS;
        if (g == Good.LUBRICANTS) return BuyerKind.FACTORIES;
        if (g == Good.BITUMEN) return BuyerKind.ROADS;
        return BuyerKind.SECTOR;
    }

    /**
     * One more of a kind, weighed by the planner (Refining.appraise(), the
     * build card's read): of its sizes, the one that passes every gate and
     * earns most on its cost, or - with none passing - the smallest's.
     */
    static SpreadPlanner.Candidate oneMore(Refining r, Game game, List<BuildingsTemplate> sizes) {
        if (sizes == null || sizes.isEmpty() || game.getBusinessInvestment() == null) return null;
        List<BuildingsTemplate> bySize = new ArrayList<>(sizes);
        bySize.sort(java.util.Comparator.comparingDouble(BuildingsTemplate::feedPerMonth));
        SpreadPlanner.Candidate first = null, best = null;
        for (BuildingsTemplate t : bySize) {
            SpreadPlanner.Candidate c = r.appraise(t, game.getBusinessInvestment());
            if (first == null) first = c;
            if (c.passes() && c.cost() > 0 && c.earns() > 0 && (best == null || c.score() > best.score())) best = c;
        }
        return best != null ? best : first;
    }

    /* =====================================================================
       THE FLOW, TRACED: which cut went through which unit into which product

       RefineryFlow.solve() keeps each stream as one pool, and a unit's
       outputs join the pools it feeds (a cracking unit's cracked gas the
       pool alkylation takes, a hydrocracker's naphtha the reformers'). The
       picture needs each ribbon's ends, so this walks the solve's steps
       again with each pool kept by where its litres came from - a cut, or a
       unit - taking each unit's run as the flow gave it (Flow.run()), from
       its pool in proportion: within a stream no unit feeds its own pool, so
       the shares do not depend on the order its units took. The leftovers go
       to their products, and the residue is cut three to one with the diesel
       pool, each part of the diesel in proportion, the rest burned. Every
       ribbon then foots to the flow: into a unit its run, into a product the
       flow's make, out of a cut the cut (RefineryViewCheck holds it).
       ===================================================================== */

    /** The flow `f` (at nameplate) traced end to end, each link's litres at `rate` (pure). */
    public static List<Link> trace(RefineryFlow.Flow f, double rate) {
        int n = Stream.values().length;
        List<Map<End, Double>> pool = new ArrayList<>();
        for (int i = 0; i < n; i++) pool.add(new LinkedHashMap<>());
        for (Stream s : COLUMN) {
            if (s != Stream.RESIDUE && f.cut(s) > 0) pool.get(s.ordinal()).put(End.cut(s), f.cut(s));
        }
        Map<String, Link> links = new LinkedHashMap<>();
        for (Stream stream : RefineryFlow.FLOW_ORDER) {
            for (Kind k : Kind.values()) {
                if (k.solvedIn() != stream) continue;
                double take = f.run(k);
                if (!(take > 0)) continue;
                if (k.feed().residue()) {
                    // The residue's pools hold the column's residue alone: no unit makes residue.
                    link(links, End.cut(Stream.RESIDUE), End.unit(k), take);
                } else {
                    Map<End, Double> p = pool.get(k.feed().ordinal());
                    double total = 0;
                    for (double v : p.values()) total += v;
                    if (total > 0) {
                        double left = Math.max(0, total - take) / total;
                        for (Map.Entry<End, Double> e : p.entrySet()) {
                            link(links, e.getKey(), End.unit(k), take * e.getValue() / total);
                            e.setValue(e.getValue() * left);
                        }
                    }
                }
                for (RefineryFlow.Yield y : k.yields()) {
                    double made = take * y.perLitre();
                    if (y.stream() != null) pool.get(y.stream().ordinal()).merge(End.unit(k), made, Double::sum);
                    else link(links, End.unit(k), End.product(y.good()), litresOf(y.good(), made));
                }
            }
        }
        // The leftovers to their products (the solve's step 4).
        for (Stream s : new Stream[] { Stream.GAS, Stream.CRACKED_GAS, Stream.LIGHT_NAPHTHA, Stream.HEAVY_NAPHTHA,
                Stream.KEROSENE, Stream.GAS_OIL }) {
            for (Map.Entry<End, Double> e : pool.get(s.ordinal()).entrySet()) {
                link(links, e.getKey(), End.product(s.leftover()), e.getValue());
            }
        }
        // The residue cut three to one with the diesel pool, each part of the diesel in proportion, the rest burned (5).
        Map<End, Double> diesel = pool.get(Stream.DIESEL.ordinal());
        double d = 0;
        for (double v : diesel.values()) d += v;
        double residue = f.spare(Stream.RESIDUE) + f.spare(Stream.HEAVY_RESIDUE);
        double cut = Math.min(residue, Refining.RESIDUE_PER_DIESEL * d);
        double cutter = cut / Refining.RESIDUE_PER_DIESEL;
        for (Map.Entry<End, Double> e : diesel.entrySet()) {
            double share = d > 0 ? e.getValue() / d : 0;
            link(links, e.getKey(), End.product(Good.DIESEL), Math.max(0, e.getValue() - cutter * share));
            link(links, e.getKey(), End.product(Good.FUEL_OIL), cutter * share);
        }
        link(links, End.cut(Stream.RESIDUE), End.product(Good.FUEL_OIL), cut);
        link(links, End.cut(Stream.RESIDUE), End.FURNACES, residue - cut);
        List<Link> out = new ArrayList<>();
        for (Link l : links.values()) if (l.litres() * rate > 0) out.add(new Link(l.from(), l.to(), l.litres() * rate));
        return Collections.unmodifiableList(out);
    }

    private static void link(Map<String, Link> links, End from, End to, double litres) {
        if (!(litres > 0)) return;
        links.merge(from + ">" + to, new Link(from, to, litres), (a, b) -> new Link(a.from(), a.to(), a.litres() + b.litres()));
    }

    /* =====================================================================
       THE PICTURE (mockup 1, 2.12's "ribbons to scale")

       Six columns, left to right: the crude's sources, the column (the
       crude's cuts in boiling order), the conversion units - the first rank
       fed from the column's gas oil and residue, the second from the pools
       other units feed, the reformers (heavy naphtha) and alkylation
       (cracked gas) - the tank (each product the month made, then what was
       taken out of the tanks, then the imports, hatched), and the takers.
       A cut no unit takes runs through the units' columns as a plain band.
       Every ribbon is its litres times one scale, the largest at which each
       column fits the picture's height; a node is as tall as its larger
       side. Ribbons leave and arrive in the order of the node at their other
       end, top first, so they cross as little as the order allows. The
       takers' labels are spread apart to LABEL_SPACING, a line to each bar.
       Every position and every word is worked out here, with its colour,
       so the screen and a check draw the same picture.
       ===================================================================== */

    /** The picture's height on the page (mockup 1's 404 px at 1,389 x 868). */
    public static final double HEIGHT = 404;

    /** The least width it is laid out at: a second rank's line of words fits before the tank (the 1,280 window's card is about 1,094). */
    public static final double LEAST_WIDTH = 1000;

    /** Room above the columns for their headings: three lines over the column. */
    public static final double TOP = 64;

    /** ...and the margin under and over what the columns hold. */
    static final double PAD = 4;

    /** The sources' room at the left: an icon, three lines of words and the bar at its right edge. */
    static final double SOURCE_ZONE = 182;

    /** The sources' bar. */
    static final double SOURCE_W = 10;

    /** The takers' room at the right: the bar, an icon, a name and its figure, a line of words. */
    static final double BUYER_ZONE = 236;

    /** The takers' bar. */
    static final double BUYER_W = 12;

    /** The column's width (mockup 1's 82). */
    static final double COLUMN_W = 82;

    /** A unit's box (mockup 1's 66, narrower for the second rank beside the first). */
    static final double UNIT_W = 56;

    /** The tank's width: a product's name and its figure inside its band, and a block's line under it. */
    static final double TANK_W = 140;

    /** Where the column, each rank of units and the tank stand, as a share of the room between the sources' bar and the takers': a rank's line of words fits between its box and the tank. */
    static final double COLUMN_AT = .10, FIRST_RANK_AT = .29, SECOND_RANK_AT = .42, TANK_AT = .69;

    /** The gap over a unit's box: its line of words (mockup 1's 18). */
    static final double UNIT_GAP = 18;

    /** The gap between bands that run through the units' columns, and between a column's bands of different ends. */
    static final double PASS_GAP = 3;

    /** The gap between the sources, between the takers, and between the tank's blocks (made, from the tanks, imported). */
    static final double SOURCE_GAP = 26, BUYER_GAP = 12, BLOCK_GAP = 22;

    /** The least a taker's label centre stands from the next (mockup 1's 33). */
    static final double LABEL_SPACING = 33;

    /** The least band a word is written in: a cut's name in the column, a product's in the tank. */
    static final double WORDS_IN_BAND = 11;

    /** The least a node is drawn, so a trickle shows. */
    static final double LEAST_NODE = 1.5;

    /* ----- the colours (mockup 1's, on the card's raised ground) ----- */

    /** The ground the picture sits on: the card's (ui/Palette.RAISED), and a word's halo. */
    public static final String GROUND = "#152130";
    /** A roof, a cap, a dome. */
    public static final String CAP = "#101924";
    /** Their edges, and the column's and the tank's frame. */
    public static final String FRAME = "#3a4f6a";
    /** Crude, in a ribbon and a source's bar. */
    public static final String CRUDE = "#a07d4a";
    /** A unit's box and its edge. */
    public static final String UNIT_FILL = "#1b2a3d", UNIT_EDGE = "#4a6283";
    /** The furnaces' box and its edge, and the flare. */
    public static final String FURNACE_FILL = "#2a1f1a", FURNACE_EDGE = "#6b4a3a", FLARE = "#ffb454";
    /** The words: the head's, the body's, the faint. */
    public static final String TEXT = "#e6edf3", TEXT_2 = "#a9b8c9", TEXT_3 = "#8496ab";
    /** A word that something is imported. */
    public static final String HOT = "#e3b341";
    /** The words on a pale band. */
    public static final String ON_BAND = "#0b1118";

    /** A product's colour (mockup 1's; coke the coal's grey). */
    public static String colour(Good g) {
        return switch (g) {
            case LPG -> "#9fd8ef";
            case NAPHTHA -> "#b7e4a8";
            case PETROL -> "#ffcf5c";
            case JET -> "#f2a65a";
            case DIESEL -> "#d9805f";
            case LUBRICANTS -> "#c792ea";
            case FUEL_OIL -> "#9a7a6c";
            case BITUMEN -> "#6e665f";
            case COKE -> "#5b6270";
            default -> TEXT_3;
        };
    }

    /** A cut's colour (mockup 1's). */
    public static String colour(Stream s) {
        return switch (s) {
            case GAS, CRACKED_GAS -> "#9fd8ef";
            case LIGHT_NAPHTHA -> "#ffe29a";
            case HEAVY_NAPHTHA -> "#b7e4a8";
            case KEROSENE -> "#f2a65a";
            case DIESEL -> "#d9805f";
            case GAS_OIL -> "#7f8ca6";
            case RESIDUE, HEAVY_RESIDUE -> "#57504b";
        };
    }

    /** A cut's name in the column (mockup 1's). */
    public static String cutName(Stream s) {
        return switch (s) {
            case GAS -> "Gas";
            case LIGHT_NAPHTHA -> "Light naphtha";
            case HEAVY_NAPHTHA -> "Heavy naphtha";
            case KEROSENE -> "Kerosene";
            case DIESEL -> "Diesel";
            case GAS_OIL -> "Gas oil";
            case RESIDUE, HEAVY_RESIDUE -> "Residue";
            case CRACKED_GAS -> "Cracked gas";
        };
    }

    /** The faces a word is set in: Plex Sans, at its weights, and Plex Mono for a figure. */
    public enum Face { SANS, SANS_MEDIUM, SANS_STRONG, MONO, MONO_STRONG }

    /** Where a word stands on its x: its start, its middle or its end. */
    public enum Align { START, MIDDLE, END }

    /** The icons the picture draws, by what they stand for; the screen picks each one's outline. */
    public enum Icon { WELL, PLATFORM, RESERVE, TANKER, TANK, CAR, LORRY, TRAIN, FACTORY, ROAD, SECTOR, GLOBE, IDLE, UNIT, FLAME }

    /**
     * A box: a source's bar, a cut, a unit, a band through the units, the
     * furnaces, a product's band, a taker's segment.
     *
     * @param fill    its colour; null for none
     * @param opacity the fill's
     * @param stroke  its edge's colour; null for none
     * @param dashed  whether its edge is dashed
     * @param hatched whether the fill is the import's hatching in `fill`'s colour
     * @param round   its corners' radius
     * @param tip     what it says under the pointer; null for nothing
     */
    public record Box(double x, double y, double w, double h, String fill, double opacity, String stroke,
                      boolean dashed, boolean hatched, double round, String tip) { }

    /**
     * A ribbon from (x1, a0..a1) to (x2, b0..b1), its two edges each a curve
     * with its handles at the middle x (mockup 1's band()).
     *
     * @param from    its colour at x1
     * @param to      ...at x2 (a gradient when they differ)
     * @param hatched the import's hatching in `from`'s colour
     */
    public record Ribbon(double x1, double a0, double a1, double x2, double b0, double b1, String from, String to,
                         double opacity, boolean hatched) {
        public double height() { return a1 - a0; }
    }

    /** An outline in SVG's absolute M, L, Q and Z, filled and edged: the column's dome, the tank's roof, the stack, the flare. */
    public record Outline(String path, String fill, double opacity, String stroke) { }

    /** A straight line: a leader from a taker's bar to its moved label, a band's divider. */
    public record Line(double x1, double y1, double x2, double y2, String colour) { }

    /**
     * A word, with its baseline at y.
     *
     * @param slot the widest it may be drawn, px - with `then`, the two together: the room it has before it runs
     *             into something
     * @param halo whether it is drawn over a halo of the ground, to read over a ribbon
     * @param then a second run on the same line, drawn straight after this one in its own face, size and colour (its
     *             x, y, align and slot are not read); null for none
     */
    public record Words(String text, double x, double y, Face face, double size, String colour, Align align,
                        double slot, boolean halo, Words then) {
        public Words(String text, double x, double y, Face face, double size, String colour, Align align, double slot,
                     boolean halo) {
            this(text, x, y, face, size, colour, align, slot, halo, null);
        }

        /** A run to follow a word on its line. */
        public static Words run(String text, Face face, double size, String colour) {
            return new Words(text, 0, 0, face, size, colour, Align.START, 0, false, null);
        }

        /** The whole line's text, both runs. */
        public String line() { return then == null ? text : text + then.text(); }
    }

    /** An icon, `size` px square from its top left. */
    public record Mark(Icon icon, String sector, double x, double y, double size, String colour) { }

    /**
     * A node of the picture by name, where it stands - for a check to attach
     * each ribbon to its two ends: "source:IMPORTED", "cut:GAS_OIL",
     * "unit:REFORMER", "band:<from>><to>" (a cut through the units' columns),
     * "furnaces", "made:PETROL", "stored:PETROL" (out of the tanks),
     * "landed:PETROL" (imported), "taker:<i>" (the i-th of the view's buyers).
     */
    public record Place(String key, double x, double y, double w, double h) {
        public double right() { return x + w; }
    }

    /**
     * The picture, in painting order: the ribbons, then the boxes over them,
     * the outlines, lines, icons and words; and its nodes by name.
     *
     * @param scale px a litre
     */
    public record Picture(double width, double height, double scale, List<Ribbon> ribbons, List<Box> boxes,
                          List<Outline> outlines, List<Line> lines, List<Mark> marks, List<Words> words, List<Place> places) {
        public boolean empty() { return ribbons.isEmpty() && boxes.isEmpty(); }

        /** A node by its name, or null. */
        public Place place(String key) {
            for (Place p : places) if (p.key().equals(key)) return p;
            return null;
        }
    }

    /** A node of the picture while it is laid out: where it stands, and where its ribbons have reached on each side. */
    private static final class Node {
        final double x, w;
        double y, h, inAt, outAt;
        Node(double x, double y, double w, double h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.inAt = y;
            this.outAt = y;
        }
        double right() { return x + w; }
    }

    /** One slot in the units' columns: a kind of unit, a band from a cut to a product or the furnaces. */
    private record Slot(Kind unit, Link pass, boolean secondRank) { }

    /** The units fed from the column's gas oil and residue, the first rank; the rest are the second (the pools other units feed). */
    static boolean firstRank(Kind k) {
        return k.feed() == Stream.GAS_OIL || k.feed().residue();
    }

    /** The column cut a kind's slot is drawn beside: its feed's, alkylation's the gas oil's (its cracked gas comes from the cracking units). */
    static Stream besideCut(Kind k) {
        return k.feed() == Stream.CRACKED_GAS ? Stream.GAS_OIL : k.feed().residue() ? Stream.RESIDUE : k.feed();
    }

    static Picture picture(View v, double width, double height) {
        List<Ribbon> ribbons = new ArrayList<>();
        List<Box> boxes = new ArrayList<>();
        List<Outline> outlines = new ArrayList<>();
        List<Line> lines = new ArrayList<>();
        List<Mark> marks = new ArrayList<>();
        List<Words> words = new ArrayList<>();
        List<Place> places = new ArrayList<>();
        if (!v.drawn()) return new Picture(width, height, 0, ribbons, boxes, outlines, lines, marks, words, places);

        double right = Math.max(LEAST_WIDTH, width) - BUYER_ZONE;
        double room = right - (SOURCE_ZONE + SOURCE_W), base = SOURCE_ZONE + SOURCE_W;
        double xSrc = SOURCE_ZONE, xCol = base + room * COLUMN_AT, x1 = base + room * FIRST_RANK_AT,
                x2 = base + room * SECOND_RANK_AT, xTank = base + room * TANK_AT, xBuy = right;
        double usable = height - TOP - 2 * PAD, mid = TOP + (height - TOP) / 2;

        /* ----- what each column holds ----- */
        double run = v.runLitres();
        Map<Kind, Unit> units = new EnumMap<>(Kind.class);
        for (Unit u : v.units()) if (u.standing() > 0) units.put(u.kind(), u);
        List<Slot> slots = new ArrayList<>();
        for (Stream c : COLUMN) {
            for (Kind k : Kind.values()) {
                if (units.containsKey(k) && besideCut(k) == c) slots.add(new Slot(k, null, !firstRank(k)));
            }
            for (Good g : TANK_ORDER) {
                for (Link l : v.links()) if (c == l.from().cut() && g == l.to().product()) slots.add(new Slot(null, l, false));
            }
            if (c == Stream.RESIDUE) {
                for (Link l : v.links()) if (l.to().furnaces()) slots.add(new Slot(null, l, false));
            }
        }
        Map<Kind, double[]> unitSides = new EnumMap<>(Kind.class);
        for (Kind k : units.keySet()) unitSides.put(k, new double[2]);
        for (Link l : v.links()) {
            if (l.to().unit() != null && unitSides.containsKey(l.to().unit())) unitSides.get(l.to().unit())[0] += l.litres();
            if (l.from().unit() != null && unitSides.containsKey(l.from().unit())) unitSides.get(l.from().unit())[1] += l.litres();
        }
        double slotLitres = 0, slotGaps = 0;
        for (int i = 0; i < slots.size(); i++) {
            Slot s = slots.get(i);
            slotLitres += s.unit() != null ? side(unitSides, s.unit()) : s.pass().litres();
            slotGaps += gapBefore(slots, i);
        }

        double tankRun = 0, fromTanks = 0, imported = 0;
        for (Product p : v.products()) {
            tankRun += litresOf(p.good(), p.run());
            fromTanks += litresOf(p.good(), p.fromTanks());
            imported += litresOf(p.good(), p.imported());
        }
        int blocks = 1 + (fromTanks > 0 ? 1 : 0) + (imported > 0 ? 1 : 0);
        double buyerLitres = 0;
        for (Buyer b : v.buyers()) buyerLitres += b.litres();

        // One scale: the largest at which every column fits (mockup 1's k).
        double k = (usable - 12) / run;
        if (!v.sources().isEmpty()) k = Math.min(k, (usable - (v.sources().size() - 1) * SOURCE_GAP) / run);
        if (slotLitres > 0) k = Math.min(k, (usable - slotGaps) / slotLitres);
        if (tankRun + fromTanks + imported > 0) {
            k = Math.min(k, (usable - (blocks - 1) * BLOCK_GAP - BLOCK_WORDS * (blocks - 1)) / (tankRun + fromTanks + imported));
        }
        if (buyerLitres > 0) k = Math.min(k, (usable - (v.buyers().size() - 1) * BUYER_GAP) / buyerLitres);
        if (!(k > 0) || !Double.isFinite(k)) return new Picture(width, height, 0, ribbons, boxes, outlines, lines, marks, words, places);

        /* ----- the sources, and the crude into the column ----- */
        double srcH = run * k + (v.sources().size() - 1) * SOURCE_GAP;
        double y = mid - srcH / 2;
        double colH = run * k, colY = mid - colH / 2, cin = colY;
        words.add(new Words("CRUDE IN", 0, y - 24, Face.SANS_STRONG, 9.5, TEXT_3, Align.START, xSrc, false));
        for (Source s : v.sources()) {
            double h = s.litres() * k;
            boxes.add(new Box(xSrc, y, SOURCE_W, Math.max(h, LEAST_NODE), CRUDE, 1, null, false, false, 1.5,
                    sourceName(s) + ": " + litres(s.litres()) + " of the month's run, " + tonnes(s.tonnes())
                            + (s.bought() > 0 ? " (" + tonnes(s.bought()) + " bought)" : "")));
            ribbons.add(new Ribbon(xSrc + SOURCE_W, y, y + h, xCol, cin, cin + h, CRUDE, CRUDE, .55, false));
            places.add(new Place("source:" + s.kind(), xSrc, y, SOURCE_W, h));
            double cy = y + h / 2;
            marks.add(new Mark(sourceIcon(s.kind()), null, 0, cy - 17, 20, "#c9b68f"));
            words.add(new Words(sourceName(s), 26, cy - 9, Face.SANS_MEDIUM, 11.5, TEXT, Align.START, xSrc - 30, false));
            words.add(new Words(sourceWords(s), 26, cy + 5, Face.SANS, 10, TEXT_3, Align.START, xSrc - 30, false));
            words.add(new Words(litres(s.litres()), 26, cy + 19, Face.MONO, 10.5, TEXT_2, Align.START, xSrc - 30, false));
            cin += h;
            y += h + SOURCE_GAP;
        }

        /* ----- the column ----- */
        Map<Stream, Node> cuts = new EnumMap<>(Stream.class);
        double cy0 = colY;
        for (Stream c : COLUMN) {
            double h = v.cut(c) * k;
            cuts.put(c, new Node(xCol, cy0, COLUMN_W, h));
            places.add(new Place("cut:" + c, xCol, cy0, COLUMN_W, h));
            cy0 += h;
        }

        /* ----- the units' columns: each unit, each band through them, the furnaces ----- */
        double s2H = slotLitres * k + slotGaps, s2Top = mid - s2H / 2;
        y = s2Top;
        Map<Kind, Node> unitNodes = new EnumMap<>(Kind.class);
        Map<Link, Node> bandOf = new java.util.IdentityHashMap<>();
        Node furnaces = null;
        for (int i = 0; i < slots.size(); i++) {
            Slot s = slots.get(i);
            y += gapBefore(slots, i);
            if (s.unit() != null) {
                double h = side(unitSides, s.unit()) * k;
                unitNodes.put(s.unit(), new Node(s.secondRank() ? x2 : x1, y, UNIT_W, h));
                places.add(new Place("unit:" + s.unit(), s.secondRank() ? x2 : x1, y, UNIT_W, h));
                y += h;
            } else {
                double h = s.pass().litres() * k;
                Node n = s.pass().to().furnaces() ? new Node(x1, y, UNIT_W, h) : new Node(x1, y, x2 + UNIT_W - x1, h);
                if (s.pass().to().furnaces()) furnaces = n;
                bandOf.put(s.pass(), n);
                places.add(new Place(s.pass().to().furnaces() ? "furnaces" : "band:" + s.pass().from().cut() + ">"
                        + s.pass().to().product(), n.x, n.y, n.w, n.h));
                y += h;
            }
        }

        /* ----- the tank: made, out of the tanks, imported ----- */
        double s3H = (tankRun + fromTanks + imported) * k + (blocks - 1) * (BLOCK_GAP + BLOCK_WORDS);
        y = mid - s3H / 2;
        double tankY = y;
        Map<Good, Node> made = new EnumMap<>(Good.class), outOfTanks = new EnumMap<>(Good.class), landed = new EnumMap<>(Good.class);
        int madeCount = 0;
        for (Product p : v.products()) {
            double h = litresOf(p.good(), p.run()) * k;
            if (!(h > 0)) continue;
            made.put(p.good(), new Node(xTank, y, TANK_W, h));
            places.add(new Place("made:" + p.good(), xTank, y, TANK_W, h));
            madeCount++;
            y += h;
        }
        double tankH = y - tankY, fromY = Double.NaN, impY = Double.NaN;
        if (fromTanks > 0) {
            y += BLOCK_GAP;
            fromY = y;
            for (Product p : v.products()) {
                double h = litresOf(p.good(), p.fromTanks()) * k;
                if (!(h > 0)) continue;
                outOfTanks.put(p.good(), new Node(xTank, y, TANK_W, h));
                places.add(new Place("stored:" + p.good(), xTank, y, TANK_W, h));
                y += h;
            }
            y += BLOCK_WORDS;
        }
        if (imported > 0) {
            y += BLOCK_GAP;
            impY = y;
            for (Product p : v.products()) {
                double h = litresOf(p.good(), p.imported()) * k;
                if (!(h > 0)) continue;
                landed.put(p.good(), new Node(xTank, y, TANK_W, h));
                places.add(new Place("landed:" + p.good(), xTank, y, TANK_W, h));
                y += h;
            }
        }

        /* ----- the takers ----- */
        double s4H = buyerLitres * k + (v.buyers().size() - 1) * BUYER_GAP;
        y = mid - s4H / 2;
        List<Node> takers = new ArrayList<>();
        for (Buyer b : v.buyers()) {
            double h = b.litres() * k;
            takers.add(new Node(xBuy, y, BUYER_W, h));
            places.add(new Place("taker:" + (takers.size() - 1), xBuy, y, BUYER_W, h));
            y += h + BUYER_GAP;
        }

        /* ----- the ribbons through the refinery: each link a hop, or two through its band ----- */
        List<Hop> hops = new ArrayList<>();
        for (Link l : v.links()) {
            double h = l.litres() * k;
            Node band = bandOf.get(l);
            if (l.from().cut() != null) {
                Node from = cuts.get(l.from().cut());
                String c = colour(l.from().cut());
                if (band != null) {
                    hops.add(new Hop(from, band, h, c, l.to().furnaces() ? FURNACE_EDGE : c));
                    if (!l.to().furnaces()) hops.add(new Hop(band, made.get(l.to().product()), h, c, colour(l.to().product())));
                } else {
                    hops.add(new Hop(from, unitNodes.get(l.to().unit()), h, c, c));
                }
            } else {
                Node from = unitNodes.get(l.from().unit());
                String c = colour(l.from().unit().feed());
                if (l.to().unit() != null) {
                    String carried = colour(l.to().unit().feed());
                    hops.add(new Hop(from, unitNodes.get(l.to().unit()), h, carried, carried));
                } else {
                    hops.add(new Hop(from, made.get(l.to().product()), h, c, colour(l.to().product())));
                }
            }
        }
        hops.removeIf(hop -> hop.from == null || hop.to == null);
        // Departures in the order of where they go, arrivals in the order of where they came from, top first.
        List<Hop> leaving = new ArrayList<>(hops);
        leaving.sort(java.util.Comparator.comparingDouble((Hop h) -> h.to.y).thenComparingDouble(h -> h.to.x));
        for (Hop hop : leaving) {
            hop.a0 = hop.from.outAt;
            hop.from.outAt += hop.h;
        }
        List<Hop> arriving = new ArrayList<>(hops);
        arriving.sort(java.util.Comparator.comparingDouble((Hop h) -> h.from.y).thenComparingDouble(h -> h.from.x));
        for (Hop hop : arriving) {
            hop.b0 = hop.to.inAt;
            hop.to.inAt += hop.h;
        }
        for (Hop hop : hops) {
            ribbons.add(new Ribbon(hop.from.right(), hop.a0, hop.a0 + hop.h, hop.to.x, hop.b0, hop.b0 + hop.h, hop.c0, hop.c1, .5, false));
        }

        /* ----- the tank's takers: each product's band to its takers, out of the tanks and landed, each taker's own ----- */
        List<Box> segments = new ArrayList<>();
        double[] takerIn = new double[takers.size()];
        for (int i = 0; i < takers.size(); i++) takerIn[i] = takers.get(i).y;
        for (Product p : v.products()) {
            Good g = p.good();
            Node band = made.get(g), stored = outOfTanks.get(g), land = landed.get(g);
            double fromRefinery = p.local() + p.exported();
            double bandShare = fromRefinery > 0 ? Math.min(1, p.made() / fromRefinery) : 1;
            double bandAt = band == null ? 0 : band.y, storedAt = stored == null ? 0 : stored.y, landAt = land == null ? 0 : land.y;
            for (int i = 0; i < takers.size(); i++) {
                Buyer b = v.buyers().get(i);
                Node t = takers.get(i);
                double take = b.local().getOrDefault(g, 0.0);
                boolean fromBandOnly = b.kind() == BuyerKind.TANKS || b.kind() == BuyerKind.IDLED;
                double fromBand = fromBandOnly ? take : take * bandShare, fromStore = take - fromBand;
                double hb = litresOf(g, fromBand) * k, hs = litresOf(g, fromStore) * k;
                double hi = litresOf(g, b.imported().getOrDefault(g, 0.0)) * k;
                if (hb > 0 && band != null) {
                    ribbons.add(new Ribbon(band.right(), bandAt, bandAt + hb, t.x, takerIn[i], takerIn[i] + hb, colour(g), colour(g), .55, false));
                    segments.add(new Box(t.x, takerIn[i], BUYER_W, Math.max(hb, .8), colour(g), 1, null, false, false, 0, null));
                    bandAt += hb;
                    takerIn[i] += hb;
                }
                if (hs > 0 && stored != null) {
                    ribbons.add(new Ribbon(stored.right(), storedAt, storedAt + hs, t.x, takerIn[i], takerIn[i] + hs, colour(g), colour(g), .35, false));
                    segments.add(new Box(t.x, takerIn[i], BUYER_W, Math.max(hs, .8), colour(g), .6, null, false, false, 0, null));
                    storedAt += hs;
                    takerIn[i] += hs;
                }
                if (hi > 0 && land != null) {
                    ribbons.add(new Ribbon(land.right(), landAt, landAt + hi, t.x, takerIn[i], takerIn[i] + hi, colour(g), colour(g), .9, true));
                    segments.add(new Box(t.x, takerIn[i], BUYER_W, Math.max(hi, .8), colour(g), 1, null, false, true, 0, null));
                    landAt += hi;
                    takerIn[i] += hi;
                }
            }
        }

        /* ----- the nodes over the ribbons ----- */
        // The column: its headings, dome and cap, its cuts, the frame.
        double cx = xCol, cw = COLUMN_W, headSlot = 2 * (cx - xSrc - SOURCE_W) + cw;
        words.add(new Words("THE COLUMN", cx + cw / 2, colY - 47, Face.SANS_STRONG, 9.5, TEXT_3, Align.MIDDLE, headSlot, false));
        words.add(new Words(columnWords(v), cx + cw / 2, colY - 34, Face.SANS, 10, TEXT_2, Align.MIDDLE, headSlot, false));
        words.add(new Words(gradeWords(v.mix()), cx + cw / 2, colY - 21, Face.SANS, 10, TEXT_2, Align.MIDDLE, headSlot, false));
        outlines.add(new Outline(String.format(java.util.Locale.ROOT, "M%.2f %.2f L%.2f %.2f Q%.2f %.2f %.2f %.2f L%.2f %.2f Z",
                cx, colY, cx, colY - 4, cx + cw / 2, colY - 14, cx + cw, colY - 4, cx + cw, colY), CAP, 1, FRAME));
        boxes.add(new Box(cx + cw / 2 - 3, colY - 12, 6, 6, CAP, 1, FRAME, false, false, 0, null));
        for (Stream c : COLUMN) {
            Node n = cuts.get(c);
            if (!(n.h > 0)) continue;
            boxes.add(new Box(cx, n.y, cw, n.h, colour(c), .92, null, false, false, 0,
                    cutName(c) + ": " + litres(n.h / k) + " of the month's run"));
            if (n.h >= WORDS_IN_BAND) {
                words.add(new Words(cutName(c), cx + 6, n.y + n.h / 2 + 3.5, Face.SANS_STRONG, 9.5,
                        c == Stream.RESIDUE || c == Stream.GAS_OIL ? TEXT : ON_BAND, Align.START, cw - 8, false));
            }
        }
        for (int i = 1; i < COLUMN.length; i++) {
            Node n = cuts.get(COLUMN[i]);
            if (n.y > colY && n.y < colY + colH) lines.add(new Line(cx, n.y, cx + cw, n.y, ON_BAND));
        }
        boxes.add(new Box(cx, colY, cw, colH, null, 1, FRAME, false, false, 0, null));
        boxes.add(new Box(cx + 10, colY + colH, cw - 20, 8, CAP, 1, FRAME, false, false, 0, null));

        // The bands through the units' columns, and the furnaces.
        for (Map.Entry<Link, Node> e : bandOf.entrySet()) {
            Link l = e.getKey();
            Node n = e.getValue();
            if (l.to().furnaces()) continue;
            boxes.add(new Box(n.x, n.y, n.w, Math.max(n.h, .5), colour(l.from().cut()), .5, null, false, false, 0,
                    cutName(l.from().cut()) + " to " + l.to().product().label().toLowerCase() + ", no unit between: "
                            + unitsWords(l.to().product(), l.litres())));
        }
        double slot = xTank - x1 - 6;
        if (furnaces != null) {
            boxes.add(new Box(furnaces.x, furnaces.y, furnaces.w, Math.max(furnaces.h, 3), FURNACE_FILL, 1, FURNACE_EDGE, false,
                    false, 3, "Residue burned in the furnaces: " + litres(v.burned()) + ". Residue sells as fuel oil only cut"
                            + " three to one with diesel, and the diesel ran out."));
            words.add(new Words("Furnaces", furnaces.x, furnaces.y - 5, Face.SANS_MEDIUM, 10.5, TEXT, Align.START, slot, true,
                    Words.run("  " + FURNACE_WORDS.formatted(litres(v.burned())), Face.SANS, 9.5, TEXT_2)));
            if (furnaces.h >= 18) marks.add(new Mark(Icon.FLAME, null, furnaces.x + furnaces.w / 2 - 8, furnaces.y + furnaces.h / 2 - 8,
                    16, FLARE));
        }

        // The units.
        boolean firstIsUnit = !slots.isEmpty() && slots.get(0).unit() != null;
        words.add(new Words(units.isEmpty() ? NO_UNITS : "CONVERSION UNITS", x1, s2Top - (firstIsUnit ? 0 : 12),
                Face.SANS_STRONG, 9.5, TEXT_3, Align.START, slot, false));
        for (Map.Entry<Kind, Node> e : unitNodes.entrySet()) {
            Unit u = units.get(e.getKey());
            Node n = e.getValue();
            boxes.add(new Box(n.x, n.y, n.w, Math.max(n.h, 3), UNIT_FILL, 1, UNIT_EDGE, false, false, 3, unitTip(u)));
            double unitSlot = xTank - n.x - 6;
            words.add(new Words(unitName(u), n.x, n.y - 5, Face.SANS_MEDIUM, 10.5, TEXT, Align.START, unitSlot, true,
                    Words.run("  " + unitWords(u), Face.SANS, 9.5, u.idle() ? HOT : TEXT_2)));
            if (n.h >= 22) marks.add(new Mark(Icon.UNIT, null, n.x + n.w / 2 - 8, n.y + n.h / 2 - 8, 16, "#8fb3e0"));
        }

        // The tank: its heading, roof, stack and flare, its bands, and the blocks under it.
        double tx = xTank, tw = TANK_W;
        words.add(new Words("WHAT IT MADE", tx + tw / 2, tankY - 48, Face.SANS_STRONG, 9.5, TEXT_3, Align.MIDDLE, tw + 60, false));
        words.add(new Words(litres(tankRun) + " · " + madeCount + (madeCount == 1 ? " product" : " products"), tx + tw / 2,
                tankY - 35, Face.SANS, 10, TEXT_2, Align.MIDDLE, tw + 60, false));
        boxes.add(new Box(tx + tw - 22, tankY - 28, 7, 28, CAP, 1, FRAME, false, false, 0, null));
        outlines.add(new Outline(String.format(java.util.Locale.ROOT, "M%.2f %.2f Q%.2f %.2f %.2f %.2f Q%.2f %.2f %.2f %.2f Z",
                tx + tw - 18.5, tankY - 29, tx + tw - 22.5, tankY - 35, tx + tw - 18.5, tankY - 40, tx + tw - 14.5, tankY - 35,
                tx + tw - 18.5, tankY - 29), FLARE, .85, null));
        outlines.add(new Outline(String.format(java.util.Locale.ROOT, "M%.2f %.2f L%.2f %.2f L%.2f %.2f L%.2f %.2f Z",
                tx, tankY, tx + 10, tankY - 12, tx + tw - 30, tankY - 12, tx + tw - 24, tankY), CAP, 1, FRAME));
        boolean first = true;
        for (Product p : v.products()) {
            Node n = made.get(p.good());
            if (n == null) continue;
            Good g = p.good();
            boxes.add(new Box(tx, n.y, tw, n.h, colour(g), .95, null, false, false, 0, productTip(p)));
            if (!first) lines.add(new Line(tx, n.y, tx + tw, n.y, ON_BAND));
            first = false;
            if (n.h >= WORDS_IN_BAND + 1) {
                String tone = g == Good.BITUMEN || g == Good.COKE || g == Good.FUEL_OIL ? TEXT : ON_BAND;
                words.add(new Words(g.label(), tx + 7, n.y + n.h / 2 + 3.5, Face.SANS_STRONG, 10, tone, Align.START,
                        tw - 7 - 6 - FIGURE_IN_BAND, false));
                words.add(new Words(figure(g, p.run()), tx + tw - 6, n.y + n.h / 2 + 3.5, Face.MONO_STRONG, 10, tone, Align.END,
                        FIGURE_IN_BAND, false));
            }
        }
        boxes.add(new Box(tx, tankY, tw, tankH, null, 1, FRAME, false, false, 0, null));
        boxes.add(new Box(tx + 8, tankY + tankH, tw - 16, 7, CAP, 1, FRAME, false, false, 0, null));
        if (!outOfTanks.isEmpty()) {
            double h = 0;
            for (Map.Entry<Good, Node> e : outOfTanks.entrySet()) {
                Node n = e.getValue();
                Product p = v.product(e.getKey());
                boxes.add(new Box(tx, n.y, tw, Math.max(n.h, LEAST_NODE), colour(e.getKey()), .45, null, false, false, 0,
                        e.getKey().label() + " taken out of the refiners' tanks: " + unitsWords(e.getKey(), p.fromTanks())));
                h += n.h;
            }
            boxes.add(new Box(tx, fromY, tw, Math.max(h, LEAST_NODE), null, 1, FRAME, true, false, 0, null));
            words.add(new Words("FROM THE TANKS", tx, fromY + h + 13, Face.SANS_STRONG, 9.5, TEXT_3, Align.START, tw - BLOCK_FIGURE, false));
            words.add(new Words(litres(fromTanks), tx + tw, fromY + h + 13, Face.MONO, 10, TEXT_2, Align.END, BLOCK_FIGURE, false));
        }
        if (!landed.isEmpty()) {
            double h = 0;
            for (Map.Entry<Good, Node> e : landed.entrySet()) {
                Node n = e.getValue();
                Product p = v.product(e.getKey());
                boxes.add(new Box(tx, n.y, tw, Math.max(n.h, LEAST_NODE), colour(e.getKey()), 1, null, false, true, 0,
                        e.getKey().label() + " imported: " + unitsWords(e.getKey(), p.imported())));
                h += n.h;
            }
            boxes.add(new Box(tx, impY, tw, Math.max(h, LEAST_NODE), null, 1, IMPORT_EDGE, true, false, 0, null));
            words.add(new Words("IMPORTED", tx, impY + h + 13, Face.SANS_STRONG, 9.5, TEXT_3, Align.START, tw - BLOCK_FIGURE, false));
            words.add(new Words(litres(imported), tx + tw, impY + h + 13, Face.MONO, 10, HOT, Align.END, BLOCK_FIGURE, false));
        }

        // The takers: their segments, and their labels spread apart.
        boxes.addAll(segments);
        double[] at = new double[takers.size()];
        for (int i = 0; i < at.length; i++) at[i] = takers.get(i).y + takers.get(i).h / 2;
        spread(at, LABEL_SPACING, 18, height - 24);
        double lx = xBuy + BUYER_W + 10;
        words.add(new Words("WHO TOOK IT", xBuy, Math.min(mid - s4H / 2, at.length == 0 ? mid : at[0] - 15) - 16, Face.SANS_STRONG,
                9.5, TEXT_3, Align.START, width - xBuy, false));
        for (int i = 0; i < takers.size(); i++) {
            Buyer b = v.buyers().get(i);
            Node n = takers.get(i);
            double cyb = n.y + n.h / 2, t = at[i];
            if (Math.abs(cyb - t) > 3) lines.add(new Line(xBuy + BUYER_W + 1, cyb, lx - 4, t, FRAME));
            boolean abroad = b.kind() == BuyerKind.ABROAD;
            marks.add(new Mark(buyerIcon(b.kind()), b.sector(), lx, t - 15, 18, abroad ? TEXT_3 : "#c3ccd3"));
            words.add(new Words(buyerName(b), lx + 24, t - 3, Face.SANS_MEDIUM, 11.5, TEXT, Align.START,
                    width - (lx + 24) - FIGURE_ROOM - 8, false));
            words.add(new Words(buyerFigure(b), width - 4, t - 3, Face.MONO, 11, TEXT_2, Align.END, FIGURE_ROOM, false));
            words.add(new Words(buyerWords(b), lx + 24, t + 11, Face.SANS, 9.5, b.importedLitres() > 0 ? HOT : TEXT_3,
                    Align.START, width - (lx + 24) - 4, false));
            boxes.add(new Box(n.x, n.y, n.w, Math.max(n.h, .8), null, 1, null, false, false, 0, buyerTip(b)));
        }
        return new Picture(width, height, k, Collections.unmodifiableList(ribbons), Collections.unmodifiableList(boxes),
                Collections.unmodifiableList(outlines), Collections.unmodifiableList(lines), Collections.unmodifiableList(marks),
                Collections.unmodifiableList(words), Collections.unmodifiableList(places));
    }

    /** One ribbon while it is laid out: its two nodes, its height, its colours, and where it leaves and arrives. */
    private static final class Hop {
        final Node from, to;
        final double h;
        final String c0, c1;
        double a0, b0;
        Hop(Node from, Node to, double h, String c0, String c1) {
            this.from = from;
            this.to = to;
            this.h = h;
            this.c0 = c0;
            this.c1 = c1;
        }
    }

    /** The gap over slot `i` in the units' columns: a unit's two lines of words; a band after a unit a little; a band after a band PASS_GAP. */
    private static double gapBefore(List<Slot> slots, int i) {
        if (slots.get(i).unit() != null || slots.get(i).pass().to().furnaces()) return UNIT_GAP;
        if (i == 0) return 0;
        Slot before = slots.get(i - 1);
        return before.unit() != null || before.pass().to().furnaces() ? AFTER_UNIT_GAP : PASS_GAP;
    }

    /** The gap under a unit's box before a band runs on. */
    static final double AFTER_UNIT_GAP = 6;

    /** The room under each of the tank's blocks for its line ("IMPORTED" and its litres): its baseline 13 px under, and the line's descent. */
    static final double BLOCK_WORDS = 16;

    /** The edge of the tank's imported block (mockup 1's). */
    public static final String IMPORT_EDGE = "#5a6d84";

    /** The furnaces' line under their name. */
    static final String FURNACE_WORDS = "burned %s · no diesel to cut it";

    /** The units' heading when none stands. */
    static final String NO_UNITS = "NO CONVERSION UNIT · EACH CUT SOLD AS IT IS";

    /** The room a product's figure has at the right of its band in the tank. */
    static final double FIGURE_IN_BAND = 46;

    /** ...and a block's litres at the right of its line under it. */
    static final double BLOCK_FIGURE = 52;

    /** The room a figure has at the right of a band or a taker's name. */
    static final double FIGURE_ROOM = 58;

    private static double side(Map<Kind, double[]> sides, Kind k) {
        double[] s = sides.get(k);
        return s == null ? 0 : Math.max(s[0], s[1]);
    }

    private static Node furnacesOf(Map<Link, Node> passOf) {
        for (Map.Entry<Link, Node> e : passOf.entrySet()) if (e.getKey().to().furnaces()) return e.getValue();
        return null;
    }

    /** Where a link arrives, for the order its source sends it in: the band it runs through, or its end. */
    private static double arrivalY(Link l, java.util.function.Function<End, Node> target, Map<Link, Node> passOf) {
        Node via = passOf.get(l);
        Node to = via != null ? via : target.apply(l.to());
        return to == null ? Double.MAX_VALUE : to.y;
    }

    /** Where a link leaves, for the order its end takes it in: the band it ran through, or its source. */
    private static double departureY(Link l, java.util.function.Function<End, Node> source, Map<Link, Node> passOf) {
        Node via = passOf.get(l);
        Node from = via != null ? via : source.apply(l.from());
        return from == null ? Double.MAX_VALUE : from.y;
    }

    /** The takers' labels spread apart: each pair closer than `least` pushed apart evenly, held between `top` and `bottom` (mockup 1's sixty rounds). */
    static void spread(double[] at, double least, double top, double bottom) {
        for (int round = 0; round < 60; round++) {
            for (int i = 1; i < at.length; i++) {
                double dy = at[i] - at[i - 1];
                if (dy < least) {
                    double m = (least - dy) / 2;
                    at[i] += m;
                    at[i - 1] -= m;
                }
            }
            for (int i = 0; i < at.length; i++) at[i] = Math.max(top, Math.min(bottom, at[i]));
        }
    }

    /* =====================================================================
       THE WORDS
       ===================================================================== */

    /** Litres in three or four figures: "9.68M L", "830k L", "640 L", "23.0B L" - never more than eight characters short of a sign. */
    public static String litres(double l) {
        return Double.isFinite(l) ? compact(l) + " L" : "—";
    }

    /** Tonnes, whole: "8,306 t". */
    public static String tonnes(double t) {
        return Double.isFinite(t) ? String.format("%,.0f t", t) : "—";
    }

    /** A quantity in three or four figures: "9.68M", "830k", "640", "23.0B". */
    static String compact(double v) {
        double a = Math.abs(v);
        if (a >= 1e10) return String.format("%.1fB", v / 1e9);
        if (a >= 1e9) return String.format("%.2fB", v / 1e9);
        if (a >= 1e7) return String.format("%.1fM", v / 1e6);
        if (a >= 1e6) return String.format("%.2fM", v / 1e6);
        if (a >= 1e4) return String.format("%,.0fk", v / 1e3);
        return String.format("%,.0f", v);
    }

    /** A product's quantity in its own unit, short, for a band or a strip's cell: litres "2.00M" ("830k", "640"), tonnes "1,234 t" ("830k t"). */
    public static String figure(Good g, double units) {
        if (!Double.isFinite(units)) return "—";
        return Double.isFinite(g.litresPerTonne()) ? compact(units) : compact(units) + " t";
    }

    /** ...and with its unit: "2.00M L", "1,234 t". */
    public static String unitsWords(Good g, double units) {
        return Double.isFinite(g.litresPerTonne()) ? litres(units) : tonnes(units);
    }

    /** A price a litre or a tonne, as the screens write one (ui/Money.unitPrice()): "$0.5620", "$642.50". */
    public static String price(double thousands) {
        if (!Double.isFinite(thousands)) return "—";
        double d = thousands * 1000, a = Math.abs(d);
        if (a >= 1_000) return String.format("$%,.0f", d);
        if (a >= 1) return String.format("$%,.2f", d);
        if (a > 0) return String.format("$%.4f", d);
        return "$0";
    }

    /** A spread a litre, signed: "+$0.0840", "−$0.0310". */
    public static String spreadWords(double thousands) {
        if (!Double.isFinite(thousands)) return "—";
        String p = price(Math.abs(thousands));
        return (thousands < 0 && !"$0".equals(p) ? "−" : "+") + p;
    }

    /** Barrels a day of a feed a month, to three figures: "2,000 b/d" (an Oil Refinery's 8,300 t), "240 b/d". */
    public static String barrels(double litresAMonth) {
        double b = litresAMonth / RefineryFlow.LITRES_A_MONTH_PER_BARREL_A_DAY;
        if (!(b > 0)) return "0 b/d";
        double unit = Math.pow(10, Math.max(0, Math.floor(Math.log10(b)) - 2));
        return String.format("%,.0f b/d", Math.round(b / unit) * unit);
    }

    /** The column's line: its barrels a day and the share it ran: "2,000 b/d · runs 100%". */
    static String columnWords(View v) {
        return barrels(v.flow().crude()) + String.format(" · runs %.0f%%", v.rate() * 100);
    }

    /** A mix of grades in words: "medium crude", "light 62% · medium 38%". */
    public static String gradeWords(double[] mix) {
        if (mix == null) return "";
        StringBuilder s = new StringBuilder();
        int shown = 0, only = -1;
        for (int i = 0; i < mix.length; i++) if (mix[i] > 0.0005) { shown++; only = i; }
        if (shown == 1) return Deposit.Grade.values()[only].name().toLowerCase() + " crude";
        for (int i = 0; i < mix.length; i++) {
            if (!(mix[i] > 0.0005)) continue;
            if (s.length() > 0) s.append(" · ");
            s.append(Deposit.Grade.values()[i].name().toLowerCase()).append(' ').append(Math.round(mix[i] * 100)).append('%');
        }
        return s.toString();
    }

    /** A source's name: "Land wells ×22", "Platform wells ×4", "Strategic reserve", "Imported crude", "Tank farm". */
    public static String sourceName(Source s) {
        return switch (s.kind()) {
            case LAND_WELLS -> "Land wells" + (s.count() > 0 ? " ×" + s.count() : "");
            case PLATFORMS -> "Platform wells" + (s.count() > 0 ? " ×" + s.count() : "");
            case RESERVE -> "Strategic reserve";
            case IMPORTED -> "Imported crude";
            case TANK_FARM -> "Tank farm";
        };
    }

    /** ...its line: the tonnes and the grade - "8,306 t · light", "8,306 t · mixed grades" (the column gives the run's shares), "1,200 t released". */
    public static String sourceWords(Source s) {
        return switch (s.kind()) {
            case RESERVE -> tonnes(s.tonnes()) + " released";
            case TANK_FARM -> tonnes(s.tonnes()) + " kept on hand";
            case IMPORTED -> tonnes(s.tonnes()) + " · medium";
            default -> tonnes(s.tonnes()) + " · " + oneGrade(s.grades());
        };
    }

    /** A mix's one grade in a word, or "mixed grades". */
    static String oneGrade(double[] mix) {
        String w = gradeWords(mix);
        return w.endsWith(" crude") ? w.substring(0, w.length() - " crude".length()) : "mixed grades";
    }

    static Icon sourceIcon(SourceKind k) {
        return switch (k) {
            case LAND_WELLS -> Icon.WELL;
            case PLATFORMS -> Icon.PLATFORM;
            case RESERVE -> Icon.RESERVE;
            case IMPORTED -> Icon.TANKER;
            case TANK_FARM -> Icon.TANK;
        };
    }

    /** A unit's name over its box: "Reformer", "Cracking Unit ×2". */
    public static String unitName(Unit u) {
        return u.kind().unitName() + (u.standing() > 1 ? " ×" + u.standing() : "");
    }

    /** ...and its line after its name (mockup 1's): "300 b/d · 100%", "300 b/d · idle" - its spread and its gate are under the pointer (unitTip()). */
    public static String unitWords(Unit u) {
        String b = barrels(u.feed());
        if (u.idle()) return b + " · idle";
        return b + " · " + Math.round(u.share() * 100) + "%";
    }

    /** Under the pointer, a unit: its run, its spread at the local and at the city's own prices, what idles it, and one more weighed by the planner. */
    public static String unitTip(Unit u) {
        StringBuilder s = new StringBuilder(u.kind().unitName()).append(": ").append(u.standing()).append(" standing");
        if (u.onSite() > 0) s.append(", ").append(u.onSite()).append(" on site");
        s.append(". Takes ").append(u.kind().feed().words()).append("; ").append(barrels(u.feed())).append(" of it.\n");
        if (u.idle()) {
            s.append(u.spread() > 0 ? "Idle: no " + u.kind().feed().words() + " was left for it."
                    : "Idle: at today's prices what it makes is worth no more than its feed.");
        } else {
            s.append("Took ").append(litres(u.run())).append(", ").append(Math.round(u.share() * 100)).append("% of what it could.");
        }
        s.append("\nSpread a litre of feed: ").append(spreadWords(u.spread())).append(" at the city's prices, ")
                .append(spreadWords(u.citySpread())).append(" at what one more litre would fetch.");
        SpreadPlanner.Candidate c = u.gate();
        if (c != null) {
            s.append("\nOne more, ").append(c.template().getName()).append(": ");
            if (c.passes()) {
                s.append(String.format("passes every gate - it would earn %s a month on %s, %.2f%% a month.",
                        Formats.INSTANCE.amount(c.earns()), Formats.INSTANCE.amount(c.cost()), 100 * c.score()));
            } else {
                s.append(gateName(c.failed())).append(": ").append(c.why()).append('.');
            }
        }
        return s.toString();
    }

    /** A planner's gate in a word. */
    public static String gateName(SpreadPlanner.Gate g) {
        if (g == null) return "passes";
        return switch (g) {
            case FEED -> "no feed";
            case LAND -> "no ground";
            case STAFF -> "no staff";
            case MONEY -> "does not pay";
        };
    }

    /** Under the pointer, a product's band: made, idled, its takers. */
    static String productTip(Product p) {
        Good g = p.good();
        StringBuilder s = new StringBuilder(g.label()).append(": made ").append(unitsWords(g, p.made()));
        if (p.idled() > 0) s.append(", ").append(unitsWords(g, p.idled())).append(" idled");
        s.append(". Taken here ").append(unitsWords(g, p.local())).append(", shipped ").append(unitsWords(g, p.exported()));
        if (p.imported() > 0) s.append("; imported ").append(unitsWords(g, p.imported()));
        return s.append('.').toString();
    }

    /** A taker's name: "Cars", "Vans & lorries", "Rail", "Factories", "Roads", a sector's label, "Into the tanks", "Idled", "Abroad". */
    public static String buyerName(Buyer b) {
        return switch (b.kind()) {
            case CARS -> "Cars";
            case VANS -> "Vans & lorries";
            case RAIL -> "Rail";
            case FACTORIES -> "Factories";
            case ROADS -> "Roads";
            case SECTOR -> b.sector();
            case TANKS -> "Into the tanks";
            case IDLED -> "Idled";
            case ABROAD -> "Abroad";
        };
    }

    static Icon buyerIcon(BuyerKind k) {
        return switch (k) {
            case CARS -> Icon.CAR;
            case VANS -> Icon.LORRY;
            case RAIL -> Icon.TRAIN;
            case FACTORIES -> Icon.FACTORY;
            case ROADS -> Icon.ROAD;
            case SECTOR -> Icon.SECTOR;
            case TANKS -> Icon.TANK;
            case IDLED -> Icon.IDLE;
            case ABROAD -> Icon.GLOBE;
        };
    }

    /** A taker's figure: its litres, or its tonnes when it took only bitumen or coke. */
    public static String buyerFigure(Buyer b) {
        double l = 0, t = 0;
        for (Good g : TANK_ORDER) {
            double u = b.of(g);
            if (Double.isFinite(g.litresPerTonne())) l += u; else t += u;
        }
        return l > 0 || !(t > 0) ? litres(l) : tonnes(t);
    }

    /**
     * ...and its line: what it took (mockup 1's), and with some of it
     * imported, the product and how much - "petrol at the pump", "diesel ·
     * 0.10M L of it imported".
     */
    public static String buyerWords(Buyer b) {
        double imp = b.importedLitres();
        if (imp > 0) return goodsWords(b, 1) + " · " + litres(imp) + " of it imported";
        return switch (b.kind()) {
            case CARS -> "petrol at the pump";
            case VANS -> "diesel, every business's vans";
            case RAIL -> "diesel · " + (int) Rail.FUEL_LITRES_PER_TONNE + " L a tonne hauled";
            case FACTORIES -> "lubricants";
            case ROADS -> "bitumen for paving";
            case TANKS -> "kept for next month";
            case IDLED -> "no buyer at a price that pays";
            case ABROAD, SECTOR -> goodsWords(b, LIST_MOST);
        };
    }

    /** The most products a taker's line names before "and more". */
    static final int LIST_MOST = 3;

    /** The goods a taker took, largest first, at most `most`, by their short names: "fuel oil, jet fuel, gas and more". */
    static String goodsWords(Buyer b, int most) {
        List<Good> gs = new ArrayList<>();
        for (Good g : TANK_ORDER) if (b.of(g) > 0) gs.add(g);
        gs.sort((x, y) -> Double.compare(litresOf(y, b.of(y)), litresOf(x, b.of(x))));
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < Math.min(most, gs.size()); i++) {
            if (i > 0) s.append(", ");
            s.append(shortName(gs.get(i)));
        }
        if (gs.size() > most) s.append(" and more");
        return s.toString();
    }

    /** A product in a list: its name, the gas and the coke shortened as mockup 1 shortens them. */
    static String shortName(Good g) {
        return g == Good.LPG ? "gas" : g == Good.COKE ? "coke" : g.label().toLowerCase();
    }

    /** Under the pointer, a taker: each product it took, home and imported. */
    static String buyerTip(Buyer b) {
        StringBuilder s = new StringBuilder(buyerName(b)).append(':');
        for (Good g : TANK_ORDER) {
            double u = b.of(g);
            if (!(u > 0)) continue;
            s.append("\n").append(g.label()).append(' ').append(unitsWords(g, u));
            double imp = b.imported().getOrDefault(g, 0.0);
            if (imp > 0) s.append(", ").append(unitsWords(g, imp)).append(" of it imported");
        }
        return s.toString();
    }

    /** The scale, for the heading's right: "10 px of ribbon = 1.30M L". */
    public static String scaleWords(double pxALitre) {
        return pxALitre > 0 ? "10 px of ribbon = " + litres(10 / pxALitre) : "";
    }

    /* =====================================================================
       THE STRIP: THIS MONTH, BY PRODUCT
       ===================================================================== */

    /**
     * One product's cell under the picture (mockup 1's strip).
     *
     * @param share {taken here from the refinery, imported, exported} each a share of the three, for its bar
     */
    public record Cell(Good good, String name, String made, String imported, boolean hot, String exported, String price,
                       String tanks, double[] share) { }

    /** The strip's heading words. */
    public static final String STRIP_HEAD = "THIS MONTH, BY PRODUCT";

    /** ...and its line. */
    public static final String STRIP_WORDS = "litres (M a million, k a thousand), bitumen and coke in tonnes · bar: taken here,"
            + " imported (hatched), exported (pale)";

    /** The strip's cells, in TANK_ORDER: every product the month made, took or shipped. */
    public static List<Cell> strip(View v) {
        List<Cell> cells = new ArrayList<>();
        for (Product p : v.products()) {
            Good g = p.good();
            double all = p.local() + p.imported() + p.exported();
            double[] share = all > 0 ? new double[] { p.local() / all, p.imported() / all, p.exported() / all } : new double[3];
            String tanks = p.tanks() > 0 ? "+" + figure(g, p.tanks()) : p.tanks() < 0 ? "−" + figure(g, -p.tanks()) : "—";
            cells.add(new Cell(g, g.label(), figure(g, p.made()), p.imported() > 0 ? figure(g, p.imported()) : "—",
                    p.imported() > 0, p.exported() > 0 ? figure(g, p.exported()) : "—", price(p.price()), tanks, share));
        }
        return cells;
    }

    /** The strip's line names, in a cell's order. */
    public static final String[] CELL_LINES = { "made here", "imported", "exported", "tanks", "price here" };

    /** What the card says when there is no month to draw: no crude unit, a month not counted, a month that ran nothing. */
    public static String emptyWords(View v) {
        if (!v.standing()) return "No crude unit stands: the refinery has nothing to draw.";
        if (!v.counted()) return "Not drawn until a month runs: the month's figures are not saved.";
        return "The crude units ran nothing this month.";
    }
}
