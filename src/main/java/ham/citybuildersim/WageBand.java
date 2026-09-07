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

    NONE      ("No diploma",  .00),
    DIPLOMA   ("Diploma",    1.00),
    COLLEGE   ("College",     .35),
    UNIVERSITY("University",  .12);

    private final String label;
    private final double arrivalCeiling;

    WageBand(String label, double arrivalCeiling) {
        this.label = label;
        this.arrivalCeiling = arrivalCeiling;
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
     * The most of a month's arrivals this band can ever be, relative to the
     * diploma band's 1.00 - and it is reached only at the wage ceiling.
     *
     * THE WORLD HAS UNIVERSAL HIGH SCHOOL AND NOTHING ELSE FOR FREE. That is
     * the whole model, and it replaces an attainment mix that kept losing this
     * argument. Three properties fall out of it:
     *
     * NOBODY ARRIVES WITHOUT A DIPLOMA. NONE is 0.00. The only people in the
     * unskilled band are the ones this city failed to put through school -
     * children born here, and the children of migrants, who aged out of the
     * teen band while the high schools were full or absent. So the unskilled
     * band is a REPORT CARD rather than an import, and a city that builds its
     * schools genuinely stops producing unskilled adults.
     *
     * A DIPLOMA IS THE BASE, at 1.00 and unconditional. It is not competed for
     * and does not respond to a premium: it is what an ordinary person moving
     * to an ordinary city has.
     *
     * ABOVE THAT, ARRIVALS ARE DEMAND ONLY. At the going rate a graduate has no
     * reason to prefer this city to the hundred others hiring, so the pull is
     * zero and stays zero - which is the fix for the thing Jerus kept seeing,
     * a city with no schools and no graduate jobs filling up with graduates
     * anyway. The ceilings bite only as the city bids the wage up, and at the
     * 4x ceiling they reproduce a developed economy's own graduate mix
     * (.35 and .12 against a diploma's 1.00 is 24% college and 8% university).
     *
     * Which keeps the property the old mix was built for: a small city can
     * import its doctor, slowly and expensively, and a city of eighty thousand
     * cannot import two hundred at any price. Import while you are small, train
     * once you are big - by arithmetic, with nothing to unlock.
     *
     * See Migration.reach() for the curve, and Migration.composeArrivals() for
     * how a licensed profession pulls on its own.
     */
    public double arrivalCeiling() { return arrivalCeiling; }

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
