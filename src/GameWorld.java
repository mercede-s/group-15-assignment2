import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameWorld {
    private final Player player;
    private final ArrayList<Coin> coins;
    private final Image[] coinSprites;
    private int score;
    private double worldOffsetX;
    private final Map<Integer, Integer> segmentCoinCount;
    private final Image background;
    private final TileMap tilemap;
    ArrayList<Tile> intersectingTiles;

    public GameWorld(Player player, Image[] coinSprites, Image background, TileMap tilemap) {
        this.player = player;
        this.coinSprites = coinSprites;
        this.background = background;
        this.coins = new ArrayList<>();
        this.score = 0;
        this.worldOffsetX = 0;
        this.segmentCoinCount = new HashMap<>();
        this.tilemap = tilemap;

        generateInitialCoins();
    }

    public void update(double dt) {
        player.update(dt);

        for (Coin coin : coins) {
            coin.update(dt);
        }

        updateWorldOffset();
        checkCoinCollisions();
        respondToTileCollisions();

    }

    public void draw(Graphics2D g) {

        //clear canvas
        g.setColor(Color.white);
        g.fillRect(0,0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        // Draw background
        for (int i = 0; i < GameConfig.WORLD_WIDTH / GameConfig.WINDOW_WIDTH + 1; i++) {
            g.drawImage(background, i * GameConfig.WINDOW_WIDTH - (int) worldOffsetX, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT, null);
        }

        // Draw player
        player.draw(g, worldOffsetX);

        // Draw coins
        for (Coin coin : coins) {
            coin.draw(g, worldOffsetX);
        }

        // Draw score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString("Score: " + score, 550, 50);
        g.drawImage(coinSprites[0], 515, 22, 32, 32, null);

        //DrawTiles
        tilemap.drawTileMap(g, worldOffsetX);

    }

    private void generateInitialCoins() {
        Random rand = new Random();
        for (int i = 0; i < GameConfig.COINS_TOTAL; i++) {
            double x, y;
            boolean overlaps;
            do {
                int segment = rand.nextInt(GameConfig.WORLD_WIDTH / GameConfig.SEGMENT_WIDTH);
                x = segment * GameConfig.SEGMENT_WIDTH + rand.nextInt(GameConfig.SEGMENT_WIDTH - Coin.SIZE);
                y = GameConfig.GROUND_LEVEL - Coin.SIZE - rand.nextInt(GameConfig.JUMP_HEIGHT);
                overlaps = false;

                // Check if the segment has reached the max coin limit
                if (segmentCoinCount.getOrDefault(segment, 0) >= GameConfig.MAX_COINS_PER_SEGMENT) {
                    overlaps = true;
                    continue;
                }

                // Check for overlaps with existing coins
                for (Coin coin : coins) {
                    if (coin.getBounds(0).intersects(new Rectangle((int) x, (int) y, Coin.SIZE, Coin.SIZE))) {
                        overlaps = true;
                        break;
                    }
                }

                if (!overlaps) {
                    segmentCoinCount.put(segment, segmentCoinCount.getOrDefault(segment, 0) + 1);
                }
            } while (overlaps);
            coins.add(new Coin(x, y, coinSprites));
        }
    }

    private void checkCoinCollisions() {
        Rectangle playerBounds = player.getBounds(worldOffsetX);
        for (int i = 0; i < coins.size(); i++) {
            Coin coin = coins.get(i);
            if (playerBounds.intersects(coin.getBounds(worldOffsetX))) {
                coins.remove(i);
                score++;
                i--;
            }
        }
    }

    private ArrayList<Tile> getCollidingTiles() {
        Rectangle playerBounds = player.getBounds(worldOffsetX);
        intersectingTiles = new ArrayList<>();
        for (Tile solidTile : tilemap.solidTiles) {
            if (playerBounds.intersects(solidTile.getBounds(worldOffsetX))) {
                intersectingTiles.add(solidTile);
            }
        }
        return intersectingTiles;

    }

    private void respondToTileCollisions() {

        for (Tile tile : getCollidingTiles()) {
            if ((player.getY() + 14 == tile.getY())
                    || (player.getY() + GameConfig.TILE_SIZE == tile.getY())
                    || (player.getY() + 14 - GameConfig.TILE_SIZE == tile.getY())) {// if player is on the tile x check
                if (player.getRight()) {
                    float newX = (float) (tile.getX() - GameConfig.TILE_SIZE - 14);
                    player.setX(newX);
                    System.out.println("right check");

                }
                if (player.getLeft()) {
                    float newX = (float) (tile.getX() + GameConfig.TILE_SIZE);
                    player.setX(newX);
                    System.out.println("left check");

                }
            }

            else if (player.getVelocityY() < 0) { // if player moving up
                float newY = (float) (tile.getY() + GameConfig.TILE_SIZE);
                player.setY(newY);
                player.setVelocityY(0);
                System.out.println("Up check");
            }
            else if (player.getVelocityY() >= 0) { // if player moving down or not moving
                float newY = (float) (tile.getY() - GameConfig.SPRITE_SIZE);
                player.setY(newY);
                player.setVelocityY(0);
                System.out.println("down check");

            }
        }
    }

    private void updateWorldOffset() {
        double playerScreenX = player.getX() - worldOffsetX;
        if (playerScreenX > GameConfig.WINDOW_WIDTH / 2) {
            worldOffsetX = player.getX() - GameConfig.WINDOW_WIDTH / 2;
        }
    }

    public int getScore() {
        return score;
    }
}