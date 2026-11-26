# HousingMarket.java - 1,117 lines · 85 methods · 1 constants · model

`ham/citybuildersim/HousingMarket.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> One monthly residential quote and clearing, with saved class prices and actual leases.
> WHY (RES3): pooled rents attributed a cheap flat's demand to a vacant premium
> home. Quotes are asks; only occupied assignments create rental receipts.

**Uses:** [HousingChoice](HousingChoice.md) (197), [HousingInventory](HousingInventory.md) (43), [FamilyModel](FamilyModel.md) (43), [Game](Game.md) (16), [Household](Household.md) (10), [BuildingsTemplate](BuildingsTemplate.md) (8), [WorkingHousehold](WorkingHousehold.md) (6), [HousingForms](HousingForms.md) (6), [RealEstate](RealEstate.md) (6), [PayTier](PayTier.md) (5), [HousingRecipe](HousingRecipe.md) (5), [JobType](JobType.md) (4), [FamilyStructure](FamilyStructure.md) (4), [HouseholdBalance](HouseholdBalance.md) (4), [AgeBand](AgeBand.md) (3), [MoneyAudit](MoneyAudit.md) (3), [Sectors](Sectors.md) (1), [StudentHousehold](StudentHousehold.md) (1), [UnemployedHousehold](UnemployedHousehold.md) (1), [WageBand](WageBand.md) (1), [AdultStage](AdultStage.md) (1), [Retail](Retail.md) (1), [BuildingManager](BuildingManager.md) (1)

**Used by (8):** [DataSave](DataSave.md), [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [HousingChoiceCheck](HousingChoiceCheck.md), [HousingMarketCheck](HousingMarketCheck.md), [MonetaryCheck](MonetaryCheck.md), [RealEstate](RealEstate.md), [Village1900Check](Village1900Check.md)

## Sections

| line | section |
|---:|---|
| 396 | · host places within the census line (RES3c, 2026-11-25) |

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
| 42 | `public Map<String, Double> overcrowded` | The households housed past the census line, by cell (overcrowding()); an older save has none. |
| 44 | `public Map<String, Double> withinTheLine` | ...those it placed within the line, by cell (boarding, lodging or on a share, not overcrowded); an older save has none. |
| 47 | `public Map<String, Double> boarding` | The payers boarding with a housed family, by cell, and the month's board, what each boarder cell paid and each host cell received (settleBoard()); an older save has none. |
| 50 | `public List<Board> owed` | The board the clearing's boarders owe, by the home they board in, settled at the next month's household settlement (settleBoard(), REV-RES3b on 19fcb37f0); an older save has none. |
| 52 | `private final Map<HousingInventory.Key, ClassState> classes` |  |
| 53 | `private List<HousingChoice.Lease> leases` |  |
| 54 | `private int month` |  |
| 55 | `private HousingChoice.Result result` |  |
| 56 | `private List<HousingChoice.Demand> demand` |  |
| 57 | `private List<HousingChoice.Offer> offers` |  |
| 58 | `private List<HousingChoice.Offer> pipeline` |  |
| 59 | `private List<Sharing> sharing` |  |
| 60 | `private double hosted` |  |
| 62 | `private double rentPriceIndex` | Chained comparable paid rent, opened at the first actual rent per capacity; saved through DataSave. |
| 64 | `private boolean arrivalProjection, arrivalsNeedFamily` | Derived pre-migration workspace, discarded by clearing and rebuilt after load. |
| 65 | `private double formingFamilies, formingAdults` |  |
| 67 | `private Map<String, Double> overcrowded` | The households housed past the census line this clearing, by cell (overcrowding()), saved with the state. |
| 69 | `private Map<String, Double> withinTheLine` | ...those it placed within the line, the payers boarding, and the board paid and received, by cell, saved with it. |
| 72 | `private List<Board> owed` | The board each boarding cell owes this clearing, by the home it boards in (overcrowding()), settled at the next month's household settlement on the census the leases were struck on (settleBoard()); saved. |
| 76 | `private double pastTheStock` | The most door-equivalents this clearing's trim held to the standing stock past its packing resolution (holdToStock()). |
| 944 | `private final Game city` |  |
| 945 | `private final List<HousingChoice.Demand> remaining` |  |
| 946 | `private final Map<HousingInventory.Key, Double> asks` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 12 | 1106 | **type** `public final class HousingMarket` | One monthly residential quote and clearing, with saved class prices and actual leases. |
| 16 | 2 | **type** `public record Sharing(String source, String target, double groups, double sourceCount, double targetCount, ...` | A monthly transfer between existing cells, not a permanent preference or financial axis. |
| 18 | 12 | **type** `public static final class ClassState` |  |
| 25 | 1 | `HousingInventory.Key key()` _(in HousingMarket.ClassState)_ |  |
| 26 | 3 | `ClassState copy()` _(in HousingMarket.ClassState)_ |  |
| 31 | 21 | **type** `public static final class State` | A bounded save snapshot; a load restores it without quoting, clearing or repricing. |
| 74 | 1 | **type** `public record Board(String cell, HousingInventory.Key home, double due)` | A boarding cell's board on a home: what overcrowding() set it owing, half the door's ask a household up to its budget. |
| 78 | 1 | `public double hosted()` | Emergency guests whose host already funds the door; no extra rent or credit is invented. |
| 80 | 1 | `public double overcrowded()` | Households living past the census line, overcrowded as the censuses counted them, never without a door (overcrowding()). |
| 82 | 1 | `public double overcrowdedPeople()` | Their people, at their cells' sizes. |
| 85 | 1 | `public double withinTheLine()` | Households overcrowding() placed within the census line, boarding, lodging or on a share at no more than two persons a room: not overcrowded, yet wanting a home of their own (Game.getHouseholdsWantingAHome()); saved w... |
| 88 | 1 | `public double boarders()` | The households whose budget funds a share that found no door standing and board with a housed family (overcrowding()), overcrowded or within the line as their door's persons a room fall, paying their hosts board (sett... |
| 91 | 1 | `public double boardPaid()` | This month's board, the last clearing's boarders', paid out of their cells and into their hosts' cells, every cent, at the household settlement (settleBoard()); saved. |
| 93 | 1 | `public double boardOwed()` | The board this clearing's boarders owe, settled at the next month's household settlement; saved. |
| 94 | 1 | `public Map<String, Double> boardOwedByCell()` |  |
| 95 | 1 | `public double boardReceived()` |  |
| 97 | 1 | `public Map<String, Double> overcrowdedByCell()` | By cell: past the line, within it, boarding, and the board paid and received. |
| 98 | 1 | `public Map<String, Double> withinTheLineByCell()` |  |
| 99 | 1 | `public Map<String, Double> boardingByCell()` |  |
| 100 | 1 | `public Map<String, Double> boardPaidByCell()` |  |
| 101 | 1 | `public Map<String, Double> boardReceivedByCell()` |  |
| 104 | 1 | `public double pastTheStock()` | The most door-equivalents this clearing held to the standing stock past the packing resolution: a pass that placed doors no house has, trimmed rather than thrown partway through the month (holdToStock()); LongPlaytest... |
| 105 | 1 | `private static double sum(Map<String, Double> m)` |  |
| 106 | 1 | `public double sharingGroups()` |  |
| 107 | 1 | `public List<HousingChoice.Demand> demands()` |  |
| 109 | 1 | `public List<HousingChoice.Offer> offers()` | The last clearing's doors as offered (not saved: a load has none until its next clearing). |
| 110 | 1 | `public boolean active()` |  |
| 111 | 1 | `public int clearedMonth()` |  |
| 112 | 1 | `public List<HousingChoice.Lease> leases()` |  |
| 113 | 1 | `public List<ClassState> classes()` |  |
| 114 | 1 | `public HousingChoice.Result result()` |  |
| 115 | 1 | `public double rentFor(String cell, double count)` |  |
| 116 | 1 | `public double receipts()` |  |
| 129 | 1 | `public double askedBurden(Game game, double disposable)` | THE ASKED BURDEN (RES3c, 2026-11-25): what a mover weighs, every household's cheapest suitable class's ask, the rent its next vacant door asks, over the city's take-home pay; NaN off the typed market. |
| 131 | 1 | `public double aloneBurden(Game game, double disposable)` | The same with every household asked a whole door's rent: what a household alone weighs, before a half share is open to it. |
| 132 | 8 | `private double asked(Game game, double disposable, boolean shares)` |  |
| 140 | 1 | `public double rentPriceIndex()` |  |
| 141 | 1 | `public double occupied()` |  |
| 143 | 23 | `public void prepareMigration(Game game)` | Reserve this press's domestic formation before admitting people who start work next press. |
| 167 | 38 | `public FamilyModel.Room roomLeft(Game game, FamilyModel.Room legacy)` | Read-only migration bound: actual remaining door/guest capacity, with the raw incoming household mix. |
| 205 | 1 | `public double price(HousingInventory.Key key)` |  |
| 207 | 2 | `public void setAsk(HousingInventory.Key key, double ask)` | A posted class ask can change without inventing a lease or a trade. |
| 209 | 5 | `public double meanPerCapacity(BuildingsTemplate t)` |  |
| 215 | 6 | `public double paidPerCapacity(boolean family)` | Descriptive pooled compatibility getter; it is never an independently traded quote. |
| 222 | 53 | `private void quote(Game game)` | Strikes each class's capital at the home's market value and its carry; no second construction timer. |
| 275 | 3 | `public HousingChoice.Result clear(Game game, List<HousingChoice.Demand> cells)` |  |
| 279 | 28 | `public HousingChoice.Result clear(Game game, List<HousingChoice.Demand> cells, HouseholdBalance.HousingRoute route)` | The month's clearing, the households that changed cell taking their doors along the route (carry()). |
| 308 | 1 | `void strikePriceDemand(List<HousingChoice.Offer> pricedOffers)` | Current physical occupancy and funded whole-door pressure, from the clearing's actual budget profiles. |
| 310 | 34 | `private void strikePriceDemand(List<HousingChoice.Offer> pricedOffers, List<HousingChoice.Lease> housed)` | housed: the census line's placements. |
| 344 | 1 | **type** `private record LeaseKey(String cell, HousingInventory.Key home)` |  |
| 353 | 12 | `static List<HousingChoice.Lease> carry(List<HousingChoice.Lease> leases, HouseholdBalance.HousingRoute route)` | TENURE FOLLOWS THE PEOPLE (RES3c, 2026-11-25). |
| 366 | 1 | `public static double shares(HousingChoice.Lease l)` | A lease's households that hold a share of its doors, two to a door; the rest are its lodgers. |
| 368 | 13 | `private List<HousingChoice.Offer> supply(Game game)` | Every known class's standing doors at its ask, as the clearing offers them. |
| 390 | 5 | `static Set<String> sitting(Game game)` | TENURE (RES3c, 2026-11-25). |

### host places within the census line (RES3c, 2026-11-25) (lines 396-1117)

| line | len | member | says |
|---:|---:|---|---|
| 398 | 1 | **type** `private record HostKey(HousingInventory.Key home, int size)` | The places beside lone host households of one size on one class of home. |
| 404 | 6 | `private static Map<HostKey, Double> hostPlaces(List<HousingChoice.Lease> all, Map<String, Integer> sizes)` | A lone household on its door leaves the one place beside it, two to a door; a guest takes it only if the pair stays within the door's census line (HousingChoice.PERSONS_PER_ROOM), so the places are kept by the host's ... |
| 411 | 5 | `private static void giveUp(List<HousingChoice.Lease> all, Map<String, Integer> sizes, Map<HostKey, Double> places, Map<HostKey,...` | Hosts give up the places their guests took: half a door a place, from the leases that gave it pro rata. |
| 417 | 3 | `private static HousingChoice.Offer hostRoom(HousingInventory.Key key, HousingChoice.Offer o, int host, double n)` | A host place as an offer: the home as built, free, open to one the census line still fits beside its host. |
| 420 | 1 | `private Map<String, Integer> sizes()` |  |
| 421 | 1 | `private Map<HousingInventory.Key, HousingChoice.Offer> homes()` |  |
| 424 | 35 | `private void retainHosted()` | Prior guests keep priority only in the real room funded by this clearing's paying hosts, beside a host the line fits. |
| 475 | 29 | `private void lodging()` | LODGING (RES3c, 2026-11-25). |
| 506 | 4 | `private boolean paysAShare(HousingChoice.Demand d)` | Whether a cell's budget funds half a share it fits of a home that stands: who does is not lodged by lodging(); it takes a door or share past the line at its ask, and boards only where none stands (overcrowding()). |
| 540 | 69 | `private void overcrowding(int now)` | OVERCROWDED (RES3c, 2026-11-25). |
| 610 | 1 | **type** `record Held(List<HousingChoice.Lease> leases, double trimmed, double past)` | The leases held to the homes standing, the households the trim took off a door, and its most past the resolution. |
| 618 | 12 | `static Held holdToStock(java.util.Collection<HousingChoice.Lease> leases, List<HousingChoice.Offer> offers)` | Each home's doors summed in the order given and held to the homes standing: who the trim takes off a door lodges on it, its contract sponsored, no bill (the households and their funding kept, so the lease is valid by ... |
| 631 | 1 | `private static String owner(String cell)` | The household cell a lease's cell belongs to (a sharing or pool key carries its cell's before the '#', as rentFor() reads). |
| 645 | 18 | `void settleBoard(HouseholdBalance balance)` | The board (REV-RES3b P1-1): each boarding cell pays the cells housed on the doors of the home it boards in, by their doors, what overcrowding() set it owing, half the door's ask a household up to its budget, as boarde... |
| 664 | 4 | `private static HousingChoice.Offer pastTheLine(HousingChoice.Offer o, double n, int share)` | A class's doors offered past the census line, at its ask over share (0: a lodger's, no bill), every other test kept. |
| 669 | 6 | `private static List<HousingChoice.Demand> remaining(List<HousingChoice.Demand> left, List<HousingChoice.Lease> taken)` | What a pass left of its households. |
| 677 | 1 | **type** `private record EmergencyRoom(HousingChoice.Offer home, HostKey host)` | A doubling node: a home's places beside one host size, or (host null) its vacant doors' paid halves. |
| 679 | 40 | `private void emergencyDoubling(Game game)` | Guests and hosts retain separate household counts while sharing one actual door and rent bill. |
| 720 | 41 | `private boolean completeNormalFootprints()` | Same-class normal space follows all movers; only actual paid-half contracts can expand. |
| 763 | 1 | **type** `private record Pool(Household source, HousingChoice.Demand original, HousingChoice.Demand target)` |  |
| 765 | 9 | `private boolean completePaidExcess(int currentMonth)` | New normal contracts follow every mover and same-class completion, without releasing old claims. |
| 774 | 3 | `private static HousingChoice.Demand with(HousingChoice.Demand d, double count, double budget)` |  |
| 778 | 77 | `private void emergencySharing(Game game)` | Normal placements and their valid incumbents are settled before this emergency phase. |
| 856 | 12 | `private List<HousingChoice.Lease> bounded(List<HousingChoice.Lease> input)` | Keeps exact physical upper bounds while tolerating only the existing packing-rounding scale. |
| 869 | 1 | `public double candidateIncome(Game game, BuildingsTemplate t, long quantity)` | Forecast only: one disjoint source per current household and per incoming worker cohort. |
| 871 | 72 | `public Forecast forecast(Game game)` | One planner read reserves standing homes and funded sites once for every alternative. |
| 943 | 28 | **type** `public final class Forecast` |  |
| 947 | 2 | `private Forecast(Game city, List<HousingChoice.Demand> remaining)` _(in HousingMarket.Forecast)_ |  |
| 949 | 3 | `public double income(BuildingsTemplate t, long quantity)` _(in HousingMarket.Forecast)_ |  |
| 953 | 10 | `public int quantityNeeded(BuildingsTemplate t)` _(in HousingMarket.Forecast)_ | Enough actual recipe units for the remaining compatible, affordable demand; no fictitious head-capacity demand. |
| 963 | 7 | `private HousingChoice.Result match(BuildingsTemplate t, long quantity)` _(in HousingMarket.Forecast)_ |  |
| 971 | 21 | `public void sold()` |  |
| 993 | 15 | `public void reprice(double expectedMonthly)` | Existing lease-length log adjustment and expected-inflation drift, class by class. |
| 1008 | 4 | `public State forSave()` |  |
| 1012 | 1 | `private static void nonnegative(double n)` |  |
| 1013 | 73 | `private static void validate(State s)` |  |
| 1086 | 1 | `private static Map<String, Double> scaled(Map<String, Double> m, double scale)` |  |
| 1087 | 1 | `private static Map<String, Double> kept(Map<String, Double> m)` |  |
| 1088 | 4 | `public void restore(State s)` |  |
| 1093 | 11 | `public void restore(State s, BuildingManager buildings)` | The native load additionally checks the complete physical catalogue, including unavailable quotes. |
| 1104 | 1 | `public void reset()` |  |
| 1105 | 12 | `public void redenominate(double scale)` |  |

