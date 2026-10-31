package ham.citybuildersim;

/**
 * Just enough of a save to label it on the slot list.
 *
 * WHY THIS IS NOT A SEPARATE FILE
 *
 * The obvious way to show "Month 213 - 1,240 people" on a menu is to write a
 * small .meta file beside each save. That is two files per slot which have to
 * agree, and the day they stop agreeing the menu lies about what is in a slot -
 * which is the one thing a save menu must never do.
 *
 * So this parses the save itself. gson fills whichever fields it recognises and
 * ignores the rest, so these field names are deliberately IDENTICAL to the ones
 * in DataSave: the same JSON deserialises into either class, and the header can
 * never describe a different city from the file it came from.
 *
 * The cost is reading the whole file to show one line. At roughly 15 KB a save
 * and eleven slots, that is nothing.
 */
public class SaveHeader {

    /* Field names must match DataSave exactly - see above. */
    private int month;
    private long population;
    private double cash;

    private String slotName;
    private String gameVersion;
    private int saveFormat;
    private long savedAt;

    /** The city's name (0.7.10), DataSave's founding record's; absent on an older save. */
    private String cityName;

    /*
     * What the Rankings board reads of a saved city beside its history (LB3, SavedCities): the money's name, the world's
     * prices, the money's unit, and the land and the people by age band as they stood at the last save. Raw, as saved: the
     * class that owns each decodes it (WorldEconomy.restore, Denomination.restore, PopulationCohorts.restore), never an
     * index here.
     */
    private String currencyPlural;
    private double[] worldEconomy;
    private double[] denomination;
    private double[] cohorts;
    private String[] bandNames;
    /** The city's dry ground in square feet (DataSave.landOwned); 0 on a save from before land existed. */
    private double landOwned;

    public int getMonth()          { return month; }
    public long getPopulation()    { return population; }
    public double getCash()        { return cash; }
    public String getSlotName()    { return slotName; }
    public String getGameVersion() { return gameVersion; }
    public int getSaveFormat()     { return saveFormat; }
    public long getSavedAt()       { return savedAt; }

    /** The city's money in the plural ("Arden dollars"), or null on a save that does not carry it. */
    public String getCurrencyPlural() { return currencyPlural; }
    /** WorldEconomy.toSaveArray(), or null: WorldEconomy.restore reads it. */
    public double[] getWorldEconomy() { return worldEconomy; }
    /** Denomination.toSaveArray() (the unit and the reforms), or null: Denomination.restore reads it. */
    public double[] getDenomination() { return denomination; }
    /** The pyramid, PopulationCohorts.toSaveArray(), or null: PopulationCohorts.restore reads it with getBandNames(). */
    public double[] getCohorts()      { return cohorts; }
    /** The age bands the cohorts were written with, or null on a save from before they travelled. */
    public String[] getBandNames()    { return bandNames; }
    /** The city's dry ground in square feet as last saved; 0 when the save does not know. */
    public double getLandOwned()      { return landOwned; }

    public boolean hasName() {
        return slotName != null && !slotName.isBlank();
    }

    /**
     * The city in the slot, by name - for the slot list. A save from before
     * 0.7.10 has none and is the city every save was, Danzik, which is what it
     * loads as (Founding.legacy()), so the list and the load agree.
     */
    public String getCityName() {
        return cityName == null || cityName.isBlank() ? Founding.DEFAULT_CITY_NAME : cityName;
    }

    /** True when this file was written by a build that knows more than this one. */
    public boolean isFromNewerBuild() {
        return GameVersion.isFromNewerBuild(saveFormat);
    }

    /**
     * True when this file is older than this build loads, below
     * GameVersion.OLDEST_LOADABLE_FORMAT (0.7.108; until then, before the
     * sector template only) - refused as a newer build's file is.
     */
    public boolean isNoLongerReadable() {
        return GameVersion.isNoLongerReadable(saveFormat);
    }

    /** Loadable by this build: neither from a newer one nor older than it reads. */
    public boolean isReadableHere() {
        return !isFromNewerBuild() && !isNoLongerReadable();
    }
}
