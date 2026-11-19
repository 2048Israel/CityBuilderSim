# BuildingsStacks.java - 724 lines · 68 methods · 0 constants · model

`ham/citybuildersim/BuildingsStacks.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> One building type in the city: how many stand, how many are on site, and
> the progress, material and contract its sites carry.

**Uses:** [Fitout](Fitout.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (4), [JobType](JobType.md) (1)

**Used by (65):** [AdviceValueOracle](AdviceValueOracle.md), [BuildAdvice](BuildAdvice.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingManager](BuildingManager.md), [BuildingStackCheck](BuildingStackCheck.md), [BusinessInvestment](BusinessInvestment.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [ConstructionControl](ConstructionControl.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [DataSave](DataSave.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DiscoveryCheck](DiscoveryCheck.md), [DriverlessTruckCheck](DriverlessTruckCheck.md), [ElectrolyserCheck](ElectrolyserCheck.md), [EquipmentCheck](EquipmentCheck.md), [EquipmentFit](EquipmentFit.md), [EquipmentForms](EquipmentForms.md), [EssentialWorksCheck](EssentialWorksCheck.md), [FieldOperatorsCheck](FieldOperatorsCheck.md), [FitoutCheck](FitoutCheck.md), [FittingCheck](FittingCheck.md), [Game](Game.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HistoricalRecipeCheck](HistoricalRecipeCheck.md), [HydrogenSteelCheck](HydrogenSteelCheck.md), [InvestCheck](InvestCheck.md), [KeptCheck](KeptCheck.md), [LandmassCheck](LandmassCheck.md), [LicenceFees](LicenceFees.md), [LongPlaytest](LongPlaytest.md), [MedicineCheck](MedicineCheck.md), [NewUnitCheck](NewUnitCheck.md), [Oil](Oil.md), [OilView](OilView.md), [OutsideCheck](OutsideCheck.md), [Ports](Ports.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [RealEstate](RealEstate.md), [RecipeChanges](RecipeChanges.md), [RecipeCheck](RecipeCheck.md), [Recipes](Recipes.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [Refining](Refining.md), [ResearchView](ResearchView.md), [ResearchViewCheck](ResearchViewCheck.md), [RoadCheck](RoadCheck.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [SlopeCheck](SlopeCheck.md), [SulphideLeachCheck](SulphideLeachCheck.md), [UserInterface](UserInterface.md), [WellCheck](WellCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md), [WorksValue](WorksValue.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 11 | `private BuildingsTemplate template` |  |
| 12 | `private long quantity` |  |
| 13 | `private long underConstruction` |  |
| 16 | `private long lastFinished` | Finished in the most recent advanceConstruction() call. |
| 19 | `private double lastApplied` | Points the most recent advanceConstruction() call put into buildings: never more than the stack owed (0.7.17). |
| 20 | `private double constructionProgress` |  |
| 40 | `private double materialsOwed` | Units of material the sites still have to take. |
| 43 | `private double materialsDue` | What this month's work drew on. |
| 57 | `private double contractValue` | What the sites are still owed FOR, in money: the builders' contract for the work on site, recognised as the work is done. |
| 60 | `private double revenueDue` | ...and what this month's work earned of it. |
| 83 | `public final String payer` | "City", or a sector's key: whoever placed the order and pays its escalation. |
| 85 | `public boolean creditable` | Whether the payer gets any of the sales tax on it back - a business building for its own taxable trade, or a landlord's rebate. |
| 87 | `double recovered` | ...and what share of it: one for a credit or a purpose-built rental's rebate, less for a smaller rebate. |
| 88 | `double value, units, allowance` |  |
| 103 | `public final String payer` |  |
| 104 | `public final boolean creditable` |  |
| 106 | `public final double recovered` | The share of the tax on it the payer gets back. |
| 107 | `public final double revenue, units, allowance` |  |
| 114 | `private final java.util.LinkedHashMap<String, Contract> contracts` |  |
| 115 | `private final java.util.List<Due> dues` |  |
| 278 | `private final java.util.TreeMap<String, Long> unfitted` | ===== THE UNFITTED (R1, 2026-11-05; Equipment, EquipmentForms, docs/queue/R1-notes.md call 4) ===== |
| 304 | `private java.util.function.Consumer<BuildingsStacks> onScrap` |  |
| 339 | `public long buildings` | How many of the order's buildings are still on site. |
| 341 | `public java.util.List<String> without` | The pieces they come without, by id; empty for every piece. |
| 343 | `private transient Fitout fit` | The fitout these read as on this stack's template; not saved (Gson skips a transient field), read again from the ids. |
| 369 | `private final java.util.ArrayList<Lot> lots` |  |
| 372 | `private long lastFinishedLoose` | Finished in the most recent advanceConstruction() call with no lot: loose, theirs to be decided at the finish. |
| 374 | `private final java.util.TreeMap<String, Long> lastFinishedWithout` | ...and of those that finished in lots, how many came without each piece, by id. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 9 | 716 | **type** `public class BuildingsStacks` | One building type in the city: how many stand, how many are on site, and the progress, material and contract its sites carry. |
| 81 | 19 | **type** `public static final class Contract` | WHO PAID FOR THE CONTRACT, AND FOR THE MATERIAL IN IT (0.7.19). |
| 89 | 1 | `Contract(String payer, boolean creditable)` _(in BuildingsStacks.Contract)_ |  |
| 90 | 5 | `Contract(String payer, double recovered)` _(in BuildingsStacks.Contract)_ |  |
| 95 | 1 | `public double getValue()` _(in BuildingsStacks.Contract)_ |  |
| 96 | 1 | `public double getUnits()` _(in BuildingsStacks.Contract)_ |  |
| 97 | 1 | `public double getAllowance()` _(in BuildingsStacks.Contract)_ |  |
| 98 | 1 | `public double getRecovered()` _(in BuildingsStacks.Contract)_ |  |
| 102 | 11 | **type** `public static final class Due` | ...and one payer's share of this month's work: the contract it earned, the units drawn for it and the allowance they took. |
| 108 | 4 | `Due(Contract c, double revenue, double units, double allowance)` _(in BuildingsStacks.Due)_ |  |
| 125 | 5 | `public BuildingsStacks(BuildingsTemplate template, int initialQuantity)` | The parameter used to be accepted and then thrown away - the body assigned quantity = 0 regardless. |
| 131 | 3 | `public void startConstruction(long n)` |  |
| 140 | 5 | `public void startConstruction(long n, Fitout fit)` | ...and the order's fitout with it (R1e; Fitout): an order a city founded the 1900 way chose goes on site as a lot of its own, behind the lots already there, and keeps its set and its points until it finishes. |
| 153 | 3 | `void addPreparation(double points)` | A foothill site's preparation, owed as work (ET2; SitePreparation): the stack owes `points` more before its first building finishes, which is progress below nothing - advanceConstruction() reads it as owedPoints = on ... |
| 158 | 3 | `public void bookContract(double amount)` | The builders' price for an order just placed on this stack. |
| 167 | 14 | `public void bookContract(String payer, double recovered, double amount, double units, double allowance)` | ...and who placed it (0.7.19): the price, the material units beyond the yard the sites will draw for it, and the allowance priced into it for them. |
| 183 | 8 | `public void restoreContract(String payer, double recovered, double value, double units, double allowance)` | Restores one payer's contract from a save, or sets an older save's up (Game, OLD CONTRACTS). |
| 193 | 1 | `public java.util.Collection<Contract> getContracts()` | The payers' contracts on this stack, in the order they were placed. |
| 202 | 5 | `public boolean isCitysOwn()` | Whether what is on site is the city's alone (0.7.22): buildings on site, and every order on them the city's - the sites the player may reorder, rush and stop (ConstructionControl). |
| 217 | 14 | `double[] stopForShell()` | Takes everything on site off the stack, for the shell a cancelled order leaves (0.7.22; ConstructionControl, C. |
| 237 | 3 | `void resumeShell(long buildings, double progress, double owedUnits)` | ...and a shell put back on site (0.7.22): its buildings, the work in them and the material they still owe, beside whatever is on site already. |
| 242 | 8 | `void resumeShell(long buildings, double progress, double owedUnits, java.util.List<Lot> stopped)` | ...with the lots it was stopped with (R1e): back on site behind what is there, each with its set; a shell with none comes back loose. |
| 252 | 1 | `public java.util.List<Due> getDues()` | This month's work, payer by payer - per the last advanceConstruction() call. |
| 255 | 13 | `private void takeDues(double fraction, boolean drawn)` | Each payer's share of the month's work: the same fraction of its contract, its units and its allowance as the stack's work was of what it owed; drawn=false when the work drew no material (nothing on site). |
| 281 | 4 | `public long getUnfitted(String id)` | How many of this stack's buildings are still without the piece of equipment of this id: 0 for a piece none lacks. |
| 287 | 6 | `public void setUnfitted(String id, long n)` | Sets how many of this stack's buildings are without a piece: clamped to 0..quantity, and 0 takes the piece off the count. |
| 295 | 3 | `public java.util.Map<String, Long> getUnfittedView()` | Every piece some of this stack's buildings lack, with how many, in the pieces' id order; unmodifiable, and empty for a stack that is fully fitted. |
| 300 | 3 | `public boolean hasUnfitted()` | Whether any of this stack's buildings lacks any piece. |
| 307 | 1 | `void setOnScrap(java.util.function.Consumer<BuildingsStacks> listener)` | The city's fitting queue hears every removal, retirement and a harness's immediate removal included, after the counts are lowered (R1d). |
| 310 | 12 | `private void lowerUnfitted(long before)` | After the quantity fell from `before`: the buildings scrapped were the unfitted ones first (the older plants), and no count stays above what stands. |
| 337 | 31 | **type** `public static final class Lot` | One order's buildings on site and the pieces they come without (R1e): plain fields, so the save and a shell carry it. |
| 345 | 1 | `public Lot()` _(in BuildingsStacks.Lot)_ |  |
| 347 | 5 | `Lot(long buildings, Fitout fit)` _(in BuildingsStacks.Lot)_ |  |
| 354 | 4 | `public Fitout fitOf(BuildingsTemplate t)` _(in BuildingsStacks.Lot)_ | Its fitout on `t`: the ids it was saved with, those the template no longer carries dropped (Fitout.without()). |
| 360 | 7 | `public Lot copy()` _(in BuildingsStacks.Lot)_ | A copy that shares nothing with this one: a shell's, the save's. |
| 377 | 5 | `private void addLot(long n, Fitout fit)` | A lot of n behind the others; one with the same set as the last joins it, which finishes the same buildings in the same order. |
| 384 | 10 | `private void trimLots()` | No lot holds more buildings than are on site: the newest give way first (a site cut by hand, a save's count). |
| 395 | 5 | `private long lotted()` |  |
| 402 | 3 | `public long getLooseOnSite()` | The buildings on site with no lot, the front of the site: every building on site where there is no lot. |
| 407 | 5 | `public java.util.List<Lot> getLots()` | The lots on site, front first: copies, for the save and a shell. |
| 414 | 5 | `public void restoreLots(java.util.List<Lot> saved)` | Puts a save's lots back, front first, after the site's count (BuildingManager.restoreLots()): none, or empty, restores none. |
| 424 | 5 | `public double pointsOnSite()` | The points every building on site takes, before any progress: the loose at the template's points, each lot at its fitout's. |
| 431 | 13 | `public double pointsOfFirst(long k)` | ...and the first k of them, front first: what the site owes before its k-th building finishes, progress aside. |
| 446 | 3 | `public double owedPoints()` | Points still owed on site: every building's points less the progress made (a foothill's preparation owed reads as more). |
| 451 | 4 | `private double nextPoints()` | The points the building at the front of the site takes: a loose one's the template's, else the first lot's. |
| 457 | 14 | `private void finishFront(long k)` | Takes k finished buildings off the front, the loose first, and counts what they come without. |
| 473 | 3 | `public long getLastFinishedLoose()` | Of the buildings the most recent advanceConstruction() call finished, those with no lot: their pieces are decided at the finish. |
| 478 | 3 | `public java.util.Map<String, Long> getLastFinishedWithout()` | ...and of those it finished in lots, how many came without each piece, by id: unmodifiable, empty when every one came with all. |
| 483 | 5 | `public void addQuantity(long n)` | for immediate add |
| 489 | 108 | `public void advanceConstruction(double constructionOutput)` |  |
| 607 | 3 | `public long getLastFinished()` | How many finished in the most recent advanceConstruction() call. |
| 612 | 3 | `public double getLastApplied()` | Points the most recent advanceConstruction() call put into buildings. |
| 622 | 7 | `public double clearBankedProgress()` | Drops progress past what the stack owes - all of it with nothing on site - and returns how many points that was. |
| 631 | 3 | `public long getTotalJobs(JobType type)` | getters |
| 635 | 3 | `public long getQuantity()` |  |
| 640 | 5 | `public void removeQuantity(long amount)` | Scraps finished buildings. |
| 646 | 3 | `public long getUnderConstruction()` |  |
| 650 | 3 | `public String getName()` |  |
| 654 | 3 | `public double getConstructionProgress()` |  |
| 658 | 5 | `public void increaseQuantity(long amount)` |  |
| 664 | 3 | `public BuildingsTemplate getBuilding()` |  |
| 668 | 6 | `public boolean getIfUnderConstruction()` |  |
| 676 | 3 | `public double getMaterialsOwed()` | Units the sites still have to draw. |
| 681 | 3 | `public double getMaterialsDue()` | Units this month's work drew on, per the last advanceConstruction() call. |
| 686 | 3 | `public double getContractValue()` | The builders' contract still on site. |
| 691 | 3 | `public double getRevenueDue()` | What this month's work earned of it, per the last advanceConstruction() call. |
| 695 | 3 | `public void setContractValue(double amount)` |  |
| 700 | 4 | `public void redenominate(double scale)` | A reform: the contract is money. |
| 706 | 3 | `public void setConstructionProgress(double progress)` | setters |
| 710 | 4 | `public void setUnderConstruction(long quantity)` |  |
| 715 | 3 | `public void setMaterialsOwed(double units)` |  |
| 720 | 3 | `public void deliverMaterials(double units)` | Material that reached the sites without a purchase - the city's yard, on the order day. |

