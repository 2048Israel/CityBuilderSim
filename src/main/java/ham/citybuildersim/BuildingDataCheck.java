package ham.citybuildersim;

import java.util.List;

/**
 * The migration's safety net: buildings.json must produce exactly the templates
 * the hardcoded definitions did.
 *
 * Run against the real Gson, not a stub, because the whole risk of moving data
 * out of code is that the two quietly disagree - a field that silently reads
 * zero, an id that lands on the wrong building, a job tier that never loads.
 * Field-by-field equality against the built-ins is the only check that catches
 * that.
 */
public class BuildingDataCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-9;
        if (!ok) fails++;
        if (!ok) {
            System.out.printf("  FAIL %-40s %12.3f != %12.3f%n", label, actual, expected);
        }
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-56s %s%n", label, ok ? "OK" : "FAIL");
    }

    public static void main(String[] args) {

        /* ---------- what the code says ---------- */
        BuildingManager builtIn = new BuildingManager();
        builtIn.initializeBuiltInTemplates();
        List<BuildingsTemplate> code = builtIn.getTemplates();

        /* ---------- what the file says ---------- */
        BuildingCatalog catalog = new BuildingCatalog();
        List<BuildingsTemplate> data = catalog.load();

        System.out.println("--- source ---");
        assertTrue("buildings.json loaded at all", data != null);
        if (data == null) {
            System.out.println("\n1 FAILED - nothing to compare against");
            System.exit(1);
        }
        System.out.println("   from: " + catalog.getSource());

        System.out.println("\n--- coverage ---");
        assertTrue("same number of buildings (" + data.size() + ")", data.size() == code.size());

        /* ---------- every field of every building ---------- */
        System.out.println("\n--- field by field, against the built-in definitions ---");

        for (BuildingsTemplate expected : code) {

            BuildingsTemplate actual = null;
            for (BuildingsTemplate t : data) {
                if (t.getId() == expected.getId()) {
                    actual = t;
                    break;
                }
            }

            if (actual == null) {
                fails++;
                System.out.println("  FAIL missing from json: " + expected.getName()
                        + " (id " + expected.getId() + ")");
                continue;
            }

            String who = expected.getName();

            boolean ok = actual.getName().equals(expected.getName())
                    && actual.getCategory() == expected.getCategory();
            if (!ok) {
                fails++;
                System.out.println("  FAIL " + who + ": name or category differs");
            }

            // The care type is the only field whose WRONG value is a plausible
            // one - every building has a legal CareType, so a hospital filed as
            // NONE reads as a perfectly valid building that happens to treat
            // nobody. Compared explicitly rather than left to the numeric sweep.
            if (actual.getCare() != expected.getCare()) {
                fails++;
                System.out.println("  FAIL " + who + ": care " + actual.getCare()
                        + " != " + expected.getCare());
            }
            if (actual.getTeaches() != expected.getTeaches()) {
                fails++;
                System.out.println("  FAIL " + who + ": teaches " + actual.getTeaches()
                        + " != " + expected.getTeaches());
            }
            if (actual.getSafety() != expected.getSafety()) {
                fails++;
                System.out.println("  FAIL " + who + ": safety " + actual.getSafety()
                        + " != " + expected.getSafety());
            }
            // What a water works draws (0.7.59): a desalination plant read as
            // FRESH is a legal building that the fresh water limit would hold.
            if (actual.getSource() != expected.getSource()) {
                fails++;
                System.out.println("  FAIL " + who + ": source " + actual.getSource()
                        + " != " + expected.getSource());
            }

            int before = fails;

            check(who + " capacity", actual.getCapacity(), expected.getCapacity());
            check(who + " coverage", actual.getCoverage(), expected.getCoverage());
            check(who + " cashCost", actual.getCashCost(), expected.getCashCost());
            check(who + " constructionPoints",
                    actual.getConstructionPoints(), expected.getConstructionPoints());
            check(who + " constructionMaterials",
                    actual.getConstructionMaterials(), expected.getConstructionMaterials());
            check(who + " upkeep", actual.getUpkeep(), expected.getUpkeep());
            check(who + " electricity",
                    actual.getElectricityConsumption(), expected.getElectricityConsumption());
            check(who + " water", actual.getWaterConsumption(), expected.getWaterConsumption());
            check(who + " land", actual.getLandSqFt(), expected.getLandSqFt());
            check(who + " roadLoad", actual.getRoadLoad(), expected.getRoadLoad());
            check(who + " freightGrade", actual.getFreightGrade(), expected.getFreightGrade());
            check(who + " transitCapacity", actual.getTransitCapacity(), expected.getTransitCapacity());
            check(who + " railCapacity", actual.getRailCapacity(), expected.getRailCapacity());
            check(who + " production1", actual.getProduction1(), expected.getProduction1());
            check(who + " production2", actual.getProduction2(), expected.getProduction2());

            // Every job tier, not just the ones the building uses - a tier that
            // silently failed to load would otherwise pass unnoticed.
            for (JobType job : JobType.values()) {
                check(who + " jobs " + job, actual.getJobs(job), expected.getJobs(job));
            }

            if (fails == before) {
                System.out.printf("%-56s OK%n", "  " + who + " (id " + expected.getId() + ")");
            }
        }

        /* ---------- care types line up with the category ----------

           An aggregate assertion cannot see a missing category, so this is
           stated both ways round: no healthcare building may be uncategorised,
           and nothing outside healthcare may claim to treat anybody. The second
           half is the one that catches a copy-paste, which is how a shop would
           end up counting toward hospital beds. */
        System.out.println("\n--- care types ---");
        boolean careSane = true;
        for (BuildingsTemplate t : data) {
            boolean healthcare = t.getCategory() == BuildingType.HEALTHCARE;
            boolean declared = t.getCare() != CareType.NONE;
            if (healthcare != declared) {
                careSane = false;
                System.out.println("  " + t.getName() + ": category "
                        + t.getCategory() + " with care " + t.getCare());
            }
        }
        assertTrue("healthcare declares a care type, nothing else does", careSane);

        // Same assertion, same reason, one category over. A school filed as
        // NONE is a perfectly valid building that happens to teach nobody.
        boolean schoolSane = true;
        for (BuildingsTemplate t : data) {
            boolean education = t.getCategory() == BuildingType.EDUCATION;
            boolean declared = t.getTeaches() != EducationType.NONE;
            if (education != declared) {
                schoolSane = false;
                System.out.println("  " + t.getName() + ": category "
                        + t.getCategory() + " teaching " + t.getTeaches());
            }
        }
        assertTrue("education declares what it teaches, nothing else does", schoolSane);

        // ...and one more over. A police station filed as NONE is a perfectly
        // valid building that happens to patrol nothing.
        boolean safetySane = true;
        int police = 0, prisons = 0;
        for (BuildingsTemplate t : data) {
            boolean safety = t.getCategory() == BuildingType.SAFETY;
            boolean declared = t.getSafety() != SafetyType.NONE;
            if (safety != declared) {
                safetySane = false;
                System.out.println("  " + t.getName() + ": category "
                        + t.getCategory() + " with safety " + t.getSafety());
            }
            if (t.getSafety() == SafetyType.POLICE) police++;
            if (t.getSafety() == SafetyType.PRISON) prisons++;
        }
        assertTrue("safety declares police or prison, nothing else does", safetySane);
        // Jerus: "two of each".
        assertTrue("two police buildings and two prisons", police == 2 && prisons == 2);

        /* ---------- the sea is drawn by water works, and one of them (0.7.59) ----------

           A building that says "source": "SEA" and is not a water works means
           somebody meant something and it does nothing; a water works that
           lost its line reads as fresh and is held to the lakes. Both ways
           round, as care is, and the count Jerus agreed: one desalination
           plant, beside the one water plant. */
        System.out.println("\n--- water sources ---");
        boolean seaSane = true;
        int sea = 0, fresh = 0;
        for (BuildingsTemplate t : data) {
            if (t.getSource() == BuildingsTemplate.Source.SEA && t.getCategory() != BuildingType.WATER) {
                seaSane = false;
                System.out.println("  " + t.getName() + ": category " + t.getCategory() + " draws the sea");
            }
            if (t.isSeaWater()) sea++;
            if (t.isFreshWater()) fresh++;
        }
        assertTrue("only a water works draws the sea", seaSane);
        assertTrue("one water plant on fresh water and one on the sea (" + fresh + ", " + sea + ")",
                fresh == 1 && sea == 1);
        BuildingsTemplate desal = null;
        for (BuildingsTemplate t : data) if (t.getId() == 73) desal = t;
        // ...in all 74 until 0.7.62; the count is fuel's below.
        assertTrue("id 73 is the Desalination Plant",
                desal != null && "Desalination Plant".equals(desal.getName()) && desal.isSeaWater());

        /* ---------- fuel (0.7.62, batch K; spec-land 2.7) ----------

           The Oil Well on an oil site and the refinery the wells feed, at the
           spec's figures: a hundred barrels a day of crude, and a 2,000-barrel
           plant's 8,300 t into a thousand litres a tonne, three months of it
           in its tanks. Owned by the two new sectors, so the investors build
           them, and each the template its sector plans from.

           ...AND SINCE 0.7.76 (batch O1; spec-oil 2.3) THE REFINERY IS A CRUDE
           UNIT: its template takes its crude and makes nothing of its own -
           its products are the sector's slate of that crude - and its tanks
           hold more than the months of its run a maker keeps of a good
           (Sector.STOCK_MONTHS), shared as the run is. */
        System.out.println("\n--- fuel ---");
        BuildingsTemplate well = null, refinery = null;
        for (BuildingsTemplate t : data) {
            if (t.getId() == 74) well = t;
            if (t.getId() == 75) refinery = t;
        }
        assertTrue("id 74 is the Oil Well: MINING, the Oil sector's, 415 t of crude a month and nothing else",
                well != null && "Oil Well".equals(well.getName()) && well.getCategory() == BuildingType.MINING
                        && Sectors.OIL.equals(well.getSector()) && well.makes(Good.CRUDE) == 415
                        && well.goodsMade().size() == 1 && well.goodsUsed().isEmpty());
        assertTrue("id 75 is the Oil Refinery: HEAVY_INDUSTRY, Refining's, a crude unit taking 8,300 t of crude and making"
                        + " nothing of its own (0.7.76: its products are the slate of its crude)",
                refinery != null && "Oil Refinery".equals(refinery.getName())
                        && refinery.getCategory() == BuildingType.HEAVY_INDUSTRY && Sectors.REFINING.equals(refinery.getSector())
                        && refinery.uses(Good.CRUDE) == 8300 && refinery.goodsMade().isEmpty()
                        && ham.citybuildersim.sectors.Refining.isCrudeUnit(refinery));
        double run = 0;
        if (refinery != null) for (double v : ham.citybuildersim.sectors.Refining.madeBy(refinery).values()) run += v;
        assertTrue("...holding more than the months of its run a maker keeps (STOCK_MONTHS) in its tanks",
                refinery != null && run > 0 && refinery.getStock() >= Sector.STOCK_MONTHS * run);
        assertTrue("...and 76 buildings in all", data.size() == 76);

        /* ---------- and every profession has exactly one school ----------

           A job type gated by a school nobody can build is a post that can
           never be filled by a resident, which looks identical to a balance
           problem. Two schools licensing the same profession is the other
           half: harmless today, and the shape of a copy-paste. */
        boolean licences = true;
        for (EducationType type : EducationType.values()) {
            if (!type.isProfessional()) continue;
            int schools = 0;
            for (BuildingsTemplate t : data) if (t.getTeaches() == type) schools++;
            if (schools != 1) {
                licences = false;
                System.out.println("  " + type + " licenses " + type.licenses()
                        + " and has " + schools + " schools");
            }
        }
        assertTrue("every gated profession has exactly one school", licences);

        /* ---------- ids are unique, which the saves depend on ---------- */
        System.out.println("\n--- ids ---");
        boolean unique = true;
        for (int i = 0; i < data.size(); i++) {
            for (int j = i + 1; j < data.size(); j++) {
                if (data.get(i).getId() == data.get(j).getId()) {
                    unique = false;
                    System.out.println("  duplicate id " + data.get(i).getId());
                }
            }
        }
        assertTrue("every id is unique", unique);

        /* ---------- the manager actually uses the file ---------- */
        System.out.println("\n--- BuildingManager uses the catalog ---");
        BuildingManager live = new BuildingManager();
        live.initializeTemplates();
        assertTrue("initializeTemplates() produced the same count",
                live.getTemplates().size() == code.size());
        assertTrue("lookup by id still works",
                live.getTemplate(10) != null
                        && "Water Treatment Plant".equals(live.getTemplate(10).getName()));
        assertTrue("lookup by name still works",
                live.getTemplateByName("House") != null);

        System.out.println(fails == 0
                ? "\nbuildings.json matches the built-in definitions exactly."
                : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
