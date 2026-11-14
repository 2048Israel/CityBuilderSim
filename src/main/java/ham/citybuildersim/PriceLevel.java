package ham.citybuildersim;

/**
 * The price level a city is founded at, 1900's: the US consumer price index of the calendar's first year over its 2025, and the series it is
 * read from, the US CPI-U a year from 1900 to 2025 (EP1; docs/studies/spec-1900-start.md 1 and its calls 1 and 3).
 *
 * WHY THIS EXISTS. The game opened in January 2000 at today's prices. From 1900 (Jerus, 2026-10-30: "1900 price level") every nominal anchor -
 * the world's prices, the wage rungs, the building costs, the opening money - is today's real economy priced at 1900's level, so the game's
 * own inflation compounds from there and reaches today's prices about when history did. One factor reaches them all (FACTOR, read where each
 * anchor is struck: PayTier, Good, BuildingCatalog and the named literals), and it comes from the same series the world's US dollar follows
 * before the world file's own years (WorldData's stand-in century), so the dollar the world inflates on and the factor that set the opening
 * prices are one dollar.
 *
 * THE SOURCES. 1913-2025: the US Bureau of Labor Statistics, CPI-U, all items, US city average, series CUUR0000SA0, 1982-84 = 100, the
 * annual average (US public domain); 2025 is the mean of the 11 months published (October 2025 was not collected). 1900-1912 (est.): Albert
 * Rees's cost-of-living index as the BLS compiled it, Historical Statistics of the United States, Colonial Times to 1970 (US Census Bureau,
 * 1975), series E 135, 1967 = 100 in whole numbers (US public domain; licences.md, LC1), joined at 1913 (BLS 9.9 over E 135's 29.7: divided
 * by 3). The table is docs/studies/1900-start/price-level-us.csv.
 */
public final class PriceLevel {

    /** The series' first year, the calendar's (CityCalendar.EPOCH_YEAR). */
    public static final int FIRST_YEAR = 1900;

    /** The series' last year, the year the game's literals are priced in (WorldMarket.REFERENCE_YEAR). */
    public static final int LAST_YEAR = 2025;

    /** The first year the BLS published the index; the years before it are Rees's index joined at it (est.). */
    public static final int FIRST_PUBLISHED_YEAR = 1913;

    /** The US CPI-U a year, 1900 to 2025, 1982-84 = 100 (the class's sources). */
    private static final double[] US_CPI = {
        8.333, 8.333, 8.667, 9.000, 9.000, 9.000, 9.000, 9.333, 9.000, 9.000,                 // 1900-1909
        9.333, 9.333, 9.667, 9.9, 10.0, 10.1, 10.9, 12.8, 15.0, 17.3,                         // 1910-1919
        20.0, 17.8, 16.8, 17.1, 17.1, 17.5, 17.7, 17.4, 17.2, 17.2,                           // 1920-1929
        16.7, 15.2, 13.6, 12.9, 13.4, 13.7, 13.9, 14.4, 14.1, 13.9,                           // 1930-1939
        14.0, 14.7, 16.3, 17.3, 17.6, 18.0, 19.5, 22.3, 24.0, 23.8,                           // 1940-1949
        24.1, 26.0, 26.6, 26.8, 26.8, 26.8, 27.2, 28.1, 28.9, 29.2,                           // 1950-1959
        29.6, 29.9, 30.2, 30.6, 31.0, 31.5, 32.5, 33.4, 34.8, 36.7,                           // 1960-1969
        38.8, 40.5, 41.8, 44.4, 49.3, 53.8, 56.9, 60.6, 65.2, 72.6,                           // 1970-1979
        82.4, 90.9, 96.5, 99.6, 103.9, 107.6, 109.6, 113.6, 118.3, 124.0,                     // 1980-1989
        130.7, 136.2, 140.3, 144.5, 148.2, 152.4, 156.8, 160.5, 163.0, 166.6,                 // 1990-1999
        172.2, 177.1, 179.9, 184.0, 188.9, 195.3, 201.6, 207.342, 215.303, 214.537,           // 2000-2009
        218.056, 224.939, 229.594, 232.957, 236.736, 237.017, 240.007, 245.120, 251.107, 255.657, // 2010-2019
        258.811, 270.970, 292.655, 304.702, 313.689, 321.943                                  // 2020-2025
    };

    /**
     * Every nominal anchor's multiplier, the US CPI of the calendar's first year over its 2025: 8.333 / 321.943 = 0.02588, 1/38.6 (call 1).
     * The property cbs.priceLevel stands in for it in a JVM a harness starts (PriceLevelCheck's twin, founded at today's prices); nothing else sets it.
     */
    public static final double FACTOR = factor();

    /** The calendar's actual US CPI ratio, independent of a counterfactual price-level override. */
    public static final double HISTORICAL_FACTOR = usCpi(CityCalendar.EPOCH_YEAR) / usCpi(LAST_YEAR);

    private PriceLevel() { }

    private static double factor() {
        String forced = System.getProperty("cbs.priceLevel");
        if (forced != null && !forced.isBlank()) {
            double v = Double.parseDouble(forced.trim());
            if (!(v > 0) || !Double.isFinite(v)) throw new IllegalArgumentException("cbs.priceLevel must be a positive number: " + forced);
            return v;
        }
        return usCpi(CityCalendar.EPOCH_YEAR) / usCpi(LAST_YEAR);
    }

    /** The US CPI-U in a year, FIRST_YEAR to LAST_YEAR, 1982-84 = 100; refused outside them. */
    public static double usCpi(int year) {
        if (year < FIRST_YEAR || year > LAST_YEAR) throw new IllegalArgumentException("the US price series runs " + FIRST_YEAR + " to " + LAST_YEAR + ", not " + year);
        return US_CPI[year - FIRST_YEAR];
    }

    /** Whether that year's figure is the BLS's own (true) or Rees's joined at 1913 (false, est.). */
    public static boolean isPublished(int year) {
        return year >= FIRST_PUBLISHED_YEAR;
    }

    /** A nominal amount priced at today's (2025's) level, priced at the founding's: amount times FACTOR. */
    public static double atFounding(double today) {
        return today * FACTOR;
    }
}
