package ham.citybuildersim;

import ham.citybuildersim.sectors.BusinessServices;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The distress rule for a price taker whose operating losses the bank's shortfall desk is still lending: it sells its plant over
 * BusinessInvestment.BLEED_MONTHS once its plant has run at an operating loss DISTRESS_LOSS_MONTHS running while it owes the desk,
 * and a quarter of its biggest holding a month only once it is overdrawn with no lender (AQ1).
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
 * WHAT THIS HAS TO PROVE (the fixture is a town with eight Contact Centres, then the minimum wage lifted until the
 * sector's own operations lose money; nothing is set but the cause):
 *
 *   1. the fixture: Business Services loses money on its operations every month while the desk lends it (it owes the desk
 *      throughout), its debt rising, until its operating-loss streak reaches DISTRESS_LOSS_MONTHS; and that streak is the
 *      run of months its running result (operating income less old metal sold) was below zero
 *   2. nothing is sold in any month that ends with fewer than DISTRESS_LOSS_MONTHS months of operating losses
 *   3. plant goes while it is still lent: a centre is sold in the month the streak reaches DISTRESS_LOSS_MONTHS, and by
 *      DISTRESS_LOSS_MONTHS + BLEED_MONTHS months of it at least half the centres are gone
 *   4. never faster than the pace: at most max(1, held / BLEED_MONTHS) a month, on the months the dial names
 *   5. a net-loss count carried past the dial by interest sells nothing: the floor lifted so the plant's own result comes
 *      back while interest keeps the net count climbing, then raised again with the net count past the dial and the
 *      operating streak begun again; in every month the net count is at the dial and the streak under it, nothing is sold
 *      (the first form of the rule would have sold), and the centres go when the streak reaches the dial
 *   6. overdrawn with no lender, the quarter as before
 *   7. only a sector the spare-capacity rule cannot reach bleeds (the three overrides), and, as behaviour, a sector that
 *      does not bleed, not overdrawn, sells nothing to the rule however long it has lost money (Freight, run on)
 *
 * Not proved here, for want of a fixture that can cause it: that a sector living on its own cash (an operating streak past the
 * dial, no principal, cash at or above zero) is not lent and sells nothing. No run of months in a founded town builds the cash
 * that would carry two years of losses without the desk (measured: Business Services' cash stayed under $500k across five
 * years of profit); the line in the rule is `lent = principal > 0`.
 *
 * Every fixture here CAUSES its condition. The counts are never set, nor the cash, nor a statement: the wage floor is
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
     * The net count at which section 5 lifts the floor: wages come back down over about a dozen months, so lifted this far
     * ahead of DISTRESS_LOSS_MONTHS the sector's operating income crosses zero just as the net count passes it (measured: it crossed
     * at a count of 24 from a lift at 12), the interest on what was borrowed keeping net income below zero a few months longer.
     */
    static final int LIFT_AT = 12;

    /** Months running the plant's own result must have held at or above zero (the operating streak at 0, the net count still climbing on interest) before section 5 raises the floor again. */
    static final int RECOVERED_MONTHS = 3;

    /** Months a run may take before it is given up, whatever the sector does. */
    static final int MOST_MONTHS = 120;

    static final int DISTRESS = BusinessInvestment.DISTRESS_LOSS_MONTHS;
    static final int BLEED = BusinessInvestment.BLEED_MONTHS;

    /** One month of the sector, read at the month's end. Money in thousands. */
    static final class Row {
        /** The net-loss streak (interest in it) and the operating-loss streak the bleed reads (AQ1). */
        int count, opCount;
        long held, others;
        /** running is the plant's own result: operating income less the old metal it sold that month. */
        double opInc, oldMetal, running, interest, net, cash, debt;
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
            r.opCount = plans.getOperatingLossMonths(key);
            BuildingManager bm = city.getBuildingManager();
            r.held = bm.getQuantity(contact.getId());
            for (BuildingsTemplate t : bm.getTemplatesBySector(key)) {
                if (t != contact) r.others += bm.getQuantity(t.getId());
            }
            r.opInc = bs.statement().operatingIncome;
            r.oldMetal = bs.statement().otherParts.getOrDefault(OldMetal.SOLD, 0.0);
            r.running = r.opInc - r.oldMetal;
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

        /** The cause: the floor lifted (the minimum wage law is open from the founding since OP1; the fixture opened it here before). */
        void raiseFloor() {
            if (foundingFloor == 0) foundingFloor = city.getLabourMarket().getMinimumWage();
            quietly(() -> {
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

    /** Whether a sector that does not bleed has lost money DISTRESS_LOSS_MONTHS running and ends the month with cash at or above zero. */
    static boolean pastDialSolvent(Town t) {
        for (Sector s : t.city.getSectors().all()) {
            if (!s.bleedsWhileLent() && s.getCash() >= 0 && t.plans.getLossMonths(s.key()) >= DISTRESS) return true;
        }
        return false;
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
            // (The quarter reads the net-loss count, as before AQ1; the bleed reads the operating streak.)
            if (quarter == null && r.count >= DISTRESS && r.held > 0) {
                overdrawnProbe = r;
                quarterHeld = r.held;
                quarter = a.plans.planDistressRetirement(a.bs, -1000, 0);
            }
            if (r.opCount >= DISTRESS + BLEED || (r.opCount >= DISTRESS && r.held == 0)) break;
        }

        int first = -1, iDistress = -1;
        for (int i = 1; i < rows.size(); i++) {
            if (first < 0 && rows.get(i).opCount >= 1) first = i;
            if (iDistress < 0 && rows.get(i).opCount >= DISTRESS) iDistress = i;
        }
        boolean reached = iDistress > 0 && first > 0;
        assertTrue(String.format("its operating-loss streak reaches DISTRESS_LOSS_MONTHS, %d months, with the cause and nothing else", DISTRESS), reached);
        if (!reached) {
            // Nothing below can be read; say so once and stop rather than index a month that is not there.
            out.println("The fixture did not lose money; the sections after it have nothing to read.");
            System.exit(fails);
        }

        boolean allLoss = true, allSolvent = true, allLent = true, streakIsTheRun = true;
        double lowestCash = Double.MAX_VALUE, worstOp = -Double.MAX_VALUE, oldMetalSold = 0;
        long othersHeld = 0;
        int run = 0;
        for (int i = 1; i < rows.size(); i++) {
            // The streak, counted here from the rows alone: the run of months the plant's own result (operating income less old metal) was below zero.
            run = rows.get(i).running < 0 ? run + 1 : 0;
            streakIsTheRun &= rows.get(i).opCount == run;
            oldMetalSold += rows.get(i).oldMetal;
        }
        for (int i = first; i <= iDistress; i++) {
            Row r = rows.get(i);
            allLoss &= r.running < 0;
            allSolvent &= r.cash >= 0;
            allLent &= r.debt > 0;
            lowestCash = Math.min(lowestCash, r.cash);
            worstOp = Math.max(worstOp, r.opInc);
        }
        for (Row r : rows) othersHeld += r.others;
        Row atDistress = rows.get(iDistress);
        out.printf("   %d months of operating losses (%d of net losses) by month %d: operating income never better than $%,.0fk a month, cash never under $%,.0fk, debt $%,.0fk -> $%,.0fk; old metal sold in the run $%,.0fk%n",
                atDistress.opCount, atDistress.count, iDistress, worstOp, lowestCash, founded.debt, atDistress.debt, oldMetalSold);
        assertTrue("the operating-loss streak is the run of months the plant's own result (operating income less old metal sold) was below zero, in every month",
                streakIsTheRun);
        assertTrue(String.format("it loses money on its operations in each of the %d months of losses it takes to reach the dial", DISTRESS),
                allLoss && iDistress - first + 1 >= DISTRESS);
        assertTrue("...and in none of them is it overdrawn: the shortfall desk lends the loss", allSolvent);
        assertTrue("...and in every one of them it owes the desk: the fixture is lent, not living on its own cash", allLent);
        assertTrue("...so what it owes the desk is more at the dial than it was at the founding", atDistress.debt > founded.debt);
        assertTrue("fixture: the Contact Centres are the only plant the sector holds, so they are its biggest holding", othersHeld == 0);

        /* ============ 2. nothing is sold below the dial ============ */
        out.println("\n--- nothing is sold before the dial ---");

        int under = 0;
        boolean unchanged = true;
        for (int i = 1; i < rows.size(); i++) {
            if (rows.get(i).opCount >= DISTRESS) continue;
            under++;
            unchanged &= rows.get(i).held == CENTRES;
        }
        out.printf("   %d months ended under %d months of operating losses, the centres held at %d in each%n", under, DISTRESS, CENTRES);
        assertTrue("a month that ends with fewer months of operating losses than the dial sells no centre", unchanged && under >= DISTRESS - 1);

        /* ============ 3. plant goes while it is still lent ============ */
        out.println("\n--- plant goes while the sector is still lent ---");

        Row before = rows.get(iDistress - 1);
        long soldAtDistress = before.held - atDistress.held;
        out.printf("   the month the operating streak reached %d: %d centre(s) sold, cash $%,.0fk, operating income $%,.0fk, debt $%,.0fk%n",
                atDistress.opCount, soldAtDistress, atDistress.cash, atDistress.opInc, atDistress.debt);
        assertTrue("a centre is sold in the month the count reaches the dial", soldAtDistress >= 1);
        assertTrue("...in a month the sector's cash is not below zero", atDistress.cash >= 0);

        // A lent month: past the dial, its plant losing money, its cash above zero at the month's end and a debt to the desk. An
        // overdrawn month ends at zero (the sale settles what it can), so a month above zero is one the quarter's overdrawn rule did not sell in.
        long soldLent = 0;
        int halfAt = -1;
        for (int i = iDistress; i < rows.size(); i++) {
            Row r = rows.get(i), p = rows.get(i - 1);
            if (r.opCount >= DISTRESS + BLEED || !(r.cash > 0 && r.running < 0 && r.debt > 0)) continue;
            soldLent += p.held - r.held;
            if (halfAt < 0 && 2 * soldLent >= CENTRES) halfAt = r.opCount;
        }
        out.printf("   %d of the %d centres sold in months the lender still covered; half gone at a count of %s (the dial's end: %d)%n",
                soldLent, CENTRES, halfAt < 0 ? "never" : String.valueOf(halfAt), DISTRESS + BLEED);
        assertTrue("by the dial plus BLEED_MONTHS months of losses at least half the centres are gone, sold while lent",
                2 * soldLent >= CENTRES);

        /* ============ 4. never faster than the pace ============ */
        out.println("\n--- never faster than the dial's pace ---");

        // Sales are read off the months a lender still covered (see above) where the plant loses money - the months the rule's
        // lent branch decides. The step is read off the operating streak: a sale is due on a count where (count - DISTRESS_LOSS_MONTHS) is a
        // multiple of ceil(BLEED_MONTHS / held), so when the holding falls the step changes and the next due count can come sooner than
        // the old step. Measured below as the gaps against the steps; the first gap, taken from a count that is a multiple of every step,
        // is the one that cannot be shorter.
        int lentMonths = 0, tooFast = 0, offStep = 0;
        List<Long> saleCounts = new ArrayList<>(), saleHeld = new ArrayList<>();
        for (int i = iDistress; i < rows.size(); i++) {
            Row r = rows.get(i), p = rows.get(i - 1);
            if (!(r.cash > 0 && r.running < 0 && r.debt > 0) || p.held <= 0) continue;
            lentMonths++;
            long sold = p.held - r.held;
            if (sold > Math.max(1, p.held / BLEED)) tooFast++;
            boolean due = (r.opCount - DISTRESS) % stepFor(p.held) == 0;
            if (due != sold > 0) offStep++;
            if (sold > 0) {
                saleCounts.add((long) r.opCount);
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
        assertTrue("a lent sale falls on a count the dial names, and a count it names with the plant losing money passes none",
                lentMonths > 0 && offStep == 0);
        assertTrue("the first two sales are at least the dial's step apart for the holding at the first",
                gaps.size() >= 1 && gaps.get(0) >= steps.get(0));

        /* ============ 5. a net-loss count carried past the dial by interest sells nothing ============ */
        out.println("\n--- a net-loss count past the dial that interest helped carry sells nothing ---");

        // A second town, the same cause given, taken away and given again. Lifted, the plant's own result comes back to zero or above
        // (its operating streak back to 0) while the interest on what the first months borrowed keeps net income below zero and the net
        // count climbing; the floor raised again, the plant loses on its operations again with the net count already past the dial.
        // The rule as e17ee67f9 had it (net count at the dial, this month's operating income below zero, lent, the step read off the net
        // count) would sell in those months; this one reads the operating streak, which has only just begun again. Nothing is set.
        Town b = town("dbleed-b");
        b.raiseFloor();
        List<Row> rowsB = new ArrayList<>();
        rowsB.add(b.read());
        while (rowsB.size() <= MOST_MONTHS && rowsB.get(rowsB.size() - 1).count < LIFT_AT) rowsB.add(b.month());
        Row liftedAt = rowsB.get(rowsB.size() - 1);
        boolean unsoldBeforeLift = true;
        for (Row r : rowsB) unsoldBeforeLift &= r.held == CENTRES;
        b.liftFloor();
        int recovered = 0;
        while (rowsB.size() <= MOST_MONTHS) {
            Row r = b.month();
            rowsB.add(r);
            recovered = r.opCount == 0 ? recovered + 1 : 0;
            // Until the plant's own result has held at or above zero long enough, or a profit resets the net count too.
            if (recovered >= RECOVERED_MONTHS || r.count == 0) break;
        }
        Row raisedAt = rowsB.get(rowsB.size() - 1);
        b.raiseFloor();
        while (rowsB.size() <= MOST_MONTHS) {
            Row r = b.month();
            rowsB.add(r);
            if (r.opCount >= DISTRESS || r.count == 0) break;
        }
        Row endB = rowsB.get(rowsB.size() - 1);

        // The months this section is about: the net count at or past the dial, the operating streak under it.
        int pastDial = 0, interestOnly = 0, soldInterestOnly = 0, bites = 0, oldRuleDue = 0, soldPast = 0;
        boolean solventPast = true;
        List<String> biteMonths = new ArrayList<>();
        for (int i = 1; i < rowsB.size(); i++) {
            Row r = rowsB.get(i), p = rowsB.get(i - 1);
            if (r.count < DISTRESS || r.opCount >= DISTRESS) continue;
            pastDial++;
            soldPast += (int) (p.held - r.held);
            solventPast &= r.cash >= 0 && r.debt > 0;
            if (r.running >= 0 && r.net < 0) {
                interestOnly++;
                soldInterestOnly += (int) (p.held - r.held);
            }
            if (r.opInc < 0 && r.cash >= 0 && r.debt > 0) {
                bites++;
                if (p.held > 0 && (r.count - DISTRESS) % stepFor(p.held) == 0) oldRuleDue++;
                biteMonths.add(String.format("%d/%d", r.count, r.opCount));
            }
        }
        out.printf("   the floor lifted at a net count of %d, the plant's own result back at or above zero for %d months by a net count of %d, raised again%n",
                liftedAt.count, RECOVERED_MONTHS, raisedAt.count);
        out.printf("   %d months with the net count at %d or over and the operating streak under it: %d interest only (operating result at or above zero, net income below), %d with the plant losing, cash at or above zero and a debt to the desk; %d centre(s) sold in them%n",
                pastDial, DISTRESS, interestOnly, bites, soldPast);
        out.printf("   net count / operating streak in the months with the plant losing: %s%n", String.join(" ", biteMonths));
        out.printf("   e17ee67f9's rule would have sold in %d of them; the run ended at a net count of %d, an operating streak of %d, %d centres held%n",
                oldRuleDue, endB.count, endB.opCount, endB.held);
        assertTrue("fixture: the floor is lifted with the net count under the dial and every centre still held", liftedAt.count < DISTRESS && unsoldBeforeLift);
        assertTrue("fixture: the floor is raised again with the net count already at the dial and the operating streak back at 0",
                raisedAt.count >= DISTRESS && raisedAt.opCount == 0);
        assertTrue("fixture: the sector is solvent and owes the desk in each month the net count is at the dial and the operating streak under it", pastDial > 0 && solventPast);
        assertTrue("fixture: at least one such month has the plant losing money, which e17ee67f9's rule would have sold in", bites >= 1 && oldRuleDue >= 1);
        assertTrue("a net count past the dial with the operating streak under it sells nothing, the plant losing money or not", soldPast == 0);
        assertTrue("a loss that is only interest holds: months at or above the dial on the net count with the plant's own result at or above zero, nothing sold",
                interestOnly >= RECOVERED_MONTHS && soldInterestOnly == 0);
        assertTrue("...and the rule is not dead: the centres go when the operating streak reaches the dial", endB.opCount >= DISTRESS && endB.held < CENTRES);


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

        // The other direction, as behaviour: a sector that does not bleed, solvent, asked the rule in town a's last month sells nothing, however
        // long it has lost money. (A read: the rule changes nothing it is asked of, so asking it of every sector moves nothing.) It bites only
        // on a sector at or past the dial on its net count, so town a is run on, with nothing set, until one that does not bleed has cash at or
        // above zero at a month's end past it: Freight, which the shortfall desk lends, loses money in this town without help.
        int more = 0;
        while (more < MOST_MONTHS && !pastDialSolvent(a)) {
            a.month();
            more++;
        }
        List<String> asked = new ArrayList<>();
        int askedPastDial = 0, sold = 0;
        for (Sector s : a.city.getSectors().all()) {
            if (s.bleedsWhileLent() || s.getCash() < 0) continue;
            int n = a.plans.getLossMonths(s.key());
            BusinessInvestment.Decision d = a.plans.planDistressRetirement(s, s.getCash(), 0);
            if (n >= DISTRESS) {
                askedPastDial++;
                asked.add(String.format("%s (%d months of losses, cash $%,.0fk, owes $%,.0fk: %s)", s.key(), n, s.getCash(), a.desk.getPrincipal(s.key()), d.reason));
            }
            if (d.build) sold++;
        }
        out.printf("   town a run %d months on; asked of every sector that does not bleed and is not overdrawn, at or past %d months of losses: %s%n",
                more, DISTRESS, asked.isEmpty() ? "none" : String.join("; ", asked));
        assertTrue("fixture: at least one sector that does not bleed has lost money for the dial's months and is not overdrawn", askedPastDial >= 1);
        assertTrue("a sector that does not bleed, not overdrawn, sells nothing to the distress rule however long it has lost money", sold == 0);

        if (fails == 0) {
            out.println("The distress rule bleeds a lent price taker over the dial, holds a loss that is only interest,"
                    + " and sells a quarter once the lender has gone.");
        } else {
            out.println(fails + " check(s) failed.");
        }
        System.exit(fails);
    }
}
