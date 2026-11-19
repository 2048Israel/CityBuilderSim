# FieldOperatorsCheck.java - 474 lines · 22 methods · 2 constants · harnesses

`ham/citybuildersim/FieldOperatorsCheck.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> Driverless field work (R6d, FE8: Deere's goal of a fully autonomous corn and soybean season by 2030, projected 2033, window 2030-2036): the autonomy kit,
> the third piece of equipment on the Grain Farm and the Mixed Farm, and what the farms that carry it do to their posts: the operator of each tractor and
> combine struck, a remote watch asked for in his place.
> 
> WHY. The kit changes three things at once that a month's figures would hide if one were wrong: how many posts the farms offer (so the wage bill and the
> labour market, which the city's own count of posts has to agree with), what the kit is worth to a farm (so whether it is ever fitted, and the patent rent
> it owes), and which farms count as autonomous (so EquipmentForms' weights). And it must change none of them where the piece does not exist: a city
> founded today's way has every farm standing "fitted" only because every count there is, so its month is today's, to the bit. Each figure is held to the
> model's own constants (Agriculture.OPERATORS_A_MACHINE, WATCH_A_MACHINE and machinesOf()), never a number typed here, and each condition is caused from
> both sides: the month before and the month of FE8's date, a farm lacking the kit and a farm with it, a city founded the 1900 way and one founded today's.
> 
> What this has to prove:
> 
>   1. THE DIALS: the share still operated is 1 until set, clamped to [0, 1], a number that is not one reads 1; a machine is a tractor or a combine the
>      template carries (the Mixed Farm one, the Grain Farm two, a bakery none); the machines that work by themselves are the farms' machines times the
>      share that is not operated, and none on a farm that is on horses
>   2. THE POSTS: at a share of 1 the farms offer the buildings' posts, to the bit, and the city's count adds none; at 0 they offer the operators' struck
>      from NO_DIPLOMA and the watch added to DIPLOMA, rounded once on the sector's whole, and the city's count (BuildingManager's getTotalJobs()) falls by
>      the same; every other tier is untouched; a farm on horses strikes none
>   3. AT FE8: the kit is NOT_KNOWN the month before FE8 and every machine is operated; at FE8's month it is known, its gross gain is operatorGain() at the
>      city's own wages and machines, positive, and its verdict is the one EquipmentFit's rule gives with the patent rent off; a city whose research found
>      FE8 sooner knows it from the month it found it, and owes no rent on it
>   4. THE FARMS' COUNTS: the operated share is the machine-weighted share of the farms lacking the kit, and it stands after a month is played; the last
>      farm fitted takes it to 0, a count that returns to 1
>   5. A DEFAULT CITY: founded today's way, with farms, past FE8's month, the share is exactly 1 and the posts the buildings', even where a count of the
>      kit is put on a farm by hand
>   6. A PLAYED COUNT: the 1900 village played 24 months from FE8's month, every other piece in it put fitted by hand, the months its operated share was
>      below 1, which is more than none if the verdict of section 3 was PAYS
> 
> Every fixture is FittingCheck.village() with Grain Farms added (placed without construction, in a GameFiles.scratch() directory: no save slot is written),
> said over by unfitTheFounding() in a city founded the 1900 way, with its month set by reflection where a date is the premise (ResearchCheck.atMonth()).

**Uses:** [Agriculture](Agriculture.md) (26), [EquipmentFit](EquipmentFit.md) (25), [JobType](JobType.md) (21), [Game](Game.md) (19), [BuildingsStacks](BuildingsStacks.md) (10), [Equipment](Equipment.md) (9), [ResearchCheck](ResearchCheck.md) (9), [LicenceFees](LicenceFees.md) (5), [FittingCheck](FittingCheck.md) (4), [EquipmentCheck](EquipmentCheck.md) (4), [ResearchCatalog](ResearchCatalog.md) (4), [BuildingManager](BuildingManager.md) (4), [Forms1900Check](Forms1900Check.md) (2), [DiscoveryCheck](DiscoveryCheck.md) (2), [Founding](Founding.md) (2), [Recipes](Recipes.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [ResearchViewCheck](ResearchViewCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 170 | 1. the dials |
| 217 | 2. the posts |
| 281 | 3. at FE8 |
| 352 | 4. the farms' counts |
| 385 | 5. a default city |
| 409 | 6. a played count |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 50 | `FieldOperatorsCheck.KIT` | `"autonomy-kit"` |  |
| 52 | `FieldOperatorsCheck.GRAINS` | `20` | The Grain Farms stood beside the village's own one: enough that the watch (a tenth of a post a machine) rounds to posts. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 45 | `static int fails` |  |
| 46 | `static int passes` |  |
| 48 | `static PrintStream quiet` |  |
| 284 | `static EquipmentFit.Kind verdictAtFe8` | Whether the kit paid at FE8's month on the Grain Farm of section 3's village, for section 6's label. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 43 | 432 | **type** `public class FieldOperatorsCheck` | Driverless field work (R6d, FE8: Deere's goal of a fully autonomous corn and soybean season by 2030, projected 2033, window 2030-2036): the autonomy kit, the third piece of equipment on the Grain Farm and the Mixed Fa... |
| 54 | 4 | `static void check(String label, boolean ok)` |  |
| 59 | 4 | `static void check(String label, boolean ok, String found)` |  |
| 64 | 3 | `static void near(String label, double actual, double expected, double tolerance)` |  |
| 68 | 5 | `static void quietly(Runnable r)` |  |
| 75 | 7 | `static void ensureQuiet()` | The streams the borrowed fixtures are quiet through, set once and never over another harness's. |
| 83 | 3 | `static void play(Game g, int months)` |  |
| 88 | 3 | `static int fe8()` | FE8's month in history: the model's own, never a literal. |
| 92 | 3 | `static Agriculture farms(Game g)` |  |
| 96 | 3 | `static BuildingsStacks mixed(Game g)` |  |
| 100 | 3 | `static BuildingsStacks grain(Game g)` |  |
| 105 | 17 | `static Game village(String label, boolean founded1900)` | A village with Mixed Farms and Grain Farms (the fixture's two, one, and GRAINS more), founded the 1900 way (every building without every piece but the tractors and combines, which FE8's year has everywhere) or today's. |
| 124 | 8 | `static double machines(Game g)` | The farms' machines, written out from the stacks: the tractors and combines the templates carry, over the buildings standing. |
| 134 | 12 | `static double lackingShare(Game g)` | The share of the machines on farms lacking the kit, written out from the stacks' own counts. |
| 147 | 3 | `static long[] base(Game g)` |  |
| 152 | 4 | `static double[] wages(Game g)` | The wages the planner reads for the two tiers, as the city's prices carry them. |
| 157 | 12 | `public static void main(String[] args) throws Exception` |  |

### 1. the dials (lines 170-216)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 44 | `static void one()` |  |

### 2. the posts (lines 217-280)

| line | len | member | says |
|---:|---:|---|---|
| 219 | 61 | `static void two()` |  |

### 3. at FE8 (lines 281-351)

| line | len | member | says |
|---:|---:|---|---|
| 286 | 65 | `static void three(Game g) throws Exception` |  |

### 4. the farms' counts (lines 352-384)

| line | len | member | says |
|---:|---:|---|---|
| 354 | 30 | `static void four() throws Exception` |  |

### 5. a default city (lines 385-408)

| line | len | member | says |
|---:|---:|---|---|
| 387 | 21 | `static void five() throws Exception` |  |

### 6. a played count (lines 409-474)

| line | len | member | says |
|---:|---:|---|---|
| 411 | 63 | `static void six() throws Exception` |  |

