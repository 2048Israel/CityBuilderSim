package ham.citybuildersim;

import java.util.function.Consumer;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;

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
 * failure arrive slowly enough for the player to see it coming. The waterfall
 * itself lives in Household.settle(); this class decides what each household
 * is handed and adds up what they did.
 *
 * ==================== SIXTY-EIGHT CELLS, NOT SEVEN ROWS ====================
 *
 * Jerus, 2026-09-10: "I need it per household and pay tier type."
 *
 * Until then the stocks were kept per pay tier - seven rows - and the header
 * here argued that the shape could not carry a stock because FamilyModel
 * rebuilds every household from scratch each month: "a stock attached to
 * 'unskilled couple with a teen' would be a stock of nothing, handed to a
 * different set of people every month." The tier survived; the shape did not.
 *
 * It does now, because the money follows the people. Every cell of the family
 * matrix is a Household object with its own savings, debt and credit line, and
 * when the monthly rebuild moves households between cells - a child ages into
 * a teen, five singles take a flatshare, a worker retires - their money moves
 * with them. See followThePeople(). What used to be seven rows of arrays is
 * sixty-eight objects this class holds, sums and strikes; the row getters the
 * screens read are sums of the cells, and the tier is a view rather than the
 * unit.
 *
 * ==================== PER HOUSEHOLD, NOT PER CELL ====================
 *
 * The stocks are kept PER HOUSEHOLD, which is the one decision here worth
 * arguing about. A cell total would be diluted every month the city grew: a
 * hundred arrivals would halve the average family's savings without anybody
 * spending a penny, and the screen would show a city getting poorer for
 * growing. Per household, a newcomer arrives with what a household like theirs
 * has, which is both truer and the only version whose figure means anything on
 * a screen.
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

    /**
     * One row per pay tier, the retired, and since 2026-09-11 the out of work,
     * the students and the orphans - the same shape as HouseholdAccounts.
     */
    public static final int ROWS = Household.ROWS;

    /** The rows before the people outside the families had books: the six tiers and the retired. */
    public static final int ROWS_BEFORE_OUTSIDE = Household.RETIRED_ROW + 1;

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
       with it, the cell cannot borrow for a year, and some of them leave. All
       three parts matter. Without the lockout a broke cell cycles straight back
       into debt and the bank bleeds on a loop; without the emigration poverty
       costs the city nothing; and without the write-off the money vanishes
       instead of landing on somebody, which is the whole reason the bank exists.

       PER CELL, since the cells exist: an unskilled large family at its ceiling
       discharges while the unskilled single adult next door keeps its credit
       line, where before the whole tier was locked out together.
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

    /** Share of a stuck cell that goes under in a month. A trickle, not a purge. */
    public static final double BANKRUPT_RATE = .04;

    /** Months a discharged household cannot borrow. */
    public static final int LOCKOUT_MONTHS = 12;

    /** ...and the share of them who give up on the city entirely. */
    public static final double LEAVE_ON_BANKRUPTCY = .25;

    /* ------------------------------- the cells ------------------------------- */

    /**
     * Every meaningful cell of the family matrix, in one fixed order: the
     * working shapes by declaration, each across the six tiers, then the two
     * retired shapes. Sixty-eight. Looked up by shape and tier through
     * `index`, and by name through key() on the save path.
     */
    private final Household[] cells;
    private final int[][] index =
            new int[FamilyStructure.values().length][PayTier.values().length];
    private final java.util.List<Household> view;
    private final int[] unemployedIndex = new int[UnemployedHousehold.Status.values().length];
    private int studentIndex;
    private final int[] orphanIndex = new int[AgeBand.values().length];
    private int prisonerIndex;

    /**
     * How many households are in each cell the family matrix does not hold -
     * the out of work, the students, the orphans. Set by Game off Unemployment,
     * the students and FamilyModel; nobody in them by default, so a fixture
     * that never sets it has the city it always had.
     */
    private ToDoubleFunction<Household> outsideCensus = c -> 0;

    public void setOutsideCensus(ToDoubleFunction<Household> census) {
        this.outsideCensus = census == null ? c -> 0 : census;
    }

    /**
     * The share of a door's rent one household of a cell pays: 1 alone in its
     * own home, a fifth sharing, half doubled up, none with no door. Set by
     * Game off the housing match; everybody pays a whole door by default.
     */
    private ToDoubleFunction<Household> rentShares = c -> 1;

    public void setRentShares(ToDoubleFunction<Household> shares) {
        this.rentShares = shares == null ? c -> 1 : shares;
    }

    /* ------------------------------- the month ------------------------------- */

    private double lastWrittenOff;
    private double lastLeaving;
    private double lastEvicted;
    private double lastStudentDebtTakenAway;
    private double lastTakenAway;
    private double graduating;
    private double lastGraduated;
    private double lastDepositInterest;
    private double lastDelivered = 1;
    private double plannedSpend;
    private double hungryPeople;
    private double totalPeople;

    public HouseholdBalance() {
        java.util.List<Household> built = new java.util.ArrayList<>();
        for (int[] row : index) java.util.Arrays.fill(row, -1);
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (shape.isRetired()) continue;
            for (PayTier tier : PayTier.values()) {
                index[shape.ordinal()][tier.ordinal()] = built.size();
                built.add(new WorkingHousehold(shape, tier));
            }
        }
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (!shape.isRetired()) continue;
            // The retired sit at tier index 0 in FamilyModel's matrix; the
            // same convention here, so one lookup serves both.
            index[shape.ordinal()][0] = built.size();
            built.add(new RetiredHousehold(shape));
        }
        // The people outside the families, after them, so every family cell
        // keeps its place. See Household's rows.
        for (UnemployedHousehold.Status s : UnemployedHousehold.Status.values()) {
            unemployedIndex[s.ordinal()] = built.size();
            built.add(new UnemployedHousehold(s));
        }
        studentIndex = built.size();
        built.add(new StudentHousehold());
        java.util.Arrays.fill(orphanIndex, -1);
        for (AgeBand b : new AgeBand[] { AgeBand.BABY, AgeBand.CHILD, AgeBand.TEEN }) {
            orphanIndex[b.ordinal()] = built.size();
            built.add(new OrphanHousehold(b));
        }
        // ...and the prisoners' ledger, last, so every cell before it keeps its place.
        prisonerIndex = built.size();
        built.add(new PrisonerHousehold());
        cells = built.toArray(new Household[0]);
        view = java.util.Collections.unmodifiableList(java.util.Arrays.asList(cells));
    }

    /* =====================================================================
       EVERY HOUSEHOLD, OR ONE OF THEM

       "That way it's a lot easier to sum everything up, or modify across all."
       These are the sum and the modify.
       ===================================================================== */

    /** The cell for this shape at this tier. For a retired shape the tier is ignored. */
    public Household cell(FamilyStructure shape, PayTier tier) {
        int i = index[shape.ordinal()][shape.isRetired() ? 0 : tier.ordinal()];
        return cells[i];
    }

    /** The cell for a retired shape, which has no tier. */
    public Household cell(FamilyStructure shape) {
        if (!shape.isRetired()) {
            throw new IllegalArgumentException(shape + " needs a tier");
        }
        return cells[index[shape.ordinal()][0]];
    }

    /** The out-of-work cell in this situation. */
    public UnemployedHousehold unemployed(UnemployedHousehold.Status status) {
        return (UnemployedHousehold) cells[unemployedIndex[status.ordinal()]];
    }

    /** The students' cell. */
    public StudentHousehold students() { return (StudentHousehold) cells[studentIndex]; }

    /** The orphans of a child band, or null for a band that has none. */
    public OrphanHousehold orphans(AgeBand band) {
        int i = orphanIndex[band.ordinal()];
        return i < 0 ? null : (OrphanHousehold) cells[i];
    }

    /** The prisoners' ledger. */
    public PrisonerHousehold prisoners() { return (PrisonerHousehold) cells[prisonerIndex]; }

    /** Every cell, in the fixed order. Read-only. */
    public java.util.List<Household> cells() { return view; }

    public int cellCount() { return cells.length; }

    public void forEach(Consumer<Household> action) {
        for (Household c : cells) action.accept(c);
    }

    /** Adds a figure up across every cell. */
    public double sum(ToDoubleFunction<Household> figure) {
        double total = 0;
        for (Household c : cells) total += figure.applyAsDouble(c);
        return total;
    }

    /** ...or across one row: a tier's cells, or the retired. */
    public double sumRow(int row, ToDoubleFunction<Household> figure) {
        double total = 0;
        for (Household c : cells) if (c.row() == row) total += figure.applyAsDouble(c);
        return total;
    }

    /** Households in the row: the denominator of every per-household row figure. */
    public double rowHouseholds(int row) {
        return sumRow(row, Household::households);
    }

    /** A row total, per household of the row. Zero for an empty row. */
    private double perHousehold(int row, ToDoubleFunction<Household> perCell) {
        double homes = rowHouseholds(row);
        if (homes <= 0) return 0;
        double total = 0;
        for (Household c : cells) {
            if (c.row() == row) total += perCell.applyAsDouble(c) * c.households;
        }
        return total / homes;
    }

    /* =====================================================================
       THE MONTH

       Settles the month that just ran, then plans the next one. Both here,
       because the plan IS the settlement's other half: what a household can
       spend next month is exactly what this month left it holding.

       WHAT IT IS HANDED. The census is who lives here now, by cell -
       FamilyModel.get, with the retired at tier index 0 by that class's
       convention. The money comes as ROW totals, straight off the books
       HouseholdAccounts keeps: the tier's take-home, its fees, what it
       actually spent in the shops. Split here, by the one rule that makes the
       sum of the cells the row exactly - income by grown-ups, fees by people,
       the shop by who planned to spend - so nothing on the city's books is
       re-derived and every figure on the tier row is the sum of the cells
       under it.
       ===================================================================== */

    /**
     * @param census           households in each cell, as FamilyModel::get
     * @param rowDisposable    row total of wages + pensions - tax - contributions
     * @param rentPerHousehold what one let home pays, the same for all of them
     * @param rowFees          row total of healthcare fees and tuition
     * @param rowShopping      row total actually spent in the shops this month
     * @param foodPricePerHead one person's monthly basket at today's shelf price
     * @param riskFreeAnnual   the city's own borrowing rate, which the lender prices off
     * @param supplyRatio      the share of what was planned that the shops had
     */
    public void advanceMonth(ToDoubleBiFunction<FamilyStructure, PayTier> census,
                             double[] rowDisposable, double rentPerHousehold,
                             double[] rowFees, double[] rowShopping,
                             double foodPricePerHead, double riskFreeAnnual,
                             double supplyRatio) {

        rowDisposable = padRows(rowDisposable);
        rowFees = padRows(rowFees);
        rowShopping = padRows(rowShopping);
        if (census == null || rowDisposable == null || rowDisposable.length != ROWS) {
            return;   // refused whole, per the standing rule on state arrays
        }

        double[] fresh = takeCensus(census);

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
        lastEvicted = 0;
        lastTakenAway = 0;
        lastAbroadTakenAway = 0;
        java.util.Arrays.fill(lastSharesTakenAway, 0);
        for (Household c : cells) c.dividends = 0;
        double delivered = Math.max(0, Math.min(1, supplyRatio));
        lastDelivered = delivered;
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            double people = fresh[i] * c.size();
            totalPeople += people;
            double ate = c.planned * delivered;
            if (c.subsistence > 0 && ate < c.subsistence) {
                hungryPeople += people * (1 - ate / c.subsistence);
            }
        }

        /*
         * WHO DID THE SHOPPING, decided before the plan is overwritten.
         *
         * The shops sold against last month's plan, and the row's takings are
         * split across its cells by the same plan - a cell that could not
         * afford to shop is a cell that bought less, not one that ran a
         * deficit. With no plan yet (the founding month) the split follows
         * people, which is what the model did before there was a budget.
         */
        double[] shopWeight = new double[cells.length];
        double[] rowShopWeight = new double[ROWS];
        boolean[] rowHasPlan = new boolean[ROWS];
        for (Household c : cells) {
            if (c.planned * c.households > 0) rowHasPlan[c.row()] = true;
        }
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            shopWeight[i] = rowHasPlan[c.row()] ? c.planned * c.households : fresh[i] * c.size();
            rowShopWeight[c.row()] += shopWeight[i];
        }

        /* ---- what each cell is handed, per household ---- */
        double[] disposablePer = splitIncome(fresh, rowDisposable);
        double[] feesPer = splitByPeople(fresh, rowFees);
        double[] buffer = new double[cells.length];
        for (int i = 0; i < cells.length; i++) {
            buffer[i] = Math.max(0, disposablePer[i]) * OPENING_BUFFER_MONTHS;
        }

        /* ---- the stock belongs to the people, not to the cells ---- */
        followThePeople(fresh, buffer);

        /* ---- and every household's month ---- */
        plannedSpend = 0;
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            if (c.households < .5) {
                // Too few to strike - the position stands, carried for the
                // month the cell fills, and the working is blank.
                c.clearWorking();
                continue;
            }
            double spentPer = rowShopping == null || rowShopWeight[c.row()] <= 0 ? 0
                    : rowShopping[c.row()] * shopWeight[i] / rowShopWeight[c.row()] / c.households;

            c.rentShare = Math.max(0, rentShares.applyAsDouble(c));
            double rentDue = rentPerHousehold * c.rentShare;
            c.settle(disposablePer[i], rentDue, feesPer[i], spentPer,
                    foodPricePerHead, riskFreeAnnual, liquidity, localPerUsd);

            /*
             * EVICTION (2026-09-11). Jerus: once EI is over and the savings are
             * gone, a share leaves and the rest are unhoused. The cells are
             * averages, so the share of the fixed bills nobody could fund is
             * the share of the households who could not pay them - a level
             * struck from the month, not a rate typed in. Unemployment moves
             * them next month.
             */
            if (c.canBeEvicted() && c.unfunded > 0 && rentDue > 0) {
                double bills = rentDue + feesPer[i] + c.interest;
                c.evicted = c.households * Math.min(1, c.unfunded / bills);
                lastEvicted += c.evicted;
            }

            lastWrittenOff += c.discharge();
            lastLeaving += c.bankrupt * LEAVE_ON_BANKRUPTCY;

            plannedSpend += c.plan() * c.households;
        }
    }

    /**
     * A seven-row array - the tiers and the retired, from a caller written
     * before the people outside the families had rows - padded with empty
     * rows. Nobody is in them unless the outside census says so.
     */
    private static double[] padRows(double[] rows) {
        if (rows == null || (rows.length != ROWS_BEFORE_OUTSIDE
                && rows.length != Household.ROWS_BEFORE_PRISON)) return rows;
        return java.util.Arrays.copyOf(rows, ROWS);
    }

    /** Who lives in each cell now, in cell order. */
    private double[] takeCensus(ToDoubleBiFunction<FamilyStructure, PayTier> census) {
        double[] fresh = new double[cells.length];
        PayTier retiredSlot = PayTier.values()[0];
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            fresh[i] = c.shape() == null
                    ? Math.max(0, outsideCensus.applyAsDouble(c))
                    : Math.max(0, census.applyAsDouble(c.shape(),
                            c.isRetired() ? retiredSlot : c.tier()));
        }
        return fresh;
    }

    /**
     * A row's take-home, per household of each of its cells.
     *
     * By grown-ups: the tier's wage bill divided among its earners, the
     * pension bill among its pensioners. A couple takes home twice a single
     * adult's pay at the same tier, which is most of why the two can afford
     * such different lives - and it is the same arithmetic
     * HouseholdAccounts.statementFor() shows the player, so the cell's books
     * and the screen's statement agree.
     */
    private double[] splitIncome(double[] fresh, double[] rowTotal) {
        double[] rowWeight = new double[ROWS];
        for (int i = 0; i < cells.length; i++) {
            rowWeight[cells[i].row()] += fresh[i] * cells[i].earningWeight();
        }
        double[] per = new double[cells.length];
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            per[i] = rowWeight[c.row()] > 0
                    ? rowTotal[c.row()] * c.earningWeight() / rowWeight[c.row()] : 0;
        }
        return per;
    }

    /** A row's fees, per household of each of its cells, by the people in them. */
    private double[] splitByPeople(double[] fresh, double[] rowTotal) {
        double[] per = new double[cells.length];
        if (rowTotal == null) return per;
        double[] rowWeight = new double[ROWS];
        for (int i = 0; i < cells.length; i++) {
            rowWeight[cells[i].row()] += fresh[i] * cells[i].size();
        }
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            per[i] = rowWeight[c.row()] > 0
                    ? rowTotal[c.row()] * c.size() / rowWeight[c.row()] : 0;
        }
        return per;
    }

    /* =====================================================================
       THE STOCK BELONGS TO THE PEOPLE, NOT TO THE CELLS

       FamilyModel re-allocates every household from the pyramid and the job
       mix each month, so a cell's count moves for reasons that have nothing
       to do with anybody arriving or leaving: a child turns thirteen and a
       COUPLE_CHILD becomes a COUPLE_TEEN; five single adults take a flatshare;
       a job mix that shifts a point moves a point of every shape up a tier; a
       worker turns sixty-five. The people in those households are the same
       people with the same money, and a ledger that let a cell's stock
       evaporate every time its count fell would be a ledger of nothing - which
       is exactly why the stocks used to be kept per tier.

       So when a cell loses households, what they had goes into a pool: their
       savings and their debt, at the cell's own average. The cells that gained
       households draw on it. Within the tier first - a family's child ageing
       is the commonest move by far and it never crosses a tier - and then
       across the city, for the retirements and the job changes. Only what no
       cell claims has actually left the city, and only a gain no pool covers
       is a genuine arrival:

           left the city   savings gone with them; debt written off to the bank,
                           which is a real loss to whoever lent it
           arrived         no debt, and the same opening buffer a founding
                           household gets - people who move somewhere new have,
                           by revealed preference, been able to afford to

       Which is what the old per-row rule did at the row's edge, now done at
       the city's edge, with everything inside it conserved.

       WEIGHTED BY GROWN-UPS, not by households. Five singles becoming one
       flatshare is five households lost and one gained; counted by household
       four of them would have "left the city" and the flatshare would inherit
       a fifth of their money. Counted by the adults in them it is five for
       five, and the flatshare carries five wallets. A couple splitting is two
       for two; a child arriving brings nothing and takes nothing; one of a
       senior couple dying leaves half the position to the survivor and takes
       the other half out - because that is who carries the money.
       ===================================================================== */

    /** One stock a household carries, for followThePeople() to move. */
    private interface Stock {
        double get(Household c);
        void set(Household c, double perHousehold);
    }

    /**
     * Moves every stock with the people, then sets every cell's count.
     *
     * Savings, debt, and the shares in each company - the same pool, the same
     * weights, one at a time. Adding a stock is adding it to the list below;
     * the arithmetic does not know what it is moving.
     *
     * @param fresh  households in each cell now
     * @param buffer what a newly arrived household of each cell brings, in savings
     */
    private void followThePeople(double[] fresh, double[] buffer) {
        java.util.List<Stock> stocks = new java.util.ArrayList<>();
        stocks.add(new Stock() {
            public double get(Household c) { return c.savings; }
            public void set(Household c, double v) { c.savings = v; }
        });
        stocks.add(new Stock() {
            public double get(Household c) { return c.debt; }
            public void set(Household c, double v) { c.debt = v; }
        });
        for (int k = 0; k < Equity.COMPANIES.length; k++) {
            final int company = k;
            stocks.add(new Stock() {
                public double get(Household c) { return c.shares[company]; }
                public void set(Household c, double v) { c.shares[company] = v; }
            });
        }
        stocks.add(new Stock() {
            public double get(Household c) { return c.abroad; }
            public void set(Household c, double v) { c.abroad = v; }
        });
        // The student loan follows the graduate into a family - and out of
        // the city with one who leaves, which the treasury writes off.
        stocks.add(new Stock() {
            public double get(Household c) { return c.studentDebt; }
            public void set(Household c, double v) { c.studentDebt = v; }
        });

        int n = cells.length;
        double[] before = new double[n];
        for (int i = 0; i < n; i++) before[i] = cells[i].households;
        double[] beforeLoans = carryGraduatesLoans(before);

        double[] left = new double[stocks.size()];
        for (int k = 0; k < stocks.size(); k++) {
            boolean loans = k == stocks.size() - 1;
            left[k] = moveStock(stocks.get(k), fresh, loans ? beforeLoans : before, k == 0 ? buffer : null);
        }
        // What nobody claimed has left the city: the savings with them, the
        // debt on the bank, the shares to wherever they went - held abroad
        // from now on, as far as the register is concerned.
        lastTakenAway += left[0];
        lastWrittenOff += left[1];
        for (int k = 0; k < Equity.COMPANIES.length; k++) lastSharesTakenAway[k] = left[2 + k];
        // ...and the dollars they held abroad go with them: already abroad,
        // so nothing crosses the border - a stock that changes hands.
        lastAbroadTakenAway = left[2 + Equity.COMPANIES.length];
        lastStudentDebtTakenAway = left[3 + Equity.COMPANIES.length];

        for (int i = 0; i < n; i++) {
            // A cell that lost households keeps its average: the ones who
            // stayed have what they had.
            cells[i].households = fresh[i];
            if (fresh[i] <= 0) cells[i].clearAll();
        }
    }

    /**
     * The graduates' student loans, handed to the working families before the
     * census moves anything. See setGraduates().
     *
     * Straight to the families, by grown-ups, and not through the pool: the
     * pool is anonymous, and in a college that takes in as many as it lets out
     * the only household claiming what the graduates released would be the
     * freshers. Only the loan goes this way. Their savings and everything else
     * still move on the net change, as every other household's do.
     *
     * @param before every cell's households coming into the month
     * @return the same, less the graduates on the students cell - the loans
     *         the census still has to move are carried by the ones who stayed
     */
    private double[] carryGraduatesLoans(double[] before) {
        double[] out = before.clone();
        lastGraduated = 0;
        double leaving = graduating;
        graduating = 0;
        Household s = students();
        if (s == null || leaving <= 0 || s.households <= 0) return out;

        double weight = 0;
        for (Household c : cells) {
            if (c instanceof WorkingHousehold && c.households > 0) weight += c.households * c.grownUps();
        }
        if (weight <= 0) return out;            // nobody working to carry it: it stays with the students

        double graduates = Math.min(leaving, s.households);
        double carried = s.studentDebt * graduates;
        for (Household c : cells) {
            if (c instanceof WorkingHousehold && c.households > 0) {
                c.studentDebt += carried * c.grownUps() / weight;
            }
        }
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] == s) out[i] = s.households - graduates;
        }
        lastGraduated = graduates;
        return out;
    }

    /**
     * One stock through the pool: released by the cells that shrank at their
     * own average, claimed by the cells that grew - within the row first, then
     * across the city - and what nobody claimed returned.
     *
     * @param before  the households each cell's stock was carried by coming in:
     *                the cell's count, except where some already left with it
     *                (the graduates and their loans - see carryGraduatesLoans())
     * @param arrival what a household nobody released brings of this stock,
     *                per cell, or null for nothing
     * @return the total of this stock that left the city
     */
    private double moveStock(Stock stock, double[] fresh, double[] before, double[] arrival) {
        int n = cells.length;
        double[] rowLoss = new double[ROWS];
        double[] rowGain = new double[ROWS];
        double[] rowPool = new double[ROWS];

        // Matched within the cell's stock group first - its own row, except for
        // the out of work, who move with the unskilled tier. See Household.stockGroup().
        double[] delta = new double[n];
        for (int i = 0; i < n; i++) {
            Household c = cells[i];
            delta[i] = fresh[i] - before[i];
            double weight = Math.abs(delta[i]) * c.grownUps();
            if (delta[i] < 0) {
                rowLoss[c.stockGroup()] += weight;
                rowPool[c.stockGroup()] += stock.get(c) * -delta[i];
            } else if (delta[i] > 0) {
                rowGain[c.stockGroup()] += weight;
            }
        }

        /* ---- within each row first ---- */
        double[] rowMoved = new double[ROWS];
        double cityPool = 0, cityLoss = 0, cityGain = 0;
        for (int r = 0; r < ROWS; r++) {
            double moved = Math.min(rowLoss[r], rowGain[r]);
            rowMoved[r] = moved;
            if (rowLoss[r] > 0) {
                double kept = moved / rowLoss[r];
                cityPool += rowPool[r] * (1 - kept);
                rowPool[r] *= kept;
                cityLoss += rowLoss[r] - moved;
            }
            cityGain += rowGain[r] - moved;
        }

        /* ---- then across the city ---- */
        double cityMoved = Math.min(cityLoss, cityGain);
        double cityKept = cityLoss > 0 ? cityMoved / cityLoss : 0;
        double gone = cityPool * (1 - cityKept);
        cityPool *= cityKept;
        double cityShare = cityGain > 0 ? cityMoved / cityGain : 0;

        /* ---- and hand it out ---- */
        for (int i = 0; i < n; i++) {
            Household c = cells[i];
            if (delta[i] <= 0 || c.grownUps() <= 0) continue;
            int r = c.stockGroup();
            double weight = delta[i] * c.grownUps();
            double fromRow = rowGain[r] > 0 ? weight * rowMoved[r] / rowGain[r] : 0;
            double rest = weight - fromRow;
            double fromCity = rest * cityShare;
            double newcomers = (rest - fromCity) / c.grownUps();

            double received = (rowMoved[r] > 0 ? rowPool[r] * fromRow / rowMoved[r] : 0)
                    + (cityMoved > 0 ? cityPool * fromCity / cityMoved : 0);
            double brought = arrival == null ? 0 : newcomers * arrival[i];

            stock.set(c, (stock.get(c) * before[i] + received + brought) / fresh[i]);
        }
        return gone;
    }

    /** Shares of each company that left the city with their holders this month. */
    private final double[] lastSharesTakenAway = new double[Equity.COMPANIES.length];
    public double getSharesTakenAway(int company) { return lastSharesTakenAway[company]; }

    /* =====================================================================
       THE MARKET

       Where a household's shares can be sold and bought. Set by Game once
       the exchange exists; null in a fixture with no market, in which case
       the waterfall goes from savings straight to credit as it did before.
       ===================================================================== */

    private Exchange exchange;
    private Equity register;
    private Bank bank;
    private Household.Liquidity liquidity;

    public void setMarket(Exchange exchange, Equity register, Bank bank) {
        this.exchange = exchange;
        this.register = register;
        this.bank = bank;
        this.liquidity = exchange == null || register == null || bank == null ? null
                : (cell, needPer) -> exchange.sellForHousehold(register, bank, cell, needPer);
    }

    /**
     * The households buy shares of one company on the exchange, each cell
     * with money past its cushion putting a share of the excess in, pro rata
     * when the desk cannot sell them all they want.
     *
     * @param fraction of the excess each household puts in
     * @param capacity the most cash the desk will take for shares this month
     * @return cash spent in total
     */
    public double buyShares(Exchange exchange, Equity register, Bank bank, int company,
                            double fraction, double capacity) {
        if (exchange == null || capacity <= 0 || fraction <= 0) return 0;
        double[] want = new double[cells.length];
        double total = 0;
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            if (c.households < .5 || !c.canInvest() || c.debt > 0 || c.lockout > 0 || c.isGoingShort()) continue;
            double excess = c.savings - SHARE_CUSHION_MONTHS * Math.max(0, c.disposable);
            if (excess <= 0) continue;
            want[i] = excess * fraction * c.households;
            total += want[i];
        }
        if (total <= 0) return 0;
        double scale = Math.min(1, capacity / total);
        double spent = 0;
        for (int i = 0; i < cells.length; i++) {
            if (want[i] <= 0) continue;
            Household c = cells[i];
            double cash = want[i] * scale;
            double shares = exchange.deskSellsToHouseholds(register, bank, company, cash);
            if (shares <= 0) continue;
            c.savings -= cash / c.households;
            c.shares[company] += shares / c.households;
            spent += cash;
        }
        return spent;
    }

    /**
     * The same, across several companies in order of preference: each cell's
     * month's money goes into the first while the desk can sell it, then the
     * next. What no company could take stays in savings.
     *
     * @param companies register indices, best first
     * @param capacity  the most cash the desk will take for each, same order
     * @return cash spent in total
     */
    public double buyShares(Exchange exchange, Equity register, Bank bank, int[] companies,
                            double fraction, double[] capacity) {
        if (exchange == null || fraction <= 0 || companies.length == 0) return 0;
        double[] left = new double[cells.length];
        double total = 0;
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            if (c.households < .5 || !c.canInvest() || c.debt > 0 || c.lockout > 0 || c.isGoingShort()) continue;
            double excess = c.savings - SHARE_CUSHION_MONTHS * Math.max(0, c.disposable);
            if (excess <= 0) continue;
            left[i] = excess * fraction * c.households;
            total += left[i];
        }
        double spent = 0;
        for (int k = 0; k < companies.length && total > 0; k++) {
            if (capacity[k] <= 0) continue;
            double scale = Math.min(1, capacity[k] / total);
            int company = companies[k];
            for (int i = 0; i < cells.length; i++) {
                if (left[i] <= 0) continue;
                Household c = cells[i];
                double cash = left[i] * scale;
                double shares = exchange.deskSellsToHouseholds(register, bank, company, cash);
                if (shares <= 0) continue;
                c.savings -= cash / c.households;
                c.shares[company] += shares / c.households;
                left[i] -= cash;
                total -= cash;
                spent += cash;
            }
        }
        return spent;
    }

    /** A split or consolidation: every household's count of the company by the factor. */
    public void splitShares(int company, double k) {
        if (!(k > 0) || k == 1) return;
        for (Household c : cells) c.shares[company] *= k;
        lastSharesTakenAway[company] *= k;
    }

    /** What the households would put into shares this month, in cash, before the desk says how much it can sell. */
    public double sharesWanted(double fraction) {
        double total = 0;
        for (Household c : cells) {
            if (c.households < .5 || !c.canInvest() || c.debt > 0 || c.lockout > 0 || c.isGoingShort()) continue;
            double excess = c.savings - SHARE_CUSHION_MONTHS * Math.max(0, c.disposable);
            if (excess > 0) total += excess * fraction * c.households;
        }
        return total;
    }

    /**
     * A company's tender: every household sells this share of what it holds
     * of the company, at this price, into its savings.
     *
     * @return cash the households received
     */
    public double tenderShares(int company, double fraction, double price) {
        if (!(fraction > 0) || !(price > 0)) return 0;
        double paid = 0;
        for (Household c : cells) {
            if (c.households <= 0 || c.shares[company] <= 0) continue;
            double sell = c.shares[company] * Math.min(1, fraction);
            c.shares[company] -= sell;
            c.savings += sell * price;
            paid += sell * price * c.households;
        }
        return paid;
    }

    /** What the households' shares are worth at the exchange's quote, or at book with no exchange. */
    public double marketValueOfShares() {
        if (exchange == null || register == null) return 0;
        return exchange.marketValueOfHouseholds(register, this);
    }

    /** What the households raised this month selling shares to cover the shop. */
    public double totalSold() {
        double total = 0;
        for (Household c : cells) total += c.sold * c.households;
        return total;
    }

    /* =====================================================================
       THE WORLD'S PAPER

       The households' savings follow the sectors' rule (OutwardInvestment):
       money at a counter paying nothing, while the world pays two percent,
       buys the world's paper, and comes home when the bank pays better or
       the household needs it. The same dials, deliberately - one rule for
       idle money, wherever it sits.

       WHY, measured: the exchange returned the sectors' hoard to the
       households - $16-28bn of dividends and buybacks a run - and the
       households kept it at the bank, so the surplus the sectors had been
       recycling abroad sat at home, and the currency did what it does with a
       surplus nobody recycles: eight seeds went from 10% off parity to 31%
       past it, exports halved, unemployment 11% -> 18%. The money changed
       hands and lost its door. This is the door.
       ===================================================================== */

    /** Local currency per dollar, this month: what the paper abroad is worth here. Set by Game before the strike. */
    private double localPerUsd = ForeignAccounts.OPENING_RATE;

    public void setExchangeRate(double localPerUsd) {
        if (localPerUsd > 0) this.localPerUsd = localPerUsd;
    }

    public double getExchangeRate() { return localPerUsd; }

    /** Dollars the households that left this month took with them. Nothing crosses the border. */
    private double lastAbroadTakenAway;

    /**
     * Every household decides where to keep its idle money: what is past the
     * cushion goes abroad towards the target share, a little a month; what
     * is abroad comes home when the target falls. A household in debt, locked
     * out or going short holds nothing abroad. The coupon is rolled where it
     * is earned. Called once a month, after the shares have traded and before
     * the audit strikes.
     *
     * @param depositRate what the bank pays savers, annual
     * @param worldRate   what the world pays, annual
     * @param rate        local currency per dollar, this month
     */
    public void investAbroad(double depositRate, double worldRate, double rate) {
        if (!(rate > 0)) return;
        localPerUsd = rate;
        double spread = Math.max(0, worldRate - Math.max(0, depositRate));
        double targetShare = Math.min(OutwardInvestment.MAX_SHARE, spread * OutwardInvestment.APPETITE);
        for (Household c : cells) {
            if (c.households < .5) continue;

            // The income first, rolled where it is earned - see OutwardInvestment.
            double earnedUsd = c.abroad * Math.max(0, worldRate) / 12;
            c.abroad += earnedUsd;
            c.foreignInterest = earnedUsd * rate;

            // A prisoner's money is held where it is. See PrisonerHousehold.
            if (!c.canInvest()) continue;

            double held = c.abroad * rate;
            boolean eligible = c.debt <= 0 && c.lockout <= 0 && !c.isGoingShort();
            double cushion = SHARE_CUSHION_MONTHS * Math.max(0, c.disposable);
            double spare = c.savings - cushion;
            double target = eligible && spare + held > 0 ? (spare + held) * targetShare : 0;
            double gap = target - held;
            double move;
            if (gap > 0) {
                move = Math.min(gap * OutwardInvestment.OUT_SPEED, Math.max(0, spare));
            } else {
                move = Math.max(gap * OutwardInvestment.HOME_SPEED, -held);
            }
            if (Math.abs(move) < 1e-12) continue;
            c.savings -= move;
            c.abroad += move / rate;
            if (c.abroad < 1e-15) c.abroad = 0;
            if (move > 0) c.sentAbroad += move; else c.broughtHome += -move;
        }
    }

    /** Dollars every household holds abroad. */
    public double totalAbroadUsd() {
        double total = 0;
        for (Household c : cells) total += c.abroad * c.households;
        return total;
    }

    /** ...worth this much at home, at the month's rate. */
    public double totalAbroadValue() { return totalAbroadUsd() * localPerUsd; }

    /** Sent abroad this month, all households, local money. For MoneyAudit. */
    public double getSentAbroad() {
        double total = 0;
        for (Household c : cells) total += c.sentAbroad * c.households;
        return total;
    }

    /** Brought home this month - to keep to the target or to eat - all households, local money. For MoneyAudit. */
    public double getBroughtHome() {
        double total = 0;
        for (Household c : cells) total += c.broughtHome * c.households;
        return total;
    }

    /** What the world paid the households this month, rolled abroad. For MoneyAudit, twice. */
    public double getForeignInterest() {
        double total = 0;
        for (Household c : cells) total += c.foreignInterest * c.households;
        return total;
    }

    /** Dollars that left with the households that left this month. */
    public double getAbroadTakenAway() { return lastAbroadTakenAway; }

    /* ------------------------ the people outside the families ------------------------ */

    /** Out-of-work households that lost their home this month. Unemployment moves them. */
    public double getEvicted() { return lastEvicted; }

    /** Student loans drawn this month, all students - the treasury's money out. */
    public double totalStudentBorrowed() { return sum(c -> c.studentBorrowed * c.households); }

    /** ...and repaid by graduates, the treasury's money back. */
    public double totalStudentRepaid()   { return sum(c -> c.studentRepaid * c.households); }

    /** What every household owes the treasury in student loans. */
    public double totalStudentDebt()     { return sum(Household::totalStudentDebt); }

    /** Student loans that left the city with graduates who left. The treasury's loss. */
    public double getStudentDebtTakenAway() { return lastStudentDebtTakenAway; }

    /**
     * THE GRADUATES LEAVE THE STUDENT BODY WITH THEIR LOANS.
     *
     * followThePeople() moves money on the NET change in each cell, which is
     * right for a family whose child turned thirteen and wrong for a college.
     * A college that takes in forty a month and lets forty out has a student
     * body that never changes size, so on the net rule nothing ever left it:
     * measured on OutsideCheck's college city, 7,530 lent over fifteen years
     * and 1 repaid, because the loans never reached a household that repays.
     *
     * So the loans take the GROSS flow: each month the ones who finished carry
     * their share of the students' loans to the working families, who repay a
     * 114th a month, and the census moves only what is left - the loans of
     * the students who stayed - on the net change as before.
     *
     * @param people who finished a course, and so left the students cell,
     *               since the census it was struck against. Used once, by the
     *               next advanceMonth().
     */
    public void setGraduates(double people) { graduating = Math.max(0, people); }

    /** Students who left the student body with what they carried, at the last month's census. */
    public double getLastGraduated() { return lastGraduated; }

    /* ------------------------- what the bank is owed ------------------------- */

    /** Written off this month, which is the bank's loss. */
    public double getWrittenOff()      { return lastWrittenOff; }

    /** Savings that left the city with the households that left. Not a loss to anybody here. */
    public double getTakenAway()       { return lastTakenAway; }

    /** Households discharged this month, and the share of them leaving the city. */
    public double getBankrupt(int row) { return sumRow(row, Household::bankrupt); }
    public double getLeavingCity()     { return lastLeaving; }

    /** True when any cell of the row the bank has stopped lending to is still locked out. */
    public boolean isLockedOut(int row){ return getLockout(row) > 0; }

    /** The longest lockout standing in the row. */
    public int getLockout(int row) {
        int longest = 0;
        for (Household c : cells) {
            if (c.row() == row && c.households >= .5) longest = Math.max(longest, c.lockout);
        }
        return longest;
    }

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
     * been settled - a second draw on the same savings - and would move the
     * stock with a census the save was not struck against.
     */
    public void planOnly(ToDoubleBiFunction<FamilyStructure, PayTier> census,
                         double[] rowDisposable, double rentPerHousehold,
                         double[] rowFees, double foodPricePerHead, double riskFreeAnnual) {

        rowDisposable = padRows(rowDisposable);
        rowFees = padRows(rowFees);
        if (census == null || rowDisposable == null || rowDisposable.length != ROWS) {
            return;
        }

        double[] fresh = takeCensus(census);
        double[] disposablePer = splitIncome(fresh, rowDisposable);
        double[] feesPer = splitByPeople(fresh, rowFees);

        plannedSpend = 0;
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            c.households = fresh[i];
            if (c.households < .5) { c.clearWorking(); continue; }
            c.rentShare = Math.max(0, rentShares.applyAsDouble(c));
            c.restrike(disposablePer[i], rentPerHousehold * c.rentShare, feesPer[i],
                    foodPricePerHead, riskFreeAnnual);
            plannedSpend += c.plan() * c.households;
        }
    }

    /* ------------------------------- reading ------------------------------- */

    /** What the shops can sell next month, in money. The budget constraint itself. */
    public double getSpendingCapacity() { return plannedSpend; }

    /** What they would spend if money were no object - the demand behind the cap. */
    public double getWantedSpend() { return sum(Household::totalWant); }

    /** One basket a head, per household of the row: what going short is measured against. */
    public double getSubsistence(int row) { return perHousehold(row, Household::subsistence); }

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
        for (Household c : cells) {
            out[c.row()] += c.totalPlanned();
            total += c.totalPlanned();
        }
        if (total > 0) for (int r = 0; r < ROWS; r++) out[r] /= total;
        else java.util.Arrays.fill(out, 0);
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
        for (Household c : cells) {
            if (c.savings <= 0 || c.households <= 0) continue;
            double share = c.totalSavings() / base;
            c.savings += total * share / c.households;
        }
        lastDepositInterest = total;
    }

    /* =====================================================================
       THEFT (2026-09-11)

       Jerus: crime is "theft, people leaving, violence", and the stolen money
       "goes to the offenders". Two halves, both here because both are cells'
       savings: what is taken, from every household in proportion to what it
       has saved, and what the offenders' households are handed, in proportion
       to how much of the crime is theirs. Every dollar stays on the books - a
       theft moves money, it does not destroy it. See Crime.
       ===================================================================== */

    /**
     * Takes up to `total` from the households' savings, each cell in
     * proportion to what it has saved. A prisoner's savings are held in the
     * ledger and are not there to take.
     *
     * @return what was actually taken, which is less than asked when the city
     *         has less saved than that
     */
    public double takeFromSavings(double total) {
        if (!(total > 0)) return 0;
        double base = 0;
        for (Household c : cells) {
            if (!c.canInvest() || c.households <= 0 || c.savings <= 0) continue;
            base += c.savings * c.households;
        }
        if (base <= 0) return 0;
        double taken = Math.min(total, base);
        double share = taken / base;
        for (Household c : cells) {
            if (!c.canInvest() || c.households <= 0 || c.savings <= 0) continue;
            c.savings -= c.savings * share;
        }
        return taken;
    }

    /**
     * Credits `total` to the cells in proportion to a weight per cell, in the
     * cells' order - what the offenders' households took home.
     *
     * @return what was credited: all of it, or nothing if no cell has weight
     */
    public double creditByWeight(double total, double[] weight) {
        if (!(total > 0) || weight == null || weight.length != cells.length) return 0;
        double sum = 0;
        for (int i = 0; i < cells.length; i++) {
            if (cells[i].households > 0 && weight[i] > 0) sum += weight[i];
        }
        if (sum <= 0) return 0;
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            if (c.households <= 0 || !(weight[i] > 0)) continue;
            c.savings += total * weight[i] / sum / c.households;
        }
        return total;
    }

    /** What the bank paid the city's savers this month. */
    public double getDepositInterest() { return lastDepositInterest; }

    /* =====================================================================
       THE OFFER

       Jerus: "each household type gets the offer and based on their situation
       and their cash available they accept or decline, or put how much."

       The situation: no debt, not locked out, not going short, and savings
       past a cushion. The cash available: what is past the cushion. Every
       household uses the same rule - one cushion, one fraction, the retired
       included - which was Jerus's call against a keener rich; the rich still
       buy more because they have more past the cushion.
       ===================================================================== */

    /** Months of take-home a household keeps in the bank before it buys a share. */
    public static final double SHARE_CUSHION_MONTHS = 3;

    /** The share of what is past the cushion it puts into one offering. */
    public static final double SHARE_OF_EXCESS = .30;

    /**
     * Puts an offering to every household, and takes up what they will buy.
     *
     * Pro rata when they would buy more than is offered, so a small offering
     * is not taken entirely by whichever cell the loop reached first. Savings
     * go down by what was paid and the shares go up by what it bought; net
     * worth does not move.
     *
     * @param company  Equity.COMPANIES index
     * @param offered  the money the company is asking for
     * @param price    per share
     * @return the money taken up
     */
    public double subscribe(int company, double offered, double price) {
        if (offered <= 0 || !(price > 0)) return 0;

        double[] want = new double[cells.length];
        double total = 0;
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            if (c.households < .5 || !c.canInvest() || c.debt > 0 || c.lockout > 0 || c.isGoingShort()) continue;
            double cushion = SHARE_CUSHION_MONTHS * Math.max(0, c.disposable);
            double excess = c.savings - cushion;
            if (excess <= 0) continue;
            want[i] = excess * SHARE_OF_EXCESS * c.households;
            total += want[i];
        }
        if (total <= 0) return 0;

        double scale = Math.min(1, offered / total);
        double taken = 0;
        for (int i = 0; i < cells.length; i++) {
            if (want[i] <= 0) continue;
            Household c = cells[i];
            double paidPer = want[i] * scale / c.households;
            c.savings -= paidPer;
            c.shares[company] += paidPer / price;
            taken += paidPer * c.households;
        }
        return taken;
    }

    /**
     * Hands out a company's founding shares to the people who founded it.
     *
     * A company's first offering finds equity already on its books - the
     * stores and homes the founding endowment built - and owned by nobody.
     * Somebody has to own it or the first buyer is handed it for free, so it
     * goes to the city's households, by grown-ups: the founders own what
     * they founded. Nothing is paid; net worth rises by what was always
     * theirs and was never written down. See Equity.offer().
     */
    public void grantFounders(int company, double shares) {
        if (!(shares > 0)) return;
        double weight = 0;
        for (Household c : cells) if (c.households >= .5) weight += c.households * c.grownUps();
        if (weight <= 0) return;
        for (Household c : cells) {
            if (c.households < .5) continue;
            c.shares[company] += shares * c.grownUps() / weight;
        }
    }

    /**
     * Pays every household its dividend, straight into its savings.
     *
     * @return what the city's households received in total
     */
    public double creditDividend(int company, double perShare) {
        if (!(perShare > 0)) return 0;
        double paid = 0;
        for (Household c : cells) {
            if (c.households <= 0 || c.shares[company] <= 0) continue;
            double each = c.shares[company] * perShare;
            c.savings += each;
            c.dividends += each;
            paid += each * c.households;
        }
        return paid;
    }

    /** Shares of this company the city's households hold between them. */
    public double sharesHeld(int company) {
        double total = 0;
        for (Household c : cells) total += c.shares[company] * c.households;
        return total;
    }

    /** What the households were paid in dividends this month. */
    public double totalDividends() {
        double total = 0;
        for (Household c : cells) total += c.dividends * c.households;
        return total;
    }

    /* ---- the row, per household of it: what the screens and the fixtures read ---- */

    public double getSavings(int row)        { return perHousehold(row, Household::savings); }

    /** Households this row was struck for - the multiplier on every per-row figure. */
    public double getHouseholds(int row)     { return rowHouseholds(row); }
    public double getDebt(int row)           { return perHousehold(row, Household::debt); }
    public double getAfterFixed(int row)     { return perHousehold(row, Household::afterFixed); }
    public double getInterest(int row)       { return perHousehold(row, Household::interest); }
    public double getDrawn(int row)          { return perHousehold(row, Household::drawn); }

    /** What this row wanted, could not fund, and did not get. See Household.unfunded. */
    public double getUnfunded(int row)       { return perHousehold(row, Household::unfunded); }

    /** True when the bank has stopped lending to any cell of this row - ceiling or lockout. */
    public boolean isCutOff(int row) {
        for (Household c : cells) if (c.row() == row && c.isCutOff()) return true;
        return false;
    }
    public double getBorrowed(int row)       { return perHousehold(row, Household::borrowed); }
    public double getRepaid(int row)         { return perHousehold(row, Household::repaid); }
    public double getBanked(int row)         { return perHousehold(row, Household::banked); }
    public double getWant(int row)           { return perHousehold(row, Household::want); }
    public double getPlanned(int row)        { return perHousehold(row, Household::planned); }
    public double getRate(int row)           { return perHousehold(row, Household::rate); }

    /** True when this row is buying less food than it wants. */
    public boolean isGoingShort(int row) {
        double want = sumRow(row, Household::totalWant);
        return want > 0 && sumRow(row, Household::totalPlanned) < want - 1e-9;
    }

    /** City totals, for the headline lines on the screen. */
    public double totalSavings()  { return sum(Household::totalSavings); }
    public double totalDebt()     { return sum(Household::totalDebt); }
    public double totalInterest() { return sum(Household::totalInterest); }

    /** New lending to families this month - the bank's money out the door. */
    public double totalBorrowed() { return sum(Household::totalBorrowed); }

    /** ...and what came back. */
    public double totalRepaid()   { return sum(Household::totalRepaid); }

    /** What every household in the city has, less what it owes. */
    public double totalNetWorth() { return totalSavings() - totalDebt(); }

    /* ------------------------------- saving -------------------------------
     *
     * A STOCK, so it has to be carried. Savings and debt are the two things on
     * this screen that are not derived from the month - rebuilding them from
     * the state a month ended in is exactly the reconstruction this codebase
     * has been caught by six times. Most of the last-month working is NOT
     * saved: it is a flow, it is recomputed on the first tick, and carrying it
     * would be carrying an answer nobody would re-ask.
     *
     * TWO ARRAYS. The cells are the state now and go under their own key,
     * named cell by cell (see cellKeys()), so a shape added to the enum or
     * moved within it cannot read one cell's money into another's. The row
     * array is still written, as the SUM of the cells, for one reason: a build
     * from before the cells reads it and restores the rows it knows, so an old
     * build opening a new save loses nothing it can hold. A new build opening
     * an old save seeds every cell of a row with the row's position - see
     * restore(). Neither direction moves SAVE_FORMAT.
     */

    /** The row array an older build reads: ROWS*8+3, per household of the row. */
    public double[] toSaveArray() {
        double[] out = new double[ROWS * 8 + 3];
        for (int r = 0; r < ROWS; r++) {
            out[r]            = getSavings(r);
            out[ROWS + r]     = getDebt(r);
            // The lockout is a countdown, which is a STOCK: a tier discharged
            // last month is eleven months from borrowing again, and a save
            // that forgot it would hand that tier a fresh line of credit.
            out[ROWS * 2 + r] = getLockout(r);
            /*
             * ...AND THE HOUSEHOLD COUNTS THE STOCKS ARE PER. The per-household
             * figures times this array are the city's whole stock of household
             * money, and unsaved it read a third out for one month after every
             * load - $1.29M against $966k - which came out as a wrong bank
             * profit, a wrong bank tax and $13 of treasury a month later. What
             * SaveFileCheck caught.
             */
            out[ROWS * 3 + r] = rowHouseholds(r);
            /*
             * ...AND THE WORKING THE NEXT MONTH IS READ AGAINST. The shops size
             * their month against getSpendingCapacity() and getWantedSpend(),
             * the advisor against the same two, all before the tick that would
             * recompute them; a reloaded city planned its twelfth month against
             * a rebuilt eleventh and came out $13 adrift on a $690.7M treasury.
             * Small, and it compounds.
             */
            out[ROWS * 4 + r] = getWant(r);
            out[ROWS * 5 + r] = getPlanned(r);
            out[ROWS * 6 + r] = getInterest(r);
            out[ROWS * 7 + r] = getSubsistence(r);
        }
        out[ROWS * 8]     = plannedSpend;
        out[ROWS * 8 + 1] = hungryPeople;
        out[ROWS * 8 + 2] = totalPeople;
        return out;
    }

    /** Figures carried per cell before the shares were appended (2026-09-10, evening). */
    public static final int CELL_SLOTS_BEFORE_SHARES = 8;

    /** ...and before the dollars abroad were (2026-09-11). */
    public static final int CELL_SLOTS_BEFORE_ABROAD = CELL_SLOTS_BEFORE_SHARES + Equity.COMPANIES.length;

    /** ...and before the student loans were (2026-09-11, afternoon). */
    public static final int CELL_SLOTS_BEFORE_STUDENT_DEBT = CELL_SLOTS_BEFORE_ABROAD + 1;

    /** Figures carried per cell, in the order toCellSaveArray() writes them: the eight, a share count per company, the dollars abroad, the student loan. */
    public static final int CELL_SLOTS = CELL_SLOTS_BEFORE_STUDENT_DEBT + 1;

    /** The name of every cell, in the order toCellSaveArray() writes them. */
    public String[] cellKeys() {
        String[] keys = new String[cells.length];
        for (int i = 0; i < cells.length; i++) keys[i] = cells[i].key();
        return keys;
    }

    /** CELL_SLOTS per cell, in cellKeys() order, then the three city figures. */
    public double[] toCellSaveArray() {
        double[] out = new double[cells.length * CELL_SLOTS + 3];
        int i = 0;
        for (Household c : cells) {
            out[i++] = c.savings;
            out[i++] = c.debt;
            out[i++] = c.lockout;
            out[i++] = c.households;
            out[i++] = c.want;
            out[i++] = c.planned;
            out[i++] = c.interest;
            out[i++] = c.subsistence;
            for (double held : c.shares) out[i++] = held;
            out[i++] = c.abroad;
            out[i++] = c.studentDebt;
        }
        out[i++] = plannedSpend;
        out[i++] = hungryPeople;
        out[i]   = totalPeople;
        return out;
    }

    /**
     * Puts the cells back, by name.
     *
     * A key this build has no cell for is skipped - a shape that no longer
     * exists - and a cell the save does not name keeps whatever restore() put
     * in it from the rows, which for a save from this build is nothing: an
     * empty cell, filled by the first census as an arrival. Refused whole when
     * the array is not the keys' length.
     *
     * @return false if nothing was restored
     */
    public boolean restoreCells(String[] keys, double[] saved) {
        if (keys == null || saved == null || keys.length == 0) return false;
        // Eight a cell from the morning the cells went in, eight plus a share
        // count per company from the evening, or those plus the dollars abroad
        // from the next day. Any of the three restores what it carries; a save
        // without shares has households that own none, one without the
        // dollars has households that hold none.
        int slots = (saved.length - 3) / keys.length;
        if (saved.length != keys.length * slots + 3
                || (slots != CELL_SLOTS && slots != CELL_SLOTS_BEFORE_STUDENT_DEBT
                    && slots != CELL_SLOTS_BEFORE_ABROAD && slots != CELL_SLOTS_BEFORE_SHARES)) {
            return false;
        }
        java.util.Map<String, Household> byKey = new java.util.HashMap<>();
        for (Household c : cells) byKey.put(c.key(), c);

        int i = 0;
        for (String key : keys) {
            Household c = byKey.get(key);
            if (c == null) { i += slots; continue; }
            c.savings     = saved[i++];
            c.debt        = saved[i++];
            c.lockout     = (int) Math.round(saved[i++]);
            c.households  = saved[i++];
            c.want        = saved[i++];
            c.planned     = saved[i++];
            c.interest    = saved[i++];
            c.subsistence = saved[i++];
            java.util.Arrays.fill(c.shares, 0);
            c.abroad = 0;
            c.studentDebt = 0;
            if (slots >= CELL_SLOTS_BEFORE_ABROAD) {
                for (int k = 0; k < c.shares.length; k++) c.shares[k] = saved[i++];
            }
            if (slots >= CELL_SLOTS_BEFORE_STUDENT_DEBT) c.abroad = Math.max(0, saved[i++]);
            if (slots >= CELL_SLOTS) c.studentDebt = Math.max(0, saved[i++]);
        }
        plannedSpend = saved[i++];
        hungryPeople = saved[i++];
        totalPeople  = saved[i];
        return true;
    }

    /**
     * Puts a ROW array back, seeding every cell of the row with the row's
     * position: the save from a build that kept the stocks per tier.
     *
     * @param saved  ROWS*8+3 from a build with the row working, or ROWS*3 from
     *               one before the household counts and the working were
     *               appended. A short array restores what it carries and
     *               leaves the rest to the rebuild.
     * @param census the household counts to seed the cells with, or null to
     *               leave them empty for the plan to fill - see Game's load
     *               path, which restores once before the family model is back
     *               and once after.
     */
    public void restore(double[] saved, ToDoubleBiFunction<FamilyStructure, PayTier> census) {
        if (saved == null) return;
        /*
         * THE ROW ARRAY FROM TODAY'S BUILD, OR FROM ONE BEFORE THE PEOPLE
         * OUTSIDE THE FAMILIES had rows (seven: the tiers and the retired).
         * A seven-row array restores the rows it knows; the new rows wait for
         * the census, as arrivals.
         */
        int rows;
        boolean current;
        int beforePrison = Household.ROWS_BEFORE_PRISON;
        if (saved.length == ROWS * 8 + 3)                     { rows = ROWS; current = true; }
        else if (saved.length == ROWS * 3)                    { rows = ROWS; current = false; }
        // ...or from the day before the prisons, with no prisoners' row.
        else if (saved.length == beforePrison * 8 + 3)        { rows = beforePrison; current = true; }
        else if (saved.length == beforePrison * 3)            { rows = beforePrison; current = false; }
        else if (saved.length == ROWS_BEFORE_OUTSIDE * 8 + 3) { rows = ROWS_BEFORE_OUTSIDE; current = true; }
        else if (saved.length == ROWS_BEFORE_OUTSIDE * 3)     { rows = ROWS_BEFORE_OUTSIDE; current = false; }
        else return;   // refused whole
        double[] fresh = census == null ? new double[cells.length] : takeCensus(census);
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            int r = c.row();
            c.households = fresh[i];
            if (r >= rows) continue;
            c.savings = saved[r];
            c.debt = saved[rows + r];
            c.lockout = (int) Math.round(saved[rows * 2 + r]);
            if (current) {
                c.want        = saved[rows * 4 + r];
                c.planned     = saved[rows * 5 + r];
                c.interest    = saved[rows * 6 + r];
                c.subsistence = saved[rows * 7 + r];
            }
        }
        if (current) {
            plannedSpend = saved[rows * 8];
            hungryPeople = saved[rows * 8 + 1];
            totalPeople  = saved[rows * 8 + 2];
        }
    }

    /** The row array alone, with no census: the cells wait for the plan to count them. */
    public void restore(double[] saved) { restore(saved, null); }

    public void reset() {
        for (Household c : cells) c.clearAll();
        java.util.Arrays.fill(lastSharesTakenAway, 0);
        lastWrittenOff = 0;
        lastLeaving = 0;
        lastEvicted = 0;
        lastStudentDebtTakenAway = 0;
        lastTakenAway = 0;
        graduating = 0;
        lastGraduated = 0;
        lastAbroadTakenAway = 0;
        lastDepositInterest = 0;
        lastDelivered = 1;
        plannedSpend = 0;
        hungryPeople = 0;
        totalPeople = 0;
    }

    /**
     * The households' stocks and this month's working, in the new unit.
     *
     * Rates, lockouts, household counts and headcounts do not move; the
     * delivered share is a share.
     */
    public void redenominate(double scale) {
        lastWrittenOff *= scale;
        lastTakenAway *= scale;
        lastStudentDebtTakenAway *= scale;
        localPerUsd *= scale;
        plannedSpend *= scale;
        lastDepositInterest *= scale;
        forEach(c -> c.redenominate(scale));
    }

}
