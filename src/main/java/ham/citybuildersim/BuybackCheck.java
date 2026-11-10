package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The city's paper bought back in a reverse auction (CD4, spec-city-debt.md
 * section 6.2-6.3; D12, D17-D19, D26, D34): every holder but the central bank
 * offers its holding in slices, each at the price where its own buying rule
 * would stop wanting it and never under fair, and the city takes the cheapest
 * offers first, paying each its own price.
 *
 * What this has to prove:
 *
 * 1. The pure auction (Buyback): every offer is at or over fair; the bank's
 *    are exactly fair (D34); each holder's rule, solved by the harness from
 *    the model's own constants, draws the prices of its slices, dearer as it
 *    sells; the central bank's face is never offered (D18); a highest price
 *    takes only the cheaper offers; the price paid is each slice's own, so
 *    paid is the sum of face x price, and stop >= average >= fair.
 * 2. A deposit rate over the line's yield (a hiking city) makes the
 *    households' and the businesses' rule price under fair, so they offer at
 *    fair: caused, and asserted. A tie in price goes to the bank first.
 * 3. A played city: a ten-year line sold through the auction and held, after
 *    the settle, by the households, the bank and the world. Bought back in
 *    HALF, the piece stays on the books with its principal, face, carrying
 *    value and schedule halved and its effective rate unchanged; each holder's
 *    principal fell by exactly what it sold and was paid exactly its own
 *    price; the treasury's cash fell by what was paid; the gain on debt
 *    retired moves by the carrying value of the face taken less the price; the
 *    money audit closes that month and the next. Then the rest: the piece
 *    leaves the books.
 * 4. A central bank holding part of the line (its dial): asking for the whole
 *    principal takes only the rest, the central bank is not paid and its
 *    principal is unchanged.
 * 5. No money pump (RestructureCheck's premise): sell a line, let it settle,
 *    buy it all back: the city ends the round trip with less than it raised,
 *    or the same, never more.
 * 6. Dollar paper (D26) is the world's alone: every slice at fair, and a part
 *    of it leaves the piece on the books in dollars.
 * 7. Refused: a buyback the city cannot pay for, one under every price, and
 *    one of a piece with a sale unsettled change nothing.
 * 8. The year adds up (CD3's star 5): a line bought back at a gain, then a
 *    year played - History's revenue less surplus plus the gain on debt
 *    retired is the budget's expenses, month by month, and the treasury's
 *    month of spending is their average; without the gain it is off by it.
 * 9. A line callable now (BB1, CD4-C ★6): the city could call it at
 *    CallTree.CALL_PRICE, so no holder asks more than that. By hand, a line
 *    whose coupon is over what the households ask offers over the call price
 *    without the cap and at most the call price with it, the offers already
 *    under it unchanged, the ladder still ascending, a full buyback's stop and
 *    cost within it; a line whose fair is itself over it offers at exactly the
 *    call price (the cap wins over the floor at fair). In a played city, a
 *    callable line before its first call month is quoted as the holders ask,
 *    over the call price; at it, the same quote is the same ladder with each
 *    offer at the least of what it was and the call price, and the buyback
 *    booked is that quote, within what calling the line costs.
 * 10. What the auction books and what fair would (BB1, CD6-C star 1): the
 *    book's desk shows a line's gain at fair, the auction books it at the
 *    holders' asks, and the two differ by exactly what the holders ask over
 *    fair: gainAtFair less gainOnCarrying is overFair, the holders' overFair
 *    add up to it, the bank's is nothing (D34), none is under nothing. The
 *    case CD6 saw, caused by hand: a line the households hold, carried
 *    between its worth at fair and what the auction pays, reads a gain at
 *    fair and books a loss. In a played city, a line sold and settled quotes
 *    the same sums.
 *
 * Each fixture causes its condition rather than finding a city in it.
 *
 * @author Jerus
 */
public class BuybackCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void near(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-96s FAIL  %,.9f != %,.9f%n", label, actual, expected);
        } else {
            out.printf("%-96s OK%n", label);
        }
    }

    static void quietly(Runnable r) {
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(out); }
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    /** A month, held to the audit: the audit is returned. */
    static MoneyAudit.Result play(Game g) {
        quietly(g::toggleNextMonth);
        return g.getLastMoneyAudit();
    }

    static boolean closes(MoneyAudit.Result r) {
        return Math.abs(r.residual) <= MoneyAudit.tolerance(r.moved());
    }

    /* =====================================================================
       THE PURE FIXTURE: a ten-year bullet at 4%, D$1,000, the market at 5%
       ===================================================================== */

    static final double PRINCIPAL = 1_000;
    static final double COUPON = .04;
    static final double MARKET_YIELD = .05;

    /** The line's remaining payments, local money. */
    static double[] flows() {
        double[] per100 = Auction.bulletPer100(COUPON, 120);
        double[] f = new double[per100.length];
        for (int i = 0; i < f.length; i++) f[i] = per100[i] * PRINCIPAL / 100;
        return f;
    }

    /** What the line's payments are worth per 100 at one yield: the harness's own sum, not Buyback's. */
    static double per100(double yield) {
        return Debt.presentValue(flows(), yield) / PRINCIPAL * 100;
    }

    static double fair() {
        return per100(MARKET_YIELD);
    }

    /** The bank 400, the households 200, the businesses 100, the world 150, the central bank 150. */
    static Buyback.Line line() {
        return new Buyback.Line(PRINCIPAL, flows(), fair(), 980, 400, 200, 100, 150, 150, false);
    }

    /** A calm city: deposits at 2%, the world at 3% and half a point of premium, the businesses with idle money to spare. */
    static Auction.Bidders calm() {
        return bidders(.02, 1_000, 150, 1_000);
    }

    static Auction.Bidders bidders(double deposit, double firmsIdle, double worldStock, double monthlyGdp) {
        return new Auction.Bidders(deposit, .03, .005, 0, firmsIdle, 0, worldStock, false, monthlyGdp, 0, 0);
    }

    /** One holder's offers, in the order they come on the ladder. */
    static List<Buyback.Offer> of(List<Buyback.Offer> ladder, Buyback.Holder holder) {
        List<Buyback.Offer> mine = new ArrayList<>();
        for (Buyback.Offer o : ladder) if (o.holder() == holder) mine.add(o);
        return mine;
    }

    static double faceOf(List<Buyback.Offer> offers) {
        double sum = 0;
        for (Buyback.Offer o : offers) sum += o.face();
        return sum;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        purely();
        hiking();
        Game city = playedCity("buybackcheck");
        halfOfALine(city);
        centralBankHolds();
        noPump(city);
        dollars();
        refused(city);
        theYearAddsUp(city);
        callableNow(city);
        fairAndTheAuction(city);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        if (fails > 0) System.exit(1);
    }

    /* ================= 1. the pure auction ================= */

    static void purely() {
        out.println("--- 1. the reverse auction, by hand: every holder's rule, solved for its price ---");

        Buyback.Line line = line();
        Auction.Bidders b = calm();
        double fair = fair();
        List<Buyback.Offer> ladder = Buyback.offers(line, b);

        check("fixture: the line is worth less than par on a market over its coupon", fair < 100);
        check("each of the four holders but the central bank cut its holding into SLICES slices",
                ladder.size() == 4 * Buyback.SLICES);
        near("the central bank's face is never offered: the ladder is the principal less its face",
                faceOf(ladder), PRINCIPAL - 150, 1e-9);
        boolean over = true, sorted = true;
        for (int i = 0; i < ladder.size(); i++) {
            over &= ladder.get(i).per100() >= fair - 1e-12;
            if (i > 0) sorted &= ladder.get(i - 1).per100() <= ladder.get(i).per100();
        }
        check("every offer is at or over fair", over);
        check("...and the ladder is the cheapest first", sorted);

        List<Buyback.Offer> bank = of(ladder, Buyback.Holder.BANK);
        boolean allFair = true;
        for (Buyback.Offer o : bank) allFair &= o.per100() == fair;
        check("the bank offers everything it holds at exactly fair (D34)", allFair && bank.size() == Buyback.SLICES);
        near("...all of it", faceOf(bank), 400, 1e-9);

        // The households: HOUSEHOLD_PAPER_APPETITE of the line for each unit of yield over the deposit rate.
        List<Buyback.Offer> hh = of(ladder, Buyback.Holder.HOUSEHOLDS);
        double firstYield = .02 + (200 * .9 / PRINCIPAL) / HouseholdBalance.HOUSEHOLD_PAPER_APPETITE;
        near("the households' first slice is the line's payments at the deposit rate plus the spread the rest of their holding asks",
                hh.get(0).per100(), per100(firstYield), 1e-9);
        check("...which is over fair: they hold it for more than a deposit pays", hh.get(0).per100() > fair);
        boolean hhMatches = true, hhDearer = true;
        for (int j = 1; j <= Buyback.SLICES; j++) {
            double left = 200.0 * (Buyback.SLICES - j) / Buyback.SLICES;
            double expect = Math.max(fair, per100(.02 + (left / PRINCIPAL) / HouseholdBalance.HOUSEHOLD_PAPER_APPETITE));
            hhMatches &= Math.abs(hh.get(j - 1).per100() - expect) <= 1e-9;
            if (j > 1) hhDearer &= hh.get(j - 1).per100() > hh.get(j - 2).per100();
        }
        check("each household slice is priced at the holding that remains once it is sold", hhMatches);
        check("...so the more they sell the dearer the next slice", hhDearer);
        near("...and their last slice, with nothing left to hold, is the line at the deposit rate",
                hh.get(Buyback.SLICES - 1).per100(), per100(.02), 1e-9);

        // The businesses: OutwardInvestment's APPETITE per unit over the better of the deposit and the world, of their idle money.
        List<Buyback.Offer> firms = of(ladder, Buyback.Holder.FIRMS);
        boolean firmsMatch = true, firmsDearer = true;
        for (int j = 1; j <= Buyback.SLICES; j++) {
            double left = 100.0 * (Buyback.SLICES - j) / Buyback.SLICES;
            double expect = Math.max(fair, per100(Math.max(.02, .03) + (left / 1_000) / OutwardInvestment.APPETITE));
            firmsMatch &= Math.abs(firms.get(j - 1).per100() - expect) <= 1e-9;
            if (j > 1) firmsDearer &= firms.get(j - 1).per100() > firms.get(j - 2).per100();
        }
        check("each business slice is priced at the holding that remains, as a share of their idle money", firmsMatch);
        check("...and dearer as they sell", firmsDearer);

        // The world: CapitalFlows' APPETITE of the city's output for each unit of excess return, to MAX_SPREAD.
        List<Buyback.Offer> world = of(ladder, Buyback.Holder.WORLD);
        boolean worldMatches = true, worldDearer = true;
        for (int j = 1; j <= Buyback.SLICES; j++) {
            double stock = 150 - 15.0 * j;
            double spread = Math.min(CapitalFlows.MAX_SPREAD, stock / (CapitalFlows.APPETITE * 1_000 * 12));
            double expect = Math.max(fair, per100(.03 + .005 + spread));
            worldMatches &= Math.abs(world.get(j - 1).per100() - expect) <= 1e-9;
            if (j > 1) worldDearer &= world.get(j - 1).per100() > world.get(j - 2).per100();
        }
        check("each world slice is priced at the stock that remains: its rate, its premium and the excess that holds it", worldMatches);
        check("...and dearer as it sells", worldDearer);

        // The price is each slice's own.
        double ask = 500;
        Buyback.Result r = Buyback.run(line, b, Buyback.Ask.anyPrice(ask));
        double expectPaid = 0, left = ask;
        for (Buyback.Offer o : ladder) {
            double f = Math.min(o.face(), left);
            if (f <= 0) break;
            expectPaid += f * o.per100() / 100;
            left -= f;
        }
        near("the city takes the face it asked for", r.takenFace(), ask, 1e-9);
        near("...and pays the sum of face times each slice's own price (multiple price, D19)", r.paid(), expectPaid, 1e-9);
        check("the stop is the dearest offer taken, the average is lower, and the average is not under fair",
                r.stopPer100() >= r.averagePer100() && r.averagePer100() >= fair - 1e-12);
        check("...strictly lower than the stop: the cheaper offers were not paid the stop", r.averagePer100() < r.stopPer100());
        near("the average is the price paid over the face taken", r.averagePer100(), r.paid() / r.takenFace() * 100, 1e-9);
        check("what the auction cost is what was paid over fair, and it is not negative", r.overFair() >= -1e-9);
        near("...computed on the face taken", r.overFair(), r.paid() - fair * ask / 100, 1e-9);
        near("the gain on the books is the face's carrying value less the price", r.gainOnCarrying(), 980 * ask / PRINCIPAL - r.paid(), 1e-9);
        near("...and against face, the face less the price", r.gainOnFace(), ask - r.paid(), 1e-9);
        near("what was offered over what was asked", r.cover(), (PRINCIPAL - 150) / ask, 1e-9);

        // The cheapest first: what was taken is the front of the ladder, and nothing left behind is cheaper than the stop.
        double takenSoFar = 0;
        boolean cheapestFirst = true;
        for (Buyback.Offer o : ladder) {
            if (takenSoFar + o.face() <= ask + 1e-9) cheapestFirst &= o.per100() <= r.stopPer100() + 1e-12;
            else if (takenSoFar >= ask - 1e-9) cheapestFirst &= o.per100() >= r.stopPer100() - 1e-12;
            takenSoFar += o.face();
        }
        check("the offers taken are the cheapest, and no offer left behind is under the stop", cheapestFirst);
        double hhTaken = 0, bankTaken = 0, firmsTaken = 0, worldTaken = 0;
        for (Buyback.Offer o : r.taken()) {
            switch (o.holder()) {
                case BANK -> bankTaken += o.face();
                case HOUSEHOLDS -> hhTaken += o.face();
                case FIRMS -> firmsTaken += o.face();
                default -> worldTaken += o.face();
            }
        }
        check("the offers taken, by holder, are the faces the result says each sold",
                Math.abs(bankTaken - r.bankFace()) <= 1e-9 && Math.abs(hhTaken - r.householdFace()) <= 1e-9
                        && Math.abs(firmsTaken - r.firmFace()) <= 1e-9 && Math.abs(worldTaken - r.worldFace()) <= 1e-9);

        // A part of the cheapest: the bank's alone, at one price.
        Buyback.Result part = Buyback.run(line, b, Buyback.Ask.anyPrice(100));
        near("a small ask is filled from the bank alone, at fair", part.bankFace(), 100, 1e-9);
        check("...so the stop is the average is fair", part.stopPer100() == fair && Math.abs(part.averagePer100() - fair) <= 1e-9);
        near("...and the auction cost nothing over fair", part.overFair(), 0, 1e-9);

        // More than there is.
        Buyback.Result all = Buyback.run(line, b, Buyback.Ask.anyPrice(5_000));
        near("asked for more than is offered, the city takes all that is offered", all.takenFace(), PRINCIPAL - 150, 1e-9);
        check("...and the cover says it fell short", all.cover() < 1);
        near("the faces by holder add to the face taken",
                all.bankFace() + all.householdFace() + all.firmFace() + all.worldFace(), all.takenFace(), 1e-9);
        near("...and the prices by holder to the price paid",
                all.bankPaid() + all.householdPaid() + all.firmPaid() + all.worldPaid(), all.paid(), 1e-9);

        // A highest price between two offers.
        int m = 12;
        while (ladder.get(m).per100() == ladder.get(m + 1).per100()) m++;
        double cap = (ladder.get(m).per100() + ladder.get(m + 1).per100()) / 2;
        Buyback.Result capped = Buyback.run(line, b, new Buyback.Ask(5_000, cap));
        double cheaper = 0;
        for (int i = 0; i <= m; i++) cheaper += ladder.get(i).face();
        near("with a highest price between two offers only the cheaper are taken", capped.takenFace(), cheaper, 1e-9);
        check("...and the stop is the dearest of them, under the highest price", capped.stopPer100() == ladder.get(m).per100() && capped.stopPer100() <= cap);
        check("...the next offer being over it", ladder.get(m + 1).per100() > cap);
        Buyback.Result nothing = Buyback.run(line, b, new Buyback.Ask(5_000, fair - .01));
        near("a highest price under the cheapest offer takes nothing", nothing.takenFace(), 0, 0);
        near("...and pays nothing", nothing.paid(), 0, 0);

        // The edges of the rules.
        List<Buyback.Offer> noIdle = of(Buyback.offers(line, bidders(.02, 0, 150, 1_000)), Buyback.Holder.FIRMS);
        boolean fairAll = true;
        for (Buyback.Offer o : noIdle) fairAll &= o.per100() == fair;
        check("businesses with no idle money want none of it, and offer at fair", fairAll && noIdle.size() == Buyback.SLICES);
        List<Buyback.Offer> thin = of(Buyback.offers(line, bidders(.02, 50, 150, 1_000)), Buyback.Holder.FIRMS);
        check("a holding past OutwardInvestment.MAX_SHARE of their idle money is offered at fair: the first slice",
                thin.get(0).per100() == fair && 90.0 / 50 > OutwardInvestment.MAX_SHARE);
        check("...and the last, with the share under MAX_SHARE, over it", thin.get(Buyback.SLICES - 1).per100() > fair);
        Auction.Bidders run = new Auction.Bidders(.02, .03, .005, 0, 1_000, 0, 150, true, 1_000, 0, 0);
        boolean runFair = true;
        for (Buyback.Offer o : of(Buyback.offers(line, run), Buyback.Holder.WORLD)) runFair &= o.per100() == fair;
        check("the world in a run wants none of it and offers at fair", runFair);
        Buyback.Line twin = new Buyback.Line(PRINCIPAL, flows(), fair, 980, 400, 200, 100, 150, 150, false);
        check("the same line and the same bidders give the same ladder", Buyback.offers(twin, b).equals(ladder));

        // Dollar paper.
        Buyback.Line dollar = new Buyback.Line(PRINCIPAL, flows(), fair, 980, 0, 0, 0, PRINCIPAL, 0, true);
        List<Buyback.Offer> dl = Buyback.offers(dollar, b);
        boolean dollarFair = true;
        for (Buyback.Offer o : dl) dollarFair &= o.per100() == fair && o.holder() == Buyback.Holder.WORLD;
        check("dollar paper is the world's alone, every slice at fair (D26)", dollarFair && dl.size() == Buyback.SLICES);
        near("...all of it", faceOf(dl), PRINCIPAL, 1e-9);
    }

    /* ================= 2. a hiking city ================= */

    static void hiking() {
        out.println("\n--- 2. a deposit rate over the line's yield: the households' rule prices under fair, so they offer at fair ---");

        Buyback.Line line = line();
        double fair = fair();
        Auction.Bidders hike = bidders(.07, 1_000, 150, 1_000);
        double rule = per100(.07 + (200 * .9 / PRINCIPAL) / HouseholdBalance.HOUSEHOLD_PAPER_APPETITE);
        check("fixture: the deposit rate is over the line's yield", .07 > MARKET_YIELD);
        check("fixture: the households' rule, solved for its first slice, is under fair", rule < fair);
        List<Buyback.Offer> ladder = Buyback.offers(line, hike);
        boolean hhFair = true, firmsFair = true;
        for (Buyback.Offer o : of(ladder, Buyback.Holder.HOUSEHOLDS)) hhFair &= o.per100() == fair;
        for (Buyback.Offer o : of(ladder, Buyback.Holder.FIRMS)) firmsFair &= o.per100() == fair;
        check("so every household slice is offered at exactly fair, never under", hhFair);
        check("...and the businesses', whose better alternative is the deposit now", firmsFair);
        check("the world, whose rule is its own rate, still asks over fair for its last slices",
                of(ladder, Buyback.Holder.WORLD).get(Buyback.SLICES - 1).per100() > fair);

        boolean order = true;
        for (int i = 0; i < Buyback.SLICES; i++) {
            order &= ladder.get(i).holder() == Buyback.Holder.BANK;
            order &= ladder.get(Buyback.SLICES + i).holder() == Buyback.Holder.HOUSEHOLDS;
            order &= ladder.get(2 * Buyback.SLICES + i).holder() == Buyback.Holder.FIRMS;
        }
        check("slices at the same price go to the bank first, then the households, then the businesses (D19)", order);
        Buyback.Result r = Buyback.run(line, hike, Buyback.Ask.anyPrice(700));
        near("the city can buy the bank's, the households' and the businesses' for fair and no more", r.overFair(), 0, 1e-9);
        near("...paying fair for every one of them", r.averagePer100(), fair, 1e-9);
        near("the households sold their whole holding", r.householdFace(), 200, 1e-9);
    }

    /* ================= the played city ================= */

    static Debt unsettledPiece(DebtManager ledger) {
        Debt found = null;
        for (Debt d : ledger.getDebt()) if (!d.isForeign() && d.unsettledFace() > 0) found = d;
        return found;
    }

    /** A line sold through the auction: the piece the sale went into, unsettled until the next month. */
    static Debt sell(Game g, double ask, int years) {
        quietly(() -> g.handleLongBondLogic(ask, years, 100 * PriceLevel.FACTOR));
        return unsettledPiece(g.getDebtManager());
    }

    /** The city HoldersCheck's section 11 was written against, with the world given an appetite (a tenth of the deposits, as in an early boom). */
    static Game playedCity(String label) {
        GameFiles files = GameFiles.scratch(label);
        Game city = new Game(files);
        // The world these sections are written against: the constant world rate (XR4), as HoldersCheck's section 9 stands it (EP1). Hot money bids for the
        // city's paper over the world's rate (Auction.worldAt()), and the file's rate stood at 2.25% on main's path and at 5.5% in the draws of 1900: the
        // bank's bid at fair took the whole line (1,180k of it) and the world, bidding nothing under 5.5%, held none of it. Its premise unmoved.
        city.quietTheWorldForTheD();
        quietly(() -> {
            city.run();
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 50_000_000L);
            city.getForeignAccounts().pinRate(1.0);
            city.getBusinessInvestment().holdSector(Sectors.CONSTRUCTION);
            // ...and Heavy Industry held (EP1), as ForeignCheck holds it (LD1): in the draws of 1900 it raised a plant in month 62, the month after the
            // town is played to, on 215k of ten-year bonds and a 414k loan, and the households' savings fell from 526k to 212k that month (the bonds
            // and the shares took them), so the households that were allotted 508k of the line at its sale could pay 148k of it at the settle.
            city.getBusinessInvestment().holdSector(Sectors.HEAVY_INDUSTRY);
            /* ...AND EVERY OTHER SECTOR HELD, SO THE SAVERS' RATE STAYS UNDER THE CLEARING YIELD (GW1). The households bid at an auction only over the savers'
               rate: Auction.householdsAt() is HOUSEHOLD_PAPER_APPETITE of the issue for each unit of the stop over Bank.depositRate(). On GW1's path (the staffing
               gate counts the workers an order's posts would draw, Sector.GATE_DRAW_MONTHS) the businesses order their buildings earlier and borrow for them, and
               at section 9's callable sale (month 97) the line cleared at 6.584%, 0 bp over fair, under a savers' rate of 6.64%: the households bid nothing and
               the line of D$10,328k went bank 10,322k, households 0, world 5k. With every sector held (a held sector still books, trades and pays its people,
               and never asks to build) the same sale at month 97 clears at 12.816% over a savers' rate of 6.60%, the households' spare for paper is D$16,347k
               and the line of D$49,044k goes bank 32,676k, households 16,347k, world 20k. The sections are about the paper and its holders, not the businesses'
               building (the same cause as HoldersCheck's reopening fixture). */
            for (String key : Sectors.KEYS) city.getBusinessInvestment().holdSector(key);
            city.buildStack(template(city, "Gravel Road"), 6, true);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Commercial Bank"), 1, false);
            city.simulateMonths(60);
            for (int i = 0; i < 36 && city.getMigration().getLastDecliningShare() > 0; i++) city.simulateMonths(1);
        });
        return city;
    }

    /** Every sector's till, added up. */
    static double tills(Game g) {
        double sum = 0;
        for (String key : Sectors.KEYS) sum += g.getEconomyManager().getSectorCash(key);
        return sum;
    }

    /** The world with an appetite for the city's paper: a tenth of the hot money it had, as early in a boom. */
    static void worldWantsPaper(Game city) {
        CapitalFlows hot = city.getCapitalFlows();
        double[] cf = hot.toSaveArray();
        cf[0] = hot.getStock() / 10;
        hot.restore(cf);
        city.getBank().setForeignDeposits(hot.getStock());
    }

    /* ================= 3. a played city, half of a line ================= */

    static void halfOfALine(Game city) {
        out.println("\n--- 3. a played city: half of a ten-year line bought back, then most of the rest, then the last ---");

        DebtManager ledger = city.getDebtManager();
        HouseholdBalance hb = city.getHouseholdBalance();

        worldWantsPaper(city);
        double ask = Math.max(100 * PriceLevel.FACTOR, hb.spareForPaper() * 3);
        Debt bond = sell(city, ask, 10);
        check("fixture: the treasury sold a ten-year line", bond != null);
        check("fixture: ...which is unsettled until the month turns, and a part of it quotes nothing",
                bond.unsettledFace() > 0 && city.quoteBuyback(bond, bond.getOustandingPrincipal() / 2, Double.NaN).takenFace() == 0);
        play(city);
        check("fixture: it is settled, and the households, the bank and the world each hold part of it",
                bond.unsettledFace() == 0 && bond.householdPrincipal() > 0 && bond.bankPrincipal() > 0 && bond.worldPrincipal() > 0);
        check("fixture: ...and the central bank none, so the whole principal can be offered", bond.centralBankPrincipal() == 0);
        out.printf("   the line: D$%,.0fk - the bank %,.0fk, the households %,.0fk, the businesses %,.0fk, the world %,.0fk%n",
                bond.getOustandingPrincipal(), bond.bankPrincipal(), bond.householdPrincipal(), bond.firmPrincipal(), bond.worldPrincipal());

        // Half of it: the cheapest offers, which are the bank's at fair.
        double p0 = bond.getOustandingPrincipal();
        Buyback.Result first = part(city, bond, p0 / 2, "half");
        check("half: the piece is still on the books, at half its principal",
                ledger.getDebt().contains(bond) && Math.abs(bond.getOustandingPrincipal() - p0 / 2) <= MoneyAudit.CENT);

        // Most of what is left: past the bank's holding, so the other holders sell at their own prices.
        double left = bond.getOustandingPrincipal();
        Buyback.Result second = part(city, bond, left * .8, "most of the rest");
        check("fixture: ...and this time the households, the businesses or the world sold too, at prices over the bank's",
                second.householdFace() + second.firmFace() + second.worldFace() > 0 && second.stopPer100() > second.fairPer100());
        check("most of the rest: the piece is still on the books, at a fifth of what was left",
                ledger.getDebt().contains(bond) && Math.abs(bond.getOustandingPrincipal() - left * .2) <= MoneyAudit.CENT);

        // The last of it: the piece leaves the books.
        double last = bond.getOustandingPrincipal();
        double cash = city.getCash();
        Buyback.Result third = city.buyBack(bond, last, Double.NaN);
        near("the last: asked for all that is left, the city takes it", third.takenFace(), last, 1e-6);
        check("...and the piece leaves the books", !ledger.getDebt().contains(bond));
        near("...the cash fell by what was paid", cash - city.getCash(), third.paid(), 1e-6);
        check("...and the holders' books agree with the paper: none of its principal is left in the cells",
                Math.abs(hb.totalPaper() - ledger.householdPrincipal()) <= 1e-6
                        && Math.abs(city.getCapitalFlows().getPaper() - ledger.worldPrincipal()) <= 1e-6);
        check("the last: the audit closes that month and the next", closes(play(city)) && closes(play(city)));
        near("(the three together took the whole line)", first.takenFace() + second.takenFace() + third.takenFace(), p0, 1e-6);
    }

    /**
     * A part of a settled piece bought back, and everything a part has to hold: the piece scaled, its rate where it was,
     * each holder's principal down by what it sold, each paid its own price, the books of the holders and the treasury's
     * cash, and the audit and the gain of the month the payments are declared in.
     */
    static Buyback.Result part(Game city, Debt bond, double face, String what) {
        HouseholdBalance hb = city.getHouseholdBalance();
        Bank bank = city.getBank();
        CapitalFlows hot = city.getCapitalFlows();
        DebtManager ledger = city.getDebtManager();

        double p0 = bond.getOustandingPrincipal(), c0 = bond.getCarryingValue(), f0 = bond.getFaceValue();
        double rate0 = bond.getEffectiveRate(), coupon0 = bond.getMonthlyInterestExpense();
        double issueDiscount0 = bond.getIssueDiscount(), discountLeft0 = bond.getDiscountLeft();
        double[] flows0 = bond.remainingCashFlows();
        double bank0 = bond.bankPrincipal(), hh0 = bond.householdPrincipal(), firm0 = bond.firmPrincipal(), world0 = bond.worldPrincipal();
        double cash0 = city.getCash(), bankCash0 = bank.getCash(), bankBook0 = bank.getCityBook();
        double savings0 = hb.totalSavings(), hotPaper0 = hot.getPaper(), carried0 = city.getBuybackUnsettled();
        double redemption0 = city.getCentralBank().getRedemptionDue();
        double tills0 = tills(city), firmPaper0 = city.getOutwardInvestment().totalPaper();

        Buyback.Result quote = city.quoteBuyback(bond, face, Double.NaN);
        out.printf("   %s: asked D$%,.0fk, offered %.1fx, stop %.3f, average %.3f, fair %.3f, paid D$%,.0fk (bank %,.0fk, households %,.0fk, businesses %,.0fk, world %,.0fk)%n",
                what, face, quote.cover(), quote.stopPer100(), quote.averagePer100(), quote.fairPer100(), quote.paid(),
                quote.bankFace(), quote.householdFace(), quote.firmFace(), quote.worldFace());
        check(what + ": quoting moves nothing: the piece and the cash are as they were",
                bond.getOustandingPrincipal() == p0 && city.getCash() == cash0);
        near(what + ": the quote takes the face asked", quote.takenFace(), face, 1e-6);
        check(what + ": the quote is never under fair", quote.averagePer100() >= quote.fairPer100() - 1e-9 && quote.overFair() >= -1e-6);

        Buyback.Result deal = city.buyBack(bond, face, Double.NaN);
        double k = 1 - deal.takenFace() / p0;
        near(what + ": the quote is struck once and is what is paid", deal.paid(), quote.paid(), 1e-9);
        near(what + ": the principal fell by the face taken", bond.getOustandingPrincipal(), p0 * k, .01);
        near("...the face", bond.getFaceValue(), f0 * k, .01);
        near("...the carrying value", bond.getCarryingValue(), c0 * k, .01);
        near("...the issue discount", bond.getIssueDiscount(), issueDiscount0 * k, .01);
        near("...and what is left of it", bond.getDiscountLeft(), discountLeft0 * k, .01);
        near("...the coupon", bond.getMonthlyInterestExpense(), coupon0 * k, .01);
        near("...but the effective rate is the same", bond.getEffectiveRate(), rate0, 0);
        double[] flows1 = bond.remainingCashFlows();
        double worst = 0;
        for (int i = 0; i < flows0.length; i++) worst = Math.max(worst, Math.abs(flows1[i] - flows0[i] * k));
        check("...and every payment of its schedule, the months it has left unchanged", flows1.length == flows0.length && worst <= .01);
        near("the bank's principal fell by exactly what it sold", bank0 - bond.bankPrincipal(), deal.bankFace(), 1e-6);
        near("...the households'", hh0 - bond.householdPrincipal(), deal.householdFace(), 1e-6);
        near("...the businesses'", firm0 - bond.firmPrincipal(), deal.firmFace(), 1e-6);
        near("...the world's", world0 - bond.worldPrincipal(), deal.worldFace(), 1e-6);
        near("...and the face sold is the face asked", deal.bankFace() + deal.householdFace() + deal.firmFace() + deal.worldFace(), face, 1e-6);
        near("the treasury's cash fell by what was paid", cash0 - city.getCash(), deal.paid(), 1e-6);
        near("the bank was paid its own slices' prices", bank.getCash() - bankCash0, deal.bankPaid(), 1e-6);
        near("...its book of the city's paper fell by the face it sold", bankBook0 - bank.getCityBook(), deal.bankFace(), 1e-6);
        near("the households' savings rose by their own slices' prices", hb.totalSavings() - savings0, deal.householdPaid(), 1e-6);
        near("the world's book of the paper fell by the face it sold", hotPaper0 - hot.getPaper(), deal.worldFace(), 1e-6);
        near("the businesses' tills rose by their own slices' prices, at once", tills(city) - tills0, deal.firmPaid(), 1e-6);
        near("...and their paper fell by the face they sold", firmPaper0 - city.getOutwardInvestment().totalPaper(), deal.firmFace(), 1e-6);
        near("what the households and the world were paid is carried to the next month's window",
                city.getBuybackUnsettled() - carried0, deal.householdPaid() + deal.worldPaid(), 1e-6);
        near("the central bank was paid nothing", city.getCentralBank().getRedemptionDue(), redemption0, 0);
        check("the two books of the households' paper agree, and of the world's",
                Math.abs(hb.totalPaper() - ledger.householdPrincipal()) <= 1e-6 && Math.abs(hot.getPaper() - ledger.worldPrincipal()) <= 1e-6);

        MoneyAudit.Result m1 = play(city);
        near("the gain on debt retired the month booked is the carrying value of the face taken less the price",
                city.getEconomyManager().getNationalAccounts().getRetiredGain(), deal.gainOnCarrying(), 1e-6);
        check("the audit closes the month the payments are declared in", closes(m1));
        check("...and the month after", closes(play(city)));
        near("the piece pays the smaller coupon, month on month", bond.getMonthlyInterestExpense(), coupon0 * k, .01);
        return deal;
    }

    /* ================= 4. the central bank holds part ================= */

    static void centralBankHolds() {
        out.println("\n--- 4. the central bank holds part of a line: it does not offer, it is not paid ---");

        // A city of its own: the dial stays on after this, and the other sections sell lines it would buy a share of.
        Game city = playedCity("buybackcheck-cb");
        DebtManager ledger = city.getDebtManager();
        CentralBank cb = city.getCentralBank();
        HouseholdBalance hb = city.getHouseholdBalance();

        worldWantsPaper(city);
        Debt bond = sell(city, Math.max(100, hb.spareForPaper() * 3), 10);
        check("fixture: the treasury sold another line", bond != null);
        play(city);
        cb.setTargetShare(.3);
        int guard = 0;
        while (bond.centralBankPrincipal() <= 0 && guard++ < 24) play(city);
        check("fixture: the central bank, on its dial, holds part of the line", bond.centralBankPrincipal() > 0 && guard < 24);
        check("fixture: ...and the bank, the households or the world hold the rest",
                bond.bankPrincipal() + bond.householdPrincipal() + bond.firmPrincipal() + bond.worldPrincipal() > 0);

        double principal = bond.getOustandingPrincipal(), cbFace = bond.centralBankPrincipal();
        double cbHeld = cb.getPaperHeld(), redemption = cb.getRedemptionDue();
        double cash0 = city.getCash();
        Buyback.Result quote = city.quoteBuyback(bond, principal, Double.NaN);
        near("asked for the whole principal, the quote takes the whole principal less the central bank's face",
                quote.takenFace(), principal - cbFace, 1e-6);
        near("...and asked is held to what can be offered", quote.asked(), principal - cbFace, 1e-6);

        Buyback.Result deal = city.buyBack(bond, principal, Double.NaN);
        near("the buyback took the same", deal.takenFace(), principal - cbFace, 1e-6);
        check("the piece stays on the books", ledger.getDebt().contains(bond));
        near("the central bank's principal is unchanged", bond.centralBankPrincipal(), cbFace, 0);
        near("...and what is left of the piece is exactly that", bond.getOustandingPrincipal(), cbFace, 1e-6);
        near("...the bank holds nothing of it any more", bond.bankPrincipal(), 0, 1e-6);
        near("the central bank's book of paper is unchanged", cb.getPaperHeld(), cbHeld, 0);
        near("...and it was paid nothing: no price is due to it", cb.getRedemptionDue(), redemption, 0);
        near("the cash fell by what was paid", cash0 - city.getCash(), deal.paid(), 1e-6);
        check("the audit closes that month and the next", closes(play(city)) && closes(play(city)));
        near("the central bank still holds what the paper says it does", cb.getPaperHeld(), ledger.centralBankPrincipal(), 1e-6);
    }

    /* ================= 5. no money pump ================= */

    static void noPump(Game city) {
        out.println("\n--- 5. can the city print money with this? A line sold and bought back ---");

        HouseholdBalance hb = city.getHouseholdBalance();
        city.setCashForTest(Founding.WEALTHY_CASH);

        // The same month: a whole line, sold and bought straight back before its settle, goes off the books as it always did.
        for (String kind : new String[]{"long", "medium", "bill"}) {
            double ask = Math.max(100 * PriceLevel.FACTOR, hb.spareForPaper() * 2);
            double before = city.getCash();
            quietly(() -> {
                switch (kind) {
                    case "long" -> city.handleLongBondLogic(ask, 10, 100 * PriceLevel.FACTOR);
                    case "medium" -> city.handleMediumBondLogic(ask, 10, 100 * PriceLevel.FACTOR);
                    default -> city.handleTBillLogic(ask, 12, 100 * PriceLevel.FACTOR);
                }
            });
            double raised = city.getCash() - before;
            Debt fresh = unsettledPiece(city.getDebtManager());
            check("fixture (the same month): a " + kind + " line was sold, and all of it is unsettled",
                    fresh != null && raised > 0 && Math.abs(fresh.unsettledFace() - fresh.getOustandingPrincipal()) <= 1e-6);
            if (fresh == null) continue;
            double face = fresh.getOustandingPrincipal();
            Buyback.Result back = city.buyBack(fresh, 1e12, Double.NaN);
            check("the " + kind + " line, bought straight back whole in the month it was sold, is off the books",
                    back.takenFace() > 0 && Math.abs(back.takenFace() - face) <= 1e-6 * Math.max(1, face)
                            && !city.getDebtManager().getDebt().contains(fresh));
            out.printf("   %-6s the same month: D$%,.0fk of face raised D$%,.2fk, bought back for D$%,.2fk (%.2f per 100, fair %.2f)%n",
                    kind, face, raised, back.paid(), back.averagePer100(), back.fairPer100());
            check("a " + kind + " round trip in the same month never nets the city money", raised - back.paid() <= .01);
            check("the audit closes that month and the next", closes(play(city)) && closes(play(city)));
        }

        // The month between: sold, settled, bought back.
        for (String kind : new String[]{"long", "medium", "bill"}) {
            double worst = Double.NEGATIVE_INFINITY, total = 0;
            for (int round = 0; round < 2; round++) {
                double ask = Math.max(100 * PriceLevel.FACTOR, hb.spareForPaper() * 2);
                double before = city.getCash();
                quietly(() -> {
                    switch (kind) {
                        case "long" -> city.handleLongBondLogic(ask, 10, 100 * PriceLevel.FACTOR);
                        case "medium" -> city.handleMediumBondLogic(ask, 10, 100 * PriceLevel.FACTOR);
                        default -> city.handleTBillLogic(ask, 12, 100 * PriceLevel.FACTOR);
                    }
                });
                double raised = city.getCash() - before;
                Debt fresh = unsettledPiece(city.getDebtManager());
                check("fixture: a " + kind + " line was sold", fresh != null && raised > 0);
                if (fresh == null) continue;
                // The face the sale put on the books (a line reopened has older face the sale did not sell).
                double soldFace = fresh.unsettledFace();
                play(city);
                Buyback.Result back = city.buyBack(fresh, soldFace, Double.NaN);
                double paid = back.paid();
                check("fixture: the " + kind + " line settled and the face sold was bought back", paid > 0
                        && Math.abs(back.takenFace() - soldFace) <= 1e-6 * Math.max(1, soldFace));
                double net = raised - paid;
                worst = Math.max(worst, net);
                total += net;
                out.printf("   %-6s round %d: D$%,.0fk of face raised D$%,.2fk, bought back for D$%,.2fk (%.2f per 100, fair %.2f), the city is D$%,.2fk %s%n",
                        kind, round + 1, soldFace, raised, paid, back.averagePer100(), back.fairPer100(), Math.abs(net), net >= 0 ? "ahead" : "behind");
            }
            check("a " + kind + " round trip never nets the city money", worst <= .01);
            check("...and two of them do not either", total <= .01);
        }
        check("the audit still closes after the round trips", closes(play(city)));
    }

    /* ================= 6. dollar paper ================= */

    static void dollars() throws Exception {
        out.println("\n--- 6. dollar paper is the world's alone, bought back at fair ---");

        Path root = TempDirs.create("buyback-dollar");
        Game g;
        System.setOut(quiet);
        try { g = ForeignDebtCheck.tradingCity(root.resolve("trading")); } finally { System.setOut(out); }
        g.setCashForTest(Founding.WEALTHY_CASH);
        DebtManager ledger = g.getDebtManager();
        quietly(() -> g.handleForeignLogic("Term", RestructureCheck.DOLLAR_ASK, 30, 100 * PriceLevel.FACTOR, false));
        Debt usd = null;
        for (Debt d : ledger.getDebt()) if (d.isForeign()) usd = d;
        check("fixture: the city owes a dollar bond", usd != null && ledger.getForeignPrincipalUsd() > 0);

        double principal = usd.getOustandingPrincipal(), carrying = usd.getCarryingValue(), inDollars = usd.principalInCurrency();
        double rate0 = usd.getEffectiveRate();
        double fairValue = g.fairValueOf(usd);
        Buyback.Result quote = g.quoteBuyback(usd, 1e12, Double.NaN);
        near("the quote takes the whole piece", quote.takenFace(), principal, 1e-6);
        near("...all of it from the world", quote.worldFace(), principal, 1e-6);
        near("...none from anyone else", quote.bankFace() + quote.householdFace() + quote.firmFace(), 0, 0);
        boolean atFair = true;
        for (Buyback.Offer o : quote.ladder()) atFair &= o.holder() == Buyback.Holder.WORLD && o.per100() == quote.fairPer100();
        check("every slice is at fair: the dollar curve's value", atFair && quote.ladder().size() == Buyback.SLICES);
        near("the stop is the average is fair", quote.stopPer100(), quote.averagePer100(), 1e-9);
        near("...and what is paid is the dollar curve's value of the piece", quote.paid(), fairValue, 1e-6);
        near("...the auction cost nothing over fair", quote.overFair(), 0, 1e-6);

        // 40% of it, then the rest.
        double cash0 = g.getCash(), carriedOn0 = g.getBuybackUnsettled();
        Buyback.Result part = g.buyBack(usd, principal * .4, Double.NaN);
        near("a part of it is taken", part.takenFace(), principal * .4, 1e-6);
        check("the piece stays on the books", ledger.getDebt().contains(usd));
        near("...in dollars, down by the part taken", usd.principalInCurrency(), inDollars * .6, 1e-6);
        near("...its carrying value, in dollars and in local money", usd.getCarryingValue(), carrying * .6, 1e-4);
        near("...and its effective rate unchanged", usd.getEffectiveRate(), rate0, 0);
        near("what the world is owed fell with it", ledger.getForeignPrincipalUsd(), inDollars * .6, 1e-6);
        near("the cash fell by what was paid", cash0 - g.getCash(), part.paid(), 1e-6);
        near("...and it leaves the country, carried to the next month's window", g.getBuybackUnsettled() - carriedOn0, part.paid(), 1e-6);
        check("the audit closes: the dollar holder's payment is a financial outflow", closes(play(g)));
        double left = usd.getOustandingPrincipal();      // at the month's exchange rate
        Buyback.Result rest = g.buyBack(usd, 1e12, Double.NaN);
        near("the rest is taken", rest.takenFace(), left, 1e-6);
        check("the dollar bond is off the books", !ledger.getDebt().contains(usd) && ledger.getForeignPrincipalUsd() <= 1e-9);
        check("...and the audit closes the month after", closes(play(g)));
    }

    /* ================= 7. refused ================= */

    static void refused(Game city) {
        out.println("\n--- 7. a buyback the city cannot pay for, or no offer will meet, or a sale not yet settled, changes nothing ---");

        DebtManager ledger = city.getDebtManager();
        HouseholdBalance hb = city.getHouseholdBalance();
        Bank bank = city.getBank();
        city.setCashForTest(Founding.WEALTHY_CASH);

        worldWantsPaper(city);
        Debt bond = sell(city, Math.max(100, hb.spareForPaper() * 3), 5);
        check("fixture: the treasury sold a five-year line", bond != null);
        check("fixture: ...unsettled", bond != null && bond.unsettledFace() > 0);
        double[] unsettled = snapshot(city, bond);
        double unsettledPrincipal = bond.getOustandingPrincipal();
        Buyback.Result part = city.buyBack(bond, unsettledPrincipal / 2, Double.NaN);
        check("a part of a sale not yet settled is refused: nothing is taken, nothing moves",
                part.takenFace() == 0 && same(unsettled, snapshot(city, bond)));
        check("...and so is the whole of it under a highest price that leaves some behind",
                city.quoteBuyback(bond, unsettledPrincipal, city.fairValueOf(bond) / unsettledPrincipal * 100 - .5).takenFace() == 0);
        check("...but the whole of it, at any price, is quoted: it costs what the thin quote says",
                city.quoteBuyback(bond, unsettledPrincipal, Double.NaN).takenFace() > 0
                        && city.quoteRepurchase(bond) == city.quoteBuyback(bond, unsettledPrincipal, Double.NaN).paid());
        check("...and it is worth something", city.fairValueOf(bond) > 0);
        play(city);
        check("fixture: it is settled now", bond.unsettledFace() == 0);

        double principal = bond.getOustandingPrincipal();
        Buyback.Result all = city.quoteBuyback(bond, principal, Double.NaN);
        double quoted = city.quoteRepurchase(bond);
        near("the whole-piece quote is the same as the thin one", quoted, all.paid(), 1e-9);
        near("...and the gain the same", city.repurchaseGain(bond), all.gainOnCarrying(), 1e-9);
        check("...which is never under what the piece is worth to somebody who does not care to hold it",
                quoted >= city.fairValueOf(bond) - 1e-6);

        city.setCashForTest(quoted / 2);
        double cash = city.getCash();
        double[] before = snapshot(city, bond);
        Buyback.Result poor = city.buyBack(bond, principal, Double.NaN);
        check("a city that cannot afford it is refused: nothing is taken", poor.takenFace() == 0 && poor.paid() == 0);
        near("...and was not charged anyway", city.getCash(), cash, 0);
        check("...and nothing else moved: the piece, its holders, the bank, the savings and the world's book",
                same(before, snapshot(city, bond)));
        near("the old button refuses the same", city.repurchaseDebt(bond), 0, 0);

        city.setCashForTest(Founding.WEALTHY_CASH);
        double[] rich = snapshot(city, bond);
        Buyback.Result cheap = city.buyBack(bond, principal, all.fairPer100() - .5);
        check("a highest price under every offer buys nothing", cheap.takenFace() == 0);
        check("...and moves nothing", same(rich, snapshot(city, bond)));
        Buyback.Result none = city.buyBack(null, principal, Double.NaN);
        check("nothing is bought of a piece that is not there", none.takenFace() == 0);
        city.setCashForTest(Founding.WEALTHY_CASH);
        check("the audit closes after the refusals", closes(play(city)));
        check("fixture: the books still hold the piece", ledger.getDebt().contains(bond) && bank.getCityBook() > 0);
    }

    /** Cash, the piece's principal and holders, the bank's cash and book, the households' savings and paper, the world's paper, the carried buyback, the count of pieces. */
    static double[] snapshot(Game g, Debt d) {
        return new double[]{g.getCash(), d.getOustandingPrincipal(), d.bankPrincipal(), d.householdPrincipal(), d.firmPrincipal(),
                d.worldPrincipal(), d.centralBankPrincipal(), d.getCarryingValue(), g.getBank().getCash(), g.getBank().getCityBook(),
                g.getHouseholdBalance().totalSavings(), g.getHouseholdBalance().totalPaper(), g.getCapitalFlows().getPaper(),
                g.getBuybackUnsettled(), g.getDebtManager().getDebt().size()};
    }

    static boolean same(double[] a, double[] b) {
        for (int i = 0; i < a.length; i++) {
            if (Math.abs(a[i] - b[i]) > 1e-9 * Math.max(1, Math.abs(a[i]))) return false;
        }
        return true;
    }

    /* ================= 8. the year adds up ================= */

    static void theYearAddsUp(Game city) {
        out.println("\n--- 8. CD3's star 5: a gain on debt retired is not less spending, month by month and in the year ---");

        DebtManager ledger = city.getDebtManager();
        HouseholdBalance hb = city.getHouseholdBalance();
        city.setCashForTest(Founding.WEALTHY_CASH);

        worldWantsPaper(city);
        Debt bond = sell(city, Math.max(100, hb.spareForPaper() * 3), 10);
        check("fixture: the treasury sold a line", bond != null);
        play(city);

        // Rates pushed up: the line's price falls under what the books carry it at. The city asks for all of it and
        // names the most it will pay, what the line is carried at: every slice it takes is at a gain.
        // FC1: five points a step until the line's price is under the books' (as one step was, on the city this was written
        // against): FC1's builders raise a bigger town, whose line sold at a coupon one step does not take the curve over.
        double carriedPer100;
        Buyback.Result quote;
        int pushes = 0;
        do {
            ledger.setPolicyRate(Math.min(DebtManager.MAX_POLICY_RATE, ledger.getPolicyRate() + .05));
            city.strikeTheCurve();
            play(city);
            city.setCashForTest(Founding.WEALTHY_CASH);
            carriedPer100 = bond.getCarryingValue() / bond.getOustandingPrincipal() * 100;
            quote = city.quoteBuyback(bond, 1e12, carriedPer100);
            pushes++;
        } while (!(quote.fairPer100() < carriedPer100) && ledger.getPolicyRate() < DebtManager.MAX_POLICY_RATE && pushes < 30);
        out.printf("   the policy rate is %.0f%% after %d push(es) of five points%n", ledger.getPolicyRate() * 100, pushes);
        check("fixture: the line is worth less than the books carry it at", quote.fairPer100() < carriedPer100);
        check("fixture: ...and the city buys back at a gain on the carrying value", quote.takenFace() > 0 && quote.gainOnCarrying() > 0);
        out.printf("   the line is carried at %.2f per 100 and is worth %.2f: D$%,.0fk of it taken for D$%,.0fk (carried at D$%,.0fk), a gain of D$%,.0fk%n",
                carriedPer100, quote.fairPer100(), quote.takenFace(), quote.paid(), quote.carriedTaken(), quote.gainOnCarrying());

        Buyback.Result deal = city.buyBack(bond, 1e12, carriedPer100);
        check("fixture: it was bought, in part, no dearer than the highest price named",
                deal.takenFace() > 0 && deal.stopPer100() <= carriedPer100 && ledger.getDebt().contains(bond));
        double gain = deal.gainOnCarrying();

        int months = Rollover.NETTING_MONTHS;
        double[] expenses = new double[months];
        for (int i = 0; i < months; i++) {
            city.setCashForTest(Math.max(city.getCash(), Founding.WEALTHY_CASH));
            play(city);
            expenses[i] = city.getEconomyManager().getNationalAccounts().getTotalExpenses();
        }
        HistorySave history = city.getHistorySave();
        double[] revenue = history.alignedTail("revenue", months);
        double[] surplus = history.alignedTail("surplus", months);
        double[] retired = history.alignedTail("retiredGain", months);
        check("fixture: History has the year of months", revenue.length == months && surplus.length == months && retired.length == months);

        double worst = 0, sumExpenses = 0, sumWith = 0, sumWithout = 0;
        for (int i = 0; i < months; i++) {
            double with = revenue[i] - surplus[i] + retired[i];
            worst = Math.max(worst, Math.abs(with - expenses[i]));
            sumExpenses += expenses[i];
            sumWith += with;
            sumWithout += revenue[i] - surplus[i];
        }
        check("History's revenue less surplus plus the gain on debt retired is the month's expenses, to the cent, every month of the year",
                worst <= .02);
        near("History records the gain in the month of the buyback", retired[0], gain, .02);
        double elsewhere = 0;
        for (int i = 1; i < months; i++) elsewhere += Math.abs(retired[i]);
        near("...and in no other month", elsewhere, 0, .02);
        near("the treasury's month of spending is their average", city.monthOfSpending(), sumExpenses / months, .02);
        near("...and so is the sum of what the history says it was", sumWith / months, sumExpenses / months, .02);
        check("without the gain the year is off by it: this is the bug", Math.abs(sumWithout - sumExpenses) > .02);
        near("...by exactly the gain (revenue less surplus is expenses less the gain)", sumExpenses - sumWithout, gain, .02 * months);
    }

    /* ================= 9. a line callable now ================= */

    /** A coupon over what the households ask, and under the market's yield: the line is worth less than par and the households still ask over par. */
    static final double COUPON_OVER_ASKS = .045;
    /** A coupon so far over the market's yield that the line's fair is itself over the call price. */
    static final double COUPON_OVER_FAIR = .08;

    /** The pure fixture's line at another coupon: ten years of months, fair at the market's yield, the call price as given (NaN: not callable now). */
    static Buyback.Line lineAt(double coupon, double callPer100) {
        double[] per100 = Auction.bulletPer100(coupon, 120);
        double[] f = new double[per100.length];
        for (int i = 0; i < f.length; i++) f[i] = per100[i] * PRINCIPAL / 100;
        double fair = Debt.presentValue(f, MARKET_YIELD) / PRINCIPAL * 100;
        return new Buyback.Line(PRINCIPAL, f, fair, 980, 400, 200, 100, 150, 150, false, callPer100);
    }

    static void callableNow(Game city) {
        out.println("\n--- 9. a line callable now: no holder asks more than the call price (BB1, CD4-C ★6) ---");
        callableByHand();
        callableInPlay(city);
    }

    static void callableByHand() {
        final double call = CallTree.CALL_PRICE;
        Auction.Bidders b = calm();
        Buyback.Line free = lineAt(COUPON_OVER_ASKS, Double.NaN);
        Buyback.Line capped = lineAt(COUPON_OVER_ASKS, call);

        double firstAsk = .02 + (200 * .9 / PRINCIPAL) / HouseholdBalance.HOUSEHOLD_PAPER_APPETITE;
        check("fixture: the coupon is over the yield the households ask for their first slice", COUPON_OVER_ASKS > firstAsk);
        check("fixture: ...and the line's fair is under the call price, the market's yield being over the coupon", free.fairPer100() < call);
        check("fixture: a line with no call price, a NaN or a call price of nothing, is not callable now",
                !free.callableNow() && !lineAt(COUPON_OVER_ASKS, 0).callableNow()
                        && !new Buyback.Line(PRINCIPAL, free.flows(), free.fairPer100(), 980, 400, 200, 100, 150, 150, false).callableNow());
        check("...and one with the call price is", capped.callableNow());

        List<Buyback.Offer> loose = Buyback.offers(free, b), tight = Buyback.offers(capped, b);
        double dearest = loose.get(loose.size() - 1).per100();
        out.printf("   without the call price: from %.3f to %.3f per 100 (fair %.3f); with it: to %.3f%n",
                loose.get(0).per100(), dearest, free.fairPer100(), tight.get(tight.size() - 1).per100());
        check("premise: without the call price the dearest offer is over it", dearest > call);
        check("premise: ...and the cheapest is under it, the bank's at fair", loose.get(0).per100() < call);
        check("a line with no call price offers what it did before the cap existed (the ten-figure line, NaN)",
                loose.equals(Buyback.offers(new Buyback.Line(PRINCIPAL, free.flows(), free.fairPer100(), 980, 400, 200, 100, 150, 150, false), b)));

        boolean atMost = true, sorted = true;
        for (int i = 0; i < tight.size(); i++) {
            atMost &= tight.get(i).per100() <= call;
            if (i > 0) sorted &= tight.get(i - 1).per100() <= tight.get(i).per100();
        }
        check("with the call price every offer is at most it", atMost);
        check("...and the ladder is still the cheapest first", sorted);
        check("...with as many offers, the central bank's face still not among them",
                tight.size() == loose.size() && Math.abs(faceOf(tight) - (PRINCIPAL - 150)) <= 1e-9);

        boolean kept = true, cut = true, same = true, climbs = true, someCut = false, someKept = false;
        for (Buyback.Holder h : Buyback.Holder.values()) {
            List<Buyback.Offer> was = of(loose, h), is = of(tight, h);
            same &= was.size() == is.size();
            for (int j = 0; j < Math.min(was.size(), is.size()); j++) {
                same &= was.get(j).face() == is.get(j).face();
                if (was.get(j).per100() <= call) {
                    kept &= is.get(j).per100() == was.get(j).per100();
                    someKept = true;
                } else {
                    cut &= is.get(j).per100() == call;
                    someCut = true;
                }
                if (j > 0) climbs &= is.get(j - 1).per100() <= is.get(j).per100();
            }
        }
        check("premise: some offers were under the call price and some over it", someKept && someCut);
        check("the offers that were under the call price are unchanged, holder by holder, slice by slice", kept && same);
        check("the ones that were over it are exactly the call price", cut);
        check("each holder's own slices still ascend", climbs);

        // A full buyback: everything offered.
        double offerable = PRINCIPAL - 150;
        Buyback.Result dear = Buyback.run(free, b, Buyback.Ask.anyPrice(PRINCIPAL));
        Buyback.Result cheap = Buyback.run(capped, b, Buyback.Ask.anyPrice(PRINCIPAL));
        near("a full buyback takes all that is offered", cheap.takenFace(), offerable, 1e-9);
        check("its stop is at most the call price", cheap.stopPer100() <= call);
        check("...and what it paid is at most the face taken at the call price", cheap.paid() <= cheap.takenFace() * call / 100 + 1e-9);
        check("premise: without the call price the stop was over it and the city paid more than the face at the call price",
                dear.stopPer100() > call && dear.paid() > dear.takenFace() * call / 100);
        check("so the call price saves the city something", cheap.paid() < dear.paid());
        check("...and moves nothing else: the face taken and the piece's fair are the same",
                Math.abs(cheap.takenFace() - dear.takenFace()) <= 1e-9 && cheap.fairPer100() == dear.fairPer100());

        // A line whose fair is itself over the call price: the cap wins over the floor at fair.
        Buyback.Line richFree = lineAt(COUPON_OVER_FAIR, Double.NaN), rich = lineAt(COUPON_OVER_FAIR, call);
        check("fixture: a coupon so far over the market's yield that the line's fair is over the call price", rich.fairPer100() > call);
        List<Buyback.Offer> richLoose = Buyback.offers(richFree, b), richTight = Buyback.offers(rich, b);
        check("premise: without the call price every offer is at least fair, so over the call price", richLoose.get(0).per100() >= richFree.fairPer100() && richLoose.get(0).per100() > call);
        boolean exactly = true;
        for (Buyback.Offer o : richTight) exactly &= o.per100() == call;
        check("with it every offer is exactly the call price: the cap wins over the floor at fair", exactly && richTight.size() == richLoose.size());
        Buyback.Result richRun = Buyback.run(rich, b, Buyback.Ask.anyPrice(PRINCIPAL));
        near("a full buyback of it pays the call price on average", richRun.averagePer100(), call, 1e-9);

        // The rule is general: dollar paper, whose holder is the world alone, is capped too (it cannot be called, so the game never sets it).
        Buyback.Line dollar = new Buyback.Line(PRINCIPAL, rich.flows(), rich.fairPer100(), 980, 0, 0, 0, PRINCIPAL, 0, true, call);
        List<Buyback.Offer> dollars = Buyback.offers(dollar, b);
        boolean dollarAtCall = dollars.size() == Buyback.SLICES;
        for (Buyback.Offer o : dollars) dollarAtCall &= o.per100() == call;
        check("dollar paper with a call price is offered at it, never at the fair that is over it", dollarAtCall);
    }

    static void callableInPlay(Game city) {
        final double call = CallTree.CALL_PRICE;
        DebtManager ledger = city.getDebtManager();
        HouseholdBalance hb = city.getHouseholdBalance();

        worldWantsPaper(city);
        city.setCashForTest(Founding.WEALTHY_CASH);
        double ask = Math.max(100 * PriceLevel.FACTOR, hb.spareForPaper() * 3);
        out.printf("   m%d: the callable sale: the households' spare for paper D$%,.1fk, the ask %,.1fk, the savers' rate %.4f%n",
                city.getMonth(), hb.spareForPaper(), ask, city.getBank().depositRate());
        quietly(() -> city.handleLongBondLogic(ask, 10, 100 * PriceLevel.FACTOR, Auction.Cap.DEFAULT, true));
        LongTermBond found = null;
        for (Debt d : ledger.getDebt()) if (d instanceof LongTermBond l && l.isCallable() && !l.isForeign() && l.unsettledFace() > 0) found = l;
        check("fixture: the treasury sold a callable ten-year line", found != null);
        if (found == null) return;
        LongTermBond bond = found;
        check("fixture: ...unsettled until the month turns, so it cannot be called", bond.unsettledFace() > 0 && !city.canCall(bond));
        play(city);
        out.printf("   the callable line: D$%,.0fk - the bank %,.0fk, the households %,.0fk, the businesses %,.0fk, the world %,.0fk, the central bank %,.0fk; the bank's equity %,.1f%n",
                bond.getOustandingPrincipal(), bond.bankPrincipal(), bond.householdPrincipal(), bond.firmPrincipal(), bond.worldPrincipal(),
                bond.centralBankPrincipal(), city.getBank().equity());
        check("fixture: it is settled, and the households, the bank and the world each hold part of it, the central bank none",
                bond.unsettledFace() == 0 && bond.householdPrincipal() > 0 && bond.bankPrincipal() > 0 && bond.worldPrincipal() > 0
                        && bond.centralBankPrincipal() == 0);
        check("fixture: its first call month is still ahead, so it cannot be called",
                bond.getFirstCallMonth() > city.getMonth() && !city.canCall(bond));

        // Before the first call month: priced as before, the holders asking what their rules ask.
        double principal = bond.getOustandingPrincipal();
        Buyback.Result notYet = city.quoteBuyback(bond, principal, Double.NaN);
        Buyback.Line byHand = new Buyback.Line(principal, ledger.pricingFlows(bond), ledger.marketValue(bond) / principal * 100,
                bond.getCarryingValue(), bond.bankPrincipal(), bond.householdPrincipal(), bond.firmPrincipal(),
                bond.worldPrincipal(), bond.centralBankPrincipal(), false);
        check("a line not yet callable is quoted as the holders ask: the ladder is Buyback.offers() with no call price",
                !notYet.ladder().isEmpty() && notYet.ladder().equals(Buyback.offers(byHand, city.auctionBidders())));
        List<Buyback.Offer> before = notYet.ladder();
        double dearest = before.get(before.size() - 1).per100();
        out.printf("   the line, not yet callable: D$%,.0fk, fair %.3f per 100, offers from %.3f to %.3f%n",
                principal, notYet.fairPer100(), before.get(0).per100(), dearest);
        check("premise: the dearest offer is over the call price (" + String.format("%.3f", dearest) + " per 100)", dearest > call);
        near("the quote takes it all", notYet.takenFace(), principal, 1e-6);

        // At the first call month: the same city, the same holders, no offer over the call price. A callable line's fair moves
        // with its call date (it is worth no more than the call price once it can be called), so the offers it is held against
        // are the line's uncapped ladder at the fair of this month, built by hand: each quoted offer is the least of its price and the call price.
        bond.moveFirstCallForTest(city.getMonth());
        check("at its first call month it can be called", city.canCall(bond));
        double callCost = city.callCost(bond);
        Buyback.Result now = city.quoteBuyback(bond, principal, Double.NaN);
        List<Buyback.Offer> after = now.ladder();
        Buyback.Line uncapped = new Buyback.Line(principal, ledger.pricingFlows(bond), ledger.marketValue(bond) / principal * 100,
                bond.getCarryingValue(), bond.bankPrincipal(), bond.householdPrincipal(), bond.firmPrincipal(),
                bond.worldPrincipal(), bond.centralBankPrincipal(), false);
        List<Buyback.Offer> loose = Buyback.offers(uncapped, city.auctionBidders());
        double looseDearest = loose.isEmpty() ? 0 : loose.get(loose.size() - 1).per100();
        check("premise: the same line with no call price, at this month's fair, still has its dearest offer over the call price (" + String.format("%.3f", looseDearest) + " per 100)", looseDearest > call);
        boolean atMost = !after.isEmpty() && after.size() == loose.size(), least = atMost;
        for (int i = 0; atMost && i < after.size(); i++) {
            atMost &= after.get(i).per100() <= call;
            least &= after.get(i).per100() == Math.min(call, loose.get(i).per100());
        }
        check("callable now, every offer on the ladder is at most the call price", atMost);
        check("...each the least of what it would have been with no call price and the call price", least);
        check("...and the same face on offer", Math.abs(faceOf(after) - faceOf(loose)) <= 1e-9);
        check("its stop is at most the call price (" + String.format("%.3f", now.stopPer100()) + " per 100)", now.stopPer100() <= call);
        near("the quote still takes it all", now.takenFace(), principal, 1e-6);
        check("what it asks is at most what calling the line costs, and less than the quote before the call month",
                now.paid() <= callCost + 1e-6 && now.paid() < notYet.paid());

        // The deal booked is the quote shown.
        city.setCashForTest(Founding.WEALTHY_CASH);
        double cash = city.getCash();
        Buyback.Result done = city.buyBack(bond, principal, Double.NaN);
        near("the buyback booked pays what the quote said", done.paid(), now.paid(), 1e-6);
        near("...its stop is the quote's", done.stopPer100(), now.stopPer100(), 1e-9);
        check("...it is within what calling the line would have cost, and the line leaves the books",
                done.paid() <= callCost + 1e-6 && !ledger.getDebt().contains(bond));
        near("...the cash fell by what was paid", cash - city.getCash(), done.paid(), 1e-6);
    }

    /* ================= 10. what the auction books and what fair would ================= */

    /** What the three figures say of one quote: the identities that hold of every result, whoever the holders. */
    static void figuresAdd(String what, Buyback.Result r) {
        double tol = 1e-9 * Math.max(1, r.paid());
        near(what + ": the gain at fair less the gain the auction books is what the holders ask over fair",
                r.gainAtFair() - r.gainOnCarrying(), r.overFair(), tol);
        near("...the gain at fair is the carrying value of the face taken less its worth at fair",
                r.gainAtFair(), r.carriedTaken() - r.takenFace() * r.fairPer100() / 100, tol);
        double sum = 0;
        boolean none = true;
        for (Buyback.Holder h : Buyback.Holder.values()) {
            sum += r.overFair(h);
            none &= r.overFair(h) >= -tol;
        }
        near("...the holders' overFair add up to the auction's", sum, r.overFair(), tol);
        check("...and no holder is paid under fair (every offer is at or over it)", none);
        near("...the bank is paid fair (D34): its overFair is nothing", r.overFair(Buyback.Holder.BANK), 0, tol);
        near("...the share is the gain at fair over the carrying value of the face taken",
                r.gainAtFairShare(), r.carriedTaken() > 0 ? r.gainAtFair() / r.carriedTaken() : 0, 1e-12);
    }

    /** The households hold the whole line, carried at a fraction of the way from its worth at fair to what the auction pays. */
    static Buyback.Result householdsHold(double between) {
        Buyback.Line probe = new Buyback.Line(PRINCIPAL, flows(), fair(), 0, 0, PRINCIPAL, 0, 0, 0, false);
        Buyback.Result r = Buyback.run(probe, calm(), Buyback.Ask.anyPrice(PRINCIPAL));
        double worthAtFair = r.takenFace() * r.fairPer100() / 100;
        double carriedTaken = worthAtFair + between * (r.paid() - worthAtFair);
        double carrying = carriedTaken * PRINCIPAL / r.takenFace();
        return Buyback.run(new Buyback.Line(PRINCIPAL, flows(), fair(), carrying, 0, PRINCIPAL, 0, 0, 0, false),
                calm(), Buyback.Ask.anyPrice(PRINCIPAL));
    }

    static void fairAndTheAuction(Game city) {
        out.println("\n--- 10. what the auction books and what fair would: they differ by what the holders ask over fair (BB1, CD6-C ★1) ---");

        // Section 1's line: 500 of it takes the bank's 400 at fair and 100 of the dearer holders' slices.
        Buyback.Line line = line();
        Buyback.Result r = Buyback.run(line, calm(), Buyback.Ask.anyPrice(500));
        int holders = 0;
        double dearest = 0;
        Buyback.Holder asking = null;
        for (Buyback.Holder h : Buyback.Holder.values()) {
            double face = switch (h) {
                case BANK -> r.bankFace();
                case HOUSEHOLDS -> r.householdFace();
                case FIRMS -> r.firmFace();
                case WORLD -> r.worldFace();
            };
            if (face > 0) holders++;
            if (r.overFair(h) > dearest) { dearest = r.overFair(h); asking = h; }
        }
        out.printf("   500 of section 1's line: at fair %+,.3f; at the asks %+,.3f; over fair %,.3f (%s the dearest)%n",
                r.gainAtFair(), r.gainOnCarrying(), r.overFair(), asking);
        check("premise: the quote takes offers from more than one holder", holders > 1);
        check("premise: some holder is paid over fair (" + asking + ", " + String.format("%.3f", dearest) + ")", dearest > 0);
        figuresAdd("a quote of several holders", r);
        near("...the gain at fair, by hand: the face's carrying value less its worth at fair", r.gainAtFair(), 980 * 500 / PRINCIPAL - fair() * 500 / 100, 1e-9);
        check("...so the book's desk reads more than the auction books: the gain at fair is over the gain on the carrying value", r.gainAtFair() > r.gainOnCarrying());

        // Only the bank's offers taken: all at fair, so the two figures agree.
        Buyback.Result bank = Buyback.run(line, calm(), Buyback.Ask.anyPrice(100));
        figuresAdd("a quote of the bank's offers alone", bank);
        check("premise: it took the bank's face alone", bank.bankFace() == 100 && bank.takenFace() == 100);
        near("...at fair the two agree: nothing is asked over fair", bank.overFair(), 0, 1e-9);
        near("...and the gain at fair is the gain the auction books", bank.gainAtFair(), bank.gainOnCarrying(), 1e-9);

        // Nothing taken: nothing carried, no share.
        Buyback.Result nothing = Buyback.run(line, calm(), Buyback.Ask.anyPrice(0));
        check("a quote that takes nothing has no gain at fair and no share of one",
                nothing.gainAtFair() == 0 && nothing.gainAtFairShare() == 0 && nothing.overFair(Buyback.Holder.HOUSEHOLDS) == 0);

        // The case CD6 saw: green at fair, red at the auction, the households holding the line.
        Buyback.Result seen = householdsHold(.5);
        out.printf("   the households hold the line, carried half way between fair and the asks: at fair %+,.3f (%+.1f%%), at the asks %+,.3f (%+.1f%%)%n",
                seen.gainAtFair(), seen.gainAtFairShare() * 100, seen.gainOnCarrying(), seen.gainOnCarryingShare() * 100);
        check("premise: the households hold all of what was taken", seen.takenFace() > 0 && seen.householdFace() == seen.takenFace());
        check("premise: they ask over fair (" + String.format("%.3f", seen.overFair(Buyback.Holder.HOUSEHOLDS)) + ")", seen.overFair(Buyback.Holder.HOUSEHOLDS) > 0);
        check("premise: the line is carried between its worth at fair and what the auction pays",
                seen.carriedTaken() > seen.takenFace() * seen.fairPer100() / 100 && seen.carriedTaken() < seen.paid());
        figuresAdd("the households' line", seen);
        check("at fair the line reads a gain", seen.gainAtFair() > 0 && seen.gainAtFairShare() > 0);
        check("...and the auction books a loss: the figures have opposite signs", seen.gainOnCarrying() < 0 && seen.gainOnCarryingShare() < 0);
        near("...the households' overFair is the whole of what the auction asks over fair", seen.overFair(Buyback.Holder.HOUSEHOLDS), seen.overFair(), 1e-9);

        // A played city: a line sold and settled, all of it quoted, then half.
        DebtManager ledger = city.getDebtManager();
        HouseholdBalance hb = city.getHouseholdBalance();
        worldWantsPaper(city);
        city.setCashForTest(Founding.WEALTHY_CASH);
        double ask = Math.max(100 * PriceLevel.FACTOR, hb.spareForPaper() * 3);
        Debt bond = sell(city, ask, 10);
        check("fixture: the treasury sold a ten-year line", bond != null);
        if (bond == null) return;
        play(city);
        check("fixture: it is settled, and held by more than the bank", bond.unsettledFace() == 0 && ledger.getDebt().contains(bond)
                && bond.householdPrincipal() + bond.firmPrincipal() + bond.worldPrincipal() > 0);
        double principal = bond.getOustandingPrincipal();
        Buyback.Result all = city.quoteBuyback(bond, principal, Double.NaN);
        check("fixture: the quote takes the line", all.takenFace() > 0);
        out.printf("   the played line: at fair %+,.1f (%+.1f%%), at the asks %+,.1f (%+.1f%%), over fair %,.1f%n",
                all.gainAtFair(), all.gainAtFairShare() * 100, all.gainOnCarrying(), all.gainOnCarryingShare() * 100, all.overFair());
        figuresAdd("a played line, all of it", all);
        Buyback.Result half = city.quoteBuyback(bond, principal / 2, Double.NaN);
        figuresAdd("...and half of it", half);
        check("a smaller face takes the cheaper offers first: each 100 of half costs no more over fair than each 100 of all",
                half.overFair() / half.takenFace() <= all.overFair() / all.takenFace() + 1e-12);
    }
}
