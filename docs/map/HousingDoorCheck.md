# HousingDoorCheck.java - 323 lines · 6 methods · 5 constants · harnesses

`ham/citybuildersim/HousingDoorCheck.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> The landlords' order at the lender's door (HN1, 2026-11-18): a played
> city's households held from the homes that fit them while the plan passed
> those homes and the door would not write the whole order.
> 
> WHY: R0h's 80-run ensemble found AUTO2 and ZERO_SLACK seed 12 with 3.6 to
> 13.2 households with no door at all at months 744-747 - LongPlaytest's
> watch, more than one - on main's model too. The probe behind this harness
> (docs/queue/HN1-notes.md) found the landlords asking 26-35 Low-Rise
> Apartments a month from m699 to m746, the family segment 2,257 to 2,870
> doors short, and the lender's door refusing every whole order for the
> bank's capital or the down payment at the purchase's price, with 32,000 in
> the till: five blocks bought outright. Nothing was built for four years;
> 2,959 of 5,918 households doubled up, the ceiling, and then past it. Today's
> main no longer takes seed 12 to the ceiling, but its landlords are held at
> the door the same way in some twenty months of m437-m496 with the door's
> price of a block in the till. This harness plays that city - the
> ensemble's AUTO2 seed 12, its founding and rhythm line for line
> (the driver in tools/ensemble/, its foundAndPlay()) - through those months, its
> player and its automatic works as they play, and watches the landlords'
> every order there (Game.watchOrders()).
> 
> What this has to prove:
>   1. the fixture: the plan passes the landlords orders the door does not
>      write whole, while their homes' segment is short of doors;
>   2. in no month is a landlord whose till holds the door's price of one of
>      the homes the plan passed, on ground free for one, while that
>      segment's households have no door of their own, left with none
>      (Game.considerOnMortgage(), THE DOOR'S NO IS TO THE ORDER);
>   3. an order the door cut is a halving of what the plan passed, and the
>      advisor says it was trimmed; a month the door trimmed for the bank's
>      capital is not left marked as held for it (REV-HN1-1: LongPlaytest
>      counts the mark as a month held for the bank's leverage);
>   4. LongPlaytest's watch holds - no month leaves more than one household
>      with no door - and the homes that finish are counted by size.

**Uses:** [LongPlaytest](LongPlaytest.md) (28), [Game](Game.md) (6), [GameFiles](GameFiles.md) (2), [Mortgage](Mortgage.md) (2), [FamilyModel](FamilyModel.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [DebtManager](DebtManager.md) (2), [TreasuryFund](TreasuryFund.md) (2), [Sectors](Sectors.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [LandManager](LandManager.md) (1), [Founding](Founding.md) (1), [LandParcel](LandParcel.md) (1)

## Sections

| line | section |
|---:|---|
| 235 | THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), line for |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 51 | `HousingDoorCheck.SEED` | `12` | The seed R0h's ensemble found it on, and today's main holds its landlords at the door on. |
| 53 | `HousingDoorCheck.FROM` | `430, TO = 500` | The months watched, months already run: the door's holds on today's main fall in m437-m496. |
| 55 | `HousingDoorCheck.TARGET` | `.02` | The ensemble's AUTO2: the autopilot at this target, every month (Ensemble.TARGET). |
| 56 | `HousingDoorCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 78 | `HousingDoorCheck.asks` | `new ArrayList<>()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `static PrintStream out` |  |
| 48 | `static int fails` |  |
| 65 | `int month` |  |
| 66 | `String building` |  |
| 67 | `boolean family` |  |
| 68 | `long asked, passed` |  |
| 69 | `double till, doorOne, shortfall` |  |
| 70 | `boolean groundForOne` |  |
| 71 | `String line` |  |
| 72 | `long built` |  |
| 74 | `boolean markedForCapital` | The month ended with the landlords marked as refused a mortgage for the bank's capital (what LongPlaytest counts). |
| 77 | `static Ask pending` |  |
| 240 | `static boolean stuck` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 45 | 279 | **type** `public final class HousingDoorCheck` | The landlords' order at the lender's door (HN1, 2026-11-18): a played city's households held from the homes that fit them while the plan passed those homes and the door would not write the whole order. |
| 58 | 4 | `static void check(String label, boolean ok)` |  |
| 64 | 12 | **type** `static final class Ask` | One month's order at the door: what the plan passed, the till it was judged on, and the door's price of one. |
| 80 | 124 | `public static void main(String[] args) throws Exception` |  |
| 206 | 21 | `static Ask ask(Game g, long asked, java.util.function.LongToDoubleFunction costOf, double cash, Mortgage.Decision found)` | The door's own facts for one of the homes Mortgage.decide() was asked about, read at the moment it was asked. |
| 229 | 5 | `static long builtIn(String line)` | How many the advisor's line says were built: "Built 4 Low-Rise Apartments ...", or none. |

### THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), line for (lines 235-323)

| line | len | member | says |
|---:|---:|---|---|
| 242 | 11 | `static void run(Game g, int months, Runnable after)` |  |
| 254 | 69 | `static void foundAndPlay(Game g, Runnable after)` |  |

