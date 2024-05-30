import java.awt.*;

public class Player {
    private double x, y, speed, velocityY;
    private boolean left, right, jump, onGround;
    private final Image[] idleSprites;
    private final Image[] walkingSprites;
    private double frameTimer;

    public Player(Image[] idleSprites, Image[] walkingSprites) {
        this.idleSprites = idleSprites;
        this.walkingSprites = walkingSprites;
        x = 250;
        y = GameConfig.GROUND_LEVEL - GameConfig.SPRITE_SIZE;
        speed = 200;
        velocityY = 0;
        onGround = true;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void setX(float x) { this.x = x; }

    public void setY(float y) { this.y = y; }

    public void setVelocityY(float velocityY) { this.velocityY = velocityY; }

    public void setOnGround(boolean onGround) { this.onGround = onGround;}

    public boolean getOnGround() { return onGround; }

    public double getY() { return y; }

    public boolean getLeft(){ return left; }

    public boolean getRight(){ return right; }

    public double getVelocityY() { return velocityY; }


    public void update(double dt) {
        frameTimer += dt;
        if (left) {
            x -= speed * dt;
        }
        if (right) {
            x += speed * dt;
        }

        //need a no double jumping check
        if (jump && velocityY == 0) {
            velocityY = -500;
            onGround = false;}

        velocityY += 1000 * dt;

        y += velocityY * dt;

        //stops player moving through floor
        if (y >= GameConfig.GROUND_LEVEL - GameConfig.SPRITE_SIZE) {
            y = GameConfig.GROUND_LEVEL - GameConfig.SPRITE_SIZE;
            velocityY = 0;
            onGround = true;
        }

    }

    public void draw(Graphics2D g, double offsetX) {
        int i = 0;
        double drawX = x - offsetX;
        if (left) {
            i = getAnimationFrame(frameTimer, 1, 8);
            g.drawImage(walkingSprites[i], (int) drawX + GameConfig.SPRITE_SIZE, (int) y, -GameConfig.SPRITE_SIZE, GameConfig.SPRITE_SIZE, null);
        } else if (right) {
            i = getAnimationFrame(frameTimer, 1, 8);
            g.drawImage(walkingSprites[i], (int) drawX, (int) y, GameConfig.SPRITE_SIZE, GameConfig.SPRITE_SIZE, null);
        } else {
            i = getAnimationFrame(frameTimer, 1, 2);
            g.drawImage(idleSprites[i], (int) drawX, (int) y, GameConfig.SPRITE_SIZE, GameConfig.SPRITE_SIZE, null);
        }

        g.setColor(Color.RED);
        g.drawRect((int)(x - offsetX), (int)y, GameConfig.SPRITE_SIZE, GameConfig.SPRITE_SIZE);
    }

    private int getAnimationFrame(double timer, double duration, int numFrames) {
        int i = (int) ((timer % duration) / duration * numFrames);
        return Math.min(i, numFrames - 1);
    }

    public Rectangle getBounds(double offsetX) {
        return new Rectangle((int) (x - offsetX), (int) y, GameConfig.SPRITE_SIZE, GameConfig.SPRITE_SIZE);
    }

    public double getX() {
        return x;
    }
}