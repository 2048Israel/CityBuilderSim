package ham.citybuildersim.ui;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * Scratch probe for the study UP4, never in src/: what it costs the pulse to take a kept page's
 * children off their parents and put the same nodes back - cleared and added in two steps, or set
 * in one - against a pulse with nothing touched and a page built afresh. Whether a kept page may
 * clear its boxes and refill them each month, or must only touch a list that changed, rests on it.
 */
public final class ReaddProbe {

    public static void main(String[] args) throws Exception {
        int month = args.length > 0 ? Integer.parseInt(args[0]) : 200;
        int n = args.length > 1 ? Integer.parseInt(args[1]) : 8;
        InPlaceProbe.open(month);
        UserInterface ui = InPlaceProbe.ui;
        for (String screen : List.of("People", "Policy")) {
            Runnable show = screen.equals("People") ? ui.peopleScreen::showPopulationInfoMenu : ui.policyScreen::showPolicyMenu;
            String r = InPlaceProbe.fx(() -> {
                show.run();
                InPlaceProbe.scenePulse();
                List<Pane> panes = new ArrayList<>();
                for (Node x : ui.rootMenu.getChildren()) collect(x, panes);
                StringBuilder b = new StringBuilder(String.format("%-7s %4d panes:", screen, panes.size()));
                String[] modes = {"nothing touched", "clear then addAll", "setAll the same", "setAll reversed twice", "rebuilt"};
                for (int mode = 0; mode < modes.length; mode++) {
                    List<Double> css = new ArrayList<>(), lay = new ArrayList<>(), all = new ArrayList<>();
                    for (int i = 0; i < n; i++) {
                        if (mode == 4) {
                            ui.redraw();
                            panes.clear();
                            for (Node x : ui.rootMenu.getChildren()) collect(x, panes);
                        }
                        long a = System.nanoTime();
                        for (Pane p : panes) {
                            List<Node> kids = new ArrayList<>(p.getChildren());
                            if (mode == 1) { p.getChildren().clear(); p.getChildren().addAll(kids); }
                            if (mode == 2) p.getChildren().setAll(kids);
                            if (mode == 3) {
                                List<Node> rev = new ArrayList<>(kids);
                                java.util.Collections.reverse(rev);
                                p.getChildren().setAll(rev);
                                p.getChildren().setAll(kids);
                            }
                        }
                        double touch = (System.nanoTime() - a) / 1e6;
                        double[] pp = InPlaceProbe.pulse();
                        double sync = InPlaceProbe.scenePulse();
                        css.add(pp[0]); lay.add(pp[1]); all.add(touch + pp[0] + pp[1] + sync);
                    }
                    b.append(String.format("%n    %-22s css %5.1f  layout %5.1f  touch+pulse %5.1f ms", modes[mode],
                            InPlaceProbe.median(css), InPlaceProbe.median(lay), InPlaceProbe.median(all)));
                }
                return b.toString();
            });
            InPlaceProbe.out.println(r);
        }
        javafx.application.Platform.exit();
        System.exit(0);
    }

    static void collect(Node n, List<Pane> out) {
        if (n instanceof Pane p && !TreePatch.opaque(n) && !(TreePatch.custom(n) && TreePatch.laysOutItself(n.getClass()))) {
            out.add(p);
            for (Node k : p.getChildren()) collect(k, out);
        }
    }
}
