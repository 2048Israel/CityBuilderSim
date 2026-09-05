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
