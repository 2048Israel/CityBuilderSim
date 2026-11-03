# WantedPostsCheck.java - 783 lines · 24 methods · 4 constants · harnesses

`ham/citybuildersim/WantedPostsCheck.java` - generated 2026-11-03 by CodeMap; line numbers are as of that run.

> The posts the staffing gate refused bid their band's wage up (LD1; WantedPosts). Not part of the game.
> 
> WHAT THIS HAS TO PROVE. Jerus, 2026-10-31: "my city was stuck... i had available mines but the mining industry wasnt
> building cause it wouldnt have enough workers... and new people didnt want to come cause the wage was trash, perhaps a
> fix would be that if the reason is not enough workers and the business can... then it raises its wage?" A building
> is ordered only if Sector.MIN_STAFFABLE_TO_ORDER of its posts can be filled from spare workers, and a wage rises
> only on posts that exist, so a refused building never moved its band's wage. So:
> 
>   1. THE GATE'S HOLD CARRIES ITS BUILDING: who asked, which building, how many, its posts and the spare workers per
>      band; and the planner's Decision carries the hold, with its reason in the hold's own words.
>   2. ONLY THE BANDS IT IS SHORT IN ask for posts, and a building that would not carry its interest asks for none.
>   3. THE MARKET PRICES IT: the band's multiple is struck on the posts that exist plus the refused ones, and the wage
>      walks ADJUST_RATE of the way, while the gate's own test still reads only the posts that exist.
>   4. NEVER MORE THAN THE BAND'S OWN POSTS AGAIN: the refused posts lift a band's multiple at most sqrt(2) times,
>      never twice, and a band with few posts - a village's - bids little.
>   5. ONLY AS FAR AS THE BUSINESS CAN PAY: the best-paying building that wants the posts is how far they count.
>   6. A WORKING BUILDER'S LAID-OFF CREWS ARE NOT A SHORTAGE: a hold the gate would not have put on but for them asks
>      nothing.
>   7. IT DECAYS: no hold, no latent posts, and the wage walks back.
>   8. IT BRINGS THE BAND'S MIGRANTS: the premium it makes is what Migration reaches for.
>   9. THE SAVE: the latent posts are priced next month, so a reloaded city prices it as the live one does.
>  10. THE WIRING: in a city played ten years every hold a planner returns is noted, sorted and priced within the rules above.
>  11. AN IDLE BUILDER'S CREWS ARE SPARE (IC1): the posts a builder at its core crew has laid off do not hold the gate,
>      and a working builder's still do.
> 
> Every fixture CAUSES its condition: a founded city given the hands and the posts of a stated shape, and every
> expected figure comes from the model's own calls (LabourMarket.advanceMonth() on a twin of the market, tightnessAt(),
> the template's posts), never a number written here.

**Uses:** [WageBand](WageBand.md) (81), [WantedPosts](WantedPosts.md) (46), [Game](Game.md) (29), [LabourMarket](LabourMarket.md) (22), [Sector](Sector.md) (16), [BuildingsTemplate](BuildingsTemplate.md) (14), [JobType](JobType.md) (14), [BusinessInvestment](BusinessInvestment.md) (11), [Construction](Construction.md) (10), [PopulationManager](PopulationManager.md) (8), [GameFiles](GameFiles.md) (7), [Migration](Migration.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (2), [BuildingManager](BuildingManager.md) (2), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 236 | · 1. the gate's hold carries its building |
| 320 | · 2. only the bands it is short in |
| 344 | · 3. the market prices it |
| 393 | · 4. never more than the band's own posts again |
| 435 | · 5. only as far as the business can pay |
| 519 | · 6. a working builder's laid-off crews are not a shortage |
| 581 | · 7. it decays |
| 614 | · 8. it brings the band's migrants |
| 629 | · 9. the save |
| 671 | · 10. the month's planning notes its holds |
| 707 | · 11. an idle builder's laid-off crews are spare for the gate: XS1's trap, released (IC1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 88 | `WantedPostsCheck.MACHINE_WORKS` | `"Machine Works"` |  |
| 92 | `WantedPostsCheck.WORKFORCE` | `5_000` | The hands the fixture's city has: its workforce, and how many of them have a diploma, a college degree and a university degree. |
| 93 | `WantedPostsCheck.DIPLOMA_HEADS` | `2_000, COLLEGE_HEADS = 200, UNIVERSITY_HEADS = 20` |  |
| 208 | `WantedPostsCheck.COLLEGE` | `WageBand.COLLEGE.ordinal(), UNIVERSITY = WageBand.UNIVERSITY.ordinal()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 41 | `static int fails` |  |
| 42 | `static PrintStream out, quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 39 | 745 | **type** `public class WantedPostsCheck` | The posts the staffing gate refused bid their band's wage up (LD1; WantedPosts). |
| 44 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 49 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 55 | 3 | `static BuildingsTemplate template(Game g, String name)` |  |
| 59 | 3 | `static GameFiles filesFor(Path root, String name)` |  |
| 63 | 3 | `static Game city(Path root, String name)` |  |
| 68 | 13 | `static Game city(GameFiles files)` | A fresh city, currency and world pinned, land by fiat (InvestCheck's). |
| 83 | 4 | `static void month(Game g)` | One month, quietly. |
| 101 | 7 | `static long collegePosts(BuildingsTemplate t)` | The college posts the fixture's city has: its graduates less the most spare ones the gate could still refuse the building with, found from the gate's own dial (a building passes at MIN_STAFFABLE_TO_ORDER of its posts,... |
| 110 | 4 | `static void shortHands(Game g)` | The fixture's hands for a city: see shortCollege(). |
| 116 | 5 | `static double postsIn(BuildingsTemplate t, WageBand band)` | The posts a building has in a wage band. |
| 123 | 4 | `static void builderHook(Game g)` | The game's own hook (Game's constructor): the builders offer the share they struck, every other sector all its posts. |
| 133 | 7 | `static void strikeWorking(Construction builders, double share)` | Strikes the builders WORKING: {@code share} of their depots' posts on offer (above the core crew, under all). |
| 142 | 3 | `static void strikeIdle(Construction builders)` | Strikes the builders IDLE: no work ahead, so only the core crew (Construction.IDLE_PAYROLL_FLOOR) is on offer. |
| 147 | 9 | `static void hands(Game g, long workforce, double[] heads, long collegePosts, long universityPosts)` | Puts the city's hands and the posts that exist in a stated shape (they stand until a month is played). |
| 162 | 3 | `static Game shortCollege(Path root, String name)` | A city whose Machine Works the gate refuses for want of college and university hands: the workforce and the diploma holders it needs, every college and university post taken by a graduate (so none is spare), and the l... |
| 166 | 7 | `static Game shortCollege(Game g)` |  |
| 175 | 3 | `static Game heldCity(Path root, String name)` | shortCollege() with the Machine Works' refusal noted and settled, as the month's planning does. |
| 179 | 6 | `static Game held(Game g)` |  |
| 187 | 6 | `static LabourMarket twin(LabourMarket lm)` | A copy of a market as it stands, to ask what a month would do to it without doing it. |
| 195 | 6 | `static LabourMarket monthOn(Game g, double[] posts)` | What the market does with a month against these posts, on a twin of it. |
| 202 | 5 | `static double[] plus(double[] a, double[] b)` |  |
| 211 | 6 | `static double marginOf(Game g, Sector owner, BuildingsTemplate t)` | The margin Game.noteStaffingHold() works out for a building: what it earns over the interest hurdle at prime and its record. |
| 219 | 10 | `static double primeFor(Game g, BuildingsTemplate t, double hurdle)` | The prime rate at which the interest hurdle on a building's cost is {@code hurdle}, found with the model's own hurdle. |
| 230 | 553 | `public static void main(String[] args) throws Exception` |  |

