import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class Item {

    public String  name;
    public int     worldX, worldY;
    public Image   sprite;
    public boolean pickedUp = false;

    public abstract void onPickup(Player player);

    public void draw(GraphicsContext gc, GamePanel gp) {
        if (pickedUp) return;

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (screenX + gp.tileSize < 0 || screenX > gp.screenWidth ||
                screenY + gp.tileSize < 0 || screenY > gp.screenHeight) return;

        if (sprite != null && !sprite.isError()) {
            gc.drawImage(sprite, screenX, screenY, gp.tileSize, gp.tileSize);
        } else {
            // Placeholder — gold rounded square with first letter
            gc.setFill(Color.GOLD);
            gc.fillRoundRect(screenX + 8, screenY + 8, 32, 32, 10, 10);
            gc.setFill(Color.DARKGOLDENROD);
            gc.setFont(Font.font("Arial", 14));
            gc.fillText(name.substring(0, 1), screenX + 19, screenY + 30);
        }
    }
}
