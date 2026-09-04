package ham.citybuildersim;

/**
 * Contributions off every wage, and a pension for everyone too old to work.
 *
 * ==================== WHY THIS EXISTS ====================
 *
 * Splitting the household books by pay tier put a row on screen that read:
 *
 *     Retired (no earner)   147 homes   203 people   earned $0   -$95.8k
 *
 * A seventh of the city's households had NO INCOME AT ALL. Not a low income - no
 * pension, no savings to draw on, no family supporting them, nothing. They paid
 * rent and bought food out of money that did not exist, and the only reason the
 * city's books balanced was that nobody was tracking whose money it was.
 *
 * Jerus: "this is going to hurt... but add cpp to everyones pay, aka they pay a
 * tad, and make it so that the government pays for the seniors."
 *
 * =========================================================
 *
 * TWO HALVES, AND THEY DELIBERATELY DO NOT BALANCE.
 *
 * Contributions are a slice of every wage. The pension is a flat amount paid to
 * every senior. The first does not cover the second and is not meant to: the
 * city carries the difference out of general revenue, which is exactly how the
 * real thing works (CPP is contributory, Old Age Security is not) and exactly
 * what Jerus asked for - the workers pay a tad, the government pays for the
 * seniors.
 *
 * The size of that gap is the interesting number, and coverage() puts it on
 * screen rather than burying it. A city whose pyramid ages watches it widen
 * without anything else changing, which is the first time the age structure has
 * had a cost attached to it.
 *
 * WHAT IT IS NOT
 *
 * Not a fund. Nothing is invested, nothing accumulates, and this month's
 * contributions pay this month's pensions - pay-as-you-go, which is the
 * simplest honest version and the one whose failure mode (too few workers per
 * pensioner) is the one worth modelling in a city builder.
 *
 * Not earnings-related. Every senior gets the same, whatever they earned, which
 * is a flat pension of the OAS kind. An earnings-related pension would need a
 * contribution history per person, and nobody in this model is a person.
 */
public class SocialSecurity {

    /* ------------------------------- the dials ------------------------------- */

    /**
     * Taken off every wage, at the real CPP employee rate.
     *
     * 5.95% is what a Canadian worker actually pays, and using the real figure
     * rather than a round one means the number on the payslip is checkable
     * against something outside this game. Jerus asked for "a tad"; this is
     * roughly a seventeenth of a wage.
     *
     * The employer half is NOT modelled. Adding it would double the revenue and
     * halve the shortfall, and it belongs with a proper payroll-cost model
     * rather than being smuggled in as a rate.
     */
    public static final double CONTRIBUTION_RATE = .0595;

    /**
     * What the pension replaces, as a share of an unskilled wage.
     *
     * Derived from the wage table rather than typed, so raising pay raises
     * pensions with it - the same drift-proofing the rent price got, and for the
     * same reason: four constants in this codebase were correct when written and
     * silently invalidated by a change somewhere else.
     *
     * 45% is about what CPP and OAS together replace for an average Canadian
     * earner. Against this game's compressed wage scale it means a senior couple
     * can cover a home between them and a senior living alone cannot quite -
     * which is true of the real thing too, and is the reason pensioner poverty
     * is concentrated among people living alone.
     */
    public static final double PENSION_REPLACEMENT = .45;

    /* ------------------------------ the arithmetic ------------------------------ */

    /** What one pensioner receives a month. */
    public static double pensionPerSenior() {
        return PENSION_REPLACEMENT * PayTier.UNSKILLED.getMonthlyWage();
    }

    /** Taken off the month's wage bill. */
    public static double contributionsOn(double wageBill) {
        return Math.max(0, wageBill) * CONTRIBUTION_RATE;
    }

    /** Paid out to everyone over the retirement age. */
    public static double pensionsFor(double seniors) {
        return Math.max(0, seniors) * pensionPerSenior();
    }

    /**
     * How much of the pension bill the contributions actually cover, 0-1.
     *
     * THE NUMBER WORTH WATCHING. It is a pure function of the dependency ratio:
     * contributions scale with workers, pensions with pensioners, so this falls
     * as the city ages and nothing else has to change for it to. A city that
     * lets its pyramid grey is buying a structural deficit, and this is where it
     * shows up before the cash does.
     */
    public static double coverage(double wageBill, double seniors) {
        double owed = pensionsFor(seniors);
        if (owed <= 0) return 1;
        return contributionsOn(wageBill) / owed;
    }

    /** What general revenue has to find, over and above the contributions. */
    public static double shortfall(double wageBill, double seniors) {
        return Math.max(0, pensionsFor(seniors) - contributionsOn(wageBill));
    }

    private SocialSecurity() { }
}
