import jdk.jfr.consumer.*;
import java.nio.file.Path;
import java.util.*;

/** Scratch: callers (up to 6 frames up, ham.* only) of samples whose stack contains a method prefix, FX thread only. */
public class JfrCallers {
    public static void main(String[] a) throws Exception {
        String target = a[1];
        Map<String, Integer> paths = new HashMap<>();
        int hits = 0, fx = 0;
        try (RecordingFile rf = new RecordingFile(Path.of(a[0]))) {
            while (rf.hasMoreEvents()) {
                RecordedEvent e = rf.readEvent();
                if (!e.getEventType().getName().equals("jdk.ExecutionSample")) continue;
                RecordedThread t = e.getThread("sampledThread");
                if (t == null || !"JavaFX Application Thread".equals(t.getJavaName())) continue;
                fx++;
                List<RecordedFrame> fr = e.getStackTrace().getFrames();
                for (int i = 0; i < fr.size(); i++) {
                    String n = fr.get(i).getMethod().getType().getName() + "." + fr.get(i).getMethod().getName();
                    if (!n.startsWith(target)) continue;
                    hits++;
                    StringBuilder sb = new StringBuilder();
                    int k = 0;
                    for (int j = i; j < fr.size() && k < 6; j++) {
                        String m = fr.get(j).getMethod().getType().getName() + "." + fr.get(j).getMethod().getName();
                        if (!m.startsWith("ham.")) continue;
                        sb.append(k == 0 ? "" : " <- ").append(m.replace("ham.citybuildersim.", ""));
                        k++;
                    }
                    paths.merge(sb.toString(), 1, Integer::sum);
                    break;
                }
            }
        }
        System.out.printf("%s: %d of %d FX samples%n", target, hits, fx);
        final int all = fx;
        paths.entrySet().stream().sorted((x, y) -> y.getValue() - x.getValue()).limit(Integer.parseInt(a.length > 2 ? a[2] : "6"))
                .forEach(x -> System.out.printf("  %4.1f%%  %s%n", 100.0 * x.getValue() / all, x.getKey()));
    }
}
