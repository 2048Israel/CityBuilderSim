/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package ham.citybuildersim;

import javafx.application.Application;

/**
 * The way in.
 *
 * Deliberately does NOT extend Application, and that is load-bearing rather than
 * a style choice: the JVM launcher checks whether the main class is an
 * Application subclass and, if it is, refuses to start unless javafx.graphics is
 * on the MODULE path - the "JavaFX runtime components are missing" error. A
 * launcher class that merely calls launch() sidesteps that check, which is the
 * only reason the packaged jar runs at all. See claude/packaging-and-distribution.md.
 *
 * @author Jerus
 */
public class CityBuilderSim {

    public static void main(String[] args) {

        /*
         * Logging first, before anything else can fail.
         *
         * Everything the game prints goes to System.out, and in a packaged
         * build there is no console attached - so without this, every
         * diagnostic the game produces about its own failure is written to
         * nowhere. Starting it here means even a crash during startup lands in
         * the file.
         */
        GameLog.start(new GameFiles());

        installCrashHandler();

        Application.launch(UserInterface.class, args);
    }

    /**
     * Catches what nothing else did.
     *
     * JavaFX swallows exceptions thrown inside a button handler: the FX thread's
     * default handler prints them to a stderr that, in a packaged build, goes
     * nowhere. The window stays open and stops responding, and the player has no
     * way to know why or to tell anyone. At minimum it should end up in the log.
     */
    private static void installCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, error) ->
                GameLog.failure("Uncaught exception on thread \"" + thread.getName() + "\"", error));
    }
}
