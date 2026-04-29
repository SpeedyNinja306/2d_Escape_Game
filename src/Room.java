import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

public class Room {

    GamePanel gp;
    public int roomId;
    public int playerSpawnX, playerSpawnY;
    public int mapRows, mapCols;
    public int[][] mapTileNum;

    public ArrayList<Item> items = new ArrayList<>();
    public ArrayList<Door> doors = new ArrayList<>();
    // Guards added Phase 7

    public Room(GamePanel gp, int roomId) {
        this.gp     = gp;
        this.roomId = roomId;
        loadRoom();
    }

    private void loadRoom() {
        switch (roomId) {
            case 1 -> loadRoom1();
            default -> loadPlaceholder(roomId);
        }
    }

    private void loadRoom1() {
        mapRows = 22;
        mapCols = 30;
        mapTileNum = new int[mapRows][mapCols];

        // Fill with dungeon floor
        for (int r = 0; r < mapRows; r++)
            for (int c = 0; c < mapCols; c++)
                mapTileNum[r][c] = 2;

        // Border walls
        for (int r = 0; r < mapRows; r++) {
            mapTileNum[r][0]          = 3;
            mapTileNum[r][mapCols - 1] = 3;
        }
        for (int c = 0; c < mapCols; c++) {
            mapTileNum[0][c]          = 3;
            mapTileNum[mapRows - 1][c] = 3;
        }

        // Internal dividing wall (vertical) — row 3 to 10, col 10
        for (int r = 3; r <= 10; r++) mapTileNum[r][10] = 3;
        // Horizontal shelf wall — row 7, cols 10 to 16
        for (int c = 10; c <= 16; c++) mapTileNum[7][c] = 3;

        // Open gap in border wall at bottom-right for exit
        mapTileNum[mapRows - 1][mapCols - 2] = 2;

        // Player spawns top-left
        playerSpawnX = 2 * gp.tileSize;
        playerSpawnY = 2 * gp.tileSize;

        // Key hidden in the top-right pocket (past the dividing wall)
        items.add(new Key(15 * gp.tileSize, 3 * gp.tileSize));

        // Locked exit door at bottom-right gap
        doors.add(new Door(
                (mapCols - 2) * gp.tileSize,
                (mapRows - 1) * gp.tileSize,
                true,
                "Key"
        ));
    }

    // Stub rooms — just a floor with a spawn and an unlocked door
    private void loadPlaceholder(int id) {
        mapRows = 15;
        mapCols = 20;
        mapTileNum = new int[mapRows][mapCols];

        for (int r = 0; r < mapRows; r++)
            for (int c = 0; c < mapCols; c++)
                mapTileNum[r][c] = 2;

        for (int r = 0; r < mapRows; r++) {
            mapTileNum[r][0]          = 3;
            mapTileNum[r][mapCols - 1] = 3;
        }
        for (int c = 0; c < mapCols; c++) {
            mapTileNum[0][c]          = 3;
            mapTileNum[mapRows - 1][c] = 3;
        }

        mapTileNum[mapRows - 1][mapCols - 2] = 2;

        playerSpawnX = 2 * gp.tileSize;
        playerSpawnY = 2 * gp.tileSize;

        // Unlocked door — placeholder rooms have no challenge yet
        doors.add(new Door(
                (mapCols - 2) * gp.tileSize,
                (mapRows - 1) * gp.tileSize,
                false,
                ""
        ));
    }

    public boolean isCleared() {
        return doors.stream().allMatch(d -> d.open);
    }

    public void update() {
        // Guard updates go here in Phase 7
    }

    public void draw(GraphicsContext gc) {
        int playerWorldX  = gp.player.worldX;
        int playerWorldY  = gp.player.worldY;
        int playerScreenX = gp.player.screenX;
        int playerScreenY = gp.player.screenY;

        for (int row = 0; row < mapRows; row++) {
            for (int col = 0; col < mapCols; col++) {
                int tileWorldX  = col * gp.tileSize;
                int tileWorldY  = row * gp.tileSize;
                int tileScreenX = tileWorldX - playerWorldX + playerScreenX;
                int tileScreenY = tileWorldY - playerWorldY + playerScreenY;

                if (tileScreenX + gp.tileSize < 0 || tileScreenX > gp.screenWidth ||
                        tileScreenY + gp.tileSize < 0 || tileScreenY > gp.screenHeight) continue;

                gc.drawImage(
                        gp.tileM.tiles[mapTileNum[row][col]].image,
                        tileScreenX, tileScreenY,
                        gp.tileSize,  gp.tileSize
                );
            }
        }

        for (Item item : items) item.draw(gc, gp);
        for (Door door : doors) door.draw(gc, gp);
    }
}
