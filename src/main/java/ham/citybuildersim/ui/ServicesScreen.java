package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.EnumSet;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * The services tab: the systems the city runs and how well each covers -
 * infrastructure (roads, transit, the railway, freight), safety, health,
 * education, utilities - two strips picking the system and the part of it,
 * the four figures for whichever is open, and the books each keeps.
 *
 * Split out of UserInterface on 2026-09-18: the eight banners from SERVICES to
 * THE BOOKS exactly as they were, the shell's members reached through ui. The
 * shell still reads which system and page are open (serviceArea, servicePage)
 * for the rail and the scroll memory, and the summary asks it for careCover().
 */
final class ServicesScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    ServicesScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       SERVICES - WHAT THE CITY PROVIDES, AND HOW WELL IT COVERS.

       Three systems that had no home. Healthcare was reachable only as a build
       menu, so a player could see what a clinic COSTS and never what the
       city's clinics were COVERING. The schools were behind a tuition slider on
       the policy screen. The utilities were filed under private enterprise,
       which is true of who owns them and useless to somebody asking whether
       the lights are on.

       COVERAGE IS THE FIGURE, not spending. What a city spends on clinics says
       nothing about whether anybody can see a doctor; a percentage of the
       people who need care and can get it says exactly that, and it is the
       number that moves the mortality rates.

       TWO RAILS, per Jerus. The first version was one scroll with three
       headings on it, and it showed a coverage percentage for each of nine
       things and nothing else - which is a table of contents, not a screen. The
       top strip picks the system and the second picks the part of it, and every
       part now gets the room to say what it actually does: what it is measured
       against, what it buys, what it costs, and - for the schools - which of
       the three enrolment gates is the one holding it shut.

       CONSTRUCTION IS NOT HERE. Jerus: "construction goes in the business, not
       service, in this game construction is private." It is a sector with a
       balance sheet and a borrowing rate, and it already sits on the sector
       economy screen with the mills and the mines.
       ===================================================================== */

    /** A system, and the parts of it the second strip offers. */
    record ServiceArea(String name, String[] pages) { }

    static ServiceArea[] serviceAreas() {
        return new ServiceArea[] {
            new ServiceArea("Health",
                    new String[] {"General care", "Childcare", "Senior care",
                                  "Death care", "Books"}),
            new ServiceArea("Education",
                    new String[] {"Basic ladder", "College", "University",
                                  "Professions", "Books"}),
            new ServiceArea("Utilities",
                    new String[] {"Power", "Water", "Roads", "Books"}),
            new ServiceArea("Safety",
                    new String[] {"Crime", "Police", "Prisons", "Books"}),
        };
    }

    /** Which system, and which part of it - remembered like the build category. */
    static final String SERVICE_AREA_HOME = "Health";
    String serviceArea = SERVICE_AREA_HOME;
    static final String SERVICE_HOME  = "General care";
    String servicePage = SERVICE_HOME;

    ServiceArea currentArea() {
        for (ServiceArea area : serviceAreas()) {
            if (area.name().equals(serviceArea)) return area;
        }
        return serviceAreas()[0];
    }

    /* =====================================================================
       INFRASTRUCTURE - the roads, the trams, the railway and the freight

       Jerus, 2026-09-17: "add a tab called infrastruture or transporation and
       basically it shows all the info about transportation and buses, and even
       a sub tab for specific info about the rail sector system, also in the bus
       section you can modify the price."

       NINE BATCHES SHIPPED BEFORE THIS SCREEN EXISTED, and the gap between what
       the model knew and what a player could see had got embarrassing. The road
       carried three streams and showed one number. The fare was a real dial
       with a real elasticity and NOTHING IN THE GAME COULD TURN IT. The railway
       repriced itself every month against a rule about its own capital and said
       so to nobody. A mill running at 65% because it had not taken delivery of
       its lorries yet looked, on every screen in the game, exactly like a mill
       running at 65% for no reason at all.

       FOUR PAGES, AND EACH ONE ANSWERS A QUESTION A PLAYER ACTUALLY ASKS.
       Roads: why is my throughput 73%. Transit: what would happen if I changed
       the fare. The railway: is this business worth anything to me. Freight:
       what does it cost this city to move a tonne, and who has not got the
       lorries to do it.

       WHAT IS NOT HERE. Nothing on this screen builds anything - the roads,
       the buses and the track are ordered on the Build tab like everything
       else, because a second place to buy a building is a second place to
       maintain. This is the instrument panel; the controls are the fare, which
       is a policy and has no other home.
       ===================================================================== */

    static final String[] INFRA_PAGES =
            {"Roads", "Transit", "The railway", "Freight"};

    String infraPage = "Roads";

    void showInfrastructureMenu() {
        ui.clearMenu("showInfrastructureMenu", () -> showInfrastructureMenu());

        boolean known = false;
        for (String page : INFRA_PAGES) if (page.equals(infraPage)) known = true;
        if (!known) infraPage = INFRA_PAGES[0];

        Label title = new Label("INFRASTRUCTURE");
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        switch (infraPage) {
            case "Transit"     -> transitPage(column);
            case "The railway" -> railwayPage(column);
            case "Freight"     -> freightPage(column);
            default            -> infraRoadsPage(column);
        }

        javafx.scene.layout.FlowPane strip =
                chipStrip(INFRA_PAGES, infraPage, Palette.SIZE_BODY, name -> {
                    infraPage = name;
                    ui.innerScrollAt.remove("showInfrastructureMenu:body");
                    showInfrastructureMenu();
                });
        strip.setStyle("-fx-padding: 4 0 10 0;");

        ui.rootMenu.getChildren().addAll(title, infraVitals(), strip, ui.scrolled(column, 250));
    }

    /**
     * The four figures the whole tab is about, on every page of it.
     *
     * Throughput first because it is the one that multiplies every business in
     * the city; then the two things that decide it - how many people are
     * driving and how many are not - and then the railway, because a city with
     * a railway has a completely different freight bill from one without.
     */
    HBox infraVitals() {

        InfrastructureManager roads = ui.game.getInfrastructureManager();
        ham.citybuildersim.sectors.Rail rail = ui.game.getSectors().rail();

        double ratio = roads.getThroughputRatio();
        double commuters = roads.getLoad(Traffic.COMMUTERS);
        double riding = commuters > 0 ? roads.getTransitRiders() / commuters : 0;
        double owned = roads.getCarOwnership();
        double fare = ui.game.getEconomyManager().getTaxPolicy().getTransitFare();

        return vitalsBar(
                limitCell("THE ROAD", String.format("%.0f%%", ratio * 100),
                        roads.getStatus().toLowerCase(),
                        ratio >= 1 ? Palette.GOOD
                                : ratio >= .75 ? Palette.WARN : Palette.BAD),
                limitCell("CARS", String.format("%.0f%%", owned * 100),
                        owned <= 0 ? "nobody drives"
                                : String.format("%.2fx on the commute", roads.carRoadFactor()),
                        owned > .5 ? Palette.WARN : Palette.TEXT_HEAD),
                limitCell("ON TRANSIT", String.format("%.0f%%", riding * 100),
                        roads.getTransitCapacity() <= 0 ? "nothing built"
                                : String.format("at %s a ride", unitPrice(fare)),
                        roads.getTransitCapacity() <= 0 ? Palette.TEXT_MUTED : Palette.GOOD),
                limitCell("BY RAIL", String.format("%.0f%%",
                                roads.getRailShare(Traffic.BULK) * 100),
                        rail.trackTonnes() <= 0 ? "no track"
                                : String.format("quoting %.0f%% of a lorry", rail.getQuote() * 100),
                        rail.trackTonnes() <= 0 ? Palette.TEXT_MUTED : Palette.ACCENT));
    }

    /* ---------------------------------------------------------------------
       ROADS
       --------------------------------------------------------------------- */

    void infraRoadsPage(VBox column) {

        InfrastructureManager roads = ui.game.getInfrastructureManager();

        double capacity = roads.getCapacity();
        double effective = roads.getEffectiveLoad();
        double utilisation = roads.getUtilisation();
        double ratio = roads.getThroughputRatio();

        column.getChildren().add(statementHead("What the network is getting through"));
        column.getChildren().add(leverHead(String.format("%.0f%%", ratio * 100),
                "Every business in the city multiplies its output by this, and so does "
                + "construction. It is 100% until the road is at "
                + String.format("%.0f%%", InfrastructureManager.FREE_FLOW * 100)
                + " of capacity and then falls away - traffic does not degrade in a "
                + "straight line, which is why a road that coped last month can gridlock "
                + "after one more office opens."));

        column.getChildren().add(ui.tradeScreen.bandMeter("How full the road is",
                String.format("%.0f%%", utilisation * 100),
                Math.min(1, utilisation / 1.5),
                new double[] {InfrastructureManager.FREE_FLOW / 1.5,
                              1 / 1.5, 1},
                new String[] {Palette.GOOD, Palette.WARN, Palette.BAD},
                String.format("free-flowing under %.0f%% · over capacity past 100%%",
                        InfrastructureManager.FREE_FLOW * 100),
                capacity <= 0));

        column.getChildren().add(statementHead("The network"));
        column.getChildren().add(statementLine("Streets the city has built",
                String.format("%,.0f trips", roads.getBuiltCapacity())));
        column.getChildren().add(statementLine("...and the ones that were always there",
                String.format("%,.0f trips", InfrastructureManager.BASE_CAPACITY),
                Palette.TEXT_MUTED));
        column.getChildren().add(statementTotal("Capacity",
                String.format("%,.0f trips a month", capacity), Palette.TEXT_HEAD));
        /*
         * BOTH TOTALS, AND THE RAW ONE FIRST (2026-09-17, on first looking at
         * this screen rendered). It printed the EFFECTIVE load here - 3,011 -
         * directly above a table whose trips column added to 7,164, and said
         * nothing about the difference. A player reads that as the screen
         * contradicting itself. The two numbers are the whole point of the
         * modes: what the city generates, and what is left of it once the
         * trams and the highways and the railway have taken their share.
         */
        column.getChildren().add(statementLine("Trips everything standing generates",
                String.format("%,.0f", roads.getLoad()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine(
                roads.getLoad() > effective + 1
                        ? "...and what is left for the road, after transit, highways and rail"
                        : "What is actually being asked of it",
                String.format("%,.0f", effective),
                effective > capacity ? Palette.BAD : Palette.TEXT_HEAD));
        column.getChildren().add(statementLine("Room before it starts to slow",
                String.format("%,.0f", roads.getHeadroom()),
                roads.getHeadroom() <= 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(statementLine("Of it built for lorries",
                String.format("%.0f%%", roads.getHighwayShare() * 100),
                Palette.TEXT_MUTED));

        /* ------------------------- the three streams ------------------------- */
        column.getChildren().add(statementHead("What is on it"));
        javafx.scene.layout.GridPane mix = grid(
                new double[] {150, 110, 80, 100, 100}, rightAfterFirst(5));
        gridHead(mix, "", "trips", "share", "costs", "gets through");

        int line = 1;
        for (Traffic stream : Traffic.values()) {
            double at = roads.getLoad(stream);
            mix.add(gridCell(stream.label(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            mix.add(gridCell(String.format("%,.0f", at), Palette.TEXT_HEAD,
                    Palette.SIZE_CAPTION, true), 1, line);
            mix.add(gridCell(String.format("%.0f%%", roads.getShareOf(stream) * 100),
                    Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 2, line);
            mix.add(gridCell(String.format("%.2fx", roads.roadCostOf(stream)),
                    roads.roadCostOf(stream) < 1 ? Palette.GOOD : Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 3, line);
            mix.add(gridCell(String.format("%.0f%%", roads.throughputOf(stream) * 100),
                    roads.throughputOf(stream) >= 1 ? Palette.GOOD : Palette.WARN,
                    Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        column.getChildren().add(mix);
        column.getChildren().add(statementNote(
                "\"Costs\" is what one trip of that kind asks of the street after the "
                + "highways and the railway have taken their share of it - a commuter is "
                + "always one, because a highway is built for commuters and already "
                + "counted. \"Gets through\" differs between streams only when somebody is "
                + "on a tram: a lorry in a jam is in a jam, and a clerk on a metro is not."));

        /* ----------------------------- the cars ----------------------------- */
        double owned = roads.getCarOwnership();
        double commuters = roads.getLoad(Traffic.COMMUTERS);
        double riders = roads.getTransitRiders();
        double driving = Math.max(0, commuters - riders);

        column.getChildren().add(statementHead("And the cars"));
        if (owned <= 0) {
            column.getChildren().add(statementNote(
                    "Nobody in this city owns one yet, so a commuter costs the road exactly "
                    + "one trip and this network is the network it has always been. That "
                    + "changes on its own as households get money past their cushion."));
        } else {
            column.getChildren().add(statementLine("Cars per household",
                    String.format("%.0f%%", owned * 100),
                    owned > .5 ? Palette.WARN : Palette.TEXT_HEAD));
            column.getChildren().add(statementLine("What one driving commuter now costs the road",
                    String.format("%.2f trips", roads.carRoadFactor()),
                    roads.carRoadFactor() > 2 ? Palette.BAD : Palette.WARN));
            column.getChildren().add(statementLine("Commuters still driving",
                    String.format("%,.0f of %,.0f", driving, commuters)));
            column.getChildren().add(statementLine("...costing the road",
                    String.format("%,.0f trips", driving * roads.carRoadFactor()),
                    Palette.WARN));
            column.getChildren().add(statementLine("Commuters on a tram",
                    String.format("%,.0f", riders),
                    riders > 0 ? Palette.GOOD : Palette.TEXT_MUTED));
            column.getChildren().add(statementTotal("What the road would carry with nobody driving",
                    String.format("%,.0f trips", effective - driving * (roads.carRoadFactor() - 1)),
                    Palette.GOOD));
            column.getChildren().add(statementNote(String.format(
                    "A fully motorised city asks %.1f times the commuter road a city where "
                    + "nobody drives does, and it only lands on the people who did not get "
                    + "on a tram. The two answers are more tarmac and more transit, and "
                    + "transit is the one that also stops the next household buying a car.",
                    InfrastructureManager.CAR_LOAD_AT_SATURATION)));
        }

        if (roads.isCongested()) {
            column.getChildren().add(alert("The city is congested",
                    String.format("Everything standing here is producing %.0f%% of what it "
                    + "could. Build streets, build a highway if the load is freight, or "
                    + "build transit if it is people - and look at the mix above before "
                    + "deciding which.", ratio * 100)));
        }
    }

    /* ---------------------------------------------------------------------
       TRANSIT - and the one control on this tab
       --------------------------------------------------------------------- */

    void transitPage(VBox column) {

        InfrastructureManager roads = ui.game.getInfrastructureManager();
        EconomyManager econ = ui.game.getEconomyManager();
        TaxPolicy policy = econ.getTaxPolicy();

        double commuters = roads.getLoad(Traffic.COMMUTERS);
        double stock = roads.getTransitCapacity();
        double usable = roads.getUsableTransit();
        double ceiling = commuters * InfrastructureManager.TRANSIT_MAX_SHARE;
        double riders = roads.getTransitRiders();

        double fare = policy.getTransitFare();
        double want = ui.policyScreen.staged("fare", fare);
        boolean stagedFare = ui.policyScreen.isStaged("fare");

        column.getChildren().add(statementHead("Who is on a tram"));
        column.getChildren().add(leverHead(String.format("%,.0f", riders),
                commuters > 0
                        ? String.format("commuter trips a month taken off the street - %.0f%% "
                                + "of everybody travelling to work in this city.",
                                riders / commuters * 100)
                        : "nobody is travelling to work in this city yet."));

        if (stock <= 0) {
            column.getChildren().add(statementNote(
                    "This city has built no transit at all. A Bus Network is the cheap rung "
                    + "and a Metro Line is the one that carries a city; all three are on the "
                    + "Build tab under infrastructure. Until one is standing the fare below "
                    + "charges nobody anything."));
        }

        /* -------------------- three ceilings, lowest wins -------------------- */
        column.getChildren().add(statementHead("Three ceilings, and the lowest one wins"));
        column.getChildren().add(statementLine("What the stock could carry",
                String.format("%,.0f", stock),
                usable + 1e-9 >= stock && stock > 0 ? Palette.TEXT_HEAD : Palette.TEXT_MUTED));
        column.getChildren().add(statementLine(
                String.format("...what the road under it allows (%.0fx street capacity)",
                        InfrastructureManager.TRANSIT_NEEDS_ROAD),
                String.format("%,.0f", roads.getCapacity() * InfrastructureManager.TRANSIT_NEEDS_ROAD),
                usable < stock ? Palette.WARN : Palette.TEXT_MUTED));
        column.getChildren().add(statementLine(
                String.format("...and the most any city ever rides (%.0f%% of commuters)",
                        InfrastructureManager.TRANSIT_MAX_SHARE * 100),
                String.format("%,.0f", ceiling),
                ceiling < usable ? Palette.WARN : Palette.TEXT_MUTED));
        column.getChildren().add(statementTotal("The lowest of them",
                String.format("%,.0f", Math.min(usable, ceiling)), Palette.TEXT_HEAD));
        column.getChildren().add(statementNote(
                "A city that builds a metro and no streets gets a metro nobody can reach, "
                + "and no city on earth puts everybody on public transport. Those are the "
                + "second and third lines. The first is the only one a player buys."));

        /* -------------------- and then two things walk it down -------------------- */
        column.getChildren().add(statementHead("...and then two things walk it down"));
        column.getChildren().add(statementLine("Put off by the fare",
                String.format("%.0f%% still ride", roads.getFareShare() * 100),
                roads.getFareShare() > .9 ? Palette.GOOD : Palette.WARN));
        column.getChildren().add(statementLine("Driving instead, because they own a car",
                roads.getCarOwnership() <= 0 ? "nobody owns one"
                        : String.format("%.0f%% still ride", roads.willingToRide() * 100),
                roads.getCarOwnership() <= 0 ? Palette.TEXT_MUTED
                        : roads.willingToRide() > .5 ? Palette.WARN : Palette.BAD));
        if (roads.getCarOwnership() > 0) {
            column.getChildren().add(statementNote(String.format(
                    "A household with a car in the drive does not ride on a clear morning - "
                    + "which is what makes a motorised city jam in the first place - and %.0f%% "
                    + "of them ride when nothing is moving. The commute this city remembers "
                    + "is %.0f%% of free-flowing, so %.0f%% of its drivers are on a tram. "
                    + "People drive until the road is full, then take the tram.",
                    InfrastructureManager.CAR_OWNER_RIDES_AT_GRIDLOCK * 100,
                    roads.getRememberedThroughput() * 100,
                    InfrastructureManager.CAR_OWNER_RIDES_AT_GRIDLOCK * roads.getJam() * 100)));
        }

        /* ------------------------------ the books ------------------------------ */
        column.getChildren().add(statementHead("What it costs the city"));
        column.getChildren().add(statementLine("Drivers, crews and upkeep",
                signedTight(econ.getTransitBill(), true), Palette.WARN));
        column.getChildren().add(statementLine("Fares collected",
                signedTight(econ.getTransitFares(), false), Palette.GOOD));
        column.getChildren().add(statementTotal(
                econ.getTransitNet() > 0 ? "Net cost to the city" : "Net profit to the city",
                signedTight(Math.abs(econ.getTransitNet()), econ.getTransitNet() > 0),
                econ.getTransitNet() > 0 ? Palette.WARN : Palette.GOOD));

        /* ------------------------------- the dial ------------------------------- */
        column.getChildren().add(statementHead("The fare"));
        column.getChildren().add(statementNote(
                "A FARE IS A PRICE AND NOT A CHARGE, which is what makes this different "
                + "from a clinic's fee: the person on the tram chose to be there and can "
                + "choose a car instead. The two lines above move against each other - a "
                + "fare high enough to turn a profit is a fare people will not pay, and "
                + "everybody who will not pay it is back on the road this was built to "
                + "relieve. That is the whole decision."));
        column.getChildren().add(statementLine(
                String.format("...and what a month of riding costs one commuter (%.0f journeys)",
                        TaxPolicy.JOURNEYS_A_MONTH),
                fare <= 0 ? "nothing" : money(policy.monthlyFare()),
                fare <= 0 ? Palette.TEXT_MUTED : Palette.TEXT_HEAD));
        column.getChildren().add(ui.policyScreen.stageSlider("fare", fare, 0,
                TaxPolicy.MAX_TRANSIT_FARE, TaxPolicy.MAX_TRANSIT_FARE / 20,
                r -> r <= 0 ? "free" : unitPrice(r)));

        if (stagedFare) {
            double shareThen = InfrastructureManager.ridershipAt(want);
            double ridersThen = Math.min(usable, ceiling) * shareThen * roads.willingToRide();
            double faresThen = ridersThen * want * TaxPolicy.JOURNEYS_A_MONTH;
            double netThen = econ.getTransitBill() - faresThen;
            double backOnRoad = (riders - ridersThen) * roads.carRoadFactor();

            column.getChildren().add(wouldHead());
            column.getChildren().add(wouldBe("The fare",
                    fare <= 0 ? "free" : unitPrice(fare),
                    want <= 0 ? "free" : unitPrice(want),
                    want > fare ? Palette.WARN : Palette.GOOD));
            column.getChildren().add(wouldBe("Riders",
                    String.format("%,.0f", riders), String.format("%,.0f", ridersThen),
                    ridersThen >= riders ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(wouldBe("Fares collected",
                    money(econ.getTransitFares()), money(faresThen),
                    faresThen >= econ.getTransitFares() ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(wouldTotal(
                    "Net cost to the city",
                    money(econ.getTransitNet()), money(netThen),
                    netThen <= econ.getTransitNet() ? Palette.GOOD : Palette.WARN));
            column.getChildren().add(wouldBe("...and back onto the road",
                    "", String.format("%+,.0f trips", backOnRoad),
                    backOnRoad > 0 ? Palette.BAD : Palette.GOOD));
            column.getChildren().add(statementNote(
                    "The riders line is this month's ceilings at the new fare, which is the "
                    + "honest half of the preview. The half it cannot do is the second round: "
                    + "a road that gets worse puts some of them back on the tram, and a road "
                    + "that clears takes more of them off it. Both take a few months."));
            column.getChildren().add(ui.policyScreen.applyBar(
                    want <= 0 ? "Make it free" : "Set the fare to " + unitPrice(want),
                    () -> policy.setTransitFare(want)));
        }
    }

    /* ---------------------------------------------------------------------
       THE RAILWAY - the twelfth sector, and the only mode the city does not own
       --------------------------------------------------------------------- */

    void railwayPage(VBox column) {

        ham.citybuildersim.sectors.Rail rail = ui.game.getSectors().rail();
        InfrastructureManager roads = ui.game.getInfrastructureManager();

        double track = rail.trackTonnes();
        double fleet = rail.fleet();
        double needs = rail.setsNeeded();
        double capacity = rail.getCapacityTonnes();
        double quote = rail.getQuote();

        column.getChildren().add(statementHead("What the railway is charging"));
        column.getChildren().add(leverHead(
                track <= 0 ? "no track" : String.format("%.0f%%", quote * 100),
                track <= 0
                        ? "Nobody has laid a line in this city, so every tonne that leaves "
                        + "it leaves by lorry and the whole of the freight wedge is paid to "
                        + "the world. A Rail Spur is on the Build tab."
                        : "of what a lorry would charge for the same tonne. The railway is a "
                        + "private business: it sets this itself, every month, from what the "
                        + "month cost it to run plus a return on the track it has sunk - and "
                        + "it can never charge more than a lorry, because nobody would pay."));

        if (track > 0) {
            column.getChildren().add(ui.tradeScreen.bandMeter("Where the quote sits",
                    String.format("%.0f%%", quote * 100),
                    Math.min(1, quote),
                    new double[] {ham.citybuildersim.sectors.Rail.RAIL_FLOOR, .8, 1},
                    new String[] {Palette.GOOD, Palette.ACCENT, Palette.WARN},
                    String.format("its own floor is %.0f%% · a lorry is 100%%",
                            ham.citybuildersim.sectors.Rail.RAIL_FLOOR * 100),
                    false));
        }

        /* ---------------------------- track and trains ---------------------------- */
        column.getChildren().add(statementHead("Track, and the trains to run on it"));
        column.getChildren().add(statementLine("Track standing",
                String.format("%,.0f tonnes a month", track)));
        column.getChildren().add(statementLine("Wagon sets owned",
                String.format("%,.1f", fleet),
                fleet + 1e-9 >= needs ? Palette.GOOD : Palette.WARN));
        column.getChildren().add(statementLine("...and the sets that track needs",
                String.format("%,.1f", needs), Palette.TEXT_MUTED));
        column.getChildren().add(statementTotal("What it can actually carry",
                String.format("%,.0f tonnes a month", capacity),
                capacity < track ? Palette.WARN : Palette.TEXT_HEAD));
        if (capacity < track - 1e-9) {
            column.getChildren().add(statementNote(String.format(
                    "Track with no locomotives carries nothing. This railway is holding "
                    + "%,.0f tonnes of line it cannot run, because a wagon set moves %,.0f "
                    + "tonnes a month and it is %,.1f sets short. It buys them like any "
                    + "other capital good - from a Locomotive Works if the city has one, "
                    + "and from the world if it does not.",
                    track - capacity,
                    ham.citybuildersim.sectors.Rail.TONNES_PER_SET, needs - fleet)));
        } else if (track > 0) {
            column.getChildren().add(statementNote(String.format(
                    "A set lasts %.0f years, so this fleet asks for about %,.2f replacement "
                    + "sets a month for ever - which is what makes a Locomotive Works a "
                    + "business rather than a single sale.",
                    ham.citybuildersim.sectors.Rail.SET_LIFE_MONTHS / 12,
                    fleet / ham.citybuildersim.sectors.Rail.SET_LIFE_MONTHS)));
        }

        /* ------------------------------ what it hauls ------------------------------ */
        column.getChildren().add(statementHead("What it is hauling"));
        column.getChildren().add(statementLine("The city's cross-border freight",
                String.format("%,.0f tonnes", rail.getTradeTonnes())));
        column.getChildren().add(statementLine("...of which the railway took",
                String.format("%,.0f tonnes", rail.getHauledTonnes()),
                rail.getHauledTonnes() > 0 ? Palette.GOOD : Palette.TEXT_MUTED));

        double[] carried = rail.getCarried();
        javafx.scene.layout.GridPane byStream = grid(
                new double[] {170, 120, 120, 120}, rightAfterFirst(4));
        gridHead(byStream, "", "by rail", "off the road", "still on it");
        int line = 1;
        for (Traffic stream : Traffic.values()) {
            double share = stream.ordinal() < carried.length ? carried[stream.ordinal()] : 0;
            byStream.add(gridCell(stream.label(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            byStream.add(gridCell(String.format("%.0f%%", share * 100),
                    share > 0 ? Palette.ACCENT : Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 1, line);
            byStream.add(gridCell(String.format("%.0f%%",
                            InfrastructureManager.RAIL_ROAD_RELIEF * share * 100),
                    share > 0 ? Palette.GOOD : Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 2, line);
            byStream.add(gridCell(String.format("%.2fx", roads.roadCostOf(stream)),
                    Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), 3, line);
            line++;
        }
        column.getChildren().add(byStream);
        column.getChildren().add(statementNote(String.format(
                "A tonne that leaves by train does not drive across the city to leave by "
                + "lorry - but it still gets to the siding somehow, and that is a truck. So "
                + "the relief is %.0f%% and never all of it, and a terminal carries a road "
                + "load of its own on top: a Rail Spur reads as 94%% lorries.",
                InfrastructureManager.RAIL_ROAD_RELIEF * 100)));

        /* ------------------------------ the business ------------------------------ */
        column.getChildren().add(statementHead("Whether it is earning it"));
        column.getChildren().add(statementLine("What the rule allows it to bill",
                money(rail.getAllowedRevenue()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("What it actually billed",
                money(rail.getHaulageBilled()),
                rail.getHaulageBilled() + 1e-9 >= rail.getAllowedRevenue()
                        ? Palette.GOOD : Palette.WARN));
        column.getChildren().add(statementLine("What those tonnes would have cost by lorry",
                money(rail.getTruckBill()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("Fuel",
                signedTight(rail.getFuelBill(), true), Palette.WARN));
        column.getChildren().add(statementTotal("Net income",
                signedTight(Math.abs(rail.statement().netIncome),
                        rail.statement().netIncome < 0),
                rail.statement().netIncome >= 0 ? Palette.GOOD : Palette.BAD));
        column.getChildren().add(statementLine("Track and land on its books",
                money(rail.getBuildingsValue() + rail.getLandValue()), Palette.TEXT_MUTED));

        if (track > 0 && rail.getHaulageBilled() < rail.getAllowedRevenue() * .75) {
            column.getChildren().add(alert("This railway is bigger than its city",
                    "It is allowed to charge for the track it has sunk and it cannot: "
                    + "nobody pays more than a lorry, so it pins at the ceiling and earns "
                    + "less on more capital. Over-building a railway does not make freight "
                    + "cheaper; it makes a railway that cannot pay for itself."));
        }

        column.getChildren().add(statementNote(String.format(
                "It will not lay a line it cannot fill to %.0f%%, which is why a town does "
                + "not get a spur. The full set of books is on the Sector economy tab, where "
                + "every sector's are.",
                ham.citybuildersim.sectors.Rail.MIN_LINE_UTILISATION * 100)));
    }

    /* ---------------------------------------------------------------------
       FREIGHT - the band, the bill, and the lorries
       --------------------------------------------------------------------- */

    void freightPage(VBox column) {

        InfrastructureManager roads = ui.game.getInfrastructureManager();
        ham.citybuildersim.sectors.Rail rail = ui.game.getSectors().rail();

        column.getChildren().add(statementHead("What it costs this city to move a tonne"));
        column.getChildren().add(statementNote(
                "Every traded good's price has a cost of MOVING it inside the gap between "
                + "what the world pays and what the world charges. A railway takes part of "
                + "that out of the band and bills it at home instead - so a city with good "
                + "logistics faces a narrower band on everything it trades, and that is "
                + "most of what a railway is worth."));

        /* ------------------------------- the band ------------------------------- */
        javafx.scene.layout.GridPane band = grid(
                new double[] {150, 95, 95, 95, 95}, rightAfterFirst(5));
        gridHead(band, "", "world pays", "world asks", "in the band", "delivered");

        int line = 1;
        for (Good g : Good.values()) {
            if (!g.traded() || !g.exportable() || !g.importable()) continue;
            GoodsMarket m = ui.game.getMarkets().get(g);
            double gross = m.importPrice() - m.exportPrice();
            double net = m.netImportPrice() - m.netExportPrice();
            if (!(m.importPrice() > 0)) continue;
            band.add(gridCell(g.label(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            band.add(gridCell(unitPrice(m.exportPrice()), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 1, line);
            band.add(gridCell(unitPrice(m.importPrice()), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 2, line);
            band.add(gridCell(String.format("%.0f%%", gross / m.importPrice() * 100),
                    Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), 3, line);
            /*
             * THE LAST COLUMN WAS BACKWARDS AND NEVER ONCE APPEARED (2026-09-17,
             * on first looking at this screen rendered). It was labelled "after
             * rail" and printed only when the net wedge was NARROWER than the
             * band - which it can never be. importPrice() already carries
             * freightChange(), so the band shown is ALREADY what the railway
             * left in it; the net pair adds the railway's own invoice back on
             * top. The two columns are the band and the DELIVERED cost, and the
             * second is the bigger one by exactly what the railway charges.
             */
            band.add(gridCell(net > gross + 1e-12
                            ? String.format("%.0f%%", net / m.importPrice() * 100) : "—",
                    net > gross + 1e-12 ? Palette.WARN : Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        if (line == 1) {
            column.getChildren().add(statementNote(
                    "Nothing this city trades has both a world buyer and a world seller yet, "
                    + "so there is no band to show."));
        } else {
            column.getChildren().add(band);
        }
        column.getChildren().add(statementNote(
                "\"In the band\" is how much of the world's asking price is the gap a local "
                + "buyer and a local seller both do better inside - and it is ALREADY "
                + "narrowed by whatever the railway is carrying, because that freight is no "
                + "longer paid abroad. \"Delivered\" adds the railway's own invoice back on "
                + "top, and is the figure a business actually decides on. A dash means "
                + "nothing is being railed, so the two are the same number."));

        /* ------------------------------- the bill ------------------------------- */
        column.getChildren().add(statementHead("The month's freight bill"));
        column.getChildren().add(statementLine("Cross-border tonnage",
                String.format("%,.0f tonnes", rail.getTradeTonnes())));
        column.getChildren().add(statementLine("What it would cost entirely by lorry",
                money(rail.getTruckBill()), Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("Billed by the railway, at home",
                money(rail.getHaulageBilled()),
                rail.getHaulageBilled() > 0 ? Palette.GOOD : Palette.TEXT_MUTED));
        column.getChildren().add(statementLine("...and the rest, paid abroad inside the band",
                money(Math.max(0, rail.getTruckBill() - rail.getHaulageBilled())),
                Palette.WARN));
        column.getChildren().add(statementNote(
                "The half billed at home stays in the city and is somebody's revenue; the "
                + "half inside the band leaves it. That is the difference a railway makes to "
                + "the trade balance, before it makes any difference to the road."));

        /* ------------------------------ the lorries ------------------------------ */
        column.getChildren().add(statementHead("And who has the lorries to do it"));
        javafx.scene.layout.GridPane fleets = grid(
                new double[] {150, 110, 90, 90, 90}, rightAfterFirst(5));
        gridHead(fleets, "", "tonnes/mo", "needs", "owns", "running at");

        line = 1;
        boolean anyShort = false;
        for (Sector s : ui.game.getSectors().all()) {
            double moves = s.tonnesMoved();
            if (moves <= 0 && s.vanFleet() <= 0) continue;
            double ratio = s.getVanRatio();
            if (ratio < 1) anyShort = true;
            fleets.add(gridCell(s.label(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            fleets.add(gridCell(String.format("%,.0f", moves), Palette.TEXT_HEAD,
                    Palette.SIZE_CAPTION, true), 1, line);
            fleets.add(gridCell(String.format("%,.0f", s.vansNeeded()), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 2, line);
            fleets.add(gridCell(String.format("%,.0f", s.vanFleet()),
                    s.vanFleet() + 1e-9 >= s.vansNeeded() ? Palette.TEXT_HEAD : Palette.WARN,
                    Palette.SIZE_CAPTION, true), 3, line);
            fleets.add(gridCell(String.format("%.0f%%", ratio * 100),
                    ratio >= 1 ? Palette.GOOD : Palette.WARN,
                    Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        if (line == 1) {
            column.getChildren().add(statementNote(
                    "Nothing standing in this city moves a tonne of anything yet, so nobody "
                    + "needs a lorry. The first farm, mine or mill changes that."));
        } else {
            column.getChildren().add(fleets);
        }
        column.getChildren().add(statementNote(String.format(
                "A vehicle moves about %,.0f tonnes a month and lasts %.0f years. The "
                + "tonnage is BOTH SIDES of the door - a mill that buys a hundred tonnes of "
                + "ore and ships eighty of steel runs a hundred and eighty tonnes of lorry "
                + "movements - and it is counted at nameplate, because a firm buys lorries "
                + "for the factory it has rather than for the month it is having.",
                Sector.TONNES_PER_VAN, Sector.VAN_LIFE_MONTHS / 12)));

        if (anyShort) {
            column.getChildren().add(alert("Somebody is short of lorries",
                    String.format("A sector cannot put a whole fleet on the road in a month "
                    + "- it takes about %.0f - so anything that has just built a factory is "
                    + "running below nameplate until the vehicles arrive. It is not stopped: "
                    + "a firm short of its own lorries hires haulage and sends fuller loads, "
                    + "which is worth %.0f%% of nameplate however bad it gets. If this never "
                    + "clears, the city is not making or importing enough of them.",
                    Sector.FLEET_DELIVERY_MONTHS, Sector.MIN_VAN_RATE * 100)));
        }
    }

    void showServicesStatsMenu() {
        ui.clearMenu("showServicesStatsMenu", () -> showServicesStatsMenu());

        ServiceArea area = currentArea();

        // A page name left over from another area would render nothing at all,
        // so the second strip always falls back to its own first entry.
        boolean known = false;
        for (String page : area.pages()) if (page.equals(servicePage)) known = true;
        if (!known) servicePage = area.pages()[0];

        Label title = new Label("SERVICES");
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        switch (serviceArea) {
            case "Education" -> educationPage(column);
            case "Utilities" -> utilityPage(column);
            case "Safety"    -> safetyPage(column);
            default          -> healthPage(column);
        }

        VBox strips = new VBox(Palette.GAP_TIGHT,
                chipStrip(areaNames(), serviceArea, Palette.SIZE_BODY, name -> {
                    serviceArea = name;
                    servicePage = currentAreaFor(name).pages()[0];
                    openPage();
                }),
                chipStrip(area.pages(), servicePage, Palette.SIZE_LABEL, name -> {
                    servicePage = name;
                    openPage();
                }));
        strips.setAlignment(Pos.CENTER);
        strips.setStyle("-fx-padding: 4 0 10 0;");

        /*
         * THE RAILS DO NOT SCROLL. They were inside the scroller in the first
         * version and vanished off the top of it the moment you read anything,
         * so switching from senior care to death care meant scrolling back up
         * to find the strip you had just used. Only the page moves; the four
         * figures and both strips stay where you left them.
         */
        ui.rootMenu.getChildren().addAll(title, areaVitals(), strips, ui.scrolled(column, 250));
    }

    /**
     * Draw a page the player just picked, at the top of itself.
     *
     * The scroll machinery keeps a position per screen, which is right for
     * everything it was built for - a monthly redraw should not move a screen
     * you scrolled on purpose - and wrong for a strip that swaps the whole
     * page underneath the same screen name. Opening death care three quarters
     * of the way down death care is not where anybody wants to arrive.
     */

    void openPage() {
        ui.innerScrollAt.remove(ui.currentScreen + ":body");
        showServicesStatsMenu();
    }

    static String[] areaNames() {
        ServiceArea[] areas = serviceAreas();
        String[] names = new String[areas.length];
        for (int i = 0; i < areas.length; i++) names[i] = areas[i].name();
        return names;
    }

    static ServiceArea currentAreaFor(String name) {
        for (ServiceArea area : serviceAreas()) {
            if (area.name().equals(name)) return area;
        }
        return serviceAreas()[0];
    }

    /* =====================================================================
       THE FOUR FIGURES FOR WHICHEVER SYSTEM IS OPEN.

       They stay put while the second strip moves underneath them, so drilling
       into senior care never costs you sight of the sick rate that senior care
       is one of the answers to.
       ===================================================================== */
    HBox areaVitals() {
        return switch (serviceArea) {
            case "Education" -> educationVitals();
            case "Utilities" -> utilityVitals();
            case "Safety"    -> safetyVitals();
            default          -> healthVitals();
        };
    }

    /* =====================================================================
       SAFETY (2026-09-11)

       Crime, the police and the prisons. The page a player opens to ask why
       there is crime has to answer with the reasons first, because the reasons
       are the only thing that removes it - Jerus: "if there is a reason for
       crime there is no way to actually remove it without changing the
       underlying reason." The police come second, as what they take off, and
       the prisons third, as whether the people the police catch can be held.
       ===================================================================== */

    static String crimeTone(double vsCanada) {
        return vsCanada > 1.5 ? Palette.BAD : vsCanada > 1 ? Palette.WARN : Palette.GOOD;
    }

    HBox safetyVitals() {
        Crime crime = ui.game.getCrime();
        double vs = crime.getRateVsCanada();
        return vitalsBar(
                limitCell("CRIME", people(crime.getRatePer100k()),
                        String.format("a year per 100,000 — %.1fx Canada's", vs), crimeTone(vs)),
                limitCell("POLICE COVER", String.format("%.0f%%", crime.getCoverage() * 100),
                        people(crime.getOfficers()) + " officers on shift",
                        crime.getCoverage() < .25 ? Palette.BAD
                                : crime.getCoverage() < .5 ? Palette.WARN : Palette.GOOD),
                limitCell("IN PRISON", people(crime.prisoners()),
                        crime.getNotHeld() >= .5
                                ? people(crime.getNotHeld()) + " caught and not held"
                                : "every one caught was held",
                        crime.getNotHeld() >= .5 ? Palette.WARN : Palette.TEXT_HEAD),
                limitCell("THE BILL", tightMoney(toDollars(-crime.getGrossCost())) + "/mo",
                        "police and prisons, no fees", Palette.TEXT_HEAD));
    }

    void safetyPage(VBox column) {
        switch (servicePage) {
            case "Police"  -> policePage(column);
            case "Prisons" -> prisonsPage(column);
            case "Books"   -> safetyBooksPage(column);
            default        -> crimePage(column);
        }
    }

    /** Where the crime comes from, and what it did. */
    void crimePage(VBox column) {
        Crime crime = ui.game.getCrime();

        column.getChildren().add(statementHead("Crime this month"));
        column.getChildren().add(statementLine("Crimes",
                people(crime.getCrimes()), null));
        column.getChildren().add(statementLine("...a year per 100,000 people",
                people(crime.getRatePer100k()), crimeTone(crime.getRateVsCanada())));
        column.getChildren().add(statementLine("Canada's",
                people(Crime.CANADA_CRIMES_PER_100K), Palette.TEXT_MUTED));
        column.getChildren().add(statementNote(String.format(
                "This city is at %.2f times Canada's rate. Above it, fewer people move here and"
                + " some leave.", crime.getRateVsCanada())));

        /* ------------------------------ where it comes from ------------------------------ */
        column.getChildren().add(subHead("Where it comes from"));
        javafx.scene.layout.GridPane from = grid(
                new double[] {170, 80, 64, 80}, rightAfterFirst(4));
        gridHead(from, "reason", "adults", "weight", "crimes");
        int line = 1;
        for (Crime.Cause cause : Crime.Cause.values()) {
            double crimes = crime.getCrimes(cause);
            double adults = cause.isGroup()
                    ? crime.getPressure(cause) / cause.weight()
                    : crime.getAdultsAtLiberty();
            double weight = cause.isGroup() ? cause.weight()
                    : cause.weight() * (1 - crime.getCoverage());
            if (adults < .5 && crimes < .05) continue;
            from.add(gridCell(cause.label(), Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
            from.add(gridCell(people(adults), Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 1, line);
            from.add(gridCell(String.format("%.2f", weight), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 2, line);
            from.add(gridCell(String.format("%,.1f", crimes),
                    cause == Crime.Cause.NO_CAUSE ? Palette.TEXT_MUTED : Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, true), 3, line);
            line++;
        }
        column.getChildren().add(from);
        column.getChildren().add(statementNote(
                "Every adult at liberty is counted once, at the heaviest reason they have. The"
                + " last line is not a group: it is every adult, tempted by the police the city"
                + " does not have. Police take a share off all of it; only work, homes and money"
                + " take the reasons away."));

        /* ------------------------------ what it did ------------------------------ */
        column.getChildren().add(statementHead("What it did"));
        column.getChildren().add(statementLine("Violent",
                String.format("%,.1f", crime.getViolent()), null));
        column.getChildren().add(statementLine("...injured, off work",
                String.format("%.2f%% of the city", crime.getInjuredShare() * 100), null));
        column.getChildren().add(statementLine("...killed",
                String.format("%,.2f", crime.getKilled()),
                crime.getKilled() >= .5 ? Palette.BAD : null));
        column.getChildren().add(statementLine("Property",
                String.format("%,.1f", crime.getProperty()), null));
        column.getChildren().add(bookRow("...stolen from households",
                -crime.getStolenFromHouseholds(), Palette.WARN));
        column.getChildren().add(bookRow("...stolen from businesses",
                -crime.getStolenFromBusinesses(), Palette.WARN));
        column.getChildren().add(statementNote(
                "What is stolen goes to the offenders' households. The killings are next"
                + " month's deaths among the adults; the injured are on this month's sick rate."));

        column.getChildren().add(subHead("Since the city was founded"));
        column.getChildren().add(statementLine("Crimes", people(crime.getEverCrimes()), null));
        column.getChildren().add(statementLine("Killed", people(crime.getEverKilled()), null));
        column.getChildren().add(bookRow("Stolen", -crime.getEverStolen(), null));
    }

    /** What the police are doing, and what more of them would. */
    void policePage(VBox column) {
        Crime crime = ui.game.getCrime();
        BuildingManager bm = ui.game.getBuildingManager();
        double population = crime.getPopulation();
        double full = population * Crime.FULL_OFFICERS_PER_100K / 100_000.0;

        column.getChildren().add(statementHead("The police"));
        column.getChildren().add(statementLine("Officers the buildings hold",
                people(bm.getSafetyCapacity(SafetyType.POLICE)), null));
        column.getChildren().add(statementLine("...on shift, with the staff the city has",
                people(crime.getOfficers()), null));
        column.getChildren().add(statementLine("Full coverage for this city",
                people(full), Palette.TEXT_MUTED));
        column.getChildren().add(statementTotal("Coverage",
                String.format("%.0f%%", crime.getCoverage() * 100),
                crime.getCoverage() < .25 ? Palette.BAD
                        : crime.getCoverage() < .5 ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementNote(String.format(
                "%s officers per 100,000 people. Canada has %s; full coverage is twice that.",
                people(crime.getOfficersPer100k()), people(Crime.CANADA_OFFICERS_PER_100K))));
        if (SafetyType.POLICE.foundingCapacity() > 0) {
            column.getChildren().add(statementNote(String.format(
                    "The first %s officers are the constabulary the city was founded with,"
                    + " enough for %s people.", String.format("%.1f", SafetyType.POLICE.foundingCapacity()),
                    people(Healthcare.FOUNDING_CITY))));
        }

        column.getChildren().add(statementHead("What they do"));
        double without = crime.crimesWithoutPolice();
        column.getChildren().add(statementLine("Crimes with no police at all",
                String.format("%,.1f", without), null));
        column.getChildren().add(statementLine("Crimes with these police",
                String.format("%,.1f", crime.getCrimes()), null));
        column.getChildren().add(statementTotal("Taken off",
                without > 0 ? String.format("%.0f%%", 100 * (1 - crime.getCrimes() / without)) : "—",
                Palette.GOOD));
        column.getChildren().add(statementLine("Caught and sentenced",
                String.format("%,.1f  (%.1f%% of crimes)", crime.getCaught(),
                        100 * Crime.caughtShare(crime.getCoverage())), null));

        BuildingsTemplate station = bm.getTemplateByName("Police Station");
        if (station != null && population > 0) {
            double more = Crime.coverageOf(crime.getOfficers() + station.getCapacity(), population);
            column.getChildren().add(subHead("One more police station"));
            column.getChildren().add(statementLine("Coverage",
                    String.format("%.0f%%  →  %.0f%%", crime.getCoverage() * 100, more * 100), null));
            column.getChildren().add(statementLine("Crimes a month",
                    String.format("%,.1f  →  %,.1f", crime.getCrimes(), crime.crimesAt(more)), null));
            column.getChildren().add(statementNote(
                    "Past full coverage another station adds nothing: the rest of the crime is"
                    + " the city's reasons."));
        }
    }

    /** Who is inside, and whether the city can hold who the police catch. */
    void prisonsPage(VBox column) {
        Crime crime = ui.game.getCrime();
        BuildingManager bm = ui.game.getBuildingManager();

        column.getChildren().add(statementHead("The prisons"));
        column.getChildren().add(statementLine("Cells the buildings hold",
                people(bm.getSafetyCapacity(SafetyType.PRISON)), null));
        column.getChildren().add(statementLine("...staffed",
                people(crime.getCells()), null));
        column.getChildren().add(statementTotal("In prison",
                people(crime.prisoners()), null));
        column.getChildren().add(statementNote(String.format(
                "%s per 100,000 people. Canada holds about 127.",
                people(crime.getPrisonersPer100k()))));

        column.getChildren().add(statementHead("This month"));
        column.getChildren().add(statementLine("Caught", String.format("%,.1f", crime.getCaught()), null));
        column.getChildren().add(statementLine("...sent down", String.format("%,.1f", crime.getAdmitted()), null));
        column.getChildren().add(statementLine("...caught and not held",
                String.format("%,.1f", crime.getNotHeld()),
                crime.getNotHeld() >= .5 ? Palette.WARN : null));
        column.getChildren().add(statementLine("Released, sentence served",
                String.format("%,.1f", crime.getReleased()), null));
        if (crime.getReleasedEarly() > 0.05) {
            column.getChildren().add(statementLine("Released early, no staffed cell",
                    String.format("%,.1f", crime.getReleasedEarly()), Palette.WARN));
        }
        column.getChildren().add(statementNote(
                "Anybody caught with no staffed cell free stays on the street and keeps"
                + " offending. The released go looking for work like anyone."));

        column.getChildren().add(subHead("By months served"));
        javafx.scene.layout.GridPane ring = grid(new double[] {120, 90}, rightAfterFirst(2));
        gridHead(ring, "month", "prisoners");
        for (int m = 0; m < Crime.SENTENCE_MONTHS; m++) {
            ring.add(gridCell(m == 0 ? "went in this month" : "month " + (m + 1),
                    Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, m + 1);
            ring.add(gridCell(String.format("%,.1f", crime.cohort(m)),
                    Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 1, m + 1);
        }
        column.getChildren().add(ring);

        PrisonerHousehold ledger = ui.game.getHouseholdBalance().prisoners();
        column.getChildren().add(statementHead("The prisoners' ledger"));
        column.getChildren().add(bookRow("Savings held", ledger.totalSavings(), null));
        column.getChildren().add(bookRow("Debt, frozen", -ledger.totalDebt(), null));
        column.getChildren().add(statementNote(
                "A prisoner's money waits for them: no interest runs on the debt, nothing is"
                + " invested, and it goes back out with them."));
    }

    /** What it costs. */
    void safetyBooksPage(VBox column) {
        Crime crime = ui.game.getCrime();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] wages = ui.game.getPopulationManager().getWagesPerType();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();

        column.getChildren().add(statementHead("What it costs"));
        double policePay = bm.getSafetyPayroll(SafetyType.POLICE, wages, staffing);
        double policeKeep = bm.getSafetyUpkeep(SafetyType.POLICE);
        double prisonPay = bm.getSafetyPayroll(SafetyType.PRISON, wages, staffing);
        double prisonKeep = bm.getSafetyUpkeep(SafetyType.PRISON);
        column.getChildren().add(bookRow("Police wages", -policePay, Palette.WARN));
        column.getChildren().add(bookRow("Police buildings", -policeKeep, Palette.WARN));
        column.getChildren().add(bookRow("Prison wages", -prisonPay, Palette.WARN));
        column.getChildren().add(bookRow("Prison upkeep, food included", -prisonKeep, Palette.WARN));
        column.getChildren().add(statementTotal("Cost to the city",
                tightMoney(toDollars(-crime.getGrossCost()), false), Palette.BAD));
        if (crime.getOfficers() > 0) {
            column.getChildren().add(statementNote(String.format(
                    "%s an officer on shift a year, and %s a prisoner a year.",
                    tightMoney(toDollars((policePay + policeKeep) * 12
                            / Math.max(1, crime.getOfficers() - SafetyType.POLICE.foundingCapacity())), false),
                    crime.prisoners() >= .5
                            ? tightMoney(toDollars((prisonPay + prisonKeep) * 12 / crime.prisoners()), false)
                            : "nothing")));
        }

        column.getChildren().add(statementHead("What crime cost the city's people"));
        column.getChildren().add(bookRow("Stolen from households", -crime.getStolenFromHouseholds(), null));
        column.getChildren().add(bookRow("Stolen from businesses", -crime.getStolenFromBusinesses(), null));
        column.getChildren().add(bookRow("...handed to the offenders' households", crime.getStolen(), null));
        column.getChildren().add(statementNote(
                "A theft moves money; it does not destroy it. What the businesses lose shows on"
                + " their cash flow as its own line."));
    }

    /* =====================================================================
       HEALTH
       ===================================================================== */

    /** How much of the people who need one kind of care can get it. */
    double careCover(CareType care, PopulationCohorts cohorts, double[] staffing) {
        return Health.coverageOf(
                ui.game.getBuildingManager().getStaffedCareCapacity(care, staffing),
                care.populationServed(cohorts));
    }

    HBox healthVitals() {

        Health health = ui.game.getHealth();
        Healthcare service = ui.game.getHealthcare();
        PopulationCohorts cohorts = ui.game.getCohorts();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();
        double sick = health.getSickRate();

        /* WHICH ONE IS THINNEST, and named. The old screen printed three
         * coverages and left the comparison to the player; the useful output of
         * three coverages is the smallest of them, because that is the one that
         * names a building. */
        CareType worst = CareType.GENERAL;
        double worstCover = 2;
        for (CareType care : new CareType[]{
                CareType.GENERAL, CareType.CHILDCARE, CareType.SENIOR}) {
            double cover = careCover(care, cohorts, staffing);
            if (cover < worstCover) { worstCover = cover; worst = care; }
        }

        double plots = ui.game.getBuildingManager().getCareCapacity(CareType.BURIAL);
        double monthsLeft = service.monthsOfPlotsLeft(plots);
        double net = service.getNetCost();

        return vitalsBar(
                limitCell("OFF SICK", String.format("%.1f%%", sick * 100),
                        people(ui.game.getPopulationManager().getWorkforce() * sick)
                                + " of the workforce",
                        sick > .12 ? Palette.BAD : sick > .06 ? Palette.WARN : Palette.GOOD),
                limitCell("THINNEST COVER", String.format("%.0f%%", worstCover * 100),
                        worst.getLabel().toLowerCase() + " — build that next",
                        worstCover < .5 ? Palette.BAD
                                : worstCover < .9 ? Palette.WARN : Palette.GOOD),
                service.getUnburied() > 0
                        ? limitCell("UNBURIED", people(service.getUnburied()),
                                "nowhere to put them", Palette.BAD)
                        : limitCell("GROUND LEFT",
                                monthsLeft > 900 ? "plenty"
                                        : String.format("%,.0f mo", monthsLeft),
                                "before the plots run out",
                                monthsLeft < 60 ? Palette.WARN : Palette.GOOD),
                limitCell("THE BILL", tightMoney(toDollars(-net)) + "/mo",
                        tightMoney(toDollars(service.getFees())) + " back in fees",
                        Palette.TEXT_HEAD));
    }

    void healthPage(VBox column) {
        switch (servicePage) {
            case "Childcare"   -> livingCarePage(column, CareType.CHILDCARE);
            case "Senior care" -> livingCarePage(column, CareType.SENIOR);
            case "Death care"  -> deathCarePage(column);
            case "Books"       -> healthBooksPage(column);
            default            -> livingCarePage(column, CareType.GENERAL);
        }
    }

    /**
     * One kind of care for living people: what it covers, and what that buys.
     *
     * THE SAME PAGE THREE TIMES on purpose. The three care types differ in
     * their denominator and in what they change, and in nothing else - so
     * writing them as one method makes that structure visible instead of
     * letting three copies drift apart.
     */
    void livingCarePage(VBox column, CareType care) {

        Health health = ui.game.getHealth();
        Healthcare service = ui.game.getHealthcare();
        PopulationCohorts cohorts = ui.game.getCohorts();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();

        double served = care.populationServed(cohorts);
        double built = bm.getCareCapacity(care);
        double staffed = bm.getStaffedCareCapacity(care, staffing);
        double cover = Health.coverageOf(staffed, served);

        column.getChildren().add(statementHead(care.getLabel()));
        column.getChildren().add(statementLine("Covers",
                String.format("%.0f%%", cover * 100),
                cover < .5 ? Palette.BAD : cover < .9 ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("People to serve", people(served)));
        column.getChildren().add(statementNote(
                care == CareType.CHILDCARE ? "Babies and children — the band it is measured against."
                : care == CareType.SENIOR ? "Everybody over the retirement age."
                : "Everybody in the city. General care is not means-tested or age-banded."));
        column.getChildren().add(statementLine("Places built", people(built)));
        column.getChildren().add(statementLine("Places staffed", people(staffed),
                staffed < built * .95 ? Palette.WARN : null));

        /*
         * STAFFED, NOT BUILT, and the gap is worth its own sentence. Pouring
         * concrete was the one way in this game to get output for free, and a
         * player who has built the wards and seen nothing change has no other
         * way to find out that nobody is in them.
         */
        if (built - staffed > .5) {
            column.getChildren().add(statementNote(String.format(
                    "%s places stand empty for want of staff. A ward with no doctors "
                    + "treats nobody — check the professions table on the People screen.",
                    people(built - staffed))));
        }

        column.getChildren().add(statementTotal("Short by",
                cover >= 1 ? "nothing" : people(Math.max(0, served - staffed)),
                cover >= 1 ? Palette.GOOD : Palette.BAD));

        /* ------------------------- what it buys ------------------------- */
        column.getChildren().add(subHead("What this coverage buys"));
        if (care == CareType.GENERAL) {
            /*
             * THE LONG SICK (2026-09-11). General care no longer scales the
             * adults' death rate; it saves them by curing them before they have
             * been ill two months. So what it buys is the recovery, and the
             * people it has not reached yet.
             */
            Sickness sickness = ui.game.getSickness();
            column.getChildren().add(statementLine("The sick who get better",
                    String.format("%.0f%% a month", Sickness.recovery(cover) * 100)));
            column.getChildren().add(statementNote(String.format(
                    "%.0f%% with no general care, %.0f%% with everybody covered.",
                    Sickness.RECOVERY_UNTREATED * 100, Sickness.RECOVERY_SERVED * 100)));
            double longSick = sickness.peoplePastTwoMonths(cohorts);
            column.getChildren().add(statementLine("Sick more than two months",
                    people(longSick), longSick >= 1 ? Palette.WARN : null));
            column.getChildren().add(statementLine("Died of it last month",
                    flowText(sickness.getLastDeaths()),
                    sickness.getLastDeaths() >= .5 ? Palette.BAD : null));
            column.getChildren().add(statementNote(String.format(
                    "Anyone ill for more than two months can die of it: %.0f%% a month for babies"
                    + " and seniors, %.0f%% for adults, %.0f%% for children and teenagers.",
                    Sickness.deathChance(AgeBand.BABY) * 100,
                    Sickness.deathChance(AgeBand.ADULT) * 100,
                    Sickness.deathChance(AgeBand.CHILD) * 100)));
            column.getChildren().add(statementLine("The sick rate",
                    String.format("%.1f%%", health.getSickRate() * 100)));
            column.getChildren().add(statementNote(String.format(
                    "With nobody covered it is %.0f%%; fully covered it settles near %.0f%%.",
                    Health.UNTREATED_RATE * 100, Health.WELL_SERVED_RATE * 100)));

            /*
             * THE SICK RATE, TAKEN APART. Health has held these four numbers
             * separately since it was written and no screen has ever printed
             * them: a player watching the rate climb with full hospital
             * coverage had no way at all to find out it was the unburied.
             */
            column.getChildren().add(subHead("Where the sick rate comes from"));
            column.getChildren().add(statementLine("Baseline, from coverage",
                    String.format("%.1f pts", health.getBaselineRate() * 100)));
            column.getChildren().add(statementLine("Outbreak",
                    health.isOutbreak()
                            ? String.format("%.1f pts", health.getOutbreakSeverity() * 100)
                            : "none",
                    health.isOutbreak() ? Palette.BAD : Palette.TEXT_SPENT));
            column.getChildren().add(statementLine("The unburied",
                    health.getUnburiedRate() > 0
                            ? String.format("%.1f pts", health.getUnburiedRate() * 100)
                            : "none",
                    health.getUnburiedRate() > 0 ? Palette.BAD : Palette.TEXT_SPENT));
            column.getChildren().add(statementLine("Hunger",
                    health.getHungerRate() > 0
                            ? String.format("%.1f pts", health.getHungerRate() * 100)
                            : "none",
                    health.getHungerRate() > 0 ? Palette.BAD : Palette.TEXT_SPENT));
            column.getChildren().add(statementLine("No home",
                    health.getUnhousedRate() > 0
                            ? String.format("%.1f pts", health.getUnhousedRate() * 100)
                            : "none",
                    health.getUnhousedRate() > 0 ? Palette.BAD : Palette.TEXT_SPENT));
            // ...and the injured, since the police (2026-09-11).
            column.getChildren().add(statementLine("Hurt by violent crime",
                    health.getInjuryRate() > 0
                            ? String.format("%.2f pts", health.getInjuryRate() * 100)
                            : "none",
                    health.getInjuryRate() > .0005 ? Palette.WARN : Palette.TEXT_SPENT));
            /*
             * AND WHATEVER IS LEFT OVER, said out loud.
             *
             * Health adds its four parts and caps the sum, so a breakdown that
             * does not reconcile is a breakdown that is lying. When it does not
             * add up the residual gets its own line rather than being quietly
             * absorbed into the total - which is how the missing hunger term in
             * Health's save array became visible in the first place.
             */
            double named = health.getBaselineRate() + health.getOutbreakSeverity()
                         + health.getUnburiedRate() + health.getHungerRate()
                         + health.getUnhousedRate() + health.getInjuryRate();
            double gap = health.getSickRate() - named;
            if (Math.abs(gap) > .0005) {
                column.getChildren().add(statementLine(
                        gap > 0 ? "Not accounted for" : "Trimmed by the cap",
                        String.format("%+.1f pts", gap * 100), Palette.WARN));
            }
            column.getChildren().add(statementTotal("Off sick",
                    String.format("%.1f%%", health.getSickRate() * 100),
                    health.getSickRate() > .12 ? Palette.BAD
                            : health.getSickRate() > .06 ? Palette.WARN : Palette.GOOD));
            column.getChildren().add(statementNote(
                    "They stay on the payroll and still get paid — what the city loses is "
                    + "their output, not their wages. Capped at "
                    + String.format("%.0f%%.", Health.MAX_SICK_RATE * 100)));

            if (health.isOutbreak()) {
                column.getChildren().add(alert("Outbreak", String.format(
                        "It began in %s and is adding %.1f points on top of the usual %.1f%%. "
                        + "It fades on its own over a few months. Hospitals make an outbreak "
                        + "milder rather than shorter — coverage cuts it by up to %.0f%%.",
                        CityCalendar.format(health.getOutbreakStarted()),
                        health.getOutbreakSeverity() * 100,
                        health.getBaselineRate() * 100,
                        Health.OUTBREAK_MITIGATION * 100)));
            }

        } else if (care == CareType.CHILDCARE) {
            column.getChildren().add(statementLine("Infant deaths",
                    String.format("x%.3f", Healthcare.mortalityFactor(
                            AgeBand.BABY, cover, .5, 0))));
            column.getChildren().add(statementNote(
                    "The largest single effect in the health model — an uncovered city "
                    + "loses babies at many times the rate a covered one does."));
            column.getChildren().add(statementLine("How often babies fall ill",
                    String.format("x%.2f the city", 1 + Sickness.EXTRA_SICKNESS * (1 - cover))));
            column.getChildren().add(statementNote(
                    "Babies get sick twice as often as everybody else with no childcare, and no "
                    + "more often with enough of it — and a baby ill for more than two months "
                    + "can die of it."));
            column.getChildren().add(statementLine("Births",
                    String.format("x%.2f", Healthcare.birthFactor(cover))));
            column.getChildren().add(statementNote(
                    "Nurseries make a city somewhere people are willing to have children."));
        } else {
            column.getChildren().add(statementLine("Senior deaths",
                    String.format("x%.2f", Healthcare.mortalityFactor(
                            AgeBand.SENIOR, 0, .5, cover))));
            column.getChildren().add(statementLine("How often seniors fall ill",
                    String.format("x%.2f the city", 1 + Sickness.EXTRA_SICKNESS * (1 - cover))));
            column.getChildren().add(statementLine("Draw on newcomers",
                    String.format("+%.0f%%", (Migration.seniorCarePull(cover) - 1) * 100),
                    Migration.seniorCarePull(cover) > 1 ? Palette.GOOD : null));
            column.getChildren().add(statementNote(
                    "Somewhere to grow old is a reason to move here. It is the only care "
                    + "type that pulls people rather than just keeping them alive."));
        }

        /* ----------------------------- the bill ----------------------------- */
        column.getChildren().add(subHead("What it charges"));
        column.getChildren().add(statementLine("Fee per treatment",
                cash(service.feeNow(care))));
        column.getChildren().add(statementLine("Treatment fees, all care",
                tightMoney(toDollars(service.getTreatmentFees()), false)));
        column.getChildren().add(statementLine("The service's gross cost",
                tightMoney(toDollars(service.getGrossCost()), false)));
        column.getChildren().add(statementTotal("Net cost to the city",
                tightMoney(toDollars(-service.getNetCost()), false),
                service.getNetCost() > 0 ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementNote(String.format(
                "Fees cover %.0f%% of what care costs. The city funds the rest — it runs "
                + "at a deficit by design, and the return is on the People screen rather "
                + "than in this column.", service.getCostRecovery() * 100)));

        column.getChildren().add(buildLink("Build healthcare",
                "Healthcare", EnumSet.of(BuildingType.HEALTHCARE)));
    }

    /**
     * Death care, which is the one service in the game that is a STOCK.
     *
     * A power station short of demand is short every month until somebody
     * builds another. A cemetery is completely fine right up to the month it is
     * full and then permanently useless, so the warning that matters is the one
     * that arrives before, and it is a countdown rather than a level.
     */
    void deathCarePage(VBox column) {

        Health health = ui.game.getHealth();
        Healthcare service = ui.game.getHealthcare();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();

        double plots = bm.getCareCapacity(CareType.BURIAL);
        double ovens = bm.getStaffedCareCapacity(CareType.CREMATION, staffing);
        double left = Healthcare.plotsRemaining(plots, service.getPlotsUsed());
        double monthsLeft = service.monthsOfPlotsLeft(plots);

        column.getChildren().add(statementHead("This month"));
        column.getChildren().add(statementLine("Died", flowText(service.getDeaths())));
        column.getChildren().add(statementLine("Buried", flowText(service.getBurials())));
        column.getChildren().add(statementLine("Cremated", flowText(service.getCremations())));
        column.getChildren().add(statementTotal("Dealt with",
                String.format("%.1f%%", service.getDeathCareRatio() * 100),
                service.isOverwhelmed() ? Palette.BAD
                        : service.isStrained() ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementNote(
                "A household that can save a plot's price over ten years chooses burial; "
                + "the ones that cannot are cremated. Which is to say the split is set by "
                + "how much your households have left over, not by anything on this screen."));

        /* ------------------------------ the ground ------------------------------ */
        column.getChildren().add(statementHead("The ground"));
        column.getChildren().add(statementLine("Plots built", people(plots)));
        column.getChildren().add(statementLine("Plots used", people(service.getPlotsUsed())));
        column.getChildren().add(statementLine("Left", people(left),
                service.getPlotUtilisation() > .9 ? Palette.BAD
                        : service.getPlotUtilisation() > .75 ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("Full",
                String.format("%.0f%%", service.getPlotUtilisation() * 100)));
        column.getChildren().add(statementTotal("Runs out in",
                plots <= 0 ? "no ground at all"
                        : monthsLeft > 900 ? "not for centuries"
                        : String.format("%,.0f months", monthsLeft),
                monthsLeft < Healthcare.PLOT_WARNING_MONTHS ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementNote(
                "Plots are consumed permanently — the land never comes back, and a cemetery "
                + "cannot be un-filled. This is the only capacity in the game that is spent "
                + "rather than used."));

        /* ---------------------------- the crematoria ---------------------------- */
        column.getChildren().add(statementHead("The crematoria"));
        column.getChildren().add(statementLine("Capacity",
                people(ovens) + " a month"));
        column.getChildren().add(statementLine("Used",
                String.format("%.0f%%", service.getCremationUtilisation() * 100),
                service.getCremationUtilisation() > .9 ? Palette.BAD
                        : service.getCremationUtilisation() > .75 ? Palette.WARN : null));
        column.getChildren().add(statementNote(
                "A rate rather than a stock, and it needs almost no land — which makes it "
                + "the answer when the ground has run out and the answer when it is about to."));

        /* ------------------------------- the bill ------------------------------- */
        column.getChildren().add(subHead("What it charges"));
        column.getChildren().add(statementLine("A burial", cash(service.feeNow(CareType.BURIAL))));
        column.getChildren().add(statementLine("A cremation",
                cash(service.feeNow(CareType.CREMATION))));
        column.getChildren().add(statementLine("Funeral fees this month",
                tightMoney(toDollars(service.getFuneralFees()), false)));

        if (service.getUnburied() > 0) {
            column.getChildren().add(alert("Nowhere to bury them", String.format(
                    "%s people are lying unburied, adding %.1f points to the sick rate — "
                    + "a health problem, not a decency one, and it does not go away on its "
                    + "own. %s The backlog stops growing at %d months' worth, which is a cap "
                    + "on the bookkeeping and not on the damage.",
                    people(service.getUnburied()), health.getUnburiedRate() * 100,
                    left <= 0 && plots > 0
                            ? "Every plot in the city is full, so a cemetery or a crematorium."
                            : "Build a cemetery or a crematorium.",
                    Healthcare.MAX_BACKLOG_MONTHS)));
        } else if (service.isStrained()) {
            column.getChildren().add(alert("Death care is filling up",
                    service.getCremationUtilisation() >= Healthcare.STRAINED
                        ? String.format("The crematoria are at %.0f%% of what they can handle. "
                                + "Past full, the overflow has nowhere to go but the ground.",
                                service.getCremationUtilisation() * 100)
                        : String.format("The ground runs out in about %,.0f months at this "
                                + "rate, and it is fine until the month it is not.",
                                monthsLeft)));
        }

        column.getChildren().add(buildLink("Build healthcare",
                "Healthcare", EnumSet.of(BuildingType.HEALTHCARE)));
    }

    /* =====================================================================
       EDUCATION

       The system with the most model behind it and the least of it on screen.
       Until now the whole of it was three lines - a coverage percentage, an
       enrolment count and a net cost - for a class that runs a real pipeline
       with course lengths, an affordability gate and a return-on-study
       calculation that reads live wages.

       The question these pages answer is the one the old screen could not:
       THE UNIVERSITY IS BUILT AND EMPTY, WHY. There are exactly three
       possible answers - no seats free, nobody eligible, or nobody willing -
       and every course page names which of the three is the binding one.
       ===================================================================== */

    HBox educationVitals() {

        Education schools = ui.game.getEducation();
        double basic = schools.basicCoverage();
        double inClass = sum(schools.getStudying());
        double ever = sum(schools.getEverGraduated());

        return vitalsBar(
                limitCell("BASIC LADDER", String.format("%.0f%%", basic * 100),
                        "held up by " + schools.basicBottleneck().getLabel().toLowerCase(),
                        basic < .5 ? Palette.BAD : basic < .9 ? Palette.WARN : Palette.GOOD),
                limitCell("IN CLASS", people(inClass),
                        "adults out of the workforce",
                        Palette.TEXT_HEAD),
                limitCell("TAUGHT EVER", people(ever),
                        "since the city was founded", Palette.TEXT_HEAD),
                limitCell("THE BILL",
                        tightMoney(toDollars(-schools.getNetCost())) + "/mo",
                        String.format("%.0f%% back in tuition",
                                schools.getCostRecovery() * 100),
                        Palette.TEXT_HEAD));
    }

    /**
     * True when a save has been loaded and no month has run since.
     *
     * Education carries only the dial, the running totals and the students in
     * flight; coverage, enrolment and the bill are this month's flow and are
     * rebuilt from the buildings and the pyramid on the first month back. Which
     * is a defensible saving - but it means a freshly loaded city shows a
     * hundred per cent staffed school with nobody in it and a bill of nothing,
     * and that reads as a broken screen rather than as an empty variable.
     *
     * The test is a school that exists and a bill of zero: the payroll of a
     * staffed school is never nought once a month has actually been run.
     */
    boolean educationNotRunYet() {
        double[] places = ui.game.getBuildingManager()
                .getStaffedEducationPlaces(ui.game.getPopulationManager().getJobFillRate());
        return sum(places) > 0 && ui.game.getEducation().getGrossCost() <= 0;
    }

    void educationPage(VBox column) {

        if (educationNotRunYet()) {
            column.getChildren().add(alert("Nothing measured yet",
                    "This city was loaded and no month has run since. The schools carry "
                    + "their students and their running totals through a save; coverage, "
                    + "enrolment and the bill are worked out fresh each month, so they read "
                    + "as nothing until you advance one. Press the arrow and this fills in."));
        }

        switch (servicePage) {
            case "College"     -> coursePage(column, EducationType.COLLEGE);
            case "University"  -> coursePage(column, EducationType.UNIVERSITY);
            case "Professions" -> professionsPage(column);
            case "Books"       -> educationBooksPage(column);
            default            -> basicLadderPage(column);
        }
    }

    /**
     * The three stages a child passes through, and the one holding up the rest.
     *
     * COVERAGE IS THE MINIMUM of the three, not the average - the average would
     * let a city paper over a missing high school with spare primary places,
     * which is the one thing a pipeline cannot do. So the bottleneck is both the
     * honest answer and the actionable one, because it names a building.
     */
    void basicLadderPage(VBox column) {

        Education schools = ui.game.getEducation();
        PopulationCohorts cohorts = ui.game.getCohorts();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();
        double[] staffed = bm.getStaffedEducationPlaces(staffing);
        double[] built = bm.getBuiltEducationPlaces();

        double children = cohorts.get(AgeBand.CHILD);
        double teens = cohorts.get(AgeBand.TEEN);

        column.getChildren().add(statementHead("The basic ladder"));

        javafx.scene.layout.GridPane table = grid(
                new double[] {124, 74, 74, 74, 74, 78}, rightAfterFirst(6));
        gridHead(table, "stage", "to teach", "seats", "staffed", "in class", "covered");

        EducationType[] stages = {EducationType.ELEMENTARY,
                                  EducationType.MIDDLE, EducationType.HIGH};
        double[] serves = {children * Education.ELEMENTARY_SHARE,
                           children * (1 - Education.ELEMENTARY_SHARE), teens};

        EducationType worst = schools.basicBottleneck();
        for (int i = 0; i < stages.length; i++) {
            EducationType stage = stages[i];
            double cover = schools.getCoverage(stage);
            String tone = stage == worst ? Palette.WARN : Palette.TEXT_BODY;
            table.add(gridCell(stage.getLabel(), tone, Palette.SIZE_CAPTION, false), 0, i + 1);
            table.add(gridCell(people(serves[i]), tone, Palette.SIZE_CAPTION, true), 1, i + 1);
            table.add(gridCell(people(built[stage.ordinal()]), tone,
                    Palette.SIZE_CAPTION, true), 2, i + 1);
            table.add(gridCell(people(staffed[stage.ordinal()]), tone,
                    Palette.SIZE_CAPTION, true), 3, i + 1);
            table.add(gridCell(people(schools.getEnrolled(stage)), tone,
                    Palette.SIZE_CAPTION, true), 4, i + 1);
            table.add(gridCell(String.format("%.0f%%", cover * 100),
                    cover < .5 ? Palette.BAD : cover < .9 ? Palette.WARN : Palette.GOOD,
                    Palette.SIZE_CAPTION, true), 5, i + 1);
        }
        column.getChildren().add(table);

        column.getChildren().add(statementTotal("The ladder covers",
                String.format("%.0f%%", schools.basicCoverage() * 100),
                schools.basicCoverage() < .5 ? Palette.BAD
                        : schools.basicCoverage() < .9 ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementNote(String.format(
                "The minimum of the three, not the average — %s is the one holding the rest "
                + "up, and spare places anywhere else cannot make up for it.",
                worst.getLabel().toLowerCase())));
        column.getChildren().add(statementNote(String.format(
                "Elementary and middle both teach the child band, so it is split %.0f/%.0f "
                + "between them — the real four years then three.",
                Education.ELEMENTARY_SHARE * 100, (1 - Education.ELEMENTARY_SHARE) * 100)));

        /* --------------------------- what comes out --------------------------- */
        column.getChildren().add(subHead("What comes out of it"));
        double diplomas = schools.getGraduates()[WageBand.DIPLOMA.ordinal()];
        column.getChildren().add(statementLine("New diplomas this month",
                flowText(diplomas), diplomas > 0 ? Palette.GOOD : Palette.WARN));
        column.getChildren().add(statementNote(
                "Teens age out at a steady rate and the ones who were in school leave with "
                + "a diploma. Every arrival to this city already has one — the unskilled "
                + "band is only ever your own children, so this line is the only thing that "
                + "grows it."));
        column.getChildren().add(statementLine("Diploma-holders ever taught",
                people(schools.getEverGraduated()[WageBand.DIPLOMA.ordinal()])));

        column.getChildren().add(tuitionBlock(EducationType.HIGH));
        column.getChildren().add(buildLink("Build schools",
                "Education", EnumSet.of(BuildingType.EDUCATION)));
    }

    /** One adult course, in full. */
    void coursePage(VBox column, EducationType course) {
        column.getChildren().add(courseBlock(course));
        column.getChildren().add(buildLink("Build schools",
                "Education", EnumSet.of(BuildingType.EDUCATION)));
    }

    /** The four schools that gate a job rather than raise a level. */
    void professionsPage(VBox column) {

        column.getChildren().add(statementHead("The licensed professions"));
        column.getChildren().add(sentence(
                "A band row on the People screen can say the city has eight hundred "
                + "graduates and two hundred graduate posts, and every one of those posts "
                + "still stands empty if they are doctor posts and nobody is a doctor. "
                + "These four schools are the only way to make your own — and the only "
                + "other source is migration, which brings a licence roughly one time in "
                + "ten.", Palette.TEXT_MUTED));

        for (EducationType course : EducationType.values()) {
            if (!course.isProfessional()) continue;
            column.getChildren().add(courseBlock(course));
        }
        column.getChildren().add(buildLink("Build schools",
                "Education", EnumSet.of(BuildingType.EDUCATION)));
    }

    /**
     * One adult course: the pipeline, the three gates, and the money.
     *
     * WRITTEN ONCE FOR SIX COURSES. College, university and the four
     * professional schools differ in their length, their entry requirement and
     * what they produce, and in nothing else - so one method makes that
     * structure the visible fact it is.
     */
    VBox courseBlock(EducationType course) {

        Education schools = ui.game.getEducation();
        PopulationManager pm = ui.game.getPopulationManager();
        LabourMarket market = ui.game.getLabourMarket();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] staffing = pm.getJobFillRate();

        double seats = bm.getStaffedEducationPlaces(staffing)[course.ordinal()];
        double built = bm.getBuiltEducationPlaces()[course.ordinal()];
        double[] queue = schools.cohortsInFlight(course);
        double inFlight = schools.getEnrolled(course);

        double eligible = schools.eligibleFor(course, pm);
        double willing = schools.willingShare(course, market);
        double back = schools.studyReturn(course, market);
        double afford = schools.studyAffordability(course, market);

        double free = Math.max(0, seats - inFlight);
        double wanted = eligible * willing * Education.ENROLMENT_RATE;
        double intake = Math.min(free, wanted);

        VBox block = new VBox(0);
        block.getChildren().add(statementHead(course.getLabel()));

        if (seats <= 0 && inFlight <= 0) {
            block.getChildren().add(sentence(built > 0
                    ? "The building is up and nobody is teaching in it — every seat here is "
                      + "discounted by how much of the staff turned up, and none of them did."
                    : "The city has not built one. " + (course.requires() == null ? ""
                      : "It would take " + course.requires().label().toLowerCase()
                        + "-holders as students, and the city has "
                        + people(eligible) + " of them."),
                    Palette.TEXT_MUTED));
            block.getChildren().add(buildingItWouldNeed(course, market, back, afford));
            return block;
        }

        /* ----------------------------- the pipeline ----------------------------- */
        block.getChildren().add(statementLine("Seats", people(built)));
        if (built - seats > .5) {
            block.getChildren().add(statementLine("...staffed", people(seats), Palette.WARN));
            block.getChildren().add(statementNote(
                    "A school with no teachers teaches nobody, exactly as a hospital with "
                    + "no doctors treats nobody."));
        }
        block.getChildren().add(statementLine("Course length",
                String.format("%d months  (%.0f years)",
                        course.months(), course.months() / 12.0)));
        block.getChildren().add(statementLine("In class now", people(inFlight)));
        block.getChildren().add(statementLine("Graduating next month",
                queue.length > 0 ? flowText(queue[0]) : "nobody",
                queue.length > 0 && queue[0] > 0 ? Palette.GOOD : Palette.TEXT_SPENT));

        block.getChildren().add(pipelineBars(queue));

        /* ------------------------------ the gates ------------------------------ */
        block.getChildren().add(subHead("Why that many and not more"));

        String binding = free <= wanted ? "seats" : "students";
        block.getChildren().add(statementLine("Seats free this month", people(free),
                binding.equals("seats") ? Palette.WARN : null));
        block.getChildren().add(statementLine("Who could enrol", people(eligible)));
        if (course.requires() != null) {
            block.getChildren().add(statementNote(course.isProfessional()
                    ? course.requires().label() + "-holders who do not already hold this "
                      + "licence — nobody trains twice."
                    : course.requires().label() + "-holders in the workforce."));
        }
        block.getChildren().add(statementLine("Of whom willing",
                String.format("%.1f%%", willing * 100),
                willing < .05 ? Palette.BAD : willing < .2 ? Palette.WARN : Palette.GOOD));
        block.getChildren().add(statementLine("Who start in a month",
                people(wanted),
                binding.equals("students") ? Palette.WARN : null));
        block.getChildren().add(statementNote(String.format(
                "About one in %.0f of the willing starts in any given month — a person "
                + "spends roughly five years of a working life in a position to consider "
                + "going back to study.", 1 / Education.ENROLMENT_RATE)));
        block.getChildren().add(statementTotal("Enrolling this month", people(intake),
                intake > 0 ? Palette.GOOD : Palette.BAD));
        block.getChildren().add(statementNote(binding.equals("seats")
                ? "The seats are the binding constraint: more people want in than the city "
                  + "can teach. Another school is the answer."
                : "The students are the binding constraint: there are seats going spare. "
                  + "Another school changes nothing — the two lines below are what to move."));

        /* ---------------------- the two things that move it ---------------------- */
        block.getChildren().add(subHead("What makes somebody willing"));
        block.getChildren().add(statementLine("The wage return",
                String.format("%.0f%%", back * 100),
                back < .1 ? Palette.BAD : back < .5 ? Palette.WARN : Palette.GOOD));
        block.getChildren().add(statementNote(returnNote(course, market, back)));
        block.getChildren().add(statementLine("Who can afford it",
                String.format("%.0f%%", afford * 100),
                afford < .1 ? Palette.BAD : afford < .5 ? Palette.WARN : Palette.GOOD));
        block.getChildren().add(statementNote(afford <= 0
                ? "Nobody. The un-subsidised part is more than "
                  + String.format("%.0f%%", Education.MAX_BURDEN * 100)
                  + " of what these people earn, which is the point where enrolment stops "
                  + "entirely. Raise the subsidy or raise their wages."
                : "Falls straight from everybody at no burden to nobody at "
                  + String.format("%.0f%%", Education.MAX_BURDEN * 100)
                  + " of a month's pay. This is the poverty trap, stated: a city of "
                  + "unskilled workers produces no graduates however many schools it builds."));
        block.getChildren().add(statementNote(
                "Multiplied, not averaged — either one being zero is a complete answer on "
                + "its own. A degree that pays no more than the job you have is not worth "
                + "doing at any price, and one you cannot pay for is not worth doing at "
                + "any salary."));

        block.getChildren().add(tuitionBlock(course));
        return block;
    }

    /** What a course would need, for a city that has not built one. */
    VBox buildingItWouldNeed(EducationType course, LabourMarket market,
                                     double back, double afford) {
        VBox box = new VBox(0);
        box.getChildren().add(statementLine("Course length",
                String.format("%d months", course.months())));
        box.getChildren().add(statementLine("The wage return would be",
                String.format("%.0f%%", back * 100),
                back < .1 ? Palette.BAD : back < .5 ? Palette.WARN : Palette.GOOD));
        box.getChildren().add(statementLine("Who could afford it",
                String.format("%.0f%%", afford * 100),
                afford < .1 ? Palette.BAD : afford < .5 ? Palette.WARN : Palette.GOOD));
        box.getChildren().add(statementNote(
                "Both read off live wages, so they answer the question before the money is "
                + "spent: a school built where neither of these is above zero teaches nobody."));
        return box;
    }

    /** What the wage return is actually comparing, in words. */
    String returnNote(EducationType course, LabourMarket market, double back) {
        if (back <= 0) {
            return course.isProfessional()
                    ? "The licence pays no better than the best job these graduates can "
                      + "already hold without it. Nobody studies for years to stand still."
                    : "The level above pays no better than the level these people are on. "
                      + "Nobody studies for years to stand still.";
        }
        return course.isProfessional()
                ? "Measured against the best job a graduate can hold WITHOUT the licence — "
                  + "not against the licensed salary they are studying for, which would "
                  + "compare the wage to itself. Read off live wages, so a city short of "
                  + "this profession is a city whose people are choosing it."
                : "Read off live wages, so a city short of a skill is a city whose people "
                  + "are choosing to learn it — the labour market's scarcity signal arriving "
                  + "in the classroom with nothing connecting them deliberately. A doubling "
                  + "of the return is full participation; past that the answer is already yes.";
    }

    /**
     * Everybody part way through, as a bar per month.
     *
     * THE LAG IS THE MECHANIC. A medical school built today is doctors in the
     * 2040s, and no figure says that the way a row of cohorts does: the left
     * end graduates next month, the right end enrolled this month, and the
     * gap between a full right end and an empty left one is the wait, drawn.
     */
    VBox pipelineBars(double[] queue) {

        if (queue.length == 0) return new VBox();

        double peak = 0;
        for (double cohort : queue) peak = Math.max(peak, cohort);

        HBox bars = new HBox(1);
        bars.setAlignment(Pos.BOTTOM_LEFT);
        bars.setPrefHeight(34);
        bars.setMinHeight(34);

        double each = Math.max(2, Math.min(9, (STATEMENT - 20) / queue.length - 1));
        for (double cohort : queue) {
            Region bar = new Region();
            bar.setMinWidth(each);
            bar.setPrefWidth(each);
            bar.setMaxWidth(each);
            double high = peak > 0 ? Math.max(1, cohort / peak * 32) : 1;
            bar.setMinHeight(high);
            bar.setPrefHeight(high);
            bar.setMaxHeight(high);
            bar.setStyle("-fx-background-color: "
                    + (cohort > 0 ? Palette.ACCENT : Palette.HAIRLINE) + ";");
            bars.getChildren().add(bar);
        }

        Label near = new Label("graduating next month");
        near.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Label far = new Label("just enrolled");
        far.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL));
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox ends = new HBox(near, gap, far);
        ends.setMaxWidth(STATEMENT);
        ends.setPrefWidth(STATEMENT);

        VBox box = new VBox(2, bars, ends);
        box.setMaxWidth(STATEMENT);
        box.setStyle("-fx-padding: 6 0 8 0;");
        return box;
    }

    /**
     * The price of a seat, and the dial that decides who pays it.
     *
     * READ-ONLY HERE, per Jerus - the subsidy is a policy and policies are set
     * on the policy screen. What it is doing belongs wherever its effect is
     * visible, and that is here.
     */
    VBox tuitionBlock(EducationType course) {

        Education schools = ui.game.getEducation();
        double full = schools.feeFor(course);
        double pocket = schools.outOfPocket(course);
        double subsidy = schools.getTuitionSubsidy();

        VBox box = new VBox(0);
        box.getChildren().add(subHead("What a seat costs"));
        box.getChildren().add(statementLine("Tuition", cash(full) + " a month"));
        box.getChildren().add(statementLine("The city pays",
                String.format("%.0f%%   %s", subsidy * 100, cash(full * subsidy)),
                Palette.ACCENT));
        box.getChildren().add(statementTotal("The household pays", cash(pocket),
                pocket <= 0 ? Palette.GOOD : null));
        box.getChildren().add(statementNote(
                "The subsidy is one dial for every course in the city, on the Policies "
                + "screen. At nothing, only the top pay tiers attend; at everything, "
                + "education becomes one of the largest lines on the budget."));

        Button policy = new Button("Set the subsidy  →");
        policy.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                + " -fx-background-color: " + Palette.ACCENT_FILL + ";");
        policy.setOnAction(e -> {
            ui.policyScreen.policyArea = "Promises";
            ui.policyScreen.policyPage = "Tuition";
            ui.policyScreen.dropProposal();
            ui.policyScreen.showPolicyMenu();
        });
        VBox.setMargin(policy, new javafx.geometry.Insets(4, 0, 4, 0));
        box.getChildren().add(policy);
        return box;
    }

    /* =====================================================================
       UTILITIES

       Coverage, load and what a shortage throttles. The books are not here:
       the utilities are a business with an income statement, and that is on
       the sector economy screen with the rest of them. What belongs here is
       the answer to "are the lights on", which is a different question from
       "did the power company make money" and has a different answer.
       ===================================================================== */

    HBox utilityVitals() {

        ServicesManager services = ui.game.getServicesManager();
        double power = services.getEnergyRatio();
        double water = services.getWaterRatio();
        double roads = services.getRoadRatio();
        double net = services.getServiceNetIncome();

        return vitalsBar(
                limitCell("POWER", String.format("%.0f%%", power * 100),
                        power >= 1 ? "the grid is stable" : "brownout",
                        power >= 1 ? Palette.GOOD : power > .9 ? Palette.WARN : Palette.BAD),
                limitCell("WATER", String.format("%.0f%%", water * 100),
                        water >= 1 ? "supply is adequate" : "rationing",
                        water >= 1 ? Palette.GOOD : water > .9 ? Palette.WARN : Palette.BAD),
                limitCell("ROADS", String.format("%.0f%%", roads * 100),
                        ui.game.getServicesManager().getInfrastructureManager().getStatus()
                                .toLowerCase(),
                        roads >= .9 ? Palette.GOOD : roads > .6 ? Palette.WARN : Palette.BAD),
                limitCell("THEY EARN", tightMoney(toDollars(net)) + "/mo",
                        "privately owned, city-regulated",
                        net < 0 ? Palette.BAD : Palette.TEXT_HEAD));
    }

    void utilityPage(VBox column) {
        switch (servicePage) {
            case "Water" -> waterPage(column);
            case "Roads" -> roadsPage(column);
            case "Books" -> utilityBooksPage(column);
            default      -> powerPage(column);
        }
    }

    void powerPage(VBox column) {

        UtilitiesHandler uh = ui.game.getServicesManager().getUtilitiesHandler();
        double ratio = uh.getEnergyRatio();
        double asked = uh.getConsumption();
        double most = uh.getBaseProduction();
        double now = uh.getProduction();

        column.getChildren().add(statementHead("The grid"));
        column.getChildren().add(statementLine("Supplied",
                String.format("%.1f%%", ratio * 100),
                ratio >= 1 ? Palette.GOOD : ratio > .9 ? Palette.WARN : Palette.BAD));
        column.getChildren().add(statementLine("Status",
                ratio >= 1 ? "stable" : "brownout",
                ratio >= 1 ? Palette.GOOD : Palette.BAD));
        column.getChildren().add(statementLine("Asked for",
                formatter.format(Math.round(asked)) + " W"));
        column.getChildren().add(statementLine("Generated",
                formatter.format(Math.round(now)) + " W"));
        column.getChildren().add(statementLine("Could generate",
                formatter.format(Math.round(most)) + " W"));
        column.getChildren().add(statementTotal(most >= asked ? "Spare" : "Short by",
                formatter.format(Math.round(Math.abs(most - asked))) + " W",
                most >= asked ? Palette.GOOD : Palette.BAD));
        column.getChildren().add(statementNote(
                "Generation is discounted by how much of the plant's staff turned up, so "
                + "the second figure can sit under the third with every station built."));

        column.getChildren().add(subHead("Who pays for it"));
        column.getChildren().add(statementLine("Price per watt", cash(uh.getPricerPerWatt())));
        column.getChildren().add(statementLine("Billed draw",
                formatter.format(Math.round(uh.getBilledElectricityDraw())) + " W"));
        column.getChildren().add(statementLine("Unbilled draw",
                formatter.format(Math.round(uh.getUnbilledElectricityDraw())) + " W"));
        column.getChildren().add(statementNote(
                "Only businesses are invoiced. Households have no cash account in this "
                + "model, so resident draw is real load and no revenue."));
        column.getChildren().add(statementLine("Staff turnout",
                String.format("%.0f%%", uh.getAverageUtilityFill() * 100),
                uh.getAverageUtilityFill() < .9 ? Palette.WARN : null));
        column.getChildren().add(statementNote("One workforce runs power and water both."));

        if (ratio < 1) {
            column.getChildren().add(alert("The grid is short", String.format(
                    "%s W of generation short of what the city is drawing. Every industrial "
                    + "and commercial building's output is cut in proportion — a brownout "
                    + "shows up as a smaller economy, not as a dark screen, which is why it "
                    + "can run for years unnoticed.",
                    formatter.format(Math.round(asked - now)))));
        }

        column.getChildren().add(utilityLinks(BuildingType.ELECTRICITY));
    }

    void waterPage(VBox column) {

        UtilitiesHandler uh = ui.game.getServicesManager().getUtilitiesHandler();
        double ratio = uh.getWaterRatio();
        double asked = uh.getWaterConsumption();
        double most = uh.getBaseWaterProduction();
        double now = uh.getWaterProduction();

        column.getChildren().add(statementHead("The supply"));
        column.getChildren().add(statementLine("Supplied",
                String.format("%.1f%%", ratio * 100),
                ratio >= 1 ? Palette.GOOD : ratio > .9 ? Palette.WARN : Palette.BAD));
        column.getChildren().add(statementLine("Status",
                ratio >= 1 ? "adequate" : "rationing",
                ratio >= 1 ? Palette.GOOD : Palette.BAD));

        column.getChildren().add(subHead("Where the draw comes from"));
        /* SPLITTING RESIDENT FROM BUILDING is the useful part: it is the
         * difference between "stop building housing" and "stop building food
         * plants", and no single total says which. */
        column.getChildren().add(statementLine("Residents",
                formatter.format(Math.round(uh.getResidentWaterDraw())) + " units"));
        column.getChildren().add(statementLine("Buildings",
                formatter.format(Math.round(uh.getBuildingWaterDraw())) + " units"));
        column.getChildren().add(statementTotal("Total draw",
                formatter.format(Math.round(asked)) + " units", null));
        column.getChildren().add(statementLine("Produced",
                formatter.format(Math.round(now)) + " units"));
        column.getChildren().add(statementLine("Could produce",
                formatter.format(Math.round(most)) + " units"));
        column.getChildren().add(statementTotal(most >= asked ? "Spare" : "Short by",
                formatter.format(Math.round(Math.abs(most - asked))) + " units",
                most >= asked ? Palette.GOOD : Palette.BAD));

        column.getChildren().add(subHead("Who pays for it"));
        column.getChildren().add(statementLine("Price per unit",
                cash(uh.getPricePerWaterUnit())));
        column.getChildren().add(statementLine("Billed",
                formatter.format(Math.round(uh.getBilledWaterDraw())) + " units"));
        column.getChildren().add(statementLine("Unbilled",
                formatter.format(Math.round(uh.getUnbilledWaterDraw())) + " units"));
        column.getChildren().add(statementNote(
                "Only commercial and industrial draw is invoiced; households have no cash, "
                + "so resident water is supplied and never paid for."));

        if (ratio < 1) {
            column.getChildren().add(alert("The supply is short", String.format(
                    "%s units short of the draw. Industrial and commercial output is cut in "
                    + "proportion — and note which half of the draw is growing: residents "
                    + "are housing, buildings are the plants.",
                    formatter.format(Math.round(asked - now)))));
        }

        column.getChildren().add(utilityLinks(BuildingType.WATER));
    }

    void roadsPage(VBox column) {

        InfrastructureManager roads =
                ui.game.getServicesManager().getInfrastructureManager();
        double ratio = roads.getThroughputRatio();

        column.getChildren().add(statementHead("The network"));
        column.getChildren().add(statementLine("Throughput",
                String.format("%.1f%%", ratio * 100),
                ratio >= .9 ? Palette.GOOD : ratio > .6 ? Palette.WARN : Palette.BAD));
        column.getChildren().add(statementLine("Status", roads.getStatus(),
                roads.isCongested() ? Palette.BAD
                        : roads.isStrained() ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("Trips wanted",
                formatter.format(Math.round(roads.getLoad()))));
        column.getChildren().add(statementLine("Capacity",
                formatter.format(Math.round(roads.getCapacity()))));
        column.getChildren().add(statementNote(String.format(
                "%s of that is built; the city starts with %s of dirt track it did not pay "
                + "for.", formatter.format(Math.round(roads.getBuiltCapacity())),
                formatter.format(Math.round(InfrastructureManager.BASE_CAPACITY)))));
        column.getChildren().add(statementLine("In use",
                String.format("%.0f%%", roads.getUtilisation() * 100),
                roads.getUtilisation() > 1 ? Palette.BAD
                        : roads.getUtilisation() > InfrastructureManager.STRAINED
                                ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementTotal(
                roads.getSpareCapacity() >= 0 ? "Spare" : "Over by",
                formatter.format(Math.round(Math.abs(roads.getSpareCapacity()))),
                roads.getSpareCapacity() >= 0 ? Palette.GOOD : Palette.BAD));

        column.getChildren().add(subHead("How congestion behaves"));
        column.getChildren().add(statementNote(String.format(
                "Free-flowing up to %.0f%% of capacity — below that, adding road does "
                + "nothing at all.", InfrastructureManager.FREE_FLOW * 100)));
        column.getChildren().add(statementNote(String.format(
                "Past it, throughput falls away and bottoms out at %.0f%%: a city can be "
                + "gridlocked but never at a standstill.",
                InfrastructureManager.MIN_THROUGHPUT * 100)));
        column.getChildren().add(statementNote(
                "It throttles output before it throttles anything you can see — a congested "
                + "city looks exactly like a city whose businesses are quietly earning less."));

        if (roads.isCongested()) {
            column.getChildren().add(alert("The roads are congested", String.format(
                    "Trips wanted are %.0f%% of what the network can carry, and %.0f%% of "
                    + "every business's output is not happening. Roads are the cheapest "
                    + "thing on the infrastructure list and the easiest to forget.",
                    roads.getUtilisation() * 100, (1 - ratio) * 100)));
        }

        column.getChildren().add(buildLink("Build infrastructure",
                "Infrastructure", EnumSet.of(BuildingType.INFRASTRUCTURE)));
    }

    /**
     * Build the plant. The books are a chip on the strip above rather than a
     * button down here, now that every service has a set.
     */
    javafx.scene.layout.FlowPane utilityLinks(BuildingType type) {
        javafx.scene.layout.FlowPane row = new javafx.scene.layout.FlowPane(
                Palette.GAP, Palette.GAP,
                buildLink("Build " + (type == BuildingType.WATER ? "water" : "power"),
                        "Utilities", BuildScreen.utilityTypes()));
        row.setStyle("-fx-padding: 8 0 4 0;");
        return row;
    }

    /* =====================================================================
       THE BOOKS.

       One per system, and every service in this game has a set now. They are
       written as statements rather than as the old INCOME STATEMENT blocks of
       padded Courier, and they all say the same three things in the same order:
       what came in, what went out, and what it left the city carrying.

       WHAT THESE ARE NOT is the business income statements on the sector
       screen. A hospital is not trying to make money and neither is a school -
       the useful figure is not profit, it is what share of the cost comes back
       and where the rest of it went. So the bottom line here is NET COST and
       the line under it is cost recovery, and both are stated as costs rather
       than dressed up as revenue.
       ===================================================================== */

    /** One line of a set of books: label, figure, and a colour when it matters. */
    HBox bookRow(String label, double thousands, String tone) {
        return statementLine(label, tightMoney(toDollars(thousands), false), tone);
    }

    /* --------------------------------- HEALTH --------------------------------- */

    void healthBooksPage(VBox column) {

        Healthcare service = ui.game.getHealthcare();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] wages = ui.game.getPopulationManager().getWagesPerType();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();

        column.getChildren().add(statementHead("What care charges for"));

        javafx.scene.layout.GridPane earns = grid(
                new double[] {124, 86, 82, 96}, rightAfterFirst(4));
        gridHead(earns, "care", "seen", "fee each", "raised");

        CareType[] living = {CareType.GENERAL, CareType.CHILDCARE, CareType.SENIOR};
        int line = 1;
        for (CareType care : living) {
            earns.add(gridCell(care.getLabel(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            earns.add(gridCell(people(service.getServed(care)), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, true), 1, line);
            earns.add(gridCell(cash(service.feeNow(care)), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 2, line);
            earns.add(gridCell(tightMoney(toDollars(service.feesFrom(care))),
                    Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 3, line);
            line++;
        }
        earns.add(gridCell("burials", Palette.TEXT_BODY, Palette.SIZE_CAPTION, false), 0, line);
        earns.add(gridCell(people(service.getBurials()), Palette.TEXT_BODY,
                Palette.SIZE_CAPTION, true), 1, line);
        earns.add(gridCell(cash(service.feeNow(CareType.BURIAL)), Palette.TEXT_MUTED,
                Palette.SIZE_CAPTION, true), 2, line);
        earns.add(gridCell(tightMoney(toDollars(
                service.getBurials() * service.feeNow(CareType.BURIAL))),
                Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 3, line);
        line++;
        earns.add(gridCell("cremations", Palette.TEXT_BODY,
                Palette.SIZE_CAPTION, false), 0, line);
        earns.add(gridCell(people(service.getCremations()), Palette.TEXT_BODY,
                Palette.SIZE_CAPTION, true), 1, line);
        earns.add(gridCell(cash(service.feeNow(CareType.CREMATION)), Palette.TEXT_MUTED,
                Palette.SIZE_CAPTION, true), 2, line);
        earns.add(gridCell(tightMoney(toDollars(
                service.getCremations() * service.feeNow(CareType.CREMATION))),
                Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 3, line);
        column.getChildren().add(earns);

        column.getChildren().add(bookRow("Treatment fees",
                service.getTreatmentFees(), null));
        column.getChildren().add(bookRow("Funeral fees",
                service.getFuneralFees(), null));
        column.getChildren().add(statementTotal("Fees collected",
                tightMoney(toDollars(service.getFees()), false), Palette.GOOD));

        /* ------------------------------ what it costs ------------------------------ */
        column.getChildren().add(statementHead("What it costs"));
        column.getChildren().add(bookRow("Wages", -service.getPayroll(), Palette.WARN));
        column.getChildren().add(bookRow("Buildings", -service.getUpkeep(), Palette.WARN));
        column.getChildren().add(statementTotal("Gross cost",
                tightMoney(toDollars(-service.getGrossCost()), false), null));
        column.getChildren().add(bookRow("Fees back", service.getFees(), Palette.GOOD));
        column.getChildren().add(statementTotal("Net cost to the city",
                tightMoney(toDollars(-service.getNetCost()), false),
                service.getNetCost() > 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(statementNote(String.format(
                "Fees cover %.0f%% of it. A hospital is not trying to make money — what "
                + "the rest buys is on the four pages behind this one, in deaths avoided "
                + "and work done.", service.getCostRecovery() * 100)));

        /* --------------------------- where the money goes --------------------------- */
        column.getChildren().add(subHead("Which kind of care the money goes on"));

        javafx.scene.layout.GridPane split = grid(
                new double[] {124, 78, 86, 82, 90}, rightAfterFirst(5));
        gridHead(split, "care", "places", "wages", "buildings", "net");

        line = 1;
        double namedCost = 0;
        for (CareType care : new CareType[]{CareType.GENERAL, CareType.CHILDCARE,
                                            CareType.SENIOR, CareType.BURIAL,
                                            CareType.CREMATION}) {
            double pay = bm.getCarePayroll(care, wages, staffing);
            double keep = bm.getCareUpkeep(care);
            if (pay <= 0 && keep <= 0 && bm.getCareCapacity(care) <= 0) continue;
            namedCost += pay + keep;

            double back = care == CareType.BURIAL
                    ? service.getBurials() * service.feeNow(care)
                    : care == CareType.CREMATION
                        ? service.getCremations() * service.feeNow(care)
                        : service.feesFrom(care);
            double net = pay + keep - back;

            split.add(gridCell(care.getLabel(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            split.add(gridCell(people(bm.getCareCapacity(care)), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, true), 1, line);
            split.add(gridCell(tightMoney(toDollars(pay)), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, true), 2, line);
            split.add(gridCell(tightMoney(toDollars(keep)), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, true), 3, line);
            split.add(gridCell(tightMoney(toDollars(-net)),
                    net > 0 ? Palette.BAD : Palette.GOOD, Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        column.getChildren().add(split);
        column.getChildren().add(statementNote(
                "Wages here are the posts each building holds, discounted by how many of "
                + "them are filled — the same figure the coverage pages are discounted by, "
                + "so an understaffed ward is cheap and useless at the same time."));

        /*
         * THE ENDOWMENT, which has no building and therefore no cost. Worth a
         * line, because a player reading this table and adding it up will be
         * short of the gross cost by exactly the founding capacity's share and
         * will go looking for the missing money.
         */
        double gap = service.getGrossCost() - namedCost;
        if (Math.abs(toDollars(gap)) > 1) {
            column.getChildren().add(statementLine("Not in the table above",
                    tightMoney(toDollars(gap), false), Palette.TEXT_MUTED));
            column.getChildren().add(statementNote(
                    "Healthcare buildings that provide no care type of their own, and the "
                    + "doctor, nursery, almshouse and churchyard the city was founded with "
                    + "— those have capacity and no payroll, which is why the coverage "
                    + "pages count them and this table does not."));
        }
    }

    /* -------------------------------- EDUCATION -------------------------------- */

    void educationBooksPage(VBox column) {

        Education schools = ui.game.getEducation();
        BuildingManager bm = ui.game.getBuildingManager();
        double[] wages = ui.game.getPopulationManager().getWagesPerType();
        double[] staffing = ui.game.getPopulationManager().getJobFillRate();
        double subsidy = schools.getTuitionSubsidy();

        column.getChildren().add(statementHead("What tuition raises"));

        javafx.scene.layout.GridPane fees = grid(
                new double[] {130, 74, 74, 84, 84}, rightAfterFirst(5));
        gridHead(fees, "course", "in class", "a seat", "households", "the city");

        int line = 1;
        double billed = 0;
        for (EducationType course : EducationType.values()) {
            if (course == EducationType.NONE) continue;
            double students = schools.getEnrolled(course);
            if (students < .5) continue;
            double charge = schools.feeFor(course) * students;
            billed += charge;

            fees.add(gridCell(course.getLabel(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            fees.add(gridCell(people(students), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, true), 1, line);
            fees.add(gridCell(cash(schools.feeFor(course)), Palette.TEXT_MUTED,
                    Palette.SIZE_CAPTION, true), 2, line);
            fees.add(gridCell(tightMoney(toDollars(charge * (1 - subsidy))),
                    Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 3, line);
            fees.add(gridCell(tightMoney(toDollars(charge * subsidy)),
                    Palette.ACCENT, Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        if (line == 1) {
            column.getChildren().add(sentence(
                    "Nobody is in a classroom this month, so nothing was billed.",
                    Palette.TEXT_MUTED));
        } else {
            column.getChildren().add(fees);
        }

        column.getChildren().add(bookRow("Billed in tuition", billed, null));
        column.getChildren().add(bookRow("Households paid",
                schools.getFees(), Palette.GOOD));
        column.getChildren().add(bookRow("The city forgave",
                schools.getSubsidy(), Palette.ACCENT));
        column.getChildren().add(statementNote(String.format(
                "The subsidy dial is at %.0f%%, so that is the split. It is one dial for "
                + "every course in the city.", subsidy * 100)));

        /* ------------------------------ what it costs ------------------------------ */
        column.getChildren().add(statementHead("What it costs"));
        column.getChildren().add(bookRow("Teachers", -schools.getPayroll(), Palette.WARN));
        column.getChildren().add(bookRow("Buildings", -schools.getUpkeep(), Palette.WARN));
        column.getChildren().add(bookRow("Tuition the city covers",
                -schools.getSubsidy(), Palette.WARN));
        column.getChildren().add(statementTotal("Gross cost",
                tightMoney(toDollars(-schools.getGrossCost()), false), null));
        column.getChildren().add(bookRow("Tuition collected", schools.getFees(),
                Palette.GOOD));
        column.getChildren().add(statementTotal("Net cost to the city",
                tightMoney(toDollars(-schools.getNetCost()), false),
                schools.getNetCost() > 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(statementNote(String.format(
                "Tuition covers %.0f%% of it.", schools.getCostRecovery() * 100)));

        /*
         * SAID OUT LOUD, because the shape of this statement is unusual and a
         * player checking the arithmetic will think the screen is wrong.
         *
         * The city pays the teachers and the buildings AND counts its share of
         * the tuition as a second expense - so a subsidised seat is charged to
         * the treasury twice, once as the school's payroll and once as the
         * forgiven fee. Whether that is intended is a model question and not
         * mine; what the books can do is make it visible instead of burying it
         * in a net figure.
         */
        column.getChildren().add(alert("The subsidy is counted twice", String.format(
                "The city already pays the teachers and the upkeep, and this statement then "
                + "charges it %s more for the share of tuition it forgives. So the net cost "
                + "reads %s rather than the %s that wages and buildings less what households "
                + "actually paid would give. It is how Education.getGrossCost() is written; "
                + "flagging it rather than touching it.",
                tightMoney(toDollars(schools.getSubsidy())),
                tightMoney(toDollars(schools.getNetCost())),
                tightMoney(toDollars(schools.getPayroll() + schools.getUpkeep()
                        - schools.getFees())))));

        /* --------------------------- where the money goes --------------------------- */
        column.getChildren().add(subHead("Which schools the money goes on"));

        javafx.scene.layout.GridPane split = grid(
                new double[] {130, 74, 86, 82, 90}, rightAfterFirst(5));
        gridHead(split, "course", "seats", "teachers", "buildings", "net");

        line = 1;
        for (EducationType course : EducationType.values()) {
            if (course == EducationType.NONE) continue;
            double pay = bm.getSchoolPayroll(course, wages, staffing);
            double keep = bm.getSchoolUpkeep(course);
            if (pay <= 0 && keep <= 0) continue;

            double back = schools.feeFor(course)
                    * schools.getEnrolled(course) * (1 - subsidy);
            double net = pay + keep - back;

            split.add(gridCell(course.getLabel(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            split.add(gridCell(people(bm.getBuiltEducationPlaces()[course.ordinal()]),
                    Palette.TEXT_BODY, Palette.SIZE_CAPTION, true), 1, line);
            split.add(gridCell(tightMoney(toDollars(pay)), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, true), 2, line);
            split.add(gridCell(tightMoney(toDollars(keep)), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, true), 3, line);
            split.add(gridCell(tightMoney(toDollars(-net)),
                    net > 0 ? Palette.BAD : Palette.GOOD, Palette.SIZE_CAPTION, true), 4, line);
            line++;
        }
        if (line > 1) column.getChildren().add(split);
        column.getChildren().add(statementNote(
                "A course with seats and no students still costs its teachers and its "
                + "upkeep every month — which is what makes a school built in the wrong "
                + "city expensive rather than merely idle."));
    }

    /* -------------------------------- UTILITIES -------------------------------- */

    void utilityBooksPage(VBox column) {

        UtilitiesHandler uh = ui.game.getServicesManager().getUtilitiesHandler();
        BuildingManager bm = ui.game.getBuildingManager();

        double elecSales = uh.getElectricityRevenue();
        double elecPay = uh.getElectricityPayroll();
        double waterSales = uh.getWaterRevenue();
        double waterPay = uh.getWaterPayroll();
        /*
         * THE ROADS' REPAIR BILL, which until 2026-09-09 could only ever read
         * ZERO. This asked BuildingManager for the INFRASTRUCTURE category's
         * `upkeep`, and no road has ever had an upkeep figure - the field was
         * only ever filled in for healthcare and education. So the Services
         * screen carried a line that was structurally incapable of being
         * non-zero, which is worse than not carrying it: a player reads $0 and
         * concludes roads are free to keep.
         *
         * They are not, and now the model says so. This is the real charge,
         * from the same per-category maintenance the whole city pays.
         */
        double roadUpkeep = ui.game.getEconomyManager().maintenanceBillFor(
                BuildingType.INFRASTRUCTURE,
                Math.max(0, ui.game.getBuildingManager().getConstructionMaterialPrice()));

        /*
         * A BUSINESS, unlike the two above it. The utilities sell what they
         * make and the figure at the bottom is profit rather than net cost -
         * which is why this page reads the other way up, and why it is the only
         * one of the three whose bottom line can be green.
         */
        column.getChildren().add(statementHead("Electric power"));
        column.getChildren().add(bookRow("Sales", elecSales, Palette.GOOD));
        column.getChildren().add(bookRow("Wages", -elecPay, Palette.WARN));
        column.getChildren().add(statementTotal("Net income",
                tightMoney(toDollars(elecSales - elecPay), false),
                elecSales - elecPay < 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(statementNote(String.format(
                "%s W billed at %s a watt. The other %s W is resident draw, which is real "
                + "load and no revenue.",
                formatter.format(Math.round(uh.getBilledElectricityDraw())),
                cash(uh.getPricerPerWatt()),
                formatter.format(Math.round(uh.getUnbilledElectricityDraw())))));

        column.getChildren().add(statementHead("Water"));
        column.getChildren().add(bookRow("Sales", waterSales, Palette.GOOD));
        column.getChildren().add(bookRow("Wages", -waterPay, Palette.WARN));
        column.getChildren().add(statementTotal("Net income",
                tightMoney(toDollars(waterSales - waterPay), false),
                waterSales - waterPay < 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(statementNote(String.format(
                "%s of %s units billed. Households have no cash account in this model, so "
                + "four fifths of the water leaves unpaid for.",
                formatter.format(Math.round(uh.getBilledWaterDraw())),
                formatter.format(Math.round(uh.getWaterConsumption())))));

        column.getChildren().add(statementHead("Both together"));
        column.getChildren().add(bookRow("Revenue", elecSales + waterSales, null));
        column.getChildren().add(bookRow("Payroll", -(elecPay + waterPay), null));
        column.getChildren().add(statementTotal("Net income",
                tightMoney(toDollars(
                        ui.game.getServicesManager().getServiceNetIncome()), false),
                ui.game.getServicesManager().getServiceNetIncome() < 0
                        ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(statementLine("Staff turnout",
                String.format("%.0f%%", uh.getAverageUtilityFill() * 100),
                uh.getAverageUtilityFill() < .9 ? Palette.WARN : null));
        column.getChildren().add(statementNote(
                "One workforce runs both, so they cannot be short-staffed independently."));

        /* ---------------------------------- roads ---------------------------------- */
        column.getChildren().add(statementHead("Roads"));
        column.getChildren().add(bookRow("Revenue", 0, Palette.TEXT_SPENT));
        column.getChildren().add(bookRow("Upkeep", -roadUpkeep, Palette.WARN));
        column.getChildren().add(statementTotal("Net cost to the city",
                tightMoney(toDollars(-roadUpkeep), false),
                roadUpkeep > 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(statementNote(roadUpkeep > 0
                ? "Nobody is tolled. Roads are the one utility the city owns outright and "
                  + "the only one that can never pay for itself — what it buys is on the "
                  + "roads page, in output that happens instead of not happening."
                : "Nobody is tolled, and no road in the catalogue carries an upkeep cost at "
                  + "all — the network is bought once and runs free for ever after. Which "
                  + "is not quite true even inside this model: every road draws power, so "
                  + "its running cost is real and lands on the grid's bill above rather "
                  + "than on this line."));

        column.getChildren().add(statementNote(
                "These are the utilities' own books. They are a private business the city "
                + "regulates rather than a service it runs, which is why this page has a "
                + "profit at the bottom and the other two have a net cost."));
    }

    /** A button that goes straight to a category of the build list. */
    Button buildLink(String label, String category, EnumSet<BuildingType> types) {
        Button go = new Button(label + "  →");
        go.setStyle(Palette.words(Palette.SIZE_LABEL, "white")
                + " -fx-background-color: " + Palette.CONFIRM + ";");
        go.setOnAction(e -> ui.buildScreen.handleAllBuildingMenus(category, types));
        VBox.setMargin(go, new javafx.geometry.Insets(8, 0, 4, 0));
        return go;
    }

    /** A double[] in one figure - the education arrays are per-type. */
    static double sum(double[] values) {
        if (values == null) return 0;
        double total = 0;
        for (double v : values) total += v;
        return total;
    }
}
