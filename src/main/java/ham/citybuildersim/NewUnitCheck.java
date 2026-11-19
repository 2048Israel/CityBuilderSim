package ham.citybuildersim;

import com.google.gson.JsonElement;
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
 * No oracle here calls NewUnit or anything QN1 added. Each expected value is taken from the ACTUAL state a new building runs:
 * the fleet's stack set unfitted for the pieces a new one would not get, the recipes applied, and the template's live figures
 * (or the investors' estimate) read there, then the fitting restored; or it is composed from the recipe data. The fleet's
 * own figures appear only as each section's PREMISE: that the expected value differs from what a reader of the fleet
 * would give, so a reader that kept reading the fleet fails.
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
 *      fitting input, and an order on site is not touched by reading them.
 *   8. What is on site counts the actual new plant's posts, and the power ahead adds its draw only from its completion (a
 *      guard on the real stack: the recipe data moves no draw).
 *   9. Power ahead: a queued Coal Power Plant whose new unit rejects the turbine and the pulverised coal generates less,
 *      and the power measure reads that generation.
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
            r.estimate = g.getBusinessInvestment().estimatedMonthlyProfit(t.getSector(), t);
            r.running = BuildCard.runningCost(g, t);
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
        double actualRunning = whereItRuns(g, w, () -> BuildCard.runningCost(g, w));
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
        silent(() -> g.simulateMonths(1));
        HistoricalRecipeCheck.fitted(w, c.stack);
        priceForBothReaders(g, Good.MACHINERY, .01);
        run(g);
        BuildCard.Figures later = BuildCard.of(g, w, null);
        check("a month on, a fresh card is of the same template with the current unit",
                later.template() == w && later.newUnit().template() == w && later.newUnit().getJobs(JobType.NO_DIPLOMA) == 100
                        && g.getBuildingManager().getStack(w) == c.stack, "posts " + later.newUnit().getJobs(JobType.NO_DIPLOMA));
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
        Game.BuildResult[] queued = new Game.BuildResult[1];
        silent(() -> queued[0] = g.buildStack(w, 2, false));
        BuildingsStacks site = bm.getStack(w);
        check("ordering two through the queue keeps the stack on its catalogue template",
                queued[0] == Game.BuildResult.SUCCESS && site == c.stack && site.getBuilding() == w && site.getUnderConstruction() == 2,
                queued[0] + ", on site " + site.getUnderConstruction());
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

        int output = g.getBuildingOutputAtEveryPost();
        double firstDone = bm.siteUnitMonths(w, 1, output), secondDone = bm.siteUnitMonths(w, 2, output);
        double draw = c.actual.electricity;
        check("guard: the recipe data moves no draw, so a new plant's draw is the fleet's (and the queue's quotes are finite)",
                draw == w.getElectricityConsumption() && draw > 0 && Double.isFinite(firstDone) && Double.isFinite(secondDone) && secondDone >= firstDone,
                "draw " + draw + ", first " + firstDone + ", second " + secondDone);
        double[] months = {firstDone / 2, Math.nextDown(firstDone), firstDone, Math.max(firstDone, secondDone) + 1};
        boolean exact = true;
        StringBuilder said = new StringBuilder();
        double sawFirst = -1, sawEarly = -1, sawLast = -1;
        for (int i = 0; i < months.length; i++) {
            int done = (bm.siteUnitMonths(w, 1, output) <= months[i] ? 1 : 0) + (bm.siteUnitMonths(w, 2, output) <= months[i] ? 1 : 0);
            double load = WorksValue.pendingPowerLoad(g, months[i]);
            exact &= load == done * draw;
            said.append(" [").append(months[i]).append(": ").append(load).append(" for ").append(done).append("]");
            if (i == 1) sawEarly = load;
            if (i == 2) sawFirst = load;
            if (i == 3) sawLast = load;
        }
        check("the power ahead adds the queued plants' draw only from their completion: none below the first, one at it, both at the last (a guard on the real stack)",
                exact && sawEarly == 0 && sawFirst == draw && sawLast == 2 * draw, said.toString());
    }

    /* ---------------- 9 ---------------- */

    static void powerAhead() throws Exception {
        section("9. power ahead: a queued Coal Power Plant that rejects its turbine and its pulverised coal generates less");
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
        double demand0 = f0 * base * fill0;
        double expected = (demand0 + actual.electricity) / ((base + actual.production1) * fill1);
        double fleetReading = (demand0 + fleet.electricity) / ((base + fleet.production1) * fill1);
        check("fixture: the utilities have posts and a fill, and a base to supply from",
                jobs0 > 0 && fill0 > 0 && base > 0 && f0 > 0 && posts > 0, "jobs " + jobs0 + ", fill " + fill0 + " -> " + fill1 + ", base " + base + ", figure " + f0);
        check("premise: the fleet's generation would read a different figure than the actual new plant's",
                apart(expected, fleetReading), "expected " + expected + ", fleet " + fleetReading);
        check("the power measure reads the actual new plant's generation (and its draw)",
                near(f1, expected) && apart(f1, fleetReading), f1 + " against " + expected + " (fleet's reading " + fleetReading + ")");
    }

    public static void main(String[] args) throws Exception {
        quietRun = Arrays.asList(args).contains("-q");
        // The fixture towns are the sibling harnesses': their model chatter goes to the same quiet stream.
        RecipeCheck.out = out; RecipeCheck.quiet = HistoricalRecipeCheck.quiet;
        FittingCheck.out = out; FittingCheck.quiet = HistoricalRecipeCheck.quiet;
        EquipmentCheck.out = out; EquipmentCheck.quiet = HistoricalRecipeCheck.quiet;
        Forms1900Check.out = out; Forms1900Check.quiet = HistoricalRecipeCheck.quiet;
        standingEverywhere();
        Cars cars = robotTown("QN1-robot");
        rejectedRobot(cars);
        staffingBoundary(cars);
        pulpChoice();
        bankClerks();
        matching(cars);
        identitiesAndSite();
        powerAhead();
        out.println(fails == 0 ? "All NewUnitCheck assertions passed (" + checks + ")." : fails + " FAILED of " + checks);
        System.exit(fails == 0 ? 0 : 1);
    }
}
