package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The district plan: one street network, + junctions no nearer than eight plots, every building within reach and none on a street or beside a highway, and the model's road drawn exactly as the streets' surface - on the prototype's test district, MapCheck's fixture of Jerus's city, a city played as the playtest plays it, and any save named - with the prototype's own figures where they apply, how full a district it can draw, and what a plan costs.
 *
 * WHY THIS EXISTS (0.7.87, batch RD1; the project's spec-roads-and-ports.md
 * 2.4, 2.5, 2.9 and 2.10). The map's road checks until 0.7.86 (MapCheck 8)
 * passed on the city Jerus's screenshot showed - a maze of road with a shop in
 * each hole beside a tile with no road - because they measured an
 * intermediate list, the road tiles, not what was drawn. DistrictPlan is what
 * batch RD2 paints from, so its rules are held on the plan itself, plot by
 * plot, after every plan: the spec's 2.4 checks and H5.
 *
 * What it has to prove:
 *   1. the port: on the prototype's own test district (proto2.py's ground,
 *      Jerus's mix at 70% of its dry ground, his roads, the same trips paved
 *      and half that), with the prototype's own hashes and rules, the plan is
 *      the prototype's - every building's box in order, every street plot,
 *      width and kind - and so is every figure of the spec's 2.5 table;
 *   2. the same district with the game's rules and hashes: the checks hold,
 *      and its figures stand beside the table's (the water rule, the streets
 *      along a cut, other hashes); a building wider than an estate's strip
 *      takes a whole estate cell, its spine closed;
 *   3. the highways (H5): with a straight highway across the district no
 *      building plot touches a highway plot, corners included, and the
 *      streets pass beneath it in one network;
 *   4. how full a district the plan can draw (spec 2.10): flat ground, every
 *      plot owned, Jerus's mix and the same city built paved at 75% to 92%
 *      of the ground - what has no place, and the street share by cell kind;
 *   5. MapCheck's fixture of Jerus's city (his city x 1 on the design's square
 *      city, at his density): every district's plan keeps the checks; and the
 *      dense screen's districts (x 10,000), each planned within PLAN_MS, half
 *      the screen's SCREEN_MS;
 *   6. a city played as the playtest plays it (MapCheck 1's), every
 *      district planned at every DRAWN_EVERY-th month: the checks hold;
 *   7. estate cells laid to fit what they hold (0.7.90, batch RD5;
 *      DistrictPlan's ESTATE LINES): each estate-band type alone on flat
 *      ground holds at least as much in no more cells than on the
 *      prototype's spine - a 4 x 4 works and a 9 x 9 plant in fewer, an
 *      11 x 11 works in as many (31 = 2 x 11 + 9) - every check holding; and
 *      a plan worked out afresh is the plan;
 *   8. industry and the outer kinds share estate cells (0.7.92, batch RD6;
 *      DistrictPlan's SHARED ESTATES): with cells to spare the plan is the
 *      plan with the bands apart, box for box, and no outer kind stands in
 *      an industry cell; with none left the outer kinds take industry's
 *      leftover ground - fewer without a place, every homes and industry
 *      box where it was - every check holding;
 *   9. any save named on the command line, every district: the checks, how
 *      full (its districts, and its own mix at 2.10's fills), its seams, and
 *      the time, each within PLAN_MS.
 *
 * THE CHECKS, after every plan (spec 2.4; the prototype's, and H5):
 *   - one network: its streets one piece, four-connected (a street beneath a
 *     highway, a seam and a track counted) - or pieces the ground parts, no
 *     way over the city's dry ground, a bridge's water or beneath a highway
 *     joining them;
 *   - the + floor: from a + junction (a street plot with street on its four
 *     sides and none on its corners, as MapCheck 8's) the next along a street
 *     is LATTICE plots or more away;
 *   - every building within REACH of a street, across corners;
 *   - no building on a street;
 *   - the surface drawn, kind by kind, is the road plots by kind to within
 *     half a plot, and with the surplus the ladder could not hold, exactly;
 *   - no building plot touches a highway plot, corners included;
 *   - (0.7.90) T junctions only in an estate cell: no + junction inside its
 *     interior, and its streets on lines DistrictPlan.LINE_FIRST to
 *     LINE_LAST, LATTICE or more apart (ESTATE LINES).
 */
public class PlanCheck {

    static int fails = 0;
    static PrintStream out = System.out;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-78s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Repeats a district's plan is timed over, after one untimed: its median is its time. */
    static final int TIMED = 5;

    /**
     * The most a district's plan may take, the slowest of the dense screen's
     * districts (each its median of TIMED): half MapCheck's screen, 40 ms
     * (star) - one plan and a screen painted from plans within SCREEN_MS, so
     * long as batch RD2's paint keeps to the other half. RD2 makes the plans
     * away from the screen's thread, as the map's snapshot is, and keeps them
     * as the deals are kept, so a frame paints from plans made already.
     */
    static final double PLAN_MS = MapCheck.SCREEN_MS / 2;

    /** The prototype's figures of the spec's 2.5 table, for his roads, the same trips paved and half that: cells, homes cells, street plots, narrow, full, tracks, square cells, boulevard cells, + junctions, buildings touching a street (facts.json). */
    static final int[][] TABLE = {
            { 49, 15, 9802, 828, 8974, 0, 13, 26, 280, 2995 },
            { 48, 13, 6052, 3137, 2915, 0, 0, 0, 119, 2516 },
            { 48, 13, 6052, 4483, 0, 1569, 0, 0, 119, 2516 } };

    /** ...its buildings, their plots, and the road of each budget, whole plots. */
    static final int TABLE_BUILDINGS = 3617, TABLE_BUILDING_PLOTS = 32217;
    static final int[] TABLE_BUDGET = { 9388, 4484, 2242 };

    /** The prototype's hub on its test district, in the district's plots: (70, 90) in its frame, 4 plots in. */
    static final double TEST_HUB_X = 66, TEST_HUB_Y = 86;

    /** The part of the test district the prototype did not own: its first 40 rows east of plot 196 (O[:44, 200:] in its frame). */
    static final int TEST_UNOWNED_ROWS = 40, TEST_UNOWNED_FROM = 196;

    /** The fills of 2.10's table: the model's buildings and roads at these shares of a flat district's ground. */
    static final double[] FILLS = { 0.75, 0.80, 0.85, 0.92 };

    /** The ground 2.10's fills are shares of: the prototype's frame, the district and 4 plots about it, 264 x 264 (its n = OFF + 32 x 8 + 4) - so its 75% is 79.8% of the district's own 256 x 256. */
    static final int PROTO_FRAME = 264;

    /** Gravel's and paved road's trips a road (buildings.json's capacities, as the prototype read them): the same city paved holds his roads' trips on Paved Roads. */
    static final int GRAVEL_TRIPS = 900, PAVED_TRIPS = 1200;

    /*
     * THE PROTOTYPE'S TEST DISTRICT (claude/roads-prototype/proto2.py and roads_proto.py, written out by batch RD1):
     * district_ground(264, 7) - grass, forest, a river two to four plots wide, a bay of sea - its district's frame
     * (plots 4 to 260 of the prototype's), row by row as runs: g grass, f forest, w fresh water, s sea, a sand, u not owned;
     * the prototype's hashes - h32(4127, 'cell', c) a cell's nudge, h32(4127, 'dir', c) under a half east-west (a bit a
     * cell), h32(4127, 'deal', t) a type's start; scaled_buildings at 70% of its dry ground, its roads; and FNV-1a
     * digests of each plan's buildings and streets (placedDigest(), streetDigest()).
     */
    static final String TEST_GROUND =
            "g36f14g13f1g14f29g89u61/g36f15g11f2g14f28g90u61/g35f17g10f2g13f29g90u61/g35f17g24f30g90u61/g34f17g24f30g91u61/" +
            "g33f18g23f30g92u61/g29f21g23f30g93u61/g28f22g23f30g93u61/g29f20g23f30g94u61/g32f17g23f30g94u61/g34f15g13f4g5f3" +
            "0g95u61/g46f3g12f39g96u61/g49f2g9f30g106u61/g49f3g8f25g111u61/g49f3g7f22g115u61/g50f3g5f21g117u61/g51f2g3f22g1" +
            "18u61/g52f25g119u61/g52f25g119u61/g52f24g120u61/g53f23g120u61/g54f21g121u61/g56f19g121u61/g58f17g121u61/g60f15" +
            "g121u61/g62f12g122u61/g6f3g55f9g123u61/g5f4g58f2g127u61/g4f6g186u61/f1g1f8g186u61/f10g186u61/f10g186u61/f10g18" +
            "6u61/f11g185u61/f11g185u61/f12g184u61/f12g184u61/f13g183u61/f14g182u61/f14g182u61/f14g243/f15g242/g3f12g242/g4" +
            "f11g242/g5f10g242/g4f11g242/g3f12g242/f16g241/f16g241/f16g241/f17g240/f17g240/f18g239/f19g238/f20g237/f22g235/" +
            "f24g233/f25g232/f26g231/f26g231/f27g230/f27g230/f27g230/f28g229/f28g229/f29g228/f29g87f2g139/f29g85f6g137/f29g" +
            "83f9g5f2g129/f30g81f18g128/f30g81f18g128/f30g81f18g128/f31g81f18g22f5g100/f31g58f6g17f18g21f7g99/f31g29f2g27f7" +
            "g16f18g21f12g94/f32g27f2g27f8g16f19g20f13g93/f33g55f9g15f20g19f13g93/f36g53f9g14f22g17f13g93/f39g50f10g13f31g1" +
            "0f10g94/f40g14f2g34f9g13f37g5f9g94/f41g10f7g32f10g12f38g5f7g95/f42g7f11g30f10g13f48g96/f44g2f15g28f10g16f46g96" +
            "/f62g27f10g18f43g97/f63g26f9g22f39g98/f64g31f2g25f36g99/f65g58f35g99/f66g58f33g100/f69g10f5g12f1g28f31g101/f85" +
            "g5f9g27f30g101/f85g4f12g26f28g102/f85g4f12g27f27g102/f85g4f12g27f27g102/f85g4f12g26f28g102/f86g3f12g24f30g102/" +
            "f101g23f31g102/f62g2f26g6f5g22f33g101/f60g6f23g7f5g21f34g101/f60g12f17g8f4g20f35g101/f60g13f16g8f4g20f36g100/f" +
            "60g14f14g20f2g10f37g100/f60g16f10g21f4g8f39g99/f60g17f7g21f7g6f41g98/f60g17f7g20f55g98/f60g17f6g20f56g98/f60g1" +
            "8f5g20f57g97/f60g18f5g21f56g97/f60g18f4g22f55g98/f14g9f38g17f4g23f54g98/f12g13f37g16f4g23f54g98/f12g15f35g17f3" +
            "g23f55g97/f12g15f34g19f1g24f55g97/f11g17f30g47f56g96/f11g18f28g47f58g95/f11g19f26g48f59g94/f10g21f24g49f60g93/" +
            "f10g22f23g49f60g93/f9g23f23g49f60g93/f1g31f23g49f60g93/g33f22g49f60g93/g33f22g49f60g93/g33f22g49f60g93/g36f18g" +
            "50f60g93/g40f14g50f60g93/g44f10g50f62g91/g48f6g51f63g89/g49f5g7f1g43f63g89/g50f4g6f3g43f63g88/g60f3g43f63g88/g" +
            "106f65g86/g106f66g85/g106f67g84/g111f62g84/g112f61g84/g112f61g84/g112f61g84/g112f62g83/g112f62g83/g112f62g83/g" +
            "112f62g83/g112f62g83/g112f63g82/g112f63g82/g111f64g82/g111f65g81/g112f64g81/g112f65g80/g113f65g79/g113f65g79/g" +
            "113f66g78/g113f66g78/g114f65g78/g114f11g8f46g78/g114f9g11f44g79/g115f6g14f43g79/g116f3g17f42g79/g137f41g79/g13" +
            "8f39g80/g139f11g2f24g81/g154f21g82/g155f18g84/g156f15g86/g156f14g87/g103f4g50f12g88/g103f4g51f11g88/g103f4g52f" +
            "9g89/g159f8g83w7/g90w1g1f2g67f5g81w10/g85w11g1w2g147w11/g83w18g143w6g7/g81w9f1w12g139w5g10/g80w5g4f7w9g136w5g1" +
            "1/g78w5g5f9w10g132w5g13/g77w4g7f10g1w10g28w4g97w4g15/g75w5g7f12g2w11g18w14g91w1g1w4g16/g74w4g8f14g3w13g10w20g8" +
            "7w6g18/g73w4g8f15g5w43g83w7g17a2/g72w4g8f17g6w23g7w12g80w8g9a11/g71w5g7f19g7w17g15w9g53w6g17w10g3a16s2/g70w5g8" +
            "f22g7w4g28w8g47w17g1w18a13s9/g68w6g9f24g39w7g42w1g1w35a6s19/g67w6g10f24g41w6g39w10g6w19a7s22/g25w7g34w6g10f26g" +
            "41w6g36w8g17w9a6s26/g21w15g29w6g11f26g42w6g34w7g20w6a6s28/g17w20g26w7g11f28g43w4g33w7g27a3s31/g17w23g22w6g13f2" +
            "9g43w5g29w8g26a4s32/w2g12w28g18w7g13f31g43w5g27w7g27a4s33/w25g7w4g1w8g13w8g13f33g43w5g24w7g26a6s34/w21g19w8g9w" +
            "8g14f33g46w3g22w7g24a8s35/g2w12g28w21g16f33g47w4g18w8g24a7s37/g45w17g18f31g49w4g15w8g24a5s41/g48w12g21f29g51w5" +
            "g11w9g24a4s43/g57w1g24f27g54w6g7w8g25a4s44/g83f25g56w19g25a4s45/g84f22g60w15g26a3s47/g85f20g64w7g1w2g27a3s48/g" +
            "86f9g109a4s49/g203a4s50/g202a4s51/g201a4s52/g199a4s54/g198a4s55/g197a4s56/g196a4s57/g186f4g5a4s58/g186f5g4a3s5" +
            "9/g186f6g2a3s60/g185f7g1a3s61/g185f8a2s62/g185f7a3s62/g185f6a3s63/g185f6a2s64/g185f5a2s65/g185f5a2s65/g185f4a2" +
            "s66/g184f4a3s66/g184f3a3s67/g183f3a3s68/g71f5g107f2a3s69/g66f10g108f1a2s70/g64f13g107a3s70/g62f16g106a2s71/g59" +
            "f20g104a3s71/g57f24g102a3s71/g54f28g101a2s72/g53f29g101a2s72/g36f6g9f32g100a2s72/g35f48g100a2s72/g35f49g98a2s7" +
            "3/g35f50g97a2s73/g35f51g63f3g2f3g25a2s73/g35f53g60f10g24a2s73/g35f54g39f5g14f11g23a2s74/g35f59g33f7g13f11g23a2" +
            "s74/g35f61g31f9g12f10g22a3s74/g35f61g30f10g12f9g23a3s74/g35f62g29f11g11f9g23a2s75/g35f61g30f10g12f9g22a3s75/g3" +
            "5f61g30f10g10f11g22a3s75/g34f61g31f10g9f12g22a3s75/g34f56g35f11g8f12g22a3s76/g34f54g37f11g7f13g22a3s76/g34f54g" +
            "37f13g4f14g22a3s76/g33f54g39f30g21a4s76/g33f38g3f12g41f28g22a3s77/g33f35g8f9g44f24g23a4s77/g32f34g11f5g52f18g2" +
            "4a4s77/g32f33g69f17g25a3s78/g31f33g69f18g25a3s78/g31f32g70f18g25a2s79/g30f33g70f18g25a2s79/g29f34g70f19g23a3s7" +
            "9/g28f35g70f19g23a3s79/g26f37g70f19g23a2s80/g21f42g70f19g23a2s80/g19f44g71f17g24a2s80/g18f44g72f16g25a2s80";
    static final double[] TEST_NUDGE = {
            0.3727494347187473, 0.03906684873305479, 0.22912251263578634, 0.5245030267418446,
            0.8596486524029501, 0.023832658552338577, 0.42353856248950167, 0.041045443138387164,
            0.42238964769303516, 0.303747742667511, 0.6647470646456921, 0.7329282036793988,
            0.7024529554205838, 0.9361951999943415, 0.2619052922340857, 0.7176876256940995,
            0.8182453573055538, 0.1627184105132867, 0.22914197981654785, 0.5401334732371174,
            0.17472130204216604, 0.4560349926661963, 0.6062620687186017, 0.2586540352464113,
            0.9384475908109629, 0.4100140995069999, 0.8024419073742566, 0.3764594967956057,
            0.3862604333982235, 0.3991077376307637, 0.6564336875931007, 0.3514818546620243,
            0.08593734463922315, 0.22897768251535924, 0.4389331679597274, 0.0797138072581272,
            0.21458035493505026, 0.7861507985779361, 0.31604840270280815, 0.2398965732239523,
            0.4485231966548687, 0.8606610312734644, 0.8761005179349131, 0.009831151426027104,
            0.6933376771293382, 0.1957883810939464, 0.5921184706922767, 0.6054709114789355,
            0.7785832249356266, 0.47071563084032503, 0.5330264379736543, 0.4182690237735671,
            0.14688990529673163, 0.776653386313684, 0.8402381104863175, 0.08123193215491663,
            0.8996890329806819, 0.8981131272245991, 0.15355654968620291, 0.41713048034363553,
            0.61747010217902, 0.8922983692958087, 0.3361171396656944, 0.6323143954710908 };
    static final long TEST_ACROSS = 2483539012620168834L;
    static final int[] TEST_TYPES = { 0, 1, 2, 4, 5, 6, 7, 9, 10, 11, 12, 17, 18, 19, 20, 22, 23, 25, 27, 28, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 43, 49, 50, 51, 53, 56, 57, 58, 59, 60, 61, 65, 66, 69, 71 };
    static final int[] TEST_COUNTS = { 899, 94, 569, 30, 2, 799, 2, 1, 2, 23, 5, 112, 54, 1, 1, 13, 6, 4, 1, 2, 22, 22, 19, 3, 3, 2, 1, 1, 1, 3, 18, 1, 3, 53, 50, 2, 7, 53, 69, 12, 5, 1, 3, 54, 62, 527 };
    static final double[] TEST_DEAL = {
            0.8623860022826665, 0.4688361493186886, 0.49244847032280087, 0.9101025582510129,
            0.04369366703319154, 0.7912363634868288, 0.18911176394895476, 0.16246220141040693,
            0.40103615293173306, 0.35386259667441994, 0.15116654907637375, 0.2320222692343274,
            0.3051565057390019, 0.9720785947353873, 0.08922301457522895, 0.9420229388094302,
            0.22603799967047836, 0.06396851826410868, 0.8248171086687733, 0.4105533378839337,
            0.22341432941422687, 0.6093191444630269, 0.4646930625171726, 0.4642897194186345,
            0.9453764333403332, 0.7053494534581594, 0.11379353228563835, 0.3207439133219679,
            0.8220796377201866, 0.4826674922777667, 0.7696680598339064, 0.7584151881939336,
            0.6221077897317457, 0.23363008861379364, 0.8266630504298469, 0.5710706088706681,
            0.9141322942518781, 0.40143441123469004, 0.5976824865654488, 0.19275056297714122,
            0.484086489351348, 0.048266584212291304, 0.9085021855150137, 0.2026354914336543,
            0.14575023866893685, 0.17028678553403506 };
    static final double TEST_GRAVEL = 8407.741935483871, TEST_PAVED = 980.6451612903226, TEST_PAVED_ALL = 4483.870967741936;
    static final long TEST_PLACED_JERUS = -1980402857778823524L, TEST_STREETS_JERUS = -7143981648789130548L;
    static final long TEST_PLACED_PAVED = 9000504916410474359L, TEST_STREETS_PAVED = -4283127041626870812L;
    static final long TEST_PLACED_THIN = 9000504916410474359L, TEST_STREETS_THIN = 4092048347058802282L;

    public static void main(String[] args) throws Exception {
        List<String> saves = new ArrayList<>();
        boolean quietRun = false;
        for (String a : args) {
            if (a.equals("-q")) quietRun = true; else saves.add(a);
        }
        if (quietRun) out = new PrintStream(new OutputStream() { @Override public void write(int b) { } }) {
            @Override public PrintStream printf(String f, Object... a) {
                String s = String.format(f, a);
                if (s.endsWith("FAIL\n")) System.out.print(s);
                return this;
            }
        };
        BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        replay(types);
        gameRules(types);
        highways(types);
        howFull(types);
        fixture(types);
        played();
        estateLines(types);
        sharedEstates(types);
        for (String s : saves) save(Path.of(s));
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =====================================================================
       THE FIGURES OF ONE PLAN
       ===================================================================== */

    /** What a plan draws, and its checks. */
    static final class Figures {
        int pieces, plus, closestPlus = Integer.MAX_VALUE, protoPlus, protoClosest = 99, outOfReach, touching, onStreet, besideHighway;
        int placed, overflow, buildingPlots, streetPlots, tracks, narrow, full, seams, under, minesBeyond;
        /** + junctions inside an estate cell's interior, and estate cells whose streets leave ESTATE LINES' lines (0.7.90). */
        int estatePlus, estateLinesOff;
        boolean groundParts;
        /** Surface drawn by kind: gravel, paved, a highway's. */
        double gravel, paved, highway;
        /** Street plots and dry ground in the open cells' tiles: homes cells of long blocks, of square blocks, estate cells; and the boulevard cells. */
        final long[] cellStreets = new long[4], cellGround = new long[4];
        final int[] cellCount = new int[4];

        /** Whether the surface is the road by kind to within half a plot, and with the surplus exactly. */
        boolean surfaceHolds(DistrictPlan p, DistrictPlan.Input in) {
            double lost = p.surplus;
            boolean byKind = Math.abs(paved - in.paved) <= DistrictPlan.HALF + lost + 1e-9
                    && Math.abs(gravel - in.gravel) <= DistrictPlan.HALF + lost + 1e-9
                    && Math.abs(highway - in.highway) <= DistrictPlan.HALF + lost + 1e-9;
            return byKind && Math.abs(paved + gravel + highway + lost - (in.paved + in.gravel + in.highway)) < 1e-6;
        }

        /** Whether every check holds: one network (or the ground parts it), the + floor, reach, none on a street, the surface, none beside a highway, T junctions only in an estate cell. */
        boolean holds(DistrictPlan p, DistrictPlan.Input in) {
            return (pieces <= 1 || groundParts) && closestPlus >= DistrictPlan.LATTICE && outOfReach == 0 && onStreet == 0
                    && surfaceHolds(p, in) && besideHighway == 0 && estateHolds();
        }

        /** Whether its estate cells keep ESTATE LINES (0.7.90): no + inside one, its streets on their lines. */
        boolean estateHolds() {
            return estatePlus == 0 && estateLinesOff == 0;
        }
    }

    /** A plan's figures and checks. */
    static Figures figures(DistrictPlan p, DistrictPlan.Input in) {
        Figures f = new Figures();
        int n = DistrictPlan.FRAME, area = DistrictPlan.AREA;
        boolean[] st = new boolean[area], road = new boolean[area];
        for (int q = 0; q < area; q++) {
            int k = p.kindAt(q);
            if (k == DistrictPlan.NONE) continue;
            road[q] = true;
            if (k == DistrictPlan.UNDER) { f.under++; continue; }
            st[q] = true;
            f.streetPlots++;
            double w = p.widthAt(q);
            if (k == DistrictPlan.SEAM) f.seams++;
            else if (k == DistrictPlan.TRACK) f.tracks++;
            else if (w == DistrictPlan.FULL) f.full++;
            else f.narrow++;
            if (k == DistrictPlan.GRAVEL) f.gravel += w;
            else if (k == DistrictPlan.PAVED) f.paved += w;
            else if (k == DistrictPlan.HIGHWAY) f.highway += w;
        }
        // One network: four-connected pieces of every street, beneath a highway included.
        int[] lab = new int[area];
        Arrays.fill(lab, -1);
        int[] stack = new int[area];
        List<Integer> sizes = new ArrayList<>();
        for (int q = 0; q < area; q++) {
            if (!road[q] || lab[q] >= 0) continue;
            int sp = 0, size = 0, id = sizes.size();
            stack[sp++] = q;
            lab[q] = id;
            while (sp > 0) {
                int u = stack[--sp];
                size++;
                for (int d = 0; d < 4; d++) {
                    int ax = u % n + TilePainter.DX[d], ay = u / n + TilePainter.DY[d];
                    if (ax < 0 || ay < 0 || ax >= n || ay >= n) continue;
                    int a = ay * n + ax;
                    if (road[a] && lab[a] < 0) { lab[a] = id; stack[sp++] = a; }
                }
            }
            sizes.add(size);
        }
        f.pieces = sizes.size();
        if (f.pieces > 1) f.groundParts = groundParts(p, in, lab, sizes);
        // The + floor, MapCheck 8's junction: street on four sides, none on the corners; the next along a street.
        boolean[] plus = new boolean[area];
        for (int y = 1; y < n - 1; y++) {
            for (int x = 1; x < n - 1; x++) {
                int q = y * n + x;
                if (!road[q] || !road[q - 1] || !road[q + 1] || !road[q - n] || !road[q + n]) continue;
                if (road[q - n - 1] || road[q - n + 1] || road[q + n - 1] || road[q + n + 1]) continue;
                plus[q] = true;
                f.plus++;
            }
        }
        // T junctions only in an estate cell (0.7.90): no + inside its interior; its streets on ESTATE LINES' lines.
        for (int q = 0; q < area; q++) {
            int x = q % n, y = q / n;
            if (!plus[q] || x % DistrictPlan.CELL == 0 || y % DistrictPlan.CELL == 0 || x >= DistrictPlan.SIDE || y >= DistrictPlan.SIDE) continue;
            if (p.cellKind[x / DistrictPlan.CELL + (y / DistrictPlan.CELL) * DistrictPlan.CELLS_A_SIDE] == DistrictPlan.ESTATE) f.estatePlus++;
        }
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (p.cellKind[c] != DistrictPlan.ESTATE) continue;
            int last = -DistrictPlan.LATTICE;
            boolean off = false;
            for (int o = 0; o < DistrictPlan.INTERIOR; o++) {
                if ((p.cellStreets[c] >>> o & 1) == 0) continue;
                off |= o < DistrictPlan.LINE_FIRST || o > DistrictPlan.LINE_LAST || o - last < DistrictPlan.LATTICE;
                last = o;
            }
            if (off) f.estateLinesOff++;
        }
        for (int q = 0; q < area; q++) {
            if (!plus[q]) continue;
            for (int d = 0; d < 4; d++) {
                int x = q % n, y = q / n;
                for (int k = 1; k < DistrictPlan.LATTICE; k++) {
                    x += TilePainter.DX[d];
                    y += TilePainter.DY[d];
                    if (x < 0 || y < 0 || x >= n || y >= n || !road[y * n + x]) break;
                    if (plus[y * n + x]) { f.closestPlus = Math.min(f.closestPlus, k); break; }
                }
            }
        }
        // ...and the prototype's: street on the lattice's lines with street on four sides, the next within 8 east or south.
        boolean[] pp = new boolean[area];
        for (int y = 1; y < n - 1; y++) {
            for (int x = 1; x < n - 1; x++) {
                int q = y * n + x;
                if (!onLattice(q) || !st[q]) continue;
                if (lat(st, q - 1) && lat(st, q + 1) && lat(st, q - n) && lat(st, q + n)) { pp[q] = true; f.protoPlus++; }
            }
        }
        for (int q = 0; q < area; q++) {
            if (!pp[q]) continue;
            int x = q % n, y = q / n;
            for (int k = 1; k <= DistrictPlan.LATTICE; k++) if (x + k < n && pp[y * n + x + k]) { f.protoClosest = Math.min(f.protoClosest, k); break; }
            for (int k = 1; k <= DistrictPlan.LATTICE; k++) if (y + k < n && pp[(y + k) * n + x]) { f.protoClosest = Math.min(f.protoClosest, k); break; }
        }
        // Reach: across corners from every street (not beneath a highway), REACH plots out.
        int[] dist = reach(st);
        f.placed = p.buildings;
        f.overflow = p.overflowCount();
        for (int i = 0; i < p.buildings; i++) {
            int best = 99;
            boolean onSt = false, beside = false;
            for (int y = p.by[i]; y < p.by[i] + p.bh[i]; y++) {
                for (int x = p.bx[i]; x < p.bx[i] + p.bw[i]; x++) {
                    int q = y * n + x;
                    best = Math.min(best, dist[q]);
                    if (road[q]) onSt = true;
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            int ax = x + dx, ay = y + dy;
                            if (ax >= 0 && ay >= 0 && ax < n && ay < n && in.fixed[ay * n + ax] == DistrictPlan.FIXED_HIGHWAY) beside = true;
                        }
                    }
                }
            }
            f.buildingPlots += p.bw[i] * p.bh[i];
            if (best > DistrictPlan.REACH) f.outOfReach++;
            if (best == 1) f.touching++;
            if (onSt) f.onStreet++;
            if (beside) f.besideHighway++;
        }
        for (int[] m : in.mines) {
            int best = 99;
            for (int y = m[1]; y <= m[3]; y++) for (int x = m[0]; x <= m[2]; x++) best = Math.min(best, dist[y * n + x]);
            if (best > DistrictPlan.REACH) f.minesBeyond++;
        }
        // The street share by cell kind: each open cell's tile - its interior and its own west and north arterials.
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (p.cellKind[c] == DistrictPlan.CLOSED) continue;
            int kind = p.cellKind[c] == DistrictPlan.ESTATE ? 2 : p.cellSquare[c] ? 1 : 0;
            int x0 = DistrictPlan.CELL * (c % DistrictPlan.CELLS_A_SIDE), y0 = DistrictPlan.CELL * (c / DistrictPlan.CELLS_A_SIDE);
            long streets = 0, ground = 0;
            for (int y = y0; y < y0 + DistrictPlan.CELL; y++) {
                for (int x = x0; x < x0 + DistrictPlan.CELL; x++) {
                    int q = y * n + x;
                    byte t = in.terrain[q];
                    if (in.owned[q] && (t == World.GRASS || t == World.FOREST || t == World.SAND)) ground++;
                    if (st[q]) streets++;
                }
            }
            f.cellStreets[kind] += streets;
            f.cellGround[kind] += ground;
            f.cellCount[kind]++;
            if (p.cellBoulevard[c]) { f.cellStreets[3] += streets; f.cellGround[3] += ground; f.cellCount[3]++; }
        }
        return f;
    }

    static boolean onLattice(int q) {
        int x = q % DistrictPlan.FRAME, y = q / DistrictPlan.FRAME;
        return x % DistrictPlan.LATTICE == 0 || y % DistrictPlan.LATTICE == 0;
    }

    static boolean lat(boolean[] st, int q) {
        return st[q] && onLattice(q);
    }

    /** Each plot's distance from a street across corners, up to REACH; 99 beyond. */
    static int[] reach(boolean[] st) {
        int n = DistrictPlan.FRAME, area = DistrictPlan.AREA;
        int[] dist = new int[area];
        Arrays.fill(dist, 99);
        int[] q = new int[area];
        int qh = 0, qt = 0;
        for (int i = 0; i < area; i++) if (st[i]) { dist[i] = 0; q[qt++] = i; }
        while (qh < qt) {
            int u = q[qh++];
            if (dist[u] >= DistrictPlan.REACH) continue;
            int ux = u % n, uy = u / n;
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    int ax = ux + dx, ay = uy + dy;
                    if (ax < 0 || ay < 0 || ax >= n || ay >= n) continue;
                    int a = ay * n + ax;
                    if (dist[a] > dist[u] + 1) { dist[a] = dist[u] + 1; q[qt++] = a; }
                }
            }
        }
        return dist;
    }

    /**
     * Whether the ground parts a plan's street pieces: no way from its
     * largest piece to another over the city's own ground - dry, fresh water
     * no wider along the way than its line may bridge (an arterial's on an
     * arterial's line, a street's off it), beneath a highway or across a
     * railway, never along one, never over a mine's site. Its buildings are
     * no part of the ground: a piece they wall in is the plan's to join.
     */
    static boolean groundParts(DistrictPlan p, DistrictPlan.Input in, int[] lab, List<Integer> sizes) {
        int n = DistrictPlan.FRAME, area = DistrictPlan.AREA;
        int main = 0;
        for (int i = 1; i < sizes.size(); i++) if (sizes.get(i) > sizes.get(main)) main = i;
        boolean[] seen = new boolean[area];
        int[] q = new int[area];
        int qh = 0, qt = 0;
        for (int i = 0; i < area; i++) if (lab[i] == main) { seen[i] = true; q[qt++] = i; }
        while (qh < qt) {
            int u = q[qh++];
            for (int d = 0; d < 4; d++) {
                int ax = u % n + TilePainter.DX[d], ay = u / n + TilePainter.DY[d];
                if (ax < 0 || ay < 0 || ax >= n || ay >= n) continue;
                int a = ay * n + ax;
                if (seen[a]) continue;
                if (lab[a] >= 0) return false;
                if (!in.owned[a] || in.terrain[a] == World.SALT || in.site[a] == DistrictPlan.SITE_MINED) continue;
                boolean horizontal = TilePainter.DY[d] == 0, arterial = horizontal ? ay % DistrictPlan.CELL == 0 : ax % DistrictPlan.CELL == 0;
                if (in.terrain[a] == World.FRESH && waterRun(in, a, d) > (arterial ? DistrictPlan.ARTERIAL_BRIDGE : DistrictPlan.STREET_BRIDGE)) continue;
                if (in.fixed[a] != 0 && DistrictPlan.along(in.fixed, a, horizontal)) continue;
                seen[a] = true;
                q[qt++] = a;
            }
        }
        return true;
    }

    /** Fresh water's run at plot a along direction d's axis. */
    static int waterRun(DistrictPlan.Input in, int a, int d) {
        int n = DistrictPlan.FRAME, x = a % n, y = a / n, dx = TilePainter.DX[d] != 0 ? 1 : 0, dy = 1 - dx, run = 1;
        for (int k = 1; ; k++) { int ax = x + dx * k, ay = y + dy * k; if (ax >= n || ay >= n || in.terrain[ay * n + ax] != World.FRESH) break; run++; }
        for (int k = 1; ; k++) { int ax = x - dx * k, ay = y - dy * k; if (ax < 0 || ay < 0 || in.terrain[ay * n + ax] != World.FRESH) break; run++; }
        return run;
    }

    /** A digest of a plan's buildings, in the order placed: x, y, across, down and type of each (FNV-1a over 64 bits). */
    static long placedDigest(DistrictPlan p) {
        long h = 0xcbf29ce484222325L;
        for (int i = 0; i < p.buildings; i++) {
            for (long v : new long[] { p.bx[i], p.by[i], p.bw[i], p.bh[i], p.btype[i] }) { h ^= v; h *= 0x100000001b3L; }
        }
        return h;
    }

    /** ...and of its streets, row by row: each plot, its width in half plots and its kind (track 0, gravel 1, paved 2). */
    static long streetDigest(DistrictPlan p) {
        long h = 0xcbf29ce484222325L;
        for (int q = 0; q < DistrictPlan.AREA; q++) {
            int k = p.kindAt(q);
            if (k == DistrictPlan.NONE || k == DistrictPlan.UNDER) continue;
            long kc = k == DistrictPlan.PAVED ? 2 : k == DistrictPlan.GRAVEL ? 1 : 0;
            for (long v : new long[] { q, Math.round(p.widthAt(q) * 2), kc }) { h ^= v; h *= 0x100000001b3L; }
        }
        return h;
    }

    /** One line of a plan's figures. */
    static String line(DistrictPlan p, Figures f) {
        return String.format("%d cells (%d homes, %d squared, %d boulevards), %,d of %,d buildings placed on %,d plots, %,d street plots"
                        + " (%,d full, %,d half, %,d track, %,d seam), %d piece(s), closest + %s, %d beyond reach, %d touching",
                p.cellsOpen, p.homesCells, Math.min(p.squares, p.homesCells), Math.min(p.boulevards, p.cellsOpen), f.placed, f.placed + f.overflow,
                f.buildingPlots, f.streetPlots, f.full, f.narrow, f.tracks, f.seams, f.pieces,
                f.closestPlus == Integer.MAX_VALUE ? DistrictPlan.LATTICE + " or more" : String.valueOf(f.closestPlus), f.outOfReach, f.touching);
    }

    /* =====================================================================
       1. THE PORT: THE PROTOTYPE'S TEST DISTRICT, REPLAYED
       ===================================================================== */

    /** The prototype's test district: its ground (TEST_GROUND, the district's frame), its buildings, and one of its three budgets (0 his roads, 1 the same trips paved, 2 half that). */
    static DistrictPlan.Input testDistrict(BuildingVisual.Type[] types, int budget, boolean prototype) {
        DistrictPlan.Input in = new DistrictPlan.Input();
        int n = DistrictPlan.FRAME, y = 0, x = 0;
        String g = TEST_GROUND;
        for (int i = 0; i < g.length(); ) {
            char c = g.charAt(i++);
            if (c == '/') { y++; x = 0; continue; }
            int e = i;
            while (e < g.length() && Character.isDigit(g.charAt(e))) e++;
            int run = Integer.parseInt(g.substring(i, e));
            i = e;
            byte t = c == 'f' ? World.FOREST : c == 'w' ? World.FRESH : c == 's' ? World.SALT : c == 'a' ? World.SAND : World.GRASS;
            for (int k = 0; k < run; k++, x++) {
                in.terrain[y * n + x] = t;
                in.owned[y * n + x] = c != 'u';
            }
        }
        in.types = types;
        in.counts = new int[types.length];
        for (int i = 0; i < TEST_TYPES.length; i++) in.counts[TEST_TYPES[i]] = TEST_COUNTS[i];
        in.seed = Founding.DEFAULT_WORLD_SEED;
        in.hubX = TEST_HUB_X;
        in.hubY = TEST_HUB_Y;
        in.gravel = budget == 0 ? TEST_GRAVEL : 0;
        in.paved = budget == 0 ? TEST_PAVED : budget == 1 ? TEST_PAVED_ALL : TEST_PAVED_ALL / 2;
        if (prototype) {
            in.asPrototype = true;
            in.hashes = new DistrictPlan.Hashes() {
                @Override public double cell(int ci, int cj) { return TEST_NUDGE[ci + cj * DistrictPlan.CELLS_A_SIDE]; }
                @Override public double dir(int ci, int cj) { return (TEST_ACROSS >>> (ci + cj * DistrictPlan.CELLS_A_SIDE) & 1) != 0 ? 0.25 : 0.75; }
                @Override public double deal(int type) {
                    for (int i = 0; i < TEST_TYPES.length; i++) if (TEST_TYPES[i] == type) return TEST_DEAL[i];
                    return 0;
                }
            };
        }
        return in;
    }

    static final String[] BUDGETS = { "his roads", "the same trips paved", "half that" };

    static void replay(BuildingVisual.Type[] types) {
        out.println("\n--- 1. the port: the prototype's test district, its own hashes and rules - proto2.py's plan, box for box ---");
        long[] placed = { TEST_PLACED_JERUS, TEST_PLACED_PAVED, TEST_PLACED_THIN }, streets = { TEST_STREETS_JERUS, TEST_STREETS_PAVED, TEST_STREETS_THIN };
        boolean digests = true, table = true, checks = true;
        for (int b = 0; b < 3; b++) {
            DistrictPlan.Input in = testDistrict(types, b, true);
            DistrictPlan p = DistrictPlan.make(in);
            Figures f = figures(p, in);
            out.printf("      %s (%,.0f road plots): %s%n", BUDGETS[b], p.budget, line(p, f));
            int[] t = TABLE[b];
            boolean same = p.cellsOpen == t[0] && p.homesCells == t[1] && f.streetPlots == t[2] && f.narrow == t[3] && f.full == t[4] && f.tracks == t[5]
                    && p.squares == t[6] && p.boulevards == t[7] && f.protoPlus == t[8] && f.touching == t[9]
                    && f.placed == TABLE_BUILDINGS && f.buildingPlots == TABLE_BUILDING_PLOTS && Math.round(p.budget) == TABLE_BUDGET[b]
                    && f.protoClosest == DistrictPlan.LATTICE && f.pieces == 1 && f.outOfReach == 0 && f.overflow == 0;
            out.printf("      ...the table's: %d cells (%d homes), %,d street plots (%,d half, %,d full, %,d track), %d squared, %d boulevards,"
                    + " %d + junctions, %,d touching; the prototype's + %d apart%n", t[0], t[1], t[2], t[3], t[4], t[5], t[6], t[7], t[8], t[9], f.protoClosest);
            table &= same;
            digests &= placedDigest(p) == placed[b] && streetDigest(p) == streets[b];
            checks &= f.holds(p, in);
        }
        check("every figure of the spec's 2.5 table, for each of its three budgets, is the prototype's", table);
        check("...every building's box in the order placed, and every street plot, width and kind, the prototype's", digests);
        check("...and every check holds on each", checks);
    }

    /* =====================================================================
       2. THE GAME'S RULES ON THE TEST DISTRICT
       ===================================================================== */

    static void gameRules(BuildingVisual.Type[] types) {
        out.println("\n--- 2. the test district with the game's rules and hashes: the water rule, streets along a cut ---");
        boolean holds = true, all = true;
        for (int b = 0; b < 3; b++) {
            DistrictPlan.Input in = testDistrict(types, b, false);
            DistrictPlan p = DistrictPlan.make(in);
            Figures f = figures(p, in);
            out.printf("      %s: %s; %d dead ends joined along a cut%n", BUDGETS[b], line(p, f), p.shoreJoins);
            holds &= f.holds(p, in);
            all &= f.overflow == 0;
        }
        check("the checks hold for each budget", holds);
        check("...and every building has a place, as in the prototype", all);
        // Wider than an estate's strip both ways (2.3): a Rail Terminal and a Livestock Farm, each a whole estate cell, its spine closed.
        DistrictPlan.Input in = testDistrict(types, 1, false);
        int terminal = -1, livestock = -1;
        for (BuildingVisual.Type t : types) {
            if (t == null || !t.drawn()) continue;
            int[] f = BuildingVisual.footprint(t);
            if (Math.min(f[0], f[1]) <= DistrictPlan.MIDDLE) continue;
            if (t.terminal()) terminal = t.id();
            else if (t.category() == BuildingType.AGRICULTURE) livestock = t.id();
        }
        in.counts[terminal] += 1;
        in.counts[livestock] += 1;
        DistrictPlan p = DistrictPlan.make(in);
        Figures f = figures(p, in);
        int whole = 0, closed = 0;
        for (int i = 0; i < p.buildings; i++) if (p.btype[i] == terminal || p.btype[i] == livestock) whole++;
        for (int c = 0; c < DistrictPlan.CELLS; c++) if (p.cellKind[c] == DistrictPlan.ESTATE && p.cellMerged[c]) closed++;
        out.printf("      with a Rail Terminal and a Livestock Farm added: %d of 2 placed, %d estate cell(s) with the spine closed; %s%n", whole, closed, line(p, f));
        check("a building wider than an estate's strip both ways takes a whole estate cell, its spine closed (2.3)", whole == 2 && closed >= 2 && f.holds(p, in));
    }

    /* =====================================================================
       3. THE HIGHWAYS (H5)
       ===================================================================== */

    static void highways(BuildingVisual.Type[] types) {
        out.println("\n--- 3. the city's highways: none, and a straight row across the district (H5: no building beside one) ---");
        DistrictPlan.Input none = testDistrict(types, 1, false);
        DistrictPlan pn = DistrictPlan.make(none);
        Figures fn = figures(pn, none);
        DistrictPlan.Input row = testDistrict(types, 1, false);
        // A straight row through the hub's cell, east to west on its own ground: 16 plots into a cell, as the spec's corridors run (2.7).
        int hy = (int) TEST_HUB_Y / DistrictPlan.CELL * DistrictPlan.CELL + DistrictPlan.CELL / 2, laid = 0;
        for (int x = 0; x < DistrictPlan.FRAME; x++) {
            int q = hy * DistrictPlan.FRAME + x;
            if (!row.owned[q] || row.terrain[q] == World.SALT) continue;
            row.fixed[q] = DistrictPlan.FIXED_HIGHWAY;
            laid++;
        }
        DistrictPlan pr = DistrictPlan.make(row);
        Figures fr = figures(pr, row);
        out.printf("      none: %s%n", line(pn, fn));
        out.printf("      a row of %d highway plots on row %d: %s; %d streets beneath it%n", laid, hy, line(pr, fr), fr.under);
        check("with none, the checks hold", fn.holds(pn, none));
        check("with a straight row, the checks hold: one network, beneath it, and no building beside it", fr.holds(pr, row) && fr.pieces == 1);
        check("...no building plot touches a highway plot, corners included", fr.besideHighway == 0);
        check("...the streets cross beneath it", fr.under > 0);
    }

    /* =====================================================================
       4. HOW FULL A DISTRICT THE PLAN CAN DRAW (spec 2.10)
       ===================================================================== */

    /** Jerus's mix (MapCheck.JERUS_COUNTS) scaled to a share of a district's dry plots, his roads with it - the prototype's scaled_buildings, its fractions by the world's hash. */
    static void scaled(DistrictPlan.Input in, long[] counts, int dry, double fill, boolean pavedCity) {
        BuildingVisual.Type[] types = in.types;
        long[] c = counts.clone();
        if (pavedCity) {
            int gravel = -1, paved = -1;
            for (BuildingVisual.Type t : types) if (t != null && t.road() == BuildingVisual.GRAVEL) gravel = t.id();
            for (BuildingVisual.Type t : types) if (t != null && t.road() == BuildingVisual.PAVED) paved = t.id();
            long trips = c[gravel] * GRAVEL_TRIPS + c[paved] * PAVED_TRIPS;
            c[gravel] = 0;
            c[paved] = Math.round(trips / (double) PAVED_TRIPS);
        }
        double total = 0;
        for (int t = 0; t < c.length && t < types.length; t++) if (types[t] != null && c[t] > 0) total += c[t] * (double) BuildingVisual.cells(types[t]);
        double k = fill * dry / total;
        in.counts = new int[types.length];
        in.gravel = in.paved = in.highway = 0;
        for (int t = 0; t < c.length && t < types.length; t++) {
            if (types[t] == null || c[t] <= 0) continue;
            double v = c[t] * k;
            int whole = (int) v + (DistrictPlan.unit(World.mix(0x5CA1EDL ^ t)) < v - (int) v ? 1 : 0);
            if (types[t].road() == BuildingVisual.GRAVEL) in.gravel += whole * types[t].plots();
            else if (types[t].road() == BuildingVisual.PAVED) in.paved += whole * types[t].plots();
            else if (types[t].drawn() && types[t].site() == null) in.counts[t] = whole;
        }
    }

    static void howFull(BuildingVisual.Type[] types) {
        out.println("\n--- 4. how full a district the plan can draw (spec 2.10): flat ground, every plot owned ---");
        boolean holds = true;
        double lost75 = 1, lost92 = 0;
        long[] streets = new long[4], ground = new long[4];
        for (int city = 0; city < 2; city++) {
            for (double fill : FILLS) {
                DistrictPlan.Input in = new DistrictPlan.Input();
                Arrays.fill(in.owned, true);
                in.types = types;
                in.seed = Founding.DEFAULT_WORLD_SEED;
                in.hubX = in.hubY = DistrictPlan.FRAME / 2;
                scaled(in, MapCheck.JERUS_COUNTS, PROTO_FRAME * PROTO_FRAME, fill, city == 1);
                DistrictPlan p = DistrictPlan.make(in);
                Figures f = figures(p, in);
                long need = 0, lost = 0;
                for (int t = 0; t < types.length; t++) {
                    if (in.counts[t] <= 0) continue;
                    need += (long) in.counts[t] * BuildingVisual.cells(types[t]);
                    lost += (long) p.overflow[t] * BuildingVisual.cells(types[t]);
                }
                out.printf("      %s at %.0f%%: %,d of %,d buildings placed, %.1f%% of their ground has no place; streets %.1f%% of what is drawn,"
                                + " drawn %.1f%% of the prototype's frame; %d cells, %d squared, %d boulevards; %,.0f road plots left over%n", city == 0 ? "his city" : "the same city paved",
                        fill * 100, f.placed, f.placed + f.overflow, 100.0 * lost / need, 100.0 * f.streetPlots / (f.streetPlots + f.buildingPlots),
                        100.0 * (f.streetPlots + f.buildingPlots) / (PROTO_FRAME * PROTO_FRAME), p.cellsOpen, Math.min(p.squares, p.homesCells),
                        Math.min(p.boulevards, p.cellsOpen), p.surplus);
                holds &= f.holds(p, in);
                if (city == 0 && fill == FILLS[0]) lost75 = lost / (double) need;
                if (city == 1 && fill == FILLS[FILLS.length - 1]) lost92 = lost / (double) need;
                if (fill == FILLS[0]) for (int k = 0; k < 4; k++) { streets[k] += f.cellStreets[k]; ground[k] += f.cellGround[k]; }
            }
        }
        out.printf("      street share of a cell's ground at 75%%: homes cells %.1f%%, squared %.1f%%, estate cells %.1f%%; boulevard cells %.1f%%%n",
                pct(streets[0], ground[0]), pct(streets[1], ground[1]), pct(streets[2], ground[2]), pct(streets[3], ground[3]));
        check("the checks hold at every fill, his city and paved", holds);
        check("at 75% his city's buildings all have a place, as 2.10's table", lost75 == 0);
        check("...and at 92% the paved city's do not: the plan draws at most about 80% of a district (2.10, star M)", lost92 > 0);
    }

    static double pct(long a, long b) {
        return b > 0 ? 100.0 * a / b : 0;
    }
    /* =====================================================================
       THE DISTRICTS OF A CITY: EVERY ONE PLANNED, ITS CHECKS AND FIGURES
       ===================================================================== */

    /** Every district of a map planned: how many keep each check, what they draw, what has no place, and what a plan costs. */
    static final class Tally {
        int districts, held, network, apart, floor, reach, onStreet, surface, beside, estate, mines, minesBeyond;
        long buildings, placed, overflow, overflowPlots, needPlots, streets, tracks, seams, under, surplusDistricts;
        double surplus, worstFill, worstLost;
        String worstWhere = "", firstFail = "";
        final long[] cellStreets = new long[4], cellGround = new long[4];
        final int[] cellCount = new int[4];
        final List<Double> planMs = new ArrayList<>(), inputMs = new ArrayList<>();
        boolean timed;

        /** District d of map m planned (and timed, TIMED times after one untimed, when `time`). */
        void add(CityMap m, CityMap.District d, boolean time) {
            long t0 = System.nanoTime();
            DistrictPlan.Input in = m.planInput(d);
            double inMs = (System.nanoTime() - t0) / 1e6;
            DistrictPlan p = DistrictPlan.make(in);
            if (time) {
                double[] ms = new double[TIMED];
                for (int r = 0; r < TIMED; r++) {
                    long t1 = System.nanoTime();
                    DistrictPlan.make(in);
                    ms[r] = (System.nanoTime() - t1) / 1e6;
                }
                Arrays.sort(ms);
                planMs.add(ms[TIMED / 2]);
                inputMs.add(inMs);
                timed = true;
            }
            add(p, in, "district (" + d.dx + ", " + d.dy + ")");
        }

        void add(DistrictPlan p, DistrictPlan.Input in, String where) {
            Figures f = figures(p, in);
            districts++;
            boolean ok = f.holds(p, in);
            if (ok) held++;
            else if (firstFail.isEmpty()) firstFail = where + ": " + line(p, f) + String.format("; surface %.1f/%.1f/%.1f of %.1f/%.1f/%.1f, %d beside a highway",
                    f.gravel, f.paved, f.highway, in.gravel, in.paved, in.highway, f.besideHighway);
            if (f.pieces <= 1) network++;
            else if (f.groundParts) apart++;
            if (f.closestPlus >= DistrictPlan.LATTICE) floor++;
            if (f.outOfReach == 0) reach++;
            if (f.onStreet == 0) onStreet++;
            if (f.surfaceHolds(p, in)) surface++;
            if (f.besideHighway == 0) beside++;
            if (f.estateHolds()) estate++;
            mines += in.mines.size();
            minesBeyond += f.minesBeyond;
            placed += f.placed;
            overflow += f.overflow;
            buildings += f.placed + f.overflow;
            streets += f.streetPlots;
            tracks += f.tracks;
            seams += f.seams;
            under += f.under;
            if (p.surplus >= DistrictPlan.HALF) { surplus += p.surplus; surplusDistricts++; }
            long need = 0, lost = 0;
            for (int t = 0; t < in.counts.length; t++) {
                if (in.counts[t] <= 0) continue;
                need += (long) in.counts[t] * BuildingVisual.cells(in.types[t]);
                lost += (long) p.overflow[t] * BuildingVisual.cells(in.types[t]);
            }
            needPlots += need;
            overflowPlots += lost;
            // How full: the model's buildings and road over the district's own dry ground.
            long dry = 0;
            for (int y = 0; y < DistrictPlan.SIDE; y++) {
                for (int x = 0; x < DistrictPlan.SIDE; x++) {
                    int q = y * DistrictPlan.FRAME + x;
                    byte t = in.terrain[q];
                    if (in.owned[q] && (t == World.GRASS || t == World.FOREST || t == World.SAND)) dry++;
                }
            }
            double fill = dry > 0 ? (need + in.gravel + in.paved + in.highway) / dry : 0;
            if (fill > worstFill) { worstFill = fill; worstLost = need > 0 ? lost / (double) need : 0; worstWhere = where; }
            for (int k = 0; k < 4; k++) { cellStreets[k] += f.cellStreets[k]; cellGround[k] += f.cellGround[k]; cellCount[k] += f.cellCount[k]; }
        }

        static double median(List<Double> v) {
            if (v.isEmpty()) return 0;
            List<Double> s = new ArrayList<>(v);
            s.sort(null);
            return s.get(s.size() / 2);
        }

        static double max(List<Double> v) {
            double m = 0;
            for (double x : v) m = Math.max(m, x);
            return m;
        }

        void report(String what) {
            out.printf("      %s: %d district(s), %,d of %,d buildings placed (%,d with no place, %.2f%% of their ground), %,d street plots (%,d track, %,d seam,"
                            + " %,d beneath a highway); mines and wells %d, %d beyond reach%n", what, districts, placed, buildings, overflow,
                    pct(overflowPlots, needPlots), streets, tracks, seams, under, mines, minesBeyond);
            out.printf("      ...checks kept: all %d; one network %d (+%d the ground parts), + floor %d, reach %d, none on a street %d, surface %d, none beside a highway %d,"
                    + " T junctions only in an estate cell %d%n", held, network, apart, floor, reach, onStreet, surface, beside, estate);
            if (surplusDistricts > 0) out.printf("      ...road the ladder could not hold: %,.1f plots in %d district(s)%n", surplus, surplusDistricts);
            out.printf("      ...fullest district %s: buildings and road %.1f%% of its dry ground, %.2f%% of its buildings' ground with no place%n",
                    worstWhere, 100 * worstFill, 100 * worstLost);
            out.printf("      ...street share of a cell's ground: homes cells %.1f%% (%d), squared %.1f%% (%d), estate cells %.1f%% (%d); boulevard cells %.1f%% (%d)%n",
                    pct(cellStreets[0], cellGround[0]), cellCount[0], pct(cellStreets[1], cellGround[1]), cellCount[1],
                    pct(cellStreets[2], cellGround[2]), cellCount[2], pct(cellStreets[3], cellGround[3]), cellCount[3]);
            if (timed) out.printf("      ...a plan's time: median %.2f ms a district, the most %.2f ms (the median of %d after one untimed); its inputs %.2f ms%n",
                    median(planMs), max(planMs), TIMED, median(inputMs));
            if (!firstFail.isEmpty()) out.printf("      ...first to fail: %s%n", firstFail);
        }

        /** Whether every check holds on every district (one network where the ground does not part it). */
        boolean allHold() {
            return held == districts;
        }
    }

    /* =====================================================================
       5. MAPCHECK'S FIXTURE OF JERUS'S CITY, AND THE DENSE SCREEN
       ===================================================================== */

    /** Jerus's city x k on the design's square city at the dry place, as MapCheck's copies (MapCheck.Squares): his mines no more than its iron sites. */
    static CityMap squareCity(BuildingVisual.Type[] types, long seed, long[] place, double k) {
        long[] c = new long[types.length];
        for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < c.length; t++) c[t] = Math.round(MapCheck.JERUS_COUNTS[t] * k);
        int mine = -1;
        for (BuildingVisual.Type t : types) if (t != null && t.site() == Resource.IRON) mine = t.id();
        long wantMines = mine >= 0 ? c[mine] : 0;
        if (mine >= 0) c[mine] = 0;
        int side = (int) Math.ceil(Math.sqrt(MapCheck.groundKm2(types) * k / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
        int capacity = (int) Math.round(MapCheck.JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        CityMap m = CityMap.square(seed, place[0], place[1], types, side, capacity, c);
        if (mine >= 0) {
            c[mine] = Math.min(wantMines, m.ownedSites(Resource.IRON));
            m.reconcile(c);
        }
        return m;
    }

    static void fixture(BuildingVisual.Type[] types) {
        out.println("\n--- 5. MapCheck's fixture of Jerus's city: his city x 1 on the design's square city, every district; the dense screen ---");
        long seed = Founding.DEFAULT_WORLD_SEED;
        long[] place = MapCheck.dryPlace(World.of(seed));
        CityMap x1 = squareCity(types, seed, place, 1);
        Tally t = new Tally();
        for (CityMap.District d : x1.districts()) t.add(x1, d, true);
        t.report("his city x 1");
        check("every district of his city x 1 keeps every check", t.allHold());
        check("...in one network, each", t.network == t.districts);
        // The dense copy's screen (MapCheck 3 and 5): 18 x 11 tiles at its site, and the districts they show.
        CityMap dense = squareCity(types, seed, place, MapCheck.TIMES[MapCheck.DENSE]);
        long tx0 = Math.floorDiv(place[0], World.TILE) - MapCheck.SCREEN_ACROSS / 2, ty0 = Math.floorDiv(place[1], World.TILE) - MapCheck.SCREEN_DOWN / 2;
        List<CityMap.District> shown = new ArrayList<>();
        for (int j = 0; j < MapCheck.SCREEN_DOWN; j++) {
            for (int i = 0; i < MapCheck.SCREEN_ACROSS; i++) {
                CityMap.District d = dense.districtOfTile(tx0 + i, ty0 + j);
                if (d != null && !shown.contains(d)) shown.add(d);
            }
        }
        // Each planned cold once (its inputs read from the world, as a screen first asks), then timed warm.
        double cold = 0;
        for (CityMap.District d : shown) {
            long t0 = System.nanoTime();
            DistrictPlan.make(dense.planInput(d));
            cold += (System.nanoTime() - t0) / 1e6;
        }
        Tally ts = new Tally();
        for (CityMap.District d : shown) ts.add(dense, d, true);
        ts.report("the dense screen's districts (x " + String.format("%,.0f", MapCheck.TIMES[MapCheck.DENSE]) + ")");
        double sum = 0;
        for (double v : ts.planMs) sum += v;
        out.printf("      ...the screen's %d districts: planned from nothing (inputs read from the world and plans, the JVM cold) in %.0f ms;"
                + " warm, their plans %.1f ms in all%n", shown.size(), cold, sum);
        check("every district the dense screen shows keeps every check", ts.allHold());
        check("a district's plan, the dense screen's slowest, in no more than " + (int) PLAN_MS + " ms (half the screen's "
                + (int) MapCheck.SCREEN_MS + ")", Tally.max(ts.planMs) <= PLAN_MS);
    }

    /* =====================================================================
       6. A CITY PLAYED AS THE PLAYTEST PLAYS IT
       ===================================================================== */

    static final PrintStream QUIET = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
        @Override public void write(byte[] b, int off, int len) { }
    });

    static void played() throws Exception {
        out.println("\n--- 6. a city played " + MapCheck.MONTHS + " months as the playtest plays it (MapCheck 1's): every district, every "
                + MapCheck.DRAWN_EVERY + "th month ---");
        Path root = Files.createTempDirectory("plancheck");
        GameFiles files = new GameFiles(root.resolve("city"), root.resolve("city-no-legacy"));
        // MapCheck 1's city, on MiningCheck.IRON_SEED's world since 0.7.99 (its iron a kilometre out).
        Game g = new Game(files, LongPlaytest.founding().withWorldSeed(MiningCheck.IRON_SEED));
        PrintStream real = System.out, was = LongPlaytest.out;
        LongPlaytest.out = QUIET;
        System.setOut(QUIET);
        Tally t = new Tally();
        int samples = 0;
        try {
            g.run();
            g.getDebtManager().setAutopilot(LongPlaytest.AUTOPILOT);
            g.setRolloverMode(LongPlaytest.ROLLOVER);
            g.setRescueMode(LongPlaytest.RESCUE_AUTO ? TreasuryFund.RescueMode.AUTOMATIC : TreasuryFund.RescueMode.BUTTON);
            g.setFundDial(LongPlaytest.FUND_DIAL);
            LongPlaytest.villageBuild(g, "House", 40);
            LongPlaytest.villageBuild(g, "Convenience Store", 3);
            LongPlaytest.villageBuild(g, "Mixed Farm", 2);
            for (int m = 0; m < MapCheck.MONTHS; m++) {
                if (m % 6 == 5) {
                    LongPlaytest.advise(g);
                    LongPlaytest.ensureSchools(g);
                }
                if (m == MapCheck.IRON_AT && g.getLandManager().getIronDeposits() == 0) {
                    MiningCheck.makeRoom(g, 0, true);
                    LongPlaytest.build(g, "Iron Mine", 1);
                }
                LongPlaytest.run(g, 1);
                if ((m + 1) % MapCheck.DRAWN_EVERY == 0) {
                    CityMap map = g.getCityMap();
                    for (CityMap.District d : map.districts()) t.add(map, d, false);
                    samples++;
                }
            }
        } finally {
            System.setOut(real);
            LongPlaytest.out = was;
        }
        t.report("month " + g.getMonth() + ", " + samples + " samples");
        check("at every " + MapCheck.DRAWN_EVERY + "th month every district's plan keeps every check", t.allHold() && samples == MapCheck.MONTHS / MapCheck.DRAWN_EVERY);
    }

    /* =====================================================================
       7. ESTATE CELLS LAID TO FIT WHAT THEY HOLD (0.7.90, batch RD5)
       ===================================================================== */

    /** The share of a flat district's plots each estate-band type is set at, alone: 0.4 - room to spare, so what differs is the cells they take. */
    static final double ESTATE_FILL = 0.4;

    /** Each estate-band type on flat owned ground, `count` of it alone, with the game's rules or the prototype's (one spine across every estate cell). */
    static DistrictPlan.Input estateAlone(BuildingVisual.Type[] types, int type, int count, boolean prototype) {
        DistrictPlan.Input in = new DistrictPlan.Input();
        Arrays.fill(in.owned, true);
        in.types = types;
        in.seed = Founding.DEFAULT_WORLD_SEED;
        in.hubX = in.hubY = DistrictPlan.FRAME / 2;
        in.counts = new int[types.length];
        in.counts[type] = count;
        in.asPrototype = prototype;
        return in;
    }

    /** Box plots in a plan's estate cells and their dry ground (the cell's tile, its own arterials with it), and how many it opened. */
    static long[] estateShare(DistrictPlan p, DistrictPlan.Input in) {
        int n = DistrictPlan.FRAME, C = DistrictPlan.CELL;
        boolean[] box = new boolean[DistrictPlan.AREA];
        for (int i = 0; i < p.buildings; i++) for (int y = p.by[i]; y < p.by[i] + p.bh[i]; y++) for (int x = p.bx[i]; x < p.bx[i] + p.bw[i]; x++) box[y * n + x] = true;
        long boxes = 0, ground = 0, cells = 0;
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (p.cellKind[c] != DistrictPlan.ESTATE) continue;
            cells++;
            int x0 = C * (c % DistrictPlan.CELLS_A_SIDE), y0 = C * (c / DistrictPlan.CELLS_A_SIDE);
            for (int y = y0; y < y0 + C; y++) {
                for (int x = x0; x < x0 + C; x++) {
                    int q = y * n + x;
                    byte t = in.terrain[q];
                    if (in.owned[q] && (t == World.GRASS || t == World.FOREST || t == World.SAND)) ground++;
                    if (box[q]) boxes++;
                }
            }
        }
        return new long[] { boxes, ground, cells };
    }

    static void estateLines(BuildingVisual.Type[] types) {
        out.println("\n--- 7. estate cells laid to fit what they hold (0.7.90, ESTATE LINES): each estate-band type alone on flat ground, against the prototype's spine ---");
        java.util.Map<Integer, String> names = new java.util.HashMap<>();
        for (BuildingsTemplate t : new BuildingCatalog().load()) names.put(t.getId(), t.getName());
        boolean noWorse = true, holds = true;
        int fewer4 = 0, fewer9 = 0, same11 = 0, seen4 = 0, seen9 = 0, seen11 = 0;
        long[] gameAll = new long[3], protoAll = new long[3];
        for (BuildingVisual.Type t : types) {
            if (t == null || !t.drawn() || t.site() != null || t.terminal()) continue;
            if (!t.outer() && t.cls() != BuildingVisual.INDUSTRY) continue;
            int[] fp = BuildingVisual.footprint(t);
            // What takes a whole cell is section 2's.
            if (Math.min(fp[0], fp[1]) > DistrictPlan.MIDDLE) continue;
            int count = (int) Math.ceil(ESTATE_FILL * DistrictPlan.SIDE * DistrictPlan.SIDE / (fp[0] * fp[1]));
            DistrictPlan.Input gi = estateAlone(types, t.id(), count, false), pi = estateAlone(types, t.id(), count, true);
            DistrictPlan g = DistrictPlan.make(gi), pr = DistrictPlan.make(pi);
            Figures fg = figures(g, gi), fpr = figures(pr, pi);
            long[] sg = estateShare(g, gi), sp = estateShare(pr, pi);
            for (int k = 0; k < 3; k++) { gameAll[k] += sg[k]; protoAll[k] += sp[k]; }
            boolean ok = g.buildings > pr.buildings || (g.buildings == pr.buildings && g.cellsOpen <= pr.cellsOpen);
            noWorse &= ok;
            holds &= fg.holds(g, gi) && fpr.holds(pr, pi);
            int side = Math.min(fp[0], fp[1]);
            boolean fewer = g.buildings >= pr.buildings && g.cellsOpen < pr.cellsOpen;
            if (side == 4 && fp[0] == fp[1]) { seen4++; if (fewer) fewer4++; }
            if (side == 9 && fp[0] == fp[1]) { seen9++; if (fewer) fewer9++; }
            if (side == 11 && fp[0] == fp[1]) { seen11++; if (g.buildings == pr.buildings && g.cellsOpen == pr.cellsOpen) same11++; }
            out.printf("      %-30s %2dx%-2d: %,5d placed in %2d cells, boxes %4.1f%% of their ground (the spine: %,5d in %2d, %4.1f%%)%s%n",
                    names.getOrDefault(t.id(), "type " + t.id()), fp[0], fp[1], g.buildings, g.cellsOpen, pct(sg[0], sg[1]), pr.buildings, pr.cellsOpen,
                    pct(sp[0], sp[1]), ok ? "" : "  WORSE");
        }
        out.printf("      ...all of them: boxes %.1f%% of their estate cells' ground in %d cells (the spine: %.1f%% in %d)%n", pct(gameAll[0], gameAll[1]), gameAll[2],
                pct(protoAll[0], protoAll[1]), protoAll[2]);
        check("each estate-band type alone holds as many in no more cells than on the prototype's spine, or more", noWorse);
        check("...a 4 x 4 works in fewer: strips 8 deep, two rows of them to each, where the spine's 15 leave rows out of reach", seen4 > 0 && fewer4 == seen4);
        check("...a 9 x 9 plant in fewer: three rows a cell where the spine holds two", seen9 > 0 && fewer9 == seen9);
        check("...an 11 x 11 works in as many: no street layout of a 31-plot cell holds more than 4 (31 = 2 x 11 + 9)", seen11 > 0 && same11 == seen11);
        check("...and every check holds on each, T junctions only in its estate cells", holds);
        // Deterministic: a plan worked out with the full cells' layouts forgotten is the plan worked out with them known.
        boolean same = true;
        for (int b = 0; b < 3; b++) {
            DistrictPlan.Input in = testDistrict(types, b, false);
            DistrictPlan.forgetFullLines();
            DistrictPlan fresh = DistrictPlan.make(in);
            DistrictPlan known = DistrictPlan.make(in);
            same &= placedDigest(fresh) == placedDigest(known) && streetDigest(fresh) == streetDigest(known) && Arrays.equals(fresh.cellStreets, known.cellStreets);
        }
        check("the same inputs give the same plan, the full cells' layouts worked out afresh or known (each budget of the test district)", same);
    }

    /* =====================================================================
       8. INDUSTRY AND THE OUTER KINDS SHARE ESTATE CELLS (0.7.92, batch RD6)
       ===================================================================== */

    /** Section 4's fill with cells to spare (2.10's first, 75%) and its fullest (92%: every cell open, and outer kinds left without a place when the bands keep apart). */
    static final double SPARE_FILL = FILLS[0], FULL_FILL = FILLS[FILLS.length - 1];

    /** A building's band in the plan (spec 2.4): 0 what follows people, 1 industry, 2 the outer kinds - as DistrictPlan deals them. */
    static int band(BuildingVisual.Type t) {
        return t.outer() ? 2 : t.cls() == BuildingVisual.INDUSTRY ? 1 : 0;
    }

    /** The outer-kind buildings standing in a cell whose first building is industry's: industry's cells they share. */
    static int outerInIndustry(DistrictPlan p, BuildingVisual.Type[] types) {
        int[] first = new int[DistrictPlan.CELLS];
        Arrays.fill(first, -1);
        int n = 0;
        for (int i = 0; i < p.buildings; i++) {
            int c = p.bx[i] / DistrictPlan.CELL + (p.by[i] / DistrictPlan.CELL) * DistrictPlan.CELLS_A_SIDE, b = band(types[p.btype[i]]);
            if (first[c] < 0) first[c] = b;
            if (b == 2 && first[c] == 1) n++;
        }
        return n;
    }

    /** Jerus's mix on a flat district at a fill (section 4's), with his roads or none, the bands sharing or apart. */
    static DistrictPlan.Input flatMix(BuildingVisual.Type[] types, double fill, boolean roads, boolean apart) {
        DistrictPlan.Input in = new DistrictPlan.Input();
        Arrays.fill(in.owned, true);
        in.types = types;
        in.seed = Founding.DEFAULT_WORLD_SEED;
        in.hubX = in.hubY = DistrictPlan.FRAME / 2;
        scaled(in, MapCheck.JERUS_COUNTS, PROTO_FRAME * PROTO_FRAME, fill, false);
        if (!roads) in.gravel = in.paved = in.highway = 0;
        in.bandsApart = apart;
        return in;
    }

    static void sharedEstates(BuildingVisual.Type[] types) {
        out.println("\n--- 8. industry and the outer kinds share estate cells (0.7.92, SHARED ESTATES): Jerus's mix on a flat district, the bands sharing and apart ---");
        // Cells to spare: the plan is the bands-apart plan, his roads or none.
        boolean same = true, none = true, holds = true;
        for (int r = 0; r < 2; r++) {
            DistrictPlan.Input in = flatMix(types, SPARE_FILL, r == 0, false), ia = flatMix(types, SPARE_FILL, r == 0, true);
            DistrictPlan p = DistrictPlan.make(in), q = DistrictPlan.make(ia);
            out.printf("      at %.0f%%, %s: %d of %d cells open, %,d placed, %d without a place; apart %d cells, %,d placed%n", SPARE_FILL * 100,
                    r == 0 ? "his roads" : "no road", p.cellsOpen, DistrictPlan.CELLS, p.buildings, p.overflowCount(), q.cellsOpen, q.buildings);
            same &= p.cellsOpen < DistrictPlan.CELLS && placedDigest(p) == placedDigest(q) && streetDigest(p) == streetDigest(q) && Arrays.equals(p.cellStreets, q.cellStreets);
            none &= outerInIndustry(p, types) == 0;
            holds &= figures(p, in).holds(p, in);
        }
        check("with cells to spare the plan is the bands-apart plan, box for box and street for street (his roads, and none)", same);
        check("...and no outer kind stands in an industry cell", none);
        // No cell left: the outer kinds take industry's leftover ground, homes and industry where they were.
        DistrictPlan.Input in = flatMix(types, FULL_FILL, false, false), ia = flatMix(types, FULL_FILL, false, true);
        DistrictPlan p = DistrictPlan.make(in), q = DistrictPlan.make(ia);
        int outerOverP = 0, outerOverQ = 0, otherOver = 0, inner = 0;
        for (int t = 0; t < types.length; t++) {
            if (types[t] == null) continue;
            if (band(types[t]) == 2) { outerOverP += p.overflow[t]; outerOverQ += q.overflow[t]; }
            else otherOver += p.overflow[t] + q.overflow[t];
        }
        boolean kept = true;
        for (int i = 0; i < q.buildings; i++) {
            if (band(types[q.btype[i]]) == 2) continue;
            inner++;
            kept &= i < p.buildings && p.bx[i] == q.bx[i] && p.by[i] == q.by[i] && p.bw[i] == q.bw[i] && p.bh[i] == q.bh[i] && p.btype[i] == q.btype[i];
        }
        int shared = outerInIndustry(p, types);
        Figures f = figures(p, in);
        out.printf("      at %.0f%%, no road: %d of %d cells open, %,d placed, outer kinds without a place %d (apart: %d cells, %,d placed, %d); %d of them in industry's cells;"
                + " homes and industry without a place %d%n", FULL_FILL * 100, p.cellsOpen, DistrictPlan.CELLS, p.buildings, outerOverP, q.cellsOpen, q.buildings, outerOverQ,
                shared, otherOver);
        check("with no cell left the outer kinds take industry's leftover ground: fewer of them without a place than with the bands apart",
                p.cellsOpen == DistrictPlan.CELLS && q.cellsOpen == DistrictPlan.CELLS && outerOverQ > 0 && outerOverP < outerOverQ && shared > 0 && p.buildings > q.buildings);
        check("...every homes and industry building where it stood with the bands apart, in the same order (" + inner + ")", kept && inner > 0);
        holds &= f.holds(p, in);
        // ...and with his roads: the ladder's steps, each build sharing.
        DistrictPlan.Input ir = flatMix(types, FULL_FILL, true, false), iar = flatMix(types, FULL_FILL, true, true);
        DistrictPlan pr = DistrictPlan.make(ir), qr = DistrictPlan.make(iar);
        out.printf("      at %.0f%%, his roads: %,d placed, %d without a place, %d outer kinds in industry's cells, %d builds (apart: %,d placed, %d)%n", FULL_FILL * 100,
                pr.buildings, pr.overflowCount(), outerInIndustry(pr, types), pr.builds, qr.buildings, qr.overflowCount());
        holds &= figures(pr, ir).holds(pr, ir);
        check("...his roads too: fewer without a place than with the bands apart", pr.overflowCount() < qr.overflowCount());
        check("...and every check holds on each, T junctions only in its estate cells", holds);
    }

    /* =====================================================================
       9. A SAVE NAMED ON THE COMMAND LINE: EVERY DISTRICT
       ===================================================================== */

    /** A save folder's autosave, loaded from a copy (a game run autosaves into any city it loads): every district planned, checked and timed. */
    static void save(Path dir) throws Exception {
        out.println("\n--- 9. the save in " + dir + ": every district ---");
        Path root = Files.createTempDirectory("plancheck-save");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Files.createDirectories(files.savesDirectory());
        for (String suffix : new String[] { ".json", "-history.json", "-map.bin" }) {
            Path f = dir.resolve("autosave" + suffix);
            if (Files.exists(f)) Files.copy(f, files.savesDirectory().resolve("autosave" + suffix), StandardCopyOption.REPLACE_EXISTING);
        }
        PrintStream real = System.out;
        System.setOut(QUIET);
        Game g;
        try {
            g = new Game(files);
            g.loadGameSave(GameFiles.AUTOSAVE_SLOT);
        } finally {
            System.setOut(real);
        }
        CityMap m = g.getCityMap();
        // Warm: every district planned once before any is timed.
        java.util.Map<CityMap.District, DistrictPlan> plans = new java.util.HashMap<>();
        for (CityMap.District d : m.districts()) plans.put(d, DistrictPlan.make(m.planInput(d)));
        Tally t = new Tally();
        for (CityMap.District d : m.districts()) t.add(m, d, true);
        t.report("month " + g.getMonth() + ", " + String.format("%,d", g.getPopulationManager().getPopulation()) + " people");
        // Seams: a street on a district's edge it lays with the neighbour's surface - laid by the neighbour too, or not (RD2 draws those as tracks).
        long seams = 0, covered = 0;
        int f = DistrictPlan.FRAME;
        for (CityMap.District d : m.districts()) {
            DistrictPlan p = plans.get(d);
            for (int q = 0; q < DistrictPlan.AREA; q++) {
                if (p.kindAt(q) != DistrictPlan.SEAM) continue;
                seams++;
                int x = q % f, y = q / f;
                boolean byOther = false;
                for (int b = -1; b <= 1 && !byOther; b++) {
                    for (int a = -1; a <= 1 && !byOther; a++) {
                        CityMap.District o = m.district(d.dx + a, d.dy + b);
                        if ((a == 0 && b == 0) || o == null) continue;
                        int ox = x - a * DistrictPlan.SIDE, oy = y - b * DistrictPlan.SIDE;
                        if (ox < 0 || oy < 0 || ox >= f || oy >= f) continue;
                        int k = plans.get(o).kindAt(oy * f + ox);
                        byOther = k != DistrictPlan.NONE && k != DistrictPlan.SEAM && k != DistrictPlan.UNDER;
                    }
                }
                if (byOther) covered++;
            }
        }
        out.printf("      ...seams: %,d street plots on districts' edges laid with the neighbour's surface, %,d of them laid by the neighbour too%n", seams, covered);
        // How full (spec 2.10) with this city's own mix: its buildings and roads at 2.10's fills of a flat district.
        long[] mix = new long[m.types().length];
        for (CityMap.District d : m.districts()) for (int k = 0; k < mix.length; k++) mix[k] += d.count(k);
        StringBuilder sb = new StringBuilder();
        for (double fill : FILLS) {
            DistrictPlan.Input in = new DistrictPlan.Input();
            Arrays.fill(in.owned, true);
            in.types = m.types();
            in.seed = Founding.DEFAULT_WORLD_SEED;
            in.hubX = in.hubY = DistrictPlan.FRAME / 2;
            scaled(in, mix, PROTO_FRAME * PROTO_FRAME, fill, false);
            DistrictPlan p = DistrictPlan.make(in);
            long need = 0, lost = 0;
            for (int k = 0; k < in.counts.length; k++) {
                if (in.counts[k] <= 0) continue;
                need += (long) in.counts[k] * BuildingVisual.cells(in.types[k]);
                lost += (long) p.overflow[k] * BuildingVisual.cells(in.types[k]);
            }
            sb.append(String.format(" %.0f%%: %.1f%%;", fill * 100, pct(lost, need)));
        }
        out.printf("      ...its own mix on a flat district (2.10), the share of its buildings' ground with no place at:%s%n", sb);
        check("every district of the save keeps every check (one network where the ground does not part it)", t.allHold());
        check("...each planned in no more than " + (int) PLAN_MS + " ms", Tally.max(t.planMs) <= PLAN_MS);
    }
}
