package ham.citybuildersim;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ham.citybuildersim.sectors.Agriculture;
import ham.citybuildersim.sectors.Haulage;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Equipment as data, the count of buildings still without it on each stack, R0c's 1900 forms set from that count, and (slice B) what a piece
 * would earn and cost and whether it pays, read on demand.
 *
 * WHAT THIS HAS TO PROVE (R1 slice A, 2026-11-05; docs/queue/R1-notes.md calls 2 to 9; research-design 2.1-2.2). R0c built the steam, the
 * farms' horses and the carts' horses as shares of a sector that nothing sets; R1 fits equipment building by building, and "the forms read one
 * number" (R0c's call 3). This part is the state, off: a piece of equipment is data on a template, a stack counts the buildings without it, and
 * EquipmentForms sets the shares from the counts where the Policy hears the month. In every played city no building is unfitted, so nothing
 * moves. What is checkable:
 *
 *   1. THE DATA IS READ: every template the notes name carries its piece, its since, ends, buys, work and life read back from buildings.json
 *      (the file read here with Gson, its figures not typed twice), no other template carries one but those whose recipes need it (R1c's pieces
 *      end no form: each is its template's recipe piece, in the file and once read), the rules the figures were struck by hold
 *      on the template's own electricity, construction points, land and truck room, the tractor's code is known from PE14's month (June 1917, R2) and
 *      not before, the drive is known from 1900; and a bad entry in the file (no id, a second of an id, an unknown form or good, no since, a life of 0) is reported and
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
 *      same counts and, before any month is played, the shares the counts set, equal to the saved city's (no hand says the forms after the
 *      saved city's month: the month does, after its sites advance, R1b), and one more month on each leaves the shares equal
 *
 * R1 slice B (EquipmentFit, research-design 2.2 steps 1 to 4: what a piece adds a month, what it costs, whether it pays over its life and
 * passes the money gate; each piece's verdict, read on demand, never in the month):
 *
 *   8. THE RULE'S ARITHMETIC: the annuity factor is the months at a rate of 0 and the closed form (and the sum, and BuildAdvice's own factor)
 *      at a positive one; a gain just over cost / factor pays and just under does not, a gain just over the hurdle passes the gate and just
 *      under does not, each with the other step comfortably met; a gain of nothing never pays; FITTED and NOT_KNOWN price nothing; the cost is
 *      the goods at their prices and the work at the point's; the steam gain is the coal less the power, and crosses 0 at the price where
 *      they are equal
 *   9. THE STEAM VERDICT AT THE CITY'S PRICES: with a Bakery unfitted, the verdict's gain and cost are the pure core's at the prices the
 *      model's getters give, its rate and hurdle the debt manager's and the investment's on the whole cost, its kind what judge() says; a
 *      coal price just over the break-even (solved from the verdict's own figures) pays and just under does not, in the core and in the city
 *  10. THE TRUCK'S, THE TRACTOR'S AND THE RANK: a depot unfitted has a finite gain from Haulage's teams less its trucks' running costs (the
 *      trucks' capital left out) and a cost of its trucks at the city's price; an unfitted Grain Farm's tractor is NOT_KNOWN; a stack with none
 *      unfitted is FITTED; rank() lists the pieces that pay and no other, best earnings on cost first, ties by template then piece id in any
 *      order they come, and a sector with none unfitted ranks none
 *  11. THE VERDICT CHANGES NOTHING: twin cities with the same counts, one asked every verdict and every rank every month for six months,
 *      are equal to the bit in the treasury, every sector's books and rates, the trucks, the shares and the counts
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
        sectionEight(played);
        Game steam = Forms1900Check.town("equipment-fit-steam");
        sectionNine(steam);
        Game fitCarts = cartTown("equipment-fit-carts");
        sectionTen(steam, fitCarts);
        sectionEleven();

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

        // R1c: a piece may end no form (it enables a recipe), so "ends" may be missing from the file; the pieces that DO end one are the notes' whole.
        int expectedPieces = DRIVES.length + 1 + FARMS.length, carried = 0, filePieces = 0, fileForms = 0, mismatched = 0;
        String firstMismatch = null;
        for (JsonObject o : file.values()) {
            if (!o.has("equipment")) continue;
            filePieces += o.getAsJsonArray("equipment").size();
            for (JsonElement e : o.getAsJsonArray("equipment")) if (e.getAsJsonObject().has("ends")) fileForms++;
        }
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
                        && p.getEnds() == (j.has("ends") ? Equipment.Form.valueOf(j.get("ends").getAsString()) : null)
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
        check("fixture: the file carries equipment, and the pieces that end a form are the notes' whole of them (R1c's recipe pieces end none)",
                fileForms == expectedPieces && filePieces >= fileForms, fileForms + " end a form in the file (" + filePieces + " pieces), " + expectedPieces + " named");
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

        // R1 held that equipment stands only where a 1900 form does; R1c changed it by design (R1-notes call 2: the other rows come "with their 1900
        // recipes in R1c"): a template may carry the piece its recipes need, which ends no form. So the rule is asked of the model and of the file.
        int other = 0;
        String firstOther = null;
        List<String> named = new ArrayList<>(List.of(DRIVES));
        named.addAll(List.of(FARMS));
        named.add("Truck Depot");
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (t.getEquipment().isEmpty() || named.contains(t.getName()) || t.recipePieceId() != null) continue;
            other++;
            if (firstOther == null) firstOther = t.getName();
        }
        check("no other template carries a piece than the drive's, the depot's, the farms' and the ones whose recipes need one", other == 0, other + " do, " + firstOther + " first");

        boolean everyEnds = true;
        String firstEnds = null;
        for (BuildingsTemplate t : bm.getTemplates()) {
            for (Equipment p : t.getEquipment()) {
                boolean ok = p.getEnds() != null || p.getId().equals(t.recipePieceId());
                everyEnds &= ok;
                if (!ok && firstEnds == null) firstEnds = t.getName() + "/" + p.getId();
            }
        }
        check("every piece ends one of the three forms or is its template's recipe piece, the one its recipes need", everyEnds, firstEnds);

        int formless = 0, formlessWanted = 0;
        String firstFormless = null;
        for (JsonObject o : file.values()) {
            if (!o.has("equipment")) continue;
            for (JsonElement e : o.getAsJsonArray("equipment")) {
                JsonObject piece = e.getAsJsonObject();
                if (piece.has("ends")) continue;
                formless++;
                boolean wanted = false;
                if (o.has("recipes")) {
                    for (JsonElement r : o.getAsJsonArray("recipes")) {
                        JsonObject recipe = r.getAsJsonObject();
                        wanted |= recipe.has("needsEquipment") && recipe.get("needsEquipment").getAsString().equals(piece.get("id").getAsString());
                    }
                }
                BuildingsTemplate t = bm.getTemplate(o.get("id").getAsInt());
                wanted &= t != null && piece.get("id").getAsString().equals(t.recipePieceId());
                if (wanted) formlessWanted++;
                else if (firstFormless == null) firstFormless = o.get("name").getAsString() + "/" + piece.get("id").getAsString();
            }
        }
        check("...and the file's pieces that end none (" + formless + ") are each needed by a recipe of their building, and are the template's recipe piece once read",
                formless > 0 && formlessWanted == formless, formlessWanted + " of " + formless + ", first " + firstFormless);

        BuildingsTemplate grainFarm = bm.getTemplateByName("Grain Farm"), bakery = bm.getTemplateByName("Bakery");
        Equipment tractor = grainFarm == null ? null : grainFarm.getEquipment(TRACTOR);
        Equipment drive = bakery == null ? null : bakery.getEquipment(DRIVE);
        boolean tractorFromPe14 = tractor != null;
        for (int y = 1800; y <= 2200 && tractor != null; y++) tractorFromPe14 &= tractor.knownIn(y) == (y >= 1918);
        int pe14 = tractor == null ? 0 : tractor.knownFrom();
        check("the tractor's code is not a year and is known from its entry's month, PE14's June 1917 (R2), and not the month before: by no January up to 1917's, by every one from 1918's",
                tractor != null && !tractor.isYear() && pe14 == PolicyDates.monthOf(1917, 6) && pe14 == ResearchCatalog.get().entry("PE14").worldMonth()
                        && tractor.knownAt(pe14) && !tractor.knownAt(pe14 - 1) && tractorFromPe14);
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
        // The counts as set are kept: the month may put up another building on a stack (the investors build), which comes fitted and moves the
        // quantity, not the count.
        final int industrialSet = stack(g, "Industrial Bakery").getQuantity(), grainSet = stack(g, "Grain Farm").getQuantity();
        stack(g, "Bakery").setUnfitted(DRIVE, 1);
        stack(g, "Industrial Bakery").setUnfitted(DRIVE, industrialSet);
        stack(g, "Grain Farm").setUnfitted(TRACTOR, grainSet);
        // ...and the shares at rest are the counts' as the month ended on them: the month says the forms once more after its sites advance
        // (SimulationEngine.simulateMonth(), R1b), so that the saved city's shares are its counts' and not the month's top's, with no hand to say it here.
        play(g, 1);
        check("fixture: the counts stand after the month and the forms are up", anyUnfitted(g) && g.getSectors().industry().getSteamShare() > 0
                && g.getSectors().agriculture().getHorseShare() > 0);
        Map<Integer, Map<String, Integer>> saved = g.getBuildingManager().getUnfittedById();
        check("the save's view is each template's id and its counts, only where a stack has any", saved.size() == 3
                && saved.get(stack(g, "Bakery").getBuilding().getId()).equals(Map.of(DRIVE, 1))
                && saved.get(stack(g, "Industrial Bakery").getBuilding().getId()).equals(Map.of(DRIVE, industrialSet))
                && saved.get(stack(g, "Grain Farm").getBuilding().getId()).equals(Map.of(TRACTOR, grainSet)), saved.toString());

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
        // ...and one more month on each (R1b): the saved city and the loaded one rest in the same shares after it, as before it.
        play(g, 1);
        play(back, 1);
        check("one more month on the saved city and on the loaded one leaves the same steam share and the same horse shares",
                back.getSectors().industry().getSteamShare() == g.getSectors().industry().getSteamShare()
                        && back.getSectors().agriculture().getHorseShare() == g.getSectors().agriculture().getHorseShare()
                        && back.getSectors().freight().haulage().getHorseShare() == g.getSectors().freight().haulage().getHorseShare(),
                back.getSectors().industry().getSteamShare() + " vs " + g.getSectors().industry().getSteamShare() + " / "
                        + back.getSectors().agriculture().getHorseShare() + " vs " + g.getSectors().agriculture().getHorseShare());

        // ...and the city that saved no key loads as every building fitted, the way an older save does (no format bump: Gson leaves the key alone).
        Game fitted = new Game(files);
        quietly(() -> fitted.loadGameSave(1));
        check("a save with no key loads without a failure, as every building fitted, and every form at 0", fitted.getLoadFailure() == null
                && fitted.getBuildingManager().getUnfittedById().isEmpty() && !anyUnfitted(fitted) && maxSteam(fitted) == 0
                && fitted.getSectors().agriculture().getHorseShare() == 0 && fitted.getSectors().freight().haulage().getHorseShare() == 0,
                String.valueOf(fitted.getLoadFailure()));
    }

    /* ================== R1 slice B: the payback rule and each piece's verdict (EquipmentFit) ================== */

    /** The city's price of a good, as the planners read it. */
    static double cityPrice(Game g, Good good) { return SpreadPlanner.cityValue(g.getMarkets().get(good)); }

    /** A piece for a pure-core fixture: it buys what it is given, its work and its life are the figures asked about. */
    static Equipment piece(String since, Equipment.Form ends, Map<Good, Double> buys, double work, int life) {
        return new Equipment("test-piece", "Test piece", since, ends, buys, work, life);
    }

    /** The year a game's month is in. */
    static int yearOf(Game g) { return CityCalendar.yearOf(g.getMonth()); }

    /**
     * The game month of a calendar year's January, which the verdict is asked by since R2 (the figures API takes the month): PolicyDates.monthOf(year, 1)
     * from 1900, and a month before the first for a year before the start (Equipment.januaryOf(): PolicyDates clamps those to month 1).
     */
    static int jan(int year) { return Equipment.januaryOf(year); }

    /* ============================ 8. the rule's arithmetic ============================ */

    static void sectionEight(Game g) {
        out.println("\n--- 8. the rule's arithmetic: the annuity, both steps either side of the line, a gain of nothing, the cost and the steam gain ---");
        check("the annuity factor at a rate of 0 is the months, and for a rate less than 0 or not a number or not finite too",
                EquipmentFit.annuityFactor(300, 0) == 300 && EquipmentFit.annuityFactor(120, -0.05) == 120
                        && EquipmentFit.annuityFactor(60, Double.NaN) == 60 && EquipmentFit.annuityFactor(60, Double.POSITIVE_INFINITY) == 60);
        check("...and no months is no factor", EquipmentFit.annuityFactor(0, 0.06) == 0 && EquipmentFit.annuityFactor(-5, 0.06) == 0);
        boolean closed = true, summed = true;
        String first = null;
        for (int n : new int[]{1, 12, 120, 300, 1200}) {
            for (double r : new double[]{0.01, 0.05, 0.12, 0.40}) {
                double i = r / 12, sum = 0;
                for (int t = 1; t <= n; t++) sum += Math.pow(1 + i, -t);
                double f = EquipmentFit.annuityFactor(n, r);
                boolean c = Math.abs(f - (1 - Math.pow(1 + i, -n)) / i) <= 1e-12 * Math.max(1, f);
                boolean s = Math.abs(f - sum) <= 1e-9 * Math.max(1, f);
                closed &= c;
                summed &= s;
                if ((!c || !s) && first == null) first = n + " months at " + r + ": " + f + " vs " + (1 - Math.pow(1 + i, -n)) / i + " / " + sum;
            }
        }
        check("at a positive rate it is the closed form (1 - (1 + i)^-n) / i, i the rate over 12, for lives of 1 to 1200 months", closed, first);
        check("...and the sum of 1 / (1 + i)^t over the months", summed, first);
        check("...a month is worth 1 / (1 + i); more months are worth more and a higher rate less, but never as much as the months themselves",
                Math.abs(EquipmentFit.annuityFactor(1, 0.12) - 1 / 1.01) <= 1e-12 && EquipmentFit.annuityFactor(121, 0.05) > EquipmentFit.annuityFactor(120, 0.05)
                        && EquipmentFit.annuityFactor(120, 0.10) < EquipmentFit.annuityFactor(120, 0.05) && EquipmentFit.annuityFactor(120, 0.05) < 120);
        double lifeRate = BuildAdvice.lifeRate(g);
        check("...and it is BuildAdvice's own factor: over LIFE_MONTHS at the rate lifeRate() gives, to the bit",
                lifeRate > 0 && EquipmentFit.annuityFactor(BuildAdvice.LIFE_MONTHS, lifeRate) == BuildAdvice.lifeFactor(g),
                EquipmentFit.annuityFactor(BuildAdvice.LIFE_MONTHS, lifeRate) + " vs " + BuildAdvice.lifeFactor(g));

        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate bakery = bm.getTemplateByName("Bakery"), farm = bm.getTemplateByName("Grain Farm");
        Equipment drive = bakery.getEquipment(DRIVE), tractor = farm.getEquipment(TRACTOR);
        final int life = drive.getLifeMonths();

        // Step 3 alone: no hurdle, a gain either side of cost / factor.
        double r = 0.06, cost = 100;
        double factor = EquipmentFit.annuityFactor(life, r), line = cost / factor;
        EquipmentFit.Verdict over = EquipmentFit.judge(drive, bakery, jan(1900), 1, line * (1 + 1e-6), cost, r, 0);
        EquipmentFit.Verdict under = EquipmentFit.judge(drive, bakery, jan(1900), 1, line * (1 - 1e-6), cost, r, 0);
        check("a gain a millionth over cost / factor pays: over its life it earns back its cost", over.kind() == EquipmentFit.Kind.PAYS && over.earnsBack() >= cost,
                over.toString());
        check("...a millionth under does not: it earns back less than its cost, with no hurdle in the way",
                under.kind() == EquipmentFit.Kind.NOT_YET && under.earnsBack() < cost && under.gain() > under.hurdle(), under.toString());
        check("the verdict carries the figures it was struck on: ids, life, rate, factor, earns back (gain x factor), earnings on cost (gain over cost)",
                over.templateId() == bakery.getId() && over.pieceId().equals(DRIVE) && over.lifeMonths() == life && over.realRate() == r && over.factor() == factor
                        && over.earnsBack() == over.gain() * factor && over.earningsOnCost() == over.gain() / cost && over.cost() == cost && over.hurdle() == 0);

        // Step 4 alone: the cost is a unit and the hurdle ten, so the life earns its cost back many times over; a gain either side of the hurdle.
        double hurdle = 10;
        EquipmentFit.Verdict gateOver = EquipmentFit.judge(drive, bakery, jan(1900), 1, hurdle * (1 + 1e-6), 1, r, hurdle);
        EquipmentFit.Verdict gateUnder = EquipmentFit.judge(drive, bakery, jan(1900), 1, hurdle * (1 - 1e-6), 1, r, hurdle);
        check("a gain a millionth over the hurdle passes the gate", gateOver.kind() == EquipmentFit.Kind.PAYS, gateOver.toString());
        check("...a millionth under does not, though it earns back many times its cost over its life: the gate alone decides",
                gateUnder.kind() == EquipmentFit.Kind.NOT_YET && gateUnder.earnsBack() > 100 * gateUnder.cost(), gateUnder.toString());
        double exactCost = hurdle * factor;
        check("a gain exactly at both lines pays (at least: g x factor >= C and g >= the hurdle)",
                EquipmentFit.judge(drive, bakery, jan(1900), 1, hurdle, exactCost, r, hurdle).kind() == EquipmentFit.Kind.PAYS);

        boolean never = true;
        for (double gain : new double[]{0, -1e-9, -50, Double.NEGATIVE_INFINITY, Double.NaN}) {
            for (double c : new double[]{0, 1, 100}) {
                never &= EquipmentFit.judge(drive, bakery, jan(1900), 1, gain, c, r, Double.NEGATIVE_INFINITY).kind() == EquipmentFit.Kind.NOT_YET
                        && EquipmentFit.judge(drive, bakery, jan(1900), 1, gain, c, 0, 0).kind() == EquipmentFit.Kind.NOT_YET;
            }
        }
        check("a gain of nothing, less than nothing or not a number never pays, whatever the cost, the hurdle or the rate", never);
        check("a cost that is not a number never pays",
                EquipmentFit.judge(drive, bakery, jan(1900), 1, 50, Double.NaN, r, 0).kind() == EquipmentFit.Kind.NOT_YET);
        EquipmentFit.Verdict free = EquipmentFit.judge(drive, bakery, jan(1900), 1, 0.5, 0, r, 0);
        check("a piece that costs nothing and adds something pays, its earnings on cost infinite; one that adds nothing earns 0 on it",
                free.kind() == EquipmentFit.Kind.PAYS && free.earningsOnCost() == Double.POSITIVE_INFINITY
                        && EquipmentFit.judge(drive, bakery, jan(1900), 1, 0, 0, r, 0).earningsOnCost() == 0);
        check("at a rate of 0 a life is worth its months: g x the life is what it earns back, and a hurdle of 0 asks nothing more",
                EquipmentFit.judge(drive, bakery, jan(1900), 1, 2, 2.0 * life, 0, 0).kind() == EquipmentFit.Kind.PAYS
                        && EquipmentFit.judge(drive, bakery, jan(1900), 1, 2, 2.0 * life + 1e-6, 0, 0).kind() == EquipmentFit.Kind.NOT_YET);

        // The two verdicts that price nothing.
        EquipmentFit.Verdict fitted = EquipmentFit.judge(drive, bakery, jan(1900), 0, 99, 1, r, 0);
        EquipmentFit.Verdict unknown = EquipmentFit.judge(drive, bakery, jan(1899), 3, 99, 1, r, 0);
        check("no building unfitted is FITTED however much it would add, and prices nothing",
                fitted.kind() == EquipmentFit.Kind.FITTED && Double.isNaN(fitted.gain()) && Double.isNaN(fitted.cost()) && Double.isNaN(fitted.earningsOnCost())
                        && fitted.lifeMonths() == life && fitted.pieceId().equals(DRIVE) && fitted.templateId() == bakery.getId(), fitted.toString());
        check("...and FITTED comes before NOT_KNOWN: a fitted piece before its year is fitted",
                EquipmentFit.judge(drive, bakery, jan(1850), 0, 99, 1, r, 0).kind() == EquipmentFit.Kind.FITTED);
        check("a piece not yet known at the city's month is NOT_KNOWN, in January 1899 and not in January 1900; and prices nothing",
                unknown.kind() == EquipmentFit.Kind.NOT_KNOWN && Double.isNaN(unknown.gain()) && Double.isNaN(unknown.cost())
                        && EquipmentFit.judge(drive, bakery, jan(1900), 3, 99, 1, r, 0).kind() == EquipmentFit.Kind.PAYS, unknown.toString());
        int pe14 = tractor.knownFrom();
        boolean tractorFromPe14 = pe14 == PolicyDates.monthOf(1917, 6);
        for (int m : new int[]{1, jan(1917), pe14 - 1}) tractorFromPe14 &= EquipmentFit.judge(tractor, farm, m, 2, 1e9, 1, 0, 0).kind() == EquipmentFit.Kind.NOT_KNOWN;
        for (int m : new int[]{pe14, jan(1918), jan(1950), jan(2200)}) tractorFromPe14 &= EquipmentFit.judge(tractor, farm, m, 2, 1e9, 1, 0, 0).kind() == EquipmentFit.Kind.PAYS;
        check("the tractor, whose since is the code PE14, is NOT_KNOWN however much it would add up to the month before PE14's, June 1917, and from that month it is judged like any other piece (R2)",
                tractorFromPe14);

        // Step 2: the goods at their prices and the work at a point's.
        Map<Good, Double> buys = new java.util.EnumMap<>(Good.class);
        buys.put(Good.MACHINERY, 2.0);
        buys.put(Good.TRUCKS, 3.0);
        Equipment bought = piece("1900", Equipment.Form.STEAM, buys, 10, 100);
        Map<Good, Double> prices = Map.of(Good.MACHINERY, 5.0, Good.TRUCKS, 7.0);
        near("the cost is each good's units at its price, plus the work at the point's price", EquipmentFit.cost(bought, prices::get, 0.5), 2 * 5.0 + 3 * 7.0 + 10 * 0.5, 1e-12);
        check("...nothing bought and no work costs nothing, and a price that is not a number makes the cost one",
                EquipmentFit.cost(piece("1900", Equipment.Form.STEAM, Map.of(), 0, 10), x -> Double.NaN, 3) == 0
                        && Double.isNaN(EquipmentFit.cost(bought, x -> Double.NaN, 0.5)));

        // Step 1, the steam: the coal its engine would burn at nameplate less the grid power it would draw instead.
        double units = bakery.powerBought();
        double coalTonnes = units * HydroSites.KWH_PER_UNIT * Sector.STEAM_COAL_KG_A_KWH / 1000;
        near("a building's steam coal a month is its draw in kWh at the engine's kilogrammes a kWh, in tonnes", EquipmentFit.steamCoalTonnes(bakery), coalTonnes, 1e-12);
        double power = 0.37, coalEven = power * units / coalTonnes;
        near("the steam gain is the coal at its price less the power at its price", EquipmentFit.steamGain(bakery, 0.91, power), coalTonnes * 0.91 - units * power, 1e-12);
        check("...it is nothing at the price of coal where the two cost the same, more above it and less below",
                Math.abs(EquipmentFit.steamGain(bakery, coalEven, power)) <= 1e-9 && EquipmentFit.steamGain(bakery, coalEven * 1.01, power) > 0
                        && EquipmentFit.steamGain(bakery, coalEven * 0.99, power) < 0);
        BuildingsTemplate house = bm.getTemplateByName("House");
        check("...a building no utility bills (a House) burns no coal and draws no billed power: it adds nothing at any price",
                EquipmentFit.steamCoalTonnes(house) == 0 && EquipmentFit.steamGain(house, 5, 5) == 0);

        // Step 1, the carts: tonnes at the teams' price less the trucks'.
        double room = 100;
        near("the carts' gain is the room's tonnes a month at the teams' price a tonne less the trucks' running price",
                EquipmentFit.cartGain(room, 0.0128, 0.0001), room * Haulage.TONNES_A_TRUCK_MONTH * (0.0128 - 0.0001), 1e-12);
        check("...less than nothing where the trucks cost more to run than the teams", EquipmentFit.cartGain(room, 0.0001, 0.0128) < 0);
    }

    /* ============================ 9. the steam verdict at the city's prices ============================ */

    /** Moves the city's price of coal to `target`, by the world's prices on its market: the price planners read (cityValue()) is affine in them, found from two probes and refined; false if it will not move. */
    static boolean priceCoalAt(Game g, double target) {
        return priceAt(g, Good.THERMAL_COAL, target);
    }

    /** Moves the city's price of any good to `target`, the same way (RecipeCheck moves the crops' and the bread's). */
    static boolean priceAt(Game g, Good good, double target) {
        GoodsMarket m = g.getMarkets().get(good);
        m.setWorldPrices(0.05, 0.05);
        double a = SpreadPlanner.cityValue(m);
        m.setWorldPrices(0.10, 0.10);
        double b = SpreadPlanner.cityValue(m);
        if (!(b > a)) return false;
        double p = 0.05 + (target - a) / (b - a) * 0.05;
        for (int i = 0; i < 6; i++) {
            m.setWorldPrices(p, p);
            double v = SpreadPlanner.cityValue(m);
            if (Math.abs(v - target) <= 1e-12 * Math.abs(target)) return true;
            p += (target - v) / (b - a) * 0.05;
        }
        return Math.abs(SpreadPlanner.cityValue(m) - target) <= 1e-9 * Math.abs(target);
    }

    /** The gain a verdict's piece needs to pay: the greater of the line (cost over factor) and the hurdle. */
    static double gainNeeded(EquipmentFit.Verdict v) { return Math.max(v.cost() / v.factor(), v.hurdle()); }

    /** The coal price at which a drive on this building just pays, from a verdict's own figures. */
    static double breakEvenCoal(Game g, BuildingsTemplate t, EquipmentFit.Verdict v) {
        return (gainNeeded(v) + t.powerBought() * g.getEconomyManager().getPricePerWatt()) / EquipmentFit.steamCoalTonnes(t);
    }

    static void sectionNine(Game g) {
        out.println("\n--- 9. the steam verdict at the city's prices: the gain, the cost, the rate and the hurdle from the model's getters, and the coal price that pays ---");
        Sector industry = g.getSectors().industry();
        BuildingsStacks bakery = stack(g, "Bakery");
        BuildingsTemplate t = bakery.getBuilding();
        Equipment drive = t.getEquipment(DRIVE);
        bakery.setUnfitted(DRIVE, 1);
        play(g, 1);
        check("fixture: a Bakery is unfitted, a month is played, and it is 1900", bakery.getUnfitted(DRIVE) == 1 && yearOf(g) == 1900 && drive.knownAt(g.getMonth()), "year " + yearOf(g));

        double coal = cityPrice(g, Good.THERMAL_COAL), power = g.getEconomyManager().getPricePerWatt(), point = g.getBuildingManager().nonMaterialPricePerPoint();
        EquipmentFit.Verdict v = EquipmentFit.verdict(g, bakery, drive);
        check("fixture: the prices are real (coal, power, machinery and a building point all more than nothing)",
                coal > 0 && power > 0 && cityPrice(g, Good.MACHINERY) > 0 && point > 0, coal + " / " + power + " / " + cityPrice(g, Good.MACHINERY) + " / " + point);
        check("the verdict is for this building and this piece, with the piece's life, and is priced (PAYS or NOT_YET)",
                v.templateId() == t.getId() && v.pieceId().equals(DRIVE) && v.lifeMonths() == drive.getLifeMonths()
                        && (v.kind() == EquipmentFit.Kind.PAYS || v.kind() == EquipmentFit.Kind.NOT_YET), v.toString());
        check("its gain is steamGain() at the city's coal price and the grid's price a unit, to the bit",
                v.gain() == EquipmentFit.steamGain(t, coal, power), v.gain() + " vs " + EquipmentFit.steamGain(t, coal, power));
        double units = t.powerBought();
        near("...which is the coal its engine would burn at nameplate at the coal price less the power it would draw instead",
                v.gain(), units * HydroSites.KWH_PER_UNIT * Sector.STEAM_COAL_KG_A_KWH / 1000 * coal - units * power, 1e-12);
        double expectedCost = 0;
        for (Map.Entry<Good, Double> e : drive.getBuys().entrySet()) expectedCost += e.getValue() * cityPrice(g, e.getKey());
        expectedCost += drive.getWork() * point;
        check("its cost is step 2's: the goods it buys at the city's prices plus its work at the builders' price a point, to the bit",
                v.cost() == EquipmentFit.cost(drive, good -> cityPrice(g, good), point), v.cost() + " vs " + expectedCost);
        near("...and the same written out here", v.cost(), expectedCost, 1e-12);
        check("fixture: the fitting buys machinery and has work, so both halves of the cost are in it", drive.getBuys().containsKey(Good.MACHINERY) && drive.getWork() > 0
                && v.cost() > drive.getWork() * point);

        // The rate and the hurdle: the debt manager's on the whole cost, the investment's real rate and hurdle.
        BusinessInvestment plans = g.getBusinessInvestment();
        double amount = g.orderGate().testedOn(v.cost(), industry.getCash());
        double nominal = g.getEconomyManager().getBusinessDebtManager().projectRate(industry.key(), amount);
        check("fixture: the gate tests the whole cost, whatever the sector holds", amount == v.cost() && v.cost() > 0);
        check("the rate is the real rate of what a loan of the whole cost is written at for the sector, to the bit",
                v.realRate() == plans.realTestRate(nominal) && nominal > 0 && v.realRate() > 0, v.realRate() + " vs " + plans.realTestRate(nominal));
        check("the factor is the annuity of the life at that rate", v.factor() == EquipmentFit.annuityFactor(drive.getLifeMonths(), v.realRate()));
        check("the hurdle is the investment's on the whole cost at that loan rate, and PROFIT_OVER_INTEREST times a month's interest at the real rate",
                v.hurdle() == plans.interestHurdle(v.cost(), nominal)
                        && Math.abs(v.hurdle() - v.cost() * v.realRate() / 12 * BusinessInvestment.PROFIT_OVER_INTEREST) <= 1e-12 * v.hurdle());
        check("...and the same hurdle SpreadPlanner strikes for an order of that cost by that sector",
                Math.abs(v.hurdle() - new SpreadPlanner().hurdle(industry, plans, g, v.cost())) <= 1e-12 * v.hurdle());
        boolean pays = v.gain() > 0 && v.gain() * v.factor() >= v.cost() && v.gain() >= v.hurdle();
        check("the kind follows the four steps from those figures (pays: g x factor >= C and g >= the hurdle)",
                (v.kind() == EquipmentFit.Kind.PAYS) == pays && v.earnsBack() == v.gain() * v.factor() && v.earningsOnCost() == v.gain() / v.cost(), v.toString());
        check("...and judge() on them says the same verdict",
                EquipmentFit.judge(drive, t, g.getMonth(), 1, v.gain(), v.cost(), v.realRate(), v.hurdle()).equals(v));

        // The steam coal, against the sector's own: at a share of 1 its buildings burn what the stacks' templates say.
        double tonnes = 0;
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            if (industry.key().equals(s.getBuilding().getSector())) tonnes += s.getQuantity() * EquipmentFit.steamCoalTonnes(s.getBuilding());
        }
        double was = industry.getSteamShare();
        industry.setSteamShare(1);
        double model = industry.getSteamCoalAtCapacity();
        industry.setSteamShare(was);
        near("steamCoalTonnes() added over Industry's stacks is the coal Industry's own steam form buys at a share of 1", tonnes, model, 1e-9);
        check("fixture: that share was put back, and the form buys some coal", industry.getSteamShare() == was && model > 0);

        // The break-even, solved from the verdict's own figures.
        double even = breakEvenCoal(g, t, v);
        check("fixture: the break-even price of coal is a price", even > 0 && Double.isFinite(even), "is " + even);
        EquipmentFit.Verdict above = EquipmentFit.judge(drive, t, g.getMonth(), 1, EquipmentFit.steamGain(t, even * 1.001, power), v.cost(), v.realRate(), v.hurdle());
        EquipmentFit.Verdict below = EquipmentFit.judge(drive, t, g.getMonth(), 1, EquipmentFit.steamGain(t, even * 0.999, power), v.cost(), v.realRate(), v.hurdle());
        check("in the pure core a coal price a thousandth above the break-even pays", above.kind() == EquipmentFit.Kind.PAYS, above.toString());
        check("...and one a thousandth below does not", below.kind() == EquipmentFit.Kind.NOT_YET, below.toString());
        near("...the gain crossing its line there: at the break-even the gain is the greater of cost / factor and the hurdle",
                EquipmentFit.steamGain(t, even, power), gainNeeded(v), 1e-9);

        // ...and in the city: the market's own price moved across it, the verdict read from the model.
        boolean movedUp = priceCoalAt(g, even * 1.01);
        EquipmentFit.Verdict cityAbove = EquipmentFit.verdict(g, bakery, drive);
        boolean movedDown = priceCoalAt(g, even * 0.99);
        EquipmentFit.Verdict cityBelow = EquipmentFit.verdict(g, bakery, drive);
        check("fixture: the city's price of coal moves to a hundredth above and then below the break-even",
                movedUp && movedDown && Math.abs(cityPrice(g, Good.THERMAL_COAL) - even * 0.99) <= 1e-9 * even);
        check("in the city, coal a hundredth above the break-even pays, and the gain is that price's",
                cityAbove.kind() == EquipmentFit.Kind.PAYS && Math.abs(cityAbove.gain() - EquipmentFit.steamGain(t, even * 1.01, power)) <= 1e-9 * cityAbove.gain(),
                cityAbove.toString());
        check("...and a hundredth below it does not, and its cost, rate and hurdle are the same as before (they do not read the coal)",
                cityBelow.kind() == EquipmentFit.Kind.NOT_YET && cityBelow.cost() == v.cost() && cityBelow.realRate() == v.realRate() && cityBelow.hurdle() == v.hurdle(),
                cityBelow.toString());
    }

    /* ============================ 10. the truck's, the tractor's and the rank ============================ */

    /** The harness's own list of the pieces that pay on a sector's stacks, from verdict() alone, in the order it comes. */
    static List<EquipmentFit.Verdict> payingPieces(Game g, Sector sector) {
        List<EquipmentFit.Verdict> pays = new ArrayList<>();
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            if (!sector.key().equals(s.getBuilding().getSector())) continue;
            for (Equipment p : s.getBuilding().getEquipment()) {
                if (s.getUnfitted(p.getId()) <= 0) continue;
                EquipmentFit.Verdict v = EquipmentFit.verdict(g, s, p);
                if (v.kind() == EquipmentFit.Kind.PAYS) pays.add(v);
            }
        }
        return pays;
    }

    static void sectionTen(Game g, Game c) {
        out.println("\n--- 10. the truck's, the tractor's and the rank: the carts' gain from the teams less the trucks, NOT_KNOWN for a tractor, FITTED for none unfitted, and the ranking ---");
        Haulage haulage = c.getSectors().freight().haulage();
        BuildingsStacks depot = stack(c, "Truck Depot");
        BuildingsTemplate dt = depot.getBuilding();
        Equipment truck = dt.getEquipment(TRUCK);
        depot.setUnfitted(TRUCK, 1);
        step(c, 1);
        check("fixture: two depots stand, one lacks its trucks, a month is played", depot.getQuantity() == 2 && depot.getUnfitted(TRUCK) == 1 && haulage.getHorseShare() > 0);
        EquipmentFit.Verdict v = EquipmentFit.verdict(c, depot, truck);
        check("the depot's verdict is priced (PAYS or NOT_YET) with finite figures", (v.kind() == EquipmentFit.Kind.PAYS || v.kind() == EquipmentFit.Kind.NOT_YET)
                && Double.isFinite(v.gain()) && Double.isFinite(v.cost()) && Double.isFinite(v.realRate()) && Double.isFinite(v.hurdle()), v.toString());

        // The carts' two prices a tonne, written out here from the model's own constants and prices.
        double crops = c.getMarkets().get(Good.CROPS).landedPrice(), diesel = c.getMarkets().get(Good.DIESEL).landedPrice();
        double carter = c.getEconomyManager().getWageRates()[JobType.NO_DIPLOMA.ordinal()], driver = c.getEconomyManager().getWageRates()[JobType.DIPLOMA.ordinal()];
        double teams = (Haulage.HORSES_A_TEAM * Haulage.HORSE_FEED_TONNES_A_MONTH * crops + Haulage.CARTERS_A_TEAM * carter) / Haulage.TONNES_A_TEAM_MONTH;
        double trucks = Haulage.DIESEL_LITRES_A_TONNE * diesel + driver / Haulage.TONNES_A_TRUCK_MONTH;
        near("a tonne's city leg by team costs the feed of the team's horses and its carter over the tonnes a team carries", haulage.horseEstimate(), teams, 1e-9);
        near("...by truck, to run: the diesel a tonne and a driver's month over the tonnes a truck carries", haulage.truckRunningEstimate(), trucks, 1e-9);
        // The running price leaves the trucks' capital out: with the horse form off, estimate() is that and the wear and return on a truck's price.
        double kept = haulage.getHorseShare();
        haulage.setHorseShare(0);
        double trucksPrice = c.getMarkets().get(Good.TRUCKS).landedPrice();
        near("...and with the horse form off, Haulage's whole estimate() is that plus the trucks' wear and return on their price over the tonnes a truck carries",
                haulage.estimate() - haulage.truckRunningEstimate(),
                trucksPrice * (1 / Sector.VAN_LIFE_MONTHS + ham.citybuildersim.sectors.Freight.TARGET_RETURN) / Haulage.TONNES_A_TRUCK_MONTH, 1e-9);
        haulage.setHorseShare(kept);
        check("fixture: the teams cost more a tonne than the trucks to run, so the gain is above nothing",
                haulage.horseEstimate() > haulage.truckRunningEstimate(), haulage.horseEstimate() + " vs " + haulage.truckRunningEstimate());
        check("the depot's gain is the truck room's tonnes a month at the teams' price less the trucks', to the bit",
                v.gain() == EquipmentFit.cartGain(dt.getTruckRoom(), haulage.horseEstimate(), haulage.truckRunningEstimate()) && v.gain() > 0, v.gain() + "");
        near("...which is the room x the tonnes a truck carries x (teams - trucks) written out", v.gain(), dt.getTruckRoom() * Haulage.TONNES_A_TRUCK_MONTH * (teams - trucks), 1e-9);
        double room = truck.getBuys().getOrDefault(Good.TRUCKS, -1.0);
        check("the motor truck buys the depot's truck room in TRUCKS and has no work", room == dt.getTruckRoom() && truck.getWork() == 0 && truck.getBuys().size() == 1);
        double truckPrice = cityPrice(c, Good.TRUCKS);
        check("the depot's cost is its trucks at the city's price of TRUCKS, to the bit, and nothing for work", v.cost() == room * truckPrice && truckPrice > 0,
                v.cost() + " vs " + room * truckPrice);
        check("its life is the truck's and its rate is struck for Freight on the whole cost",
                v.lifeMonths() == truck.getLifeMonths() && v.realRate() == c.getBusinessInvestment().realTestRate(
                        c.getEconomyManager().getBusinessDebtManager().projectRate(c.getSectors().freight().key(), v.cost())),
                v.toString());
        check("...and the kind follows the four steps", (v.kind() == EquipmentFit.Kind.PAYS) == (v.gain() > 0 && v.gain() * v.factor() >= v.cost() && v.gain() >= v.hurdle()));

        // The tractor and the fitted.
        BuildingsStacks grain = stack(g, "Grain Farm");
        grain.setUnfitted(TRACTOR, grain.getQuantity());
        EquipmentFit.Verdict tractor = EquipmentFit.verdict(g, grain, grain.getBuilding().getEquipment(TRACTOR));
        check("an unfitted Grain Farm's tractor is NOT_KNOWN in 1900: it is not known before PE14's June 1917, and nothing is priced",
                tractor.kind() == EquipmentFit.Kind.NOT_KNOWN && Double.isNaN(tractor.gain()) && Double.isNaN(tractor.cost()) && tractor.pieceId().equals(TRACTOR),
                tractor.toString());
        BuildingsStacks mixed = stack(g, "Mixed Farm"), bread = stack(g, "Industrial Bakery");
        check("a stack with none unfitted is FITTED, for a tractor and for a drive",
                !mixed.hasUnfitted() && EquipmentFit.verdict(g, mixed, mixed.getBuilding().getEquipment(TRACTOR)).kind() == EquipmentFit.Kind.FITTED
                        && !bread.hasUnfitted() && EquipmentFit.verdict(g, bread, bread.getBuilding().getEquipment(DRIVE)).kind() == EquipmentFit.Kind.FITTED);

        // The rank, in the steam town: two Bakeries' drives with different earnings on cost.
        BuildingsStacks bakery = stack(g, "Bakery");
        bakery.setUnfitted(DRIVE, 1);
        bread.setUnfitted(DRIVE, 1);
        Sector industry = g.getSectors().industry();
        play(g, 1);
        EquipmentFit.Verdict small = EquipmentFit.verdict(g, bakery, bakery.getBuilding().getEquipment(DRIVE));
        EquipmentFit.Verdict large = EquipmentFit.verdict(g, bread, bread.getBuilding().getEquipment(DRIVE));
        double evenSmall = breakEvenCoal(g, bakery.getBuilding(), small), evenLarge = breakEvenCoal(g, bread.getBuilding(), large);
        check("fixture: two drives are unfitted in Industry, and the two stacks need different coal prices to pay",
                small.kind() != EquipmentFit.Kind.FITTED && large.kind() != EquipmentFit.Kind.FITTED && evenSmall > 0 && evenLarge > 0 && evenSmall != evenLarge
                        && Double.isFinite(evenSmall) && Double.isFinite(evenLarge), evenSmall + " / " + evenLarge);
        double lowEven = Math.min(evenSmall, evenLarge), highEven = Math.max(evenSmall, evenLarge);
        BuildingsStacks cheaper = evenSmall < evenLarge ? bakery : bread;

        check("fixture: the city's price of coal moves", priceCoalAt(g, lowEven * 0.9));
        check("coal under both break-evens: both drives are NOT_YET and the rank is empty", EquipmentFit.rank(g, industry).isEmpty() && payingPieces(g, industry).isEmpty(),
                rankIds(g, industry).toString());
        priceCoalAt(g, (lowEven + highEven) / 2);
        List<EquipmentFit.Verdict> mid = EquipmentFit.rank(g, industry);
        check("coal between them: only the drive that pays is ranked, the other (NOT_YET) is left out",
                mid.size() == 1 && mid.get(0).templateId() == cheaper.getBuilding().getId() && mid.get(0).kind() == EquipmentFit.Kind.PAYS, mid.toString());
        priceCoalAt(g, highEven * 1.1);
        List<EquipmentFit.Verdict> both = EquipmentFit.rank(g, industry);
        List<EquipmentFit.Verdict> own = payingPieces(g, industry);
        own.sort((x, y) -> Double.compare(y.earningsOnCost(), x.earningsOnCost()));
        check("coal over both: both are ranked, the same pieces as the verdicts that pay", both.size() == 2 && own.size() == 2 && both.containsAll(own), both.toString());
        check("...best earnings on cost first, as sorted here from the verdicts alone", both.equals(own) && both.get(0).earningsOnCost() > both.get(1).earningsOnCost(),
                both.toString());
        check("...each entry is a PAYS verdict whose gain x factor covers its cost and whose gain clears its hurdle", both.stream().allMatch(x -> x.kind() == EquipmentFit.Kind.PAYS
                && x.earnsBack() >= x.cost() && x.gain() >= x.hurdle()));
        check("a sector with nothing unfitted ranks none, and so does one whose only unfitted piece is NOT_KNOWN (Agriculture's tractor)",
                EquipmentFit.rank(g, g.getSectors().freight()).isEmpty() && EquipmentFit.rank(g, g.getSectors().retail()).isEmpty()
                        && EquipmentFit.rank(g, g.getSectors().agriculture()).isEmpty() && grain.hasUnfitted());
        List<EquipmentFit.Verdict> hauled = EquipmentFit.rank(c, c.getSectors().freight());
        check("the depot's truck is ranked when it pays and not when it does not, alone in the cart town's Freight",
                v.kind() == EquipmentFit.Kind.PAYS ? hauled.size() == 1 && hauled.get(0).equals(v) : hauled.isEmpty(), hauled.toString());
        check("...and nothing else in the cart town is ranked", EquipmentFit.rank(c, c.getSectors().industry()).isEmpty() && EquipmentFit.rank(c, c.getSectors().agriculture()).isEmpty());
        depot.setUnfitted(TRUCK, 0);
        check("with the depots' count at 0 the piece is FITTED and Freight ranks none",
                EquipmentFit.verdict(c, depot, truck).kind() == EquipmentFit.Kind.FITTED && EquipmentFit.rank(c, c.getSectors().freight()).isEmpty());

        // The ordering, with ties: fixed verdicts, in every order they might come.
        EquipmentFit.Verdict best = fixed(5, "a", 3.0), tieLow = fixed(7, "a", 1.5), tieMid = fixed(7, "b", 1.5), tieHigh = fixed(9, "a", 1.5), worst = fixed(1, "z", 0.25);
        List<EquipmentFit.Verdict> want = List.of(best, tieLow, tieMid, tieHigh, worst);
        boolean everyOrder = true;
        int orders = 0;
        for (List<EquipmentFit.Verdict> order : permutations(new ArrayList<>(want))) {
            List<EquipmentFit.Verdict> sorted = new ArrayList<>(order);
            sorted.sort(EquipmentFit.BEST_FIRST);
            everyOrder &= sorted.equals(want);
            orders++;
        }
        check("the ranking is best earnings on cost first, equal earnings by template id and then piece id, whatever order they come in (all 120 orders)",
                everyOrder && orders == 120);
        EquipmentFit.Verdict free = fixed(2, "a", Double.POSITIVE_INFINITY);
        check("...and an unbounded earning (a free piece) is first", free.equals(sortedFirst(List.of(best, free, worst))));
    }

    /** The ids of a sector's ranked verdicts, template and piece: for a failure's detail. */
    static List<String> rankIds(Game g, Sector s) {
        List<String> ids = new ArrayList<>();
        for (EquipmentFit.Verdict v : EquipmentFit.rank(g, s)) ids.add(v.templateId() + "/" + v.pieceId());
        return ids;
    }

    /** A verdict that pays, with the figures a ranking reads: its template, piece and earnings on cost. */
    static EquipmentFit.Verdict fixed(int templateId, String pieceId, double earningsOnCost) {
        return new EquipmentFit.Verdict(EquipmentFit.Kind.PAYS, templateId, pieceId, 1, 1, 100, 0.05, 80, 80, 0, earningsOnCost);
    }

    static EquipmentFit.Verdict sortedFirst(List<EquipmentFit.Verdict> list) {
        List<EquipmentFit.Verdict> copy = new ArrayList<>(list);
        copy.sort(EquipmentFit.BEST_FIRST);
        return copy.get(0);
    }

    /** Every order a list of distinct items can come in. */
    static <T> List<List<T>> permutations(List<T> items) {
        List<List<T>> all = new ArrayList<>();
        permute(items, 0, all);
        return all;
    }

    private static <T> void permute(List<T> items, int k, List<List<T>> all) {
        if (k == items.size()) {
            all.add(new ArrayList<>(items));
            return;
        }
        for (int i = k; i < items.size(); i++) {
            Collections.swap(items, k, i);
            permute(items, k + 1, all);
            Collections.swap(items, k, i);
        }
    }

    /* ============================ 11. the verdict changes nothing ============================ */

    /** Asks the city every verdict and every rank there is: each piece of each stack, fitted or not, and each sector's ranking. */
    static void askEverything(Game g) {
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            for (Equipment p : s.getBuilding().getEquipment()) EquipmentFit.verdict(g, s, p);
        }
        for (Sector s : g.getSectors().all()) EquipmentFit.rank(g, s);
    }

    /** The shares and counts the forms and the stacks hold, as text: for the twins' comparison beside Forms1900Check's snapshot. */
    static String formsAndCounts(Game g) {
        StringBuilder b = new StringBuilder();
        for (Sector s : g.getSectors().all()) b.append(s.key()).append(':').append(Double.doubleToLongBits(s.getSteamShare())).append(' ');
        b.append("farm ").append(Double.doubleToLongBits(g.getSectors().agriculture().getHorseShare()));
        b.append(" cart ").append(Double.doubleToLongBits(g.getSectors().freight().haulage().getHorseShare()));
        b.append(" driven ").append(g.getEquipmentForms().driven());
        for (BuildingsStacks s : g.getBuildingManager().getStacksView()) {
            b.append(' ').append(s.getBuilding().getId()).append('x').append(s.getQuantity()).append(s.getUnfittedView());
        }
        return b.toString();
    }

    /** Plays two twin cities for `months` (the cart towns by toggleNextMonth, the others by simulateMonths, as their sections do), asking the first everything before and after each month; the first month they differ in, or null. */
    static String twinsAfter(Game asked, Game left, int months, boolean carts) {
        for (int m = 0; m < months; m++) {
            askEverything(asked);
            if (carts) step(asked, 1); else play(asked, 1);
            askEverything(asked);
            if (carts) step(left, 1); else play(left, 1);
            String diff = Forms1900Check.firstDifference(asked, left);
            if (diff != null) return "month " + (m + 1) + ", " + diff;
            if (!formsAndCounts(asked).equals(formsAndCounts(left))) return "month " + (m + 1) + ", shares or counts";
        }
        return null;
    }

    static void sectionEleven() {
        out.println("\n--- 11. the verdict changes nothing: twin cities with the same counts, one asked everything every month for six months, are equal to the bit ---");
        Game a = Forms1900Check.town("equipment-twin-a"), b = Forms1900Check.town("equipment-twin-b");
        for (Game g : new Game[]{a, b}) {
            stack(g, "Bakery").setUnfitted(DRIVE, 1);
            stack(g, "Industrial Bakery").setUnfitted(DRIVE, stack(g, "Industrial Bakery").getQuantity());
            stack(g, "Grain Farm").setUnfitted(TRACTOR, stack(g, "Grain Farm").getQuantity());
        }
        check("fixture: the twins start equal, with the same counts and nothing asked", Forms1900Check.firstDifference(a, b) == null && formsAndCounts(a).equals(formsAndCounts(b))
                && anyUnfitted(a));
        String diff = twinsAfter(a, b, 6, false);
        check("six months, asked every verdict and every rank before and after each: the treasury, every sector's books and rates, the shares and the counts are equal",
                diff == null, diff);
        check("...and the forms were set from the counts the whole time (the question did not stop the month): Industry's steam share is up in both",
                a.getSectors().industry().getSteamShare() > 0 && a.getSectors().industry().getSteamShare() == b.getSectors().industry().getSteamShare());

        Game ca = cartTown("equipment-twin-carts-a"), cb = cartTown("equipment-twin-carts-b");
        for (Game g : new Game[]{ca, cb}) stack(g, "Truck Depot").setUnfitted(TRUCK, 1);
        check("fixture: the cart twins start equal with a depot unfitted", Forms1900Check.firstDifference(ca, cb) == null && anyUnfitted(ca) && anyUnfitted(cb));
        String cartDiff = twinsAfter(ca, cb, 6, true);
        check("...and six months of the same, on the depot's truck: equal to the bit, the trucks, the quote and the carts' share included", cartDiff == null, cartDiff);
        check("...with the carts' horse share up in both", ca.getSectors().freight().haulage().getHorseShare() > 0
                && ca.getSectors().freight().haulage().getHorseShare() == cb.getSectors().freight().haulage().getHorseShare());
    }
}
