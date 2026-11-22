package ham.citybuildersim;

import ham.citybuildersim.sectors.SpreadPlanner;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * E21's crop-only historical undo, its actual farm production and patent bill,
 * and the saved physical state on either side of the discovery.
 * <p>
 * WHY (R6e): a dated catalogue entry alone would leave old farms modern; a
 * saved recipe without the new missing-change flag could do the same on load.
 * The 22% is the approved crop-mix estimate, not a universal measured effect.
 * <p>
 * What this proves:
 * 1. Only the two farms gain the sourced signed CROPS undo; modern anchors,
 *    all other figures, catalogue dates, lag and work rule remain exact.
 * 2. PE20/E10/E21 compose independently in either discovery order, at zero,
 *    one and mixed quantities, with no per-building output rounding.
 * 3. Both farms actually dispatch positive crops before and after history's
 *    month, and native research work can find E21 earlier but not before science.
 * 4. Imported E21 pays the native patent share on actual partial production;
 *    local knowledge/expiry exempt it and later state cannot erase its capture.
 * 5. Current saved choices restore immediately and across the next month;
 *    an exact .181-shaped missing-flag record must be correct or refused.
 * <p>
 * The village factory advances no month (unlike LongPlaytest.probeCity).
 * Every subsequent normal player-month advance is counted from its actual
 * before/after clock, separately from explicit date jumps. Nothing here is
 * an observation waiver: a missing positive dispatch or transition fails.
 */
public final class ModernEngineeringCheck {
    private static final PrintStream OUT = System.out;
    private static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());
    /** Study/root's deliberately rounded abstract result; body21% is disclosed in the data and credits. */
    static final double GM_FACTOR = 1.22;
    /** The exact pre-R6e source-plan base, .181, wrote format58; this is a legacy fixture, not a new format decision. */
    static final int LEGACY_FORMAT = 58;
    private static final List<String> FARMS = List.of("Mixed Farm", "Grain Farm");
    private static int checks, fails;

    private ModernEngineeringCheck() { }

    static void check(String label, boolean ok, Object detail) {
        checks++;
        if (!ok) fails++;
        OUT.println((ok ? "  PASS " : "FAIL ") + label + (ok ? "" : ": " + detail));
    }

    static boolean near(double a, double b) {
        return Double.isFinite(a) && Double.isFinite(b)
                && Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static void near(String label, double actual, double expected) {
        check(label, near(actual, expected), actual + " vs " + expected);
    }

    static int date() { return ResearchCatalog.knownFrom("E21"); }

    static void quietHelpers() {
        if (RecipeCheck.quiet == null) RecipeCheck.quiet = QUIET;
        if (RecipeCheck.out == null) RecipeCheck.out = OUT;
        if (EquipmentCheck.quiet == null) EquipmentCheck.quiet = QUIET;
        if (EquipmentCheck.out == null) EquipmentCheck.out = OUT;
        if (FittingCheck.quiet == null) FittingCheck.quiet = QUIET;
        if (FittingCheck.out == null) FittingCheck.out = OUT;
        if (Forms1900Check.quiet == null) Forms1900Check.quiet = QUIET;
        if (Forms1900Check.out == null) Forms1900Check.out = OUT;
    }

    static final class Fixture {
        final Game game;
        final String label;
        final int factoryMonth;
        int advances, oldActive, crossings, adoptedActive;
        double oldUnits, adoptedUnits;
        boolean previousOldPositive;

        Fixture(String label, boolean historical) {
            this.label = label;
            game = RecipeCheck.town(label, historical);
            factoryMonth = game.getMonth();
            check(label + " factory returns without a simulation advance",
                    factoryMonth == PolicyDates.monthOf(1900, 1), factoryMonth);
            OUT.println("  factory " + label + " returnedMonth=" + factoryMonth + " advances=0 (village, not probeCity)");
        }

        void jump(int month) throws Exception { HistoricalRecipeCheck.at(game, month); }

        void advance(BuildingsTemplate farm) {
            int before = game.getMonth();
            boolean absentBefore = missing(game, farm);
            RecipeCheck.step(game, 1);
            int actual = game.getMonth() - before;
            advances += actual;
            check(label + " player month actually advances once", actual == 1, actual);
            Sector.Output row = game.getSectors().agriculture().output(Good.CROPS);
            double units = row.produced + row.exportBound + row.captive;
            boolean absentNow = missing(game, farm);
            if (units > 0 && absentNow) {
                oldActive++;
                oldUnits += units;
            }
            if (units > 0 && !absentNow) {
                adoptedActive++;
                adoptedUnits += units;
            }
            if (actual == 1 && before < date() && game.getMonth() == date()
                    && absentBefore && !absentNow && previousOldPositive && units > 0) crossings++;
            previousOldPositive = units > 0 && absentNow;
            OUT.println("  firing " + label + " month=" + game.getMonth() + " quantity="
                    + game.getBuildingManager().getStack(farm).getQuantity() + " missingE21=" + absentNow
                    + " cropUnits=" + units + " advances=" + advances);
        }
    }

    static void knowledge(Game game, Set<String> absent) {
        Map<String, Integer> found = new HashMap<>();
        for (ResearchCatalog.Entry e : ResearchCatalog.get().entries())
            if (!absent.contains(e.code())) found.put(e.code(), PolicyDates.monthOf(1900, 1));
        game.getResearch().restore(Map.of(), Map.of(), Map.of(), found);
    }

    static void apply(Game game) { HistoricalRecipeCheck.run(game); }

    static void advanceLoaded(Game game, String label) {
        int before = game.getMonth();
        RecipeCheck.step(game, 1);
        int actual = game.getMonth() - before;
        check(label + " loaded city actually advances once", actual == 1, actual);
        OUT.println("  advance " + label + " before=" + before + " after=" + game.getMonth() + " actualAdvances=" + actual);
    }

    static BuildingsTemplate farm(Game game, String name) {
        return game.getBuildingManager().getTemplateByName(name);
    }

    static void quantity(Game game, BuildingsTemplate t, int wanted) {
        BuildingsStacks stack = game.getBuildingManager().getStack(t);
        if (stack != null && stack.getQuantity() > wanted) stack.removeQuantity(stack.getQuantity() - wanted);
        RecipeCheck.stand(game, t, wanted, new int[1]);
        stack = game.getBuildingManager().getStack(t);
        if (stack != null) HistoricalRecipeCheck.fitted(t, stack);
    }

    static BuildingsTemplate isolate(Fixture fixture, String name, int wanted) {
        Game game = fixture.game;
        for (String other : FARMS) quantity(game, farm(game, other), other.equals(name) ? wanted : 0);
        MaterialsCheck.handWorkers(game);
        return farm(game, name);
    }

    static boolean missing(Game game, BuildingsTemplate t) {
        List<String> chosen = game.getRecipes().forSave().get(t.getId());
        return chosen != null && chosen.stream().anyMatch(id -> List.of(id.split("~")).contains("E21"));
    }

    static Map<String, Double> nonCrop(BuildingsTemplate t) {
        Map<String, Double> figures = HistoricalRecipeCheck.figures(t);
        figures.remove("makes.CROPS");
        return figures;
    }

    static void catalogueAndDefaults() throws Exception {
        OUT.println("--- 1. E21 data, approved source arithmetic and modern/default guard ---");
        ResearchCatalog.Entry e = ResearchCatalog.get().entry("E21");
        check("E21 is the independent 1996 recipe, June placeholder, native work and patent estimate",
                e != null && e.year() == 1996 && e.monthOfYear() == 6 && !e.monthKnown()
                        && e.size() == ResearchCatalog.Size.RECIPE && e.field() == ResearchCatalog.Field.ENGINEERING
                        && e.kinds().equals(List.of("recipe")) && e.needs().isEmpty() && e.fee()
                        && e.work() == Math.round(ResearchCatalog.workByRule(e.size(), e.field(), e.year())), e);
        check("engineering keeps its existing seven-year lag", Research.lagYears(ResearchCatalog.Field.ENGINEERING) == 7, "lag");
        Fixture modern = new Fixture("R6e-modern", false);
        modern.jump(date() - 1);
        knowledge(modern.game, Set.of("E21"));
        Map<Integer, Map<String, Double>> before = new HashMap<>();
        int recipients = 0;
        for (BuildingsTemplate t : modern.game.getBuildingManager().getTemplates()) {
            for (RecipeChange c : t.getRecipeChanges()) if (c.code().equals("E21")) {
                recipients++;
                check(t.getName() + " E21 has only its approved signed CROPS undo",
                        FARMS.contains(t.getName()) && c.piece() == null && c.after() == null
                                && c.makes().keySet().equals(Set.of(Good.CROPS)) && c.uses().isEmpty() && c.jobs().isEmpty()
                                && c.electricity() == 0 && c.gen() == 0 && c.water() == 0 && c.consumables() == 0
                                && c.production() == 0 && c.land() == 0 && c.dig() == 0
                                && c.steamRail() == 0 && c.crackerPetrol() == 0, c);
                double anchor = t.defaultRecipe().makes().get(Good.CROPS);
                near(t.getName() + " E21 undo is anchor/1.22-anchor", c.makes().get(Good.CROPS), anchor / GM_FACTOR - anchor);
                before.put(t.getId(), HistoricalRecipeCheck.figures(t));
            }
        }
        check("only the two released farms carry E21", recipients == FARMS.size(), recipients);
        near("Mixed Farm's immutable modern anchor remains69", farm(modern.game, "Mixed Farm").defaultRecipe().makes().get(Good.CROPS), 69);
        near("Grain Farm's immutable modern anchor remains500", farm(modern.game, "Grain Farm").defaultRecipe().makes().get(Good.CROPS), 500);
        apply(modern.game);
        for (String name : FARMS) {
            BuildingsTemplate t = farm(modern.game, name);
            check(name + " default founding keeps every physical figure before E21's date",
                    before.get(t.getId()).equals(HistoricalRecipeCheck.figures(t)), HistoricalRecipeCheck.figures(t));
        }
        check("default founding saves no historical recipe choices", modern.game.getRecipes().forSave().isEmpty(), modern.game.getRecipes().forSave());
    }

    static void composition() throws Exception {
        OUT.println("--- 2. independent PE20/E10/E21 stages, quantities and discovery order ---");
        for (String name : FARMS) {
            Fixture f = new Fixture("R6e-compose-" + name, true);
            BuildingsTemplate t = isolate(f, name, 1);
            f.jump(date() - 1);
            double anchor = t.defaultRecipe().makes().get(Good.CROPS);
            knowledge(f.game, Set.of());
            apply(f.game);
            Map<String, Double> other = nonCrop(t);
            List<Set<String>> absent = List.of(Set.of("PE20"), Set.of("E10"), Set.of("E21"), Set.of("PE20", "E10", "E21"));
            double[] expected = {anchor / 1.145, anchor / 2, anchor / GM_FACTOR,
                    anchor + (anchor / 1.145 - anchor) - anchor / 2 + (anchor / GM_FACTOR - anchor)};
            for (int i = 0; i < absent.size(); i++) {
                knowledge(f.game, absent.get(i));
                apply(f.game);
                near(name + " fixed-anchor stage " + absent.get(i), t.makes(Good.CROPS), expected[i]);
                check(name + " stage changes no non-crop figure", nonCrop(t).equals(other), absent.get(i));
                check(name + " even all missing leaves positive output", t.makes(Good.CROPS) > 0, t.makes(Good.CROPS));
            }
            for (List<String> order : List.of(List.of("PE20", "E10", "E21"), List.of("E21", "E10", "PE20"))) {
                Set<String> missing = new java.util.HashSet<>(List.of("PE20", "E10", "E21"));
                knowledge(f.game, missing);
                apply(f.game);
                for (String code : order) {
                    missing.remove(code);
                    knowledge(f.game, missing);
                    apply(f.game);
                }
                near(name + " either discovery order recovers the exact modern anchor " + order, t.makes(Good.CROPS), anchor);
            }
            knowledge(f.game, Set.of("E21"));
            quantity(f.game, t, 0);
            apply(f.game);
            near(name + " a zero-quantity template still describes its historical unit", t.makes(Good.CROPS), anchor / GM_FACTOR);
            near(name + " zero farms contribute no crop capacity", f.game.getSectors().agriculture().getCapacity(Good.CROPS), 0);
            quantity(f.game, t, 3);
            apply(f.game);
            near(name + " three buildings retain the unrounded per-unit coefficient", f.game.getSectors().agriculture().getCapacity(Good.CROPS), 3 * anchor / GM_FACTOR);
        }
        Fixture mix = new Fixture("R6e-mixed-quantities", true);
        mix.jump(date() - 1);
        knowledge(mix.game, Set.of("E21"));
        quantity(mix.game, farm(mix.game, "Mixed Farm"), 2);
        quantity(mix.game, farm(mix.game, "Grain Farm"), 3);
        apply(mix.game);
        double plate = 2 * farm(mix.game, "Mixed Farm").defaultRecipe().makes().get(Good.CROPS)
                + 3 * farm(mix.game, "Grain Farm").defaultRecipe().makes().get(Good.CROPS);
        near("mixed standing quantities sum the two historical farm outputs", mix.game.getSectors().agriculture().getCapacity(Good.CROPS), plate / GM_FACTOR);
    }

    static void actualProductionAndFiringCounts() throws Exception {
        OUT.println("--- 3. both isolated farms dispatch across the real player-month boundary ---");
        for (String name : FARMS) {
            Fixture f = new Fixture("R6e-clock-" + name, true);
            BuildingsTemplate t = isolate(f, name, 1);
            f.jump(date() - 2);
            knowledge(f.game, Set.of("E21"));
            apply(f.game);
            Map<String, Double> other = nonCrop(t);
            f.advance(t);
            near(name + " date-1 runs the pre-E21 crop coefficient", t.makes(Good.CROPS), t.defaultRecipe().makes().get(Good.CROPS) / GM_FACTOR);
            check(name + " date-1 really dispatches old crops", f.oldActive == 1 && f.oldUnits > 0, f.oldUnits);
            double dairy = t.makes(Good.DAIRY_EGGS), meat = t.makes(Good.MEAT);
            f.advance(t);
            near(name + " history's month runs the exact modern crop coefficient", t.makes(Good.CROPS), t.defaultRecipe().makes().get(Good.CROPS));
            near(name + " E21 leaves dairy unchanged", t.makes(Good.DAIRY_EGGS), dairy);
            near(name + " E21 leaves meat unchanged", t.makes(Good.MEAT), meat);
            check(name + " actual transition leaves every non-crop figure unchanged", other.equals(nonCrop(t)), nonCrop(t));
            check(name + " counts require positive production on both sides of one actual crossing",
                    f.advances == 2 && f.oldActive == 1 && f.crossings == 1 && f.adoptedActive == 1 && f.adoptedUnits > 0,
                    "advances=" + f.advances + " old=" + f.oldActive + " crossings=" + f.crossings + " adopted=" + f.adoptedActive);
            OUT.println("  totals " + name + " old-active=" + f.oldActive + " crossing=" + f.crossings
                    + " adopted-active=" + f.adoptedActive + " actualAdvances=" + f.advances);
        }
    }

    static void localFind() throws Exception {
        OUT.println("--- 4. native work finds E21 after its science boundary, before history ---");
        ResearchCatalog.Entry e = ResearchCatalog.get().entry("E21");
        int science = date() - Research.lagMonths(e.field());
        long seed = 21;
        for (String name : FARMS) {
            Fixture f = new Fixture("R6e-local-" + name, true);
            BuildingsTemplate t = isolate(f, name, 1);
            knowledge(f.game, Set.of("E21"));
            Map<String, Integer> found = new HashMap<>(f.game.getResearch().foundHere());
            f.game.getResearch().restore(Map.of(), Map.of(), Map.of("E21", Research.hiddenSize(seed, e)), found);
            EnumMap<ResearchCatalog.Field, Double> people = new EnumMap<>(ResearchCatalog.Field.class);
            people.put(e.field(), 1.0);
            f.jump(science - 1);
            f.game.getResearch().month(f.game.getMonth(), seed, people);
            apply(f.game);
            check(name + " no local finding or early GM effect before science exists",
                    f.game.getResearch().foundMonth("E21") == Integer.MAX_VALUE && !f.game.getResearch().isOpen(e, science - 1), "science=" + science);
            near(name + " pre-science farms retain old output", t.makes(Good.CROPS), t.defaultRecipe().makes().get(Good.CROPS) / GM_FACTOR);
            f.jump(science);
            f.game.getResearch().month(science, seed, people);
            apply(f.game);
            check(name + " actual native research work finds E21 at the open boundary",
                    f.game.getResearch().foundThisMonth().contains("E21") && f.game.getResearch().foundMonth("E21") == science && science < date(), f.game.getResearch().foundThisMonth());
            near(name + " a real local find activates its modern crop coefficient", t.makes(Good.CROPS), t.defaultRecipe().makes().get(Good.CROPS));
        }
    }

    static void licenceCapture() throws Exception {
        OUT.println("--- 5. native prices, partial actual production, captured rent and exemptions ---");
        for (String name : FARMS) {
            Fixture f = new Fixture("R6e-fee-" + name, true);
            BuildingsTemplate t = isolate(f, name, 1);
            knowledge(f.game, Set.of("E21"));
            f.jump(date() - 1);
            apply(f.game);
            f.advance(t);
            Sector owner = f.game.getSectors().agriculture();
            double cropPrice = Math.max(Good.CROPS.worldImportPrice(), 2 * owner.getCostPerUnit(Good.CROPS));
            HistoricalRecipeCheck.priceForBothReaders(f.game, Good.CROPS, cropPrice);
            apply(f.game);
            // An explicit half-supplied grid causes partial dispatch, without changing any crop/job premise.
            owner.setEnergyRatio(0.5);
            owner.setStock(Good.CROPS, 0);
            double anchor = t.defaultRecipe().makes().get(Good.CROPS);
            double extra = anchor - anchor / GM_FACTOR;
            double quoteExpected = LicenceFees.SHARE * extra * Recipes.pricesOf(f.game, t).good().applyAsDouble(Good.CROPS) * owner.getOperatingRate();
            near(name + " new-unit rent uses the native patent share and live quote price", LicenceFees.forNew(f.game, t), quoteExpected);
            double before = owner.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0);
            LicenceFees.Production capture = new LicenceFees.Production(f.game);
            owner.produceStock(Good.CROPS, f.game.getMarkets().get(Good.CROPS));
            Sector.Output crops = owner.output(Good.CROPS);
            double made = crops.produced + crops.exportBound + crops.captive;
            check(name + " fee fixture dispatches positive crops below its real capacity",
                    crops.capacity > 0 && made > 0 && made < crops.capacity && owner.getOperatingRate() > 0 && owner.getOperatingRate() < 1,
                    "made=" + made + " plate=" + crops.capacity + " rate=" + owner.getOperatingRate());
            double usedShare = made / crops.capacity;
            SpreadPlanner.Values values = SpreadPlanner.values(f.game.getMarkets());
            double expected = LicenceFees.SHARE * (values.in(Good.CROPS, anchor * usedShare)
                    - values.in(Good.CROPS, anchor / GM_FACTOR * usedShare));
            capture.made(owner, true);
            capture.finish();
            double captured = owner.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0) - before;
            check(name + " imported patent has positive marginal actual value", expected > 0 && LicenceFees.payable("E21", date(), f.game.getResearch()), expected);
            near(name + " actual production capture books only the dispatched crop gain", captured, expected);
            knowledge(f.game, Set.of());
            near(name + " a local find exempts the prospective patent", LicenceFees.forNew(f.game, t), 0);
            near(name + " local knowledge cannot erase the already captured flow",
                    owner.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0) - before, captured);
            knowledge(f.game, Set.of("E21"));
            f.jump(date() + LicenceFees.TERM_MONTHS - 1);
            check(name + " patent remains payable in its last native month", LicenceFees.payable("E21", f.game.getMonth(), f.game.getResearch()), f.game.getMonth());
            f.jump(date() + LicenceFees.TERM_MONTHS);
            near(name + " native twentieth-anniversary boundary exempts rent", LicenceFees.forNew(f.game, t), 0);
            near(name + " expiry cannot erase the production-period liability",
                    owner.pending().otherInputs.getOrDefault(LicenceFees.IP_CHARGES, 0.0) - before, captured);
        }
    }

    static void savedStates() throws Exception {
        OUT.println("--- 6. immediate native load, next month, and the pre-R6e missing-flag record ---");
        for (String name : FARMS) {
            Fixture f = new Fixture("R6e-save-" + name, true);
            BuildingsTemplate t = isolate(f, name, 1);
            knowledge(f.game, Set.of("E21"));
            f.jump(date() - 2);
            apply(f.game);
            f.advance(t);
            double old = t.defaultRecipe().makes().get(Good.CROPS) / GM_FACTOR;
            Map<Integer, List<String>> present = f.game.getRecipes().forSave();
            check(name + " present native choice actually contains the absent E21 flag", missing(f.game, t), present);
            GameFiles.Result saved = RecipeCheck.save10(f.game, "R6e present flags");
            check(name + " current native save writes wholly", saved.whole(), saved.message());

            // Snapshot the same May state, including its history, before either twin
            // advances. Construct the literal pre-R6e choice through native setters;
            // no flag stripping, prefix padding or loader repair is performed.
            SaveWriter.Job job = f.game.prepareSaveForChecks(10, "R6e .181 missing E21");
            DataSave legacy = job.data();
            Map<Integer, List<String>> priorChoices = new HashMap<>(legacy.getRecipeChoices());
            String priorProcess = t.defaultRecipe().getId();
            priorChoices.put(t.getId(), List.of(priorProcess, priorProcess));
            legacy.setRecipeChoices(priorChoices);
            legacy.stamp("0.9.181", LEGACY_FORMAT, legacy.getSavedAt());

            Game loaded = RecipeCheck.load10(f.game);
            BuildingsTemplate restored = farm(loaded, name);
            check(name + " current save loads with exactly the saved choices",
                    loaded.getLoadFailure() == null && loaded.getRecipes().forSave().equals(present), loaded.getLoadFailure());
            near(name + " present flags restore old crops immediately", restored.makes(Good.CROPS), old);
            advanceLoaded(loaded, name + " present flags");
            f.advance(t);
            check(name + " loaded twin really advances to history's month", loaded.getMonth() == date(), loaded.getMonth());
            near(name + " loaded and unbroken next-month crop figures agree", restored.makes(Good.CROPS), t.makes(Good.CROPS));
            check(name + " next-month native saved choices agree", loaded.getRecipes().forSave().equals(f.game.getRecipes().forSave()), loaded.getRecipes().forSave());

            GameFiles.Result written = job.write();
            check(name + " exact .181-shaped record is written by native save APIs", written.whole(), written.message());
            Game earlier = RecipeCheck.load10(f.game);
            if (GameVersion.OLDEST_LOADABLE_FORMAT > LEGACY_FORMAT) {
                check(name + " coordinated format floor refuses the old physical semantics", earlier.getLoadFailure() != null, earlier.getLoadFailure());
            } else {
                check(name + " legacy record loads without an unrelated refusal", earlier.getLoadFailure() == null, earlier.getLoadFailure());
                near(name + " accepted missing-flag record must already run pre-E21 crops immediately", farm(earlier, name).makes(Good.CROPS), old);
                advanceLoaded(earlier, name + " legacy flags");
                check(name + " missing-flag load really advances once", earlier.getMonth() == date(), earlier.getMonth());
                near(name + " next-month selection recovers modern crops after the legacy load",
                        farm(earlier, name).makes(Good.CROPS), t.defaultRecipe().makes().get(Good.CROPS));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        quietHelpers();
        catalogueAndDefaults();
        composition();
        actualProductionAndFiringCounts();
        localFind();
        licenceCapture();
        savedStates();
        OUT.println("ModernEngineeringCheck: " + (fails == 0 ? "all " + checks + " ok" : fails + " FAILED of " + checks));
        System.exit(fails == 0 ? 0 : 1);
    }
}
