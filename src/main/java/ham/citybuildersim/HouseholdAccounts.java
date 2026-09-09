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
        return wages - wageTax - contributions + pensions;
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
        return rent + shopping + healthcare + tuition + interest;
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
    private static final int ROWS = PayTier.values().length + 1;

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
    private final double[] rowInterest      = new double[ROWS];

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

        if (peoplePerRow == null || peoplePerRow.length != ROWS
                || housePerRow == null || housePerRow.length != ROWS) {
            return;   // refused whole, per the standing rule on state arrays
        }

        double heads = 0;
        for (double n : peoplePerRow) heads += n;
        double doors = 0;
        for (double n : housePerRow) doors += n;

        for (int r = 0; r < ROWS; r++) {
            rowPeople[r] = peoplePerRow[r];
            rowHouseholds[r] = housePerRow[r];

            if (r < PayTier.values().length) {
                if (wagesPerTier != null && r < wagesPerTier.length) rowWages[r] = wagesPerTier[r];
                if (taxPerTier != null && r < taxPerTier.length)     rowTax[r]   = taxPerTier[r];
            }

            double share = heads > 0 ? peoplePerRow[r] / heads : 0;
            double doorShare = doors > 0 ? housePerRow[r] / doors : 0;
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

            // Fees follow people. Healthcare is charged per person served and
            // tuition per student - the model has no per-tier student count, so
            // headcount is the honest approximation rather than an invented one.
            rowTuition[r] = tuition * share;
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
            rowHealthcare[r] = healthcare * share;
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
        }
        rowPensions[RETIRED] = pensions;
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

    /** Rent one let home pays, whoever lives in it. */
    public double rentPerHousehold() {
        double doors = 0;
        for (double n : rowHouseholds) doors += n;
        return doors > 0 ? rent / doors : 0;
    }

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
        return heads > 0 ? (healthcare + tuition) / heads : 0;
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
        double pension = shape.membersOf(AgeBand.SENIOR) * pensionPerSenior;
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
    public double getRowPeople(int row)     { return rowPeople[row]; }
    public double getRowHouseholds(int row) { return rowHouseholds[row]; }
    public int getRowCount()                { return ROWS; }

    /* =====================================================================
       THE MONTH'S STATEMENT, CARRIED

       Twelve scalars and eleven row arrays, saved and restored as one, in the
       order below - new fields go on the END and a wrong length is refused
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
        double[] out = new double[12 + ROWS * 11];
        int i = 0;
        out[i++] = wages;         out[i++] = wageTax;
        out[i++] = rent;          out[i++] = shopping;
        out[i++] = contributions; out[i++] = pensions;
        out[i++] = healthcare;    out[i++] = tuition;
        out[i++] = interest;
        out[i++] = population;    out[i++] = workforce;   out[i++] = jobsFilled;
        for (double[] row : new double[][] {
                rowWages, rowTax, rowRent, rowShopping, rowPeople, rowHouseholds,
                rowContributions, rowPensions, rowHealthcare, rowTuition, rowInterest }) {
            System.arraycopy(row, 0, out, i, ROWS);
            i += ROWS;
        }
        return out;
    }

    /**
     * Puts a saved statement back, exactly as it was written.
     *
     * @return false if the array is not this build's shape, in which case
     *         nothing was changed and the caller keeps the rebuilt one
     */
    public boolean restoreStatement(double[] in) {
        if (in == null || in.length != 12 + ROWS * 11) return false;
        int i = 0;
        wages = in[i++];         wageTax = in[i++];
        rent = in[i++];          shopping = in[i++];
        contributions = in[i++]; pensions = in[i++];
        healthcare = in[i++];    tuition = in[i++];
        interest = in[i++];
        population = (int) Math.round(in[i++]);
        workforce  = (int) Math.round(in[i++]);
        jobsFilled = (int) Math.round(in[i++]);
        for (double[] row : new double[][] {
                rowWages, rowTax, rowRent, rowShopping, rowPeople, rowHouseholds,
                rowContributions, rowPensions, rowHealthcare, rowTuition, rowInterest }) {
            System.arraycopy(in, i, row, 0, ROWS);
            i += ROWS;
        }
        return true;
    }

    public double getRowContributions(int row) { return rowContributions[row]; }
    public double getRowPensions(int row)      { return rowPensions[row]; }
    public double getRowHealthcare(int row)    { return rowHealthcare[row]; }
    public double getRowTuition(int row)       { return rowTuition[row]; }
    public double getRowInterest(int row)      { return rowInterest[row]; }

    public double getRowDisposable(int row) {
        return rowWages[row] - rowTax[row] - rowContributions[row] + rowPensions[row];
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
        return row == RETIRED ? "Retired (no earner)" : PayTier.values()[row].getLabel();
    }

    public void reset() {
        wages = 0;
        wageTax = 0;
        rent = 0;
        shopping = 0;
        contributions = 0;
        pensions = 0;
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
        tuition = 0;
        interest = 0;
    }

    /** The households' income statement, in the new unit. Headcounts do not move. */
    public void redenominate(double scale) {
        wages *= scale;  wageTax *= scale;  rent *= scale;  shopping *= scale;
        contributions *= scale;  pensions *= scale;
        healthcare *= scale;  tuition *= scale;  interest *= scale;
        cumulativeSaving *= scale;
        pensionPerSenior *= scale;
        for (int r = 0; r < ROWS; r++) {
            rowWages[r] *= scale;  rowTax[r] *= scale;  rowRent[r] *= scale;
            rowShopping[r] *= scale;  rowContributions[r] *= scale;
            rowPensions[r] *= scale;  rowHealthcare[r] *= scale;
            rowTuition[r] *= scale;  rowInterest[r] *= scale;
        }
    }

}
