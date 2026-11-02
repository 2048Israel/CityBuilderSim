package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The Partners board's and a nation's page's figures (WN6; docs/studies/spec-nations.md 8, boards 2 and 3, D29): the table of who the city trades with, by
 * nation, for the month or the year so far, and one nation's full page. A pure model of what a screen draws, built from the game's public getters alone
 * and read from the screen's thread (the snapshot it starts from is immutable; the rest are the figures the map card already reads).
 * <p>
 * WHY. CLAUDE.md, "A screen section": every model figure through a public getter, never recomputed in the screen. The Partners board was to rank nations,
 * name each one's money, its policy rate, its distance and its land, and foot to the month; a screen that did that itself would have its own idea of the
 * year, of the money, of what the rank counts. The page is the map card's: its rows ARE NationsMap.figureRows (the same words, the same ranks by
 * NationsMap.YearFigures), and the ranks are asserted to be the card's (PartnersCheck).
 * <p>
 * UNITS. Money in the trade is thousands of the city's money (the model counts money in thousands); tonnes are tonnes; distances kilometres along the
 * great circle from the city's site to the nation's node; a rate is the nation's money a US$ (units a US$) and D$ a unit is the D$ a US$ over it; a policy
 * rate is a fraction a year; land is square kilometres and US$ (the tally keeps thousands; the page whole US$). Nations are numbered from 1.
 */
public final class PartnersView {

    private PartnersView() { }

    /** The span of the trade a table is of: the month last tallied, or the calendar year to date. */
    public enum Span { MONTH, YEAR }

    /**
     * One nation of the table. sold is what the city sold it and bought what the city bought of it, thousands of the city's money; tonnesOut and tonnesIn
     * the same in tonnes. money is the ISO code of the money the nation uses this year and mark what a figure in it is written with
     * (ForeignAccounts.MONEY_MARK for the eight, the ISO code for the rest); unitsPerUsd that money a US$ this month (the moneys' rate, else the file's for
     * the year, as the map card reads it) and dollarsPerUnit the D$ one unit is worth. policyRate is its central bank's, a fraction a year, where the money
     * is one of the eight, else NaN. distanceKm is from the city's site to the nation's node. landFactor is the nation's land factor now and landMark how
     * sure the table is of its price (WorldData.MARK_*; 0 and NaN where the city has no land sellers); landKm2 and landUsd what the city bought from it
     * since founding, km2 and US$.
     */
    public record Row(int rank, int nation, String name, String iso3, double sold, double bought, double tonnesOut, double tonnesIn,
                      String money, String mark, double unitsPerUsd, double dollarsPerUnit, double policyRate, double distanceKm,
                      double landFactor, int landMark, double landKm2, double landUsd) {
        /** What the city sold it and bought of it together: what the rows are ranked by. */
        public double total() { return sold + bought; }
    }

    /**
     * The table's totals: how many nations the city sold to and how many it bought of, the sums of the rows, and the trade that no partner took (the
     * month's: a good the market has no buyers or sellers of, which the tally keeps apart so that the rows plus these are the month). For the YEAR the
     * tally keeps no remainder: noPartnerSold and noPartnerBought are NaN, and the rows are the year's trade by nation.
     */
    public record Totals(int nationsSoldTo, int nationsBoughtFrom, double sold, double bought, double tonnesOut, double tonnesIn,
                         double noPartnerSold, double noPartnerBought) { }

    /** The Partners table for a span: the month's number (the game's) and the calendar year it is in, the rows largest first, and the totals. */
    public record Table(Span span, int month, int year, List<Row> rows, Totals totals) { }

    /**
     * The table of who the city trades with, by nation, for a span; null where the city has no market (no world file, or it is not built yet). A row for
     * each nation with any trade in the span, ranked by what the city sold it and bought of it together, largest first (a tie by the nation's number).
     */
    public static Table table(Game g, Span span) {
        PartnerTrade.Snapshot snap = g.getPartnerTrade();
        Nations nations = g.getNations();
        WorldMarket market = g.getWorldMarket();
        if (snap == null || nations == null || market == null) return null;
        boolean month = span == Span.MONTH;
        double[] sold = month ? snap.exportValue() : snap.yearExportValue(), bought = month ? snap.importValue() : snap.yearImportValue();
        double[] out = month ? snap.exportTonnes() : snap.yearExportTonnes(), in = month ? snap.importTonnes() : snap.yearImportTonnes();
        Ctx ctx = new Ctx(g, nations, market, snap);
        List<Row> rows = new ArrayList<>();
        int soldTo = 0, boughtFrom = 0;
        double totalSold = 0, totalBought = 0, totalOut = 0, totalIn = 0;
        for (int i = 0; i < sold.length && i < nations.count(); i++) {
            if (!(sold[i] > 0 || bought[i] > 0 || out[i] > 0 || in[i] > 0)) continue;
            rows.add(row(ctx, i + 1, 0, sold[i], bought[i], out[i], in[i]));
            if (sold[i] > 0) soldTo++;
            if (bought[i] > 0) boughtFrom++;
            totalSold += sold[i];
            totalBought += bought[i];
            totalOut += out[i];
            totalIn += in[i];
        }
        rows.sort(Comparator.comparingDouble((Row r) -> -r.total()).thenComparingInt(Row::nation));
        List<Row> ranked = new ArrayList<>(rows.size());
        for (int k = 0; k < rows.size(); k++) {
            Row r = rows.get(k);
            ranked.add(new Row(k + 1, r.nation(), r.name(), r.iso3(), r.sold(), r.bought(), r.tonnesOut(), r.tonnesIn(), r.money(), r.mark(), r.unitsPerUsd(),
                    r.dollarsPerUnit(), r.policyRate(), r.distanceKm(), r.landFactor(), r.landMark(), r.landKm2(), r.landUsd()));
        }
        Totals totals = new Totals(soldTo, boughtFrom, totalSold, totalBought, totalOut, totalIn,
                month ? snap.noPartnerExports() : Double.NaN, month ? snap.noPartnerImports() : Double.NaN);
        return new Table(span, snap.month(), snap.year(), List.copyOf(ranked), totals);
    }

    /** What every row of a table (and a page) reads from the game, once. */
    private static final class Ctx {
        final Game g;
        final Nations nations;
        final WorldMarket market;
        final int year;
        final double dollarsAUsd, siteLat, siteLon;
        final WorldMoneys moneys;
        final PolicyRates policy;
        final LandFactors land;

        Ctx(Game g, Nations nations, WorldMarket market, PartnerTrade.Snapshot snap) {
            this.g = g;
            this.nations = nations;
            this.market = market;
            this.year = Nations.yearOf(g.getMonth());
            this.dollarsAUsd = g.getForeignAccounts().getRate();
            this.moneys = g.getWorldMoneys();
            this.policy = g.getPolicyRates();
            LandSellers sellers = g.getLandSellers();
            this.land = sellers == null ? null : sellers.factors();
            double lat = g.getFounding().getEarthLat(), lon = g.getFounding().getEarthLon();
            if (!(Double.isFinite(lat) && Double.isFinite(lon))) {
                // a generated world's city stands at its stand-in nation's node (WorldMarket.setStandIn; the snapshot keeps it for the map's edge)
                lat = snap.standInLat();
                lon = snap.standInLon();
                if (!(Double.isFinite(lat) && Double.isFinite(lon))) {
                    lat = market.nodeLat(market.host());
                    lon = market.nodeLon(market.host());
                }
            }
            this.siteLat = lat;
            this.siteLon = lon;
        }

        /** The nation's money a US$ this month: the moneys' rate where it is a number above 0, else the file's for the year (the map card's rule). */
        double unitsPerUsd(Nations.Year y, int nation) {
            double rate = moneys == null ? Double.NaN : moneys.rateOfNation(nation);
            return rate > 0 && !Double.isInfinite(rate) ? rate : y.rateToUsd();
        }

        double policyRate(Nations.Year y) {
            return policy == null || y.money() == null ? Double.NaN : policy.rate(y.money());
        }

        double distanceKm(int nation) {
            return Double.isFinite(siteLat) && Double.isFinite(siteLon)
                    ? EarthProj.distanceM(siteLat, siteLon, market.nodeLat(nation), market.nodeLon(nation)) / 1000 : Double.NaN;
        }
    }

    private static String markOf(String money) {
        int k = ForeignAccounts.moneyIndex(money);
        return k >= 0 ? ForeignAccounts.MONEY_MARK[k] : money == null ? "" : money;
    }

    private static Row row(Ctx c, int nation, int rank, double sold, double bought, double tonnesOut, double tonnesIn) {
        Nations.Year y = c.nations.year(nation, c.year);
        double units = c.unitsPerUsd(y, nation);
        double perUnit = units > 0 && c.dollarsAUsd > 0 ? c.dollarsAUsd / units : Double.NaN;
        return new Row(rank, nation, c.nations.name(nation), c.nations.iso3(nation), sold, bought, tonnesOut, tonnesIn, y.money(), markOf(y.money()),
                units > 0 ? units : Double.NaN, perUnit, c.policyRate(y), c.distanceKm(nation),
                c.land == null ? Double.NaN : c.land.factorAt(nation, c.g.getMonth()), c.land == null ? WorldData.MARK_NONE : c.land.mark(nation),
                c.g.getLandBoughtKm2(nation), c.g.getLandBoughtUsd(nation) * 1000);
    }

    /** One finished year of the city's trade with a nation: what it sold there and bought of it, thousands of the city's money. */
    public record YearRow(int year, double valueOut, double valueIn) { }

    /**
     * A nation's full page. figureRows are the map card's rows (NationsMap.figureRows: GDP, People and GDP a head with their ranks, This year, Its money,
     * and the policy rate where it has one), and the numbers beside them are what the rows were written from: output (US$ of 2015) with the file's mark,
     * people, outputPerHead, the ranks of the count nations, growth (a share, NaN in the first year) and inflation (a share). unitsPerUsd, dollarsPerUnit,
     * policyRate and distanceKm are the table row's. monthSold/Bought and yearSold/Bought are the city's trade with it (thousands), yearTonnesOut/In the
     * year's tonnes; goods is this year's trade with it by good, largest first (value out plus in, a tie by the good's order), and years its finished years,
     * oldest first. landKm2 and landUsd are what the city bought from it, landFactor and landMark its land factor now and how sure the table is, and
     * landUsdPerHectare the published price of a hectare, US$ of the table's year (NaN with no land sellers).
     * <p>
     * vaultIndex is the money's place among the eight (ForeignAccounts.moneyIndex), -1 where its money is none of them; then vaultHeld is what the vault
     * holds of it in its own units, vaultHeldUsd that in US$, owedOwn what the city owes in it (DebtManager.getForeignPrincipalIn, CD8: the principal of
     * every foreign piece written in it, its own units) and owedUsd that at the vault's rate for it; all thousands, NaN where there is none to read.
     * <p>
     * THE COMPARISON D29 ASKS FOR: cityGdpUsd is the city's GDP in real 2015 US$ a year (Game.realGdp2015Usd(), the last finished MONTH's GDP in thousands of
     * real 2015 US$, times 12 and 1,000: a year at that month's pace, in whole US$), set beside the nation's output, which is US$ of 2015 as the file has
     * it, and gdpShare is the first over the second.
     */
    public record Page(int nation, String name, String iso3, int year, int count, List<NationsMap.Row> figureRows,
                       double output, int outputMark, double people, int peopleMark, double outputPerHead, int rankOutput, int rankPeople, int rankHead,
                       double growth, double inflation, String money, String mark, double unitsPerUsd, double dollarsPerUnit, double policyRate, double distanceKm,
                       double monthSold, double monthBought, double yearSold, double yearBought, double yearTonnesOut, double yearTonnesIn,
                       List<PartnerTrade.Cell> goods, List<YearRow> years,
                       double landKm2, double landUsd, double landFactor, int landMark, double landUsdPerHectare,
                       int vaultIndex, double vaultHeld, double vaultHeldUsd, double owedOwn, double owedUsd,
                       double cityGdpUsd, double gdpShare) { }

    /** A nation's full page; null where the city has no market or the number is not a nation's. */
    public static Page nation(Game g, int nation) {
        PartnerTrade.Snapshot snap = g.getPartnerTrade();
        Nations nations = g.getNations();
        WorldMarket market = g.getWorldMarket();
        if (snap == null || nations == null || market == null || nation < 1 || nation > nations.count()) return null;
        Ctx c = new Ctx(g, nations, market, snap);
        int i = nation - 1, year = c.year;
        Nations.Year y = nations.year(nation, year);
        NationsMap.YearFigures ranks = new NationsMap.YearFigures(nations, year);
        double units = c.unitsPerUsd(y, nation);
        List<NationsMap.Row> rows = NationsMap.figureRows(nations, nation, year, c.dollarsAUsd, c.moneys == null ? Double.NaN : c.moneys.rateOfNation(nation),
                c.policy, ranks.rankOutput[i], ranks.rankPeople[i], ranks.rankHead[i]);
        List<PartnerTrade.Cell> goods = new ArrayList<>();
        for (PartnerTrade.Cell cell : snap.yearCells()) if (cell.nation() == nation) goods.add(cell);
        goods.sort(Comparator.comparingDouble((PartnerTrade.Cell cell) -> -(cell.valueOut() + cell.valueIn())).thenComparingInt(cell -> cell.good().ordinal()));
        List<YearRow> years = new ArrayList<>();
        for (PartnerTrade.Table t : snap.history()) {
            double out = 0, in = 0;
            for (PartnerTrade.Cell cell : t.cells()) {
                if (cell.nation() != nation) continue;
                out += cell.valueOut();
                in += cell.valueIn();
            }
            years.add(new YearRow(t.year(), out, in));
        }
        int k = ForeignAccounts.moneyIndex(y.money());
        ForeignAccounts fx = g.getForeignAccounts();
        double owedOwn = k >= 0 ? g.getDebtManager().getForeignPrincipalIn(y.money()) : Double.NaN;
        double usdPer = k >= 0 ? fx.getMoneyUsd(k) : Double.NaN;
        double cityGdp = g.realGdp2015Usd() * 12 * 1000;
        return new Page(nation, nations.name(nation), nations.iso3(nation), year, nations.count(), List.copyOf(rows),
                y.output(), y.outputMark(), y.people(), y.peopleMark(), y.outputPerHead(), ranks.rankOutput[i], ranks.rankPeople[i], ranks.rankHead[i],
                Math.expm1(nations.growth(nation, year)), y.inflation(), y.money(), markOf(y.money()), units > 0 ? units : Double.NaN,
                units > 0 && c.dollarsAUsd > 0 ? c.dollarsAUsd / units : Double.NaN, c.policyRate(y), c.distanceKm(nation),
                snap.exportValue()[i], snap.importValue()[i], snap.yearExportValue()[i], snap.yearImportValue()[i], snap.yearExportTonnes()[i], snap.yearImportTonnes()[i],
                List.copyOf(goods), List.copyOf(years),
                g.getLandBoughtKm2(nation), g.getLandBoughtUsd(nation) * 1000, c.land == null ? Double.NaN : c.land.factorAt(nation, g.getMonth()),
                c.land == null ? WorldData.MARK_NONE : c.land.mark(nation), c.land == null ? Double.NaN : c.land.usdPerHectare(nation),
                k, k >= 0 ? fx.getHolding(k) : Double.NaN, k >= 0 ? fx.getHoldingUsd(k) : Double.NaN, owedOwn, k >= 0 ? owedOwn * usdPer : Double.NaN,
                cityGdp, y.output() > 0 ? cityGdp / y.output() : Double.NaN);
    }
}
