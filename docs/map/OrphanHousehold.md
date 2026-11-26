# OrphanHousehold.java - 42 lines · 10 methods · 0 constants · model

`ham/citybuildersim/OrphanHousehold.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> Children no family holds, by age band.
> 
> Found designing the unemployed households: FamilyModel's builder takes a
> half share of each shape in turn and only three shapes hold a baby and three
> a teen, so about one child in seven is in no household in every city - fed
> by nobody, charged nothing, in no cell. Jerus, shown it: "they're the
> orphans," and before that, of children with no family: "they dont even get
> ei, and its named the orphan section, yes they get sick and die for now."
> 
> So a ledger with nothing in it. No income, no earner, no credit; they go
> without, which the hunger measure reads, and their band's mortality is
> struck at zero care coverage - see Game.advanceDemographics(). They carry
> no grown-ups, so the census's pool weighs them by their headcount (OH1,
> HS1): what a child can hold - savings, shares, money abroad, the city's
> paper and bonds - follows them; no debt, student loan or car ever does
> (HouseholdBalance.poolWeight()).

**Uses:** [AgeBand](AgeBand.md) (3), [Household](Household.md) (1), [PayTier](PayTier.md) (1)

**Used by (3):** [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [OutsideCheck](OutsideCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 23 | `private final AgeBand band` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 21 | 22 | **type** `public class OrphanHousehold extends Household` | Children no family holds, by age band. |
| 25 | 4 | `public OrphanHousehold(AgeBand band)` |  |
| 30 | 1 | `public AgeBand band()` |  |
| 32 | 1 | `public PayTier tier()` |  |
| 33 | 1 | `public int row()` |  |
| 34 | 1 | `public boolean isRetired()` |  |
| 35 | 1 | `public int grownUps()` |  |
| 36 | 1 | `public int size()` |  |
| 38 | 1 | `public String label()` |  |
| 39 | 1 | `public String key()` |  |
| 41 | 1 | `public double creditRoom(double disposablePer)` |  |

