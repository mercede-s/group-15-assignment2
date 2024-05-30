import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class TileMap {
    private Image rock;
    private double viewportWidth;
    private double worldWidth;

    private int tileSize;
    private int map[][];
    private int numRows;
    private int numCols;

    private double maxXOffset;
    private double minXOffset;

    ArrayList<Tile> solidTiles;

    public TileMap(String filename, int tileSize, double viewportWidth, Image rock){
        this.tileSize = tileSize;
        this.viewportWidth = viewportWidth;
        this.rock = rock;

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            numCols = Integer.parseInt(br.readLine());
            numRows = Integer.parseInt(br.readLine());
            map = new int[numRows][numCols];

            String deliminator = " ";
            solidTiles = new ArrayList<>();

            for (int row = 0; row < numRows; row++) {
                String line = br.readLine();
                String[] tokens = line.split(deliminator);
                for (int col = 0; col < numCols; col++) {
                    map[row][col] = Integer.parseInt(tokens[col]);
                }
            }

            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    int rc = map[row][col];
                    if (rc == 0) {
                        Tile tile = new Tile(col*tileSize, row*tileSize);
                        solidTiles.add(tile);

                    }
                }
            }

            this.worldWidth = GameConfig.WORLD_WIDTH;
            this.maxXOffset = worldWidth - viewportWidth;
            this.minXOffset = 0;

            //Add Solid Tiles to an array
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawTileMap(Graphics g, double offsetX) {

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();

        g2d.translate(0, 0);

        g2d.setColor(Color.BLACK);

        for (Tile solidTile : solidTiles) {
            double x = solidTile.getX() - offsetX;
            double y = solidTile.getY();
            g.drawImage(rock, (int)x, (int)y, tileSize, tileSize, null);
            //g2d.fillRect((int) x, (int) y, tileSize, tileSize);
        }

        g2d.setTransform(originalTransform);
    }


}
