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
            check(who + " production1", actual.getProduction1(), expected.getProduction1());
            check(who + " production2", actual.getProduction2(), expected.getProduction2());
            check(who + " stock", actual.getStock(), expected.getStock());
            check(who + " refinery feed", actual.feedPerMonth(), expected.feedPerMonth());
            // ...and a filling station's litres at the pump (0.7.83): one that lost its line sells nothing.
            check(who + " pump", actual.pumpLitres(), expected.pumpLitres());
            // ...and a sea terminal's berth (0.7.86): one that lost its line takes nothing off the lorries.
            if (actual.berthCargo() != expected.berthCargo()) {
                fails++;
                System.out.println("  FAIL " + who + ": berth " + actual.berthCargo() + " != " + expected.berthCargo());
            }
            check(who + " berth tonnes", actual.berthTonnesAYear(), expected.berthTonnesAYear());
            // ...and the oil at sea (0.7.91): a platform that lost its line would stand on the land and hold no well.
            if (actual.offshore() != expected.offshore()) {
                fails++;
                System.out.println("  FAIL " + who + ": offshore " + actual.offshore() + " != " + expected.offshore());
            }
            check(who + " platform slots", actual.platformSlots(), expected.platformSlots());
            check(who + " onshore cash a km", actual.onshoreCashPerKm(), expected.onshoreCashPerKm());

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
        assertTrue("lubricants on every car, van, machinery and fabrication plant at the spec's rate a unit made, and on"
                + " nothing else (" + oiled + " plants)", lubricated && oiled == 6);
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
        assertTrue("...and 101 buildings in all (0.7.91; 98 from 0.7.86, 94 from 0.7.85, 92 from 0.7.83, 91 from 0.7.80, 76"
                        + " until then)",
                data.size() == 101);

        /* ---------- the oil storage (0.7.85, batch O8; spec-oil 2.8) ----------

           The refiners' Tank Farm and the city's Strategic Reserve: 500,000 m3 of
           tanks [4.5] on 10 ha [P13], D$190M (the research's US$60 a barrel)
           split as the refiners' units are, cash the capital and material a
           sixtieth, ten posts the farm's and none the reserve's. */
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
        assertTrue("...and the only buildings with a berth", berths == 4);

        /* ---------- the oil at sea (0.7.91, batch O10; spec-oil 2.7, 2.11) ----------

           The Oil sector's three buildings at sea, on none of the city's dry
           ground: the platform's jacket (D$18M [W13], 12 posts, 6 aboard on a
           two-on, two-off rota, est. [W14][W27], in O4-4's mix), its well
           (D$14M [W13], 415 t, one diploma's post) and a kilometre of pipe
           (D$4.5M at sea, D$3M on land, est. on [S2]); their capital split as
           the sector's own Oil Well's is (id 74: material a fortieth of the
           cash, points a fifth). WellCheck holds the rest. */
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
