package ham.citybuildersim;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * One recorded city on disk (TR1, 2026-10-31): the folder {@code recordings/<id>/} that "Record this city" keeps, and the one
 * place that knows its files. A month is one file ({@code month-0000123.bin}: a head, the map's sidecar as the game made it,
 * then the save as compact JSON, gzipped), the history one more ({@code history.json.gz}), the meta
 * ({@code recording.json}) says whose it is, and each sitting's input is a log ({@code input-<epochMs>.bin.gz}, InputLog).
 * Reading is by month and cheap: the map comes first in the file, so a quick step reads only the head of it. Writing is two
 * {@link SaveWriter.Task}s built on the FX thread from snapshots and run on the save writer's thread, so a month pays only
 * its fill. No JavaFX.
 *
 * WHY IT EXISTS (TR1)
 *
 * Jerus is filming the game's trailer, and a live take cannot be repeated: a peg breaks by chance
 * ({@code ForeignAccounts.BREAK_BASE}, {@code BREAK_SLOPE}), a collapse comes when it comes, and a big city's month takes
 * time. So the city is played once, reloading earlier saves until the story happens, and every month of the timeline last
 * played is kept; the replay (ReplayCity) shows any month of it on a throwaway copy, as often as he likes. The history is not
 * written every month (his is 6 to 27 MB): it goes at the autosave's pace and when recording stops, and the replay cuts it at
 * the month on screen (HistorySave.cutAt).
 */
public final class Recording {

    /** The first four bytes of a month's file: "CREC". */
    public static final int MAGIC = 0x43524543;

    /** The month file's layout: 1 is the head (magic, format, month, map length), the map's sidecar, then the save's gzipped JSON. */
    public static final int FORMAT = 1;

    /** What a month's file name starts with; the month follows in seven digits, so the folder sorts the way the months run. */
    public static final String MONTH_PREFIX = "month-";

    /** What a month's file name ends with. */
    public static final String MONTH_SUFFIX = ".bin";

    /** The history's file in the folder: its compact JSON, gzipped. */
    public static final String HISTORY_FILE = "history.json.gz";

    /** The meta's file in the folder: who the recording is of, as JSON. */
    public static final String META_FILE = "recording.json";

    /** What a sitting's input log starts with in the folder; the sitting's start in epoch milliseconds follows. */
    public static final String INPUT_PREFIX = "input-";

    /** What a sitting's input log ends with. */
    public static final String INPUT_SUFFIX = ".bin.gz";

    /** How hard the save's JSON is deflated (java.util.zip): the default, 6 - ReplayCheck prints the size and time of 6 and of 1 on a grown city. */
    public static final int DEFLATE_LEVEL = java.util.zip.Deflater.DEFAULT_COMPRESSION;

    /** The longest the name part of an id is, so a long city name does not make a long folder name. */
    static final int ID_NAME_LENGTH = 40;

    /** Who a recording is of: kept in recording.json when the first month is written. {@code earth}: the city stands on the real Earth. */
    public record Meta(String id, String cityName, String version, int saveFormat, long startedAt, boolean earth) { }

    private static final Gson GSON = new Gson();

    private final Path folder;
    private final Meta meta;

    private Recording(Path folder, Meta meta) {
        this.folder = folder;
        this.meta = meta;
    }

    /* ------------------------------ ids and listing ------------------------------ */

    /**
     * A file-safe id for a new recording: the city's name in lower case without accents, its words joined by hyphens, then
     * the moment as {@code yyyyMMdd-HHmmss} - "medellin-20261031-142355". A name with no letters or digits gives "city".
     */
    public static String newId(String cityName, long epochMs) {
        String plain = cityName == null ? "" : Normalizer.normalize(cityName, Normalizer.Form.NFD);
        StringBuilder slug = new StringBuilder();
        boolean gap = false;
        for (int i = 0; i < plain.length(); i++) {
            char c = Character.toLowerCase(plain.charAt(i));
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                if (gap && slug.length() > 0) slug.append('-');
                slug.append(c);
                gap = false;
            } else if (Character.getType(c) != Character.NON_SPACING_MARK) {
                gap = true;
            }
        }
        if (slug.length() > ID_NAME_LENGTH) slug.setLength(ID_NAME_LENGTH);
        while (slug.length() > 0 && slug.charAt(slug.length() - 1) == '-') slug.setLength(slug.length() - 1);
        String name = slug.length() == 0 ? "city" : slug.toString();
        LocalDateTime at = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMs), ZoneId.systemDefault());
        return name + "-" + at.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss", Locale.ROOT));
    }

    /** The recording in a folder; its meta() is null when the folder holds none that can be read. */
    public static Recording at(Path folder) {
        Meta meta = null;
        try {
            Path file = folder.resolve(META_FILE);
            if (Files.isRegularFile(file)) meta = GSON.fromJson(Files.readString(file, StandardCharsets.UTF_8), Meta.class);
        } catch (IOException | RuntimeException e) {
            meta = null;
        }
        return new Recording(folder, meta);
    }

    /** Every folder in {@code recordingsDirectory} that holds a meta, newest started first; empty when the folder is missing. */
    public static List<Recording> all(Path recordingsDirectory) {
        List<Recording> found = new ArrayList<>();
        if (recordingsDirectory == null || !Files.isDirectory(recordingsDirectory)) return found;
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(recordingsDirectory)) {
            for (Path entry : entries) {
                if (!Files.isDirectory(entry)) continue;
                Recording r = at(entry);
                if (r.meta != null) found.add(r);
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("The recordings could not be listed: " + e);
        }
        found.sort(Comparator.comparingLong((Recording r) -> r.meta.startedAt()).reversed().thenComparing(Recording::id));
        return found;
    }

    /* --------------------------------- reading ---------------------------------- */

    public Path folder() { return folder; }

    /** The recording's id: its folder's name. */
    public String id() { return folder.getFileName().toString(); }

    /** Who it is of, or null in a folder with no readable meta. */
    public Meta meta() { return meta; }

    /** The file a month is in; it may not exist. */
    public Path monthFile(int month) {
        return folder.resolve(MONTH_PREFIX + String.format(Locale.ROOT, "%07d", month) + MONTH_SUFFIX);
    }

    /** The months on disk, ascending, from the folder's listing (a .bak or .tmp beside a month is not one). */
    public int[] months() {
        List<Integer> found = new ArrayList<>();
        if (Files.isDirectory(folder)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(folder, MONTH_PREFIX + "*" + MONTH_SUFFIX)) {
                for (Path entry : entries) {
                    int m = monthOf(entry.getFileName().toString());
                    if (m >= 0) found.add(m);
                }
            } catch (IOException | RuntimeException e) {
                System.out.println("The recording's months could not be listed: " + e);
            }
        }
        return found.stream().sorted().mapToInt(Integer::intValue).toArray();
    }

    /** The month a file name stands for, or -1 when it is not exactly a month file's name. */
    static int monthOf(String name) {
        if (!name.startsWith(MONTH_PREFIX) || !name.endsWith(MONTH_SUFFIX)) return -1;
        String digits = name.substring(MONTH_PREFIX.length(), name.length() - MONTH_SUFFIX.length());
        if (digits.length() != 7) return -1;
        for (int i = 0; i < digits.length(); i++) if (digits.charAt(i) < '0' || digits.charAt(i) > '9') return -1;
        return Integer.parseInt(digits);
    }

    /** The first month on disk, or -1 with none. */
    public int firstMonth() {
        int[] m = months();
        return m.length == 0 ? -1 : m[0];
    }

    /** The last month on disk, or -1 with none. */
    public int lastMonth() {
        int[] m = months();
        return m.length == 0 ? -1 : m[m.length - 1];
    }

    /** Every file in the folder, in bytes (the logs and the .bak beside a rewritten month too). */
    public long bytes() {
        long total = 0;
        if (!Files.isDirectory(folder)) return 0;
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(folder)) {
            for (Path entry : entries) if (Files.isRegularFile(entry)) total += Files.size(entry);
        } catch (IOException | RuntimeException e) {
            System.out.println("The recording's size could not be read: " + e);
        }
        return total;
    }

    /**
     * The map sidecar of a month, as the game made it (deflated), or null: no file for the month, the city had no map then, or
     * the file will not read. Reads the head of the file and the map and no more.
     */
    public byte[] mapOf(int month) {
        Path file = monthFile(month);
        if (!Files.isRegularFile(file)) return null;
        try (DataInputStream in = new DataInputStream(new java.io.BufferedInputStream(Files.newInputStream(file)))) {
            int length = readHead(in, month);
            if (length < 0) return null;
            byte[] map = in.readNBytes(length);
            return map.length == length ? map : null;
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    /** The month's save as compact JSON, or null when there is no file for it or it will not read. */
    public String saveJsonOf(int month) {
        Path file = monthFile(month);
        if (!Files.isRegularFile(file)) return null;
        try (DataInputStream in = new DataInputStream(new java.io.BufferedInputStream(Files.newInputStream(file)))) {
            int length = readHead(in, month);
            if (length == -2) return null;
            if (length > 0) in.skipNBytes(length);
            try (GZIPInputStream gz = new GZIPInputStream(in)) {
                return new String(gz.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    /** Reads a month file's head: the map's length (-1: none), or -2 when it is not this recording's format or this month's. */
    private static int readHead(DataInputStream in, int month) throws IOException {
        if (in.readInt() != MAGIC) return -2;
        if (in.readInt() != FORMAT) return -2;
        if (in.readInt() != month) return -2;
        int length = in.readInt();
        return length < 0 ? -1 : length;
    }

    /** The history, parsed, or null when there is none yet or it will not read. */
    public HistorySave history() {
        Path file = folder.resolve(HISTORY_FILE);
        if (!Files.isRegularFile(file)) return null;
        try (InputStream in = new GZIPInputStream(new java.io.BufferedInputStream(Files.newInputStream(file)))) {
            return GSON.fromJson(new String(in.readAllBytes(), StandardCharsets.UTF_8), HistorySave.class);
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    /* --------------------------------- writing ---------------------------------- */

    /**
     * A month to write: built on the FX thread from a snapshot, run on the save writer's thread. It makes the folder, writes the
     * meta when the folder has none, then the month's file (the .tmp and the swap); with {@code dropLater} it then deletes every
     * month file numbered above this one - the rewind: the recording is always the timeline last played, and months past the new
     * one go when the first new month is written. {@code map} is the sidecar's bytes or null for none.
     */
    public static SaveWriter.Task monthTask(GameFiles files, Path folder, Meta meta, int month, DataSave snapshot, byte[] map, boolean dropLater) {
        return new SaveWriter.Task() {
            @Override public Path target() {
                return folder.resolve(MONTH_PREFIX + String.format(Locale.ROOT, "%07d", month) + MONTH_SUFFIX);
            }

            @Override public GameFiles.Result write() {
                Path path = target();
                try {
                    Files.createDirectories(folder);
                } catch (IOException e) {
                    return GameFiles.Result.failed(path, e);
                }
                Path metaFile = folder.resolve(META_FILE);
                if (!Files.isRegularFile(metaFile) && meta != null) {
                    GameFiles.Result metaResult = files.write(metaFile, GSON.toJson(meta));
                    if (!metaResult.ok) return metaResult;
                }
                byte[] bytes;
                try {
                    bytes = monthBytes(snapshot, map, month, DEFLATE_LEVEL);
                } catch (IOException e) {
                    return GameFiles.Result.failed(path, e);
                } catch (RuntimeException e) {
                    return GameFiles.Result.failed(path, "the city's numbers cannot be written down"
                            + snapshot.describeUnwritable() + " (" + e.getClass().getSimpleName() + ")");
                }
                GameFiles.Result written = files.write(path, bytes);
                if (written.ok && dropLater) dropMonthsAfter(folder, month);
                return written;
            }
        };
    }

    /** A month file's bytes: the head, the map, then the save's compact JSON gzipped at {@code level}. A number JSON cannot hold throws the RuntimeException Gson does. */
    static byte[] monthBytes(DataSave snapshot, byte[] map, int month, int level) throws IOException {
        ByteArrayOutputStream raw = new ByteArrayOutputStream(map == null ? 1 << 18 : map.length + (1 << 18));
        DataOutputStream out = new DataOutputStream(raw);
        out.writeInt(MAGIC);
        out.writeInt(FORMAT);
        out.writeInt(month);
        out.writeInt(map == null ? -1 : map.length);
        if (map != null) out.write(map);
        out.flush();
        try (Writer json = new OutputStreamWriter(new Gz(raw, level), StandardCharsets.UTF_8)) {
            GSON.toJson(snapshot, json);
        }
        return raw.toByteArray();
    }

    /** The history to write: built on the FX thread from a snapshot; written whole (the .tmp and the swap), a newer one replacing one still waiting. */
    public static SaveWriter.Task historyTask(GameFiles files, Path folder, HistorySave snapshot) {
        return new SaveWriter.Task() {
            @Override public Path target() {
                return folder.resolve(HISTORY_FILE);
            }

            @Override public GameFiles.Result write() {
                Path path = target();
                try {
                    Files.createDirectories(folder);
                    ByteArrayOutputStream raw = new ByteArrayOutputStream(1 << 20);
                    try (Writer json = new OutputStreamWriter(new Gz(raw, DEFLATE_LEVEL), StandardCharsets.UTF_8)) {
                        GSON.toJson(snapshot, json);
                    }
                    return files.write(path, raw.toByteArray());
                } catch (IOException e) {
                    return GameFiles.Result.failed(path, e);
                } catch (RuntimeException e) {
                    return GameFiles.Result.failed(path, "the history's numbers cannot be written down ("
                            + e.getClass().getSimpleName() + ")");
                }
            }
        };
    }

    /** Deletes the month files numbered above {@code month} (and a .bak or .tmp of one): the timeline last played is the one kept. */
    private static void dropMonthsAfter(Path folder, int month) {
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(folder, MONTH_PREFIX + "*")) {
            for (Path entry : entries) {
                String name = entry.getFileName().toString();
                if (name.length() < MONTH_PREFIX.length() + 7) continue;
                String digits = name.substring(MONTH_PREFIX.length(), MONTH_PREFIX.length() + 7);
                boolean numbered = digits.chars().allMatch(c -> c >= '0' && c <= '9');
                if (!numbered || Integer.parseInt(digits) <= month) continue;
                try {
                    Files.deleteIfExists(entry);
                } catch (IOException e) {
                    System.out.println("A recorded month past the rewind could not be deleted: " + entry + " - " + e);
                }
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("The months past the rewind could not be listed: " + e);
        }
    }

    /** A gzip stream at a chosen deflate level (GZIPOutputStream has no constructor for one). */
    private static final class Gz extends GZIPOutputStream {
        Gz(OutputStream out, int level) throws IOException {
            super(out);
            def.setLevel(level);
        }
    }
}
