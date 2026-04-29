import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class TileManager {
    GamePanel gp;
    public Tile[] tiles;
    public int[][] mapTileNum = new int[50][50];

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tiles   = new Tile[4];

        String basePath = "file:///" + System.getProperty("user.dir").replace("\\", "/");

        // 0 — Overworld grass
        tiles[0] = new Tile();
        tiles[0].image     = new Image(basePath + "/src/Resources/grass.png");
        tiles[0].collision = false;

        // 1 — Overworld wall
        tiles[1] = new Tile();
        tiles[1].image     = new Image(basePath + "/src/Resources/wall.png");
        tiles[1].collision = true;

        // 2 — Dungeon floor (placeholder color)
        tiles[2] = makeColorTile(Color.color(0.25, 0.22, 0.20), false);

        // 3 — Dungeon wall (placeholder color)
        tiles[3] = makeColorTile(Color.color(0.15, 0.12, 0.10), true);

        loadMap();
    }

    private Tile makeColorTile(Color color, boolean collision) {
        WritableImage img = new WritableImage(gp.tileSize, gp.tileSize);
        PixelWriter   pw  = img.getPixelWriter();
        for (int y = 0; y < gp.tileSize; y++)
            for (int x = 0; x < gp.tileSize; x++)
                pw.setColor(x, y, color);
        Tile t = new Tile();
        t.image     = img;
        t.collision = collision;
        return t;
    }

    private void loadMap() {
        for (int row = 0; row < 50; row++) {
            for (int col = 0; col < 50; col++) {
                if (row == 0 || row == 49 || col == 0 || col == 49) {
                    mapTileNum[row][col] = 1;
                } else {
                    mapTileNum[row][col] = 0;
                }
            }
        }
    }

    public void draw(GraphicsContext gc) {
        int playerWorldX  = gp.player.worldX;
        int playerWorldY  = gp.player.worldY;
        int playerScreenX = gp.player.screenX;
        int playerScreenY = gp.player.screenY;

        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {
                int tileWorldX  = col * gp.tileSize;
                int tileWorldY  = row * gp.tileSize;
                int tileScreenX = tileWorldX - playerWorldX + playerScreenX;
                int tileScreenY = tileWorldY - playerWorldY + playerScreenY;

                if (tileScreenX + gp.tileSize < 0 || tileScreenX > gp.screenWidth ||
                        tileScreenY + gp.tileSize < 0 || tileScreenY > gp.screenHeight) continue;

                int tileNum = mapTileNum[row][col];
                gc.drawImage(tiles[tileNum].image, tileScreenX, tileScreenY, gp.tileSize, gp.tileSize);
            }
        }
    }
}
