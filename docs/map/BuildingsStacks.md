# BuildingsStacks.java - 261 lines · 25 methods · 0 constants · model

`ham/citybuildersim/BuildingsStacks.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (3), [JobType](JobType.md) (1)

**Used by (3):** [BuildingManager](BuildingManager.md), [InvestCheck](InvestCheck.md), [UserInterface](UserInterface.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 9 | `private BuildingsTemplate template` |  |
| 10 | `private int quantity` |  |
| 11 | `private int underConstruction` |  |
| 14 | `private int lastFinished` | Finished in the most recent advanceConstruction() call. |
| 15 | `private double constructionProgress` |  |
| 35 | `private double materialsOwed` | Units of material the sites still have to take. |
| 38 | `private double materialsDue` | What this month's work drew on. |
| 52 | `private double contractValue` | What the sites are still owed FOR, in money: the builders' contract for the work on site, recognised as the work is done. |
| 55 | `private double revenueDue` | ...and what this month's work earned of it. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 7 | 255 | **type** `public class BuildingsStacks` |  |
| 65 | 5 | `public BuildingsStacks(BuildingsTemplate template, int initialQuantity)` | The parameter used to be accepted and then thrown away - the body assigned quantity = 0 regardless. |
| 71 | 4 | `public void startConstruction(int n)` |  |
| 77 | 3 | `public void bookContract(double amount)` | The builders' price for an order just placed on this stack. |
| 82 | 3 | `public void addQuantity(int n)` | for immediate add |
| 86 | 73 | `public void advanceConstruction(double constructionOutput)` |  |
| 169 | 3 | `public int getLastFinished()` | How many finished in the most recent advanceConstruction() call. |
| 174 | 3 | `public int getTotalJobs(JobType type)` | getters |
| 178 | 3 | `public int getQuantity()` |  |
| 183 | 3 | `public void removeQuantity(int amount)` | Scraps finished buildings. |
| 187 | 3 | `public int getUnderConstruction()` |  |
| 191 | 3 | `public String getName()` |  |
| 195 | 3 | `public double getConstructionProgress()` |  |
| 199 | 3 | `public void increaseQuantity(int amount)` |  |
| 203 | 3 | `public BuildingsTemplate getBuilding()` |  |
| 207 | 6 | `public boolean getIfUnderConstruction()` |  |
| 215 | 3 | `public double getMaterialsOwed()` | Units the sites still have to draw. |
| 220 | 3 | `public double getMaterialsDue()` | Units this month's work drew on, per the last advanceConstruction() call. |
| 225 | 3 | `public double getContractValue()` | The builders' contract still on site. |
| 230 | 3 | `public double getRevenueDue()` | What this month's work earned of it, per the last advanceConstruction() call. |
| 234 | 3 | `public void setContractValue(double amount)` |  |
| 239 | 3 | `public void redenominate(double scale)` | A reform: the contract is money. |
| 244 | 3 | `public void setConstructionProgress(double progress)` | setters |
| 248 | 3 | `public void setUnderConstruction(int quantity)` |  |
| 252 | 3 | `public void setMaterialsOwed(double units)` |  |
| 257 | 3 | `public void deliverMaterials(double units)` | Material that reached the sites without a purchase - the city's yard, on the order day. |

