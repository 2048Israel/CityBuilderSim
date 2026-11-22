package ham.citybuildersim;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Household debt as the statistics offices print it, read from the cells (HC2).
 * WHY: the average across everyone hides both debt-free families and struggling holders.
 * Amounts per adult split a household's debts evenly between its grown-ups.
 * Total holders assume independent kinds; their total is the SCF-shaped approximation.
 */
public final class DebtStatistics {
    private DebtStatistics() { }
    public enum Kind { TOTAL, CARD, STUDENT, CAR, MORTGAGE }
    public enum Stage { YOUNG, ESTABLISHED, SENIOR, ELDER, OTHER }
    /** Coarse log-CDF grid, shared by the quantile search. */
    public static final int GRID_POINTS = 32;
    /** Payment burden boundary used by the SCF. */
    public static final double HIGH_SERVICE = .40;

    public record Measure(double shareHolding, double meanAmongHolders, double medianAmongHolders,
                          double lateShare, double late60Share, double delinquency, double annualChargeOff) { }
    public record Snapshot(double households, double adults, Map<Kind, Measure> perHousehold,
                           Map<Kind, Measure> perAdult, double debtToDisposableIncome,
                           double debtToAssets, double debtServiceRatio, double payingOver40,
                           double debtorsPayingOver40, double adultsPayingOver40,
                           double adultDebtorsPayingOver40, double bankruptcies, double bankruptAdults, double forcedSales) {
        public Snapshot {
            perHousehold = Map.copyOf(perHousehold); perAdult = Map.copyOf(perAdult);
        }
        Snapshot scaled(double scale) {
            return new Snapshot(households, adults, scaledMeasures(perHousehold, scale), scaledMeasures(perAdult, scale),
                    debtToDisposableIncome, debtToAssets, debtServiceRatio, payingOver40, debtorsPayingOver40,
                    adultsPayingOver40, adultDebtorsPayingOver40, bankruptcies, bankruptAdults, forcedSales);
        }
    }
    /** Yearly surveys have their own year axis; they must not be folded as monthly series. */
    public record Survey(int month, Map<String, Snapshot> groups) {
        public Survey { groups = Map.copyOf(groups); }
        public Survey scaled(double scale) {
            Map<String, Snapshot> scaled = new LinkedHashMap<>();
            groups.forEach((k, v) -> scaled.put(k, v.scaled(scale)));
            return new Survey(month, scaled);
        }
    }

    private static Map<Kind, Measure> scaledMeasures(Map<Kind, Measure> measures, double scale) {
        Map<Kind, Measure> scaled = new EnumMap<>(Kind.class);
        measures.forEach((k, v) -> scaled.put(k, new Measure(v.shareHolding, v.meanAmongHolders * scale,
                v.medianAmongHolders * scale, v.lateShare, v.late60Share, v.delinquency, v.annualChargeOff)));
        return scaled;
    }
    record Component(double weight, double share, double mean, double sigma) { }

    public static Stage stage(Household c) {
        if (c instanceof WorkingHousehold w) return w.stage() == AdultStage.YOUNG ? Stage.YOUNG : Stage.ESTABLISHED;
        if (c.isRetired()) return c.shape() == FamilyStructure.ELDER_ALONE
                || c.shape() == FamilyStructure.ELDER_COUPLE ? Stage.ELDER : Stage.SENIOR;
        return Stage.OTHER;
    }

    /** Cars remain financed on the card; owner mortgages arrive with HO1, so those books are zero. */
    static double amount(Household c, Kind kind) {
        return switch (kind) {
            case CARD -> c.debt;
            case STUDENT -> c.studentDebt;
            case TOTAL -> c.debt + c.studentDebt;
            case CAR, MORTGAGE -> 0;
        };
    }

    static double holding(Household c, Kind kind) {
        return switch (kind) {
            case CARD -> c.debt > 0 ? c.revolving : 0;
            case STUDENT -> c.studentDebt > 0 ? 1 : 0;
            case TOTAL -> 1 - (1 - holding(c, Kind.CARD)) * (1 - holding(c, Kind.STUDENT));
            case CAR, MORTGAGE -> 0;
        };
    }

    static double sigma(Kind kind) {
        return kind == Kind.CARD ? CellSpread.CARD_SIGMA
                : kind == Kind.MORTGAGE ? CellSpread.HOME_SIGMA : CellSpread.TOTAL_SIGMA;
    }

    static double cdf(List<Component> components, double x) {
        double sum = 0, holders = 0;
        for (Component c : components) {
            double w = c.weight * c.share;
            holders += w; sum += w * CellSpread.below(x, c.mean, c.sigma);
        }
        return holders > 0 ? sum / holders : 0;
    }

    /** Locate on the log grid, then refine only that interval; no household is sampled. */
    static double median(List<Component> components) {
        double low = Double.POSITIVE_INFINITY, high = 0;
        for (Component c : components) if (c.weight * c.share > 0 && c.mean > 0) {
            low = Math.min(low, c.mean * Math.exp(-c.sigma * c.sigma / 2 - 8 * c.sigma));
            high = Math.max(high, c.mean * Math.exp(-c.sigma * c.sigma / 2 + 8 * c.sigma));
        }
        if (!(high > 0)) return 0;
        double logLow = Math.log(low), step = (Math.log(high) - logLow) / (GRID_POINTS - 1);
        double left = low, right = high;
        for (int i = 1; i < GRID_POINTS; i++) {
            right = Math.exp(logLow + step * i);
            if (cdf(components, right) >= .5) break;
            left = right;
        }
        for (int i = 0; i < 12; i++) {
            double mid = Math.sqrt(left * right);
            if (cdf(components, mid) < .5) left = mid; else right = mid;
        }
        return Math.sqrt(left * right);
    }

    public static Snapshot of(HouseholdBalance balance, PayTier tier, Stage stage) {
        return of(balance, tier, stage, false);
    }

    private static Snapshot of(HouseholdBalance balance, PayTier tier, Stage stage, boolean untiered) {
        List<Household> cells = new ArrayList<>();
        double homes = 0, adults = 0, income = 0, assets = 0, service = 0, high = 0, bankrupt = 0;
        double highAdults = 0, bankruptAdults = 0;
        for (Household c : balance.cells()) {
            if (c.grownUps() <= 0 || !(c.households > 0)
                    || tier != null && c.tier() != tier || stage != null && stage(c) != stage) continue;
            if (untiered && c.tier() != null) continue;
            cells.add(c); homes += c.households; adults += c.households * c.grownUps();
            double disposable = c.disposable + c.investmentIncome + c.depositIncome;
            income += disposable * c.households;
            assets += balance.assets(c) * c.households;
            double due = c.minimumDue + c.studentRepaid + c.studentInterest;
            service += due * c.households;
            double p = holding(c, Kind.TOTAL);
            double over = due > 0 && p > 0 ? p * CellSpread.above(
                    HIGH_SERVICE * Math.max(0, disposable), due / p, CellSpread.TOTAL_SIGMA) : 0;
            high += c.households * over;
            highAdults += c.households * c.grownUps() * over;
            bankrupt += c.bankrupt;
            bankruptAdults += c.bankrupt * c.grownUps();
        }
        Map<Kind, Measure> perHome = new EnumMap<>(Kind.class), perAdult = new EnumMap<>(Kind.class);
        double total = 0, totalHolders = 0, totalAdultHolders = 0;
        for (Kind kind : Kind.values()) {
            List<Component> h = new ArrayList<>(), a = new ArrayList<>();
            double debt = 0, holders = 0, adultHolders = 0, lateH = 0, lateA = 0, lateDebt = 0, off = 0;
            for (Household c : cells) {
                double p = holding(c, kind), d = amount(c, kind), n = c.households;
                debt += n * d; holders += n * p; adultHolders += n * c.grownUps() * p;
                if (p > 0) {
                    h.add(new Component(n, p, d / p, sigma(kind)));
                    a.add(new Component(n * c.grownUps(), p, d / p / c.grownUps(), sigma(kind)));
                }
                if (kind == Kind.CARD || kind == Kind.TOTAL) {
                    lateH += n * c.lateShare; lateA += n * c.grownUps() * c.lateShare;
                    lateDebt += n * c.lateDebt; off += n * c.cardWrittenOff;
                }
            }
            double delinquency = ratio(lateDebt, debt), chargeOff = ratio(12 * off, debt);
            // Single-stock approximation: two completed months surviving cure/write-off.
            double late60 = Math.pow(1 - Bank.CARD_CURE - 1 / Bank.CARD_CHARGE_OFF_MONTHS, 2);
            perHome.put(kind, new Measure(ratio(holders, homes), ratio(debt, holders), median(h),
                    ratio(lateH, homes), ratio(lateH, homes) * late60, delinquency, chargeOff));
            perAdult.put(kind, new Measure(ratio(adultHolders, adults), ratio(debt, adultHolders), median(a),
                    ratio(lateA, adults), ratio(lateA, adults) * late60, delinquency, chargeOff));
            if (kind == Kind.TOTAL) { total = debt; totalHolders = holders; totalAdultHolders = adultHolders; }
        }
        return new Snapshot(homes, adults, perHome, perAdult, ratio(total, 12 * income),
                ratio(total, assets), ratio(service, income), ratio(high, homes), ratio(high, totalHolders),
                ratio(highAdults, adults), ratio(highAdults, totalAdultHolders), bankrupt, bankruptAdults, 0);
    }

    /** Students and other households without a pay tier, on the same stage axis. */
    public static Snapshot untiered(HouseholdBalance balance, Stage stage) {
        return of(balance, null, stage, true);
    }

    public static Survey survey(HouseholdBalance balance, int month) {
        Map<String, Snapshot> groups = new LinkedHashMap<>();
        groups.put("All", of(balance, null, null));
        for (PayTier t : PayTier.values()) {
            groups.put(t.name(), of(balance, t, null));
            for (Stage s : Stage.values()) groups.put(t.name() + ":" + s.name(), of(balance, t, s));
        }
        for (Stage s : Stage.values()) groups.put(s.name(), of(balance, null, s));
        groups.put("UNTIERED", of(balance, null, null, true));
        for (Stage s : Stage.values()) groups.put("UNTIERED:" + s.name(), of(balance, null, s, true));
        return new Survey(month, groups);
    }

    private static double ratio(double a, double b) { return b > 0 ? a / b : 0; }
}
