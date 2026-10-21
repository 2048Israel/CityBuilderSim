package ham.citybuildersim;

import ham.citybuildersim.Auction.Ask;
import ham.citybuildersim.Auction.Bidders;
import ham.citybuildersim.Auction.Cap;
import ham.citybuildersim.Auction.Fill;
import ham.citybuildersim.Auction.Kind;
import ham.citybuildersim.Auction.Mode;
import ham.citybuildersim.Auction.Result;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The city's paper sold at auction: one clearing yield, and what it costs.
 *
 * WHAT THIS HAS TO PROVE (spec-city-debt.md, section 4; CD2). Auction is pure -
 * a snapshot of the bidders and the ask in, a result out - so sections 1 to 6
 * start no Game:
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
 *   7. THE LINEUP IN A CITY (CD2 slice 7). A ten-year sale opens a line at the
 *      stop rounded down to an eighth; the next month's ten-year sale reopens the
 *      SAME piece (its face the sum, its coupon unchanged, priced at the second
 *      auction's yield); a line with under nine tenths of its term left is not
 *      reopened (the edge, on a two-year line: 22 months left is a benchmark, 21
 *      is not); a 40-year ask is refused; a bill and an amortizing sale each
 *      open a piece of their own, every time.
 *
 *   8. THE QUOTE IS THE DEAL, AND THE SETTLE PAYS THE ALLOTMENT. A quote and its
 *      booking agree to the cent (face, proceeds, coupon); at the next settle the
 *      households pay exactly their allotment (their savings fall by its cash, their
 *      paper rises by its face) and the bank the rest, the month's money audit
 *      closes; and a short sale that sells under the minimum issue is no sale (the
 *      quote is empty, with its reason in the summary, and books nothing).
 *
 *   9. THE ROLLOVER SELLS AT ITS OWN CAP (D28) AND A SHORT SALE IS PAID IN FULL
 *      (D29). The cap is a setting of the rollover's, not automatic building's,
 *      and saved. A rollover capped and short sells what was bid; the maturity
 *      is paid in full all the same - the part the sale did not raise from the
 *      cash, and past the cash from the central bank's advances - and the log
 *      and the inbox name which. Filled, the same sale covers at the higher
 *      stop, and the inbox names the tail when it passes the default cap.
 *
 *  10. AUTOMATIC BUILDING'S OWN CAP, AND THE ASK A SHORT SALE KEEPS OPEN (D28,
 *      D30). A bond short at the builder's cap opens an ask and places no
 *      order; the money raised stays in the treasury for it and no other order
 *      spends it; next month, before the pass ranks anything new, the ask is
 *      offered again at the builder's cap, and the order is placed when what it
 *      raised and the spendable cash cover it - by a sale that covers, or by
 *      cash alone with nothing more borrowed. An ask whose measure drops out
 *      of the ranking, or whose next sale would cross the debt limit, is
 *      withdrawn, with what it raised left in the treasury. Save and load keep
 *      the ask and the cap. The time skip's report lists the sales.
 *
 *  11. THE REAL RUN (D33). LongPlaytest's fixture city on automatic building for
 *      1,200 months, every booked sale seen as it is counted: the mechanic fires,
 *      every tail is nothing or more, none sells more than was asked. It prints
 *      the count - sales, tails over a point, short sales, the largest tail - and
 *      asserts no rate: the count is what the cap's default is a judgement about.
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

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(java.io.OutputStream.nullOutputStream());
        // ChildcareCheck's town is the fixture for sections 9 and 10: its quiet stream too.
        ChildcareCheck.out = quiet;
        ChildcareCheck.quiet = quiet;
        Path root = Files.createTempDirectory("auctioncheck");

        section1();
        section2();
        section3();
        section4();
        section5();
        section6();
        section9(root);
        section10(root);

        Game city = lineupCity();
        section7(city);
        section8(city);
        section11();

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

    /* =====================================================================
       THE GAME'S OWN SALES (sections 7, 8 and 11): a city that sells its paper

       Sections 1-6 hand Auction a snapshot. These play a city: HoldersCheck's
       (sixty months, a bank, three hundred houses, the households with savings
       past the cushion), and then LongPlaytest's fixture for the long count.
       ===================================================================== */

    static final PrintStream QUIET = new PrintStream(java.io.OutputStream.nullOutputStream());

    /** Runs a piece of the city with the game's narration off. */
    static void quietly(Runnable r) {
        System.setOut(QUIET);
        try { r.run(); } finally { System.setOut(out); }
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    static double savings(Game g) { return g.getHouseholdBalance().totalSavings(); }

    /** A month, played with the narration off; the month's money audit comes back. */
    static MoneyAudit.Result play(Game g) {
        quietly(g::toggleNextMonth);
        return g.getLastMoneyAudit();
    }

    /** Money to the cent, as every proceeds figure is. */
    static double cents(double v) {
        return Math.round(v * 100) / 100.0;
    }

    /** HoldersCheck's city: given the money and the ground, a bank and three hundred houses, sixty months played. */
    static Game lineupCity() {
        Game city = new Game(GameFiles.scratch("auctioncheck"));
        quietly(() -> {
            city.run();
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 50_000_000L);
            city.getForeignAccounts().pinRate(1.0);
            city.buildStack(template(city, "Gravel Road"), 6, true);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Commercial Bank"), 1, false);
            city.simulateMonths(60);
        });
        return city;
    }

    /** How many pieces of this class the city's debt book holds. */
    static long pieces(DebtManager ledger, Class<? extends Debt> kind) {
        return ledger.getDebt().stream().filter(kind::isInstance).count();
    }

    /* ============== 7. the lineup in a city ============== */
    static void section7(Game city) {
        out.println("--- 7. the lineup in a city: a line opens, reopens while it is the benchmark, and is not reopened past that ---");
        DebtManager ledger = city.getDebtManager();
        double spare = city.getHouseholdBalance().spareForPaper();
        double ask = Math.max(100, spare * .05);
        out.printf("   m%d: households hold $%,.0fk past their cushion; each sale below asks $%,.0fk of face%n",
                city.getMonth(), spare, ask);
        assertTrue("fixture: households with savings past the cushion", spare > 0);
        assertTrue("fixture: no ten-year or two-year line stands yet", ledger.benchmarkLine(10) == null && ledger.benchmarkLine(2) == null);

        /* A NEW LINE: the stop, rounded down to an eighth. */
        DebtQuote q1 = city.quoteLongBond(ask, 10, 100);
        Result r1 = q1.auction();
        assertTrue("a ten-year sale with no line to reopen offers a new one, and sells it in full",
                r1 != null && r1.ask().isNewLine() && !q1.isEmpty() && !r1.shortSale() && r1.sold() == r1.ask().face());
        int piecesBefore = ledger.getDebt().size();
        quietly(() -> city.handleLongBondLogic(ask, 10, 100));
        Debt ten = ledger.benchmarkLine(10);
        assertTrue("...which opens a ten-year LongTermBond: one piece more on the books, 120 months long",
                ten instanceof LongTermBond && ten.getDuration() == 120 && ledger.getDebt().size() == piecesBefore + 1);
        double coupon1 = ((LongTermBond) ten).couponRate();
        double steps = coupon1 / Auction.COUPON_STEP;
        assertTrue("its coupon is the stop rounded DOWN to an eighth: a multiple of COUPON_STEP, not over the stop, less than a step under it",
                Math.abs(steps - Math.rint(steps)) < 1e-9 && coupon1 <= r1.stop() + 1e-12
                        && r1.stop() - coupon1 < Auction.COUPON_STEP + 1e-12);
        close("...and the quote promised exactly that coupon", coupon1, q1.couponRate(), 1e-12);
        out.printf("   the stop %.4f%% -> coupon %.3f%%, sold at %.4f per 100%n", r1.stop() * 100, coupon1 * 100, r1.pricePer100());
        assertTrue("a new line sells just under 100: its coupon is under its yield", r1.pricePer100() < 100.05 && r1.pricePer100() > 96);
        close("its face is what the auction sold", ten.getOustandingPrincipal(), r1.sold(), 1e-9);

        /* A BILL AND AN AMORTIZING SALE open pieces of their own, and open another each time. */
        long bills = pieces(ledger, ShortTermTBill.class), serials = pieces(ledger, MediumTermBond.class);
        DebtQuote billQuote = city.quoteTBill(ask, 6, 100);
        assertTrue("fixture: the bill sells in full", !billQuote.isEmpty() && !billQuote.isShort());
        quietly(() -> city.handleTBillLogic(ask, 6, 100));
        // Quoted after the bill is booked: its issue is on the books the amortizing sale is struck on.
        DebtQuote serialQuote = city.quoteMediumBond(ask, 10, 100);
        assertTrue("fixture: the amortizing bond sells in full", !serialQuote.isEmpty() && !serialQuote.isShort());
        quietly(() -> city.handleMediumBondLogic(ask, 10, 100));
        assertTrue("a six-month bill and a ten-year amortizing bond each open a piece of their own",
                pieces(ledger, ShortTermTBill.class) == bills + 1 && pieces(ledger, MediumTermBond.class) == serials + 1
                        && ledger.getDebt().size() == piecesBefore + 3);
        assertTrue("...neither is a line a bond sale reopens", ledger.benchmarkLine(10) == ten && ledger.benchmarkLine(6) == null);
        Debt serial1 = ledger.getDebt().get(ledger.getDebt().size() - 1);
        close("the amortizing bond's coupon is its auction's stop", ((MediumTermBond) serial1).couponRate(), serialQuote.auction().stop(), 1e-12);

        /* A TWO-YEAR LINE, opened now: it is the benchmark while 22 of its 24 months are left, and not when 21 are. */
        quietly(() -> city.handleLongBondLogic(ask, 2, 100));
        Debt two = ledger.benchmarkLine(2);
        assertTrue("a two-year line opens beside it", two instanceof LongTermBond && two.getDuration() == 24 && two != ten);
        double twoFace = two.getOustandingPrincipal();

        /* A MONTH ON: the same ten-year line is reopened, and a bill and a serial open another piece each. */
        play(city);
        int need10 = (int) Math.ceil(Auction.BENCHMARK_LIFE * 120 - 1e-9);
        assertTrue("a month on, the ten-year line still has " + ten.getRemainingMonths() + " of its 120 months: at least "
                + need10 + ", nine tenths, so it is still the benchmark",
                ten.getRemainingMonths() >= need10 && ledger.benchmarkLine(10) == ten);
        double ask2 = Math.max(100, spare * .03);
        double principal1 = ten.getOustandingPrincipal(), yield1 = ten.getIssueYield();
        double monthly1 = ten.getMonthlyInterestExpense();
        int left = ten.getRemainingMonths();
        int debtsBefore = ledger.getDebt().size();
        DebtQuote q2 = city.quoteLongBond(ask2, 10, 100);
        Result r2 = q2.auction();
        assertTrue("the second ten-year sale reopens the SAME piece: its coupon, priced for the months it has left",
                !r2.ask().isNewLine() && r2.ask().lineCoupon() == coupon1 && r2.ask().months() == left
                        && !q2.isEmpty() && !r2.shortSale());
        // The curve the sale was struck on: the issue priced in (Game.runAuction()).
        YieldCurve curve = ledger.quoteCurve(r2.ask().face());
        double priced = curve.pvAtSpread(Auction.bulletPer100(coupon1, left), r2.tail());
        close("...priced at the SECOND auction's yield: the line's schedule on the curve at that sale's tail", r2.pricePer100(), priced, 1e-9);
        close("...raising that price on the face, less the fee", q2.cashReceived(),
                cents(priced / 100 * r2.sold() - city.costOfIssuance(r2.sold())), 1e-9);
        double cash0 = city.getCash();
        quietly(() -> city.handleLongBondLogic(ask2, 10, 100));
        close("the treasury was paid what the second sale raised", city.getCash() - cash0, q2.cashReceived(), 1e-6);
        assertTrue("no piece was added: the face went into the line", ledger.getDebt().size() == debtsBefore && ledger.benchmarkLine(10) == ten);
        close("the line's face is the sum of the two sales", ten.getOustandingPrincipal(), principal1 + q2.faceValue(), 1e-9);
        close("its coupon is unchanged", ((LongTermBond) ten).couponRate(), coupon1, 0);
        close("its monthly interest rose by the quoted bill", ten.getMonthlyInterestExpense() - monthly1, q2.monthlyInterest(), 1e-9);
        close("its issue yield is the two stops blended by face", ten.getIssueYield(),
                (yield1 * principal1 + r2.stop() * q2.faceValue()) / (principal1 + q2.faceValue()), 1e-9);

        long bills2 = pieces(ledger, ShortTermTBill.class), serials2 = pieces(ledger, MediumTermBond.class);
        quietly(() -> city.handleTBillLogic(ask, 6, 100));
        quietly(() -> city.handleMediumBondLogic(ask, 10, 100));
        Debt serial2 = ledger.getDebt().get(ledger.getDebt().size() - 1);
        assertTrue("a second bill and a second amortizing bond are pieces of their own: nothing reopens them",
                pieces(ledger, ShortTermTBill.class) == bills2 + 1 && pieces(ledger, MediumTermBond.class) == serials2 + 1
                        && serial2 != serial1);
        close("...the first amortizing bond is as it was sold", serial1.getOustandingPrincipal(), serialQuote.faceValue(), 1e-9);

        /* THE EDGE OF NINE TENTHS, on the two-year line: 22 months left is a benchmark, 21 is not. */
        int need2 = (int) Math.ceil(Auction.BENCHMARK_LIFE * 24 - 1e-9);
        assertTrue("(nine tenths of 24 months is 21.6, so a two-year line needs 22 left)", need2 == 22);
        for (int guard = 0; guard < 6 && two.getRemainingMonths() > need2; guard++) play(city);
        assertTrue("with " + two.getRemainingMonths() + " of its 24 months left the two-year line is still the benchmark",
                two.getRemainingMonths() == need2 && ledger.benchmarkLine(2) == two);
        DebtQuote atEdge = city.quoteLongBond(ask, 2, 100);
        assertTrue("...and a two-year sale reopens it", !atEdge.auction().ask().isNewLine()
                && atEdge.auction().ask().lineCoupon() == ((LongTermBond) two).couponRate());
        play(city);
        assertTrue("one month more and it has " + two.getRemainingMonths() + ", under nine tenths: no longer the benchmark",
                two.getRemainingMonths() == need2 - 1 && ledger.benchmarkLine(2) == null);
        DebtQuote past = city.quoteLongBond(ask, 2, 100);
        assertTrue("...so a two-year sale opens a NEW line", past.auction().ask().isNewLine() && !past.isEmpty());
        int debtsAtEdge = ledger.getDebt().size();
        quietly(() -> city.handleLongBondLogic(ask, 2, 100));
        Debt newTwo = ledger.benchmarkLine(2);
        assertTrue("...a piece more, the old line not touched, and the new one is the benchmark now",
                ledger.getDebt().size() == debtsAtEdge + 1 && newTwo != null && newTwo != two
                        && newTwo.getRemainingMonths() >= need2 && two.getOustandingPrincipal() == twoFace);
        assertTrue("(the ten-year line, 117 months on, is still the benchmark)", ledger.benchmarkLine(10) == ten);

        /* A TERM THE TREASURY DOES NOT SELL. */
        int debts = ledger.getDebt().size();
        double cash = city.getCash();
        int tallied = ledger.auctionSales();
        String said = city.handleLongBondLogic(ask, 40, 100);
        assertTrue("a 40-year ask is refused, in LongTermBond's words", LongTermBond.REFUSAL.equals(said));
        assertTrue("...quoted nothing, and books nothing: no piece, no cash, no sale counted",
                city.quoteLongBond(ask, 40, 100).isEmpty() && ledger.getDebt().size() == debts
                        && city.getCash() == cash && ledger.auctionSales() == tallied);
    }

    /* ============== 8. the quote is the deal, and the settle pays the allotment ============== */
    static void section8(Game city) {
        out.println("--- 8. the quote is the deal, the settle pays the allotment, and a tiny sale is no sale ---");
        DebtManager ledger = city.getDebtManager();
        HouseholdBalance hb = city.getHouseholdBalance();
        play(city);                                    // section 7's last sales settle here, not in the month under test
        double spare = hb.spareForPaper();
        double askFace = Math.max(100, spare * .2);
        assertTrue("fixture: households with savings past the cushion", spare > 0);

        DebtQuote quote = city.quoteLongBond(askFace, 20, 100);
        Result r = quote.auction();
        assertTrue("fixture: the twenty-year sale sells in full", !quote.isEmpty() && !quote.isShort() && r.sold() == r.ask().face());
        double cash0 = city.getCash(), principal0 = ledger.getAllPrincipal();
        int salesWas = ledger.auctionSales();
        quietly(() -> city.handleLongBondLogic(askFace, 20, 100));
        Debt bond = ledger.benchmarkLine(20);
        assertTrue("fixture: it is booked as a twenty-year line", bond instanceof LongTermBond && bond.getDuration() == 240);
        close("the booking took in what the quote said, to the cent", city.getCash() - cash0, quote.cashReceived(), .005);
        close("...and that is the auction's proceeds to the cent", quote.cashReceived(), cents(r.proceeds()), 1e-9);
        close("the face it put on the books is the quote's", ledger.getAllPrincipal() - principal0, quote.faceValue(), 1e-9);
        close("...at the quote's coupon", ((LongTermBond) bond).couponRate(), quote.couponRate(), 1e-12);
        close("...and so its monthly bill", bond.getMonthlyInterestExpense(), quote.monthlyInterest(), 1e-9);
        assertTrue("it was counted as one sale, and the quote before it was not", ledger.auctionSales() == salesWas + 1);
        AuctionTally.Last last = ledger.lastAuction();
        assertTrue("...the last sale on the tally is this one",
                last != null && last.kind() == Auction.Kind.BOND && last.months() == 240 && last.sold() == r.sold()
                        && last.asked() == r.ask().face() && last.stop() == r.stop() && last.tail() == r.tail() && last.cover() == r.cover());

        /* THE SETTLE: the households pay what the auction allotted them, the bank the rest. */
        double received = bond.getSettleDue(), face = bond.getOustandingPrincipal();
        double allotFace = bond.allotHouseholdsFace(), allotCash = bond.allotHouseholdsCash();
        assertTrue("fixture: the households were allotted some, and can pay for it",
                allotFace > 0 && allotCash > 0 && allotCash < hb.spareForPaper());
        close("what the buyers owe at the settle is what the treasury was paid", received, quote.cashReceived(), 1e-9);
        final double[] around = new double[2];
        city.settleProbeForTest = after -> around[after ? 1 : 0] = savings(city);
        MoneyAudit.Result settled = play(city);
        city.settleProbeForTest = null;
        double paid = city.getHouseholdsBoughtPaper();
        out.printf("   allotted the households $%,.2fk of the $%,.0fk face; they paid $%,.2fk, the bank $%,.2fk%n",
                allotFace, face, paid, city.getCityPaperSettled());
        close("the households paid exactly their allotment", paid, allotCash, 1e-6);
        close("their savings fell by exactly that cash", around[0] - around[1], allotCash, 1e-6);
        close("their paper rose by exactly their allotted face", bond.householdPrincipal(), allotFace, 1e-6);
        close("the bank paid the rest of what the treasury was paid", city.getCityPaperSettled(), received - paid, 1e-6);
        close("...and holds the rest of the face", bond.bankPrincipal(), face - bond.householdPrincipal(), 1e-9);
        close("the households' paper in the cells is the paper's household principal",
                hb.totalPaper(), ledger.householdPrincipal(), 1e-9 * Math.max(1, ledger.householdPrincipal()));
        close("nothing more is owed for the sale, and the allotment is cleared", bond.getSettleDue() + bond.allotHouseholdsCash(), 0, 0);
        out.printf("   the month's audit: residual %.4f on %,.0fk moved%n", settled.residual, settled.moved());
        assertTrue("the month's money audit closed on the settle", Math.abs(settled.residual) <= MoneyAudit.tolerance(settled.moved()));

        /* A TINY SALE IS NO SALE. Drain the buyers with bills asked for far more than they can take: each sale takes the households'
           savings past the cushion and the bank's room, the next finds what the first left - at a discount, the households' allotment
           costs them less than the savings the bid was made against - and a sliver is all there is to sell. */
        Auction.Bidders bd = city.auctionBidders(6);
        double huge = 1000 * (bd.householdSpare() + bd.bankRoomFace() + bd.firmsIdle() + bd.worldStock() + bd.monthlyGdp() * 12 + 1000);
        double smallest = city.minimumIssueSize();
        DebtQuote tiny = null;
        boolean everyBookedSaleRaised = true, everyBookedSaleWorthIt = true;
        int rounds = 0;
        for (; rounds < 12 && tiny == null; rounds++) {
            DebtQuote q = city.quoteTBill(huge, 6, 100);
            if (q.isEmpty()) { tiny = q; break; }
            everyBookedSaleRaised &= q.cashReceived() > 0;
            everyBookedSaleWorthIt &= !q.isShort() || q.faceValue() >= smallest;
            quietly(() -> city.handleTBillLogic(huge, 6, 100));
        }
        out.printf("   %d huge bill sale(s) drained the buyers; the smallest deal worth arranging is $%,.1fk%n", rounds, smallest);
        assertTrue("fixture: the drain reached a sliver the auction could sell: bids, but under the minimum issue (or raising nothing)",
                tiny != null && tiny.auction() != null && tiny.auction().sold() > 0 && tiny.auction().shortSale()
                        && (tiny.auction().sold() < smallest || !(tiny.auction().proceeds() > 0)));
        assertTrue("every sale before it raised cash, and none sold short of the minimum", everyBookedSaleRaised && everyBookedSaleWorthIt);
        out.printf("   the sliver: sold $%,.4fk of $%,.0fk asked, would raise $%,.4fk%n",
                tiny.auction().sold(), tiny.auction().ask().face(), tiny.auction().proceeds());
        assertTrue("a short sale under the minimum issue is no sale: the quote is empty, nothing to book",
                tiny.isEmpty() && tiny.faceValue() == 0 && tiny.cashReceived() == 0);
        assertTrue("...still known to have come up short, and carrying the auction that would have been", tiny.isShort() && tiny.auction().ask().face() > 0);
        assertTrue("...with the reason in its summary", tiny.summary().contains("under the smallest deal worth arranging"));
        out.println("   " + tiny.summary().replace('\n', ' '));
        int debtsNow = ledger.getDebt().size(), salesNow = ledger.auctionSales();
        double cashNow = city.getCash();
        String said = city.handleTBillLogic(huge, 6, 100);
        assertTrue("booking it does nothing: 'Nothing issued', no cash, no piece, no sale counted",
                "Nothing issued.".equals(said) && city.getCash() == cashNow && ledger.getDebt().size() == debtsNow
                        && ledger.auctionSales() == salesNow);
        assertTrue("the build screen's and the builder's bond for the same gap reads none to be had",
                AutoBuilder.bondFor(city, huge) == null);
    }

    /* ============== 11. the real run (D33) ============== */
    static void section11() {
        out.println("--- 11. the real run: LongPlaytest's city on automatic building, 1,200 months, its auctions counted ---");
        final int months = 1200;
        long started = System.currentTimeMillis();
        GameFiles files = GameFiles.scratch("auctioncheck-run");
        PrintStream was = LongPlaytest.out;
        boolean flag = LongPlaytest.AUTOBUILD;
        LongPlaytest.out = QUIET;
        LongPlaytest.AUTOBUILD = true;
        Game g = new Game(files, LongPlaytest.founding());
        AuctionTally tally = g.getDebtManager().getAuctionTally();
        // Each booked sale, as it is counted: the sums are checked against the sales they are the sums of.
        int[] seen = new int[3];                 // sales, tails over a point, short
        double[] sums = new double[4];           // sold, asked, largest tail, the worst sold-over-asked
        boolean[] bad = new boolean[3];          // a tail under nothing, a sale of more than was asked, a sale of nothing
        tally.probeForTest = r -> {
            seen[0]++;
            if (r.tail() > Auction.MAX_TAIL + Auction.STOP_TOLERANCE) seen[1]++;
            if (r.shortSale()) seen[2]++;
            sums[0] += r.sold();
            sums[1] += r.ask().face();
            sums[2] = Math.max(sums[2], r.tail());
            if (r.tail() < -1e-12) bad[0] = true;
            if (r.sold() > r.ask().face() * (1 + 1e-12)) bad[1] = true;
            if (!(r.sold() > 0)) bad[2] = true;
        };
        try {
            quietly(() -> {
                g.run();
                g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
                g.setRolloverMode(LongPlaytest.ROLLOVER);
                g.setRescueMode(TreasuryFund.RescueMode.AUTOMATIC);
                g.getAutoBuilder().setOn(true, g.getDecisions());
                // The test player's founding, by hand, and its rhythm after (LongPlaytest.main()).
                LongPlaytest.villageBuild(g, "House", 40);
                LongPlaytest.villageBuild(g, "Convenience Store", 3);
                LongPlaytest.villageBuild(g, "Mixed Farm", 2);
                LongPlaytest.run(g, 3);
                LongPlaytest.villageBuild(g, "House", 20);
                LongPlaytest.run(g, 4);
                LongPlaytest.villageBuild(g, "Convenience Store", 2);
                LongPlaytest.villageBuild(g, "Construction Depot", 1);
                LongPlaytest.run(g, 5);
                LongPlaytest.advise(g);
                LongPlaytest.run(g, 6);
                int stop = 0;
                while (g.getMonth() < months) {
                    stop++;
                    int skip = switch (stop % 6) { case 0 -> 100; case 1 -> 12; case 2 -> 24; case 3 -> 60; case 4 -> 6; default -> 120; };
                    LongPlaytest.run(g, Math.min(skip, months - g.getMonth()));
                    if (g.getMonth() >= months) break;
                    for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                        if (LongPlaytest.advise(g) == null) break;
                        LongPlaytest.run(g, 1);
                    }
                    LongPlaytest.run(g, 2);
                }
            });
        } finally {
            tally.probeForTest = null;
            LongPlaytest.AUTOBUILD = flag;
            LongPlaytest.out = was;
        }
        double seconds = (System.currentTimeMillis() - started) / 1000.0;
        int sales = tally.auctionSales(), over = tally.auctionTailsOverPoint(), shorts = tally.auctionShortSales();
        out.printf("   %d months in %.0f s: %,d sale(s), $%,.0fk sold of $%,.0fk asked; %,d tail(s) over a point (%.1f%%),"
                + " %,d short; the largest tail %.1f bp%n", g.getMonth(), seconds, sales, tally.faceSold(), tally.faceAsked(),
                over, sales > 0 ? 100.0 * over / sales : 0, shorts, tally.largestTail() * 10_000);
        assertTrue("the city played the run", g.getMonth() >= months);
        assertTrue("the mechanic fires: the treasury booked sales at auction", sales > 0);
        assertTrue("every booked sale's tail was nothing or more", !bad[0]);
        assertTrue("...none sold more than was asked, and none sold nothing", !bad[1] && !bad[2]);
        assertTrue("the tally counted every sale the run booked, the tails over a point and the short ones among them",
                seen[0] == sales && seen[1] == over && seen[2] == shorts);
        close("...and the face they sold and asked", tally.faceSold(), sums[0], 1e-9);
        close("...and the face they asked", tally.faceAsked(), sums[1], 1e-9);
        close("...and the largest tail", tally.largestTail(), sums[2], 1e-12);
        assertTrue("in all, no more sold than asked", tally.faceSold() <= tally.faceAsked() * (1 + 1e-12));
        assertTrue("(the counts are consistent: tails over a point and short sales are sales)", over <= sales && shorts <= sales);
    }

    /* =====================================================================
       SECTIONS 9 AND 10: THE SALES NOBODY PRESSES (CD2, slice 6; D28 to D30)

       They start cities, unlike 1 to 6, and CAUSE the short sale in them. The
       lever is the bank's room, which auctionBidders() reads as its capital
       less its weighted book over Bank.RISK_CITY: roomTo() raises the
       weighted book until the room it reads is the face wanted - used up, or
       a share of the issue. The households bid a fifth of the issue a point
       over the deposit rate (D32), so the deposit rate is the other lever: at
       3% they have a slope between a point over fair and Fill it's ceiling
       (section 9's), and a hair under fair (4%) they bid two per cent of an
       issue at a cap of fair (section 10's). Each fixture asserts that it
       caused what it is for.
       ===================================================================== */

    /** The stream ChildcareCheck's town and the cities here are built behind. */
    static PrintStream quiet;

    /** What a piece of work printed: the log is where the rollover and the builder say what they did. */
    static String printed(Runnable r) {
        PrintStream real = System.out;
        java.io.ByteArrayOutputStream said = new java.io.ByteArrayOutputStream();
        System.setOut(new PrintStream(said));
        try { r.run(); } finally { System.setOut(real); }
        return said.toString();
    }

    /** Leaves the bank room for this much face of the city's paper and no more: its weighted book raised until the room the auction reads is that. */
    static void roomTo(Game g, double faceRoom) {
        Bank bank = g.getBank();
        double lo = 0, hi = 1e13;
        for (int i = 0; i < 200; i++) {
            double mid = (lo + hi) / 2;
            bank.setWeightedBook(0, mid, 0);
            if (g.auctionBidders().bankRoomFace() > faceRoom) lo = mid; else hi = mid;
        }
        bank.setWeightedBook(0, hi, 0);
    }

    /** A city saved to its own scratch folder's slot 10 and loaded into another Game, as a player's reload. */
    static Game reload(Game g) {
        quietly(() -> g.saveGame(10));
        return load(g);
    }

    /** ...the same save loaded again, into a city of its own. */
    static Game load(Game g) {
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(10));
        return back;
    }

    /** The sales automatic building and the rollover made in the month just run, by whom. */
    static int salesBy(Game g, String by) {
        int n = 0;
        for (TimeSkipReport.AutoSale s : g.getAutoSalesThisMonth()) if (s.by().equals(by)) n++;
        return n;
    }

    static int siteCount(Game g) {
        int n = 0;
        for (int v : g.getBuildingManager().getUnderConstructionById()) n += v;
        return n;
    }

    /* ============== 9. the rollover's own cap, and a short sale paid in full ============== */

    /** A town with the rollover on, a 3-month note of D$20M on its books that falls due next month. */
    static Game rolloverTown(Path root, String label) {
        Game g = ChildcareCheck.town(root, label);
        quietly(() -> {
            g.setRolloverMode(Rollover.Mode.SAME_STRUCTURE);
            g.handleTBillLogic(20_000, 3, Game.BUILD_NOTE_GRANULE);
        });
        for (int i = 0; i < 2; i++) quietly(g::toggleNextMonth);
        return g;
    }

    /** What the rollover is about to roll, and the room that makes it short at the default cap and covered at Fill it. */
    record Scene(Rollover.Issue issue, double askFace, double hhCapped, double hhFilled, double room, double cash) { }

    /**
     * The rollover's one issue as the city stands with `cash` in the treasury and the deposit rate at 3%
     * (so the households' appetite has a slope), with the bank's room ample, and the room that leaves the
     * default cap short and Fill it covered: the issue less the midway of the households' two bids.
     */
    static Scene scene(Game g, double cash) {
        g.getBank().setDepositRate(.03);
        g.setCashForTest(cash);
        Rollover.Issue issue = g.rolloverPlan().issues().get(0);
        DebtQuote capped = g.quoteTBill(issue.cash(), issue.term(), Game.BUILD_NOTE_GRANULE, Cap.DEFAULT);
        DebtQuote filled = g.quoteTBill(issue.cash(), issue.term(), Game.BUILD_NOTE_GRANULE, Cap.FILL);
        Bidders b = g.auctionBidders(issue.term());
        double ask = capped.auction().ask().face();
        double hhCapped = Auction.householdsAt(capped.auction().ceiling(), b, ask);
        double hhFilled = Auction.householdsAt(filled.auction().ceiling(), b, ask);
        return new Scene(issue, ask, hhCapped, hhFilled, ask - (hhCapped + hhFilled) / 2, cash);
    }

    static void section9(Path root) throws Exception {
        out.println("--- 9. the rollover sells at its own cap; a short sale is paid in full (D28, D29) ---");

        /* EACH ITS OWN (D28), AND SAVED. */
        Game cfg = new Game(GameFiles.scratch("auction9-cap"));
        quietly(cfg::newGame);
        assertTrue("a new city's rollover sells at fair plus a point, and so does automatic building: two settings, one default",
                CapSetting.same(cfg.getRolloverCap(), Cap.DEFAULT) && CapSetting.same(cfg.getAutoBuilder().getCap(), Cap.DEFAULT)
                        && cfg.getRolloverCap().mode() == Mode.TAIL && cfg.getRolloverCap().value() == Auction.MAX_TAIL);
        int decided = cfg.getDecisions().size();
        cfg.setRolloverCap(Cap.FILL);
        assertTrue("the rollover's cap moved to Fill it leaves automatic building's at the default, and the move is a decision",
                cfg.getRolloverCap().fill() && CapSetting.same(cfg.getAutoBuilder().getCap(), Cap.DEFAULT)
                        && cfg.getDecisions().size() == decided + 1 && cfg.getDecisions().last().label().startsWith("Rollover cap"));
        cfg.getAutoBuilder().setCap(Cap.at(.0525), cfg.getDecisions());
        assertTrue("...and automatic building's moved to a named yield leaves the rollover's at Fill it",
                cfg.getRolloverCap().fill() && CapSetting.same(cfg.getAutoBuilder().getCap(), Cap.at(.0525)));
        Game back = reload(cfg);
        assertTrue("both are saved: Fill it and a named yield come back as they were, each on its own setting",
                back.getLoadFailure() == null && back.getRolloverCap().fill()
                        && CapSetting.same(back.getAutoBuilder().getCap(), Cap.at(.0525)));
        cfg.setRolloverCap(Cap.tail(.025));
        Game back2 = reload(cfg);
        assertTrue("...and a tail of two and a half points comes back a tail, not a yield",
                back2.getRolloverCap().mode() == Mode.TAIL && Math.abs(back2.getRolloverCap().value() - .025) < 1e-12);
        assertTrue("the words a log line and a decision use for a cap",
                CapSetting.words(Cap.DEFAULT).equals("fair plus 1 point") && CapSetting.words(Cap.FILL).equals("Fill it")
                        && CapSetting.words(Cap.at(.045)).equals("no more than 4.50%") && CapSetting.words(Cap.tail(0)).equals("fair, no tail"));

        /* A SHORT SALE: the cash covers the rest. */
        Game s1 = rolloverTown(root, "auction9-short");
        Scene sc = scene(s1, 1_000_000);
        DebtQuote ample = s1.quoteTBill(sc.issue().cash(), sc.issue().term(), Game.BUILD_NOTE_GRANULE, Cap.DEFAULT);
        assertTrue("fixture: a 3-month note rolls into a 3-month bill, and with the bank's room ample the sale covers at the default cap",
                sc.issue().type().equals("Note") && sc.issue().term() == 3 && !ample.isShort());
        assertTrue("fixture: the households bid more at Fill it's ceiling than at a point over fair (a fifth of the issue a point over"
                        + " a deposit rate of 3%), so there is a room between the two",
                sc.hhFilled() > sc.hhCapped() + .01 * sc.askFace() && sc.room() > 0 && sc.room() < sc.askFace());
        roomTo(s1, sc.room());
        DebtQuote capped = s1.quoteTBill(sc.issue().cash(), sc.issue().term(), Game.BUILD_NOTE_GRANULE, Cap.DEFAULT);
        DebtQuote filled = s1.quoteTBill(sc.issue().cash(), sc.issue().term(), Game.BUILD_NOTE_GRANULE, Cap.FILL);
        assertTrue("fixture: with the bank's room left at that, a sale at the default cap sells short and a filled one covers",
                capped.isShort() && capped.auction().sold() < sc.askFace() && !filled.isShort());
        DebtQuote atFair = s1.quoteTBill(sc.issue().cash(), sc.issue().term(), Game.BUILD_NOTE_GRANULE, Cap.tail(0));
        assertTrue("a cap at fair allows no tail: the same room sells short at fair itself, and sells less than at fair plus a point",
                atFair.isShort() && atFair.auction().ceiling() == atFair.auction().ask().fair()
                        && atFair.auction().stop() == atFair.auction().ask().fair() && atFair.auction().sold() < capped.auction().sold());
        s1.getAutoBuilder().setCap(Cap.FILL, null);
        Debt maturing = null;
        for (Debt d : s1.getDebtManager().getDebt()) if (d.principalDueNextMonth() > 0) maturing = d;
        double due = s1.rolloverPlan().due();
        String log1 = printed(() -> s1.simulateMonths(1));
        Rollover roll1 = s1.getRollover();
        close("the rollover sold at its own cap, fair plus a point, though automatic building's is Fill it: it booked the face the"
                + " default cap's quote gave, short", roll1.getLastIssued(), capped.faceValue(), 1e-9);
        close("...and raised the cash that quote gave", roll1.getLastRaised(), capped.cashReceived(), 1e-9);
        assertTrue("the maturity is paid in full all the same: the note is off the books and the treasury repaid all that fell due",
                maturing != null && !s1.getDebtManager().getDebt().contains(maturing) && Math.abs(s1.getTreasuryRepaid() - due) < 1e-6);
        assertTrue("...from the cash: the treasury is not overdrawn, and the central bank advanced nothing",
                s1.getCash() > 0 && s1.getCentralBank().getAdvancesToTreasury() == 0);
        String note1 = s1.getRolloverShortNotes().size() == 1 ? s1.getRolloverShortNotes().get(0) : "";
        String capText = String.format("at its cap of %.2f%%", capped.auction().ceiling() * 100);
        assertTrue("the log names it: the sale, what it covered of what it needed, its cap, and that the cash paid the maturity's rest",
                note1.startsWith("The rollover's 3-month bill sale covered ") && note1.contains(capText)
                        && note1.contains("of the ") && note1.contains(CityCalendar.monthName(s1.getMonth()) + "'s maturity was paid from the treasury's cash")
                        && !note1.contains("advances") && log1.contains(note1));
        Notice inbox1 = s1.getInbox().live("rolloverShort");
        assertTrue("...and the inbox says it", inbox1 != null && String.join(" ", inbox1.getBody()).contains(note1));
        assertTrue("...and the time skip's report lists the sale, short at its cap, and worries about it",
                s1.getSkipReport().getAutoSaleLines().size() == 1
                        && s1.getSkipReport().getAutoSaleLines().get(0).contains("The rollover sold a 3-month bill")
                        && s1.getSkipReport().getAutoSaleLines().get(0).contains("short at its cap of")
                        && s1.getSkipReport().getHeadlines().stream().anyMatch(h -> h.startsWith("The rollover came up short at its cap 1 time")));

        /* A SHORT SALE: the cash does not cover the rest, the central bank's advances do. */
        Game s2 = rolloverTown(root, "auction9-advances");
        Scene sc2 = scene(s2, 300);
        roomTo(s2, sc2.room());
        DebtQuote capped2 = s2.quoteTBill(sc2.issue().cash(), sc2.issue().term(), Game.BUILD_NOTE_GRANULE, Cap.DEFAULT);
        assertTrue("fixture: a treasury of D$300k, the same room: the sale is short at the default cap, and the treasury cannot pay the rest",
                capped2.isShort() && s2.getCash() == 300 && sc2.issue().cash() - capped2.cashReceived() > 300);
        double shortfall = sc2.issue().cash() - capped2.cashReceived();
        double due2 = s2.rolloverPlan().due();
        String log2 = printed(() -> s2.simulateMonths(1));
        String note2 = s2.getRolloverShortNotes().size() == 1 ? s2.getRolloverShortNotes().get(0) : "";
        assertTrue("the maturity is paid in full here too: all that fell due is repaid",
                Math.abs(s2.getTreasuryRepaid() - due2) < 1e-6);
        assertTrue(String.format("the log names the advances' part - the sale's shortfall, %,.0fk, less the D$300k the cash held - and not the cash's",
                        shortfall - 300),
                note2.contains(String.format("%,.0fk of ", shortfall - 300)) && note2.contains("was paid by the central bank's advances")
                        && !note2.contains("paid from the treasury's cash") && log2.contains(note2));
        assertTrue("...and the inbox says it", s2.getInbox().live("rolloverShort") != null
                && String.join(" ", s2.getInbox().live("rolloverShort").getBody()).contains("central bank's advances"));
        assertTrue("the treasury ends the month overdrawn", s2.getCash() < 0);
        quietly(() -> s2.simulateMonths(1));
        double advances2 = s2.getCentralBank().getAdvancesToTreasury();
        Game k = rolloverTown(root, "auction9-control");
        scene(k, 300);
        DebtQuote coveredK = k.quoteTBill(sc2.issue().cash(), sc2.issue().term(), Game.BUILD_NOTE_GRANULE, Cap.DEFAULT);
        quietly(() -> k.simulateMonths(1));
        quietly(() -> k.simulateMonths(1));
        double advancesK = k.getCentralBank().getAdvancesToTreasury();
        assertTrue("fixture: the control city, the same treasury and an ample bank, sold in full and named no short sale",
                !coveredK.isShort() && k.getRolloverShortNotes().isEmpty() && k.getRolloverTailNotes().isEmpty()
                        && k.getRollover().getLastRaised() >= sc2.issue().cash() - 1e-6);
        double missing = k.getRollover().getLastRaised() - s2.getRollover().getLastRaised();
        close("the next settle advanced the treasury more than the control's by what the short sale did not raise",
                advances2 - advancesK, missing, .03);
        assertTrue("...so the maturity was paid by the central bank's advances: it owes them more than the control's", advances2 > advancesK);

        /* FILLED: the same room covers at the higher stop, and the inbox names the tail. */
        Game f = rolloverTown(root, "auction9-filled");
        Scene scf = scene(f, 1_000_000);
        roomTo(f, scf.room());
        f.setRolloverCap(Cap.FILL);
        DebtQuote fq = f.quoteTBill(scf.issue().cash(), scf.issue().term(), Game.BUILD_NOTE_GRANULE, Cap.FILL);
        DebtQuote cq = f.quoteTBill(scf.issue().cash(), scf.issue().term(), Game.BUILD_NOTE_GRANULE, Cap.DEFAULT);
        assertTrue("fixture: the same room, the rollover filled: the sale covers, at a stop above the capped sale's, with a tail past a point",
                !fq.isShort() && cq.isShort() && fq.auction().stop() > cq.auction().stop()
                        && fq.auction().tail() > Auction.MAX_TAIL);
        String log3 = printed(() -> f.simulateMonths(1));
        assertTrue("filled, the rollover covers: no short sale is named, and what it raised pays the maturity's whole share",
                f.getRolloverShortNotes().isEmpty() && f.getRollover().getLastRaised() >= scf.issue().cash() - 1e-6);
        close("...at the stop the filled quote gave, higher than a capped sale would have stopped at", f.getRollover().getLastRaised(),
                fq.cashReceived(), 1e-9);
        String tail = f.getRolloverTailNotes().size() == 1 ? f.getRolloverTailNotes().get(0) : "";
        assertTrue("the log names the tail it paid: the stop, the basis points over fair, and the cap that allowed it",
                tail.startsWith("The rollover's 3-month bill sale cleared at ")
                        && tail.contains(String.format("a tail of %.0f basis points over fair", fq.auction().tail() * 10_000))
                        && tail.contains("with its cap set to Fill it") && log3.contains(tail));
        Notice tailNotice = f.getInbox().live("rolloverTail");
        assertTrue("...and the inbox says it", tailNotice != null && String.join(" ", tailNotice.getBody()).contains(tail));
        assertTrue("the sale is entered for the skip's report with its tail, not short",
                f.getAutoSalesThisMonth().size() == 1 && !f.getAutoSalesThisMonth().get(0).shortSale()
                        && f.getAutoSalesThisMonth().get(0).tail() > Auction.MAX_TAIL);
        assertTrue("a rollover that sold with no tail past a point says nothing of one", k.getInbox().live("rolloverTail") == null
                && k.getInbox().live("rolloverShort") == null);
    }

    /* ============== 10. automatic building: its own cap, and the ask a short sale keeps open ============== */

    /** The measure Streets, whose card is a Paved Road: the first the pass walks to in the fixture's town. */
    static BuildAdvice.Measure streets() {
        return BuildAdvice.Measure.of(BuildAdvice.Kind.ROADS);
    }

    /** The deposit rate of section 10's fixture: a hair under fair (4.1%), so at a cap of fair the households bid about two per cent of an issue. */
    static final double HAIR_UNDER_FAIR = .04;

    /**
     * The room used up and the deposit rate a hair under fair, so that at a cap of fair the households bid two per cent of an
     * issue and the bank bids D$100k of face: a bond for an order sells almost nothing, and short.
     */
    static void useUpTheRoom(Game g) {
        g.getBank().setDepositRate(HAIR_UNDER_FAIR);
        roomTo(g, 100);
    }

    /**
     * One month of the fixture's, run and printed: the room used up as above (unless `ample`), and the treasury's cash set to the
     * reserve plus what the asks hold - the month's other bills are met from elsewhere, so what an ask raised stands at the
     * next pass (they may draw on it in a real city: AutoBuilder.held() reads what is left).
     */
    static String month(Game g, boolean ample) {
        g.getBank().setDepositRate(HAIR_UNDER_FAIR);
        if (ample) roomTo(g, 1e9); else roomTo(g, 100);
        g.setCashForTest(AutoBuilder.reserve(g) + g.getAutoBuilder().askedRaised());
        return printed(() -> g.simulateMonths(1));
    }

    static void section10(Path root) throws Exception {
        out.println("--- 10. automatic building's own cap; the ask a short sale keeps open (D28, D30) ---");

        Game a = ChildcareCheck.town(root, "auction10-town");
        AutoBuilder ab = a.getAutoBuilder();
        ab.setOn(true, null);
        ab.setCap(Cap.tail(0), null);
        assertTrue("automatic building's cap is its own: set to fair, the rollover's stays at fair plus a point",
                CapSetting.same(ab.getCap(), Cap.tail(0)) && CapSetting.same(a.getRolloverCap(), Cap.DEFAULT));

        /* THE FIXTURE: the cash at the reserve, so every order needs its bond; the room used up. */
        BuildAdvice.Measure streets = streets();
        useUpTheRoom(a);
        a.setCashForTest(AutoBuilder.reserve(a));
        List<CityNeeds.Need> all = CityNeeds.measure(a, CityNeeds.PLAIN);
        BuildAdvice.Suggestion card = BuildAdvice.suggestFor(a, AutoBuilder.rowFor(all, streets), streets, a.getCash(),
                a.getLandManager().getAvailableSqFt(), ab.getSlack());
        double one = card == null ? Double.NaN : card.paving() ? a.quotePave(1).total : a.quoteBuild(card.template(), 1).total;
        double gap = AutoBuilder.gapFor(a, one);
        DebtQuote probe = card == null ? null : AutoBuilder.bondQuote(a, gap, ab.getCap());
        assertTrue("fixture: the town is short of streets and its cash at the reserve; the bank's room used up and the deposit rate"
                        + " a hair under fair, the bond for one Paved Road sells short at a cap of fair",
                card != null && probe != null && probe.isShort() && probe.cashReceived() < gap && gap > 0
                        && card.template().getName().equals("Paved Road") && probe.auction().ask().fair() > HAIR_UNDER_FAIR + 1e-4);

        /* PASS 1: the ask opens. */
        double[] snap = new double[6];
        int[] monthAt = new int[1];
        a.autoBuildProbeForTest = after -> {
            int i = after ? 1 : 0;
            snap[i] = a.getCash();
            snap[2 + i] = a.getDebtManager().getAllPrincipal();
            if (!after) monthAt[0] = a.getMonth(); else { snap[4] = AutoBuilder.spendable(a); snap[5] = a.getCash() - AutoBuilder.reserve(a); }
        };
        int sites0 = siteCount(a);
        String log1 = month(a, false);
        a.autoBuildProbeForTest = null;
        List<AutoBuilder.OpenAsk> asks = ab.asks();
        Set<String> labels = new HashSet<>();
        for (AutoBuilder.OpenAsk k : asks) labels.add(k.measure());
        AutoBuilder.OpenAsk ask = ab.askFor(streets);
        assertTrue("a short sale opens an ask for the order the money refused: one for the streets' Paved Road, its count one, and at most"
                        + " one a measure (" + asks.size() + " open)",
                ask != null && ask.building().equals("Paved Road") && ask.count() == 1 && labels.size() == asks.size() && asks.size() >= 1);
        double raised = 0;
        for (AutoBuilder.OpenAsk k : asks) raised += k.raised();
        assertTrue("no order is placed on money that is not there: no order, no site, nothing in its log",
                ab.getOrders() == 0 && ab.log().isEmpty() && siteCount(a) == sites0);
        close("the paper sold is in the treasury: the cash rose by what the asks raised", snap[1] - snap[0], raised, 1e-9);
        double soldFace = 0;
        for (TimeSkipReport.AutoSale s : a.getAutoSalesThisMonth()) if (s.by().equals("Automatic building")) soldFace += s.sold();
        close("...and the city owes the face it sold: the principal rose by it", snap[3] - snap[2], soldFace, 1e-9);
        assertTrue("it raised less than the order costs, with the rest still to raise (the cost less what is held for it) and the month it opened",
                ask != null && ask.raised() > 0 && ask.raised() < ask.cost() && ask.toRaise() > 0
                        && Math.abs(ask.toRaise() - (ask.cost() - ask.raised())) < 1e-6 && ask.monthOpened() == monthAt[0]);
        close("what it raised counts against the order: the cash over the reserve is all asks' money", snap[5], raised, 1e-6);
        near("...so the builder has nothing to spend on any other order: AutoBuilder.spendable() is nothing", snap[4], 0, 1e-6);
        assertTrue("every sale was short at the builder's cap of fair, and the skip's report has it",
                salesBy(a, "Automatic building") == asks.size()
                        && a.getAutoSalesThisMonth().stream().allMatch(s -> s.shortSale() && s.tail() == 0 && s.heldOpen() > 0));
        Notice askNotice = a.getInbox().live("autobuild-ask");
        Notice heldNotice = a.getInbox().live("autobuild");
        assertTrue("the inbox says an ask was opened, and that the order waits for the money",
                askNotice != null && String.join(" ", askNotice.getBody()).contains("opened an ask")
                        && String.join(" ", askNotice.getBody()).contains("Paved Road")
                        && heldNotice != null && String.join(" ", heldNotice.getBody()).contains("The order waits for the money"));
        assertTrue("...and so does the log", log1.contains("Automatic building: Streets: the 20-year bond for 1 Paved Road sold short"));

        /* Save it: the cities below are loads of this one, which also proves the ask and the cap survive. */
        Game saved = reload(a);
        AutoBuilder sb = saved.getAutoBuilder();
        boolean same = saved.getLoadFailure() == null && sb.asks().size() == asks.size() && CapSetting.same(sb.getCap(), Cap.tail(0))
                && CapSetting.same(saved.getRolloverCap(), Cap.DEFAULT);
        for (int i = 0; same && i < asks.size(); i++) {
            AutoBuilder.OpenAsk x = asks.get(i), y = sb.asks().get(i);
            same = x.measure().equals(y.measure()) && x.building().equals(y.building()) && x.count() == y.count()
                    && x.paving() == y.paving() && x.monthOpened() == y.monthOpened()
                    && Math.abs(x.cost() - y.cost()) < 1e-9 && Math.abs(x.raised() - y.raised()) < 1e-9
                    && Math.abs(x.toRaise() - y.toRaise()) < 1e-9;
        }
        assertTrue("save and load keep the asks, each as it was (the order, what is raised, what is to raise, the month), and automatic"
                + " building's cap - and not the rollover's, which is its own", same);

        /* PASS 2: offered again, still short. */
        int bonds1 = ab.getBonds();
        double askRaised1 = ask.raised();
        String log2 = month(a, false);
        AutoBuilder.OpenAsk ask2 = ab.askFor(streets);
        double raised2 = 0;
        for (AutoBuilder.OpenAsk k : ab.asks()) raised2 += k.raised();
        assertTrue("next month, before the pass ranks anything new, every open ask is offered again at the builder's cap: a sale each in"
                        + " the month's report, short again, and still one ask a measure",
                ask2 == ask && ab.asks().size() == asks.size() && salesBy(a, "Automatic building") == asks.size()
                        && a.getAutoSalesThisMonth().stream().allMatch(s -> s.shortSale()));
        assertTrue("...what the asks raised grew (the bank's D$100k of room, sold again), and no ask raised less than it had",
                raised2 > raised + 1e-9 && ask2 != null && ask2.raised() >= askRaised1 - 1e-9 && ab.getBonds() > bonds1);
        assertTrue("...its order is still not placed, and it opened in the month it opened in",
                ab.getOrders() == 0 && ab.log().isEmpty() && ask2 != null && ask2.monthOpened() == monthAt[0]);
        assertTrue("(the pass held the measure on its ask rather than ordering again: one hold, no second ask)",
                ab.steps().stream().filter(s -> s.measure().equals(streets)).allMatch(s -> s.outcome() == AutoBuilder.Outcome.HELD));

        /* PASS 3: the market has room again: the offer covers, and the order is placed. */
        double cost3 = ask2 == null ? 0 : ask2.cost();
        String log3 = month(a, true);
        AutoBuilder.OpenAsk gone = ab.askFor(streets);
        AutoBuilder.Entry placedEntry = null;
        for (AutoBuilder.Entry e : ab.log()) if (e.measure().equals(streets.label())) placedEntry = e;
        assertTrue("the room back, the ask is covered by the sale that month and its order is placed on the same path as any other:"
                        + " one Paved Road, a site, the ask closed",
                gone == null && placedEntry != null && placedEntry.building().equals("Paved Road") && placedEntry.count() == 1
                        && siteCount(a) > sites0 && ab.getOrders() >= 1);
        assertTrue("...paid for by the bond sales it raised: the log shows all of them as its borrowing, the cost of the order covered",
                placedEntry != null && placedEntry.borrowed() >= placedEntry.cost() - 1e-6
                        && Math.abs(placedEntry.cost() - cost3) <= .02 * cost3);
        assertTrue("...and says it waited, and the inbox says the order was placed",
                placedEntry != null && placedEntry.why().contains("waited 2 months for the money")
                        && a.getInbox().live("autobuild-ask") != null
                        && String.join(" ", a.getInbox().live("autobuild-ask").getBody()).contains("placed its order for 1 Paved Road (Streets) after 2 months"));

        /* THE CASH COVERS IT: what is raised and the spendable cash, nothing more borrowed. */
        Game b = load(saved);
        AutoBuilder bb = b.getAutoBuilder();
        double costs = 0;
        for (AutoBuilder.OpenAsk k : bb.asks()) costs += k.cost();
        AutoBuilder.OpenAsk streetsB = bb.askFor(streets);
        double raisedB = streetsB == null ? Double.NaN : streetsB.raised();
        int openB = bb.asks().size(), bondsB = bb.getBonds(), sitesB = siteCount(b);
        double principalB = b.getDebtManager().getAllPrincipal();
        useUpTheRoom(b);
        b.setCashForTest(AutoBuilder.reserve(b) + costs + 1000);
        String logB = printed(() -> b.simulateMonths(1));
        AutoBuilder.Entry placedB = null;
        for (AutoBuilder.Entry e : bb.log()) if (e.measure().equals(streets.label())) placedB = e;
        assertTrue("the treasury can pay for what is left of the orders: they are placed with no sale at all - no bond, no new paper,"
                        + " nothing in the month's report - and every ask is closed",
                bb.asks().isEmpty() && openB >= 1 && bb.getBonds() == bondsB && salesBy(b, "Automatic building") == 0
                        && Math.abs(b.getDebtManager().getAllPrincipal() - principalB) < 1e-6 && siteCount(b) >= sitesB + openB);
        assertTrue("...the streets' log entry shows the bond money its ask had raised, borrowed on an earlier month, and no more",
                placedB != null && Math.abs(placedB.borrowed() - raisedB) < 1e-6 && placedB.borrowed() < placedB.cost());

        /* THE MONTH'S BILLS DRAW THE MONEY: the paper is not sold twice, and the order waits for the cash. */
        Game p = load(saved);
        AutoBuilder pb = p.getAutoBuilder();
        int openP = pb.asks().size(), sitesP = siteCount(p);
        p.getBank().setDepositRate(HAIR_UNDER_FAIR);
        roomTo(p, 1e9);
        p.setCashForTest(AutoBuilder.reserve(p));
        printed(() -> p.simulateMonths(1));
        double costsP = 0;
        boolean raisedAllP = !pb.asks().isEmpty();
        for (AutoBuilder.OpenAsk k : pb.asks()) {
            costsP += k.cost();
            raisedAllP &= k.raised() >= k.cost() - 1e-6 * k.cost();
        }
        assertTrue("treasury at the reserve, the room back: each ask is offered what it still has to raise and sells it in full (the"
                        + " cost less what it had raised), not short",
                pb.asks().size() == openP && salesBy(p, "Automatic building") == openP
                        && p.getAutoSalesThisMonth().stream().noneMatch(TimeSkipReport.AutoSale::shortSale) && raisedAllP);
        assertTrue("...but the money it raised before was drawn by the month's bills, so the treasury does not hold the cost for any: no"
                        + " order is placed on money that is not there",
                pb.getOrders() == 0 && pb.log().isEmpty() && pb.asks().size() == openP);
        double principalP = p.getDebtManager().getAllPrincipal();
        p.setCashForTest(AutoBuilder.reserve(p));
        printed(() -> p.simulateMonths(1));
        Notice waits = p.getInbox().live("autobuild");
        assertTrue("next month, drawn again, the asks have raised their whole cost and the paper is not sold twice: the only sales are a"
                        + " granule's top-up for a price that drifted up (D$" + (int) Game.BUILD_BOND_GRANULE + "k of face at most each), the orders"
                        + " still waiting",
                pb.asks().size() == openP && pb.getOrders() == 0
                        && p.getAutoSalesThisMonth().stream().allMatch(s -> s.asked() <= Game.BUILD_BOND_GRANULE + 1e-9 && !s.shortSale())
                        && p.getDebtManager().getAllPrincipal() <= principalP + openP * Game.BUILD_BOND_GRANULE + 1e-6);
        assertTrue("...and the inbox says the month's bills drew the money and that nothing more is borrowed for it",
                waits != null && String.join(" ", waits.getBody()).contains("have drawn the rest")
                        && String.join(" ", waits.getBody()).contains("nothing more is borrowed for it"));
        int bondsP = pb.getBonds();
        double principalP2 = p.getDebtManager().getAllPrincipal();
        p.setCashForTest(AutoBuilder.reserve(p) + costsP + 1000);
        printed(() -> p.simulateMonths(1));
        assertTrue("the cash back in the treasury, the orders are placed, and nothing more is borrowed: no sale, no new bond, no new debt",
                pb.asks().isEmpty() && pb.getOrders() >= openP && siteCount(p) >= sitesP + openP && pb.getBonds() == bondsP
                        && salesBy(p, "Automatic building") == 0 && Math.abs(p.getDebtManager().getAllPrincipal() - principalP2) < 1e-6);

        /* WITHDRAWN: the advice no longer ranks the order. */
        Game c = load(saved);
        AutoBuilder cb = c.getAutoBuilder();
        BuildAdvice.Measure care = null;
        for (BuildAdvice.Measure k : AutoBuilder.remit(c, CityNeeds.measure(c, CityNeeds.PLAIN))) if (k.label().equals("General care")) care = k;
        final BuildAdvice.Measure general = care;
        AutoBuilder.OpenAsk careAsk = general == null ? null : cb.askFor(general);
        double raisedC = careAsk == null ? 0 : careAsk.raised();
        BuildingsTemplate centre = c.getBuildingManager().getTemplateByName("Community Health Centre");
        c.setCashForTest(Founding.WEALTHY_CASH);
        quietly(() -> c.buildStack(centre, 20, true));
        double[] snapC = new double[4];
        c.autoBuildProbeForTest = after -> { snapC[after ? 1 : 0] = c.getCash(); snapC[2 + (after ? 1 : 0)] = c.getDebtManager().getAllPrincipal(); };
        String logC = month(c, false);
        c.autoBuildProbeForTest = null;
        String withdrew = String.join(" ", cb.asked());
        assertTrue("fixture: the town had an ask open for its general care, with money raised for it, and then twenty Health Centres stand:"
                        + " the measure is ahead of its margin without another",
                careAsk != null && raisedC > 0 && general != null
                        && BuildAdvice.ahead(c, general, BuildAdvice.onSite(c, general), BuildAdvice.opening(c, 0, cb.getSlack())));
        assertTrue("an ask whose measure is no longer short is withdrawn: the log and the inbox say so, and that what it raised stays in the"
                        + " treasury - the paper is sold and cannot be unsold",
                general != null && cb.askFor(general) == null
                        && withdrew.contains("withdrew its ask for Community Health Centre (General care)")
                        && withdrew.contains("stays in the treasury") && withdrew.contains("cannot be unsold")
                        && logC.contains("withdrew its ask for Community Health Centre (General care)")
                        && c.getInbox().live("autobuild-ask") != null
                        && String.join(" ", c.getInbox().live("autobuild-ask").getBody()).contains("cannot be unsold"));
        assertTrue("...the others stay open, and nothing was given back: the cash did not fall in the pass, and the city still owes every bond it sold",
                cb.askFor(streets) != null && snapC[1] >= snapC[0] - 1e-6 && snapC[3] >= snapC[2] - 1e-6);

        /* WITHDRAWN: the debt limit closes. */
        Game d = load(saved);
        AutoBuilder db = d.getAutoBuilder();
        int openD = db.asks().size();
        db.setDebtLimit(.1, null);
        double[] snapD = new double[2];
        d.autoBuildProbeForTest = after -> snapD[after ? 1 : 0] = d.getCash();
        String logD = month(d, false);
        d.autoBuildProbeForTest = null;
        assertTrue("fixture: the debt limit is under what the city owes", !db.within(d));
        assertTrue("an ask whose next sale would cross the debt limit is withdrawn, every one, and the log says the limit closed on it",
                openD >= 1 && db.asks().isEmpty() && logD.contains("the debt limit closed on it") && snapD[1] >= snapD[0] - 1e-6);

        /* THE PLAYER WITHDRAWS ONE. */
        Game w = load(saved);
        AutoBuilder wb = w.getAutoBuilder();
        int openW = wb.asks().size();
        AutoBuilder.OpenAsk victim = wb.asks().get(0);
        double cashW = w.getCash();
        int decidedW = w.getDecisions().size();
        assertTrue("the panel's withdraw(index) takes one ask off, leaves the others, leaves the cash alone, and is a decision",
                !wb.withdraw(99, w.getDecisions()) && wb.withdraw(0, w.getDecisions()) && wb.asks().size() == openW - 1
                        && !wb.asks().contains(victim) && w.getCash() == cashW && w.getDecisions().size() == decidedW + 1);

        /* THE TIME SKIP'S REPORT. */
        Game t = load(saved);
        useUpTheRoom(t);
        t.setCashForTest(AutoBuilder.reserve(t) + t.getAutoBuilder().askedRaised());
        quietly(() -> t.simulateMonths(2));
        TimeSkipReport skip = t.getSkipReport();
        long autoSales = skip.getAutoSales().stream().filter(s -> s.by().equals("Automatic building")).count();
        assertTrue("a skip lists every sale automatic building made: at least each ask offered again, one a line with its stop, its tail and"
                        + " what was left open",
                autoSales >= saved.getAutoBuilder().asks().size() && skip.getAutoSaleLines().size() == skip.getAutoSales().size()
                        && skip.getAutoSaleLines().stream().anyMatch(l -> l.contains("Automatic building sold a 20-year bond at ")
                        && l.contains("basis points") && l.contains("short at its cap of") && l.contains("of the ask left open")));
        assertTrue("...and its headlines say automatic building came up short at its cap, and how many times it sold",
                skip.getHeadlines().stream().anyMatch(h -> h.startsWith("Automatic building came up short at its cap"))
                        && skip.getHeadlines().stream().anyMatch(h -> h.startsWith("Automatic building sold ")));
    }
}
