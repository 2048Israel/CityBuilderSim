package ham.citybuildersim;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ham.citybuildersim.sectors.Agriculture;
import ham.citybuildersim.sectors.Haulage;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Equipment as data, the count of buildings still without it on each stack, and R0c's 1900 forms set from that count.
 *
 * WHAT THIS HAS TO PROVE (R1 slice A, 2026-11-05; docs/queue/R1-notes.md calls 2 to 9; research-design 2.1-2.2). R0c built the steam, the
 * farms' horses and the carts' horses as shares of a sector that nothing sets; R1 fits equipment building by building, and "the forms read one
 * number" (R0c's call 3). This part is the state, off: a piece of equipment is data on a template, a stack counts the buildings without it, and
 * EquipmentForms sets the shares from the counts where the Policy hears the month. In every played city no building is unfitted, so nothing
 * moves. What is checkable:
 *
 *   1. THE DATA IS READ: every template the notes name carries its piece, its since, ends, buys, work and life read back from buildings.json
 *      (the file read here with Gson, its figures not typed twice), no other template carries one, the rules the figures were struck by hold
 *      on the template's own electricity, construction points, land and truck room, the tractor's code is never known, the drive is known
 *      from 1900; and a bad entry in the file (no id, a second of an id, an unknown form or good, no since, a life of 0) is reported and
 *      skipped, the good ones beside it read
 *   2. A PLAYED CITY HAS NONE UNFITTED: after SETTLE and twelve months no stack has an unfitted building, every steam share and both horse
 *      shares are 0 and EquipmentForms has driven none
 *   3. THE COUNTS SET THE FORMS: unfitted bakeries set Industry's steam share to the share of its grid draw, unfitted farms Agriculture's
 *      horse share to the share of its land, an unfitted depot Haulage's horse share to the share of its truck room, each as the harness
 *      works it out from the stacks and the templates, and no other sector's
 *   4. BACK TO 0: the counts set to 0 put every share driven back to 0 the next month, and it stays 0 a month later
 *   5. A SHARE SET BY HAND STANDS: with no count anywhere, a share set by hand is the same after a month (Forms1900Check's way)
 *   6. THE COUNT FOLLOWS THE STACK: a count set above what stands is what stands; a scrapped building is an unfitted one first; a building
 *      finished (bought, increased, or built on site) leaves the count as it was
 *   7. THE SAVE: a city with none unfitted writes no key; a city with counts writes them by template id, and the one loaded from it has the
 *      same counts and, before any month is played, the shares the counts set, equal to the saved city's
 *
 * Every fixture CAUSES its condition: the counts are set on stacks that stand, in the towns Forms1900Check builds (farms and bakeries) and in
 * HaulageCheck's city with a depot built. The figures are asserted against the stacks' own quantities and the templates' own getters, never
 * numbers copied from the brief.
 */
public class EquipmentCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static final String DRIVE = "electric-drive", TRUCK = "motor-truck", TRACTOR = "tractor";

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

    static void play(Game g, int months) { quietly(() -> g.simulateMonths(months)); }

    static void step(Game g, int months) { quietly(() -> { for (int i = 0; i < months; i++) g.toggleNextMonth(); }); }

    static BuildingsStacks stack(Game g, String template) {
        return g.getBuildingManager().getStack(g.getBuildingManager().getTemplateByName(template));
    }

    /* ---------------------- the shares the counts should set, worked out here from the stacks and the templates ---------------------- */

    /** The unfitted part of a sector's grid draw: each stack's unfitted buildings (of the piece that ends the steam) at their electricity over all its buildings' at theirs. */
    static double steamShare(Game g, String sectorKey) {
        double total = 0, off = 0;
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            BuildingsTemplate t = s.getBuilding();
            if (!sectorKey.equals(t.getSector())) continue;
            total += s.getQuantity() * (double) t.getElectricityConsumption();
            off += s.getUnfitted(DRIVE) * (double) t.getElectricityConsumption();
        }
        return total > 0 ? off / total : 0;
    }

    /** The unfitted part of the farms' land. */
    static double farmShare(Game g) {
        double total = 0, off = 0;
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            BuildingsTemplate t = s.getBuilding();
            if (!g.getSectors().agriculture().key().equals(t.getSector())) continue;
            total += s.getQuantity() * t.getLandSqFt();
            off += s.getUnfitted(TRACTOR) * t.getLandSqFt();
        }
        return total > 0 ? off / total : 0;
    }

    /** The unfitted part of the depots' truck room. */
    static double cartShare(Game g) {
        double total = 0, off = 0;
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            double room = s.getBuilding().getTruckRoom();
            if (!(room > 0)) continue;
            total += s.getQuantity() * room;
            off += s.getUnfitted(TRUCK) * room;
        }
        return total > 0 ? off / total : 0;
    }

    /** Whether any stack of the city is without any piece. */
    static boolean anyUnfitted(Game g) {
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) if (s.hasUnfitted()) return true;
        return false;
    }

    /** The largest steam share any sector has. */
    static double maxSteam(Game g) {
        double m = 0;
        for (Sector s : g.getSectors().all()) m = Math.max(m, s.getSteamShare());
        return m;
    }

    /** The largest steam share any sector has but the one named. */
    static double maxSteamExcept(Game g, String key) {
        double m = 0;
        for (Sector s : g.getSectors().all()) if (!s.key().equals(key)) m = Math.max(m, s.getSteamShare());
        return m;
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(OutputStream.nullOutputStream());
        // The town is Forms1900Check's, which narrates through its own streams.
        Forms1900Check.out = out;
        Forms1900Check.quiet = quiet;

        sectionOne();
        Game played = Forms1900Check.town("equipment-played");
        sectionTwo(played);
        Game counted = Forms1900Check.town("equipment-counted");
        Game carts = cartTown("equipment-carts");
        sectionThree(counted, carts);
        sectionFour(counted, carts);
        sectionFive(played);
        sectionSix(counted);
        sectionSeven(counted, played);

        out.println(fails == 0 ? "\nAll EquipmentCheck assertions passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** HaulageCheck's city on the goods' literals, with two Truck Depots standing (Haulage's carts are the depots' trucks). */
    static Game cartTown(String label) {
        Game g = HaulageCheck.base(GameFiles.scratch(label), true, true);
        quietly(() -> g.buildStack(g.getBuildingManager().getTemplateByName("Truck Depot"), 2, true));
        return g;
    }

    /* ============================ 1. the data is read ============================ */

    /** The templates that carry each piece, by name, as the notes say (R1-notes calls 2 and 3). */
    static final String[] DRIVES = {
        "Meat Works", "Snack & Oils Plant", "Bottling Plant", "Industrial Bakery", "Bakery",
        "Fabrication Shop", "Machine Works", "Fabrication Works", "Wire & Cable Works",
        "Vehicle Works", "Assembly Plant", "Commercial Vehicle Plant", "Locomotive Works", "Heavy Truck Plant",
        "Sawmill", "Large Sawmill", "Paper Mill", "Construction Materials Plant"
    };
    static final String[] FARMS = { "Mixed Farm", "Grain Farm", "Livestock Farm" };

    /** The piece of this id as the file says it, on the building of this id, or null. */
    static JsonObject jsonPiece(JsonObject building, String pieceId) {
        if (building == null || !building.has("equipment")) return null;
        for (JsonElement e : building.getAsJsonArray("equipment")) {
            if (e.getAsJsonObject().get("id").getAsString().equals(pieceId)) return e.getAsJsonObject();
        }
        return null;
    }

    static void sectionOne() throws Exception {
        out.println("--- 1. the data is read: every piece the notes name is on its template, as the file says it ---");
        JsonObject root;
        try (InputStreamReader in = new InputStreamReader(EquipmentCheck.class.getResourceAsStream("/" + BuildingCatalog.FILE_NAME), StandardCharsets.UTF_8)) {
            root = JsonParser.parseReader(in).getAsJsonObject();
        }
        Map<Integer, JsonObject> file = new TreeMap<>();
        for (JsonElement e : root.getAsJsonArray("buildings")) file.put(e.getAsJsonObject().get("id").getAsInt(), e.getAsJsonObject());

        BuildingManager bm = new BuildingManager();
        quietly(bm::initializeTemplates);

        int expectedPieces = DRIVES.length + 1 + FARMS.length, carried = 0, filePieces = 0, mismatched = 0;
        String firstMismatch = null;
        for (JsonObject o : file.values()) if (o.has("equipment")) filePieces += o.getAsJsonArray("equipment").size();
        for (BuildingsTemplate t : bm.getTemplates()) {
            carried += t.getEquipment().size();
            JsonObject o = file.get(t.getId());
            int inFile = o != null && o.has("equipment") ? o.getAsJsonArray("equipment").size() : 0;
            if (inFile != t.getEquipment().size()) {
                mismatched++;
                if (firstMismatch == null) firstMismatch = t.getName() + ": the file has " + inFile + ", the template " + t.getEquipment().size();
            }
            for (Equipment p : t.getEquipment()) {
                JsonObject j = jsonPiece(o, p.getId());
                boolean same = j != null
                        && p.getName().equals(j.get("name").getAsString())
                        && p.getSince().equals(j.get("since").getAsString())
                        && p.getEnds() == Equipment.Form.valueOf(j.get("ends").getAsString())
                        && p.getWork() == j.get("work").getAsDouble()
                        && p.getLifeMonths() == j.get("lifeMonths").getAsInt()
                        && p.getBuys().size() == j.getAsJsonObject("buys").size();
                if (same) {
                    for (Map.Entry<String, JsonElement> b : j.getAsJsonObject("buys").entrySet()) {
                        Good good = Good.byName(b.getKey());
                        if (good == null || p.getBuys().getOrDefault(good, -1.0) != b.getValue().getAsDouble()) same = false;
                    }
                }
                if (!same) {
                    mismatched++;
                    if (firstMismatch == null) firstMismatch = t.getName() + "/" + p.getId() + " reads other than the file";
                }
            }
        }
        check("fixture: the file carries equipment, and the notes' pieces are the whole of it",
                filePieces == expectedPieces, filePieces + " in the file, " + expectedPieces + " named");
        check("every piece in the file is read onto its template, field for field (id, name, since, ends, buys, work, life)",
                mismatched == 0 && carried == filePieces, firstMismatch == null ? carried + " carried" : firstMismatch);

        int driveOk = 0, farmOk = 0;
        String bad = null;
        for (String name : DRIVES) {
            BuildingsTemplate t = bm.getTemplateByName(name);
            Equipment p = t == null ? null : t.getEquipment(DRIVE);
            boolean ok = p != null && t.getEquipment().size() == 1 && p.getEnds() == Equipment.Form.STEAM && p.isYear() && p.getSince().equals("1900")
                    && p.getLifeMonths() == 300;
            // The rules the figures were struck by (R1-notes call 9, est.): the motors' tonnes are 8 kg a kW of peak draw, the month's mean
            // over a 0.6 load factor, to a tenth of a tonne; the work a tenth of the building's own construction points, to a point.
            if (ok) {
                double machinery = p.getBuys().getOrDefault(Good.MACHINERY, -1.0);
                ok = p.getBuys().size() == 1
                        && Math.abs(machinery - t.getElectricityConsumption() * 0.008 / 0.6) <= 0.05 + 1e-9
                        && Math.abs(p.getWork() - t.getConstructionPoints() / 10.0) <= 0.5 + 1e-9;
            }
            if (ok) driveOk++;
            else if (bad == null) bad = name;
        }
        check("the electric motor drive is on the " + DRIVES.length + " factories, known from 1900, 300 months, ending STEAM, its tonnes and work by the rule",
                driveOk == DRIVES.length, "first wrong: " + bad);

        BuildingsTemplate depot = bm.getTemplateByName("Truck Depot");
        Equipment truck = depot == null ? null : depot.getEquipment(TRUCK);
        check("the motor truck is on the Truck Depot alone, ending CART_HORSES, its room in TRUCKS and no work",
                truck != null && depot.getEquipment().size() == 1 && truck.getEnds() == Equipment.Form.CART_HORSES && truck.isYear()
                        && truck.getBuys().size() == 1 && truck.getBuys().getOrDefault(Good.TRUCKS, -1.0) == depot.getTruckRoom()
                        && truck.getWork() == 0 && truck.getLifeMonths() > 0);

        for (String name : FARMS) {
            BuildingsTemplate t = bm.getTemplateByName(name);
            Equipment p = t == null ? null : t.getEquipment(TRACTOR);
            boolean ok = p != null && t.getEquipment().size() == 1 && p.getEnds() == Equipment.Form.FARM_HORSES && !p.isYear()
                    && p.getSince().equals("PE14") && p.getWork() == 0 && p.getLifeMonths() == 180 && p.getBuys().size() == 1;
            // 3 t of machinery a hundred acres of the farm's land (R1-notes call 9, est.), to a tenth of a tonne.
            if (ok) ok = Math.abs(p.getBuys().getOrDefault(Good.MACHINERY, -1.0) - 3.0 * t.getLandSqFt() / 43_560 / 100) <= 0.05 + 1e-9;
            if (ok) farmOk++;
        }
        check("the tractor is on the three farms alone, ending FARM_HORSES, 3 t of machinery a hundred acres, no work, 180 months",
                farmOk == FARMS.length, farmOk + " of " + FARMS.length);

        int other = 0;
        List<String> named = new ArrayList<>(List.of(DRIVES));
        named.addAll(List.of(FARMS));
        named.add("Truck Depot");
        for (BuildingsTemplate t : bm.getTemplates()) if (!t.getEquipment().isEmpty() && !named.contains(t.getName())) other++;
        check("no other template carries a piece", other == 0, other + " do");

        boolean everyEnds = true;
        for (BuildingsTemplate t : bm.getTemplates()) for (Equipment p : t.getEquipment()) everyEnds &= p.getEnds() != null;
        check("every piece ends one of the three forms", everyEnds);

        BuildingsTemplate grainFarm = bm.getTemplateByName("Grain Farm"), bakery = bm.getTemplateByName("Bakery");
        Equipment tractor = grainFarm == null ? null : grainFarm.getEquipment(TRACTOR);
        Equipment drive = bakery == null ? null : bakery.getEquipment(DRIVE);
        boolean tractorNever = tractor != null;
        for (int y = 1800; y <= 2200 && tractor != null; y++) tractorNever &= !tractor.knownIn(y);
        check("the tractor's code is not a year and is known in no year: the piece is data, and never pays", tractor != null && !tractor.isYear() && tractorNever);
        check("the electric drive is known from its January 1900 and not in 1899", drive != null && drive.knownIn(1900) && !drive.knownIn(1899) && drive.knownIn(2025));

        // A bad entry costs that entry: it is reported and skipped, and the good ones beside it are read.
        String json = "{\"buildings\": [{\"id\": 1, \"name\": \"Test Works\", \"category\": \"INDUSTRIAL\", \"equipment\": ["
                + "{\"id\": \"good\", \"name\": \"Good\", \"since\": 1900, \"ends\": \"STEAM\", \"buys\": {\"MACHINERY\": 2.5}, \"work\": 10, \"lifeMonths\": 100},"
                + "{\"id\": \"good\", \"since\": \"1900\", \"ends\": \"STEAM\", \"lifeMonths\": 100},"
                + "{\"since\": \"1900\", \"ends\": \"STEAM\", \"lifeMonths\": 100},"
                + "{\"id\": \"formless\", \"since\": \"1900\", \"ends\": \"WINDMILL\", \"lifeMonths\": 100},"
                + "{\"id\": \"goodless\", \"since\": \"1900\", \"ends\": \"STEAM\", \"buys\": {\"UNOBTAINIUM\": 1}, \"lifeMonths\": 100},"
                + "{\"id\": \"datelss\", \"ends\": \"STEAM\", \"lifeMonths\": 100},"
                + "{\"id\": \"lifeless\", \"since\": \"1900\", \"ends\": \"STEAM\", \"lifeMonths\": 0},"
                + "\"not an object\","
                + "{\"id\": \"coded\", \"name\": \"Coded\", \"since\": \"PE99\", \"ends\": \"CART_HORSES\", \"buys\": {\"TRUCKS\": 5}, \"work\": 0, \"lifeMonths\": 60}"
                + "]}]}";
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        PrintStream real = System.out;
        List<BuildingsTemplate> read;
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
        try {
            read = new BuildingCatalog().parse(new StringReader(json));
        } finally {
            System.setOut(real);
        }
        String said = captured.toString(StandardCharsets.UTF_8);
        BuildingsTemplate test = read == null || read.isEmpty() ? null : read.get(0);
        check("a file with seven bad pieces and two good still reads the building", test != null);
        check("...reading the good pieces beside them, in the file's order, and no bad one",
                test != null && test.getEquipment().size() == 2 && test.getEquipment().get(0).getId().equals("good")
                        && test.getEquipment().get(1).getId().equals("coded"),
                test == null ? "no template" : test.getEquipment().toString());
        check("...a numeric since read as its digits, a code as written, the first of two of one id kept",
                test != null && test.getEquipment().size() == 2 && test.getEquipment().get(0).getSince().equals("1900")
                        && test.getEquipment(DRIVE) == null && test.getEquipment("coded").getSince().equals("PE99")
                        && test.getEquipment("good").getBuys().get(Good.MACHINERY) == 2.5);
        check("...and each bad one reported by its line (the second \"good\", no id, the form, the good, no since, the life, the non-object)",
                said.contains("two pieces of equipment called \"good\"") && said.contains("no id") && said.contains("WINDMILL")
                        && said.contains("UNOBTAINIUM") && said.contains("\"datelss\"") && said.contains("\"lifeless\"")
                        && said.contains("non-object"),
                said);
    }

    /* ============================ 2. a played city has none unfitted ============================ */

    static void sectionTwo(Game g) {
        out.println("\n--- 2. a played city has none unfitted: SETTLE and twelve months, no count and every form at 0 ---");
        play(g, Forms1900Check.MONTHS);
        BuildingManager bm = g.getBuildingManager();
        check("fixture: the town has the bakeries, the farms and a sector to ask", stack(g, "Bakery").getQuantity() > 0
                && stack(g, "Industrial Bakery").getQuantity() > 0 && stack(g, "Grain Farm").getQuantity() > 0
                && stack(g, "Mixed Farm").getQuantity() > 0);
        check("no stack has an unfitted building", !anyUnfitted(g));
        check("...and none is written to the save's view of the buildings", bm.getUnfittedById().isEmpty());
        check("every sector's steam share is 0", maxSteam(g) == 0, "largest " + maxSteam(g));
        check("the farms' horse share is 0", g.getSectors().agriculture().getHorseShare() == 0);
        check("Haulage's horse share is 0", g.getSectors().freight().haulage().getHorseShare() == 0);
        check("EquipmentForms has driven none", g.getEquipmentForms().driven().isEmpty(), g.getEquipmentForms().driven().toString());
    }

    /* ============================ 3. the counts set the forms ============================ */

    static void sectionThree(Game g, Game c) {
        out.println("\n--- 3. the counts set the forms: the unfitted share of the draw, the land and the truck room, set where the month begins ---");
        Sector industry = g.getSectors().industry();
        Agriculture farms = g.getSectors().agriculture();

        BuildingsStacks bakery = stack(g, "Bakery"), industrial = stack(g, "Industrial Bakery"), grain = stack(g, "Grain Farm");
        check("fixture: two Bakeries, an Industrial Bakery and a Grain Farm stand", bakery.getQuantity() == 2 && industrial.getQuantity() >= 1
                && grain.getQuantity() >= 1, bakery.getQuantity() + "/" + industrial.getQuantity() + "/" + grain.getQuantity());
        // One of the two Bakeries, the whole of the Industrial Bakery stack and of the Grain Farm stack.
        int industrialAll = industrial.getQuantity(), grainAll = grain.getQuantity();
        bakery.setUnfitted(DRIVE, 1);
        industrial.setUnfitted(DRIVE, industrialAll);
        grain.setUnfitted(TRACTOR, grainAll);
        check("fixture: the counts stand as set", bakery.getUnfitted(DRIVE) == 1 && industrial.getUnfitted(DRIVE) == industrialAll
                && grain.getUnfitted(TRACTOR) == grainAll);
        double steam = steamShare(g, industry.key()), horses = farmShare(g);
        check("fixture: some of the draw and some of the land is unfitted, not all", steam > 0 && steam < 1 && horses > 0 && horses < 1,
                steam + " / " + horses);
        check("fixture: before the month the shares are 0", industry.getSteamShare() == 0 && farms.getHorseShare() == 0);
        play(g, 1);
        near("Industry's steam share is the unfitted bakeries' grid draw over all its buildings'", industry.getSteamShare(), steam, 1e-9);
        near("the farms' horse share is the unfitted farms' land over all their land", farms.getHorseShare(), horses, 1e-9);
        check("no other sector has a steam share", maxSteamExcept(g, industry.key()) == 0, "largest " + maxSteamExcept(g, industry.key()));
        check("...and Haulage's horses are not set by a farm", g.getSectors().freight().haulage().getHorseShare() == 0);
        check("EquipmentForms says which it drives", g.getEquipmentForms().driven().equals(java.util.Set.of(
                "FARM_HORSES:" + farms.key(), "STEAM:" + industry.key())), g.getEquipmentForms().driven().toString());

        Haulage haulage = c.getSectors().freight().haulage();
        BuildingsStacks depot = stack(c, "Truck Depot");
        int q = depot.getQuantity();
        check("fixture: depots stand in the cart town", q >= 2, "quantity " + q);
        depot.setUnfitted(TRUCK, 1);
        double carted = cartShare(c);
        check("fixture: part of the depots' room is unfitted, not all", carted > 0 && carted < 1, "share " + carted);
        step(c, 1);
        near("Haulage's horse share is the unfitted depots' truck room over all the depots'", haulage.getHorseShare(), carted, 1e-9);
        check("...and no sector in the cart town has a steam share", maxSteam(c) == 0);
    }

    /* ============================ 4. back to 0 ============================ */

    static void sectionFour(Game g, Game c) {
        out.println("\n--- 4. back to 0: with the counts gone the shares driven are 0 the next month, and stay 0 ---");
        stack(g, "Bakery").setUnfitted(DRIVE, 0);
        stack(g, "Industrial Bakery").setUnfitted(DRIVE, 0);
        stack(g, "Grain Farm").setUnfitted(TRACTOR, 0);
        stack(c, "Truck Depot").setUnfitted(TRUCK, 0);
        check("fixture: no count is left in either city", !anyUnfitted(g) && !anyUnfitted(c));
        check("fixture: the shares are still up before the month", g.getSectors().industry().getSteamShare() > 0
                && g.getSectors().agriculture().getHorseShare() > 0 && c.getSectors().freight().haulage().getHorseShare() > 0);
        play(g, 1);
        step(c, 1);
        check("Industry's steam share is 0 again", g.getSectors().industry().getSteamShare() == 0);
        check("the farms' horse share is 0 again", g.getSectors().agriculture().getHorseShare() == 0);
        check("Haulage's horse share is 0 again", c.getSectors().freight().haulage().getHorseShare() == 0);
        check("EquipmentForms drives none", g.getEquipmentForms().driven().isEmpty() && c.getEquipmentForms().driven().isEmpty());
        play(g, 1);
        step(c, 1);
        check("a month later every share is still 0", maxSteam(g) == 0 && g.getSectors().agriculture().getHorseShare() == 0
                && maxSteam(c) == 0 && c.getSectors().freight().haulage().getHorseShare() == 0);
    }

    /* ============================ 5. a share set by hand stands ============================ */

    static void sectionFive(Game g) {
        out.println("\n--- 5. a share set by hand stands: with no count anywhere, EquipmentForms touches nothing ---");
        Sector industry = g.getSectors().industry();
        Agriculture farms = g.getSectors().agriculture();
        Haulage haulage = g.getSectors().freight().haulage();
        check("fixture: no count stands in the city", !anyUnfitted(g));
        industry.setSteamShare(0.5);
        farms.setHorseShare(0.25);
        haulage.setHorseShare(0.75);
        play(g, 1);
        check("a steam share of 0.5 set by hand is 0.5 after a month", industry.getSteamShare() == 0.5, "is " + industry.getSteamShare());
        check("the farms' horse share of 0.25 set by hand is 0.25 after it", farms.getHorseShare() == 0.25, "is " + farms.getHorseShare());
        check("Haulage's horse share of 0.75 set by hand is 0.75 after it", haulage.getHorseShare() == 0.75, "is " + haulage.getHorseShare());
        check("EquipmentForms has driven none of them", g.getEquipmentForms().driven().isEmpty());
        industry.setSteamShare(0);
        farms.setHorseShare(0);
        haulage.setHorseShare(0);
    }

    /* ============================ 6. the count follows the stack ============================ */

    static void sectionSix(Game g) {
        out.println("\n--- 6. the count follows the stack: never more than stand, scrapped first, and a building finished leaves it ---");
        BuildingsTemplate bakery = g.getBuildingManager().getTemplateByName("Bakery");
        BuildingsStacks s = new BuildingsStacks(bakery, 5);
        s.setUnfitted(DRIVE, 9);
        check("a count set above what stands is what stands", s.getUnfitted(DRIVE) == 5 && s.hasUnfitted(), "is " + s.getUnfitted(DRIVE));
        s.setUnfitted(DRIVE, -3);
        check("a count set below 0 takes the piece off the count", s.getUnfitted(DRIVE) == 0 && !s.hasUnfitted() && s.getUnfittedView().isEmpty());
        s.setUnfitted(DRIVE, 2);
        s.setUnfitted("other", 3);
        check("two pieces are kept apart, sorted by their ids", s.getUnfitted(DRIVE) == 2 && s.getUnfitted("other") == 3
                && new ArrayList<>(s.getUnfittedView().keySet()).equals(List.of(DRIVE, "other")));
        check("the view cannot be changed from outside", throwsOnPut(s));
        s.setUnfitted("other", 0);

        s.setUnfitted(DRIVE, 5);
        s.removeQuantity(2);
        check("scrapping two of five, all unfitted, leaves three and three", s.getQuantity() == 3 && s.getUnfitted(DRIVE) == 3,
                s.getQuantity() + "/" + s.getUnfitted(DRIVE));
        s.removeQuantity(1);
        check("...and scrapping one more leaves two and two: the count is never above what stands", s.getQuantity() == 2 && s.getUnfitted(DRIVE) == 2,
                s.getQuantity() + "/" + s.getUnfitted(DRIVE));

        BuildingsStacks t = new BuildingsStacks(bakery, 5);
        t.setUnfitted(DRIVE, 2);
        t.removeQuantity(1);
        check("with two of five unfitted, one scrapped is an unfitted one: four stand and one of them is unfitted",
                t.getQuantity() == 4 && t.getUnfitted(DRIVE) == 1, t.getQuantity() + "/" + t.getUnfitted(DRIVE));
        t.removeQuantity(10);
        check("scrapping them all (more than stand) leaves none standing and none counted", t.getQuantity() == 0 && !t.hasUnfitted());

        BuildingsStacks f = new BuildingsStacks(bakery, 3);
        f.setUnfitted(DRIVE, 2);
        f.increaseQuantity(2);
        check("a building added by increaseQuantity leaves the count as it was", f.getQuantity() == 5 && f.getUnfitted(DRIVE) == 2);
        f.addQuantity(1);
        check("...and by addQuantity", f.getQuantity() == 6 && f.getUnfitted(DRIVE) == 2);
        f.startConstruction(1);
        quietly(() -> f.advanceConstruction(1e9));
        check("...and a building finished on site comes fitted: the count is as it was", f.getLastFinished() == 1 && f.getQuantity() == 7
                && f.getUnfitted(DRIVE) == 2, f.getQuantity() + "/" + f.getUnfitted(DRIVE));

        // ...and through the city's own paths: the order the player or the investor places, and a retirement.
        BuildingsStacks live = stack(g, "Bakery");
        int before = live.getQuantity();
        live.setUnfitted(DRIVE, 1);
        quietly(() -> g.buildStack(bakery, 1, true));
        check("a Bakery bought into the city (buildStack) leaves the stack's count as it was", live.getQuantity() == before + 1 && live.getUnfitted(DRIVE) == 1);
        int scrapped = g.getBuildingManager().retire(bakery, live.getQuantity());
        check("every Bakery retired by the city takes the count with it", scrapped == before + 1 && live.getQuantity() == 0 && !live.hasUnfitted());
        quietly(() -> g.buildStack(bakery, before, true));
    }

    /** A city saved to slot 1 of its own scratch folder, quietly (never a real slot: the game was made with GameFiles.scratch()). */
    static GameFiles.Result save(Game g, String name) {
        GameFiles.Result[] result = new GameFiles.Result[1];
        quietly(() -> result[0] = g.saveGame(1, name));
        return result[0];
    }

    static boolean throwsOnPut(BuildingsStacks s) {
        try {
            s.getUnfittedView().put("x", 1);
            return false;
        } catch (UnsupportedOperationException e) {
            return true;
        }
    }

    /* ============================ 7. the save ============================ */

    static void sectionSeven(Game g, Game none) {
        out.println("\n--- 7. the save: none unfitted writes no key; the counts are saved by template id and drive the forms again after a load ---");
        GameFiles files = none.getGameFiles();
        GameFiles.Result wroteNone = save(none, "equipment none");
        String noneJson = "";
        try {
            noneJson = Files.readString(files.saveFile(1));
        } catch (Exception e) {
            noneJson = "unreadable: " + e;
        }
        check("fixture: the city with none unfitted saved", wroteNone.ok, wroteNone.message());
        check("a save with no building unfitted has no unfittedById key", !noneJson.contains("unfittedById") && !noneJson.startsWith("unreadable"),
                noneJson.substring(0, Math.min(80, noneJson.length())));

        // The counts: one of two Bakeries, the Industrial Bakery stack and the Grain Farm stack whole; then a month, so the shares stand as the city has them.
        stack(g, "Bakery").setUnfitted(DRIVE, 1);
        stack(g, "Industrial Bakery").setUnfitted(DRIVE, stack(g, "Industrial Bakery").getQuantity());
        stack(g, "Grain Farm").setUnfitted(TRACTOR, stack(g, "Grain Farm").getQuantity());
        play(g, 1);
        // ...and the forms said once more from the stacks as they stand now, so that the saved city's shares are its counts' and not the month's top's.
        g.getEquipmentForms().apply(g);
        check("fixture: the counts stand after the month and the forms are up", anyUnfitted(g) && g.getSectors().industry().getSteamShare() > 0
                && g.getSectors().agriculture().getHorseShare() > 0);
        Map<Integer, Map<String, Integer>> saved = g.getBuildingManager().getUnfittedById();
        check("the save's view is each template's id and its counts, only where a stack has any", saved.size() == 3
                && saved.get(stack(g, "Bakery").getBuilding().getId()).equals(Map.of(DRIVE, 1))
                && saved.get(stack(g, "Industrial Bakery").getBuilding().getId()).equals(Map.of(DRIVE, stack(g, "Industrial Bakery").getQuantity()))
                && saved.get(stack(g, "Grain Farm").getBuilding().getId()).equals(Map.of(TRACTOR, stack(g, "Grain Farm").getQuantity())), saved.toString());

        GameFiles.Result wrote = save(g, "equipment counted");
        check("the city with counts saved", wrote.ok, wrote.message());
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(1));
        check("...and loaded without a failure", back.getLoadFailure() == null, String.valueOf(back.getLoadFailure()));
        check("the loaded city has the same counts by template id", back.getBuildingManager().getUnfittedById().equals(saved),
                back.getBuildingManager().getUnfittedById().toString());
        boolean sameStacks = true;
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            BuildingsStacks r = back.getBuildingManager().getStack(back.getBuildingManager().getTemplate(s.getBuilding().getId()));
            sameStacks &= r != null && r.getQuantity() == s.getQuantity() && r.getUnfittedView().equals(s.getUnfittedView());
        }
        check("...and every stack stands as many and counts as many unfitted as the saved city's", sameStacks);
        check("before any month is played the loaded city's steam share is the saved city's, and what the counts say",
                back.getSectors().industry().getSteamShare() == g.getSectors().industry().getSteamShare()
                        && Math.abs(back.getSectors().industry().getSteamShare() - steamShare(back, back.getSectors().industry().key())) <= 1e-9
                        && back.getSectors().industry().getSteamShare() > 0,
                back.getSectors().industry().getSteamShare() + " vs " + g.getSectors().industry().getSteamShare());
        check("...and the farms' horse share too", back.getSectors().agriculture().getHorseShare() == g.getSectors().agriculture().getHorseShare()
                && Math.abs(back.getSectors().agriculture().getHorseShare() - farmShare(back)) <= 1e-9
                && back.getSectors().agriculture().getHorseShare() > 0);
        check("...with no other sector's steam set, nor Haulage's horses", maxSteamExcept(back, back.getSectors().industry().key()) == 0
                && back.getSectors().freight().haulage().getHorseShare() == 0);

        // ...and the city that saved no key loads as every building fitted, the way an older save does (no format bump: Gson leaves the key alone).
        Game fitted = new Game(files);
        quietly(() -> fitted.loadGameSave(1));
        check("a save with no key loads without a failure, as every building fitted, and every form at 0", fitted.getLoadFailure() == null
                && fitted.getBuildingManager().getUnfittedById().isEmpty() && !anyUnfitted(fitted) && maxSteam(fitted) == 0
                && fitted.getSectors().agriculture().getHorseShare() == 0 && fitted.getSectors().freight().haulage().getHorseShare() == 0,
                String.valueOf(fitted.getLoadFailure()));
    }
}
