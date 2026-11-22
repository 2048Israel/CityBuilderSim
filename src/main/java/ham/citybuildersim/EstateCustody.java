package ham.citybuildersim;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.DoubleConsumer;
import java.util.function.IntSupplier;

/**
 * The treasury's passive owner of assets for which an estate has no heir (HT1).
 * WHY: the old no-heir path sent the property abroad; it was neither owned by
 * the treasury nor distinguished from the fund's trading and rescue books.
 */
public final class EstateCustody {
    /** Fixed receipt/disclosure slots: household cash, issuer cash, foreign coupon, local book received, asset loss, own-debt gain, face consolidated. */
    public static final int FLOW_SLOTS = 7;
    private static final int CASH = 0, ISSUER = 1, FOREIGN = 2, IN_KIND = 3, LOSS = 4, DEBT_GAIN = 5, PAPER_FACE = 6;
    private final double[] shares = new double[Equity.COMPANIES.length];
    private final Map<Integer, Double> bonds = new LinkedHashMap<>();
    private FundLedger ledger = new FundLedger();
    private double usd, cars, lossCarried, pendingPaper, pendingCoupons;
    private final double[] flows = new double[FLOW_SLOTS], pending = new double[FLOW_SLOTS];
    private boolean window = true;
    private Equity equity;
    private DebtManager debt;
    private BondMarket market;
    private DoubleConsumer cashSink = amount -> {};
    private IntSupplier month = () -> 0;

    public void attach(Equity register, DebtManager paper, BondMarket bondsMarket, DoubleConsumer sink, IntSupplier clock) {
        equity = register; debt = paper; market = bondsMarket; cashSink = sink; month = clock;
        register.attachCustody(this); bondsMarket.attachCustody(this);
    }
    private void flow(int slot, double amount) { (window ? flows : pending)[slot] += amount; }
    private void cash(int slot, double amount) {
        if (amount == 0) return;
        flow(slot, amount); cashSink.accept(amount);
    }
    public void beginMonth() {
        if (window) return;
        System.arraycopy(pending, 0, flows, 0, FLOW_SLOTS); Arrays.fill(pending, 0); window = true;
    }
    public void closeMonth() { window = false; }
    public double shares(int company) { return shares[company]; }
    public double bondFace(int id) { return bonds.getOrDefault(id, 0.0); }
    public Map<Integer, Double> bondFaces() { return java.util.Collections.unmodifiableMap(bonds); }
    public FundLedger getLedger() { return ledger; }
    public double usd() { return usd; }
    public double cars() { return cars; }
    public double lossCarried() { return lossCarried; }
    public double pendingPaper() { return pendingPaper; }
    public double pendingCoupons() { return pendingCoupons; }
    public double cashReceived() { return flows[CASH] + flows[ISSUER] + flows[FOREIGN]; }
    public double cashFromHouseholds() { return flows[CASH]; }
    public double foreignCoupon() { return flows[FOREIGN]; }
    public double noncashReceived() { return flows[IN_KIND]; }
    public double assetLoss() { return flows[LOSS]; }
    public double debtGain() { return flows[DEBT_GAIN]; }
    public double paperConsolidated() { return flows[PAPER_FACE]; }
    public double paperFinding() { return debt == null ? 0 : debt.unbackedCustodyPaper(); }

    /** Installs actual property before the tax book consolidates the self-claim. */
    public void receive(double savings, double[] units, double[] basis, Map<Integer, Double> face,
                        double dollars, double vehicles, double losses, double paper) {
        for (Map.Entry<Integer, Double> e : face.entrySet()) market.checkCustodyTransfer(e.getKey(), e.getValue());
        double paperBook = debt.reserveCustodyPaper(paper);
        pendingPaper += paper;
        for (int c = 0; c < shares.length; c++) if (units[c] != 0 || basis[c] != 0) {
            shares[c] += units[c]; equity.receiveCustodyShares(c, units[c]);
            ledger.receivedShareInKind(Equity.COMPANIES[c], units[c], basis[c], month.getAsInt());
            flow(IN_KIND, basis[c]);
        }
        for (Map.Entry<Integer, Double> e : face.entrySet()) market.transferToCustody(e.getKey(), e.getValue());
        usd += dollars; cars += vehicles; lossCarried += losses;
        flow(IN_KIND, paperBook); cash(CASH, savings);
    }
    void receiveBond(CorporateBond b, double face, double bookValue) {
        bonds.merge(b.id(), face, Double::sum);
        ledger.receivedBondInKind(b, face, bookValue, month.getAsInt()); flow(IN_KIND, bookValue);
    }
    void recordCoupon(double amount) { pendingCoupons += amount; }
    void payRecordedCoupon(double amount) {
        if (amount > pendingCoupons) throw new IllegalStateException("Custody coupon exceeds its recorded entitlement");
        pendingCoupons -= amount; cash(ISSUER, amount);
    }
    void receiveDividend(int company, double amount) {
        ledger.dividend(Equity.COMPANIES[company], amount, 0, month.getAsInt()); cash(ISSUER, amount);
    }
    void redeemBond(CorporateBond b, double face) {
        double held = bondFace(b.id());
        if (face != held) throw new IllegalStateException("Custody maturity differs from the issuer holder");
        bonds.remove(b.id()); ledger.matured(b, face, month.getAsInt()); cash(ISSUER, face);
    }
    void writeDownBond(CorporateBond b, double before, double after) {
        if (bondFace(b.id()) != before) throw new IllegalStateException("Custody bond write-down has no matching owner");
        FundLedger.Lot lot = ledger.lot(FundLedger.bondKey(b.id())); double cost = lot == null ? 0 : lot.acb();
        if (after > 0) bonds.put(b.id(), after); else bonds.remove(b.id());
        ledger.writtenDown(b, before, after, month.getAsInt());
        flow(LOSS, cost - (lot == null ? 0 : lot.acb()));
    }
    void extinguishShares(int company) {
        FundLedger.Lot lot = ledger.lot(FundLedger.shareKey(Equity.COMPANIES[company]));
        double cost = lot == null ? 0 : lot.acb(); shares[company] = 0;
        ledger.sharesGone(Equity.COMPANIES[company], month.getAsInt()); flow(LOSS, cost);
    }
    void split(int company, double factor) { shares[company] *= factor; ledger.split(Equity.COMPANIES[company], factor, month.getAsInt()); }
    public void consolidatePaper() {
        DebtManager.CustodyRetirement r = debt.consolidateCustodyPaper();
        pendingPaper -= r.face(); flow(DEBT_GAIN, r.gain()); flow(PAPER_FACE, r.face());
    }
    public void payForeignCoupon(double worldRate, double localPerUsd) {
        if (!(localPerUsd > 0)) return;
        // Same real coupon paid home as the households; principal remains USD, not reserves.
        cash(FOREIGN, HouseholdBalance.foreignCouponUsd(usd, worldRate) * localPerUsd);
    }
    public void reset() {
        Arrays.fill(shares, 0); bonds.clear(); ledger.reset();
        usd = cars = lossCarried = pendingPaper = pendingCoupons = 0;
        Arrays.fill(flows, 0); Arrays.fill(pending, 0); window = true;
    }
    public void redenominate(double scale) {
        ledger.redenominate(scale); bonds.replaceAll((id, value) -> value * scale);
        lossCarried *= scale; pendingPaper *= scale; pendingCoupons *= scale;
        for (int k = 0; k < FLOW_SLOTS; k++) { flows[k] *= scale; pending[k] *= scale; }
    }
    /** Fixed owner snapshot; live bond IDs are the existing securities, never estate IDs. */
    public static final class State {
        double[] shares, flows, pending;
        Map<Integer, Double> bonds;
        FundLedger ledger;
        double usd, cars, lossCarried, pendingPaper, pendingCoupons;
        boolean window;
        public State copy() {
            State c = new State(); c.shares = shares == null ? null : shares.clone();
            c.flows = flows == null ? null : flows.clone(); c.pending = pending == null ? null : pending.clone();
            c.bonds = bonds == null ? null : new LinkedHashMap<>(bonds); c.ledger = ledger == null ? null : ledger.copy();
            c.usd = usd; c.cars = cars; c.lossCarried = lossCarried; c.pendingPaper = pendingPaper;
            c.pendingCoupons = pendingCoupons; c.window = window; return c;
        }
    }
    public State toState() {
        State s = new State(); s.shares = shares.clone(); s.bonds = new LinkedHashMap<>(bonds); s.ledger = ledger.copy();
        s.usd = usd; s.cars = cars; s.lossCarried = lossCarried; s.pendingPaper = pendingPaper;
        s.pendingCoupons = pendingCoupons; s.flows = flows.clone(); s.pending = pending.clone(); s.window = window; return s;
    }
    public void restore(State saved) {
        reset(); if (saved == null) return; State s = saved.copy();
        if (s.shares != null) System.arraycopy(s.shares, 0, shares, 0, Math.min(shares.length, s.shares.length));
        if (s.bonds != null) bonds.putAll(s.bonds); if (s.ledger != null) ledger = s.ledger;
        usd = s.usd; cars = s.cars; lossCarried = s.lossCarried; pendingPaper = s.pendingPaper; pendingCoupons = s.pendingCoupons;
        if (s.flows != null) System.arraycopy(s.flows, 0, flows, 0, Math.min(flows.length, s.flows.length));
        if (s.pending != null) System.arraycopy(s.pending, 0, pending, 0, Math.min(pending.length, s.pending.length));
        window = s.window;
    }
}
