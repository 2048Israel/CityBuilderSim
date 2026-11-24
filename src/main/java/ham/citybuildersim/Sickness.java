package ham.citybuildersim;

/**
 * Who has been sick, and for how long - and the ones it kills.
 *
 * Jerus, 2026-09-11: people who stay sick should start dying. Health sets how
 * many people are sick this month, as it always has;
 * this class remembers how long they have been, with a ring of monthly
 * cohorts the way Unemployment remembers who is on EI, and whoever has been
 * sick for more than two months dies at their age's monthly chance.
 *
 * ONE RING PER BAND, AND IT HOLDS SHARES. Each slot is a share of the band -
 * sick for under a month, one to two months, ... eleven to twelve, and a year
 * or more - not a count of people. The pyramid ages, gives birth, dies and
 * migrates in proportion, so a share carried from one month to the next is
 * still the same share of the same band, and no sick person ever has to be
 * walked from one band to the next. Nobody is tracked; the ring only knows
 * how the band's sick are spread over time.
 *
 * ACUTE INFLUENZA STAYS BESIDE THE RING (R0h). Its cases end within weeks and
 * CausesOfDeath books its dead. Putting them in the ring killed them again
 * three months later. The acute share joins the visible sick and the youngest
 * bar, while only the rate without influenza enters the mortality ring.
 *
 * THE RATE DECIDES HOW MANY, THE RING HOW LONG. Each month the ring turns - the
 * dead come out, the month's recovery gets better, everyone else moves a slot
 * along - and then it meets the band's share: the shortfall fell ill this month
 * and enters the first slot; a surplus means the sickness is lifting, and every
 * slot is scaled down alike. The same shape as Unemployment.reconcile(): the
 * pool is the labour market's, the ring only says who is in it.
 *
 * WHAT CLINICS DO NOW. General care still sets the city's rate (Health), and
 * now it also sets how fast the sick get better - half of them a month with
 * none, nine in ten with everybody covered - and that is the only way care
 * keeps a teenager or an adult alive: its old three-fold swing on their death
 * rate is gone (Healthcare.mortalityFactor). Jerus: sickness replaces it.
 * Childcare and senior care keep their swings, and also take away the extra
 * sickness the youngest and the oldest carry.
 */
public class Sickness {

    /* ===================================================================
       THE NUMBERS - every one of them Jerus's, from a table of what each
       would do. See claude/the-long-sick.md.
       =================================================================== */

    /** Slots in each band's ring: 0 is under a month, 12 is a year or more. */
    public static final int RING = 13;

    /**
     * The first slot that can die: sick for more than two months. The long-term
     * sickness absence studies draw the line at eight weeks - a Norwegian cohort
     * of those absences died at 1.5 (women) to 2.0 (men) times the expected rate
     * over nine years (European Journal of Public Health, 2008).
     */
    public static final int DEADLY_FROM = 2;

    /**
     * How much sicker babies and seniors are than the city, with none of their
     * own care: twice. The US Health Interview Survey puts under-fives at 318
     * acute illnesses per 100 people a year against 144 at 25-44 (1996), and
     * the over-65s at 36.5 restricted-activity days a year against 15.8 (1977).
     * Their care takes the extra away: at full coverage they are no sicker than
     * anybody else. Jerus: "childcare and senior care also target those".
     */
    public static final double EXTRA_SICKNESS = 1.0;

    /** The share of the sick who get better in a month, with no general care... */
    public static final double RECOVERY_UNTREATED = .5;

    /** ...and with general care for everybody. Jerus: "general care cures everyone". */
    public static final double RECOVERY_SERVED = .9;

    /** No band has more of itself sick than the city rate may. */
    public static final double MAX_SHARE = Health.MAX_SICK_RATE;

    /**
     * The monthly chance of dying once sick for more than two months, by age:
     * babies 4%, children and teenagers 1%, adults 2%, seniors 4%, elders 8%.
     *
     * THE ELDERS DIE MORE OF IT, Jerus's call with the band. Being ill for
     * three months at ninety is not the same event as being ill for three
     * months at seventy-two, and the band exists precisely so the model can
     * say so; doubling the senior figure is the same relationship the two
     * bands' own mortality has (12.77% against 2.90% is four and a half times,
     * so twice is if anything conservative).
     *
     * THE DEFAULT USED TO SWALLOW THIS. A `default: return 0` meant the elder
     * band, on the day it was added, would have been the one band in the city
     * that could not die of illness - the frailest people in it, immortal to
     * the thing that kills the frail, with nothing to say so. The switch is
     * exhaustive now and the default is gone.
     */
    public static double deathChance(AgeBand band) {
        if (band == null) return 0;
        switch (band) {
            case BABY:   return .04;
            case CHILD:  return .01;
            case TEEN:   return .01;
            case ADULT:  return .02;
            case SENIOR: return .04;
            case ELDER:  return .08;
        }
        return 0;
    }

    /** How much of a band is sick at a city rate, given the care that serves it. */
    public static double shareFor(AgeBand band, double cityRate,
                                  double childcareCoverage, double seniorCoverage) {
        double rate = Math.max(0, cityRate);
        double extra = 0;
        if (band == AgeBand.BABY)      extra = EXTRA_SICKNESS * (1 - clamp(childcareCoverage));
        if (band.isRetirementAge())    extra = EXTRA_SICKNESS * (1 - clamp(seniorCoverage));
        return Math.min(MAX_SHARE, rate * (1 + extra));
    }

    /** The share of the sick who get better this month. */
    public static double recovery(double generalCoverage) {
        return RECOVERY_UNTREATED + (RECOVERY_SERVED - RECOVERY_UNTREATED) * clamp(generalCoverage);
    }

    /* ===================================================================
       THE MEDICAL ROWS (R2f) - what a 1900 city's sick lack until each is known
       =================================================================== */

    /*
     * Research-design Q17: each medical row moves the constants of a mechanism
     * the game has. The figures above are today's, so a row's constant is
     * today's from the month the row is known (history's month, or the month
     * the city found it) and its value before the row until then; only a city
     * founded the 1900 way can be before one (MedicalRows.TODAY knows them all,
     * and every reading below is then the arithmetic of before R2f, to the
     * bit). See docs/queue/R2f-notes.md for each figure's call.
     */

    /** Before the TB drugs (M1) the sick of a year or more get better at this share of the month's recovery: Canadian TB stays fell from 362 to 224 days with them [M-S1]. */
    public static final double LONG_RECOVERY_BEFORE_M1 = 224 / 362.0;

    /**
     * Before the breathing machine (M2) the long sick general care serves die
     * this much more often: it took respiratory polio's deaths from 87% to 22%
     * [M-M5], on the respiratory share of deaths, pneumonia's 55 of about 900
     * per 100,000 in Canada, 1946-50 (est.) [SC-B35].
     */
    public static final double SERVED_DEATHS_BEFORE_M2 = 55 / 900.0 * (87 / 22.0 - 1);

    /** General care's part of the recovery before penicillin (PM5) is this share of its part after: Canada's general-hospital stay fell from 11.0 to 10.0 days, 1945-50 (observed, not proven causal) [SC-B285]. */
    public static final double STAYS_BEFORE_PM5 = 10 / 11.0;

    /*
     * A STAYS ROW WITH ITS OWN MEASURE (R6f, M12; MED3's M14 takes the same
     * rule): until the row is known a stay is longer by the row's saving an
     * operation times the share of stays it reaches, so general care's part
     * of the recovery is the mean stay over that longer stay. The four later
     * rows below split evenly only what such rows leave of the fall from
     * 1950 to today, so 1950's 10.0 days stay 10.0 [SC-B285].
     */

    /** Robot-assisted stays' share of all stays: 276,325 of the US's 35,527,500 inpatient stays in 2018 [R6f-S1], [R6f-S2]; Canada's share was not found. */
    public static final double ROBOTIC_SHARE_OF_STAYS = 276_325 / 35_527_500.0;

    /** The days a robot-assisted operation takes off a stay against keyhole surgery (M9, the way before it): 0.23 in a 2024 meta-analysis of randomised trials [G-M39]; its 1.69 against open surgery is M9's own saving and more. */
    public static final double ROBOT_DAYS_SAVED = 0.23;

    /** The US mean inpatient stay of 2018 those stays are of, in days: 27.0% of stays with an operating-room procedure at 5.1 days, the rest at 4.5 [R6f-S2]. */
    public static final double US_MEAN_STAY_2018 = .270 * 5.1 + .730 * 4.5;

    /** General care's part of the recovery before robot-assisted surgery (M12) is this share of its part after: the mean stay over the mean stay with the robot's saving back on its share of the stays (est.). */
    public static final double STAYS_BEFORE_M12 = US_MEAN_STAY_2018 / (US_MEAN_STAY_2018 + ROBOTIC_SHARE_OF_STAYS * ROBOT_DAYS_SAVED);

    /**
     * ...and before each of the four later rows that shorten a stay (M5, M6,
     * M9, M10), this share: they split evenly what the rows with their own
     * measure (M12) leave of the fall from 1950's 10.0 days to 2022-23's 7.3
     * [SC-B285], [CIHI-LOS] (est.).
     */
    public static final double STAYS_BEFORE_LATER_ROW = Math.pow(7.3 / 10.0 / STAYS_BEFORE_M12, 1 / 4.0);

    /** The later rows that shorten general care's stays, each taking STAYS_BEFORE_LATER_ROW off until it is known. */
    public static final java.util.List<String> LATER_STAYS_ROWS = java.util.List.of("M5", "M6", "M9", "M10");

    /** Every row that shortens general care's stays, in date order: PM5, the later four, then the rows with their own measure (M12). */
    public static final java.util.List<String> STAYS_ROWS = java.util.List.of("PM5", "M5", "M6", "M9", "M10", "M12");

    /** The share of today's part a stays row leaves until it is known: STAYS_BEFORE_PM5, STAYS_BEFORE_LATER_ROW or STAYS_BEFORE_M12; 1 for a code that is no stays row. */
    public static double staysBefore(String code) {
        if ("PM5".equals(code)) return STAYS_BEFORE_PM5;
        if ("M12".equals(code)) return STAYS_BEFORE_M12;
        return LATER_STAYS_ROWS.contains(code) ? STAYS_BEFORE_LATER_ROW : 1;
    }

    /** General care's part of the recovery in a month, as a share of today's: 1 once every stays row is known. */
    public static double staysFactor(MedicalRows rows, int month) {
        if (rows == null || rows.isToday()) return 1;
        double f = 1;
        for (String code : STAYS_ROWS) if (!rows.known(code, month)) f *= staysBefore(code);
        return f;
    }

    /** The year-or-more slot's share of the month's recovery: 1 once M1 is known. */
    public static double longRecoveryFactor(MedicalRows rows, int month) {
        return rows == null || rows.known("M1", month) ? 1 : LONG_RECOVERY_BEFORE_M1;
    }

    /** How much more often the served long sick die, per unit of general coverage: 0 once M2 is known. */
    public static double servedDeathsBefore(MedicalRows rows, int month) {
        return rows == null || rows.known("M2", month) ? 0 : SERVED_DEATHS_BEFORE_M2;
    }

    /** The share of the sick who get better this month, general care's part times the stays factor: recovery(generalCoverage) itself where it is 1. */
    public static double recovery(double generalCoverage, double staysFactor) {
        if (staysFactor == 1) return recovery(generalCoverage);
        return RECOVERY_UNTREATED + (RECOVERY_SERVED - RECOVERY_UNTREATED) * staysFactor * clamp(generalCoverage);
    }

    /* ------------------------------ state ------------------------------ */

    private final double[][] ring = new double[AgeBand.values().length][RING];
    /** This month's acute influenza shares: visible, but never aged into the mortality ring (R0h). */
    private final double[] acute = new double[AgeBand.values().length];
    private boolean seeded;
    private double lastRecovery = RECOVERY_SERVED;

    /*
     * This month's medical rows (R2f), set by Game before the month reads the
     * ring and derived from the city's research, so never saved: general
     * care's part of the recovery, the year-or-more slot's share of it, and
     * how much more often the long sick die (the served share's lift before
     * M2). Today's (1, 1, 0) until set, and in every city that knows the rows.
     */
    private double staysFactor = 1;
    private double longRecovery = 1;
    private double deathLift = 0;

    /**
     * The month's medical rows (R2f): what the city knows this month, and
     * general care's coverage, the share of the long sick the breathing
     * machine would serve. Game calls it in the demographics, before
     * deathRates() and the month's advanceMonth().
     */
    public void setMedicine(MedicalRows rows, int month, double generalCoverage) {
        staysFactor = staysFactor(rows, month);
        longRecovery = longRecoveryFactor(rows, month);
        deathLift = servedDeathsBefore(rows, month) * clamp(generalCoverage);
    }

    /** General care's part of the recovery this month, as a share of today's (R2f). */
    public double getStaysFactor()   { return staysFactor; }
    /** The year-or-more slot's share of the month's recovery (R2f). */
    public double getLongRecovery()  { return longRecovery; }
    /** How much more often the long sick die this month than deathChance() says (R2f). */
    public double getDeathLift()     { return deathLift; }

    /** The share of the sick who get better this month at a coverage, with the month's stays factor: what a notice or a screen quotes (R2f). */
    public double recoveryAt(double generalCoverage) { return recovery(generalCoverage, staysFactor); }

    /** The share of the sick who get better this month with everybody served: RECOVERY_SERVED once the stays rows are known (R2f). */
    public double servedRecovery()   { return recoveryAt(1); }

    /** A band's monthly chance of dying once sick past two months, this month: deathChance() raised by the lift before M2. */
    public double chanceThisMonth(AgeBand band) {
        return deathLift == 0 ? deathChance(band) : deathChance(band) * (1 + deathLift);
    }

    /** The dead the pyramid booked to sickness last month, by band - set by Game. */
    private final double[] lastDeaths = new double[AgeBand.values().length];

    /* ---------------------------- the month ---------------------------- */

    /**
     * Each band's extra monthly chance of dying, from the ring as it stands: the
     * share sick past two months times their age's chance. Read at the top of
     * the demographics, before the ring turns, and handed to
     * PopulationCohorts.advanceMonth() beside the base rate.
     */
    public double[] deathRates() {
        double[] out = new double[AgeBand.values().length];
        for (AgeBand b : AgeBand.values()) {
            out[b.ordinal()] = pastTwoMonths(b) * chanceThisMonth(b);
        }
        return out;
    }

    /**
     * Turns every band's ring a month and meets this month's rate.
     *
     * @param cityRate          Health's sick rate this month
     * @param generalCoverage   general care, which sets the recovery
     * @param childcareCoverage childcare, which takes away the babies' extra
     * @param seniorCoverage    senior care, which takes away the seniors' extra
     */
    public void advanceMonth(double cityRate, double generalCoverage,
                             double childcareCoverage, double seniorCoverage) {
        advanceMonth(cityRate, cityRate, generalCoverage, childcareCoverage, seniorCoverage);
    }

    /** The full rate remains visible; only the rate without influenza enters the ring. */
    public void advanceMonth(double cityRate, double withoutInfluenza, double generalCoverage,
                             double childcareCoverage, double seniorCoverage) {
        if (!seeded) {
            seed(cityRate, withoutInfluenza, generalCoverage, childcareCoverage, seniorCoverage);
            return;
        }
        double r = recovery(generalCoverage, staysFactor);
        double rLast = longRecovery == 1 ? r : r * longRecovery;       // the year or more, before M1 (R2f)
        lastRecovery = r;
        for (AgeBand b : AgeBand.values()) {
            double[] slot = ring[b.ordinal()];
            double h = chanceThisMonth(b);
            double[] next = new double[RING];
            for (int k = 0; k < RING; k++) {
                double left = slot[k] * (k >= DEADLY_FROM ? 1 - h : 1);   // the dead come out
                double got = k == RING - 1 ? rLast : r;
                next[Math.min(k + 1, RING - 1)] += left * (1 - got);        // the rest are a month longer
            }
            double still = 0;
            for (double v : next) still += v;
            double share = shareFor(b, withoutInfluenza, childcareCoverage, seniorCoverage);
            acute[b.ordinal()] = Math.max(0, shareFor(b, cityRate, childcareCoverage, seniorCoverage) - share);
            if (still > share) {
                // The sickness is lifting: everyone's odds of being among the
                // better alike, whatever slot they are in.
                double keep = still > 0 ? share / still : 0;
                for (int k = 0; k < RING; k++) next[k] *= keep;
            } else {
                next[0] += share - still;                                   // fell ill this month
            }
            System.arraycopy(next, 0, slot, 0, RING);
        }
    }

    /**
     * Puts every band's ring at the steady state for a rate: a city that has
     * been this sick, and this well served, for ever. Used on the first month
     * of a new city and the first month after a save that has no ring - an
     * empty ring would let nobody die of sickness for two months.
     *
     * The closed form of advanceMonth() held still: N fall ill, N(1-r) are in
     * their second month, N(1-r)^2 their third, and each month after keeps
     * (1-h)(1-r) of the one before; the last slot sums the tail.
     */
    public void seed(double cityRate, double generalCoverage,
                     double childcareCoverage, double seniorCoverage) {
        seed(cityRate, cityRate, generalCoverage, childcareCoverage, seniorCoverage);
    }

    /** Seeds the chronic steady state, carrying the month's acute shares beside it. */
    public void seed(double cityRate, double withoutInfluenza, double generalCoverage,
                     double childcareCoverage, double seniorCoverage) {
        double r = recovery(generalCoverage, staysFactor);
        double rLast = longRecovery == 1 ? r : r * longRecovery;       // the year or more, before M1 (R2f)
        lastRecovery = r;
        for (AgeBand b : AgeBand.values()) {
            double[] slot = ring[b.ordinal()];
            double share = shareFor(b, withoutInfluenza, childcareCoverage, seniorCoverage);
            acute[b.ordinal()] = Math.max(0, shareFor(b, cityRate, childcareCoverage, seniorCoverage) - share);
            double h = chanceThisMonth(b);
            double q = (1 - h) * (1 - r);
            if (rLast == r) {
                double total = 1 + (1 - r) + (1 - r) * (1 - r) / (1 - q);
                double n = share / total;
                slot[0] = n;
                slot[1] = n * (1 - r);
                double c = n * (1 - r) * (1 - r);
                for (int k = DEADLY_FROM; k < RING - 1; k++) {
                    slot[k] = c;
                    c *= q;
                }
                slot[RING - 1] = c / (1 - q);
            } else {
                // Before M1 the last slot keeps (1-h)(1-rLast) of itself, not q:
                // its tail has its own sum, and the total is added slot by slot.
                double[] unit = new double[RING];
                unit[0] = 1;
                unit[1] = 1 - r;
                double c = (1 - r) * (1 - r);
                for (int k = DEADLY_FROM; k < RING - 1; k++) {
                    unit[k] = c;
                    c *= q;
                }
                unit[RING - 1] = c / (1 - (1 - h) * (1 - rLast));
                double total = 0;
                for (double v : unit) total += v;
                double n = share / total;
                for (int k = 0; k < RING; k++) slot[k] = n * unit[k];
            }
        }
        seeded = true;
    }

    /* ---------------------------- reading it ---------------------------- */

    /** The share of a band that is sick, including this month's acute influenza. */
    public double share(AgeBand b) {
        double s = acute[b.ordinal()];
        for (double v : ring[b.ordinal()]) s += v;
        return s;
    }

    /** The share of a band sick for more than two months - the ones who can die of it. */
    public double pastTwoMonths(AgeBand b) {
        double s = 0;
        double[] slot = ring[b.ordinal()];
        for (int k = DEADLY_FROM; k < RING; k++) s += slot[k];
        return s;
    }

    /** The share of a band sick for a year or more. */
    public double aYearOrMore(AgeBand b) { return ring[b.ordinal()][RING - 1]; }

    /** One slot of one band's ring, 0 to RING - 1. */
    public double slot(AgeBand b, int k) {
        return k < 0 || k >= RING ? 0 : ring[b.ordinal()][k];
    }

    public boolean isSeeded()        { return seeded; }
    public double getLastRecovery()  { return lastRecovery; }

    /** People sick past two months, across the city. */
    public double peoplePastTwoMonths(PopulationCohorts cohorts) {
        double people = 0;
        if (cohorts == null) return 0;
        for (AgeBand b : AgeBand.values()) people += pastTwoMonths(b) * cohorts.get(b);
        return people;
    }

    /**
     * People sick k months, across the city (0.7.28): one slot of every
     * band's ring in people, with acute cases in slot 0 - the Services screen's
     * long-sick bars. peoplePastTwoMonths() is the slots from DEADLY_FROM on.
     */
    public double peopleInSlot(PopulationCohorts cohorts, int k) {
        double people = 0;
        if (cohorts == null) return 0;
        for (AgeBand b : AgeBand.values()) people += (slot(b, k) + (k == 0 ? acute[b.ordinal()] : 0)) * cohorts.get(b);
        return people;
    }

    /** People sick this month, across the city. */
    public double peopleSick(PopulationCohorts cohorts) {
        double people = 0;
        if (cohorts == null) return 0;
        for (AgeBand b : AgeBand.values()) people += share(b) * cohorts.get(b);
        return people;
    }

    /** Recorded by Game from what the pyramid actually booked. */
    public void setLastDeaths(double[] byBand) {
        for (int i = 0; i < lastDeaths.length; i++) {
            lastDeaths[i] = byBand != null && i < byBand.length ? Math.max(0, byBand[i]) : 0;
        }
    }

    public double getLastDeaths(AgeBand b) { return lastDeaths[b.ordinal()]; }

    public double getLastDeaths() {
        double s = 0;
        for (double v : lastDeaths) s += v;
        return s;
    }

    /* ------------------------------ saving ------------------------------ */

    /** A copy of this month's acute shares, saved separately from the chronic ring. */
    public double[] getAcuteShares() { return acute.clone(); }

    /** Missing on an older save means none; names follow the same saved bands as the ring. */
    public void restoreAcute(String[] bands, double[] saved) {
        java.util.Arrays.fill(acute, 0);
        String[] names = bands == null || bands.length == 0 ? PopulationCohorts.LEGACY_BANDS : bands;
        if (saved == null || saved.length != names.length) return;
        for (int i = 0; i < names.length; i++) {
            AgeBand target = bandNamed(names[i]);
            if (target != null) acute[target.ordinal()] = Math.max(0, Math.min(MAX_SHARE, saved[i]));
        }
    }

    /** Every ring, the month's dead by band, the recovery, and whether it has been seeded. */
    public double[] getState() {
        int bands = AgeBand.values().length;
        double[] out = new double[bands * RING + bands + 2];
        int i = 0;
        for (double[] slot : ring) for (double v : slot) out[i++] = v;
        for (double v : lastDeaths) out[i++] = v;
        out[i++] = lastRecovery;
        out[i] = seeded ? 1 : 0;
        return out;
    }

    /** A ring saved before the band names travelled with it. Read as five bands. */
    public boolean restore(double[] saved) {
        return restore(null, saved);
    }

    /**
     * Refused whole on a length mismatch. A save from before the ring existed
     * has none, and the first month back seeds it - see seed().
     *
     * THE WIDTH COMES FROM THE SAVE, NOT FROM THIS BUILD (2026-09-15). It used
     * to be AgeBand.values().length, so the day a band is added every ring on
     * disk is the wrong length, refused, and silently reseeded - a city that
     * quietly forgets who has been ill for eleven months. Each band is found
     * by name; a band the save does not carry starts empty, which is the right
     * answer for a band that did not exist when it was written.
     *
     * @param bands the names the save was written with, or null for LEGACY_BANDS
     */
    public boolean restore(String[] bands, double[] saved) {
        reset();
        String[] names = (bands == null || bands.length == 0)
                ? PopulationCohorts.LEGACY_BANDS : bands;
        int saveBands = names.length;
        if (saved == null || saved.length != saveBands * RING + saveBands + 2) return false;
        int i = 0;
        for (int b = 0; b < saveBands; b++) {
            AgeBand target = bandNamed(names[b]);
            for (int k = 0; k < RING; k++) {
                double v = Math.max(0, saved[i++]);
                if (target != null) ring[target.ordinal()][k] = v;
            }
        }
        for (int b = 0; b < saveBands; b++) {
            AgeBand target = bandNamed(names[b]);
            double v = Math.max(0, saved[i++]);
            if (target != null) lastDeaths[target.ordinal()] = v;
        }
        lastRecovery = saved[i++];
        seeded = saved[i] > .5;
        return true;
    }

    /** The band of that name, or null if this build has no such band. */
    private static AgeBand bandNamed(String name) {
        if (name == null) return null;
        for (AgeBand b : AgeBand.values()) if (b.name().equals(name)) return b;
        return null;
    }

    public void reset() {
        for (double[] slot : ring) java.util.Arrays.fill(slot, 0);
        java.util.Arrays.fill(lastDeaths, 0);
        java.util.Arrays.fill(acute, 0);
        lastRecovery = RECOVERY_SERVED;
        seeded = false;
    }

    private static double clamp(double v) {
        return v < 0 ? 0 : (v > 1 ? 1 : v);
    }
}
