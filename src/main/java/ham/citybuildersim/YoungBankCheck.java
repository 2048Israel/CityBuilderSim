package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A young town's bank against the lone plant it lends for, and the first plant
 * that plant is weighed as: the harness of DQ1, "young towns borrow ahead of
 * their people and default", and since OE1 of the first plants they borrow for.
 *
 * WHY: GW1's staffing gate counts a year's draw, so a young town's sectors
 * order their first works earlier, and six fixtures held sectors so that their
 * banks stood (GW1's notes, section 5: BankCheck 19's town went into
 * resolution at month 44). On 0.9.128 its Cement & Concrete was lent $1,328k for
 * a Lime Kiln at month 11, 168% of the bank's equity, on an estimate struck at
 * an operating rate of 0.97; the kiln ran at 0.52-0.61, was sold at month 58,
 * and the bank wrote off $1,601k: under its 8% minimum for ten months, down to
 * 0.7%, but never in resolution. DQ1 measured four ways of holding or pricing a
 * lone plant's loan at the bank and took none (docs/queue/DQ1-notes.md). The
 * miss was the estimate's: a sector with nothing running weighed its first plant
 * at the city's health alone, its staffing read 1 for want of posts. OE1
 * (docs/queue/OE1-notes.md) weighs a first plant at the rate the month reads for
 * it (Sector.plannedRate()). The first form of this harness played that town
 * for 120 months with no sector held and read what happened. FS9: the played
 * town moved under it - on main 0.9.181 its bank spends 42 months in resolution
 * on its desk's trading (equity $2,732k at month 50, -$500k at month 55, no
 * lone plant's write-off behind it), and its Bottling Plant, weighed at 0.458
 * where its sector's own rate was 0.803, ran at 0.742 as its freight recovered
 * over twelve months - both a played path's and not the laws', outside this
 * harness's subject.
 *
 * WHAT IS SET, AND HOW: nothing is played to a condition. Every premise is
 * caused on BankCheck 19's founding (the same buildStack list), a scratch town
 * each, and each threshold is read from both sides against the model's own
 * constants.
 *
 * Sections 1 and 2, a lone plant against the young bank. The founding is played
 * FOUNDING_MONTHS with Cement & Concrete held, so it orders nothing of its own
 * and the bank, which has opened its first branch by then, carries the town's
 * lending as a book. Cement & Concrete is then given one Lime Kiln by
 * buildStack and a loan of LOAN_PER_EQUITY times the bank's equity by
 * BusinessDebtManager.issueLoan(), with the loan's proceeds in the sector's
 * till (setSectorCash()) and the bank's cash out (Bank.lend(): the month's
 * settle does it for a loan written in the month, and the desk's tally of it is
 * cleared as a month begins, so a loan set before the month is lent by hand or
 * is equity from nowhere). The sector's assets are not set (setAssets()):
 * EconomyManager.refreshCreditAssets() restates them from its balance sheet as
 * each month settles, and the till is what keeps it solvent. One month is
 * played, so the loan stands on the bank's book as the write-off month begins.
 * The plant then closes by the model's own
 * path: BuildingManager.retire(), BusinessDebtManager.settleRetirement(sector,
 * 0, true) (an empty borrower), and the month's default judgment writing off
 * what the sector's till did not pay (Game's bank.writeOffSector(), read as
 * bank.getWrittenOff()). The loss is sized by the till: the claim is paid from
 * it first, the rest is written off, so the till is the loan less the loss.
 * Bank.writeOffSector() alone is not used: it moves the month's statement and
 * not the book, so it moves no equity and no ratio. Four twins of one founding
 * (built the same way, one after another) differ only in that till:
 *  - a control, whose till pays the claim in full: nothing is written off, and
 *    its month's close is the bank a loss is taken from, with its own income
 *    known. A loss takes the lone loan off the book whole (what is paid and
 *    what is written off both leave it), so the ratio a loss leaves is read
 *    against the control's book, and its cushion is the control's equity less
 *    Bank.CAPITAL_RATIO of its weighted book, not the start's, which still
 *    carries the lone loan's weight;
 *  - (a) a loss of LOSS_UNDER_CUSHION of that cushion;
 *  - (b) a loss halfway between that cushion and the bank's equity;
 *  - (c) a loss of LOSS_PAST_EQUITY times the bank's equity.
 * The treasury stays on the button setting a scratch town opens with, so a bank
 * the loss fails waits for the city and isInResolution() reads it at the
 * month's close (Bank.resolveIfFailed(), equity() under zero, with
 * getFailures() counting it).
 *
 * Section 3, one first plant. The town is founded with every sector held, so
 * nothing moves its freight or staffing, and played LABOUR_MONTHS so the labour
 * market has read its people. A sector with nothing standing or on site is
 * weighed as the month begins: Sector.plannedRate() of its plant and the
 * sector's own rate (BusinessInvestment.operatingRateOf() of
 * getOperatingRate(), the estimate before OE1). They differ by cause: the
 * plant's posts are more than the young town's labour fills, so Sector
 * .firstPlantRate() reads a staffing under 1 and the empty sector reads 1 for
 * want of posts. The plant is then placed by buildStack (instant, so it stands
 * alone) and its operating rate read each of RUN_MONTHS_AT_LEAST months.
 *
 * What this has to prove:
 *  1. The fixture causes the condition: a sector of one plant owes the bank
 *     more than the bank's whole equity, and the four twins stand alike as the
 *     write-off month begins. A loss under the cushion leaves the ratio at or
 *     over Bank.CAPITAL_RATIO and begins no spell; one past the cushion and
 *     within equity puts it under, and a spell under Bank.CAPITAL_RATIO begins
 *     with the write-off of a sector of one plant (printed with the write-off
 *     that began it and whether its sector was one plant or many). Each loss
 *     lands with a margin the control's month cannot cross.
 *  2. A bank a lone plant's loss leaves in its equity stands: it is never in
 *     resolution. A loss past its equity (equity() under zero) puts it in
 *     resolution. The town's people are printed beside it.
 *  3. A first plant is weighed at the rate it runs at (OE1): the rate the order
 *     weighs it at (Sector.plannedRate()) is no further from the rate the plant
 *     ran at, standing alone, than its sector's own rate, and in all they are
 *     weighed nearer: with the old estimate put in as the weighed figure the
 *     two are one figure and the second line fails.
 */
public final class YoungBankCheck {

    static int fails = 0;
    static PrintStream out = System.out;
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    /** The months the young town is played, with only its lone sector held, before that sector is lent for: the bank has opened its first branch and carries the town's own lending, so its minimum stands a distance under its equity (with no other book the two are one point apart), and the loan month and the write-off month after it end the town's first year on record (Equity.RECORD_MONTHS), where the desk's shares are priced on prospects - from the month after, Equity.fairValue() prices a young landlord with no spare and no income at its last price, the desk's holding of its offering collapsed in the write-off month (a re-mark of -$220k on a bank of $850k), and a control month's own movement crossed every margin. */
    static final int FOUNDING_MONTHS = Equity.RECORD_MONTHS - 2;

    /** What the lone plant's loan is, in times the bank's equity as the founding ends: above the whole of it, and small enough that the largest loss sized below (LOSS_PAST_EQUITY of equity) is less than the loan. */
    static final double LOAN_PER_EQUITY = 2;

    /** Side (a)'s loss, in times the cushion the control's close leaves over Bank.CAPITAL_RATIO: under it. */
    static final double LOSS_UNDER_CUSHION = .5;

    /** Side (c)'s loss, in times the control's equity at its close: past all of it. */
    static final double LOSS_PAST_EQUITY = 1.5;

    /** The months section 3's all-held town is played before its first plant is weighed: the labour market has read the town's people (until it has, every tier's fill reads 1). */
    static final int LABOUR_MONTHS = 2;

    /** The fewest months a first plant's rate is read over, standing alone: a run to weigh it against. */
    static final int RUN_MONTHS_AT_LEAST = 3;

    /** The sector with nothing in the founding that borrows alone, and the plant BankCheck 19's loan was for. */
    static final String LONE = Sectors.CEMENT_CONCRETE, LONE_PLANT = "Lime Kiln";

    /** Section 3's first plant: Manufacturing's wire works, whose posts are more than the young town's people fill. */
    static final String FIRST_PLANT = "Wire & Cable Works";

    static void check(String label, boolean ok) {
        out.println((ok ? "   ok   " : "   FAIL ") + label);
        if (!ok) fails++;
    }

    /** `r` with the model's own printing muted. */
    static void quietly(Runnable r) {
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(out); }
    }

    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-6 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    /** A sector of one plant at most, standing or on site: one borrower, whose failure empties it. A sector that owns no building type borrows as many. */
    static boolean onePlant(Game g, String sector) {
        BuildingManager bm = g.getBuildingManager();
        java.util.List<BuildingsTemplate> types = bm.getTemplatesBySector(sector);
        if (types == null || types.isEmpty()) return false;
        double plants = bm.getUnderConstructionBySector(sector);
        for (BuildingsTemplate t : types) plants += bm.getQuantity(t.getId());
        return plants <= 1;
    }

    /**
     * BankCheck 19's young town (0.7.x, GW1) founded: the same buildStack list,
     * every sector free to order but the ones held, and `months` played.
     */
    static Game young(String label, boolean holdAll, int months) {
        Game town = new Game(GameFiles.scratch(label));
        quietly(() -> {
            town.run();
            town.setCashForTest(Founding.WEALTHY_CASH);
            town.getForeignAccounts().pinRate(1.0);
            town.getLandManager().setOwnedSqFt(30_000_000);
            town.buildStack(BankCheck.template(town, "House"), 400, true);
            town.buildStack(BankCheck.template(town, "Convenience Store"), 8, true);
            town.buildStack(BankCheck.template(town, "Small Grocery Store"), 2, true);
            town.buildStack(BankCheck.template(town, "Bakery"), 1, true);
            town.buildStack(BankCheck.template(town, "Paved Road"), 20, true);
            town.buildStack(BankCheck.template(town, "Industrial Bakery"), 2, true);
            town.buildStack(BankCheck.template(town, "Construction Depot"), 4, true);
            town.buildStack(BankCheck.template(town, "Coal Power Plant"), 1, true);
            town.buildStack(BankCheck.template(town, "Water Treatment Plant"), 1, true);
            for (Sector s : town.getSectors().all()) {
                if (holdAll || s.key().equals(LONE)) town.getBusinessInvestment().holdSector(s.key());
            }
            town.simulateMonths(months);
        });
        return town;
    }

    /** One twin of the lone plant's founding: its readings as the write-off month begins and as it closes. */
    static final class Lone {
        final Game town;
        final Bank bank;
        final BusinessDebtManager credit;
        final BuildingsTemplate plant;
        // The founding: what the sector held, and what the loan was set against.
        boolean aloneAtFounding;
        double loan, till;
        // The write-off month's start.
        double equity0, weighted0, principal0, interest0, ratio0, cushion0;
        boolean onePlant0;
        /** Whether any listing is priced on its record as the write-off month begins (Equity.valuedOnRecord()). */
        boolean onRecord0;
        long people;
        int failures0;
        boolean resolution0;
        final java.util.Map<String, Double> last = new java.util.HashMap<>();
        final java.util.Map<String, Boolean> wasOnePlant = new java.util.HashMap<>();
        // Its close.
        double equity, weighted, ratio, writtenOff, principal;
        boolean resolution, spell, lone;
        int failures;
        String cause;
        double most;

        Lone(String label) {
            town = young(label, false, FOUNDING_MONTHS);
            bank = town.getBank();
            credit = town.getEconomyManager().getBusinessDebtManager();
            plant = BankCheck.template(town, LONE_PLANT);
        }

        /** The loss the month's default judgment wrote off at the lone sector's name: what the sector's till did not pay of its claim. */
        double loss() { return writtenOff; }

        /** The spell line, as the first form of this harness printed it. */
        String spellLine() {
            return String.format(java.util.Locale.ROOT, "   month %d: ratio %.4f, %s wrote off $%.0fk, a sector of %s",
                    town.getMonth(), bank.capitalRatio(), cause, most, lone ? "one plant" : "many plants");
        }
    }

    /**
     * A twin: the founding played, the lone sector given its plant and its
     * loan, a month played so the loan stands on the bank's book, and then the
     * plant closed with the sector's till at what `tillOf` reads off the twin's
     * start, and the month played.
     */
    static Lone lone(String label, java.util.function.ToDoubleFunction<Lone> tillOf) {
        Lone l = new Lone(label);
        Game town = l.town;
        Sector sector = town.getSectors().byKey(LONE);
        l.aloneAtFounding = sector.runsNothing() && sector.buildingsOnSite() == 0 && onePlant(town, LONE);
        l.loan = LOAN_PER_EQUITY * l.bank.equity();
        quietly(() -> {
            town.buildStack(l.plant, 1, true);
            l.credit.issueLoan(LONE, l.loan, town.getMonth());
            town.getEconomyManager().setSectorCash(LONE, l.loan);
            l.bank.lend(l.loan);
            town.simulateMonths(1);
        });
        // The write-off month begins.
        l.equity0 = l.bank.equity();
        l.weighted0 = l.bank.getWeightedBook();
        l.ratio0 = l.weighted0 > 0 ? l.bank.capitalRatio() : Double.MAX_VALUE;
        l.cushion0 = l.equity0 - Bank.CAPITAL_RATIO * l.weighted0;
        l.principal0 = l.credit.getPrincipal(LONE);
        // ...and the month of interest its claim will carry when the plant closes: the till is sized with it, so the loss lands where it was sized.
        l.interest0 = l.credit.getMonthlyInterest(LONE);
        l.onePlant0 = onePlant(town, LONE);
        for (int c = 0; c < town.getEquity().keys().length; c++) l.onRecord0 |= town.getEquity().valuedOnRecord(c);
        l.people = (long) town.getPopulationManager().getPopulation();
        l.failures0 = l.bank.getFailures();
        l.resolution0 = l.bank.isInResolution();
        for (String s : l.credit.sectors()) {
            l.last.put(s, l.bank.getWrittenOff(s));
            l.wasOnePlant.put(s, onePlant(town, s));
        }
        l.till = tillOf.applyAsDouble(l);
        quietly(() -> {
            town.getEconomyManager().setSectorCash(LONE, l.till);
            town.getBuildingManager().retire(l.plant, 1);
            l.credit.settleRetirement(LONE, 0, true);
            town.simulateMonths(1);
        });
        // ...and closes.
        l.equity = l.bank.equity();
        l.weighted = l.bank.getWeightedBook();
        l.ratio = l.weighted > 0 ? l.bank.capitalRatio() : Double.MAX_VALUE;
        l.writtenOff = l.bank.getWrittenOff(LONE);
        l.principal = l.credit.getPrincipal(LONE);
        l.resolution = l.bank.isInResolution();
        l.failures = l.bank.getFailures();
        // A spell under the minimum is put down to the largest write-off of its first month and the one before.
        l.spell = l.weighted > 0 && l.ratio < Bank.CAPITAL_RATIO && !(l.ratio0 < Bank.CAPITAL_RATIO);
        for (String s : l.credit.sectors()) {
            double w = l.bank.getWrittenOff(s) + l.last.getOrDefault(s, 0.0);
            if (w > l.most) { l.most = w; l.cause = s; }
        }
        l.lone = l.cause != null && l.wasOnePlant.getOrDefault(l.cause, false);
        return l;
    }

    /** A first plant: the rates it was weighed at when the month began, and the rates it ran at. */
    static final class First {
        final String sector;
        final BuildingsTemplate building;
        final int month;
        final double weighed, own;
        final java.util.List<Double> ran = new java.util.ArrayList<>();

        First(String sector, BuildingsTemplate building, int month, double weighed, double own) {
            this.sector = sector;
            this.building = building;
            this.month = month;
            this.weighed = weighed;
            this.own = own;
        }

        double run() { return ran.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN); }
        boolean counted() { return ran.size() >= RUN_MONTHS_AT_LEAST; }
    }

    static String k(double v) { return String.format(java.util.Locale.ROOT, "$%.0fk", v); }

    public static void main(String[] args) throws Exception {

        // 1 and 2: the control, whose close is the bank a loss is taken from, and the three sides of it.
        Lone control = lone("youngbank-control", l -> 2 * l.principal0);
        double equityC = control.equity;
        double cushion = control.equity - Bank.CAPITAL_RATIO * control.weighted;
        double income = Math.abs(control.equity - control.equity0);
        double lossA = LOSS_UNDER_CUSHION * cushion;
        double lossB = (cushion + equityC) / 2;
        double lossC = LOSS_PAST_EQUITY * equityC;
        Lone a = lone("youngbank-a", l -> l.principal0 + l.interest0 - lossA);
        Lone b = lone("youngbank-b", l -> l.principal0 + l.interest0 - lossB);
        Lone c = lone("youngbank-c", l -> l.principal0 + l.interest0 - lossC);
        Lone[] twins = { a, b, c };

        out.println("--- 1. the fixture: a lone plant's loan above the young bank's whole equity, its loss taken from both sides of the bank's cushion ---");
        out.printf(java.util.Locale.ROOT, "   as the write-off month begins: equity %s, the lone sector owes %s (%.2f times it), weighted book %s, ratio %.4f; with the lone loan's weight on it the cushion over %.2f reads %s%n",
                k(control.equity0), k(control.principal0), control.principal0 / control.equity0, k(control.weighted0), control.ratio0,
                Bank.CAPITAL_RATIO, k(control.cushion0));
        out.printf(java.util.Locale.ROOT, "   control (the till pays the claim in full): nothing written off (%s), equity %s -> %s, its own month %+.1fk; weighted book %s, ratio %.4f; the cushion over the minimum on the book a loss leaves %s%n",
                k(control.writtenOff), k(control.equity0), k(control.equity), control.equity - control.equity0, k(control.weighted), control.ratio, k(cushion));
        String[] sides = { "(a) a loss under the cushion", "(b) a loss past the cushion, within equity", "(c) a loss past equity" };
        for (int i = 0; i < twins.length; i++) {
            Lone t = twins[i];
            out.printf(java.util.Locale.ROOT, "   %s: till %s, written off %s (%.2f of the cushion, %.2f of equity), equity %s, weighted book %s, ratio %.4f against %.2f, %s, %d people%n",
                    sides[i], k(t.till), k(t.loss()), t.loss() / cushion, t.loss() / equityC, k(t.equity), k(t.weighted), t.ratio, Bank.CAPITAL_RATIO,
                    t.resolution ? "in resolution" : "not in resolution", t.people);
        }
        for (Lone t : twins) if (t.spell) out.println(t.spellLine());

        check("fixture: a sector of one plant owes the bank more than the bank's whole equity as its plant closes, in each twin",
                control.aloneAtFounding && control.onePlant0 && a.onePlant0 && b.onePlant0 && c.onePlant0
                && control.principal0 > control.equity0 && a.principal0 > a.equity0 && b.principal0 > b.equity0 && c.principal0 > c.equity0
                && c.principal0 > lossC);
        check("fixture: the twins of one founding stand alike as the write-off month begins (equity, weighted book, what the lone sector owes), the bank over its minimum with a cushion",
                near(a.equity0, control.equity0) && near(b.equity0, control.equity0) && near(c.equity0, control.equity0)
                && near(a.weighted0, control.weighted0) && near(b.weighted0, control.weighted0) && near(c.weighted0, control.weighted0)
                && near(a.principal0, control.principal0) && near(b.principal0, control.principal0) && near(c.principal0, control.principal0)
                && control.ratio0 >= Bank.CAPITAL_RATIO && control.ratio >= Bank.CAPITAL_RATIO && cushion > 0);
        check("fixture: the control writes nothing off, and each loss lands where it was sized with a margin the control's own month cannot cross",
                near(control.writtenOff, 0) && cushion - a.loss() > income
                && b.loss() - cushion > income && equityC - b.loss() > income
                && c.loss() - equityC > income && a.loss() < cushion);
        check("fixture: the write-off month begins in every listing's first year on record (Equity.RECORD_MONTHS), in each twin: the desk's shares are priced on prospects, so the control's own month is the bank's income and no re-mark of the desk's holding",
                !control.onRecord0 && !a.onRecord0 && !b.onRecord0 && !c.onRecord0);
        check("a loss under the bank's cushion over its minimum leaves the ratio at or over Bank.CAPITAL_RATIO, and no spell begins",
                a.ratio >= Bank.CAPITAL_RATIO && !a.spell);
        check("a loss past the cushion and within equity puts the ratio under Bank.CAPITAL_RATIO, and a spell under Bank.CAPITAL_RATIO begins with the write-off of a sector of one plant",
                b.ratio < Bank.CAPITAL_RATIO && b.equity > 0 && b.spell && b.lone && LONE.equals(b.cause));

        out.println("--- 2. the young town's bank is in resolution only when a loss passes its equity ---");
        out.printf(java.util.Locale.ROOT, "   control %s, (a) %s, (b) %s: equity %s, %s, %s; (c) %s, equity %s, %d failure(s) in the month; the treasury's rescue setting %s; %d people%n",
                control.resolution ? "in resolution" : "standing", a.resolution ? "in resolution" : "standing", b.resolution ? "in resolution" : "standing",
                k(control.equity), k(a.equity), k(b.equity),
                c.resolution ? "in resolution" : "standing", k(c.equity), c.failures - c.failures0,
                c.town.getFund().getRescueMode(), c.people);
        check("the bank is never in resolution",
                !control.resolution0 && !a.resolution0 && !b.resolution0
                && !control.resolution && !a.resolution && !b.resolution
                && control.failures == control.failures0 && a.failures == a.failures0 && b.failures == b.failures0);
        check("a loss past the bank's equity (equity() under zero, Bank.resolveIfFailed's threshold) puts it in resolution, with the treasury on its button setting",
                c.town.getFund().getRescueMode() == TreasuryFund.RescueMode.BUTTON
                && !c.resolution0 && c.equity < 0 && c.resolution && c.failures == c.failures0 + 1);

        // 3: a first plant, weighed as the month begins, placed, and read alone.
        out.println("--- 3. a first plant is weighed at the rate it runs at ---");
        Game labour = young("youngbank-first", true, LABOUR_MONTHS);
        BuildingsTemplate plant = BankCheck.template(labour, FIRST_PLANT);
        Sector sector = labour.getSectors().byKey(plant.getSector());
        NewUnit unit = labour.getBusinessInvestment().newUnit(plant);
        boolean empty = sector.runsNothing() && sector.buildingsStanding() == 0 && sector.buildingsOnSite() == 0;
        double planned = sector.plannedRate(unit);
        double own = BusinessInvestment.operatingRateOf(sector.getOperatingRate());
        double[] fills = labour.getPopulationManager().getJobFillRate();
        JobType[] types = JobType.values();
        boolean short_ = false;
        long posts = 0;
        for (int i = 0; i < types.length && i < fills.length; i++) {
            posts += unit.getJobs(types[i]);
            if (unit.getJobs(types[i]) > 0 && fills[i] < 1) short_ = true;
        }
        int month = labour.getMonth();
        Game.BuildResult placed = null;
        First first = new First(sector.key(), plant, month, planned, own);
        for (int m = 0; m < RUN_MONTHS_AT_LEAST; m++) {
            if (m == 0) {
                Game.BuildResult[] r = new Game.BuildResult[1];
                quietly(() -> r[0] = labour.buildStack(plant, 1, true));
                placed = r[0];
            }
            quietly(() -> labour.simulateMonths(1));
            if (sector.buildingsStanding() == 1) first.ran.add(sector.getOperatingRate());
        }
        String ran = first.ran.isEmpty() ? "not open"
                : String.format(java.util.Locale.ROOT, "ran at %.3f-%.3f, %.3f on average, over %d month(s) alone",
                        first.ran.stream().mapToDouble(Double::doubleValue).min().orElse(Double.NaN),
                        first.ran.stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN), first.run(), first.ran.size());
        out.printf(java.util.Locale.ROOT, "   month %d: %s's %s (%d posts) weighed at %.3f (its sector's own rate %.3f); %s%n",
                first.month, first.sector, plant.getName(), posts, first.weighed, first.own, ran);
        boolean counted = first.counted();
        double missWeighed = counted ? Math.abs(first.weighed - first.run()) : Double.NaN;
        double missOwn = counted ? Math.abs(first.own - first.run()) : Double.NaN;
        boolean further = counted && missWeighed > missOwn;
        out.printf(java.util.Locale.ROOT, "   the first plant ran %d months or more: missed its run by %.3f as weighed, by %.3f at its sector's own rate%n",
                RUN_MONTHS_AT_LEAST, missWeighed, missOwn);
        check("fixture: the sector runs nothing, and the town's labour cannot fill the plant's posts (a fill under 1 in a tier it has posts in), so it is weighed under its sector's own rate",
                empty && short_ && planned < own);
        check("the fixture places a first plant in a sector with nothing running, and it runs " + RUN_MONTHS_AT_LEAST + " months alone",
                placed == Game.BuildResult.SUCCESS && counted);
        check("no first plant is weighed further from the rate it ran at than its sector's own rate", counted && !further);
        check("in all the first plants are weighed nearer the rates they ran at than their sectors' own rates (the estimate before OE1)",
                counted && missWeighed < missOwn);

        out.println(fails == 0 ? "YoungBankCheck: all passed" : "YoungBankCheck: " + fails + " FAILED");
        System.exit(fails);
    }
}
