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
 * Since FS15 every city is a founding, founded(): its works stood by buildStack,
 * its people placed at the cohorts' equilibrium, two months struck for books and
 * loads, then its people, trend and year of GDP set again by hand. Until then the
 * sections read towns played 24 and 36 months, whose builders, growth and GDP moved
 * with every change to the month.
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

    /** ChildcareCheck.town()'s works, the town once (k 1), which flat() and laterQuotes() stand on a founding (FS15). */
    static final String[][] TOWN_WORKS = {
            { "Industrial Bakery", "4" }, { "Coal Power Plant", "1" }, { "Water Treatment Plant", "1" },
            { "Commercial Bank", "1" }, { "Convenience Store", "10" }, { "House", "900" },
            { "Elementary School", "1" }, { "Middle School", "1" }, { "High School", "1" },
            { "Community College", "1" }, { "University", "1" }, { "Medical School", "4" },
            { "Construction Depot", "5" }, { "Paved Road", "1" }, { "Police Station", "2" },
            { "Home Care Service", "2" } };

    /** BuildAdviceCheck.servedCity()'s works, which reserveTown() and crewPromise() stand on a founding (FS15). */
    static final String[][] SERVED_WORKS = {
            { "Industrial Bakery", "2" }, { "Coal Power Plant", "1" }, { "Water Treatment Plant", "1" },
            { "Commercial Bank", "1" }, { "Convenience Store", "4" }, { "House", "300" },
            { "Construction Depot", "2" }, { "Paved Road", "4" },
            { "Community Health Centre", "1" }, { "Childcare Centre", "2" }, { "Home Care Service", "2" },
            { "Elementary School", "1" }, { "Middle School", "1" }, { "High School", "1" },
            { "Community College", "1" }, { "University", "1" }, { "Medical School", "1" },
            { "Police Station", "1" } };

    /** The months founded() strikes on the state it set: the founding's first, which barely trades, and the one after. */
    static final int STRUCK_MONTHS = 2;

    /** growingCalendarChoice()'s year: its people grew from this share of today's (FS15: .9 on the served city played two years, whose builders took longer: a founding's Paved Road opens in 7.5 months, so the city grows faster for the road to have a plan). */
    static final double GROWN_FROM = .75;

    /** The share of its homes' places founded()'s people fill. */
    static final double PEOPLE_PER_PLACE = .75;

    /**
     * FS15: the city a section's premise is set on, caused. A founded scratch city with `works` stood by buildStack on the
     * founders' cash and ground, every sector held; its people placed by hand - PEOPLE_PER_PLACE of its
     * homes' places, at a settled pyramid (PopulationCohorts.equilibriumShare()), their workforce, and a flat history in
     * both the businesses' trend and the city's - STRUCK_MONTHS struck on that state for its books, put back after them,
     * and the founders' cash. Until FS15 flat() and laterQuotes() read
     * EssentialWorksCheck.town() (ChildcareCheck.town() played three years) and reserveTown() and crewPromise()
     * BuildAdviceCheck.servedCity() (played two years): whatever those towns' months made of them.
     */
    static Game founded(Path root, String name, String[][] works) {
        Game g = new Game(new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy")));
        EssentialWorksCheck.quietly(() -> {
            g.run();
            // PW2: these towns' power plans weigh a wind proposal, and a 1900 city knows the wind farm from its year; its research found it first.
            EssentialWorksCheck.knowWind(g);
            g.getEconomyManager().getTaxPolicy().reset();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 80_000_000L);
            for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
            BuildingManager bm = g.getBuildingManager();
            for (String[] w : works) g.buildStack(bm.getTemplateByName(w[0]), Integer.parseInt(w[1]), true);
            long people = Math.round(PEOPLE_PER_PLACE * bm.getTotalHouseCapacity());
            place(g, people);
            // Two months struck on the state the fixture set, for what only a month writes: the sectors' books, the
            // streets' load, the builders' output. A founding's first month barely trades (its shops have no stock: a GDP
            // of 5.6 against the second's 129), so the second is the month read. Then the people put back, the history
            // flat, and the national accounts' year that month twelve times over (NationalAccounts.seedHistory(), the
            // load path's setter), so a debt is weighed against a year's output (AutoBuilder.annualGdp()).
            g.simulateMonths(STRUCK_MONTHS);
            place(g, people);
            NationalAccounts accounts = g.getEconomyManager().getNationalAccounts();
            double month = accounts.getHistory().get(accounts.getHistory().size() - 1);
            accounts.seedHistory(java.util.Collections.nCopies(12, month));
            g.getHistorySave().getPopulation().clear();
            g.getHistorySave().getMonth().clear();
            g.getBusinessInvestment().restorePopulationHistory(List.of(people, people));
            g.setCashForTest(Founding.WEALTHY_CASH);
        });
        return g;
    }

    /** `people` placed in the city: a settled pyramid (PopulationCohorts.equilibriumShare()), the population and its workforce. */
    static void place(Game g, long people) {
        double[] bands = new double[AgeBand.values().length + 2];   // the bands, then the month's births and deaths (none)
        for (AgeBand b : AgeBand.values()) bands[b.ordinal()] = people * PopulationCohorts.equilibriumShare(b);
        g.getCohorts().restore(PopulationCohorts.saveBands(), bands);
        g.getPopulationManager().setPopulation(people);
        g.getPopulationManager().recomputeWorkforce();
    }

    /** EssentialWorksCheck.town()'s terms on a caused founding: two years of GDP borrowed, the builder may not borrow more, and builds from cash anyway. */
    static Game town(Path root, String name) {
        Game g = founded(root, name, TOWN_WORKS);
        EssentialWorksCheck.quietly(() -> g.handleLongBondForCash(2 * AutoBuilder.annualGdp(g),
                Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE));
        g.getAutoBuilder().setDebtLimit(0, null);
        g.getAutoBuilder().setCashAnyway(true, null);
        return g;
    }

    static Game flat(Path root, String name) {
        return town(root, name);
    }

    static double financing(Game g, double amount, double horizon) {
        if (!(amount > 0)) return 0;
        DebtQuote q = g.quoteLongBondForCash(amount, Game.BUILD_BOND_YEARS, g.fundingLot("Term"),
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
        double expected = financing(g, invoice, BuildAdvice.LIFE_MONTHS);
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
        DebtQuote q = g.quoteLongBondForCash(invoice, Game.BUILD_BOND_YEARS, g.fundingLot("Term"),
                g.getAutoBuilder().getCap());
        check("fixture: a nonempty quote reopens the benchmark with twelve coupons already paid",
                q.cashReceived() > 0 && q.auction() != null
                        && q.auction().ask().months() == Game.BUILD_BOND_YEARS * 12 - 12);
        WorksValue.Value value = WorksValue.of(g, BuildAdvice.Measure.of(BuildAdvice.Kind.POWER),
                Map.of(), wind, 1, g.getLandManager().getAvailableSqFt());
        check("fixture: the flat comparison needs no later plant", value.futureUnits() == 0);
        check("the initial financing stops at the quoted benchmark maturity",
                close(value.financing(), financing(g, invoice, BuildAdvice.LIFE_MONTHS)));
    }

    static void laterQuotes(Path root) {
        out.println("--- cash-funded first work, capped borrowing for later additions ---");
        Game g = town(root, "finance-later");
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

    /** Hand-priced parcels: each has only one and a half orders of reusable ground. */
    static void carriedGround(Path root, boolean zoned) throws Exception {
        carriedGround(root, zoned, false);
    }

    static void carriedGround(Path root, boolean zoned, boolean unlinkedForest) throws Exception {
        out.println("--- projected parcels retain usable ground, zoned=" + zoned + " ---");
        Game g = new Game(new GameFiles(root.resolve("carried-ground-" + zoned), root.resolve("no-legacy-" + zoned)));
        EssentialWorksCheck.quietly(g::run);
        LandManager land = g.getLandManager();
        land.setOwnedSqFt(land.getAllocatedSqFt());
        g.getCityLand().setCentreRelief(new double[] {LandManager.km2(land.getOwnedSqFt()), 0, 0, 0, 0});
        land.restoreWorks(0);
        land.restoreRuns(0);
        double area = 10_000;
        if (zoned) {
            land.getZoning().set(Sectors.MANUFACTURING, Zoning.Size.SHARE, .25, 1);
            land.getZoning().addUsed(Sectors.MANUFACTURING, .25 * land.getOwnedSqFt());
        }
        // In the zoning case the last quarter of each dry parcel is held for industry.
        // Otherwise that quarter is steep too. Flat + foothill remain usable for a clinic.
        if (unlinkedForest) land.getZoning().setForestLaw(0, 1);
        double dryOrders = unlinkedForest ? 6 : 4;
        double dry = LandManager.km2(dryOrders * area);
        List<LandParcel> offers = new ArrayList<>();
        for (int id = 1; id <= 3; id++) {
            double[] areas = LandClassCheck.areas(dry, 0, 0);
            areas[CityLand.FOREST] = unlinkedForest ? LandManager.km2(2 * area) : 0;
            offers.add(new LandParcel(id, 0, id - 1, 0, 0, 0, 0, 0, areas,
                    new double[] {LandManager.km2((unlinkedForest ? 3 : 1) * area),
                            LandManager.km2((zoned ? 1.5 : .5) * area),
                            LandManager.km2((zoned ? 1.5 : 2.5) * area), 0, 0},
                    new int[CityLand.KINDS], new double[CityLand.KINDS], id * 100, 0));
        }
        land.getMarket().restoreOffers(offers.stream().map(LandParcel::offerRow).toArray(double[][]::new),
                offers.stream().map(LandParcel::reliefRow).toArray(double[][]::new), 4);
        if (unlinkedForest) for (LandParcel p : offers) land.getMarket().find(p.getId()).setUnlinked(
                LandManager.km2(2 * area), LandManager.km2(2 * area), LandManager.km2(2 * area), 2);
        BuildingsTemplate clinic = g.getBuildingManager().getTemplateByName("Walk-in Clinic");
        check("fixture: no clinic ground is free, and each listed parcel has " + dryOrders + " orders of dry ground but only 1.5 usable",
                close(land.freeForAnyone(Game.groundUse(clinic)), 0)
                        && offers.stream().allMatch(p -> {
                            LandParcel listed = land.getMarket().find(p.getId());
                            double linked = listed.getSizeSqFt() - LandManager.sqFt(listed.getUnlinkedDryKm2());
                            double usable = LandManager.sqFt(listed.getReliefKm2(CityLand.RELIEF_FLAT)
                                    + listed.getReliefKm2(CityLand.RELIEF_FOOTHILL) - listed.getUnlinkedBuildableKm2());
                            if (zoned) usable -= .25 * linked;
                            return close(listed.getSizeSqFt(), dryOrders * area) && close(usable, 1.5 * area);
                        }));
        double rate = g.getForeignAccounts().getRate();
        for (Class<?> type : new Class<?>[] {WorksValue.class, AdviceValueOracle.class}) {
            Class<?> ground = Class.forName(type.getName() + (type == WorksValue.class ? "$GroundPlan" : "$Ground"));
            var constructor = ground.getDeclaredConstructor(Game.class, BuildingsTemplate.class, double.class);
            constructor.setAccessible(true);
            Object projection = constructor.newInstance(g, clinic, 0);
            var take = ground.getDeclaredMethod(type == WorksValue.class ? "take" : "use", double.class);
            take.setAccessible(true);
            double first = (double) take.invoke(projection, area);
            double second = (double) take.invoke(projection, area);
            double third = (double) take.invoke(projection, area);
            double fourth = (double) take.invoke(projection, area);
            check(type.getSimpleName() + ": the first parcel is bought whole", close(first, 100 * rate));
            check(type.getSimpleName() + ": the next addition buys a second parcel instead of reusing steep or zoned ground",
                    close(second, 200 * rate));
            check(type.getSimpleName() + ": the third addition reuses exactly the usable remainder", close(third, 0));
            check(type.getSimpleName() + ": exhaustion buys the third parcel at its whole price", close(fourth, 300 * rate));
            // PW3 (Jerus, 2026-11-23: "you just buy more land"): a plan the listing cannot cover is assembled offer by offer
            // (AutoBuilder.ASSEMBLE_MOST); unavailable is a plan past that many offers of the listing's size.
            double offer = offers.stream().mapToDouble(o -> {
                LandParcel listed = land.getMarket().find(o.getId());
                return listed.getSizeSqFt() - LandManager.sqFt(listed.getUnlinkedDryKm2());
            }).min().orElse(0);
            double past = (AutoBuilder.ASSEMBLE_MOST + 1) * offer;
            Object unavailable = constructor.newInstance(g, clinic, 0);
            double estimate = (double) take.invoke(unavailable, past);
            double smaller = (double) take.invoke(unavailable, area);
            check(type.getSimpleName() + ": a plan past ASSEMBLE_MOST offers is unavailable: the office estimate, the shelf not taken",
                    close(estimate, past * land.getOfficePricePerSqFt()));
            check(type.getSimpleName() + ": a later smaller addition still buys the first parcel's usable ground once",
                    close(smaller, 100 * rate));
            Object assembled = constructor.newInstance(g, clinic, 0);
            double large = (double) take.invoke(assembled, 20 * area);
            check(type.getSimpleName() + ": a large plan within ASSEMBLE_MOST buys the whole shelf and prices what it still lacks at the office's rate",
                    20 * area > 3 * offer && 20 * area <= AutoBuilder.ASSEMBLE_MOST * offer
                            && close(large, 600 * rate + (20 - 3 * 1.5) * area * land.getOfficePricePerSqFt()));
        }
        check("projecting purchases leaves the live shelf and ownership alone",
                land.getMarket().getOffersState().length == 3 && close(land.getOwnedSqFt(), land.getAllocatedSqFt()));
    }

    /** The physical calendar supplies counts; this ledger independently carries each quote's cash. */
    static void roundedCash(Path root, boolean borrowFirst) {
        out.println("--- rounded bond cash survives projected additions, borrowFirst=" + borrowFirst + " ---");
        Game g = flat(root, "rounded-cash-" + borrowFirst);
        LandManager land = g.getLandManager();
        land.setOwnedSqFt(land.getAllocatedSqFt() + 1_000 * LandManager.SQ_FT_PER_KM2);
        long people = g.getPopulationManager().getPopulation();
        var history = g.getHistorySave();
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
                land.getAvailableSqFt(), g.getAutoBuilder().getSlack(), skip, true);
        check("fixture: the growing clinic plan exists", draft != null);
        if (draft == null) return;
        g.getAutoBuilder().setCap(Auction.Cap.FILL, null);
        g.setCashForTest(AutoBuilder.reserve(g) + (borrowFirst ? 0 : draft.price()));
        BuildAdvice.Suggestion plan = BuildAdvice.suggestFor(g, null, care, g.getCash(),
                land.getAvailableSqFt(), g.getAutoBuilder().getSlack(), skip, true);
        check("fixture: the actual plan has later clinic additions", plan != null && plan.value().futureUnits() > 1);
        if (plan == null) return;
        Map<BuildingsTemplate, Long> site = BuildAdvice.onSite(g, care);
        double available = g.getAutoBuilder().spendableForMeasure(g, care);
        double invoice = g.quoteBuild(clinic, plan.count()).total;
        double gap = Math.max(0, invoice - available);
        double expected = financing(g, gap, BuildAdvice.LIFE_MONTHS);
        double raised = gap > 0 ? g.quoteLongBondForCash(gap, Game.BUILD_BOND_YEARS,
                g.fundingLot("Term"), g.getAutoBuilder().getCap()).cashReceived() : 0;
        available = Math.max(0, available + raised - invoice);
        check("fixture: the initial bond strictly exceeds the gap, or the first plan is paid entirely in cash",
                borrowFirst ? gap > 0 && raised > gap : gap == 0);
        boolean initialExcess = available > 0;
        boolean reusedInitial = false, reusedLater = false, laterExcess = false;
        long additions = 0;
        int purchases = 0;
        double rate = Math.max(0, BuildAdvice.lifeRate(g)) / 12;
        double nextLead = g.quoteBuild(clinic, 1).months;
        for (int start = 0; start < BuildAdvice.LIFE_MONTHS - plan.lead(); start += 12) {
            double duration = Math.min(12, BuildAdvice.LIFE_MONTHS - plan.lead() - start);
            double date = plan.lead() + start + duration / 2;
            Map<BuildingsTemplate, Long> with = BuildAdvice.plus(site, Map.of(clinic, plan.count() + additions));
            BuildAdvice.Ahead target = new BuildAdvice.Ahead(date, BuildAdvice.lifeTrend(g, date), g.getAutoBuilder().getSlack());
            if (BuildAdvice.maintained(g, care, with, target)) continue;
            long extra = BuildAdvice.count(g, care, with, Map.of(clinic, 1L), target, BuildAdvice.MOST, true)[0];
            if (extra == 0) continue;
            double bill = g.quoteBuild(clinic, extra).total;
            if (available > 0) {
                if (purchases == 0) reusedInitial = true;
                else reusedLater = true;
            }
            double lack = Math.max(0, bill - available);
            double paid = Math.max(0, date - nextLead);
            expected += financing(g, lack, BuildAdvice.LIFE_MONTHS - paid) * Math.pow(1 + rate, -paid);
            double proceeds = lack > 0 ? g.quoteLongBondForCash(lack, Game.BUILD_BOND_YEARS,
                    g.fundingLot("Term"), g.getAutoBuilder().getCap()).cashReceived() : 0;
            available = Math.max(0, available + proceeds - bill);
            laterExcess |= lack > 0 && proceeds > lack;
            additions += extra;
            purchases++;
        }
        check("fixture: at least two later purchase dates, with rounded later cash spent at the next date",
                purchases >= 2 && laterExcess && reusedLater && additions == plan.value().futureUnits());
        check("fixture: the initial quote's rounded excess is spent by the first later addition when it borrows",
                !borrowFirst || initialExcess && reusedInitial);
        check("projected financing retains the initial and later quoted proceeds above their gaps",
                close(plan.value().financing(), expected));
        double oracleNet = AdviceValueOracle.value(g, plan, land.getAvailableSqFt(), g.getCash())[0];
        double expectedNet = plan.planValue() - plan.value().financing() + expected;
        check("the independent advice oracle retains the same hard-ledger cash", close(oracleNet, expectedNet));
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
        DebtQuote q = g.quoteLongBondForCash(invoice, Game.BUILD_BOND_YEARS, g.fundingLot("Term"),
                g.getAutoBuilder().getCap());
        check("fixture: the flat-demand comparison borrows its build price and needs no later unit",
                value.futureUnits() == 0 && q.cashReceived() > 0 && land.getOfficePricePerSqFt() > 0);
        check("owned run or works ground adds no parcel borrowing",
                close(value.financing(), financing(g, invoice, BuildAdvice.LIFE_MONTHS)));
    }

    static void independentPending(Path root) {
        out.println("--- the independent oracle sizes its own pending electrical load ---");
        Game g = flat(root, "oracle-pending-load");
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate mill = bm.getTemplateByName("Large Mini-Mill");
        // THE FIXTURE CAUSES IT (PW3): the reserve test orders firm rows alone (spec 2.8: a wind farm's credit falls with its share;
        // the spread orders it), so the proposal is the Diesel Station's (1910), dated to this town's year, a plant smaller than the
        // mill's draw as the Wind Farm was.
        BuildingsTemplate plant = bm.getTemplateByName("Diesel Station");
        plant.setSince(CityCalendar.yearOf(g.getMonth()));
        EssentialWorksCheck.quietly(() -> g.buildStack(mill, 1, false));
        BuildingsStacks stack = bm.getStack(mill);
        stack.setConstructionProgress(Math.max(0, mill.getConstructionPoints()
                - g.getBuildingOutputAtEveryPost() * .25));
        UtilitiesHandler utility = g.getServicesManager().getUtilitiesHandler();
        utility.setWattsConsumption(utility.getProduction() * 1.01);
        Set<BuildingsTemplate> skip = new HashSet<>(bm.getTemplates());
        skip.remove(plant);
        BuildAdvice.Suggestion plan = BuildAdvice.suggestFor(g, null,
                BuildAdvice.Measure.of(BuildAdvice.Kind.POWER), g.getCash(),
                g.getLandManager().getAvailableSqFt(), g.getAutoBuilder().getSlack(), skip, true);
        check("fixture: an operating power shortage has a firm proposal (a Diesel Station) and a paid industrial load opens shortly",
                plan != null && plan.count() > 0 && plan.template() == plant && stack.getUnderConstruction() == 1
                        && mill.getElectricityConsumption() > plant.getProduction1()
                        && bm.siteUnitMonths(mill, 1, g.getBuildingOutputAtEveryPost()) > 0
                        && bm.siteUnitMonths(mill, 1, g.getBuildingOutputAtEveryPost()) < BuildAdvice.HORIZON);
        if (plan == null) return;
        double[] oracle = AdviceValueOracle.value(g, plan, g.getLandManager().getAvailableSqFt(), g.getCash());
        check("the independent pending-load schedule and sizing reproduce the complete power plan",
                close(plan.planValue(), oracle[0]) && close(plan.value().used(), oracle[1]));
    }

    static double addedPoints(Game.BuildQuote quote, BuildingsTemplate t, long count) {
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
            long sites = bm.getUnderConstructionById()[road.getId()];
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
        Game g = founded(root, partial ? "crew-partial" : "crew-held", SERVED_WORKS);
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
        long required = BuildAdvice.count(g, care, Map.of(), Map.of(centre, 1L), auto.target(), BuildAdvice.MOST, true)[0];
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

    static Game reserveTown(Path root, String name) {
        BuildAdviceCheck.quiet = EssentialWorksCheck.quiet;
        Game g = founded(root, name, SERVED_WORKS);
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        long people = g.getPopulationManager().getPopulation();
        g.getBusinessInvestment().restorePopulationHistory(List.of(people, people));
        g.getBuildingManager().setBuildersFirstForTest(false);
        g.getBuildingManager().setUnburied(() -> 0);
        return g;
    }

    static void pavingHold(Path root) {
        out.println("--- the hard construction-hold reader resolves the actual paving template ---");
        RoadCheck.quiet = EssentialWorksCheck.quiet;
        Game g = RoadCheck.town(root, "paving-hold");
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        BuildingManager bm = g.getBuildingManager();
        bm.setBuildersFirstForTest(false);
        bm.setUnburied(() -> 0);
        BuildAdvice.Measure care = BuildAdvice.Measure.care(CareType.GENERAL);
        check("fixture: unrelated essential services already have their operating capacity",
                EssentialWorksCheck.serveOtherEssentials(g, care));
        g.getHistorySave().getPopulation().clear();
        g.getHistorySave().getMonth().clear();
        long people = g.getPopulationManager().getPopulation();
        g.getBusinessInvestment().restorePopulationHistory(List.of(people, people));
        BuildingsTemplate centre = bm.getTemplateByName("Community Health Centre");
        long required = BuildAdvice.count(g, care, Map.of(), Map.of(centre, 1L),
                g.getAutoBuilder().target(), BuildAdvice.MOST, true)[0];
        EssentialWorksCheck.quietly(() -> g.buildStack(centre, required, false));
        RoadCheck.scale(g, RoadCheck.noGroundFree(g), 1);
        InfrastructureManager roads = g.getInfrastructureManager();
        roads.setBuiltCapacity(roads.getStreetLoad() * 1.01
                + roads.getHighwayCapacity() - InfrastructureManager.BASE_CAPACITY);
        AutoBuilder auto = g.getAutoBuilder();
        BuildAdvice.Measure streets = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
        BuildAdvice.Suggestion paving = BuildAdvice.suggestFor(g, null, streets, g.getCash(),
                0, auto.getSlack(), RoadCheck.everyRoadAndLine(g), false);
        out.printf("      paving %s, available %d, quote %.9f, unit %.9f, cheaper %b%n",
                paving == null ? "none" : paving.count(), g.paveable(), g.quotePave(1).months,
                BuildAdvice.pavingUnit(g, Map.of()), BuildAdvice.pavingBeatsPaved(g, 0, Map.of()));
        check("fixture: physically served streets have a real paving reserve proposal with positive site output",
                paving != null && paving.paving() && paving.count() > 0 && g.paveable() >= paving.count()
                        && BuildAdvice.maintained(g, streets, Map.of(), BuildAdvice.NOW)
                        && Double.isFinite(g.quotePave(1).months) && g.quotePave(1).months > 0);
        if (paving == null) return;
        double output = g.getBuildingOutputAtEveryPost();
        BuildingsStacks site = bm.getStack(centre);
        double initial = bm.siteUnitMonths(centre, 1, output);
        site.setConstructionProgress(centre.getConstructionPoints()
                - (centre.getConstructionPoints() - site.getConstructionProgress()) * .99 / initial);
        double afterOne = bm.siteUnitMonthsAfter(centre, 1, output, paving.template(), 1,
                addedPoints(g.quotePave(1), paving.template(), 1));
        check("fixture: even one paving would move unserved care past its one-month saved promise",
                required > 0 && !BuildAdvice.maintained(g, care, Map.of(), BuildAdvice.NOW)
                        && BuildAdvice.maintained(g, care, Map.of(centre, required), BuildAdvice.NOW)
                        && initial > 1 && afterOne > 1);
        AutoBuilder.State state = auto.toState();
        state.on = true;
        state.pendingUntil.put(care.label(), g.getMonth() + 1.0);
        AutoBuilder.OpenAsk ask = ask(paving, g.getMonth(), 0);
        ask.templateId = -1;
        state.asks = new ArrayList<>(List.of(ask));
        auto.restore(state);
        EssentialWorksCheck.quietly(() -> auto.pass(g));
        AutoBuilder.Step step = EssentialWorksCheck.step(g, streets);
        out.println("      paving step " + step);
        check("the real automatic ask path records a paving construction hold",
                step != null && step.cut() == AutoBuilder.Cut.BUILDERS && step.ordered() == 0
                        && step.building().equals(ConstructionControl.PAVE_FROM + ", paved"));
        check("the hard hold-reason reader accepts the actual paving construction hold",
                step != null && AutoBuildCheck.heldBuildingIsTrue(g, auto, step));
    }

    static void highwayPromise(Path root) {
        out.println("--- an early street cannot cover a later highway's saved promise ---");
        Game g = reserveTown(root, "highway-promise");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate highway = bm.getTemplateByName("Elevated Highway");
        BuildingsTemplate street = bm.getTemplateByName("Paved Road");
        // THE FIXTURE CAUSES IT (PW3): the reserve test orders firm rows alone (spec 2.8: a wind farm's credit falls with its share,
        // and the spread orders it), so the reserve plant is the Diesel Station (1910, 30 months), dated to this town's year, a build
        // the size of the Wind Farm PW1 wrote the section against (3,110 points to 2,802).
        BuildingsTemplate plant = bm.getTemplateByName("Diesel Station");
        plant.setSince(CityCalendar.yearOf(g.getMonth()));
        BuildAdvice.Measure through = BuildAdvice.Measure.of(BuildAdvice.Kind.HIGHWAYS);
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        check("fixture: unrelated essential services already have their operating capacity",
                EssentialWorksCheck.serveOtherEssentials(g, through));
        EssentialWorksCheck.quietly(() -> {
            g.buildStack(highway, 1, false);
            g.buildStack(street, 1, false);
        });
        InfrastructureManager roads = g.getInfrastructureManager();
        roads.setThrough(0, LandManager.SQ_FT_PER_KM2 * 100, 0);
        roads.setLoad(BuildAdvice.highwayFlow(g) * .5);
        roads.setBuiltCapacity(roads.getStreetLoad() * 2);
        // ...power served with 5% to spare, as the advice reads it (PW1: the calm peak's draw over the firm supply).
        EssentialWorksCheck.powerDrawAt(g, 1 / 1.05);
        List<BuildingsTemplate> catalogue = new ArrayList<>(bm.getTemplates());
        bm.getTemplates().removeIf(t -> t.getCategory() == BuildingType.ELECTRICITY && t != plant);
        try {
            AutoBuilder auto = g.getAutoBuilder();
            BuildAdvice.Suggestion reserve = BuildAdvice.suggestFor(g, null, power, g.getCash(),
                    g.getLandManager().getAvailableSqFt());
            check("fixture: power is physically served with a firm reserve proposal (a Diesel Station), and the pending highway covers its unserved traffic (served now "
                            + BuildAdvice.maintained(g, power, Map.of(), BuildAdvice.NOW) + ", proposal "
                            + (reserve == null ? "none" : reserve.count() + " " + reserve.template().getName()) + ")",
                    BuildAdvice.maintained(g, power, Map.of(), BuildAdvice.NOW) && reserve != null
                            && reserve.template() == plant && !BuildAdvice.maintained(g, through, Map.of(), BuildAdvice.NOW)
                            && BuildAdvice.maintained(g, through, Map.of(highway, 1L), auto.target()));
            if (reserve == null) return;
            double output = g.getBuildingOutputAtEveryPost();
            BuildingsStacks h = bm.getStack(highway), r = bm.getStack(street);
            double hQuote = bm.siteUnitMonths(highway, 1, output);
            h.setConstructionProgress(highway.getConstructionPoints()
                    - (highway.getConstructionPoints() - h.getConstructionProgress()) * .99 / hQuote);
            double rQuote = bm.siteUnitMonths(street, 1, output);
            r.setConstructionProgress(street.getConstructionPoints()
                    - (street.getConstructionPoints() - r.getConstructionProgress()) * .01 / rQuote);
            double points = addedPoints(g.quoteBuild(plant, 1), plant, 1);
            double streetAfter = bm.siteUnitMonthsAfter(street, 1, output, plant, 1, points);
            double highwayAfter = bm.siteUnitMonthsAfter(highway, 1, output, plant, 1, points);
            AutoBuilder.State state = auto.toState();
            state.on = true;
            double due = g.getMonth() + BuildAdvice.firstCompletion(g, through, BuildAdvice.onSite(g, through));
            state.pendingUntil.put(through.label(), due);
            auto.restore(state);
            check("fixture: the shared road-site map includes an early street, but even one reserve diesel would postpone the highway beyond its own due",
                    BuildAdvice.onSite(g, through).containsKey(street)
                            && bm.siteUnitMonths(street, 1, output) < bm.siteUnitMonths(highway, 1, output)
                            && g.getMonth() + Math.ceil(streetAfter) <= due
                            && g.getMonth() + Math.ceil(highwayAfter - 1e-9) > due);
            EssentialWorksCheck.quietly(() -> auto.pass(g));
            AutoBuilder.Step step = EssentialWorksCheck.step(g, power);
            check("the actual reserve order preserves the highway-only completion promise (" + (step == null ? "no step"
                            : step.outcome() + ", " + step.ordered() + " " + step.building() + ", cut " + step.cut()) + ")",
                    step != null && step.ordered() == 0 && step.cut() == AutoBuilder.Cut.BUILDERS
                            && g.getMonth() + Math.ceil(bm.siteUnitMonths(highway, 1, output) - 1e-9) <= due);
        } finally {
            bm.getTemplates().clear();
            bm.getTemplates().addAll(catalogue);
        }
    }

    static void promotedDepot(Path root) {
        out.println("--- a reserve order can promote a previously unstarved depot ---");
        Game g = reserveTown(root, "promoted-depot");
        BuildingManager bm = g.getBuildingManager();
        BuildAdvice.Measure care = BuildAdvice.Measure.care(CareType.GENERAL);
        BuildAdvice.Measure streets = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
        BuildingsTemplate centre = bm.getTemplateByName("Community Health Centre");
        BuildingsTemplate depot = bm.getTemplateByName("Construction Depot");
        BuildingsTemplate road = bm.getTemplateByName("Paved Road");
        for (BuildingsTemplate t : bm.getTemplates()) if (care.serves(t) && bm.getQuantity(t.getId()) > 0)
            EssentialWorksCheck.quietly(() -> bm.retire(t, bm.getQuantity(t.getId())));
        // FS15: builders short of the depot, caused: the town keeps one standing depot of its founding's two, whose month
        // less the repairs is under the reserve's builders-first threshold (the next fixture). The served city played two
        // years had such builders; the founding's two build 535 a month and owe the depot under half a month.
        EssentialWorksCheck.quietly(() -> bm.retire(depot, bm.getQuantity(depot.getId()) - 1));
        check("fixture: the town keeps one standing depot", bm.getQuantity(depot.getId()) == 1);
        long required = BuildAdvice.count(g, care, Map.of(), Map.of(centre, 1L),
                g.getAutoBuilder().target(), BuildAdvice.MOST, true)[0];
        EssentialWorksCheck.quietly(() -> {
            g.buildStack(centre, required, false);
            g.buildStack(depot, 1, false);
        });
        InfrastructureManager roads = g.getInfrastructureManager();
        roads.setModes(0, 0);
        roads.setThrough(0, g.getLandManager().getAllocatedSqFt(), 0);
        roads.setBuiltCapacity(20.0 * road.getCapacity());
        roads.setLoad(roads.getStreetCapacity() / 1.01);
        double[] traffic = new double[Traffic.values().length];
        traffic[Traffic.GOODS.ordinal()] = roads.getLoad();
        roads.setBreakdown(traffic);
        roads.setBuiltCapacity(roads.getStreetLoad() * 1.01 - InfrastructureManager.BASE_CAPACITY);
        List<BuildingsTemplate> catalogue = new ArrayList<>(bm.getTemplates());
        bm.getTemplates().removeIf(t -> t.getCategory() == BuildingType.INFRASTRUCTURE && !t.isHighway() && t != road);
        try {
            AutoBuilder auto = g.getAutoBuilder();
            auto.setSlack(.50, null);
            BuildAdvice.Suggestion reserve = BuildAdvice.suggestFor(g, null, streets, g.getCash(),
                    g.getLandManager().getAvailableSqFt());
            check("fixture: physically served streets have a multi-road reserve plan while pending care covers its operating shortage",
                    reserve != null && reserve.count() > 1
                            && BuildAdvice.maintained(g, streets, Map.of(), BuildAdvice.NOW)
                            && !BuildAdvice.maintained(g, care, Map.of(), BuildAdvice.NOW)
                            && BuildAdvice.maintained(g, care, Map.of(centre, required), BuildAdvice.NOW));
            if (reserve == null) return;
            double output = g.getBuildingOutputAtEveryPost();
            BuildingsStacks depotSite = bm.getStack(depot);
            // THE FIXTURE CAUSES IT (PW2): three quarters of the depot owed, not half, so it owes more than a month: PW2's
            // refigured coal plant, cheaper to repair, left the town's month at 201 points, past half the depot's 400.
            double depotOwed = Math.min(depot.getConstructionPoints() * .75, output * 5);
            depotSite.setConstructionProgress(depot.getConstructionPoints() - depotOwed);
            double total = 0;
            for (BuildingsStacks site : bm.getStacksUnderConstruction())
                total += BuildingManager.weightOf(site.getBuilding(), site.getUnderConstruction());
            double weight = BuildingManager.weightOf(depot, 1);
            double extra = BuildingManager.weightOf(road, reserve.count());
            double sites = depotOwed * (total + extra * .5)
                    / (BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING * weight);
            bm.setSitesAtEveryPost(() -> sites);
            bm.setBuildersCouldStaff((t, n) -> true);
            bm.setBuildersFirstForTest(true);
            BuildingsStacks careSite = bm.getStack(centre);
            double initial = bm.siteUnitMonths(centre, 1, output);
            double goal = .9 * total / (total + extra);
            careSite.setConstructionProgress(centre.getConstructionPoints()
                    - (centre.getConstructionPoints() - careSite.getConstructionProgress()) * goal / initial);
            double before = bm.siteUnitMonths(centre, 1, output);
            double naiveAfter = before * (total + extra) / total;
            double depotAfter = depotOwed * (total + extra) / (sites * weight);
            AutoBuilder.State state = auto.toState();
            state.on = true;
            double due = g.getMonth() + Math.ceil(before);
            state.pendingUntil.put(care.label(), due);
            auto.restore(state);
            check("fixture: the reserve promotes a staffable depot past the model's builders-first threshold even though ordinary crew sharing would keep care inside its due",
                    !bm.goesFirst(depot, 0) && depotAfter > BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING
                            && before > 0 && before < 1 && naiveAfter < 1 && depotOwed / output > 1);
            EssentialWorksCheck.quietly(() -> auto.pass(g));
            AutoBuilder.Step step = EssentialWorksCheck.step(g, streets);
            check("the actual reserve pass trims the promoted-depot delay and preserves care's completion",
                    step != null && step.ordered() < reserve.count() && step.cut() == AutoBuilder.Cut.BUILDERS
                            && g.getMonth() + Math.ceil(bm.siteUnitMonths(centre, 1, output) - 1e-9) <= due);
            // Complete the original reserve counterfactual only after checking the real guarded pass.
            long remaining = Math.max(0, reserve.count() - (step == null ? 0 : step.ordered()));
            double predicted = bm.siteUnitMonthsAfter(centre, 1, output, road, remaining,
                    addedPoints(g.quoteBuild(road, remaining), road, remaining));
            EssentialWorksCheck.quietly(() -> g.buildStack(road, remaining, false));
            double actual = bm.siteUnitMonths(centre, 1, output);
            check("the full reserve actually promotes the depot and its prospective quote matches the changed queue",
                    bm.goesFirst(depot, 0) && actual > 1 && close(predicted, actual));
        } finally {
            bm.getTemplates().clear();
            bm.getTemplates().addAll(catalogue);
        }
    }

    static void commonCalendar(Path root) {
        out.println("--- a work opening after the common comparison end earns no operating credit ---");
        Game g = flat(root, "common-calendar");
        g.setCashForTest(1e14);
        g.getLandManager().setOwnedSqFt(g.getLandManager().getAllocatedSqFt()
                + 1_000 * LandManager.SQ_FT_PER_KM2);
        BuildingManager bm = g.getBuildingManager();
        bm.setBuildersFirstForTest(false);
        bm.setUnburied(() -> 0);
        BuildingsTemplate road = bm.getTemplateByName("Paved Road"), wind = bm.getTemplateByName("Wind Farm");
        int queued = Math.max(1, (int) Math.ceil((BuildAdvice.LIFE_MONTHS + 12.0)
                * g.getBuildingOutputAtEveryPost() / road.getConstructionPoints()));
        EssentialWorksCheck.quietly(() -> g.buildStack(road, queued, false));
        bm.getControl().setOrder(List.of(ConstructionControl.keyOf(road), ConstructionControl.keyOf(wind)));
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        UtilitiesHandler utility = g.getServicesManager().getUtilitiesHandler();
        utility.setWattsConsumption(utility.getProduction() * 1.2);
        for (Sector sector : g.getSectors().all()) sector.setEnergyRatio(.8);
        double lead = g.quoteBuild(wind, 1).months;
        check("fixture: funded priority work delays a staffable plant beyond twenty years while operating power is physically short",
                Double.isFinite(lead) && lead > BuildAdvice.LIFE_MONTHS && lead < 2 * BuildAdvice.LIFE_MONTHS
                        && !BuildAdvice.maintained(g, power, Map.of(), BuildAdvice.NOW)
                        && BuildAdvice.unit(g, power, wind) > 0
                        && g.getSectors().all().stream().anyMatch(t -> t.statement().revenue > t.statement().inputs));
        WorksValue.Value value = WorksValue.of(g, power, Map.of(), wind, 1,
                g.getLandManager().getAvailableSqFt());
        check("a post-horizon opening keeps its capital and posts but claims no operating loss relief, running, tax return or later works",
                value.capital() > 0 && value.jobs() > 0 && value.payroll() > 0
                        && value.used() == 0 && value.avoided() == 0 && value.running() == 0
                        && value.payrollReturned() == 0 && value.futureUnits() == 0);
    }

    static void growingCalendarChoice(Path root) {
        out.println("--- growing works share a calendar end before their actual funded choice ---");
        Game g = reserveTown(root, "growing-calendar");
        BuildAdvice.Measure streets = BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
        check("fixture: unrelated essential services already have their operating capacity",
                EssentialWorksCheck.serveOtherEssentials(g, streets));
        BuildingManager bm = g.getBuildingManager();
        EssentialWorksCheck.quietly(() -> g.buildStack(bm.getTemplateByName("House"), 100, false));
        BuildingsTemplate paved = bm.getTemplateByName("Paved Road"), rail = bm.getTemplateByName("Light Rail Line");
        List<BuildingsTemplate> catalogue = new ArrayList<>(bm.getTemplates());
        bm.getTemplates().removeIf(t -> t.getCategory() == BuildingType.INFRASTRUCTURE && t != paved && t != rail);
        try {
            long people = g.getPopulationManager().getPopulation();
            var population = g.getHistorySave().getPopulation();
            var months = g.getHistorySave().getMonth();
            population.clear();
            months.clear();
            for (int i = 0; i <= 12; i++) {
                population.add(Math.round(people * (GROWN_FROM + (1 - GROWN_FROM) * i / 12)));
                months.add(g.getMonth() - 12 + i);
            }
            g.getBusinessInvestment().restorePopulationHistory(population);
            InfrastructureManager roads = g.getInfrastructureManager();
            roads.setModes(0, 0);
            roads.setThrough(0, LandManager.SQ_FT_PER_KM2 * 100, 0);
            double capacity = InfrastructureManager.BASE_CAPACITY + paved.getCapacity();
            roads.setBuiltCapacity(paved.getCapacity());
            roads.setLoad(capacity * .6);
            double[] traffic = new double[Traffic.values().length];
            traffic[Traffic.COMMUTERS.ordinal()] = roads.getLoad();
            roads.setBreakdown(traffic);
            roads.setBuiltCapacity(roads.getStreetLoad() / .6 - InfrastructureManager.BASE_CAPACITY);
            // A high-value trading month makes the different forecast endpoints affect the funded choice.
            for (Sector sector : g.getSectors().all()) {
                Sector.Statement book = sector.statement();
                book.revenue += Math.max(0, book.revenue - book.inputs - book.electricity - book.water);
            }
            AutoBuilder auto = g.getAutoBuilder();
            auto.setCap(Auction.Cap.FILL, null);
            // FS15: the player's limit set to its most, so rail's credit is inside it: the founding's year of GDP is the
            // seeded month's twelve, under the served city's after two played years.
            auto.setDebtLimit(AutoBuilder.DEBT_LIMIT_MOST, null);
            g.setCashForTest(AutoBuilder.reserve(g) + 1);
            Set<BuildingsTemplate> skip = new HashSet<>(bm.getTemplates());
            skip.remove(paved);
            BuildAdvice.Suggestion roadPlan = BuildAdvice.suggestFor(g, null, streets, g.getCash(),
                    g.getLandManager().getAvailableSqFt(), auto.getSlack(), skip, true);
            skip.add(paved);
            skip.remove(rail);
            BuildAdvice.Suggestion railPlan = BuildAdvice.suggestFor(g, null, streets, g.getCash(),
                    g.getLandManager().getAvailableSqFt(), auto.getSlack(), skip, true);
            check("fixture: currently served streets have two growing plans, with slower dearer rail, both needing credit inside the player's limit",
                    roadPlan != null && railPlan != null
                            && BuildAdvice.maintained(g, streets, Map.of(), auto.target())
                            && railPlan.lead() > roadPlan.lead() && railPlan.lead() < BuildAdvice.LIFE_MONTHS
                            && railPlan.price() > roadPlan.price()
                            && auto.canPay(g, roadPlan.price()) && auto.canPay(g, railPlan.price())
                            && AutoBuilder.gapFor(g, roadPlan.price()) > 0);
            if (roadPlan == null || railPlan == null) return;
            double[] roadValue = AdviceValueOracle.value(g, roadPlan,
                    g.getLandManager().getAvailableSqFt(), g.getCash());
            double[] railValue = AdviceValueOracle.value(g, railPlan,
                    g.getLandManager().getAvailableSqFt(), g.getCash());
            out.printf("      growing plan values: road %.9f, rail %.9f; independent road %.9f, rail %.9f%n",
                    roadPlan.planValue(), railPlan.planValue(), roadValue[0], railValue[0]);
            check("the independent common-calendar values favour the timely road and match both complete plan sums",
                    roadValue[0] < railValue[0] && close(roadPlan.planValue(), roadValue[0])
                            && close(railPlan.planValue(), railValue[0]));
            double railGap = AutoBuilder.gapFor(g, railPlan.price());
            auto.setOn(true, null);
            EssentialWorksCheck.quietly(() -> auto.pass(g));
            AutoBuilder.Step order = EssentialWorksCheck.step(g, streets);
            check("the real automatic order takes the timely road with less new borrowing than rail requires",
                    order != null && order.ordered() == roadPlan.count() && order.building().equals(paved.getName())
                            && order.borrowed() < railGap);
        } finally {
            bm.getTemplates().clear();
            bm.getTemplates().addAll(catalogue);
        }
    }

    public static void main(String[] args) throws Exception {
        java.util.Locale.setDefault(java.util.Locale.CANADA);
        Path root = TempDirs.create("worksfinancecheck");
        if (args.length > 0 && args[0].startsWith("--projection")) {
            if (!args[0].equals("--projection-cash")) {
                carriedGround(root, false);
                carriedGround(root, true);
                carriedGround(root, false, true);
            }
            if (!args[0].equals("--projection-ground")) {
                roundedCash(root, false);
                roundedCash(root, true);
            }
            out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
            System.exit(fails == 0 ? 0 : 1);
        }
        commonCalendar(root);
        growingCalendarChoice(root);
        reservationRelease(root);
        reservationRelease(root, true);
        groundUse("Elevated Highway");
        groundUse("Hydro Station");
        independentPending(root);
        prospectiveQueue(root);
        crewPromise(root, false);
        crewPromise(root, true);
        pavingHold(root);
        highwayPromise(root);
        promotedDepot(root);
        parcelCash(root, false);
        parcelCash(root, true);
        remainingCoupons(root);
        laterQuotes(root);
        carriedGround(root, false);
        carriedGround(root, true);
        carriedGround(root, false, true);
        roundedCash(root, false);
        roundedCash(root, true);
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
