package ham.citybuildersim;

import java.util.Locale;

/**
 * The owner families (BO1): one two-adult household per company that has
 * listed and not gone broke, holding the company's founding stake alone.
 *
 * Jerus, 2026-11-02, on the households study: business owners are "few in
 * nature and very rich", "one per listed company", "No job, lives of
 * dividends or sells or whatever"; "the others still have shares, they just
 * dont get the initial ownership". Every fixture here is a bare balance and a
 * bare register, the cause set by the fixture and not stood next to.
 *
 *   1. THE LINE OFF: a bare balance that never switched the line on gets
 *      today's founders' grant, by grown-ups, every non-empty cell its share.
 *   2. THE LINE ON, NO OWNER HOUSEHOLD YET: the listing waits (listIfUnlisted
 *      says false, nothing is in issue), the family is reserved, and a
 *      company with a book and no owners raises nothing at its first offering.
 *   3. THE LINE ON, WITH HOUSEHOLDS: the founders' shares go to the owner cell
 *      alone, each family an equal part of every stake; the row, the label and
 *      the save of the line.
 *   4. EVERYONE ELSE STILL BUYS: an offering is taken up by the ordinary cells
 *      and never by the owner cell, even when it is the richest.
 *   5. THE OWNERS TAKE NO JOB: their adults come out of the adult pool with
 *      the outside adults (FamilyModel) and out of the labour force
 *      (PopulationManager).
 *
 * Slice B of BO1 (the owners' slow sale, the broke rule, the leaving family
 * keeping its shares in the line) appends sections 6 onwards.
 */
public class OwnerCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Relative to the larger of the two, so a figure of any size is held to the same 1e-9. */
    static void close(String label, double actual, double expected, double rel) {
        boolean ok = Math.abs(actual - expected) <= rel * Math.max(1, Math.max(Math.abs(actual), Math.abs(expected)));
        if (!ok) fails++;
        System.out.printf("%-96s %14.6f  expected %14.6f  %s%n", label, actual, expected, ok ? "OK" : "FAIL");
    }

    /** The owners the fixture's census names: set by a section, read by the balance's outside census. */
    static final double[] owners = new double[1];

    /** A bare balance in today's dollar: a hundred unskilled couples and forty skilled single adults, owners as the census says. */
    static HouseholdBalance town(boolean lineOn) {
        HouseholdBalance hb = new HouseholdBalance();
        hb.seedConstants(PriceLevel.FACTOR);
        hb.setOwnerLine(lineOn);
        hb.setOutsideCensus(c -> c instanceof OwnerHousehold ? owners[0] : 0);
        month(hb);
        return hb;
    }

    /** One month's census, with the owner count in owners[0]. */
    static void month(HouseholdBalance hb) {
        double[][] mix = new double[FamilyStructure.values().length][PayTier.values().length];
        mix[FamilyStructure.COUPLE.ordinal()][PayTier.UNSKILLED.ordinal()] = 100;
        mix[FamilyStructure.SINGLE_ADULT.ordinal()][PayTier.SKILLED.ordinal()] = 40;
        double[] income = new double[HouseholdBalance.ROWS];
        income[PayTier.UNSKILLED.ordinal()] = 100 * 4;
        income[PayTier.SKILLED.ordinal()] = 40 * 6;
        double[] none = new double[HouseholdBalance.ROWS];
        double[] spent = new double[HouseholdBalance.ROWS];
        hb.advanceMonth((s, t) -> mix[s.ordinal()][t.ordinal()], income, 0, none, spent, .25, .05, 1);
    }

    static Equity register() {
        Equity e = new Equity();
        e.seedConstants(PriceLevel.FACTOR);
        return e;
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.CANADA);
        int IND = Equity.indexOf(Sectors.INDUSTRY);
        int RETAIL = Equity.indexOf(Sectors.RETAIL);
        double W = DebtManager.WORLD_BASE_RATE;
        double book = 20_000;

        /* ============ 1. the line off: today's grant ============ */
        System.out.println("--- the line off: today's grant ---");
        {
            owners[0] = 0;
            HouseholdBalance hb = town(false);
            Equity e = register();
            double founders = book / e.foundingPrice();
            check("fixture: the line was never switched on", !hb.isOwnerLine() && hb.ownerFamilies() == 0);
            check("a company with equity and no owners lists", e.listIfUnlisted(IND, book, hb));
            close("the founders' shares are all in the households' hands", hb.sharesHeld(IND), founders, 1e-9);
            close("...and the register issued exactly them", e.getShares(IND), founders, 1e-9);
            Household couple = hb.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED);
            Household single = hb.cell(FamilyStructure.SINGLE_ADULT, PayTier.SKILLED);
            close("a couple holds twice a single adult's stake: by grown-ups, as before", couple.shares[IND], 2 * single.shares[IND], 1e-9);
            check("...and every non-empty cell holds some", couple.shares[IND] > 0 && single.shares[IND] > 0);
            check("...and the owner cell, empty, holds none", hb.owners().shares[IND] == 0 && hb.owners().isEmpty());
            check("...and no family was reserved", !hb.hasOwnerFamily(IND) && hb.ownerFamilies() == 0);
        }

        /* ============ 2. the line on, no owner household yet: the listing waits ============ */
        System.out.println("\n--- the line on, no owner household yet: the listing waits ---");
        owners[0] = 0;
        HouseholdBalance hb = town(true);
        Equity e = register();
        {
            check("fixture: the line is on and the owner cell is empty", hb.isOwnerLine() && hb.owners().isEmpty());
            check("a company with equity does not list: its family does not exist yet", !e.listIfUnlisted(IND, book, hb));
            close("...the register has no shares in issue", e.getShares(IND), 0, 1e-12);
            close("...and no household holds any", hb.sharesHeld(IND), 0, 1e-12);
            check("...but its family is reserved: the census forms it next month",
                    hb.ownerFamilies() == 1 && hb.hasOwnerFamily(IND) && !hb.hasOwnerFamily(RETAIL));

            double raised = e.offer(RETAIL, 5_000, book, hb, W);
            close("an offering by a company with a book and no owners raises nothing", raised, 0, 1e-12);
            check("...opens no offering and issues no share",
                    e.getOfferings(RETAIL) == 0 && e.getOfferedThisMonth(RETAIL) == 0 && e.getShares(RETAIL) == 0);
            check("...and reserves its family too", hb.ownerFamilies() == 2 && hb.hasOwnerFamily(RETAIL));
        }

        /* ============ 3. the line on, with households: the founders' shares to the owners alone ============ */
        System.out.println("\n--- the line on, with households: the founders' shares to the owners alone ---");
        double founders = book / e.foundingPrice();
        Household ownerCell = hb.owners();
        {
            owners[0] = hb.ownerFamilies();     // what the census names after the month: the reserved count
            month(hb);
            close("fixture: the census formed the families the grant reserved", ownerCell.households(), 2, 1e-12);
            check("a company that waited for its family lists", e.listIfUnlisted(IND, book, hb));
            close("the owners' stake times their households is the founders' shares",
                    ownerCell.shares[IND] * ownerCell.households(), founders, 1e-9);
            boolean others = true;
            for (Household c : hb.cells()) if (c != ownerCell && c.shares[IND] != 0) others = false;
            check("every other cell holds none of the company", others);
            close("the households hold what the register issued", hb.sharesHeld(IND), e.getShares(IND), 1e-9);
            close("...which is the founders' shares, and nothing was raised", e.getShares(IND), founders, 1e-9);

            check("the owner cell is its own row, after the prisoners",
                    Household.OWNER_ROW == Household.PRISON_ROW + 1 && ownerCell.row() == Household.OWNER_ROW
                    && Household.ROWS == Household.OWNER_ROW + 1);
            check("...and a save from before the prisons still reads ten rows, not eleven",
                    Household.ROWS_BEFORE_PRISON == Household.RETIRED_ROW + 4
                    && Household.ROWS_BEFORE_OWNER == Household.RETIRED_ROW + 5);
            check("...two adults, no wage, no job to split an income by",
                    ownerCell.grownUps() == 2 && ownerCell.size() == 2 && ownerCell.earningWeight() == 0 && ownerCell.tier() == null);
            check("...which keeps its bonds and money abroad but not the households' share trading",
                    ownerCell.canInvest() && !ownerCell.tradesShares());
            check("...and every other cell trades shares by the households' rules",
                    hb.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED).tradesShares() && hb.prisoners().tradesShares() == hb.prisoners().canInvest());
            check("...the books name the row", new HouseholdAccounts().getRowLabel(HouseholdAccounts.OWNERS).equals("Owner families"));

            // The line goes through a save and back.
            java.util.Map<String, Integer> line = hb.ownerLineForSave();
            check("the line for the save names the companies with a family, in company order",
                    line.size() == 2 && new java.util.ArrayList<>(line.keySet()).equals(java.util.List.of(
                            Equity.COMPANIES[Math.min(IND, RETAIL)], Equity.COMPANIES[Math.max(IND, RETAIL)])));
            HouseholdBalance back = town(true);
            java.util.Map<String, Integer> saved = new java.util.LinkedHashMap<>(line);
            saved.put(Equity.COMPANIES[IND], 7);
            saved.put("No Such Company", 3);
            back.restoreOwnerLine(saved);
            check("a line restored keeps its families and its months at the floor, and skips a key it has no company for",
                    back.ownerFamilies() == 2 && back.hasOwnerFamily(IND) && back.ownerLineForSave().get(Equity.COMPANIES[IND]) == 7);
            back.restoreOwnerLine(null);
            check("a save from before BO1 restores an empty line", back.ownerFamilies() == 0 && !back.hasOwnerFamily(IND));
        }

        /* ============ 4. everyone else still buys ============ */
        System.out.println("\n--- everyone else still buys ---");
        {
            Household couple = hb.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED);
            Household single = hb.cell(FamilyStructure.SINGLE_ADULT, PayTier.SKILLED);
            couple.savings = 1_000;
            single.savings = 800;
            ownerCell.savings = 5_000_000;    // the richest cell in town, by far
            double ownerSharesBefore = ownerCell.shares[IND], ownerSavingsBefore = ownerCell.savings;
            double coupleSharesBefore = couple.shares[IND], singleSharesBefore = single.shares[IND];
            check("fixture: the owner cell is the richest and would qualify by the households' rules but for the line",
                    ownerCell.savings > couple.savings && ownerCell.canInvest() && !ownerCell.isEmpty());
            double taken = hb.subscribe(IND, 10_000_000, 1.0);
            check("an offering is taken up", taken > 0);
            check("the owner cell buys nothing: its shares unchanged", ownerCell.shares[IND] == ownerSharesBefore);
            check("...and its savings unchanged, richest as it is", ownerCell.savings == ownerSavingsBefore);
            check("an ordinary cell with savings past the cushion buys: the couples",
                    couple.shares[IND] > coupleSharesBefore && couple.savings < 1_000);
            check("...and the single adults", single.shares[IND] > singleSharesBefore && single.savings < 800);
            close("...and the money paid is the money taken", taken,
                    (1_000 - couple.savings) * couple.households() + (800 - single.savings) * single.households(), 1e-9);
        }

        /* ============ 5. the owners take no job ============ */
        System.out.println("\n--- the owners take no job ---");
        {
            int tiers = PayTier.values().length;
            PopulationCohorts c = new PopulationCohorts();
            double[] pyr = new double[AgeBand.values().length + 3];
            for (AgeBand b : AgeBand.values()) pyr[b.ordinal()] = 18_000 * PopulationCohorts.equilibriumShare(b);
            c.restore(PopulationCohorts.saveBands(), pyr);
            double[] jobs = new double[tiers];
            jobs[PayTier.UNSKILLED.ordinal()] = 700;
            jobs[PayTier.SKILLED.ordinal()] = 300;
            double allAdults = c.get(AgeBand.ADULT);
            int N = 7;

            FamilyModel f = new FamilyModel();
            f.rebuild(c, jobs, 0, 0, 2.0 * N);
            check("the families count the owner families: two adults each", Math.abs(f.getOwnerFamilies() - N) < 1e-12
                    && Math.abs(f.getOwnerAdults() - 2.0 * N) < 1e-12);
            close("...they are outside the families, with the outside adults", f.getOutsideAdults(), 2.0 * N, 1e-12);
            close("...so the families hold the adult cohort less them", inFamilies(f) + f.getOutsideAdults(), allAdults, 1e-9);

            FamilyModel three = new FamilyModel();
            three.rebuild(c, jobs, 100, 200, 2.0 * N);
            close("with prisoners and the out of work, all three are outside the families", three.getOutsideAdults(), 100 + 200 + 2.0 * N, 1e-9);
            close("...and the families hold the rest", inFamilies(three) + three.getOutsideAdults(), allAdults, 1e-9);

            FamilyModel clamped = new FamilyModel();
            clamped.rebuild(c, jobs, 100, 200, 1e12);
            close("more owner adults than the adults left are clamped to what is left", clamped.getOwnerAdults(), allAdults - 300, 1e-9);
            close("...the adult cohort is still the families' and the outside's together",
                    inFamilies(clamped) + clamped.getOutsideAdults(), allAdults, 1e-9);

            FamilyModel old = new FamilyModel();
            old.rebuild(c, jobs, 100, 200);
            check("the four-argument form is the owner adults at nothing",
                    old.getOwnerAdults() == 0 && old.getOwnerFamilies() == 0 && old.getOutsideAdults() == 300);

            PopulationManager pm = new PopulationManager();
            pm.setWagesPerType();
            long[] posts = new long[JobType.values().length];
            posts[JobType.NO_DIPLOMA.ordinal()] = 50;
            pm.updateJobs(posts);
            pm.applyPopulation(200, 100);
            double without = pm.getLabourForce();
            double bandsWithout = sum(pm.workforceByBand());
            pm.setOwners(2.0 * N);
            check("fixture: a labour force larger than the owners' adults", without > 2.0 * N);
            close("the labour force is lower by exactly the owners' adults", pm.getLabourForce(), without - 2.0 * N, 1e-12);
            close("...and so is the workforce by band, taken off every band in proportion", sum(pm.workforceByBand()), bandsWithout - 2.0 * N, 1e-9);
            pm.setOwners(1e9);
            close("more owners than the labour force leaves it at zero, never below", pm.getLabourForce(), 0, 1e-12);
            check("...and no band below zero", java.util.Arrays.stream(pm.workforceByBand()).allMatch(v -> v >= 0));
        }

        System.out.println(fails == 0 ? "\nOwnerCheck: all OK" : "\nOwnerCheck: " + fails + " FAIL");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** The adults the family matrix holds. */
    static double inFamilies(FamilyModel f) {
        double adults = 0;
        for (FamilyStructure s : FamilyStructure.values()) adults += f.totalOf(s) * s.membersOf(AgeBand.ADULT);
        return adults;
    }

    static double sum(double[] v) {
        double s = 0;
        for (double x : v) s += x;
        return s;
    }
}
