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
 * interest. settle() then turns the month's asks into a latent post count per band, and Game.repriceLabour() adds
 * them to the band's posts for the wage alone (pricedOn()): the multiple is priced as if the refused posts were
 * offered, at the market's own pace (ADJUST_RATE) and cap (MAX_MULTIPLE), and the gate's own test and Migration's
 * opportunity still read only the posts that exist. The refused posts are the numerator of their own band's
 * tightness and nothing else: the supply the wage is struck against is the fill against the posts that exist, so a
 * refused post takes no worker the fill carries down to the bands below (LD2). And a band no migrant can come for
 * (Migration.admits()) asks nothing, since the wage is meant to bring migrants (LD2).
 *
 * WHEN. One Game.nextMonth() runs the whole of it: the month's planning (Game.runPrivateInvestment()) notes the holds
 * and ends with settle(), and the labour market is re-priced a few steps later in the same nextMonth()
 * (SimulationEngine.updatePopulation(), then Game.repriceLabour()). So a month's asks are priced in the month they
 * are made. The latent posts a save holds are replaced by the next month's settle before the next reprice reads
 * them: they are saved for what shows them between a load and that settle (the People screen's caption), not for
 * the wage.
 *
 * It decays by being rebuilt: settle() replaces the latent posts every month from that month's asks, so a building
 * the gate lets through, or a sector that stops asking, bids nothing from the next settle on and the wage walks back.
 */
public final class WantedPosts {

    /**
     * The most a band's refused posts may count as, as a share of the posts it has that exist. At one, the posts the
     * wage is priced on are at most doubled; the multiple goes as the root of the tightness (LabourMarket.ELASTICITY,
     * .5), and the supply it is struck against is the fill against the posts that exist (LD2: the refused posts are
     * only the numerator of their own band's tightness), so the refused posts lift a band's multiple at most sqrt(2) =
     * 1.41 times what its own posts set: one business's wish never doubles a wage. And on the band's posts rather than
     * its workers, because the gate holds a plant out of a village on purpose (Sector.MIN_STAFFABLE_TO_ORDER: "it puts
     * the first one in a city of three or four thousand rather than a village"): a village has few posts, so it bids
     * little for a plant no wage could staff there. Measured (LD1's notes 4): capped at the band's workers instead, the
     * founding village's unskilled wage was bid to 1.87x by month 10 against main's 0.92x, and the median young city
     * was 15% smaller at m300. (LD1 struck the supply on the posts plus the refused ones, so a band that asked nothing
     * lost the workers a band above would have carried down to it and was priced up, and the bands that did ask went
     * past this cap, up to 1.90: LD2's notes 3, four AUTO2 cities over 1,600 months.)
     */
    public static final double MOST_OF_POSTS = 1.0;

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
     * and never more than MOST_OF_POSTS of the posts the band has (pricedOn() holds it to that again at the reprice).
     * A band no migrant can come for asks nothing (LD2: the wage is meant to bring migrants); the hold is still an
     * ask, for the bands that are open.
     *
     * @param posts   the band's posts that exist (PopulationManager.staffablePostsByBand())
     * @param supply  the band's workers (PopulationManager.supplyByBand())
     * @param premium the band's wage over its base today (LabourMarket.bandPremium())
     * @param open    whether a migrant can come for the band (Migration.admits()); null: every band is open
     */
    public void settle(double[] posts, double[] supply, double[] premium, boolean[] open) {
        Arrays.fill(latent, 0);
        for (int b = 0; b < latent.length; b++) {
            if (open != null && b < open.length && !open[b]) continue;
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
            if (asked > 0) latent[b] = Math.max(0, Math.min(asked, Math.min(MOST_OF_POSTS * Math.max(now, 0), room)));
        }
    }

    /** settle() with every band open to migrants (the harness's direct calls). */
    public void settle(double[] posts, double[] supply, double[] premium) {
        settle(posts, supply, premium, null);
    }

    /**
     * The posts per band the wage is priced on: the posts given plus the latent posts, the latent first held, in
     * place, to MOST_OF_POSTS of the band's posts as they stand now. A new array comes back and the posts given are
     * not moved; with no latent posts it is the posts, unchanged.
     *
     * WHY. LD2, the review of LD1. settle() read the band's posts at the end of the month's planning and held the
     * latent posts to MOST_OF_POSTS of those, but Game.repriceLabour() prices them a few steps later in the same
     * month, after completions, strikeBuildersCrews() and retirement have moved the posts: a band's latent posts could
     * exceed MOST_OF_POSTS of the posts it is priced on (measured: 18 months in 6,400, four played cities), and the
     * sqrt(1 + MOST_OF_POSTS) cap on the multiple did not hold for them. Held here, at the reprice, to the posts it
     * reads, the cap is the cap of what is priced, and the latent posts the People caption and the save show are the
     * ones that were priced.
     */
    public double[] pricedOn(double[] posts) {
        double[] priced = posts.clone();
        for (int b = 0; b < latent.length && b < priced.length; b++) {
            latent[b] = Math.min(latent[b], MOST_OF_POSTS * Math.max(posts[b], 0));
            priced[b] += latent[b];
        }
        return priced;
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

    /**
     * The latent posts as saved. The next month's settle replaces them before the next reprice reads them, so the wage
     * never reads a saved one; they are saved for what shows them between a load and that settle (the People caption),
     * and cannot be rebuilt from the state the month ends in (the refusals are gone with the plans).
     */
    public double[] state() { return latent.clone(); }

    /** Puts the saved latent posts back; null or a wrong length leaves none. */
    public void restore(double[] saved) {
        Arrays.fill(latent, 0);
        if (saved == null || saved.length != latent.length) return;
        for (int b = 0; b < latent.length; b++) latent[b] = saved[b] > 0 ? saved[b] : 0;
    }
}
