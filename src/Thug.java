import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Thug extends NPC {

    private boolean contacted = false;
    private static boolean cutsceneTriggered = false; // shared — only one triggers it

    public Thug(GamePanel gp, int spawnX, int spawnY) {
        super(gp);
        worldX    = spawnX;
        worldY    = spawnY;
        speed     = 3;
        solidArea = new Rectangle2D(8, 16, 32, 32);
    }

    public static void resetTrigger() {
        cutsceneTriggered = false;
    }

    @Override
    public void setAction() {
        if (contacted) return;

        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        // Walk straight toward player
        if (Math.abs(dx) > Math.abs(dy)) {
            direction = dx > 0 ? "east" : "west";
        } else {
            direction = dy > 0 ? "south" : "north";
        }

        // Contact — trigger cutscene once
        // Change this one line in Thug.setAction() where contact is detected:
        if (dist < gp.tileSize * 1.2) {
            contacted = true;
            if (!cutsceneTriggered) {
                cutsceneTriggered = true;
                gp.cutsceneManager.startHitSequence(); // CHANGED
            }
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (screenX + gp.tileSize < 0 || screenX > gp.screenWidth ||
                screenY + gp.tileSize < 0 || screenY > gp.screenHeight) return;

        // Placeholder — red square until you have thug sprites
        gc.setFill(Color.DARKRED);
        gc.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 11));
        gc.fillText("THUG", screenX + 6, screenY + 28);
    }
}
