import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public abstract class NPC extends Entity {

    GamePanel gp;
    public String[] dialogueLines;
    public Image portrait; // each subclass sets this
    int actionLockCounter = 0;

    public NPC(GamePanel gp) {
        this.gp = gp;
        solidArea = new Rectangle2D(4, 4, 24, 24);
        direction = "south";
        speed = 2;
    }

    public abstract void setAction();

    @Override
    public void update() {
        setAction();

        collisionOn = false;
        gp.collisionChecker.checkTile(this);

        if (!collisionOn) {
            switch (direction) {
                case "north"      -> worldY -= speed;
                case "south"      -> worldY += speed;
                case "west"       -> worldX -= speed;
                case "east"       -> worldX += speed;
                case "north-east" -> { worldY -= speed; worldX += speed; }
                case "north-west" -> { worldY -= speed; worldX -= speed; }
                case "south-east" -> { worldY += speed; worldX += speed; }
                case "south-west" -> { worldY += speed; worldX -= speed; }
            }
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (screenX + gp.tileSize < 0 || screenX > gp.screenWidth ||
                screenY + gp.tileSize < 0 || screenY > gp.screenHeight) return;

        if (sprite != null) {
            gc.drawImage(sprite, screenX, screenY, gp.tileSize, gp.tileSize);
        } else {
            gc.setFill(Color.CORNFLOWERBLUE);
            gc.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            gc.setFill(Color.WHITE);
            gc.fillText("J", screenX + 18, screenY + 30);
        }
    }
}
