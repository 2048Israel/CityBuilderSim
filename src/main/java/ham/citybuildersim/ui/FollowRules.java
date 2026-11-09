package ham.citybuildersim.ui;

import ham.citybuildersim.InputLog;
import java.util.List;

/**
 * What Follow re-enacts of a logged sitting and what it drops (TR2, 2026-11-09): the verdict on one logged input event, from
 * the screen the player had open, the screen the copy shows now and what the copy's window is doing. No JavaFX in this class,
 * so a model harness can ask it; the window's nodes are told to it as a flag ({@link #DROP}), the keys as the log's codes.
 *
 * <p>WHY (TR2): Follow plays a sitting's mouse and keys back into the copy's window (Jerus chose "Re-enact it"), and a click
 * of the player's that saved, loaded, founded a city, quit or ran the clock must not do that again in the copy: the copy is
 * a throwaway shown to a viewer, whose own clock, files and window are not the ghost's to touch. Everything on a game menu is
 * the player leaving the city, so every input there is dropped, on the logged side and on the copy's; the clock's keys and
 * the keys that open the menu or change the window are dropped; a click on a node that opens a menu, leaves the city or moves
 * the clock is dropped. The Reenactor (the one that fires the events) asks here for each one, and keeps a press's verdict
 * for its drags and its release, so a dropped press is never half done.
 */
public final class FollowRules {

    private FollowRules() { }

    /** The node property that marks a node (or, set on an ancestor, everything in it) the ghost never clicks: it opens a game menu, leaves the city, quits or moves the clock. */
    public static final String DROP = "follow.drop";

    /** The log's code of the Space key, which runs the clock: the toolkit's key name, as {@link InputLog#keyCode} makes it. */
    static final int SPACE = InputLog.keyCode("Space");

    /** The log's code of the Left key, which slows the clock. */
    static final int LEFT = InputLog.keyCode("Left");

    /** The log's code of the Right key, which speeds the clock. */
    static final int RIGHT = InputLog.keyCode("Right");

    /** The log's code of the F11 key, which is the window's full screen. */
    static final int F11 = InputLog.keyCode("F11");

    /** The log's code of the Esc key, which opens the game menu. */
    static final int ESC = InputLog.keyCode("Esc");

    /** The log's code of the P key, which opens the game menu too when nobody is typing. */
    static final int P = InputLog.keyCode("P");

    /** The screens the rules treat as game menus (the model's own list, UserInterface.GAME_MENUS), for a harness that walks them. */
    public static List<String> gameMenus() {
        return UserInterface.GAME_MENUS;
    }

    /** KEEP: re-enact it; every other verdict is a reason for dropping the event. */
    public enum Verdict {
        KEEP,
        /** The player's screen was a game menu (Save, Load, New city, Settings, Quit were clicked there). */
        DROP_LOGGED_MENU,
        /** The copy shows a game menu. */
        DROP_COPY_MENU,
        /** A dialog (Quit's) is up on the copy. */
        DROP_DIALOG,
        /** A press or release on a node marked {@link #DROP}. */
        DROP_NODE,
        /** Space, Left or Right: the clock and its speed. */
        DROP_CLOCK_KEY,
        /** F11: the window's full screen. */
        DROP_WINDOW_KEY,
        /** Esc or P where it would open the game menu. */
        DROP_MENU_KEY
    }

    /**
     * The verdict on one logged event.
     *
     * @param loggedScreen    the screen the player had open at the event's moment (the last SCREEN marker), or null if unknown
     * @param copyScreen      the screen the copy shows now
     * @param dialogUp        whether a dialog is up on the copy
     * @param typing          whether the copy's focus is in a text field
     * @param escapeOpensMenu whether this key, Esc or P, would open the game menu on the copy now (asked per key: P opens it where Esc closes the full chart)
     * @param onDropNode      whether the event's point is on a node marked {@link #DROP}, or inside one
     */
    public static Verdict judge(InputLog.Event e, String loggedScreen, String copyScreen, boolean dialogUp, boolean typing,
                                boolean escapeOpensMenu, boolean onDropNode) {
        switch (e.kind()) {
            case WINDOW: case SCREEN: case MAP_VIEW: case CLOCK: case MONTH:
                return Verdict.KEEP;
            default:
                break;
        }
        if (loggedScreen != null && UserInterface.isGameMenu(loggedScreen)) return Verdict.DROP_LOGGED_MENU;
        if (copyScreen != null && UserInterface.isGameMenu(copyScreen)) return Verdict.DROP_COPY_MENU;
        if (dialogUp) return Verdict.DROP_DIALOG;
        switch (e.kind()) {
            case PRESS: case RELEASE:
                return onDropNode ? Verdict.DROP_NODE : Verdict.KEEP;
            case KEY_DOWN: case KEY_UP: {
                int code = e.code();
                // The window's own key is the window's, typing or not; Esc opens the menu from a text field too.
                if (code == F11) return Verdict.DROP_WINDOW_KEY;
                if (code == ESC) return escapeOpensMenu ? Verdict.DROP_MENU_KEY : Verdict.KEEP;
                if (typing) return Verdict.KEEP;
                if (code == SPACE || code == LEFT || code == RIGHT) return Verdict.DROP_CLOCK_KEY;
                if (code == P && escapeOpensMenu) return Verdict.DROP_MENU_KEY;
                return Verdict.KEEP;
            }
            default:
                return Verdict.KEEP;
        }
    }
}
