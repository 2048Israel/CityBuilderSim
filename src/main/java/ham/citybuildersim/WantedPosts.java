package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The posts the city's businesses wanted this month and the staffing gate would not let them open, and the part of
 * them the labour market prices as if they were offered (LD1).
 *
 * WHY. Jerus, 2026-10-31: "my city was stuck... i had available mines but the mining industry wasnt building cause it
 * wouldnt have enough workers... and new people didnt want to come cause the wage was trash, perhaps a fix would be
 * that if the reason is not enough workers and the business can... then it raises its wage?" The loop was closed on
 * both sides. A building is ordered only if MIN_STAFFABLE_TO_ORDER of its posts can be filled from spare workers
 * (Sector.staffing()), and a wage rises only on posts that exist (LabourMarket.advanceMonth(): posts over supply per
 * band). A refused building posts no jobs, so the band it was short in never saw it: in the playtest's own founding
 * (AUTO2 seed 0, main 0.8.89) a Machine Works waited 255 months for the 34 college and 4 university workers of its
 * 173 posts while the college wage sat at 1.05-1.08x, and migrants, who come for a band's premium
 * (Migration.reach()), had little reason to.
 *
 * THE RULE. A planner whose building the gate refused hands the refusal in with its test (Decision.heldForStaff);
 * Game.noteStaffingHold() sorts it: held for the builders' laid-off crews (IC1's trap, not a shortage a wage
 * answers), held though it would not carry its interest (no wage rise would make it worth building), or an Ask for the
 * posts of the bands it is short in, with how far the wage of those posts could rise and the building still carry its
 * interest. settle() then turns the month's asks into a latent post count per band, which Game.repriceLabour() adds
 * to the band's posts for the wage alone: the multiple is priced as if the refused posts were offered, at the
 * market's own pace (ADJUST_RATE) and cap (MAX_MULTIPLE), and the gate's own test and Migration's opportunity still
 * read only the posts that exist.
 *
 * It decays by being rebuilt: settle() replaces the latent posts every month from that month's asks, so a building
 * the gate lets through, or a sector that stops asking, bids nothing the month after and the wage walks back.
 */
public final class WantedPosts {

    /**
     * The most of a band's own workers its refused posts may count as. The multiple goes as the root of the
     * tightness (LabourMarket.ELASTICITY, .5), so posts as many as the band has workers add at most one to its
     * tightness; from the floor's tightness (MIN_MULTIPLE .7, tightness .49) that is a multiple at most
     * sqrt(1.49 / .49) = 1.74 times as high: the refused posts can never double a band's wage.
     */
    public static final double MOST_OF_SUPPLY = 1.0;

    /**
     * One refused building's ask: its sector and building, the posts it lacks workers for per band (only in the bands
     * it is short in), and how far the wage of those posts could rise, as a factor on today's, and the building
     * still carry its interest.
     */
    public record Ask(String sector, String building, double[] posts, double payFactor) {
        public Ask {
            posts = posts == null ? new double[0] : posts.clone();
        }
    }

    /**
     * The ask a refused building makes, or null when it would not carry its interest even at today's wage
     * ({@code margin} is what it earns over the interest hurdle). Its posts are the building's posts in the bands
     * where it wants more than the band has spare; {@code shortBill} is what those posts cost a month at today's
     * wages, so {@code 1 + margin / shortBill} is the most the wage of the posts it lacks could rise by and the
     * building still carry its interest (infinite when it lacks nothing that costs).
     */
    public static Ask askOf(String sector, String building, double[] wanted, double[] spare,
                            double margin, double shortBill) {
        if (!(margin > 0)) return null;
        double[] posts = new double[wanted.length];
        for (int b = 0; b < wanted.length; b++) {
            double free = b < spare.length ? Math.max(0, spare[b]) : 0;
            if (wanted[b] > free) posts[b] = wanted[b];
        }
        double payFactor = shortBill > 0 ? 1 + margin / shortBill : Double.POSITIVE_INFINITY;
        return new Ask(sector, building, posts, payFactor);
    }

    private final List<Ask> asks = new ArrayList<>();
    private int crewsHolds, unpaidHolds;
    private final double[] latent = new double[WageBand.values().length];

    /** The month's start: last month's asks and counts cleared (the latent posts stay until settle() replaces them). */
    public void beginMonth() {
        asks.clear();
        crewsHolds = 0;
        unpaidHolds = 0;
    }

    /** A hold the gate put on for the builders' laid-off crews: IC1's, no shortage the wage answers. */
    public void heldForCrews() { crewsHolds++; }

    /** A hold for want of workers on a building that would not carry its interest at today's wage: no ask. */
    public void heldUnpaid() { unpaidHolds++; }

    /** A hold for want of workers on a building that would pay: it asks for its posts. */
    public void ask(Ask a) {
        if (a != null) asks.add(a);
    }

    /** Every staffing hold this month, whatever came of it. */
    public int getHolds() { return crewsHolds + unpaidHolds + asks.size(); }

    public int getCrewsHolds() { return crewsHolds; }

    public int getUnpaidHolds() { return unpaidHolds; }

    /** The holds that asked. */
    public int getAsked() { return asks.size(); }

    /** This month's asks. */
    public List<Ask> asks() { return Collections.unmodifiableList(new ArrayList<>(asks)); }

    /**
     * Turns the month's asks into the posts per band the wage is priced on, replacing last month's. For each band the
     * asked posts are summed, then held to the most the best-paying building that wants them could still pay (the
     * tightness at today's premium times its payFactor, less the posts that exist: Jerus's "and the business can"),
     * and never more than MOST_OF_SUPPLY of the band's own workers.
     *
     * @param posts   the band's posts that exist (PopulationManager.staffablePostsByBand())
     * @param supply  the band's workers (PopulationManager.supplyByBand())
     * @param premium the band's wage over its base today (LabourMarket.bandPremium())
     */
    public void settle(double[] posts, double[] supply, double[] premium) {
        Arrays.fill(latent, 0);
        for (int b = 0; b < latent.length; b++) {
            double asked = 0, room = Double.NEGATIVE_INFINITY;
            double workers = Math.max(b < supply.length ? supply[b] : 0, 1);
            double now = b < posts.length ? posts[b] : 0;
            double pay = b < premium.length ? premium[b] : 1;
            for (Ask a : asks) {
                if (b >= a.posts().length || !(a.posts()[b] > 0)) continue;
                asked += a.posts()[b];
                double reach = pay * a.payFactor();
                if (Double.isNaN(reach)) reach = 0;
                room = Math.max(room, LabourMarket.tightnessAt(Math.min(LabourMarket.MAX_MULTIPLE, reach)) * workers - now);
            }
            if (asked > 0) latent[b] = Math.max(0, Math.min(asked, Math.min(MOST_OF_SUPPLY * workers, room)));
        }
    }

    /** The posts per band the wage is priced on beyond those that exist (a copy). */
    public double[] latent() { return latent.clone(); }

    public double getLatent(WageBand band) { return latent[band.ordinal()]; }

    public double getTotal() {
        double sum = 0;
        for (double v : latent) sum += v;
        return sum;
    }

    /** Whether any band is priced on refused posts. */
    public boolean any() { return getTotal() > 0; }

    /** The latent posts as saved: they are priced next month, and cannot be rebuilt from the state this month ends in. */
    public double[] state() { return latent.clone(); }

    /** Puts the saved latent posts back; null or a wrong length leaves none. */
    public void restore(double[] saved) {
        Arrays.fill(latent, 0);
        if (saved == null || saved.length != latent.length) return;
        for (int b = 0; b < latent.length; b++) latent[b] = saved[b] > 0 ? saved[b] : 0;
    }
}
