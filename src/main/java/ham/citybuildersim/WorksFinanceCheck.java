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
        out.println("--- a higher-loss ask releases lower earmarks before an empty capped sale ---");
        Game g = flat(root, "finance-reservations");
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
        state.asks.add(ask(high, g.getMonth(), 0));
        state.asks.add(ask(low, g.getMonth(), .4 * free));
        auto.restore(state);
        g.getBank().setDepositRate(1);
        AuctionCheck.roomTo(g, 0);
        double[] foreign = g.getCapitalFlows().toSaveArray();
        foreign[1] = g.getMonth() + 12;
        foreign[6] = g.getMonth();
        g.getCapitalFlows().restore(foreign);
        double needed = high.price() - auto.spendableForMeasure(g, streets);
        DebtQuote empty = AutoBuilder.bondQuote(g, needed, auto.getCap());
        out.printf("      free %.9f, unreserved %.9f, needed %.9f, raised by quote %.9f%n",
                free, AutoBuilder.spendable(g), needed, empty == null ? 0 : empty.cashReceived());
        check("fixture: 100 above reserve, 40 lower-reserved, 90 higher-cost, and the gap's capped auction sells nothing",
                close(AutoBuilder.spendable(g), .6 * free) && close(high.price(), .9 * free)
                        && needed > 0 && (empty == null || empty.cashReceived() == 0));
        double debt = AutoBuilder.debt(g);
        EssentialWorksCheck.quietly(() -> auto.pass(g));
        AutoBuilder.Step order = EssentialWorksCheck.step(g, streets);
        check("the higher-loss service is actually ordered using the released cash",
                order != null && order.ordered() > 0);
        check("the lower funded reservation is released while outstanding debt is retained",
                auto.asks().stream().noneMatch(a -> a.measure().equals(power.label()) && a.raised() > 0)
                        && AutoBuilder.debt(g) == debt);
    }

    public static void main(String[] args) throws Exception {
        java.util.Locale.setDefault(java.util.Locale.CANADA);
        Path root = TempDirs.create("worksfinancecheck");
        reservationRelease(root);
        parcelCash(root, false);
        parcelCash(root, true);
        remainingCoupons(root);
        laterQuotes(root);
        out.println(fails == 0 ? "All checks passed." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
