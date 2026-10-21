package ham.citybuildersim;

import ham.citybuildersim.Auction.Ask;
import ham.citybuildersim.Auction.Bidders;
import ham.citybuildersim.Auction.Cap;
import ham.citybuildersim.Auction.Fill;
import ham.citybuildersim.Auction.Kind;
import ham.citybuildersim.Auction.Mode;
import ham.citybuildersim.Auction.Result;

import java.io.PrintStream;

/**
 * The city's paper sold at auction: one clearing yield, and what it costs.
 *
 * WHAT THIS HAS TO PROVE (spec-city-debt.md, section 4; CD2). Auction is pure -
 * a snapshot of the bidders and the ask in, a result out - so nothing here
 * starts a Game:
 *
 *   1. EACH BIDDER BIDS BY THE RULE IT ALREADY FOLLOWS, against that rule's own
 *      constants: the households' (D32) out of savings and never past the
 *      issue, the firms' (D33) as OutwardInvestment moves their money, the
 *      world's as CapitalFlows.stockAt() sizes its stock, the bank's room at
 *      fair and nothing under.
 *
 *   2. ONE CLEARING YIELD (D5). Covered at fair, it stops at fair. Otherwise it
 *      is the lowest yield the bids cover: covered at the stop, not a hair
 *      under; everyone is filled at it, the households first and in full, the
 *      other three pro rata to their bids at it.
 *
 *   3. THE COVER AND THE TAIL are read off the bids at the ceiling and the stop
 *      over fair, each worked out here from bidsAt() rather than taken from
 *      the result.
 *
 *   4. CAPPED AND FILLED (D20). Capped, a short auction sells what was bid at
 *      the ceiling and says so; filled, the stop rises past the default tail
 *      because the world's bid rises with the yield; an issue bigger than
 *      every rule sells short even then; a named yield under fair is fair.
 *
 *   5. THE COUPON AND THE PRICE (D2). A new bond line's coupon is the stop
 *      rounded down to an eighth; a reopening keeps its line's; an amortizing
 *      bond's is the stop and prices at 100; a bill is a discount. Every price
 *      is checked by summing the schedule here, and proceeds are the price on
 *      what sold, less the fee.
 *
 *   6. PRICED ON THE CURVE (slice 3's finding). An ask that carries a pricer is
 *      priced by it: with no tail the schedule is worth exactly the curve's value
 *      of it, to the bit, and with a tail the curve's value at that spread over
 *      every month's zero rate - lower. An ask with none prices at the stop, one
 *      flat yield, as sections 1-5 do. On a curve that is not flat the two
 *      prices differ, which is what a round trip would have netted.
 *
 *   7-11 are slice 7's (the bidders as the game builds them, the long
 *      playtest's count of tails over a point) and arrive with it.
 *
 * Every fixture CAUSES its condition, and from both sides: the bank's room is
 * made ample or nearly gone, the issue small or too big for every rule.
 */
public class AuctionCheck {

    static int fails = 0;
    static int asserts = 0;
    static PrintStream out;

    static void assertTrue(String label, boolean ok) {
        asserts++;
        if (!ok) fails++;
        out.printf("%-84s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Equal within tol, or within tol of the expected figure's size, whichever is larger. */
    static void close(String label, double actual, double expected, double tol) {
        asserts++;
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-84s %s  %,.8f against %,.8f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    /** Equal within an absolute tolerance, for prices per 100. */
    static void near(String label, double actual, double expected, double abs) {
        asserts++;
        boolean ok = Math.abs(actual - expected) <= abs;
        if (!ok) fails++;
        out.printf("%-84s %s  %,.10f against %,.10f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    /* =====================================================================
       THE FIXTURE: the study's calm city (debt-model.py CALM), in the game's terms

       A year of output D$5.4B, savings past the cushion D$2B, businesses'
       idle money D$1.5B, hot money here D$150M, deposits 1.5%, the world 2%.
       The bank's room is risk-weighted, as Bank holds the city's paper at
       Bank.RISK_CITY, so its face is the room over that.
       ===================================================================== */

    /** The fixed cost of a sale: the study's AUCTION_FEE, the one Game charges. */
    static final double FEE = 12_000;

    /** The 10-year's fair yield in the calm city (spec-city-debt.md, section 4.3's table: 3.602%). */
    static final double FAIR = .03602;

    /** D$300M of face, the table's issue. */
    static final double FACE = 3e8;

    static final class Fx {
        double deposit = .015, world = .02, premium = 0;
        double spare = 2.0e9, idle = 1.5e9, held = 0, stock = 1.5e8;
        double monthlyGdp = 5.4e9 / 12;
        double bankRoom = 1.0e9;     // risk-weighted
        double bankExtra = 0;
        boolean panic = false;

        Bidders b() {
            return new Bidders(deposit, world, premium, spare, idle, held, stock, panic, monthlyGdp,
                    bankRoom / Bank.RISK_CITY, bankExtra);
        }

        /** The bank's room nearly gone: D$20M risk-weighted, D$100M of face. */
        Fx tight() {
            bankRoom = 2e7;
            return this;
        }
    }

    static Fx calm() {
        return new Fx();
    }

    static Ask bond(double face, double fair) {
        return new Ask(Kind.BOND, 120, face, fair, Double.NaN, FEE);
    }

    /** The paper's value per 100 at one flat yield, summed here with Math.pow and not Debt.presentValue. */
    static double bulletPrice(double coupon, int months, double y) {
        double v = 0;
        for (int t = 1; t <= months; t++) {
            double flow = 100 * coupon / 12 + (t == months ? 100 : 0);
            v += flow / Math.pow(1 + y / 12, t);
        }
        return v;
    }

    /** The same for an amortizing bond: a yearly slice of principal, the coupon monthly on what is left. */
    static double amortizingPrice(double coupon, int years, double y) {
        double balance = 100;
        double v = 0;
        for (int t = 1; t <= years * 12; t++) {
            double flow = balance * coupon / 12;
            if (t % 12 == 0) {
                flow += 100.0 / years;
                balance -= 100.0 / years;
            }
            v += flow / Math.pow(1 + y / 12, t);
        }
        return v;
    }

    /** What the cover is: each bidder's bid at the ceiling, at most the whole issue, over the issue. */
    static double coverAt(double ceiling, Bidders b, Ask a) {
        Fill top = Auction.bidsAt(ceiling, b, a);
        double sum = Math.min(top.households(), a.face()) + Math.min(top.firms(), a.face())
                + Math.min(top.world(), a.face()) + Math.min(top.bank(), a.face());
        return sum / a.face();
    }

    public static void main(String[] args) {

        out = System.out;

        section1();
        section2();
        section3();
        section4();
        section5();
        section6();

        out.println();
        if (fails == 0) {
            out.println("The city's paper clears at one yield, and every bidder bids by its own rule (" + asserts + " assertions).");
        } else {
            out.println(fails + " FAILED");
            System.exit(1);
        }
    }

    /* ============== 1. each bidder's rule against its constants ============== */
    static void section1() {
        out.println("--- 1. each bidder's rule against its constants ---");

        /* THE HOUSEHOLDS (D32): a fifth of the issue for each point over deposits. */
        Fx f = calm();
        Bidders b = f.b();
        double s = .01;
        close("households a point over deposits take appetite x spread x face",
                Auction.householdsAt(b.depositRate() + s, b, FACE),
                HouseholdBalance.HOUSEHOLD_PAPER_APPETITE * s * FACE, 1e-9);
        close("...which at the study's fair yield is its table's D$126.1M",
                Auction.householdsAt(FAIR, b, FACE), 126.1e6, 5e-4);

        double wide = 2.0 / HouseholdBalance.HOUSEHOLD_PAPER_APPETITE;
        close("a spread wide enough to want twice the issue is capped at the issue",
                Auction.householdsAt(b.depositRate() + wide, b, FACE), FACE, 1e-12);

        double most = .8 / HouseholdBalance.HOUSEHOLD_PAPER_APPETITE;
        double eight = Auction.householdsAt(b.depositRate() + most, b, FACE);
        close("D32: a spread that wants four fifths of the issue gets four fifths", eight, .8 * FACE, 1e-9);
        assertTrue("...which is more than the old half-the-issue cap would have let through",
                eight > HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE * FACE);

        Fx thin = calm();
        thin.spare = 1e7;
        close("thin savings cap them at savings past the cushion, whatever the spread",
                Auction.householdsAt(b.depositRate() + wide, thin.b(), FACE), thin.spare, 1e-12);
        close("...and at a modest spread too, where the rule would take more than they have",
                Auction.householdsAt(b.depositRate() + s, thin.b(), FACE), thin.spare, 1e-12);

        close("at the deposit rate they take nothing", Auction.householdsAt(b.depositRate(), b, FACE), 0, 0);
        close("under the deposit rate they take nothing", Auction.householdsAt(b.depositRate() - .005, b, FACE), 0, 0);
        Fx neg = calm();
        neg.deposit = -.01;
        close("a negative deposit rate is read as zero: the spread is the yield itself",
                Auction.householdsAt(.01, neg.b(), FACE), HouseholdBalance.HOUSEHOLD_PAPER_APPETITE * .01 * FACE, 1e-9);

        /* THE FIRMS (D33): OutwardInvestment's rule, the paper beside the deposit and abroad. */
        Fx fm = calm();
        Bidders fb = fm.b();
        double base = Math.max(fb.depositRate(), fb.worldRate());
        double target = fm.idle * Math.min(OutwardInvestment.MAX_SHARE, OutwardInvestment.APPETITE * s);
        close("firms a point over the better of deposit and world: target share of idle money, at OUT_SPEED",
                Auction.firmsAt(base + s, fb), target * OutwardInvestment.OUT_SPEED, 1e-9);
        assertTrue("...the target being APPETITE x the spread of their idle money, a point under MAX_SHARE",
                OutwardInvestment.APPETITE * s < OutwardInvestment.MAX_SHARE
                        && Math.abs(target - fm.idle * OutwardInvestment.APPETITE * s) < 1e-3);

        close("at the world's rate (the better of the two) they bid nothing", Auction.firmsAt(fb.worldRate(), fb), 0, 0);
        close("under it they bid nothing", Auction.firmsAt(fb.worldRate() - .01, fb), 0, 0);
        Fx dear = calm();
        dear.deposit = .05;
        close("when the deposit pays as much as the paper they bid nothing", Auction.firmsAt(.05, dear.b()), 0, 0);
        close("...and nothing under it, though the world's rate is lower still", Auction.firmsAt(.04, dear.b()), 0, 0);
        assertTrue("...but over the deposit they bid", Auction.firmsAt(.06, dear.b()) > 0);

        Fx holding = calm();
        holding.held = target / 2;
        close("paper they already hold subtracts from the target",
                Auction.firmsAt(base + s, holding.b()), (target - holding.held) * OutwardInvestment.OUT_SPEED, 1e-9);
        holding.held = target * 1.5;
        close("...and holding more than the target they bid nothing", Auction.firmsAt(base + s, holding.b()), 0, 0);

        double capSpread = OutwardInvestment.MAX_SHARE / OutwardInvestment.APPETITE;
        double ceil = fm.idle * OutwardInvestment.MAX_SHARE * OutwardInvestment.OUT_SPEED;
        close("the share stops at MAX_SHARE of their idle money, where the spread reaches it",
                Auction.firmsAt(base + capSpread, fb), ceil, 1e-9);
        close("...and a far wider spread asks no more", Auction.firmsAt(base + 1.0, fb), ceil, 1e-9);

        /* THE WORLD (D7): CapitalFlows' rule, the auction's yield as the city's rate. */
        Fx wf = calm();
        wf.premium = .005;
        Bidders wb = wf.b();
        for (double y : new double[]{.03, .05, .12}) {
            double stockAt = new CapitalFlows().stockAt(wb.depositRate(), y, wb.worldRate(), wb.countryPremium(), wb.monthlyGdp());
            close("the world at " + (y * 100) + "%: (stockAt - stock) x ARRIVAL_SPEED",
                    Auction.worldAt(y, wb), Math.max(0, stockAt - wb.worldStock()) * CapitalFlows.ARRIVAL_SPEED, 1e-9);
        }
        Fx full = calm();
        full.stock = 1e12;
        close("hot money already past its target bids nothing", Auction.worldAt(.05, full.b()), 0, 0);
        Fx panic = calm();
        panic.panic = true;
        assertTrue("(the same city bids when it is not panicking)", Auction.worldAt(.05, calm().b()) > 0);
        close("a world in a panic bids nothing, whatever the yield", Auction.worldAt(.12, panic.b()), 0, 0);

        double beyond = wb.worldRate() + wb.countryPremium() + CapitalFlows.MAX_SPREAD;
        double flatA = Auction.worldAt(beyond + .01, wb);
        double flatB = Auction.worldAt(beyond + .05, wb);
        close("past MAX_SPREAD over its rate and premium the bid is flat", flatA, flatB, 1e-12);
        close("...at the MAX_SPREAD stock less what is here, at ARRIVAL_SPEED",
                flatA, (CapitalFlows.MAX_SPREAD * CapitalFlows.APPETITE * wb.monthlyGdp() * 12 - wb.worldStock())
                        * CapitalFlows.ARRIVAL_SPEED, 1e-9);
        assertTrue("...while under it the bid still rises with the yield", Auction.worldAt(.05, wb) < Auction.worldAt(.10, wb));

        /* THE BANK: its whole room at fair plus what its funding needs, and nothing under. */
        Fx bf = calm();
        Bidders bk = bf.b();
        close("the bank's room, as the caller states it, is the risk-weighted room over RISK_CITY",
                bk.bankRoomFace(), bf.bankRoom / Bank.RISK_CITY, 1e-12);
        close("at fair the bank bids its whole room", Auction.bankAt(FAIR, bk, FAIR), bk.bankRoomFace(), 0);
        close("a hair under fair it bids nothing", Auction.bankAt(FAIR - 1e-9, bk, FAIR), 0, 0);
        close("over fair it bids the same room: a step, not a slope", Auction.bankAt(FAIR + .05, bk, FAIR), bk.bankRoomFace(), 0);
        Fx need = calm();
        need.bankExtra = .004;
        close("with funding past the floor it bids at fair plus the extra", Auction.bankAt(FAIR + .004, need.b(), FAIR), need.b().bankRoomFace(), 0);
        close("...and not a hair under that", Auction.bankAt(FAIR + .004 - 1e-9, need.b(), FAIR), 0, 0);

        /* bidsAt is the four, for the screen's chart. */
        Ask ask = bond(FACE, FAIR);
        Fill at = Auction.bidsAt(.05, b, ask);
        assertTrue("bidsAt is the four rules at one yield, the households on the issue",
                at.households() == Auction.householdsAt(.05, b, FACE) && at.firms() == Auction.firmsAt(.05, b)
                        && at.world() == Auction.worldAt(.05, b) && at.bank() == Auction.bankAt(.05, b, FAIR));
        close("...and total() is their sum", at.total(), at.households() + at.firms() + at.world() + at.bank(), 1e-12);
        close("an empty city bids nothing at any yield", Auction.bidsAt(.2, Bidders.none(), ask).total(), 0, 0);
    }

    /* ============== 2. one clearing yield ============== */
    static void section2() {
        out.println("--- 2. one clearing yield ---");

        Ask ask = bond(FACE, FAIR);

        Result easy = Auction.run(ask, calm().b(), Cap.DEFAULT);
        assertTrue("a city the bids cover at fair stops at fair", easy.stop() == FAIR);
        close("...with no tail", easy.tail(), 0, 0);
        close("...and sells the whole issue", easy.sold(), FACE, 0);
        assertTrue("...not short", !easy.shortSale());

        Bidders tight = calm().tight().b();
        assertTrue("with the bank's room nearly gone the bids do not cover at fair",
                Auction.bidsAt(FAIR, tight, ask).total() < FACE);
        Result r = Auction.run(ask, tight, Cap.DEFAULT);
        assertTrue("...so it stops over fair", r.stop() > FAIR);
        assertTrue("...and under the highest yield the city takes", r.stop() < FAIR + Auction.MAX_TAIL);
        assertTrue("at the stop the bids cover the issue", Auction.bidsAt(r.stop(), tight, ask).total() >= FACE);
        assertTrue("a thousandth of a basis point under it they do not",
                Auction.bidsAt(r.stop() - 1e-5, tight, ask).total() < FACE);
        assertTrue("...nor two tolerances under it: the search found the stop to STOP_TOLERANCE",
                Auction.bidsAt(r.stop() - 2 * Auction.STOP_TOLERANCE, tight, ask).total() < FACE);
        assertTrue("the whole issue sold", r.sold() == FACE && !r.shortSale());

        Fill bids = Auction.bidsAt(r.stop(), tight, ask);
        Fill fills = r.fills();
        close("the households are filled in full at the stop", fills.households(), bids.households(), 1e-12);
        double rf = fills.firms() / bids.firms();
        double rw = fills.world() / bids.world();
        double rb = fills.bank() / bids.bank();
        close("the other three are filled pro rata: the firms' share of their bid equals the world's", rf, rw, 1e-9);
        close("...and the bank's equals the world's", rb, rw, 1e-9);
        assertTrue("...a share under the whole bid, because the households were served first and the bids overshoot",
                rw > 0 && rw < 1);
        close("the fills sum to the sold face", fills.total(), r.sold(), 1e-9);
        close("the stop is one yield for all: the result carries nothing but it", r.tail(), r.stop() - FAIR, 1e-15);

        /* The bank is a step: when its room is what covers, the stop is the step. */
        Fx stepped = calm();
        stepped.bankExtra = .004;
        Bidders sb = stepped.b();
        double step = FAIR + stepped.bankExtra;
        assertTrue("(a fixture where the others cannot cover even at the bank's step)",
                Auction.bidsAt(step, sb, ask).total() - Auction.bankAt(step, sb, FAIR) < FACE);
        Result st = Auction.run(ask, sb, Cap.DEFAULT);
        assertTrue("the stop is the bank's step, found from above", st.stop() >= step && st.stop() - step <= Auction.STOP_TOLERANCE);
        close("...and the bank takes what the others left", st.fills().bank(),
                FACE - Math.min(Auction.householdsAt(st.stop(), sb, FACE), FACE) - st.fills().firms() - st.fills().world(), 1e-9);
        assertTrue("under the step nobody covers it", Auction.bidsAt(step - 1e-5, sb, ask).total() < FACE);

        /* Where the bids overshoot the issue a long way, the pro rata share is far under the whole bid. */
        Fill sBids = Auction.bidsAt(st.stop(), sb, ask);
        double sf = st.fills().firms() / sBids.firms();
        double sw = st.fills().world() / sBids.world();
        double sk = st.fills().bank() / sBids.bank();
        assertTrue("the bank's room is far more than the issue, so each bidder gets a small share of its bid", sk < .1);
        close("...the firms' share of their bid is the bank's", sf, sk, 1e-9);
        close("...the world's share of its bid is the bank's", sw, sk, 1e-9);
        close("...the households are still filled in full, first", st.fills().households(), sBids.households(), 1e-12);
        close("...and the four fills sum to the issue", st.fills().total(), FACE, 1e-9);
    }

    /* ============== 3. cover and tail ============== */
    static void section3() {
        out.println("--- 3. cover and tail ---");

        Ask ask = bond(FACE, FAIR);
        Bidders ample = calm().b();
        Result easy = Auction.run(ask, ample, Cap.DEFAULT);
        double ceiling = FAIR + Auction.MAX_TAIL;
        close("the ceiling of the default cap is fair plus MAX_TAIL", easy.ceiling(), ceiling, 1e-15);
        double want = coverAt(ceiling, ample, ask);
        close("the cover is each bid at the ceiling, at most the issue, over the issue", easy.cover(), want, 1e-12);
        assertTrue("an easy sale is covered more than once", easy.cover() > 1);
        Fill top = Auction.bidsAt(ceiling, ample, ask);
        assertTrue("the bank's room is many times the issue, but it counts at most the issue",
                top.bank() > 2 * FACE && easy.cover() < top.total() / FACE);
        close("tail is stop over fair: none when it clears at fair", easy.tail(), easy.stop() - FAIR, 1e-15);

        Bidders tight = calm().tight().b();
        Result mid = Auction.run(ask, tight, Cap.DEFAULT);
        close("a sale that needs a tail: tail is stop over fair", mid.tail(), mid.stop() - FAIR, 1e-15);
        assertTrue("...and the tail is positive", mid.tail() > 0);
        close("...and its cover is read at the ceiling, not the stop", mid.cover(), coverAt(ceiling, tight, ask), 1e-12);
        assertTrue("...still over one, because it sold", mid.cover() >= 1);

        Ask big = bond(10 * FACE, FAIR);
        Result shorted = Auction.run(big, tight, Cap.DEFAULT);
        double wantShort = coverAt(ceiling, tight, big);
        close("an issue ten times the size: cover computed from bidsAt at the ceiling", shorted.cover(), wantShort, 1e-12);
        assertTrue("...is under one", shorted.cover() < 1);
        assertTrue("...and it sold short", shorted.shortSale());
        close("...its tail is the whole default tail", shorted.tail(), Auction.MAX_TAIL, 1e-12);
        close("...and cover x face is what was sold, since no bidder there bids past the issue",
                shorted.cover() * big.face(), shorted.sold(), 1e-9);
    }

    /* ============== 4. capped and filled ============== */
    static void section4() {
        out.println("--- 4. capped and filled ---");

        Bidders tight = calm().tight().b();
        Ask twice = bond(2 * FACE, FAIR);
        double ceiling = FAIR + Auction.MAX_TAIL;

        Result capped = Auction.run(twice, tight, Cap.DEFAULT);
        Fill atCeiling = Auction.bidsAt(ceiling, tight, twice);
        assertTrue("(a fixture where the bids fall short at the ceiling)", atCeiling.total() < twice.face());
        assertTrue("capped, a short sale says so", capped.shortSale());
        close("...the stop is the ceiling", capped.stop(), ceiling, 1e-12);
        close("...it sells exactly what was bid at the ceiling", capped.sold(), atCeiling.total(), 1e-9);
        close("...and is short by the rest", capped.shortBy(), twice.face() - atCeiling.total(), 1e-9);
        close("...each bidder is filled what it bid at the ceiling",
                capped.fills().bank() + capped.fills().firms() + capped.fills().world() + capped.fills().households(),
                atCeiling.total(), 1e-9);
        close("...the households in full", capped.fills().households(), atCeiling.households(), 1e-12);

        Result filled = Auction.run(twice, tight, Cap.FILL);
        assertTrue("the same fixture filled is not short", !filled.shortSale() && filled.sold() == twice.face());
        assertTrue("...it covers at a stop over the default tail", filled.stop() > ceiling && filled.tail() > Auction.MAX_TAIL);
        assertTrue("...and the world bids more at that stop than at the capped one",
                Auction.worldAt(filled.stop(), tight) > Auction.worldAt(capped.stop(), tight));
        assertTrue("...covered at its stop", Auction.bidsAt(filled.stop(), tight, twice).total() >= twice.face());
        assertTrue("...and not a hair under it", Auction.bidsAt(filled.stop() - 1e-5, tight, twice).total() < twice.face());
        double fillCeiling = tight.worldRate() + tight.countryPremium() + CapitalFlows.MAX_SPREAD;
        close("Fill it's ceiling is where no rule adds anything: the world's rate, premium and MAX_SPREAD",
                filled.ceiling(), fillCeiling, 1e-15);
        assertTrue("...and the stop is under it", filled.stop() <= fillCeiling);

        Ask huge = bond(1e11, FAIR);
        Result nope = Auction.run(huge, tight, Cap.FILL);
        assertTrue("an issue bigger than every rule takes sells short even when filled", nope.shortSale());
        close("...at the ceiling where nothing adds more", nope.stop(), fillCeiling, 1e-15);
        close("...selling what the rules bid there", nope.sold(), Auction.bidsAt(fillCeiling, tight, huge).total(), 1e-9);
        assertTrue("...which is less than the issue", nope.sold() < huge.face() && nope.shortBy() > 0);
        close("...and the fills still sum to what sold", nope.fills().total(), nope.sold(), 1e-9);

        /* A named yield. */
        Result under = Auction.run(twice, tight, Cap.at(FAIR - .01));
        close("a named yield under fair is treated as fair: the ceiling", under.ceiling(), FAIR, 0);
        assertTrue("...the stop is fair, short where fair does not cover",
                under.stop() == FAIR && under.shortSale() && under.sold() < twice.face());
        close("...and it sells what was bid at fair", under.sold(), Auction.bidsAt(FAIR, tight, twice).total(), 1e-9);
        Result underEasy = Auction.run(bond(FACE, FAIR), calm().b(), Cap.at(FAIR - .01));
        assertTrue("...and where fair covers, it sells all at fair", underEasy.stop() == FAIR && !underEasy.shortSale());
        double named = FAIR + .02;
        Result over = Auction.run(twice, tight, Cap.at(named));
        close("a named yield over fair is the ceiling", over.ceiling(), named, 1e-15);
        assertTrue("...and the sale clears under it when the bids cover there", !over.shortSale() && over.stop() <= named && over.stop() > ceiling);
        Result none = Auction.run(twice, tight, Cap.tail(0));
        close("a tail of nothing is a ceiling at fair", none.ceiling(), FAIR, 0);

        /* How the screen says which cap it is. */
        assertTrue("the default cap is a tail of MAX_TAIL", Cap.DEFAULT.mode() == Mode.TAIL && Cap.DEFAULT.value() == Auction.MAX_TAIL);
        assertTrue("Fill it says it is Fill it", Cap.FILL.fill() && Cap.FILL.mode() == Mode.FILL);
        assertTrue("a named yield is not Fill it, and carries the yield", !Cap.at(.05).fill() && Cap.at(.05).mode() == Mode.YIELD && Cap.at(.05).value() == .05);

        /* Nobody to sell to, and nothing to sell. */
        Result nobody = Auction.run(bond(FACE, FAIR), Bidders.none(), Cap.DEFAULT);
        assertTrue("a city nobody bids on sells nothing, and says short", nobody.sold() == 0 && nobody.shortSale());
        assertTrue("...raises nothing, and pays no fee for it", nobody.proceeds() == 0 && nobody.fills().total() == 0);
        Result empty = Auction.run(bond(0, FAIR), calm().b(), Cap.DEFAULT);
        assertTrue("an ask of nothing is an empty result", empty.isEmpty() && empty.sold() == 0 && !empty.shortSale());
        assertTrue("...with no proceeds", empty.proceeds() == 0 && empty.shortBy() == 0);
        assertTrue("a real sale is not empty", !capped.isEmpty());
    }

    /* ============== 5. the coupon and the price ============== */
    static void section5() {
        out.println("--- 5. the coupon and the price ---");

        Bidders ample = calm().b();
        Bidders tight = calm().tight().b();
        Ask ask = bond(FACE, FAIR);

        Result r = Auction.run(ask, tight, Cap.DEFAULT);
        assertTrue("(a stop over fair, so the coupon has something to round)", r.stop() > FAIR);
        assertTrue("a new bond line's coupon is the stop rounded down to the step, never over it",
                r.coupon() <= r.stop() + 1e-12 && r.stop() < r.coupon() + Auction.COUPON_STEP);
        double steps = r.coupon() / Auction.COUPON_STEP;
        close("...a whole number of steps", steps, Math.rint(steps), 1e-9);
        double p = r.pricePer100();
        close("its price is the schedule summed here at the stop", p, bulletPrice(r.coupon(), 120, r.stop()), 1e-12);
        assertTrue("...at most 100 (the coupon never over the yield)", p <= 100 + 1e-9);
        assertTrue("...and over 99 for a 10-year (an eighth of a point of coupon is about one point of price)", p > 99);
        assertTrue("...and never more than a step's annuity under par, whatever the stop",
                p > 100 - 100 * Auction.COUPON_STEP * 10);
        close("proceeds are the price on what sold, less the fee", r.proceeds(), p / 100 * r.sold() - FEE, 1e-9);

        /* The guard against floating error: a stop that is a multiple of the step loses no step. */
        boolean allSteps = true;
        for (int k = 8; k <= 48; k++) {
            double fair = k * 125 / 100000.0;
            Result q = Auction.run(bond(FACE, fair), ample, Cap.DEFAULT);
            allSteps &= q.stop() == fair && Math.abs(q.coupon() - fair) < 1e-12;
        }
        assertTrue("a stop that is exactly a multiple of the step keeps it, for every eighth from 1% to 6%", allSteps);
        Result atPar = Auction.run(bond(FACE, .04), ample, Cap.DEFAULT);
        near("...so a bond sold at a stop on a step prices at par", atPar.pricePer100(), 100, 1e-9);

        /* A reopening takes its line's coupon. */
        Result easy = Auction.run(ask, ample, Cap.DEFAULT);
        Ask rich = new Ask(Kind.BOND, 120, FACE, FAIR, FAIR + .01, FEE);
        Result above = Auction.run(rich, ample, Cap.DEFAULT);
        close("a reopening keeps the line's coupon", above.coupon(), FAIR + .01, 0);
        assertTrue("...a line coupon above the stop prices over 100", above.pricePer100() > 100);
        close("...as the schedule summed here prices it", above.pricePer100(), bulletPrice(FAIR + .01, 120, above.stop()), 1e-12);
        Ask thin = new Ask(Kind.BOND, 120, FACE, FAIR, FAIR - .01, FEE);
        Result below = Auction.run(thin, ample, Cap.DEFAULT);
        assertTrue("a line coupon below the stop prices under 100", below.pricePer100() < 100);
        close("...as the schedule summed here prices it", below.pricePer100(), bulletPrice(FAIR - .01, 120, below.stop()), 1e-12);
        Ask level = new Ask(Kind.BOND, 120, FACE, FAIR, FAIR, FEE);
        near("a line coupon at the stop prices at 100", Auction.run(level, ample, Cap.DEFAULT).pricePer100(), 100, 1e-9);
        assertTrue("(the new line's stop here is the same fair yield)", easy.stop() == above.stop() && easy.stop() == below.stop());

        /* The amortizing bond: the coupon is the stop, and the price is par. */
        for (int years : new int[]{5, 20, 30}) {
            for (Bidders b : new Bidders[]{ample, tight}) {
                Ask am = new Ask(Kind.AMORTIZING, years * 12, FACE, FAIR, Double.NaN, FEE);
                Result a = Auction.run(am, b, Cap.DEFAULT);
                String tag = years + "-year amortizing, stop " + (a.stop() > FAIR ? "over" : "at") + " fair";
                close(tag + ": the coupon is the stop itself", a.coupon(), a.stop(), 0);
                near(tag + ": prices at 100", a.pricePer100(), 100, 1e-9);
                near(tag + ": as the schedule summed here prices it", amortizingPrice(a.coupon(), years, a.stop()), 100, 1e-9);
            }
        }
        double[] am = Auction.amortizingPer100(.05, 10);
        assertTrue("an amortizing schedule runs a payment a month for its years", am.length == 120);
        close("...the first is a month's coupon on the whole 100", am[0], 100 * .05 / 12, 1e-12);
        close("...the twelfth adds the first slice, a tenth of the principal", am[11], 100 * .05 / 12 + 10, 1e-12);
        close("...and the last is the last slice with a month's coupon on it", am[119], 10 * (1 + .05 / 12), 1e-12);
        double[] bullet = Auction.bulletPer100(.06, 24);
        assertTrue("a bullet schedule is a coupon a month and the principal at the end",
                bullet.length == 24 && bullet[0] == 100 * .06 / 12 && bullet[23] == 100 * .06 / 12 + 100);

        /* A bill: no coupon; a discount. */
        Fx ampleBills = calm();
        ampleBills.deposit = .01;
        ampleBills.world = .015;
        Fx tightBills = calm().tight();
        tightBills.deposit = .01;
        tightBills.world = .015;
        double billFair = .0195;
        for (int months : Auction.BILL_MONTHS) {
            Ask bill = new Ask(Kind.BILL, months, FACE, billFair, Double.NaN, FEE);
            Result b0 = Auction.run(bill, ampleBills.b(), Cap.DEFAULT);
            Result b1 = Auction.run(bill, tightBills.b(), Cap.FILL);
            for (Result b : new Result[]{b0, b1}) {
                String tag = months + "-month bill, stop " + (b.stop() > billFair ? "over" : "at") + " fair";
                close(tag + ": no coupon", b.coupon(), 0, 0);
                close(tag + ": price is 100 over (1 + stop/12) to the months", b.pricePer100(),
                        100 / Math.pow(1 + b.stop() / 12, months), 1e-12);
                close(tag + ": the discount rate", b.discountRate(), (100 - b.pricePer100()) / 100 * 12 / months, 1e-12);
            }
            assertTrue(months + "-month bill: one fixture cleared at fair and one above it", b0.stop() == billFair && b1.stop() > billFair);
        }
        assertTrue("paper that is not a bill has no discount rate", Double.isNaN(easy.discountRate()));

        /* Proceeds on a short sale are on what sold. */
        Result sh = Auction.run(bond(10 * FACE, FAIR), tight, Cap.DEFAULT);
        assertTrue("(a short sale)", sh.shortSale() && sh.sold() < 10 * FACE);
        close("proceeds of a short sale are the price on what sold, less the fee",
                sh.proceeds(), sh.pricePer100() / 100 * sh.sold() - FEE, 1e-9);
        Ask free = new Ask(Kind.BOND, 120, FACE, FAIR, Double.NaN, 0);
        Result nf = Auction.run(free, tight, Cap.DEFAULT);
        close("with no fee the proceeds are the price on the face", nf.proceeds(), nf.pricePer100() / 100 * FACE, 1e-12);

        /* The dials. */
        assertTrue("bills are sold at 1, 3, 6 and 12 months", java.util.Arrays.equals(Auction.BILL_MONTHS, new int[]{1, 3, 6, 12}));
        assertTrue("amortizing bonds at 5 to 30 years in fives", java.util.Arrays.equals(Auction.AMORTIZING_YEARS, new int[]{5, 10, 15, 20, 25, 30}));
        close("a hundredth of a basis point is 1e-6, and the stop is found to a tenth of that", Auction.STOP_TOLERANCE * 10, 1e-6, 1e-12);
        close("the coupon step is an eighth of a point", Auction.COUPON_STEP * 8, .01, 1e-12);
    }

    /* =====================================================================
       6. PRICED ON THE CURVE

       A curve that rises: the dial at 3% and a term premium of 8 basis
       points a year past the first, to 4 points. Flat or not, the rule is
       the same; on a rising one a flat price at the stop and the curve's
       value of a schedule are different numbers, and the pricer is what makes
       the sale's price the one the buyback reads.
       ===================================================================== */

    static void section6() {
        out.println("--- 6. priced on the curve ---");
        YieldCurve curve = YieldCurve.of(YieldCurve.flatPath(.03), 0, 1.0,
                m -> m <= 12 ? 0 : Math.min(.04, .0008 * (m - 12) / 12.0));
        assertTrue("(the fixture's curve rises: the ten-year zero over the one-year)", curve.zero(120) > curve.zero(12) + .005);
        Auction.Pricer pricer = curve::pvAtSpread;
        int months = 120;
        double fair = curve.parYield(months);

        /* The curve's own pricing. */
        double[] flows = Auction.bulletPer100(.05, months);
        assertTrue("a spread of nothing prices a schedule at the curve's value of it, to the bit", curve.pvAtSpread(flows, 0) == curve.pv(flows));
        assertTrue("...a spread lowers it", curve.pvAtSpread(flows, .001) < curve.pvAtSpread(flows, 0)
                && curve.pvAtSpread(flows, .01) < curve.pvAtSpread(flows, .001));
        double manual = 0;
        for (int t = 1; t <= months; t++) manual += flows[t - 1] / Math.pow(1 + (curve.zero(t) + .0025) / 12, t);
        close("...and a spread is added to every month's zero rate, as summed here", curve.pvAtSpread(flows, .0025), manual, 1e-12);

        /* A sale with no tail is worth the curve's value of the schedule. */
        Ask ask = new Ask(Kind.BOND, months, FACE, fair, Double.NaN, FEE, pricer);
        Result none = Auction.run(ask, calm().b(), Cap.DEFAULT);
        assertTrue("(an ample bank: the sale clears at fair, with no tail)", none.stop() == fair && none.tail() == 0 && !none.shortSale());
        double[] sold = Auction.bulletPer100(none.coupon(), months);
        assertTrue("with no tail the price is the curve's own value of the schedule it sells, to the bit", none.pricePer100() == curve.pv(sold));
        close("...and the proceeds are that price on the face, less the fee", none.proceeds(), none.pricePer100() / 100 * FACE - FEE, 1e-9);

        /* A tail is a spread over the curve. */
        Result tail = Auction.run(ask, calm().tight().b(), Cap.DEFAULT);
        assertTrue("(a tight bank: the sale clears over fair, sold in full)", tail.tail() > 0 && !tail.shortSale() && tail.stop() > fair);
        double[] soldTail = Auction.bulletPer100(tail.coupon(), months);
        assertTrue("a tail prices the schedule on the curve at that spread, to the bit",
                tail.pricePer100() == curve.pvAtSpread(soldTail, tail.stop() - fair));
        double sum = 0;
        for (int t = 1; t <= months; t++) sum += soldTail[t - 1] / Math.pow(1 + (curve.zero(t) + tail.tail()) / 12, t);
        close("...as summed here, every month's zero rate and the tail", tail.pricePer100(), sum, 1e-12);
        assertTrue("...and it is lower than the same schedule's value on the curve with no tail",
                tail.pricePer100() < curve.pv(soldTail));

        /* On a curve that rises, this is not the flat price. */
        Ask line = new Ask(Kind.BOND, months, FACE, fair, fair - .01, FEE, pricer);
        Result reopened = Auction.run(line, calm().b(), Cap.DEFAULT);
        double[] lineFlows = Auction.bulletPer100(reopened.coupon(), months);
        assertTrue("a line reopened a point under fair is worth the curve's value of its schedule",
                reopened.pricePer100() == curve.pv(lineFlows));
        assertTrue("...which on a rising curve is not what one flat yield at the stop prices it at",
                Math.abs(reopened.pricePer100() - Debt.presentValue(lineFlows, reopened.stop())) > 1e-4);
        Ask unpriced = new Ask(Kind.BOND, months, FACE, fair, fair - .01, FEE);
        Result flat = Auction.run(unpriced, calm().b(), Cap.DEFAULT);
        assertTrue("an ask with no pricer prices at the stop, one flat yield, as before",
                flat.pricePer100() == Debt.presentValue(lineFlows, flat.stop()));
        assertTrue("(an ask built without a pricer carries none)", unpriced.pricer() == null && line.pricer() != null);

        /* The other two kinds. */
        double parAmortizing = DebtManager.amortizingParYield(curve, 10);
        Ask am = new Ask(Kind.AMORTIZING, months, FACE, parAmortizing, Double.NaN, FEE, pricer);
        Result amNone = Auction.run(am, calm().b(), Cap.DEFAULT);
        near("an amortizing bond with no tail is worth 100 on the curve it was struck on", amNone.pricePer100(), 100, 1e-9);
        Result amTail = Auction.run(am, calm().tight().b(), Cap.DEFAULT);
        assertTrue("...and with a tail it still sells near 100: the coupon is the stop, so it rises with the spread that discounts it",
                amTail.tail() > 0 && Math.abs(amTail.pricePer100() - 100) < .05);

        for (int m : Auction.BILL_MONTHS) {
            Ask bill = new Ask(Kind.BILL, m, FACE, curve.zero(m), Double.NaN, FEE, pricer);
            Result b0 = Auction.run(bill, calm().b(), Cap.DEFAULT);
            near(m + "-month bill with no tail: 100 times the curve's discount factor", b0.pricePer100(), 100 * curve.factor(m), 1e-12);
        }
        Fx tightBill = calm().tight();
        Ask bill6 = new Ask(Kind.BILL, 6, FACE, curve.zero(6), Double.NaN, FEE, pricer);
        Result b6 = Auction.run(bill6, tightBill.b(), Cap.FILL);
        close("a bill with a tail: 100 over (1 + (zero + tail)/12) to the months", b6.pricePer100(),
                100 / Math.pow(1 + (curve.zero(6) + b6.tail()) / 12, 6), 1e-12);
    }
}
