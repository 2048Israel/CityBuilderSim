package ham.citybuildersim;

/**
 * The city's residents, treated as one household.
 *
 * Every other participant in this economy had books before this: shops, mills,
 * landlords, builders, the utilities, the city itself. The people did not, even
 * though they are the largest single flow in the game - they earn every wage
 * paid and they are the customer at the other end of every rent cheque and
 * every till.
 *
 * WHERE THE NUMBERS COME FROM
 *
 * Nothing here is estimated or re-derived. Wages are the figure PopulationManager
 * already computes for filled jobs; the wage tax is what the city already
 * collects; rent and shopping are the two halves of consumption that
 * NationalAccounts already measures, because the money households spend IS
 * consumption. Building this account is a matter of putting existing figures on
 * the other side of the ledger from the businesses that receive them.
 *
 * WHAT IT IS FOR
 *
 * It answers a question nothing else in the game could: can the people afford
 * to live here? Retail spending is currently driven by how many residents there
 * are rather than by what they earn - there is no budget constraint anywhere in
 * the model - so households CAN be made to spend more than they take home, and
 * that shortfall would be money appearing from nowhere. Now it is a visible
 * negative line rather than an invisible one.
 *
 * AND NOW, SEVEN SETS OF BOOKS
 *
 * The note that used to sit here said splitting residents into income groups was
 * "the obvious next step" and "a long way off". It is here: the city total, plus
 * one statement per pay tier and one for the retired, who have no tier because
 * they have no earner.
 *
 * NOTHING IN THE SPLIT IS ESTIMATED, which is the only reason it is worth having.
 * Wages come from the job mix by tier. The tax is the SAME banded calculation the
 * city collects, split rather than re-derived. Rent is charged per resident at a
 * uniform price, and shopping is driven by headcount, so allocating both by the
 * people living in each tier's households is exact arithmetic rather than an
 * apportionment.
 *
 * That last point is also the finding. Income across the tiers varies about
 * tenfold; rent and shopping per head do not vary at all, because nothing in the
 * model lets what a household earns affect what it spends. So the poorest tier
 * runs a deficit and the richest banks almost everything, and both are artefacts
 * of a missing budget constraint rather than results. The screen says so.
 */
public class HouseholdAccounts {

    /* --------------------------- this month --------------------------- */
    private double wages;
    private double wageTax;
    private double rent;
    private double shopping;

    /**
     * Pension contributions off the workers, and pensions in to the retired.
     *
     * Two flows in opposite directions between the same two parties, and they
     * must NOT be netted into one line. A worker seeing 5.95% gone from their
     * pay and a pensioner seeing money arrive are different facts about
     * different households, and the whole point of splitting these books by tier
     * was to stop facts about one group being averaged into another.
     */
    private double contributions;
    private double pensions;

    /*
     * THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11): the EI premium off every
     * wage, the EI benefit to the out of work, and the grant to the students.
     * Two flows in opposite directions again, and kept apart for the reason
     * the pension lines are.
     */
    private double eiPremiums;
    private double eiBenefits;
    private double studentGrants;

    /**
     * THE HEALTH PREMIUM (2026-09-19): a share of every wage into the
     * treasury, employee side, in the EI premium's shape. Jerus: "the option
     * to make it an obligatory insurance payment system". Zero until the
     * player sets one, and a save from before it reads zero.
     */
    private double healthPremiums;

    /**
     * The month's EI and grants, from the same figures the treasury books -
     * set before update() or refresh(), which leave them as they are.
     */
    public void setOutsideMoney(double eiPremiums, double eiBenefits, double studentGrants) {
        setOutsideMoney(eiPremiums, eiBenefits, studentGrants, 0);
    }

    /** ...and the health premium beside them, from the figure the treasury books (2026-09-19). */
    public void setOutsideMoney(double eiPremiums, double eiBenefits, double studentGrants,
                                double healthPremiums) {
        this.eiPremiums = Math.max(0, eiPremiums);
        this.eiBenefits = Math.max(0, eiBenefits);
        this.studentGrants = Math.max(0, studentGrants);
        this.healthPremiums = Math.max(0, healthPremiums);
    }

    public double getEiPremiums()    { return eiPremiums; }
    public double getEiBenefits()    { return eiBenefits; }
    public double getStudentGrants() { return studentGrants; }

    /** What the people paid the treasury as health premium this month, off their wages. */
    public double getHealthPremiums() { return healthPremiums; }

    /**
     * What the people paid the healthcare service this month.
     *
     * Fees and funerals together, on one line, because they are one bill from
     * the household's point of view - and because the alternative was to credit
     * the city with fee revenue and debit nobody, which is precisely the shape
     * of money-from-nowhere this account exists to make visible.
     */
    private double healthcare;
    private double tuition;
    private double interest;

    /* =====================================================================
       THE FARE (2026-09-16)

       WHAT THIS LINE IS FIXING, and it is not a car mechanic. Transit has been
       in the game since the modes went in, and the fare it charges has been
       CREDITED TO THE CITY AND DEBITED TO NOBODY the whole time: the money
       arrived in the treasury out of thin air, exactly the failure the note on
       the health fees in Game.syncHouseholdAccounts() says these books exist to
       catch, and exactly the failure that note says this codebase has produced
       before.

       WHY NOBODY SAW IT. Nothing had ever built a bus. The playtest advisor
       answered every jam with tarmac - the road throttle offered three roads
       and no transit - so four thousand months a seed, eight seeds a run, for
       as long as transit has existed, not one city ever carried a passenger.
       The first ensemble in which the advisor bought a Bus Network failed the
       money audit on all eight seeds within a few hundred months, and the
       residual was the fare to six decimals: 2,186 riders at $2.50 is $5.47,
       and the audit said 5.465207.

       A HARNESS CANNOT CHECK A THING NOBODY DOES. That is the useful half of
       this and it is worth more than the fix: a mechanic with no player is a
       mechanic with no test, however many assertions surround it.

       AND IT IS A FEE, NOT A PRICE, AS FAR AS THESE BOOKS GO. It sits with the
       health fee and the tuition rather than with the shopping - deducted
       before the food money is counted, split across the rows the way the
       clinic's fees are - because that is what it is: a fixed monthly charge
       on everybody who rides.
       ===================================================================== */
    private double fares;

    /** What the city took in fares this month, told to the households who paid it. */
    public void setTransitFares(double paid) {
        this.fares = Math.max(0, paid);
    }

    public double getFares() { return fares; }

    private int population;
    private int workforce;
    private int jobsFilled;

    /**
     * Everything households have not spent, accumulated.
     *
     * Not a pot anyone can draw on - nothing in the game lets residents spend
     * savings, and it must not, because that money has no counterparty holding
     * it. It is a running total of the gap between what the people earned and
     * what they paid out, which is the honest way to show whether the city's
     * wages have kept up with its prices over time.
     */
    private double cumulativeSaving;

    /** Feed it the month. Every argument is a figure someone else already had. */
    public void update(double wages, double wageTax, double rent, double shopping,
                       int population, int workforce, int jobsFilled) {
        update(wages, wageTax, rent, shopping, 0, 0, population, workforce, jobsFilled);
    }

    public void update(double wages, double wageTax, double rent, double shopping,
                       double contributions, double pensions,
                       int population, int workforce, int jobsFilled) {
        update(wages, wageTax, rent, shopping, contributions, pensions, 0,
                population, workforce, jobsFilled);
    }

    public void update(double wages, double wageTax, double rent, double shopping,
                       double contributions, double pensions, double healthcare,
                       int population, int workforce, int jobsFilled) {
        update(wages, wageTax, rent, shopping, contributions, pensions, healthcare,
                0, 0, population, workforce, jobsFilled);
    }

    /**
     * @param tuition  what the households paid the schools, net of subsidy - the
     *                 same figure Education collects, not a second copy of it
     * @param interest what they paid the lender on household debt
     */
    public void update(double wages, double wageTax, double rent, double shopping,
                       double contributions, double pensions, double healthcare,
                       double tuition, double interest,
                       int population, int workforce, int jobsFilled) {

        assign(wages, wageTax, rent, shopping, contributions, pensions, healthcare,
                tuition, interest, population, workforce, jobsFilled);
        cumulativeSaving += getNetSaving();
    }

    /**
     * The same figures, WITHOUT adding a month to the running total.
     *
     * For the load path. rebuildSimulationState() has to repopulate this
     * statement or every row on the People screen reads $0 after a reload - but
     * cumulativeSaving is history, it was restored from the save, and calling
     * update() would book the same month onto it a second time.
     *
     * Splitting the two is the honest fix. A month's statement is derived state
     * and can be rebuilt; a running total is a fact about every month that came
     * before and cannot.
     */
    public void refresh(double wages, double wageTax, double rent, double shopping,
                        double contributions, double pensions,
                        int population, int workforce, int jobsFilled) {
        refresh(wages, wageTax, rent, shopping, contributions, pensions, 0,
                population, workforce, jobsFilled);
    }

    public void refresh(double wages, double wageTax, double rent, double shopping,
                        double contributions, double pensions, double healthcare,
                        int population, int workforce, int jobsFilled) {
        refresh(wages, wageTax, rent, shopping, contributions, pensions, healthcare,
                0, 0, population, workforce, jobsFilled);
    }

    public void refresh(double wages, double wageTax, double rent, double shopping,
                        double contributions, double pensions, double healthcare,
                        double tuition, double interest,
                        int population, int workforce, int jobsFilled) {

        assign(wages, wageTax, rent, shopping, contributions, pensions, healthcare,
                tuition, interest, population, workforce, jobsFilled);
    }

    private void assign(double wages, double wageTax, double rent, double shopping,
                        double contributions, double pensions, double healthcare,
                        double tuition, double interest,
                        int population, int workforce, int jobsFilled) {

        this.healthcare = healthcare;
        this.tuition = tuition;
        this.interest = interest;
        this.wages = wages;
        this.wageTax = wageTax;
        this.rent = rent;
        this.shopping = shopping;
        this.contributions = contributions;
        this.pensions = pensions;

        this.population = population;
        this.workforce = workforce;
        this.jobsFilled = jobsFilled;
    }

    /* ----------------------------- the statement ----------------------------- */

    public double getWages()         { return wages; }
    public double getWageTax()       { return wageTax; }
    public double getRent()          { return rent; }
    public double getShopping()      { return shopping; }
    public double getContributions() { return contributions; }
    public double getPensions()      { return pensions; }
    public double getHealthcare()    { return healthcare; }
    public double getTuition()       { return tuition; }
    public double getInterest()      { return interest; }

    /**
     * What the people actually have to spend after the city has taken its share.
     *
     * Pensions are income and contributions are a deduction, so both belong
     * here rather than beside the rent. A pensioner's whole income is on this
     * line; a worker's is this line minus a seventeenth.
     */
    public double getDisposableIncome() {
        return wages - wageTax - contributions - eiPremiums - healthPremiums
                + pensions + eiBenefits + studentGrants;
    }

    /**
     * Everything that leaves a household in a month.
     *
     * ALL OF IT, since 2026-09-07 (Jerus: "have it show all costs, including
     * tuition and healthcare"). Tuition and the interest on household debt were
     * both real money leaving real households and neither appeared on the
     * statement - so the bottom line said a city was saving when its families
     * were paying school fees out of savings they did not have.
     */
    public double getSpending() {
        return rent + shopping + healthcare + tuition + fares + interest;
    }

    /** Income less tax less everything paid out. Negative means living beyond it. */
    public double getNetSaving() {
        return getDisposableIncome() - getSpending();
    }

    public double getCumulativeSaving() {
        return cumulativeSaving;
    }

    /** For the load path. The running total is history and has to survive a save. */
    public void setCumulativeSaving(double value) {
        this.cumulativeSaving = value;
    }

    /**
     * Saving as a share of take-home pay.
     *
     * The single number worth watching. Real household saving rates sit around
     * 5-15%; a city pushed to 0 is one where the rent and the shops have taken
     * everything, and a negative one is not sustainable in any economy.
     */
    public double getSavingRate() {
        double disposable = getDisposableIncome();
        return (disposable > 0) ? getNetSaving() / disposable : 0;
    }

    /** Rent as a share of take-home. The affordability figure everyone knows. */
    public double getRentBurden() {
        double disposable = getDisposableIncome();
        return (disposable > 0) ? rent / disposable : 0;
    }

    public double getEffectiveTaxRate() {
        return (wages > 0) ? wageTax / wages : 0;
    }

    /* ------------------------------- per head ------------------------------- */

    public double getIncomePerResident() {
        return (population > 0) ? (wages + pensions) / population : 0;
    }

    public double getSpendingPerResident() {
        return (population > 0) ? getSpending() / population : 0;
    }

    /** What a filled job pays on average. Not the same as income per resident. */
    public double getAverageWage() {
        return (jobsFilled > 0) ? wages / jobsFilled : 0;
    }

    /** How many people each working resident is carrying, themselves included. */
    public double getDependencyRatio() {
        return (workforce > 0) ? population / (double) workforce : 0;
    }

    public int getPopulation() { return population; }
    public int getWorkforce()  { return workforce; }
    public int getJobsFilled() { return jobsFilled; }

    /** True when the people are being made to spend more than they earn. */
    public boolean isLivingBeyondIncome() {
        return getNetSaving() < 0;
    }

    /* =====================================================================
       THE SAME STATEMENT, PER PAY TIER

       Seven rows: the six tiers, plus the retired at the end. Held as parallel
       arrays rather than seven objects because every row is the same four
       figures and the screen walks them in order; a class per row would be
       ceremony around an array index.

       The retired row is not an afterthought. Pensioners have NO income in this
       model - there is no pension, no savings drawdown, nothing - so their row
       is pure outgoing, and their deficit is the largest single thing on the
       screen. That is a real gap in the game, and putting it in the same table
       as everyone else is how it stops being invisible.
       ===================================================================== */

    /** Index of the retired row, which sits after the six tiers. */
    public static final int RETIRED = PayTier.values().length;

    /** The out of work, the students and the orphans, after the retired - Household's rows. */
    public static final int UNEMPLOYED = Household.UNEMPLOYED_ROW;
    public static final int STUDENTS = Household.STUDENT_ROW;
    public static final int ORPHANS = Household.ORPHAN_ROW;
    /** ...and the prisoners, since 2026-09-11 (night). */
    public static final int PRISONERS = Household.PRISON_ROW;
    private static final int ROWS = Household.ROWS;

    /** The rows a save from before 2026-09-11 carries: the tiers and the retired. */
    private static final int ROWS_BEFORE_OUTSIDE = RETIRED + 1;

    private final double[] rowWages     = new double[ROWS];
    private final double[] rowTax       = new double[ROWS];
    private final double[] rowRent      = new double[ROWS];
    private final double[] rowShopping  = new double[ROWS];
    private final double[] rowPeople    = new double[ROWS];
    private final double[] rowHouseholds = new double[ROWS];
    private final double[] rowContributions = new double[ROWS];
    private final double[] rowPensions      = new double[ROWS];
    private final double[] rowHealthcare    = new double[ROWS];
    private final double[] rowTuition       = new double[ROWS];
    private final double[] rowFares         = new double[ROWS];
    private final double[] rowInterest      = new double[ROWS];
    private final double[] rowEiPremiums    = new double[ROWS];
    /**
     * Doors each row's households pay for: one a household, a fifth sharing,
     * none with no home. What the rent is split by since 2026-09-11; the
     * household count itself is rowHouseholds.
     */
    private final double[] rowDoors         = new double[ROWS];
    /** EI to the out of work, grants to the students. */
    private final double[] rowBenefits      = new double[ROWS];
    /** The health premium, a slice off the same payslip the EI premium comes off (2026-09-19). */
    private final double[] rowHealthPremiums = new double[ROWS];

    /* =====================================================================
       WHO PAID FOR CARE, AND WHO WAS TURNED AWAY (2026-09-19)

       The clinic's fees follow heads, below - but since a household can be
       priced out of care, not every head paid. HouseholdBalance strikes, per
       cell, the share of its people who could afford the fee (see
       Household.affordCare()), and hands it here per row; the fee bill is
       then split over the heads WHO PAID, so a row of households the price
       turned away is not billed for the care it did not get. A row's share
       at 1.0 multiplies to exactly what it was, so a city where everybody
       pays splits its bill to the bit as before.

       And the TREATMENT bill beside it, twice over, for the test each
       household makes of its own means next month: what was charged for
       treatment, split over the heads who paid it, and what would have been
       charged had every one of them paid, split over every head. Treatment
       only: the funeral levy is on the same statement line but a fee of
       nothing must leave nobody priced out, and the dead are not a price at
       the clinic door.
       ===================================================================== */

    /** Of each row's people, the share who paid for care last month; null is everybody. */
    private double[] carePaid;

    /** The treatment fees the households were billed - Healthcare.getTreatmentFees() - and the same at full service. */
    private double careBilled, careFull;

    /** ...and each by row: the billed over the heads who paid, the full over every head. */
    private final double[] rowCareBilled = new double[ROWS];
    private final double[] rowCareFull = new double[ROWS];

    /**
     * Tells the split who paid for care: the share of each row's people the
     * clinic's fee did not turn away, from HouseholdBalance.carePaidShare().
     * Set before updateByTier(); null, or a missing row, means everybody paid.
     */
    public void setCarePaid(double[] shareByRow) { this.carePaid = shareByRow; }

    /**
     * Tells the split the month's treatment bill, from Healthcare: what was
     * charged (getTreatmentFees) and what would have been at full service
     * (fullTreatmentFees). Set before updateByTier().
     */
    public void setCareBills(double treatmentBilled, double treatmentAtFullService) {
        this.careBilled = Math.max(0, treatmentBilled);
        this.careFull = Math.max(0, treatmentAtFullService);
    }

    private double carePaidOf(int row) {
        return carePaid == null || row >= carePaid.length ? 1
                : Math.max(0, Math.min(1, carePaid[row]));
    }

    /** The treatment fees this row's people were billed, over the heads who paid. */
    public double getRowCareBilled(int row) { return rowCareBilled[row]; }

    /** The treatment fees this row would have been billed had every one of its people paid. */
    public double getRowCareFull(int row) { return rowCareFull[row]; }

    /**
     * Splits the month across the tiers.
     *
     * Called straight after update(), with the totals it was given.
     *
     * SHOPPING FOLLOWS PEOPLE, RENT FOLLOWS HOUSEHOLDS, and getting that wrong
     * was worth a screen. Both were shared out by headcount, on a comment that
     * said rent is `residents * rentPrice` - which stopped being true when
     * dwellings arrived. CommercialHandler.getRentIncome() charges
     * `let homes * averageHomeSize * rentPrice`: the same figure for every let
     * home, whoever is in it, because that is what a landlord charges for - the
     * flat. Splitting it by headcount then billed a family of five five times
     * what it billed a single adult in the identical flat, and made every large
     * household look like it could not afford to live here.
     *
     * Jerus, 2026-09-07: "rent should be charged by household not by head, and
     * grocery is per head." Which is what the engine already did and the books
     * did not.
     *
     * @param wagesPerTier what each tier earned, staffed
     * @param taxPerTier   the banded wage tax on it, split not re-derived
     * @param peoplePerRow residents in each row's households, retired last
     * @param housePerRow  households in each row, retired last
     */
    public void updateByTier(double[] wagesPerTier, double[] taxPerTier,
                             double[] peoplePerRow, double[] housePerRow) {
        updateByTier(wagesPerTier, taxPerTier, peoplePerRow, housePerRow, null, null);
    }

    /**
     * @param spendShare  each row's share of the month's shopping, from
     *                    HouseholdBalance - who could AFFORD to shop, not who
     *                    was hungry. Null falls back to headcount, which is what
     *                    the model did before there was a budget constraint.
     * @param interestPerRow what each row paid the lender on household debt
     */
    public void updateByTier(double[] wagesPerTier, double[] taxPerTier,
                             double[] peoplePerRow, double[] housePerRow,
                             double[] spendShare, double[] interestPerRow) {
        updateByTier(wagesPerTier, taxPerTier, peoplePerRow, housePerRow, spendShare,
                interestPerRow, null);
    }

    /**
     * @param doorsPerRow the doors each row pays rent on - see rowDoors - or
     *                    null for one door a household, as before
     */
    public void updateByTier(double[] wagesPerTier, double[] taxPerTier,
                             double[] peoplePerRow, double[] housePerRow,
                             double[] spendShare, double[] interestPerRow,
                             double[] doorsPerRow) {

        java.util.Arrays.fill(rowWages, 0);
        java.util.Arrays.fill(rowTax, 0);
        java.util.Arrays.fill(rowRent, 0);
        java.util.Arrays.fill(rowShopping, 0);
        java.util.Arrays.fill(rowPeople, 0);
        java.util.Arrays.fill(rowHouseholds, 0);
        java.util.Arrays.fill(rowContributions, 0);
        java.util.Arrays.fill(rowPensions, 0);
        java.util.Arrays.fill(rowHealthcare, 0);
        java.util.Arrays.fill(rowTuition, 0);
        java.util.Arrays.fill(rowFares, 0);
        java.util.Arrays.fill(rowInterest, 0);
        java.util.Arrays.fill(rowEiPremiums, 0);
        java.util.Arrays.fill(rowBenefits, 0);
        java.util.Arrays.fill(rowDoors, 0);
        java.util.Arrays.fill(rowHealthPremiums, 0);
        java.util.Arrays.fill(rowCareBilled, 0);
        java.util.Arrays.fill(rowCareFull, 0);

        // A caller from before the people outside the families had rows hands
        // seven, and one from before the prisons ten; the missing rows are
        // empty for it.
        peoplePerRow = padOlder(peoplePerRow);
        housePerRow = padOlder(housePerRow);
        interestPerRow = padOlder(interestPerRow);
        spendShare = padOlder(spendShare);

        if (peoplePerRow == null || peoplePerRow.length != ROWS
                || housePerRow == null || housePerRow.length != ROWS) {
            return;   // refused whole, per the standing rule on state arrays
        }

        double heads = 0;
        for (double n : peoplePerRow) heads += n;
        // The orphans are served and charged nothing - nobody can pay for
        // them - so the fees fall on the people who can, as they did before
        // the orphans were counted (2026-09-11). Nor are the prisoners: the
        // prison treats them, on its own upkeep.
        double payingHeads = heads - Math.max(0, peoplePerRow[ORPHANS])
                - Math.max(0, peoplePerRow[PRISONERS]);
        /*
         * ...AND THE HEADS WHO ACTUALLY PAID FOR CARE (2026-09-19): each row's
         * people times the share of them the fee did not turn away, summed in
         * the same order and less the same two rows, so that at a share of
         * exactly 1.0 this is the figure above to the bit.
         */
        double[] carePayingPeople = new double[ROWS];
        double careHeads = 0;
        for (int r = 0; r < ROWS; r++) {
            carePayingPeople[r] = peoplePerRow[r] * carePaidOf(r);
            careHeads += carePayingPeople[r];
        }
        double carePayingHeads = careHeads - Math.max(0, carePayingPeople[ORPHANS])
                - Math.max(0, carePayingPeople[PRISONERS]);
        for (int r = 0; r < ROWS; r++) {
            rowDoors[r] = doorsPerRow != null && r < doorsPerRow.length
                    ? Math.max(0, doorsPerRow[r]) : housePerRow[r];
        }
        double doors = 0;
        for (double n : rowDoors) doors += n;

        for (int r = 0; r < ROWS; r++) {
            rowPeople[r] = peoplePerRow[r];
            rowHouseholds[r] = housePerRow[r];

            if (r < PayTier.values().length) {
                if (wagesPerTier != null && r < wagesPerTier.length) rowWages[r] = wagesPerTier[r];
                if (taxPerTier != null && r < taxPerTier.length)     rowTax[r]   = taxPerTier[r];
            }

            double share = heads > 0 ? peoplePerRow[r] / heads : 0;
            double doorShare = doors > 0 ? rowDoors[r] / doors : 0;
            rowRent[r] = rent * doorShare;

            /*
             * THE SHOP FOLLOWS THE BUDGET, not the headcount.
             *
             * This is the whole of the budget constraint as it reaches the
             * books: HouseholdBalance decides how much each row could afford to
             * spend, retail sells what it could, and the takings are split back
             * by who did the spending. A tier that cannot pay now shows up as
             * BUYING LESS, which is a fact about the world, instead of as an
             * unexplained deficit, which was a fact about the model.
             */
            rowShopping[r] = shopping * (spendShare != null && r < spendShare.length
                    ? spendShare[r] : share);

            /*
             * TUITION IS THE STUDENTS' (2026-09-11). Jerus: "students pay it".
             * It used to follow headcount, because the model had no per-tier
             * student count to be exact with; the students are a row now, so
             * the whole bill is theirs. A caller that hands no student row
             * keeps the old split.
             */
            rowTuition[r] = peoplePerRow[STUDENTS] > 0
                    ? (r == STUDENTS ? tuition : 0)
                    : tuition * share;
            rowInterest[r] = interestPerRow != null && r < interestPerRow.length
                    ? interestPerRow[r] : 0;

            /*
             * Healthcare follows PEOPLE, like rent and shopping and for the same
             * reason: the fees are charged per person served and the funeral
             * fees per body, so spreading them by headcount is the same
             * arithmetic read backwards rather than an allocation rule invented
             * here. It is not exact - a tier with more children pays more
             * childcare in reality - but the model has no per-tier child count
             * to be exact WITH, and inventing one would be an estimate wearing
             * a fact's clothes.
             */
            rowHealthcare[r] = r == ORPHANS || r == PRISONERS || carePayingHeads <= 0 ? 0
                    : healthcare * carePayingPeople[r] / carePayingHeads;
            // ...and the treatment part of it, over the same heads, and what
            // it would have been had all of the row's people paid.
            rowCareBilled[r] = r == ORPHANS || r == PRISONERS || carePayingHeads <= 0 ? 0
                    : careBilled * carePayingPeople[r] / carePayingHeads;
            rowCareFull[r] = r == ORPHANS || r == PRISONERS || payingHeads <= 0 ? 0
                    : careFull * peoplePerRow[r] / payingHeads;
            /*
             * The fare follows the same heads as the clinic's fees, and with
             * the same two exemptions: an orphan and a prisoner pay for
             * nothing, so what they would have paid falls on the people who
             * can. It is not exact - a retired household rides less than a
             * commuting one - but the model has no per-row ridership to be
             * exact WITH, and inventing one would be an estimate wearing a
             * fact's clothes. The same sentence the healthcare split makes.
             */
            rowFares[r] = r == ORPHANS || r == PRISONERS || payingHeads <= 0 ? 0
                    : fares * peoplePerRow[r] / payingHeads;
        }

        /*
         * Contributions follow WAGES, not people - it is a slice off a payslip,
         * so a tier that earns nothing contributes nothing. The pension goes
         * entirely to the retired row, which is the whole point of it: that row
         * read "earned $0" before this existed.
         */
        double totalWages = 0;
        for (double w : rowWages) totalWages += w;
        for (int r = 0; r < ROWS; r++) {
            rowContributions[r] = totalWages > 0
                    ? contributions * (rowWages[r] / totalWages) : 0;
            // The EI premium is a slice off the same payslip.
            rowEiPremiums[r] = totalWages > 0
                    ? eiPremiums * (rowWages[r] / totalWages) : 0;
            // ...and so is the health premium (2026-09-19).
            rowHealthPremiums[r] = totalWages > 0
                    ? healthPremiums * (rowWages[r] / totalWages) : 0;
        }
        rowPensions[RETIRED] = pensions;
        rowBenefits[UNEMPLOYED] = eiBenefits;
        rowBenefits[STUDENTS] = studentGrants;
    }

    /* =====================================================================
       ONE HOUSEHOLD OF A GIVEN SHAPE, AT A GIVEN TIER

       The tier rows above average across every shape in the tier, and the
       average is the one household nobody lives in: an unskilled single adult
       and an unskilled large family are the same row, and they are the two
       households whose books differ most. Jerus asked for the split, and this
       is where it belongs rather than in the screen - the allocation rule is
       documented six inches above, and a screen that restated it would be wrong
       the first time the rule changed.

       Every figure is derived from the SAME per-unit rates the tier split uses:
       rent per door, shopping per head, wages per earner in the tier. Nothing
       here invents an allocation.
       ===================================================================== */

    /** One household's month. All figures in the game's thousands. */
    public record Statement(double households, double people,
                            double income, double tax, double rent,
                            double fees, double shopping, double left) { }

    /** Rent one let home pays, whoever lives in it. Per DOOR since 2026-09-11 - see rowDoors. */
    public double rentPerHousehold() {
        double doors = 0;
        for (double n : rowDoors) doors += n;
        if (doors <= 0) for (double n : rowHouseholds) doors += n;
        return doors > 0 ? rent / doors : 0;
    }

    public double getRowDoors(int row) { return rowDoors[row]; }

    /**
     * How badly a single adult in each tier cannot afford to live alone, 0-1.
     *
     * Jerus, 2026-09-07: "perhaps poor families start living together." Right,
     * and it is the response the model was missing - a household that cannot
     * cover its own front door does not sit there going hungry for twenty
     * months, it gets a flatmate. FamilyModel already knows how to do that; it
     * only ever did it when the city ran out of HOMES. This is the other
     * reason, and the commoner one.
     *
     * 0 means one wage covers a home and a basket with room to spare. 1 means
     * it covers none of it. In between is the share of that tier's single
     * adults who pair up rather than live alone, which is a LEVEL rather than a
     * rate on purpose: FamilyModel rebuilds every household from scratch each
     * month, so anything that had to accumulate would be wiped every tick.
     *
     * Computed here rather than in FamilyModel because it is the household
     * books' arithmetic - rent per door, basket per head, take-home per earner -
     * and FamilyModel has never known what anybody earns.
     */
    public double[] livingAlonePressure(FamilyModel families) {
        double[] out = new double[PayTier.values().length];
        if (families == null) return out;

        double costOfLivingAlone = rentPerHousehold() + shoppingPerHead() + feesPerHead();
        if (costOfLivingAlone <= 0) return out;

        for (PayTier tier : PayTier.values()) {
            int t = tier.ordinal();

            double earners = 0;
            for (FamilyStructure s : FamilyStructure.values()) {
                if (s.isRetired()) continue;
                earners += families.get(s, tier) * s.earners();
            }
            if (earners <= 0) continue;

            double takeHome = (rowWages[t] - rowTax[t] - rowContributions[t]) / earners;
            double shortfall = costOfLivingAlone - takeHome;
            out[t] = Math.max(0, Math.min(1, shortfall / costOfLivingAlone));
        }
        return out;
    }

    /** Healthcare and tuition, per person. Both are charged per head served. */
    public double feesPerHead() {
        double heads = 0;
        for (double n : rowPeople) heads += n;
        return heads > 0 ? (healthcare + tuition + fares) / heads : 0;
    }

    /** What one pensioner receives, as the policy currently sets it. */
    private double pensionPerSenior = SocialSecurity.pensionPerSenior();
    public void setPensionPerSenior(double value) { this.pensionPerSenior = value; }
    public double getPensionPerSenior()           { return pensionPerSenior; }

    /** The weekly shop, per person, which is how retail demand is counted. */
    public double shoppingPerHead() {
        double heads = 0;
        for (double n : rowPeople) heads += n;
        return heads > 0 ? shopping / heads : 0;
    }

    /**
     * How badly one of each group living outside the families cannot afford a
     * door of their own, 0-1 - livingAlonePressure()'s arithmetic on what they
     * live on: EI for the out of work (nothing past the twelfth month), the
     * grant for the students.
     *
     * @param households the group's households, in Seeker order
     */
    public double[] seekerPressure(double[] households) {
        FamilyModel.Seeker[] groups = FamilyModel.Seeker.values();
        double[] out = new double[groups.length];
        double costOfLivingAlone = rentPerHousehold() + shoppingPerHead() + feesPerHead();
        if (costOfLivingAlone <= 0 || households == null) return out;
        for (FamilyModel.Seeker g : groups) {
            int i = g.ordinal();
            if (i >= households.length || households[i] <= 0) continue;
            int row = g == FamilyModel.Seeker.UNEMPLOYED ? UNEMPLOYED : STUDENTS;
            double takeHome = getRowDisposable(row) / households[i];
            out[i] = Math.max(0, Math.min(1, (costOfLivingAlone - takeHome) / costOfLivingAlone));
        }
        return out;
    }

    /**
     * What one household of this shape and tier earns, pays and keeps.
     *
     * @param families the household mix, for how many of these there are and
     *                 how many earners the tier is splitting its wages between
     */
    public Statement statementFor(FamilyModel families, FamilyStructure shape, PayTier tier) {
        double homes = families == null ? 0 : families.get(shape, tier);
        double people = shape.size();

        /*
         * Wages per EARNER, not per household. A couple fields two earners at
         * the same tier and takes home twice what a single adult does, which is
         * most of why the two can afford such different lives - and dividing a
         * tier's wage bill by its households would have hidden exactly that.
         */
        double earnersInTier = 0;
        for (FamilyStructure s : FamilyStructure.values()) {
            if (s.isRetired()) continue;
            earnersInTier += families == null ? 0 : families.get(s, tier) * s.earners();
        }
        double wagePerEarner = earnersInTier > 0
                ? rowWages[tier.ordinal()] / earnersInTier : 0;
        double taxRate = rowWages[tier.ordinal()] > 0
                ? (rowTax[tier.ordinal()] + rowContributions[tier.ordinal()])
                        / rowWages[tier.ordinal()] : 0;

        double wages = shape.earners() * wagePerEarner;
        double pension = (shape.membersOf(AgeBand.SENIOR)
                + shape.membersOf(AgeBand.ELDER)) * pensionPerSenior;
        double income = wages + pension;
        double tax = wages * taxRate;
        double rentDue = rentPerHousehold();

        // Healthcare and tuition follow heads, like the shop; the interest a
        // household pays follows its OWN tier's debt, which is the one figure
        // here that is not a per-head share of a city total.
        double feesDue = people * feesPerHead()
                + (rowHouseholds[tier.ordinal()] > 0
                        ? rowInterest[tier.ordinal()] / rowHouseholds[tier.ordinal()] : 0);
        double shop = people * shoppingPerHead();

        return new Statement(homes, people, income, tax, rentDue, feesDue, shop,
                income - tax - rentDue - feesDue - shop);
    }

    public double getRowWages(int row)      { return rowWages[row]; }
    public double getRowTax(int row)        { return rowTax[row]; }
    public double getRowRent(int row)       { return rowRent[row]; }
    public double getRowShopping(int row)   { return rowShopping[row]; }
    public double getRowFares(int row)      { return rowFares[row]; }
    public double getRowPeople(int row)     { return rowPeople[row]; }
    public double getRowHouseholds(int row) { return rowHouseholds[row]; }
    public int getRowCount()                { return ROWS; }

    /* =====================================================================
       THE MONTH'S STATEMENT, CARRIED

       Seventeen scalars and eighteen row arrays - twelve and eleven when this
       note was written - saved and restored as one, in the order below: new
       fields go on the END and a wrong length is refused
       whole, the same rule CommercialHandler.getReportState() follows and for
       the same reason: a half-restored statement puts a figure on the wrong
       line of a player's screen.

       WHY IT IS CARRIED RATHER THAN REBUILT. It was rebuilt, by
       Game.refreshHouseholdAccounts(), and rebuilding it needs three things a
       reloaded city does not have: the month's retail revenue (which the
       recomputed commercial report reads as zero, because the city has not
       traded yet), the wage bill per tier as it stood when the month was
       struck, and HouseholdBalance's plan - which is a flow, is not saved, and
       decides how the shopping splits across the tiers. Two of those were
       eventually fixed in place; the plan cannot be, because re-striking it
       needs a disposable income that is itself part of what is being rebuilt.

       So the statement is carried, and the rebuild becomes the fallback for a
       save too old to have one. Same conclusion the sector reports reached
       after closing one input at a time: enumerating inputs is a losing game
       when the report has thirty of them.
       ===================================================================== */
    public double[] getStatementState() {
        double[] out = new double[STATE_SCALARS + ROWS * STATE_ROWS];
        int i = 0;
        out[i++] = wages;         out[i++] = wageTax;
        out[i++] = rent;          out[i++] = shopping;
        out[i++] = contributions; out[i++] = pensions;
        out[i++] = healthcare;    out[i++] = tuition;
        out[i++] = interest;
        out[i++] = population;    out[i++] = workforce;   out[i++] = jobsFilled;
        out[i++] = eiPremiums;    out[i++] = eiBenefits;  out[i++] = studentGrants;
        // ...and the fare, on the END, per the rule at the top of this note.
        out[i++] = fares;
        // ...and the health premium, on the end after it (2026-09-19). A save
        // from before it reads zero, which is what that city charged.
        out[i++] = healthPremiums;
        // ...and the health premium's row, and the two treatment bills the
        // next strike measures the households against, on the end (2026-09-19):
        // the rebuild cannot reproduce their split any more than the rest.
        for (double[] row : new double[][] {
                rowWages, rowTax, rowRent, rowShopping, rowPeople, rowHouseholds,
                rowContributions, rowPensions, rowHealthcare, rowTuition, rowInterest,
                rowEiPremiums, rowBenefits, rowDoors, rowFares, rowHealthPremiums,
                rowCareBilled, rowCareFull }) {
            System.arraycopy(row, 0, out, i, ROWS);
            i += ROWS;
        }
        return out;
    }

    /** Scalars and row arrays in the statement's state since 2026-09-19. */
    private static final int STATE_SCALARS = 17, STATE_ROWS = 18;

    /** ...and the shape before the health premium was a line on it (2026-09-19). */
    private static final int SCALARS_BEFORE_HEALTH = 16, ROWS_BEFORE_HEALTH = 15;

    /** ...and the shape before the transit fare was a line on it (2026-09-16). */
    private static final int SCALARS_BEFORE_FARES = 15, ROWS_BEFORE_FARES = 14;

    /**
     * Puts a saved statement back, exactly as it was written.
     *
     * @return false if the array is not this build's shape, in which case
     *         nothing was changed and the caller keeps the rebuilt one
     */
    public boolean restoreStatement(double[] in) {
        /*
         * TODAY'S SHAPE, OR THE ONE FROM BEFORE THE PEOPLE OUTSIDE THE FAMILIES
         * (twelve scalars, eleven arrays of seven rows). The older one restores
         * what it carries; the EI, the grants and the three new rows are zero,
         * which is the month those saves were struck in.
         */
        // ...or the day's shape before the prisoners had a row: the same arrays, one row short.
        /*
         * ...OR THE SHAPE FROM BEFORE THE FARE WAS ON IT, which every save
         * written before 2026-09-16 is. It restores what it carries and the
         * fare reads zero - which is exactly true of a city whose fare was
         * being collected from nobody.
         */
        /*
         * ...OR THE SHAPE FROM BEFORE THE HEALTH PREMIUM WAS ON IT (2026-09-19),
         * which every save written before that day is: one scalar and one row
         * array fewer, and the premium reads zero, which is what that city
         * charged. The same tail-append the fare got, and the same reading.
         */
        boolean beforePrison = in != null
                && (in.length == SCALARS_BEFORE_HEALTH + Household.ROWS_BEFORE_PRISON * ROWS_BEFORE_HEALTH
                    || in.length == SCALARS_BEFORE_FARES + Household.ROWS_BEFORE_PRISON * ROWS_BEFORE_FARES);
        boolean beforeFares = in != null
                && (in.length == SCALARS_BEFORE_FARES + ROWS * ROWS_BEFORE_FARES
                    || in.length == SCALARS_BEFORE_FARES + Household.ROWS_BEFORE_PRISON * ROWS_BEFORE_FARES);
        boolean beforeHealth = beforeFares || beforePrison
                || (in != null && in.length == SCALARS_BEFORE_HEALTH + ROWS * ROWS_BEFORE_HEALTH);
        boolean older = in != null && in.length == 12 + ROWS_BEFORE_OUTSIDE * 11;
        boolean current = in != null
                && (in.length == STATE_SCALARS + ROWS * STATE_ROWS
                    || in.length == SCALARS_BEFORE_HEALTH + ROWS * ROWS_BEFORE_HEALTH
                    || in.length == SCALARS_BEFORE_FARES + ROWS * ROWS_BEFORE_FARES
                    || beforePrison);
        if (!current && !older) return false;
        int rows = beforePrison ? Household.ROWS_BEFORE_PRISON : current ? ROWS : ROWS_BEFORE_OUTSIDE;
        int i = 0;
        wages = in[i++];         wageTax = in[i++];
        rent = in[i++];          shopping = in[i++];
        contributions = in[i++]; pensions = in[i++];
        healthcare = in[i++];    tuition = in[i++];
        interest = in[i++];
        population = (int) Math.round(in[i++]);
        workforce  = (int) Math.round(in[i++]);
        jobsFilled = (int) Math.round(in[i++]);
        eiPremiums = 0; eiBenefits = 0; studentGrants = 0;
        fares = 0;
        healthPremiums = 0;
        if (current) {
            eiPremiums = in[i++]; eiBenefits = in[i++]; studentGrants = in[i++];
            if (!beforeFares) fares = in[i++];
            if (!beforeHealth) healthPremiums = in[i++];
        }
        double[][] arrays = !current
                ? new double[][] { rowWages, rowTax, rowRent, rowShopping, rowPeople, rowHouseholds,
                        rowContributions, rowPensions, rowHealthcare, rowTuition, rowInterest }
                : beforeFares
                ? new double[][] { rowWages, rowTax, rowRent, rowShopping, rowPeople, rowHouseholds,
                        rowContributions, rowPensions, rowHealthcare, rowTuition, rowInterest,
                        rowEiPremiums, rowBenefits, rowDoors }
                : beforeHealth
                ? new double[][] { rowWages, rowTax, rowRent, rowShopping, rowPeople, rowHouseholds,
                        rowContributions, rowPensions, rowHealthcare, rowTuition, rowInterest,
                        rowEiPremiums, rowBenefits, rowDoors, rowFares }
                : new double[][] { rowWages, rowTax, rowRent, rowShopping, rowPeople, rowHouseholds,
                        rowContributions, rowPensions, rowHealthcare, rowTuition, rowInterest,
                        rowEiPremiums, rowBenefits, rowDoors, rowFares, rowHealthPremiums,
                        rowCareBilled, rowCareFull };
        java.util.Arrays.fill(rowEiPremiums, 0);
        java.util.Arrays.fill(rowBenefits, 0);
        java.util.Arrays.fill(rowDoors, 0);
        java.util.Arrays.fill(rowFares, 0);
        java.util.Arrays.fill(rowHealthPremiums, 0);
        java.util.Arrays.fill(rowCareBilled, 0);
        java.util.Arrays.fill(rowCareFull, 0);
        for (double[] row : arrays) {
            java.util.Arrays.fill(row, 0);
            System.arraycopy(in, i, row, 0, rows);
            i += rows;
        }
        return true;
    }

    /** A row array from an older caller, padded to today's rows; anything else as it came. */
    private static double[] padOlder(double[] rows) {
        if (rows == null) return null;
        if (rows.length == ROWS_BEFORE_OUTSIDE || rows.length == Household.ROWS_BEFORE_PRISON) {
            return java.util.Arrays.copyOf(rows, ROWS);
        }
        return rows;
    }

    public double getRowContributions(int row) { return rowContributions[row]; }
    public double getRowPensions(int row)      { return rowPensions[row]; }
    public double getRowHealthcare(int row)    { return rowHealthcare[row]; }
    public double getRowTuition(int row)       { return rowTuition[row]; }
    public double getRowInterest(int row)      { return rowInterest[row]; }
    public double getRowEiPremiums(int row)    { return rowEiPremiums[row]; }
    /** The health premium this row's wages carried (2026-09-19). */
    public double getRowHealthPremiums(int row){ return rowHealthPremiums[row]; }
    /** EI for the out of work, the grant for the students; zero for every other row. */
    public double getRowBenefits(int row)      { return rowBenefits[row]; }

    public double getRowDisposable(int row) {
        return rowWages[row] - rowTax[row] - rowContributions[row] - rowEiPremiums[row]
                - rowHealthPremiums[row] + rowPensions[row] + rowBenefits[row];
    }

    public double getRowSpending(int row) {
        return rowRent[row] + rowShopping[row] + rowHealthcare[row]
                + rowTuition[row] + rowInterest[row];
    }

    public double getRowSaving(int row) {
        return getRowDisposable(row) - getRowSpending(row);
    }

    /** Saving as a share of take-home. Zero income has no rate, only a deficit. */
    public double getRowSavingRate(int row) {
        double disposable = getRowDisposable(row);
        return disposable > 0 ? getRowSaving(row) / disposable : 0;
    }

    public String getRowLabel(int row) {
        if (row == RETIRED)    return "Retired (no earner)";
        if (row == UNEMPLOYED) return "Out of work";
        if (row == STUDENTS)   return "Full-time students";
        if (row == ORPHANS)    return "Orphans";
        if (row == PRISONERS)  return "In prison";
        return PayTier.values()[row].getLabel();
    }

    public void reset() {
        wages = 0;
        wageTax = 0;
        rent = 0;
        shopping = 0;
        contributions = 0;
        pensions = 0;
        eiPremiums = 0;
        eiBenefits = 0;
        studentGrants = 0;
        healthcare = 0;
        population = 0;
        workforce = 0;
        jobsFilled = 0;
        cumulativeSaving = 0;
        java.util.Arrays.fill(rowWages, 0);
        java.util.Arrays.fill(rowTax, 0);
        java.util.Arrays.fill(rowRent, 0);
        java.util.Arrays.fill(rowShopping, 0);
        java.util.Arrays.fill(rowPeople, 0);
        java.util.Arrays.fill(rowHouseholds, 0);
        java.util.Arrays.fill(rowContributions, 0);
        java.util.Arrays.fill(rowPensions, 0);
        java.util.Arrays.fill(rowHealthcare, 0);
        java.util.Arrays.fill(rowTuition, 0);
        java.util.Arrays.fill(rowInterest, 0);
        java.util.Arrays.fill(rowEiPremiums, 0);
        java.util.Arrays.fill(rowBenefits, 0);
        java.util.Arrays.fill(rowDoors, 0);
        java.util.Arrays.fill(rowFares, 0);
        java.util.Arrays.fill(rowHealthPremiums, 0);
        java.util.Arrays.fill(rowCareBilled, 0);
        java.util.Arrays.fill(rowCareFull, 0);
        tuition = 0;
        fares = 0;
        interest = 0;
        healthPremiums = 0;
        careBilled = 0;
        careFull = 0;
        carePaid = null;
    }

    /** The households' income statement, in the new unit. Headcounts and the shares who paid do not move. */
    public void redenominate(double scale) {
        wages *= scale;  wageTax *= scale;  rent *= scale;  shopping *= scale;
        contributions *= scale;  pensions *= scale;
        eiPremiums *= scale;  eiBenefits *= scale;  studentGrants *= scale;
        healthcare *= scale;  tuition *= scale;  interest *= scale;
        fares *= scale;
        healthPremiums *= scale;  careBilled *= scale;  careFull *= scale;
        cumulativeSaving *= scale;
        pensionPerSenior *= scale;
        for (int r = 0; r < ROWS; r++) {
            rowWages[r] *= scale;  rowTax[r] *= scale;  rowRent[r] *= scale;
            rowShopping[r] *= scale;  rowContributions[r] *= scale;
            rowPensions[r] *= scale;  rowHealthcare[r] *= scale;
            rowTuition[r] *= scale;  rowFares[r] *= scale;  rowInterest[r] *= scale;
            rowEiPremiums[r] *= scale;  rowBenefits[r] *= scale;
            rowHealthPremiums[r] *= scale;  rowCareBilled[r] *= scale;  rowCareFull[r] *= scale;
        }
    }

}
