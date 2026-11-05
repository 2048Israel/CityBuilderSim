
package ham.citybuildersim;

/**
 * The order the month runs in: the sites advance, the roads, the posts and
 * the wages, the people, then the economy and the services.
 *
 * @author Jerus
 */
public class SimulationEngine {

    private EconomyManager economyManager;
    private PopulationManager populationManager;
    private ServicesManager servicesManager;
    private BuildingManager buildingManager;
    private DebtManager debtManager;

    public SimulationEngine(
            EconomyManager economyManager,
            PopulationManager populationManager,
            ServicesManager servicesManager,
            BuildingManager buildingManager,
            DebtManager debtManager) {

        this.economyManager = economyManager;
        this.populationManager = populationManager;
        this.servicesManager = servicesManager;
        this.buildingManager = buildingManager;
        this.debtManager = debtManager;
    }

    public void simulateMonth(Game game) {

        /*
         * The month's build rate: capacity, discounted for staffing and for
         * congestion.
         *
         * Asked of Game rather than worked out here, because the same figure is
         * also what the screen shows and what construction is PAID for, a few
         * lines below (recogniseSiteWork()). This used to be a second copy of
         * the arithmetic and the two drifted the moment roads were added - the
         * sites crawled while the sector still billed for a full month. One
         * definition now.
         *
         * Both discounts are a month stale, deliberately. The fill rate is
         * whatever the construction sector last computed, since this month's
         * wages have not been refreshed yet, and the road ratio is the network as
         * it stood while the crews were working. Reordering to make either
         * current would change *when* a newly finished building starts counting
         * toward population and the economy, which is a much larger behavioural
         * change than the discount itself.
         *
         * Congestion slowing the site is the pointed part: a jammed city builds
         * the roads that would unjam it more slowly. MIN_THROUGHPUT is what
         * keeps that from being a trap - it always digs itself out eventually,
         * slowly, at a real cost in months.
         */
        /*
         * getBuildingOutput(), not getConstructionOutput(): the second is what
         * the sector can do in a month, the first is what is left for the
         * SITES once the standing housing stock has had its repairs. Same
         * figure recogniseWork() is paid on, a few lines below, so the sites
         * advance by exactly the work the sector is paid for. See
         * Game.getBuildingOutput().
         */
        int siteOutput = game.getBuildingOutput();
        // The foreign contractor's month beside the city's own (FC1): nothing unless the repairs took the
        // builders' points and left the sites none - see Game, THE FOREIGN CREWS.
        Game.ForeignOffer foreign = game.foreignCrewsOffer();
        // His points are in the month the sites take, and not on a rushed site's overtime (FC1b): his crews are not the city's.
        java.util.List<BuildingManager.Completion> opened = buildingManager.advanceConstruction(siteOutput + foreign.points(), foreign.points());
        game.recordCompletions(opened);
        // ...and what the buildings that opened come with (R1b): in a city founded the 1900 way, only the equipment that pays for a new building;
        // nothing in a city founded the default way, where every building opens fitted.
        game.fitWhatFinished(opened);
        /*
         * ...AND THE 1900 FORMS AT REST (R1b; R1's stranger's review, 2026-11-05): the shares the counts set, said again now that the month has
         * changed the counts' weights. The top of the month said them (Game.policyHearsTheMonth()) before the investment pass scrapped anything
         * and before the sites finished a building, so a building that finished or was scrapped on a stack with buildings unfitted moved its
         * sector's unfitted share after that, and a city rested in the gap between two presses in the month's TOP shares while a save written
         * there, read back, sets them from the month's END counts (the load path). The twins then parted on the next press: its first
         * statement, the auto-builder's pass, reads the shares. It follows the last change the month makes to a stack's quantity or its counts
         * (the scrapping is above, in the investment pass; the sites, and what opened on them, just now), and it is here and not at the end of
         * Game.nextMonth() because everything the month derives from the buildings and the shares
         * (the road network, the posts, the sectors' grid draw, the city's power demand and the ratio it gives) is derived below, and the load
         * re-derives all of it from the counts at rest (rebuildSimulationState()): said at the end of Game.nextMonth() instead, the unbroken
         * city kept the power demand struck at the top's shares and the loaded one the demand at the end's (2,734 against 2,748 units in
         * FittingCheck 1's town), and the next press parted, the treasury within two months. A city with no building unfitted that never
         * had one calls nothing.
         */
        game.getEquipmentForms().apply(game);
        // ...and the material that work drew on, bought now, and the work
        // itself recognised on the same figure the sites advanced by. The
        // plant's sale, the builders' purchase and the builders' revenue are
        // all booked into this month's ledger and struck at the top of the
        // next. See Game.drawSiteMaterials() and recogniseSiteWork(). The
        // points the sites actually took beside the points they were offered
        // (0.7.17): what no site could use was idle, not banked.
        game.drawSiteMaterials(buildingManager.takeMaterialsDue());
        // The site output the crews had: what the sites were left after the
        // repairs, and - on the city's rushed sites (0.7.22) - the hours
        // they worked over it, or the hours a tired crew lost.
        double overtime = buildingManager.getOvertimePoints();
        double ownMonth = overtime == 0 ? siteOutput : siteOutput + overtime;
        // ...and the contractor is paid for what the sites took past the city's own month (FC1).
        game.settleForeignCrews(foreign, buildingManager.getPointsBuilt() - Math.max(0, ownMonth));
        game.recogniseSiteWork(buildingManager.takeRevenueDue(), buildingManager.getPointsBuilt(),
                ownMonth + foreign.points());
        // ...and each owner pays the material its work drew at the price it
        // was drawn at, less what its quote allowed (0.7.19): the escalation
        // clause, settled on the same month's work. See Game, MATERIAL AT THE
        // PRICE WHEN IT IS USED.
        game.settleSiteContracts(buildingManager.takeContractsDue());
        // ...and what the player's hand on the queue left (0.7.22): the
        // overtime on the city's rushed sites paid and paid out as wages, a
        // cancelled order's refund, and a finished demolition's material and
        // ground. See Game, THE PLAYER'S HAND ON THE QUEUE.
        game.settleConstructionControl(buildingManager.takeControlEvents());

        // Roads, before anything reads them. Capacity and load are both pure
        // functions of what is standing, and what is standing just changed:
        // the shops that opened this morning trade this month, are counted for
        // population and jobs this month, and put their trucks on the road this
        // month too. Recomputing here rather than waiting for updateServices()
        // is also what lets a reloaded save agree with the game it was saved
        // from - see ServicesManager.updateInfrastructure().
        servicesManager.updateInfrastructure();

        // Recount the posts and refresh the wage arrays. This NO LONGER decides
        // how many people live here - it used to, and the comment that lived
        // here warned that routing it through refreshDerivedState() would freeze
        // every city at zero residents. That trap has moved: the population step
        // is now advanceDemographics(), immediately below.
        updatePopulation(game);

        /*
         * THE POPULATION STEP.
         *
         * It sits AFTER the jobs and wages are refreshed because it reads both -
         * the job count is what the city is pulling against, and the per-tier
         * wage bill is what the twelve-month decline test is filed from. It sits
         * BEFORE the economy because everything there keys off the population it
         * produces.
         *
         * This ordering was the reverse until the switch, when demographics were
         * a placeholder that consumed the month's population and fed nothing.
         * The comment that used to sit here said it "must never be a source" for
         * the population. It is now the only source.
         */
        game.advanceDemographics();

        updateEconomy(game);
        updateServices(game);
    }

    public void updatePopulation(Game game) {
        // The builders strike their crews before the posts are counted
        // (0.7.17): the work ahead says how many of their posts they offer,
        // and the labour market fills what is offered. See
        // sectors.Construction, THE CREWS THE WORK NEEDS.
        game.strikeBuildersCrews();
        game.updatePopulation();
        populationManager.updateJobs(game.getJobs());
        // Between these two on purpose: the market prices against this month's
        // posts, and the wage bill below multiplies by the price it sets.
        game.repriceLabour();
        populationManager.UpdateTotalWagePerType();
    }
    
    public void updateEconomy(Game game) {
        //TBE
        
        economyManager.setTotalJobs(populationManager.getTotalJobs());
        economyManager.setPopulation(populationManager.getPopulation());
        economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());
        economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());

        // The two inputs to the rent price, which is a market now. Set here
        // beside the other housing figures and mirrored in
        // Game.rebuildSimulationState(), or a reloaded city prices its rent off
        // a different shortage from the live one for a month.
        economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());
        // ...and the same count split into the two segments the rent market
        // now prices separately. See FamilyModel's TWO SEGMENTS.
        economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),
                                         game.getFamilies().familySeekers(),
                                         game.getFamilies().studioSeekerHeads(),
                                         game.getFamilies().familySeekerHeads());
        economyManager.setMarginalHousingCost(game.marginalHousingCost());

        economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)
                + game.getCohorts().get(AgeBand.ELDER));
        // The fill first: every sector's payroll is discounted by it, so it
        // has to be current before the wages are set. One call for all seven
        // sectors since the sector template (2026-09-11); the bank's posts
        // ride along because it is the one payroll outside the registry.
        economyManager.updateJobFillRate(populationManager.getJobFillRate());
        economyManager.updateWages(
                populationManager.getWagesPerType(),
                buildingManager.getJobArrayByName("Commercial Bank"));
        economyManager.setTotalWage(populationManager.getTotalWage());

        // The split behind that total. The wage tax is banded, so a single
        // figure cannot be charged at four different rates - and this line has
        // to sit beside setTotalWage() in BOTH places that sync wages, the
        // monthly path here and rebuildSimulationState() on load, or the two
        // disagree and a reloaded city taxes a different wage bill.
        economyManager.setWageDetail(populationManager.getStaffedWagePerType());
        economyManager.setEnergyRatio(servicesManager.getEnergyRatio());
        economyManager.setWaterRatio(servicesManager.getWaterRatio());
        /*
         * THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD
         * PATH. This line read servicesManager.getRoadRatio() - one number for
         * everybody - while Game.rebuildSimulationState() already handed every
         * sector its own mix-weighted exposure, so the split streams were doing
         * nothing at all in a running game and appeared for the first time when
         * a save was loaded. That is precisely the class of bug this file's own
         * comments warn about three lines further down, and it was mine, from
         * the batch that added the streams. See EconomyManager.setRoadRatio.
         *
         * A city with no highway, no transit and no rail reads the same number
         * it always read, to the bit - InfrastructureManager.throughputFor()
         * returns the plain ratio unblended when there is nothing to blend.
         */
        economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);
        // The fourth ratio. advanceDemographics() has already set it for this
        // month - see Game.advanceDemographics step 6.
        economyManager.setHealthRatio(game.getHealth().getWorkRatio());
        economyManager.updateEcon();
        
         
    }
    
    public void updateServices(Game game){
        servicesManager.updateServiceWages(populationManager.getWagesPerType());
        servicesManager.updateJobFillRate(populationManager.getJobFillRate());
        // must precede updateServices(): the residents' draw is part of the
        // water demand the ratio is computed against
        servicesManager.setPopulation(populationManager.getPopulation());
        servicesManager.updateServices();
    }

}
