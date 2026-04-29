import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UIManager {

    GamePanel gp;
    public int choiceIndex = 0;
    public boolean showingChoice = false;

    // Box layout
    private final int BOX_X      = 40;
    private final int BOX_W      = 688;
    private final int BOX_H      = 150;
    private final int BOX_Y      = 576 - BOX_H - 20;

    // Portrait panel (sits inside the left side of the box)
    private final int PORT_PADDING = 10;
    private final int PORT_SIZE    = BOX_H - (PORT_PADDING * 2);  // square, fits inside box
    private final int PORT_X       = BOX_X + PORT_PADDING;
    private final int PORT_Y       = BOX_Y + PORT_PADDING;

    // Text starts after the portrait
    private final int TEXT_X       = PORT_X + PORT_SIZE + 16;
    private final int TEXT_W       = BOX_W - PORT_SIZE - PORT_PADDING * 3 - 16;

    public UIManager(GamePanel gp) {
        this.gp = gp;
    }

    public void drawDialogueBox(GraphicsContext gc, NPC npc, String text) {
        drawBox(gc);
        drawPortrait(gc, npc);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        drawWrappedText(gc, text, TEXT_X, BOX_Y + 40, TEXT_W);

        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Arial", 13));
        gc.fillText("[ E ] Continue", TEXT_X, BOX_Y + BOX_H - 15);
    }

    public void drawChoicePrompt(GraphicsContext gc, NPC npc, String question) {
        drawBox(gc);
        drawPortrait(gc, npc);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        gc.fillText(question, TEXT_X, BOX_Y + 40);

        String[] options = {"Yes", "No"};
        for (int i = 0; i < options.length; i++) {
            if (i == choiceIndex) {
                gc.setFill(Color.YELLOW);
                gc.fillText("> " + options[i], TEXT_X, BOX_Y + 75 + i * 32);
            } else {
                gc.setFill(Color.WHITE);
                gc.fillText("  " + options[i], TEXT_X, BOX_Y + 75 + i * 32);
            }
        }
    }

    private void drawPortrait(GraphicsContext gc, NPC npc) {
        // Portrait background
        gc.setFill(Color.color(0, 0, 0, 0.6));
        gc.fillRoundRect(PORT_X, PORT_Y, PORT_SIZE, PORT_SIZE, 8, 8);
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(PORT_X, PORT_Y, PORT_SIZE, PORT_SIZE, 8, 8);

        if (npc != null && npc.portrait != null && !npc.portrait.isError()) {
            double imgW = npc.portrait.getWidth();
            double imgH = npc.portrait.getHeight();

            // Scale to fit inside PORT_SIZE x PORT_SIZE while preserving aspect ratio
            double scale = Math.min(PORT_SIZE / imgW, PORT_SIZE / imgH);
            double drawW = imgW * scale;
            double drawH = imgH * scale;

            // Center within the portrait box
            double drawX = PORT_X + (PORT_SIZE - drawW) / 2.0;
            double drawY = PORT_Y + (PORT_SIZE - drawH) / 2.0;

            gc.drawImage(npc.portrait, drawX, drawY, drawW, drawH);
        } else {
            gc.setFill(Color.LIGHTGRAY);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 40));
            gc.fillText("?", PORT_X + PORT_SIZE / 2 - 12, PORT_Y + PORT_SIZE / 2 + 14);
        }
    }


    // Basic word wrap — prevents text overflowing outside the box
    private void drawWrappedText(GraphicsContext gc, String text, int x, int y, int maxWidth) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineHeight = 24;
        int currentY = y;

        for (String word : words) {
            String test = line.isEmpty() ? word : line + " " + word;
            if (test.length() * 10 > maxWidth) {
                gc.fillText(line.toString(), x, currentY);
                line = new StringBuilder(word);
                currentY += lineHeight;
            } else {
                line = new StringBuilder(test);
            }
        }
        if (!line.isEmpty()) gc.fillText(line.toString(), x, currentY);
    }

    private void drawBox(GraphicsContext gc) {
        gc.setFill(Color.color(0, 0, 0, 0.85));
        gc.fillRoundRect(BOX_X, BOX_Y, BOX_W, BOX_H, 16, 16);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRoundRect(BOX_X, BOX_Y, BOX_W, BOX_H, 16, 16);
    }
}
