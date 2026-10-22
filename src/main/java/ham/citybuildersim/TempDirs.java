package ham.citybuildersim;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * The one place a harness, or a scratch city, gets a temporary folder from:
 * one folder per JVM under the system temp folder, deleted when that JVM exits.
 *
 * <p>WHY (batch TD1, 2026-10-21): every harness made its temp folders with
 * {@code Files.createTempDirectory(prefix)} straight into the system temp
 * folder and never removed them. A suite run makes about 95, so a week of
 * suite runs on Jerus's PC left some 37 GB of {@code autobuildcheck...},
 * {@code educheck...} and {@code mapcheck...} folders in his %TEMP%, nearly
 * filling the disk. The call is now {@link #create}: the same call with the
 * same checked exception, but into {@code <java.io.tmpdir>/citybuildersim-tmp/<pid>},
 * a folder made on the first call, with one shutdown hook that deletes it.
 *
 * <p>A JVM that is killed hard (a stopped batch, a closed console) skips its
 * shutdown hooks and leaves its folder. So the first call of the next JVM
 * sweeps the folders beside its own: one named for a process that is gone is
 * deleted, one named for a live process (another batch's suite, Jerus's running
 * game) is left alone, and a name that is not a number is none of this class's
 * business. Deleting is best effort and never throws, never follows a link, and
 * never reaches outside {@code citybuildersim-tmp}. Nothing is printed: in this
 * project every {@code println} is the log.
 *
 * <p>What this does not do: it does not clean the folders the old calls left
 * directly in the temp folder, and it is no home for a file somebody opens
 * after the JVM is gone.
 */
public final class TempDirs {

    private TempDirs() { }

    /** The folder, under the system temp folder, that holds one folder per JVM. */
    private static final String BASE_NAME = "citybuildersim-tmp";

    /** This JVM's folder, made on the first call; only read or written inside {@link #runRoot()}. */
    private static Path runRoot;

    /**
     * A new empty folder named from {@code prefix}, inside this JVM's folder;
     * {@code Files.createTempDirectory(prefix)} with the clean-up added.
     */
    public static Path create(String prefix) throws IOException {
        return Files.createTempDirectory(runRoot(), prefix);
    }

    /** This JVM's folder: made, hooked for deletion and the stale folders beside it swept, once per JVM. */
    private static synchronized Path runRoot() throws IOException {
        if (runRoot == null) {
            Path base = base();
            Path mine = base.resolve(Long.toString(ProcessHandle.current().pid()));
            Files.createDirectories(mine);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> deleteTree(mine, base), "TempDirs-cleanup"));
            sweep(base);
            runRoot = mine;
        }
        return runRoot;
    }

    private static Path base() {
        return Path.of(System.getProperty("java.io.tmpdir")).toAbsolutePath().normalize().resolve(BASE_NAME);
    }

    /** Deletes the folders beside this JVM's whose name is the number of a process that no longer exists. */
    private static void sweep(Path base) {
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(base)) {
            for (Path entry : entries) {
                String name = entry.getFileName().toString();
                if (name.isEmpty() || !name.chars().allMatch(c -> c >= '0' && c <= '9')) continue;
                try {
                    if (ProcessHandle.of(Long.parseLong(name)).isEmpty()) deleteTree(entry, base);
                } catch (NumberFormatException tooLongToBeAPid) {
                    // not a process number; none of ours
                }
            }
        } catch (IOException | RuntimeException ignored) {
            // best effort: a folder left for the next run's sweep is no harm
        }
    }

    /**
     * Deletes {@code target} and everything under it, files first and each
     * folder after its contents. A link (a symlink, or a junction on Windows)
     * is deleted as an entry and never descended. Does nothing unless
     * {@code target} is strictly inside {@code base}. Never throws: a file
     * Windows has locked stays, and the next run's sweep gets it.
     */
    private static void deleteTree(Path target, Path base) {
        try {
            Path t = target.toAbsolutePath().normalize();
            Path b = base.toAbsolutePath().normalize();
            if (!t.startsWith(b) || t.equals(b)) return;
            Files.walkFileTree(t, new SimpleFileVisitor<Path>() {
                @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (attrs.isSymbolicLink() || attrs.isOther()) {
                        tryDelete(dir);
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }
                @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    tryDelete(file);
                    return FileVisitResult.CONTINUE;
                }
                @Override public FileVisitResult visitFileFailed(Path file, IOException e) {
                    return FileVisitResult.CONTINUE;
                }
                @Override public FileVisitResult postVisitDirectory(Path dir, IOException e) {
                    tryDelete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException | RuntimeException ignored) {
            // best effort
        }
    }

    /** One entry deleted if it can be; a read-only one is made writable and tried once more. */
    private static void tryDelete(Path p) {
        try {
            Files.deleteIfExists(p);
            return;
        } catch (IOException | RuntimeException firstTry) {
            // fall through to the second try
        }
        try {
            p.toFile().setWritable(true);
            Files.deleteIfExists(p);
        } catch (IOException | RuntimeException secondTry) {
            // locked or gone; leave it
        }
    }
}
