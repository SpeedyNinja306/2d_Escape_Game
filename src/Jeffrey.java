import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Jeffrey extends NPC {

    private boolean triggered  = false;
    public  boolean escorting  = false;
    private final int triggerRadius;
    private final Random random = new Random();
    private final Map<String, Image> sprites = new HashMap<>();

    // Airport trigger zone — tile row 45, col 25
    private final int airportWorldX = 25 * 48;
    private final int airportWorldY = 45 * 48;
    private final int airportRadius = 48 * 2;

    public Jeffrey(GamePanel gp) {
        super(gp);
        triggerRadius = gp.tileSize * 6;
        worldX = (gp.maxWorldCol / 2 + 6) * gp.tileSize;
        worldY = (gp.maxWorldRow / 2)      * gp.tileSize;

        dialogueLines = new String[]{
                "Hey! Over here!",
                "The name's Jeffrey. I couldn't help but notice...",
                "What is such a young kid like you doing? Wandering around alone...",
                "Not an adult in sight...",
                "I have a very special island nearby. Beautiful place, really...",
                "I think you'd love it...",
                "I know I do, I take all the special kiddos there...",
                "So... would you like to come?"
        };

        loadSprites();

        String basePath = "file:///" + System.getProperty("user.dir").replace("\\", "/");
        portrait = new Image(basePath + "/src/Resources/bigStien.png");
        if (portrait.isError()) System.out.println("Jeffrey portrait error: bigStien.png");
    }

    private void loadSprites() {
        String base = "file:///" + System.getProperty("user.dir").replace("\\", "/")
                + "/src/Resources/Jeff/";
        String[] directions = {
                "north", "north-east", "north-west",
                "east",  "west",
                "south", "south-east", "south-west"
        };
        for (String dir : directions) {
            Image img = new Image(base + dir + ".png");
            if (img.isError()) System.out.println("Jeffrey sprite error: " + dir + ".png");
            sprites.put(dir, img);
        }
    }

    public void startEscort() {
        escorting = true;
    }

    @Override
    public void setAction() {
        if (escorting) {
            // Walk toward airport
            int dx = airportWorldX - worldX;
            int dy = airportWorldY - worldY;
            double dist = Math.sqrt(dx * dx + dy * dy);
            setDirection(dx, dy);

            // Reached airport — trigger cutscene
            if (dist < airportRadius) {
                escorting = false;
                gp.cutsceneManager.startFadeToBlack(false);
            }
            return;
        }

        if (triggered) return;

        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < triggerRadius) {
            setDirection(dx, dy);
            if (dist < gp.tileSize * 1.5) {
                triggered     = true;
                gp.currentNPC = this;
                gp.gameState  = GameState.DIALOGUE;
            }
        } else {
            actionLockCounter++;
            if (actionLockCounter >= 120) {
                direction = switch (random.nextInt(4)) {
                    case 0 -> "north";
                    case 1 -> "south";
                    case 2 -> "east";
                    default -> "west";
                };
                actionLockCounter = 0;
            }
        }
    }

    private void setDirection(int dx, int dy) {
        int threshold = gp.tileSize / 3;
        boolean goNorth = dy < -threshold;
        boolean goSouth = dy >  threshold;
        boolean goEast  = dx >  threshold;
        boolean goWest  = dx < -threshold;

        if      (goNorth && goEast) direction = "north-east";
        else if (goNorth && goWest) direction = "north-west";
        else if (goSouth && goEast) direction = "south-east";
        else if (goSouth && goWest) direction = "south-west";
        else if (goNorth)           direction = "north";
        else if (goSouth)           direction = "south";
        else if (goEast)            direction = "east";
        else if (goWest)            direction = "west";
    }

    @Override
    public void draw(GraphicsContext gc) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (screenX + gp.tileSize < 0 || screenX > gp.screenWidth ||
                screenY + gp.tileSize < 0 || screenY > gp.screenHeight) return;

        Image current = sprites.get(direction);
        if (current != null && !current.isError()) {
            gc.drawImage(current, screenX, screenY, gp.tileSize, gp.tileSize);
        } else {
            gc.setFill(javafx.scene.paint.Color.CORNFLOWERBLUE);
            gc.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.fillText("J", screenX + 18, screenY + 30);
        }
    }
}
