package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Essential funding reservations, actual parcel cash and quoted bond terms (HW1).
 * WHY. A plan could count available cash twice, finance fractional parcels,
 * charge a reopened bond past maturity, or reuse an unrelated borrowing rate.
 * Each fixture causes that side of the rule and checks the real advice/pass.
 */
public final class WorksFinanceCheck {
    static final PrintStream out = System.out;
    static int fails;

    static void check(String words, boolean condition) {
        out.println(words + (condition ? " OK" : " FAIL"));
        if (!condition) fails++;
    }

    static boolean close(double a, double b) {
        return Math.abs(a - b) <= 1e-10 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static Game flat(Path root, String name) {
        Game g = EssentialWorksCheck.town(root, name);
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        long people = g.getPopulationManager().getPopulation();
        g.getBusinessInvestment().restorePopulationHistory(List.of(people, people));
        return g;
    }

    static double financing(Game g, double amount, double horizon) {
        if (!(amount > 0)) return 0;
        DebtQuote q = g.quoteLongBondForCash(amount, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE,
                g.getAutoBuilder().getCap());
        double rate = Math.max(0, BuildAdvice.lifeRate(g)) / 12;
        double discount = (1 + rate)
                * (1 + Math.max(-.99, g.getBusinessInvestment().getExpectedInflation()) / 12) - 1;
        int remaining = q.auction() == null ? q.duration() * 12 : q.auction().ask().months();
        double months = Math.min(horizon, remaining);
        double factor = Math.abs(discount) > 1e-12
                ? (1 - Math.pow(1 + discount, -months)) / discount : months;
        return (q.faceValue() - q.cashReceived() + q.monthlyInterest() * factor)
                * Math.max(1, amount / q.cashReceived());
    }

    static void parcelCash(Path root, boolean vault) {
        out.println("--- whole-parcel financing, vault=" + vault + " ---");
        Game g = flat(root, vault ? "finance-vault" : "finance-parcel");
        BuildingsTemplate wind = g.getBuildingManager().getTemplateByName("Wind Farm");
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt());
        g.setLandPaidFromVault(vault);
        if (vault) g.getForeignAccounts().buyReserves(1e12, "USD");
        g.setCashForTest(AutoBuilder.reserve(g));
        AutoBuilder.Ground ground = AutoBuilder.groundFor(g, wind, 1);
        double squareFeet = Math.max(0, AutoBuilder.lacks(g, wind, 1));
        double fractional = squareFeet * g.getLandManager().getOfficePricePerSqFt();
        check("fixture: available whole parcels cover a strictly smaller footprint",
                ground != null && ground.sqFt() > squareFeet && squareFeet > 0);
        if (ground == null) return;
        check("fixture: actual purchase cash differs from the fractional office estimate",
                vault ? ground.cash() == 0 && fractional > 0 : !close(ground.cash(), fractional));
        double cash = g.getCash(), debt = AutoBuilder.debt(g), reserves = g.getForeignAccounts().getReserves();
        WorksValue.Value value = WorksValue.of(g, power, Map.of(), wind, 1, 0);
        double invoice = g.quoteBuild(wind, 1).total + ground.cash();
        double expected = financing(g, invoice, value.lead() + BuildAdvice.LIFE_MONTHS);
        check("fixture: this flat one-wind comparison needs no later plant", value.futureUnits() == 0);
        check("financing uses the actual parcel cash and the vault's actual top-up",
                close(value.financing(), expected));
        check("valuation leaves cash, principal and the foreign vault untouched",
                g.getCash() == cash && AutoBuilder.debt(g) == debt && g.getForeignAccounts().getReserves() == reserves);
    }

    static void remainingCoupons(Path root) {
        out.println("--- reopened benchmark pays only its remaining coupons ---");
        Game g = flat(root, "finance-remaining");
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        g.getDebtManager().clearDebts();
        Debt benchmark = g.getDebtManager().addLongTermBond(100, Game.BUILD_BOND_YEARS * 12, g.getMonth(), .04);
        EssentialWorksCheck.quietly(() -> {
            for (int month = 0; month < 12; month++) benchmark.processMonth(g);
        });
        g.setCashForTest(AutoBuilder.reserve(g));
        BuildingsTemplate wind = g.getBuildingManager().getTemplateByName("Wind Farm");
        double invoice = g.quoteBuild(wind, 1).total;
        DebtQuote q = g.quoteLongBondForCash(invoice, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE,
                g.getAutoBuilder().getCap());
        check("fixture: a nonempty quote reopens the benchmark with twelve coupons already paid",
                q.cashReceived() > 0 && q.auction() != null
                        && q.auction().ask().months() == Game.BUILD_BOND_YEARS * 12 - 12);
        WorksValue.Value value = WorksValue.of(g, BuildAdvice.Measure.of(BuildAdvice.Kind.POWER),
                Map.of(), wind, 1, g.getLandManager().getAvailableSqFt());
        check("fixture: the flat comparison needs no later plant", value.futureUnits() == 0);
        check("the initial financing stops at the quoted benchmark maturity",
                close(value.financing(), financing(g, invoice, value.lead() + BuildAdvice.LIFE_MONTHS)));
    }

    static void laterQuotes(Path root) {
        out.println("--- cash-funded first work, capped borrowing for later additions ---");
        Game g = EssentialWorksCheck.town(root, "finance-later");
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        long people = g.getPopulationManager().getPopulation();
        var history = g.getHistorySave();
        history.getMonth().clear();
        history.getPopulation().clear();
        history.getMonth().add(0);
        history.getMonth().add(g.getMonth());
        history.getPopulation().add(people / 2);
        history.getPopulation().add(people);
        BuildingsTemplate clinic = g.getBuildingManager().getTemplateByName("Walk-in Clinic");
        BuildAdvice.Measure care = BuildAdvice.Measure.care(CareType.GENERAL);
        Set<BuildingsTemplate> skip = new HashSet<>(g.getBuildingManager().getTemplates());
        skip.remove(clinic);
        g.setCashForTest(1e14);
        BuildAdvice.Suggestion draft = BuildAdvice.suggestFor(g, null, care, g.getCash(),
                g.getLandManager().getAvailableSqFt(), g.getAutoBuilder().getSlack(), skip, true);
        check("fixture: the growing care need has an actual clinic advice plan", draft != null);
        if (draft == null) return;
        g.setCashForTest(AutoBuilder.reserve(g) + draft.price() + 1);
        BuildAdvice.Suggestion plan = BuildAdvice.suggestFor(g, null, care, g.getCash(),
                g.getLandManager().getAvailableSqFt(), g.getAutoBuilder().getSlack(), skip, true);
        check("fixture: cash covers the first plan and growth requires later units",
                plan != null && plan.price() <= g.getAutoBuilder().spendableForMeasure(g, care)
                        && plan.value().futureUnits() > 0 && plan.value().financing() > 0);
        if (plan == null) return;
        double[] oracle = AdviceValueOracle.value(g, plan, g.getLandManager().getAvailableSqFt(), g.getCash());
        check("later financed additions use their own capped quotes, independently recomputed",
                close(plan.planValue(), oracle[0]));
    }

    static AutoBuilder.OpenAsk ask(BuildAdvice.Suggestion plan, int month, double raised) {
        AutoBuilder.OpenAsk ask = new AutoBuilder.OpenAsk();
        ask.measure = plan.measure().label();
        ask.templateId = plan.template().getId();
        ask.building = plan.template().getName();
        ask.paving = plan.paving();
        ask.count = plan.count();
        ask.cost = plan.price();
        ask.raised = raised;
        ask.toRaise = Math.max(0, ask.cost - raised);
        ask.month = month;
        return ask;
    }

    static void reservationRelease(Path root) {
        reservationRelease(root, false);
    }

    static void reservationRelease(Path root, boolean fullyRaised) {
        out.println(fullyRaised ? "--- a fully raised cash-short ask cannot borrow its cost again ---"
                : "--- a higher-loss ask releases lower earmarks before an empty capped sale ---");
        Game g = flat(root, fullyRaised ? "finance-raised-reservations" : "finance-reservations");
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        g.setCashForTest(1e14);
        AutoBuilder auto = g.getAutoBuilder();
        UtilitiesHandler utility = g.getServicesManager().getUtilitiesHandler();
        utility.setWattsConsumption(utility.getProduction() * 4);
        auto.setDebtLimit(AutoBuilder.DEBT_LIMIT_MOST, null);
        auto.setCap(Auction.Cap.at(0), null);
        BuildAdvice.Measure streets = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        BuildAdvice.Suggestion high = BuildAdvice.suggestFor(g, null, streets, g.getCash(), g.getLandManager().getAvailableSqFt());
        BuildAdvice.Suggestion low = BuildAdvice.suggestFor(g, null, power, g.getCash(), g.getLandManager().getAvailableSqFt());
        check("fixture: streets have the higher current monetary loss and both works are proposed",
                high != null && low != null && WorksValue.shortageLoss(g, streets) > WorksValue.shortageLoss(g, power));
        if (high == null || low == null) return;
        double free = high.price() / .9;
        g.setCashForTest(AutoBuilder.reserve(g) + free);
        AutoBuilder.State state = auto.toState();
        state.on = true;
        state.asks = new ArrayList<>();
        state.asks.add(ask(high, g.getMonth(), fullyRaised ? high.price() : 0));
        state.asks.add(ask(low, g.getMonth(), .4 * free));
        auto.restore(state);
        if (fullyRaised) {
            auto.setCap(Auction.Cap.FILL, null);
            AuctionCheck.roomTo(g, 1e9);
        } else {
            g.getBank().setDepositRate(1);
            AuctionCheck.roomTo(g, 0);
            double[] foreign = g.getCapitalFlows().toSaveArray();
            foreign[1] = g.getMonth() + 12;
            foreign[6] = g.getMonth();
            g.getCapitalFlows().restore(foreign);
        }
        double needed = high.price() - auto.spendableForMeasure(g, streets);
        DebtQuote empty = AutoBuilder.bondQuote(g, needed, auto.getCap());
        out.printf("      free %.9f, unreserved %.9f, needed %.9f, raised by quote %.9f%n",
                free, AutoBuilder.spendable(g), needed, empty == null ? 0 : empty.cashReceived());
        if (fullyRaised) {
            AutoBuilder.OpenAsk raised = auto.askFor(streets);
            check("fixture: the higher ask has raised its full cost, cash held is short, and a new gap loan would be available",
                    raised != null && raised.raised() >= raised.cost() && raised.toRaise() == 0
                            && auto.held(g, raised) < raised.cost() && empty != null && empty.cashReceived() >= needed
                            && AutoBuilder.ratioAfter(g, empty) <= auto.getDebtLimit()
                            && close(auto.spendableForMeasure(g, streets), .6 * free));
        } else check("fixture: 100 above reserve, 40 lower-reserved, 90 higher-cost, and the gap's capped auction sells nothing",
                close(AutoBuilder.spendable(g), .6 * free) && close(high.price(), .9 * free)
                        && needed > 0 && (empty == null || empty.cashReceived() == 0));
        double debt = AutoBuilder.debt(g);
        AutoBuilder.OpenAsk originalAsk = auto.askFor(streets);
        double originalRaised = originalAsk.raised();
        EssentialWorksCheck.quietly(() -> auto.pass(g));
        AutoBuilder.Step order = EssentialWorksCheck.step(g, streets);
        check("the higher-loss service is actually ordered using the released cash",
                order != null && order.ordered() > 0);
        check("the lower funded reservation is released",
                auto.asks().stream().noneMatch(a -> a.measure().equals(power.label()) && a.raised() > 0));
        if (fullyRaised) check("the already funded higher-loss order sells no additional paper",
                order != null && order.ordered() > 0 && originalAsk.raised() == originalRaised);
        else check("releasing the reservation leaves outstanding debt intact", AutoBuilder.debt(g) == debt);
    }

    static void groundUse(String name) {
        out.println("--- financing uses the actual ground class for " + name + " ---");
        RunWorksCheck.quiet = EssentialWorksCheck.quiet;
        Game seed = RunWorksCheck.hills("finance-use-size", 0, .4, .3, .3, 0);
        BuildingsTemplate sized = seed.getBuildingManager().getTemplateByName(name);
        double owned = sized.getLandSqFt() / .04;
        Game g = RunWorksCheck.hills("finance-use-" + name.replace(' ', '-'), owned, .4, .3, .3, 0);
        LandManager land = g.getLandManager();
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        BuildingsTemplate house = g.getBuildingManager().getTemplateByName("House");
        double fill = .95 * (land.getFlatSqFt() + land.getFoothillSqFt()) - land.getAllocatedSqFt();
        boolean filled = land.allocate(fill, house.getSector(), LandManager.Use.BUILDING);
        double buildingFree = land.getAvailableSqFt();
        AutoBuilder.Ground ground = AutoBuilder.groundFor(g, t, 1);
        double needed = g.landNeededFor(t, 1);
        check("fixture: building-only room is short, while the order's actual use has enough owned ground",
                filled && buildingFree < needed
                        && land.freeForAnyone(Game.groundUse(t)) >= needed
                        && ground != null && ground.cash() == 0 && ground.offers().isEmpty());
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        g.getAutoBuilder().setCap(Auction.Cap.FILL, null);
        g.setCashForTest(AutoBuilder.reserve(g));
        BuildAdvice.Measure m = BuildCard.cityMeasure(t);
        WorksValue.Value value = WorksValue.of(g, m, Map.of(), t, 1, buildingFree);
        double invoice = g.quoteBuild(t, 1).total;
        DebtQuote q = g.quoteLongBondForCash(invoice, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE,
                g.getAutoBuilder().getCap());
        check("fixture: the flat-demand comparison borrows its build price and needs no later unit",
                value.futureUnits() == 0 && q.cashReceived() > 0 && land.getOfficePricePerSqFt() > 0);
        check("owned run or works ground adds no parcel borrowing",
                close(value.financing(), financing(g, invoice, value.lead() + BuildAdvice.LIFE_MONTHS)));
    }

    static void independentPending(Path root) {
        out.println("--- the independent oracle sizes its own pending electrical load ---");
        Game g = flat(root, "oracle-pending-load");
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate mill = bm.getTemplateByName("Large Mini-Mill");
        BuildingsTemplate wind = bm.getTemplateByName("Wind Farm");
        EssentialWorksCheck.quietly(() -> g.buildStack(mill, 1, false));
        BuildingsStacks stack = bm.getStack(mill);
        stack.setConstructionProgress(Math.max(0, mill.getConstructionPoints()
                - g.getBuildingOutputAtEveryPost() * .25));
        UtilitiesHandler utility = g.getServicesManager().getUtilitiesHandler();
        utility.setWattsConsumption(utility.getProduction() * 1.01);
        Set<BuildingsTemplate> skip = new HashSet<>(bm.getTemplates());
        skip.remove(wind);
        BuildAdvice.Suggestion plan = BuildAdvice.suggestFor(g, null,
                BuildAdvice.Measure.of(BuildAdvice.Kind.POWER), g.getCash(),
                g.getLandManager().getAvailableSqFt(), g.getAutoBuilder().getSlack(), skip, true);
        check("fixture: an operating power shortage has a wind proposal and a paid industrial load opens shortly",
                plan != null && plan.count() > 0 && stack.getUnderConstruction() == 1
                        && mill.getElectricityConsumption() > wind.getProduction1()
                        && bm.siteUnitMonths(mill, 1, g.getBuildingOutputAtEveryPost()) > 0
                        && bm.siteUnitMonths(mill, 1, g.getBuildingOutputAtEveryPost()) < BuildAdvice.HORIZON);
        if (plan == null) return;
        double[] oracle = AdviceValueOracle.value(g, plan, g.getLandManager().getAvailableSqFt(), g.getCash());
        check("the independent pending-load schedule and sizing reproduce the complete power plan",
                close(plan.planValue(), oracle[0]) && close(plan.value().used(), oracle[1]));
    }

    static double addedPoints(Game.BuildQuote quote, BuildingsTemplate t, int count) {
        return count * (double) t.getConstructionPoints()
                + quote.prepPoints + quote.worksPoints + quote.bridgePoints + quote.deepWaterPoints;
    }

    static void prospectiveQueue(Path root) {
        out.println("--- prospective unit completion matches the actually changed queue ---");
        for (int mode = 0; mode < 4; mode++) {
            Game g = flat(root, "prospective-queue-" + mode);
            g.setCashForTest(1e14);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                    + 1_000 * LandManager.SQ_FT_PER_KM2);
            BuildingManager bm = g.getBuildingManager();
            BuildingsTemplate jail = bm.getTemplateByName("Jail"), road = bm.getTemplateByName("Paved Road");
            bm.setBuildersFirstForTest(mode == 3);
            bm.setUnburied(() -> 0);
            EssentialWorksCheck.quietly(() -> g.buildStack(jail, 1, false));
            if (mode == 1) bm.getControl().setOrder(List.of(ConstructionControl.keyOf(road), ConstructionControl.keyOf(jail)));
            if (mode == 2) bm.getControl().setRush(ConstructionControl.keyOf(jail), true);
            if (mode == 3) {
                bm.setBuildersCouldStaff((template, count) -> true);
                EssentialWorksCheck.quietly(() -> g.buildStack(bm.getTemplateByName("Construction Depot"), 1, false));
            }
            double output = g.getBuildingOutputAtEveryPost();
            Game.BuildQuote quote = g.quoteBuild(road, 4);
            double cash = g.getCash(), debt = AutoBuilder.debt(g);
            int sites = bm.getUnderConstructionById()[road.getId()];
            double predicted = bm.siteUnitMonthsAfter(jail, 1, output, road, 4, addedPoints(quote, road, 4));
            check("prospective quote places no order or cash flow, mode " + mode,
                    g.getCash() == cash && AutoBuilder.debt(g) == debt
                            && bm.getUnderConstructionById()[road.getId()] == sites);
            EssentialWorksCheck.quietly(() -> g.buildStack(road, 4, false));
            double actual = bm.siteUnitMonths(jail, 1, output);
            check("the quoted first unit matches the actual queue after placement, mode " + mode,
                    Double.isFinite(predicted) && predicted > 0 && close(predicted, actual));
        }
    }

    static void crewPromise(Path root, boolean partial) throws Exception {
        out.println("--- reserve expansion preserves an unserved work's promise, partial=" + partial + " ---");
        BuildAdviceCheck.quiet = EssentialWorksCheck.quiet;
        Game g = BuildAdviceCheck.servedCity(root, partial ? "crew-partial" : "crew-held");
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        long people = g.getPopulationManager().getPopulation();
        g.getBusinessInvestment().restorePopulationHistory(List.of(people, people));
        BuildingManager bm = g.getBuildingManager();
        bm.setBuildersFirstForTest(false);
        bm.setUnburied(() -> 0);
        AutoBuilder auto = g.getAutoBuilder();
        auto.setSlack(.50, null);
        BuildAdvice.Measure care = BuildAdvice.Measure.care(CareType.GENERAL);
        BuildAdvice.Measure streets = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
        for (BuildingsTemplate t : bm.getTemplates()) if (care.serves(t) && bm.getQuantity(t.getId()) > 0)
            EssentialWorksCheck.quietly(() -> bm.retire(t, bm.getQuantity(t.getId())));
        BuildingsTemplate centre = bm.getTemplateByName("Community Health Centre");
        int required = BuildAdvice.count(g, care, Map.of(), Map.of(centre, 1), auto.target(), BuildAdvice.MOST, true)[0];
        EssentialWorksCheck.quietly(() -> g.buildStack(centre, required, false));
        // One street candidate isolates a reserve order which cannot escape through a different crew weight.
        List<BuildingsTemplate> catalogue = new ArrayList<>(bm.getTemplates());
        BuildingsTemplate road = bm.getTemplateByName("Paved Road");
        InfrastructureManager roads = g.getInfrastructureManager();
        roads.setModes(0, 0);
        roads.setThrough(0, g.getLandManager().getAllocatedSqFt(), 0);
        roads.setBuiltCapacity(20.0 * road.getCapacity());
        roads.setLoad(roads.getStreetCapacity() / 1.01);
        double[] traffic = new double[Traffic.values().length];
        traffic[Traffic.COMMUTERS.ordinal()] = roads.getLoad();
        roads.setBreakdown(traffic);
        roads.setBuiltCapacity(roads.getStreetLoad() * 1.01 - InfrastructureManager.BASE_CAPACITY);
        bm.getTemplates().removeIf(t -> t.getCategory() == BuildingType.INFRASTRUCTURE && !t.isHighway() && t != road);
        try {
            BuildAdvice.Suggestion reserve = BuildAdvice.suggestFor(g, null, streets, g.getCash(),
                    g.getLandManager().getAvailableSqFt());
            double output = g.getBuildingOutputAtEveryPost();
            double initial = bm.siteUnitMonths(centre, 1, output);
            out.printf("      required %d, care warranted %b now %b pending %b, streets now %b, reserve %s, lead %.9f%n",
                    required, BuildAdvice.warranted(g, care), BuildAdvice.maintained(g, care, Map.of(), BuildAdvice.NOW),
                    BuildAdvice.maintained(g, care, Map.of(centre, required), auto.target()),
                    BuildAdvice.maintained(g, streets, Map.of(), BuildAdvice.NOW),
                    reserve == null ? "none" : reserve.count() + " " + reserve.template().getName(), initial);
            check("fixture: one fully covered pending care plan, unserved now, and a multiple-road reserve proposal",
                    required > 0 && BuildAdvice.warranted(g, care)
                            && !BuildAdvice.maintained(g, care, Map.of(), BuildAdvice.NOW)
                            && BuildAdvice.maintained(g, care, Map.of(centre, required), auto.target())
                            && BuildAdvice.maintained(g, streets, Map.of(), BuildAdvice.NOW)
                            && reserve != null && reserve.count() > 1 && Double.isFinite(initial) && initial > 1);
            if (reserve == null || !(initial > 1)) return;
            double one = bm.siteUnitMonthsAfter(centre, 1, output, road, 1,
                    addedPoints(g.quoteBuild(road, 1), road, 1)) / initial;
            double all = bm.siteUnitMonthsAfter(centre, 1, output, road, reserve.count(),
                    addedPoints(g.quoteBuild(road, reserve.count()), road, reserve.count())) / initial;
            double goal = partial ? (1 / one + 1 / all) / 2 : 1 - 1e-6;
            BuildingsStacks site = bm.getStack(centre);
            site.setConstructionProgress(centre.getConstructionPoints()
                    - (centre.getConstructionPoints() - site.getConstructionProgress()) * goal / initial);
            double before = bm.siteUnitMonths(centre, 1, output);
            double delayed = bm.siteUnitMonthsAfter(centre, 1, output, road, reserve.count(),
                    addedPoints(g.quoteBuild(road, reserve.count()), road, reserve.count()));
            AutoBuilder.State state = auto.toState();
            state.on = true;
            state.pendingUntil.put(care.label(), g.getMonth() + Math.ceil(before));
            auto.restore(state);
            double due = state.pendingUntil.get(care.label());
            check("fixture: the full reserve plan would miss the saved completion, with the intended one-unit headroom",
                    before > 0 && before < 1 && g.getMonth() + Math.ceil(delayed - 1e-9) > due
                            && (bm.siteUnitMonthsAfter(centre, 1, output, road, 1,
                                addedPoints(g.quoteBuild(road, 1), road, 1)) <= 1) == partial);
            EssentialWorksCheck.quietly(() -> auto.pass(g));
            AutoBuilder.Step step = EssentialWorksCheck.step(g, streets);
            double after = bm.siteUnitMonths(centre, 1, output);
            check("the reserve order respects the saved essential completion",
                    step != null && step.cut() == AutoBuilder.Cut.BUILDERS
                            && (partial ? step.ordered() > 0 && step.ordered() < reserve.count() : step.ordered() == 0)
                            && g.getMonth() + Math.ceil(after - 1e-9) <= due);
            String reason = auto.heldBackWords(reserve);
            check("the actual card explains the construction-capacity limit", reason != null);
            boolean[] saved = {false};
            EssentialWorksCheck.quietly(() -> saved[0] = g.saveGame(10, "HW1 crew promise").ok);
            Game loaded = new Game(g.getGameFiles());
            EssentialWorksCheck.quietly(() -> loaded.loadGameSave(10));
            check("a real reload keeps the construction-capacity reason",
                    saved[0] && reason != null && reason.equals(loaded.getAutoBuilder().heldBackWords(reserve)));
            auto.setOn(false, null);
            loaded.getAutoBuilder().setOn(false, null);
            check("switching off clears both construction-capacity reasons",
                    auto.heldBackWords(reserve) == null && loaded.getAutoBuilder().heldBackWords(reserve) == null);
        } finally {
            bm.getTemplates().clear();
            bm.getTemplates().addAll(catalogue);
        }
    }

    public static void main(String[] args) throws Exception {
        java.util.Locale.setDefault(java.util.Locale.CANADA);
        Path root = TempDirs.create("worksfinancecheck");
        reservationRelease(root);
        reservationRelease(root, true);
        groundUse("Elevated Highway");
        groundUse("Hydro Station");
        independentPending(root);
        prospectiveQueue(root);
        crewPromise(root, false);
        crewPromise(root, true);
        parcelCash(root, false);
        parcelCash(root, true);
        remainingCoupons(root);
        laterQuotes(root);
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
