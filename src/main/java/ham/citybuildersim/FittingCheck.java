package ham.citybuildersim;

import ham.citybuildersim.sectors.Agriculture;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Set;

/**
 * Fitting the equipment (R1b): the 1900 forms at rest between the presses, a Game's forms cleared when it founds or loads another city, the city
 * founded the 1900 way, what a building that finishes comes with, and the fitting itself - ordered, paid, bought and worked, ranked with the
 * sector's buildings, and the player's Fit.
 *
 * WHAT THIS HAS TO PROVE (R1b, 2026-11-05; docs/queue/R1b-notes.md; research-design 2.2 step 6; R1's stranger's review). R1 made a piece
 * of equipment the count of a stack's buildings still without it and set R0c's 1900 forms from the counts at the top of every month and on a
 * load. This part makes the counts rest honestly, lets a city start with them full, and says what a new building is given. What is checkable:
 *
 *   1. THE SHARES AT REST: a month changes the weights the counts are shares of (bakeries on site finish in it), so the shares the top of the
 *      month set are not the counts' at its end; the city rests, between two presses, in the shares of the counts as the month left them, the
 *      loaded twin of a save written there holds the same shares and drives the same forms before any further press, and one more press on
 *      both, through the player's path with the auto-builder on (whose pass reads the shares first), leaves the treasury, every sector's cash
 *      and the shares equal to the bit
 *   2. CLEARED ON A NEW GAME AND ON A LOAD: a Game with counts drives forms; a save with no counts loaded into it drives the same as the same
 *      save loaded into a fresh Game; clear() forgets what was driven; a new game on the same object drives none and has every share at 0
 *   3. THE 1900 FOUNDING, OFF BY DEFAULT: Founding.defaults() is not unfitted and a default founding unfits nothing; a founding withUnfitted(true)
 *      is, every other choice kept through every withX, saved with the founding (a default city writes no key) and read back; the buildings it
 *      stands up are unfitted for every piece their templates carry (the tractor's too), the forms set from them before the first press, and
 *      the flag and the counts come back after a save and a load. The founding's own buildings (the bank, the builders' yard) carry no piece,
 *      so the harness places a village by hand after newGame(), as the playtest does, and says unfitTheFounding() over it: a village placed
 *      without construction is not hooked, and comes as placed until that is said
 *   4. WHAT A NEW BUILDING COMES WITH: in a city founded the 1900 way, bakeries ordered on site that finish come fitted with the electric
 *      drive when it pays for a new building at the city's coal price (the count unchanged) and without it when it does not (the count up by
 *      what finished), a farm finishing has no tractor before PE14's June 1917 (NOT_KNOWN), and the order costs what the model quotes for it in either city,
 *      the template's whole cost at that city's builders (R1c: the two cities' queues and wages are each its own, so it is no longer the same
 *      figure in both); in a default city every building finishes fitted, at either price
 *   5. THE ORDER RANKED WITH THE BUILDINGS (slice B): at a coal price where the drive pays, the choice is the fitting when the plan builds
 *      nothing, the fitting when it earns more on its cost than the building the plan would put up and the building when it earns less, and
 *      the plan's own Decision, unchanged, when the drive does not pay, when no stack is unfitted, and when the sector has a fitting on site;
 *      and one month of the real round orders the fitting
 *   6. THE MONEY AND THE GOODS: Industry's cash falls by the total (or the shortfall is borrowed), the market's draw is the piece's units,
 *      the builders' fitting revenue is the total, and the total is the draws' cost plus the work at the price a point
 *   7. THE WORK: the sites' output falls by exactly the points the fittings took, after the repairs; a piece whose work is more than a
 *      month's output takes several months and its count falls only in the month its points are done; jobs are worked in the order placed
 *   8. ONE AT A TIME: a sector with a fitting on site orders no second in the round, and rank() leaves out a stack whose unfitted are all on site
 *   9. THE DEPOT'S TRUCKS: a motor-truck fitting on a Truck Depot raises Freight's trucks held by the piece's TRUCKS, the builders' draw of
 *      TRUCKS is the same units
 *  10. THE PLAYER'S FIT: the city pays and puts a "City" job on site even where the verdict is NOT_YET; refused for the tractor, for a stack
 *      with every unfitted building on site, and for a treasury short of the total, with nothing drawn and nothing paid
 *  11. THE SAVE: no job, no key; the jobs saved and loaded are the same, and the twin and the loaded city one month on are equal to the bit
 *  12. A DEFAULT CITY ORDERS NONE: a town founded the default way played 24 months has no job ever on site and no Decision with a piece
 *
 * Every fixture CAUSES its condition: the weights move because bakeries on site finish in the month asked about (the harness asserts that the
 * unfitted share of Industry's draw differs between the month's top and its end); the coal price is moved across the break-even solved from
 * the verdict's own figures (EquipmentCheck 9's way), on the goods' literals so that the month does not hand the world's band back; the held
 * sectors build nothing on their own. Figures are asserted against the stacks' own quantities and counts, the templates' getters and the model's
 * verdicts, never numbers copied from the brief.
 */
public class FittingCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static final String DRIVE = EquipmentCheck.DRIVE, TRUCK = EquipmentCheck.TRUCK, TRACTOR = EquipmentCheck.TRACTOR;

    static void check(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-96s %s%s%n", label, ok ? "OK" : "FAIL", ok || detail == null ? "" : "  " + detail);
    }

    static void check(String label, boolean ok) { check(label, ok, null); }

    /** A figure against the harness's own, to a relative tolerance (and an absolute floor for a figure that is nothing). */
    static void near(String label, double actual, double expected, double rel) {
        boolean ok = Math.abs(actual - expected) <= rel * Math.max(Math.abs(expected), 1e-12) + 1e-12;
        check(label, ok, String.format("%.9g != %.9g", actual, expected));
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** One press of the clock, as the player's button makes it (toggleNextMonth(): the auto-builder's pass first), quietly. */
    static void step(Game g, int months) { quietly(() -> { for (int i = 0; i < months; i++) g.toggleNextMonth(); }); }

    static BuildingsStacks stack(Game g, String template) { return EquipmentCheck.stack(g, template); }

    /** Every sector's steam share, the farms' horse share and the carts', in one line: what a city rests in. Two cities rest alike when their lines are equal. */
    static String shares(Game g) {
        StringBuilder sb = new StringBuilder();
        for (Sector s : g.getSectors().all()) sb.append(s.key()).append('=').append(s.getSteamShare()).append(' ');
        sb.append("farms=").append(g.getSectors().agriculture().getHorseShare());
        sb.append(" carts=").append(g.getSectors().freight().haulage().getHorseShare());
        return sb.toString();
    }

    /** The first sector whose cash differs between two cities to the bit, or null. */
    static String firstCashDifference(Game a, Game b) {
        for (Sector s : a.getSectors().all()) {
            Sector other = b.getSectors().byKey(s.key());
            if (other == null || Double.compare(s.getCash(), other.getCash()) != 0) return s.key() + " " + s.getCash() + " vs " + (other == null ? "none" : other.getCash());
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(OutputStream.nullOutputStream());
        // The towns are Forms1900Check's, and the helpers borrowed from EquipmentCheck narrate through its own streams.
        Forms1900Check.out = out;
        Forms1900Check.quiet = quiet;
        EquipmentCheck.out = out;
        EquipmentCheck.quiet = quiet;

        sectionOne();
        sectionTwo();
        sectionThree();
        sectionFour();
        sectionFive();
        sectionSix();
        sectionSeven();
        sectionEight();
        sectionNine();
        sectionTen();
        sectionEleven();
        sectionTwelve();

        out.println(fails == 0 ? "\nAll FittingCheck assertions passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. the shares at rest ============================ */

    static void sectionOne() {
        out.println("--- 1. the shares at rest: a save written at the month's end and the city that goes on rest in the same shares, and keep together through the next press ---");
        Game a = Forms1900Check.town("fitting-rest");
        // Held from R1b slice B on: Industry may now order the fitting of a drive that pays, in the round at the top of a month, and this section is about
        // the counts as it sets them and the month's own change to their weights; the fitting has sections of its own. Held in the twin too, below.
        holdAll(a);
        String industryKey = a.getSectors().industry().key();
        BuildingManager bm = a.getBuildingManager();
        BuildingsStacks bakery = stack(a, "Bakery"), industrial = stack(a, "Industrial Bakery"), grain = stack(a, "Grain Farm");
        int standing = bakery.getQuantity();
        // One of the Bakeries, the whole Industrial Bakery stack and the whole Grain Farm stack are without their pieces...
        bakery.setUnfitted(DRIVE, 1);
        industrial.setUnfitted(DRIVE, industrial.getQuantity());
        grain.setUnfitted(TRACTOR, grain.getQuantity());
        // ...and two more Bakeries are put on site, with depots to build them: they open fitted (a default city) in a month that moves what the share is a share of.
        Game.BuildResult[] ordered = new Game.BuildResult[1];
        quietly(() -> {
            a.buildStack(bm.getTemplateByName("Construction Depot"), 4, true);
            ordered[0] = a.buildStack(bakery.getBuilding(), 2, false);
        });
        check("fixture: some Bakeries are unfitted and two more are on site", ordered[0] == Game.BuildResult.SUCCESS && bakery.getUnderConstruction() == 2
                && bakery.getUnfitted(DRIVE) == 1 && standing >= 2 && industrial.hasUnfitted() && grain.hasUnfitted(), String.valueOf(ordered[0]));

        double top = 0, end = 0;
        int months = 0;
        while (bakery.getUnderConstruction() > 0 && months < 36) {
            top = EquipmentCheck.steamShare(a, industryKey);
            step(a, 1);
            months++;
            end = EquipmentCheck.steamShare(a, industryKey);
        }
        check("fixture: the on-site Bakeries opened, in the last month played", bakery.getUnderConstruction() == 0 && bakery.getLastFinished() > 0
                && bakery.getQuantity() > standing, "months " + months + ", quantity " + bakery.getQuantity());
        check("fixture: that month moved what Industry's unfitted share is a share of (the share of its draw at the month's top differs from the month's end)",
                Math.abs(end - top) > 1e-6, top + " vs " + end);
        Sector industry = a.getSectors().industry();
        near("the city rests in the share of the counts as the month left them (its end), not in the month's top", industry.getSteamShare(), end, 1e-9);
        check("...which is not the top's", Math.abs(industry.getSteamShare() - top) > 1e-6, industry.getSteamShare() + " vs " + top);

        // The twin: A saved at the month's end and loaded.
        GameFiles.Result wrote = EquipmentCheck.save(a, "fitting rest");
        check("fixture: the unbroken city saved at the month's end", wrote.ok, wrote.message());
        Game b = new Game(a.getGameFiles());
        quietly(() -> b.loadGameSave(1));
        holdAll(b);
        check("...and loaded without a failure, in the same month", b.getLoadFailure() == null && b.getMonth() == a.getMonth(),
                String.valueOf(b.getLoadFailure()) + " / month " + b.getMonth() + " vs " + a.getMonth());
        check("before any further press every sector's steam share and the horse shares are the unbroken city's", shares(a).equals(shares(b)), shares(a) + " | " + shares(b));
        check("...and the forms driven are the same", a.getEquipmentForms().driven().equals(b.getEquipmentForms().driven()) && !a.getEquipmentForms().driven().isEmpty(),
                a.getEquipmentForms().driven() + " vs " + b.getEquipmentForms().driven());

        // The next press, on both, through the player's path, the auto-builder on: its pass is the first thing the press does, and reads the shares.
        quietly(() -> {
            a.getAutoBuilder().setOn(true, a.getDecisions());
            b.getAutoBuilder().setOn(true, b.getDecisions());
        });
        check("fixture: automatic building is on in both", a.getAutoBuilder().isOn() && b.getAutoBuilder().isOn());
        step(a, 1);
        step(b, 1);
        check("one more press on each leaves the treasury equal to the bit", Double.compare(a.getCash(), b.getCash()) == 0, a.getCash() + " vs " + b.getCash());
        String cash = firstCashDifference(a, b);
        check("...and every sector's cash", cash == null, cash);
        check("...and the shares", shares(a).equals(shares(b)), shares(a) + " | " + shares(b));
    }

    /* ============================ 2. cleared on a new game and on a load ============================ */

    static void sectionTwo() {
        out.println("\n--- 2. cleared on a new game and on a load: a Game that drove forms starts the next city as a fresh process does ---");
        Game g = Forms1900Check.town("fitting-cleared");
        String industryKey = g.getSectors().industry().key(), farmKey = g.getSectors().agriculture().key();
        GameFiles.Result wrote = EquipmentCheck.save(g, "fitting no counts");
        check("fixture: a city with no building unfitted saved", wrote.ok && !EquipmentCheck.anyUnfitted(g), wrote.message());

        stack(g, "Bakery").setUnfitted(DRIVE, 1);
        stack(g, "Grain Farm").setUnfitted(TRACTOR, stack(g, "Grain Farm").getQuantity());
        step(g, 1);
        check("a Game with counts has forms driven (the steam of Industry, the farms' horses) and their shares up",
                g.getEquipmentForms().driven().equals(Set.of("STEAM:" + industryKey, "FARM_HORSES:" + farmKey))
                        && g.getSectors().industry().getSteamShare() > 0 && g.getSectors().agriculture().getHorseShare() > 0,
                g.getEquipmentForms().driven().toString());

        // The load: the save with no counts, into the Game that drove forms and into a fresh one.
        quietly(() -> g.loadGameSave(1));
        Game fresh = new Game(g.getGameFiles());
        quietly(() -> fresh.loadGameSave(1));
        check("fixture: the save with no counts loaded into both", g.getLoadFailure() == null && fresh.getLoadFailure() == null
                && !EquipmentCheck.anyUnfitted(g) && !EquipmentCheck.anyUnfitted(fresh), g.getLoadFailure() + " / " + fresh.getLoadFailure());
        check("a save with no counts loaded into the Game that drove forms drives what a fresh Game drives (nothing)",
                g.getEquipmentForms().driven().equals(fresh.getEquipmentForms().driven()) && g.getEquipmentForms().driven().isEmpty(),
                g.getEquipmentForms().driven() + " vs " + fresh.getEquipmentForms().driven());
        check("...with the same shares, all at 0", shares(g).equals(shares(fresh)) && EquipmentCheck.maxSteam(g) == 0
                && g.getSectors().agriculture().getHorseShare() == 0 && g.getSectors().freight().haulage().getHorseShare() == 0, shares(g) + " | " + shares(fresh));

        // clear() on its own, then a new game.
        stack(g, "Bakery").setUnfitted(DRIVE, 1);
        stack(g, "Grain Farm").setUnfitted(TRACTOR, stack(g, "Grain Farm").getQuantity());
        step(g, 1);
        check("fixture: the Game drives forms again", !g.getEquipmentForms().driven().isEmpty() && EquipmentCheck.maxSteam(g) > 0);
        g.getEquipmentForms().clear();
        check("clear() forgets which shares were driven", g.getEquipmentForms().driven().isEmpty(), g.getEquipmentForms().driven().toString());
        step(g, 1);
        check("fixture: the counts drive them again from the next month", !g.getEquipmentForms().driven().isEmpty());
        quietly(g::newGame);
        check("a new game on the same object drives none", g.getEquipmentForms().driven().isEmpty(), g.getEquipmentForms().driven().toString());
        check("...with every steam share and the horse shares at 0", EquipmentCheck.maxSteam(g) == 0 && g.getSectors().agriculture().getHorseShare() == 0
                && g.getSectors().freight().haulage().getHorseShare() == 0 && !EquipmentCheck.anyUnfitted(g), shares(g));
        Game founded = new Game(GameFiles.scratch("fitting-cleared-fresh"));
        quietly(founded::newGame);
        check("...as a fresh Game's new game has", shares(g).equals(shares(founded)) && founded.getEquipmentForms().driven().equals(g.getEquipmentForms().driven()));
    }

    /* ============================ 3. the 1900 founding, off by default ============================ */

    /** A village the way the playtest places one after newGame(): roads, homes, groceries, farms, bakeries, power and water, placed without construction (and, if asked, depots). */
    static Game village(String label, Founding founding, boolean literals, boolean depots) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> {
            g.newGame(founding);
            if (literals) g.playOnLiterals();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("Gravel Road"), 6, true);
            g.buildStack(b.getTemplateByName("House"), 400, true);
            g.buildStack(b.getTemplateByName("Small Grocery Store"), 4, true);
            g.buildStack(b.getTemplateByName("Mixed Farm"), 2, true);
            g.buildStack(b.getTemplateByName("Grain Farm"), 1, true);
            g.buildStack(b.getTemplateByName("Industrial Bakery"), 2, true);
            g.buildStack(b.getTemplateByName("Bakery"), 2, true);
            g.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            if (depots) g.buildStack(b.getTemplateByName("Truck Depot"), 2, true);
            MaterialsCheck.handWorkers(g);
        });
        return g;
    }

    /** Whether every piece of every template on every stack is unfitted for the whole stack, and no stack without a piece has a count; the first fault, or null. How many pieces were looked at goes in counted[0]. */
    static String unfittedWholly(Game g, int[] counted) {
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            if (s.getBuilding().getEquipment().isEmpty()) {
                if (s.hasUnfitted()) return s.getName() + " carries no piece but has " + s.getUnfittedView();
                continue;
            }
            for (Equipment piece : s.getBuilding().getEquipment()) {
                counted[0]++;
                if (s.getUnfitted(piece.getId()) != s.getQuantity()) {
                    return s.getName() + " " + piece.getId() + ": " + s.getUnfitted(piece.getId()) + " of " + s.getQuantity();
                }
            }
        }
        return null;
    }

    static void sectionThree() throws Exception {
        out.println("\n--- 3. the 1900 founding, off by default: the flag, its copies, its save, and the buildings the founding stands up ---");
        Founding def = Founding.defaults();
        check("Founding.defaults() is not founded the 1900 way", !def.isUnfitted());
        boolean noPreset = true;
        for (Founding.Preset p : Founding.Preset.values()) if (p != Founding.Preset.CUSTOM && Founding.named("Danzik", p, def.getMeanInflation()).isUnfitted()) noPreset = false;
        check("...nor is any preset, a custom founding, the legacy one, nor the bare constructors'", noPreset
                && !Founding.custom("Danzik", Founding.MIN_CASH, 0, def.getMeanInflation()).isUnfitted()
                && !Founding.legacy(def.getMeanInflation()).isUnfitted()
                && !new Founding("Danzik", Currency.DANZIK, Founding.MIN_CASH, 0, def.getMeanInflation()).isUnfitted()
                && !new Founding("Danzik", Currency.DANZIK, Founding.MIN_CASH, 0, def.getMeanInflation(), 99).isUnfitted());

        Founding wanted = def.withUnfitted(true);
        check("withUnfitted(true) is founded the 1900 way, and withUnfitted(false) of it is not", wanted.isUnfitted() && !wanted.withUnfitted(false).isUnfitted());
        check("...keeping every other choice: the name, the money, the treasury, the vault, the inflation, the world and the path",
                wanted.getCityName().equals(def.getCityName()) && wanted.getCurrency().equals(def.getCurrency()) && wanted.getCash() == def.getCash()
                        && wanted.getReserveUsd() == def.getReserveUsd() && wanted.getMeanInflation() == def.getMeanInflation()
                        && wanted.getWorldKey().equals(def.getWorldKey()) && wanted.getPath() == def.getPath() && wanted.getPathSeed() == def.getPathSeed());
        World.Key key = World.Key.generated(88);
        check("every withX keeps the flag on (and off): money, world seed, world key, Earth, inflation, path",
                wanted.withCurrency(wanted.getCurrency()).isUnfitted() && wanted.withWorldSeed(77).isUnfitted() && wanted.withWorldKey(key).isUnfitted()
                        && wanted.onEarth(45, -75).isUnfitted() && wanted.withMeanInflation(0.03).isUnfitted() && wanted.withPath(Nations.Path.RANDOM, 5).isUnfitted()
                        && !def.withCurrency(def.getCurrency()).isUnfitted() && !def.withWorldSeed(77).isUnfitted() && !def.withWorldKey(key).isUnfitted()
                        && !def.onEarth(45, -75).isUnfitted() && !def.withMeanInflation(0.03).isUnfitted() && !def.withPath(Nations.Path.RANDOM, 5).isUnfitted());
        DataSave onFile = new DataSave();
        onFile.setFounding(wanted);
        DataSave plain = new DataSave();
        plain.setFounding(def);
        check("the founding's save record reads the flag back, and a default founding's reads false",
                onFile.getFounding(wanted.getMeanInflation()).isUnfitted() && !plain.getFounding(def.getMeanInflation()).isUnfitted()
                        && new DataSave().getFounding(def.getMeanInflation()) != null && !new DataSave().getFounding(def.getMeanInflation()).isUnfitted());
        check("...and the record keeps the other choices the way it did (the path, the world seed)",
                onFile.getFounding(wanted.getMeanInflation()).getPath() == wanted.getPath() && onFile.getFounding(wanted.getMeanInflation()).getWorldSeed() == wanted.getWorldSeed());

        // A default founding unfits nothing.
        Game plainCity = village("fitting-default-founding", def, false, true);
        check("a city founded the default way: not unfitted, no building unfitted, no form driven, every share 0", !plainCity.foundedUnfitted()
                && !EquipmentCheck.anyUnfitted(plainCity) && plainCity.getEquipmentForms().driven().isEmpty() && EquipmentCheck.maxSteam(plainCity) == 0,
                shares(plainCity));
        GameFiles.Result wrotePlain = EquipmentCheck.save(plainCity, "fitting default founding");
        String plainJson = wrotePlain.ok ? Files.readString(plainCity.getGameFiles().saveFile(1)) : "";
        check("...whose save has no foundedUnfitted key, and which loads as not unfitted", wrotePlain.ok && !plainJson.contains("foundedUnfitted"), wrotePlain.message());
        Game plainBack = new Game(plainCity.getGameFiles());
        quietly(() -> plainBack.loadGameSave(1));
        check("...and the loaded one is not founded the 1900 way", plainBack.getLoadFailure() == null && !plainBack.foundedUnfitted(), String.valueOf(plainBack.getLoadFailure()));

        // The 1900 founding.
        Game g = village("fitting-1900-founding", wanted, false, true);
        check("a city founded withUnfitted(true) knows it", g.foundedUnfitted() && g.getFounding().isUnfitted());
        boolean standingCarryNone = true;
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            if (s.getBuilding().getEquipment().isEmpty()) continue;
            // the village, placed after the founding without construction, is fitted as placed: only the founding's own buildings (the bank, the yard) were there to unfit
            standingCarryNone &= !s.hasUnfitted();
        }
        check("the founding's own buildings (the bank, the builders' yard) carry no piece, and a village placed after it without construction comes as placed",
                standingCarryNone && !EquipmentCheck.anyUnfitted(g) && g.getEquipmentForms().driven().isEmpty());
        quietly(g::unfitTheFounding);
        int[] counted = { 0 };
        String fault = unfittedWholly(g, counted);
        check("unfitTheFounding(), over that village: on every stack whose template carries a piece every building is unfitted for every piece",
                fault == null && counted[0] > 0, fault == null ? counted[0] + " pieces looked at" : fault);
        check("...the tractor's too: the Grain Farm's whole stack, which stays on horses (NOT_KNOWN before PE14's June 1917)",
                stack(g, "Grain Farm").getUnfitted(TRACTOR) == stack(g, "Grain Farm").getQuantity()
                        && EquipmentFit.verdict(g, stack(g, "Grain Farm"), stack(g, "Grain Farm").getBuilding().getEquipment(TRACTOR)).kind()
                        == EquipmentFit.Kind.NOT_KNOWN);
        Sector industry = g.getSectors().industry();
        Agriculture farms = g.getSectors().agriculture();
        double steam = EquipmentCheck.steamShare(g, industry.key());
        check("fixture: some of Industry's draw is unfitted (bakeries among its buildings)", steam > 0, String.valueOf(steam));
        near("before the first press Industry's steam share is its unfitted part of the grid draw, worked out here from the stacks", industry.getSteamShare(), steam, 1e-9);
        near("...the farms' horse share the unfitted part of their land", farms.getHorseShare(), EquipmentCheck.farmShare(g), 1e-9);
        near("...and Haulage's the unfitted part of the depots' truck room", g.getSectors().freight().haulage().getHorseShare(), EquipmentCheck.cartShare(g), 1e-9);
        check("...with the forms driven set", g.getEquipmentForms().driven().contains("STEAM:" + industry.key())
                && g.getEquipmentForms().driven().contains("FARM_HORSES:" + farms.key()) && g.getEquipmentForms().driven().contains("CART_HORSES:" + g.getSectors().freight().key()),
                g.getEquipmentForms().driven().toString());
        step(g, 1);
        near("one press later Industry's steam share is still the counts' (the month's end said it again)", industry.getSteamShare(), EquipmentCheck.steamShare(g, industry.key()), 1e-9);

        // The save and the load.
        GameFiles.Result wrote = EquipmentCheck.save(g, "fitting 1900 founding");
        String json = wrote.ok ? Files.readString(g.getGameFiles().saveFile(1)) : "";
        check("the city founded the 1900 way saved, with the flag in the file", wrote.ok && json.contains("foundedUnfitted"), wrote.message());
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(1));
        check("...and loaded: the flag is back and so are the counts", back.getLoadFailure() == null && back.foundedUnfitted()
                && back.getBuildingManager().getUnfittedById().equals(g.getBuildingManager().getUnfittedById()) && EquipmentCheck.anyUnfitted(back),
                String.valueOf(back.getLoadFailure()));
        check("...and the shares the counts set are the saved city's", shares(back).equals(shares(g)), shares(back) + " | " + shares(g));
    }

    /* ============================ 4. what a new building comes with ============================ */

    /**
     * The months a pair of Bakeries is given to finish: 36 until R0d, whose 1900 village on 1901's wages (WageLadder) grows more slowly - 1,242
     * people at month 14 against 1,780 - and so builds more slowly, its second pair still on site at 36. The fixture causes the finish; the
     * premise, what a finished Bakery comes with, is unmoved. A default village stops as soon as its pair is open.
     */
    static final int BAKERY_MONTHS = 60;

    /** Presses the clock until nothing is on site on this stack (at most `most` months); how many buildings opened over them. */
    static int pressUntilOpen(Game g, BuildingsStacks s, int most) {
        int opened = 0;
        for (int i = 0; i < most && s.getUnderConstruction() > 0; i++) {
            step(g, 1);
            opened += s.getLastFinished();
        }
        return opened;
    }

    /**
     * What the last order cost, by city: [the city founded the 1900 way, the default one]; what the model quoted for it just before (Game.quoteBuildOn(),
     * which buildStack() charges); the quote's sticker, the template's whole cost at that city's builders; and the model's own sticker for it,
     * the template's cost at the city's builders' wages times its quantity and the builders' multiple on the queue the city stands with.
     */
    static final double[] ORDER_COST = { Double.NaN, Double.NaN }, ORDER_QUOTED = { Double.NaN, Double.NaN },
            ORDER_STICKER = { Double.NaN, Double.NaN }, ORDER_STICKER_MODEL = { Double.NaN, Double.NaN }, ORDER_CASH_COST = { Double.NaN, Double.NaN };

    /** A village on the goods' literals (the moved prices hold through the months), the builders' depots standing and the sectors that would build on their own held. */
    static Game builtVillage(String label, Founding founding) {
        Game g = village(label, founding, true, false);
        quietly(() -> {
            g.getBusinessInvestment().holdSector(Sectors.INDUSTRY);
            g.getBusinessInvestment().holdSector(Sectors.AGRICULTURE);
            g.buildStack(g.getBuildingManager().getTemplateByName("Construction Depot"), 4, true);
            g.simulateMonths(Forms1900Check.SETTLE);
        });
        return g;
    }

    static void newBuildings(String label, boolean unfittedCity, int which) {
        Game g = builtVillage(label, unfittedCity ? Founding.defaults().withUnfitted(true) : Founding.defaults());
        String what = unfittedCity ? "a city founded the 1900 way" : "a default city";
        check("fixture: " + what + " is " + (unfittedCity ? "" : "not ") + "founded the 1900 way, with nothing unfitted and Industry and Agriculture held",
                g.foundedUnfitted() == unfittedCity && !EquipmentCheck.anyUnfitted(g)
                        && g.getBusinessInvestment().isHeld(Sectors.INDUSTRY) && g.getBusinessInvestment().isHeld(Sectors.AGRICULTURE) && g.isOnLiterals());
        BuildingsStacks bakery = stack(g, "Bakery");
        BuildingsTemplate t = bakery.getBuilding();
        Equipment drive = t.getEquipment(DRIVE);
        EquipmentFit.Verdict v = EquipmentFit.verdictForNew(g, t, drive);
        check("a new building's verdict for the drive is priced in 1900 (PAYS or NOT_YET) and is never FITTED", drive.knownAt(g.getMonth())
                && (v.kind() == EquipmentFit.Kind.PAYS || v.kind() == EquipmentFit.Kind.NOT_YET), v.toString());
        double even = EquipmentCheck.breakEvenCoal(g, t, v);
        check("fixture: the break-even price of coal for the drive on a new building is a price", even > 0 && Double.isFinite(even), "is " + even);

        // At a coal price that makes the drive pay for a new building: the finished ones come fitted.
        boolean movedUp = EquipmentCheck.priceCoalAt(g, even * 3);
        check("fixture: the city's coal price moved to three times the break-even, where the drive pays for a new building",
                movedUp && EquipmentFit.verdictForNew(g, t, drive).kind() == EquipmentFit.Kind.PAYS, EquipmentFit.verdictForNew(g, t, drive).toString());
        int before = bakery.getUnfitted(DRIVE);
        double cash = g.getCash();
        Game.BuildResult[] ordered = new Game.BuildResult[1];
        Game.BuildQuote quoted = g.quoteBuildOn(t, 2, Landmasses.CHOSEN);
        ORDER_QUOTED[which] = quoted.total;
        ORDER_STICKER[which] = quoted.sticker;
        ORDER_STICKER_MODEL[which] = g.getBuildingManager().nonMaterialCost(t) * 2 * g.getBuildingManager().orderPriceMultiplier(0, t.getConstructionPoints(), 2);
        ORDER_CASH_COST[which] = t.getCashCost();
        quietly(() -> ordered[0] = g.buildStack(t, 2, false));
        ORDER_COST[which] = cash - g.getCash();
        check("fixture: two Bakeries are on site", ordered[0] == Game.BuildResult.SUCCESS && bakery.getUnderConstruction() == 2, String.valueOf(ordered[0]));
        int opened = pressUntilOpen(g, bakery, BAKERY_MONTHS);
        check("at that price the Bakeries that finished came fitted: the count of the drive unfitted is as it was", opened == 2 && bakery.getUnfitted(DRIVE) == before,
                "opened " + opened + ", count " + before + " -> " + bakery.getUnfitted(DRIVE));
        if (unfittedCity) {
            check("...and nothing was unfitted by it: Industry's steam share is 0", !EquipmentCheck.anyUnfitted(g) && g.getSectors().industry().getSteamShare() == 0);
        }

        // Below it: the finished ones come unfitted in a city founded the 1900 way, fitted in a default city.
        EquipmentFit.Verdict v2 = EquipmentFit.verdictForNew(g, t, drive);
        double even2 = EquipmentCheck.breakEvenCoal(g, t, v2);
        boolean movedDown = EquipmentCheck.priceCoalAt(g, even2 * 0.3);
        check("fixture: the coal price moved to three tenths of the break-even, where the drive does not pay for a new building",
                movedDown && EquipmentFit.verdictForNew(g, t, drive).kind() == EquipmentFit.Kind.NOT_YET, EquipmentFit.verdictForNew(g, t, drive).toString());
        before = bakery.getUnfitted(DRIVE);
        final int heldBefore = before;
        quietly(() -> ordered[0] = g.buildStack(t, 2, false));
        check("fixture: two more Bakeries are on site", ordered[0] == Game.BuildResult.SUCCESS && bakery.getUnderConstruction() == 2, String.valueOf(ordered[0]));
        opened = pressUntilOpen(g, bakery, BAKERY_MONTHS);
        if (unfittedCity) {
            check("below it the Bakeries that finished came unfitted: the count of the drive unfitted is up by what finished", opened == 2 && bakery.getUnfitted(DRIVE) == heldBefore + opened,
                    "opened " + opened + ", count " + heldBefore + " -> " + bakery.getUnfitted(DRIVE));
            near("...and the forms at rest are the counts': Industry's steam share is the unfitted part of its draw", g.getSectors().industry().getSteamShare(),
                    EquipmentCheck.steamShare(g, g.getSectors().industry().key()), 1e-9);
            check("...and it is up (the share is more than nothing)", g.getSectors().industry().getSteamShare() > 0);
        } else {
            check("in a default city the Bakeries that finished came fitted at that price too: no count, no share", opened == 2 && bakery.getUnfitted(DRIVE) == 0
                    && !EquipmentCheck.anyUnfitted(g) && g.getSectors().industry().getSteamShare() == 0 && g.getEquipmentForms().driven().isEmpty(),
                    "opened " + opened + ", count " + bakery.getUnfitted(DRIVE));
        }

        // A farm finishing: the tractor is not known before PE14's month (June 1917), whatever the prices.
        BuildingsStacks grain = stack(g, "Grain Farm");
        Equipment tractor = grain.getBuilding().getEquipment(TRACTOR);
        final int tractorBefore = grain.getUnfitted(TRACTOR);
        quietly(() -> ordered[0] = g.buildStack(grain.getBuilding(), 1, false));
        check("fixture: a Grain Farm is on site", ordered[0] == Game.BuildResult.SUCCESS && grain.getUnderConstruction() == 1, String.valueOf(ordered[0]));
        int farmsOpened = pressUntilOpen(g, grain, 36);
        check("the verdict for a new farm's tractor is NOT_KNOWN before PE14's June 1917, whatever the prices", EquipmentFit.verdictForNew(g, grain.getBuilding(), tractor).kind() == EquipmentFit.Kind.NOT_KNOWN);
        if (unfittedCity) {
            check("a farm that finished came without its tractor: the count of the tractor unfitted is up by what finished", farmsOpened == 1
                    && grain.getUnfitted(TRACTOR) == tractorBefore + farmsOpened, "opened " + farmsOpened + ", count " + tractorBefore + " -> " + grain.getUnfitted(TRACTOR));
            near("...and the farms' horse share is the unfitted part of their land", g.getSectors().agriculture().getHorseShare(), EquipmentCheck.farmShare(g), 1e-9);
            check("...which is more than nothing", g.getSectors().agriculture().getHorseShare() > 0);
        } else {
            check("in a default city a farm that finished came fitted: no count of the tractor", farmsOpened == 1 && grain.getUnfitted(TRACTOR) == 0
                    && g.getSectors().agriculture().getHorseShare() == 0);
        }
    }

    static void sectionFour() {
        out.println("\n--- 4. what a new building comes with: the pieces that pay, at the finish; none of the others; a default city's buildings all fitted ---");
        newBuildings("fitting-new-1900", true, 0);
        newBuildings("fitting-new-default", false, 1);
        // The two cities are not one city at the order: what the builders' queue holds and the wages stand at are each city's own (R1c: the 1900 city's
        // un-held sectors order what its recipes make pay, a Pulp Mill among them, and the default city's farms buy fuel), and the quote reads both. So the
        // order is compared with what the MODEL charges for it in that city, and across the cities on what the order itself is, the template.
        double tol = 1e-9 * Math.max(ORDER_QUOTED[0], ORDER_QUOTED[1]);
        check("the order costs what it costs: in each city the two Bakeries are charged exactly what the model quoted for them just before, in a city founded the 1900 way as in a default one",
                Math.abs(ORDER_COST[0] - ORDER_QUOTED[0]) <= tol && Math.abs(ORDER_COST[1] - ORDER_QUOTED[1]) <= tol && ORDER_COST[0] > 0 && ORDER_COST[1] > 0,
                ORDER_COST[0] + " vs " + ORDER_QUOTED[0] + " / " + ORDER_COST[1] + " vs " + ORDER_QUOTED[1]);
        check("...the template's whole cost at that city's builders: the sticker is the cash cost at the city's wages, times two, times the builders' multiple on the city's own queue, with nothing for a piece",
                Math.abs(ORDER_STICKER[0] - ORDER_STICKER_MODEL[0]) <= 1e-9 * ORDER_STICKER[0] && Math.abs(ORDER_STICKER[1] - ORDER_STICKER_MODEL[1]) <= 1e-9 * ORDER_STICKER[1]
                        && ORDER_STICKER[0] > 0 && ORDER_STICKER[1] > 0, ORDER_STICKER[0] + " vs " + ORDER_STICKER_MODEL[0] + " / " + ORDER_STICKER[1] + " vs " + ORDER_STICKER_MODEL[1]);
        check("...and the template is the same in both: the Bakery's cash cost (the order's own part) is the same figure in the city founded the 1900 way and in the default one",
                Double.compare(ORDER_CASH_COST[0], ORDER_CASH_COST[1]) == 0 && ORDER_CASH_COST[0] > 0, ORDER_CASH_COST[0] + " vs " + ORDER_CASH_COST[1]);
    }

    /* ============================ the fixtures of 5 to 12 ============================ */

    /**
     * A default-founded village on the goods' literals (a moved price holds through the months) with the builders' depots standing, at least `unfitted`
     * Bakeries standing and that many lacking the electric drive, and a month played so that the shares and the verdicts are at rest. Industry is not
     * held: it may order, as it does in a played city.
     */
    static Game fitTown(String label, int unfitted) {
        Game g = village(label, Founding.defaults(), true, false);
        quietly(() -> {
            g.buildStack(g.getBuildingManager().getTemplateByName("Construction Depot"), 4, true);
            g.simulateMonths(Forms1900Check.SETTLE);
        });
        BuildingsStacks bakery = stack(g, "Bakery");
        if (bakery.getQuantity() < unfitted) quietly(() -> g.buildStack(bakery.getBuilding(), unfitted - bakery.getQuantity(), true));
        bakery.setUnfitted(DRIVE, unfitted);
        step(g, 1);
        return g;
    }

    /**
     * Moves bread's world price (EquipmentCheck.priceAt's way) to where the investors' estimate of a Bakery over the cost of one is `target`: the score rises
     * with bread's price, found by bisection between the price as it stands and eight times it. False if a price would not move or the target is out of reach.
     */
    static boolean breadForBakeryScore(Game g, BusinessInvestment plans, String key, BuildingsTemplate t, double target) {
        double bread0 = SpreadPlanner.cityValue(g.getMarkets().get(Good.BREAD));
        double lo = 1, hi = 8;
        for (int i = 0; i < 60; i++) {
            double mid = (lo + hi) / 2;
            if (!EquipmentCheck.priceAt(g, Good.BREAD, bread0 * mid)) return false;
            if (plans.estimatedMonthlyProfit(key, t) / plans.getCostOf(t, 1) < target) lo = mid; else hi = mid;
        }
        return EquipmentCheck.priceAt(g, Good.BREAD, bread0 * hi) && hi < 8;
    }

    /** Holds every sector: none asks its plan or fits anything in the round, so that the fixture's only orders are the ones the harness places. */
    static void holdAll(Game g) {
        for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
    }

    /** The coal price at which the drive on this stack's building just pays, from the verdict's own figures (EquipmentCheck 9's way): the stack must have a building unfitted. */
    static double breakEven(Game g, BuildingsStacks bakery) {
        return EquipmentCheck.breakEvenCoal(g, bakery.getBuilding(), EquipmentFit.verdict(g, bakery, bakery.getBuilding().getEquipment(DRIVE)));
    }

    /** The builders' month after the repairs, before the fittings take theirs: what getBuildingOutput() reads with no job on site. */
    static int afterRepairs(Game g) {
        return (int) Math.max(0, Math.round(g.getConstructionOutput() - g.getMaintenancePoints()));
    }

    /** What the quote of a piece's goods at the city's market comes to, plus its work at the builders' price a point: what ordering it should cost. */
    static double expectedCost(Game g, Equipment piece) {
        double total = piece.getWork() * g.getBuildingManager().nonMaterialPricePerPoint();
        for (java.util.Map.Entry<Good, Double> e : piece.getBuys().entrySet()) total += g.getMarkets().quote(e.getKey(), e.getValue(), g.getSectors()).cost();
        return total;
    }

    /** What the market recorded of the builders' draws of a good since `from` trades ago: {units, cost}. */
    static double[] buildersDrew(Game g, Good good, int from) {
        java.util.List<Trade> trades = g.getMarkets().get(good).getTrades();
        String builders = g.getSectors().construction().key();
        double units = 0, cost = 0;
        for (int i = from; i < trades.size(); i++) {
            if (!builders.equals(trades.get(i).buyer())) continue;
            units += trades.get(i).units();
            cost += trades.get(i).value();
        }
        return new double[] { units, cost };
    }

    /* ============================ 5. the order ranked with the buildings ============================ */

    static void sectionFive() {
        out.println("\n--- 5. the order ranked with the buildings: the fitting when the plan builds nothing or earns less on its cost, the plan's own decision otherwise ---");
        Game g = fitTown("fitting-order", 1);
        Sector industry = g.getSectors().industry();
        String key = industry.key();
        BuildingsStacks bakery = stack(g, "Bakery");
        BuildingsTemplate t = bakery.getBuilding();
        Equipment drive = t.getEquipment(DRIVE);
        BusinessInvestment plans = g.getBusinessInvestment();
        double even = breakEven(g, bakery);
        check("fixture: one Bakery lacks its drive, Industry may order, nothing is on site, and the break-even price of coal is a price",
                bakery.getUnfitted(DRIVE) == 1 && !plans.isHeld(key) && g.getFittings().isEmpty() && even > 0 && Double.isFinite(even), "break-even " + even);
        BusinessInvestment.Decision idle = BusinessInvestment.Decision.no(key, "nothing it wants");
        BusinessInvestment.Decision builds = new BusinessInvestment.Decision(key, t, 1, "a Bakery would pay", true);
        check("fixture: neither decision of the plan carries a piece", idle.piece == null && builds.piece == null);

        // The drive does not pay.
        check("fixture: the city's price of coal moves to half the break-even", EquipmentCheck.priceCoalAt(g, even * 0.5));
        EquipmentFit.Verdict poor = EquipmentFit.verdict(g, bakery, drive);
        check("...where the drive does not pay", poor.kind() == EquipmentFit.Kind.NOT_YET, poor.toString());
        check("the plan's own decision comes back, the very object, when the drive does not pay: for a plan that builds nothing and for one that builds",
                EquipmentFit.choose(g, industry, idle) == idle && EquipmentFit.choose(g, industry, builds) == builds);

        // The drive pays, and the plan builds nothing.
        check("fixture: the price of coal moves to three times the break-even", EquipmentCheck.priceCoalAt(g, even * 3));
        EquipmentFit.Verdict rich = EquipmentFit.verdict(g, bakery, drive);
        check("...where the drive pays", rich.kind() == EquipmentFit.Kind.PAYS, rich.toString());
        BusinessInvestment.Decision fitting = EquipmentFit.choose(g, industry, idle);
        check("the plan builds nothing: the month's decision is to fit the drive to a Bakery, one, and it builds (it spends)",
                fitting != idle && fitting.piece == drive && fitting.template == t && fitting.build && fitting.quantity == 1 && fitting.sector.equals(key)
                        && !fitting.landBlocked && fitting.heldForStaff == null, String.valueOf(fitting.piece));
        check("...its reason in the planner's words: the piece, the building, what it earns a month on what it costs at the city's prices, and the rate a month",
                fitting.reason.startsWith("the electric motor drive on a Bakery earns")
                        && fitting.reason.contains(String.format("$%,.1fk a month on $%,.1fk at the city's prices", rich.gain(), rich.cost()))
                        && fitting.reason.contains(String.format("%.2f%% a month", rich.earningsOnCost() * 100)), fitting.reason);

        // Ranked with the building, both ways, from the getters the choice reads.
        /*
         * WG2, {next}, 2026-11-10: BREAD'S PRICE IS MOVED HERE TOO, TO WHERE A BAKERY EARNS HALF WHAT THE DRIVE DOES. WG2's pulls (Migration.jobsPull(),
         * homesPull()) put this village on another path, and the investors' estimate of a Bakery over the cost of one stood at 0.00154 a month on main
         * (the GW1 note below) and stands at -0.0020577 now, under nothing, while the drive at three times the break-even earns 0.0186495: the fixture
         * below needs a Bakery that earns something, and less than the fitting. Bread's price is moved (the way GW1's is below, world prices) to where
         * the Bakery's score is half the drive's at three times; the move below then goes on from that price to its own target, which is dearer.
         */
        boolean breadFirst = breadForBakeryScore(g, plans, key, t, rich.earningsOnCost() / 2);
        check("fixture: bread's price moves until a Bakery earns on its cost half of what the drive earns at three times the break-even",
                breadFirst && Math.abs(plans.estimatedMonthlyProfit(key, t) / plans.getCostOf(t, 1) - rich.earningsOnCost() / 2) <= 1e-6 * rich.earningsOnCost(),
                plans.estimatedMonthlyProfit(key, t) / plans.getCostOf(t, 1) + " vs " + rich.earningsOnCost());
        double buildScore = plans.estimatedMonthlyProfit(key, t) / plans.getCostOf(t, 1);
        check("fixture: at three times the break-even the fitting earns more on its cost than a Bakery would (the investors' estimate over its cost of one)",
                buildScore > 0 && rich.earningsOnCost() > buildScore, rich.earningsOnCost() + " vs " + buildScore);
        BusinessInvestment.Decision wins = EquipmentFit.choose(g, industry, builds);
        check("the plan builds a Bakery and the fitting earns more on its cost: the month's decision is the fitting, and not the plan's", wins != builds && wins.piece == drive && wins.template == t);
        check("fixture: the price of coal moves to one and a half times the break-even", EquipmentCheck.priceCoalAt(g, even * 1.5));
        EquipmentFit.Verdict middling = EquipmentFit.verdict(g, bakery, drive);
        /*
         * ...WITH BREAD AT THE PRICE THAT PUTS A BAKERY BETWEEN THE TWO (GW1, 2026-11-10). A drive that pays earns on its cost at least the annuity's
         * and the hurdle's share of it (0.0058 a month here), and the claim below needs a Bakery that earns more than that. The staffing gate counts the
         * workers a building's own posts would draw (Sector.GATE_DRAW_MONTHS), so this village's businesses build earlier and its wages stand higher
         * at the order, and a Bakery earns 0.00154 a month on its cost (the investors' estimate over the cost of one) - under the drive at any price
         * at which it pays (0.00904 at one and a half times the break-even, 0.01881 at three times): the Bakery could not earn more than a fitting
         * that pays. Bread's price is moved (world prices, the way the coal's is) to where a Bakery earns the mean of those two, so the drive at
         * three times earns more than a Bakery, as the fixture above read, and at one and a half times earns less, as the claim below needs.
         */
        double wanted = (middling.earningsOnCost() + rich.earningsOnCost()) / 2;
        boolean breadMoved = breadForBakeryScore(g, plans, key, t, wanted);
        double buildScore2 = plans.estimatedMonthlyProfit(key, t) / plans.getCostOf(t, 1);
        check("fixture: bread's price moves until a Bakery earns on its cost the mean of what the drive earns at one and a half times and at three times the break-even",
                breadMoved && Math.abs(buildScore2 - wanted) <= 1e-6 * wanted && middling.earningsOnCost() < buildScore2 && buildScore2 < rich.earningsOnCost(),
                buildScore2 + " vs " + middling.earningsOnCost() + " / " + rich.earningsOnCost());
        check("...where the drive still pays but earns less on its cost than a Bakery would", middling.kind() == EquipmentFit.Kind.PAYS && middling.earningsOnCost() < buildScore2,
                middling.earningsOnCost() + " vs " + buildScore2);
        check("the plan builds a Bakery and the building earns more on its cost: the plan's own decision comes back, the very object", EquipmentFit.choose(g, industry, builds) == builds);
        check("...but the plan that builds nothing still gets the fitting that pays, however little", EquipmentFit.choose(g, industry, idle).piece == drive);
        SpreadPlanner.Candidate b1 = new SpreadPlanner.Candidate(t, 2, 100, null, null, java.util.Map.of(), java.util.List.of());
        SpreadPlanner.Candidate b2 = new SpreadPlanner.Candidate(t, 4, 200, null, null, java.util.Map.of(), java.util.List.of());
        check("a tie keeps the one listed first (the building is listed first): SpreadPlanner.best() on equal earnings on cost", SpreadPlanner.best(java.util.List.of(b1, b2)) == b1);

        // Nothing to fit, a fitting already on site, another sector.
        EquipmentCheck.priceCoalAt(g, even * 3);
        g.getFittings().add(t.getId(), DRIVE, key, 1e6);
        check("a sector with a fitting on site of its own gets the plan's decision, the very object, though the drive pays",
                EquipmentFit.choose(g, industry, idle) == idle && EquipmentFit.choose(g, industry, builds) == builds);
        g.getFittings().clear();
        check("...with none on site the drive is the choice again", EquipmentFit.choose(g, industry, idle).piece == drive);
        g.getFittings().add(t.getId(), DRIVE, "City", 1e6);
        check("the city's own job takes the only Bakery that lacks the drive: it is not Industry's fitting, but there is none left to fit, and the plan's decision comes back",
                !g.getFittings().hasOnSite(key) && EquipmentFit.choose(g, industry, idle) == idle);
        g.getFittings().clear();
        bakery.setUnfitted(DRIVE, 0);
        check("no stack unfitted: the plan's own decision comes back", EquipmentFit.choose(g, industry, idle) == idle && EquipmentFit.choose(g, industry, builds) == builds);
        Sector farms = g.getSectors().agriculture();
        BusinessInvestment.Decision farmIdle = BusinessInvestment.Decision.no(farms.key(), "nothing it wants");
        stack(g, "Grain Farm").setUnfitted(TRACTOR, 1);
        check("a sector whose only unfitted piece is not known (Agriculture's tractor) gets its plan's decision", EquipmentFit.choose(g, farms, farmIdle) == farmIdle);
        stack(g, "Grain Farm").setUnfitted(TRACTOR, 0);
        bakery.setUnfitted(DRIVE, 1);

        // One month of the real round. The round is the top of the month (Game.startOfMonthUpdate()) and the crews work the job in the same month's tick, so a
        // job as small as a Bakery's drive would be fitted by the time the press is over: the city's own long job is placed ahead of it, and takes every point.
        bakery.setUnfitted(DRIVE, 2);
        g.getFittings().add(t.getId(), DRIVE, "City", 1e6);
        check("fixture: the price of coal moves to ten times the break-even and the drive pays; two Bakeries lack it, one has a long job of the city's ahead",
                EquipmentCheck.priceCoalAt(g, even * 10) && EquipmentFit.verdict(g, bakery, drive).kind() == EquipmentFit.Kind.PAYS
                        && EquipmentFit.rank(g, industry).size() == 1 && !g.getFittings().hasOnSite(key));
        step(g, 1);
        String line = g.getLastInvestment(key);
        check("one month of the real round: Industry's line says it Fitted the drive", line.startsWith("Fitted the electric motor drive to a Bakery - "), line);
        java.util.List<Fittings.Job> jobs = g.getFittings().view();
        check("...a job of its own is on site behind the city's, the drive on the Bakery, with all its work still to do (the crews' points went to the job ahead), and the count has not moved",
                jobs.size() == 2 && jobs.get(0).payer.equals("City") && jobs.get(1).templateId == t.getId() && jobs.get(1).pieceId.equals(DRIVE) && jobs.get(1).payer.equals(key)
                        && jobs.get(1).pointsLeft == drive.getWork() && bakery.getUnfitted(DRIVE) == 2, jobs.toString());
        step(g, 1);
        check("the next round orders no second while that job is on site: still one of Industry's, and not another Fitted line", g.getFittings().view().size() == 2
                && !g.getLastInvestment(key).startsWith("Fitted"), g.getLastInvestment(key) + " / " + g.getFittings().view());
    }

    /* ============================ 6. the money and the goods ============================ */

    static void sectionSix() {
        out.println("\n--- 6. the money and the goods: the goods drawn by the builders at the city's market, the payer pays, the builders are paid, the work at the price a point ---");
        Game g = fitTown("fitting-money", 2);
        holdAll(g);
        Sector industry = g.getSectors().industry();
        String key = industry.key();
        BuildingsStacks bakery = stack(g, "Bakery");
        BuildingsTemplate t = bakery.getBuilding();
        Equipment drive = t.getEquipment(DRIVE);
        ham.citybuildersim.sectors.Construction builders = g.getSectors().construction();
        BusinessDebtManager credit = g.getEconomyManager().getBusinessDebtManager();
        GoodsMarket machinery = g.getMarkets().get(Good.MACHINERY);
        double point = g.getBuildingManager().nonMaterialPricePerPoint();
        double units = drive.getBuys().getOrDefault(Good.MACHINERY, -1.0);
        check("fixture: the drive buys machinery and nothing else, and has work; the builders are paid a point", drive.getBuys().size() == 1 && units > 0 && drive.getWork() > 0 && point > 0,
                drive.getBuys() + " / " + drive.getWork() + " / " + point);
        double expected = expectedCost(g, drive);
        check("fixture: two Bakeries lack the drive, and what it should cost is a price", bakery.getUnfitted(DRIVE) == 2 && expected > 0 && Double.isFinite(expected), "expected " + expected);

        // Cash above the total: it is paid out of the till.
        g.getEconomyManager().setSectorCash(key, expected * 4);
        double cash0 = industry.getCash(), principal0 = credit.getPrincipal(key), drawn0 = machinery.getDrawn(), revenue0 = builders.getFittingThisMonth();
        double fleet0 = g.getSectors().freight().getPantry(Good.TRUCKS);
        int trades0 = machinery.getTrades().size();
        double local0 = machinery.getLocalFilled(), imported0 = machinery.getImported();
        String refused = g.fit(g.getSectorInvestor(key), bakery, drive);
        check("Industry orders the fitting: taken (no reason given)", refused == null, String.valueOf(refused));
        near("with cash above the total its cash falls by the total: the quote's goods and the work at the builders' price a point", cash0 - industry.getCash(), expected, 1e-9);
        check("...and nothing is borrowed", credit.getPrincipal(key) == principal0, credit.getPrincipal(key) + " vs " + principal0);
        near("the market's draw of machinery is the piece's units", machinery.getDrawn() - drawn0, units, 1e-12);
        double[] drew = buildersDrew(g, Good.MACHINERY, trades0);
        near("...the builders are the buyer in the market's own records, for the same units", drew[0], units, 1e-12);
        near("...local fills and imports together are those units (the makers' stock down, or the import up)",
                (machinery.getLocalFilled() - local0) + (machinery.getImported() - imported0), units, 1e-12);
        check("an electric drive delivers nothing into Freight's fleet: only the motor truck does", g.getSectors().freight().getPantry(Good.TRUCKS) == fleet0);
        near("the builders' fitting revenue this month is the total", builders.getFittingThisMonth() - revenue0, expected, 1e-9);
        near("...and the total is the draws' cost in the market's records plus the work at the price a point", builders.getFittingThisMonth() - revenue0, drew[1] + drive.getWork() * point, 1e-9);
        java.util.List<Fittings.Job> jobs = g.getFittings().view();
        check("a job is on site for it: the Bakery, the drive, Industry's, with the piece's work to do, and the count has not moved",
                jobs.size() == 1 && jobs.get(0).templateId == t.getId() && jobs.get(0).pieceId.equals(DRIVE) && jobs.get(0).payer.equals(key)
                        && jobs.get(0).pointsLeft == drive.getWork() && bakery.getUnfitted(DRIVE) == 2 && g.getFittings().onSite(t.getId(), DRIVE) == 1, jobs.toString());

        // Cash below the total: the shortfall is borrowed.
        double expected2 = expectedCost(g, drive);
        g.getEconomyManager().setSectorCash(key, expected2 * 0.1);
        double principal1 = credit.getPrincipal(key), revenue1 = builders.getFittingThisMonth();
        String refused2 = g.fit(g.getSectorInvestor(key), bakery, drive);
        check("with cash under the total the second order is taken too (the second Bakery lacks it)", refused2 == null && g.getFittings().onSite(t.getId(), DRIVE) == 2, String.valueOf(refused2));
        check("...the shortfall borrowed: the sector's debt rose and its till, after paying the whole total, is empty to a rounding", credit.getPrincipal(key) > principal1
                && Math.abs(industry.getCash()) <= 1e-6 * expected2, "principal " + principal1 + " -> " + credit.getPrincipal(key) + ", cash " + industry.getCash());
        near("...and the builders were paid the whole total", builders.getFittingThisMonth() - revenue1, expected2, 1e-9);

        // Refused: nothing drawn, nothing paid.
        double cash3 = industry.getCash(), drawn3 = machinery.getDrawn(), revenue3 = builders.getFittingThisMonth();
        int jobs3 = g.getFittings().view().size();
        String none = g.fit(g.getSectorInvestor(key), bakery, drive);
        check("a third order is refused: no Bakery lacking the drive is left that is not being fitted", none != null && none.contains("already being fitted"), String.valueOf(none));
        check("...with nothing drawn, nothing paid and no job added", industry.getCash() == cash3 && machinery.getDrawn() == drawn3 && builders.getFittingThisMonth() == revenue3
                && g.getFittings().view().size() == jobs3);
        String unknown = g.fit(g.getSectorInvestor(g.getSectors().agriculture().key()), stack(g, "Grain Farm"), stack(g, "Grain Farm").getBuilding().getEquipment(TRACTOR));
        check("a piece not known in the city's year is refused, with a reason in words (the tractor)", unknown != null && unknown.contains("not known"), String.valueOf(unknown));
        String wrong = g.fit(g.getSectorInvestor(key), stack(g, "Grain Farm"), drive);
        check("a piece not on the stack's template is refused", wrong != null && wrong.contains("carries no"), String.valueOf(wrong));
        check("...with nothing drawn, nothing paid and no job added", industry.getCash() == cash3 && machinery.getDrawn() == drawn3 && builders.getFittingThisMonth() == revenue3
                && g.getFittings().view().size() == jobs3);

        // The month that follows: the strike, the accounts and the audit.
        double ordered = builders.getFittingThisMonth();
        // The strike at the top of the press reads the ledger since the last one: the work the last tick recognised, and the fittings ordered since.
        double recognised = builders.getRecognisedThisMonth();
        step(g, 1);
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        check("the press after two orders: the money audit closes (what the payer spent is in the builders' pool, in the order book, until their strike banks it)",
                Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved()), "residual " + audit.residual + " of " + audit.moved());
        java.util.Map<String, Double> parts = builders.otherRevenueParts();
        near("...the builders' statement names the equipment fitted, at what they were paid for it", parts.getOrDefault("Equipment fitted", 0.0), ordered, 1e-9);
        near("...and the parts it names add up to its other revenue", parts.values().stream().mapToDouble(Double::doubleValue).sum(), builders.statement().otherRevenue, 1e-9);
        near("the national accounts' construction investment is the work recognised plus the equipment fitted, once, on the builders' side",
                g.getEconomyManager().getNationalAccounts().getInvestmentConstruction(), recognised + ordered, 1e-9);
        check("...and the ledger is banked: nothing is booked for the next strike", builders.getFittingThisMonth() == 0 && g.getFittings().isEmpty());
    }

    /* ============================ 7. the work ============================ */

    static void sectionSeven() {
        out.println("\n--- 7. the work: the crews work the fittings after the repairs and before the sites, in the order placed; the count falls in the month the points are done ---");

        // The jobs alone.
        Fittings f = new Fittings();
        f.add(1, "a", "X", 100);
        f.add(1, "a", "Y", 30);
        f.add(2, "b", "X", 0);
        check("the list: 3 jobs, 130 points owed, two of a on template 1 and one of b on 2, X has some on site and Z has none",
                f.pointsOwed() == 130 && f.onSite(1, "a") == 2 && f.onSite(2, "b") == 1 && f.onSite(2, "a") == 0 && f.hasOnSite("X") && !f.hasOnSite("Z") && !f.isEmpty());
        check("working 60 points finishes none: the first job is worked first", f.work(60).isEmpty() && f.pointsOwed() == 70 && f.view().get(0).pointsLeft == 40 && f.view().get(1).pointsLeft == 30);
        java.util.List<Fittings.Job> done = f.work(40);
        check("working 40 more finishes the first and only it: the second is not touched while the points are gone (30 left)",
                done.size() == 1 && done.get(0).payer.equals("X") && f.view().size() == 2 && f.view().get(0).pointsLeft == 30 && f.onSite(1, "a") == 1);
        done = f.work(30);
        check("working 30 more finishes the second, and the job of no points behind it with the same call, in the order placed",
                done.size() == 2 && done.get(0).payer.equals("Y") && done.get(1).templateId == 2 && f.isEmpty() && f.pointsOwed() == 0);
        f.add(3, "c", "Z", 0);
        check("a job of no points finishes at the first call, whatever the points", f.work(0).size() == 1 && f.isEmpty());
        f.add(1, "a", "X", 5);
        java.util.List<Fittings.Job> copy = f.view();
        copy.get(0).pointsLeft = 1e9;
        check("the view is a copy: changing it moves nothing", f.pointsOwed() == 5);
        Fittings g2 = new Fittings();
        g2.restore(f.view());
        check("restore() puts back what view() gave, and replaces what was there", g2.view().toString().equals(f.view().toString()) && g2.pointsOwed() == 5);
        g2.restore(null);
        f.clear();
        check("restore(null) and clear() leave none", g2.isEmpty() && f.isEmpty());

        // The sites' output falls by exactly the points the fittings took: a twin without the fitting.
        Game a = fitTown("fitting-work-a", 1), b = fitTown("fitting-work-b", 1);
        holdAll(a);
        holdAll(b);
        BuildingsStacks bakeryA = stack(a, "Bakery"), bakeryB = stack(b, "Bakery");
        BuildingsTemplate t = bakeryA.getBuilding();
        Equipment drive = t.getEquipment(DRIVE);
        quietly(() -> {
            a.buildStack(t, 12, false);
            b.buildStack(t, 12, false);
        });
        check("fixture: the twins stand alike, with twelve Bakeries on site each to take every point the sites are given, and the sites are offered output after the repairs",
                a.getBuildingOutput() == b.getBuildingOutput() && a.getBuildingOutput() > drive.getWork() && bakeryA.getUnderConstruction() == 12 && bakeryB.getUnderConstruction() == 12
                        && a.getCash() == b.getCash() && afterRepairs(a) == a.getBuildingOutput(), a.getBuildingOutput() + " vs " + b.getBuildingOutput());
        int without = b.getBuildingOutput();
        String ordered = a.fitForCity(t, DRIVE);
        check("fixture: the city orders one drive in A", ordered == null && a.getFittings().onSite(t.getId(), DRIVE) == 1, String.valueOf(ordered));
        check("the output between the presses reads what the next advance will give the sites: the repairs' left less the fittings' points, whole",
                a.getBuildingOutput() == without - (int) Math.min(without, Math.ceil(drive.getWork())), a.getBuildingOutput() + " vs " + without + " - " + drive.getWork());
        step(a, 1);
        step(b, 1);
        near("one press on: the sites in A were given exactly the points the fitting took fewer than the sites in B, the piece's work", b.getBuildingManager().getPointsBuilt() - a.getBuildingManager().getPointsBuilt(),
                Math.ceil(drive.getWork()), 1e-9);
        check("...the repairs the same in both, and the job done in the month: its count down by one and nothing left on site", a.getMaintenancePoints() == b.getMaintenancePoints()
                && a.getFittings().isEmpty() && bakeryA.getUnfitted(DRIVE) == 0 && bakeryB.getUnfitted(DRIVE) == 1);
        check("...and with no job on site the output is the repairs' left, to the bit, as it always was", a.getBuildingOutput() == afterRepairs(a) && b.getBuildingOutput() == afterRepairs(b));

        // A piece whose work is more than a month's output, and the order placed.
        Game c = fitTown("fitting-work-c", 1);
        holdAll(c);
        BuildingsStacks bake = stack(c, "Bakery");
        quietly(() -> {
            c.buildStack(c.getBuildingManager().getTemplateByName("Fabrication Works"), 1, true);
            c.buildStack(bake.getBuilding(), 12, false);
        });
        BuildingsStacks fab = stack(c, "Fabrication Works");
        BuildingsTemplate fabT = fab.getBuilding();
        Equipment fabDrive = fabT.getEquipment(DRIVE);
        Sector fabSector = c.getSectors().byKey(fabT.getSector());
        // RN1 (2026-11-09): a glut's rent no longer lifted, the town came out of the settle larger and its crews fuller (59% of the posts
        // against 39% on 0.9.62), so four depots' month after the repairs (829) was more than the drive's work (612). The fixture retires
        // depots, one at a time, until even every post filled would leave the crews less than the drive's work after the repairs.
        BuildingsTemplate depot = c.getBuildingManager().getTemplateByName("Construction Depot");
        while (c.getBuildingManager().getQuantity(depot.getId()) > 0
                && c.getBuildingManager().getTotalConstructionCapacity() * c.getRoadRatio() * c.getHealth().getWorkRatio() - c.getMaintenancePoints() >= fabDrive.getWork())
            quietly(() -> c.getBuildingManager().retire(depot, 1));
        fab.setUnfitted(DRIVE, 1);
        step(c, 1);
        int month = afterRepairs(c);
        check("fixture: a Fabrication Works stands and lacks its drive, and fitting it is more work than a month's output after the repairs",
                fab.getQuantity() == 1 && fab.getUnfitted(DRIVE) == 1 && fabDrive.getWork() > month && month > bake.getBuilding().getEquipment(DRIVE).getWork(),
                "work " + fabDrive.getWork() + " vs output " + month);
        double shareBefore = fabSector.getSteamShare();
        String o1 = c.fitForCity(fabT, DRIVE);
        String o2 = c.fitForCity(bake.getBuilding(), DRIVE);
        check("fixture: the city orders the Works' drive first and a Bakery's second", o1 == null && o2 == null && c.getFittings().view().size() == 2
                && c.getFittings().view().get(0).templateId == fabT.getId() && c.getFittings().view().get(1).templateId == bake.getBuilding().getId(), o1 + " / " + o2);
        check("fixture: the Works' share of steam is up (a building in it lacks its drive)", shareBefore > 0 && fabSector.getSteamShare() == shareBefore, shareBefore + "");
        double bakeWork = bake.getBuilding().getEquipment(DRIVE).getWork();
        int presses = 0;
        boolean countHeld = true, secondHeld = true, taken = true, shareAtRest = true;
        while (!c.getFittings().isEmpty() && presses < 12) {
            double owed = c.getFittings().pointsOwed();
            double firstBefore = c.getFittings().view().get(0).pointsLeft;
            int month1 = afterRepairs(c);
            step(c, 1);
            presses++;
            taken &= Math.abs((owed - c.getFittings().pointsOwed()) - Math.min(owed, month1)) <= 1e-9;
            if (!c.getFittings().isEmpty() && c.getFittings().view().get(0).templateId == fabT.getId()) {
                countHeld &= fab.getUnfitted(DRIVE) == 1;
                secondHeld &= c.getFittings().view().size() == 2 && c.getFittings().view().get(1).pointsLeft == bakeWork;
            }
            shareAtRest &= Math.abs(fabSector.getSteamShare() - EquipmentCheck.steamShare(c, fabSector.key())) <= 1e-9;
        }
        check("each press worked exactly what the month left the crews after the repairs, or what was owed if less", taken);
        check("the Works' drive took more than one press, as the work exceeds a month's output", presses >= 2, presses + " presses");
        check("...its count fell only in the month its points were done, not before, and the job placed second had not been touched while the first was worked", countHeld && secondHeld);
        check("...and then both are fitted, in the order placed: the Works and the Bakery, nothing left on site", c.getFittings().isEmpty() && fab.getUnfitted(DRIVE) == 0
                && bake.getUnfitted(DRIVE) == 0, "works " + fab.getUnfitted(DRIVE) + ", bakery " + bake.getUnfitted(DRIVE));
        check("...and the Works' steam share went with its count, at the month's end: the counts' at rest (nothing in its sector lacks a drive now)",
                shareAtRest && fabSector.getSteamShare() == 0 && !c.getEquipmentForms().driven().contains("STEAM:" + fabSector.key()), fabSector.getSteamShare() + "");
    }

    /* ============================ 8. one at a time ============================ */

    static void sectionEight() {
        out.println("\n--- 8. one at a time: a sector with a fitting on site orders no second, and a stack whose unfitted are all on site is left out of the rank ---");
        Game g = fitTown("fitting-one", 2);
        Sector industry = g.getSectors().industry();
        String key = industry.key();
        BuildingsStacks bakery = stack(g, "Bakery");
        BuildingsTemplate t = bakery.getBuilding();
        Equipment drive = t.getEquipment(DRIVE);
        double even = breakEven(g, bakery);
        check("fixture: the drive pays at three times the break-even coal price, two Bakeries lack it and Industry has no job on site",
                EquipmentCheck.priceCoalAt(g, even * 10) && EquipmentFit.verdict(g, bakery, drive).kind() == EquipmentFit.Kind.PAYS && bakery.getUnfitted(DRIVE) == 2
                        && !g.getFittings().hasOnSite(key));
        check("with none on site the rank holds the drive", EquipmentFit.rank(g, industry).size() == 1 && EquipmentFit.rank(g, industry).get(0).pieceId().equals(DRIVE));
        // A job that never finishes (more points than any month has), so that the round meets it on site.
        g.getFittings().add(t.getId(), DRIVE, key, 1e6);
        check("one of the two is on site: the rank still holds the other (the unfitted count less the fittings on site is 1)", EquipmentFit.rank(g, industry).size() == 1);
        BusinessInvestment.Decision idle = BusinessInvestment.Decision.no(key, "nothing it wants");
        check("...but the sector has a fitting on site and orders no second: its plan's decision comes back", EquipmentFit.choose(g, industry, idle) == idle);
        for (int i = 0; i < 3; i++) step(g, 1);
        String line = g.getLastInvestment(key);
        check("three months of the real round on, the sector still orders no second: nothing Fitted and the one job is all there is", !line.startsWith("Fitted") && g.getFittings().view().size() == 1
                && g.getFittings().onSite(t.getId(), DRIVE) == 1, line + " / " + g.getFittings().view());
        g.getFittings().clear();
        g.getFittings().add(t.getId(), DRIVE, "City", 1e6);
        g.getFittings().add(t.getId(), DRIVE, "City", 1e6);
        check("a stack whose unfitted are all on site (two lack it, two are being fitted by the city) is left out of the rank, and the sector orders none",
                EquipmentFit.rank(g, industry).isEmpty() && !g.getFittings().hasOnSite(key) && EquipmentFit.choose(g, industry, idle) == idle);
        String none = g.fit(g.getSectorInvestor(key), bakery, drive);
        check("...and ordering one is refused", none != null && none.contains("already being fitted"), String.valueOf(none));
        for (int i = 0; i < 2; i++) step(g, 1);
        check("...two months of the real round on, still none ordered", !g.getLastInvestment(key).startsWith("Fitted") && g.getFittings().view().size() == 2, g.getLastInvestment(key));
    }

    /* ============================ 9. the depot's trucks ============================ */

    static void sectionNine() {
        out.println("\n--- 9. the depot's trucks: the trucks a fitting buys are delivered into Freight's fleet and the builders' draw of them is the same ---");
        Game c = EquipmentCheck.cartTown("fitting-trucks");
        ham.citybuildersim.sectors.Freight freight = c.getSectors().freight();
        String fk = freight.key();
        BuildingsStacks depot = stack(c, "Truck Depot");
        Equipment truck = depot.getBuilding().getEquipment(TRUCK);
        depot.setUnfitted(TRUCK, 1);
        step(c, 1);
        double units = truck.getBuys().getOrDefault(Good.TRUCKS, -1.0);
        check("fixture: a Truck Depot lacks its trucks, the piece buys trucks and nothing else, a month is played and the carts' horse share is up",
                depot.getUnfitted(TRUCK) == 1 && truck.getBuys().size() == 1 && units > 0 && truck.getEnds() == Equipment.Form.CART_HORSES
                        && freight.haulage().getHorseShare() > 0, truck.getBuys().toString());
        GoodsMarket market = c.getMarkets().get(Good.TRUCKS);
        c.getEconomyManager().setSectorCash(fk, expectedCost(c, truck) * 4);
        double held0 = freight.getPantry(Good.TRUCKS), drawn0 = market.getDrawn(), cash0 = freight.getCash();
        double expected = expectedCost(c, truck);
        int trades0 = market.getTrades().size();
        String refused = c.fit(c.getSectorInvestor(fk), depot, truck);
        check("Freight orders the trucks: taken", refused == null, String.valueOf(refused));
        near("Freight's trucks held rose by the piece's TRUCKS, added to what it held", freight.getPantry(Good.TRUCKS) - held0, units, 1e-12);
        near("the builders' draw of TRUCKS is the same units, in the market's own count", market.getDrawn() - drawn0, units, 1e-12);
        near("...and in the market's records: the builders are the buyer", buildersDrew(c, Good.TRUCKS, trades0)[0], units, 1e-12);
        near("Freight paid the draws' cost, and the piece has no work", cash0 - freight.getCash(), expected, 1e-9);
        check("the depot is not yet fitted (a job of no points is on site): the horse share has not moved", depot.getUnfitted(TRUCK) == 1 && c.getFittings().onSite(depot.getBuilding().getId(), TRUCK) == 1
                && truck.getWork() == 0);
        step(c, 1);
        check("the next press works it (no points): the depot is fitted and the carts' horse share is 0", c.getFittings().isEmpty() && depot.getUnfitted(TRUCK) == 0
                && freight.haulage().getHorseShare() == 0, "share " + freight.haulage().getHorseShare());
    }

    /* ============================ 10. the player's Fit ============================ */

    static void sectionTen() {
        out.println("\n--- 10. the player's Fit: the city pays whatever the verdict says, not limited to one at a time, refused with nothing drawn and nothing paid ---");
        Game g = fitTown("fitting-city", 3);
        holdAll(g);
        BuildingsStacks bakery = stack(g, "Bakery");
        BuildingsTemplate t = bakery.getBuilding();
        Equipment drive = t.getEquipment(DRIVE);
        ham.citybuildersim.sectors.Construction builders = g.getSectors().construction();
        GoodsMarket machinery = g.getMarkets().get(Good.MACHINERY);
        /*
         * ...WITH THE CITY'S COAL AT HALF THE BREAK-EVEN (GW1, 2026-11-10). At the prices this village stands at after its settling months the drive paid
         * (Verdict PAYS, a gain of 0.0531 a month on a cost of 8.64, earning back 9.18, the hurdle 0.0441): the staffing gate counts the workers a building's own
         * posts would draw (Sector.GATE_DRAW_MONTHS), so the village's businesses build earlier and its wages and prices part from where the drive did not pay.
         * The player's Fit is taken whatever the verdict says, and the claim needs a verdict that says NOT_YET: the coal is priced at half the break-even the
         * stack's own verdict gives, as section 5 prices it for the drive that does not pay.
         */
        boolean coalMoved = EquipmentCheck.priceCoalAt(g, breakEven(g, bakery) * 0.5);
        EquipmentFit.Verdict v = EquipmentFit.verdict(g, bakery, drive);
        check("fixture: three Bakeries lack the drive and, with the city's coal at half the break-even, it does not pay (NOT_YET), and the treasury can pay", bakery.getUnfitted(DRIVE) == 3
                && coalMoved && v.kind() == EquipmentFit.Kind.NOT_YET && g.getCash() > 100 * expectedCost(g, drive), v.toString());

        double expected = expectedCost(g, drive);
        double cash0 = g.getCash(), drawn0 = machinery.getDrawn(), revenue0 = builders.getFittingThisMonth();
        String first = g.fitForCity(t, DRIVE);
        check("the player's Fit is taken though the verdict is NOT_YET: it is the player's money", first == null, String.valueOf(first));
        near("...the treasury fell by the total: the goods at the market and the work at the builders' price a point", cash0 - g.getCash(), expected, 1e-9);
        check("...a \"City\" job is on site, and the count has not moved", g.getFittings().view().size() == 1 && g.getFittings().view().get(0).payer.equals("City")
                && g.getFittings().view().get(0).pointsLeft == drive.getWork() && bakery.getUnfitted(DRIVE) == 3);
        near("...the units drawn are the piece's", machinery.getDrawn() - drawn0, drive.getBuys().get(Good.MACHINERY), 1e-12);
        near("...and the builders earned the total", builders.getFittingThisMonth() - revenue0, expected, 1e-9);
        String second = g.fitForCity(t, DRIVE);
        check("it is not limited to one at a time: a second goes on site beside the first", second == null && g.getFittings().onSite(t.getId(), DRIVE) == 2, String.valueOf(second));

        // A treasury short of the total.
        double need = expectedCost(g, drive);
        double saved = g.getCash();
        g.setCashForTest(need * 0.5);
        double cash1 = g.getCash(), drawn1 = machinery.getDrawn(), revenue1 = builders.getFittingThisMonth();
        int jobs1 = g.getFittings().view().size();
        String short_ = g.fitForCity(t, DRIVE);
        check("a treasury short of the total refuses it (the city does not borrow for it), with a reason that names the treasury", short_ != null && short_.contains("treasury"), String.valueOf(short_));
        check("...nothing drawn, nothing paid, no job added", g.getCash() == cash1 && machinery.getDrawn() == drawn1 && builders.getFittingThisMonth() == revenue1
                && g.getFittings().view().size() == jobs1);
        g.setCashForTest(saved);

        // The tractor: not known.
        BuildingsStacks grain = stack(g, "Grain Farm");
        grain.setUnfitted(TRACTOR, 1);
        double cash2 = g.getCash(), drawn2 = g.getMarkets().get(Good.MACHINERY).getDrawn();
        int jobs2 = g.getFittings().view().size();
        String tractor = g.fitForCity(grain.getBuilding(), TRACTOR);
        check("the tractor is refused: it is not known in the city's year", tractor != null && tractor.contains("not known"), String.valueOf(tractor));
        check("...nothing drawn, nothing paid, no job added", g.getCash() == cash2 && g.getMarkets().get(Good.MACHINERY).getDrawn() == drawn2 && g.getFittings().view().size() == jobs2);
        grain.setUnfitted(TRACTOR, 0);

        // The third lacking building, then every unfitted building on site.
        String third = g.fitForCity(t, DRIVE);
        check("the third Bakery is taken, and then every one that lacks the drive is on site", third == null && g.getFittings().onSite(t.getId(), DRIVE) == 3, String.valueOf(third));
        double cash3 = g.getCash(), drawn3 = machinery.getDrawn();
        String fourth = g.fitForCity(t, DRIVE);
        double ordered = builders.getFittingThisMonth();
        double recognised = builders.getRecognisedThisMonth();
        check("a fourth is refused: every unfitted building of the stack is on site", fourth != null && fourth.contains("already being fitted"), String.valueOf(fourth));
        check("...nothing drawn, nothing paid, no job added", g.getCash() == cash3 && machinery.getDrawn() == drawn3 && g.getFittings().view().size() == 3);
        check("a template with no stack, or a piece it does not carry, is refused", g.fitForCity(null, DRIVE) != null && g.fitForCity(t, "no-such-piece") != null && g.fitForCity(t, null) != null);

        // The month: the city's own money went out through its journal, as its buildings' do (treasuryPays()), and the audit and the accounts see it.
        step(g, 1);
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        check("the press after the city's three orders: the money audit closes", Math.abs(audit.residual) <= MoneyAudit.tolerance(audit.moved()), "residual " + audit.residual + " of " + audit.moved());
        near("...the national accounts' construction investment carries the three, once", g.getEconomyManager().getNationalAccounts().getInvestmentConstruction(),
                recognised + ordered, 1e-9);
    }

    /* ============================ 11. the save ============================ */

    /** Every sector's cash and the treasury, the counts and the jobs, as one line: two cities are alike when their lines are equal. */
    static String stateLine(Game g) {
        StringBuilder sb = new StringBuilder();
        sb.append("treasury=").append(Double.doubleToLongBits(g.getCash())).append(' ');
        for (Sector s : g.getSectors().all()) sb.append(s.key()).append('=').append(Double.doubleToLongBits(s.getCash())).append(' ');
        sb.append(g.getBuildingManager().getUnfittedById()).append(' ').append(g.getFittings().view());
        sb.append(" revenue=").append(Double.doubleToLongBits(g.getSectors().construction().getFittingThisMonth()));
        return sb.toString();
    }

    static void sectionEleven() throws Exception {
        out.println("\n--- 11. the save: no job, no key; the jobs saved are the jobs loaded, and a loaded city and the one it was saved from stay equal to the bit ---");
        Game g = fitTown("fitting-save", 2);
        holdAll(g);
        BuildingsStacks bakery = stack(g, "Bakery");
        quietly(() -> g.buildStack(g.getBuildingManager().getTemplateByName("Fabrication Works"), 1, true));
        BuildingsStacks fab = stack(g, "Fabrication Works");
        fab.setUnfitted(DRIVE, 1);
        step(g, 1);

        GameFiles.Result none = EquipmentCheck.save(g, "fitting save none");
        String noneJson = none.ok ? Files.readString(g.getGameFiles().saveFile(1)) : "";
        check("a city with no job on site saves no fittings key", none.ok && g.getFittings().isEmpty() && !noneJson.contains("\"fittings\""), none.message());
        Game empty = new Game(g.getGameFiles());
        empty.playOnLiterals();
        quietly(() -> empty.loadGameSave(1));
        check("...and loads with none on site", empty.getLoadFailure() == null && empty.getFittings().isEmpty(), String.valueOf(empty.getLoadFailure()));

        String o1 = g.fitForCity(fab.getBuilding(), DRIVE), o2 = g.fitForCity(bakery.getBuilding(), DRIVE);
        check("fixture: two jobs on site (the Works' first), ordered between two presses so that the builders' ledger holds their revenue", o1 == null && o2 == null
                && g.getFittings().view().size() == 2 && g.getSectors().construction().getFittingThisMonth() > 0, o1 + " / " + o2);
        GameFiles.Result wrote = EquipmentCheck.save(g, "fitting save jobs");
        String json = wrote.ok ? Files.readString(g.getGameFiles().saveFile(1)) : "";
        check("a city with jobs on site saves them (the key is in the file)", wrote.ok && json.contains("\"fittings\""), wrote.message());
        Game b = new Game(g.getGameFiles());
        // On the literals and held like the city it was saved from, before the load: neither is saved (Game.playOnLiterals()).
        b.playOnLiterals();
        quietly(() -> b.loadGameSave(1));
        holdAll(b);
        check("...and loads without a failure, in the same month, with the same jobs in the same order, every field to the bit",
                b.getLoadFailure() == null && b.getMonth() == g.getMonth() && b.getFittings().view().toString().equals(g.getFittings().view().toString())
                        && b.getFittings().view().size() == 2, String.valueOf(b.getLoadFailure()) + " / " + b.getFittings().view());
        check("...the builders' fitting revenue booked in the ledger is back, so that its strike names it", b.getSectors().construction().getFittingThisMonth()
                == g.getSectors().construction().getFittingThisMonth(), b.getSectors().construction().getFittingThisMonth() + " vs " + g.getSectors().construction().getFittingThisMonth());
        check("the twin and the loaded city are equal to the bit in the treasury, every sector's cash, the counts and the jobs, before any press", stateLine(g).equals(stateLine(b)),
                stateLine(g) + " | " + stateLine(b));
        boolean same = true;
        String first = null;
        for (int i = 0; i < 6 && (!g.getFittings().isEmpty() || i == 0); i++) {
            step(g, 1);
            step(b, 1);
            if (!stateLine(g).equals(stateLine(b))) {
                same = false;
                first = "month " + (i + 1) + ": " + stateLine(g) + " | " + stateLine(b);
                break;
            }
        }
        check("...and equal to the bit after each press until the jobs are worked", same, first);
        check("...the jobs worked in both, the counts equal and down by one each", g.getFittings().isEmpty() && b.getFittings().isEmpty() && fab.getUnfitted(DRIVE) == 0
                && stack(b, "Fabrication Works").getUnfitted(DRIVE) == 0 && bakery.getUnfitted(DRIVE) == 1 && stack(b, "Bakery").getUnfitted(DRIVE) == 1,
                g.getFittings().view() + " / " + b.getFittings().view());
        step(g, 2);
        step(b, 2);
        check("...and two presses beyond, equal still", stateLine(g).equals(stateLine(b)), stateLine(g) + " | " + stateLine(b));
    }

    /* ============================ 12. a default city orders none ============================ */

    static void sectionTwelve() {
        out.println("\n--- 12. a default city orders none: a town founded the default way, played 24 months, has no job ever on site and no decision with a piece ---");
        Game g = Forms1900Check.town("fitting-default-city");
        boolean noJob = true, noFitted = true, planKept = true, noneUnfitted = true;
        String fault = null;
        for (int month = 0; month < 24; month++) {
            step(g, 1);
            noJob &= g.getFittings().isEmpty();
            noneUnfitted &= !EquipmentCheck.anyUnfitted(g);
            for (Sector s : g.getSectors().all()) {
                String line = g.getLastInvestment(s.key());
                if (line != null && line.startsWith("Fitted")) { noFitted = false; fault = "month " + month + " " + s.key() + ": " + line; }
                BusinessInvestment.Decision plan = BusinessInvestment.Decision.no(s.key(), "nothing it wants");
                if (EquipmentFit.choose(g, s, plan) != plan) { planKept = false; fault = "month " + month + " " + s.key() + " got a decision of its own"; }
            }
        }
        check("fixture: nothing is unfitted in the town in any of the 24 months", noneUnfitted && !g.foundedUnfitted());
        check("no job is on site in any of the 24 months, and the builders were paid no fitting", noJob && g.getSectors().construction().getFittingThisMonth() == 0);
        check("no sector's line says Fitted in any month, and the choice hands every sector's plan back, the very object, every month", noFitted && planKept, fault);
    }
}
