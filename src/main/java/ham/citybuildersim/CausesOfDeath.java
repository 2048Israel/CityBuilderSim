package ham.citybuildersim;

/**
 * The causes of death of 1900, and how history and the city take them away.
 *
 * WHY. The game's deaths had no causes. A city founded in 1900 lost its
 * adults at AgeBand's 0.225% a year, roughly a third of what Canada lost in
 * 1901, and its babies and children died only by childcare's swing
 * (Healthcare.CHILDCARE_SWING); typhoid, the summer diarrhoea, the childhood
 * outbreaks, TB, mothers at home births, pneumonia and the 1918 influenza were
 * nowhere. So the catalogue of discoveries (R2: chlorination, the toxoid,
 * sulfa, streptomycin, penicillin) had nothing to act on. Seven causes now
 * each carry a death rate by band, and history and the city move them. See
 * docs/queue/R0b-notes.md and docs/studies/research-1900.md, 2.7.
 *
 * NOBODY DIES HERE EITHER. Like Sickness, this class says how likely each band
 * is to die of each cause this month and remembers the dead it is told were
 * booked; PopulationCohorts does the dying, off the same opening balance as the
 * band's own rate, illness and violence (a fourth competing risk).
 *
 * THE CAUSES ADD, EXCEPT WHERE THE SWING ALREADY HOLDS THEM. For teenagers,
 * adults, seniors and elders the rate in AgeBand is today's, so the causes of
 * 1900 are on top of it. For babies and children, whose unserved rate
 * childcare's swing already sets at 1900's level, the causes SPLIT the swing's
 * excess deaths (the rate above today's): each takes its 1900 share, history
 * and the city move its part, and the factor the band keeps (keptFactor) is
 * what the causes do not name. At 1900 conditions a baby's total is unchanged.
 *
 * HISTORY IS THE PATH, THE CITY ONLY RAISES IT (R0b call 3). Each cause follows
 * Canada's observed rate on its dates, each step named by the catalogue row
 * that will later be the city's find; water short, hunger and no general care
 * lift a cause above that path and never below it. Every cause runs to zero by
 * 1980 (est.), so a city past it dies as AgeBand says it does today.
 */
public class CausesOfDeath {

    /** The seven causes, in the order every array here is indexed. */
    public enum Cause {
        TYPHOID("typhoid"),
        DIARRHOEA("summer diarrhoea"),
        OUTBREAK("childhood outbreaks"),
        TB("tuberculosis"),
        PNEUMONIA("pneumonia"),
        CHILDBIRTH("childbirth"),
        INFLUENZA("the 1918 influenza");

        private final String label;

        Cause(String label) { this.label = label; }

        public String label() { return label; }
    }

    private static final Cause[] CAUSES = Cause.values();
    private static final AgeBand[] BANDS = AgeBand.values();

    /* ===================================================================
       1900's NUMBERS - all-ages rates, and who they fall on
       =================================================================== */

    /**
     * Canada's 1901 people by band, in band order (est., from the census's
     * five-year groups); it turns an all-ages rate into a band's.
     */
    public static final double[] PEOPLE_1901 = {.143, .154, .102, .566, .032, .003};

    /**
     * The 1900 death rate of each cause, per 100,000 people a year, all ages.
     * Typhoid is Toronto 1910 [TOR-W]; summer diarrhoea the US 1900 [GOR53];
     * the childhood outbreaks the US 1900's 243 per 100,000 children under 15
     * times the under-15s' .338 share of 1901's people; TB Canada 1901
     * [LAC-TB]; pneumonia the US 1900's influenza and pneumonia [VCU].
     * Childbirth and the influenza have their own dials below, so 0 here.
     */
    public static final double[] RATE_1900 = {40.8, 155, 82, 180, 202, 0, 0};

    /**
     * The share of each cause's deaths that falls on each band, in band order
     * (est.; each row sums to 1): who died of it in 1900, judged from each
     * disease's age pattern.
     */
    public static final double[][] DEATH_SHARE = {
        /* TYPHOID   */ {.03, .12, .12, .68, .05, 0},
        /* DIARRHOEA */ {.85, .02, 0, .05, .06, .02},
        /* OUTBREAK  */ {.65, .32, .03, 0, 0, 0},
        /* TB        */ {.05, .04, .10, .73, .07, .01},
        /* PNEUMONIA */ {.35, .03, .02, .30, .20, .10},
        /* CHILDBIRTH*/ {0, 0, 0, 1, 0, 0},
        /* INFLUENZA */ {.10, .05, .07, .70, .06, .02}
    };

    /**
     * Mothers dying in childbirth per 1,000 live births in 1900: Canada 1921's
     * 4.7, the first figure there is, held back to 1900 [SC-B51].
     */
    public static final double MOTHERS_1900 = 4.7;

    /** The 1918 influenza killed .6% of the people: about 50,000 Canadians [FLU-W]. */
    public static final double INFLUENZA_DEATHS = .006;

    /** A quarter of the people were ill in the 1918 influenza (est.). */
    public static final double INFLUENZA_SICK = .25;

    /** The first month of the influenza: October 1918. */
    public static final int INFLUENZA_FIRST = monthOf(1918, 10);

    /** The last month of the influenza: March 1920, its third wave. */
    public static final int INFLUENZA_LAST = monthOf(1920, 3);

    /* the game month of a calendar month: game month 1 is January 1900 */
    private static int monthOf(int year, int monthOfYear) {
        return (year - CityCalendar.EPOCH_YEAR) * 12 + monthOfYear;
    }

    /* The three waves: October-December 1918 (.35, .35, .10 of the dead and the sick),
       January-April 1919 (.03 each), January-March 1920 (.08 between them). */
    private static final int[] FLU_MONTHS = {
        monthOf(1918, 10), monthOf(1918, 11), monthOf(1918, 12),
        monthOf(1919, 1), monthOf(1919, 2), monthOf(1919, 3), monthOf(1919, 4),
        monthOf(1920, 1), monthOf(1920, 2), monthOf(1920, 3)
    };
    private static final double[] FLU_SHARES = {
        .35, .35, .10, .03, .03, .03, .03, .08 / 3, .08 / 3, .08 / 3
    };

    /* ===================================================================
       SEASONS - each averages exactly 1 over a year
       =================================================================== */

    /** Summer diarrhoea in July, August and September is two and a half times a mean month's. */
    public static final double DIARRHOEA_SUMMER = 2.5;

    /** ...and in the other nine months, what makes the year average 1. */
    public static final double DIARRHOEA_REST = (12 - 3 * DIARRHOEA_SUMMER) / 9;

    /** Pneumonia in December, January, February and March is one and six-tenths a mean month's. */
    public static final double PNEUMONIA_WINTER = 1.6;

    /** ...and in the other eight months, what makes the year average 1. */
    public static final double PNEUMONIA_REST = (12 - 4 * PNEUMONIA_WINTER) / 8;

    /* ===================================================================
       OUTBREAKS - Health's, the same ones
       =================================================================== */

    /**
     * The long-run mean of Health's unmitigated outbreakSeverity, so that the
     * childhood outbreaks' rate, which is 1900's on average, rises and falls
     * with the outbreaks the city actually has. An outbreak lasts as many months
     * as its peak, decayed by OUTBREAK_DECAY, stays at or above OUTBREAK_FLOOR;
     * the next one is rolled the month this one ends and then every month at
     * OUTBREAK_CHANCE. So the mean is the sum of the decay series over the
     * outbreak, averaged over the peaks Health draws, divided by the expected
     * cycle: the outbreak's months plus the quiet months before the next roll
     * comes up. CausesOfDeathCheck runs Health alone and measures it.
     */
    public static final double OUTBREAK_MEAN_SEVERITY = meanOutbreakSeverity();

    private static double meanOutbreakSeverity() {
        int steps = 20_000;
        double severityMonths = 0, months = 0;
        for (int i = 0; i < steps; i++) {
            double peak = Health.OUTBREAK_MIN_PEAK
                    + (i + .5) / steps * (Health.OUTBREAK_MAX_PEAK - Health.OUTBREAK_MIN_PEAK);
            double severity = peak;
            for (int k = 0; k < 1200 && severity >= Health.OUTBREAK_FLOOR; k++) {
                severityMonths += severity;
                months++;
                severity *= Health.OUTBREAK_DECAY;
            }
        }
        double quiet = (1 - Health.OUTBREAK_CHANCE) / Health.OUTBREAK_CHANCE;
        return (severityMonths / steps) / (months / steps + quiet);
    }

    /* ===================================================================
       THE CITY'S CONDITIONS - est., each multiplies a cause above history's path
       =================================================================== */

    /** TB is 1 + 2 x the hungry share higher: hunger is what lets it take hold (est.). */
    public static final double TB_HUNGER = 2.0;

    /** Summer diarrhoea is 1 + 1 x the hungry share higher (est.). */
    public static final double DIARRHOEA_HUNGER = 1.0;

    /**
     * TB, pneumonia and childbirth are 1 + .5 x the share with no general care
     * higher (est.), for teenagers and over: babies' and children's care is
     * childcare's, "one care type per band" (Healthcare).
     */
    public static final double UNCARED = .5;

    /** All the multipliers on one cause together never come to more than this (est.). */
    public static final double CONDITION_CAP = 3.0;

    /**
     * The splitting bands' causes never name more than this share of the swing's
     * excess (est.): the children's shares sum to about 1.05 at the game's own
     * rates, and a cause cannot take more than the swing holds.
     */
    public static final double MAX_SPLIT = .9;

    /* ===================================================================
       HISTORY - Canada's observed rates, as a factor of the 1900 rate
       =================================================================== */

    /* Each cause's knots: {decimal year, factor of the 1900 rate}. Linear between,
       1 before the first, the last one's after it. Indexed like Cause. */
    private static final double[][][] KNOTS = {
        /* TYPHOID */ {
            {1900, 1},
            {1910, 1},              // Toronto 1910: 40.8, the level 1900 starts from [TOR-W]
            {1915, 1 / 40.8},       // chlorination, catalogue row PE9: Toronto 40.8 to 1.0
            {1950, .5 / 40.8},      // about 0 by 1946-50 [SC-B35]; 0.5 (est.)
            {1980, 0}
        },
        /* DIARRHOEA */ {
            {1900, 1},
            {1923, 72 / 155.0},     // Canada 1921-25: 72 [SC-B35]
            {1948, 14 / 155.0},     // Canada 1946-50: 14 [SC-B35]
            {1980, 0}
        },
        /* OUTBREAK */ {
            {1900, 1},
            {1923, .44},            // Canada 1921-25's 47 with typhoid, less typhoid's (est.)
            {1948, .055},           // 5 of 1946-50 [SC-B35]; diphtheria toxoid, catalogue row PM3, from 1925
            {1980, 0}
        },
        /* TB */ {
            {1900, 1},
            {1948, 37 / 180.0},     // Canada 1946-50: 37 [SC-B35]
            {1952, 37 / 180.0 * .26}, // streptomycin, catalogue row PM6, Canada 1948: deaths in six months 27% to 7%
            {1980, 0}
        },
        /* PNEUMONIA */ {
            {1900, 1},
            {1923, 141 / 202.0},    // Canada 1921-25: 141 [SC-B35]
            {1937, .62},            // est.
            {1940, .465},           // sulfa, catalogue row PM4, 1937: -17% to -32%, -25% taken
            {1948, 55 / 202.0},     // Canada 1946-50: 55 [SC-B35]
            {1980, 0}
        },
        /* CHILDBIRTH */ {
            {1900, 1},
            {1921, 1},              // Canada 1921: 4.7 [SC-B51]
            {1930, 5.8 / 4.7},      // Canada 1930: 5.8, the rise
            {1937, 5.8 / 4.7},
            {1940, 5.8 * .7 / 4.7}, // sulfa, catalogue row PM4: -24% to -36%, -30% taken
            {1944, 2.8 / 4.7},      // penicillin, catalogue row PM5: 2.8
            {1950, 1.1 / 4.7},      // Canada 1950: 1.1 [SC-B51]
            {1980, 0}
        },
        /* INFLUENZA */ {}          // 1 always: its months carry it
    };

    /**
     * A cause's factor of its 1900 rate in a game month: the knots joined in a
     * straight line over the decimal year, 1 before the first, and 0 from 1980
     * on. Never negative. The influenza is 1 always: influenzaShare() carries it.
     */
    public static double history(Cause cause, int month) {
        if (cause == null) return 0;
        double[][] knots = KNOTS[cause.ordinal()];
        if (knots.length == 0) return 1;
        double year = CityCalendar.yearOf(month) + (CityCalendar.monthOfYear(month) - 1) / 12.0;
        if (year <= knots[0][0]) return Math.max(0, knots[0][1]);
        for (int i = 1; i < knots.length; i++) {
            if (year < knots[i][0]) {
                double span = knots[i][0] - knots[i - 1][0];
                double t = (year - knots[i - 1][0]) / span;
                return Math.max(0, knots[i - 1][1] + t * (knots[i][1] - knots[i - 1][1]));
            }
        }
        return Math.max(0, knots[knots.length - 1][1]);
    }

    /** The influenza's share of its dead and its sick that fell in this game month: 0 outside its ten. */
    public static double influenzaShare(int month) {
        for (int i = 0; i < FLU_MONTHS.length; i++) if (FLU_MONTHS[i] == month) return FLU_SHARES[i];
        return 0;
    }

    /** The share of the people sick of the influenza this month: slice 2 puts it on Health's rate. */
    public static double influenzaSick(int month) {
        return INFLUENZA_SICK * influenzaShare(month);
    }

    /** A cause's season in a game month: its mean is 1 over a year, and it is 1 for every cause without one. */
    public static double season(Cause cause, int month) {
        int m = CityCalendar.monthOfYear(month);
        if (cause == Cause.DIARRHOEA) return m >= 7 && m <= 9 ? DIARRHOEA_SUMMER : DIARRHOEA_REST;
        if (cause == Cause.PNEUMONIA) return m == 12 || m <= 3 ? PNEUMONIA_WINTER : PNEUMONIA_REST;
        return 1;
    }

    /* ===================================================================
       RATES AND SPLITS - what a cause was in 1900, band by band
       =================================================================== */

    /**
     * A cause's annual chance of killing a person of a band at 1900's rate:
     * the all-ages rate times the band's share of the dead, over the band's
     * share of the people.
     */
    public static double rate1900(Cause cause, AgeBand band) {
        if (cause == null || band == null) return 0;
        int c = cause.ordinal(), b = band.ordinal();
        return RATE_1900[c] * DEATH_SHARE[c][b] / PEOPLE_1901[b] / 100_000;
    }

    /** True for the bands childcare's swing sets (babies and children): the causes split their excess. */
    public static boolean isSwung(AgeBand band) {
        return band != null && CareType.CHILDCARE.servedBy(band);
    }

    /* the causes that split the swing's excess: childbirth and the influenza never do */
    private static boolean splits(Cause cause) {
        return cause != Cause.CHILDBIRTH && cause != Cause.INFLUENZA;
    }

    private static final double[][] SPLIT = new double[CAUSES.length][BANDS.length];

    static {
        for (AgeBand b : BANDS) {
            if (!isSwung(b)) continue;
            double excess = b.getAnnualMortality() * (Healthcare.CHILDCARE_SWING - 1);
            if (excess <= 0) continue;
            double raw = 0;
            for (Cause c : CAUSES) if (splits(c)) raw += rate1900(c, b) / excess;
            double scale = raw > MAX_SPLIT ? MAX_SPLIT / raw : 1;
            for (Cause c : CAUSES) {
                if (splits(c)) SPLIT[c.ordinal()][b.ordinal()] = rate1900(c, b) / excess * scale;
            }
        }
    }

    /**
     * The share of a swung band's swing excess (its mortality above today's)
     * that this cause takes at 1900: the cause's 1900 rate over the excess at
     * the top of the swing, the band's shares scaled down together so they sum
     * to no more than MAX_SPLIT. 0 for every other band, and for childbirth
     * and the influenza.
     */
    public static double share(Cause cause, AgeBand band) {
        if (cause == null || band == null) return 0;
        return SPLIT[cause.ordinal()][band.ordinal()];
    }

    /** The share of a swung band's excess the causes name together; 0 for any other band. */
    public static double split(AgeBand band) {
        double s = 0;
        for (Cause c : CAUSES) s += share(c, band);
        return s;
    }

    /**
     * The mortality factor the band keeps once the causes have taken their
     * part of the swing: f less the causes' share of the excess above 1. For a
     * band the swing does not set it is f.
     */
    public static double keptFactor(AgeBand band, double f) {
        if (!isSwung(band)) return f;
        return f - split(band) * Math.max(0, f - 1);
    }

    /**
     * What the city's conditions make of a cause: hunger for TB and summer
     * diarrhoea, and no general care for TB, pneumonia and childbirth in the
     * bands whose care is general care (not the swung ones); the product never
     * more than CONDITION_CAP and never less than 1. Water is not here: it
     * takes a share of the cause back to 1900's rate, in the history.
     */
    public static double conditions(Cause cause, AgeBand band, double hungry, double generalCoverage) {
        double h = clamp01(hungry), g = clamp01(generalCoverage);
        double m = 1;
        if (cause == Cause.TB) m *= 1 + TB_HUNGER * h;
        if (cause == Cause.DIARRHOEA) m *= 1 + DIARRHOEA_HUNGER * h;
        boolean care = cause == Cause.TB || cause == Cause.PNEUMONIA || cause == Cause.CHILDBIRTH;
        if (care && !isSwung(band)) m *= 1 + UNCARED * (1 - g);
        return Math.min(CONDITION_CAP, m);
    }

    /** Each band's chance of death, the sum over causes of a [cause][band] array. */
    public static double[] bandTotals(double[][] rates) {
        double[] out = new double[BANDS.length];
        if (rates == null) return out;
        for (double[] row : rates) {
            if (row == null) continue;
            for (int b = 0; b < out.length && b < row.length; b++) out[b] += row[b];
        }
        return out;
    }

    /* ------------------------------ state ------------------------------ */

    private static final int MONTHS = 12;

    /** The dead of each cause booked last month. */
    private final double[] lastDeaths = new double[CAUSES.length];

    /** The dead of each cause since the city began. */
    private final double[] totals = new double[CAUSES.length];

    /** The last twelve months' dead of each cause, one slot to a month, at month % 12. */
    private final double[][] ring = new double[CAUSES.length][MONTHS];

    /** The people sick of TB for a year or more: the long sick of TB, from the month's rates. */
    private double lastTbSick;

    /**
     * Held off: no cause kills, and the babies' and children's swing stays
     * whole - the deaths of today, as before R0b. Only for a harness fixture
     * whose condition was sized on those deaths and that a 1900 city's do not
     * give (R0b call 7, R0's call 18's class); the game never sets it, and it
     * is not saved, so a fixture that loads a held-off city into a NEW Game
     * holds it off again. On the same Game it outlives a load (R0g: this
     * object is a final field of the Game, and the Game gives a load's new
     * Health the influenza's hold again). Game.holdCausesOfDeathOff() also
     * holds off the influenza's sick.
     */
    private boolean heldOff;

    public void holdOff() { heldOff = true; }

    public boolean isHeldOff() { return heldOff; }

    /* ---------------------------- the month ---------------------------- */

    /**
     * Each cause's MONTHLY chance of death in each band this month,
     * [cause][band]: a rate to hand PopulationCohorts as a competing risk.
     * Every cell is finite and not below 0. Anything wrong with the arguments
     * (a null, a length that is not the bands') gives zeros.
     *
     * A band the swing does not set gets the cause at its 1900 rate times
     * history, the city's conditions and the season. A swung band gets the
     * cause's share of the swing's excess this month (its factor above 1 times
     * today's rate), the same other terms. Childbirth is last month's births
     * times the mothers' rate, over the adults; the influenza is its month's
     * dead in every band.
     *
     * @param people            the people in each band, in band order
     * @param mortalityFactor   Healthcare.mortalityFactors(): the swing this month
     * @param pipedWater        the share of the people with piped water
     * @param hungry            the share who go short of food
     * @param generalCoverage   general care's coverage
     * @param outbreakSeverity  Health's outbreakSeverity this month
     * @param lastBirths        the births last month, a count
     */
    public double[][] rates(int month, double[] people, double[] mortalityFactor,
                            double pipedWater, double hungry, double generalCoverage,
                            double outbreakSeverity, double lastBirths) {
        double[][] out = new double[CAUSES.length][BANDS.length];
        lastTbSick = 0;
        if (heldOff) return out;
        if (people == null || people.length != BANDS.length
                || mortalityFactor == null || mortalityFactor.length != BANDS.length) return out;

        double piped = clamp01(pipedWater);
        double severity = Double.isFinite(outbreakSeverity) ? Math.max(0, outbreakSeverity) : 0;
        double births = Double.isFinite(lastBirths) ? Math.max(0, lastBirths) : 0;
        double adults = people[AgeBand.ADULT.ordinal()];

        for (Cause c : CAUSES) {
            int ci = c.ordinal();
            double history = history(c, month);
            if (c == Cause.TYPHOID || c == Cause.DIARRHOEA) history = piped * history + (1 - piped);
            double season = season(c, month);
            double ratio = c == Cause.OUTBREAK ? severity / OUTBREAK_MEAN_SEVERITY : 1;
            for (AgeBand b : BANDS) {
                int bi = b.ordinal();
                double v;
                if (c == Cause.CHILDBIRTH) {
                    v = b == AgeBand.ADULT && adults > 0 && Double.isFinite(adults)
                            ? births * MOTHERS_1900 / 1000 * history
                                    * conditions(c, b, hungry, generalCoverage) / adults
                            : 0;
                } else if (c == Cause.INFLUENZA) {
                    v = INFLUENZA_DEATHS * influenzaShare(month) * DEATH_SHARE[ci][bi] / PEOPLE_1901[bi];
                } else {
                    double annual;
                    if (isSwung(b)) {
                        double f = Double.isFinite(mortalityFactor[bi]) ? mortalityFactor[bi] : 1;
                        annual = SPLIT[ci][bi] * b.getAnnualMortality() * Math.max(0, f - 1);
                    } else {
                        annual = rate1900(c, b);
                    }
                    v = annual * history * conditions(c, b, hungry, generalCoverage)
                            * season * ratio / 12;
                }
                out[ci][bi] = Double.isFinite(v) ? Math.max(0, v) : 0;
            }
        }

        /* the TB sick: the deaths over the long sick's monthly chance of dying (R0b call 4) */
        for (AgeBand b : BANDS) {
            double chance = Sickness.deathChance(b);
            double n = people[b.ordinal()];
            if (chance > 0 && n > 0 && Double.isFinite(n)) {
                lastTbSick += n * out[Cause.TB.ordinal()][b.ordinal()] / chance;
            }
        }
        return out;
    }

    /**
     * Books the month's dead by cause. Each band's dead from these causes (the
     * cohorts' count, which the monthly cap may have scaled down) are given out
     * by each cause's share of the band's total rate; a band with no rate has
     * nothing to book. The ring slot at month % 12 is overwritten, so twelve
     * months on it holds the latest twelve.
     */
    public void book(int month, double[][] rates, double[] causeDeathsByBand) {
        double[] dead = new double[CAUSES.length];
        boolean shaped = rates != null && rates.length >= CAUSES.length && causeDeathsByBand != null;
        for (int b = 0; shaped && b < BANDS.length && b < causeDeathsByBand.length; b++) {
            double deaths = causeDeathsByBand[b];
            if (!(deaths > 0) || !Double.isFinite(deaths)) continue;
            double total = 0;
            for (int c = 0; c < CAUSES.length; c++) {
                if (rates[c] != null && b < rates[c].length) total += Math.max(0, rates[c][b]);
            }
            if (!(total > 0) || !Double.isFinite(total)) continue;
            for (int c = 0; c < CAUSES.length; c++) {
                if (rates[c] != null && b < rates[c].length) dead[c] += deaths * Math.max(0, rates[c][b]) / total;
            }
        }
        int slot = Math.floorMod(month, MONTHS);
        for (int c = 0; c < CAUSES.length; c++) {
            lastDeaths[c] = dead[c];
            ring[c][slot] = dead[c];
            totals[c] += dead[c];
        }
    }

    /* ---------------------------- reading it ---------------------------- */

    /** The dead of one cause booked last month. */
    public double getLastDeaths(Cause cause) { return cause == null ? 0 : lastDeaths[cause.ordinal()]; }

    /** The dead of every cause booked last month. */
    public double getLastDeaths() {
        double s = 0;
        for (double v : lastDeaths) s += v;
        return s;
    }

    /** The dead of one cause in the last twelve months. */
    public double getYearDeaths(Cause cause) {
        if (cause == null) return 0;
        double s = 0;
        for (double v : ring[cause.ordinal()]) s += v;
        return s;
    }

    /** The dead of one cause since the city began. */
    public double getTotalDeaths(Cause cause) { return cause == null ? 0 : totals[cause.ordinal()]; }

    /** The people sick of TB for a year or more, as of the last rates() call. */
    public double getTbSick() { return lastTbSick; }

    /* ------------------------------ saving ------------------------------ */

    /** The state, in order: last month's dead, the totals, the ring (cause by cause), the TB sick. */
    public double[] getState() {
        int n = CAUSES.length;
        double[] out = new double[n + n + n * MONTHS + 1];
        int i = 0;
        for (double v : lastDeaths) out[i++] = v;
        for (double v : totals) out[i++] = v;
        for (double[] slot : ring) for (double v : slot) out[i++] = v;
        out[i] = lastTbSick;
        return out;
    }

    /**
     * Puts a saved state back. Refused whole, leaving this as it was, on a null
     * or a length that is not getState()'s: a half-restored ring would give a
     * twelve-month figure that matches no month.
     */
    public boolean restore(double[] saved) {
        int n = CAUSES.length;
        if (saved == null || saved.length != n + n + n * MONTHS + 1) return false;
        int i = 0;
        for (int c = 0; c < n; c++) lastDeaths[c] = clean(saved[i++]);
        for (int c = 0; c < n; c++) totals[c] = clean(saved[i++]);
        for (int c = 0; c < n; c++) for (int k = 0; k < MONTHS; k++) ring[c][k] = clean(saved[i++]);
        lastTbSick = clean(saved[i]);
        return true;
    }

    public void reset() {
        java.util.Arrays.fill(lastDeaths, 0);
        java.util.Arrays.fill(totals, 0);
        for (double[] slot : ring) java.util.Arrays.fill(slot, 0);
        lastTbSick = 0;
    }

    private static double clean(double v) {
        return Double.isFinite(v) ? Math.max(0, v) : 0;
    }

    private static double clamp01(double v) {
        return Double.isNaN(v) ? 0 : (v < 0 ? 0 : (v > 1 ? 1 : v));
    }
}
