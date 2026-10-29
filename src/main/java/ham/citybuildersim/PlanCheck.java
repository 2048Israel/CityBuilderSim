package ham.citybuildersim;

import ham.citybuildersim.sectors.Mining;

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
 *      the time, each within PLAN_MS;
 *  10. homes cells' streets by a hash of their place (batch CL2;
 *      DistrictPlan's LAYOUT): on flat ground whose homes cells are all long
 *      blocks, on flat ground whose homes cells are squared and with
 *      boulevards, and on the test district cut by water, Input.combOnly is
 *      0.8.11's plan box for box and street for street, and with the variety
 *      on every check holds, each of the three layouts (the comb, dropped
 *      crossings, brick) appears in long-block and in squared cells and over
 *      many cells in its share, a cell's layout is a function of the world's
 *      seed and its place and not of what its district holds, two districts
 *      agree along their seam no worse than with the comb, no cell of a
 *      varied layout has a dead end and no plan has more than the comb's -
 *      and a city played as the playtest plays it shows the variety firing;
 *  11. the order of arrival (batch CL3; DistrictPlan's THE ORDER OF ARRIVAL):
 *      a log that is one batch draws today's plan box for box; a later
 *      month's arrivals move none placed before and a departure frees only
 *      its own box (where today's plan, packed again from the counts, moves
 *      many); a cell takes the use of what opens it, so the first works to
 *      come stands in the nearest cell; the outer kinds that stand on no site
 *      open their cells from the far end of the growth order and a mine off
 *      its site from the near end; a homes cell keeps off a mine's site, a
 *      mine's box and a mine the plan placed while another cell can open;
 *      the ladder is decided when a cell opens and its streets never change
 *      after; the dense screen's districts, their counts dealt in forty
 *      batches, cover at least 95% of the ground today's plans cover, every
 *      check holding; and a plan is a pure function of its input, a carry
 *      merged by month and a leftover given back with its month;
 *  12. the ground bought (batch FX6; DistrictPlan's THE GROUND BOUGHT): a
 *      full district that buys whole cells nearer the hub than its farthest
 *      keeps every box where it stands and places the arrivals after the
 *      purchase, some on the new cells, where the same ground owned from the
 *      start moves boxes; a purchase dated at or before the first batch's
 *      month, or no dates at all, is ground from the start;
 *  13. the avenues (batch CL4; DistrictPlan's AVENUES): four avenues at 45
 *      degrees from the founding site, laid when a cell opens - each arm's
 *      staircase plot for plot in the open cells and none in a closed one,
 *      beneath a highway, across the railway, not on a yard or an
 *      interchange's reserve (laid on both sides), bridging water up to
 *      AVENUE_BRIDGE_STEPS and not laid across wider; the + floor along an
 *      avenue (estate streets stop a plot short, with latticeOnly they cross;
 *      a comb cell's streets all meet) and no building on an outer plot; a
 *      homes cell an avenue crosses keeps the comb; every box of a month
 *      stands the next and a cell's avenue plots are the same whichever
 *      month it opened, and ground bought after its cell opened gets the
 *      avenue in the month it is bought; a pure function, two neighbouring districts laying
 *      the same plots and marks on the line they share; and the counts in a
 *      real run;
 *  14. the curves (batch CL4, slice C; DistrictPlan's CURVES): a long-block
 *      homes cell beyond CRESCENT_FROM cells of the founding site, whose CL2
 *      layout is not the comb and whose hash is under CRESCENT_SHARE, is a
 *      crescent - its cross street a quarter arc between its rings, laid plot
 *      for plot as the harness walks the circle out itself, joining both rings
 *      and crossing its three long streets, its curve the staircase and the
 *      circle, its blocks never merged and no box on the plots its band
 *      reaches; none nearer than the distance (the threshold from both sides),
 *      none in a cell something crosses (a railway plot, an avenue), a cell
 *      whose arc the ground would not lay whole keeps its layout, the share is
 *      CRESCENT_SHARE of the cells that are not the comb; each street the
 *      ground's cut lays along water (the test district) is a curve whose line
 *      starts and ends on its plots, turns at no corner tighter than its
 *      radius allows and keeps its band's ground free; a pure function, every
 *      crescent standing as its district grows, and the counts in a real run;
 *  15. the runs dated (batch FX7; DistrictPlan's THE RUNS DATED): a yard laid
 *      across built ground, or a highway beside it (its verge barring the
 *      plots about it), moves none of the boxes standing - those under it
 *      stay and the arrivals after it stand on none of its plots - where the
 *      same run there from the start, with no dates, moves boxes; a yard
 *      lifted again moves none and its ground is room again for the arrivals
 *      after it; records dated at or before the first batch's month are the
 *      ground from the start; a run laid after the log's last month, or after
 *      the first batch of a log that is one batch, moves nothing and the plan
 *      is drawn on today's runs; and a pure function.
 *  17. no street piece walled in by buildings (batch FW11; DistrictPlan's A
 *      PIECE BUILDINGS WALL IN): MapCheck 1's city, every district as the map
 *      draws it (the carry from the districts before it), keeps every check -
 *      the played city's first district stood with a street piece only its
 *      buildings and ground the city does not own enclosed, which the join,
 *      walking free ground alone, left apart; now the way over the buildings
 *      that close it is kept clear and the district planned again, so its
 *      buildings find other lots and the join lays the street (FR8: and
 *      MapCheck's river fixture, whose district (-1, -1) has the case
 *      whatever path the played city takes, planned the same way and tallied
 *      with it); the case
 *      counted across this harness's cities and MapCheck's; a pure function;
 *      and the same districts with no log (the plan with no events) keep every
 *      check and every building, placed or left out.
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
 *     LINE_LAST, LATTICE or more apart (ESTATE LINES);
 *   - (CL4) the + floor along an avenue: of the junctions of a street that is
 *     not the avenue's with an arm's staircase - walked from DistrictPlan.avenue
 *     by this harness's own geometry (Stairs, junctions()) - no two are fewer
 *     than LATTICE steps apart; and no building on an outer plot.
 *   - (CL4, section 14) no building on a plot a curve's band reaches, and the
 *     crescent's arc and a shore curve held to the harness's own working.
 */
public class PlanCheck {

    static int fails = 0;
    static PrintStream out = System.out;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-78s %s%n", label, Timing.verdict(ok, label));
    }

    /** Repeats a district's plan is timed over, after one untimed: its median is its time. */
    static final int TIMED = 5;

    /**
     * The most a district's plan may take, the slowest of the dense screen's
     * districts (each its median of TIMED): half MapCheck's screen, 40 ms
     * (star) - one plan and a screen painted from plans within SCREEN_MS, so
     * long as the paint keeps to the other half. Since 0.7.88 (batch RD2)
     * the plans are made away from the screen's thread, as the map's
     * snapshot is, and kept (CityMap's THE DRAWN PLANS), so a frame paints
     * from plans made already.
     */
    static final double PLAN_MS = MapCheck.SCREEN_MS / 2;

    /** The prototype's figures of the spec's 2.5 table, for his roads, the same trips paved and half that: cells, homes cells, street plots, narrow, full, tracks, square cells, boulevard cells, + junctions, buildings touching a street (facts.json). */
    static final int[][] TABLE = {
            { 49, 15, 9802, 828, 8974, 0, 13, 26, 280, 2995 },
            { 48, 13, 6052, 3137, 2915, 0, 0, 0, 119, 2516 },
            { 48, 13, 6052, 4483, 0, 1569, 0, 0, 119, 2516 } };

    /** ...its buildings and their plots... */
    static final int TABLE_BUILDINGS = 3617, TABLE_BUILDING_PLOTS = 32217;
    /** ...and the road of each budget, whole plots. */
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
    /** Each cell's nudge, h32(4127, 'cell', c) as a fraction, cell ci + cj x CELLS_A_SIDE. */
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
    /** Each cell's direction, a bit a cell: set where h32(4127, 'dir', c) is under a half (east-west), read as 0.25, else 0.75. */
    static final long TEST_ACROSS = 2483539012620168834L;
    /** The types its buildings are, by id (BuildingVisual.table())... */
    static final int[] TEST_TYPES = { 0, 1, 2, 4, 5, 6, 7, 9, 10, 11, 12, 17, 18, 19, 20, 22, 23, 25, 27, 28, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 43, 49, 50, 51, 53, 56, 57, 58, 59, 60, 61, 65, 66, 69, 71 };
    /** ...how many of each, scaled_buildings at 70% of its dry ground... */
    static final int[] TEST_COUNTS = { 899, 94, 569, 30, 2, 799, 2, 1, 2, 23, 5, 112, 54, 1, 1, 13, 6, 4, 1, 2, 22, 22, 19, 3, 3, 2, 1, 1, 1, 3, 18, 1, 3, 53, 50, 2, 7, 53, 69, 12, 5, 1, 3, 54, 62, 527 };
    /** ...and each type's start, h32(4127, 'deal', t) as a fraction. */
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
    /** Its road plots (Input.gravel and Input.paved): his Gravel and Paved Roads, and the same trips all paved (the third budget half of it). */
    static final double TEST_GRAVEL = 8407.741935483871, TEST_PAVED = 980.6451612903226, TEST_PAVED_ALL = 4483.870967741936;
    /** Each budget's plan as the prototype made it, its buildings' and its streets' FNV-1a digests (placedDigest(), streetDigest()): his roads... */
    static final long TEST_PLACED_JERUS = -1980402857778823524L, TEST_STREETS_JERUS = -7143981648789130548L;
    /** ...the same trips paved... */
    static final long TEST_PLACED_PAVED = 9000504916410474359L, TEST_STREETS_PAVED = -4283127041626870812L;
    /** ...and half that. */
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
        streetLayouts(types);
        arrivals(types);
        groundBought(types);
        avenues(types);
        curves(types);
        runsDated(types);
        farBank(types);
        walledIn(types);
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
        /** (CL4) pairs of junctions along an avenue fewer than LATTICE steps apart, and buildings with a plot on an avenue's outer plot. */
        int avenueBad, avenueOuter;
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
                    && surfaceHolds(p, in) && besideHighway == 0 && estateHolds() && avenueBad == 0 && avenueOuter == 0;
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
        // (CL4) The avenues: no box on an outer plot; the + floor along each arm, from the plan's own marks.
        if (!in.asPrototype && !in.latticeOnly) {
            Stairs stairsIn = stairs(in);
            for (int i = 0; i < p.buildings; i++) {
                boolean hit = false;
                for (int y = p.by[i]; y < p.by[i] + p.bh[i] && !hit; y++) for (int x = p.bx[i]; x < p.bx[i] + p.bw[i]; x++) if (stairsIn.outer[y * n + x]) { hit = true; break; }
                if (hit) f.avenueOuter++;
            }
            if (p.avenue != null) {
                boolean[] laid = new boolean[area];
                for (int q = 0; q < area; q++) laid[q] = (p.avenue[q] & CityRuns.M_DIAG) != 0;
                for (int a = 0; a < 4; a++) f.avenueBad += floorBad(junctions(p, stairsIn, a, laid, null));
            }
        }
        return f;
    }

    /* =====================================================================
       THE AVENUES, THE HARNESS'S OWN WORKING (batch CL4)
       ===================================================================== */

    /** The four avenues' headings (dx, dy) by arm, CityRuns.diagCode()'s numbering: south-east 0, south-west 1, north-west 2, north-east 3. */
    static final int[] ARM_DX = { 1, -1, -1, 1 }, ARM_DY = { 1, 1, -1, -1 };

    /** An input's avenues as the harness works them out from the founding site (CL4) - not by calling the plan's code: the origin, each staircase plot's mark in CityRuns' bits and the outer plots. */
    static final class Stairs {
        /** The origin in the frame's plots: the lattice node nearest the founding site. */
        int ox, oy;
        /** Per plot, the mark an avenue plot there carries (the first arm's where two share it), else 0. */
        final byte[] mark = new byte[DistrictPlan.AREA];
        /** ...and the outer plots, where no building stands. */
        final boolean[] outer = new boolean[DistrictPlan.AREA];
        /** Each arm's first and last step to look at: those whose plots can touch the frame. */
        final int[] from = new int[4], to = new int[4];
    }

    /** The harness's staircase plot at (x, y) of arm a, a corner or not; kept if the frame holds it and no earlier arm marked it. */
    static void mark(Stairs s, int a, int x, int y, int corner) {
        int n = DistrictPlan.FRAME;
        if (x < 0 || y < 0 || x >= n || y >= n || s.mark[y * n + x] != 0) return;
        s.mark[y * n + x] = (byte) (CityRuns.M_DIAG | (a << CityRuns.M_DIAG_SHIFT) | corner);
    }

    /** An input's avenues, worked out here (CL4): step k >= 0 of an arm is S_k = (ox + dx k, oy + dy k), for k >= 1 its corner C_k = (ox + dx k, oy + dy (k - 1)) and its outer plot O_k = (ox + dx (k - 1), oy + dy k). */
    static Stairs stairs(DistrictPlan.Input in) {
        Stairs s = new Stairs();
        int n = DistrictPlan.FRAME, l = DistrictPlan.LATTICE;
        long sx = (long) Math.floor(in.x0 + in.hubX), sy = (long) Math.floor(in.y0 + in.hubY);
        s.ox = (int) (Math.floorDiv(sx + l / 2, (long) l) * l - in.x0);
        s.oy = (int) (Math.floorDiv(sy + l / 2, (long) l) * l - in.y0);
        for (int a = 0; a < 4; a++) {
            int dx = ARM_DX[a], dy = ARM_DY[a];
            int lo = Math.max(0, Math.max(dx > 0 ? -2 - s.ox : s.ox - n - 1, dy > 0 ? -2 - s.oy : s.oy - n - 1));
            int hi = Math.min(dx > 0 ? n + 1 - s.ox : s.ox + 2, dy > 0 ? n + 1 - s.oy : s.oy + 2);
            s.from[a] = lo;
            s.to[a] = hi;
            for (int k = lo; k <= hi; k++) {
                mark(s, a, s.ox + dx * k, s.oy + dy * k, 0);
                if (k < 1) continue;
                mark(s, a, s.ox + dx * k, s.oy + dy * (k - 1), CityRuns.M_CORNER);
                int x = s.ox + dx * (k - 1), y = s.oy + dy * k;
                if (x >= 0 && y >= 0 && x < n && y < n) s.outer[y * n + x] = true;
            }
        }
        for (int q = 0; q < s.mark.length; q++) if (s.mark[q] != 0) s.outer[q] = false;
        return s;
    }

    /** Whether plot (x, y) lies in the box of a cell the plan opened - its interior or one of its ring lines. */
    static boolean inOpenBox(DistrictPlan p, int x, int y) {
        int c = DistrictPlan.CELL, side = DistrictPlan.CELLS_A_SIDE;
        for (int cj = Math.max(0, y / c - 1); cj <= Math.min(side - 1, y / c); cj++) {
            if (y < c * cj || y > c * cj + c) continue;
            for (int ci = Math.max(0, x / c - 1); ci <= Math.min(side - 1, x / c); ci++) {
                if (x >= c * ci && x <= c * ci + c && p.cellKind[ci + cj * side] != DistrictPlan.CLOSED) return true;
            }
        }
        return false;
    }

    /** Whether the harness's staircase has a plot in the interior of the cell (ci, cj). */
    static boolean avenueInCell(Stairs s, int ci, int cj) {
        int x0 = DistrictPlan.CELL * ci + 1, y0 = DistrictPlan.CELL * cj + 1;
        for (int y = y0; y < y0 + DistrictPlan.INTERIOR; y++) for (int x = x0; x < x0 + DistrictPlan.INTERIOR; x++) if (s.mark[y * DistrictPlan.FRAME + x] != 0) return true;
        return false;
    }

    /**
     * The junctions of arm a (CL4's + floor), ascending and distinct, by the harness's own walk: step j has one when a street plot that is
     * no avenue's - role ROLE_STREET, or ROLE_ARTERIAL or ROLE_BOULEVARD on the ring line that runs the foot's way (a column, for a foot beyond
     * a plot in its column, or a row, a multiple of CELL), never a join's, a shore's or an access road's - is one of its four FEET and the plot beyond it, away from the avenue, is a street too (and no avenue plot of any arm, as about the origin): the plot beyond the corner C_j in
     * its column, beyond S_j in its column, the outer plot O_j beside S_j, and the plot beyond the corner C_(j+1) in its row, each touching
     * the avenue plot (laid[]) it is beyond. cellStreets, when given, gets its [0] raised by the junctions a cell's street (ROLE_STREET) makes.
     */
    static int[] junctions(DistrictPlan p, Stairs s, int a, boolean[] laid, int[] cellStreets) {
        int n = DistrictPlan.FRAME, dx = ARM_DX[a], dy = ARM_DY[a];
        List<Integer> steps = new ArrayList<>();
        for (int j = Math.max(0, s.from[a] - 2); j <= s.to[a] + 2; j++) {
            boolean meets = false, street = false;
            for (int f = 0; f < 4; f++) {
                if (f == 0 && j < 1) continue;
                int fx, fy, qx, qy;
                if (f == 0) { fx = s.ox + dx * j; fy = s.oy + dy * (j - 2); qx = fx; qy = s.oy + dy * (j - 1); }
                else if (f == 1) { fx = s.ox + dx * j; fy = s.oy + dy * (j + 1); qx = fx; qy = s.oy + dy * j; }
                else if (f == 2) { fx = s.ox + dx * (j - 1); fy = s.oy + dy * j; qx = s.ox + dx * j; qy = fy; }
                else { fx = s.ox + dx * (j + 2); fy = s.oy + dy * j; qx = s.ox + dx * (j + 1); qy = fy; }
                int bx = 2 * fx - qx, by = 2 * fy - qy;
                if (fx < 0 || fy < 0 || fx >= n || fy >= n || qx < 0 || qy < 0 || qx >= n || qy >= n || bx < 0 || by < 0 || bx >= n || by >= n) continue;
                int q = fy * n + fx;
                if (!laid[qy * n + qx] || s.mark[q] != 0 || s.mark[by * n + bx] != 0 || p.kindAt(q) == DistrictPlan.NONE || p.kindAt(by * n + bx) == DistrictPlan.NONE) continue;
                int r = p.roleAt(q);
                boolean ring = (r == DistrictPlan.ROLE_ARTERIAL || r == DistrictPlan.ROLE_BOULEVARD) && (f < 2 ? fx : fy) % DistrictPlan.CELL == 0;
                if (r == DistrictPlan.ROLE_STREET || ring) {
                    meets = true;
                    if (r == DistrictPlan.ROLE_STREET) street = true;
                }
            }
            if (meets) {
                steps.add(j);
                if (street && cellStreets != null) cellStreets[0]++;
            }
        }
        int[] out = new int[steps.size()];
        for (int i = 0; i < out.length; i++) out[i] = steps.get(i);
        return out;
    }

    /** The pairs of consecutive junctions fewer than LATTICE steps apart. */
    static int floorBad(int[] steps) {
        int bad = 0;
        for (int i = 1; i < steps.length; i++) if (steps[i] - steps[i - 1] < DistrictPlan.LATTICE) bad++;
        return bad;
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

    /** The three budgets' names, as the sections print them. */
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
        /** Districts planned again with the way to a piece the buildings wall in kept clear (FW11), and the plots so kept clear in them. */
        int walled, kept;
        long buildings, placed, overflow, overflowPlots, needPlots, streets, tracks, seams, under, surplusDistricts;
        double surplus, worstFill, worstLost;
        String worstWhere = "", firstFail = "";
        final long[] cellStreets = new long[4], cellGround = new long[4];
        final int[] cellCount = new int[4];
        /** The open homes cells by kind (0 long blocks, 1 squared) and layout (CL2), and the merges undone in them. */
        final int[][] homes = new int[2][3];
        /** ...and the places of those cells (the district's corner and the cell's number in it), so a cell sampled again counts once in homesPlaces() (WN3). */
        final java.util.Set<String> homesAt = new java.util.HashSet<>();
        long mergesUndone;
        /** The avenues' counts summed (CL4): plots laid, beneath a highway, across the railway, bridging, not laid at a yard, reserve or mine, at water too wide, stops, open cells crossed; and the floor and outer-plot breaches. */
        final long[] av = new long[8];
        int avenueBad, avenueOuter;
        /** The curves' counts summed (CL4, slice C): crescents, crescents the ground would not lay whole, streets along a cut drawn as curves, curves in all. */
        final long[] cv = new long[4];
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
            if (p.wallTries > 0) walled++;
            kept += p.wallClear;
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
            int[][] h = homesByLayout(p);
            for (int k = 0; k < 2; k++) for (int l = 0; l < 3; l++) homes[k][l] += h[k][l];
            for (int c = 0; c < DistrictPlan.CELLS; c++) if (p.cellKind[c] == DistrictPlan.HOMES && p.cellLayout[c] <= DistrictPlan.LAYOUT_BRICK) homesAt.add(in.x0 + "," + in.y0 + "," + c);
            mergesUndone += p.mergesUndone;
            av[0] += p.avenuePlots; av[1] += p.avenueUnder; av[2] += p.avenueRail; av[3] += p.avenueBridged;
            av[4] += p.avenueBroken; av[5] += p.avenueWater; av[6] += p.avenueStops; av[7] += p.avenueCells;
            avenueBad += f.avenueBad;
            avenueOuter += f.avenueOuter;
            cv[0] += p.crescents; cv[1] += p.crescentsOff; cv[2] += p.shoreCurves; cv[3] += p.curves.size();
        }

        /** The open homes cells that took a layout other than the comb or the whole square. */
        int varied() {
            int n = 0;
            for (int k = 0; k < 2; k++) for (int l = 1; l < 3; l++) n += homes[k][l];
            return n;
        }

        /** The open homes cells by place: a cell sampled in four months is one (WN3; homesCells() counts it four times). */
        int homesPlaces() { return homesAt.size(); }

        /** All the open homes cells. */
        int homesCells() {
            return homes[0][0] + homes[0][1] + homes[0][2] + homes[1][0] + homes[1][1] + homes[1][2];
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
            if (walled > 0) out.printf("      ...pieces the buildings wall in (FW11): %d district(s) planned again with the way to them kept clear, %d plot(s) so kept%n", walled, kept);
            if (surplusDistricts > 0) out.printf("      ...road the ladder could not hold: %,.1f plots in %d district(s)%n", surplus, surplusDistricts);
            out.printf("      ...fullest district %s: buildings and road %.1f%% of its dry ground, %.2f%% of its buildings' ground with no place%n",
                    worstWhere, 100 * worstFill, 100 * worstLost);
            out.printf("      ...street share of a cell's ground: homes cells %.1f%% (%d), squared %.1f%% (%d), estate cells %.1f%% (%d); boulevard cells %.1f%% (%d)%n",
                    pct(cellStreets[0], cellGround[0]), cellCount[0], pct(cellStreets[1], cellGround[1]), cellCount[1],
                    pct(cellStreets[2], cellGround[2]), cellCount[2], pct(cellStreets[3], cellGround[3]), cellCount[3]);
            out.printf("      ...homes cells by layout over the samples: %s; merges undone %,d%n", layoutLine(homes), mergesUndone);
            out.printf("      ...avenues (CL4), summed over the districts: %,d plots laid (%,d beneath a highway, %,d across the railway, %,d bridging water), %,d not laid at a yard, reserve or mine,"
                    + " %,d at water too wide, %,d cell streets stopped short, %,d open cells crossed; %d box(es) on an outer plot, %d breach(es) of the + floor along an avenue%n",
                    av[0], av[1], av[2], av[3], av[4], av[5], av[6], av[7], avenueOuter, avenueBad);
            out.printf("      ...curves (CL4), summed over the districts: %,d crescents (%,d the ground would not lay whole), %,d streets along a cut drawn as curves, %,d curves%n", cv[0], cv[1], cv[2], cv[3]);
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

    /** Jerus's city x k on the design's square city at the dry place, as MapCheck's copies (MapCheck.Squares): his mines no more than its iron sites of their type (since 0.8.0, an Iron Mine's hematite). */
    static CityMap squareCity(BuildingVisual.Type[] types, long seed, long[] place, double k) {
        long[] c = new long[types.length];
        for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < c.length; t++) c[t] = Math.round(MapCheck.JERUS_COUNTS[t] * k);
        int mine = -1;
        for (BuildingVisual.Type t : types) if (t != null && t.id() == Mining.IRON_MINE) mine = t.id();
        long wantMines = mine >= 0 ? c[mine] : 0;
        if (mine >= 0) c[mine] = 0;
        int side = (int) Math.ceil(Math.sqrt(MapCheck.groundKm2(types) * k / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
        int capacity = (int) Math.round(MapCheck.JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT);
        CityMap m = CityMap.square(seed, place[0], place[1], types, side, capacity, c);
        if (mine >= 0) {
            c[mine] = Math.min(wantMines, m.ownedSites(Resource.IRON, m.types()[mine].field()));
            m.reconcile(c);
        }
        return m;
    }

    /** The dense copy of his city (MapCheck.DENSE) and the districts its screen shows, made once and kept: section 5 times their plans and section 11 deals their counts as a log. */
    static CityMap denseCity;
    static List<CityMap.District> denseShown;

    /** denseCity and denseShown made, if they are not. */
    static void denseScreen(BuildingVisual.Type[] types) {
        if (denseCity != null) return;
        long seed = Founding.DEFAULT_WORLD_SEED;
        long[] place = MapCheck.dryPlace(World.of(seed));
        CityMap dense = squareCity(types, seed, place, MapCheck.TIMES[MapCheck.DENSE]);
        long tx0 = Math.floorDiv(place[0], World.TILE) - MapCheck.SCREEN_ACROSS / 2, ty0 = Math.floorDiv(place[1], World.TILE) - MapCheck.SCREEN_DOWN / 2;
        List<CityMap.District> shown = new ArrayList<>();
        for (int j = 0; j < MapCheck.SCREEN_DOWN; j++) {
            for (int i = 0; i < MapCheck.SCREEN_ACROSS; i++) {
                CityMap.District d = dense.districtOfTile(tx0 + i, ty0 + j);
                if (d != null && !shown.contains(d)) shown.add(d);
            }
        }
        denseCity = dense;
        denseShown = shown;
    }

    static void fixture(BuildingVisual.Type[] types) {
        out.println("\n--- 5. MapCheck's fixture of Jerus's city: his city x 1 on the design's square city, every district; the dense screen ---");
        long seed = Founding.DEFAULT_WORLD_SEED;
        long[] place = MapCheck.dryPlace(World.of(seed));
        CityMap x1 = squareCity(types, seed, place, 1);
        Tally t = new Tally();
        for (CityMap.District d : x1.districts()) t.add(x1, d, true);
        t.report("his city x 1");
        count(t);
        check("every district of his city x 1 keeps every check", t.allHold());
        check("...in one network, each", t.network == t.districts);
        // The dense copy's screen (MapCheck 3 and 5): 18 x 11 tiles at its site, and the districts they show.
        x1Avenues = t.av.clone();
        x1Curves = t.cv.clone();
        check("...avenues are laid in his city x 1, and no box stands on an outer plot nor does the + floor along an avenue break (" + t.av[0] + " plots in " + t.av[7] + " open cells)",
                t.av[0] > 0 && t.avenueBad == 0 && t.avenueOuter == 0);
        denseScreen(types);
        CityMap dense = denseCity;
        List<CityMap.District> shown = denseShown;
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
        count(ts);
        double sum = 0;
        for (double v : ts.planMs) sum += v;
        out.printf("      ...the screen's %d districts: planned from nothing (inputs read from the world and plans, the JVM cold) in %.0f ms;"
                + " warm, their plans %.1f ms in all%n", shown.size(), cold, sum);
        check("every district the dense screen shows keeps every check", ts.allHold());
        check("a district's plan, the dense screen's slowest, in no more than " + (int) PLAN_MS + " ms (half the screen's "
                + (int) MapCheck.SCREEN_MS + ")", Timing.within(Tally.max(ts.planMs), PLAN_MS));
    }

    /* =====================================================================
       6. A CITY PLAYED AS THE PLAYTEST PLAYS IT
       ===================================================================== */

    /** A stream that drops what is written to it: the game's and the playtest's prose while a section plays or loads a city. */
    static final PrintStream QUIET = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
        @Override public void write(byte[] b, int off, int len) { }
    });

    static void played() throws Exception {
        out.println("\n--- 6. a city played " + MapCheck.MONTHS + " months as the playtest plays it (MapCheck 1's): every district, every "
                + MapCheck.DRAWN_EVERY + "th month ---");
        Path root = TempDirs.create("plancheck");
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
                    LongPlaytest.build(g, LongPlaytest.nameOf(g, Mining.IRON_MINE), 1);
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
        count(t);
        check("at every " + MapCheck.DRAWN_EVERY + "th month every district's plan keeps every check", t.allHold() && samples == MapCheck.MONTHS / MapCheck.DRAWN_EVERY);
        playedAvenues = t.av.clone();
        playedCurves = t.cv.clone();
        check("...the avenues were laid in the played city (" + t.av[0] + " plots in " + t.av[7] + " open cells over the samples), every check holding with them", t.av[0] > 0 && t.allHold());
        // CL2: how often the variety fired in this run. A city of a few homes cells says little, so the assertion waits for enough of them.
        // WN3: ENOUGH PLACES, NOT SAMPLED CELLS. The samples read the same cells again (a town's homes stand in the same few cells at every
        // look), so 11 cells of the samples were 3 places on the world's band (8 were 2 on the literals, one short of the threshold, which is
        // why this never fired before): three cells all keeping the comb by the hash alone is a chance of 1 in 27, not 1 in 59,049.
        if (t.homesPlaces() >= HOMES_ENOUGH) {
            check("...the layouts fired: of " + t.homesPlaces() + " homes cells (" + t.homesCells() + " in the samples) " + t.varied() + " took dropped crossings or brick", t.varied() > 0);
        } else {
            out.printf("      ...only %d homes cells (%d in the samples), fewer than the %d a layout's share needs to show: no assertion that the variety fired%n", t.homesPlaces(), t.homesCells(), HOMES_ENOUGH);
        }
    }

    /** The homes cells (places, each counted once however often it is sampled) a played city's samples must hold before they are asked to show a layout other than the comb (CL2): ten cells all keeping the comb by the hash alone has a chance of 1 in 59,049. */
    static final int HOMES_ENOUGH = 10;

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
        // CL4: section 7 compares ESTATE LINES with the prototype's spine on flat ground, cell by cell - a plan from before the avenues; they are section 13's.
        in.latticeOnly = true;
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
       10. HOMES CELLS' STREETS BY A HASH OF THEIR PLACE (CL2)
       ===================================================================== */

    /** LONG's roads, a factor of his (section 4's 75% fill): 0.5 - too little road for the ladder to square a homes cell, so all of them keep long blocks. */
    static final double LONG_ROADS = 0.5;

    /** SQUARED's: 0.7 - road enough to square most of the homes cells and not to make boulevards of them. */
    static final double SQUARED_ROADS = 0.7;

    /** BOULEVARD's: 1, his own - road enough for boulevards, squared cells among them. */
    static final double BOULEVARD_ROADS = 1;

    /** Section 10's fixtures: flat ground at his fill with his roads times a factor (the first three), and the test district's ground, a river and the sea cutting cells, on its first two budgets. */
    static final String[] LAYOUT_FIXTURES = { "LONG", "SQUARED", "BOULEVARD", "CUT0", "CUT1" };

    /** Each fixture's plan with Input.combOnly, its buildings' and its streets' digests (placedDigest(), streetDigest()) as 0.8.11 drew them: the switch off is that plan, box for box and street for street. */
    static final long[][] COMB_DIGESTS = {
            { -8123742265030361694L, -580478996347052330L },
            { 6065673935051394403L, 4482261714529494111L },
            { 5888432815123760610L, 2938454372846433564L },
            { -6965839022495023265L, 6467073671628129364L },
            { -244617492940599626L, -3549788141600851655L } };

    /** The cells on a side of the square of the lattice layoutOf() is asked about to measure its shares: 200, 40,000 cells. */
    static final int SHARE_SIDE = 200;

    /** How far a layout's share of those cells may stand from its dial: 0.01. */
    static final double SHARE_TOLERANCE = 0.01;

    /** The part more of the homes band's largest type LONG is given, to see a cell keep its layout as its district grows: a tenth. */
    static final double MORE_HOMES = 0.1;

    /** Another world's seed, to see the layouts read it. */
    static final long OTHER_SEED = Founding.DEFAULT_WORLD_SEED + 1;

    /** Section 10's fixture k (LAYOUT_FIXTURES): the flat district with his roads times a factor, or the test district's ground and budget k - 3; with combOnly when asked. */
    static DistrictPlan.Input layoutInput(BuildingVisual.Type[] types, int k, boolean combOnly) {
        DistrictPlan.Input in;
        if (k < 3) {
            in = flatMix(types, FILLS[0], true, false);
            double roads = k == 0 ? LONG_ROADS : k == 1 ? SQUARED_ROADS : BOULEVARD_ROADS;
            in.gravel *= roads;
            in.paved *= roads;
        } else {
            in = testDistrict(types, k - 3, false);
        }
        in.combOnly = combOnly;
        return in;
    }

    /** A plan's open homes cells counted by kind (0 long blocks, 1 squared; a boulevard's interior by whether it is squared) and layout. */
    static int[][] homesByLayout(DistrictPlan p) {
        int[][] n = new int[2][3];
        for (int c = 0; c < DistrictPlan.CELLS; c++) if (p.cellKind[c] == DistrictPlan.HOMES && p.cellLayout[c] <= DistrictPlan.LAYOUT_BRICK) n[p.cellSquare[c] ? 1 : 0][p.cellLayout[c]]++;
        return n;
    }

    /** Homes cells by kind and layout as a line. */
    static String layoutLine(int[][] n) {
        return String.format("long blocks today %d, dropped %d, brick %d; squared today %d, dropped %d, brick %d", n[0][0], n[0][1], n[0][2], n[1][0], n[1][1], n[1][2]);
    }

    /** A plan's open homes cells: how many, how many squared, how many boulevards, how many of those squared, and how many squared and not boulevards. */
    static int[] homesKinds(DistrictPlan p) {
        int[] k = new int[5];
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (p.cellKind[c] != DistrictPlan.HOMES) continue;
            k[0]++;
            if (p.cellSquare[c]) k[1]++;
            if (p.cellBoulevard[c]) { k[2]++; if (p.cellSquare[c]) k[3]++; }
            if (p.cellSquare[c] && !p.cellBoulevard[c]) k[4]++;
        }
        return k;
    }

    /** The dead ends of a plan: street plots (any kind but none) of the frame's interior, not its first or last row or column, with at most one street among their four neighbours - only those inside the interior of a homes cell whose layout is not LAYOUT_TODAY when `variedOnly`. */
    static int deadEnds(DistrictPlan p, boolean variedOnly) {
        int f = DistrictPlan.FRAME, dead = 0;
        for (int y = 1; y < f - 1; y++) {
            for (int x = 1; x < f - 1; x++) {
                int q = y * f + x;
                if (p.kindAt(q) == DistrictPlan.NONE) continue;
                int around = 0;
                if (p.kindAt(q - 1) != DistrictPlan.NONE) around++;
                if (p.kindAt(q + 1) != DistrictPlan.NONE) around++;
                if (p.kindAt(q - f) != DistrictPlan.NONE) around++;
                if (p.kindAt(q + f) != DistrictPlan.NONE) around++;
                if (around > 1) continue;
                if (variedOnly) {
                    if (x % DistrictPlan.CELL == 0 || y % DistrictPlan.CELL == 0) continue;
                    int c = x / DistrictPlan.CELL + (y / DistrictPlan.CELL) * DistrictPlan.CELLS_A_SIDE;
                    if (p.cellKind[c] != DistrictPlan.HOMES || p.cellLayout[c] == DistrictPlan.LAYOUT_TODAY) continue;
                }
                dead++;
            }
        }
        return dead;
    }

    /** Street plots and dry ground in the open homes cells' tiles (a cell's interior and its own west and north arterials, as Figures.cellStreets has them), by kind and layout: row kind x 3 + layout, {street plots, dry ground}. */
    static long[][] homesStreetShare(DistrictPlan p, DistrictPlan.Input in) {
        long[][] s = new long[6][2];
        int n = DistrictPlan.FRAME;
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (p.cellKind[c] != DistrictPlan.HOMES || p.cellLayout[c] > DistrictPlan.LAYOUT_BRICK) continue;
            long[] row = s[(p.cellSquare[c] ? 3 : 0) + p.cellLayout[c]];
            int x0 = DistrictPlan.CELL * (c % DistrictPlan.CELLS_A_SIDE), y0 = DistrictPlan.CELL * (c / DistrictPlan.CELLS_A_SIDE);
            for (int y = y0; y < y0 + DistrictPlan.CELL; y++) {
                for (int x = x0; x < x0 + DistrictPlan.CELL; x++) {
                    int q = y * n + x;
                    byte t = in.terrain[q];
                    if (in.owned[q] && (t == World.GRASS || t == World.FOREST || t == World.SAND)) row[1]++;
                    int k = p.kindAt(q);
                    if (k != DistrictPlan.NONE && k != DistrictPlan.UNDER) row[0]++;
                }
            }
        }
        return s;
    }

    /** The street share of homesStreetShare() as a line, a layout of a kind at a time. */
    static String shareLine(long[][] s) {
        return String.format("long blocks today %.1f%%, dropped %.1f%%, brick %.1f%%; squared today %.1f%%, dropped %.1f%%, brick %.1f%%",
                pct(s[0][0], s[0][1]), pct(s[1][0], s[1][1]), pct(s[2][0], s[2][1]), pct(s[3][0], s[3][1]), pct(s[4][0], s[4][1]), pct(s[5][0], s[5][1]));
    }

    /** Whether every cell of a plan keeps LAYOUT_TODAY: the comb, the whole square, an estate. */
    static boolean allComb(DistrictPlan p) {
        for (int c = 0; c < DistrictPlan.CELLS; c++) if (p.cellLayout[c] != DistrictPlan.LAYOUT_TODAY) return false;
        return true;
    }

    /** Whether a highway, railway, yard or reserve (Input.fixed) has a plot in the box of the cell (ci, cj), its interior and its ring, or an avenue one in its interior: DistrictPlan's crossed(), such a homes cell keeping the comb. */
    static boolean crossedCell(DistrictPlan.Input in, int ci, int cj) {
        int x0 = DistrictPlan.CELL * ci + 1, y0 = DistrictPlan.CELL * cj + 1;
        // ...or an avenue (CL4): a staircase plot of the harness's own in its interior.
        if (!in.asPrototype && !in.latticeOnly && avenueInCell(stairs(in), ci, cj)) return true;
        for (int y = Math.max(0, y0 - 1); y <= Math.min(DistrictPlan.FRAME - 1, y0 + DistrictPlan.INTERIOR); y++) {
            for (int x = Math.max(0, x0 - 1); x <= Math.min(DistrictPlan.FRAME - 1, x0 + DistrictPlan.INTERIOR); x++) if (in.fixed[y * DistrictPlan.FRAME + x] != 0) return true;
        }
        return false;
    }

    /** The layout cell c of a plan has to have: its place's (layoutOf of the seed and the cell's north-west plot in the world) for a homes cell no highway, railway, yard or reserve crosses, else LAYOUT_TODAY. */
    static byte expectedLayout(DistrictPlan p, DistrictPlan.Input in, int c) {
        int ci = c % DistrictPlan.CELLS_A_SIDE, cj = c / DistrictPlan.CELLS_A_SIDE;
        if (p.cellKind[c] != DistrictPlan.HOMES || crossedCell(in, ci, cj)) return DistrictPlan.LAYOUT_TODAY;
        return DistrictPlan.layoutOf(in.seed, in.x0 + (long) DistrictPlan.CELL * ci, in.y0 + (long) DistrictPlan.CELL * cj, p.cellSquare[c]);
    }

    static void streetLayouts(BuildingVisual.Type[] types) {
        out.println("\n--- 10. homes cells' streets by a hash of their place (CL2): the comb, dropped crossings, brick ---");
        int n = LAYOUT_FIXTURES.length;
        long seed = Founding.DEFAULT_WORLD_SEED;
        DistrictPlan.Input[] in = new DistrictPlan.Input[n];
        DistrictPlan[] on = new DistrictPlan[n], off = new DistrictPlan[n], pinned = new DistrictPlan[n];
        Figures[] fig = new Figures[n];
        int[] dead = new int[n], deadOff = new int[n], deadVaried = new int[n];
        int[][] total = new int[2][3];
        long[][] share = new long[6][2];
        for (int k = 0; k < n; k++) {
            in[k] = layoutInput(types, k, false);
            on[k] = DistrictPlan.make(in[k]);
            off[k] = DistrictPlan.make(layoutInput(types, k, true));
            // CL4: the digests below are 0.8.11's plans, from before the avenues - the same fixture with latticeOnly as well.
            DistrictPlan.Input lattice = layoutInput(types, k, true);
            lattice.latticeOnly = true;
            pinned[k] = DistrictPlan.make(lattice);
            fig[k] = figures(on[k], in[k]);
            dead[k] = deadEnds(on[k], false);
            deadOff[k] = deadEnds(off[k], false);
            deadVaried[k] = deadEnds(on[k], true);
            int[][] h = homesByLayout(on[k]);
            long[][] s = homesStreetShare(on[k], in[k]);
            for (int a = 0; a < 2; a++) for (int l = 0; l < 3; l++) total[a][l] += h[a][l];
            for (int i = 0; i < 6; i++) { share[i][0] += s[i][0]; share[i][1] += s[i][1]; }
            out.printf("      %s: %s%n", LAYOUT_FIXTURES[k], line(on[k], fig[k]));
            out.printf("      ...homes cells by layout: %s; merges undone %d (with combOnly %d)%n", layoutLine(h), on[k].mergesUndone, off[k].mergesUndone);
            out.printf("      ...dead ends: %d (%d inside a cell of another layout) against %d with combOnly; street share of the homes cells' ground: %s%n",
                    dead[k], deadVaried[k], deadOff[k], shareLine(s));
        }
        out.printf("      all five: homes cells by layout %s; street share of their ground %s%n", layoutLine(total), shareLine(share));
        // a. The premise: each fixture causes its condition with the switch off.
        for (int k = 0; k < n; k++) {
            int[] kinds = homesKinds(off[k]);
            boolean ok;
            String what;
            if (k == 0) {
                ok = kinds[0] > 0 && kinds[1] == 0 && kinds[2] == 0;
                what = String.format("its %d homes cells are all long blocks, none squared and none a boulevard", kinds[0]);
            } else if (k == 1) {
                ok = kinds[4] > 0;
                what = String.format("%d of its %d homes cells are squared and not boulevards", kinds[4], kinds[0]);
            } else if (k == 2) {
                ok = kinds[2] > 0;
                what = String.format("%d of its %d homes cells are boulevards (%d of them squared)", kinds[2], kinds[0], kinds[3]);
            } else {
                ok = kinds[0] > 0 && (off[k].shoreJoins > 0 || off[k].strays > 0);
                what = String.format("the ground cuts its %d homes cells: %d dead ends joined along a cut, %d street plots taken up as strays", kinds[0], off[k].shoreJoins, off[k].strays);
            }
            check(LAYOUT_FIXTURES[k] + " with combOnly: " + what, ok);
        }
        // b. The switch off is 0.8.11's plan; c. with the variety on every check holds.
        StringBuilder bad = new StringBuilder(), broken = new StringBuilder();
        for (int k = 0; k < n; k++) {
            long placed = placedDigest(pinned[k]), streets = streetDigest(pinned[k]);
            if (placed != COMB_DIGESTS[k][0] || streets != COMB_DIGESTS[k][1] || !allComb(pinned[k])) {
                bad.append(' ').append(LAYOUT_FIXTURES[k]);
                out.printf("      ...%s with combOnly: placed %dL streets %dL, every cell LAYOUT_TODAY %s%n", LAYOUT_FIXTURES[k], placed, streets, allComb(pinned[k]));
            }
            if (!fig[k].holds(on[k], in[k])) broken.append(' ').append(LAYOUT_FIXTURES[k]);
        }
        check("with combOnly each fixture's plan is 0.8.11's, box for box and street for street, every cell LAYOUT_TODAY" + (bad.length() > 0 ? " - not:" + bad : ""), bad.length() == 0);
        check("with the variety on every check holds, each of the five fixtures" + (broken.length() > 0 ? " - not:" + broken : ""), broken.length() == 0);
        // d. Each layout appears, in long-block and in squared cells.
        for (int a = 0; a < 2; a++) {
            check(String.format("over the five fixtures %s cells take each layout: %d today, %d dropped, %d brick", a == 0 ? "long-block" : "squared", total[a][0], total[a][1], total[a][2]),
                    total[a][0] > 0 && total[a][1] > 0 && total[a][2] > 0);
        }
        // e. The shares: layoutOf over a square of the lattice at a world place.
        long[] place = MapCheck.dryPlace(World.of(seed));
        long i0 = Math.floorDiv(place[0], DistrictPlan.CELL), j0 = Math.floorDiv(place[1], DistrictPlan.CELL);
        double[] dial = { DistrictPlan.TODAY_SHARE, DistrictPlan.DROPPED_SHARE, 1 - DistrictPlan.TODAY_SHARE - DistrictPlan.DROPPED_SHARE };
        for (int a = 0; a < 2; a++) {
            int[] drawn = new int[3];
            for (int j = 0; j < SHARE_SIDE; j++) {
                for (int i = 0; i < SHARE_SIDE; i++) drawn[DistrictPlan.layoutOf(seed, (i0 + i) * DistrictPlan.CELL, (j0 + j) * DistrictPlan.CELL, a == 1)]++;
            }
            double cells = (double) SHARE_SIDE * SHARE_SIDE;
            boolean ok = true;
            for (int l = 0; l < 3; l++) ok &= Math.abs(drawn[l] / cells - dial[l]) <= SHARE_TOLERANCE;
            out.printf("      layoutOf over %,d cells of the lattice from the dry place, asked as %s cells: today %.4f, dropped %.4f, brick %.4f (the dials %.4f, %.4f, %.4f)%n",
                    SHARE_SIDE * SHARE_SIDE, a == 0 ? "long-block" : "squared", drawn[0] / cells, drawn[1] / cells, drawn[2] / cells, dial[0], dial[1], dial[2]);
            check(String.format("each layout's share of %,d %s cells is within %.2f of its dial", SHARE_SIDE * SHARE_SIDE, a == 0 ? "long-block" : "squared", SHARE_TOLERANCE), ok);
        }
        // f. A layout is a function of its place (and of a crossing: a homes cell a highway, railway, yard or reserve crosses keeps the comb).
        int cells = 0;
        boolean ofPlace = true, twice = true;
        for (int k = 0; k < n; k++) {
            for (int c = 0; c < DistrictPlan.CELLS; c++) {
                if (on[k].cellKind[c] == DistrictPlan.HOMES) cells++;
                ofPlace &= on[k].cellLayout[c] == expectedLayout(on[k], in[k], c);
            }
            DistrictPlan again = DistrictPlan.make(in[k]);
            twice &= placedDigest(again) == placedDigest(on[k]) && streetDigest(again) == streetDigest(on[k]) && Arrays.equals(again.cellLayout, on[k].cellLayout);
        }
        check("in every fixture each open homes cell's layout is layoutOf of the seed and its place in the world (" + cells + " cells), every other cell's LAYOUT_TODAY", ofPlace && cells > 0);
        check("...and the same Input planned twice gives the same buildings, streets and layouts", twice);
        DistrictPlan.Input more = layoutInput(types, 0, false);
        int big = -1;
        for (int t = 0; t < types.length; t++) {
            if (types[t] != null && more.counts[t] > 0 && band(types[t]) == 0 && (big < 0 || more.counts[t] > more.counts[big])) big = t;
        }
        String bigName = "type " + big;
        for (BuildingsTemplate bt : new BuildingCatalog().load()) if (bt.getId() == big) bigName = bt.getName();
        int added = (int) Math.round(MORE_HOMES * more.counts[big]);
        more.counts[big] += added;
        DistrictPlan grown = DistrictPlan.make(more);
        int both = 0, kept = 0, reshaped = 0;
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (on[0].cellKind[c] != DistrictPlan.HOMES || grown.cellKind[c] != DistrictPlan.HOMES) continue;
            if (on[0].cellSquare[c] != grown.cellSquare[c]) { reshaped++; continue; }
            both++;
            if (on[0].cellLayout[c] == grown.cellLayout[c]) kept++;
        }
        out.printf("      LONG with %,d more %s (the largest count in the homes band, %,d to %,d): %d homes cells against %d; %d open in both with the same squared flag, %d changed it%n",
                added, bigName, more.counts[big] - added, more.counts[big], homesKinds(grown)[0], homesKinds(on[0])[0], both, reshaped);
        check("...LONG with a tenth more " + bigName + " keeps each of the " + both + " cells that open in both, with the same squared flag, at the same layout", both > 0 && kept == both);
        DistrictPlan.Input elsewhere = layoutInput(types, 0, false);
        elsewhere.seed = OTHER_SEED;
        DistrictPlan other = DistrictPlan.make(elsewhere);
        int shared = 0, changed = 0;
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (on[0].cellKind[c] != DistrictPlan.HOMES || other.cellKind[c] != DistrictPlan.HOMES) continue;
            shared++;
            if (on[0].cellLayout[c] != other.cellLayout[c]) changed++;
        }
        check("...and another seed gives another layout to " + changed + " of the " + shared + " homes cells LONG opens with either: the hash reads the seed", changed > 0);
        // g. Two districts agree along their seam, no less than with the comb.
        CityMap m = squareCity(types, seed, place, 1);
        int f = DistrictPlan.FRAME, side = DistrictPlan.SIDE, pairs = 0;
        long[] apart = new long[2];
        for (int pass = 0; pass < 2; pass++) {
            java.util.Map<CityMap.District, DistrictPlan> plans = new java.util.HashMap<>();
            for (CityMap.District d : m.districts()) {
                DistrictPlan.Input di = m.planInput(d);
                di.combOnly = pass == 1;
                plans.put(d, DistrictPlan.make(di));
            }
            pairs = 0;
            for (CityMap.District d : m.districts()) {
                for (int east = 0; east < 2; east++) {
                    CityMap.District o = east == 0 ? m.district(d.dx + 1, d.dy) : m.district(d.dx, d.dy + 1);
                    if (o == null) continue;
                    pairs++;
                    for (int t = 0; t <= side; t++) {
                        int a = east == 0 ? t * f + side : side * f + t, b = east == 0 ? t * f : t;
                        if ((plans.get(d).kindAt(a) != DistrictPlan.NONE) != (plans.get(o).kindAt(b) != DistrictPlan.NONE)) apart[pass]++;
                    }
                }
            }
        }
        out.printf("      his city x 1 (%d districts, %d neighbouring pairs): plots of a shared edge one district lays a street on and the other does not: %d with the layouts, %d with combOnly%n",
                m.districts().size(), pairs, apart[0], apart[1]);
        check("two neighbouring districts disagree along their seam on no more plots than with the comb (" + apart[0] + " against " + apart[1] + ")", pairs > 0 && apart[0] <= apart[1]);
        // h. Dead ends.
        boolean noMore = true, noneVaried = true;
        for (int k = 0; k < n; k++) {
            noMore &= dead[k] <= deadOff[k];
            if (k < 3) noneVaried &= deadVaried[k] == 0;
        }
        check("each fixture has no more dead ends than with the comb (" + Arrays.toString(dead) + " against " + Arrays.toString(deadOff) + ")", noMore);
        check("...and in LONG, SQUARED and BOULEVARD, where the ground cuts nothing, no cell of a varied layout has a dead end inside it ("
                + deadVaried[0] + ", " + deadVaried[1] + ", " + deadVaried[2] + ")", noneVaried);
        // ...and the counter itself sees one: a street plot hung off a varied cell's street (which has two streets beside it already) is one dead end more.
        DistrictPlan stubbed = DistrictPlan.make(in[0]);
        int before = deadEnds(stubbed, true), hung = -1;
        for (int c = 0; c < DistrictPlan.CELLS && hung < 0; c++) {
            if (stubbed.cellKind[c] != DistrictPlan.HOMES || stubbed.cellLayout[c] == DistrictPlan.LAYOUT_TODAY) continue;
            int x0 = DistrictPlan.CELL * (c % DistrictPlan.CELLS_A_SIDE) + 1, y0 = DistrictPlan.CELL * (c / DistrictPlan.CELLS_A_SIDE) + 1;
            for (int y = y0; y < y0 + DistrictPlan.INTERIOR && hung < 0; y++) {
                for (int x = x0; x < x0 + DistrictPlan.INTERIOR && hung < 0; x++) {
                    int q = y * f + x;
                    if (stubbed.kindAt(q) != DistrictPlan.NONE) continue;
                    int[] around = { q - 1, q + 1, q - f, q + f };
                    int streets = 0, from = -1;
                    for (int u : around) if (stubbed.kindAt(u) != DistrictPlan.NONE) { streets++; from = u; }
                    if (streets != 1) continue;
                    int beside = 0;
                    for (int u : new int[] { from - 1, from + 1, from - f, from + f }) if (stubbed.kindAt(u) != DistrictPlan.NONE) beside++;
                    if (beside < 2) continue;
                    stubbed.street[q] = stubbed.street[from];
                    hung = q;
                }
            }
        }
        check("...the counter sees a stub: one hung off a varied cell's street takes its dead ends from " + before + " to " + (hung >= 0 ? deadEnds(stubbed, true) : -1),
                hung >= 0 && deadEnds(stubbed, true) == before + 1);
        // The crossing: section 3's highway row. Its crossed homes cells keep the comb, the others their place's layout.
        DistrictPlan.Input hw = testDistrict(types, 1, false);
        int hy = (int) TEST_HUB_Y / DistrictPlan.CELL * DistrictPlan.CELL + DistrictPlan.CELL / 2;
        for (int x = 0; x < DistrictPlan.FRAME; x++) {
            int q = hy * DistrictPlan.FRAME + x;
            if (!hw.owned[q] || hw.terrain[q] == World.SALT) continue;
            hw.fixed[q] = DistrictPlan.FIXED_HIGHWAY;
        }
        DistrictPlan ph = DistrictPlan.make(hw);
        int crossed = 0, free = 0, freeVaried = 0;
        boolean ruled = true;
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            ruled &= ph.cellLayout[c] == expectedLayout(ph, hw, c);
            if (ph.cellKind[c] != DistrictPlan.HOMES) continue;
            if (crossedCell(hw, c % DistrictPlan.CELLS_A_SIDE, c / DistrictPlan.CELLS_A_SIDE)) crossed++;
            else { free++; if (ph.cellLayout[c] != DistrictPlan.LAYOUT_TODAY) freeVaried++; }
        }
        out.printf("      the test district with section 3's highway row: %d homes cells crossed by it, %d not (%d of them of another layout than the comb), %d dead ends%n",
                crossed, free, freeVaried, deadEnds(ph, false));
        check("...a homes cell the highway crosses keeps the comb, the others their place's layout, every check holding", crossed > 0 && ruled && figures(ph, hw).holds(ph, hw));
    }

    /* =====================================================================
       11. THE ORDER OF ARRIVAL (batch CL3)
       ===================================================================== */

    /**
     * A log built by hand (CL3): records {month, type, change} in order, how many of the first are one batch, and a carry
     * of {month, type} pairs - what CityMap.District's log is to a plan, written down in a fixture.
     */
    static final class Log {
        final List<int[]> rec = new ArrayList<>();
        int firstBatch;
        int[] carry;

        Log add(int month, int type, int change) {
            rec.add(new int[] { month, type, change });
            return this;
        }

        /** The records so far are the first batch. */
        Log batch() {
            firstBatch = rec.size();
            return this;
        }

        /** The log as it stood at the end of month m: its records of that month or before (the first batch kept), the same carry. */
        Log until(int m) {
            Log l = new Log();
            for (int[] r : rec) if (r[0] <= m) l.rec.add(r);
            l.firstBatch = Math.min(firstBatch, l.rec.size());
            l.carry = carry;
            return l;
        }

        /** A copy that goes on from here. */
        Log copy() {
            Log l = until(Integer.MAX_VALUE);
            l.firstBatch = firstBatch;
            return l;
        }

        /** Input `in` given this log, as its arrays and as the counts and road they come to (the drawn types' sums; the road types' plots by kind). */
        DistrictPlan.Input into(DistrictPlan.Input in) {
            attach(in);
            in.counts = new int[in.types.length];
            in.gravel = in.paved = in.highway = 0;
            for (int[] r : rec) {
                BuildingVisual.Type t = in.types[r[1]];
                if (t.road() == BuildingVisual.GRAVEL) in.gravel += r[2] * BuildingVisual.cells(t);
                else if (t.road() == BuildingVisual.PAVED) in.paved += r[2] * BuildingVisual.cells(t);
                else if (t.drawn()) in.counts[r[1]] += r[2];
            }
            return in;
        }

        /** Input `in` given this log's arrays alone: its counts and road are what it has. */
        DistrictPlan.Input attach(DistrictPlan.Input in) {
            int n = rec.size();
            in.logMonth = new int[n];
            in.logType = new int[n];
            in.logChange = new int[n];
            for (int k = 0; k < n; k++) {
                in.logMonth[k] = rec.get(k)[0];
                in.logType[k] = rec.get(k)[1];
                in.logChange[k] = rec.get(k)[2];
            }
            in.firstBatch = firstBatch;
            in.carry = carry;
            return in;
        }
    }

    /** Whether flat() lays no avenue (Input.latticeOnly): set for a fixture whose premise is a plan from before the avenues (CL4). */
    static boolean pinLattice = false;

    /** A flat district, every plot owned, the hub at its middle: the order of arrival's fixture. */
    static DistrictPlan.Input flat(BuildingVisual.Type[] types) {
        DistrictPlan.Input in = new DistrictPlan.Input();
        in.latticeOnly = pinLattice;
        Arrays.fill(in.owned, true);
        in.types = types;
        in.seed = Founding.DEFAULT_WORLD_SEED;
        in.hubX = in.hubY = DistrictPlan.FRAME / 2;
        in.counts = new int[types.length];
        return in;
    }

    /** The flat district with a log, or (`withLog` false) with the counts and road it comes to and no log - today's plan from them, the carry joined. */
    static DistrictPlan.Input input(BuildingVisual.Type[] types, Log log, boolean withLog) {
        DistrictPlan.Input in = log.into(flat(types));
        if (!withLog) {
            in.logMonth = in.logType = in.logChange = null;
            in.firstBatch = 0;
        }
        return in;
    }

    /** The first type, by id, of the smallest footprint no smaller than `least` plots that `ok` holds of; -1 for none. */
    static int pick(BuildingVisual.Type[] types, int least, java.util.function.Predicate<BuildingVisual.Type> ok) {
        int best = -1, area = Integer.MAX_VALUE;
        for (int t = 0; t < types.length; t++) {
            if (types[t] == null || !types[t].drawn() || !ok.test(types[t])) continue;
            int[] f = BuildingVisual.footprint(types[t]);
            if (f[0] * f[1] >= least && f[0] * f[1] < area) { area = f[0] * f[1]; best = t; }
        }
        return best;
    }

    /** The first type, by id, of the largest footprint that `ok` holds of. */
    static int pickLargest(BuildingVisual.Type[] types, java.util.function.Predicate<BuildingVisual.Type> ok) {
        int best = -1, area = 0;
        for (int t = 0; t < types.length; t++) {
            if (types[t] == null || !types[t].drawn() || !ok.test(types[t])) continue;
            int[] f = BuildingVisual.footprint(types[t]);
            if (f[0] * f[1] > area) { area = f[0] * f[1]; best = t; }
        }
        return best;
    }

    /** The paved road type: the first whose road is paved. */
    static int pavedRoad(BuildingVisual.Type[] types) {
        for (int t = 0; t < types.length; t++) if (types[t] != null && types[t].road() == BuildingVisual.PAVED) return t;
        return -1;
    }

    /** A plan's boxes, each packed from its place, size and type. */
    static java.util.Set<Long> boxes(DistrictPlan p) {
        java.util.Set<Long> s = new java.util.HashSet<>();
        for (int i = 0; i < p.buildings; i++) s.add(boxKey(p, i));
        return s;
    }

    static long boxKey(DistrictPlan p, int i) {
        return ((((long) p.bx[i] * 512 + p.by[i]) * 64 + p.bw[i]) * 64 + p.bh[i]) * 256 + p.btype[i];
    }

    /** How many of a's boxes b does not have: those that moved or went. */
    static int missing(DistrictPlan a, DistrictPlan b) {
        java.util.Set<Long> in = boxes(b);
        int n = 0;
        for (int i = 0; i < a.buildings; i++) if (!in.contains(boxKey(a, i))) n++;
        return n;
    }

    /** A plan's box total: its buildings' plots. */
    static long boxPlots(DistrictPlan p) {
        long n = 0;
        for (int i = 0; i < p.buildings; i++) n += (long) p.bw[i] * p.bh[i];
        return n;
    }

    /** The cell with rank r among those a plan opened, or -1. */
    static int cellOfRank(DistrictPlan p, int r) {
        for (int c = 0; c < DistrictPlan.CELLS; c++) if (p.cellRank[c] == r && p.cellKind[c] != DistrictPlan.CLOSED) return c;
        return -1;
    }

    /** The cells a plan opened (CLOSED aside). */
    static int cellsOpened(DistrictPlan p) {
        int n = 0;
        for (int c = 0; c < DistrictPlan.CELLS; c++) if (p.cellKind[c] != DistrictPlan.CLOSED) n++;
        return n;
    }

    /** The cell a box of a plan stands in. */
    static int cellOfBox(DistrictPlan p, int i) {
        return (p.by[i] - 1) / DistrictPlan.CELL * DistrictPlan.CELLS_A_SIDE + (p.bx[i] - 1) / DistrictPlan.CELL;
    }

    /** How many cells apart two cells are, the larger of their columns' and rows' (the eight about a cell are 1). */
    static int cellApart(int a, int b) {
        return Math.max(Math.abs(a % DistrictPlan.CELLS_A_SIDE - b % DistrictPlan.CELLS_A_SIDE), Math.abs(a / DistrictPlan.CELLS_A_SIDE - b / DistrictPlan.CELLS_A_SIDE));
    }

    /** Whether cell c is at the corner of the district's grid of cells. */
    static boolean cornerCell(int c) {
        int last = DistrictPlan.CELLS_A_SIDE - 1;
        return (c % DistrictPlan.CELLS_A_SIDE == 0 || c % DistrictPlan.CELLS_A_SIDE == last) && (c / DistrictPlan.CELLS_A_SIDE == 0 || c / DistrictPlan.CELLS_A_SIDE == last);
    }

    /** Whether every plan this section has made keeps every check (figures().holds()): each plan goes through plan(). */
    static boolean arrivalRules = true;
    static int arrivalPlans;

    /** The plan of an input, its checks held to account. */
    static DistrictPlan plan(DistrictPlan.Input in) {
        DistrictPlan p = DistrictPlan.make(in);
        arrivalPlans++;
        arrivalRules &= figures(p, in).holds(p, in);
        return p;
    }

    /** A site plot in the middle of cell c's interior (a resource's field, SITE_FIELD). */
    static void siteIn(DistrictPlan.Input in, int c) {
        int x = DistrictPlan.CELL * (c % DistrictPlan.CELLS_A_SIDE) + 1 + DistrictPlan.INTERIOR / 2, y = DistrictPlan.CELL * (c / DistrictPlan.CELLS_A_SIDE) + 1 + DistrictPlan.INTERIOR / 2;
        in.site[y * DistrictPlan.FRAME + x] = DistrictPlan.SITE_FIELD;
    }

    /** The batches the dense screen's counts are dealt in (h): forty months. */
    static final int BATCHES = 40;

    /** What the order of arrival must cover of the ground today's plans cover on the dense screen: 95%. */
    static final double COVER_LEAST = 0.95;

    /** The slowest of a list of times. */
    static double slowest(List<Double> ms) {
        return Tally.max(ms);
    }

    static void arrivals(BuildingVisual.Type[] types) {
        out.println("\n--- 11. the order of arrival (batch CL3) ---");
        arrivalRules = true;
        arrivalPlans = 0;
        final int house = pick(types, 1, t -> t.category() == BuildingType.RESIDENTIAL && !t.outer() && t.site() == null);
        final int shop = pick(types, 1, t -> t.category() == BuildingType.COMMERCIAL && !t.outer() && t.site() == null);
        final int works = pick(types, 9, t -> t.cls() == BuildingVisual.INDUSTRY && !t.outer() && t.site() == null && t.campusGroup() == BuildingVisual.NO_GROUP);
        final int farm = pick(types, 1, t -> t.category() == BuildingType.AGRICULTURE && t.outer() && t.site() == null);
        final int bigFarm = pickLargest(types, t -> t.category() == BuildingType.AGRICULTURE && t.outer() && t.site() == null);
        final int mine = Mining.IRON_MINE;
        final int paved = pavedRoad(types);
        check("the fixture's types are in the table: a house, a shop, a works, a farm, a mine and a paved road", house >= 0 && shop >= 0 && works >= 0 && farm >= 0
                && bigFarm >= 0 && paved >= 0 && types[mine] != null && types[mine].site() != null && types[mine].outer());

        // ---- (a) one batch is today's plan
        boolean same = true, carried = true, logOk = true;
        DistrictPlan.Input[] fixtures = { flatMix(types, FILLS[3], true, false), testDistrict(types, 0, false), testDistrict(types, 1, false) };
        String[] names = { "his city on flat ground at 92%", "the test district, his roads", "the test district, the same trips paved" };
        for (int k = 0; k < fixtures.length; k++) {
            DistrictPlan.Input in = fixtures[k];
            Log one = new Log();
            for (int t = 0; t < types.length; t++) if (in.counts[t] > 0) one.add(5, t, in.counts[t]);
            one.batch();
            DistrictPlan today = plan(in);
            DistrictPlan.Input withLog = one.attach(k == 0 ? flatMix(types, FILLS[3], true, false) : testDistrict(types, k - 1, false));
            DistrictPlan arrived = plan(withLog);
            boolean eq = placedDigest(today) == placedDigest(arrived) && streetDigest(today) == streetDigest(arrived) && Arrays.equals(today.overflow, arrived.overflow)
                    && Arrays.equals(today.cellKind, arrived.cellKind) && Arrays.equals(today.cellSquare, arrived.cellSquare) && today.builds == arrived.builds;
            out.printf("      %s: %,d placed, %d without a place, %d builds - with its counts as a log of one batch: %s%n", names[k], today.buildings, today.overflowCount(), today.builds,
                    eq ? "the same" : "DIFFERENT");
            same &= eq;
            // ...its leftovers given back with the batch's month.
            int[] lg = arrived.overflowLog;
            boolean months = (lg == null ? 0 : lg.length / 2) == arrived.overflowCount();
            for (int j = 0; lg != null && j + 1 < lg.length; j += 2) months &= lg[j] == 5;
            logOk &= months;
            // ...and a carry of the batch's month joins its counts, as it did before the log.
            if (k == 0) {
                int[] pairs = { 5, house, 5, shop, 3, house };
                DistrictPlan.Input a = flatMix(types, FILLS[3], true, false);
                a.counts[house] += 2;
                a.counts[shop]++;
                DistrictPlan.Input b = one.attach(flatMix(types, FILLS[3], true, false));
                b.carry = pairs;
                carried &= placedDigest(plan(a)) == placedDigest(plan(b));
            }
        }
        check("a log that is one batch gives today's plan box for box, street for street and step for step (the dense flat mix, the test district at two budgets)", same);
        check("...its leftovers go on as {month, type} pairs, one for each without a place, with the batch's month", logOk);
        check("...and a carry of the batch's months joins its counts, the plan today's with the carry added to them", carried);

        // ---- (b) a later month's arrivals move none placed before
        final int months = 10;
        Log growth = new Log();
        growth.add(0, house, 300).add(0, shop, 40).add(0, works, 4).add(0, paved, 3).batch();
        for (int m = 1; m <= months + 3; m++) {
            growth.add(m, house, 120).add(m, shop, 15).add(m, works, 2 + m % 2).add(m, paved, 2);
            if (m % 4 == 0) growth.add(m, farm, 2);
        }
        DistrictPlan[] arrived = new DistrictPlan[months + 1], today = new DistrictPlan[months + 1];
        for (int m = 0; m <= months; m++) {
            arrived[m] = plan(input(types, growth.until(m), true));
            today[m] = plan(input(types, growth.until(m), false));
        }
        int movedArrived = 0, movedToday = 0, opened = 0;
        for (int m = 0; m < months; m++) {
            movedArrived += missing(arrived[m], arrived[m + 1]);
            movedToday += missing(today[m], today[m + 1]);
            if (cellsOpened(arrived[m + 1]) > cellsOpened(arrived[m])) opened++;
        }
        out.printf("      a district growing %d months (%,d buildings at the end, %d cells): %d buildings moved in all by the order of arrival, %d by packing again from the counts;"
                + " a cell opened in %d of the months%n", months, arrived[months].buildings, cellsOpened(arrived[months]), movedArrived, movedToday, opened);
        check("a later month's arrivals move none placed before: every box of each month's plan stands in the next", movedArrived == 0 && arrived[months].buildings > arrived[0].buildings);
        check("...in a district that grew into new cells, where packing again from the counts moves buildings", opened >= 2 && movedToday > 0);

        // ---- (c) a departure frees its own box and moves nothing else
        DistrictPlan whole = arrived[months];
        Log gone = growth.until(months);
        Log goneHouse = gone.copy().add(months + 1, house, -1), goneWorks = gone.copy().add(months + 1, works, -1);
        DistrictPlan noHouse = plan(input(types, goneHouse, true)), noWorks = plan(input(types, goneWorks, true));
        int lastHouse = -1, lastWorks = -1;
        for (int i = 0; i < whole.buildings; i++) {
            if (whole.btype[i] == house) lastHouse = i;
            if (whole.btype[i] == works) lastWorks = i;
        }
        boolean freed = missing(noHouse, whole) == 0 && whole.buildings - noHouse.buildings == 1 && !boxes(noHouse).contains(boxKey(whole, lastHouse))
                && missing(noWorks, whole) == 0 && whole.buildings - noWorks.buildings == 1 && !boxes(noWorks).contains(boxKey(whole, lastWorks));
        check("a departure frees its own box and moves nothing else: the newest house, then the newest works, gone - the plan has that box less and every other where it was", freed);
        DistrictPlan again = plan(input(types, goneHouse.copy().add(months + 2, house, 1), true));
        check("...and the arrival after it moves none: every box standing before it stands still", missing(noHouse, again) == 0 && again.buildings == noHouse.buildings + 1);
        // ...one of the first batch too, when the departures outnumber the arrivals after it: the newest of the type, wherever it stands.
        Log deep = new Log().add(1, house, 60).add(1, works, 3).batch().add(2, house, 10);
        DistrictPlan deepBefore = plan(input(types, deep, true)), deepAfter = plan(input(types, deep.copy().add(3, house, -15), true));
        int housesBefore = 0, housesAfter = 0;
        for (int i = 0; i < deepBefore.buildings; i++) if (deepBefore.btype[i] == house) housesBefore++;
        for (int i = 0; i < deepAfter.buildings; i++) if (deepAfter.btype[i] == house) housesAfter++;
        check("...a first-batch building leaves like any other, the newest of its type: fifteen houses gone, ten of them the later arrivals and five the batch's, each box freed and every other where it was",
                missing(deepAfter, deepBefore) == 0 && housesBefore - housesAfter == 15 && deepBefore.buildings - deepAfter.buildings == 15);
        // One without a place leaves the list of those without: the district full of the largest farm.
        Log farms = new Log().add(1, bigFarm, 2 * DistrictPlan.CELLS);
        DistrictPlan full = plan(input(types, farms, true)), lessOne = plan(input(types, farms.copy().add(2, bigFarm, -1), true));
        check("...one that had no place leaves the list of those without one: the district full of the largest farm, one gone, has one less without a place and every box where it was",
                full.overflowCount() > 0 && lessOne.overflowCount() == full.overflowCount() - 1 && missing(full, lessOne) == 0 && full.buildings == lessOne.buildings);

        // ---- (d) a cell takes the use of what opens it
        Log works1 = new Log().add(1, works, 1).add(2, house, 1);
        DistrictPlan first = plan(input(types, works1, true)), packed = plan(input(types, works1, false));
        int c0 = cellOfRank(first, 0), p0 = cellOfRank(packed, 0);
        out.printf("      a works, then a house: the nearest cell opened is %s (packed from the counts it is %s, in the same place: %b)%n", c0 < 0 ? "none" : first.cellKind[c0] == DistrictPlan.ESTATE ? "an estate" : "homes",
                p0 < 0 ? "none" : packed.cellKind[p0] == DistrictPlan.ESTATE ? "an estate" : "homes", c0 == p0);
        check("the first arrival a works: the nearest cell opened is an estate, the house after it opens the next as homes; packed from the counts the nearest is homes",
                c0 >= 0 && c0 == p0 && first.cellKind[c0] == DistrictPlan.ESTATE && cellOfRank(first, 1) >= 0 && first.cellKind[cellOfRank(first, 1)] == DistrictPlan.HOMES
                        && packed.cellKind[p0] == DistrictPlan.HOMES);

        // ---- (e) the edge kinds from the far side, a mine off its site from the near
        String[] kinds = { "farm", "power plant", "water works", "car plant", "truck depot", "rail yard" };
        BuildingType[] cats = { BuildingType.AGRICULTURE, BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.AUTOMOTIVE, BuildingType.HAULAGE, BuildingType.RAIL };
        boolean far = true, notFar = true;
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < cats.length; k++) {
            final BuildingType cat = cats[k];
            int edge = pick(types, 1, t -> t.category() == cat && t.outer() && t.site() == null);
            if (edge < 0) { far = false; continue; }
            Log l = new Log().add(1, house, 1).add(2, edge, 1);
            DistrictPlan ev = plan(input(types, l, true)), base = plan(input(types, l, false));
            int at = -1, near = -1;
            for (int i = 0; i < ev.buildings; i++) if (ev.btype[i] == edge) at = cellOfBox(ev, i);
            for (int i = 0; i < base.buildings; i++) if (base.btype[i] == edge) near = cellOfBox(base, i);
            far &= at >= 0 && cornerCell(at) && ev.cellKind[at] == DistrictPlan.ESTATE;
            notFar &= near >= 0 && !cornerCell(near);
            sb.append(String.format(" %s in cell %d (packed from the counts: %d);", kinds[k], at, near));
        }
        out.printf("      after a house, the edge kinds open:%s%n", sb);
        check("the farm, power plant, water works, car plant, truck depot and rail yard each open their cell from the far end of the growth order: a corner of the district's cells, an estate", far);
        check("...where packed from the counts they open beside the homes, none at a corner", notFar);
        Log nearMine = new Log().add(1, house, 1).add(2, mine, 1);
        DistrictPlan withMine = plan(input(types, nearMine, true));
        int homeCell = cellOfRank(withMine, 0), mineCell = -1;
        for (int i = 0; i < withMine.buildings; i++) if (withMine.btype[i] == mine) mineCell = cellOfBox(withMine, i);
        check("a mine off its site opens its cell from the near end as any works: the cell after the homes', beside it (not at a corner)", homeCell >= 0 && mineCell >= 0
                && cellApart(homeCell, mineCell) == 1 && !cornerCell(mineCell) && withMine.cellKind[mineCell] == DistrictPlan.ESTATE);

        // ---- (f) homes keep off mines
        Log houseOnly = new Log().add(1, house, 1);
        DistrictPlan plain = plan(input(types, houseOnly, true));
        int nearest = cellOfRank(plain, 0);
        DistrictPlan.Input sited = input(types, houseOnly, true);
        siteIn(sited, nearest);
        DistrictPlan avoided = plan(sited);
        int away = cellOfRank(avoided, 0);
        DistrictPlan.Input everywhere = input(types, houseOnly, true);
        for (int c = 0; c < DistrictPlan.CELLS; c++) siteIn(everywhere, c);
        DistrictPlan none = plan(everywhere);
        DistrictPlan.Input boxed = input(types, houseOnly, true);
        {
            int x = DistrictPlan.CELL * (nearest % DistrictPlan.CELLS_A_SIDE) + 1 + 10, y = DistrictPlan.CELL * (nearest / DistrictPlan.CELLS_A_SIDE) + 1 + 10;
            boxed.mines.add(new int[] { x, y, x + 5, y + 6 });
            for (int yy = y; yy <= y + 6; yy++) for (int xx = x; xx <= x + 5; xx++) boxed.site[yy * DistrictPlan.FRAME + xx] = DistrictPlan.SITE_MINED;
        }
        DistrictPlan beside = plan(boxed);
        int awayBox = cellOfRank(beside, 0);
        Log mineFirst = new Log().add(1, mine, 1).add(2, house, 1);
        DistrictPlan placedMine = plan(input(types, mineFirst, true)), packedMine = plan(input(types, mineFirst, false));
        int mineAt = -1, homesAfter = -1, homesPacked = -1;
        for (int i = 0; i < placedMine.buildings; i++) {
            if (placedMine.btype[i] == mine) mineAt = cellOfBox(placedMine, i);
            if (placedMine.btype[i] == house) homesAfter = cellOfBox(placedMine, i);
        }
        for (int i = 0; i < packedMine.buildings; i++) if (packedMine.btype[i] == house) homesPacked = cellOfBox(packedMine, i);
        out.printf("      homes: nearest cell %d; with a site in it the first homes cell is %d (%d cells off), with a mine's box in it %d, with a site in every cell %d;"
                + " after a mine placed in cell %d the house stands in %d (packed from the counts: %d)%n", nearest, away, away < 0 ? -1 : cellApart(nearest, away), awayBox,
                cellOfRank(none, 0), mineAt, homesAfter, homesPacked);
        check("a district with a mine's site in the cell nearest the hub: the first homes cell opened is not beside it (two cells or more off)", nearest >= 0 && away >= 0 && cellApart(nearest, away) >= 2
                && avoided.cellKind[away] == DistrictPlan.HOMES);
        check("...nor beside a mine standing on its site (a box of Input.mines)", awayBox >= 0 && cellApart(nearest, awayBox) >= 2);
        check("...nor beside a mine the plan placed itself", mineAt >= 0 && homesAfter >= 0 && cellApart(mineAt, homesAfter) >= 2 && homesPacked >= 0 && cellApart(mineAt, homesPacked) <= 1);
        check("...but with a site in every cell homes still open, in the cell nearest the hub as before", none.buildings == 1 && cellOfRank(none, 0) == nearest && none.cellKind[nearest] == DistrictPlan.HOMES);

        // ---- (g) the ladder decided once
        final int cellsPaved = BuildingVisual.cells(types[paved]);
        int thin = (int) ((DistrictPlan.LONG_BLOCK_STREETS + DistrictPlan.SQUARE_PLOTS - 1) / cellsPaved);
        final int home2 = pickLargest(types, t -> t.category() == BuildingType.RESIDENTIAL && !t.outer() && t.site() == null);
        int[] hf = BuildingVisual.footprint(types[home2]);
        int perCell = 3 * DistrictPlan.INTERIOR * DistrictPlan.INTERIOR / 4 / (hf[0] * hf[1]);
        Log road = new Log().add(1, home2, 2 * perCell).add(1, paved, thin).add(2, home2, 3 * perCell).add(2, paved, 400);
        DistrictPlan before = plan(input(types, road.until(1), true)), afterRoad = plan(input(types, road, true)), packedRoad = plan(input(types, road, false));
        boolean kept = true;
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (before.cellKind[c] == DistrictPlan.CLOSED) continue;
            kept &= afterRoad.cellKind[c] == before.cellKind[c] && afterRoad.cellSquare[c] == before.cellSquare[c] && afterRoad.cellBoulevard[c] == before.cellBoulevard[c]
                    && afterRoad.cellLayout[c] == before.cellLayout[c];
            int x0 = DistrictPlan.CELL * (c % DistrictPlan.CELLS_A_SIDE) + 1, y0 = DistrictPlan.CELL * (c / DistrictPlan.CELLS_A_SIDE) + 1;
            for (int y = y0; y < y0 + DistrictPlan.INTERIOR; y++) for (int x = x0; x < x0 + DistrictPlan.INTERIOR; x++) kept &= before.isStreet(y * DistrictPlan.FRAME + x) == afterRoad.isStreet(y * DistrictPlan.FRAME + x);
        }
        int rich = cellOfRank(packedRoad, 0);
        out.printf("      road %d plots in month 1 (%d cells opened, %d squared), %,d more in month 2 (%d cells, %d squared, %d boulevards); packed again from the counts the first cell is %s%n",
                thin * cellsPaved, cellsOpened(before), before.squares, 400 * cellsPaved, cellsOpened(afterRoad), afterRoad.squares, afterRoad.boulevards,
                rich < 0 ? "none" : packedRoad.cellSquare[rich] ? "squared" : "long blocks");
        check("the ladder is decided once: cells opened while the road was thin keep their layout and every street when the road grows (none squared, none a boulevard, the same streets)",
                cellsOpened(before) >= 2 && before.squares == 0 && before.boulevards == 0 && kept && missing(before, afterRoad) == 0);
        check("...and cells opened after the road grew take it: some squared, some boulevards, where packed from the counts the first cell is squared", cellsOpened(afterRoad) > cellsOpened(before)
                && afterRoad.squares > 0 && afterRoad.boulevards > 0 && rich >= 0 && packedRoad.cellSquare[rich]);
        // Road alone after the first batch changes none of its streets or buildings.
        Log batchThenRoad = new Log().add(1, home2, 2 * perCell).add(1, paved, thin).batch().add(2, paved, 400);
        DistrictPlan roadLater = plan(input(types, batchThenRoad, true)), roadThen = plan(input(types, batchThenRoad.until(1), true));
        boolean calm = missing(roadThen, roadLater) == 0 && roadThen.buildings == roadLater.buildings;
        for (int c = 0; c < DistrictPlan.CELLS; c++) calm &= roadThen.cellSquare[c] == roadLater.cellSquare[c] && roadThen.cellBoulevard[c] == roadLater.cellBoulevard[c];
        check("...road that comes after the first batch squares none of its cells and moves none of its buildings", calm && roadThen.squares == 0);

        // ---- (h) the packing: the dense screen's districts, their counts dealt in forty batches
        packing(types);

        // ---- (i) a pure function
        Log pure = growth.until(months);
        pure.carry = new int[] { 4, house, 7, shop, months + 5, works };
        DistrictPlan.Input one = input(types, pure, true);
        int[] countsWas = one.counts.clone();
        DistrictPlan p1 = plan(one), p2 = plan(one), p3 = plan(input(types, pure, true));
        check("a plan is a pure function of its input: the same input twice, the same plan, box for box, street for street and leftover for leftover, the input as it was",
                placedDigest(p1) == placedDigest(p2) && streetDigest(p1) == streetDigest(p2) && Arrays.equals(p1.overflowLog, p2.overflowLog) && Arrays.equals(one.counts, countsWas)
                        && placedDigest(p1) == placedDigest(p3) && streetDigest(p1) == streetDigest(p3) && Arrays.equals(p1.overflow, p3.overflow));

        // ---- (j) a carry merged by month, a leftover given back with its month
        Log base = growth.until(6);
        DistrictPlan noCarry = plan(input(types, base, true));
        Log withLate = base.copy();
        withLate.carry = new int[] { 6, works, 6, house };
        DistrictPlan late = plan(input(types, withLate, true));
        Log withMore = base.copy();
        withMore.carry = new int[] { 6, works, 6, house, 9, works };
        DistrictPlan later = plan(input(types, withMore, true));
        Log withMid = base.copy();
        withMid.carry = new int[] { 3, shop, 6, works, 6, house };
        DistrictPlan mid = plan(input(types, withMid, true));
        // ...an early pair is among those of its month: the buildings placed by the end of month 2 still stand
        DistrictPlan upToTwo = plan(input(types, base.until(2), true));
        out.printf("      a carry of pairs at months 6 and later: %,d buildings against %,d with none; at month 3 as well: %,d%n", late.buildings, noCarry.buildings, mid.buildings);
        check("a carried pair arrives after its own month's buildings and moves none placed before: the plan with it has every box of the plan without, and a later pair moves none of those",
                late.buildings == noCarry.buildings + 2 && missing(noCarry, late) == 0 && missing(late, later) == 0 && later.buildings == late.buildings + 1);
        check("...a pair of an earlier month is merged into that month and moves none of the months before it", mid.buildings == noCarry.buildings + 3 && missing(upToTwo, mid) == 0);
        Log crowded = new Log().add(1, bigFarm, 2 * DistrictPlan.CELLS).add(3, bigFarm, 2);
        crowded.carry = new int[] { 1, bigFarm, 5, bigFarm };
        DistrictPlan over = plan(input(types, crowded, true));
        int[] lg = over.overflowLog;
        boolean ascending = lg != null && lg.length / 2 == over.overflowCount(), has1 = false, has3 = false, has5 = false;
        for (int j = 0; lg != null && j + 3 < lg.length; j += 2) ascending &= lg[j] <= lg[j + 2];
        for (int j = 0; lg != null && j + 1 < lg.length; j += 2) {
            ascending &= lg[j + 1] == bigFarm;
            has1 |= lg[j] == 1;
            has3 |= lg[j] == 3;
            has5 |= lg[j] == 5;
        }
        out.printf("      a district full of the largest farm: %d without a place, their months %s%n", over.overflowCount(), lg == null ? "none" : monthsOf(lg));
        check("what a plan cannot place goes on as {month, type} pairs by month, each with the month it came in (an own building's record's, a carried one's pair's)", ascending && has5 && has3);

        // ---- (j2) the carry only grows: the departure of a building passed on travels with it, and the district after keeps its boxes
        // CL4: its premise is a farm of the largest kind in each of the district's 64 cells, which an avenue through a cell cuts - the plans before the avenues.
        pinLattice = true;
        // The first district is given more than it can draw, over several months, so its carry has pairs from several months; a
        // later month then takes the newest house - one it passed on, with a shop and a farm still to come after it in the list.
        // Taking its pair out of the list's middle (as it was done) moved what the second district placed after it.
        final int upFirstOver = 2, upFarms = DistrictPlan.CELLS + upFirstOver, upFarmMonth = 3, upHomeMonth = 4, upPassedMonth = 5, upShopMonth = 6, upLastMonth = 7, upLeaveMonth = 8, upReachMonth = 9, monthsLeast = 4;
        Log upstream = new Log().add(1, bigFarm, upFarms).batch().add(upFarmMonth, bigFarm, 2).add(upHomeMonth, house, 3).add(upPassedMonth, house, 1)
                .add(upShopMonth, shop, 2).add(upLastMonth, bigFarm, 1);
        DistrictPlan firstWas = plan(input(types, upstream, true)), firstNow = plan(input(types, upstream.copy().add(upLeaveMonth, house, -1), true));
        int[] listWas = firstWas.overflowLog, listNow = firstNow.overflowLog;
        int listPairs = listWas == null ? 0 : listWas.length / 2, passedAt = -1;
        java.util.TreeSet<Integer> listMonths = new java.util.TreeSet<>();
        for (int k = 0; k < listPairs; k++) {
            listMonths.add(listWas[2 * k]);
            if (listWas[2 * k] == upPassedMonth && listWas[2 * k + 1] == house) passedAt = k;
        }
        out.printf("      the first district: %d buildings left out in months %s; the house of month %d is pair %d of %d%n", firstWas.overflowCount(), listWas == null ? "none" : monthsOf(listWas),
                upPassedMonth, passedAt, listPairs);
        check("a district given more than it can draw (a farm in each of its cells, and farms, houses and shops of five months more) passes on pairs from several months, the house that is to leave among them with pairs after it",
                listMonths.size() >= monthsLeast && passedAt >= 0 && passedAt < listPairs - 1 && listPairs == firstWas.overflowCount() && boxesOfType(firstWas, bigFarm) == DistrictPlan.CELLS && home2 != house);
        boolean grew = listWas != null && listNow != null && listNow.length == listWas.length + 2 && Arrays.equals(listWas, Arrays.copyOf(listNow, listWas.length));
        check("...its departure is a pair added at the end of the carry, {month, ~k} for the k-th pair, the list as it was up to its old end and nothing taken out of its middle",
                grew && listNow[listNow.length - 2] == upLeaveMonth && listNow[listNow.length - 1] == ~passedAt && firstNow.overflowCount() == firstWas.overflowCount() - 1);
        int[] wasCounts = CityMap.countsOf(listWas, types.length), nowCounts = CityMap.countsOf(listNow, types.length);
        check("...the carry counted by type is its arrivals less its departures: the house less, and the plan's own count of those left out the same",
                wasCounts != null && nowCounts != null && nowCounts[house] == wasCounts[house] - 1 && Arrays.equals(wasCounts, firstWas.overflow) && Arrays.equals(nowCounts, firstNow.overflow));
        final int countTypeA = house, countTypeB = shop;
        int[] oddList = { 1, countTypeA, 2, ~0, 3, ~0, 4, ~5, 5, countTypeB, 6, ~2, 7, ~3 };
        int[] oddCounts = CityMap.countsOf(oddList, types.length);
        check("...a departure whose arrival is missing, comes after it, is itself a departure or has gone already counts for nothing: one arrival and its departure, a second departure of it, then three of those",
                oddCounts != null && oddCounts[countTypeA] == 0 && oddCounts[countTypeB] == 1 && CityMap.countsOf(null, types.length) == null);

        // The second district: homes in every month of its own, the first's list carried in, before and after the departure.
        int secondHomes = perCell;
        Log second = new Log().add(1, home2, secondHomes).batch();
        for (int mo = 2; mo <= upLastMonth; mo++) second.add(mo, home2, secondHomes);
        DistrictPlan carriedWas = plan(input(types, chained(second, listWas), true)), carriedNow = plan(input(types, chained(second, listNow), true));
        out.printf("      the second district: %,d buildings with the carry (%d houses, %d farms of the largest), %d left out; with the departure %,d (%d houses)%n", carriedWas.buildings,
                boxesOfType(carriedWas, house), boxesOfType(carriedWas, bigFarm), carriedWas.overflowCount(), carriedNow.buildings, boxesOfType(carriedNow, house));
        check("the district after it places all it is sent, the house among them: no box left out, and a house in it for each pair of that type in the carry", carriedWas.overflowCount() == 0
                && boxesOfType(carriedWas, house) == wasCounts[house] && boxesOfType(carriedWas, bigFarm) == wasCounts[bigFarm]);
        check("...the carry with the departure added moves none of its boxes: the plan has the one box of the house that left less and every other where it stood",
                missing(carriedWas, carriedNow) == 1 && missing(carriedNow, carriedWas) == 0 && carriedWas.buildings - carriedNow.buildings == 1 && boxesOfType(carriedNow, house) == boxesOfType(carriedWas, house) - 1);
        // ...and with months of its own after the departure's: the house freed, every box placed before it still where it was.
        Log secondOn = second.copy().add(upLeaveMonth, home2, secondHomes).add(upReachMonth, home2, secondHomes);
        DistrictPlan onWas = plan(input(types, chained(secondOn, listWas), true)), onNow = plan(input(types, chained(secondOn, listNow), true));
        DistrictPlan onDropped = plan(input(types, chained(secondOn, withoutPair(listWas, passedAt)), true));
        int movedByDrop = missing(carriedWas, onDropped) - 1, movedByLeave = missing(carriedWas, onNow) - 1;
        out.printf("      the second district with its months %d and %d: the house's pair taken out of the list's middle (as before) moves %,d of the %,d boxes it had placed; the departure added, %,d%n",
                upLeaveMonth, upReachMonth, movedByDrop, carriedWas.buildings, movedByLeave);
        check("...with months of its own after the departure's, every box placed before it still stands but the house's, freed, and its plan takes the new months' homes", missing(carriedWas, onNow) == 1 && onNow.overflowCount() == 0
                && missing(carriedWas, onWas) == 0 && onNow.buildings == carriedNow.buildings + 2 * secondHomes && boxesOfType(onNow, house) == boxesOfType(onWas, house) - 1);
        check("...whereas the same list with the house's pair taken out of its middle moves some of them, the fault this fixture causes", movedByDrop > 0);
        // A district that cannot place the house either passes the departure on, as a pair of its own list.
        Log fullToo = new Log().add(1, bigFarm, upFarms).batch();
        DistrictPlan fullWas = plan(input(types, chained(fullToo, listWas), true)), fullNow = plan(input(types, chained(fullToo, listNow), true));
        int[] middleWas = fullWas.overflowLog, middleNow = fullNow.overflowLog;
        int middleAt = -1;
        for (int k = 0; middleWas != null && k < middleWas.length / 2; k++) if (middleWas[2 * k] == upPassedMonth && middleWas[2 * k + 1] == house) middleAt = k;
        boolean middleGrew = middleWas != null && middleNow != null && middleNow.length == middleWas.length + 2 && Arrays.equals(middleWas, Arrays.copyOf(middleNow, middleWas.length));
        check("a district full of the largest farm that is sent the house and cannot place it passes the departure on at the end of its own list, for the house's pair in that list",
                middleAt >= 0 && middleGrew && middleNow[middleNow.length - 2] == upLeaveMonth && middleNow[middleNow.length - 1] == ~middleAt && fullNow.overflowCount() == fullWas.overflowCount() - 1
                        && missing(fullWas, fullNow) == 0 && fullWas.buildings == fullNow.buildings);
        // A carried building that comes and goes within the first batch's months never stands; one that goes after is freed.
        Log lone = new Log().add(upLastMonth, home2, secondHomes).batch();
        DistrictPlan loneNone = plan(input(types, lone, true));
        Log withNever = lone.copy(), withLater = lone.copy();
        withNever.carry = new int[] { upFarmMonth, bigFarm, upHomeMonth, ~0 };
        withLater.carry = new int[] { upFarmMonth, bigFarm, upLeaveMonth, ~0 };
        DistrictPlan neverStood = plan(input(types, withNever, true)), freedLater = plan(input(types, withLater, true));
        check("a carried building that comes and goes within the first batch's months never stands: the plan is the plan without it, box for box", placedDigest(neverStood) == placedDigest(loneNone) && neverStood.buildings == loneNone.buildings
                && neverStood.overflowLog == null);
        check("...one that goes after the first batch is placed with it and freed: the plan without it again, no farm standing", freedLater.buildings == loneNone.buildings && missing(loneNone, freedLater) == 0
                && boxesOfType(freedLater, bigFarm) == 0);

        pinLattice = false;

        // ---- (k) the log squared with the plan's own counts (the map takes out its mines on sites, the yards the runs draw and the shore's works)
        Log old = new Log().add(5, house, 30).add(5, shop, 5).add(5, works, 2).batch();
        DistrictPlan.Input fewer = old.into(flat(types)), fewerToday = input(types, old, false);
        fewer.counts[house] = 27;
        fewerToday.counts[house] = 27;
        DistrictPlan lessLog = plan(fewer), lessToday = plan(fewerToday);
        check("the log is squared with the plan's counts: of a type the log holds more of, the counts' are placed - a log that is one batch gives today's plan from the counts, box for box",
                placedDigest(lessLog) == placedDigest(lessToday) && lessLog.buildings == lessToday.buildings && lessLog.builds == lessToday.builds);
        Log grown = old.copy().add(6, house, 10);
        DistrictPlan.Input trimmed = grown.into(flat(types));
        trimmed.counts[house] -= 4;
        DistrictPlan trimmedPlan = plan(trimmed), smaller = plan(input(types, old.copy().add(6, house, 6), true));
        check("...the newest beyond the counts are not the plan's, none placed: four fewer than the log's gives the plan of the log with four fewer arrivals, box for box",
                placedDigest(trimmedPlan) == placedDigest(smaller) && trimmedPlan.buildings == smaller.buildings);
        DistrictPlan.Input wanted = grown.into(flat(types));
        wanted.counts[house] += 5;
        DistrictPlan wantedPlan = plan(wanted), exact = plan(input(types, grown, true));
        check("...of a type the log holds fewer of, the missing arrive last: five more than the log's, and every box of the plan without them stands", wantedPlan.buildings == exact.buildings + 5
                && missing(exact, wantedPlan) == 0);
        check("...every plan of this section, " + arrivalPlans + " of them, keeps every check", arrivalRules);
    }

    /** A copy of a log with the carry `carry` in. */
    static Log chained(Log own, int[] carry) {
        Log l = own.copy();
        l.carry = carry;
        return l;
    }

    /** A carry with its k-th pair taken out of the list's middle, as a departure of a building passed on once did (before the carry was append-only). */
    static int[] withoutPair(int[] list, int k) {
        int[] r = new int[list.length - 2];
        System.arraycopy(list, 0, r, 0, 2 * k);
        System.arraycopy(list, 2 * k + 2, r, 2 * k, list.length - 2 * k - 2);
        return r;
    }

    /** How many of a plan's boxes are of type t. */
    static int boxesOfType(DistrictPlan p, int t) {
        int n = 0;
        for (int i = 0; i < p.buildings; i++) if (p.btype[i] == t) n++;
        return n;
    }

    /** The months of an overflow log, as counts: "month x count, ...". */
    static String monthsOf(int[] log) {
        java.util.TreeMap<Integer, Integer> m = new java.util.TreeMap<>();
        for (int j = 0; j + 1 < log.length; j += 2) m.merge(log[j], 1, Integer::sum);
        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<Integer, Integer> e : m.entrySet()) sb.append(sb.length() > 0 ? ", " : "").append("month ").append(e.getKey()).append(" x ").append(e.getValue());
        return sb.toString();
    }

    /** Each dense-screen district's counts and road dealt over BATCHES months: batch k a share of each type, the first batch the first (CL3). */
    static Log dealt(DistrictPlan.Input in, CityMap.District d, int batches) {
        Log l = new Log();
        for (int k = 0; k < batches; k++) {
            for (int t = 0; t < in.types.length; t++) {
                if (in.types[t] == null) continue;
                long total = in.types[t].road() != BuildingVisual.NOT_A_ROAD ? d.count(t) : in.counts[t];
                long share = total * (k + 1) / batches - total * k / batches;
                if (share > 0) l.add(k + 1, t, (int) share);
            }
            if (k == 0) l.batch();
        }
        return l;
    }

    /* =====================================================================
       12. THE GROUND BOUGHT (batch FX6)
       ===================================================================== */

    /** A flat district that owns whole cells ci0 to ci1 across and cj0 to cj1 down, their rings too, the hub at its middle: the ground bought's fixture. */
    static DistrictPlan.Input cellsOwned(BuildingVisual.Type[] types, int ci0, int ci1, int cj0, int cj1) {
        DistrictPlan.Input in = flat(types);
        Arrays.fill(in.owned, false);
        for (int y = cj0 * DistrictPlan.CELL; y <= (cj1 + 1) * DistrictPlan.CELL; y++) {
            for (int x = ci0 * DistrictPlan.CELL; x <= (ci1 + 1) * DistrictPlan.CELL; x++) in.owned[y * DistrictPlan.FRAME + x] = true;
        }
        return in;
    }

    /** The houses each month from the first to the fifth brings: with the shops, works and farms beside them, enough to fill the old ground. */
    static final int HOUSES_A_MONTH = 1000;

    /** The plots an input owns. */
    static int countOwned(DistrictPlan.Input in) {
        int n = 0;
        for (boolean o : in.owned) if (o) n++;
        return n;
    }

    /** The input with the ground east of plot column x not owned. */
    static DistrictPlan.Input westOf(DistrictPlan.Input in, int x) {
        for (int p = 0; p < DistrictPlan.AREA; p++) if (p % DistrictPlan.FRAME > x) in.owned[p] = false;
        return in;
    }

    /** The months the ground of `grown` was bought in: `month` for each plot it owns that `old` does not, 0 for the rest. */
    static int[] boughtOn(DistrictPlan.Input grown, DistrictPlan.Input old, int month) {
        int[] at = new int[DistrictPlan.AREA];
        for (int p = 0; p < DistrictPlan.AREA; p++) if (grown.owned[p] && !old.owned[p]) at[p] = month;
        return at;
    }

    /** Whether two plans are the same plan: every box in the order placed, every street, what was left out. */
    static boolean samePlan(DistrictPlan a, DistrictPlan b) {
        return placedDigest(a) == placedDigest(b) && streetDigest(a) == streetDigest(b) && a.buildings == b.buildings && Arrays.equals(a.overflow, b.overflow)
                && Arrays.equals(a.cellKind, b.cellKind);
    }

    /** How many of a plan's boxes lie wholly east of plot column x (the cells bought are east of the old ground). */
    static int boxesEastOf(DistrictPlan p, int x) {
        int n = 0;
        for (int i = 0; i < p.buildings; i++) if (p.bx[i] > x) n++;
        return n;
    }

    static void groundBought(BuildingVisual.Type[] types) {
        out.println("\n--- 12. the ground bought: a district that buys land keeps its buildings where they stand (batch FX6) ---");
        final int house = pick(types, 1, t -> t.category() == BuildingType.RESIDENTIAL && !t.outer() && t.site() == null);
        final int shop = pick(types, 1, t -> t.category() == BuildingType.COMMERCIAL && !t.outer() && t.site() == null);
        final int works = pick(types, 9, t -> t.cls() == BuildingVisual.INDUSTRY && !t.outer() && t.site() == null && t.campusGroup() == BuildingVisual.NO_GROUP);
        final int farm = pick(types, 1, t -> t.category() == BuildingType.AGRICULTURE && t.outer() && t.site() == null);
        final int paved = pavedRoad(types);
        // The old ground: cells 0 to 3 across, 3 and 4 down, the hub on its east edge; the ground bought: cells 4 and 5 beside it,
        // two of them as near the hub as the old ground's nearest and all nearer than its farthest.
        DistrictPlan.Input oldGround = cellsOwned(types, 0, 3, 3, 4), wholeGround = cellsOwned(types, 0, 5, 3, 4);
        final int east = 4 * DistrictPlan.CELL;
        final int bought = 5, first = 0;
        // The log: a first batch in month 0, then every month to the fifth fills the old ground; the purchase comes after the
        // fifth month's arrivals (bought between months), and the sixth month's arrivals come after it.
        Log log = new Log();
        log.add(first, house, 150).add(first, shop, 20).add(first, works, 2).add(first, paved, 3).batch();
        for (int m = 1; m <= bought; m++) log.add(m, house, HOUSES_A_MONTH).add(m, shop, 15).add(m, works, 1 + m % 2).add(m, paved, 2).add(m, farm, 1);
        log.add(bought + 1, house, 120).add(bought + 1, shop, 15).add(bought + 1, works, 2).add(bought + 1, paved, 2);
        Log until = log.until(bought);
        DistrictPlan before = DistrictPlan.make(until.into(cellsOwned(types, 0, 3, 3, 4)));
        DistrictPlan.Input grownIn = log.into(cellsOwned(types, 0, 5, 3, 4));
        grownIn.boughtAt = boughtOn(wholeGround, oldGround, bought);
        DistrictPlan after = DistrictPlan.make(grownIn);
        DistrictPlan sameLand = DistrictPlan.make(log.into(cellsOwned(types, 0, 5, 3, 4)));
        out.printf("      the old ground (%d plots): %,d buildings placed in %d cells, %d without a place; the land bought in month %d (%,d plots): %,d placed, %d without a place,"
                + " %,d east of the old ground; the same ground owned from the start: %,d placed, %d of month %d's plan moved%n", countOwned(oldGround), before.buildings,
                cellsOpened(before), before.overflowCount(), bought, countOwned(wholeGround) - countOwned(oldGround), after.buildings, after.overflowCount(),
                boxesEastOf(after, east), sameLand.buildings, missing(before, sameLand), bought);
        check("the fixture: the district is full - the old ground leaves buildings without a place - and none of its boxes lies on the cells bought", before.overflowCount() > 0
                && boxesEastOf(before, east) == 0);
        check("land bought after a month's arrivals keeps what stands where it stands: every box of that month's plan stands in the next, the purchase between", missing(before, after) == 0);
        check("...and the arrivals after the purchase are placed, some on the cells bought", after.buildings > before.buildings && boxesEastOf(after, east) > 0);
        check("...the streets are joined on today's ground, every check holding", figures(after, grownIn).holds(after, grownIn));
        check("...where the same ground owned from the start moves boxes (the fault this section stands against)", missing(before, sameLand) > 0);
        // ---- land bought inside cells already open: the old ground ends in the middle of the cell beside the hub
        final int cut = east - DistrictPlan.CELL / 2;
        DistrictPlan.Input halfOld = westOf(cellsOwned(types, 0, 3, 3, 4), cut);
        DistrictPlan beforeHalf = DistrictPlan.make(until.into(westOf(cellsOwned(types, 0, 3, 3, 4), cut)));
        DistrictPlan.Input halfIn = log.into(cellsOwned(types, 0, 5, 3, 4));
        halfIn.boughtAt = boughtOn(wholeGround, halfOld, bought);
        DistrictPlan afterHalf = DistrictPlan.make(halfIn);
        int inside = 0;
        for (int i = 0; i < afterHalf.buildings; i++) if (afterHalf.bx[i] > cut && afterHalf.bx[i] < east) inside++;
        out.printf("      the old ground cut in the middle of the cell beside the hub: %,d placed, %d without a place; with its other half and the cells beside bought in month %d: %,d placed,"
                + " %d of them in the cell's other half%n", beforeHalf.buildings, beforeHalf.overflowCount(), bought, afterHalf.buildings, inside);
        check("land bought inside a cell already open adds room there and moves nothing: the district is full, every box stands, the arrivals after the purchase are placed, some in the cell's new half",
                beforeHalf.overflowCount() > 0 && missing(beforeHalf, afterHalf) == 0 && afterHalf.buildings > beforeHalf.buildings && inside > 0);
        // ---- dates that change nothing
        DistrictPlan.Input zeros = log.into(cellsOwned(types, 0, 5, 3, 4));
        zeros.boughtAt = new int[DistrictPlan.AREA];
        check("a plan whose ground was all there at the start (every plot bought in month 0) is the plan with no dates, box for box and street for street", samePlan(DistrictPlan.make(zeros), sameLand));
        Log late = log.copy();
        late.firstBatch = 0;
        for (int[] r : late.rec) if (r[0] <= bought) late.firstBatch++;
        DistrictPlan.Input standing = late.into(cellsOwned(types, 0, 5, 3, 4)), standingNone = late.into(cellsOwned(types, 0, 5, 3, 4));
        standing.boughtAt = boughtOn(wholeGround, oldGround, bought);
        DistrictPlan lateDated = DistrictPlan.make(standing), lateNone = DistrictPlan.make(standingNone);
        check("land bought at or before the month the first batch stands on is ground from the start: the plan is the plan with no dates", samePlan(lateDated, lateNone)
                && lateDated.buildings == lateNone.buildings);
        DistrictPlan again = DistrictPlan.make(grownIn);
        check("a plan is a pure function of its input: the plan with the land bought, made twice, is the same plan box for box and street for street", samePlan(after, again));
    }

    /* =====================================================================
       13. THE AVENUES (batch CL4)
       ===================================================================== */

    /** The avenues' counts of section 5's city x 1 and section 6's played city, summed over their districts (Tally.av), for (i). */
    static long[] x1Avenues, playedAvenues;

    /** Whether the plan's avenue plot q is a street of an arterial's role (ROLE_ARTERIAL, or ROLE_BOULEVARD on a boulevard's second row). */
    static boolean allArterial(DistrictPlan p) {
        if (p.avenue == null) return false;
        for (int q = 0; q < DistrictPlan.AREA; q++) {
            if (p.avenue[q] == 0) continue;
            int r = p.roleAt(q);
            if (p.kindAt(q) == DistrictPlan.NONE || (r != DistrictPlan.ROLE_ARTERIAL && r != DistrictPlan.ROLE_BOULEVARD)) return false;
        }
        return true;
    }

    /** Whether CityRuns' heading numbering gives each arm the (dx, dy) and diagCode the harness uses: the arm in a mark is the one CityRuns' own stretches carry. */
    static boolean headingsOk() {
        for (int a = 0; a < 4; a++) {
            boolean found = false;
            for (int h = 0; h < 8; h++) if (CityRuns.DX[h] == ARM_DX[a] && CityRuns.DY[h] == ARM_DY[a] && CityRuns.diagCode(h) == a) found = true;
            if (!found) return false;
        }
        return true;
    }

    /** The plan's avenue laid against the harness's own: {expected, laid, wrong}, a staircase plot expected where it lies in an open cell's box and `allowed` says the rules let it be laid; wrong counts the plots laid where not expected, not laid where expected and marks that differ. */
    static int[] avenueAgainst(DistrictPlan p, Stairs s, java.util.function.IntPredicate allowed) {
        int n = DistrictPlan.FRAME, expected = 0, laid = 0, wrong = 0;
        for (int q = 0; q < DistrictPlan.AREA; q++) {
            boolean is = p.avenue != null && p.avenue[q] != 0;
            if (s.mark[q] == 0) {
                if (is) wrong++;
                continue;
            }
            boolean should = inOpenBox(p, q % n, q / n) && allowed.test(q);
            if (should) expected++;
            if (is) laid++;
            if (should != is || (is && p.avenue[q] != s.mark[q])) wrong++;
        }
        return new int[] { expected, laid, wrong };
    }

    /** The plan's avenue plots laid, by plot (a mask of avenue[]), or none. */
    static boolean[] avenueMask(DistrictPlan p) {
        boolean[] m = new boolean[DistrictPlan.AREA];
        if (p.avenue != null) for (int q = 0; q < m.length; q++) m[q] = (p.avenue[q] & CityRuns.M_DIAG) != 0;
        return m;
    }

    /** Whether the avenue plot of arm a at step k (its S_k) is laid in plan p. */
    static boolean stepLaid(DistrictPlan p, Stairs s, int a, int k) {
        int x = s.ox + ARM_DX[a] * k, y = s.oy + ARM_DY[a] * k;
        return x >= 0 && y >= 0 && x < DistrictPlan.FRAME && y < DistrictPlan.FRAME && p.avenue != null && p.avenue[y * DistrictPlan.FRAME + x] != 0;
    }

    static void avenues(BuildingVisual.Type[] types) {
        out.println("\n--- 13. the avenues (batch CL4): four at 45 degrees from the founding site, laid when a cell opens ---");
        final int n = DistrictPlan.FRAME, side = DistrictPlan.SIDE;
        final java.util.function.IntPredicate any = q -> true;
        final int house = pick(types, 1, t -> t.category() == BuildingType.RESIDENTIAL && !t.outer() && t.site() == null);
        final int shop = pick(types, 1, t -> t.category() == BuildingType.COMMERCIAL && !t.outer() && t.site() == null);
        final int works = pick(types, 9, t -> t.cls() == BuildingVisual.INDUSTRY && !t.outer() && t.site() == null && t.campusGroup() == BuildingVisual.NO_GROUP);
        final int farm = pick(types, 1, t -> t.category() == BuildingType.AGRICULTURE && t.outer() && t.site() == null);
        final int paved = pavedRoad(types);

        // ---- (a) flat owned ground, every cell open, the hub inside the district
        DistrictPlan.Input in = flatMix(types, FULL_FILL, true, false);
        DistrictPlan p = DistrictPlan.make(in);
        Stairs s = stairs(in);
        Figures f = figures(p, in);
        int[] ag = avenueAgainst(p, s, any);
        int[] perArm = new int[4];
        for (int q = 0; q < DistrictPlan.AREA; q++) if (p.avenue != null && p.avenue[q] != 0) perArm[(p.avenue[q] >> CityRuns.M_DIAG_SHIFT) & 3]++;
        int outers = 0;
        for (boolean o : s.outer) if (o) outers++;
        out.printf("      (a) flat owned ground, the hub at (%d, %d), the origin (%d, %d): %d of %d cells open; %,d avenue plots laid of %,d expected - south-east %d, south-west %d, north-west %d, north-east %d;"
                + " %d open cells crossed, %d cell streets stopped short, %d pair(s) of junctions fewer than LATTICE apart, %d box(es) on one of the %d outer plots%n",
                (int) in.hubX, (int) in.hubY, s.ox, s.oy, cellsOpened(p), DistrictPlan.CELLS, p.avenuePlots, ag[0], perArm[0], perArm[1], perArm[2], perArm[3], p.avenueCells, p.avenueStops,
                f.avenueBad, f.avenueOuter, outers);
        check("(a) the fixture: every cell of the district is open, so each arm's whole staircase lies in open cells", cellsOpened(p) == DistrictPlan.CELLS);
        check("(a) each arm's staircase is laid plot for plot with its marks (M_DIAG, the arm, M_CORNER): " + ag[1] + " laid of " + ag[0] + " expected, " + ag[2] + " wrong",
                ag[0] > 0 && ag[2] == 0 && p.avenuePlots == ag[0] && perArm[0] > 0 && perArm[1] > 0 && perArm[2] > 0 && perArm[3] > 0);
        check("(a) ...each a street of the role ROLE_ARTERIAL, the arms CityRuns.diagCode()'s, and the open cells the avenues cross counted", allArterial(p) && headingsOk() && p.avenueCells > 0);
        check("(a) every check holds with them - one network, the + floor, reach, the surface - none of the " + outers + " outer plots built on and no two junctions along an arm fewer than LATTICE steps apart",
                f.holds(p, in) && f.pieces == 1 && outers > 0 && f.avenueOuter == 0 && f.avenueBad == 0);
        DistrictPlan.Input few = flatMix(types, 0.30, true, false);
        DistrictPlan pf = DistrictPlan.make(few);
        Stairs sf = stairs(few);
        int[] gf = avenueAgainst(pf, sf, any);
        int closedPlots = 0;
        for (int q = 0; q < DistrictPlan.AREA; q++) if (sf.mark[q] != 0 && !inOpenBox(pf, q % n, q / n)) closedPlots++;
        out.printf("      (a) with %d of %d cells open: %d staircase plots lie in closed cells' boxes and none is laid; %,d laid of %,d expected%n", cellsOpened(pf), DistrictPlan.CELLS, closedPlots, gf[1], gf[0]);
        check("(a) a cell left closed lays nothing of an avenue: its plots are laid when it opens (the fixture leaves cells closed that an arm crosses)",
                cellsOpened(pf) < DistrictPlan.CELLS && closedPlots > 0 && gf[2] == 0 && gf[0] > 0 && figures(pf, few).holds(pf, few));

        // ---- (b) a straight highway across the arms
        DistrictPlan.Input hw = flatMix(types, FULL_FILL, true, false);
        final int hy = 100;
        for (int x = 0; x < n; x++) hw.fixed[hy * n + x] = DistrictPlan.FIXED_HIGHWAY;
        DistrictPlan ph = DistrictPlan.make(hw);
        Stairs sh = stairs(hw);
        int underExpected = 0, underOk = 0;
        for (int q = 0; q < DistrictPlan.AREA; q++) {
            if (sh.mark[q] == 0 || hw.fixed[q] != DistrictPlan.FIXED_HIGHWAY || !inOpenBox(ph, q % n, q / n)) continue;
            underExpected++;
            if (ph.avenue != null && ph.avenue[q] != 0 && ph.kindAt(q) == DistrictPlan.UNDER) underOk++;
        }
        Figures fh = figures(ph, hw);
        out.printf("      (b) a highway along row %d: %d avenue plots cross it, %d laid beneath it (UNDER); %d pieces, %d beside the highway%n", hy, underExpected, underOk, fh.pieces, fh.besideHighway);
        check("(b) where an avenue meets a highway its plots there are beneath it (UNDER), never the highway's plot itself (" + underOk + " of " + underExpected + ")",
                underExpected > 0 && underOk == underExpected && ph.avenueUnder == underExpected);
        check("(b) ...one network, no building beside the highway, every check holding", fh.holds(ph, hw) && fh.pieces == 1 && fh.besideHighway == 0);

        // ---- (c) the railway across an arm
        DistrictPlan.Input rl = flatMix(types, FULL_FILL, true, false);
        final int rx = 150;
        for (int y = 0; y < n; y++) rl.fixed[y * n + rx] = DistrictPlan.FIXED_RAIL;
        DistrictPlan pr = DistrictPlan.make(rl);
        Stairs sr = stairs(rl);
        int railExpected = 0, railOk = 0;
        for (int q = 0; q < DistrictPlan.AREA; q++) {
            if (sr.mark[q] == 0 || rl.fixed[q] != DistrictPlan.FIXED_RAIL || !inOpenBox(pr, q % n, q / n)) continue;
            railExpected++;
            if (pr.avenue != null && pr.avenue[q] != 0 && pr.kindAt(q) != DistrictPlan.NONE && (pr.street[q] & DistrictPlan.CROSSING) != 0) railOk++;
        }
        Figures fr = figures(pr, rl);
        out.printf("      (c) a railway along column %d: %d avenue plots cross it, %d laid as a crossing (CROSSING); %d pieces%n", rx, railExpected, railOk, fr.pieces);
        check("(c) where an avenue meets the railway its plots there are a crossing, as an arterial's (" + railOk + " of " + railExpected + ")", railExpected > 0 && railOk == railExpected && pr.avenueRail == railExpected);
        check("(c) ...one network, every check holding", fr.holds(pr, rl) && fr.pieces == 1);

        // ---- (d) a yard and an interchange's reserve on an arm
        final DistrictPlan.Input yd = flatMix(types, FULL_FILL, true, false);
        Stairs sy = stairs(yd);
        // The yard on the north-east arm (arm 3) at steps 18 to 24, the reserve on the south-west arm (arm 1) at steps 28 to 34.
        for (int k = 18; k <= 24; k++) {
            for (int y = sy.oy - 24; y <= sy.oy - 18; y++) yd.fixed[y * n + sy.ox + k] = DistrictPlan.FIXED_YARD;
        }
        for (int k = 28; k <= 34; k++) {
            for (int y = sy.oy + 28; y <= sy.oy + 34; y++) yd.fixed[y * n + sy.ox - k] = DistrictPlan.FIXED_RESERVE;
        }
        DistrictPlan pd = DistrictPlan.make(yd);
        int[] gd = avenueAgainst(pd, sy, q -> yd.fixed[q] != DistrictPlan.FIXED_YARD && yd.fixed[q] != DistrictPlan.FIXED_RESERVE);
        int worksPlots = 0, laidOnWorks = 0;
        for (int q = 0; q < DistrictPlan.AREA; q++) {
            if (sy.mark[q] == 0 || !DistrictPlan.works(yd.fixed[q]) || !inOpenBox(pd, q % n, q / n)) continue;
            worksPlots++;
            if (pd.avenue != null && pd.avenue[q] != 0) laidOnWorks++;
        }
        boolean bothSides = stepLaid(pd, sy, 3, 17) && stepLaid(pd, sy, 3, 25) && stepLaid(pd, sy, 1, 27) && stepLaid(pd, sy, 1, 35);
        Figures fd = figures(pd, yd);
        out.printf("      (d) a yard on the north-east arm (steps 18 to 24) and a reserve on the south-west (28 to 34): %d avenue plots on them, %d laid; %d counted not laid; the avenue laid on both sides of each %s; %d pieces%n",
                worksPlots, laidOnWorks, pd.avenueBroken, bothSides, fd.pieces);
        check("(d) no avenue plot on a yard's or an interchange's reserve plot, and the avenue laid on both sides of each (" + gd[2] + " plots wrong)", worksPlots > 0 && laidOnWorks == 0 && gd[2] == 0 && bothSides);
        check("(d) ...the plots not laid are counted (avenueBroken " + pd.avenueBroken + " of " + worksPlots + "), and every check holds", pd.avenueBroken == worksPlots && fd.holds(pd, yd));

        // ---- (e) fresh water across the arms: a river of the widest a bridge carries, and one plot wider
        for (int w = DistrictPlan.AVENUE_BRIDGE_STEPS; w <= DistrictPlan.AVENUE_BRIDGE_STEPS + 1; w++) {
            final DistrictPlan.Input wi = flatMix(types, FULL_FILL, true, false);
            final boolean wide = w > DistrictPlan.AVENUE_BRIDGE_STEPS;
            final int top = 160;
            for (int y = top; y < top + w; y++) for (int x = 0; x < n; x++) wi.terrain[y * n + x] = World.FRESH;
            DistrictPlan pw = DistrictPlan.make(wi);
            Stairs sw = stairs(wi);
            int[] gw = avenueAgainst(pw, sw, q -> !wide || wi.terrain[q] != World.FRESH);
            int wet = 0, wetLaid = 0, wetBridge = 0;
            for (int q = 0; q < DistrictPlan.AREA; q++) {
                if (sw.mark[q] == 0 || wi.terrain[q] != World.FRESH || !inOpenBox(pw, q % n, q / n)) continue;
                wet++;
                if (pw.avenue != null && pw.avenue[q] != 0) {
                    wetLaid++;
                    if ((pw.street[q] & DistrictPlan.BRIDGE) != 0) wetBridge++;
                }
            }
            boolean shores = stepLaid(pw, sw, 0, top - 1 - sw.oy) && stepLaid(pw, sw, 0, top + w - sw.oy) && stepLaid(pw, sw, 1, top - 1 - sw.oy) && stepLaid(pw, sw, 1, top + w - sw.oy);
            Figures fw = figures(pw, wi);
            out.printf("      (e) a river %d plots wide (%d steps x sqrt 2 = %.1f against ARTERIAL_BRIDGE %d): %d avenue plots on the water, %d laid (%d bridged), %d counted too wide, the shores laid %s; %d pieces%n",
                    w, w, w * Math.sqrt(2), DistrictPlan.ARTERIAL_BRIDGE, wet, wetLaid, wetBridge, pw.avenueWater, shores, fw.pieces);
            if (!wide) {
                check("(e) water " + w + " plots wide, " + w + " steps x sqrt 2 within ARTERIAL_BRIDGE: the avenue bridges it, every plot on the water a BRIDGE (" + wetBridge + " of " + wet + ")",
                        wet > 0 && wetLaid == wet && wetBridge == wet && pw.avenueBridged == wet && pw.avenueWater == 0 && gw[2] == 0 && shores);
            } else {
                check("(e) water " + w + " plots wide, over ARTERIAL_BRIDGE on the diagonal: not laid at all, the avenue stopping at both shores (" + wetLaid + " laid of " + wet + ", " + pw.avenueWater + " counted)",
                        wet > 0 && wetLaid == 0 && pw.avenueWater == wet && pw.avenueBridged == 0 && gw[2] == 0 && shores);
            }
            check("(e) ...every check holds with the water (one network or pieces the ground parts)", fw.holds(pw, wi));
        }

        // ---- (f) the + floor along an avenue
        long stops = 0;
        int typesStopped = 0, badAvenue = 0, badLattice = 0, estatePlans = 0, estateHold = 0, streetJunctions = 0;
        for (BuildingVisual.Type t : types) {
            if (t == null || !t.drawn() || t.site() != null || t.terminal()) continue;
            if (!t.outer() && t.cls() != BuildingVisual.INDUSTRY) continue;
            int[] fp = BuildingVisual.footprint(t);
            if (Math.min(fp[0], fp[1]) > DistrictPlan.MIDDLE) continue;
            int count = (int) Math.ceil(ESTATE_FILL * DistrictPlan.SIDE * DistrictPlan.SIDE / (fp[0] * fp[1]));
            DistrictPlan.Input ei = estateAlone(types, t.id(), count, false), li = estateAlone(types, t.id(), count, false);
            ei.latticeOnly = false;
            ei.hubX = li.hubX = DistrictPlan.FRAME / 2 + DistrictPlan.LATTICE;
            DistrictPlan pe = DistrictPlan.make(ei), pl = DistrictPlan.make(li);
            Stairs se = stairs(ei);
            Figures fe = figures(pe, ei);
            boolean[] asLaid = new boolean[DistrictPlan.AREA];
            for (int q = 0; q < asLaid.length; q++) asLaid[q] = se.mark[q] != 0 && inOpenBox(pl, q % n, q / n);
            estatePlans++;
            if (fe.holds(pe, ei)) estateHold++;
            badAvenue += fe.avenueBad;
            for (int a = 0; a < 4; a++) badLattice += floorBad(junctions(pl, se, a, asLaid, null));
            boolean[] laid = avenueMask(pe);
            int[] street = new int[1];
            for (int a = 0; a < 4; a++) junctions(pe, se, a, laid, street);
            streetJunctions += street[0];
            stops += pe.avenueStops;
            if (pe.avenueStops > 0) typesStopped++;
        }
        out.printf("      (f) %d estate-band types alone on flat ground (section 7's fixtures): with the avenues %,d junction(s) of an estate street with an arm, %,d estate streets stopped short in %d of the plans,"
                + " %d pair(s) of junctions fewer than LATTICE apart; with latticeOnly, the same streets crossing the same arms: %d such pair(s)%n", estatePlans, streetJunctions, stops, typesStopped, badAvenue, badLattice);
        check("(f) the condition caused from both sides: with latticeOnly estate streets meet an arm fewer than LATTICE steps from another junction (" + badLattice + " pairs), with the avenues they stop a plot short ("
                + stops + " stopped) and no pair is left (" + badAvenue + ")", estatePlans > 0 && badLattice > 0 && stops > 0 && badAvenue == 0);
        check("(f) ...every check holds on each of the " + estatePlans + " plans with the avenues, estate cells' lines and T junctions included", estateHold == estatePlans);
        DistrictPlan.Input hi = flat(types);
        hi.hubX = DistrictPlan.FRAME / 2 + DistrictPlan.LATTICE;
        int[] hf = BuildingVisual.footprint(types[house]);
        hi.counts[house] = (int) (0.55 * side * side / (hf[0] * hf[1]));
        DistrictPlan pc = DistrictPlan.make(hi);
        Stairs sc = stairs(hi);
        Figures fc = figures(pc, hi);
        int crossedHomes = 0, crossedComb = 0;
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (pc.cellKind[c] != DistrictPlan.HOMES || !avenueInCell(sc, c % DistrictPlan.CELLS_A_SIDE, c / DistrictPlan.CELLS_A_SIDE)) continue;
            crossedHomes++;
            if (pc.cellLayout[c] == DistrictPlan.LAYOUT_TODAY) crossedComb++;
        }
        int[] combStreets = new int[1];
        boolean[] combLaid = avenueMask(pc);
        for (int a = 0; a < 4; a++) junctions(pc, sc, a, combLaid, combStreets);
        out.printf("      (f) a homes-only district (%,d houses): %d homes cells an avenue crosses, %d of them the comb; %d junctions of cell streets with the arms, %d stopped short, %d pair(s) fewer than LATTICE apart%n",
                hi.counts[house], crossedHomes, crossedComb, combStreets[0], pc.avenueStops, fc.avenueBad);
        check("(f) a homes cell an avenue crosses keeps the comb (" + crossedComb + " of " + crossedHomes + "), its streets cross the lattice's nodes and all meet the avenue: none stopped short, the floor holds",
                crossedHomes > 0 && crossedComb == crossedHomes && combStreets[0] > 0 && pc.avenueStops == 0 && fc.avenueBad == 0 && fc.holds(pc, hi));

        // ---- (g) the order of arrival: cells opening month by month across the arms
        Log log = new Log();
        log.add(0, house, 150).add(0, shop, 20).add(0, works, 2).add(0, paved, 3).batch();
        final int months = 6;
        for (int m = 1; m <= months; m++) log.add(m, house, 900).add(m, shop, 15).add(m, works, 1 + m % 2).add(m, paved, 2).add(m, farm, 1);
        DistrictPlan[] pm = new DistrictPlan[months + 1];
        DistrictPlan.Input[] im = new DistrictPlan.Input[months + 1];
        boolean monthsHold = true;
        for (int m = 0; m <= months; m++) {
            im[m] = log.until(m).into(flat(types));
            pm[m] = DistrictPlan.make(im[m]);
            monthsHold &= figures(pm[m], im[m]).holds(pm[m], im[m]);
        }
        int moved = 0, differ = 0, cellsSeen = 0;
        for (int m = 0; m < months; m++) moved += missing(pm[m], pm[m + 1]);
        for (int m = 0; m < months; m++) {
            for (int c = 0; c < DistrictPlan.CELLS; c++) {
                if (pm[m].cellKind[c] == DistrictPlan.CLOSED) continue;
                cellsSeen++;
                int x0 = DistrictPlan.CELL * (c % DistrictPlan.CELLS_A_SIDE) + 1, y0 = DistrictPlan.CELL * (c / DistrictPlan.CELLS_A_SIDE) + 1;
                for (int y = y0; y < y0 + DistrictPlan.INTERIOR; y++) {
                    for (int x = x0; x < x0 + DistrictPlan.INTERIOR; x++) {
                        byte was = pm[m].avenue == null ? 0 : pm[m].avenue[y * n + x], last = pm[months].avenue == null ? 0 : pm[months].avenue[y * n + x];
                        if (was != last) differ++;
                    }
                }
            }
        }
        StringBuilder crossedPerMonth = new StringBuilder();
        for (int m = 0; m <= months; m++) crossedPerMonth.append(' ').append(pm[m].avenueCells).append('/').append(cellsOpened(pm[m]));
        out.printf("      (g) a log of %d months on flat ground: open cells crossed by an avenue / open cells by month:%s; boxes moved month to the next %d; avenue plots differing between a month's cells and the last month's %d (over %d cells)%n",
                months, crossedPerMonth, moved, differ, cellsSeen);
        check("(g) the fixture: cells open month by month across the arms (" + pm[0].avenueCells + " crossed at month 0, " + pm[months].avenueCells + " at month " + months + ")", pm[0].avenueCells > 0 && pm[months].avenueCells > pm[0].avenueCells);
        check("(g) every box of month m stands at month m + 1, with the avenues, " + months + " months over", moved == 0 && pm[months].buildings > pm[0].buildings);
        check("(g) a cell's avenue plots are the same whichever month it opened, and every check holds each month", differ == 0 && cellsSeen > 0 && monthsHold);

        // ---- (h) a pure function, and two districts agreeing along their shared line
        DistrictPlan again = DistrictPlan.make(in);
        check("(h) the same input twice is the same plan: boxes, streets, avenue marks and every count", samePlan(p, again) && Arrays.equals(p.avenue, again.avenue) && p.avenuePlots == again.avenuePlots
                && p.avenueUnder == again.avenueUnder && p.avenueStops == again.avenueStops && p.avenueCells == again.avenueCells && p.avenueBroken == again.avenueBroken);
        DistrictPlan.Input west = flatMix(types, FULL_FILL, true, false), east = flatMix(types, FULL_FILL, true, false), south = flatMix(types, FULL_FILL, true, false);
        west.hubX = 200;
        west.hubY = 100;
        east.x0 = side;
        east.hubX = 200 - side;
        east.hubY = 100;
        south.y0 = side;
        south.hubX = 200;
        south.hubY = 100 - side;
        DistrictPlan pw = DistrictPlan.make(west), pe = DistrictPlan.make(east), ps = DistrictPlan.make(south);
        int sharedCol = 0, agreeCol = 0, sharedRow = 0, agreeRow = 0, colPairs = 0, rowPairs = 0;
        for (int t = 0; t < n; t++) {
            if (inOpenBox(pw, side, t) && inOpenBox(pe, 0, t)) {
                colPairs++;
                byte a = pw.avenue == null ? 0 : pw.avenue[t * n + side], b = pe.avenue == null ? 0 : pe.avenue[t * n];
                if (a != 0 || b != 0) sharedCol++;
                if (a == b) agreeCol++;
            }
            if (inOpenBox(pw, t, side) && inOpenBox(ps, t, 0)) {
                rowPairs++;
                byte a = pw.avenue == null ? 0 : pw.avenue[side * n + t], b = ps.avenue == null ? 0 : ps.avenue[t];
                if (a != 0 || b != 0) sharedRow++;
                if (a == b) agreeRow++;
            }
        }
        out.printf("      (h) the district and its east neighbour on the column they share: %d plots both open, %d with an avenue plot, %d agreeing; and its south neighbour on the row: %d, %d, %d%n",
                colPairs, sharedCol, agreeCol, rowPairs, sharedRow, agreeRow);
        check("(h) two neighbouring districts lay the same avenue plots with the same marks on the column and on the row their frames share (an arm crosses each)",
                sharedCol > 0 && agreeCol == colPairs && sharedRow > 0 && agreeRow == rowPairs);

        // ---- (j) ground bought after its cell opened (CL4 with FX6's THE GROUND BOUGHT): the arms' plots the city did not own when
        // the cell opened are laid in the month the ground is bought - nothing stood on them, so nothing moves. Until the review of
        // slice A they were decided "not laid" at the opening and never laid (the Earth playtest's north-west arm: 31 of 169 plots).
        {
            final int jHouse = pick(types, 1, t -> t.category() == BuildingType.RESIDENTIAL && !t.outer() && t.site() == null);
            final int jShop = pick(types, 1, t -> t.category() == BuildingType.COMMERCIAL && !t.outer() && t.site() == null);
            final int jWorks = pick(types, 9, t -> t.cls() == BuildingVisual.INDUSTRY && !t.outer() && t.site() == null && t.campusGroup() == BuildingVisual.NO_GROUP);
            final int jPaved = pavedRoad(types);
            // Section 12's ground: cells 0 to 3 across and 3 and 4 down, the old ground west of the middle of cell 3 - the cells the
            // north-west and south-west arms leave the origin (the hub, a cell's corner) through - its east half bought in month 5.
            final int jBought = 5, jCut = 4 * DistrictPlan.CELL - DistrictPlan.CELL / 2;
            Log jLog = new Log();
            jLog.add(0, jHouse, 150).add(0, jShop, 20).add(0, jWorks, 2).add(0, jPaved, 3).batch();
            for (int m = 1; m <= jBought + 1; m++) jLog.add(m, jHouse, 200).add(m, jShop, 10).add(m, jWorks, 1).add(m, jPaved, 2);
            DistrictPlan.Input jOld = westOf(cellsOwned(types, 0, 3, 3, 4), jCut), jWhole = cellsOwned(types, 0, 3, 3, 4);
            DistrictPlan jBefore = DistrictPlan.make(jLog.until(jBought).into(westOf(cellsOwned(types, 0, 3, 3, 4), jCut)));
            DistrictPlan.Input jIn = jLog.into(cellsOwned(types, 0, 3, 3, 4));
            jIn.boughtAt = boughtOn(jWhole, jOld, jBought);
            DistrictPlan jAfter = DistrictPlan.make(jIn);
            Stairs sj = stairs(jIn);
            // The arms' plots in the boxes of the two cells beside the origin, on the half bought.
            int expected = 0, laidBefore = 0, laidAfter = 0, laidWest = 0;
            for (int q = 0; q < DistrictPlan.AREA; q++) {
                int x = q % DistrictPlan.FRAME, y = q / DistrictPlan.FRAME;
                if (sj.mark[q] == 0 || x < 3 * DistrictPlan.CELL || x > 4 * DistrictPlan.CELL || y < 3 * DistrictPlan.CELL || y > 5 * DistrictPlan.CELL) continue;
                if (x <= jCut) { if (jBefore.avenue != null && jBefore.avenue[q] != 0) laidWest++; continue; }
                expected++;
                if (jBefore.avenue != null && jBefore.avenue[q] != 0) laidBefore++;
                if (jAfter.avenue != null && jAfter.avenue[q] != 0) laidAfter++;
            }
            boolean bothOpen = jBefore.cellKind[3 + 3 * DistrictPlan.CELLS_A_SIDE] != DistrictPlan.CLOSED && jBefore.cellKind[3 + 4 * DistrictPlan.CELLS_A_SIDE] != DistrictPlan.CLOSED;
            out.printf("      (j) the half of the cells beside the origin bought in month %d: of its %d avenue plots %d laid before the purchase (%d west of it), %d after; %d of month %d's boxes moved%n",
                    jBought, expected, laidBefore, laidWest, laidAfter, missing(jBefore, jAfter), jBought);
            check("(j) the fixture: the cells the arms leave the origin through open on the old ground, the avenue laid there, none on the half not yet bought", bothOpen && laidWest > 0
                    && expected > 0 && laidBefore == 0);
            check("(j) ground bought after its cell opened: the avenue is laid on it in the month it is bought, every plot (" + laidAfter + " of " + expected + ")", laidAfter == expected);
            check("(j) ...every box of the month before the purchase stands after it, and every check holds", missing(jBefore, jAfter) == 0 && figures(jAfter, jIn).holds(jAfter, jIn));
        }

        // ---- (i) the counts in a real run
        if (x1Avenues != null && playedAvenues != null) {
            out.printf("      (i) his city x 1, summed over its districts: %,d plots laid (%,d beneath a highway, %,d across the railway, %,d bridging), %,d not laid at a yard, reserve or mine, %,d at water too wide, %,d stopped short, %,d open cells crossed%n",
                    x1Avenues[0], x1Avenues[1], x1Avenues[2], x1Avenues[3], x1Avenues[4], x1Avenues[5], x1Avenues[6], x1Avenues[7]);
            out.printf("      (i) the played city (section 6's samples), summed: %,d plots laid (%,d beneath a highway, %,d across the railway, %,d bridging), %,d not laid at a yard, reserve or mine, %,d at water too wide, %,d stopped short, %,d open cells crossed%n",
                    playedAvenues[0], playedAvenues[1], playedAvenues[2], playedAvenues[3], playedAvenues[4], playedAvenues[5], playedAvenues[6], playedAvenues[7]);
            check("(i) avenues are laid in a real run - his city x 1 and the played city - and every check held with them (sections 5 and 6)", x1Avenues[0] > 0 && playedAvenues[0] > 0);
        }
    }

    /* =====================================================================
       14. THE CURVES (batch CL4, slice C)
       ===================================================================== */

    /** The curves' counts of section 5's city x 1 and section 6's played city, summed over their districts (Tally.cv): crescents, crescents the ground refused, streets along a cut drawn as curves, curves in all. For (g). */
    static long[] x1Curves, playedCurves;

    /** How many cells west of the frame section 14's far fixture puts the founding site: CRESCENT_FROM and four more, so every cell of the frame lies beyond CRESCENT_FROM. */
    static final int FAR_CELLS = (int) DistrictPlan.CRESCENT_FROM + 4;

    /** Section 14's crowded fixture: 90% of the ground, with 0.7 of his roads - the ladder takes several builds (the later ones from its turns), squares a few homes cells and leaves a few buildings out, and the far homes cells it does not square are crescents. */
    static final double CROWDED_FILL = 0.90, CROWDED_ROADS = 0.7;

    /** The roads section 14's road-rich fixture is given, a factor of his: BOULEVARD_ROADS, 1 - road enough for the ladder to square cells and make boulevards. */
    static final double ROAD_RICH = BOULEVARD_ROADS;

    /** Section 14 (b)'s site, in cells west of the frame's edge short of CRESCENT_FROM: 1.25 - the frame's first column of cells lies within CRESCENT_FROM of it and its second beyond. */
    static final double MID_WEST = 1.25;

    /** Section 14 (c)'s site for an avenue across the frame: (-ARM_SITE, -ARM_SITE), 21 cells out along the diagonal, so its south-east arm runs through the frame's diagonal cells - the ones nearest it, where the homes cells open. */
    static final int ARM_SITE = 21 * DistrictPlan.CELL;

    /** Section 10's LONG fixture (flat ground at his fill, his roads times LONG_ROADS: every homes cell long blocks) with the founding site at (hubX, hubY) in the frame's plots - outside the frame, for a far one. */
    static DistrictPlan.Input farHomes(BuildingVisual.Type[] types, double hubX, double hubY) {
        DistrictPlan.Input in = layoutInput(types, 0, false);
        in.hubX = hubX;
        in.hubY = hubY;
        return in;
    }

    /** The far fixture: the founding site FAR_CELLS cells west of the frame, level with its middle. */
    static DistrictPlan.Input farHomes(BuildingVisual.Type[] types) {
        return farHomes(types, -FAR_CELLS * DistrictPlan.CELL, DistrictPlan.FRAME / 2);
    }

    /** The frame plot of the cell-axes plot (u, v) of the cell whose interior starts at (x0, y0): u along the long streets, v across them, the long streets running east-west where `across`. */
    static int cellPlot(int x0, int y0, boolean across, int u, int v) {
        return across ? (y0 + v) * DistrictPlan.FRAME + x0 + u : (y0 + u) * DistrictPlan.FRAME + x0 + v;
    }

    /** The harness's own working of a crescent for one bulge (CL4), in the cell's axes, by walking the circle in small steps rather than by DistrictPlan's rows: the plots (u, v) the arc passes through (stair), and those outside them a street-wide band along it reaches, by the nearest approach of a step to the plot's square - surely (tight, nearer than BAND_HALF less a millionth) and at most (loose, a millionth more). */
    static final class Crescent {
        final int bulge, a;
        final double cu, cv, r;
        final boolean[][] stair = new boolean[DistrictPlan.INTERIOR][DistrictPlan.INTERIOR];
        final boolean[][] tight = new boolean[DistrictPlan.INTERIOR][DistrictPlan.INTERIOR], loose = new boolean[DistrictPlan.INTERIOR][DistrictPlan.INTERIOR];
        double firstV, lastV;

        Crescent(int s) {
            int in = DistrictPlan.INTERIOR;
            bulge = s;
            a = DistrictPlan.MIDDLE - s * DistrictPlan.ARC_SHIFT;
            double chord = in + 1;
            r = chord / Math.sqrt(2);
            cu = a + 0.5 - s * chord / 2;
            cv = chord / 2 - 0.5;
            int steps = 40000;
            double[] us = new double[steps + 1], vs = new double[steps + 1];
            for (int i = 0; i <= steps; i++) {
                double th = -Math.PI / 4 + (Math.PI / 2) * i / steps;
                us[i] = cu + s * r * Math.cos(th);
                vs[i] = cv + r * Math.sin(th);
                int iu = (int) Math.floor(us[i]), iv = (int) Math.floor(vs[i]);
                if (iu >= 0 && iu < in && iv >= 0 && iv < in) stair[iv][iu] = true;
            }
            firstV = vs[0];
            lastV = vs[steps];
            for (int v = 0; v < in; v++) {
                for (int u = 0; u < in; u++) {
                    if (stair[v][u]) continue;
                    boolean near = false;
                    for (int dv = -1; dv <= 1 && !near; dv++) for (int du = -1; du <= 1 && !near; du++) {
                        int qv = v + dv, qu = u + du;
                        near = qv >= 0 && qv < in && qu >= 0 && qu < in && stair[qv][qu];
                    }
                    if (!near) continue;
                    double best = Double.MAX_VALUE;
                    for (int i = 0; i <= steps; i++) {
                        double dx = Math.max(Math.max(u - us[i], 0), us[i] - (u + 1)), dy = Math.max(Math.max(v - vs[i], 0), vs[i] - (v + 1));
                        best = Math.min(best, Math.sqrt(dx * dx + dy * dy));
                    }
                    tight[v][u] = best < StreetCurves.BAND_HALF - 1e-6;
                    loose[v][u] = best < StreetCurves.BAND_HALF + 1e-6;
                }
            }
        }

        /** Whether (u, v) is on one of the cell's three long streets' rows. */
        static boolean longRow(int v) {
            for (int s : DistrictPlan.STREETS_AT) if (s == v) return true;
            return false;
        }
    }

    /** The cell c's bulge as its streets show it: +1 or -1 where the street plots of its interior are exactly its three long streets and the harness's arc of that bulge (and a boulevard cell's second row of ring), 0 where they are neither's. */
    static int bulgeOf(DistrictPlan p, int c, Crescent[] arcs) {
        int x0 = DistrictPlan.CELL * (c % DistrictPlan.CELLS_A_SIDE) + 1, y0 = DistrictPlan.CELL * (c / DistrictPlan.CELLS_A_SIDE) + 1;
        boolean across = p.cellAcross[c];
        for (Crescent a : arcs) {
            boolean same = true;
            for (int v = 0; v < DistrictPlan.INTERIOR && same; v++) {
                for (int u = 0; u < DistrictPlan.INTERIOR; u++) {
                    boolean street = p.kindAt(cellPlot(x0, y0, across, u, v)) != DistrictPlan.NONE;
                    boolean boulevardRing = p.cellBoulevard[c] && (u == 0 || v == 0 || u == DistrictPlan.INTERIOR - 1 || v == DistrictPlan.INTERIOR - 1);
                    if (street != (Crescent.longRow(v) || a.stair[v][u] || boulevardRing)) { same = false; break; }
                }
            }
            if (same) return a.bulge;
        }
        return 0;
    }

    /** Whether the middle of cell c of an input lies DistrictPlan.CRESCENT_FROM cells or more from its founding site, in a straight line, as the harness measures it (the squares in plots, against CRESCENT_FROM x CELL squared). */
    static boolean farEnough(DistrictPlan.Input in, int c) {
        double dx = DistrictPlan.CELL * (c % DistrictPlan.CELLS_A_SIDE) + DistrictPlan.CELL / 2.0 - in.hubX, dy = DistrictPlan.CELL * (c / DistrictPlan.CELLS_A_SIDE) + DistrictPlan.CELL / 2.0 - in.hubY;
        double from = DistrictPlan.CRESCENT_FROM * DistrictPlan.CELL;
        return dx * dx + dy * dy >= from * from;
    }

    /** Whether the place's hash and CL2 layout make cell c a crescent, whatever its distance and its ground (DistrictPlan.crescentOf at the cell's place in the world). */
    static boolean hashWants(DistrictPlan.Input in, int c) {
        return DistrictPlan.crescentOf(in.seed, in.x0 + (long) DistrictPlan.CELL * (c % DistrictPlan.CELLS_A_SIDE), in.y0 + (long) DistrictPlan.CELL * (c / DistrictPlan.CELLS_A_SIDE));
    }

    /** The crescent cell a plan opened, as the harness expects it of a long-block homes cell: the place's hash and layout, beyond CRESCENT_FROM, no highway, railway, yard, reserve or avenue in it - and the ground laying the arc whole, which flat owned ground does. */
    static boolean expectedCrescent(DistrictPlan p, DistrictPlan.Input in, int c) {
        if (p.cellKind[c] != DistrictPlan.HOMES || p.cellSquare[c] || in.asPrototype || in.latticeOnly || in.combOnly) return false;
        return farEnough(in, c) && hashWants(in, c) && !crossedCell(in, c % DistrictPlan.CELLS_A_SIDE, c / DistrictPlan.CELLS_A_SIDE);
    }

    /** A crescent curve of plan p whose first plot is the frame plot `first`, or null. */
    static DistrictPlan.Curve crescentCurve(DistrictPlan p, int first) {
        for (DistrictPlan.Curve cv : p.curves) if (cv.kind == DistrictPlan.Curve.CRESCENT && cv.plots()[0] == first) return cv;
        return null;
    }

    /** The plots every curve of the plan reaches with its band, by plot. */
    static boolean[] bandMask(DistrictPlan p) {
        boolean[] m = new boolean[DistrictPlan.AREA];
        for (DistrictPlan.Curve cv : p.curves) for (int q : cv.band()) m[q] = true;
        return m;
    }

    /** How many of a plan's boxes stand on a plot of mask. */
    static int boxesOn(DistrictPlan p, boolean[] mask) {
        int n = 0;
        for (int i = 0; i < p.buildings; i++) {
            boolean hit = false;
            for (int y = p.by[i]; y < p.by[i] + p.bh[i] && !hit; y++) for (int x = p.bx[i]; x < p.bx[i] + p.bw[i]; x++) if (mask[y * DistrictPlan.FRAME + x]) { hit = true; break; }
            if (hit) n++;
        }
        return n;
    }

    /** Whether two plans have the same curves: kind, circle or vertices, radius, plots and band, in order. */
    static boolean sameCurves(DistrictPlan a, DistrictPlan b) {
        if (a.curves.size() != b.curves.size()) return false;
        for (int i = 0; i < a.curves.size(); i++) {
            DistrictPlan.Curve x = a.curves.get(i), y = b.curves.get(i);
            if (x.kind != y.kind || !Arrays.equals(x.geom, y.geom) || x.radius != y.radius || !Arrays.equals(x.plots(), y.plots()) || !Arrays.equals(x.band(), y.band())) return false;
        }
        return true;
    }

    /** What section 14 finds of a plan's homes cells and crescents (crescentStats()). */
    static final class CrescentStats {
        boolean wrongLayout, exact = true, ringsJoined = true, crossesAll = true, curved = true, geomHolds = true, bandHolds = true, noMerge = true;
        int homes, squared, crescents, wanted, mergedElsewhere, wideBoxes, wideInCrescent, onBand, acrossCells, alongCells;
        final int[] bulges = new int[2];
        String first = "";
    }

    /** A plan's homes cells held to what the harness expects of them: each crescent exactly the cell the place, the distance and the crossings make one, its streets its three long streets and the arc the harness walks out, its curve and band the harness's, its blocks unmerged, no box on a band, no wide box in one. */
    static CrescentStats crescentStats(DistrictPlan p, DistrictPlan.Input in, Crescent[] arcs) {
        final int n = DistrictPlan.FRAME, cell = DistrictPlan.CELL, side = DistrictPlan.CELLS_A_SIDE, in31 = DistrictPlan.INTERIOR;
        CrescentStats st = new CrescentStats();
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (p.cellKind[c] != DistrictPlan.HOMES) continue;
            st.homes++;
            if (p.cellSquare[c]) st.squared++;
            boolean want = expectedCrescent(p, in, c), is = p.cellLayout[c] == DistrictPlan.LAYOUT_CRESCENT;
            if (want) st.wanted++;
            if (want != is) { st.wrongLayout = true; if (st.first.isEmpty()) st.first = "cell " + c + " expected " + want + " is " + is; }
            if (!is) {
                if (p.cellMerged[c]) st.mergedElsewhere++;
                continue;
            }
            st.crescents++;
            if (p.cellMerged[c]) st.noMerge = false;
            int x0 = cell * (c % side) + 1, y0 = cell * (c / side) + 1;
            boolean across = p.cellAcross[c];
            if (across) st.acrossCells++; else st.alongCells++;
            int s = bulgeOf(p, c, arcs);
            st.exact &= s != 0;
            if (s == 0) { if (st.first.isEmpty()) st.first = "cell " + c + " streets match neither arc"; continue; }
            st.bulges[(s + 1) / 2]++;
            Crescent a = arcs[(s + 1) / 2];
            // The rings: the arc's end plots are the ring's own arterial plots, and the plot beside each inside the cell is the arc's.
            int ringA = cellPlot(x0, y0, across, a.a, -1), ringB = cellPlot(x0, y0, across, a.a, in31);
            st.ringsJoined &= (p.roleAt(ringA) == DistrictPlan.ROLE_ARTERIAL || p.roleAt(ringA) == DistrictPlan.ROLE_BOULEVARD)
                    && (p.roleAt(ringB) == DistrictPlan.ROLE_ARTERIAL || p.roleAt(ringB) == DistrictPlan.ROLE_BOULEVARD)
                    && a.stair[0][a.a] && a.stair[in31 - 1][a.a] && p.kindAt(cellPlot(x0, y0, across, a.a, 0)) != DistrictPlan.NONE && p.kindAt(cellPlot(x0, y0, across, a.a, in31 - 1)) != DistrictPlan.NONE;
            // It crosses each long street: a plot of the arc in its row, a street of the cell's.
            for (int v : DistrictPlan.STREETS_AT) {
                boolean cross = false;
                for (int u = 0; u < in31; u++) if (a.stair[v][u] && p.roleAt(cellPlot(x0, y0, across, u, v)) == DistrictPlan.ROLE_STREET) cross = true;
                st.crossesAll &= cross;
            }
            // Its curve: the staircase from ring to ring, the circle, the band.
            DistrictPlan.Curve cv = crescentCurve(p, ringA);
            if (cv == null) { st.curved = false; if (st.first.isEmpty()) st.first = "cell " + c + " has no curve"; continue; }
            java.util.Set<Integer> want0 = new java.util.HashSet<>(), got = new java.util.HashSet<>();
            for (int v = 0; v < in31; v++) for (int u = 0; u < in31; u++) if (a.stair[v][u]) want0.add(cellPlot(x0, y0, across, u, v));
            want0.add(ringA);
            want0.add(ringB);
            int[] pl = cv.plots();
            for (int q : pl) got.add(q);
            st.curved &= want0.equals(got) && pl.length == want0.size() && pl[pl.length - 1] == ringB;
            // The circle: radius, a quarter turn, from the middle of the first ring plot to the middle of the last.
            double cx = cv.geom[0], cy = cv.geom[1], r = cv.geom[2], a0 = cv.geom[3], sw = cv.geom[4];
            double sx = cx + r * Math.cos(a0), sy = cy + r * Math.sin(a0), ex = cx + r * Math.cos(a0 + sw), ey = cy + r * Math.sin(a0 + sw);
            boolean ends = Math.abs(sx - (ringA % n + 0.5)) < 1e-6 && Math.abs(sy - (ringA / n + 0.5)) < 1e-6 && Math.abs(ex - (ringB % n + 0.5)) < 1e-6 && Math.abs(ey - (ringB / n + 0.5)) < 1e-6;
            st.geomHolds &= ends && Math.abs(r - (in31 + 1) / Math.sqrt(2)) < 1e-9 && Math.abs(Math.abs(sw) - Math.PI / 2) < 1e-9 && cv.radius == r && cv.kind == DistrictPlan.Curve.CRESCENT;
            // The band: the plots the harness's walk surely reaches are in it, and it reaches none the walk does not (within a millionth).
            java.util.Set<Integer> band = new java.util.HashSet<>();
            for (int q : cv.band()) band.add(q);
            for (int v = 0; v < in31; v++) {
                for (int u = 0; u < in31; u++) {
                    int q = cellPlot(x0, y0, across, u, v);
                    if (a.tight[v][u] && !band.contains(q)) st.bandHolds = false;
                    if (band.contains(q) && !a.loose[v][u]) st.bandHolds = false;
                }
            }
        }
        for (int i = 0; i < p.buildings; i++) {
            if (Math.min(p.bw[i], p.bh[i]) <= DistrictPlan.BLOCK_DEPTH) continue;
            st.wideBoxes++;
            if (p.cellLayout[cellOfBox(p, i)] == DistrictPlan.LAYOUT_CRESCENT) st.wideInCrescent++;
        }
        st.onBand = boxesOn(p, bandMask(p));
        return st;
    }

    /** Every check but the one network, which holds on a district planned alone only where the ground leaves it one piece: the map joins a district's pieces across the districts before it, and a district of the dense city's rim planned alone (rimCurves()) is often several, at 0.8.18 as now. */
    static boolean holdsApartFromNetwork(Figures f, DistrictPlan p, DistrictPlan.Input in) {
        return f.closestPlus >= DistrictPlan.LATTICE && f.outOfReach == 0 && f.onStreet == 0 && f.surfaceHolds(p, in) && f.besideHighway == 0 && f.estateHolds() && f.avenueBad == 0 && f.avenueOuter == 0;
    }

    static void curves(BuildingVisual.Type[] types) {
        out.println("\n--- 14. the curves (batch CL4, slice C): crescents far from the founding site, and the streets along a cut drawn smooth ---");
        final int n = DistrictPlan.FRAME, cell = DistrictPlan.CELL, side = DistrictPlan.CELLS_A_SIDE, in31 = DistrictPlan.INTERIOR;
        final long seed = Founding.DEFAULT_WORLD_SEED;
        final Crescent[] arcs = { new Crescent(-1), new Crescent(1) };
        final int house = pick(types, 1, t -> t.category() == BuildingType.RESIDENTIAL && !t.outer() && t.site() == null);
        final int shop = pick(types, 1, t -> t.category() == BuildingType.COMMERCIAL && !t.outer() && t.site() == null);
        final int works = pick(types, 9, t -> t.cls() == BuildingVisual.INDUSTRY && !t.outer() && t.site() == null && t.campusGroup() == BuildingVisual.NO_GROUP);
        final int farm = pick(types, 1, t -> t.category() == BuildingType.AGRICULTURE && t.outer() && t.site() == null);
        final int paved = pavedRoad(types);

        // ---- (a) flat owned ground, its homes cells long blocks, the founding site FAR_CELLS cells west of the frame
        DistrictPlan.Input far = farHomes(types);
        DistrictPlan p = DistrictPlan.make(far);
        Figures f = figures(p, far);
        CrescentStats cs = crescentStats(p, far, arcs);
        int[] bulges = cs.bulges;
        out.printf("      (a) flat owned ground, the founding site %d cells west of the frame: %d homes cells (%d squared), %d crescents of %d expected (bulge toward -u %d, +u %d; the long streets running east-west in %d, north-south in %d), %d crescent curves;"
                + " %d boxes wider than a block (%d in a crescent), %d cells with blocks merged (none a crescent); %d boxes on a band; %s%n", FAR_CELLS, cs.homes, cs.squared, cs.crescents, cs.wanted, bulges[0], bulges[1], cs.acrossCells, cs.alongCells,
                p.curves.size(), cs.wideBoxes, cs.wideInCrescent, cs.mergedElsewhere, cs.onBand, line(p, f));
        check("(a) the fixture: long-block homes cells only (none squared), and beyond CRESCENT_FROM = " + (int) DistrictPlan.CRESCENT_FROM + " cells from the founding site some cells are crescents (" + cs.crescents + " of " + cs.homes + ")",
                cs.homes > 0 && cs.squared == 0 && cs.crescents > 0 && p.crescents == cs.crescents && p.crescentsOff == 0);
        check("(a) the crescents are exactly the cells the harness expects - the place's hash and CL2 layout, beyond the distance, uncrossed" + (cs.first.isEmpty() ? "" : " (" + cs.first + ")"), !cs.wrongLayout && cs.crescents == cs.wanted);
        check("(a) each crescent's streets are its three long streets and the arc the harness walks out for its bulge, plot for plot (both bulges appear, " + bulges[0] + " and " + bulges[1] + ", and both directions of the long streets, "
                + cs.acrossCells + " and " + cs.alongCells + ")", cs.exact && bulges[0] > 0 && bulges[1] > 0 && cs.acrossCells > 0 && cs.alongCells > 0);
        check("(a) ...the arc joins its two rings in T junctions (the ring's plot at each end, the arc's beside it) and crosses its three long streets", cs.ringsJoined && cs.crossesAll);
        check("(a) ...its curve is the staircase from ring to ring and the circle of radius (INTERIOR + 1) / sqrt 2, a quarter turn from the middle of one ring plot to the middle of the other", cs.curved && cs.geomHolds && p.curves.size() == cs.crescents);
        check("(a) ...and its band is the plots a street-wide band along the arc reaches, no box standing on one (" + cs.onBand + " boxes on the " + countTrue(bandMask(p)) + " plots)", cs.bandHolds && cs.onBand == 0);
        check("(a) every check holds - one network, the + floor, reach, the surface - and no building wider than a block stands in a crescent, whose blocks are never merged (" + cs.wideBoxes + " such boxes, "
                + cs.mergedElsewhere + " cells merged elsewhere)", f.holds(p, far) && f.pieces == 1 && cs.wideBoxes > 0 && cs.wideInCrescent == 0 && cs.noMerge && cs.mergedElsewhere > 0);
        // The same with road to spare: the surface ladder climbs (squares some homes cells, makes boulevards of arterials), its steps built from the ladder's turns - the crescents among the cells it leaves long blocks hold all the same.
        DistrictPlan.Input rich = farHomes(types);
        rich.gravel *= ROAD_RICH / LONG_ROADS;
        rich.paved *= ROAD_RICH / LONG_ROADS;
        DistrictPlan prich = DistrictPlan.make(rich);
        Figures frich = figures(prich, rich);
        CrescentStats rs = crescentStats(prich, rich, arcs);
        out.printf("      (a) with %.1f times his roads (the ladder climbed: %d builds): %d homes cells (%d squared), %d crescents of %d expected, %d crescent curves, %d boxes on a band; %s%n", ROAD_RICH, prich.builds, rs.homes, rs.squared, rs.crescents, rs.wanted,
                prich.curves.size(), rs.onBand, line(prich, frich));
        check("(a) with road to spare the ladder squares homes cells (" + rs.squared + " of " + rs.homes + ", " + prich.builds + " builds), the crescents among the others are the cells expected and their arcs, curves and bands are the harness's, every check holding",
                prich.builds > 1 && rs.squared > 0 && rs.crescents > 0 && !rs.wrongLayout && rs.crescents == rs.wanted && rs.exact && rs.curved && rs.geomHolds && rs.bandHolds && rs.noMerge && rs.onBand == 0
                        && rs.wideInCrescent == 0 && frich.holds(prich, rich) && prich.curves.size() == rs.crescents);

        // ...and crowded, the ladder's first steps leaving buildings out so a later one is gone on with from a turn (THE LADDER'S TURNS): the curves kept through it.
        DistrictPlan.Input crowd = flatMix(types, CROWDED_FILL, true, false);
        crowd.gravel *= CROWDED_ROADS;
        crowd.paved *= CROWDED_ROADS;
        crowd.hubX = -FAR_CELLS * cell;
        crowd.hubY = DistrictPlan.FRAME / 2;
        DistrictPlan pcrowd = DistrictPlan.make(crowd);
        Figures fcrowd = figures(pcrowd, crowd);
        CrescentStats ccs = crescentStats(pcrowd, crowd, arcs);
        out.printf("      (a) crowded, at %.0f%% of the ground with %.1f times his roads (%d builds, %d buildings without a place): %d homes cells (%d squared), %d crescents of %d expected, %d crescent curves, %d boxes on a band; %s%n", CROWDED_FILL * 100, CROWDED_ROADS,
                pcrowd.builds, pcrowd.overflowCount(), ccs.homes, ccs.squared,
                ccs.crescents, ccs.wanted, pcrowd.curves.size(), ccs.onBand, line(pcrowd, fcrowd));
        check("(a) crowded the ladder takes several builds (" + pcrowd.builds + "), the later ones from its turns, and squares only some homes cells (" + ccs.squared + " of " + ccs.homes + "): the crescents among the rest are the cells expected, their arcs, curves and bands the harness's, no box on a band, every check holding",
                pcrowd.builds > 2 && ccs.squared > 0 && ccs.squared < ccs.homes && ccs.crescents > 0 && !ccs.wrongLayout && ccs.crescents == ccs.wanted && ccs.exact && ccs.curved && ccs.geomHolds && ccs.bandHolds && ccs.noMerge && ccs.onBand == 0 && ccs.wideInCrescent == 0
                        && fcrowd.holds(pcrowd, crowd) && pcrowd.curves.size() == ccs.crescents);

        // ---- (b) the same ground with the founding site inside the frame, and one site that puts some cells on each side of the distance
        DistrictPlan.Input near = layoutInput(types, 0, false);
        DistrictPlan pn = DistrictPlan.make(near);
        int hashNear = 0, crescentsNear = 0, variedNear = 0;
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (pn.cellKind[c] != DistrictPlan.HOMES) continue;
            if (hashWants(near, c)) hashNear++;
            if (pn.cellLayout[c] == DistrictPlan.LAYOUT_CRESCENT) crescentsNear++;
            if (pn.cellLayout[c] != DistrictPlan.LAYOUT_TODAY) variedNear++;
        }
        DistrictPlan.Input mid = farHomes(types, -(DistrictPlan.CRESCENT_FROM - MID_WEST) * cell, DistrictPlan.FRAME / 2);
        DistrictPlan pm = DistrictPlan.make(mid);
        int farWant = 0, nearWant = 0, farCrescent = 0, nearCrescent = 0;
        boolean midRight = true;
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (pm.cellKind[c] != DistrictPlan.HOMES || pm.cellSquare[c] || crossedCell(mid, c % side, c / side) || !hashWants(mid, c)) continue;
            boolean farCell = farEnough(mid, c), is = pm.cellLayout[c] == DistrictPlan.LAYOUT_CRESCENT;
            if (farCell) { farWant++; if (is) farCrescent++; } else { nearWant++; if (is) nearCrescent++; }
            midRight &= is == farCell;
        }
        out.printf("      (b) the founding site inside the frame: %d homes cells, %d of them the hash would make crescents, %d crescents, %d of another layout than the comb; the site %.2f cells west of the frame's edge:"
                + " %d cells the hash wants beyond the distance (%d crescents) and %d within it (%d crescents)%n", homesIn(pn), hashNear, crescentsNear, variedNear, DistrictPlan.CRESCENT_FROM - MID_WEST, farWant, farCrescent, nearWant, nearCrescent);
        check("(b) nearer than CRESCENT_FROM no cell is a crescent, though the hash and CL2 layout would make " + hashNear + " of them one beyond it", hashNear > 0 && crescentsNear == 0 && variedNear > 0 && pn.crescents == 0 && pn.curves.stream().noneMatch(cv -> cv.kind == DistrictPlan.Curve.CRESCENT));
        check("(b) ...the threshold from both sides in one plan: of the cells the hash wants, those beyond the distance are crescents (" + farCrescent + " of " + farWant + ") and those within it are not (" + nearCrescent + " of " + nearWant + ")",
                farWant > 0 && nearWant > 0 && midRight && farCrescent == farWant && nearCrescent == 0);

        // ---- (c) a crossed cell beyond CRESCENT_FROM keeps the comb: a railway plot in a crescent's interior, and avenues across the cells
        int railCell = -1, crossedKept = 0, crossedTried = 0;
        boolean othersKept = false;
        for (int c = 0; c < DistrictPlan.CELLS && railCell < 0; c++) {
            if (p.cellLayout[c] != DistrictPlan.LAYOUT_CRESCENT) continue;
            crossedTried++;
            int x0 = cell * (c % side) + 1, y0 = cell * (c / side) + 1;
            DistrictPlan.Input rail = farHomes(types);
            rail.fixed[(y0 + 3) * n + x0 + 3] = DistrictPlan.FIXED_RAIL;
            DistrictPlan pr = DistrictPlan.make(rail);
            if (pr.cellKind[c] != DistrictPlan.HOMES || pr.cellSquare[c] || !hashWants(rail, c)) continue;
            railCell = c;
            crossedKept = pr.cellLayout[c] == DistrictPlan.LAYOUT_TODAY ? 1 : 0;
            for (int d = 0; d < DistrictPlan.CELLS; d++) if (d != c && pr.cellLayout[d] == DistrictPlan.LAYOUT_CRESCENT) othersKept = true;
        }
        DistrictPlan.Input arms = farHomes(types, -ARM_SITE, -ARM_SITE);
        DistrictPlan pa = DistrictPlan.make(arms);
        int armCells = 0, armComb = 0, armCrescents = 0, armWanted = 0;
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (pa.cellKind[c] != DistrictPlan.HOMES || pa.cellSquare[c]) continue;
            if (pa.cellLayout[c] == DistrictPlan.LAYOUT_CRESCENT) armCrescents++;
            if (!crossedCell(arms, c % side, c / side)) continue;
            armCells++;
            if (pa.cellLayout[c] == DistrictPlan.LAYOUT_TODAY) armComb++;
            if (farEnough(arms, c) && hashWants(arms, c)) armWanted++;
        }
        out.printf("      (c) a railway plot in the interior of crescent cell %s (the %d%s tried): that cell has the comb %s, other cells still crescents %s; with an avenue's arm across the frame (the site at (-%d, -%d), its south-east arm along the frame's diagonal):"
                + " %d homes cells it crosses, %d of them the comb (%d would be crescents by hash and distance), %d crescents elsewhere%n", railCell, crossedTried, crossedTried == 1 ? "st" : "th", crossedKept == 1, othersKept,
                ARM_SITE, ARM_SITE, armCells, armComb, armWanted, armCrescents);
        check("(c) a crescent cell a railway plot is laid in (the same input with it) keeps the comb, the other crescents staying", railCell >= 0 && crossedKept == 1 && othersKept);
        check("(c) ...so do the cells an avenue crosses: all " + armCells + " keep the comb, though the hash and distance would make " + armWanted + " of them crescents, and the cells it does not cross are crescents (" + armCrescents + ")",
                armCells > 0 && armComb == armCells && armWanted > 0 && armCrescents > 0 && figures(pa, arms).holds(pa, arms));

        // ---- (d) the share over many cells, and (with the plans) every cell as the function says
        long[] place = MapCheck.dryPlace(World.of(seed));
        long i0 = Math.floorDiv(place[0], cell), j0 = Math.floorDiv(place[1], cell);
        int notComb = 0, cres = 0, plus = 0, minus = 0, cells = SHARE_SIDE * SHARE_SIDE;
        for (int j = 0; j < SHARE_SIDE; j++) {
            for (int i = 0; i < SHARE_SIDE; i++) {
                long cx = (i0 + i) * cell, cy = (j0 + j) * cell;
                if (DistrictPlan.layoutOf(seed, cx, cy, false) == DistrictPlan.LAYOUT_TODAY) continue;
                notComb++;
                if (!DistrictPlan.crescentOf(seed, cx, cy)) continue;
                cres++;
                if (DistrictPlan.crescentBulge(seed, cx, cy) > 0) plus++; else minus++;
            }
        }
        double share = cres / (double) notComb;
        out.printf("      (d) over %,d cells of the lattice: %,d are not the comb, %,d of those crescents - %.4f against CRESCENT_SHARE %.2f (%.4f of all the cells: a third); bulging toward +u %,d, -u %,d%n", cells, notComb, cres, share,
                DistrictPlan.CRESCENT_SHARE, cres / (double) cells, plus, minus);
        check("(d) of the long-block cells beyond CRESCENT_FROM whose CL2 layout is not the comb, the crescents' share is CRESCENT_SHARE within 0.01 (" + String.format("%.4f", share) + ")", Math.abs(share - DistrictPlan.CRESCENT_SHARE) <= SHARE_TOLERANCE);
        check("(d) ...a third of all the cells are crescents (a third keep the comb, a sixth drop crossings, a sixth are brick) and the arc bulges each way in about half", Math.abs(cres / (double) cells - (1 - DistrictPlan.TODAY_SHARE) * DistrictPlan.CRESCENT_SHARE) <= SHARE_TOLERANCE
                && Math.abs(plus / (double) cres - 0.5) <= SHARE_TOLERANCE);

        // ---- (c2) the ground refusing an arc: a pond wider than a street's bridge in the middle of every second crescent's arc; the cells it cuts keep the layout they have
        DistrictPlan.Input wet = farHomes(types);
        final int wide = DistrictPlan.STREET_BRIDGE + 2;
        boolean[] pond = new boolean[DistrictPlan.CELLS];
        int ponds = 0, seen = 0;
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (p.cellLayout[c] != DistrictPlan.LAYOUT_CRESCENT || seen++ % 2 == 0) continue;
            int s = bulgeOf(p, c, arcs), x0 = cell * (c % side) + 1, y0 = cell * (c / side) + 1;
            Crescent a = arcs[(s + 1) / 2];
            int lo = in31, hi = -1;
            for (int u = 0; u < in31; u++) if (a.stair[in31 / 2][u]) { lo = Math.min(lo, u); hi = Math.max(hi, u); }
            int mu = (lo + hi) / 2;
            for (int v = in31 / 2 - wide / 2; v < in31 / 2 - wide / 2 + wide; v++) {
                for (int u = mu - wide / 2; u < mu - wide / 2 + wide; u++) wet.terrain[cellPlot(x0, y0, p.cellAcross[c], u, v)] = World.FRESH;
            }
            pond[c] = true;
            ponds++;
        }
        DistrictPlan pw = DistrictPlan.make(wet);
        int wetWant = 0, wetKept = 0, wetOff = 0, wetCres = 0, wetWrong = 0;
        boolean wetWhole = true;
        for (int c = 0; c < DistrictPlan.CELLS; c++) {
            if (pw.cellKind[c] != DistrictPlan.HOMES || pw.cellSquare[c] || !hashWants(wet, c) || !farEnough(wet, c) || crossedCell(wet, c % side, c / side)) continue;
            wetWant++;
            if (pw.cellLayout[c] == DistrictPlan.LAYOUT_CRESCENT) {
                wetCres++;
                wetWhole &= bulgeOf(pw, c, arcs) != 0;
                if (pond[c]) wetWrong++;
            } else {
                wetOff++;
                if (pw.cellLayout[c] == DistrictPlan.layoutOf(seed, wet.x0 + (long) cell * (c % side), wet.y0 + (long) cell * (c / side), false)) wetKept++;
                if (!pond[c]) wetWrong++;
            }
        }
        Figures fw = figures(pw, wet);
        out.printf("      (c2) a %d x %d pond in the middle of the arc of %d of the %d crescents: of %d cells the hash wants as crescents %d are (arcs whole), %d keep the layout CL2 gave them (counted: %d), %d on the wrong side of their pond; %s%n",
                wide, wide, ponds, p.crescents, wetWant, wetCres, wetOff, pw.crescentsOff, wetWrong, line(pw, fw));
        check("(c2) a cell the hash wants as a crescent whose arc the ground would not lay whole (water wider than a street's bridge) keeps its CL2 layout and is counted (" + wetOff + " cells, crescentsOff " + pw.crescentsOff + "), the others are crescents with the arc whole ("
                + wetCres + ")", ponds > 0 && wetOff > 0 && wetCres > 0 && wetKept == wetOff && pw.crescentsOff == wetOff && wetWhole && pw.crescents == wetCres && wetWrong == 0);
        check("(c2) ...every check holds with the ponds (one network or pieces the ground parts)", fw.holds(pw, wet));
        DistrictPlan.Input lat = farHomes(types);
        lat.latticeOnly = true;
        DistrictPlan pl = DistrictPlan.make(lat);
        DistrictPlan.Input comb = farHomes(types);
        comb.combOnly = true;
        DistrictPlan pc0 = DistrictPlan.make(comb);
        check("(c2) with latticeOnly or combOnly no cell is a crescent and the plan lays no curve (" + pl.crescents + " and " + pc0.crescents + " crescents, " + pl.curves.size() + " and " + pc0.curves.stream().filter(cv -> cv.kind == DistrictPlan.Curve.CRESCENT).count() + " curves)",
                pl.crescents == 0 && pc0.crescents == 0 && pl.curves.isEmpty() && pc0.curves.stream().noneMatch(cv -> cv.kind == DistrictPlan.Curve.CRESCENT));

        // ---- (e) the shore: the test district cut by its river and the sea
        shoreCurves(types);

        // ---- (f) a pure function, and the order of arrival
        DistrictPlan again = DistrictPlan.make(far);
        check("(f) the same input twice is the same plan: boxes, streets, layouts, counts and every curve, its plots and its band", samePlan(p, again) && Arrays.equals(p.cellLayout, again.cellLayout) && sameCurves(p, again)
                && p.crescents == again.crescents && p.shoreCurves == again.shoreCurves);
        Log log = new Log();
        log.add(0, house, 150).add(0, shop, 20).add(0, works, 2).add(0, paved, 3).batch();
        final int months = 6;
        for (int m = 1; m <= months; m++) log.add(m, house, HOUSES_A_MONTH_FAR).add(m, shop, 15).add(m, works, 1 + m % 2).add(m, paved, 2).add(m, farm, 1);
        DistrictPlan[] pmo = new DistrictPlan[months + 1];
        boolean monthsHold = true, curvesStand = true;
        int movedBoxes = 0, crescentsEver = 0;
        for (int m = 0; m <= months; m++) {
            DistrictPlan.Input im = log.until(m).into(flat(types));
            im.hubX = -FAR_CELLS * cell;
            im.hubY = DistrictPlan.FRAME / 2;
            pmo[m] = DistrictPlan.make(im);
            monthsHold &= figures(pmo[m], im).holds(pmo[m], im);
        }
        for (int m = 0; m < months; m++) {
            movedBoxes += missing(pmo[m], pmo[m + 1]);
            for (DistrictPlan.Curve cv : pmo[m].curves) {
                boolean found = false;
                for (DistrictPlan.Curve later : pmo[m + 1].curves) {
                    if (later.kind == cv.kind && Arrays.equals(later.geom, cv.geom) && Arrays.equals(later.plots(), cv.plots()) && Arrays.equals(later.band(), cv.band())) { found = true; break; }
                }
                curvesStand &= found;
            }
        }
        StringBuilder perMonth = new StringBuilder();
        for (int m = 0; m <= months; m++) perMonth.append(' ').append(pmo[m].crescents).append('/').append(cellsOpened(pmo[m]));
        crescentsEver = pmo[months].crescents;
        out.printf("      (f) a log of %d months on the far fixture: crescents / open cells by month:%s; boxes moved from one month to the next %d%n", months, perMonth, movedBoxes);
        check("(f) the fixture: cells open month by month beyond CRESCENT_FROM and crescents among them (" + pmo[0].crescents + " at month 0, " + crescentsEver + " at month " + months + ")", pmo[months].crescents > pmo[0].crescents && crescentsEver > 0);
        check("(f) every box of month m stands at month m + 1, and every crescent of month m is the same curve at month m + 1 (the arc and its band, plot for plot), every check holding each month", movedBoxes == 0 && curvesStand && monthsHold);
        // ...and the ground under a crescent's band bought after the cell opened (THE GROUND BOUGHT): the plots stay free of buildings, bought or not.
        boolean[] hole = new boolean[DistrictPlan.AREA];
        int holes = 0;
        for (DistrictPlan.Curve cv : pmo[months].curves) {
            if (cv.kind != DistrictPlan.Curve.CRESCENT) continue;
            for (int q : cv.band()) if (pmo[months].kindAt(q) == DistrictPlan.NONE && !hole[q]) { hole[q] = true; holes++; }
        }
        final int boughtMonth = 3;
        DistrictPlan.Input dated = log.into(flat(types)), datedLattice = log.into(flat(types));
        for (DistrictPlan.Input d : new DistrictPlan.Input[] { dated, datedLattice }) {
            d.hubX = -FAR_CELLS * cell;
            d.hubY = DistrictPlan.FRAME / 2;
            d.boughtAt = new int[DistrictPlan.AREA];
            for (int q = 0; q < DistrictPlan.AREA; q++) if (hole[q]) d.boughtAt[q] = boughtMonth;
        }
        datedLattice.latticeOnly = true;
        DistrictPlan pdated = DistrictPlan.make(dated), plattice = DistrictPlan.make(datedLattice);
        int onHoles = boxesOn(pdated, hole), onHolesLattice = boxesOn(plattice, hole);
        boolean wholeArcs = true;
        for (int c = 0; c < DistrictPlan.CELLS; c++) if (pdated.cellLayout[c] == DistrictPlan.LAYOUT_CRESCENT) wholeArcs &= bulgeOf(pdated, c, arcs) != 0;
        out.printf("      (f) the %d plots of the crescents' bands (the final month's) bought in month %d, after the cells opened: %d crescents, %d boxes on the plots; with latticeOnly (no crescent, no band) %d boxes on them%n", holes, boughtMonth,
                pdated.crescents, onHoles, onHolesLattice);
        check("(f) ground bought under a crescent's band after its cell opened stays free of buildings: " + onHoles + " boxes on the " + holes + " plots, where the same ground without the band takes " + onHolesLattice + " (the arrivals after the purchase)",
                holes > 0 && pdated.crescents > 0 && wholeArcs && onHoles == 0 && onHolesLattice > 0 && figures(pdated, dated).holds(pdated, dated));

        // ---- (g) the counts in a real run
        if (x1Curves != null && playedCurves != null) {
            out.printf("      (g) his city x 1, summed over its districts: %,d crescents (%,d the ground would not lay whole), %,d streets along a cut drawn as curves, %,d curves%n", x1Curves[0], x1Curves[1], x1Curves[2], x1Curves[3]);
            out.printf("      (g) the played city (section 6's samples), summed: %,d crescents (%,d the ground would not lay whole), %,d streets along a cut drawn as curves, %,d curves%n", playedCurves[0], playedCurves[1], playedCurves[2], playedCurves[3]);
            check("(g) streets along a cut are drawn as curves in a real run (his city x 1 or the played city), every check holding with them (sections 5 and 6)", x1Curves[2] + playedCurves[2] > 0);
        }
        rimCurves(types);
    }

    /** The houses each month brings in (f): enough to open a homes cell or three a month beyond CRESCENT_FROM. */
    static final int HOUSES_A_MONTH_FAR = 3000;

    /** The homes cells a plan opened. */
    static int homesIn(DistrictPlan p) {
        int k = 0;
        for (int c = 0; c < DistrictPlan.CELLS; c++) if (p.cellKind[c] == DistrictPlan.HOMES) k++;
        return k;
    }

    /** The plots a mask holds. */
    static int countTrue(boolean[] mask) {
        int k = 0;
        for (boolean b : mask) if (b) k++;
        return k;
    }

    /** Section 14 (e): the test district cut by its river and the sea, the game's rules - each street along a cut a curve whose line starts and ends where its plots do, with corners no tighter than their radius, and no box on the plots its band reaches. */
    static void shoreCurves(BuildingVisual.Type[] types) {
        final int n = DistrictPlan.FRAME;
        DistrictPlan.Input cut = testDistrict(types, 0, false);
        DistrictPlan pc = DistrictPlan.make(cut);
        Figures fc = figures(pc, cut);
        int curves = 0, withBand = 0, bandPlots = 0, fullBends = 0, cutBends = 0, bends = 0;
        boolean ordered = true, roles = true, endsOn = true, near = true, corners = true, ownBand = true;
        double farthest = 0;
        for (DistrictPlan.Curve cv : pc.curves) {
            if (cv.kind != DistrictPlan.Curve.SHORE) continue;
            curves++;
            int[] pl = cv.plots();
            // Its plots in order: four-connected, the dead end's last plot first and the street it meets last, the plots between the join's own.
            boolean chain = pl.length >= 3;
            for (int i = 1; i < pl.length; i++) chain &= Math.abs(pl[i] % n - pl[i - 1] % n) + Math.abs(pl[i] / n - pl[i - 1] / n) == 1;
            ordered &= chain;
            boolean own = true;
            for (int i = 1; i + 1 < pl.length; i++) own &= pc.roleAt(pl[i]) == DistrictPlan.ROLE_SHORE;
            roles &= own && pc.roleAt(pl[0]) != DistrictPlan.ROLE_SHORE && pc.kindAt(pl[0]) != DistrictPlan.NONE && pc.kindAt(pl[pl.length - 1]) != DistrictPlan.NONE;
            // Its line, as the painter will draw it: from the middle of the first plot to the middle of the last.
            double[] vx = new double[cv.geom.length / 2], vy = new double[cv.geom.length / 2];
            for (int i = 0; i < vx.length; i++) { vx[i] = cv.geom[2 * i]; vy[i] = cv.geom[2 * i + 1]; }
            double[][] line = StreetCurves.line(vx, vy);
            int last = line[0].length - 1;
            endsOn &= Math.abs(line[0][0] - (pl[0] % n + 0.5)) < 1e-9 && Math.abs(line[1][0] - (pl[0] / n + 0.5)) < 1e-9
                    && Math.abs(line[0][last] - (pl[pl.length - 1] % n + 0.5)) < 1e-9 && Math.abs(line[1][last] - (pl[pl.length - 1] / n + 0.5)) < 1e-9 && cv.radius == StreetCurves.CURVE_RADIUS;
            // ...within one plot of its own plots' middles.
            for (int i = 0; i <= last; i++) {
                double best = Double.MAX_VALUE;
                for (int q : pl) best = Math.min(best, Math.hypot(line[0][i] - (q % n + 0.5), line[1][i] - (q / n + 0.5)));
                farthest = Math.max(farthest, best);
                if (best > 1 + 1e-9) near = false;
            }
            // ...and every corner of its polyline rounded with no radius over the curve radius and no tangent over half of a leg beside it.
            for (TrackCurves.Bend b : TrackCurves.bends(vx, vy, cv.radius)) {
                bends++;
                double tangent = b.r() * Math.tan(Math.abs(b.sweep()) / 2);
                int k = -1;
                for (int i = 1; i + 1 < vx.length; i++) if (Math.abs(vx[i] - b.x()) < 1e-9 && Math.abs(vy[i] - b.y()) < 1e-9) k = i;
                double legs = k < 0 ? 0 : Math.min(Math.hypot(vx[k] - vx[k - 1], vy[k] - vy[k - 1]), Math.hypot(vx[k + 1] - vx[k], vy[k + 1] - vy[k]));
                corners &= k > 0 && b.r() <= cv.radius + 1e-9 && b.t() <= legs / 2 + 1e-9 && Math.abs(tangent - b.t()) < 1e-9;
                if (Math.abs(b.r() - cv.radius) < 1e-9) fullBends++; else cutBends++;
            }
            int[] band = cv.band();
            bandPlots += band.length;
            if (band.length > 0) withBand++;
            java.util.Set<Integer> own0 = new java.util.HashSet<>();
            for (int q : pl) own0.add(q);
            for (int q : band) ownBand &= !own0.contains(q);
        }
        boolean[] mask = bandMask(pc);
        int onBand = boxesOn(pc, mask);
        // The premise from both sides: with latticeOnly the same ground lays no curve and keeps no plot free.
        DistrictPlan.Input flatCut = testDistrict(types, 0, false);
        flatCut.latticeOnly = true;
        DistrictPlan plain = DistrictPlan.make(flatCut);
        out.printf("      (e) the test district cut by its river and the sea: %d dead ends joined along a cut, %d drawn as curves (%d reaching ground beyond their own plots, %d such plots), %d corners rounded (%d with the full radius %.0f, %d cut to fit a short leg),"
                + " the line at most %.2f plots from its plots' middles; %d boxes on a band; with latticeOnly %d curves; %s%n", pc.shoreJoins, curves, withBand, bandPlots, bends, fullBends, StreetCurves.CURVE_RADIUS, cutBends, farthest, onBand,
                plain.curves.size(), line(pc, fc));
        check("(e) the fixture: the ground cuts cells and dead ends are joined along the cut (" + pc.shoreJoins + "), most of them drawn as curves (" + curves + ")", pc.shoreJoins > 0 && curves > 0 && pc.shoreCurves == curves && curves <= pc.shoreJoins);
        check("(e) each curve's plots run from the dead end's last plot to the street it meets, four-connected, the plots between the cut's join (ROLE_SHORE)", ordered && roles);
        check("(e) ...its line starts at the dead end's plot middle and ends at the met street's, and stays within one plot of its own plots' middles (at most " + String.format("%.2f", farthest) + ")", endsOn && near);
        check("(e) ...turns at no corner tighter than its radius allows: no corner over CURVE_RADIUS, none whose tangent is over half of a leg beside it (" + bends + " corners, " + fullBends + " with the full radius, " + cutBends + " cut)", corners && bends > 0 && fullBends > 0);
        check("(e) ...the plots its band reaches beyond its own are kept free (" + bandPlots + " plots of " + withBand + " curves), no box standing on one, and with latticeOnly the same ground keeps none and lays none ("
                + plain.curves.size() + " curves)", ownBand && withBand > 0 && onBand == 0 && plain.curves.isEmpty() && plain.shoreJoins > 0);
        check("(e) every check holds on the cut ground with its curves, and no building lacks a place that the same ground without them places (" + pc.overflowCount() + " against " + plain.overflowCount() + ")", fc.holds(pc, cut) && pc.overflowCount() <= plain.overflowCount());
    }

    /** Section 14 (g), the rim of the dense city: the plans of districts far from its founding site, where crescents are laid in a real city (the screen and the samples of sections 5 and 6 lie within CRESCENT_FROM cells of it). */
    static void rimCurves(BuildingVisual.Type[] types) {
        denseScreen(types);
        CityMap dense = denseCity;
        long[] cv = new long[4];
        int planned = 0, rim = 0, apart = 0, onBand = 0;
        boolean holds = true;
        long ms = 0;
        for (CityMap.District d : dense.districts()) {
            if (Math.max(Math.abs(d.dx), Math.abs(d.dy)) < RIM_FROM) continue;
            rim++;
            if (rim % RIM_EVERY != 0) continue;
            long t0 = System.nanoTime();
            DistrictPlan.Input in = dense.planInput(d);
            DistrictPlan p = DistrictPlan.make(in);
            ms += (System.nanoTime() - t0) / 1_000_000;
            planned++;
            Figures fg = figures(p, in);
            holds &= holdsApartFromNetwork(fg, p, in);
            if (fg.pieces > 1 && !fg.groundParts) apart++;
            onBand += boxesOn(p, bandMask(p));
            cv[0] += p.crescents;
            cv[1] += p.crescentsOff;
            cv[2] += p.shoreCurves;
            cv[3] += p.curves.size();
        }
        out.printf("      (g) the dense city (x %,.0f): its %,d districts at least %d districts from the site, every %dth planned (%d): %,d crescents (%,d the ground would not lay whole), %,d streets along a cut drawn as curves, %,d curves, %d boxes on a band, %d districts of several pieces planned alone, %d ms in all%n",
                MapCheck.TIMES[MapCheck.DENSE], rim, RIM_FROM, RIM_EVERY, planned, cv[0], cv[1], cv[2], cv[3], onBand, apart, ms);
        check("(g) crescents are laid at the rim of a real city, every check but the one network holding on the " + planned + " districts planned (alone they are often several pieces, as at 0.8.18), no box on a band", planned > 0 && cv[0] > 0 && holds && onBand == 0);
    }

    /** The rim of the dense city (rimCurves()): districts this many or more from the one the founding site lies in, by the larger of their columns and rows. */
    static final int RIM_FROM = 3;

    /** ...and of those, one in this many is planned: 37, a stride that takes districts all round it. */
    static final int RIM_EVERY = 37;

    static void packing(BuildingVisual.Type[] types) {
        denseScreen(types);
        Tally todays = new Tally(), arrival = new Tally();
        long plotsToday = 0, plotsArrival = 0, placedToday = 0, placedArrival = 0, missedToday = 0, missedArrival = 0;
        List<Double> msToday = new ArrayList<>(), msArrival = new ArrayList<>();
        int built = 0, builds = 0;
        for (CityMap.District d : denseShown) {
            DistrictPlan.Input in0 = denseCity.planInput(d);
            DistrictPlan p0 = DistrictPlan.make(in0);
            DistrictPlan.Input in1 = denseCity.planInput(d);
            dealt(in1, d, BATCHES).attach(in1);
            DistrictPlan p1 = DistrictPlan.make(in1);
            todays.add(p0, in0, "district (" + d.dx + ", " + d.dy + ")");
            arrival.add(p1, in1, "district (" + d.dx + ", " + d.dy + ")");
            plotsToday += boxPlots(p0);
            plotsArrival += boxPlots(p1);
            placedToday += p0.buildings;
            placedArrival += p1.buildings;
            missedToday += p0.overflowCount();
            missedArrival += p1.overflowCount();
            builds += p1.builds;
            built++;
            double[] a = new double[TIMED], b = new double[TIMED];
            for (int r = 0; r < TIMED; r++) {
                long t0 = System.nanoTime();
                DistrictPlan.make(in0);
                a[r] = (System.nanoTime() - t0) / 1e6;
                long t1 = System.nanoTime();
                DistrictPlan.make(in1);
                b[r] = (System.nanoTime() - t1) / 1e6;
            }
            Arrays.sort(a);
            Arrays.sort(b);
            msToday.add(a[TIMED / 2]);
            msArrival.add(b[TIMED / 2]);
        }
        out.printf("      the dense screen's %d districts, their counts dealt in %d batches: %,d plots of boxes against %,d today (%.2f%%); %,d buildings placed against %,d (%,d without a place against %,d);"
                        + " %.1f builds a district%n", built, BATCHES, plotsArrival, plotsToday, 100.0 * plotsArrival / plotsToday, placedArrival, placedToday, missedArrival, missedToday, builds / (double) built);
        out.printf("      ...the slowest district's plan %.2f ms against %.2f ms today (the median of %d after one untimed)%n", slowest(msArrival), slowest(msToday), TIMED);
        check("the order of arrival covers no less than " + (int) (100 * COVER_LEAST) + "% of the ground today's plans cover on the dense screen's districts dealt in " + BATCHES + " batches",
                plotsArrival >= COVER_LEAST * plotsToday);
        check("...every check holding on each of its plans", arrival.allHold());
        check("...each planned in no more than " + (int) PLAN_MS + " ms", Timing.within(slowest(msArrival), PLAN_MS));
    }

    /* =====================================================================
       9. A SAVE NAMED ON THE COMMAND LINE: EVERY DISTRICT
       ===================================================================== */

    /** A save folder's autosave, loaded from a copy (a game run autosaves into any city it loads): every district planned, checked and timed. */
    static void save(Path dir) throws Exception {
        out.println("\n--- 9. the save in " + dir + ": every district ---");
        Path root = TempDirs.create("plancheck-save");
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
        // CL2: the layouts on and off - a second plan of every district with combOnly (0.8.11's comb) - so a save names its own figures.
        long deadOn = 0, deadOff = 0, placedOn = 0, placedOff = 0, lostOn = 0, lostOff = 0;
        for (CityMap.District d : m.districts()) {
            DistrictPlan on = plans.get(d);
            DistrictPlan.Input comb = m.planInput(d);
            comb.combOnly = true;
            DistrictPlan off = DistrictPlan.make(comb);
            deadOn += deadEnds(on, false);
            deadOff += deadEnds(off, false);
            placedOn += on.buildings;
            placedOff += off.buildings;
            lostOn += on.overflowCount();
            lostOff += off.overflowCount();
        }
        out.printf("      ...with the layouts on and with combOnly (0.8.11's comb): %,d against %,d dead ends; %,d against %,d buildings placed (%,d against %,d without a place)%n",
                deadOn, deadOff, placedOn, placedOff, lostOn, lostOff);
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
        check("...each planned in no more than " + (int) PLAN_MS + " ms", Timing.within(Tally.max(t.planMs), PLAN_MS));
    }

    /* =====================================================================
       15. THE RUNS DATED (batch FX7)
       ===================================================================== */

    /**
     * The runs of a fixture, dated (FX7): a record {month, plot, before, after} for each plot whose kind of fixed ground a
     * month changes, in the order laid - what CityMap's journal hands a plan as Input.fixedLog, written down by hand.
     */
    static final class Runs {
        final List<int[]> rec = new ArrayList<>();

        /** A w x h rectangle from plot (x, y) laid as `kind` at `month`, on ground that held no run. */
        Runs lay(int month, int x, int y, int w, int h, byte kind) {
            for (int j = 0; j < h; j++) for (int i = 0; i < w; i++) rec.add(new int[] { month, (y + j) * DistrictPlan.FRAME + x + i, 0, kind });
            return this;
        }

        /** ...and the same rectangle taken away again at `month`. */
        Runs lift(int month, int x, int y, int w, int h, byte kind) {
            for (int j = 0; j < h; j++) for (int i = 0; i < w; i++) rec.add(new int[] { month, (y + j) * DistrictPlan.FRAME + x + i, kind, 0 });
            return this;
        }

        /** `in` given the runs as they stand after the records of the months up to `through` as its fixed ground, and with `dated` those records as its Input.fixedLog. */
        DistrictPlan.Input into(DistrictPlan.Input in, int through, boolean dated) {
            List<int[]> r = new ArrayList<>();
            for (int[] x : rec) if (x[0] <= through) r.add(x);
            r.sort((a, b) -> Integer.compare(a[0], b[0]));
            Arrays.fill(in.fixed, (byte) 0);
            int[] month = new int[r.size()], log = new int[r.size()];
            for (int k = 0; k < r.size(); k++) {
                int[] x = r.get(k);
                in.fixed[x[1]] = (byte) x[3];
                month[k] = x[0];
                log[k] = x[1] << 8 | x[2] << 4 | x[3];
            }
            in.fixedLogMonth = dated && !r.isEmpty() ? month : null;
            in.fixedLog = dated && !r.isEmpty() ? log : null;
            return in;
        }
    }

    /** The houses each month of the runs' fixtures brings: with the shops, works and farms beside them, a district that grows by some hundreds of boxes a month. */
    static final int RUN_HOUSES = 250;

    /** A log of a first batch in month 0 and `months` months of arrivals after it. */
    static Log runsLog(int house, int shop, int works, int farm, int paved, int months) {
        Log log = new Log();
        log.add(0, house, 150).add(0, shop, 20).add(0, works, 2).add(0, paved, 3).batch();
        for (int m = 1; m <= months; m++) log.add(m, house, RUN_HOUSES).add(m, shop, 15).add(m, works, 1 + m % 2).add(m, paved, 2).add(m, farm, 1);
        return log;
    }

    /** Whether box i of plan p has a plot in the w x h rectangle from (x, y). */
    static boolean boxIn(DistrictPlan p, int i, int x, int y, int w, int h) {
        return p.bx[i] < x + w && p.bx[i] + p.bw[i] > x && p.by[i] < y + h && p.by[i] + p.bh[i] > y;
    }

    /** How many of plan p's boxes have a plot in the rectangle; with `old`, only those that plan does not have (the arrivals since it). */
    static int boxesIn(DistrictPlan p, DistrictPlan old, int x, int y, int w, int h) {
        java.util.Set<Long> had = old == null ? new java.util.HashSet<>() : boxes(old);
        int n = 0;
        for (int i = 0; i < p.buildings; i++) if (boxIn(p, i, x, y, w, h) && !had.contains(boxKey(p, i))) n++;
        return n;
    }

    static void runsDated(BuildingVisual.Type[] types) {
        out.println("\n--- 15. the runs dated: a run laid across built ground, or lifted, moves none of its buildings (batch FX7) ---");
        final int house = pick(types, 1, t -> t.category() == BuildingType.RESIDENTIAL && !t.outer() && t.site() == null);
        final int shop = pick(types, 1, t -> t.category() == BuildingType.COMMERCIAL && !t.outer() && t.site() == null);
        final int works = pick(types, 9, t -> t.cls() == BuildingVisual.INDUSTRY && !t.outer() && t.site() == null && t.campusGroup() == BuildingVisual.NO_GROUP);
        final int farm = pick(types, 1, t -> t.category() == BuildingType.AGRICULTURE && t.outer() && t.site() == null);
        final int paved = pavedRoad(types);
        final byte yard = DistrictPlan.FIXED_YARD, highway = DistrictPlan.FIXED_HIGHWAY;
        final int yw = 8, yh = 7, hw = 40;
        final int side = DistrictPlan.SIDE;
        // The runs are laid in month 3, between the second month's arrivals and the third's; the log goes on to month 6.
        final int laid = 3, last = 6;
        Log log = runsLog(house, shop, works, farm, paved, last);
        DistrictPlan before = DistrictPlan.make(log.until(laid - 1).into(flat(types)));
        out.printf("      the district to month %d: %,d buildings placed in %d cells, %d without a place%n", laid - 1, before.buildings, cellsOpened(before), before.overflowCount());

        // ---- (a) a yard laid across built ground
        int pickA = before.buildings / 3;
        int yx = Math.max(2, Math.min(before.bx[pickA], side - 1 - yw)), yy = Math.max(2, Math.min(before.by[pickA], side - 1 - yh));
        Runs yardRuns = new Runs().lay(laid, yx, yy, yw, yh, yard);
        int under = boxesIn(before, null, yx, yy, yw, yh);
        DistrictPlan.Input datedIn = yardRuns.into(log.into(flat(types)), Integer.MAX_VALUE, true);
        DistrictPlan after = DistrictPlan.make(datedIn);
        DistrictPlan moved = DistrictPlan.make(yardRuns.into(log.into(flat(types)), Integer.MAX_VALUE, false));
        out.printf("      (a) a yard of %d x %d plots laid in month %d at (%d, %d), across %d of the %,d boxes standing: with the month it was laid %,d placed, %d of month %d's boxes moved,"
                + " %d box(es) under the yard and %d arrived on it; with no dates, the yard there from the start: %,d placed, %d of them moved%n", yw, yh, laid, yx, yy, under, before.buildings,
                after.buildings, missing(before, after), laid - 1, boxesIn(after, null, yx, yy, yw, yh), boxesIn(after, before, yx, yy, yw, yh), moved.buildings, missing(before, moved));
        check("(a) the fixture: the yard lies across boxes the plan of the months before it has built, and the months after it bring buildings", under >= 3 && after.buildings > before.buildings);
        check("(a) a yard laid across built ground moves none of the boxes standing: each of month " + (laid - 1) + "'s " + before.buildings + " stands in the plan of the log through month " + last,
                missing(before, after) == 0);
        check("(a) ...the boxes under the yard stay where they are", boxesIn(after, null, yx, yy, yw, yh) >= under);
        check("(b) the arrivals after the yard stand on none of its plots: a free plot a run is laid on is barred", boxesIn(after, before, yx, yy, yw, yh) == 0);
        check("(a) where the same yard is there from the start, with no dates, the plan moves boxes (the fault this section stands against)", missing(before, moved) > 0);

        // ---- (c) a yard laid where a later arrival would stand, and lifted again before it comes
        final int lifted = laid + 2;
        DistrictPlan control = DistrictPlan.make(log.into(flat(types)));
        DistrictPlan until = DistrictPlan.make(log.until(lifted - 1).into(flat(types)));
        java.util.Set<Long> standingBefore = boxes(until);
        int[] spot = null;
        // The yard is laid round the first house that comes after the lift in the plan with no runs, where nothing stands before it.
        for (int i = 0; i < control.buildings && spot == null; i++) {
            if (control.btype[i] != house || standingBefore.contains(boxKey(control, i))) continue;
            int[] round = { control.bx[i] - 3, control.by[i] - 3, control.bw[i] + 6, control.bh[i] + 6 };
            if (boxesIn(until, null, round[0], round[1], round[2], round[3]) == 0) spot = round;
        }
        check("(c) the fixture: a house comes after month " + (lifted - 1) + " in the plan with no runs, on ground nothing stands on before it", spot != null);
        if (spot != null) {
            Runs comeAndGo = new Runs().lay(laid, spot[0], spot[1], spot[2], spot[3], yard).lift(lifted, spot[0], spot[1], spot[2], spot[3], yard);
            DistrictPlan standing = DistrictPlan.make(comeAndGo.into(log.until(lifted - 1).into(flat(types)), lifted - 1, true));
            DistrictPlan.Input goneIn = comeAndGo.into(log.into(flat(types)), Integer.MAX_VALUE, true);
            DistrictPlan gone = DistrictPlan.make(goneIn);
            out.printf("      (c) a yard of %d x %d plots at (%d, %d) laid in month %d and lifted in month %d: with it standing (month %d) %,d placed, %d of them on it; with it gone"
                    + " (month %d) %,d placed, %d moved, %d of the arrivals since on the ground it left%n", spot[2], spot[3], spot[0], spot[1], laid, lifted, lifted - 1, standing.buildings,
                    boxesIn(standing, null, spot[0], spot[1], spot[2], spot[3]), last, gone.buildings, missing(standing, gone), boxesIn(gone, standing, spot[0], spot[1], spot[2], spot[3]));
            check("(c) a yard lifted moves none of the boxes standing: every box of the plan with it standing stands in the plan with it gone", missing(standing, gone) == 0
                    && gone.buildings > standing.buildings);
            check("(c) ...the arrivals while it stood are on none of its ground", boxesIn(standing, before, spot[0], spot[1], spot[2], spot[3]) == 0);
            check("(c) ...and the ground it left is room again: some of the arrivals after it stand on it", boxesIn(gone, standing, spot[0], spot[1], spot[2], spot[3]) > 0);
            check("(c) a plan is a pure function of its input: the plan with the yard come and gone, made twice, is the same plan box for box and street for street", samePlan(gone, DistrictPlan.make(goneIn)));
        }

        // ---- (d) a highway laid beside built ground: its verge bars the plots beside it
        int pickD = before.buildings * 2 / 3;
        int hx = Math.max(2, Math.min(before.bx[pickD] - 10, side - 1 - hw)), hy = Math.max(2, Math.min(before.by[pickD], side - 2));
        Runs highwayRuns = new Runs().lay(laid, hx, hy, hw, 1, highway);
        int beside = boxesIn(before, null, hx - 1, hy - 1, hw + 2, 3);
        DistrictPlan.Input highwayIn = highwayRuns.into(log.into(flat(types)), Integer.MAX_VALUE, true);
        DistrictPlan highwayAfter = DistrictPlan.make(highwayIn);
        DistrictPlan highwayMoved = DistrictPlan.make(highwayRuns.into(log.into(flat(types)), Integer.MAX_VALUE, false));
        out.printf("      (d) a highway of %d plots along row %d from column %d laid in month %d, %d of the %,d boxes on it or beside it: with the month it was laid %,d placed, %d moved, %d arrived beside it;"
                + " with no dates %,d placed, %d moved%n", hw, hy, hx, laid, beside, before.buildings, highwayAfter.buildings, missing(before, highwayAfter),
                boxesIn(highwayAfter, before, hx - 1, hy - 1, hw + 2, 3), highwayMoved.buildings, missing(before, highwayMoved));
        check("(d) the fixture: boxes stand on the highway's row or in its verge, and the months after it bring buildings", beside > 0 && highwayAfter.buildings > before.buildings);
        check("(d) a highway laid beside built ground moves none of the boxes standing, those on it and in its verge included", missing(before, highwayAfter) == 0);
        check("(d) ...the arrivals after it stand on none of its plots nor beside it: its verge (H5) bars the plots about it", boxesIn(highwayAfter, before, hx - 1, hy - 1, hw + 2, 3) == 0);
        check("(d) where the same highway is there from the start, with no dates, the plan moves boxes", missing(before, highwayMoved) > 0);

        // ---- (e) records at or before the first batch's month change nothing
        Log early = log.copy();
        early.firstBatch = 0;
        for (int[] r : early.rec) if (r[0] <= 2) early.firstBatch++;
        Runs upToFirst = new Runs().lay(1, yx, yy, yw, yh, yard).lay(2, 1, side - 10, side - 1, 1, highway);
        DistrictPlan withRecords = DistrictPlan.make(upToFirst.into(early.into(flat(types)), Integer.MAX_VALUE, true));
        DistrictPlan withoutRecords = DistrictPlan.make(upToFirst.into(early.into(flat(types)), Integer.MAX_VALUE, false));
        check("(e) records dated at or before the month the first batch stands on are the ground from the start: the plan is the plan with no records, box for box and street for street",
                samePlan(withRecords, withoutRecords) && withRecords.buildings == withoutRecords.buildings && withRecords.buildings > 0);

        // ---- (f) a run laid after the log's last month, and after the first batch of a log that is one batch
        final int rx = 4 * DistrictPlan.CELL + 3;
        Runs railway = new Runs().lay(last + 3, rx, 1, 1, side - 1, DistrictPlan.FIXED_RAIL);
        DistrictPlan stood = DistrictPlan.make(log.into(flat(types)));
        DistrictPlan laterRun = DistrictPlan.make(railway.into(log.into(flat(types)), Integer.MAX_VALUE, true));
        Log one = new Log();
        one.add(0, house, 150).add(0, shop, 20).add(0, works, 2).add(0, paved, 3).batch();
        DistrictPlan oneBefore = DistrictPlan.make(one.into(flat(types)));
        DistrictPlan oneAfter = DistrictPlan.make(railway.into(one.into(flat(types)), Integer.MAX_VALUE, true));
        int crossings = crossingsOn(laterRun, rx), crossingsOne = crossingsOn(oneAfter, rx);
        out.printf("      (f) a railway down column %d laid in month %d, after the log's last month (%d): %,d boxes stand, %d moved, %d of its plots a street crosses (CROSSING); on a log of a first batch alone:"
                + " %,d boxes, %d moved, %d crossings%n", rx, last + 3, last, laterRun.buildings, missing(stood, laterRun), crossings, oneAfter.buildings, missing(oneBefore, oneAfter), crossingsOne);
        check("(f) a run laid after the log's last month moves nothing, and the plan is drawn on today's runs: its streets cross the railway",
                missing(stood, laterRun) == 0 && laterRun.buildings == stood.buildings && crossings > 0);
        check("(f) ...so does one laid after the first batch of a log that is one batch and no more", missing(oneBefore, oneAfter) == 0 && oneAfter.buildings == oneBefore.buildings && crossingsOne > 0);

        // ---- (g) a pure function
        check("(g) a plan is a pure function of its input: the plans with the yard and with the highway laid, each made twice, are the same plan box for box and street for street",
                samePlan(after, DistrictPlan.make(datedIn)) && samePlan(highwayAfter, DistrictPlan.make(highwayIn)));
    }

    /** How many plots of column x a plan's streets cross a railway on (the surface's CROSSING flag). */
    static int crossingsOn(DistrictPlan p, int x) {
        int n = 0;
        for (int y = 0; y < DistrictPlan.FRAME; y++) {
            int q = y * DistrictPlan.FRAME + x;
            if (p.kindAt(q) != DistrictPlan.NONE && (p.street[q] & DistrictPlan.CROSSING) != 0) n++;
        }
        return n;
    }

    /* ----- 16. the far bank (FW3) ----- */

    /** A river too wide for any bridge across a root district south of its hub: its streets' road counted from the hub's side. */
    static DistrictPlan.Input farBankInput(BuildingVisual.Type[] types, double gravel) {
        DistrictPlan.Input in = flatMix(types, FULL_FILL, true, false);
        in.root = true;
        int n = DistrictPlan.FRAME, top = 3 * DistrictPlan.FRAME / 4, wide = DistrictPlan.ARTERIAL_BRIDGE + 2;
        for (int y = top; y < top + wide; y++) for (int x = 0; x < n; x++) in.terrain[y * n + x] = World.FRESH;
        in.paved = in.highway = 0;
        in.gravel = gravel;
        return in;
    }

    /** The surfaced width over a plan's surfaced streets (kind TRACK to HIGHWAY), [network, parted], and their plot counts. */
    static double[] farBankWidths(DistrictPlan p) {
        double[] r = new double[4];
        for (int q = 0; q < DistrictPlan.AREA; q++) {
            int code = p.street[q], kind = code & DistrictPlan.KIND_MASK;
            if (kind < DistrictPlan.TRACK || kind > DistrictPlan.HIGHWAY) continue;
            int side = p.parted[q] ? 1 : 0;
            r[side] += DistrictPlan.HALF * ((code >> DistrictPlan.WIDTH_SHIFT) & 3);
            r[2 + side]++;
        }
        return r;
    }

    static void farBank(BuildingVisual.Type[] types) {
        out.println("\n--- 16. the far bank: a district cut by a river no bridge crosses surfaces its hub's network before the streets beyond it (FW3) ---");
        // The streets with no road, to count them: the network's plots and the parted plots beyond the river.
        double[] zero = farBankWidths(DistrictPlan.make(farBankInput(types, 0)));
        double netCap = DistrictPlan.FULL * zero[2], partCap = DistrictPlan.FULL * zero[3];
        out.printf("      a river %d plots wide at row %d: %.0f network street plots, %.0f parted beyond it%n",
                DistrictPlan.ARTERIAL_BRIDGE + 2, 3 * DistrictPlan.FRAME / 4, zero[2], zero[3]);
        check("the fixture parts the district: streets on both banks, the far bank's not on the network", zero[2] > 0 && zero[3] > 0);
        // (a) gravel only, three quarters of what the network holds at full width: all of it on the network, none beyond.
        double ba = 0.75 * netCap;
        DistrictPlan pa = DistrictPlan.make(farBankInput(types, ba));
        double[] wa = farBankWidths(pa);
        out.printf("      (a) %.1f plots of gravel: %.1f on the network, %.1f on the far bank%n", ba, wa[0], wa[1]);
        check("(a) road short of the network's full width goes on the network alone: the far bank is dealt none", wa[1] == 0 && Math.abs(wa[0] - ba) <= DistrictPlan.HALF);
        // (b) the network at full width and half the far bank's: the far bank dealt only what the network leaves.
        double bb = netCap + 0.5 * partCap;
        DistrictPlan pb = DistrictPlan.make(farBankInput(types, bb));
        double[] wb = farBankWidths(pb);
        out.printf("      (b) %.1f plots of gravel: %.1f on the network (%.0f at full width), %.1f on the far bank%n", bb, wb[0], netCap, wb[1]);
        check("(b) the network takes its full width first, and the far bank only what is left (" + (bb - netCap) + ")",
                Math.abs(wb[0] - netCap) < 1e-9 && Math.abs(wb[1] - (bb - netCap)) <= DistrictPlan.HALF && wb[2] == zero[2] && wb[3] == zero[3]);
    }

    /* ----- 17. a street piece buildings wall in (FW11) ----- */

    /** The districts of this harness's cities (sections 5 and 6), those planned again to join a piece the buildings wall in, the plots so kept clear, and the districts still left with a piece the ground does not part (FW11). */
    static int citiesDistricts, citiesWalled, citiesKept, citiesLeft;

    /** A section's tally of one city added to the cities' (FW11). */
    static void count(Tally t) {
        citiesDistricts += t.districts;
        citiesWalled += t.walled;
        citiesKept += t.kept;
        citiesLeft += t.districts - t.network - t.apart;
    }

    /**
     * Every district of a map planned as the map draws it, each from its job's input, and tallied: those planned again kept in
     * walledIns, ok[0] cleared where a plan is not the same plan made twice (boxes, streets, ways kept clear), ok[1] where one is
     * planned more than WALL_TRIES times or has plots kept clear without being planned again (FR8: the played city's loop, for the
     * river fixture's districts too).
     */
    static void planDrawn(CityMap map, Tally t, List<DistrictPlan.Input> walledIns, boolean[] ok) {
        map.forgetPainted();
        for (CityMap.District d : map.districts()) {
            for (CityMap.Job j : map.planJobs(d)) {
                j.run();
                map.adopt(j);
                if (!(j instanceof CityMap.PlanJob pj)) continue;
                DistrictPlan p = DistrictPlan.make(pj.in);
                t.add(p, pj.in, "district (" + pj.d.dx + ", " + pj.d.dy + ")");
                if (p.wallTries > 0) walledIns.add(pj.in);
                DistrictPlan again = DistrictPlan.make(pj.in);
                ok[0] &= placedDigest(p) == placedDigest(again) && streetDigest(p) == streetDigest(again) && p.wallTries == again.wallTries && p.wallClear == again.wallClear;
                ok[1] &= p.wallTries <= DistrictPlan.WALL_TRIES && (p.wallTries == 0) == (p.wallClear == 0);
            }
        }
    }

    static void walledIn(BuildingVisual.Type[] catalog) throws Exception {
        out.println("\n--- 17. no street piece walled in by buildings: MapCheck 1's city, every district as the map draws it, the carry from the districts before it (FW11) ---");
        Path root = TempDirs.create("plancheck-walled");
        PrintStream was = MapCheck.out;
        MapCheck.out = QUIET;
        Game g;
        try {
            g = MapCheck.playedCity(root);
        } finally {
            MapCheck.out = was;
        }
        CityMap map = g.getCityMap();
        // The plans the map draws, each from its job's input - the ground, what the districts before it carried to it and what it reads of
        // them - and not from planInput(d), which has no carry: the case stood in the drawn plan of the first district, where the
        // second's carry was not yet.
        Tally t = new Tally();
        boolean[] ok = { true, true };
        List<DistrictPlan.Input> walledIns = new ArrayList<>();
        planDrawn(map, t, walledIns, ok);
        t.report("MapCheck 1's city, month " + g.getMonth());
        int left = t.districts - t.network - t.apart;
        out.printf("      the case (a street piece only buildings, or buildings and ground the city does not own, enclose): in MapCheck's city %d of %d district(s) -"
                + " planned again with the way kept clear in %d (%d plots), %d left (before the fix %d of %d)%n", t.walled + left, t.districts, t.walled, t.kept, left,
                t.walled + left, t.districts);
        out.printf("      ...in this harness's cities (sections 5 and 6): %d of %d district(s) - planned again in %d (%d plots), %d left%n",
                citiesWalled + citiesLeft, citiesDistricts, citiesWalled, citiesKept, citiesLeft);
        // The played city has the case only if its path leaves it (FR8's grows another city, and its districts have none): the river
        // fixture is a city of the default world's own river and his x 1 counts, whatever the played city's path, and one of its
        // districts stands with a street piece the river parts and the buildings enclose. Its districts are planned the same way and
        // join the played city's in every count and check below.
        long[] x1 = new long[catalog.length];
        for (int k = 0; k < MapCheck.JERUS_COUNTS.length && k < x1.length; k++) x1[k] = Math.round(MapCheck.JERUS_COUNTS[k] * MapCheck.TIMES[0]);
        int playedWalled = t.walled, playedDistricts = t.districts;
        planDrawn(MapCheck.riverCity(catalog, Founding.DEFAULT_WORLD_SEED, x1, BuildingVisual.GRAVEL), t, walledIns, ok);
        left = t.districts - t.network - t.apart;
        out.printf("      ...and MapCheck's river fixture (his x 1 at the founding river, his streets all gravel), %d district(s): planned again with the way kept clear"
                + " in %d, where the played city's %d of %d were; %d left in all%n", t.districts - playedDistricts, t.walled - playedWalled,
                playedWalled, playedDistricts, left);
        boolean pure = ok[0], bounded = ok[1];
        check("fixture: a district of the played city stood with a street piece the buildings enclose (planned again with the way kept clear in " + t.walled + " of " + t.districts + ")", t.walled > 0);
        check("every district as the map draws it keeps every check: one network or pieces the ground parts, the + floor, reach, none on a street, the surface", t.allHold());
        check("...none left with a piece the ground does not part, in MapCheck's city or this harness's", left == 0 && citiesLeft == 0);
        check("...planned again at most " + DistrictPlan.WALL_TRIES + " times, and plots kept clear only in a district planned again", bounded);
        check("...and a pure function: the same district, the same plan, boxes and streets, the same ways kept clear", pure);
        // The same districts with no log - their counts and their carry, the first batch alone, no events: the join's way kept clear and
        // every building placed or sent on, once (placed and left out = counts + carry).
        Tally n = new Tally();
        boolean whole = true;
        for (DistrictPlan.Input in : walledIns) {
            int types = in.types.length;
            long want = 0;
            int[] carried = in.carry == null ? new int[types] : CityMap.countsOf(in.carry, types);
            for (int k = 0; k < types; k++) want += Math.max(0, k < in.counts.length ? in.counts[k] : 0) + carried[k];
            in.logMonth = null;
            in.logType = null;
            in.logChange = null;
            DistrictPlan p = DistrictPlan.make(in);
            n.add(p, in, "district with no log");
            whole &= p.buildings + p.overflowCount() == want;
        }
        out.printf("      ...with no log: %d district(s) of them, %d planned again, %d plots kept clear%n", n.districts, n.walled, n.kept);
        check("...the same districts planned with no log keep every check, and every building is placed or left out, once", n.allHold() && whole);
    }

}
