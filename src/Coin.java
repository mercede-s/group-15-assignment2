import java.awt.*;

public class Coin {
    private final double x, y;
    private final Image[] sprites;
    private double frameTimer;
    public static final int SIZE = 32;

    public Coin(double x, double y, Image[] sprites) {
        this.x = x;
        this.y = y;
        this.sprites = sprites;
    }

    public void update(double dt) {
        frameTimer += dt;
    }

    public void draw(Graphics2D g, double offsetX) {
        int i = getAnimationFrame(frameTimer, 1, 10);
        g.drawImage(sprites[i], (int) (x - offsetX), (int) y, SIZE, SIZE, null);
    }

    private int getAnimationFrame(double timer, double duration, int numFrames) {
        int i = (int) ((timer % duration) / duration * numFrames);
        return Math.min(i, numFrames - 1);
    }

    public Rectangle getBounds(double offsetX) {
        return new Rectangle((int) (x - offsetX), (int) y, SIZE, SIZE);
    }
}