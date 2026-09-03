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
    private int population;
    private double cash;

    private String slotName;
    private String gameVersion;
    private int saveFormat;
    private long savedAt;

    public int getMonth()          { return month; }
    public int getPopulation()     { return population; }
    public double getCash()        { return cash; }
    public String getSlotName()    { return slotName; }
    public String getGameVersion() { return gameVersion; }
    public int getSaveFormat()     { return saveFormat; }
    public long getSavedAt()       { return savedAt; }

    public boolean hasName() {
        return slotName != null && !slotName.isBlank();
    }

    /** True when this file was written by a build that knows more than this one. */
    public boolean isFromNewerBuild() {
        return GameVersion.isFromNewerBuild(saveFormat);
    }
}
