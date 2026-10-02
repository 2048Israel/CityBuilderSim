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
     * students' grant; and the interest on the graduates' loans.
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
        return moved;
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

    /** What savers would earn after inflation next month at this dial (M7): the bank's own choice at it, less the year's inflation - Game.realDepositRate()'s rule. */
    public static double realDepositRateAt(Game g, double policy) {
        return g.getBank().depositRateAt(policy) - g.getPriceIndex().inflation();
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
     * premiums and the students' grants - the Policy tab's PROMISES, which
     * the screen summed itself until 0.7.36. NOT the school subsidy, which
     * that sum held (the Policy spec's B17, D13): the tuition the city waives
     * is revenue it does not collect (Education.getSubsidy()), not money out
     * of the treasury.
     */
    public record Promises(double pensionGap, double eiPastPremiums, double subsidies, double grants) {
        public double total() { return pensionGap + eiPastPremiums + subsidies + grants; }
    }

    /** ...this month's. */
    public static Promises promises(Game g) {
        EconomyManager em = g.getEconomyManager();
        return new Promises(em.getPensionShortfall(), Math.max(0, em.getEiBenefits() - em.getEiPremiums()),
                g.getTotalSubsidyPaid(), em.getStudentGrants());
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
}
