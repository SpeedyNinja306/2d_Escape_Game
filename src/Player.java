import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player extends Entity {

    GamePanel  gp;
    KeyHandler keyH;
    public Inventory inventory = new Inventory();

    private int cameraWorldX, cameraWorldY;
    private final int screenCenterX, screenCenterY;
    private final int deadZone;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp   = gp;
        this.keyH = keyH;

        screenCenterX = (gp.screenWidth  / 2) - (gp.tileSize / 2);
        screenCenterY = (gp.screenHeight / 2) - (gp.tileSize / 2);

        worldX = (gp.maxWorldCol / 2) * gp.tileSize;
        worldY = (gp.maxWorldRow / 2) * gp.tileSize;

        cameraWorldX = worldX;
        cameraWorldY = worldY;
        screenX      = screenCenterX;
        screenY      = screenCenterY;

        deadZone  = gp.tileSize * 2;
        speed     = 4;
        solidArea = new Rectangle2D(4, 4, 24, 24);

        String basePath = "file:///" + System.getProperty("user.dir").replace("\\", "/");
        sprite = new Image(basePath + "/src/Resources/mainChar.png");
    }

    @Override
    public void update() {
        if (gp.keyH.upPressed) {
            direction = "up"; collisionOn = false;
            gp.collisionChecker.checkTile(this);
            gp.collisionChecker.checkDoor(this);
            if (!collisionOn) worldY -= speed;
        }
        if (gp.keyH.downPressed) {
            direction = "down"; collisionOn = false;
            gp.collisionChecker.checkTile(this);
            gp.collisionChecker.checkDoor(this);
            if (!collisionOn) worldY += speed;
        }
        if (gp.keyH.leftPressed) {
            direction = "left"; collisionOn = false;
            gp.collisionChecker.checkTile(this);
            gp.collisionChecker.checkDoor(this);
            if (!collisionOn) worldX -= speed;
        }
        if (gp.keyH.rightPressed) {
            direction = "right"; collisionOn = false;
            gp.collisionChecker.checkTile(this);
            gp.collisionChecker.checkDoor(this);
            if (!collisionOn) worldX += speed;
        }

        clampToWorld();
        updateCamera();
    }

    // Interact with nearby items and doors — called from GamePanel on E press
    public void interact() {
        if (gp.currentRoom == null) return;

        // Items first
        for (Item item : gp.currentRoom.items) {
            if (!item.pickedUp) {
                int dx = Math.abs(worldX - item.worldX);
                int dy = Math.abs(worldY - item.worldY);
                if (dx < gp.tileSize * 1.5 && dy < gp.tileSize * 1.5) {
                    item.onPickup(this);
                    return;
                }
            }
        }

        // Doors
        for (Door door : gp.currentRoom.doors) {
            int dx = Math.abs(worldX - door.worldX);
            int dy = Math.abs(worldY - door.worldY);
            if (dx < gp.tileSize * 1.5 && dy < gp.tileSize * 1.5) {
                door.interact(this);
                return;
            }
        }
    }

    // Teleports player and resets camera — used for room transitions
    public void teleportTo(int x, int y) {
        worldX       = x;
        worldY       = y;
        cameraWorldX = x;
        cameraWorldY = y;
        screenX      = screenCenterX;
        screenY      = screenCenterY;
    }

    public void followEntity(Entity target) {
        int dx   = target.worldX - worldX;
        int dy   = target.worldY - worldY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        int followGap = gp.tileSize * 2;

        if (dist > followGap) {
            if (Math.abs(dx) >= Math.abs(dy)) {
                direction   = dx > 0 ? "right" : "left";
                collisionOn = false;
                gp.collisionChecker.checkTile(this);
                if (!collisionOn) worldX += dx > 0 ? speed : -speed;
            } else {
                direction   = dy > 0 ? "down" : "up";
                collisionOn = false;
                gp.collisionChecker.checkTile(this);
                if (!collisionOn) worldY += dy > 0 ? speed : -speed;
            }
        }

        clampToWorld();
        updateCamera();
    }

    private void clampToWorld() {
        int maxX = (gp.currentRoom != null ? gp.currentRoom.mapCols : gp.maxWorldCol) * gp.tileSize - gp.tileSize;
        int maxY = (gp.currentRoom != null ? gp.currentRoom.mapRows : gp.maxWorldRow) * gp.tileSize - gp.tileSize;
        worldX = Math.max(0, Math.min(maxX, worldX));
        worldY = Math.max(0, Math.min(maxY, worldY));
    }

    private void updateCamera() {
        int dx = worldX - cameraWorldX;
        int dy = worldY - cameraWorldY;

        if (dx >  deadZone) cameraWorldX = worldX - deadZone;
        if (dx < -deadZone) cameraWorldX = worldX + deadZone;
        if (dy >  deadZone) cameraWorldY = worldY - deadZone;
        if (dy < -deadZone) cameraWorldY = worldY + deadZone;

        screenX = screenCenterX + (worldX - cameraWorldX);
        screenY = screenCenterY + (worldY - cameraWorldY);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.drawImage(sprite, screenX, screenY, gp.tileSize, gp.tileSize);
    }
}
