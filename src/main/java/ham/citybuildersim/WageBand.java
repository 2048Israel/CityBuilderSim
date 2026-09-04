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

    NONE      ("No diploma"),
    DIPLOMA   ("Diploma"),
    COLLEGE   ("College"),
    UNIVERSITY("University");

    private final String label;

    WageBand(String label) { this.label = label; }

    public String label() { return label; }

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
    public static WageBand of(JobType job) {
        return switch (job) {
            case NO_DIPLOMA -> NONE;
            case DIPLOMA    -> DIPLOMA;
            case COLLEGE_HEALTH, COLLEGE_BUSINESS, COLLEGE_ENGINEERING -> COLLEGE;
            default -> UNIVERSITY;
        };
    }
}
