package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

/**
 * The minimap: a stamp of the city at the foot of the rail, above Menu, on screen from every tab; while the pointer is on it a preview beside it, 288 x 192; a click opens the land office's map over the window on what it shows - drawn by the model's MiniMapFrame from the district pictures, at the month's end.
 *
 * WHY THIS EXISTS (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3, and
 * its minimap/ mockup). Jerus, 2026-10-11: "i think a small map always
 * visible on the ui would be nice or no?". The header is full (the money
 * block and five tiles to a 1,280-pixel window) and anything floating over
 * the page hides what is under it - the income dome once hid the Memorial
 * Cemetery's price - so the stamp sits in the rail's own foot, which covers
 * nothing (the study's Q10, his to change). What it frames, its stride, its
 * pixels and what a click opens on are MiniMapFrame's, which MapCheck 12
 * holds; this is the toolkit's half, checked by eye on the PC.
 *
 * WHEN IT IS DRAWN: when the map's version has moved (MapTiles.version():
 * a month's end, a purchase) and at least STAMP_EVERY_MS of the clock has
 * passed since its picture was last begun, and again when a district it drew
 * without its picture gets one. Each far node is drawn from its kept layers
 * (MapTiles.KeptNode): the districts that moved repainted, the runs and the
 * land only when they moved - a few hundred microseconds a month at Jerus's
 * size, on the screen's thread, a node at a time (MiniMapFrame.node()), the
 * stamp's one node in one frame, the preview's six over as many frames as
 * FRAME_MS allows, and only while it shows. A month that is not drawn costs
 * nothing: the frame (MiniMapFrame.frame(), two passes over every district)
 * is found again only when a picture is about to be begun. Nothing slow is
 * done here: the city's map drawn aside the first time (a draft, as the land
 * office's map does, and never both at once: MapView.drafting), the nodes'
 * ground and the pictures it lacks (CityMap.pictureJobs()) on MapView.WORKER,
 * the map's one worker. No boats (Q12). On the real Earth it is the city's
 * plane, never the globe.
 */
final class MiniMap {

    /** The most a frame spends drawing the stamp and the preview, in ms: MapView.FRAME_MS - a node at least, so each finishes. */
    static final double FRAME_MS = MapView.FRAME_MS;

    /** The least a drawing waits between beginning one picture and the next, in ms: the stamp drawn at most four times a second, against ten at 50x (a month every 100 ms), so a highway laid shows within a quarter of a second (batch UP3). */
    static final double STAMP_EVERY_MS = 250;

    /** The gap between the rail's edge and the preview: 6 px (the mockup's). */
    static final double PREVIEW_GAP = 6;

    /** The preview's line under the picture (the mockup's). */
    static final String PREVIEW_WORDS = "Click for the full map";

    /** The stamp's word, under it as each rail button's is. */
    static final String STAMP_WORD = "Map";

    private final UserInterface ui;
    private final VBox node;
    private final StackPane stampBox;
    private final Popup popup = new Popup();
    private final VBox previewBox;

    /** One of its two pictures: its view, its far nodes' ground (asked of WORKER), its pixels and image, and how far its pass has come. */
    private final class Drawing {
        final int w, h;
        final int[] px, buf = new int[MapFrame.NODE_PX * MapFrame.NODE_PX];
        final WritableImage image;
        final ImageView view;
        MiniMapFrame.View at;
        byte[][] ground;
        boolean groundAsked;
        /** One kept node per far node of its view, row by row: what each was last drawn from (MapTiles.KeptNode), made when it is aimed at a new view. */
        MapTiles.KeptNode[] keeps;
        /** The map's version and pictures kept that the image shows, whether it was drawn short of a picture; and the pass under way: its next node, what it is of. */
        long drawnFor = Long.MIN_VALUE, pictures;
        boolean partial;
        int next = -1;
        long passFor, passPictures;
        boolean passPartial;
        /** When its last pass was begun (System.nanoTime()), and whether any has been since it was aimed at a view: the pace is kept from there. */
        long startedAt;
        boolean started;
        /** The built districts its last pass drew without the picture they would sample: asked of WORKER (askPictures()). */
        final Set<CityMap.District> wanted = new LinkedHashSet<>();

        Drawing(int w, int h) {
            this.w = w;
            this.h = h;
            px = new int[w * h];
            image = new WritableImage(w, h);
            view = new ImageView(image);
            view.setSmooth(false);
            blank();
        }

        /** Aimed at a view: the same view keeps its ground and its kept nodes; another asks for its own ground, makes new kept nodes and is not held to the pace. Either way drawn again. */
        void aim(MiniMapFrame.View v) {
            if (!v.equals(at)) {
                at = v;
                ground = null;
                groundAsked = false;
                next = -1;
                started = false;
                keeps = new MapTiles.KeptNode[v.nodesAcross() * v.nodesDown()];
                for (int k = 0; k < keeps.length; k++) keeps[k] = new MapTiles.KeptNode();
            }
            drawnFor = Long.MIN_VALUE;
        }

        void forget() {
            at = null;
            ground = null;
            groundAsked = false;
            keeps = null;
            started = false;
            drawnFor = Long.MIN_VALUE;
            next = -1;
            wanted.clear();
            blank();
        }

        /** Whether the pace lets it begin a picture at `now` (System.nanoTime()): none begun since it was aimed at its view, or STAMP_EVERY_MS passed since the last was. */
        boolean paceOpen(long now) {
            return !started || now - startedAt >= (long) (STAMP_EVERY_MS * 1e6);
        }

        void blank() {
            java.util.Arrays.fill(px, TileRaster.VOID);
            image.getPixelWriter().setPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), px, 0, w);
        }
    }

    private final Drawing stamp = new Drawing(MiniMapFrame.STAMP_W, MiniMapFrame.STAMP_H),
            preview = new Drawing(MiniMapFrame.PREVIEW_W, MiniMapFrame.PREVIEW_H);

    /* ----------------------------- what it draws from ----------------------------- */

    private CityMap shown;
    private MapTiles tiles;
    private long version = Long.MIN_VALUE;
    private MiniMapFrame.Box frame;
    private Game.MapDraft draft;
    private int draftsFailed;
    /** The districts whose pictures' jobs are on WORKER. */
    private final Set<CityMap.District> asked = new HashSet<>();

    private final AnimationTimer pump = new AnimationTimer() {
        @Override public void handle(long now) { tick(); }
    };
    private boolean running;

    MiniMap(UserInterface ui) {
        this.ui = ui;
        stampBox = new StackPane(stamp.view);
        double bw = MiniMapFrame.STAMP_W + 2, bh = MiniMapFrame.STAMP_H + 2;
        stampBox.setMinSize(bw, bh);
        stampBox.setPrefSize(bw, bh);
        stampBox.setMaxSize(bw, bh);
        stampBox.setStyle(stampStyle(false));
        Label word = new Label(STAMP_WORD);
        word.setStyle("-fx-font-size: 10.5px; -fx-text-fill: " + Palette.TEXT_MUTED + ";");
        word.setMinWidth(Region.USE_PREF_SIZE);
        node = new VBox(3, stampBox, word);
        node.setAlignment(Pos.CENTER);
        node.setStyle("-fx-padding: 4 0 6 0; -fx-cursor: hand;");
        node.setMinHeight(Region.USE_PREF_SIZE);
        node.setMaxHeight(Region.USE_PREF_SIZE);
        node.setOnMouseClicked(e -> {
            hidePreview();
            ui.openMapOn(frame);
            e.consume();
        });
        node.hoverProperty().addListener((o, was, now) -> {
            stampBox.setStyle(stampStyle(now));
            if (now) showPreview(); else hidePreview();
        });
        Label words = new Label(PREVIEW_WORDS);
        words.setStyle(Palette.words(Palette.SIZE_LABEL, Palette.TEXT_LABEL));
        previewBox = new VBox(4, preview.view, words);
        previewBox.setStyle("-fx-background-color: " + Palette.PANEL + "; -fx-border-color: " + Palette.EDGE + ";"
                + " -fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 6;");
        previewBox.setEffect(new DropShadow(28, 0, 8, Color.rgb(0, 0, 0, 0.45)));
        previewBox.setMouseTransparent(true);
        popup.getContent().add(previewBox);
        popup.setAutoHide(false);
        popup.setAutoFix(true);
        node.sceneProperty().addListener((o, was, now) -> {
            if (now != null) start(); else hidePreview();
        });
    }

    /** The stamp's frame: a pixel of the accent round it (the mockup's), the lighter accent under the pointer. */
    private static String stampStyle(boolean hover) {
        return "-fx-border-color: " + (hover ? Palette.ACCENT_LIGHT : Palette.ACCENT) + "; -fx-border-width: 1;"
                + " -fx-border-radius: 3; -fx-background-color: " + MapView.css(TileRaster.VOID) + "; -fx-background-radius: 3;";
    }

    /** The stamp and its word: what the rail lays above Menu. */
    Region node() { return node; }

    /** A new city or a load: everything drawn is another city's. */
    void forget() {
        hidePreview();
        shown = null;
        tiles = null;
        version = Long.MIN_VALUE;
        frame = null;
        draft = null;
        draftsFailed = 0;
        asked.clear();
        stamp.forget();
        preview.forget();
        start();
    }

    private void start() {
        if (!running) {
            running = true;
            pump.start();
        }
    }

    /* =====================================================================
       A FRAME OF THE CLOCK
       ===================================================================== */

    private void tick() {
        if (node.getScene() == null) {
            pump.stop();
            running = false;
            hidePreview();
            return;
        }
        Game g = ui.game;
        if (g == null || !inView(node)) {
            hidePreview();
            return;
        }
        if (!keepUp(g)) return;
        long deadline = System.nanoTime() + (long) (FRAME_MS * 1e6);
        draw(stamp, deadline);
        askPictures(stamp);
        // The preview's ground is asked for while it is hidden, so a hover finds it; its pixels only while it shows.
        if (popup.isShowing()) {
            draw(preview, deadline);
            askPictures(preview);
        } else if (preview.at != null && preview.ground == null && !preview.groundAsked) {
            askGround(preview);
        }
    }

    /** Whether a node and every parent of it are visible: the rail is hidden on the game's menus. */
    private static boolean inView(Node n) {
        for (Node p = n; p != null; p = p.getParent()) if (!p.isVisible()) return false;
        return true;
    }

    /**
     * The minimap caught up with the city: its map drawn aside the first
     * time - a draft on WORKER, unless the land office's map has one there
     * already (MapView.drafting), when it waits for that - a new map (a load)
     * thrown out with what was drawn from it, and when the map's version
     * moved and the stamp's pace allows a picture to be begun (STAMP_EVERY_MS),
     * the frame found again and both views aimed at it: a month that is not
     * drawn finds nothing. False while there is no map to draw from.
     */
    private boolean keepUp(Game g) {
        if (!g.hasCityMap()) {
            if (shown != null) forgetDrawn();
            if (draft == null || draft.game() != g) {
                Game.MapDraft other = MapView.drafting;
                if (other != null && other.game() == g && other != draft) return false;
                if (draftsFailed >= MapView.DRAFTS_MOST) {
                    g.getCityMap();
                } else {
                    Game.MapDraft d = g.mapDraft();
                    draft = d;
                    MapView.drafting = d;
                    if (d != null) MapView.WORKER.submit(() -> {
                        try {
                            d.draw();
                        } catch (RuntimeException e) {
                            System.out.println("The city map could not be drawn aside for the minimap: " + e);
                        }
                        Platform.runLater(() -> kept(d));
                    });
                }
            }
            if (!g.hasCityMap()) return false;
        }
        CityMap m = g.getCityMap();
        if (m != shown) {
            forgetDrawn();
            shown = m;
            tiles = new MapTiles(m);
        }
        long v = MapTiles.version(m, g.getCityLand());
        if (v != version && stamp.paceOpen(System.nanoTime())) {
            version = v;
            frame = MiniMapFrame.frame(m);
            if (frame == null) return false;
            stamp.aim(MiniMapFrame.stamp(frame));
            preview.aim(MiniMapFrame.preview(frame));
        }
        return frame != null;
    }

    /** A draft drawn on WORKER, back on the screen's thread: kept as the city's map when it still is the city's. */
    private void kept(Game.MapDraft d) {
        if (MapView.drafting == d) MapView.drafting = null;
        if (d != draft) return;
        draft = null;
        Game g = ui.game;
        if (g == d.game() && d.drawn() && !g.hasCityMap() && !g.adoptMap(d)) draftsFailed++;
        if (!d.drawn()) draftsFailed++;
    }

    private void forgetDrawn() {
        shown = null;
        tiles = null;
        version = Long.MIN_VALUE;
        frame = null;
        asked.clear();
        stamp.forget();
        preview.forget();
    }

    /**
     * A picture drawn, or a node of it, until the deadline - one node at
     * least: when the version moved, or a district it drew without its
     * picture has one now (the map's count of pictures kept moved), and at
     * least STAMP_EVERY_MS has passed since it last began one, its nodes
     * again from the first, each MiniMapFrame.node() from its ground and its
     * kept node (only what moved since is redrawn), and the image written
     * when the last is done. A picture under way runs on whatever the pace.
     * Its ground is asked of WORKER first, the world's (MiniMapFrame.ground()).
     */
    private void draw(Drawing dr, long deadline) {
        if (dr.at == null) return;
        if (dr.ground == null) {
            if (!dr.groundAsked) askGround(dr);
            return;
        }
        long kept = shown.picturesKept();
        if (dr.next < 0) {
            boolean stale = dr.drawnFor != version || (dr.partial && dr.pictures != kept);
            if (!stale) return;
            long now = System.nanoTime();
            if (!dr.paceOpen(now)) return;
            dr.started = true;
            dr.startedAt = now;
            dr.next = 0;
            dr.passFor = version;
            dr.passPictures = kept;
            dr.passPartial = false;
        }
        int nodes = dr.at.nodesAcross() * dr.at.nodesDown();
        Set<CityMap.District> lacked = new LinkedHashSet<>();
        do {
            MiniMapFrame.node(tiles, dr.at, dr.next, dr.ground[dr.next], dr.keeps[dr.next], dr.buf, dr.px, lacked);
            dr.next++;
        } while (dr.next < nodes && System.nanoTime() < deadline);
        if (!lacked.isEmpty()) {
            dr.passPartial = true;
            dr.wanted.addAll(lacked);
        }
        if (dr.next < nodes) return;
        dr.image.getPixelWriter().setPixels(0, 0, dr.w, dr.h, PixelFormat.getIntArgbInstance(), dr.px, 0, dr.w);
        dr.drawnFor = dr.passFor;
        dr.pictures = dr.passPictures;
        dr.partial = dr.passPartial;
        dr.next = -1;
    }

    /** A view's far nodes' ground read on WORKER (the world is safe there), kept on the screen's thread while the view is still the one aimed at. */
    private void askGround(Drawing dr) {
        final MiniMapFrame.View v = dr.at;
        final CityMap map = shown;
        final World.Key world = map.worldKey();
        dr.groundAsked = true;
        MapView.WORKER.submit(() -> {
            byte[][] got = MiniMapFrame.ground(world, v);
            Platform.runLater(() -> {
                if (shown != map || !v.equals(dr.at)) return;
                dr.ground = got;
                dr.groundAsked = false;
            });
        });
    }

    /**
     * The district pictures a drawing's last pass lacked: their plans' jobs
     * (CityMap.pictureJobs()) handed to WORKER as one task, in order - as
     * the expanded map's far nodes ask (MapView.askPictures()) - while its
     * view's districts fit what the map keeps (MapTiles.picturesFit()),
     * each district once until they come back and are kept here
     * (CityMap.adopt()); the drawings that lacked them are drawn again then,
     * their count of pictures kept having moved.
     */
    private void askPictures(Drawing dr) {
        if (dr.wanted.isEmpty() || dr.at == null) return;
        if (!tiles.picturesFit(dr.at.asMapFrame())) {
            dr.wanted.clear();
            return;
        }
        final CityMap map = shown;
        final List<CityMap.District> handed = new ArrayList<>();
        final List<CityMap.Job> jobs = new ArrayList<>();
        long until = System.nanoTime() + (long) (FRAME_MS * 1e6);
        for (CityMap.District d : dr.wanted) {
            if (System.nanoTime() > until) break;
            if (!asked.add(d)) continue;
            handed.add(d);
            jobs.addAll(map.pictureJobs(d));
        }
        dr.wanted.clear();
        if (handed.isEmpty()) return;
        MapView.WORKER.submit(() -> {
            for (CityMap.Job j : jobs) {
                try {
                    j.run();
                } catch (RuntimeException e) {
                    System.out.println("A district's picture could not be drawn for the minimap: " + e);
                }
            }
            Platform.runLater(() -> {
                if (shown != map) return;
                for (CityMap.Job j : jobs) map.adopt(j);
                asked.removeAll(handed);
            });
        });
    }

    /* =====================================================================
       THE PREVIEW, ON HOVER
       ===================================================================== */

    /** The preview beside the rail, its foot level with the stamp's, while the pointer is on the stamp: shown, then moved by what its box measures on the screen (its shadow is the popup's too). */
    private void showPreview() {
        if (node.getScene() == null || node.getScene().getWindow() == null || ui.game == null) return;
        Node rail = node.getParent() != null ? node.getParent() : node;
        Bounds r = rail.localToScreen(rail.getBoundsInLocal()), s = stampBox.localToScreen(stampBox.getBoundsInLocal());
        if (r == null || s == null) return;
        popup.show(node.getScene().getWindow(), r.getMaxX() + PREVIEW_GAP, Math.max(r.getMinY(), s.getMaxY() - previewBox.prefHeight(-1)));
        Bounds b = previewBox.localToScreen(previewBox.getBoundsInLocal());
        if (b != null) {
            popup.setX(popup.getX() + r.getMaxX() + PREVIEW_GAP - b.getMinX());
            popup.setY(popup.getY() + Math.max(r.getMinY() - b.getMinY(), s.getMaxY() - b.getMaxY()));
        }
        start();
    }

    private void hidePreview() {
        if (popup.isShowing()) popup.hide();
    }
}
