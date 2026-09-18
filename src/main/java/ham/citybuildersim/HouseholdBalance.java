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

    /* =====================================================================
       AND A HOUSEHOLD SPENDS OUT OF WHAT IT HAS, NOT ONLY OUT OF WHAT IT
       EARNS (2026-09-17)

       Jerus, having been shown where the money went: yes, "a wealth term in
       the plan".

       WHY NOTHING ELSE COULD HAVE WORKED, and it was measured in that order
       rather than argued. Two changes went in first. Paying the foreign coupon
       home instead of rolling it abroad took household net worth from 5,380
       months of the city's GDP to 3,412. Then letting dividends and that coupon
       into the spending plan - which is plainly right and which Jerus asked for
       - put it back up to 5,465, HIGHER than where it started.

       THAT IS NOT A PARADOX AND IT IS THE WHOLE LESSON. Investment income is a
       FLOW. Spending more of a flow makes the city bigger; a bigger city pays
       more wages and more dividends; MARGINAL_PROPENSITY saves a fifth of all
       of it. Every term in the ratio grew together and the ratio did not move.
       A STOCK THAT COMPOUNDS CAN ONLY BE BOUNDED BY A DRAIN THAT READS THE
       STOCK. Nothing that reads a flow can do it, however large.

       FOUR PENCE IN THE POUND A YEAR is where the empirical work sits - the
       marginal propensity to consume out of wealth is measured at three to five
       cents on the dollar a year across a lot of countries and a lot of decades
       - and it is a pure ratio, so a currency reform cannot touch it.

       AND IT IS SELF-STABILISING, which is the property to want. The households'
       money abroad compounds at the world's rate on the share they keep there:
       about 1.6% a year, mechanically, whatever the city does. A drain of four
       percent a year on the whole stock beats that at every level, so the stock
       has a ceiling instead of an exponent - and the richer the households get,
       the harder they spend, which is both true of people and the only shape
       that closes the loop.
       ===================================================================== */

    /**
     * What share of its net worth a household spends in a month, over and above
     * what it spends out of income.
     *
     * A third of a percent a month is four percent a year, the middle of the
     * measured range. A pure number: both sides are money and a reform moves
     * neither.
     */
    public static final double WEALTH_SPENT_A_MONTH = .0033;

    /**
     * What share of the income a household does NOT spend on food goes over a
     * luxury counter instead of into the bank.
     *
     * A LUXURY IS A GOOD WHOSE SHARE RISES WITH INCOME - that is the
     * definition, and it is the other half of Engel's law, which this model
     * has only ever had the food half of. `want` above takes
     * MARGINAL_PROPENSITY of the surplus for the grocer and the shape of
     * Consumption's basket means most of that is refused past appetite; what
     * is left is the income a rich household has and a poor one does not.
     * Half of it, here.
     *
     * AND IT IS A SHARE RATHER THAN A CURVE, deliberately, for now. The
     * genuinely income-elastic part of this is the wealth term beside it,
     * which is unbounded and is where the fortunes go; this line is the
     * ordinary earner buying something nice, and a constant is an honest
     * description of that until something measures otherwise.
     */
    public static final double LUXURY_SHARE_OF_SURPLUS = .5;

    /* =====================================================================
       ...AND WHAT IT SPENDS EATING OUT (2026-09-18)

       A QUARTER OF THE SAME SURPLUS, so the two together take three quarters
       of it and a household that got everything it asked for still banks the
       rest. Smaller than the counter's half, because eating out has a ceiling
       the counter does not: a person eats ninety meals a month and a fortune
       buys DEARER ones, not more of them. See Household.plan().

       NO WEALTH TERM for the same reason, which is the whole difference
       between this sector and the boutiques as a drain on the hoard - and the
       reason this is the one that feeds anybody.
       ===================================================================== */
    public static final double MEAL_SHARE_OF_SURPLUS = .25;

    /**
     * ...and the share of a FORTUNE'S monthly spend that goes on a table.
     *
     * Half of what the counter gets, and additive with it rather than a
     * partition of it - the same "adds without subtracting" the luxury term
     * itself was written under. Without this the sector has no demand at all
     * in any city rich enough to matter, because `want` carries the wealth
     * term and the surplus is therefore zero; see Household.plan().
     */
    public static final double MEAL_SHARE_OF_WEALTH = .5;

    /* =====================================================================
       AND THE CEILING, WHICH IS IN MEALS AND NOT IN MONEY (2026-09-18)

       A person eats ninety meals a month whatever they are worth, and a third
       of them is one a day - about the most anybody eats out. This is what
       stops a fortune buying a ninety-first dinner, and it is the reason the
       wealth term above is safe: money says what a household CAN spend, and
       appetite says how many dinners it can actually get through.

       THE SAME SHAPE CONSUMPTION PUTS ON THE BASKET, and for the same reason -
       a rich household buys a DEARER meal, which the margin gives it, not
       more of them. Applied at the counter rather than in the plan because
       that is the only place a meal has a price.
       ===================================================================== */
    public static final double MOST_MEALS_EATEN_OUT = 1 / 3.0;

    /* =====================================================================
       AND IT IS NOT ENOUGH, WHICH IS THE REAL ANSWER (2026-09-17)

       This term does what it was built to do and the stock still diverges: 85
       months of GDP at month 240, 3,982 by month 3,840. Slower than before -
       5,072 without it - and the same exponent.

       SO THE COEFFICIENT WAS PUSHED TO THIRTY-SIX PERCENT A YEAR, nine times
       the empirical range and far past anything defensible, purely to find out
       whether the consumption side CAN bound this. It cannot. The stock came
       down a great deal, wandered instead of climbing smoothly - and ended at
       946 months, still rising. What fell instead was DELIVERY: the shops got
       from 76% of what was planned to 57%.

       BECAUSE THE BINDING CONSTRAINT IS NOT THE CONSUMPTION FUNCTION. It is
       that the city cannot sell its households what they already want to buy.
       A quarter of every plan goes undelivered at the honest coefficient and
       nearly half at the absurd one, and WHATEVER IS NOT DELIVERED IS BANKED,
       by construction - settle() has nowhere else to put it. Wanting to spend
       more money in a city with nothing left on the shelf does not spend it;
       it just moves the shortage.

       The households are not hoarding because they are misers. They are
       hoarding because there is nothing to buy. That is a supply question and
       it is the next one - see , which
       asked half of it before any of this was measured.
       ===================================================================== */

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

    /**
     * Dependants living in each cell the family matrix does not hold.
     *
     * A LIVE FUNCTION, like the census and the rent shares beside it, and for
     * the same reason: it is FamilyModel's figure, re-derived every month, and
     * a copy kept here would be a second version of it that drifts. Set by Game
     * off FamilyModel.dependantsPerOutsideHousehold(); nobody has anybody by
     * default, so a fixture that never sets it has the city it always had.
     */
    private ToDoubleFunction<Household> outsideDependants = c -> 0;

    public void setOutsideDependants(ToDoubleFunction<Household> kin) {
        this.outsideDependants = kin == null ? c -> 0 : kin;
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
        carsToReplace = new double[cells.length];
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
        /*
         * WHAT THE SHOPS ACTUALLY HANDED OVER, as a share of what was asked
         * for (2026-09-17). Game passes Retail.getHouseholdShare() now; it
         * used to pass getSupplyRatio(), whose denominator the shops had
         * already capped at their own coverage.
         *
         * AND THE DIFFERENCE IS THE WHOLE OF WHETHER THE LINE BELOW MEANS
         * ANYTHING. `ate` is a household's plan times this share, so with the
         * honest denominator the eating adds up to what was sold, and with the
         * old one it added up to more than the shops had. The city was being
         * fed groceries that were never on a shelf - not in money, which came
         * from the real takings, but in the one number anybody reads to ask
         * whether people are being fed.
         */
        double delivered = Math.max(0, Math.min(1, supplyRatio));
        lastDelivered = delivered;
        /* =================================================================
           ...AND A MEAL OUT IS FOOD (2026-09-18)

           Jerus's rule for the kitchens: *a meal out replaces groceries*. This
           line is where it is true of anything. A restaurant meal is one
           ninetieth of a person-month of eating, and one person-month of
           eating costs `foodPricePerHead` - so what the household ate out is
           worth that much of the subsistence it would otherwise have had to
           buy at a shop.

           IT IS PRICED AT THE GROCER'S, NOT AT THE RESTAURANT'S, and that is
           the whole of the honesty here. A kitchen charges two and a half to
           five times what the food in the plate cost, and NONE of that markup
           is nourishment: it is wages, rent and somebody else's washing up.
           Counting the ticket would have a city fill its stomachs by putting
           its prices up.

           WHAT THIS BUYS THE MODEL is a second door. Retail's binding
           constraint is coverage times the operating rate - people the shops
           can physically serve - and a city at that wall has been unable to
           feed anybody a different way. Now it can build kitchens, and they
           compete for the same thirteen foods, which is exactly the pressure
           on the food price Jerus asked for when he chose to let the basket
           get dearer rather than bigger.

           A MONTH LATE, AND ON THE RIGHT SIDE OF THE BOUNDARY. `ate` is
           already last month's plan against last month's delivery - the shops
           sold against the plan this line is reading - so last month's meals
           belong beside it. See Household.mealsEaten.
           ================================================================= */
        double subsistencePerMeal = Math.max(0, foodPricePerHead)
                * ham.citybuildersim.sectors.Restaurants.PERSON_MONTHS_PER_MEAL;
        lastMealsEaten = 0;
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            double people = fresh[i] * c.headcount();
            totalPeople += people;
            lastMealsEaten += c.mealsEaten * c.households;
            double ate = c.planned * delivered + c.mealsEaten * subsistencePerMeal;
            if (c.subsistence > 0 && ate < c.subsistence) {
                hungryPeople += people * (1 - ate / c.subsistence);
            }
            /*
             * READ, THEN CLEARED, here rather than in clearWorking(), because
             * this field is the one thing in the cell that is deliberately a
             * month old. Leaving it would feed a household the same dinner
             * every month for ever.
             */
            c.mealsEaten = 0;
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
            shopWeight[i] = rowHasPlan[c.row()] ? c.planned * c.households : fresh[i] * c.headcount();
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

            plannedSpend += c.plan(localPerUsd) * c.households;
        }

        /*
         * AND THE MONTH'S INVESTMENT INCOME IS SPENT ONCE (2026-09-17).
         *
         * Cleared HERE, after every cell has planned, and not at the top of
         * this method beside `c.dividends = 0`. Two reasons, and the second is
         * the one that would have cost a day:
         *
         * IT HAS TO SURVIVE UNTIL plan(), which runs at the bottom of the loop
         * above. Clearing it at the top would hand every household a zero.
         *
         * AND THE LOAD PATH MUST REPRODUCE THE SAME PLAN. The restrike loop in
         * planFromLoad() re-plans without coming through here, so anything
         * plan() reads has to be a SAVED stock that a reload restores
         * unchanged - which is why investmentIncome is saved and why nothing
         * clears it except this line. A field cleared inside plan() would give
         * one answer live and another on reload, and LongPlaytest compares
         * those two every single month.
         */
        for (Household c : cells) c.investmentIncome = 0;
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
            if (c.shape() == null) {
                fresh[i] = Math.max(0, outsideCensus.applyAsDouble(c));
                // Struck with the count, from the same month's rebuild.
                c.dependants = Math.max(0, outsideDependants.applyAsDouble(c));
            } else {
                fresh[i] = Math.max(0, census.applyAsDouble(c.shape(),
                        c.isRetired() ? retiredSlot : c.tier()));
            }
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
            rowWeight[cells[i].row()] += fresh[i] * cells[i].headcount();
        }
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            per[i] = rowWeight[c.row()] > 0
                    ? rowTotal[c.row()] * c.headcount() / rowWeight[c.row()] : 0;
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
        //
        // ITS POSITION IN THE LIST IS NOW NAMED. It used to be "the last one",
        // read off stocks.size() - 1 inside the loop below, which was true for
        // exactly as long as it was last. The car that follows it would have
        // silently taken the graduates' census and moved the loans on the
        // plain one - nobody's loan wrong by much, everybody's loan wrong.
        final int loanSlot = stocks.size();
        stocks.add(new Stock() {
            public double get(Household c) { return c.studentDebt; }
            public void set(Household c, double v) { c.studentDebt = v; }
        });
        /*
         * AND THE CAR GOES WITH THE PEOPLE (2026-09-16), which is the whole
         * reason it is a household stock rather than a city-wide count. A
         * couple who move in together have two cars between them and then one
         * household; five flatmates who split have a car each. Nobody had to
         * write that down - the pool below does not know what it is moving,
         * which is what the note at the top of this section promised the day
         * the shares were added.
         */
        stocks.add(new Stock() {
            public double get(Household c) { return c.cars; }
            public void set(Household c, double v) { c.cars = v; }
        });

        int n = cells.length;
        double[] before = new double[n];
        for (int i = 0; i < n; i++) before[i] = cells[i].households;
        double[] beforeLoans = carryGraduatesLoans(before);

        double[] left = new double[stocks.size()];
        for (int k = 0; k < stocks.size(); k++) {
            boolean loans = k == loanSlot;
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
        // ...and the cars drove out of the city with their owners. Not written
        // off and not sold: a car that left is simply not here any more, which
        // is why nothing but the road reads this.
        lastCarsTakenAway = left[4 + Equity.COMPANIES.length];

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

        /* =================================================================
           EVERY SHARE HERE IS A FRACTION, AND IS HELD TO BEING ONE (2026-09-16)

           `moved` is a MINIMUM of the two sides, so every ratio below is at
           most one on paper - and `weight * moved / gain` with moved == gain
           can still come back a ULP ABOVE `weight`, which makes `rest`
           a hair NEGATIVE. A negative share hands a cell a negative slice of
           the pool, and from then on the cell is carrying a signed residue
           instead of a position.

           WHY THAT IS NOT HARMLESS, AND HOW IT WAS FOUND. The residue is
           1e-29 of a dollar and nothing reads it as money - but something
           reads its SIGN. investAbroad() asks `c.debt <= 0` to decide whether
           a household may keep money abroad, and a negative dust reads
           debt-free while a positive dust reads indebted. DenominationCheck
           ran a city and its reformed twin exactly together for 158 months
           and then parted: the reform changed the last bit of this
           cancellation, four retired cells flipped, their money abroad came
           home at HOME_SPEED, the financial account moved by thousands, and
           with it the exchange rate - which is every import and export price
           in the city. By the decade the two were 2-4% apart on population,
           GDP, rent, the price level and the bank. NINE FIXTURES IN TEN NEVER
           SAW IT; the sign of a residue is luck and this fixture was unlucky.

           THE CLAMPS ARE EXACT AND FREE OF UNITS. A cell cannot take more
           than its own weight from its own row, and a pool cannot hand out
           more than it holds. No tolerance, no figure in dollars - a bound in
           absolute money is the mistake this project keeps finding, and the
           way not to make it again is not to write a number at all.
           ================================================================= */

        /* ---- within each row first ---- */
        double[] rowMoved = new double[ROWS];
        double cityPool = 0, cityLoss = 0, cityGain = 0;
        for (int r = 0; r < ROWS; r++) {
            double moved = Math.min(rowLoss[r], rowGain[r]);
            rowMoved[r] = moved;
            if (rowLoss[r] > 0) {
                double kept = Math.min(1, moved / rowLoss[r]);
                cityPool += rowPool[r] * (1 - kept);
                rowPool[r] *= kept;
                cityLoss += Math.max(0, rowLoss[r] - moved);
            }
            cityGain += Math.max(0, rowGain[r] - moved);
        }

        /* ---- then across the city ---- */
        double cityMoved = Math.min(cityLoss, cityGain);
        double cityKept = cityLoss > 0 ? Math.min(1, cityMoved / cityLoss) : 0;
        double gone = cityPool * (1 - cityKept);
        cityPool *= cityKept;
        double cityShare = cityGain > 0 ? Math.min(1, cityMoved / cityGain) : 0;

        /* ---- and hand it out ---- */
        for (int i = 0; i < n; i++) {
            Household c = cells[i];
            if (delta[i] <= 0 || c.grownUps() <= 0) continue;
            int r = c.stockGroup();
            double weight = delta[i] * c.grownUps();
            double fromRow = rowGain[r] > 0 ? Math.min(weight, weight * rowMoved[r] / rowGain[r]) : 0;
            double rest = Math.max(0, weight - fromRow);
            double fromCity = Math.min(rest, rest * cityShare);
            double newcomers = Math.max(0, rest - fromCity) / c.grownUps();

            double received = (rowMoved[r] > 0 ? rowPool[r] * fromRow / rowMoved[r] : 0)
                    + (cityMoved > 0 ? cityPool * fromCity / cityMoved : 0);
            double brought = arrival == null ? 0 : newcomers * arrival[i];

            /*
             * AND THE POSITION IS CLAMPED ON THE WAY IN (2026-09-16).
             *
             * Every stock this method moves is a thing a household HOLDS -
             * savings, debt, shares in a company, dollars abroad, a student
             * loan - and not one of them can be less than nothing. The share
             * clamps above stop a cell taking a negative SLICE; this stops a
             * cell ending up with a negative POSITION, which is the same
             * defect one step further on and reachable by a different route:
             * a pool that is itself a hair negative divides out into every
             * cell that draws on it, month after month.
             *
             * Found by the eight-seed ensemble on the day the railway started
             * buying locomotives. Seed 2 carried `debt -4.61e-19` on one cell
             * for THREE THOUSAND MONTHS - 35,199 flagged months against the
             * dozen the known rounding noise produces - because once a cell
             * holds a signed dust it hands it on to everyone it shares a pool
             * with. Nothing about rolling stock was wrong; the trajectory
             * simply put a cell somewhere the old residue could persist.
             *
             * WHY THE SIGN MATTERS WHEN THE MAGNITUDE CANNOT: investAbroad()
             * asks `c.debt <= 0` to decide whether a household may hold money
             * abroad, so a negative dust reads debt-free and a positive one
             * reads indebted. See the header above this loop for the 158-month
             * divergence that cost.
             *
             * Exact, and free of units, for the reason written up there: a
             * bound in absolute money is the mistake this project keeps
             * finding, and the way not to make it again is not to write a
             * number at all.
             */
            double position = (stock.get(c) * before[i] + received + brought) / fresh[i];
            stock.set(c, position > 0 ? position : 0);
        }
        return gone;
    }

    /** Shares of each company that left the city with their holders this month. */
    private final double[] lastSharesTakenAway = new double[Equity.COMPANIES.length];
    public double getSharesTakenAway(int company) { return lastSharesTakenAway[company]; }

    /* =====================================================================
       THE CARS (2026-09-16)

       Jerus, on how ownership should work: "income decides who can afford
       one, good transit makes people not bother" - and, on what it should do
       to the street, "if everyone has cars then road demand is enormous".

       WHAT THE MEASUREMENT SAID BEFORE A LINE OF THIS WAS WRITTEN, because it
       decided the shape. A six-hundred-month city: an UNSKILLED couple takes
       home $4,880 a month, banks $3,160 of it, and is sitting on $799,100 -
       a hundred and sixty-four months of income. Every cell in the city is
       between a hundred and three hundred months. Total household savings
       $17.6bn against zero household debt.

       That is not a rich city, it is a city WITH NOTHING TO BUY. Rent is
       rent, the grocery basket is capped at what a person eats, and every
       dollar past those two has had nowhere to go since the game was written.
       A car is the first durable this city has ever been able to own, and the
       pile is why affordability barely binds in a mature one and binds hard
       in a young one - which is exactly the right way round and took no
       tuning at all.

       SO THE CONSTRAINT IS NOT MONEY, IT IS TIME. Everybody could buy a car
       this month; nobody does, because that is not how a country motorises.
       CAR_ADOPTION spreads it over about three decades of game time, and the
       fleet then holds itself up against CAR_LIFE_MONTHS for ever after. The
       road feels the level, the industry feels the flow, and the two settle
       at different times - which is the interesting part.

       WHAT REPLACEMENT IS NOT THROTTLED BY. A household whose car has died
       buys another at once; only a household that has NEVER owned one waits
       its turn. Put the diffusion rate on both and the fleet plateaus at 78%
       for ever, which is an arithmetic accident of two constants and not a
       fact about anywhere.
       ===================================================================== */

    /** How long a car lasts. Fifteen years, and then it is replaced or it is not. */
    public static final double CAR_LIFE_MONTHS = 180;

    /**
     * What share of the households who have never owned a car buy one in a
     * month, before they are asked whether they can afford it.
     *
     * A DIFFUSION RATE AND NOT A PREFERENCE. Two percent is a half-life of
     * about thirty-five months, so a city that could motorise instantly takes
     * something like three decades to get most of the way - which is roughly
     * what motorisation took everywhere it happened, and is slow enough that a
     * player watches it rather than being handed it.
     */
    public static final double CAR_ADOPTION = .02;

    /**
     * How much of the wanting a fully-served transit system takes away.
     *
     * The other half of Jerus's rule. This is the ONLY place transit touches
     * the decision to BUY; what it does to the decision to DRIVE, on a given
     * morning, with a car already in the drive, is InfrastructureManager's and
     * is a different mechanism.
     *
     * IT IS A CEILING ON OWNERSHIP AND NOT A BRAKE ON ADOPTION, and the
     * difference is the whole of whether a player has a move here. Scaled
     * against the RATE it only delays: a well-served city motorises at half
     * speed and arrives at the same place a few decades later, so transit buys
     * a postponement and nothing else. Against the LEVEL it is a decision - a
     * city whose lines could carry everybody tops out at half a car per
     * household, for ever, because half of its households never bother owning
     * one.
     *
     * AND IT WORKS BACKWARDS TOO, which is the part worth building for. A city
     * that motorises first and builds its metro afterwards does not lose its
     * cars overnight; it stops REPLACING them, and the fleet decays over
     * CAR_LIFE_MONTHS toward what the new ceiling allows. Fifteen years to
     * unmotorise, which is about what it takes.
     */
    public static final double TRANSIT_DETERRENT = .5;

    /* =====================================================================
       AND THEY BORROW FOR IT (2026-09-17)

       Jerus: "make it so that citizens the car requirements are lowered and
       instead they finance with the bank."

       WHAT WAS WRONG WITH THE FIRST VERSION, and it is worse than a high bar.
       A household had to have the WHOLE price in spare savings, and - this is
       the part that reads as a bug rather than a dial - it was refused
       outright if it owed a single dollar to anybody. `c.debt > 0` was in the
       gate, copied from the share offer, where it belongs: a family that
       cannot pay its grocer has no business speculating on equities. A car is
       not a speculation. Nobody on earth buys one that way, and a family that
       is $40 down on a bad month is not thereby barred from driving to work.

       SO A HOUSEHOLD PAYS WHAT IT HAS AND BORROWS THE DIFFERENCE, against the
       SAME credit room the grocery waterfall uses - no new ceiling, no new
       field, no second debt to save. A family that can buy the car outright
       still does, and still owes nothing.

       WHICH IS DELIBERATELY NOT "A FIFTH DOWN AND FOUR FIFTHS BORROWED", and
       that was the first draft, and the ensemble threw it out. These
       households are cash-RICH and income-POOR: a hundred and sixty months of
       take-home in savings against a credit line of six. Making the loan
       mandatory moved the binding constraint off the thing they have and onto
       the thing they have not, and ownership at month 266 fell from 42% to
       11% - the opposite of what was asked for. Pay-what-you-have reduces
       exactly to the old cash rule when the credit line is zero, so it cannot
       refuse a purchase the rule it replaced would have allowed. That is the
       property to preserve if this is ever touched again.

       WHERE IT ACTUALLY BITES is the young city and not the rich one. Eight of
       the playtest's seeds run four thousand months and seven of them come out
       bit-identical, because a mature city's households are never short of the
       cash; the education fixture's city, which is poor and growing, diverges
       in month EIGHT and buys nearly twice the cars by month ten. Lowering a
       requirement can only show up where the requirement was binding.

       WHAT THAT COSTS THE HOUSEHOLD, deliberately: a family carrying a car
       loan is four months closer to its ceiling, so the month it goes short it
       reaches the discharge sooner. Household debt in this game has been
       exactly zero in every measured city; this is the first thing that puts a
       real credit cycle on the residents, and a city where everybody financed
       a car is a city with something to lose. That is the game.
       ===================================================================== */

    /**
     * The least of a car's price a household must find in cash before a lender
     * will put up the rest.
     *
     * A MINIMUM AND NOT AN INSTALMENT. A household hands over every dollar it
     * has spare and borrows what is left, so most buyers put down far more
     * than this; what the fifth does is stop a family with nothing borrowing a
     * car into existence. The deposit is a test of savings and savings are a
     * test of income, so Jerus's rule - "income decides who can afford one" -
     * survives the change intact. It is the level it bites at that moved.
     */
    public static final double CAR_DEPOSIT = .20;

    /**
     * How much of what a household could still borrow a lender will actually
     * advance against a car.
     *
     * NO UNDERWRITER LENDS YOU YOUR LAST DOLLAR, which is the honest reading
     * and is genuinely how car finance works: the line is sized so that the
     * borrower can still absorb a bad month, because a borrower who cannot is
     * a borrower who defaults. Half leaves a financed household three months
     * of its six-month ceiling free.
     *
     * AND IT WAS FOUND BY A HARNESS RATHER THAN REASONED TO, which is worth
     * saying. Without it, every financed household parked itself within a
     * whisker of BANKRUPT_AT_MONTHS - the discharge fires at
     * `debt >= 5.88 x disposable` - and a currency reform sent the two cities
     * to opposite sides of that comparison within a year. DenominationCheck
     * went red on nine assertions: bit-identical the month after the reform,
     * 9% apart on output a year later, which is the signature of a discrete
     * decision taken differently once rather than of an arithmetic that
     * drifts. The comparison itself is sound - both sides are money, so a
     * reform moves them together - and a threshold grazed by every household
     * every month will be crossed differently eventually whatever the
     * arithmetic. The fix is not to sharpen the knife; it is to stop standing
     * on it.
     */
    public static final double CAR_CREDIT_SHARE = .5;

    /* =====================================================================
       THE SECOND-HAND MARKET (2026-09-17)

       Jerus: "if they are doing bad they cut back expenses, sell their cars or
       go for cheaper groceries and so on."

       WHAT A HOUSEHOLD IN TROUBLE DID BEFORE THIS. The waterfall in
       Household.settle() was savings, then the paper abroad, then the shares,
       then the credit line, then going without - and a CAR was in none of it.
       A family could sell every share it owned, draw its last dollar of credit
       and then go hungry with five months of take-home parked in the drive,
       for fifteen years, until the thing wore out. That is not poverty; that is
       an asset the model could not see.

       SO THEY SELL IT, AND SOMEBODY BUYS IT. Jerus's call, and the harder of
       the two: the alternative was selling it abroad, which is one line and a
       lie - a city's used cars are bought by the people who live in it. The
       fleet does not shrink when a household sells; it CHANGES HANDS, from a
       family that cannot keep it to one that could never have afforded a new
       one. Which is the actual social fact about used cars and is worth having
       in a game about who can afford what.

       AND THE PRICE IS STRUCK, NOT SET, which is where the mechanic comes from.
       It is the same rule GoodsMarket.strike() uses - a floor, a ceiling, and a
       position between them off demand against supply - so it falls out for
       free that A CITY CANNOT SELL ITS WAY OUT OF A GENERAL CRASH. When one
       family is short it offers one car into a market full of buyers and gets
       near the ceiling. When the whole city is short every family offers at
       once, nobody is buying, and the price collapses to scrap. Personal bad
       luck is insurable; a downturn is not. Nobody had to write that down.

       WHERE IT SITS IN THE MONTH, and this is the part that needed care.
       Motoring.month() runs AFTER the household ledger, so by the time this clears,
       every cell's month has been struck: afterFixed, savings, subsistence and
       the credit room are all this month's. A household sells when the month
       AHEAD does not add up - when savings and the whole credit line together
       will not buy the food - so the cash is in the bank before the bills it
       is for. That is a household seeing it coming, which is what people do,
       and it means not one line of the settle waterfall had to change.

       THE SELLERS CANNOT ALSO BE THE BUYERS, and that is free too. wantOf()
       has always refused a cell that is going short, and a cell short of bare
       subsistence is going short by definition - planned is the smaller of
       want and what it can spend, so if it cannot afford subsistence it cannot
       afford want either. A household selling its car this month cannot buy a
       new one this month, and nothing had to be added to say so.
       ===================================================================== */

    /**
     * What a scrapper pays, as a share of a new car.
     *
     * THE FLOOR OF THE BAND, and a share rather than an amount because a bound
     * in absolute money is not a bound - it is a bug waiting for a reform. See
     * the twenty-five constants of that family. A car has metal in it and the
     * metal is worth something, so this is where the price goes when there is
     * nobody at all on the other side.
     */
    public static final double USED_CAR_FLOOR = .15;

    /**
     * ...and what one fetches when buyers are queueing, on the same terms.
     *
     * NEVER THE PRICE OF A NEW ONE. Seventy percent is a two- or three-year-old
     * car in a tight market, and the gap between this and 1.0 is what the
     * showroom sells: a warranty, a choice of colour, and nobody else's miles.
     */
    public static final double USED_CAR_CEILING = .70;

    /** What died this month, per cell, waiting to be replaced. Within-month working. */
    private final double[] carsToReplace;

    /** ...and the cars whose owners left the city. */
    private double lastCarsTakenAway;

    public double getCarsTakenAway() { return lastCarsTakenAway; }

    /** Every car in the city. */
    public double totalCars() { return sum(Household::totalCars); }

    /** Cars per household, 0 to 1 - what the road reads. */
    public double carsPerHousehold() {
        double homes = sum(Household::households);
        return homes > 0 ? Math.min(1, totalCars() / homes) : 0;
    }

    /**
     * Fifteen years on, every car in the city is scrapped.
     *
     * Runs whether or not anything can be bought to replace it, because that
     * is what makes ownership something a city can LOSE: a crash that empties
     * the savings does not take the cars away this month, it takes them away
     * over the fifteen years nobody can replace one.
     *
     * @return cars scrapped
     */
    public double wearOutCars() {
        double gone = 0;
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            double worn = c.cars > 0 ? c.cars / CAR_LIFE_MONTHS : 0;
            carsToReplace[i] = worn;
            if (worn <= 0) continue;
            c.cars = Math.max(0, c.cars - worn);
            gone += worn * c.households;
        }
        return gone;
    }

    /**
     * What the households would buy this month at this price, before the
     * market says how many there are.
     *
     * THE SAME FOUR GATES THE SHARE OFFER USES - no debt, not locked out, not
     * going short, and a household that can hold anything at all - and the
     * same cushion, which was Jerus's call the day the shares went in: one
     * rule for idle money, and the rich still buy more because they have more
     * past it.
     *
     * AFFORDABILITY IS A FRACTION RATHER THAN A CLIFF. A cell is an average of
     * thousands of households, so "this cell can afford four tenths of a car
     * each" is the honest reading of "two fifths of these households can
     * afford one" - and it is the reading that makes a poor city motorise
     * slowly instead of all at once on the month its savings cross a line.
     *
     * @param price   what one car costs here
     * @param ceiling cars per household this city will own at all: 1 where
     *                nobody has a tram - see TRANSIT_DETERRENT
     * @return cars, city-wide
     */
    public double carsWanted(double price, double ceiling) {
        if (!(price > 0)) return 0;
        double total = 0;
        for (int i = 0; i < cells.length; i++) total += wantOf(i, price, ceiling);
        return total;
    }

    /* =====================================================================
       WHOLE CARS, AND WHY THE FLOOR IS LOAD-BEARING (2026-09-16)

       The affordability test is a MONEY RATIO - what a household has spare,
       over what a car costs - and it comes out of this method as a PHYSICAL
       QUANTITY that the road, the goods market and the trade balance all then
       read. Nothing else in this file does that. Every other household
       decision multiplies money by a scale-free fraction and stays money.

       A currency reform divides every amount by a hundred, and (a/100)/(b/100)
       is not a/b - it is a/b give or take a unit in the last place. So a
       reformed city wanted an ulp more or less of a car than the plain one,
       bought it from a different place, and the two cities' trade balances
       parted company: DenominationCheck went red on sixteen assertions, the
       first of them the exchange rate four decimals in, one month after the
       reform.

       THIS IS THE MONEY-CONSTANT FAMILY WEARING A NEW COAT. The other
       twenty-three are absolute amounts that a reform walks past a threshold;
       this one is scale-INVARIANT in arithmetic and not in floating point, and
       it bites for the same reason: a quantity crossing from the money world
       into the physical one has to cross at a grain coarser than the dust.

       So a cell buys WHOLE CARS. It is also the truer statement - a cell is
       thousands of households and "four hundred and seven cars" is what
       happens - and the grain is far coarser than any rounding: a cell that
       wants 407.0000000000001 cars and one that wants 406.9999999999999 both
       buy 406, which is the same answer the shops reach by flooring a basket
       count (see Retail.sellOwnPriced) and for exactly the same reason.
       ===================================================================== */

    /** What one cell would buy this month, in whole cars. */
    private double wantOf(int i, double price, double ceiling) {
        Household c = cells[i];
        /*
         * THE DEBT GATE IS GONE and the lockout is not. Owing money is no bar
         * to buying a car - see CAR_DEPOSIT - but having been DISCHARGED is,
         * because a discharged household cannot borrow at all for a year, and
         * creditRoom() below already returns zero for one. Going short still
         * bars it: a family that cannot buy its groceries this month is not
         * signing a finance agreement this month either.
         */
        if (c.households < .5 || !c.canInvest() || c.lockout > 0 || c.isGoingShort()) return 0;
        double spare = (c.savings - SHARE_CUSHION_MONTHS * Math.max(0, c.disposable)) * c.households;
        if (spare <= 0) return 0;
        /*
         * THE ROOM IS MEASURED TO THE CEILING, which is what makes a tram
         * something other than a delay - and it is also what lets a fleet
         * SHRINK. A city that motorised and then built a metro has cars above
         * its new ceiling: room is zero, so the month's scrapped cars are not
         * replaced and the fleet walks down to the ceiling over the life of a
         * car. Nothing special had to be written for that; it falls out of
         * replacement being room-limited like everything else.
         */
        double room = Math.max(0, Math.max(0, Math.min(1, ceiling)) - c.cars) * c.households;
        double want = carsToReplace[i] * c.households + room * CAR_ADOPTION;
        /*
         * CASH PLUS WHAT A LENDER WILL FIND, AND A DEPOSIT TEST ON TOP - and
         * the first draft of this was a fixed fifth down and the rest borrowed,
         * which MADE THINGS WORSE and was caught by the ensemble. This model's
         * households are cash-rich and income-poor: they are sitting on a
         * hundred and sixty months of take-home and their credit line is six.
         * Replacing "have the price in savings" with "have a fifth of it and
         * borrow four fifths against income" moved the binding constraint from
         * the thing they have to the thing they have not, and car ownership at
         * month 266 went from 42% to 11%. Jerus asked for the requirement to be
         * LOWERED.
         *
         * So a household pays what it has and borrows the difference. The two
         * limits are what the money stretches to at all, and the deposit test
         * that stops a family borrowing a car into existence with nothing down.
         * With no credit line the first reduces to spare/price, which is
         * exactly the old rule - so this can never refuse a purchase the cash
         * test alone would have allowed.
         */
        double credit = CAR_DEPOSIT >= 1 ? 0
                : c.creditRoom(c.disposable) * CAR_CREDIT_SHARE * c.households;
        double onMoney = (spare + credit) / price;
        double onDeposit = spare / (price * CAR_DEPOSIT);
        return Math.floor(Math.min(Math.min(want, room), Math.min(onMoney, onDeposit)));
    }

    /**
     * Hands out the cars the market actually had, pro rata over who wanted
     * them, and takes the money out of savings.
     *
     * OUT OF SAVINGS AND NOT OFF THE INCOME STATEMENT, which is the same
     * treatment buyShares() and investAbroad() give: a household turning money
     * into a thing it owns is a portfolio move, not consumption. The month's
     * books do not see it and the balance sheet does - savings down, a car up
     * - which is what happened.
     *
     * @return cash spent
     */
    public double takeCars(double units, double price, double ceiling) {
        if (!(units > 0) || !(price > 0)) return 0;
        lastCarsBought = 0;
        lastCarsFinanced = 0;
        double bought = 0, financed = 0;
        double[] want = new double[cells.length];
        double total = 0;
        for (int i = 0; i < cells.length; i++) {
            want[i] = wantOf(i, price, ceiling);
            total += want[i];
        }
        if (total <= 0) return 0;
        /*
         * ONE IN THE ORDINARY CASE, and that is worth saying out loud. Cars are
         * importable, so Markets.draw() always returns the units it was asked
         * for - the shelf for what the shelf holds and the world for the rest -
         * and the ratio below is exactly 1. It is here for the day something
         * stops that being true, not for today.
         */
        double scale = Math.min(1, units / total);
        double spent = 0;
        for (int i = 0; i < cells.length; i++) {
            if (want[i] <= 0) continue;
            Household c = cells[i];
            double got = want[i] * scale / c.households;
            // A position, and a position cannot be negative or past its
            // ceiling - the clamp every stock in this file carries, and for
            // the reason HouseholdBalance.moveStock's note gives.
            c.cars = Math.max(0, Math.min(1, c.cars + got));
            /*
             * THE SELLER IS PAID IN FULL; THE HOUSEHOLD PAYS WHAT IT HAS AND
             * BORROWS THE DIFFERENCE. A family that can buy the car outright
             * does, and owes nothing - which is why this change cannot make
             * anybody worse off than the cash rule it replaced. The rest is a
             * loan: the bank's cash goes out of the door the same month
             * (Motoring.month tells it so), the debt sits in the same ledger the
             * grocery borrowing does, and the existing waterfall charges the
             * interest and takes the repayments without knowing what the money
             * was for.
             */
            double bill = got * price;
            double inHand = Math.max(0,
                    c.savings - SHARE_CUSHION_MONTHS * Math.max(0, c.disposable));
            double cash = Math.min(bill, inHand);
            double lent = bill - cash;
            c.savings = Math.max(0, c.savings - cash);
            c.debt += lent;
            financed += lent * c.households;
            spent += got * price * c.households;
            bought += got * c.households;
        }
        lastCarsBought = bought;
        lastCarsFinanced = financed;
        return spent;
    }

    /* ---------------------- the second-hand market ---------------------- */

    /**
     * How far ahead a household looks before it decides the car has to go.
     *
     * SIX MONTHS, WHICH IS CREDIT_LIMIT_MONTHS, and the two being the same
     * number is the point rather than a coincidence: a household looks as far
     * ahead as its bank does. A family whose wage no longer covers the food
     * does not sell the car on the first thin month - it eats the savings, and
     * it draws the credit line, exactly as the settle waterfall says. What
     * makes it sell is arithmetic it can do itself: this gap, every month, for
     * half a year, against everything it has and everything it can borrow.
     *
     * AND THE FIRST VERSION OF THIS LOOKED ONE MONTH AHEAD, which sounded
     * strict and honest and was neither. Measured over twelve hundred months of
     * a city played from new: a car-owning cell was SHORT OF THE SHOP in 520 of
     * them and a discharge fired in 513, while the one-month test found only 40
     * - so households went bankrupt with the car still in the drive, thirteen
     * times for every one that sold it. The whole point of the asset is that it
     * is there to be sold BEFORE the crash, and a test that only fires in the
     * month the money actually runs out cannot do that.
     */
    public static final double CAR_SALE_HORIZON_MONTHS = CREDIT_LIMIT_MONTHS;

    /**
     * What one cell would put up for sale this month, in whole cars.
     *
     * THE TEST IS THE MODEL'S OWN WORDS FOR TROUBLE. "Short of the shop" -
     * afterFixed below subsistence - is the same condition discharge() uses,
     * and it means the wage, after the rent and the fees and the interest, no
     * longer buys the food. A household there is living on its savings and its
     * credit line and has a countable number of months before both are gone.
     *
     * JERUS'S ORDERING SURVIVES, AND THIS IS WHERE IT LIVES: savings, then the
     * paper, then the shares, then credit, and the car after all of them. It is
     * in the second line rather than the first - the need is what half a year of
     * the gap comes to LESS everything saved and everything borrowable, so a
     * household that can ride it out on what it has offers nothing at all, and
     * only one that cannot offers anything.
     *
     * AND IT SELLS WHAT IT NEEDS, NOT WHAT IT HAS. The need valued at the FLOOR
     * - what a scrapper would pay - because a family deciding whether to put the
     * car up does not know what it will fetch and will not bet the half-year on
     * the optimistic answer. One car at the floor is many months of food, so a
     * cell a little short offers a FRACTION of its fleet: the share of its
     * households who cannot manage. A city does not dump its cars on one bad
     * month, which it would if this offered the lot.
     *
     * A PURE NUMBER, TWICE OVER: money over a price in money, and a share of a
     * fleet. Neither moves when the currency does.
     */
    private double offerOf(int i, double floorPrice) {
        Household c = cells[i];
        if (c.households < .5 || !(c.cars > 0) || !(floorPrice > 0)) return 0;
        double gap = c.subsistence - Math.max(0, c.afterFixed);
        if (gap <= 0) return 0;
        double need = CAR_SALE_HORIZON_MONTHS * gap
                - Math.max(0, c.savings) - c.creditRoom(c.disposable);
        if (need <= 0) return 0;
        // Whole cars, for the reason the note above wantOf() gives: a quantity
        // crossing from the money world into the physical one crosses at a
        // grain coarser than the dust.
        return Math.floor(Math.min(c.cars, need / floorPrice) * c.households);
    }

    /* =====================================================================
       THE LUXURY COUNTER (2026-09-17)

       The same three-step every household purchase in this file uses - ask
       what they want at a price, hand out what the seller had, take the money
       - and the third one written, after the shares and the cars. See
       LuxuryRetail for why the good exists at all.
       ===================================================================== */

    /**
     * What the city's households would buy at this price, in whole pieces.
     *
     * THE MONEY IS ADDED UP FIRST AND THE FLOOR HAPPENS ONCE, which is the
     * twenty-sixth sighting of the money-constant family and the subtlest one
     * yet. The first draft floored each CELL's budget over the price - sixty-
     * eight crossings from the money world into the physical one every month -
     * and DenominationCheck came apart 6e-05 at a time. The arithmetic is
     * scale-invariant and the floating point is not: (a/100)/(b/100) differs
     * from a/b in the last bit, and a floor turns a last-bit difference into a
     * whole piece whenever the quotient lands near an integer. Sixty-eight
     * chances a month for four thousand months is not a small number.
     *
     * One crossing for the whole city is the same construction
     * Retail.sellOwnPriced() uses on the grocery basket, and for the same
     * reason. The cells still get their share: takeLuxuries() splits what was
     * bought pro rata over the BUDGETS, which are money and stay money.
     */
    public double luxuriesWanted(double price) {
        if (!(price > 0)) return 0;
        return Math.floor(luxuryBudget() / price);
    }

    /** What the city's households would put over a counter, in money. */
    private double luxuryBudget() {
        double total = 0;
        for (Household c : cells) total += luxuryBudgetOf(c);
        return total;
    }

    /**
     * What one cell would buy, in whole pieces.
     *
     * THE GATES ARE THE CAR'S, LESS THE CAR'S CEILING. Not locked out, not
     * going short, able to hold anything at all, and money past the cushion -
     * a household that cannot buy its groceries this month is not buying a
     * watch this month either. What it does NOT have is a room-left term: a
     * fleet has a ceiling of one car per household and a collection has none,
     * which is the whole reason this good is here.
     */
    private double luxuryBudgetOf(Household c) {
        if (c.households < .5 || !c.canInvest() || c.lockout > 0 || c.isGoingShort()) return 0;
        if (!(c.luxuryWant > 0)) return 0;
        double spare = (c.savings - SHARE_CUSHION_MONTHS * Math.max(0, c.disposable)) * c.households;
        if (spare <= 0) return 0;
        return Math.min(c.luxuryWant * c.households, spare);
    }

    /**
     * Hands out the pieces the shops actually had, pro rata over who wanted
     * them, and takes the money out of savings.
     *
     * OUT OF SAVINGS AND NOT OFF THE INCOME STATEMENT, the same treatment the
     * shares and the cars get - except that unlike a car, a watch is not an
     * asset this model tracks. It is bought, the money is gone, and what the
     * household has instead is not written down anywhere. That is a deliberate
     * simplification and it is the honest one for a consumption good: the
     * point of buying it was to have spent the money.
     *
     * @return cash spent
     */
    public double takeLuxuries(double units, double price) {
        lastLuxuriesBought = 0;
        lastLuxurySpend = 0;
        if (!(units > 0) || !(price > 0)) return 0;

        double[] budget = new double[cells.length];
        double total = 0;
        for (int i = 0; i < cells.length; i++) {
            budget[i] = luxuryBudgetOf(cells[i]);
            total += budget[i];
        }
        if (total <= 0) return 0;

        // Pro rata over the BUDGETS. What each cell gets is its share of the
        // pieces the shops had, which is money over money and needs no floor
        // of its own - see luxuriesWanted().
        double spent = 0, bought = 0;
        for (int i = 0; i < cells.length; i++) {
            if (budget[i] <= 0) continue;
            Household c = cells[i];
            double got = units * (budget[i] / total) / c.households;
            double bill = got * price;
            c.savings = Math.max(0, c.savings - bill);
            spent += bill * c.households;
            bought += got * c.households;
        }
        lastLuxuriesBought = bought;
        lastLuxurySpend = spent;
        return spent;
    }

    /* =====================================================================
       THE TABLE (2026-09-18)

       The luxury counter's three-step, one sector later: ask what the city
       wants at a price, hand out what the kitchens could serve, take the
       money. What is different is the third line of it - see the hunger
       measure in advanceMonth(), where a meal counts as food.
       ===================================================================== */

    /**
     * What the city's households would eat out at this price, in whole meals.
     *
     * ONE FLOOR FOR THE CITY, for the reason written in full above
     * luxuriesWanted(): a floor on a ratio of two money quantities is the
     * money-constant family's quietest coat, and doing it per cell gives a
     * reform sixty-eight chances a month to turn a last-bit difference into a
     * whole meal.
     */
    public double mealsWanted(double price) {
        if (!(price > 0)) return 0;
        /*
         * MONEY FIRST, THEN APPETITE, AND ONE FLOOR OVER BOTH. What a city can
         * pay for divided by what a meal costs, capped at a third of the meals
         * its people eat at all - see MOST_MEALS_EATEN_OUT. The cap is struck
         * for the CITY rather than per cell, like the floor and for the same
         * reason: sixty-eight crossings from money into meals a month is
         * sixty-eight chances for a reform to move one of them.
         */
        double appetite = 0;
        for (Household c : cells) {
            appetite += c.people() * ham.citybuildersim.sectors.Restaurants.MEALS_A_PERSON_MONTH;
        }
        return Math.floor(Math.min(mealBudget() / price, appetite * MOST_MEALS_EATEN_OUT));
    }

    /** What the city's households would put on a table, in money. */
    private double mealBudget() {
        double total = 0;
        for (Household c : cells) total += mealBudgetOf(c);
        return total;
    }

    /**
     * What one cell would spend eating out.
     *
     * THE SAME GATES AS THE COUNTER, and the awkward one is deliberate: a
     * household that is `isGoingShort()` - that could not fund its own plan
     * this month - does not go out to dinner. It is tempting to argue the
     * opposite, that a household which could not get groceries is exactly the
     * one that would eat out, and in a city at the supply wall that is a real
     * story. It is not this one: `isGoingShort()` is about MONEY, not about
     * shelves, and a household short of money is short of it at a restaurant
     * too. The shelf story belongs in what the shops could deliver, and that
     * is measured elsewhere.
     */
    private double mealBudgetOf(Household c) {
        if (c.households < .5 || !c.canInvest() || c.lockout > 0 || c.isGoingShort()) return 0;
        if (!(c.mealWant > 0)) return 0;
        double spare = (c.savings - SHARE_CUSHION_MONTHS * Math.max(0, c.disposable)) * c.households;
        if (spare <= 0) return 0;
        return Math.min(c.mealWant * c.households, spare);
    }

    /**
     * Hands out the meals the kitchens actually served, pro rata over who
     * wanted them, takes the money out of savings and REMEMBERS THE MEALS.
     *
     * The remembering is what makes this different from the counter. A watch
     * is bought and gone; a dinner was eaten, and next month's hunger measure
     * has to know it - see advanceMonth(), and Household.mealsEaten for why it
     * is a month late and why that is the honest side of the boundary.
     *
     * @return cash spent
     */
    public double takeMeals(double meals, double price) {
        lastMealsBought = 0;
        lastMealSpend = 0;
        if (!(meals > 0) || !(price > 0)) return 0;

        double[] budget = new double[cells.length];
        double total = 0;
        for (int i = 0; i < cells.length; i++) {
            budget[i] = mealBudgetOf(cells[i]);
            total += budget[i];
        }
        if (total <= 0) return 0;

        double spent = 0, eaten = 0;
        for (int i = 0; i < cells.length; i++) {
            if (budget[i] <= 0) continue;
            Household c = cells[i];
            double got = meals * (budget[i] / total) / c.households;
            double bill = got * price;
            c.savings = Math.max(0, c.savings - bill);
            c.mealsEaten += got;
            spent += bill * c.households;
            eaten += got * c.households;
        }
        lastMealsBought = eaten;
        lastMealSpend = spent;
        return spent;
    }

    private double lastMealsBought, lastMealSpend, lastMealsEaten;

    /** Meals the city ate out in the month the hunger measure just read. */
    public double getMealsEaten() { return lastMealsEaten; }

    /** Meals the households ate out this month. */
    public double getMealsBought() { return lastMealsBought; }

    /** ...and what they paid for them. */
    public double getMealSpend() { return lastMealSpend; }

    private double lastLuxuriesBought, lastLuxurySpend;

    /** Pieces the households took this month. */
    public double getLuxuriesBought() { return lastLuxuriesBought; }

    /** ...and what they paid for them. */
    public double getLuxurySpend()    { return lastLuxurySpend; }

    /** What every household in the city would like to put over a counter this month. */
    public double getLuxuryWant() { return sum(c -> c.luxuryWant); }

    /** What the city's households would put up for sale at this floor. */
    public double carsOffered(double floorPrice) {
        double total = 0;
        for (int i = 0; i < cells.length; i++) total += offerOf(i, floorPrice);
        return total;
    }

    /**
     * Clears the month's second-hand market: strikes a price, moves the cars
     * that find a buyer, and pays the households that sold them.
     *
     * THE TWO-PASS SHAPE IS Motoring.month()'s OWN, and deliberately so. Demand
     * depends on the price and the price depends on demand, which is a circle
     * every market in this game has had to break: GoodsMarket strikes off an
     * intended demand and then sells at the struck price, and the new-car path
     * three lines below quotes, re-asks at the quote, and then draws. This does
     * the same - probe the middle of the band, strike, then ask again at what
     * was struck - so a reader who understands one understands all three.
     *
     * THE MONEY IS A TRANSFER AND THE AUDIT SHOULD SEE NOTHING. Buyers' savings
     * fall and sellers' rise by the same figure, both inside the household
     * pool; the only cash that crosses a boundary is what a buyer BORROWED,
     * which leaves the bank and is declared the way a new car's finance already
     * is. See Motoring.month().
     *
     * @param newPrice what a new car costs this month
     * @param ceiling  cars per household this city will own at all
     * @return cars that changed hands
     */
    public double clearUsedCars(double newPrice, double ceiling) {
        lastUsedOffered = 0;
        lastUsedTraded = 0;
        lastUsedSpend = 0;
        lastUsedFinanced = 0;
        lastUsedPrice = 0;
        lastUsedNewPrice = 0;
        // Only the sellers are written to below, so the rest have to be told
        // that this month they sold nothing.
        for (Household c : cells) c.carsSold = 0;
        if (!(newPrice > 0)) return 0;

        lastUsedNewPrice = newPrice;
        double lo = newPrice * USED_CAR_FLOOR, hi = newPrice * USED_CAR_CEILING;

        double[] offer = new double[cells.length];
        double supply = 0;
        for (int i = 0; i < cells.length; i++) {
            offer[i] = offerOf(i, lo);
            supply += offer[i];
        }
        lastUsedOffered = supply;
        if (supply <= 0) return 0;

        /*
         * THE POSITION, ON GoodsMarket.strike()'s OWN TERMS, special cases and
         * all. There is always supply here - the guard above saw to it - so the
         * no-makers branch cannot fire, and the one that matters is the other:
         * a city where everybody is selling and nobody is buying puts the
         * position at zero and the price at the floor. That is the crash.
         */
        double demand = carsWanted((lo + hi) / 2, ceiling);
        double position = demand <= 0 ? 0 : demand / (demand + supply);
        double price = lo + (hi - lo) * position;
        lastUsedPrice = price;

        double taken = Math.min(supply, carsWanted(price, ceiling));
        if (taken <= 0) return 0;

        /*
         * THE BUYERS FIRST, through the same takeCars() a new car goes through:
         * the same whole-car floor, the same cushion, the same deposit test,
         * and the same lender finding the difference. A used car is a car.
         */
        double spent = takeCars(taken, price, ceiling);
        double traded = lastCarsBought;
        if (!(traded > 0)) return 0;

        /*
         * ...AND THE SELLERS ARE PAID, pro rata over what each offered, into
         * savings - which is where a sale of anything a household owns lands.
         * It is the same treatment the shares get: turning a thing you own back
         * into money is a balance-sheet move and the month's income statement
         * does not see it.
         */
        double scale = traded / supply;
        for (int i = 0; i < cells.length; i++) {
            if (offer[i] <= 0) continue;
            Household c = cells[i];
            double sold = offer[i] * scale / c.households;
            c.cars = Math.max(0, c.cars - sold);
            c.savings += sold * price;
            c.carsSold = sold * c.households;
        }

        lastUsedTraded = traded;
        lastUsedSpend = spent;
        lastUsedFinanced = lastCarsFinanced;
        return traded;
    }

    private double lastUsedOffered, lastUsedTraded, lastUsedSpend,
            lastUsedFinanced, lastUsedPrice, lastUsedNewPrice;

    /** Cars put up for sale this month, whether or not anybody took them. */
    public double getUsedCarsOffered() { return lastUsedOffered; }

    /** ...and the ones that found a buyer. */
    public double getUsedCarsTraded() { return lastUsedTraded; }

    /** What they went for. Zero in a month with no market at all. */
    public double getUsedCarPrice() { return lastUsedPrice; }

    /**
     * ...as a share of what a NEW one cost the month it was struck, which is
     * the only honest denominator and is between USED_CAR_FLOOR and
     * USED_CAR_CEILING by construction.
     *
     * A READER WHO DIVIDES BY TODAY'S SHOWROOM PRICE GETS A LIE, and it was
     * printed once: the playtest read the CARS market AFTER motoring had
     * finished with it, the new price had moved underneath, and the summary
     * announced a used car going for 74% of a new one against a ceiling of 70%.
     * The band was never breached; the division was.
     */
    public double getUsedCarShare() {
        return lastUsedNewPrice > 0 ? lastUsedPrice / lastUsedNewPrice : 0;
    }

    /** What the buyers paid, all in. */
    public double getUsedCarSpend() { return lastUsedSpend; }

    /**
     * ...and what of that a lender advanced, which the bank has to be told
     * about in the same month. See Motoring.month().
     */
    public double getUsedCarsFinanced() { return lastUsedFinanced; }

    private double lastCarsBought;
    private double lastCarsFinanced;

    /** Cars the households actually took this month. See takeCars(). */
    public double getCarsBought() { return lastCarsBought; }

    /**
     * ...and what of that a lender advanced, which the bank has to be told
     * about in the same month or the money audit sees a pool fall for no
     * reason. See Motoring.month().
     */
    public double getCarsFinanced() { return lastCarsFinanced; }


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
            // The same clamp as its sibling in Exchange, and for the same
            // reason: a holding is a position and cannot be negative.
            c.shares[company] = Math.max(0, c.shares[company] - sell);
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

    /* =====================================================================
       THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-16)

       A household move smaller than a trillionth of a dollar is the
       arithmetic's own dust, and moving it writes a line into the balance of
       payments for nothing. So it is swept to zero - and it was swept at a
       FIXED 1e-12, which makes it a money constant that a currency reform
       never reseeded. That is the FOURTH of these the project has found, after
       Equity.FOUNDING_PRICE, Exchange.MIN_FAIR and OutwardInvestment.MIN_MOVE,
       whose note describes this exact symptom - and this is the sector rule's
       own twin, on the households, written the day the households were given
       the same door and given a bare literal instead of the seeded field
       sitting twenty lines away in OutwardInvestment.

       WHAT IT WOULD COST, and an honest note about how it was found. After a
       hundred-to-one reform every amount is a hundred times smaller, so a
       household move the plain city makes is swept away in the reformed one:
       that cell's savings and its dollars abroad differ from then on. THIS IS
       NOT WHAT BROKE DenominationCheck ON 2026-09-16 - it was found while
       chasing that, seeded, and the divergence did not move by a digit. The
       cause was the cancellation dust in moveStock() above. It is fixed here
       anyway because it is unambiguously the same bug as the seeded field
       twenty lines away in OutwardInvestment, and leaving a known-wrong
       constant in because today's harness cannot see it is how the other four
       survived as long as they did.

       THE DOLLAR FLOOR BELOW IS NOT THIS BUG. `abroad` is in dollars and a
       reform does not move dollars - see Household.redenominate(), whose note
       says so - so 1e-15 there is scale-invariant and stays a literal.
       ===================================================================== */
    public static final double MIN_MOVE = 1e-12;

    /** The same floor in today's money. See MIN_MOVE. */
    private double minMove = MIN_MOVE;

    /** Re-seeds the floor at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        minMove = MIN_MOVE / (unit > 0 ? unit : 1);
    }

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

            /* =============================================================
               THE COUPON IS PAID HOME, NOT ROLLED (2026-09-17)

               Jerus, on being shown where the money went: "pay it home as
               income."

               WHAT ROLLING IT COST, measured over one four-thousand-month run.
               Cumulative foreign coupons came to $2,649bn against $584bn of
               every wage the city ever paid - four and a half times the entire
               wage bill, twenty times the dividends, and seven times everything
               ever spent in the shops. It overtook wages around month 2,400 and
               ran away afterwards. Household net worth, which sat at a
               textbook-perfect 45 months of GDP at month 480, was at 5,072 by
               month 3,840 and still climbing. Past about two centuries the city
               stopped being an economy and became a rentier fund with a town
               attached.

               THE ENGINE WAS THIS LINE. Households hold about 79% of their
               wealth abroad at the world's 2%, and the coupon was added to the
               pile where it was earned. That is wealth compounding at 1.58% a
               year MECHANICALLY - independent of the city, of the player, of
               whether anybody works. Over 333 years, 193 times, which is
               exactly the curve that was measured.

               AND PAYING IT HOME DOES NOT ON ITS OWN STOP THE COMPOUNDING, so
               this is honest about what it is: the coupon lands in savings,
               savings past the cushion go abroad again at OUT_SPEED, and the
               same loop turns a little slower. What it BUYS is that the money
               is now somewhere a household can spend it, somewhere the
               statement shows it, and somewhere the plan can see it. It is the
               change the other two need to exist. A coupon is income, not a
               bigger bond.
               ============================================================= */
            double earnedUsd = c.abroad * Math.max(0, worldRate) / 12;
            c.foreignInterest = earnedUsd * rate;
            c.savings += c.foreignInterest;
            c.investmentIncome += c.foreignInterest;

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
            if (Math.abs(move) < minMove) continue;
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
            plannedSpend += c.plan(localPerUsd) * c.households;
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
            // ...and the household knows it has it, next time it plans a month.
            // See Household.investmentIncome.
            c.investmentIncome += each;
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

    /** ...and before the cars were (2026-09-16). */
    public static final int CELL_SLOTS_BEFORE_CARS = CELL_SLOTS_BEFORE_STUDENT_DEBT + 1;

    /** ...and before the month's investment income was (2026-09-17). */
    public static final int CELL_SLOTS_BEFORE_INVESTMENT_INCOME = CELL_SLOTS_BEFORE_CARS + 1;

    /** Figures carried per cell, in the order toCellSaveArray() writes them: the eight, a share count per company, the dollars abroad, the student loan, the cars, the month's investment income. */
    /**
     * ...and the dinners, appended 2026-09-18.
     *
     * A save from before the kitchens existed has households that ate out
     * nothing last month, which is exactly true of that city. The reader
     * checks the width, so SAVE_FORMAT does not move - the same tail-append
     * the cars and the investment income got.
     */
    public static final int CELL_SLOTS_BEFORE_MEALS = CELL_SLOTS_BEFORE_INVESTMENT_INCOME + 1;

    public static final int CELL_SLOTS = CELL_SLOTS_BEFORE_MEALS + 1;

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
            out[i++] = c.cars;
            out[i++] = c.investmentIncome;
            out[i++] = c.mealsEaten;
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
    public boolean restoreCells(String[] keys, double[] saved, String[] savedCompanies) {
        if (keys == null || saved == null || keys.length == 0) return false;
        /* =====================================================================
           THE SLOT COUNT ALONE DOES NOT SAY WHAT THE SLOTS ARE (2026-09-12)

           Eight a cell from the morning the cells went in, eight plus a share
           per company from the evening, those plus the dollars abroad from the
           next day, and those plus the student debt. Any of them restores what
           it carries; a save without shares has households that own none.

           But the share block is as wide as Equity.COMPANIES, which is the
           SECTORS PLUS THE BANK - so adding a sector moves every width after
           it. Eighteen slots meant "the full array" while there were seven
           sectors and means "the array before student debt" now there are
           eight, and the old reading of a format-21 save would have taken its
           eight holdings plus its dollars abroad as nine holdings, and its
           student debt as its dollars abroad. Silent corruption of every
           household's portfolio, with no refusal - and bumping SAVE_FORMAT
           would not have caught it, because older saves always load.

           So the widths are computed from the company list THE SAVE WAS WRITTEN
           WITH, which the save already carries (DataSave.getEquityKeys), and
           the holdings are mapped BY NAME rather than by position. A save from
           before the eighth sector restores its eight holdings correctly and
           owns none of the ninth company, which is exactly true of that city.
           ===================================================================== */
        int savedShares = savedCompanies == null ? Equity.COMPANIES.length : savedCompanies.length;
        final int wasBeforeShares  = CELL_SLOTS_BEFORE_SHARES;
        final int wasBeforeAbroad  = wasBeforeShares + savedShares;
        final int wasBeforeStudent = wasBeforeAbroad + 1;
        final int wasBeforeCars    = wasBeforeStudent + 1;
        final int wasBeforeIncome  = wasBeforeCars + 1;
        final int wasBeforeMeals   = wasBeforeIncome + 1;
        final int wasFull          = wasBeforeMeals + 1;

        int slots = (saved.length - 3) / keys.length;
        if (saved.length != keys.length * slots + 3
                || (slots != wasFull && slots != wasBeforeMeals
                    && slots != wasBeforeIncome && slots != wasBeforeCars
                    && slots != wasBeforeStudent && slots != wasBeforeAbroad
                    && slots != wasBeforeShares)) {
            return false;
        }

        // Where each saved holding belongs in today's register, by name. -1 is
        // a company this build no longer lists: that holding is dropped, the
        // same way a cell key this build has no cell for is skipped.
        int[] shareSlot = new int[savedShares];
        for (int k = 0; k < savedShares; k++) {
            shareSlot[k] = -1;
            String name = savedCompanies == null || k >= savedCompanies.length
                    ? (k < Equity.COMPANIES.length ? Equity.COMPANIES[k] : null)
                    : savedCompanies[k];
            if (name == null) continue;
            for (int c = 0; c < Equity.COMPANIES.length; c++) {
                if (Equity.COMPANIES[c].equals(name)) { shareSlot[k] = c; break; }
            }
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
            /*
             * A CITY FROM BEFORE CARS EXISTED OWNS NONE, and that zero is the
             * whole of why this batch could be measured at all: the road
             * multiplier and the transit rule below are both exactly 1 at zero
             * ownership, so an old save reloads into a city that behaves to
             * the bit as it did the day it was written, and then starts buying.
             */
            c.cars = 0;
            if (slots >= wasBeforeAbroad) {
                for (int k = 0; k < savedShares; k++) {
                    double held = saved[i++];
                    if (shareSlot[k] >= 0) c.shares[shareSlot[k]] = held;
                }
            }
            if (slots >= wasBeforeStudent) c.abroad = Math.max(0, saved[i++]);
            if (slots >= wasBeforeCars) c.studentDebt = Math.max(0, saved[i++]);
            if (slots >= wasBeforeIncome) c.cars = Math.max(0, saved[i++]);
            // A save from before this field has households that received no
            // investment income last month, which is exactly true of that city
            // - nothing in it was ever going to spend any.
            if (slots >= wasBeforeMeals) c.investmentIncome = Math.max(0, saved[i++]);
            // ...and a city from before the kitchens ate out nothing.
            c.mealsEaten = 0;
            if (slots >= wasFull) c.mealsEaten = Math.max(0, saved[i++]);
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
            // Clamped on the way back in, for the reason every other write to
             // a position is: a saved dust is still a dust. See moveStock().
            c.savings = Math.max(0, saved[r]);
            c.debt = Math.max(0, saved[rows + r]);
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
        minMove *= scale;
        lastWrittenOff *= scale;
        lastTakenAway *= scale;
        lastStudentDebtTakenAway *= scale;
        localPerUsd *= scale;
        plannedSpend *= scale;
        lastDepositInterest *= scale;
        forEach(c -> c.redenominate(scale));
    }

}
