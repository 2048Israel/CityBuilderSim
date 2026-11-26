# HousingMarket.java - 865 lines · 61 methods · 1 constants · model

`ham/citybuildersim/HousingMarket.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> One monthly residential quote and clearing, with saved class prices and actual leases.
> WHY (RES3): pooled rents attributed a cheap flat's demand to a vacant premium
> home. Quotes are asks; only occupied assignments create rental receipts.

**Uses:** [HousingChoice](HousingChoice.md) (162), [HousingInventory](HousingInventory.md) (38), [FamilyModel](FamilyModel.md) (35), [Game](Game.md) (16), [BuildingsTemplate](BuildingsTemplate.md) (8), [Household](Household.md) (7), [WorkingHousehold](WorkingHousehold.md) (6), [HousingForms](HousingForms.md) (6), [RealEstate](RealEstate.md) (6), [PayTier](PayTier.md) (5), [HousingRecipe](HousingRecipe.md) (5), [JobType](JobType.md) (4), [FamilyStructure](FamilyStructure.md) (4), [AgeBand](AgeBand.md) (3), [HouseholdBalance](HouseholdBalance.md) (3), [MoneyAudit](MoneyAudit.md) (2), [Sectors](Sectors.md) (1), [StudentHousehold](StudentHousehold.md) (1), [UnemployedHousehold](UnemployedHousehold.md) (1), [WageBand](WageBand.md) (1), [AdultStage](AdultStage.md) (1), [Retail](Retail.md) (1), [BuildingManager](BuildingManager.md) (1)

**Used by (8):** [DataSave](DataSave.md), [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [HousingChoiceCheck](HousingChoiceCheck.md), [HousingMarketCheck](HousingMarketCheck.md), [MonetaryCheck](MonetaryCheck.md), [RealEstate](RealEstate.md), [Village1900Check](Village1900Check.md)

## Sections

| line | section |
|---:|---|
| 342 | · host places within the census line (RES3c, 2026-11-25) |

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
| 40 | `public double rentPriceIndex` |  |
| 42 | `private final Map<HousingInventory.Key, ClassState> classes` |  |
| 43 | `private List<HousingChoice.Lease> leases` |  |
| 44 | `private int month` |  |
| 45 | `private HousingChoice.Result result` |  |
| 46 | `private List<HousingChoice.Demand> demand` |  |
| 47 | `private List<HousingChoice.Offer> offers` |  |
| 48 | `private List<HousingChoice.Offer> pipeline` |  |
| 49 | `private List<Sharing> sharing` |  |
| 50 | `private double hosted` |  |
| 52 | `private double rentPriceIndex` | Chained comparable paid rent, opened at the first actual rent per capacity; saved through DataSave. |
| 54 | `private boolean arrivalProjection, arrivalsNeedFamily` | Derived pre-migration workspace, discarded by clearing and rebuilt after load. |
| 55 | `private double formingFamilies, formingAdults` |  |
| 723 | `private final Game city` |  |
| 724 | `private final List<HousingChoice.Demand> remaining` |  |
| 725 | `private final Map<HousingInventory.Key, Double> asks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 854 | **type** `public final class HousingMarket` | One monthly residential quote and clearing, with saved class prices and actual leases. |
| 16 | 2 | **type** `public record Sharing(String source, String target, double groups, double sourceCount, double targetCount, ...` | A monthly transfer between existing cells, not a permanent preference or financial axis. |
| 18 | 12 | **type** `public static final class ClassState` |  |
| 25 | 1 | `HousingInventory.Key key()` _(in HousingMarket.ClassState)_ |  |
| 26 | 3 | `ClassState copy()` _(in HousingMarket.ClassState)_ |  |
| 31 | 11 | **type** `public static final class State` | A bounded save snapshot; a load restores it without quoting, clearing or repricing. |
| 57 | 1 | `public double hosted()` | Emergency guests whose host already funds the door; no extra rent or credit is invented. |
| 58 | 1 | `public double sharingGroups()` |  |
| 59 | 1 | `public List<HousingChoice.Demand> demands()` |  |
| 61 | 1 | `public List<HousingChoice.Offer> offers()` | The last clearing's doors as offered (not saved: a load has none until its next clearing). |
| 62 | 1 | `public boolean active()` |  |
| 63 | 1 | `public int clearedMonth()` |  |
| 64 | 1 | `public List<HousingChoice.Lease> leases()` |  |
| 65 | 1 | `public List<ClassState> classes()` |  |
| 66 | 1 | `public HousingChoice.Result result()` |  |
| 67 | 1 | `public double rentFor(String cell, double count)` |  |
| 68 | 1 | `public double receipts()` |  |
| 81 | 1 | `public double askedBurden(Game game, double disposable)` | THE ASKED BURDEN (RES3c, 2026-11-25): what a mover weighs, every household's cheapest suitable class's ask, the rent its next vacant door asks, over the city's take-home pay; NaN off the typed market. |
| 83 | 1 | `public double aloneBurden(Game game, double disposable)` | The same with every household asked a whole door's rent: what a household alone weighs, before a half share is open to it. |
| 84 | 8 | `private double asked(Game game, double disposable, boolean shares)` |  |
| 92 | 1 | `public double rentPriceIndex()` |  |
| 93 | 1 | `public double occupied()` |  |
| 95 | 23 | `public void prepareMigration(Game game)` | Reserve this press's domestic formation before admitting people who start work next press. |
| 119 | 36 | `public FamilyModel.Room roomLeft(Game game, FamilyModel.Room legacy)` | Read-only migration bound: actual remaining door/guest capacity, with the raw incoming household mix. |
| 155 | 1 | `public double price(HousingInventory.Key key)` |  |
| 157 | 2 | `public void setAsk(HousingInventory.Key key, double ask)` | A posted class ask can change without inventing a lease or a trade. |
| 159 | 5 | `public double meanPerCapacity(BuildingsTemplate t)` |  |
| 165 | 6 | `public double paidPerCapacity(boolean family)` | Descriptive pooled compatibility getter; it is never an independently traded quote. |
| 172 | 53 | `private void quote(Game game)` | Strikes each class's capital at the home's market value and its carry; no second construction timer. |
| 225 | 3 | `public HousingChoice.Result clear(Game game, List<HousingChoice.Demand> cells)` |  |
| 229 | 26 | `public HousingChoice.Result clear(Game game, List<HousingChoice.Demand> cells, HouseholdBalance.HousingRoute route)` | The month's clearing, the households that changed cell taking their doors along the route (carry()). |
| 256 | 34 | `void strikePriceDemand(List<HousingChoice.Offer> pricedOffers)` | Current physical occupancy and funded whole-door pressure, from the clearing's actual budget profiles. |
| 290 | 1 | **type** `private record LeaseKey(String cell, HousingInventory.Key home)` |  |
| 299 | 12 | `static List<HousingChoice.Lease> carry(List<HousingChoice.Lease> leases, HouseholdBalance.HousingRoute route)` | TENURE FOLLOWS THE PEOPLE (RES3c, 2026-11-25). |
| 312 | 1 | `public static double shares(HousingChoice.Lease l)` | A lease's households that hold a share of its doors, two to a door; the rest are its lodgers. |
| 314 | 13 | `private List<HousingChoice.Offer> supply(Game game)` | Every known class's standing doors at its ask, as the clearing offers them. |
| 336 | 5 | `static Set<String> sitting(Game game)` | TENURE (RES3c, 2026-11-25). |

### host places within the census line (RES3c, 2026-11-25) (lines 342-865)

| line | len | member | says |
|---:|---:|---|---|
| 344 | 1 | **type** `private record HostKey(HousingInventory.Key home, int size)` | The places beside lone host households of one size on one class of home. |
| 350 | 6 | `private static Map<HostKey, Double> hostPlaces(List<HousingChoice.Lease> all, Map<String, Integer> sizes)` | A lone household on its door leaves the one place beside it, two to a door; a guest takes it only if the pair stays within the door's census line (HousingChoice.PERSONS_PER_ROOM), so the places are kept by the host's ... |
| 357 | 5 | `private static void giveUp(List<HousingChoice.Lease> all, Map<String, Integer> sizes, Map<HostKey, Double> places, Map<HostKey,...` | Hosts give up the places their guests took: half a door a place, from the leases that gave it pro rata. |
| 363 | 3 | `private static HousingChoice.Offer hostRoom(HousingInventory.Key key, HousingChoice.Offer o, int host, double n)` | A host place as an offer: the home as built, free, open to one the census line still fits beside its host. |
| 366 | 1 | `private Map<String, Integer> sizes()` |  |
| 367 | 1 | `private Map<HousingInventory.Key, HousingChoice.Offer> homes()` |  |
| 370 | 35 | `private void retainHosted()` | Prior guests keep priority only in the real room funded by this clearing's paying hosts, beside a host the line fits. |
| 420 | 29 | `private void lodging()` | LODGING (RES3c, 2026-11-25). |
| 450 | 4 | `private boolean paysAShare(HousingChoice.Demand d)` | Whether a cell's budget funds half a share it fits of a home that stands: who does is not a lodger. |
| 456 | 1 | **type** `private record EmergencyRoom(HousingChoice.Offer home, HostKey host)` | A doubling node: a home's places beside one host size, or (host null) its vacant doors' paid halves. |
| 458 | 40 | `private void emergencyDoubling(Game game)` | Guests and hosts retain separate household counts while sharing one actual door and rent bill. |
| 499 | 41 | `private boolean completeNormalFootprints()` | Same-class normal space follows all movers; only actual paid-half contracts can expand. |
| 542 | 1 | **type** `private record Pool(Household source, HousingChoice.Demand original, HousingChoice.Demand target)` |  |
| 544 | 9 | `private boolean completePaidExcess(int currentMonth)` | New normal contracts follow every mover and same-class completion, without releasing old claims. |
| 553 | 3 | `private static HousingChoice.Demand with(HousingChoice.Demand d, double count, double budget)` |  |
| 557 | 77 | `private void emergencySharing(Game game)` | Normal placements and their valid incumbents are settled before this emergency phase. |
| 635 | 12 | `private List<HousingChoice.Lease> bounded(List<HousingChoice.Lease> input)` | Keeps exact physical upper bounds while tolerating only the existing packing-rounding scale. |
| 648 | 1 | `public double candidateIncome(Game game, BuildingsTemplate t, long quantity)` | Forecast only: one disjoint source per current household and per incoming worker cohort. |
| 650 | 72 | `public Forecast forecast(Game game)` | One planner read reserves standing homes and funded sites once for every alternative. |
| 722 | 28 | **type** `public final class Forecast` |  |
| 726 | 2 | `private Forecast(Game city, List<HousingChoice.Demand> remaining)` _(in HousingMarket.Forecast)_ |  |
| 728 | 3 | `public double income(BuildingsTemplate t, long quantity)` _(in HousingMarket.Forecast)_ |  |
| 732 | 10 | `public int quantityNeeded(BuildingsTemplate t)` _(in HousingMarket.Forecast)_ | Enough actual recipe units for the remaining compatible, affordable demand; no fictitious head-capacity demand. |
| 742 | 7 | `private HousingChoice.Result match(BuildingsTemplate t, long quantity)` _(in HousingMarket.Forecast)_ |  |
| 750 | 21 | `public void sold()` |  |
| 772 | 15 | `public void reprice(double expectedMonthly)` | Existing lease-length log adjustment and expected-inflation drift, class by class. |
| 787 | 3 | `public State forSave()` |  |
| 790 | 1 | `private static void nonnegative(double n)` |  |
| 791 | 51 | `private static void validate(State s)` |  |
| 842 | 3 | `public void restore(State s)` |  |
| 846 | 11 | `public void restore(State s, BuildingManager buildings)` | The native load additionally checks the complete physical catalogue, including unavailable quotes. |
| 857 | 1 | `public void reset()` |  |
| 858 | 7 | `public void redenominate(double scale)` |  |

