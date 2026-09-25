package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * How a city was founded: its name, its money's name, and the treasury and the vault the founders left it.
 *
 * WHY THIS EXISTS (0.7.10). Every city used to be founded the same way - the
 * Danzik dollar, D$2.5B in the treasury and US$1B in the vault - so none of it
 * was a fact about a city: it was five static finals in Currency and two
 * constants in Game, and a screen that wanted "the founders' dollars" read the
 * constant. Jerus asked for a screen at the start of the game "where you choose
 * the name of the city and the currency ... and perhaps the settings to choose
 * the starting cash and usd cash and offworld inflation rate ... (with option
 * to just start at default)". Once the choice exists the constant is only the
 * DEFAULT, and a screen reading it would describe a city it is not looking at.
 *
 * SO THE CHOICE IS A RECORD ON THE CITY. Set once, by the one founding path
 * (Game's constructor, newGame(), and newGame() after a load - all through
 * buildWorld()), saved with the city (DataSave: cityName, the five currency
 * fields, foundingCash, foundingReserveUsd), and never changed afterwards. The
 * world's inflation is chosen here too but not kept here: WorldEconomy already
 * saves the mean a city grew up in, and a second copy would be a second place
 * to disagree - so the record a loaded city carries reads it back from there.
 *
 * AN OLD SAVE HAS NONE OF IT and loads as legacy(): the city Danzik, the
 * Danzik dollar (Currency.DANZIK), D$2.5B and US$1B - how every city has been
 * founded since 0.6.10 (2026-09-21), when the endowment was split between the
 * treasury and the vault. Saves from before 0.6.10 were founded otherwise (the
 * whole endowment in cash, no vault); nothing reads that far back - the
 * founders' note shows only for Game.FOUNDERS_NOTE_MONTHS, and a city that
 * old is past them.
 *
 * WHAT IS NOT HERE: anything that is a figure about the city's life. The
 * record says what the city started with; the treasury and the vault say what
 * it has.
 *
 * @author Jerus
 */
public final class Founding {

    /* =====================================================================
       THE PRESETS (0.7.10)

       Jerus's four: Lean, Standard (the default), Wealthy, and Custom, each
       shown with what it buys. Standard is the two constants on Game, which
       stay the named defaults the harnesses and the playtest read; Wealthy is
       the start every city had from 0.6.10 to 0.7.9.
       ===================================================================== */

    /** Lean's treasury, in thousands: D$25M - three-quarters of the founding village at a new city's invoices, so the city borrows from its first months, for the rest of it and for every big work. */
    public static final double LEAN_CASH = 25_000;

    /** Lean's vault, in thousands of US dollars: US$10M - a few months to two years of a young city's imports. */
    public static final double LEAN_RESERVE_USD = 10_000;

    /** Wealthy's treasury, in thousands: D$2.5B - the start every city had from 0.6.10 to 0.7.9, which the playtest never drew below D$2.46B in thirty years. */
    public static final double WEALTHY_CASH = 2_500_000;

    /** Wealthy's vault, in thousands of US dollars: US$1B - the old start's, which the playtest's defence took sixty-odd years to spend half of (month 739, the median of eight seeds). */
    public static final double WEALTHY_RESERVE_USD = 1_000_000;

    /** The four choices of money; CUSTOM carries no figures of its own. */
    public enum Preset {
        LEAN("Lean", LEAN_CASH, LEAN_RESERVE_USD),
        STANDARD("Standard", Game.FOUNDING_CASH, Game.FOUNDING_RESERVE_USD),
        WEALTHY("Wealthy", WEALTHY_CASH, WEALTHY_RESERVE_USD),
        CUSTOM("Custom", Double.NaN, Double.NaN);

        private final String label;
        private final double cash, reserveUsd;

        Preset(String label, double cash, double reserveUsd) {
            this.label = label;
            this.cash = cash;
            this.reserveUsd = reserveUsd;
        }

        public String label()       { return label; }
        /** The treasury, in thousands; NaN for CUSTOM. */
        public double cash()        { return cash; }
        /** The vault, in thousands of US dollars; NaN for CUSTOM. */
        public double reserveUsd()  { return reserveUsd; }

        /** The preset these two figures are, or CUSTOM. */
        public static Preset of(double cash, double reserveUsd) {
            for (Preset p : values()) {
                if (p != CUSTOM && p.cash == cash && p.reserveUsd == reserveUsd) return p;
            }
            return CUSTOM;
        }
    }

    /* =====================================================================
       THE BOUNDS ON A CUSTOM FOUNDING

       Testing room, not policy - the way WorldEconomy's MIN_MEAN_INFLATION and
       MAX_MEAN_INFLATION are: wide enough to found a city poorer than Lean and
       richer than Wealthy, narrow enough that every figure inside them founds
       a city that runs (NewGameCheck founds one at each end and plays it).

       NO EMPTY TREASURY. simulateMonths() will not run a month while cash is
       at or below zero, and the clock's nextMonth() carries a broke city only
       on the central bank's advances, whose ceiling is months of REVENUE - of
       which a city with nothing built has none, so it cannot draw a dollar.
       And every building is paid for out of cash or borrowed against, and a
       city with no revenue cannot be quoted a bond it could service. A city
       founded with nothing could not build its first house or live its first
       month; that is not a hard start, it is not a city. The floor is a
       hamlet's worth: ten houses and a shop, at a new city's invoices.

       AN EMPTY VAULT IS ALLOWED. Every city before 0.6.10 was founded with
       one and played its whole life that way: the central bank has nothing
       to steady the currency with until the treasury buys dollars, and land -
       priced in US dollars - is bought by converting cash, as it is by
       default anyway.
       ===================================================================== */

    /** The least a city may be founded with in its treasury, in thousands: D$5M, ten houses and a shop at a new city's invoices. */
    public static final double MIN_CASH = 5_000;

    /** The most, in thousands: D$10B, four times the Wealthy start. Testing room, not policy. */
    public static final double MAX_CASH = 10_000_000;

    /** The least in the vault, in thousands of US dollars: none, as every city had before 0.6.10. */
    public static final double MIN_RESERVE_USD = 0;

    /** The most, in thousands of US dollars: US$4B, four times the Wealthy start. Testing room, not policy. */
    public static final double MAX_RESERVE_USD = 4_000_000;

    /* ------------------------------------------------------------ the name */

    /** The city a founding is named when nobody names it - Jerus's first city, and every save's before 0.7.10. */
    public static final String DEFAULT_CITY_NAME = "Danzik";

    /** The longest city name: room for the window's title and the slot list's line, not a policy. */
    public static final int MAX_CITY_NAME_LENGTH = 24;

    /* ------------------------------------------------------------ the record */

    private final String cityName;
    private final Currency currency;
    private final double cash;
    private final double reserveUsd;
    private final double meanInflation;

    /**
     * A founding exactly as given. The load path's door, and the one the
     * factories below end in; nothing here judges it - problem() does, and
     * Game.newGame() will not found a city it has a problem with.
     *
     * @param meanInflation the world's average inflation the city is founded
     *        into. Chosen here; kept by WorldEconomy, not in the save's
     *        founding fields - a loaded record is handed back the world's.
     */
    public Founding(String cityName, Currency currency, double cash, double reserveUsd, double meanInflation) {
        this.cityName = cityName;
        this.currency = currency;
        this.cash = cash;
        this.reserveUsd = reserveUsd;
        this.meanInflation = meanInflation;
    }

    /** "Found with defaults": Danzik, its money named after it, the Standard preset, the default world. */
    public static Founding defaults() {
        return named(DEFAULT_CITY_NAME, Preset.STANDARD, WorldEconomy.DEFAULT_MEAN_INFLATION);
    }

    /** A city with its money named after it, on one of the three presets with figures. */
    public static Founding named(String cityName, Preset preset, double meanInflation) {
        if (preset == Preset.CUSTOM) throw new IllegalArgumentException("CUSTOM has no figures; use custom()");
        return new Founding(clean(cityName), Currency.fromCityName(clean(cityName)),
                preset.cash(), preset.reserveUsd(), meanInflation);
    }

    /** A city with its money named after it, on figures of the player's own. */
    public static Founding custom(String cityName, double cash, double reserveUsd, double meanInflation) {
        return new Founding(clean(cityName), Currency.fromCityName(clean(cityName)), cash, reserveUsd, meanInflation);
    }

    /** The same founding with money the player named by hand - Currency.typed(), which is null when the typed pair does not pass, and problem() then says so. */
    public Founding withCurrency(Currency typed) {
        return new Founding(cityName, typed, cash, reserveUsd, meanInflation);
    }

    /** What a save from before 0.7.10 was founded with. See the class header. */
    public static Founding legacy(double meanInflation) {
        return new Founding(DEFAULT_CITY_NAME, Currency.DANZIK, WEALTHY_CASH, WEALTHY_RESERVE_USD, meanInflation);
    }

    private static String clean(String name) { return name == null ? "" : name.trim(); }

    public String getCityName()        { return cityName; }
    public Currency getCurrency()      { return currency; }
    /** The treasury it was founded with, in thousands. */
    public double getCash()            { return cash; }
    /** The vault it was founded with, in thousands of US dollars - bought on day one at the opening rate. */
    public double getReserveUsd()      { return reserveUsd; }
    /** The world's average inflation it was founded into. */
    public double getMeanInflation()   { return meanInflation; }
    /** Which of the four this is. */
    public Preset getPreset()          { return Preset.of(cash, reserveUsd); }

    /* ------------------------------------------------------------ is it a city */

    /** Why a city name will not do, in the player's words, or null when it will. */
    public static String cityNameProblem(String name) {
        String n = clean(name);
        if (n.isEmpty()) return "Name the city.";
        if (n.length() > MAX_CITY_NAME_LENGTH) return "A city's name can be at most " + MAX_CITY_NAME_LENGTH + " characters.";
        if (n.codePoints().noneMatch(Character::isLetter)) return "A city's name needs at least one letter.";
        if (n.codePoints().anyMatch(Character::isISOControl)) return "A city's name cannot hold control characters.";
        return null;
    }

    /** Why a treasury will not do, or null. */
    public static String cashProblem(double cash) {
        if (!Double.isFinite(cash) || cash < MIN_CASH || cash > MAX_CASH) {
            return String.format("The treasury has to be between $%,.0fM and $%,.0fM.", MIN_CASH / 1000, MAX_CASH / 1000);
        }
        return null;
    }

    /** Why a vault will not do, or null. */
    public static String reserveProblem(double reserveUsd) {
        if (!Double.isFinite(reserveUsd) || reserveUsd < MIN_RESERVE_USD || reserveUsd > MAX_RESERVE_USD) {
            return String.format("The vault has to be between US$%,.0fM and US$%,.0fM.",
                    MIN_RESERVE_USD / 1000, MAX_RESERVE_USD / 1000);
        }
        return null;
    }

    /**
     * Why this founding cannot found a city, or null when it can: the first
     * of the city's name, its money (a typed pair that did not pass leaves no
     * currency - the screen says which half), the two amounts and the world.
     */
    public String problem() {
        String p = cityNameProblem(cityName);
        if (p != null) return p;
        if (currency == null) return "The currency's name or code will not do.";
        if (Currency.FOREIGN_CODE.equals(currency.code())) return Currency.codeProblem(currency.code());
        p = cashProblem(cash);
        if (p != null) return p;
        p = reserveProblem(reserveUsd);
        if (p != null) return p;
        if (!(meanInflation >= WorldEconomy.MIN_MEAN_INFLATION && meanInflation <= WorldEconomy.MAX_MEAN_INFLATION)) {
            return "The world's inflation is outside what a city can be founded into.";
        }
        return null;
    }

    /* =====================================================================
       WHAT IT BUYS (0.7.10)

       Jerus: "each option shows what it buys". A model figure, worked out
       here over the catalogue's own costs, never on the screen.

       AT THE INVOICE, NOT THE STICKER. What the treasury is charged for an
       order is its cash cost and the material it takes beyond what the yard
       holds, bought abroad at the world's price (Game.quoteBuild()); at the
       founding there is no plant in the city, the rate is the opening rate
       and the world's price level is one, so that is a closed form - and the
       sticker alone would promise a water plant for D$65.8M that is invoiced
       at D$109.6M. NewGameCheck founds a city, places the village and holds
       this to what it was actually charged.

       THE VILLAGE is the playtest's own, placed by hand before its advisor
       takes over: sixty houses, five shops, two farms and a depot. THE FIRST
       WORKS are the four things a town needs before it grows that the
       treasury buys and nobody else will: power (a wind farm, the advisor's
       own first choice; a coal plant is the price of a whole city), water, a
       school and the police. The clinic is not among them: at D$1.8M it is
       paid out of change and says nothing about an endowment.
       ===================================================================== */

    /** The founding village: the playtest's hand-built settlement, name and count. */
    public static final String[][] VILLAGE = {
            { "House", "60" }, { "Convenience Store", "5" }, { "Mixed Farm", "2" }, { "Construction Depot", "1" } };

    /** The first big works, in the order a young city tends to need them. */
    public static final String[] FIRST_WORKS = {
            "Wind Farm", "Water Treatment Plant", "Elementary School", "Police Station" };

    /** One of the first works: what it is invoiced at on a new city, and whether the treasury pays it in cash after the village or needs a bond for the rest. */
    public record Work(String name, double cost, boolean fitsInCash, double bondNeeded) { }

    /**
     * What a founding buys: the village, what is left, each first work taken
     * on its own after the village, and the vault in words.
     */
    public record Buys(double village, double leftAfterVillage, List<Work> works,
                       double reserveUsd, String vaultSays) {

        /** The works the treasury pays for in cash after the village, each on its own. */
        public List<Work> inCash() {
            List<Work> out = new ArrayList<>();
            for (Work w : works) if (w.fitsInCash()) out.add(w);
            return out;
        }

        /** The ones it needs a bond for. */
        public List<Work> onABond() {
            List<Work> out = new ArrayList<>();
            for (Work w : works) if (!w.fitsInCash()) out.add(w);
            return out;
        }
    }

    /** What one unit of building material costs a new city abroad: the world's price at the opening rate, the world's level at one. */
    public static double foundingMaterialPrice() {
        return Good.MATERIALS.worldImportPrice() * ForeignAccounts.OPENING_RATE;
    }

    /**
     * An order's invoice on a new city with `yard` units of material in the
     * yard: its cash cost, and whatever material the yard does not hold at
     * foundingMaterialPrice(). Game.quoteBuild() on a city that has just
     * been founded, as a closed form.
     */
    public static double orderCost(BuildingsTemplate t, int quantity, double yard) {
        double needed = t.constructionMaterials * (double) quantity;
        return t.getCashCost() * quantity + Math.max(0, needed - yard) * foundingMaterialPrice();
    }

    /** What these two figures buy, over this catalogue. See the banner above. */
    public static Buys whatItBuys(List<BuildingsTemplate> catalogue, double cash, double reserveUsd) {
        double yard = BuildingManager.BASE_MATERIALS;
        double village = 0;
        for (String[] order : VILLAGE) {
            BuildingsTemplate t = find(catalogue, order[0]);
            if (t == null) continue;
            int n = Integer.parseInt(order[1]);
            village += orderCost(t, n, yard);
            yard = Math.max(0, yard - t.constructionMaterials * (double) n);
        }
        double left = cash - village;
        List<Work> works = new ArrayList<>();
        for (String name : FIRST_WORKS) {
            BuildingsTemplate t = find(catalogue, name);
            if (t == null) continue;
            double cost = orderCost(t, 1, yard);
            works.add(new Work(name, cost, cost <= left, Math.max(0, cost - Math.max(0, left))));
        }
        return new Buys(village, left, works, reserveUsd, vaultSays(reserveUsd));
    }

    /** The vault, in words: what it is for. */
    public static String vaultSays(double reserveUsd) {
        if (reserveUsd <= 0) {
            return "No vault. The central bank has no dollars to steady the currency with until the "
                    + "treasury buys some, and land, which is priced in US dollars, is bought by converting cash.";
        }
        return String.format("US$%,.0fM in the vault, the central bank's. It sells these dollars to steady "
                + "the currency when a trade deficit pushes it down, and land, which is priced in US dollars, "
                + "can be paid out of it. The IMF's old rule of thumb is three months of imports.",
                reserveUsd / 1000);
    }

    private static BuildingsTemplate find(List<BuildingsTemplate> catalogue, String name) {
        for (BuildingsTemplate t : catalogue) if (name.equals(t.getName())) return t;
        return null;
    }
}
