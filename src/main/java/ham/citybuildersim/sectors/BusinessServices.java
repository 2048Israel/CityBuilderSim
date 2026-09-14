package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.JobType;
import ham.citybuildersim.Sector;

import java.util.List;

/**
 * Somebody else's work, done here, paid for from outside.
 *
 * WHY (2026-09-12). Jerus, on why the city plateaus: every job-creating sector
 * in the game is either a DOMESTIC SERVICE whose demand is the population
 * itself - retail, real estate, construction, healthcare, education, and they
 * scale WITH people so they cannot lead them - or the ONE TRADABLE CHAIN of
 * ore to steel, which geology gates. Once the domestic sectors are saturated
 * and the ore is out, nobody in the city can pay for what another worker would
 * produce. That is the wall, and it sits at eight hundred to fourteen hundred
 * people past their twelve months of EI in every seed.
 *
 * There are exactly three ways out: sell something to foreigners, replace an
 * import, or have the government employ people. Inventing occupations is none
 * of them - a sector only breaks the stalemate if its output can LEAVE THE
 * CITY. This is the first one that does it with labour instead of a deposit.
 *
 * THREE RUNGS, one per rung of the education ladder, so that building a
 * university finally has a payoff other than staffing a hospital:
 *
 *   Contact Centre                support work       mostly unskilled
 *   Shared Services Centre        back-office work   diploma and college
 *   Engineering Services Office   engineering work   licensed engineers, GATED
 *
 * THE MECHANISM IS ALREADY BUILT, which is the best thing about it. An
 * export-only BAND good clears at its FLOOR - there is no local demand to lift
 * it off the bottom - so revenue per seat is fixed by the world and the wage
 * bill is the entire constraint. Which makes the sector self-limiting with no
 * new rule: it hires, the labour market walks that band's wage up on
 * clamp(tightness^0.5, .70, 4.0), unit cost rises, the expansion test
 * (profit >= interest * 1.25) fails, and twenty-four months of losses sheds
 * plant. BusinessInvestment.runningCostOf() already prices a prospective
 * building's payroll at TODAY's wages, so the brake needs no wiring at all.
 *
 * And because the world's price arrives THROUGH THE EXCHANGE RATE, a weak
 * currency wins the city contracts and a strong one kills the sector. That is
 * offshoring, and it is the model working rather than a special case. Rising
 * wages also draw migrants, who add supply, which relieves tightness - so the
 * equilibrium is a BIGGER CITY, which is the entire point of the batch.
 *
 * NO CEILING ON THE WORLD'S APPETITE, per Jerus. The research says the raw wage
 * gap that drives relocation is ten to one but the realised saving is only
 * 45-55%, because telecom, management and attrition eat the rest - so work
 * actually moves at about a 2x loaded-cost ratio, not a wage ratio. The game
 * does not need that separately: the world price already embeds what a client
 * will pay, so local loaded cost crossing it IS the threshold, expressed as a
 * price. What would falsify the design is a seed where this sector employs a
 * third of the workforce and the wage never moves.
 *
 * Calibrated on Nova Scotia's own call-centre decade, which is also the best
 * evidence the shape is right: 3,700 jobs across 13 centres in 1999, 15,693
 * across 53 by 2007 - about 296 seats a centre and 3.5% of provincial
 * employment - landed with roughly $8,800 a job of payroll rebates, paying
 * $8.50-10.50 an hour where one steelworker went from $16 to $8.75, and then
 * shedding two thousand jobs to the Philippines and India inside a decade.
 * Boom and bleed, documented. See claude/business-services.md.
 */
public final class BusinessServices extends Sector {

    public BusinessServices() {
        super("Business Services", "Business Services", BuildingType.BUSINESS_SERVICES);
        makes(Good.SUPPORT_WORK);
        makes(Good.BACK_OFFICE_WORK);
        makes(Good.ENGINEERING_WORK);
        blurb("Sells months of its people's work to clients who are not here - support "
                + "desks, back offices, engineering drawings. It needs no ore and almost "
                + "no ground, only people, so it is the one sector that can grow before "
                + "the city does. The world sets the price; the wage bill decides whether "
                + "it is worth doing. A strong currency closes it.");
    }

    /** Seats of every kind, staffed or not - the sector in one number. */
    public double getSeats() {
        return getCapacity(Good.SUPPORT_WORK)
                + getCapacity(Good.BACK_OFFICE_WORK)
                + getCapacity(Good.ENGINEERING_WORK);
    }

    /**
     * What a seat-month of each kind fetches in the city's own money.
     *
     * The export price, through the exchange rate - which is why this number
     * falls when the currency strengthens and the sector dies without anything
     * having changed about the city.
     */
    public double priceOfSeat(Good g) {
        return markets == null ? 0 : Math.max(0, markets.get(g).exportPrice());
    }

    /**
     * Payroll as a share of revenue, at the wages the city is paying now.
     *
     * The single number that says whether this sector has a future here. Real
     * contact centres run 67-70%, real shared-services 64-65%, real engineering
     * consultancies about 60-65%. Past 100% the sector is shedding.
     */
    public double payrollShare() {
        // Against what it ACTUALLY billed, not against capacity times price. The
        // first version divided by nameplate and read 69% on a sector whose
        // payroll was 95% of its revenue - the seats it could not staff were
        // counted as earning. A ratio that flatters is worse than no ratio.
        double revenue = statement().revenue;
        return revenue > 0 ? statement().payroll / revenue : 0;
    }

    /* ------------------------------------------------------------------ plan */

    /**
     * Overridden for the same reason Mining and Heavy Industry override it: the
     * generic planner forecasts from LOCAL demand, and BusinessInvestment says
     * so in its own header - "the world is not demand". An export-only good's
     * local demand is zero for ever, so the generic path would decide output is
     * permanently ahead of it and never build anything.
     *
     * What gates it instead is the profit test the brake already applies, plus
     * the licence: a template the city cannot staff the core of is skipped
     * rather than ordered, because buildStack() would refuse it anyway and a
     * sector that keeps asking is a sector that never asks for anything else
     * (MAX_CONCURRENT_ORDERS is one).
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        BuildingsTemplate best = null;
        double bestScore = 0;
        boolean sawStaffingWall = false;
        double bestStaffable = 0;
        boolean sawLicenceWall = false;
        JobType wanted = null;
        double shortBy = 0;

        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {

            JobType licence = t.getRequiresLicence();
            if (licence != null && !game.hasLicencesFor(t, 1)) {
                sawLicenceWall = true;
                double need = game.licencesNeededFor(t, 1);
                double have = game.getPopulationManager().spareLicences(licence);
                if (wanted == null || need - have < shortBy) {
                    wanted = licence;
                    shortBy = need - have;
                }
                continue;
            }

            double staffable = staffableShare(t);
            if (staffable < MIN_STAFFABLE_TO_ORDER) {
                if (staffable > bestStaffable) bestStaffable = staffable;
                sawStaffingWall = true;
                continue;
            }

            double cost = plans.getCostOf(t, 1);
            if (cost <= 0) continue;
            // Still discounted as well as floored: the floor says whether any
            // of them is worth opening, the weight says which.
            double score = estimatedMonthlyProfit(t, plans) * staffable / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) {
            if (sawLicenceWall && wanted != null) {
                return BusinessInvestment.Decision.no(sector, String.format(
                        "%,.0f more %s before a practice could open",
                        Math.ceil(shortBy), licenceLabel(wanted)));
            }
            if (sawStaffingWall) {
                return BusinessInvestment.Decision.no(sector, String.format(
                        "the city could staff %.0f%% of a practice; it wants %.0f%%",
                        bestStaffable * 100, MIN_STAFFABLE_TO_ORDER * 100));
            }
            return BusinessInvestment.Decision.no(sector,
                    "the wage bill is above what the world pays for the work");
        }
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }
        return new BusinessInvestment.Decision(sector, best, 1,
                "work the world will pay more for than it costs to do here", true);
    }

    /**
     * A price taker with no stock: it shrinks on distress, not on a demand
     * reading. Same as Heavy Industry, and for the same reason - there is no
     * local demand series to compare capacity against.
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) { return null; }

    /**
     * A licence, in words, without reaching into the UI for it - a sector must
     * not depend on the screen that draws it.
     */
    private static String licenceLabel(JobType job) {
        if (job == null) return "licences";
        return switch (job) {
            case UNIV_HIGHTECH_ENG -> "engineering licences";
            case UNIV_DOCTOR       -> "medical licences";
            case UNIV_LAW          -> "law licences";
            case UNIV_FINANCE      -> "finance licences";
            default                -> "licences";
        };
    }

    /* ------------------------------------------------------------ the screen */

    @Override
    public List<Line> operations(Game game) {
        List<Line> lines = super.operations(game);
        if (game == null) return lines;
        Formats f = Formats.INSTANCE;

        lines.add(Line.head("What the world pays, a seat a month"));
        Good[] kinds = {Good.SUPPORT_WORK, Good.BACK_OFFICE_WORK, Good.ENGINEERING_WORK};
        double seats = getSeats();
        for (Good g : kinds) {
            double held = getCapacity(g);
            if (held <= 0) continue;
            lines.add(Line.of(g.label(), f.cash(priceOfSeat(g)) + " x " + f.count(held) + " seats"));
        }
        if (seats <= 0) {
            lines.add(Line.note("Nothing standing. This is the only sector whose customer is not "
                    + "in the city, so it can hire before the city has grown - but only while "
                    + "the wage bill stays under what the work fetches abroad."));
            return lines;
        }

        double share = payrollShare();
        Line.Tone tone = share <= 0 ? Line.Tone.MUTED
                : share < .80 ? Line.Tone.GOOD
                : share < 1.0 ? Line.Tone.WARN
                : Line.Tone.BAD;
        lines.add(Line.head("Whether it has a future here"));
        lines.add(Line.of("Wages, as a share of what the work fetches", f.pct(share), tone));
        if (share >= 1.0) {
            lines.add(Line.note("The city costs more than the work is worth. Nothing about the "
                    + "clients has changed - either wages here have risen or the currency has, "
                    + "and this is what closes a contact centre in life too."));
        } else if (share >= .80) {
            lines.add(Line.note("Thin. Real contact centres run about seventy percent and real "
                    + "engineering practices about sixty; past one hundred the offices start "
                    + "closing."));
        }
        return lines;
    }
}
