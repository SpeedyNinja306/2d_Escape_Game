import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Door {

    public int     worldX, worldY;
    public boolean locked;
    public boolean open = false;
    public String  requiredItem;

    public Door(int worldX, int worldY, boolean locked, String requiredItem) {
        this.worldX       = worldX;
        this.worldY       = worldY;
        this.locked       = locked;
        this.requiredItem = requiredItem;
    }

    public void interact(Player player) {
        if (!locked) {
            open = true;
        } else if (player.inventory.hasItem(requiredItem)) {
            player.inventory.removeItem(requiredItem);
            locked = false;
            open   = true;
            System.out.println("Door unlocked with: " + requiredItem);
        } else {
            System.out.println("Locked. Requires: " + requiredItem);
            // UIManager hint message goes here later
        }
    }

    public void draw(GraphicsContext gc, GamePanel gp) {
        if (open) return;

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (screenX + gp.tileSize < 0 || screenX > gp.screenWidth ||
                screenY + gp.tileSize < 0 || screenY > gp.screenHeight) return;

        gc.setFill(locked ? Color.SADDLEBROWN : Color.BURLYWOOD);
        gc.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(screenX, screenY, gp.tileSize, gp.tileSize);

        // Gold lock dot when locked
        if (locked) {
            gc.setFill(Color.GOLD);
            gc.fillOval(screenX + gp.tileSize / 2 - 5, screenY + gp.tileSize / 2 - 5, 10, 10);
        }
    }
}
