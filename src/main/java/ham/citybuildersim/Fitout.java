package ham.citybuildersim;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * One new building's equipment as its order fixes it (R1e): the pieces of its template it comes WITHOUT, chosen when the order
 * is quoted and frozen on site when it is accepted, and what that makes one building's cash cost and construction points - the
 * template's shell and each piece's part of it, defined here once for the quote, the investors' estimate, the site and the bill.
 *
 * WHY (R1e, 2026-11-19; docs/queue/R1e.md, R1b-R's call 3 left open by R1d). A new building paid its template's whole cost at
 * the order, and what it came with was decided at its finish (Game.fitWhatFinished() asked EquipmentFit.fittedForNew() then): a
 * building that came without a piece had still paid for it, and no discount could be given safely while the finish might deliver
 * another set than the one priced. Now the set is chosen at the quote (of(): the pieces fittedForNew() keeps, the same rule), the
 * order is priced on it, the site owes its points and keeps it (BuildingsStacks' lots, saved), and the finish delivers it: a
 * discovery or a price change after the order is a new fitting decision on a standing building (EquipmentFit.choose()).
 *
 * THE SPLIT, SOURCED (R1c call 10; R2e's capital estimate, "25% of plant cash at MACHINERY 13.4, 10% construction points"). The
 * pieces were sized as parts of their templates: one built into the plant buys a quarter of the plant's cash in MACHINERY at its
 * import price and its work is a tenth of the plant's points; one carried in (the truck, the tractor, the shovel, the drill, the
 * cutter) buys its machines and takes no work. So the split is read back off the catalogue and not off today's market: a piece
 * with work is in its template's price, its part of the cash its goods at the table's import price (Good.worldImportPrice(), the
 * price its quantity was sized at) over the template's catalogue cash, its part of the points its work (inThePrice(),
 * cashShare()), and with that work the cash a point of the template holds beside its pieces' goods (shareAPoint(): the frame,
 * its labour and the rest, over its points), so a building that does fewer points pays the same for each and none for the work
 * left off (REV-R1e: the shell had kept the left-off work's cash, which a refit then charged again). A piece with no work is
 * bought apart and no part of the price, so a building without it costs what it did. The shell is the template less every
 * built-in piece, never less than nothing. Not Game.quoteFitting(), a refit's goods drawn from the market today and its work at
 * the city's price a point, which nothing bounds by what the template charges for the piece.
 *
 * A city founded the default way fits every piece to every building, so there an order's fitout is whole() and chosen by no one
 * (isChosen() false): the template's own cash and points, the same numbers in the same arithmetic as before, no lot on site and
 * nothing saved.
 */
public final class Fitout {

    private final BuildingsTemplate template;
    /** The pieces left off, by id, sorted; empty for the whole template. */
    private final Set<String> without;
    /** Whether an order chose it (a city founded the 1900 way, a template with equipment): only a chosen fitout is kept on site. */
    private final boolean chosen;

    private Fitout(BuildingsTemplate template, Set<String> without, boolean chosen) {
        this.template = template;
        this.without = without;
        this.chosen = chosen;
    }

    /** The whole template, chosen by no one: every piece, its own cash and points. Null for no template. */
    public static Fitout whole(BuildingsTemplate t) {
        return t == null ? null : new Fitout(t, Collections.emptySet(), false);
    }

    /**
     * A chosen fitout without these pieces (an order's, or one read back from a save or a shell): ids the template does not carry
     * are dropped, so a piece taken out of the catalogue leaves the building as its template now stands. Null for no template.
     */
    public static Fitout without(BuildingsTemplate t, Collection<String> ids) {
        if (t == null) return null;
        TreeSet<String> off = new TreeSet<>();
        if (ids != null) for (String id : ids) if (id != null && t.getEquipment(id) != null) off.add(id);
        return new Fitout(t, Collections.unmodifiableSet(off), true);
    }

    /**
     * What an order of `t` placed now comes with: in a city founded the 1900 way, for a template that carries equipment, the pieces
     * EquipmentFit.fittedForNew() keeps (the rule the finish used to ask, asked at the quote), the rest left off, chosen; whole()
     * in a city founded the default way, for a template with no equipment, for no game, and inside another pricing (a proposal's
     * or a licence's, which price the states they are given, as NewUnit.of() reads there). Evaluated afresh from this call's
     * inputs; reads, prices and books nothing.
     */
    public static Fitout of(Game game, BuildingsTemplate t) {
        if (t == null) return null;
        if (game == null || !game.foundedUnfitted() || t.getEquipment().isEmpty() || NewUnit.resolving() || LicenceFees.pricing())
            return whole(t);
        Set<String> fitted = EquipmentFit.fittedForNew(game, t);
        TreeSet<String> off = new TreeSet<>();
        for (Equipment piece : t.getEquipment()) if (!fitted.contains(piece.getId())) off.add(piece.getId());
        return new Fitout(t, Collections.unmodifiableSet(off), true);
    }

    /** The template it is of: what is ordered and stacked. */
    public BuildingsTemplate template() { return template; }

    /** Whether an order chose it, and so whether a site keeps it (BuildingsStacks' lots). */
    public boolean isChosen() { return chosen; }

    /** Whether it leaves nothing off: the template's own cash and points. */
    public boolean isWhole() { return without.isEmpty(); }

    /** The pieces it comes without, by id, sorted; unmodifiable. */
    public Set<String> without() { return without; }

    /** Whether a building of it comes with this piece. */
    public boolean comesWith(String pieceId) { return pieceId != null && template.getEquipment(pieceId) != null && !without.contains(pieceId); }

    /** Whether a piece is part of its template's price: one with fitting work, built into the plant (R1c call 10). One with none is carried in and bought apart. */
    public static boolean inThePrice(Equipment piece) {
        return piece != null && piece.getWork() > 0;
    }

    /**
     * A piece's part of its template's cash cost, sourced (see THE SPLIT above): its goods at the table's import price over the
     * template's catalogue cash, for a piece in the price; 0 for one carried in, for a good the world does not sell, and for a
     * template with no cash. Never more than 1. A ratio of two figures struck at the founding's price level, so it does not move
     * with prices, wages, a reform or the expected level.
     */
    public static double cashShare(BuildingsTemplate t, Equipment piece) {
        if (t == null || !inThePrice(piece)) return 0;
        double cash = t.getFoundingCashCost();
        if (!(cash > 0)) return 0;
        double value = 0;
        for (java.util.Map.Entry<Good, Double> e : piece.getBuys().entrySet()) {
            double price = e.getKey().worldImportPrice();
            if (Double.isFinite(price) && price > 0) value += e.getValue() * price;
        }
        return Math.max(0, Math.min(1, value / cash));
    }

    /**
     * The share of its template's cash one point of its work holds beside the built-in pieces' goods: the cash less every
     * piece's cashShare(), the frame with all its labour and the rest, over the template's points. What a point left off takes
     * out of the price with it, so the shell's cash a point, its pieces' goods apart, is the whole template's. 0 for a template
     * with no points, and when the pieces' goods are the whole cash. A ratio of catalogue figures, as cashShare() is.
     */
    public static double shareAPoint(BuildingsTemplate t) {
        if (t == null || t.getConstructionPoints() <= 0) return 0;
        double goods = 0;
        for (Equipment piece : t.getEquipment()) goods += cashShare(t, piece);
        return Math.max(0, 1 - goods) / t.getConstructionPoints();
    }

    /** The share of the template's cash this fitout leaves off: its pieces' goods and their work's cash (shareAPoint()), never more than the whole. 0 for the whole template. */
    public double cashShareLeftOff() {
        if (without.isEmpty()) return 0;
        double off = pointsLeftOff() * shareAPoint(template);
        for (String id : without) off += cashShare(template, template.getEquipment(id));
        return Math.min(1, off);
    }

    /** One building's cash cost (thousands, struck as the template's is): the template's, less the pieces left off, their goods and their work; the template's own for the whole. */
    public double cashCost() {
        if (without.isEmpty()) return template.getCashCost();
        return template.getCashCost() * (1 - cashShareLeftOff());
    }

    /** The construction points the pieces left off would have taken a building: their work. 0 for the whole template. */
    public double pointsLeftOff() {
        if (without.isEmpty()) return 0;
        double off = 0;
        for (String id : without) {
            Equipment piece = template.getEquipment(id);
            if (inThePrice(piece)) off += piece.getWork();
        }
        return Math.min(off, template.getConstructionPoints());
    }

    /** One building's construction points: the template's, less the work of the pieces left off; the template's own for the whole. */
    public double points() {
        if (without.isEmpty()) return template.getConstructionPoints();
        return Math.max(0, template.getConstructionPoints() - pointsLeftOff());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Fitout f)) return false;
        return f.template == template && f.chosen == chosen && f.without.equals(without);
    }

    @Override
    public int hashCode() { return System.identityHashCode(template) * 31 + without.hashCode() + (chosen ? 1 : 0); }

    @Override
    public String toString() {
        return template.getName() + (without.isEmpty() ? " (whole)" : " without " + String.join(", ", without));
    }
}
