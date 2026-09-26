package ham.citybuildersim;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Corporate bonds (0.7.12): the bond, how it is sold, when a sector takes it
 * over the bank, who loses what in a default, what the bank charges for
 * concentration, who buys and sells, and the save.
 *
 * WHAT THIS HAS TO PROVE (Jerus, 2026-09-24: "Cheapest, within the bank's
 * limit", "No limit, just price it", "Bank notes rank first", "Order book for
 * both"):
 *
 *   1. THE BOND'S ARITHMETIC: priced at its own coupon it is worth its face;
 *      its price falls as the yield rises; the coupons a month are a twelfth
 *      of the coupon on the face each holder had, exactly; at maturity every
 *      holder is paid its face.
 *   2. BOOKBUILDING: the coupon is the lowest yield at which the bids fill the
 *      issue; what the bids will not fill at a price no dearer than the loan
 *      is a bank loan; and at a loan rate under every bid, all of it is.
 *   3. THE CHOICE: a small amount goes to the bank, a large one to a bond,
 *      and the crossover comes from the fixed cost, the underwriter's spread,
 *      the loan's price and the book's yield alone - printed.
 *   4. THE ORDER BOOK in a played month: after the market's step no book is
 *      crossed, the sellers who waited are counted, and the audit closes
 *      through months of trading. (The book's own rules are OrderBookCheck's.)
 *   5. RECOVERIES BY INSTRUMENT (round 2, Jerus: "Real averages by type",
 *      in place of round 1's "Bank notes rank first"): a slice takes a
 *      loan's loss off the loans and a bond's off the bonds, whatever the
 *      mix; the backstop on a sector with nothing left writes both off whole;
 *      the loss reaches every holder class; the world's is declared across
 *      the border; the allowance, a loan's price and a bond's value read the
 *      same pair. And the recoveries against Moody's and S&P's ranges,
 *      printed.
 *   6. CONCENTRATION: Basel's IRB capital reproduces Basel's published risk
 *      weights; a sector's charge rises with its share and falls as the book
 *      diversifies; the Euler shares add back to the add-on; the charge is in
 *      the loan's price through the capital charge, and the add-on in the
 *      bank's requirement, its weight table still footing (BankCheck 13).
 *   7. EACH PARTICIPANT on a fixture that causes it: a household bids when the
 *      expected return beats the deposit rate, and a household short of money
 *      sells into a resting bid - or waits; the bank bids only at or over its
 *      loan-equivalent yield, never under its target, and asks to raise
 *      capital; a company never bids for its own bonds; the world's purchase
 *      is a financial inflow the currency reads and its coupons an income
 *      outflow, and it does not bid while its money is running.
 *   8. SAVE AND LOAD: bonds, holdings and resting orders round-trip and the
 *      reloaded city plays the same month; a save from before 0.7.12 loads
 *      with no bonds and plays. Each cell's own bonds round-trip by the
 *      cell's name, and a round-1 save - one pool, a claim per cell - loads
 *      into the cells by those claims.
 *   9. EACH HOUSEHOLD TYPE TRADES (round 2, Jerus: "Each household type
 *      trades"): a cell with money past its cushion bids and a cell holding
 *      more than its money calls for asks, and they trade with each other -
 *      a transfer inside the households that no pool line sees; and a cell
 *      short of money sells into a rich cell's resting bid in the waterfall.
 *  10. BUY ONLY WHAT IT CAN PAY FOR (round 6, Jerus: "Buy only what it can
 *      pay for"; section 5d): a sector under its ceiling places its orders
 *      whole; one over it and short of cash buys only what its cash and the
 *      credit it can get cover, and does not default on the stock it did not
 *      buy; the same sector still defaults on a bill it cannot avoid, and
 *      buys nothing that month; the audit closes.
 *  11. CAN'T PAY MEANS DEFAULT, IN PLAY (rounds 4 and 5; section 5c): a bank
 *      short of capital still covers a healthy sector's short month - the
 *      working-capital line - and refuses it a building; a short sector
 *      sells its bonds first, then asks the lender, and what nobody lends it
 *      defaults that month by instrument and is lent in the interim, its
 *      till ending at or above nothing; nothing is forgiven off the
 *      backstop's path; the month closes.
 *
 * Every fixture causes its condition.
 */
public class BondCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;
    static int closedMonths, brokenMonths;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-92s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-92s %s  %,.9f against %,.9f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    /** An audit line against the figure it declares: the audit's detail is written to the cent. */
    static void cents(String label, double line, double figure) {
        boolean ok = Math.abs(line - figure) <= .005 + 1e-9;
        if (!ok) fails++;
        out.printf("%-92s %s  %,.2f against %,.4f%n", label, ok ? "OK" : "FAIL", line, figure);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** The sector every played fixture's bond is issued by: sound, owing nothing, with plant to borrow against. */
    static final String ISSUER = Sectors.CONSTRUCTION;

    /** A city with households who save, a bank, sectors that owe it and sectors that do not: founded, built, played two years. */
    static Game city(Path root, String name, int months) {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            // The treasury this fixture is written against: the Wealthy preset's.
            g.setCashForTest(Founding.WEALTHY_CASH);
            BuildingManager b = g.getBuildingManager();
            b.addStack(b.getTemplateByName("House"), 300, true);
            b.addStack(b.getTemplateByName("Convenience Store"), 12, true);
            b.addStack(b.getTemplateByName("Bakery"), 3, true);
            b.addStack(b.getTemplateByName("Construction Depot"), 3, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 2, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            LongPlaytest.build(g, "Commercial Bank", 1);
            // The food plants the fixture keeps, as ConservationCheck's does.
            g.setAutoSubsidised(Sectors.INDUSTRY, true);
            g.simulateMonths(months);
        });
        return g;
    }

    /** One played month, and whether its audit closed. */
    static MoneyAudit.Result play(Game g) {
        quietly(g::toggleNextMonth);
        MoneyAudit.Result r = g.getLastMoneyAudit();
        if (closes(r)) closedMonths++;
        else {
            brokenMonths++;
            out.printf("   m%d: residual %.4f%n", g.getMonth(), r.residual);
        }
        return r;
    }

    static boolean closes(MoneyAudit.Result r) {
        return r != null && (Math.abs(r.residual) <= .01 || r.relative() <= 1e-7);
    }

    /** One line of a month's audit, by its label: what MoneyAudit declared under it, 0 when it declared nothing. */
    static double line(MoneyAudit.Result r, String label) {
        for (String l : r.detail.split("\n")) {
            String t = l.trim();
            if (!t.startsWith(label + " ")) continue;
            String rest = t.substring(label.length()).trim();
            try { return Double.parseDouble(rest.replace(",", "")); } catch (NumberFormatException e) { return Double.NaN; }
        }
        return 0;
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = Files.createTempDirectory("bondcheck");

        arithmetic();
        Game g = city(root, "market", 24);
        bookbuilding(g);
        theChoice(g);
        companies(g);
        CorporateBond bond = issuedInTheMonth(g);
        if (bond == null) {
            out.println("\nno bond was issued in the fixture's month: the rest cannot run");
            out.println("\n" + (fails + 1) + " FAILED");
            System.exit(1);
        }
        theBookInPlay(g);
        participants(g, bond);
        seniorityOnTheLender();
        seniorityInPlay(g);
        cantPayInPlay(g);
        buyOnlyWhatItCanPayFor(root);
        coupons(g);
        concentration(g);
        eachTypeTrades();
        saveAndLoad(g, root);
        maturity(g);

        assertTrue("every month this harness played closed the audit (" + closedMonths + ")", brokenMonths == 0);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================== 1. THE ARITHMETIC ============================== */

    static void arithmetic() {
        out.println("--- 1. the bond's arithmetic: par at its own coupon, and a price that falls as the yield rises ---");
        double worstPar = 0;
        for (double c : new double[] { .01, .045, .08, .15 }) {
            for (int n : new int[] { 1, 12, 60, 119, CorporateBond.TERM_MONTHS }) {
                worstPar = Math.max(worstPar, Math.abs(CorporateBond.priceAtYield(c, n, c) - 1));
            }
        }
        close("priced at its own coupon a bond is worth exactly its face, whatever is left of it", worstPar, 0, 1e-12);
        int n = CorporateBond.TERM_MONTHS;
        double c = .05;
        assertTrue("a point more on the yield and it is worth less", CorporateBond.priceAtYield(c, n, .06) < 1);
        assertTrue("...a point less and it is worth more", CorporateBond.priceAtYield(c, n, .04) > 1);
        boolean falls = true;
        double prev = Double.POSITIVE_INFINITY;
        for (double y = 0; y <= .30; y += .0025) {
            double p = CorporateBond.priceAtYield(c, n, y);
            if (!(p < prev)) falls = false;
            prev = p;
        }
        assertTrue("...and it falls all the way, the yield from 0% to 30%", falls);
        double r = .06 / 12, disc = Math.pow(1 + r, -n);
        close("the coupons and the face, each discounted a month at a time", CorporateBond.priceAtYield(c, n, .06),
                c / 12 * (1 - disc) / r + disc, 1e-12);
        close("the yield a price implies is the one that gives it",
                CorporateBond.yieldAtPrice(c, n, CorporateBond.priceAtYield(c, n, .0731)), .0731, 1e-9);
        close("with nothing left it is worth its face", CorporateBond.priceAtYield(c, 0, .2), 1, 0);
        CorporateBond b = new CorporateBond(1, ISSUER, 1_000, .06, 0, n);
        close("a month's coupon is a twelfth of the coupon on the face", b.monthlyCoupon(), 1_000 * .06 / 12, 1e-15);
        b.households = 400; b.bank = 300; b.world = 200; b.companies.put(Sectors.RETAIL, 100.0);
        b.bankCost = 300;
        double gone = b.writeDown(.25);
        close("written down to a quarter, three quarters of it is gone", gone, 750, 1e-12);
        assertTrue("...off every holder by the same share, the bank's cost with its face",
                b.households == 100 && b.bank == 75 && b.world == 50 && b.company(Sectors.RETAIL) == 25 && b.bankCost == 75);
        close("...so the holders still add up to the face", b.held(), b.face(), 1e-12);
    }

    /* ============================== 2. BOOKBUILDING ============================== */

    static void bookbuilding(Game g) {
        out.println("\n--- 2. bookbuilding: the coupon is the lowest yield that fills the issue, and the rest is a bank loan ---");
        BondMarket bm = g.getBondMarket();
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        int m = g.getMonth();
        double amount = 5_000;
        double lr = credit.projectRate(ISSUER, amount);
        BusinessDebtManager.Plan p = bm.plan(ISSUER, amount, lr, amount, amount, amount, m);
        out.printf("   %s borrowing $%,.0fk at the bank's %.2f%% (%.2f%% all-in): a bond of $%,.1fk at %.3f%% (%.3f%% all-in), $%,.1fk from the bank%n",
                ISSUER, amount, lr * 100, p.loanAllIn() * 100, p.bondFace(), p.coupon() * 100, p.allIn() * 100, p.loan());
        assertTrue("fixture: at the bank's own price the book takes a bond of it", p.hasBond());
        BondMarket.Demand dm = bm.new Demand(ISSUER, p.bondFace(), p.offered(), p.dealDebt(), p.extraAssets());
        assertTrue("the bids at its coupon fill it", dm.total(p.coupon()) >= p.bondFace() * (1 - 1e-9));
        assertTrue("...and a hundredth of a point under it they do not: its coupon is the lowest yield that does",
                dm.total(p.coupon() - 1e-4) < p.bondFace());
        assertTrue("...no dearer than the loan, its costs spread over its ten years",
                p.allIn() <= p.loanAllIn() + 1e-9);
        assertTrue("fixture: the bank would take it only at its loan-equivalent yield, over the loan's price",
                dm.bankYield > p.loanAllIn());
        assertTrue("fixture: so the book does not take all of it at a price no dearer than the loan", p.bondFace() < amount);
        close("...and the rest is a bank loan", p.loan(), amount - p.bondFace(), 1e-9);
        assertTrue("...the two raising it all", p.covers());
        close("...at the leverage the whole deal leaves, which every bid read", p.dealDebt(), amount, 0);

        BusinessDebtManager.Plan none = bm.plan(ISSUER, amount, .001, amount, amount, amount, m);
        assertTrue("at a loan rate under every bid the book fills nothing: all of it is a bank loan",
                !none.hasBond() && none.loan() == amount);
        close("...and the plan says what the book would have cleared the whole at",
                none.clearing(), bm.clearingYield(bm.new Demand(ISSUER, amount, amount, amount, amount), amount), 1e-12);

        BusinessDebtManager.Plan shut = bm.plan(ISSUER, amount, lr, 0, amount, amount, m);
        assertTrue("where the bank would lend nothing, no bond either: \"within the bank's limit\"", !shut.hasBond());
        BusinessDebtManager.Plan part = bm.plan(ISSUER, amount, lr, amount / 4, amount, amount, m);
        assertTrue("...and where it would lend a quarter, the bond and the loan raise no more than that quarter",
                part.bondFace() + part.loan() <= amount / 4 * (1 + 1e-12));
    }

    /* ============================== 3. THE CHOICE ============================== */

    static void theChoice(Game g) {
        out.println("\n--- 3. the choice: a small amount goes to the bank, a large one to a bond, and where they cross ---");
        BondMarket bm = g.getBondMarket();
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        int m = g.getMonth();
        double[] amounts = { 10, 20, 50, 100, 200, 500, 1_000, 5_000, 20_000 };
        BusinessDebtManager.Plan[] plans = new BusinessDebtManager.Plan[amounts.length];
        out.println("      borrowing    bank's rate  all-in   bond        its coupon  all-in   from the bank");
        double lowest = Double.POSITIVE_INFINITY;
        for (int i = 0; i < amounts.length; i++) {
            double a = amounts[i];
            double lr = credit.projectRate(ISSUER, a);
            plans[i] = bm.plan(ISSUER, a, lr, a, a, a, m);
            BusinessDebtManager.Plan p = plans[i];
            out.printf("      $%,9.0fk  %6.3f%%     %6.3f%%  $%,9.1fk  %s  %s  $%,9.1fk%n", a, lr * 100, p.loanAllIn() * 100,
                    p.bondFace(), p.hasBond() ? String.format("%7.3f%%", p.coupon() * 100) : "      -",
                    p.hasBond() ? String.format("%6.3f%%", p.allIn() * 100) : "     -", p.loan());
            if (p.hasBond()) lowest = Math.min(lowest, p.coupon());
        }
        assertTrue("borrowing $10k it takes the bank: the fixed cost of an issue would be more than a tenth of it",
                !plans[0].hasBond());
        assertTrue("borrowing $20M it takes a bond", plans[amounts.length - 1].hasBond());
        BusinessDebtManager.Plan big = plans[amounts.length - 1];
        double loanAllIn = BondMarket.loanAllIn(big.loanRate());
        double crossAtLowest = bm.crossover(plans[0].loanRate(), lowest);
        out.printf("   at the book's lowest coupon in the table, %.3f%%, against the bank's %.3f%% (%.3f%% all-in),"
                + " a bond beats the bank from $%,.1fk%n", lowest * 100, plans[0].loanRate() * 100,
                plans[0].loanAllIn() * 100, crossAtLowest);
        boolean under = true;
        for (int i = 0; i < amounts.length; i++) if (amounts[i] < crossAtLowest && plans[i].hasBond()) under = false;
        assertTrue("...and nothing smaller than that took a bond", under);
        double x = bm.crossover(big.loanRate(), big.coupon());
        out.printf("   at the $%,.0fk issue's coupon, %.3f%%, against %.3f%% all-in, the crossover is $%,.1fk%n",
                amounts[amounts.length - 1], big.coupon() * 100, loanAllIn * 100, x);
        close("the crossover is the face at which the coupon and its costs cost what the loan does",
                bm.allIn(x, big.coupon()), loanAllIn, 1e-12);
        double unit = g.getDenomination().getUnit();
        close("...fixed / (TERM_YEARS x (loan all-in - coupon - spread / TERM_YEARS)), from nothing else", x,
                Game.FIXED_ISSUE_COST / unit / (BondMarket.TERM_YEARS
                        * (loanAllIn - big.coupon() - Game.UNDERWRITING_SPREAD / BondMarket.TERM_YEARS)), 1e-12);
        assertTrue("...half that size and the bond would be dearer than the bank", bm.allIn(x / 2, big.coupon()) > loanAllIn);
        assertTrue("...twice it, cheaper", bm.allIn(2 * x, big.coupon()) < loanAllIn);
        close("a bank loan's all-in is its rate and its fee over its term", BondMarket.loanAllIn(.05),
                .05 + Bank.LOAN_FEE * 12.0 / BusinessDebtManager.LOAN_TERM_MONTHS, 1e-15);
    }

    /* ======================= 7a. A BOND SOLD IN THE MONTH ======================= */

    static CorporateBond issuedInTheMonth(Game g) {
        out.println("\n--- 7a. a sector short of cash borrows in the month: the shortfall desk asks the book, and the world's purchase crosses the border ---");
        BondMarket bm = g.getBondMarket();
        EconomyManager em = g.getEconomyManager();
        // Five million short, set between months - before the month's audit
        // opens its pools. Past what it holds abroad and in other sectors'
        // bonds since round 4, which a short sector sells before it borrows
        // (CAN'T PAY MEANS DEFAULT): without them the desk would never be asked.
        em.setSectorCash(ISSUER, -5_000 - em.getForeignAssets(ISSUER) - bm.faceHeldBy(ISSUER));
        int before = bm.getLifeIssues();
        MoneyAudit.Result r = play(g);
        assertTrue("fixture: short of $5M, " + ISSUER + " issued a bond in the month", bm.getIssued(ISSUER) > 0
                && bm.getLifeIssues() > before);
        CorporateBond bond = null;
        for (CorporateBond b : bm.getBonds(ISSUER)) if (b.issueMonth() == g.getMonth()) bond = b;
        if (bond == null) return null;
        out.printf("   bond %d: $%,.1fk at %.3f%% - households $%,.1fk, the bank $%,.1fk, companies $%,.1fk, the world $%,.1fk%n",
                bond.id(), bond.face(), bond.coupon() * 100, bond.households(), bond.bank(), bond.companiesTotal(), bond.world());
        close("its holders add up to its face", bond.held(), bond.face(), 1e-9);
        close("the issuer was handed its face less its costs", bm.getProceeds(ISSUER), bm.getIssued(ISSUER) - bm.issueCost(bm.getIssued(ISSUER)), 1e-9);
        close("...which the bank, its underwriter, was paid", g.getBank().getUnderwritingFees(), bm.getIssuedCosts(), 1e-9);
        double worldBought = bm.getWorldBought();
        assertTrue("fixture: the world bought part of it", worldBought > 0);
        cents("the world's purchase is on the month's audit as money arriving from abroad", line(r, "+ bonds BoughtAbroad"), worldBought);
        assertTrue("...in the financial account", r.financialIn >= worldBought - 1e-9);
        close("...which is what the currency read for the month", g.getForeignAccounts().getFinancialIn(), r.financialIn, 1e-9);
        assertTrue("...and the month closes", closes(r));
        return bond;
    }

    /* ============================ 1b. THE COUPONS ============================ */

    static void coupons(Game g) {
        out.println("\n--- 1b. the coupons: a twelfth of each coupon on what each holder had at the month's top, every class ---");
        BondMarket bm = g.getBondMarket();
        double hh = 0, bank = 0, world = 0, companies = 0;
        for (CorporateBond b : bm.getBonds()) {
            double c = b.coupon() / 12;
            hh += b.households() * c;
            bank += b.bank() * c;
            world += b.world() * c;
            companies += b.companiesTotal() * c;
        }
        double issuerCoupon = bm.monthlyCoupon(ISSUER);
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        close("the issuer's month's interest is its loans' and its bonds' coupons",
                credit.getMonthlyInterest(ISSUER), credit.getLoanInterest(ISSUER) + issuerCoupon, 1e-12);
        MoneyAudit.Result r = play(g);
        close("the households were paid theirs", bm.getCouponsToHouseholds(), hh, 1e-12);
        close("...the bank its", bm.getCouponsToBank(), bank, 1e-12);
        close("...which it took as interest", g.getBank().getInterestFromBonds(), bank, 1e-12);
        close("...the companies theirs", bm.getCouponsToCompanies(), companies, 1e-12);
        close("...and the world its, abroad", bm.getCouponsAbroad(), world, 1e-12);
        assertTrue("fixture: every class held some of the city's bonds at the month's top", world > 0 && hh > 0 && bank > 0 && companies > 0);
        cents("the world's coupons leave on the audit, as income paid abroad", line(r, "- bonds CouponsAbroad"), world);
        assertTrue("...in the income account the currency reads", r.incomeOut >= world - 1e-9);
        cents("the households' coupons are on it too", line(r, "- bonds CouponsToHouseholds"), hh);
    }

    /* ============================ 4. THE BOOK IN PLAY ============================ */

    static void theBookInPlay(Game g) {
        out.println("\n--- 4. the order book in play: no book crossed after the step, the sellers who waited counted, the audit closed ---");
        BondMarket bm = g.getBondMarket();
        int months = 0, trades = 0, sellsPosted = 0, sellsWaited = 0;
        boolean uncrossed = true, ownBid = false;
        int restingBids = 0, restingAsks = 0;
        for (int i = 0; i < 6; i++) {
            play(g);
            months++;
            trades += bm.getLastTrades();
            for (CorporateBond b : bm.getBonds()) {
                OrderBook book = bm.bookOf(b);
                restingBids += book.bids().size();
                restingAsks += book.asks().size();
                if (!book.bids().isEmpty() && !book.asks().isEmpty() && !(book.bestBid() < book.bestAsk())) uncrossed = false;
                if (book.resting(b.issuer(), OrderBook.Side.BUY) > 0) ownBid = true;
            }
        }
        // The month's close is counted at the next step: one more to read the last one's.
        play(g);
        sellsPosted = bm.getLifeSellsPosted();
        sellsWaited = bm.getLifeSellsWaited();
        out.printf("   over the city's life: %d sell order(s) posted, %d of them waited unfilled; %d trade(s) in these %d months;"
                + " bids and asks resting after the steps: %d and %d%n", sellsPosted, sellsWaited, trades, months, restingBids, restingAsks);
        assertTrue("fixture: participants posted orders on the books", restingBids + restingAsks > 0);
        assertTrue("after every step every book is uncrossed: what could meet already traded", uncrossed);
        assertTrue("...and no sector bids for its own bonds", !ownBid);
        assertTrue("the sellers who waited are counted, and are no more than the sellers", sellsWaited <= sellsPosted);
        close("the market's life volume is the sum of its books'", bm.getLifeFilledSell(), bm.getLifeVolume(), 1e-9);
    }

    /* ============================ 7. THE PARTICIPANTS ============================ */

    static void participants(Game g, CorporateBond bond) {
        out.println("\n--- 7. each participant by its own rule ---");
        BondMarket bm = g.getBondMarket();
        Bank bank = g.getBank();
        double size = 1_000;
        BondMarket.Demand dm = bm.new Demand(ISSUER, size, size, size, 0);

        // The households: the city's paper's rule on the expected return.
        double floor = dm.el + dm.deposit;
        close("households bid nothing where a bond's expected return is no more than the deposit rate",
                dm.households(floor), 0, 0);
        close("...a point over it, the paper's rule: HOUSEHOLD_PAPER_APPETITE a unit of spread of the issue",
                dm.households(floor + .01), Math.min(dm.householdSpare,
                        HouseholdBalance.HOUSEHOLD_PAPER_APPETITE * .01 * size), 1e-9);
        close("...and never more than MAX_HOUSEHOLD_PAPER_SHARE of it", dm.households(floor + .30),
                Math.min(dm.householdSpare, HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE * size), 1e-9);

        // The bank: its loan-equivalent yield, on its spare capital.
        assertTrue("fixture: the bank is over its capital target, so it buys", bank.equity() >= bank.targetEquity()
                && Double.isFinite(dm.bankYield));
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        double policy = g.getDebtManager().getPolicyRate();
        double lev = BusinessDebtManager.pricingLeverage(credit.quarterPrincipal(ISSUER) + size, credit.quarterAssets(ISSUER));
        close("its yield is an equal loan's four parts at the bond's term, the concentration charge, the loss at (1 - BOND_RECOVERY) and the record",
                dm.bankYield, bank.loanRate(policy, CorporateBond.TERM_MONTHS, Bank.RISK_BUSINESS)
                        + bank.concentrationCharge(policy, CorporateBond.TERM_MONTHS, ISSUER)
                        + BusinessDebtManager.expectedLossSpread(lev, 1 - BusinessDebtManager.BOND_RECOVERY)
                        + credit.getRecordSurcharge(ISSUER), 1e-12);
        close("under it the bank bids nothing", dm.bank(dm.bankYield - 1e-9), 0, 0);
        close("...at it, the issue up to what its spare capital carries", dm.bank(dm.bankYield),
                Math.min(size, bank.spareCapital() / bank.capitalPerDollar(Bank.RISK_BUSINESS, ISSUER)), 1e-9);
        double weighted = 0;
        for (CorporateBond b : bm.getBonds()) {
            weighted += b.bankCost() * Bank.RISK_BUSINESS * Bank.maturityWeight(b.remainingMonths(g.getMonth()));
        }
        close("its bonds are in its weighted book at a loan's weight, for the months they have left", bank.getBondWeighted(), weighted, 1e-9);

        // A bank under its target: it bids for nothing and asks for what takes it back.
        underItsTarget();

        // The world: hot money's rule; nothing while it runs.
        close("the world bids a month of closing the gap to hot money's target: ARRIVAL_SPEED of it",
                dm.world(dm.el + dm.world + dm.premium + .01),
                Math.min(size, CapitalFlows.ARRIVAL_SPEED * Math.min(size, .01 * CapitalFlows.APPETITE * dm.yearOfOutput
                        * size / (dm.faceOutstanding + size))), 1e-9);
        worldRunning();

        // A household short of money: into a resting bid, or it waits.
        shortOfMoney(g, bond);
    }

    /** The companies, on the fixture's month before anything borrows: the one that owes nothing bids for another's bonds and never its own. */
    static void companies(Game g) {
        out.println("\n--- 7c. a company with idle cash bids for other sectors' bonds, never its own, and only while it owes nothing ---");
        BondMarket bm = g.getBondMarket();
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        String other = Sectors.RETAIL;
        assertTrue("fixture: " + ISSUER + " has idle cash and owes nothing; " + other + " owes the bank",
                g.getEconomyManager().getSectorCash(ISSUER) > 0 && credit.getPrincipal(ISSUER) == 0
                        && credit.getPrincipal(other) > 0);
        BondMarket.Demand theirs = bm.new Demand(other, 1_000, 1_000, 1_000, 0);
        BondMarket.Demand own = bm.new Demand(ISSUER, 1_000, 1_000, 1_000, 0);
        int at = -1;
        for (int i = 0; i < theirs.companies.length; i++) if (theirs.companies[i].equals(ISSUER)) at = i;
        double y = theirs.el + theirs.world + .01;
        double target = Math.min(OutwardInvestment.MAX_SHARE, OutwardInvestment.APPETITE * .01) * theirs.wealth[at]
                * 1_000 / (theirs.othersFace[at] + 1_000);
        close(ISSUER + " bids for " + other + "'s bond: a month of OutwardInvestment's gap, at OUT_SPEED",
                theirs.company(at, y), Math.min(theirs.cash[at], OutwardInvestment.OUT_SPEED * Math.min(1_000, target)), 1e-9);
        assertTrue("...a point over the world's rate, a positive bid", theirs.company(at, y) > 0);
        close("...and for its own bond nothing, at any yield", own.company(at, .30), 0, 0);
        boolean oweNothing = true;
        for (int i = 0; i < theirs.companies.length; i++) {
            if (theirs.company(i, .30) > 0 && credit.getPrincipal(theirs.companies[i]) > 0) oweNothing = false;
        }
        assertTrue("...and no company that owes anything bids at all", oweNothing);
    }

    /** A market of its own around a bank rebuilding its capital. */
    static void underItsTarget() {
        double B = 1_000_000;
        double target = Bank.CAPITAL_RATIO + Bank.CONSERVATION_BUFFER;
        Bank rebuilding = BankCheck.lentOut((Bank.CAPITAL_RATIO + target) / 2 * B, B);
        BondMarket bm = new BondMarket();
        bm.attach(readings(false), null, rebuilding, null, null);
        CorporateBond b = new CorporateBond(1, ISSUER, 10_000, .06, 0, CorporateBond.TERM_MONTHS);
        b.bank = 10_000;
        b.bankCost = 10_000;
        BondMarket.State s = new BondMarket.State();
        s.bonds = new ArrayList<>(List.of(b));
        s.nextId = 2;
        bm.restore(s);
        assertTrue("fixture: a bank between its minimum and its target", rebuilding.equity() < rebuilding.targetEquity()
                && rebuilding.equity() > rebuilding.minimumEquity());
        BondMarket.Demand dm = bm.new Demand(ISSUER, 1_000, 1_000, 1_000, 0);
        assertTrue("under its target the bank bids for no bond at any yield", dm.bank(10) == 0);
        double short_ = rebuilding.targetEquity() - rebuilding.equity();
        bm.takeMonth(1);
        OrderBook book = bm.bookOf(bm.getBonds().get(0));
        double asked = book.resting(BondMarket.BANK, OrderBook.Side.SELL) + book.filledSell();
        out.printf("   the rebuilding bank is $%,.0fk short of its target and asked to sell $%,.0fk of its bonds;"
                + " the world bought $%,.0fk of it%n", short_, asked, book.filledSell());
        close("...and asks to sell what takes it back there, at a loan's weight on its target - or all it holds",
                asked, Math.min(10_000, short_ / (rebuilding.capitalTarget() * Bank.RISK_BUSINESS)), 1e-9);
    }

    /** The world's rule while its money is running: no bid. */
    static void worldRunning() {
        BondMarket bm = new BondMarket();
        bm.attach(readings(true), null, null, null, null);
        BondMarket.Demand dm = bm.new Demand(ISSUER, 1_000, 1_000, 1_000, 0);
        close("while hot money is running the world bids for nothing, at any yield", dm.world(.5), 0, 0);
    }

    static BondMarket.Readings readings(boolean running) {
        return new BondMarket.Readings() {
            @Override public int month() { return 1; }
            @Override public double curve(int months) { return .04; }
            @Override public double policyRate() { return .03; }
            @Override public double depositRate() { return .01; }
            @Override public double worldRate() { return .02; }
            @Override public double countryPremium() { return 0; }
            @Override public double monthlyGdp() { return 1_000_000; }
            @Override public double localPerUsd() { return 1; }
            @Override public boolean worldRunning() { return running; }
            @Override public double unit() { return 1; }
        };
    }

    /** A household cell short of money asks at the price its own borrowing rate makes the buyer's yield: into a bid at or over it, it sells at the bid's price; under every bid, it waits. */
    static void shortOfMoney(Game g, CorporateBond bond) {
        BondMarket bm = g.getBondMarket();
        HouseholdBalance hb = g.getHouseholdBalance();
        Household cell = null;
        for (Household c : hb.cells()) if (c.bonds() > 0 && (cell == null || c.bonds() > cell.bonds())) cell = c;
        assertTrue("fixture: a household cell holds a claim on the bonds", cell != null);
        if (cell == null) return;
        int m = g.getMonth();
        String me = BondMarket.CELL + cell.key();
        // The yield every resting best bid gives a buyer, over every bond.
        double lowest = Double.POSITIVE_INFINITY, highest = Double.NEGATIVE_INFINITY;
        Map<Integer, List<Double>> restingBids = new LinkedHashMap<>();
        Map<Integer, Double> lastBefore = new LinkedHashMap<>();
        for (CorporateBond b : bm.getBonds()) {
            OrderBook book = bm.bookOf(b);
            List<Double> prices = new ArrayList<>();
            for (OrderBook.Order o : book.bids()) prices.add(o.price());
            restingBids.put(b.id(), prices);
            lastBefore.put(b.id(), book.lastPrice());
            if (book.bids().isEmpty() || !(b.households() > 0)) continue;
            double y = CorporateBond.yieldAtPrice(b.coupon(), b.remainingMonths(m), book.bestBid());
            lowest = Math.min(lowest, y);
            highest = Math.max(highest, y);
        }
        assertTrue("fixture: bids rest on the books of the bonds it has a claim on, after the market's step",
                Double.isFinite(lowest));
        if (!Double.isFinite(lowest)) return;
        // Credit cheaper than every bid's yield: it would rather borrow.
        double before = cell.bonds();
        double raised = bm.sellForCell(cell, 1, Math.max(0, lowest - .01));
        close("a household whose credit is cheaper than every bid's yield sells nothing", raised, 0, 0);
        int waiting = 0;
        for (CorporateBond b : bm.getBonds()) {
            if (bm.bookOf(b).resting(me, OrderBook.Side.SELL) > 0) waiting++;
            bm.bookOf(b).withdraw(me);
        }
        assertTrue("...its asks wait on the books, over the bids", waiting > 0 && cell.bonds() == before);
        // Credit dearer than every bid's yield: the bids meet it.
        double need = Math.min(1, .25 * before);
        double savings = cell.savings();
        double per = bm.sellForCell(cell, need, highest + .05);
        assertTrue("a household whose credit costs more than the bids' yield sells into them", per > 0 && cell.bonds() < before);
        close("...raising what it was short, and banking what the higher bids paid past it", per, need, 1e-9);
        assertTrue("...the rest in its savings", cell.savings() >= savings);
        cell.savings += per;   // what the waterfall would have spent; here it is banked
        boolean atResting = true;
        int traded = 0;
        for (CorporateBond b : bm.getBonds()) {
            OrderBook book = bm.bookOf(b);
            double now = book.lastPrice();
            Double was = lastBefore.get(b.id());
            if (book.lastTradeMonth() != m || (was != null && (now == was || Double.isNaN(now)))) continue;
            traded++;
            if (!restingBids.get(b.id()).contains(now)) atResting = false;
        }
        assertTrue("...each at a price a bid was resting at, not its own ask", traded > 0 && atResting);
    }

    /* ============================ 5c. CAN'T PAY MEANS DEFAULT ============================ */

    /**
     * 0.7.12 round 4 (Jerus: "Can't pay means default"): a sector whose till
     * the month leaves short first sells what it holds, by the households'
     * waterfall rule - the other sectors' bonds, into the bids resting - then
     * asks the lender, and what nobody lends it defaults that month: its
     * overdraft closed, its debt sliced by instrument, its till at or above
     * nothing at the month's end, the audit closing. And one the lender
     * covers does not default. The unit form is CreditCheck's section 13.
     */
    static void cantPayInPlay(Game g) {
        out.println("\n--- 5c. in play: a rationing bank keeps the lines open, a short sector sells its bonds first,"
                + " and what nobody lends it defaults and is lent in the interim ---");
        BondMarket bm = g.getBondMarket();
        EconomyManager em = g.getEconomyManager();
        BusinessDebtManager credit = em.getBusinessDebtManager();
        Bank bank = g.getBank();
        double T = BusinessDebtManager.INSOLVENCY_TRIGGER;
        // The company holding the most of the other sectors' bonds; if none
        // does, one handed half the world's holding between months - a claim,
        // no money moved.
        String y = null;
        for (String s : Sectors.KEYS) {
            if (s.equals(ISSUER) || !(credit.getAssets(s) > 0) || !(bm.faceHeldBy(s) > 0)) continue;
            if (y == null || bm.faceHeldBy(s) > bm.faceHeldBy(y)) y = s;
        }
        if (y == null) {
            y = Sectors.RETAIL;
            for (CorporateBond b : bm.getBonds()) {
                if (b.issuer.equals(y) || !(b.world > 0)) continue;
                double f = .5 * b.world;
                b.world -= f;
                b.companies.merge(y, f, Double::sum);
            }
        }
        final String Y = y;
        g.getBusinessInvestment().holdSector(Y);
        // Past the line on the quarter the bank reads: owing 1.6 times what it
        // owns, taken on between months - a claim, no money moved.
        credit.issueLoan(Y, Math.max(0, 1.6 * credit.getAssets(Y) - credit.getPrincipal(Y)), g.getMonth());
        // ...and a sector over the shortfall desk's ceiling and under the
        // line: 1.2 times what it owns, the same way.
        String w = null;
        for (String s : Sectors.KEYS) {
            if (s.equals(Y) || s.equals(ISSUER) || !(credit.getAssets(s) > 0) || credit.isBorrowingBlocked(s)) continue;
            if (credit.getPrincipal(s) < credit.getAssets(s) * BusinessDebtManager.MAX_LOAN_TO_ASSETS
                    && (w == null || credit.getAssets(s) > credit.getAssets(w))) w = s;
        }
        final String W = w;
        g.getBusinessInvestment().holdSector(W);
        credit.issueLoan(W, Math.max(0, 1.2 * credit.getAssets(W) - credit.getPrincipal(W)), g.getMonth());
        // A month for the bank to book the two claims on its book and its
        // requirement - the capital rule reads them at the top of a month.
        assertTrue("fixture: the month the bank books the claims closes", closes(play(g)));
        // Three month-ends at 1.6 and 1.2, the quarter the bank reads, as it files them.
        for (int q = 0; q < BusinessDebtManager.STATEMENT_MONTHS; q++) {
            credit.recordStatement(Y, credit.getPrincipal(Y), credit.getPrincipal(Y) / 1.6);
            credit.recordStatement(W, credit.getPrincipal(W), credit.getPrincipal(W) / 1.2);
        }
        double held = bm.faceHeldBy(Y);
        // Short, between months: Y by twice what its bonds are worth at par,
        // W by a twentieth of what it owns.
        em.setSectorCash(Y, -2 * held - 1_000);
        double wShort = .05 * credit.getAssets(W);
        em.setSectorCash(W, -wShort);
        // ...and a sector the desk will lend to: the least levered of the
        // rest - one owing nothing if there is one - short a hundredth of what it owns.
        String z = null;
        for (String s : Sectors.KEYS) {
            if (s.equals(Y) || s.equals(W) || s.equals(ISSUER) || !(credit.getAssets(s) > 0) || credit.isBorrowingBlocked(s)) continue;
            if (z == null || credit.getPrincipal(s) / credit.getAssets(s) < credit.getPrincipal(z) / credit.getAssets(z)) z = s;
        }
        final String Z = z;
        double zShort = .01 * credit.getAssets(Z);
        em.setSectorCash(Z, -zShort);
        // The bank short of capital: its equity taken, between months, to
        // halfway between its minimum and its target - rationing.
        double taken = bank.equity() - (bank.minimumEquity() + bank.targetEquity()) / 2;
        bank.setCash(bank.getCash() - taken);
        assertTrue("fixture: " + Y + " holds other sectors' bonds and the bank's quarter reads it past the line",
                held > 0 && credit.getQuarterLeverage(Y) > T);
        assertTrue("fixture: " + W + " owes past the shortfall desk's ceiling and under the line",
                credit.getPrincipal(W) > credit.getAssets(W) * BusinessDebtManager.MAX_LOAN_TO_ASSETS
                        && credit.getQuarterLeverage(W) < T);
        assertTrue("fixture: " + Z + " owes under the shortfall desk's ceiling, and the bank is open",
                credit.getPrincipal(Z) + zShort < credit.getAssets(Z) * BusinessDebtManager.MAX_LOAN_TO_ASSETS
                        && credit.isLendingOpen());
        assertTrue("fixture: the bank is between its minimum and its target, and rations growth",
                taken > 0 && !Double.isInfinite(bank.lendingGrowthLimit()) && !bank.lendsOnlyToKeepBorrowersGoing());
        double zOwed = credit.getPrincipal(Z);
        MoneyAudit.Result r = play(g);
        out.printf("   the rule's growth %.4f; %s: sold $%,.1fk of bonds short, $%,.1fk unpaid (%s), interim $%,.1fk, refused %s;"
                        + " %s: $%,.1fk unpaid (%s), interim $%,.1fk; %s owed $%,.1fk, was lent $%,.1fk%n",
                credit.getCapitalGrowth(), Y, bm.getCompaniesSoldShort(), credit.getCannotPayShort(Y), credit.getCannotPayReason(Y),
                credit.getInterimLentThisMonth(Y), credit.getInterimRefusal(Y), W, credit.getCannotPayShort(W),
                credit.getCannotPayReason(W), credit.getInterimLentThisMonth(W), Z, zOwed, credit.getShortfallLentThisMonth(Z));
        assertTrue("fixture: the month ran on the bank's capital rule, rationing",
                credit.capitalRuleOn() && !credit.isKeepGoingOnly());
        assertTrue("a rationing bank covers a healthy sector's short month: a working-capital line",
                credit.getShortfallLentThisMonth(Z) >= zShort && credit.getCannotPayShort(Z) == 0);
        assertTrue("...past what the capital rule would have let its debt grow", credit.getLineLentPastOldRule(Z) > 0);
        assertTrue("...and refuses it a building its room does not cover: growth",
                !credit.canFundProject(Z, credit.capitalRoom(Z) + 1_000) && credit.wasProjectRefusedForCapital(Z));
        assertTrue("a short sector sells what it holds first: its bonds, into the bids resting",
                bm.faceHeldBy(Y) < held && bm.getCompaniesSoldShort() > 0);
        assertTrue("...then asks the lender, who will not lend past the line",
                credit.getCannotPayReason(Y) == BusinessDebtManager.ShortReason.PAST_DEFAULT_POINT);
        assertTrue("...and what it still could not pay defaults that month", credit.getCannotPayShort(Y) > 0);
        close("...its loans losing (1 - LOAN_RECOVERY) of what of them defaulted", credit.getWrittenOffThisMonth(Y),
                credit.getLoansDefaultedThisMonth(Y) * (1 - BusinessDebtManager.LOAN_RECOVERY), 1e-9);
        close("...its bonds (1 - BOND_RECOVERY) of theirs", credit.getBondWrittenOffThisMonth(Y),
                credit.getBondsDefaultedThisMonth(Y) * (1 - BusinessDebtManager.BOND_RECOVERY), 1e-9);
        assertTrue("fixture: after the write-down the lender reads it under the line, and lends", credit.getInterimRefusal(Y) == null);
        close("...the unpaid rest lent as an interim loan, ranked first, its fee on top", credit.getInterimLentThisMonth(Y),
                credit.getCannotPayShort(Y) / (1 - Bank.LOAN_FEE), 1e-6);
        assertTrue("...its till ends the month at or above nothing", em.getSectorCash(Y) >= 0);
        assertTrue("a sector over the ceiling is refused all but its interest reserve, and defaults on its own credit",
                credit.getCannotPayShort(W) > 0 && credit.getCannotPayReason(W) == BusinessDebtManager.ShortReason.CEILING);
        assertTrue("...lent the unpaid rest in the interim: the ceiling is not the interim lender's",
                credit.getInterimLentThisMonth(W) > 0 && em.getSectorCash(W) >= 0);
        close("nothing is forgiven on either: the audit's OverdraftForgiven is the backstop's alone",
                em.getOverdraftForgivenThisMonth(Y) + em.getOverdraftForgivenThisMonth(W), 0, 0);
        cents("...and it is the month's forgiven overdraft, as the backstop's always was", line(r, "+ sectors OverdraftForgiven"),
                em.getOverdraftForgiven());
        assertTrue("...and the month closes", closes(r));
        // The bank's capital put back, between months, for the sections after.
        bank.setCash(bank.getCash() + taken);
        interimSector = Y;
    }

    /** The sector §5c lent an interim loan to, for the save's round trip (§8). */
    static String interimSector;

    /* ============================ 5d. BUY ONLY WHAT IT CAN PAY FOR ============================ */

    /**
     * 0.7.12 round 6 (Jerus: "Buy only what it can pay for"; Kashyap, Lamont &
     * Stein, QJE 109(3), 1994): a sector's orders for stock - the shops'
     * shelves and every fleet - are limited to its cash plus the credit it can
     * get that month, after the bills it cannot avoid, and the cash-flow test
     * still reads those bills. On its own city, so the sections after it read
     * the market's as they always have: the shops, under their ceiling, then
     * over it and short, then owing principal they cannot pay.
     */
    static void buyOnlyWhatItCanPayFor(Path root) {
        out.println("\n--- 5d. buy only what it can pay for: a sector short of credit restocks only as far as its cash"
                + " and credit reach ---");
        Game g = city(root, "buy", 24);
        EconomyManager em = g.getEconomyManager();
        BusinessDebtManager credit = em.getBusinessDebtManager();
        String R = Sectors.RETAIL;
        ham.citybuildersim.sectors.Retail shops = g.getSectors().retail();
        g.getBusinessInvestment().holdSector(R);

        // UNDER ITS CEILING: its shelf cut, between months, to a month and a
        // fifth of what it sells, so the month's sales are whole and its
        // order is most of its cover.
        int sells = Math.max(1, shops.getProductsSold());
        shops.setStoreInventory((int) Math.ceil(1.2 * sells));
        assertTrue("fixture: the shops owe under the shortfall desk's ceiling",
                credit.getPrincipal(R) < credit.getAssets(R) * BusinessDebtManager.MAX_LOAN_TO_ASSETS);
        assertTrue("the month closes", closes(play(g)));
        double[] under = stockBought(shops);
        out.printf("   under the ceiling: ordered $%,.1fk of stock, could pay for $%,.1fk, bought $%,.1fk%n",
                shops.getOrderValue(), shops.getPurchaseBudget(), under[0]);
        assertTrue("fixture: it ordered stock, and could pay for all of it", shops.getOrderValue() > 0
                && shops.getPurchaseBudget() >= shops.getOrderValue());
        assertTrue("a sector under its ceiling buys as before: every order placed whole",
                !shops.wasPurchaseLimited() && shops.getPurchasesForgone() == 0);
        close("...and filled whole", under[1], under[2], 1e-9);

        // OVER ITS CEILING AND SHORT: owing 1.2 times what it owns - a claim
        // taken on between months, the quarter the bank reads with it - and
        // its till emptied, between months, with the shelf cut again.
        credit.issueLoan(R, Math.max(0, 1.2 * credit.getAssets(R) - credit.getPrincipal(R)), g.getMonth());
        assertTrue("fixture: the month the bank books the claim closes", closes(play(g)));
        for (int q = 0; q < BusinessDebtManager.STATEMENT_MONTHS; q++) {
            credit.recordStatement(R, credit.getPrincipal(R), credit.getPrincipal(R) / 1.2);
        }
        sells = Math.max(1, shops.getProductsSold());
        shops.setStoreInventory((int) Math.ceil(1.2 * sells));
        em.setSectorCash(R, 0);
        assertTrue("fixture: the shops owe past the shortfall desk's ceiling and under the line on their quarter",
                credit.getPrincipal(R) > credit.getAssets(R) * BusinessDebtManager.MAX_LOAN_TO_ASSETS
                        && credit.getQuarterLeverage(R) < BusinessDebtManager.INSOLVENCY_TRIGGER);
        assertTrue("the month closes", closes(play(g)));
        double[] over = stockBought(shops);
        double budget = shops.getPurchaseBudget(), ordered = shops.getOrderValue();
        out.printf("   over the ceiling, till emptied: ordered $%,.1fk of stock, could pay for $%,.1fk, bought $%,.1fk,"
                + " did not buy $%,.1fk%n", ordered, budget, over[0], shops.getPurchasesForgone());
        assertTrue("fixture: it could pay for some of its order and not all of it", budget > 0 && budget < ordered);
        assertTrue("a sector over its ceiling and short buys only what its cash and the credit it can get cover",
                over[0] > 0 && over[0] <= budget * (1 + 1e-9));
        assertTrue("...and does not order the rest", shops.wasPurchaseLimited() && shops.getPurchasesForgone() > 0);
        MoneyAudit.Result r = play(g);
        out.printf("   the settle after: $%,.1fk unpaid (%s), the month asked $%,.1fk%n", credit.getCannotPayShort(R),
                credit.getCannotPayReason(R), credit.getMonthObligations(R));
        check0("...so it does not default on the stock it did not buy", credit.getCannotPayShort(R));
        assertTrue("the month closes", closes(r));

        // A BILL IT CANNOT AVOID: a quarter of what it owns falling due at the
        // next settle - a claim, between months, the quarter read with it -
        // which over its ceiling it cannot roll.
        double due = .25 * credit.getAssets(R);
        credit.getLoans().add(new BusinessLoan(R, due, 2, g.getMonth(), credit.getRate(R)));
        for (int q = 0; q < BusinessDebtManager.STATEMENT_MONTHS; q++) {
            credit.recordStatement(R, credit.getPrincipal(R), credit.getPrincipal(R) / 1.45);
        }
        sells = Math.max(1, shops.getProductsSold());
        shops.setStoreInventory((int) Math.ceil(1.2 * sells));
        assertTrue("fixture: under the line on its quarter, still", credit.getQuarterLeverage(R) < BusinessDebtManager.INSOLVENCY_TRIGGER);
        assertTrue("the month closes", closes(play(g)));
        double[] owing = stockBought(shops);
        out.printf("   owing $%,.1fk that falls due: could pay for $%,.1fk of stock, bought $%,.1fk%n",
                due, shops.getPurchaseBudget(), owing[0]);
        assertTrue("a sector whose unavoidable bills outrun its cash and credit can pay for no stock",
                shops.getPurchaseBudget() == 0 && shops.wasPurchaseLimited());
        check0("...and buys none", owing[0]);
        r = play(g);
        out.printf("   the settle after: $%,.1fk unpaid (%s) of $%,.1fk the month asked%n", credit.getCannotPayShort(R),
                credit.getCannotPayReason(R), credit.getMonthObligations(R));
        assertTrue("the same sector still defaults on a bill it cannot avoid: principal that fell due",
                credit.getCannotPayShort(R) > 0);
        assertTrue("...on no more than the principal: nothing it bought is in what it could not pay",
                credit.getCannotPayShort(R) <= due * (1 + 1e-9) && shops.statement().inputs - shops.statement().paidEarlier <= 1e-9);
        assertTrue("the month closes", closes(r));
    }

    /** What a sector bought of its stock in the month just played: {its value, the units it asked for, the units it got}. */
    static double[] stockBought(Sector s) {
        double value = 0, asked = 0, got = 0;
        for (Map.Entry<Good, Sector.Split> e : s.pending().bought.entrySet()) {
            if (s.hasPantry(e.getKey())) value += e.getValue().total();
        }
        for (Good gd : Good.values()) {
            if (!s.hasPantry(gd) || !s.isUser(gd)) continue;
            Sector.Input in = s.input(gd);
            asked += in.bid;
            got += in.boughtLocal + in.imported;
        }
        return new double[] { value, asked, got };
    }

    static void check0(String label, double actual) {
        boolean ok = actual == 0;
        if (!ok) fails++;
        out.printf("%-92s %s  %,.9f%n", label, ok ? "OK" : "FAIL", actual);
    }

    /* ============================ 5. RECOVERIES BY INSTRUMENT ============================ */

    static void seniorityOnTheLender() {
        out.println("\n--- 5. recoveries by instrument: a slice takes a loan's loss off the loans and a bond's off the bonds ---");
        String S = Sectors.MANUFACTURING;
        BusinessDebtManager lender = new BusinessDebtManager();
        double[] bonds = { 600 };
        lender.setBondMarket(new BusinessDebtManager.BondBook() {
            @Override public double principal(String sector) { return sector.equals(S) ? bonds[0] : 0; }
            @Override public double monthlyCoupon(String sector) { return 0; }
            @Override public double writeDown(String sector, double scale) {
                if (!sector.equals(S)) return 0;
                double gone = bonds[0] * (1 - scale);
                bonds[0] *= scale;
                return gone;
            }
        }, null);
        lender.setAssets(S, 1_000);
        lender.issueLoan(S, 900, 0);
        double principal = lender.getPrincipal(S);
        close("fixture: $900k of loans and $600k of bonds, 1.5 times its assets", principal, 1_500, 1e-12);
        double share = BusinessDebtManager.monthlyDefaultShare(1.5);
        double s = .6;
        double loans = lender.defaultSlice(S);
        double bondsLost = lender.getBondWrittenOffThisMonth(S);
        // 0.7.12 round 2 (Jerus: "Real averages by type"): each instrument its
        // own recovery, whatever the mix - no priority rule on top.
        close("the loans lose h x (1 - LOAN_RECOVERY) of theirs", loans,
                900 * share * (1 - BusinessDebtManager.LOAN_RECOVERY), 1e-9);
        close("...the bonds h x (1 - BOND_RECOVERY) of theirs", bondsLost,
                600 * share * (1 - BusinessDebtManager.BOND_RECOVERY), 1e-9);
        close("...so the loans recover LOAN_RECOVERY of what defaulted", 1 - loans / (900 * share),
                BusinessDebtManager.LOAN_RECOVERY, 1e-12);
        close("...and the bonds BOND_RECOVERY", 1 - bondsLost / (600 * share), BusinessDebtManager.BOND_RECOVERY, 1e-12);
        close("what defaulted is on the lender's record by class",
                lender.getLoansDefaultedThisMonth(S) + lender.getBondsDefaultedThisMonth(S), principal * share, 1e-9);
        // The same at a different mix: all loans, then mostly bonds - the mix moves the total, not each class's loss.
        for (double loansOwed : new double[] { 1_500, 300 }) {
            BusinessDebtManager mix = new BusinessDebtManager();
            double[] mixBonds = { 1_500 - loansOwed };
            mix.setBondMarket(new BusinessDebtManager.BondBook() {
                @Override public double principal(String sector) { return sector.equals(S) ? mixBonds[0] : 0; }
                @Override public double monthlyCoupon(String sector) { return 0; }
                @Override public double writeDown(String sector, double scale) {
                    if (!sector.equals(S)) return 0;
                    double gone = mixBonds[0] * (1 - scale);
                    mixBonds[0] *= scale;
                    return gone;
                }
            }, null);
            mix.setAssets(S, 1_000);
            mix.issueLoan(S, loansOwed, 0);
            double l = mix.defaultSlice(S), b = mix.getBondWrittenOffThisMonth(S);
            close("at " + (int) (loansOwed / 15) + "% loans the loans still lose h x (1 - LOAN_RECOVERY)", l,
                    loansOwed * share * (1 - BusinessDebtManager.LOAN_RECOVERY), 1e-9);
            close("...and the bonds h x (1 - BOND_RECOVERY)", b,
                    (1_500 - loansOwed) * share * (1 - BusinessDebtManager.BOND_RECOVERY), 1e-9);
        }
        out.printf("   loans recover %.0f%% (Moody's and S&P, 1987-2024: first-lien bank loans 70-80%%),"
                + " bonds %.0f%% (senior unsecured, 40-50%%)%n",
                BusinessDebtManager.LOAN_RECOVERY * 100, BusinessDebtManager.BOND_RECOVERY * 100);
        assertTrue("both inside the sources' ranges", BusinessDebtManager.LOAN_RECOVERY >= .70
                && BusinessDebtManager.LOAN_RECOVERY <= .80 && BusinessDebtManager.BOND_RECOVERY >= .40
                && BusinessDebtManager.BOND_RECOVERY <= .50);

        // The backstop: nothing left, nothing recovered, both classes to nothing.
        BusinessDebtManager dead = new BusinessDebtManager();
        double[] deadBonds = { 400 };
        dead.setBondMarket(new BusinessDebtManager.BondBook() {
            @Override public double principal(String sector) { return sector.equals(S) ? deadBonds[0] : 0; }
            @Override public double monthlyCoupon(String sector) { return 0; }
            @Override public double writeDown(String sector, double scale) {
                if (!sector.equals(S)) return 0;
                double gone = deadBonds[0] * (1 - scale);
                deadBonds[0] *= scale;
                return gone;
            }
        }, null);
        dead.issueLoan(S, 500, 0);
        dead.setAssets(S, 0);
        double lost = dead.restructure(S);
        close("the backstop on a sector with nothing left writes its loans off whole", lost, 500, 1e-9);
        close("...and its bonds", dead.getBondWrittenOffThisMonth(S), 400, 1e-9);
        close("...leaving it owing nothing on either", dead.getPrincipal(S), 0, 1e-9);
    }

    static void seniorityInPlay(Game g) {
        out.println("\n--- 5b. a slice in play: the bondholders' loss reaches every class, and the world's crosses the border ---");
        BondMarket bm = g.getBondMarket();
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        HouseholdBalance hb = g.getHouseholdBalance();
        // Owing 1.2 times what it owns, taken on between months: a claim, no money moved (MortgageCheck's fixture).
        g.getBusinessInvestment().holdSector(ISSUER);
        double claim = Math.max(0, 1.2 * credit.getAssets(ISSUER) - credit.getPrincipal(ISSUER));
        credit.issueLoan(ISSUER, claim, g.getMonth());
        MoneyAudit.Result r = play(g);
        double loanLoss = credit.getWrittenOffThisMonth(ISSUER), bondLoss = credit.getBondWrittenOffThisMonth(ISSUER);
        assertTrue("fixture: " + ISSUER + "'s firms defaulted a slice, its bonds among it", bondLoss > 0 && loanLoss > 0
                && !credit.wasRestructuredThisMonth(ISSUER));
        double loansDefaulted = credit.getLoansDefaultedThisMonth(ISSUER), bondsDefaulted = credit.getBondsDefaultedThisMonth(ISSUER);
        close("the loans lost (1 - LOAN_RECOVERY) of what of them defaulted", loanLoss,
                loansDefaulted * (1 - BusinessDebtManager.LOAN_RECOVERY), 1e-9);
        close("...and the bonds (1 - BOND_RECOVERY) of theirs", bondLoss,
                bondsDefaulted * (1 - BusinessDebtManager.BOND_RECOVERY), 1e-9);
        out.printf("   loans recovered %.1f%% of $%,.1fk defaulted, bonds %.1f%% of $%,.1fk%n",
                (1 - loanLoss / loansDefaulted) * 100, loansDefaulted, (1 - bondLoss / bondsDefaulted) * 100, bondsDefaulted);
        assertTrue("...the loans recovering more than the bonds", loanLoss / loansDefaulted < bondLoss / bondsDefaulted);
        double everySector = 0;
        for (String s : credit.sectors()) everySector += credit.getBondWrittenOffThisMonth(s);
        out.printf("   the month's bond losses, every sector that defaulted: households $%,.2fk, the bank $%,.2fk,"
                + " companies $%,.2fk, the world $%,.2fk%n", bm.getLossHouseholds(), bm.getLossBank(), bm.getLossCompanies(),
                bm.getWorldWrittenOff());
        close("the bondholders lost it between them: households, the bank, the companies and the world",
                bm.getLossHouseholds() + bm.getLossBank() + bm.getLossCompanies() + bm.getWorldWrittenOff(), everySector, 1e-9);
        assertTrue("fixture: every class held some of what defaulted", bm.getLossHouseholds() > 0 && bm.getWorldWrittenOff() > 0
                && bm.getLossBank() > 0 && bm.getLossCompanies() > 0);
        cents("the world's loss is declared across the border, a valuation with no cash in it", line(r, "+ bonds WrittenOffAbroad"), bm.getWorldWrittenOff());
        assertTrue("...on the valuation line the foreign position reads", r.valuationIn >= bm.getWorldWrittenOff() - 1e-9);
        assertTrue("...and the month closes", closes(r));

        // The same two numbers in the allowance, a loan's price and a bond's value.
        close("a bondholder's expected loss is the default rate times (1 - BOND_RECOVERY)",
                bm.expectedLoss(ISSUER), bm.defaultRate(ISSUER, 0, 0) * (1 - BusinessDebtManager.BOND_RECOVERY), 1e-15);
        double q = credit.quarterPrincipal(ISSUER), qa = credit.quarterAssets(ISSUER);
        /*
         * ...ON WHAT THE BANK HELD WHEN IT PROVIDED. The allowance is struck
         * at the month's provision; the market's step later in the month can
         * buy or sell the sector's bonds (in this fixture the bank bought
         * $1.4k more of them in round 2), so the bond holding is the one the
         * bank read then (Bank.getAllowanceReading()), and what it owes the
         * bank is asserted against today's - nothing moves that after it.
         */
        double[] read = g.getBank().getAllowanceReading(ISSUER);
        assertTrue("fixture: the bank read the sector at its provision", read != null && read.length >= 6);
        close("...what it owes the bank as it stands at the month's end", read[2], credit.getUninsuredPrincipal(ISSUER), 1e-9);
        close("...the bank's allowance on the sector its loans at (1 - LOAN_RECOVERY) and its bonds at (1 - BOND_RECOVERY)",
                g.getBank().getSectorAllowance(ISSUER),
                Bank.sectorAllowance(read[2], q, qa, 1 - BusinessDebtManager.LOAN_RECOVERY)
                        + Bank.sectorAllowance(read[4], q, qa, 1 - BusinessDebtManager.BOND_RECOVERY), 1e-9);
        close("...and a loan's own risk in its price the curve at (1 - LOAN_RECOVERY)", credit.getRiskSpread(ISSUER),
                Math.max(0, (1 - BusinessDebtManager.LOAN_RECOVERY) * BusinessDebtManager.defaultProbability(
                        BusinessDebtManager.pricingLeverage(q, qa)) - Bank.BASE_LOSS_RATE), 1e-15);

        // Every class by the same share, and every cell's own face with it (round 2): a write-down by hand, between months.
        CorporateBond most = null;
        for (CorporateBond b : bm.getBonds(ISSUER)) if (most == null || b.face() > most.face()) most = b;
        double[] was = { most.households(), most.bank(), most.bankCost(), most.world(), most.companiesTotal() };
        double claims = hb.totalBonds(), lossH = bm.getLossHouseholds(), lossW = bm.getWorldWrittenOff();
        double worldOfIssuer = 0;
        for (CorporateBond b : bm.getBonds(ISSUER)) worldOfIssuer += b.world();
        double gone = bm.writeDown(ISSUER, .9);
        assertTrue("written down by a tenth, every holder of the bond loses a tenth: households, the bank at its cost, the world, the companies",
                Math.abs(most.households() - .9 * was[0]) <= 1e-9 * was[0] && Math.abs(most.bank() - .9 * was[1]) <= 1e-9 * (1 + was[1])
                        && Math.abs(most.bankCost() - .9 * was[2]) <= 1e-9 * (1 + was[2])
                        && Math.abs(most.world() - .9 * was[3]) <= 1e-9 * was[3]
                        && Math.abs(most.companiesTotal() - .9 * was[4]) <= 1e-9 * (1 + was[4]));
        close("...the households' claims falling by exactly their loss", claims - hb.totalBonds(), bm.getLossHouseholds() - lossH, 1e-9);
        close("...the world's on the border's line", bm.getWorldWrittenOff() - lossW, .1 * worldOfIssuer, 1e-9);
        assertTrue("...and it was a tenth of the sector's bonds", gone > 0);

    }

    /* ============================ 6. CONCENTRATION ============================ */

    static void concentration(Game g) {
        out.println("\n--- 6. concentration: Basel's capital, a charge that rises with a sector's share and falls as the book spreads ---");
        // Basel's published risk weights for corporate exposures at LGD 45% and 2.5 years (BCBS, "An Explanatory
        // Note on the Basel II IRB Risk Weight Functions", July 2005): risk weight = K x 12.5.
        double[][] table = { { .0003, 14.44 }, { .001, 29.65 }, { .01, 92.32 }, { .03, 128.44 }, { .10, 193.09 }, { .20, 238.23 } };
        double worst = 0;
        for (double[] row : table) {
            double rw = Bank.irbCapital(row[0], .45, Bank.irbCorrelation(row[0])) * 12.5 * 100;
            worst = Math.max(worst, Math.abs(rw - row[1]));
        }
        close("the IRB capital gives Basel's own table of corporate risk weights, to its two decimals", worst, 0, .005);
        close("the correlation at the soundest firms is 24%", Bank.irbCorrelation(0), Bank.IRB_CORRELATION_HIGH, 1e-15);
        close("...and a book in one industry R x SECTOR_CORRELATION_MULTIPLIER", Bank.effectiveCorrelation(.01, 1),
                Bank.irbCorrelation(.01) * Bank.SECTOR_CORRELATION_MULTIPLIER, 1e-15);

        double pd = BondMarket.DEFAULT_RATE_FLOOR, lgd = BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT;
        Bank b = new Bank();
        double last = Double.NEGATIVE_INFINITY;
        boolean rises = true;
        out.println("      one industry's share of the book, and the capital a dollar more of it adds at the minimum:");
        for (double a : new double[] { 25, 50, 100, 200, 400, 800 }) {
            Map<String, Bank.Exposure> book = new LinkedHashMap<>();
            book.put("A", new Bank.Exposure(a, pd, lgd));
            book.put("B", new Bank.Exposure(100, pd, lgd));
            book.put("C", new Bank.Exposure(100, pd, lgd));
            b.setConcentration(book);
            double per = b.concentrationPerDollar("A");
            out.printf("         %4.0f%%   %+.4f%n", a / (a + 200) * 100, per);
            if (!(per > last)) rises = false;
            last = per;
        }
        assertTrue("a dollar lent to an industry carries more capital the more of the book it already is", rises);
        assertTrue("...and a small one's carries less than none: it diversifies the book",
                concentrationAt(b, 25, 100, 100) < 0);
        last = Double.POSITIVE_INFINITY;
        boolean falls = true;
        double lastH = Double.POSITIVE_INFINITY;
        for (int k = 1; k <= 6; k++) {
            Map<String, Bank.Exposure> book = new LinkedHashMap<>();
            for (int j = 0; j < k; j++) book.put(String.valueOf((char) ('A' + j)), new Bank.Exposure(100, pd, lgd));
            b.setConcentration(book);
            double per = b.concentrationPerDollar("A");
            if (!(per < last) || !(b.getConcentrationHerfindahl() < lastH)) falls = false;
            last = per;
            lastH = b.getConcentrationHerfindahl();
        }
        assertTrue("...and falls as the book spreads over more industries, its Herfindahl index with it", falls);
        Map<String, Bank.Exposure> mixed = new LinkedHashMap<>();
        mixed.put("A", new Bank.Exposure(500, .02, lgd));
        mixed.put("B", new Bank.Exposure(200, .005, .3));
        mixed.put("C", new Bank.Exposure(50, .10, lgd));
        b.setConcentration(mixed);
        double sum = 0;
        for (Map.Entry<String, Bank.Exposure> e : mixed.entrySet()) sum += e.getValue().amount() * b.concentrationPerDollar(e.getKey());
        close("by the Euler rule the sectors' shares add back up to the book's add-on", sum, b.getConcentrationAddOn(), 1e-9);
        close("...which is weight on the book at the minimum ratio", b.getConcentrationWeighted(),
                b.getConcentrationAddOn() / Bank.CAPITAL_RATIO, 1e-12);

        // In the played city: in the price, in the requirement, and the weight table foots.
        Bank bank = g.getBank();
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        double policy = g.getDebtManager().getPolicyRate();
        String top = null;
        double topShare = -1;
        for (String s : credit.sectors()) {
            Bank.Exposure x = bank.getExposure(s);
            if (x != null && x.amount() > topShare) { topShare = x.amount(); top = s; }
        }
        assertTrue("fixture: the played bank's book has a largest industry", top != null && topShare > 0);
        double conc = bank.concentrationPerDollar(top) * bank.capitalTarget() / Bank.CAPITAL_RATIO;
        close("a loan's capital per dollar is the target on its weight plus the sector's add-on, carried at the target",
                bank.capitalPerDollar(Bank.RISK_BUSINESS, top),
                Math.max(Bank.RISK_BUSINESS * bank.capitalTarget() + conc, bank.leverageTarget()), 1e-15);
        close("...and its charge is that capital at the owners' return over the money - the one formula",
                bank.concentrationCharge(policy, Bank.PRIME_TERM_MONTHS, top),
                bank.capitalCharge(policy, Bank.PRIME_TERM_MONTHS, Bank.RISK_BUSINESS, top)
                        - bank.capitalCharge(policy, Bank.PRIME_TERM_MONTHS, Bank.RISK_BUSINESS), 1e-15);
        Map<String, Double> charges = new LinkedHashMap<>();
        for (String s : credit.sectors()) charges.put(s, bank.concentrationCharge(policy, Bank.PRIME_TERM_MONTHS, s));
        credit.setConcentrationCharges(charges);
        credit.updateRates();
        close("pushed to the lender, it is the part of the sector's rate over prime, its own risk and its record",
                credit.getRate(top) - credit.getPrimeRate() - credit.getRiskSpread(top) - credit.getRecordSurcharge(top),
                charges.get(top), 1e-12);
        double rows = 0, concRow = Double.NaN;
        for (Bank.WeightRow row : bank.weightTable()) {
            rows += row.weighted();
            if (row.book() == Bank.Book.CONCENTRATION) concRow = row.weighted();
        }
        close("the add-on is in the bank's requirement: its own row of the weight table", concRow, bank.getConcentrationWeighted(), 1e-12);
        close("...and the table still foots to the weighted book the requirement reads (BankCheck 13)", rows, bank.getWeightedBook(), 1e-9);
        close("...its minimum the ratio on all of it", bank.minimumEquity(),
                Math.max(Bank.CAPITAL_RATIO * bank.getWeightedBook(), bank.minimumEquity()), 1e-9);
    }

    static double concentrationAt(Bank b, double a, double b2, double c) {
        Map<String, Bank.Exposure> book = new LinkedHashMap<>();
        double pd = BondMarket.DEFAULT_RATE_FLOOR, lgd = BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT;
        book.put("A", new Bank.Exposure(a, pd, lgd));
        book.put("B", new Bank.Exposure(b2, pd, lgd));
        book.put("C", new Bank.Exposure(c, pd, lgd));
        b.setConcentration(book);
        return b.concentrationPerDollar("A");
    }

    /* ============================ 8. SAVE AND LOAD ============================ */

    static void saveAndLoad(Game g, Path root) throws Exception {
        out.println("\n--- 8. save and load: the bonds, who holds them and the resting orders; and a save from before them ---");
        BondMarket bm = g.getBondMarket();
        // A month of the model's own first, after the harness's hand in the sections above.
        play(g);
        int slot = 10;
        quietly(() -> g.saveGame(slot, "bonds"));
        Game[] back = new Game[1];
        quietly(() -> {
            back[0] = new Game(g.getGameFiles());
            back[0].loadGameSave(slot);
        });
        Game twin = back[0];
        assertTrue("it loads", twin.getLoadFailure() == null && twin.getMonth() == g.getMonth());
        BondMarket tb = twin.getBondMarket();
        assertTrue("fixture: the city has bonds outstanding and orders resting", bm.getBonds().size() > 0 && resting(bm) > 0);
        assertTrue("the same bonds come back", sameBonds(bm, tb));
        // ...and the interim loans (0.7.12, round 5), by their own type, so a reload knows their rank.
        BusinessDebtManager lc = g.getEconomyManager().getBusinessDebtManager();
        BusinessDebtManager tc = twin.getEconomyManager().getBusinessDebtManager();
        assertTrue("fixture: an interim loan is outstanding (5c)", interimSector != null && lc.getInterimCount(interimSector) > 0);
        close("the interim loans come back, by their own type: what a sector owes on them",
                tc.getInterimPrincipal(interimSector), lc.getInterimPrincipal(interimSector), 1e-9);
        assertTrue("...as many of them, ranked first, over every sector",
                tc.getInterimCount() == lc.getInterimCount() && Math.abs(tc.getInterimPrincipal() - lc.getInterimPrincipal()) < 1e-6);
        assertTrue("...with the same orders resting on their books, in the same order", sameBooks(bm, tb));
        close("...the households' bonds on them", twin.getHouseholdBalance().totalBonds(), g.getHouseholdBalance().totalBonds(), 1e-9);
        // Each cell's own, by the cell's name (round 2).
        int cellsHolding = 0;
        boolean sameByCell = true;
        for (Household c : g.getHouseholdBalance().cells()) {
            Household t = twin.getHouseholdBalance().cellByKey(c.key());
            if (!c.bondFace.isEmpty()) cellsHolding++;
            if (t == null || !t.bondFace.equals(c.bondFace) || t.bonds() != c.bonds()) sameByCell = false;
        }
        assertTrue("fixture: " + cellsHolding + " cells hold bonds of their own", cellsHolding > 1);
        assertTrue("...and each comes back by the cell's name, bond by bond, to the bit", sameByCell);
        JsonObject saved = JsonParser.parseString(Files.readString(g.getGameFiles().saveFile(slot))).getAsJsonObject();
        JsonObject byCell = saved.getAsJsonObject("householdBondsByCell");
        boolean named = byCell != null && byCell.size() == cellsHolding;
        if (named) for (String k : byCell.keySet()) if (g.getHouseholdBalance().cellByKey(k) == null) named = false;
        assertTrue("...kept in the save under the cells' names", named);
        close("...their value a unit of face", twin.getHouseholdBalance().getBondRatio(), g.getHouseholdBalance().getBondRatio(), 1e-12);
        close("...the bank's bonds on its book", twin.getBank().getBondBook(), g.getBank().getBondBook(), 1e-9);
        close("...and its concentration add-on", twin.getBank().getConcentrationAddOn(), g.getBank().getConcentrationAddOn(), 1e-9);
        close("...the lender's bond write-offs on the record",
                twin.getEconomyManager().getBusinessDebtManager().getBondWrittenOffTotal(ISSUER),
                g.getEconomyManager().getBusinessDebtManager().getBondWrittenOffTotal(ISSUER), 1e-9);
        close("...and last month's book for the screens", tb.getLastFilled(), bm.getLastFilled(), 1e-9);
        /*
         * A MONTH ON, BOTH PLAY AND CLOSE - NOT TO THE CENT ALIKE. A reloaded
         * city does not play its next month exactly as the one it was saved
         * from, and that is not the bonds': in this fixture 0.7.11 as deployed
         * already parts in the first month on the landlords' till (Real
         * Estate $1,033k against $675k) and the households' savings, with no
         * bond in the city. Reported with 0.7.12; LongPlaytest's round trip
         * compares the state loaded, which is what the lines above do.
         */
        assertTrue("a month on, both the city and its reload play and close", closes(play(g)) && closes(play(twin)));

        // A SAVE FROM ROUND 1: one pool per bond, a claim per cell at face
        // (the cells' Household.bonds). The same save without the cells' own
        // holdings is that save; it loads into the cells by the claims.
        quietly(() -> g.saveGame(slot, "pooled"));
        JsonObject pooled = JsonParser.parseString(Files.readString(g.getGameFiles().saveFile(slot))).getAsJsonObject();
        pooled.remove("householdBondsByCell");
        Files.writeString(g.getGameFiles().saveFile(slot), pooled.toString());
        quietly(() -> {
            back[0] = new Game(g.getGameFiles());
            back[0].loadGameSave(slot);
        });
        Game migrated = back[0];
        assertTrue("a round-1 save, its households' bonds one pool, loads", migrated.getLoadFailure() == null);
        HouseholdBalance mh = migrated.getHouseholdBalance();
        double claims = 0;
        for (Household c : g.getHouseholdBalance().cells()) claims += c.bonds() * c.households();
        boolean byClaim = true, poolKept = true;
        for (Household c : g.getHouseholdBalance().cells()) {
            Household m = mh.cellByKey(c.key());
            if (m == null) { byClaim = false; continue; }
            if (Math.abs(m.bonds() - c.bonds()) > 1e-9 * Math.max(1, c.bonds())) byClaim = false;
            for (CorporateBond x : migrated.getBondMarket().getBonds()) {
                double expect = x.households() * c.bonds() * c.households() / claims;
                if (Math.abs(m.bondFace(x.id()) * m.households() - expect) > 1e-9 * Math.max(1, expect)) byClaim = false;
            }
        }
        for (CorporateBond x : g.getBondMarket().getBonds()) {
            CorporateBond y = migrated.getBondMarket().bond(x.id());
            if (y == null || Math.abs(y.households() - x.households()) > 1e-9 * Math.max(1, x.households())
                    || Math.abs(mh.bondFaceHeld(x.id()) - x.households()) > 1e-9 * Math.max(1, x.households())) poolKept = false;
        }
        assertTrue("...each cell holding the share of every bond its claim was of the pool, its total its claim", byClaim);
        assertTrue("...every bond's households' face the pool it was, and the cells' sum", poolKept);
        int brokenBefore = brokenMonths;
        play(migrated);
        assertTrue("...and it plays, the audit closing", brokenMonths == brokenBefore);

        // A save from before 0.7.12.
        Game young = city(root, "young", 6);
        quietly(() -> young.saveGame(slot, "before the bonds"));
        JsonObject json = JsonParser.parseString(Files.readString(young.getGameFiles().saveFile(slot))).getAsJsonObject();
        // (and round 2's key for the cells' own bonds, which no such save has either)
        for (String k : new String[] { "bondMarket", "bondWrittenOff", "householdBondRatio", "householdBondsByCell" }) json.remove(k);
        JsonArray keys = json.getAsJsonArray("householdCellKeys");
        JsonArray cells = json.getAsJsonArray("householdCells");
        int nk = keys.size(), slots = (cells.size() - 3) / nk;
        assertTrue("fixture: the save's cells are today's width", slots == HouseholdBalance.CELL_SLOTS);
        JsonArray shorter = new JsonArray();
        for (int c = 0; c < nk; c++) for (int s = 0; s < slots - 1; s++) shorter.add(cells.get(c * slots + s));
        for (int t = nk * slots; t < cells.size(); t++) shorter.add(cells.get(t));
        json.add("householdCells", shorter);
        Files.writeString(young.getGameFiles().saveFile(slot), json.toString());
        quietly(() -> {
            back[0] = new Game(young.getGameFiles());
            back[0].loadGameSave(slot);
        });
        Game old = back[0];
        assertTrue("a save from before the bonds loads", old.getLoadFailure() == null && old.getMonth() > 1);
        assertTrue("...with no bonds, no books and nobody holding any", old.getBondMarket().getBonds().isEmpty()
                && old.getHouseholdBalance().totalBonds() == 0 && old.getBank().getBondBook() == 0);
        close("...the households' paper where it was", old.getHouseholdBalance().totalPaper(), young.getHouseholdBalance().totalPaper(), 1e-9);
        int broken = brokenMonths;
        for (int i = 0; i < 3; i++) play(old);
        assertTrue("...and it plays, the audit closing every month", brokenMonths == broken);
    }

    static int resting(BondMarket bm) {
        int n = 0;
        for (CorporateBond b : bm.getBonds()) n += bm.bookOf(b).bids().size() + bm.bookOf(b).asks().size();
        return n;
    }

    static boolean sameBonds(BondMarket a, BondMarket b) {
        if (a.getBonds().size() != b.getBonds().size()) return false;
        for (int i = 0; i < a.getBonds().size(); i++) {
            CorporateBond x = a.getBonds().get(i), y = b.getBonds().get(i);
            if (x.id() != y.id() || !x.issuer().equals(y.issuer()) || x.face() != y.face() || x.coupon() != y.coupon()
                    || x.issueMonth() != y.issueMonth() || x.maturityMonth() != y.maturityMonth()
                    || x.households() != y.households() || x.bank() != y.bank() || x.bankCost() != y.bankCost()
                    || x.world() != y.world() || x.issued() != y.issued() || x.writtenOff() != y.writtenOff()
                    || !x.companies().equals(y.companies())) return false;
        }
        return a.toState().nextId == b.toState().nextId;
    }

    static boolean sameBooks(BondMarket a, BondMarket b) {
        for (CorporateBond x : a.getBonds()) {
            CorporateBond y = b.bond(x.id());
            if (y == null) return false;
            OrderBook p = a.bookOf(x), q = b.bookOf(y);
            if (!sameOrders(p.bids(), q.bids()) || !sameOrders(p.asks(), q.asks())) return false;
            if (!(Double.isNaN(p.lastPrice()) && Double.isNaN(q.lastPrice())) && p.lastPrice() != q.lastPrice()) return false;
        }
        return true;
    }

    static boolean sameOrders(List<OrderBook.Order> a, List<OrderBook.Order> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            OrderBook.Order x = a.get(i), y = b.get(i);
            if (x.seq() != y.seq() || !x.who().equals(y.who()) || x.side() != y.side() || x.price() != y.price()
                    || x.quantity() != y.quantity()) return false;
        }
        return true;
    }

    /* ======================= 9. EACH HOUSEHOLD TYPE TRADES ======================= */

    /**
     * Two cells and one bond, on a market of their own: skilled couples with
     * $88k each past their cushion and no bonds, and unskilled couples with
     * nothing saved holding the households' face. The world asks a rate over
     * every bond's return, so it is not in the market, and there is no bank
     * to trade (what the fixture gives it only sits): the cells have only
     * each other.
     */
    static final class TwoCells {
        final HouseholdBalance hb = new HouseholdBalance();
        final BondMarket bm = new BondMarket();
        final Household poor, rich;
        final CorporateBond bond;

        TwoCells(double face, double poorHolds) {
            double[][] mix = new double[FamilyStructure.values().length][PayTier.values().length];
            mix[FamilyStructure.COUPLE.ordinal()][PayTier.UNSKILLED.ordinal()] = 100;
            mix[FamilyStructure.COUPLE.ordinal()][PayTier.SKILLED.ordinal()] = 100;
            double[] pay = new double[HouseholdBalance.ROWS];
            pay[PayTier.UNSKILLED.ordinal()] = 100 * 4.0;
            pay[PayTier.SKILLED.ordinal()] = 100 * 4.0;
            double[] none = new double[HouseholdBalance.ROWS];
            hb.advanceMonth((s, t) -> mix[s.ordinal()][t.ordinal()], pay, 0, none, none, .25, .05, 1);
            poor = hb.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED);
            rich = hb.cell(FamilyStructure.COUPLE, PayTier.SKILLED);
            poor.savings = 0;
            rich.savings = 100;
            hb.setBondMarket(bm);
            bm.attach(new BondMarket.Readings() {
                @Override public int month() { return 1; }
                @Override public double curve(int months) { return .04; }
                @Override public double policyRate() { return .03; }
                @Override public double depositRate() { return .01; }
                @Override public double worldRate() { return .20; }   // over every bond's return: the world stays out
                @Override public double countryPremium() { return 0; }
                @Override public double monthlyGdp() { return 1_000_000; }
                @Override public double localPerUsd() { return 1; }
                @Override public boolean worldRunning() { return false; }
                @Override public double unit() { return 1; }
            }, hb, null, null, null);
            CorporateBond b = new CorporateBond(1, ISSUER, face, .06, 0, CorporateBond.TERM_MONTHS);
            b.households = poorHolds;
            b.bank = face - poorHolds;
            BondMarket.State st = new BondMarket.State();
            st.bonds = new ArrayList<>(List.of(b));
            st.nextId = 2;
            bm.restore(st);
            bond = bm.getBonds().get(0);
            poor.setBondFace(1, poorHolds / 100);
        }
    }

    static void eachTypeTrades() {
        out.println("\n--- 9. each household type trades: a rich cell bids, a cell over its money asks, and they meet ---");
        TwoCells a = new TwoCells(10_000, 10_000);          // the unskilled couples hold all of it: $100k of face each
        close("fixture: the bond's households' face is the cells' own, summed", a.bond.households(), a.hb.bondFaceHeld(1), 1e-9);
        double richSavings = a.rich.savings(), poorSavings = a.poor.savings();
        a.bm.startMonth();
        a.bm.takeMonth(1);
        OrderBook book = a.bm.bookOf(a.bond);
        double bought = a.rich.bondFace(1) * 100;
        out.printf("   the rich cell bought $%,.1fk of face from the poor one for $%,.1fk in %d trade(s); what rests: bids %,.1f, asks %,.1f%n",
                bought, a.bm.getBetweenHouseholds(), a.bm.getBetweenHouseholdsTrades(),
                book.depth(OrderBook.Side.BUY, 0), book.depth(OrderBook.Side.SELL, Double.MAX_VALUE));
        assertTrue("the rich cell bid and the cell over its money asked, and they traded with each other",
                bought > 0 && a.bm.getBetweenHouseholdsTrades() > 0);
        close("...the face the one gave up is the face the other took", (100 - a.poor.bondFace(1)) * 100, bought, 1e-9);
        close("...the cash the one paid is the cash the other was paid, the transfer counted",
                (richSavings - a.rich.savings()) * 100, a.bm.getBetweenHouseholds(), 1e-9);
        close("...and the same the other way up", (a.poor.savings() - poorSavings) * 100, a.bm.getBetweenHouseholds(), 1e-9);
        close("...so the bond's households' face did not move", a.bond.households(), 10_000, 1e-9);
        close("...and is still the cells' own, summed", a.bond.households(), a.hb.bondFaceHeld(1), 1e-9);
        close("no pool line saw it: nothing bought from or sold to the pools",
                a.bm.getHouseholdsBought() + a.bm.getHouseholdsSold() + a.bm.getHouseholdsBoughtAbroad() + a.bm.getHouseholdsSoldAbroad(), 0, 0);

        // ...and in the waterfall. The unskilled couples hold less than their
        // money calls for, so they post nothing at the step; the rich cell's
        // bid rests, and a cell short of money sells into it.
        TwoCells w = new TwoCells(30_000, 1_000);
        w.bm.startMonth();
        w.bm.takeMonth(1);
        OrderBook wb = w.bm.bookOf(w.bond);
        assertTrue("fixture: the rich cell's bid rests after the step, and nothing traded",
                wb.resting(BondMarket.CELL + w.rich.key(), OrderBook.Side.BUY) > 0 && w.bm.getBetweenHouseholdsTrades() == 0
                        && wb.resting(BondMarket.CELL + w.poor.key(), OrderBook.Side.SELL) == 0);
        double facePoor = w.poor.bondFace(1);
        double per = w.bm.sellForCell(w.poor, 1.0, .30);    // $1k short each, its credit at 30%: dearer than the bid's yield
        close("a cell short of money sells into it, raising what it was short", per, 1.0, 1e-9);
        assertTrue("...cell to cell, the rich cell taking the face", w.poor.bondFace(1) < facePoor
                && w.bm.getBetweenHouseholdsTrades() > 0 && w.rich.bondFace(1) > 0);
        close("...the face the one gave up the other took", (facePoor - w.poor.bondFace(1)) * 100, w.rich.bondFace(1) * 100, 1e-9);
        close("...and no pool line", w.bm.getHouseholdsBought() + w.bm.getHouseholdsSold(), 0, 0);
    }

    /* ============================ 1c. MATURITY ============================ */

    static void maturity(Game g) {
        out.println("\n--- 1c. at maturity the issuer pays every holder its face, and the bond and its book go ---");
        BondMarket bm = g.getBondMarket();
        CorporateBond bond = null;
        for (CorporateBond b : bm.getBonds()) if (bond == null || b.face() > bond.face()) bond = b;
        assertTrue("fixture: a bond outstanding", bond != null);
        if (bond == null) return;
        bond.maturityMonth = g.getMonth() + 1;
        double face = bond.face();
        String issuer = bond.issuer();
        int id = bond.id();
        MoneyAudit.Result r = play(g);
        assertTrue("it falls due and is gone", bm.bond(id) == null);
        close("the issuer repaid its face", bm.getRepaid(issuer), face, 1e-9);
        close("...to its holders, every class together", bm.getPrincipalToHouseholds() + bm.getPrincipalToBank()
                + bm.getPrincipalToCompanies() + bm.getPrincipalAbroad(), face, 1e-9);
        cents("the world's principal leaves on the audit, in the financial account", line(r, "- bonds PrincipalAbroad"), bm.getPrincipalAbroad());
        cents("...the households' arrives in their savings", line(r, "- bonds PrincipalToHouseholds"), bm.getPrincipalToHouseholds());
        assertTrue("...and the month closes", closes(r));
    }
}
