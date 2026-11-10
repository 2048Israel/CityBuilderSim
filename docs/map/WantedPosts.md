# WantedPosts.java - 222 lines · 19 methods · 1 constants · model

`ham/citybuildersim/WantedPosts.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> The posts the city's businesses wanted this month and the staffing gate would not let them open, and the part of
> them the labour market prices as if they were offered (LD1).
> 
> WHY. Jerus, 2026-10-31: "my city was stuck... i had available mines but the mining industry wasnt building cause it
> wouldnt have enough workers... and new people didnt want to come cause the wage was trash, perhaps a fix would be
> that if the reason is not enough workers and the business can... then it raises its wage?" The loop was closed on
> both sides. A building is ordered only if MIN_STAFFABLE_TO_ORDER of its posts can be filled from spare workers
> (Sector.staffing()), and a wage rises only on posts that exist (LabourMarket.advanceMonth(): posts over supply per
> band). A refused building posts no jobs, so the band it was short in never saw it: in the playtest's own founding
> (AUTO2 seed 0, main 0.8.89) a Machine Works waited 255 months for the 34 college and 4 university workers of its
> 173 posts while the college wage sat at 1.05-1.08x, and migrants, who come for a band's premium
> (Migration.reach()), had little reason to.
> 
> THE RULE. A planner whose building the gate refused hands the refusal in with its test (Decision.heldForStaff);
> Game.noteStaffingHold() sorts it: held for the builders' laid-off crews (IC1's trap, not a shortage a wage
> answers), held though it would not carry its interest (no wage rise would make it worth building), or an Ask for the
> posts of the bands it is short in, with how far the wage of those posts could rise and the building still carry its
> interest. settle() then turns the month's asks into a latent post count per band, and Game.repriceLabour() adds
> them to the band's posts for the wage alone (pricedOn()): the multiple is priced as if the refused posts were
> offered, at the market's own pace (ADJUST_RATE) and cap (MAX_MULTIPLE), and the gate's own test and Migration's
> opportunity still read only the posts that exist. The refused posts are the numerator of their own band's
> tightness and nothing else: the supply the wage is struck against is the fill against the posts that exist, so a
> refused post takes no worker the fill carries down to the bands below (LD2). And a band no migrant can come for
> (Migration.admits()) asks nothing, since the wage is meant to bring migrants (LD2).
> 
> WHEN. One Game.nextMonth() runs the whole of it: the month's planning (Game.runPrivateInvestment()) notes the holds
> and ends with settle(), and the labour market is re-priced a few steps later in the same nextMonth()
> (SimulationEngine.updatePopulation(), then Game.repriceLabour()). So a month's asks are priced in the month they
> are made. The latent posts a save holds are replaced by the next month's settle before the next reprice reads
> them: they are saved for what shows them between a load and that settle (the People screen's caption), not for
> the wage.
> 
> It decays by being rebuilt: settle() replaces the latent posts every month from that month's asks, so a building
> the gate lets through, or a sector that stops asking, bids nothing from the next settle on and the wage walks back.

**Uses:** [WageBand](WageBand.md) (2), [LabourMarket](LabourMarket.md) (2)

**Used by (10):** [AutoBuildCheck](AutoBuildCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [CurveCheck](CurveCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DeskCheck](DeskCheck.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [PeopleScreen](PeopleScreen.md), [WantedPostsCheck](WantedPostsCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 60 | `WantedPosts.MOST_OF_POSTS` | `1.0` | The most a band's refused posts may count as, as a share of the posts it has that exist. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `static boolean carryKeptOut` | Whether the supply the wage is struck against is the fill against the posts that exist (LD2): always, in the game. |
| 101 | `private final List<Ask> asks` |  |
| 102 | `private int crewsHolds, unpaidHolds` |  |
| 103 | `private final double[] latent` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 44 | 179 | **type** `public final class WantedPosts` | The posts the city's businesses wanted this month and the staffing gate would not let them open, and the part of them the labour market prices as if they were offered (LD1). |
| 76 | 5 | **type** `public record Ask(String sector, String building, double[] posts, double payFactor)` | One refused building's ask: its sector and building, the posts it lacks workers for per band (only in the bands it is short in), and how far the wage of those posts could rise, as a factor on today's, and the building... |
| 77 | 3 | `{ ... }` _(in WantedPosts.Ask)_ |  |
| 89 | 11 | `public static Ask askOf(String sector, String building, double[] wanted, double[] spare, double margin, double shortBill)` | The ask a refused building makes, or null when it would not carry its interest even at today's wage ({@code margin} is what it earns over the interest hurdle). |
| 106 | 5 | `public void beginMonth()` | The month's start: last month's asks and counts cleared (the latent posts stay until settle() replaces them). |
| 113 | 1 | `public void heldForCrews()` | A hold the gate put on for the builders' laid-off crews: IC1's, no shortage the wage answers. |
| 116 | 1 | `public void heldUnpaid()` | A hold for want of workers on a building that would not carry its interest at today's wage: no ask. |
| 119 | 3 | `public void ask(Ask a)` | A hold for want of workers on a building that would pay: it asks for its posts. |
| 124 | 1 | `public int getHolds()` | Every staffing hold this month, whatever came of it. |
| 126 | 1 | `public int getCrewsHolds()` |  |
| 128 | 1 | `public int getUnpaidHolds()` |  |
| 131 | 1 | `public int getAsked()` | The holds that asked. |
| 134 | 1 | `public List<Ask> asks()` | This month's asks. |
| 149 | 18 | `public void settle(double[] posts, double[] supply, double[] premium, boolean[] open)` | Turns the month's asks into the posts per band the wage is priced on, replacing last month's. |
| 169 | 3 | `public void settle(double[] posts, double[] supply, double[] premium)` | settle() with every band open to migrants (the harness's direct calls). |
| 186 | 8 | `public double[] pricedOn(double[] posts)` | The posts per band the wage is priced on: the posts given plus the latent posts, the latent first held, in place, to MOST_OF_POSTS of the band's posts as they stand now. |
| 196 | 1 | `public double[] latent()` | The posts per band the wage is priced on beyond those that exist (a copy). |
| 198 | 1 | `public double getLatent(WageBand band)` |  |
| 200 | 5 | `public double getTotal()` |  |
| 207 | 1 | `public boolean any()` | Whether any band is priced on refused posts. |
| 214 | 1 | `public double[] state()` | The latent posts as saved. |
| 217 | 5 | `public void restore(double[] saved)` | Puts the saved latent posts back; null or a wrong length leaves none. |

