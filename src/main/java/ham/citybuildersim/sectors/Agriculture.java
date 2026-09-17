package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingType;
import ham.citybuildersim.BusinessInvestment;
import ham.citybuildersim.EconomyManager;
import ham.citybuildersim.Formats;
import ham.citybuildersim.Game;
import ham.citybuildersim.Good;
import ham.citybuildersim.Sector;

import java.util.List;

/**
 * The fields, and what they cost the city in ground.
 *
 * THE TENTH SECTOR (2026-09-13, Jerus's call): "lets add farms, which use land
 * a lot, but are labour and energy cheap, and give the textile or whatever its
 * called the goods to manufacture food, and yes that means imports also exist
 * if needed."
 *
 * It closes the last open end in the goods economy. Ore becomes steel becomes a
 * beam that leaves; building material has a plant; a seat-month has a client.
 * Food had nothing behind it at all - the mills conjured it - and that is why
 * the Food Processing Plant was the most profitable building in the game. Now
 * there is a field behind the loaf, and the chain is four links deep:
 *
 *     ground -> crops -> food -> groceries
 *
 * WHAT MAKES IT DIFFERENT FROM EVERY OTHER SECTOR is the shape of its costs.
 * Labour is a tenth of what a farm sells and power is a fiftieth; what it
 * spends is GROUND, and it spends more of it than anything else in the game by
 * a factor of three. A Mixed Farm stands on twenty-four city blocks. The
 * Fabrication Works, which was the largest thing an investor could build until
 * this morning, stands on eleven and a half; a coal power station on twenty.
 *
 * SO THE SECTOR IS A CLOCK, and the clock is the land price. A young city buys
 * its fields at a dollar and change a square foot and farming is the best
 * return on the board; a city of a hundred thousand is paying sixty dollars,
 * the ground under one farm is worth more than forty farms' worth of buildings,
 * and no investor will ever sink another. That is not a defect. It is what
 * happened to every market garden that was ever within a day's cart of a
 * growing town, and the game now says it with numbers.
 *
 * WHICH IS WHY THE PLAYER GETS A DIAL. Farmland is assessed at USE value rather
 * than development value almost everywhere in Canada and the United States,
 * for exactly this reason - without it the tax on the ground exceeds what the
 * ground can grow long before the city reaches the fence. See
 * TaxPolicy.FARMLAND_RELIEF: at nothing the fields become suburbs and the city
 * imports its dinner; at full relief it keeps them, and forgoes the tax on what
 * by then is a good share of its whole assessment. Jerus's call, taken against
 * the alternative of simply making an acre of wheat out-earn an acre of houses,
 * which is the opposite of the reason cities exist.
 *
 * AND A THIRD RUNG THAT ESCAPES THE CLOCK. A Greenhouse Complex grows twenty
 * times as much off an acre as a field does and pays for it in electricity and
 * people - which is the Netherlands, and Leamington, and every square mile of
 * glass anybody ever built next to a city instead of away from one. Early it is
 * an expensive way to do what a field does cheaply. Late, when the ground under
 * a field costs more than the field earns in a decade, it is the only farming
 * left. The sector has a late game because that building is in it.
 *
 * See claude/farms.md.
 */
public final class Agriculture extends Sector {

    public Agriculture() {
        super("Agriculture", "Agriculture", BuildingType.AGRICULTURE);
        /*
         * FIVE GOODS, AND THE SECTOR'S OWN LIST IS WHAT MAKES THEM EXIST.
         *
         * Markets iterates goodsMade() to decide what comes to market, so this
         * line - not the templates - is what brings a good into being. The day
         * the ovens were repointed at BREAD and FoodIndustry still said FOOD,
         * the sector had 858,000kg of nameplate capacity and produced nothing,
         * with no exception and no log line. Two places name what a sector
         * makes and they have to agree: the buildings say how much, this says
         * what.
         */
        makes(Good.CROPS);
        makes(Good.MEAT);
        makes(Good.DAIRY_EGGS);
        makes(Good.VEGETABLES);
        makes(Good.FRUIT);
        blurb("Grows the city's dinner, and the grain its bakeries turn into bread. "
                + "It needs almost no people and almost no power - what it needs is "
                + "ground, more of it than anything else the city builds, and ground "
                + "is the one thing a growing city runs out of. Whether a field can "
                + "outbid a warehouse for the same acre is a tax decision, and "
                + "whether the city grows its own dinner at all is a price one.");
    }

    /* ---------------------------------------------------------------- reading */

    /** Tonnes the fields can bring in a month, at the rate they are running. */
    public double getHarvest() {
        return getCapacity(Good.CROPS) * getOperatingRate();
    }

    /**
     * Months of the city's BAKED eating the fields cover.
     *
     * IT USED TO MEAN ALL EATING, and it stopped meaning that on 2026-09-15.
     * The mills turned a tonne of crops into 9.5 units of FOOD, and a unit of
     * FOOD was a person fed for a month - everything they ate. The shelf is
     * thirteen goods now and the ovens make two of them, so what a tonne of
     * crops covers is bread and the things next to it on the counter, which is
     * about a tenth of the basket by value.
     *
     * That is a smaller claim and an honest one. A city that grows all its own
     * crops is not a city that feeds itself; it is a city that bakes its own
     * bread and imports its dinner, which is what this model actually says.
     */
    public double getSelfSufficiency(Game game) {
        if (game == null) return 0;
        double eaters = game.getPopulationManager().getPopulation();
        return eaters > 0 ? getHarvest() * BAKED_KG_PER_TONNE / (eaters * BAKED_KG_A_HEAD) : 0;
    }

    /**
     * Kilograms of bread and bakery goods a tonne of crops becomes.
     *
     * 52.5% of the crop's mass, which is what both ovens are struck at - see
     * BuildingManager, building 3 and building 7, for why it is the margin and
     * not the milling that sets it. Replaced FOOD_PER_TONNE = 9.5 when a unit
     * of food stopped being a thing anybody could buy.
     */
    public static final double BAKED_KG_PER_TONNE = 525;

    /** What one person eats of the city's own baking a month: 3.5kg of bread, 1.5kg of the rest. */
    public static final double BAKED_KG_A_HEAD = 5.0;

    /** Ground the sector stands on, in square feet. */
    public double getLandSqFt() {
        return buildings == null ? 0 : buildings.totalBySector(key(), t -> t.getLandSqFt());
    }

    /** ...and what the city would charge for it if it taxed it like anything else. */
    public double getLandValue(EconomyManager economy) {
        return economy == null ? 0 : getLandSqFt() * Math.max(0, economy.getLandPricePerSqFt());
    }

    /**
     * The ground bill as a share of what the fields sell - the clock, in one
     * number. Property tax plus nothing else, because the ground is the only
     * thing this sector really buys.
     */
    public double groundShare() {
        double revenue = statement().revenue;
        return revenue > 0 ? statement().propertyTax / revenue : 0;
    }

    /** Wages as a share of the same, which is the number that should stay small. */
    public double payrollShare() {
        double revenue = statement().revenue;
        return revenue > 0 ? statement().payroll / revenue : 0;
    }

    /* ------------------------------------------------------------------ plan */

    /**
     * THE GENERIC MAKER'S RULE DECIDES WHAT, AND THE GROUND DECIDES WHETHER.
     *
     * Crops have a local buyer from the first month a city exists - the mills
     * are in the founding order and they have to eat - so unlike the two export
     * sectors there is a real demand series to plan against, and
     * BusinessInvestment.planMaker() reading the crop market is exactly right
     * about which farm and how many. It is not right about affordability, and
     * for this sector alone that is fatal.
     *
     * WHY THIS SECTOR ALONE. Every other building in the game is mostly a
     * BUILDING: a Steel Foundry is $30m of plant on nine tenths of a city
     * block, so what it pays for its lot is a rounding error and a planner that
     * scores profit over total cost gets the right answer without thinking
     * about the ground. A Mixed Farm is $1.19m of barn on twenty-four blocks.
     * At a young city's land price the ground is three times the building; by
     * the time the city is grown it is a hundred and twenty times, and the
     * sector borrows every cent of it.
     *
     * Measured without this gate, over 333 years: SIXTEEN restructures, $29.5m
     * owed at 10.29%, and $30.7m of assets left standing at the end. The engine
     * kept buying fields at development prices with money the fields could
     * never earn back, the lender kept funding it because the lender funds
     * anything with a positive score, and the sector spent three centuries
     * being refinanced.
     *
     * AND THE BRAKE FOR IT ALREADY EXISTED, WITH NOBODY CALLING IT.
     * BusinessInvestment.servicesItsOwnDebt() has been in the file since the
     * distress batch - "if the new capacity cannot out-earn the interest on the
     * money that built it, by a margin, the business declines the project even
     * though the lender would fund it" - and a search of the tree on 2026-09-13
     * found ZERO callers. It is the brake this sector needs and this is its
     * first one. Whether the other nine should be held to it as well is a real
     * question and a whole batch of its own; it is in claude/todo.md.
     */
    @Override
    public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game) {

        /* =================================================================
           NOBODY BREAKS GROUND ON A FIELD WHILE SOMEBODY IS SLEEPING OUTSIDE

           The one rule this sector needs that no other sector does, and it took
           sixteen seeds to see why. A farm is twenty-four to seventy-two city
           blocks, and the investment engine has no idea that the landlord wants
           the same ground: the fields plan off the crop market and the houses
           plan off the jobs, and neither can see the other. In a young city,
           which is exactly when farming is most profitable because the ground
           is cheapest, the fields win that race and the houses never get built.

           Measured over 333 years, seed 3: SIXTY-FOUR months with households
           who had nowhere at all, running from month 122 to month 2,777 - a
           city that never caught up. The same seed with the fields held out of
           the investment engine: four months, at the founding transient every
           build has. Eleven of sixteen seeds had an episode of some kind and
           the long ones were all this.

           So the fields yield. It is not a fudge and it is what land-use
           planning has done everywhere since it existed: ground inside a city
           that is short of housing is worth more as somewhere to live, and the
           city knows it because somebody is sleeping outside. The farm is not
           refused for ever - the moment the landlord has caught up, the fields
           can have what is left, which in a city with room is most of it.
           ================================================================= */
        if (game != null && game.getFamilies() != null
                && game.getFamilies().getStillUnplaced() > 0) {
            return BusinessInvestment.Decision.no(key(),
                    "the city is short of homes - ground is worth more to live on");
        }

        BusinessInvestment.Decision wanted = plans.planMaker(this, game);
        if (wanted == null || !wanted.build || wanted.template == null || game == null) {
            return wanted;
        }

        double cost = plans.getCostOf(wanted.template, Math.max(1, wanted.quantity));
        double rate = game.getEconomyManager().getBusinessDebtManager().getRate(key());
        double atFloor = worthAtTheFloor(wanted.template, plans);
        if (plans.servicesItsOwnDebt(atFloor, cost, rate)) return wanted;

        double ground = wanted.template.getLandSqFt()
                * Math.max(0, game.getEconomyManager().getLandPricePerSqFt());
        return BusinessInvestment.Decision.no(key(), String.format(
                "the ground under a %s costs %s and a bad year would leave %s a month on it",
                wanted.template.getName(), Formats.INSTANCE.cash(ground),
                Formats.INSTANCE.cash(Math.max(0, atFloor))));
    }

    /**
     * What a farm would clear in a month at the EXPORT FLOOR - the worst price
     * the world will ever hand it - rather than at whatever crops happen to
     * fetch the month somebody is deciding.
     *
     * NOBODY FINANCES A FARM ON A GOOD YEAR, and the first version of this gate
     * did. Measured over 333 years with the gate reading today's price: the city
     * built fields hard while crops were dear, reached a hundred percent of its
     * own eating by month 618, crashed the price onto its floor by doing so -
     * because a sector that can export its surplus always overshoots into one -
     * and then could not service the land it had borrowed against at the price
     * it had created. Fifteen restructures, and by month 1,074 there was not a
     * field left in the city.
     *
     * A crop is a price-taking commodity with a floor the world guarantees and
     * a ceiling the world imposes, and the only honest question to ask of a
     * quarter-century mortgage on twenty-four city blocks is whether it survives
     * the floor. Underwritten this way the sector stops building at about five
     * dollars a square foot of ground - and a Greenhouse Complex, which needs a
     * sixth of the land for three times the crop, goes on clearing the same test
     * at sixty.
     */
    private double worthAtTheFloor(ham.citybuildersim.BuildingsTemplate t, BusinessInvestment plans) {
        if (t == null || markets == null) return 0;
        /*
         * EVERY GOOD THE FARM MAKES, EACH AT ITS OWN FLOOR. It read CROPS alone
         * while that was all a farm made; a greenhouse making vegetables and
         * fruit would have been underwritten at zero and never built, and a
         * livestock farm likewise. The argument above is unchanged and is the
         * whole of Jerus's rule for this sector: value the harvest at the worst
         * price the world will ever pay for it, and if the mortgage still
         * clears, the city can afford to grow its own.
         */
        double atTheFloor = 0;
        for (Good g : goodsMade()) {
            double made = t.makes(g);
            if (made > 0) atTheFloor += made * Math.max(0, markets.get(g).netExportPrice());
        }
        return atTheFloor
                - plans.runningCostOf(t)
                - plans.standingCostOf(this, t);
    }

    /**
     * Half a farm's nameplate, a month, before the first one is sunk.
     *
     * The same rule the materials plant carries and for the same reason: a farm
     * is large against a young city's appetite - one covers about two thousand
     * people - and the founding city is four hundred. Measured without it, the
     * first probe city put a Mixed Farm up in month nine against a demand of
     * sixty tonnes, exported nine tenths of every harvest at the floor, and sold
     * the farm back inside three years.
     */
    public static final double FIRST_FARM_UTILISATION = .5;

    @Override
    public double firstPlantUtilisation() { return FIRST_FARM_UTILISATION; }

    /* ------------------------------------------------------------ the screen */

    @Override
    public List<Sector.Line> operations(Game game) {
        List<Line> lines = super.operations(game);
        if (game == null) return lines;
        Formats f = Formats.INSTANCE;

        /*
         * ANY of the five, not just crops - a city with nothing but greenhouses
         * grows a great deal and no crops at all, and this line told it it had
         * nothing under cultivation.
         */
        double growingAnything = 0;
        for (Good g : goodsMade()) growingAnything += getCapacity(g);
        if (growingAnything <= 0) {
            lines.add(Line.note("Nothing under cultivation. The mills are buying their crops "
                    + "from the world, which they can do for ever - but a field here is worth "
                    + "the whole gap between what the world charges and what a farm next door "
                    + "would take. What it costs is ground, and more of it than anything else "
                    + "the city builds."));
            return lines;
        }

        lines.add(Line.head("What the city grows for itself"));
        for (Good g : goodsMade()) {
            double made = getCapacity(g) * getOperatingRate();
            if (made > 0) lines.add(Line.of(g.label(), f.units(made, g)));
        }
        double share = getSelfSufficiency(game);
        lines.add(Line.of("Of what the city eats", f.pct(Math.min(1, share)),
                share >= .5 ? Line.Tone.GOOD : share >= .2 ? Line.Tone.NONE : Line.Tone.MUTED));
        lines.add(Line.of("Brought in a month", f.units(getHarvest(), Good.CROPS)));
        if (share > 1) {
            lines.add(Line.note("More than the city can eat. The surplus leaves at the export "
                    + "price, which is what a farming region does."));
        }

        lines.add(Line.head("What it costs in ground"));
        double acres = getLandSqFt() / 43560;
        lines.add(Line.of("Under cultivation", String.format("%,.0f acres", acres)));
        lines.add(Line.of("What that ground is worth",
                f.cash(getLandValue(game.getEconomyManager()))));
        double ground = groundShare(), pay = payrollShare();
        Line.Tone tone = ground <= 0 ? Line.Tone.MUTED
                : ground < .25 ? Line.Tone.GOOD
                : ground < .60 ? Line.Tone.WARN
                : Line.Tone.BAD;
        lines.add(Line.of("Land tax, as a share of what the fields sold", f.pct(ground), tone));
        lines.add(Line.of("Wages, as a share of the same", f.pct(pay)));
        if (ground >= .60) {
            lines.add(Line.note("The ground under these fields is taxed at more than half what "
                    + "they can grow on it. Without relief on farmland this is where the fields "
                    + "become suburbs - which is a real choice and not a fault, but it is a "
                    + "choice, and it is on the Policy screen."));
        } else if (ground >= .25) {
            lines.add(Line.note("Rising, and it will keep rising as long as the city does: the "
                    + "bill is struck on what the ground would fetch, not on what it grows."));
        }
        return lines;
    }

    /**
     * A price taker with a silo: it holds a harvest, sells what the mills want
     * and ships the rest, and shrinks only on distress. The generic rule would
     * measure it against the crop market, which is right - but a farm whose
     * customer imported one bad month is not a farm worth scrapping, and the
     * ground it sits on cannot be bought back.
     */
    @Override
    public double[] retirementDemandAndCapacity(Game game) {
        /*
         * MEASURED IN MONEY, because the five goods are counted in two units -
         * crops by the tonne, the rest by the kilogram - and adding them would
         * be adding tonnes to kilos. Each good's demand and capacity is valued
         * at its own export floor first, which is the same yardstick the
         * underwriting above uses.
         */
        double demand = 0, capacity = 0;
        for (Good g : goodsMade()) {
            double floor = markets == null ? 0 : Math.max(0, markets.get(g).netExportPrice());
            if (floor <= 0) continue;
            double trend = markets.get(g).getDemandTrend();
            demand   += Math.max(plannedDemand(g), trend) * floor;
            capacity += getCapacity(g) * floor;
        }
        return new double[] { demand, capacity };
    }
}
