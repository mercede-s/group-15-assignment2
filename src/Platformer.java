import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;

public class Platformer extends GameEngine{
    public static void main(String[] args) {
        createGame(new Platformer(),60);
    }

    //SETUP VARIABLES
    private boolean left, right, jump, isJumping;
    private boolean facingLeft;
    Image spritesheet;
    Image background;
    private TileMap tilemap;

    public void init() {

        setWindowSize(800, 600);
        spritesheet = loadImage("AnimationSheet_Character.png");
        background = loadImage("Background.png");

        //create new tilemap
        tilemap = new TileMap("testmap.txt", 50, 800);

        //initialise player
        initPlayer();
    }

    @Override
    public void update(double dt) {
        //update player
        updatePlayer(dt);

        //update tilemap
        tilemap.updateTileMap(playerPositionX, playerPositionY);
    }

    @Override
    public void paintComponent() {

        //draw background image
        drawImage(background, 0,0, 800, 600);

        //draw tilemap
        tilemap.drawTileMap();

        //draws the player
        drawPlayer();
    }

    //-------------PLAYER-----------------------

    //player variables
    double playerPositionX;
    double playerPositionY;
    double playerXVelocity;
    double playerYVelocity;
    Image[] playerSpriteIdle = new Image[2];
    Image[] playerSpriteWalking = new Image[8];
    Image[] playerSpriteJumping = new Image[8];
    double playerFrameTimer;

    double floorHeight; //height player is drawn at that puts its feet at 550px
    private float jumpStrength;
    private float gravity;

    public void initPlayer() {
        // Setup booleans
        left  = false;
        right = false;
        jump = false;

        //load images for player into arrays
        for (int i = 0; i < 8; i++) {
            playerSpriteWalking[i] = subImage(spritesheet, i * 32,96, 32, 32);
        }
        for (int i = 0; i < 2; i++) {
            playerSpriteIdle[i] = subImage(spritesheet, i * 32, 0, 32, 32);
        }
        for (int i = 0; i < 8; i++) {
            playerSpriteJumping[i] = subImage(spritesheet, i * 32, 160, 32, 32);
        }

        // this may need to be updated for platforms
        floorHeight = 486; // 600 - 50 - 64

        //starting point for player
        playerPositionX = 250;
        playerPositionY = floorHeight;
        facingLeft = false;

        //controls how fast player moves
        playerXVelocity = 200;

        //frame timer for which frame is displayed
        playerFrameTimer = 0;

        //for jumping
        jumpStrength = 300;
        gravity = 20;
        playerYVelocity = 0;

    }

    public void updatePlayer(double dt) {
        //updating frame timer for player
        playerFrameTimer +=dt;

        //player movement
        if (left) {
            playerPositionX -= playerXVelocity *dt;
            facingLeft = true;
        }
        if (right) {
            playerPositionX += playerXVelocity *dt;
            facingLeft = false;
        }

        //apply gravity if player is in air
        if (playerPositionY < floorHeight) {
            playerYVelocity += gravity;
        }
        else {
            //player is on ground, reset vertical velocity
            playerPositionY = floorHeight;
            playerYVelocity = 0;
            isJumping = false;
        }

        //Sets initial upward velocity for jump
        if (jump && !isJumping) {
            playerYVelocity = -jumpStrength;
            isJumping = true;
            jump = false;
        }

        //update player's vertical position
        playerPositionY += playerYVelocity*dt;

        //make sure player does not fall through floor
        if (playerPositionY >= floorHeight) {
            playerPositionY = floorHeight;
            playerYVelocity = 0;
            isJumping = false;
        }

    }

    //draws the player
    public void drawPlayer() {
        int i;

        //may be better to replace if statements with switch if we are going to be adding many more player states

        //walking animation when moving left
        if (left && !isJumping) {
            i = getAnimationFrame(playerFrameTimer, 1, 8);
            //drawing image with negative width flips it horizontally
            drawImage(playerSpriteWalking[i], playerPositionX+64,  playerPositionY, -64, 64);
        }
        //walking animation when moving right
        else if (right && !isJumping) {
            i = getAnimationFrame(playerFrameTimer, 1, 8);
            drawImage(playerSpriteWalking[i], playerPositionX,  playerPositionY, 64, 64);
        }
        //jumping animations when moving right and left
        else if (isJumping && !facingLeft) {
            i = getAnimationFrame(playerFrameTimer, 1, 8);
            drawImage(playerSpriteJumping[i], playerPositionX, playerPositionY, 64, 64);
        }
        else if (isJumping && facingLeft) {
            i = getAnimationFrame(playerFrameTimer, 1, 8);
            drawImage(playerSpriteJumping[i], playerPositionX+64, playerPositionY, -64, 64);
        }
        //idle facing left and right
        else  if (facingLeft) {
            //currently idle will always face right, can add int/bool to track direction of last direction faced and draw image to match
            i = getAnimationFrame(playerFrameTimer, 1, 2);
            drawImage(playerSpriteIdle[i], playerPositionX+64, playerPositionY, -64, 64);
        }
        else  {
            i = getAnimationFrame(playerFrameTimer, 1, 2);
            drawImage(playerSpriteIdle[i], playerPositionX, playerPositionY, 64, 64);
        }
    }

    //this method is used to calculate the frame to display independent of the frame rate
    public int getAnimationFrame(double timer, double duration, int numFrames) {
        // Get frame
        int i = floor(((timer % duration) / duration) * numFrames);
        // Check range
        if(i >= numFrames) {
            i = numFrames-1;
        }
        // Return
        return i;
    }

    //--------------TILEMAP--------------------------
    public class TileMap {


        private double viewportWidth; // Width of the viewport in pixels
        private double worldWidth; // width of the world in pixels

        private int tileSize;
        private int[][] map; //2D array represents the tile map
        private int numRows;
        private int numCols;

        double camX;
        double offsetMaxX;
        double offsetMinX;

        //constructor
        public TileMap(String s, int tileSize, int viewportWidth){
            this.tileSize = tileSize;
            this.viewportWidth = viewportWidth;

            try {
                BufferedReader br = new BufferedReader(new FileReader(s));

                //Reads the number of columns and rows from the test map
                numCols = Integer.parseInt(br.readLine());
                numRows= Integer.parseInt(br.readLine());
                map = new int[numRows][numCols];
                System.out.println(numRows + " " + numCols);

                //Reads the tile map data
                String deliminator = " ";
                for (int row = 0; row < numRows; row++) {
                    String line = br.readLine();
                    String[] tokens = line.split(deliminator);
                    for (int col = 0; col < numCols; col++) {
                        map[row][col] = Integer.parseInt((tokens[col]));
                    }
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            //calculate the total width of the world
            this.worldWidth = tileSize*numCols;
            //max and mix offsets for the camera prevent it scrolling forever
            this.offsetMaxX = worldWidth - viewportWidth;
            this.offsetMinX = 0;

        }

        public void updateTileMap(double playerPositionX, double playerPositionY) {
            //update the viewport position based on player's X position
            camX = (int) playerPositionX - viewportWidth / 2;

            //check camera is not out of bounds
            if (camX > offsetMaxX) {
                camX = offsetMaxX;
            }
            else if (camX < offsetMinX) {
                camX = offsetMinX;
            }

        }

        public void drawTileMap() {
            saveCurrentTransform();
            //translates the tiles to simulate camera moving
            translate(-camX, 0);
            //drawing tiles
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    int rc = map[row][col];
                    if (rc == 0) {
                        //draw black tiles for 0's in test map
                        changeColor(black);
                        drawSolidRectangle(col*tileSize, row*tileSize, tileSize, tileSize);
                        System.out.println(col + " " + row);
                    }
                }
            }
            restoreLastTransform();
        }


    }

    //--------------KEY EVENTS-------------------------------------

    // KeyPressed for Game
    public void keyPressed(KeyEvent e) {

        // The user pressed left arrow
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            left = true;
        }
        // The user pressed right arrow
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = true;
        }
        // The user pressed space
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            jump = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        // The user released left arrow
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            // Record it
            left = false;
        }
        // The user released right arrow
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            // Record it
            right = false;
        }
        // The user released space
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            //jump = false;
        }
    }
}

