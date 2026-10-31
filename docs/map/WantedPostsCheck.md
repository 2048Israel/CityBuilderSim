# WantedPostsCheck.java - 659 lines · 21 methods · 4 constants · harnesses

`ham/citybuildersim/WantedPostsCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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
>   4. NEVER MORE THAN THE BAND HAS WORKERS: the refused posts cannot double a band's wage.
>   5. ONLY AS FAR AS THE BUSINESS CAN PAY: the best-paying building that wants the posts is how far they count.
>   6. THE BUILDERS' LAID-OFF CREWS ARE NOT A SHORTAGE: a hold the gate would not have put on but for them asks nothing.
>   7. IT DECAYS: no hold, no latent posts, and the wage walks back.
>   8. IT BRINGS THE BAND'S MIGRANTS: the premium it makes is what Migration reaches for.
>   9. THE SAVE: the latent posts are priced next month, so a reloaded city prices it as the live one does.
>  10. THE WIRING: in a city played ten years every hold a planner returns is noted, sorted and priced within the rules above.
> 
> Every fixture CAUSES its condition: a founded city given the hands and the posts of a stated shape, and every
> expected figure comes from the model's own calls (LabourMarket.advanceMonth() on a twin of the market, tightnessAt(),
> the template's posts), never a number written here.

**Uses:** [WageBand](WageBand.md) (78), [WantedPosts](WantedPosts.md) (42), [Game](Game.md) (27), [LabourMarket](LabourMarket.md) (22), [Sector](Sector.md) (14), [BuildingsTemplate](BuildingsTemplate.md) (12), [JobType](JobType.md) (11), [BusinessInvestment](BusinessInvestment.md) (11), [GameFiles](GameFiles.md) (7), [PopulationManager](PopulationManager.md) (7), [Migration](Migration.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (2), [TempDirs](TempDirs.md) (1), [BuildingManager](BuildingManager.md) (1)

## Sections

| line | section |
|---:|---|
| 206 | · 1. the gate's hold carries its building |
| 284 | · 2. only the bands it is short in |
| 308 | · 3. the market prices it |
| 357 | · 4. never more than the band has workers |
| 386 | · 5. only as far as the business can pay |
| 470 | · 6. the builders' laid-off crews are not a shortage |
| 525 | · 7. it decays |
| 558 | · 8. it brings the band's migrants |
| 573 | · 9. the save |
| 615 | · 10. the month's planning notes its holds |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 82 | `WantedPostsCheck.MACHINE_WORKS` | `"Machine Works"` |  |
| 86 | `WantedPostsCheck.WORKFORCE` | `5_000` | The hands the fixture's city has: its workforce, and how many of them have a diploma, a college degree and a university degree. |
| 87 | `WantedPostsCheck.DIPLOMA_HEADS` | `2_000, COLLEGE_HEADS = 200, UNIVERSITY_HEADS = 20` |  |
| 178 | `WantedPostsCheck.COLLEGE` | `WageBand.COLLEGE.ordinal(), UNIVERSITY = WageBand.UNIVERSITY.ordinal()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 35 | `static int fails` |  |
| 36 | `static PrintStream out, quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 33 | 627 | **type** `public class WantedPostsCheck` | The posts the staffing gate refused bid their band's wage up (LD1; WantedPosts). |
| 38 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 43 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 49 | 3 | `static BuildingsTemplate template(Game g, String name)` |  |
| 53 | 3 | `static GameFiles filesFor(Path root, String name)` |  |
| 57 | 3 | `static Game city(Path root, String name)` |  |
| 62 | 13 | `static Game city(GameFiles files)` | A fresh city, currency and world pinned, land by fiat (InvestCheck's). |
| 77 | 4 | `static void month(Game g)` | One month, quietly. |
| 95 | 7 | `static long collegePosts(BuildingsTemplate t)` | The college posts the fixture's city has: its graduates less the most spare ones the gate could still refuse the building with, found from the gate's own dial (a building passes at MIN_STAFFABLE_TO_ORDER of its posts,... |
| 104 | 4 | `static void shortHands(Game g)` | The fixture's hands for a city: see shortCollege(). |
| 110 | 5 | `static double postsIn(BuildingsTemplate t, WageBand band)` | The posts a building has in a wage band. |
| 117 | 9 | `static void hands(Game g, long workforce, double[] heads, long collegePosts, long universityPosts)` | Puts the city's hands and the posts that exist in a stated shape (they stand until a month is played). |
| 132 | 3 | `static Game shortCollege(Path root, String name)` | A city whose Machine Works the gate refuses for want of college and university hands: the workforce and the diploma holders it needs, every college and university post taken by a graduate (so none is spare), and the l... |
| 136 | 7 | `static Game shortCollege(Game g)` |  |
| 145 | 3 | `static Game heldCity(Path root, String name)` | shortCollege() with the Machine Works' refusal noted and settled, as the month's planning does. |
| 149 | 6 | `static Game held(Game g)` |  |
| 157 | 6 | `static LabourMarket twin(LabourMarket lm)` | A copy of a market as it stands, to ask what a month would do to it without doing it. |
| 165 | 6 | `static LabourMarket monthOn(Game g, double[] posts)` | What the market does with a month against these posts, on a twin of it. |
| 172 | 5 | `static double[] plus(double[] a, double[] b)` |  |
| 181 | 6 | `static double marginOf(Game g, Sector owner, BuildingsTemplate t)` | The margin Game.noteStaffingHold() works out for a building: what it earns over the interest hurdle at prime and its record. |
| 189 | 10 | `static double primeFor(Game g, BuildingsTemplate t, double hurdle)` | The prime rate at which the interest hurdle on a building's cost is {@code hurdle}, found with the model's own hurdle. |
| 200 | 459 | `public static void main(String[] args) throws Exception` |  |

