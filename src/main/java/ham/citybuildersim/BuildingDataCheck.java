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

    /** The building with this id in a list of templates, or null. */
    static BuildingsTemplate byId(List<BuildingsTemplate> data, int id) {
        for (BuildingsTemplate t : data) if (t.getId() == id) return t;
        return null;
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
            // A refinery's conversion unit (0.7.80): a unit that lost its line
            // reads as a building that takes nothing and makes nothing.
            if (actual.refineryUnit() != expected.refineryUnit()) {
                fails++;
                System.out.println("  FAIL " + who + ": refinery unit " + actual.refineryUnit()
                        + " != " + expected.refineryUnit());
            }
            // ...and what it makes and uses (0.7.62's two wells and refinery were read; now every building).
            if (!actual.goodsMade().equals(expected.goodsMade()) || !actual.goodsUsed().equals(expected.goodsUsed())) {
                fails++;
                System.out.println("  FAIL " + who + ": makes or uses differs");
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
            check(who + " stationTonnes (FR7)", actual.getStationTonnes(), expected.getStationTonnes());
            check(who + " siding (FR7)", actual.isSiding() ? 1 : 0, expected.isSiding() ? 1 : 0);
            check(who + " hydro (HY1)", actual.isHydro() ? 1 : 0, expected.isHydro() ? 1 : 0);
            check(who + " truckRoom", actual.getTruckRoom(), expected.getTruckRoom());
            check(who + " production1", actual.getProduction1(), expected.getProduction1());
            check(who + " production2", actual.getProduction2(), expected.getProduction2());
            check(who + " stock", actual.getStock(), expected.getStock());
            check(who + " refinery feed", actual.feedPerMonth(), expected.feedPerMonth());
            // ...and a filling station's litres at the pump (0.7.83): one that lost its line sells nothing.
            check(who + " pump", actual.pumpLitres(), expected.pumpLitres());
            // ...and a road kind's own repairs (0.7.103): one that lost its line is repaired at the house's 1%.
            check(who + " repairs", actual.repairsPerYear(), expected.repairsPerYear());
            // ...and a sea terminal's berth (0.7.86): one that lost its line takes nothing off the lorries.
            if (actual.berthCargo() != expected.berthCargo()) {
                fails++;
                System.out.println("  FAIL " + who + ": berth " + actual.berthCargo() + " != " + expected.berthCargo());
            }
            check(who + " berth tonnes", actual.berthTonnesAYear(), expected.berthTonnesAYear());
            // ...and the classes that call at it (FR3): a Bulk Berth that lost its line would take a Capesize.
            if (!actual.berthShips().equals(expected.berthShips())) {
                fails++;
                System.out.println("  FAIL " + who + ": berth ships " + actual.berthShips() + " != " + expected.berthShips());
            }
            // ...and the oil at sea (0.7.91): a platform that lost its line would stand on the land and hold no well.
            if (actual.offshore() != expected.offshore()) {
                fails++;
                System.out.println("  FAIL " + who + ": offshore " + actual.offshore() + " != " + expected.offshore());
            }
            check(who + " platform slots", actual.platformSlots(), expected.platformSlots());
            check(who + " onshore cash a km", actual.onshoreCashPerKm(), expected.onshoreCashPerKm());
            // ...and the site it stands on (0.7.110): a mine that lost its line would stand on no deposit and be no mine.
            if (!java.util.Objects.equals(actual.site(), expected.site())) {
                fails++;
                System.out.println("  FAIL " + who + ": site " + actual.site() + " != " + expected.site());
            }
            // ...and a works' own power and its consumables (0.7.112): one that lost its line would buy all its
            // power and nothing abroad, one that gained a line would make power or pay the world unasked.
            check(who + " gen", actual.getGen(), expected.getGen());
            check(who + " consumables", actual.getConsumables(), expected.getConsumables());
            // ...and the old metal a yard takes and the year it first existed (0.7.123, batch I4): a yard that lost
            // its line would take no old steel and make no scrap; a row that lost its year would read as undated.
            if (!actual.oldMetalTaken().equals(expected.oldMetalTaken())) {
                fails++;
                System.out.println("  FAIL " + who + ": old metal " + actual.oldMetalTaken() + " != " + expected.oldMetalTaken());
            }
            check(who + " since", actual.getSince(), expected.getSince());
            // ...and the raw coal a preparation plant washes (0.7.126, batch I6): a plant that lost its line would wash
            // nothing, and every coking mine's coal would sell raw.
            check(who + " wash", actual.getWash(), expected.getWash());

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

        /* ---------- the refinery's units (0.7.80, batch O4; spec-oil 2.3) ----------

           The Crude Unit, the large crude unit, at 50 Oil Refineries' crude,
           road load and tanks; and behind the crude units fourteen conversion
           units, a small and a large of each of RefineryFlow's seven kinds,
           ids 77 to 90 in the kinds' order, each Refining's, buying and
           making nothing of its own - its feed a stream of the crude units'
           run - with no road load and no tanks. The small one takes less
           feed than the large and costs less, and more for each litre. */
        System.out.println("\n--- the refinery's units ---");
        BuildingsTemplate large = null;
        for (BuildingsTemplate t : data) if (t.getId() == 76) large = t;
        assertTrue("id 76 is the Crude Unit: HEAVY_INDUSTRY, Refining's, a crude unit of 50 Oil Refineries' crude, road load and tanks",
                large != null && refinery != null && "Crude Unit".equals(large.getName())
                        && large.getCategory() == BuildingType.HEAVY_INDUSTRY && Sectors.REFINING.equals(large.getSector())
                        && ham.citybuildersim.sectors.Refining.isCrudeUnit(large) && large.refineryUnit() == null
                        && large.uses(Good.CRUDE) == 50 * refinery.uses(Good.CRUDE) && large.goodsMade().isEmpty()
                        && large.getRoadLoad() == 50 * refinery.getRoadLoad() && large.getStock() == 50 * refinery.getStock());
        boolean units = true, sizes = true;
        ham.citybuildersim.sectors.RefineryFlow.Kind[] kinds = ham.citybuildersim.sectors.RefineryFlow.Kind.values();
        for (int k = 0; k < kinds.length; k++) {
            BuildingsTemplate small = null, big = null;
            for (BuildingsTemplate t : data) {
                if (t.getId() == 77 + 2 * k) small = t;
                if (t.getId() == 78 + 2 * k) big = t;
            }
            for (BuildingsTemplate t : new BuildingsTemplate[] { small, big }) {
                boolean ok = t != null && t.refineryUnit() == kinds[k] && t.getCategory() == BuildingType.HEAVY_INDUSTRY
                        && Sectors.REFINING.equals(t.getSector()) && ham.citybuildersim.sectors.Refining.isConversionUnit(t)
                        && !ham.citybuildersim.sectors.Refining.isCrudeUnit(t) && t.goodsMade().isEmpty() && t.goodsUsed().isEmpty()
                        && t.feedPerMonth() > 0 && t.getRoadLoad() == 0 && t.getStock() == 0;
                if (!ok) {
                    units = false;
                    System.out.println("  not a " + kinds[k] + " unit: " + (t == null ? "missing" : t.getName()));
                }
            }
            if (small == null || big == null) { sizes = false; continue; }
            boolean pair = big.getName().equals(kinds[k].unitName()) && small.getName().equals("Small " + kinds[k].unitName())
                    && small.feedPerMonth() < big.feedPerMonth() && small.getCashCost() < big.getCashCost()
                    && small.getCashCost() / small.feedPerMonth() > big.getCashCost() / big.feedPerMonth();
            if (!pair) {
                sizes = false;
                System.out.println("  the " + kinds[k] + " pair: " + small.getName() + ", " + big.getName());
            }
        }
        assertTrue("ids 77 to 90 are the conversion units, a small and a large of each kind in RefineryFlow.Kind's order:"
                + " Refining's, each fed its stream and buying and making nothing, with no road load and no tanks", units);
        assertTrue("...each pair named for its kind, the small taking less feed for less money and more money a litre", sizes);

        /* ---------- the phase-1 buyers and the forecourt (0.7.83, batch O6) ----------

           LUBRICANTS on the factories that make cars, vans, machinery and
           fabricated steel, at the spec's rates a unit made (spec-oil 2.5):
           Automotive's eight litres a vehicle (a van at a car's, star O6; a
           wagon set none), Manufacturing's ten a tonne of machinery and two a
           tonne of fabricated steel. And id 91, the Filling Station: Retail's,
           COMMERCIAL, selling 350,000 litres a month at the pump
           (runs/research-pump.md 7) and buying and making nothing on a market,
           US$0.5M all in - the research's fuel-only kiosk - on an acre's
           4,000 m2 with three posts. */
        System.out.println("\n--- the phase-1 buyers and the forecourt ---");
        boolean lubricated = true;
        int oiled = 0;
        for (BuildingsTemplate t : data) {
            double rate = Sectors.AUTOMOTIVE.equals(t.getSector())
                    ? (t.makes(Good.CARS) + t.makes(Good.VANS)) * ham.citybuildersim.sectors.Automotive.LUBRICANT_LITRES_A_VEHICLE
                            + t.makes(Good.TRUCKS) * ham.citybuildersim.sectors.Automotive.LUBRICANT_LITRES_A_TRUCK   // FR2: a truck's own fill
                    : Sectors.MANUFACTURING.equals(t.getSector())
                    ? t.makes(Good.MACHINERY) * ham.citybuildersim.sectors.Manufacturing.LUBRICANT_LITRES_A_TONNE_OF_MACHINERY
                            + t.makes(Good.FABRICATED_STEEL) * ham.citybuildersim.sectors.Manufacturing.LUBRICANT_LITRES_A_TONNE_FABRICATED
                    : 0;
            if (t.uses(Good.LUBRICANTS) != rate) {
                lubricated = false;
                System.out.println("  " + t.getName() + ": " + t.uses(Good.LUBRICANTS) + " L of lubricants, not " + rate);
            }
            if (rate > 0) oiled++;
        }
        assertTrue("lubricants on every car, van, truck, machinery and fabrication plant at the spec's rate a unit made, and on"
                + " nothing else (" + oiled + " plants; the Heavy Truck Plant, FR2, the seventh)", lubricated && oiled == 7);
        BuildingsTemplate station = null;
        for (BuildingsTemplate t : data) if (t.getId() == 91) station = t;
        assertTrue("id 91 is the Filling Station: COMMERCIAL, Retail's, 350,000 L a month at the pump, buying and making"
                        + " nothing on a market",
                station != null && "Filling Station".equals(station.getName())
                        && station.getCategory() == BuildingType.COMMERCIAL && Sectors.RETAIL.equals(station.getSector())
                        && station.pumpLitres() == 350_000 && station.goodsMade().isEmpty() && station.goodsUsed().isEmpty()
                        && station.getCoverage() == 0 && ham.citybuildersim.sectors.Retail.isStation(station));
        assertTrue("...US$0.5M all in at founding prices (cash and 18 a unit of material), three posts, an acre's 4,000 m2",
                station != null && station.getCashCost() + 18 * station.getConstructionMaterials() == 500
                        && station.getTotalJobs() == 3 && Math.round(station.getLandSqFt() / 10.7639104) == 4000);
        int stations = 0;
        for (BuildingsTemplate t : data) if (t.pumpLitres() > 0) stations++;
        assertTrue("...the only building with a pump", stations == 1);
        // EACH ROAD KIND ITS OWN REPAIRS (0.7.103, batch P4; Jerus's A11): the three roads' "repairs", and no other's.
        java.util.Map<String, Double> repairs = new java.util.TreeMap<>();
        for (BuildingsTemplate t : data) if (t.hasOwnRepairs()) repairs.put(t.getName(), t.repairsPerYear());
        assertTrue("the three roads each have their own repairs a year - the Gravel Road 3%, the Paved Road 2%, the Elevated"
                        + " Highway 1.25% - and no other building has: " + repairs,
                repairs.equals(java.util.Map.of("Gravel Road", .03, "Paved Road", .02, "Elevated Highway", .0125)));
        assertTrue("...and 145 buildings in all (HY1: the Hydro Station, id 144; 144 from PT1-M4: the Builders' Yard, id 143, the founding's own; 143 from FR3, the Bulk Berth, id 142; 142 from FR7, the Rail Depot; 141 from FR2, the Truck Depot and the Heavy Truck Plant; 139 from 0.7.140, Wood Products' six; 133 from 0.7.138, Forestry's two crews and two planted"
                        + " stands; 129 from 0.7.133, Copper"
                        + " Mining's three, Copper Refining's four and Manufacturing's Wire & Cable Works; 121 from 0.7.131, Quarrying's three and Cement & Concrete's three; 115 from 0.7.126, Coal Mining's four;"
                        + " 111 from 0.7.124, Iron Mining's three; 108 from 0.7.123, the integrated route's seven; 101 from 0.7.91, 98 from"
                        + " 0.7.86, 94 from 0.7.85, 92 from 0.7.83, 91 from 0.7.80, 76 until then)",
                data.size() == 145);

        /* ---------- the oil storage (0.7.85, batch O8; spec-oil 2.8) ----------

           The refiners' Tank Farm and the city's Strategic Reserve: 500,000 m3 of
           tanks [4.5] on 10 ha [P13], D$190M (the research's US$60 a barrel)
           split as the refiners' units are, cash the capital and material a
           sixtieth, ten posts the farm's and none the reserve's. */
        System.out.println("\n--- the oil storage ---");
        BuildingsTemplate farm = null, store = null;
        for (BuildingsTemplate t : data) {
            if ("Tank Farm".equals(t.getName())) farm = t;
            if ("Strategic Reserve".equals(t.getName())) store = t;
        }
        assertTrue("id 92 is the Tank Farm, the refiners': 500,000 m3 of tanks, refining nothing, buying nothing",
                farm != null && farm.getId() == 92 && Sectors.REFINING.equals(farm.getSector())
                        && farm.getCategory() == BuildingType.HEAVY_INDUSTRY && farm.getStock() == 500_000_000
                        && farm.goodsMade().isEmpty() && farm.goodsUsed().isEmpty() && farm.refineryUnit() == null
                        && ham.citybuildersim.sectors.Refining.isTankFarm(farm) && !StrategicReserve.isReserve(farm));
        assertTrue("...D$190M of capital (US$60 a barrel of room, star), material a sixtieth of it and points a half; ten"
                        + " posts 5/3/2; 10 ha",
                farm != null && farm.getCashCost() == 190_000 && farm.getConstructionMaterials() == Math.round(190_000 / 60.0)
                        && farm.getConstructionPoints() == 95_000 && farm.getTotalJobs() == 10
                        && farm.getJobs(JobType.NO_DIPLOMA) == 5 && farm.getJobs(JobType.DIPLOMA) == 3
                        && farm.getJobs(JobType.COLLEGE_ENGINEERING) == 2
                        && Math.round(farm.getLandSqFt() / 10.7639104) == 100_000);
        assertTrue("id 93 is the Strategic Reserve, the city's: the farm's tanks, ground and price, and no posts (star O8-2)",
                store != null && farm != null && store.getId() == 93 && !store.isOwnedBySector()
                        && store.getCategory() == BuildingType.HEAVY_INDUSTRY && store.getStock() == farm.getStock()
                        && store.getCashCost() == farm.getCashCost()
                        && store.getConstructionMaterials() == farm.getConstructionMaterials()
                        && store.getConstructionPoints() == farm.getConstructionPoints()
                        && store.getLandSqFt() == farm.getLandSqFt() && store.getTotalJobs() == 0
                        && StrategicReserve.isReserve(store) && !ham.citybuildersim.sectors.Refining.isTankFarm(store));

        /* ---------- the ports (0.7.86, batch O9; spec-oil 2.9) ----------

           Four sea terminals, the city's, a berth each of one kind of cargo,
           their capital split as the refiners' units are (cash the capital,
           material a sixtieth, points a half). PortCheck holds the rest. */
        System.out.println("\n--- the ports ---");
        String[] terminals = { "Tanker Terminal", "Bulk Terminal", "Container Terminal", "General Cargo Terminal" };
        boolean ports = true;
        int berths = 0;
        for (int i = 0; i < terminals.length; i++) {
            BuildingsTemplate t = null;
            for (BuildingsTemplate x : data) if (terminals[i].equals(x.getName())) t = x;
            ports &= t != null && t.getId() == 94 + i && t.getCategory() == BuildingType.PORTS && !t.isOwnedBySector()
                    && t.isPort() && t.berthCargo() == Ports.Cargo.values()[i] && t.goodsMade().isEmpty() && t.goodsUsed().isEmpty()
                    && t.getConstructionMaterials() == Math.round(t.getCashCost() / 60.0)
                    && t.getConstructionPoints() == t.getCashCost() / 2 && t.getStock() == 0;
        }
        for (BuildingsTemplate t : data) if (t.isPort()) berths++;
        assertTrue("ids 94-97 are the Tanker, Bulk, Container and General Cargo Terminals: the city's, PORTS, a berth each of its"
                + " kind, material a sixtieth of the capital and points a half", ports);
        assertTrue("...and the only buildings with a berth, bar FR3's Bulk Berth (id 142, a fifth)", berths == 5);

        /* ---------- the oil at sea (0.7.91, batch O10; spec-oil 2.7, 2.11) ----------

           The Oil sector's three buildings at sea, on none of the city's dry
           ground: the platform's jacket (D$18M [W13], 12 posts, 6 aboard on a
           two-on, two-off rota, est. [W14][W27], in O4-4's mix), its well
           (D$14M [W13], 415 t, one diploma's post) and a kilometre of pipe
           (D$4.5M at sea, D$3M on land, est. on [S2]); their capital split as
           the sector's own Oil Well's is (id 74: material a fortieth of the
           cash, points a fifth). WellCheck holds the rest. */
        System.out.println("\n--- the oil at sea ---");
        BuildingsTemplate jacket = null, platformWell = null, pipe = null, landWell = null;
        int atSea = 0;
        for (BuildingsTemplate t : data) {
            if (t.getId() == 74) landWell = t;
            if (t.getId() == 98) jacket = t;
            if (t.getId() == 99) platformWell = t;
            if (t.getId() == 100) pipe = t;
            if (t.standsAtSea()) atSea++;
        }
        boolean split = landWell != null;
        for (BuildingsTemplate t : new BuildingsTemplate[] { jacket, platformWell, pipe }) {
            split &= t != null && landWell != null && Sectors.OIL.equals(t.getSector()) && t.getCategory() == BuildingType.MINING
                    && t.getLandSqFt() == 0 && t.getElectricityConsumption() == 0 && t.getWaterConsumption() == 0
                    && t.getConstructionMaterials() == Math.round(t.getCashCost() * landWell.getConstructionMaterials() / landWell.getCashCost())
                    && t.getConstructionPoints() == Math.round(t.getCashCost() * landWell.getConstructionPoints() / landWell.getCashCost());
        }
        assertTrue("ids 98-100 are the oil at sea, the Oil sector's MINING, on no dry ground and with no power or water;"
                + " material and points to the cash as the Oil Well's, a fortieth and a fifth", split);
        assertTrue("id 98 is the Offshore Platform: a jacket of 12 slots, D$18M, 12 posts 6/4/2, lifting nothing itself",
                jacket != null && "Offshore Platform".equals(jacket.getName()) && jacket.isPlatform()
                        && jacket.platformSlots() == 12 && jacket.getCashCost() == 18_000 && jacket.getTotalJobs() == 12
                        && jacket.getJobs(JobType.NO_DIPLOMA) == 6 && jacket.getJobs(JobType.DIPLOMA) == 4
                        && jacket.getJobs(JobType.COLLEGE_ENGINEERING) == 2 && jacket.goodsMade().isEmpty()
                        && jacket.goodsUsed().isEmpty() && Game.siteOf(jacket) == Resource.OIL);
        assertTrue("id 99 is the Platform Well: D$14M, 415 t of crude a month, one diploma's post, on oil, and no land well",
                platformWell != null && "Platform Well".equals(platformWell.getName()) && platformWell.isPlatformWell()
                        && platformWell.getCashCost() == 14_000 && platformWell.makes(Good.CRUDE) == 415
                        && platformWell.getTotalJobs() == 1 && platformWell.getJobs(JobType.DIPLOMA) == 1
                        && Game.siteOf(platformWell) == Resource.OIL && !ham.citybuildersim.sectors.Oil.isLandWell(platformWell));
        assertTrue("id 100 is the Crude Pipeline, a kilometre: D$4.5M at sea and D$3M on land, no posts, on no site",
                pipe != null && "Crude Pipeline".equals(pipe.getName()) && pipe.isPipeline() && pipe.getCashCost() == 4_500
                        && pipe.onshoreCashPerKm() == 3_000 && pipe.getTotalJobs() == 0 && Game.siteOf(pipe) == null);
        assertTrue("...and the only buildings at sea", atSea == 3);

        /* ---------- the site each stands on (0.7.110, batch I1) ----------

           Until 0.7.110 Game.siteOf() was a rule on the good a MINING
           building makes (oil for a platform's jacket); now every building
           that stands on sites declares it (spec-materials 2.5), and the
           declaration must give the rule's answer for every building, so
           nothing reads a site differently. The oil's take one each, on
           ANY, a tonne of field a unit - but the jacket, which takes none of
           its field's (its wells do). Since 0.7.124 (batch I5; spec-materials
           2.4, the study's 2.4) iron's four stand on a type: the Iron Mine on
           one hematite site and the Large Open-Pit Mine on twenty, ore as
           dug; the Magnetite Mine & Concentrator on one magnetite site and
           the Large Magnetite Complex on twenty, three tonnes of rock a tonne
           of concentrate. Since 0.7.126 (batch I6; the study's 3.3, spec 2.4)
           coal's three mines do: the Surface Coal Mine on one thermal site and
           the Large Surface Mine on four, 1/.95 t of seam a tonne; the
           Underground Coal Mine on one coking site, 1/.80 - and the Coal
           Preparation Plant on none. Since 0.7.131 (batch I7; the study's 6.4,
           spec 2.4) stone's three: the Quarry on one site and the Large Quarry
           on three of one field, on ANY (packed onto limestone first), a
           tonne of stone a tonne; the Dimension Stone Quarry on one hard-rock
           site, three tonnes of rock a tonne of blocks - the one building on
           a site whose good is not its resource's (stone is priced as
           AGGREGATE, Resource.STONE), so the good rule gave it none. Since
           0.7.133 (batch I8; the study's 4.3, spec 2.4) copper's three: the
           Copper Mine & Mill on one sulphide site and the Large Copper Mine on
           eight of one field, .341 t of the field's copper a tonne of
           concentrate - which is not copper's pricing good, cathode, so the
           good rule gave them none either - and the Leach & SX-EW Plant on one
           oxide site, 1.4 t a tonne of cathode. */
        System.out.println("\n--- the site each building stands on (0.7.110; iron's by type and size, 0.7.124; coal's, 0.7.126; stone's, 0.7.131; copper's, 0.7.133) ---");
        boolean rule = true, shape = true;
        java.util.Map<Integer, String> ironSites = new java.util.TreeMap<>(), coalSites = new java.util.TreeMap<>(),
                stoneSites = new java.util.TreeMap<>(), copperSites = new java.util.TreeMap<>();
        int onSites = 0;
        for (BuildingsTemplate t : data) {
            Resource was = null;
            if (t.getCategory() == BuildingType.MINING) {
                for (Resource r : Resource.values()) if (r.good() != null && t.makes(r.good()) > 0 && was == null) was = r;
                if (was == null && t.isPlatform()) was = Resource.OIL;
            }
            // ...but the Dimension Stone Quarry (0.7.131), whose blocks are not stone's pricing good: the rule gave it no site.
            boolean blocks = t.getId() == ham.citybuildersim.sectors.Quarrying.DIMENSION_STONE_QUARRY;
            // ...and copper's two mines (0.7.133), whose concentrate is not copper's pricing good, cathode: the rule gave them none.
            boolean concentrate = t.makes(Good.COPPER_CONC) > 0;
            // ...and Forestry's crews (0.7.138), whose pulpwood is forest's pricing good: they cut the forest, on no site.
            boolean crew = ham.citybuildersim.sectors.Forestry.isCrew(t);
            rule &= blocks ? was == null && Game.siteOf(t) == Resource.STONE
                    : concentrate ? was == null && Game.siteOf(t) == Resource.COPPER
                    : crew ? was == Resource.FOREST && Game.siteOf(t) == null : Game.siteOf(t) == was;
            BuildingsTemplate.Site site = t.site();
            if (site == null) continue;
            onSites++;
            if (site.resource() == Resource.IRON) {
                ironSites.put(t.getId(), site.type() + " " + site.sites() + " " + site.dig());
                continue;
            }
            if (site.resource() == Resource.COAL) {
                coalSites.put(t.getId(), site.type() + " " + site.sites() + " " + site.dig());
                continue;
            }
            if (site.resource() == Resource.STONE) {
                stoneSites.put(t.getId(), (site.any() ? "ANY" : site.type().name()) + " " + site.sites() + " " + site.dig());
                continue;
            }
            if (site.resource() == Resource.COPPER) {
                copperSites.put(t.getId(), site.type() + " " + site.sites() + " " + site.dig());
                continue;
            }
            shape &= site.any() && (t.isPlatform() ? site.sites() == 0 && site.dig() == 0 : site.sites() == 1 && site.dig() == 1);
        }
        assertTrue("every building's site is the one the good rule gave it (Game.siteOf(), to 0.7.109) - but the Dimension Stone"
                + " Quarry's (0.7.131) and copper's two mines' (0.7.133), which the rule gave none, and Forestry's two crews (0.7.138),"
                + " which the rule gave the forest and which cut it on no site", rule);
        assertTrue("...sixteen stand on sites - iron's four mines, the Oil Well, the Offshore Platform, the Platform Well, coal's three"
                + " mines, stone's three quarries and copper's three (thirteen until 0.7.133, ten until 0.7.131, seven until 0.7.126)",
                onSites == 16);
        assertTrue("...oil's on ANY, one site and a tonne of field a unit, but the jacket, which takes none", shape);
        java.util.Map<Integer, String> byType = new java.util.TreeMap<>();
        byType.put(ham.citybuildersim.sectors.Mining.IRON_MINE, "HEMATITE 1 1.0");
        byType.put(ham.citybuildersim.sectors.Mining.LARGE_OPEN_PIT, "HEMATITE 20 1.0");
        byType.put(ham.citybuildersim.sectors.Mining.MAGNETITE_MINE, "MAGNETITE 1 3.0");
        byType.put(ham.citybuildersim.sectors.Mining.LARGE_MAGNETITE, "MAGNETITE 20 3.0");
        assertTrue("...iron's on a type, its sites and its dig (0.7.124): the Iron Mine one hematite site and the open pit twenty, ore as"
                + " dug; the magnetite works one and twenty, three tonnes of rock a tonne: " + ironSites, ironSites.equals(byType));
        java.util.Map<Integer, String> coalByType = new java.util.TreeMap<>();
        coalByType.put(ham.citybuildersim.sectors.CoalMining.SURFACE_MINE, "THERMAL 1 " + (1 / .95));
        coalByType.put(ham.citybuildersim.sectors.CoalMining.LARGE_SURFACE_MINE, "THERMAL 4 " + (1 / .95));
        coalByType.put(ham.citybuildersim.sectors.CoalMining.UNDERGROUND_MINE, "COKING 1 " + (1 / .80));
        assertTrue("...coal's on a type, its sites and its dig (0.7.126): the surface mines one and four thermal sites at 1/.95 t of"
                + " seam a tonne, the underground mine one coking site at 1/.80; the preparation plant on none: " + coalSites,
                coalSites.equals(coalByType) && byId(data, ham.citybuildersim.sectors.CoalMining.PREPARATION_PLANT).site() == null);
        java.util.Map<Integer, String> stoneByType = new java.util.TreeMap<>();
        stoneByType.put(ham.citybuildersim.sectors.Quarrying.QUARRY, "ANY 1 1.0");
        stoneByType.put(ham.citybuildersim.sectors.Quarrying.LARGE_QUARRY, "ANY 3 1.0");
        stoneByType.put(ham.citybuildersim.sectors.Quarrying.DIMENSION_STONE_QUARRY, "HARD_ROCK 1 3.0");
        assertTrue("...stone's, its sites and its dig (0.7.131): the Quarry one site and the Large Quarry three, on ANY, a tonne a tonne;"
                + " the Dimension Stone Quarry one hard-rock site, three tonnes of rock a tonne of blocks; the cement plants and the kiln"
                + " on none: " + stoneSites,
                stoneSites.equals(stoneByType) && byId(data, ham.citybuildersim.sectors.CementConcrete.CEMENT_PLANT).site() == null
                        && byId(data, ham.citybuildersim.sectors.CementConcrete.LARGE_CEMENT_PLANT).site() == null
                        && byId(data, ham.citybuildersim.sectors.CementConcrete.LIME_KILN).site() == null);
        java.util.Map<Integer, String> copperByType = new java.util.TreeMap<>();
        copperByType.put(ham.citybuildersim.sectors.CopperMining.MINE_AND_MILL, "SULPHIDE 1 " + ham.citybuildersim.sectors.CopperMining.CONC_DIG);
        copperByType.put(ham.citybuildersim.sectors.CopperMining.LARGE_MINE, "SULPHIDE 8 " + ham.citybuildersim.sectors.CopperMining.CONC_DIG);
        copperByType.put(ham.citybuildersim.sectors.CopperMining.LEACH_PLANT, "OXIDE 1 " + ham.citybuildersim.sectors.CopperMining.LEACH_DIG);
        boolean worksOnNone = true;
        for (int id = ham.citybuildersim.sectors.CopperRefining.SMELTER; id <= ham.citybuildersim.sectors.Manufacturing.WIRE_CABLE_WORKS; id++) {
            worksOnNone &= byId(data, id).site() == null;
        }
        assertTrue("...copper's, its sites and its dig (0.7.133): the Copper Mine & Mill one sulphide site and the Large Copper Mine"
                + " eight, .341 t of copper a tonne of concentrate; the Leach & SX-EW Plant one oxide site, 1.4 t a tonne of cathode;"
                + " the four works and the wire works on none: " + copperSites, copperSites.equals(copperByType) && worksOnNone);

        /* ---------- a works' own power and its consumables (0.7.112, batch I2) ----------

           spec-materials 2.5 and 2.8: "gen" (kW a building makes itself) and
           "consumables" (world thousands a month it buys abroad) are read
           from the file like every field, and the shared machinery uses them.
           Since 0.7.123 (batch I4; the study's 1.6, Jerus's Q20) three works
           make power from their gas - the Coke Ovens 8,000 kW, the Blast
           Furnace 60,000 and the Oxygen Steel Shop 25,000 - and every other
           building buys its whole draw and sells none, to the bit. Since
           0.7.120 (batch I3) the iron rows buy consumables - the Iron Mine and
           the two arc furnaces, and since 0.7.123 Iron & Steel's seven new
           works, the study's running cost less their wages, the power they
           buy, water and diesel (MaterialsCheck 9 and 11) - and every other
           pays the world nothing past its goods. */
        System.out.println("\n--- a works' own power and its consumables (0.7.112; the iron rows' consumables, 0.7.120; the gen, 0.7.123) ---");
        boolean wholeDraw = true;
        java.util.Map<Integer, Double> making = new java.util.TreeMap<>();
        java.util.Set<Integer> buying = new java.util.TreeSet<>();
        for (BuildingsTemplate t : data) {
            if (t.getGen() != 0) making.put(t.getId(), t.getGen());
            else wholeDraw &= t.powerBought() == t.getElectricityConsumption() && t.powerSold() == 0;
            if (t.getConsumables() != 0) buying.add(t.getId());
        }
        int ovens = ham.citybuildersim.sectors.HeavyIndustry.COKE_OVENS, furnace = ham.citybuildersim.sectors.HeavyIndustry.BLAST_FURNACE,
                shop = ham.citybuildersim.sectors.HeavyIndustry.OXYGEN_STEEL_SHOP;
        int pulp = ham.citybuildersim.sectors.WoodProducts.PULP_MILL;
        assertTrue("four works make power, the study's 1.6: the Coke Ovens 8,000 kW, the Blast Furnace 60,000, the Oxygen Steel Shop"
                + " 25,000 - and since 0.7.140 the Pulp Mill 13,500, from its liquor and bark: " + making,
                making.equals(java.util.Map.of(ovens, 8000.0, furnace, 60000.0, shop, 25000.0, pulp, 13500.0)));
        BuildingsTemplate ovensT = byId(data, ovens), furnaceT = byId(data, furnace), shopT = byId(data, shop);
        assertTrue("...so the ovens sell the grid 5,800 kW and the furnace 8,600, buying none, and the shop buys 26,600 and sells none"
                        + " (the study's 1.6)",
                ovensT.powerSold() == 5800 && ovensT.powerBought() == 0 && furnaceT.powerSold() == 8600 && furnaceT.powerBought() == 0
                        && shopT.powerBought() == 26600 && shopT.powerSold() == 0);
        BuildingsTemplate pulpT = byId(data, pulp);
        assertTrue("...and the pulp mill draws nothing from the grid - its boiler meets its own draw - and sells it all 13,500 kW (0.7.140)",
                pulpT.getElectricityConsumption() == 0 && pulpT.powerBought() == 0 && pulpT.powerSold() == 13500);
        assertTrue("...and every other building buys its whole draw and sells the grid none, to the bit", wholeDraw);
        java.util.Set<Integer> iron = new java.util.TreeSet<>(List.of(
                ham.citybuildersim.sectors.HeavyIndustry.ARC_FURNACE, ham.citybuildersim.sectors.HeavyIndustry.MINI_MILL,
                ham.citybuildersim.sectors.Mining.IRON_MINE));
        for (int id = ham.citybuildersim.sectors.HeavyIndustry.SCRAP_YARD; id <= ham.citybuildersim.sectors.HeavyIndustry.LARGE_MINI_MILL; id++) iron.add(id);
        for (int id = ham.citybuildersim.sectors.Mining.LARGE_OPEN_PIT; id <= ham.citybuildersim.sectors.Mining.LARGE_MAGNETITE; id++) iron.add(id);
        for (int id = ham.citybuildersim.sectors.CoalMining.SURFACE_MINE; id <= ham.citybuildersim.sectors.CoalMining.PREPARATION_PLANT; id++) iron.add(id);
        for (int id = ham.citybuildersim.sectors.Quarrying.QUARRY; id <= ham.citybuildersim.sectors.CementConcrete.LIME_KILN; id++) iron.add(id);
        for (int id = ham.citybuildersim.sectors.CopperMining.MINE_AND_MILL; id <= ham.citybuildersim.sectors.Manufacturing.WIRE_CABLE_WORKS; id++) iron.add(id);
        for (int id = ham.citybuildersim.sectors.Forestry.LOGGING_CAMP; id <= ham.citybuildersim.sectors.Forestry.WOODLOT; id++) iron.add(id);
        for (int id = ham.citybuildersim.sectors.WoodProducts.SAWMILL; id <= ham.citybuildersim.sectors.WoodProducts.PELLET_MILL; id++) iron.add(id);
        assertTrue("forty-one buy consumables, the materials chains' rows: the Electric Arc Furnace (11), the Steel Mini-Mill (12) and"
                + " the Iron Mine (14) of batch I3, Iron & Steel's seven of batch I4 (101-107), Iron Mining's three of batch I5 (108-110),"
                + " Coal Mining's four of batch I6 (111-114), stone's six of batch I7 (115-120), copper's eight of batch I8 (121-128,"
                + " the wire works' insulation among them), Forestry's four of batch I9 (129-132: the crews' fuel and wear, the"
                + " stands' seedlings) and Wood Products' six of batch I10 (133-138: the panel mill's resin among them); every other"
                + " none: " + buying,
                buying.equals(iron));

        /* ---------- the old steel a yard takes, and the year each row first existed (0.7.123, batch I4) ----------

           spec-materials 2.5 and 2.9: "oldMetal" is the city's old steel a
           Scrap Yard takes a month at nameplate (OldMetal), the one feed no
           market sells; research-design.md 2.1 (Jerus's start year 1900, the
           orchestrator's recommendation on its Q3): "since" is the year a row
           first existed, written on every row the materials chains add - the
           start year for one that stood before it - and on none before them,
           which read as undated (0). Read by nothing yet. */
        System.out.println("\n--- the old steel a yard takes, and the year each new row first existed (0.7.123) ---");
        java.util.Map<Integer, Double> takers = new java.util.TreeMap<>(), copperTakers = new java.util.TreeMap<>();
        java.util.Map<Integer, Integer> dated = new java.util.TreeMap<>();
        for (BuildingsTemplate t : data) {
            if (t.oldMetal(Good.STEEL) > 0) takers.put(t.getId(), t.oldMetal(Good.STEEL));
            if (t.oldMetal(Good.COPPER) > 0) copperTakers.put(t.getId(), t.oldMetal(Good.COPPER));
            if (!t.oldMetalTaken().keySet().stream().allMatch(m -> m == Good.STEEL || m == Good.COPPER)) takers.put(-t.getId(), 0.0);
            if (t.getSince() != 0) dated.put(t.getId(), t.getSince());
        }
        BuildingsTemplate yard = byId(data, ham.citybuildersim.sectors.HeavyIndustry.SCRAP_YARD);
        assertTrue("one building takes the city's old steel, the Scrap Yard: 10,500 t a month into 10,000 t of scrap (the study's 2.4): "
                + takers, takers.equals(java.util.Map.of(ham.citybuildersim.sectors.HeavyIndustry.SCRAP_YARD, 10500.0))
                && yard.makes(Good.SCRAP) == 10000 && yard.goodsUsed().isEmpty());
        BuildingsTemplate scrapRefinery = byId(data, ham.citybuildersim.sectors.CopperRefining.SCRAP_REFINERY);
        assertTrue("...and one its old copper (0.7.133), the Scrap Copper Refinery: 4,400 t a month at nameplate, its whole feed - its"
                + " COPPER_SCRAP bought on the market past it - into 4,000 t of cathode; no building takes any other metal: " + copperTakers,
                copperTakers.equals(java.util.Map.of(ham.citybuildersim.sectors.CopperRefining.SCRAP_REFINERY, 4400.0))
                && scrapRefinery.uses(Good.COPPER_SCRAP) == 4400 && scrapRefinery.makes(Good.COPPER) == 4000);
        java.util.Map<Integer, Integer> years = new java.util.TreeMap<>();
        years.put(ham.citybuildersim.sectors.HeavyIndustry.SCRAP_YARD, 1900);
        years.put(ham.citybuildersim.sectors.HeavyIndustry.PELLETISING_PLANT, 1955);
        years.put(ham.citybuildersim.sectors.HeavyIndustry.COKE_OVENS, 1900);
        years.put(ham.citybuildersim.sectors.HeavyIndustry.MINI_BLAST_FURNACE, 1900);
        years.put(ham.citybuildersim.sectors.HeavyIndustry.BLAST_FURNACE, 1900);
        years.put(ham.citybuildersim.sectors.HeavyIndustry.OXYGEN_STEEL_SHOP, 1952);
        years.put(ham.citybuildersim.sectors.HeavyIndustry.LARGE_MINI_MILL, 1969);
        years.put(ham.citybuildersim.sectors.Mining.LARGE_OPEN_PIT, 1900);
        years.put(ham.citybuildersim.sectors.Mining.MAGNETITE_MINE, 1955);
        years.put(ham.citybuildersim.sectors.Mining.LARGE_MAGNETITE, 1955);
        for (int id = ham.citybuildersim.sectors.CoalMining.SURFACE_MINE; id <= ham.citybuildersim.sectors.CoalMining.PREPARATION_PLANT; id++) {
            years.put(id, 1900);
        }
        for (int id = ham.citybuildersim.sectors.Quarrying.QUARRY; id <= ham.citybuildersim.sectors.CementConcrete.LIME_KILN; id++) {
            years.put(id, 1900);
        }
        years.put(ham.citybuildersim.sectors.CementConcrete.LARGE_CEMENT_PLANT, 1902);
        for (int id = ham.citybuildersim.sectors.CopperMining.MINE_AND_MILL; id <= ham.citybuildersim.sectors.Manufacturing.WIRE_CABLE_WORKS; id++) {
            years.put(id, 1900);
        }
        years.put(ham.citybuildersim.sectors.CopperMining.LARGE_MINE, 1906);
        years.put(ham.citybuildersim.sectors.CopperMining.LEACH_PLANT, 1968);
        for (int id = ham.citybuildersim.sectors.Forestry.LOGGING_CAMP; id <= ham.citybuildersim.sectors.Forestry.WOODLOT; id++) {
            years.put(id, 1900);
        }
        years.put(ham.citybuildersim.sectors.Forestry.HARVESTING_OPERATION, 1973);
        for (int id = ham.citybuildersim.sectors.WoodProducts.SAWMILL; id <= ham.citybuildersim.sectors.WoodProducts.PELLET_MILL; id++) {
            years.put(id, 1900);
        }
        years.put(ham.citybuildersim.sectors.WoodProducts.PANEL_MILL, 1980);
        years.put(ham.citybuildersim.sectors.WoodProducts.PELLET_MILL, 1970);
        years.put(144, 1900);   // HY1's Hydro Station: it stood before the start (Appleton 1882, Niagara 1895)
        assertTrue("the seven rows of batch I4 are dated, research-design.md's years - the Pelletising Plant 1955 (E4, Silver Bay), the"
                        + " Oxygen Steel Shop 1952 (E1, Linz), the Large Mini-Mill 1969 (E13) and the other four 1900, the start year,"
                        + " as standing before it - and batch I5's three: the Large Open-Pit Mine 1900, the two magnetite works 1955 (E4,"
                        + " taconite concentrated at Silver Bay); and batch I6's four 1900, coal's mines and washeries standing before"
                        + " the start (the longwall's shields, E15 1975, equipment on the mine); and batch I7's six 1900 but the Large"
                        + " Cement Plant, 1902 (Edison's long kilns, research-1900.md 1.2); and batch I8's eight 1900 but the Large Copper"
                        + " Mine, 1906 (Bingham Canyon, research-1900.md 1.2), and the Leach & SX-EW Plant, 1968 (E12, Bluebird); and"
                        + " batch I9's four 1900 but the Harvesting Operation, 1973 (E14, PIKA 75) - the Tree Plantation and the Woodlot"
                        + " available from the start (research-1900.md 12); and batch I10's six 1900 but the Panel Mill, 1980 (OSB about"
                        + " 1980), and the Wood Pellet Mill, 1970 (the 1970s) - research-1900.md 1.2's words, both unverified; and HY1's"
                        + " Hydro Station, 1900 (standing before it: Appleton 1882, Niagara 1895); no row"
                        + " before them: " + dated,
                dated.equals(years));

        /* ---------- the raw coal a preparation plant washes (0.7.126, batch I6) ----------

           spec-materials 2.5 and 2.10: "wash" is the raw coal a Coal
           Preparation Plant washes a month at nameplate, read from the file
           like every field (compared above, json against the twin); the plant
           buys and makes no good of its own - its coal is Coal Mining's flow
           (sectors.CoalMining.flow()) - and no other building washes any. */
        System.out.println("\n--- the raw coal a preparation plant washes (0.7.126) ---");
        java.util.Map<Integer, Double> washers = new java.util.TreeMap<>();
        for (BuildingsTemplate t : data) if (t.getWash() != 0) washers.put(t.getId(), t.getWash());
        BuildingsTemplate plant = byId(data, ham.citybuildersim.sectors.CoalMining.PREPARATION_PLANT);
        assertTrue("one building washes raw coal, the Coal Preparation Plant: 500,000 t a month (the study's 3.3), buying and making no"
                        + " good of its own: " + washers,
                washers.equals(java.util.Map.of(ham.citybuildersim.sectors.CoalMining.PREPARATION_PLANT, 500000.0))
                        && plant.goodsMade().isEmpty() && plant.goodsUsed().isEmpty()
                        && Sectors.COAL_MINING.equals(plant.getSector()));

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
