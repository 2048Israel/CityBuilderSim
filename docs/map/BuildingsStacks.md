# BuildingsStacks.java - 973 lines · 88 methods · 0 constants · model

`ham/citybuildersim/BuildingsStacks.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> One building type in the city: how many stand, how many are on site, and
> the progress, material and contract its sites carry.

**Uses:** [Fitout](Fitout.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (4), [BuildingManager](BuildingManager.md) (4), [JobType](JobType.md) (1)

**Used by (78):** [AdviceValueOracle](AdviceValueOracle.md), [BuildAdvice](BuildAdvice.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingManager](BuildingManager.md), [BuildingStackCheck](BuildingStackCheck.md), [BusinessInvestment](BusinessInvestment.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [ConstructionControl](ConstructionControl.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [DataSave](DataSave.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DiscoveryCheck](DiscoveryCheck.md), [DriverlessTruckCheck](DriverlessTruckCheck.md), [ElectrolyserCheck](ElectrolyserCheck.md), [EquipmentCheck](EquipmentCheck.md), [EquipmentFit](EquipmentFit.md), [EquipmentForms](EquipmentForms.md), [EssentialWorksCheck](EssentialWorksCheck.md), [FieldOperatorsCheck](FieldOperatorsCheck.md), [FitoutCheck](FitoutCheck.md), [FittingCheck](FittingCheck.md), [ForeignChoiceCheck](ForeignChoiceCheck.md), [ForeignCrewsCheck](ForeignCrewsCheck.md), [Game](Game.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HistoricalRecipeCheck](HistoricalRecipeCheck.md), [HousingInventory](HousingInventory.md), [HungerCheck](HungerCheck.md), [HydrogenSteelCheck](HydrogenSteelCheck.md), [InvestCheck](InvestCheck.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LandmassCheck](LandmassCheck.md), [LicenceFees](LicenceFees.md), [LongPlaytest](LongPlaytest.md), [MedicineCheck](MedicineCheck.md), [ModernEngineeringCheck](ModernEngineeringCheck.md), [ModernRowsCheck](ModernRowsCheck.md), [NewUnitCheck](NewUnitCheck.md), [Oil](Oil.md), [OilView](OilView.md), [OutsideCheck](OutsideCheck.md), [Ports](Ports.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [PowerPlanCheck](PowerPlanCheck.md), [PowerPlantsCheck](PowerPlantsCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [RealEstate](RealEstate.md), [RecipeChanges](RecipeChanges.md), [RecipeCheck](RecipeCheck.md), [Recipes](Recipes.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [Refining](Refining.md), [ResearchView](ResearchView.md), [ResearchViewCheck](ResearchViewCheck.md), [RoadCheck](RoadCheck.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [SlopeCheck](SlopeCheck.md), [SubstationCheck](SubstationCheck.md), [SulphideLeachCheck](SulphideLeachCheck.md), [TownGridCheck](TownGridCheck.md), [UserInterface](UserInterface.md), [Village1900Check](Village1900Check.md), [WaterPowerCheck](WaterPowerCheck.md), [WellCheck](WellCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md), [WorksValue](WorksValue.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 11 | `private BuildingsTemplate template` |  |
| 12 | `private long quantity` |  |
| 13 | `private long underConstruction` |  |
| 16 | `private long lastFinished` | Finished in the most recent advanceConstruction() call. |
| 19 | `private double lastApplied` | Points the most recent advanceConstruction() call put into buildings: never more than the stack owed (0.7.17). |
| 22 | `private double lastAbroadApplied, lastAbroadMaterials` | ...of which the foreign crews' part (FC2b), and the units that part drew. |
| 23 | `private double constructionProgress` |  |
| 43 | `private double materialsOwed` | Units of material the sites still have to take. |
| 46 | `private double materialsDue` | What this month's work drew on. |
| 60 | `private double contractValue` | What the sites are still owed FOR, in money: the builders' contract for the work on site, recognised as the work is done. |
| 63 | `private double revenueDue` | ...and what this month's work earned of it. |
| 86 | `public final String payer` | "City", or a sector's key: whoever placed the order and pays its escalation. |
| 88 | `public boolean creditable` | Whether the payer gets any of the sales tax on it back - a business building for its own taxable trade, or a landlord's rebate. |
| 90 | `double recovered` | ...and what share of it: one for a credit or a purpose-built rental's rebate, less for a smaller rebate. |
| 91 | `double value, units, allowance` |  |
| 93 | `double points` | The points of work its payer's orders put on site still owed (FC2b), earned at the same fraction as its value: what a payer's share of a shared site is read by. |
| 109 | `public final String payer` |  |
| 110 | `public final boolean creditable` |  |
| 112 | `public final double recovered` | The share of the tax on it the payer gets back. |
| 113 | `public final double revenue, units, allowance` |  |
| 115 | `public final boolean abroad` | Whether it is the payer's contract handed to the foreign crews (FC2b), earned by their part of the site's work and not the city's builders'. |
| 124 | `private final java.util.LinkedHashMap<String, Contract> contracts` |  |
| 132 | `private final java.util.LinkedHashMap<String, Contract> abroadContracts` | ...and the contracts handed to the foreign crews (FC2b; ConstructionControl, G. |
| 133 | `private final java.util.List<Due> dues` |  |
| 371 | `private double lastHomeEarned` | What this month's work earned of the home contracts, the part handed abroad (FC2b) apart; nothing for a site that had nothing on it. |
| 376 | `private double lastHomeAllowance` | ...and of it the material allowance the home contracts priced in, for the material the month's work drew (R1g). |
| 423 | `private final java.util.TreeMap<String, Long> unfitted` | ===== THE UNFITTED (R1, 2026-11-05; Equipment, EquipmentForms, docs/queue/R1-notes.md call 4) ===== |
| 449 | `private java.util.function.Consumer<BuildingsStacks> onScrap` |  |
| 484 | `public long buildings` | How many of the order's buildings are still on site. |
| 486 | `public java.util.List<String> without` | The pieces they come without, by id; empty for every piece. |
| 488 | `private transient Fitout fit` | The fitout these read as on this stack's template; not saved (Gson skips a transient field), read again from the ids. |
| 514 | `private final java.util.ArrayList<Lot> lots` |  |
| 517 | `private long lastFinishedLoose` | Finished in the most recent advanceConstruction() call with no lot: loose, theirs to be decided at the finish. |
| 519 | `private final java.util.TreeMap<String, Long> lastFinishedWithout` | ...and of those that finished in lots, how many came without each piece, by id. |
| 597 | `private double abroadClaim` | This site's claim on the foreign crews' book (REV-FC2, the second review; BuildingManager.noteAbroad(), abroadOwedOf()): the work its orders handed him, in a unit only the claims of the sites with points owed share - ... |
| 649 | `private java.util.function.LongPredicate mayRemove` | for immediate add |
| 650 | `private java.util.function.Consumer<BuildingsStacks> physicalChanged` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 9 | 965 | **type** `public class BuildingsStacks` | One building type in the city: how many stand, how many are on site, and the progress, material and contract its sites carry. |
| 84 | 22 | **type** `public static final class Contract` | WHO PAID FOR THE CONTRACT, AND FOR THE MATERIAL IN IT (0.7.19). |
| 94 | 1 | `Contract(String payer, boolean creditable)` _(in BuildingsStacks.Contract)_ |  |
| 95 | 5 | `Contract(String payer, double recovered)` _(in BuildingsStacks.Contract)_ |  |
| 100 | 1 | `public double getValue()` _(in BuildingsStacks.Contract)_ |  |
| 101 | 1 | `public double getUnits()` _(in BuildingsStacks.Contract)_ |  |
| 102 | 1 | `public double getAllowance()` _(in BuildingsStacks.Contract)_ |  |
| 103 | 1 | `public double getRecovered()` _(in BuildingsStacks.Contract)_ |  |
| 104 | 1 | `public double getPoints()` _(in BuildingsStacks.Contract)_ |  |
| 108 | 15 | **type** `public static final class Due` | ...and one payer's share of this month's work: the contract it earned, the units drawn for it and the allowance they took. |
| 116 | 1 | `Due(Contract c, double revenue, double units, double allowance)` _(in BuildingsStacks.Due)_ |  |
| 117 | 5 | `Due(Contract c, double revenue, double units, double allowance, boolean abroad)` _(in BuildingsStacks.Due)_ |  |
| 143 | 5 | `public BuildingsStacks(BuildingsTemplate template, int initialQuantity)` | The parameter used to be accepted and then thrown away - the body assigned quantity = 0 regardless. |
| 149 | 3 | `public void startConstruction(long n)` |  |
| 158 | 6 | `public void startConstruction(long n, Fitout fit)` | ...and the order's fitout with it (R1e; Fitout): an order a city founded the 1900 way chose goes on site as a lot of its own, behind the lots already there, and keeps its set and its points until it finishes. |
| 172 | 4 | `void addPreparation(double points)` | A foothill site's preparation, owed as work (ET2; SitePreparation): the stack owes `points` more before its first building finishes, which is progress below nothing - advanceConstruction() reads it as owedPoints = on ... |
| 178 | 3 | `public void bookContract(double amount)` | The builders' price for an order just placed on this stack. |
| 187 | 14 | `public void bookContract(String payer, double recovered, double amount, double units, double allowance)` | ...and who placed it (0.7.19): the price, the material units beyond the yard the sites will draw for it, and the allowance priced into it for them. |
| 203 | 3 | `public void restoreContract(String payer, double recovered, double value, double units, double allowance)` | Restores one payer's contract from a save, or sets an older save's up (Game, OLD CONTRACTS). |
| 208 | 10 | `public void restoreContract(String payer, double recovered, double value, double units, double allowance, double points, boolea...` | ...with the points it still owes (FC2b), into the contracts handed abroad when {@code abroad}; an older save's carry none. |
| 220 | 1 | `public java.util.Collection<Contract> getContracts()` | The payers' contracts on this stack, in the order they were placed. |
| 227 | 5 | `void addPoints(String payer, double points)` | Adds the points an order of this payer's put on site (FC2b) to its home contract: what a payer's share of a shared site is read by (homePointsShare()). |
| 234 | 3 | `public java.util.Collection<Contract> getAbroadContracts()` | The contracts handed to the foreign crews (FC2b), in the order they went: an unmodifiable view, empty for a site none is abroad on. |
| 239 | 1 | `public boolean hasAbroad()` | Whether any of this site's work is handed to the foreign crews (FC2b). |
| 247 | 14 | `void handAbroad(String payer)` | Hands a payer's home contract to the foreign crews (FC2b): it moves, whole, from the contracts the city's crews' rule earns to the ones the foreign crews' part does, merged into the payer's contract there when it has ... |
| 267 | 6 | `void bookAbroad(String payer, double amount)` | What the payer paid the foreign crews over its home contract (FC2b): booked to its abroad contract and to the stack's own book (contractValue), earned as their part of the work is done. |
| 279 | 10 | `public double homePointsShare(String payer)` | The payer's share of the work still owed at home (FC2b): its points over the home contracts' points, or - when none carries any (an older save) - its value over their values, or - when none carries a value - an equal ... |
| 297 | 6 | `public boolean isCitysOwn()` | Whether what is on site is the city's alone (0.7.22): buildings on site, and every order on them the city's - the sites the player may reorder, rush and stop (ConstructionControl). |
| 313 | 14 | `double[] stopForShell()` | Takes everything on site off the stack, for the shell a cancelled order leaves (0.7.22; ConstructionControl, C. |
| 333 | 3 | `void resumeShell(long buildings, double progress, double owedUnits)` | ...and a shell put back on site (0.7.22): its buildings, the work in them and the material they still owe, beside whatever is on site already. |
| 338 | 9 | `void resumeShell(long buildings, double progress, double owedUnits, java.util.List<Lot> stopped)` | ...with the lots it was stopped with (R1e): back on site behind what is there, each with its set; a shell with none comes back loose. |
| 349 | 1 | `public java.util.List<Due> getDues()` | This month's work, payer by payer - per the last advanceConstruction() call. |
| 357 | 12 | `private void takeDues(java.util.Map<String, Contract> map, double fraction, boolean drawn, boolean abroad)` | Each payer's share of the month's work, of the contracts in {@code map} (the home ones, or the ones handed abroad, FC2b): the same fraction of its contract, its units, its allowance and its points as the work was of w... |
| 373 | 1 | `public double getLastHomeEarned()` |  |
| 378 | 1 | `public double getLastHomeAllowance()` |  |
| 381 | 5 | `public double getHomeContractValue()` | What the home contracts still hold: what their payers have paid for work not yet earned, the ones handed abroad (FC2b) apart. |
| 395 | 18 | `public double earnAhead(double amount)` | Earns {@code amount} of the home contracts now, ahead of the work that would have earned it, each payer's contract by its share of what they hold (R1g; BuildingManager, THE CONTRACTOR'S POINTS EARNED AT HIS PRICE): th... |
| 426 | 4 | `public long getUnfitted(String id)` | How many of this stack's buildings are still without the piece of equipment of this id: 0 for a piece none lacks. |
| 432 | 6 | `public void setUnfitted(String id, long n)` | Sets how many of this stack's buildings are without a piece: clamped to 0..quantity, and 0 takes the piece off the count. |
| 440 | 3 | `public java.util.Map<String, Long> getUnfittedView()` | Every piece some of this stack's buildings lack, with how many, in the pieces' id order; unmodifiable, and empty for a stack that is fully fitted. |
| 445 | 3 | `public boolean hasUnfitted()` | Whether any of this stack's buildings lacks any piece. |
| 452 | 1 | `void setOnScrap(java.util.function.Consumer<BuildingsStacks> listener)` | The city's fitting queue hears every removal, retirement and a harness's immediate removal included, after the counts are lowered (R1d). |
| 455 | 12 | `private void lowerUnfitted(long before)` | After the quantity fell from `before`: the buildings scrapped were the unfitted ones first (the older plants), and no count stays above what stands. |
| 482 | 31 | **type** `public static final class Lot` | One order's buildings on site and the pieces they come without (R1e): plain fields, so the save and a shell carry it. |
| 490 | 1 | `public Lot()` _(in BuildingsStacks.Lot)_ |  |
| 492 | 5 | `Lot(long buildings, Fitout fit)` _(in BuildingsStacks.Lot)_ |  |
| 499 | 4 | `public Fitout fitOf(BuildingsTemplate t)` _(in BuildingsStacks.Lot)_ | Its fitout on `t`: the ids it was saved with, those the template no longer carries dropped (Fitout.without()). |
| 505 | 7 | `public Lot copy()` _(in BuildingsStacks.Lot)_ | A copy that shares nothing with this one: a shell's, the save's. |
| 522 | 5 | `private void addLot(long n, Fitout fit)` | A lot of n behind the others; one with the same set as the last joins it, which finishes the same buildings in the same order. |
| 529 | 10 | `private void trimLots()` | No lot holds more buildings than are on site: the newest give way first (a site cut by hand, a save's count). |
| 540 | 5 | `private long lotted()` |  |
| 547 | 3 | `public long getLooseOnSite()` | The buildings on site with no lot, the front of the site: every building on site where there is no lot. |
| 552 | 5 | `public java.util.List<Lot> getLots()` | The lots on site, front first: copies, for the save and a shell. |
| 559 | 5 | `public void restoreLots(java.util.List<Lot> saved)` | Puts a save's lots back, front first, after the site's count (BuildingManager.restoreLots()): none, or empty, restores none. |
| 569 | 5 | `public double pointsOnSite()` | The points every building on site takes, before any progress: the loose at the template's points, each lot at its fitout's. |
| 576 | 13 | `public double pointsOfFirst(long k)` | ...and the first k of them, front first: what the site owes before its k-th building finishes, progress aside. |
| 599 | 1 | `public double getAbroadClaim()` |  |
| 600 | 1 | `public void setAbroadClaim(double claim)` |  |
| 609 | 1 | `private void claimAfresh()` | New work on a site that owed nothing starts with no claim on his book (REV-FC2, the third review): what its orders before claimed was the work of a site that has finished, or stopped, and a load reads none for it (Bui... |
| 612 | 3 | `public double owedPoints()` | Points still owed on site: every building's points less the progress made (a foothill's preparation owed reads as more). |
| 617 | 4 | `private double nextPoints()` | The points the building at the front of the site takes: a loose one's the template's, else the first lot's. |
| 623 | 14 | `private void finishFront(long k)` | Takes k finished buildings off the front, the loose first, and counts what they come without. |
| 639 | 3 | `public long getLastFinishedLoose()` | Of the buildings the most recent advanceConstruction() call finished, those with no lot: their pieces are decided at the finish. |
| 644 | 3 | `public java.util.Map<String, Long> getLastFinishedWithout()` | ...and of those it finished in lots, how many came without each piece, by id: unmodifiable, empty when every one came with all. |
| 653 | 4 | `void bindHousing(java.util.function.LongPredicate guard, java.util.function.Consumer<BuildingsStacks> changed)` | Inventory hooks protect conveyed titles; stacks remain physical authority. |
| 658 | 8 | `public void addQuantity(long n)` |  |
| 667 | 3 | `public void advanceConstruction(double constructionOutput)` |  |
| 681 | 149 | `public void advanceConstruction(double homeOutput, double abroadOutput, double abroadOwed, double abroadMaterials)` | The month's work on a site with part of it handed to the foreign crews (FC2b): {@code homeOutput} is what the city's crews' rule gives it, {@code abroadOutput} what the foreign crews build on it, {@code abroadOwed} th... |
| 840 | 3 | `public long getLastFinished()` | How many finished in the most recent advanceConstruction() call. |
| 845 | 3 | `public double getLastApplied()` | Points the most recent advanceConstruction() call put into buildings. |
| 850 | 3 | `public double getLastAbroadApplied()` | ...of which the foreign crews' part on a site handed them (FC2b), per the last advanceConstruction() call. |
| 855 | 3 | `public double getLastAbroadMaterials()` | The units the abroad part drew (FC2b), per the last advanceConstruction() call: part of getMaterialsDue(). |
| 865 | 7 | `public double clearBankedProgress()` | Drops progress past what the stack owes - all of it with nothing on site - and returns how many points that was. |
| 874 | 3 | `public long getTotalJobs(JobType type)` | getters |
| 878 | 3 | `public long getQuantity()` |  |
| 883 | 9 | `public void removeQuantity(long amount)` | Scraps finished buildings. |
| 893 | 3 | `public long getUnderConstruction()` |  |
| 897 | 3 | `public String getName()` |  |
| 901 | 3 | `public double getConstructionProgress()` |  |
| 905 | 6 | `public void increaseQuantity(long amount)` |  |
| 912 | 3 | `public BuildingsTemplate getBuilding()` |  |
| 916 | 6 | `public boolean getIfUnderConstruction()` |  |
| 924 | 3 | `public double getMaterialsOwed()` | Units the sites still have to draw. |
| 929 | 3 | `public double getMaterialsDue()` | Units this month's work drew on, per the last advanceConstruction() call. |
| 934 | 3 | `public double getContractValue()` | The builders' contract still on site. |
| 939 | 3 | `public double getRevenueDue()` | What this month's work earned of it, per the last advanceConstruction() call. |
| 943 | 3 | `public void setContractValue(double amount)` |  |
| 948 | 5 | `public void redenominate(double scale)` | A reform: the contract is money. |
| 955 | 3 | `public void setConstructionProgress(double progress)` | setters |
| 959 | 4 | `public void setUnderConstruction(long quantity)` |  |
| 964 | 3 | `public void setMaterialsOwed(double units)` |  |
| 969 | 3 | `public void deliverMaterials(double units)` | Material that reached the sites without a purchase - the city's yard, on the order day. |

