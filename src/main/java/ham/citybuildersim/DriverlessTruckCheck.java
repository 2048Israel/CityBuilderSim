package ham.citybuildersim;

import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Haulage;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

/**
 * The driverless truck (R6b, FE1): the Truck Depot's second piece of equipment, the kit that ends the driver's cab, and what a depot fitted with it does
 * to Haulage: a truck that carries more tonnes a month and a depot that keeps oversight posts where it kept drivers.
 *
 * WHY. The kit changes three things at once that a month's figures would hide if one were wrong: how many tonnes a truck carries (so how many trucks the
 * same need asks for), how many driver posts the depots offer (so the labour market and the wage bill), and what the kit is worth to a depot (so whether
 * it is ever fitted and what the patent rent is billed on). And it must change none of them where the piece does not exist: a city founded today's way has
 * every depot standing "fitted" only because every count there is, so its month is today's, to the bit. Each figure is therefore held to the model's own
 * constants (TONNES_A_TRUCK_MONTH, SHIFT_HOURS, DRIVERLESS_HOURS, DRIVERS_A_TRUCK, OVERSIGHT_A_TRUCK) and getters, never a number typed here, and each
 * condition is caused from both sides: the month before and the month of FE1's date, a depot lacking the kit and a depot with it, a city founded the 1900 way
 * and one founded today's.
 *
 * What this has to prove:
 *
 *   1. THE DIALS: a truck carries TONNES_A_TRUCK_MONTH itself, to the bit, while every truck is driven and TONNES_A_TRUCK_MONTH x DRIVERLESS_HOURS /
 *      SHIFT_HOURS with none, between them in a straight line; a truck keeps DRIVERS_A_TRUCK driver posts driven and OVERSIGHT_A_TRUCK driverless; the
 *      share is clamped to [0, 1] and one that is not a number reads 1
 *   2. BEFORE FE1: in a city founded the 1900 way with depots, the month before FE1's date, the kit is NOT_KNOWN on the depot and the driven share is 1
 *   3. AT FE1: the kit is known; its gross gain is driverGain() at the city's own room, wage and truck capital, positive, and the verdict is the one
 *      EquipmentFit's rule gives (PAYS or NOT_YET) with the patent rent on that gain taken off; a city whose research FOUND FE1 earlier knows the kit from
 *      the month it found it, and owes no rent on it from then
 *   4. EVERY DEPOT FITTED: the driven share is 0, a truck carries the driverless figure, the same need asks for fewer trucks by that factor, and the depots
 *      offer fewer DIPLOMA posts by exactly the driverless trucks times (DRIVERS_A_TRUCK - OVERSIGHT_A_TRUCK)
 *   5. HALF THE DEPOTS FITTED: the share is the room-weighted share of the depots lacking the kit, and it stands after a month is played
 *   6. A DEFAULT CITY: founded today's way, with depots, past FE1's month, the share is exactly 1 and a truck carries TONNES_A_TRUCK_MONTH, even where a
 *      count of the kit is put on its depot by hand
 *   7. A PLAYED COUNT: the 1900 village played 24 months from FE1's month, every other piece in it put fitted (a village has no builders' crews to work a
 *      queue with six points in it), the months its driven share was below 1, which is more than none if the
 *      verdict of section 3 was PAYS
 *
 * Every fixture is FittingCheck.village() (placed without construction, in a GameFiles.scratch() directory: no save slot is written), said over by
 * unfitTheFounding() in a city founded the 1900 way, with its month set by reflection where a date is the premise (ResearchCheck.atMonth()).
 */
public class DriverlessTruckCheck {

    static int fails;
    static int passes;

    static PrintStream quiet;

    static final String TRUCK = EquipmentCheck.TRUCK, KIT = "driverless-kit";

    static void check(String label, boolean ok) {
        System.out.println((ok ? "  PASS " : "  FAIL ") + label);
        if (ok) passes++; else fails++;
    }

    /** The same, with what was found printed under a failure. */
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

    /** One press of the clock, quietly (the player's button: the auto-builder's pass first). */
    static void play(Game g, int months) {
        quietly(() -> { for (int i = 0; i < months; i++) g.toggleNextMonth(); });
    }

    /** FE1's month in history: the model's own, never a literal. */
    static int fe1() {
        return ResearchCatalog.get().entry("FE1").worldMonth();
    }

    static Haulage haulage(Game g) {
        return g.getSectors().freight().haulage();
    }

    static BuildingsStacks depots(Game g) {
        return EquipmentCheck.stack(g, "Truck Depot");
    }

    static Equipment kit(Game g) {
        return depots(g).getBuilding().getEquipment(KIT);
    }

    /** A village with two Truck Depots, founded the 1900 way (stood up as that founding leaves it: every building without every piece) or today's. */
    static Game village(String label, boolean founded1900) {
        ensureQuiet();
        Game g = FittingCheck.village(label, Founding.defaults().withUnfitted(founded1900), false, true);
        if (founded1900) quietly(g::unfitTheFounding);
        return g;
    }

    /** The share of the depots' truck room lacking the kit, written out from the stacks' own counts and the templates' rooms. */
    static double lackingShare(Game g) {
        double total = 0, off = 0;
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            double room = s.getBuilding().getTruckRoom();
            if (room <= 0) continue;
            total += s.getQuantity() * room;
            off += Math.min(s.getUnfitted(KIT), s.getQuantity()) * room;
        }
        return total > 0 ? off / total : 1;
    }

    /** What a DIPLOMA driver post costs a month, as the economy pays it. */
    static double driverWage(Game g) {
        return g.getEconomyManager().getWageRates()[JobType.DIPLOMA.ordinal()];
    }

    /** What a truck's capital costs a month: its wear and its return on what it cost, at the market's price. */
    static double truckCapitalMonth(Game g) {
        double p = g.getMarkets().get(Good.TRUCKS).landedPrice();
        if (!Double.isFinite(p)) p = Good.TRUCKS.worldImportPrice();
        return p * (1 / Sector.VAN_LIFE_MONTHS + Freight.TARGET_RETURN);
    }

    public static void main(String[] args) throws Exception {
        ensureQuiet();
        Game g = one();
        two(g);
        three(g);
        four();
        five();
        six();
        seven();
        System.out.println(fails == 0 ? "DriverlessTruckCheck: all " + passes + " ok" : "DriverlessTruckCheck: " + fails + " FAILED of " + (passes + fails));
        System.exit(fails);
    }

    /* ============================ 1. the dials ============================ */

    static Game one() {
        System.out.println("\n--- 1. the dials: the tonnes a truck carries and the driver posts it keeps, from the share still driven ---");
        Game g = village("driverless-dials", true);
        Haulage h = haulage(g);
        double f = Haulage.DRIVERLESS_HOURS / Haulage.SHIFT_HOURS;
        check("fixture: a truck works longer driverless than driven, and the oversight is less than the drivers it replaces",
                Haulage.DRIVERLESS_HOURS > Haulage.SHIFT_HOURS && Haulage.OVERSIGHT_A_TRUCK < Haulage.DRIVERS_A_TRUCK && Haulage.OVERSIGHT_A_TRUCK >= 0);
        h.setDriverShare(1);
        check("at a share of 1 a truck carries TONNES_A_TRUCK_MONTH itself, to the bit", h.tonnesATruck() == Haulage.TONNES_A_TRUCK_MONTH, "" + h.tonnesATruck());
        check("...and keeps DRIVERS_A_TRUCK posts, to the bit", h.driverPostsATruck() == Haulage.DRIVERS_A_TRUCK, "" + h.driverPostsATruck());
        h.setDriverShare(0);
        check("at 0 a truck carries TONNES_A_TRUCK_MONTH x DRIVERLESS_HOURS / SHIFT_HOURS",
                h.tonnesATruck() == Haulage.TONNES_A_TRUCK_MONTH * Haulage.DRIVERLESS_HOURS / Haulage.SHIFT_HOURS, "" + h.tonnesATruck());
        check("...and keeps OVERSIGHT_A_TRUCK posts", h.driverPostsATruck() == Haulage.OVERSIGHT_A_TRUCK, "" + h.driverPostsATruck());
        check("...which is more tonnes a truck and fewer posts than driven", h.tonnesATruck() > Haulage.TONNES_A_TRUCK_MONTH
                && h.driverPostsATruck() < Haulage.DRIVERS_A_TRUCK);
        double ends = Haulage.TONNES_A_TRUCK_MONTH * f;
        h.setDriverShare(0.5);
        near("at a half the tonnes a truck carries are the two ends' mean (a straight line)", h.tonnesATruck(), (Haulage.TONNES_A_TRUCK_MONTH + ends) / 2, 1e-9);
        near("...and the posts it keeps", h.driverPostsATruck(), (Haulage.DRIVERS_A_TRUCK + Haulage.OVERSIGHT_A_TRUCK) / 2, 1e-12);
        h.setDriverShare(0.25);
        near("...at a quarter, the tonnes", h.tonnesATruck(), 0.25 * Haulage.TONNES_A_TRUCK_MONTH + 0.75 * ends, 1e-9);
        h.setDriverShare(-3);
        check("a share below 0 is clamped to 0", h.getDriverShare() == 0 && h.tonnesATruck() == ends);
        h.setDriverShare(7);
        check("...above 1 to 1", h.getDriverShare() == 1 && h.tonnesATruck() == Haulage.TONNES_A_TRUCK_MONTH);
        h.setDriverShare(0);
        h.setDriverShare(Double.NaN);
        check("...and one that is not a number reads 1", h.getDriverShare() == 1);
        check("a Haulage nobody set reads 1: the default is every truck driven",
                haulage(village("driverless-dials-default", false)).getDriverShare() == 1);
        return g;
    }

    /* ============================ 2. before FE1 ============================ */

    static void two(Game g) throws Exception {
        System.out.println("\n--- 2. before FE1: the kit is not known, and every truck is driven ---");
        BuildingsStacks depot = depots(g);
        Equipment kit = kit(g);
        check("fixture: the Truck Depot carries the kit, since FE1 and ending CAB_DRIVERS, and lacks it in every depot of the stack",
                kit != null && kit.getEnds() == Equipment.Form.CAB_DRIVERS && kit.getSince().equals("FE1") && depot.getQuantity() >= 2
                        && depot.getUnfitted(KIT) == depot.getQuantity(), kit == null ? "no kit" : kit.getEnds() + " " + kit.getSince());
        check("fixture: FE1 is a patented near-future row, its date after 1900's founding month", ResearchCatalog.get().entry("FE1").fee()
                && fe1() > g.getMonth(), "" + fe1());
        // A month played first, so that the labour market has struck the wages the kit saves (a village is born with none).
        play(g, 1);
        ResearchCheck.atMonth(g, fe1() - 1);
        g.getEquipmentForms().apply(g);
        EquipmentFit.Verdict v = EquipmentFit.verdict(g, depot, kit);
        check("the month before FE1's the kit is NOT_KNOWN on the depot", v.kind() == EquipmentFit.Kind.NOT_KNOWN, v.kind().toString());
        check("...and the driven share is 1, a truck carrying TONNES_A_TRUCK_MONTH",
                haulage(g).getDriverShare() == 1 && haulage(g).tonnesATruck() == Haulage.TONNES_A_TRUCK_MONTH, "" + haulage(g).getDriverShare());
        check("...with every depot lacking it, the share the stacks' counts say is 1 too", lackingShare(g) == 1);
    }

    /* ============================ 3. at FE1 ============================ */

    /** Whether the kit paid at FE1's month in the village of section 3, for section 7's label. */
    static EquipmentFit.Kind verdictAtFe1;

    static void three(Game g) throws Exception {
        System.out.println("\n--- 3. at FE1: the kit is known, what it earns a depot is driverGain(), and a find earlier knows it earlier ---");
        BuildingsStacks depot = depots(g);
        BuildingsTemplate dt = depot.getBuilding();
        Equipment kit = kit(g);
        ResearchCheck.atMonth(g, fe1());
        EquipmentFit.Verdict v = EquipmentFit.verdict(g, depot, kit);
        verdictAtFe1 = v.kind();
        check("in FE1's own month the kit is known: PAYS or NOT_YET, priced", (v.kind() == EquipmentFit.Kind.PAYS || v.kind() == EquipmentFit.Kind.NOT_YET)
                && Double.isFinite(v.gain()) && Double.isFinite(v.cost()) && Double.isFinite(v.realRate()) && Double.isFinite(v.hurdle()), v.toString());
        double room = dt.getTruckRoom();
        double wage = driverWage(g), capital = truckCapitalMonth(g);
        double gross = EquipmentFit.driverGain(room, wage, capital);
        double f = Haulage.DRIVERLESS_HOURS / Haulage.SHIFT_HOURS;
        check("fixture: the depot's room, the wage and a truck's capital are all above nothing", room > 0 && wage > 0 && capital > 0, room + " " + wage + " " + capital);
        near("the model's gross gain on the depot is driverGain() at the city's own room, wage and truck capital", EquipmentFit.grossGainOf(g, dt, kit), gross, 1e-9 * Math.abs(gross));
        near("...which is the drivers' wages less the oversight's, plus the capital of the trucks it needs fewer of (written out)", gross,
                room * Haulage.DRIVERS_A_TRUCK * wage - room / f * Haulage.OVERSIGHT_A_TRUCK * wage + room * (1 - 1 / f) * capital, 1e-9 * Math.abs(gross));
        check("...and positive: the same tonnes by fewer trucks and fewer posts earn something", gross > 0, "" + gross);
        double rent = LicenceFees.onGain(kit.getSince(), g.getMonth(), g.getResearch(), gross);
        check("FE1 is payable at its own month, so the rent is the patent's share of that gain (LicenceFees, unchanged)",
                LicenceFees.payable("FE1", g.getMonth(), g.getResearch()) && rent == LicenceFees.SHARE * gross, rent + " of " + gross);
        near("the verdict's gain is the gross less that rent", v.gain(), gross - rent, 1e-9 * Math.abs(gross));
        boolean rule = v.gain() > 0 && v.gain() * EquipmentFit.annuityFactor(kit.getLifeMonths(), v.realRate()) >= v.cost() && v.gain() >= v.hurdle();
        check("the verdict is PAYS exactly when EquipmentFit's rule holds (the gain's life earns back the cost and clears the hurdle): " + v.kind(),
                (v.kind() == EquipmentFit.Kind.PAYS) == rule, "gain " + v.gain() + " cost " + v.cost() + " hurdle " + v.hurdle());
        System.out.printf("  the kit at month %d: gain %.2f a month on a cost of %.2f, earnsBack %.2f, hurdle %.2f: %s%n", g.getMonth(), v.gain(), v.cost(),
                v.earnsBack(), v.hurdle(), v.kind());

        // A city whose research found FE1 before history's month knows the kit from that month.
        Game early = village("driverless-found", true);
        int found = fe1() - 12;
        ResearchViewCheck.setFinds(early, Map.of("FE1", found));
        BuildingsStacks eDepot = depots(early);
        Equipment eKit = kit(early);
        ResearchCheck.atMonth(early, found - 1);
        check("a city that finds FE1 in month " + found + " has not the kit the month before it", EquipmentFit.verdict(early, eDepot, eKit).kind() == EquipmentFit.Kind.NOT_KNOWN);
        ResearchCheck.atMonth(early, found);
        EquipmentFit.Verdict known = EquipmentFit.verdict(early, eDepot, eKit);
        check("...and has it in the month it found it, twelve months before history's (" + known.kind() + ")",
                known.kind() == EquipmentFit.Kind.PAYS || known.kind() == EquipmentFit.Kind.NOT_YET, known.kind().toString());
        check("...owing no patent rent on what it found itself", !LicenceFees.payable("FE1", early.getMonth(), early.getResearch())
                && LicenceFees.onGain("FE1", early.getMonth(), early.getResearch(), 1000) == 0);
    }

    /* ============================ 4. every depot fitted ============================ */

    static void four() {
        System.out.println("\n--- 4. every depot fitted: no driver in a cab, a truck carries more, the same need asks for fewer, the posts are laid off ---");
        Game g = village("driverless-fitted", true);
        Haulage h = haulage(g);
        BuildingsStacks depot = depots(g);
        // The motor trucks on, so that the trucks carry the legs (a depot lacking them is a stable of horses: no trucks asked), then the months that strike a need and a fleet.
        depot.setUnfitted(TRUCK, 0);
        g.getEquipmentForms().apply(g);
        play(g, 3);
        double room = h.depotRoom();
        check("fixture: the depots keep room, the trucks carry the legs (horse share 0), and the city has a need and a fleet",
                room > 0 && h.getHorseShare() == 0 && h.trucksNeeded() > 0 && h.trucks() > 0, room + " " + h.getHorseShare() + " " + h.trucksNeeded() + " " + h.trucks());
        check("fixture: every depot still lacks the kit, so every truck is driven", depot.getUnfitted(KIT) == depot.getQuantity() && h.getDriverShare() == 1
                && h.tonnesATruck() == Haulage.TONNES_A_TRUCK_MONTH);
        long diploma = g.getBuildingManager().getOwnedJobArrayBySector(g.getSectors().freight().key())[JobType.DIPLOMA.ordinal()];
        h.strikeDrivers();
        double offeredDriven = h.offeredShare(JobType.DIPLOMA);
        double needed = h.trucksNeeded(), asked = h.trucksAsked(), trucks = h.trucks();
        check("fixture: the depots post drivers, and the fleet does not fill them, so some are laid off but not all", diploma > 0 && offeredDriven > 0 && offeredDriven < 1,
                diploma + " posts, " + offeredDriven + " offered");

        // The kit fitted on every depot, the forms said over it, no month between: the same need, the same fleet.
        depot.setUnfitted(KIT, 0);
        g.getEquipmentForms().apply(g);
        double f = Haulage.DRIVERLESS_HOURS / Haulage.SHIFT_HOURS;
        check("every depot fitted, the driven share is 0", h.getDriverShare() == 0, "" + h.getDriverShare());
        check("a truck carries TONNES_A_TRUCK_MONTH x DRIVERLESS_HOURS / SHIFT_HOURS",
                h.tonnesATruck() == Haulage.TONNES_A_TRUCK_MONTH * Haulage.DRIVERLESS_HOURS / Haulage.SHIFT_HOURS, "" + h.tonnesATruck());
        near("the same need asks for 1 / (DRIVERLESS_HOURS / SHIFT_HOURS) of the trucks (trucksNeeded())", h.trucksNeeded(), needed / f, 1e-9 * needed);
        near("...and of the fleet it orders toward (trucksAsked())", h.trucksAsked(), asked / f, 1e-9 * asked);
        check("...fewer trucks than the driven need, the fleet held the same", h.trucksNeeded() < needed && h.trucks() == trucks);
        h.strikeDrivers();
        double offered = h.offeredShare(JobType.DIPLOMA);
        double driverless = Math.min(h.trucks(), room);
        double expected = (offeredDriven * diploma - driverless * (Haulage.DRIVERS_A_TRUCK - Haulage.OVERSIGHT_A_TRUCK)) / diploma;
        check("fixture: the posts laid off are not all of them", offered > 0, "" + offered);
        check("the depots offer fewer DIPLOMA posts: " + offeredDriven + " -> " + offered, offered < offeredDriven);
        near("...by exactly the driverless trucks x (DRIVERS_A_TRUCK - OVERSIGHT_A_TRUCK) posts, over the depots' posts", offered, expected, 1e-12);
        near("the estimate a tonne's city leg is struck on carries both: the driver's month is the posts a truck keeps",
                h.truckRunningEstimate() * h.tonnesATruck() - h.tonnesATruck() * Haulage.DIESEL_LITRES_A_TONNE * dieselPrice(g),
                h.driverWage() * h.driverPostsATruck(), 1e-6 * h.driverWage() * h.driverPostsATruck());

        // REV-R6b-1: the fleet raised past the depots' room, every depot fitted. A truck on the street stands in no depot and under no
        // kit: it is driven, carries TONNES_A_TRUCK_MONTH and keeps DRIVERS_A_TRUCK, and only the trucks the depots keep run driverless.
        Sector fr = g.getSectors().freight();
        double held = fr.getPantry(Good.TRUCKS), street = Math.max(1, Math.ceil(room / 2));
        fr.setPantry(Good.TRUCKS, room + street);
        check("fixture: every depot fitted (share 0), and the fleet held past the depots' room, some trucks on the street",
                h.getDriverShare() == 0 && h.trucks() > room && h.onStreet() == street, h.trucks() + " trucks, " + room + " room, " + h.onStreet() + " on the street");
        near("the fleet carries the depots' trucks at DRIVERLESS_HOURS / SHIFT_HOURS the tonnes, the street's at TONNES_A_TRUCK_MONTH: a street truck is driven",
                h.tonnesATruck() * h.trucks(), room * Haulage.TONNES_A_TRUCK_MONTH * f + street * Haulage.TONNES_A_TRUCK_MONTH, 1e-9 * room * Haulage.TONNES_A_TRUCK_MONTH * f);
        near("...and keeps the depots' trucks' OVERSIGHT_A_TRUCK posts and the street's DRIVERS_A_TRUCK",
                h.driverPostsATruck() * h.trucks(), room * Haulage.OVERSIGHT_A_TRUCK + street * Haulage.DRIVERS_A_TRUCK, 1e-9 * (room + street));
        check("...so a truck of the fleet carries less than a driverless one, more than a driven one", h.tonnesATruck() < Haulage.TONNES_A_TRUCK_MONTH * f
                && h.tonnesATruck() > Haulage.TONNES_A_TRUCK_MONTH, "" + h.tonnesATruck());
        h.strikeDrivers();
        near("the posts struck are the depots' trucks', min(trucks, room): every place filled, none idle, the street's trucks' drivers kept",
                h.offeredShare(JobType.DIPLOMA), (diploma - room * (Haulage.DRIVERS_A_TRUCK - Haulage.OVERSIGHT_A_TRUCK)) / diploma, 1e-12);
        near("the driverless share of the fleet is the depots' room over the fleet (driverlessShare())", h.driverlessShare(), room / (room + street), 1e-12);
        fr.setPantry(Good.TRUCKS, held);
        play(g, 1);
        check("a month played leaves the share at 0 (the counts are the saved truth, the share is derived each month)", h.getDriverShare() == 0 && h.tonnesATruck() > Haulage.TONNES_A_TRUCK_MONTH);
    }

    /** The diesel's price Haulage's running estimate reads: the market's landed, the world's before one. */
    static double dieselPrice(Game g) {
        double d = g.getMarkets().get(Good.DIESEL).landedPrice();
        return Double.isFinite(d) ? Math.max(0, d) : 0;
    }

    /* ============================ 5. half the depots fitted ============================ */

    static void five() {
        System.out.println("\n--- 5. half the depots fitted: the share is the room-weighted share of the depots lacking the kit ---");
        Game g = village("driverless-half", true);
        Haulage h = haulage(g);
        BuildingsStacks depot = depots(g);
        depot.setUnfitted(TRUCK, 0);
        check("fixture: two or more depots stand and one is fitted", depot.getQuantity() >= 2);
        depot.setUnfitted(KIT, depot.getQuantity() - 1);
        g.getEquipmentForms().apply(g);
        double lacking = lackingShare(g);
        check("fixture: the share lacking is between none and all", lacking > 0 && lacking < 1, "" + lacking);
        near("the driven share is the room of the depots lacking the kit over all the depots' room", h.getDriverShare(), lacking, 1e-12);
        double f = Haulage.DRIVERLESS_HOURS / Haulage.SHIFT_HOURS;
        near("...a truck carries the blend of the two at that share", h.tonnesATruck(), Haulage.TONNES_A_TRUCK_MONTH * (lacking + (1 - lacking) * f), 1e-9);
        near("...and keeps the blend of the posts", h.driverPostsATruck(), lacking * Haulage.DRIVERS_A_TRUCK + (1 - lacking) * Haulage.OVERSIGHT_A_TRUCK, 1e-12);
        check("...between the driven figure and the driverless", h.tonnesATruck() > Haulage.TONNES_A_TRUCK_MONTH
                && h.tonnesATruck() < Haulage.TONNES_A_TRUCK_MONTH * f);
        play(g, 1);
        near("a month played leaves it where the counts say", h.getDriverShare(), lackingShare(g), 1e-12);
        depot.setUnfitted(KIT, 0);
        g.getEquipmentForms().apply(g);
        check("the last depot fitted takes it to 0", h.getDriverShare() == 0);
        depot.setUnfitted(KIT, depot.getQuantity());
        g.getEquipmentForms().apply(g);
        check("...and a count that returns takes it back to 1", h.getDriverShare() == 1);
    }

    /* ============================ 6. a default city ============================ */

    static void six() throws Exception {
        System.out.println("\n--- 6. a city founded today's way: no near-future piece is ever on its depots, so every truck is driven ---");
        Game g = village("driverless-default", false);
        if (g.getMonth() < fe1()) ResearchCheck.atMonth(g, fe1());
        BuildingsStacks depot = depots(g);
        check("fixture: founded the default way, past FE1's month, with depots standing all fitted", !g.foundedUnfitted() && g.getMonth() >= fe1()
                && depot.getQuantity() >= 2 && !depot.hasUnfitted(), g.getMonth() + " " + fe1());
        g.getEquipmentForms().apply(g);
        check("the forms applied, the driven share is exactly 1 and a truck carries TONNES_A_TRUCK_MONTH",
                haulage(g).getDriverShare() == 1 && haulage(g).tonnesATruck() == Haulage.TONNES_A_TRUCK_MONTH);
        check("...and the depots' driver posts are DRIVERS_A_TRUCK a truck", haulage(g).driverPostsATruck() == Haulage.DRIVERS_A_TRUCK);
        // The guard itself: a count of the kit put on the depots by hand (which a default city never has) moves nothing.
        depot.setUnfitted(KIT, depot.getQuantity());
        g.getEquipmentForms().apply(g);
        check("a count of the kit put on every depot by hand does not move it: the share is 1 still", haulage(g).getDriverShare() == 1
                && haulage(g).tonnesATruck() == Haulage.TONNES_A_TRUCK_MONTH);
        depot.setUnfitted(KIT, 0);
        play(g, 2);
        check("two months played, still 1", haulage(g).getDriverShare() == 1 && haulage(g).tonnesATruck() == Haulage.TONNES_A_TRUCK_MONTH);
    }

    /* ============================ 7. a played count ============================ */

    static void seven() throws Exception {
        System.out.println("\n--- 7. a played count: the 1900 village played 24 months from FE1's month ---");
        Game g = village("driverless-played", true);
        Haulage h = haulage(g);
        // The village has no builders' crews, so a fitting queue with six points of work in it (an electric drive) never reaches the kit's, which is of none:
        // every piece but the kit is put fitted by hand, so that the one fitting the round can order is the kit's, and it is worked in the month it is ordered.
        int left = 0;
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            for (Equipment piece : s.getBuilding().getEquipment()) {
                if (piece.getId().equals(KIT)) continue;
                s.setUnfitted(piece.getId(), 0);
            }
            left += s.getUnfittedView().size();
        }
        ResearchCheck.atMonth(g, fe1());
        quietly(() -> { g.getRecipes().choose(g); g.getRecipes().apply(g); g.getEquipmentForms().apply(g); });
        check("fixture: the kit on the depots is the only piece left unfitted in the city, and every truck is driven", left == 1
                && depots(g).getUnfitted(KIT) == depots(g).getQuantity() && h.getDriverShare() == 1 && g.getFittings().isEmpty(), left + " pieces with a count");
        int below = 0, months = 24;
        double least = 1;
        for (int i = 0; i < months; i++) {
            play(g, 1);
            if (h.getDriverShare() < 1) below++;
            least = Math.min(least, h.getDriverShare());
        }
        System.out.printf("  played %d months from month %d: the driven share was below 1 in %d of them, lowest %.3f, the kit unfitted on %d of %d depots; section 3's verdict: %s%n",
                months, fe1(), below, least, depots(g).getUnfitted(KIT), depots(g).getQuantity(), verdictAtFe1);
        check("fixture: the clock has moved the whole span", g.getMonth() >= fe1() + months, "" + g.getMonth());
        if (verdictAtFe1 == EquipmentFit.Kind.PAYS) {
            check("section 3's verdict was PAYS: the city fits the kit, so the driven share was below 1 in " + below + " of the " + months + " months", below > 0);
            check("...and the share it fell to is the share of the depots still lacking it (" + String.format("%.3f", h.getDriverShare()) + ")",
                    h.getDriverShare() == lackingShare(g));
        } else {
            System.out.println("  section 3's verdict was " + verdictAtFe1 + ": no assertion on the count (a city fits only what pays)");
        }
    }
}
