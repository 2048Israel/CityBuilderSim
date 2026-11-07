# Fittings.java - 124 lines · 13 methods · 0 constants · model

`ham/citybuildersim/Fittings.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

> The fittings on site: pieces of equipment ordered for a standing building and not yet worked, first ordered first worked.
> 
> WHY (R1b, 2026-11-05; docs/studies/research-design.md 2.2 step 5; Jerus: "equipment requires investment"). Before R1b a piece could be judged
> (EquipmentFit) and never bought: the count of a stack's buildings still without it (BuildingsStacks.getUnfitted()) only ever went up or stayed.
> A fitting is the builders' work on a building that is standing, so it is worked by their crews like the repairs are, off the top of the
> month's output (Game.getBuildingOutput(): the repairs first, then the fittings, then the sites), and the building counts as fitted only when
> the work is done: this is the list of what is waiting on those crews. One job is one building and one piece, with the points of work still
> owed on it and who ordered it (a sector's key, or "City" for the player's own Fit), so that a sector fits one at a time and a save, which
> writes the jobs, resumes them.
> 
> Money is not kept here. What a fitting costs is paid at the order (Game.fit()) and the builders are paid then; this holds the work only.
> A city with nothing unfitted never adds a job, so every method below does nothing in it.

**Used by (4):** [DataSave](DataSave.md), [EquipmentFit](EquipmentFit.md), [FittingCheck](FittingCheck.md), [Game](Game.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 29 | `public int templateId` | The building template's id (BuildingsTemplate.getId()). |
| 31 | `public String pieceId` | The piece's id on that template (Equipment.getId()). |
| 33 | `public String payer` | Who ordered it: a sector's key, or "City" (Game.getGovernmentInvestor()'s name). |
| 35 | `public double pointsLeft` | The BUILDING_WORK points still owed on it. |
| 53 | `private final List<Job> jobs` | First ordered, first worked. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 21 | 104 | **type** `public final class Fittings` | The fittings on site: pieces of equipment ordered for a standing building and not yet worked, first ordered first worked. |
| 27 | 24 | **type** `public static final class Job` | One fitting on site, and the shape a save writes it in (DataSave.fittings; public fields for Gson, as BuildingManager.ContractRecord is). |
| 37 | 1 | `public Job()` _(in Fittings.Job)_ |  |
| 39 | 6 | `Job(int templateId, String pieceId, String payer, double pointsLeft)` _(in Fittings.Job)_ |  |
| 46 | 1 | `Job copy()` _(in Fittings.Job)_ |  |
| 49 | 1 | `public String toString()` _(in Fittings.Job)_ |  |
| 56 | 3 | `public void add(int templateId, String pieceId, String payer, double points)` | Puts one fitting on site: one building of the template, the piece, its payer and the points of work it owes (not below 0, and 0 for anything that is not a number). |
| 61 | 5 | `public int onSite(int templateId, String pieceId)` | How many fittings of this piece are on site for this template (the buildings being fitted, not yet fitted). |
| 68 | 4 | `public boolean hasOnSite(String payer)` | Whether this payer has a fitting on site: a sector fits one at a time. |
| 74 | 1 | `public boolean isEmpty()` | Whether nothing is on site. |
| 77 | 5 | `public double pointsOwed()` | The points of work still owed on every fitting on site. |
| 89 | 15 | `public List<Job> work(double points)` | Works `points` off the jobs in the order placed: the first job's remainder first, then the next, and so on until the points are gone. |
| 106 | 1 | `public void clear()` | Forgets every job: Game.buildWorld() calls it, as it clears the forms, so a city founded or loaded into a Game that played another starts with none on site. |
| 109 | 5 | `public List<Job> view()` | The jobs on site, in the order placed, as copies: for the save and for the harnesses. |
| 116 | 8 | `public void restore(List<Job> saved)` | Puts back the jobs a save carries (null or empty: none), replacing any on site; a job with no piece, no payer or points that are not a number is skipped. |

