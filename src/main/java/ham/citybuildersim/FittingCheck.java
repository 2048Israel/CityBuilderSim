package ham.citybuildersim;

import ham.citybuildersim.sectors.Agriculture;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Set;

/**
 * Fitting the equipment (R1b): the 1900 forms at rest between the presses, a Game's forms cleared when it founds or loads another city, the city
 * founded the 1900 way, and what a building that finishes comes with.
 *
 * WHAT THIS HAS TO PROVE (R1b slice A, 2026-11-05; docs/queue/R1b-notes.md; research-design 2.2 step 6; R1's stranger's review). R1 made a piece
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
 *      what finished), a farm finishing never has its tractor (NOT_KNOWN), and the order costs what it costs in a default city; in a default
 *      city every building finishes fitted, at either price
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

        out.println(fails == 0 ? "\nAll FittingCheck assertions passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. the shares at rest ============================ */

    static void sectionOne() {
        out.println("--- 1. the shares at rest: a save written at the month's end and the city that goes on rest in the same shares, and keep together through the next press ---");
        Game a = Forms1900Check.town("fitting-rest");
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
        check("...the tractor's too: the Grain Farm's whole stack, which stays on horses (NOT_KNOWN, never paying)",
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

    /** Presses the clock until nothing is on site on this stack (at most `most` months); how many buildings opened over them. */
    static int pressUntilOpen(Game g, BuildingsStacks s, int most) {
        int opened = 0;
        for (int i = 0; i < most && s.getUnderConstruction() > 0; i++) {
            step(g, 1);
            opened += s.getLastFinished();
        }
        return opened;
    }

    /** What the last order cost, by city: [the city founded the 1900 way, the default one]. */
    static final double[] ORDER_COST = { Double.NaN, Double.NaN };

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
        check("a new building's verdict for the drive is priced in 1900 (PAYS or NOT_YET) and is never FITTED", drive.knownIn(CityCalendar.yearOf(g.getMonth()))
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
        quietly(() -> ordered[0] = g.buildStack(t, 2, false));
        ORDER_COST[which] = cash - g.getCash();
        check("fixture: two Bakeries are on site", ordered[0] == Game.BuildResult.SUCCESS && bakery.getUnderConstruction() == 2, String.valueOf(ordered[0]));
        int opened = pressUntilOpen(g, bakery, 36);
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
        opened = pressUntilOpen(g, bakery, 36);
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

        // A farm finishing: the tractor is not known (a catalogue code), whatever the prices.
        BuildingsStacks grain = stack(g, "Grain Farm");
        Equipment tractor = grain.getBuilding().getEquipment(TRACTOR);
        final int tractorBefore = grain.getUnfitted(TRACTOR);
        quietly(() -> ordered[0] = g.buildStack(grain.getBuilding(), 1, false));
        check("fixture: a Grain Farm is on site", ordered[0] == Game.BuildResult.SUCCESS && grain.getUnderConstruction() == 1, String.valueOf(ordered[0]));
        int farmsOpened = pressUntilOpen(g, grain, 36);
        check("the verdict for a new farm's tractor is NOT_KNOWN, whatever the prices", EquipmentFit.verdictForNew(g, grain.getBuilding(), tractor).kind() == EquipmentFit.Kind.NOT_KNOWN);
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
        check("the order costs what it costs: two Bakeries cost the same in a city founded the 1900 way as in a default one (decided at the finish, not the order)",
                Double.compare(ORDER_COST[0], ORDER_COST[1]) == 0 && ORDER_COST[0] > 0, ORDER_COST[0] + " vs " + ORDER_COST[1]);
    }
}
