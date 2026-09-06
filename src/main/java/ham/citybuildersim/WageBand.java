package ham.citybuildersim;

/**
 * The four education bands the wage tax is set by.
 *
 * There are eleven JobTypes and eleven separate wage rates behind them, but a
 * tax screen with eleven dials is not a decision - it is data entry, and ten of
 * the eleven would move together anyway. These are the bands the job types
 * already fall into by the education they require, so setting them is the
 * progressive-tax choice in the form a player actually thinks about it: what do
 * I charge the people I most want to attract?
 *
 * The arithmetic is still exact. PopulationManager tracks the wage bill per job
 * type, so the tax is summed per type at its band's rate - the banding decides
 * what the player sets, never what is charged.
 */
public enum WageBand {

    NONE      ("No diploma",  .50),
    DIPLOMA   ("Diploma",     .34),
    COLLEGE   ("College",     .12),
    UNIVERSITY("University",  .04);

    private final String label;
    private final double worldShare;

    WageBand(String label, double worldShare) {
        this.label = label;
        this.worldShare = worldShare;
    }

    public String label() { return label; }

    /* =====================================================================
       THE SECOND JOB THIS ENUM DOES

       It started as a tax banding - four dials instead of eleven. It is now
       also the SKILL LADDER, because it was already the right one: it is the
       education each job requires, which is the only axis a worker can be
       short of.

       Two properties follow from that and both are load-bearing.

       SUBSTITUTION RUNS DOWNWARD ONLY. A university graduate can labour; a
       labourer cannot doctor. So a band's surplus cascades down into the bands
       beneath it and never up, and rank() is what orders that cascade.

       WITHIN A BAND, WORKERS ARE FUNGIBLE. A university graduate can be a
       doctor or a lawyer or a policy officer. That is a simplification - real
       training is not interchangeable - and it is the right one here, because
       the alternative is eleven separate supply pools the player has no way to
       influence. The player attracts GRADUATES; the city decides what they do.
       The design note said otherwise before it met the bootstrap problem.
       ===================================================================== */

    /** 0 for unskilled, 3 for university. Higher can do lower's work. */
    public int rank() { return ordinal(); }

    /**
     * Roughly what share of the outside world's workers sit at this level.
     *
     * THIS IS WHAT MAKES SCHOOLS NECESSARY LATER, and it is the answer to
     * "a 4,000-person city needs scientists and cannot have a science school".
     *
     * Migrants are drawn by the wage premium, but they are drawn out of a world
     * that has far more labourers than graduates. So the same premium pulls in
     * a flood of the first and a trickle of the last. A small city CAN import
     * its doctor - slowly, expensively, one at a time. A city of eighty
     * thousand cannot import two hundred of them at any price.
     *
     * Which means the handoff to home-grown education arrives by arithmetic
     * rather than by rule: import while you are small, train once you are big,
     * with nothing to unlock and nothing to explain.
     *
     * Loosely a developed economy's attainment mix, PULLED DOWN AT THE TOP once
     * it met the game's own demand curve. Measured: the buildings in this game
     * offer 51% unskilled / 41% diploma / 6% college / 1.4% university posts,
     * and a world of 46/32/16/6 poured four times as many graduates into a city
     * as it had graduate jobs. That is not the world being wrong - a real
     * economy employs far more professionals than this building set does - but
     * a migration model has to be aimed at the jobs that exist, not at the ones
     * a fuller game would have.
     *
     * Revisit these the day the building set grows a professional sector.
     */
    public double worldShare() { return worldShare; }

    /**
     * How readily somebody at this level will move away for work.
     *
     * EDUCATED PEOPLE ARE MORE MOBILE, NOT LESS, and it is not a small
     * difference. Jerus guessed the other way, which is the common intuition -
     * a graduate has a career and a mortgage and roots - and the measured
     * answer is the reverse of it. The Boston Fed, tracking the same people
     * from 1979 to 1996, found the share who changed STATE was:
     *
     *     high school only .................. 19.2%
     *     four-year degree ................. 36.6%
     *     more than four years ............. 45.0%
     *
     * Roughly double, and better than double at the top. The reason is the
     * shape of the market rather than anything about the person: a labourer's
     * job market is the town they are in, so leaving is a gamble; a
     * radiologist's is the whole country, so a town with no radiology is
     * simply somewhere they are not going to stay.
     *
     * Which makes this the honest fix for what Jerus was seeing - graduates
     * piling up in a city with nothing for them to do. In reality they leave,
     * faster than anybody else. The multipliers are those percentages
     * normalised on the high-school figure, with the no-diploma band placed
     * below it for the same reason it sits below in the table.
     */
    public double mobility() {
        switch (this) {
            case NONE:       return .80;
            case DIPLOMA:    return 1.00;
            case COLLEGE:    return 1.90;
            case UNIVERSITY: return 2.35;
            default:         return 1.00;
        }
    }

    /**
     * Which band a job sits in.
     *
     * A switch rather than a field on JobType, because JobType is the game's
     * labour model and this is a tax policy detail - a new job tier should not
     * have to know that a tax screen exists. The default is UNIVERSITY on
     * purpose: a job type nobody has classified is more likely to be a new
     * specialist role than a new unskilled one, and taxing it at the top band is
     * the error that gets NOTICED rather than the one that quietly under-collects.
     */
    /** The band a worker of this level can also work down into. */
    public static WageBand[] ladder() { return values(); }

    public static WageBand of(JobType job) {
        return switch (job) {
            case NO_DIPLOMA -> NONE;
            case DIPLOMA    -> DIPLOMA;
            case COLLEGE_HEALTH, COLLEGE_BUSINESS, COLLEGE_ENGINEERING -> COLLEGE;
            default -> UNIVERSITY;
        };
    }
}
