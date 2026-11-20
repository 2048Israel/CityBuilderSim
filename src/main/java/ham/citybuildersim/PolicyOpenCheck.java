package ham.citybuildersim;

import com.google.gson.Gson;
import ham.citybuildersim.PolicyDates.Lever;
import ham.citybuildersim.ResearchCatalog.Entry;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Every Policy lever is open from the city's first month, in every city, and nothing opens one later (OP1, 2026-11-19).
 *
 * WHY. From R0 (2026-11-04) to OP1 each lever opened on the date Canada first had it: the income taxes 1916-1920, the minimum wage 1918,
 * zoning 1924, the promises 1927-1966, and in a city founded the 1900 way the Advance Rate in 1914 and the Bank of Canada in 1935; a law find
 * brought one up to four years forward, and the Policy screen drew a shut lever with its date. Jerus, 2026-11-19: "remove it, instead
 * everything policy related is available right away, and later ill think of an actual tutorial". PolicyDatesCheck held the gate; this
 * holds its absence, and it is written so that a gate put back fails it: a dated lever refuses its setter in month 1, or holds a setting at
 * zero the month after, or opens a lever on a find, or reappears as a method by its old name. Canada's dates stay as the record (the
 * research catalogue's law rows carry them) and the gold years stay history's (call 2): MoneyDatesCheck holds those.
 *
 * What it has to prove:
 *
 *   1. THE RECORD. PolicyDates keeps Canada's dates row for row (research-1900.md 7.1, the table typed out below), each as a game month
 *      CityCalendar gives back, and the levers a city had before 1900 at month 1; the table and the open list name every lever once, the
 *      dates run in the declared order, and no two levers share an entry key.
 *   2. THE FOUNDING. In month 1 of a city founded the 1900 way and of one founded the default way, the three income taxes read the founding
 *      rate (DEFAULT_INCOME_TAX, their offsets zero), exactly the levers PolicyDates.opensAtFoundingRate() names; every promise reads zero
 *      (the pension, its contribution, EI's premium and benefit, the health premium, the grant and the loan rate); the minimum wage's floor
 *      binds; the rule has the dial; and the builders' founding quote is the city's month-1 sales tax.
 *   3. EVERY SETTER TAKES IN MONTH 1, in both cities: every dial of the twelve dated levers, the minimum wage through Game, a zone through
 *      Game (and its line in the decision log); and a month on, every setting stands as set and the floor still binds.
 *   4. MONEY IS THE PLAYER'S FROM THE FOUNDING. The 1900 city is founded on gold at par, its dial tied to the US$ (history's regime,
 *      call 2); in month 1 the target and the holdings take a setting, the dollar can be floated, and the dial then takes a hand's rate, and
 *      a month on gold is not forced back; the default city's dial, target, holdings and regime take settings in month 1 too.
 *   5. A LAW FIND OPENS NOTHING (★1a). Every law row of a lever says so in its touches; in a 1900 city carried to the month before PL3's
 *      window, the faculty find PL3 (the wage tax's law) and no dial of the policy moves, that month or the next, the wage tax having read
 *      the founding rate all along; the find is told as a find and no lever's opening is told.
 *   6. NOTHING TELLS A DATE. No Policy screen, Zoning or Trade card, Policy model or the inbox reads PolicyDates (its dates are history's
 *      record only), and a 1900 city's first year raises no "lever:" notice.
 *   7. THE GATE CANNOT COME BACK BY ITS OLD NAMES: TaxPolicy, PolicyDates, Game, DebtManager, CentralBank and DataSave declare none of the
 *      gate's members (the month the policy heard, the openings, the shut words, the early-opening bound, the saved openings).
 */
public class PolicyOpenCheck {

    private static int fails;
    private static final PrintStream OUT = System.out;
    private static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) {
        OUT.println((ok ? "  PASS " : "  FAIL ") + label);
        if (!ok) fails++;
    }

    static void check(String label, boolean ok, String detail) {
        check(label + (ok ? "" : " [" + detail + "]"), ok);
    }

    /** Runs a city's work with its printing silenced. */
    static void quietly(Runnable r) {
        System.setOut(QUIET);
        try { r.run(); } finally { System.setOut(OUT); }
    }

    /** One row of research-1900.md 7.1: the year and month Canada first had the lever (monthKnown false: the study gives the year only). */
    record Row(Lever lever, int year, int month, boolean monthKnown, String entry) { }

    /** research-1900.md 7.1 and 7.2, in history's order: the specification the record is held to (PolicyDatesCheck's, kept). */
    static final List<Row> TABLE = List.of(
            new Row(Lever.POLICY_RATE,          1914, 8, true,  ""),       // the Finance Act's Advance Rate, Aug 1914
            new Row(Lever.PROFIT_TAX,           1916, 1, false, "PL2"),
            new Row(Lever.WAGE_TAX,             1917, 1, false, "PL3"),
            new Row(Lever.MINIMUM_WAGE,         1918, 1, false, "PL4"),
            new Row(Lever.SALES_TAX,            1920, 1, false, "PL5"),
            new Row(Lever.ZONING,               1924, 1, false, "PL6"),
            new Row(Lever.PENSION_REPLACEMENT,  1927, 1, false, "PL7"),
            new Row(Lever.CENTRAL_BANK,         1935, 3, true,  ""),       // the Bank of Canada, 11 Mar 1935
            new Row(Lever.STUDENT_AID,          1939, 1, false, "PL9"),
            new Row(Lever.EI,                   1940, 8, true,  "PL10"),   // the Unemployment Insurance Act, 7 Aug 1940
            new Row(Lever.HEALTH_PREMIUM,       1947, 1, true,  "PL12"),   // Saskatchewan, 1 Jan 1947
            new Row(Lever.PENSION_CONTRIBUTION, 1966, 1, true,  "L5"));    // the Canada Pension Plan, 1 Jan 1966

    /** The levers the study leaves open from the first day (7.1: "open"). */
    static final List<Lever> OPEN = List.of(Lever.PROPERTY_TAX, Lever.FARMLAND_RELIEF, Lever.FOOD_ASSISTANCE, Lever.TRANSIT_FARE,
            Lever.TUITION_SCALES, Lever.HEALTH_FEE_SCALE, Lever.FUEL_DUTY, Lever.PORT_DUES, Lever.SECTOR_SUBSIDIES);

    /** The three income taxes, typed here as the specification of PolicyDates.opensAtFoundingRate() (call 16): the levers that fund the city. */
    static final Set<Lever> INCOME_TAXES = EnumSet.of(Lever.PROFIT_TAX, Lever.WAGE_TAX, Lever.SALES_TAX);

    /** The promises: each costs the city, and opens at zero. */
    static final Set<Lever> PROMISES = EnumSet.of(Lever.PENSION_REPLACEMENT, Lever.PENSION_CONTRIBUTION, Lever.EI, Lever.HEALTH_PREMIUM,
            Lever.STUDENT_AID);

    /** What research.json's law row of a lever says since OP1, at the end of its touches. */
    static final String OPENS_NOTHING = "; the find opens nothing (every lever is open from the founding)";

    /** The game month of a real calendar month, worked out here and not by PolicyDates. */
    static int monthOf(int year, int monthOfYear) {
        return 1 + 12 * (year - CityCalendar.EPOCH_YEAR) + monthOfYear - 1;
    }

    /* ----- the dials ----- */

    /** Every dial of the policy's dated levers set to a non-zero setting through the setters, none of them its opening rate. */
    static void setEveryDial(TaxPolicy p) {
        WageBand band = WageBand.values()[0];
        p.setProfitTaxRate(.20);
        p.setSalesTaxRate(.10);
        p.setWageTaxRate(.12);
        p.setWageOffset(band, .02);
        p.setProfitOffset(TaxPolicy.FARM_SECTOR, .03);
        p.setSalesOffset(TaxPolicy.FARM_SECTOR, .01);
        p.setPensionReplacement(.30);
        p.setContributionRate(.04);
        p.setEiPremiumRate(.02);
        p.setEiBenefitRate(.40);
        p.setHealthPremiumRate(.03);
        p.setGrant(TaxPolicy.GrantBasis.WAGE_SHARE, .50);
        p.setStudentLoanRate(.05);
    }

    /** What setEveryDial() set, lever by lever, as dialOf() reads it. */
    static double setting(Lever lever) {
        return switch (lever) {
            case PROFIT_TAX -> .20 + .03;
            case SALES_TAX -> .10 + .01;
            case WAGE_TAX -> .12 + .02;
            case PENSION_REPLACEMENT -> .30;
            case PENSION_CONTRIBUTION -> .04;
            case EI -> .02 + .40;
            case HEALTH_PREMIUM -> .03;
            case STUDENT_AID -> .50 + .05;
            default -> 0;
        };
    }

    /** What one of the policy's dated levers owns, as one number: its dials and offsets summed (the grant and the loan rate for student aid). */
    static double dialOf(TaxPolicy p, Lever lever) {
        WageBand band = WageBand.values()[0];
        return switch (lever) {
            case WAGE_TAX -> p.getWageTaxRate() + p.getWageOffset(band);
            case PROFIT_TAX -> p.getProfitTaxRate() + p.getProfitOffset(TaxPolicy.FARM_SECTOR);
            case SALES_TAX -> p.getSalesTaxRate() + p.getSalesOffset(TaxPolicy.FARM_SECTOR);
            case PENSION_REPLACEMENT -> p.getPensionReplacement();
            case PENSION_CONTRIBUTION -> p.getContributionRate();
            case EI -> p.getEiPremiumRate() + p.getEiBenefitRate();
            case HEALTH_PREMIUM -> p.getHealthPremiumRate();
            case STUDENT_AID -> p.getGrantAmount() + p.getStudentLoanRate();
            default -> 0;
        };
    }

    /** An income tax's base rate alone, which is what opens at the founding rate. */
    static double baseRateOf(TaxPolicy p, Lever tax) {
        return switch (tax) {
            case WAGE_TAX -> p.getWageTaxRate();
            case PROFIT_TAX -> p.getProfitTaxRate();
            case SALES_TAX -> p.getSalesTaxRate();
            default -> throw new IllegalArgumentException(tax + " is not an income tax");
        };
    }

    /** Every dial of the policy, the property tax's too, as one array: what a find must leave as it was. */
    static double[] dials(TaxPolicy p) {
        List<Lever> owned = new ArrayList<>(INCOME_TAXES);
        owned.addAll(PROMISES);
        double[] d = new double[owned.size() + 1];
        for (int i = 0; i < owned.size(); i++) d[i] = dialOf(p, owned.get(i));
        d[owned.size()] = p.getPropertyTaxRate();
        return d;
    }

    /** How many lines the city's decision log holds under the zoning kind. */
    static int zoningLines(Game g) {
        int n = 0;
        for (DecisionLog.Entry e : g.getDecisions().entries()) if (DecisionLog.ZONING.equals(e.kind)) n++;
        return n;
    }

    /** How many of a city's notices are a lever's opening (the R0 notice's key). */
    static long leverNotices(Game g) {
        return g.getInbox().all().stream().filter(n -> n.getKey().startsWith("lever:")).count();
    }

    /** A city founded on Founding.defaults(), the 1900 way if asked, on scratch files. */
    static Game city(String label, boolean the1900Way) {
        Game g = new Game(GameFiles.scratch(label), the1900Way ? Founding.defaults().withUnfitted(true) : Founding.defaults());
        quietly(g::run);
        return g;
    }

    /** The names a class declares, methods and fields. */
    static Set<String> declared(Class<?> c) {
        Set<String> names = new HashSet<>();
        for (Method m : c.getDeclaredMethods()) names.add(m.getName());
        for (Field f : c.getDeclaredFields()) names.add(f.getName());
        return names;
    }

    public static void main(String[] args) throws Exception {

        /* ==================== 1. the record ==================== */
        OUT.println("--- 1. Canada's dates stay as the record, row for row, and nothing opens on them ---");
        for (Row r : TABLE) {
            int expected = monthOf(r.year(), r.month());
            int month = PolicyDates.canadaMonth(r.lever());
            check(r.lever() + ": Canada had it in game month " + expected + " (" + r.year() + (r.monthKnown() ? ", month " + r.month() : ", January")
                    + "), and CityCalendar gives the year and month back", month == expected && CityCalendar.yearOf(month) == r.year()
                    && CityCalendar.monthOfYear(month) == r.month() && r.lever().monthKnown() == r.monthKnown() && r.lever().entry().equals(r.entry()));
        }
        boolean openAtOne = true;
        for (Lever l : OPEN) openAtOne &= PolicyDates.canadaMonth(l) == 1 && l.entry().isEmpty();
        check("the levers a city had before 1900 are at month 1, with no entry key", openAtOne);
        Set<Lever> both = EnumSet.noneOf(Lever.class);
        for (Row r : TABLE) both.add(r.lever());
        both.addAll(OPEN);
        check("the table and the open list name every lever exactly once (" + Lever.values().length + " levers)",
                both.size() == TABLE.size() + OPEN.size() && both.size() == Lever.values().length);
        List<Lever> declaredOrder = new ArrayList<>();
        for (Lever l : Lever.values()) if (!OPEN.contains(l)) declaredOrder.add(l);
        boolean inOrder = true;
        for (int i = 1; i < TABLE.size(); i++) inOrder &= PolicyDates.canadaMonth(TABLE.get(i).lever()) > PolicyDates.canadaMonth(TABLE.get(i - 1).lever());
        check("the dates run in the table's order, which is the declared order", inOrder
                && declaredOrder.equals(TABLE.stream().map(Row::lever).toList()));
        Set<String> keys = new HashSet<>();
        boolean unique = true;
        for (Lever l : Lever.values()) if (!l.entry().isEmpty()) unique &= keys.add(l.entry());
        check("no two levers share an entry key", unique);

        /* ==================== 2. the founding ==================== */
        OUT.println("\n--- 2. in month 1 every lever is open at its opening rate, in a 1900 city and in a default one ---");
        check("PolicyDates.opensAtFoundingRate() names the three income taxes and nothing else",
                Arrays.stream(Lever.values()).filter(PolicyDates::opensAtFoundingRate).collect(java.util.stream.Collectors.toSet()).equals(INCOME_TAXES));
        Game old = city("policy-open-1900", true), today = city("policy-open-today", false);
        check("fixture: one city founded the 1900 way and one the default way, each in month 1", old.foundedUnfitted() && !today.foundedUnfitted()
                && old.getMonth() == 1 && today.getMonth() == 1);
        for (Game g : List.of(old, today)) {
            String which = g.foundedUnfitted() ? "the 1900 city" : "the default city";
            TaxPolicy p = g.getEconomyManager().getTaxPolicy();
            WageBand band = WageBand.values()[0];
            for (Lever tax : INCOME_TAXES) {
                check(which + ": " + tax + " reads the founding rate, " + TaxPolicy.DEFAULT_INCOME_TAX + ", its offsets zero",
                        baseRateOf(p, tax) == TaxPolicy.DEFAULT_INCOME_TAX && dialOf(p, tax) == TaxPolicy.DEFAULT_INCOME_TAX,
                        baseRateOf(p, tax) + " / " + dialOf(p, tax));
            }
            for (Lever promise : PROMISES) check(which + ": " + promise + " reads zero", dialOf(p, promise) == 0, String.valueOf(dialOf(p, promise)));
            check(which + ": the wage tax's band offset is zero", p.getWageOffset(band) == 0);
            check(which + ": the minimum wage's floor binds from the first month", g.getLabourMarket().isFloorBinding());
            check(which + ": the rule has the dial from the first month", g.getDebtManager().isAutopilot());
            check(which + ": the builders' founding quote is the city's month-1 sales tax on construction",
                    Founding.foundingBuildersRate() == p.effectiveSalesRate("Construction"),
                    Founding.foundingBuildersRate() + " / " + p.effectiveSalesRate("Construction"));
        }

        /* ==================== 3. every setter takes in month 1 ==================== */
        OUT.println("\n--- 3. every setter takes a setting in month 1, and a month on nothing holds it ---");
        for (Game g : List.of(old, today)) {
            String which = g.foundedUnfitted() ? "the 1900 city" : "the default city";
            TaxPolicy p = g.getEconomyManager().getTaxPolicy();
            setEveryDial(p);
            boolean took = true;
            StringBuilder off = new StringBuilder();
            for (Lever l : INCOME_TAXES) if (Math.abs(dialOf(p, l) - setting(l)) > 1e-12) { took = false; off.append(l).append(' '); }
            for (Lever l : PROMISES) if (Math.abs(dialOf(p, l) - setting(l)) > 1e-12) { took = false; off.append(l).append(' '); }
            check(which + ": every dial of the twelve dated levers' policy takes its setting in month 1", took, off.toString());
            double floor = g.getLabourMarket().getMinimumWage() * 1.10;
            boolean floorTook = g.setMinimumWage(floor) && g.getLabourMarket().getMinimumWage() == floor;
            check(which + ": the minimum wage takes a setting through Game in month 1", floorTook,
                    floor + " / " + g.getLabourMarket().getMinimumWage());
            int lines = zoningLines(g);
            quietly(() -> g.setZone(Sectors.RETAIL, Zoning.Size.SHARE, .05, .5));
            check(which + ": a zone is drawn through Game in month 1, with its line in the decision log",
                    g.getZoning().zone(Sectors.RETAIL) != null && zoningLines(g) == lines + 1);
            quietly(g::toggleNextMonth);
            boolean stood = true;
            off.setLength(0);
            for (Lever l : INCOME_TAXES) if (Math.abs(dialOf(p, l) - setting(l)) > 1e-12) { stood = false; off.append(l).append(' '); }
            for (Lever l : PROMISES) if (Math.abs(dialOf(p, l) - setting(l)) > 1e-12) { stood = false; off.append(l).append(' '); }
            check(which + ": a month on, month " + g.getMonth() + ", every setting stands as set", stood && g.getMonth() == 2, off.toString());
            check(which + ": ...the zone stands and the floor still binds", g.getZoning().zone(Sectors.RETAIL) != null
                    && g.getLabourMarket().isFloorBinding());
        }

        /* ==================== 4. money is the player's from the founding ==================== */
        OUT.println("\n--- 4. Money is the player's from the first month; the gold years stay history's ---");
        Game gold = city("policy-open-gold", true);
        ForeignAccounts fx = gold.getForeignAccounts();
        DebtManager rates = gold.getDebtManager();
        check("the 1900 city is founded on Canada's gold at par, its dial tied to the US$", fx.isCanadaGold()
                && fx.getRegime() == ForeignAccounts.Regime.PEG && fx.getRate() == ForeignAccounts.OPENING_RATE && rates.isTied(),
                fx.getRegime() + " " + fx.getRate());
        rates.setInflationTarget(DebtManager.MAX_INFLATION_TARGET);
        gold.getCentralBank().setTargetShare(CentralBank.MAX_QE_SHARE);
        check("in month 1 the inflation target and the bank's holdings take a setting", rates.getInflationTarget() == DebtManager.MAX_INFLATION_TARGET
                && gold.getCentralBank().getTargetShare() == CentralBank.MAX_QE_SHARE);
        quietly(() -> gold.setCurrencyRegime(ForeignAccounts.Regime.FLOAT));
        check("...the dollar can be floated in month 1, which leaves Canada's gold", fx.getRegime() == ForeignAccounts.Regime.FLOAT && !fx.isCanadaGold()
                && !rates.isTied());
        double manual = DebtManager.MAX_POLICY_RATE / 10;
        rates.takeTheDial(manual);
        check("...and the dial then takes a hand's rate, from the rule", rates.getPolicyRate() == manual && !rates.isAutopilot(),
                String.valueOf(rates.getPolicyRate()));
        quietly(gold::toggleNextMonth);
        check("a month on, the float, the hand's rate, the target and the holdings stand: gold is not forced back", gold.getMonth() == 2
                && fx.getRegime() == ForeignAccounts.Regime.FLOAT && !fx.isCanadaGold() && rates.getPolicyRate() == manual
                && rates.getInflationTarget() == DebtManager.MAX_INFLATION_TARGET && gold.getCentralBank().getTargetShare() == CentralBank.MAX_QE_SHARE);
        Game now = city("policy-open-money", false);
        DebtManager nowRates = now.getDebtManager();
        check("fixture: the default city is on no gold, its dial not tied", !now.getForeignAccounts().isCanadaGold() && !nowRates.isTied());
        nowRates.takeTheDial(manual);
        nowRates.setInflationTarget(DebtManager.MAX_INFLATION_TARGET);
        now.getCentralBank().setTargetShare(CentralBank.MAX_QE_SHARE);
        quietly(() -> now.setCurrencyRegime(ForeignAccounts.Regime.LEAN));
        check("the default city's dial, target, holdings and regime take settings in month 1", nowRates.getPolicyRate() == manual
                && nowRates.getInflationTarget() == DebtManager.MAX_INFLATION_TARGET && now.getCentralBank().getTargetShare() == CentralBank.MAX_QE_SHARE
                && now.getForeignAccounts().getRegime() == ForeignAccounts.Regime.LEAN);

        /* ==================== 5. a law find opens nothing ==================== */
        OUT.println("\n--- 5. a law find opens nothing: its row says so, and a find moves no dial ---");
        ResearchCatalog cat = ResearchCatalog.get();
        List<Entry> lawRows = cat.entries().stream().filter(e -> "a Policy lever".equals(e.kindNote())).toList();
        boolean sayNothing = !lawRows.isEmpty();
        for (Entry e : lawRows) sayNothing &= e.touches().endsWith(OPENS_NOTHING);
        check("every law row of a lever (" + lawRows.size() + ") says its find opens nothing", sayNothing);
        boolean everyKeyARow = true;
        for (Lever l : Lever.values()) {
            if (l.entry().isEmpty()) continue;
            Entry e = cat.entry(l.entry());
            everyKeyARow &= e != null && lawRows.contains(e) && e.worldMonth() == PolicyDates.canadaMonth(l);
        }
        check("...and every lever's entry key is one of those rows, on Canada's month", everyKeyARow);
        lawFind();

        /* ==================== 6. nothing tells a date ==================== */
        OUT.println("\n--- 6. no screen, Policy model or notice tells a lever's date ---");
        Path tree = Path.of("src/main/java/ham/citybuildersim");
        check("fixture: the source tree is found from the working directory", Files.isDirectory(tree.resolve("ui")));
        if (Files.isDirectory(tree.resolve("ui"))) {
            List<Path> readers = new ArrayList<>();
            try (Stream<Path> ui = Files.list(tree.resolve("ui"))) { ui.filter(f -> f.toString().endsWith(".java")).forEach(readers::add); }
            for (String model : List.of("Inbox.java", "PolicyPreview.java", "FloorView.java", "ZoningView.java", "GovernmentView.java")) readers.add(tree.resolve(model));
            List<String> reading = new ArrayList<>();
            for (Path f : readers) {
                if (!Files.exists(f)) { reading.add(f.getFileName() + " (missing)"); continue; }
                String text = Files.readString(f, StandardCharsets.UTF_8);
                if (text.contains("PolicyDates") || text.contains("isLeverOpen") || text.contains("leverOpeningMonth")) reading.add(f.getFileName().toString());
            }
            check("no interface file (" + (readers.size() - 5) + ") and none of the inbox and the Policy models reads PolicyDates or a lever's opening",
                    reading.isEmpty(), String.join(", ", reading));
        }
        quietly(() -> { for (int i = 0; i < 11; i++) old.toggleNextMonth(); });
        check("the 1900 city's first year (to month " + old.getMonth() + ") raises no lever's notice", old.getMonth() == 13 && leverNotices(old) == 0);

        /* ==================== 7. the gate cannot come back by its old names ==================== */
        OUT.println("\n--- 7. none of the gate's members is declared again ---");
        Set<String> tax = declared(TaxPolicy.class), dates = declared(PolicyDates.class), game = declared(Game.class);
        List<String> back = new ArrayList<>();
        for (String n : List.of("setMonth", "isOpen", "openingMonth", "openAll", "openNow", "bringForward", "openingsForSave", "restoreOpenings",
                "closed", "held", "opening", "GATED")) if (tax.contains(n)) back.add("TaxPolicy." + n);
        for (String n : List.of("isOpen", "openingMonth", "openAtFounding", "earliestOpening", "earliestFor", "leverOf", "openingDate", "shutLine",
                "shutWhy", "notice", "stillShut", "opensAt", "MAX_EARLY_MONTHS", "isMoney")) if (dates.contains(n)) back.add("PolicyDates." + n);
        for (String n : List.of("isLeverOpen", "leverOpeningMonth", "setLeverOpenings")) if (game.contains(n)) back.add("Game." + n);
        for (String n : List.of("hearMoneyMonth", "isAutopilotAtOpening", "autopilotAtOpening", "rateOpen", "moneyOpen"))
            if (declared(DebtManager.class).contains(n)) back.add("DebtManager." + n);
        for (String n : List.of("hearMoneyMonth", "moneyOpen")) if (declared(CentralBank.class).contains(n)) back.add("CentralBank." + n);
        for (String n : List.of("moneyOpen", "moneyMonth")) if (declared(ForeignAccounts.class).contains(n)) back.add("ForeignAccounts." + n);
        if (declared(DataSave.class).contains("leverOpenings")) back.add("DataSave.leverOpenings");
        if (declared(HouseholdBalance.class).contains("studentLoansOpen")) back.add("HouseholdBalance.studentLoansOpen");
        check("no class declares a member of the gate", back.isEmpty(), String.join(", ", back));

        OUT.println("\nPolicyOpenCheck: " + fails + " failures");
        System.exit(fails == 0 ? 0 : 1);
    }

    /**
     * Section 5's city: DiscoveryCheck's 1900 village with a University and a Law School, saved in its second month and carried by its file
     * to the month before the wage tax's law (PL3) is open to the law faculty, then a pile just under PL3's size, so the next month finds it.
     */
    static void lawFind() throws Exception {
        Lever wage = Lever.WAGE_TAX;
        Entry pl3 = ResearchCatalog.get().entry(wage.entry());
        int window = PolicyDates.canadaMonth(wage) - Research.lagMonths(pl3.field());
        int month = window - 1;
        DiscoveryCheck.quiet = QUIET;
        Game start = DiscoveryCheck.cityWithSchools("policy-open-law", true, true);
        DiscoveryCheck.play(start, 1);
        DataSave saved = start.prepareSaveForChecks(0, "policy-open-law").data();
        String json = new Gson().toJson(saved);
        String moved = json.replaceFirst("\"month\": *\\d+", "\"month\":" + month);
        Files.createDirectories(start.getGameFiles().saveFile(0).getParent());
        Files.writeString(start.getGameFiles().saveFile(0), moved, StandardCharsets.UTF_8);
        Game g = new Game(start.getGameFiles());
        quietly(() -> g.loadGameSave(0));
        check("fixture: the 1900 village with a Law School is carried to month " + month + ", the one before PL3's window",
                !moved.equals(json) && g.getLoadFailure() == null && g.getMonth() == month && g.foundedUnfitted(), String.valueOf(g.getLoadFailure()));
        if (g.getLoadFailure() != null || g.getMonth() != month) return;
        quietly(() -> MaterialsCheck.handWorkers(g));
        Research r = g.getResearch();
        TaxPolicy policy = g.getEconomyManager().getTaxPolicy();
        check("fixture: PL3 is open to the law faculty next month, and the wage tax already reads the founding rate, " + (CityCalendar.yearOf(month)),
                r.isOpen(pl3, month + 1) && policy.getWageTaxRate() == TaxPolicy.DEFAULT_INCOME_TAX);
        double[] before = dials(policy);
        DiscoveryCheck.pileJustUnder(g, pl3);
        DiscoveryCheck.play(g, 1);
        check("PL3 is found in month " + g.getMonth() + ", the first the faculty could", r.foundMonth(pl3.code()) == month + 1);
        check("...and no dial of the policy moved in the find's month", Arrays.equals(before, dials(policy)),
                Arrays.toString(before) + " -> " + Arrays.toString(dials(policy)));
        DiscoveryCheck.play(g, 1);
        check("...nor in the month after", Arrays.equals(before, dials(policy)), Arrays.toString(dials(policy)));
        check("the find is told as a find, once, and no lever's opening is told", DiscoveryCheck.noticesKeyed(g, "found:" + pl3.code()).size() == 1
                && leverNotices(g) == 0);
    }
}
