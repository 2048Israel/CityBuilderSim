package ham.citybuildersim.sectors;

import ham.citybuildersim.BuildingManager;
import ham.citybuildersim.Game;
import ham.citybuildersim.Ports;
import ham.citybuildersim.Sector;

import java.util.Map;

/**
 * Freight's Ports division: the crews that work the city's berths, paid by the
 * shippers who use them and paying the city for the quay (FR3, 2026-10-19;
 * docs/queue/FR3.md; docs/studies/spec-freight-and-infrastructure.md 3.4, D8,
 * D9).
 *
 * WHY. The city built the berths and the sea terminals earned nothing: the
 * crews were the city's transit bill, the tonnes crossed free, and so no city
 * ever built a port that the advice or the auto-builder could justify (the
 * spec's finding 9.5). Now the berths stay the city's, with their repairs
 * (EconomyManager.CITY_MAINTAINED), and Freight works them: it bills every
 * shipper a handling charge on each tonne that crosses a berth - the buyer for
 * what it lands, the exporter for what it ships, "Port handling" on the
 * shipper's input line - and pays the city port dues on the same tonnes, a
 * revenue line of the treasury ("Port dues") and a dial of the city's own
 * (TaxPolicy.getPortDues(), D9: the fare's shape). A tonne sold at home, railed
 * or by lorry pays no handling: each leg is paid once (spec 2).
 *
 * NOT A SECTOR, A DIVISION OF ONE, as Haulage is: it holds its own figures and
 * its own price rule, and books through Freight (the package-private seam at
 * the end of Freight's "Haulage division's seam"): its bill is Freight's other
 * revenue named "Port handling billed", its dues Freight's operating expense
 * "Port dues", its crews the posts of the berths Freight operates
 * (BuildingsTemplate.getOperator(); Freight.portsCrewByKind()).
 *
 * THE PRICE, BY KIND OF CARGO (FR3.md 2). The quote is the berths' crew over
 * the tonnes - the larger of the tonnes billed and DESIGN_USE of the kind's
 * berths' month, so an idle berth is carried by the division and not billed to
 * the few tonnes that use it - times the scarcity when the kind asks for more
 * than its berths, plus the dues a tonne pays the city. NO RETURN ON CAPITAL
 * (call 3): the division has no rate base, the city owns the berths and
 * recovers them through the dues.
 *
 * THE DUES, A TONNE OF A KIND: the annuity on the kind's terminals standing
 * (their cash cost and materials at market, BuildingManager
 * .getPortBookValueByCargo()) at the city's monthly borrowing rate over
 * PORT_LIFE_MONTHS, over the kind's berths' month at DESIGN_USE; times the
 * dial. The city's rate is what its last issue cleared at (cityRate()).
 *
 * THE MONTH, which Freight.haul() runs: billMonth() in step 1, beside the
 * railway's and the trucks' bills, at the shares in force while the cargo
 * moved; closeMonth() straight after, while the tally of the month billed and
 * the class crude went in are still the ones that month was billed with.
 */
public final class PortDivision {

    /** The share of a berth's month the dues and the rule's floor are worked at (FR3.md call 4): Ports.WORTH_A_BERTH, .60, the test player's own threshold for ordering a berth. */
    public static final double DESIGN_USE = Ports.WORTH_A_BERTH;

    /** Months over which a berth's cost is recovered by the dues: 360, thirty years, the term a landlord port leases a terminal for (est., FR3.md 2 [R91][R92]). */
    public static final double PORT_LIFE_MONTHS = 360;

    /** What the handling is called on the shipper's input line (FR3.md 1). */
    public static final String BILLED_AS = "Port handling";

    /** ...and on Freight's revenue (Freight.nameOtherRevenue()), beside "Haulage billed". */
    public static final String REVENUE_NAME = "Port handling billed";

    /** ...and the dues, on Freight's input line and the treasury's revenue. */
    public static final String DUES_NAME = "Port dues";

    private static final int KINDS = Ports.Cargo.values().length;

    private final Freight freight;

    /** The handling quote in force by kind, the city's money a tonne (D$ thousands a tonne); meaningful only when quoteKnown. */
    private final double[] quote = new double[KINDS];
    private final boolean[] quoteKnown = new boolean[KINDS];

    /* the month billed, by kind (the screens', the check's and the save's) */
    private final double[] tonnes = new double[KINDS];
    private final double[] billed = new double[KINDS];
    private final double[] dues = new double[KINDS];
    private final double[] crew = new double[KINDS];
    private final double[] design = new double[KINDS];
    private final double[] duesRate = new double[KINDS];
    /** The month's handling billed to the shippers (the sum of their lines) and the dues paid to the city, as booked. */
    private double handlingTotal, duesTotal;

    PortDivision(Freight freight) {
        this.freight = freight;
    }

    /* ------------------------------------------------------------ the arithmetic */

    /**
     * What a sum of capital costs a month to recover over its life: the
     * annuity V x r / (1 - (1 + r)^-n), and V / n at no interest.
     */
    public static double monthlyRecovery(double value, double monthlyRate, double months) {
        if (!(value > 0) || !(months > 0) || !Double.isFinite(value)) return 0;
        if (!(monthlyRate > 0) || !Double.isFinite(monthlyRate)) return value / months;
        return value * monthlyRate / (1 - Math.pow(1 + monthlyRate, -months));
    }

    /**
     * THE CITY'S BORROWING RATE, a year's (D9): what its last issue cleared at
     * (BondMarket.getLastIssueLoanRate(), an annual rate), the bank's lending
     * rate at the policy rate while it has issued nothing; nothing without a
     * game.
     */
    public double cityRate() {
        Game game = freight.gameOf();
        if (game == null) return 0;
        double r = game.getBondMarket() == null ? 0 : game.getBondMarket().getLastIssueLoanRate();
        if (!(r > 0) || !Double.isFinite(r)) {
            r = game.getBank() == null || game.getDebtManager() == null
                    ? 0 : game.getBank().lendingRate(game.getDebtManager().getPolicyRate());
        }
        return Double.isFinite(r) ? Math.max(0, r) : 0;
    }

    /** The dial: the multiple of the default due the city asks (TaxPolicy.getPortDues()); 1 with no game. */
    public double dial() {
        Game game = freight.gameOf();
        if (game == null || game.getEconomyManager() == null || game.getEconomyManager().getTaxPolicy() == null) {
            return ham.citybuildersim.TaxPolicy.DEFAULT_PORT_DUES;
        }
        return game.getEconomyManager().getTaxPolicy().getPortDues();
    }

    /**
     * The default due a tonne of a kind pays the city (dial 1), the city's
     * money: the kind's terminals' book value recovered over PORT_LIFE_MONTHS
     * at the city's monthly rate, over its berths' month at DESIGN_USE. Nothing
     * with no berth standing.
     */
    public double duesRateOf(Ports.Cargo kind, double berthMonth) {
        BuildingManager buildings = freight.buildingsOf();
        double over = berthMonth * DESIGN_USE;
        if (buildings == null || !(over > 0)) return 0;
        double value = buildings.getPortBookValueByCargo(kind);
        return monthlyRecovery(value, cityRate() / 12, PORT_LIFE_MONTHS) / over;
    }

    /**
     * The scarcity multiple on a kind's crew cost: 1 while what crossed of the
     * kind is at most its berths' month, rising linearly to Freight
     * .MAX_SCARCITY_MULTIPLE at twice the berths' month, capped there.
     */
    public static double scarcityOf(double boundaryTonnes, double berthMonth) {
        if (!(berthMonth > 0) || !(boundaryTonnes > berthMonth)) return 1;
        double over = Math.min(1, boundaryTonnes / berthMonth - 1);
        return 1 + over * (Freight.MAX_SCARCITY_MULTIPLE - 1);
    }

    /**
     * The rule's target for a kind: crew / max(tonnes, berths x DESIGN_USE) x
     * scarcity + the due x the dial. A kind with no tonnes and no berth is
     * the due alone.
     */
    public static double targetOf(double crewWages, double handled, double berthMonth, double scarcity,
                                  double dueRate, double dial) {
        double base = Math.max(handled, berthMonth * DESIGN_USE);
        double cost = base > 0 && crewWages > 0 ? crewWages / base * scarcity : 0;
        return cost + dueRate * dial;
    }

    /* ------------------------------------------------------------ the month */

    /**
     * Step 1 of the month: bills last month's sea tonnes, sector by sector, and
     * pays the city its dues on them.
     *
     * Called by Freight.haul() beside the railway's bill and Haulage's, at the
     * shares that were in force while the cargo moved. A sector's bill is its
     * sea tonnes of each kind (landed and shipped, the loop's) at the kind's
     * quote in force - the rule's own target while none has been struck yet
     * (the opening quote, FR3.md 2: no tariff is written in).
     *
     * @param seaOf each sector's sea tonnes of the month by kind (Ports.Cargo order), in the order of the loop that summed them
     */
    void billMonth(Map<Sector, double[]> seaOf, Ports ports) {
        java.util.Arrays.fill(tonnes, 0);
        java.util.Arrays.fill(billed, 0);
        java.util.Arrays.fill(dues, 0);
        handlingTotal = duesTotal = 0;
        double[] berths = Ports.berths(freight.buildingsOf());
        double[] crews = freight.portsCrewByKind();
        double dial = dial();
        for (Ports.Cargo kind : Ports.Cargo.values()) {
            int k = kind.ordinal();
            crew[k] = crews[k];
            design[k] = berths[k] * DESIGN_USE;
            duesRate[k] = duesRateOf(kind, berths[k]);
        }
        for (double[] sea : seaOf.values()) {
            for (int k = 0; k < KINDS; k++) tonnes[k] += sea[k];
        }
        double[] price = new double[KINDS];
        for (Ports.Cargo kind : Ports.Cargo.values()) {
            int k = kind.ordinal();
            price[k] = quoteKnown[k] ? quote[k]
                    : targetOf(crew[k], tonnes[k], berths[k], scarcityOf(boundaryOf(ports, kind), berths[k]), duesRate[k], dial);
            billed[k] = tonnes[k] * price[k];
            dues[k] = tonnes[k] * duesRate[k] * dial;
            if (!(billed[k] > 0) || !Double.isFinite(billed[k])) billed[k] = 0;
            if (!(dues[k] > 0) || !Double.isFinite(dues[k])) dues[k] = 0;
        }
        for (Map.Entry<Sector, double[]> e : seaOf.entrySet()) {
            double bill = 0;
            for (int k = 0; k < KINDS; k++) bill += e.getValue()[k] * price[k];
            if (!(bill > 0) || !Double.isFinite(bill)) continue;
            e.getKey().billForService(freight.key(), BILLED_AS, bill);
            handlingTotal += bill;
        }
        for (int k = 0; k < KINDS; k++) duesTotal += dues[k];
        freight.bookPortHandling(handlingTotal);
        freight.payPortDues(duesTotal);
    }

    /**
     * Straight after the bill: the rule, per kind. The target is crew / max(the
     * tonnes, DESIGN_USE of the berths' month) x the scarcity + the due x the
     * dial; the quote walks Freight.REPRICE_SPEED of the way to it, and is the
     * target itself the first month a kind has a berth and a crew. A kind with
     * no berth keeps its quote and bills nothing; nor does one with no crew
     * strike its first.
     */
    void closeMonth(Ports ports) {
        double[] berths = Ports.berths(freight.buildingsOf());
        double dial = dial();
        for (Ports.Cargo kind : Ports.Cargo.values()) {
            int k = kind.ordinal();
            if (!(berths[k] > 0) || !(crew[k] > 0)) continue;
            double target = targetOf(crew[k], tonnes[k], berths[k], scarcityOf(boundaryOf(ports, kind), berths[k]),
                    duesRate[k], dial);
            if (!Double.isFinite(target)) continue;
            if (!quoteKnown[k]) {
                quote[k] = target;
                quoteKnown[k] = true;
            } else {
                quote[k] += (target - quote[k]) * Freight.REPRICE_SPEED;
            }
        }
    }

    /** What crossed of a kind that could go by sea, the month billed: the tightness the berths are measured against (Ports.carriable()). */
    private static double boundaryOf(Ports ports, Ports.Cargo kind) {
        return ports == null ? 0 : ports.carriable(kind);
    }

    /* ------------------------------------------------------------ what it is doing */

    /** The handling quote in force for a kind, a tonne; 0 until one is struck (the bill meanwhile is the rule's target). */
    public double getQuote(Ports.Cargo k) { return quoteKnown[k.ordinal()] ? quote[k.ordinal()] : 0; }

    /** Whether the quote has been struck from a month with a berth and a crew. */
    public boolean isQuoteKnown(Ports.Cargo k) { return quoteKnown[k.ordinal()]; }

    /* the month billed, by kind */
    public double getTonnes(Ports.Cargo k)      { return tonnes[k.ordinal()]; }
    public double getBilled(Ports.Cargo k)      { return billed[k.ordinal()]; }
    public double getDues(Ports.Cargo k)        { return dues[k.ordinal()]; }
    public double getCrew(Ports.Cargo k)        { return crew[k.ordinal()]; }
    /** The kind's berths' month at DESIGN_USE, as the month was billed. */
    public double getDesignTonnes(Ports.Cargo k) { return design[k.ordinal()]; }
    /** The default due a tonne of the kind paid (the dial not in it), as the month was billed. */
    public double getDuesRate(Ports.Cargo k)    { return duesRate[k.ordinal()]; }

    /** The handling billed to the shippers in the month billed: the sum of their lines, Freight's "Port handling billed". */
    public double getHandlingBilled() { return handlingTotal; }

    /** The dues Freight paid the city in the month billed: its "Port dues", and the treasury's. */
    public double getDuesPaid() { return duesTotal; }

    /* ------------------------------------------------------------ save, reset, reform */

    /** The division's state into Freight's extras (keys port.<KIND>.*; a total for the dues the treasury reads back after a load). */
    public void save(Map<String, Double> extras) {
        for (Ports.Cargo kind : Ports.Cargo.values()) {
            int k = kind.ordinal();
            String p = "port." + kind.name() + ".";
            extras.put(p + "quote", quote[k]);
            extras.put(p + "known", quoteKnown[k] ? 1.0 : 0.0);
            extras.put(p + "tonnes", tonnes[k]);
            extras.put(p + "billed", billed[k]);
            extras.put(p + "dues", dues[k]);
            extras.put(p + "crew", crew[k]);
            extras.put(p + "designTonnes", design[k]);
            extras.put(p + "duesRate", duesRate[k]);
        }
        extras.put("port.handlingTotal", handlingTotal);
        extras.put("port.duesTotal", duesTotal);
    }

    /** ...and back. A save without the keys loads no quote known and nothing billed. */
    public void restore(Map<String, Double> extras) {
        for (Ports.Cargo kind : Ports.Cargo.values()) {
            int k = kind.ordinal();
            String p = "port." + kind.name() + ".";
            quote[k] = extras.getOrDefault(p + "quote", 0.0);
            quoteKnown[k] = extras.getOrDefault(p + "known", 0.0) > 0;
            tonnes[k] = extras.getOrDefault(p + "tonnes", 0.0);
            billed[k] = extras.getOrDefault(p + "billed", 0.0);
            dues[k] = extras.getOrDefault(p + "dues", 0.0);
            crew[k] = extras.getOrDefault(p + "crew", 0.0);
            design[k] = extras.getOrDefault(p + "designTonnes", 0.0);
            duesRate[k] = extras.getOrDefault(p + "duesRate", 0.0);
        }
        handlingTotal = extras.getOrDefault("port.handlingTotal", 0.0);
        duesTotal = extras.getOrDefault("port.duesTotal", 0.0);
    }

    /** Back to a city that has never worked a berth. */
    public void reset() {
        java.util.Arrays.fill(quote, 0);
        java.util.Arrays.fill(quoteKnown, false);
        java.util.Arrays.fill(tonnes, 0);
        java.util.Arrays.fill(billed, 0);
        java.util.Arrays.fill(dues, 0);
        java.util.Arrays.fill(crew, 0);
        java.util.Arrays.fill(design, 0);
        java.util.Arrays.fill(duesRate, 0);
        handlingTotal = duesTotal = 0;
    }

    /** Its money in the new unit (Denomination): the quote, the dues rate, the bill, the dues and the crews' wages. Tonnes are not money. */
    public void redenominate(double scale) {
        for (int k = 0; k < KINDS; k++) {
            quote[k] *= scale;
            billed[k] *= scale;
            dues[k] *= scale;
            crew[k] *= scale;
            duesRate[k] *= scale;
        }
        handlingTotal *= scale;
        duesTotal *= scale;
    }
}
