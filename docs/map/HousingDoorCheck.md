# HousingDoorCheck.java - 393 lines · 6 methods · 5 constants · harnesses

`ham/citybuildersim/HousingDoorCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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
> HL1's merged path moves the first capital-refused trim from m495 to m502, just beyond that old cutoff; the watch now retains
> those months and continues to m900 (eight capital trims), with the households' cash identity checked every played month too.
> 
> What this has to prove:
>   1. the fixture: the plan passes the landlords orders while their homes'
>      segment is short of doors; before FB15 the door cut nine of them for a
>      down payment raised at the plan's price (m600-m706), and since FB15
>      (HN1-A, the order at the door's own price) it cuts or holds none for
>      its down payment (OrderDoorCheck 2 causes the case in one month);
>   2. in no month is a landlord whose till holds the door's price of one of
>      the homes the plan passed, on ground free for one, while that
>      segment's households have no door of their own, left with none
>      (Game.considerOnMortgage(), THE DOOR'S NO IS TO THE ORDER);
>   3. an order the door cut is a halving of what the plan passed, and the
>      advisor says it was trimmed; a month the door trimmed for the bank's
>      capital is not left marked as held for it (REV-HN1-1: LongPlaytest
>      counts the mark as a month held for the bank's leverage); since MC1
>      an order the door would refuse for the bank's capital is put to the
>      owners first, and the door trims for the capital only what their
>      issue left uncovered (that trim caused in CreditCrunchCheck 3);
>   4. LongPlaytest's watch holds - no month leaves more than one household
>      with no door - and the homes that finish are counted by size;
>   5. every played month's households' cash identity closes beside the accepted and refused mortgage orders (HL1).

**Uses:** [LongPlaytest](LongPlaytest.md) (29), [Game](Game.md) (6), [MoneyAudit](MoneyAudit.md) (3), [Mortgage](Mortgage.md) (3), [GameFiles](GameFiles.md) (2), [FamilyModel](FamilyModel.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [DebtManager](DebtManager.md) (2), [TreasuryFund](TreasuryFund.md) (2), [Sectors](Sectors.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [Equity](Equity.md) (1), [LandManager](LandManager.md) (1), [Founding](Founding.md) (1), [LandParcel](LandParcel.md) (1)

## Sections

| line | section |
|---:|---|
| 305 | THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), line for |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 60 | `HousingDoorCheck.SEED` | `12` | The seed R0h's ensemble found it on, and today's main holds its landlords at the door on. |
| 62 | `HousingDoorCheck.FROM` | `430, TO = 900` | The months watched, months already run: HL1 retains m431-500 and continues to m900; its first capital trim is m502, eight fire by m900. |
| 64 | `HousingDoorCheck.TARGET` | `.02` | The ensemble's AUTO2: the autopilot at this target, every month (Ensemble.TARGET). |
| 65 | `HousingDoorCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 92 | `HousingDoorCheck.asks` | `new ArrayList<>()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `static PrintStream out` |  |
| 57 | `static int fails` |  |
| 74 | `int month` |  |
| 75 | `String building` |  |
| 76 | `boolean family` |  |
| 77 | `long asked, passed` |  |
| 78 | `double till, doorOne, shortfall` |  |
| 79 | `boolean groundForOne` |  |
| 80 | `String line` |  |
| 81 | `long built` |  |
| 83 | `boolean markedForCapital` | The month ended with the landlords marked as refused a mortgage for the bank's capital (what LongPlaytest counts). |
| 85 | `double cost, held, offered, raised` | The order's cost, the till before the owners' issue, what consider() offered the owners this month and what they put in, and whether the door would have refused the order's mortgage for the bank's capital on that till... |
| 86 | `boolean shortBeforeOwners` |  |
| 88 | `boolean quoteShut` | The landlord's quote supported no issue this month, so consider() offered nothing (Exchange.quoteSupportsIssue()). |
| 91 | `static Ask pending` |  |
| 310 | `static boolean stuck` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 340 | **type** `public final class HousingDoorCheck` | The landlords' order at the lender's door (HN1, 2026-11-18): a played city's households held from the homes that fit them while the plan passed those homes and the door would not write the whole order. |
| 67 | 4 | `static void check(String label, boolean ok)` |  |
| 73 | 17 | **type** `static final class Ask` | One month's order at the door: what the plan passed, the till it was judged on, and the door's price of one. |
| 94 | 167 | `public static void main(String[] args) throws Exception` |  |
| 263 | 34 | `static Ask ask(Game g, long asked, java.util.function.LongToDoubleFunction costOf, double cash, Mortgage.Decision found)` | The door's own facts for one of the homes Mortgage.decide() was asked about, read at the moment it was asked. |
| 299 | 5 | `static long builtIn(String line)` | How many the advisor's line says were built: "Built 4 Low-Rise Apartments ...", or none. |

### THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), line for (lines 305-393)

| line | len | member | says |
|---:|---:|---|---|
| 312 | 11 | `static void run(Game g, int months, Runnable after)` |  |
| 324 | 69 | `static void foundAndPlay(Game g, Runnable after)` |  |

