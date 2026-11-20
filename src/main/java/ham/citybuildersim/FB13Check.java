package ham.citybuildersim;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * FB13's three texts, each asked of the model that says it: the island's words (AL1-W), the Government screen's coupons abroad (CD9-G) and the
 * build card's licence line (R5-L). Not part of the game.
 *
 * WHY. Three screens kept words from before the model changed under them. The Land tab, the map's chip and the island's notice said "needs a
 * port here" where AL1 had made a bridge the way (BuildAdvice.linkFor() orders one wherever a crossing is allowed), and the Build tab had no card
 * for the link; the Government screen's "...of which paid abroad" row said "in dollars" for coupons CD8 lets the city pay in any of eight moneys,
 * its figure being in D$; and the build card drew no licence line for a patented building from abroad, whose fee R2c and R2c-P built after R5
 * waited. Each text is the model's: a public getter's figure through a pure words class (LandmassView, ForeignCouponView, ResearchView), the
 * screen only putting it where it goes. So each is proved here from the model's side, against its own constants and getters, from the states that
 * cause it and the states that do not - and, where a screen cannot be drawn without the toolkit, from the screen's source line that places it.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE ISLAND'S WORDS, A CROSSING ALLOWED. With a shallow channel the survey can cross, the screens' read of the island
 *      (BuildAdvice.islandLink()) is the rule's own answer (BuildAdvice.linkFor()): a BRIDGE, the template, the price. The table's
 *      cell, its tooltip, the map's chip (a third line), the notice's body and its button (a door to the bridge) name that bridge and
 *      the crossing it found, and the port after it; with no link given the words are the old ones.
 *   2. ...A BRIDGE UNDER WAY. With the bridge ordered, the island's words say one is being built, and the notice's door is the
 *      bridge's, to see it.
 *   3. ...DEEP SEA AND NO LINK. Where the survey finds deep water the link is a PORT and every word is the old one (two lines on the
 *      chip, the port card's door); the dry island with no shore and no crossing is NONE, the same words, and the Build tab's card says
 *      why nothing links it, with no door.
 *   4. THE BUILD TAB'S CARD. For a crossing the card is the rule's link in words: the island and its ground, the bridge and its
 *      crossing, the price and months of the quote, the door to the bridge's card; for ports, the two terminals and a door to the first.
 *   5. A SCREEN'S READ MOVES NOTHING. Reading the island for a screen leaves the bridge card's chosen landmass alone, does not count as
 *      one of the rule's surveys, leaves the rule's own memory of a survey that found no crossing as it was (so the rule surveys when it
 *      would have), and is kept for the month: a second read with nothing changed reads nothing.
 *   6. THE GOVERNMENT SCREEN'S COUPONS. The row's note names D$ and the moneys the foreign paper is written in, from the debt's own
 *      books (US dollars, euros, yen), and says "in dollars" for none of them; with no foreign paper it says D$ alone; the screen's line
 *      places that note and no other.
 *   7. THE BUILD CARD'S LICENCE LINE. In a city founded the 1900 way a building the city found itself says none ever, one the world had
 *      first and still patented says a quarter of what it adds goes abroad until the month its patent ends (history's date and
 *      LicenceFees.TERM_MONTHS) and about what the licence costs a month (LicenceFees.forNew()), one whose patent has ended or that
 *      the city has found since says none now, an unpatented one none; a building known by a year, a house and every card of a city
 *      founded the default way have no line.
 *
 * Every premise is asserted before what rests on it.
 *
 * @author Jerus
 */
public class FB13Check {

    static int fails = 0;
    static final PrintStream out = System.out;

    static void check(String label, boolean ok) {
        out.println((ok ? "  PASS " : "  FAIL ") + label);
        if (!ok) fails++;
    }

    static void report(String label, boolean ok, String detail) {
        out.println((ok ? "  PASS " : "  FAIL ") + label + (detail == null || detail.isEmpty() ? "" : "  [" + detail + "]"));
        if (!ok) fails++;
    }

    static void quietly(Runnable r) { LandmassCheck.quietly(r); }

    /** The island's row as the one-argument rows have it, with no link given. */
    static LandmassView.Line oldRow(LandManager land, int island) {
        for (LandmassView.Line l : LandmassView.rows(land)) if (l.id() == island) return l;
        return null;
    }

    public static void main(String[] args) throws Exception {
        java.util.Locale.setDefault(java.util.Locale.CANADA);
        out.println("--- the fixture ---");
        byte[] fixture = EarthCheck.resource(EarthCheck.FIXTURE);
        check("EarthCheck's fixture is beside the classes (" + EarthCheck.FIXTURE + ")", fixture != null);
        if (fixture == null) {
            out.println("\n" + fails + " FAILED");
            System.exit(1);
        }
        EarthData data = EarthData.of(fixture);
        String id = EarthTerrain.use(data);
        World.Key rotterdam = World.Key.earth(LandmassCheck.SEED, LandmassCheck.RLAT, LandmassCheck.RLON, id);
        LandmassCheck.Fixtures f = LandmassCheck.fixtures(rotterdam);
        check("the island square with shore, the one with none and home's coast were found (LandmassCheck's fixtures)", f.ok);
        if (!f.ok) {
            out.println("\n" + fails + " FAILED");
            System.exit(1);
        }
        BridgeCheck.Island isle = new BridgeCheck.Island(rotterdam, f.withShore), dry = new BridgeCheck.Island(rotterdam, f.withoutShore);
        islandWithACrossing(f, isle);
        islandUnderWay(isle);
        islandOnDeepSea(f, isle, dry);
        screensMoveNothing(f, isle);
        couponsAbroad();
        licenceLine();
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** "no: needs a port here and one at home", the words as they were before a link was given (no terminal stands anywhere in these fixtures). */
    static final String OLD_NEEDS = "no: needs a port here and one at home";

    /* =====================================================================
       1. THE ISLAND'S WORDS, A CROSSING ALLOWED
       ===================================================================== */

    static void islandWithACrossing(LandmassCheck.Fixtures f, BridgeCheck.Island isle) {
        out.println("\n--- 1. the island's words, a crossing allowed: the table, the chip, the notice and its door name the bridge the rule would order ---");
        LandmassCheck.City c = BridgeCheck.bridgeCity(isle, "fb13-bridge");
        Game g = c.g;
        Landmasses lms = c.books();
        int island = isle.id();
        BuildingsTemplate road = LandmassCheck.tpl(c, "Road Bridge");
        BridgeCheck.seam(c, isle, 14, World.SHALLOW, 10);
        BuildAdvice.Link rule = BuildAdvice.linkFor(g);
        BuildAdvice.Link shown = BuildAdvice.islandLink(g, island);
        report("premise: the rule links the island by a BRIDGE (the Road Bridge: no highway stands), and the screens' read of it is that answer: the same kind, template, landmass, price and crossing",
                rule != null && rule.kind() == BuildAdvice.LinkKind.BRIDGE && shown != null && shown.kind() == rule.kind() && shown.bridge() != null
                        && shown.bridge().getId() == road.getId() && rule.bridge().getId() == road.getId() && shown.landmass() == rule.landmass() && shown.landmass() == island
                        && Math.abs(shown.price() - rule.price()) <= 1e-9 * Math.max(1, rule.price()) && shown.survey().ok()
                        && Math.abs(shown.survey().lengthM() - rule.survey().lengthM()) < 1e-9 && !shown.underWay(),
                shown == null ? "none" : shown.kind() + " " + road.getName() + String.format(" over %.0f m", shown.survey().lengthM()));
        check("premise: no terminal stands anywhere, so the port's half of the words is 'a port here and one at home'", lms.portsAt(island) == 0 && lms.portsAt(lms.home()) == 0 && LandmassView.linkedPortName(lms) == null);

        String bridge = "a " + road.getName() + " (" + BridgeView.lengthWords(shown.survey().lengthM()) + ")";
        check("the bridge in words is the template's name and the survey's length: " + bridge, LandmassView.bridgeWords(shown).equals(bridge));
        String needs = LandmassView.needsWords(lms, island, shown);
        check("the table's cell names the bridge first and the port after it: 'no: needs " + bridge + " or a port here and one at home'",
                needs.equals("no: needs " + bridge + " or a port here and one at home"));
        check("...and with no link given the words are the old ones (a port alone), as the one-argument form says them", LandmassView.needsWords(lms, island, null).equals(OLD_NEEDS) && LandmassView.needsWords(lms, island).equals(OLD_NEEDS));
        List<LandmassView.Line> rows = LandmassView.rows(c.lm, x -> BuildAdvice.islandLink(g, x));
        LandmassView.Line row = null;
        for (LandmassView.Line l : rows) if (l.id() == island) row = l;
        report("the Land tab's row for the island says the same in its 'linked' cell, and its tooltip says 'until it is linked' (a bridge links it as a port does), where the one-argument rows say 'until a port links it'",
                row != null && row.linked().equals(needs) && row.tip().contains("until it is linked (" + needs.substring("no: ".length()) + ")")
                        && !row.tip().contains("until a port links it") && oldRow(c.lm, island).tip().contains("until a port links it (" + OLD_NEEDS.substring("no: ".length()) + ")"),
                row == null ? "no row" : row.linked());

        List<LandMap.Chip> chips = LandMap.unlinkedChips(lms, c.w, c.lm.getCityLand());
        LandMap.Chip chip = chips.isEmpty() ? null : chips.get(0);
        check("premise: the map has one chip, for the island", chips.size() == 1 && chip.id() == island);
        String name = LandmassView.landmassName(lms, island) + " · " + LandMap.area(chip.plots() * World.KM2_PER_PLOT);
        String[] lines = LandmassView.chipLines(lms, chip, shown);
        report("the map's chip is three lines, each short as the old second line was: the name and area, 'not linked: needs " + bridge + "', then 'or a port here and one at home'",
                lines.length == 3 && lines[0].equals(name) && lines[1].equals("not linked: needs " + bridge) && lines[2].equals("or a port here and one at home"),
                String.join(" | ", lines));
        check("...its one-line words are the three joined, and the old chip (no link) is the old two lines",
                LandmassView.chipWords(lms, chip, shown).equals(name + " · not linked: needs " + bridge + " or a port here and one at home")
                        && LandmassView.chipLines(lms, chip).length == 2 && LandmassView.chipLines(lms, chip)[1].equals("not linked: needs a port here and one at home")
                        && LandmassView.chipLines(lms, chip, null).length == 2);

        quietly(() -> g.simulateMonths(1));
        Notice notice = g.getInbox().live(LandmassView.noticeKey(island));
        BuildAdvice.Link again = BuildAdvice.islandLink(g, island);
        report("a month on, the island's notice is raised with the body the view writes for the link the screens read, which says the bridge and the crossing it found before the port's sentence",
                notice != null && again != null && again.bridge() != null && notice.getBody().equals(LandmassView.noticeBody(lms, island, again))
                        && String.join(" ", notice.getBody()).contains(Character.toUpperCase(bridge.charAt(0)) + bridge.substring(1) + " is allowed there (" + BridgeView.crossingWords(again.survey(), lms) + ")")
                        && String.join(" ", notice.getBody()).contains("needs a port here and one at home"),
                notice == null ? "no notice" : String.join(" ", notice.getBody()).substring(0, Math.min(120, String.join(" ", notice.getBody()).length())));
        check("...and a body with no link is the old one: the bridge's sentence is the only difference",
                !String.join(" ", LandmassView.noticeBody(lms, island, null)).contains("is allowed there") && LandmassView.noticeBody(lms, island).equals(LandmassView.noticeBody(lms, island, null)));
        check("the notice's door is the bridge's, 'Build a bridge there ›', where the city owns shore on the island or none (a bridge wants landings, not a port's shore); the old button is the port card's",
                LandmassView.noticeDoor(lms, island, again) == LandmassView.Door.BRIDGE && LandmassView.noticeButton(lms, island, again).equals("Build a bridge there ›")
                        && LandmassView.noticeButton(lms, island, again).equals(LandmassView.BRIDGE_DOOR)
                        && LandmassView.noticeButton(lms, island).equals(LandmassView.NOTICE_BUTTON) && lms.shorePlots(island) > 0
                        && LandmassView.noticeDoor(lms, island, null) == LandmassView.Door.PORT);

        // The Build tab's card for the same link, a month on (the quote is the month's: the card is what the rule says now).
        rule = BuildAdvice.linkFor(g);
        shown = again;
        LandmassView.Card card = LandmassView.linkCard(lms, BuildAdvice.linkShown(g));
        report("the Build tab's card for it (4): the island and its ground, the bridge and its crossing, the price and the months of the rule's own quote, and the door to the bridge's card",
                card != null && card.landmass() == island && card.title().equals(lms.name(island) + " · " + LandManager.areaWords(shown.sqFt()) + " of ground to link")
                        && card.does().equals(Character.toUpperCase(bridge.charAt(0)) + bridge.substring(1) + " links it: " + BridgeView.crossingWords(shown.survey(), lms) + ".")
                        && Math.abs(card.price() - shown.price()) <= 1e-9 * Math.max(1, shown.price()) && (card.months() == shown.months() || (Double.isNaN(card.months()) && Double.isNaN(shown.months())))
                        && card.door() == LandmassView.Door.BRIDGE && card.label().equals(LandmassView.BRIDGE_DOOR) && card.template().getId() == road.getId() && card.asked() == island,
                card == null ? "no card" : card.title() + " | " + card.does());
        check("...and it is the rule's link: the card's island, building and price are linkFor()'s", card != null && card.landmass() == rule.landmass()
                && card.template().getId() == rule.orders().get(0).template().getId() && Math.abs(card.price() - rule.price()) <= 1e-9 * Math.max(1, rule.price()));
        check("the section's head and hint are the view's words", LandmassView.LINK_HEAD.equals("AN ISLAND TO LINK") && !LandmassView.LINK_HINT.isEmpty());
        check("a city on one landmass has no link to say: the screens' reads are null, and so is the card", LandmassView.linkCard(f.b.books(), BuildAdvice.linkShown(f.b.g)) == null && BuildAdvice.islandLink(f.b.g, 0) == null);
        check("the bridge's door where none is named (one is on site) is the catalogue's first Road Bridge",
                LandmassView.bridgeFor(g.getBuildingManager().getTemplates()) != null && LandmassView.bridgeFor(g.getBuildingManager().getTemplates()).bridgeKind() == BridgeWorks.Kind.ROAD);
    }

    /* =====================================================================
       2. A BRIDGE UNDER WAY
       ===================================================================== */

    static void islandUnderWay(BridgeCheck.Island isle) {
        out.println("\n--- 2. a bridge under way: the island's words say one is being built, and the door is the bridge's, to see it ---");
        LandmassCheck.City c = BridgeCheck.bridgeCity(isle, "fb13-underway");
        Game g = c.g;
        Landmasses lms = c.books();
        int island = isle.id();
        BuildingsTemplate road = LandmassCheck.tpl(c, "Road Bridge");
        BridgeCheck.seam(c, isle, 14, World.SHALLOW, 10);
        Game.BuildResult built = BridgeCheck.orderBridge(c, road, 1, false, island);
        report("premise: the Road Bridge is ordered toward the island, on site (not at once), and the island is still unlinked",
                built == Game.BuildResult.SUCCESS && g.getBridges().buildingTo(island) && !lms.linked(island) && lms.anyUnlinked(), "order " + built);
        BuildAdvice.Link link = BuildAdvice.islandLink(g, island);
        check("the screens' read of the island is a link under way: a BRIDGE with no order and no survey, which the rule's own answer never is (the rule leaves the island alone)",
                link != null && link.underWay() && link.kind() == BuildAdvice.LinkKind.BRIDGE && link.orders().isEmpty() && link.bridge() == null && link.survey() == null
                        && link.landmass() == island && BuildAdvice.linkFor(g) == null && BuildAdvice.linkShown(g) == null);
        check("the cell says 'no: a bridge is being built to it' and the chip 'not linked: a bridge is being built to it', in two lines as before",
                LandmassView.needsWords(lms, island, link).equals("no: a bridge is being built to it")
                        && LandmassView.chipLines(lms, LandMap.unlinkedChips(lms, c.w, c.lm.getCityLand()).get(0), link).length == 2
                        && LandmassView.chipLines(lms, LandMap.unlinkedChips(lms, c.w, c.lm.getCityLand()).get(0), link)[1].equals("not linked: a bridge is being built to it"));
        List<String> body = LandmassView.noticeBody(lms, island, link);
        check("the notice's body says a bridge is being built and links it when it opens, and the card of the Build tab has none (nothing to order)",
                String.join(" ", body).contains("A bridge is being built to " + lms.name(island) + ", and links it when it opens.") && LandmassView.linkCard(lms, link) == null);
        check("the door is the bridge's, 'See the bridge ›', not the port card's",
                LandmassView.noticeDoor(lms, island, link) == LandmassView.Door.BRIDGE && LandmassView.noticeButton(lms, island, link).equals(LandmassView.BRIDGE_UNDER_WAY_DOOR)
                        && LandmassView.BRIDGE_UNDER_WAY_DOOR.equals("See the bridge ›"));
    }

    /* =====================================================================
       3. DEEP SEA AND NO LINK
       ===================================================================== */

    static void islandOnDeepSea(LandmassCheck.Fixtures f, BridgeCheck.Island isle, BridgeCheck.Island dry) {
        out.println("\n--- 3. deep sea and no link: the words are the old ones, and the card says why where nothing links ---");
        int island = isle.id();
        LandmassCheck.City c = LandmassCheck.portCity(f, "fb13-port", true);
        Game g = c.g;
        Landmasses lms = c.books();
        BridgeCheck.seam(c, isle, 12, World.SALT, 45);
        BuildAdvice.Link link = BuildAdvice.islandLink(g, island);
        BuildAdvice.Link rule = BuildAdvice.linkFor(g);
        report("premise: the survey finds deep water, so the link is two sea terminals (a PORT), the island's own and one at home, and the screens read what the rule does",
                link != null && link.kind() == BuildAdvice.LinkKind.PORT && link.orders().size() == 2 && rule != null && rule.kind() == BuildAdvice.LinkKind.PORT
                        && link.orders().get(0).template().getId() == rule.orders().get(0).template().getId() && link.orders().get(0).asked() == island
                        && link.orders().get(1).asked() == Landmasses.RULE && link.bridge() == null && !link.underWay(),
                link == null ? "none" : link.kind() + " " + link.orders().size() + " orders");
        LandMap.Chip chip = LandMap.unlinkedChips(lms, c.w, c.lm.getCityLand()).get(0);
        check("every word is the old one for a port: the cell, the chip's two lines, the notice's body and its button (the port card's)",
                LandmassView.needsWords(lms, island, link).equals(LandmassView.needsWords(lms, island)) && LandmassView.chipLines(lms, chip, link).length == 2
                        && String.join("|", LandmassView.chipLines(lms, chip, link)).equals(String.join("|", LandmassView.chipLines(lms, chip)))
                        && LandmassView.noticeBody(lms, island, link).equals(LandmassView.noticeBody(lms, island))
                        && LandmassView.noticeButton(lms, island, link).equals(LandmassView.NOTICE_BUTTON) && LandmassView.noticeDoor(lms, island, link) == LandmassView.Door.PORT);
        LandmassView.Card card = LandmassView.linkCard(lms, BuildAdvice.linkShown(g));
        String there = link.orders().get(0).template().getName() + " on " + link.name(), home = link.orders().get(1).template().getName() + " on linked ground";
        report("the Build tab's card is the two terminals the rule would order: 'No bridge can be built there, so sea terminals link it: <island's> and <home's>', their price and months, the door to the first terminal's card",
                card != null && card.does().equals("No bridge can be built there, so sea terminals link it: " + there + " and " + home + ".") && card.door() == LandmassView.Door.PORT
                        && card.label().equals(LandmassView.PORT_DOOR) && card.template().getId() == link.orders().get(0).template().getId() && card.asked() == island
                        && Math.abs(card.price() - link.price()) <= 1e-9 * Math.max(1, link.price()),
                card == null ? "no card" : card.does());

        // A terminal at home already: the island's alone is what links it; a terminal on the island and none at home: the home one.
        LandmassCheck.City h = LandmassCheck.portCity(f, "fb13-port-shared", true);
        BridgeCheck.seam(h, isle, 12, World.SALT, 45);
        BuildingsTemplate berth = h.g.getBuildingManager().getTemplate(link.orders().get(0).template().getId());
        Game.BuildResult homeBuilt = LandmassCheck.order(h, berth, 1, true, Landmasses.RULE);
        BuildAdvice.Link shared = BuildAdvice.islandLink(h.g, island);
        LandmassView.Card sharedCard = LandmassView.linkCard(h.books(), BuildAdvice.linkShown(h.g));
        report("with a terminal standing at home the island's alone links it, and with a port standing the words name where it is: 'needs a port here (home has one)'",
                homeBuilt == Game.BuildResult.SUCCESS && shared != null && shared.kind() == BuildAdvice.LinkKind.PORT && shared.orders().size() == 1 && shared.orders().get(0).asked() == island
                        && LandmassView.needsWords(h.books(), island, shared).equals("no: needs a port here (home has one)") && sharedCard != null && sharedCard.door() == LandmassView.Door.PORT,
                "order " + homeBuilt);

        // Nothing links it: the island with no shore and no crossing.
        LandmassCheck.City d = BridgeCheck.bridgeCity(dry, "fb13-dry");
        BridgeCheck.seam(d, dry, 12, World.SALT, 45);
        BuildAdvice.Link none = BuildAdvice.islandLink(d.g, dry.id());
        LandmassView.Card noCard = LandmassView.linkCard(d.books(), BuildAdvice.linkShown(d.g));
        report("an island owned with no shore and no crossing is NONE: the words are the old ones, and the card says the rule's own reason with no price and no door",
                none != null && none.kind() == BuildAdvice.LinkKind.NONE && none.why() != null && none.why().contains("No port either")
                        && LandmassView.needsWords(d.books(), dry.id(), none).equals(LandmassView.needsWords(d.books(), dry.id())) && noCard != null
                        && noCard.does().equals(none.why()) && noCard.door() == null && noCard.label() == null && noCard.price() == 0 && noCard.template() == null,
                none == null ? "none" : none.why());
        check("...and with no door the notice's button is the port card's or the Land office's as it was",
                LandmassView.noticeDoor(d.books(), dry.id(), none) == (d.books().shorePlots(dry.id()) > 0 ? LandmassView.Door.PORT : LandmassView.Door.LAND_OFFICE));
    }

    /* =====================================================================
       5. A SCREEN'S READ MOVES NOTHING
       ===================================================================== */

    static void screensMoveNothing(LandmassCheck.Fixtures f, BridgeCheck.Island isle) {
        out.println("\n--- 5. a screen's read moves nothing: not the bridge card's pick, not the rule's survey count or its memory; and it is kept for the month ---");
        int island = isle.id();
        // On a crossing: the card's pick, the survey count, the kept read.
        LandmassCheck.City c = BridgeCheck.bridgeCity(isle, "fb13-reads");
        Game g = c.g;
        Landmasses lms = c.books();
        BuildingsTemplate road = LandmassCheck.tpl(c, "Road Bridge");
        BridgeCheck.seam(c, isle, 14, World.SHALLOW, 10);
        int surveys = BuildAdvice.linkSurveys;
        BuildAdvice.Link first = BuildAdvice.islandLink(g, island);
        check("premise: the island has a crossing, and the first read is a BRIDGE", first != null && first.bridge() != null);
        check("the bridge card's chosen landmass is as the screen found it (none), and the rule's survey count did not move: the screens' surveys are not the rule's",
                g.getBridges().chosen(road.getId()) == Bridges.NONE_CHOSEN && BuildAdvice.linkSurveys == surveys);
        g.chooseBridgeLandmass(road, lms.home());
        BuildAdvice.islandLink(g, island);
        check("...and a pick the player had made (home) is the pick still, after a read", g.getBridges().chosen(road.getId()) == lms.home());
        g.getBridges().clearChoice(road.getId());
        int reads = BuildAdvice.shownReads;
        for (int k = 0; k < 5; k++) {
            BuildAdvice.islandLink(g, island);
            BuildAdvice.linkShown(g);
        }
        check("a read with nothing changed reads nothing: ten asks in the month are no new reading (the map asks every frame)", BuildAdvice.shownReads == reads);
        quietly(() -> g.simulateMonths(1));
        BuildAdvice.islandLink(g, island);
        check("...and the next month reads again, once", BuildAdvice.shownReads == reads + 1);

        // On deep sea: the survey that found no crossing is the rule's own memory to keep.
        LandmassCheck.City s = LandmassCheck.portCity(f, "fb13-reads-sea", true);
        BridgeCheck.seam(s, isle, 12, World.SALT, 45);
        int before = BuildAdvice.linkSurveys;
        BuildAdvice.Link sea = BuildAdvice.islandLink(s.g, island);
        BuildAdvice.islandLink(s.g, island);
        check("premise: the survey finds deep water, and the screens' read is a PORT", sea != null && sea.kind() == BuildAdvice.LinkKind.PORT && sea.survey() == null);
        BuildAdvice.linkFor(s.g);
        check("the rule, asked after the screens' read of a survey that found no crossing, surveys for itself (the screens' memory is not its own)", BuildAdvice.linkSurveys == before + 1);
        BuildAdvice.linkFor(s.g);
        check("...and remembers it as ever: asked again in the month it does not survey", BuildAdvice.linkSurveys == before + 1);
    }

    /* =====================================================================
       6. THE GOVERNMENT SCREEN'S COUPONS ABROAD
       ===================================================================== */

    static void couponsAbroad() {
        out.println("\n--- 6. the Government screen's '...of which paid abroad': D$, and the moneys the coupons are written in ---");
        PrintStream quiet = new PrintStream(java.io.OutputStream.nullOutputStream());
        DebtMoneysCheck.quiet = quiet;
        Game g = DebtMoneysCheck.town("fb13-coupons", false);
        DebtManager ledger = g.getDebtManager();
        check("premise: the city has the world's moneys, owes nothing abroad yet, and every one of the eight is offered but by what the files say",
                !g.isOnLiterals() && !ledger.hasForeignDebt() && ledger.offers("USD") && ledger.offers("EUR") && ledger.offers("JPY"));
        check("with no foreign paper the note is D$ alone: 'in D$, at the month's rates'", ForeignCouponView.moneys(ledger).isEmpty() && ForeignCouponView.note(ledger).equals("in D$, at the month's rates"));
        String usd = DebtMoneysCheck.sell(g, "Term", "USD", DebtMoneysCheck.askIn(g, "USD"), 10, false);
        report("premise: a US$ term bond is sold", ledger.hasForeignDebtIn("USD") && !ledger.hasForeignDebtIn("EUR"), usd);
        check("one money: the note names it, from the books ('US dollars' is ForeignAccounts.MONEY_PLURAL's first), and says D$ first",
                ForeignCouponView.note(ledger).equals("in D$, from coupons written in " + ForeignAccounts.MONEY_PLURAL[0] + ", at the month's rates")
                        && ForeignCouponView.moneys(ledger).equals(List.of("US dollars")));
        String euro = DebtMoneysCheck.sell(g, "Term", "EUR", DebtMoneysCheck.askIn(g, "EUR"), 10, false);
        String yen = DebtMoneysCheck.sell(g, "Note", "JPY", DebtMoneysCheck.askIn(g, "JPY"), 12, false);
        report("premise: a euro term bond and a twelve-month yen note are sold as well", ledger.hasForeignDebtIn("EUR") && ledger.hasForeignDebtIn("JPY"), euro + " / " + yen);
        String names = ForeignAccounts.MONEY_PLURAL[ForeignAccounts.moneyIndex("USD")] + ", " + ForeignAccounts.MONEY_PLURAL[ForeignAccounts.moneyIndex("EUR")]
                + " and " + ForeignAccounts.MONEY_PLURAL[ForeignAccounts.moneyIndex("JPY")];
        String note = ForeignCouponView.note(ledger);
        check("three moneys: 'in D$, from coupons written in US dollars, euros and yen, at the month's rates' - in the eight's order, each as the model names it",
                note.equals("in D$, from coupons written in " + names + ", at the month's rates") && note.equals("in D$, from coupons written in US dollars, euros and yen, at the month's rates"));
        boolean none = true;
        for (String code : WorldMoneys.EIGHT) if (!List.of("USD", "EUR", "JPY").contains(code)) none &= !ledger.hasForeignDebtIn(code) && !note.contains(ForeignAccounts.MONEY_PLURAL[ForeignAccounts.moneyIndex(code)]);
        check("...and no money the city owes nothing in is named; 'in dollars' is not said of any of them", none && !note.contains("in dollars"));
        for (int i = 0; i < 2; i++) DebtMoneysCheck.press(g);
        double paid = g.getForeignInterestPaidThisMonth();
        report("the figure the row carries is the month's coupons in the city's own D$ (Game.getForeignInterestPaidThisMonth()): positive after a month with paper in three moneys, so the note is for a row that is drawn",
                paid > 0, String.format("%.4f", paid));
        // The screen places this note and no other.
        Path source = Path.of("src/main/java/ham/citybuildersim/ui/GovernmentScreen.java");
        if (Files.exists(source)) {
            try {
                String text = Files.readString(source);
                int at = text.indexOf("\"...of which paid abroad\"");
                String line = at < 0 ? "" : text.substring(at, text.indexOf('\n', at));
                report("the screen's row puts ForeignCouponView.note() beside the figure, and the old 'in dollars' is not said anywhere on the screen",
                        at >= 0 && line.contains("ForeignCouponView.note(ui.game.getDebtManager())") && !text.contains("\"in dollars\""), line.trim());
            } catch (java.io.IOException e) {
                check("the screen's source is readable", false);
            }
        } else {
            out.println("  (skipped: the screen's source is not beside the run)");
        }
    }

    /* =====================================================================
       7. THE BUILD CARD'S LICENCE LINE
       ===================================================================== */

    static void licenceLine() throws Exception {
        out.println("\n--- 7. the build card's licence line: a quarter of what it adds, until the patent ends - and none for what the city found itself ---");
        Game town = ResearchViewCheck.researchTown();
        BuildingsTemplate furnace = ResearchViewCheck.template(town, "Electric Arc Furnace");
        BuildingsTemplate house = ResearchViewCheck.template(town, "House");
        ResearchCatalog.Entry pe1 = ResearchCatalog.get().entry("PE1");
        int foundAt = town.getResearch().foundMonth("PE1");
        int ends = pe1.worldMonth() + LicenceFees.TERM_MONTHS;
        check("premise: the furnace is opened by PE1, a patented entry (fee) the city found itself in month " + foundAt + " ahead of history's " + pe1.worldMonth()
                + "; the 1900 city is in month " + town.getMonth(), "PE1".equals(furnace.getSinceText()) && pe1.fee() && foundAt < pe1.worldMonth() && town.foundedUnfitted());

        // Found here first: none, ever.
        ResearchView.Licence mine = ResearchView.licence(town, furnace);
        report("found here before history's date, the licence is HERE and the line says none, ever, naming the school that found it",
                mine != null && mine.kind() == ResearchView.LicenceKind.HERE && mine.perMonth() == 0 && mine.entry() == pe1
                        && ResearchView.licenceWords(mine, v -> "$" + v).equals("Licence: none. Found by " + pe1.field().school() + "; your builders pay no fee, ever.")
                        && !LicenceFees.payable("PE1", town.getMonth(), town.getResearch()),
                mine == null ? "none" : ResearchView.licenceWords(mine, v -> "$" + v));

        // The world had it first, and the patent runs: a quarter of what it adds, until it ends.
        ResearchViewCheck.setFinds(town, Map.of());
        quietly(() -> town.getMarkets().get(Good.STEEL).setWorldPrices(100 * Good.STEEL.worldImportPrice(), 100 * Good.STEEL.worldExportPrice()));
        ResearchView.Licence theirs = ResearchView.licence(town, furnace);
        double fee = LicenceFees.forNew(town, furnace);
        report("premise: found by no one the world's month is the origin and its patent is payable now, and a new furnace pays the licence a month (steel at 100 times its price causes a gain to share): LicenceFees.forNew() > 0",
                theirs != null && theirs.kind() == ResearchView.LicenceKind.UNDER && LicenceFees.payable("PE1", town.getMonth(), town.getResearch()) && fee > 0,
                String.format("fee %.4f", fee));
        java.util.function.DoubleFunction<String> money = v -> String.format("$%.2f", v);
        String line = ResearchView.licenceWords(theirs, money);
        report("from abroad and under patent the line is: 'Licence: a quarter of what it adds goes abroad until <the month history's date and the term end in>, about <LicenceFees.forNew()> a month.'",
                theirs.endsMonth() == ends && theirs.perMonth() == fee && ResearchView.shareWords().equals("a quarter") && LicenceFees.SHARE == .25
                        && line.equals("Licence: a quarter of what it adds goes abroad until " + CityCalendar.format(ends) + ", about " + money.apply(fee) + " a month."),
                line);
        check("...the term is history's date and LicenceFees.TERM_MONTHS: twenty years", LicenceFees.TERM_MONTHS == 240 && ends - pe1.worldMonth() == 240 && !CityCalendar.format(ends).isEmpty());
        String noFee = ResearchView.licenceWords(new ResearchView.Licence(ResearchView.LicenceKind.UNDER, ends, 0, pe1), money);
        check("a patent that adds nothing now leaves the amount out: 'Licence: a quarter of what it adds goes abroad until <month>.'",
                noFee.equals("Licence: a quarter of what it adds goes abroad until " + CityCalendar.format(ends) + "."));

        // The patent's last month and the one after.
        ResearchCheck.atMonth(town, ends - 1);
        ResearchView.Licence last = ResearchView.licence(town, furnace);
        ResearchCheck.atMonth(town, ends);
        ResearchView.Licence after = ResearchView.licence(town, furnace);
        report("in the patent's last month it is still UNDER, and in the month it ends (history's date plus the term) it is ENDED: 'Licence: none now. The patent ended <that month>.'",
                last != null && last.kind() == ResearchView.LicenceKind.UNDER && after != null && after.kind() == ResearchView.LicenceKind.ENDED && !LicenceFees.payable("PE1", ends, town.getResearch())
                        && ResearchView.licenceWords(after, money).equals("Licence: none now. The patent ended " + CityCalendar.format(ends) + "."),
                after == null ? "none" : ResearchView.licenceWords(after, money));

        // Found here, but after the world: the licence stops being payable.
        ResearchCheck.atMonth(town, pe1.worldMonth() + 24);
        ResearchViewCheck.setFinds(town, Map.of("PE1", pe1.worldMonth() + 12));
        ResearchView.Licence own = ResearchView.licence(town, furnace);
        report("found here twelve months after the world had it, the patent is no longer payable and the line says the city's researchers have found it themselves: OWN",
                own != null && own.kind() == ResearchView.LicenceKind.OWN && !LicenceFees.payable("PE1", town.getMonth(), town.getResearch())
                        && ResearchView.licenceWords(own, money).equals("Licence: none now. Your researchers have found it themselves, so your builders pay no fee."),
                own == null ? "none" : ResearchView.licenceWords(own, money));

        // An entry that is not patented, built into the words, and the buildings with no line.
        ResearchCatalog.Entry free = null;
        for (ResearchCatalog.Entry e : ResearchCatalog.get().entries()) if (!e.fee() && free == null) free = e;
        check("premise: the catalogue has an entry that is not patented (medical, business, law)", free != null);
        check("an unpatented entry's line is 'Licence: none. It is not patented.'",
                ResearchView.licenceWords(new ResearchView.Licence(ResearchView.LicenceKind.FREE, 0, 0, free), money).equals("Licence: none. It is not patented."));
        ResearchViewCheck.setFinds(town, Map.of());
        ResearchCheck.atMonth(town, ResearchViewCheck.CAT.entry("PE1").worldMonth() + 2);
        check("a building known at the founding (a house) has no line, nor one known by a year, nor one the catalogue's code does not date",
                ResearchView.licence(town, house) == null && ResearchView.licence(town, ResearchViewCheck.template(town, "Mixed Farm")) == null);
        Game plain = ResearchCheck.datedCity("fb13-default", false);
        check("a city founded the default way has no licence line on any card",
                !plain.foundedUnfitted() && ResearchView.licence(plain, ResearchViewCheck.template(plain, "Electric Arc Furnace")) == null);

        // The card places the model's line.
        Path source = Path.of("src/main/java/ham/citybuildersim/ui/BuildScreen.java");
        if (Files.exists(source)) {
            String text = Files.readString(source);
            check("the build card draws the line the model words, under the origin: ResearchView.licence() and ResearchView.licenceWords(), in a scope of its own",
                    text.contains("ResearchView.Licence licence = ResearchView.licence(ui.game, template);") && text.contains("Kept.scope(\"licence\", () -> licenceLine(licence))")
                            && text.contains("ResearchView.licenceWords(l, v -> money(v))") && text.indexOf("Kept.scope(\"origin\"") < text.indexOf("Kept.scope(\"licence\""));
        } else {
            out.println("  (skipped: the screen's source is not beside the run)");
        }
    }
}
