package ham.citybuildersim;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * The blow-up study's driver (docs/studies/spec-blowup-7200.md): the wealth
 * study's WealthRun, unchanged, with a hook that writes the bank, the central
 * bank's window and every audit line each month from -Dblowup.from on.
 *
 * WHY. The wealth study saw the central bank lend the bank 35,000 years of GDP
 * in a year past month 7,200 (seed 1 near 7,390). Its CSV is a row a year; the
 * month and the line need a row a month, on the same path, so this drives
 * WealthRun.foundAndPlay itself and only listens.
 *
 * Not game code. The city lives in GameFiles.scratch(), never a save slot.
 *
 *   java ... ham.citybuildersim.BlowupRun SEED MONTHS OUT.csv LINES.csv
 */
public class BlowupRun {
    static PrintWriter w, lines;
    static boolean header;
    static final int FROM = Integer.getInteger("blowup.from", 7200);

    static void put(LinkedHashMap<String, Double> c, String k, double v) { c.put(k, v); }

    static void month(Game g) {
        if (g.getMonth() < FROM) return;
        LinkedHashMap<String, Double> c = new LinkedHashMap<>();
        Bank bk = g.getBank();
        CentralBank cb = g.getCentralBank();
        MoneyAudit.Result a = g.getLastMoneyAudit();
        NationalAccounts na = g.getEconomyManager().getNationalAccounts();
        put(c, "month", g.getMonth());
        put(c, "policy", g.getDebtManager().getPolicyRate());
        put(c, "index", g.getPriceIndex().getIndex());
        put(c, "fx", g.getForeignAccounts().getRate());
        put(c, "gdpMonth", g.getEconomyManager().getMonthGdp());
        put(c, "cbAdvBank", cb.getAdvancesToBank());
        put(c, "cbAdvanced", cb.getAdvancedToBank());
        put(c, "cbRepaid", cb.getRepaidByBank());
        put(c, "cbWindowInt", cb.getWindowInterest());
        put(c, "m0", cb.m0());
        put(c, "bankCash", bk.getCash());
        put(c, "bankEquity", bk.equity());
        put(c, "bankBook", bk.getBook());
        put(c, "bankSecurities", bk.getSecurities());
        put(c, "bankMark", bk.getMarkChange());
        put(c, "bankTrading", bk.getTradingIncome());
        put(c, "bankBondBook", bk.getBondBook());
        put(c, "bankCarry", bk.getCarryBook());
        put(c, "bankDeposits", bk.depositsGathered());
        put(c, "bankHot", bk.getForeignDeposits());
        put(c, "bankCapacity", bk.capacity());
        put(c, "bankLiab", bk.totalLiabilities());
        put(c, "bankDiv", bk.getDividendsPaid());
        put(c, "bankWriteOffs", bk.getWriteOffs());
        put(c, "bankShort", bk.getShortfallThisMonth());
        put(c, "bankFrozen", bk.isInResolution() ? 1 : 0);
        put(c, "treasury", g.getCash());
        put(c, "fund", g.getFund().getCash());
        put(c, "vault", g.getForeignAccounts().getReserves());
        put(c, "valuation", a.valuationChange());
        put(c, "moneyMade", a.moneyMade());
        put(c, "residual", a.residual);
        put(c, "trade", a.tradeBalance());
        put(c, "financial", a.financialAccount());
        put(c, "taxBusiness", na.getTaxBusiness());
        put(c, "revenue", na.getTotalRevenue());
        double cap = 0, top = 0, topK = -1;
        Equity eq = g.getEquity();
        for (int k = 0; k < Equity.COMPANIES.length; k++) {
            double px = g.getExchange().fair(k), n = eq.getShares(k);
            if (!(px > 0) || !(n > 0)) continue;
            cap += px * n;
            if (px * n > top) { top = px * n; topK = k; }
        }
        put(c, "marketCap", cap);
        put(c, "topCap", top);
        put(c, "topK", topK);
        if (!header) { w.println(String.join(",", c.keySet())); header = true; }
        StringBuilder b = new StringBuilder();
        for (double v : c.values()) {
            if (b.length() > 0) b.append(',');
            b.append(String.format(Locale.ROOT, "%.9g", v));
        }
        w.println(b);
        w.flush();
        for (String line : a.detail.split("\n")) {
            String s = line.trim();
            if (!(s.startsWith("+") || s.startsWith("-"))) continue;
            int cut = s.lastIndexOf(' ');
            if (cut < 0) continue;
            lines.println(g.getMonth() + "," + s.substring(0, cut).trim().replace(',', ';') + "," + s.substring(cut + 1).replace(",", ""));
        }
        lines.flush();
    }

    public static void main(String[] args) throws Exception {
        w = new PrintWriter(new BufferedWriter(new FileWriter(args[2])));
        lines = new PrintWriter(new BufferedWriter(new FileWriter(args[3])));
        WealthRun.hook = BlowupRun::month;
        WealthRun.main(new String[] {args[0], args[1], args[2] + ".wealth.csv"});
        w.close();
        lines.close();
    }
}
