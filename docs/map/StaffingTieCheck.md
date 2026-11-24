# StaffingTieCheck.java - 522 lines · 29 methods · 6 constants · harnesses

`ham/citybuildersim/StaffingTieCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> The harness for TQ1's tie rule at the staffing gate: a planner whose buildings the gate refused hands WantedPosts one hold
> (LD1), and where two of its refusals stand at the same share the hold keeps both, Game.noteStaffingHold() notes each, and
> each asks for its part of its posts (Sector.Staffing.held(), WantedPosts.Ask.part()). Not part of the game.
> 
> WHY. On main 0.9.129 the thirteen planners each wrote a strict {@code >} and so the first template they met kept every tie.
> In a city with no spare, every template at or below the diploma band shares drawn/posts, snapped to DRAWN_SHARE_GRID (GW1,
> docs/queue/GW1-notes.md, section 5), so the refused templates tie exactly, and the order a sector lists its buildings in
> decided which one the wage was bid for: measured on that default playtest, a tie decided 638 of its 3,502 holds, 278 of
> Construction's 305 (the Builders' Yard's 20 posts priced, never the Construction Depot's 50), 226 of Restaurants' 467 and 103
> of Manufacturing's 522, mostly in the founding. Before the snap float noise broke the tie as it fell; shared is that noise's
> expectation, and nothing a sector lists first decides it. Nothing here moves a premise to get green: the tie is caused on a
> fresh city (section 1), and every figure asserted against comes from the model's own calls.
> 
> What this has to prove:
>   1. THE FIXTURE CAUSES THE TIE: in a fresh city a planner hands in a hold for two templates of its sector the gate
>      refused at the same share to the bit, which differ in posts: the founding's tie (no spare, and a month struck with no
>      room, so no draw: a share of 0), and one at a share above 0 and under Sector.MIN_STAFFABLE_TO_ORDER (the same city
>      with the month struck with room, so the draw fills part of every template); and asking the planner for it moves
>      nothing in the city. Both are set by hand on a fresh city, StaffingDrawCheck's seatWith() and its month struck by hand
>      (noRoomMonth(), roomMonth()), no month played: nothing in them waits for a played city to come to a tie.
>   2. THE ONE PLACE KEEPS BOTH, WHICHEVER IS MET FIRST: held(a, b) and held(b, a) both carry both templates, each once; none
>      so far keeps the one; a refusal at a larger share is kept alone, whenever it is met; one at a smaller share leaves the two.
>   3. WANTEDPOSTS PRICES THE TWO ALIKE, WHICHEVER IS FIRST: the same asks in the same order and the same latent posts to the
>      bit; one tied hold counted once and each building counted besides; each building's outcome is the one it has alone and
>      each ask is its ask alone divided by the number tied, so the posts asked are the mean of the two alone.
>   4. THE MONTH'S OWN PLANNING HANDS THE TIE IN, AND HOW OFTEN: in a fresh city's first month (its people have no workforce
>      yet, so no spare and no draw: section 1's founding tie, which the month strikes again whatever is set by hand) the tie
>      fires and a sector asked one question a month (every one but Retail) names two of its buildings; and in a city played
>      ten years every month's holds are its crews', its unpaid and its asks and a tie's buildings each count. How often the
>      tie fires in that played city is printed, a measurement, never asserted.

**Uses:** [Sector](Sector.md) (44), [WantedPosts](WantedPosts.md) (20), [Game](Game.md) (16), [StaffingDrawCheck](StaffingDrawCheck.md) (7), [WageBand](WageBand.md) (5), [GameFiles](GameFiles.md) (2), [DeterminismCheck](DeterminismCheck.md) (2), [BusinessInvestment](BusinessInvestment.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [PopulationManager](PopulationManager.md) (1), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 282 | 1. the fixture causes the tie |
| 322 | 2. the one place keeps both, whichever is met first |
| 387 | 3. WantedPosts prices the two alike, whichever is first |
| 450 | 4. the month's own planning hands the tie in, and how often |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `StaffingTieCheck.SPARE_WORKERS` | `0` | The spare workers section 1 seats in the fresh city: none, so every template's share is the draw's alone (drawn over its posts, on the grid), the same at any posts, and two templates of a sector that differ in posts tie. |
| 63 | `StaffingTieCheck.EMPLOYED_WORKERS` | `200` | The workers the fresh city employs in posts of its own beside them: enough that the latent posts a wage is priced on (WantedPosts.settle() keeps them within the posts the city has) differ from one tied building's ask ... |
| 66 | `StaffingTieCheck.PLAYED_MONTHS` | `120` | The months of the city section 4 plays for the every-month identities, ten years as WantedPostsCheck's. |
| 69 | `StaffingTieCheck.TIED_WAYS` | `2` | How many buildings share a tie that section 3 finds: the two of the fixture. |
| 185 | `StaffingTieCheck.NO_SHARE` | `share -> share = = 0` | The shares a tie may stand at: none (the founding's), or above 0 and under Sector.MIN_STAFFABLE_TO_ORDER (a share the draw filled part of). |
| 186 | `StaffingTieCheck.A_SHARE_THE_DRAW_FILLED_PART_OF` | `share -> share> 0 & & share<Sector.MIN_STAFFABLE_TO_ORDER` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 50 | `static int fails` |  |
| 51 | `static PrintStream out, quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 475 | **type** `public class StaffingTieCheck` | The harness for TQ1's tie rule at the staffing gate: a planner whose buildings the gate refused hands WantedPosts one hold (LD1), and where two of its refusals stand at the same share the hold keeps both, Game.noteSta... |
| 71 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 76 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 82 | 3 | `static GameFiles filesFor(Path root, String name)` |  |
| 87 | 14 | `static Game city(Path root, String name)` | A fresh city, currency and world pinned, land by fiat, no wait read for an order's interest (WantedPostsCheck's). |
| 106 | 3 | `static void noWait(Game g)` | No wait read for an order's interest (BA1), as WantedPostsCheck's cities: a building nobody can build carries no interest at any wage, and what the posts are priced at is this harness's subject, not the wait. |
| 111 | 4 | `static void month(Game g)` | One month, quietly. |
| 117 | 5 | `static double total(Sector.Staffing s)` | A building's posts, all bands. |
| 124 | 7 | **type** `record Tie(int month, Sector sector, Sector.Staffing first, Sector.Staffing second)` | Two refusals of one sector at the same share to the bit, in the order the sector lists them, and the month they were found in. |
| 125 | 5 | `String describe()` _(in StaffingTieCheck.Tie)_ |  |
| 133 | 1 | **type** `record Found(Game g, Tie tie)` | A city and the tie found in it. |
| 143 | 22 | `static Tie tieIn(Game g, DoublePredicate shareTaken, int month)` | The first tie a planner of this city hands in, in the order the sectors are listed: its plan asked now as the month's planning asks it (Sector.plan()), a hold for staff whose refusals stand at the same share to the bi... |
| 172 | 11 | `static Found caused(Path root, String name, boolean room)` | A fresh city in the state that ties two templates, no month played: SPARE_WORKERS seated (StaffingDrawCheck.seat(), the workforce, its skills and its posts) and Migration's month struck by hand on households (Staffing... |
| 189 | 8 | `static Sector.Staffing otherRefusal(Game g, Tie tie, DoublePredicate taken)` | A refusal of this sector's at a share this test takes, other than the two tied, or null. |
| 199 | 4 | `static Sector.Staffing refusalAt(double share)` | A refusal of no template and no owner at this share: held() reads nothing else of it. |
| 204 | 5 | `static Set<Integer> idsOf(List<Sector.Staffing> alike)` |  |
| 211 | 3 | `static Set<Integer> idsOf(Tie tie)` | The template ids of these two, as a set. |
| 216 | 1 | **type** `record Noted(List<WantedPosts.Ask> asks, int holds, int crews, int unpaid, int asked, int tied, double[] la...` | What a hold noted alone, or tied, came to in WantedPosts, and the latent posts a fresh WantedPosts settles its asks to. |
| 223 | 9 | `static Noted note(Game g, Sector.Staffing hold)` | Game.noteStaffingHold() of this hold, in a month begun, and what it came to: its asks, its counts, and the latent posts of a fresh WantedPosts given those asks and settled against the city's own posts, supply and prem... |
| 234 | 10 | `static double[] settled(Game g, List<WantedPosts.Ask> asks)` | The latent posts a fresh WantedPosts settles to, given these asks and the city's posts, supply and premiums. |
| 246 | 9 | `static boolean sameAsks(List<WantedPosts.Ask> x, List<WantedPosts.Ask> y)` | The asks alike: the same sector, building, posts per band and pay factor to the bit, in the same order. |
| 256 | 3 | `static String outcome(Noted n)` |  |
| 261 | 5 | `static double[] sumByBand(List<WantedPosts.Ask> asks)` | The posts these asks make in each band, summed. |
| 267 | 14 | `public static void main(String[] args) throws Exception` |  |

### 1. the fixture causes the tie (lines 282-321)

| line | len | member | says |
|---:|---:|---|---|
| 287 | 34 | `static Found sectionOne(Path root, boolean first)` |  |

### 2. the one place keeps both, whichever is met first (lines 322-386)

| line | len | member | says |
|---:|---:|---|---|
| 324 | 9 | `static void sectionTwo(Found founding, Found grown)` |  |
| 334 | 44 | `static void keepsBoth(String label, Found f)` |  |
| 379 | 3 | `static boolean alone(Sector.Staffing held, Sector.Staffing expected)` |  |
| 383 | 3 | `static boolean sameTwo(Sector.Staffing held, Tie tie)` |  |

### 3. WantedPosts prices the two alike, whichever is first (lines 387-449)

| line | len | member | says |
|---:|---:|---|---|
| 389 | 9 | `static void sectionThree(Found founding, Found grown)` |  |
| 399 | 50 | `static void priced(String label, Found f)` |  |

### 4. the month's own planning hands the tie in, and how often (lines 450-522)

| line | len | member | says |
|---:|---:|---|---|
| 458 | 12 | `static Set<String> sectorsNamingTwo(Game g, WantedPosts w, Map<String, Integer> askedBySector)` | The sectors, other than Retail, whose asks this month named two of its buildings, when a hold was tied. |
| 471 | 51 | `static void sectionFour(Path root)` |  |

