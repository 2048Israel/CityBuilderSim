package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * What the screens and the investors' words say of the city's landmasses: pure functions of the model's figures, so that
 * the words are written in one place and a harness can rebuild them from getters (ET5, 2026-10-25).
 *
 * WHY THIS EXISTS. A screen must not recompute a model figure, and a refusal the investors write ("no land - needs ...")
 * and the Build card's gate must say the same thing about ground across the water in the same words. LandClassView is
 * the shape: a pure class beside the model that the interface calls. Slice A of ET5 put here the two things the model's
 * own words and the pickers need - the investors' clause and the ISLAND tag's share; slice D adds the rest, so that
 * every sentence the interface writes about a landmass is here and ham.citybuildersim.LandmassCheck (section 11) can
 * hold each of them without the toolkit: the LANDMASSES table's heads, widths and rows, an offer's line, the strip's
 * lines, the map's chip, hover and legend, the notice a landmass raises and its button, the port card's three link
 * sentences, the receipt's clause, the refusals' words and the playtest's report line. Every figure is a model getter's
 * (Landmasses, LandManager, LandParcel); nothing here is worked out from a screen's own state. While the city owns
 * ground on one landmass nothing here has anything to say, and each function answers "" or null or false.
 *
 * @author Jerus
 */
public final class LandmassView {

    private LandmassView() { }

    /** The share of an offer's dry ground that must lie on landmasses the city has not linked for the land office to tag the offer ISLAND (before BEST VALUE and the rest): half. */
    public static final double ISLAND_TAG_SHARE = 0.5;

    /** Whether the land office tags an offer ISLAND: it has dry ground on a landmass the city has not linked, and at least ISLAND_TAG_SHARE of its dry ground is such. False for every offer of home's ground and on every generated world. */
    public static boolean islandTagged(LandParcel p) {
        return p.getUnlinkedDryKm2() > 0 && p.getUnlinkedDryKm2() >= ISLAND_TAG_SHARE * p.getDryKm2();
    }

    /**
     * The clause the investors' land refusals end with when the city owns ground it cannot build on: "12.0 km² is free on
     * Island 2, which no port links". `freeSqFt` is LandManager.getUnlinkedFreeSqFt(), `names` LandManager.getUnlinkedNames().
     * BusinessInvestment.landReason() and Game's "Could not build" words join it to theirs with "; ".
     */
    public static String unlinkedWords(double freeSqFt, String names) {
        return LandManager.areaWords(freeSqFt) + " is free on " + names + ", which no port links";
    }

    /* =====================================================================
       NAMES AND SMALL WORDS (ET5 slice D)
       ===================================================================== */

    /** The tag the land office puts on an offer that is mostly ground no port links. */
    public static final String ISLAND_TAG = "ISLAND";

    /** The words for ground on a landmass no port links, as a clause: "which no port links". */
    public static final String NOT_LINKED_CLAUSE = "which no port links";

    /** The map's legend entry for the hatch over ground no port links. */
    public static final String LEGEND_WORDS = "Not linked: only a port";

    /** What only a port can do on ground no port links, as a sentence's end: "only a port can be built there until it has one". */
    public static final String ONLY_A_PORT = "only a port can be built there until it has one";

    /**
     * A landmass's name for a sentence: "Home", the data's name as the founding card writes it, "Island 2" - from the
     * rows the city owns - and "another landmass" for one it owns nothing on (an offer's ground the city has not bought).
     */
    public static String landmassName(Landmasses lms, int id) {
        if (lms == null) return "another landmass";
        if (id == 0 || id == lms.home()) return lms.name(id);
        for (Landmasses.Row r : lms.rows()) if (r.id() == id) return r.name();
        return "another landmass";
    }

    /** Whether a landmass has a port standing on it: some terminal of any kind (Landmasses.portsOn()). */
    public static boolean hasPort(Landmasses lms, int id) {
        long n = 0;
        for (long v : lms.portsOn(id)) n += v;
        return n > 0;
    }

    /** The terminals standing on a landmass, all kinds together. */
    public static long terminalsOn(Landmasses lms, int id) {
        long n = 0;
        for (long v : lms.portsOn(id)) n += v;
        return n;
    }

    /**
     * The linked landmass that has a port standing - what makes a port on another landmass link it (Landmasses.closure()) -
     * named for a sentence: "home" for home's, else the island's name; null when no linked landmass has one.
     */
    public static String linkedPortName(Landmasses lms) {
        for (Landmasses.Row r : lms.rows()) {
            if (r.linked() && hasPort(lms, r.id())) return r.home() ? "home" : r.name();
        }
        return null;
    }

    /** "2nd", "3rd", "4th", "11th", "21st": a count as an ordinal. */
    public static String ordinal(int n) {
        int tens = n % 100;
        String suffix = tens >= 11 && tens <= 13 ? "th" : switch (n % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
        return n + suffix;
    }

    /* =====================================================================
       THE LANDMASSES TABLE (the land office, between THE GROUND and WHAT THE GROUND IS WORTH)
       ===================================================================== */

    /** The table's section head. */
    public static final String TABLE_TITLE = "LANDMASSES";

    /** The table's hint, at the right of its head. */
    public static final String TABLE_HINT = "where the city's ground is · only a port can be built where none links";

    /** The table is drawn for a city that owns ground on this many landmasses or more (home counts): 2. */
    public static final int TABLE_MIN_ROWS = 2;

    /** The table's heads, in its columns' order. */
    public static final String[] HEADS = { "landmass", "dry owned", "free", "ports", "linked" };

    /** The columns' widths in pixels (the first, 0, takes the rest). */
    public static final double[] WIDTHS = { 0, 92, 230, 190, 290 };

    /** Which columns' cells lie to the right. */
    public static final boolean[] RIGHT = { false, true, false, false, false };

    /** A cell's tone: the screen maps it to its colour. */
    public enum Tone { PLAIN, GOOD, WARN, MUTED }

    /**
     * One landmass as a table row, worded.
     *
     * @param id         the landmass
     * @param name       "Home", "Island 2"
     * @param dry        its dry ground the city owns, "3.10 km²"
     * @param free       its free ground: linked rows share the linked pool's figure, an unlinked row says its own unbuilt ground and "only a port"
     * @param freeTone   the free cell's tone
     * @param ports      the terminals standing on it by kind, "1 general cargo", or "none"
     * @param linked     "home", "yes", or why not: "no: needs a port here (home has one)"
     * @param linkedTone the linked cell's tone
     * @param tip        the row's tooltip
     */
    public record Line(int id, String name, String dry, String free, Tone freeTone, String ports, String linked,
                       Tone linkedTone, String tip) { }

    /** Whether the table is drawn: the city owns ground on TABLE_MIN_ROWS landmasses or more. */
    public static boolean shown(Landmasses lms) {
        return lms != null && lms.rows().size() >= TABLE_MIN_ROWS;
    }

    /**
     * The table's rows, home first: from the landmasses' own rows (Landmasses.rows()) and the free ground the strip
     * shows (LandManager.getAvailableSqFt()), the linked landmasses sharing that one figure.
     */
    public static List<Line> rows(LandManager land) {
        return rows(land, id -> null);
    }

    /** ...with what links each island that is not linked (FB13): `links` is BuildAdvice.islandLink() by the landmass's id, and the "linked" cell and the tip say the bridge where it is allowed. */
    public static List<Line> rows(LandManager land, java.util.function.IntFunction<BuildAdvice.Link> links) {
        Landmasses lms = land.getLandmasses();
        List<Line> out = new ArrayList<>();
        double linkedFree = land.getAvailableSqFt();
        for (Landmasses.Row r : lms.rows()) {
            String dry = LandManager.areaWords(r.drySqFt());
            boolean linked = r.linked();
            String free = linked
                    ? LandManager.areaWords(linkedFree) + " free on linked ground"
                    : LandManager.areaWords(lms.unlinkedFreeSqFt(r.id())) + " free · only a port";
            BuildAdvice.Link link = linked ? null : links.apply(r.id());
            String linkedWords = linked ? (r.home() ? "home" : "yes") : needsWords(lms, r.id(), link);
            // A bridge links it as a port does (FB13): the tip's "until a port links it" says "until it is linked" where one is allowed or on site.
            String until = link != null && (link.bridge() != null || link.underWay()) ? "until it is linked" : "until a port links it";
            String tip = linked
                    ? r.name() + ": " + dry + " of dry ground owned, room for any building; its free ground is the linked ground's, one figure."
                    : r.name() + ": " + dry + " of dry ground owned that no port links. It is owned, counted and priced as ever, but no building"
                            + " may stand on it except a sea terminal, and no works on its fields, " + until + " (" + linkedWords.substring("no: ".length()) + ").";
            out.add(new Line(r.id(), r.name(), dry, free, linked ? Tone.PLAIN : Tone.WARN, portsWords(lms, r.id()), linkedWords,
                    linked ? Tone.GOOD : Tone.WARN, tip));
        }
        return out;
    }

    /** The terminals standing on a landmass by kind, "1 general cargo, 2 containers", or "none". */
    public static String portsWords(Landmasses lms, int id) {
        long[] by = lms.portsOn(id);
        StringBuilder s = new StringBuilder();
        Ports.Cargo[] kinds = Ports.Cargo.values();
        for (int i = 0; i < by.length && i < kinds.length; i++) {
            if (by[i] <= 0) continue;
            if (s.length() > 0) s.append(", ");
            s.append(by[i]).append(' ').append(kinds[i].label().toLowerCase(Locale.ROOT));
        }
        return s.length() == 0 ? "none" : s.toString();
    }

    /**
     * Why a landmass is not linked, as the table's cell and the map's chip say it: "no: needs a port here (home has one)"
     * where a linked landmass has a port already, "no: needs a port here and one at home" where none has, "no: it has a
     * port, and needs one at home" where it has its own and none is linked. A port links a landmass once it stands and
     * some linked landmass has one standing too (Landmasses.closure()).
     */
    public static String needsWords(Landmasses lms, int id) {
        return needsWords(lms, id, null);
    }

    /**
     * ...and, where the rule would link the island by a bridge (FB13, AL1-W), the bridge first and the port after it: "no: needs a Road Bridge
     * (420 m) or a port here (home has one)"; "no: a bridge is being built to it" where one is on site. `link` is BuildAdvice.islandLink()'s
     * answer for the island, null for none - and for a link by port or none at all the words are the old ones.
     */
    public static String needsWords(Landmasses lms, int id, BuildAdvice.Link link) {
        return "no: " + String.join(" ", needsParts(lms, id, link));
    }

    /** The words that say what links a landmass, without the "no: ", in the pieces the map's chip breaks them at: the bridge, then "or" the port; a lone piece for the rest. */
    private static String[] needsParts(Landmasses lms, int id, BuildAdvice.Link link) {
        if (hasPort(lms, id)) return new String[] { "it has a port, and needs one at home" };
        if (link != null && link.underWay()) return new String[] { "a bridge is being built to it" };
        String linkedPort = linkedPortName(lms);
        String port = linkedPort != null ? "a port here (" + linkedPort + " has one)" : "a port here and one at home";
        if (link != null && link.bridge() != null) return new String[] { "needs " + bridgeWords(link), "or " + port };
        return new String[] { "needs " + port };
    }

    /** A bridge as the words that offer it name it: "a Road Bridge (420 m)", the template the rule would order and the length its survey found (BridgeView.lengthWords). */
    public static String bridgeWords(BuildAdvice.Link link) {
        return "a " + link.bridge().getName() + " (" + BridgeView.lengthWords(link.survey().lengthM()) + ")";
    }

    /* =====================================================================
       THE OFFERS, THE STRIP AND THE RECEIPT
       ===================================================================== */

    /**
     * The line the land office writes under an offer that has dry ground on a landmass no port links, beside
     * LandClassView.offerLine(): "On Island 2, which no port links: only a port can be built there until it has one" for
     * an offer wholly on it, "0.842 km² of its dry ground is on Island 2, ..." for one that is partly. "" for an offer of
     * ground the city can build on, and for every offer on a generated world.
     */
    public static String offerLine(LandParcel p, Landmasses lms) {
        double unlinked = p.getUnlinkedDryKm2();
        if (!(unlinked > 0)) return "";
        String name = landmassName(lms, p.getUnlinkedLargest());
        boolean whole = unlinked >= p.getDryKm2() - 1e-9;
        return (whole ? "On " + name : LandMap.area(unlinked) + " of its dry ground is on " + name) + ", " + NOT_LINKED_CLAUSE + ": " + ONLY_A_PORT;
    }

    /**
     * ...for the map card of several picks (offerLine() is one offer's): "0.842 km² of their dry ground is on Island 2, which
     * no port links: only a port can be built there until it has one", or "Their dry ground is on ..." where all of it is.
     * The landmasses are those each pick has the most unlinked dry ground on. "" while none of the picks has any.
     */
    public static String picksLine(List<LandParcel> picks, Landmasses lms) {
        double unlinked = 0, dry = 0;
        List<String> names = new ArrayList<>();
        for (LandParcel p : picks) {
            dry += p.getDryKm2();
            if (!(p.getUnlinkedDryKm2() > 0)) continue;
            unlinked += p.getUnlinkedDryKm2();
            String name = landmassName(lms, p.getUnlinkedLargest());
            if (!names.contains(name)) names.add(name);
        }
        if (!(unlinked > 0)) return "";
        boolean whole = unlinked >= dry - 1e-9;
        return (whole ? "Their dry ground is on " : LandMap.area(unlinked) + " of their dry ground is on ") + joined(names)
                + ", " + NOT_LINKED_CLAUSE + ": " + ONLY_A_PORT;
    }

    /**
     * What an offer adds to the ground the city can build on, square feet: its size less its dry ground on landmasses no
     * port links (LandParcel.getUnlinkedDryKm2()); its size itself where it has none, to the bit.
     */
    public static double roomSqFt(LandParcel p) {
        double unlinked = p.getUnlinkedDryKm2();
        return unlinked > 0 ? Math.max(0, p.getSizeSqFt() - LandManager.sqFt(unlinked)) : p.getSizeSqFt();
    }

    /**
     * The strip's lines under THE GROUND's figures, one for each landmass no port links: "Island 2: 12.0 km² owned, 11.8
     * km² free, no port links it". Empty while the city owns nothing on one.
     */
    public static List<String> unlinkedLines(LandManager land) {
        Landmasses lms = land.getLandmasses();
        List<String> out = new ArrayList<>();
        for (int id : lms.unlinkedIds()) {
            double dry = lms.dryPlots(id) * Landmasses.SQ_FT_PER_PLOT;
            out.add(landmassName(lms, id) + ": " + LandManager.areaWords(dry) + " owned, " + LandManager.areaWords(lms.unlinkedFreeSqFt(id))
                    + " free, no port links it: only a port can be built there");
        }
        return out;
    }

    /** The tooltip of the bar's striped segment for the ground free on landmasses no port links. */
    public static String segmentWords(LandManager land) {
        return "free, no port links it · " + LandManager.areaWords(land.getUnlinkedFreeSqFt()) + " on " + land.getUnlinkedNames()
                + "\nOnly a port can be built there until it has one.";
    }

    /** The key's words for that segment. */
    public static final String SEGMENT_KEY = "free, no port links it";

    /**
     * The sentence a purchase's receipt ends with when it bought ground on a landmass no port links: "It lies on Island 2, which
     * no port links." ("0.842 km² of it lies on Island 2, which no port links." when only part of it lay there). "" otherwise.
     * A sentence of its own since WN5b (it was a clause glued on after the sellers' sentence, " - on Island 2, which no port
     * links", with the receipt's last full stop cut to make room). Read off the offer as it stood, split before it was bought
     * (LandParcel.getUnlinkedDryKm2()), and named by the books after it (Landmasses).
     */
    public static String receiptWords(LandParcel p, Landmasses lms) {
        double unlinked = p.getUnlinkedDryKm2();
        if (!(unlinked > 0)) return "";
        String name = landmassName(lms, p.getUnlinkedLargest());
        boolean whole = unlinked >= p.getDryKm2() - 1e-9;
        return (whole ? "It lies" : LandMap.area(unlinked) + " of it lies") + " on " + name + ", " + NOT_LINKED_CLAUSE + ".";
    }

    /* =====================================================================
       THE MAP
       ===================================================================== */

    /** The map's chip on a landmass no port links: "Island 2 · 12.0 km² · not linked: needs a port here (home has one)". */
    public static String chipWords(Landmasses lms, LandMap.Chip chip) {
        return chipWords(lms, chip, null);
    }

    /** ...with what links the island (FB13): "Island 2 · 12.0 km² · not linked: needs a Road Bridge (420 m) or a port here (home has one)". */
    public static String chipWords(Landmasses lms, LandMap.Chip chip, BuildAdvice.Link link) {
        String[] lines = chipLines(lms, chip, link);
        return lines[0] + " · " + String.join(" ", java.util.Arrays.copyOfRange(lines, 1, lines.length));
    }

    /**
     * ...in the two lines the map draws it in (MT1, BK1-L): the name and area, "Island 2 · 12.0 km²", then why it is not
     * linked, "not linked: needs a port here (home has one)". WHY two: in one line it was some 70 characters of amber
     * over the landmass's own hatch and tint, wider than a small island; each line is half that. chipWords() is the two
     * with " · " between, as the chip read before.
     */
    public static String[] chipLines(Landmasses lms, LandMap.Chip chip) {
        return chipLines(lms, chip, null);
    }

    /**
     * ...with what links the island (FB13, AL1-W): where the rule would link it by a bridge the chip has a third line, "not linked: needs a Road
     * Bridge (420 m)" and "or a port here (home has one)" under it, each as short as the old second line was; the plate takes any number of lines
     * (IslandChip.draw). A link by port, none and null read the old two lines.
     */
    public static String[] chipLines(Landmasses lms, LandMap.Chip chip, BuildAdvice.Link link) {
        String[] needs = needsParts(lms, chip.id(), link);
        String[] out = new String[1 + needs.length];
        out[0] = landmassName(lms, chip.id()) + " · " + LandMap.area(chip.plots() * World.KM2_PER_PLOT);
        out[1] = "not linked: " + needs[0];
        for (int i = 1; i < needs.length; i++) out[i + 1] = needs[i];
        return out;
    }

    /** The hover card's line for a plot of owned or offered ground on a landmass no port links: "On Island 2, which no port links: only a port can be built here". */
    public static String hoverWords(Landmasses lms, int id) {
        return "On " + landmassName(lms, id) + ", " + NOT_LINKED_CLAUSE + ": only a port can be built here";
    }

    /* =====================================================================
       THE PORT CARD: where, and what it links
       ===================================================================== */

    /** The chooser's label, before its chips. */
    public static final String CHOOSER_LABEL = "Where";

    /**
     * Whether the card shows the chooser for these choices (Game.portLandmassChoices()): there is something to choose between,
     * or the one landmass with shore is not home (an island's terminal is chosen, never the rule's). Not for none, and not
     * for home alone, where the card is the card it was.
     */
    public static boolean chooserShown(List<Landmasses.Row> choices) {
        return !choices.isEmpty() && !(choices.size() == 1 && choices.get(0).home());
    }

    /** The names on the chooser's chips, in Game.portLandmassChoices()' order: an island no port links says so. */
    public static String[] choiceNames(List<Landmasses.Row> choices) {
        String[] out = new String[choices.size()];
        for (int i = 0; i < out.length; i++) out[i] = choices.get(i).name() + (choices.get(i).linked() ? "" : " (not linked)");
        return out;
    }

    /** The chip that stands for the landmass the order would stand on now (Landmasses.portLandmass()), or null for none among the choices. */
    public static String currentChoice(List<Landmasses.Row> choices, int landmass) {
        String[] names = choiceNames(choices);
        for (int i = 0; i < names.length; i++) if (choices.get(i).id() == landmass || (choices.get(i).home() && landmass == 0)) return names[i];
        return null;
    }

    /**
     * What a sea terminal on the landmass it is ordered for would link, as the card says it (D10). On a landmass no port
     * links: "Island 2 becomes the city's 2nd linked landmass" where a linked landmass has a port already, else "links
     * nothing until home has a port too". On a linked landmass where no linked landmass has a port yet: "links Island 2,
     * which has a port" for the unlinked landmasses that have one. "" where it links nothing, and for a city on one landmass.
     */
    public static String linkWords(Landmasses lms, int landmass) {
        if (lms.single() || landmass == Landmasses.NO_LANDMASS) return "";
        boolean linkedPort = linkedPortName(lms) != null;
        if (!lms.linked(landmass)) {
            if (!linkedPort) return "links nothing until home has a port too";
            int linkedNow = 0;
            for (Landmasses.Row r : lms.rows()) if (r.linked()) linkedNow++;
            return landmassName(lms, landmass) + " becomes the city's " + ordinal(linkedNow + 1) + " linked landmass";
        }
        if (linkedPort) return "";
        List<String> waiting = new ArrayList<>();
        for (int id : lms.unlinkedIds()) if (hasPort(lms, id)) waiting.add(landmassName(lms, id));
        if (waiting.isEmpty()) return "";
        return "links " + joined(waiting) + ", which " + (waiting.size() == 1 ? "has a port" : "have ports");
    }

    private static String joined(List<String> list) {
        if (list.size() == 1) return list.get(0);
        return String.join(", ", list.subList(0, list.size() - 1)) + " and " + list.get(list.size() - 1);
    }

    /**
     * The sea terminal a notice's "Build a port there" opens the card of: the first General Cargo terminal in the
     * catalogue's order, else the first of any kind; null for a catalogue with none.
     */
    public static BuildingsTemplate portFor(List<BuildingsTemplate> catalogue) {
        BuildingsTemplate first = null;
        for (BuildingsTemplate t : catalogue) {
            if (!Ports.isPort(t)) continue;
            if (t.berthCargo() == Ports.Cargo.GENERAL) return t;
            if (first == null) first = t;
        }
        return first;
    }

    /* =====================================================================
       THE REFUSALS
       ===================================================================== */

    /**
     * What a sea terminal's NO_COAST says on the Build card's button once the city owns ground on more than one landmass:
     * the shore has to be on the landmass it serves. `chosen` is the landmass the card picked (Landmasses.chosen()), or
     * Landmasses.NO_LANDMASS (a pick of none, and none will do by the rule): "No shore owned" while the city owns shore on
     * no landmass, "No shore on linked ground" while it owns some on a landmass no port links. Null for a city on one
     * landmass: the old words stand.
     */
    public static String noShoreTitle(Landmasses lms, boolean port, int chosen) {
        if (lms.single()) return null;
        if (!port) return "No shore on linked ground";
        if (chosen == Landmasses.NO_LANDMASS) return lms.portChoices().isEmpty() ? "No shore owned" : "No shore on linked ground";
        return "No shore on " + landmassName(lms, chosen);
    }

    /** ...and the way out under it. */
    public static final String NO_SHORE_WAY = "own some sea in front of dry shore, on the landmass it serves: the Land office ›";

    /** The quote line's words for the same refusal: "no shore on Island 2". Null for a city on one landmass. */
    public static String noShoreQuote(Landmasses lms, boolean port, int chosen) {
        if (lms.single()) return null;
        if (!port) return "no shore on linked ground";
        if (chosen == Landmasses.NO_LANDMASS) return lms.portChoices().isEmpty() ? "no shore owned for it" : "no shore on linked ground";
        return "no shore on " + landmassName(lms, chosen);
    }

    /** The NO COAST page's extra paragraph for a city on more than one landmass: a terminal's shore is the landmass's own. Null for a city on one. */
    public static String noCoastParagraph(Landmasses lms) {
        if (lms.single()) return null;
        return "The city owns ground on more than one landmass, and a terminal needs dry shore it owns beside sea it owns, on the landmass it serves.";
    }

    /** The clause the sea terminal's description adds for a city on more than one landmass: ", in front of dry shore on the landmass it serves". "" for a city on one. */
    public static String seaClause(Landmasses lms) {
        return lms == null || lms.single() ? "" : ", in front of dry shore on the landmass it serves";
    }

    /**
     * The not-enough-land page's lines for ground the city owns and no port links: the clause the investors read, and where
     * to go. Empty while the city owns none.
     */
    public static List<String> noLandLines(LandManager land) {
        return noLandLines(land, null);
    }

    /** ...and, where the rule would link the island the Build tab's card names by a bridge (FB13, AL1-W), the line that says a bridge links it as well. */
    public static List<String> noLandLines(LandManager land, BuildAdvice.Link advice) {
        List<String> out = new ArrayList<>();
        double free = land.getUnlinkedFreeSqFt();
        if (!(free > 0)) return out;
        out.add(unlinkedWords(free, land.getUnlinkedNames()) + ".");
        out.add("Only a sea terminal can be built there; two standing ports, one at home, link it.");
        if (advice != null && advice.bridge() != null) {
            out.add("A bridge links " + advice.name() + " as well: " + bridgeWords(advice) + " is allowed there.");
        }
        return out;
    }

    /** The label of the door from the refusal to the port card. */
    public static final String PORT_DOOR = "Build a port there ›";

    /* =====================================================================
       THE NOTICE
       ===================================================================== */

    /** A standing notice's key prefix: "island:" and the landmass's id. */
    public static final String NOTICE_KEY = "island:";

    /** The key of the notice for a landmass. */
    public static String noticeKey(int id) {
        return NOTICE_KEY + id;
    }

    /** The landmass a notice's key is for, or -1 for a key that is no island's. */
    public static int noticeId(String key) {
        if (key == null || !key.startsWith(NOTICE_KEY)) return -1;
        try {
            return Integer.parseInt(key.substring(NOTICE_KEY.length()));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** The button on that notice. */
    public static final String NOTICE_BUTTON = PORT_DOOR;

    /** The button on that notice where the city owns no shore on the landmass: a terminal cannot stand there yet, and sea is bought at the Land office. */
    public static final String NOTICE_BUTTON_NO_SHORE = "Go to the Land office ›";

    /** The button on the notice for a landmass: the port card's door where the city owns shore on it, else the Land office's. */
    public static String noticeButton(Landmasses lms, int id) {
        return noticeButton(lms, id, null);
    }

    /** The label of the door to a bridge's card (FB13): the notice's button and the Build tab's card, where the rule would link the island by a bridge. */
    public static final String BRIDGE_DOOR = "Build a bridge there ›";

    /** ...and where a bridge is already being built to the island: the door to its card, to see it. */
    public static final String BRIDGE_UNDER_WAY_DOOR = "See the bridge ›";

    /** Where a notice's button, and the Build tab's card, take the player (FB13): a bridge's card, a sea terminal's card, or the Land office to buy the sea a terminal needs. */
    public enum Door { BRIDGE, PORT, LAND_OFFICE }

    /** The door of the notice for a landmass: the bridge's where `link` is a bridge to it (ordered or allowed), else the port card's where the city owns shore on it, else the Land office's. */
    public static Door noticeDoor(Landmasses lms, int id, BuildAdvice.Link link) {
        if (link != null && (link.bridge() != null || link.underWay())) return Door.BRIDGE;
        return lms.shorePlots(id) > 0 ? Door.PORT : Door.LAND_OFFICE;
    }

    /** ...and the button on the notice for a landmass, with what links it (FB13, AL1-W): the old words for a port or none, "Build a bridge there ›" where the rule would link it by one. */
    public static String noticeButton(Landmasses lms, int id, BuildAdvice.Link link) {
        return switch (noticeDoor(lms, id, link)) {
            case BRIDGE -> link.underWay() ? BRIDGE_UNDER_WAY_DOOR : BRIDGE_DOOR;
            case PORT -> NOTICE_BUTTON;
            case LAND_OFFICE -> NOTICE_BUTTON_NO_SHORE;
        };
    }

    /** A notice's title: "Island 2: owned, but no port links it". */
    public static String noticeTitle(Landmasses lms, int id) {
        return landmassName(lms, id) + ": owned, but no port links it";
    }

    /** A notice's body, in lines of at most Inbox.BODY_COLUMNS characters: what the city owns there, what that means, what links it. */
    public static List<String> noticeBody(Landmasses lms, int id) {
        return noticeBody(lms, id, null);
    }

    /**
     * ...and, where the rule would link the island by a bridge (FB13, AL1-W), the sentence that says so before the port's: the bridge the rule would
     * order and the crossing its survey found, or that one is being built. A link by port, none and null read the old paragraphs.
     */
    public static List<String> noticeBody(Landmasses lms, int id, BuildAdvice.Link link) {
        String name = landmassName(lms, id);
        List<String> out = new ArrayList<>();
        out.addAll(Inbox.wrapped("The city owns " + LandManager.areaWords(lms.dryPlots(id) * Landmasses.SQ_FT_PER_PLOT) + " of dry ground on " + name
                + ", and " + LandManager.areaWords(lms.unlinkedFreeSqFt(id)) + " of it is free.", Inbox.BODY_COLUMNS));
        out.add("");
        out.addAll(Inbox.wrapped("No port links " + name + ", so no home, shop or works may be built there, and nothing is dug from its fields:"
                + " only a sea terminal may stand on it, until it has one.", Inbox.BODY_COLUMNS));
        out.add("");
        if (link != null && link.underWay()) {
            out.addAll(Inbox.wrapped("A bridge is being built to " + name + ", and links it when it opens.", Inbox.BODY_COLUMNS));
            out.add("");
        } else if (link != null && link.bridge() != null) {
            String bridge = bridgeWords(link);
            out.addAll(Inbox.wrapped("A bridge links " + name + " without a port: " + Character.toUpperCase(bridge.charAt(0)) + bridge.substring(1)
                    + " is allowed there (" + BridgeView.crossingWords(link.survey(), lms) + ").", Inbox.BODY_COLUMNS));
            out.add("");
        }
        // A terminal needs shore the city owns on its own landmass: with none, the way to a port is the Land office's, first.
        if (lms.shorePlots(id) == 0) {
            out.addAll(Inbox.wrapped("The city owns no shore on " + name + ": own some sea in front of its dry shore, at the Land office,"
                    + " before a terminal can stand there.", Inbox.BODY_COLUMNS));
            out.add("");
        }
        out.addAll(Inbox.wrapped("A port on " + name + " links it once it stands and a port stands at a linked landmass too ("
                + needsWords(lms, id).substring("no: ".length()) + ").", Inbox.BODY_COLUMNS));
        return out;
    }

    /* =====================================================================
       THE BUILD TAB'S CARD FOR THE LINK (FB13, AL1-W)
       ===================================================================== */

    /** The head of the Overview's section for the link, drawn only while the rule has an island to link. */
    public static final String LINK_HEAD = "AN ISLAND TO LINK";

    /** The hint at the right of that head: whose rule the card is. */
    public static final String LINK_HINT = "what automatic building orders when the city is short of ground";

    /**
     * The card for the link the rule would order (BuildAdvice.linkShown()), worded: `title` the island and the ground it would give, `does` what
     * links it (the bridge and its crossing, the terminals, or why nothing can), `price` the orders' total in the thousands the game keeps and
     * `months` the longest order's (NaN where a site has no output), for the screen to write in its money and its months; `door` where the card's
     * button goes (null where nothing can link the island), `label` its words, and `template` and `asked` the building whose card it opens and the
     * landmass its next order is for (a bridge's far landmass, or a terminal's: Landmasses.RULE for the rule's).
     */
    public record Card(int landmass, String title, String does, double price, double months, Door door, String label,
                       BuildingsTemplate template, int asked) { }

    /** The card for a link, or null for none and for one a bridge is already on site to (the island the rule has nothing to order for). */
    public static Card linkCard(Landmasses lms, BuildAdvice.Link link) {
        if (link == null || link.underWay()) return null;
        String title = link.name() + " · " + LandManager.areaWords(link.sqFt()) + " of ground to link";
        switch (link.kind()) {
            case BRIDGE: {
                String bridge = bridgeWords(link);
                BuildAdvice.LinkOrder order = link.orders().get(0);
                return new Card(link.landmass(), title, Character.toUpperCase(bridge.charAt(0)) + bridge.substring(1) + " links it: "
                        + BridgeView.crossingWords(link.survey(), lms) + ".", link.price(), link.months(), Door.BRIDGE, BRIDGE_DOOR,
                        order.template(), order.asked());
            }
            case PORT: {
                List<String> parts = new ArrayList<>();
                for (BuildAdvice.LinkOrder o : link.orders()) {
                    parts.add(o.template().getName() + (o.asked() == Landmasses.RULE ? " on linked ground" : " on " + link.name()));
                }
                String does = link.orders().size() == 1 && link.orders().get(0).asked() == Landmasses.RULE
                        ? "A terminal stands on " + link.name() + " and none on linked ground, so " + parts.get(0) + " links it."
                        : "No bridge can be built there, so sea terminals link it: " + joined(parts) + ".";
                BuildAdvice.LinkOrder first = link.orders().get(0);
                return new Card(link.landmass(), title, does, link.price(), link.months(), Door.PORT, PORT_DOOR, first.template(), first.asked());
            }
            default:
                return new Card(link.landmass(), title, link.why(), 0, Double.NaN, null, null, null, Landmasses.NO_LANDMASS);
        }
    }

    /** The bridge a door opens the card of where no link names one (a bridge already on site): the first Road Bridge in the catalogue's order, else the first bridge of any kind; null for a catalogue with none. */
    public static BuildingsTemplate bridgeFor(List<BuildingsTemplate> catalogue) {
        BuildingsTemplate first = null;
        for (BuildingsTemplate t : catalogue) {
            if (!t.isBridge()) continue;
            if (t.bridgeKind() == BridgeWorks.Kind.ROAD) return t;
            if (first == null) first = t;
        }
        return first;
    }

    /* =====================================================================
       THE PLAYTEST'S REPORT
       ===================================================================== */

    /**
     * The long playtest's line after ITS GROUND AT THE END: the city's dry ground by landmass and the terminals standing
     * off home - "ITS LANDMASSES AT THE END: Home 177.7 km², Island 2 3.1 km2 (not linked); terminals off home 0". Null for a
     * city on one landmass, which prints exactly what it printed before.
     */
    public static String reportLine(Landmasses lms) {
        if (lms == null || lms.single()) return null;
        StringBuilder s = new StringBuilder("ITS LANDMASSES AT THE END: ");
        long off = 0;
        boolean first = true;
        for (Landmasses.Row r : lms.rows()) {
            if (!first) s.append(", ");
            first = false;
            s.append(r.name()).append(' ').append(String.format(Locale.ROOT, "%.1f km2", LandManager.km2(r.drySqFt())));
            if (!r.linked()) s.append(" (not linked)");
            if (!r.home()) off += terminalsOn(lms, r.id());
        }
        return s.append("; terminals off home ").append(off).toString();
    }
}
