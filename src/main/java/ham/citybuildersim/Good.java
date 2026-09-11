package ham.citybuildersim;

/**
 * A thing that can be made, bought, held, imported and exported.
 *
 * THE GOODS ECONOMY (2026-09-11). Until the sector template, the city had
 * four bespoke markets - food between the mills and the shops, ore between
 * the mines and the mills, a free yard of building material, and rent - and
 * every one of them was written by hand inside two handlers that knew each
 * other by name. A thousand sectors cannot trade that way. So a good is one
 * row here, a market is one object per row (see GoodsMarket), and a sector
 * says which rows it makes and which it uses (see Sector). Nothing else has
 * to know that bread and ore are different.
 *
 * PRICES ARE IN THE WORLD'S MONEY, per unit, in THOUSANDS like every money
 * field in the game (see BuildingsTemplate's header). A unit of food is what
 * one person eats in a month, so .20 is $200 of food; ore is .14 a tonne,
 * $140; steel .847, $847; a unit of building material is 18, $18,000, and a
 * House is ten of them. The city faces these multiplied by the exchange rate
 * (city money per dollar, times the world's own price level - see
 * Game.startOfMonthUpdate), which is why they are stored unconverted: a
 * devaluation lifts every ceiling and every floor at once, and that is what
 * a devaluation does.
 *
 * TWO WORLD PRICES, NOT ONE. What the world charges the city for a unit
 * (the import price, the CEILING on the local price - nobody pays more at
 * home than it costs to bring one in) and what the world pays the city for
 * one (the export price, the FLOOR - nobody sells at home for less than the
 * ship pays). The gap between them is freight, middlemen and the buyer's
 * margin, and it is the whole of what makes a local market worth having:
 * inside the band a local buyer and a local seller both do better than
 * trading with the world. Food's .12 against .20 is the 0.6 wedge
 * IndustrialHandler measured; ore's .14 against .41 is the mine's export
 * price against the mill's scrap, which IronMarket carried as its two ends.
 * A good with no import price (NaN) cannot be imported and has no ceiling
 * from the world; a good with no export price cannot be exported and its
 * makers idle or stock what they cannot sell.
 *
 * THREE FLAGS. Importable and exportable are the two prices. Stockable says
 * whether a maker can hold it from one month to the next: food keeps, in a
 * warehouse the plant owns; ore does not - a mine ships what it lifts; steel
 * does not. A service - a haircut, a night in a hotel - is none of the
 * three, and that is how the thousand sectors that are not factories fit the
 * same template.
 *
 * WHO SETS THE PRICE. Most goods clear in the band, on scarcity, in
 * GoodsMarket - that is Pricing.BAND. Two kinds do not: what a sector sells
 * straight to households or the city at a price it strikes itself - the
 * shops' cost-plus shelf, the landlords' lagged rent, the builders' order
 * price - is Pricing.SELLER, and the market for such a good only records
 * what was sold. See Sector for the hooks.
 *
 * NAMES ARE SAVED. A sector's stock and the month's trades are keyed by the
 * enum name, so a constant here can be added but not renamed.
 */
public enum Good {

    /** What one person eats in a month. Made by the mills, sold to the shops. */
    FOOD("Food", "unit", .20, .12, true, Pricing.BAND, false),

    /**
     * Iron ore, and the scrap that stands in for it. Made by the mines, used
     * by the mills. The import price is imported scrap, the export price is
     * what a mine gets shipping ore out - IronMarket's two ends, unchanged.
     */
    IRON("Iron ore", "tonne", .41, .14, false, Pricing.BAND, false),

    /**
     * Made by the mills and sold entirely abroad, because nothing in the
     * city buys it. Not importable: there is no local buyer to import for.
     * .847 is scrap plus half the real conversion margin - see IronMarket's
     * note on why the numbers are these numbers.
     */
    STEEL("Steel", "tonne", Double.NaN, .847, false, Pricing.BAND, false),

    /**
     * One unit of building material - a House is ten of them. Made by the
     * materials plant, used by the builders on every order and every repair,
     * imported at BuildingManager.MATERIALS_WORLD_PRICE when the city has
     * none. Exportable, at a WIDER wedge than food's: aggregate and cement
     * are heavy and cheap, and shipping them out is most of their price.
     *
     * The wedge was food's - sixty percent of the import price - for one
     * afternoon, and at that price a plant's payroll came to $7,500 a unit
     * against $10,800 abroad, so a plant with nobody to build for was a
     * profitable export mill: the first fixture city that built one built
     * six, shipped $1.1B of aggregate abroad in five years and doubled its
     * population on the jobs. Forty percent puts the world's price under
     * the plant's payroll: a plant still ships its spare nameplate rather
     * than idle (the staff are paid either way), but it cannot LIVE on it,
     * and a city that stops building sells its plants back within the
     * year. The sector is the builders' supplier, not an exporter.
     */
    MATERIALS("Building materials", "unit", 18, 7.2, true, Pricing.BAND, false),

    /**
     * What the shops sell: food, on a shelf, to a household. One unit of
     * food becomes one unit of this. Priced by the shops, cost-plus with a
     * scarcity mark-up (see sectors.Retail), never traded with the world.
     */
    GROCERIES("Groceries", "unit", Double.NaN, Double.NaN, false, Pricing.SELLER, false),

    /**
     * A home for a month. Made by the landlords out of their doors, priced
     * by the lagged rent walk (see sectors.RealEstate), and EXEMPT from sales
     * tax - long-term residential rent is an exempt supply under real HST,
     * and taxing it would put fifteen percent on every tenant in the city.
     */
    HOUSING("Housing", "home", Double.NaN, Double.NaN, false, Pricing.SELLER, true),

    /**
     * A point of construction work. Made by the builders and sold to whoever
     * orders a building, at the order price; recognised as the points are
     * delivered. See sectors.Construction.
     */
    BUILDING_WORK("Building work", "point", Double.NaN, Double.NaN, false, Pricing.SELLER, false);

    /** How a good's price is struck. */
    public enum Pricing {
        /** Clears in GoodsMarket between the export floor and the import ceiling. */
        BAND,
        /** The selling sector strikes it; the market only records the sale. */
        SELLER
    }

    private final String label;
    private final String unit;
    private final double worldImportPrice;
    private final double worldExportPrice;
    private final boolean stockable;
    private final Pricing pricing;
    private final boolean taxExempt;

    Good(String label, String unit, double worldImportPrice, double worldExportPrice,
         boolean stockable, Pricing pricing, boolean taxExempt) {
        this.label = label;
        this.unit = unit;
        this.worldImportPrice = worldImportPrice;
        this.worldExportPrice = worldExportPrice;
        this.stockable = stockable;
        this.pricing = pricing;
        this.taxExempt = taxExempt;
    }

    public String label() { return label; }
    public String unit()  { return unit; }

    /** What the world charges for one, in ITS money. NaN when it will not sell the city any. */
    public double worldImportPrice() { return worldImportPrice; }

    /** What the world pays for one, in ITS money. NaN when it will not buy any. */
    public double worldExportPrice() { return worldExportPrice; }

    public boolean importable() { return !Double.isNaN(worldImportPrice) && worldImportPrice > 0; }
    public boolean exportable() { return !Double.isNaN(worldExportPrice) && worldExportPrice > 0; }
    public boolean stockable()  { return stockable; }
    public Pricing pricing()    { return pricing; }

    /** True for a supply the sales tax never touches. Housing, today. */
    public boolean taxExempt()  { return taxExempt; }

    /**
     * How many months of the city's take a maker averages before it plans
     * a plant against it. A year for a good people eat every month; three
     * for a good that is DRAWN ON ORDER - a foundry takes four hundred
     * units of material in the month it is ordered and none for the years
     * after, and a year's average of that read as seven hundred a month in
     * the first long run, built three plants, and sold them back within the
     * year. See GoodsMarket.getDemandTrend().
     */
    public int planningMonths() { return this == MATERIALS ? 36 : 12; }

    /** Clears in the band on scarcity, as opposed to being priced by its seller. */
    public boolean traded()     { return pricing == Pricing.BAND; }

    /** The good with this saved name, or null - a save from a build without it loses that line, not the load. */
    public static Good byName(String name) {
        if (name == null) return null;
        for (Good g : values()) if (g.name().equals(name)) return g;
        return null;
    }
}
