package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The city's paper bought back in a reverse auction: every holder offers what
 * it holds in slices, each at the price where its own buying rule would stop
 * wanting it and never under fair, and the city takes the cheapest offers
 * first, paying each its own price.
 *
 * WHY: until CD4 a buyback was a button that cleared a whole piece at one
 * price, fair, and paid every holder its share of it pro rata - the households
 * who held it for the spread they asked, the world that held it for its excess
 * return, were bought out at the same number as the bank. Nobody sells a
 * bond they are glad to hold for what it is worth to somebody who does not
 * care. Here the city names how much face of ONE piece it wants and the
 * most it will pay per 100, each holder offers by the rule it already follows
 * when it BUYS (Auction's bidders' rules, read the other way round), and what
 * the city pays is the sum of what the offers it took asked: the stop is the
 * dearest of them, and what it cost over fair is a figure the screen can show
 * before the button (spec-city-debt.md, section 6.2-6.3; D12, D17, D18, D19,
 * D26, D34).
 *
 * This class is pure, as Auction is: no Game, handed a snapshot of the piece
 * (a {@link Line}) and of the bidders (Auction.Bidders, the same record the
 * auction reads, so the buyback and the sale see the same city). The caller
 * works out what the snapshot holds - the piece's remaining payments, its
 * fair price off the curve, who holds how much - and pays the result holder by
 * holder; nothing here moves money.
 *
 * DOLLAR PAPER (D26): its only holder is the world. It offers the whole piece,
 * every slice at fair - the dollar curve's value, which
 * DebtManager.marketValue() already returns for it - because the world's rule
 * for the city's own paper (CapitalFlows' excess-return appetite) is not the
 * rule that holds a dollar bond.
 */
public final class Buyback {

    private Buyback() { }

    /* =====================================================================
       THE DIAL
       ===================================================================== */

    /** How many equal slices each holder cuts its holding into: the more there are, the finer the ladder of prices the holder's rule draws (D12). */
    public static final int SLICES = 10;

    /* =====================================================================
       WHAT IS ON OFFER, WHAT IS ASKED, WHAT COMES OUT
       ===================================================================== */

    /** The holders who offer, in the order a tie in price goes to them (D19): the bank first, the world last. The central bank is not among them (D18). */
    public enum Holder { BANK, HOUSEHOLDS, FIRMS, WORLD }

    /**
     * One slice on the ladder.
     *
     * @param holder who offers it
     * @param face   its face, local money
     * @param per100 its price per 100 of face
     */
    public record Offer(Holder holder, double face, double per100) { }

    /**
     * What the city asks for.
     *
     * @param face      the face it wants back, local money
     * @param maxPer100 the most it will pay per 100, or NaN for no limit
     */
    public record Ask(double face, double maxPer100) {

        /** This much face at any price. */
        public static Ask anyPrice(double face) {
            return new Ask(face, Double.NaN);
        }

        /** True when the ask names a highest price. */
        public boolean capped() {
            return !Double.isNaN(maxPer100);
        }
    }

    /**
     * The piece as it stands when the buyback runs. Every figure is the
     * caller's, read from the model; this class holds none of the city.
     *
     * @param principal       the piece's outstanding principal before the buyback, local money
     * @param flows           its remaining payments, one a month starting next month, local money
     * @param fairPer100      what it is worth per 100 of principal on the curve (DebtManager.marketValue() over the principal)
     * @param carryingValue   what the books carry it at, local money
     * @param bankFace        what the commercial bank holds, face
     * @param householdFace   ...the households
     * @param firmFace        ...the businesses
     * @param worldFace       ...the world (for dollar paper: all of it)
     * @param centralBankFace ...the central bank, which does not offer (D18)
     * @param foreign         true for dollar paper (D26): the world's alone, every slice at fair
     */
    public record Line(double principal, double[] flows, double fairPer100, double carryingValue,
                       double bankFace, double householdFace, double firmFace, double worldFace,
                       double centralBankFace, boolean foreign) {

        /** The face that can be offered: everything but the central bank's. */
        public double offerable() {
            return foreign ? Math.max(0, worldFace)
                    : Math.max(0, bankFace) + Math.max(0, householdFace) + Math.max(0, firmFace) + Math.max(0, worldFace);
        }
    }

    /**
     * The buyback: the ladder, what was taken from whom at what, and the three
     * comparisons the city books (spec 6.3).
     *
     * @param ladder         every offer, the cheapest first
     * @param asked          the face the city asked for
     * @param offeredFace    the face on the ladder
     * @param takenFace      the face taken: the ask or what was offered at or under the highest price, whichever is less
     * @param paid           what the city pays: each slice taken at its own price (multiple price, D19)
     * @param bankFace       what was taken from the bank, face; likewise below
     * @param bankPaid       and what the bank is paid for it
     * @param stopPer100     the dearest offer taken, per 100; 0 when nothing was taken
     * @param averagePer100  paid over the face taken, per 100; 0 when nothing was taken
     * @param fairPer100     the piece's fair price per 100
     * @param carriedTaken   the books' carrying value of the face taken
     */
    public record Result(List<Offer> ladder, double asked, double offeredFace, double takenFace, double paid,
                         double bankFace, double bankPaid, double householdFace, double householdPaid,
                         double firmFace, double firmPaid, double worldFace, double worldPaid,
                         double stopPer100, double averagePer100, double fairPer100, double carriedTaken) {

        /** A buyback that took nothing: no ladder, nothing asked of the books. */
        public static Result none(double asked) {
            return new Result(List.of(), Math.max(0, asked), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }

        /** What was offered over what was asked: 1 or more when the ask can be filled, under 1 when it cannot. */
        public double cover() {
            return asked > 0 ? offeredFace / asked : 0;
        }

        /** The statement's "Gain (loss) on debt retired": the carrying value of the face taken less what was paid (CD3). */
        public double gainOnCarrying() {
            return carriedTaken - paid;
        }

        /**
         * The premium still on the books for the face taken (CD6): the carrying value of the face taken
         * less the face - the journal's "Unamortised premium", which the buyback takes off with the paper;
         * below nothing it is the discount still on the books, the journal's "Unamortised discount".
         */
        public double unamortisedTaken() {
            return carriedTaken - takenFace;
        }

        /** What the debt-to-GDP measure sees: the face taken less what was paid. */
        public double gainOnFace() {
            return takenFace - paid;
        }

        /** What the auction cost: what was paid over what the same face is worth at fair. */
        public double overFair() {
            return paid - fairPer100 * takenFace / 100;
        }

        /** The offers that were taken, in the order they were: the ladder up to the stop, the last one in part when the ask ran out. */
        public List<Offer> taken() {
            List<Offer> out = new ArrayList<>();
            double left = takenFace;
            for (Offer o : ladder) {
                if (!(left > 0)) break;
                double f = Math.min(o.face(), left);
                out.add(new Offer(o.holder(), f, o.per100()));
                left -= f;
            }
            return out;
        }
    }

    /**
     * THE HONEST LINE's four figures (CD6; spec-city-debt section 6.3): a buyback's gain is real on the
     * statement, but the cash to pay for it costs today's rate. Game.honestLine() strikes them from a quote.
     *
     * @param months        the months the piece has left, one at least: the term the city would borrow again for
     * @param rate          the curve's rate for that term, a year (the dollar curve for dollar paper)
     * @param couponStopped the coupon a month the buyback stops: the piece's coupon in the share of its principal taken
     * @param costAgain     what borrowing the price again would cost a month, at that rate
     */
    public record HonestLine(int months, double rate, double couponStopped, double costAgain) {

        /** What the city's interest does a month if it borrows the price again: above nothing it rises, below it falls. */
        public double change() {
            return costAgain - couponStopped;
        }
    }

    /* =====================================================================
       EACH HOLDER'S OFFERS: its buying rule, read the other way round

       A holder sells a slice at the price where its own rule would STOP
       wanting the bond: the yield at which it would still want to hold what
       would remain once the slice is sold, discounted into a price. The more
       it sells, the less it holds, the lower the yield it would still hold at
       and the dearer the next slice. Never under fair: a holder whose rule
       wants nothing of the bond (its required yield is over what the bond
       pays) sells at what the market would pay, not under it.
       ===================================================================== */

    /** The piece's remaining payments discounted at one flat yield, per 100 of principal: Debt.presentValue's convention, the one every price in the game uses. */
    static double per100At(Line line, double yield) {
        if (!(line.principal() > 0) || line.flows() == null || line.flows().length == 0) return 0;
        return Debt.presentValue(line.flows(), yield) / line.principal() * 100;
    }

    /**
     * The households' yield for a holding h left: the deposit rate plus the
     * spread their rule asks to hold that share of the line - Auction.householdsAt
     * solved for the yield, HOUSEHOLD_PAPER_APPETITE of the line for each unit
     * of yield over the deposit rate.
     */
    public static double householdsYield(double remaining, Line line, Auction.Bidders b) {
        return Math.max(0, b.depositRate())
                + (Math.max(0, remaining) / line.principal()) / HouseholdBalance.HOUSEHOLD_PAPER_APPETITE;
    }

    /**
     * The businesses' yield for a holding f left: the better of the deposit
     * and the world, plus the spread that holds f of their idle wealth -
     * Auction.firmsAt solved for the yield. NaN when their rule wants nothing:
     * no idle wealth, or that share of it past OutwardInvestment.MAX_SHARE,
     * which no spread asks them to hold.
     */
    public static double firmsYield(double remaining, Auction.Bidders b) {
        if (!(b.firmsIdle() > 0)) return Double.NaN;
        double share = Math.max(0, remaining) / b.firmsIdle();
        if (share > OutwardInvestment.MAX_SHARE) return Double.NaN;
        return Math.max(b.depositRate(), b.worldRate()) + share / OutwardInvestment.APPETITE;
    }

    /**
     * The world's yield for a stock s left: its rate plus the country's
     * premium plus the excess return that holds s - CapitalFlows' appetite
     * solved for the yield, never past MAX_SPREAD. NaN when its rule wants
     * nothing: no output to size an appetite against, or the world in a run.
     */
    public static double worldYield(double stockLeft, Auction.Bidders b) {
        if (b.worldPanicking() || !(b.monthlyGdp() > 0)) return Double.NaN;
        double spread = Math.min(CapitalFlows.MAX_SPREAD,
                Math.max(0, stockLeft) / (CapitalFlows.APPETITE * b.monthlyGdp() * 12));
        return b.worldRate() + b.countryPremium() + spread;
    }

    /** A slice's price: the piece's payments at the yield its holder would still hold at, never under fair. NaN yield (the rule wants nothing) is fair. */
    private static double priceAt(Line line, double yield) {
        double fair = line.fairPer100();
        if (Double.isNaN(yield)) return fair;
        return Math.max(fair, per100At(line, yield));
    }

    /** The ladder: every holder's slices, the cheapest first, ties to the bank, the households, the businesses, the world (D19). */
    public static List<Offer> offers(Line line, Auction.Bidders bidders) {
        List<Offer> ladder = new ArrayList<>();
        if (line == null || !(line.principal() > 0)) return ladder;
        Auction.Bidders b = bidders == null ? Auction.Bidders.none() : bidders;
        double fair = line.fairPer100();

        if (line.foreign()) {
            // D26: the world's alone, whole, every slice at fair.
            slices(ladder, Holder.WORLD, line.worldFace(), n -> fair);
        } else {
            // D34: the bank offers at fair, as it bids at fair at the auction.
            slices(ladder, Holder.BANK, line.bankFace(), n -> fair);
            double hh = Math.max(0, line.householdFace());
            slices(ladder, Holder.HOUSEHOLDS, hh, left -> priceAt(line, householdsYield(left, line, b)));
            double firms = Math.max(0, line.firmFace());
            slices(ladder, Holder.FIRMS, firms, left -> priceAt(line, firmsYield(left, b)));
            double world = Math.max(0, line.worldFace());
            double stock = Math.max(0, b.worldStock());
            // The world's stock is the whole of what it has in the city, of which this piece's slices are part:
            // each slice sold takes that much off it, so the next asks for the stock that is left.
            slices(ladder, Holder.WORLD, world, left -> priceAt(line, worldYield(stock - (world - left), b)));
            // The central bank does not offer (D18): its face stays on the piece.
        }
        // A stable sort: price, then the holder's place in a tie; a holder's own slices already ascend.
        ladder.sort((x, y) -> {
            int c = Double.compare(x.per100(), y.per100());
            return c != 0 ? c : Integer.compare(x.holder().ordinal(), y.holder().ordinal());
        });
        return ladder;
    }

    /** How a holder prices a slice: given the holding that REMAINS once the slice is sold. */
    private interface SlicePrice {
        double of(double remaining);
    }

    /** Cuts one holder's holding into SLICES equal slices, each priced at the holding that remains once it is sold, and never under the slice before it. */
    private static void slices(List<Offer> ladder, Holder holder, double holding, SlicePrice price) {
        if (!(holding > 0)) return;
        double each = holding / SLICES;
        double floor = Double.NEGATIVE_INFINITY;
        for (int j = 1; j <= SLICES; j++) {
            double remaining = j == SLICES ? 0 : holding - each * j;
            double p = Math.max(floor, price.of(remaining));
            ladder.add(new Offer(holder, each, p));
            floor = p;
        }
    }

    /* =====================================================================
       THE REVERSE AUCTION
       ===================================================================== */

    /**
     * Run the buyback: take the cheapest offers first up to the face asked,
     * the last one in part, skipping any over the most per 100, and pay each
     * its own price (D19). The stop is the dearest offer taken; the average is
     * what was paid over the face taken.
     */
    public static Result run(Line line, Auction.Bidders bidders, Ask ask) {
        double asked = ask == null || !(ask.face() > 0) ? 0 : ask.face();
        List<Offer> ladder = offers(line, bidders);
        double offered = 0;
        for (Offer o : ladder) offered += o.face();
        if (line == null || !(asked > 0) || ladder.isEmpty()) {
            return new Result(ladder, asked, offered, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    line == null ? 0 : line.fairPer100(), 0);
        }

        double left = asked, taken = 0, paid = 0, stop = 0;
        double[] face = new double[Holder.values().length], cost = new double[face.length];
        for (Offer o : ladder) {
            if (!(left > 0)) break;
            if (ask.capped() && o.per100() > ask.maxPer100()) break;     // sorted: nothing later is cheaper
            double f = Math.min(o.face(), left);
            double c = f * o.per100() / 100;
            face[o.holder().ordinal()] += f;
            cost[o.holder().ordinal()] += c;
            taken += f;
            paid += c;
            stop = o.per100();
            left -= f;
        }
        double average = taken > 0 ? paid / taken * 100 : 0;
        double carried = line.principal() > 0 ? line.carryingValue() * taken / line.principal() : 0;
        return new Result(ladder, asked, offered, taken, paid,
                face[Holder.BANK.ordinal()], cost[Holder.BANK.ordinal()],
                face[Holder.HOUSEHOLDS.ordinal()], cost[Holder.HOUSEHOLDS.ordinal()],
                face[Holder.FIRMS.ordinal()], cost[Holder.FIRMS.ordinal()],
                face[Holder.WORLD.ordinal()], cost[Holder.WORLD.ordinal()],
                taken > 0 ? stop : 0, average, line.fairPer100(), carried);
    }
}
