package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Everything the city has had to say for itself, newest first.
 *
 * WHAT THIS REPLACED
 *
 * Four red boxes that appeared in the middle of the main screen and had to be
 * dismissed before you could get on with anything. They worked - the shedding
 * banner is the reason a 4,000-month playtest stopped freezing at 660 people -
 * but they had two faults a banner cannot fix. A dismissed banner was gone: no
 * record it had ever appeared, and nothing to go back to when the player
 * changed their mind three months later. And a banner only exists on the screen
 * it was written for, so a warning raised during a fifty-month skip was
 * announced to an empty room.
 *
 * A notice does neither. It is raised by the CITY, once a month, whether
 * anybody is watching; it stays in the list after it is read; and it stays
 * after it is resolved, greyed, for two years, so "what went wrong in the
 * forties" has an answer.
 *
 * WHY THE TEXT IS BUILT HERE AND NOT ON THE SCREEN
 *
 * Because the screen is not always there. These sentences used to be built
 * inside UserInterface, which meant they could only be built while somebody was
 * looking at the main menu - so a condition that arose and passed inside a skip
 * produced no words at all. Built here, they are written the month the
 * condition is true, and the screen's only job is to draw them.
 *
 * @author Jerus
 */
public class Inbox {

    /** How long a resolved notice stays readable. Jerus asked for two years. */
    public static final int KEEP_MONTHS = 24;

    /*
     * Newest LAST, because that is the order they happened in and appending is
     * the honest way to record a sequence. The screen reverses it.
     */
    private List<Notice> notices = new ArrayList<>();

    /* ==================================================================
       THE MONTH
       ================================================================== */

    /**
     * Raise what is newly true, refresh what still is, resolve what is not.
     *
     * Called from Game.recordMonth, beside the graph history, and for the same
     * reason: both are records of a month that has finished, and both have to
     * happen whether or not a window is open.
     */
    public void takeMonth(Game game) {

        int month = game.getMonth();

        take(game, month, "shedding",
                game.isConstructionShedding(),
                "Your builders are being laid off",
                sheddingBody(game));

        take(game, month, "landlock",
                game.isPrivateInvestmentLandLocked(),
                "Your businesses have nowhere to build",
                landLockBody(game));

        take(game, month, "bank",
                game.getBank().isInsolvent(),
                "The bank has failed",
                bankBody(game));

        List<String> health = healthcareBody(game);
        take(game, month, "healthcare",
                !health.isEmpty(),
                "The city is not looking after its people",
                health);

        cull(month);
    }

    /**
     * One condition, this month.
     *
     * The live/not-live distinction is the whole method. A condition that is
     * true and has no live notice raises one; a condition that is true and
     * already has one refreshes its wording, because the numbers in it have
     * moved; a condition that has gone away resolves whatever was standing.
     */
    private void take(Game game, int month, String key, boolean nowTrue,
                      String title, List<String> body) {

        Notice live = live(key);

        if (nowTrue) {
            if (live == null) {
                notices.add(new Notice(key, title, body, month));
            } else {
                live.refresh(title, body);
            }
        } else if (live != null) {
            live.markResolved(month);
        }
    }

    /** Drop what has been settled longer than anybody needs to remember. */
    private void cull(int month) {
        notices.removeIf(n -> n.isResolved()
                && n.monthsSinceResolved(month) > KEEP_MONTHS);
    }

    /* ==================================================================
       READING
       ================================================================== */

    /** Newest first, which is the order the screen wants and nothing else does. */
    public List<Notice> newestFirst() {
        List<Notice> out = new ArrayList<>(notices);
        Collections.reverse(out);
        return out;
    }

    /** In the order they happened. The harnesses read this one. */
    public List<Notice> all() { return notices; }

    public int size() { return notices.size(); }

    /**
     * The one that interrupts: newest unread notice whose condition still holds.
     *
     * Null most of the time, which is the point - an inbox that always has
     * something shouting in it is an inbox nobody opens.
     */
    public Notice urgent() {
        for (int i = notices.size() - 1; i >= 0; i--) {
            if (notices.get(i).isUrgent()) return notices.get(i);
        }
        return null;
    }

    /** What goes on the envelope. Counts unread, resolved or not. */
    public int unread() {
        int n = 0;
        for (Notice notice : notices) if (!notice.isRead()) n++;
        return n;
    }

    /** The live notice for a condition, or null if there is not one. */
    public Notice live(String key) {
        for (int i = notices.size() - 1; i >= 0; i--) {
            Notice notice = notices.get(i);
            if (notice.getKey().equals(key) && !notice.isResolved()) return notice;
        }
        return null;
    }

    public void markRead(Notice notice, int month) {
        if (notice != null) notice.markRead(month);
    }

    /* ==================================================================
       SAVE AND LOAD
       ================================================================== */

    /**
     * Takes over a loaded inbox wholesale.
     *
     * The same shape as HistorySave.restoreFrom and for the same reason: Gson
     * leaves a field alone when the JSON has no key for it, so a save written
     * before the inbox existed arrives here as null and has to come out as an
     * empty list rather than a crash. A city that loads with no notices is
     * correct - it is a city nothing had been said to yet, and next month the
     * conditions that are still true will say it again.
     */
    public void restoreFrom(List<Notice> loaded) {
        notices = loaded == null ? new ArrayList<>() : new ArrayList<>(loaded);
        notices.removeIf(java.util.Objects::isNull);
    }

    public void reset() { notices = new ArrayList<>(); }

    /* ==================================================================
       WHAT EACH ONE SAYS

       Lifted from the four banners in UserInterface, unchanged in wording,
       because the wording was the part that worked. What changed is where they
       are built: here, from the city, every month - rather than on a screen,
       from the city, whenever somebody happened to be looking at it.
       ================================================================== */

    private static List<String> sheddingBody(Game game) {

        List<String> lines = new ArrayList<>();
        if (!game.isConstructionShedding()) return lines;

        double capacity = game.getBuildingManager().getTotalConstructionCapacity();
        boolean covered = game.isAutoSubsidised(game.getSectors().construction());
        double lost = game.getConstructionShedPoints();

        lines.add(String.format("Construction sold %,.0f points of capacity - it has no", lost));
        lines.add("orders, so it is losing money and shrinking to fit.");
        lines.add("");
        lines.add(String.format("Capacity left:      %,.0f pts", capacity));
        lines.add(covered
                ? "Standing policy:    ON - the city covers its losses"
                : "Standing policy:    off - nothing is protecting them");
        lines.add("");
        lines.add("Everything you build runs through these crews. Once they are");
        lines.add("gone, rebuilding them is the slowest thing in the game.");
        return lines;
    }

    private static List<String> landLockBody(Game game) {

        List<String> lines = new ArrayList<>();
        if (!game.isPrivateInvestmentLandLocked()) return lines;

        java.util.Set<String> blocked = game.getLandBlockedSectors();
        double free = game.getLandManager().getAvailableSqFt();
        double used = game.getLandManager().getUtilisation() * 100;

        lines.add(blocked.size() == 1
                ? String.format("%s wants to expand and cannot -", blocked.iterator().next())
                : String.format("%d sectors want to expand and cannot -", blocked.size()));
        lines.add("there is no land left to build on.");
        lines.add("");
        lines.add(String.format("Waiting:            %s", String.join(", ", blocked)));
        lines.add(String.format("Land free:          %,.0f sq ft", free));
        lines.add(String.format("Land in use:        %.0f%%", used));
        lines.add("");
        lines.add("They will keep asking every month and keep being refused.");
        lines.add("Nothing else is wrong, and nothing will fix itself.");
        return lines;
    }

    private static List<String> bankBody(Game game) {

        List<String> lines = new ArrayList<>();
        Bank bank = game.getBank();
        if (!bank.isInsolvent()) return lines;

        lines.add("It lost more than it owned. Its creditors have absorbed the");
        lines.add("loss, but a bank with no capital cannot lend - so every");
        lines.add("borrower in the city is paying the full premium and the");
        lines.add("private sector has stopped building.");
        lines.add("");
        // TIMES A THOUSAND: the model counts in thousands and this was the one
        // place in the inbox that printed the raw figure with a dollar sign.
        lines.add(String.format("To recapitalise:    $%,.0f",
                bank.recapitalisationNeeded() * 1000));
        lines.add(String.format("The treasury holds: $%,.0f", game.getCash() * 1000));
        lines.add("");
        lines.add("It can rebuild its capital out of profits on the loans it");
        lines.add("still holds, but that takes years. Credit is shut until then.");
        return lines;
    }

    /**
     * The three things healthcare is silently costing the city, when they apply.
     *
     * ONE NOTICE, UP TO FOUR PARAGRAPHS, rather than four notices - they are one
     * subject and one answer, and an inbox with four red lines in it teaches a
     * player to stop reading red lines.
     *
     * Ordered by how expensive each is right now rather than by category, so
     * whatever is actually hurting most is the first thing read. Empty when the
     * city has nothing to answer for, which is the common case in a city that
     * has built its clinics and its cemetery.
     */
    private static List<String> healthcareBody(Game game) {

        Health health = game.getHealth();
        Healthcare service = game.getHealthcare();
        BuildingManager bm = game.getBuildingManager();
        PopulationCohorts cohorts = game.getCohorts();
        double[] staffing = game.getPopulationManager().getJobFillRate();

        List<String> lines = new ArrayList<>();

        /* 1. the dead, because it is the sharpest and the cheapest to fix */
        if (service.getUnburied() > 0) {
            lines.add(String.format("%,.0f people have nowhere to be buried. It is adding %.0f",
                    service.getUnburied(), health.getUnburiedRate() * 100));
            lines.add("points to the sick rate. A cemetery or a crematorium ends it.");
            lines.add("");
        }

        /* 2. general care, which is the largest standing loss */
        double generalCover = Health.coverageOf(
                bm.getStaffedCareCapacity(CareType.GENERAL, staffing), cohorts.total());
        if (generalCover < .9 && cohorts.total() > 0) {
            lines.add(String.format("Only %.0f%% of the city can see a doctor, so %.0f%% of every",
                    generalCover * 100, health.getBaselineRate() * 100));
            lines.add("month's work is not done. Clinics and hospitals fix it.");
            lines.add("");
        }

        /* 3. the two that kill people rather than slow them down */
        double childCover = Health.coverageOf(
                bm.getStaffedCareCapacity(CareType.CHILDCARE, staffing),
                CareType.CHILDCARE.populationServed(cohorts));
        double seniorCover = Health.coverageOf(
                bm.getStaffedCareCapacity(CareType.SENIOR, staffing),
                CareType.SENIOR.populationServed(cohorts));

        if (childCover < .5) {
            lines.add(String.format("Childcare reaches %.0f%%, so infants die at %.1fx the rate a",
                    childCover * 100,
                    Healthcare.mortalityFactor(AgeBand.BABY, childCover, .5, .5)));
            lines.add(String.format("half-served city loses - %.2f%% a year - and %.0f%% fewer",
                    AgeBand.BABY.getAnnualMortality()
                            * Healthcare.mortalityFactor(AgeBand.BABY, childCover, .5, .5) * 100,
                    (1 - Healthcare.birthFactor(childCover)
                            / Healthcare.birthFactor(1)) * 100));
            lines.add("children are born than in a city that has somewhere to put them.");
            lines.add("");
        }

        if (seniorCover < .5) {
            lines.add(String.format("Senior care reaches %.0f%%. Covering them would cut senior",
                    seniorCover * 100));
            lines.add(String.format("deaths to %.0f%% of today's and draw %.0f%% more people here.",
                    Healthcare.mortalityFactor(AgeBand.SENIOR, .5, .5, 1) * 100,
                    (Migration.seniorCarePull(1) - Migration.seniorCarePull(seniorCover)) * 100));
            lines.add("");
        }

        // Drop the trailing blank so the notice does not end in whitespace.
        if (!lines.isEmpty()) lines.remove(lines.size() - 1);
        return lines;
    }
}
