package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BuildingsTemplate;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.Sector;

import java.util.ArrayList;
import java.util.List;

/**
 * The automobile industry. THE THIRTEENTH SECTOR (2026-09-16, Jerus's call).
 *
 * Jerus: "we are going to also add the autombile industry, where customers can
 * buy cars and business can buy vans/trucks, and rail sector can buy trains."
 *
 * =======================================================================
 * WHAT IT IS FOR, AND IT IS NOT THE CARS
 * =======================================================================
 *
 * Until today this game's manufacturing chain stopped one link short of
 * anything a person could own. Ore becomes steel, steel becomes fabricated
 * steel and machinery, and then BOTH LEAVE - see Good's header on those two:
 * "nothing in the city buys a beam or a machine". A city could build the whole
 * industrial chain and still have nothing at the end of it but a dock.
 *
 * An assembly plant is the first thing that buys them. That is the point of
 * this sector: it gives Manufacturing a customer that is not the world, and it
 * gives the city a FOURTH link - ore, steel, fabrication, assembly - which is
 * the longest chain in the game and the one that pays the most people.
 *
 * AND THE HEAVY HALF OF THE CHAIN CANNOT BE SHORTCUT. Fabricated steel is not
 * importable, so a city with money and no fabrication shop cannot be in the
 * car business however much it wants to be - it can only buy cars, like
 * anybody else. plan() below refuses to sink a plant the city cannot supply,
 * for exactly the reason HeavyIndustry refuses to sink a mill with no ore
 * behind it: a sector that expands on a business case it does not have is the
 * borrowing spiral the investment engine was written to end.
 *
 * MACHINERY CAN COME OFF A SHIP, and that asymmetry is deliberate and was
 * forced by a measurement. A car is three and a half tonnes of fabricated
 * steel and four hundred kilos of machinery: the bulky input is the one a city
 * has to be able to make, and the specialised one is the one every
 * industrialising country in history has imported. Gating on both was the
 * first draft, and the four-thousand-month playtest showed why it could not
 * stand - 179 Fabrication Works and ZERO Machine Works, because Manufacturing
 * scores its two products against each other every month and fabrication wins
 * every month. An industry gated on a building the model never builds is a
 * dead branch, not a design. Machinery got an import ceiling instead, which is
 * what Good's own header had said would happen the day anything here bought
 * one. A city that DOES build a machine works still wins: it buys its
 * machinery at the local floor instead of the world's landed price.
 *
 * =======================================================================
 * THREE THINGS, THREE CUSTOMERS
 * =======================================================================
 *
 * CARS go to households, VANS to businesses and ROLLING STOCK to the railway -
 * and in this batch NONE of those customers exists yet. All three clear at the
 * export floor, like fabricated steel and machinery before them, which makes
 * this an export industry on the day it opens and is what Jerus asked for when
 * he picked "a real export industry": building cars is a strategy in its own
 * right and not just a way to supply your own people.
 *
 * The domestic side comes next, and it is the larger half: a household that
 * owns a car puts far more on the road than one that does not, a business
 * short of vans cannot move what it makes, and a railway with track and no
 * locomotives carries nothing. See claude/the-railway.md for where that goes.
 */
public final class Automotive extends Sector {

    /**
     * The most of the city's WHOLE fabrication output one new plant may want.
     *
     * A PLANT WITH NO SUPPLIER IS NOT A THIN BUSINESS, IT IS NO BUSINESS, and
     * that is the difference between this test and Manufacturing's deliberate
     * refusal to run one. A fabricator with no local mill falls back on
     * imported steel and earns eighteen per cent instead of thirty - thin, but
     * real. An assembly plant with no local fabricator falls back on nothing at
     * all, because the world will not sell it a beam at any price.
     *
     * "SPARE" WAS THE WRONG WORD AND IT COST A FIXTURE THREE HUNDRED MONTHS.
     * The first version of this test asked HeavyIndustry's question - is there
     * spare local supply - and that question only works for ore, because the
     * mills are the only thing that buys ore. Fabricated steel has a second
     * buyer with an infinite appetite: the world. None of it is ever spare, so
     * measuring Manufacturing's nameplate against this sector's own demand
     * returned the whole of it, every month, from the first fabrication shop
     * onwards.
     *
     * What that did, measured: EducationCheck's fixture city sank a $172m
     * Assembly Plant in month SEVENTEEN, against two fabrication shops whose
     * output was entirely spoken for, borrowed the whole price of it, and paid
     * $1,745 a month of interest on zero revenue until it was written off. The
     * harness that caught it was measuring tuition.
     *
     * THE RIGHT QUESTION IS PROPORTION, not spareness. The fabricators will
     * happily divert from the export floor to a local buyer - that is what the
     * band is for, and the local price is never below the floor - so the steel
     * IS available. What must not happen is a plant so large against the city's
     * fabrication that it could never be fed. A quarter is the number: it makes
     * the industry follow the fabricators up the ladder instead of leaping
     * ahead of them, and it puts the small rung first in a young city, which
     * is what a ladder is for.
     */
    public static final double MAX_SHARE_OF_LOCAL_SUPPLY = .25;

    public Automotive() {
        super("Automotive", "Automotive", BuildingType.AUTOMOTIVE);
        makes(Good.CARS);
        makes(Good.VANS);
        makes(Good.ROLLING_STOCK);

        uses(Good.FABRICATED_STEEL);
        uses(Good.MACHINERY);

        blurb("Builds cars, vans and locomotives out of fabricated steel and "
                + "machinery - the only thing in the city that buys either. The top "
                + "of the longest chain there is, and the biggest payroll on it.");
    }

    /* =====================================================================
       PLANNING - can the city supply it, and then is it worth it
       ===================================================================== */

    /** What the city can fabricate a month, whoever is currently buying it. */
    public double localSupplyOf(Good g, Game game) {
        return game == null ? 0 : game.getSectors().manufacturing().getCapacity(g);
    }

    /** The largest draw a new plant may have on that supply. See MAX_SHARE_OF_LOCAL_SUPPLY. */
    public double biggestDrawAllowed(Good g, Game game) {
        return localSupplyOf(g, game) * MAX_SHARE_OF_LOCAL_SUPPLY;
    }

    /**
     * Manufacturing's shape - the best template by profit over cost, floored on
     * staffing - with HeavyIndustry's supply gate in front of it.
     *
     * AN ASSEMBLY PLANT IS THE LARGEST SINGLE HIRING DECISION IN THE GAME,
     * larger than a rail terminal, so the eighty-per-cent staffing wall matters
     * here more than anywhere. See Sector.MIN_STAFFABLE_TO_ORDER.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        String sector = key();
        if (buildings.getUnderConstructionBySector(sector) >= BusinessInvestment.MAX_CONCURRENT_ORDERS) {
            return BusinessInvestment.Decision.no(sector, "already building");
        }

        double fabricated = localSupplyOf(Good.FABRICATED_STEEL, game);
        if (fabricated <= 0) {
            return BusinessInvestment.Decision.no(sector,
                    "nothing here fabricates steel, and the world will not sell a beam at any price");
        }
        double biggestDraw = fabricated * MAX_SHARE_OF_LOCAL_SUPPLY;

        BuildingsTemplate best = null;
        double bestScore = 0;
        boolean sawStaffingWall = false;
        boolean sawSupplyWall = false;
        double bestStaffable = 0;

        for (BuildingsTemplate t : buildings.getTemplatesBySector(sector)) {

            // No plant so large against the city's fabrication that it could
            // never be fed. The machinery it can buy from the world; the steel
            // it cannot. See MAX_SHARE_OF_LOCAL_SUPPLY.
            if (t.uses(Good.FABRICATED_STEEL) > biggestDraw) {
                sawSupplyWall = true;
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
            double score = estimatedMonthlyProfit(t, plans) * staffable / cost;
            if (score > bestScore) {
                bestScore = score;
                best = t;
            }
        }

        if (best == null) {
            if (sawSupplyWall) {
                return BusinessInvestment.Decision.no(sector, String.format(
                        "the city fabricates %s tonnes a month; the smallest plant would want more "
                        + "than the %s one plant may take",
                        Formats.INSTANCE.count(fabricated),
                        Formats.INSTANCE.count(Math.max(0, biggestDraw))));
            }
            if (sawStaffingWall) {
                return BusinessInvestment.Decision.no(sector, String.format(
                        "the city could staff %.0f%% of a plant; it wants %.0f%%",
                        bestStaffable * 100, MIN_STAFFABLE_TO_ORDER * 100));
            }
            return BusinessInvestment.Decision.no(sector,
                    "the parts cost more than the vehicle fetches");
        }
        if (plans.plotsAvailableFor(best) < 1) {
            return BusinessInvestment.Decision.noLand(sector, plans.landReason(best));
        }
        return new BusinessInvestment.Decision(sector, best, 1,
                "fabricated steel worth more with wheels on it", true);
    }

    /**
     * A price taker with nobody at home to sell to - YET.
     *
     * The default would compare a car market's local demand against nameplate
     * and scrap every plant in the city, because in this batch there is no
     * local demand for a car at all: households do not own them until the next
     * one. The same reasoning Manufacturing writes out for fabricated steel,
     * and the same answer - shrink on distress, not on a demand reading.
     *
     * THIS IS THE LINE THAT CHANGES when households start buying. A car IS a
     * stockable good with a local market, unlike a beam, so the moment there
     * are buyers this should become the default rule rather than null.
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) { return null; }

    /* ------------------------------------------------------------ the screen */

    @Override
    public List<Sector.Line> operations(Game game) {
        List<Sector.Line> lines = new ArrayList<>();
        Formats f = Formats.INSTANCE;

        lines.add(Line.head("The plants"));
        lines.add(Line.of("Staffed", f.pct(getAverageFill()),
                getAverageFill() < .9 ? Line.Tone.WARN : Line.Tone.NONE));
        for (Good g : new Good[] { Good.CARS, Good.VANS, Good.ROLLING_STOCK }) {
            double cap = getCapacity(g);
            if (cap <= 0) continue;
            Output o = output(g);
            lines.add(Line.of(g.label(), String.format("%s a month, %s sold here and %s shipped",
                    f.count(cap), f.count(o.soldLocal), f.count(o.exported))));
        }

        lines.add(Line.head("The parts"));
        for (Good g : new Good[] { Good.FABRICATED_STEEL, Good.MACHINERY }) {
            double want = getInputAtCapacity(g);
            if (want <= 0) continue;
            Input in = input(g);
            lines.add(Line.of(g.label(), String.format("%s tonnes wanted, %s bought here",
                    f.count(want), f.count(in.boughtLocal)),
                    in.boughtLocal < want * .9 ? Line.Tone.WARN : Line.Tone.NONE));
        }

        lines.add(Line.note("The fabricated steel cannot be imported at any price, so a city "
                + "that cannot make beams cannot make vehicles. The machinery can be - dearly. "
                + "A city with its own machine works pays the local floor instead."));
        return lines;
    }
}
