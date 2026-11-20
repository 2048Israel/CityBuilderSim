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
 *   6. THE GOVERNMENT SCREEN'S COUPONS. The row's note says D$ and, held now, the moneys the foreign paper is written in, from the debt's own
 *      books (US dollars, euros, yen), and says "in dollars" for none of them; with no foreign paper it says D$ alone; the screen's line places that note.
 *   7. THE BUILD CARD'S LICENCE LINE. It is the planners' licence, template by template (LicenceFees.rentOnNew(): the patents forNew() and
 *      forNewWork() are made of - the building's, its recipe's, a piece's - with the same fee to the bit): UNDER wherever one is due, saying the date its
 *      patent ends or how many there are and when the first and the last end; never none where the planners charge something. With none due, the
 *      building's own discovery says it: found here (none, ever), found here since the world had it, not patented, ended. A house and every card of a
 *      city founded the default way have no line.
 *   8. THE SCREENS CALL THE MODEL. A screen cannot be drawn without the toolkit, so the line of each that hands the model's link, note or licence to the
 *      words (LandScreen, MapView, UserInterface, Inbox, BuildScreen, GovernmentScreen) is held as text: putting one back fails here.
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
        screensCallTheModel();
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

        // The rule's fresh memory is read first: the screens say what the rule will do, not what a survey of their own would find.
        LandmassCheck.City u = LandmassCheck.portCity(f, "fb13-reads-rule", true);
        BridgeCheck.seam(u, isle, 12, World.SALT, 45);
        BuildAdvice.linkFor(u.g);
        int ruleMonth = u.g.getMonth();
        BridgeCheck.seam(u, isle, 14, World.SHALLOW, 10);
        quietly(() -> u.g.simulateMonths(1));
        BuildAdvice.Link ruleNow = BuildAdvice.linkFor(u.g), screenNow = BuildAdvice.islandLink(u.g, island);
        report("with the rule holding a refusal (a month old) the crossing handed to the survey since is not what either says: the rule orders terminals and so do the screens, the same answer",
                u.g.getMonth() - ruleMonth < BuildAdvice.NO_CROSSING_MONTHS && ruleNow != null && ruleNow.kind() == BuildAdvice.LinkKind.PORT && screenNow != null && screenNow.kind() == BuildAdvice.LinkKind.PORT
                        && screenNow.orders().size() == ruleNow.orders().size() && screenNow.landmass() == ruleNow.landmass(),
                ruleNow == null || screenNow == null ? "none" : ruleNow.kind() + " / " + screenNow.kind());
        quietly(() -> u.g.simulateMonths(BuildAdvice.NO_CROSSING_MONTHS));
        BuildAdvice.Link ruleLater = BuildAdvice.linkFor(u.g), screenLater = BuildAdvice.islandLink(u.g, island);
        report("...and once its memory is NO_CROSSING_MONTHS old the rule surveys again, finds the crossing, and the screens say the bridge with it",
                ruleLater != null && ruleLater.kind() == BuildAdvice.LinkKind.BRIDGE && screenLater != null && screenLater.kind() == BuildAdvice.LinkKind.BRIDGE
                        && screenLater.bridge() != null && screenLater.bridge().getId() == ruleLater.bridge().getId(),
                ruleLater == null || screenLater == null ? "none" : ruleLater.kind() + " / " + screenLater.kind());

        // Where the rule holds nothing, the screens' own memory stands for the ground it surveyed and no other.
        LandmassCheck.City t = LandmassCheck.portCity(f, "fb13-reads-ground", true);
        BridgeCheck.seam(t, isle, 12, World.SALT, 45);
        int surveyed = BuildAdvice.screenSurveys, ruleSurveys = BuildAdvice.linkSurveys;
        BuildAdvice.islandLink(t.g, island);
        check("the screens' first read of an island whose survey finds no crossing is one survey of their own, and none of the rule's", BuildAdvice.screenSurveys == surveyed + 1 && BuildAdvice.linkSurveys == ruleSurveys);
        quietly(() -> t.g.simulateMonths(3));
        BuildAdvice.islandLink(t.g, island);
        check("...three months on, with the ground as it was, the read is again and the survey is not: the memory stands for the ground", BuildAdvice.screenSurveys == surveyed + 1);
        t.buy(f.withoutShore);
        BuildAdvice.islandLink(t.g, island);
        check("...and ground bought (the land's stamp moves) is surveyed afresh, not read from a memory of ground that is gone", BuildAdvice.screenSurveys > surveyed + 1);
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
        check("one money: the note says D$ first and names the paper held now ('US dollars' is ForeignAccounts.MONEY_PLURAL's first)",
                ForeignCouponView.note(ledger).equals("in D$, at the month's rates · paper held now in " + ForeignAccounts.MONEY_PLURAL[0])
                        && ForeignCouponView.moneys(ledger).equals(List.of("US dollars")));
        String euro = DebtMoneysCheck.sell(g, "Term", "EUR", DebtMoneysCheck.askIn(g, "EUR"), 10, false);
        String yen = DebtMoneysCheck.sell(g, "Note", "JPY", DebtMoneysCheck.askIn(g, "JPY"), 12, false);
        report("premise: a euro term bond and a twelve-month yen note are sold as well", ledger.hasForeignDebtIn("EUR") && ledger.hasForeignDebtIn("JPY"), euro + " / " + yen);
        String names = ForeignAccounts.MONEY_PLURAL[ForeignAccounts.moneyIndex("USD")] + ", " + ForeignAccounts.MONEY_PLURAL[ForeignAccounts.moneyIndex("EUR")]
                + " and " + ForeignAccounts.MONEY_PLURAL[ForeignAccounts.moneyIndex("JPY")];
        String note = ForeignCouponView.note(ledger);
        check("three moneys: 'in D$, at the month's rates · paper held now in US dollars, euros and yen' - in the eight's order, each as the model names it, and saying held now (the books keep no split of the month's coupons by money)",
                note.equals("in D$, at the month's rates · paper held now in " + names) && note.equals("in D$, at the month's rates · paper held now in US dollars, euros and yen"));
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
        out.println("\n--- 7. the build card's licence line: drawn from the patents the planners' licence is made of, and none where it charges nothing ---");
        Game town = ResearchViewCheck.researchTown();
        BuildingsTemplate furnace = ResearchViewCheck.template(town, "Electric Arc Furnace");
        BuildingsTemplate house = ResearchViewCheck.template(town, "House");
        ResearchCatalog.Entry pe1 = ResearchCatalog.get().entry("PE1");
        int foundAt = town.getResearch().foundMonth("PE1");
        check("premise: the furnace is opened by PE1, a patented entry (fee) the city found itself in month " + foundAt + " ahead of history's " + pe1.worldMonth()
                + "; the 1900 city is in month " + town.getMonth(), "PE1".equals(furnace.getSinceText()) && pe1.fee() && foundAt < pe1.worldMonth() && town.foundedUnfitted());
        java.util.function.DoubleFunction<String> money = v -> String.format("$%.2f", v);
        // Steel at a hundred times its price causes a gain for a furnace to share; the furnace's own patent is the world's, the city's finds none.
        ResearchViewCheck.setFinds(town, Map.of());
        quietly(() -> town.getMarkets().get(Good.STEEL).setWorldPrices(100 * Good.STEEL.worldImportPrice(), 100 * Good.STEEL.worldExportPrice()));

        // The line is the planners' licence, template by template: UNDER exactly where the rent has a patent due, with the rent's own figure to the bit.
        int under = 0, none = 0, noLine = 0, drift = 0, wrongFee = 0, feeWhileNone = 0, noPatentUnder = 0;
        java.util.Map<ResearchView.LicenceKind, Integer> kinds = new java.util.EnumMap<>(ResearchView.LicenceKind.class);
        BuildingsTemplate one = null, several = null, yearWithPieces = null;
        int month0 = town.getMonth();
        // ...at the town's own month and at 1960's January, when more of the catalogue is known and more patents are running.
        for (int at : new int[] { month0, PolicyDates.monthOf(1960, 1) }) {
        ResearchCheck.atMonth(town, at);
        for (BuildingsTemplate t : town.getBuildingManager().getTemplates()) {
            ResearchView.Licence l = ResearchView.licence(town, t);
            LicenceFees.Rent rent = LicenceFees.rentOnNew(town, t);
            double planners = t.isOwnedBySector() ? LicenceFees.forNew(town, t)
                    : LicenceFees.forNewWork(town, t, BuildAdvice.rawRunningCostAt(town, t, 0), 1, 0);
            if (l == null) {
                noLine++;
                if (!rent.patents().isEmpty()) drift++;
                if (planners > 0) feeWhileNone++;
                continue;
            }
            kinds.merge(l.kind(), 1, Integer::sum);
            if (l.kind() == ResearchView.LicenceKind.UNDER) {
                under++;
                if (rent.patents().isEmpty() || !l.patents().equals(rent.patents())) noPatentUnder++;
                if (l.perMonth() != planners || rent.perMonth() != planners) wrongFee++;
                if (l.patents().size() == 1 && l.perMonth() > 0 && one == null) one = t;
                if (l.patents().size() > 1 && several == null) several = t;
                if (ResearchView.origin(town, t) == null && yearWithPieces == null) yearWithPieces = t;
            } else {
                none++;
                if (!rent.patents().isEmpty()) drift++;
                if (planners > 0) feeWhileNone++;
            }
        }
        }
        ResearchCheck.atMonth(town, month0);
        out.println("   the town's templates by licence, in the two months: " + kinds + ", " + noLine + " with no line");
        report("a template's line is UNDER exactly where the rent has a patent due, naming those patents, and its fee is the planners' to the bit (forNew() for a sector's building, forNewWork() for a city utility's)",
                under > 0 && noPatentUnder == 0 && wrongFee == 0 && drift == 0, under + " under patent; mismatched patents " + noPatentUnder + ", fees " + wrongFee + ", drift " + drift);
        check("...and no card says none, or has no line, where the planners' licence charges something (the old line drew the building's own entry alone)", feeWhileNone == 0);
        check("premise: the town has a template under one patent with a fee to share", one != null);

        if (one != null) {
            ResearchView.Licence l = ResearchView.licence(town, one);
            LicenceFees.Patent patent = l.patents().get(0);
            String line = ResearchView.licenceWords(l, money);
            report("one patent running: 'Licence: a quarter of what it adds goes abroad until <the month its patent ends>, about <the planners' fee> a month.' (" + one.getName() + ")",
                    patent.endsMonth() == ResearchCatalog.get().entry(patent.code()).worldMonth() + LicenceFees.TERM_MONTHS && ResearchView.shareWords().equals("a quarter") && LicenceFees.SHARE == .25
                            && line.equals("Licence: a quarter of what it adds goes abroad until " + CityCalendar.format(patent.endsMonth()) + ", about " + money.apply(l.perMonth()) + " a month."),
                    line);
        }
        if (several != null) {
            ResearchView.Licence l = ResearchView.licence(town, several);
            int first = l.patents().get(0).endsMonth(), last = l.patents().get(l.patents().size() - 1).endsMonth();
            String line = ResearchView.licenceWords(l, money);
            report("several patents running say how many and when the first and the last end, never one date for fees that end at others (" + several.getName() + ")",
                    first <= last && line.startsWith("Licence: a quarter of what it adds goes abroad on " + l.patents().size() + " patents, the first ending " + CityCalendar.format(first))
                            && (first == last || line.contains(" and the last " + CityCalendar.format(last))), line);
        } else {
            out.println("  (no template in the town is under several patents at once: the words for several are held on a record built for them, below)");
        }
        LicenceFees.Patent early = new LicenceFees.Patent("PE1", pe1.worldMonth() + LicenceFees.TERM_MONTHS), late = new LicenceFees.Patent("PE1", pe1.worldMonth() + LicenceFees.TERM_MONTHS + 100);
        String many = ResearchView.licenceWords(new ResearchView.Licence(ResearchView.LicenceKind.UNDER, List.of(early, late, late), 0, pe1), money);
        check("three patents, the first and the last ending months apart: 'on 3 patents, the first ending <first> and the last <last>', and no amount where the rent is nothing",
                many.equals("Licence: a quarter of what it adds goes abroad on 3 patents, the first ending " + CityCalendar.format(early.endsMonth()) + " and the last " + CityCalendar.format(late.endsMonth()) + "."));
        out.println("   a building known by a year with a patent due on a piece or a recipe: " + (yearWithPieces == null ? "none in this town" : yearWithPieces.getName() + " (its line is UNDER with no discovery of its own)"));

        // The building's own discovery, with no patent running anywhere on it.
        int ends = pe1.worldMonth() + LicenceFees.TERM_MONTHS;
        int month = town.getMonth();
        ResearchViewCheck.setFinds(town, Map.of("PE1", foundAt));
        ResearchView.Licence mine = ResearchView.licence(town, furnace);
        LicenceFees.Rent mineRent = LicenceFees.rentOnNew(town, furnace);
        report("found here before history's date its own patent is not due: where nothing else on the furnace is, the licence is HERE and says none, ever, naming the school; where a recipe's or a piece's is, it is UNDER with that patent and not PE1",
                mine != null && (mineRent.patents().isEmpty()
                        ? mine.kind() == ResearchView.LicenceKind.HERE && mine.perMonth() == 0 && mine.entry() == pe1
                                && ResearchView.licenceWords(mine, money).equals("Licence: none. Found by " + pe1.field().school() + "; your builders pay no fee, ever.")
                        : mine.kind() == ResearchView.LicenceKind.UNDER && mine.patents().stream().noneMatch(p -> p.code().equals("PE1"))) && !LicenceFees.payable("PE1", month, town.getResearch()),
                mine == null ? "none" : ResearchView.licenceWords(mine, money));

        // The patent's own ends.
        ResearchViewCheck.setFinds(town, Map.of());
        ResearchCheck.atMonth(town, ends - 1);
        LicenceFees.Rent lastRent = LicenceFees.rentOnNew(town, furnace);
        ResearchCheck.atMonth(town, ends);
        ResearchView.Licence after = ResearchView.licence(town, furnace);
        LicenceFees.Rent afterRent = LicenceFees.rentOnNew(town, furnace);
        report("in the patent's last month PE1 is still due on the furnace, and in the month it ends (history's date plus the term) it is not: the line is then ENDED, 'Licence: none now. The patent ended <that month>.', unless another of the furnace's patents is due",
                lastRent.patents().stream().anyMatch(p -> p.code().equals("PE1")) && afterRent.patents().stream().noneMatch(p -> p.code().equals("PE1")) && !LicenceFees.payable("PE1", ends, town.getResearch())
                        && after != null && (afterRent.patents().isEmpty()
                                ? after.kind() == ResearchView.LicenceKind.ENDED && ResearchView.licenceWords(after, money).equals("Licence: none now. The patent ended " + CityCalendar.format(ends) + ".")
                                : after.kind() == ResearchView.LicenceKind.UNDER),
                after == null ? "none" : ResearchView.licenceWords(after, money));

        // Found here, but after the world: the patent stops being due.
        ResearchCheck.atMonth(town, pe1.worldMonth() + 24);
        ResearchViewCheck.setFinds(town, Map.of("PE1", pe1.worldMonth() + 12));
        ResearchView.Licence own = ResearchView.licence(town, furnace);
        LicenceFees.Rent ownRent = LicenceFees.rentOnNew(town, furnace);
        report("found here twelve months after the world had it, PE1 is no longer due: OWN, 'Licence: none now. Your researchers have found it themselves, so your builders pay no fee.', unless another of the furnace's patents is due",
                own != null && !LicenceFees.payable("PE1", town.getMonth(), town.getResearch()) && ownRent.patents().stream().noneMatch(p -> p.code().equals("PE1"))
                        && (ownRent.patents().isEmpty()
                                ? own.kind() == ResearchView.LicenceKind.OWN && ResearchView.licenceWords(own, money).equals("Licence: none now. Your researchers have found it themselves, so your builders pay no fee.")
                                : own.kind() == ResearchView.LicenceKind.UNDER),
                own == null ? "none" : ResearchView.licenceWords(own, money));

        // An unpatented entry's building, if the catalogue opens one: licence() says FREE for it itself.
        ResearchViewCheck.setFinds(town, Map.of());
        ResearchCheck.atMonth(town, 9_999);
        BuildingsTemplate unpatented = null;
        boolean anyUnpatented = false;
        for (BuildingsTemplate t : town.getBuildingManager().getTemplates()) {
            ResearchCatalog.Entry e = ResearchCatalog.get().entry(t.getSinceText());
            if (e == null || e.fee()) continue;
            anyUnpatented = true;
            ResearchView.Licence l = ResearchView.licence(town, t);
            if (l != null && l.kind() == ResearchView.LicenceKind.FREE && unpatented == null) unpatented = t;
        }
        if (unpatented != null) {
            check("a building opened by an unpatented entry says so from licence() itself: 'Licence: none. It is not patented.' (" + unpatented.getName() + ")",
                    ResearchView.licenceWords(ResearchView.licence(town, unpatented), money).equals("Licence: none. It is not patented."));
        } else {
            out.println("   no building of the catalogue is opened by an unpatented entry with no other patent due (" + (anyUnpatented ? "some are opened by one, with a patent due on a piece or a recipe" : "none is opened by one") + "): licence() has none to say FREE of, and the words are held on a record built for them");
            check("...an unpatented entry's words: 'Licence: none. It is not patented.'",
                    ResearchView.licenceWords(new ResearchView.Licence(ResearchView.LicenceKind.FREE, List.of(), 0, ResearchCatalog.get().entries().get(0)), money).equals("Licence: none. It is not patented."));
        }
        ResearchCheck.atMonth(town, month);
        Game plain = ResearchCheck.datedCity("fb13-default", false);
        check("a building known at the founding (a house) has no line, and neither has a city founded the default way on any card",
                ResearchView.licence(town, house) == null && !plain.foundedUnfitted() && ResearchView.licence(plain, ResearchViewCheck.template(plain, "Electric Arc Furnace")) == null);
    }

    /* =====================================================================
       8. THE SCREENS CALL THE MODEL'S READS
       ===================================================================== */

    /** Whether a source file holds every one of these pieces of text (a screen cannot be drawn without the toolkit, so the line that places the model's words is read as text). */
    static boolean sourceHas(String file, String... pieces) {
        Path source = Path.of("src/main/java/ham/citybuildersim/" + file);
        if (!Files.exists(source)) {
            out.println("  (skipped: " + file + " is not beside the run)");
            return true;
        }
        try {
            String text = Files.readString(source);
            for (String piece : pieces) {
                if (!text.contains(piece)) {
                    out.println("   missing from " + file + ": " + piece);
                    return false;
                }
            }
            return true;
        } catch (java.io.IOException e) {
            return false;
        }
    }

    static void screensCallTheModel() {
        out.println("\n--- 8. each screen puts the model's words where they go: the call that hands the link to the words is the screen's line, so reverting one fails here ---");
        check("the Land tab's table is LandmassView.rows() given BuildAdvice.islandLink() for each landmass",
                sourceHas("ui/LandScreen.java", "LandmassView.rows(land, id -> BuildAdvice.islandLink(ui.game, id))"));
        check("the map's chip is LandmassView.chipLines() given the island's link",
                sourceHas("ui/MapView.java", "LandmassView.chipLines(lms, chip, BuildAdvice.islandLink(g, chip.id()))"));
        check("the notice's button label is LandmassView.noticeButton() given the link, and its door is noticeDoor()'s: the bridge's card for a bridge",
                sourceHas("ui/UserInterface.java", "LandmassView.noticeButton(game.getLandManager().getLandmasses(), island, BuildAdvice.islandLink(game, island))",
                        "LandmassView.noticeDoor(game.getLandManager().getLandmasses(), island, link)", "case BRIDGE -> buildScreen.openBridgeCardFor(island, link);",
                        "case PORT -> buildScreen.openPortCardFor(island);", "case LAND_OFFICE -> landScreen.showLandMenu();"));
        check("the notice's body is LandmassView.noticeBody() given the link, raised by the Inbox each month",
                sourceHas("Inbox.java", "LandmassView.noticeBody(lms, id, BuildAdvice.islandLink(game, id))"));
        check("the Build tab's Overview draws the link's card from BuildAdvice.linkShown() through LandmassView.linkCard(), and the not-enough-land page its bridge line and door",
                sourceHas("ui/BuildScreen.java", "LandmassView.linkCard(lms, BuildAdvice.linkShown(ui.game))", "Kept.scope(\"link\", this::linkSection)",
                        "LandmassView.noLandLines(land, advice)", "openBridgeCardFor(advice.landmass(), advice)", "ui.game.chooseBridgeLandmass(t, card.landmass())"));
        check("the Government screen's row, the build card's line and the card's wait are the model's: ForeignCouponView.note(), ResearchView.licence() and licenceWords()",
                sourceHas("ui/GovernmentScreen.java", "ForeignCouponView.note(ui.game.getDebtManager())")
                        && sourceHas("ui/BuildScreen.java", "ResearchView.Licence licence = ResearchView.licence(ui.game, template);", "Kept.scope(\"licence\", () -> licenceLine(licence))",
                                "ResearchView.licenceWords(l, v -> money(v))"));
    }
}
