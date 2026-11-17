package ham.citybuildersim;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * What the government owns net of its paper, in years of its revenue, and
 * the existing levers that can give an excess back (GV1, spec-wealth 3.2).
 *
 * WHY. A surplus is a flow; it never says how large a stock has accumulated.
 * The treasury, both fund books and the vault belong in that stock, valued
 * in the city's money. This read changes no policy and reprices no market.
 */
public final class GovernmentView {
    /** A year of actual revenue is required; an unfinished year is not forecast as a full one. */
    public static final int REVENUE_MONTHS = 12;
    /** Under a tenth of revenue is broke (spec-wealth's estimated treasury-buffer bands). */
    public static final double THIN_MIN = .1;
    /** Half a year's revenue is the sound band's bottom (spec-wealth 3.2, estimated). */
    public static final double SOUND_MIN = .5;
    /** Two years is its top, beyond which the test player gives money back (spec-wealth 3.2). */
    public static final double RICH_MIN = 2;
    /** Over five years is hoarding (spec-wealth 3.2, estimated). */
    public static final double HOARDING_MIN = 5;
    /** How far past a band's bound, as a share of that bound, the wealth must go before the band notice leaves the band: a tenth (GV1-R, estimated), so wealth that sits on a bound posts one notice, not one a month. */
    public static final double BAND_MARGIN = .1;
    /** An illustrative tax cut on the advice card: one percentage point, not a policy change. */
    public static final double TAX_CUT_STEP = .01;
    /** An illustrative transfer rise: the five-point step the brief gives its test player. */
    public static final double TRANSFER_RAISE_STEP = .05;

    public enum Band {
        BROKE("broke"), THIN("thin"), SOUND("sound"), RICH("rich"), HOARDING("hoarding");
        private final String label;
        Band(String label) { this.label = label; }
        public String label() { return label; }
    }

    private final Game game;
    private final double treasury, fundCash, fundHoldings, vault, paper, debt, annualRevenue, netWealth, wealthYears;
    private Advice advice;

    private GovernmentView(Game game) {
        this.game = game;
        treasury = game.getCash();
        fundCash = game.getFund().getCash();
        fundHoldings = game.fundValue() - fundCash;
        vault = game.getForeignAccounts().getReserves();
        paper = game.getDebtManager().getAllPrincipal();
        debt = paper + game.getCentralBank().getAdvancesToTreasury() + game.getDebtManager().getIndexationOwed();
        HistorySave history = game.getHistorySave();
        annualRevenue = history.monthsRecorded("revenue") >= REVENUE_MONTHS
                ? history.recentTotal("revenue", REVENUE_MONTHS) : Double.NaN;
        netWealth = treasury + fundCash + fundHoldings + vault - debt;
        wealthYears = annualRevenue > 0 && Double.isFinite(annualRevenue) && Double.isFinite(netWealth)
                ? netWealth / annualRevenue : Double.NaN;
    }

    public static GovernmentView of(Game game) { return new GovernmentView(game); }
    public double treasury() { return treasury; }
    public double fundCash() { return fundCash; }
    public double fundHoldings() { return fundHoldings; }
    public double vault() { return vault; }
    /**
     * What the government owes: its paper at face, the central bank's advances and the linked bonds'
     * indexation past their face (GV1-R). Not getPricedDebt(), which adds the overdraft as well: the
     * treasury's cash below nothing already is the overdraft here (Game.getNetPosition() tells that double count).
     */
    public double debt() { return debt; }
    /** The government's paper alone, at face: what a buyback can buy (debt() adds the advances and the indexation, which no buyback retires; FB4's review). */
    public double paper() { return paper; }
    public double annualRevenue() { return annualRevenue; }
    public double netWealth() { return netWealth; }
    public double wealthYears() { return wealthYears; }
    public Band band() { return bandFor(wealthYears); }

    /** Exact endpoints: 0.1 thin, 0.5 sound, 2 sound, 5 rich; unavailable is no band. */
    public static Band bandFor(double years) {
        if (!Double.isFinite(years)) return null;
        if (years < THIN_MIN) return Band.BROKE;
        if (years < SOUND_MIN) return Band.THIN;
        if (years <= RICH_MIN) return Band.SOUND;
        if (years <= HOARDING_MIN) return Band.RICH;
        return Band.HOARDING;
    }

    /**
     * The band a notice holding {@code held} reports for {@code years}: the held band while the wealth lies
     * within its range widened by BAND_MARGIN of each finite bound (a lower bound L to L x (1 - margin), an upper
     * bound U to U x (1 + margin), the open and closed ends bandFor() gives), else bandFor(years). With no held
     * band, or wealth that is no number, it is bandFor(years).
     */
    public static Band bandFrom(Band held, double years) {
        if (held == null || !Double.isFinite(years)) return bandFor(years);
        boolean within = switch (held) {
            case BROKE -> years < THIN_MIN * (1 + BAND_MARGIN);
            case THIN -> years >= THIN_MIN * (1 - BAND_MARGIN) && years < SOUND_MIN * (1 + BAND_MARGIN);
            case SOUND -> years >= SOUND_MIN * (1 - BAND_MARGIN) && years <= RICH_MIN * (1 + BAND_MARGIN);
            case RICH -> years > RICH_MIN * (1 - BAND_MARGIN) && years <= HOARDING_MIN * (1 + BAND_MARGIN);
            case HOARDING -> years > HOARDING_MIN * (1 - BAND_MARGIN);
        };
        return within ? held : bandFor(years);
    }

    public boolean buybackAvailable() { return advice().paper != null; }
    public Debt buybackDebt() { return advice().paper; }
    public double buybackMonthlySaving() { return advice().coupon; }
    public String largestTaxName() { return advice().taxName; }
    public double largestTaxMonthly() { return advice().tax; }
    public double taxCutMonthly() { return advice().cut; }
    public String transferName() { return advice().transferName; }
    public double transferMonthly() { return advice().transfer; }
    public double transferRaiseMonthly() { return advice().raise; }

    /** Advice is only needed on screen, never paid for by the monthly notice or the test player. */
    private Advice advice() {
        if (advice == null) advice = new Advice(game, treasury, debt);
        return advice;
    }

    private static final class Advice {
        Debt paper;
        double coupon;
        String taxName = "Business tax", transferName = "Pensions";
        double tax, cut, transfer, raise;

        Advice(Game g, double cash, double owed) {
            EconomyManager em = g.getEconomyManager();
            TaxPolicy live = em.getTaxPolicy();
            PolicyPreview.TaxTake take = PolicyPreview.taxTake(g, live);
            double[] taxes = {take.profitTotal(), take.salesTotal(), take.wageTotal(), take.propertyTotal()};
            String[] names = {"Business tax", "Sales tax", "Wage tax", "Property tax"};
            int largest = 0;
            for (int i = 1; i < taxes.length; i++) if (taxes[i] > taxes[largest]) largest = i;
            taxName = names[largest];
            tax = taxes[largest];
            if (!(tax > 0)) taxName = null;
            TaxPolicy after = live.copy();
            switch (largest) {
                case 0 -> after.setProfitTaxRate(live.getProfitTaxRate() - TAX_CUT_STEP);
                case 1 -> after.setSalesTaxRate(live.getSalesTaxRate() - TAX_CUT_STEP);
                case 2 -> after.setWageTaxRate(live.getWageTaxRate() - TAX_CUT_STEP);
                case 3 -> after.setPropertyTaxRate(live.getPropertyTaxRate() - TAX_CUT_STEP);
                default -> throw new AssertionError(largest);
            }
            cut = Math.max(0, take.total() - PolicyPreview.taxTake(g, after).total());
            transfer = em.pensionsPaidUnder(live);
            after = live.copy();
            after.setPensionReplacement(live.getPensionReplacement() + TRANSFER_RAISE_STEP);
            raise = Math.max(0, em.pensionsPaidUnder(after) - transfer);
            TaxPolicy ei = live.copy();
            ei.setEiBenefitRate(live.getEiBenefitRate() + TRANSFER_RAISE_STEP);
            double eiNow = g.getUnemployment().benefitsAt(live.getEiBenefitRate());
            double eiRaise = Math.max(0, g.getUnemployment().benefitsAt(ei.getEiBenefitRate()) - eiNow);
            if (eiRaise > raise) {
                transferName = "EI";
                transfer = eiNow;
                raise = eiRaise;
            }
            if (!(raise > 0)) transferName = null;
            // Cash earns nothing. The yield at the offered price, not a coupon, decides:
            // a discount bill pays no coupon but retiring it below maturity value still saves money.
            // The central bank's holdings and unsettled partial pieces obey CD4's own exclusions.
            if (owed > 0 && cash > owed) {
                Quote best = buyback(g, cash, owed);
                paper = best.paper;
                coupon = best.coupon;
            }
        }
    }

    /** The piece worth buying back and the coupon that stops, as one quoting pass found them. */
    private record Quote(Debt paper, double coupon) { }

    /** What a quote was struck on: the month, the cash, the debt, each piece and its principal, and the market's saved inputs. */
    private static final class Stamp {
        final int month;
        final double cash, owed;
        final Debt[] pieces;
        final double[] principals, market;

        Stamp(Game g, double cash, double owed) {
            month = g.getMonth();
            this.cash = cash;
            this.owed = owed;
            pieces = g.getDebtManager().getDebt().toArray(new Debt[0]);
            principals = new double[pieces.length];
            for (int i = 0; i < pieces.length; i++) principals[i] = pieces[i].getOustandingPrincipal();
            market = g.getDebtManager().marketToSave();
        }

        boolean same(Stamp other) {
            if (month != other.month || Double.compare(cash, other.cash) != 0 || Double.compare(owed, other.owed) != 0
                    || pieces.length != other.pieces.length) return false;
            for (int i = 0; i < pieces.length; i++) if (pieces[i] != other.pieces[i]) return false;
            return Arrays.equals(principals, other.principals) && Arrays.equals(market, other.market);
        }
    }

    /** The last buyback quote of each city and what it was struck on, so a redraw of the Government overview with nothing changed quotes nothing (GV1-R). */
    private static final Map<Game, Map.Entry<Stamp, Quote>> QUOTED = Collections.synchronizedMap(new WeakHashMap<>());
    private static final AtomicInteger QUOTING_PASSES = new AtomicInteger();

    /** How many times a buyback quote has been struck over every piece of debt, for the check that a view with nothing changed reuses the last. */
    static int buybackQuotes() { return QUOTING_PASSES.get(); }

    private static Quote buyback(Game g, double cash, double owed) {
        Stamp stamp = new Stamp(g, cash, owed);
        Map.Entry<Stamp, Quote> last = QUOTED.get(g);
        if (last != null && last.getKey().same(stamp)) return last.getValue();
        Quote fresh = quoteEvery(g, cash);
        QUOTED.put(g, Map.entry(stamp, fresh));
        return fresh;
    }

    private static Quote quoteEvery(Game g, double cash) {
        QUOTING_PASSES.incrementAndGet();
        Debt paper = null;
        double coupon = 0;
        for (Debt d : g.getDebtManager().getDebt()) {
            Buyback.Result q = g.quoteBuybackAtRest(d, g.buybackOfferable(d), Double.NaN);
            if (q.paid() > cash) {
                // The cheapest slices come first, so scaling the filled face by cash/price
                // cannot cost more than cash. CD4 still refuses an unsettled partial piece.
                double affordable = q.takenFace() * cash / q.paid() * (1 - 1e-12);
                q = g.quoteBuybackAtRest(d, affordable, Double.NaN);
            }
            if (!(q.takenFace() > 0) || !(q.paid() <= cash)) continue;
            double fullPrice = q.paid() * d.getOustandingPrincipal() / q.takenFace();
            if (!(Debt.effectiveRateOf(g.getDebtManager().pricingFlows(d), fullPrice) > 0)) continue;
            double stopped = g.honestLine(d, q).couponStopped();
            if (paper == null || stopped > coupon) {
                paper = d;
                coupon = stopped;
            }
        }
        return new Quote(paper, coupon);
    }
}
