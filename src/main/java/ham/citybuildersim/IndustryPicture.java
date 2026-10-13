package ham.citybuildersim;

import ham.citybuildersim.IndustryView.Feed;
import ham.citybuildersim.IndustryView.Origin;
import ham.citybuildersim.IndustryView.Part;
import ham.citybuildersim.IndustryView.Product;
import ham.citybuildersim.IndustryView.Unit;
import ham.citybuildersim.IndustryView.Vessel;
import ham.citybuildersim.IndustryView.View;
import ham.citybuildersim.RefineryView.Align;
import ham.citybuildersim.RefineryView.Box;
import ham.citybuildersim.RefineryView.Face;
import ham.citybuildersim.RefineryView.Line;
import ham.citybuildersim.RefineryView.Outline;
import ham.citybuildersim.RefineryView.Place;
import ham.citybuildersim.RefineryView.Ribbon;
import ham.citybuildersim.RefineryView.Words;
import ham.citybuildersim.sectors.CoalMining;
import ham.citybuildersim.sectors.Forestry;
import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.Quarrying;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Each materials industry's month drawn as a picture, laid out to scale
 * (0.7.143, batch I12; docs/studies/spec-materials.md 2.13, the study's
 * mockups 1 to 5): the fields as vessels filled to what remains, or the feeds
 * by where they came from, the imports hatched; each kind of its works with
 * its run and the gate one more would fail; the tank of what it made under
 * its industry's roof; who took it; the power its works made and sold - every
 * ribbon one scale in the chain's own unit, and the strip of cells under it.
 *
 * WHY. IndustryView (batch I11) holds each of the nine industries' month as
 * figures, words and colours, and its ★1 left the layout to this batch: the
 * refinery keeps its picture in the model (RefineryView's THE PICTURE) so a
 * harness can hold its scale, and the materials pictures are the refinery
 * pictogram made general (the study's 11). Worked out in the screen, a
 * ribbon that did not foot to the tonnes beside it, a taker's bar taller than
 * what it took, or a label run off the card's edge at 1,389 x 868 would be
 * checked only by eye. Here the flow is traced first - each good's month
 * split over the kinds that made and used it - and laid out second, and
 * IndustryViewCheck holds both; ui/SectorScreen's OPERATIONS only paints
 * them, with Refining's painter.
 *
 * THE FLOW (trace()). Every quantity is IndustryView's: a product's made is
 * split over the kinds that make it in proportion to what each made at its
 * run (Unit.makes()), a feed's parts over the kinds that use it by what each
 * used (Unit.uses()); what a works kept for its own (captive) runs from its
 * makers to its users; what left the works runs into its band in the tank,
 * and from the band - and from the warehouse, when the month sold more than
 * it made - to whoever took it. A mine's ribbon from its field is what it
 * made times its site's dig (the magnetite's three tonnes of rock a tonne,
 * the copper field's copper). Three industries are drawn by their own rule:
 * Coal Mining's coking coal is the wash plant's, worked back from the month's
 * coking coal at CoalMining's yields, with the raw coal the underground mines
 * send it; Quarrying's quarries on ANY dig the stone of the field the packing
 * puts them on (Resource.goodOf()), and the limestone no buyer took runs
 * into the aggregate's band (Quarrying, THE CARRY); Forestry's salvage cut
 * comes from the cleared forest, half and half (Forestry.SAWLOG_SHARE).
 *
 * THE UNIT. Ribbons are drawn in cubic metres where every good the industry
 * draws is counted in them (Forestry), and in tonnes otherwise
 * (Good.tonnesPerUnit()); a figure is written in its good's own unit.
 * ★ Copper is drawn in tonnes of the good, not of copper contained: the model
 * holds no copper content for a good (the concentrate's 30% is a comment).
 *
 * Pure: it reads a View and builds nothing in the city.
 */
public final class IndustryPicture {

    private IndustryPicture() { }

    /* =====================================================================
       THE FLOW, TRACED
       ===================================================================== */

    /** A node's key: a field by type, the salvage cut, a feed by its good and where it came from, a kind of works, the lorries, a product's band, what left the warehouse, a taker. */
    public static String fieldKey(Deposit.FieldType t) { return "field:" + t; }
    /** ...the forest cleared for building, Forestry's salvage cut. */
    public static final String SALVAGE = "salvage";
    /** ...a feed's part: "source:IRON:HOME", "source:SAWLOGS:WORLD", "source:STEEL:OLD_METAL" (Iron & Steel's old steel, not a good). */
    public static String sourceKey(Good g, Origin o) { return "source:" + g + ":" + o; }
    /** ...a kind of works by its permanent id. */
    public static String unitKey(Unit u) { return "unit:" + u.template().getId(); }
    /** ...the lorries' diesel. */
    public static final String LORRIES = "lorries";
    /** ...a product's band in the tank: what left the works. */
    public static String bandKey(Good g) { return "made:" + g; }
    /** ...what was taken out of its warehouse. */
    public static String storedKey(Good g) { return "stored:" + g; }
    /** ...a flow's band through the r-th rank of works' column: "pass:1:unit:44>made:COKE>COKE". */
    public static String passKey(int rank, Flow f) { return "pass:" + rank + ":" + f.from() + ">" + f.to() + ">" + f.good(); }
    /** ...a taker: a party in the city (a sector's key, Trade.HOUSEHOLDS, Trade.CITY), or ABROAD, WAREHOUSE, LOST. */
    public static String takerKey(String who) { return "taker:" + who; }
    /** The takers that are not a party: the world, the warehouse, what was lost. */
    public static final String ABROAD = "ABROAD", WAREHOUSE = "WAREHOUSE", LOST = "LOST";

    /**
     * Units of a good, or of a field's ground, a month from one node to another.
     *
     * @param good  the good; null for a field's ground, the raw coal a mine sends its wash plant, and the old steel
     * @param units in the good's own unit (a field's: its resource's; the old steel and the raw coal: tonnes)
     * @param drawn ...and in the picture's (unitOf())
     */
    public record Flow(String from, String to, Good good, double units, double drawn) { }

    /** The least part of a feed, a sale or the old metal the picture draws, in its good's unit: under half a unit its figure reads "0 t", and it is left out (I11's ★11: Iron & Steel buys a sliver of its own scrap). */
    public static final double LEAST_PART = .5;

    /** The unit a picture is drawn in: "m³" where every good the industry makes or uses is counted in cubic metres (Forestry), "t" otherwise. */
    public static String unitOf(View v) {
        List<Good> made = new ArrayList<>();
        for (Product p : v.products()) made.add(p.good());
        // ...an industry that made nothing yet by the goods its plant is for.
        if (made.isEmpty()) made.addAll(v.sector().goodsMade());
        boolean m3 = !made.isEmpty();
        for (Good g : made) m3 &= isCubic(g);
        for (Feed f : v.feeds()) m3 &= isCubic(f.good());
        return m3 ? "m³" : "t";
    }

    static boolean isCubic(Good g) {
        return "cubic metre".equals(g.unit());
    }

    /** Units of a good in the picture's unit: its own cubic metres in a picture in them, else its tonnes. */
    public static double drawn(View v, Good g, double units) {
        if (g == null) return units;
        return "m³".equals(unitOf(v)) && isCubic(g) ? units : units * g.tonnesPerUnit();
    }

    /** A field's ground in the picture's unit: a mineral field's tonnes, the forest's cubic metres - as tonnes, in a picture in tonnes, at the logs' weight. */
    public static double drawnGround(View v, Resource r, double amount) {
        if (r == Resource.FOREST && !"m³".equals(unitOf(v))) return amount * Good.SAWLOGS.tonnesPerCubicMetre();
        return amount;
    }

    /** The month traced end to end (pure): every flow from a field, the salvage, a feed or a kind of works to a kind, a band or a taker; empty when there is no month to draw. */
    public static List<Flow> trace(View v) {
        List<Flow> out = new ArrayList<>();
        if (v == null || !v.drawn()) return out;
        List<Unit> built = new ArrayList<>();
        for (Unit u : v.units()) if (u.standing() > 0) built.add(u);
        Map<String, Flow> merged = new LinkedHashMap<>();

        /* ----- the products: each split over its makers, and where each maker's part went ----- */
        // dug: what each kind dug, by the good it dug it as (null: the raw coal), for its ribbon from the field.
        Map<Unit, Map<Good, Double>> dug = new LinkedHashMap<>();
        if (v.sector() instanceof CoalMining) {
            coal(v, built, merged, dug);
        } else {
            for (Product p : v.products()) {
                Good g = p.good();
                double made = Math.max(0, p.made());
                if (v.sector() instanceof Forestry && v.forest() != null) {
                    double salvage = Math.min(made, v.forest().salvage() * (g == Good.SAWLOGS ? Forestry.SAWLOG_SHARE
                            : 1 - Forestry.SAWLOG_SHARE));
                    if (salvage > 0) add(merged, v, SALVAGE, bandKey(g), g, salvage);
                    made -= salvage;
                }
                if (!(made > 0)) continue;
                Map<Unit, Double> share = split(made, makers(v, built, g));
                if (share.isEmpty()) continue;
                double kept = clamp(p.captive(), made), carried = clamp(p.carriedOut(), made - kept);
                Map<Unit, Double> users = split(kept, users(built, g));
                for (Map.Entry<Unit, Double> e : share.entrySet()) {
                    Unit u = e.getKey();
                    double a = e.getValue(), f = a / made;
                    dug.computeIfAbsent(u, x -> new LinkedHashMap<>()).merge(g, a, Double::sum);
                    for (Map.Entry<Unit, Double> to : users.entrySet()) {
                        add(merged, v, unitKey(u), unitKey(to.getKey()), g, f * to.getValue());
                    }
                    if (carried > 0) add(merged, v, unitKey(u), bandKey(Good.AGGREGATE), g, f * carried);
                    add(merged, v, unitKey(u), bandKey(g), g, f * (made - kept - carried));
                }
            }
        }

        /* ----- the fields: each kind's dig, from the field its ground is ----- */
        for (Map.Entry<Unit, Map<Good, Double>> e : dug.entrySet()) {
            Unit u = e.getKey();
            for (Map.Entry<Good, Double> d : e.getValue().entrySet()) {
                Vessel field = fieldOf(v, u, d.getKey());
                if (field == null) continue;
                BuildingsTemplate.Site site = u.template().site();
                double ground = d.getValue() * (site == null ? 1 : site.dig());
                Flow f = new Flow(fieldKey(field.type()), unitKey(u), null, ground, drawnGround(v, field.resource(), ground));
                merge(merged, f);
            }
        }

        /* ----- the feeds: each part over its users, the lorries' diesel to the lorries ----- */
        for (Feed f : v.feeds()) {
            Good g = f.good();
            Map<Unit, Double> users = users(built, g);
            double bought = Math.max(0, f.home()) + Math.max(0, f.imported());
            double lorries = g == Good.DIESEL ? clamp(f.lorries(), bought) : 0;
            double toLorries = bought > 0 ? lorries / bought : 0;
            for (Origin o : new Origin[] { Origin.HOME, Origin.WORLD }) {
                double part = Math.max(0, o == Origin.HOME ? f.home() : f.imported());
                if (!(part >= LEAST_PART)) continue;
                if (toLorries > 0) add(merged, v, sourceKey(g, o), LORRIES, g, part * toLorries);
                for (Map.Entry<Unit, Double> e : split(part * (1 - toLorries), users).entrySet()) {
                    add(merged, v, sourceKey(g, o), unitKey(e.getKey()), g, e.getValue());
                }
            }
            if (f.oldMetal() >= LEAST_PART) {
                Map<Unit, Double> takers = oldMetalUsers(built, Good.COPPER);
                if (takers.isEmpty()) takers = users;
                for (Map.Entry<Unit, Double> e : split(f.oldMetal(), takers).entrySet()) {
                    add(merged, v, sourceKey(g, Origin.OLD_METAL), unitKey(e.getKey()), g, e.getValue());
                }
            }
        }
        // ...and Iron & Steel's old steel, which is not a good: the yards' purchase.
        if (v.sector() instanceof HeavyIndustry && v.oldMetal() != null && v.oldMetal().bought() >= LEAST_PART) {
            for (Map.Entry<Unit, Double> e : split(v.oldMetal().bought(), oldMetalUsers(built, Good.STEEL)).entrySet()) {
                merge(merged, new Flow(sourceKey(Good.STEEL, Origin.OLD_METAL), unitKey(e.getKey()), null, e.getValue(),
                        e.getValue()));
            }
        }

        /* ----- the takers: each band to whoever took it, and the warehouse's part ----- */
        for (Product p : v.products()) {
            Good g = p.good();
            double band = 0;
            for (Flow f : merged.values()) if (f.to().equals(bandKey(g))) band += f.units();
            double stored = Math.max(0, -p.intoStock());
            if (!(band + stored > 0)) continue;
            double fromBand = band / (band + stored);
            for (Part part : takers(p)) {
                if (!(part.units() >= LEAST_PART)) continue;
                String to = takerKey(part.origin() == Origin.WORLD ? ABROAD : part.party());
                boolean bandOnly = part.party() != null && (part.party().equals(WAREHOUSE) || part.party().equals(LOST));
                double take = part.units(), a = bandOnly ? take : take * fromBand;
                if (a > 0) add(merged, v, bandKey(g), to, g, a);
                if (take - a > 0) add(merged, v, storedKey(g), to, g, take - a);
            }
        }
        out.addAll(merged.values());
        out.removeIf(f -> !(f.units() > 0));
        return Collections.unmodifiableList(out);
    }

    /** A product's takers in drawing order: each buyer in the city, into the warehouse, lost, the world - a part's party WAREHOUSE or LOST for those two. */
    public static List<Part> takers(Product p) {
        List<Part> out = new ArrayList<>(p.buyers());
        if (p.intoStock() > 0) out.add(new Part(Origin.HOME, WAREHOUSE, p.intoStock()));
        if (p.lost() > 0) out.add(new Part(Origin.HOME, LOST, p.lost()));
        if (p.exported() > 0) out.add(new Part(Origin.WORLD, null, p.exported()));
        return out;
    }

    /**
     * Coal Mining's month (the spec's 2.10): the coking coal is the wash
     * plants', washed = coking ÷ COKING_YIELD out of the raw coal the
     * underground mines sent them, the middlings THERMAL_YIELD of it; the rest
     * of the thermal coal the surface mines' and the raw coal no plant washed,
     * split as the month's wash (CoalMining.lift()) splits them.
     */
    static void coal(View v, List<Unit> built, Map<String, Flow> merged, Map<Unit, Map<Good, Double>> dug) {
        List<Unit> surface = new ArrayList<>(), underground = new ArrayList<>(), plants = new ArrayList<>();
        for (Unit u : built) {
            BuildingsTemplate.Site site = u.template().site();
            if (u.template().getWash() > 0) plants.add(u);
            else if (site != null && site.type() == Deposit.FieldType.COKING) underground.add(u);
            else if (site != null) surface.add(u);
        }
        Product coking = v.product(Good.COKING_COAL), thermal = v.product(Good.THERMAL_COAL);
        double c = coking == null ? 0 : Math.max(0, coking.made()), t = thermal == null ? 0 : Math.max(0, thermal.made());
        double washed = plants.isEmpty() ? 0 : c / CoalMining.COKING_YIELD, middlings = washed * CoalMining.THERMAL_YIELD;
        double rest = Math.max(0, t - Math.min(middlings, t));
        CoalMining.Flow w = v.wash();
        double surf = w == null ? 0 : w.surface(), unwashedNow = w == null ? 0 : Math.max(0, w.raw() - w.washed());
        double toSurface = surface.isEmpty() ? 0 : underground.isEmpty() || surf + unwashedNow <= 0 ? rest
                : rest * surf / (surf + unwashedNow);
        double unwashed = underground.isEmpty() ? 0 : rest - toSurface;
        if (surface.isEmpty() && underground.isEmpty()) toSurface = unwashed = 0;
        // The surface mines' thermal coal.
        for (Map.Entry<Unit, Double> e : split(toSurface, weights(surface, Good.THERMAL_COAL)).entrySet()) {
            add(merged, v, unitKey(e.getKey()), bandKey(Good.THERMAL_COAL), Good.THERMAL_COAL, e.getValue());
            dug.computeIfAbsent(e.getKey(), x -> new LinkedHashMap<>()).merge(Good.THERMAL_COAL, e.getValue(), Double::sum);
        }
        // The underground mines' raw coal: unwashed, sold as thermal; washed, to the plants.
        Map<Unit, Double> sendShare = split(1, weights(underground, Good.THERMAL_COAL));
        Map<Unit, Double> plantShare = split(1, washWeights(plants));
        for (Map.Entry<Unit, Double> e : sendShare.entrySet()) {
            Unit u = e.getKey();
            double s = e.getValue();
            if (unwashed * s > 0) add(merged, v, unitKey(u), bandKey(Good.THERMAL_COAL), Good.THERMAL_COAL, unwashed * s);
            for (Map.Entry<Unit, Double> p : plantShare.entrySet()) {
                double raw = washed * s * p.getValue();
                if (raw > 0) merge(merged, new Flow(unitKey(u), unitKey(p.getKey()), null, raw, raw));
            }
            double all = (unwashed + (plantShare.isEmpty() ? 0 : washed)) * s;
            if (all > 0) dug.computeIfAbsent(u, x -> new LinkedHashMap<>()).merge(Good.COKING_COAL, all, Double::sum);
        }
        // The wash plants' coking coal and middlings.
        for (Map.Entry<Unit, Double> p : plantShare.entrySet()) {
            if (c * p.getValue() > 0) add(merged, v, unitKey(p.getKey()), bandKey(Good.COKING_COAL), Good.COKING_COAL, c * p.getValue());
            double mid = Math.min(middlings, t) * p.getValue();
            if (mid > 0) add(merged, v, unitKey(p.getKey()), bandKey(Good.THERMAL_COAL), Good.THERMAL_COAL, mid);
        }
    }

    /** The raw coal the month washed and the spoil it left, tonnes (Coal Mining's wash, as trace() works it): {washed, kept, spoil}; nothing for any other industry. */
    public static double[] washOf(View v) {
        if (!(v.sector() instanceof CoalMining) || v.product(Good.COKING_COAL) == null) return new double[3];
        double c = Math.max(0, v.product(Good.COKING_COAL).made()), washed = c / CoalMining.COKING_YIELD;
        return new double[] { washed, washed * (CoalMining.COKING_YIELD + CoalMining.THERMAL_YIELD), washed * CoalMining.SPOIL_YIELD };
    }

    /** The field a kind dug a good from: its site's type, or for a kind on ANY the type whose ground is that good (Resource.goodOf()); the forest for Forestry's crews; null where the city holds none. */
    static Vessel fieldOf(View v, Unit u, Good g) {
        BuildingsTemplate.Site site = u.template().site();
        for (Vessel f : v.fields()) {
            if (site == null) {
                if (f.resource() == Resource.FOREST) return f;
            } else if (site.resource() == f.resource()) {
                if (site.any() ? f.resource().goodOf(f.type()) == g : site.type() == f.type()) return f;
            }
        }
        return null;
    }

    /** What each kind standing made of a good at its run (Unit.makes()); Quarrying's quarries on ANY by what they dig, whichever stone it is (Quarrying's rule). */
    static Map<Unit, Double> makers(View v, List<Unit> built, Good g) {
        Map<Unit, Double> w = new LinkedHashMap<>();
        boolean quarry = v.sector() instanceof Quarrying && (g == Good.LIMESTONE || g == Good.AGGREGATE);
        for (Unit u : built) {
            BuildingsTemplate t = u.template();
            boolean any = quarry && t.site() != null && t.site().any();
            double made = any ? (t.makes(Good.AGGREGATE) + t.makes(Good.LIMESTONE)) * u.standing() * u.run() : u.makes(g);
            if (made > 0) w.put(u, made);
        }
        if (w.isEmpty()) {
            // ...a month run at nothing: at nameplate.
            for (Unit u : built) {
                BuildingsTemplate t = u.template();
                boolean any = quarry && t.site() != null && t.site().any();
                double made = (any ? t.makes(Good.AGGREGATE) + t.makes(Good.LIMESTONE) : t.makes(g)) * u.standing();
                if (made > 0) w.put(u, made);
            }
        }
        return w;
    }

    /** What each kind standing used of a good at its run (Unit.uses()), or at nameplate where none ran. */
    static Map<Unit, Double> users(List<Unit> built, Good g) {
        Map<Unit, Double> w = new LinkedHashMap<>();
        for (Unit u : built) if (u.uses(g) > 0) w.put(u, u.uses(g));
        if (w.isEmpty()) for (Unit u : built) if (u.template().uses(g) * u.standing() > 0) w.put(u, u.template().uses(g) * u.standing());
        return w;
    }

    /** The kinds that take the city's old steel or old copper, by what they take at their run. */
    static Map<Unit, Double> oldMetalUsers(List<Unit> built, Good metal) {
        Map<Unit, Double> w = new LinkedHashMap<>();
        for (Unit u : built) {
            double take = u.template().oldMetal(metal) * u.standing();
            if (take > 0) w.put(u, take * Math.max(u.run(), 1e-9));
        }
        return w;
    }

    static Map<Unit, Double> weights(List<Unit> units, Good g) {
        Map<Unit, Double> w = new LinkedHashMap<>();
        for (Unit u : units) w.put(u, Math.max(u.makes(g), 1e-9 * u.template().makes(g) * u.standing()));
        return w;
    }

    static Map<Unit, Double> washWeights(List<Unit> plants) {
        Map<Unit, Double> w = new LinkedHashMap<>();
        for (Unit u : plants) w.put(u, u.template().getWash() * u.standing() * Math.max(u.run(), 1e-9));
        return w;
    }

    /** `total` split in proportion to the weights; empty where they add to nothing. */
    static Map<Unit, Double> split(double total, Map<Unit, Double> weights) {
        Map<Unit, Double> out = new LinkedHashMap<>();
        double sum = 0;
        for (double w : weights.values()) sum += Math.max(0, w);
        if (!(sum > 0) || !(total > 0)) return out;
        for (Map.Entry<Unit, Double> e : weights.entrySet()) if (e.getValue() > 0) out.put(e.getKey(), total * e.getValue() / sum);
        return out;
    }

    static double clamp(double x, double most) {
        return Math.max(0, Math.min(x, Math.max(0, most)));
    }

    private static void add(Map<String, Flow> merged, View v, String from, String to, Good g, double units) {
        if (!(units > 0)) return;
        merge(merged, new Flow(from, to, g, units, drawn(v, g, units)));
    }

    private static void merge(Map<String, Flow> merged, Flow f) {
        if (!(f.units() > 0)) return;
        String key = f.from() + ">" + f.to() + ">" + f.good();
        Flow was = merged.get(key);
        merged.put(key, was == null ? f : new Flow(f.from(), f.to(), f.good(), was.units() + f.units(), was.drawn() + f.drawn()));
    }

    /* =====================================================================
       THE NODES: the sources, the works by rank, the tank, the takers
       ===================================================================== */

    /**
     * The ranks of the works, left to right: a kind that uses a good its own
     * industry keeps for itself (Sector.captive()) stands right of the kinds
     * that make it - coke ovens, then the blast furnace, then the oxygen shop;
     * Coal Mining's wash plants right of its underground mines; every other
     * kind in the first.
     */
    public static Map<Unit, Integer> ranks(View v) {
        List<Unit> kinds = v.built();
        Map<Unit, Integer> rank = new LinkedHashMap<>();
        for (Unit u : kinds) rank.put(u, 0);
        for (int pass = 0; pass < kinds.size(); pass++) {
            boolean moved = false;
            for (Unit b : kinds) {
                int r = 0;
                for (Unit a : kinds) if (a != b && feeds(v, a, b)) r = Math.max(r, rank.get(a) + 1);
                if (r != rank.get(b) && r < kinds.size()) {
                    rank.put(b, r);
                    moved = true;
                }
            }
            if (!moved) break;
        }
        return rank;
    }

    /** Whether kind `a` hands kind `b` what it makes: a good its industry keeps for its own works, or Coal Mining's raw coal. */
    static boolean feeds(View v, Unit a, Unit b) {
        if (v.sector() instanceof CoalMining) {
            BuildingsTemplate.Site site = a.template().site();
            return b.template().getWash() > 0 && site != null && site.type() == Deposit.FieldType.COKING;
        }
        for (Good g : a.template().goodsMade().keySet()) {
            if (b.template().uses(g) > 0 && v.sector().captive(g)) return true;
        }
        return false;
    }

    /* =====================================================================
       THE PICTURE (the study's 11: the oil mockups' frame at 1,389 x 868,
       the refinery pictogram made general)

       Four kinds of column, left to right: the sources - a mine's fields as
       vessels filled to what remains with the bar of what it dug beside each,
       or a works' feeds by where they came from, the imports hatched; the
       works in ranks; the tank of what left the works, by product, under the
       industry's roof, and under it what was taken out of the warehouse; and
       the takers. Every ribbon is its quantity times one scale, the largest
       at which every column fits; a works' box is as tall as the larger of
       what went in and what came out, the rest what its process takes away
       (the slag, the spoil, the rock). Ribbons leave and arrive in the order
       of the node at their other end, top first.
       ===================================================================== */

    /** The picture's height on the page (the materials mockups' 420 px at 1,389 x 868). */
    public static final double HEIGHT = 420;

    /** The least width it is laid out at (RefineryView's: the 1,280 window's card is about 1,094). */
    public static final double LEAST_WIDTH = RefineryView.LEAST_WIDTH;

    /** Room above the columns for their headings and a line of notes (the mockups' 60). */
    public static final double TOP = 60;

    /** ...the headings' baselines, their lines' and the notes' (the mockups' 14, 27 and 46). */
    static final double HEAD_Y = 14, SUB_Y = 27, NOTE_Y = 46;

    /** ...and the margin under and over what the columns hold. */
    static final double PAD = 4;

    /** Where the sources' bar stands: its words, an icon and a field's vessel at its left (the mockups' 196 to 232). */
    static final double SOURCE_ZONE = 214;

    /** The sources' bar (the mockups' 10 and 12). */
    static final double SOURCE_W = 12;

    /** A field's vessel, and its gap to the bar (the mockups' 22 x 40, 12). */
    static final double VESSEL_W = 22, VESSEL_H = 40, VESSEL_GAP = 12;

    /** The least room a field's row takes: its vessel and three lines of words (the mockups' 42). */
    static final double FIELD_ROOM = 44;

    /** The takers' room at the right: the bar, an icon, a name and its figure, a line of words (RefineryView's). */
    static final double BUYER_ZONE = 236;

    /** The takers' bar. */
    static final double BUYER_W = 12;

    /** A works' box (the mockups' 54 to 64). */
    static final double UNIT_W = 60;

    /** The tank's width: a product's name and its figure inside its band (the mockups' 136 to 156). */
    static final double TANK_W = 150;

    /** The room between the tank and the takers' bar, for the ribbons to cross (the mockups' 44 to 60). */
    static final double TANK_TO_TAKERS = 60;

    /** The gap over a works' box: its three lines of words - its name, its run, one more in a word. */
    static final double UNIT_GAP = 42;

    /** ...the room between the sources' bar and the first rank of works, for the ribbons to turn; and the room the last rank's words have before the tank. */
    static final double FIRST_RANK_GAP = 70, LAST_RANK_ROOM = 130;

    /** The share of its size a letter of the picture's words is set at, at most: a capital's and a lower-case letter's or a figure's (IBM Plex Sans's average is .55, measured at 1,389 x 868 by the batch's probe; Plex Mono's every letter .6). */
    static final double CAPITAL = .74, LETTER = .6;

    /** The gap between the sources, between the fields' rows, between the takers, and over the warehouse's block. */
    static final double SOURCE_GAP = 22, FIELD_GAP = 14, BUYER_GAP = 12, BLOCK_GAP = 22;

    /** The room under the warehouse's block for its line: its baseline 13 px under, and the line's descent. */
    static final double BLOCK_WORDS = 16;

    /** The room over the tank for its roof: the furnace's stack and flame, the tallest. */
    static final double ROOF_ROOM = 48;

    /** The room under the tank for its foot (the mockups' 7), when no warehouse's block stands under it. */
    static final double FOOT_ROOM = 8;

    /** The least a source's or a taker's label centre stands from the next (the mockups' 30 and 31). */
    static final double SOURCE_SPACING = 30, LABEL_SPACING = 31;

    /** The least band a product's name and figure are written in (the mockups' 12). */
    static final double WORDS_IN_BAND = 12;

    /** The room a product's figure has at the right of its band, and a taker's at the right of its name. */
    static final double FIGURE_IN_BAND = 52, FIGURE_ROOM = 56;

    /** The gap over a band through a works' column (RefineryView's PASS_GAP). */
    static final double PASS_GAP = 3;

    /** The least a node is drawn, so a trickle shows; and a kind only on site, drawn dashed with nothing through it. */
    static final double LEAST_NODE = 1.5, ON_SITE_NODE = 6;

    /** The least a box is drawn with an icon in it (the mockups' 22). */
    static final double ICON_ROOM = 22;

    /** The opacity of a ribbon, and of an imported one, hatched (the mockups' .5 and .95). */
    static final double RIBBON = .5, HATCHED = .95;

    /** The icons the picture draws, by what they stand for; the screen picks each one's outline. */
    public enum Icon { FIELD, FOREST, SECTOR, HOUSEHOLDS, CITY, GLOBE, SHIP, OLD_METAL, WAREHOUSE, LOST, WORKS, LORRY, POWER }

    /** An icon, `size` px square from its top left; `key` a sector's key for SECTOR, the roof for WORKS, the resource for FIELD. */
    public record Mark(Icon icon, String key, double x, double y, double size, String colour) { }

    /**
     * The picture, in painting order: the ribbons, then the boxes over them,
     * the outlines, lines, icons and words; and its nodes by name (the flow's
     * keys).
     *
     * @param scale px a unit of `unit`
     * @param unit  "t" or "m³" (unitOf())
     */
    public record Picture(double width, double height, double scale, String unit, List<Ribbon> ribbons, List<Box> boxes,
                          List<Outline> outlines, List<Line> lines, List<Mark> marks, List<Words> words, List<Place> places) {
        public boolean empty() { return ribbons.isEmpty() && boxes.isEmpty(); }

        /** A node by its name, or null. */
        public Place place(String key) {
            for (Place p : places) if (p.key().equals(key)) return p;
            return null;
        }

        /** Every word it writes, a line each, in painting order. */
        public List<String> texts() {
            List<String> out = new ArrayList<>();
            for (Words w : words) out.add(w.line());
            return out;
        }
    }

    /** A node while the picture is laid out: its key, what passes through it, its least height, the gap over it, and where it stands. */
    private static final class Node {
        final String key;
        final double q, least, gap;
        double x, y, w, h, inAt, outAt, inQ, outQ;
        Node(String key, double q, double least, double gap) {
            this.key = key;
            this.q = q;
            this.least = least;
            this.gap = gap;
        }
        double right() { return x + w; }
    }

    /** The picture of `v` at a width and a height (pure); empty when there is no month to draw. `game` names the parties (IndustryView.partyWords()). */
    public static Picture picture(Game game, View v, double width, double height) {
        List<Ribbon> ribbons = new ArrayList<>();
        List<Box> boxes = new ArrayList<>();
        List<Outline> outlines = new ArrayList<>();
        List<Line> lines = new ArrayList<>();
        List<Mark> marks = new ArrayList<>();
        List<Words> words = new ArrayList<>();
        List<Place> places = new ArrayList<>();
        String unit = v == null ? "t" : unitOf(v);
        Picture none = new Picture(width, height, 0, unit, ribbons, boxes, outlines, lines, marks, words, places);
        if (v == null || !v.drawn()) return none;
        List<Flow> flows = trace(v);
        if (flows.isEmpty()) return none;
        Map<String, double[]> sides = new LinkedHashMap<>();
        for (Flow f : flows) {
            sides.computeIfAbsent(f.from(), x -> new double[2])[1] += f.drawn();
            sides.computeIfAbsent(f.to(), x -> new double[2])[0] += f.drawn();
        }

        /* ----- the columns ----- */
        // The sources: the fields, the salvage, each feed's parts.
        List<Node> sources = new ArrayList<>();
        boolean extraction = !v.fields().isEmpty();
        for (Vessel f : v.fields()) {
            Node n = new Node(fieldKey(f.type()), out(sides, fieldKey(f.type())), FIELD_ROOM, sources.isEmpty() ? 0 : FIELD_GAP);
            sources.add(n);
        }
        int fieldRows = sources.size();
        if (out(sides, SALVAGE) > 0) sources.add(new Node(SALVAGE, out(sides, SALVAGE), LEAST_NODE, sources.isEmpty() ? 0 : SOURCE_GAP));
        List<String> feedKeys = new ArrayList<>();
        for (Feed f : v.feeds()) {
            for (Origin o : new Origin[] { Origin.HOME, Origin.OLD_METAL, Origin.WORLD }) feedKeys.add(sourceKey(f.good(), o));
        }
        feedKeys.add(sourceKey(Good.STEEL, Origin.OLD_METAL));
        for (String key : feedKeys) {
            double q = out(sides, key);
            if (q > 0 && !has(sources, key)) sources.add(new Node(key, q, LEAST_NODE, sources.isEmpty() ? 0 : SOURCE_GAP));
        }
        // The works, by rank; the lorries at the foot of the first.
        Map<Unit, Integer> rankOf = ranks(v);
        int rankCount = 1;
        for (int r : rankOf.values()) rankCount = Math.max(rankCount, r + 1);
        List<List<Node>> ranks = new ArrayList<>();
        for (int r = 0; r < rankCount; r++) ranks.add(new ArrayList<>());
        Map<String, Unit> unitOfKey = new LinkedHashMap<>();
        for (Map.Entry<Unit, Integer> e : rankOf.entrySet()) {
            Unit u = e.getKey();
            String key = unitKey(u);
            unitOfKey.put(key, u);
            double q = Math.max(in(sides, key), out(sides, key));
            ranks.get(e.getValue()).add(new Node(key, q, u.standing() > 0 ? LEAST_NODE : ON_SITE_NODE, UNIT_GAP));
        }
        if (in(sides, LORRIES) > 0) ranks.get(0).add(new Node(LORRIES, in(sides, LORRIES), LEAST_NODE, UNIT_GAP));
        // The tank: each band, then what left the warehouse.
        List<Node> bands = new ArrayList<>(), stored = new ArrayList<>();
        for (Product p : v.products()) {
            double q = Math.max(in(sides, bandKey(p.good())), out(sides, bandKey(p.good())));
            if (q > 0) bands.add(new Node(bandKey(p.good()), q, LEAST_NODE, 0));
        }
        for (Product p : v.products()) {
            double q = out(sides, storedKey(p.good()));
            if (q > 0) stored.add(new Node(storedKey(p.good()), q, LEAST_NODE, stored.isEmpty() ? BLOCK_GAP : 0));
        }
        List<Node> tank = new ArrayList<>(bands);
        tank.addAll(stored);
        // The takers, in drawing order: the city's by party, the warehouse, the lost, the world.
        List<String> takerKeys = new ArrayList<>();
        for (Flow f : flows) if (f.to().startsWith("taker:") && !takerKeys.contains(f.to())) takerKeys.add(f.to());
        takerKeys.sort(Comparator.comparingInt(IndustryPicture::takerRank));
        List<Node> takers = new ArrayList<>();
        for (String key : takerKeys) takers.add(new Node(key, in(sides, key), LEAST_NODE, takers.isEmpty() ? 0 : BUYER_GAP));
        // The bands through the works' columns: a flow that skips a rank - the ore to the blast furnace past the coke ovens, the
        // coke to the tank past the furnace - runs straight through that rank's column, beside its works (the refinery's rule).
        Map<String, Integer> columnOf = new LinkedHashMap<>();
        for (Node n : sources) columnOf.put(n.key, 0);
        for (int r = 0; r < rankCount; r++) for (Node n : ranks.get(r)) columnOf.put(n.key, r + 1);
        for (Node n : tank) columnOf.put(n.key, rankCount + 1);
        for (Node n : takers) columnOf.put(n.key, rankCount + 2);
        Map<Flow, List<String>> through = new LinkedHashMap<>();
        Map<String, Flow> passOf = new LinkedHashMap<>();
        for (Flow f : flows) {
            Integer a = columnOf.get(f.from()), b = columnOf.get(f.to());
            if (a == null || b == null || b - a < 2) continue;
            List<String> keys = new ArrayList<>();
            for (int c = a + 1; c < b && c <= rankCount; c++) {
                String key = passKey(c - 1, f);
                ranks.get(c - 1).add(new Node(key, f.drawn(), 0, PASS_GAP));
                keys.add(key);
                passOf.put(key, f);
            }
            through.put(f, keys);
        }

        /* ----- one scale: the largest at which every column fits ----- */
        double usable = height - TOP - 2 * PAD;
        List<List<Node>> columns = new ArrayList<>();
        columns.add(sources);
        columns.addAll(ranks);
        columns.add(takers);
        double k = Double.POSITIVE_INFINITY;
        for (int pass = 0; pass < 4; pass++) {
            double kk = Double.POSITIVE_INFINITY;
            for (List<Node> c : columns) kk = Math.min(kk, fit(c, usable, k));
            kk = Math.min(kk, fit(tank, usable - ROOF_ROOM - (stored.isEmpty() ? FOOT_ROOM : BLOCK_WORDS), k));
            k = kk;
        }
        if (!(k > 0) || !Double.isFinite(k)) return none;
        for (List<Node> c : columns) size(c, k);
        size(tank, k);

        /* ----- where each column stands ----- */
        double right = Math.max(LEAST_WIDTH, width) - BUYER_ZONE;
        double xSrc = SOURCE_ZONE, srcRight = xSrc + SOURCE_W, xBuy = right, xTank = xBuy - TANK_TO_TAKERS - TANK_W;
        double mid = TOP + (height - TOP) / 2;
        stack(sources, xSrc, SOURCE_W, mid);
        double firstLeft = srcRight + FIRST_RANK_GAP, lastLeft = xTank - LAST_RANK_ROOM - UNIT_W;
        double[] rankAt = new double[rankCount];
        for (int r = 0; r < rankCount; r++) {
            rankAt[r] = rankCount == 1 ? (firstLeft + lastLeft) / 2 : firstLeft + (lastLeft - firstLeft) * r / (rankCount - 1);
            stack(ranks.get(r), rankAt[r], UNIT_W, mid);
        }
        stack(tank, xTank, TANK_W, TOP + ROOF_ROOM + (height - TOP - ROOF_ROOM - (stored.isEmpty() ? FOOT_ROOM : BLOCK_WORDS)) / 2);
        stack(takers, xBuy, BUYER_W, mid);
        Map<String, Node> nodes = new LinkedHashMap<>();
        for (List<Node> c : columns) for (Node n : c) nodes.put(n.key, n);
        for (Node n : tank) nodes.put(n.key, n);
        // ...each band through a column beside the works nearest the height it comes from, then each rank stacked again.
        if (!passOf.isEmpty()) {
            for (int r = 0; r < rankCount; r++) {
                Map<Node, Double> at = new java.util.IdentityHashMap<>();
                for (Node n : ranks.get(r)) {
                    Node by = passOf.containsKey(n.key) ? nodes.get(passOf.get(n.key).from()) : n;
                    at.put(n, by == null ? n.y : by.y + by.h / 2);
                }
                ranks.get(r).sort(Comparator.comparingDouble(at::get));
                stack(ranks.get(r), rankAt[r], UNIT_W, mid);
            }
        }
        apart(ranks, unitOfKey, v, TOP + PAD, height - PAD);
        for (Node n : nodes.values()) {
            double h = n.key.startsWith("field:") ? n.q * k : n.h;
            places.add(new Place(n.key, n.x, n.y, n.w, h));
        }

        /* ----- the ribbons, each from its node's right edge to the next's left ----- */
        record Hop(Flow f, Node from, Node to, double h, boolean last) { }
        List<Hop> hops = new ArrayList<>();
        for (Flow f : flows) {
            Node a = nodes.get(f.from()), b = nodes.get(f.to());
            if (a == null || b == null) continue;
            List<Node> chain = new ArrayList<>();
            chain.add(a);
            for (String key : through.getOrDefault(f, List.of())) chain.add(nodes.get(key));
            chain.add(b);
            for (int i = 0; i + 1 < chain.size(); i++) hops.add(new Hop(f, chain.get(i), chain.get(i + 1), f.drawn() * k, i + 2 == chain.size()));
        }
        Map<Hop, Double> leave = new java.util.IdentityHashMap<>(), arrive = new java.util.IdentityHashMap<>();
        List<Hop> leaving = new ArrayList<>(hops);
        leaving.sort(Comparator.comparingDouble((Hop h) -> h.to().y).thenComparingDouble(h -> h.to().x));
        for (Hop h : leaving) {
            leave.put(h, h.from().outAt);
            h.from().outAt += h.h();
        }
        List<Hop> arriving = new ArrayList<>(hops);
        arriving.sort(Comparator.comparingDouble((Hop h) -> h.from().y).thenComparingDouble(h -> h.from().x));
        for (Hop h : arriving) {
            arrive.put(h, h.to().inAt);
            h.to().inAt += h.h();
        }
        Map<String, List<Box>> segments = new LinkedHashMap<>();
        for (Hop h : hops) {
            Flow f = h.f();
            double a0 = leave.get(h), b0 = arrive.get(h);
            boolean hatched = f.from().startsWith("source:") && f.from().endsWith(":" + Origin.WORLD);
            String c0 = colourFrom(v, f), c1 = h.last() ? colourTo(v, f) : c0;
            double op = hatched ? HATCHED : f.from().startsWith("stored:") ? RIBBON * .7 : RIBBON;
            ribbons.add(new Ribbon(h.from().right(), a0, a0 + h.h(), h.to().x, b0, b0 + h.h(), c0, c1, op, hatched));
            if (!h.last()) {
                Node pass = h.to();
                boxes.add(new Box(pass.x, pass.y, pass.w, pass.h, c0, op, null, false, hatched, 0, null));
            }
            if (h.last() && f.to().startsWith("taker:")) {
                segments.computeIfAbsent(f.to(), x -> new ArrayList<>()).add(new Box(h.to().x, b0, BUYER_W, Math.max(h.h(), .8),
                        c1, f.from().startsWith("stored:") ? .6 : 1, null, false, false, 0, null));
            }
        }

        /* ----- the headings and the notes ----- */
        String srcHead = v.forest() != null ? "THE FOREST" : extraction ? "THE FIELDS" : "IN";
        String srcSub = v.forest() != null ? "what stands · what was cut" : extraction ? "how full · what it dug"
                : figureOf(unit, totalOut(sides, sources)) + " in";
        words.add(new Words(srcHead, 8, HEAD_Y, Face.SANS_STRONG, 9.5, RefineryView.TEXT_3, Align.START, srcRight - 8, false));
        words.add(new Words(srcSub, 8, SUB_Y, Face.SANS, 10, RefineryView.TEXT_2, Align.START, srcRight - 8, false));
        double rankX = ranks.get(0).isEmpty() ? firstLeft : ranks.get(0).get(0).x;
        double rankSlot = rankCount > 1 ? ranks.get(1).get(0).x - rankX - 8 : xTank - rankX - 8;
        words.add(new Words(worksHead(v), rankX, HEAD_Y, Face.SANS_STRONG, 9.5, RefineryView.TEXT_3, Align.START, rankSlot, false));
        if (rankCount > 1 && v.sector() instanceof CoalMining) {
            double x2 = ranks.get(1).get(0).x;
            words.add(new Words("WASHING", x2, HEAD_Y, Face.SANS_STRONG, 9.5, RefineryView.TEXT_3, Align.START, xTank - x2 - 8, false));
        }
        double tankQ = 0;
        for (Node n : bands) tankQ += n.q;
        words.add(new Words(tankHead(v), xTank + TANK_W / 2, HEAD_Y, Face.SANS_STRONG, 9.5, RefineryView.TEXT_3, Align.MIDDLE,
                TANK_W + 60, false));
        words.add(new Words(figureOf(unit, tankQ) + " · " + bands.size() + (bands.size() == 1 ? " product" : " products"),
                xTank + TANK_W / 2, SUB_Y, Face.SANS, 10, RefineryView.TEXT_2, Align.MIDDLE, TANK_W + 60, false));
        words.add(new Words("WHO TOOK IT", xBuy, HEAD_Y, Face.SANS_STRONG, 9.5, RefineryView.TEXT_3, Align.START, width - xBuy, false));
        String note = noteWords(v);
        // ...the power over the tank, or left of it as far as its line needs to end inside the frame.
        String powerLine = IndustryView.powerWords(v.power());
        double powerX = Math.min(xTank - 24, Math.max(LEAST_WIDTH, width) - 4 - widthOf(Words.run(powerLine, Face.SANS, 9.5, "")) - 18);
        if (!note.isEmpty()) {
            words.add(new Words(note, 8, NOTE_Y, Face.SANS, 9.5, RefineryView.TEXT_3, Align.START, powerX - 8 - 12, false));
        }
        if (v.power().made() > 0 || v.power().drawn() > 0) {
            marks.add(new Mark(Icon.POWER, null, powerX, NOTE_Y - 11, 14, IndustryView.POWER));
            words.add(new Words(powerLine, powerX + 18, NOTE_Y, Face.SANS, 9.5, RefineryView.TEXT_2,
                    Align.START, Math.max(LEAST_WIDTH, width) - powerX - 18, false));
        }

        /* ----- the sources: the fields' vessels and bars, the feeds' bars, their words ----- */
        List<Node> labelled = new ArrayList<>();
        for (int i = 0; i < sources.size(); i++) {
            Node n = sources.get(i);
            if (i < fieldRows) {
                Vessel f = v.fields().get(i);
                double bar = n.q * k;
                String colour = IndustryView.colour(f.resource(), f.type());
                if (bar > 0) boxes.add(new Box(n.x, n.y, SOURCE_W, Math.max(bar, LEAST_NODE), colour, 1, null, false, false, 1.5,
                        IndustryView.vesselWords(f) + " · dug this month: " + IndustryView.groundWords(f.resource(), n.q)));
                double vx = n.x - VESSEL_GAP - VESSEL_W, vy = n.y + Math.min(n.h, FIELD_ROOM) / 2 - VESSEL_H / 2;
                boxes.add(new Box(vx, vy, VESSEL_W, VESSEL_H, RefineryView.IMPORT_EDGE, .5, RefineryView.FRAME, false, true, 3,
                        IndustryView.vesselWords(f)));
                double left = f.filled();
                if (left > 0) boxes.add(new Box(vx + 1, vy + VESSEL_H * (1 - left), VESSEL_W - 2, Math.max(VESSEL_H * left - 1, .5),
                        colour, .9, null, false, false, 2, null));
                marks.add(new Mark(f.resource() == Resource.FOREST ? Icon.FOREST : Icon.FIELD, f.resource().name(), vx - 26,
                        vy + VESSEL_H / 2 - 10, 18, "#c9b68f"));
                double tx = vx - 32, cy = vy + VESSEL_H / 2;
                words.add(new Words(IndustryView.typeWords(f.resource(), f.type()), tx, cy - 8, Face.SANS_MEDIUM, 11.5,
                        RefineryView.TEXT, Align.END, tx, false));
                words.add(new Words(vesselLine(v, f), tx, cy + 6, Face.SANS, 10, RefineryView.TEXT_3, Align.END, tx, false));
                words.add(new Words(vesselFigure(f), tx, cy + 19, Face.MONO, 10, left < .4 ? RefineryView.HOT : RefineryView.TEXT_2,
                        Align.END, tx, false));
            } else {
                boolean hatched = n.key.endsWith(":" + Origin.WORLD);
                String colour = sourceColour(v, n.key);
                boxes.add(new Box(n.x, n.y, SOURCE_W, n.h, colour, 1, hatched ? colour : null, hatched, hatched, 1.5,
                        sourceTip(game, v, n.key)));
                labelled.add(n);
            }
        }
        double[] at = new double[labelled.size()];
        for (int i = 0; i < at.length; i++) at[i] = labelled.get(i).y + labelled.get(i).h / 2;
        double srcTop = fieldRows > 0 ? sources.get(fieldRows - 1).y + sources.get(fieldRows - 1).h + SOURCE_SPACING / 2 : TOP + 12;
        RefineryView.spread(at, SOURCE_SPACING, srcTop, height - 14);
        for (int i = 0; i < labelled.size(); i++) {
            Node n = labelled.get(i);
            double cy = n.y + n.h / 2, t = at[i], tx = n.x - 14;
            if (Math.abs(cy - t) > 3) lines.add(new Line(n.x - 1, cy, tx + 3, t, RefineryView.FRAME));
            marks.add(new Mark(sourceIcon(n.key), sellerKey(v, n.key), 0, t - 17, 18,
                    n.key.endsWith(":" + Origin.WORLD) ? RefineryView.TEXT_3 : "#c9b68f"));
            words.add(new Words(sourceName(v, n.key), tx, t - 4, Face.SANS_MEDIUM, 11.5, RefineryView.TEXT, Align.END, tx - 22, false));
            boolean hot = n.key.endsWith(":" + Origin.WORLD);
            words.add(new Words(sourceLine(game, v, n.key) + " · ", tx, t + 9, Face.SANS, 10, hot ? RefineryView.HOT : RefineryView.TEXT_3,
                    Align.END, tx - 22, false, Words.run(sourceFigure(v, n.key), Face.MONO, 10, RefineryView.TEXT_2)));
        }

        /* ----- the works ----- */
        for (List<Node> rank : ranks) {
            for (Node n : rank) {
                double slot = xTank - n.x - 8;
                if (n.key.equals(LORRIES)) {
                    boxes.add(new Box(n.x, n.y, n.w, n.h, RefineryView.UNIT_FILL, 1, RefineryView.UNIT_EDGE, true, false, 3,
                            "Its lorries burned " + IndustryView.unitsWords(Good.DIESEL, v.lorries().diesel()) + " of diesel, "
                                    + String.format("%,.0f", v.lorries().vans()) + " vans"));
                    words.add(new Words("Lorries", n.x, n.y - 18, Face.SANS_MEDIUM, 10.5, RefineryView.TEXT, Align.START, slot, true));
                    words.add(new Words(lorriesLine(v), n.x, n.y - 5, Face.SANS, 9.5, RefineryView.TEXT_2, Align.START, slot, true));
                    continue;
                }
                Unit u = unitOfKey.get(n.key);
                if (u == null) continue;
                boolean only = u.standing() <= 0;
                boxes.add(new Box(n.x, n.y, n.w, n.h, RefineryView.UNIT_FILL, only ? .5 : 1, only ? RefineryView.IMPORT_EDGE
                        : RefineryView.UNIT_EDGE, only, false, 3, unitTip(u)));
                String gate = gateLine(u);
                double y = gate.isEmpty() ? n.y - 18 : n.y - 31;
                words.add(new Words(unitName(u), n.x, y, Face.SANS_MEDIUM, 10.5, only ? RefineryView.TEXT_2 : RefineryView.TEXT,
                        Align.START, slot, true));
                words.add(new Words(runLine(u), n.x, y + 13, Face.SANS, 9.5, idle(u) ? RefineryView.HOT : RefineryView.TEXT_2,
                        Align.START, slot, true));
                if (!gate.isEmpty()) {
                    words.add(new Words(gate, n.x, n.y - 5, Face.SANS, 9.5, u.oneMore().passes() ? RefineryView.TEXT_2 : RefineryView.TEXT_3,
                            Align.START, slot, true));
                }
                if (n.h >= ICON_ROOM) marks.add(new Mark(Icon.WORKS, v.roof().name(), n.x + n.w / 2 - 8, n.y + n.h / 2 - 8, 16, "#8fb3e0"));
            }
        }

        /* ----- the tank: its roof, its bands, the warehouse's block ----- */
        if (!bands.isEmpty()) {
            double ty = bands.get(0).y, tb = bands.get(bands.size() - 1).y + bands.get(bands.size() - 1).h;
            for (int i = 0; i < bands.size(); i++) {
                Node n = bands.get(i);
                Good g = Good.valueOf(n.key.substring("made:".length()));
                Product p = v.product(g);
                boxes.add(new Box(xTank, n.y, TANK_W, n.h, IndustryView.colour(g), .95, null, false, false, 0, productTip(v, p)));
                if (i > 0) lines.add(new Line(xTank, n.y, xTank + TANK_W, n.y, RefineryView.ON_BAND));
                String fig = IndustryView.figure(g, n.q / drawn(v, g, 1));
                String tone = dark(g) ? RefineryView.TEXT : RefineryView.ON_BAND;
                if (n.h >= WORDS_IN_BAND) {
                    words.add(new Words(bandName(g), xTank + 7, n.y + n.h / 2 + 3.5, Face.SANS_STRONG, 10, tone, Align.START,
                            TANK_W - 13 - FIGURE_IN_BAND, false));
                    words.add(new Words(fig, xTank + TANK_W - 6, n.y + n.h / 2 + 3.5, Face.MONO_STRONG, 10, tone, Align.END,
                            FIGURE_IN_BAND, false));
                } else if (n.h >= 4) {
                    words.add(new Words(bandName(g) + " " + fig, xTank - 6, n.y + n.h / 2 + 3.5, Face.SANS, 9.5, RefineryView.TEXT_2,
                            Align.END, xTank - 6 - (lastLeft + UNIT_W) - 6, true));
                }
            }
            boxes.add(new Box(xTank, ty, TANK_W, tb - ty, null, 1, RefineryView.FRAME, false, false, 0, null));
            boxes.add(new Box(xTank + 8, tb, TANK_W - 16, 7, RefineryView.GROUND, 1, RefineryView.FRAME, false, false, 0, null));
            outlines.addAll(roof(v.roof(), xTank, ty, TANK_W));
        }
        if (!stored.isEmpty()) {
            double sy = stored.get(0).y, total = 0, h = 0;
            for (Node n : stored) {
                Good g = Good.valueOf(n.key.substring("stored:".length()));
                boxes.add(new Box(xTank, n.y, TANK_W, n.h, IndustryView.colour(g), .45, null, false, false, 0,
                        g.label() + " taken out of its warehouse: " + IndustryView.unitsWords(g, n.q / drawn(v, g, 1))));
                total += n.q;
                h += n.h;
            }
            boxes.add(new Box(xTank, sy, TANK_W, h, null, 1, RefineryView.FRAME, true, false, 0, null));
            words.add(new Words("OUT OF THE WAREHOUSE", xTank, sy + h + 13, Face.SANS_STRONG, 9.5, RefineryView.TEXT_3, Align.START,
                    TANK_W - FIGURE_IN_BAND, false));
            words.add(new Words(figureOf(unit, total), xTank + TANK_W, sy + h + 13, Face.MONO, 10, RefineryView.TEXT_2, Align.END,
                    FIGURE_IN_BAND, false));
        }

        /* ----- the takers: their segments, and their labels spread apart ----- */
        for (List<Box> s : segments.values()) boxes.addAll(s);
        double[] tat = new double[takers.size()];
        for (int i = 0; i < tat.length; i++) tat[i] = takers.get(i).y + takers.get(i).h / 2;
        RefineryView.spread(tat, LABEL_SPACING, TOP + 16, height - 16);
        double lx = xBuy + BUYER_W + 10, w = Math.max(LEAST_WIDTH, width);
        for (int i = 0; i < takers.size(); i++) {
            Node n = takers.get(i);
            String who = n.key.substring("taker:".length());
            double cy = n.y + n.h / 2, t = tat[i];
            if (Math.abs(cy - t) > 3) lines.add(new Line(xBuy + BUYER_W + 1, cy, lx - 4, t, RefineryView.FRAME));
            boolean abroad = who.equals(ABROAD);
            marks.add(new Mark(takerIcon(who), who, lx, t - 15, 18, abroad ? RefineryView.TEXT_3 : "#c3ccd3"));
            words.add(new Words(takerName(game, v, who), lx + 24, t - 3, Face.SANS_MEDIUM, 11.5, RefineryView.TEXT, Align.START,
                    w - (lx + 24) - FIGURE_ROOM - 4, false));
            words.add(new Words(takerFigure(v, flows, n.key), w - 4, t - 3, Face.MONO, 11, RefineryView.TEXT_2, Align.END, FIGURE_ROOM,
                    false));
            words.add(new Words(takerLine(v, flows, n.key), lx + 24, t + 11, Face.SANS, 9.5, RefineryView.TEXT_3, Align.START,
                    w - (lx + 24) - 4, false));
            boxes.add(new Box(n.x, n.y, n.w, Math.max(n.h, .8), null, 1, null, false, false, 0, takerTip(game, v, flows, n.key)));
        }
        return new Picture(width, height, k, unit, Collections.unmodifiableList(ribbons), Collections.unmodifiableList(boxes),
                Collections.unmodifiableList(outlines), Collections.unmodifiableList(lines), Collections.unmodifiableList(marks),
                Collections.unmodifiableList(words), Collections.unmodifiableList(places));
    }

    /** The most a line of words is set wide, px: each capital at CAPITAL of its size, every other letter at LETTER - an upper bound on IBM Plex at the picture's sizes. */
    public static double widthOf(Words w) {
        double t = 0;
        for (Words r = w; r != null; r = r.then()) {
            for (int i = 0; i < r.text().length(); i++) t += r.size() * (Character.isUpperCase(r.text().charAt(i)) ? CAPITAL : LETTER);
        }
        return t;
    }

    /** The words over a works' box, as {left, top, right, bottom}: its name and its lines, as wide as the widest (widthOf()); null for a band through the column, which has none. */
    private static double[] wordsOver(Node n, Unit u, View v) {
        if (n.key.startsWith("pass:")) return null;
        double wide;
        if (u == null) {
            wide = Math.max(widthOf(Words.run("Lorries", Face.SANS_MEDIUM, 10.5, "")), widthOf(Words.run(lorriesLine(v), Face.SANS, 9.5, "")));
        } else {
            wide = Math.max(widthOf(Words.run(unitName(u), Face.SANS_MEDIUM, 10.5, "")), Math.max(widthOf(Words.run(runLine(u), Face.SANS, 9.5,
                    "")), widthOf(Words.run(gateLine(u), Face.SANS, 9.5, ""))));
        }
        return new double[] { n.x, n.y - n.gap + 2, n.x + wide, n.y };
    }

    /**
     * Each rank of works moved up or down, whole, until no works' words run
     * into the next rank's boxes or words (a rank's words reach right, past
     * its boxes, toward the tank): the least move that clears them and keeps
     * the rank between `top` and `bottom`; a rank with no room to move stays.
     * A band through a column is no obstacle: words read over it on their
     * halo, as over any ribbon.
     */
    private static void apart(List<List<Node>> ranks, Map<String, Unit> unitOfKey, View v, double top, double bottom) {
        for (int r = 1; r < ranks.size(); r++) {
            List<Node> rank = ranks.get(r);
            if (rank.isEmpty()) continue;
            for (int pass = 0; pass < 8; pass++) {
                double move = 0;
                for (int q = 0; q < r && move == 0; q++) {
                    for (Node a : ranks.get(q)) {
                        double[] w = wordsOver(a, unitOfKey.get(a.key), v);
                        if (w == null) continue;
                        for (Node b : rank) {
                            double[] bw = wordsOver(b, unitOfKey.get(b.key), v);
                            if (bw == null) continue;
                            double bTop = b.y - b.gap + 2, bBottom = b.y + b.h, bRight = Math.max(b.x + b.w, bw[2]);
                            if (w[2] <= b.x || w[0] >= bRight || w[3] <= bTop || w[1] >= bBottom) continue;
                            double down = w[3] - bTop + 2, up = w[1] - bBottom - 2;
                            double first = rank.get(0).y - rank.get(0).gap, last = rank.get(rank.size() - 1).y + rank.get(rank.size() - 1).h;
                            boolean canDown = last + down <= bottom, canUp = first + up >= top;
                            move = canDown && (!canUp || down <= -up) ? down : canUp ? up : 0;
                            if (move != 0) break;
                        }
                        if (move != 0) break;
                    }
                }
                if (move == 0) break;
                for (Node b : rank) {
                    b.y += move;
                    b.inAt = b.y;
                    b.outAt = b.y;
                }
            }
        }
    }

    /** The scale at which a column fits `room`, its nodes under their least at the last scale `k` held at their least (the mockups' three passes). */
    private static double fit(List<Node> column, double room, double k) {
        if (column.isEmpty()) return Double.POSITIVE_INFINITY;
        double fixed = 0, free = 0;
        for (Node n : column) {
            fixed += n.gap;
            if (!(n.q > 0) || (Double.isFinite(k) && n.q * k < n.least)) fixed += n.least;
            else free += n.q;
        }
        if (!(free > 0)) return Double.POSITIVE_INFINITY;
        return Math.max(0, room - fixed) / free;
    }

    private static void size(List<Node> column, double k) {
        for (Node n : column) n.h = Math.max(n.q * k, n.least);
    }

    /** A column's nodes stacked from the top, centred on `mid`. */
    private static void stack(List<Node> column, double x, double w, double mid) {
        double total = 0;
        for (Node n : column) total += n.gap + n.h;
        double y = mid - total / 2;
        for (Node n : column) {
            y += n.gap;
            n.x = x;
            n.w = w;
            n.y = y;
            n.inAt = y;
            n.outAt = y;
            y += n.h;
        }
    }

    private static boolean has(List<Node> nodes, String key) {
        for (Node n : nodes) if (n.key.equals(key)) return true;
        return false;
    }

    private static double in(Map<String, double[]> sides, String key) {
        double[] s = sides.get(key);
        return s == null ? 0 : s[0];
    }

    private static double out(Map<String, double[]> sides, String key) {
        double[] s = sides.get(key);
        return s == null ? 0 : s[1];
    }

    private static double totalOut(Map<String, double[]> sides, List<Node> nodes) {
        double t = 0;
        for (Node n : nodes) t += out(sides, n.key);
        return t;
    }

    /** A taker's place: the city's parties in Sectors.KEYS' order, the households and the city, then the warehouse, the lost and the world. */
    static int takerRank(String key) {
        String who = key.substring("taker:".length());
        for (int i = 0; i < Sectors.KEYS.length; i++) if (Sectors.KEYS[i].equals(who)) return i;
        int n = Sectors.KEYS.length;
        return switch (who) {
            case Trade.HOUSEHOLDS -> n;
            case Trade.CITY -> n + 1;
            case WAREHOUSE -> n + 3;
            case LOST -> n + 4;
            case ABROAD -> n + 5;
            default -> n + 2;
        };
    }

    /* ----- the colours ----- */

    /** A ribbon's colour where it leaves: a field's its ground's, the raw coal the coking field's, the old steel the old metal's, a good its own. */
    static String colourFrom(View v, Flow f) {
        if (f.from().startsWith("field:")) {
            for (Vessel field : v.fields()) if (fieldKey(field.type()).equals(f.from())) return IndustryView.colour(field.resource(), field.type());
        }
        if (f.good() == null) {
            return f.from().startsWith("source:") ? IndustryView.OLD_METAL : IndustryView.colour(Resource.COAL, Deposit.FieldType.COKING);
        }
        return IndustryView.colour(f.good());
    }

    /** ...and where it arrives: the band's good (the limestone carried into aggregate turns its colour), else the colour it left with. */
    static String colourTo(View v, Flow f) {
        if (f.to().startsWith("made:")) return IndustryView.colour(Good.valueOf(f.to().substring("made:".length())));
        return colourFrom(v, f);
    }

    static String sourceColour(View v, String key) {
        if (key.equals(SALVAGE)) return IndustryView.colour(Resource.FOREST, Deposit.FieldType.NONE);
        String[] p = key.split(":");
        if (p[2].equals(Origin.OLD_METAL.name()) && p[1].equals(Good.STEEL.name()) && v.sector() instanceof HeavyIndustry) {
            return IndustryView.OLD_METAL;
        }
        return IndustryView.colour(Good.valueOf(p[1]));
    }

    /** Whether a band's words are written light: the dark goods. */
    static boolean dark(Good g) {
        String c = IndustryView.colour(g);
        int rgb = Integer.parseInt(c.substring(1), 16);
        double lum = .2126 * (rgb >> 16 & 255) + .7152 * (rgb >> 8 & 255) + .0722 * (rgb & 255);
        return lum < 120;
    }

    /* ----- the roofs (the spec's 2.13, the mockups' roof()) ----- */

    /** The roof over the tank, by the industry: a pit's headframe, the crews' logs, a furnace's stack and flame, a kiln's silo, a mill's saw-tooth. */
    static List<Outline> roof(IndustryView.Roof r, double x, double y, double w) {
        List<Outline> out = new ArrayList<>();
        String cap = RefineryView.CAP, frame = RefineryView.FRAME;
        switch (r) {
            case PIT -> {
                out.add(new Outline(path("M", x - 6, y, "L", x + 6, y - 12, "L", x + w - 6, y - 12, "L", x + w + 6, y, "Z"), cap, 1, frame));
                double hx = x + w * .35;
                out.add(new Outline(path("M", hx, y - 12, "L", hx + 6, y - 21, "L", hx + 20, y - 21, "L", hx + 26, y - 12, "Z"), cap, 1, frame));
            }
            case CAMP -> {
                for (int i = 0; i < 5; i++) {
                    double cx = x + 12 + i * (w - 24) / 4;
                    out.add(new Outline(circle(cx, y - 8, 8), cap, 1, frame));
                    out.add(new Outline(circle(cx, y - 8, 3), null, 1, frame));
                }
            }
            case FURNACE -> {
                out.add(new Outline(path("M", x, y, "L", x + 14, y - 10, "L", x + w - 14, y - 10, "L", x + w, y, "Z"), cap, 1, frame));
                out.add(new Outline(path("M", x + w / 2 - 10, y - 10, "L", x + w / 2 - 10, y - 30, "L", x + w / 2 + 10, y - 30, "L",
                        x + w / 2 + 10, y - 10, "Z"), cap, 1, frame));
                out.add(new Outline(path("M", x + w / 2 - 4, y - 30, "L", x + w / 2 - 4, y - 40, "L", x + w / 2 + 4, y - 40, "L",
                        x + w / 2 + 4, y - 30, "Z"), cap, 1, frame));
                out.add(new Outline(path("M", x + w / 2, y - 41, "Q", x + w / 2 - 4, y - 44, x + w / 2, y - 47, "Q", x + w / 2 + 4, y - 44,
                        x + w / 2, y - 41, "Z"), RefineryView.FLARE, .85, null));
            }
            case KILN -> {
                out.add(new Outline(path("M", x, y, "L", x + w / 2, y - 22, "L", x + w, y, "Z"), cap, 1, frame));
                out.add(new Outline(path("M", x + w / 2 - 3, y - 22, "L", x + w / 2 - 3, y - 30, "L", x + w / 2 + 3, y - 30, "L",
                        x + w / 2 + 3, y - 22, "Z"), cap, 1, frame));
            }
            case MILL -> {
                StringBuilder s = new StringBuilder(path("M", x, y));
                for (int i = 0; i < 4; i++) {
                    double a = x + i * w / 4, b = x + (i + 1) * w / 4;
                    s.append(path(" L", a, y - 8, "L", b, y - 18, "L", b, y - 8));
                }
                s.append(path(" L", x + w, y, "Z"));
                out.add(new Outline(s.toString(), cap, 1, frame));
            }
        }
        return out;
    }

    /** An outline's path: commands and coordinates, two decimals (RefineryView's). */
    static String path(Object... parts) {
        StringBuilder s = new StringBuilder();
        for (Object o : parts) {
            if (s.length() > 0 && !(o instanceof String str && str.startsWith(" "))) s.append(' ');
            s.append(o instanceof Double d ? String.format(Locale.ROOT, "%.2f", d) : o.toString().trim());
        }
        return s.toString();
    }

    /** A circle as four quadratic curves. */
    static String circle(double cx, double cy, double r) {
        return path("M", cx - r, cy, "Q", cx - r, cy - r, cx, cy - r, "Q", cx + r, cy - r, cx + r, cy, "Q", cx + r, cy + r, cx, cy + r,
                "Q", cx - r, cy + r, cx - r, cy, "Z");
    }

    /* =====================================================================
       THE WORDS
       ===================================================================== */

    /** The card's heading, by the industry (the mockups'): "THE MINES THIS MONTH", "THE STEELWORKS THIS MONTH". */
    public static String headWords(View v) {
        return switch (v.sector().key()) {
            case Sectors.HEAVY_INDUSTRY -> "THE STEELWORKS THIS MONTH";
            case Sectors.QUARRYING -> "THE QUARRIES THIS MONTH";
            case Sectors.CEMENT_CONCRETE -> "THE KILNS AND PLANTS THIS MONTH";
            case Sectors.COPPER_REFINING -> "THE SMELTER AND REFINERY THIS MONTH";
            case Sectors.FORESTRY -> "THE FOREST THIS MONTH";
            case Sectors.WOOD_PRODUCTS -> "THE MILLS THIS MONTH";
            default -> "THE MINES THIS MONTH";
        };
    }

    /** ...and its line: where the picture runs from and its unit. */
    public static String subWords(View v) {
        String in = "m³".equals(unitOf(v)) ? "cubic metres" : "tonnes";
        return (v.fields().isEmpty() ? "from what it bought to who bought it" : "from the ground to who took it")
                + " · ribbons to scale in " + in;
    }

    /** The scale, for the heading's right: "10 px of ribbon = 1,234 t". */
    public static String scaleWords(Picture p) {
        return p.scale() > 0 ? "10 px of ribbon = " + figureOf(p.unit(), 10 / p.scale()) : "";
    }

    /** What the card says when there is no month to draw: nothing standing, or a month not yet run since the city was loaded. */
    public static String emptyWords(View v) {
        if (v.standing() <= 0) {
            return v.onSite() > 0 ? v.sector().label() + " has nothing standing yet: " + v.onSite()
                    + (v.onSite() == 1 ? " building" : " buildings") + " on site." : v.sector().label() + " has nothing standing.";
        }
        if (!v.counted()) return "The month's figures are not saved: they are drawn once a month has run.";
        return "Nothing went through its works this month.";
    }

    /** The works' heading: the mines', the quarries', the crews', or the works'. */
    static String worksHead(View v) {
        return switch (v.sector().key()) {
            case Sectors.QUARRYING -> "THE QUARRIES";
            case Sectors.FORESTRY -> "THE CREWS";
            case Sectors.MINING, Sectors.COAL_MINING, Sectors.COPPER_MINING -> "THE MINES";
            default -> "THE WORKS";
        };
    }

    /** The tank's heading: what the mines dug, what the crews cut, what the works made. */
    static String tankHead(View v) {
        if (v.forest() != null) return "WHAT WAS CUT";
        return v.fields().isEmpty() ? "WHAT IT MADE" : "WHAT IT DUG";
    }

    /** The line of notes over the works: Coal Mining's wash, Quarrying's carry, the forest's cut, the old metal's month. */
    public static String noteWords(View v) {
        if (v.sector() instanceof CoalMining) {
            double[] w = washOf(v);
            if (w[0] > 0) {
                return String.format("the wash keeps %s of the %s of raw coal it takes; %s of spoil goes to the tip",
                        figureOf("t", w[1]), figureOf("t", w[0]), figureOf("t", w[2]));
            }
            return "";
        }
        if (v.carried() > 0) return "limestone no buyer took, carried into aggregate: " + IndustryView.unitsWords(Good.LIMESTONE, v.carried());
        if (v.forest() != null) return IndustryView.forestWords(v.forest()).get(1);
        if (v.oldMetal() != null && (v.oldMetal().arose() >= LEAST_PART || v.oldMetal().bought() >= LEAST_PART)) {
            return IndustryView.metalWords(v.oldMetal());
        }
        return "";
    }

    /** A quantity in a picture's unit, short: "1,234 t", "830k m³". */
    public static String figureOf(String unit, double q) {
        return Double.isFinite(q) ? RefineryView.compact(q) + " " + unit : "—";
    }

    /** A vessel's line under its name: its sites worked, or the forest the crews may cut. */
    static String vesselLine(View v, Vessel f) {
        if (f.resource() == Resource.FOREST) {
            return v.forest() == null ? "" : "may cut " + IndustryView.groundWords(f.resource(), v.forest().cuttable());
        }
        return String.format("%,d of %,d site%s worked", f.taking(), f.sites(), f.sites() == 1 ? "" : "s");
    }

    /** ...and its figure: what remains of it and the share. */
    static String vesselFigure(Vessel f) {
        return IndustryView.pct(f.filled()) + (f.resource() == Resource.FOREST ? " standing · " : " left · ")
                + IndustryView.groundWords(f.resource(), f.remaining());
    }

    /** A source's name: the good, or the city's old steel. */
    static String sourceName(View v, String key) {
        if (key.equals(SALVAGE)) return "Salvage cut";
        String[] p = key.split(":");
        if (p[2].equals(Origin.OLD_METAL.name())) return v.oldMetal() != null && v.sector() instanceof HeavyIndustry ? "Old steel" : "Old copper";
        return Good.valueOf(p[1]).label();
    }

    /** ...its line: who sold it - the first seller here and how many more ("Coal Mining +1") - imported, the city's old metal, the forest cleared for building. */
    static String sourceLine(Game game, View v, String key) {
        if (key.equals(SALVAGE)) return "the forest cleared for building";
        String[] p = key.split(":");
        Origin o = Origin.valueOf(p[2]);
        if (o == Origin.WORLD) return "imported";
        if (o == Origin.OLD_METAL) return "the city's " + (v.sector() instanceof HeavyIndustry ? "old steel" : "old copper");
        Feed f = v.feed(Good.valueOf(p[1]));
        List<String> names = new ArrayList<>();
        if (f != null) for (Part s : f.sellers()) if (s.units() >= LEAST_PART) names.add(IndustryView.partyWords(s.party(), game));
        if (names.isEmpty()) return "bought here";
        return names.size() == 1 ? names.get(0) : names.get(0) + " +" + (names.size() - 1);
    }

    /** Under the pointer, a source: its name, every seller here with what each sold, or where it came from, and its figure. */
    static String sourceTip(Game game, View v, String key) {
        String[] p = key.split(":");
        Feed f = key.equals(SALVAGE) ? null : v.feed(Good.valueOf(p[1]));
        if (f == null || !key.endsWith(":" + Origin.HOME) || f.sellers().size() < 2) {
            return sourceName(v, key) + ", " + sourceLine(game, v, key) + ": " + sourceFigure(v, key);
        }
        List<String> each = new ArrayList<>();
        for (Part s : f.sellers()) {
            if (s.units() >= LEAST_PART) each.add(IndustryView.partyWords(s.party(), game) + " " + IndustryView.unitsWords(f.good(), s.units()));
        }
        return sourceName(v, key) + " bought here: " + String.join(", ", each);
    }

    /** ...and its figure, in its good's own unit. */
    static String sourceFigure(View v, String key) {
        if (key.equals(SALVAGE)) return v.forest() == null ? "" : IndustryView.unitsWords(Good.SAWLOGS, v.forest().salvage());
        String[] p = key.split(":");
        Origin o = Origin.valueOf(p[2]);
        if (o == Origin.OLD_METAL && v.sector() instanceof HeavyIndustry && v.oldMetal() != null) {
            return String.format("%,.0f t", v.oldMetal().bought());
        }
        Good g = Good.valueOf(p[1]);
        Feed f = v.feed(g);
        if (f == null) return "";
        return IndustryView.unitsWords(g, o == Origin.HOME ? f.home() : o == Origin.WORLD ? f.imported() : f.oldMetal());
    }

    static Icon sourceIcon(String key) {
        if (key.equals(SALVAGE)) return Icon.FOREST;
        if (key.endsWith(":" + Origin.WORLD)) return Icon.SHIP;
        if (key.endsWith(":" + Origin.OLD_METAL)) return Icon.OLD_METAL;
        return Icon.SECTOR;
    }

    /** The first seller of a source bought here, for its icon; null for any other. */
    static String sellerKey(View v, String key) {
        if (!key.endsWith(":" + Origin.HOME)) return null;
        Feed f = v.feed(Good.valueOf(key.split(":")[1]));
        if (f != null) for (Part s : f.sellers()) if (s.units() >= LEAST_PART) return s.party();
        return null;
    }

    /** A kind's name over its box: "Coke Ovens ×2"; a kind only on site its name alone. */
    public static String unitName(Unit u) {
        String name = u.template().getName();
        return u.standing() > 0 ? name + " ×" + u.standing() : name;
    }

    /** ...its run: "runs 87%", "idle", "worked out", "grows 500 m³ a month", "2 on site" - and those on site beside it, "+1 on site". */
    public static String runLine(Unit u) {
        String s;
        if (u.standing() <= 0) s = String.format("%,d on site", u.onSite());
        else if (u.oneMore() == null && u.growth() > 0) s = String.format("grows %,.0f m³ a month", u.growth());
        else if (u.workedOut()) s = "worked out";
        else s = idle(u) ? "idle" : "runs " + IndustryView.pct(u.run());
        if (u.standing() > 0 && u.onSite() > 0) s += String.format(" · +%,d on site", u.onSite());
        return s;
    }

    /** ...and one more in a word: "one more pays 1.20% a month", or the first gate it fails - "one more: the money"; "" for a kind the plan does not weigh. */
    public static String gateLine(Unit u) {
        SpreadPlanner.Candidate c = u.oneMore();
        if (c == null) return "";
        return c.passes() ? String.format("one more pays %.2f%% a month", 100 * c.score()) : "one more: " + IndustryView.gateName(c.failed());
    }

    /** The lorries' line: the diesel they burned. */
    static String lorriesLine(View v) {
        return IndustryView.unitsWords(Good.DIESEL, v.lorries().diesel()) + " of diesel";
    }

    /** Whether a kind standing ran at nothing. */
    static boolean idle(Unit u) {
        return u.standing() > 0 && !(u.run() > 0) && !(u.growth() > 0);
    }

    /** Under the pointer, a kind: its line, what the last one earns, and one more in the plan's words. */
    public static String unitTip(Unit u) {
        StringBuilder s = new StringBuilder(IndustryView.unitWords(u));
        String last = IndustryView.lastWords(u);
        if (!last.isEmpty()) s.append(".\n").append(Character.toUpperCase(last.charAt(0))).append(last.substring(1));
        String gate = IndustryView.gateWords(u);
        if (!gate.isEmpty()) s.append(".\n").append(Character.toUpperCase(gate.charAt(0))).append(gate.substring(1));
        return s.append('.').toString();
    }

    /** A band's name in the tank: the good's, short where the label would not fit. */
    public static String bandName(Good g) {
        return switch (g) {
            case PELLETS -> "Pellets";
            case COPPER_CONC -> "Concentrate";
            case COPPER -> "Cathode";
            case COPPER_ROD -> "Rod";
            case ACID -> "Acid";
            case WOOD_PELLETS -> "Wood pellets";
            default -> g.label();
        };
    }

    /** Under the pointer, a product's band: made, kept, sold here, shipped, into or out of its warehouse. */
    static String productTip(View v, Product p) {
        Good g = p.good();
        StringBuilder s = new StringBuilder(g.label()).append(": made ").append(IndustryView.unitsWords(g, p.made()));
        if (p.captive() > 0) s.append(", ").append(IndustryView.unitsWords(g, p.captive())).append(" of it kept for its own works");
        if (p.carriedOut() > 0) s.append(", ").append(IndustryView.unitsWords(g, p.carriedOut())).append(" carried into aggregate");
        if (p.carriedIn() > 0) s.append(", and ").append(IndustryView.unitsWords(g, p.carriedIn())).append(" of limestone carried in");
        s.append(". Sold here ").append(IndustryView.unitsWords(g, p.home())).append(", shipped ").append(IndustryView.unitsWords(g, p.exported()));
        if (p.intoStock() > 0) s.append(", into its warehouse ").append(IndustryView.unitsWords(g, p.intoStock()));
        else if (p.intoStock() < 0) s.append(", out of its warehouse ").append(IndustryView.unitsWords(g, -p.intoStock()));
        if (p.lost() > 0) s.append(", lost ").append(IndustryView.unitsWords(g, p.lost()));
        if (p.idled() > 0) s.append("; ").append(IndustryView.unitsWords(g, p.idled())).append(" not made: the warehouse held enough");
        return s.append('.').toString();
    }

    /** A taker's name: a sector's label, "Households", "The city", "Abroad", "Into the warehouse", "Lost". */
    static String takerName(Game game, View v, String who) {
        return switch (who) {
            case ABROAD -> "Abroad";
            case WAREHOUSE -> "Into the warehouse";
            case LOST -> "Lost";
            case Trade.HOUSEHOLDS -> "Households";
            case Trade.CITY -> "The city";
            default -> IndustryView.partyWords(who, game);
        };
    }

    static Icon takerIcon(String who) {
        return switch (who) {
            case ABROAD -> Icon.GLOBE;
            case WAREHOUSE -> Icon.WAREHOUSE;
            case LOST -> Icon.LOST;
            case Trade.HOUSEHOLDS -> Icon.HOUSEHOLDS;
            case Trade.CITY -> Icon.CITY;
            default -> Icon.SECTOR;
        };
    }

    /** What a taker took, by good, in the goods' own units, largest drawn first. */
    static Map<Good, Double> took(List<Flow> flows, String key) {
        Map<Good, double[]> by = new LinkedHashMap<>();
        for (Flow f : flows) if (f.to().equals(key) && f.good() != null) {
            double[] a = by.computeIfAbsent(f.good(), x -> new double[2]);
            a[0] += f.units();
            a[1] += f.drawn();
        }
        List<Map.Entry<Good, double[]>> order = new ArrayList<>(by.entrySet());
        order.sort((a, b) -> Double.compare(b.getValue()[1], a.getValue()[1]));
        Map<Good, Double> out = new LinkedHashMap<>();
        for (Map.Entry<Good, double[]> e : order) out.put(e.getKey(), e.getValue()[0]);
        return out;
    }

    /** A taker's figure: its one good in its own unit, or all it took in the picture's. */
    static String takerFigure(View v, List<Flow> flows, String key) {
        Map<Good, Double> took = took(flows, key);
        if (took.size() == 1) {
            Map.Entry<Good, Double> e = took.entrySet().iterator().next();
            return IndustryView.figure(e.getKey(), e.getValue());
        }
        double q = 0;
        for (Flow f : flows) if (f.to().equals(key)) q += f.drawn();
        return figureOf(unitOf(v), q);
    }

    /** ...its line: what it took, largest first, at most three - "pig iron, scrap"; the warehouse's and the lost's what they are. */
    static String takerLine(View v, List<Flow> flows, String key) {
        List<String> names = new ArrayList<>();
        for (Good g : took(flows, key).keySet()) names.add(bandName(g).toLowerCase(Locale.ROOT));
        String goods = names.size() <= 3 ? String.join(", ", names) : String.join(", ", names.subList(0, 3)) + " and more";
        String who = key.substring("taker:".length());
        if (who.equals(LOST)) return goods + " · it could not ship";
        return goods;
    }

    /** Under the pointer, a taker: each good it took. */
    static String takerTip(Game game, View v, List<Flow> flows, String key) {
        StringBuilder s = new StringBuilder(takerName(game, v, key.substring("taker:".length()))).append(" took ");
        boolean first = true;
        for (Map.Entry<Good, Double> e : took(flows, key).entrySet()) {
            if (!first) s.append(", ");
            s.append(IndustryView.unitsWords(e.getKey(), e.getValue())).append(" of ").append(e.getKey().label().toLowerCase(Locale.ROOT));
            first = false;
        }
        return s.append('.').toString();
    }

    /* =====================================================================
       THE STRIP: THE FIELDS AND THE PRODUCTS, A CELL EACH (O11's strip)
       ===================================================================== */

    /** The most cells the strip holds: six at about 200 px each at 1,389 x 868. */
    public static final int STRIP_MOST = 6;

    /** The strip's heading. */
    public static final String STRIP_HEAD = "THIS MONTH, CELL BY CELL";

    /** A part of a cell's bar: its share, its colour, and whether it is pale or hatched. */
    public record Segment(double share, String colour, double opacity, boolean hatched) { }

    /** A line of a cell: its words, its figure, and whether the figure is a warning's colour. */
    public record Row(String label, String figure, boolean hot) { }

    /** One cell under the picture: a field, the forest, a product, the old metal, the power. */
    public record Cell(String name, String colour, List<Segment> bar, List<Row> rows) { }

    /** The strip's line: what its bars show. */
    public static final String STRIP_WORDS = "a field's bar what is left of it · a product's: used here, shipped (pale), into the warehouse (hatched)";

    /** The strip's cells (pure): the fields or the forest first, then the products by tonnes, then the old metal and the power - at most STRIP_MOST. */
    public static List<Cell> strip(View v) {
        List<Cell> out = new ArrayList<>();
        if (v == null || !v.drawn()) return out;
        List<Flow> flows = trace(v);
        for (Vessel f : v.fields()) {
            String colour = IndustryView.colour(f.resource(), f.type());
            List<Row> rows = new ArrayList<>();
            double dug = 0;
            for (Flow fl : flows) if (fl.from().equals(fieldKey(f.type()))) dug += fl.units();
            if (f.resource() == Resource.FOREST && v.forest() != null) {
                IndustryView.Forest fo = v.forest();
                rows.add(new Row("standing", IndustryView.groundWords(f.resource(), fo.standing()), false));
                rows.add(new Row("may cut", IndustryView.groundWords(f.resource(), fo.cuttable()), false));
                rows.add(new Row("cut this month", IndustryView.groundWords(f.resource(), fo.cut()), false));
                rows.add(new Row("grows back", IndustryView.groundWords(f.resource(), fo.regrowth()), false));
            } else {
                rows.add(new Row("sites worked", String.format("%,d of %,d", f.taking(), f.sites()), false));
                rows.add(new Row("left", IndustryView.groundWords(f.resource(), f.remaining()), f.filled() < .4));
                rows.add(new Row("dug this month", IndustryView.groundWords(f.resource(), dug), false));
                rows.add(new Row("lasts", lastsWords(f.remaining(), dug), dug > 0 && f.remaining() / dug < 120));
            }
            out.add(new Cell(fieldName(f), colour,
                    List.of(new Segment(f.filled(), colour, 1, false), new Segment(1 - f.filled(), RefineryView.FRAME, 1, false)), rows));
        }
        if (v.forest() != null) {
            IndustryView.Forest fo = v.forest();
            out.add(new Cell("Where it settles", IndustryView.colour(Resource.FOREST, Deposit.FieldType.NONE),
                    List.of(new Segment(clamp(fo.settlesWithLaw(), 1), IndustryView.colour(Resource.FOREST, Deposit.FieldType.NONE), 1, false),
                            new Segment(1 - clamp(fo.settlesWithLaw(), 1), RefineryView.FRAME, 1, false)),
                    List.of(new Row("under the law", IndustryView.pct(fo.settlesWithLaw()), false),
                            new Row("protected", IndustryView.pct(fo.protectedShare()), false),
                            new Row("with no law", IndustryView.pct(fo.settlesWithout()), fo.settlesWithout() < .4),
                            new Row("planting past", IndustryView.groundWords(Resource.FOREST, fo.plantingLine()), fo.pastPlantingLine()))));
        }
        List<Product> products = new ArrayList<>(v.products());
        products.removeIf(p -> !(p.made() > 0) && !(p.home() > 0) && !(p.exported() > 0) && !(p.stock() > 0));
        products.sort((a, b) -> Double.compare(drawn(v, b.good(), b.made()), drawn(v, a.good(), a.made())));
        for (Product p : products) {
            Good g = p.good();
            double used = p.captive() + p.home(), shipped = p.exported(), stock = Math.max(0, p.intoStock());
            double all = used + shipped + stock;
            List<Segment> bar = all > 0 ? List.of(new Segment(used / all, IndustryView.colour(g), 1, false),
                    new Segment(shipped / all, IndustryView.colour(g), .35, false), new Segment(stock / all, IndustryView.colour(g), 1, true))
                    : List.of();
            String warehouse = (p.intoStock() >= 0 ? "+" : "−") + IndustryView.unitsWords(g, Math.abs(p.intoStock()));
            out.add(new Cell(g.label(), IndustryView.colour(g), bar, List.of(
                    new Row("made here", IndustryView.unitsWords(g, p.made()), false),
                    new Row(p.captive() > 0 ? "kept, and sold here" : "sold here", IndustryView.unitsWords(g, used), false),
                    new Row("shipped", IndustryView.unitsWords(g, shipped), false),
                    new Row("warehouse", warehouse, p.lost() > 0))));
        }
        if (v.oldMetal() != null && v.oldMetal().arose() >= LEAST_PART) {
            IndustryView.Metal m = v.oldMetal();
            double share = m.arose() > 0 ? clamp(m.bought() / m.arose(), 1) : 0;
            String name = IndustryView.metalName(m);
            out.add(new Cell(Character.toUpperCase(name.charAt(0)) + name.substring(1), IndustryView.OLD_METAL,
                    List.of(new Segment(share, IndustryView.OLD_METAL, 1, false), new Segment(1 - share, IndustryView.OLD_METAL, .35, false)),
                    List.of(new Row("arose", String.format("%,.0f t", m.arose()), false),
                            new Row("bought", String.format("%,.0f t", m.bought()), false),
                            new Row("to landfill", String.format("%,.0f t", m.landfill()), m.landfill() > m.bought()),
                            new Row("price", RefineryView.price(m.price()), false))));
        }
        if (v.power().makes()) {
            IndustryView.Power p = v.power();
            double share = p.made() > 0 ? clamp(p.sold() / p.made(), 1) : 0;
            out.add(new Cell("Power", IndustryView.POWER, List.of(new Segment(1 - share, IndustryView.POWER, 1, false),
                    new Segment(share, IndustryView.POWER, .35, false)), List.of(
                    new Row("made", IndustryView.kw(p.made()), false),
                    new Row("drawn", IndustryView.kw(p.drawn()), false),
                    new Row("sold", IndustryView.kw(p.sold()), false),
                    new Row("bought", IndustryView.kw(p.bought()), false))));
        }
        return out.size() <= STRIP_MOST ? Collections.unmodifiableList(out) : Collections.unmodifiableList(out.subList(0, STRIP_MOST));
    }

    /** A field type's cell's name: "Hematite fields", "Coking fields"; the forest "Forest" - apart from a product's cell of the same good ("Limestone"). */
    public static String fieldName(Vessel f) {
        String w = IndustryView.typeWords(f.resource(), f.type());
        return f.resource() == Resource.FOREST ? w : w + " fields";
    }

    /** How long a field lasts at this month's dig: "38 years", "11 months", "—" with nothing dug. */
    public static String lastsWords(double remaining, double dugAMonth) {
        if (!(dugAMonth > 0)) return "—";
        double months = remaining / dugAMonth;
        if (months >= 1_200) return "over a century";
        return months >= 24 ? String.format("%,.0f years", months / 12) : String.format("%,.0f months", months);
    }
}
