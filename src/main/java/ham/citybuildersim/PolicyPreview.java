package ham.citybuildersim;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * What a staged set of the Policy tab's dials would do, by the model's own
 * arithmetic (0.7.36): the tax take under another policy, line by line, and
 * THE BUDGET before and after it - advice, not a model change.
 *
 * WHY. Every dial on the Policy tab is a proposal until it is applied, and
 * the tab shows what it would do first ("a before/after preview once you've
 * moved the dial", Jerus's call in 0.6.x). Nine of those "afters" were the
 * screen's own arithmetic: the tax total summed in the screen with a clamp
 * copied from TaxPolicy (and missing the bank's tax), the pension and the
 * premiums scaled by the ratio of two rates (so a dial at zero previewed
 * nothing), the EI bill scaled from the one the treasury paid rather than
 * restruck on the pool, the savers' rate a share of the dial beside the rate
 * the bank chose. CLAUDE.md's rule is that a screen recomputes nothing, and a
 * rule changed in the model would not have changed on the tab. The Policy
 * spec (runs/spec-policy-0735.md, its section 6.2 and D2) moved every one of
 * them here and onto the classes that own the arithmetic.
 *
 * HOW. The staged set is put through a detached copy of the city's policy
 * (TaxPolicy.copy()), so its own setters clamp it; each line is then the
 * owner's read at the copy - EconomyManager's tax take and payroll lines,
 * Bank.taxAt() through Game.bankTaxUnder(), Unemployment.benefitsAt(),
 * Healthcare's fees at one, Education.billedAt(), the grant's bill and the
 * loan's interest - and the same read at the live policy is the line's
 * "before". THE BUDGET is last month's balance (NationalAccounts.getBalance())
 * plus the sum of every line's after less its before: struck the same way
 * twice, so the difference is the answer even where a line's "before" is not
 * last month's booked figure to the dollar (the bank's tax is NEXT month's,
 * charged in arrears; the sales tax is scaled, D11). At the live policy each
 * sector's and band's tax line is the month's booked figure (property's on
 * the roll as it stands): PolicyPreviewCheck holds it.
 *
 * AND ONE DIAL OFF THE POLICY TAB (0.7.48, C2): the fund's withdrawal, on
 * Finances > The city's fund > Rules & cash - what it would pay next month,
 * from its cash and from its market book, and the fund a year on
 * (fundWithdrawalAt()), struck as TreasuryFund.payTransfer() strikes it.
 *
 * AND ZONING (0.7.107, batch Z1): a zone's held ground, what it keeps from
 * everyone, the price it gives up and its tax relief (zoneAt()), and the
 * forest law's three kinds and its wood (forestAt()), each before and after
 * on a staged Zoning.copy().
 *
 * AND THE RULE'S STRICTNESS (0.7.52): at any step of the dial beside the
 * inflation target, what the bank would aim at, what the rule sets on
 * target and what it would advise at this year's inflation (ruleAt()),
 * struck by DebtManager.ruleRate() at that step; CentralBankCheck 21
 * holds it, the preview at the step in force being the rule's own.
 *
 * WHAT NONE OF IT KNOWS is who changes what they do. A business taxed
 * harder earns less, a dearer clinic turns people away, a cheaper school
 * fills; that arrives in the month's own books, and every card on the tab
 * says so behind its (i). Pure: it reads the city and changes nothing.
 */
public final class PolicyPreview {

    private PolicyPreview() { }

    /* =====================================================================
       THE TAX TAKE UNDER A POLICY (M2)
       ===================================================================== */

    /**
     * The tax a month every line would raise under one policy, struck on this
     * month's books: profit, sales and property by sector key, wage by band,
     * and the bank's profit tax at the policy's retail rate (next month's
     * bill - Bank.taxAt()).
     */
    public record TaxTake(Map<String, Double> profit, Map<String, Double> sales,
                          Map<String, Double> property, Map<WageBand, Double> wage, double bank) {

        /** One sector's line of a kind, 0 for a sector the map does not hold. */
        public double profit(String key)   { return profit.getOrDefault(key, 0.0); }
        public double sales(String key)    { return sales.getOrDefault(key, 0.0); }
        public double property(String key) { return property.getOrDefault(key, 0.0); }
        public double wage(WageBand band)  { return wage.getOrDefault(band, 0.0); }

        /** Every sector's profit tax and the bank's: the Profit page's head, which NationalAccounts' business line includes the bank in (the spec's B5). */
        public double profitTotal() { return sum(profit) + bank; }
        public double salesTotal()  { return sum(sales); }
        public double propertyTotal() { return sum(property); }
        public double wageTotal() {
            double t = 0;
            for (double v : wage.values()) t += v;
            return t;
        }

        /** The four taxes together. */
        public double total() { return profitTotal() + salesTotal() + wageTotal() + propertyTotal(); }

        private static double sum(Map<String, Double> m) {
            double t = 0;
            for (double v : m.values()) t += v;
            return t;
        }
    }

    /** The tax take under `p`: every sector's three lines, every band's wage tax and the bank's, each its owner's read. */
    public static TaxTake taxTake(Game g, TaxPolicy p) {
        EconomyManager em = g.getEconomyManager();
        Map<String, Double> profit = new LinkedHashMap<>(), sales = new LinkedHashMap<>(),
                property = new LinkedHashMap<>();
        for (Sector s : g.getSectors().all()) {
            profit.put(s.key(), em.profitTaxUnder(s, p));
            sales.put(s.key(), em.salesTaxUnder(s, p));
            property.put(s.key(), em.propertyTaxUnder(s, p));
        }
        Map<WageBand, Double> wage = new EnumMap<>(WageBand.class);
        for (WageBand band : WageBand.values()) wage.put(band, em.wageTaxUnder(band, p));
        return new TaxTake(profit, sales, property, wage, g.bankTaxUnder(p));
    }

    /* =====================================================================
       THE BUDGET (M8)
       ===================================================================== */

    /** THE BUDGET a month, before and after: last month's balance, and the same plus what the staged set would change. */
    public record Budget(double before, double after) {
        public double change() { return after - before; }
    }

    /**
     * What a staged set would change in the budget a month: every line it
     * reaches, its read under `after` less the same read under `live`, revenue
     * up and spending down. The lines: the four taxes and the bank's (M2);
     * the pension contribution and the EI and health premiums off the wage
     * bill (M3); the pensions (M4); the EI bill on the pool (M5); the clinics'
     * treatment fees at their scale, at full service on both sides (the spec's
     * B13: the fees net of the priced-out against fees at full was a move
     * nobody made); the tuition households pay at the scales and the city's
     * share (`shareNow`, `shareThen`: Education's dial, not the policy's - the
     * share the city waives is forgone revenue, not spending: D13); the
     * students' grant; the interest on the graduates' loans; (0.7.45, the
     * UI spec's B19) the food vouchers at the last sale, foodAssistanceAt();
     * and (0.7.103, Jerus's A7) the fuel duty on the month's litres at the
     * pump, fuelDutyAt().
     */
    public static double change(Game g, TaxPolicy live, TaxPolicy after, double shareNow, double shareThen) {
        EconomyManager em = g.getEconomyManager();
        double moved = taxTake(g, after).total() - taxTake(g, live).total();

        moved += em.contributionsAt(after.getContributionRate()) - em.contributionsAt(live.getContributionRate());
        moved += em.premiumAt(after.getEiPremiumRate()) - em.premiumAt(live.getEiPremiumRate());
        moved += em.premiumAt(after.getHealthPremiumRate()) - em.premiumAt(live.getHealthPremiumRate());
        moved -= em.pensionsPaidUnder(after) - em.pensionsPaidUnder(live);

        Unemployment u = g.getUnemployment();
        moved -= u.benefitsAt(after.getEiBenefitRate()) - u.benefitsAt(live.getEiBenefitRate());

        Healthcare care = g.getHealthcare();
        moved += care.treatmentFeesAtOne() * (after.getHealthFeeScale() - live.getHealthFeeScale());

        moved += tuitionPaid(g, after, shareThen) - tuitionPaid(g, live, shareNow);

        moved -= g.studentGrantBillUnder(after.getGrantBasis(), after.getGrantAmount())
                - g.studentGrantBillUnder(live.getGrantBasis(), live.getGrantAmount());

        HouseholdBalance hb = g.getHouseholdBalance();
        moved += hb.studentInterestAt(after.getStudentLoanRate()) - hb.studentInterestAt(live.getStudentLoanRate());

        moved -= foodAssistanceAt(g, after.getFoodAssistance()) - foodAssistanceAt(g, live.getFoodAssistance());
        moved += fuelDutyAt(g, after) - fuelDutyAt(g, live);
        return moved;
    }

    /* =====================================================================
       THE FUEL DUTY (0.7.103, batch P4; Jerus's A7)
       ===================================================================== */

    /** The litres the drivers bought at the pump last month, at the stations and past them: what a duty is struck on (sectors.Retail). */
    public static double fuelLitres(Game g) {
        ham.citybuildersim.sectors.Retail r = g.getSectors().retail();
        return Math.max(0, r.getPumpLitres()) + Math.max(0, r.getQueueLitres());
    }

    /**
     * What the fuel duty would raise a month under a policy: the month's
     * litres at the pump at its duty a litre as charged (TaxPolicy
     * .chargedFuelDuty()) - at the city's own dial what the month took, to
     * the bit (EconomyManager.getFuelDuty()). The litres held: dearer fuel
     * moves the owners toward the bus a month on (Motoring.journeyFuel()),
     * which this does not know (the tab's CAVEAT).
     */
    public static double fuelDutyAt(Game g, TaxPolicy p) {
        double duty = p.chargedFuelDuty();
        return duty > 0 ? fuelLitres(g) * duty : 0;
    }

    /** ...and a litre at the pump under it: the sign's price today without the city's duty, and the policy's on top. */
    public static double pumpPriceAt(Game g, TaxPolicy p) {
        ham.citybuildersim.sectors.Retail r = g.getSectors().retail();
        double today = r.pumpPriceToday(g);
        if (!(today > 0)) return 0;
        return today - g.getEconomyManager().getTaxPolicy().chargedFuelDuty() + p.chargedFuelDuty();
    }

    /** THE BUDGET under `after` with the city's share of tuition at `shareThen`: last month's balance, and that plus change(). */
    public static Budget budget(Game g, TaxPolicy after, double shareThen) {
        TaxPolicy live = g.getEconomyManager().getTaxPolicy();
        double before = g.getEconomyManager().getNationalAccounts().getBalance();
        double share = g.getEducation().getTuitionSubsidy();
        return new Budget(before, before + change(g, live, after, share, shareThen));
    }

    /** What households would pay for tuition a month at a policy's scales and a share the city waives: Education.billedAt() less the waiver. */
    public static double tuitionPaid(Game g, TaxPolicy p, double share) {
        double clamped = Math.max(0, Math.min(1, share));
        return g.getEducation().billedAt(p::tuitionScaleOf) * (1 - clamped);
    }

    /** ...and what the city would waive of it. */
    public static double tuitionWaived(Game g, TaxPolicy p, double share) {
        double clamped = Math.max(0, Math.min(1, share));
        return g.getEducation().billedAt(p::tuitionScaleOf) * clamped;
    }

    /* =====================================================================
       THE POLICY RATE, THE FLOOR AND THE PENSIONER
       ===================================================================== */

    /** What savers would earn after inflation next month at this dial (M7): the bank's own choice at it, less the inflation savers expect - Game.realDepositRate()'s rule since 0.7.42, so at the dial in force it parts from the month's own only by the bank's step toward its funding (B1, 0.7.47; until then it took the year's inflation, the rule before 0.7.42). */
    public static double realDepositRateAt(Game g, double policy) {
        return g.getBank().depositRateAt(policy) - g.getExpectations().getExpectedInflation();
    }

    /** ...and the share of their spending above a basket a head the households would plan at it: HouseholdBalance.spendFactor() on that. */
    public static double spendFactorAt(Game g, double policy) {
        return HouseholdBalance.spendFactor(realDepositRateAt(g, policy));
    }

    /**
     * The factor every wage heads for with the real floor at `floor` (M6):
     * a job's target is its base at the floor times multiples that do not
     * move with it, and never under the floor in today's money, both in
     * proportion to the floor - so every target moves by the floor's own
     * ratio. "Once wages have walked there", about a year at ADJUST_RATE.
     */
    public static double wageMoveAt(Game g, double floor) {
        LabourMarket m = g.getLabourMarket();
        double now = m.targetWageAt(JobType.NO_DIPLOMA, m.getMinimumWage());
        return now > 0 ? m.targetWageAt(JobType.NO_DIPLOMA, floor) / now : 1;
    }

    /** What the city's own staff - teachers, doctors and nurses - would cost a month with every wage moved by wageMoveAt(): today's posts, paid at the targets of the new floor. */
    public static double cityPayrollAt(Game g, double floor) {
        return (g.getEducation().getPayroll() + g.getHealthcare().getPayroll()) * wageMoveAt(g, floor);
    }

    /* =====================================================================
       THE PROMISES, THE CLINIC AND THE SCHOOLS
       ===================================================================== */

    /**
     * What the promises cost the treasury in a month, net of what they
     * collect: the pension's gap, the sectors kept alive, EI past its
     * premiums, the students' grants and - since 0.7.43 - the food vouchers
     * paid at the month's sale - the Policy tab's PROMISES, which
     * the screen summed itself until 0.7.36. NOT the school subsidy, which
     * that sum held (the Policy spec's B17, D13): the tuition the city waives
     * is revenue it does not collect (Education.getSubsidy()), not money out
     * of the treasury.
     */
    public record Promises(double pensionGap, double eiPastPremiums, double subsidies, double grants,
                           double foodAssistance) {
        public double total() { return pensionGap + eiPastPremiums + subsidies + grants + foodAssistance; }
    }

    /** ...this month's. */
    public static Promises promises(Game g) {
        EconomyManager em = g.getEconomyManager();
        return new Promises(em.getPensionShortfall(), Math.max(0, em.getEiBenefits() - em.getEiPremiums()),
                g.getTotalSubsidyPaid(), em.getStudentGrants(), em.getFoodAssistance());
    }

    /**
     * What the food vouchers would have cost at the last sale at a dial of
     * the caller's (0.7.43): each eligible household's voucher at that share
     * of its baskets at the price that sale charged, up to the baskets it got
     * - HouseholdBalance.foodAssistanceAt(). Against the baskets of the sale
     * that has happened: a voucher bids, so the baskets would have moved too.
     *
     * AT THE PRICE THE SALE CHARGED since 0.7.45 (Retail.getChargedPrice()),
     * not the shelf as the bottom of the month repriced it: the vouchers were
     * struck at that price, so at the city's own dial this is what the month
     * paid, to the bit (HouseholdBalance.getFoodAssistancePaid()) - the house
     * rule for a dial card's rows at rest.
     */
    public static double foodAssistanceAt(Game g, double share) {
        return g.getHouseholdBalance().foodAssistanceAt(share, lastSalePrice(g));
    }

    /** ...and the households a voucher would go to at that price, at any dial over 0. */
    public static double foodAssistanceHouseholds(Game g) {
        return g.getHouseholdBalance().householdsEligibleForFoodAssistance(lastSalePrice(g));
    }

    /** ...the same at a dial of the caller's (0.7.45): none at 0, every eligible household at any dial over it. */
    public static double foodAssistanceHouseholdsAt(Game g, double share) {
        return share > 0 ? foodAssistanceHouseholds(g) : 0;
    }

    /** ...and the baskets the vouchers would have paid for at it (0.7.45): foodAssistanceAt() over the price. */
    public static double foodAssistanceBasketsAt(Game g, double share) {
        return g.getHouseholdBalance().foodAssistanceBasketsAt(share, lastSalePrice(g));
    }

    /** The price a basket was charged at the last sale, which the vouchers were struck at (Retail.getChargedPrice()). */
    public static double lastSalePrice(Game g) {
        return g.getSectors().retail().getChargedPrice();
    }

    /** What the treasury paid towards care this month: the service's cost less the fees it collected, the funerals' and the health premium (the Health page's "the treasury's share"). */
    public static double careTreasuryShare(Game g) {
        Healthcare care = g.getHealthcare();
        return care.getGrossCost() - care.getTreatmentFees() - care.getFuneralFees()
                - g.getEconomyManager().getHealthPremiums();
    }

    /**
     * ...at a fee scale and a premium of the caller's, with every patient
     * paying (the spec's B13: the fees at full on both sides of a move, so
     * the move is the dial's and not who was priced out this month).
     */
    public static double careTreasuryShareAt(Game g, double feeScale, double premiumRate) {
        Healthcare care = g.getHealthcare();
        return care.getGrossCost() - care.treatmentFeesAtOne() * Math.max(0, feeScale) - care.getFuneralFees()
                - g.getEconomyManager().premiumAt(premiumRate);
    }

    /**
     * What a place on a course costs a family as a share of a month's wage at
     * a tuition scale and a city share of the caller's: the fee at the scale,
     * less the share, over the best wage somebody who could take the course
     * earns (the unskilled job for a course anybody can take). Education
     * .MAX_BURDEN is where nobody enrols. 0 where nobody earns.
     */
    public static double schoolBurden(Game g, EducationType kind, double scale, double share) {
        LabourMarket m = g.getLabourMarket();
        WageBand from = kind.requires();
        double wage = from == null ? m.getWage(JobType.NO_DIPLOMA) : m.bestWageIn(from);
        double pocket = g.getEducation().feeAtOne(kind) * Math.max(0, scale) * (1 - Math.max(0, Math.min(1, share)));
        return wage > 0 ? pocket / wage : 0;
    }

    /** The fields' ground on the property roll under a policy (the farmland relief, T15): today's land price on the share the policy assesses. */
    public static double farmlandOnRoll(Game g, TaxPolicy p) {
        Sector fields = g.getSectors().byKey(Sectors.AGRICULTURE);
        if (fields == null) return 0;
        return g.getEconomyManager().landValueOf(fields) * p.assessedLandShare(Sectors.AGRICULTURE);
    }

    /** ...and the tax a month the relief forgoes on the rest of it, at the fields' own monthly rate. */
    public static double farmlandForgone(Game g, TaxPolicy p) {
        Sector fields = g.getSectors().byKey(Sectors.AGRICULTURE);
        if (fields == null) return 0;
        return g.getEconomyManager().landValueOf(fields) * (1 - p.assessedLandShare(Sectors.AGRICULTURE))
                * p.effectiveMonthlyPropertyRate(Sectors.AGRICULTURE);
    }

    /** What EI and the grants take from the treasury a month at a premium and a benefit rate of the caller's: the pool's bill at the rate and the grants, less the premiums off the wage bill. */
    public static double eiTreasuryAt(Game g, double premiumRate, double benefitRate) {
        EconomyManager em = g.getEconomyManager();
        return g.getUnemployment().benefitsAt(benefitRate) + em.getStudentGrants() - em.premiumAt(premiumRate);
    }

    /** The pension's gap under a policy: the pensions at its rate less the contributions at its rate off the wage bill - getPensionShortfall()'s rule (M3, M4). */
    public static double pensionGapUnder(Game g, TaxPolicy p) {
        EconomyManager em = g.getEconomyManager();
        return Math.max(0, em.pensionsPaidUnder(p) - em.contributionsAt(p.getContributionRate()));
    }

    /** ...and what the contributions cover of the pensions under it - getPensionCoverage()'s rule; 1 with nothing owed. */
    public static double pensionCoverageUnder(Game g, TaxPolicy p) {
        EconomyManager em = g.getEconomyManager();
        double owed = em.pensionsPaidUnder(p);
        return owed > 0 ? em.contributionsAt(p.getContributionRate()) / owed : 1;
    }

    /**
     * What one pensioner household would have to spend under `after` (M11):
     * the retired row, whose income is the pension bill, with the bill moved
     * by what the dial's move would change it by at today's seniors -
     * HouseholdBalance.disposableWithRowMoved(). The DIAL's move, so at the
     * city's own policy it is the household's ledger to the bit (its bill was
     * struck a few seniors ago, and the difference is no decision). A pension
     * applied since the month was struck reaches the ledger when the next
     * month runs; until then the "before" is the month the household had.
     */
    public static double pensionerHasUnder(Game g, TaxPolicy after) {
        EconomyManager em = g.getEconomyManager();
        return g.getHouseholdBalance().disposableWithRowMoved(HouseholdAccounts.RETIRED,
                em.pensionsPaidUnder(after) - em.getPensionsPaid());
    }

    /* =====================================================================
       THE FUND'S WITHDRAWAL (0.7.48, C2)
       ===================================================================== */

    /**
     * What the fund's withdrawal would pay the treasury next month at a dial
     * of the caller's, on the fund as it stands: the due; what its cash
     * would pay of it; the rest - over the default sold from its market book
     * (toSell), at or under it not paid (unpaid); and what it would be worth
     * in a year earning nothing.
     */
    public record Withdrawal(double due, double fromCash, double toSell, double unpaid, double yearOn) { }

    /**
     * ...struck as TreasuryFund.payTransfer() strikes it: the due at the
     * rate's whole steps on Game.fundValue() (TreasuryFund.withdrawalAt() -
     * Game.fundTransferDue() at the dial in force, to the bit); its cash
     * after what last month's sale still owes (getToRaise()), which the top
     * of the month pays first; and the value times (1 - the rate) to the
     * twelfth, a year of withdrawals with nothing earned. Prices, dividends
     * and what the book takes of a sale all move it by the month's top.
     */
    public static Withdrawal fundWithdrawalAt(Game g, double rate) {
        TreasuryFund f = g.getFund();
        double value = g.fundValue();
        int steps = TreasuryFund.stepsFor(rate);
        double due = TreasuryFund.withdrawalAt(rate, value);
        double cash = Math.max(0, f.getCash() - Math.max(0, f.getToRaise()));
        double fromCash = Math.max(0, Math.min(due, cash));
        boolean sells = steps > TreasuryFund.DEFAULT_WITHDRAWAL_STEPS;
        double yearOn = value > 0 ? value * Math.pow(1 - steps * TreasuryFund.WITHDRAWAL_STEP, TreasuryFund.YEAR_MONTHS) : 0;
        return new Withdrawal(due, fromCash, sells ? due - fromCash : 0, sells ? 0 : due - fromCash, yearOn);
    }

    /* =====================================================================
       ZONING (0.7.107, batch Z1; the project's spec-zoning.md 4)

       Zoning's dials before and after, as every dial's: a staged Zoning
       (Zoning.copy() with the dial moved) read against the land as it stands
       (LandManager.heldUnder(), groundUnder()), and the same read of the
       city's own the "before". A zone: what it holds, what that ground would
       fetch at the going price - what it keeps from everyone else - the
       price the city gives up selling it at the zone's share, and the
       property tax a month its assessment forgoes on the ground built in it.
       The law: the forest's three kinds, the ground left free for anyone,
       the wood it keeps standing for ever and the wood that may be cut -
       Forestry's crews on it are I9's (materials). The zone's tax rows are
       the sector's own offsets, previewed where they always were (taxTake()).
       ===================================================================== */

    /**
     * One zone under a zoning, on the land as it stands: its size and what
     * its sector has built in it, square feet; what it holds of the free
     * ground; its price share; the held ground at the going price; the price
     * given up selling that at the zone's share; and the property tax a month
     * forgone on the ground built in it (Z4) - all nothing for a sector with
     * no zone.
     */
    public record ZoneAt(String sector, double sizeSqFt, double usedSqFt, double heldSqFt, double priceShare,
                         double heldAtGoingPrice, double priceGivenUp, double taxRelief) { }

    /** ...struck from LandManager.heldUnder() and the roll's rule: the relief is the zone's built ground (never more than the sector stands on) at the roll's price less the zone's, on the share the roll carries, at the sector's own monthly rate - at the city's own zoning, getAssessedValue()'s difference to the bit's rounding. */
    public static ZoneAt zoneAt(Game g, Zoning z, String sector) {
        LandManager land = g.getLandManager();
        Zoning.Zone zone = z == null ? null : z.zone(sector);
        if (zone == null) return new ZoneAt(sector, 0, 0, 0, 1, 0, 0, 0);
        double going = land.getPricePerSqFt();
        double held = land.heldUnder(z, sector);
        EconomyManager em = g.getEconomyManager();
        TaxPolicy p = em.getTaxPolicy();
        double built = Math.min(g.getBuildingManager().getLandSqFtBySector(sector), zone.usedSqFt());
        double relief = built * em.getLandPricePerSqFt() * (1 - zone.priceShare())
                * p.assessedLandShare(sector) * p.effectiveMonthlyPropertyRate(sector);
        return new ZoneAt(sector, zone.targetSqFt(land.getOwnedSqFt()), zone.usedSqFt(), held, zone.priceShare(),
                held * going, held * going * (1 - zone.priceShare()), relief);
    }

    /** What a staged zoning would change in the property tax a month: every zone's relief at the city's own zoning less at the staged one's. */
    public static double zoningTaxChange(Game g, Zoning after) {
        Zoning live = g.getZoning();
        double moved = 0;
        for (String key : Sectors.KEYS) moved += zoneAt(g, live, key).taxRelief() - zoneAt(g, after, key).taxRelief();
        return moved;
    }

    /**
     * The forest law under a zoning, on the land as it stands: the
     * protected, working and open forest and the ground free for anyone,
     * square feet; the wood it keeps standing for ever and the wood that may
     * be cut, cubic metres; and the share of the forest's full stock it keeps
     * standing - where the forest settles under the law (spec-zoning 3.3).
     */
    public record ForestAt(double protectedSqFt, double workingSqFt, double openSqFt, double freeForAnyone,
                           double protectedWood, double cuttableWood, double standsForEver) { }

    /** ...struck by LandManager.groundUnder(). */
    public static ForestAt forestAt(Game g, Zoning z) {
        LandManager land = g.getLandManager();
        LandManager.GroundUnder u = land.groundUnder(z);
        double full = land.getOwnedAmount(Resource.FOREST);
        return new ForestAt(u.protectedSqFt(), u.workingSqFt(), u.openSqFt(), u.freeForAnyone(), u.protectedWood(),
                u.cuttableWood(), full > 0 ? u.protectedWood() / full : 0);
    }

    /* =====================================================================
       THE RULE'S STRICTNESS (0.7.52)
       ===================================================================== */

    /**
     * What the rule would do at one step of the strictness dial, at the
     * city's target: the step; what it aims at, in words
     * (DebtManager.aimWords(): "aims under 2.0%, at 1.0%", "acts only past
     * 4.0% (or under 2.0%)"); the rate it sets on target; and its advice at
     * this year's inflation, inside the dial - NaN before there is a year of
     * prices to read.
     */
    public record RuleAt(DebtManager.Strictness step, String aims, double onTarget, double advised) { }

    /** ...struck by DebtManager.ruleRate() and advisedPolicyRate() at that step; at the step in force, the rule's own to the bit. */
    public static RuleAt ruleAt(Game g, DebtManager.Strictness step) {
        DebtManager m = g.getDebtManager();
        PriceIndex px = g.getPriceIndex();
        double target = m.getInflationTarget();
        return new RuleAt(step, DebtManager.aimWords(target, step), DebtManager.ruleRate(target, target, step),
                px.hasRate() ? m.advisedPolicyRate(px.inflation(), step) : Double.NaN);
    }
}
