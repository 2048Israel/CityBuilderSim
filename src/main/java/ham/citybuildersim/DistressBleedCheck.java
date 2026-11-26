package ham.citybuildersim;

import ham.citybuildersim.sectors.BusinessServices;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The distress rule for a price taker whose operating losses the bank's shortfall desk is still lending: it sells its plant over
 * BusinessInvestment.BLEED_MONTHS once its plant has run at an operating loss DISTRESS_LOSS_MONTHS running while it owes the desk,
 * and a quarter of its biggest holding a month after the same operating-loss runway when overdrawn with no lender (QR1).
 *
 * WHY: on main's AUTO2 ensemble, seed 2, Business Services lost money 101 months running. The shortfall desk lent it
 * $409M of the losses and the sector held every office through all of it, until the desk refused at month 1091; then the
 * rule sold a quarter of its biggest holding a month, 6,000 posts in twelve months, and the town went from 8% to 38%
 * out of work. The same happened in 8 of 16 seeds. A sector that makes nothing of what it sells and has no demand reading
 * (Business Services, Automotive, Manufacturing: Sector.bleedsWhileLent()) has no rule but this one, so the wait for the
 * refusal was a wait for a cliff. The first form of the rule read the net-loss count, which interest alone keeps climbing, and
 * this month's operating income, and counted a sector living on its own cash as lent; it now reads its own saved
 * operating-loss streak (BusinessInvestment.getOperatingLossMonths(), the old metal sold left out) and the desk's principal.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. the streak is the run of months the plant's own result (operating income less old metal sold) was below zero, and it
 *      reaches DISTRESS_LOSS_MONTHS after that many losing months; at DISTRESS_LOSS_MONTHS - 1 a lent, solvent sector sells
 *      nothing and at DISTRESS_LOSS_MONTHS it sells a centre
 *   2. nothing is sold at any operating streak under DISTRESS_LOSS_MONTHS, the sector lent
 *   3. plant goes while it is still lent: a centre is sold at the count that reaches DISTRESS_LOSS_MONTHS, and by
 *      DISTRESS_LOSS_MONTHS + BLEED_MONTHS months of it at least half the centres are gone
 *   4. never faster than the pace: at most max(1, held / BLEED_MONTHS) a month, on the counts the dial names, for every holding
 *   5. a net-loss count carried past the dial by interest sells nothing: with the net count at the dial or past it and the
 *      operating streak under the dial (back at 0, a loss that is only interest, or begun again), nothing is sold where the first
 *      form of the rule (e17ee67f9's: the net count alone, the plant losing money this month) would have sold; and the centres
 *      go when the operating streak reaches the dial
 *   6. overdrawn with no lender, interest-only losses keep paying plant; the quarter starts only at the operating-loss dial
 *   7. only a sector the spare-capacity rule cannot reach bleeds (the three overrides), and, as behaviour, a sector that
 *      does not bleed, lent, not overdrawn, sells nothing to the rule however long it has lost money, where Business
 *      Services in the same state sells
 *   8. a sector living on its own cash (both streaks past the dial, no principal, cash at or above zero) is not lent and sells
 *      nothing; the same sector once the desk lends it sells
 *
 * WHAT IS SET, AND WHY (FS9: the played town moved under it): every section reads the rule, so the rule's own inputs are set
 * through the model's own doors on a scratch town that holds CENTRES Contact Centres and no month played: the net-loss and
 * operating-loss streaks (BusinessInvestment.restoreLossMonths(), restoreOperatingLossMonths()), the shortfall desk's principal
 * (BusinessDebtManager.issueLoan()) and the sector's cash (setSectorCash()); the holding moves only by the model's own retire
 * (BuildingManager.retire()), as the decision names. The streak's own formula is not set: a statement is written and
 * recordOperatingResult() is asked of it, a month each way. The first form of this harness played a wage-floored town for
 * its counts and waited, in section 5, for a net count (LIFT_AT) measured on main's town; the town moved under that timing on
 * two other batches' paths and the fixture's own premises went red with the rule untouched. Nothing here is a timing: each
 * state is the state, and each threshold is read from both sides against the model's constants.
 */
public class DistressBleedCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-62s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static final String CONTACT = "Contact Centre";

    /** The centres the town holds: eight held is one sold every two months at the dial's pace, so the pace shows. */
    static final int CENTRES = 8;

    static final int DISTRESS = BusinessInvestment.DISTRESS_LOSS_MONTHS;
    static final int BLEED = BusinessInvestment.BLEED_MONTHS;

    /** The net-loss streak sections 1 to 4 set beside the operating streak: past the dial and the whole pace after it, so the rule's first question (the net count) is answered yes and only the operating streak decides. */
    static final int NET_PAST = DISTRESS + BLEED;

    /** What the shortfall desk is lent against the sector: the rule reads only that the principal is above zero. */
    static final double LOAN = 1_000;

    /** The cash a solvent sector is set at: the rule reads only that it is at or above zero. */
    static final double CASH = 1_000;

    /** The cash an overdrawn sector is set at: below zero, which is all the quarter's rule reads. */
    static final double OVERDRAWN = -1_000;

    /** The operating income, in thousands, of a month the plant loses money on its operations. */
    static final double LOSS = -50;

    /** The operating income, in thousands, of a month the plant's own result is above zero. */
    static final double PROFIT = 10;

    /** The interest, in thousands, a month's statement carries: more than PROFIT, so a month of operating profit can still lose money in net. */
    static final double INTEREST = 40;

    /** A scratch city with the sector's fixture in it, and the doors the rule reads through. */
    static final class Town {
        final Game city;
        final BusinessServices bs;
        final BuildingsTemplate contact;
        final BusinessInvestment plans;
        final BusinessDebtManager desk;
        final String key;

        Town(Game city) {
            this.city = city;
            this.bs = city.getSectors().businessServices();
            this.contact = city.getBuildingManager().getTemplateByName(CONTACT);
            this.plans = city.getBusinessInvestment();
            this.desk = city.getEconomyManager().getBusinessDebtManager();
            this.key = bs.key();
        }

        long held() {
            return city.getBuildingManager().getQuantity(contact.getId());
        }

        /** What the sector holds of anything but the centres. */
        long others() {
            long n = 0;
            BuildingManager bm = city.getBuildingManager();
            for (BuildingsTemplate t : bm.getTemplatesBySector(key)) {
                if (t != contact) n += bm.getQuantity(t.getId());
            }
            return n;
        }

        /** The centres held set to n: the surplus scrapped by the model's own retire, the shortfall stood by addStack. */
        void holdAt(long n) {
            BuildingManager bm = city.getBuildingManager();
            long now = held();
            if (now > n) bm.retire(contact, now - n);
            else if (now < n) quietly(() -> bm.addStack(contact, n - now, true));
        }

        /** Both streaks of one sector set: the restore methods replace the whole map, so the sector asked is the only one that holds a count. */
        void streaks(String sector, int net, int op) {
            plans.restoreLossMonths(Map.of(sector, net));
            plans.restoreOperatingLossMonths(Map.of(sector, op));
        }

        void cash(String sector, double cash) {
            city.getEconomyManager().setSectorCash(sector, cash);
        }

        /** The desk lends the sector LOAN: its principal is above zero from here. */
        void lend(String sector) {
            quietly(() -> desk.issueLoan(sector, LOAN, city.getMonth()));
        }

        /** The rule asked as Game asks it each month: the sector's own cash, no order in flight. */
        BusinessInvestment.Decision ask(Sector s) {
            return plans.planDistressRetirement(s, s.getCash(), 0);
        }
    }

    /** A town holding CENTRES Contact Centres and no month played: the rule reads counts, a principal, a cash and a holding, none of which a month builds. */
    static Town town(String label) {
        Game city = new Game(GameFiles.scratch(label));
        quietly(city::run);
        Town t = new Town(city);
        quietly(() -> city.getBuildingManager().addStack(t.contact, CENTRES, true));
        return t;
    }

    /** A month's statement written for the sector: its operating income, the old metal it sold that month (0 for none), the interest on its debt. */
    static void statement(Town t, double operating, double oldMetal, double interest) {
        Sector.Statement st = t.bs.statement();
        st.operatingIncome = operating;
        st.interest = interest;
        st.preTaxIncome = operating - interest;
        if (oldMetal != 0) st.otherParts.put(OldMetal.SOLD, oldMetal);
        else st.otherParts.remove(OldMetal.SOLD);
    }

    /** What a decision sells: its quantity when it builds (sells) at all, else none. */
    static long sold(BusinessInvestment.Decision d) {
        return d.build ? d.quantity : 0;
    }

    /** The months the dial lets pass between two sales when this many are held: one building every so many months of the losses. */
    static long stepFor(long held) {
        return (long) Math.ceil((double) BLEED / held);
    }

    /** The first form of the rule (e17ee67f9's): the net count at the dial, this month's operating income below zero, solvent and lent, the step read off the net count. */
    static boolean oldRuleSells(int net, double operatingIncome, double cash, boolean lent, long held) {
        return net >= DISTRESS && operatingIncome < 0 && cash >= 0 && lent && held > 0 && (net - DISTRESS) % stepFor(held) == 0;
    }

    /** A building of Freight's the sector may sell and that counts toward its measure: plant for a sector that does not bleed to hold. */
    static BuildingsTemplate freightPlant(Town t) {
        Sector freight = t.city.getSectors().freight();
        for (BuildingsTemplate x : t.city.getBuildingManager().getTemplatesBySector(freight.key())) {
            if (!x.isStation() && x.getTruckRoom() <= 0 && freight.unitsOf(x) > 0 && freight.mayRetire(x)) return x;
        }
        return null;
    }

    static String list(List<Long> xs) {
        StringBuilder s = new StringBuilder();
        for (long x : xs) s.append(s.length() == 0 ? "" : ", ").append(x);
        return s.length() == 0 ? "none" : s.toString();
    }

    /** One month's statement for section 1's streak formula, and the streak it must leave from a streak one short of the dial. */
    record Month(String what, double operating, double oldMetal, int streak) { }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ============ 1. the streak, and the dial from both sides ============ */
        out.println("--- the streak is the run of months the plant lost money on its operations; the dial from both sides ---");

        Town a = town("dbleed-a");
        String key = a.key;
        double owedAtFounding = a.desk.getPrincipal(key);
        assertTrue("fixture: the founding holds the centres, with no streak of either kind and nothing owed to the desk",
                a.held() == CENTRES && a.plans.getLossMonths(key) == 0 && a.plans.getOperatingLossMonths(key) == 0 && owedAtFounding == 0);

        a.lend(key);
        a.cash(key, CASH);

        // The streak's own formula, DISTRESS months of it: a losing statement written each month and recordOperatingResult() asked of it.
        Sector.Statement st = a.bs.statement();
        boolean allLoss = true, allSolvent = true, allLent = true, oneMoreEach = true;
        for (int i = 1; i <= DISTRESS; i++) {
            statement(a, LOSS, 0, INTEREST);
            a.plans.recordOperatingResult(a.bs);
            allLoss &= st.operatingIncome - st.otherParts.getOrDefault(OldMetal.SOLD, 0.0) < 0;
            oneMoreEach &= a.plans.getOperatingLossMonths(key) == i;
            allSolvent &= a.bs.getCash() >= 0;
            allLent &= a.desk.getPrincipal(key) > 0;
        }
        int reachedCount = a.plans.getOperatingLossMonths(key);
        out.printf("   %d months of operating loss written ($%,.0fk a month), the streak read after each; debt $%,.0fk -> $%,.0fk, cash $%,.0fk%n",
                DISTRESS, LOSS, owedAtFounding, a.desk.getPrincipal(key), a.bs.getCash());
        assertTrue(String.format("its operating-loss streak reaches DISTRESS_LOSS_MONTHS, %d months, with the cause and nothing else", DISTRESS),
                reachedCount == DISTRESS && oneMoreEach);

        // One month each way, from a streak one short of the dial, the old metal sold left out of the running result.
        List<Month> cases = List.of(
                new Month("a loss on operations", LOSS, 0, DISTRESS),
                new Month("a profit on operations", PROFIT, 0, 0),
                new Month("a profit only because old metal was sold", PROFIT, 3 * PROFIT, DISTRESS),
                new Month("a profit that survives the old metal sold", 4 * PROFIT, 3 * PROFIT, 0),
                new Month("a result of exactly zero after the old metal", 3 * PROFIT, 3 * PROFIT, 0),
                new Month("an operating income of exactly zero", 0, 0, 0));
        boolean streakIsTheRun = true, dialFollows = true;
        for (Month m : cases) {
            a.streaks(key, NET_PAST, DISTRESS - 1);
            statement(a, m.operating(), m.oldMetal(), INTEREST);
            a.plans.recordOperatingResult(a.bs);
            int got = a.plans.getOperatingLossMonths(key);
            BusinessInvestment.Decision d = a.ask(a.bs);
            boolean ok = got == m.streak();
            streakIsTheRun &= ok;
            dialFollows &= d.build == (m.streak() >= DISTRESS);
            out.printf("   from %d: %s (operating income $%,.0fk, old metal sold $%,.0fk) leaves the streak at %d (expected %d); %d centre(s) sold at it%n",
                    DISTRESS - 1, m.what(), m.operating(), m.oldMetal(), got, m.streak(), sold(d));
        }
        assertTrue("the operating-loss streak is the run of months the plant's own result (operating income less old metal sold) was below zero, in every month",
                streakIsTheRun);
        assertTrue("a month that takes the streak to the dial sells a centre, and a month that resets it sells none", dialFollows);

        // The dial from both sides, the streaks set: net count past the dial, lent, solvent.
        a.holdAt(CENTRES);
        a.streaks(key, NET_PAST, DISTRESS - 1);
        BusinessInvestment.Decision justUnder = a.ask(a.bs);
        a.streaks(key, NET_PAST, DISTRESS);
        BusinessInvestment.Decision atDial = a.ask(a.bs);
        out.printf("   lent, cash $%,.0fk: at %d months of operating losses %d sold (%s); at %d months %d sold (%s)%n",
                a.bs.getCash(), DISTRESS - 1, sold(justUnder), justUnder.reason, DISTRESS, sold(atDial), atDial.reason);
        assertTrue("just under the dial, DISTRESS_LOSS_MONTHS - 1 months of operating losses, lent and solvent: nothing is sold", !justUnder.build);
        assertTrue("at the dial, DISTRESS_LOSS_MONTHS months of operating losses, lent and solvent: a centre is sold",
                atDial.build && atDial.template != null && atDial.template.getId() == a.contact.getId() && atDial.quantity >= 1);

        assertTrue("it loses money on its operations in each of the " + DISTRESS + " months of losses it takes to reach the dial",
                allLoss && reachedCount == DISTRESS);
        assertTrue("...and in none of them is it overdrawn: the shortfall desk lends the loss", allSolvent);
        assertTrue("...and in every one of them it owes the desk: the fixture is lent, not living on its own cash", allLent);
        assertTrue("...so what it owes the desk is more at the dial than it was at the founding", a.desk.getPrincipal(key) > owedAtFounding);
        assertTrue("fixture: the Contact Centres are the only plant the sector holds, so they are its biggest holding", a.others() == 0);

        /* ============ 2. nothing is sold below the dial ============ */
        out.println("\n--- nothing is sold before the dial ---");

        int under = 0;
        boolean unchanged = true;
        for (int op = 0; op < DISTRESS; op++) {
            a.streaks(key, NET_PAST, op);
            unchanged &= !a.ask(a.bs).build && a.held() == CENTRES;
            under++;
        }
        out.printf("   %d operating-loss counts under %d, the net count at %d, lent and solvent, the centres held at %d in each%n", under, DISTRESS, NET_PAST, CENTRES);
        assertTrue("a month that ends with fewer months of operating losses than the dial sells no centre", unchanged && under >= DISTRESS);

        /* ============ 3. plant goes while it is still lent ============ */
        out.println("\n--- plant goes while the sector is still lent ---");

        // Each count from the dial to the dial plus BLEED_MONTHS asked in turn, the holding falling by what the decision says.
        a.holdAt(CENTRES);
        BuildingManager bm = a.city.getBuildingManager();
        long soldAtDistress = -1, soldLent = 0;
        int halfAt = -1;
        boolean cashStays = true, stillLent = true;
        List<Long> saleCounts = new ArrayList<>(), saleHeld = new ArrayList<>();
        for (int count = DISTRESS; count <= DISTRESS + BLEED; count++) {
            a.streaks(key, NET_PAST, count);
            long before = a.held();
            BusinessInvestment.Decision d = a.ask(a.bs);
            if (d.build) {
                bm.retire(d.template, d.quantity);
                saleCounts.add((long) count);
                saleHeld.add(before);
            }
            long gone = before - a.held();
            if (count == DISTRESS) soldAtDistress = gone;
            soldLent += gone;
            if (halfAt < 0 && 2 * soldLent >= CENTRES) halfAt = count;
            cashStays &= a.bs.getCash() >= 0;
            stillLent &= a.desk.getPrincipal(key) > 0;
        }
        out.printf("   the count that reached %d: %d centre(s) sold, cash $%,.0fk, debt $%,.0fk%n",
                DISTRESS, soldAtDistress, a.bs.getCash(), a.desk.getPrincipal(key));
        out.printf("   %d of the %d centres sold at counts %d to %d, lent throughout; half gone at a count of %s (the dial's end: %d)%n",
                soldLent, CENTRES, DISTRESS, DISTRESS + BLEED, halfAt < 0 ? "never" : String.valueOf(halfAt), DISTRESS + BLEED);
        assertTrue("a centre is sold in the month the count reaches the dial", soldAtDistress >= 1);
        assertTrue("...in a month the sector's cash is not below zero", cashStays);
        assertTrue("by the dial plus BLEED_MONTHS months of losses at least half the centres are gone, sold while lent",
                2 * soldLent >= CENTRES && stillLent);

        /* ============ 4. never faster than the pace ============ */
        out.println("\n--- never faster than the dial's pace ---");

        // Every holding from one to all the centres, each asked at every count from the dial to the dial plus BLEED_MONTHS: a sale is due on a
        // count where (count - DISTRESS_LOSS_MONTHS) is a multiple of ceil(BLEED_MONTHS / held), and never more than max(1, held / BLEED_MONTHS) goes.
        int asks = 0, tooFast = 0, offStep = 0;
        for (int held = 1; held <= CENTRES; held++) {
            a.holdAt(held);
            for (int count = DISTRESS; count <= DISTRESS + BLEED; count++) {
                a.streaks(key, NET_PAST, count);
                long sold = sold(a.ask(a.bs));
                asks++;
                if (sold > Math.max(1, held / BLEED)) tooFast++;
                boolean due = (count - DISTRESS) % stepFor(held) == 0;
                if (due != sold > 0) offStep++;
            }
        }
        // The gap between the first two sales of the walk in section 3 (the holding falling as it sells): not shorter than the step at the first.
        List<Long> gaps = new ArrayList<>(), steps = new ArrayList<>();
        for (int i = 1; i < saleCounts.size(); i++) {
            gaps.add(saleCounts.get(i) - saleCounts.get(i - 1));
            steps.add(stepFor(saleHeld.get(i - 1)));
        }
        out.printf("   %d asks over holdings of 1 to %d at counts %d to %d%n", asks, CENTRES, DISTRESS, DISTRESS + BLEED);
        out.printf("   section 3's %d sales, at counts %s with %s held before them%n", saleCounts.size(), list(saleCounts), list(saleHeld));
        out.printf("   the months between them: %s; the dial's step at the sale before each: %s%n", list(gaps), list(steps));
        assertTrue("in every lent month the centres sold are at most max(1, held / BLEED_MONTHS)", asks > 0 && tooFast == 0);
        assertTrue("a lent sale falls on a count the dial names, and a count it names with the plant losing money passes none",
                asks > 0 && offStep == 0);
        assertTrue("the first two sales are at least the dial's step apart for the holding at the first",
                gaps.size() >= 1 && gaps.get(0) >= steps.get(0));

        /* ============ 5. a net-loss count carried past the dial by interest sells nothing ============ */
        out.println("\n--- a net-loss count past the dial that interest helped carry sells nothing ---");

        // The states a played town reached after the floor was lifted and raised again, set: the net count at the dial or past it (up to the
        // dial plus BLEED_MONTHS, so the counts the first form of the rule's step named are in it), the operating streak at each value under the dial,
        // lent, solvent, every centre held. At 0 the plant's own result is at or above zero while interest keeps net income below it (a loss that
        // is only interest); at 1 and over the plant loses on its operations this month. The rule as e17ee67f9 had it (net count at the dial, this
        // month's operating income below zero, lent, the step read off the net count) would sell in those states; this one reads the operating streak.
        a.holdAt(CENTRES);
        a.cash(key, CASH);
        int pastDial = 0, interestOnly = 0, soldInterestOnly = 0, bites = 0, oldRuleDue = 0, soldPast = 0;
        boolean solventPast = true, readsBack = true, raisedAgain = false;
        for (int net = DISTRESS; net <= DISTRESS + BLEED; net++) {
            for (int op = 0; op < DISTRESS; op++) {
                a.streaks(key, net, op);
                statement(a, op == 0 ? PROFIT : LOSS, 0, INTEREST);
                boolean lent = a.desk.getPrincipal(key) > 0;
                double cash = a.bs.getCash();
                readsBack &= a.plans.getLossMonths(key) == net && a.plans.getOperatingLossMonths(key) == op && a.held() == CENTRES;
                if (net == DISTRESS && op == 0) {
                    raisedAgain = a.plans.getLossMonths(key) >= DISTRESS && a.plans.getOperatingLossMonths(key) == 0;
                }
                solventPast &= cash >= 0 && lent;
                long sold = sold(a.ask(a.bs));
                pastDial++;
                soldPast += (int) sold;
                if (st.operatingIncome - st.otherParts.getOrDefault(OldMetal.SOLD, 0.0) >= 0 && a.bs.getNetIncome() < 0) {
                    interestOnly++;
                    soldInterestOnly += (int) sold;
                }
                if (st.operatingIncome < 0 && cash >= 0 && lent) {
                    bites++;
                    if (oldRuleSells(net, st.operatingIncome, cash, lent, a.held())) oldRuleDue++;
                }
            }
        }
        // ...and the rule is not dead: at each net count the operating streak at the dial sells, the holding falling as the model's retire does it.
        int atDialAsked = 0, notSold = 0;
        boolean centresGo = true;
        for (int net = DISTRESS; net <= DISTRESS + BLEED; net++) {
            a.holdAt(CENTRES);
            a.streaks(key, net, DISTRESS);
            BusinessInvestment.Decision d = a.ask(a.bs);
            atDialAsked++;
            if (!d.build) notSold++;
            else bm.retire(d.template, d.quantity);
            centresGo &= a.held() < CENTRES;
        }
        out.printf("   net counts %d to %d, operating streaks 0 to %d, lent (debt $%,.0fk), cash $%,.0fk, %d centres held%n",
                DISTRESS, DISTRESS + BLEED, DISTRESS - 1, a.desk.getPrincipal(key), a.bs.getCash(), CENTRES);
        out.printf("   %d states with the net count at %d or over and the operating streak under it: %d interest only (operating result at or above zero, net income below), %d with the plant losing, cash at or above zero and a debt to the desk; %d centre(s) sold in them%n",
                pastDial, DISTRESS, interestOnly, bites, soldPast);
        out.printf("   e17ee67f9's rule would have sold in %d of the states with the plant losing; at the operating streak %d, %d of %d net counts sold nothing%n",
                oldRuleDue, DISTRESS, notSold, atDialAsked);
        assertTrue("fixture: the net count and the operating streak read back as set, with every centre held, the sector lent and solvent",
                readsBack && solventPast && pastDial > 0);
        assertTrue("fixture: the floor is raised again with the net count already at the dial and the operating streak back at 0",
                raisedAgain);
        assertTrue("fixture: the sector is solvent and owes the desk in each month the net count is at the dial and the operating streak under it", pastDial > 0 && solventPast);
        assertTrue("fixture: at least one such month has the plant losing money, which e17ee67f9's rule would have sold in", bites >= 1 && oldRuleDue >= 1);
        assertTrue("a net count past the dial with the operating streak under it sells nothing, the plant losing money or not", soldPast == 0);
        assertTrue("a loss that is only interest holds: months at or above the dial on the net count with the plant's own result at or above zero, nothing sold",
                interestOnly >= 1 && soldInterestOnly == 0);
        assertTrue("...and the rule is not dead: the centres go when the operating streak reaches the dial", atDialAsked > 0 && notSold == 0 && centresGo);

        /* ============ 6. overdrawn with no lender: running costs before liquidation (QR1) ============ */
        out.println("\n--- overdrawn with no lender: interest-only losses hold, operating losses sell ---");

        // Cause the condition: the plant earns its running costs every month, interest makes every month a net loss,
        // and a prior restructuring blocks fresh credit despite the remaining debt. Both clocks read the statements.
        Town c = town("dbleed-c");
        // The no-interim-lender backstop already restructures debt and closes the overdraft without selling the plant.
        c.lend(c.key);
        double assets = LOAN / (2 * BusinessDebtManager.INSOLVENCY_TRIGGER);
        c.desk.setAssets(c.key, assets);
        c.desk.setCash(c.key, OVERDRAWN);
        double writtenOff = c.desk.restructure(c.key, true);
        assertTrue("the existing no-lender restructuring writes down debt and closes the overdraft while keeping the plant",
                writtenOff > 0 && Math.abs(c.desk.getPrincipal(c.key) - assets * BusinessDebtManager.RESTRUCTURE_TARGET) < 1e-6
                        && c.desk.getCash(c.key) == 0 && c.desk.takeOverdraftForgiven(c.key) == -OVERDRAWN && c.held() == CENTRES);
        c.cash(c.key, OVERDRAWN);
        long interestSales = 0;
        for (int i = 0; i < DISTRESS + BLEED; i++) {
            statement(c, PROFIT, 0, INTEREST);
            c.plans.recordSectorResult(c.key, c.bs.getNetIncome());
            c.plans.recordOperatingResult(c.bs);
            interestSales += sold(c.ask(c.bs));
        }
        assertTrue("fixture: paying plant is overdrawn, has no lender and has net losses past the dial solely from interest",
                c.bs.getCash() < 0 && c.desk.getPrincipal(c.key) > 0 && c.desk.isBorrowingBlocked(c.key)
                        && !c.desk.canFundProject(c.key, LOAN) && c.held() == CENTRES
                        && c.bs.statement().operatingIncome > 0 && c.bs.getNetIncome() < 0
                        && c.plans.getLossMonths(c.key) == DISTRESS + BLEED && c.plans.getOperatingLossMonths(c.key) == 0);
        assertTrue("interest-only losses sell no paying plant even overdrawn with no lender", interestSales == 0);

        // A fresh operating-loss run gets the entire runway even though interest has already run out the net clock.
        long earlySales = 0;
        for (int i = 0; i < DISTRESS - 1; i++) {
            statement(c, LOSS, 0, INTEREST);
            c.plans.recordSectorResult(c.key, c.bs.getNetIncome());
            c.plans.recordOperatingResult(c.bs);
            earlySales += sold(c.ask(c.bs));
        }
        assertTrue("fixture: the operating-loss streak is one month short, the net streak already past the dial",
                c.plans.getOperatingLossMonths(c.key) == DISTRESS - 1 && c.plans.getLossMonths(c.key) > DISTRESS);
        assertTrue("the quarter waits for the full operating-loss runway", earlySales == 0);
        statement(c, LOSS, 0, INTEREST);
        c.plans.recordSectorResult(c.key, c.bs.getNetIncome());
        c.plans.recordOperatingResult(c.bs);
        long quarterHeld = c.held();
        BusinessInvestment.Decision quarter = c.ask(c.bs);
        assertTrue("fixture: the rule was asked of a sector past the dial that still held centres",
                c.plans.getLossMonths(c.key) >= DISTRESS && c.plans.getOperatingLossMonths(c.key) == DISTRESS
                        && quarterHeld == CENTRES && c.bs.getCash() < 0 && c.desk.isBorrowingBlocked(c.key));
        long want = Math.min(quarterHeld, Math.max(1, (long) Math.ceil(quarterHeld * BusinessInvestment.MAX_RETIREMENT_FRACTION)));
        long bleedStep = Math.max(1, quarterHeld / BLEED);
        out.printf("   %d centres held at a net count of %d, cash $%,.0fk, fresh credit blocked: %d sold; a lent sector would sell %d%n",
                quarterHeld, c.plans.getLossMonths(c.key), c.bs.getCash(), quarter.quantity, bleedStep);
        assertTrue("it sells", quarter.build);
        assertTrue("...from the biggest holding", quarter.template != null && quarter.template.getId() == c.contact.getId());
        assertTrue("...MAX_RETIREMENT_FRACTION of it, rounded up and at most all of it", quarter.quantity == want);
        assertTrue("...which is more than the one a lent sector sells, so the two rules are two rules", quarter.quantity > bleedStep);

        // Recovery resets the operating clock even with the net clock still climbing; breaking even also pays running costs.
        for (double running : new double[] { PROFIT, 0 }) {
            statement(c, running, 0, INTEREST);
            c.plans.recordSectorResult(c.key, c.bs.getNetIncome());
            c.plans.recordOperatingResult(c.bs);
            assertTrue("running result " + running + " resets the distress clock while interest keeps net income below zero",
                    c.plans.getOperatingLossMonths(c.key) == 0 && c.bs.getNetIncome() < 0 && !c.ask(c.bs).build);
        }

        /* ============ 7. only a sector the spare-capacity rule cannot reach bleeds ============ */
        out.println("\n--- only a sector with no demand reading bleeds ---");

        int bleeders = 0;
        for (Sector s : a.city.getSectors().all()) {
            if (!s.bleedsWhileLent()) continue;
            bleeders++;
            assertTrue(s.key() + " bleeds while lent and the spare-capacity rule has no reading for it",
                    s.retirementDemandAndCapacity(a.city) == null);
        }
        assertTrue("at least one sector bleeds while lent", bleeders >= 1);

        // The other direction, as behaviour: every sector that does not bleed, lent, not overdrawn, with both streaks at the dial and on past it
        // for as long as the dial's pace runs, sells nothing; Freight is given plant to sell, and sold once overdrawn, so the bleed's gate and not an
        // empty holding is what holds it. Business Services, in the same state, sells.
        a.holdAt(CENTRES);
        Sector freight = a.city.getSectors().freight();
        BuildingsTemplate track = freightPlant(a);
        if (track != null) quietly(() -> bm.addStack(track, CENTRES, true));
        int asked = 0;
        long soldSteady = 0;
        boolean steadyLent = true;
        List<String> steady = new ArrayList<>();
        for (Sector s : a.city.getSectors().all()) {
            if (s.bleedsWhileLent()) continue;
            a.cash(s.key(), CASH);
            if (a.desk.getPrincipal(s.key()) <= 0) a.lend(s.key());
            steadyLent &= a.desk.getPrincipal(s.key()) > 0 && s.getCash() >= 0;
            for (int k = 0; k <= BLEED; k++) {
                a.streaks(s.key(), DISTRESS + k, DISTRESS + k);
                soldSteady += sold(a.ask(s));
                asked++;
            }
            steady.add(s.key());
        }
        out.printf("   %d sectors that do not bleed (%s), each lent, cash at or above zero, both streaks at %d to %d: %d asks, %d sold%n",
                steady.size(), String.join(", ", steady), DISTRESS, DISTRESS + BLEED, asked, soldSteady);
        assertTrue("fixture: at least one sector that does not bleed has lost money for the dial's months and is not overdrawn",
                asked >= 1 && steadyLent);
        assertTrue("a sector that does not bleed, not overdrawn, sells nothing to the distress rule however long it has lost money", soldSteady == 0);

        long freightHeld = track == null ? 0 : bm.getQuantity(track.getId());
        a.cash(freight.key(), OVERDRAWN);
        a.streaks(freight.key(), DISTRESS, 0);
        assertTrue("a sector that does not bleed also keeps paying plant when overdrawn", !a.ask(freight).build);
        a.streaks(freight.key(), DISTRESS, DISTRESS);
        BusinessInvestment.Decision freightOverdrawn = a.ask(freight);
        a.cash(freight.key(), CASH);
        out.printf("   Freight holds %d of %s; overdrawn, the same counts, it sells %d%n",
                freightHeld, track == null ? "nothing" : track.getName(), sold(freightOverdrawn));
        assertTrue("fixture: the plant a sector that does not bleed holds is sold by it once it is overdrawn, so only the bleed's gate holds it while it is lent",
                track != null && freightHeld > 0 && freightOverdrawn.build);

        a.streaks(key, DISTRESS, DISTRESS);
        BusinessInvestment.Decision services = a.ask(a.bs);
        out.printf("   Business Services, lent, cash $%,.0fk, both streaks at %d: %d sold (%s)%n", a.bs.getCash(), DISTRESS, sold(services), services.reason);
        assertTrue("Business Services, in the state the sectors that do not bleed were asked in, sells a centre",
                a.desk.getPrincipal(key) > 0 && a.bs.getCash() >= 0 && services.build && services.template != null
                        && services.template.getId() == a.contact.getId() && services.quantity >= 1);

        /* ============ 8. a sector living on its own cash is not lent ============ */
        out.println("\n--- a sector living on its own cash is not lent ---");

        // A fresh town with nothing owed to the desk: the sector's cash set at or above zero, both streaks past the dial,
        // its centres held. Asked at every count from the dial to the dial plus BLEED_MONTHS, then the desk lends it and it is asked again.
        Town own = town("dbleed-own");
        own.cash(own.key, CASH);
        boolean ownCash = own.desk.getPrincipal(own.key) == 0 && own.bs.getCash() >= 0 && own.held() == CENTRES;
        int askedOwn = 0;
        long soldOwn = 0;
        for (int k = 0; k <= BLEED; k++) {
            own.streaks(own.key, DISTRESS + k, DISTRESS + k);
            soldOwn += sold(own.ask(own.bs));
            askedOwn++;
        }
        own.lend(own.key);
        own.streaks(own.key, DISTRESS, DISTRESS);
        BusinessInvestment.Decision lentOwn = own.ask(own.bs);
        out.printf("   no principal, cash $%,.0fk, both streaks at %d to %d: %d asks, %d sold; lent $%,.0fk, both streaks at %d: %d sold (%s)%n",
                CASH, DISTRESS, DISTRESS + BLEED, askedOwn, soldOwn, own.desk.getPrincipal(own.key), DISTRESS, sold(lentOwn), lentOwn.reason);
        assertTrue("fixture: a sector living on its own cash owes the desk nothing, holds cash at or above zero and its centres, with both streaks past the dial",
                ownCash && askedOwn > 0 && own.plans.getLossMonths(own.key) >= DISTRESS);
        assertTrue("a sector living on its own cash is not lent: nothing is sold at any count from the dial to the dial plus BLEED_MONTHS", soldOwn == 0);
        assertTrue("...and the same sector, its streaks and cash as they were, sells a centre once the desk lends it",
                own.desk.getPrincipal(own.key) > 0 && lentOwn.build && lentOwn.template != null && lentOwn.template.getId() == own.contact.getId());

        if (fails == 0) {
            out.println("The distress rule bleeds a lent price taker over the dial, holds a loss that is only interest and a sector living on its own cash,"
                    + " and sells a quarter once the lender has gone.");
        } else {
            out.println(fails + " check(s) failed.");
        }
        System.exit(fails);
    }
}
