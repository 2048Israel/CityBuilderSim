package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import static ham.citybuildersim.ui.Money.*;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * The People tab and the household screen behind it.
 *
 * Who lives here by age and by household shape, the people outside the
 * families (the out of work, the students, the orphans, the prisoners), the
 * grid of household cells with a cell that opens into its own books, the
 * money blocks both panels share, and the tier table. Split out of
 * UserInterface on 2026-09-18: the six banners PEOPLE, THE PIECES THE PEOPLE
 * SCREEN IS BUILT FROM, THE PEOPLE OUTSIDE THE FAMILIES, CAN THE PEOPLE OF
 * THIS CITY AFFORD TO LIVE IN IT, THE MONEY BLOCKS BOTH PANELS SHARE and THE
 * TIER TABLE, exactly as they were, with the shell's members reached through
 * ui. The sector report the household grid opens into is still the shell's
 * (showSectorReport), and it reads revealTop and revealBottom from here.
 */
final class PeopleScreen {

    /** The window this screen draws into: its game, its root, its clearMenu(). */
    private final UserInterface ui;

    PeopleScreen(UserInterface ui) { this.ui = ui; }

    /* =====================================================================
       PEOPLE.

       IT USED TO BE TWO SCREENS and then it was one very long one. This is
       still the one - "fill it up, with the population info so that all the
       info is visible" - because the question it answers is why is my city
       this size, and no half of it can answer that alone. What changed is the
       language: it was ten monospaced report sections with a hundred and
       twenty %-26s format strings, so every heading looked like every other
       heading and the eye had nowhere to land in eight hundred lines of
       Courier.

       WHAT REPLACED WHAT

       Four vitals across the top, which are the only figures a player needs
       before deciding whether to read further. Then the same causal chain the
       old screen had - who is here, what moved, why they came, how old they
       are, how well they are, where they sleep, how they are grouped, what
       work there is - as statements and real tables rather than as padded
       strings. Nothing was dropped: every figure the old screen printed is
       still printed, and the notes that explain the model are still here,
       because those notes are the difference between a number and a reason.

       THE TABLES ARE GRIDS NOW. The households matrix and the skill ladder
       were built out of %9s columns, which line up only in Courier and only
       until one figure runs long. A GridPane with fixed columns lines up
       because it was told to, and it lets a cell carry its own colour without
       the whole row changing.
       ===================================================================== */
    void showPopulationInfoMenu() {
        ui.clearMenu("showPopulationInfoMenu", () -> showPopulationInfoMenu());

        Label title = new Label("PEOPLE");
        title.setStyle(Palette.words(Palette.SIZE_TITLE, Palette.TEXT_HEAD)
                + " -fx-font-weight: bold; -fx-padding: 8 0 2 0;");

        PopulationManager pm = ui.game.getPopulationManager();
        PopulationCohorts cohorts = ui.game.getCohorts();
        FamilyModel families = ui.game.getFamilies();
        Migration migration = ui.game.getMigration();
        BuildingManager bm = ui.game.getBuildingManager();
        Health health = ui.game.getHealth();
        Healthcare service = ui.game.getHealthcare();
        LabourMarket market = ui.game.getLabourMarket();

        int population = pm.getPopulation();
        int workforce = pm.getWorkforce();
        int totalJobs = pm.getTotalJobs();
        int[] vacancies = pm.getJobVacancy();
        double[] fillRates = pm.getJobFillRate();
        int[] jobs = pm.getJobs();
        double[] jobWage = pm.getWagesPerType();
        double[] staffing = fillRates;

        int totalVacancies = 0;
        for (int v : vacancies) totalVacancies += v;

        double total = cohorts.total();
        int unemployed = pm.getUnemployed();
        double jobless = pm.getUnemploymentRate();
        double sick = health.getSickRate();

        double net = migration.getLastArrivals() - migration.getLastDepartures()
                + cohorts.getLastBirths() - cohorts.getLastDeaths();

        int builtHomes = bm.getTotalHomes();
        double needed = families.homesNeeded();
        double spare = builtHomes - needed;
        double shares = families.getSharedHouseholds();
        double doubled = families.getDoubledUpHouseholds();

        /* ------------------------------ the vitals ------------------------------
         *
         * Four, and they are the four a player checks before reading anything
         * else: how many, how many idle, whether there is a door for them, and
         * how many of them turned up for work. Everything below is the reason
         * one of these four reads the way it does.
         */
        VBox sickCell = limitCell("OFF SICK", String.format("%.1f%%", sick * 100),
                people(workforce * sick) + " of the workforce",
                sick > .12 ? Palette.BAD : sick > .06 ? Palette.WARN : Palette.GOOD);
        sickCell.setStyle("-fx-padding: 0 14 0 14;");

        HBox vitals = new HBox(0);
        vitals.setAlignment(Pos.CENTER);
        vitals.setMaxWidth(Region.USE_PREF_SIZE);
        vitals.setStyle("-fx-padding: 8 6 8 6;" + Palette.block(Palette.PANEL));
        vitals.getChildren().addAll(
                limitCell("LIVING HERE", formatter.format(population),
                        String.format("%s%s a month", net >= 0 ? "+" : "-",
                                flowText(Math.abs(net))),
                        net >= 0 ? Palette.TEXT_HEAD : Palette.BAD),
                limitCell("OUT OF WORK", String.format("%.1f%%", jobless * 100),
                        people(unemployed) + " with nothing to do",
                        jobless > .25 || jobless < .03 ? Palette.BAD
                                : jobless > .15 ? Palette.WARN : Palette.GOOD),
                limitCell(spare >= 0 ? "SPARE HOMES" : "HOMES SHORT",
                        people(Math.abs(spare)),
                        doubled >= .5 ? people(doubled) + " doubled up anyway"
                                : shares >= .5 ? people(shares) + " flatsharing"
                                : "everyone has a front door",
                        spare < 0 ? Palette.BAD
                                : doubled >= .5 || shares >= .5
                                        ? Palette.WARN : Palette.GOOD),
                sickCell);

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        /* =============================== WHO IS HERE =============================== */
        column.getChildren().add(statementHead("Who is here"));
        column.getChildren().add(statementLine("Living here", people(population)));
        column.getChildren().add(statementLine("Of working age",
                String.format("%s   (%.0f%%)", people(cohorts.workingAge()),
                        total > 0 ? cohorts.workingAge() / total * 100 : 0)));
        column.getChildren().add(statementLine("In a job", people(pm.getJobsFilled())));
        column.getChildren().add(statementLine("Looking for work", people(unemployed)));

        /*
         * Unemployment is the total rather than another line, because it is the
         * one figure here that is a verdict rather than a count - and because it
         * is the number that quietly went wrong when the workforce stopped being
         * half the city. See Migration's note on residents per job.
         */
        column.getChildren().add(statementTotal("Out of work",
                String.format("%.1f%%", jobless * 100),
                jobless > .25 || jobless < .03 ? Palette.BAD
                        : jobless > .15 ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementNote(
                jobless > .25 ? "Far too many adults with nothing to do."
                        : jobless > .15 ? "High."
                        : jobless < .03 ? "Nobody spare — every new job goes unfilled."
                        : "A healthy amount of slack."));
        column.getChildren().add(statementLine("Each 100 working adults carry",
                String.format("%.0f", cohorts.dependencyRatio())));
        column.getChildren().add(statementNote(
                "Children and pensioners, who do not work and still eat."));

        /* ============================== THIS MONTH ============================== */
        column.getChildren().add(statementHead("This month"));
        column.getChildren().add(statementLine("Born",
                "+" + flowText(cohorts.getLastBirths()), Palette.GOOD));
        column.getChildren().add(statementLine("Died",
                "-" + flowText(cohorts.getLastDeaths()), Palette.TEXT_BODY));
        /*
         * ...AND HOW MANY OF THEM STAYED SICK (2026-09-11). Anybody ill for
         * more than two months can die of it, at their age's chance. By band,
         * because the youngest and the oldest are the ones it takes.
         */
        Sickness sickness = ui.game.getSickness();
        column.getChildren().add(statementLine("...of illness they did not get over",
                "-" + flowText(sickness.getLastDeaths()),
                sickness.getLastDeaths() >= .5 ? Palette.BAD : Palette.TEXT_BODY));
        StringBuilder byAge = new StringBuilder();
        for (AgeBand b : AgeBand.values()) {
            if (byAge.length() > 0) byAge.append("  ·  ");
            byAge.append(b.getLabel().toLowerCase()).append(' ')
                 .append(flowText(sickness.getLastDeaths(b)));
        }
        column.getChildren().add(statementNote(String.format(
                "%s. %s have been ill for more than two months.",
                byAge, people(sickness.peoplePastTwoMonths(cohorts)))));
        // ...and the killed (2026-09-11), all of them adults. See Crime.
        double killed = cohorts.getKilled(AgeBand.ADULT);
        column.getChildren().add(statementLine("...killed",
                "-" + flowText(killed), killed >= .5 ? Palette.BAD : Palette.TEXT_BODY));
        column.getChildren().add(statementLine("In prison",
                people(ui.game.getCrime().prisoners()), Palette.TEXT_BODY));

        /* ------------------------- WHO MOVED IN -------------------------
         *
         * The headline number is a headcount and the headcount is the least
         * interesting thing about it. A city that cannot staff its hospital
         * needs to know whether the thirty-four people who arrived were
         * labourers or graduates, and until now the screen would not say.
         *
         * Also the only place the arrival rules are legible: nobody arrives
         * without a diploma, and every band above one is bought at a premium.
         * Both are visible here as a zero row and as a set of rows that appear
         * only when the city is paying for them.
         * ---------------------------------------------------------------- */
        double[] inMix = migration.getLastArrivalMix();
        double[] inLic = migration.getLastArrivalLicences();
        double inTotal = 0;
        for (double v : inMix) inTotal += v;

        VBox inDetail = new VBox(1);
        inDetail.setStyle("-fx-padding: 2 0 8 14;");
        inDetail.getChildren().add(mixTable(inMix, inTotal));

        boolean anyLicence = false;
        for (double v : inLic) if (v > 0) anyLicence = true;
        if (anyLicence) {
            Label licHead = new Label("...of whom already licensed");
            licHead.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)
                    + " -fx-padding: 6 0 2 0;");
            inDetail.getChildren().add(licHead);
            for (EducationType type : EducationType.values()) {
                if (!type.isProfessional()) continue;
                JobType job = type.licenses();
                double n = inLic[job.ordinal()];
                if (n <= 0) continue;
                inDetail.getChildren().add(statementLine("  " + ui.buildScreen.jobLabel(job),
                        String.format("%.2f  at %.2fx over its band", n,
                                market.licencePremium(job)), Palette.ACCENT));
            }
        }

        inDetail.getChildren().add(statementNote(anyLicence
                        || inMix[WageBand.COLLEGE.ordinal()] > 0
                        || inMix[WageBand.UNIVERSITY.ordinal()] > 0
                ? "Nobody arrives without a diploma — the unskilled band is only ever "
                  + "this city's own children. Anything above a diploma is bought: those "
                  + "rows are here because the city is paying over the going rate for them."
                : "Nobody arrives without a diploma, and nobody above one either — at the "
                  + "going rate a graduate has no reason to prefer this city. Bid a band's "
                  + "wage up, or build the school."));

        column.getChildren().add(statementDisclosure("Moved in",
                flowText(migration.getLastArrivals()), inDetail));

        /* ------------------------- AND WHO LEFT -------------------------
         *
         * The same table, and it has to be the same table, because the two
         * mixes are the argument: a city can hold its headcount steady while
         * quietly trading its graduates for labourers, and no single number
         * anywhere else would show that happening.
         */
        double[] outMix = migration.getLastDepartureMix();
        double outTotal = 0;
        for (double v : outMix) outTotal += v;

        VBox outDetail = new VBox(1);
        outDetail.setStyle("-fx-padding: 2 0 8 14;");
        outDetail.getChildren().add(mixTable(outMix, outTotal));
        outDetail.getChildren().add(statementNote(
                "The educated leave first, and by a wide margin — their labour market is "
                + "national while a labourer's is local. A band whose wage has hit its "
                + "floor with people to spare sheds them every month."));

        column.getChildren().add(statementDisclosure("Moved out",
                flowText(migration.getLastDepartures()), outDetail));

        column.getChildren().add(statementTotal("Net",
                (net >= 0 ? "+" : "-") + flowText(Math.abs(net)),
                net >= 0 ? Palette.GOOD : Palette.BAD));

        /* ============================= WHY THEY COME ============================= */
        double target = migration.getLastTarget();
        column.getChildren().add(statementHead("Why people come"));
        column.getChildren().add(statementLine("Jobs on offer", people(totalJobs)));
        column.getChildren().add(statementLine("Each job supports",
                String.format("%.2f people", migration.getLastResidentsPerJob())));
        column.getChildren().add(statementLine("Homes built for",
                people(ui.game.getHouseholdCapacity())));
        column.getChildren().add(statementLine("A city this good draws", people(target)));
        column.getChildren().add(statementTotal(
                population > target ? "More than it can hold by" : "Room to grow",
                people(Math.abs(target - population)),
                population > target ? Palette.WARN : Palette.GOOD));

        String why;
        String tone;
        if (migration.getLastCrowding() <= 0) {
            why = "Nobody else can fit. Every flatshare and every doubled-up household "
                + "the city can form has already formed — build homes and the queue "
                + "outside starts moving again.";
            tone = Palette.BAD;
        } else if (migration.getLastCrowding() < .95) {
            why = String.format("Housing is tight enough to turn people away: only %.0f%% "
                    + "of the people this city attracts can find somewhere to live. The "
                    + "jobs are still pulling.", migration.getLastCrowding() * 100);
            tone = Palette.WARN;
        } else if (migration.getLastDepartures() > 0) {
            why = String.format("People are leaving. %.0f%% of the city's payroll sits in "
                    + "trades that have been shrinking for a year or have stopped paying "
                    + "altogether.", migration.getLastDecliningShare() * 100);
            tone = Palette.BAD;
        } else if (target > population) {
            why = "Work is going begging and there is room to house whoever takes it. "
                + "People are still arriving.";
            tone = Palette.GOOD;
        } else {
            why = "The city is about the size its jobs and housing support. Nobody is "
                + "leaving — a shortage of work is unemployment, not an exodus, until a "
                + "whole trade has been dying for a year.";
            tone = Palette.TEXT_MUTED;
        }
        column.getChildren().add(sentence(why, tone));

        for (PayTier tier : PayTier.values()) {
            if (!migration.isDeclining(tier)) continue;
            column.getChildren().add(statementLine("  " + tier.getLabel() + " work shrinking",
                    migration.getDecliningStreak(tier) + " months", Palette.BAD));
        }

        /* =============================== THE PYRAMID =============================== */
        column.getChildren().add(statementHead("Ages"));
        for (int i = AgeBand.values().length - 1; i >= 0; i--) {
            AgeBand band = AgeBand.values()[i];
            column.getChildren().add(pyramidRow(band.getLabel(),
                    cohorts.get(band), cohorts.share(band), band.isWorkingAge()));
        }
        column.getChildren().add(statementNote(String.format(
                "Ages 0-120. Adults are %d-%d and are the whole workforce.",
                AgeBand.ADULT.getFromAge(), AgeBand.ADULT.getToAge() - 1)));

        /* ================================= HEALTH ================================= */
        /*
         * Sits under the ages on purpose: coverage is beds divided by the band
         * that needs them, so the two panels are the same arithmetic read from
         * opposite ends. An ageing pyramid IS a senior-care shortage, and
         * putting them next to each other is the cheapest way to say so.
         */
        column.getChildren().add(statementHead(health.isOutbreak()
                ? String.format("Health — outbreak, month %d",
                        Math.max(1, ui.game.getMonth() - health.getOutbreakStarted() + 1))
                : "Health"));
        column.getChildren().add(statementLine("Off sick this month",
                String.format("%.1f%%", sick * 100),
                sick > .12 ? Palette.BAD : sick > .06 ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("That is",
                people(workforce * sick) + " of the workforce"));
        column.getChildren().add(statementNote(
                "They are still on the payroll and still get paid — the loss is output, "
                + "not wages."));
        column.getChildren().add(statementLine("General care covers",
                String.format("%.0f%%", health.getCoverage() * 100),
                health.getCoverage() >= 1 ? Palette.GOOD
                        : health.getCoverage() <= 0 ? Palette.BAD : Palette.WARN));
        column.getChildren().add(statementNote(String.format(
                "%s staffed beds for %s people.",
                people(bm.getStaffedCareCapacity(CareType.GENERAL, staffing)),
                people(total))));
        column.getChildren().add(statementLine("...which also cures",
                String.format("%.0f%% of the sick a month",
                        Sickness.recovery(health.getCoverage()) * 100)));
        column.getChildren().add(statementLine("Sick more than two months",
                people(sickness.peoplePastTwoMonths(cohorts)),
                sickness.peoplePastTwoMonths(cohorts) >= 1 ? Palette.WARN : null));
        column.getChildren().add(statementLine("Died of illness last month",
                flowText(sickness.getLastDeaths()),
                sickness.getLastDeaths() >= .5 ? Palette.BAD : null));

        String illness;
        String illnessTone;
        if (health.isOutbreak()) {
            illness = String.format("An outbreak that began in %s is adding %.1f points on "
                    + "top of the usual %.1f%%. It fades on its own over a few months, and "
                    + "hospitals make it milder rather than shorter.",
                    CityCalendar.format(health.getOutbreakStarted()),
                    health.getOutbreakSeverity() * 100, health.getBaselineRate() * 100);
            illnessTone = Palette.BAD;
        } else if (health.getCoverage() <= 0) {
            illness = String.format("There is no general care in this city at all, so %.0f%% "
                    + "of every month's work simply does not happen. A walk-in clinic is the "
                    + "cheapest thing on the healthcare list.", sick * 100);
            illnessTone = Palette.BAD;
        } else if (health.getCoverage() < 1) {
            illness = String.format("Clinics and hospitals reach %.0f%% of the city. Covering "
                    + "the rest would take the absence rate down toward %.0f%%.",
                    health.getCoverage() * 100, Health.WELL_SERVED_RATE * 100);
            illnessTone = Palette.WARN;
        } else {
            illness = "Everyone can get seen. This is as healthy as a workforce gets — "
                + "people still fall ill, and an outbreak can still arrive.";
            illnessTone = Palette.GOOD;
        }
        column.getChildren().add(sentence(illness, illnessTone));

        /*
         * The unburied get their own line rather than a place in the list,
         * because they are the one thing here that is an emergency rather than
         * a level - and because a player seeing the sick rate climb with full
         * hospital coverage has no other way to find out why.
         */
        if (service.getUnburied() > 0) {
            column.getChildren().add(statementLine("Lying unburied",
                    people(service.getUnburied()), Palette.BAD));
            column.getChildren().add(statementNote(String.format(
                    "Adding %.1f points to the rate above.", health.getUnburiedRate() * 100)));
        }

        /*
         * The other two care types, and what each of them buys.
         *
         * Coverage is STAFFED, not built - a hospital with no doctors treats
         * nobody, and this is the screen where a player would otherwise wonder
         * why the wards they paid for changed nothing.
         */
        for (CareType type : new CareType[]{CareType.CHILDCARE, CareType.SENIOR}) {
            double served = type.populationServed(cohorts);
            double beds = bm.getStaffedCareCapacity(type, staffing);
            double cover = Health.coverageOf(beds, served);

            column.getChildren().add(statementLine(type.getLabel(),
                    String.format("%.0f%%", cover * 100),
                    cover < .5 ? Palette.WARN : Palette.TEXT_BODY));
            column.getChildren().add(statementNote(String.format("%s places for %s  ·  %s",
                    people(beds), people(served),
                    type == CareType.CHILDCARE
                        ? String.format("infant deaths x%.3f, illness x%.2f, births x%.2f",
                                Healthcare.mortalityFactor(AgeBand.BABY, cover, .5, 0),
                                1 + Sickness.EXTRA_SICKNESS * (1 - cover),
                                Healthcare.birthFactor(cover))
                        : String.format("senior deaths x%.2f, illness x%.2f, +%.0f%% draw",
                                Healthcare.mortalityFactor(AgeBand.SENIOR, 0, .5, cover),
                                1 + Sickness.EXTRA_SICKNESS * (1 - cover),
                                (Migration.seniorCarePull(cover) - 1) * 100))));
        }

        /* ============================== DEATH CARE ==============================
         *
         * A cemetery is a stock, not a rate: it is completely fine right up to
         * the month it is full and then permanently useless. So the useful
         * warning is the one that comes BEFORE, and it is a countdown rather
         * than a level.
         */
        double plots = bm.getCareCapacity(CareType.BURIAL);
        double ovens = bm.getStaffedCareCapacity(CareType.CREMATION, staffing);
        double plotsLeft = Healthcare.plotsRemaining(plots, service.getPlotsUsed());
        double monthsLeft = service.monthsOfPlotsLeft(plots);

        column.getChildren().add(statementHead("Death care"));
        column.getChildren().add(statementLine("Funerals dealt with",
                String.format("%.1f%%", service.getDeathCareRatio() * 100),
                service.isOverwhelmed() ? Palette.BAD
                        : service.isStrained() ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("Capacity", service.getStatus(),
                service.isOverwhelmed() ? Palette.BAD
                        : service.isStrained() ? Palette.WARN : Palette.GOOD));
        column.getChildren().add(statementLine("Deaths",
                flowText(service.getDeaths()) + " a month"));
        column.getChildren().add(statementLine("Dealt with",
                String.format("%s buried, %s cremated",
                        flowText(service.getBurials()),
                        flowText(service.getCremations()))));
        column.getChildren().add(statementLine("Ground left",
                String.format("%s of %s", people(plotsLeft), people(plots)),
                service.getPlotUtilisation() > .9 ? Palette.BAD
                        : service.getPlotUtilisation() > .75 ? Palette.WARN : null));
        column.getChildren().add(statementNote(String.format("%.0f%% full%s",
                service.getPlotUtilisation() * 100,
                plots > 0 && monthsLeft < 600
                        ? String.format("  ·  about %,.0f months left at this rate", monthsLeft)
                        : "")));
        column.getChildren().add(statementLine("Crematoria",
                people(ovens) + " a month",
                service.getCremationUtilisation() > .9 ? Palette.BAD : null));
        column.getChildren().add(statementNote(String.format("%.0f%% used.",
                service.getCremationUtilisation() * 100)));

        if (service.isOverwhelmed()) {
            column.getChildren().add(alert("Nowhere to bury them", String.format(
                    "%s people are lying unburied. It is adding %.0f points to the sick rate "
                    + "above, and it does not go away on its own. %s",
                    people(service.getUnburied()),
                    health.getUnburiedRate() * 100,
                    plotsLeft <= 0 && plots > 0
                            ? "Every plot in the city is full. Build another cemetery, or a "
                              + "crematorium, which needs almost no land."
                            : "Build a cemetery or a crematorium. A cemetery is cheap and "
                              + "vast; a crematorium is the opposite.")));
        } else if (service.isStrained()) {
            column.getChildren().add(alert("Death care is filling up",
                    (service.getCremationUtilisation() >= Healthcare.STRAINED
                        ? String.format("The crematoria are at %.0f%% of what they can handle. ",
                                service.getCremationUtilisation() * 100)
                        : String.format("The ground runs out in about %,.0f months at this "
                                + "rate. ", monthsLeft))
                    + "A cemetery is a stock, not a rate: it is fine until the month it is "
                    + "full, and then people start piling up immediately. Plots are consumed "
                    + "permanently — the land never comes back."));
        }

        /* ================================== HOMES ================================== */
        column.getChildren().add(statementHead("Homes"));
        column.getChildren().add(statementLine("Homes in the city", people(builtHomes)));
        column.getChildren().add(statementLine("Under construction",
                people(bm.getHomesUnderConstruction())));
        column.getChildren().add(statementLine("Households wanting one", people(needed)));
        column.getChildren().add(statementTotal(spare >= 0 ? "Spare" : "Short by",
                people(Math.abs(spare)),
                spare >= 0 ? Palette.GOOD : Palette.BAD));

        String pressure;
        String pressureTone;
        if (shares < .5 && doubled < .5) {
            pressure = "Everyone who wants their own front door has one.";
            pressureTone = Palette.GOOD;
        } else if (doubled < .5) {
            pressure = String.format("%s households are flatshares — five single adults to "
                    + "a home, which is what people do first when housing is tight. They "
                    + "would rather live alone.", people(shares));
            pressureTone = Palette.WARN;
        } else {
            pressure = String.format("%s flatshares, and %s households are doubled up two "
                    + "to a home. Doubling up is the last resort — the city has run out of "
                    + "single adults to crowd and is now crowding families.",
                    people(shares), people(doubled));
            pressureTone = Palette.BAD;
        }
        column.getChildren().add(sentence(pressure, pressureTone));

        /* ================================ HOUSEHOLDS ================================
         *
         * THE FULL MATRIX: household shape down, pay tier across.
         *
         * It used to be one row per shape with the tiers crushed into a trailing
         * string, which made the two axes impossible to compare - you could see
         * that large families existed and that unskilled households existed, but
         * not whether the city's large families were the poor ones. That
         * crossing is the entire reason the model has two axes, and burying one
         * of them in a sentence threw it away.
         *
         * A dot rather than a zero where a cell is empty, because a grid of
         * zeros reads as data and an empty cell reads as empty.
         */
        column.getChildren().add(statementHead("Households"));
        column.getChildren().add(statementLine("Total",
                people(families.totalHouseholds())));
        column.getChildren().add(statementLine("Average size",
                String.format("%.2f people", families.averageHouseholdSize())));
        column.getChildren().add(statementLine("Adults unplaced",
                people(families.getUnhousedAdults()),
                families.getUnhousedAdults() > .5 ? Palette.WARN : null));
        column.getChildren().add(shapeMatrix(families));
        column.getChildren().add(statementNote(
                "Seniors carry no tier — they have no earner — so their row sits under the "
                + "first column by convention rather than by wage."));
        /*
         * THE HOUSEHOLDS REMEMBER (2026-09-11). The builder keeps last month's
         * households as far as the people are still there for them, so the
         * month's change is four numbers: kept, re-formed on their own, no
         * longer fitting, and formed new from the people left over.
         */
        if (families.hasRecord()) {
            column.getChildren().add(statementLine("Kept from last month",
                    people(families.getLastKept())));
            column.getChildren().add(statementNote(String.format(
                    "%s re-formed on their own (%.0f%% a month)  ·  %s no longer fitted — "
                    + "a child grown, a death, a job lost  ·  %s formed from the people left over.",
                    people(families.getLastReformed()),
                    FamilyModel.REFORMING_EACH_MONTH * 100,
                    people(families.getLastNoLongerFit()),
                    people(families.getLastNew()))));
        }

        /* ======================= OUTSIDE THE FAMILIES (2026-09-11) ======================= */
        outsideBlock(column);

        /* ============================= THE LABOUR MARKET ============================= */
        column.getChildren().add(statementHead("Work"));
        column.getChildren().add(statementLine("Workers", people(workforce)));
        column.getChildren().add(statementLine("Positions", people(totalJobs)));
        column.getChildren().add(statementLine("Unfilled", people(totalVacancies),
                totalVacancies > 0 ? Palette.WARN : null));

        String labourNote;
        String labourTone;
        if (workforce > totalJobs) {
            labourNote = String.format("Labour surplus: %s adults with nowhere to work. "
                    + "That is unemployment, not an exodus — people do not leave over it "
                    + "until a whole trade has been dying for a year.",
                    people(workforce - totalJobs));
            labourTone = Palette.WARN;
        } else if (totalVacancies > 0) {
            labourNote = String.format("Labour shortage: %s positions across the city stand "
                    + "empty, and every one of them is pulling people toward the city.",
                    people(totalVacancies));
            labourTone = Palette.GOOD;
        } else {
            labourNote = "Every position is filled and every worker has one.";
            labourTone = Palette.TEXT_MUTED;
        }
        column.getChildren().add(sentence(labourNote, labourTone));

        /* -----------------------------------------------------------------
           THE SKILL LADDER, which is the explanation for everything below it.

           The per-job table says a post is unfilled. It cannot say WHY, and
           until the labour market existed there was no why - the workforce was
           one pool of interchangeable adults and a city with no schools staffed
           220 doctor posts out of it. Now a post is unfilled because nobody
           qualified is available, and this block is where a player can see that
           rather than infer it.

           SUPPLY IS NOT WORKERS. A band's supply is its own people plus anyone
           from ABOVE who could not find work at their own level - graduates
           labouring - which is why the college row can show more supply than it
           has college-trained residents. That cascade is the reason an
           oversupply of graduates depresses the diploma wage instead of sitting
           in a pool of its own.
           ----------------------------------------------------------------- */
        column.getChildren().add(subHead("The skill ladder"));
        column.getChildren().add(ladderTable(pm, market, ui.game.getEducation().getStudying()));
        column.getChildren().add(statementNote(
                "open = posts this band can be put into (licensed posts are below). "
                + "queue = its own workers plus everyone over-qualified who came down for "
                + "the same jobs. chance = open/queue, which is what decides whether "
                + "anybody in this band moves here at all."));

        /*
         * WHAT IT WOULD TAKE TO ATTRACT ONE.
         *
         * The ladder says a band is short. It does not say what to do about it,
         * and the answer is not obvious, because the pull is zero at the going
         * rate by design - a graduate has no reason to prefer this city until
         * it is paying over the odds. So this names the two levers in the two
         * cases: pay more, or teach your own.
         */
        StringBuilder pulled = new StringBuilder();
        for (WageBand wb : WageBand.values()) {
            if (wb == WageBand.NONE || wb == WageBand.DIPLOMA) continue;
            double p = market.bandPremium(wb);
            double reach = Migration.reach(p);
            if (reach <= 0) continue;
            if (pulled.length() > 0) pulled.append(", ");
            pulled.append(String.format("%s at %.0f%% of what the world can spare",
                    wb.label().toLowerCase(), reach * 100));
        }
        column.getChildren().add(statementNote(pulled.length() > 0
                ? "Paying over the going rate is pulling " + pulled + ". At the going rate "
                  + "the pull is nothing at all, which is why a school is the other answer."
                : "Nobody above a diploma is being drawn here: every band is paid the going "
                  + "rate or less, and the going rate is what that trade costs everywhere. "
                  + "Bid a band up or build the school that makes your own."));

        /*
         * THE GATED PROFESSIONS, which the ladder above cannot show.
         *
         * A band row says the city has eight hundred graduates and two hundred
         * graduate posts, and every one of those posts still stands empty if
         * they are doctor posts and nobody is a doctor. That is not visible
         * anywhere else, and "unfilled" on the job table below reads as a
         * labour shortage when it is actually a licensing one.
         */
        javafx.scene.layout.GridPane gated = professionTable(pm, market);
        if (gated != null) {
            column.getChildren().add(subHead("The licensed professions"));
            column.getChildren().add(gated);
        }

        column.getChildren().add(subHead("Every post in the city"));
        column.getChildren().add(jobTable(jobs, vacancies, fillRates, jobWage));
        column.getChildren().add(statementNote(String.format(
                "Minimum wage %s a month — every wage in the city is a multiple of it.",
                cash(market.getMinimumWage()))));

        /* ========================== WHAT IT CANNOT DO YET ========================== */
        VBox gaps = new VBox(0);
        gaps.setStyle("-fx-padding: 2 0 8 14;");
        for (String line : new String[] {
                "Births are a flat rate — healthcare, housing and prosperity will "
                    + "eventually have a say.",
                "Nobody has an age. Each band holds a mean, so an intake spreads out "
                    + "rather than moving through as a wave.",
                "Mortality is one figure per band, so an adult of twenty carries the same "
                    + "risk as one of sixty-nine.",
                "Arrivals copy the city's age mix, so a growing city imports its own "
                    + "dependency ratio. Real migrants skew young.",
                "Anyone can do any job. Education does not gate the pay tiers yet.",
                "No multi-generational households, and couples share one tier.",
                "Households are rebuilt from scratch each month — nobody keeps the family "
                    + "they had last month.",
                "Crowding is counted but costs nothing. Nobody is unhappy about it and no "
                    + "rent goes up." }) {
            gaps.getChildren().add(statementNote("· " + line));
        }
        column.getChildren().add(statementHead("What this model does not do yet"));
        column.getChildren().add(statementDisclosure("Eight things it fakes",
                "", gaps, "show"));

        Button cashflow = new Button("Household Cash Flow");
        cashflow.setOnAction(e -> showHouseholdMenu());

        VBox all = new VBox(0, vitals, column);
        all.setAlignment(Pos.CENTER);

        // See the rail: it reaches this screen from every other
        // one, so Back had nothing left to do.
        ui.rootMenu.getChildren().addAll(title, ui.scrolled(all), cashflow);
    }

    /* =====================================================================
       THE PIECES THE PEOPLE SCREEN IS BUILT FROM.

       All of them compose Palette and the statement primitives above; none
       invents a colour or a size of its own. They live here rather than in the
       method because four of them are tables, and a table built inline is a
       hundred lines of grid arithmetic in the middle of a sentence about
       migration.
       ===================================================================== */

    /** How wide the age bars are drawn. */
    static final double PYRAMID_BAR = 200;

    /**
     * One age band: name, headcount, share, and a bar.
     *
     * The bar was a run of "#" characters, which works and looks like a bug.
     * A Region of the right width is the same information drawn rather than
     * typed, and it can be a colour - green for the bands that work, grey for
     * the ones the working bands carry.
     */
    HBox pyramidRow(String label, double count, double share, boolean workingAge) {

        Label name = new Label(label);
        name.setPrefWidth(88);
        name.setMinWidth(88);
        name.setStyle(Palette.words(Palette.SIZE_BODY, Palette.TEXT_LABEL));

        Label headcount = new Label(people(count));
        headcount.setPrefWidth(80);
        headcount.setMinWidth(80);
        headcount.setAlignment(Pos.CENTER_RIGHT);
        headcount.setStyle(Palette.figure(Palette.SIZE_BODY, Palette.TEXT_BODY));

        Label share100 = new Label(String.format("%.1f%%", share * 100));
        share100.setPrefWidth(52);
        share100.setMinWidth(52);
        share100.setAlignment(Pos.CENTER_RIGHT);
        share100.setStyle(Palette.figure(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));

        Region bar = new Region();
        bar.setMinHeight(9);
        bar.setPrefHeight(9);
        bar.setMaxHeight(9);
        double wide = Math.max(2, share * PYRAMID_BAR);
        bar.setMinWidth(wide);
        bar.setPrefWidth(wide);
        bar.setMaxWidth(wide);
        bar.setStyle("-fx-background-color: "
                + (workingAge ? Palette.GOOD : Palette.TEXT_LABEL)
                + "; -fx-background-radius: 2;");

        HBox track = new HBox(bar);
        track.setAlignment(Pos.CENTER_LEFT);
        track.setMinWidth(PYRAMID_BAR);
        track.setPrefWidth(PYRAMID_BAR);

        HBox row = new HBox(Palette.GAP, name, headcount, share100, track);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Region.USE_PREF_SIZE);
        row.setStyle("-fx-padding: 2 0 2 0;");
        return row;
    }

    /** Who arrived, or who left, by the skill they hold. */
    javafx.scene.layout.GridPane mixTable(double[] mix, double total) {

        javafx.scene.layout.GridPane table =
                grid(new double[] {150, 84, 70}, rightAfterFirst(3));
        gridHead(table, "skill", "people", "share");

        int line = 1;
        for (WageBand band : WageBand.values()) {
            double n = mix[band.ordinal()];
            String tone = n <= 0 ? Palette.TEXT_SPENT : Palette.TEXT_BODY;
            table.add(gridCell(band.label(), tone, Palette.SIZE_CAPTION, false), 0, line);
            table.add(gridCell(people(n), tone, Palette.SIZE_CAPTION, true), 1, line);
            table.add(gridCell(String.format("%.1f%%", total > 0 ? n / total * 100 : 0),
                    tone, Palette.SIZE_CAPTION, true), 2, line);
            line++;
        }
        return table;
    }

    /**
     * Household shape down the side, pay tier across the top.
     *
     * The same two axes the affordability grid crosses, counting households
     * instead of dollars - so a player can read "the city's large families are
     * the poor ones" off one screen and "and here is what that costs them" off
     * the other.
     */
    /* =====================================================================
       THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11)

       Jerus: "a new household structure called unemployed... these will just
       sum up by age the unemployed, or unhoused". The families are built from
       the adults who work; this is everybody else, by age - which is the
       table - and then what moved through the pool this month and what EI
       cost. See claude/the-people-the-books-left-out.md.
       ===================================================================== */
    void outsideBlock(VBox column) {
        Unemployment u = ui.game.getUnemployment();
        FamilyModel families = ui.game.getFamilies();
        EconomyManager em = ui.game.getEconomyManager();

        double students = families.getSeekers(FamilyModel.Seeker.STUDENT);
        double[] noDoor = families.unhousedPeopleByBand();
        noDoor[AgeBand.ADULT.ordinal()] += u.getUnhoused();
        double unhoused = 0;
        for (double v : noDoor) unhoused += v;
        double orphans = families.getOrphansTotal();

        column.getChildren().add(statementHead("Outside the families"));
        column.getChildren().add(outsideTable(u, families, noDoor));
        column.getChildren().add(statementNote(
                "Families are built from the adults who work, and their children. Everybody "
                + "else is here, by age: the out of work and the students keep their own "
                + "books, the unhoused have no home, and the orphans are the children no "
                + "family took."));

        /* ---- the out of work ---- */
        column.getChildren().add(statementLine("Out of work", people(u.getPool())));
        column.getChildren().add(statementLine("   on EI", people(u.onEi()),
                u.onEi() >= .5 ? Palette.GOOD : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("   EI run out", people(u.getOffEi()),
                u.getOffEi() >= .5 ? Palette.WARN : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("   lost their home", people(u.getUnhoused()),
                u.getUnhoused() >= .5 ? Palette.BAD : Palette.TEXT_SPENT));

        VBox flows = new VBox(1);
        flows.setStyle("-fx-padding: 2 0 8 14;");
        flows.getChildren().add(statementLine("Jobs that disappeared", flowText(u.getJobsLost()),
                u.getJobsLost() >= .5 ? Palette.WARN : Palette.TEXT_SPENT));
        flows.getChildren().add(statementLine("Arrived and found no work", flowText(u.getArrivalsUnhired())));
        flows.getChildren().add(statementLine("Came of age, or finished school, with no post",
                flowText(u.getEntrants())));
        flows.getChildren().add(statementLine("Hired out of the pool", flowText(u.getLocalHires()),
                u.getLocalHires() >= .5 ? Palette.GOOD : Palette.TEXT_SPENT));
        flows.getChildren().add(statementLine("Reached the end of their EI", flowText(u.getDroppedOffEi())));
        flows.getChildren().add(statementLine("Lost their home", flowText(u.getNewlyUnhoused()),
                u.getNewlyUnhoused() >= .05 ? Palette.BAD : Palette.TEXT_SPENT));
        flows.getChildren().add(statementLine("Gave up and left the city", flowText(u.getLeftWhenBroke())));
        flows.getChildren().add(statementNote(
                "Only jobs that actually close put somebody on EI; an arrival who finds no "
                + "work is on it too. It lasts twelve months. Past that they live on what "
                + "they saved and what the bank will lend, and when both are gone and the "
                + "rent is not paid, a quarter leave and the rest lose their home."));
        column.getChildren().add(statementDisclosure("Through the pool this month",
                flowText(u.getJobsLost() + u.getArrivalsUnhired() + u.getEntrants()) + " in",
                flows, "how?"));

        /* ---- EI ---- */
        double premiums = em.getEiPremiums();
        double paid = u.getBenefitsPaid();
        column.getChildren().add(statementLine("EI paid this month", money(paid),
                paid > 0 ? Palette.WARN : Palette.TEXT_SPENT));
        if (u.onEi() >= .5) {
            column.getChildren().add(statementLine("   per claimant", moneyFull(u.getBenefitPerClaimant())));
        }
        column.getChildren().add(statementLine(String.format("Premiums at %.2f%% of wages",
                        em.getTaxPolicy().getEiPremiumRate() * 100),
                money(premiums), Palette.GOOD));
        column.getChildren().add(statementNote(paid <= 0
                ? "Nobody is drawing EI, so the premiums are all revenue."
                : premiums >= paid
                        ? "The premiums cover what EI pays out; the rest is revenue."
                        : String.format("The premiums cover %.0f%% of it. The rest is general "
                                + "revenue - a bust costs the treasury twice, in EI and in "
                                + "the premiums on the wages that went.", premiums / paid * 100)));

        /* ---- the students, the unhoused, the orphans ---- */
        column.getChildren().add(statementLine("Full-time students", people(students)));
        column.getChildren().add(statementLine("No home", people(unhoused),
                unhoused >= .5 ? Palette.BAD : Palette.TEXT_SPENT));
        column.getChildren().add(statementLine("Orphans", people(orphans),
                orphans >= .5 ? Palette.BAD : Palette.TEXT_SPENT));
        // ...and the prisoners, since 2026-09-11 (night). See Crime.
        column.getChildren().add(statementLine("In prison", people(ui.game.getCrime().prisoners()),
                ui.game.getCrime().prisoners() >= .5 ? Palette.WARN : Palette.TEXT_SPENT));
        if (orphans >= .5 || unhoused >= .5) {
            column.getChildren().add(sentence(
                    "Nobody feeds or cares for the orphans: they go hungry, get sick and die "
                    + "far faster than other children. The unhoused get sick and die faster "
                    + "too. Both are in the sick rate and in the deaths above.",
                    Palette.BAD_TEXT));
        }
    }

    /** Everybody outside the families, by age. A dot where nobody is. */
    javafx.scene.layout.GridPane outsideTable(Unemployment u, FamilyModel families,
                                                     double[] noDoor) {
        javafx.scene.layout.GridPane table = grid(
                new double[] {SHAPE_COL, 76, 70, 70, 70, 70, 70}, rightAfterFirst(7));
        gridHead(table, "age", "out of work", "students", "no home", "orphans", "in prison", "total");

        double students = families.getSeekers(FamilyModel.Seeker.STUDENT);
        double prisoners = ui.game.getCrime().prisoners();
        double[] sum = new double[6];
        int line = 1;
        for (AgeBand band : AgeBand.values()) {
            int b = band.ordinal();
            double out = band == AgeBand.ADULT ? u.getPool() : 0;
            double study = band == AgeBand.ADULT ? students : 0;
            double home = noDoor[b];
            double orphan = families.getOrphans(band);
            double inside = band == AgeBand.ADULT ? prisoners : 0;
            // The out of work who lost their home are in both columns; the total counts them once.
            double total = out + study + orphan + inside
                    + home - (band == AgeBand.ADULT ? Math.min(home, u.getUnhoused()) : 0);
            double[] row = { out, study, home, orphan, inside, total };
            table.add(gridCell(band.getLabel().toLowerCase(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);
            for (int c = 0; c < row.length; c++) {
                String tone = row[c] < .5 ? Palette.TEXT_SPENT
                        : c == 2 || c == 3 ? Palette.BAD_SOFT : Palette.TEXT_BODY;
                table.add(gridCell(cell(row[c]), tone, Palette.SIZE_CAPTION, true), c + 1, line);
                sum[c] += row[c];
            }
            line++;
        }
        table.add(gridCell("everybody", Palette.TEXT_HEAD, Palette.SIZE_CAPTION, false), 0, line);
        for (int c = 0; c < sum.length; c++) {
            table.add(gridCell(cell(sum[c]), Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), c + 1, line);
        }
        return table;
    }

    javafx.scene.layout.GridPane shapeMatrix(FamilyModel families) {

        int columns = PayTier.values().length + 2;
        double[] widths = new double[columns];
        widths[0] = SHAPE_COL;
        for (int i = 1; i < columns; i++) widths[i] = 64;

        javafx.scene.layout.GridPane table = grid(widths, rightAfterFirst(columns));

        String[] heads = new String[columns];
        heads[0] = "household";
        int at = 1;
        for (PayTier tier : PayTier.values()) heads[at++] = shortTier(tier);
        heads[at] = "total";
        gridHead(table, heads);

        int line = 1;
        table.add(gridCell("pays per worker", Palette.TEXT_MUTED,
                Palette.SIZE_CAPTION, false), 0, line);
        int col = 1;
        for (PayTier tier : PayTier.values()) {
            table.add(gridCell(cash(tier.getMonthlyWage()),
                    Palette.TEXT_MUTED, Palette.SIZE_CAPTION, true), col++, line);
        }
        line++;

        for (FamilyStructure shape : FamilyStructure.values()) {
            double count = families.totalOf(shape);
            if (count < .5) continue;

            // Flatshares in amber wherever they appear: they are the one shape
            // nobody chooses, so a row of them is a housing shortage with a
            // number on it.
            String tone = shape == FamilyStructure.SHARED_ADULTS
                    ? Palette.WARN : Palette.TEXT_BODY;

            table.add(gridCell(shape.getLabel(), tone, Palette.SIZE_CAPTION, false), 0, line);
            col = 1;
            for (PayTier tier : PayTier.values()) {
                double n = families.get(shape, tier);
                table.add(gridCell(cell(n), n < .5 ? Palette.TEXT_SPENT : tone,
                        Palette.SIZE_CAPTION, true), col++, line);
            }
            table.add(gridCell(shape.isRetired() ? "no earner" : people(count),
                    tone, Palette.SIZE_CAPTION, true), col, line);
            line++;
        }

        // The column totals, which is the answer to "how rich is this city".
        table.add(gridCell("all households", Palette.TEXT_HEAD,
                Palette.SIZE_CAPTION, false), 0, line);
        col = 1;
        for (PayTier tier : PayTier.values()) {
            table.add(gridCell(cell(families.totalOf(tier)), Palette.TEXT_HEAD,
                    Palette.SIZE_CAPTION, true), col++, line);
        }
        table.add(gridCell(people(families.totalHouseholds()),
                Palette.TEXT_HEAD, Palette.SIZE_CAPTION, true), col, line);
        return table;
    }

    /** The skill ladder: what each band has, what it can take, and what it costs. */
    javafx.scene.layout.GridPane ladderTable(PopulationManager pm,
                                                     LabourMarket market,
                                                     double[] studying) {

        javafx.scene.layout.GridPane table = grid(
                new double[] {96, 74, 62, 70, 74, 62, 54, 54}, rightAfterFirst(8));
        gridHead(table, "skill", "workers", "study", "open", "queue", "chance", "tight", "pay");

        double[] own = pm.workforceByBand();
        // OPEN, not posts. A doctor post is not a job an ordinary graduate can
        // take, so counting it here would say the graduate band is short when
        // what is short is doctors - and the wage is priced on this number, so
        // the column has to be the one the market used. The posts only a
        // licence holder can fill are in the professions table below.
        double[] open = pm.staffablePostsByBand();
        double[] queue = pm.supplyByBand();

        int line = 1;
        for (WageBand band : WageBand.values()) {
            int b = band.ordinal();
            // Off an ungated job: the first university job in enum order is
            // the doctor, whose own licence premium is not the band's.
            double premium = market.bandPremium(band);

            /*
             * CHANCE is the number that decides who moves here, and it is not
             * the wage. It is this band's open posts against everybody queueing
             * for them - its own people PLUS every over-qualified worker who
             * came down a rung - which is why a city full of graduates doing
             * diploma work is not a city an incoming diploma-holder wants.
             */
            double chance = queue[b] > 0 ? Math.min(1, open[b] / queue[b]) : 1;

            // Red when the city is paying over the odds to staff it, which is
            // the shortage showing up as money before it shows up as a gap.
            String tone = premium > 1.05 ? Palette.BAD
                    : premium < .95 ? Palette.TEXT_MUTED : Palette.TEXT_BODY;

            String[] cells = {
                people(own[b]),
                people(studying[b]),
                people(open[b]),
                people(queue[b]),
                String.format("%.0f%%", chance * 100),
                String.format("%.2f", market.getTightness(band)),
                String.format("%.2fx", premium)
            };
            table.add(gridCell(band.label(), tone, Palette.SIZE_CAPTION, false), 0, line);
            for (int i = 0; i < cells.length; i++) {
                table.add(gridCell(cells[i], tone, Palette.SIZE_CAPTION, true), i + 1, line);
            }
            line++;
        }
        return table;
    }

    /** The posts only a licence can fill. Null when the city has none of them. */
    javafx.scene.layout.GridPane professionTable(PopulationManager pm,
                                                         LabourMarket market) {
        boolean any = false;
        for (JobType job : JobType.values()) {
            if (!PopulationManager.isGated(job)) continue;
            if (pm.getJobs()[job.ordinal()] > 0 || pm.getLicensed(job) > 0) any = true;
        }
        if (!any) return null;

        javafx.scene.layout.GridPane table = grid(
                new double[] {150, 78, 66, 62, 66, 118},
                new javafx.geometry.HPos[] {
                    javafx.geometry.HPos.LEFT, javafx.geometry.HPos.RIGHT,
                    javafx.geometry.HPos.RIGHT, javafx.geometry.HPos.RIGHT,
                    javafx.geometry.HPos.RIGHT, javafx.geometry.HPos.LEFT});
        gridHead(table, "profession", "licensed", "posts", "paying", "pulling", "school");

        int line = 1;
        for (EducationType type : EducationType.values()) {
            if (!type.isProfessional()) continue;
            JobType job = type.licenses();
            int posts = pm.getJobs()[job.ordinal()];
            double held = pm.getLicensed(job);
            if (posts <= 0 && held <= 0) continue;

            boolean built = ui.game.getBuildingManager()
                    .getBuiltEducationPlaces()[type.ordinal()] > 0;

            /*
             * PAYING is the realised premium over this profession's own band -
             * what the city actually hands a doctor above what it hands an
             * ordinary graduate. PULLING is what that buys in migrants, and it
             * is its own number: a profession draws on its own shortage, so a
             * hospital can import doctors into a city drowning in graduates.
             */
            double paying = market.licencePremium(job);
            String tone = held < posts ? Palette.BAD : Palette.TEXT_BODY;

            table.add(gridCell(ui.buildScreen.jobLabel(job), tone, Palette.SIZE_CAPTION, false), 0, line);
            table.add(gridCell(people(held), tone,
                    Palette.SIZE_CAPTION, true), 1, line);
            table.add(gridCell(people(posts), tone,
                    Palette.SIZE_CAPTION, true), 2, line);
            table.add(gridCell(String.format("%.2fx", paying), tone,
                    Palette.SIZE_CAPTION, true), 3, line);
            table.add(gridCell(String.format("%.0f%%", Migration.reach(paying) * 100),
                    tone, Palette.SIZE_CAPTION, true), 4, line);
            table.add(gridCell(built ? "yes" : "none — imports only",
                    built ? Palette.TEXT_MUTED : Palette.WARN,
                    Palette.SIZE_CAPTION, false), 5, line);
            line++;
        }
        return table;
    }

    /** Every kind of post the city has built, and how well it is staffed. */
    javafx.scene.layout.GridPane jobTable(int[] jobs, int[] vacancies,
                                                  double[] fillRates, double[] jobWage) {

        javafx.scene.layout.GridPane table = grid(
                new double[] {150, 74, 74, 62, 74, 112}, rightAfterFirst(6));
        gridHead(table, "post", "built", "unfilled", "staffed", "wage", "payroll");

        JobType[] kinds = JobType.values();
        int line = 1;
        for (int i = 0; i < jobs.length; i++) {
            if (jobs[i] <= 0) continue;
            String tone = fillRates[i] < .9 ? Palette.BAD : Palette.TEXT_BODY;
            table.add(gridCell(ui.buildScreen.jobLabel(kinds[i]), tone, Palette.SIZE_CAPTION, false), 0, line);
            table.add(gridCell(people(jobs[i]), tone,
                    Palette.SIZE_CAPTION, true), 1, line);
            table.add(gridCell(people(vacancies[i]),
                    vacancies[i] > 0 ? tone : Palette.TEXT_SPENT,
                    Palette.SIZE_CAPTION, true), 2, line);
            table.add(gridCell(String.format("%.0f%%", fillRates[i] * 100), tone,
                    Palette.SIZE_CAPTION, true), 3, line);
            table.add(gridCell(cash(jobWage[i]), tone,
                    Palette.SIZE_CAPTION, true), 4, line);
            table.add(gridCell(tightMoney(toDollars(jobWage[i] * jobs[i])), tone,
                    Palette.SIZE_CAPTION, true), 5, line);
            line++;
        }
        return table;
    }
    /**
     * The residents' own books - the last participant in this economy that did
     * not have any.
     *
     * The line that matters is the bottom one. Retail spending is currently
     * driven by how many people there are rather than by what they earn, so
     * nothing in the model stops households being made to spend more than they
     * take home. If that happens it is money arriving from nowhere, and this is
     * the screen where it becomes visible instead of invisible.
     */
    /**
     * Whether the per-tier table shows one family or the whole city.
     *
     * A screen-level toggle rather than two tables, because they answer two
     * different questions and only one of them is being asked at a time: "can a
     * family like this live here" and "where is the city's money". Per family is
     * the default because it is the one a player can act on - a total of $12.4M
     * does not tell you whether anybody is short.
     */
    boolean householdPerFamily = true;

    /**
     * The residents' own books - the last participant in this economy that did
     * not have any.
     *
     * The line that matters is the bottom one. Retail spending is currently
     * driven by how many people there are rather than by what they earn, so
     * nothing in the model stops households being made to spend more than they
     * take home. If that happens it is money arriving from nowhere, and this is
     * the screen where it becomes visible instead of invisible.
     *
     * REBUILT 2026-09-07 (Jerus: "that part is basically unreadable"). It was
     * five separate boxes - INCOME, OUTGOINGS, WHAT IS LEFT, PENSIONS,
     * AFFORDABILITY - each with its own alignment, and the pension lines sat two
     * boxes away from the statement they belong to. It is one statement now, in
     * one column, read top to bottom: what came in, what went out, what is left.
     */
    /* =====================================================================
       CAN THE PEOPLE OF THIS CITY AFFORD TO LIVE IN IT?

       That is the whole question this screen exists to answer, and the old one
       answered it with two nested tables and seven clicks. It had a row per pay
       tier, and behind each row a second table of the eleven household shapes
       inside that tier - so the answer, which is "an unskilled single parent
       cannot and an unskilled couple can", was seven disclosures deep and never
       visible in one place.

       The two tables were the same data cut twice. Household shape and pay tier
       are two axes of one grid, and every cell of it is already computable -
       HouseholdAccounts.statementFor(families, shape, tier) has been there the
       whole time. So the grid is the screen: eleven rows of household, six
       columns of tier, and in each cell what that household has left at the end
       of the month, tinted by how badly.

       WHY THE SHAPES ARE THE ROWS. There are eleven of them and six tiers, and
       a label like "Couple, a baby and a child" needs width a column cannot
       give it. Down the side it reads in full; across the top it would have had
       to be abbreviated into something nobody could parse.

       WHAT THE GRID SAYS THAT NEITHER TABLE COULD. Rent is one figure per front
       door and the shopping basket is one figure per head, so a row gets
       cheaper to the right as income climbs, and a column gets dearer downward
       as mouths are added. The diagonal where those two meet is the line
       between a household that saves and one that does not, and on the grid you
       can see exactly where it falls.
       ===================================================================== */

    /** Which cell of the grid is open, as "tier:shape". Empty for none. */
    String openCell = "";

    /** Set by a click that opens a cell, so the panel it opened is scrolled to. */
    boolean revealOpened = false;

    /** What showSectorReport should bring into view on this draw, once. */
    javafx.scene.Node revealTop = null;
    javafx.scene.Node revealBottom = null;

    void showHouseholdMenu() {
        ui.clearMenu("showHouseholdMenu", () -> showHouseholdMenu());

        HouseholdAccounts hh = ui.game.getHouseholds();
        HouseholdBalance bal = ui.game.getHouseholdBalance();
        FamilyModel families = ui.game.getFamilies();

        double savingRate = hh.getSavingRate();
        double burden = hh.getRentBurden();
        double hunger = bal.getHungerRate();

        /* ------------------------------ the vitals ------------------------------ */
        HBox vitals = new HBox(0);
        vitals.setAlignment(Pos.CENTER);
        vitals.setMaxWidth(Region.USE_PREF_SIZE);
        vitals.setStyle("-fx-padding: 8 6 8 6;" + Palette.block(Palette.PANEL));

        VBox lastCell = limitCell("PER WORKER",
                String.format("%.2f", hh.getDependencyRatio()),
                "people carried by each job", Palette.TEXT_HEAD);
        lastCell.setStyle("-fx-padding: 0 14 0 14;");

        vitals.getChildren().addAll(
                limitCell("THEY KEEP", String.format("%.1f%%", savingRate * 100),
                        "of what they take home",
                        savingRate < 0 ? Palette.BAD
                                : savingRate < .03 ? Palette.WARN : Palette.GOOD),
                limitCell("RENT TAKES", String.format("%.0f%%", burden * 100),
                        burden > .35 ? "over a third — rent-burdened" : "of take-home pay",
                        burden > .35 ? Palette.BAD : burden > .25 ? Palette.WARN : Palette.GOOD),
                limitCell("GOING SHORT", String.format("%.1f%%", hunger * 100),
                        hunger > .01 ? "eating less than a full basket" : "everybody can eat",
                        hunger > .10 ? Palette.BAD : hunger > .01 ? Palette.WARN : Palette.GOOD),
                lastCell);

        VBox column = new VBox(0);
        column.setAlignment(Pos.TOP_LEFT);
        column.setMaxWidth(Region.USE_PREF_SIZE);

        /* ============================== THE GRID ============================== */
        column.getChildren().add(statementHead("Who can afford to live here"));

        int shortCells = 0, livedCells = 0;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (shape.isRetired()) continue;
            for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
                HouseholdAccounts.Statement s =
                        hh.statementFor(families, shape, PayTier.values()[t]);
                if (s.households() < .5) continue;
                livedCells++;
                if (s.left() < 0) shortCells++;
            }
        }

        Label verdict = new Label(shortCells == 0
                ? "Every kind of household in this city covers its month."
                : String.format("%d of the %d kinds of household in this city cannot cover"
                        + " their month. Rent is %s a home whoever lives in it and the basket"
                        + " is %s a head, so what separates them is earners against mouths.",
                        shortCells, livedCells,
                        tightMoney(toDollars(hh.rentPerHousehold()), false),
                        tightMoney(toDollars(hh.shoppingPerHead()), false)));
        verdict.setWrapText(true);
        verdict.setMaxWidth(STATEMENT);
        verdict.setStyle(Palette.words(Palette.SIZE_BODY,
                shortCells == 0 ? Palette.GOOD : Palette.BAD_TEXT)
                + " -fx-padding: 0 0 8 0;");
        column.getChildren().add(verdict);

        HBox views = new HBox(6);
        views.setAlignment(Pos.CENTER_LEFT);
        views.setStyle("-fx-padding: 0 0 8 0;");
        views.getChildren().addAll(
                viewTab("One household", householdPerFamily, () -> {
                    householdPerFamily = true;
                    showHouseholdMenu();
                }),
                viewTab("All of them", !householdPerFamily, () -> {
                    householdPerFamily = false;
                    showHouseholdMenu();
                }));
        column.getChildren().add(views);

        VBox grid = affordabilityGrid(hh, bal, families);
        column.getChildren().add(grid);
        column.getChildren().add(statementNote(
                "Below the rule: the retired, on a pension and no wage; the out of work, on "
                + "EI for twelve months and then on what they saved and what the bank will "
                + "lend; students, on a grant, their savings and a student loan that never "
                + "runs out; the orphans, who have nothing; and the prison, which feeds its "
                + "own. None of them has a pay tier, so none of them has a row of six - what "
                + "their one cell says is what a month leaves them against a full basket."));

        /* --------------- and the cell somebody has clicked on --------------- */
        VBox opened = openStatement(hh, bal, families);
        if (opened != null) column.getChildren().add(opened);

        /*
         * THE GRID IS WHAT GETS REVEALED, not the panel. Scrolling the panel to
         * the top of the viewport would push the grid off it, and the grid is
         * the thing that made the panel interesting - so the top of the grid
         * goes to the top of the view and the panel lands underneath it, both
         * on screen at once.
         */
        if (revealOpened) {
            revealTop = grid;
            revealBottom = opened;
            revealOpened = false;
        }


        /* ======================= THE CITY'S OWN MONTH ======================= */
        column.getChildren().add(statementHead("The city's month"));
        column.getChildren().add(statementLine("Wages earned",
                tightMoney(toDollars(hh.getWages()), false)));
        column.getChildren().add(statementLine(
                String.format("Wage tax at %.0f%%", hh.getEffectiveTaxRate() * 100),
                tightMoney(toDollars(-hh.getWageTax()), false), Palette.WARN));
        column.getChildren().add(statementLine(
                String.format("Pension contributions at %.1f%%",
                        ui.game.getEconomyManager().getTaxPolicy().getContributionRate() * 100),
                tightMoney(toDollars(-hh.getContributions()), false), Palette.WARN));
        column.getChildren().add(statementLine("Pensions received",
                tightMoney(toDollars(hh.getPensions()), false), "#8ed4ff"));
        // EI and the grants (2026-09-11): off the same payslips, and in to the out of work and the students.
        column.getChildren().add(statementLine(
                String.format("EI premiums at %.2f%%",
                        ui.game.getEconomyManager().getTaxPolicy().getEiPremiumRate() * 100),
                tightMoney(toDollars(-hh.getEiPremiums()), false), Palette.WARN));
        column.getChildren().add(statementLine("EI received",
                tightMoney(toDollars(hh.getEiBenefits()), false), "#8ed4ff"));
        column.getChildren().add(statementLine("Student grants received",
                tightMoney(toDollars(hh.getStudentGrants()), false), "#8ed4ff"));
        column.getChildren().add(statementTotal("Take-home",
                tightMoney(toDollars(hh.getDisposableIncome()), false), null));
        column.getChildren().add(statementLine("Rent to landlords",
                tightMoney(toDollars(-hh.getRent()), false)));
        column.getChildren().add(statementLine("Healthcare fees",
                tightMoney(toDollars(-hh.getHealthcare()), false)));
        column.getChildren().add(statementLine("School fees",
                tightMoney(toDollars(-hh.getTuition()), false)));
        // The third fee, and the one the city used to collect from nobody.
        // See HouseholdAccounts.fares.
        column.getChildren().add(statementLine("Transit fares",
                tightMoney(toDollars(-hh.getFares()), false)));
        column.getChildren().add(statementLine("Interest on debt",
                tightMoney(toDollars(-hh.getInterest()), false)));
        column.getChildren().add(statementLine("Spent in the shops",
                tightMoney(toDollars(-hh.getShopping()), false)));

        double saved = hh.getNetSaving();
        column.getChildren().add(statementTotal(
                hh.isLivingBeyondIncome() ? "Short by" : "Saved",
                tightMoney(toDollars(saved), false),
                saved < 0 ? Palette.BAD : Palette.GOOD));
        column.getChildren().add(statementNote(hh.isLivingBeyondIncome()
                ? "Spending more than they earn — nothing in the model funds this."
                : String.format("%s saved since founding. A record, not a pot.",
                        tightMoney(toDollars(hh.getCumulativeSaving())))));
        column.getChildren().add(statementNote(
                "Every figure on this screen is in dollars, not the thousands the rest"
                + " of the game counts in."));

        /* ========================= WHAT THEY HAVE ========================= */
        column.getChildren().add(statementHead("What they have put by"));
        column.getChildren().add(statementLine("Saved",
                tightMoney(toDollars(bal.totalSavings()), false),
                bal.totalSavings() > 0 ? Palette.GOOD : null));
        column.getChildren().add(statementLine("Owed to lenders",
                tightMoney(toDollars(-bal.totalDebt()), false),
                bal.totalDebt() > 0 ? Palette.BAD : null));
        column.getChildren().add(statementLine("Interest on it",
                tightMoney(toDollars(-bal.totalInterest()), false)));
        double dividends = bal.totalDividends();
        if (dividends > 0 || ui.game.getEquity().getLifetimeDividendsHome() > 0) {
            column.getChildren().add(statementLine("Dividends this month",
                    tightMoney(toDollars(dividends), false), dividends > 0 ? Palette.GOOD : null));
        }

        /*
         * AND THE CARS, WHICH ARE THE ONLY THING ON THIS LINE THAT IS NOT MONEY
         * (2026-09-17).
         *
         * A car sits beside savings, debt, shares and the dollars abroad in the
         * household's own books - it follows the people, it is saved per cell,
         * a household that leaves takes it - and it is the first durable this
         * city has ever been able to own. It belongs here because it is a fact
         * about households; what it is DOING, which is most of what is wrong
         * with the road, is on Infrastructure > Roads.
         */
        if (bal.totalCars() > 0 || ui.game.getHouseholdCarsBought() > 0) {
            column.getChildren().add(statementLine("Cars owned",
                    formatter.format(Math.round(bal.totalCars()))
                            + String.format("  (%.0f%% of households)",
                                    bal.carsPerHousehold() * 100),
                    Palette.TEXT_HEAD));
            if (ui.game.getHouseholdCarsBought() > 0) {
                column.getChildren().add(statementLine("Bought this month",
                        formatter.format(Math.round(ui.game.getHouseholdCarsBought()))
                                + " for " + tightMoney(toDollars(ui.game.getHouseholdCarSpend()), false),
                        Palette.WARN));
            }
            column.getChildren().add(statementNote(
                    "Bought out of savings past the same cushion a share is, so income "
                    + "decides who can afford one - and a city with transit that could "
                    + "carry everybody sees half of its households never bother. What it "
                    + "costs the road is on Infrastructure."));
        }

        // Every cell keeps its own books, so this can be said by household
        // rather than by tier: who the bank has stopped lending to, and who
        // has been discharged and is waiting out the year.
        double cutOff = 0, lockedOut = 0;
        for (Household c : bal.cells()) {
            if (c.isLockedOut()) lockedOut += c.households();
            else if (c.isCutOff()) cutOff += c.households();
        }
        if (cutOff + lockedOut >= .5) {
            column.getChildren().add(statementNote(String.format(
                    "%s households are at their credit ceiling and %s have been discharged"
                    + " and cannot borrow. Click a cell above to see which.",
                    formatter.format(Math.round(cutOff)),
                    formatter.format(Math.round(lockedOut)))));
        }
        column.getChildren().add(statementLine("Income per resident",
                tightMoney(toDollars(hh.getIncomePerResident()), false)));
        column.getChildren().add(statementLine("An average filled job pays",
                tightMoney(toDollars(hh.getAverageWage()), false)));
        column.getChildren().add(statementNote(String.format(
                "%s people, %s in the workforce, %s of them in a job.",
                formatter.format(hh.getPopulation()),
                formatter.format(hh.getWorkforce()),
                formatter.format(hh.getJobsFilled()))));

        double priced = families == null ? 0 : families.getPricedOutShares();
        if (priced >= .5) {
            column.getChildren().add(statementNote(String.format(
                    "%s households are flatsharing because one wage will not cover a"
                    + " home — five to a door, five baskets, one rent.",
                    formatter.format(Math.round(priced)))));
        }

        /* ---------------- the order things get paid in ---------------- */
        if (shortCells > 0) {
            VBox whyDetail = new VBox(2);
            Label why = new Label(
                    "The order is fixed: the payslip first, then rent, then the fees, and "
                    + "the shop takes what is left. A household short of the last one "
                    + "spends its savings, then borrows — at a rate that climbs with what "
                    + "it already owes — and only when both are gone does it eat less. "
                    + "That last step is a health problem: hunger is in the sick rate.");
            why.setWrapText(true);
            why.setMaxWidth(STATEMENT - 40);
            why.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)
                    + " -fx-padding: 2 0 4 0;");
            whyDetail.getChildren().add(why);
            column.getChildren().add(disclosure(
                    "What happens to a household that comes up short", "why?", whyDetail));
        }

        VBox all = new VBox(0, vitals, column);
        all.setAlignment(Pos.CENTER);
        ui.showSectorReport("HOUSEHOLD CASH FLOW", all, this::showPopulationInfoMenu);
    }

    /** One of the two view buttons over the grid. */
    Button viewTab(String label, boolean on, Runnable go) {
        Button tab = new Button(label);
        tab.setStyle(Palette.words(Palette.SIZE_LABEL,
                    on ? Palette.TEXT_HEAD : Palette.TEXT_BODY)
                + " -fx-background-color: " + (on ? Palette.RAISED : Palette.CONTROL) + ";"
                + " -fx-background-radius: " + Palette.RADIUS_TIGHT + ";"
                + " -fx-border-color: " + (on ? Palette.ACCENT : "transparent") + ";"
                + " -fx-border-width: 0 0 2 0; -fx-cursor: hand;");
        tab.setOnAction(e -> go.run());
        return tab;
    }

    /** How wide a tier column is. Six of them, plus the household name. */
    static final double TIER_COL = 88;
    static final double SHAPE_COL = 168;

    /**
     * Household shape down the side, pay tier across the top, and in every cell
     * what that household has left at the end of the month.
     */
    VBox affordabilityGrid(HouseholdAccounts hh, HouseholdBalance bal,
                                   FamilyModel families) {

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(3);
        grid.setVgap(3);

        javafx.scene.layout.ColumnConstraints nameCol =
                new javafx.scene.layout.ColumnConstraints(SHAPE_COL);
        nameCol.setHalignment(javafx.geometry.HPos.LEFT);
        grid.getColumnConstraints().add(nameCol);
        for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
            javafx.scene.layout.ColumnConstraints spec =
                    new javafx.scene.layout.ColumnConstraints(TIER_COL);
            spec.setHalignment(javafx.geometry.HPos.RIGHT);
            grid.getColumnConstraints().add(spec);
        }

        grid.add(gridCell("household", Palette.TEXT_LABEL, Palette.SIZE_CAPTION, false), 0, 0);
        for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
            Label head = gridCell(hh.getRowLabel(t), Palette.TEXT_LABEL,
                    Palette.SIZE_CAPTION, true);
            head.setWrapText(true);
            /*
             * A WRAPPED LABEL ALIGNS ITS BOX, NOT ITS LINES. gridCell sets
             * CENTER_RIGHT, which puts the label at the right of its column;
             * the text inside it still ran left, so "Senior professional"
             * broke over two lines flush against the column's LEFT edge and
             * read as though it had collided with "Professional" next door.
             */
            head.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);
            grid.add(head, t + 1, 0);
        }

        int line = 1;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (shape.isRetired()) continue;

            // A shape nobody in the city lives in is not a row worth a line.
            boolean lived = false;
            for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
                if (hh.statementFor(families, shape, PayTier.values()[t]).households() >= .5) {
                    lived = true;
                }
            }
            if (!lived) continue;

            grid.add(gridCell(shape.getLabel(), Palette.TEXT_BODY,
                    Palette.SIZE_CAPTION, false), 0, line);

            for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
                grid.add(cashCell(hh, families, shape, PayTier.values()[t], t), t + 1, line);
            }
            line++;
        }

        /* THE TIER ITSELF, on the bottom line - the average of the column above
         * it, which is the household nobody actually lives in and the figure the
         * old screen led with. Kept, and kept last. */
        grid.add(gridCell("every household of that tier", Palette.TEXT_LABEL,
                Palette.SIZE_CAPTION, false), 0, line);
        for (int t = 0; t < HouseholdAccounts.RETIRED; t++) {
            double homes = hh.getRowHouseholds(t);
            double per = householdPerFamily && homes >= .5 ? homes : 1;
            double left = toDollars(hh.getRowSaving(t)) / per;
            Label cell = gridCell(homes < .5 ? "—" : tightMoney(left, !householdPerFamily),
                    homes < .5 ? Palette.TEXT_LABEL
                            : left < 0 ? Palette.BAD : Palette.GOOD,
                    Palette.SIZE_CAPTION, true);
            cell.setStyle(cell.getStyle() + " -fx-padding: 4 4 4 4;"
                    + (bal.isGoingShort(t) ? " -fx-border-color: " + Palette.ALERT_EDGE
                            + "; -fx-border-width: 0 0 2 0;" : ""));
            grid.add(cell, t + 1, line);
        }
        line++;

        /*
         * ...AND EVERYBODY WHO HAS NO PAY TIER, IN THE SAME GRID.
         *
         * Jerus: "the others should be in the matrix in some way, not on their
         * own thing in the bottom." They were two more tables underneath this
         * one, each with its own columns in its own widths, so the retired and
         * the out of work read as a different kind of citizen from a couple
         * with a child - which they are not. What they have not got is a PAY
         * TIER, and a pay tier is the only thing the six columns above say.
         *
         * So the row keeps the shape column and gives up the tier columns: one
         * wide cell across all six, tinted and clickable on exactly cashCell's
         * rule, carrying how many of them there are and what a month leaves
         * one of them.
         */
        line = otherRows(grid, hh, bal, families, line);

        VBox box = new VBox(grid);
        box.setStyle("-fx-padding: 2 0 8 0;");
        return box;
    }

    /** A rule and a caption across the matrix: a different kind of household below. */
    int gridSectionRow(javafx.scene.layout.GridPane grid, String caption, int line) {
        Label head = new Label(caption);
        head.setMaxWidth(Double.MAX_VALUE);
        head.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_LABEL)
                + " -fx-padding: 14 0 3 0;"
                + " -fx-border-color: " + Palette.EDGE + " transparent transparent transparent;"
                + " -fx-border-width: 1 0 0 0;");
        grid.add(head, 0, line);
        javafx.scene.layout.GridPane.setColumnSpan(head, HouseholdAccounts.RETIRED + 1);
        return line + 1;
    }

    /**
     * The retired, the out of work, the students, the orphans and the prison,
     * as rows of the same matrix.
     *
     * Both kinds are drawn from the same two things - a Household ledger and
     * what a month leaves it - so neither can drift from the other. The retired
     * reach their figure through HouseholdAccounts.statementFor(), because a
     * pension and a rent make a statement; everybody else reaches it through
     * their own books, because they have no shape to derive one from.
     */
    int otherRows(javafx.scene.layout.GridPane grid, HouseholdAccounts hh,
                          HouseholdBalance bal, FamilyModel families, int line) {

        if (bal == null) return line;

        /* ------------------------------ the retired ------------------------------ */
        boolean headed = false;
        for (FamilyStructure shape : FamilyStructure.values()) {
            if (!shape.isRetired()) continue;
            HouseholdAccounts.Statement s =
                    hh.statementFor(families, shape, PayTier.values()[0]);
            if (s.households() < .5) continue;
            if (!headed) {
                line = gridSectionRow(grid, "the retired · no tier, and a pension of "
                        + tightMoney(toDollars(hh.getPensionPerSenior()), false)
                        + " a head", line);
                headed = true;
            }
            line = otherRow(grid, shape.getLabel(), bal.cell(shape),
                    toDollars(s.left()), s.households(), line);
        }

        /* ------------------------ and outside the families ------------------------ */
        java.util.List<Household> rest = new java.util.ArrayList<>();
        for (UnemployedHousehold.Status st : UnemployedHousehold.Status.values()) {
            rest.add(bal.unemployed(st));
        }
        rest.add(bal.students());
        for (AgeBand b : AgeBand.values()) if (bal.orphans(b) != null) rest.add(bal.orphans(b));
        // The prisoners kept their own books from the day they were built and
        // no grid ever listed them. Out of the labour force, debts frozen.
        if (bal.prisoners() != null) rest.add(bal.prisoners());

        headed = false;
        for (Household own : rest) {
            if (own == null || own.households() < .5) continue;
            if (!headed) {
                line = gridSectionRow(grid,
                        "outside the families · no tier, and no wage to have one", line);
                headed = true;
            }
            /*
             * MEASURED AGAINST A FULL BASKET rather than against what they ate.
             * Orphans have no income, no bills and nothing put by: their month
             * nets to exactly zero, and a column of zeroes would paint starving
             * babies the same colour as a household that broke even. What they
             * are short of is the FOOD.
             */
            line = otherRow(grid, own.label(), own,
                    toDollars(own.afterFixed() - own.want()), own.households(), line);
        }
        return line;
    }

    /** One matrix row for a household with no tier: a name, then one wide cell. */
    int otherRow(javafx.scene.layout.GridPane grid, String label, Household own,
                         double perHousehold, double homes, int line) {

        boolean trouble = own.isCutOff() || own.isGoingShort();
        grid.add(gridCell(label, trouble ? Palette.BAD_SOFT : Palette.TEXT_BODY,
                Palette.SIZE_CAPTION, false), 0, line);

        javafx.scene.Node cell = wideCell(own, perHousehold, homes);
        grid.add(cell, 1, line);
        javafx.scene.layout.GridPane.setColumnSpan(cell, HouseholdAccounts.RETIRED);
        return line + 1;
    }

    /**
     * cashCell's cell, one row wide: the same tint, the same ring, the same
     * click - with the headcount inside it, because the six tier columns it
     * spans have nothing of their own to say about this household.
     *
     * A ZERO IS NOT A SURPLUS. The prison feeds its own, so its basket is zero
     * and its month nets to exactly nothing; painting that the same green as a
     * couple banking $7,000 was the old grid's worst line. Nothing-to-say gets
     * a grey wash and a grey figure.
     */
    javafx.scene.Node wideCell(Household own, double perHousehold, double homes) {

        double shown = perHousehold * (householdPerFamily ? 1 : Math.max(1, homes));
        double base = Math.max(toDollars(own.want()),
                Math.max(toDollars(own.disposable()), 1));
        double severity = Math.min(1, Math.abs(perHousehold) / base);

        boolean quiet = Math.abs(shown) < .005;
        String tone = quiet ? Palette.TEXT_MUTED : shown < 0 ? Palette.BAD : Palette.GOOD;
        String wash = quiet ? "rgba(255,255,255,0.04)"
                : "rgba(" + (shown < 0 ? "255,107,107," : "95,214,138,")
                        + String.format("%.2f", .08 + severity * .30) + ")";

        String key = "OUT:" + own.key();
        boolean open = key.equals(openCell);

        Label many = new Label(shortNumber(homes) + " of them");
        many.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label figure = new Label(tightMoney(shown, !householdPerFamily));
        figure.setStyle(Palette.figure(Palette.SIZE_CAPTION, tone));

        HBox box = new HBox(8, many, gap, figure);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle("-fx-padding: 4 6 4 6; -fx-cursor: hand;"
                + " -fx-background-radius: " + Palette.RADIUS_TIGHT + ";"
                + " -fx-background-color: " + wash + ";"
                + (open ? " -fx-border-color: " + Palette.ACCENT
                        + "; -fx-border-radius: " + Palette.RADIUS_TIGHT + ";" : ""));

        Tooltip tip = new Tooltip(own.label()
                + "\n" + people(own.households()) + " of them"
                + "\nwhat a month leaves one of them, against a full basket"
                + "\nclick for the month");
        tip.setShowDelay(Duration.millis(300));
        Tooltip.install(box, tip);

        box.setOnMouseClicked(e -> {
            openCell = open ? "" : key;
            revealOpened = !openCell.isEmpty();
            showHouseholdMenu();
        });
        return box;
    }

    /**
     * One cell: what this household has left, and how badly.
     *
     * TINTED BY SEVERITY rather than just coloured by sign. A household $12
     * short and a household $1,300 short are both in the red and only one of
     * them is a crisis; a wash whose strength follows the shortfall against the
     * income says which without another column of figures.
     */
    Label cashCell(HouseholdAccounts hh, FamilyModel families,
                           FamilyStructure shape, PayTier tier, int tierIndex) {

        HouseholdAccounts.Statement s = hh.statementFor(families, shape, tier);

        if (s.households() < .5) {
            Label none = gridCell("—", Palette.TEXT_LABEL, Palette.SIZE_CAPTION, true);
            none.setStyle(none.getStyle() + " -fx-padding: 4 4 4 4;");
            return none;
        }

        // statementFor gives ONE household of this shape and tier; multiplied up
        // when the player asked for the whole city rather than one family.
        double left = toDollars(s.left()) * (householdPerFamily ? 1 : s.households());
        double severity = s.income() > 0
                ? Math.min(1, Math.abs(s.left()) / Math.abs(s.income())) : 0;
        double alpha = .08 + severity * .30;

        String key = tierIndex + ":" + shape.name();
        boolean open = key.equals(openCell);

        Label cell = gridCell(tightMoney(left, !householdPerFamily),
                s.left() < 0 ? Palette.BAD : Palette.GOOD, Palette.SIZE_CAPTION, true);
        cell.setStyle(cell.getStyle()
                + " -fx-padding: 4 4 4 4; -fx-cursor: hand;"
                + " -fx-background-radius: " + Palette.RADIUS_TIGHT + ";"
                + " -fx-background-color: rgba("
                + (s.left() < 0 ? "255,107,107," : "95,214,138,")
                + String.format("%.2f", alpha) + ");"
                + (open ? " -fx-border-color: " + Palette.ACCENT
                        + "; -fx-border-radius: " + Palette.RADIUS_TIGHT + ";" : ""));

        Tooltip tip = new Tooltip(shape.getLabel() + ", " + hh.getRowLabel(tierIndex)
                + "\n" + formatter.format(Math.round(s.households())) + " of them"
                + "\nclick for the month");
        tip.setShowDelay(Duration.millis(300));
        Tooltip.install(cell, tip);

        cell.setOnMouseClicked(e -> {
            openCell = open ? "" : key;
            revealOpened = !openCell.isEmpty();
            showHouseholdMenu();
        });
        return cell;
    }

    /**
     * What a full basket would have been, what they ate, and where the
     * difference came from.
     *
     * SHARED BY BOTH PANELS since the families asked for it too. Every cell in
     * the game carries this - `want`, `planned`, `unfunded`, `drawn`,
     * `borrowed`, `banked` - written by the month as it happened rather than
     * derived afterwards from a shape and a tier, and until now only the
     * families' statement was drawn at all and it did not include any of it.
     * A household that ate its full basket out of its savings and one that ate
     * two thirds of it looked identical.
     */
    VBox basketBlock(Household cell) {

        VBox block = new VBox(0);
        double wanted = toDollars(cell.want());
        double spent = toDollars(cell.planned());
        if (wanted <= .5 && spent <= .5) return block;

        Label head = new Label("The shopping, and how it was paid for");
        head.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL)
                + " -fx-padding: 10 0 2 0;");
        block.getChildren().add(head);

        block.getChildren().add(statementLine("A full basket would be",
                tightMoney(wanted, false), Palette.TEXT_MUTED));
        block.getChildren().add(statementLine("What they actually ate",
                tightMoney(spent, false),
                spent < wanted - .5 ? Palette.BAD : Palette.GOOD));

        double shortBy = toDollars(cell.unfunded());
        if (shortBy > .5) {
            block.getChildren().add(statementLine("Could not fund",
                    tightMoney(shortBy, false), Palette.BAD));
        }
        if (spent < wanted - .5 && shortBy <= .5) {
            block.getChildren().add(statementNote(
                    "Nothing funds the difference: there is no income, nothing put by and "
                    + "nobody to borrow from, so the basket is simply not bought."));
        }

        double drawn = toDollars(cell.drawn());
        double borrowed = toDollars(cell.borrowed());
        double banked = toDollars(cell.banked());
        if (drawn > .5) {
            block.getChildren().add(statementLine("Drawn from savings",
                    tightMoney(drawn, false), Palette.WARN));
        }
        if (borrowed > .5) {
            block.getChildren().add(statementLine("Borrowed",
                    tightMoney(borrowed, false), Palette.BAD));
        }
        if (banked > .5) {
            block.getChildren().add(statementLine("Put by",
                    tightMoney(banked, false), Palette.GOOD));
        }
        return block;
    }

    /* =====================================================================
       THE MONEY BLOCKS BOTH PANELS SHARE

       Jerus: "i also want a better panel (the financial stuff) thats what i
       mean by more detailed, i want to expand that further, its great, but
       more."

       The month was six lines and the position was four. Everything added
       below was already in the model and had never been drawn: what the bank
       will still lend, what was borrowed and paid off this month, what a
       student loan has left to run, how long savings cover a shortfall, and
       what share of a pay packet each claim on it takes.

       WRITTEN ONCE AND CALLED TWICE on purpose. Two panels that have to agree
       agree because they are the same code, not because somebody kept them in
       step - the rule the rent-units bug taught this codebase.
       ===================================================================== */

    /** A cost, printed negative - but a cost of nothing reads "$0", never "-$0". */
    String costMoney(double dollars) {
        double a = Math.abs(dollars);
        return a < .005 ? "$0" : tightMoney(-a, false);
    }

    /** A stake, with enough decimals left on it to still say something. */
    static String stakePct(double share) {
        double p = share * 100;
        if (p >= 1)   return String.format("%.1f%%", p);
        if (p >= .1)  return String.format("%.2f%%", p);
        if (p >= .01) return String.format("%.3f%%", p);
        return String.format("%.4f%%", p);
    }

    /**
     * What part is of whole - or a dash, when there is no whole to be part of.
     *
     * A SHARE THAT ROUNDS TO NOTHING SAYS SO IN WORDS. 1,361 out-of-work
     * households in a city of half a million printed "0% of the city's
     * households", which reads as none of them rather than as very few.
     */
    static String shareOf(double part, double whole) {
        if (Math.abs(whole) < 1e-9) return "—";
        double p = part / whole * 100;
        if (p > 0 && p < .05) return "under 1%";
        if (Math.abs(p) < 10)  return String.format("%.1f%%", p);
        return String.format("%.0f%%", p);
    }

    /** A run of months, said the way a person would say it. */
    static String monthsRun(double months) {
        if (!Double.isFinite(months) || months >= 600) return "longer than a lifetime";
        if (months >= 24) return String.format("%.0f years", months / 12);
        if (months < 1)   return "less than a month";
        return String.format("%.0f months", months);
    }

    /** The small heading that divides a statement panel into blocks. */
    Label panelBlockHead(String text) {
        Label head = new Label(text);
        head.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL)
                + " -fx-padding: 12 0 2 0;");
        return head;
    }

    /** How many of them there are, how big each one is, and how much of the city that is. */
    Label panelWho(HouseholdBalance bal, Household cell) {
        double allHomes = 0, allPeople = 0;
        for (Household c : bal.cells()) {
            allHomes += c.households();
            allPeople += c.people();
        }
        Label many = new Label(people(cell.households()) + " of them, "
                + (cell.size() == 1 ? "one person each" : cell.size() + " people each")
                + "  ·  " + shareOf(cell.households(), allHomes)
                + " of the city's households, "
                + shareOf(cell.people(), allPeople) + " of its people");
        many.setWrapText(true);
        many.setMaxWidth(STATEMENT - 28);
        many.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED)
                + " -fx-padding: 0 0 6 0;");
        return many;
    }

    /**
     * What one of these households HAS, what it OWES, what it OWNS and what
     * all of that leaves it worth.
     *
     * The month above is a flow. This is the stock behind it, and it is the
     * half that decides whether a bad month is survivable: a household short
     * $400 with six months of credit left and one at the ceiling are the same
     * red figure and completely different situations.
     */
    void positionBlock(VBox panel, HouseholdBalance bal, Household own) {

        panel.getChildren().add(panelBlockHead("What one of them has"));

        double put = toDollars(own.savings());
        double owe = toDollars(own.debt());

        panel.getChildren().add(statementLine("Put by", tightMoney(put, false),
                own.savings() > 0 ? Palette.GOOD : null));
        double moved = toDollars(own.banked() - own.drawn());
        if (Math.abs(moved) > .005) {
            panel.getChildren().add(statementLine(
                    moved > 0 ? "...added this month" : "...drawn down this month",
                    tightMoney(Math.abs(moved), false),
                    moved > 0 ? Palette.GOOD : Palette.WARN));
        }

        panel.getChildren().add(statementLine("Owed to lenders", costMoney(owe),
                own.debt() > 0 ? Palette.BAD : null));
        if (own.debt() > 0) {
            panel.getChildren().add(statementLine(
                    String.format("Interest at %.1f%% a year", own.rate() * 100),
                    costMoney(toDollars(own.interest()))));
        }
        if (own.borrowed() > .005) {
            panel.getChildren().add(statementLine("...borrowed this month",
                    tightMoney(toDollars(own.borrowed()), false), Palette.BAD));
        }
        if (own.repaid() > .005) {
            panel.getChildren().add(statementLine("...paid off this month",
                    tightMoney(toDollars(own.repaid()), false), Palette.GOOD));
        }
        if (own.bankrupt() > .005) {
            panel.getChildren().add(statementLine("...written off this month",
                    tightMoney(toDollars(own.bankrupt()), false), Palette.BAD));
        }

        /*
         * WHAT THE BANK WILL STILL LEND, which is the figure that decides
         * whether a short month is survivable or is the month they stop eating.
         * Read from Household.creditRoom - the same call the month itself
         * makes, so the screen shows the number the model used rather than a
         * second opinion about it.
         */
        double room = toDollars(own.creditRoom(own.disposable()));
        panel.getChildren().add(statementLine("Room still to borrow",
                own.isLockedOut() ? "none - locked out" : tightMoney(room, false),
                own.isLockedOut() ? Palette.BAD : room > .5 ? null : Palette.WARN));

        if (own.studentDebt() > 0) {
            panel.getChildren().add(statementLine("Student loan outstanding",
                    costMoney(toDollars(own.studentDebt())), Palette.WARN));
            if (own.studentBorrowed() > .005) {
                panel.getChildren().add(statementLine("...drawn this month",
                        tightMoney(toDollars(own.studentBorrowed()), false), Palette.WARN));
            }
            if (own.studentRepaid() > .005) {
                panel.getChildren().add(statementLine("...repaid this month",
                        tightMoney(toDollars(own.studentRepaid()), false), Palette.GOOD));
                panel.getChildren().add(statementNote("At this month's rate it has "
                        + monthsRun(own.studentDebt() / own.studentRepaid())
                        + " left to run."));
            } else {
                panel.getChildren().add(statementNote(
                        "Nothing is coming off it: a student loan is only collected out of a wage."));
            }
        }

        /*
         * AND WHAT IT OWNS. Shares in the city's companies - bought at
         * offerings out of what was past the cushion, or held since the
         * founding. At the desk's quote while there is an exchange, at book
         * when the bank is dead and there is none.
         */
        Equity register = ui.game.getEquity();
        Exchange exchange = ui.game.getExchange();
        double shareWorth = 0;
        int held = 0;
        StringBuilder holdings = new StringBuilder();
        StringBuilder names = new StringBuilder();
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            if (own.shares(c) <= 0 || register.getShares(c) <= 0) continue;
            double stake = own.shares(c) / register.getShares(c);
            double book = c == Equity.BANK ? ui.game.getBank().equity()
                    : ui.game.getSectorBooks().get(Equity.COMPANIES[c]).equity();
            shareWorth += exchange.isOpen()
                    ? own.shares(c) * exchange.mid(c) : stake * Math.max(0, book);
            held++;
            if (holdings.length() > 0) holdings.append(", ");
            holdings.append(Equity.COMPANIES[c]).append(' ').append(stakePct(stake));
            if (names.length() > 0) names.append(", ");
            names.append(Equity.COMPANIES[c]);
        }
        double shares = toDollars(shareWorth);
        if (held > 0) {
            panel.getChildren().add(statementLine(
                    exchange.isOpen() ? "Shares, at the market" : "Shares, at book",
                    tightMoney(shares, false), Palette.GOOD));
            panel.getChildren().add(statementLine("Dividends this month",
                    tightMoney(toDollars(own.dividends()), false),
                    own.dividends() > 0 ? Palette.GOOD : null));
            if (own.sold() > 0) {
                panel.getChildren().add(statementLine("Sold to cover the month",
                        tightMoney(toDollars(own.sold()), false), Palette.WARN));
            }
            /*
             * ELEVEN SECTORS AT THE SAME STAKE IS NOT ELEVEN FACTS. A household
             * that bought at every offering owns the same sliver of everything,
             * and the line that listed them printed that sliver eleven times -
             * a wall of "0.0000%", which is the one thing a percentage cannot
             * usefully carry. What it is worth is already on the line above;
             * what this line is for is WHICH companies, so once there are more
             * than a couple of them that is all it says.
             */
            panel.getChildren().add(statementNote(
                    held >= Equity.COMPANIES.length
                            ? "A slice of every company in the city."
                            : held >= 3
                                    ? "A slice of " + held + " of the city's companies: "
                                            + names + "."
                                    : "Owns " + holdings + "."));
        }

        /* ...AND WHAT IT KEEPS ABROAD: the world's paper, bought when the world
         * paid more than the bank. See HouseholdBalance.investAbroad(). */
        double away = 0;
        if (own.abroad() > 0) {
            away = toDollars(own.abroadValue(bal.getExchangeRate()));
            panel.getChildren().add(statementLine("Kept abroad",
                    tightMoney(away, false)
                            + " (US" + tightMoney(toDollars(own.abroad()), false) + ")",
                    Palette.GOOD));
            if (own.sentAbroad() > 0) {
                panel.getChildren().add(statementLine("...sent out this month",
                        tightMoney(toDollars(own.sentAbroad()), false)));
            }
            if (own.broughtHome() > 0) {
                panel.getChildren().add(statementLine("...brought home this month",
                        tightMoney(toDollars(own.broughtHome()), false)));
            }
            if (own.foreignInterest() > 0) {
                panel.getChildren().add(statementLine("...interest earned out there",
                        tightMoney(toDollars(own.foreignInterest()), false), Palette.GOOD));
            }
        }

        double net = put + shares + away - owe;
        panel.getChildren().add(statementTotal("What one of them is worth",
                tightMoney(net, false), net < 0 ? Palette.BAD : Palette.GOOD));

        /*
         * AND HOW LONG THAT LASTS, which is the question a red cell actually
         * raises. Measured against a FULL basket, so a household eating less
         * than it wants is counted as short by the difference rather than as
         * having balanced its month by going hungry.
         */
        double shortfall = -toDollars(own.afterFixed() - own.want());
        if (shortfall > .005) {
            double cushion = Math.max(0, put + shares + away);
            panel.getChildren().add(statementNote(cushion > .005
                    ? "Short " + tightMoney(shortfall, false) + " a month, with "
                            + monthsRun(cushion / shortfall) + " of cover behind it."
                    : "Short " + tightMoney(shortfall, false)
                            + " a month with nothing behind it."));
        }

        if (own.isLockedOut()) {
            panel.getChildren().add(statementNote(String.format(
                    "Discharged: nothing will lend to them for another %d month%s, and what "
                    + "they cannot pay for they go without.",
                    own.lockout(), own.lockout() == 1 ? "" : "s")));
        } else if (own.isCutOff()) {
            panel.getChildren().add(statementNote(
                    "At the credit ceiling: what they cannot fund, they go without."));
        } else if (own.isGoingShort()) {
            panel.getChildren().add(statementNote(
                    "Buying less than they want this month."));
        }
        if (own.evicted() > .5) {
            panel.getChildren().add(statementNote(people(own.evicted())
                    + " of them lost their home this month: the rent went unpaid, and a "
                    + "quarter of those evicted leave the city rather than stay in it."));
        }
    }

    /**
     * The same month again, as shares rather than figures.
     *
     * Two households on very different money can be in identical trouble, and
     * the figures do not say so - $400 left on $6,000 and $40 left on $600 read
     * as a tenfold difference and are the same household. The percentages are
     * the comparison the grid above cannot make.
     */
    void ratioBlock(VBox panel, Household own, double income, double tax,
                            String billsLabel, double bills, double fees,
                            double shopping, double left) {

        double takeHome = income - tax;
        if (takeHome <= .005) return;

        panel.getChildren().add(panelBlockHead("Where the money goes"));
        if (tax > .005) {
            panel.getChildren().add(statementLine("Tax and contributions take",
                    shareOf(tax, income) + " of what they earn", Palette.WARN));
        }
        double burden = bills / takeHome;
        panel.getChildren().add(statementLine(billsLabel,
                shareOf(bills, takeHome) + " of take-home",
                burden > .35 ? Palette.BAD : burden > .25 ? Palette.WARN : null));
        if (fees > .005) {
            panel.getChildren().add(statementLine("Fees and interest take",
                    shareOf(fees, takeHome) + " of take-home"));
        }
        panel.getChildren().add(statementLine("The shopping takes",
                shareOf(shopping, takeHome) + " of take-home"));
        panel.getChildren().add(statementLine("They keep",
                shareOf(left, takeHome) + " of take-home",
                left < 0 ? Palette.BAD : Palette.GOOD));
        if (own.debt() > 0) {
            panel.getChildren().add(statementLine("What they owe is",
                    monthsRun(toDollars(own.debt()) / takeHome) + " of take-home",
                    Palette.WARN));
        }
    }

    /**
     * One of the people outside the families, and what a month does to them.
     *
     * The families' panel is built from HouseholdAccounts.statementFor(), which
     * derives a month for a shape and a tier. These cells have no tier and no
     * shape - what they have is their OWN ledger, which the month wrote as it
     * happened. The retired come through here too since 2026-09-14: they have a
     * shape and no tier, and a pension is not a wage.
     */
    VBox outsideStatement(HouseholdBalance bal) {

        if (bal == null) return null;
        String want = openCell.substring(4);
        Household cell = null;
        for (Household c : bal.cells()) if (c.key().equals(want)) { cell = c; break; }
        if (cell == null || cell.households() < .5) { openCell = ""; return null; }

        VBox panel = new VBox(0);
        panel.setMaxWidth(STATEMENT);
        panel.setStyle("-fx-padding: 10 14 12 14;"
                + Palette.block(Palette.PANEL, Palette.ACCENT));

        Label who = new Label(cell.label());
        who.setStyle(Palette.words(Palette.SIZE_HEADING, Palette.ACCENT)
                + " -fx-font-weight: bold;");
        panel.getChildren().add(who);
        panel.getChildren().add(panelWho(bal, cell));

        /* ------------------------------ the month ------------------------------ */
        double spend = toDollars(cell.disposable());
        double after = toDollars(cell.afterFixed());
        double bills = spend - after;
        double ate = toDollars(cell.planned());

        panel.getChildren().add(panelBlockHead("The month"));
        panel.getChildren().add(statementLine("Money to spend",
                tightMoney(spend, false), spend > 0 ? null : Palette.TEXT_SPENT));
        panel.getChildren().add(statementLine("Rent, fees and interest",
                costMoney(bills), bills > .005 ? Palette.WARN : Palette.TEXT_SPENT));
        panel.getChildren().add(statementTotal("Left for the shopping",
                tightMoney(after, false), after < 0 ? Palette.BAD : null));

        panel.getChildren().add(basketBlock(cell));
        positionBlock(panel, bal, cell);
        ratioBlock(panel, cell, spend, 0, "The bills take", bills, 0, ate, after - ate);
        return panel;
    }

    /**
     * The month of whichever cell is open, in full.
     *
     * The detail is one click from anywhere on the grid rather than seven
     * disclosures deep, and it is one panel rather than eleven rows - so the
     * figures behind a cell can be read without losing the grid that made you
     * curious about it.
     */
    VBox openStatement(HouseholdAccounts hh, HouseholdBalance bal,
                               FamilyModel families) {

        if (openCell.isEmpty()) return null;

        /*
         * EVERYBODY OUTSIDE THE FAMILY MATRIX GETS THE SAME PANEL - the out of
         * work, the students, the orphans, the prison, and since 2026-09-14 the
         * retired as well. They have their own ledgers, which are richer than
         * anything statementFor() can derive.
         */
        if (openCell.startsWith("OUT:")) return outsideStatement(bal);

        if (families == null) return null;

        String[] parts = openCell.split(":", 2);
        int tierIndex;
        FamilyStructure shape;
        try {
            tierIndex = Integer.parseInt(parts[0]);
            shape = FamilyStructure.valueOf(parts[1]);
        } catch (RuntimeException bad) {
            openCell = "";
            return null;
        }
        if (tierIndex < 0 || tierIndex >= HouseholdAccounts.RETIRED) {
            openCell = "";
            return null;
        }

        PayTier tier = PayTier.values()[tierIndex];
        HouseholdAccounts.Statement s = hh.statementFor(families, shape, tier);
        Household own = bal.cell(shape, tier);
        double left = toDollars(s.left());

        VBox panel = new VBox(0);
        panel.setMaxWidth(STATEMENT);
        panel.setStyle("-fx-padding: 10 14 12 14;"
                + Palette.block(Palette.PANEL, Palette.ACCENT));

        Label who = new Label(shape.getLabel() + "  ·  " + hh.getRowLabel(tierIndex));
        who.setStyle(Palette.words(Palette.SIZE_HEADING, Palette.ACCENT)
                + " -fx-font-weight: bold;");
        panel.getChildren().add(who);
        panel.getChildren().add(panelWho(bal, own));

        /*
         * WHAT COMES IN, SPLIT IN TWO. statementFor() adds a wage per earner to
         * a pension per senior and hands back the sum; they are different money
         * and a household with a grandparent in it is a different proposition
         * from one without. The split is exact - the pension side is seniors
         * times the pension, which is where the sum came from.
         */
        double income = toDollars(s.income());
        double pensioners = RetiredHousehold.pensionersIn(shape);
        double pension = pensioners * toDollars(hh.getPensionPerSenior());
        double wages = income - pension;

        panel.getChildren().add(panelBlockHead("What comes in"));
        if (shape.earners() > 0) {
            panel.getChildren().add(statementLine(
                    shape.earners() == 1 ? "One wage"
                            : shape.earners() + " wages, at " + hh.getRowLabel(tierIndex).toLowerCase(),
                    tightMoney(wages, false)));
        }
        if (pension > .005) {
            panel.getChildren().add(statementLine(
                    pensioners == 1 ? "A pension" : "Pensions",
                    tightMoney(pension, false), "#8ed4ff"));
        }

        /*
         * AND WHAT COMES OFF IT, split the same way and for the same reason:
         * the wage tax is a dial the player turns and the pension contribution
         * is not. The share between them is this tier's own - rowTax against
         * rowContributions - so the two lines add up to the tax the cell was
         * actually charged rather than to a number of this screen's invention.
         */
        double taxAll = toDollars(s.tax());
        double tierTax = hh.getRowTax(tierIndex);
        double tierCont = hh.getRowContributions(tierIndex);
        double split = tierTax + tierCont > 0 ? tierTax / (tierTax + tierCont) : 1;
        double onWages = hh.getRowWages(tierIndex);

        panel.getChildren().add(statementLine(
                String.format("Wage tax at %.0f%%",
                        onWages > 0 ? tierTax / onWages * 100 : 0),
                costMoney(taxAll * split), Palette.WARN));
        if (taxAll * (1 - split) > .005) {
            panel.getChildren().add(statementLine(
                    String.format("Pension contributions at %.1f%%",
                            ui.game.getEconomyManager().getTaxPolicy().getContributionRate() * 100),
                    costMoney(taxAll * (1 - split)), Palette.WARN));
        }
        panel.getChildren().add(statementTotal("Take-home",
                tightMoney(income - taxAll, false), null));

        /*
         * WHAT THE MONTH COSTS. The fees line was one figure covering two
         * unlike things: a per-head charge for clinics and schools, and this
         * tier's own interest bill. feesPerHead() times the heads is the first
         * exactly, so whatever is left of the line is the second exactly.
         */
        double feesAll = toDollars(s.fees());
        double perHead = toDollars(hh.feesPerHead()) * s.people();
        double interest = Math.max(0, feesAll - perHead);

        panel.getChildren().add(panelBlockHead("What the month costs"));
        panel.getChildren().add(statementLine("Rent", costMoney(toDollars(s.rent()))));
        panel.getChildren().add(statementLine("Healthcare and school fees",
                costMoney(Math.min(perHead, feesAll))));
        if (interest > .005) {
            panel.getChildren().add(statementLine("Interest on what they owe",
                    costMoney(interest), Palette.WARN));
        }
        panel.getChildren().add(statementLine("The shopping basket",
                costMoney(toDollars(s.shopping()))));
        panel.getChildren().add(statementTotal(left < 0 ? "Short by" : "Left over",
                tightMoney(left, false), left < 0 ? Palette.BAD : Palette.GOOD));

        HBox bar = flowBar(toDollars(s.tax()), toDollars(s.rent()), toDollars(s.fees()),
                toDollars(s.shopping()), left, STATEMENT - 28);
        VBox.setMargin(bar, new javafx.geometry.Insets(8, 0, 0, 0));
        panel.getChildren().add(bar);
        panel.getChildren().add(flowKey());

        /*
         * ...AND THE HOUSEHOLD'S OWN BOOKS, which are its own since 2026-09-10.
         * Everything above is a flow the accounts derive; everything below is
         * the cell's own ledger, written by the month as it happened.
         */
        if (own.households() >= .5) {
            /*
             * AND WHERE THE TWO DISAGREE, SAY SO. The month above is DERIVED -
             * a wage per earner off the tier's totals, a rent per door, a
             * basket per head. The ledger below was WRITTEN, by the month, as
             * it happened. On most cells the two land within a rounding of each
             * other and on some they do not, and a panel that quietly showed a
             * derived month above a written position would be inventing a
             * household that is the top half of one and the bottom half of
             * another.
             *
             * The house rule, from the reporting audit: a breakdown that
             * silently absorbs its own gap is worse than no breakdown.
             */
            double ledgerTakeHome = toDollars(own.disposable());
            double gap = ledgerTakeHome - (income - taxAll);
            if (Math.abs(gap) > Math.max(1, Math.abs(income - taxAll) * .01)) {
                panel.getChildren().add(statementNote(String.format(
                        "The books below are this household's own and they open on %s of "
                        + "take-home, not the %s above - %s apart. The month above is derived "
                        + "from the tier's totals; the ledger was written as the month happened.",
                        tightMoney(ledgerTakeHome, false),
                        tightMoney(income - taxAll, false),
                        tightMoney(Math.abs(gap), false))));
            }
            panel.getChildren().add(basketBlock(own));
            positionBlock(panel, bal, own);
            ratioBlock(panel, own, income, taxAll, "Rent takes",
                    toDollars(s.rent()), feesAll, toDollars(s.shopping()), left);
        }

        VBox holder = new VBox(panel);
        holder.setStyle("-fx-padding: 4 0 10 0;");
        return holder;
    }

    /* =====================================================================
       THE TIER TABLE, AS A TABLE.

       What this replaces was ten columns lined up by padding a String, and it
       had two faults that no amount of care with the padding could fix.

       THE ROW WAS ONE LABEL, so it was one colour. A tier that earns well and
       banks nothing, and a tier that earns badly and banks nothing, came out
       the same shade - because the only thing the screen could colour was the
       whole line. The one figure on the row that is NEWS is what is left at the
       end of it, and it could not be said any louder than the rest.

       AND THE ALIGNMENT WAS A GUESS. There is a comment in the old code about
       "Retired (no earner)" being nineteen characters against a nineteen-wide
       column, which shunted every figure on that row one place right and made
       the whole table unreadable. A column width should not be a fact somebody
       has to remember.

       A GridPane fixes both: the columns line up because the layout lines them
       up, and every cell is its own node with its own colour.

       AND THE BAR ON THE END is the point of the screen. Jerus's own note on
       this data: income across these rows varies about tenfold, while rent and
       the shopping basket per head do not vary at all - so the bottom rows run
       a deficit and the top rows bank almost everything. That is the finding,
       and reading it out of eight columns of figures took work. One stacked bar
       per row says it at a glance, and the figures are still there for the
       reading.
       ===================================================================== */

    /**
     * Where a household's month went, as one bar.
     *
     * Proportional to whichever is larger, what came in or what went out - so a
     * household spending more than it earns fills the bar and the shortfall
     * shows as red on the end rather than silently rescaling everything else.
     *
     * The colours are the categories, not a gradient: tax amber because it is
     * the one the player sets, rent blue, fees purple, the shop lime, and what
     * survives green - or red, when nothing does.
     */
    HBox flowBar(double tax, double rent, double fees, double shops,
                         double left, double width) {

        double out = Math.max(0, tax) + Math.max(0, rent)
                   + Math.max(0, fees) + Math.max(0, shops);
        double span = out + Math.abs(left);
        if (span <= 0) return new HBox();

        HBox bar = new HBox(0);
        bar.setPrefWidth(width);
        bar.setMinWidth(width);
        bar.setMaxWidth(width);
        bar.setPrefHeight(9);
        bar.setMinHeight(9);
        bar.setStyle("-fx-background-radius: 2;");

        bar.getChildren().addAll(
                barPart(tax / span * width, Palette.WARN, "tax"),
                barPart(rent / span * width, "#5cb8ff", "rent"),
                barPart(fees / span * width, "#ce93d8", "fees"),
                barPart(shops / span * width, "#d4e157", "the shops"),
                barPart(Math.abs(left) / span * width,
                        left < 0 ? Palette.BAD : Palette.GOOD,
                        left < 0 ? "short" : "left over"));
        return bar;
    }

    /** One segment. Zero-width segments are skipped rather than drawn as slivers. */
    Region barPart(double width, String colour, String what) {
        Region part = new Region();
        double w = Math.max(0, width);
        part.setPrefWidth(w);
        part.setMinWidth(w);
        part.setMaxWidth(w);
        part.setStyle("-fx-background-color: " + colour + ";");
        if (w >= 2) Tooltip.install(part, new Tooltip(what));
        return part;
    }

    /** The key under the bar, so the colours mean something the first time. */
    HBox flowKey() {
        HBox key = new HBox(Palette.GAP_LOOSE);
        key.setAlignment(Pos.CENTER_LEFT);
        key.setStyle("-fx-padding: 6 0 0 0;");
        String[][] parts = {
            {"tax", Palette.WARN}, {"rent", "#5cb8ff"}, {"fees", "#ce93d8"},
            {"the shops", "#d4e157"}, {"what is left", Palette.GOOD},
        };
        for (String[] part : parts) {
            Region swatch = new Region();
            swatch.setPrefSize(9, 9);
            swatch.setMinSize(9, 9);
            swatch.setStyle("-fx-background-color: " + part[1] + "; -fx-background-radius: 2;");
            Label what = new Label(part[0]);
            what.setStyle(Palette.words(Palette.SIZE_CAPTION, Palette.TEXT_MUTED));
            HBox one = new HBox(4, swatch, what);
            one.setAlignment(Pos.CENTER_LEFT);
            key.getChildren().add(one);
        }
        return key;
    }

    /* ---------------------------------------------------------------------
       Small helpers so the report screens stay readable. Shared by the sector
       screens - Industrial and Utility can reuse these when they get ported.
       --------------------------------------------------------------------- */
}
