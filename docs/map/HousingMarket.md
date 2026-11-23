# HousingMarket.java - 566 lines · 41 methods · 1 constants · model

`ham/citybuildersim/HousingMarket.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> One monthly residential quote and clearing, with saved class prices and actual leases.
> WHY (RES3): pooled rents attributed a cheap flat's demand to a vacant premium
> home. Quotes are asks; only occupied assignments create rental receipts.

**Uses:** [HousingChoice](HousingChoice.md) (113), [HousingInventory](HousingInventory.md) (28), [FamilyModel](FamilyModel.md) (27), [Game](Game.md) (10), [BuildingsTemplate](BuildingsTemplate.md) (8), [Household](Household.md) (6), [WorkingHousehold](WorkingHousehold.md) (6), [HousingForms](HousingForms.md) (6), [RealEstate](RealEstate.md) (6), [PayTier](PayTier.md) (5), [HousingRecipe](HousingRecipe.md) (5), [JobType](JobType.md) (4), [FamilyStructure](FamilyStructure.md) (4), [AgeBand](AgeBand.md) (3), [Sectors](Sectors.md) (1), [StudentHousehold](StudentHousehold.md) (1), [UnemployedHousehold](UnemployedHousehold.md) (1), [WageBand](WageBand.md) (1), [AdultStage](AdultStage.md) (1), [Retail](Retail.md) (1), [MoneyAudit](MoneyAudit.md) (1), [BuildingManager](BuildingManager.md) (1)

**Used by (4):** [DataSave](DataSave.md), [HouseholdBalance](HouseholdBalance.md), [HousingMarketCheck](HousingMarketCheck.md), [RealEstate](RealEstate.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 14 | `HousingMarket.SAVINGS_MONTHS` | `12` | Liquid savings can fund one year of recurring housing; no unfunded credit is assumed. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 19 | `public int recipe, capacity` |  |
| 20 | `public String dwelling` |  |
| 21 | `public double ask, capital, carry, demand, homes, occupied, target` |  |
| 22 | `public double lastTrade, tradeQuantity, tradeValue` |  |
| 23 | `public int stale` |  |
| 24 | `public boolean available` |  |
| 32 | `public int month` |  |
| 33 | `public List<ClassState> classes` |  |
| 34 | `public List<HousingChoice.Lease> leases` |  |
| 35 | `public List<HousingChoice.Demand> demand` |  |
| 36 | `public List<HousingChoice.Offer> pipeline` |  |
| 37 | `public List<Sharing> sharing` |  |
| 38 | `public double kept, suitable, crowded, unaffordable, noSuitable, unplaced` |  |
| 39 | `public double hosted` |  |
| 41 | `private final Map<HousingInventory.Key, ClassState> classes` |  |
| 42 | `private List<HousingChoice.Lease> leases` |  |
| 43 | `private int month` |  |
| 44 | `private HousingChoice.Result result` |  |
| 45 | `private List<HousingChoice.Demand> demand` |  |
| 46 | `private List<HousingChoice.Offer> offers` |  |
| 47 | `private List<HousingChoice.Offer> pipeline` |  |
| 48 | `private List<Sharing> sharing` |  |
| 49 | `private double hosted` |  |
| 51 | `private boolean arrivalProjection, arrivalsNeedFamily` | Derived pre-migration workspace, discarded by clearing and rebuilt after load. |
| 52 | `private double formingFamilies, formingAdults` |  |
| 439 | `private final Game city` |  |
| 440 | `private final List<HousingChoice.Demand> remaining` |  |
| 441 | `private final Map<HousingInventory.Key, Double> asks` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 12 | 555 | **type** `public final class HousingMarket` | One monthly residential quote and clearing, with saved class prices and actual leases. |
| 16 | 2 | **type** `public record Sharing(String source, String target, double groups, double sourceCount, double targetCount, ...` | A monthly transfer between existing cells, not a permanent preference or financial axis. |
| 18 | 12 | **type** `public static final class ClassState` |  |
| 25 | 1 | `HousingInventory.Key key()` _(in HousingMarket.ClassState)_ |  |
| 26 | 3 | `ClassState copy()` _(in HousingMarket.ClassState)_ |  |
| 31 | 10 | **type** `public static final class State` | A bounded save snapshot; a load restores it without quoting, clearing or repricing. |
| 54 | 1 | `public double hosted()` | Emergency guests whose host already funds the door; no extra rent or credit is invented. |
| 55 | 1 | `public double sharingGroups()` |  |
| 56 | 1 | `public List<HousingChoice.Demand> demands()` |  |
| 57 | 1 | `public boolean active()` |  |
| 58 | 1 | `public int clearedMonth()` |  |
| 59 | 1 | `public List<HousingChoice.Lease> leases()` |  |
| 60 | 1 | `public List<ClassState> classes()` |  |
| 61 | 1 | `public HousingChoice.Result result()` |  |
| 62 | 1 | `public double rentFor(String cell, double count)` |  |
| 63 | 1 | `public double receipts()` |  |
| 64 | 1 | `public double occupied()` |  |
| 66 | 23 | `public void prepareMigration(Game game)` | Reserve this press's domestic formation before admitting people who start work next press. |
| 90 | 35 | `public FamilyModel.Room roomLeft(Game game, FamilyModel.Room legacy)` | Read-only migration bound: actual remaining door/guest capacity, with the raw incoming household mix. |
| 125 | 1 | `public double price(HousingInventory.Key key)` |  |
| 127 | 2 | `public void setAsk(HousingInventory.Key key, double ask)` | A posted class ask can change without inventing a lease or a trade. |
| 129 | 5 | `public double meanPerCapacity(BuildingsTemplate t)` |  |
| 135 | 6 | `public double paidPerCapacity(boolean family)` | Descriptive pooled compatibility getter; it is never an independently traded quote. |
| 142 | 48 | `private void quote(Game game)` | Reads the existing builder's taxed work/material/land quote; no second construction timer. |
| 190 | 64 | `public HousingChoice.Result clear(Game game, List<HousingChoice.Demand> cells)` |  |
| 254 | 1 | **type** `private record LeaseKey(String cell, HousingInventory.Key home)` |  |
| 255 | 1 | **type** `private record EmergencyRoom(HousingChoice.Offer home, boolean hosted)` |  |
| 257 | 42 | `private void emergencyDoubling(Game game)` | Guests and hosts retain separate household counts while sharing one actual door and rent bill. |
| 299 | 1 | **type** `private record Pool(Household source, HousingChoice.Demand original, HousingChoice.Demand target)` |  |
| 300 | 3 | `private static HousingChoice.Demand with(HousingChoice.Demand d, double count, double budget)` |  |
| 304 | 75 | `private void emergencySharing(Game game)` | Normal placements and their valid incumbents are settled before this emergency phase. |
| 380 | 12 | `private List<HousingChoice.Lease> bounded(List<HousingChoice.Lease> input)` | Keeps exact physical upper bounds while tolerating only the existing packing-rounding scale. |
| 393 | 1 | `public double candidateIncome(Game game, BuildingsTemplate t, long quantity)` | Forecast only: one disjoint source per current household and per incoming worker cohort. |
| 395 | 43 | `public Forecast forecast(Game game)` | One planner read reserves standing homes and funded sites once for every alternative. |
| 438 | 28 | **type** `public final class Forecast` |  |
| 442 | 2 | `private Forecast(Game city, List<HousingChoice.Demand> remaining)` _(in HousingMarket.Forecast)_ |  |
| 444 | 3 | `public double income(BuildingsTemplate t, long quantity)` _(in HousingMarket.Forecast)_ |  |
| 448 | 10 | `public int quantityNeeded(BuildingsTemplate t)` _(in HousingMarket.Forecast)_ | Enough actual recipe units for the remaining compatible, affordable demand; no fictitious head-capacity demand. |
| 458 | 7 | `private HousingChoice.Result match(BuildingsTemplate t, long quantity)` _(in HousingMarket.Forecast)_ |  |
| 467 | 6 | `private List<HousingChoice.Demand> planningDemand()` | The current cell counts, before this month's emergency sharing transfers. |
| 473 | 8 | `public void sold()` |  |
| 482 | 15 | `public void reprice(double expectedMonthly)` | Existing lease-length log adjustment and expected-inflation drift, class by class. |
| 497 | 3 | `public State forSave()` |  |
| 500 | 1 | `private static void nonnegative(double n)` |  |
| 501 | 43 | `private static void validate(State s)` |  |
| 544 | 3 | `public void restore(State s)` |  |
| 548 | 11 | `public void restore(State s, BuildingManager buildings)` | The native load additionally checks the complete physical catalogue, including unavailable quotes. |
| 559 | 1 | `public void reset()` |  |
| 560 | 6 | `public void redenominate(double scale)` |  |

