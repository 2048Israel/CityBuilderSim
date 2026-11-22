package ham.citybuildersim;

import com.google.gson.JsonElement;
import ham.citybuildersim.ui.KeptBench;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import static ham.citybuildersim.HistoricalRecipeCheck.allExcept;
import static ham.citybuildersim.HistoricalRecipeCheck.at;
import static ham.citybuildersim.HistoricalRecipeCheck.priceForBothReaders;
import static ham.citybuildersim.HistoricalRecipeCheck.run;
import static ham.citybuildersim.HistoricalRecipeCheck.silent;

/**
 * A new building is read as it would run, not as the fleet standing runs: NewUnit beside the template, read by every
 * future-building figure.
 *
 * WHY (QN1, R2e's fifth review). In a city founded the 1900 way a template's live figures are the standing fleet's mix, while
 * a building ordered now is fitted with whatever EquipmentFit.fittedForNew() keeps: a Vehicle Works fleet carrying the
 * welding robot runs 100 unskilled posts, and the plant ordered while machinery is dear opens without the robot and needs
 * 300. The investors, the Build card, the order bar, the staffing gate, the posts and draw on site, the bank's new-branch
 * fee gate and the power ahead each quoted the fleet's 100. NewUnit.of() resolves one new building's figures where they are
 * read, and the template stays what is ordered, stacked and keyed.
 *
 * No oracle here resolves a proposal. Each expected value is taken from the ACTUAL state a new building runs: the fleet's
 * stack set unfitted for the pieces a new one would not get, the recipes applied, and the template's live figures read
 * there, or a door read through the standing routes (NewUnit.standing(), or the investors' door under
 * NewUnit.standingWhile(): the template as it stands, as main read it), then the fitting restored; or it is composed from
 * the recipe data. A wrong proposal therefore cannot reach both sides (REV-QN1, B). The fleet's own figures appear only as
 * each section's PREMISE: that the expected value differs from what a reader of the fleet would give, so a reader that
 * kept reading the fleet fails.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. A city founded the default way reads every template as it stands (nothing proposed, the same posts, draw, makes and
 *      uses), and no recipeChanges "without" block in buildings.json moves land, which NewUnit does not carry.
 *   2. The rejected welding robot: the fleet keeps 100 unskilled posts, the actual new plant has 300, and the new unit, the
 *      investors' estimate, the appraisal and the investors' summary all read the new plant's.
 *   3. The staffing boundary with arrivals closed: the Build card carries the 300-post unit and its running bill, the
 *      investors' own gate is STAFFING where the fleet's 100 posts would pass, and the order bar counts 600 for two.
 *   4. The pulp mill's saved choice is kept, not re-chosen afresh by the moved prices (a guard).
 *   5. The bank's new-branch estimate and fee gate price the new branch's own clerks once, on the one branch that has them,
 *      and the gate's boundary is told from the removed (N + 1) x (C + extra) one.
 *   6. With the robot accepted again the new unit's figures equal the template's, the template being the same object.
 *   7. Identities: the card, the appraisal and the investors keep the catalogue template through a month and a changed
 *      fitting input; the Build screen's own card, drawn kept, keeps the Build button and steps it was drawn with a month
 *      on, whose steps pressed then reprice the order bar at the actual new plants' posts and whose Build button orders the
 *      catalogue template; and the order on site is not touched by reading it.
 *   8. What is on site counts the actual new plants' posts, and the landlords' head shortage counts them too.
 *   9. The power ahead of a queued changed draw: two Cement Plants without their rotary kiln add the shaft kilns' draw,
 *      not the fleet's, and only from their completion.
 *  10. What a queued changed crew asks of the city: two Shared Services Centres without their computer add their own
 *      college posts to the colleges' places (CityNeeds.hires()) and their own posts to the landlords' head shortage.
 *  11. Power ahead: a queued Coal Power Plant whose new unit rejects the turbine and the pulverised coal generates less,
 *      and the power measure reads that generation.
 *  12. The card's goods: a new Electric Arc Furnace without its continuous caster burns fuel oil, and the card's words and
 *      its group's heading say it; a new Logging Camp without its chainsaw cuts less, and the card's outlets and its
 *      sentences say that cut; and a tripwire that no piece changes which goods are made, nor what a building on typed
 *      ground makes, where the heading's made words and stands() would read a new unit and the fleet alike.
 *  13. Held for one pass (AP1, WorksValue.hold()), on section 9's queued plants: inside a scope the power ahead at every
 *      horizon, and a power plant's avoided loss, are the bare figures to the bit; an order, a rush and a priority placed
 *      inside it, and the progress and the builders-first switch moved by hand, are read at the next call; one schedule is
 *      built for the scope with nothing changed on site (a count, WorksValue.SCHEDULES); a second city (saved and loaded),
 *      a second thread, and the second city's hold nested in g's read their own bare figures and leave g's scope as it
 *      was; closed, nothing is held.
 *
 * Sections 7 and 12 draw on the JavaFX toolkit (ui.KeptBench, KeptCheck's way); where it cannot start, their card parts
 * are said to be skipped, section 7 orders through buildStack() and says so, and the rest runs: the harness exits 0
 * without them, as KeptCheck does.
 */
public final class NewUnitCheck {
    static int fails, checks;
    static boolean quietRun;
    static final PrintStream out = System.out;

    static final String ROBOT = "welding-robot";

    static void check(String label, boolean ok, String detail) {
        checks++;
        if (!ok) fails++;
        if (ok && quietRun) return;
        out.println((ok ? "  ok " : "FAIL ") + label + (detail == null || detail.isBlank() ? "" : ": " + detail));
    }

    static void section(String title) { out.println("--- " + title + " ---"); }

    static boolean near(double a, double b) { return HistoricalLicenceCheck.near(a, b); }

    /** Whether two readings differ by more than rounding: the premise that a reader of the fleet would be told apart. */
    static boolean apart(double a, double b) { return Math.abs(a - b) > 1e-6 * Math.max(1, Math.max(Math.abs(a), Math.abs(b))); }

    /* ---------------- the actual state a new building runs ---------------- */

    /** The live figures of a template, and the investors' numbers, read in one state. */
    static final class Ran {
        final int[] jobs = new int[JobType.values().length];
        int total, electricity;
        double gen, production1, estimate, running;

        @Override public String toString() {
            return "posts " + jobs[JobType.NO_DIPLOMA.ordinal()] + "/" + total + ", draw " + electricity + ", gen " + gen
                    + ", production1 " + production1 + ", estimate " + estimate + ", running " + running;
        }
    }

    static Ran ranOf(Game g, BuildingsTemplate t, boolean priced) {
        Ran r = new Ran();
        for (JobType j : JobType.values()) r.jobs[j.ordinal()] = t.getJobs(j);
        r.total = t.getTotalJobs();
        r.electricity = t.getElectricityConsumption();
        r.gen = t.getGen();
        r.production1 = t.getProduction1();
        if (priced) {
            // Through the standing routes (REV-QN1, B): every proposal the investors' door asks for is read as the template
            // stands, so in the physically changed fleet the figure is that fleet's own, whatever a resolved proposal says.
            r.estimate = NewUnit.standingWhile(() -> g.getBusinessInvestment().estimatedMonthlyProfit(t.getSector(), t));
            r.running = BuildCard.runningCost(g, NewUnit.standing(t));
        }
        return r;
    }

    /** The pieces a new building of t would not get now. */
    static Set<String> rejected(Game g, BuildingsTemplate t) {
        Set<String> kept = EquipmentFit.fittedForNew(g, t);
        Set<String> lacking = new TreeSet<>();
        for (Equipment e : t.getEquipment()) if (!kept.contains(e.getId())) lacking.add(e.getId());
        return lacking;
    }

    /**
     * Reads where a new building of t actually runs: the standing fleet's stack set without every piece a new one would not
     * get, the recipes applied, `read` run there, then the fitting put back and applied again.
     */
    static <T> T whereItRuns(Game g, BuildingsTemplate t, Supplier<T> read) {
        BuildingsStacks stack = g.getBuildingManager().getStack(t);
        Set<String> lacking = rejected(g, t);
        for (String id : lacking) stack.setUnfitted(id, stack.getQuantity());
        run(g);
        try {
            return read.get();
        } finally {
            for (String id : lacking) stack.setUnfitted(id, 0);
            run(g);
        }
    }

    static boolean sameFigures(NewUnit u, BuildingsTemplate t) {
        for (JobType j : JobType.values()) if (u.getJobs(j) != t.getJobs(j)) return false;
        for (Good g : Good.values()) if (!near(u.makes(g), t.makes(g)) || !near(u.uses(g), t.uses(g))) return false;
        return u.getTotalJobs() == t.getTotalJobs() && u.getElectricityConsumption() == t.getElectricityConsumption()
                && near(u.getWaterConsumption(), t.getWaterConsumption()) && near(u.getGen(), t.getGen())
                && near(u.getConsumables(), t.getConsumables()) && near(u.getProduction1(), t.getProduction1())
                && near(u.steamRailShare(), t.steamRailShare()) && near(u.crackerPetrol(), t.crackerPetrol());
    }

    /** A 1900 city with one fitted Vehicle Works and every discovery known at the robot's date. */
    static final class Cars {
        Game game;
        BuildingsTemplate works;
        BuildingsStacks stack;
        Ran actual;
        double rejectedAt;
    }

    static Cars robotTown(String label) throws Exception {
        Cars c = new Cars();
        c.game = HistoricalLicenceCheck.town(label, true);
        allExcept(c.game, "");
        at(c.game, ResearchCatalog.knownFrom("E8"));
        c.works = HistoricalLicenceCheck.stand(c.game, "Vehicle Works", 1);
        c.stack = c.game.getBuildingManager().getStack(c.works);
        run(c.game);
        return c;
    }

    /** Steps machinery from a cent up by half again until the verdict for a new plant drops the robot; false if it never does. */
    static boolean rejectRobot(Cars c) {
        priceForBothReaders(c.game, Good.MACHINERY, .01);
        for (int step = 0; step < 120 && EquipmentFit.fittedForNew(c.game, c.works).contains(ROBOT); step++) {
            c.rejectedAt = .01 * Math.pow(1.5, step);
            priceForBothReaders(c.game, Good.MACHINERY, c.rejectedAt);
        }
        return !EquipmentFit.fittedForNew(c.game, c.works).contains(ROBOT);
    }

    static final int NO = JobType.NO_DIPLOMA.ordinal();

    /** Whether the toolkit has been tried, and why it is down (null: up); it starts once in a JVM, so sections 7 and 12 share it. */
    static boolean toolkitTried;
    static String toolkitDown;

    /** The JavaFX toolkit, started at the first ask (KeptCheck.startJavaFx()): null when it is up, else why not. */
    static String toolkit() {
        if (!toolkitTried) {
            toolkitTried = true;
            toolkitDown = KeptCheck.startJavaFx();
        }
        return toolkitDown;
    }

    /** The fitted fleet's quote: what the sector says of the fleet's figures, as a reader of the template would have quoted. */
    static double fleetQuote(Game g, BuildingsTemplate t, Sector owner) {
        return owner.estimatedMonthlyProfit(NewUnit.standing(t), g.getBusinessInvestment());
    }

    /* ---------------- 1 ---------------- */

    static void standingEverywhere() throws Exception {
        section("1. the default city reads every template as it stands; no undo moves land");
        Game modern = HistoricalLicenceCheck.town("QN1-default", false);
        int templates = 0, choices = 0, proposed = 0, differing = 0;
        for (BuildingsTemplate t : modern.getBuildingManager().getTemplates()) {
            NewUnit u = NewUnit.of(modern, t);
            templates++;
            if (t.hasRecipeChoice()) choices++;
            if (u.proposed()) proposed++;
            if (u.template() != t || !sameFigures(u, t)) differing++;
        }
        check("fixture: a city founded the default way, with templates that have a recipe choice",
                !modern.foundedUnfitted() && choices > 0 && templates > choices, templates + " templates, " + choices + " with a choice");
        check("every template's new unit is standing and carries the template's posts, draw, makes and uses",
                proposed == 0 && differing == 0, proposed + " proposed, " + differing + " differing");
        check("no game, no template: standing figures and null", NewUnit.of(null, modern.getBuildingManager().getTemplates().get(0)).template()
                == modern.getBuildingManager().getTemplates().get(0) && NewUnit.of(modern, null) == null, "");

        JsonObject data = JsonParser.parseString(Files.readString(Path.of("src/main/resources/buildings.json"), StandardCharsets.UTF_8)).getAsJsonObject();
        int blocks = 0, moving = 0, loaded = 0, loadedMoving = 0;
        for (JsonElement element : data.getAsJsonArray("buildings")) {
            JsonObject definition = element.getAsJsonObject();
            if (!definition.has("recipeChanges")) continue;
            for (JsonElement change : definition.getAsJsonArray("recipeChanges")) {
                JsonObject without = change.getAsJsonObject().getAsJsonObject("without");
                blocks++;
                if (without.has("land") && without.get("land").getAsDouble() != 0) moving++;
            }
        }
        for (BuildingsTemplate t : modern.getBuildingManager().getTemplates())
            for (RecipeChange change : t.getRecipeChanges()) {
                loaded++;
                if (change.land() != 0) loadedMoving++;
            }
        check("data guard: no recipeChanges 'without' block in buildings.json moves land (NewUnit keeps the template's)",
                blocks > 0 && moving == 0 && loaded == blocks && loadedMoving == 0,
                blocks + " blocks in the file, " + loaded + " loaded, " + moving + "/" + loadedMoving + " moving land");
    }

    /* ---------------- 2 ---------------- */

    static void rejectedRobot(Cars c) throws Exception {
        section("2. the rejected robot: the fleet keeps 100 posts, the new plant has 300, and every estimate reads the new plant");
        Game g = c.game;
        BuildingsTemplate w = c.works;
        double[] wages = Recipes.pricesOf(g).wages();
        priceForBothReaders(g, Good.MACHINERY, .01);
        NewUnit kept = NewUnit.of(g, w);
        boolean flips = rejectRobot(c);
        double priceRejected = c.rejectedAt;
        check("fixture: stepping machinery from a cent flips the verdict on the robot, kept at a cent and rejected above",
                flips && kept.getJobs(JobType.NO_DIPLOMA) == w.getJobs(JobType.NO_DIPLOMA), "rejected at machinery " + priceRejected);
        c.actual = whereItRuns(g, w, () -> ranOf(g, w, true));
        Ran fleet = ranOf(g, w, true);
        Sector owner = g.getSectors().automotive();
        double fleetQuote = fleetQuote(g, w, owner);
        check("fixture: the fleet is fitted at 100 unskilled posts; the actual new plant, without the robot, has 300; wages are positive",
                fleet.jobs[NO] == 100 && c.actual.jobs[NO] == 300 && EquipmentFit.fittedForNew(g, w).size() < w.getEquipment().size()
                        && !EquipmentFit.fittedForNew(g, w).contains(ROBOT) && wages[NO] > 0 && c.stack.getUnfitted(ROBOT) == 0,
                "fleet " + fleet.jobs[NO] + ", actual " + c.actual.jobs[NO] + ", wage " + wages[NO]);
        NewUnit unit = NewUnit.of(g, w);
        boolean sameJobs = true;
        for (JobType j : JobType.values()) sameJobs &= unit.getJobs(j) == c.actual.jobs[j.ordinal()];
        check("the new unit carries the actual new plant's posts and is of the catalogue template",
                unit.proposed() && unit.template() == w && sameJobs && unit.getJobs(JobType.NO_DIPLOMA) == 300 && unit.getTotalJobs() == c.actual.total,
                "unit " + unit.getJobs(JobType.NO_DIPLOMA) + "/" + unit.getTotalJobs() + " against " + c.actual.jobs[NO] + "/" + c.actual.total);
        check("premise: the fleet's quote differs from the actual new plant's estimate, so a reader of the fleet fails",
                apart(fleetQuote, c.actual.estimate), "fleet quote " + fleetQuote + ", actual new " + c.actual.estimate);
        double investors = g.getBusinessInvestment().estimatedMonthlyProfit(w.getSector(), w);
        check("the investors' estimate is the actual new plant's",
                near(investors, c.actual.estimate), investors + " against " + c.actual.estimate);
        BuildCard.Appraisal appraisal = BuildCard.appraise(g, w);
        BuildCard.Investors summary = BuildCard.investors(g, w);
        check("the appraisal's and the investors' summary's estimates are the actual new plant's",
                near(appraisal.estimate(), c.actual.estimate) && near(summary.estimate(), c.actual.estimate),
                appraisal.estimate() + ", " + summary.estimate() + " against " + c.actual.estimate);
        check("none of them quotes the fleet's", apart(appraisal.estimate(), fleetQuote) && apart(summary.estimate(), fleetQuote)
                && apart(investors, fleetQuote), "fleet " + fleetQuote);
    }

    /* ---------------- 3 ---------------- */

    static void staffingBoundary(Cars c) throws Exception {
        section("3. the staffing boundary, arrivals closed: the card, the investors' gate and the order bar read the 300 posts");
        Game g = c.game;
        BuildingsTemplate w = c.works;
        Sector owner = g.getSectors().automotive();
        for (WageBand band : WageBand.values()) g.getMigration().holdArrivals(band);
        PopulationManager people = g.getPopulationManager();
        double[] existing = people.staffablePostsByBand();
        // Skilled heads enough for the posts standing and the new plant's, over the students the labour supply leaves out.
        double[] needed = existing.clone();
        // ...and the posts the builders' laid-off crews keep, which the staffing test counts as spoken for.
        for (JobType job : JobType.values())
            needed[WageBand.of(job).ordinal()] += c.actual.jobs[job.ordinal()] + g.getBuildingManager().getPostsWithheld(job);
        needed[WageBand.NONE.ordinal()] = 0;
        double[] heads = needed.clone();
        people.restoreWorkforce((long) (10 * Arrays.stream(needed).sum() + 5000));
        for (int pass = 0; pass < 6; pass++) {
            people.restoreSkilledHeads(heads);
            double[] have = people.workforceByBand();
            for (int b = 1; b < heads.length; b++) heads[b] += Math.max(0, needed[b] - have[b]);
        }
        people.restoreSkilledHeads(heads);
        int skilled = (int) Math.ceil(Arrays.stream(heads).sum());
        boolean boundary = false;
        long at = 0;
        // The search only: with the unskilled workers stepped up, the fleet's 100 posts pass and the new plant's 300 do not.
        for (int workers = skilled; workers <= skilled + existing[WageBand.NONE.ordinal()] + 2000 && !boundary; workers += 5) {
            people.restoreWorkforce(workers);
            at = workers;
            boundary = owner.staffing(NewUnit.standing(w), 1).passes() && !owner.staffing(NewUnit.of(g, w), 1).passes();
        }
        check("fixture: a staffing pass for the fleet's 100 posts and a fail for the new plant's 300, arrivals closed",
                boundary && w.getJobs(JobType.NO_DIPLOMA) == 100 && c.actual.jobs[NO] == 300, "workforce " + at);
        double actualRunning = whereItRuns(g, w, () -> BuildCard.runningCost(g, NewUnit.standing(w)));
        double fleetRunning = BuildCard.runningCost(g, NewUnit.standing(w));
        BuildCard.Figures card = BuildCard.of(g, w, null);
        check("the Build card is of the catalogue template and carries the 300-post unit",
                card.template() == w && card.newUnit().template() == w && card.newUnit().getJobs(JobType.NO_DIPLOMA) == 300,
                "posts " + card.newUnit().getJobs(JobType.NO_DIPLOMA));
        check("premise: the fleet's running bill differs from the actual new plant's", apart(fleetRunning, actualRunning),
                "fleet " + fleetRunning + ", actual new " + actualRunning);
        check("the card's running bill is the actual new plant's, not the fleet's",
                near(card.running(), actualRunning) && apart(card.running(), fleetRunning), card.running() + " against " + actualRunning);
        BuildCard.Investors summary = BuildCard.investors(g, w);
        check("the investors' own gate is STAFFING (the fleet's 100 posts would pass it)",
                summary.own() != null && summary.own().kind() == BuildCard.GateKind.STAFFING && owner.staffing(NewUnit.standing(w), 1).passes(),
                summary.own() == null ? "no gate" : String.valueOf(summary.own().kind()));
        Map<BuildingsTemplate, Long> two = Map.of(w, 2L);
        double[] bar = BuildAdvice.needs(g, two);
        double[] old = BuildAdvice.needs(two);
        check("the order bar counts 600 unskilled posts for two; the old overload keeps the fleet's 200 and the fleet's 100 stand",
                bar[NO] == 2 * c.actual.jobs[NO] && bar[NO] == 600 && old[NO] == 200 && w.getJobs(JobType.NO_DIPLOMA) == 100,
                "bar " + bar[NO] + ", fleet " + old[NO]);
    }

    /* ---------------- 4 ---------------- */

    static void pulpChoice() throws Exception {
        section("4. a pulp mill's saved choice is the new unit's, not a fresh one (a guard)");
        Game g = HistoricalLicenceCheck.town("QN1-pulp", true);
        allExcept(g, "");
        at(g, PolicyDates.monthOf(2000, 1));
        BuildingsTemplate mill = HistoricalLicenceCheck.stand(g, "Pulp Mill", 1);
        priceForBothReaders(g, Good.PULPWOOD, 1000);
        priceForBothReaders(g, Good.RESIDUES, .001);
        run(g);
        String saved = mill.runningRecipeIds()[0];
        priceForBothReaders(g, Good.RESIDUES, 1e6);
        Recipe fresh = Recipes.best(mill, true, g.getMonth(), Recipes.pricesOf(g), g.getResearch());
        silent(() -> g.getRecipes().apply(g));
        double groundwood = mill.getRecipe("groundwood").uses().getOrDefault(Good.RESIDUES, 0.0);
        check("fixture: a Pulp Mill with no independent changes saved kraft; moved residue prices now prefer groundwood, which uses no residues",
                mill.getRecipeChanges().isEmpty() && saved.equals("kraft") && fresh.getId().equals("groundwood") && groundwood == 0,
                saved + " -> " + fresh.getId() + ", groundwood residues " + groundwood);
        NewUnit unit = NewUnit.of(g, mill);
        check("guard: the new pulp mill keeps kraft's figures (residues 21500, the mill's own), whatever a fresh choice would pick",
                mill.uses(Good.RESIDUES) == 21500 && unit.uses(Good.RESIDUES) == 21500 && unit.uses(Good.RESIDUES) == mill.uses(Good.RESIDUES)
                        && unit.recipe() != null && unit.recipe().getId().equals("kraft") && sameFigures(unit, mill) && unit.template() == mill,
                "residues " + unit.uses(Good.RESIDUES) + ", recipe " + (unit.recipe() == null ? null : unit.recipe().getId()));
    }

    /* ---------------- 5 ---------------- */

    static void bankClerks() throws Exception {
        section("5. the bank's new branch: its own clerks, once, and the fee gate's boundary");
        Game g = HistoricalLicenceCheck.town("QN1-bank", true);
        allExcept(g, "");
        at(g, Math.max(ResearchCatalog.knownFrom("B1"), ResearchCatalog.knownFrom("B3")));
        BuildingsTemplate branch = g.getBuildingManager().getTemplateByName("Commercial Bank");
        HistoricalRecipeCheck.fitted(branch, g.getBuildingManager().getStack(branch));
        priceForBothReaders(g, Good.MACHINERY, 1e9);
        run(g);
        Recipe earlier = RecipeChanges.compose(branch, branch.defaultRecipe(), Map.of("B1", 1.0, "B3", 1.0), "qn1-bank");
        double[] wages = Recipes.pricesOf(g).wages();
        double extra = 0;
        for (JobType job : JobType.values()) extra += (earlier.getJobs(job) - branch.getJobs(job)) * wages[job.ordinal()];
        Bank bank = g.getBank();
        long n = g.getBuildingManager().countByName("Commercial Bank");
        double c = bank.laterBranchCost();
        Set<String> kept = EquipmentFit.fittedForNew(g, branch);
        check("fixture: a fitted bank with a branch standing and a cost known; the new branch rejects both machines and hires clerks at positive wages",
                n >= 1 && bank.getBranches() == n && c > 0 && extra > 0 && !kept.contains("business-computer") && !kept.contains("cash-machine"),
                n + " branches, C " + c + ", extra clerks " + extra);
        double expected = bank.feesPerBranchWithAnother(n) - c - extra;
        double fleetEstimate = bank.feesPerBranchWithAnother(n) - c;
        double quote = g.getBusinessInvestment().estimatedMonthlyProfit(branch.getSector(), branch);
        check("the investors' new-branch estimate carries the new branch's clerks over a later branch's cost",
                near(quote, expected) && apart(quote, fleetEstimate), quote + " against " + expected + " (the fleet's " + fleetEstimate + ")");

        Field fees = Bank.class.getDeclaredField("accountFees");
        Field exempt = Bank.class.getDeclaredField("lastUpkeepPerLater");
        fees.setAccessible(true);
        exempt.setAccessible(true);
        double lastUpkeepPerLater = exempt.getDouble(bank);
        double threshold = n * c + (c + extra) - lastUpkeepPerLater;
        fees.setDouble(bank, threshold + extra / 4);
        double above = bank.getAccountFees();
        boolean coversAbove = bank.feesWouldCoverAnother(n);
        boolean removedRefusesAbove = above + lastUpkeepPerLater < (n + 1) * (c + extra);
        fees.setDouble(bank, threshold - extra / 4);
        double below = bank.getAccountFees();
        boolean coversBelow = bank.feesWouldCoverAnother(n);
        boolean fleetCoversBelow = below + lastUpkeepPerLater >= (n + 1) * c;
        check("fixture: positive fees just above and just below N x C + (C + extra) - the charter's exemption",
                above > 0 && below > 0 && above > below, "above " + above + ", below " + below + ", threshold " + threshold);
        check("just above, the gate lets the branch open; just below, it does not",
                coversAbove && !coversBelow, "above " + coversAbove + ", below " + coversBelow);
        check("premise: the removed (N + 1) x (C + extra) gate would have refused the case above, and the fleet's C would have passed the case below",
                removedRefusesAbove && fleetCoversBelow, "removed refuses above " + removedRefusesAbove + ", fleet C passes below " + fleetCoversBelow);
    }

    /* ---------------- 6 ---------------- */

    static void matching(Cars c) throws Exception {
        section("6. with the robot accepted again, the new unit's figures are the template's");
        Game g = c.game;
        BuildingsTemplate w = c.works;
        priceForBothReaders(g, Good.MACHINERY, .000001);
        run(g);
        NewUnit unit = NewUnit.of(g, w);
        check("fixture: machinery cheap again keeps the robot for a new plant, and the unit is resolved, not the template as it stands",
                EquipmentFit.fittedForNew(g, w).contains(ROBOT) && unit.proposed() && unit.fitted().contains(ROBOT) && w.getJobs(JobType.NO_DIPLOMA) == 100,
                "fitted " + unit.fitted());
        check("its figures equal the template's and its template is the same object",
                unit.template() == w && sameFigures(unit, w) && unit.getJobs(JobType.NO_DIPLOMA) == 100,
                "unit " + unit.getJobs(JobType.NO_DIPLOMA) + " against " + w.getJobs(JobType.NO_DIPLOMA));
        BuildCard.Figures card = BuildCard.of(g, w, null);
        check("the card and the investors' estimate then agree with the fleet's own figures",
                card.template() == w && card.newUnit().getJobs(JobType.NO_DIPLOMA) == 100
                        && near(g.getBusinessInvestment().estimatedMonthlyProfit(w.getSector(), w), fleetQuote(g, w, g.getSectors().automotive())),
                "");
    }

    /* ---------------- 7 and 8 ---------------- */

    /** An order on site as the Build card's reads must leave it: the stack, its sites and their contracts. */
    static List<Object> siteSnapshot(Game g, BuildingsTemplate w) {
        BuildingsStacks stack = g.getBuildingManager().getStack(w);
        List<Object> snapshot = new ArrayList<>();
        snapshot.add(stack);
        snapshot.add(stack.getBuilding());
        snapshot.add(stack.getQuantity());
        snapshot.add(stack.getUnderConstruction());
        snapshot.add(Double.doubleToLongBits(g.onSiteMonths(w)));
        for (BuildingsStacks.Contract k : stack.getContracts()) {
            snapshot.add(k);
            snapshot.add(k.payer);
            snapshot.add(Arrays.asList(k.getValue(), k.units, k.allowance, k.recovered));
        }
        return snapshot;
    }

    static void identitiesAndSite() throws Exception {
        section("7. identities: through a month, a changed fitting input and an order on site");
        Cars c = robotTown("QN1-queue");
        Game g = c.game;
        BuildingsTemplate w = c.works;
        HistoricalLicenceCheck.stand(g, "Construction Depot", 4);   // builders, so that a site has a wait
        priceForBothReaders(g, Good.MACHINERY, .01);
        run(g);
        BuildCard.Figures first = BuildCard.of(g, w, null);
        BuildCard.Appraisal appraisal = BuildCard.appraise(g, w);
        BuildCard.Investors summary = BuildCard.investors(g, w);
        check("the card, the appraisal and the investors keep the catalogue template (machinery cheap: the new unit has the fleet's 100)",
                first.template() == w && first.newUnit().template() == w && appraisal.template() == w && summary.building().equals(w.getName())
                        && first.newUnit().getJobs(JobType.NO_DIPLOMA) == 100 && w.getJobs(JobType.NO_DIPLOMA) == 100,
                "posts " + first.newUnit().getJobs(JobType.NO_DIPLOMA));
        // The Build screen's own card (REV-QN1, D): the Vehicles page drawn kept with two dialled up, its order's Build button
        // and steps held from this draw and pressed after a month and a changed fitting input, as a player presses the kept page's.
        String down = toolkit();
        final KeptBench bench = down == null ? KeptCheck.fx(() -> new KeptBench(g)) : null;
        final KeptBench.Page page = bench == null ? null : KeptCheck.fx(() -> bench.build("Build, Vehicles, two Vehicle Works dialled",
                BuildAdvice.VEHICLES, Map.of(w.getName(), 2L)));
        final KeptBench.BuildOrder held = bench == null ? null : KeptCheck.fx(() -> {
            bench.home(page);
            bench.drawKept(page);
            bench.passes();
            return bench.buildOrder(w.getName());
        });
        if (bench == null) out.println("  (no held order: the JavaFX toolkit cannot start here - " + down + ")");
        else check("fixture: the Vehicles page has the catalogue template's card, two dialled up, and the order bar at the fleet's 200 unskilled posts while machinery is cheap",
                held != null && held.template() == w && held.button() != null && held.reprice() != null && held.less() != null
                        && held.more() != null && KeptCheck.fx(() -> bench.buildBarSays(unskilled(200))), held == null ? "no card" : "");

        if (bench != null) KeptCheck.fx(() -> { silent(() -> g.simulateMonths(1)); return null; });
        else silent(() -> g.simulateMonths(1));
        HistoricalRecipeCheck.fitted(w, c.stack);
        priceForBothReaders(g, Good.MACHINERY, .01);
        run(g);
        BuildCard.Figures later = BuildCard.of(g, w, null);
        check("a month on, a fresh card is of the same template with the current unit",
                later.template() == w && later.newUnit().template() == w && later.newUnit().getJobs(JobType.NO_DIPLOMA) == 100
                        && g.getBuildingManager().getStack(w) == c.stack, "posts " + later.newUnit().getJobs(JobType.NO_DIPLOMA));
        final KeptBench.BuildOrder kept = bench == null ? null : KeptCheck.fx(() -> {
            bench.drawKept(page);
            bench.passes();
            return bench.buildOrder(w.getName());
        });
        // The redraw makes the card's order afresh (orderControls() under Kept.scope("order")): its reprice is a new one, and the
        // kept nodes - the Build button, the steps - carry the new closures (Pieces.stepper(), ActionButton.onPress()).
        if (bench != null) check("a month on, the kept page keeps the Build button and the steps held from the first draw, on the catalogue template's card",
                kept != null && held != null && kept.button() == held.button() && kept.less() == held.less() && kept.more() == held.more()
                        && kept.template() == w,
                kept == null ? "no card" : "button " + (kept.button() == held.button() ? "kept" : "redrawn") + ", steps "
                        + (kept.more() == held.more() && kept.less() == held.less() ? "kept" : "redrawn")
                        + ", reprice " + (kept.reprice() == held.reprice() ? "the same" : "made afresh"));
        boolean flips = rejectRobot(c);
        c.actual = whereItRuns(g, w, () -> ranOf(g, w, true));
        BuildCard.Figures dear = BuildCard.of(g, w, null);
        check("fixture: machinery dear now rejects the robot; a fresh card is of the same template with the actual new plant's 300 while the fleet keeps 100",
                flips && dear.template() == w && dear.newUnit().template() == w && dear.newUnit().getJobs(JobType.NO_DIPLOMA) == 300
                        && c.actual.jobs[NO] == 300 && w.getJobs(JobType.NO_DIPLOMA) == 100 && BuildCard.appraise(g, w).template() == w,
                "posts " + dear.newUnit().getJobs(JobType.NO_DIPLOMA) + ", actual " + c.actual.jobs[NO]);

        // The queue: nothing else on site, so what the readers count below is the two plants.
        BuildingManager bm = g.getBuildingManager();
        check("fixture: nothing else on site and nothing pending", bm.getStacksUnderConstruction().isEmpty()
                && bm.getJobsUnderConstruction() == 0 && bm.postsOnSite(w.getSector(), JobType.NO_DIPLOMA) == 0
                && WorksValue.pendingPowerLoad(g, 1000) == 0 && g.getBuildingOutputAtEveryPost() > 0,
                "site output " + g.getBuildingOutputAtEveryPost());
        silent(() -> g.setCashForTest(Founding.WEALTHY_CASH * 100));
        if (bench != null) {
            // The kept card's steps, pressed between draws as a player presses them: up to three, then back to two; each press
            // runs what the last draw set, the card's reprice into the order bar. Then the held Build button, the same node, pressed.
            long actualPosts = c.actual.jobs[NO], fleetPosts = w.getJobs(JobType.NO_DIPLOMA);
            String[] words = {unskilled(3 * actualPosts), unskilled(3 * fleetPosts), unskilled(2 * actualPosts), unskilled(2 * fleetPosts)};
            boolean[] bar = KeptCheck.fx(() -> {
                boolean[] at = new boolean[6];
                if (held == null || held.more() == null || held.less() == null) return at;   // no steps found: the check below fails
                KeptBench.press(held.more());
                at[0] = bench.buildBarSays(words[0]);
                at[1] = bench.buildBarSays(words[1]);
                at[2] = bench.buildDialled(w.getName()) == 3;
                KeptBench.press(held.less());
                at[3] = bench.buildBarSays(words[2]);
                at[4] = bench.buildBarSays(words[3]);
                at[5] = bench.buildDialled(w.getName()) == 2;
                return at;
            });
            check("the kept steps, pressed after the month and the fitting change, reprice the order bar at the actual new plants' posts: 900 at three, 600 back at two, not the fleet's 300 and 200",
                    bar[0] && !bar[1] && bar[2] && bar[3] && !bar[4] && bar[5] && actualPosts == 300,
                    "at three \"" + words[0] + "\" " + bar[0] + ", \"" + words[1] + "\" " + bar[1] + ", dialled 3 " + bar[2]
                            + "; at two \"" + words[2] + "\" " + bar[3] + ", \"" + words[3] + "\" " + bar[4] + ", dialled 2 " + bar[5]);
            if (held != null && held.button() != null) KeptCheck.fx(() -> { silent(() -> KeptBench.click(held.button())); return null; });
        } else {
            silent(() -> g.buildStack(w, 2, false));
        }
        BuildingsStacks site = bm.getStack(w);
        long dialled = bench == null ? 0 : KeptCheck.fx(() -> bench.buildDialled(w.getName()));
        check(bench != null ? "the held Build button, pressed then, orders the catalogue template: two on its own stack's site, and the card's dial back to none"
                        : "with no toolkit, the order is placed by buildStack(), not the card: two of the catalogue template on its own stack's site",
                site == c.stack && site.getBuilding() == w && site.getUnderConstruction() == 2 && dialled == 0,
                "on site " + site.getUnderConstruction() + ", dialled " + dialled);
        List<Object> before = siteSnapshot(g, w);
        BuildCard.of(g, w, null);
        BuildCard.investors(g, w);
        BuildCard.appraise(g, w);
        BuildCard.of(g, w, BuildAdvice.Measure.of(BuildAdvice.Kind.POWER));
        check("with the order on site, the card, the investors and the appraisal leave the stack, its quantity, its sites, the wait and the contracts as they were",
                before.equals(siteSnapshot(g, w)) && site.getUnderConstruction() == 2, "wait " + g.onSiteMonths(w));
        BuildCard.Figures onSite = BuildCard.of(g, w, null);
        check("...and still read the unit at the new plant's 300 and 2 on site",
                onSite.template() == w && onSite.newUnit().getJobs(JobType.NO_DIPLOMA) == 300 && onSite.onSite() == 2 && onSite.owned() == 1,
                "owned " + onSite.owned() + ", on site " + onSite.onSite());

        long owned = site.getQuantity();
        Game.BuildResult[] placed = new Game.BuildResult[1];
        silent(() -> placed[0] = g.buildStack(w, 1, true));
        check("placing one beside them, with no construction, keeps the stack on its catalogue template and the queue as it was",
                placed[0] == Game.BuildResult.SUCCESS && bm.getStack(w) == c.stack && c.stack.getBuilding() == w
                        && c.stack.getQuantity() == owned + 1 && c.stack.getUnderConstruction() == 2,
                placed[0] + ", owned " + c.stack.getQuantity() + ", on site " + c.stack.getUnderConstruction());

        section("8. what is on site: the actual new plants' posts, and the power ahead from their completion");
        check("the posts and total jobs under construction are the actual new plants', not the fleet's",
                bm.getJobsUnderConstruction() == 2L * c.actual.total && apart(bm.getJobsUnderConstruction(), 2.0 * w.getTotalJobs())
                        && bm.postsOnSite(w.getSector(), JobType.NO_DIPLOMA) == 2.0 * c.actual.jobs[NO]
                        && bm.postsOnSite(w.getSector(), JobType.NO_DIPLOMA) == 600,
                "total " + bm.getJobsUnderConstruction() + " (actual " + 2 * c.actual.total + ", fleet " + 2 * w.getTotalJobs() + "), unskilled "
                        + bm.postsOnSite(w.getSector(), JobType.NO_DIPLOMA));

        // ...and the homes the posts on site ask for (REV-QN1, C): the landlords count every post on site at 2.25 heads.
        boolean onlyThem = true;
        for (BuildingsStacks s : bm.getStacksUnderConstruction()) onlyThem &= s.getBuilding() == w;
        double latent = g.getSectors().realEstate().latentHeadShortfall();
        double expectedLatent = latentWith(g, 2.0 * c.actual.total), fleetLatent = latentWith(g, 2.0 * w.getTotalJobs());
        check("premise: only the two plants are on site, and the fleet's posts would give the landlords another head shortage",
                onlyThem && apart(expectedLatent, fleetLatent), "actual " + expectedLatent + ", fleet " + fleetLatent);
        check("the landlords' head shortage counts the actual new plants' posts on site, not the fleet's",
                near(latent, expectedLatent) && apart(latent, fleetLatent), latent + " against " + expectedLatent + " (fleet's " + fleetLatent + ")");
    }

    /** The order bar's words for this many unskilled posts (BuildScreen.refreshOrderBar(), jobPlural()). */
    static String unskilled(long posts) { return String.format("%,d unskilled workers", posts); }

    /**
     * The landlords' head shortage with `coming` posts on site, from its parts (RealEstate.latentHeadShortfall()'s arithmetic,
     * no home on site): every post standing, the builders' laid-off crews included, and the coming ones, at 2.25 heads a post,
     * with the headroom over them (RN1-H, 0.9.157: a margin over demand), less the homes.
     */
    static double latentWith(Game g, double coming) {
        BuildingManager bm = g.getBuildingManager();
        double standing = bm.getPostsWithheld();
        for (long n : g.getJobs()) standing += n;
        return (standing + coming) * 2.25 * (1 + BusinessInvestment.TARGET_HEADROOM) - bm.getTotalHouseCapacity();
    }

    /** The power ahead of a queue on a stack: the readings at half the first completion, just before it, at it and past the last. */
    static double[] loadsAhead(Game g, BuildingsTemplate t, StringBuilder said, int[] done) {
        BuildingManager bm = g.getBuildingManager();
        int output = g.getBuildingOutputAtEveryPost();
        double firstDone = bm.siteUnitMonths(t, 1, output), secondDone = bm.siteUnitMonths(t, 2, output);
        double[] months = {firstDone / 2, Math.nextDown(firstDone), firstDone, Math.max(firstDone, secondDone) + 1};
        double[] loads = new double[months.length];
        for (int i = 0; i < months.length; i++) {
            done[i] = (bm.siteUnitMonths(t, 1, output) <= months[i] ? 1 : 0) + (bm.siteUnitMonths(t, 2, output) <= months[i] ? 1 : 0);
            loads[i] = WorksValue.pendingPowerLoad(g, months[i]);
            said.append(" [").append(months[i]).append(": ").append(loads[i]).append(" for ").append(done[i]).append("]");
        }
        return loads;
    }

    /* ---------------- 9 ---------------- */

    static Queued changedDraw() throws Exception {
        section("9. the power ahead: two queued Cement Plants without their rotary kiln draw the shaft kilns' power, from their completion");
        Game g = HistoricalLicenceCheck.town("QN1-kilns", true);
        allExcept(g, "");
        at(g, ResearchCatalog.knownFrom("E2"));
        HistoricalLicenceCheck.stand(g, "Construction Depot", 4);   // builders, so that the sites have a wait
        BuildingsTemplate kiln = HistoricalLicenceCheck.stand(g, "Cement Plant", 1);
        BuildingsStacks stack = g.getBuildingManager().getStack(kiln);
        // New depots hire up over months (a month on they are a tenth staffed, short of their upkeep): four months, hands kept.
        for (int m = 0; m < 4; m++) silent(() -> {
            MaterialsCheck.handWorkers(g);
            g.simulateMonths(1);
        });
        SubstationCheck.buildersForSite(g, 1); // PW5: actual depots must cover the standing wires' repairs before a site can finish.
        HistoricalRecipeCheck.fitted(kiln, stack);
        priceForBothReaders(g, Good.MACHINERY, .01);
        run(g);
        Ran fleet = ranOf(g, kiln, false);
        priceForBothReaders(g, Good.MACHINERY, 1e9);
        run(g);
        Set<String> lacking = rejected(g, kiln);
        Ran actual = whereItRuns(g, kiln, () -> ranOf(g, kiln, false));
        check("fixture: the fleet is fitted and on its rotary kiln; a new plant rejects the kiln, and its shaft kilns draw other power than the fleet's",
                lacking.contains("rotary-kiln") && stack.getUnfitted("rotary-kiln") == 0 && kiln.getCategory() != BuildingType.ELECTRICITY
                        && fleet.electricity == kiln.getElectricityConsumption() && actual.electricity > 0 && apart(actual.electricity, fleet.electricity),
                "fleet " + fleet.electricity + " kW, actual new " + actual.electricity + " kW, rejected " + lacking);
        BuildingManager bm = g.getBuildingManager();
        check("fixture: nothing on site and nothing pending", bm.getStacksUnderConstruction().isEmpty()
                && WorksValue.pendingPowerLoad(g, 1000) == 0 && g.getBuildingOutputAtEveryPost() > 0,
                "site output " + g.getBuildingOutputAtEveryPost() + ": builders " + g.getConstructionOutputAtEveryPost() + " (capacity "
                        + bm.getTotalConstructionCapacity() + ", fill " + g.getSectors().construction().getAverageFill() + ", roads "
                        + g.getRoadRatio() + ") less upkeep " + g.getMaintenancePoints());
        silent(() -> g.setCashForTest(Founding.WEALTHY_CASH * 100));
        Game.BuildResult[] queued = new Game.BuildResult[1];
        silent(() -> queued[0] = g.buildStack(kiln, 2, false));
        StringBuilder said = new StringBuilder();
        int[] done = new int[4];
        double[] loads = loadsAhead(g, kiln, said, done);
        check("fixture: two queued on the plant's own stack, the first finishing before the last",
                queued[0] == Game.BuildResult.SUCCESS && bm.getStack(kiln) == stack && stack.getBuilding() == kiln && stack.getUnderConstruction() == 2
                        && done[0] == 0 && done[1] == 0 && done[2] >= 1 && done[3] == 2, queued[0] + said.toString());
        boolean exact = true;
        for (int i = 0; i < loads.length; i++) exact &= loads[i] == done[i] * (double) actual.electricity;
        check("the power ahead adds the queued plants' own draw only from their completion: none before the first, one at it, both past the last",
                exact && loads[1] == 0 && loads[3] == 2.0 * actual.electricity, said.toString());
        check("...and not the fleet's draw", apart(loads[3], 2.0 * fleet.electricity) && apart(loads[2], done[2] * (double) fleet.electricity),
                "fleet's " + 2.0 * fleet.electricity + " for two");
        return new Queued(g, kiln);
    }

    /* ---------------- 13 ---------------- */

    /** Section 9's city after it: two Cement Plants queued on the plant's own stack, their new units' draw not the fleet's. */
    record Queued(Game g, BuildingsTemplate kiln) { }

    /** Every reading equal to the other to the bit (Double.doubleToLongBits). */
    static boolean sameBits(double[] a, double[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) if (Double.doubleToLongBits(a[i]) != Double.doubleToLongBits(b[i])) return false;
        return true;
    }

    static boolean sameBits(double a, double b) { return Double.doubleToLongBits(a) == Double.doubleToLongBits(b); }

    /** The power ahead at each horizon, read as the caller's scope (or none) has it. */
    static double[] loads(Game g, double[] horizons) {
        double[] out = new double[horizons.length];
        for (int i = 0; i < horizons.length; i++) out[i] = WorksValue.pendingPowerLoad(g, horizons[i]);
        return out;
    }

    /** Horizons from now to a month past the last unit on the kilns' site, by its own quotes: every completion inside them. */
    static double[] horizons(Game g, BuildingsTemplate kiln, int steps) {
        BuildingManager bm = g.getBuildingManager();
        double output = g.getBuildingOutputAtEveryPost(), last = 0;
        long on = bm.getStack(kiln).getUnderConstruction();
        for (long n = 1; n <= on; n++) last = Math.max(last, bm.siteUnitMonths(kiln, n, output));
        double[] out = new double[steps + 1];
        for (int i = 0; i <= steps; i++) out[i] = (last + 1) * (i + 1) / (steps + 1);
        return out;
    }

    static void heldForAPass(Queued q) throws Exception {
        section("13. held for one pass (AP1): inside WorksValue.hold() the power ahead is the bare figure to the bit; an order, a rush"
                + " and a priority on site are read afresh; one schedule is built with nothing changed; closed, nothing is held");
        Game g = q.g();
        BuildingsTemplate kiln = q.kiln();
        BuildingManager bm = g.getBuildingManager();
        String key = ConstructionControl.keyOf(kiln);
        double[] at = horizons(g, kiln, 24);
        double[] bare = loads(g, at);
        double far = at[at.length - 1] * 4;
        check("fixture: no scope open, two plants queued on the city's own site, and the power ahead some draw by the last horizon",
                WorksValue.open() == null && bm.getStack(kiln).getUnderConstruction() == 2 && bm.isCitySite(key) && bare[at.length - 1] > 0,
                "power ahead " + Arrays.toString(bare));

        // 1 and 4: to the bit, with one schedule built for the scope.
        long before = WorksValue.SCHEDULES.get();
        double[] inside, again;
        long built;
        boolean nested;
        try (WorksValue.Scope s = WorksValue.hold(g)) {
            inside = loads(g, at);
            try (WorksValue.Scope inner = WorksValue.hold(g)) { nested = inner == s; }
            again = loads(g, at);
            built = s.built;
            nested &= WorksValue.open() == s;
        }
        long schedules = WorksValue.SCHEDULES.get() - before;
        check("inside a scope the power ahead at each of " + at.length + " horizons is the bare figure to the bit, at the first and second reading",
                sameBits(inside, bare) && sameBits(again, bare), Arrays.toString(inside));
        check("one schedule is built for the scope's " + 2 * at.length + " readings with nothing changed on site, and a scope opened"
                + " inside it is the same one, open past the inner close", built == 1 && schedules == 1 && nested,
                built + " built in the scope, " + schedules + " counted");
        before = WorksValue.SCHEDULES.get();
        loads(g, at);
        long bareBuilt = WorksValue.SCHEDULES.get() - before;
        check("premise: outside a scope each reading builds its own schedule", bareBuilt == at.length, bareBuilt + " for " + at.length);

        // Another city, another thread, and another city's hold nested in g's: none of them reads g's held loads (REV-AP1).
        GameFiles.Result saved = RecipeCheck.save10(g, "AP1 second city");
        Game other = RecipeCheck.load10(g);
        BuildingsTemplate otherKiln = other.getBuildingManager().getTemplateByName(kiln.getName());
        Game.BuildResult[] otherOrder = new Game.BuildResult[1];
        silent(() -> other.setCashForTest(Founding.WEALTHY_CASH * 100));
        silent(() -> otherOrder[0] = other.buildStack(otherKiln, 1, false));
        double[] wide = Arrays.copyOf(at, at.length + 1);   // the 25 horizons and far, past every unit on both sites
        wide[at.length] = far;
        double[] gWide = loads(g, wide);
        double[] otherBare = loads(other, wide);
        check("premise: a second city, g saved and loaded, a third plant ordered there: its power ahead is its own, not g's",
                saved != null && saved.ok && other != g && otherOrder[0] == Game.BuildResult.SUCCESS
                        && other.getBuildingManager().getStack(otherKiln).getUnderConstruction() == 3 && !sameBits(otherBare, gWide),
                otherOrder[0] + ": " + Arrays.toString(otherBare));
        double[] otherInside, gAround;
        long otherBuilt, builtAround;
        try (WorksValue.Scope s = WorksValue.hold(g)) {
            loads(g, wide);
            long b0 = WorksValue.SCHEDULES.get();
            otherInside = loads(other, wide);
            otherBuilt = WorksValue.SCHEDULES.get() - b0;
            gAround = loads(g, wide);
            builtAround = s.built;
        }
        check("the second city read inside g's scope reads its own bare figure to the bit, built afresh at each reading, and g's scope"
                + " builds nothing more", sameBits(otherInside, otherBare) && otherBuilt == wide.length && sameBits(gAround, gWide)
                && builtAround == 1, otherBuilt + " built for the second city's " + wide.length + " readings, " + builtAround + " in g's scope");
        double bareFar = WorksValue.pendingPowerLoad(g, far);
        double[] onThread = new double[1];
        long threadBuilt, builtThread;
        try (WorksValue.Scope s = WorksValue.hold(g)) {
            WorksValue.pendingPowerLoad(g, far);
            long b0 = WorksValue.SCHEDULES.get();
            Thread reader = new Thread(() -> onThread[0] = WorksValue.pendingPowerLoad(g, far), "AP1 second thread");
            reader.start();
            reader.join();
            threadBuilt = WorksValue.SCHEDULES.get() - b0;
            builtThread = s.built;
        }
        check("g read on a second thread while g's scope is open on this one: the bare figure, from a schedule of its own"
                + " (SCHEDULES +1), and the scope builds nothing more", sameBits(onThread[0], bareFar) && threadBuilt == 1 && builtThread == 1,
                onThread[0] + " against " + bareFar + ", " + threadBuilt + " built on the thread, " + builtThread + " in the scope");
        double[] nestedOther, gNested;
        long nestedBuilt, builtNested;
        boolean sameScope, stillOpen;
        try (WorksValue.Scope s = WorksValue.hold(g)) {
            loads(g, wide);
            try (WorksValue.Scope o = WorksValue.hold(other)) {
                sameScope = o == s;
                long b0 = WorksValue.SCHEDULES.get();
                nestedOther = loads(other, wide);
                nestedBuilt = WorksValue.SCHEDULES.get() - b0;
            }
            stillOpen = WorksValue.open() == s;
            gNested = loads(g, wide);
            builtNested = s.built;
        }
        check("the second city's hold nested in g's is g's scope one deeper: the second city reads bare, built afresh at each"
                + " reading, and g's scope stays open and held past the inner close", sameScope && sameBits(nestedOther, otherBare)
                && nestedBuilt == wide.length && stillOpen && sameBits(gNested, gWide) && builtNested == 1 && WorksValue.open() == null,
                nestedBuilt + " built for the second city, " + builtNested + " in g's scope");

        // 2: an order inside the scope.
        silent(() -> g.setCashForTest(Founding.WEALTHY_CASH * 100));
        Game.BuildResult[] ordered = new Game.BuildResult[1];
        double heldBefore, afterOrder;
        long builtOrder;
        try (WorksValue.Scope s = WorksValue.hold(g)) {
            heldBefore = WorksValue.pendingPowerLoad(g, far);
            silent(() -> ordered[0] = g.buildStack(kiln, 1, false));
            afterOrder = WorksValue.pendingPowerLoad(g, far);
            builtOrder = s.built;
        }
        double bareAfter = WorksValue.pendingPowerLoad(g, far);
        check("premise: a third plant ordered moves the power ahead", ordered[0] == Game.BuildResult.SUCCESS
                && bm.getStack(kiln).getUnderConstruction() == 3 && apart(bareAfter, heldBefore), heldBefore + " -> " + bareAfter);
        check("an order inside a scope: the next reading is the bare one after the order, to the bit, from a schedule built anew",
                sameBits(afterOrder, bareAfter) && builtOrder == 2, afterOrder + " against " + bareAfter + ", " + builtOrder + " built");

        // 3: a rush, and then a priority, inside the scope.
        at = horizons(g, kiln, 48);
        double[] unrushed, rushedInside;
        long builtRush;
        try (WorksValue.Scope s = WorksValue.hold(g)) {
            unrushed = loads(g, at);
            g.rushSite(key, true);
            rushedInside = loads(g, at);
            builtRush = s.built;
        }
        double[] rushedBare = loads(g, at);
        check("premise: the city's site is rushed, and the rush moves the power ahead", bm.getControl().isRushed(key)
                && !sameBits(rushedBare, unrushed), Arrays.toString(unrushed) + " -> " + Arrays.toString(rushedBare));
        check("a rush inside a scope: the readings after it are the bare ones to the bit", sameBits(rushedInside, rushedBare)
                && builtRush == 2, builtRush + " built");
        g.rushSite(key, false);
        // PW5: isolate city priority from the independent builders-first rule; the depot must be allowed to follow the kilns.
        bm.setBuildersFirstForTest(false);
        BuildingsTemplate depot = bm.getTemplateByName("Construction Depot");
        Game.BuildResult[] second = new Game.BuildResult[1];
        silent(() -> second[0] = g.buildStack(depot, 1, false));
        int kilnAt = bm.cityOrder().indexOf(key), depotAt = bm.cityOrder().indexOf(ConstructionControl.keyOf(depot));
        double[] unordered, orderedInside;
        long builtPriority;
        boolean moved;
        try (WorksValue.Scope s = WorksValue.hold(g)) {
            unordered = loads(g, at);
            moved = g.moveSite(key, kilnAt < depotAt ? 1 : -1);
            orderedInside = loads(g, at);
            builtPriority = s.built;
        }
        double[] orderedBare = loads(g, at);
        check("premise: a second city site, the kilns moved past it in the city's order, and the priority moves the power ahead",
                second[0] == Game.BuildResult.SUCCESS && moved && bm.getControl().isPrioritySet() && !sameBits(orderedBare, unordered),
                second[0] + ", kilns " + kilnAt + " depot " + depotAt + ": " + Arrays.toString(unordered) + " -> " + Arrays.toString(orderedBare));
        check("a priority inside a scope: the readings after it are the bare ones to the bit", sameBits(orderedInside, orderedBare)
                && builtPriority == 2, builtPriority + " built");
        bm.setBuildersFirstForTest(true);

        // Two stamped inputs moved inside a scope, by no order: the kilns' progress, and the builders-first switch (REV-AP1).
        BuildingsStacks kilns = bm.getStack(kiln);
        double progress = kilns.getConstructionProgress();
        double[] unmoved, movedInside;
        long builtProgress;
        try (WorksValue.Scope s = WorksValue.hold(g)) {
            unmoved = loads(g, at);
            kilns.setConstructionProgress(progress + (kilns.pointsOnSite() - progress) / 2);
            movedInside = loads(g, at);
            builtProgress = s.built;
        }
        double[] movedBare = loads(g, at);
        check("premise: the kilns' work half done moves the power ahead", !sameBits(movedBare, unmoved),
                Arrays.toString(unmoved) + " -> " + Arrays.toString(movedBare));
        check("progress moved inside a scope: the readings after it are the bare ones to the bit, from a schedule built anew",
                sameBits(movedInside, movedBare) && builtProgress == 2, builtProgress + " built");
        kilns.setConstructionProgress(progress);
        double[] firstOn, offInside;
        long builtSwitch;
        try (WorksValue.Scope s = WorksValue.hold(g)) {
            firstOn = loads(g, at);
            bm.setBuildersFirstForTest(false);
            offInside = loads(g, at);
            builtSwitch = s.built;
        }
        double[] offBare = loads(g, at);
        bm.setBuildersFirstForTest(true);
        check("builders first turned off inside a scope: the readings after it are the bare ones to the bit, from a schedule"
                + " built anew", sameBits(offInside, offBare) && builtSwitch == 2 && sameBits(firstOn, loads(g, at)),
                builtSwitch + " built; " + Arrays.toString(firstOn) + " -> " + Arrays.toString(offBare));

        // 5: closed, nothing is held.
        double first = WorksValue.pendingPowerLoad(g, far);
        g.resetSiteOrder();
        silent(() -> g.buildStack(kiln, 1, false));
        double then = WorksValue.pendingPowerLoad(g, far), fresh;
        try (WorksValue.Scope s = WorksValue.hold(g)) { fresh = WorksValue.pendingPowerLoad(g, far); }
        check("closed, nothing is held: no scope open, and a reading after a fourth plant ordered reads it, as a scope opened then does",
                WorksValue.open() == null && bm.getStack(kiln).getUnderConstruction() == 4 && apart(then, first) && sameBits(then, fresh),
                first + " -> " + then);

        // relief()'s own reading of the held schedule and draws: a power plant's avoided loss on a rationed grid, to the bit
        // (EssentialWorksCheck's rationing: twice the production asked, the sectors on half their power), with builders enough
        // that the plants on site open inside the plant's life. Last, as it moves the grid and the crews.
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        BuildingsTemplate coal = bm.getTemplateByName("Coal Power Plant");
        HistoricalLicenceCheck.stand(g, "Construction Depot", 40);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        u.setWattsConsumption(Math.max(u.getProduction(), then) * 2);
        for (Sector sector : g.getSectors().all()) sector.setEnergyRatio(.5);
        double avoidedBare = WorksValue.avoided(g, power, Map.of(), Map.of(coal, 1L), 0), avoidedHeld;
        long builtAvoided;
        try (WorksValue.Scope s = WorksValue.hold(g)) {
            avoidedHeld = WorksValue.avoided(g, power, Map.of(), Map.of(coal, 1L), 0);
            builtAvoided = s.built;
        }
        check("premise: on the rationed grid a Coal Power Plant prevents some output loss, with the plants on site drawing ahead",
                avoidedBare > 0 && WorksValue.pendingPowerLoad(g, BuildAdvice.LIFE_MONTHS) > 0,
                "avoided " + avoidedBare + ", production " + u.getProduction() + ", power ahead " + WorksValue.pendingPowerLoad(g, BuildAdvice.LIFE_MONTHS));
        check("...and its avoided loss read inside a scope, from the held schedule and draws, is the bare figure to the bit",
                sameBits(avoidedHeld, avoidedBare) && builtAvoided == 1, avoidedHeld + " against " + avoidedBare + ", " + builtAvoided + " built");
    }

    /* ---------------- 10 ---------------- */

    static void crewDemand() throws Exception {
        section("10. what posts on site ask of the city: two queued Shared Services Centres without their computer, their college places and their homes");
        Game g = HistoricalLicenceCheck.town("QN1-clerks", true);
        allExcept(g, "");
        at(g, Math.max(ResearchCatalog.knownFrom("B6"), ResearchCatalog.knownFrom("B1")));
        HistoricalLicenceCheck.stand(g, "Construction Depot", 4);
        BuildingsTemplate centre = HistoricalLicenceCheck.stand(g, "Shared Services Centre", 1);
        BuildingsStacks stack = g.getBuildingManager().getStack(centre);
        silent(() -> g.simulateMonths(1));
        HistoricalRecipeCheck.fitted(centre, stack);
        priceForBothReaders(g, Good.MACHINERY, .01);
        run(g);
        Ran fleet = ranOf(g, centre, false);
        priceForBothReaders(g, Good.MACHINERY, 1e9);
        run(g);
        Set<String> lacking = rejected(g, centre);
        Ran actual = whereItRuns(g, centre, () -> ranOf(g, centre, false));
        double fleetCollege = college(fleet), actualCollege = college(actual);
        check("fixture: the fleet is fitted; a new centre rejects the business computer and has more college posts than the fleet",
                lacking.contains("business-computer") && fleetCollege > 0 && actualCollege > fleetCollege && actual.total > fleet.total,
                "college posts: fleet " + fleetCollege + ", actual new " + actualCollege + "; rejected " + lacking);
        // Nobody college-qualified: every college post, standing or on site, is a place the colleges would fill (CityNeeds.hires()).
        PopulationManager people = g.getPopulationManager();
        double[] heads = people.workforceByBand().clone();
        heads[WageBand.COLLEGE.ordinal()] = 0;
        people.restoreSkilledHeads(heads);
        BuildingManager bm = g.getBuildingManager();
        double before = CityNeeds.hires(g, EducationType.COLLEGE, 1);
        check("fixture: nothing on site, nobody college-qualified, and the colleges' places are the standing college posts",
                bm.getStacksUnderConstruction().isEmpty() && people.workforceByBand()[WageBand.COLLEGE.ordinal()] == 0 && before > 0,
                "places " + before);
        silent(() -> g.setCashForTest(Founding.WEALTHY_CASH * 100));
        Game.BuildResult[] queued = new Game.BuildResult[1];
        silent(() -> queued[0] = g.buildStack(centre, 2, false));
        check("fixture: two queued on the centre's own stack", queued[0] == Game.BuildResult.SUCCESS && bm.getStack(centre) == stack
                && stack.getBuilding() == centre && stack.getUnderConstruction() == 2, queued[0] + ", on site " + stack.getUnderConstruction());
        double places = CityNeeds.hires(g, EducationType.COLLEGE, 1) - before;
        check("the colleges' places grow by the queued centres' own college posts, not the fleet's",
                near(places, 2 * actualCollege) && apart(places, 2 * fleetCollege), places + " against " + 2 * actualCollege + " (fleet's " + 2 * fleetCollege + ")");
        double latent = g.getSectors().realEstate().latentHeadShortfall();
        double expectedLatent = latentWith(g, 2.0 * actual.total), fleetLatent = latentWith(g, 2.0 * fleet.total);
        check("the landlords' head shortage counts the queued centres' own posts, not the fleet's",
                near(latent, expectedLatent) && apart(latent, fleetLatent), latent + " against " + expectedLatent + " (fleet's " + fleetLatent + ")");
    }

    /** A building's posts in the college band (COLLEGE_HEALTH, _BUSINESS, _ENGINEERING), the band a college teaches. */
    static double college(Ran r) {
        double n = 0;
        for (JobType j : JobType.values()) if (WageBand.of(j) == WageBand.COLLEGE) n += r.jobs[j.ordinal()];
        return n;
    }

    /* ---------------- 11 ---------------- */

    static void powerAhead() throws Exception {
        section("11. power ahead: a queued Coal Power Plant that rejects its turbine and its pulverised coal generates less");
        Game g = HistoricalLicenceCheck.town("QN1-power", true);
        allExcept(g, "");
        at(g, Math.max(ResearchCatalog.knownFrom("PE6"), ResearchCatalog.knownFrom("PE16")));
        BuildingsTemplate coal = HistoricalLicenceCheck.stand(g, "Coal Power Plant", 1);
        BuildingsStacks stack = g.getBuildingManager().getStack(coal);
        silent(() -> g.simulateMonths(1));
        HistoricalRecipeCheck.fitted(coal, stack);
        priceForBothReaders(g, Good.MACHINERY, .01);
        run(g);
        Ran fleet = ranOf(g, coal, false);
        priceForBothReaders(g, Good.MACHINERY, 1e9);
        run(g);
        Set<String> lacking = rejected(g, coal);
        Ran actual = whereItRuns(g, coal, () -> ranOf(g, coal, false));
        check("fixture: the fleet is fitted and generates more than a new plant, which rejects both the turbine and the pulverised coal; the posts and draw do not move",
                lacking.equals(Set.of("steam-turbine", "pulverised-coal")) && fleet.production1 > actual.production1 && actual.production1 > 0
                        && Arrays.equals(fleet.jobs, actual.jobs) && fleet.electricity == actual.electricity && fleet.production1 == coal.getProduction1(),
                "fleet " + fleet.production1 + " kW, actual new " + actual.production1 + " kW, rejected " + lacking);
        BuildAdvice.Measure power = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);
        silent(() -> g.setCashForTest(Founding.WEALTHY_CASH * 100));
        Game.BuildResult[] queued = new Game.BuildResult[1];
        silent(() -> queued[0] = g.buildStack(coal, 1, false));
        Map<BuildingsTemplate, Long> site = BuildAdvice.onSite(g, power);
        check("fixture: one Coal Power Plant is queued and the power measure's on-site map holds it on the catalogue template",
                queued[0] == Game.BuildResult.SUCCESS && site.size() == 1 && site.get(coal) == 1L && site.keySet().iterator().next() == coal,
                queued[0] + ", " + site.size() + " on site");
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double f0 = BuildAdvice.figure(g, power, Map.of());
        double f1 = BuildAdvice.figure(g, power, site);
        double base = u.getBaseProduction();
        // The measure is demand over supply (BuildAdvice.figure()): supply is the base plus what is added, times the pooled fill of
        // both utilities' posts with the added plant's posts at the city's fill; demand gains the added plant's own draw. The
        // expected figure takes the generation, the posts and the draw from the actual new plant, the rest from the city.
        double[] fill = g.getPopulationManager().getJobFillRate();
        BuildingManager bm = g.getBuildingManager();
        double jobs0 = 0;
        for (long n : bm.getJobArrayPerCategory(BuildingType.ELECTRICITY)) jobs0 += n;
        for (long n : bm.getJobArrayPerCategory(BuildingType.WATER)) jobs0 += n;
        double fill0 = u.getAverageUtilityFill(), posts = 0, filled = 0;
        for (JobType j : JobType.values()) {
            posts += actual.jobs[j.ordinal()];
            filled += actual.jobs[j.ordinal()] * fill[j.ordinal()];
        }
        double fill1 = (fill0 * jobs0 + filled) / (jobs0 + posts);
        // PW1, 2026-11-20: power is planned for the calm peak, the draw x PowerDispatch.PLAN_PEAK over the FIRM supply (the base less the wind's
        // non-firm part): the advice's own supply and demand today (BuildAdvice.supplyDemand()) give the firm base and the peak's draw, and the
        // added plant's draw counts at the peak as the city's does (a Coal Power Plant: its generation all firm).
        double[] today = BuildAdvice.supplyDemand(g, power, Map.of());
        double firm = today[0] / fill0, demand0 = today[1];
        double expected = (demand0 + PowerDispatch.PLAN_PEAK * actual.electricity) / ((firm + actual.production1) * fill1);
        double fleetReading = (demand0 + PowerDispatch.PLAN_PEAK * fleet.electricity) / ((firm + fleet.production1) * fill1);
        check("fixture: the utilities have posts and a fill, and a base to supply from",
                jobs0 > 0 && fill0 > 0 && base > 0 && f0 > 0 && posts > 0, "jobs " + jobs0 + ", fill " + fill0 + " -> " + fill1 + ", base " + base + ", figure " + f0);
        check("premise: the fleet's generation would read a different figure than the actual new plant's",
                apart(expected, fleetReading), "expected " + expected + ", fleet " + fleetReading);
        check("the power measure reads the actual new plant's generation (and its draw)",
                near(f1, expected) && apart(f1, fleetReading), f1 + " against " + expected + " (fleet's reading " + fleetReading + ")");
    }

    /* ---------------- 12 ---------------- */

    static void cardGoods() throws Exception {
        section("12. the card's goods: a furnace without its caster burns fuel oil, a camp without its chainsaw cuts less, and the card says so");
        Game g = HistoricalLicenceCheck.town("QN1-goods", true);
        allExcept(g, "");
        at(g, Math.max(ResearchCatalog.knownFrom("E9"), ResearchCatalog.knownFrom("PE26")));
        BuildingsTemplate furnace = HistoricalLicenceCheck.stand(g, "Electric Arc Furnace", 1);
        BuildingsTemplate camp = HistoricalLicenceCheck.stand(g, "Logging Camp", 1);
        BuildingManager bm = g.getBuildingManager();
        silent(() -> g.simulateMonths(1));
        HistoricalRecipeCheck.fitted(furnace, bm.getStack(furnace));
        HistoricalRecipeCheck.fitted(camp, bm.getStack(camp));
        priceForBothReaders(g, Good.MACHINERY, .01);
        run(g);
        Map<Good, Double> fleetUses = new java.util.EnumMap<>(furnace.goodsUsed()), fleetCut = new java.util.EnumMap<>(camp.goodsMade());
        priceForBothReaders(g, Good.MACHINERY, 1e9);
        run(g);
        Set<String> furnaceLacks = rejected(g, furnace), campLacks = rejected(g, camp);
        Map<Good, Double> actualUses = whereItRuns(g, furnace, () -> new java.util.EnumMap<>(furnace.goodsUsed()));
        Map<Good, Double> actualCut = whereItRuns(g, camp, () -> new java.util.EnumMap<>(camp.goodsMade()));
        check("fixture: both fleets are fitted; a new furnace rejects the continuous caster and burns fuel oil the fleet does not",
                furnaceLacks.contains("continuous-caster") && actualUses.getOrDefault(Good.FUEL_OIL, 0.0) > 0
                        && fleetUses.getOrDefault(Good.FUEL_OIL, 0.0) == 0 && furnace.uses(Good.FUEL_OIL) == 0,
                "fuel oil: actual new " + actualUses.get(Good.FUEL_OIL) + ", fleet " + fleetUses.get(Good.FUEL_OIL) + "; rejected " + furnaceLacks);
        boolean cutApart = false;
        for (Good c : actualCut.keySet()) cutApart |= apart(actualCut.get(c), fleetCut.getOrDefault(c, 0.0));
        check("fixture: a new camp rejects the chainsaw and cuts other figures than the fleet",
                campLacks.contains("chainsaw") && !actualCut.isEmpty() && cutApart, "actual new " + actualCut + ", fleet " + fleetCut);

        List<BuildCard.Outlet> outlets = BuildCard.outlets(g, camp);
        boolean asCut = !outlets.isEmpty(), notFleet = true;
        StringBuilder said = new StringBuilder();
        for (BuildCard.Outlet o : outlets) {
            asCut &= near(o.made(), actualCut.getOrDefault(o.good(), 0.0));
            notFleet &= apart(o.made(), fleetCut.getOrDefault(o.good(), 0.0));
            said.append(o.good()).append(' ').append(o.made()).append("; ");
        }
        check("the camp's card lists its outlets at the actual new camp's cut, not the fleet's",
                asCut && notFleet && outlets.size() == actualCut.values().stream().filter(v -> v > 0).count(), said + "actual " + actualCut);

        // What no fixture can show today (REV-QN1-2): the group heading's made words read only which goods are made, and the card's
        // stands() serves buildings on typed ground; no piece in the catalogue changes which goods a building makes, nor what one
        // on typed ground makes at all, so there a new unit and the fleet read alike. The tripwire: a piece that does fails this,
        // and must bring the card check those readers then need.
        List<String> kinds = new java.util.ArrayList<>(), typed = new java.util.ArrayList<>();
        for (BuildingsTemplate t : bm.getTemplates()) {
            BuildingsTemplate.Site site = t.site();
            boolean onTypes = site != null && site.resource() != null && site.resource().hasTypes();
            Map<Good, Double> top = t.getRecipes().stream().filter(Recipe::isDefault).findFirst().map(Recipe::makes).orElse(Map.of());
            for (RecipeChange rc : t.getRecipeChanges()) {
                if (rc.piece() == null) continue;
                for (Map.Entry<Good, Double> e : rc.makes().entrySet()) {
                    if (e.getValue() == 0) continue;
                    double base = top.getOrDefault(e.getKey(), 0.0);
                    if (base == 0 || base + e.getValue() <= 0) kinds.add(t.getName() + " without " + rc.piece() + ": " + e.getKey());
                    if (onTypes) typed.add(t.getName() + " without " + rc.piece() + ": " + e.getKey());
                }
            }
            if (t.recipePieceId() == null) continue;
            for (Recipe r : t.getRecipes()) {
                if (!r.makes().keySet().equals(top.keySet())) kinds.add(t.getName() + "'s " + r.getId() + " recipe");
                if (onTypes && !r.makes().equals(top)) typed.add(t.getName() + "'s " + r.getId() + " recipe");
            }
        }
        check("tripwire: no piece changes which goods a building makes, nor what a building on typed ground makes (the group heading's made words and stands() read alike for a new unit)",
                kinds.isEmpty() && typed.isEmpty(), "which goods: " + kinds + "; on typed ground: " + typed);

        String down = toolkit();
        if (down != null) {
            out.println("  (the furnace's card words are not read: the JavaFX toolkit cannot start here - " + down + ")");
            return;
        }
        KeptBench bench = KeptCheck.fx(() -> new KeptBench(g));
        BuildCard.Figures card = BuildCard.of(g, furnace, null);
        String words = KeptCheck.fx(() -> bench.buildHeroDetail(card));
        String fuel = BuildCard.goodWords(Good.FUEL_OIL);
        check("the furnace's card says it is made from the actual new furnace's inputs, fuel oil among them",
                card.kind() == BuildCard.Kind.MAKER && words != null && words.contains("from ") && words.contains(fuel),
                "\"" + words + "\"");

        // The heading of the furnace's group on its market page (BuildScreen.groupSubtitle()): what its cards are made from.
        BuildCard.Group found = null;
        for (BuildCard.Group group : BuildCard.groups(g, BuildAdvice.categoryOf(furnace).name(), null))
            for (BuildCard.Figures f : group.cards()) if (f.template() == furnace) found = group;
        final BuildCard.Group steel = found;
        boolean fleetBurnsOil = false;
        if (steel != null) for (BuildCard.Figures f : steel.cards()) fleetBurnsOil |= f.template().uses(Good.FUEL_OIL) > 0;
        String heading = steel == null ? null : KeptCheck.fx(() -> bench.buildGroupSubtitle(steel));
        String oil = Good.FUEL_OIL.label().toLowerCase();
        check("the furnace's group heading says its cards are made from fuel oil, which only the actual new furnaces burn",
                steel != null && !fleetBurnsOil && heading != null && heading.contains("from ") && heading.contains(oil),
                "\"" + heading + "\"" + (fleetBurnsOil ? "; a fleet in the group burns it" : ""));

        // The camp's sentences (whatItDoes(t, unit), the stat cover's): what it makes a month, at the actual new camp's cut.
        BuildCard.Figures campCard = BuildCard.of(g, camp, null);
        List<String> does = KeptCheck.fx(() -> bench.buildWhatItDoes(campCard));
        boolean saysActual = true, saysFleet = false;
        StringBuilder makes = new StringBuilder();
        for (Map.Entry<Good, Double> e : actualCut.entrySet()) {
            String of = " of " + e.getKey().label().toLowerCase() + " a month";
            String actualLine = "Makes " + ham.citybuildersim.ui.Money.formatter.format(e.getValue()) + " ";
            String fleetLine = "Makes " + ham.citybuildersim.ui.Money.formatter.format(fleetCut.getOrDefault(e.getKey(), 0.0)) + " ";
            saysActual &= does.stream().anyMatch(s -> s.startsWith(actualLine) && s.contains(of));
            saysFleet |= does.stream().anyMatch(s -> s.startsWith(fleetLine) && s.contains(of));
        }
        for (String s : does) if (s.startsWith("Makes ")) makes.append('"').append(s).append("\" ");
        check("the camp's card says it makes the actual new camp's cut a month, not the fleet's",
                !actualCut.isEmpty() && saysActual && !saysFleet, makes + "actual " + actualCut + ", fleet " + fleetCut);
    }

    public static void main(String[] args) throws Exception {
        quietRun = Arrays.asList(args).contains("-q");
        // The fixture towns are the sibling harnesses': their model chatter goes to the same quiet stream.
        RecipeCheck.out = out; RecipeCheck.quiet = HistoricalRecipeCheck.quiet;
        FittingCheck.out = out; FittingCheck.quiet = HistoricalRecipeCheck.quiet;
        EquipmentCheck.out = out; EquipmentCheck.quiet = HistoricalRecipeCheck.quiet;
        Forms1900Check.out = out; Forms1900Check.quiet = HistoricalRecipeCheck.quiet;
        try {
            standingEverywhere();
            Cars cars = robotTown("QN1-robot");
            rejectedRobot(cars);
            staffingBoundary(cars);
            pulpChoice();
            bankClerks();
            matching(cars);
            identitiesAndSite();
            Queued kilns = changedDraw();
            crewDemand();
            powerAhead();
            cardGoods();
            heldForAPass(kilns);
        } catch (Throwable t) {
            // Once sections 7 and 12 have started the JavaFX toolkit, its thread holds the JVM open past a throw: say it, red, and go.
            out.println("FAIL: " + t);
            t.printStackTrace();
            System.exit(1);
        }
        out.println(fails == 0 ? "All NewUnitCheck assertions passed (" + checks + ")." : fails + " FAILED of " + checks);
        System.exit(fails == 0 ? 0 : 1);
    }
}
