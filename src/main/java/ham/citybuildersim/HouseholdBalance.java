package ham.citybuildersim;

/**
 * The households' balance sheet: what they have saved, what they owe, and what
 * happens in the month they cannot cover the shop.
 *
 * ==================== WHY THIS EXISTS ====================
 *
 * Retail demand was `min(storeCoverage, population)` - a headcount, with no
 * reference whatever to what anybody earned. So the shops sold the same basket
 * to an unskilled single adult on $552 and to an Elite household on $18,673,
 * and the household screen carried a five-line paragraph admitting it:
 *
 *     "Nothing in the model ties spending to income yet - rent is the same per
 *      head for everyone and so is the weekly shop - so a poor household is
 *      charged what a rich one is. Those deficits are a missing budget
 *      constraint, not a result."
 *
 * A deficit that nothing funds is money from nowhere. This is the constraint.
 *
 * ==================== THE WATERFALL ====================
 *
 * Jerus's design, and it is the right shape: a household short of money does
 * not simply eat less. It spends its savings, then it borrows, and only when
 * both are gone does it go without - at which point going without is a health
 * problem rather than an accounting one.
 *
 *     payslip  ->  rent  ->  fees (healthcare, tuition, interest)  ->  food
 *
 *     food short?   savings first, then credit, then go hungry
 *     food spare?   pay down the debt first, then bank it
 *
 * Debt before hunger and repayment before saving are both deliberate: they are
 * what a household actually does, and each is the direction that makes the
 * failure arrive slowly enough for the player to see it coming.
 *
 * ==================== PER HOUSEHOLD, NOT PER ROW ====================
 *
 * The stocks are kept PER HOUSEHOLD, which is the one decision here worth
 * arguing about. A row total would be diluted every month the city grew: a
 * hundred arrivals would halve the average family's savings without anybody
 * spending a penny, and the screen would show a city getting poorer for growing.
 * Per household, a newcomer arrives with what a household like theirs has, which
 * is both truer and the only version whose figure means anything on a screen.
 *
 * Jerus asked for the stocks per pay tier AND per family shape. Per tier is what
 * exists here, because FamilyModel rebuilds every household from scratch each
 * month - "nobody keeps the family they had last month" - so a stock attached to
 * "unskilled couple with a teen" would be a stock of nothing, handed to a
 * different set of people every month. The tier survives; the shape does not.
 * The screen still SHOWS both, by giving every shape in a tier that tier's
 * per-household position.
 *
 * ==================== WHAT IT IS NOT ====================
 *
 * Not part of the money audit. Households have never been one of MoneyAudit's
 * pools - wages leaving the city's businesses leave the audited system, and rent
 * and shopping arrive back into it - so savings and household debt sit outside
 * the identity by construction, exactly as they did when they were invisible.
 * What changes is that they are now written down.
 */
public class HouseholdBalance {

    /** One row per pay tier, plus the retired - the same shape as HouseholdAccounts. */
    public static final int ROWS = PayTier.values().length + 1;

    /* ------------------------------- the dials ------------------------------- */

    /**
     * How many months of take-home a household can owe before the lender stops.
     *
     * Six is roughly half a year's income, which is about where unsecured
     * consumer credit actually stops for somebody with no assets. It is also the
     * number that sets how long a household can be short before the hunger
     * starts: an unskilled single adult short $117 a month hits the ceiling in
     * about twenty months, which is slow enough to be a warning and fast enough
     * to matter inside a playthrough.
     */
    public static final double CREDIT_LIMIT_MONTHS = 6;

    /**
     * What a household pays over the risk-free rate, before any risk premium.
     *
     * Unsecured consumer credit is dear. Three points over the city's own
     * borrowing rate is the floor, and the risk premium below takes it up from
     * there.
     */
    public static final double BASE_SPREAD = .03;

    /**
     * Extra spread per month of income owed.
     *
     * A household at the ceiling - six months of income in debt - pays
     * BASE_SPREAD + 6 x this, so about twelve points over risk-free. That is a
     * credit card, which is what this is. The slope is what makes the debt
     * compound faster the deeper it goes, and it is why a household that starts
     * borrowing to eat rarely stops.
     */
    public static final double RISK_SLOPE = .015;

    /** Nobody is charged more than this, however deep they are. */
    public static final double MAX_RATE = .36;

    /**
     * How much of the money above subsistence a household spends.
     *
     * WITHOUT THIS THE RICH BANK EVERYTHING. Demand was one basket per person,
     * so a College household with $3,800 spare after the bills bought $713 of
     * goods and saved the rest - measured at $715,012 per household after three
     * hundred months, and $4.0M for an Elite one. Numbers like that are not a
     * balance problem, they are a missing behaviour: a household with money
     * spends more of it, on more and better things, and the shops sell them.
     *
     * 0.80 puts a high earner's saving rate near 16-19%, which is the top end
     * of what real households manage. The floor underneath it is subsistence -
     * one basket a head, the old headcount demand - so the poor still want
     * exactly what they always wanted, and only the top of the distribution
     * moves. Which is the point: this is the other half of "spending follows
     * income", and without it the constraint only ever bites downward.
     */
    public static final double MARGINAL_PROPENSITY = .80;

    /**
     * A month's savings a founding city's households already have.
     *
     * Not zero, because a city where every household is one bad month from the
     * food bank on the day it is founded reads as a bug rather than as poverty -
     * and because people who move somewhere new have, by revealed preference,
     * been able to afford to.
     */
    public static final double OPENING_BUFFER_MONTHS = 1.5;

    /* =====================================================================
       BANKRUPTCY

       Jerus: "if families get hit with too much debt they go bankrupt and reset
       their financial position... wiped, locked out, but only some leave."

       Which is the missing end of the waterfall. Before this a tier that ran
       out of savings and credit simply sat at its ceiling for the rest of the
       game, going hungry, paying interest it could not afford on a debt that
       could never be repaid - a permanent condition rather than an event, and
       nothing in the model ever closed it out.

       Now it closes: the debt is written off against the bank, the savings go
       with it, the tier cannot borrow for a year, and some of them leave. All
       three parts matter. Without the lockout a broke tier cycles straight back
       into debt and the bank bleeds on a loop; without the emigration poverty
       costs the city nothing; and without the write-off the money vanishes
       instead of landing on somebody, which is the whole reason the bank exists.
       ===================================================================== */

    /**
     * Months of income owed at which a household stops being able to carry it.
     *
     * The credit ceiling is six months; this is past it, because reaching the
     * ceiling is not the same as failing - a household at the ceiling that can
     * still service the interest is merely poor. What fails is the one that
     * cannot, so the trigger is BOTH: at the ceiling, and short of the shop.
     */
    public static final double BANKRUPT_AT_MONTHS = CREDIT_LIMIT_MONTHS * .98;

    /** Share of a stuck tier that goes under in a month. A trickle, not a purge. */
    public static final double BANKRUPT_RATE = .04;

    /** Months a discharged household cannot borrow. */
    public static final int LOCKOUT_MONTHS = 12;

    /** ...and the share of them who give up on the city entirely. */
    public static final double LEAVE_ON_BANKRUPTCY = .25;

    private final int[] lockout = new int[ROWS];
    private final double[] lastBankrupt = new double[ROWS];
    private double lastWrittenOff;
    private double lastLeaving;

    /* ------------------------------ the position ------------------------------ */

    /** Per household of that row, in the game's thousands. */
    private final double[] savings = new double[ROWS];
    private final double[] debt    = new double[ROWS];

    /* --------------------- last month's working, for the screen --------------------- */

    private final double[] lastAfterFixed = new double[ROWS];
    private final double[] lastInterest   = new double[ROWS];
    private final double[] lastDrawn      = new double[ROWS];

    /**
     * What the household needed and could neither pay for, draw down, nor
     * borrow - because it is at its credit ceiling or locked out after a
     * discharge.
     *
     * Not a debt and not a loss: it is spending that did not happen. Kept
     * because it is the only figure that distinguishes a family that is merely
     * poor from one the bank has stopped lending to.
     */
    private final double[] lastUnfunded   = new double[ROWS];
    private final double[] lastBorrowed   = new double[ROWS];
    private final double[] lastRepaid     = new double[ROWS];
    private final double[] lastSaved      = new double[ROWS];
    private final double[] lastWant       = new double[ROWS];
    private final double[] lastPlanned    = new double[ROWS];
    private final double[] lastRate       = new double[ROWS];
    private final double[] lastSubsistence = new double[ROWS];
    private final double[] lastHouseholds = new double[ROWS];
    private double plannedSpend;
    private double hungryPeople;
    private double totalPeople;
    private boolean opened;

    /* =====================================================================
       THE MONTH

       Settles the month that just ran, then plans the next one. Both here,
       because the plan IS the settlement's other half: what a household can
       spend next month is exactly what this month left it holding.
       ===================================================================== */

    /**
     * @param households        homes in each row
     * @param people            residents in each row
     * @param disposable        row total of wages + pensions - tax - contributions
     * @param rentPerHousehold  what one let home pays, the same for all of them
     * @param fees              row total of healthcare fees and tuition
     * @param actualShopping    row total actually spent in the shops this month
     * @param foodPricePerHead  one person's monthly basket at today's shelf price
     * @param riskFreeAnnual    the city's own borrowing rate, which the lender prices off
     */
    public void advanceMonth(double[] households, double[] people,
                             double[] disposable, double rentPerHousehold,
                             double[] fees, double[] actualShopping,
                             double foodPricePerHead, double riskFreeAnnual,
                             double supplyRatio) {

        if (households == null || households.length != ROWS
                || people == null || people.length != ROWS) {
            return;   // refused whole, per the standing rule on state arrays
        }

        /*
         * HUNGER, BEFORE ANYTHING IS OVERWRITTEN.
         *
         * Last month's plan is what each household set out to buy; the supply
         * ratio is the share of it the shops actually had. Both bite, and they
         * are different failures - one is a household with no money, the other
         * is a city with no stock - so a player who fixes the wrong one gets
         * nowhere. Measured against SUBSISTENCE, because a household that
         * wanted a better television and bought a worse one is not hungry.
         */
        hungryPeople = 0;
        totalPeople = 0;
        lastWrittenOff = 0;
        lastLeaving = 0;
        double delivered = Math.max(0, Math.min(1, supplyRatio));
        lastDelivered = delivered;
        for (int r = 0; r < ROWS; r++) {
            totalPeople += people[r];
            double ate = lastPlanned[r] * delivered;
            if (lastSubsistence[r] > 0 && ate < lastSubsistence[r]) {
                hungryPeople += people[r] * (1 - ate / lastSubsistence[r]);
            }
        }

        plannedSpend = 0;

        for (int r = 0; r < ROWS; r++) {
            double homes = households[r];
            double previousHomes = lastHouseholds[r];
            lastHouseholds[r] = homes;

            /* ---------- THE STOCK BELONGS TO THE PEOPLE, NOT TO THE DOORS ----------
             *
             * savings[] and debt[] are PER HOUSEHOLD, and the city total is that
             * figure times however many households the row holds. So when the
             * row grew, the newcomers silently arrived owing the row's average
             * debt - money the bank had never lent anybody. The bank's book grew
             * by $4.53 in a month in which it made no loans at all, and that is
             * how this was found: its balance sheet said equity had moved by
             * more than the month's profit.
             *
             * GROWTH DILUTES. A family that has just moved in owes nothing, so
             * the same total is spread over more households and the average
             * falls. Nothing is created.
             *
             * SHRINKAGE DOES NOT. A family that leaves takes its debt with it -
             * out of the city, out of reach - so the per-household figure stands
             * and the total falls by what went with them. That is a real loss to
             * whoever lent it, and it is handed to the bank as a write-off
             * below rather than quietly evaporating, which is what it used to do.
             */
            if (previousHomes > 0 && homes > previousHomes) {
                debt[r] *= previousHomes / homes;

                /*
                 * ...but they do NOT arrive penniless. A household moving in
                 * brings what a household has - the same opening buffer a
                 * founding one gets - so the row's savings are the old total
                 * plus what the newcomers carried, spread over all of them.
                 *
                 * Diluting savings the way debt is diluted was tried and is
                 * wrong: a growing row's savings fall towards zero, every
                 * family in it is suddenly broke, and consumption collapses.
                 * InfrastructureCheck caught it as the city that built roads
                 * producing a fourteenth of the GDP of the city that did not.
                 */
                double arriving = homes - previousHomes;
                double brought = Math.max(0, disposable[r] / homes) * OPENING_BUFFER_MONTHS;
                savings[r] = (savings[r] * previousHomes + arriving * brought) / homes;
            } else if (previousHomes > homes && homes > 0) {
                lastWrittenOff += debt[r] * (previousHomes - homes);
            }

            if (homes < .5) {
                savings[r] = 0;
                debt[r] = 0;
                clearRow(r);
                continue;
            }

            double disposablePer = disposable[r] / homes;
            double sizePer = people[r] / homes;

            /*
             * A founding city's households are not destitute on day one. Also
             * catches a row that has just come into existence - the city's first
             * College household should open with what a College household has,
             * not with nothing.
             */
            if (!opened || (savings[r] == 0 && debt[r] == 0)) {
                savings[r] = Math.max(0, disposablePer) * OPENING_BUFFER_MONTHS;
            }

            /* ---------------- what the lender charges this one ---------------- */
            double owedMonths = disposablePer > 0 ? debt[r] / disposablePer : 0;
            double annual = Math.min(MAX_RATE,
                    Math.max(0, riskFreeAnnual) + BASE_SPREAD + RISK_SLOPE * owedMonths);
            lastRate[r] = annual;
            double interestPer = debt[r] * annual / 12;

            /* ---------------- the bills, in order ---------------- */
            double feesPer = fees == null ? 0 : fees[r] / homes;
            double afterFixed = disposablePer - rentPerHousehold - feesPer - interestPer;
            lastAfterFixed[r] = afterFixed;
            lastInterest[r] = interestPer;

            /* ---------------- settle what they actually spent ---------------- */
            double subsistence = sizePer * foodPricePerHead;
            lastSubsistence[r] = subsistence;
            double spentPer = actualShopping == null ? 0 : actualShopping[r] / homes;
            double gap = spentPer - afterFixed;

            double drawn = 0, borrowed = 0, repaid = 0, banked = 0, unfunded = 0;
            if (gap > 0) {
                drawn = Math.min(gap, Math.max(0, savings[r]));
                savings[r] -= drawn;

                /*
                 * ...AND THE LIMIT BINDS HERE TOO, NOT ONLY IN THE PLAN.
                 *
                 * The credit ceiling and the lockout used to live in planRow()
                 * alone, which capped what a household set out to spend and
                 * capped nothing about what it ended up owing. Two things went
                 * wrong with that, and BankCheck caught both: a household short
                 * of the rent every month borrowed the shortfall for ever, so
                 * its debt compounded past six months of income to twenty and
                 * kept going; and a household discharged last month was lent to
                 * again the next one, which is not a bankruptcy but a subsidy.
                 *
                 * A cap in the plan and no cap in the settlement is the same
                 * mistake as a quote that disagrees with the booking.
                 *
                 * What is refused is simply not funded. It is not a debt and it
                 * is not a loss to anybody: the household went without, which is
                 * what next month's plan already says will happen and what the
                 * hunger measure already reads. No money is created or
                 * destroyed, because households sit outside the audited pools.
                 */
                double room = lockout[r] > 0 ? 0
                        : Math.max(0, CREDIT_LIMIT_MONTHS * Math.max(0, disposablePer) - debt[r]);
                borrowed = Math.min(gap - drawn, room);
                unfunded = (gap - drawn) - borrowed;
                debt[r] += borrowed;
            } else {
                double surplus = -gap;
                repaid = Math.min(surplus, debt[r]);
                debt[r] -= repaid;
                banked = surplus - repaid;
                savings[r] += banked;
            }
            lastDrawn[r] = drawn;
            lastUnfunded[r] = unfunded;
            lastBorrowed[r] = borrowed;
            lastRepaid[r] = repaid;
            lastSaved[r] = banked;

            /* ---------------- and whoever cannot carry it any more ---------------- */
            lastBankrupt[r] = 0;
            if (lockout[r] > 0) lockout[r]--;

            boolean atTheCeiling = disposablePer > 0
                    && debt[r] >= BANKRUPT_AT_MONTHS * disposablePer;
            boolean cannotEat = afterFixed < subsistence;

            if (atTheCeiling && cannotEat) {
                double going = homes * BANKRUPT_RATE;
                lastBankrupt[r] = going;

                // The debt dies with the household's position, not with the
                // household: the per-household figures are averages, so a share
                // of them discharging is that share off the average.
                lastWrittenOff += debt[r] * going;
                debt[r] *= (1 - BANKRUPT_RATE);
                savings[r] *= (1 - BANKRUPT_RATE);
                lockout[r] = LOCKOUT_MONTHS;
                lastLeaving += going * LEAVE_ON_BANKRUPTCY;
            }

            planRow(r, disposablePer, afterFixed, subsistence, homes);
        }
        opened = true;
    }

    /* ------------------------- what the bank is owed ------------------------- */

    /** Written off this month, which is the bank's loss. */
    public double getWrittenOff()      { return lastWrittenOff; }

    /** Households discharged this month, and the share of them leaving the city. */
    public double getBankrupt(int row) { return lastBankrupt[row]; }
    public double getLeavingCity()     { return lastLeaving; }
    public boolean isLockedOut(int row){ return lockout[row] > 0; }
    public int getLockout(int row)     { return lockout[row]; }

    /** Everything the households owe the bank. */
    public double bookOwed() { return totalDebt(); }

    /**
     * The plan alone, without settling a month.
     *
     * FOR THE LOAD PATH, and it is not a nicety. Savings and debt are saved;
     * the plan is a flow and is not, so a reloaded city had lastPlanned all
     * zeros - which meant HouseholdAccounts.updateByTier() fell back to
     * splitting the shopping by HEADCOUNT for exactly one month, the tiers'
     * books came out different from the live city's, and everything downstream
     * of them differed with it. Caught by SaveFileCheck to the cent once rent
     * started depending on the household mix. The seventh time this codebase
     * has been bitten by rebuilding a flow instead of re-striking it.
     *
     * Calling advanceMonth() here instead would settle a month that has already
     * been settled - a second draw on the same savings.
     */
    public void planOnly(double[] households, double[] people,
                         double[] disposable, double rentPerHousehold,
                         double[] fees, double foodPricePerHead, double riskFreeAnnual) {

        if (households == null || households.length != ROWS
                || people == null || people.length != ROWS) {
            return;
        }

        plannedSpend = 0;
        for (int r = 0; r < ROWS; r++) {
            double homes = households[r];
            lastHouseholds[r] = homes;
            if (homes < .5) { clearRow(r); continue; }

            double disposablePer = disposable[r] / homes;
            double sizePer = people[r] / homes;

            double owedMonths = disposablePer > 0 ? debt[r] / disposablePer : 0;
            double annual = Math.min(MAX_RATE,
                    Math.max(0, riskFreeAnnual) + BASE_SPREAD + RISK_SLOPE * owedMonths);
            lastRate[r] = annual;
            double interestPer = debt[r] * annual / 12;
            lastInterest[r] = interestPer;

            double feesPer = fees == null ? 0 : fees[r] / homes;
            double afterFixed = disposablePer - rentPerHousehold - feesPer - interestPer;
            lastAfterFixed[r] = afterFixed;

            double subsistence = sizePer * foodPricePerHead;
            lastSubsistence[r] = subsistence;

            planRow(r, disposablePer, afterFixed, subsistence, homes);
        }
        opened = true;
    }

    /** What one row can afford next month. One definition, both paths. */
    private void planRow(int r, double disposablePer, double afterFixed,
                         double subsistence, double homes) {

        double wantPer = subsistence
                + MARGINAL_PROPENSITY * Math.max(0, afterFixed - subsistence);

        // A discharged household is not lent to again for a year. Which is the
        // teeth in the bankruptcy: they go straight from borrowing to eating
        // less, and the hunger shows up on the health service the same month.
        double room = lockout[r] > 0 ? 0
                : Math.max(0, CREDIT_LIMIT_MONTHS * Math.max(0, disposablePer) - debt[r]);
        double spendable = Math.max(0, afterFixed) + savings[r] + room;

        lastWant[r] = wantPer;
        lastPlanned[r] = Math.min(wantPer, spendable);
        plannedSpend += lastPlanned[r] * homes;
    }

    private void clearRow(int r) {
        lastAfterFixed[r] = 0; lastInterest[r] = 0; lastDrawn[r] = 0; lastUnfunded[r] = 0;
        lastBorrowed[r] = 0;  lastRepaid[r] = 0;   lastSaved[r] = 0;
        lastWant[r] = 0;      lastPlanned[r] = 0;  lastRate[r] = 0;
        lastSubsistence[r] = 0;
    }

    /* ------------------------------- reading ------------------------------- */

    /** What the shops can sell next month, in money. The budget constraint itself. */
    public double getSpendingCapacity() { return plannedSpend; }

    /** What they would spend if money were no object - the demand behind the cap. */
    public double getWantedSpend() {
        double sum = 0;
        for (int r = 0; r < ROWS; r++) sum += lastWant[r] * lastHouseholds[r];
        return sum;
    }

    /** One basket a head: what going short is measured against. */
    public double getSubsistence(int row) { return lastSubsistence[row]; }

    /**
     * The share of each row's planned spend, for splitting what retail actually
     * sold back across the rows.
     *
     * This is what makes the tier table honest: the shops' takings are divided
     * by who could afford to shop, not by headcount, so a tier that cannot pay
     * shows up as buying less rather than as an unexplained deficit.
     */
    public double[] plannedShare() {
        double[] out = new double[ROWS];
        double total = 0;
        for (int r = 0; r < ROWS; r++) total += lastPlanned[r] * lastHouseholds[r];
        for (int r = 0; r < ROWS; r++) {
            out[r] = total > 0 ? lastPlanned[r] * lastHouseholds[r] / total : 0;
        }
        return out;
    }

    /** Share of the city going short of food, 0-1. What the health service reads. */
    public double getHungerRate() {
        return totalPeople > 0 ? Math.min(1, hungryPeople / totalPeople) : 0;
    }

    public double getHungryPeople()          { return hungryPeople; }

    /**
     * The share of what households planned to buy that the shops could hand
     * over - and therefore which of the two hungers is biting.
     *
     * A household short of money and a city short of stock both come out as
     * hunger, and a player who fixes the wrong one gets nowhere. This is the
     * figure that tells them apart: at 1.0 the shelves were full and anybody
     * still hungry could not afford to eat.
     */
    public double getDeliveredShare() { return lastDelivered; }

    private double lastDelivered = 1;
    /**
     * Interest the bank paid on what these households have saved.
     *
     * Credited straight to the accounts, in proportion to what is in them, which
     * is what "interest on your savings" means. It is not spending money and it
     * is not counted as income in the month's waterfall: it accrues, and it is
     * there next month when the family needs it.
     *
     * Small, and compounding, and the only thing in this model that ever made
     * saving worth doing on its own.
     */
    public void creditDepositInterest(double total) {
        if (total <= 0) return;
        double base = totalSavings();
        if (base <= 0) return;
        for (int r = 0; r < ROWS; r++) {
            if (savings[r] <= 0 || lastHouseholds[r] <= 0) continue;
            double share = savings[r] * lastHouseholds[r] / base;
            savings[r] += total * share / lastHouseholds[r];
        }
        lastDepositInterest = total;
    }

    private double lastDepositInterest;

    /** What the bank paid the city's savers this month. */
    public double getDepositInterest() { return lastDepositInterest; }

    public double getSavings(int row)        { return savings[row]; }

    /** Households this row was struck for - the multiplier on every per-row figure. */
    public double getHouseholds(int row)     { return lastHouseholds[row]; }
    public double getDebt(int row)           { return debt[row]; }
    public double getAfterFixed(int row)     { return lastAfterFixed[row]; }
    public double getInterest(int row)       { return lastInterest[row]; }
    public double getDrawn(int row)          { return lastDrawn[row]; }

    /** What this row wanted, could not fund, and did not get. See lastUnfunded. */
    public double getUnfunded(int row)       { return lastUnfunded[row]; }

    /** True when the bank has stopped lending to this row - ceiling or lockout. */
    public boolean isCutOff(int row)         { return lastUnfunded[row] > 0; }
    public double getBorrowed(int row)       { return lastBorrowed[row]; }
    public double getRepaid(int row)         { return lastRepaid[row]; }
    public double getBanked(int row)         { return lastSaved[row]; }
    public double getWant(int row)           { return lastWant[row]; }
    public double getPlanned(int row)        { return lastPlanned[row]; }
    public double getRate(int row)           { return lastRate[row]; }

    /** True when this row is buying less food than it wants. */
    public boolean isGoingShort(int row) {
        return lastWant[row] > 0 && lastPlanned[row] < lastWant[row] - 1e-9;
    }

    /** City totals, for the headline lines on the screen. */
    public double totalSavings() {
        double sum = 0;
        for (int r = 0; r < ROWS; r++) sum += savings[r] * lastHouseholds[r];
        return sum;
    }

    public double totalDebt() {
        double sum = 0;
        for (int r = 0; r < ROWS; r++) sum += debt[r] * lastHouseholds[r];
        return sum;
    }

    public double totalInterest() {
        double sum = 0;
        for (int r = 0; r < ROWS; r++) sum += lastInterest[r] * lastHouseholds[r];
        return sum;
    }

    /** New lending to families this month - the bank's money out the door. */
    public double totalBorrowed() {
        double sum = 0;
        for (int r = 0; r < ROWS; r++) sum += lastBorrowed[r] * lastHouseholds[r];
        return sum;
    }

    /** ...and what came back. */
    public double totalRepaid() {
        double sum = 0;
        for (int r = 0; r < ROWS; r++) sum += lastRepaid[r] * lastHouseholds[r];
        return sum;
    }

    /* ------------------------------- saving -------------------------------
     *
     * A STOCK, so it has to be carried. Savings and debt are the two things on
     * this screen that are not derived from the month - rebuilding them from
     * the state a month ended in is exactly the reconstruction this codebase
     * has been caught by six times. The last-month working is NOT saved: it is
     * a flow, it is recomputed on the first tick, and carrying it would be
     * carrying an answer nobody would re-ask.
     */

    public double[] toSaveArray() {
        double[] out = new double[ROWS * 3];
        System.arraycopy(savings, 0, out, 0, ROWS);
        System.arraycopy(debt, 0, out, ROWS, ROWS);
        // The lockout is a countdown, which is a STOCK: a tier discharged last
        // month is eleven months from borrowing again, and a save that forgot
        // it would hand that tier a fresh line of credit on load.
        for (int r = 0; r < ROWS; r++) out[ROWS * 2 + r] = lockout[r];
        return out;
    }

    public void restore(double[] saved) {
        if (saved == null || saved.length != ROWS * 3) return;   // refused whole
        System.arraycopy(saved, 0, savings, 0, ROWS);
        System.arraycopy(saved, ROWS, debt, 0, ROWS);
        for (int r = 0; r < ROWS; r++) lockout[r] = (int) Math.round(saved[ROWS * 2 + r]);
        opened = true;
    }

    public void reset() {
        java.util.Arrays.fill(savings, 0);
        java.util.Arrays.fill(debt, 0);
        java.util.Arrays.fill(lockout, 0);
        java.util.Arrays.fill(lastBankrupt, 0);
        lastWrittenOff = 0;
        lastLeaving = 0;
        for (int r = 0; r < ROWS; r++) { clearRow(r); lastHouseholds[r] = 0; }
        plannedSpend = 0;
        hungryPeople = 0;
        totalPeople = 0;
        opened = false;
    }

    /**
     * The households' stocks and this month's working, in the new unit.
     *
     * lastRate is an interest rate and lastBankrupt, lastHouseholds and the
     * headcounts are people; none of those move. lastDelivered is a share.
     */
    public void redenominate(double scale) {
        lastWrittenOff *= scale;
        plannedSpend   *= scale;
        lastDepositInterest *= scale;
        for (int r = 0; r < ROWS; r++) {
            savings[r]         *= scale;
            debt[r]            *= scale;
            lastAfterFixed[r]  *= scale;
            lastInterest[r]    *= scale;
            lastDrawn[r]       *= scale;
            lastUnfunded[r]    *= scale;
            lastBorrowed[r]    *= scale;
            lastRepaid[r]      *= scale;
            lastSaved[r]       *= scale;
            lastWant[r]        *= scale;
            lastPlanned[r]     *= scale;
            lastSubsistence[r] *= scale;
        }
    }

}
