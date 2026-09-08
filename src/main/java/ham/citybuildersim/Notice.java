package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * One thing the city needs told about, and whether anybody has looked at it.
 *
 * WHY THE TEXT IS STORED RATHER THAN REBUILT
 *
 * Every other panel in this game derives its words from the city as it is now,
 * and that is right for a panel, because a panel describes the present. A
 * notice does not. A notice says "on month 340 your builders started being laid
 * off, and here is what it was costing", and by the time the player reads it
 * the city may have moved on - so rebuilding the sentence from today's figures
 * would rewrite history, and a RESOLVED notice could not be rebuilt at all: the
 * condition it describes is gone, and the numbers behind it with it.
 *
 * So the body is written when the notice is raised and refreshed every month it
 * is still true, which keeps a live warning current and freezes a resolved one
 * at the last thing it actually said.
 *
 * THREE MONTHS, AND THEY MEAN DIFFERENT THINGS
 *
 * raised   - when the city first said it. Never changes.
 * read     - when the player opened it. 0 while unread, and unread is what
 *            makes a notice force the inbox open.
 * resolved - when the condition stopped being true. 0 while it still is. A
 *            resolved notice stays in the list, greyed, for two years.
 *
 * @author Jerus
 */
public class Notice {

    /* Which condition this is. The inbox keys on it, so a city can never hold
     * two live notices about the same thing, and the screen maps it to the
     * place that fixes it. */
    private String key;

    private String title;
    private List<String> body = new ArrayList<>();

    private int raised;
    private int read;
    private int resolved;

    /** Gson needs it. */
    Notice() { }

    Notice(String key, String title, List<String> body, int month) {
        this.key = key;
        this.title = title;
        this.body = new ArrayList<>(body);
        this.raised = month;
    }

    public String getKey()   { return key == null ? "" : key; }
    public String getTitle() { return title == null ? "" : title; }

    /** Never null, even on a notice from a save written before the field existed. */
    public List<String> getBody() {
        if (body == null) body = new ArrayList<>();
        return body;
    }

    public int getRaised()   { return raised; }
    public int getRead()     { return read; }
    public int getResolved() { return resolved; }

    public boolean isRead()     { return read > 0; }
    public boolean isResolved() { return resolved > 0; }

    /** Unread AND still true: the only state that interrupts the player. */
    public boolean isUrgent() { return read <= 0 && resolved <= 0; }

    void refresh(String title, List<String> body) {
        this.title = title;
        this.body = new ArrayList<>(body);
    }

    void markRead(int month) {
        if (read <= 0) read = Math.max(1, month);
    }

    void markResolved(int month) {
        if (resolved <= 0) resolved = Math.max(1, month);
    }

    /**
     * How old the resolution is, for the two-year cull.
     *
     * Live notices are never stale, however old they are - a city that has had
     * no cemetery for thirty years still has no cemetery.
     */
    int monthsSinceResolved(int month) {
        return resolved <= 0 ? 0 : month - resolved;
    }
}
