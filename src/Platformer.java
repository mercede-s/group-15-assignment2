import java.awt.*;
import java.awt.event.KeyEvent;

public class Platformer extends GameEngine {
    private GameWorld gameWorld;
    private Player player;
    private Image[] coinSprites;
    private TileMap tilemap;

    public static void main(String[] args) {
        createGame(new Platformer(), 60);
    }

    public Platformer() {
        super(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
    }

    @Override
    public void init() {
        Image playerSpriteSheet = loadImage("group-15-assignment2-main/resource/AnimationSheet_Character.png");
        Image coinSheet = loadImage("group-15-assignment2-main/resource/Coin_Silver-Sheet.png ");
        Image background = loadImage("group-15-assignment2-main/resource/Background.png");
        Image rockSheet = loadImage("group-15-assignment2-main/resource/Cave - Platforms.png");

        Image rock = subImage(rockSheet, 0,0, 210,210);

        Image[] playerIdleSprites = new Image[2];
        Image[] playerWalkingSprites = new Image[8];
        for (int i = 0; i < 8; i++) {
            playerWalkingSprites[i] = subImage(playerSpriteSheet, i * 32, 96, 32, 32);
        }
        for (int i = 0; i < 2; i++) {
            playerIdleSprites[i] = subImage(playerSpriteSheet, i * 32, 0, 32, 32);
        }

        player = new Player(playerIdleSprites, playerWalkingSprites);
        tilemap = new TileMap("testmap.txt", GameConfig.TILE_SIZE, GameConfig.WINDOW_WIDTH, rock);

        coinSprites = new Image[10];
        int coinSheetWidth = coinSheet.getWidth(null);
        int coinSheetHeight = coinSheet.getHeight(null);
        int coinIndex = 0;

        for (int y = 0; y < coinSheetHeight; y += 32) {
            for (int x = 0; x < coinSheetWidth; x += 32) {
                if (coinIndex < 10) {
                    coinSprites[coinIndex] = subImage(coinSheet, x, y, 32, 32);
                    if (coinSprites[coinIndex] == null) {
                        System.out.println("Error: coin image " + coinIndex + " is null.");
                    } else {
                        System.out.println("Coin frame " + coinIndex + " loaded successfully.");
                    }
                    coinIndex++;
                }
            }
        }

        gameWorld = new GameWorld(player, coinSprites, background, tilemap);
    }

    @Override
    public void update(double dt) {
        gameWorld.update(dt);
    }

    @Override
    public void paintComponent() {
        gameWorld.draw(mGraphics);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> player.setLeft(true);
            case KeyEvent.VK_RIGHT -> player.setRight(true);
            case KeyEvent.VK_SPACE -> player.setJump(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> player.setLeft(false);
            case KeyEvent.VK_RIGHT -> player.setRight(false);
            case KeyEvent.VK_SPACE ->player.setJump(false);

        }
    }
}