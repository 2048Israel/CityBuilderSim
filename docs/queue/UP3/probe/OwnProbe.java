package ham.citybuildersim;

import java.util.*;
import ham.citybuildersim.sectors.Mining;

/** Where ownStamp()'s time goes on the 5B copy: the whole pass twice after a month, and its parts. */
public class OwnProbe {
    public static void main(String[] args) throws Exception {
        double k = args.length > 0 ? Double.parseDouble(args[0]) : 9_814;
        BuildingVisual.Type[] types = BuildingVisual.table(new BuildingCatalog().load());
        long seed = Founding.DEFAULT_WORLD_SEED;
        long[] place = MapCheck.dryPlace(World.of(seed));
        long[] c = new long[types.length];
        for (int t = 0; t < MapCheck.JERUS_COUNTS.length && t < c.length; t++) c[t] = Math.round(MapCheck.JERUS_COUNTS[t] * k);
        for (BuildingVisual.Type t : types) if (t != null && t.id() == Mining.IRON_MINE) c[t.id()] = 0;
        int side = (int) Math.ceil(Math.sqrt(MapCheck.groundKm2(types) * k / (CityMap.DISTRICT * CityMap.DISTRICT * World.KM2_PER_PLOT)));
        CityMap m = CityMap.square(seed, place[0], place[1], types, side, (int) Math.round(MapCheck.JERUS_FILL * CityMap.DISTRICT * CityMap.DISTRICT), c);
        for (int month = 0; month < 4; month++) {
            for (int t = 0; t < c.length; t++) if (c[t] > 0 && !(types[t] != null && (types[t].road() == BuildingVisual.HIGHWAY || types[t].track()))) c[t] += Math.max(1, c[t] / 10_000);
            m.reconcile(c);
            for (int pass = 0; pass < 2; pass++) {
                long t0 = System.nanoTime();
                long sink = 0;
                for (CityMap.District d : m.districts()) sink ^= m.ownStamp(d);
                System.out.printf("month %d pass %d: ownStamp of %d districts %.1f ms%n", month, pass, m.districts().size(), (System.nanoTime() - t0) / 1e6);
            }
            long t1 = System.nanoTime(), sink = 0;
            for (CityMap.District d : m.districts()) for (int b = -1; b <= 1; b++) for (int a = -1; a <= 1; a++) sink += m.siteList(d.dx + a, d.dy + b).size();
            long t2 = System.nanoTime();
            for (CityMap.District d : m.districts()) {
                long x0 = (m.baseDX() + d.dx) * CityMap.DISTRICT, y0 = (m.baseDY() + d.dy) * CityMap.DISTRICT;
                sink ^= m.runs().frameHash(x0, y0, DistrictPlan.FRAME, DistrictPlan.FRAME);
            }
            long t3 = System.nanoTime();
            for (CityMap.District d : m.districts()) { sink ^= m.yardsOf(d); sink ^= m.sidingsOf(d); }
            long t4 = System.nanoTime();
            for (CityMap.District d : m.districts()) {
                long x0 = (m.baseDX() + d.dx) * CityMap.DISTRICT, y0 = (m.baseDY() + d.dy) * CityMap.DISTRICT;
                sink ^= m.land().grid().owned(x0, y0, x0 + DistrictPlan.FRAME, y0 + DistrictPlan.FRAME);
            }
            long t5 = System.nanoTime();
            System.out.printf("   parts: siteLists %.1f, runs.frameHash %.1f, yards+sidings %.1f, owned %.1f ms (%d)%n", (t2 - t1) / 1e6, (t3 - t2) / 1e6, (t4 - t3) / 1e6, (t5 - t4) / 1e6, sink & 1);
        }
    }
}
