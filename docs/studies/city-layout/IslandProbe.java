package ham.citybuildersim;
import java.nio.file.Path;
import java.util.*;
/** Scratch (CL1): the city's ground in pieces the water parts (tiles, four samples each, eight ways), the home piece the founding site's; built districts and highways by piece. */
public class IslandProbe {
    public static void main(String[] a) throws Exception {
        Path root = Path.of(a[0]);
        for (int k = 1; k < a.length; k++) {
            Game g = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
            g.run();
            g.loadGame(Integer.parseInt(a[k]));
            CityMap m = g.getCityMap();
            java.lang.reflect.Field f = CityMap.class.getDeclaredField("runGround");
            f.setAccessible(true);
            CityRuns.Ground gr = (CityRuns.Ground) f.get(m);
            long[] box = gr.box();
            long tx0 = box[0] >> 5, ty0 = box[1] >> 5, tx1 = box[2] >> 5, ty1 = box[3] >> 5;
            int W = (int) (tx1 - tx0 + 1), H = (int) (ty1 - ty0 + 1);
            boolean[] land = new boolean[W * H];
            for (int j = 0; j < H; j++) for (int i = 0; i < W; i++) {
                long x = (tx0 + i) << 5, y = (ty0 + j) << 5;
                for (int s = 0; s < 4 && !land[j * W + i]; s++) {
                    int c = gr.at(x + 8 + 16 * (s & 1), y + 8 + 16 * (s >> 1), -1);
                    if (c >= 0 && CityRuns.dry(c)) land[j * W + i] = true;
                }
            }
            int[] piece = new int[W * H];
            Arrays.fill(piece, -1);
            int pieces = 0;
            List<Integer> sizes = new ArrayList<>();
            ArrayDeque<Integer> q = new ArrayDeque<>();
            for (int s = 0; s < W * H; s++) {
                if (!land[s] || piece[s] >= 0) continue;
                int n = 0; piece[s] = pieces; q.add(s);
                while (!q.isEmpty()) {
                    int c = q.poll(); n++;
                    int ci = c % W, cj = c / W;
                    for (int dj = -1; dj <= 1; dj++) for (int di = -1; di <= 1; di++) {
                        int ni = ci + di, nj = cj + dj;
                        if (ni < 0 || nj < 0 || ni >= W || nj >= H) continue;
                        int nb = nj * W + ni;
                        if (land[nb] && piece[nb] < 0) { piece[nb] = pieces; q.add(nb); }
                    }
                }
                sizes.add(n); pieces++;
            }
            CityMap.District d0 = m.districts().get(0);
            DistrictPlan.Input i0 = m.planInputHere(d0);
            long sx = Math.round(i0.hubX + i0.x0), sy = Math.round(i0.hubY + i0.y0);
            int home = -1;
            for (int r = 0; r < 8 && home < 0; r++) for (int dj = -r; dj <= r && home < 0; dj++) for (int di = -r; di <= r && home < 0; di++) {
                long ti = (sx >> 5) + di - tx0, tj = (sy >> 5) + dj - ty0;
                if (ti >= 0 && tj >= 0 && ti < W && tj < H && piece[(int) (tj * W + ti)] >= 0) home = piece[(int) (tj * W + ti)];
            }
            int[] distByPiece = new int[pieces];
            int bHome = 0, bOther = 0, hwHome = 0, hwOther = 0; long nHome = 0, nOther = 0;
            Set<Integer> otherPieces = new TreeSet<>();
            for (CityMap.District d : m.districts()) {
                long n = 0; for (int t = 0; t < m.types().length; t++) if (m.types()[t] != null && m.types()[t].drawn()) n += d.count(t);
                if (n == 0) continue;
                DistrictPlan.Input in = m.planInputHere(d);
                int[] cnt = new int[pieces];
                for (int tj = 0; tj < 8; tj++) for (int ti = 0; ti < 8; ti++) {
                    long gi = (in.x0 >> 5) + ti - tx0, gj = (in.y0 >> 5) + tj - ty0;
                    if (gi >= 0 && gj >= 0 && gi < W && gj < H && piece[(int) (gj * W + gi)] >= 0) cnt[piece[(int) (gj * W + gi)]]++;
                }
                int best = -1; for (int p = 0; p < pieces; p++) if (cnt[p] > 0 && (best < 0 || cnt[p] > cnt[best])) best = p;
                boolean hw = false;
                for (byte fx : in.fixed) if (fx == DistrictPlan.FIXED_HIGHWAY) { hw = true; break; }
                if (best == home || (best >= 0 && cnt[home < 0 ? 0 : home] > 0)) { bHome++; nHome += n; if (hw) hwHome++; }
                else { bOther++; nOther += n; if (hw) hwOther++; otherPieces.add(best); }
            }
            System.out.printf("slot %s: %d pieces of ground the water parts (home %d tiles of 32 x 32 plots); built districts on the home piece %d (%d with a highway), %,d buildings; on %d other pieces %d (%d with a highway), %,d buildings%n",
                a[k], pieces, home < 0 ? 0 : sizes.get(home), bHome, hwHome, nHome, otherPieces.size(), bOther, hwOther, nOther);
        }
    }
}
