package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The fourteen fixed forms, geometry and capital partitions of the RES1 catalogue.
 * WHY (RES3): mixed homes need one actual project quote, partitioned without charging
 * optional parking twice. Constants are the study's disclosed starting recipes.
 */
public final class HousingForms {
    private HousingForms() { }
    /** Square feet in a square metre, used for site geometry only. */
    public static final double SQFT_PER_M2 = 10.7639104167;
    /** Area including access for one surface parking bay. */
    public static final double BAY_SQFT = 350;
    public record Form(String code, int id, String name, int floors, double grossArea,
                       double plot, int since, double cash, int points, int materials,
                       int staff, int power, double water, int roadLoad, HousingRecipe recipe,
                       double[] structureShares, double[] landShares) {
        public double footprint() { return grossArea / floors * SQFT_PER_M2; }
        public double garden() { return floors <= 3 && recipe.doors() <= 6
                ? Math.max(0, plot-footprint()-recipe.mix().stream().mapToDouble(d -> d.count()*d.privateParking()*BAY_SQFT).sum()) / recipe.doors() / SQFT_PER_M2 : 0; }
        /** Only explicit ground-level homes have step-free layouts in this catalogue. */
        public boolean stepFree() { return floors == 1; }
        public double homeCapital(String dwelling, double structure, double land) {
            for (int i=0;i<recipe.mix().size();i++) if (recipe.mix().get(i).id().equals(dwelling))
                return structure*structureShares[i]+land*landShares[i];
            throw new IllegalArgumentException("Unknown dwelling "+dwelling);
        }
    }
    private static void add(List<Form> out, Form form) { out.add(form); }
    public static final List<Form> ALL = catalogue();
    private static List<Form> catalogue() {
        List<Form> out = new ArrayList<>();
        add(out, new Form("C01", 155, "Compact house", 1, 68.75, 4000, 1900,
                149.84507275132276, 5, 5, 0, 1, 0.09166666666666666, 2,
                new HousingRecipe("C01", List.of(new HousingRecipe.Dwelling("H55", 1, 2, 55, 1, 1, 1.0, 0, 439845.07275132276)), 0),
                new double[] {1.0}, new double[] {1.0}));
        add(out, new Form("C02", 0, "House", 2, 150.0, 8000, 1900,
                266.0, 10, 10, 0, 1, 0.2, 2,
                new HousingRecipe("C02", List.of(new HousingRecipe.Dwelling("H120", 1, 6, 120, 3, 1, 1.0, 0, 846000.0)), 0),
                new double[] {1.0}, new double[] {1.0}));
        add(out, new Form("C03", 156, "Large house", 2, 300.0, 12000, 1900,
                626.5059523809523, 24, 20, 0, 3, 0.4, 2,
                new HousingRecipe("C03", List.of(new HousingRecipe.Dwelling("H240", 1, 6, 240, 4, 1.25, 2.0, 0, 1586505.9523809524)), 0),
                new double[] {1.0000000000000002}, new double[] {1.0}));
        add(out, new Form("C04", 157, "Duplex", 2, 200.0, 10000, 1900,
                344.7412698412698, 86, 15, 0, 1, 0.22857142857142856, 3,
                new HousingRecipe("C04", List.of(new HousingRecipe.Dwelling("D90", 2, 4, 90, 2, 1, 1.0, 0, 557370.6349206349)), 0),
                new double[] {0.5}, new double[] {0.5}));
        add(out, new Form("C05", 158, "Terrace", 3, 666.6666666666666, 18000, 1900,
                1068.9179894179892, 277, 44, 0, 4, 0.7619047619047619, 14,
                new HousingRecipe("C05", List.of(new HousingRecipe.Dwelling("T100", 6, 6, 100, 3, 1, 1.0, 0, 460152.99823633156)), 0),
                new double[] {0.16666666666666669}, new double[] {0.16666666666666666}));
        add(out, new Form("C06", 1, "Studio Apartments", 4, 4000.0, 25000, 1900,
                8692.0, 700, 322, 2, 8, 2, 64,
                new HousingRecipe("C06", List.of(new HousingRecipe.Dwelling("S", 80, 2, 40, 0, 1, 0.0, 0, 196725.0)), 20),
                new double[] {0.012413721700717835}, new double[] {0.009}));
        add(out, new Form("C07", 159, "Mixed walk-up", 3, 2160, 22000, 1900,
                3824.79619047619, 890, 140, 1, 9, 2.107936507936508, 35,
                new HousingRecipe("C07", List.of(new HousingRecipe.Dwelling("S", 8, 2, 40, 0, 1, 0.0, 0, 209832.77299674888), new HousingRecipe.Dwelling("F", 12, 4, 75, 2, 1, 0.0, 0, 325773.25492445973), new HousingRecipe.Dwelling("L", 4, 6, 110, 3, 1, 0.0, 1, 464213.73685217055)), 12),
                new double[] {0.029007967307585827, 0.0437255781118577, 0.059231236293838134}, new double[] {0.019496166484118293, 0.036555312157721793, 0.0695235487404162}));
        add(out, new Form("C08", 6, "Low-Rise Apartments", 3, 5906.25, 60000, 1900,
                10284.0, 2432, 381, 2, 25, 6, 101,
                new HousingRecipe("C08", List.of(new HousingRecipe.Dwelling("F", 63, 4, 75, 2, 1, 0.0, 0, 319714.28571428574)), 32),
                new double[] {0.01572486031708993}, new double[] {0.01291005291005291}));
        add(out, new Form("C09", 160, "Mixed mid-rise", 8, 5650.0, 24000, 1900,
                10296.625726984128, 2402, 376, 2, 26, 5.73968253968254, 96,
                new HousingRecipe("C09", List.of(new HousingRecipe.Dwelling("S", 16, 2, 40, 0, 1, 0.0, 0, 193573.13819356653), new HousingRecipe.Dwelling("F", 40, 4, 75, 2, 1, 0.0, 0, 295286.43966849276), new HousingRecipe.Dwelling("L", 8, 6, 110, 3, 1, 0.0, 1, 419499.74114341906)), 32),
                new double[] {0.010731595479874931, 0.016156627051879677, 0.021874662413126255}, new double[] {0.00471976401179941, 0.008849557522123894, 0.02756268436578171}));
        add(out, new Form("C10", 161, "Mixed tower", 20, 15200, 24000, 1931,
                30997.54681904763, 7768, 1255, 6, 69, 13.968253968253968, 230,
                new HousingRecipe("C10", List.of(new HousingRecipe.Dwelling("S", 48, 2, 40, 0, 1, 0.0, 0, 225822.29584992788), new HousingRecipe.Dwelling("F", 96, 4, 75, 2, 1, 0.0, 0, 355753.61027417035), new HousingRecipe.Dwelling("L", 12, 6, 110, 3, 1, 0.0, 1, 555684.9246984129), new HousingRecipe.Dwelling("P140", 4, 6, 140, 3, 1.4, 0.0, 1, 781877.733887446)), 80),
                new double[] {0.003828646067493722, 0.0059160449836866014, 0.009309717542828156, 0.013241636665933644}, new double[] {0.003636363636363636, 0.006818181818181818, 0.01, 0.012727272727272726}));
        add(out, new Form("C11", 162, "Large apartment block", 12, 29550.0, 100000, 1900,
                56756.00716190477, 12957, 2025, 12, 135, 30.01904761904762, 499,
                new HousingRecipe("C11", List.of(new HousingRecipe.Dwelling("S", 120, 2, 40, 0, 1, 0.0, 0, 195480.69457739106), new HousingRecipe.Dwelling("F", 216, 4, 75, 2, 1, 0.0, 0, 298863.10788816377), new HousingRecipe.Dwelling("L", 24, 6, 110, 3, 1, 0.0, 1, 424745.5211989365)), 180),
                new double[] {0.001999992557465876, 0.003024032895824262, 0.004101717845214153}, new double[] {0.0006260575296108291, 0.0011738578680203044, 0.00522165820642978}));
        add(out, new Form("C12", 163, "Large high-rise", 40, 40000, 40000, 1931,
                87945.15682539683, 23336, 3769, 14, 199, 35.25079365079365, 582,
                new HousingRecipe("C12", List.of(new HousingRecipe.Dwelling("S", 120, 2, 40, 0, 1, 0.0, 0, 253728.87836786974), new HousingRecipe.Dwelling("F", 232, 4, 75, 2, 1, 0.0, 0, 408078.4524953114), new HousingRecipe.Dwelling("L", 44, 6, 110, 3, 1, 0.0, 1, 639428.0266227529), new HousingRecipe.Dwelling("P180", 4, 6, 180, 3, 1.5, 0.0, 1, 1132664.3177347789)), 240),
                new double[] {0.0014734490634859764, 0.002328385975849129, 0.003677586995530833, 0.006572007394041577}, new double[] {0.0014409221902017292, 0.002701729106628242, 0.003962536023054755, 0.00648414985590778}));
        add(out, new Form("C13", 164, "Very tall tower", 80, 80000, 50000, 1969,
                205984.1381587302, 57073, 9172, 28, 503, 70.34920634920636, 1152,
                new HousingRecipe("C13", List.of(new HousingRecipe.Dwelling("S", 240, 2, 40, 0, 1, 0.0, 0, 295860.11563807237), new HousingRecipe.Dwelling("F", 480, 4, 75, 2, 1, 0.0, 0, 487074.5223769412), new HousingRecipe.Dwelling("L", 76, 6, 110, 3, 1, 0.0, 1, 769288.9291158101), new HousingRecipe.Dwelling("P360", 4, 6, 360, 4, 1.6, 0.0, 1, 2577995.262964873)), 480),
                new double[] {0.000721605511864539, 0.0011706691434869372, 0.0018649628228600305, 0.006266073660285089}, new double[] {0.0007220216606498196, 0.0013537906137184115, 0.0019855595667870036, 0.006498194945848376}));
        add(out, new Form("C14", 165, "Large-home luxury tower", 36, 43200, 30000, 1931,
                85385.83090793651, 23249, 3704, 12, 263, 35.403174603174605, 288,
                new HousingRecipe("C14", List.of(new HousingRecipe.Dwelling("A", 61, 6, 160, 3, 1.25, 0.0, 1, 876426.2327617226), new HousingRecipe.Dwelling("V", 48, 6, 240, 4, 1.4, 0.0, 1, 1314789.5237457585), new HousingRecipe.Dwelling("X", 8, 6, 600, 5, 1.6, 0.0, 1, 3312029.2855548724), new HousingRecipe.Dwelling("P600", 3, 6, 600, 5, 1.75, 0.0, 1, 3496566.428412015)), 120),
                new double[] {0.005707156982331361, 0.008561723088552957, 0.021569083688794725, 0.02278268210562911}, new double[] {0.005738880918220946, 0.00860832137733142, 0.021520803443328552, 0.021520803443328552}));
        return List.copyOf(out);
    }
    public static Form of(int id) { return ALL.stream().filter(f -> f.id()==id).findFirst().orElse(null); }
    /** The built-in fallback carries exactly the same physical definitions as buildings.json. */
    static void installFallback(List<BuildingsTemplate> templates) {
        for (Form f : ALL) {
            BuildingsTemplate t = templates.stream().filter(b -> b.getId()==f.id()).findFirst().orElse(null);
            if (t == null) { t=new BuildingsTemplate(f.name(), BuildingType.RESIDENTIAL); templates.add(t); }
            t.setId(f.id()).setCapacity((int)f.recipe().capacity()).setDwellings((int)f.recipe().doors())
                    .setCashCost(f.cash()).setConstructionPoints(f.points()).setConstructionMaterials(f.materials())
                    .setSector("Real Estate").setElectricityConsumption(f.power()).setWaterConsumption(f.water())
                    .setLandSqFt(f.plot()).setRoadLoad(f.roadLoad()).makes(Good.HOUSING,f.recipe().doors())
                    .setSince(f.since()).setHousingRecipe(f.recipe());
            t.setJobs(JobType.DIPLOMA, f.staff());
        }
    }
}
