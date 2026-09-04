package ham.citybuildersim;

/**
 * The six parts of the economy the player can set policy for, as one type.
 *
 * WHY THIS EXISTS
 *
 * The same six sectors were already being named three different ways, and
 * nothing tied the three together:
 *
 *   - BusinessDebtManager.SECTORS - Strings, used for credit and for the loss
 *     counter that decides whether a sector downsizes
 *   - BuildingType             - an enum, used to assess property tax
 *   - one handler each         - CommercialHandler covers TWO of them
 *
 * A policy screen has to address all three at once: set Mining's profit tax
 * (handler), its property tax (BuildingType) and its subsidy (credit String).
 * Doing that with string literals and a switch in every caller is how the wrong
 * sector gets charged, and nothing would catch it - RETAIL and COMMERCIAL are
 * the same sector under two names, so a mix-up type-checks perfectly.
 *
 * ONE CAVEAT WORTH KNOWING
 *
 * RETAIL and REAL_ESTATE both live inside CommercialHandler. They are separate
 * sectors everywhere else - separate books, separate credit, separate property
 * tax - so they are separate here too, and the handler is asked for each of them
 * by name rather than as a whole.
 */
public enum PolicySector {

    RETAIL        ("Retail",         BuildingType.COMMERCIAL),
    REAL_ESTATE   ("Real Estate",    BuildingType.RESIDENTIAL),
    INDUSTRY      ("Industry",       BuildingType.INDUSTRIAL),
    CONSTRUCTION  ("Construction",   BuildingType.CONSTRUCTION),
    HEAVY_INDUSTRY("Heavy Industry", BuildingType.HEAVY_INDUSTRY),
    MINING        ("Mining",         BuildingType.MINING);

    private final String creditName;
    private final BuildingType category;

    PolicySector(String creditName, BuildingType category) {
        this.creditName = creditName;
        this.category = category;
    }

    /** The name BusinessDebtManager and the loss counter know it by. */
    public String creditName() { return creditName; }

    /** The building category its property tax is assessed on. */
    public BuildingType category() { return category; }

    /** For screens. */
    public String label() { return creditName; }

    /**
     * The sector a credit-side name belongs to, or null.
     *
     * Deliberately returns null rather than throwing: it is called with names
     * that come out of saved games, and a save from a build with a sector this
     * one does not have should lose that sector's policy, not the whole load.
     */
    public static PolicySector byCreditName(String name) {
        for (PolicySector s : values()) {
            if (s.creditName.equals(name)) return s;
        }
        return null;
    }

    /** The sector that owns a building category, or null for city-owned ones. */
    public static PolicySector byCategory(BuildingType category) {
        for (PolicySector s : values()) {
            if (s.category == category) return s;
        }
        return null;
    }
}
