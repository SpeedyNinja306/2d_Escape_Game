import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Entity {

    public int worldX, worldY;
    public int speed;

    public Image sprite;

    public int screenX, screenY;

    // Direction the entity is facing — used for sprite selection later
    public String direction = "down";

    // Collision hitbox, relative to the entity's world position
    public Rectangle2D solidArea;
    public boolean collisionOn = false;

    public abstract void update();
    public abstract void draw(GraphicsContext gc);
}
