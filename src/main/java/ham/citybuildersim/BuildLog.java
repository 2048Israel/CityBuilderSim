package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * What the city has gained, and when. The other half of DemolitionLog.
 *
 * The construction panel already showed what is going up and what has come
 * down, and had nothing at all for what actually LANDED. A player skipping fifty
 * months watched sites appear and disappear from the panel with no record of
 * which of them finished - the only evidence was a building count that had gone
 * up while they were not looking, which is exactly the complaint the demolition
 * log was written to answer, pointed the other way.
 *
 * Entries keep for the same KEEP_MONTHS as demolitions, on purpose: the two
 * lists sit in the same panel and a player comparing them should not have to
 * remember that the halves expire on different clocks.
 *
 * MERGED BY BUILDING AND MONTH, WHICH DEMOLITIONS DO NOT NEED
 *
 * Demolitions come from planRetirement, one sector at a time, a handful a year.
 * Completions come from advanceConstruction, which can finish several stacks in
 * a single month in a large city and does it every month forever. Recording
 * them one row per stack per month would push anything older than a year or two
 * out of a 40-entry cap within a few turns and make the panel unreadable long
 * before that. So a second batch of Houses in the same month adds to the row
 * already there rather than starting another.
 */
public class BuildLog {

    /** How long a completion stays on the panel. Matches DemolitionLog. */
    public static final int KEEP_MONTHS = DemolitionLog.KEEP_MONTHS;

    /** Hard cap, so a city building constantly cannot grow this without limit. */
    private static final int MAX_ENTRIES = 40;

    public static class Entry {

        public final String building;
        public final int month;

        /** Not final: completions of the same building in the same month merge. */
        public int quantity;

        Entry(String building, int quantity, int month) {
            this.building = building;
            this.quantity = quantity;
            this.month = month;
        }

        /** How long ago, in months. Never negative, however the clock moved. */
        public int monthsAgo(int currentMonth) {
            return Math.max(currentMonth - month, 0);
        }

        /** "this month", "last month", "3 months ago". */
        public String when(int currentMonth) {
            int ago = monthsAgo(currentMonth);
            if (ago <= 0) return "this month";
            if (ago == 1) return "last month";
            return ago + " months ago";
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    public void record(String building, int quantity, int month) {

        if (building == null || quantity <= 0) {
            return;
        }

        // Same building, same month, one row. Searching backwards because the
        // match, when there is one, is almost always the last thing added.
        for (int i = entries.size() - 1; i >= 0; i--) {
            Entry existing = entries.get(i);
            if (existing.month != month) {
                break;   // the list is in month order; older entries cannot match
            }
            if (existing.building.equals(building)) {
                existing.quantity += quantity;
                return;
            }
        }

        entries.add(new Entry(building, quantity, month));

        while (entries.size() > MAX_ENTRIES) {
            entries.remove(0);
        }
    }

    /**
     * What to show right now: everything within KEEP_MONTHS, newest first.
     *
     * Filtered on read rather than pruned on write, so the list does not depend
     * on anything having called a tidy-up method at the right moment.
     */
    public List<Entry> recent(int currentMonth) {

        List<Entry> visible = new ArrayList<>();

        for (Entry entry : entries) {
            if (entry.monthsAgo(currentMonth) <= KEEP_MONTHS) {
                visible.add(entry);
            }
        }

        Collections.reverse(visible);
        return visible;
    }

    /** Every entry ever kept, newest first. What gets saved. */
    public List<Entry> all() {
        List<Entry> copy = new ArrayList<>(entries);
        Collections.reverse(copy);
        return copy;
    }

    /**
     * Puts a saved log back, in the order it was written.
     *
     * Null-tolerant on purpose: a save written before this existed has no build
     * log at all, and Gson hands back null for a field that was not in the JSON.
     * That is the normal case for every save made before this build, not an
     * error, and it loads as an empty log.
     */
    public void restore(List<Entry> saved) {
        clear();
        if (saved == null) return;
        for (int i = saved.size() - 1; i >= 0; i--) {
            Entry e = saved.get(i);
            if (e != null) {
                record(e.building, e.quantity, e.month);
            }
        }
    }

    public int size() {
        return entries.size();
    }

    public void clear() {
        entries.clear();
    }
}
