package ham.citybuildersim;

import ham.citybuildersim.CausesOfDeath.Cause;

import java.util.Arrays;

/**
 * Proves the causes of death of 1900 (R0b, slice 1): the model on its own,
 * before the month hands its rates to the cohorts. Every claim below sets its
 * own cause and is asserted against the model's constants and methods.
 *
 *   1. the constants hold together (shares, seasons, the influenza's months, history's ends)
 *   2. a 1900 city dies of each cause at its 1900 rate
 *   3. the split leaves a 1900 baby's death rate where it was
 *   4. seasons and outbreaks (and Health's own mean severity, measured)
 *   5. history on its dates
 *   6. the city's conditions raise a cause, never lower it
 *   7. the 1918 influenza
 *   8. booking the dead, the TB sick and the save
 *
 * The 1900 city of sections 2, 4, 5 and 6 is Canada's 1901 pyramid at 100,000
 * people with no childcare (the swing at its top), general care for everybody,
 * piped water, nobody hungry, and the mean outbreak running.
 */
public class CausesOfDeathCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        System.out.printf("%-78s %12.6f  expected %12.6f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    /** Within a relative tolerance of the expected value. */
    static void checkRel(String label, double actual, double expected, double rel) {
        check(label, actual, expected, rel * Math.abs(expected));
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-78s %s%n", label, ok ? "OK" : "FAIL");
    }

    static final Cause[] CAUSES = Cause.values();
    static final AgeBand[] BANDS = AgeBand.values();
    static final int BABY = AgeBand.BABY.ordinal();
    static final int CHILD = AgeBand.CHILD.ordinal();
    static final int TEEN = AgeBand.TEEN.ordinal();
    static final int ADULT = AgeBand.ADULT.ordinal();

    /** The people in the 1900 city. */
    static final double CITY = 100_000;

    /** The game month of a calendar month. */
    static int month(int year, int monthOfYear) {
        return (year - CityCalendar.EPOCH_YEAR) * 12 + monthOfYear;
    }

    static double sum(double[] v) {
        double s = 0;
        for (double x : v) s += x;
        return s;
    }

    static double[] people() {
        double[] p = new double[BANDS.length];
        for (int b = 0; b < p.length; b++) p[b] = CausesOfDeath.PEOPLE_1901[b] * CITY;
        return p;
    }

    /** Thirty births per 1,000 people a year, a month's worth. */
    static double births() {
        return CITY * 30 / 1000 / 12;
    }

    /** Healthcare's factors with no childcare and everybody covered by general and senior care. */
    static double[] topOfSwing() {
        return Healthcare.mortalityFactors(0, 1, 1);
    }

    /** The factors with the swung bands set to f. */
    static double[] swungAt(double f) {
        double[] factor = topOfSwing();
        for (AgeBand b : BANDS) if (CausesOfDeath.isSwung(b)) factor[b.ordinal()] = f;
        return factor;
    }

    /** The 1900 city's month, with its conditions as given. */
    static double[][] city(CausesOfDeath m, int month, double piped, double hungry,
                           double general, double severity, double[] factor) {
        return m.rates(month, people(), factor, piped, hungry, general, severity, births());
    }

    /** The 1900 city's month at the mean outbreak, in the best of conditions. */
    static double[][] city(CausesOfDeath m, int month) {
        return city(m, month, 1, 0, 1, CausesOfDeath.OUTBREAK_MEAN_SEVERITY, topOfSwing());
    }

    /** Each cause's dead in the month, for the city's people. */
    static double[] dead(double[][] rates) {
        double[] p = people();
        double[] out = new double[CAUSES.length];
        for (int c = 0; c < out.length; c++) {
            for (int b = 0; b < BANDS.length; b++) out[c] += rates[c][b] * p[b];
        }
        return out;
    }

    /** Each cause's dead in a calendar year, per 100,000 people. */
    static double[] year(CausesOfDeath m, int year) {
        double[] out = new double[CAUSES.length];
        for (int k = 1; k <= 12; k++) {
            double[] d = dead(city(m, month(year, k)));
            for (int c = 0; c < out.length; c++) out[c] += d[c] * 100_000 / CITY;
        }
        return out;
    }

    /** One cause's yearly dead per 100,000, averaged over the years from..to inclusive. */
    static double average(CausesOfDeath m, Cause c, int from, int to) {
        double s = 0;
        for (int y = from; y <= to; y++) s += year(m, y)[c.ordinal()];
        return s / (to - from + 1);
    }

    /** A band's member, in the singular. */
    static String one(AgeBand band) {
        return band == AgeBand.BABY ? "baby" : "child";
    }

    /** The mean of a cause's history over a calendar year's twelve months. */
    static double meanHistory(Cause c, int year) {
        double s = 0;
        for (int k = 1; k <= 12; k++) s += CausesOfDeath.history(c, month(year, k));
        return s / 12;
    }

    public static void main(String[] args) {
        CausesOfDeath model = new CausesOfDeath();
        int lastMonth = month(2100, 12);

        /* ============ 1. the constants hold together ============ */
        System.out.println("--- the constants hold together ---");
        check("Canada's 1901 people by band sum to 1", sum(CausesOfDeath.PEOPLE_1901), 1, 1e-12);
        for (Cause c : CAUSES) {
            check("who dies of " + c.label() + " sums to 1", sum(CausesOfDeath.DEATH_SHARE[c.ordinal()]), 1, 1e-12);
        }
        for (Cause c : CAUSES) {
            double s = 0;
            for (int k = 1; k <= 12; k++) s += CausesOfDeath.season(c, month(1900, k));
            check(c.label() + ": the season averages 1 over a year", s / 12, 1, 1e-12);
        }
        assertTrue("fixture: summer diarrhoea and winter pneumonia do have seasons",
                CausesOfDeath.DIARRHOEA_SUMMER > 1 && CausesOfDeath.PNEUMONIA_WINTER > 1
                        && CausesOfDeath.DIARRHOEA_REST < 1 && CausesOfDeath.PNEUMONIA_REST < 1);
        double flu = 0;
        for (int m = 1; m <= lastMonth; m++) flu += CausesOfDeath.influenzaShare(m);
        check("the influenza's months share all of its dead between them", flu, 1, 1e-12);
        boolean januaryOk = true, endsOk = true, nonNegative = true, notAbove = true;
        for (Cause c : CAUSES) {
            januaryOk &= Math.abs(CausesOfDeath.history(c, 1) - 1) < 1e-12;
            double expected1980 = c == Cause.INFLUENZA ? 1 : 0;
            endsOk &= Math.abs(CausesOfDeath.history(c, month(1980, 1)) - expected1980) < 1e-12;
            for (int m = 1; m <= lastMonth; m++) {
                double h = CausesOfDeath.history(c, m);
                nonNegative &= h >= 0;
                if (c != Cause.CHILDBIRTH) notAbove &= h <= 1 + 1e-12;
            }
        }
        assertTrue("history is 1 in January 1900 for every cause", januaryOk);
        assertTrue("...and 0 in January 1980 for every cause but the influenza, whose months carry it", endsOk);
        assertTrue("...never negative in two hundred years", nonNegative);
        assertTrue("...and never above 1900's value, childbirth aside", notAbove);
        double maxBirth = 0;
        int firstUp = Integer.MAX_VALUE, lastUp = 0;
        for (int m = 1; m <= lastMonth; m++) {
            double h = CausesOfDeath.history(Cause.CHILDBIRTH, m);
            maxBirth = Math.max(maxBirth, h);
            if (h > 1 + 1e-12) { firstUp = Math.min(firstUp, m); lastUp = Math.max(lastUp, m); }
        }
        check("mothers' deaths peak at 1930's 5.8 over 1900's MOTHERS_1900", maxBirth, 5.8 / CausesOfDeath.MOTHERS_1900, 1e-12);
        assertTrue("...and sit above 1900's only between 1921 and 1940",
                firstUp >= month(1921, 1) && lastUp <= month(1940, 1));
        assertTrue("the mean outbreak is a real severity, between Health's floor and its largest peak",
                CausesOfDeath.OUTBREAK_MEAN_SEVERITY > Health.OUTBREAK_FLOOR
                        && CausesOfDeath.OUTBREAK_MEAN_SEVERITY < Health.OUTBREAK_MAX_PEAK);

        /* ============ 2. a 1900 city dies of each cause at its 1900 rate ============ */
        System.out.println("\n--- a 1900 city dies of each cause at 1900's rate ---");
        double[] in1900 = year(model, 1900);
        Cause[] counted = {Cause.TYPHOID, Cause.DIARRHOEA, Cause.OUTBREAK, Cause.TB, Cause.PNEUMONIA};
        for (Cause c : counted) {
            int i = c.ordinal();
            double rate = CausesOfDeath.RATE_1900[i];
            double held = rate * meanHistory(c, 1900);
            System.out.printf("   %-22s %8.2f per 100,000 in 1900; the rate is %.1f, %.1f%% of it%n",
                    c.label(), in1900[i], rate, 100 * in1900[i] / rate);
            checkRel(c.label() + " takes 1900's rate, less history's first year, within 5%",
                    in1900[i], held, .05);
            assertTrue("...and never more than the rate itself", in1900[i] <= rate * (1 + 1e-9));
        }
        double mothers = in1900[Cause.CHILDBIRTH.ordinal()] * (CITY / 100_000) / (births() * 12) * 1000;
        check("mothers die at " + CausesOfDeath.MOTHERS_1900 + " per 1,000 births", mothers,
                CausesOfDeath.MOTHERS_1900, 1e-9 * CausesOfDeath.MOTHERS_1900);
        check("the influenza kills nobody in 1900", in1900[Cause.INFLUENZA.ordinal()], 0, 0);

        /* ============ 3. the split leaves a 1900 baby's rate where it was ============ */
        System.out.println("\n--- the split leaves a 1900 baby's death rate where it was ---");
        double top = Healthcare.CHILDCARE_SWING;
        for (AgeBand b : new AgeBand[]{AgeBand.BABY, AgeBand.CHILD}) {
            double base = b.getAnnualMortality();
            double parts = 0, raw = 0;
            for (Cause c : CAUSES) {
                parts += CausesOfDeath.share(c, b) * base * (top - 1);
                if (c != Cause.CHILDBIRTH && c != Cause.INFLUENZA) raw += CausesOfDeath.rate1900(c, b) / (base * (top - 1));
            }
            check(b.getLabel() + ": the factor kept and the causes' parts make the swing's top again",
                    base * CausesOfDeath.keptFactor(b, top) + parts, base * top, 1e-12);
            System.out.printf("   %s: the causes name %.1f%% of the swing's excess (%.1f%% before the cap)%n",
                    b.getLabel().toLowerCase(), 100 * CausesOfDeath.split(b), 100 * raw);
            if (b == AgeBand.BABY) {
                assertTrue("fixture: babies' shares are under the cap", raw < CausesOfDeath.MAX_SPLIT);
                check("babies' shares are 1900's rates over the excess, uncapped", CausesOfDeath.split(b), raw, 1e-12);
            } else {
                assertTrue("fixture: children's shares come to more than the cap", raw > CausesOfDeath.MAX_SPLIT);
                check("children's shares are scaled to sum to MAX_SPLIT", CausesOfDeath.split(b), CausesOfDeath.MAX_SPLIT, 1e-12);
            }
        }
        check("the children's shares keep their proportions under the cap",
                CausesOfDeath.share(Cause.OUTBREAK, AgeBand.CHILD) / CausesOfDeath.share(Cause.TB, AgeBand.CHILD),
                CausesOfDeath.rate1900(Cause.OUTBREAK, AgeBand.CHILD) / CausesOfDeath.rate1900(Cause.TB, AgeBand.CHILD), 1e-9);
        check("an adult's shares of a swing are nothing", CausesOfDeath.split(AgeBand.ADULT), 0, 0);
        check("childbirth never splits a swing", CausesOfDeath.share(Cause.CHILDBIRTH, AgeBand.BABY)
                + CausesOfDeath.share(Cause.INFLUENZA, AgeBand.BABY), 0, 0);
        check("an adult's factor is untouched by the split", CausesOfDeath.keptFactor(AgeBand.ADULT, 7), 7, 0);

        int july1930 = month(1930, 7), oct1918 = month(1918, 10);
        double[] swing = swungAt(top);
        double[][] withSwing = city(model, july1930, 1, 0, 1, CausesOfDeath.OUTBREAK_MEAN_SEVERITY, swing);
        assertTrue("fixture: at the swing's top a baby dies of causes in July 1930",
                CausesOfDeath.bandTotals(withSwing)[BABY] > 0 && CausesOfDeath.bandTotals(withSwing)[CHILD] > 0);
        for (double f : new double[]{1, .5, 1 / top}) {
            double[] totals = CausesOfDeath.bandTotals(city(model, july1930, 1, 0, 1,
                    CausesOfDeath.OUTBREAK_MEAN_SEVERITY, swungAt(f)));
            assertTrue("at a factor of " + String.format("%.3f", f) + " babies and children die of no cause but the influenza",
                    totals[BABY] == 0 && totals[CHILD] == 0 && totals[ADULT] > 0);
        }
        double[] fluTotals = CausesOfDeath.bandTotals(city(model, oct1918, 1, 0, 1,
                CausesOfDeath.OUTBREAK_MEAN_SEVERITY, swungAt(1)));
        for (AgeBand b : new AgeBand[]{AgeBand.BABY, AgeBand.CHILD}) {
            int i = b.ordinal();
            double flu1918 = CausesOfDeath.INFLUENZA_DEATHS * CausesOfDeath.influenzaShare(oct1918)
                    * CausesOfDeath.DEATH_SHARE[Cause.INFLUENZA.ordinal()][i] / CausesOfDeath.PEOPLE_1901[i];
            checkRel("in October 1918 at a factor of 1 " + b.getLabel().toLowerCase() + " die of the influenza alone",
                    fluTotals[i], flu1918, 1e-9);
        }
        double[] in1980 = CausesOfDeath.bandTotals(city(model, month(1980, 1), 1, 0, 1,
                CausesOfDeath.OUTBREAK_MEAN_SEVERITY, swing));
        for (AgeBand b : new AgeBand[]{AgeBand.BABY, AgeBand.CHILD}) {
            double kept = CausesOfDeath.keptFactor(b, top);
            check("in 1980 " + b.getLabel().toLowerCase() + " die of no named cause", in1980[b.ordinal()], 0, 0);
            check("in 1980 " + b.getLabel().toLowerCase() + " keep the swing less the causes' part",
                    kept, top - CausesOfDeath.split(b) * (top - 1), 1e-12);
            System.out.printf("   in 1980 an unserved %s dies at %.1f times today's rate (the swing alone is %.0f)%n",
                    one(b), kept, top);
        }

        /* ============ 4. seasons and outbreaks ============ */
        System.out.println("\n--- seasons and outbreaks ---");
        double[][] july = city(model, month(1900, 7));
        double[][] february = city(model, month(1900, 2));
        double[][] january = city(model, month(1900, 1));
        int d = Cause.DIARRHOEA.ordinal(), p = Cause.PNEUMONIA.ordinal();
        for (int b : new int[]{BABY, ADULT}) {
            String who = BANDS[b].getLabel().toLowerCase();
            double julySeason = july[d][b] / CausesOfDeath.history(Cause.DIARRHOEA, month(1900, 7));
            double februarySeason = february[d][b] / CausesOfDeath.history(Cause.DIARRHOEA, month(1900, 2));
            check("diarrhoea in " + who + " in July is DIARRHOEA_SUMMER over the rest times February's",
                    julySeason / februarySeason, CausesOfDeath.DIARRHOEA_SUMMER / CausesOfDeath.DIARRHOEA_REST, 1e-9);
            double winter = january[p][b] / CausesOfDeath.history(Cause.PNEUMONIA, month(1900, 1));
            double summer = july[p][b] / CausesOfDeath.history(Cause.PNEUMONIA, month(1900, 7));
            check("pneumonia in " + who + " in January is PNEUMONIA_WINTER over the rest times July's",
                    winter / summer, CausesOfDeath.PNEUMONIA_WINTER / CausesOfDeath.PNEUMONIA_REST, 1e-9);
        }
        check("typhoid has no season", july[Cause.TYPHOID.ordinal()][ADULT] / february[Cause.TYPHOID.ordinal()][ADULT], 1, 1e-12);
        double mean = CausesOfDeath.OUTBREAK_MEAN_SEVERITY;
        int o = Cause.OUTBREAK.ordinal();
        double[][] quiet = city(model, month(1900, 5), 1, 0, 1, 0, topOfSwing());
        double[][] atMean = city(model, month(1900, 5), 1, 0, 1, mean, topOfSwing());
        double[][] doubled = city(model, month(1900, 5), 1, 0, 1, 2 * mean, topOfSwing());
        boolean quietOk = true;
        for (int b = 0; b < BANDS.length; b++) quietOk &= quiet[o][b] == 0;
        assertTrue("fixture: with an outbreak running the babies and children do die of one", atMean[o][BABY] > 0 && atMean[o][CHILD] > 0);
        assertTrue("with no outbreak running nobody dies of one", quietOk);
        for (int b : new int[]{BABY, CHILD, TEEN}) {
            checkRel("outbreak deaths in " + BANDS[b].getLabel().toLowerCase() + " double when the severity does",
                    doubled[o][b], 2 * atMean[o][b], 1e-12);
        }

        Health health = new Health();
        int runMonths = 120_000;
        double severitySum = 0;
        int outbreakMonths = 0;
        for (int m = 1; m <= runMonths; m++) {
            health.advanceMonth(0, 1000, m);
            severitySum += health.getOutbreakSeverity();
            if (health.isOutbreak()) outbreakMonths++;
        }
        double measured = severitySum / runMonths;
        assertTrue("fixture: Health alone had outbreaks in a tenth of its months or fewer, and some",
                outbreakMonths > 0 && outbreakMonths < runMonths / 10);
        checkRel("Health's mean outbreak severity over " + runMonths + " months is OUTBREAK_MEAN_SEVERITY",
                measured, mean, .10);
        System.out.printf("   OUTBREAK_MEAN_SEVERITY %.6f; measured %.6f over %d months (%.1f%% of the months are in an outbreak)%n",
                mean, measured, runMonths, 100.0 * outbreakMonths / runMonths);

        /* ============ 5. history on its dates ============ */
        System.out.println("\n--- history on its dates ---");
        int ty = Cause.TYPHOID.ordinal(), tb = Cause.TB.ordinal(), bi = Cause.CHILDBIRTH.ordinal();
        double[][] jan1900 = city(model, month(1900, 1));
        check("typhoid in 1915 is 1/40.8 of 1900's (chlorination, Toronto 40.8 to 1.0)",
                CausesOfDeath.history(Cause.TYPHOID, month(1915, 1)), 1 / CausesOfDeath.RATE_1900[ty], 1e-12);
        checkRel("...and so is an adult's typhoid rate that January",
                city(model, month(1915, 1))[ty][ADULT] / jan1900[ty][ADULT], 1 / CausesOfDeath.RATE_1900[ty], 1e-9);
        checkRel("TB in 1948 is 37 of 1900's 180", city(model, month(1948, 1))[tb][ADULT] / jan1900[tb][ADULT],
                37 / CausesOfDeath.RATE_1900[tb], 1e-9);
        double[] jan1944 = dead(city(model, month(1944, 1)));
        check("mothers in January 1944 die at 2.8 per 1,000 births (penicillin)",
                jan1944[bi] / births() * 1000, 2.8, 1e-9);
        double[] jan1950 = dead(city(model, month(1950, 1)));
        check("...and in January 1950 at 1.1", jan1950[bi] / births() * 1000, 1.1, 1e-9);
        double[] study = {14, 5, 37, 55};
        Cause[] listed = {Cause.DIARRHOEA, Cause.OUTBREAK, Cause.TB, Cause.PNEUMONIA};
        for (int k = 0; k < listed.length; k++) {
            double got = average(model, listed[k], 1946, 1950);
            System.out.printf("   %-22s %8.2f per 100,000 a year over 1946-50; the study has %.0f%n",
                    listed[k].label(), got, study[k]);
            checkRel(listed[k].label() + " over 1946-50 is the study's, within 25%", got, study[k], .25);
        }
        assertTrue("typhoid over 1946-50 is under 1 per 100,000 (the study: about 0)",
                average(model, Cause.TYPHOID, 1946, 1950) < 1);
        boolean zero = true;
        for (int y : new int[]{1980, 2000}) {
            for (int k = 1; k <= 12; k++) {
                for (double[] row : city(model, month(y, k))) for (double v : row) zero &= v == 0;
            }
        }
        assertTrue("every cause is zero in every month of 1980 and 2000", zero);
        double[] last = year(model, 1979);
        assertTrue("fixture: 1979 still has deaths to take away", last[tb] > 0 && last[Cause.PNEUMONIA.ordinal()] > 0);

        /* ============ 6. the city's conditions raise a cause, never lower it ============ */
        System.out.println("\n--- the city's conditions raise a cause, never lower it ---");
        int jan1930 = month(1930, 1);
        double[][] best = city(model, jan1930);
        double[][] dry = city(model, jan1930, 0, 0, 1, mean, topOfSwing());
        double[][] half = city(model, jan1930, .5, 0, 1, mean, topOfSwing());
        assertTrue("fixture: by 1930 history has taken most of typhoid away",
                CausesOfDeath.history(Cause.TYPHOID, jan1930) < .1);
        checkRel("1930 with no piped water: an adult's typhoid is 1900's again",
                dry[ty][ADULT], jan1900[ty][ADULT], 1e-12);
        checkRel("...a baby's diarrhoea too", dry[d][BABY], jan1900[d][BABY], 1e-12);
        checkRel("1930 with half the city piped: typhoid is half-way between",
                half[ty][ADULT], (best[ty][ADULT] + dry[ty][ADULT]) / 2, 1e-12);
        for (double hungry : new double[]{.5, 1}) {
            double[][] hungrier = city(model, jan1930, 1, hungry, 1, mean, topOfSwing());
            checkRel("hunger " + hungry + " raises an adult's TB by 1 + TB_HUNGER x hungry",
                    hungrier[tb][ADULT] / best[tb][ADULT], 1 + CausesOfDeath.TB_HUNGER * hungry, 1e-12);
            checkRel("...and a baby's TB the same", hungrier[tb][BABY] / best[tb][BABY],
                    1 + CausesOfDeath.TB_HUNGER * hungry, 1e-12);
            checkRel("...and summer diarrhoea by 1 + DIARRHOEA_HUNGER x hungry",
                    hungrier[d][BABY] / best[d][BABY], 1 + CausesOfDeath.DIARRHOEA_HUNGER * hungry, 1e-12);
        }
        double[][] uncared = city(model, jan1930, 1, 0, 0, mean, topOfSwing());
        checkRel("no general care raises an adult's pneumonia by 1 + UNCARED",
                uncared[p][ADULT] / best[p][ADULT], 1 + CausesOfDeath.UNCARED, 1e-12);
        check("...and leaves a baby's pneumonia alone (childcare is theirs)", uncared[p][BABY], best[p][BABY], 0);
        checkRel("...and raises an adult's TB the same", uncared[tb][ADULT] / best[tb][ADULT], 1 + CausesOfDeath.UNCARED, 1e-12);
        checkRel("...and the mothers' deaths", uncared[bi][ADULT] / best[bi][ADULT], 1 + CausesOfDeath.UNCARED, 1e-12);
        assertTrue("fixture: TB's hunger and no care together would be over the cap",
                (1 + CausesOfDeath.TB_HUNGER) * (1 + CausesOfDeath.UNCARED) > CausesOfDeath.CONDITION_CAP);
        double[][] worst = city(model, jan1930, 1, 1, 0, mean, topOfSwing());
        checkRel("every condition at its worst holds an adult's TB at CONDITION_CAP",
                worst[tb][ADULT] / best[tb][ADULT], CausesOfDeath.CONDITION_CAP, 1e-12);
        boolean neverBelow = true, clamped = true;
        double[] grid = {0, .5, 1};
        for (int m = 1; m <= month(1980, 12); m += 7) {
            double[][] base = city(model, m);
            for (double piped : grid) for (double hungry : grid) for (double general : grid) {
                double[][] rates = city(model, m, piped, hungry, general, mean, topOfSwing());
                for (int c = 0; c < CAUSES.length; c++) for (int b = 0; b < BANDS.length; b++) {
                    neverBelow &= rates[c][b] >= base[c][b] * (1 - 1e-12);
                }
            }
        }
        double[][] out = city(model, jan1930, 5, 3, -2, mean, topOfSwing());
        double[][] edge = city(model, jan1930, 1, 1, 0, mean, topOfSwing());
        double[][] wild = city(model, jan1930, -4, 1, 0, mean, topOfSwing());
        double[][] dryWorst = city(model, jan1930, 0, 1, 0, mean, topOfSwing());
        for (int c = 0; c < CAUSES.length; c++) for (int b = 0; b < BANDS.length; b++) {
            clamped &= out[c][b] == edge[c][b] && wild[c][b] == dryWorst[c][b];
        }
        assertTrue("no piped water, hunger or lack of care takes any cause below its history, in any month to 1980", neverBelow);
        assertTrue("shares outside 0 to 1 are held to the ends", clamped);

        /* ============ 7. the 1918 influenza ============ */
        System.out.println("\n--- the 1918 influenza ---");
        int inf = Cause.INFLUENZA.ordinal();
        boolean onlyInItsMonths = true;
        int fluMonths = 0;
        for (int m = 1; m <= month(2000, 12); m++) {
            double[] row = city(model, m)[inf];
            boolean any = false;
            for (double v : row) any |= v > 0;
            if (CausesOfDeath.influenzaShare(m) > 0) { fluMonths++; onlyInItsMonths &= any; }
            else onlyInItsMonths &= !any;
        }
        assertTrue("the influenza kills in the months it has a share of and in no other", onlyInItsMonths);
        check("...and it ran for ten months", fluMonths, 10, 0);
        double fluDead = 0;
        for (int m = CausesOfDeath.INFLUENZA_FIRST; m <= CausesOfDeath.INFLUENZA_LAST; m++) fluDead += dead(city(model, m))[inf];
        checkRel("October 1918 to March 1920 the city loses INFLUENZA_DEATHS of its people",
                fluDead, CausesOfDeath.INFLUENZA_DEATHS * CITY, 1e-9);
        check("October 1918 holds .35 of the dead", CausesOfDeath.influenzaShare(oct1918), .35, 1e-12);
        double peak = 0, sick = 0;
        int peakMonth = 0;
        for (int m = 1; m <= month(2000, 12); m++) {
            double s = CausesOfDeath.influenzaSick(m);
            sick += s;
            if (s > peak) { peak = s; peakMonth = m; }
        }
        check("the sick peak in October 1918, at INFLUENZA_SICK x .35", peak, CausesOfDeath.INFLUENZA_SICK * .35, 1e-12);
        check("...in October 1918", peakMonth, oct1918, 0);
        check("September 1918 has no influenza sick", CausesOfDeath.influenzaSick(month(1918, 9)), 0, 0);
        check("...nor has April 1920", CausesOfDeath.influenzaSick(month(1920, 4)), 0, 0);
        check("over the epidemic a quarter of the people were sick, INFLUENZA_SICK", sick, CausesOfDeath.INFLUENZA_SICK, 1e-12);

        /* ============ 8. booking the dead, the TB sick and the save ============ */
        System.out.println("\n--- booking the dead, the TB sick and the save ---");
        CausesOfDeath books = new CausesOfDeath();
        int july1900 = month(1900, 7);
        double[][] rates = city(books, july1900, 1, .3, .6, mean, topOfSwing());
        double[] bandRates = CausesOfDeath.bandTotals(rates);
        double[] cohortsDead = new double[BANDS.length];
        double[] p1900 = people();
        for (int b = 0; b < BANDS.length; b++) cohortsDead[b] = p1900[b] * bandRates[b] * .8;   // the cap took a fifth
        books.book(july1900, rates, cohortsDead);
        check("the causes' dead add up to the dead the cohorts booked", books.getLastDeaths(), sum(cohortsDead), 1e-9 * sum(cohortsDead));
        double byHand = 0;
        for (int b = 0; b < BANDS.length; b++) byHand += cohortsDead[b] * rates[tb][b] / bandRates[b];
        checkRel("...TB's are each band's dead by TB's share of its rate", books.getLastDeaths(Cause.TB), byHand, 1e-12);
        boolean bandsOk = true;
        for (int b = 0; b < BANDS.length; b++) {
            double s = 0;
            for (int c = 0; c < CAUSES.length; c++) s += bandRates[b] > 0 ? cohortsDead[b] * rates[c][b] / bandRates[b] : 0;
            bandsOk &= Math.abs(s - cohortsDead[b]) <= 1e-9 * Math.max(1, cohortsDead[b]);
        }
        assertTrue("...and every band's dead are given out whole", bandsOk);
        double[][] oneCause = new double[CAUSES.length][BANDS.length];
        oneCause[Cause.TB.ordinal()][ADULT] = .25;
        oneCause[Cause.PNEUMONIA.ordinal()][ADULT] = .75;
        CausesOfDeath two = new CausesOfDeath();
        double[] adultsDead = new double[BANDS.length];
        adultsDead[ADULT] = 40;
        adultsDead[BABY] = 9;     // a band with no rate has nothing to book
        two.book(1, oneCause, adultsDead);
        check("two causes split a band's dead by their rates: TB 25%", two.getLastDeaths(Cause.TB), 10, 1e-12);
        check("...pneumonia 75%", two.getLastDeaths(Cause.PNEUMONIA), 30, 1e-12);
        check("a band with no rate books nothing", two.getLastDeaths(), 40, 1e-12);

        CausesOfDeath ring = new CausesOfDeath();
        double[][] diarrhoea = new double[CAUSES.length][BANDS.length];
        diarrhoea[d][ADULT] = 1;
        double all = 0, lastTwelve = 0;
        for (int m = 1; m <= 13; m++) {
            double[] one = new double[BANDS.length];
            one[ADULT] = 10 * m;
            ring.book(m, diarrhoea, one);
            all += 10 * m;
            if (m >= 2) lastTwelve += 10 * m;
        }
        check("after thirteen months the twelve-month sum has dropped the first", ring.getYearDeaths(Cause.DIARRHOEA), lastTwelve, 1e-9);
        check("...and the total has not", ring.getTotalDeaths(Cause.DIARRHOEA), all, 1e-9);
        check("...and last month's is the thirteenth", ring.getLastDeaths(Cause.DIARRHOEA), 130, 1e-12);
        check("a cause that was never booked has none", ring.getTotalDeaths(Cause.TB), 0, 0);

        /* the TB sick */
        CausesOfDeath sickModel = new CausesOfDeath();
        double[][] tbRates = city(sickModel, july1900, 1, .3, .6, mean, topOfSwing());
        double tbSick = 0, adultsTb = 0;
        for (AgeBand b : BANDS) {
            double part = p1900[b.ordinal()] * tbRates[tb][b.ordinal()] / Sickness.deathChance(b);
            tbSick += part;
            if (b == AgeBand.ADULT) adultsTb = part;
        }
        check("the TB sick are the TB's dead over the long sick's chance, band by band", sickModel.getTbSick(), tbSick, 1e-9 * tbSick);
        check("...the adults' part being their TB deaths over Sickness.deathChance(ADULT)", adultsTb,
                p1900[ADULT] * tbRates[tb][ADULT] / Sickness.deathChance(AgeBand.ADULT), 1e-9);
        assertTrue("fixture: the adults are most of the TB sick", adultsTb > .5 * tbSick && tbSick > 0);
        System.out.printf("   the 1900 city has %.0f people sick of TB for a year or more, %.2f%% of the city%n",
                tbSick, 100 * tbSick / CITY);

        /* bad arguments */
        double[][] none = sickModel.rates(july1900, null, topOfSwing(), 1, 0, 1, mean, 1);
        boolean zeros = none.length == CAUSES.length && sickModel.getTbSick() == 0;
        for (double[] row : none) for (double v : row) zeros &= row.length == BANDS.length && v == 0;
        assertTrue("a null people array gives zeros and no TB sick", zeros);
        zeros = true;
        for (double[] row : sickModel.rates(july1900, people(), new double[3], 1, 0, 1, mean, 1)) for (double v : row) zeros &= v == 0;
        for (double[] row : sickModel.rates(july1900, new double[7], topOfSwing(), 1, 0, 1, mean, 1)) for (double v : row) zeros &= v == 0;
        for (double[] row : sickModel.rates(july1900, people(), null, 1, 0, 1, mean, 1)) for (double v : row) zeros &= v == 0;
        assertTrue("...and so does a length that is not the bands'", zeros);
        double nan = Double.NaN, inf1 = Double.POSITIVE_INFINITY;
        boolean sane = true;
        double[][] fuzz = {
            {nan, nan, nan, nan, -1, 0}, {-3, 7, inf1, 2, 2, 2}, {0.5, 0.5, 0.5, 0.5, 0.5, 0.5}
        };
        for (double[] odd : fuzz) {
            double[] peopleOdd = {odd[0], 1000, odd[1], 5000, odd[2], 0};
            double[] factorOdd = {nan, inf1, -5, 1e9, odd[3], odd[4]};
            for (int m : new int[]{1, oct1918, july1930, month(1950, 6), month(2100, 3), -4, 0}) {
                double[][] r = sickModel.rates(m, peopleOdd, factorOdd, odd[5], nan, -7, inf1, nan);
                double[][] r2 = sickModel.rates(m, people(), swungAt(1e9), nan, 5, nan, -1, -9);
                for (double[][] g : new double[][][]{r, r2}) {
                    for (double[] row : g) for (double v : row) sane &= Double.isFinite(v) && v >= 0;
                }
                sane &= Double.isFinite(sickModel.getTbSick()) && sickModel.getTbSick() >= 0;
            }
        }
        assertTrue("garbage in (NaN, negatives, infinities) still gives finite rates of at least zero", sane);

        /* the save */
        CausesOfDeath played = new CausesOfDeath();
        for (int m = 1; m <= 15; m++) {
            double[][] r = city(played, m, 1, .1, .9, mean, topOfSwing());
            double[] deaths = new double[BANDS.length];
            double[] bt = CausesOfDeath.bandTotals(r);
            for (int b = 0; b < BANDS.length; b++) deaths[b] = p1900[b] * bt[b];
            played.book(m, r, deaths);
        }
        double[] state = played.getState();
        int n = CAUSES.length;
        check("the state is last month's, the totals, the ring and the TB sick", state.length, n + n + n * 12 + 1, 0);
        CausesOfDeath back = new CausesOfDeath();
        assertTrue("a state of the right length is taken", back.restore(state));
        boolean gettersOk = Arrays.equals(state, back.getState()) && back.getTbSick() == played.getTbSick()
                && back.getLastDeaths() == played.getLastDeaths();
        for (Cause c : CAUSES) {
            gettersOk &= back.getLastDeaths(c) == played.getLastDeaths(c)
                    && back.getYearDeaths(c) == played.getYearDeaths(c)
                    && back.getTotalDeaths(c) == played.getTotalDeaths(c);
        }
        assertTrue("the restored instance gives back every getter, and the state itself", gettersOk);
        assertTrue("fixture: the played city has dead of each named cause", played.getTotalDeaths(Cause.TB) > 0
                && played.getYearDeaths(Cause.DIARRHOEA) > 0 && played.getTbSick() > 0);
        double[] keep = back.getState();
        assertTrue("a state one too short is refused", !back.restore(Arrays.copyOf(state, state.length - 1)));
        assertTrue("...one too long", !back.restore(Arrays.copyOf(state, state.length + 1)));
        assertTrue("...empty", !back.restore(new double[0]));
        assertTrue("...and null", !back.restore(null));
        assertTrue("a refused state leaves the instance as it was", Arrays.equals(keep, back.getState()));
        back.reset();
        assertTrue("a reset city has booked nothing", back.getTotalDeaths(Cause.TB) == 0 && back.getYearDeaths(Cause.TB) == 0
                && back.getTbSick() == 0 && sum(back.getState()) == 0);

        System.out.println();
        System.out.println(fails == 0 ? "The dead have causes, and history takes them away." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
