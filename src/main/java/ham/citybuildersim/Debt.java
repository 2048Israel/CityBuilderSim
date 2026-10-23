package ham.citybuildersim;

/**
 * One piece of city paper.
 *
 * PRICED OFF ITS OWN CASH FLOWS, NOT OFF ITS SHAPE
 *
 * Everything a bond is worth, and everything it yields, comes from one thing:
 * the money it still owes and when. So that is what a subclass declares -
 * `remainingCashFlows()`, one figure per remaining month - and market value and
 * yield to maturity are computed here, once, from that.
 *
 * The previous version used the closed-form annuity instead:
 *
 *     MV = c x [1 - (1+r)^-n] / r  +  F / (1+r)^n
 *
 * which is exactly right for a bullet and silently wrong for anything else. A
 * serial bond repays principal in slices, so its coupon falls every year and its
 * principal arrives in pieces; the annuity formula would have priced it as
 * though the whole face were sitting at the end, over-valuing it badly. Adding
 * the serial bond is what made the shape assumption visible, but the assumption
 * was already there.
 *
 * A schedule cannot be wrong about a shape it does not know about. The next
 * instrument - a revenue bond, a serial with a deferred first slice, anything -
 * gets correct pricing and yield with no change here at all.
 */
public abstract class Debt {

    protected double faceValue;
    protected double outstandingPrincipal;
    protected int duration;
    protected int remainingMonths;
    protected int monthStarted;
    protected String type;

    /* =======================================================================
       WHICH MONEY THIS PAPER IS WRITTEN IN
       =======================================================================

       ORIGINAL SIN, which is the name the literature gives it: a city that
       cannot borrow abroad in its own currency carries the exchange risk
       itself. It owes dollars and earns local money, so a devaluation makes the
       debt dearer without anybody having borrowed another cent. Domestic paper
       does the opposite - inflation and devaluation quietly shrink it.

       HOW IT IS STORED, and this is the part that has to be got right once:

         - a foreign bond holds its face, its principal and its coupon in USD,
           because that is what the contract says and it does not change when
           the currency moves
         - every PUBLIC getter returns LOCAL money, converted at the live rate,
           because that is what the city's books, its debt-to-GDP, its credit
           rating and every screen are denominated in

       So `getOustandingPrincipal()` on a USD bond rises when the currency falls,
       everywhere in the game at once, with no call site knowing anything about
       it. That is the whole mechanic.

       ENFORCED BY THE COMPILER rather than by memory. The three money-valued
       getters are FINAL here and convert; what a subclass declares instead is
       the figure in the paper's OWN currency. A subclass cannot forget to
       convert, because it is not given the chance - the previous shape, where
       each subclass returned its own number, would have needed the conversion
       written correctly in nine places and wrong in none.
       ======================================================================= */

    /** True if this paper is written in USD. */
    protected boolean foreign;

    /** Local currency per USD, as of the last month tick. See DebtManager. */
    protected double exchangeRate = 1.0;

    public boolean isForeign() { return foreign; }

    /** The rate this paper is currently being valued at. */
    public double getExchangeRate() { return exchangeRate; }

    /**
     * Told to it by DebtManager, every month and on the load path.
     *
     * A rate of zero or less is refused rather than accepted, because a bond
     * valued at zero disappears out of the city's debt silently, and a save
     * restored before the exchange rate is would do exactly that.
     */
    void setExchangeRate(double rate) {
        if (rate > 0) this.exchangeRate = rate;
    }

    /** USD into local money, for foreign paper; the identity for domestic. */
    protected double inLocal(double own) {
        return foreign ? own * exchangeRate : own;
    }

    /* =======================================================================
       WHO HOLDS IT (0.7.1)
       =======================================================================

       Jerus, on the holders: "yes households should be able to hold." Until
       0.7.1 the commercial bank held every dollar of the city's own paper,
       so a bond was a loan with extra steps. Three holders now, and the split
       is what makes a bond market rather than a loan: the HOUSEHOLDS, who
       take a share at the settle when the yield beats the deposit rate and
       sell back to the bank's desk; the CENTRAL BANK, which buys and sells
       from the bank's holdings with money it makes (the holdings dial, QE and
       QT); and the COMMERCIAL BANK, which holds the rest - the residual buyer
       at issue, and the desk on the way back.

       Kept ON THE PAPER, not beside it, so a serial slice, a coupon, a
       maturity or a buyback can split by holder without asking anybody
       else: principal held, in local money, by the two holders who are not
       the bank, and the bank holds what is left. Domestic paper only - a
       dollar bond is held abroad, and these read zero on it.

       SAVED WITH THE PAPER. DataSave serialises the debt list whole with Gson,
       and a key an old save does not carry reads zero: the bank held
       everything, which is exactly what every bank did before 0.7.1.
       ======================================================================= */

    /** Principal of this paper the city's households hold, in local money. Zero on an old save: the bank held everything. */
    protected double householdPrincipal;

    /** ...and the central bank, at face (its open-market holdings). Zero on an old save. */
    protected double centralBankPrincipal;

    /** ...and the businesses, at face (CD2, slice 4): the sum of every sector's paper on OutwardInvestment. Zero on an old save. */
    protected double firmPrincipal;

    /** ...and the world, at face (CD2, slice 5): what it bought at the auction and holds on CapitalFlows' paper book, outside the bank's room. Zero on an old save. */
    protected double worldPrincipal;

    /**
     * What the buyers still owe for this paper: the cash the treasury received
     * at issue, which the households and the bank hand over at the next settle
     * (Game, THE BANK SETTLES). The issue carries what the settle needs on the
     * paper itself, so a city saved between the issue and the settle still
     * settles it right. Zero on an old save, whose settle the totals carried.
     */
    protected double settleDue;

    /** The market rate it was issued at - the curve's rate for its maturity - which the households weigh against the deposit rate at the settle. Zero on an old save. */
    protected double issueYield;

    /**
     * FACE LESS WHAT THE CITY RECEIVED FOR IT, at issue (0.7.1), and the part
     * of it not yet accreted. The discount is the buyer's interest and it
     * belongs to the whole life of the paper, so it accretes straight-line
     * over the months left rather than landing in the settle month - which is
     * where it landed until 0.7.1: $60M of "interest" in one month of seed 0
     * borrowing at home, taxed, 45% of it payable to savers
     * (the-bank-that-never-paid.md section 5). Zero on an old save, whose
     * discount was booked the month it settled.
     */
    protected double issueDiscount;
    protected double discountLeft;

    public double householdPrincipal()   { return foreign ? 0 : householdPrincipal; }
    public double centralBankPrincipal() { return foreign ? 0 : centralBankPrincipal; }
    public double firmPrincipal()        { return foreign ? 0 : firmPrincipal; }
    public double worldPrincipal()       { return foreign ? 0 : worldPrincipal; }

    /** What the commercial bank holds: the rest of the principal after the households, the central bank, the businesses and the world. Zero on a dollar bond, which is held abroad. */
    public double bankPrincipal() {
        return foreign ? 0
                : Math.max(0, getOustandingPrincipal() - householdPrincipal - centralBankPrincipal - firmPrincipal
                        - worldPrincipal);
    }

    public double getSettleDue()      { return settleDue; }
    public double getIssueYield()     { return issueYield; }
    public double getIssueDiscount()  { return issueDiscount; }
    public double getDiscountLeft()   { return discountLeft; }

    /** Told at issue what it raised and at what yield. See Game's handle*Logic(). The city's books open with what it raised (openBooks()); a dollar piece's are opened by Game.handleForeignLogic(). */
    void markIssued(double received, double yield) {
        if (foreign) return;
        settleDue = Math.max(0, received);
        issueYield = yield;
        issueDiscount = Math.max(0, getOustandingPrincipal() - received);
        discountLeft = issueDiscount;
        unsettledFace = getOustandingPrincipal();
        openBooks(received);
    }

    /**
     * A SALE INTO A LINE THAT IS ALREADY ON THE BOOKS (CD2, spec-city-debt.md
     * section 2.2): this much more face of the same paper, at its coupon and
     * its date, for `proceeds`. The face joins the piece's face and principal;
     * what the buyers owe for it joins settleDue (the line's older part was
     * settled when it was sold); the discount it carries - the face less what
     * it raised - joins the discount still to accrete; and the issue yield
     * blends by face, until CD3 replaces it with the effective interest.
     *
     * A PREMIUM (proceeds over face, a line sold at a coupon over today's
     * yield) is not accreted: accrete() and Bank.takeDiscount() both ignore
     * a negative discount, and the bank's unearned discount is held at
     * nothing or more, exactly as markIssued() has always held a new piece's.
     * So the discount stays at nothing or more here too: the premium first
     * eats what the line has left to accrete, and the rest is the buyers'
     * loss when they pay it, never carried. Domestic paper only.
     *
     * THE CITY'S BOOKS (CD3) are not so held: what the sale raised, premium
     * or not, joins the carrying value, and the effective rate is solved
     * again on the whole remaining schedule at the new face - one blended
     * rate a line.
     */
    void reopen(double face, double proceeds, double yield) {
        if (foreign || !(face > 0)) return;
        ensureBooks();
        double was = Math.max(0, outstandingPrincipal);
        faceValue += face;
        outstandingPrincipal += face;
        settleDue += Math.max(0, proceeds);
        unsettledFace += face;
        double discount = face - Math.max(0, proceeds);
        issueDiscount = Math.max(0, issueDiscount + discount);
        discountLeft = Math.max(0, discountLeft + discount);
        issueYield = was + face > 0 ? (issueYield * was + yield * face) / (was + face) : yield;
        carryingValue += Math.max(0, proceeds);
        effectiveRate = effectiveRateOf(scheduleOwed(), carryingValue);
    }

    /**
     * The central bank's own par, rolled into a line that is already on the
     * books (CD2): reopen() for this much face at this price, but paid for at
     * once by the central bank (Game, THE CENTRAL BANK ROLLS ITS OWN, AT
     * ISSUE) - so nothing is owed for it at the settle, none of it is
     * unsettled, and the face is the central bank's.
     */
    void reopenForCentralBank(double face, double price, double yield) {
        if (foreign || !(face > 0)) return;
        double dueBefore = settleDue, unsettledBefore = unsettledFace;
        reopen(face, price, yield);
        settleDue = dueBefore;
        unsettledFace = unsettledBefore;
        centralBankPrincipal += face;
    }

    /** The settle has taken place: nothing is owed for this paper any more, and the allotment is paid. */
    void settled() {
        settleDue = 0;
        unsettledFace = 0;
        allotHouseholdsCash = allotHouseholdsFace = 0;
        allotFirmsCash = allotFirmsFace = 0;
        allotWorldCash = allotWorldFace = 0;
    }

    /* -----------------------------------------------------------------------
       THE ALLOTMENT (CD2, spec-city-debt.md section 4)

       Who the auction gave the paper to, kept on the paper until the settle:
       each bidder's face and the cash it will pay for it. Set at the sale
       (and added to at a reopening), read by the settle, cleared by it. Plain
       fields, so Gson saves them with the piece and a city saved between the
       sale and the settle settles the same. The firms' and the world's pairs
       are written by the sale from the auction's fills: the firms' are read
       by the settle since slice 4 (Game.firmsTakeTheirShare()), the world's
       since slice 5 (Game.worldTakesItsShare()).
       ----------------------------------------------------------------------- */

    /** The face the sale still to be settled gave the households, and the cash they pay for it at the settle. */
    protected double allotHouseholdsFace, allotHouseholdsCash;

    /** ...the businesses' (CD2, slice 4: they pay it from their tills at the settle, Game.firmsTakeTheirShare()). */
    protected double allotFirmsFace, allotFirmsCash;

    /** ...and the world's (CD2, slice 5: it pays it from abroad at the settle, Game.worldTakesItsShare()). */
    protected double allotWorldFace, allotWorldCash;

    /**
     * The face the market has bought since the last settle and not yet paid
     * for: the whole piece for a new one, the reopening's own face for a line
     * reopened. The central bank's add-on is not in it - it pays at issue.
     * What the bank's book, the weighted book and the unearned discount leave
     * out until the settle (bankPrincipalPaid()).
     */
    protected double unsettledFace;

    public double allotHouseholdsFace() { return allotHouseholdsFace; }
    public double allotHouseholdsCash() { return allotHouseholdsCash; }
    public double allotFirmsFace()      { return allotFirmsFace; }
    public double allotFirmsCash()      { return allotFirmsCash; }
    public double allotWorldFace()      { return allotWorldFace; }
    public double allotWorldCash()      { return allotWorldCash; }
    public double unsettledFace()       { return foreign ? 0 : unsettledFace; }

    /**
     * Adds a sale's allotment: each bidder's face from the auction's fills,
     * and the cash it pays - its share of what the sale raised, since the
     * buyers pay together what the treasury was paid (settleDue), the fee
     * and the rest of the price riding in the discount as they always have.
     *
     * @param sold   the face the sale sold
     * @param cash   what it raised
     */
    void allot(Auction.Fill fills, double sold, double cash) {
        if (foreign || !(sold > 0) || fills == null) return;
        double per = Math.max(0, cash) / sold;
        allotHouseholdsFace += fills.households();
        allotHouseholdsCash += fills.households() * per;
        allotFirmsFace += fills.firms();
        allotFirmsCash += fills.firms() * per;
        allotWorldFace += fills.world();
        allotWorldCash += fills.world() * per;
    }

    /**
     * What the commercial bank holds of this paper that it has PAID for: its
     * principal less the face sold since the last settle and not yet paid
     * for. A new piece sold between the presses is on nobody's book until the
     * settle; a line reopened has its older part on the bank's book all the
     * while and only the reopening off it (DebtManager.bankBook()).
     */
    public double bankPrincipalPaid() {
        return foreign ? 0 : Math.max(0, bankPrincipal() - unsettledFace);
    }

    /**
     * The discount the bank has not yet earned on what it has paid for: what
     * is left to accrete on the part of the piece that has settled - the
     * whole of it less the unsettled sale's own - pro rata to the face the
     * bank holds of that part.
     */
    public double unaccretedOnPaid() {
        if (foreign) return 0;
        double settledOut = getOustandingPrincipal() - unsettledFace;
        double paid = bankPrincipalPaid();
        if (!(settledOut > 0) || !(paid > 0)) return 0;
        double settledDiscount = Math.max(0, discountLeft - Math.max(0, unsettledFace - settleDue));
        return settledDiscount * Math.min(1, paid / settledOut);
    }

    /** Moves face between the holders: positive to the households, the central bank, the businesses or the world from the bank, negative back to it. */
    void moveToHouseholds(double face)  { householdPrincipal = Math.max(0, householdPrincipal + face); }
    void moveToCentralBank(double face) { centralBankPrincipal = Math.max(0, centralBankPrincipal + face); }
    void moveToFirms(double face)       { firmPrincipal = Math.max(0, firmPrincipal + face); }
    void moveToWorld(double face)       { worldPrincipal = Math.max(0, worldPrincipal + face); }

    /**
     * THE CENTRAL BANK'S ADD-ON (0.7.15, round 2): this much more face of the
     * same paper, on the same terms, for the central bank, which holds it
     * from issue - its non-competitive bid, which the Federal Reserve Bank of
     * New York's "FAQs: Treasury Rollovers" says is "treated as add-ons to
     * announced auction sizes" (Game, THE CENTRAL BANK ROLLS ITS OWN, AT
     * ISSUE). The piece grows by that face, its schedule in proportion as a
     * change of unit scales it (redenominate()), and the add-on is sold at the
     * price of the sale it rides on, so it carries the same discount on each
     * unit of face: the face less what the market's part of that sale raised
     * (settleDue) over the face of that sale (unsettledFace - a line
     * reopened the same month has older face that is not in the sale). What
     * the market still owes for its own part (settleDue) and the households'
     * holding do not move. Domestic paper only.
     *
     * The city's books (CD3) take it on as a reopening: the carrying value
     * grows by what the add-on raised, its face at that price, and the
     * effective rate is solved again on the whole schedule.
     */
    void addOnForCentralBank(double face) {
        double out = outstandingPrincipal;
        if (foreign || !(face > 0) || !(out > 0)) return;
        ensureBooks();
        double sale = unsettledFace > 0 ? unsettledFace : out;
        double price = settleDue > 0 ? settleDue / sale : 1;
        double discount = Math.max(0, face * (1 - price));
        double k = (out + face) / out;
        faceValue += face;
        outstandingPrincipal = out + face;
        issueDiscount += discount;
        discountLeft += discount;
        redenominateSchedule(k);
        centralBankPrincipal += face;
        carryingValue += face * price;
        effectiveRate = effectiveRateOf(scheduleOwed(), carryingValue);
    }

    /**
     * The unaccreted discount that rides on this much face - the part a
     * holder gives up, or takes on, when that face changes hands.
     */
    public double unaccretedOn(double face) {
        double out = getOustandingPrincipal();
        return out > 0 && face > 0 ? discountLeft * Math.min(1, face / out) : 0;
    }

    /**
     * This month's accretion: what is left of the discount over the months
     * left, taken off what is left. Straight-line, and exact to the last
     * month, when the remainder goes whole. Called by DebtManager before the
     * month's payments, so the holders' shares it is split by are the ones
     * the month opened with.
     */
    double accrete() {
        if (foreign || discountLeft <= 0) return 0;
        double step = remainingMonths <= 1 ? discountLeft : discountLeft / remainingMonths;
        discountLeft = Math.max(0, discountLeft - step);
        return step;
    }

    /* =======================================================================
       THE CITY'S BOOKS (CD3, spec-city-debt.md section 5)

       Jerus's call D10, "effective interest": the city carries each piece of
       its paper at AMORTISED COST - what the sale raised, net of the fixed
       cost, stepped every month by the interest the piece truly costs less
       what it pays. IFRS 9's amortised cost and ASPE 3856's.

           at the sale     carrying value = what the treasury received
                           effective rate = the yield that equates that with
                                            the payments the piece still owes
           every month     expense        = carrying value x effective rate / 12
                           amortised      = expense - coupon (the discount
                                            wearing off; below nothing for a
                                            premium)
                           carrying value += expense - the month's payment
           at the end      carrying value = 0 once the face is repaid, and it
                           equals the face just before

       THE CONVENTION is presentValue()'s - an annual nominal rate,
       compounded monthly, the first payment next month - so the recurrence
       above lands on nothing after the last payment by construction. What
       rounding is left goes into the last month's expense, which closes the
       piece to the cent (amortise()).

       WHAT THIS REPLACES. The budget's "interest" was the coupon alone, so a
       bill - a piece with no coupon, which costs its whole discount - read
       as free money, and a bond sold under par read as dear as a par one.
       The bank's side is NOT this: accrete() above is the bank's straight-
       line discount income (Bank.takeDiscount()), and stays as it was; the
       bank's books are another study's (spec section 10). These fields sit
       beside issueDiscount and discountLeft and read neither.

       ONE CARRYING VALUE AND ONE EFFECTIVE RATE A LINE (D16): a reopening
       adds what the new sale raised to the carrying value and solves the
       blended rate again on the whole remaining schedule (reopen(),
       addOnForCentralBank()). Fungible paper is one liability.

       IN ITS OWN CURRENCY, as the schedule is. A dollar piece's carrying
       value is dollars, and its figures in D$ go through inLocal() at the
       rate the paper is valued at - DebtManager.setExchangeRate(), the
       month's actual D$ - like every other figure it gives the city. What
       the carrying value gains or loses in D$ when the currency moves is a
       revaluation, not interest, and does not enter the expense: the
       expense is booked in dollars and converted, the same as the coupon,
       so expense less coupon is the amortisation and nothing else.

       SAVED WITH THE PIECE, as the holders are: Gson takes every field of
       the hierarchy.
       ======================================================================= */

    /** The lowest effective rate effectiveRateOf() will report: a premium so deep it costs the issuer less than nothing, a year. */
    static final double EFFECTIVE_RATE_FLOOR = -0.5;

    /** The highest, as getYieldToMaturity()'s bracket always was: 500% a year. */
    static final double EFFECTIVE_RATE_CEILING = 5.0;

    /** What the piece is carried at, in the currency it is written in: what it raised, stepped by the effective interest less the payments. Read carryingInCurrency(), which says par for paper whose books were never opened. */
    protected double carryingValue;

    /** The yield, a year, compounded monthly, at which the payments still owed are worth carryingValue: the piece's effective rate. Solved again when the line is reopened. */
    protected double effectiveRate;

    /** True once openBooks() has run: what a sold piece's books are opened with, and what a piece built without a sale (a fixture, the founding's land bond) is given at par by its first month. */
    protected boolean booksOpen;

    /** The month's interest expense and coupon as amortise() last booked them, in the currency the piece is written in. */
    protected double lastExpenseOwn, lastCouponOwn;

    /**
     * The yield, a year compounded monthly, at which these payments are worth
     * this price: the bisection getYieldToMaturity() always made, on a bracket
     * that reaches below nothing so that a piece sold well over par (a coupon
     * over today's yield) has the negative rate it truly costs. PV falls as the
     * rate rises, so it cannot get lost. Nothing owed or nothing raised has no
     * rate: 0. The one place an effective rate is solved: openBooks(),
     * reopen() and addOnForCentralBank() all call it. getYieldToMaturity() is
     * left as it was (its bracket stops at nothing, and the buyback screens
     * read it).
     */
    static double effectiveRateOf(double[] flows, double price) {
        if (flows == null || flows.length == 0 || !(price > 0)) return 0;
        double low = EFFECTIVE_RATE_FLOOR, high = EFFECTIVE_RATE_CEILING;
        for (int i = 0; i < 100; i++) {
            double mid = (low + high) / 2;
            if (presentValue(flows, mid) > price) {
                low = mid;      // still worth more than was raised: the cost is higher
            } else {
                high = mid;
            }
        }
        return (low + high) / 2;
    }

    /**
     * The books are opened with what the sale raised, in the currency the
     * piece is written in (the treasury's cash, net of the fixed cost): that is
     * the carrying value, and the effective rate is solved on the payments the
     * piece still owes. Called by markIssued() for paper at home and by
     * Game.handleForeignLogic() for a dollar piece. A sale that raised nothing
     * is no sale: the piece is carried at par.
     */
    void openBooks(double proceedsOwn) {
        carryingValue = proceedsOwn > 0 && Double.isFinite(proceedsOwn) ? proceedsOwn : principalOwed();
        effectiveRate = effectiveRateOf(scheduleOwed(), carryingValue);
        lastExpenseOwn = lastCouponOwn = 0;
        booksOpen = true;
    }

    /** A piece that was never sold through the treasury (a fixture's, the founding's land bond) is carried at par before anything is added to it or booked on it: its effective rate is its coupon's. */
    private void ensureBooks() {
        if (!booksOpen) openBooks(principalOwed());
    }

    /**
     * This month's books, once, BEFORE processMonth() pays the month (the
     * payment is read off the schedule as it stands, so the books follow the
     * cash by construction): the interest expense at the effective rate on the
     * carrying value, the coupon part of the payment, and the carrying value
     * stepped by the one less the whole payment. In the piece's last month the
     * expense is what closes the carrying value to nothing - the payment less
     * what is carried - so the rounding of a solved rate is in that month and
     * the piece closes to the cent.
     *
     * @return what the discount wore off this month (the expense less the
     *         coupon; below nothing for a premium), in the piece's own currency
     */
    double amortise() {
        double[] schedule = scheduleOwed();
        if (schedule.length == 0) {
            lastExpenseOwn = lastCouponOwn = 0;
            return 0;
        }
        ensureBooks();
        double payment = schedule[0];
        double coupon = couponOwed();
        double expense;
        if (schedule.length == 1) {
            expense = payment - carryingValue;
            carryingValue = 0;
        } else {
            expense = carryingValue * effectiveRate / 12;
            carryingValue += expense - payment;
        }
        lastExpenseOwn = expense;
        lastCouponOwn = coupon;
        return expense - coupon;
    }

    /** What the piece is carried at, in the currency written on it; par for a piece whose books were never opened. */
    public final double carryingInCurrency() { return booksOpen ? carryingValue : principalOwed(); }

    /** ...and in local money, at the rate the paper is valued at (inLocal()). */
    public final double getCarryingValue() { return inLocal(carryingInCurrency()); }

    /** The effective rate, a year: the yield the piece's sale made of its payments. Zero before the books are opened. */
    public final double getEffectiveRate() { return effectiveRate; }

    /** This month's interest expense as amortise() booked it, in local money: the carrying value at the effective rate, a month. */
    public final double getLastInterestExpense() { return inLocal(lastExpenseOwn); }

    /** ...the coupon that month paid, likewise. */
    public final double getLastCoupon() { return inLocal(lastCouponOwn); }

    /** ...and the part of the expense that is not coupon: the discount wearing off, below nothing for a premium. */
    public final double getLastAmortised() { return inLocal(lastExpenseOwn - lastCouponOwn); }

    public abstract void processMonth(Game game);

    public abstract double getIssuePrice();

    public abstract int getMaturityMonth();

    public abstract boolean isMatured();

    public abstract String getType();

    /* ------------- what a subclass declares, in its own currency ------------- */

    /** Principal still owed, in the currency the paper is written in. */
    protected abstract double principalOwed();

    /** This month's coupon, in its own currency. Zero for a discount instrument. */
    protected abstract double couponOwed();

    /**
     * Every payment still owed, in order, starting with next month's, in its own
     * currency.
     *
     * Coupons AND principal, because a buyer does not care which is which - they
     * care what arrives and when. An empty array means nothing is left to pay.
     */
    protected abstract double[] scheduleOwed();

    /* -------------------- ...and what the city's books see -------------------- */

    public final double getOustandingPrincipal() { return inLocal(principalOwed()); }

    /** This month's coupon, in local money. Zero for a discount instrument. */
    public final double getMonthlyInterestExpense() { return inLocal(couponOwed()); }

    public final double[] remainingCashFlows() {
        double[] own = scheduleOwed();
        if (!foreign) return own;
        double[] local = new double[own.length];
        for (int i = 0; i < own.length; i++) local[i] = own[i] * exchangeRate;
        return local;
    }

    /* ------------------- and the same figures, in dollars ------------------- */

    /** Principal still owed, in the currency written on the paper. */
    public final double principalInCurrency() { return principalOwed(); }

    /** Face value in the currency written on the paper. */
    public final double faceInCurrency() { return faceValue; }

    /** This month's coupon in the currency written on the paper. */
    public final double couponInCurrency() { return couponOwed(); }

    /* ------------------- what the next month repays (0.7.13) ------------------- */

    /**
     * The principal the next month's payment repays, in the currency written
     * on the paper: the first payment of its own schedule less that month's
     * coupon - a note's or a term bond's whole face in its last month, a
     * serial bond's slice on an anniversary, and nothing in any other month.
     * Read off scheduleOwed() so the three shapes keep one account of when
     * their principal falls due, not two. What the treasury's rollover reads
     * (Game, ROLLING WHAT FALLS DUE).
     */
    public final double principalDueNextMonthInCurrency() {
        double[] schedule = scheduleOwed();
        return schedule.length == 0 ? 0 : Math.max(0, schedule[0] - couponOwed());
    }

    /** ...and in local money, at the rate the paper is valued at. */
    public final double principalDueNextMonth() { return inLocal(principalDueNextMonthInCurrency()); }

    /* --------------------------- paying for it --------------------------- */

    /**
     * A repayment of principal, routed by the currency it is owed in.
     *
     * The subclasses used to call `game.subtractCash()` directly, which is
     * exactly right for domestic paper and exactly wrong for foreign: a USD
     * repayment does not just leave the treasury, it leaves the COUNTRY, and it
     * must not be handed to the bank as a repayment of a loan the bank never
     * made. Both differences live behind this one call.
     *
     * @param owed in the paper's own currency
     */
    protected void payPrincipal(Game game, double owed) {
        if (foreign) game.repayForeignPrincipal(owed);
        else         game.payDomesticPrincipal(this, owed);
    }

    /**
     * A coupon, likewise - and at home, split by who holds the paper (0.7.1):
     * see Game.payDomesticCoupon(). @param owed in the paper's own currency
     */
    protected void payCoupon(Game game, double owed) {
        if (foreign) game.payForeignInterest(owed);
        else         game.payDomesticCoupon(this, owed);
    }

    /** Months of payments still to run. */
    public int getRemainingMonths() {
        return remainingMonths;
    }

    /** What it says on the bond, in local money. See getOustandingPrincipal(). */
    public final double getFaceValue() {
        return inLocal(faceValue);
    }

    public int getDuration() {
        return duration;
    }

    public int getMonthStarted() {
        return monthStarted;
    }

    /**
     * What this paper is worth today, to somebody buying it.
     *
     * The present value of everything it still owes, discounted at the rate the
     * market is charging the city NOW - and since 0.7.1, for the city's own
     * paper, at the CURVE's rate for the months it has left, which is what
     * DebtManager.marketValue() hands in: a bond with thirty years to run is
     * priced at the thirty-year rate, not at the note's. This method takes
     * the rate it is given, so a fixture can price at anything.
     *
     * WHY THIS IS NOT THE FACE VALUE, which is the whole point of being able to
     * buy it back:
     *
     *   - Market rate ABOVE the coupon -> worth less than face. The city can
     *     extinguish $1,000 of debt for less than $1,000. Not a loophole; it is
     *     what happens to anyone's bonds when their credit deteriorates, and a
     *     city that has damaged its own credit really can retire its paper
     *     cheaply.
     *   - Market rate BELOW the coupon -> worth more than face, and getting out
     *     early costs a premium. A city that borrowed dear and then improved
     *     pays for the lender's good deal.
     *
     * PRICED AT THE CURRENT RATE, not at the rate that would apply afterwards.
     * There is a fixed point lurking - retiring debt lowers the city's rate,
     * which raises what the remaining bonds are worth - and issuance already
     * walks that loop in quoteRate(). Deliberately not walked here: a buyer
     * quotes you off the market as it stands, and chasing the post-trade rate
     * would make every quote depend on which bond you happened to buy first.
     */
    public double getMarketValue(double annualMarketRate) {

        /*
         * Due now, so there is nothing left to discount and it costs what is
         * owed to clear it.
         *
         * processAllDebts() removes matured paper, so this should be
         * unreachable in play - but "should be unreachable" is how the schedule
         * would quietly return an empty array and price a bond that is due this
         * instant at ZERO, which on the buyback screen is a free retirement of
         * real debt. Worth a line to make impossible rather than improbable.
         */
        if (remainingMonths <= 0) {
            return getOustandingPrincipal();
        }
        return presentValue(remainingCashFlows(), annualMarketRate);
    }

    /** PV of a monthly schedule at an annual nominal rate. */
    static double presentValue(double[] cashFlows, double annualRate) {

        if (cashFlows == null || cashFlows.length == 0) {
            return 0;
        }

        double r = annualRate / 12.0;

        // A non-positive rate discounts nothing. MIN_RATE keeps the real market
        // well above this, but a fixture is free to hand in anything and an
        // undiscounted sum is the honest limit rather than a divide-by-zero.
        if (r <= -1 + 1e-9) {
            double sum = 0;
            for (double cf : cashFlows) sum += cf;
            return sum;
        }

        double pv = 0;
        double discount = 1;
        for (double cf : cashFlows) {
            discount /= (1 + r);
            pv += cf * discount;
        }
        return pv;
    }

    /**
     * The yield a buyer earns at a given price - the bond's true cost to the
     * city, as opposed to the coupon printed on it.
     *
     * This is the number that makes "premium" and "discount" mean something. A
     * 3% bond bought at 80 cents on the dollar does not yield 3%; it yields
     * whatever makes its remaining payments worth 80. Coupon is what the paper
     * says, yield is what the money does, and for a long bond issued at a deep
     * discount the two are nowhere near each other.
     *
     * SOLVED BY BISECTION, not by a formula, because there is no closed form for
     * an arbitrary schedule - which is exactly the point of having a schedule.
     * PV falls monotonically as the rate rises, so bisection cannot get lost;
     * eighty iterations over a 0-500% bracket is far more precision than a
     * percentage on a screen can use.
     *
     * @return the nominal annual yield, or 0 if the bond owes nothing
     */
    public double getYieldToMaturity(double price) {

        double[] flows = remainingCashFlows();
        if (flows.length == 0 || price <= 0) {
            return 0;
        }

        double total = 0;
        for (double cf : flows) total += cf;

        // Paying more than every remaining payment put together is a negative
        // yield. Real, but off the bottom of the bracket, so it is reported as
        // zero rather than as a wrong number.
        if (price >= total) {
            return 0;
        }

        double low = 0;
        double high = 5.0;

        for (int i = 0; i < 80; i++) {
            double mid = (low + high) / 2;
            if (presentValue(flows, mid) > price) {
                low = mid;      // still too valuable: demand a higher yield
            } else {
                high = mid;
            }
        }
        return (low + high) / 2;
    }

    /** Yield at what the market would actually pay today. */
    public double getCurrentYield(double annualMarketRate) {
        return getYieldToMaturity(getMarketValue(annualMarketRate));
    }

    /**
     * Where this bond trades against par, as a percentage of face.
     *
     * 100 is par, above is a premium, below is a discount. The convention every
     * bond desk quotes in, and much easier to read across bonds of different
     * sizes than two dollar figures.
     */
    public double getPriceAsPercentOfPar(double annualMarketRate) {
        double owed = getOustandingPrincipal();
        if (owed <= 0) return 0;
        return getMarketValue(annualMarketRate) / owed * 100;
    }

    /**
     * The instrument in the new unit.
     *
     * FOREIGN PAPER DOES NOT MOVE, and that is the whole reason Debt knows what
     * currency it is in. A bond issued in US dollars owes US dollars whatever
     * the city calls its money this century; what changes is the exchange rate
     * it is translated at, and inLocal() reads that. Redenominating a foreign
     * face value would be defaulting on it by arithmetic.
     */
    public void redenominate(double scale) {
        if (!foreign) {
            faceValue *= scale;
            outstandingPrincipal *= scale;
            // Who holds it, and what is still owed for it, in the new unit;
            // the yield is a rate and does not move.
            householdPrincipal *= scale;
            centralBankPrincipal *= scale;
            firmPrincipal *= scale;
            worldPrincipal *= scale;
            settleDue *= scale;
            unsettledFace *= scale;
            allotHouseholdsFace *= scale;
            allotHouseholdsCash *= scale;
            allotFirmsFace *= scale;
            allotFirmsCash *= scale;
            allotWorldFace *= scale;
            allotWorldCash *= scale;
            issueDiscount *= scale;
            discountLeft *= scale;
            // The city's books (CD3): the carrying value and the month's booked figures, in the new unit
            // like the face; the effective rate is a rate and does not move.
            carryingValue *= scale;
            lastExpenseOwn *= scale;
            lastCouponOwn *= scale;
        }
        exchangeRate *= scale;
        redenominateSchedule(scale);
    }

    /**
     * Anything a subclass carries in its own currency - a coupon, an
     * amortisation schedule - in the new unit. Default: nothing to do.
     */
    protected void redenominateSchedule(double scale) { }

}
