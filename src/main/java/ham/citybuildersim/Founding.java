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
 * AND THE WORLD IT STANDS ON (0.7.56, batch J1a): the seed of the World the
 * city is founded on - its coast, its lakes and river, the fields of ore and
 * oil under it - saved as worldSeed. Founding.defaults() and every preset take
 * DEFAULT_WORLD_SEED, so the harnesses and the playtest found on one world;
 * the founding screen rolls a new one when it opens (rollWorldSeed()). A save
 * from before 0.7.56 has none, and reads one made from what it does carry
 * (derivedWorldSeed()), the same every time it is loaded until a save keeps
 * it. The city's land stands on it since 0.7.57 (CityLand; the project's
 * spec-land.md).
 *
 * @author Jerus
 */
public final class Founding {

    /* =====================================================================
       THE PRESETS (0.7.10)

       Jerus's four: Lean, Standard (the default), Wealthy, and Custom, each
       shown with what it buys until 0.7.20, and since then with its treasury
       and its vault alone. Standard is the two constants on Game, which
       stay the named defaults the harnesses and the playtest read; Wealthy is
       the start every city had from 0.6.10 to 0.7.9.

       ...AND A FIFTH, INSANE (0.7.14), first, the hardest. Jerus: "on game
       start, add a new difficulty called Insane, which is just you start with
       0 cash, 0 vault, and a 20y bond 3% for the initial land cost the city
       starts with ... if easier the starting debt is abroad in usd". Nothing
       in the treasury, nothing in the vault, and the city owes the world for
       its ground: LandManager.STARTING_SQ_FT at the land market's opening
       dollar price a square foot (LandMarket.openingUsdPerSqFt()), about
       US$2.1M, on the model's own twenty-year dollar term loan with its
       coupon fixed at Jerus's 3% (landBondUsd(), Game.foundTheLandBond()). A
       founding of D$0 and US$0 reads back as Insane (Preset.of()).
       ===================================================================== */

    /** Insane's coupon on the land it owes for, a year: 3%, Jerus's number. */
    public static final double INSANE_LAND_COUPON = .03;

    /** Insane's land bond's term, in years: twenty, Jerus's "20y" - one of the five term loans (LongTermBond.MATURITIES). */
    public static final int INSANE_LAND_YEARS = 20;

    /** What an Insane city owes for its founding ground, in thousands of US dollars: every starting square foot at the land market's opening dollar price - STARTING_SQ_FT, the figure its centre is drawn to hold, though since 0.7.67 it owns the dry plots drawn, a little more. */
    public static double landBondUsd() {
        return LandManager.STARTING_SQ_FT * LandMarket.openingUsdPerSqFt();
    }

    /** Lean's treasury, in thousands: D$25M - two-thirds of the founding village at a new city's invoices (three-quarters until the builders' sales tax went into them, 0.7.19), so the city borrows from its first months, for the rest of it and for every big work. */
    public static final double LEAN_CASH = 25_000;

    /** Lean's vault, in thousands of US dollars: US$10M - a few months to two years of a young city's imports. */
    public static final double LEAN_RESERVE_USD = 10_000;

    /** Wealthy's treasury, in thousands: D$2.5B - the start every city had from 0.6.10 to 0.7.9, which the playtest never drew below D$2.46B in thirty years. */
    public static final double WEALTHY_CASH = 2_500_000;

    /** Wealthy's vault, in thousands of US dollars: US$1B - the old start's, which the playtest's defence took sixty-odd years to spend half of (month 739, the median of eight seeds). */
    public static final double WEALTHY_RESERVE_USD = 1_000_000;

    /** The five choices of money, hardest first; CUSTOM carries no figures of its own. */
    public enum Preset {
        /** Nothing in the treasury or the vault, and the founding ground owed abroad (0.7.14): the one preset under MIN_CASH, on purpose. */
        INSANE("Insane", 0, 0),
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

       NO EMPTY TREASURY ON A CUSTOM FOUNDING - AND ONE PRESET WITH ONE, ON
       PURPOSE. The floor is a hamlet's worth: ten houses and a shop at a new
       city's invoices, the least a custom founding takes. Insane (0.7.14) is
       the one preset under it: D$0, and since 0.7.15 it runs from day one on
       the central bank's advances. Jerus: "Play works from day one. The
       central bank covers what the treasury must pay, which starts with just
       the land bond's coupon; optional spending is refused." What it must
       pay - its promises, on day 0 the land bond's coupon and the pensions
       of the residents it is founded with - is paid, taking the treasury
       under nothing, and the next settle advances the shortfall
       (Game.settleTreasury()). Anything discretionary is refused: the
       central bank's ceiling is months of revenue, nothing for a city that
       has had none, so the treasury may spend only cash it has
       (Game.discretionaryRoom()). BORROWING IS HOW IT BUILDS. Every building
       is paid for out of cash or borrowed against, and a city with no cash
       borrows for it: the build screen's funding page, the Finances tab's
       borrowing, the land office's funding page. A day-0 city is quoted its
       first bond at the full spread on both of the measures the price reads
       - it has no revenue and no output to measure its debt against
       (DebtManager.spreadFor()) - which is what Insane costs. (0.7.14 held
       the play clock until the city borrowed, Jerus's "Borrow first" then;
       0.7.15 took the stop out, and the time skip's with it.)

       AN EMPTY VAULT IS ALLOWED. Every city before 0.6.10 was founded with
       one and played its whole life that way: the central bank has nothing
       to steady the currency with until the treasury buys dollars, and land -
       priced in US dollars - is bought by converting cash, as it is by
       default anyway.
       ===================================================================== */

    /** The least a city may be founded with in its treasury, in thousands: D$6M, ten houses and a shop at a new city's invoices - D$5.37M since the builders' sales tax went into them (0.7.19; it was D$5M, over D$4.56M). */
    public static final double MIN_CASH = 6_000;

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

    /* ------------------------------------------------------------ the world */

    /** The world a founding stands on when nobody rolls another: 4127, the map mockup's default seed (city-map.html), so every harness and the playtest found on the same ground. */
    public static final long DEFAULT_WORLD_SEED = 4127;

    /** The largest seed the founding screen's dice rolls: 999,999,999, nine digits a player can read back and type. Any whole number is a world; this is only the dice's range. */
    public static final long ROLLED_SEED_MAX = 999_999_999;

    /** A seed for the founding screen's dice, 1 to ROLLED_SEED_MAX. Not deterministic, on purpose: nothing in the model calls it. */
    public static long rollWorldSeed() {
        return java.util.concurrent.ThreadLocalRandom.current().nextLong(1, ROLLED_SEED_MAX + 1);
    }

    /** A typed seed as a whole number - digits, a leading minus, commas ignored - or null when it is not one. */
    public static Long parseWorldSeed(String typed) {
        String t = typed == null ? "" : typed.replace(",", "").trim();
        if (!t.matches("-?[0-9]{1,19}")) return null;
        try {
            return Long.parseLong(t);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * The seed a save from before 0.7.56 is read with (spec-land 2.9): SplitMix64
     * of its city's name's hash, the bits of the treasury it was founded with,
     * the bits of the ground it owns and the month, XORed - what the save
     * carries, so the same save gives the same world on every load, and two
     * cities almost never share one.
     */
    public static long derivedWorldSeed(String cityName, double foundingCash, double landOwnedSqFt, int month) {
        long z = (cityName == null ? 0 : cityName.hashCode()) ^ Double.doubleToLongBits(foundingCash)
                ^ Double.doubleToLongBits(landOwnedSqFt) ^ month;
        return World.mix(z);
    }

    /* ------------------------------------------------------------ the record */

    private final String cityName;
    private final Currency currency;
    private final double cash;
    private final double reserveUsd;
    private final double meanInflation;
    private final long worldSeed;

    /**
     * A founding exactly as given. The load path's door, and the one the
     * factories below end in; nothing here judges it - problem() does, and
     * Game.newGame() will not found a city it has a problem with.
     *
     * @param meanInflation the world's average inflation the city is founded
     *        into. Chosen here; kept by WorldEconomy, not in the save's
     *        founding fields - a loaded record is handed back the world's.
     * @param worldSeed the seed of the World it stands on (0.7.56)
     */
    public Founding(String cityName, Currency currency, double cash, double reserveUsd, double meanInflation,
                    long worldSeed) {
        this.cityName = cityName;
        this.currency = currency;
        this.cash = cash;
        this.reserveUsd = reserveUsd;
        this.meanInflation = meanInflation;
        this.worldSeed = worldSeed;
    }

    /** ...on the default world, DEFAULT_WORLD_SEED. */
    public Founding(String cityName, Currency currency, double cash, double reserveUsd, double meanInflation) {
        this(cityName, currency, cash, reserveUsd, meanInflation, DEFAULT_WORLD_SEED);
    }

    /** The defaults: Danzik, its money named after it, the Standard preset, the default world - what "Found with defaults" founded until 0.7.21. */
    public static Founding defaults() {
        return named(DEFAULT_CITY_NAME, Preset.STANDARD, WorldEconomy.DEFAULT_MEAN_INFLATION);
    }

    /** A city with its money named after it, on one of the four presets with figures. */
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
        return new Founding(cityName, typed, cash, reserveUsd, meanInflation, worldSeed);
    }

    /** The same founding on another world: the founding screen's World field and its dice (0.7.56). */
    public Founding withWorldSeed(long seed) {
        return new Founding(cityName, currency, cash, reserveUsd, meanInflation, seed);
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
    /** Which of the five this is. */
    public Preset getPreset()          { return Preset.of(cash, reserveUsd); }
    /** The seed of the world it stands on (0.7.56): World.of(getWorldSeed()) is its ground. */
    public long getWorldSeed()         { return worldSeed; }

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
        // Insane is the one founding under the custom bounds, on purpose (0.7.14).
        if (getPreset() != Preset.INSANE) {
            p = cashProblem(cash);
            if (p != null) return p;
            p = reserveProblem(reserveUsd);
            if (p != null) return p;
        }
        if (!(meanInflation >= WorldEconomy.MIN_MEAN_INFLATION && meanInflation <= WorldEconomy.MAX_MEAN_INFLATION)) {
            return "The world's inflation is outside what a city can be founded into.";
        }
        return null;
    }

    /* =====================================================================
       WHAT IT BUYS (0.7.10)

       Jerus: "each option shows what it buys". A model figure, worked out
       here over the catalogue's own costs, never on the screen. Since 0.7.20
       the screen does not show it (Jerus: "even the screen shouldnt say what
       the money could buy, the start screen should be real simple"), and it
       stays for NewGameCheck, FundCheck, ReadPathCheck and the playtest.

       AT THE INVOICE, NOT THE STICKER. What the treasury is charged for an
       order is its cash cost and the material it takes beyond what the yard
       holds, bought abroad at the world's price, with the builders' sales tax
       on both since 0.7.19 (Game.quoteBuild()); at the founding there is no
       plant in the city, no wage has moved, the rate is the opening rate and
       the world's price level is one, so that is a closed form - and the
       sticker alone would promise a water plant for D$65.8M that is invoiced
       at D$129.0M (D$109.6M before the tax went in). NewGameCheck founds a
       city, places the village and holds this to what it was actually
       charged.

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

    /** The builders' sales tax on a new city: what a fresh policy charges them (0.7.19; Game, THE BUILDERS' PRICE). */
    public static double foundingBuildersRate() {
        return new TaxPolicy().effectiveSalesRate("Construction");
    }

    /**
     * An order's invoice on a new city with `yard` units of material in the
     * yard: its cash cost - no wage has been paid yet, so its labour is at
     * the founding ladder - and whatever material the yard does not hold at
     * foundingMaterialPrice(), with the builders' sales tax passed on (0.7.19).
     * Game.quoteBuild() on a city that has just been founded, as a closed form.
     */
    public static double orderCost(BuildingsTemplate t, int quantity, double yard) {
        double needed = t.constructionMaterials * (double) quantity;
        return (t.getCashCost() * quantity + Math.max(0, needed - yard) * foundingMaterialPrice())
                / (1 - foundingBuildersRate());
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
