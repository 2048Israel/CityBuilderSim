package ham.citybuildersim;

import ham.citybuildersim.sectors.ExtractionSector;
import java.util.ArrayList;
import java.util.List;

/**
 * The land office's Ore card as the model reads it (batch I13-L): for each ore
 * the city's ground holds, its deposits, the tonnes still in the ground, the
 * sites the mines on it take and whether ore lies undug - and the industry
 * that digs it, which the card's tooltip and click go to.
 *
 * WHY. The card was iron's alone: ui/LandScreen.ore() built its figures from
 * iron, its tooltip said "Iron mines are built under Build > Industry", and
 * "ore that nothing is digging" was iron's flag (I6's note, carried by I13).
 * The materials batches then gave the city coal, copper and stone to dig
 * (ExtractionSector, one sector each), so a city could own a coal field no
 * mine stood on and the card said nothing - while the sector's own page
 * (ExtractionSector.ownLines()) already did. The card's figures are read here,
 * from the same getters that page reads (LandManager.getSites(),
 * getRemaining(), Game.committedOn()), so the two cannot disagree and the
 * screen computes nothing (CLAUDE.md, "A screen section"). LandCheck 30 holds it.
 *
 * Oil has its own line on the card (a well is not a mine) and the forest is not
 * in fields: neither is here. Iron is always the first row, as it was the
 * card's headline even with no deposit; the others only where the city owns a
 * site of them.
 */
public final class OreView {

    private OreView() { }

    /**
     * One ore.
     *
     * @param resource the ore
     * @param words    what it is called in a sentence: "iron ore", "coal", "copper ore", "stone" (ExtractionSector.oreWords())
     * @param sector   the key of the sector that digs it (Sectors.MINING for iron), or null where none does (uranium)
     * @param deposits the sites of it the city owns (LandManager.getSites())
     * @param tonnes   what is still in its ground, in tonnes (LandManager.getRemaining())
     * @param mines    the sites the mines on it take, standing, on site or ordered (Game.committedOn())
     * @param undug    whether a deposit has no mine on it: fewer sites taken than owned - the page's flag (ExtractionSector.ownLines())
     */
    public record Ore(Resource resource, String words, String sector, int deposits, double tonnes, int mines, boolean undug) {

        /** Whether an industry digs it at all: the card's click goes to its page. */
        public boolean hasDigger() { return sector != null; }
    }

    /** The ores the card lists, in Resource's order: iron always, then each of stone, coal, copper and uranium the city owns a site of. */
    public static List<Ore> ores(Game game) {
        List<Ore> out = new ArrayList<>();
        LandManager land = game.getLandManager();
        for (Resource r : Resource.values()) {
            if (r == Resource.OIL || !r.inFields()) continue;
            int deposits = land.getSites(r);
            if (r != Resource.IRON && deposits <= 0) continue;
            int mines = game.committedOn(r);
            out.add(new Ore(r, ExtractionSector.oreWords(r, null), diggerOf(game, r), deposits, land.getRemaining(r), mines, mines < deposits));
        }
        return out;
    }

    /** The key of the extraction sector whose resource this is, or null. */
    public static String diggerOf(Game game, Resource r) {
        for (Sector s : game.getSectors().all()) {
            if (s instanceof ExtractionSector e && e.resource() == r) return s.key();
        }
        return null;
    }
}
