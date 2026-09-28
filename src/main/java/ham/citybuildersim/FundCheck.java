package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Proves the city's fund, the bank's rescue for its shares, the preferred a standing bank asks for, and the Insane founding (0.7.14). Not part of the game.
 *
 * WHY THIS EXISTS. Until 0.7.14 the city rescued its bank with a gift - capital
 * for no shares, the owners keeping theirs, the hole "absorbed from outside"
 * by creditors who were the central bank's window - and could buy nothing of
 * its own city's companies. Jerus: "City takes the shares", "Treasury setting,
 * auto", "Preferred shares", "Rule plus your hand", "New issues only",
 * "Central bank advances it", a dial "default 0, max 300%" of "the year's
 * surplus", "Home only", and an Insane start: "0 cash, 0 vault, and a 20y bond
 * 3% for the initial land cost". Each is a rule with a source, and each is a
 * fixture here that causes the condition rather than waiting for a run to.
 *
 * What it has to prove, a section each:
 *
 *   1. A failed bank, automatic: resolved the month it fails - every share
 *      the city's, taken from the households, the world and the fund's own
 *      market book; the city paid the hole and the capital to reopen, from
 *      its cash first and the rest advanced by the central bank past its
 *      ceiling; the bank open again the same month; nothing from outside the
 *      city, the world's loss a valuation; the audit closes. Twelve banks
 *      frozen on holes in the trillions are each resolved once, and open.
 *   2. On the button: frozen, carrying its hole, until pressed - then the same.
 *   3. Dilution: the bank's new shares go to others, the city's stake falls,
 *      and the fund never takes a new issue (the book passes it over).
 *   4. The preferred, TARP's terms: sized within 1-3% of risk-weighted
 *      assets, 3% and the rest left to its own share issues when that does
 *      not reach its target; 5%, then 9% from the fifth anniversary; arrears
 *      block the common dividend, and nothing is paid under the target; no
 *      buybacks and no rise in the common dividend a share for three years;
 *      warrants struck at last month's price, exercised at expiry if still
 *      out and in the money; cancelled at a failure.
 *   5. Repaid at its third anniversary (Jerus: "Sell new shares to repay"):
 *      whole, at par with its unpaid dividends, from the bank's capital over
 *      its target and then an offering of new common to the public - none
 *      to the city, whose stake falls by exactly the dilution; under its
 *      target it is repaid all the same and its equity ends where it was;
 *      the warrants bought back at their Black-Scholes value after the last
 *      block; the equity's parts move by exactly those causes; an offering
 *      half taken up pays half and the rest the next month; through a save;
 *      a failed bank repays nothing.
 *   6. The offer: raised at the month's end, in the inbox; declined, asked
 *      again a quarter later; a pending offer survives a save.
 *   7. The dial: 0, 100% and 300% of a year's surplus; the one-month floor; a
 *      deficit year saves nothing; the surplus used once with the rollover,
 *      the dial's share first.
 *   8. The rule: 70/30, the 10% limit, the rebalancing band, cash that
 *      cannot be placed waits, and a holding over 10% is asked down to the
 *      limit and no further.
 *   9. The 3% transfer, from cash only.
 *  10. The hand: its orders at fair value; pay-in and draw-out off the
 *      surplus.
 *  11. Every piece round-trips through a save; an older save loads empty.
 *  12. Insane: nothing in the treasury or the vault; one dollar bond abroad at
 *      3% for twenty years at the land's value; a day-0 city is quoted both
 *      of the build screen's offers, and the build screen and the land office
 *      ask for funding at D$0; borrowed, it runs and its audit closes.
 *  13. Insane, never borrowing (0.7.15, Jerus: "Play runs on advances"): at
 *      day 0 its ceiling is nothing, so a purchase and a discretionary line
 *      are refused and a promise is paid past it; the time skip runs it a
 *      year, the audit closing every month; month one pays the land bond's
 *      coupon from nothing and month two's settle advances the whole
 *      shortfall; nothing discretionary is spent past the room it had; and
 *      what it owes the central bank at the end is printed.
 *
 * @author Jerus
 */
public class FundCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-96s FAIL  %,.6f != %,.6f%n", label, actual, expected);
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

    /** One month, pressed, and whether its audit closed: the playtest's own test (LongPlaytest.audit()). */
    static boolean play(Game g) {
        quietly(g::toggleNextMonth);
        MoneyAudit.Result r = g.getLastMoneyAudit();
        return Math.abs(r.residual) <= .01 || r.relative() <= 1e-7;
    }

    static GameFiles files;

    /** A copy of the fixture city, loaded from its save: every scenario starts from the same city. */
    static Game copy() {
        Game g = new Game(files);
        quietly(() -> g.loadGameSave(1));
        return g;
    }

    /** Takes the bank to `equity` by losing money between two presses - a loss nobody is paid for, which the next strike does not see. */
    static void takeEquityTo(Bank bank, double equity) {
        bank.setCash(bank.getCash() - (bank.equity() - equity));
    }

    public static void main(String[] args) {
        out = System.out;
        quiet = new PrintStream(OutputStream.nullOutputStream());

        /* ================= the city ================= */
        out.println("--- a city with a bank its households own ---");
        files = GameFiles.scratch("fundcheck");
        Game city = new Game(files);
        quietly(() -> {
            city.run();
            city.getForeignAccounts().pinRate(1.0);
            city.buildStack(template(city, "Gravel Road"), 6, true);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.simulateMonths(72);
            city.saveGame(1);
        });
        Bank b0 = city.getBank();
        out.printf("   m%d: %,.0f branch(es), equity $%,.0fk, %,.0f bank shares (households %,.0f, abroad %,.0f), treasury $%,.0fk%n",
                city.getMonth(), b0.getBranches(), b0.equity(), city.getEquity().getShares(Equity.BANK),
                city.getHouseholdBalance().sharesHeld(Equity.BANK), city.getEquity().getForeignShares(Equity.BANK),
                city.getCash());
        check("fixture: a standing bank, owned by somebody other than the city", b0.getBranches() >= 1 && !b0.isInsolvent()
                && city.getHouseholdBalance().sharesHeld(Equity.BANK) + city.getEquity().getForeignShares(Equity.BANK) > 0
                && city.getEquity().getCityShares(Equity.BANK) == 0);
        check("fixture: a constructor city resolves on the button and its fund's dial is 0",
                city.getRescueMode() == TreasuryFund.RescueMode.BUTTON && city.getFundDial() == 0);

        resolvedAutomatically();
        resolvedOnTheButton();
        thePreferred();
        theRepayment();
        theOffer();
        theDial();
        theRule();
        theTransfer();
        theHand();
        theSave();
        insane();
        insaneOnAdvances();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ================= 1. automatic ================= */

    static void resolvedAutomatically() {
        out.println("\n--- 1. a failed bank, automatic: resolved for its shares the month it fails ---");
        Game g = copy();
        g.setRescueMode(TreasuryFund.RescueMode.AUTOMATIC);
        Bank bank = g.getBank();
        CentralBank cb = g.getCentralBank();
        int failuresBefore = bank.getFailures();
        // A hole ten times what the central bank would advance for anything
        // but a promise, and a treasury holding a quarter of the bill.
        double hole = 10 * Math.max(1_000, cb.ceiling());
        takeEquityTo(bank, -hole);
        g.setCashForTest(.25 * (hole + bank.resolutionExitEquity()));
        check("fixture: the bank has lost more than it owns", bank.isInsolvent());
        boolean audited = play(g);
        TreasuryFund.Resolution r = g.getLastResolution();
        check("the month resolved it", r != null && r.month() == g.getMonth());
        if (r == null) return;
        out.printf("   paid $%,.0fk: hole $%,.0fk + capital $%,.0fk; $%,.0fk from cash, $%,.0fk advanced; took %,.0f shares"
                        + " (households %,.0f, abroad %,.0f, the fund's %,.0f); the old owners lost $%,.0fk%n",
                r.paid(), r.shortfall(), r.exitCapital(), r.fromCash(), r.advanced(), r.shares(),
                r.householdsShares(), r.worldShares(), r.fundShares(), r.ownersLost());
        close("the city paid the hole the bank failed with and the capital to reopen", r.paid(),
                r.shortfall() + r.exitCapital(), 1e-6);
        close("...the hole as the bank recorded it failing", r.shortfall(), bank.getShortfallThisMonth(), 1e-6);
        check("...its cash first, and the rest for the central bank to advance",
                r.fromCash() > 0 && r.advanced() > 0 && Math.abs(r.fromCash() + r.advanced() - r.paid()) < 1e-6);
        close("every share passed to the city: the households', the world's and the fund's own",
                r.householdsShares() + r.worldShares() + r.fundShares(), r.shares(), 1e-6 * Math.max(1, r.shares()));
        close("...and it holds every one it took, in its rescue book",
                g.getEquity().getCityRescueShares(Equity.BANK), r.shares(), 1e-6 * Math.max(1, r.shares()));
        check("the bank failed once and is open again the same month", bank.getFailures() == failuresBefore + 1
                && !bank.isInsolvent() && !bank.lendsOnlyToKeepBorrowersGoing());
        close("nothing arrived from outside the city: no creditor absorbed a hole", bank.getResolutionLossThisMonth(), 0, 0);
        check("the audit closed on the month of the resolution", audited);
        close("...and declared the world's loss as a valuation, at its last price",
                g.getLastMoneyAudit().valuationIn - g.getBondMarket().getWorldWrittenOff()
                        - bank.getFoundingSettlement() - g.getEconomyManager().getOverdraftForgiven()
                        - bank.getResolutionLossThisMonth(), r.worldValue(), 1e-6);
        check("the treasury's journal says what the city put in",
                journalHas(g, "Resolved the bank for its shares", -r.paid()));
        // ...and the rest advanced at the next settle, past the ceiling.
        boolean audited2 = play(g);
        check("the next month's settle advanced the treasury past the central bank's ceiling",
                cb.getAdvancedToTreasury() > 0 && cb.getAdvancesToTreasury() > cb.ceiling());
        check("...and its audit closed too", audited2);
        check("the inbox said what happened, and it is settled the month after",
                g.getInbox().all().stream().anyMatch(n -> n.getKey().equals("resolved")));

        // A hole in the trillions (round 2): the rounding of the payment is bigger than a billionth,
        // and a resolution paid to exactly the exit level must still leave the bank open, paid once.
        boolean rounded = false, open = true, frozen = true;
        for (int k = 0; k < 12; k++) {
            Game t = copy();
            Bank tb = t.getBank();
            takeEquityTo(tb, -(1.7 + .37 * k) * 1e12);
            tb.resolveIfFailed();
            frozen &= tb.isInResolution();
            quietly(() -> t.resolveBank());
            rounded |= Math.abs(tb.equity() - tb.resolutionExitEquity()) > 1e-9;
            open &= !tb.isInsolvent() && !t.canResolveBank() && t.getFund().getResolutions().size() == 1;
        }
        check("fixture: twelve banks frozen on holes in the trillions", frozen);
        check("fixture: ...and after the resolution, equity misses the exit level by more than a billionth", rounded);
        check("...and the resolution still leaves the bank open, paid for once", open);
    }

    static boolean journalHas(Game g, String label, double amount) {
        for (TreasuryJournal.Entry e : g.getTreasuryJournal()) {
            if (e.label().equals(label) && Math.abs(e.amount() - amount) < 1e-6) return true;
        }
        return false;
    }

    /* ================= 2. the button, and 3. dilution ================= */

    static void resolvedOnTheButton() {
        out.println("\n--- 2. on the button: frozen, carrying its hole, until the player presses ---");
        Game g = copy();
        Bank bank = g.getBank();
        Equity register = g.getEquity();
        check("fixture: the setting is the button", g.getRescueMode() == TreasuryFund.RescueMode.BUTTON);
        takeEquityTo(bank, -5_000);
        double households = g.getHouseholdBalance().sharesHeld(Equity.BANK);
        boolean audited = play(g);
        check("a month on, it is frozen and nobody has resolved it", bank.isInsolvent() && bank.isInResolution()
                && bank.lendsOnlyToKeepBorrowersGoing() && g.getFund().getResolutions().isEmpty());
        check("...its hole carried, not absorbed: its equity is still under nothing", bank.equity() < 0
                && bank.getResolutionLossThisMonth() == 0);
        check("...the owners still own it", g.getHouseholdBalance().sharesHeld(Equity.BANK)
                + g.getEquity().getForeignShares(Equity.BANK) > 0 && g.getEquity().getCityShares(Equity.BANK) == 0);
        check("...and the month's audit closed", audited);
        int failures = bank.getFailures();
        play(g);
        check("another month: still frozen, the failure counted once", bank.isInResolution() && bank.getFailures() == failures);

        double needed = g.bankRecapitalisationNeeded();
        double shares = register.getShares(Equity.BANK);
        double paid = g.resolveBank();
        out.printf("   pressed at m%d: paid $%,.0fk for %,.0f shares; the households had %,.0f of them%n",
                g.getMonth(), paid, shares, households);
        close("pressed: the city paid the hole and the capital to reopen", paid, needed, 1e-6);
        check("the old owners hold nothing: no household, nobody abroad",
                g.getHouseholdBalance().sharesHeld(Equity.BANK) == 0 && register.getForeignShares(Equity.BANK) == 0);
        close("the city holds every share, the count in issue unmoved", register.getCityShares(Equity.BANK), shares, 1e-9);
        check("...and the bank is open again", !bank.isInsolvent());
        check("the next month's audit closes", play(g));

        out.println("\n--- 3. dilution: the bank's new shares go to others, and the city's stake falls ---");
        double cityHeld = register.getCityShares(Equity.BANK);
        double before = register.getShares(Equity.BANK);
        close("fixture: the city holds all of it", g.cityStakeInBank(), 1, 1e-12);
        // A second branch: its capital is sold as new shares, households first
        // and the world for the rest (Game.capitaliseBank()) - an issue. The
        // households are handed savings to buy with: a young city's have none
        // past their cushion, and the world will not buy a bank that has just
        // failed. Outside the audited pools, so the month's audit is untouched.
        for (Household cell : g.getHouseholdBalance().cellsForMarket()) {
            if (!cell.isEmpty()) cell.savings += 1_000;
        }
        g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 1_000_000L);
        final Game.BuildResult[] built = new Game.BuildResult[1];
        quietly(() -> built[0] = g.buildStack(g.getBuildingManager().getTemplateByName("Commercial Bank"), 1, true));
        check("fixture: a second branch stands", built[0] == Game.BuildResult.SUCCESS);
        play(g);
        double after = register.getShares(Equity.BANK);
        out.printf("   a branch opened: %,.0f shares -> %,.0f; the city's %,.0f, its stake %.1f%%%n",
                before, after, register.getCityShares(Equity.BANK), g.cityStakeInBank() * 100);
        check("fixture: the bank sold new shares", after > before);
        close("the city bought none of them: its shares are what it took", register.getCityShares(Equity.BANK), cityHeld, 1e-9);
        check("...so its stake fell", g.cityStakeInBank() < 1 - 1e-9);
        close("...to what it holds over what is in issue", g.cityStakeInBank(), cityHeld / after, 1e-12);

        // THE BOOK PASSES A NEW ISSUE OVER FOR THE FUND: the desk's ask in the
        // bank's own shares is an issue, and the clearing refuses it to the
        // city (Exchange.Settle.mayTrade()) - the book's own mechanism, on a
        // book of its own, and the same refusal the exchange's clearing makes.
        OrderBook book = new OrderBook("Bank");
        OrderBook.Clearing refusesTheCity = new OrderBook.Clearing() {
            @Override public double capacity(String who, OrderBook.Side side, double price) { return 1_000; }
            @Override public void settle(String buyer, String seller, double q, double price) { }
            @Override public boolean mayTrade(String buyer, String seller) {
                return !(Exchange.isFund(buyer) && Exchange.DESK.equals(seller));
            }
        };
        book.submit(Exchange.DESK, OrderBook.Side.SELL, 1.0, 10, 1, refusesTheCity);
        check("the fund's bid over the desk's new-issue ask does not fill",
                book.submit(Exchange.FUND, OrderBook.Side.BUY, 1.1, 5, 1, refusesTheCity, false).isEmpty());
        check("...by rule or by hand", book.submit(Exchange.FUND_HAND, OrderBook.Side.BUY, 1.1, 5, 1, refusesTheCity, false).isEmpty());
        check("...and the ask keeps its place for a household's bid, which does",
                book.submit(Exchange.CELL + "x", OrderBook.Side.BUY, 1.1, 5, 1, refusesTheCity, false).size() == 1);
    }

    /* ================= 4. the preferred ================= */

    static void thePreferred() {
        out.println("\n--- 4. the preferred: TARP's terms ---");
        Game g = copy();
        Bank bank = g.getBank();
        takeEquityTo(bank, .95 * bank.minimumEquity());
        check("fixture: a standing bank under its minimum", bank.wantsPreferred());
        double rwa = bank.getWeightedBook();
        double size = bank.preferredOfferSize();
        double need = bank.recapitalisationNeeded();
        out.printf("   it asks $%,.0fk against $%,.0fk to its target, on $%,.0fk of risk-weighted assets (%.2f%%)%n",
                size, need, rwa, size / rwa * 100);
        check("its size is at least 1% of its risk-weighted assets and at most 3%",
                size >= Bank.PREFERRED_MIN_SHARE * rwa - 1e-9 && size <= Bank.PREFERRED_MAX_SHARE * rwa + 1e-9);
        close("...what takes it back to its target, inside that band", size,
                Math.max(Bank.PREFERRED_MIN_SHARE * rwa, Math.min(Bank.PREFERRED_MAX_SHARE * rwa, need)), 1e-9);
        // Deeper under: 3% does not reach the target, and it says so.
        double keep = bank.getCash();
        takeEquityTo(bank, .2 * bank.minimumEquity());
        check("deeper under, it asks for 3% and says the rest is its own share issues'",
                bank.preferredOfferCapped() && Math.abs(bank.preferredOfferSize() - Bank.PREFERRED_MAX_SHARE * rwa) < 1e-9
                        && bank.preferredOfferShortOfTarget() > 0);
        bank.setCash(keep);

        // Accepted, between two presses: the treasury pays, a purchase.
        quietly(() -> g.getFund().noteOffered(g.getMonth()));
        g.setCashForTest(size * 3);
        double price = g.getExchange().price(Equity.BANK);
        double cashBefore = g.getCash();
        check("accepted", g.acceptPreferredOffer());
        Bank.Preferred p = bank.getPreferred().get(0);
        close("the treasury paid its par, a purchase", cashBefore - g.getCash(), size, 1e-9);
        close("...and it is an equity line of its own, the city's", bank.preferredOutstanding(), size, 1e-9);
        close("its warrants are struck at last month's price", p.strike(), price, 1e-12);
        close("...on shares worth 15% of the preferred at it", p.warrantShares() * price, Bank.WARRANT_SHARE * size, 1e-9);
        check("...for ten years", p.warrantsExpire() - p.issued() == Bank.WARRANT_TERM_MONTHS);
        close("its dividend is 5% a year until the fifth anniversary", p.rate(p.issued() + Bank.PREFERRED_STEP_MONTHS - 1),
                Bank.PREFERRED_RATE, 0);
        close("...and 9% from it", p.rate(p.issued() + Bank.PREFERRED_STEP_MONTHS), Bank.PREFERRED_STEP_RATE, 0);

        // Arrears: a month under its target pays nothing and owes it - the bank's
        // own rule for a distribution, "under its target it keeps everything".
        int m0 = p.issued();
        bank.setMonth(m0 + 1);
        check("fixture: its target is over its minimum", bank.targetEquity() > bank.minimumEquity());
        takeEquityTo(bank, .5 * (bank.minimumEquity() + bank.targetEquity()));
        double paid = bank.payPreferredDividends();
        close("a month over its minimum but under its target pays the preferred nothing", paid, 0, 0);
        close("...and owes a twelfth of 5% of its par", bank.getPreferredArrears(), size * Bank.PREFERRED_RATE / 12, 1e-9);
        takeEquityTo(bank, 2 * bank.targetEquity());
        check("with a dividend unpaid, the common gets nothing however much it holds", bank.dividendDue(1_000_000) == 0);
        bank.setMonth(m0 + 2);
        paid = bank.payPreferredDividends();
        close("the next month pays the arrears and the month's, cumulative", paid, 2 * size * Bank.PREFERRED_RATE / 12, 1e-9);
        check("...and the common is paid again", bank.dividendDue(1_000) > 0);

        // Consent: three years, no buyback and no rise in the dividend a share.
        bank.setMonth(m0 + Bank.PREFERRED_CONSENT_MONTHS - 1);
        check("over its target but in its third year, it buys no share back", !bank.buysBackOwnShares());
        double shares = g.getEquity().getShares(Equity.BANK);
        double capped = bank.payOwners(1_000_000, shares);
        close("...and pays its common no more a share than the year before the city bought in",
                capped, Math.min(bank.dividendDue(1_000_000) + capped, p.capPerShare() * shares), 1e-6);
        bank.setMonth(m0 + Bank.PREFERRED_CONSENT_MONTHS);
        check("from the third anniversary it may", bank.buysBackOwnShares());

        // Warrants at expiry, unredeemed: a cashless exercise.
        Bank e = new Bank();
        e.injectCapital(80_000);
        e.refresh(1, 10_000_000, 0, 200_000, 0, 0);
        e.setMonth(10);
        double n = e.issuePreferred(1_000, 2.0, 0);
        e.setMonth(10 + Bank.WARRANT_TERM_MONTHS);
        close("at expiry in the money, the city takes shares worth what they are over the strike",
                e.exerciseExpiredWarrants(4.0), n * (4.0 - 2.0) / 4.0, 1e-9);
        check("...and the preferred stays", e.preferredOutstanding() == 1_000);

        // Cancelled at a failure.
        takeEquityTo(bank, -1_000);
        double par = bank.preferredOutstanding();
        // (The fixture's own losses, set by hand, are unexplained by any cause:
        // what is asserted is that the resolution routes every one of its own.)
        double splitBefore = bank.equitySplitResidual();
        g.resolveBank();
        check("a failure cancels the preferred and its warrants: the hole took them", bank.getPreferred().isEmpty()
                && Math.abs(g.getLastResolution().preferredCancelled() - par) < 1e-9);
        close("...and the equity's parts move by exactly the resolution's causes: the preferred to retained,"
                + " the old paid-in written off, the city's paid in", bank.equitySplitResidual(), splitBefore, 1e-6);
        close("...the old owners' paid-in gone: paid in is what the city paid", bank.paidInCapital(),
                g.getLastResolution().paid(), 1e-6);
    }

    /* ================= 5. the repayment ================= */

    /**
     * THE PREFERRED REPAID AT ITS THIRD ANNIVERSARY (Jerus: "Sell new shares
     * to repay"): whole, at par with its unpaid dividends, from the bank's
     * capital over its target and then an offering of new common to the
     * public; the warrants after the last block, at their fair value.
     */
    static void theRepayment() {
        out.println("\n--- 5. repaid at its third anniversary: its capital over target, then new shares to the public ---");

        // (a) Out of its capital over its target, with a month's dividend owed, and the warrants after it.
        Game g = copy();
        Bank bank = g.getBank();
        int m = g.getMonth();
        double price = g.getExchange().price(Equity.BANK);
        double par = 1_000;
        bank.setMonth(m - Bank.PREFERRED_REDEEM_MONTHS);
        bank.issuePreferred(par, price, 0);
        bank.setMonth(m - 1);
        takeEquityTo(bank, bank.targetEquity());
        bank.payPreferredDividends();
        double arrears = bank.getPreferredArrears();
        bank.setMonth(m);
        check("fixture: a block three years old, a month's dividend on it unpaid",
                arrears > 0 && bank.getPreferred().get(0).due(m) && !bank.getPreferred().get(0).due(m - 1));
        double warrants = bank.warrantValue(price, g.bankVolatility(), g.getDebtManager().getPolicyRate());
        takeEquityTo(bank, bank.targetEquity() + 2 * (par + arrears + warrants));
        double equityBefore = bank.equity(), sharesBefore = g.getEquity().getShares(Equity.BANK);
        double fundBefore = g.getFund().getCash(), split = bank.equitySplitResidual();
        g.settleThePreferred();
        check("at its third anniversary a block is redeemed whole", bank.preferredOutstanding() == 0);
        close("...at par with its unpaid dividends, to the city's fund",
                g.getFund().getCash() - fundBefore - warrants, par + arrears, 1e-9);
        check("...out of its capital over its target: nothing raised, no new share",
                bank.getRepaymentRaisedThisMonth() == 0 && g.getEquity().getShares(Equity.BANK) == sharesBefore);
        check("its unpaid dividends paid with it, as a dividend", bank.getPreferredArrears() == 0
                && Math.abs(bank.getPreferredDividendsThisMonth() - arrears) < 1e-9);
        check("...and the city's consent lifts with it", !bank.inConsentPeriod());
        check("fixture: its warrants were worth something", warrants > 0);
        close("with no preferred left, the warrants are bought back at their fair value",
                bank.getWarrantsBoughtBackThisMonth(), warrants, 1e-9);
        check("...and nothing is left out", bank.warrantSharesOut() == 0 && bank.getPreferred().isEmpty());
        close("the bank's equity fell by exactly what it paid", equityBefore - bank.equity(), par + arrears + warrants, 1e-6);
        close("...and its three parts moved by exactly those causes: par off the preferred, the dividend out of"
                + " retained, the warrants off paid-in", bank.equitySplitResidual(), split, 1e-6);

        // (b) Nothing over its target: an offering to the public for all of it, and the city's stake diluted.
        Game h = copy();
        Bank hb = h.getBank();
        Equity reg = h.getEquity();
        int hm = h.getMonth();
        double hprice = h.getExchange().price(Equity.BANK);
        hb.setMonth(hm - Bank.PREFERRED_REDEEM_MONTHS);
        hb.issuePreferred(par, hprice, 0);
        hb.setMonth(hm);
        // The city holds a fifth of the common (a fixture of a holding), and the households have
        // savings past their cushion to buy with - outside the audited pools, between presses.
        reg.issueToCityRescue(Equity.BANK, .25 * reg.getShares(Equity.BANK));
        for (Household cell : h.getHouseholdBalance().cellsForMarket()) {
            if (!cell.isEmpty()) cell.savings += 1_000;
        }
        takeEquityTo(hb, hb.targetEquity());
        double hWarrants = hb.warrantValue(h.getExchange().price(Equity.BANK), h.bankVolatility(),
                h.getDebtManager().getPolicyRate());
        double s0 = reg.getShares(Equity.BANK), city0 = reg.getCityShares(Equity.BANK);
        double abroad0 = reg.getForeignShares(Equity.BANK), home0 = h.getHouseholdBalance().sharesHeld(Equity.BANK);
        double hFund = h.getFund().getCash(), hSplit = hb.equitySplitResidual();
        h.settleThePreferred();
        double raised = hb.getRepaymentRaisedThisMonth();
        double s1 = reg.getShares(Equity.BANK);
        out.printf("   no spare capital: raised $%,.0fk in new shares for $%,.0fk of preferred and $%,.0fk of warrants;"
                        + " shares %,.1f -> %,.1f, the city's stake %.1f%% -> %.1f%%%n",
                raised, par, hWarrants, s0, s1, city0 / s0 * 100, h.cityStakeInBank() * 100);
        check("with nothing over its target, it is still redeemed whole", hb.preferredOutstanding() == 0);
        close("...by an offering sized to what it owes: the par, then the warrants' value", raised, par + hWarrants, 1e-6);
        close("...whose shares went to the households and the world",
                (h.getHouseholdBalance().sharesHeld(Equity.BANK) - home0) + (reg.getForeignShares(Equity.BANK) - abroad0),
                s1 - s0, 1e-9);
        check("...none to the city", reg.getCityShares(Equity.BANK) == city0 && s1 > s0);
        close("so the city's stake fell by exactly the dilution", h.cityStakeInBank(), city0 / s1, 1e-12);
        close("the fund was paid the par and the warrants", h.getFund().getCash() - hFund, par + hWarrants, 1e-6);
        close("the bank ends at its target, where it began", hb.equity(), hb.targetEquity(), 1e-6);
        close("...its three parts still footing: the new common paid in, the par off the preferred",
                hb.equitySplitResidual(), hSplit, 1e-6);

        // (b2) Under its target at the anniversary: the offering pays the whole of it and the bank
        // ends where it was - the repayment spends only what is over the target, and the offering
        // is not a recapitalisation.
        Game w = copy();
        Bank wb = w.getBank();
        int wm = w.getMonth();
        wb.setMonth(wm - Bank.PREFERRED_REDEEM_MONTHS);
        wb.issuePreferred(par, w.getExchange().price(Equity.BANK), 0);
        wb.setMonth(wm);
        for (Household cell : w.getHouseholdBalance().cellsForMarket()) {
            if (!cell.isEmpty()) cell.savings += 1_000;
        }
        takeEquityTo(wb, .8 * wb.targetEquity());
        double wWarrants = wb.warrantValue(w.getExchange().price(Equity.BANK), w.bankVolatility(),
                w.getDebtManager().getPolicyRate());
        double wEquity = wb.equity();
        w.settleThePreferred();
        check("under its target, it is redeemed whole all the same", wb.preferredOutstanding() == 0);
        close("...the offering raising exactly what it pays", wb.getRepaymentRaisedThisMonth(), par + wWarrants, 1e-6);
        close("...and its equity where it was, still under its target", wb.equity(), wEquity, 1e-6);

        // (c) Played, with a save in the middle: the month repays it, raises what its capital does not
        // cover, and its audit closes; a reload does the same month the same way.
        Game p = copy();
        Bank pb = p.getBank();
        int pm = p.getMonth();
        double big = .1 * pb.equity();
        pb.setMonth(pm + 1 - Bank.PREFERRED_REDEEM_MONTHS);
        pb.issuePreferred(big, p.getExchange().price(Equity.BANK), 0);
        pb.setMonth(pm);
        for (Household cell : p.getHouseholdBalance().cellsForMarket()) {
            if (!cell.isEmpty()) cell.savings += 1_000;
        }
        takeEquityTo(pb, pb.targetEquity());
        quietly(() -> p.saveGame(4));
        Game q = new Game(files);
        quietly(() -> q.loadGameSave(4));
        close("a save before the anniversary carries the block", q.getBank().preferredOutstanding(), big, 1e-9);
        check("played: the month it comes due, its audit closes", play(p));
        check("...and the preferred is repaid", pb.preferredOutstanding() == 0 && pb.getPreferredRedeemedThisMonth() > 0);
        check("...partly with new shares sold that month", pb.getRepaymentRaisedThisMonth() > 0);
        check("the reloaded city's month closes too", play(q));
        close("...and repays the same", q.getBank().getPreferredRedeemedThisMonth(), pb.getPreferredRedeemedThisMonth(), 1e-9);
        close("...raising the same", q.getBank().getRepaymentRaisedThisMonth(), pb.getRepaymentRaisedThisMonth(), 1e-9);
        close("...to the same shares in issue", q.getEquity().getShares(Equity.BANK), p.getEquity().getShares(Equity.BANK), 1e-9);
        close("...and the same fund", q.getFund().getCash(), p.getFund().getCash(), 1e-6);

        // (d) An offering the public takes only half of pays half - the dividends first - and the block
        // stays due; the warrants' likewise buys back half of them; a bank that is not standing makes no
        // offering at all.
        Bank u = new Bank();
        u.injectCapital(80_000);
        u.refresh(1, 10_000_000, 0, 200_000, 0, 0);
        u.setMonth(100);
        u.issuePreferred(1_000, 2.0, 0);
        u.setMonth(100 + Bank.PREFERRED_REDEEM_MONTHS);
        takeEquityTo(u, u.targetEquity());
        double[] half = u.redeemDuePreferred(x -> { u.injectCapital(0, x / 2); return x / 2; });
        close("an offering half taken up repays half", half[1], 500, 1e-9);
        check("...and the rest stays due, to be repaid the next month", Math.abs(u.preferredOutstanding() - 500) < 1e-9
                && u.getPreferred().get(0).due(100 + Bank.PREFERRED_REDEEM_MONTHS));
        close("...when it is", u.redeemDuePreferred(x -> { u.injectCapital(0, x); return x; })[1], 500, 1e-9);
        // ...and so are the warrants: an offering half taken up buys back half of them, every block's
        // alike, and pays out all it raised - none of it is kept as the bank's capital.
        double wAll = u.warrantValue(3, .3, .03), wShares = u.warrantSharesOut(), uEquity = u.equity();
        check("fixture: with the preferred gone its warrants are worth something, and nothing is over its target",
                u.preferredOutstanding() == 0 && wAll > 0 && !(uEquity > u.targetEquity() + 1e-9));
        int shortBefore = u.getRepaymentShortLifetime();
        double wHalf = u.repurchaseWarrants(3, .3, .03, x -> { u.injectCapital(0, x / 2); return x / 2; });
        close("an offering half taken up for the warrants buys back half their value", wHalf, wAll / 2, 1e-9);
        close("...half of every block's warrant shares", u.warrantSharesOut(), wShares / 2, 1e-9);
        close("...and pays out all it raised: the bank's equity where it was", u.equity(), uEquity, 1e-9);
        check("...counted as a repayment left part-paid", u.getRepaymentShortLifetime() == shortBefore + 1);
        close("the rest the next month", u.repurchaseWarrants(3, .3, .03, x -> { u.injectCapital(0, x); return x; }),
                wAll / 2, 1e-9);
        check("...and none is left out", u.warrantSharesOut() == 0 && u.getPreferred().isEmpty());
        Bank f = new Bank();
        f.injectCapital(80_000);
        f.refresh(1, 10_000_000, 0, 200_000, 0, 0);
        f.setMonth(100);
        f.issuePreferred(1_000, 2.0, 0);
        f.setMonth(100 + Bank.PREFERRED_REDEEM_MONTHS);
        takeEquityTo(f, -500);
        f.resolveIfFailed();
        final boolean[] asked = new boolean[1];
        double[] frozen = f.redeemDuePreferred(x -> { asked[0] = true; return x; });
        check("a failed bank repays nothing and offers no share", frozen[1] == 0 && !asked[0]
                && f.preferredOutstanding() == 1_000);
    }

    /* ================= 6. the offer ================= */

    static void theOffer() {
        out.println("\n--- 6. the offer: in the inbox, a quarter after it is declined, and through a save ---");
        Game g = copy();
        Bank bank = g.getBank();
        takeEquityTo(bank, .5 * bank.minimumEquity());
        play(g);
        check("a standing bank under its minimum asks, at the month's end", g.isPreferredOfferPending()
                && g.getFund().getOfferMonth() == g.getMonth());
        check("...and the inbox has it, the popup Jerus asked for", g.getInbox().live("preferred") != null);
        g.declinePreferredOffer();
        int declined = g.getMonth();
        play(g);
        play(g);
        check("declined, it does not ask for two months", !g.isPreferredOfferPending());
        takeEquityTo(bank, .5 * bank.minimumEquity());
        play(g);
        check("...and asks again a quarter after", g.isPreferredOfferPending() && g.getMonth() - declined == TreasuryFund.OFFER_AGAIN_MONTHS);
        quietly(() -> g.saveGame(2));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(2));
        check("a pending offer survives a save, its month and the refusal before it",
                back.isPreferredOfferPending() && back.getFund().getOfferMonth() == g.getFund().getOfferMonth()
                        && back.getFund().getDeclinedMonth() == declined);
    }

    /* ================= 7. the dial ================= */

    static void theDial() {
        out.println("\n--- 7. the dial: a share of the year's surplus, the floor, and the surplus used once ---");
        double[] zero = TreasuryFund.payIn(0, 1_000, 1_000, 10_000, 100);
        double[] whole = TreasuryFund.payIn(1, 1_000, 1_000, 10_000, 100);
        double[] triple = TreasuryFund.payIn(TreasuryFund.MAX_DIAL, 1_000, 1_000, 10_000, 100);
        check("at 0 it takes nothing", zero[0] == 0 && zero[1] == 0);
        check("at 100% it takes the year's surplus, all from the surplus", whole[0] == 1_000 && whole[1] == 0);
        check("at 300% three times it, the extra from the treasury's cash", triple[0] == 1_000 && triple[1] == 2_000);
        double[] floored = TreasuryFund.payIn(TreasuryFund.MAX_DIAL, 1_000, 1_000, 1_800, 100);
        close("never taking the treasury under a month of its spending", floored[0] + floored[1], 1_700, 1e-9);
        check("...the surplus's part first", floored[0] == 1_000);
        double[] deficit = TreasuryFund.payIn(TreasuryFund.MAX_DIAL, -500, 0, 10_000, 100);
        check("a deficit year saves nothing", deficit[0] == 0 && deficit[1] == 0);
        double[] used = TreasuryFund.payIn(1, 1_000, 300, 10_000, 100);
        close("...and what the rollover already netted of it is not taken again", used[0], 300, 1e-9);
        close("through the year the rollover leaves the dial's share of the year so far",
                TreasuryFund.reservedFor(.5, 800), 400, 1e-9);
        check("...nothing at a dial of 0, which is 0.7.13's rollover", TreasuryFund.reservedFor(0, 800) == 0);

        // In a city: December closes, the next press pays in before the rollover.
        Game g = copy();
        g.setFundDial(1);
        int guard = 0;
        while (CityCalendar.monthOfYear(g.getMonth()) != 12 && guard++ < 24) play(g);
        double year = g.surplusOverLastYear();
        double unused = year - g.getRollover().usedInYear(g.getMonth());
        double floor = g.monthOfSpending();
        double cash = g.getCash();
        double[] expect = TreasuryFund.payIn(1, year, unused, cash, floor);
        int yearEnd = g.getMonth();
        out.printf("   m%d, December: the year's surplus $%,.0fk, a month's spending $%,.0fk, the treasury $%,.0fk%n",
                yearEnd, year, floor, cash);
        play(g);
        TreasuryFund f = g.getFund();
        check("the press after December closed paid the dial's share in", f.getLastPayInMonth() == yearEnd);
        close("...what the rule says, from the surplus", f.getLastPayInFromSurplus(), expect[0], 1e-6);
        check("...entered on the rollover's ledger, so the rollover cannot net it again",
                expect[0] <= 0 || g.getRollover().usedInYear(yearEnd + 1) >= expect[0] - 1e-6);
    }

    /* ================= 8. the rule ================= */

    static void theRule() {
        out.println("\n--- 8. the rule: 70/30, the 10% limit, the band, and cash that waits ---");
        Game g = copy();
        Equity register = g.getEquity();
        double lump = 1_000_000;
        g.setCashForTest(g.getCash() + lump);
        g.fundPayIn(lump);
        play(g);
        TreasuryFund f = g.getFund();
        Exchange ex = g.getExchange();
        double shares = g.fundMarketSharesValue(), bonds = g.fundBondsValue(), cash = f.getCash();
        double v = shares + bonds + cash;
        double bidShares = 0;
        boolean capped = true;
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            double resting = ex.bookOf(c).resting(Exchange.FUND, OrderBook.Side.BUY);
            bidShares += resting * ex.fair(c);
            capped &= register.getCityMarketShares(c) + resting
                    <= TreasuryFund.OWNERSHIP_LIMIT * register.getShares(c) + 1e-6;
        }
        out.printf("   after a step: shares $%,.0fk held and $%,.0fk bid, bonds $%,.0fk, cash $%,.0fk%n",
                shares, bidShares, bonds, cash);
        check("it bids for shares up to 70% of its market book and cash, and no further",
                shares + bidShares <= TreasuryFund.EQUITY_WEIGHT * v + 1e-6 * v);
        check("...never for more than 10% of any company", capped);
        check("what does not fit waits as its cash", cash > 0);
        check("...and nothing was lost in the placing: at the marks it is what was paid in, less its transfer",
                v + f.getTransferPaid() >= lump * (1 - 1e-9));

        // The band: the fund given its market book in shares and no cash.
        Game h = copy();
        Equity reg = h.getEquity();
        int company = -1;
        for (int c = 0; c < Sectors.KEYS.length; c++) {
            if (reg.getShares(c) > 0 && h.getExchange().fair(c) > 0
                    && (company < 0 || reg.getShares(c) > reg.getShares(company))) company = c;
        }
        double take = .05 * reg.getShares(company);
        double households = h.getHouseholdBalance().sharesHeld(company);
        check("fixture: a listed company whose households can hand the fund 5% of it", households >= take);
        // The households hand it over whole-cell by share: the register's
        // identity kept, no money moved - a fixture of a holding, not a trade.
        double k = 1 - take / households;
        for (Household cell : h.getHouseholdBalance().cellsForMarket()) cell.shares[company] *= k;
        reg.moveCity(company, take);
        play(h);
        double left = reg.getCityMarketShares(company);
        double asked = h.getExchange().bookOf(company).resting(Exchange.FUND, OrderBook.Side.SELL);
        out.printf("   the fund held %,.0f shares and nothing else; after the step %,.0f, and %,.0f asked%n",
                take, left, asked);
        check("over 74% in shares, it asks for the excess", asked > 0 || left < take);
        final int listed = company;
        check("...at fair value", h.getExchange().bookOf(listed).asks().stream()
                .filter(o -> o.who().equals(Exchange.FUND)).allMatch(o -> Math.abs(o.price() - h.getExchange().fair(listed)) < 1e-12));
        // The band's two triggers, exactly: GPFG's 74% and four points under 70%.
        close("the excess is what takes it back to 70%", TreasuryFund.sharesOver(80, 10, 10), 80 - 70, 1e-12);
        check("...and inside the band nothing is sold", TreasuryFund.sharesOver(74, 16, 10) == 0
                && TreasuryFund.bondsOver(66, 24, 10) == 0);
        close("four points under 70%, it sells the bonds over their 30%", TreasuryFund.bondsOver(60, 40, 0), 10, 1e-12);
        check("...but a fund short of shares only for want of a seller sells nothing", TreasuryFund.bondsOver(10, 0, 90) == 0);

        // Over the limit without buying - a company's buyback retires shares under the fund -
        // with its mix inside the band: it asks the excess over 10%, and only that.
        Game o = copy();
        Equity ro = o.getEquity();
        double over = .15 * ro.getShares(company);
        double theirs = o.getHouseholdBalance().sharesHeld(company);
        check("fixture: the households can hand the fund 15% of the company", theirs >= over);
        double ko = 1 - over / theirs;
        for (Household cell : o.getHouseholdBalance().cellsForMarket()) cell.shares[company] *= ko;
        ro.moveCity(company, over);
        double lump2 = 4 * over * o.getExchange().fair(company);
        o.setCashForTest(o.getCash() + lump2);
        o.fundPayIn(lump2);
        check("fixture: its shares are inside the band, so it has no mix to sell for",
                TreasuryFund.sharesOver(over * o.getExchange().price(company), 0, lump2) == 0);
        play(o);
        double leftO = ro.getCityMarketShares(company);
        double askedO = o.getExchange().bookOf(company).resting(Exchange.FUND, OrderBook.Side.SELL);
        out.printf("   the fund held 15%% of %s; after the step %.1f%%, and %.1f%% of it asked%n", Equity.COMPANIES[company],
                leftO / ro.getShares(company) * 100, askedO / ro.getShares(company) * 100);
        check("over 10% of a company, it asks what is over the limit at fair value", askedO > 0 || leftO < over);
        close("...down to the limit and no further", leftO - askedO, TreasuryFund.OWNERSHIP_LIMIT * ro.getShares(company),
                1e-6 * ro.getShares(company));
    }

    /* ================= 9. the transfer ================= */

    static void theTransfer() {
        out.println("\n--- 9. the 3% transfer: a twelfth a month, from its cash only ---");
        Game g = copy();
        g.setCashForTest(g.getCash() + 10_000);
        g.fundPayIn(10_000);
        double value = g.fundValue();
        double due = TreasuryFund.transferOn(value);
        double treasury = g.getCash();
        play(g);
        TreasuryFund f = g.getFund();
        close("a twelfth of 3% of all it is worth", f.getTransferDue(), due, 1e-9);
        close("...paid from its cash", f.getTransferPaid(), due, 1e-9);
        close("...a revenue line in the budget", g.getEconomyManager().getNationalAccounts().getFundTransfer(), due, 1e-9);
        // A fund with nothing in cash pays nothing: its rule never sells to pay.
        TreasuryFund bare = new TreasuryFund();
        close("a fund worth something with no cash pays nothing", bare.payTransfer(1_000_000, 2000), 0, 0);
        close("...and says so", bare.getTransferShort(), TreasuryFund.transferOn(1_000_000), 1e-12);
        bare.receive(100);
        close("with some cash, what it has", bare.payTransfer(1_000_000, 2000), 100, 1e-12);
        check("fixture: the treasury was not what paid it", treasury > 0);
    }

    /* ================= 10. the hand ================= */

    static void theHand() {
        out.println("\n--- 10. the hand: its orders at fair value, and pay-in and draw-out off the surplus ---");
        Game a = copy(), b = copy();
        double x = 50_000;
        a.setCashForTest(a.getCash() + x);
        b.setCashForTest(b.getCash() + x);
        a.fundPayIn(x);
        play(a);
        play(b);
        double surplusA = a.getEconomyManager().getNationalAccounts().getBalance();
        double surplusB = b.getEconomyManager().getNationalAccounts().getBalance();
        close("a pay-in is not spending: the budget moves by the fund's transfer on it and nothing more",
                surplusA - surplusB, a.getFund().getTransferPaid() - b.getFund().getTransferPaid(), 1e-6);
        check("...and the journal names it", journalHas(a, "Paid into the fund", -x));
        double drawn = a.fundDrawOut(1_000);
        check("a draw-out moves what the fund's cash holds back", drawn == 1_000);
        a.fundBuyShares(0, 100);
        check("the hand's order waits for the step", a.getFund().getHandOrders().size() == 1);
        play(a);
        check("...and is posted at it, at fair value", a.getFund().getHandOrders().isEmpty()
                && a.getExchange().bookOf(0).bids().stream().filter(o -> o.who().equals(Exchange.FUND_HAND))
                        .allMatch(o -> Math.abs(o.price() - a.getExchange().fair(0)) < 1e-12));
    }

    /* ================= 11. the save ================= */

    static void theSave() {
        out.println("\n--- 11. every piece through a save, and an older save's fund ---");
        Game g = copy();
        g.setRescueMode(TreasuryFund.RescueMode.AUTOMATIC);
        g.setFundDial(2.5);
        g.setCashForTest(g.getCash() + 100_000);
        g.fundPayIn(60_000);
        play(g);
        Bank bank = g.getBank();
        takeEquityTo(bank, .9 * bank.minimumEquity());
        quietly(() -> g.getFund().noteOffered(g.getMonth()));
        g.acceptPreferredOffer();
        takeEquityTo(bank, bank.minimumEquity());
        bank.payPreferredDividends();
        g.fundBuyShares(0, 500);
        quietly(() -> g.saveGame(3));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(3));
        TreasuryFund f = g.getFund(), r = back.getFund();
        check("the dial and the rescue setting", r.getDial() == 2.5 && r.getRescueMode() == TreasuryFund.RescueMode.AUTOMATIC);
        close("its cash", r.getCash(), f.getCash(), 1e-9);
        boolean shares = true;
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            shares &= Math.abs(back.getEquity().getCityShares(c) - g.getEquity().getCityShares(c)) < 1e-9
                    && Math.abs(back.getEquity().getCityRescueShares(c) - g.getEquity().getCityRescueShares(c)) < 1e-9;
        }
        check("its shares, both books, on the register", shares);
        close("its bonds, on the bonds", back.getBondMarket().faceHeldByCity(), g.getBondMarket().faceHeldByCity(), 1e-9);
        close("the preferred", back.getBank().preferredOutstanding(), bank.preferredOutstanding(), 1e-9);
        close("...its arrears", back.getBank().getPreferredArrears(), bank.getPreferredArrears(), 1e-9);
        check("...its anniversary and warrants", back.getBank().getPreferred().size() == bank.getPreferred().size()
                && back.getBank().getPreferred().get(0).issued() == bank.getPreferred().get(0).issued()
                && back.getBank().getPreferred().get(0).warrantShares() == bank.getPreferred().get(0).warrantShares());
        check("the player's order waiting for the step", r.getHandOrders().size() == 1
                && r.getHandOrders().get(0).amount() == f.getHandOrders().get(0).amount());
        check("its record of the offers", r.getOffersAccepted() == f.getOffersAccepted()
                && r.getAcceptedMonth() == f.getAcceptedMonth());
        close("its value", back.fundValue(), g.fundValue(), 1e-6);
        check("fixture: the month just closed paid a transfer and bought something", f.getTransferDue() > 0
                && f.getMonthBought() > 0);
        check("...and the month's transfer and flows, which the Fund page reads, come back with it",
                r.getTransferDue() == f.getTransferDue() && r.getTransferPaid() == f.getTransferPaid()
                        && r.getMonthDividends() == f.getMonthDividends() && r.getMonthCoupons() == f.getMonthCoupons()
                        && r.getMonthPrincipal() == f.getMonthPrincipal() && r.getMonthBought() == f.getMonthBought()
                        && r.getMonthSold() == f.getMonthSold());
        TreasuryFund old = new TreasuryFund();
        old.restore(null);
        check("a save from before the fund loads it empty, the dial at 0, the rescue on the button",
                old.isEmpty() && old.getDial() == 0 && old.getRescueMode() == TreasuryFund.RescueMode.BUTTON
                        && !old.isOfferPending());
    }

    /* ================= 12. Insane ================= */

    static void insane() {
        out.println("\n--- 12. Insane: nothing, and the ground owed abroad ---");
        Founding insane = Founding.named("Insane", Founding.Preset.INSANE, WorldEconomy.DEFAULT_MEAN_INFLATION);
        check("D$0 and US$0 read back as Insane", Founding.Preset.of(0, 0) == Founding.Preset.INSANE
                && insane.problem() == null);
        check("...and a custom founding still takes at least D$5M", Founding.cashProblem(Founding.MIN_CASH - 1) != null);
        Game g = new Game(GameFiles.scratch("fundcheck-insane"), insane);
        quietly(g::run);
        check("the treasury holds nothing and the vault nothing",
                g.getCash() == 0 && g.getForeignAccounts().getReservesUsd() == 0);
        Debt land = null;
        int pieces = 0;
        for (Debt d : g.getDebtManager().getDebt()) { land = d; pieces++; }
        check("it owes one piece of paper", pieces == 1);
        check("...a dollar term loan abroad", land instanceof LongTermBond && land.isForeign());
        LongTermBond bond = (LongTermBond) land;
        close("...at 3%", bond.getCouponRate(), Founding.INSANE_LAND_COUPON, 1e-15);
        check("...for twenty years", bond.getDuration() == Founding.INSANE_LAND_YEARS * 12);
        close("...for its ground at the land market's opening dollar price", bond.principalInCurrency(),
                LandManager.STARTING_SQ_FT * LandMarket.openingUsdPerSqFt(), 1e-9);
        out.printf("   the land bond: US$%,.0fk face, worth US$%,.0fk on the world's curve; the window abroad: %s%n",
                bond.principalInCurrency(), g.getDebtManager().marketValue(bond) / Math.max(1e-9, g.getForeignAccounts().getRate()),
                g.foreignWindowOpen() ? "open" : "shut - " + g.foreignWindowReason());
        // 0.7.14 asserted here that the clock and the time skip refused this city until it
        // borrowed; Jerus's "Play runs on advances" and "Skip runs too" (0.7.15) took both
        // stops out, and section 13 runs it unborrowed instead.
        final int[] ran = new int[1];
        double village = Founding.whatItBuys(g.getBuildingManager().getTemplates(), 0, 0).village();
        DebtQuote quote = g.quoteLongBondForCash(village, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE);
        out.printf("   day 0: the village invoiced $%,.0fk; the %d-year bond for it at %.2f%%, $%,.0fk of face%n",
                village, Game.BUILD_BOND_YEARS, quote.marketRate() * 100, quote.faceValue());
        check("a day-0 city is quoted a bond", !quote.isEmpty() && quote.cashReceived() >= village);
        DebtQuote note = g.quoteTBill(village, Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE);
        out.printf("   ...the %d-month note for it at %.2f%%, $%,.0fk of face%n", Game.BUILD_NOTE_MONTHS,
                note.marketRate() * 100, note.faceValue());
        check("...and the note", !note.isEmpty() && note.cashReceived() >= village);
        BuildingsTemplate house = template(g, "House");
        check("the build screen at D$0: a house asks for funding, the whole of it",
                g.buildStack(house, 1, false) == Game.BuildResult.NEEDS_FUNDING && g.buildFundingGap(house, 1) > 0);
        java.util.List<Integer> plot = g.nextLandParcels(1);
        check("the land office at D$0: a plot asks for funding, and is quoted both offers",
                !plot.isEmpty() && g.landNeedsFunding(plot) && g.landCashGap(plot) > 0
                        && !g.quoteLongBondForCash(g.landCashGap(plot), Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE).isEmpty()
                        && !g.quoteTBill(g.landCashGap(plot), Game.BUILD_NOTE_MONTHS, Game.BUILD_NOTE_GRANULE).isEmpty());
        quietly(() -> g.handleLongBondForCash(village, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE));
        check("borrowed, the treasury holds cash", g.getCash() > 0);
        quietly(() -> ran[0] = g.simulateMonths(1));
        check("...a month", ran[0] == 1);
        MoneyAudit.Result r = g.getLastMoneyAudit();
        check("...and its audit closes", Math.abs(r.residual) <= .01 || r.relative() <= 1e-7);
    }

    /* ================= 13. Insane, never borrowing (0.7.15) ================= */

    /**
     * Jerus, asked whether play should work before the city borrows: "Play
     * works from day one. The central bank covers what the treasury must pay,
     * which starts with just the land bond's coupon; optional spending is
     * refused." And of the time skip, "Skip runs too". So an Insane city that
     * never borrows is played a year through the skip, as a player leaving it
     * alone would.
     */
    static void insaneOnAdvances() {
        out.println("\n--- 13. Insane, never borrowing: play runs on the central bank's advances ---");
        Founding insane = Founding.named("Insane", Founding.Preset.INSANE, WorldEconomy.DEFAULT_MEAN_INFLATION);
        Game g = new Game(GameFiles.scratch("fundcheck-insane-advances"), insane);
        quietly(g::run);
        CentralBank cb = g.getCentralBank();
        check("day 0: nothing in the treasury and no revenue behind it, so the central bank's ceiling is nothing",
                g.getCash() == 0 && cb.ceiling() == 0);
        check("...and nothing that is not a promise may be spent", g.discretionaryRoom() == 0);
        Game twin = new Game(GameFiles.scratch("fundcheck-insane-twin"), insane);
        quietly(twin::run);
        check("...a purchase asked of it is refused whole, and nothing is owed for it",
                twin.treasuryPays(TreasuryLine.BUILDINGS, 1_000) == 0 && twin.getCash() == 0 && twin.getArrearsTotal() == 0);
        check("...a discretionary line is refused, and owed as arrears",
                twin.treasuryPays(TreasuryLine.CITY_REPAIRS, 1_000) == 0 && twin.getCash() == 0
                        && twin.getArrearsTotal() == 1_000);
        check("...and a promise is paid past it, taking the treasury under nothing",
                twin.treasuryPays(TreasuryLine.PENSIONS, 1_000) == 1_000 && twin.getCash() == -1_000);

        LongTermBond land = (LongTermBond) g.getDebtManager().getDebt().get(0);
        double couponUsd = Founding.landBondUsd() * Founding.INSANE_LAND_COUPON / 12;
        int monthsLeft = land.getRemainingMonths();
        boolean everyMonthRan = true, everyAuditClosed = true, everyCoupon = true, withinRoom = true;
        double cashAfterOne = Double.NaN, paidInOne = Double.NaN, rateInOne = Double.NaN, advancedInTwo = Double.NaN;
        double mostOwed = 0;
        int mostOwedMonth = 0;
        final int[] ran = new int[1];
        for (int m = 1; m <= 12; m++) {
            double rate = g.getForeignAccounts().getRate();
            double room = g.discretionaryRoom();
            quietly(() -> ran[0] = g.simulateMonths(1));
            everyMonthRan &= ran[0] == 1;
            MoneyAudit.Result r = g.getLastMoneyAudit();
            everyAuditClosed &= (Math.abs(r.residual) <= .01 || r.relative() <= 1e-7)
                    && Math.abs(g.getPostAuditDrift()) <= .01;
            double paid = g.getForeignInterestPaidThisMonth();
            everyCoupon &= Math.abs(paid - couponUsd * rate) <= 1e-9 * Math.max(1, paid);
            NationalAccounts books = g.getEconomyManager().getNationalAccounts();
            double discretionary = books.getCapitalSpending() + books.getLandPurchases()
                    + books.getStudentGrants() + books.getSubsidies();
            withinRoom &= discretionary <= room + 1e-9;
            if (m == 1) { cashAfterOne = g.getCash(); paidInOne = paid; rateInOne = rate; }
            if (m == 2) advancedInTwo = cb.getAdvancedToTreasury();
            if (cb.getAdvancesToTreasury() > mostOwed) { mostOwed = cb.getAdvancesToTreasury(); mostOwedMonth = g.getMonth(); }
        }
        check("an Insane city that never borrowed runs twelve months through the time skip", everyMonthRan && g.getMonth() == 13);
        check("...and the audit closes every month, nothing moving after it", everyAuditClosed);
        close("month one pays the land bond's coupon, a twelfth of INSANE_LAND_COUPON on its face at the month's rate",
                paidInOne, couponUsd * rateInOne, 1e-9);
        check("...from a treasury that held nothing, which the month leaves overdrawn", cashAfterOne < 0);
        close("month two's settle: the central bank advances the whole shortfall", advancedInTwo, -cashAfterOne, 1e-9);
        check("every month's coupon is paid, the land bond owed whole and never missed",
                everyCoupon && land.getRemainingMonths() == monthsLeft - 12
                        && land.principalInCurrency() == Founding.landBondUsd()
                        && g.getDebtManager().getDefaultScar() == 0);
        check("nothing that is not a promise was spent past the room the month opened with", withinRoom);
        check("...and it borrowed nothing: the land bond is still all it owes",
                g.getDebtManager().getDebt().size() == 1);
        out.printf("   after a year: owes the central bank $%,.2fk (at most $%,.2fk, m%d); the treasury holds $%,.0fk;"
                        + " %d people%n", cb.getAdvancesToTreasury(), mostOwed, mostOwedMonth, g.getCash(),
                g.getPopulationManager().getPopulation());
    }
}
