/**
 * The globe study's timing (docs/studies/spec-globe.md): one frame of a globe drawn
 * from a picture of the planet, as FoundingScreen.globePixels() draws it -
 * each pixel's orthographic inverse, then its texel - on the expanded map's
 * 1,345 x 806, for a picture at 12', 6' and 4'. A design aid, not game code.
 */
public class GlobeBench {
    static final double R = 6_371_007;

    static boolean orthoInverse(double lat0, double lon0, double x, double y, double[] ll) {
        double rho = Math.hypot(x, y);
        if (rho > R) return false;
        double f0 = Math.toRadians(lat0);
        if (rho == 0) { ll[0] = lat0; ll[1] = lon0; return true; }
        double c = Math.asin(rho / R), sc = Math.sin(c), cc = Math.cos(c);
        ll[0] = Math.toDegrees(Math.asin(cc * Math.sin(f0) + y * sc * Math.cos(f0) / rho));
        double lon = lon0 + Math.toDegrees(Math.atan2(x * sc, rho * cc * Math.cos(f0) - y * sc * Math.sin(f0)));
        ll[1] = ((lon + 180) % 360 + 360) % 360 - 180;
        return true;
    }

    static int[] frame(int[] t, int arcmin, double lat0, double lon0, double kmPx, int w, int h, int y0, int y1, int[] out) {
        int cols = 360 * 60 / arcmin, rows = 180 * 60 / arcmin;
        double[] ll = new double[2];
        for (int j = y0; j < y1; j++) {
            for (int i = 0; i < w; i++) {
                double x = (i + 0.5 - w / 2.0) * kmPx * 1000, y = (h / 2.0 - j - 0.5) * kmPx * 1000;
                if (!orthoInverse(lat0, lon0, x, y, ll)) { out[j * w + i] = 0xff0b1118; continue; }
                int row = (int) Math.min(rows - 1, Math.max(0, Math.floor((90 - ll[0]) * 60 / arcmin)));
                int col = Math.floorMod((int) Math.floor((ll[1] + 180) * 60 / arcmin), cols);
                out[j * w + i] = t[row * cols + col];
            }
        }
        return out;
    }

    public static void main(String[] a) throws Exception {
        int w = 1345, h = 806;
        double kmPx = 2 * R / 1000 / (0.9 * h);
        int threads = Runtime.getRuntime().availableProcessors();
        System.out.println("processors " + threads);
        for (int arcmin : new int[] { 12, 6, 4 }) {
            int cols = 360 * 60 / arcmin, rows = 180 * 60 / arcmin;
            int[] t = new int[cols * rows];
            java.util.Random r = new java.util.Random(1);
            for (int k = 0; k < t.length; k++) t[k] = 0xff000000 | r.nextInt(0xffffff);
            int[] out = new int[w * h];
            for (int k = 0; k < 20; k++) frame(t, arcmin, 14.6 + k, 121, kmPx, w, h, 0, h, out);
            long best = Long.MAX_VALUE;
            for (int k = 0; k < 20; k++) {
                long s = System.nanoTime();
                frame(t, arcmin, 14.6 + k * 0.5, 121 + k, kmPx, w, h, 0, h, out);
                best = Math.min(best, System.nanoTime() - s);
            }
            // the same frame cut into bands, one a thread
            java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(threads);
            long bestPar = Long.MAX_VALUE;
            for (int k = 0; k < 20; k++) {
                long s = System.nanoTime();
                java.util.List<java.util.concurrent.Future<?>> fs = new java.util.ArrayList<>();
                for (int b = 0; b < threads; b++) {
                    int y0 = b * h / threads, y1 = (b + 1) * h / threads;
                    final int kk = k;
                    fs.add(pool.submit(() -> frame(t, arcmin, 14.6 + kk * 0.5, 121 + kk, kmPx, w, h, y0, y1, out)));
                }
                for (java.util.concurrent.Future<?> f : fs) f.get();
                bestPar = Math.min(bestPar, System.nanoTime() - s);
            }
            pool.shutdown();
            System.out.printf("%2d' picture %,d x %,d (%,d MB as ints): a frame %.1f ms on one thread, %.1f ms on %d%n",
                    arcmin, cols, rows, cols * (long) rows * 4 / 1_000_000, best / 1e6, bestPar / 1e6, threads);
        }
    }
}
