package ham.citybuildersim;

/**
 * The city's tax rates - the revenue half of what the player actually decides.
 *
 * Both rates used to be one hardcoded field on EconomyManager. They live here
 * together because they are the same kind of thing, they are both set by the
 * player, they both have to survive a save, and because the two of them are
 * only interesting relative to each other: an income tax takes a share of what
 * a business earned, a property tax takes a share of what it owns whether it
 * earned anything or not. Which one the city leans on decides who actually pays
 * for it.
 *
 * ANNUAL IN, MONTHLY OUT
 *
 * Property tax is quoted annually, because that is how anyone who has ever paid
 * one thinks about it, and charged monthly, because that is the game's tick.
 * The conversion lives here and nowhere else - getting a 1.5%/year rate charged
 * as 1.5%/month would be an eighteen-fold error that still looks like a
 * plausible number on screen, so there is exactly one place it can be got wrong.
 *
 * Income tax is not converted: it is a share of a month's income, so the rate
 * applies to the month directly.
 *
 * ======================================================================
 * CITY RATES, AND OFFSETS FROM THEM
 * ======================================================================
 *
 * There are four base rates - one per tax: profit, sales, wage and property -
 * and then every band and every sector carries an OFFSET in rate points from
 * its own tax's base. Industry at -0.03 on profit pays three points under
 * whatever the profit rate is.
 *
 * Jerus's call, and it is the right one for a game where a base is a lever the
 * player pulls often: raise it and every sector you have customised keeps its
 * relative treatment instead of being silently left behind. The cost is that a
 * rate on screen is arithmetic rather than a number, so every screen shows the
 * offset AND what it resolves to.
 *
 * THREE BASES WHERE THERE WAS ONE (0.7.4). Until 0.7.4 profit, sales and wage
 * all moved off ONE city income rate, so raising sales tax for every sector
 * meant fifteen offsets or the city rate, and the city rate dragged profit and
 * wage with it. Jerus: "what about just increasing sale tax for all at the same
 * time? currently that's a hassle, i want to be able to do that for every type
 * of tax, even wage tax". Each of the three has its own base now, and
 * setIncomeTaxRate() is what his old city rate became: every income tax at
 * once, all three bases set to one number.
 *
 * RESOLUTION HAPPENS HERE, ONCE. effective*() clamps to [0, max]. No caller
 * adds an offset itself: an offset that escapes clamping is a negative tax rate,
 * which is the city paying businesses to trade, and it would show up as revenue
 * appearing from nowhere three layers away from the line that caused it.
 *
 * DEFAULTS ARE ALL ZERO, and the three bases open equal, which makes every
 * effective rate the one income rate and reproduces the single-rate behaviour
 * this replaced, exactly.
 */
public class TaxPolicy {

    /** Where income tax started before it was a dial. */
    public static final double DEFAULT_INCOME_TAX = .15;

    /**
     * 1.5% a year, near the real-world average.
     *
     * Worth being conservative with. Property tax is charged on capital whether
     * or not it earned anything, so it is the one rate that can push a business
     * under while it is doing nothing wrong - and the city's biggest owner of
     * idle capital is its construction sector.
     */
    public static final double DEFAULT_PROPERTY_TAX = .015;

    /** Nobody has ever paid 100% property tax and the game should not model it. */
    public static final double MAX_PROPERTY_TAX = .10;

    /** Above this, income tax stops being a policy and starts being confiscation. */
    public static final double MAX_INCOME_TAX = .60;

    /**
     * How far a band or sector may be moved from its tax's base rate, either way.
     *
     * Bounded so a single offset cannot express a policy the base rate could not
     * express on its own - an offset is a discount or a surcharge, not a
     * separate tax system.
     */
    public static final double MAX_OFFSET = .30;

    /*
     * THE THREE INCOME BASES (0.7.4), one per tax, where there was one income
     * rate - see CITY RATES, AND OFFSETS FROM THEM above. Each opens
     * at DEFAULT_INCOME_TAX, so a new city is the single-rate city it was.
     */
    private double profitTaxRate = DEFAULT_INCOME_TAX;
    private double salesTaxRate = DEFAULT_INCOME_TAX;
    private double wageTaxRate = DEFAULT_INCOME_TAX;
    private double propertyTaxRate = DEFAULT_PROPERTY_TAX;

    /* ==================================================================
       FARMLAND, AND WHAT IT IS ASSESSED AT (2026-09-13)

       A farm is the only building in this game whose cost is the GROUND
       rather than the structure. A Mixed Farm's shed and machinery come to
       $1.19m; the twenty-four city blocks under it are $3.1m in a young city
       and $144m in a grown one, and the property tax is struck on what the
       ground would FETCH. At a grown city's land price that bill is more than
       the field can grow on it, several times over, long before the city
       actually reaches the fence.

       THAT IS NOT A BUG, IT IS WHAT HAPPENS, and it is why almost every
       province and state assesses farmland at USE value rather than at
       development value - Ontario's Farm Property Class, Nova Scotia's
       resource-property rate, California's Williamson Act, and farm-use
       assessment in most of the United States all exist to stop a growing
       town taxing the market gardens off its own edge.

       So the player gets the same lever, with the same cost. At 0 the fields
       are assessed like anything else, the tax exceeds the harvest, and the
       city buys its dinner from strangers - which is a real outcome and a
       defensible one. At 1 the ground under a farm is assessed at nothing and
       only the barn is taxed, the fields survive the city growing round them,
       and the city forgoes the tax on what by then is a large share of its
       whole assessment. Jerus's call, 2026-09-13, taken against the
       alternative of simply making an acre of wheat out-earn an acre of
       houses, which is the opposite of the reason cities exist.

       IT TOUCHES THE LAND HALF ONLY. A barn is a building and is taxed like
       one; the relief is on the difference between what the ground is worth to
       a developer and what it is worth to a farmer, which is exactly what the
       real programmes relieve.

       Defaults to full relief because that is what the real default is almost
       everywhere, and because a player who never finds the dial should get the
       behaviour a real jurisdiction has rather than the one nobody chose.
       ================================================================== */
    public static final double DEFAULT_FARMLAND_RELIEF = 1.0;

    /** The sector whose ground the relief applies to. */
    public static final String FARM_SECTOR = Sectors.AGRICULTURE;

    private double farmlandRelief = DEFAULT_FARMLAND_RELIEF;

    /** 0 assesses a field like a building lot; 1 assesses only what stands on it. */
    public double getFarmlandRelief() { return farmlandRelief; }

    public void setFarmlandRelief(double share) {
        this.farmlandRelief = share < 0 ? 0 : Math.min(share, 1);
    }

    /**
     * The share of a sector's LAND that is on the assessment roll. One for
     * everybody; less for the fields, by however much the player has relieved
     * them. See EconomyManager.getAssessedValue().
     */
    public double assessedLandShare(String sector) {
        return FARM_SECTOR.equals(sector) ? 1 - farmlandRelief : 1;
    }

    /* =====================================================================
       THE PENSION PROMISE, AS TWO DIALS

       They were constants on SocialSecurity, and constants are the wrong shape
       for the only two numbers in the game that are a POLICY CHOICE about the
       age structure. A city whose pyramid is greying has exactly two levers -
       charge the workers more, or pay the pensioners less - and until now it
       had neither.

       They live here rather than on SocialSecurity because this is the class
       that is saved, has a screen, and already holds every other rate the
       player sets. SocialSecurity keeps the arithmetic and now takes the rate
       as an argument, which also makes it testable at rates nobody has set.
       ===================================================================== */

    private double contributionRate  = SocialSecurity.DEFAULT_CONTRIBUTION_RATE;
    private double pensionReplacement = SocialSecurity.DEFAULT_PENSION_REPLACEMENT;

    /** Nobody hands over more than a fifth of a wage, whatever the deficit. */
    public static final double MAX_CONTRIBUTION = .20;

    /** A pension of more than one unskilled wage is a wage, not a pension. */
    public static final double MAX_REPLACEMENT = 1.00;

    public double getContributionRate()   { return contributionRate; }
    public double getPensionReplacement() { return pensionReplacement; }

    public void setContributionRate(double rate) {
        contributionRate = Math.max(0, Math.min(MAX_CONTRIBUTION, rate));
    }

    public void setPensionReplacement(double share) {
        pensionReplacement = Math.max(0, Math.min(MAX_REPLACEMENT, share));
    }

    /* =====================================================================
       EMPLOYMENT INSURANCE AND THE STUDENT GRANT, AS THREE MORE DIALS
       (2026-09-11)

       Jerus: EI "like CPP" - a premium off every wage into the treasury,
       pay-as-you-go, and a policy dial. The same shape as the pension's two:
       what the workers pay, and what the out of work draw. And the grant a
       full-time student is paid, beside them, because it is the same kind of
       promise. Defaults are the real 2026 figures; see Unemployment and
       StudentHousehold.
       ===================================================================== */

    private double eiPremiumRate = Unemployment.DEFAULT_PREMIUM_RATE;
    private double eiBenefitRate = Unemployment.DEFAULT_BENEFIT_RATE;

    /**
     * The Canada Student Grant, $525 a month of study (2026-27), as a share of
     * the $3,460 unskilled median the ladder is anchored on - so it moves with
     * the wage it was measured against and with a currency reform.
     */
    public static final double DEFAULT_STUDENT_GRANT_SHARE = 525.0 / 3_460;

    /** A premium past a tenth of a wage is a second income tax. */
    public static final double MAX_EI_PREMIUM = .10;

    /** EI that replaces more than the wage pays people to stay out of work. */
    public static final double MAX_EI_BENEFIT = 1.00;

    /** A grant of more than an unskilled wage is a wage: the ceiling on the WAGE_SHARE basis's share. */
    public static final double MAX_STUDENT_GRANT = 1.00;

    public double getEiPremiumRate()     { return eiPremiumRate; }
    public double getEiBenefitRate()     { return eiBenefitRate; }

    /**
     * The share of the unskilled wage a student is granted a month: the
     * amount under the WAGE_SHARE basis, and nothing under any other, because
     * under any other there is no such share. See getGrantBasis().
     */
    public double getStudentGrantShare() {
        return grantBasis == GrantBasis.WAGE_SHARE ? grantAmount : 0;
    }

    public void setEiPremiumRate(double rate)     { eiPremiumRate = clamp(rate, MAX_EI_PREMIUM); }
    public void setEiBenefitRate(double share)    { eiBenefitRate = clamp(share, MAX_EI_BENEFIT); }

    /** The grant as a share of the unskilled wage - the founding basis, at this share. */
    public void setStudentGrantShare(double share) {
        grantBasis = GrantBasis.WAGE_SHARE;
        grantAmount = clamp(share, MAX_STUDENT_GRANT);
    }

    /* =====================================================================
       THE PRICE OF A PLACE - THE GRANT'S BASIS, THE LOAN'S RATE AND THE
       TUITION SCALE (2026-09-21)

       Jerus: "yes grants and government tuition are in the game, but what
       about more granularity, so perhaps, grants its just a menu where you
       can choose between a fixed amount, or a percentage of last month's
       surplus, or a % as it is now of living costs, or a % of tuition. and
       then another slider which is the interest rate for the student loans,
       and idk if real life is like that but have it so that the money is
       withdrawn from the treasury and then later when they pay it back it's
       added back, and you get the interest if there is any. ... and also
       make it so that you can tweak the price of tuition as well."

       Three more dials in the EI premium's shape, and the grant's one share
       becomes a MENU:

         grantBasis, grantAmount   what the grant is a share of, or is, and
                                   the one number that goes with it. The
                                   founding rule - a share of the unskilled
                                   wage, per student per month ("as it is
                                   now"; Jerus said living costs, and the
                                   wage is what that has always been struck
                                   against) - is one of four. The others: a
                                   fixed amount a month; a share of LAST
                                   month's budget surplus as one pool split
                                   over this month's students, nothing in a
                                   deficit month; a share of each student's
                                   own course tuition at today's price. The
                                   amount is in the basis's own unit and has
                                   a ceiling per basis, below. TaxPolicy
                                   strikes the bill (grantBill) and Game
                                   hands it the four figures it needs
                                   (Game.studentGrantBillUnder); every
                                   reader - the treasury's bill, the
                                   save's re-strike, the students' income,
                                   the Schools page - goes through that one
                                   method.
         studentLoanRate           annual, on the treasury's student loans.
                                   Interest is charged on a GRADUATE's
                                   balance during repayment only - the
                                   Canadian shape, the government carrying
                                   the interest while the student studies -
                                   and it lands in the treasury as its own
                                   revenue line, beside the premiums; the
                                   principal keeps coming back through the
                                   journal's line as it did. Zero by default,
                                   as Canada's loans have been since April
                                   2023. See Household's THE LOAN'S RATE.
         tuitionScale              a multiplier on the founding tuition table,
                                   Healthcare's fee scale for the schools. 1
                                   is the founding price; 0 is free; the
                                   poverty trap the Education header describes
                                   returns around x3 against today's wages.
                                   Education.foundingTuition() stays the
                                   founding table; what a seat is charged at,
                                   what a household measures against its wage,
                                   what the treasury collects and forgoes, and
                                   the TUITION_SHARE grant all read the scaled
                                   fee.

       The loan rate and the scale are RATIOS and a currency reform leaves
       them alone; a FIXED grant is money and is scaled with it. All four
       ride this class's save array on the end: an old save reads WAGE_SHARE
       at the share its old slot already carried, no interest and the
       founding price, which is the game it was.

       A SCALE PER SCHOOL WHERE THERE WAS ONE (0.7.6). Jerus: "have it so
       that not only can you raise prices but also raise the price for a
       specific university". The one tuition scale is nine now, one per
       EducationType bar NONE - tuitionScaleOf() and setTuitionScaleOf() -
       each opening at DEFAULT_TUITION_SCALE and held to MAX_TUITION_SCALE,
       which is the shape 0.7.4 gave the income taxes (THREE BASES WHERE
       THERE WAS ONE, under THE CITY RATES): setTuitionScale() is what the
       one scale became, every school at once, all nine set to one number;
       getTuitionScale() reads the first of the nine, ELEMENTARY's, and
       tuitionScalesSplit() says whether they have parted. The old slot in
       the save array carries getTuitionScale(), and the nine ride the end;
       an older, shorter array reads all nine as the one scale it carried.
       ===================================================================== */

    /** How the student grant is struck: what the one amount is a share of, or is. */
    public enum GrantBasis {
        /** A share of the unskilled wage, per student per month - the founding rule, and the default. */
        WAGE_SHARE,
        /** A fixed amount per student per month, in thousands: money, so a currency reform scales it. */
        FIXED,
        /** A share of last month's budget surplus as ONE POOL, split evenly over this month's students; a deficit month pays nothing. */
        SURPLUS_SHARE,
        /** A share of each student's own course tuition at today's price - after the tuition scale, before the subsidy. */
        TUITION_SHARE
    }

    /** Where the grant starts: the founding rule, a share of the unskilled wage. */
    public static final GrantBasis DEFAULT_GRANT_BASIS = GrantBasis.WAGE_SHARE;

    /** A fixed grant of more than one unskilled wage a month is a wage: the FIXED ceiling, in founding unskilled wages, struck against that wage in today's money. */
    public static final double MAX_FIXED_GRANT_WAGES = 1.00;

    /** The whole of last month's surplus, and no more: the SURPLUS_SHARE ceiling. */
    public static final double MAX_SURPLUS_GRANT_SHARE = 1.00;

    /** Twice the course's tuition: the TUITION_SHARE ceiling, so a grant can cover the fee and living costs on top. */
    public static final double MAX_TUITION_GRANT_SHARE = 2.00;

    /** Where the loan rate starts: no interest, as Canada's loans have been since April 2023. */
    public static final double DEFAULT_STUDENT_LOAN_RATE = 0;

    /** Fifteen per cent a year: past any rate a government has charged a student, and the dial's ceiling. */
    public static final double MAX_STUDENT_LOAN_RATE = .15;

    /** Where the tuition scale starts: the founding table, unscaled. */
    public static final double DEFAULT_TUITION_SCALE = 1.0;

    /** Five times the founding table: the trap the Education header describes returns around x3 against today's wages (university 3.60 against a diploma wage of 4.500 is past MAX_BURDEN unsubsidised), so x5 leaves room past it; a ceiling, not a default. */
    public static final double MAX_TUITION_SCALE = 5.0;

    /**
     * What the grant is a share of, or is. An old save reads WAGE_SHARE - the
     * only basis there was.
     */
    private GrantBasis grantBasis = DEFAULT_GRANT_BASIS;

    /**
     * The grant's one number, in the basis's own unit: a share of the wage, a
     * fixed amount in thousands, a share of the surplus, or a share of the
     * tuition. An old save reads its wage share into this, from the slot that
     * always carried it.
     */
    private double grantAmount = DEFAULT_STUDENT_GRANT_SHARE;

    /** Annual interest on the treasury's student loans. An old save reads none. */
    private double studentLoanRate = DEFAULT_STUDENT_LOAN_RATE;

    /**
     * The multiplier on the founding tuition table, one per school kind by
     * EducationType ordinal (0.7.6). NONE's slot is never read, set or saved:
     * a building that teaches nothing has no price. An old save reads 1 on
     * every kind.
     */
    private final double[] tuitionScales = new double[EducationType.values().length];
    { java.util.Arrays.fill(tuitionScales, DEFAULT_TUITION_SCALE); }

    /** The nine kinds a school can be, in EducationType order: everything bar NONE (0.7.6). */
    private static final EducationType[] SCHOOL_KINDS = schoolKinds();

    private static EducationType[] schoolKinds() {
        java.util.List<EducationType> kinds = new java.util.ArrayList<>();
        for (EducationType type : EducationType.values()) {
            if (type != EducationType.NONE) kinds.add(type);
        }
        return kinds.toArray(new EducationType[0]);
    }

    public GrantBasis getGrantBasis() { return grantBasis; }

    /** The amount that goes with getGrantBasis(), in that basis's unit. */
    public double getGrantAmount()    { return grantAmount; }

    /** The annual rate a graduate is charged on the student loan while repaying it. */
    public double getStudentLoanRate(){ return studentLoanRate; }

    /**
     * The multiplier on the founding tuition table; 1 is the founding price,
     * 0 is free at the point of use. EVERY SCHOOL AT ONCE (0.7.6): while the
     * nine kinds are equal, which they are in any city that has only ever
     * moved them together, that one scale; once they have parted, the first
     * kind's, ELEMENTARY's - what the old slot of the save carries for a
     * reader that knows only one. A caller that means one kind asks
     * tuitionScaleOf(); a screen that prints "the scale" asks
     * tuitionScalesSplit() first.
     */
    public double getTuitionScale()   { return tuitionScales[SCHOOL_KINDS[0].ordinal()]; }

    /** One school kind's own multiplier (0.7.6); NONE, or null, reads the every-school one. */
    public double tuitionScaleOf(EducationType type) {
        return type == null || type == EducationType.NONE ? getTuitionScale()
                : tuitionScales[type.ordinal()];
    }

    /** Whether the nine kinds' scales have parted - no longer one number (0.7.6). */
    public boolean tuitionScalesSplit() {
        double first = getTuitionScale();
        for (EducationType type : SCHOOL_KINDS) {
            if (Math.abs(tuitionScales[type.ordinal()] - first) > 1e-12) return true;
        }
        return false;
    }

    /**
     * The ceiling on the amount under a basis: a share of a wage up to one
     * wage, a fixed amount up to an unskilled wage (the founding wage in
     * today's money, which pensionWageBase carries), the whole surplus, twice
     * the tuition.
     */
    public double maxGrantAmount(GrantBasis basis) {
        if (basis == null) return MAX_STUDENT_GRANT;
        switch (basis) {
            case FIXED:         return MAX_FIXED_GRANT_WAGES * pensionWageBase;
            case SURPLUS_SHARE: return MAX_SURPLUS_GRANT_SHARE;
            case TUITION_SHARE: return MAX_TUITION_GRANT_SHARE;
            default:            return MAX_STUDENT_GRANT;
        }
    }

    /** Picks the basis; the amount is clamped to the new basis's ceiling and otherwise left where it was. */
    public void setGrantBasis(GrantBasis basis) {
        grantBasis = basis == null ? DEFAULT_GRANT_BASIS : basis;
        grantAmount = clamp(grantAmount, maxGrantAmount(grantBasis));
    }

    /** Sets the amount, in the current basis's unit, clamped to its ceiling. */
    public void setGrantAmount(double amount) {
        grantAmount = clamp(amount, maxGrantAmount(grantBasis));
    }

    /** Both at once, so a screen can stage them together. */
    public void setGrant(GrantBasis basis, double amount) {
        setGrantBasis(basis);
        setGrantAmount(amount);
    }

    public void setStudentLoanRate(double annual) { studentLoanRate = clamp(annual, MAX_STUDENT_LOAN_RATE); }

    /**
     * Every school at once: all nine kinds set to this scale (0.7.6), which
     * is what the one scale did. The Schools page's top lever, the
     * playtest's switch and a save from before the split all come through
     * here, so a city that never parts them is the city it was.
     */
    public void setTuitionScale(double scale) {
        double s = clamp(scale, MAX_TUITION_SCALE);
        for (EducationType type : SCHOOL_KINDS) tuitionScales[type.ordinal()] = s;
    }

    /** One school kind's own scale, held to MAX_TUITION_SCALE (0.7.6); NONE, or null, is ignored. */
    public void setTuitionScaleOf(EducationType type, double scale) {
        if (type == null || type == EducationType.NONE) return;
        tuitionScales[type.ordinal()] = clamp(scale, MAX_TUITION_SCALE);
    }

    /**
     * The month's grant bill under any basis and amount - the one place the
     * four rules are written, so the treasury's bill, the save's re-strike,
     * the students' income and the Schools page's preview cannot disagree.
     *
     *   WAGE_SHARE     students x amount x the unskilled wage, in that order:
     *                  the founding expression, and at the founding share it
     *                  is bit for bit the bill it always was.
     *   FIXED          students x amount.
     *   SURPLUS_SHARE  amount x last month's surplus, as ONE pool - a deficit
     *                  is a pool of nothing - and nothing at all with nobody
     *                  to split it over. Per student it is the pool over the
     *                  students.
     *   TUITION_SHARE  amount x what the whole student body is charged at
     *                  today's price, before the subsidy: each student's own
     *                  course fee, so a medical student is granted more than
     *                  a college one.
     *
     * @param students           full-time students this month
     * @param unskilledWage      what an unskilled post pays a month, today
     * @param lastSurplus        last month's budget balance, as the Government
     *                           tab shows it; negative in a deficit month
     * @param studentBodyTuition the whole adult student body's tuition a month
     *                           at today's price, before the subsidy
     */
    public static double grantBill(GrantBasis basis, double amount, double students,
                                   double unskilledWage, double lastSurplus,
                                   double studentBodyTuition) {
        if (basis == null) basis = DEFAULT_GRANT_BASIS;
        switch (basis) {
            case FIXED:         return students * amount;
            case SURPLUS_SHARE: return students > 0 ? amount * Math.max(0, lastSurplus) : 0;
            case TUITION_SHARE: return amount * Math.max(0, studentBodyTuition);
            default:            return students * amount * unskilledWage;
        }
    }

    /* =====================================================================
       HEALTHCARE HAS A PRICE, AND A PREMIUM (2026-09-19)

       Jerus: "healthcare should be an adjustable price, all the way to even
       make it a profitable business or the option to make it an obligatory
       insurance payment system." Two dials in the EI premium's shape:

         healthFeeScale    a multiplier on the three care fees. 0 is care free
                           at the point of use; the scale that recovers the
                           service's cost is the city's own (Healthcare
                           .breakEvenScale(), x7 to x13 in the played cities,
                           because wages have risen against the founding
                           fees, and the dial runs to 15 so it is reachable);
                           past it, a business. FUNERAL FEES ARE NOT
                           SCALED - the cemetery-against-crematorium design is
                           its own thing and keeps its own prices.
         healthPremiumRate a share of every wage, employee side, into the
                           treasury as revenue. No employer share and no
                           automatic balancing, per Jerus: what it raises goes
                           against the service's cost, and the gap either way
                           is the treasury's.

       Fees 0 and premium 0 is tax-funded care; fees up is fee-funded;
       fees 0 and a premium is insurance; anything between is a mix. AND A
       FEE CAN PRICE PEOPLE OUT: a household that cannot pay its care bill
       after its savings, its shares and its credit goes without care rather
       than without food - see Household.affordCare() - so a high fee is a
       sicker city. That is what makes the dial a decision.

       Both are RATIOS, not money, so a currency reform leaves them alone;
       both ride this class's save array, on the end, and an old save reads
       1 and 0, which is the game it was.
       ===================================================================== */

    /** Where the fee scale starts: the founding fees, unscaled. */
    public static final double DEFAULT_HEALTH_FEE_SCALE = 1.0;

    /** Fifteen times the founding fees: past every played city's break-even (x7 to x13 at today's wages), so the business corner is reachable; a ceiling, not a default. */
    public static final double MAX_HEALTH_FEE_SCALE = 15.0;

    /** Where the health premium starts: nobody pays one until the player says so. */
    public static final double DEFAULT_HEALTH_PREMIUM = 0;

    /** A health premium past a tenth of a wage is a second income tax, as the EI premium's ceiling says. */
    public static final double MAX_HEALTH_PREMIUM = .10;

    private double healthFeeScale = DEFAULT_HEALTH_FEE_SCALE;
    private double healthPremiumRate = DEFAULT_HEALTH_PREMIUM;

    /** The multiplier on the three care fees; 1 is the founding fee, 0 is free at the point of use. */
    public double getHealthFeeScale()   { return healthFeeScale; }

    /** The share of every wage the health premium takes, employee side. */
    public double getHealthPremiumRate(){ return healthPremiumRate; }

    public void setHealthFeeScale(double scale)  { healthFeeScale = clamp(scale, MAX_HEALTH_FEE_SCALE); }
    public void setHealthPremiumRate(double rate){ healthPremiumRate = clamp(rate, MAX_HEALTH_PREMIUM); }

    /**
     * A pension, in TODAY's money.
     *
     * SocialSecurity computes this as `replacement x PayTier.UNSKILLED`, which
     * is a compile-time money constant read every month - so a reformed city
     * went on paying its pensioners the FOUNDING cheque out of the new,
     * hundred-times-smaller money. Measured at a factor of two: seniors
     * received 110.65 where 55.32 was due, and they were the last row in the
     * household matrix that would not come into line.
     *
     * (The wage this is struck against does not move with the labour market
     * either, so a pension is frozen in real terms for three centuries. That is
     * a separate design question and not this one's to answer - filed, not
     * fixed.)
     */
    public double pensionPerSenior() {
        return pensionReplacement * pensionWageBase;
    }

    /** The wage a pension is a share of, carried in today's money. */
    private double pensionWageBase = PayTier.UNSKILLED.getMonthlyWage();

    public void redenominate(double scale) {
        pensionWageBase *= scale;
        /*
         * AND THE FARE, because it is a price and not a rate. Every other dial
         * on this class (bar a FIXED grant, since 2026-09-21 - below) is a
         * fraction and survives a reform untouched; a fare is dollars a
         * journey and a reform that left it alone would multiply the real
         * price of a bus ride by a hundred overnight. Twenty-third of
         * a family this codebase has been finding since September 8th, and the
         * first one caught in the same edit that created the field.
         */
        transitFare *= scale;
        /*
         * AND A FIXED GRANT (2026-09-21), for the fare's reason: it is dollars
         * a student a month, not a share of anything, and a reform that left
         * it alone would hand every student a hundred times the grant. The
         * grant's other three bases are shares and do not move; nor do the
         * loan rate and the tuition scale, which are ratios.
         */
        if (grantBasis == GrantBasis.FIXED) grantAmount *= scale;
    }

    /* =======================================================================
       WHAT A RIDE COSTS (2026-09-16)

       The first price the city charges that a person can refuse to pay.

       Nobody chooses to be policed and a patient in a clinic is not shopping,
       so healthcare and safety are charges. A fare is a PRICE: set it high and
       people drive instead, and every one of them is back on the road the
       transit was built to empty. Jerus: "yes riders pay, yes its adjustable,
       to the point you can even make it profitable by alot" - and it can be,
       at a ridership the city may not want.

       ZERO IS A LEGITIMATE SETTING and the default is not it. Free transit is
       a real policy with a real bill attached; the default is a fare that
       covers a decent share of the wages, because a city that has not thought
       about it should not be quietly running the buses for nothing.
       ======================================================================= */

    /** What a single journey costs a rider, in thousands. $2.50 a ride. */
    public static final double DEFAULT_TRANSIT_FARE = .0025;

    /** Past this nobody rides at all, as a multiple of the default. */
    public static final double MAX_TRANSIT_FARE = .05;

    /* =======================================================================
       A RIDE IS NOT A MONTH (2026-09-17)

       Jerus: "the bus fares you got it right? the fact that you were charging
       2.5 for a month when in fact 2.50 for a month is way way way way too
       good of a deal lol" - and he is exactly right, and it had been wrong
       from the hour the fare was written.

       THE CONSTANT SAYS A JOURNEY AND THE ARITHMETIC SAID A MONTH. The city
       collected getTransitRiders() x getTransitFare(), and a rider is a
       PERSON: InfrastructureManager documents the figure as "commuters
       actually carried off the road this month", and the commuter load it is
       capped against is a headcount - jobs, plus 1.2 per home, straight out
       of BuildingsTemplate.loadOf(). So a city with fifty thousand people on
       its trams took a hundred and twenty-five thousand dollars a month for
       carrying them, every day, all month, and wondered why the buses lost
       money.

       AND THAT IS THE TRANSIT LOSS. The $5.4M a month the todo list has been
       carrying as a balance problem was never a balance problem. The wages
       were priced per month, because a driver is paid per month; the fare was
       priced per ride and then charged once. One of the two numbers was in
       the wrong unit and it was not the payroll.

       WHY FORTY. Two journeys a day - out and back, which is what a commuter
       is - times twenty working days. A monthly pass at the default fare is
       $100 against an unskilled wage of $3,460, or 2.9% of it, which is about
       what a real one costs a real low earner. Nothing here is tuned to a
       target; it is two trips a day and a working month.

       THE DIAL STAYS PER RIDE and that is deliberate. The player sets what a
       BUS TICKET costs, because that is the number a person on a platform
       recognises and the number the ridership curve is about - MAX_TRANSIT_FARE
       is $50 a ride, which is obviously absurd, and is meant to be. The
       multiplication into a month happens once, here, so that every place
       that needs the monthly figure asks for it rather than each deriving it.
       ======================================================================= */

    /** Journeys one commuter makes in a month: out and back, twenty days. */
    public static final double JOURNEYS_A_MONTH = 40;

    /**
     * What a month of riding costs one commuter, in thousands.
     *
     * The ONE source of the monthly figure. Game bills the households with it
     * and the national accounts collect it; anything that multiplies a rider
     * headcount by a fare wants this and not getTransitFare().
     */
    public double monthlyFare() { return transitFare * JOURNEYS_A_MONTH; }

    private double transitFare = DEFAULT_TRANSIT_FARE;

    public double getTransitFare() { return transitFare; }

    public void setTransitFare(double fare) {
        this.transitFare = Math.max(0, Math.min(MAX_TRANSIT_FARE, fare));
    }

    /** Re-seeds the money CONSTANTS at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        pensionWageBase = PayTier.UNSKILLED.getMonthlyWage() / unit;
    }

    private final double[] wageOffset     = new double[WageBand.values().length];

    /*
     * THE SECTOR OFFSETS, KEYED BY THE SECTOR'S NAME (2026-09-11, the sector
     * template). They were three arrays indexed by PolicySector.ordinal(),
     * which was the cleanest registry-conversion candidate in the codebase
     * and the one that would have re-keyed every player's policy the day a
     * seventh sector was inserted anywhere but the end. A name is a name.
     * An unset sector reads zero, which is its tax's base rate - the same
     * default the arrays had.
     */
    private final java.util.Map<String, Double> profitOffset   = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, Double> salesOffset    = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, Double> propertyOffset = new java.util.LinkedHashMap<>();

    /* ==================================================================
       THE CITY RATES

       Four bases: profit, sales and wage since 0.7.4 (one income rate
       before), and property. "The income rate" survives as the three
       together - getIncomeTaxRate() and setIncomeTaxRate() below.
       ================================================================== */

    /**
     * The three income taxes TOGETHER - what "the city rate" was until 0.7.4.
     *
     * While the three bases are equal, which they are in any city that has
     * only ever moved them together, that one rate. Once they have parted,
     * the PROFIT rate: the first of the three, and what slot 0 of the save
     * carries for a reader that knows only one. A caller that means one tax
     * asks for it by name; a screen that prints "the income rate" asks
     * incomeRatesSplit() first and prints each when they have parted.
     */
    public double getIncomeTaxRate() {
        return profitTaxRate;
    }

    /** Whether the three income bases have parted - profit, sales and wage no longer one number. */
    public boolean incomeRatesSplit() {
        return Math.abs(profitTaxRate - salesTaxRate) > 1e-12
                || Math.abs(profitTaxRate - wageTaxRate) > 1e-12;
    }

    /** What every sector's profit tax moves off (0.7.4). */
    public double getProfitTaxRate() { return profitTaxRate; }

    /** What every sector's sales tax moves off (0.7.4). */
    public double getSalesTaxRate()  { return salesTaxRate; }

    /** What every band's wage tax moves off (0.7.4). */
    public double getWageTaxRate()   { return wageTaxRate; }

    public void setProfitTaxRate(double rate) { profitTaxRate = clamp(rate, MAX_INCOME_TAX); }
    public void setSalesTaxRate(double rate)  { salesTaxRate = clamp(rate, MAX_INCOME_TAX); }
    public void setWageTaxRate(double rate)   { wageTaxRate = clamp(rate, MAX_INCOME_TAX); }

    /** The annual rate - what the player sets and what the screens show. */
    public double getPropertyTaxRate() {
        return propertyTaxRate;
    }

    /** The annual rate divided by twelve. What is actually charged each month. */
    public double getMonthlyPropertyTaxRate() {
        return propertyTaxRate / 12;
    }

    /**
     * Every income tax at once: the profit, sales and wage bases all set to
     * this rate (0.7.4), which is what the one city rate did. The Everything
     * page's lever, the playtest's advisor and a save from before the split
     * all come through here, so a city that never parts them is the city it
     * was.
     */
    public void setIncomeTaxRate(double rate) {
        double r = clamp(rate, MAX_INCOME_TAX);
        profitTaxRate = r;
        salesTaxRate = r;
        wageTaxRate = r;
    }

    /** Takes the ANNUAL rate. */
    public void setPropertyTaxRate(double annualRate) {
        this.propertyTaxRate = clamp(annualRate, MAX_PROPERTY_TAX);
    }

    /* ==================================================================
       OFFSETS
       ================================================================== */

    public double getWageOffset(WageBand band)     { return wageOffset[band.ordinal()]; }
    public double getProfitOffset(String sector)   { return profitOffset.getOrDefault(sector, 0.0); }
    public double getSalesOffset(String sector)    { return salesOffset.getOrDefault(sector, 0.0); }
    public double getPropertyOffset(String sector) { return propertyOffset.getOrDefault(sector, 0.0); }

    public double getProfitOffset(Sector s)   { return getProfitOffset(s.key()); }
    public double getSalesOffset(Sector s)    { return getSalesOffset(s.key()); }
    public double getPropertyOffset(Sector s) { return getPropertyOffset(s.key()); }

    public void setWageOffset(WageBand band, double points) {
        wageOffset[band.ordinal()] = clampOffset(points);
    }

    public void setProfitOffset(String sector, double points) {
        if (sector != null) profitOffset.put(sector, clampOffset(points));
    }

    public void setSalesOffset(String sector, double points) {
        if (sector != null) salesOffset.put(sector, clampOffset(points));
    }

    /** In ANNUAL points, matching the rate it offsets. */
    public void setPropertyOffset(String sector, double points) {
        if (sector != null) propertyOffset.put(sector, clampOffset(points));
    }

    public void setProfitOffset(Sector s, double points)   { setProfitOffset(s.key(), points); }
    public void setSalesOffset(Sector s, double points)    { setSalesOffset(s.key(), points); }
    public void setPropertyOffset(Sector s, double points) { setPropertyOffset(s.key(), points); }

    /* ==================================================================
       EFFECTIVE RATES - the only numbers anything is ever charged at
       ================================================================== */

    /** What wages in this band are taxed at: the wage base and the band's offset. */
    public double effectiveWageRate(WageBand band) {
        return clamp(wageTaxRate + wageOffset[band.ordinal()], MAX_INCOME_TAX);
    }

    /** What this sector's profit is taxed at: the profit base and the sector's offset. */
    public double effectiveProfitRate(String sector) {
        return clamp(profitTaxRate + getProfitOffset(sector), MAX_INCOME_TAX);
    }

    /** What this sector charges on the value it adds: the sales base and its offset. See SalesTaxLedger. */
    public double effectiveSalesRate(String sector) {
        return clamp(salesTaxRate + getSalesOffset(sector), MAX_INCOME_TAX);
    }

    /** ANNUAL property tax rate for this sector. */
    public double effectivePropertyRate(String sector) {
        return clamp(propertyTaxRate + getPropertyOffset(sector), MAX_PROPERTY_TAX);
    }

    /** ...and the monthly one, which is what is actually billed. */
    public double effectiveMonthlyPropertyRate(String sector) {
        return effectivePropertyRate(sector) / 12;
    }

    public double effectiveProfitRate(Sector s)          { return effectiveProfitRate(s.key()); }
    public double effectiveSalesRate(Sector s)           { return effectiveSalesRate(s.key()); }
    public double effectivePropertyRate(Sector s)        { return effectivePropertyRate(s.key()); }
    public double effectiveMonthlyPropertyRate(Sector s) { return effectiveMonthlyPropertyRate(s.key()); }

    /**
     * What one month's property tax comes to on a given assessed value.
     *
     * The sector-blind version, kept for the one caller that has a value but no
     * sector - and for old tests. Prefer propertyTaxOn(value, sector).
     */
    public double propertyTaxOn(double assessedValue) {
        if (assessedValue <= 0) {
            return 0;   // a business that owns nothing owes nothing
        }
        return assessedValue * getMonthlyPropertyTaxRate();
    }

    /** One month's property tax at this sector's own rate. */
    public double propertyTaxOn(double assessedValue, String sector) {
        if (assessedValue <= 0 || sector == null) {
            return propertyTaxOn(assessedValue);
        }
        return assessedValue * effectiveMonthlyPropertyRate(sector);
    }

    /* ==================================================================
       WAGES
       ================================================================== */

    /**
     * The month's wage tax, summed job type by job type at its band's rate.
     *
     * NOT the total wage bill times an average rate. The bands exist so the
     * player can tax a doctor differently from a labourer, and averaging would
     * throw away exactly the distinction they just set - while still LOOKING
     * right, because the total is in the same neighbourhood either way.
     *
     * @param wagePerType the monthly wage bill per JobType, before fill
     * @param fillRate    what share of each type's posts are actually staffed
     */
    public double wageTaxOn(double[] wagePerType, double[] fillRate) {
        double tax = 0;
        for (double t : wageTaxPerTier(wagePerType, fillRate)) tax += t;
        return Math.max(0, tax);
    }

    /**
     * The same wage tax, split across the six pay tiers.
     *
     * The residents' books needed the split so each tier could have its own
     * cash-flow statement, and this is the ONLY place either figure is worked
     * out - wageTaxOn() is now a sum over this array rather than a second loop
     * that does the same thing. That is deliberate and it is not tidiness.
     *
     * The last time this codebase kept a second copy of the wage tax it cost
     * 23%. Game.updateHouseholdAccounts() computed the residents' tax as
     * `wages * incomeTaxRate` under a comment claiming it was "the identical
     * formula EconomyManager uses". It was, the day it was written - and then
     * the Policy tab added per-band offsets in this file only, so the moment a
     * player used the wage bands at all, the households were shown paying a
     * different tax from the one the city collected. With every offset at zero
     * the two agreed exactly, which is precisely why nothing noticed.
     *
     * Banded, and summed job type by job type rather than as one total times an
     * average - averaging would throw away exactly the distinction the bands
     * exist to express while still looking about right.
     */
    public double[] wageTaxPerTier(double[] wagePerType, double[] fillRate) {

        double[] byTier = new double[PayTier.values().length];
        if (wagePerType == null) return byTier;

        for (int i = 0; i < wagePerType.length && i < JobType.values().length; i++) {

            double paid = wagePerType[i];
            if (fillRate != null && i < fillRate.length) {
                paid *= fillRate[i];
            }
            if (paid <= 0) continue;

            JobType type = JobType.values()[i];
            byTier[PayTier.of(type).ordinal()] +=
                    paid * effectiveWageRate(WageBand.of(type));
        }
        return byTier;
    }

    /* ==================================================================
       SAVE AND RESTORE
       ================================================================== */

    /** The slots a save from before the EI and grant dials carried: the four rates and the wage-band offsets. */
    public static final int STATE_BEFORE_EI = 4 + WageBand.values().length;

    /** ...and one from before the health dials of 2026-09-19: EI's three, the farmland relief and the fare on top. */
    public static final int STATE_BEFORE_HEALTH = STATE_BEFORE_EI + 3 + 1 + 1;

    /** ...and one from before the education dials of 2026-09-21: the two health dials on top. */
    public static final int STATE_BEFORE_EDUCATION = STATE_BEFORE_HEALTH + 2;

    /** ...and one from before the income rate split in three (0.7.4): the grant's basis and amount, the loan rate and the tuition scale on top. */
    public static final int STATE_BEFORE_SPLIT = STATE_BEFORE_EDUCATION + 4;

    /** ...and one from before the tuition scale split by school (0.7.6): the profit, sales and wage bases on top, each its own slot since 0.7.4. */
    public static final int STATE_BEFORE_SCHOOLS = STATE_BEFORE_SPLIT + 3;

    /** This build's array: a tuition scale per school kind on top, the nine in EducationType order bar NONE, since 0.7.6. */
    public static final int STATE_SLOTS = STATE_BEFORE_SCHOOLS + EducationType.values().length - 1;

    /**
     * The city rates and the wage-band offsets as one array, city rates
     * first - bar the three income bases of 0.7.4, which ride the end like
     * every field added since. ORDER IS THE FORMAT and new fields go on the
     * END. The sector offsets are NOT in here any more: they are keyed by
     * name and saved as their own list - see getSectorOffsets().
     */
    public double[] getPolicyState() {

        int bands = WageBand.values().length;

        // 4 rates, one per wage band, the three dials of 2026-09-11, the
        // farmland relief of 09-13, the transit fare of 09-16, the two
        // health dials of 09-19, the four education dials of 09-21, the
        // three income bases of 0.7.4 and the nine school kinds' tuition
        // scales of 0.7.6. GROW THIS WITH EVERY SLOT ADDED
        // BELOW: the first version of the fare wrote to state[12] of a
        // twelve-long array and took forty-three harnesses down with it in
        // nineteen seconds, which is the good outcome.
        double[] state = new double[STATE_SLOTS];
        int i = 0;
        // The one income rate, as every older reader knows it: the three
        // together while they are equal, the profit rate once they have
        // parted (getIncomeTaxRate()). The three themselves ride the end.
        state[i++] = getIncomeTaxRate();
        state[i++] = propertyTaxRate;
        state[i++] = contributionRate;
        state[i++] = pensionReplacement;
        for (int b = 0; b < bands; b++) state[i++] = wageOffset[b];
        // The three dials of 2026-09-11, on the end. The grant's slot has
        // always meant "the share of the unskilled wage", and still does: it
        // carries the amount under WAGE_SHARE and nothing under any other
        // basis, so a build that reads only this slot reads a grant it can
        // strike and this build reads the basis and its amount from the end.
        state[i++] = eiPremiumRate;
        state[i++] = eiBenefitRate;
        state[i++] = getStudentGrantShare();
        // The farmland dial of 2026-09-13, on the end, for the same reason the
        // three above are on the end: an older save has one fewer slot and
        // keeps the default, which is the behaviour that city had.
        state[i++] = farmlandRelief;
        // The fare of 2026-09-16, on the end, for the reason the four above it
        // are on the end: an older save is one slot shorter and keeps the
        // default, which is the city it was.
        state[i++] = transitFare;
        // The two health dials of 2026-09-19, on the end again: an older save
        // is two slots shorter and reads the founding fee at 1x and no
        // premium, which is the game it was.
        state[i++] = healthFeeScale;
        state[i++] = healthPremiumRate;
        // The four education dials of 2026-09-21, on the end: the basis as
        // its ordinal, its amount, the loan rate and the tuition scale. An
        // older save is four slots shorter and reads WAGE_SHARE at the share
        // its grant slot above carries, no interest and the founding price,
        // which is the game it was.
        state[i++] = grantBasis.ordinal();
        state[i++] = grantAmount;
        state[i++] = studentLoanRate;
        // The one scale, as every older reader knows it: the nine together
        // while they are equal, the first kind's once they have parted
        // (getTuitionScale()). The nine themselves ride the end.
        state[i++] = getTuitionScale();
        // The three income bases of 0.7.4, on the end: an older save is three
        // slots shorter and reads all three as its one income rate in slot 0,
        // which is the city it was.
        state[i++] = profitTaxRate;
        state[i++] = salesTaxRate;
        state[i++] = wageTaxRate;
        // The nine school kinds' tuition scales of 0.7.6, on the end in
        // EducationType order: an older save is nine slots shorter and reads
        // all nine as the one scale its slot above carries, which is the
        // city it was.
        for (EducationType type : SCHOOL_KINDS) state[i++] = tuitionScales[type.ordinal()];
        return state;
    }

    /**
     * @return false if the array is shorter than the oldest shape
     *         (STATE_BEFORE_EI) or longer than this build's (STATE_SLOTS);
     *         nothing is changed. Every append has gone on the end, so any
     *         older save is a PREFIX of this build's array: what is there is
     *         read in order and every slot past its end keeps its default,
     *         which is the city that save was.
     */
    public boolean restorePolicyState(double[] state) {

        if (state == null || state.length < STATE_BEFORE_EI || state.length > STATE_SLOTS) return false;

        int i = 0;
        // All three income bases at the one rate slot 0 carries; a save from
        // 0.7.4 on overrides each from the end of the array below.
        setIncomeTaxRate(state[i++]);
        setPropertyTaxRate(state[i++]);
        setContributionRate(state[i++]);
        setPensionReplacement(state[i++]);
        for (WageBand b : WageBand.values()) setWageOffset(b, state[i++]);
        eiPremiumRate = Unemployment.DEFAULT_PREMIUM_RATE;
        eiBenefitRate = Unemployment.DEFAULT_BENEFIT_RATE;
        grantBasis = DEFAULT_GRANT_BASIS;
        grantAmount = DEFAULT_STUDENT_GRANT_SHARE;
        farmlandRelief = DEFAULT_FARMLAND_RELIEF;
        transitFare = DEFAULT_TRANSIT_FARE;
        healthFeeScale = DEFAULT_HEALTH_FEE_SCALE;
        healthPremiumRate = DEFAULT_HEALTH_PREMIUM;
        studentLoanRate = DEFAULT_STUDENT_LOAN_RATE;
        setTuitionScale(DEFAULT_TUITION_SCALE);
        if (i < state.length) setEiPremiumRate(state[i++]);
        if (i < state.length) setEiBenefitRate(state[i++]);
        // The wage share, as the slot always carried it: WAGE_SHARE at that
        // share, and the basis and amount four slots on override it when
        // the save has them.
        if (i < state.length) setStudentGrantShare(state[i++]);
        if (i < state.length) setFarmlandRelief(state[i++]);
        if (i < state.length) setTransitFare(state[i++]);
        if (i < state.length) setHealthFeeScale(state[i++]);
        if (i < state.length) setHealthPremiumRate(state[i++]);
        if (i < state.length) {
            int ordinal = (int) Math.round(state[i++]);
            GrantBasis[] bases = GrantBasis.values();
            grantBasis = ordinal >= 0 && ordinal < bases.length ? bases[ordinal] : DEFAULT_GRANT_BASIS;
        }
        if (i < state.length) setGrantAmount(state[i++]);
        if (i < state.length) setStudentLoanRate(state[i++]);
        // Every school at the one scale this slot carries; a save from 0.7.6
        // on overrides each kind from the end of the array below.
        if (i < state.length) setTuitionScale(state[i++]);
        if (i < state.length) setProfitTaxRate(state[i++]);
        if (i < state.length) setSalesTaxRate(state[i++]);
        if (i < state.length) setWageTaxRate(state[i++]);
        for (EducationType type : SCHOOL_KINDS) {
            if (i < state.length) setTuitionScaleOf(type, state[i++]);
        }
        return true;
    }

    /** One sector's three offsets, as the save carries them. */
    public static final class SectorOffsets {
        public String sector;
        public double profit, sales, property;
    }

    public java.util.List<SectorOffsets> getSectorOffsets() {
        java.util.Set<String> keys = new java.util.LinkedHashSet<>();
        keys.addAll(profitOffset.keySet());
        keys.addAll(salesOffset.keySet());
        keys.addAll(propertyOffset.keySet());
        java.util.List<SectorOffsets> out = new java.util.ArrayList<>();
        for (String k : keys) {
            SectorOffsets o = new SectorOffsets();
            o.sector = k;
            o.profit = getProfitOffset(k);
            o.sales = getSalesOffset(k);
            o.property = getPropertyOffset(k);
            out.add(o);
        }
        return out;
    }

    /** A sector the build does not have keeps its row - harmless, and it comes back if the sector does. */
    public void restoreSectorOffsets(java.util.List<SectorOffsets> saved) {
        profitOffset.clear();
        salesOffset.clear();
        propertyOffset.clear();
        if (saved == null) return;
        for (SectorOffsets o : saved) {
            if (o == null || o.sector == null) continue;
            setProfitOffset(o.sector, o.profit);
            setSalesOffset(o.sector, o.sales);
            setPropertyOffset(o.sector, o.property);
        }
    }

    public void reset() {
        setIncomeTaxRate(DEFAULT_INCOME_TAX);
        propertyTaxRate = DEFAULT_PROPERTY_TAX;
        transitFare = DEFAULT_TRANSIT_FARE;
        contributionRate = SocialSecurity.DEFAULT_CONTRIBUTION_RATE;
        pensionReplacement = SocialSecurity.DEFAULT_PENSION_REPLACEMENT;
        eiPremiumRate = Unemployment.DEFAULT_PREMIUM_RATE;
        eiBenefitRate = Unemployment.DEFAULT_BENEFIT_RATE;
        grantBasis = DEFAULT_GRANT_BASIS;
        grantAmount = DEFAULT_STUDENT_GRANT_SHARE;
        studentLoanRate = DEFAULT_STUDENT_LOAN_RATE;
        setTuitionScale(DEFAULT_TUITION_SCALE);
        farmlandRelief = DEFAULT_FARMLAND_RELIEF;
        healthFeeScale = DEFAULT_HEALTH_FEE_SCALE;
        healthPremiumRate = DEFAULT_HEALTH_PREMIUM;
        java.util.Arrays.fill(wageOffset, 0);
        profitOffset.clear();
        salesOffset.clear();
        propertyOffset.clear();
    }

    private double clamp(double rate, double max) {
        if (rate < 0) {
            return 0;
        }
        return Math.min(rate, max);
    }

    private double clampOffset(double points) {
        if (Double.isNaN(points)) return 0;
        return Math.max(-MAX_OFFSET, Math.min(points, MAX_OFFSET));
    }
}
