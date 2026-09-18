package ham.citybuildersim.tools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ham.citybuildersim.GameFiles;
import ham.citybuildersim.SaveHeader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Looks inside a save without loading the game - or reading the file.
 *
 *     java -cp "target/classes;gson.jar" ham.citybuildersim.tools.SaveDump 3
 *     java -cp "target/classes;gson.jar" ham.citybuildersim.tools.SaveDump 3 sectors
 *     java -cp "target/classes;gson.jar" ham.citybuildersim.tools.SaveDump 3 sectors.Bank.deposits
 *     java -cp "target/classes;gson.jar" ham.citybuildersim.tools.SaveDump path\to\any.json people
 *
 * A slot's save is megabytes of JSON, which is exactly the kind of file an
 * assistant cannot open and a player cannot read. The first form prints
 * the header (version, format, month, cash, population) and every top-level
 * key with its shape - a number, a string, an object of N keys, an array of
 * N - so the reader can see what the save carries. Each further dotted key
 * descends one level, and an array index is a number ("sectors.3"). Arrays
 * of numbers print their length, first, last, min, max and sum, which is
 * usually the question.
 *
 * Slot numbers resolve through GameFiles to the real saves folder, so this
 * reads the player's actual city; it never writes anything. Slots 1-9 are
 * Jerus's own cities and slot 10 is the assistant's, by standing agreement,
 * and this tool cannot tell them apart - it only reads.
 */
public final class SaveDump {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("usage: SaveDump <slot 1-10 | path.json> [key.key.index]");
            return;
        }
        Path file;
        String what = args[0];
        if (what.matches("\\d{1,2}")) {
            int slot = Integer.parseInt(what);
            GameFiles files = new GameFiles();
            file = files.saveFile(slot);
            SaveHeader h = files.readHeader(slot);
            if (h != null) {
                System.out.printf("slot %d  \"%s\"  %s (format %d)  month %d  cash %,.0f  population %,d%n",
                        slot, h.getSlotName(), h.getGameVersion(), h.getSaveFormat(), h.getMonth(), h.getCash(), h.getPopulation());
            }
        } else {
            file = Paths.get(what);
        }
        if (!Files.exists(file)) { System.out.println("no such file: " + file.toAbsolutePath()); return; }
        System.out.printf("%s  (%,d bytes)%n%n", file.toAbsolutePath(), Files.size(file));

        JsonElement root;
        try (var r = Files.newBufferedReader(file)) { root = JsonParser.parseReader(r); }
        JsonElement at = root;
        String trail = "";
        if (args.length > 1) {
            for (String key : args[1].split("\\.")) {
                if (at.isJsonObject() && at.getAsJsonObject().has(key)) at = at.getAsJsonObject().get(key);
                else if (at.isJsonArray() && key.matches("\\d+") && Integer.parseInt(key) < at.getAsJsonArray().size()) at = at.getAsJsonArray().get(Integer.parseInt(key));
                else { System.out.println("no key '" + key + "' under " + (trail.isEmpty() ? "the root" : trail) + "; the keys there are:"); list(at, "  "); return; }
                trail = trail.isEmpty() ? key : trail + "." + key;
            }
            System.out.println(trail + ":");
        }
        list(at, "  ");
    }

    static void list(JsonElement e, String indent) {
        if (e.isJsonObject()) {
            JsonObject o = e.getAsJsonObject();
            for (Map.Entry<String, JsonElement> en : o.entrySet()) {
                System.out.printf("%s%-34s %s%n", indent, en.getKey(), shape(en.getValue()));
            }
        } else if (e.isJsonArray()) {
            JsonArray a = e.getAsJsonArray();
            System.out.printf("%sarray of %d: %s%n", indent, a.size(), shape(e));
            int shown = 0;
            for (JsonElement x : a) {
                if (shown++ == 12) { System.out.printf("%s  ... %d more%n", indent, a.size() - 12); break; }
                System.out.printf("%s  [%d] %s%n", indent, shown - 1, shape(x));
            }
        } else {
            System.out.println(indent + e);
        }
    }

    /** One line describing a value: its type, size and, for numbers, the number. */
    static String shape(JsonElement e) {
        if (e == null || e.isJsonNull()) return "null";
        if (e.isJsonPrimitive()) {
            String s = e.toString();
            return s.length() > 80 ? s.substring(0, 77) + "..." : s;
        }
        if (e.isJsonObject()) {
            JsonObject o = e.getAsJsonObject();
            StringBuilder sb = new StringBuilder("{" + o.size() + " keys");
            int n = 0;
            for (String k : o.keySet()) { if (n++ == 6) { sb.append(", ..."); break; } sb.append(n == 1 ? ": " : ", ").append(k); }
            return sb.append("}").toString();
        }
        JsonArray a = e.getAsJsonArray();
        if (a.size() == 0) return "[]";
        boolean numeric = true;
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY, sum = 0;
        for (JsonElement x : a) {
            if (!x.isJsonPrimitive() || !x.getAsJsonPrimitive().isNumber()) { numeric = false; break; }
            double v = x.getAsDouble(); min = Math.min(min, v); max = Math.max(max, v); sum += v;
        }
        if (numeric) {
            return String.format("[%d numbers: first %s, last %s, min %s, max %s, sum %s]", a.size(),
                    a.get(0), a.get(a.size() - 1), num(min), num(max), num(sum));
        }
        JsonElement first = a.get(0);
        if (first.isJsonObject()) return "[" + a.size() + " objects like " + shape(first) + "]";
        if (first.isJsonArray()) return "[" + a.size() + " arrays, first " + shape(first) + "]";
        return "[" + a.size() + " values, first " + shape(first) + "]";
    }

    static String num(double v) {
        if (v == Math.rint(v) && Math.abs(v) < 1e15) return String.format("%,d", (long) v);
        return String.format("%,.4f", v);
    }
}
