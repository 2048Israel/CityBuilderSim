# HousingDoorCheck.java - 625 lines · 12 methods · 8 constants · harnesses

`ham/citybuildersim/HousingDoorCheck.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

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
>      with no door - and the homes that finish are counted by size; since
>      call A (RES3c, 2026-11-25) a household the census line has no door for
>      lives past it, overcrowded (HousingMarket.overcrowding(), the 1891 and
>      1901 censuses' 7.04 million families in 6.26 million houses), counted
>      by the census's own line on its door - past two persons a room
>      overcrowded, within it sharing (REV-RES3b P1-4) - and sharing, the
>      families more than the doors they lease, rises
>      when the town's households outgrow its homes: over the months the
>      households seeking a home grow by more than the homes standing, the
>      households sharing a door rise, and by more a month than over the
>      months the homes catch up; the coda's fire puts households past the
>      line, overcrowded, none doorless. The months the landlords' order is trimmed or held
>      (its down payment, the lender's test, the bank's capital, or no land)
>      are printed beside it, not asserted: a month's order is not its stock
>      of crowding, and the comparison by order turned over with main 0.9.208
>      (behind 10.688 a month against 3.454 kept up on RES3's own town; 0.192
>      against 2.065 on the merge, its peak, 52.100 at m830, the month before
>      the order trimmed at m831; RES3c, 2026-11-26); and a household that
>      could pay and found no door boards with a housed family, counted by
> ... (6 more lines in the source)

**Uses:** [LongPlaytest](LongPlaytest.md) (31), [Game](Game.md) (12), [MoneyAudit](MoneyAudit.md) (6), [Mortgage](Mortgage.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [FamilyModel](FamilyModel.md) (3), [GameFiles](GameFiles.md) (2), [DebtManager](DebtManager.md) (2), [TreasuryFund](TreasuryFund.md) (2), [Sectors](Sectors.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [Equity](Equity.md) (1), [LandManager](LandManager.md) (1), [HousingChoice](HousingChoice.md) (1), [Founding](Founding.md) (1), [LandParcel](LandParcel.md) (1)

## Sections

| line | section |
|---:|---|
| 474 | THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), line for |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 83 | `HousingDoorCheck.SEED` | `12` | The seed R0h's ensemble found it on, and today's main holds its landlords at the door on. |
| 85 | `HousingDoorCheck.FROM` | `430, TO = 900` | The months watched, months already run: HL1 retains m431-500 and continues to m900; its first capital trim is m502, eight fire by m900. |
| 87 | `HousingDoorCheck.TARGET` | `.02` | The ensemble's AUTO2: the autopilot at this target, every month (Ensemble.TARGET). |
| 88 | `HousingDoorCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 90 | `HousingDoorCheck.BOOM` | `831` | Call A's first town's boom (a6b4e2369): 36.993 households overcrowded, the landlords' 389 Houses trimmed to 14 for the down payment. |
| 92 | `HousingDoorCheck.CODA` | `6` | The months played past the window once the fire has taken its homes, for the payers that find no door to board (call A over R1g). |
| 94 | `HousingDoorCheck.FIRE` | `4` | The fire at the window's end leaves one structure in this many of every housing class standing, the rest taken occupied as they stand (call A over R1g's coda). |
| 133 | `HousingDoorCheck.asks` | `new ArrayList<>()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `static PrintStream out` |  |
| 80 | `static int fails` |  |
| 98 | `int months, first` |  |
| 99 | `double sum, most` |  |
| 115 | `int month` |  |
| 116 | `String building` |  |
| 117 | `boolean family` |  |
| 118 | `long asked, passed` |  |
| 119 | `double till, doorOne, shortfall` |  |
| 120 | `boolean groundForOne` |  |
| 121 | `String line` |  |
| 122 | `long built` |  |
| 124 | `boolean markedForCapital` | The month ended with the landlords marked as refused a mortgage for the bank's capital (what LongPlaytest counts). |
| 126 | `double cost, held, offered, raised` | The order's cost, the till before the owners' issue, what consider() offered the owners this month and what they put in, and whether the door would have refused the order's mortgage for the bank's capital on that till... |
| 127 | `boolean shortBeforeOwners` |  |
| 129 | `boolean quoteShut` | The landlord's quote supported no issue this month, so consider() offered nothing (Exchange.quoteSupportsIssue()). |
| 132 | `static Ask pending` |  |
| 479 | `static boolean stuck` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 77 | 549 | **type** `public final class HousingDoorCheck` | The landlords' order at the lender's door (HN1, 2026-11-18): a played city's households held from the homes that fit them while the plan passed those homes and the door would not write the whole order. |
| 97 | 10 | **type** `static final class Tally` | Months of one kind and the households overcrowded in them (call A). |
| 100 | 4 | `void add(int m, double crowded)` _(in HousingDoorCheck.Tally)_ |  |
| 104 | 1 | `double mean()` _(in HousingDoorCheck.Tally)_ |  |
| 105 | 1 | `String text()` _(in HousingDoorCheck.Tally)_ |  |
| 108 | 4 | `static void check(String label, boolean ok)` |  |
| 114 | 17 | **type** `static final class Ask` | One month's order at the door: what the plan passed, the till it was judged on, and the door's price of one. |
| 135 | 283 | `public static void main(String[] args) throws Exception` |  |
| 420 | 34 | `static Ask ask(Game g, long asked, java.util.function.LongToDoubleFunction costOf, double cash, Mortgage.Decision found)` | The door's own facts for one of the homes Mortgage.decide() was asked about, read at the moment it was asked. |
| 456 | 5 | `static long builtIn(String line)` | How many the advisor's line says were built: "Built 4 Low-Rise Apartments ...", or none. |
| 463 | 10 | `private static String housingSnapshot(Game g)` | A failed played finding keeps the actual budgets, occupancy and posted rents that caused it. |

### THE FOUNDING AND THE RHYTHM - the ensemble's foundAndPlay(), line for (lines 474-625)

| line | len | member | says |
|---:|---:|---|---|
| 491 | 28 | `static void boarders(Game g, MoneyAudit.Households households, int[] board, double[] boardMost)` | A month's payers boarding with the housed (call A over R1g): [0] the months with any, [1] of them every boarder counted by the census's own line on its door, overcrowded past two persons a room and within the line oth... |
| 525 | 19 | `static String roundTrip(Game g, int[] trip)` | The city saved to its own scratch folder's slot 10 and loaded into another Game, as a player's reload, with boarders standing (REV-RES3b P1-2): [1] 1 if the boarders, the overcrowded, those within the line and the boa... |
| 544 | 11 | `static void run(Game g, int months, Runnable after)` |  |
| 556 | 69 | `static void foundAndPlay(Game g, Runnable after)` |  |

