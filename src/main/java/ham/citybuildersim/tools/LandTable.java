package ham.citybuildersim.tools;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ham.citybuildersim.LandFactors;
import ham.citybuildersim.WorldData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Makes src/main/resources/world/land-2024.json, the table LandFactors reads: every nation's land a hectare, in US dollars of 2024, from the
 * published farmland prices of tools/world/land.tsv (Eurostat's, the USDA's, Statistics Canada's) and, for the nations that publish none, a fit.
 *
 * WHY. Jerus: "land costs different prices purely by who you are buying it from" (spec-nations 6.3). Three statistical offices publish a price of
 * farmland, which is 25 of the world file's 196 nations; the other 171 need one too, and a guess for each by hand would be 171 guesses. A fit on what is
 * published (ln of the price against ln of the nation's output a head and ln of its people a square kilometre, ordinary least squares over the 25) gives
 * every nation a figure that moves the right way with how rich and how crowded it is, and is marked 1, the game's estimate, against the 3 of a
 * published one. A fitted nation is never dearer than the dearest nation that publishes (CEILING): the city-states, Singapore, Monaco, Macau and Hong
 * Kong, would otherwise run to 30 to 200 times Canada's price, from an elasticity read off 25 nations none of which is a city.
 *
 * Reads tools/world/land.tsv, the world file (WorldData.locate(), or the first argument) for each nation's output a head in 2024 (gdp_usd over people)
 * and src/main/resources/world/world-2025.json for its people a square kilometre. Run it from the repository root; same input, same bytes.
 */
public final class LandTable {

    private LandTable() { }

    /** The published prices, one row each, and their rates and sources. */
    static final String TSV = "tools/world/land.tsv";

    /** The rankings' file, whose places carry each nation's density (people a square kilometre). */
    static final String RANKINGS = "src/main/resources/world/world-2025.json";

    /** The table it writes. */
    static final String OUT = "src/main/resources" + LandFactors.RESOURCE;

    /** Acres in a hectare: a published price an acre times it is the price a hectare. */
    public static final double ACRES_PER_HECTARE = 2.47105381;

    /** The mark of a published figure, and of the game's estimate (WorldData.MARK_PUBLISHED, MARK_ESTIMATE). */
    static final int PUBLISHED = WorldData.MARK_PUBLISHED, ESTIMATE = WorldData.MARK_ESTIMATE;

    /** One published row of the tsv. */
    record Row(String iso3, double figure, String money, String per, int year, String source) { }

    /** One nation's line of the table, before it is written. */
    static final class Line {
        final int number;
        final String iso3, name;
        double usdHa = Double.NaN, outputPp = Double.NaN, density = Double.NaN;
        int mark;
        String source;
        Row row;

        Line(int number, String iso3, String name) {
            this.number = number;
            this.iso3 = iso3;
            this.name = name;
        }
    }

    public static void main(String[] args) throws IOException {
        Path worldFile = args.length > 0 ? Path.of(args[0]) : WorldData.locate();
        if (worldFile == null) throw new IOException("no world file: name one on the command line, or with -D" + WorldData.PROPERTY);
        Path out = args.length > 1 ? Path.of(args[1]) : Path.of(OUT);
        try (WorldData file = WorldData.open(worldFile)) {
            run(file, Path.of(TSV), Path.of(RANKINGS), out);
        }
    }

    static void run(WorldData file, Path tsv, Path rankings, Path out) throws IOException {
        List<Row> rows = new ArrayList<>();
        Map<String, Double> usdPerUnit = new LinkedHashMap<>();
        Map<String, String> rateRows = new LinkedHashMap<>();
        Map<String, Double> area = new HashMap<>();
        List<String[]> sources = new ArrayList<>();
        readTsv(tsv, rows, usdPerUnit, rateRows, area, sources);

        Map<String, Double> density = readDensity(rankings);
        int n = file.nationCount();
        List<Line> lines = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            WorldData.Nation nation = file.nation(i);
            Line line = new Line(i, nation.iso3(), nation.name());
            double people = file.pathValue("people", i, LandFactors.BASE_YEAR), gdp = file.pathValue("gdp_usd", i, LandFactors.BASE_YEAR);
            if (people > 0 && gdp > 0 && Double.isFinite(people) && Double.isFinite(gdp)) line.outputPp = gdp / people;
            Double d = density.get(line.iso3);
            if (d != null) line.density = d;
            else if (area.containsKey(line.iso3) && people > 0) line.density = people / area.get(line.iso3);
            lines.add(line);
        }
        Map<String, Line> byIso = new HashMap<>();
        for (Line line : lines) byIso.put(line.iso3, line);
        for (Row row : rows) {
            Line line = byIso.get(row.iso3());
            if (line == null) throw new IOException(TSV + ": " + row.iso3() + " is not a nation of the world file");
            if (line.row != null) throw new IOException(TSV + ": " + row.iso3() + " is published twice");
            Double rate = row.money().equals("USD") ? Double.valueOf(1) : usdPerUnit.get(row.money());
            if (rate == null) throw new IOException(TSV + ": no #rate line for " + row.money());
            line.row = row;
            line.usdHa = round2(row.figure() * rate * (row.per().equals("acre") ? ACRES_PER_HECTARE : 1));
            line.mark = PUBLISHED;
            line.source = row.source();
        }
        Line ref = byIso.get(LandFactors.REFERENCE);
        if (ref == null || ref.row == null) throw new IOException(TSV + " does not publish the reference nation " + LandFactors.REFERENCE);

        // The fit: ln usd_ha on [1, ln output a head, ln density] over the nations that publish and have both figures.
        List<Line> basis = new ArrayList<>();
        double dearest = 0;
        for (Line line : lines) {
            if (line.row == null) continue;
            dearest = Math.max(dearest, line.usdHa);
            if (usable(line)) basis.add(line);
        }
        Fit fit = Fit.of(basis);

        int none = 0;
        for (Line line : lines) {
            if (line.row != null) continue;
            if (usable(line)) {
                line.usdHa = round2(Math.min(dearest, Math.exp(fit.a + fit.output * Math.log(line.outputPp) + fit.density * Math.log(line.density))));
                line.mark = ESTIMATE;
                line.source = "fit";
            } else {
                line.usdHa = ref.usdHa;
                line.mark = ESTIMATE;
                line.source = "none";
                none++;
                System.out.println("land table: " + line.iso3 + " (" + line.name + ") lacks "
                        + (Double.isNaN(line.outputPp) ? "output a head" : "") + (Double.isNaN(line.outputPp) && Double.isNaN(line.density) ? " and " : "")
                        + (Double.isNaN(line.density) ? "people a square kilometre" : "") + ": Canada's price");
            }
        }

        write(out, lines, fit, dearest, usdPerUnit, rateRows, sources);
        summary(lines, fit, dearest, ref.usdHa, none, out);
    }

    private static boolean usable(Line line) {
        return line.outputPp > 0 && line.density > 0 && Double.isFinite(line.outputPp) && Double.isFinite(line.density);
    }

    /* =====================================================================
       READING
       ===================================================================== */

    private static void readTsv(Path tsv, List<Row> rows, Map<String, Double> usdPerUnit, Map<String, String> rateRows, Map<String, Double> area,
                                List<String[]> sources) throws IOException {
        int number = 0;
        for (String line : Files.readAllLines(tsv, StandardCharsets.UTF_8)) {
            number++;
            if (line.isBlank()) continue;
            String[] f = line.split("\t", -1);
            String where = TSV + " line " + number;
            if (f[0].equals("#rate")) {
                if (f.length != 6) throw new IOException(where + ": a #rate line has six fields");
                double v = Double.parseDouble(f[2]);
                if (!(v > 0)) throw new IOException(where + ": no rate");
                double perUnit = switch (f[3]) {
                    case "usd-per-unit" -> v;
                    case "units-per-usd" -> 1 / v;
                    default -> throw new IOException(where + ": direction " + f[3]);
                };
                usdPerUnit.put(f[1], perUnit);
                rateRows.put(f[1], f[1] + "\t" + f[2] + "\t" + f[3] + "\t" + f[4] + "\t" + f[5]);
            } else if (f[0].equals("#area")) {
                if (f.length != 4) throw new IOException(where + ": an #area line has four fields");
                area.put(f[1], Double.parseDouble(f[2]));
            } else if (f[0].equals("#source")) {
                if (f.length != 6) throw new IOException(where + ": a #source line has six fields");
                sources.add(new String[]{f[1], f[2], f[3], f[4], f[5]});
            } else if (!f[0].startsWith("#")) {
                if (f.length != 6) throw new IOException(where + ": a row has six fields");
                String per = f[3];
                if (!per.equals("ha") && !per.equals("acre")) throw new IOException(where + ": per " + per);
                rows.add(new Row(f[0], Double.parseDouble(f[1]), f[2], per, Integer.parseInt(f[4]), f[5]));
            }
        }
    }

    /** The rankings' people a square kilometre by iso3 (a place's id): the first of its "density" figure. */
    private static Map<String, Double> readDensity(Path rankings) throws IOException {
        JsonObject root = JsonParser.parseString(Files.readString(rankings, StandardCharsets.UTF_8)).getAsJsonObject();
        Map<String, Double> out = new HashMap<>();
        for (JsonElement e : root.getAsJsonArray("places")) {
            JsonObject place = e.getAsJsonObject();
            JsonObject f = place.getAsJsonObject("f");
            if (f == null || !f.has("density")) continue;
            double d = f.getAsJsonArray("density").get(0).getAsDouble();
            if (d > 0 && Double.isFinite(d)) out.put(place.get("id").getAsString(), d);
        }
        return out;
    }

    /* =====================================================================
       THE FIT
       ===================================================================== */

    /** Ordinary least squares of ln usd_ha on [1, ln output a head, ln density]: the three coefficients, R squared, the residual standard error, how many nations. */
    record Fit(double a, double output, double density, double r2, double sd, int n) {

        static Fit of(List<Line> basis) {
            int n = basis.size();
            if (n < 4) throw new IllegalStateException("land table: " + n + " nations to fit on");
            double[][] m = new double[3][4];
            double[] ys = new double[n];
            double[][] xs = new double[n][];
            for (int i = 0; i < n; i++) {
                Line l = basis.get(i);
                xs[i] = new double[]{1, Math.log(l.outputPp), Math.log(l.density)};
                ys[i] = Math.log(l.usdHa);
                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 3; c++) m[r][c] += xs[i][r] * xs[i][c];
                    m[r][3] += xs[i][r] * ys[i];
                }
            }
            double[] b = solve(m);
            double mean = 0;
            for (double y : ys) mean += y / n;
            double ssRes = 0, ssTot = 0;
            for (int i = 0; i < n; i++) {
                double hat = b[0] * xs[i][0] + b[1] * xs[i][1] + b[2] * xs[i][2];
                ssRes += (ys[i] - hat) * (ys[i] - hat);
                ssTot += (ys[i] - mean) * (ys[i] - mean);
            }
            return new Fit(b[0], b[1], b[2], 1 - ssRes / ssTot, Math.sqrt(ssRes / (n - 3)), n);
        }

        /** Gaussian elimination with partial pivoting on a 3 x 4 augmented matrix. */
        private static double[] solve(double[][] m) {
            for (int col = 0; col < 3; col++) {
                int pivot = col;
                for (int r = col + 1; r < 3; r++) if (Math.abs(m[r][col]) > Math.abs(m[pivot][col])) pivot = r;
                if (Math.abs(m[pivot][col]) < 1e-12) throw new IllegalStateException("land table: the fit's normal equations are singular");
                double[] t = m[col];
                m[col] = m[pivot];
                m[pivot] = t;
                for (int r = col + 1; r < 3; r++) {
                    double k = m[r][col] / m[col][col];
                    for (int c = col; c < 4; c++) m[r][c] -= k * m[col][c];
                }
            }
            double[] b = new double[3];
            for (int r = 2; r >= 0; r--) {
                double s = m[r][3];
                for (int c = r + 1; c < 3; c++) s -= m[r][c] * b[c];
                b[r] = s / m[r][r];
            }
            return b;
        }
    }

    /* =====================================================================
       WRITING
       ===================================================================== */

    private static double round2(double v) { return Math.round(v * 100) / 100.0; }

    private static double round3(double v) { return Math.round(v * 1000) / 1000.0; }

    /** The table's text: the meta apart and each nation on a line of its own, so a diff of a changed figure is one line. */
    private static void write(Path out, List<Line> lines, Fit fit, double ceiling, Map<String, Double> usdPerUnit, Map<String, String> rateRows,
                              List<String[]> sources) throws IOException {
        JsonObject meta = new JsonObject();
        meta.addProperty("name", "land-2024");
        meta.addProperty("year", LandFactors.BASE_YEAR);
        meta.addProperty("reference", LandFactors.REFERENCE);
        meta.addProperty("unit", "US$ a hectare, " + LandFactors.BASE_YEAR);
        meta.addProperty("acres_per_hectare", ACRES_PER_HECTARE);
        JsonObject f = new JsonObject();
        f.addProperty("a", fit.a());
        f.addProperty("output", fit.output());
        f.addProperty("density", fit.density());
        f.addProperty("r2", fit.r2());
        f.addProperty("n", fit.n());
        f.addProperty("sd", fit.sd());
        meta.add("fit", f);
        meta.addProperty("ceiling", ceiling);
        JsonArray rates = new JsonArray();
        for (Map.Entry<String, Double> e : usdPerUnit.entrySet()) {
            String[] r = rateRows.get(e.getKey()).split("\t");
            JsonObject o = new JsonObject();
            o.addProperty("money", r[0]);
            o.addProperty("usd_per_unit", e.getValue());
            o.addProperty("given", Double.parseDouble(r[1]));
            o.addProperty("direction", r[2]);
            o.addProperty("year", Integer.parseInt(r[3]));
            o.addProperty("source", r[4]);
            rates.add(o);
        }
        meta.add("rates", rates);
        JsonArray src = new JsonArray();
        for (String[] s : sources) {
            JsonObject o = new JsonObject();
            o.addProperty("id", s[0]);
            o.addProperty("name", s[1]);
            o.addProperty("url", s[2]);
            o.addProperty("licence", s[3]);
            o.addProperty("downloaded", s[4]);
            src.add(o);
        }
        meta.add("sources", src);

        Gson gson = new com.google.gson.GsonBuilder().disableHtmlEscaping().create();
        StringBuilder sb = new StringBuilder();
        sb.append("{\n\"meta\": ").append(new com.google.gson.GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(meta)).append(",\n\"nations\": [\n");
        for (int i = 0; i < lines.size(); i++) {
            Line l = lines.get(i);
            JsonObject o = new JsonObject();
            o.addProperty("iso3", l.iso3);
            o.addProperty("name", l.name);
            o.addProperty("usd_ha", l.usdHa);
            o.addProperty("mark", l.mark);
            o.addProperty("source", l.source);
            if (l.row != null) {
                o.addProperty("figure", l.row.figure());
                o.addProperty("money", l.row.money());
                o.addProperty("per", l.row.per());
                o.addProperty("year", l.row.year());
            } else if (l.source.equals("fit")) {
                o.addProperty("output_pp", round2(l.outputPp));
                o.addProperty("density", round3(l.density));
            }
            sb.append(gson.toJson(o)).append(i + 1 < lines.size() ? ",\n" : "\n");
        }
        sb.append("]\n}\n");
        Files.createDirectories(out.toAbsolutePath().getParent());
        Files.writeString(out, sb.toString(), StandardCharsets.UTF_8);
    }

    private static void summary(List<Line> lines, Fit fit, double ceiling, double reference, int none, Path out) {
        Map<String, Integer> bySource = new LinkedHashMap<>();
        for (Line l : lines) bySource.merge(l.source.equals("fit") || l.source.equals("none") ? l.source : "published (" + l.source + ")", 1, Integer::sum);
        System.out.printf(Locale.ROOT, "land table: fit ln(usd_ha) = %.4f + %.4f ln(output a head) + %.4f ln(density); R2 %.3f, sd %.3f, n %d; ceiling US$ %.0f (%.1f x Canada)%n",
                fit.a(), fit.output(), fit.density(), fit.r2(), fit.sd(), fit.n(), ceiling, ceiling / reference);
        System.out.println("land table: " + lines.size() + " nations: " + bySource + (none > 0 ? ", " + none + " on Canada's price" : ""));
        List<Line> sorted = new ArrayList<>(lines);
        sorted.sort((a, b) -> Double.compare(a.usdHa, b.usdHa));
        StringBuilder low = new StringBuilder(), high = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            Line l = sorted.get(i), h = sorted.get(sorted.size() - 1 - i);
            low.append(String.format(Locale.ROOT, " %s %.3f", l.iso3, l.usdHa / reference));
            high.append(String.format(Locale.ROOT, " %s %.2f", h.iso3, h.usdHa / reference));
        }
        System.out.println("land table: cheapest against Canada:" + low);
        System.out.println("land table: dearest against Canada:" + high);
        System.out.println("land table: wrote " + out);
    }
}
