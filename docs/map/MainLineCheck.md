# MainLineCheck.java - 416 lines · 13 methods · 0 constants · harnesses

`ham/citybuildersim/MainLineCheck.java` - generated 2026-10-18 by CodeMap; line numbers are as of that run.

> The main line is the railway's run from its gateway to the freight station,
> the yard nearest the gate arm's end, and it is drawn double where the city
> has a Freight Line.
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. NO YARD: with a gateway and track but no yard, fill() marks M_MAIN on
>      the whole gate arm (the hub and the hub's row toward the gateway) and
>      on no other plot of the track, and freightStation() is null.
> 
>   2. A YARD ON THE GATE ARM: M_MAIN runs from the yard's anchor out to the
>      arm's end, not between the hub and the yard.
> 
>   3. A YARD ON THE OPPOSITE ARM: the whole gate arm, the hub and the
>      opposite arm from the hub to the yard are M_MAIN; the opposite arm
>      beyond the yard is not.
> 
>   4. A YARD ON A LATER CORRIDOR: the chain through its interchange is
>      M_MAIN - the later arm from its start to the yard and not beyond it,
>      and the earlier arm's plot at the interchange.
> 
>   5. TWO YARDS: the one nearer the gate arm's end is freightStation().
> 
>   6. NO GATEWAY: nothing is marked M_MAIN, and freightStation() is null.
> 
>   7. DOUBLE TRACK: setDoubleMain(true) marks M_DOUBLE on exactly the
>      M_MAIN plots; false marks none; signature() differs between the two.
> 
>   8. THE HASHES: fill()'s hash with marks changes when a nearer yard moves
>      the station over a box on the old path; with marks == null it does
>      not, and frameHash() does not.
> 
>   9. ON A REAL MAP: MapCheck.yardCity() has a Freight Line, so
>      runs().doubleMain() is true, its freightStation() is not null and some
>      tile's marks carry M_MAIN | M_DOUBLE; with NO_GATEWAY on that seed,
>      nothing is marked instead.
> 
>  10. A GATE ARM THAT BENDS: an arm of three stretches - east, south-east,
>      east - is marked step for step, each 45-degree step's corner with it:
>      a stretch's steps are counted from the arm's start, not the stretch's.
> 
> WHY THIS EXISTS: batch FR0 slice M sets the main-line marks that the painter
> already draws. A fixture CAUSES each condition, and every assertion is
> against the model's own constants, never a bare number.

**Uses:** [CityRuns](CityRuns.md) (51), [TilePainter](TilePainter.md) (16), [CityMap](CityMap.md) (8), [MapCheck](MapCheck.md) (3), [MapTiles](MapTiles.md) (2), [World](World.md) (1)

## Sections

| line | section |
|---:|---|
| 119 | · 1. no yard |
| 152 | · 2. a yard on the gate arm |
| 184 | · 3. a yard on the opposite arm |
| 226 | · 4. a yard on a later corridor |
| 268 | · 5. two yards |
| 284 | · 6. no gateway |
| 298 | · 7. double track |
| 325 | · 8. the hashes |
| 348 | · 9. on a real map |
| 363 | · 10. a gate arm that bends |

## Fields (state)

| line | field | says |
|---:|---|---|
| 55 | `static int fails` |  |
| 64 | `final long x0, y0, x1, y1` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 53 | 364 | **type** `public class MainLineCheck` | The main line is the railway's run from its gateway to the freight station, the yard nearest the gate arm's end, and it is drawn double where the city has a Freight Line. |
| 57 | 4 | `static void check(String label, boolean ok)` |  |
| 63 | 15 | **type** `static class BoxGround implements CityRuns.Ground` | A square of owned dry ground the runs may lay on, -1 outside: the GatewayCheck fixture's shape. |
| 66 | 3 | `BoxGround(long x0, long y0, long x1, long y1)` _(in MainLineCheck.BoxGround)_ |  |
| 70 | 3 | `public int at(long x, long y, int dir)` _(in MainLineCheck.BoxGround)_ |  |
| 74 | 1 | `public long version()` _(in MainLineCheck.BoxGround)_ |  |
| 76 | 1 | `public long[] box()` _(in MainLineCheck.BoxGround)_ |  |
| 79 | 1 | `static BoxGround ground(long x0, long y0, long x1, long y1)` |  |
| 81 | 3 | `static void layRail(CityRuns runs, BoxGround g, long track, long siteX, long siteY)` |  |
| 86 | 13 | `static long[] plotOnArm(CityRuns.Arm a, int step)` | The main plot of step k along an arm: step 0 its start plot. |
| 100 | 4 | `static void addYardAtStep(CityRuns runs, int arm, int step)` |  |
| 105 | 5 | `static int markAt(long x0, long y0, int w, byte[] marks, long x, long y)` |  |
| 111 | 4 | `static long fillBox(CityRuns runs, BoxGround g, byte[] fixed, byte[] marks)` |  |
| 116 | 285 | `public static void main(String[] args)` |  |
| 402 | 14 | `static boolean marksCarry(CityMap m, int bits)` |  |

