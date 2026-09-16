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

    /* ------------------------------ state ------------------------------ */

    private final double[][] ring = new double[AgeBand.values().length][RING];
    private boolean seeded;
    private double lastRecovery = RECOVERY_SERVED;

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
            out[b.ordinal()] = pastTwoMonths(b) * deathChance(b);
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
        if (!seeded) {
            seed(cityRate, generalCoverage, childcareCoverage, seniorCoverage);
            return;
        }
        double r = recovery(generalCoverage);
        lastRecovery = r;
        for (AgeBand b : AgeBand.values()) {
            double[] slot = ring[b.ordinal()];
            double h = deathChance(b);
            double[] next = new double[RING];
            for (int k = 0; k < RING; k++) {
                double left = slot[k] * (k >= DEADLY_FROM ? 1 - h : 1);   // the dead come out
                next[Math.min(k + 1, RING - 1)] += left * (1 - r);          // the rest are a month longer
            }
            double still = 0;
            for (double v : next) still += v;
            double share = shareFor(b, cityRate, childcareCoverage, seniorCoverage);
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
        double r = recovery(generalCoverage);
        lastRecovery = r;
        for (AgeBand b : AgeBand.values()) {
            double[] slot = ring[b.ordinal()];
            double share = shareFor(b, cityRate, childcareCoverage, seniorCoverage);
            double q = (1 - deathChance(b)) * (1 - r);
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
        }
        seeded = true;
    }

    /* ---------------------------- reading it ---------------------------- */

    /** The share of a band that is sick. */
    public double share(AgeBand b) {
        double s = 0;
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
        lastRecovery = RECOVERY_SERVED;
        seeded = false;
    }

    private static double clamp(double v) {
        return v < 0 ? 0 : (v > 1 ? 1 : v);
    }
}
