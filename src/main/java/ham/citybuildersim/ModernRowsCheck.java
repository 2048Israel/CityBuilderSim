package ham.citybuildersim;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * R6f's three research rows, each undone in a city founded the 1900 way until it is known: robot-assisted
 * surgery's share of general care's stays (M12), the Contact Centre's agents' 14% (B10) and Basel
 * III's conservation buffer (L8), on both sides of their months, in a default city and across a save.
 * <p>
 * WHY (R6f): a row in research.json alone changes nothing a city does. Each of these three needed a figure
 * the model reads by the city's own knowledge (a stays factor, a recipe change, a bank's floor), and B10's
 * undo is a recipe choice whose id is saved: a format-63 save lacked the flag, so a held city would have
 * loaded modern. A city founded the default way knows every row from its first month and must be unchanged.
 * <p>
 * What this proves:
 * 1. M12: the stays factor is Sickness.STAYS_BEFORE_M12 alone the month before July 2000 and 1 from it, in
 *    the unit and in a played 1900 city (recovery too); a default city reads 1; a find ahead of the world
 *    lifts it early.
 * 2. B10: the Contact Centre's NO_DIPLOMA posts are today's plus buildings.json's undo, rounded once, with
 *    every other figure and the output unchanged, before November 2020 and today's from it (by a recipe
 *    call and by a real month); a default city runs today's; a held city's save loads the same posts and
 *    "~B10" id at once, and the next month's choice keeps it.
 * 3. L8: the bank's floor is Bank.CONSERVATION_BUFFER or Bank.BUFFER_BEFORE_L8 by the rule it hears, which
 *    moves its target, its reason and its lending limit; a 1900 city hears it by the month (founding,
 *    each month, a load) and a default city always the buffer.
 * <p>
 * Every city is the existing fixtures': the licence factory's (three months played) or the recipe
 * checks' on GameFiles.scratch, and every save and load is slot 10 of the fixture's own scratch folder.
 * The clock is set by HistoricalRecipeCheck.at(), which the month hears only on the next step.
 */
public final class ModernRowsCheck {
    private static final PrintStream OUT = System.out;
    private static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());
    /** Brynjolfsson, Li and Raymond's 14% more issues resolved an hour, the ratio buildings.json's B10 undo gives the agents before the assistant. */
    static final double AGENTS_GAIN = .14;
    /** The general-care coverages (0, 1] the recovery is read at. */
    static final double[] COVERAGES = {.25, .5, 1};
    private static int checks, fails;

    private ModernRowsCheck() { }

    static void check(String label, boolean ok, Object detail) {
        checks++;
        if (!ok) fails++;
        OUT.println((ok ? "  PASS " : "FAIL ") + label + (ok ? "" : ": " + detail));
    }

    static void near(String label, double actual, double expected, double tolerance) {
        check(label, Double.isFinite(actual) && Double.isFinite(expected) && Math.abs(actual - expected) <= tolerance,
                actual + " vs " + expected);
    }

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

    /** One real month of the player's button, said by what it did. */
    static void step(Game g, String label) {
        int before = g.getMonth();
        RecipeCheck.step(g, 1);
        check(label + " actually advances one month", g.getMonth() == before + 1, before + " -> " + g.getMonth());
    }

    /* ----- 1. M12 ----- */

    static void robotSurgery() throws Exception {
        OUT.println("--- 1. M12: general care's stays are longer until robot-assisted surgery is known ---");
        int w = ResearchCatalog.knownFrom("M12");
        check("M12 is known from July 2000 (PolicyDates.monthOf(2000, 7))", w == PolicyDates.monthOf(2000, 7), w);
        check("Sickness.STAYS_BEFORE_M12 is a share of today's part under 1, and M12 is one of Sickness.STAYS_ROWS",
                Sickness.STAYS_BEFORE_M12 > 0 && Sickness.STAYS_BEFORE_M12 < 1 && Sickness.STAYS_ROWS.contains("M12"),
                Sickness.STAYS_BEFORE_M12 + " " + Sickness.STAYS_ROWS);

        MedicalRows rows = MedicalRows.of(new Research(), true);
        check("a 1900 city's rows know M12 from history's month and not the month before",
                rows.known("M12", w) && !rows.known("M12", w - 1), w);
        List<String> others = new ArrayList<>(Sickness.STAYS_ROWS);
        others.remove("M12");
        boolean allKnown = !others.isEmpty();
        for (String code : others) allKnown &= rows.known(code, w - 1);
        check("every other row in Sickness.STAYS_ROWS is known the month before M12, so the factor then is M12's alone", allKnown, others);
        near("the stays factor the month before M12 is Sickness.STAYS_BEFORE_M12", Sickness.staysFactor(rows, w - 1), Sickness.STAYS_BEFORE_M12, 1e-12);
        near("...and is 1 from M12's month", Sickness.staysFactor(rows, w), 1, 1e-12);
        near("a city founded the default way (MedicalRows.TODAY) reads 1 the month before M12", Sickness.staysFactor(MedicalRows.TODAY, w - 1), 1, 1e-12);
        for (double c : COVERAGES) check("at coverage " + c + " the recovery is lower before M12 than after it",
                Sickness.recovery(c, Sickness.STAYS_BEFORE_M12) < Sickness.recovery(c, 1),
                Sickness.recovery(c, Sickness.STAYS_BEFORE_M12) + " vs " + Sickness.recovery(c, 1));

        // A city that found M12 a year ahead of the world knows it from its own month, whatever history's.
        int early = w - 12;
        Research found = new Research();
        found.restore(Map.of(), Map.of(), Map.of(), Map.of("M12", early));
        MedicalRows ahead = MedicalRows.of(found, true);
        check("a research that found M12 a year early knows it then and not the month before, though history's month is later",
                ahead.known("M12", early) && !ahead.known("M12", early - 1) && early < w && ahead.known("M12", early + 1), early);
        near("...so its stays factor is STAYS_BEFORE_M12 the month before the find", Sickness.staysFactor(ahead, early - 1), Sickness.STAYS_BEFORE_M12, 1e-12);
        near("...and 1 from the find, before history's month", Sickness.staysFactor(ahead, early), 1, 1e-12);

        Game old = HistoricalLicenceCheck.town("R6f-m12-1900", true);
        check("fixture: a city founded the 1900 way that has not found M12", old.foundedUnfitted() && !old.getResearch().foundHere("M12"), "");
        HistoricalRecipeCheck.at(old, w - 2);
        for (int i = 0; i < 3; i++) {
            step(old, "1900 city, month " + (w - 2 + i));
            int month = old.getMonth();
            double expected = month < w ? Sickness.STAYS_BEFORE_M12 : 1;
            String when = "1900 city, month " + (month - w) + " from M12's: ";
            near(when + "getStaysFactor() is " + (month < w ? "Sickness.STAYS_BEFORE_M12" : "1"), old.getSickness().getStaysFactor(), expected, 1e-12);
            for (double c : COVERAGES) near(when + "recoveryAt(" + c + ") is Sickness.recovery(c, " + (month < w ? "STAYS_BEFORE_M12" : "1") + ")",
                    old.getSickness().recoveryAt(c), Sickness.recovery(c, expected), 1e-12);
        }

        Game today = HistoricalLicenceCheck.town("R6f-m12-default", false);
        check("fixture: a city founded the default way", !today.foundedUnfitted(), "");
        HistoricalRecipeCheck.at(today, w - 2);
        for (int i = 0; i < 3; i++) {
            step(today, "default city, month " + (w - 2 + i));
            near("default city, month " + (today.getMonth() - w) + " from M12's: getStaysFactor() is 1", today.getSickness().getStaysFactor(), 1, 1e-12);
            near("...and recoveryAt(1) is Sickness.recovery(1, 1)", today.getSickness().recoveryAt(1), Sickness.recovery(1, 1), 1e-12);
        }

        Game finder = HistoricalLicenceCheck.town("R6f-m12-early", true);
        finder.getResearch().restore(Map.of(), Map.of(), Map.of(), Map.of("M12", early));
        HistoricalRecipeCheck.at(finder, early - 2);
        step(finder, "1900 city that found M12 a year early, month before the find");
        near("...it still reads Sickness.STAYS_BEFORE_M12 the month before its own find", finder.getSickness().getStaysFactor(), Sickness.STAYS_BEFORE_M12, 1e-12);
        step(finder, "1900 city that found M12 a year early, month of the find");
        check("...history's month is still ahead of it", finder.getMonth() < w, finder.getMonth());
        near("...and reads 1 from its own find, before history's month", finder.getSickness().getStaysFactor(), 1, 1e-12);
    }

    /* ----- 2. B10 ----- */

    /** The Contact Centre's definition in buildings.json, read as the file has it. */
    static JsonObject contactCentre() throws Exception {
        JsonObject data = JsonParser.parseString(Files.readString(Path.of("src/main/resources/buildings.json"), StandardCharsets.UTF_8)).getAsJsonObject();
        for (JsonElement b : data.getAsJsonArray("buildings"))
            if (b.getAsJsonObject().get("name").getAsString().equals("Contact Centre")) return b.getAsJsonObject();
        throw new IllegalStateException("no Contact Centre in buildings.json");
    }

    static JsonObject b10(JsonObject centre) {
        for (JsonElement c : centre.getAsJsonArray("recipeChanges"))
            if (c.getAsJsonObject().get("entry").getAsString().equals("B10")) return c.getAsJsonObject();
        throw new IllegalStateException("no B10 change on the Contact Centre");
    }

    /** Whether the template's saved recipe choice carries B10's missing flag (the "~B10" of its id). */
    static boolean missing(Game g, BuildingsTemplate t) {
        List<String> chosen = g.getRecipes().forSave().get(t.getId());
        return chosen != null && chosen.stream().anyMatch(id -> List.of(id.split("~")).contains("B10"));
    }

    /** A Contact Centre stood in the city at the month, its choice struck and applied, B10 the only discovery unknown when `heldBack`. */
    static BuildingsTemplate centreAt(Game g, int month, boolean heldBack) throws Exception {
        if (heldBack) HistoricalRecipeCheck.allExcept(g, "B10");
        HistoricalRecipeCheck.at(g, month);
        BuildingsTemplate t = HistoricalLicenceCheck.stand(g, "Contact Centre", 1);
        HistoricalRecipeCheck.run(g);
        return t;
    }

    static Map<String, Double> restOf(BuildingsTemplate t) {
        Map<String, Double> figures = HistoricalRecipeCheck.figures(t);
        figures.remove("jobs." + JobType.NO_DIPLOMA.name());
        return figures;
    }

    static void agents() throws Exception {
        OUT.println("--- 2. B10: the Contact Centre's agents are more posts until the AI assistant is known ---");
        int w = ResearchCatalog.knownFrom("B10");
        check("B10 is known from November 2020 (PolicyDates.monthOf(2020, 11))", w == PolicyDates.monthOf(2020, 11), w);
        JsonObject centre = contactCentre();
        JsonObject change = b10(centre);
        JsonObject without = change.getAsJsonObject("without");
        double extra = without.getAsJsonObject("jobs").get("NO_DIPLOMA").getAsDouble();
        double base = centre.getAsJsonObject("jobs").get("NO_DIPLOMA").getAsDouble();
        check("buildings.json's B10 undo on the Contact Centre changes the NO_DIPLOMA posts and nothing else",
                without.keySet().equals(Set.of("jobs")) && without.getAsJsonObject("jobs").keySet().equals(Set.of("NO_DIPLOMA")), without);

        // Today's anchor, from a default city's own template at the month before B10.
        Game today = HistoricalLicenceCheck.town("R6f-b10-default", false);
        BuildingsTemplate modern = centreAt(today, w - 1, false);
        int anchor = modern.getJobs(JobType.NO_DIPLOMA);
        check("a default city's Contact Centre stands, and its NO_DIPLOMA posts are buildings.json's own at the month before B10",
                today.getBuildingManager().getStack(modern).getQuantity() == 1 && anchor == base, anchor + " vs " + base);
        check("...with no B10 flag on its choice", !missing(today, modern), today.getRecipes().forSave());
        near("buildings.json's NO_DIPLOMA undo over that anchor is the source's AGENTS_GAIN", extra / anchor, AGENTS_GAIN, 1e-3);
        long before = Math.round(anchor * (1 + AGENTS_GAIN));
        check("fixture: the source's 14% of the anchor, rounded once, is the anchor and buildings.json's undo, rounded once, and more than the anchor",
                before > anchor && before == Math.round(anchor + extra), before + " vs " + anchor + " + " + extra);
        Map<String, Double> today1 = restOf(modern);
        double output = modern.makes(Good.SUPPORT_WORK);

        // The month before B10, in a 1900 city.
        Game old = HistoricalLicenceCheck.town("R6f-b10-1900", true);
        check("fixture: a city founded the 1900 way", old.foundedUnfitted(), "");
        BuildingsTemplate t = centreAt(old, w - 1, true);
        check("fixture: one Contact Centre stands in it, every discovery but B10 known",
                old.getBuildingManager().getStack(t).getQuantity() == 1 && !old.getResearch().known("B10", w - 1)
                        && old.getResearch().known("B1", w - 1), old.getBuildingManager().getStack(t).getQuantity());
        check("a 1900 Contact Centre the month before B10 has NO_DIPLOMA posts of Math.round(anchor + undo)",
                t.getJobs(JobType.NO_DIPLOMA) == before, t.getJobs(JobType.NO_DIPLOMA) + " vs " + before);
        check("...every other figure of it (other posts, power, water, land) is the default city's",
                restOf(t).equals(today1), restOf(t) + " vs " + today1);
        near("...and its output (Good.SUPPORT_WORK) is the default city's", t.makes(Good.SUPPORT_WORK), output, 1e-9);
        check("...and its running choice carries the missing-B10 flag, \"~B10\"", missing(old, t), old.getRecipes().forSave());
        check("...as the saved choice, which names it too",
                old.getRecipes().forSave().get(t.getId()) != null && old.getRecipes().forSave().get(t.getId()).stream().anyMatch(id -> id.contains("~B10")),
                old.getRecipes().forSave());

        // From B10's month, by the recipe call...
        HistoricalRecipeCheck.at(old, w);
        HistoricalRecipeCheck.run(old);
        check("a 1900 Contact Centre in B10's month has today's NO_DIPLOMA posts, the anchor",
                t.getJobs(JobType.NO_DIPLOMA) == anchor, t.getJobs(JobType.NO_DIPLOMA) + " vs " + anchor);
        check("...every other figure still the default city's and no \"~B10\" on its choice",
                restOf(t).equals(today1) && !missing(old, t), old.getRecipes().forSave());

        // ...and by a real month.
        Game stepped = HistoricalLicenceCheck.town("R6f-b10-month", true);
        BuildingsTemplate s = centreAt(stepped, w - 1, true);
        check("fixture: the month before B10 the stepped city runs the undo", s.getJobs(JobType.NO_DIPLOMA) == before && missing(stepped, s), s.getJobs(JobType.NO_DIPLOMA));
        step(stepped, "the 1900 city across B10's month");
        check("...and the real month into B10's month runs today's posts, the anchor, with no \"~B10\"",
                stepped.getMonth() == w && s.getJobs(JobType.NO_DIPLOMA) == anchor && !missing(stepped, s),
                stepped.getMonth() + " " + s.getJobs(JobType.NO_DIPLOMA) + " " + stepped.getRecipes().forSave());

        // A default city's Contact Centre is the anchor across B10's month.
        HistoricalRecipeCheck.at(today, w - 1);
        step(today, "the default city across B10's month");
        check("a default city's Contact Centre keeps the anchor posts and no \"~B10\" through B10's month",
                modern.getJobs(JobType.NO_DIPLOMA) == anchor && !missing(today, modern) && restOf(modern).equals(today1),
                modern.getJobs(JobType.NO_DIPLOMA) + " " + today.getRecipes().forSave());

        // A city held before B10, saved and loaded.
        Game held = HistoricalLicenceCheck.town("R6f-b10-held", true);
        BuildingsTemplate h = centreAt(held, w - 2, true);
        check("fixture: the held city runs the undo", h.getJobs(JobType.NO_DIPLOMA) == before && missing(held, h), h.getJobs(JobType.NO_DIPLOMA));
        GameFiles.Result saved = RecipeCheck.save10(held, "R6f B10 held");
        check("the held city's save writes wholly", saved.whole(), saved.message());
        Game loaded = RecipeCheck.load10(held);
        check("the held city loads", loaded.getLoadFailure() == null && loaded.getMonth() == w - 2, loaded.getLoadFailure());
        BuildingsTemplate l = loaded.getBuildingManager().getTemplateByName("Contact Centre");
        check("the loaded city's NO_DIPLOMA posts are Math.round(anchor + undo) at once, before any month (the saved flag, format 64)",
                l.getJobs(JobType.NO_DIPLOMA) == before, l.getJobs(JobType.NO_DIPLOMA) + " vs " + before);
        check("...with the \"~B10\" id the save held, and every other figure the default city's",
                missing(loaded, l) && loaded.getRecipes().forSave().equals(held.getRecipes().forSave()) && restOf(l).equals(today1),
                loaded.getRecipes().forSave());
        step(loaded, "the loaded city");
        check("...and the next month's choice, still before B10, keeps the posts and the id",
                loaded.getMonth() == w - 1 && l.getJobs(JobType.NO_DIPLOMA) == before && missing(loaded, l),
                loaded.getMonth() + " " + l.getJobs(JobType.NO_DIPLOMA));
        step(loaded, "the loaded city into B10's month");
        check("...and B10's month drops both: the anchor posts, no \"~B10\"",
                loaded.getMonth() == w && l.getJobs(JobType.NO_DIPLOMA) == anchor && !missing(loaded, l),
                loaded.getMonth() + " " + l.getJobs(JobType.NO_DIPLOMA));
    }

    /* ----- 3. L8 ----- */

    static void basel3() throws Exception {
        OUT.println("--- 3. L8: a bank holds no buffer over the minimum but its worst year until Basel III is the rule ---");
        int w = ResearchCatalog.knownFrom("L8");
        check("L8 is known from January 2013 (PolicyDates.monthOf(2013, 1))", w == PolicyDates.monthOf(2013, 1), w);
        check("fixture: Bank.BUFFER_BEFORE_L8 is under Bank.CONSERVATION_BUFFER, the rule a tightening",
                Bank.BUFFER_BEFORE_L8 < Bank.CONSERVATION_BUFFER, Bank.BUFFER_BEFORE_L8);

        Bank bank = new Bank();
        check("a bare bank requires the buffer, its floor Bank.CONSERVATION_BUFFER",
                bank.conservationBufferRequired() && bank.bufferFloor() == Bank.CONSERVATION_BUFFER, bank.bufferFloor());
        near("...so its capital target is Bank.CAPITAL_RATIO plus Bank.CONSERVATION_BUFFER",
                bank.capitalTarget(), Bank.CAPITAL_RATIO + Math.max(Bank.CONSERVATION_BUFFER, bank.getWorstLossRate()), 1e-12);
        String reason = bank.targetReason();
        bank.hearCapitalRule(false);
        check("hearing that Basel III is not the rule drops the floor to Bank.BUFFER_BEFORE_L8",
                !bank.conservationBufferRequired() && bank.bufferFloor() == Bank.BUFFER_BEFORE_L8, bank.bufferFloor());
        near("...a fresh bank's buffer is then the larger of Bank.BUFFER_BEFORE_L8 and its worst year",
                bank.capitalBuffer(), Math.max(Bank.BUFFER_BEFORE_L8, bank.getWorstLossRate()), 1e-12);
        near("...and its capital target is Bank.CAPITAL_RATIO plus that",
                bank.capitalTarget(), Bank.CAPITAL_RATIO + Math.max(Bank.BUFFER_BEFORE_L8, bank.getWorstLossRate()), 1e-12);
        check("...and its reason names Basel III, which the rule's own reason does not say",
                bank.targetReason().contains("Basel III") && !bank.targetReason().equals(reason), bank.targetReason());
        bank.hearCapitalRule(true);
        check("hearing Basel III again restores the floor Bank.CONSERVATION_BUFFER and the target Bank.CAPITAL_RATIO plus it",
                bank.conservationBufferRequired() && bank.bufferFloor() == Bank.CONSERVATION_BUFFER
                        && Math.abs(bank.capitalTarget() - (Bank.CAPITAL_RATIO + Math.max(Bank.CONSERVATION_BUFFER, bank.getWorstLossRate()))) < 1e-12
                        && bank.targetReason().equals(reason), bank.capitalTarget());

        // What the floor does to lending: a bank inside the conservation buffer is rationed by it, not before it.
        double book = 1_000_000;
        double ratio = Bank.CAPITAL_RATIO + Bank.CONSERVATION_BUFFER / 2;
        Bank lender = BankCheck.lentOut(ratio * book, book);
        near("fixture: a bank halfway into the conservation buffer", lender.capitalRatio(), ratio, 1e-12);
        check("fixture: its ratio is over the minimum plus Bank.BUFFER_BEFORE_L8", ratio >= Bank.CAPITAL_RATIO + Bank.BUFFER_BEFORE_L8, ratio);
        lender.hearCapitalRule(false);
        check("before Basel III that bank is at its target, and its lendingGrowthLimit() is unlimited",
                Double.isInfinite(lender.lendingGrowthLimit()), lender.lendingGrowthLimit());
        lender.hearCapitalRule(true);
        check("...with the conservation buffer it is under its target, and the limit is finite",
                Double.isFinite(lender.lendingGrowthLimit()) && lender.lendingGrowthLimit() > 0, lender.lendingGrowthLimit());
        near("...Bank.RATIONED_GROWTH, halfway between the minimum and the target", lender.lendingGrowthLimit(), Bank.RATIONED_GROWTH, 1e-12);

        // A 1900 city hears it by the month.
        Game old = HistoricalLicenceCheck.town("R6f-l8-1900", true);
        check("fixture: a city founded the 1900 way that has not found L8", old.foundedUnfitted() && !old.getResearch().foundHere("L8"), "");
        check("at its founding the 1900 city's bank is before Basel III", !old.getBank().conservationBufferRequired() && old.getMonth() < w,
                old.getMonth() + " " + old.getBank().conservationBufferRequired());
        HistoricalRecipeCheck.at(old, w - 2);
        double[] target = new double[3];
        for (int i = 0; i < 3; i++) {
            step(old, "1900 city, month " + (w - 2 + i));
            Bank b = old.getBank();
            boolean required = old.getMonth() >= w;
            double floor = required ? Bank.CONSERVATION_BUFFER : Bank.BUFFER_BEFORE_L8;
            String when = "1900 city, month " + (old.getMonth() - w) + " from L8's: ";
            check(when + "conservationBufferRequired() is " + required, b.conservationBufferRequired() == required, b.conservationBufferRequired());
            check(when + "bufferFloor() is " + (required ? "Bank.CONSERVATION_BUFFER" : "Bank.BUFFER_BEFORE_L8"), b.bufferFloor() == floor, b.bufferFloor());
            check("fixture: " + when + "its worst loss rate is under Bank.CONSERVATION_BUFFER", b.getWorstLossRate() < Bank.CONSERVATION_BUFFER, b.getWorstLossRate());
            near(when + "capitalTarget() is Bank.CAPITAL_RATIO plus the larger of the floor and its worst year",
                    b.capitalTarget(), Bank.CAPITAL_RATIO + Math.max(floor, b.getWorstLossRate()), 1e-12);
            target[i] = b.capitalTarget();
        }
        check("the 1900 city's capital target rises in L8's month, to Bank.CAPITAL_RATIO plus Bank.CONSERVATION_BUFFER, and holds there",
                target[1] > target[0] && Math.abs(target[1] - (Bank.CAPITAL_RATIO + Bank.CONSERVATION_BUFFER)) < 1e-12
                        && Math.abs(target[2] - target[1]) < 1e-12, target[0] + " " + target[1] + " " + target[2]);

        // A default city has the buffer throughout.
        Game today = HistoricalLicenceCheck.town("R6f-l8-default", false);
        check("fixture: a city founded the default way, its bank requiring the buffer at its founding",
                !today.foundedUnfitted() && today.getBank().conservationBufferRequired(), "");
        HistoricalRecipeCheck.at(today, w - 2);
        for (int i = 0; i < 3; i++) {
            step(today, "default city, month " + (w - 2 + i));
            Bank b = today.getBank();
            check("default city, month " + (today.getMonth() - w) + " from L8's: the buffer is required, its floor Bank.CONSERVATION_BUFFER",
                    b.conservationBufferRequired() && b.bufferFloor() == Bank.CONSERVATION_BUFFER, b.bufferFloor());
        }

        // A load hears it again, from the city it loads.
        Game held = HistoricalLicenceCheck.town("R6f-l8-held", true);
        HistoricalRecipeCheck.at(held, w - 3);
        RecipeCheck.step(held, 1);
        check("fixture: the held 1900 city is two months before Basel III", !held.getBank().conservationBufferRequired() && held.getMonth() < w, held.getMonth());
        GameFiles.Result saved = RecipeCheck.save10(held, "R6f L8 held");
        check("the held city's save writes wholly", saved.whole(), saved.message());
        Game loaded = RecipeCheck.load10(held);
        check("the held city loads and its bank is before Basel III at once, before any month, floor Bank.BUFFER_BEFORE_L8",
                loaded.getLoadFailure() == null && loaded.getMonth() == held.getMonth()
                        && !loaded.getBank().conservationBufferRequired() && loaded.getBank().bufferFloor() == Bank.BUFFER_BEFORE_L8,
                loaded.getLoadFailure() + " " + loaded.getMonth());
        step(loaded, "the loaded 1900 city");
        check("...and the loaded city hears Basel III by the month: still before it one month on", loaded.getMonth() < w && !loaded.getBank().conservationBufferRequired(), loaded.getMonth());
        step(loaded, "the loaded 1900 city into L8's month");
        check("...and requires it in L8's month", loaded.getMonth() == w && loaded.getBank().conservationBufferRequired()
                && loaded.getBank().bufferFloor() == Bank.CONSERVATION_BUFFER, loaded.getMonth());

        Game kept = HistoricalLicenceCheck.town("R6f-l8-kept", false);
        HistoricalRecipeCheck.at(kept, w - 2);
        RecipeCheck.step(kept, 1);
        GameFiles.Result keptSave = RecipeCheck.save10(kept, "R6f L8 default");
        check("the default city's save writes wholly", keptSave.whole(), keptSave.message());
        Game keptLoaded = RecipeCheck.load10(kept);
        check("the default city loads, and its bank requires the buffer at once though the month is before L8's",
                keptLoaded.getLoadFailure() == null && keptLoaded.getMonth() < w && keptLoaded.getBank().conservationBufferRequired()
                        && keptLoaded.getBank().bufferFloor() == Bank.CONSERVATION_BUFFER,
                keptLoaded.getLoadFailure() + " " + keptLoaded.getMonth());
    }

    public static void main(String[] args) throws Exception {
        quietHelpers();
        robotSurgery();
        agents();
        basel3();
        OUT.println("ModernRowsCheck: " + (fails == 0 ? "all " + checks + " ok" : fails + " FAILED of " + checks));
        System.exit(fails == 0 ? 0 : 1);
    }
}
