package ham.citybuildersim;

/**
 * Children no family holds, by age band.
 *
 * Found designing the unemployed households: FamilyModel's builder takes a
 * half share of each shape in turn and only three shapes hold a baby and three
 * a teen, so about one child in seven is in no household in every city - fed
 * by nobody, charged nothing, in no cell. Jerus, shown it: "they're the
 * orphans," and before that, of children with no family: "they dont even get
 * ei, and its named the orphan section, yes they get sick and die for now."
 *
 * So a ledger with nothing in it. No income, no earner, no credit; they go
 * without, which the hunger measure reads, and their band's mortality is
 * struck at zero care coverage - see Game.advanceDemographics(). Nobody's
 * money follows them: they carry no grown-ups.
 */
public class OrphanHousehold extends Household {

    private final AgeBand band;

    public OrphanHousehold(AgeBand band) {
        super(null);
        this.band = band;
    }

    public AgeBand band() { return band; }

    @Override public PayTier tier()      { return null; }
    @Override public int row()           { return ORPHAN_ROW; }
    @Override public boolean isRetired() { return false; }
    @Override public int grownUps()      { return 0; }
    @Override public int size()          { return 1; }

    @Override public String label() { return "Orphans, " + band.getLabel().toLowerCase(); }
    @Override public String key()   { return "ORPHAN:" + band.name(); }

    @Override double creditRoom(double disposablePer) { return 0; }
}
