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
 * R6f's three research rows and R6e-rest's one, each undone in a city founded the 1900 way until it is known:
 * robot-assisted surgery's share of general care's stays (M12), the Contact Centre's agents' 14% (B10), Basel
 * III's conservation buffer (L8) and the Desalination Plant's electricity before the pressure exchanger (E22),
 * on both sides of their months, in a default city and across a save.
 * <p>
 * WHY (R6f): a row in research.json alone changes nothing a city does. Each of these three needed a figure
 * the model reads by the city's own knowledge (a stays factor, a recipe change, a bank's floor), and B10's
 * undo is a recipe choice whose id is saved: a format-63 save lacked the flag, so a held city would have
 * loaded modern. A city founded the default way knows every row from its first month and must be unchanged.
 * WHY (R6e-rest): E22 is the same kind of undo on a figure the water's books read, and a recipe change that
 * moved a template's electricity without moving the sea works' draw would charge a held city for power the
 * plant no longer draws (or not charge it for what it does); the draw is read through the model's own getters.
 * <p>
 * What this proves:
 * 1. M12: the stays factor is Sickness.STAYS_BEFORE_M12 alone the month before July 2000 and 1 from it, in
 *    the unit and in a played 1900 city (recovery too); a default city reads 1; a find ahead of the world
 *    lifts it early; a 1900 city held before M12 and saved loads the medicine it ran (the stays factor, the
 *    long recovery, the death lift and the recovery at every coverage) at once, before any month, since
 *    the medicine is derived and not saved (REV-R6f P2); a city whose Medical School's faculty complete M12
 *    in a played month ahead of history runs the stays factor 1 in that same month, not the next, and a
 *    reload of that month reads the same; and a 1900 city played before M1 and M2 are known, whose general
 *    care covers part of its people, loads the long recovery and the death lift (a figure times that
 *    coverage) it ran at once, which a month with both known cannot tell (they are 1 and 0 there; REV-R6f P2).
 * 2. B10: the Contact Centre's NO_DIPLOMA posts are today's plus buildings.json's undo, rounded once, with
 *    every other figure and the output unchanged, before November 2020 and today's from it (by a recipe
 *    call and by a real month); a default city runs today's; a held city's save loads the same posts and
 *    "~B10" id at once, and the next month's choice keeps it.
 * 3. L8: the bank's floor is Bank.CONSERVATION_BUFFER or Bank.BUFFER_BEFORE_L8 by the rule it hears, which
 *    moves its target, its reason and its lending limit; a 1900 city hears it by the month (founding,
 *    each month, a load) and a default city always the buffer; a city whose Law School's faculty complete L8
 *    in a played month ahead of history hears it in that same month, not the next, and a reload of that
 *    month hears the same (REV-R6f P1).
 * 4. E22: the catalogue's row (1997, June placeholder, equipment, needs E6, fee, the rule's work) and buildings.json's
 *    one undo, carried by the Desalination Plant's pressure exchanger, on its electricity alone and the sourced
 *    arithmetic's kW (Stover 2010's 3.49 to 2.68 kWh a m3 over the DOE's 2.70 kWh a m3 RO step); a 1900 city's plant
 *    that finishes before June 1997 is recorded as lacking the exchanger (the stack's unfitted count) and runs
 *    Math.round(10,880 + undo) = 13,454 kW with every other figure today's, and still does in June 1997 (knowing a
 *    piece fits none, by a recipe call and by a real month); a plant that finishes after it, where the exchanger pays,
 *    comes fitted at 10,880; the sea works' draw the water's books pay is the plants' count times the template's
 *    powerBought() after a real month, more than today's while a plant lacks the exchanger; no sector fits a city
 *    work's piece, and the player's Fit (Game.fitForCity()) does, after which the plant and the draw are today's; the
 *    imported exchanger's public gain inside its term is the power it saves at the commerce price, and the plant's licence
 *    bill SHARE of it (it serves no more water; LicenceFees.entryGain() adds the saving); a
 *    default city runs 10,880 across the month; a held city's save loads the unfitted count and the draw at once and
 *    the next month keeps them; a plant with no record of the count (a format-65 save's) is read as fitted from June 1997.
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

        // A held city, saved and loaded: the medicine is derived from the finds, the month and the coverage and is not saved, so the load must work it out again.
        Game held = HistoricalLicenceCheck.town("R6f-m12-held", true);
        HistoricalRecipeCheck.at(held, w - 3);
        step(held, "held 1900 city before M12");
        Sickness live = held.getSickness();
        boolean othersKnown = true;
        for (String code : others) othersKnown &= held.getResearch().known(code, held.getMonth());
        check("fixture: the held city is before M12 with M12 not found and every other stays row known",
                held.getMonth() < w && !held.getResearch().foundHere("M12") && !held.getResearch().known("M12", held.getMonth()) && othersKnown,
                held.getMonth());
        near("fixture: the held city's live stays factor, after a real month, is Sickness.STAYS_BEFORE_M12", live.getStaysFactor(), Sickness.STAYS_BEFORE_M12, 1e-12);
        GameFiles.Result saved = RecipeCheck.save10(held, "R6f M12 held");
        check("the held city's save writes wholly", saved.whole(), saved.message());
        Game loaded = RecipeCheck.load10(held);
        check("the held city loads in the month it was saved", loaded.getLoadFailure() == null && loaded.getMonth() == held.getMonth(),
                loaded.getLoadFailure() + " " + loaded.getMonth());
        Sickness back = loaded.getSickness();
        near("the loaded city's stays factor is Sickness.STAYS_BEFORE_M12 at once, before any month", back.getStaysFactor(), Sickness.STAYS_BEFORE_M12, 1e-12);
        near("...and is the live city's", back.getStaysFactor(), live.getStaysFactor(), 1e-12);
        near("...its long recovery is the live city's", back.getLongRecovery(), live.getLongRecovery(), 1e-12);
        near("...its death lift is the live city's", back.getDeathLift(), live.getDeathLift(), 1e-12);
        for (double c : COVERAGES) near("...recoveryAt(" + c + ") is the live city's", back.recoveryAt(c), live.recoveryAt(c), 1e-12);
        near("...and servedRecovery() is the live city's", back.servedRecovery(), live.servedRecovery(), 1e-12);
        step(loaded, "the loaded city");
        near("...and the month after the load it still reads Sickness.STAYS_BEFORE_M12", loaded.getSickness().getStaysFactor(), Sickness.STAYS_BEFORE_M12, 1e-12);

        medicineBeforeM1AndM2();

        // A find in a played month: the Medical School's faculty complete M12 two years before history's month, and the sickness runs the new medicine in that month (REV-R6f P2).
        Game finds = HistoricalLicenceCheck.town("R6f-m12-find", true);
        HistoricalRecipeCheck.allExcept(finds, "M12");
        int lead = w - 24;
        // The fixture's medicine was last told in its first months, before allExcept(): one month played with no school lets the sickness hear the finds it now has.
        HistoricalRecipeCheck.at(finds, lead - 1);
        step(finds, "the 1900 city, every discovery but M12 found, with no school yet");
        check("fixture: that month is the one two years before history's month", finds.getMonth() == lead, finds.getMonth());
        HistoricalLicenceCheck.stand(finds, "Medical School", 1);
        ResearchCatalog.Entry m12 = ResearchCatalog.get().entry("M12");
        DiscoveryCheck.pileJustUnder(finds, m12);
        Research seeking = finds.getResearch();
        check("fixture: a city founded the 1900 way, every discovery but M12 found, M12 open to the medical field two years before history's month with its pile just under its size",
                finds.foundedUnfitted() && !seeking.foundHere("M12") && seeking.openProblems(ResearchCatalog.Field.MEDICINE, lead + 1).contains(m12)
                        && seeking.piles().containsKey("M12") && seeking.piles().get("M12") < Research.hiddenSize(finds.getWorldSeed(), m12),
                seeking.piles());
        near("fixture: its live stays factor before the month is Sickness.STAYS_BEFORE_M12", finds.getSickness().getStaysFactor(), Sickness.STAYS_BEFORE_M12, 1e-12);
        step(finds, "the 1900 city whose faculty complete M12");
        int playedM12 = finds.getMonth();
        check("fixture: the Medical School's faculty worked in medicine that month and M12 was found in it, before history's month",
                seeking.researchers(ResearchCatalog.Field.MEDICINE) > 0 && seeking.foundThisMonth().contains("M12") && seeking.foundMonth("M12") == playedM12 && playedM12 < w,
                seeking.researchers(ResearchCatalog.Field.MEDICINE) + " " + seeking.foundHere() + " " + playedM12);
        Sickness liveFinds = finds.getSickness();
        near("in that same month the live stays factor is 1, before the next month is heard", liveFinds.getStaysFactor(), 1, 1e-12);
        GameFiles.Result findSave = RecipeCheck.save10(finds, "R6f M12 found");
        check("the finder's save writes wholly", findSave.whole(), findSave.message());
        Game findLoaded = RecipeCheck.load10(finds);
        check("the finder loads in the month of the find, with M12 found in it",
                findLoaded.getLoadFailure() == null && findLoaded.getMonth() == playedM12 && findLoaded.getResearch().foundMonth("M12") == playedM12,
                findLoaded.getLoadFailure() + " " + findLoaded.getMonth());
        near("the loaded city's stays factor is the live city's", findLoaded.getSickness().getStaysFactor(), liveFinds.getStaysFactor(), 1e-12);
        near("...and is 1", findLoaded.getSickness().getStaysFactor(), 1, 1e-12);
    }

    /**
     * The M12 group's month has M1 and M2 known, where the long recovery is 1 and the death lift 0, the fresh defaults, so it cannot tell a load that
     * derives them from one that does not. This is a 1900 city played before both, with general care covering part of its people: the death lift is
     * then a figure times that coverage, so a load must restore the coverage the month noted before it derives the medicine from it (REV-R6f P2).
     */
    static void medicineBeforeM1AndM2() throws Exception {
        Game care = HistoricalLicenceCheck.town("R6f-m1m2-held", true);
        step(care, "held 1900 city, early, before M1 and M2");
        int month = care.getMonth();
        int m1 = ResearchCatalog.knownFrom("M1"), m2 = ResearchCatalog.knownFrom("M2");
        Research research = care.getResearch();
        check("fixture: a city founded the 1900 way, played to a month before history's M1 and M2, with neither found nor known",
                care.foundedUnfitted() && month < m1 && month < m2 && !research.foundHere("M1") && !research.foundHere("M2")
                        && !research.known("M1", month) && !research.known("M2", month),
                month + " " + m1 + " " + m2);
        double stays = 1;
        boolean noneKnown = true;
        for (String code : Sickness.STAYS_ROWS) {
            noneKnown &= !research.known(code, month);
            stays *= Sickness.staysBefore(code);
        }
        check("fixture: no row that shortens general care's stays is known that month either, so its stays factor is the product of what each leaves, under 1",
                noneKnown && stays < 1, Sickness.STAYS_ROWS + " " + stays);
        double coverage = care.getHealthcare().getCoverage(CareType.GENERAL);
        check("fixture: the month noted a general care coverage strictly between 0 and 1, neither none nor the default 1", coverage > 0 && coverage < 1, coverage);
        Sickness live = care.getSickness();
        near("the live city's stays factor is the product of every stays row's share", live.getStaysFactor(), stays, 1e-12);
        near("the live city's long recovery is Sickness.LONG_RECOVERY_BEFORE_M1", live.getLongRecovery(), Sickness.LONG_RECOVERY_BEFORE_M1, 1e-12);
        near("the live city's death lift is Sickness.SERVED_DEATHS_BEFORE_M2 times the coverage the month noted", live.getDeathLift(), Sickness.SERVED_DEATHS_BEFORE_M2 * coverage, 1e-12);
        check("...so the death lift is not the fresh default 0, nor is the long recovery the default 1", live.getDeathLift() != 0 && live.getLongRecovery() != 1,
                live.getDeathLift() + " " + live.getLongRecovery());

        GameFiles.Result saved = RecipeCheck.save10(care, "R6f M1 M2 held");
        check("the early city's save writes wholly", saved.whole(), saved.message());
        Game loaded = RecipeCheck.load10(care);
        check("the early city loads in the month it was saved", loaded.getLoadFailure() == null && loaded.getMonth() == month,
                loaded.getLoadFailure() + " " + loaded.getMonth());
        near("the loaded city's general care coverage is the live city's, as the month noted it", loaded.getHealthcare().getCoverage(CareType.GENERAL), coverage, 1e-12);
        Sickness back = loaded.getSickness();
        near("the loaded city's stays factor is the live city's at once, before any month", back.getStaysFactor(), live.getStaysFactor(), 1e-12);
        near("...and is the product of every stays row's share", back.getStaysFactor(), stays, 1e-12);
        near("the loaded city's long recovery is the live city's", back.getLongRecovery(), live.getLongRecovery(), 1e-12);
        near("...and is Sickness.LONG_RECOVERY_BEFORE_M1", back.getLongRecovery(), Sickness.LONG_RECOVERY_BEFORE_M1, 1e-12);
        near("the loaded city's death lift is the live city's", back.getDeathLift(), live.getDeathLift(), 1e-12);
        near("...and is Sickness.SERVED_DEATHS_BEFORE_M2 times the coverage", back.getDeathLift(), Sickness.SERVED_DEATHS_BEFORE_M2 * coverage, 1e-12);
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

        // A find in a played month: the Law School's faculty complete L8 two years before history's month, and the bank hears it in that month (REV-R6f P1).
        Game finder = HistoricalLicenceCheck.town("R6f-l8-find", true);
        HistoricalRecipeCheck.allExcept(finder, "L8");
        int ahead = w - 24;
        HistoricalRecipeCheck.at(finder, ahead);
        HistoricalLicenceCheck.stand(finder, "Law School", 1);
        ResearchCatalog.Entry l8 = ResearchCatalog.get().entry("L8");
        DiscoveryCheck.pileJustUnder(finder, l8);
        Research research = finder.getResearch();
        check("fixture: a city founded the 1900 way, every discovery but L8 found, L8 open to the law field two years before history's month with its pile just under its size",
                finder.foundedUnfitted() && !research.foundHere("L8") && research.openProblems(ResearchCatalog.Field.LAW, ahead + 1).contains(l8)
                        && research.piles().containsKey("L8") && research.piles().get("L8") < Research.hiddenSize(finder.getWorldSeed(), l8),
                research.piles());
        check("fixture: its bank has not heard Basel III before the month", !finder.getBank().conservationBufferRequired() && finder.getBank().bufferFloor() == Bank.BUFFER_BEFORE_L8,
                finder.getBank().bufferFloor());
        double targetBefore = finder.getBank().capitalTarget();
        step(finder, "the 1900 city whose faculty complete L8");
        int played = finder.getMonth();
        check("fixture: the Law School's faculty worked in law that month and L8 was found in it, before history's month",
                research.researchers(ResearchCatalog.Field.LAW) > 0 && research.foundThisMonth().contains("L8") && research.foundMonth("L8") == played && played < w,
                research.researchers(ResearchCatalog.Field.LAW) + " " + research.foundHere() + " " + played);
        Bank hearing = finder.getBank();
        check("in that same month the bank requires the buffer, its floor Bank.CONSERVATION_BUFFER, before the next month is heard",
                hearing.conservationBufferRequired() && hearing.bufferFloor() == Bank.CONSERVATION_BUFFER, hearing.conservationBufferRequired() + " " + hearing.bufferFloor());
        check("fixture: its worst loss rate is under Bank.CONSERVATION_BUFFER", hearing.getWorstLossRate() < Bank.CONSERVATION_BUFFER, hearing.getWorstLossRate());
        near("...so its capital target that month is Bank.CAPITAL_RATIO plus Bank.CONSERVATION_BUFFER", hearing.capitalTarget(), Bank.CAPITAL_RATIO + Bank.CONSERVATION_BUFFER, 1e-12);
        check("...risen from the month before", hearing.capitalTarget() > targetBefore, targetBefore + " -> " + hearing.capitalTarget());
        GameFiles.Result foundSave = RecipeCheck.save10(finder, "R6f L8 found");
        check("the finder's save writes wholly", foundSave.whole(), foundSave.message());
        Game reread = RecipeCheck.load10(finder);
        check("the finder loads in the month of the find, with L8 found in it",
                reread.getLoadFailure() == null && reread.getMonth() == played && reread.getResearch().foundMonth("L8") == played,
                reread.getLoadFailure() + " " + reread.getMonth());
        Bank heard = reread.getBank();
        check("the loaded bank's conservationBufferRequired() is the live city's", heard.conservationBufferRequired() == hearing.conservationBufferRequired(),
                heard.conservationBufferRequired() + " vs " + hearing.conservationBufferRequired());
        check("...and its bufferFloor() is the live city's", heard.bufferFloor() == hearing.bufferFloor(), heard.bufferFloor() + " vs " + hearing.bufferFloor());
        near("...and its capitalTarget() is the live city's", heard.capitalTarget(), hearing.capitalTarget(), 1e-12);
    }

    /* ----- 4. E22 ----- */

    /** Stover (Desalination and Water Treatment 13, 2010, pp38-39): Las Palmas III's SWRO process drew 3.49 kWh a m3 with its Pelton turbines, before the pressure-exchanger retrofit. */
    static final double PELTON_KWH = 3.49;
    /** Stover (2010, pp38-39): the same SWRO process drew 2.68 kWh a m3 after the pressure exchangers replaced the turbines. */
    static final double PX_KWH = 2.68;
    /** The US DOE's Seawater Desalination Bandwidth Study (2017, pp16 and 24): the RO step with energy recovery (Carlsbad) draws 2.70 kWh a m3, the part of the plant's draw the exchanger changes. */
    static final double RO_STEP_KWH = 2.70;
    /** The game's unit of water in litres: 10,000 US gallons of 3.785411784 litres each, the gallon's exact definition (UtilitiesHandler's units; SulphideLeachCheck's 37,854 is this rounded). */
    static final double LITRES_A_WATER_UNIT = 37_854.11784;

    /** One building's definition in buildings.json, read as the file has it. */
    static JsonObject building(String name) throws Exception {
        JsonObject data = JsonParser.parseString(Files.readString(Path.of("src/main/resources/buildings.json"), StandardCharsets.UTF_8)).getAsJsonObject();
        for (JsonElement b : data.getAsJsonArray("buildings"))
            if (b.getAsJsonObject().get("name").getAsString().equals(name)) return b.getAsJsonObject();
        throw new IllegalStateException("no " + name + " in buildings.json");
    }

    /** The kW the exchanger's step adds to a works of this many water units a month: the RO step's kWh a m3 over the plant's m3 a month, over the kWh a kW-month, times the step's rise from PX_KWH back to PELTON_KWH. */
    static double undoKw(double unitsAMonth) {
        return RO_STEP_KWH * (unitsAMonth * LITRES_A_WATER_UNIT / 1000) / HydroSites.KWH_PER_UNIT * (PELTON_KWH / PX_KWH - 1);
    }

    /** The id of the pressure exchanger in buildings.json, the piece E22's change on the Desalination Plant is carried by (a piece's change is a count of the stack's unfitted, not a recipe flag). */
    static final String EXCHANGER = "pressure-exchanger";

    /** How many of the stack's plants lack the pressure exchanger (BuildingsStacks.getUnfitted()). */
    static long unfitted(Game g, BuildingsTemplate t) {
        BuildingsStacks stack = g.getBuildingManager().getStack(t);
        return stack == null ? 0 : stack.getUnfitted(EXCHANGER);
    }

    /**
     * A Desalination Plant that finishes in the city at the month: placed as the fixtures place a village (fitted), then the model's own finish rule said
     * over it (Game.fitWhatFinished(): a piece not known, or not paying, at the finish is left off a city founded the 1900 way), its choice struck and
     * applied. E22 the only discovery unknown when `heldBack`.
     */
    static BuildingsTemplate plantAt(Game g, int month, boolean heldBack) throws Exception {
        if (heldBack) HistoricalRecipeCheck.allExcept(g, "E22");
        HistoricalRecipeCheck.at(g, month);
        BuildingsTemplate t = HistoricalLicenceCheck.stand(g, "Desalination Plant", 1);
        HistoricalRecipeCheck.silent(() -> g.fitWhatFinished(List.of(new BuildingManager.Completion(t.getName(), 1))));
        HistoricalRecipeCheck.run(g);
        return t;
    }

    /** Every figure of the plant but its electricity, the one the exchanger changes. */
    static Map<String, Double> plantRest(BuildingsTemplate t) {
        Map<String, Double> figures = HistoricalRecipeCheck.figures(t);
        figures.remove("electricity");
        return figures;
    }

    /** The share of the plant's sector the grid does not draw for (ServicesManager.gridDraw()'s steam share): 0 for a plant in no sector. */
    static double steamShare(Game g, BuildingsTemplate t) {
        Sector s = g.getSectors().byKey(t.getSector());
        return s == null ? 0 : s.getSteamShare();
    }

    /** The sea works' draw the water's books pay at the last update (UtilitiesHandler.getLatestWaterDraw()), NaN before any update. */
    static double seaKw(Game g) {
        UtilitiesHandler.WaterDraw draw = g.getServicesManager().getUtilitiesHandler().getLatestWaterDraw();
        return draw == null ? Double.NaN : draw.seaKw();
    }

    /** How many plants of this template stand in the city. */
    static double standing(Game g, BuildingsTemplate t) {
        BuildingsStacks stack = g.getBuildingManager().getStack(t);
        return stack == null ? 0 : stack.getQuantity();
    }

    static void exchanger() throws Exception {
        OUT.println("--- 4. E22: the Desalination Plant draws more power until the pressure exchanger is known ---");
        int w = ResearchCatalog.knownFrom("E22");
        check("E22 is known from June 1997 (PolicyDates.monthOf(1997, 6))", w == PolicyDates.monthOf(1997, 6), w);
        ResearchCatalog.Entry entry = ResearchCatalog.get().entry("E22");
        check("the catalogue's E22 is dated June 1997, the month a placeholder (monthKnown false)",
                entry != null && entry.year() == 1997 && entry.monthOfYear() == 6 && !entry.monthKnown() && entry.worldMonth() == w, entry);
        check("...equipment of engineering, needing E6, with a fee, and its work the rule's for equipment in 1997, rounded",
                entry != null && entry.kinds().equals(List.of("equipment")) && entry.size() == ResearchCatalog.Size.EQUIPMENT
                        && entry.field() == ResearchCatalog.Field.ENGINEERING && entry.needs().equals(List.of("E6")) && entry.fee()
                        && entry.work() == Math.round(ResearchCatalog.workByRule(ResearchCatalog.Size.EQUIPMENT, ResearchCatalog.Field.ENGINEERING, 1997)), entry);

        JsonObject plant = building("Desalination Plant");
        JsonObject change = null;
        for (JsonElement c : plant.getAsJsonArray("recipeChanges"))
            if (c.getAsJsonObject().get("entry").getAsString().equals("E22")) change = c.getAsJsonObject();
        check("buildings.json's Desalination Plant (id 73) carries an E22 change", plant.get("id").getAsInt() == 73 && change != null, plant.get("id"));
        JsonObject without = change.getAsJsonObject("without");
        double base = plant.get("electricity").getAsDouble();
        double units = plant.get("production1").getAsDouble();
        double undo = without.get("electricity").getAsDouble();
        check("buildings.json's E22 undo changes the plant's electricity and nothing else", without.keySet().equals(Set.of("electricity")), without);
        near("...and is the RO step's PELTON_KWH/PX_KWH rise over the plant's m3 a month, in kW (RO_STEP_KWH, LITRES_A_WATER_UNIT, 720 kWh a kW-month)", undo, undoKw(units), 1e-9);
        boolean listed = false;
        for (JsonElement p : plant.getAsJsonArray("equipment"))
            listed |= p.getAsJsonObject().get("id").getAsString().equals(EXCHANGER) && p.getAsJsonObject().get("since").getAsString().equals("E22");
        check("...carried by the plant's own piece: the plant lists a pressure exchanger since E22 and the change names it",
                listed && change.get("equipment").getAsString().equals(EXCHANGER), change);

        // Today's anchor, from a default city's own template at the month before E22.
        Game today = HistoricalLicenceCheck.town("R6e-e22-default", false);
        int carriers = 0;
        BuildingsTemplate carrier = null;
        RecipeChange modelChange = null;
        for (BuildingsTemplate t : today.getBuildingManager().getTemplates())
            for (RecipeChange c : t.getRecipeChanges()) if (c.code().equals("E22")) { carriers++; carrier = t; modelChange = c; }
        check("exactly one template carries a recipe change coded E22, and it is the Desalination Plant (id 73)",
                carriers == 1 && carrier.getId() == 73 && carrier.getName().equals("Desalination Plant"), carriers);
        check("...its change is the pressure exchanger's, follows no other, and its only non-zero field is electricity",
                EXCHANGER.equals(modelChange.piece()) && modelChange.after() == null && modelChange.makes().isEmpty() && modelChange.uses().isEmpty()
                        && modelChange.jobs().isEmpty() && modelChange.gen() == 0 && modelChange.water() == 0 && modelChange.consumables() == 0
                        && modelChange.production() == 0 && modelChange.land() == 0 && modelChange.steamRail() == 0
                        && modelChange.crackerPetrol() == 0 && modelChange.dig() == 0 && modelChange.electricity() != 0, modelChange);
        near("...and its electricity is the sourced arithmetic's", modelChange.electricity(), undoKw(units), 1e-9);
        Equipment exchanger = carrier.getEquipment(EXCHANGER);
        check("the piece is the plant's only one, known from E22, bought in Machinery alone and worked for",
                exchanger != null && carrier.getEquipment().size() == 1 && exchanger.getSince().equals("E22")
                        && exchanger.getBuys().keySet().equals(Set.of(Good.MACHINERY)) && exchanger.getWork() > 0, exchanger);

        BuildingsTemplate modern = plantAt(today, w - 1, false);
        int anchor = modern.getElectricityConsumption();
        check("a default city's plant stands, and its electricity is buildings.json's own at the month before E22",
                standing(today, modern) == 1 && anchor == base, anchor + " vs " + base);
        check("...with no plant lacking the exchanger", unfitted(today, modern) == 0, unfitted(today, modern));
        long before = Math.round(base + undo);
        check("fixture: the plant before E22 runs Math.round(electricity + undo), 13,454 kW (the row's text), more than the anchor",
                before == Math.round(anchor + undoKw(units)) && before == 13_454 && before > anchor, before + " vs " + anchor + " + " + undo);
        Map<String, Double> today1 = plantRest(modern);

        // The month before E22, in a 1900 city: the plant that finishes then is recorded as lacking the exchanger.
        Game old = HistoricalLicenceCheck.town("R6e-e22-1900", true);
        check("fixture: a city founded the 1900 way", old.foundedUnfitted(), "");
        BuildingsTemplate t = plantAt(old, w - 1, true);
        check("fixture: one Desalination Plant stands in it, every discovery but E22 known",
                standing(old, t) == 1 && !old.getResearch().known("E22", w - 1) && old.getResearch().known("E6", w - 1), standing(old, t));
        check("a plant that finishes before E22 is recorded as lacking the exchanger: the stack's unfitted count is its quantity",
                unfitted(old, t) == standing(old, t), unfitted(old, t) + " of " + standing(old, t));
        check("a 1900 plant the month before E22 runs Math.round(electricity + undo) kW",
                t.getElectricityConsumption() == before, t.getElectricityConsumption() + " vs " + before);
        check("...with production1, water and jobs today's",
                t.getProduction1() == modern.getProduction1() && t.getWaterConsumption() == modern.getWaterConsumption()
                        && java.util.Arrays.stream(JobType.values()).allMatch(j -> t.getJobs(j) == modern.getJobs(j)),
                t.getProduction1() + " " + t.getWaterConsumption());
        check("...every other figure of it is the default city's", plantRest(t).equals(today1), plantRest(t) + " vs " + today1);

        // From E22's month, by the recipe call: knowing the piece fits none, so the plant built before it still runs the undo.
        HistoricalRecipeCheck.at(old, w);
        HistoricalRecipeCheck.run(old);
        check("in E22's month the plant built before it still lacks the exchanger and runs the undo, knowledge alone fitting nothing",
                unfitted(old, t) == 1 && t.getElectricityConsumption() == before, unfitted(old, t) + " " + t.getElectricityConsumption());
        check("...every other figure still the default city's", plantRest(t).equals(today1), plantRest(t) + " vs " + today1);

        // ...and by a real month.
        Game stepped = HistoricalLicenceCheck.town("R6e-e22-month", true);
        BuildingsTemplate s = plantAt(stepped, w - 1, true);
        check("fixture: the month before E22 the stepped city's plant lacks the exchanger and runs the undo",
                unfitted(stepped, s) == 1 && s.getElectricityConsumption() == before, unfitted(stepped, s) + " " + s.getElectricityConsumption());
        step(stepped, "the 1900 city across E22's month");
        check("...and the real month into E22's month fits none: the count and the undo stay, every other figure today's",
                stepped.getMonth() == w && unfitted(stepped, s) == 1 && s.getElectricityConsumption() == before && plantRest(s).equals(today1),
                stepped.getMonth() + " " + unfitted(stepped, s) + " " + s.getElectricityConsumption());

        // A plant that finishes after E22 comes fitted, when the exchanger pays for a new one at the city's prices.
        Game fresh = HistoricalLicenceCheck.town("R6e-e22-new", true);
        HistoricalRecipeCheck.allExcept(fresh, "E22");
        HistoricalRecipeCheck.at(fresh, w);
        BuildingsTemplate newPlant = HistoricalLicenceCheck.stand(fresh, "Desalination Plant", 1);
        HistoricalRecipeCheck.silent(() -> fresh.simulateMonths(2));
        EquipmentFit.Verdict forNew = EquipmentFit.verdictForNew(fresh, newPlant, newPlant.getEquipment(EXCHANGER));
        check("fixture: two months after E22 with a plant standing, at the power price the played months struck, the exchanger PAYS on a new plant",
                fresh.getMonth() > w && fresh.getResearch().known("E22", fresh.getMonth()) && forNew.kind() == EquipmentFit.Kind.PAYS, forNew);
        HistoricalLicenceCheck.stand(fresh, "Desalination Plant", 2);
        HistoricalRecipeCheck.silent(() -> fresh.fitWhatFinished(List.of(new BuildingManager.Completion(newPlant.getName(), 1))));
        HistoricalRecipeCheck.run(fresh);
        check("a plant that finishes then comes fitted: two stand, no plant lacks the exchanger, and the plants run today's electricity, the anchor",
                standing(fresh, newPlant) == 2 && unfitted(fresh, newPlant) == 0 && newPlant.getElectricityConsumption() == anchor && plantRest(newPlant).equals(today1),
                standing(fresh, newPlant) + " " + unfitted(fresh, newPlant) + " " + newPlant.getElectricityConsumption());

        // The reader: the sea works' draw the water's books pay, after a real month in a held city.
        Game drawn = HistoricalLicenceCheck.town("R6e-e22-draw", true);
        BuildingsTemplate p = plantAt(drawn, w - 2, true);
        check("fixture: the held city's plant lacks the exchanger and runs the undo", unfitted(drawn, p) == 1 && p.getElectricityConsumption() == before, unfitted(drawn, p));
        step(drawn, "the held city before E22's month");
        double count = standing(drawn, p);
        check("fixture: the plant stands, the month is still before E22's, and the water's books have made an update",
                count == 1 && drawn.getMonth() == w - 1 && p.getElectricityConsumption() == before && !Double.isNaN(seaKw(drawn)), drawn.getMonth() + " " + seaKw(drawn));
        double steam = steamShare(drawn, p);
        check("fixture: the plant's sector holds no steam share, so the grid's draw is the template's powerBought() whole", steam == 0, steam);
        near("the sea works' draw the water's books pay is the plants' count times the template's powerBought()", seaKw(drawn), count * p.powerBought(), 1e-9);
        check("...and is more than the same count times today's electricity, buildings.json's own", seaKw(drawn) > count * base, seaKw(drawn) + " vs " + count * base);
        step(drawn, "the held city into E22's month");
        near("...and in E22's month, the plant still lacking the exchanger, the draw is still the count times powerBought()", seaKw(drawn), count * p.powerBought(), 1e-9);
        check("...which is the undo's, more than the count times buildings.json's own electricity", p.getElectricityConsumption() == before && seaKw(drawn) > count * base,
                p.getElectricityConsumption() + " " + seaKw(drawn));

        // The fitting: who fits a city work's piece. Four depots and the months they settle in give the builders the output a fitting is worked with
        // (FittingCheck.fitTown()'s), and the played months the prices; the verdict on the standing plant is the model's own.
        HistoricalLicenceCheck.stand(drawn, "Construction Depot", 4);
        HistoricalRecipeCheck.silent(() -> drawn.simulateMonths(Forms1900Check.SETTLE));
        check("fixture: the builders have output to work a fitting with",
                drawn.getBuildingOutput() > 0, drawn.getBuildingOutput() + " vs " + exchanger.getWork());
        BuildingsStacks drawnStack = drawn.getBuildingManager().getStack(p);
        EquipmentFit.Verdict verdict = EquipmentFit.verdict(drawn, drawnStack, p.getEquipment(EXCHANGER));
        check("fixture: in E22's month the plant lacks the exchanger, and its gain pays for the fitting (the verdict is PAYS, the gain a month earning back more than the cost)",
                unfitted(drawn, p) == 1 && verdict.kind() == EquipmentFit.Kind.PAYS && verdict.gain() > 0 && verdict.earnsBack() > verdict.cost(), verdict);
        int ordered = 0;
        for (int i = 0; i < 12; i++) {
            RecipeCheck.step(drawn, 1);
            ordered = Math.max(ordered, drawn.getFittings().onSite(p.getId(), EXCHANGER));
        }
        OUT.println("  NOTE twelve months after E22, the exchanger paying (gain " + verdict.gain() + " a month against a cost of " + verdict.cost() + "), no one ordered it:"
                + " fittings on site at the most " + ordered + ", plants still lacking it " + unfitted(drawn, p)
                + " (EquipmentFit.rank() and choose() read a sector's stacks, and a city work has no sector; only the player's Fit, Game.fitForCity(), orders a city work's piece)");
        String[] refused = new String[1];
        HistoricalRecipeCheck.silent(() -> refused[0] = drawn.fitForCity(p, EXCHANGER));
        check("the player's Fit orders the exchanger on the plant: fitForCity() accepts, and a fitting stands on site", refused[0] == null && drawn.getFittings().onSite(p.getId(), EXCHANGER) == 1, refused[0]);
        int months = 0;
        while (unfitted(drawn, p) > 0 && months < 36) {
            RecipeCheck.step(drawn, 1);
            months++;
        }
        RecipeCheck.step(drawn, 1);
        check("...the builders' crews finish it (" + months + " months): no plant lacks the exchanger and it runs today's electricity, the anchor, every other figure today's",
                unfitted(drawn, p) == 0 && p.getElectricityConsumption() == anchor && plantRest(p).equals(today1),
                unfitted(drawn, p) + " " + p.getElectricityConsumption() + " after " + months + " months");
        near("...and the sea works' draw the water's books pay falls to the count times powerBought()", seaKw(drawn), count * p.powerBought(), 1e-9);
        check("...which is the count times buildings.json's own electricity, today's draw", seaKw(drawn) == count * base, seaKw(drawn) + " vs " + count * base);

        // The imported patent on the fitted plant, through the getters PublicLicenceCheck reads rapid sand's capture by.
        double gain = LicenceFees.publicGain(drawn, drawnStack, EXCHANGER);
        double bill = LicenceFees.publicBillOf(drawn, drawnStack);
        check("fixture: E22 is abroad inside its term (June 1997 to June 2017) and the plant is fitted with it",
                LicenceFees.payable("E22", drawn.getMonth(), drawn.getResearch()) && drawn.getMonth() - w < LicenceFees.TERM_MONTHS && drawnStack.getUnfitted(EXCHANGER) == 0,
                drawn.getMonth());
        double price = drawn.getEconomyManager().getPowerPriceFor(p);
        double savedKw = count * (before - anchor);
        check("fixture: the plant's own row, E6, is no longer abroad under patent, so the stack's bill is E22's alone; and the works' power has a price",
                !LicenceFees.payable("E6", drawn.getMonth(), drawn.getResearch()) && price > 0, price);
        check("the imported exchanger's public gain is the power it saves, the plants' undone draw at the commerce price"
                + " (it serves no more water, so the gain is not service: LicenceFees.entryGain() adds the saving)",
                gain > 0 && Math.abs(gain - savedKw * price) <= 1e-9 * Math.max(1, gain), gain + " vs " + savedKw + " kW at " + price);
        check("...and the plant's licence bill is SHARE of that gain while the patent runs", Math.abs(bill - LicenceFees.SHARE * gain) <= 1e-9 * Math.max(1, bill),
                bill + " vs " + LicenceFees.SHARE * gain);

        // A default city's plant is the anchor across E22's month.
        HistoricalRecipeCheck.at(today, w - 1);
        step(today, "the default city across E22's month");
        check("a default city's plant keeps the anchor and lacks no exchanger through E22's month",
                modern.getElectricityConsumption() == anchor && unfitted(today, modern) == 0 && plantRest(modern).equals(today1),
                modern.getElectricityConsumption() + " " + unfitted(today, modern));
        near("...and its sea works' draw is the count times the anchor, before and after", seaKw(today), standing(today, modern) * base, 1e-9);

        // A city held before E22, saved and loaded.
        Game held = HistoricalLicenceCheck.town("R6e-e22-held", true);
        BuildingsTemplate h = plantAt(held, w - 2, true);
        check("fixture: the held city's plant lacks the exchanger and runs the undo", unfitted(held, h) == 1 && h.getElectricityConsumption() == before, unfitted(held, h));
        GameFiles.Result saved = RecipeCheck.save10(held, "R6e E22 held");
        check("the held city's save writes wholly", saved.whole(), saved.message());
        Game loaded = RecipeCheck.load10(held);
        check("the held city loads", loaded.getLoadFailure() == null && loaded.getMonth() == w - 2, loaded.getLoadFailure());
        BuildingsTemplate l = loaded.getBuildingManager().getTemplateByName("Desalination Plant");
        check("the loaded city's stack keeps the unfitted count, and its plant runs the undo at once, before any month",
                unfitted(loaded, l) == 1 && l.getElectricityConsumption() == before && l.powerBought() == h.powerBought(),
                unfitted(loaded, l) + " " + l.getElectricityConsumption() + " vs " + before);
        check("...every other figure the default city's", plantRest(l).equals(today1), plantRest(l) + " vs " + today1);
        step(loaded, "the loaded city");
        check("...and the next month, still before E22, keeps the count and the electricity",
                loaded.getMonth() == w - 1 && unfitted(loaded, l) == 1 && l.getElectricityConsumption() == before,
                loaded.getMonth() + " " + unfitted(loaded, l) + " " + l.getElectricityConsumption());
        near("...and the sea works' draw it pays is the count times powerBought()", seaKw(loaded), standing(loaded, l) * l.powerBought(), 1e-9);
        step(loaded, "the loaded city into E22's month");
        check("...and E22's month fits none: the count and the undo stay, the draw is still the undo's",
                loaded.getMonth() == w && unfitted(loaded, l) == 1 && l.getElectricityConsumption() == before && seaKw(loaded) > standing(loaded, l) * base,
                loaded.getMonth() + " " + unfitted(loaded, l) + " " + l.getElectricityConsumption());

        // A save from before the piece (format 65) holds no unfitted count for the exchanger: a plant with no record, which a placed plant is.
        Game legacy = HistoricalLicenceCheck.town("R6e-e22-legacy", true);
        HistoricalRecipeCheck.allExcept(legacy, "E22");
        HistoricalRecipeCheck.at(legacy, w - 2);
        BuildingsTemplate g65 = HistoricalLicenceCheck.stand(legacy, "Desalination Plant", 1);
        HistoricalRecipeCheck.run(legacy);
        check("fixture: a held city's plant with no record of the exchanger lacking (what a save from before the piece holds), before E22",
                unfitted(legacy, g65) == 0 && g65.getElectricityConsumption() == before, unfitted(legacy, g65) + " " + g65.getElectricityConsumption());
        GameFiles.Result savedOld = RecipeCheck.save10(legacy, "R6e E22 legacy");
        check("the legacy city's save writes wholly", savedOld.whole(), savedOld.message());
        Game loadedOld = RecipeCheck.load10(legacy);
        BuildingsTemplate lo = loadedOld.getBuildingManager().getTemplateByName("Desalination Plant");
        check("the loaded legacy plant runs the undo before E22, with no count", loadedOld.getLoadFailure() == null && unfitted(loadedOld, lo) == 0
                && lo.getElectricityConsumption() == before, loadedOld.getLoadFailure());
        HistoricalRecipeCheck.at(loadedOld, w);
        HistoricalRecipeCheck.run(loadedOld);
        OUT.println("  NOTE the legacy plant (no unfitted record) in E22's month: " + lo.getElectricityConsumption() + " kW, unfitted " + unfitted(loadedOld, lo)
                + " (RecipeChanges.apply() reads the piece's share as unfitted over quantity once the entry is known, and an absent count is 0: the plant is read as fitted)");
        check("from E22's month the legacy plant, with no record of the exchanger lacking, runs today's electricity with no fitting ordered",
                lo.getElectricityConsumption() == anchor && unfitted(loadedOld, lo) == 0 && loadedOld.getFittings().onSite(lo.getId(), EXCHANGER) == 0,
                lo.getElectricityConsumption() + " vs " + anchor);
    }

    public static void main(String[] args) throws Exception {
        quietHelpers();
        robotSurgery();
        agents();
        basel3();
        exchanger();
        OUT.println("ModernRowsCheck: " + (fails == 0 ? "all " + checks + " ok" : fails + " FAILED of " + checks));
        System.exit(fails == 0 ? 0 : 1);
    }
}
