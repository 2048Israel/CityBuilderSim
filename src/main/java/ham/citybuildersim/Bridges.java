package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * The city's long bridges, built and building: for each, the template it was ordered as, its kind, the two landings and the
 * landmass at each end, its length, the depth of the water under it, the chain of plots it runs along as its corner plots,
 * and whether it stands, is on site or is a stopped shell - and, as Landmasses.Joins, the landmasses a standing one joins.
 *
 * WHY THIS EXISTS. spec-earth2-terrain 6.5 and 6.6 (ET6). ET5 left a slot in the linked set for the second way to link two
 * landmasses, "a long bridge from a linked landmass links the one it reaches", and the map needs to lay a built bridge
 * along its path (CityRuns, DistrictPlan, TileRaster.DECK) and read it from the model, since the model never reads the
 * map. A building is a template and a count with no place in it, so where a bridge runs, and between which landmasses, is
 * kept here - as ET5 keeps where the sea terminals stand: fitted to the buildings' own counts at every month's settle
 * (reconcile()), a completion taking the oldest bridge on site to standing, a stop the newest to the shell, a restart the
 * shell back to the oldest end of on site. A bridge joins its two landmasses once it STANDS: one on site or a shell never
 * does. An order of n is n parallel spans on the same survey, n records the builders finish one after another.
 *
 * SAVED, whole: DataSave.bridges holds a row for each (toState()), absent while the city has none. Nothing is derived, since
 * the survey that found a bridge's path read the ground as it was when it was ordered and a later purchase could not find
 * the same path again.
 */
public final class Bridges implements Landmasses.Joins {

    /** A bridge that stands. */
    public static final int STANDING = 0;

    /** ...one the builders are at work on. */
    public static final int ON_SITE = 1;

    /** ...one a stopped order left standing as a shell. */
    public static final int SHELL = 2;

    /** What Bridges reads of the buildings: Game wires it; a bare one has none and no bridge is fitted to a count. */
    public interface Stock {
        /** Whether the template is one of the bridges'. */
        boolean known(int template);

        /** The finished bridges of a template. */
        int standing(int template);

        /** ...those under construction. */
        int onSite(int template);

        /** ...those a stopped order left standing as a shell. */
        int shell(int template);
    }

    /**
     * One bridge, as saved and as read: its template, kind, state, landings (the near on the landmass the city has linked, the far
     * across the water) with the landmass at each, length and depths (metres) and the chain's corner plots (x0, y0, x1, y1, ...,
     * from the far landing to the near).
     */
    public static final class Row {
        public int template;
        public String kind;
        public int state;
        public long nearX, nearY, farX, farY;
        public int nearLandmass, farLandmass;
        public double lengthM, deepestM, meanM;
        public long[] path;

        public Row() { }

        /** Its kind. */
        public BridgeWorks.Kind kind() {
            try {
                return BridgeWorks.Kind.valueOf(kind);
            } catch (RuntimeException e) {
                return null;
            }
        }

        public boolean standing() { return state == STANDING; }

        Row copy() {
            Row r = new Row();
            r.template = template;
            r.kind = kind;
            r.state = state;
            r.nearX = nearX;
            r.nearY = nearY;
            r.farX = farX;
            r.farY = farY;
            r.nearLandmass = nearLandmass;
            r.farLandmass = farLandmass;
            r.lengthM = lengthM;
            r.deepestM = deepestM;
            r.meanM = meanM;
            r.path = path == null ? null : path.clone();
            return r;
        }
    }

    /** DataSave's bridges (ET6): every bridge the city has built or is building. Absent on an older save and on every city with none. */
    public static final class State {
        public List<Row> bridges = new ArrayList<>();

        public State() { }
    }

    /** Every bridge, oldest first; a template's records stay in the order they were ordered. */
    private final List<Row> rows = new ArrayList<>();

    /** The landmass the card chose to bridge toward, by template (ONE order's: spent by it and forgotten at the month's settle); transient, never saved. */
    private final TreeMap<Integer, Integer> chosen = new TreeMap<>();

    private Stock stock;

    /** A bare one: no bridge. */
    public Bridges() { }

    /** Back to a bare one (LandManager.reset(): a new city). */
    void reset() {
        rows.clear();
        chosen.clear();
    }

    /** Wires the buildings' counts (Game, once). */
    void setStock(Stock stock) { this.stock = stock; }

    /* =====================================================================
       THE ORDERS - a bridge is ordered, stopped, restarted and built
       ===================================================================== */

    /**
     * An order of n bridges of a template was placed on a surveyed crossing, after the stack took it: n records on the same
     * survey, on site - standing at once for an order built at once (`instant`).
     */
    void ordered(int template, BridgeWorks.Kind kind, BridgeSurvey.Result survey, int n, boolean instant) {
        if (survey == null || !survey.found()) return;
        for (int k = 0; k < n; k++) {
            Row r = new Row();
            r.template = template;
            r.kind = kind.name();
            r.state = instant ? STANDING : ON_SITE;
            r.nearX = survey.nearX();
            r.nearY = survey.nearY();
            r.farX = survey.farX();
            r.farY = survey.farY();
            r.nearLandmass = survey.nearLandmass();
            r.farLandmass = survey.far();
            r.lengthM = survey.lengthM();
            r.deepestM = survey.deepestM();
            r.meanM = survey.meanM();
            r.path = survey.corners().clone();
            rows.add(r);
        }
    }

    /**
     * Fits every template's records to the buildings' own counts (at the month's settle and at a load): a completion has taken
     * the oldest bridge on site to standing, a stop the newest to the shell, a restart the shell back to the oldest end of on
     * site; then each state is fitted to its count - a record over it is dropped, the newest first (a shell demolished, an
     * order withdrawn) - and a record of a template that is no bridge is forgotten. A standing bridge no record stands for
     * (one built at once with no survey) joins nothing.
     */
    void reconcile() {
        if (stock == null) return;
        for (java.util.Iterator<Row> it = rows.iterator(); it.hasNext(); ) {
            if (!stock.known(it.next().template)) it.remove();
        }
        java.util.TreeSet<Integer> templates = new java.util.TreeSet<>();
        for (Row r : rows) templates.add(r.template);
        for (int t : templates) fit(t);
    }

    private List<Row> of(int template, int state) {
        List<Row> out = new ArrayList<>();
        for (Row r : rows) if (r.template == template && r.state == state) out.add(r);
        return out;
    }

    private void fit(int template) {
        int standing = Math.max(0, stock.standing(template)), onSite = Math.max(0, stock.onSite(template)), shell = Math.max(0, stock.shell(template));
        List<Row> s = of(template, STANDING), o = of(template, ON_SITE), h = of(template, SHELL);
        // The completions: the oldest bridges on site have finished.
        int done = Math.min(Math.max(0, standing - s.size()), o.size());
        for (int k = 0; k < done; k++) {
            Row r = o.remove(0);
            r.state = STANDING;
            s.add(r);
        }
        // The stops: the newest bridges on site past the count have been stopped, to the shell.
        int stopped = Math.min(Math.max(0, o.size() - onSite), Math.max(0, shell - h.size()));
        for (int k = 0; k < stopped; k++) {
            Row r = o.remove(o.size() - 1);
            r.state = SHELL;
            h.add(r);
        }
        // The restarts: a shell the buildings no longer hold, with bridges missing on site, is back on site.
        int restarted = Math.min(Math.max(0, h.size() - shell), Math.max(0, onSite - o.size()));
        for (int k = 0; k < restarted; k++) {
            Row r = h.remove(0);
            r.state = ON_SITE;
            o.add(r);
        }
        o.sort((a, b) -> Integer.compare(rows.indexOf(a), rows.indexOf(b)));
        // Then each state is fitted to its count: the newest over it dropped.
        drop(s, standing);
        drop(o, onSite);
        drop(h, shell);
    }

    private void drop(List<Row> list, int count) {
        while (list.size() > count) rows.remove(list.remove(list.size() - 1));
    }

    /** The month's settle (Game.settleConstructionControl()): the choices are forgotten (one order's), the records fitted. */
    void settle() {
        chosen.clear();
        reconcile();
    }

    /* =====================================================================
       THE LINKS - what a standing bridge joins
       ===================================================================== */

    /** The landmasses the standing bridges join to this one, both ways: the far end of one whose near end is on it, the near end of one whose far end is. */
    @Override
    public int[] joinedTo(int id) {
        java.util.TreeSet<Integer> out = new java.util.TreeSet<>();
        for (Row r : rows) {
            if (r.state != STANDING) continue;
            if (r.nearLandmass == id) out.add(r.farLandmass);
            if (r.farLandmass == id) out.add(r.nearLandmass);
        }
        out.remove(id);
        int[] a = new int[out.size()];
        int i = 0;
        for (int v : out) a[i++] = v;
        return a;
    }

    /* =====================================================================
       THE CHOICE - the landmass the card bridges toward
       ===================================================================== */

    /** The card's pick of the far landmass for a template's next order (ONE order's). */
    void choose(int template, int landmass) { chosen.put(template, landmass); }

    /** The landmass the card picked for a template, or NONE_CHOSEN. */
    public int chosen(int template) { return chosen.getOrDefault(template, NONE_CHOSEN); }

    /** What chosen() says when the card has picked nothing: the rule decides. */
    public static final int NONE_CHOSEN = Integer.MIN_VALUE;

    /** An order was placed: its pick is spent. */
    void clearChoice(int template) { chosen.remove(template); }

    /* =====================================================================
       READS - plain getters; none fills anything
       ===================================================================== */

    /** Every bridge, oldest first, as read-only copies. */
    public List<Row> all() {
        List<Row> out = new ArrayList<>();
        for (Row r : rows) out.add(r.copy());
        return Collections.unmodifiableList(out);
    }

    /** How many bridges the city has built or is building, in any state. */
    public int size() { return rows.size(); }

    /** How many are in a state (STANDING, ON_SITE or SHELL). */
    public int count(int state) {
        int n = 0;
        for (Row r : rows) if (r.state == state) n++;
        return n;
    }

    /** How many of a template are in a state. */
    public int count(int template, int state) {
        int n = 0;
        for (Row r : rows) if (r.template == template && r.state == state) n++;
        return n;
    }

    /** The standing bridges' length, metres, all together. */
    public double standingLengthM() {
        double m = 0;
        for (Row r : rows) if (r.state == STANDING) m += r.lengthM;
        return m;
    }

    /** Whether a landmass has a standing bridge at either end. */
    public boolean joins(int landmass) { return joinedTo(landmass).length > 0; }

    /* =====================================================================
       THE SAVE
       ===================================================================== */

    /** DataSave's bridges: fresh on every call (the autosave's snapshot shares nothing), null when the city has none. */
    public State toState() {
        if (rows.isEmpty()) return null;
        State s = new State();
        for (Row r : rows) s.bridges.add(r.copy());
        return s;
    }

    /** Puts the bridges back as saved; null, none. A row of a template that is no bridge is passed over. The caller reconciles and refreshes. */
    void restore(State s) {
        rows.clear();
        chosen.clear();
        if (s == null || s.bridges == null) return;
        for (Row r : s.bridges) {
            if (r == null || r.kind() == null || (stock != null && !stock.known(r.template))) continue;
            if (r.state != STANDING && r.state != ON_SITE && r.state != SHELL) continue;
            rows.add(r.copy());
        }
    }
}
