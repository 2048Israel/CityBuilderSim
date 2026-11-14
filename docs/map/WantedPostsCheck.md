# WantedPostsCheck.java - 1,056 lines · 31 methods · 4 constants · harnesses

`ham/citybuildersim/WantedPostsCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

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
>   3. THE MARKET PRICES IT: the band's multiple is struck on the posts that exist plus the refused ones, against the
>      supply the posts that exist give, and the wage walks ADJUST_RATE of the way, while the gate's own test still
>      reads only the posts that exist.
>   4. NEVER MORE THAN THE BAND'S OWN POSTS AGAIN: the refused posts lift a band's multiple at most sqrt(2) times,
>      never twice, and a band with few posts - a village's - bids little.
>   5. ONLY AS FAR AS THE BUSINESS CAN PAY: the best-paying building that wants the posts is how far they count.
>   6. A WORKING BUILDER'S LAID-OFF CREWS ARE NOT A SHORTAGE: a hold the gate would not have put on but for them asks
>      nothing.
>   7. IT DECAYS: no hold, no latent posts, and the wage walks back.
>   8. IT BRINGS THE BAND'S MIGRANTS: the premium it makes is what Migration reaches for.
>   9. THE SAVE: the latent posts come back from a save to the bit (the People caption reads them between a load and
>      the next settle), but they never reach the wage: a month's own planning settles its asks before the reprice
>      prices them, so a reloaded city's month is the same with the saved posts or without.
>  10. THE WIRING: in a city played ten years every hold a planner returns is noted, sorted and priced within the rules above.
>  11. AN IDLE BUILDER'S CREWS ARE SPARE (IC1): the posts a builder at its core crew has laid off do not hold the gate,
>      and a working builder's still do.
>  12. THE CAP READS THE MONTH'S OWN POSTS (LD2): posts that fall between the settle and the reprice (completions, a
>      strike, retirement) leave the latent posts held, at the reprice, to MOST_OF_POSTS of the posts it prices.
>  13. A BAND THAT ASKED NOTHING IS PRICED AS IT WAS (LD2): a refused post is the numerator of its own band's
>      tightness alone, so it keeps no worker from the band below it, which is priced on the fill against the posts
>      that exist.
>  14. A BAND CLOSED TO MIGRANTS ASKS NOTHING (LD2): the wage is meant to bring migrants, so a band none can come for
>      has no latent posts, though the hold is still counted and the bands open to migrants still bid.
> 
> Every fixture CAUSES its condition: a founded city given the hands and the posts of a stated shape, and every
> expected figure comes from the model's own calls (LabourMarket.advanceMonth() on a twin of the market, tightnessAt(),
> the template's posts), never a number written here.

**Uses:** [WageBand](WageBand.md) (136), [WantedPosts](WantedPosts.md) (65), [Game](Game.md) (47), [LabourMarket](LabourMarket.md) (36), [Sector](Sector.md) (19), [JobType](JobType.md) (17), [BuildingsTemplate](BuildingsTemplate.md) (15), [BusinessInvestment](BusinessInvestment.md) (11), [Construction](Construction.md) (10), [PopulationManager](PopulationManager.md) (10), [GameFiles](GameFiles.md) (8), [Migration](Migration.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (2), [BuildingManager](BuildingManager.md) (2), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 304 | · 1. the gate's hold carries its building |
| 392 | · 2. only the bands it is short in |
| 416 | · 3. the market prices it |
| 492 | · 4. never more than the band's own posts again |
| 534 | · 5. only as far as the business can pay |
| 618 | · 6. a working builder's laid-off crews are not a shortage |
| 684 | · 7. it decays |
| 717 | · 8. it brings the band's migrants |
| 732 | · 9. the save |
| 797 | · 10. the month's planning notes its holds |
| 833 | · 11. an idle builder's laid-off crews are spare for the gate: XS1's trap, released (IC1) |
| 905 | · 12. the cap reads the month's own posts (LD2) |
| 964 | · 13. a band that asked nothing is priced as it was (LD2) |
| 1010 | · 14. a band closed to migrants asks nothing (LD2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 109 | `WantedPostsCheck.MACHINE_WORKS` | `"Machine Works"` |  |
| 113 | `WantedPostsCheck.WORKFORCE` | `5_000` | The hands the fixture's city has: its workforce, and how many of them have a diploma, a college degree and a university degree. |
| 114 | `WantedPostsCheck.DIPLOMA_HEADS` | `2_000, COLLEGE_HEADS = 200, UNIVERSITY_HEADS = 20` |  |
| 275 | `WantedPostsCheck.DIPLOMA` | `WageBand.DIPLOMA.ordinal(), COLLEGE = WageBand.COLLEGE.ordinal(), UNIVERSITY ...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 51 | `static int fails` |  |
| 52 | `static PrintStream out, quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 49 | 1008 | **type** `public class WantedPostsCheck` | The posts the staffing gate refused bid their band's wage up (LD1; WantedPosts). |
| 54 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 59 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 65 | 3 | `static BuildingsTemplate template(Game g, String name)` |  |
| 69 | 3 | `static GameFiles filesFor(Path root, String name)` |  |
| 73 | 3 | `static Game city(Path root, String name)` |  |
| 78 | 14 | `static Game city(GameFiles files)` | A fresh city, currency and world pinned, land by fiat (InvestCheck's). |
| 99 | 3 | `static void noWait(Game g)` | No wait read for an order's interest (BA1: BusinessInvestment.carryOf(), THE INTEREST ON THE WAIT), as in a bare fixture: these cities have no builders, so every wait would be endless and every hold unpaid - rightly, ... |
| 104 | 4 | `static void month(Game g)` | One month, quietly. |
| 122 | 7 | `static long collegePosts(BuildingsTemplate t)` | The college posts the fixture's city has: its graduates less the most spare ones the gate could still refuse the building with, found from the gate's own dial (a building passes at MIN_STAFFABLE_TO_ORDER of its posts,... |
| 131 | 4 | `static void shortHands(Game g)` | The fixture's hands for a city: see shortCollege(). |
| 137 | 5 | `static double postsIn(BuildingsTemplate t, WageBand band)` | The posts a building has in a wage band. |
| 144 | 4 | `static void builderHook(Game g)` | The game's own hook (Game's constructor): the builders offer the share they struck, every other sector all its posts. |
| 154 | 7 | `static void strikeWorking(Construction builders, double share)` | Strikes the builders WORKING: {@code share} of their depots' posts on offer (above the core crew, under all). |
| 163 | 3 | `static void strikeIdle(Construction builders)` | Strikes the builders IDLE: no work ahead, so only the core crew (Construction.IDLE_PAYROLL_FLOOR) is on offer. |
| 168 | 6 | `static void hands(Game g, long workforce, double[] heads, long collegePosts, long universityPosts)` | Puts the city's hands and the posts that exist in a stated shape (they stand until a month is played). |
| 176 | 6 | `static void hands(Game g, long workforce, double[] heads, long[] jobs)` | hands() with the posts written job by job, for a fixture that needs a band the other one does not (the diploma's). |
| 188 | 3 | `static Game shortCollege(Path root, String name)` | A city whose Machine Works the gate refuses for want of college and university hands: the workforce and the diploma holders it needs, every college and university post taken by a graduate (so none is spare), and the l... |
| 192 | 5 | `static Game shortCollege(Game g)` |  |
| 199 | 6 | `static Game atBase(Game g)` | A city a month old with its labour market at its base, so no band starts under or over the going rate. |
| 212 | 4 | `static Game noRoom(Game g)` | A city with no room, a month older (GW1: the gate counts a year's draw, Sector.GATE_DRAW_MONTHS, and migration brings nobody to a city that has no room, Migration.getLastCrowding() 0): the founding's first households ... |
| 218 | 3 | `static Game heldCity(Path root, String name)` | shortCollege() with the Machine Works' refusal noted and settled, as the month's planning does. |
| 222 | 6 | `static Game held(Game g)` |  |
| 230 | 6 | `static LabourMarket twin(LabourMarket lm)` | A copy of a market as it stands, to ask what a month would do to it without doing it. |
| 238 | 3 | `static LabourMarket monthOn(Game g, double[] posts)` | What the market does with a month against these posts, on a twin of it. |
| 246 | 6 | `static LabourMarket monthOn(Game g, double[] posts, double[] supplyPosts)` | What the market does with a month priced on {@code posts} against the supply the fill gives {@code supplyPosts} (LD2: the posts that exist; LD1 filled against the posts it priced on), on a twin of it. |
| 254 | 7 | `static Game reload(GameFiles files)` | A city loaded from the harness's own save slot (never a player's: the folder is the TempDirs root's). |
| 263 | 5 | `static boolean sameBits(double[] a, double[] b)` | Whether two arrays hold the same doubles to the bit. |
| 269 | 5 | `static double[] plus(double[] a, double[] b)` |  |
| 278 | 7 | `static double marginOf(Game g, Sector owner, BuildingsTemplate t)` | The margin Game.noteStaffingHold() works out for a building: what it earns over the interest hurdle at prime and its record. |
| 287 | 10 | `static double primeFor(Game g, BuildingsTemplate t, double hurdle)` | The prime rate at which the interest hurdle on a building's cost is {@code hurdle}, found with the model's own hurdle. |
| 298 | 758 | `public static void main(String[] args) throws Exception` |  |

