package ham.citybuildersim;

/**
 * One field of a resource in the world's ground: which resource, the world cell it was drawn in and its place in that cell's list, its centre as a plot, its sites and what it holds - where each of its sites lies, and (0.7.79) an oil field's grade.
 *
 * WHY THIS EXISTS (0.7.56, batch J1a; the project's spec-land.md 2.1). A
 * field is the unit the land office sells and the map draws: it belongs
 * whole to the piece of ground that holds its centre (spec-land star 12), it
 * is mined a site at a time, and it is worked out in the order the city
 * bought it. World.fieldsInCell() draws a cell's fields from the cell's own
 * stream whenever they are asked for, so a field is never stored - the cell
 * and the index say which one it is, from any save, on any machine.
 *
 * ITS SITES LIE ON THE GROUND (0.7.58, batch J1c): each a square of its
 * resource's site area (siteWidth()) laid on a grid round the centre,
 * nearest first (siteAt()), each holding an equal share of the field's
 * amount to the whole unit (siteAmount()) - where the map draws them and the
 * mines and wells stand. From 0.7.58 to 0.7.63 a site also belonged to the
 * piece of ground holding the site's own centre, so a field was shared, a
 * site at a time, among the ground it covers, and a new city bought one site
 * of its founding field for about US$5.3M. Since 0.7.64 (batch L; Jerus,
 * "whole iron fields as one offer") the whole field goes with its centre
 * again, every site and every tonne in one offer: the default world's
 * founding field, 35 sites and 449 Mt, for about US$180M (CityLand, THE
 * FIELDS IN A PIECE OF GROUND).
 *
 * @param kind   the resource (never Resource.FOREST, which is terrain)
 * @param cell   the world cell it was drawn in: row x World.CELLS + column
 * @param index  its place in that cell's list, from 0
 * @param x      its centre's plot, east from the world's west edge
 * @param y      its centre's plot, south from the world's north edge
 * @param sites  how many mines or wells it takes, 1 to World.MAX_SITES
 * @param amount what it holds, in its resource's unit, whole: the fields of a
 *               cell sum exactly to World.cellTotal()
 */
public record Deposit(Resource kind, int cell, int index, long x, long y, int sites, double amount) {

    /** The ground it covers, in square kilometres: its sites times its resource's site. */
    public double km2() { return sites * kind.siteKm2(); }

    /* =====================================================================
       WHERE THE SITES LIE (0.7.58)

       On a square grid one site wide, centred on the field's centre plot:
       the k-th site at the k-th nearest grid point (SITE_PLACES), ring by
       ring, so a field is as compact as its sites allow - a round patch of
       its area. The partly filled outer ring is laid from one of four sides,
       turned by a quarter for each of the field's own quarter-turns (turn()),
       so fields do not all fill their last ring from the north.
       ===================================================================== */

    /**
     * The grid points a field's sites stand on, nearest the centre first, in
     * site widths east and south: every point within 14 of the centre each
     * way sorted by its squared distance, then from north to south, then west
     * to east, the first World.MAX_SITES of them. 14 is enough: the 512th
     * point lies about 12.8 from the centre (512 = pi r^2), so every ring the
     * table reaches is whole in the box.
     */
    static final int[][] SITE_PLACES = places();

    /** How far, in site widths each way (L-infinity), the first k + 1 sites reach from the centre. */
    private static final int[] PLACES_REACH = reaches();

    private static int[][] places() {
        int box = 14, n = (2 * box + 1) * (2 * box + 1), at = 0;
        int[][] all = new int[n][];
        for (int j = -box; j <= box; j++) for (int i = -box; i <= box; i++) all[at++] = new int[] { i, j };
        java.util.Arrays.sort(all, (a, b) -> {
            int da = a[0] * a[0] + a[1] * a[1], db = b[0] * b[0] + b[1] * b[1];
            if (da != db) return Integer.compare(da, db);
            if (a[1] != b[1]) return Integer.compare(a[1], b[1]);
            return Integer.compare(a[0], b[0]);
        });
        return java.util.Arrays.copyOf(all, World.MAX_SITES);
    }

    private static int[] reaches() {
        int[] out = new int[World.MAX_SITES];
        int most = 0;
        for (int k = 0; k < out.length; k++) {
            most = Math.max(most, Math.max(Math.abs(SITE_PLACES[k][0]), Math.abs(SITE_PLACES[k][1])));
            out[k] = most;
        }
        return out;
    }

    /** A site's width in plots, for a resource: the side of a square of its site area - an Iron Mine's 0.03716 km2 is 6.43 plots (193 m). */
    public static double siteWidth(Resource r) { return Math.sqrt(r.siteKm2() / World.KM2_PER_PLOT); }

    /** The quarter-turns its grid is laid at, 0 to 3: from its kind, cell and index, so the same field always lies the same way. */
    int turn() {
        return (int) (World.mix(((long) cell << 24) ^ ((long) index << 4) ^ kind.ordinal() ^ 0x51735L) & 3);
    }

    /** Where its k-th site's centre lies, from 0 to sites - 1: {plots east, plots south} of its centre plot. */
    public double[] siteAt(int k) {
        int i = SITE_PLACES[k][0], j = SITE_PLACES[k][1];
        switch (turn()) {
            case 1:  { int t = i; i = -j; j = t; break; }
            case 2:  { i = -i; j = -j; break; }
            case 3:  { int t = i; i = j; j = -t; break; }
            default: break;
        }
        double w = siteWidth(kind);
        return new double[] { i * w, j * w };
    }

    /**
     * Its k-th site's share of its amount, whole: floor((k + 1) A / S) less
     * floor(k A / S), so each site holds the amount over its sites to the
     * whole unit, the remainders spread one each through the sites, and the
     * shares of all S sites sum to the amount exactly - the last taking
     * whatever a fraction left (an amount is whole, so nothing).
     */
    public double siteAmount(int k) {
        return cumulative(k + 1) - cumulative(k);
    }

    private double cumulative(int k) {
        if (k >= sites) return amount;
        long whole = (long) Math.floor(amount);
        return Math.floorDiv(whole * k, sites);
    }

    /** How far its sites reach from its centre plot, in plots each way (L-infinity): the outermost site's centre and half a site. */
    public double reach() {
        return (PLACES_REACH[Math.max(0, Math.min(sites, World.MAX_SITES) - 1)] + 0.5) * siteWidth(kind);
    }

    /** ...and the most any field of a resource reaches: World.MAX_SITES of its sites. */
    public static double mostReach(Resource r) {
        return (PLACES_REACH[World.MAX_SITES - 1] + 0.5) * siteWidth(r);
    }

    /* =====================================================================
       THE CRUDE'S GRADE (0.7.79, batch O3; runs/spec-oil.md 2.2)

       An oil field is light, medium or heavy crude, and the grade decides
       what a barrel of it cuts into (Refining.CUTS): light is Brent's column
       [R1], more naphtha and diesel; heavy is Maya's [R2], over a third of it
       residue that only cutting with diesel, a coker or an asphalt unit
       turns into money; medium is the research's blend, what the world's
       imports are. A pure function of the field's cell, index and kind,
       drawn like its turn() - so nothing is stored, no tonne moves, and the
       world's totals (unowned + remaining + extracted = W) hold exactly as
       they did. Read for oil; the draw is the same for any field.
       ===================================================================== */

    /** A crude's grade: LIGHT (Brent's cuts), MEDIUM (the research's blend) or HEAVY (Maya's), in that order. */
    public enum Grade { LIGHT, MEDIUM, HEAVY }

    /** The share of fields of each grade, in Grade's order: a third each (est., spec-oil 2.2 and 6 - to confirm; the research gives a grade a field, not the world's mix). */
    public static final double[] GRADE_SHARES = { 1.0 / 3, 1.0 / 3, 1.0 / 3 };

    /** What makes a field's grade a draw of its own, apart from its turn()'s. */
    static final long GRADE_SALT = 0x6A7DE5L;

    /**
     * Its crude's grade (0.7.79): a uniform draw from World.mix() of its
     * cell, index, kind and GRADE_SALT, against GRADE_SHARES in Grade's
     * order - the same field always the same grade, on any machine.
     */
    public Grade grade() {
        double u = World.unit(World.mix(((long) cell << 24) ^ ((long) index << 4) ^ kind.ordinal() ^ GRADE_SALT));
        Grade[] all = Grade.values();
        double upTo = 0;
        for (int g = 0; g < all.length - 1; g++) {
            upTo += GRADE_SHARES[g];
            if (u < upTo) return all[g];
        }
        return all[all.length - 1];
    }
}
