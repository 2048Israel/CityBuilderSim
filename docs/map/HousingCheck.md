# HousingCheck.java - 1,133 lines · 11 methods · 1 constants · harnesses

`ham/citybuildersim/HousingCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> Audits the three subsystems that describe the same housing, every month, and
> makes them agree. Not part of the game.
> 
> WHY THIS EXISTS. Three classes hold a piece of one fact - how many doors this
> city has and who is behind them - and nothing made them say the same thing:
> 
>   BuildingManager  built the doors, and knows their SIZES
>   FamilyModel      puts households behind them, and knows who did not fit
>   CommercialHandler owns them as a business, and bills whoever is in one
> 
> On 2026-09-08 a UI redo drew two of those figures next to each other for the
> first time and reported an apparent contradiction: 15,181 homes standing
> empty in a city where 17,416 households were doubled up. It looked like two
> subsystems disagreeing and it was not - the empty homes were STUDIOS, a studio
> cannot take a child, and every one of the doubled-up households had a child.
> Both were right. Nothing in the game could say so, and it cost a day.
> 
> The lesson is not "add a counter". It is that three descriptions of one fact
> need an identity between them, checked every month, or the next person to draw
> two of them side by side will lose the same day. So:
> 
>   1. EVERY DOOR IS ACCOUNTED FOR. Let plus empty equals owned, and the
>      per-size breakdown sums to the same total. If it ever does not, one of
>      the three is counting a building the others cannot see.
> 
>   2. EVERY HOUSEHOLD IS ACCOUNTED FOR. The ones in a home of their own, plus
>      the ones doubled up, is all of them. Nobody is placed twice and nobody
>      falls between the two.
> 
>   3. RENT PAID IS RENT RECEIVED, and both are rentWeight x rentPrice. This is
>      the money identity: households are outside MoneyAudit's pool, so a rent
>      the landlords collect and nobody pays would be invisible to it. It has
>      been wrong before - the two sides used to be computed separately and
>      drifted - which is why Game reads the landlords' figure and hands it
>      straight to the households rather than working it out twice.
> 
>   4. AND AN EMPTY HOME BESIDE A HOMELESS FAMILY HAS A REASON. The assertion
>      the finding above actually wanted: whenever this city holds vacancies AND
>      doubled-up households at the same time, something must SAY WHY - either
>      the spare doors are too small to take a child, or they are the wrong size
>      for the households queueing. A city that cannot explain it is a city
>      where the two subsystems really have come apart.
> 
>   5. AND ALL OF IT SURVIVES A SAVE, because every figure above is struck
>      inside the month tick and three of them were reading zero on a reloaded
>      city until 2026-09-09.
> 
>   6. THE LANDLORDS BUILD BEFORE ANYONE CAN STAFF THEM (HD1, 2026-10-29).
>      Real Estate has workers now - an office post and a maintenance post in
>      a Studio or a Low-Rise, the National Apartment Association's rule - and
>      the landlords are the one owner that does not ask the city whether it
>      could staff what they order: homes come before workers. The section
>      causes a city that could not staff a block and asserts the landlords
>      still plan one, the investors' card does not refuse it for staffing,
>      and the block, once standing, counts whole in the city's housing, bills
>      its rent with its posts mostly empty, and pays the wages of those filled.
> 
>   7. HEADROOM IS SPARE HOMES OVER DEMAND (RN1-H). At equal demand and
>      capacity the plan orders the margin, counts homes on site once,
>      and stops at that margin, both before and after they finish.

**Uses:** [RealEstate](RealEstate.md) (22), [Game](Game.md) (15), [FamilyModel](FamilyModel.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (10), [BusinessInvestment](BusinessInvestment.md) (10), [BuildCard](BuildCard.md) (8), [Sector](Sector.md) (6), [GameFiles](GameFiles.md) (5), [FamilyStructure](FamilyStructure.md) (4), [PopulationCohorts](PopulationCohorts.md) (3), [BuildingManager](BuildingManager.md) (2), [JobType](JobType.md) (2), [Observation](Observation.md) (2), [AgeBand](AgeBand.md) (2), [HouseholdAccounts](HouseholdAccounts.md) (1), [Construction](Construction.md) (1), [EconomyManager](EconomyManager.md) (1), [Sectors](Sectors.md) (1), [PriceLevel](PriceLevel.md) (1), [DebtManager](DebtManager.md) (1), [Founding](Founding.md) (1), [SectorState](SectorState.md) (1), [PayTier](PayTier.md) (1)

## Sections

| line | section |
|---:|---|
| 111 | · · 1. every door is accounted for |
| 135 | · · 2. every household is accounted for |
| 162 | · · 3. rent paid is rent received |
| 186 | · · 3b. the two segments partition everything |
| 202 | · · 3c. rent never falls through the floor |
| 248 | · · 3d. repairs are a flow, not a number |
| 354 | · 4b. A CITY THAT REALLY DOES HOLD BOTH AT ONCE. |
| 398 | · WHAT THIS SECTION CAN AND CANNOT CLAIM, REWRITTEN 2026-09-09. |
| 482 | · 5. AND ALL OF IT SURVIVES A SAVE. |
| 547 | · 4b. A SMALL HOUSEHOLD TAKES A BIG DOOR WHEN THE SMALL ONES RUN OUT. |
| 639 | · 6. THE LANDLORDS BUILD BEFORE ANYONE CAN STAFF THEM (HD1, 2026-10-29) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 72 | `HousingCheck.TOLERANCE` | `1e-6` | Everything here is in thousands, so a tenth of a cent is plenty. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 67 | 1067 | **type** `public class HousingCheck` | Audits the three subsystems that describe the same housing, every month, and makes them agree. |
| 74 | 8 | `static void near(String what, int month, double actual, double expected)` |  |
| 83 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 88 | 712 | `public static void main(String[] args)` |  |
| 801 | 86 | `static void homesKeepHeadroomOverDemand()` |  |
| 888 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 901 | 15 | `static boolean assertGlutLegsNotLifted(ham.citybuildersim.sectors.RealEstate re, int month)` | A leg under GLUT_PRESSURE is its own cost times its scarcity and is not lifted to cover the carry (RN1, 2026-11-09). |
| 930 | 85 | `static void heldRateRentsItsGlutAtNoLift()` | RN1, 2026-11-09. |
| 1024 | 63 | `static void aClosedHomesDebtIsTheLandlordsLoss()` | RN1, 2026-11-09. |
| 1100 | 20 | `static void anExactPackingLeavesNobody()` | AN EXACT PACKING LEAVES NOBODY (OE1, 2026-11-20). |
| 1121 | 3 | `static void isNear(String label, double actual, double expected)` |  |
| 1125 | 8 | `static void assertNotMore(String what, int month, double actual, double ceiling)` |  |

