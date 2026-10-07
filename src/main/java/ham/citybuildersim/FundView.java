package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * The city's fund as the screens read it (0.7.39): every holding with its average cost and P&L, what the fund has made since it began and by kind, its return over a chart's window, the market to search, a hand order's quote on today's book, and its record - pure, and the screens' only door to the fund's cost basis.
 *
 * WHY. Jerus, the morning after 0.7.38: "when you click buy manually i want it
 * to be like wealthsimple trade type kinda like a brokerage, where you can
 * search the shares and bonds and see and all, and also the city fund should
 * show pnl and acb and all that". The Fund page showed what the fund held at
 * its marks and nothing of what it had cost; a hand order was a chip and a
 * button at fair value, with nothing to say what it would meet on the book.
 * The project's spec-fund-0739.md, sections 3.4 and 4.
 *
 * EVERY FIGURE IS THE MODEL'S. A holding's units are the register's and the
 * bonds' (Equity, CorporateBond.city()), its price the city's mark - the
 * exchange's last trade, or fair value before one or once it is a year old
 * (Exchange.cityMark(), 0.7.48) - or the bond market's valuation
 * (BondMarket.modelPrice()), its cost FundLedger's (or TreasuryFund's for the
 * rescue book), its income the ledger's and the counters'. The fund's gain
 * since it began, and by kind, is the counters' alone - exact on any save,
 * whatever the ledger knows. Nothing here writes anything: BuildCard's shape,
 * so a probe reads every figure a page draws.
 */
public final class FundView {

    private FundView() { }

    /* ============================== the dials ============================== */

    /** A share's last trade older than this many months is called stale on every page that shows it (the spec's B3: a price is its last trade, however old - and since 0.7.48 the city's own holding is marked at fair value once that trade is Exchange.STALE_MARK_MONTHS old). */
    public static final int STALE_MONTHS = 3;

    /** The least a price per founding share, as the history records it to four places, can be and still carry three significant figures: a move off less is not shown (the spec's B6 - a consolidated share records 0.0000). */
    public static final double MIN_RECORDED_PRICE = .01;

    /** A holding worth less than this, money, is folded into "and N more" on the Portfolio page: a thousand dollars. */
    public static final double FOLD_UNDER = 1;

    /** The months a hit's sparkline and its move cover: a year. */
    public static final int MOVE_MONTHS = 12;

    /** What a holding is. */
    public static final String SHARE = "share", RESCUE = "rescue", BOND = "bond", PREFERRED = "preferred", WARRANTS = "warrants";

    /** A search hit that is a lot the fund closed on a bond no longer on any book: found only when named (the spec's D1). */
    public static final String CLOSED = "closed";

    /* ============================== a position ============================== */

    /**
     * One holding, as the pages draw it: its key and name, what it is (and
     * for a share the company's index, for a bond its id); the units held
     * (shares, or face), its price a unit and the month of that price (-1:
     * fair value, it has never traded; for a bond, the month it is valued
     * in), fair value a unit (a bond's: its value), its worth; its ACB, the
     * average a unit, the unrealized gain in money and as a share of the ACB
     * (NaN with no cost); the realized gain and the income since `since`,
     * the income before it; whether its cost was counted at market value
     * when tracking began; the city's stake in the company (both books) and
     * its market book's share; and for a bond its issuer, coupon and maturity.
     */
    public record Position(String key, String name, String kind, int company, int bondId,
                           double units, double price, int priceMonth, double fair, double value,
                           double acb, double average, double unrealized, double unrealizedPct,
                           double realized, double income, double incomeBefore, int since, boolean seeded,
                           double stake, double marketBookShare, String issuer, double coupon, int maturity) {
        /** Unrealized, realized and income together: what the holding has made since its cost began. */
        public double totalReturn() {
            return (Double.isNaN(unrealized) ? 0 : unrealized) + realized + income;
        }
        public boolean isShare()  { return SHARE.equals(kind) || RESCUE.equals(kind); }
        public boolean isBond()   { return BOND.equals(kind); }
        /** True when its price is a last trade older than STALE_MONTHS. */
        public boolean stale(int month) { return isShare() && priceMonth >= 0 && month - priceMonth > STALE_MONTHS; }
    }

    /** Every holding the fund has now, each lot its own row: the market books, the rescue book, each bond, the preferred and the warrants while held - in register order, then bonds by id. */
    public static List<Position> positions(Game g) {
        List<Position> out = new ArrayList<>();
        Equity reg = g.getEquity();
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            Position m = sharePosition(g, c, false), r = sharePosition(g, c, true);
            if (m != null) out.add(m);
            if (r != null) out.add(r);
        }
        BondMarket bm = g.getBondMarket();
        for (CorporateBond b : bm.getBonds()) {
            Position p = bondPosition(g, b);
            if (p != null) out.add(p);
        }
        Position pref = preferredPosition(g), warr = warrantsPosition(g);
        if (pref != null) out.add(pref);
        if (warr != null) out.add(warr);
        return out;
    }

    /** One company's market book (rescue false) or rescue book, or null when it holds none. */
    public static Position sharePosition(Game g, int c, boolean rescue) {
        Equity reg = g.getEquity();
        Exchange ex = g.getExchange();
        TreasuryFund f = g.getFund();
        FundLedger ledger = f.getLedger();
        String name = Equity.COMPANIES[c];
        double units = rescue ? reg.getCityRescueShares(c) : reg.getCityMarketShares(c);
        if (!(units > FundLedger.DUST)) return null;
        FundLedger.Lot lot = ledger.lot(rescue ? FundLedger.rescueKey(name) : FundLedger.shareKey(name));
        // The city's mark (0.7.48, C4): the last trade, or fair value once that is STALE_MARK_MONTHS old.
        double price = ex.cityMark(c);
        int priceMonth = ex.hasTraded(c) ? ex.bookOf(c).lastTradeMonth() : -1;
        double value = units * price;
        // The rescue book's cost is the fund's own record of it; a market lot's the ledger's.
        double acb = rescue ? f.getRescueCost() : lot == null ? Double.NaN : lot.acb();
        double shares = reg.getShares(c);
        return new Position(rescue ? FundLedger.rescueKey(name) : FundLedger.shareKey(name),
                name, rescue ? RESCUE : SHARE, c, -1, units, price, priceMonth, ex.fair(c), value,
                acb, acb / units, value - acb, acb > 0 ? (value - acb) / acb : Double.NaN,
                lot == null ? 0 : lot.realized(), lot == null ? 0 : lot.income(), lot == null ? 0 : lot.incomeBefore(),
                lot == null ? -1 : lot.since(), lot != null && lot.seeded(),
                reg.cityShare(c), shares > 0 ? reg.getCityMarketShares(c) / shares : 0, null, 0, -1);
    }

    /** One bond the fund holds face of, or null. */
    public static Position bondPosition(Game g, CorporateBond b) {
        if (b == null || !(b.city() > FundLedger.DUST)) return null;
        int month = g.getMonth();
        BondMarket bm = g.getBondMarket();
        FundLedger.Lot lot = g.getFund().getLedger().lot(FundLedger.bondKey(b.id()));
        double price = bm.modelPrice(b, month);
        double value = b.city() * price;
        double acb = lot == null ? Double.NaN : lot.acb();
        return new Position(FundLedger.bondKey(b.id()), bondName(b), BOND, -1, b.id(), b.city(), price, month, price, value,
                acb, acb / b.city(), value - acb, acb > 0 ? (value - acb) / acb : Double.NaN,
                lot == null ? 0 : lot.realized(), lot == null ? 0 : lot.income(), 0,
                lot == null ? -1 : lot.since(), lot != null && lot.seeded(),
                b.face() > 0 ? b.city() / b.face() : 0, b.face() > 0 ? b.city() / b.face() : 0,
                b.issuer(), b.coupon(), b.maturityMonth());
    }

    /** The bank's preferred while any is outstanding: at par, its cost (the treasury paid par), its dividends; realized what a failure cancelled. */
    public static Position preferredPosition(Game g) {
        double par = g.fundPreferredValue();
        if (!(par > 0)) return null;
        TreasuryFund f = g.getFund();
        int since = -1;
        for (Bank.Preferred p : g.getBank().getPreferred()) if (p.par() > 0) since = since < 0 ? p.issued() : Math.min(since, p.issued());
        return new Position(FundLedger.PREFERRED_KEY, "The bank's preferred", PREFERRED, Equity.BANK, -1, par, 1, g.getMonth(), 1, par,
                par, 1, 0, 0, -preferredCancelled(f), f.getPreferredDividends(), 0, since, false,
                0, 0, null, Bank.PREFERRED_RATE, -1);
    }

    /** The warrants on the bank's shares while any are out: they cost nothing, so all they are worth is unrealized and all they brought back realized. */
    public static Position warrantsPosition(Game g) {
        double v = g.fundWarrantsValue();
        double shares = g.getBank().warrantSharesOut();
        if (!(v > 0) && !(shares > 0)) return null;
        return new Position(FundLedger.WARRANTS_KEY, "Warrants on the bank", WARRANTS, Equity.BANK, -1, shares,
                shares > 0 ? v / shares : 0, g.getMonth(), 0, v, 0, 0, v, Double.NaN,
                g.getFund().getWarrantsBoughtBack(), 0, 0, -1, false, 0, 0, null, 0, -1);
    }

    /** The par a failure cancelled, every resolution together. */
    static double preferredCancelled(TreasuryFund f) {
        double t = 0;
        for (TreasuryFund.Resolution r : f.getResolutions()) t += r.preferredCancelled();
        return t;
    }

    /** A bond's name: "#142 Automotive 2.81% 2059". */
    public static String bondName(CorporateBond b) {
        return String.format(Locale.ROOT, "#%d %s %.2f%% %d", b.id(), b.issuer(), b.coupon() * 100,
                CityCalendar.yearOf(b.maturityMonth()));
    }

    /** One position by its key, held now, or null. */
    public static Position position(Game g, String key) {
        if (key == null) return null;
        if (key.equals(FundLedger.PREFERRED_KEY)) return preferredPosition(g);
        if (key.equals(FundLedger.WARRANTS_KEY)) return warrantsPosition(g);
        if (key.startsWith("B:")) {
            int id = bondIdOf(key);
            return bondPosition(g, g.getBondMarket().bond(id));
        }
        int c = companyOf(key);
        if (c < 0) return null;
        return sharePosition(g, c, key.endsWith(":rescue"));
    }

    /** The company a share key names, or -1. */
    public static int companyOf(String key) {
        if (key == null || !key.startsWith("S:")) return -1;
        String name = key.substring(2);
        if (name.endsWith(":rescue")) name = name.substring(0, name.length() - ":rescue".length());
        for (int c = 0; c < Equity.COMPANIES.length; c++) if (Equity.COMPANIES[c].equals(name)) return c;
        return -1;
    }

    /** The bond a bond key names, or -1. */
    public static int bondIdOf(String key) {
        if (key == null || !key.startsWith("B:")) return -1;
        try { return Integer.parseInt(key.substring(2)); } catch (NumberFormatException e) { return -1; }
    }

    /** A lot the fund has closed - sold out, repaid, written off, passed to the rescue book - with what it realized and paid. Newest first. */
    public static List<FundLedger.Lot> closedLots(Game g) {
        List<FundLedger.Lot> out = new ArrayList<>();
        for (FundLedger.Lot l : g.getFund().getLedger().getLots()) if (l.isClosed()) out.add(l);
        out.sort(Comparator.comparingInt(FundLedger.Lot::closedMonth).reversed());
        return out;
    }

    /** The dividend a share pays a year over what a share cost: the rescue book's yield on cost (the spec's 5). NaN without a cost. */
    public static double yieldOnCost(Game g, Position p) {
        if (p == null || !p.isShare() || !(p.average() > 0)) return Double.NaN;
        return g.getEquity().dividendPerShareAnnual(p.company()) / p.average();
    }

    /* ============================== the fund ============================== */

    /** One kind's line of SINCE IT BEGAN, BY KIND: what went into it, what came back out, what it is worth, its income and its gain. */
    public record Kind(String name, double putIn, double gotBack, double worth, double income, double gain, String note) { }

    /**
     * The fund as a whole: its worth and cash; last month's income and its
     * transfer (and what of it went unpaid); since it began - what the city
     * put in (pay-ins, rescues paid, preferred bought), what it took out
     * (transfers, draw-outs), the gain (worth + taken out - put in) and the
     * multiple ((worth + taken out) / put in); and the gain by kind, which
     * adds up to the whole. Exact from the counters on any save.
     */
    public record Portfolio(double value, double cash, double incomeLastMonth, double transferLastMonth,
                            double transferShort, double putIn, double takenOut, double gain, double multiple,
                            double sharesValue, double bondsValue, double rescueValue, List<Kind> kinds,
                            double reserved, double cashFree) { }

    public static Portfolio portfolio(Game g) {
        TreasuryFund f = g.getFund();
        double value = g.fundValue();
        double putIn = f.getPutIn(), takenOut = f.getTakenOut();
        double gain = value + takenOut - putIn;
        List<Kind> kinds = new ArrayList<>();
        double mkt = g.fundMarketSharesValue();
        double inMkt = f.getSharesBought(), backMkt = f.getSharesSold() - f.getRescueSold();
        kinds.add(new Kind("Company shares", inMkt, backMkt, mkt, f.getDividendsMarket(),
                mkt + backMkt + f.getDividendsMarket() - inMkt, null));
        double rescue = g.fundRescueSharesValue();
        kinds.add(new Kind("The rescue book", f.getRescuesPaid(), f.getRescueSold(), rescue, f.getDividendsRescue(),
                rescue + f.getRescueSold() + f.getDividendsRescue() - f.getRescuesPaid(), null));
        double bonds = g.fundBondsValue();
        double backBonds = f.getBondsSold() + f.getPrincipal();
        kinds.add(new Kind("Company bonds", f.getBondsBought(), backBonds, bonds, f.getCoupons(),
                bonds + backBonds + f.getCoupons() - f.getBondsBought(),
                f.getBondFaceLost() > 0 ? "face lost to defaults" : null));
        double pw = g.fundPreferredValue() + g.fundWarrantsValue();
        double backPw = f.getPreferredRedeemed() + f.getWarrantsBoughtBack();
        kinds.add(new Kind("Preferred & warrants", f.getPreferredBought(), backPw, pw, f.getPreferredDividends(),
                pw + backPw + f.getPreferredDividends() - f.getPreferredBought(),
                preferredCancelled(f) > 0 ? "cancelled by a rescue" : null));
        // What the treasury had of it last month: the month's own and, over the default withdrawal, the month before's sale (0.7.48).
        return new Portfolio(value, f.getCash(), f.getMonthDividends() + f.getMonthCoupons(), f.getTransferPaid() + f.getTransferPaidLate(),
                f.getTransferShort(), putIn, takenOut, gain, putIn > 0 ? (value + takenOut) / putIn : Double.NaN,
                g.fundMarketSharesValue(), bonds, g.fundRescueValue(), kinds, f.handReserve(), g.fundCashFree());
    }

    /* ============================== the return over a window ============================== */

    /**
     * What the fund made over a window of months: the gain in money - its
     * worth at the end less at the start less what was put in net between -
     * and as a return, Modified Dietz (each month's net flow weighted by the
     * share of the window left after it): (V1 - V0 - sum F) / (V0 + sum w F).
     * NaN as a return when the start was not recorded (an older save's
     * months), with `recordedFrom` the first month that was.
     */
    public record RangeReturn(double gain, double pct, int fromMonth, int toMonth, int recordedFrom) {
        public boolean recorded() { return !Double.isNaN(pct); }
    }

    public static RangeReturn rangeReturn(HistorySave h, int fromMonth, int toMonth) {
        List<Integer> axis = h.getMonth();
        double[] v = h.aligned("fundValue"), in = h.aligned("fundPutIn"), out = h.aligned("fundTakenOut");
        int recordedFrom = -1;
        for (int i = 0; i < v.length; i++) if (!Double.isNaN(v[i])) { recordedFrom = axis.get(i); break; }
        if (axis.isEmpty()) return new RangeReturn(Double.NaN, Double.NaN, fromMonth, toMonth, recordedFrom);
        int i0 = ChartModel.nearest(axis, fromMonth), i1 = ChartModel.nearest(axis, toMonth);
        if (i0 < 0 || i1 < 0) return new RangeReturn(Double.NaN, Double.NaN, fromMonth, toMonth, recordedFrom);
        // A window that starts before the record: from the first month recorded inside it.
        while (i0 < i1 && Double.isNaN(v[i0])) i0++;
        int from = axis.get(i0), to = axis.get(i1);
        if (Double.isNaN(v[i0]) || Double.isNaN(v[i1]) || i1 <= i0) return new RangeReturn(Double.NaN, Double.NaN, from, to, recordedFrom);
        double flows = 0, weighted = 0;
        // Weighted by the MONTHS left after each flow, read off the axis
        // (0.7.55): past five hundred years an entry is a year
        // (HistorySave.foldOldYears()). On an axis of months it is the
        // entries counted, as it always was.
        int span = to - from;
        for (int i = i0 + 1; i <= i1; i++) {
            double fi = (in[i] - in[i - 1]) - (out[i] - out[i - 1]);
            if (Double.isNaN(fi)) continue;
            flows += fi;
            weighted += fi * (double) (to - axis.get(i)) / span;
        }
        double gain = v[i1] - v[i0] - flows;
        double base = v[i0] + weighted;
        return new RangeReturn(gain, base > 0 ? gain / base : Double.NaN, from, to, recordedFrom);
    }

    /** The fund's net put in, month by month - what went in less what came out, cumulative (HistorySave's fundPutIn less fundTakenOut) - for the Portfolio chart's second line; NaN where not recorded. */
    public static double[] netPutIn(HistorySave h) {
        double[] in = h.aligned("fundPutIn"), out = h.aligned("fundTakenOut");
        double[] net = new double[in.length];
        for (int i = 0; i < net.length; i++) net[i] = in[i] - out[i];
        return net;
    }

    /* ============================== a security's history ============================== */

    /**
     * A company's price history a share as it is today: the last trade and
     * fair value per founding share (the history's sharePrice and
     * shareValue) over today's split factor, so the line runs through every
     * split at today's count; and the fund's average cost a share, flat from
     * the month its cost began (NaN before, and with no lot). Aligned to the
     * history's months: {last trade, fair value, average cost}.
     */
    public static double[][] sharePrices(Game g, int c) {
        HistorySave h = g.getHistorySave();
        String name = Equity.COMPANIES[c];
        double k = g.getExchange().getSplitFactor(c);
        double[] last = h.aligned(HistorySave.priceKey(name)), fair = h.aligned(HistorySave.valueKey(name));
        double[] avg = new double[last.length];
        java.util.Arrays.fill(avg, Double.NaN);
        Position p = sharePosition(g, c, false);
        if (p == null) p = sharePosition(g, c, true);
        if (p != null && p.average() > 0) {
            int since = p.since();
            List<Integer> axis = h.getMonth();
            for (int i = 0; i < avg.length; i++) if (since < 0 || axis.get(i) >= since) avg[i] = p.average();
        }
        for (int i = 0; i < last.length; i++) {
            last[i] = k > 0 ? last[i] / k : Double.NaN;
            fair[i] = k > 0 ? fair[i] / k : Double.NaN;
        }
        return new double[][] { last, fair, avg };
    }

    /** One calendar year of what a bond still pays: the coupons and the face, the whole bond's and the fund's part. */
    public record YearFlow(int year, double coupons, double face, double fundCoupons, double fundFace) { }

    /**
     * WHAT A BOND STILL PAYS, a calendar year at a time (the spec's D9: no
     * bond's price is kept month by month, so its chart is its cash): a
     * twelfth of its coupon on its face every month it has left, and the
     * face in its last - the whole bond's, and the fund's part of it.
     */
    public static List<YearFlow> bondCashFlows(Game g, CorporateBond b) {
        List<YearFlow> out = new ArrayList<>();
        if (b == null) return out;
        int month = g.getMonth();
        int left = b.remainingMonths(month);
        java.util.Map<Integer, double[]> byYear = new java.util.TreeMap<>();
        double share = b.face() > 0 ? b.city() / b.face() : 0;
        for (int i = 1; i <= left; i++) {
            int m = month + i;
            double[] y = byYear.computeIfAbsent(CityCalendar.yearOf(m), k -> new double[2]);
            y[0] += b.face() * b.coupon() / CorporateBond.COUPONS_A_YEAR;
            if (m == b.maturityMonth()) y[1] += b.face();
        }
        for (java.util.Map.Entry<Integer, double[]> e : byYear.entrySet()) {
            double[] y = e.getValue();
            out.add(new YearFlow(e.getKey(), y[0], y[1], y[0] * share, y[1] * share));
        }
        return out;
    }

    /** The FUND decisions that name a company or a bond - its own orders - as flags on its chart; the founding month's on the axis. */
    public static List<ChartModel.Flag> flagsFor(Game g, String words) {
        List<ChartModel.Flag> all = ChartModel.flags(g.getDecisions(), DecisionLog.FUND);
        List<ChartModel.Flag> out = new ArrayList<>();
        for (ChartModel.Flag f : all) {
            List<DecisionLog.Entry> mine = new ArrayList<>();
            for (DecisionLog.Entry e : f.entries()) if (words != null && e.label().contains(words)) mine.add(e);
            if (!mine.isEmpty()) out.add(new ChartModel.Flag(f.month(), mine));
        }
        HistorySave h = g.getHistorySave();
        return h.getMonth().isEmpty() ? out : ChartModel.onAxis(out, h.getMonth().get(0));
    }

    /* ============================== search ============================== */

    /** What a search hit is for: the market's sections when the box is empty. */
    public static final String HELD = "YOUR HOLDINGS", SHARES_SECTION = "SHARES", BONDS_SECTION = "BONDS";

    /**
     * One hit: its key and name, what it is, the company or bond, its price
     * a unit and the month of it, its move over MOVE_MONTHS (a share's, per
     * founding share so a split is no move; NaN when its record is shorter or
     * not precise enough - recordShort() says which), a bond's yield at its
     * value and months left, the year of prices for its sparkline (a
     * share's), what the fund holds of it, what it is ranked by (a company's
     * market value, a bond's face outstanding), and the section it falls in
     * when the box is empty.
     */
    public record Hit(String key, String name, String kind, int company, int bondId, double price, int priceMonth,
                      double move, double yield, int monthsLeft, double[] spark, double held, double size,
                      String section, String issuer, double coupon, int maturity) { }

    /**
     * The market, searched (the spec's D1): every listed company, every bond
     * outstanding, and the preferred and warrants while the fund holds them.
     * A query matches a company's or an issuer's name (any case), "#id", a
     * coupon ("2.8"), a maturity year, or a kind word ("bond", "share",
     * "bank", "preferred"). Ranked: the name starts with the query, then
     * held, then size; an issuer's bonds by maturity. `filter` is "All",
     * "Shares", "Bonds" or "Held". With an empty query, the market: YOUR
     * HOLDINGS, then SHARES by market value, then BONDS by face. A lot the
     * fund closed on a bond since gone - repaid, written off - is found
     * only when named (its name, issuer, "#id" or "closed"), last.
     */
    public static List<Hit> search(Game g, String query, String filter) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        String f = filter == null ? "All" : filter;
        List<Hit> all = market(g);
        List<Hit> out = new ArrayList<>();
        for (Hit h : all) {
            if ("Shares".equals(f) && !(SHARE.equals(h.kind()))) continue;
            if ("Bonds".equals(f) && !BOND.equals(h.kind())) continue;
            if ("Held".equals(f) && !(h.held() > 0)) continue;
            if (!q.isEmpty() && !matches(h, q)) continue;
            out.add(h);
        }
        if (q.isEmpty()) {
            List<Hit> held = new ArrayList<>(), shares = new ArrayList<>(), bonds = new ArrayList<>();
            for (Hit h : out) {
                if (h.held() > 0 && !"Held".equals(f)) held.add(withSection(h, HELD));
                if ("Held".equals(f)) { held.add(withSection(h, HELD)); continue; }
                if (SHARE.equals(h.kind())) shares.add(withSection(h, SHARES_SECTION));
                else if (BOND.equals(h.kind())) bonds.add(withSection(h, BONDS_SECTION));
            }
            held.sort(Comparator.comparingDouble(Hit::held).reversed());
            shares.sort(Comparator.comparingDouble(Hit::size).reversed());
            bonds.sort(Comparator.comparingDouble(Hit::size).reversed().thenComparingInt(Hit::bondId));
            List<Hit> market = new ArrayList<>(held);
            market.addAll(shares);
            market.addAll(bonds);
            return market;
        }
        out.sort(Comparator.<Hit>comparingInt(h -> startsWith(h, q) ? 0 : 1)
                .thenComparingInt(h -> h.held() > 0 ? 0 : 1)
                .thenComparing((a, b) -> BOND.equals(a.kind()) && BOND.equals(b.kind()) && a.issuer() != null
                        && a.issuer().equals(b.issuer()) ? Integer.compare(a.maturity(), b.maturity())
                        : Double.compare(b.size(), a.size())));
        if ("All".equals(f) || "Bonds".equals(f)) {
            // ...and, named, the lots it closed on bonds no longer on any book, newest first.
            for (FundLedger.Lot l : closedLots(g)) {
                if (!l.isBond() || g.getBondMarket().bond(l.bondId()) != null) continue;
                boolean named = l.name().toLowerCase(Locale.ROOT).contains(q)
                        || (l.issuer() != null && l.issuer().toLowerCase(Locale.ROOT).contains(q))
                        || (q.startsWith("#") && ("#" + l.bondId()).startsWith(q)) || CLOSED.startsWith(q);
                if (!named) continue;
                out.add(new Hit(l.key(), l.name(), CLOSED, -1, l.bondId(), Double.NaN, l.closedMonth(), Double.NaN,
                        Double.NaN, -1, null, 0, 0, null, l.issuer(), l.coupon(), l.maturity()));
            }
        }
        return out;
    }

    private static Hit withSection(Hit h, String section) {
        return new Hit(h.key(), h.name(), h.kind(), h.company(), h.bondId(), h.price(), h.priceMonth(), h.move(),
                h.yield(), h.monthsLeft(), h.spark(), h.held(), h.size(), section, h.issuer(), h.coupon(), h.maturity());
    }

    private static boolean startsWith(Hit h, String q) {
        String n = h.name().toLowerCase(Locale.ROOT);
        if (n.startsWith(q)) return true;
        return h.issuer() != null && h.issuer().toLowerCase(Locale.ROOT).startsWith(q);
    }

    /** Whether a hit answers a query: its name or issuer, "#id", a coupon, a maturity year, or a kind word. */
    static boolean matches(Hit h, String q) {
        String name = h.name().toLowerCase(Locale.ROOT);
        if (name.contains(q)) return true;
        if (h.issuer() != null && h.issuer().toLowerCase(Locale.ROOT).contains(q)) return true;
        if (BOND.equals(h.kind())) {
            if (q.startsWith("#") && ("#" + h.bondId()).startsWith(q)) return true;
            String coupon = String.format(Locale.ROOT, "%.2f", h.coupon() * 100);
            if (q.matches("[0-9]+(\\.[0-9]*)?") && q.contains(".") && coupon.startsWith(q)) return true;
            if (q.matches("[0-9]{3,}") && String.valueOf(CityCalendar.yearOf(h.maturity())).startsWith(q)) return true;
            if ("bond".startsWith(q) || "bonds".startsWith(q)) return true;
        }
        if (SHARE.equals(h.kind()) && ("share".startsWith(q) || "shares".startsWith(q))) return true;
        if ((PREFERRED.equals(h.kind()) || WARRANTS.equals(h.kind())) && (h.kind().startsWith(q) || "bank".startsWith(q))) return true;
        if (h.company() == Equity.BANK && "bank".startsWith(q)) return true;
        return false;
    }

    /** Every instrument the fund could look at: the listed companies, the bonds outstanding, and its own preferred and warrants while it holds them. */
    public static List<Hit> market(Game g) {
        List<Hit> out = new ArrayList<>();
        Equity reg = g.getEquity();
        Exchange ex = g.getExchange();
        HistorySave h = g.getHistorySave();
        int month = g.getMonth();
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            if (!(reg.getShares(c) > 0)) continue;
            String name = Equity.COMPANIES[c];
            double held = reg.getCityShares(c) * ex.cityMark(c);
            out.add(new Hit(FundLedger.shareKey(name), name, SHARE, c, -1, ex.price(c),
                    ex.hasTraded(c) ? ex.bookOf(c).lastTradeMonth() : -1, move(h, name, MOVE_MONTHS), Double.NaN, -1,
                    spark(h, name, MOVE_MONTHS), held, ex.marketCap(reg, c), null, null, 0, -1));
        }
        BondMarket bm = g.getBondMarket();
        for (CorporateBond b : bm.getBonds()) {
            if (b.isMatured(month) || !(b.face() > 0)) continue;
            double price = bm.modelPrice(b, month);
            out.add(new Hit(FundLedger.bondKey(b.id()), bondName(b), BOND, -1, b.id(), price, month, Double.NaN,
                    bm.modelYield(b, month), b.remainingMonths(month), null, b.city() * price, b.face(), null,
                    b.issuer(), b.coupon(), b.maturityMonth()));
        }
        Position pref = preferredPosition(g), warr = warrantsPosition(g);
        if (pref != null) out.add(new Hit(pref.key(), pref.name(), PREFERRED, Equity.BANK, -1, 1, month, Double.NaN,
                Bank.PREFERRED_RATE, -1, null, pref.value(), pref.value(), null, null, 0, -1));
        if (warr != null) out.add(new Hit(warr.key(), warr.name(), WARRANTS, Equity.BANK, -1, warr.price(), month, Double.NaN,
                Double.NaN, -1, null, warr.value(), warr.value(), null, null, 0, -1));
        return out;
    }

    /**
     * A share's move over the last `months` months, per founding share (the
     * history's), so a split is no move: NaN when either end was not recorded
     * or was recorded under MIN_RECORDED_PRICE (four places of a consolidated
     * share's price are not a price - the spec's B6).
     */
    public static double move(HistorySave h, String company, int months) {
        double[] a = h.aligned(HistorySave.priceKey(company));
        int last = a.length - 1, then = last - months;
        if (months <= 0 || then < 0) return Double.NaN;
        if (!(a[last] >= MIN_RECORDED_PRICE) || !(a[then] >= MIN_RECORDED_PRICE)) return Double.NaN;
        return a[last] / a[then] - 1;
    }

    /**
     * ...and why there is none, when the reason is that the record does not
     * reach back `months` months: a city younger than that (its history has
     * fewer months), or a price whose series began since (an end not
     * recorded at all). False when both ends were recorded, so a NaN move is
     * then MIN_RECORDED_PRICE's (the spec's B6) - Search says which.
     */
    public static boolean recordShort(HistorySave h, String company, int months) {
        double[] a = h.aligned(HistorySave.priceKey(company));
        int last = a.length - 1, then = last - months;
        if (months <= 0) return false;
        return then < 0 || Double.isNaN(a[last]) || Double.isNaN(a[then]);
    }

    /** ...and its last `months` + 1 recorded prices, for a sparkline (NaN where not recorded). */
    public static double[] spark(HistorySave h, String company, int months) {
        double[] a = h.aligned(HistorySave.priceKey(company));
        int from = Math.max(0, a.length - months - 1);
        return java.util.Arrays.copyOfRange(a, from, a.length);
    }

    /* ============================== the quote ============================== */

    /**
     * An order the ticket would place: the lot's key, buy or sell, the
     * figure - money on a buy by amount, units (shares or face) otherwise -
     * and the price a unit it is to post at (0: fair value, what the rule
     * asks at; a bond's: its value).
     */
    public record Order(String key, boolean buy, boolean byAmount, double figure, double limit) { }

    /**
     * WHAT THE ORDER WOULD DO, NOW, ON TODAY'S BOOK (the spec's 4.4). Today's
     * book is withdrawn and posted afresh at the next step, so this is what
     * stands now, not a promise:
     *
     *   the price it would post at and the order's units and money (a buy by
     *   amount: the money over the price - the model's own amount / price);
     *   what is offered on the other side at or better than the price - asks
     *   at or under it for a buy (the bank's desk's asks are new shares, which
     *   the fund never buys, and the rule's are the fund's own), bids at or
     *   over it for a sale - its units, money and average; what would rest
     *   for the month, and what stands ahead of it on its side (the rule's own
     *   bid and a company's buyback among them); the price over fair value;
     *   no fee; the fund's free cash after; the position after - its units,
     *   its ACB (on a buy, the most it can cost), its average; on a sale the
     *   ACB out and what it realizes at the quote on what is offered; the
     *   price it would leave as the last trade, which every holding is marked
     *   at; and the 10% cap - the market book's share now, what the fund's
     *   other buys on the company could add to it (`capOrders`: the hand's on
     *   the book and waiting, and the rule's bid as far as their room lets it
     *   fill), the share after (a buy's: with those and this order all
     *   filled), how much of the rule's bid makes way for this order
     *   (`ruleGivesWay`), the room left and a buy capped at it -
     *   Exchange.fundRoom(), the reckoning the step trims the order with
     *   (Exchange.postFund()) and the rule's bid fills by. The warnings say
     *   each in a word: stale, nothing offered, over fair, capped, the cash.
     */
    public record Quote(boolean buy, double price, double fair, double units, double money,
                        double offeredUnits, double offeredMoney, double offeredAverage, double rests,
                        double aheadUnits, double aheadRule, double aheadBuyback, double bestBid, double bestAsk,
                        double overFair, double fee, double cash, double cashAfter, double unitsHeld, double unitsAfter,
                        double acbAfter, double averageAfter, double acbOut, double realized, double markAfter,
                        double markMove, double capBefore, double capOrders, double capAfter, double ruleGivesWay,
                        double room, double cappedAt, boolean capped, boolean stale, int priceMonth, List<String> warnings) { }

    public static Quote quote(Game g, Order o) {
        if (o == null || o.key() == null) return null;
        if (o.key().startsWith("B:")) return bondQuote(g, o);
        int c = companyOf(o.key());
        if (c < 0) return null;
        Equity reg = g.getEquity();
        Exchange ex = g.getExchange();
        TreasuryFund f = g.getFund();
        OrderBook book = ex.bookOf(c);
        double fair = ex.fair(c);
        double price = o.limit() > 0 ? o.limit() : fair;
        List<String> warnings = new ArrayList<>();
        double market = reg.getCityMarketShares(c), rescue = reg.getCityRescueShares(c), held = market + rescue;
        double shares = reg.getShares(c);
        // The cap as the step reckons it: the market book with every buy of the fund's on the company filled.
        double could = ex.fundCouldHold(reg, f, c, 0), room = ex.fundRoom(reg, f, c);
        double capOrders = shares > 0 ? Math.max(0, could - market) / shares : 0;
        double cash = g.fundCashFree();
        FundLedger.Lot lot = f.getLedger().lot(FundLedger.shareKey(Equity.COMPANIES[c]));
        double marketAcb = lot == null ? 0 : lot.acb();
        double acbNow = marketAcb + (rescue > 0 ? f.getRescueCost() : 0);
        int priceMonth = ex.hasTraded(c) ? book.lastTradeMonth() : -1;
        boolean stale = priceMonth >= 0 && g.getMonth() - priceMonth > STALE_MONTHS;
        if (stale) warnings.add("stale");
        if (!(price > 0)) return null;
        if (o.buy()) {
            double units = o.byAmount() ? o.figure() / price : o.figure();
            boolean capped = units > room + FundLedger.DUST;
            if (capped) { units = room; warnings.add("capped"); }
            double money = units * price;
            if (money > cash + 1e-9) warnings.add("cash");
            double left = units, offU = 0, offM = 0, last = Double.NaN;
            for (OrderBook.Order a : book.asks()) {
                if (a.price() > price || !(left > FundLedger.DUST)) break;
                if (Exchange.isFund(a.who()) || (c == Equity.BANK && Exchange.DESK.equals(a.who()))) continue;
                double take = Math.min(left, a.quantity());
                offU += take; offM += take * a.price(); left -= take; last = a.price();
            }
            if (!(offU > FundLedger.DUST) && units > FundLedger.DUST) warnings.add("nothing offered");
            double aheadU = 0, aheadRule = 0, aheadBuyback = Double.NaN;
            for (OrderBook.Order b : book.bids()) {
                if (b.price() < price) break;
                if (Exchange.FUND_HAND.equals(b.who())) continue;
                aheadU += b.quantity();
                if (Exchange.FUND.equals(b.who())) aheadRule += b.quantity();
                if (b.who().equals(Equity.COMPANIES[c]) && Double.isNaN(aheadBuyback)) aheadBuyback = b.price();
            }
            double over = fair > 0 ? price / fair - 1 : Double.NaN;
            if (over > 1e-9) warnings.add("over fair");
            double unitsAfter = held + units;
            double acbAfter = acbNow + offM + Math.max(0, units - offU) * price;
            double markAfter = offU > FundLedger.DUST ? last : Double.NaN;
            // ...with this order among the fund's buys: the rule's bid makes way for it.
            double couldAfter = ex.fundCouldHold(reg, f, c, units);
            return new Quote(true, price, fair, units, money, offU, offM, offU > 0 ? offM / offU : Double.NaN,
                    Math.max(0, units - offU), aheadU, aheadRule, aheadBuyback, ex.bestBid(c), ex.bestAsk(c), over, 0,
                    cash, cash - money, held, unitsAfter, acbAfter, unitsAfter > 0 ? acbAfter / unitsAfter : Double.NaN,
                    0, 0, markAfter, Double.isNaN(markAfter) ? Double.NaN : markAfter / ex.price(c) - 1,
                    shares > 0 ? market / shares : 0, capOrders, shares > 0 ? couldAfter / shares : 0,
                    Math.max(0, could + units - couldAfter), room, room * price, capped, stale, priceMonth, warnings);
        }
        // A sale: the market book first, then the rescue book, as the register sells by hand.
        double units = Math.min(Math.max(0, o.byAmount() ? o.figure() / price : o.figure()), held);
        double fromMarket = Math.min(units, market), fromRescue = Math.max(0, units - fromMarket);
        double acbOut = (market > 0 ? marketAcb * fromMarket / market : 0) + (rescue > 0 ? f.getRescueCost() * fromRescue / rescue : 0);
        double left = units, offU = 0, offM = 0, last = Double.NaN;
        for (OrderBook.Order b : book.bids()) {
            if (b.price() < price || !(left > FundLedger.DUST)) break;
            if (Exchange.isFund(b.who())) continue;
            double take = Math.min(left, b.quantity());
            offU += take; offM += take * b.price(); left -= take; last = b.price();
        }
        if (!(offU > FundLedger.DUST) && units > FundLedger.DUST) warnings.add("nothing offered");
        double aheadU = 0, aheadRule = 0;
        for (OrderBook.Order a : book.asks()) {
            if (a.price() > price) break;
            if (Exchange.FUND_HAND.equals(a.who())) continue;
            aheadU += a.quantity();
            if (Exchange.FUND.equals(a.who())) aheadRule += a.quantity();
        }
        double over = fair > 0 ? price / fair - 1 : Double.NaN;
        double realized = units > 0 ? offM - acbOut * offU / units : 0;
        double markAfter = offU > FundLedger.DUST ? last : Double.NaN;
        double money = units * price;
        return new Quote(false, price, fair, units, money, offU, offM, offU > 0 ? offM / offU : Double.NaN,
                Math.max(0, units - offU), aheadU, aheadRule, Double.NaN, ex.bestBid(c), ex.bestAsk(c), over, 0,
                cash, cash + offM, held, held - units, acbNow - acbOut,
                held - units > FundLedger.DUST ? (acbNow - acbOut) / (held - units) : Double.NaN,
                acbOut, realized, markAfter, Double.isNaN(markAfter) ? Double.NaN : markAfter / ex.price(c) - 1,
                shares > 0 ? market / shares : 0, capOrders, shares > 0 ? (market - fromMarket) / shares : 0, 0, room,
                room * price, false, stale, priceMonth, warnings);
    }

    /** ...a bond's: at its value a unit of face, or the price named; no cap (the rule's 30% is of its book, not of the bond). */
    static Quote bondQuote(Game g, Order o) {
        int id = bondIdOf(o.key());
        BondMarket bm = g.getBondMarket();
        CorporateBond b = bm.bond(id);
        if (b == null) return null;
        int month = g.getMonth();
        double value = bm.modelPrice(b, month);
        double price = o.limit() > 0 ? o.limit() : value;
        if (!(price > 0)) return null;
        OrderBook book = bm.bookOf(b);
        TreasuryFund f = g.getFund();
        FundLedger.Lot lot = f.getLedger().lot(o.key());
        double acbNow = lot == null ? 0 : lot.acb();
        double held = b.city();
        double cash = g.fundCashFree();
        List<String> warnings = new ArrayList<>();
        double over = value > 0 ? price / value - 1 : Double.NaN;
        if (o.buy()) {
            double units = o.byAmount() ? o.figure() / price : o.figure();
            double money = units * price;
            if (money > cash + 1e-9) warnings.add("cash");
            double left = units, offU = 0, offM = 0, last = Double.NaN;
            for (OrderBook.Order a : book.asks()) {
                if (a.price() > price || !(left > FundLedger.DUST)) break;
                if (BondMarket.isFund(a.who())) continue;
                double take = Math.min(left, a.quantity());
                offU += take; offM += take * a.price(); left -= take; last = a.price();
            }
            if (!(offU > FundLedger.DUST) && units > FundLedger.DUST) warnings.add("nothing offered");
            if (over > 1e-9) warnings.add("over fair");
            double aheadU = 0, aheadRule = 0;
            for (OrderBook.Order x : book.bids()) {
                if (x.price() < price) break;
                if (BondMarket.FUND_HAND.equals(x.who())) continue;
                aheadU += x.quantity();
                if (BondMarket.FUND.equals(x.who())) aheadRule += x.quantity();
            }
            double unitsAfter = held + units;
            double acbAfter = acbNow + offM + Math.max(0, units - offU) * price;
            return new Quote(true, price, value, units, money, offU, offM, offU > 0 ? offM / offU : Double.NaN,
                    Math.max(0, units - offU), aheadU, aheadRule, Double.NaN, book.bestBid(), book.bestAsk(), over, 0,
                    cash, cash - money, held, unitsAfter, acbAfter, unitsAfter > 0 ? acbAfter / unitsAfter : Double.NaN,
                    0, 0, offU > FundLedger.DUST ? last : Double.NaN, Double.NaN,
                    b.face() > 0 ? held / b.face() : 0, Double.NaN, b.face() > 0 ? unitsAfter / b.face() : 0, 0, Double.NaN,
                    Double.NaN, false, false, month, warnings);
        }
        double units = Math.min(Math.max(0, o.byAmount() ? o.figure() / price : o.figure()), held);
        double acbOut = held > 0 ? acbNow * units / held : 0;
        double left = units, offU = 0, offM = 0, last = Double.NaN;
        for (OrderBook.Order x : book.bids()) {
            if (x.price() < price || !(left > FundLedger.DUST)) break;
            if (BondMarket.isFund(x.who())) continue;
            double take = Math.min(left, x.quantity());
            offU += take; offM += take * x.price(); left -= take; last = x.price();
        }
        if (!(offU > FundLedger.DUST) && units > FundLedger.DUST) warnings.add("nothing offered");
        double aheadU = 0, aheadRule = 0;
        for (OrderBook.Order a : book.asks()) {
            if (a.price() > price) break;
            if (BondMarket.FUND_HAND.equals(a.who())) continue;
            aheadU += a.quantity();
            if (BondMarket.FUND.equals(a.who())) aheadRule += a.quantity();
        }
        double realized = units > 0 ? offM - acbOut * offU / units : 0;
        return new Quote(false, price, value, units, units * price, offU, offM, offU > 0 ? offM / offU : Double.NaN,
                Math.max(0, units - offU), aheadU, aheadRule, Double.NaN, book.bestBid(), book.bestAsk(), over, 0,
                cash, cash + offM, held, held - units, acbNow - acbOut,
                held - units > FundLedger.DUST ? (acbNow - acbOut) / (held - units) : Double.NaN,
                acbOut, realized, offU > FundLedger.DUST ? last : Double.NaN, Double.NaN,
                b.face() > 0 ? held / b.face() : 0, Double.NaN, b.face() > 0 ? (held - units) / b.face() : 0, 0, Double.NaN,
                Double.NaN, false, false, month, warnings);
    }

    /* ============================== the record ============================== */

    /** The activity page's chips: what each kind of row is filed under. */
    public static final String TRADES = "Trades", INCOME = "Income", MONEY = "Money in and out", EVENTS = "Events";

    /**
     * One line of the record, newest first: a ledger row, or before the
     * ledger began one of the save's own dated records - a resolution, a
     * FUND or BANK decision - marked `fromLog`.
     */
    public record Line(int month, String kind, String group, FundLedger.Activity row, DecisionLog.Entry entry,
                       TreasuryFund.Resolution resolution) {
        public boolean fromLog() { return row == null; }
    }

    /** The group a ledger row is filed under. */
    public static String groupOf(String kind) {
        return switch (kind) {
            case FundLedger.BUY, FundLedger.SELL, FundLedger.LAPSED -> TRADES;
            case FundLedger.DIVIDEND, FundLedger.COUPON -> INCOME;
            case FundLedger.PAY_IN, FundLedger.DRAW_OUT, FundLedger.TRANSFER -> MONEY;
            default -> EVENTS;
        };
    }

    /**
     * The record, newest first, filtered by group ("All" for every one) and,
     * when `key` is not null, to one lot's rows (a share's market and rescue
     * books together). Before the ledger began - a save from before 0.7.39 -
     * the save's own resolutions and its FUND and BANK decisions fill in.
     */
    public static List<Line> activity(Game g, String group, String key) {
        String gr = group == null ? "All" : group;
        FundLedger ledger = g.getFund().getLedger();
        List<Line> out = new ArrayList<>();
        List<FundLedger.Activity> rows = ledger.getActivity();
        for (int i = rows.size() - 1; i >= 0; i--) {
            FundLedger.Activity a = rows.get(i);
            String grp = groupOf(a.kind());
            if (!"All".equals(gr) && !gr.equals(grp)) continue;
            if (key != null && !sameLot(key, a.key())) continue;
            out.add(new Line(a.month(), a.kind(), grp, a, null, null));
        }
        int since = ledger.getTrackingSince();
        if (since >= 0 && key == null && ("All".equals(gr) || EVENTS.equals(gr))) {
            List<Line> before = new ArrayList<>();
            for (TreasuryFund.Resolution r : g.getFund().getResolutions()) {
                if (r.month() < since) before.add(new Line(r.month(), FundLedger.RESCUE, EVENTS, null, null, r));
            }
            for (DecisionLog.Entry e : g.getDecisions().entries()) {
                if (e.month() >= since) continue;
                if (!DecisionLog.FUND.equals(e.kind()) && !DecisionLog.BANK.equals(e.kind())) continue;
                before.add(new Line(e.month(), e.kind(), EVENTS, null, e, null));
            }
            before.sort(Comparator.comparingInt(Line::month).reversed());
            out.addAll(before);
        }
        return out;
    }

    /** Whether a row's key is the lot a security page shows: a share's books together. */
    public static boolean sameLot(String key, String rowKey) {
        if (rowKey == null) return false;
        if (rowKey.equals(key)) return true;
        int c = companyOf(key);
        return c >= 0 && c == companyOf(rowKey);
    }
}
