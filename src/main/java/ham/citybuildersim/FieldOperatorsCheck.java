package ham.citybuildersim;

import ham.citybuildersim.sectors.Agriculture;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;

/**
 * Driverless field work (R6d, FE8: Deere's goal of a fully autonomous corn and soybean season by 2030, projected 2033, window 2030-2036): the autonomy kit,
 * the third piece of equipment on the Grain Farm and the Mixed Farm, and what the farms that carry it do to their posts: the operator of each tractor and
 * combine struck, a remote watch asked for in his place.
 *
 * WHY. The kit changes three things at once that a month's figures would hide if one were wrong: how many posts the farms offer (so the wage bill and the
 * labour market, which the city's own count of posts has to agree with), what the kit is worth to a farm (so whether it is ever fitted, and the patent rent
 * it owes), and which farms count as autonomous (so EquipmentForms' weights). And it must change none of them where the piece does not exist: a city
 * founded today's way has every farm standing "fitted" only because every count there is, so its month is today's, to the bit. Each figure is held to the
 * model's own constants (Agriculture.OPERATORS_A_MACHINE, WATCH_A_MACHINE and machinesOf()), never a number typed here, and each condition is caused from
 * both sides: the month before and the month of FE8's date, a farm lacking the kit and a farm with it, a city founded the 1900 way and one founded today's.
 *
 * What this has to prove:
 *
 *   1. THE DIALS: the share still operated is 1 until set, clamped to [0, 1], a number that is not one reads 1; a machine is a tractor or a combine the
 *      template carries (the Mixed Farm one, the Grain Farm two, a bakery none); the machines that work by themselves are the farms' machines times the
 *      share that is not operated, and none on a farm that is on horses
 *   2. THE POSTS: at a share of 1 the farms offer the buildings' posts, to the bit, and the city's count adds none; at 0 they offer the operators' struck
 *      from NO_DIPLOMA and the watch added to DIPLOMA, rounded once on the sector's whole, and the city's count (BuildingManager's getTotalJobs()) falls by
 *      the same; every other tier is untouched; a farm on horses strikes none
 *   3. AT FE8: the kit is NOT_KNOWN the month before FE8 and every machine is operated; at FE8's month it is known, its gross gain is operatorGain() at the
 *      city's own wages and machines, positive, and its verdict is the one EquipmentFit's rule gives with the patent rent off; a city whose research found
 *      FE8 sooner knows it from the month it found it, and owes no rent on it
 *   4. THE FARMS' COUNTS: the operated share is the machine-weighted share of the farms lacking the kit, and it stands after a month is played; the last
 *      farm fitted takes it to 0, a count that returns to 1
 *   5. A DEFAULT CITY: founded today's way, with farms, past FE8's month, the share is exactly 1 and the posts the buildings', even where a count of the
 *      kit is put on a farm by hand
 *   6. A PLAYED COUNT: the 1900 village played 24 months from FE8's month, every other piece in it put fitted by hand, the months its operated share was
 *      below 1, which is more than none if the verdict of section 3 was PAYS
 *
 * Every fixture is FittingCheck.village() with Grain Farms added (placed without construction, in a GameFiles.scratch() directory: no save slot is written),
 * said over by unfitTheFounding() in a city founded the 1900 way, with its month set by reflection where a date is the premise (ResearchCheck.atMonth()).
 */
public class FieldOperatorsCheck {

    static int fails;
    static int passes;

    static PrintStream quiet;

    static final String KIT = "autonomy-kit";
    /** The Grain Farms stood beside the village's own one: enough that the watch (a tenth of a post a machine) rounds to posts. */
    static final int GRAINS = 20;

    static void check(String label, boolean ok) {
        System.out.println((ok ? "  PASS " : "  FAIL ") + label);
        if (ok) passes++; else fails++;
    }

    static void check(String label, boolean ok, String found) {
        check(label, ok);
        if (!ok && found != null) System.out.println("       found: " + found);
    }

    static void near(String label, double actual, double expected, double tolerance) {
        check(label, Math.abs(actual - expected) <= tolerance, String.format("%.9g against %.9g (tolerance %g)", actual, expected, tolerance));
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** The streams the borrowed fixtures are quiet through, set once and never over another harness's. */
    static void ensureQuiet() {
        if (quiet == null) quiet = new PrintStream(OutputStream.nullOutputStream());
        if (FittingCheck.quiet == null) FittingCheck.quiet = quiet;
        if (EquipmentCheck.quiet == null) EquipmentCheck.quiet = quiet;
        if (Forms1900Check.quiet == null) Forms1900Check.quiet = quiet;
        if (DiscoveryCheck.quiet == null) DiscoveryCheck.quiet = quiet;
    }

    static void play(Game g, int months) {
        quietly(() -> { for (int i = 0; i < months; i++) g.toggleNextMonth(); });
    }

    /** FE8's month in history: the model's own, never a literal. */
    static int fe8() {
        return ResearchCatalog.get().entry("FE8").worldMonth();
    }

    static Agriculture farms(Game g) {
        return g.getSectors().agriculture();
    }

    static BuildingsStacks mixed(Game g) {
        return EquipmentCheck.stack(g, "Mixed Farm");
    }

    static BuildingsStacks grain(Game g) {
        return EquipmentCheck.stack(g, "Grain Farm");
    }

    /** A village with Mixed Farms and Grain Farms (the fixture's two, one, and GRAINS more), founded the 1900 way (every building without every piece but the tractors and combines, which FE8's year has everywhere) or today's. */
    static Game village(String label, boolean founded1900) {
        ensureQuiet();
        Game g = FittingCheck.village(label, Founding.defaults().withUnfitted(founded1900), false, false);
        quietly(() -> {
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("Grain Farm"), GRAINS, true);
            if (founded1900) {
                g.unfitTheFounding();
                // ...and the tractors and the combines on, as a farm has them by FE8's year (PE14 is 1917, PE17 1939): no farm is on horses, so each machine has its operator.
                for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
                    for (Equipment e : s.getBuilding().getEquipment()) if (e.getId().equals("tractor") || e.getId().equals("combine")) s.setUnfitted(e.getId(), 0);
                }
                g.getEquipmentForms().apply(g);
            }
        });
        return g;
    }

    /** The farms' machines, written out from the stacks: the tractors and combines the templates carry, over the buildings standing. */
    static double machines(Game g) {
        double n = 0;
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            if (!s.getBuilding().getSector().equals(farms(g).key())) continue;
            for (Equipment e : s.getBuilding().getEquipment()) if (e.getId().equals("tractor") || e.getId().equals("combine")) n += s.getQuantity();
        }
        return n;
    }

    /** The share of the machines on farms lacking the kit, written out from the stacks' own counts. */
    static double lackingShare(Game g) {
        double total = 0, off = 0;
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            if (!s.getBuilding().getSector().equals(farms(g).key())) continue;
            double m = 0;
            for (Equipment e : s.getBuilding().getEquipment()) if (e.getId().equals("tractor") || e.getId().equals("combine")) m++;
            if (m == 0) continue;
            total += s.getQuantity() * m;
            off += Math.min(s.getUnfitted(KIT), s.getQuantity()) * m;
        }
        return total > 0 ? off / total : 1;
    }

    static long[] base(Game g) {
        return g.getBuildingManager().getJobArrayBySector(farms(g).key());
    }

    /** The wages the planner reads for the two tiers, as the city's prices carry them. */
    static double[] wages(Game g) {
        double[] w = Recipes.pricesOf(g).wages();
        return new double[] { w[JobType.NO_DIPLOMA.ordinal()], w[JobType.DIPLOMA.ordinal()] };
    }

    public static void main(String[] args) throws Exception {
        ensureQuiet();
        one();
        two();
        Game g = village("fieldops-1900", true);
        three(g);
        four();
        five();
        six();
        System.out.println(fails == 0 ? "FieldOperatorsCheck: all " + passes + " ok" : "FieldOperatorsCheck: " + fails + " FAILED of " + (passes + fails));
        System.exit(fails);
    }

    /* ============================ 1. the dials ============================ */

    static void one() {
        System.out.println("\n--- 1. the dials: the share still operated, a machine, the machines that work by themselves ---");
        Agriculture a = new Agriculture();
        check("fixture: an operator is a post and the watch is less of one", Agriculture.OPERATORS_A_MACHINE > 0 && Agriculture.WATCH_A_MACHINE >= 0
                && Agriculture.WATCH_A_MACHINE < Agriculture.OPERATORS_A_MACHINE);
        check("a sector nobody set reads 1: the default is every machine operated", a.getOperatedShare() == 1);
        a.setOperatedShare(0.4);
        check("a share is kept", a.getOperatedShare() == 0.4);
        a.setOperatedShare(-3);
        check("a share below 0 is clamped to 0", a.getOperatedShare() == 0);
        a.setOperatedShare(7);
        check("...above 1 to 1", a.getOperatedShare() == 1);
        a.setOperatedShare(0);
        a.setOperatedShare(Double.NaN);
        check("...and one that is not a number reads 1, today's", a.getOperatedShare() == 1);

        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        BuildingsTemplate mixed = b.getTemplateByName("Mixed Farm"), grain = b.getTemplateByName("Grain Farm"), bakery = b.getTemplateByName("Bakery");
        check("the Mixed Farm carries one machine (its tractor), the Grain Farm two (its tractor and its combine), a bakery none",
                Agriculture.machinesOf(mixed) == 1 && Agriculture.machinesOf(grain) == 2 && Agriculture.machinesOf(bakery) == 0,
                Agriculture.machinesOf(mixed) + " " + Agriculture.machinesOf(grain) + " " + Agriculture.machinesOf(bakery));
        check("both farms carry the kit, since FE8 and ending FIELD_OPERATORS",
                mixed.getEquipment(KIT) != null && grain.getEquipment(KIT) != null && mixed.getEquipment(KIT).getSince().equals("FE8")
                        && grain.getEquipment(KIT).getEnds() == Equipment.Form.FIELD_OPERATORS && bakery.getEquipment(KIT) == null);

        Game g = village("fieldops-dials", true);
        Agriculture f = farms(g);
        double machines = machines(g);
        check("fixture: the village runs the farms' machines, " + (2 * 1 + (1 + GRAINS) * 2), f.machines() == machines && machines == 2 * 1 + (1 + GRAINS) * 2,
                f.machines() + " " + machines);
        f.setOperatedShare(1);
        check("at a share of 1 no machine works by itself", f.autonomousMachines() == 0);
        f.setOperatedShare(0);
        check("at 0 every machine does", f.autonomousMachines() == machines, "" + f.autonomousMachines());
        f.setOperatedShare(0.25);
        near("at a quarter, three quarters of them", f.autonomousMachines(), 0.75 * machines, 1e-9);
        f.setHorseShare(1);
        f.setOperatedShare(0);
        check("a farm all on horses has no tractor to fit: none works by itself at any share", f.autonomousMachines() == 0, "" + f.autonomousMachines());
        f.setHorseShare(0.5);
        near("half on horses, at most half the machines do", f.autonomousMachines(), 0.5 * machines, 1e-9);
        f.setHorseShare(0);
    }

    /* ============================ 2. the posts ============================ */

    static void two() {
        System.out.println("\n--- 2. the posts: the operators struck from NO_DIPLOMA, the watch added to DIPLOMA, the city's count with them ---");
        Game g = village("fieldops-posts", true);
        Agriculture a = farms(g);
        BuildingManager b = g.getBuildingManager();
        long[] base = base(g);
        int hands = JobType.NO_DIPLOMA.ordinal(), watch = JobType.DIPLOMA.ordinal();
        double machines = machines(g);
        check("fixture: the farms post hands to strike, more than the machines' operators", base[hands] > machines * Agriculture.OPERATORS_A_MACHINE && machines > 0,
                base[hands] + " posts, " + machines + " machines");
        long cityHands = b.getTotalJobs(JobType.NO_DIPLOMA), cityWatch = b.getTotalJobs(JobType.DIPLOMA);
        a.setOperatedShare(1);
        check("at a share of 1 the farms offer the buildings' posts, to the bit, and run none past them", Arrays.equals(a.postsPerTier(), base) && a.postsPastBuildings() == null);
        check("...and the city counts its buildings' posts", b.getTotalJobs(JobType.NO_DIPLOMA) == cityHands && b.getTotalJobs(JobType.DIPLOMA) == cityWatch);

        a.setOperatedShare(0);
        long[] posts = a.postsPerTier();
        long struck = Math.round(machines * Agriculture.OPERATORS_A_MACHINE), asked = Math.round(machines * Agriculture.WATCH_A_MACHINE);
        check("fixture: the watch rounds to some posts", asked > 0, "" + asked);
        check("at 0 the operators are struck from the hands: " + base[hands] + " -> " + posts[hands], posts[hands] == base[hands] - struck, "" + posts[hands]);
        check("...and the watch is added to the DIPLOMA posts: " + base[watch] + " -> " + posts[watch], posts[watch] == base[watch] + asked, "" + posts[watch]);
        boolean others = true;
        for (int i = 0; i < base.length; i++) if (i != hands && i != watch) others &= posts[i] == base[i];
        check("...and every other tier is untouched", others);
        long[] past = a.postsPastBuildings();
        check("the posts past the buildings' are the difference, fewer hands and more watch", past != null && past[hands] == -struck && past[watch] == asked,
                past == null ? "null" : past[hands] + " " + past[watch]);
        check("the city's own count (BuildingManager.getTotalJobs()) follows: the labour market is offered the posts the payroll is struck on",
                b.getTotalJobs(JobType.NO_DIPLOMA) == cityHands - struck && b.getTotalJobs(JobType.DIPLOMA) == cityWatch + asked,
                b.getTotalJobs(JobType.NO_DIPLOMA) + " " + b.getTotalJobs(JobType.DIPLOMA));

        a.setOperatedShare(0.5);
        long[] half = a.postsPerTier();
        long halfStruck = Math.round(machines * 0.5 * Agriculture.OPERATORS_A_MACHINE), halfAsked = Math.round(machines * 0.5 * Agriculture.WATCH_A_MACHINE);
        check("at a half, half the machines' operators are struck and half the watch asked", half[hands] == base[hands] - halfStruck && half[watch] == base[watch] + halfAsked
                && half[hands] > posts[hands] && half[hands] < base[hands], half[hands] + " " + half[watch]);

        // A farm on horses: the posts are the horse form's alone, whatever the share of the kit.
        a.setOperatedShare(1);
        a.setHorseShare(1);
        long[] horses = a.postsPerTier();
        a.setOperatedShare(0);
        check("a farm all on horses offers the horse form's posts and no fewer for a kit it cannot carry", Arrays.equals(a.postsPerTier(), horses),
                Arrays.toString(a.postsPerTier()) + " against " + Arrays.toString(horses));
        a.setHorseShare(0);
        a.setOperatedShare(1);
        check("fixture: back at 1 and no horses, the buildings' posts again", Arrays.equals(a.postsPerTier(), base));

        // The payroll is struck on the posts: at the same wages fewer hands cost less and the watch more, by the posts' difference.
        a.setOperatedShare(1);
        double[] w = new double[JobType.values().length];
        Arrays.fill(w, 1);
        a.updateWages(w, a.postsPerTier());
        double whole = a.getPayrollAtFullStaffing();
        a.setOperatedShare(0);
        a.updateWages(w, a.postsPerTier());
        near("the farms' payroll at wages of one a post falls by the posts struck less the watch asked (the payroll reads the posts offered)", a.getPayrollAtFullStaffing(), whole - struck + asked,
                1e-9 * Math.max(1, whole));
        a.setOperatedShare(1);
        a.updateWages(w, a.postsPerTier());
    }

    /* ============================ 3. at FE8 ============================ */

    /** Whether the kit paid at FE8's month on the Grain Farm of section 3's village, for section 6's label. */
    static EquipmentFit.Kind verdictAtFe8;

    static void three(Game g) throws Exception {
        System.out.println("\n--- 3. at FE8: the kit is known from its month and not before, what it earns a farm is operatorGain(), and a find earlier knows it earlier ---");
        BuildingsStacks gr = grain(g), mx = mixed(g);
        Equipment kit = gr.getBuilding().getEquipment(KIT);
        check("fixture: the farms carry the kit and every farm lacks it", kit != null && kit.getEnds() == Equipment.Form.FIELD_OPERATORS && kit.getSince().equals("FE8")
                && gr.getUnfitted(KIT) == gr.getQuantity() && mx.getUnfitted(KIT) == mx.getQuantity() && fe8() > g.getMonth());
        check("fixture: FE8 is a patented speculative row, needing the tractor and the combine", ResearchCatalog.get().entry("FE8").fee() && ResearchCatalog.get().entry("FE8").projected()
                && ResearchCatalog.get().entry("FE8").needs().equals(java.util.List.of("PE14", "PE17")));
        // A month played first, so that the labour market has struck the wages the kit saves (a village is born with none).
        play(g, 1);
        ResearchCheck.atMonth(g, fe8() - 1);
        g.getEquipmentForms().apply(g);
        check("the month before FE8's the kit is NOT_KNOWN on the Grain Farm and the Mixed Farm", EquipmentFit.verdict(g, gr, kit).kind() == EquipmentFit.Kind.NOT_KNOWN
                && EquipmentFit.verdict(g, mx, mx.getBuilding().getEquipment(KIT)).kind() == EquipmentFit.Kind.NOT_KNOWN);
        check("...and every machine is operated: the share is 1 and the posts are the buildings'", farms(g).getOperatedShare() == 1 && Arrays.equals(farms(g).postsPerTier(), base(g)));

        ResearchCheck.atMonth(g, fe8());
        EquipmentFit.Verdict v = EquipmentFit.verdict(g, gr, kit);
        verdictAtFe8 = v.kind();
        check("in FE8's own month the kit is known: PAYS or NOT_YET, priced", (v.kind() == EquipmentFit.Kind.PAYS || v.kind() == EquipmentFit.Kind.NOT_YET)
                && Double.isFinite(v.gain()) && Double.isFinite(v.cost()) && Double.isFinite(v.realRate()) && Double.isFinite(v.hurdle()), v.toString());
        double[] w = wages(g);
        double gross = EquipmentFit.operatorGain(2, w[0], w[1]);
        check("fixture: the wages are above nothing and a machine's operator costs more than its watch", w[0] > 0 && w[1] > 0 && w[0] > Agriculture.WATCH_A_MACHINE * w[1], w[0] + " " + w[1]);
        near("the model's gross gain on a Grain Farm is operatorGain() of its two machines at the city's own wages", EquipmentFit.grossGainOf(g, gr.getBuilding(), kit), gross, 1e-9 * Math.abs(gross));
        near("...which is the operators' wages less the watch's (written out)", gross, 2 * (Agriculture.OPERATORS_A_MACHINE * w[0] - Agriculture.WATCH_A_MACHINE * w[1]), 1e-9 * Math.abs(gross));
        near("...and a Mixed Farm's, with one machine, is half of it", EquipmentFit.grossGainOf(g, mx.getBuilding(), mx.getBuilding().getEquipment(KIT)), gross / 2, 1e-9 * Math.abs(gross));
        check("...positive: the same machine without its operator earns something", gross > 0, "" + gross);
        double rent = LicenceFees.onGain(kit.getSince(), g.getMonth(), g.getResearch(), gross);
        check("FE8 is payable at its own month, so the rent is the patent's share of that gain (LicenceFees, unchanged)",
                LicenceFees.payable("FE8", g.getMonth(), g.getResearch()) && rent == LicenceFees.SHARE * gross, rent + " of " + gross);
        near("the verdict's gain is the gross less that rent", v.gain(), gross - rent, 1e-9 * Math.abs(gross));
        boolean rule = v.gain() > 0 && v.gain() * EquipmentFit.annuityFactor(kit.getLifeMonths(), v.realRate()) >= v.cost() && v.gain() >= v.hurdle();
        check("the verdict is PAYS exactly when EquipmentFit's rule holds (the gain's life earns back the cost and clears the hurdle): " + v.kind(),
                (v.kind() == EquipmentFit.Kind.PAYS) == rule, "gain " + v.gain() + " cost " + v.cost() + " hurdle " + v.hurdle());
        System.out.printf("  the kit on a Grain Farm at month %d: gain %.4f a month on a cost of %.4f, earnsBack %.4f, hurdle %.4f: %s%n", g.getMonth(), v.gain(), v.cost(),
                v.earnsBack(), v.hurdle(), v.kind());

        // A farm placed by hand with no count at all reads "fitted" by its counts, but a kit nobody knows yet is on no farm.
        Game bare = FittingCheck.village("fieldops-bare", Founding.defaults().withUnfitted(true), false, false);
        ResearchCheck.atMonth(bare, fe8() - 1);
        bare.getEquipmentForms().apply(bare);
        check("fixture: a 1900 village whose farms were placed by hand, no count of any piece on them", bare.foundedUnfitted() && !grain(bare).hasUnfitted() && !mixed(bare).hasUnfitted());
        check("the month before FE8 no count-less farm is autonomous (a kit nobody knows is on none): every machine is operated and the posts are the buildings'",
                farms(bare).getOperatedShare() == 1 && farms(bare).autonomousMachines() == 0 && Arrays.equals(farms(bare).postsPerTier(), base(bare)));
        ResearchCheck.atMonth(bare, fe8());
        bare.getEquipmentForms().apply(bare);
        check("...and at FE8's month the same farms, lacking nothing by their counts, are all autonomous", farms(bare).getOperatedShare() == 0 && farms(bare).autonomousMachines() == machines(bare),
                farms(bare).getOperatedShare() + " " + farms(bare).autonomousMachines());

        // A city whose research found FE8 before history's month knows the kit from that month.
        Game early = village("fieldops-found", true);
        int found = fe8() - 12;
        ResearchViewCheck.setFinds(early, Map.of("FE8", found));
        BuildingsStacks eGrain = grain(early);
        Equipment eKit = eGrain.getBuilding().getEquipment(KIT);
        ResearchCheck.atMonth(early, found - 1);
        check("a city that finds FE8 in month " + found + " has not the kit the month before it", EquipmentFit.verdict(early, eGrain, eKit).kind() == EquipmentFit.Kind.NOT_KNOWN);
        ResearchCheck.atMonth(early, found);
        EquipmentFit.Verdict known = EquipmentFit.verdict(early, eGrain, eKit);
        check("...and has it in the month it found it, twelve months before history's (" + known.kind() + ")",
                known.kind() == EquipmentFit.Kind.PAYS || known.kind() == EquipmentFit.Kind.NOT_YET, known.kind().toString());
        check("...owing no patent rent on what it found itself", !LicenceFees.payable("FE8", early.getMonth(), early.getResearch())
                && LicenceFees.onGain("FE8", early.getMonth(), early.getResearch(), 1000) == 0);
    }

    /* ============================ 4. the farms' counts ============================ */

    static void four() throws Exception {
        System.out.println("\n--- 4. the farms' counts: the operated share is the machine-weighted share of the farms lacking the kit ---");
        Game g = village("fieldops-half", true);
        ResearchCheck.atMonth(g, fe8());   // the kit is known: a count of it is a farm's lacking it
        Agriculture a = farms(g);
        BuildingsStacks mx = mixed(g), gr = grain(g);
        check("fixture: two Mixed Farms and " + (1 + GRAINS) + " Grain Farms, none fitted", mx.getQuantity() == 2 && gr.getQuantity() == 1 + GRAINS && a.getOperatedShare() == 1);
        mx.setUnfitted(KIT, mx.getQuantity() - 1);
        gr.setUnfitted(KIT, gr.getQuantity() - 5);
        g.getEquipmentForms().apply(g);
        double lacking = lackingShare(g);
        check("fixture: the share lacking is between none and all", lacking > 0 && lacking < 1, "" + lacking);
        near("the operated share is the machines on the farms lacking the kit over all the farms' machines", a.getOperatedShare(), lacking, 1e-12);
        near("...a Grain Farm weighing for its two machines: 1 Mixed Farm and 5 Grain Farms fitted is 1 + 5 x 2 machines of " + machines(g),
                a.autonomousMachines(), 1 + 5 * 2, 1e-9);
        long[] base = base(g), posts = a.postsPerTier();
        check("...so the farms offer " + (1 + 5 * 2) + " fewer hands, as the posts' rounding says", posts[JobType.NO_DIPLOMA.ordinal()] == base[JobType.NO_DIPLOMA.ordinal()] - Math.round(a.autonomousMachines()),
                posts[JobType.NO_DIPLOMA.ordinal()] + " against " + base[JobType.NO_DIPLOMA.ordinal()]);
        check("...a share between the two ends", a.getOperatedShare() > 0 && a.getOperatedShare() < 1);
        play(g, 1);
        near("a month played leaves it where the counts say", a.getOperatedShare(), lackingShare(g), 1e-12);
        mx.setUnfitted(KIT, 0);
        gr.setUnfitted(KIT, 0);
        g.getEquipmentForms().apply(g);
        check("the last farm fitted takes it to 0", a.getOperatedShare() == 0 && a.autonomousMachines() == machines(g));
        mx.setUnfitted(KIT, mx.getQuantity());
        gr.setUnfitted(KIT, gr.getQuantity());
        g.getEquipmentForms().apply(g);
        check("...and a count that returns takes it back to 1, the posts the buildings'", a.getOperatedShare() == 1 && Arrays.equals(a.postsPerTier(), base(g)));
    }

    /* ============================ 5. a default city ============================ */

    static void five() throws Exception {
        System.out.println("\n--- 5. a city founded today's way: no near-future piece is ever on its farms, so every machine is operated ---");
        Game g = village("fieldops-default", false);
        if (g.getMonth() < fe8()) ResearchCheck.atMonth(g, fe8());
        BuildingsStacks gr = grain(g);
        check("fixture: founded the default way, past FE8's month, with farms standing all fitted", !g.foundedUnfitted() && g.getMonth() >= fe8()
                && gr.getQuantity() >= 2 && !gr.hasUnfitted(), g.getMonth() + " " + fe8());
        g.getEquipmentForms().apply(g);
        check("the forms applied, the operated share is exactly 1 and the farms offer the buildings' posts, to the bit",
                farms(g).getOperatedShare() == 1 && Arrays.equals(farms(g).postsPerTier(), base(g)) && farms(g).postsPastBuildings() == null);
        // The guard itself: a count of the kit put on the farms by hand (which a default city never has) moves nothing.
        gr.setUnfitted(KIT, gr.getQuantity());
        mixed(g).setUnfitted(KIT, mixed(g).getQuantity());
        g.getEquipmentForms().apply(g);
        check("a count of the kit put on every farm by hand does not move it: the share is 1 still", farms(g).getOperatedShare() == 1
                && Arrays.equals(farms(g).postsPerTier(), base(g)));
        gr.setUnfitted(KIT, 0);
        mixed(g).setUnfitted(KIT, 0);
        play(g, 2);
        check("two months played, still 1", farms(g).getOperatedShare() == 1 && farms(g).autonomousMachines() == 0);
    }

    /* ============================ 6. a played count ============================ */

    static void six() throws Exception {
        System.out.println("\n--- 6. a played count: the 1900 village played 24 months from FE8's month ---");
        Game g = village("fieldops-played", true);
        Agriculture a = farms(g);
        // The village has no builders' crews, so a fitting queue with a piece's points in it never reaches the kit's, which has none: every piece but the kit is
        // put fitted by hand, so that the one fitting the round can order is the kit's, and it is worked in the month it is ordered.
        int left = 0;
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            for (Equipment piece : s.getBuilding().getEquipment()) {
                if (piece.getId().equals(KIT)) continue;
                s.setUnfitted(piece.getId(), 0);
            }
            left += s.getUnfittedView().size();
        }
        ResearchCheck.atMonth(g, fe8());
        quietly(() -> { g.getRecipes().choose(g); g.getRecipes().apply(g); g.getEquipmentForms().apply(g); });
        check("fixture: the kit on the two farms is the only piece left unfitted in the city, and every machine is operated", left == 2
                && grain(g).getUnfitted(KIT) == grain(g).getQuantity() && a.getOperatedShare() == 1 && g.getFittings().isEmpty(), left + " stacks with a count");
        // REV-R6d P2: a played count that accepted a kit that did not pay would leave both green with no kit fitted (a life of one month, say). The fixture
        // is built to pay, and that is asserted before a month is played; then the city is required to fit it and the fitting to show in the posts and the payroll.
        // One month is played first, so that the labour market has struck the wages the kit saves (a village is born with none), as section 3 does.
        play(g, 1);
        Equipment grainKit = grain(g).getBuilding().getEquipment(KIT), mixedKit = mixed(g).getBuilding().getEquipment(KIT);
        EquipmentFit.Verdict grainVerdict = EquipmentFit.verdict(g, grain(g), grainKit), mixedVerdict = EquipmentFit.verdict(g, mixed(g), mixedKit);
        check("fixture: the kit PAYS on the Mixed Farm in this village in the month the city first orders a fitting (" + mixedVerdict.kind() + "): asserted, not accepted",
                mixedVerdict.kind() == EquipmentFit.Kind.PAYS && mixedVerdict.gain() > 0 && mixedVerdict.gain() * mixedVerdict.factor() >= mixedVerdict.cost(), "" + mixedVerdict);
        System.out.printf("  the kit on the Grain Farm in the same village and month: %s (gain %.4f a month on a cost of %.4f, earns back %.4f): printed, not asserted, as the village's price level has moved with its clock%n",
                grainVerdict.kind(), grainVerdict.gain(), grainVerdict.cost(), grainVerdict.earnsBack());
        check("...and so did section 3's, in its own village", verdictAtFe8 == EquipmentFit.Kind.PAYS, "" + verdictAtFe8);
        int below = a.getOperatedShare() < 1 ? 1 : 0, months = 24;
        double least = a.getOperatedShare();
        for (int i = 1; i < months; i++) {
            play(g, 1);
            if (a.getOperatedShare() < 1) below++;
            least = Math.min(least, a.getOperatedShare());
        }
        long kits = (mixed(g).getQuantity() - mixed(g).getUnfitted(KIT)) + (grain(g).getQuantity() - grain(g).getUnfitted(KIT));
        System.out.printf("  played %d months from month %d: the operated share was below 1 in %d of them, lowest %.3f, the kit fitted on %d farms (the Grain Farms still lacking it: %d of %d)%n",
                months, fe8(), below, least, kits, grain(g).getUnfitted(KIT), grain(g).getQuantity());
        check("fixture: the clock has moved the whole span", g.getMonth() >= fe8() + months, "" + g.getMonth());
        check("the city fitted the kit where it pays: every Mixed Farm has it, and the operated share was below 1 in " + below + " of the " + months + " months",
                mixed(g).getUnfitted(KIT) == 0 && kits >= mixed(g).getQuantity() && below > 0, kits + " farms fitted, " + mixed(g).getUnfitted(KIT) + " Mixed Farms lacking it, " + below + " months below 1");
        check("...the share it fell to is the share of the farms still lacking the kit (" + String.format("%.3f", a.getOperatedShare()) + ")", a.getOperatedShare() == lackingShare(g) && a.getOperatedShare() < 1);
        play(g, 1);
        long[] base = base(g), posts = a.postsPerTier(), offered = a.postsOfferedPerTier();
        int hands = JobType.NO_DIPLOMA.ordinal();
        long struck = Math.round(a.autonomousMachines() * Agriculture.OPERATORS_A_MACHINE);
        check("the operators are struck from the posts the farms offer: " + base[hands] + " -> " + posts[hands] + " (" + struck + " machines work by themselves)",
                struck > 0 && posts[hands] == base[hands] - struck, posts[hands] + " against " + base[hands]);
        check("...and the month's wage pass was struck on them: the posts offered are the posts the farms say, tier by tier", Arrays.equals(offered, posts),
                Arrays.toString(offered) + " against " + Arrays.toString(posts));
        double[] rates = g.getEconomyManager().getWageRates();
        double payroll = a.getPayrollAtFullStaffing(), unstruck = 0, struckPay = 0;
        for (int i = 0; i < base.length; i++) {
            unstruck += rates[i] * base[i];
            struckPay += rates[i] * posts[i];
        }
        near("the farms' payroll at full staffing is the wage rates over the posts they offer", payroll, struckPay, 1e-9 * Math.max(1, struckPay));
        check("...and it is less than the buildings' posts would have cost at the same rates: the operators struck are paid no more, the watch added is paid",
                payroll < unstruck && payroll > 0, payroll + " against " + unstruck);
        near("...by exactly the operators' wages less the watch's", unstruck - payroll,
                rates[hands] * (base[hands] - posts[hands]) - rates[JobType.DIPLOMA.ordinal()] * (posts[JobType.DIPLOMA.ordinal()] - base[JobType.DIPLOMA.ordinal()]), 1e-9 * Math.max(1, unstruck));
    }
}
