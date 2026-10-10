# AutoBuildCheck.java - 1,027 lines · 22 methods · 3 constants · harnesses

`ham/citybuildersim/AutoBuildCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> Automatic building (0.7.73, batch N4): AutoBuilder's month held to its
> rules, in a city it builds for decades and in fixtures that cause each of
> the things that hold it back.
> 
> WHY. Jerus: "an automatic build and acquire debt button for basically
> automatic building, with a required slack button that you add, aka
> maintain say 15% surplus service of everything ... right in the build
> menu, and on/off, so that one can focus on other things." A switch that
> spends the treasury and borrows by itself every month has to be shown to
> keep its word over a city's life, not on one order: what it builds, what
> it borrows, and that a saved city plays on as the one it was saved from.
> 
> What this has to prove:
>   1. THE SETTINGS: a new city has it off, at a 15% margin and a debt
>      limit of 60% of a year's GDP (0.7.81; 15% of revenue until then),
>      "Build from cash anyway" off; the sliders keep to their steps and
>      their ends; switching it, moving a slider and the toggle are
>      decisions; off, a month's pass does nothing; a save from before it
>      loads with it off at its defaults, and one from 0.7.73 to 0.7.80 - its
>      limit a share of revenue - with the default limit and the toggle off.
>   2. THE ADVICE'S CARD: the first order a pass places is the build
>      advice's card for that measure at the player's margin - its building
>      and its count - where nothing cuts it.
>   3. A CITY ON IT FOR DECADES, played by the test player with the city's
>      works left to it (-Dplaytest.autobuild's player): after every pass
>      every service it keeps is at or above the margin's target, or has
>      works under way, or the pass said why not - and each reason it gave
>      is true; it builds nothing outside its remit; it buys ground (0.7.77;
>      none until then) only for its own orders - each short of ground when
>      it was weighed, placed, of a building in its remit - bare ground only,
>      the city's ground grown in a pass by exactly what they bought; it
>      never borrows past the debt limit - the city's debt over a year of
>      GDP after every pass that borrowed at or under it (0.7.81; debt
>      payments over revenue until then), the ground's price in what it
>      borrowed - and a pass that begins over it orders nothing and borrows
>      nothing; nor spends the cash under a month's tax; and a staffed
>      order fits what the budget leaves.
>   4. A SAVED CITY PLAYS ON AS THE ONE IT WAS SAVED FROM: the city at the
>      end of section 3, saved and loaded, and both run on with it on - the
>      cash, the debt, every building and site, the ground it owns and what
>      it bought, the people and its own log, month by month, to the cent;
>      and (0.7.77) a town with no ground free, saved before its first pass,
>      buys its ground and builds the same, saved or not, to the cent.
>   5. WHAT HOLDS IT BACK, CAUSED: a debt limit of nothing, a limit that
>      binds and one that does not, a budget that cannot run a building, and
>      a first police station a town does not need - each one held as it
>      says, and the inbox's notice raised while it holds and settled when
>      it is switched off; and (0.7.81) THE LIMIT, A SHARE OF GDP: a bond's
>      face is what it adds to the debt; under the limit a bond that would
>      cross it is not taken; a city over it builds nothing and borrows
>      nothing with the cash to pay, the inbox saying why and naming the
>      toggle; with "Build from cash anyway" on it builds what the cash pays
>      for and borrows nothing, and with the cash at a month's tax holds for
>      the cash; and (0.7.77) THE GROUND IT BUYS: with none free it
>      buys the offer Build's land shortcut offers for the shortfall and
>      places the order, the inbox noting the purchase and why; its ground
>      and order borrowed for within the limit, and none bought at a limit
>      of nothing, nor over the limit (0.7.81) - unless the toggle is on and
>      the cash pays for both; no field bought for its ore, where the
>      shortcut would buy one; and with no offer at all, held, the inbox
> ... (3 more lines in the source)

**Uses:** [AutoBuilder](AutoBuilder.md) (195), [Game](Game.md) (51), [BuildAdvice](BuildAdvice.md) (25), [LongPlaytest](LongPlaytest.md) (25), [ChildcareCheck](ChildcareCheck.md) (14), [CityNeeds](CityNeeds.md) (11), [DecisionLog](DecisionLog.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (9), [LandParcel](LandParcel.md) (9), [GameFiles](GameFiles.md) (7), [LandManager](LandManager.md) (6), [LandMarket](LandMarket.md) (4), [DebtQuote](DebtQuote.md) (4), [Notice](Notice.md) (4), [Crime](Crime.md) (2), [CityLand](CityLand.md) (2), [Resource](Resource.md) (2), [BusinessInvestment](BusinessInvestment.md) (1), [TreasuryFund](TreasuryFund.md) (1), [BuildingManager](BuildingManager.md) (1), [ConstructionControl](ConstructionControl.md) (1), [Sector](Sector.md) (1), [CareType](CareType.md) (1), [NationalAccounts](NationalAccounts.md) (1)

## Sections

| line | section |
|---:|---|
| 122 | · 1 |
| 183 | · 2 |
| 232 | · 3 |
| 491 | · 4 |
| 623 | · 5 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 241 | `AutoBuildCheck.heldBy` | `new LinkedHashMap<>()` | The passes that held a kept service short of its target with nothing on site, by the cut that held it. |
| 243 | `AutoBuildCheck.builtBy` | `new LinkedHashMap<>()` | The buildings it ordered over the run, by measure. |
| 709 | `AutoBuildCheck.ORE_TONNES` | `1` | A tonne of iron under each offer of the ore fixture: any ore makes an offer not bare ground (LandMarket.bareGround()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 80 | `static int fails` |  |
| 81 | `static PrintStream out` |  |
| 82 | `static PrintStream quiet` |  |
| 235 | `static int passes, kept, atTarget, works, held, nothing, unexplained, borrowingPasses, ordered` | What a pass found, measure by measure, over the run. |
| 236 | `static int outsideRemit, underReserve, overBudget, overLimit, falseHolds` |  |
| 238 | `static int landPasses, landMismatch, landOrders, landNotItsOwn, oreBought, landBorrowPasses` | The ground it bought (0.7.77): passes the city's ground grew in, those not by exactly what its orders bought, orders that bought ground, those not its own short orders, offers holding ore, passes that borrowed with gr... |
| 239 | `static double landSqFt, landCost` |  |
| 245 | `static double worstShare` | The city's debt over a year of GDP after the passes that borrowed, the worst (0.7.81); passes that began over the limit, and the orders and bonds placed in them. |
| 246 | `static int overPasses, overOrders, overBonds` |  |
| 247 | `static double mostOver` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 78 | 950 | **type** `public class AutoBuildCheck` | Automatic building (0.7.73, batch N4): AutoBuilder's month held to its rules, in a city it builds for decades and in fixtures that cause each of the things that hold it back. |
| 84 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 90 | 5 | `static void cents(String label, double actual, double expected)` | Two money figures, in thousands, equal to the cent. |
| 96 | 5 | `static void quietly(Runnable r)` |  |
| 102 | 19 | `public static void main(String[] args) throws Exception` |  |

### 1 (lines 122-182)

| line | len | member | says |
|---:|---:|---|---|
| 124 | 58 | `static void theSettings(Path root)` |  |

### 2 (lines 183-231)

| line | len | member | says |
|---:|---:|---|---|
| 185 | 46 | `static void theAdvicesCard(Path root)` |  |

### 3 (lines 232-490)

| line | len | member | says |
|---:|---:|---|---|
| 249 | 98 | `static Game decades(Path root)` |  |
| 349 | 9 | `static int[] standingAndOnSite(Game g)` | Every template's standing and on-site count, by id. |
| 360 | 77 | `static void look(Game g, AutoBuilder ab, int[] before, double[] snap)` | One pass, read: what rose, what it did measure by measure, the debt, the cash and the budget. |
| 445 | 45 | `static boolean holdIsTrue(Game g, AutoBuilder ab, AutoBuilder.Step s)` | A hold, checked after the pass against the city as the pass left it. |

### 4 (lines 491-622)

| line | len | member | says |
|---:|---:|---|---|
| 493 | 75 | `static void savedAndLoaded(Path root, Game g)` |  |
| 577 | 45 | `static void groundSavedAndLoaded(Path root)` | A city buying its ground, saved and loaded (0.7.77): the town with no ground free, it on, saved before its first pass; both run on with it on - the cash, the debt, the ground owned and what it bought, every building a... |

### 5 (lines 623-1027)

| line | len | member | says |
|---:|---:|---|---|
| 625 | 82 | `static void whatHoldsIt(Path root)` |  |
| 712 | 6 | `static Game noGround(Path root, String name)` | The town with no ground free: its ground cut to what its buildings use (Game's land office relists for the smaller city). |
| 720 | 4 | `static AutoBuilder.Step firstLand(AutoBuilder ab)` | The first step of the last pass that bought ground, or null. |
| 726 | 156 | `static void theGround(Path root)` | THE GROUND IT BUYS (0.7.77): none free, at a limit of nothing and of the most, only ore on offer. |
| 884 | 5 | `static double annualised(Game g)` | A year of GDP as SummaryScreen's Debt/GDP reads it (Pieces.annualGdp()): the history's last twelve, scaled up from fewer. |
| 891 | 3 | `static double justOver(Game g)` | The debt limit's next step over a city's debt now (0.7.81): a limit it is within, which one road's bond crosses. |
| 899 | 6 | `static Game overTheLimit(Game g)` | A town over the debt limit (0.7.81): a twenty-year bond on the funding page for twice a year of its GDP, so the city's debt is past DEFAULT_DEBT_LIMIT whatever the bond's price, and its cash back where it was - the ca... |
| 911 | 105 | `static void theLimit(Path root, BuildAdvice.Measure roads)` | THE LIMIT, A SHARE OF GDP (0.7.81): a bond's face is what it adds to the debt; under the limit a bond that would cross it is not taken; over it nothing is built and nothing borrowed, with the cash to pay; and with Bui... |
| 1017 | 4 | `static AutoBuilder.Step stepFor(AutoBuilder ab, BuildAdvice.Measure m)` |  |
| 1022 | 5 | `static int siteCount(Game g)` |  |

