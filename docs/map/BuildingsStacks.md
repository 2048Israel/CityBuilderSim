# BuildingsStacks.java - 466 lines · 42 methods · 0 constants · model

`ham/citybuildersim/BuildingsStacks.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> One building type in the city: how many stand, how many are on site, and
> the progress, material and contract its sites carry.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (3), [JobType](JobType.md) (1)

**Used by (10):** [BuildScreen](BuildScreen.md), [BuildingManager](BuildingManager.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [Game](Game.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [RealEstate](RealEstate.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 11 | `private BuildingsTemplate template` |  |
| 12 | `private int quantity` |  |
| 13 | `private int underConstruction` |  |
| 16 | `private int lastFinished` | Finished in the most recent advanceConstruction() call. |
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

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 9 | 458 | **type** `public class BuildingsStacks` | One building type in the city: how many stand, how many are on site, and the progress, material and contract its sites carry. |
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
| 131 | 4 | `public void startConstruction(int n)` |  |
| 137 | 3 | `public void bookContract(double amount)` | The builders' price for an order just placed on this stack. |
| 146 | 14 | `public void bookContract(String payer, double recovered, double amount, double units, double allowance)` | ...and who placed it (0.7.19): the price, the material units beyond the yard the sites will draw for it, and the allowance priced into it for them. |
| 162 | 8 | `public void restoreContract(String payer, double recovered, double value, double units, double allowance)` | Restores one payer's contract from a save, or sets an older save's up (Game, OLD CONTRACTS). |
| 172 | 1 | `public java.util.Collection<Contract> getContracts()` | The payers' contracts on this stack, in the order they were placed. |
| 181 | 5 | `public boolean isCitysOwn()` | Whether what is on site is the city's alone (0.7.22): buildings on site, and every order on them the city's - the sites the player may reorder, rush and stop (ConstructionControl). |
| 196 | 12 | `double[] stopForShell()` | Takes everything on site off the stack, for the shell a cancelled order leaves (0.7.22; ConstructionControl, C. |
| 214 | 5 | `void resumeShell(int buildings, double progress, double owedUnits)` | ...and a shell put back on site (0.7.22): its buildings, the work in them and the material they still owe, beside whatever is on site already. |
| 221 | 1 | `public java.util.List<Due> getDues()` | This month's work, payer by payer - per the last advanceConstruction() call. |
| 224 | 13 | `private void takeDues(double fraction, boolean drawn)` | Each payer's share of the month's work: the same fraction of its contract, its units and its allowance as the stack's work was of what it owed; drawn=false when the work drew no material (nothing on site). |
| 239 | 3 | `public void addQuantity(int n)` | for immediate add |
| 243 | 101 | `public void advanceConstruction(double constructionOutput)` |  |
| 354 | 3 | `public int getLastFinished()` | How many finished in the most recent advanceConstruction() call. |
| 359 | 3 | `public double getLastApplied()` | Points the most recent advanceConstruction() call put into buildings. |
| 369 | 7 | `public double clearBankedProgress()` | Drops progress past what the stack owes - all of it with nothing on site - and returns how many points that was. |
| 378 | 3 | `public int getTotalJobs(JobType type)` | getters |
| 382 | 3 | `public int getQuantity()` |  |
| 387 | 3 | `public void removeQuantity(int amount)` | Scraps finished buildings. |
| 391 | 3 | `public int getUnderConstruction()` |  |
| 395 | 3 | `public String getName()` |  |
| 399 | 3 | `public double getConstructionProgress()` |  |
| 403 | 3 | `public void increaseQuantity(int amount)` |  |
| 407 | 3 | `public BuildingsTemplate getBuilding()` |  |
| 411 | 6 | `public boolean getIfUnderConstruction()` |  |
| 419 | 3 | `public double getMaterialsOwed()` | Units the sites still have to draw. |
| 424 | 3 | `public double getMaterialsDue()` | Units this month's work drew on, per the last advanceConstruction() call. |
| 429 | 3 | `public double getContractValue()` | The builders' contract still on site. |
| 434 | 3 | `public double getRevenueDue()` | What this month's work earned of it, per the last advanceConstruction() call. |
| 438 | 3 | `public void setContractValue(double amount)` |  |
| 443 | 4 | `public void redenominate(double scale)` | A reform: the contract is money. |
| 449 | 3 | `public void setConstructionProgress(double progress)` | setters |
| 453 | 3 | `public void setUnderConstruction(int quantity)` |  |
| 457 | 3 | `public void setMaterialsOwed(double units)` |  |
| 462 | 3 | `public void deliverMaterials(double units)` | Material that reached the sites without a purchase - the city's yard, on the order day. |

