# TruckCheck.java - 441 lines · 10 methods · 0 constants · harnesses

`ham/citybuildersim/TruckCheck.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> Proves that Haulage's loads are drawn as trucks on the map's roads, as a
> pure function of time, and that drawing them changes nothing in the game
> (0.8.8, batch FR5, slice A; docs/studies/spec-freight-and-infrastructure.md
> 4.7, D21). Not part of the game.
> 
> WHY THIS EXISTS. TruckSchedule counts a month's trucks from three tallies
> Freight keeps beside the railway's (truckedIn(), truckedOut(), truckedHome()),
> read by nothing in the model and saved with it. A tally that did not come
> to Haulage's tonnes, a count that dropped a load, an N that read as no round
> number, a truck that appeared at the month's turn or a setting that was
> off for an old settings.json would each draw a map that lies. Every number
> asserted against is the classes' own constant or the model's own getter.
> 
> What it has to prove, a section each:
> 
>   1. the tally: in a played city, truckedIn() + truckedOut() + truckedHome()
>      come to Haulage.getTonnes() every month, each is the sum of the
>      model's own getters at the railway's share in force, and a city with
>      no bulk has all three 0;
>   2. the counts: the trucks drawn, times what each stands for, come to the
>      month's loads within one truck a load group; a world route takes no
>      home load; stands() is 1 up to MOST_A_MONTH loads and then the
>      smallest 1-2-5 number that brings the trucks to MOST_A_MONTH or fewer,
>      the city's N, which a district's part (a share under 1) keeps;
>   3. a pure function: the same month, freight and routes give the same
>      calls and the same frames, a salt gives others, no visit crosses the
>      month's turn, frame() is exactly the trucks position() says are in
>      the box, and a truck runs its route and back, loaded as its load is;
>   4. the near-zoom gate: trucks are drawn at MapFrame.L0 with the setting
>      on, at no other level and never with it off;
>   5. the setting: GamePrefs.isTrucksOnMap() is on by default, an old
>      settings.json reads on, and off and on both survive a save and load;
>   6. never saved: the tallies ride in Freight's save and come back, and no
>      save class mentions a TruckSchedule.

**Uses:** [TruckSchedule](TruckSchedule.md) (65), [GamePrefs](GamePrefs.md) (11), [GameFiles](GameFiles.md) (6), [Game](Game.md) (5), [Freight](Freight.md) (5), [MapFrame](MapFrame.md) (5), [Good](Good.md) (4), [Haulage](Haulage.md) (3), [SectorState](SectorState.md) (3), [HaulageCheck](HaulageCheck.md) (2), [Sector](Sector.md) (2), [InfrastructureManager](InfrastructureManager.md) (2), [Traffic](Traffic.md) (1)

## Sections

| line | section |
|---:|---|
| 125 | · 1. THE TALLY |
| 183 | · 2. THE COUNTS |
| 270 | · 3. A PURE FUNCTION |
| 356 | · 4. THE NEAR-ZOOM GATE |
| 368 | · 5. THE SETTING |
| 390 | · 6. NEVER SAVED |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 50 | 392 | **type** `public class TruckCheck` | Proves that Haulage's loads are drawn as trucks on the map's roads, as a pure function of time, and that drawing them changes nothing in the game (0.8.8, batch FR5, slice A; docs/studies/spec-freight-and-infrastructur... |
| 54 | 4 | `static void check(String label, boolean ok)` |  |
| 59 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 64 | 3 | `static boolean near(double a, double b, double rel)` |  |
| 69 | 7 | `static List<Route> routes()` | The routes of the counts' sections: one world route, two access roads. |
| 77 | 5 | `static Set<Integer> routesOf(TruckSchedule s, Load l)` |  |
| 84 | 10 | `static List<Truck> brute(TruckSchedule s, long x0, long y0, long x1, long y1, double t)` | The trucks position() puts in the box at t, found the long way: every call, in arrival order. |
| 96 | 7 | `static long walkedStands(double loads)` | The N the rule gives for a month of `loads`, by a plain walk of the sequence 1, 2, 5, 10, 20, 50...: the first that brings loads / N to MOST_A_MONTH or under. |
| 105 | 8 | `static long stepBelow(long n)` | The step of 1, 2, 5, 10, 20, 50... |
| 114 | 5 | `static void quietly(Runnable r)` |  |
| 120 | 321 | `public static void main(String[] args)` |  |

