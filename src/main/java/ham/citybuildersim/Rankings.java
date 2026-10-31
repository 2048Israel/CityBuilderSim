package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The Rankings board as the screen draws it: a measure's table of ranked places, the active city among
 * the real world's 196 nations and 62 city regions or among the nations of its own world, in the money
 * and at the prices the player picked, with the sentence that says what is on screen. A pure model in
 * the *View manner: no JavaFX, nothing in the month reads it.
 *
 * WHY. A table that ranks a city's GDP beside Japan's is only as honest as the money it is in, and the
 * game has three: the city's own, the game's US dollar (which inflates with the world's US prices) and
 * the real US dollar of 2015. A screen that converted each row as it drew it would let the Money toggle
 * reorder the board (a city could pass a nation by changing the unit it is shown in) and let two
 * conversions of one figure disagree. So every money figure is turned ONCE into US$ of 2015 (the pivot),
 * ranked there, and only then shown in the money picked (the display): Money can never reorder a board,
 * and the city's own figure in its own money comes back to the figure the rest of the game shows. A
 * founding game dollar counts as one 2015 dollar (D1), so at founding the game's US dollar and the real
 * one are equal.
 *
 * Builds spec-leaderboards 3 (the pivot, the display, the identities, the lock, the sentence), 4 (the two
 * boards: Real world 2025 from WorldMeasures, Your world from Nations at the city's month, the city
 * regions by share), 6 (the city's side, CityMeasures), 7 (the marks), 9 (Around you, Your size, the
 * drawer) and 10 (D1, D9, D10, D13, D14, D15). The saved cities (LB3, spec-leaderboards 5) are Kind.SAVED rows,
 * each its own world pivoted by its own rate, parity and founding rate, and by its own price level or the active
 * city's (Prices, D2): the toggle's reason to exist.
 */
public final class Rankings {

    private Rankings() { }

    /** The two boards: the real world's latest year (spec-leaderboards 4), and the nations of the city's own world at its month. */
    public enum Board { REAL_2025, YOUR_WORLD }

    /** The money a figure is shown in: the city's own, the game's US dollars, or real US dollars at 2015 prices. */
    public enum Money { CITY, GAME_USD, REAL_USD }

    /** Whether a saved city's figure is in its own year's dollars (PER_YEAR) or the active city's year (STATIC). Moves only saved rows (LB3), and not under real US$ or Constant, which pricesLocked says. */
    public enum Prices { PER_YEAR, STATIC }

    /** How a foreign figure becomes this city's: at this month's rate, at the constant 2015 rate, or at purchasing power parity (D14). */
    public enum Convert { MARKET, CONSTANT, PPP }

    /** What a row is. SAVED is one of the other slots (LB3), read through SavedCities. */
    public enum Kind { NATION, REGION, CITY, SAVED }

    /** What the player picked on the four controls. */
    public record Settings(Board board, Money money, Prices prices, Convert convert) {
        /** What the tab opens on (D9, spec-leaderboards 9): Real world 2025, real US$, per year, this month's rate. */
        public static final Settings OPENING = new Settings(Board.REAL_2025, Money.REAL_USD, Prices.PER_YEAR, Convert.MARKET);
    }

    /** The active city at its month: what the pivot and its row need. */
    public record City(String name, String currency /* the city's money, in the plural: "Arden dollars" */, int month, int year,
                       double fx /* city's money per game US$ now */, double parity /* fxParity now */,
                       double usPrices /* P: the game US$'s price level since founding, 1 at founding */,
                       double foundingRate /* fx at founding in today's money */,
                       Map<String, CityMeasures.Figure> figures) {
        public City {
            figures = Collections.unmodifiableMap(new LinkedHashMap<>(figures == null ? Map.of() : figures));
        }

        /**
         * The live city, off public getters only: its name, its money (Currency.plural()), its month and year,
         * ForeignAccounts' rate and parity, WorldEconomy's price level, the founding rate in today's money
         * (Denomination.money(ForeignAccounts.OPENING_RATE), as WN4 gives the real dollar), and CityMeasures
         * over its history with its dry ground (LandManager.km2 of getOwnedSqFt: the real file's area is land area, inland
         * water left out, and a save keeps only the dry ground, LB3), the people in each age band (PopulationCohorts)
         * and the wage floor in today's money (LabourMarket.cashMinimumWage). It reads the history through
         * alignedTail a measure at a time: a screen that draws it more than once a month should keep it.
         */
        public static City of(Game g) {
            double[] bands = new double[AgeBand.values().length];
            for (AgeBand band : AgeBand.values()) bands[band.ordinal()] = g.getCohorts().get(band);
            CityMeasures.Extras extras = new CityMeasures.Extras(LandManager.km2(g.getLandManager().getOwnedSqFt()), bands, g.getLabourMarket().cashMinimumWage());
            ForeignAccounts abroad = g.getForeignAccounts();
            int month = g.getMonth();
            return new City(g.getCityName(), g.getCurrency().plural(), month, CityCalendar.yearOf(month),
                    abroad.getRate(), abroad.getParity(), g.getWorldEconomy().getPriceLevel(),
                    g.getDenomination().money(ForeignAccounts.OPENING_RATE), CityMeasures.of(g.getHistorySave(), extras));
        }
    }

    /**
     * One ranked place. value is what it ranks on: US$ of 2015 for a money measure, else the figure in the measure's unit; shown is value in the
     * display money (value itself for a measure that is not money). mark is 3 (published), 2 (a published projection), 1 (the game's estimate), 0 for the
     * city; source is a source id of the file's meta ("world-1" for a nation of the game's world, "city" for the city); rule is an estimate's rule id of
     * the file's meta, else null (a Your-world estimate is the game's path, which the file has no rule for). nation is a region's nation id (ISO3), null
     * otherwise. year is the figure's, the city's year for the city. people is NaN where unknown.
     */
    public record Row(int rank, String id, Kind kind, String name, String nation, double value, double shown, int year, int mark,
                      String source, String rule, double people) { }

    /**
     * A measure's board, ranked. ranked is false when this board does not rank the measure (Your world's other 60: rows is empty); realFigures is false
     * when no real place has a figure for it, only the player's cities rank (D10: the real rows show "-"); you is the city's index in rows, -1 when it is
     * off; estimates counts the rows that are the game's estimates (mark 1) of figures, the rows with a mark (not the city).
     */
    public record Table(WorldMeasures.Measure measure, Settings settings, List<Row> rows, int you, boolean ranked, boolean realFigures,
                        int estimates, int figures) { }

    /**
     * A saved city on the board (LB3): the slot it was read from and the city as that save has it, a City in the units of its last save (its own fx,
     * parity, P and founding rate, all rescaled together by a reform, so the pivot divides the unit out). Its row's id is "saved:" + slot, its year the
     * city's, its mark 0 and its source SOURCE_SAVE, as the active city's row has SOURCE_CITY. SavedCities makes them, at the last save or at an age.
     */
    public record Saved(int slot, City city) {
        /** The id of this city's row, and what the drawer is asked for. */
        public String id() {
            return SAVED_PREFIX + slot;
        }
    }

    /** One value of the rank over the years: the active city's rank on one measure at a December (a game month), among `of` places. */
    public record RankPoint(int month, int rank, int of) { }

    /** One measure for one place, in the drawer: its figure on each board in the display money, with its mark and source on the real board, NaN where there is none. */
    public record Line(WorldMeasures.Measure measure, double real2025 /* shown */, int realMark, String realSource,
                       double yourWorld /* shown, NaN when that board does not rank it */, int yourMark) { }

    /* ---------------------------------------------------------------------
       THE CONSTANTS
       --------------------------------------------------------------------- */

    /** The year the real dollar is struck at: US$ of 2015 (D29: "later the start date will move back anyways"). */
    public static final int BASE_YEAR = 2015;

    /** The real world's year: the board Real world 2025. */
    public static final int REAL_YEAR = 2025;

    /** The id the drawer takes for the active city. */
    public static final String PLACE_CITY = "city";

    /** A row's source: the active city's own figure, a nation of the game's world file (a source id of world-2025.json's meta), and a saved city's figure. */
    public static final String SOURCE_CITY = "city", SOURCE_WORLD = "world-1", SOURCE_SAVE = "save";

    /** What a saved city's row id starts with, then its slot: the id the drawer is asked for. */
    public static final String SAVED_PREFIX = "saved:";

    /** The rule a city region's Your-world figure is an estimate by: its share of its nation's path (a rule id of the file's meta). */
    public static final String RULE_REGION = "region-output";

    /** The places Your size keeps: those within this factor of the city's people, either way. */
    public static final double YOUR_SIZE_FACTOR = 10;

    /** Around you: the rows from the top, and either side of the city; the rows from the top when the city is off the board. */
    public static final int AROUND_TOP = 3, AROUND_EITHER_SIDE = 6, AROUND_OFF = 10;

    /** Two figures within this fraction of each other are one figure and share a rank: a city saved before and after a reform by 100 differs in its last digits only (LB3). */
    public static final double TIE = 1e-9;

    /** The money measures with a series at constant prices (gdp, gdp per person, household spending per person); the rest show this month's rate under Constant. */
    static final Set<String> CONSTANT_SERIES = Set.of("gdp", "gdp_pp", "hh_spend_pp");

    /** Whether a measure has a series at constant prices: the others (wages) show this month's rate under Constant, and the sentence says so. */
    public static boolean hasConstantSeries(WorldMeasures.Measure m) {
        return m.money() && CONSTANT_SERIES.contains(m.key());
    }

    /** The settings a measure is really converted at: Constant becomes this month's rate for a money measure with no constant series. */
    static Settings effective(Settings s, WorldMeasures.Measure m) {
        if (m.money() && s.convert() == Convert.CONSTANT && !hasConstantSeries(m))
            return new Settings(s.board(), s.money(), s.prices(), Convert.MARKET);
        return s;
    }

    /* ---------------------------------------------------------------------
       THE DISPLAY (spec-leaderboards 3)
       --------------------------------------------------------------------- */

    /**
     * Real US$ of 2015 into the money picked: REAL_USD as is; GAME_USD times the game dollar's price level (times 1 under Constant, which is at
     * founding's prices); CITY times that level and the rate (this month's rate, or parity under PPP), or under Constant the founding rate. So the
     * city's own figure in its own money is X under this month's rate and PPP and X at founding's prices under Constant. s must be the settings the
     * figure was pivoted at (effective() for a money measure with no constant series).
     */
    public static double display(double usd15, Settings s, City c) {
        return switch (s.money()) {
            case REAL_USD -> usd15;
            case GAME_USD -> s.convert() == Convert.CONSTANT ? usd15 : usd15 * c.usPrices();
            case CITY -> switch (s.convert()) {
                case MARKET -> usd15 * c.usPrices() * c.fx();
                case PPP -> usd15 * c.usPrices() * c.parity();
                case CONSTANT -> usd15 * c.foundingRate();
            };
        };
    }

    private static double shown(double value, Settings eff, City c, WorldMeasures.Measure m) {
        return m.money() ? display(value, eff, c) : value;
    }

    /* ---------------------------------------------------------------------
       THE LOCK AND THE SENTENCE (spec-leaderboards 3)
       --------------------------------------------------------------------- */

    /**
     * Whether Prices is greyed: Money is real US$ (2015 prices by definition), Convert is Constant, the measure is not money, or no saved city is on the
     * board (the caller passes !saved.isEmpty()). A locked Prices reads as STATIC in the saved rows' pivot (savedCell).
     */
    public static boolean pricesLocked(Settings s, WorldMeasures.Measure m, boolean anySaved) {
        return !pricesLockedWhy(s, m, anySaved).isEmpty();
    }

    /** The sentence for the greyed control's tooltip, saying which reason locks it; empty when it is not locked. */
    public static String pricesLockedWhy(Settings s, WorldMeasures.Measure m, boolean anySaved) {
        if (!m.money()) return capitalised(m.name()) + " is not money, so Prices has nothing to move.";
        if (s.money() == Money.REAL_USD) return "Real US dollars are at 2015 prices by definition, so Prices has nothing to move.";
        if (s.convert() == Convert.CONSTANT) return "Constant figures are at founding's prices by definition, so Prices has nothing to move.";
        if (!anySaved) return "Prices moves only your saved cities, and none is on the board.";
        return "";
    }

    /**
     * The line under the controls that names exactly what is on screen (spec-leaderboards 3: "the most important piece of the UI"): the money, the
     * way it was converted, and the board with the city's month. A measure that is not money drops the money clause and names its unit; a wage under
     * Constant says it has no constant series. path is the city's nations' path, or null.
     */
    public static String sentence(City c, Settings s, WorldMeasures.Measure m, Nations.Path path) {
        String when = CityCalendar.format(c.month());
        boolean constant = s.convert() == Convert.CONSTANT;
        StringBuilder b = new StringBuilder();
        if (m.money()) {
            String money = switch (s.money()) {
                case REAL_USD -> "Real US dollars at 2015 prices";
                case GAME_USD -> constant ? "Game US dollars at founding's prices" : "Game US dollars of " + when;
                case CITY -> c.currency() + (constant ? " at founding's prices" : " of " + when);
            };
            String how = switch (s.convert()) {
                case MARKET -> "at this month's exchange rates";
                case CONSTANT -> "at the constant 2015 rate, so only real growth moves a place";
                case PPP -> "at purchasing power parity";
            };
            b.append(money).append(", ").append(how).append(". ");
            if (constant && !hasConstantSeries(m)) b.append(capitalised(m.name())).append(" has no constant series, so it is at this month's rate. ");
        } else {
            b.append(capitalised(m.name())).append(" (").append(m.unit()).append("). ");
        }
        if (s.board() == Board.REAL_2025) {
            b.append("The real world in ").append(REAL_YEAR).append(", each figure its latest year since ").append(BASE_YEAR)
                    .append("; ").append(c.name()).append(" in ").append(when).append(".");
        } else {
            b.append(c.name()).append("'s world in ").append(when);
            if (path != null) b.append(", its nations on the ").append(path == Nations.Path.HISTORICAL ? "Historical" : "Random").append(" path");
            b.append(".");
        }
        return b.toString();
    }

    /**
     * sentence() with the saved cities named when there are any (LB3). The saved clause follows the board's sentence: "Your saved cities", then for a money
     * measure that Prices can move (not locked) per year "in their own year's dollars" or static "at Arden's prices of 2027" (the active city's), then "as last
     * saved" or, with atAge, "at 4 years 2 months" (the active city's age, ageWords). With none on the board it is sentence() unchanged.
     */
    public static String sentence(City c, Settings s, WorldMeasures.Measure m, Nations.Path path, List<Saved> saved, boolean atAge) {
        String base = sentence(c, s, m, path);
        if (saved == null || saved.isEmpty()) return base;
        StringBuilder b = new StringBuilder(base).append(" Your saved cities");
        if (m.money() && !pricesLocked(s, m, true)) {
            b.append(s.prices() == Prices.PER_YEAR ? " in their own year's dollars" : " at " + c.name() + "'s prices of " + c.year());
            b.append(',');
        }
        return b.append(atAge ? " at " + ageWords(c.month()) : " as last saved").append('.').toString();
    }

    /** A number of months as a person says an age: "4 years 2 months", "1 year", "7 months". */
    public static String ageWords(int months) {
        int years = Math.max(0, months) / 12, rest = Math.max(0, months) % 12;
        String y = years + (years == 1 ? " year" : " years"), mo = rest + (rest == 1 ? " month" : " months");
        return years == 0 ? mo : rest == 0 ? y : y + " " + mo;
    }

    private static String capitalised(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /* ---------------------------------------------------------------------
       A FIGURE, BEFORE IT IS A ROW
       --------------------------------------------------------------------- */

    /** A place's figure for the measure once pivoted: its value (US$ of 2015 for money), year, mark, source, rule and people. */
    private record Cell(double value, int year, int mark, String source, String rule, double people) { }

    private static boolean usable(double v) {
        return Double.isFinite(v);
    }

    /* ---------------------------------------------------------------------
       THE ACTIVE CITY
       --------------------------------------------------------------------- */

    /**
     * The city's figure for a measure, pivoted to US$ of 2015 (spec-leaderboards 3): a money figure X (the city's money, whole units) is X over the
     * rate over the game dollar's price level at this month's rate, X over parity over that level at PPP, and under Constant X at founding's prices
     * over the founding rate. Null where the city has no figure, or none at constant prices under Constant.
     */
    private static Cell cityCell(City c, WorldMeasures.Measure m, Settings eff) {
        CityMeasures.Figure f = c.figures().get(m.key());
        if (f == null) return null;
        double value = f.value();
        if (m.money()) {
            value = switch (eff.convert()) {
                case MARKET -> f.value() / c.fx() / c.usPrices();
                case PPP -> f.value() / c.parity() / c.usPrices();
                case CONSTANT -> f.real() / c.foundingRate();
            };
        }
        if (!usable(value)) return null;
        CityMeasures.Figure population = c.figures().get("people");
        return new Cell(value, c.year(), 0, SOURCE_CITY, null, population == null ? Double.NaN : population.value());
    }

    /* ---------------------------------------------------------------------
       A SAVED CITY (spec-leaderboards 3 and 5; LB3)
       --------------------------------------------------------------------- */

    /**
     * A saved city's figure for a measure, pivoted to US$ of 2015 as cityCell does but by ITS OWN rate, parity and founding rate, all in the units of its last
     * save (a reform rescales them with its money, so a reform never moves a rank). A money figure X is X over its rate over a price level at this month's
     * rate, X over its parity over that level at PPP, and under Constant its figure at founding's prices over its founding rate. The level is the saved
     * city's own P under static prices, so the figure is in real dollars; under per year (D2, the default) it is the ACTIVE city's P, so the figure the
     * display shows in game US$ is the saved city's own year's game dollars (X over its rate). Prices is read as static where pricesLocked says it is
     * greyed (real US$ and Constant are at fixed prices already), so Prices moves only a saved row, and never under those. Null where it has no figure.
     */
    private static Cell savedCell(Saved saved, City active, Settings s, WorldMeasures.Measure m, Settings eff) {
        City c = saved.city();
        CityMeasures.Figure f = c.figures().get(m.key());
        if (f == null) return null;
        double value = f.value();
        if (m.money()) {
            double level = s.prices() == Prices.PER_YEAR && !pricesLocked(s, m, true) ? active.usPrices() : c.usPrices();
            value = switch (eff.convert()) {
                case MARKET -> f.value() / c.fx() / level;
                case PPP -> f.value() / c.parity() / level;
                case CONSTANT -> f.real() / c.foundingRate();
            };
        }
        if (!usable(value)) return null;
        CityMeasures.Figure population = c.figures().get("people");
        return new Cell(value, CityCalendar.yearOf(c.month()), 0, SOURCE_SAVE, null, population == null ? Double.NaN : population.value());
    }

    /**
     * A city as a history stood at the end of game month `month` (LB3): its name, its money and its founding rate as given, its rate, parity and the game
     * dollar's price level from the history's fxRate, fxParity and usPrices at that month (NaN where the history did not record them, so a money figure
     * is then absent, never wrong), and its figures from CityMeasures.at with the extras given. Null when the history does not hold that month as a month.
     * The one place a City is built off a history: SavedCities at an age and rankOverYears both use it.
     */
    public static City cityAt(String name, String currency, double foundingRate, HistorySave h, CityMeasures.Extras extras, int month) {
        if (CityMeasures.entriesTo(h, month) < 0) return null;
        return new City(name, currency, month, CityCalendar.yearOf(month), CityMeasures.valueAt(h, "fxRate", month), CityMeasures.valueAt(h, "fxParity", month),
                CityMeasures.valueAt(h, "usPrices", month), foundingRate, CityMeasures.at(h, extras, month));
    }

    /**
     * The active city's rank on one measure at each December of its history (D12, LB3), against Real world 2025 only: its nations and regions, no saved
     * rows and no Your world, a board that holds still, at the money and conversion in s (the board in s is ignored). Each December is the city built
     * by cityAt with Extras.UNKNOWN (its land and bands are not in the history), ranked by table() exactly as the board ranks it; a December in a folded
     * year, or where the city has no figure, has no point. Oldest first. city supplies the name, the money and the founding rate.
     */
    public static List<RankPoint> rankOverYears(WorldMeasures wm, City city, HistorySave h, Settings s, String measure) {
        List<RankPoint> out = new ArrayList<>();
        if (h == null) return out;
        Settings real = new Settings(Board.REAL_2025, s.money(), s.prices(), s.convert());
        List<Integer> axis = h.getMonth();
        for (int i = h.yearlyPoints(); i < axis.size(); i++) {
            int month = axis.get(i);
            if (CityCalendar.monthOfYear(month) != 12) continue;
            City then = cityAt(city.name(), city.currency(), city.foundingRate(), h, CityMeasures.Extras.UNKNOWN, month);
            if (then == null) continue;
            Table t = table(wm, null, then, real, measure);
            if (t.you() >= 0) out.add(new RankPoint(month, t.rows().get(t.you()).rank(), t.rows().size()));
        }
        return out;
    }

    /* ---------------------------------------------------------------------
       THE REAL WORLD, 2025 (spec-leaderboards 3 and LB1-notes 5)
       --------------------------------------------------------------------- */

    /** A US dollar of the given year in 2015 dollars (CPI-U); NaN where the file has no CPI-U for the year. */
    private static double toBase(WorldMeasures wm, int year) {
        return wm.cpiU(BASE_YEAR) / wm.cpiU(year);
    }

    /** The lower of two marks, and the rule of the lower (the first on a tie). */
    private static Cell lowest(WorldMeasures.Figure primary, double value, WorldMeasures.Figure... also) {
        WorldMeasures.Figure low = primary;
        for (WorldMeasures.Figure f : also) if (f.mark() < low.mark()) low = f;
        return new Cell(value, primary.year(), low.mark(), primary.source(), low.mark() == 1 ? low.rule() : null, Double.NaN);
    }

    /**
     * A real place's figure, pivoted: current US$ of its year times CPI-U 2015 over CPI-U of that year (this month's rate); under Constant the
     * file's .k figure as is for gdp and gdp per person, and household spending's this-month's figure times gdp.k over gdp; under PPP the .ppp figure
     * times the same ratio, and household spending and wages times gdp.ppp over gdp. A place with a figure of the measure but not of the one it
     * is converted by has no figure here. Null where there is none.
     */
    private static Cell realCell(WorldMeasures wm, WorldMeasures.Place p, WorldMeasures.Measure m, Settings eff) {
        WorldMeasures.Figure f = p.figure(m.key());
        if (f == null) return null;
        double people = p.value("people");
        Cell c;
        if (!m.money()) {
            c = lowest(f, f.value());
        } else {
            String key = m.key();
            boolean own = key.equals("gdp") || key.equals("gdp_pp");
            switch (eff.convert()) {
                case MARKET -> c = lowest(f, f.value() * toBase(wm, f.year()));
                case CONSTANT -> {
                    if (own) {
                        WorldMeasures.Figure k = p.figure(key + ".k");
                        c = k == null ? null : lowest(k, k.value());
                    } else {
                        WorldMeasures.Figure gdp = p.figure("gdp"), constant = p.figure("gdp.k");
                        c = gdp == null || constant == null ? null : lowest(f, f.value() * toBase(wm, f.year()) * constant.value() / gdp.value(), gdp, constant);
                    }
                }
                case PPP -> {
                    if (own) {
                        WorldMeasures.Figure ppp = p.figure(key + ".ppp");
                        c = ppp == null ? null : lowest(ppp, ppp.value() * toBase(wm, ppp.year()));
                    } else {
                        WorldMeasures.Figure gdp = p.figure("gdp"), ppp = p.figure("gdp.ppp");
                        c = gdp == null || ppp == null ? null : lowest(f, f.value() * toBase(wm, f.year()) * ppp.value() / gdp.value(), gdp, ppp);
                    }
                }
                default -> c = null;
            }
        }
        if (c == null || !usable(c.value())) return null;
        return new Cell(c.value(), c.year(), c.mark(), c.source(), c.rule(), people);
    }

    /* ---------------------------------------------------------------------
       YOUR WORLD (spec-leaderboards 3 and 4)
       --------------------------------------------------------------------- */

    /** The nations of the city's own world at the city's year, and what the pivot needs of the US. */
    private static final class World {
        final WorldMeasures wm;
        final Nations nations;
        final int year, us;

        World(WorldMeasures wm, Nations nations, City city) {
            this.wm = wm;
            this.nations = nations;
            this.year = Math.max(Nations.FIRST_YEAR, Nations.yearOf(city.month()));
            this.us = nations.index(Nations.FRONTIER);
        }

        /** A nation's output at that year's prices and rate, into US$ of 2015 by the world's own US prices (spec-leaderboards 3, this month's rate). */
        double market(int i) {
            if (us < 0) return Double.NaN;
            Nations.Year now = nations.year(i, year), base = nations.year(i, BASE_YEAR);
            Nations.Year usNow = nations.year(us, year), usBase = nations.year(us, BASE_YEAR);
            return now.output() * (now.priceLevel() / base.priceLevel()) * (base.rateToUsd() / now.rateToUsd())
                    * (usBase.priceLevel() / usNow.priceLevel());
        }

        /** The nation's price level against the US at the city's year: the file's 2025 figure (none: 1) moved by relative prices and the rate since (D13). The US is 1. */
        double priceLevel(int i, WorldMeasures.Place file) {
            if (us < 0) return Double.NaN;
            if (i == us) return 1;
            WorldMeasures.Figure f = file == null ? null : file.figure("price_level");
            double at = f == null ? 1 : f.value();
            Nations.Year now = nations.year(i, year), then = nations.year(i, wm.year());
            Nations.Year usNow = nations.year(us, year), usThen = nations.year(us, wm.year());
            return at * (now.priceLevel() / then.priceLevel()) / (usNow.priceLevel() / usThen.priceLevel()) * (then.rateToUsd() / now.rateToUsd());
        }

        /** A nation's output in the converted money (US$ of 2015): this month's rate, the output as it is under Constant, or PPP, the market figure over the price level. */
        double output(int i, WorldMeasures.Place file, Settings eff) {
            return switch (eff.convert()) {
                case CONSTANT -> nations.year(i, year).output();
                case MARKET -> market(i);
                case PPP -> market(i) / priceLevel(i, file);
            };
        }

        /** The mark of that figure: the output's, and under PPP the lower of it and the price level's (a nation with none is an estimate). */
        int outputMark(int i, WorldMeasures.Place file, Settings eff) {
            int mark = nations.year(i, year).outputMark();
            if (eff.convert() != Convert.PPP || i == us) return mark;
            WorldMeasures.Figure f = file == null ? null : file.figure("price_level");
            return Math.min(mark, f == null ? 1 : f.mark());
        }

        /** A nation of the game's world as a cell for a measure, or null where it has no figure at that year. */
        Cell nation(int i, WorldMeasures.Place file, WorldMeasures.Measure m, Settings eff) {
            Nations.Year now = nations.year(i, year);
            boolean first = year <= Nations.FIRST_YEAR;
            double value;
            int mark = now.outputMark();
            WorldMeasures.Figure area = file == null ? null : file.figure("area");
            switch (m.key()) {
                case "people" -> { value = now.people(); mark = now.peopleMark(); }
                case "pop_growth" -> {
                    value = first ? Double.NaN : 100 * (now.people() / nations.year(i, year - 1).people() - 1);
                    mark = now.peopleMark();
                }
                case "gdp" -> { value = output(i, file, eff); mark = outputMark(i, file, eff); }
                case "gdp_pp" -> { value = output(i, file, eff) / now.people(); mark = outputMark(i, file, eff); }
                case "growth_real" -> value = first ? Double.NaN : 100 * (Math.exp(nations.growth(i, year)) - 1);
                case "growth_10y" -> value = year - CityMeasures.DECADE < Nations.FIRST_YEAR ? Double.NaN
                        : 100 * (Math.pow(now.output() / nations.year(i, year - CityMeasures.DECADE).output(), 1.0 / CityMeasures.DECADE) - 1);
                case "inflation" -> value = 100 * now.inflation();
                case "price_level" -> {
                    value = priceLevel(i, file);
                    WorldMeasures.Figure f = file == null ? null : file.figure("price_level");
                    mark = i == us ? mark : Math.min(mark, f == null ? 1 : f.mark());
                }
                case "area" -> { value = area == null ? Double.NaN : area.value(); mark = area == null ? 1 : area.mark(); }
                case "density" -> {
                    value = area == null ? Double.NaN : now.people() / area.value();
                    mark = Math.min(now.peopleMark(), area == null ? 1 : area.mark());
                }
                default -> value = Double.NaN;
            }
            if (!usable(value)) return null;
            return new Cell(value, year, mark, SOURCE_WORLD, null, now.people());
        }

        /**
         * A city region as a cell: its nation's path times its 2025 share of the nation's people and output, rates held, so every figure is an estimate
         * (spec-leaderboards 4). gdp is the nation's times the share of output; gdp per person only for a region whose 2025 entry has one (the output
         * share over the people share); people the nation's times the share of people; growth the nation's; area the region's own, held; density the
         * region's people over its area. Null for any other measure, or where the nation is not in this world.
         */
        Cell region(WorldMeasures.Place r, WorldMeasures.Measure m, Settings eff) {
            int i = nations.index(r.nation());
            if (i < 0 || r.sharePeople() == null || r.shareOutput() == null) return null;
            WorldMeasures.Place file = wm.place(r.nation());
            Nations.Year now = nations.year(i, year);
            double people = now.people() * r.sharePeople().value();
            double value;
            switch (m.key()) {
                case "gdp" -> value = output(i, file, eff) * r.shareOutput().value();
                case "gdp_pp" -> value = r.figure("gdp_pp") == null ? Double.NaN
                        : output(i, file, eff) * r.shareOutput().value() / people;
                case "people" -> value = people;
                case "pop_growth" -> value = year <= Nations.FIRST_YEAR ? Double.NaN : 100 * (now.people() / nations.year(i, year - 1).people() - 1);
                case "area" -> value = r.value("area");
                case "density" -> value = r.value("area") > 0 ? people / r.value("area") : Double.NaN;
                default -> value = Double.NaN;
            }
            if (!usable(value)) return null;
            return new Cell(value, year, 1, SOURCE_WORLD, rule(RULE_REGION), people);
        }

        private String rule(String id) {
            return wm.rules().containsKey(id) ? id : null;
        }
    }

    /* ---------------------------------------------------------------------
       THE TABLE (spec-leaderboards 4 and 9)
       --------------------------------------------------------------------- */

    /**
     * The ranked board of one measure. REAL_2025 ranks every place of the file with a figure, and the city; a measure nothing real publishes
     * (!sourced(), the four only the player's cities rank among them) has the city alone and realFigures false (D10). YOUR_WORLD ranks only the ten
     * measures it carries (the nations at the city's year, the city regions by share, the city) and is unranked, with no rows, for the other 60, or
     * with no Nations. Arrow DOWN ranks ascending, UP and NONE descending; equal values share a rank and the next rank skips; a place with no figure
     * is no row. Throws IllegalArgumentException for a key that is no measure.
     */
    public static Table table(WorldMeasures wm, Nations nations, City city, Settings s, String measure) {
        return table(wm, nations, city, List.of(), s, measure);
    }

    /**
     * table() with the saved cities beside the active one (LB3): each is a Kind.SAVED row ranked by the same function, pivoted by its own rate and price
     * level (savedCell) and shown in the active city's money, on any board that ranks the measure (Your world's ten, and Real world 2025's, where a measure
     * nothing real publishes has only the player's cities). A saved city with no figure of the measure is no row. The caller sets pricesLocked's anySaved
     * from !saved.isEmpty().
     */
    public static Table table(WorldMeasures wm, Nations nations, City city, List<Saved> saved, Settings s, String measure) {
        if (saved == null) saved = List.of();
        WorldMeasures.Measure m = wm.measure(measure);
        if (m == null) throw new IllegalArgumentException("rankings: no measure " + measure);
        Settings eff = effective(s, m);
        List<Row> rows = new ArrayList<>();
        boolean ranked = true, real = m.sourced();
        if (s.board() == Board.REAL_2025) {
            if (real) {
                for (WorldMeasures.Place p : wm.places()) {
                    Cell c = realCell(wm, p, m, eff);
                    if (c != null) rows.add(row(p.id(), p.kind() == WorldMeasures.Kind.NATION ? Kind.NATION : Kind.REGION, p.name(), p.nation(), c, m, eff, city));
                }
            }
        } else if (!m.yourWorld() || nations == null) {
            ranked = false;
        } else {
            World world = new World(wm, nations, city);
            for (int i = 1; i <= nations.count(); i++) {
                Cell c = world.nation(i, wm.place(nations.iso3(i)), m, eff);
                if (c != null) rows.add(row(nations.iso3(i), Kind.NATION, nations.name(i), null, c, m, eff, city));
            }
            for (WorldMeasures.Place r : wm.regions()) {
                Cell c = world.region(r, m, eff);
                if (c != null) rows.add(row(r.id(), Kind.REGION, r.name(), r.nation(), c, m, eff, city));
            }
        }
        if (ranked) {
            Cell c = cityCell(city, m, eff);
            if (c != null) rows.add(row(PLACE_CITY, Kind.CITY, city.name(), null, c, m, eff, city));
            for (Saved sv : saved) {
                Cell sc = savedCell(sv, city, s, m, eff);
                if (sc != null) rows.add(row(sv.id(), Kind.SAVED, sv.city().name(), null, sc, m, eff, city));
            }
        }
        return rank(m, s, rows, ranked, s.board() == Board.YOUR_WORLD && ranked || real);
    }

    private static Row row(String id, Kind kind, String name, String nation, Cell c, WorldMeasures.Measure m, Settings eff, City city) {
        double value = c.value() + 0.0;   // a -0.0 ties with 0.0
        return new Row(0, id, kind, name, nation, value, shown(value, eff, city, m) + 0.0, c.year(), c.mark(), c.source(), c.rule(), c.people());
    }

    /** Orders the rows (DOWN ascending, otherwise descending, ties by name), gives ties one rank and the next rank skips, and counts the marks. */
    private static Table rank(WorldMeasures.Measure m, Settings s, List<Row> unranked, boolean ranked, boolean realFigures) {
        Comparator<Row> order = (a, b) -> {
            int by = m.arrow() == WorldMeasures.Arrow.DOWN ? Double.compare(a.value(), b.value()) : Double.compare(b.value(), a.value());
            if (by != 0) return by;
            by = a.name().compareTo(b.name());
            return by != 0 ? by : a.id().compareTo(b.id());
        };
        List<Row> sorted = new ArrayList<>(unranked);
        sorted.sort(order);
        List<Row> rows = new ArrayList<>();
        int rank = 0, you = -1, estimates = 0, figures = 0;
        for (int i = 0; i < sorted.size(); i++) {
            Row r = sorted.get(i);
            if (i == 0 || !tied(r.value(), sorted.get(i - 1).value())) rank = i + 1;
            rows.add(new Row(rank, r.id(), r.kind(), r.name(), r.nation(), r.value(), r.shown(), r.year(), r.mark(), r.source(), r.rule(), r.people()));
            if (r.kind() == Kind.CITY) you = i;
            if (r.mark() > 0) figures++;
            if (r.mark() == 1) estimates++;
        }
        return new Table(m, s, Collections.unmodifiableList(rows), you, ranked, realFigures, estimates, figures);
    }

    /** Whether two figures, next to each other in rank order, are one figure: equal, or within TIE of each other. */
    static boolean tied(double a, double b) {
        return a == b || Math.abs(a - b) <= TIE * Math.max(Math.abs(a), Math.abs(b));
    }

    /** The rows Around you shows (spec-leaderboards 1, 11): the top three, then the city and six either side, none twice, in rank order; the first ten when the city is off the board. */
    public static List<Row> around(Table t) {
        List<Row> rows = t.rows();
        TreeSet<Integer> keep = new TreeSet<>();
        if (t.you() < 0) {
            for (int i = 0; i < Math.min(AROUND_OFF, rows.size()); i++) keep.add(i);
        } else {
            for (int i = 0; i < Math.min(AROUND_TOP, rows.size()); i++) keep.add(i);
            for (int i = Math.max(0, t.you() - AROUND_EITHER_SIDE); i <= Math.min(rows.size() - 1, t.you() + AROUND_EITHER_SIDE); i++) keep.add(i);
        }
        List<Row> out = new ArrayList<>();
        for (int i : keep) out.add(rows.get(i));
        return out;
    }

    /** Your size: the places with people within YOUR_SIZE_FACTOR times the city's, either way, and the city; empty when the city is off the board or its people are not known. */
    public static List<Row> yourSize(Table t) {
        List<Row> out = new ArrayList<>();
        if (t.you() < 0) return out;
        double mine = t.rows().get(t.you()).people();
        if (!(mine > 0)) return out;
        for (Row r : t.rows()) {
            if (r.kind() == Kind.CITY || (r.people() >= mine / YOUR_SIZE_FACTOR && r.people() <= mine * YOUR_SIZE_FACTOR)) out.add(r);
        }
        return out;
    }

    /* ---------------------------------------------------------------------
       THE DRAWER (spec-leaderboards 7: opening a row)
       --------------------------------------------------------------------- */

    /**
     * Every measure for one place: placeId is a nation's or a region's id in the file, or PLACE_CITY for the active city. Each line has the figure on the
     * real board (with its mark and source) and on Your world (NaN where that board does not rank the measure or the place has none), both shown in the
     * money and conversion picked. A measure the place has no figure for is a line of NaN; the city's marks are 0.
     */
    public static List<Line> drawer(WorldMeasures wm, Nations nations, City city, Settings s, String placeId) {
        return drawer(wm, nations, city, List.of(), s, placeId);
    }

    /**
     * drawer() that also opens a saved city (LB3): placeId is then its row's id, "saved:" + slot (Saved.id()), and its lines are its own figures pivoted by
     * its own rate and price level (savedCell), on both boards as the active city's are, shown in the active city's money. A placeId that names no saved
     * city in the list and no place is a list of NaN lines, as drawer() has it.
     */
    public static List<Line> drawer(WorldMeasures wm, Nations nations, City city, List<Saved> saved, Settings s, String placeId) {
        if (saved == null) saved = List.of();
        World world = nations == null ? null : new World(wm, nations, city);
        WorldMeasures.Place place = PLACE_CITY.equals(placeId) ? null : wm.place(placeId);
        Saved opened = null;
        for (Saved sv : saved) if (sv.id().equals(placeId)) opened = sv;
        List<Line> out = new ArrayList<>();
        for (WorldMeasures.Measure m : wm.measures()) {
            Settings eff = effective(s, m);
            Cell real = null, mine = null;
            if (PLACE_CITY.equals(placeId)) {
                real = cityCell(city, m, eff);
                mine = world != null && m.yourWorld() ? real : null;
            } else if (opened != null) {
                real = savedCell(opened, city, s, m, eff);
                mine = world != null && m.yourWorld() ? real : null;
            } else if (place != null) {
                if (m.sourced()) real = realCell(wm, place, m, eff);
                if (world != null && m.yourWorld()) {
                    mine = place.kind() == WorldMeasures.Kind.REGION ? world.region(place, m, eff)
                            : nations.index(place.id()) < 0 ? null : world.nation(nations.index(place.id()), place, m, eff);
                }
            }
            out.add(new Line(m, real == null ? Double.NaN : shown(real.value(), eff, city, m), real == null ? 0 : real.mark(), real == null ? null : real.source(),
                    mine == null ? Double.NaN : shown(mine.value(), eff, city, m), mine == null ? 0 : mine.mark()));
        }
        return out;
    }
}
