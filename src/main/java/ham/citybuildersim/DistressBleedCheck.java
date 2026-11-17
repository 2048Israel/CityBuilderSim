package ham.citybuildersim;

import ham.citybuildersim.sectors.BusinessServices;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The distress rule for a price taker whose losses the bank's shortfall desk is still lending: it sells its plant over
 * BusinessInvestment.BLEED_MONTHS while it is lent, and a quarter of its biggest holding a month only once it is
 * overdrawn with no lender (AQ1).
 *
 * WHY: on main's AUTO2 ensemble, seed 2, Business Services lost money 101 months running. The shortfall desk lent it
 * $409M of the losses and the sector held every office through all of it, until the desk refused at month 1091; then the
 * rule sold a quarter of its biggest holding a month, 6,000 posts in twelve months, and the town went from 8% to 38%
 * out of work. The same happened in 8 of 16 seeds. A sector that makes nothing of what it sells and has no demand reading
 * (Business Services, Automotive, Manufacturing: Sector.bleedsWhileLent()) has no rule but this one, so the wait for the
 * refusal was a wait for a cliff.
 *
 * WHAT THIS HAS TO PROVE (the fixture is a town with eight Contact Centres, then the minimum wage lifted until the
 * sector's own operations lose money; nothing is set but the cause):
 *
 *   1. the fixture: Business Services loses money on its operations every month while the desk lends it, its debt
 *      rising, until its loss count reaches DISTRESS_LOSS_MONTHS
 *   2. nothing is sold in any month that ends with fewer than DISTRESS_LOSS_MONTHS months of losses
 *   3. plant goes while it is still lent: a centre is sold in the month the count reaches DISTRESS_LOSS_MONTHS, and by
 *      DISTRESS_LOSS_MONTHS + BLEED_MONTHS months of losses at least half the centres are gone
 *   4. never faster than the pace: at most max(1, held / BLEED_MONTHS) a month, on the months the dial names
 *   5. a loss that is only interest holds: operating income back above zero, net income still below it, the count
 *      climbing, and nothing is sold
 *   6. overdrawn with no lender, the quarter as before
 *   7. only a sector the spare-capacity rule cannot reach bleeds (kept last: the one call main lacks)
 *
 * Every fixture here CAUSES its condition. The count is never set, nor the cash, nor a statement: the wage floor is
 * moved and the months are run.
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

    /** The centres the town founds the sector with: eight held is one sold every three months at the dial's pace, so the pace shows. */
    static final int CENTRES = 8;

    /**
     * How many times the founding minimum wage the floor is lifted to: past the point the sector's operations lose money, short of
     * the three times at which the desk refuses within two years (measured: x3 loses $250k a month and is refused at month 25).
     */
    static final double FLOOR_TIMES = 1.8;

    /**
     * The loss count at which section 5 lifts the floor again: wages come back down over about a dozen months, so lifted this far
     * ahead of DISTRESS_LOSS_MONTHS the sector's operating income crosses zero just as the count passes it (measured: it crossed
     * at a count of 24 from a lift at 12).
     */
    static final int LIFT_AT = 12;

    /** Months a run may take before it is given up, whatever the sector does. */
    static final int MOST_MONTHS = 120;

    static final int DISTRESS = BusinessInvestment.DISTRESS_LOSS_MONTHS;
    static final int BLEED = BusinessInvestment.BLEED_MONTHS;

    /** One month of the sector, read at the month's end. Money in thousands. */
    static final class Row {
        int count;
        long held, others;
        double opInc, interest, net, cash, debt;
    }

    /** A scratch city with the sector's fixture in it, and what a month reads of it. */
    static final class Town {
        final Game city;
        final BusinessServices bs;
        final BuildingsTemplate contact;
        final BusinessInvestment plans;
        final BusinessDebtManager desk;
        final String key;
        double foundingFloor;

        Town(Game city) {
            this.city = city;
            this.bs = city.getSectors().businessServices();
            this.contact = city.getBuildingManager().getTemplateByName(CONTACT);
            this.plans = city.getBusinessInvestment();
            this.desk = city.getEconomyManager().getBusinessDebtManager();
            this.key = bs.key();
        }

        Row read() {
            Row r = new Row();
            r.count = plans.getLossMonths(key);
            BuildingManager bm = city.getBuildingManager();
            r.held = bm.getQuantity(contact.getId());
            for (BuildingsTemplate t : bm.getTemplatesBySector(key)) {
                if (t != contact) r.others += bm.getQuantity(t.getId());
            }
            r.opInc = bs.statement().operatingIncome;
            r.interest = bs.statement().interest;
            r.net = bs.getNetIncome();
            r.cash = bs.getCash();
            r.debt = desk.getPrincipal(key);
            return r;
        }

        Row month() {
            quietly(() -> city.simulateMonths(1));
            return read();
        }

        /** The cause: the minimum wage law opened and the floor lifted. Opened by the lever's own date rule, as BusinessServicesCheck does. */
        void raiseFloor() {
            foundingFloor = city.getLabourMarket().getMinimumWage();
            quietly(() -> {
                city.getEconomyManager().getTaxPolicy().openNow(PolicyDates.Lever.MINIMUM_WAGE);
                city.getLabourMarket().setFloorBinding(true);
                city.getLabourMarket().setMinimumWage(foundingFloor * FLOOR_TIMES);
            });
        }

        /** The cause taken away again: the floor back where it was, no longer binding. */
        void liftFloor() {
            city.getLabourMarket().setMinimumWage(foundingFloor);
            city.getLabourMarket().setFloorBinding(false);
        }
    }

    /** A town of houses, shops, power, water and road around CENTRES Contact Centres, a founding two years old. */
    static Town town(String prefix) throws Exception {
        Path root = TempDirs.create(prefix);
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game city = new Game(files);
        quietly(() -> {
            city.run();
            BuildingManager b = city.getBuildingManager();
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 200_000_000L);
            b.addStack(b.getTemplateByName("House"), 2000, true);
            b.addStack(b.getTemplateByName("Convenience Store"), 40, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 2, true);
            b.addStack(b.getTemplateByName("Paved Road"), 30, true);
            b.addStack(b.getTemplateByName(CONTACT), CENTRES, true);
            for (int i = 0; i < 24; i++) city.simulateMonths(1);
        });
        return new Town(city);
    }

    /** The months the dial lets pass between two sales when this many are held: one building every so many months of the losses. */
    static long stepFor(long held) {
        return (long) Math.ceil((double) BLEED / held);
    }

    static String list(List<Long> xs) {
        StringBuilder s = new StringBuilder();
        for (long x : xs) s.append(s.length() == 0 ? "" : ", ").append(x);
        return s.length() == 0 ? "none" : s.toString();
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ============ 1. the fixture: the sector loses money on its operations while it is lent ============ */
        out.println("--- the sector loses money on its own operations while the desk lends it ---");

        Town a = town("dbleed-a");
        Row founded = a.read();
        out.printf("   founded: %d centres, operating income $%,.0fk a month, debt $%,.0fk, %d months of losses%n",
                founded.held, founded.opInc, founded.debt, founded.count);
        assertTrue("fixture: before the floor is lifted the sector books an operating profit and has no loss count",
                founded.opInc > 0 && founded.count == 0 && founded.held == CENTRES && founded.others == 0);

        a.raiseFloor();
        List<Row> rows = new ArrayList<>();
        rows.add(founded);
        Row overdrawnProbe = null;
        BusinessInvestment.Decision quarter = null;
        long quarterHeld = 0;
        while (rows.size() <= MOST_MONTHS) {
            Row r = a.month();
            rows.add(r);
            // Section 6's call, made while the sector still holds something: an overdrawn balance handed to the rule as its argument,
            // the one way to ask the quarter's question of a sector that is not overdrawn. The rule reads nothing it changes.
            if (quarter == null && r.count >= DISTRESS && r.held > 0) {
                overdrawnProbe = r;
                quarterHeld = r.held;
                quarter = a.plans.planDistressRetirement(a.bs, -1000, 0);
            }
            if (r.count >= DISTRESS + BLEED || (r.count >= DISTRESS && r.held == 0)) break;
        }

        int first = -1, iDistress = -1;
        for (int i = 1; i < rows.size(); i++) {
            if (first < 0 && rows.get(i).count >= 1) first = i;
            if (iDistress < 0 && rows.get(i).count >= DISTRESS) iDistress = i;
        }
        boolean reached = iDistress > 0 && first > 0;
        assertTrue(String.format("its loss count reaches DISTRESS_LOSS_MONTHS, %d months, with the cause and nothing else", DISTRESS), reached);
        if (!reached) {
            // Nothing below can be read; say so once and stop rather than index a month that is not there.
            out.println("The fixture did not lose money; the sections after it have nothing to read.");
            System.exit(fails);
        }

        boolean allLoss = true, allSolvent = true;
        double lowestCash = Double.MAX_VALUE, worstOp = -Double.MAX_VALUE;
        long othersHeld = 0;
        for (int i = first; i <= iDistress; i++) {
            Row r = rows.get(i);
            allLoss &= r.opInc < 0;
            allSolvent &= r.cash >= 0;
            lowestCash = Math.min(lowestCash, r.cash);
            worstOp = Math.max(worstOp, r.opInc);
        }
        for (Row r : rows) othersHeld += r.others;
        Row atDistress = rows.get(iDistress);
        out.printf("   %d months of losses by month %d: operating income never better than $%,.0fk a month, cash never under $%,.0fk, debt $%,.0fk -> $%,.0fk%n",
                atDistress.count, iDistress, worstOp, lowestCash, founded.debt, atDistress.debt);
        assertTrue(String.format("it loses money on its operations in each of the %d months of losses it takes to reach the dial", DISTRESS),
                allLoss && iDistress - first + 1 >= DISTRESS);
        assertTrue("...and in none of them is it overdrawn: the shortfall desk lends the loss", allSolvent);
        assertTrue("...so what it owes the desk is more at the dial than it was at the founding", atDistress.debt > founded.debt);
        assertTrue("fixture: the Contact Centres are the only plant the sector holds, so they are its biggest holding", othersHeld == 0);

        /* ============ 2. nothing is sold below the dial ============ */
        out.println("\n--- nothing is sold before the dial ---");

        int under = 0;
        boolean unchanged = true;
        for (int i = 1; i < rows.size(); i++) {
            if (rows.get(i).count >= DISTRESS) continue;
            under++;
            unchanged &= rows.get(i).held == CENTRES;
        }
        out.printf("   %d months ended under %d months of losses, the centres held at %d in each%n", under, DISTRESS, CENTRES);
        assertTrue("a month that ends with fewer months of losses than the dial sells no centre", unchanged && under >= DISTRESS - 1);

        /* ============ 3. plant goes while it is still lent ============ */
        out.println("\n--- plant goes while the sector is still lent ---");

        Row before = rows.get(iDistress - 1);
        long soldAtDistress = before.held - atDistress.held;
        out.printf("   the month the count reached %d: %d centre(s) sold, cash $%,.0fk, operating income $%,.0fk, debt $%,.0fk%n",
                atDistress.count, soldAtDistress, atDistress.cash, atDistress.opInc, atDistress.debt);
        assertTrue("a centre is sold in the month the count reaches the dial", soldAtDistress >= 1);
        assertTrue("...in a month the sector's cash is not below zero", atDistress.cash >= 0);

        // A lent month: past the dial, its operations losing money, its cash above zero at the month's end. An overdrawn month ends at
        // zero (the sale settles what it can), so a month above zero is one the quarter's overdrawn rule did not sell in.
        long soldLent = 0;
        int halfAt = -1;
        for (int i = iDistress; i < rows.size(); i++) {
            Row r = rows.get(i), p = rows.get(i - 1);
            if (r.count >= DISTRESS + BLEED || !(r.cash > 0 && r.opInc < 0)) continue;
            soldLent += p.held - r.held;
            if (halfAt < 0 && 2 * soldLent >= CENTRES) halfAt = r.count;
        }
        out.printf("   %d of the %d centres sold in months the lender still covered; half gone at a count of %s (the dial's end: %d)%n",
                soldLent, CENTRES, halfAt < 0 ? "never" : String.valueOf(halfAt), DISTRESS + BLEED);
        assertTrue("by the dial plus BLEED_MONTHS months of losses at least half the centres are gone, sold while lent",
                2 * soldLent >= CENTRES);

        /* ============ 4. never faster than the pace ============ */
        out.println("\n--- never faster than the dial's pace ---");

        // Sales are read off the months a lender still covered (see above) where the operations lose money - the months the rule's
        // lent branch decides. The step is read off the loss counter: a sale is due on a count where (count - DISTRESS_LOSS_MONTHS) is a
        // multiple of ceil(BLEED_MONTHS / held), so when the holding falls the step changes and the next due count can come sooner than
        // the old step. Measured below as the gaps against the steps; the first gap, taken from a count that is a multiple of every step,
        // is the one that cannot be shorter.
        int lentMonths = 0, tooFast = 0, offStep = 0;
        List<Long> saleCounts = new ArrayList<>(), saleHeld = new ArrayList<>();
        for (int i = iDistress; i < rows.size(); i++) {
            Row r = rows.get(i), p = rows.get(i - 1);
            if (!(r.cash > 0 && r.opInc < 0) || p.held <= 0) continue;
            lentMonths++;
            long sold = p.held - r.held;
            if (sold > Math.max(1, p.held / BLEED)) tooFast++;
            boolean due = (r.count - DISTRESS) % stepFor(p.held) == 0;
            if (due != sold > 0) offStep++;
            if (sold > 0) {
                saleCounts.add((long) r.count);
                saleHeld.add(p.held);
            }
        }
        List<Long> gaps = new ArrayList<>(), steps = new ArrayList<>();
        for (int i = 1; i < saleCounts.size(); i++) {
            gaps.add(saleCounts.get(i) - saleCounts.get(i - 1));
            steps.add(stepFor(saleHeld.get(i - 1)));
        }
        out.printf("   %d sales over %d months of lent losses, at counts %s with %s held before them%n",
                saleCounts.size(), lentMonths, list(saleCounts), list(saleHeld));
        out.printf("   the months between them: %s; the dial's step at the sale before each: %s%n", list(gaps), list(steps));
        assertTrue("in every lent month the centres sold are at most max(1, held / BLEED_MONTHS)", lentMonths > 0 && tooFast == 0);
        assertTrue("a lent sale falls on a count the dial names, and a count it names with the operations losing money passes none",
                lentMonths > 0 && offStep == 0);
        assertTrue("the first two sales are at least the dial's step apart for the holding at the first",
                gaps.size() >= 1 && gaps.get(0) >= steps.get(0));

        /* ============ 5. a loss that is only interest holds ============ */
        out.println("\n--- a loss that is only interest sells nothing ---");

        // A second town, the same cause, taken away again before the count can reach the dial: the pay comes down over months, the
        // operating income crosses zero as the count passes the dial, and what keeps the net income below zero is the interest on what
        // the first months borrowed. The count is the model's own; nothing is set.
        Town b = town("dbleed-b");
        b.raiseFloor();
        List<Row> rowsB = new ArrayList<>();
        rowsB.add(b.read());
        while (rowsB.size() <= MOST_MONTHS && rowsB.get(rowsB.size() - 1).count < LIFT_AT) rowsB.add(b.month());
        Row liftedAt = rowsB.get(rowsB.size() - 1);
        boolean unsoldBeforeLift = true;
        for (Row r : rowsB) unsoldBeforeLift &= r.held == CENTRES;
        b.liftFloor();
        while (rowsB.size() <= MOST_MONTHS) {
            Row r = b.month();
            rowsB.add(r);
            // Until a profit resets the count, or the dial's own months are out.
            if (r.count == 0 || r.count >= DISTRESS + BLEED) break;
        }
        int interestOnly = 0, interestSold = 0, dueInside = 0;
        boolean climbing = true, solvent = true;
        Row prevB = null;
        for (int i = 1; i < rowsB.size(); i++) {
            Row r = rowsB.get(i), p = rowsB.get(i - 1);
            if (r.count < DISTRESS || !(r.opInc >= 0 && r.net < 0)) continue;
            interestOnly++;
            interestSold += (int) (p.held - r.held);
            solvent &= r.cash >= 0 && r.debt > 0;
            if (p.held > 0 && (r.count - DISTRESS) % stepFor(p.held) == 0) dueInside++;
            if (prevB != null) climbing &= r.count == prevB.count + 1;
            prevB = r;
        }
        out.printf("   the floor lifted back at a count of %d; operating income back above zero with net income still below it for %d months at counts of %d and over%n",
                liftedAt.count, interestOnly, DISTRESS);
        if (prevB != null) {
            out.printf("   the last of them: operating income $%,.0fk, interest $%,.0fk, net income $%,.0fk, cash $%,.0fk, debt $%,.0fk, %d centres held%n",
                    prevB.opInc, prevB.interest, prevB.net, prevB.cash, prevB.debt, prevB.held);
        }
        assertTrue("fixture: the floor is lifted with the count under the dial and every centre still held", liftedAt.count < DISTRESS && unsoldBeforeLift);
        assertTrue("fixture: operating income is back above zero and net income still below it, past the dial, for several months running",
                interestOnly >= 3 && climbing);
        assertTrue("...in months the sector is solvent and still owes the desk", solvent);
        assertTrue("fixture: at least one of those months is one the dial would have sold in had the operations been losing", dueInside >= 1);
        assertTrue("a loss that is only interest sells nothing while the lender covers it", interestOnly >= 3 && interestSold == 0);

        /* ============ 6. overdrawn with no lender, the quarter as before ============ */
        out.println("\n--- overdrawn with no lender, a quarter ---");

        // The rule is handed an overdrawn balance as its argument (section 1 took it while the sector still held centres) rather than
        // waiting for the desk to refuse, which no month of this fixture can be made to do at a chosen count.
        assertTrue("fixture: the rule was asked of a sector past the dial that still held centres", quarter != null && overdrawnProbe != null);
        if (quarter != null) {
            long want = Math.min(quarterHeld, Math.max(1, (long) Math.ceil(quarterHeld * BusinessInvestment.MAX_RETIREMENT_FRACTION)));
            long bleedStep = Math.max(1, quarterHeld / BLEED);
            out.printf("   %d centres held at a count of %d, an overdrawn balance: %d sold; a lent sector would sell %d%n",
                    quarterHeld, overdrawnProbe.count, quarter.quantity, bleedStep);
            assertTrue("it sells", quarter.build);
            assertTrue("...from the biggest holding", quarter.template != null && quarter.template.getId() == a.contact.getId());
            assertTrue("...MAX_RETIREMENT_FRACTION of it, rounded up and at most all of it", quarter.quantity == want);
            assertTrue("...which is more than the one a lent sector sells, so the two rules are two rules", quarter.quantity > bleedStep);
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

        if (fails == 0) {
            out.println("The distress rule bleeds a lent price taker over the dial, holds a loss that is only interest,"
                    + " and sells a quarter once the lender has gone.");
        } else {
            out.println(fails + " check(s) failed.");
        }
        System.exit(fails);
    }
}
