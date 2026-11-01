package ham.citybuildersim;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * The nations' world file's reader (batch WN0; the project's spec-nations.md
 * 3.3): WorldData's reading of world-1.bin held to tools/world/worldread.py
 * figure for figure, and the file's own promises held to the file: its sums
 * close, its freight table keeps the triangle rule, its base year is the
 * smooth equilibrium its prefs say, its borders own every cell of land.
 *
 * WHY. The nations' world puts a country's borders, trade, people, output,
 * money and distance in the game, and a wrong figure there is a city whose
 * neighbour trades with the wrong nation or pays the wrong price for a tonne.
 * The reader is checked against the Python that wrote the file and reads it
 * independently; the file against the rules FORMAT.txt states of it, which the
 * pipeline could break without the reader noticing.
 *
 * THE DATA. The full world-1.bin is not in the repository; it is the asset of
 * the GitHub release world-1, which tools/earth/get-earth.bat fetches into
 * data/ with "world-1" as its second argument (WorldData.DATA_FILE). This runs
 * on a fixture cut from it by tools/world/fixture.py - twelve nations
 * renumbered 1..12 and the Rotterdam window's owner tiles - a resource beside
 * the classes (world/world-1-fixture.bin), with what worldread.py read from it
 * and from the full file beside it (world/world-1-fixture.expect). The full
 * file's sections run when it is named, -Dcbs.world=<path> (WorldData.locate()),
 * or lies at data/world-1.bin, and say so in one line when it is not.
 *
 * What this has to prove:
 *   1. THE FILE: the fixture opens; its layers are FORMAT.txt's, in order; its
 *      SHA-256 and size are the expect file's; its meta's "of" names the full
 *      file's identity when the full file is present.
 *   2. THE READER IS worldread.py'S: every expect line of the scope (nation,
 *      path, rate, price, tile, cell, freight, flow, make, lines, line) equals
 *      what WorldData reads, exactly.
 *   3. EVERY UN MEMBER AND THE THREE: the 193 member states and TWN, HKG and
 *      MAC are the full file's nations, numbered in iso3 order; the fixture's
 *      are its twelve.
 *   4. THE WORLD'S SUMS CLOSE: for every good and nation, make is its row's
 *      flows and use its column's; the world's make is its use; every flow is
 *      above 0; every nation has its own pair.
 *   5. THE FREIGHT TABLE KEEPS THE TRIANGLE RULE: cost(a, c) is never more than
 *      cost(a, b) + cost(b, c), 0 on the diagonal, symmetric, every entry
 *      finite; the closure of the edges is the stored table.
 *   6. THE BASE YEAR REPRODUCES BACI'S FLOWS ON THE KEPT PAIRS: the kernel
 *      solved from a = b = 1 gives every pair's flow, and each pair's cost is
 *      the kernel's; on the fixture each BACI sum is the file's flow where the
 *      pair is kept.
 *   7. A NATION FOR EVERY LAND CELL INSIDE EVERY BORDER TILE: in every owner
 *      tile with a blob, every cell the earth file's landmass layer calls land
 *      has an owner.
 *   8. THE MONEYS AND PATHS ARE WHOLE: every nation's money has a rate in
 *      every month of FIRST_YEAR-01 (the stand-in century's) to 2025-12,
 *      people are known from FIRST_YEAR to 2100, output and the exchange rate
 *      to 2025, and a figure is NaN exactly where its mark is 0; each good's
 *      price path ends at 1 in 2025.
 *   9. THE STAND-IN CENTURY (EP1): the file begins in 2000 and the reader in
 *      1900. Before a series' first figure a real series holds it and the two
 *      nominal ones (cpi, gdp_usd) follow the US price level; a good's price
 *      and a money's rate hold theirs; a nation's money at month 0 is its
 *      2000-01 money; everything made is marked an estimate; the file's own
 *      years read as they did.
 *
 * Every fixture causes its condition: a fixture's twelve nations trade with one
 * another in every good, its edges are a whole freight table, and its window's
 * tiles are the coast of the Netherlands and Belgium.
 */
public class WorldDataCheck {

    /** The fixture's expected figures, beside the classes (the fixture itself is WorldData.FIXTURE). */
    static final String EXPECT = "world/world-1-fixture.expect";

    /** A fixture's meta name row, and the full file's. */
    static final String FIXTURE_NAME = "world-1-fixture", FULL_NAME = "world-1";

    /** A sum, a make or a use, agrees with the flows it is the sum of within this share of it (FORMAT.txt, trade: 1e-9). */
    static final double SUM_TOL = 1e-9;

    /** The triangle rule holds within this share of the dearer side (floating point adds the same route in a different order). */
    static final double TRIANGLE_SLACK = 1e-9;

    /** The freight table is symmetric, and is its edges' closure, within this share of the figure. */
    static final double TABLE_TOL = 1e-9;

    /** The solve's margins meet when no make or use is off by more than this share of it (FORMAT.txt, THE KERNEL: 1e-12). */
    static final double MARGIN_TOL = 1e-12;

    /** The solve gives the file's flow within this share of it (FORMAT.txt: 1e-9), plus MARGIN_ULPS of its margin over the flow (see there). */
    static final double FLOW_TOL = 1e-9;

    /**
     * The room a flow is given beyond FLOW_TOL, as this share of the smaller of its row's make and its column's use, over the flow: 4 ulps of the margin. A flow
     * under about 1e-6 of its margin is the difference of two margins that double precision holds to 1e-16 of the larger, so it comes back to a few ulps of the
     * margin, not to FLOW_TOL of itself (tools/world/econ_trade.py solve_exact proves the file with the same rule).
     */
    static final double MARGIN_ULPS = 1e-15;

    /** A pair's cost is elasticity x ln(1 + freight / unit value) within this share of it. */
    static final double COST_TOL = 1e-9;

    /** BACI's own sum for a pair is the file's flow within this share of it (the file's flow is BACI's, as the CSV is summed in a second pass). */
    static final double BACI_TOL = 1e-6;

    /** The most passes (every nation's a and b solved together, the others held) the solve may take for one good before it is called not to converge. */
    static final int SWEEP_CAP = 100_000;

    /** The solve may stop when no a or b moved by more than this share in a pass, and every margin is within MARGIN_TOL / TARGET_DIVISOR (econ_trade.py's rule). */
    static final double CHANGE_TOL = 1e-9;

    /** A margin must be within MARGIN_TOL over this before the solve may stop: the margin floor of double precision is far below MARGIN_TOL. */
    static final double TARGET_DIVISOR = 100;

    /** The solve stops at once when no a or b moved by more than this share in a pass: they have stopped moving, at the floor of double precision. */
    static final double CHANGE_FLOOR = 1e-15;

    /** ...or when this many passes after it may stop have moved them no less than the least so far: a nation whose own pair is nearly all of its make and use keeps a rounding wobble. */
    static final int STALE_PASSES = 8;

    /** The nations of the fixture, in iso3 order (FORMAT.txt, THE FIXTURE). */
    static final List<String> TWELVE = List.of("AUS", "BEL", "CAN", "CHE", "CHN", "COL", "DEU", "FRA", "GBR", "JPN", "NLD", "USA");

    /** The three the 193 UN members do not hold, that the full file's N = 196 adds: Taiwan, Hong Kong and Macau. */
    static final List<String> THE_THREE = List.of("TWN", "HKG", "MAC");

    /** How many member states the UN has (since South Sudan joined in 2011). */
    static final int UN_MEMBER_COUNT = 193;

    /** How many regions the World Bank's countries list sorts the nations in (FORMAT.txt, nations). */
    static final int WORLD_BANK_REGIONS = 7;

    /** How many nations the full file holds. */
    static final int FULL_NATIONS = UN_MEMBER_COUNT + THE_THREE.size();

    /**
     * The United Nations' 193 member states by ISO 3166 alpha-3 code, A to Z: the list on the UN's member states page
     * (https://www.un.org/en/about-us/member-states), written here and not read from the file, so that the file's list is
     * held to the UN's and not to itself.
     */
    static final String UN_MEMBERS =
            "AFG AGO ALB AND ARE ARG ARM ATG AUS AUT AZE "
          + "BDI BEL BEN BFA BGD BGR BHR BHS BIH BLR BLZ BOL BRA BRB BRN BTN BWA "
          + "CAF CAN CHE CHL CHN CIV CMR COD COG COL COM CPV CRI CUB CYP CZE "
          + "DEU DJI DMA DNK DOM DZA "
          + "ECU EGY ERI ESP EST ETH "
          + "FIN FJI FRA FSM "
          + "GAB GBR GEO GHA GIN GMB GNB GNQ GRC GRD GTM GUY "
          + "HND HRV HTI HUN "
          + "IDN IND IRL IRN IRQ ISL ISR ITA "
          + "JAM JOR JPN "
          + "KAZ KEN KGZ KHM KIR KNA KOR KWT "
          + "LAO LBN LBR LBY LCA LIE LKA LSO LTU LUX LVA "
          + "MAR MCO MDA MDG MDV MEX MHL MKD MLI MLT MMR MNE MNG MOZ MRT MUS MWI MYS "
          + "NAM NER NGA NIC NLD NOR NPL NRU NZL "
          + "OMN "
          + "PAK PAN PER PHL PLW PNG POL PRK PRT PRY "
          + "QAT "
          + "ROU RUS RWA "
          + "SAU SDN SEN SGP SLB SLE SLV SMR SOM SRB SSD STP SUR SVK SVN SWE SWZ SYC SYR "
          + "TCD TGO THA TJK TKM TLS TON TTO TUN TUR TUV TZA "
          + "UGA UKR URY USA UZB "
          + "VCT VEN VNM VUT "
          + "WSM "
          + "YEM "
          + "ZAF ZMB ZWE";

    /** The last month the rates and the figures run to: 2025-12. */
    static final int LAST_MONTH = WorldData.monthNumber(2025, 12);

    /** The file's own month 0, 2000-01, as WorldData numbers months from FIRST_YEAR: where the stand-in century ends and the file begins. */
    static final int FILE_MONTH_ZERO = WorldData.monthNumber(WorldData.FILE_FIRST_YEAR, 1);

    /** The people run to this year; the figures (output, the exchange rate) to FIGURES_LAST. */
    static final int PEOPLE_LAST = 2100, FIGURES_LAST = 2025;

    /** The series that have a figure for every nation to FIGURES_LAST. */
    static final List<String> FIGURE_SERIES = List.of("gdp_2015usd", "gdp_usd", "gdp_ppp", "rate");

    /** The dials the freight cost and the kernel read (FORMAT.txt, trade and freight). */
    static final List<String> FREIGHT_DIALS = List.of("SEA_USD_TKM", "LAND_USD_TKM", "HANDLING_USD_T", "LAND_PER_SEA");

    /** A day as the meta writes it. */
    static final Pattern DAY = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    static int fails = 0;
    /** Where the harness prints: the console. */
    static final PrintStream out = System.out;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-110s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. */
    static final class Tally {
        int n, bad;
        String first;

        void see(boolean ok, String what) {
            n++;
            if (!ok) {
                bad++;
                if (first == null) first = what;
            }
        }
    }

    static void tally(String label, Tally t) {
        boolean ok = t.n > 0 && t.bad == 0;
        if (!ok) fails++;
        out.printf("%-110s %s  (%d of %d agree)%s%n", label, ok ? "OK" : "FAIL", t.n - t.bad, t.n,
                t.bad > 0 ? "; the first that does not: " + t.first : t.n == 0 ? "; none read" : "");
    }

    /** One file read: the fixture, or the full file, with the expect file's lines of its scope by kind. */
    static final class Scope {
        final String name;
        final WorldData data;
        final Map<String, List<String[]>> lines = new HashMap<>();

        Scope(String name, WorldData data) {
            this.name = name;
            this.data = data;
        }

        String label(String s) { return "[" + name + "] " + s; }

        List<String[]> of(String kind) { return lines.getOrDefault(kind, List.of()); }
    }

    /** Judges one expect line: null where WorldData reads what it says, else what differs. */
    interface Judge {
        String judge(String[] f) throws Exception;
    }

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        byte[] fixtureBytes = resource(WorldData.FIXTURE);
        byte[] expectBytes = resource(EXPECT);
        if (fixtureBytes == null || expectBytes == null) {
            assertTrue("no fixture: " + WorldData.FIXTURE + " and " + EXPECT + " are not beside the classes (a build that copies only src/main/resources/*.json leaves world/ behind)", false);
            System.out.println("\n" + fails + " FAILED");
            System.exit(1);
        }

        Scope fixture = new Scope("fixture", WorldData.of(fixtureBytes));
        Path fullPath = WorldData.locate();
        String named = System.getProperty(WorldData.PROPERTY);
        WorldData opened = null;
        out.println("--- the full file, when it is named ---");
        if (named != null && !named.isBlank() && !Files.isRegularFile(fullPath)) {
            assertTrue("the file -D" + WorldData.PROPERTY + " names is there (" + named + ")", false);
        } else if (fullPath == null) {
            out.println("world-1.bin not named (-D" + WorldData.PROPERTY + "=<path>) nor at " + WorldData.DATA_FILE + ": its sections did not run");
        } else {
            out.println("reading " + fullPath.toAbsolutePath());
            opened = WorldData.open(fullPath);
        }
        try (WorldData fullData = opened) {
            Scope full = fullData == null ? null : new Scope("full", fullData);
            List<Scope> scopes = new ArrayList<>(List.of(fixture));
            if (full != null) scopes.add(full);
            for (String line : new String(expectBytes, StandardCharsets.UTF_8).split("\n")) {
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] f = (line.endsWith("\r") ? line.substring(0, line.length() - 1) : line).split("\t", -1);
                for (Scope s : scopes) {
                    if (f[0].equals(s.name)) s.lines.computeIfAbsent(f[1], k -> new ArrayList<>()).add(Arrays.copyOfRange(f, 2, f.length));
                }
            }

            theFile(scopes, fixture, full);
            theReader(scopes);
            theNations(scopes);
            theSums(scopes);
            theFreight(scopes);
            theKernel(scopes);
            theCells(scopes);
            theMoneysAndPaths(scopes);
            theStandInCentury(scopes, fixtureBytes);
        }

        out.printf("%nWorldDataCheck took %.1f s%n", (System.nanoTime() - start) / 1e9);
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** A resource beside the classes, or failing that the source tree's copy from the working directory; null if neither. */
    static byte[] resource(String name) throws IOException {
        try (InputStream in = WorldDataCheck.class.getResourceAsStream("/" + name)) {
            if (in != null) return in.readAllBytes();
        }
        Path p = Path.of("src/main/resources", name);
        return Files.isRegularFile(p) ? Files.readAllBytes(p) : null;
    }

    /* =====================================================================
       HELPERS
       ===================================================================== */

    /** Whether two figures are the same: equal, or both NaN. */
    static boolean same(double a, double b) {
        return a == b || (Double.isNaN(a) && Double.isNaN(b));
    }

    /** The share of the larger by which two figures differ; 0 where they are equal, infinite where either is not finite and they are not equal. */
    static double rel(double a, double b) {
        if (a == b) return 0;
        if (!Double.isFinite(a) || !Double.isFinite(b)) return Double.POSITIVE_INFINITY;
        return Math.abs(a - b) / Math.max(Math.abs(a), Math.abs(b));
    }

    /** What differs between a text expected and read, null where nothing does. */
    static String differs(String what, String expected, String read) {
        return expected.equals(read) ? null : what + " expected " + expected + ", read " + read;
    }

    /** What differs between a figure expected and read, null where they are the same double. */
    static String differs(String what, double expected, double read) {
        return same(expected, read) ? null : what + " expected " + expected + ", read " + read;
    }

    /** The first of these that is not null. */
    static String firstOf(String... differences) {
        for (String d : differences) if (d != null) return d;
        return null;
    }

    /** A nation as an expect line writes it: its iso3, or its number; 0 where neither is one. */
    static int nation(WorldData d, String token) {
        if (token.chars().allMatch(Character::isDigit)) return Integer.parseInt(token);
        return d.numberOf(token);
    }

    /** A month as an expect line writes it: its number in the FILE's count (FORMAT.txt: month 0 is 2000-01), or yyyy-mm; as WorldData numbers it, from FIRST_YEAR (EP1). */
    static int month(String token) {
        if (token.length() == 7 && token.charAt(4) == '-') return WorldData.monthNumber(Integer.parseInt(token.substring(0, 4)), Integer.parseInt(token.substring(5)));
        return FILE_MONTH_ZERO + Integer.parseInt(token);
    }

    /** A tolerance as a short label: 1e-09. */
    static String sci(double x) {
        return String.format("%.0e", x);
    }

    /** One tally over an expect kind's lines of the scope, each judged; at least one line has to be there. */
    static void each(Scope s, String label, String kind, Judge judge) {
        Tally t = new Tally();
        for (String[] f : s.of(kind)) {
            String bad;
            try {
                bad = judge.judge(f);
            } catch (Exception e) {
                bad = e.toString();
            }
            t.see(bad == null, kind + " " + String.join(" ", f) + ": " + bad);
        }
        tally(s.label(label), t);
    }

    /** The expect file's one line of a kind in the scope, null if it has none or more than one. */
    static String[] one(Scope s, String kind) {
        List<String[]> l = s.of(kind);
        return l.size() == 1 ? l.get(0) : null;
    }

    /* =====================================================================
       1. THE FILE
       ===================================================================== */

    static void theFile(List<Scope> scopes, Scope fixture, Scope full) {
        out.println("--- 1. the file ---");
        for (Scope s : scopes) {
            WorldData d = s.data;
            List<String> names = new ArrayList<>();
            boolean kinds = true;
            for (WorldData.Layer l : d.layers()) {
                names.add(l.name);
                boolean tiles = l.name.equals(WorldData.OWNER);
                kinds &= l.kind == (tiles ? WorldData.KIND_TILES : WorldData.KIND_WHOLE) && l.cellsPerDegree == (tiles ? WorldData.OWNER_CELLS : 0);
            }
            assertTrue(s.label("its layers are FORMAT.txt's, in order: " + String.join(" ", WorldData.LAYERS)), names.equals(WorldData.LAYERS));
            assertTrue(s.label("the owner layer is tiles of 240 cells a degree, and every other layer is one blob"), kinds);
            assertTrue(s.label("its layout version is the one this build reads, " + WorldData.FORMAT), d.version() == WorldData.FORMAT);

            String[] file = one(s, "file");
            assertTrue(s.label("the expect file has one file line for it"), file != null);
            if (file != null) {
                String identity = d.identity();
                assertTrue(s.label("its SHA-256 is the expect file's (" + file[0].substring(0, 12) + "...)"), identity.endsWith(":" + file[0]));
                assertTrue(s.label("its size is the expect file's (" + file[1] + " bytes)"), d.size() == Long.parseLong(file[1]));
            }
            String name = s == fixture ? FIXTURE_NAME : FULL_NAME;
            assertTrue(s.label("its meta names it " + name + ", and its identity begins so"), name.equals(d.meta("name")) && d.identity().startsWith(name + ":"));
            assertTrue(s.label("its meta says the day it was made, yyyy-mm-dd"), d.meta("made") != null && DAY.matcher(d.meta("made")).matches());
            assertTrue(s.label("its meta counts the layers' rows: " + d.nationCount() + " nations, " + d.areaCount() + " areas, " + d.moneys().size() + " moneys, " + d.goodCount() + " goods, base year " + d.tradeBaseYear()),
                    String.valueOf(d.nationCount()).equals(d.meta("nations")) && String.valueOf(d.areaCount()).equals(d.meta("areas"))
                    && String.valueOf(d.moneys().size()).equals(d.meta("moneys")) && String.valueOf(d.goodCount()).equals(d.meta("goods"))
                    && String.valueOf(d.tradeBaseYear()).equals(d.meta("base_year")));
            List<String> missing = new ArrayList<>();
            for (String dial : FREIGHT_DIALS) {
                try {
                    if (!Double.isFinite(d.dial(dial)) || d.dial(dial) <= 0) missing.add(dial);
                } catch (RuntimeException e) {
                    missing.add(dial);
                }
            }
            assertTrue(s.label("its meta holds the dials the freight cost reads, each above 0" + (missing.isEmpty() ? "" : " (missing or bad: " + missing + ")")), missing.isEmpty());
            Tally sources = new Tally();
            for (WorldData.Citation c : d.sources()) {
                boolean downloaded = c.url().startsWith("http") && DAY.matcher(c.downloaded()).matches(), authored = c.url().equals("-") && c.downloaded().equals("-");
                sources.see(!c.id().isEmpty() && !c.name().isEmpty() && !c.licence().isEmpty() && (downloaded || authored), c.id());
            }
            tally(s.label("its meta names every source with an id, a name, a licence, and an address and the day it was downloaded - or neither, for a table the game authored"), sources);
        }

        String[] of = one(fixture, "of");
        assertTrue("[fixture] the expect file says which full file it was cut from", of != null);
        String made = fixture.data.meta("of");
        if (of != null) assertTrue("[fixture] its meta's of names that file (" + of[0].substring(0, Math.min(20, of[0].length())) + "...)", of[0].equals(made));
        if (full != null) {
            assertTrue("[fixture] its meta's of is the identity of the full file found (" + full.data.identity().substring(0, 20) + "...)", full.data.identity().equals(made));
        }
    }

    /* =====================================================================
       2. THE READER IS worldread.py's
       ===================================================================== */

    static void theReader(List<Scope> scopes) {
        out.println("--- 2. the reader is worldread.py's ---");
        for (Scope s : scopes) {
            WorldData d = s.data;
            assertTrue(s.label("the expect file has a nation line for every nation and area (" + d.nations().size() + ")"), s.of("nation").size() == d.nations().size());
            each(s, "every nation line: number, iso3, kind, money, node and landlocked are what WorldData reads", "nation", f -> {
                WorldData.Nation n = d.nation(Integer.parseInt(f[0]));
                return firstOf(differs("iso3", f[1], n.iso3()), differs("kind", f[2], n.kind()), differs("money", f[3], n.money() == null ? "-" : n.money()),
                        differs("node_lat", WorldData.number(f[4]), n.nodeLat()), differs("node_lon", WorldData.number(f[5]), n.nodeLon()),
                        differs("landlocked", f[6], n.landlocked() ? "1" : "0"));
            });
            each(s, "every path line: the value and its mark are what WorldData reads", "path", f -> {
                int n = nation(d, f[1]);
                int year = Integer.parseInt(f[2]);
                return firstOf(differs("value", WorldData.number(f[3]), d.pathValue(f[0], n, year)), differs("mark", f[4], String.valueOf(d.pathMark(f[0], n, year))));
            });
            if (!d.hasPrices()) {
                out.println("  " + s.label("no prices layer: the price lines were not read (section 1 fails on the layer list)"));
            } else {
                each(s, "every price line: the price and its mark are what WorldData reads", "price", f -> {
                    int good = d.goodNumber(f[0]);
                    int year = Integer.parseInt(f[1]);
                    return firstOf(differs("value", WorldData.number(f[2]), d.price(good, year)), differs("mark", f[3], String.valueOf(d.priceMark(good, year))));
                });
            }
            each(s, "every rate line: the rate and its mark are what WorldData reads", "rate", f -> {
                int money = d.moneyNumber(f[0]);
                int m = month(f[1]);
                return firstOf(differs("value", WorldData.number(f[2]), d.rate(money, m)), differs("mark", f[3], String.valueOf(d.rateMark(money, m))));
            });
            each(s, "every tile line: the index entry is what WorldData reads", "tile", f -> differs("entry", f[1], String.valueOf(d.ownerEntry(Integer.parseInt(f[0])))));
            each(s, "every cell line: the owner is what WorldData reads, by tile and cell and by the cell's centre", "cell", f -> {
                int tile = Integer.parseInt(f[0]), r = Integer.parseInt(f[1]), c = Integer.parseInt(f[2]);
                double lat = EarthData.tileLat0(tile) + 1 - (r + 0.5) / WorldData.OWNER_CELLS, lon = EarthData.tileLon0(tile) + (c + 0.5) / WorldData.OWNER_CELLS;
                return firstOf(differs("owner", f[3], String.valueOf(d.ownerCell(tile, r, c))), differs("owner at the centre " + lat + ", " + lon, f[3], String.valueOf(d.ownerAt(lat, lon))));
            });
            each(s, "every freight line: sea km, land km and cost are what WorldData reads", "freight", f -> {
                int a = nation(d, f[0]), b = nation(d, f[1]);
                return firstOf(differs("sea", WorldData.number(f[2]), d.freight().sea(a, b)), differs("land", WorldData.number(f[3]), d.freight().land(a, b)),
                        differs("cost", WorldData.number(f[4]), d.freight().cost(a, b)));
            });
            each(s, "every flow line: flow, tonnes, cost and pref are what WorldData reads", "flow", f -> {
                WorldData.Flows t = d.trade(d.goodNumber(f[0]));
                int k = t.find(nation(d, f[1]), nation(d, f[2]));
                if (k < 0) return "the pair is not in the file";
                return firstOf(differs("flow", WorldData.number(f[3]), t.flow[k]), differs("tonnes", WorldData.number(f[4]), t.tonnes[k]),
                        differs("cost", WorldData.number(f[5]), t.cost[k]), differs("pref", WorldData.number(f[6]), t.pref[k]));
            });
            each(s, "every make line: make and use are what WorldData reads", "make", f -> {
                WorldData.Flows t = d.trade(d.goodNumber(f[0]));
                int n = nation(d, f[1]);
                return firstOf(differs("make", WorldData.number(f[2]), t.make[n - 1]), differs("use", WorldData.number(f[3]), t.use[n - 1]));
            });
            String[] lines = one(s, "lines");
            assertTrue(s.label("the border lines are as many as the expect file counts" + (lines == null ? " (it has no one lines line)" : " (" + lines[0] + ")")),
                    lines != null && d.lines().size() == Integer.parseInt(lines[0]));
            each(s, "every line line: owners, vertex count and first vertex are what WorldData reads", "line", f -> {
                WorldData.Border b = d.lines().get(Integer.parseInt(f[0]));
                return firstOf(differs("left", f[1], String.valueOf(b.left())), differs("right", f[2], String.valueOf(b.right())),
                        differs("n", f[3], String.valueOf(b.lat().length)), vertex("first lat", f[4], b.lat()[0]), vertex("first lon", f[5], b.lon()[0]));
            });

            List<Integer> blobTiles = new ArrayList<>();
            for (int t = 0; t < WorldData.TILES && blobTiles.size() < 3; t++) if (d.ownerEntry(t) > 0) blobTiles.add(t);
            assertTrue(s.label("it has owner tiles with a blob to read twice"), !blobTiles.isEmpty());
            if (!blobTiles.isEmpty()) {
                int before = d.inflations();
                byte[] first = d.ownerTile(blobTiles.get(0)), second = d.ownerTile(blobTiles.get(0));
                assertTrue(s.label("an owner tile read twice is the same bytes, inflated at most once"),
                        first != null && first == second && d.inflations() - before <= 1 && first.length == WorldData.OWNER_TILE_BYTES);
            }
            if (s.name.equals("fixture") && blobTiles.size() == 3) {
                try (WorldData small = reopen(s, 2)) {
                    for (int t : blobTiles) small.ownerTile(t);
                    int three = small.inflations();
                    small.ownerTile(blobTiles.get(2));
                    boolean kept = small.inflations() == three;
                    small.ownerTile(blobTiles.get(0));
                    assertTrue(s.label("with room for two tiles the one read last stays and the one read longest ago goes"), three == 3 && kept && small.inflations() == three + 1);
                } catch (IOException e) {
                    assertTrue(s.label("a second reader opens the same file: " + e), false);
                }
            }
        }
    }

    /** A second reader of a scope's file, with an LRU of the given tiles: the fixture's resource again, or the full file by its path. */
    static WorldData reopen(Scope s, int cacheTiles) throws IOException {
        if (s.name.equals("fixture")) return WorldData.of(resource(WorldData.FIXTURE), cacheTiles);
        return WorldData.open(WorldData.locate(), cacheTiles);
    }

    /** What differs between a vertex as the expect line writes it - in the file's integer units, or as degrees with a point - and the one read. */
    static String vertex(String what, String token, int stored) {
        try {
            long units = Long.parseLong(token);
            return units == stored ? null : what + " expected " + units + ", read " + stored;
        } catch (NumberFormatException e) {
            return differs(what, WorldData.number(token), stored / (double) WorldData.LINE_UNITS);
        }
    }

    /* =====================================================================
       3. EVERY UN MEMBER AND THE THREE
       ===================================================================== */

    static void theNations(List<Scope> scopes) {
        out.println("--- 3. every UN member and the three ---");
        List<String> un = Arrays.asList(UN_MEMBERS.split(" "));
        assertTrue("the check's own list holds the UN's " + UN_MEMBER_COUNT + " member states, none twice, A to Z", un.size() == UN_MEMBER_COUNT && new TreeSet<>(un).size() == UN_MEMBER_COUNT
                && new ArrayList<>(new TreeSet<>(un)).equals(un));
        TreeSet<String> world = new TreeSet<>(un);
        world.addAll(THE_THREE);
        assertTrue("and the three (TWN, HKG, MAC) are none of them: " + FULL_NATIONS + " in all", world.size() == FULL_NATIONS);

        for (Scope s : scopes) {
            WorldData d = s.data;
            List<String> expected = s.name.equals("fixture") ? TWELVE : new ArrayList<>(world);
            List<String> got = new ArrayList<>();
            List<WorldData.Nation> areas = new ArrayList<>();
            boolean numbered = true, kindsKnown = true;
            for (int i = 0; i < d.nations().size(); i++) {
                WorldData.Nation n = d.nations().get(i);
                numbered &= n.number() == i + 1;
                if (n.isNation()) got.add(n.iso3());
                else if (WorldData.KIND_AREA.equals(n.kind())) areas.add(n);
                else kindsKnown = false;
            }
            assertTrue(s.label("its nations of kind nation are " + (s.name.equals("fixture") ? "the fixture's twelve" : "the 193 UN members and TWN, HKG, MAC") + " (" + got.size() + ")"), got.equals(expected));
            assertTrue(s.label("they are numbered 1..N in the order of their iso3 codes, A to Z, and the areas follow them"),
                    numbered && kindsKnown && got.size() == d.nationCount() && d.nationCount() == expected.size()
                    && d.nations().subList(0, d.nationCount()).stream().allMatch(n -> n.isNation())
                    && d.nations().stream().limit(d.nationCount()).map(WorldData.Nation::iso3).toList().equals(new ArrayList<>(new TreeSet<>(got))));
            List<String> areaCodes = areas.stream().map(WorldData.Nation::iso3).toList();
            assertTrue(s.label("the areas are numbered N+1..N+" + areas.size() + " in the order of their codes, and nations and areas are at most " + WorldData.MAX_OWNERS),
                    areaCodes.equals(new ArrayList<>(new TreeSet<>(areaCodes))) && d.nations().size() <= WorldData.MAX_OWNERS && (!s.name.equals("fixture") || areas.isEmpty()));
            Tally sellers = new Tally();
            for (WorldData.Nation n : d.nations()) {
                if (n.isNation()) sellers.see(n.iso3().equals(n.seller()) && n.money() != null && n.moneyCodes().length > 0, n.iso3() + " seller " + n.seller() + " money " + n.money());
                else sellers.see((n.seller() == null || got.contains(n.seller())) && n.money() == null && n.moneyCodes().length == 0, n.iso3() + " seller " + n.seller() + " money " + n.money());
            }
            tally(s.label("a nation sells its own land and has a money; an area's seller is a nation or none, and it has no money"), sellers);
            Tally regions = new Tally();
            TreeSet<String> seen = new TreeSet<>();
            for (WorldData.Nation n : d.nations()) {
                regions.see(n.isNation() ? n.region() != null && !n.region().isBlank() : n.region() == null, n.iso3() + " region " + n.region());
                if (n.region() != null) seen.add(n.region());
            }
            tally(s.label("every nation has a World Bank region, and an area has none"), regions);
            if (s.name.equals("full")) assertTrue(s.label("the full file's nations fall in the World Bank's " + WORLD_BANK_REGIONS + " regions: " + seen), seen.size() == WORLD_BANK_REGIONS);
        }
    }

    /* =====================================================================
       4. THE WORLD'S SUMS CLOSE
       ===================================================================== */

    static void theSums(List<Scope> scopes) {
        out.println("--- 4. the world's sums close ---");
        for (Scope s : scopes) {
            WorldData d = s.data;
            int n = d.nationCount();
            Tally make = new Tally(), use = new Tally(), world = new Tally(), positive = new Tally(), own = new Tally(), marks = new Tally(), order = new Tally();
            long pairs = 0;
            for (int g = 1; g <= d.goodCount(); g++) {
                WorldData.Flows f = d.trade(g);
                String key = d.good(g).key();
                double[] row = new double[n], col = new double[n];
                for (int p = 0; p < f.pairs(); p++) {
                    int a = f.seller[p], b = f.buyer[p];
                    boolean inRange = a >= 1 && a <= n && b >= 1 && b <= n;
                    order.see(inRange && (p == 0 || f.seller[p - 1] < a || (f.seller[p - 1] == a && f.buyer[p - 1] < b)), key + " pair " + p + " (" + a + ", " + b + ")");
                    positive.see(f.flow[p] > 0 && Double.isFinite(f.flow[p]), key + " " + a + " to " + b + " flow " + f.flow[p]);
                    marks.see(f.mark[p] == (a == b ? WorldData.MARK_ESTIMATE : WorldData.MARK_PUBLISHED), key + " " + a + " to " + b + " mark " + f.mark[p]);
                    if (inRange) {
                        row[a - 1] += f.flow[p];
                        col[b - 1] += f.flow[p];
                    }
                    pairs++;
                }
                double makeAll = 0, useAll = 0;
                for (int i = 1; i <= n; i++) {
                    make.see(rel(f.make[i - 1], row[i - 1]) <= SUM_TOL, key + " nation " + i + " make " + f.make[i - 1] + " against its row's " + row[i - 1]);
                    use.see(rel(f.use[i - 1], col[i - 1]) <= SUM_TOL, key + " nation " + i + " use " + f.use[i - 1] + " against its column's " + col[i - 1]);
                    own.see(f.find(i, i) >= 0, key + " nation " + i + " has no own pair");
                    makeAll += f.make[i - 1];
                    useAll += f.use[i - 1];
                }
                world.see(rel(makeAll, useAll) <= SUM_TOL, key + " the world's make " + makeAll + " against its use " + useAll);
            }
            out.println("  [" + s.name + "] " + d.goodCount() + " goods, " + pairs + " pairs kept");
            tally(s.label("every nation's make in every good is the sum of its row's flows (" + sci(SUM_TOL) + " relative)"), make);
            tally(s.label("and its use is the sum of its column's"), use);
            tally(s.label("the world's make in every good is the world's use"), world);
            tally(s.label("every flow is above 0 and finite"), positive);
            tally(s.label("every nation has its own pair in every good"), own);
            tally(s.label("every pair is marked 1 if the nation's own, else 3 (BACI's)"), marks);
            tally(s.label("every good's pairs are sorted by seller then buyer, none twice, all among the nations"), order);
        }
    }

    /* =====================================================================
       5. THE FREIGHT TABLE KEEPS THE TRIANGLE RULE
       ===================================================================== */

    static void theFreight(List<Scope> scopes) {
        out.println("--- 5. the freight table keeps the triangle rule ---");
        for (Scope s : scopes) {
            WorldData d = s.data;
            WorldData.Freight f = d.freight();
            int n = f.n;
            assertTrue(s.label("the table is of the nations (" + n + " x " + n + ")"), n == d.nationCount());
            Tally finite = new Tally(), diagonal = new Tally(), symmetric = new Tally(), sum = new Tally();
            double landPerSea;
            WorldData.Freight again;
            try {
                landPerSea = d.dial(WorldData.LAND_PER_SEA);
                again = d.closure();
            } catch (RuntimeException e) {
                assertTrue(s.label("the meta has the LAND_PER_SEA dial the table and its closure are made with: " + e.getMessage()), false);
                continue;
            }
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    int at = f.at(i, j);
                    finite.see(Double.isFinite(f.sea[at]) && Double.isFinite(f.land[at]) && Double.isFinite(f.cost[at]) && f.sea[at] >= 0 && f.land[at] >= 0 && f.cost[at] >= 0, i + " to " + j);
                    if (i == j) diagonal.see(f.sea[at] == 0 && f.land[at] == 0 && f.cost[at] == 0, "nation " + i);
                    if (i < j) {
                        int back = f.at(j, i);
                        symmetric.see(rel(f.sea[at], f.sea[back]) <= TABLE_TOL && rel(f.land[at], f.land[back]) <= TABLE_TOL && rel(f.cost[at], f.cost[back]) <= TABLE_TOL, i + " and " + j);
                    }
                    sum.see(rel(f.cost[at], f.sea[at] + landPerSea * f.land[at]) <= TABLE_TOL, i + " to " + j + " cost " + f.cost[at]);
                }
            }
            tally(s.label("every entry is finite and not below 0"), finite);
            tally(s.label("the diagonal is 0 in sea km, land km and cost"), diagonal);
            tally(s.label("the table is symmetric (" + sci(TABLE_TOL) + " relative)"), symmetric);
            tally(s.label("every cost is its sea km + LAND_PER_SEA x its land km"), sum);

            long triples = 0, broken = 0;
            String example = null;
            for (int a = 0; a < n; a++) {
                for (int b = 0; b < n; b++) {
                    double ab = f.cost[a * n + b];
                    for (int c = 0; c < n; c++) {
                        triples++;
                        double via = ab + f.cost[b * n + c];
                        if (f.cost[a * n + c] > via * (1 + TRIANGLE_SLACK)) {
                            broken++;
                            if (example == null) example = (a + 1) + " to " + (c + 1) + " costs " + f.cost[a * n + c] + ", through " + (b + 1) + " " + via;
                        }
                    }
                }
            }
            assertTrue(s.label("cost(a, c) <= cost(a, b) + cost(b, c) for all " + triples + " triples" + (broken > 0 ? "; " + broken + " do not, e.g. " + example : "")), broken == 0 && triples > 0);

            assertTrue(s.label("the edges are nations a < b, their sea and land km above 0 or 0, and kinds " + (s.name.equals("fixture") ? "4 only (a fixture's)" : "0 to 3")), edgesOk(s, f));
            double worst = 0;
            String worstAt = "none";
            for (int at = 0; at < n * n; at++) {
                for (int t = 0; t < 3; t++) {
                    double[] a = t == 0 ? f.sea : t == 1 ? f.land : f.cost, b = t == 0 ? again.sea : t == 1 ? again.land : again.cost;
                    double r = rel(a[at], b[at]);
                    if (r > worst) {
                        worst = r;
                        worstAt = (t == 0 ? "sea " : t == 1 ? "land " : "cost ") + (at / n + 1) + " to " + (at % n + 1) + ": stored " + a[at] + ", closure " + b[at];
                    }
                }
            }
            assertTrue(s.label("the closure of the " + f.edges.size() + " edges (Floyd-Warshall) is the stored table in sea km, land km and cost (" + sci(TABLE_TOL) + " relative; the largest " + String.format("%.2e", worst) + ")" + (worst > TABLE_TOL ? ": " + worstAt : "")),
                    worst <= TABLE_TOL && again.n == n);
        }
    }

    /** Whether a scope's freight edges are well formed. */
    static boolean edgesOk(Scope s, WorldData.Freight f) {
        if (f.edges.isEmpty()) return false;
        for (WorldData.Edge e : f.edges) {
            if (e.a() < 1 || e.b() > f.n || e.a() >= e.b()) return false;
            if (!(e.seaKm() >= 0) || !(e.landKm() >= 0) || !Double.isFinite(e.seaKm()) || !Double.isFinite(e.landKm())) return false;
            if (s.name.equals("fixture") ? e.kind() != 4 : e.kind() < 0 || e.kind() > 3) return false;
        }
        return true;
    }

    /* =====================================================================
       6. THE BASE YEAR REPRODUCES BACI'S FLOWS ON THE KEPT PAIRS
       ===================================================================== */

    /** One good's kernel solved from a = b = 1: the passes it took, the worst margin, and the flows. */
    static final class Solved {
        int sweeps;
        double margin;
        double[] x;
    }

    /**
     * FORMAT.txt's THE KERNEL: x_ij = a_i b_j exp(pref_ij - cost_ij) with the rows summing to make and the columns to use, solved from a = b = 1. Plain
     * Sinkhorn cannot reach MARGIN_TOL: a nation that trades a ten-thousandth of its use (San Marino) has a and b chasing each other through its own pair, the
     * error shrinking by that share a sweep. So each pass solves every nation's a_i and b_i together and exactly, the others held (nodeSolve: its own flow
     * z = K_ii a_i b_i is the root in [0, min(make, use)] of K_ii (make - z)(use - z) = z R_i C_i, R_i the off-diagonal row sum of K b, C_i the off-diagonal
     * column sum of a K), in nation order; the solution is unique, so it is the one the sweeps approach. As econ_trade.py solve_exact does it, pass for pass.
     */
    static Solved solve(WorldData.Flows f, int n) {
        int k = f.pairs();
        double[] kernel = new double[k];
        for (int p = 0; p < k; p++) kernel[p] = Math.exp(f.pref[p] - f.cost[p]);
        double[] kd = new double[n + 1];
        int[] rowCount = new int[n + 2], colCount = new int[n + 2];
        boolean[] present = new boolean[n + 1];
        for (int p = 0; p < k; p++) {
            present[f.seller[p]] = present[f.buyer[p]] = true;
            if (f.seller[p] == f.buyer[p]) {
                kd[f.seller[p]] = kernel[p];
            } else {
                rowCount[f.seller[p] + 1]++;
                colCount[f.buyer[p] + 1]++;
            }
        }
        for (int i = 1; i <= n + 1; i++) {
            rowCount[i] += rowCount[i - 1];
            colCount[i] += colCount[i - 1];
        }
        int[] rowAt = new int[rowCount[n + 1]], colAt = new int[colCount[n + 1]];
        int[] rowFill = Arrays.copyOf(rowCount, n + 1), colFill = Arrays.copyOf(colCount, n + 1);
        for (int p = 0; p < k; p++) {
            if (f.seller[p] == f.buyer[p]) continue;
            rowAt[rowFill[f.seller[p]]++] = p;
            colAt[colFill[f.buyer[p]]++] = p;
        }
        double[] a = new double[n + 1], b = new double[n + 1];
        Arrays.fill(a, 1);
        Arrays.fill(b, 1);
        double target = MARGIN_TOL / TARGET_DIVISOR, best = 1;
        int stale = 0;
        Solved r = new Solved();
        while (r.sweeps < SWEEP_CAP) {
            r.sweeps++;
            double change = 0;
            for (int i = 1; i <= n; i++) {
                if (!present[i]) continue;
                double rowSum = 0, colSum = 0;
                for (int q = rowCount[i]; q < rowCount[i + 1]; q++) rowSum += kernel[rowAt[q]] * b[f.buyer[rowAt[q]]];
                for (int q = colCount[i]; q < colCount[i + 1]; q++) colSum += kernel[colAt[q]] * a[f.seller[colAt[q]]];
                double[] pq = nodeSolve(kd[i], rowSum, colSum, f.make[i - 1], f.use[i - 1], a[i]);
                change = Math.max(change, Math.max(Math.abs(pq[0] / a[i] - 1), Math.abs(pq[1] / b[i] - 1)));
                a[i] = pq[0];
                b[i] = pq[1];
            }
            if (change < CHANGE_TOL && residual(f, n, present, kd, kernel, rowCount, rowAt, colCount, colAt, a, b) <= target) {
                if (change < best) {
                    best = change;
                    stale = 0;
                } else {
                    stale++;
                }
                if (change < CHANGE_FLOOR || stale >= STALE_PASSES) break;
            }
        }
        r.x = new double[k];
        double[] rowTotal = new double[n + 1], rowKeep = new double[n + 1], colTotal = new double[n + 1], colKeep = new double[n + 1];
        for (int p = 0; p < k; p++) {
            r.x[p] = a[f.seller[p]] * b[f.buyer[p]] * kernel[p];
            add(rowTotal, rowKeep, f.seller[p], r.x[p]);
            add(colTotal, colKeep, f.buyer[p], r.x[p]);
        }
        for (int i = 1; i <= n; i++) {
            if (!present[i]) continue;
            r.margin = Math.max(r.margin, Math.max(margin(rowTotal[i] + rowKeep[i], f.make[i - 1]), margin(colTotal[i] + colKeep[i], f.use[i - 1])));
        }
        return r;
    }

    /** The largest share by which a nation's row or column, at the current a and b, is off its make or use. */
    static double residual(WorldData.Flows f, int n, boolean[] present, double[] kd, double[] kernel, int[] rowCount, int[] rowAt, int[] colCount, int[] colAt, double[] a, double[] b) {
        double worst = 0;
        for (int i = 1; i <= n; i++) {
            if (!present[i]) continue;
            double rowSum = 0, colSum = 0;
            for (int q = rowCount[i]; q < rowCount[i + 1]; q++) rowSum += kernel[rowAt[q]] * b[f.buyer[rowAt[q]]];
            for (int q = colCount[i]; q < colCount[i + 1]; q++) colSum += kernel[colAt[q]] * a[f.seller[colAt[q]]];
            if (f.make[i - 1] > 0) worst = Math.max(worst, Math.abs(a[i] * (kd[i] * b[i] + rowSum) - f.make[i - 1]) / f.make[i - 1]);
            if (f.use[i - 1] > 0) worst = Math.max(worst, Math.abs(b[i] * (kd[i] * a[i] + colSum) - f.use[i - 1]) / f.use[i - 1]);
        }
        return worst;
    }

    /**
     * The pair (a_i, b_i) that meets nation i's row and column exactly, the others held: a (kd b + R) = make and b (kd a + C) = use, R and C the
     * off-diagonal sums of K b and K a, kd the own pair's K (0 where it has none); the cases econ_trade.py node_solve has, in its order.
     */
    static double[] nodeSolve(double kd, double rowSum, double colSum, double make, double use, double aOld) {
        if (kd > 0 && rowSum > 0 && colSum > 0) {
            double alpha = kd * rowSum / colSum, beta = rowSum - kd * (make - use) / colSum;
            double disc = Math.sqrt(beta * beta + 4 * alpha * make);
            double p = beta >= 0 ? 2 * make / (beta + disc) : (disc - beta) / (2 * alpha);
            return new double[] { p, use / (kd * p + colSum) };
        }
        if (kd > 0 && rowSum > 0) {            // nobody else sells to it: b (kd a) = use and a R = make - use
            double p = (make - use) / rowSum;
            return new double[] { p, use / (kd * p) };
        }
        if (kd > 0 && colSum > 0) {            // it sells to nobody else: a (kd b) = make and b C = use - make
            double q = (use - make) / colSum;
            return new double[] { make / (kd * q), q };
        }
        if (kd > 0) return new double[] { aOld, make / (kd * aOld) };         // its own pair alone: only the product is fixed
        return new double[] { rowSum > 0 ? make / rowSum : aOld, colSum > 0 ? use / colSum : 1.0 };
    }

    /** Adds x to total[i] keeping the rounding it loses in keep[i] (Neumaier's sum), so a row of 196 flows adds up to the last ulp of the larger. */
    static void add(double[] total, double[] keep, int i, double x) {
        double t = total[i] + x;
        keep[i] += Math.abs(total[i]) >= Math.abs(x) ? (total[i] - t) + x : (x - t) + total[i];
        total[i] = t;
    }

    /** How far a margin is from the make or use it is meant to be, as a share of that. */
    static double margin(double got, double want) {
        if (want == 0) return got == 0 ? 0 : Double.POSITIVE_INFINITY;
        return Math.abs(got - want) / Math.abs(want);
    }

    static void theKernel(List<Scope> scopes) {
        out.println("--- 6. the base year reproduces BACI's flows on the kept pairs ---");
        for (Scope s : scopes) {
            WorldData d = s.data;
            long t0 = System.nanoTime();
            double seaUsd, landUsd, handling;
            try {
                seaUsd = d.dial("SEA_USD_TKM");
                landUsd = d.dial("LAND_USD_TKM");
                handling = d.dial("HANDLING_USD_T");
            } catch (RuntimeException e) {
                assertTrue(s.label("the meta has the freight dials the kernel's cost is made with: " + e.getMessage()), false);
                continue;
            }
            Tally converged = new Tally(), flows = new Tally(), costs = new Tally();
            long sweeps = 0, pairs = 0, limited = 0;
            int most = 0;
            String mostGood = "-";
            double worstFlow = 0, worstMargin = 0;
            for (int g = 1; g <= d.goodCount(); g++) {
                WorldData.TradedGood good = d.good(g);
                WorldData.Flows f = d.trade(g);
                Solved r = solve(f, d.nationCount());
                sweeps += r.sweeps;
                pairs += f.pairs();
                if (r.sweeps > most) {
                    most = r.sweeps;
                    mostGood = good.key();
                }
                worstMargin = Math.max(worstMargin, r.margin);
                converged.see(r.margin <= MARGIN_TOL, good.key() + " after " + r.sweeps + " passes its largest margin is " + r.margin);
                for (int p = 0; p < f.pairs(); p++) {
                    double err = rel(r.x[p], f.flow[p]);
                    double allow = FLOW_TOL + MARGIN_ULPS * Math.min(f.make[f.seller[p] - 1], f.use[f.buyer[p] - 1]) / f.flow[p];
                    if (allow > 2 * FLOW_TOL) limited++;
                    else worstFlow = Math.max(worstFlow, err);
                    flows.see(err <= allow, good.key() + " " + f.seller[p] + " to " + f.buyer[p] + " solved " + r.x[p] + ", file " + f.flow[p] + ", off by " + err + " of an allowed " + allow);
                    double freight = f.seller[p] == f.buyer[p] ? 0
                            : seaUsd * d.freight().sea(f.seller[p], f.buyer[p]) + landUsd * d.freight().land(f.seller[p], f.buyer[p]) + handling;
                    double cost = good.elasticity() * Math.log(1 + freight / good.unitValue());
                    costs.see(rel(cost, f.cost[p]) <= COST_TOL, good.key() + " " + f.seller[p] + " to " + f.buyer[p] + " cost " + f.cost[p] + ", elasticity x ln(1 + freight / unit value) " + cost);
                }
            }
            out.printf("  [%s] the solve took %d passes in all over %d goods and %d pairs, at most %d (%s); largest margin %.2e, largest flow error %.2e (%d flows under 1e-6 of their margin are held to its ulps), %.1f s%n",
                    s.name, sweeps, d.goodCount(), pairs, most, mostGood, worstMargin, worstFlow, limited, (System.nanoTime() - t0) / 1e9);
            tally(s.label("every good's solve from a = b = 1 meets every margin to " + sci(MARGIN_TOL) + " within " + SWEEP_CAP + " passes"), converged);
            tally(s.label("it gives every pair's flow within " + sci(FLOW_TOL) + " relative, and a few ulps of the margin for a tiny flow"), flows);
            tally(s.label("every pair's cost is elasticity x ln(1 + freight a tonne / unit value) from the freight table and the dials"), costs);

            if (s.name.equals("fixture")) {
                int[] dropped = { 0 };
                each(s, "every BACI sum equals the file's flow within " + sci(BACI_TOL) + " relative where the pair is kept", "baci", f -> {
                    int k = d.trade(d.goodNumber(f[0])).find(nation(d, f[1]), nation(d, f[2]));
                    if (k < 0) {
                        dropped[0]++;
                        return null;
                    }
                    double flow = d.trade(d.goodNumber(f[0])).flow[k];
                    return rel(flow, WorldData.number(f[3])) <= BACI_TOL ? null : "the file's flow is " + flow + ", BACI's " + f[3];
                });
                out.println("  [fixture] " + dropped[0] + " of " + s.of("baci").size() + " BACI pairs are not kept (the partner rule dropped them), counted, not failed");
            }
        }
    }

    /* =====================================================================
       7. A NATION FOR EVERY LAND CELL INSIDE EVERY BORDER TILE
       ===================================================================== */

    static void theCells(List<Scope> scopes) throws IOException {
        out.println("--- 7. a nation for every land cell inside every border tile ---");
        for (Scope s : scopes) {
            EarthData earth = null;
            String against;
            if (s.name.equals("fixture")) {
                byte[] bytes = EarthCheck.resource(EarthCheck.FIXTURE);
                assertTrue(s.label("EarthCheck's earth fixture is beside the classes (" + EarthCheck.FIXTURE + ")"), bytes != null);
                if (bytes == null) continue;
                earth = EarthData.of(bytes);
                against = "the earth fixture";
            } else {
                Path path = EarthData.locate();
                if (path == null) {
                    out.println("[full] earth-2.bin not named (-D" + EarthData.PROPERTY + "=<path>) nor at " + EarthData.DATA_FILE + ": the full file's cells were not checked");
                    continue;
                }
                earth = EarthData.open(path);
                against = path.toString();
            }
            try (EarthData e = earth) {
                if (!e.hasRelief()) {
                    assertTrue(s.label("the earth file has the landmass layer"), false);
                    continue;
                }
                WorldData d = s.data;
                long t0 = System.nanoTime();
                Tally owned = new Tally();
                long tiles = 0, land = 0;
                for (int tile = 0; tile < WorldData.TILES; tile++) {
                    if (d.ownerEntry(tile) <= 0) continue;
                    byte[] blob = d.ownerTile(tile);
                    EarthData.LandmassGrid grid = e.landmassGrid(tile);
                    tiles++;
                    for (int r = 0; r < WorldData.OWNER_CELLS; r++) {
                        for (int c = 0; c < WorldData.OWNER_CELLS; c++) {
                            int landmass = grid.id(r, c);
                            if (landmass == 0) continue;
                            land++;
                            int owner = blob[r * WorldData.OWNER_CELLS + c] & 0xFF;
                            if (owner == 0 || owner > d.nations().size()) owned.see(false, "tile " + tile + " cell " + r + ", " + c + " is land of landmass " + landmass + " and its owner is " + owner);
                            else owned.n++;
                        }
                    }
                }
                out.println("  [" + s.name + "] " + tiles + " border tiles with a blob, " + land + " land cells, read against " + against + String.format(" in %.1f s", (System.nanoTime() - t0) / 1e9));
                tally(s.label("every cell the earth file calls land, in every border tile, has an owner among the " + d.nations().size() + " nations and areas"), owned);
            }
        }
    }

    /* =====================================================================
       8. THE MONEYS AND PATHS ARE WHOLE
       ===================================================================== */

    static void theMoneysAndPaths(List<Scope> scopes) {
        out.println("--- 8. the moneys and paths are whole ---");
        for (Scope s : scopes) {
            WorldData d = s.data;
            int n = d.nationCount();
            Tally history = new Tally(), rates = new Tally();
            for (WorldData.Nation nation : d.nations()) {
                if (!nation.isNation()) continue;
                int[] from = nation.moneyMonths();
                String[] codes = nation.moneyCodes();
                boolean ok = from.length > 0 && from[0] == 0 && codes[codes.length - 1].equals(nation.money());
                for (int i = 1; i < from.length; i++) ok &= from[i] > from[i - 1] && from[i] > FILE_MONTH_ZERO;
                for (String code : codes) ok &= d.moneyNumber(code) > 0;
                history.see(ok, nation.iso3() + " history " + Arrays.toString(codes) + " from " + Arrays.toString(from) + ", money in 2025-12 " + nation.money());
                for (int m = 0; m <= LAST_MONTH; m++) {
                    String code = d.moneyAt(nation.number(), m);
                    int money = code == null ? 0 : d.moneyNumber(code);
                    double rate = money == 0 ? Double.NaN : d.rate(money, m);
                    rates.see(Double.isFinite(rate) && rate > 0, nation.iso3() + " " + WorldData.monthLabel(m) + " " + code + " rate " + rate);
                }
            }
            tally(s.label("every nation's money history starts at month 0 (its 2000-01 money covers the stand-in century), rises by month after 2000-01, ends at its money, and names moneys the file has"), history);
            tally(s.label("every nation's money, month by month from " + WorldData.monthLabel(0) + " to 2025-12, has a rate above 0 and finite"), rates);

            assertTrue(s.label("the moneys span " + d.months() + " months from " + WorldData.monthLabel(d.firstMonth()) + ", and the paths " + d.pathYears() + " years from " + d.pathFirstYear() + " for the " + d.pathNations() + " nations"),
                    d.firstMonth() == 0 && d.months() == LAST_MONTH + 1 && d.pathFirstYear() == WorldData.FIRST_YEAR && d.pathYears() == PEOPLE_LAST - WorldData.FIRST_YEAR + 1 && d.pathNations() == n);
            assertTrue(s.label("the series are people, gdp_2015usd, gdp_usd, gdp_ppp, cpi, rate, in that order"),
                    d.pathSeries().equals(List.of("people", "gdp_2015usd", "gdp_usd", "gdp_ppp", "cpi", "rate")));
            Tally people = new Tally(), figures = new Tally(), marks = new Tally();
            for (String series : d.pathSeries()) {
                for (int nation = 1; nation <= n; nation++) {
                    for (int year = d.pathFirstYear(); year < d.pathFirstYear() + d.pathYears(); year++) {
                        double v = d.pathValue(series, nation, year);
                        int mark = d.pathMark(series, nation, year);
                        marks.see(mark >= 0 && mark <= WorldData.MARK_PUBLISHED && (mark == WorldData.MARK_NONE ? Double.isNaN(v) : Double.isFinite(v)),
                                series + " " + d.nation(nation).iso3() + " " + year + " value " + v + " mark " + mark);
                        if (series.equals("people")) people.see(Double.isFinite(v) && v > 0, d.nation(nation).iso3() + " " + year + " people " + v);
                        else if (FIGURE_SERIES.contains(series) && year <= FIGURES_LAST) figures.see(Double.isFinite(v) && v > 0, series + " " + d.nation(nation).iso3() + " " + year + " " + v);
                    }
                }
            }
            Tally rateMarks = new Tally();
            for (WorldData.Money money : d.moneys()) {
                for (int m = d.firstMonth(); m < d.firstMonth() + d.months(); m++) {
                    double v = d.rate(money.number(), m);
                    int mark = d.rateMark(money.number(), m);
                    rateMarks.see(mark >= 0 && mark <= WorldData.MARK_PUBLISHED && (mark == WorldData.MARK_NONE ? Double.isNaN(v) : Double.isFinite(v) && v > 0), money.code() + " " + WorldData.monthLabel(m) + " rate " + v + " mark " + mark);
                }
            }
            if (!d.hasPrices()) {
                out.println("  " + s.label("no prices layer: the price paths were not checked (section 1 fails on the layer list)"));
            } else {
                Tally prices = new Tally();
                assertTrue(s.label("the price paths are of the " + d.goodCount() + " goods, " + d.priceYears() + " years from " + d.priceFirstYear()),
                        d.priceFirstYear() == WorldData.FIRST_YEAR && d.priceYears() == FIGURES_LAST - WorldData.FIRST_YEAR + 1);
                for (WorldData.TradedGood good : d.goods()) {
                    for (int year = d.priceFirstYear(); year < d.priceFirstYear() + d.priceYears(); year++) {
                        double v = d.price(good.number(), year);
                        int mark = d.priceMark(good.number(), year);
                        boolean ok = mark >= 0 && mark <= WorldData.MARK_PUBLISHED && (mark == WorldData.MARK_NONE ? Double.isNaN(v) : Double.isFinite(v) && v > 0);
                        if (year == FIGURES_LAST) ok &= v == 1;
                        prices.see(ok, good.key() + " " + year + " price " + v + " mark " + mark);
                    }
                }
                tally(s.label("every good's price is above 0 where it has a mark, NaN where its mark is 0, and 1 in 2025"), prices);
            }
            tally(s.label("every nation's people are finite and above 0 in every year " + WorldData.FIRST_YEAR + " to 2100"), people);
            tally(s.label("every nation's output (gdp_2015usd, gdp_usd, gdp_ppp) and rate are finite and above 0 in every year " + WorldData.FIRST_YEAR + " to 2025"), figures);
            tally(s.label("every path figure's mark is 0 to 3, and the figure is NaN exactly where the mark is 0"), marks);
            tally(s.label("every money's rate has a mark of 0 to 3, and is NaN exactly where the mark is 0"), rateMarks);
        }
    }

    /* =====================================================================
       9. THE STAND-IN CENTURY (EP1)
       ===================================================================== */

    /** The paths' series the stand-in carries back on the US price level; it holds every other. */
    static final List<String> NOMINAL = List.of("cpi", "gdp_usd");

    /** The lead's figure is the rule's within this share of it (a product and a quotient of the same doubles: a rounding or two). */
    static final double LEAD_TOL = 1e-12;

    /** The first year from the file's first that the series has a figure for the nation; -1 where it has none. */
    static int firstFigure(WorldData d, String series, int nation) {
        for (int y = WorldData.FILE_FIRST_YEAR; y < d.pathFirstYear() + d.pathYears(); y++) if (!Double.isNaN(d.pathValue(series, nation, y))) return y;
        return -1;
    }

    /** What the rule makes of a nominal series' lead year y, given the figure v0 of its first year y0: that figure on the US price level. */
    static double carried(double v0, int y0, int y) {
        return v0 * PriceLevel.usCpi(y) / PriceLevel.usCpi(y0);
    }

    /** The mark a made value has: an estimate, or none where it is NaN. */
    static boolean madeMark(double v, int mark) {
        return mark == (Double.isNaN(v) ? WorldData.MARK_NONE : WorldData.MARK_ESTIMATE);
    }

    static void theStandInCentury(List<Scope> scopes, byte[] fixtureBytes) throws Exception {
        int lead = WorldData.FILE_FIRST_YEAR - WorldData.FIRST_YEAR;
        out.println("--- 9. the stand-in century: the reader supplies " + WorldData.FIRST_YEAR + " to " + (WorldData.FILE_FIRST_YEAR - 1) + ", the file's begin in " + WorldData.FILE_FIRST_YEAR + " ---");
        for (Scope s : scopes) {
            WorldData d = s.data;
            int n = d.nationCount();
            assertTrue(s.label("the stand-in is " + lead + " years: the paths and the prices begin in " + WorldData.FIRST_YEAR + ", the rates in its January, month 0"),
                    d.standInYears() == lead && d.priceStandInYears() == lead && d.standInMonths() == 12 * lead
                    && d.pathFirstYear() == WorldData.FIRST_YEAR && d.priceFirstYear() == WorldData.FIRST_YEAR && d.firstMonth() == 0);
            assertTrue(s.label("...and the file's own figures run after it: " + (PEOPLE_LAST - WorldData.FILE_FIRST_YEAR + 1) + " years of paths, " + (FIGURES_LAST - WorldData.FILE_FIRST_YEAR + 1)
                            + " of prices, " + (LAST_MONTH + 1 - FILE_MONTH_ZERO) + " months of rates"),
                    d.pathYears() == lead + PEOPLE_LAST - WorldData.FILE_FIRST_YEAR + 1 && d.priceYears() == lead + FIGURES_LAST - WorldData.FILE_FIRST_YEAR + 1
                    && d.months() == 12 * lead + LAST_MONTH + 1 - FILE_MONTH_ZERO);
            assertTrue(s.label("month 0 is " + WorldData.FIRST_YEAR + "-01 and the file's month 0 is 2000-01, month " + FILE_MONTH_ZERO),
                    WorldData.monthLabel(0).equals(WorldData.FIRST_YEAR + "-01") && WorldData.monthLabel(FILE_MONTH_ZERO).equals("2000-01") && FILE_MONTH_ZERO == 12 * lead);

            Tally held = new Tally(), followed = new Tally(), marks = new Tally(), parity = new Tally();
            int none = 0, later = 0;
            int us = d.numberOf(Nations.FRONTIER);
            for (String series : d.pathSeries()) {
                boolean nominal = NOMINAL.contains(series);
                for (int nation = 1; nation <= n; nation++) {
                    int y0 = firstFigure(d, series, nation);
                    if (y0 < 0) none++;
                    else if (y0 > WorldData.FILE_FIRST_YEAR) later++;
                    double v0 = y0 < 0 ? Double.NaN : d.pathValue(series, nation, y0);
                    for (int y = d.pathFirstYear(); y < WorldData.FILE_FIRST_YEAR; y++) {
                        double v = d.pathValue(series, nation, y);
                        String what = series + " " + d.nation(nation).iso3() + " " + y + " " + v + " (first figure " + y0 + ": " + v0 + ")";
                        if (nominal) followed.see(y0 < 0 ? Double.isNaN(v) : rel(v, carried(v0, y0, y)) <= LEAD_TOL, what);
                        else held.see(same(v, v0), what);
                        marks.see(madeMark(v, d.pathMark(series, nation, y)), what + " mark " + d.pathMark(series, nation, y));
                    }
                }
            }
            tally(s.label("a real series (people, gdp_2015usd, gdp_ppp, rate) holds its first figure through every year before the file's, a nation at a time"), held);
            tally(s.label("a nominal series (cpi, gdp_usd) is its first figure times PriceLevel.usCpi(year) over usCpi(the figure's year), before the file's years"), followed);
            tally(s.label("every figure the stand-in makes is marked an estimate (1), and none (0) where it is NaN"), marks);
            out.println("  " + s.label("rows (a series and a nation) with no figure at all: " + none + "; with a first figure after " + WorldData.FILE_FIRST_YEAR + ": " + later + " (the tallies above count both; the cases below cause them)"));
            if (us > 0) {
                int usY0 = firstFigure(d, "cpi", us);
                for (int nation = 1; nation <= n; nation++) {
                    if (nation == us || firstFigure(d, "cpi", nation) != WorldData.FILE_FIRST_YEAR || usY0 != WorldData.FILE_FIRST_YEAR) continue;
                    double rate0 = d.pathValue("rate", nation, WorldData.FILE_FIRST_YEAR), relative0 = d.pathValue("cpi", nation, WorldData.FILE_FIRST_YEAR) / d.pathValue("cpi", us, WorldData.FILE_FIRST_YEAR);
                    for (int y = d.pathFirstYear(); y < WorldData.FILE_FIRST_YEAR; y++) {
                        double relative = d.pathValue("cpi", nation, y) / d.pathValue("cpi", us, y), rate = d.pathValue("rate", nation, y);
                        parity.see(rel(relative, relative0) <= LEAD_TOL && same(rate, rate0), d.nation(nation).iso3() + " " + y + " prices against the US's " + relative + " and rate " + rate + ", " + WorldData.FILE_FIRST_YEAR + "'s " + relative0 + " and " + rate0);
                    }
                }
            }
            tally(s.label("purchasing-power parity holds before the file: each nation's prices against the US's, and its rate, are its " + WorldData.FILE_FIRST_YEAR + "'s in every year"), parity);

            if (d.hasPrices()) {
                Tally prices = new Tally();
                for (WorldData.TradedGood good : d.goods()) {
                    int first = -1;
                    for (int y = WorldData.FILE_FIRST_YEAR; y < d.priceFirstYear() + d.priceYears() && first < 0; y++) if (!Double.isNaN(d.price(good.number(), y))) first = y;
                    double p0 = first < 0 ? Double.NaN : d.price(good.number(), first);
                    for (int y = d.priceFirstYear(); y < WorldData.FILE_FIRST_YEAR; y++) {
                        double v = d.price(good.number(), y);
                        prices.see(same(v, p0) && madeMark(v, d.priceMark(good.number(), y)), good.key() + " " + y + " price " + v + " mark " + d.priceMark(good.number(), y) + ", the first figure " + p0);
                    }
                }
                tally(s.label("a good's real price holds its first figure through every year before the file's, marked an estimate"), prices);
            }

            Tally rates = new Tally();
            for (WorldData.Money money : d.moneys()) {
                int first = -1;
                for (int t = FILE_MONTH_ZERO; t < d.firstMonth() + d.months() && first < 0; t++) if (!Double.isNaN(d.rate(money.number(), t))) first = t;
                double r0 = first < 0 ? Double.NaN : d.rate(money.number(), first);
                for (int t = d.firstMonth(); t < FILE_MONTH_ZERO; t++) {
                    double v = d.rate(money.number(), t);
                    rates.see(same(v, r0) && madeMark(v, d.rateMark(money.number(), t)), money.code() + " " + WorldData.monthLabel(t) + " rate " + v + " mark " + d.rateMark(money.number(), t) + ", the first month's " + r0);
                }
            }
            tally(s.label("a money's rate holds its first month's through every month before the file's, marked an estimate; NaN stays NaN"), rates);

            Tally moneys = new Tally();
            for (WorldData.Nation nation : d.nations()) {
                if (!nation.isNation()) continue;
                String in2000 = d.moneyAt(nation.number(), FILE_MONTH_ZERO);
                boolean ok = in2000 != null && nation.moneyMonths()[0] == 0 && in2000.equals(nation.moneyCodes()[0]);
                for (int t = 0; t < FILE_MONTH_ZERO; t++) ok &= in2000 != null && in2000.equals(d.moneyAt(nation.number(), t));
                moneys.see(ok, nation.iso3() + " is on " + d.moneyAt(nation.number(), 0) + " in month 0 and " + in2000 + " in 2000-01");
            }
            tally(s.label("a nation's money at month 0, and in every month before the file's, is the money it had in 2000-01 (the first entry of its history covers them)"), moneys);

            each(s, "the file's own first year reads as the expect file says, the same calendar year: every " + WorldData.FILE_FIRST_YEAR + " path line", "path", f -> {
                if (Integer.parseInt(f[2]) != WorldData.FILE_FIRST_YEAR) return null;
                int nation = nation(d, f[1]);
                return firstOf(differs("value", WorldData.number(f[3]), d.pathValue(f[0], nation, WorldData.FILE_FIRST_YEAR)), differs("mark", f[4], String.valueOf(d.pathMark(f[0], nation, WorldData.FILE_FIRST_YEAR))));
            });
            each(s, "...and its first month, 2000-01: every rate line at the file's month 0", "rate", f -> {
                if (month(f[1]) != FILE_MONTH_ZERO) return null;
                int money = d.moneyNumber(f[0]);
                return firstOf(differs("value", WorldData.number(f[2]), d.rate(money, FILE_MONTH_ZERO)), differs("mark", f[3], String.valueOf(d.rateMark(money, FILE_MONTH_ZERO))));
            });
        }
        theStandInOnAHole(scopes.get(0), fixtureBytes);
    }

    /**
     * The conditions the fixture does not have, caused: the fixture's bytes with cells made NaN (a row with no figure at all, a row whose first figure is
     * later than the file's first year, for a real and for a nominal series, for a good's price and for a money's rate), read again. Every other cell must read
     * as the unpatched file's.
     */
    static void theStandInOnAHole(Scope fixture, byte[] fixtureBytes) throws Exception {
        WorldData whole = fixture.data;
        int first = WorldData.FILE_FIRST_YEAR, last = PEOPLE_LAST;
        // Paths: cpi of 1 and gdp_usd of 4 and people of 5 with no figure at all; gdp_ppp of 2 from 2005; cpi of 3 from 2003.
        byte[] cut = withLayer(fixtureBytes, WorldData.PATHS, raw -> {
            noPaths(raw, "cpi", 1, first, last);
            noPaths(raw, "gdp_usd", 4, first, last);
            noPaths(raw, "people", 5, first, last);
            noPaths(raw, "gdp_ppp", 2, first, 2004);
            noPaths(raw, "cpi", 3, first, 2002);
            return raw;
        });
        // Prices: good 1 with no figure at all, good 2 from 2003.
        cut = withLayer(cut, WorldData.PRICES, raw -> {
            noPrices(raw, 1, first, FIGURES_LAST);
            noPrices(raw, 2, first, 2002);
            return raw;
        });
        // Rates: the first money that has a figure two years in, from 2002-01; the next money with a figure, none at all.
        int slow = 0, gone = 0;
        for (WorldData.Money m : whole.moneys()) {
            if (slow == 0 && !Double.isNaN(whole.rate(m.number(), FILE_MONTH_ZERO)) && !Double.isNaN(whole.rate(m.number(), FILE_MONTH_ZERO + 24))) slow = m.number();
            else if (gone == 0 && !Double.isNaN(whole.rate(m.number(), FILE_MONTH_ZERO))) gone = m.number();
        }
        assertTrue(fixture.label("the fixture has a money to make late and one to make none (so the cases below are caused)"), slow > 0 && gone > 0);
        final int lateMoney = slow, noMoney = gone;
        cut = withLayer(cut, WorldData.MONEYS, raw -> {
            noRates(raw, lateMoney, 0, 23);
            noRates(raw, noMoney, 0, LAST_MONTH - FILE_MONTH_ZERO);
            return raw;
        });
        WorldData d = WorldData.of(cut);

        Tally nothing = new Tally(), late = new Tally(), carriedLate = new Tally(), others = new Tally();
        String[] noneSeries = {"cpi", "gdp_usd", "people"};
        int[] noneNation = {1, 4, 5};
        for (int row = 0; row < noneSeries.length; row++) {
            String series = noneSeries[row];
            int nation = noneNation[row];
            for (int y = d.pathFirstYear(); y < d.pathFirstYear() + d.pathYears(); y++) {
                double v = d.pathValue(series, nation, y);
                nothing.see(Double.isNaN(v) && d.pathMark(series, nation, y) == WorldData.MARK_NONE, series + " " + nation + " " + y + " " + v + " mark " + d.pathMark(series, nation, y));
            }
        }
        tally(fixture.label("a row with no figure at all (a nominal and a real one) stays NaN, marked none, through the lead and the file's years"), nothing);
        double ppp2005 = d.pathValue("gdp_ppp", 2, 2005);
        for (int y = d.pathFirstYear(); y < 2005; y++) {
            double v = d.pathValue("gdp_ppp", 2, y);
            boolean inLead = y < first;
            late.see(inLead ? same(v, ppp2005) && d.pathMark("gdp_ppp", 2, y) == WorldData.MARK_ESTIMATE : Double.isNaN(v) && d.pathMark("gdp_ppp", 2, y) == WorldData.MARK_NONE, "gdp_ppp 2 " + y + " " + v);
        }
        late.see(ppp2005 == whole.pathValue("gdp_ppp", 2, 2005) && d.pathMark("gdp_ppp", 2, 2005) == whole.pathMark("gdp_ppp", 2, 2005), "gdp_ppp 2 in 2005 is the file's");
        tally(fixture.label("a real row whose first figure is 2005's holds it through the lead; its " + first + " to 2004 stay NaN, marked none, and 2005 is the file's"), late);
        double cpi2003 = d.pathValue("cpi", 3, 2003);
        for (int y = d.pathFirstYear(); y < 2003; y++) {
            double v = d.pathValue("cpi", 3, y);
            boolean inLead = y < first;
            carriedLate.see(inLead ? rel(v, carried(cpi2003, 2003, y)) <= LEAD_TOL && d.pathMark("cpi", 3, y) == WorldData.MARK_ESTIMATE : Double.isNaN(v) && d.pathMark("cpi", 3, y) == WorldData.MARK_NONE, "cpi 3 " + y + " " + v);
        }
        carriedLate.see(cpi2003 == whole.pathValue("cpi", 3, 2003), "cpi 3 in 2003 is the file's");
        tally(fixture.label("a nominal row whose first figure is 2003's is that figure on the US price level through the lead; its " + first + " to 2002 stay NaN (Nations.filled's to fill)"), carriedLate);

        Tally priced = new Tally();
        int g1 = 1, g2 = 2;
        for (int y = d.priceFirstYear(); y < d.priceFirstYear() + d.priceYears(); y++) {
            priced.see(Double.isNaN(d.price(g1, y)) && d.priceMark(g1, y) == WorldData.MARK_NONE, "good 1 " + y + " " + d.price(g1, y));
            double v = d.price(g2, y);
            if (y < first) priced.see(same(v, d.price(g2, 2003)) && d.priceMark(g2, y) == WorldData.MARK_ESTIMATE, "good 2 " + y + " " + v);
            else if (y < 2003) priced.see(Double.isNaN(v) && d.priceMark(g2, y) == WorldData.MARK_NONE, "good 2 " + y + " " + v);
        }
        tally(fixture.label("a good with no price stays NaN through the lead; a good whose first is 2003's holds it through the lead and is NaN 2000 to 2002"), priced);

        Tally moneyRates = new Tally();
        double late2002 = d.rate(lateMoney, FILE_MONTH_ZERO + 24);
        for (int t = 0; t < FILE_MONTH_ZERO + 24; t++) {
            double v = d.rate(lateMoney, t);
            if (t < FILE_MONTH_ZERO) moneyRates.see(same(v, late2002) && d.rateMark(lateMoney, t) == WorldData.MARK_ESTIMATE, "late money " + WorldData.monthLabel(t) + " " + v);
            else moneyRates.see(Double.isNaN(v) && d.rateMark(lateMoney, t) == WorldData.MARK_NONE, "late money " + WorldData.monthLabel(t) + " " + v);
        }
        for (int t = d.firstMonth(); t < d.firstMonth() + d.months(); t++) {
            moneyRates.see(Double.isNaN(d.rate(noMoney, t)) && d.rateMark(noMoney, t) == WorldData.MARK_NONE, "money with none " + WorldData.monthLabel(t));
        }
        tally(fixture.label("a money first quoted in 2002-01 holds that rate through the lead and is NaN to 2001-12; a money with no rate is NaN in every month"), moneyRates);

        // Everything else is as the fixture reads it: the lead made from the cells that were not cut, and the file's years.
        for (String series : whole.pathSeries()) {
            for (int nation = 1; nation <= whole.nationCount(); nation++) {
                boolean cutRow = nation == 1 && series.equals("cpi") || nation == 4 && series.equals("gdp_usd") || nation == 5 && series.equals("people")
                        || nation == 2 && series.equals("gdp_ppp") || nation == 3 && series.equals("cpi");
                if (cutRow) continue;
                for (int y = whole.pathFirstYear(); y < whole.pathFirstYear() + whole.pathYears(); y++) {
                    others.see(same(whole.pathValue(series, nation, y), d.pathValue(series, nation, y)) && whole.pathMark(series, nation, y) == d.pathMark(series, nation, y), series + " " + nation + " " + y);
                }
            }
        }
        for (int good = 3; good <= whole.goodCount(); good++) {
            for (int y = whole.priceFirstYear(); y < whole.priceFirstYear() + whole.priceYears(); y++) others.see(same(whole.price(good, y), d.price(good, y)), "good " + good + " " + y);
        }
        tally(fixture.label("the rows that were not cut read as the fixture's, the lead and the file's years alike"), others);
    }

    /** A short-prefixed UTF-8 string, as the layers write names. */
    static String utf(java.nio.ByteBuffer b) {
        byte[] raw = new byte[b.getShort() & 0xFFFF];
        b.get(raw);
        return new String(raw, StandardCharsets.UTF_8);
    }

    /** A paths layer's inflated bytes with a series' figures for a nation (1-based) made NaN, mark 0, in the years from to to. */
    static void noPaths(byte[] raw, String series, int nation, int from, int to) {
        java.nio.ByteBuffer b = java.nio.ByteBuffer.wrap(raw);
        int nations = b.getInt(), firstYear = b.getInt(), years = b.getInt(), count = b.getInt(), which = -1;
        for (int i = 0; i < count; i++) {
            if (utf(b).equals(series)) which = i;
            utf(b);
        }
        int values = b.position(), marks = values + count * nations * years * 8;
        for (int y = from; y <= to; y++) {
            int cell = which * nations * years + (nation - 1) * years + (y - firstYear);
            b.putDouble(values + 8 * cell, Double.NaN);
            raw[marks + cell] = 0;
        }
    }

    /** A prices layer's inflated bytes with a good's prices (1-based) made NaN, mark 0, in the years from to to. */
    static void noPrices(byte[] raw, int good, int from, int to) {
        java.nio.ByteBuffer b = java.nio.ByteBuffer.wrap(raw);
        int goods = b.getInt(), firstYear = b.getInt(), years = b.getInt();
        int[] at = new int[goods];
        for (int i = 0; i < goods; i++) {
            utf(b);
            at[i] = b.position();
            b.position(b.position() + 8 * years);
        }
        int marks = b.position();
        for (int y = from; y <= to; y++) {
            b.putDouble(at[good - 1] + 8 * (y - firstYear), Double.NaN);
            raw[marks + (good - 1) * years + (y - firstYear)] = 0;
        }
    }

    /** A moneys layer's inflated bytes with a money's rates (1-based) made NaN, mark 0, in the file's months from to to (the file's count, month 0 being 2000-01). */
    static void noRates(byte[] raw, int money, int from, int to) {
        java.nio.ByteBuffer b = java.nio.ByteBuffer.wrap(raw);
        int count = b.getInt(), firstMonth = b.getInt(), months = b.getInt();
        for (int i = 0; i < count; i++) {
            utf(b);
            utf(b);
        }
        int values = b.position(), marks = values + count * months * 8;
        for (int t = from; t <= to; t++) {
            int cell = (money - 1) * months + (t - firstMonth);
            b.putDouble(values + 8 * cell, Double.NaN);
            raw[marks + cell] = 0;
        }
    }

    /**
     * A file's bytes with one whole layer's inflated bytes changed: the new blob is appended to the file and the layer's header entry points at it (the old blob
     * is left where it was, unread). A fixture the harness cuts itself, so that a condition the fixture does not have is caused.
     */
    static byte[] withLayer(byte[] file, String layer, java.util.function.UnaryOperator<byte[]> change) throws Exception {
        java.nio.ByteBuffer b = java.nio.ByteBuffer.wrap(file);
        b.position(8 + 4);   // the magic and the layout's version
        int layers = b.getInt();
        for (int k = 0; k < layers; k++) {
            String name = utf(b);
            b.get();         // kind
            b.getInt();      // cells a degree
            b.getLong();     // index offset
            b.getInt();      // index length
            int at = b.position();
            long offset = b.getLong(), length = b.getLong();
            if (!name.equals(layer)) continue;
            byte[] changed = change.apply(EarthData.inflate(Arrays.copyOfRange(file, (int) offset, (int) (offset + length)), 0));
            java.util.zip.Deflater deflater = new java.util.zip.Deflater();
            deflater.setInput(changed);
            deflater.finish();
            java.io.ByteArrayOutputStream blob = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            while (!deflater.finished()) blob.write(buffer, 0, deflater.deflate(buffer));
            deflater.end();
            byte[] out = Arrays.copyOf(file, file.length + blob.size());
            System.arraycopy(blob.toByteArray(), 0, out, file.length, blob.size());
            java.nio.ByteBuffer patch = java.nio.ByteBuffer.wrap(out);
            patch.putLong(at, file.length);
            patch.putLong(at + 8, blob.size());
            return out;
        }
        throw new IllegalArgumentException("the file has no layer " + layer);
    }
}
