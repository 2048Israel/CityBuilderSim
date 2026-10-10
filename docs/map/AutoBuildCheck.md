# AutoBuildCheck.java - 1,469 lines · 32 methods · 5 constants · harnesses

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
>      limit of 240% of a year's GDP (0.7.101, Jerus's A6; 60% from 0.7.81,
>      15% of revenue until then), "Build from cash anyway" off; the sliders
>      keep to their steps and their ends - the limit's to 600%, past 300%
>      as he asked; switching it, moving a slider and the toggle are
>      decisions; off, a month's pass does nothing; a save from before it
>      loads with it off at its defaults, one from 0.7.73 to 0.7.80 - its
>      limit a share of revenue - with the default limit and the toggle off,
>      and one from 0.7.81 to 0.7.100 at the old default with the new one.
>   2. THE ADVICE'S CARD: the first order a pass places is the build
>      advice's card for that measure at the player's margin - its building
>      and its count - where nothing cuts it.
>   3. A CITY ON IT FOR DECADES, played by the test player with the city's
>      works left to it (-Dplaytest.autobuild's player): after every pass
>      every service it keeps is at or above the margin's target, or has
>      works under way, or the pass said why not - and each reason it gave
>      is true, a hold for the money or the ground only where no building in
>      the advice's ranking could be paid for one of (0.7.101); it builds
>      nothing outside its remit; it buys ground (0.7.77; none until then)
>      only for its own orders - each short of ground when it was weighed,
>      placed, of a building in its remit - bare ground only, the city's
>      ground grown in a pass by exactly what they bought; it never borrows
>      past the debt limit - the city's debt over a year of GDP after every
>      pass that borrowed at or under it (0.7.81; debt payments over revenue
>      until then; since 0.7.101 all the city owes over the output it has
>      recorded), the ground's price in what it borrowed - and a pass that
>      begins over it orders nothing and borrows nothing; nor spends the
>      cash under a month's tax; and (0.7.104) every order of a building the
>      payback holds - a school, a station, a prison, a line - paid back at
>      the count it placed, as it weighed it (a staffed order fitting what
>      the budget left until then).
>   4. A SAVED CITY PLAYS ON AS THE ONE IT WAS SAVED FROM: the city at the
>      end of section 3, saved and loaded, and both run on with it on - the
>      cash, the debt, every building and site, the ground it owns and what
>      it bought, the people and its own log, month by month, to the cent;
>      and (0.7.77) a town with no ground free, saved before its first pass,
>      buys its ground and builds the same, saved or not, to the cent.
>   5. WHAT HOLDS IT BACK, CAUSED: a debt limit of nothing, a limit that
>      binds and one that does not - each one held as it says, and the
>      inbox's notice raised while it holds and settled when it is switched
>      off; (0.7.104, Jerus's decision of 2026-10-10) an essential service
>      ordered in a town spending past its revenue, where the budget held it
>      until then, and a school held where it would not pay back - the inbox
>      giving its figure - and ordered in a twin where it would; and (0.7.81) THE LIMIT, A SHARE
>      OF GDP: a bond's face is what it adds to the debt (since 0.7.101 with
>      the overdraft it clears; the debt all the city owes - its overdraft
>      and its central bank's advances counted - and a young city's year
> ... (22 more lines in the source)

**Uses:** [AutoBuilder](AutoBuilder.md) (279), [Game](Game.md) (78), [BuildAdvice](BuildAdvice.md) (53), [LongPlaytest](LongPlaytest.md) (25), [DecisionLog](DecisionLog.md) (24), [ChildcareCheck](ChildcareCheck.md) (20), [CityNeeds](CityNeeds.md) (18), [BuildingsTemplate](BuildingsTemplate.md) (18), [LandManager](LandManager.md) (10), [LandParcel](LandParcel.md) (9), [GameFiles](GameFiles.md) (8), [DebtQuote](DebtQuote.md) (6), [Notice](Notice.md) (5), [LandMarket](LandMarket.md) (4), [NationalAccounts](NationalAccounts.md) (3), [EducationType](EducationType.md) (2), [CityLand](CityLand.md) (2), [Resource](Resource.md) (2), [BusinessInvestment](BusinessInvestment.md) (1), [TreasuryFund](TreasuryFund.md) (1), [Founding](Founding.md) (1), [BuildingManager](BuildingManager.md) (1), [ConstructionControl](ConstructionControl.md) (1), [Sector](Sector.md) (1), [CareType](CareType.md) (1), [EconomyManager](EconomyManager.md) (1)

## Sections

| line | section |
|---:|---|
| 141 | · 1 |
| 215 | · 2 |
| 265 | · 3 |
| 655 | · 4 |
| 793 | · 5 |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 278 | `AutoBuildCheck.heldBy` | `new LinkedHashMap<>()` | The passes that held a kept service short of its target with nothing on site, by the cut that held it. |
| 280 | `AutoBuildCheck.builtBy` | `new LinkedHashMap<>()` | The buildings it ordered over the run, by measure. |
| 952 | `AutoBuildCheck.ROOM` | `50_000` | A month's property tax withRoom() adds, in thousands: $50M, far past what any pass in the town orders runs to (0.7.101; the payback's since 0.7.104). |
| 1054 | `AutoBuildCheck.OVER_SQ_FT` | `1_000_000` | The over-full fixture's deficit, in square feet: a million, a sixth of Jerus's 5.66M at month 416 (runs/fixN5-notes.md) - any deficit makes the point. |
| 1095 | `AutoBuildCheck.ORE_TONNES` | `1` | A tonne of iron under each offer of the ore fixture: any ore makes an offer not bare ground (LandMarket.bareGround()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 99 | `static int fails` |  |
| 100 | `static PrintStream out` |  |
| 101 | `static PrintStream quiet` |  |
| 268 | `static int passes, kept, atTarget, works, held, nothing, unexplained, borrowingPasses, ordered` | What a pass found, measure by measure, over the run. |
| 269 | `static int outsideRemit, underReserve, overLimit, falseHolds` |  |
| 271 | `static int paybackOrders, unpaid` | Orders of a building the payback holds (0.7.104), and those not paid back at their count as the pass weighed it. |
| 273 | `static int walked` | Orders placed past the advice's first card, which the money or the ground could not pay for one of, or (0.7.104) which would not pay back - the budget would not run until then (0.7.101). |
| 275 | `static int landPasses, landMismatch, landOrders, landNotItsOwn, oreBought, landBorrowPasses` | The ground it bought (0.7.77): passes the city's ground grew in, those not by exactly what its orders bought, orders that bought ground, those not its own short orders, offers holding ore, passes that borrowed with gr... |
| 276 | `static double landSqFt, landCost` |  |
| 282 | `static double worstShare` | The city's debt over a year of GDP after the passes that borrowed, the worst (0.7.81); passes that began over the limit, and the orders and bonds placed in them. |
| 283 | `static int overPasses, overOrders, overBonds` |  |
| 284 | `static double mostOver` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 97 | 1373 | **type** `public class AutoBuildCheck` | Automatic building (0.7.73, batch N4): AutoBuilder's month held to its rules, in a city it builds for decades and in fixtures that cause each of the things that hold it back. |
| 103 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 109 | 5 | `static void cents(String label, double actual, double expected)` | Two money figures, in thousands, equal to the cent. |
| 115 | 5 | `static void quietly(Runnable r)` |  |
| 121 | 19 | `public static void main(String[] args) throws Exception` |  |

### 1 (lines 141-214)

| line | len | member | says |
|---:|---:|---|---|
| 143 | 71 | `static void theSettings(Path root)` |  |

### 2 (lines 215-264)

| line | len | member | says |
|---:|---:|---|---|
| 217 | 47 | `static void theAdvicesCard(Path root)` |  |

### 3 (lines 265-654)

| line | len | member | says |
|---:|---:|---|---|
| 286 | 132 | `static Game decades(Path root)` |  |
| 420 | 18 | `static java.util.function.Consumer<Boolean> passReader(Game g)` | The decades' reading of a pass, before and after it, by look() (0.7.102: a function, so the copy that borrows is read the same). |
| 440 | 9 | `static int[] standingAndOnSite(Game g)` | Every template's standing and on-site count, by id. |
| 451 | 84 | `static void look(Game g, AutoBuilder ab, int[] before, double[] snap)` | One pass, read: what rose, what it did measure by measure, the debt, the cash and (0.7.104) the payback. |
| 547 | 9 | `static boolean holdIsTrue(Game g, AutoBuilder ab, AutoBuilder.Step s)` | A hold, checked after the pass against the city as the pass left it. |
| 558 | 13 | `static boolean noneOtherPays(Game g, AutoBuilder ab, BuildAdvice.Measure m)` | The advice's ranking for a measure walked as the pass walks it (0.7.101), on the city as the pass left it: none of its buildings pays back (0.7.104; is run by the budget until then) and is paid for one of, its ground ... |
| 573 | 13 | `static boolean paysForOne(Game g, AutoBuilder ab, BuildAdvice.Measure m, BuildAdvice.Suggestion c, boolean fromCash)` | Whether one of a card pays back where the payback holds it (0.7.104; is run by the budget until then) and is paid for, its ground and all: at or under the limit by the cash over the reserve and past it a bond that kee... |
| 588 | 12 | `static BuildAdvice.Suggestion cardFor(Game g, AutoBuilder ab, BuildAdvice.Measure m, BuildingsTemplate t)` | The advice's card for a measure whose building is t, walking its ranking as the pass does (0.7.104): null where t is not in it. |
| 602 | 52 | `static boolean heldBuildingIsTrue(Game g, AutoBuilder ab, AutoBuilder.Step s)` | The held building's own reason, true after the pass. |

### 4 (lines 655-792)

| line | len | member | says |
|---:|---:|---|---|
| 657 | 81 | `static void savedAndLoaded(Path root, Game g)` |  |
| 747 | 45 | `static void groundSavedAndLoaded(Path root)` | A city buying its ground, saved and loaded (0.7.77): the town with no ground free, it on, saved before its first pass; both run on with it on - the cash, the debt, the ground owned and what it bought, every building a... |

### 5 (lines 793-1469)

| line | len | member | says |
|---:|---:|---|---|
| 795 | 155 | `static void whatHoldsIt(Path root)` |  |
| 968 | 8 | `static void withRoom(Game g)` | The town fixture with room (0.7.101): its healthcare and education bills - four medical schools and a university, three times its revenue - cleared, ROOM more property tax, and its accounts refreshed; the pass reads t... |
| 986 | 66 | `static void theWalk(Path root)` | THE RANKING, WALKED (0.7.101): with the cash for one of a building the advice ranks after its first card but not for one of the first, and no bond within the limit (the town brought just under it, nearTheLimit(); a li... |
| 1057 | 4 | `static double oneCosts(Game g, BuildAdvice.Suggestion c)` | One of a card, its ground and all (AutoBuilder.groundFor()): what the pass weighs for one; +∞ with no ground to be had. |
| 1069 | 24 | `static void overFull(Path root)` | ONLY THE ORDER'S OWN GROUND (0.7.101, Jerus's decision A5): in a town whose buildings stand on more ground than it owns, an order on ground of its own could not be placed (Game.buildStack()'s NO_LAND) - so it buys non... |
| 1098 | 6 | `static Game noGround(Path root, String name)` | The town with no ground free: its ground cut to what its buildings use (Game's land office relists for the smaller city). |
| 1106 | 4 | `static AutoBuilder.Step firstLand(AutoBuilder ab)` | The first step of the last pass that bought ground, or null. |
| 1112 | 159 | `static void theGround(Path root)` | THE GROUND IT BUYS (0.7.77): none free, at a limit of nothing and of the most, only ore on offer. |
| 1273 | 5 | `static double annualised(Game g)` | A year of GDP as SummaryScreen's Debt/GDP reads it (Pieces.annualGdp()): the history's last twelve, scaled up from fewer. |
| 1280 | 3 | `static double justOver(Game g)` | The debt limit's next step over a city's debt now (0.7.81): a limit it is within. |
| 1293 | 12 | `static double nearTheLimit(Game g)` | A town brought just under the debt limit's next step over its debt (0.7.101): a twenty-year bond on the funding page as large as keeps the city's debt within that step - found by halving, to the bond's granule - so th... |
| 1310 | 6 | `static Game overTheLimit(Game g)` | A town over the debt limit (0.7.81): a twenty-year bond on the funding page for twice a year of its GDP, so the city's debt is past DEFAULT_DEBT_LIMIT whatever the bond's price, and its cash back where it was - the ca... |
| 1322 | 136 | `static void theLimit(Path root, BuildAdvice.Measure roads)` | THE LIMIT, A SHARE OF GDP (0.7.81): a bond's face is what it adds to the debt; under the limit a bond that would cross it is not taken; over it nothing is built and nothing borrowed, with the cash to pay; and with Bui... |
| 1459 | 4 | `static AutoBuilder.Step stepFor(AutoBuilder ab, BuildAdvice.Measure m)` |  |
| 1464 | 5 | `static int siteCount(Game g)` |  |

