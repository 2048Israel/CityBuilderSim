# Offending.java - 169 lines · 5 methods · 0 constants · model

`ham/citybuildersim/Offending.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Who is at risk of offending, sorted by reason, and the thefts handed to
> them.
> 
> ==================== WHO IS AT RISK OF OFFENDING (2026-09-11) ====================
> 
> Every adult at liberty, sorted once into Jerus's groups, cell by cell -
> because the cells are the only place the reasons meet: the pool knows
> who is out of work, the housing match who has no door and who is
> crowded, and the household books who is going short. Where a group
> overlaps another, the higher weight counts:
> 
>   no home        the evicted; the families and the seekers with no door  5
>   past EI        out of work with a home, EI run out                      4
>   short of money of what is left, the share that cannot buy a basket     3
>   on EI          out of work with a home, still drawing                   2
>   crowded        of what is left, the share doubled up or flatsharing     2
>   no reason      everybody else                                          .1
> 
> The retired, the orphans and the prisoners are nobody's offenders: the
> adult band is 18 to 70, and a prisoner is not at liberty.
> 
> Each cell's weight - its adults times their weights, plus what the
> police are missing - is what the thefts are handed back by.
> 
> ==================== WHERE IT CAME FROM ====================
> 
> This was Game's "WHO IS AT RISK OF OFFENDING" section until 2026-09-18,
> when it moved out with its text intact: crimeCauses() as causes(), the
> two static helpers it sorts with, steal() under its own name, and
> unhousedShareOfCity(). It holds no month flows of its own - the figures
> live on Crime - so Game keeps only unhousedShareOfCity() as a delegation,
> because that one is public, and calls the two from the crime step of the
> month in the same place it always did.
> 
> WHY IT LEFT. Game.java was 8,200 lines, and a session reading who offends
> had to carry the month, the save and the treasury with it. A
> mechanic that has the shape of a class - its own month, its own figures,
> one place it is called from - is its own file since 2026-09-18, and Game
> keeps what every reader goes through: the getters, and the order of the
> month. The interface went the same way the same day. See the project's
> splitting-game.md.

**Uses:** [Crime](Crime.md) (19), [FamilyModel](FamilyModel.md) (5), [UnemployedHousehold](UnemployedHousehold.md) (5), [Game](Game.md) (3), [Household](Household.md) (3), [HouseholdBalance](HouseholdBalance.md) (2), [Sectors](Sectors.md) (2), [StudentHousehold](StudentHousehold.md) (1), [AgeBand](AgeBand.md) (1), [FamilyStructure](FamilyStructure.md) (1), [EconomyManager](EconomyManager.md) (1)

**Used by (1):** [Game](Game.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 46 | 124 | **type** `public final class Offending` | Who is at risk of offending, sorted by reason, and the thefts handed to them. |
| 54 | 46 | `Crime.Causes causes(Game game, double coverage, double[] cellWeight)` | Was Game.crimeCauses(); the body is that one, through Game's getters. |
| 102 | 10 | `private static void sortHoused(Crime.Causes into, double adults, double noDoor, double shortShare, double crowded)` | A cell's adults with a door or without: no home, then short of money, then crowded, then no reason. |
| 114 | 4 | `private static double shortOfMoney(Household c)` | How far one of a cell's households is from a basket it can afford, 0-1: last month's plan against subsistence. |
| 129 | 29 | `void steal(Game game, double[] offenderWeight)` | THE THEFTS: taken half from the households by what they have saved and half from the businesses by what is in their tills, and handed to the offenders' households by how much of the crime is theirs. |
| 160 | 9 | `public double unhousedShareOfCity(Game game)` | The share of the city with no home: the unhoused, and the orphans. |

