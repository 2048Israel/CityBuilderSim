import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Scratch, not in the build: CL1's renders side by side - args: out.png "title" a.png b.png [c.png ...],
 * each scaled to the first's height, a title strip above. java docs/studies/city-layout/Pair.java ...
 */
public class Pair {
    public static void main(String[] args) throws Exception {
        String title = args[1];
        BufferedImage[] im = new BufferedImage[args.length - 2];
        for (int i = 0; i < im.length; i++) im[i] = ImageIO.read(new File(args[i + 2]));
        int h = im[0].getHeight(), gap = 8, top = 28, w = 0;
        int[] ws = new int[im.length];
        for (int i = 0; i < im.length; i++) { ws[i] = (int) Math.round(im[i].getWidth() * (double) h / im[i].getHeight()); w += ws[i]; }
        w += gap * (im.length - 1);
        BufferedImage out = new BufferedImage(w, h + top, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setColor(new Color(0x16222c));
        g.fillRect(0, 0, w, h + top);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int x = 0;
        for (int i = 0; i < im.length; i++) { g.drawImage(im[i], x, top, ws[i], h, null); x += ws[i] + gap; }
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        g.setColor(new Color(0xf0f0f0));
        g.drawString(title, 8, 20);
        g.dispose();
        ImageIO.write(out, "png", new File(args[0]));
        System.out.println("wrote " + args[0]);
    }
}
