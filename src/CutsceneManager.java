import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CutsceneManager {

    GamePanel gp;

    public int scenePhase  = 0;
    private double fadeAlpha   = 0.0;
    private int frameCounter   = 0;
    private int planeLineIndex = 0;

    // Hit flash state
    private boolean hitSequence  = false;
    private int     flashCount   = 0;       // how many flashes completed
    private double  redAlpha     = 0.0;
    private boolean flashRising  = true;    // alpha going up or down
    private static final int MAX_FLASHES = 3;

    private final String[] planeLines = {
            "You board Jeffrey's private plane...",
            "The island is just a few hours away...",
            "You lean back and close your eyes...",
            "..."
    };

    public CutsceneManager(GamePanel gp) {
        this.gp = gp;
    }

    // Called by airport trigger (YES path)
    public void startFadeToBlack(boolean fromNoPath) {
        hitSequence    = false;
        scenePhase     = 0;
        fadeAlpha      = 0.0;
        frameCounter   = 0;
        planeLineIndex = 0;
        gp.gameState   = GameState.CUTSCENE;
    }

    // Called by thugs (NO path) — plays hit flashes first, then fades
    public void startHitSequence() {
        hitSequence   = true;
        scenePhase    = 0;  // phase 0 = flash phase
        flashCount    = 0;
        redAlpha      = 0.0;
        flashRising   = true;
        frameCounter  = 0;
        planeLineIndex = 0;
        gp.gameState  = GameState.CUTSCENE;
    }

    public void update() {
        frameCounter++;

        if (hitSequence && scenePhase == 0) {
            updateHitFlash();
            return;
        }

        switch (scenePhase) {
            case 0 -> {
                // Fade to black
                fadeAlpha = Math.min(1.0, fadeAlpha + 0.02);
                if (fadeAlpha >= 1.0) {
                    scenePhase   = 1;
                    frameCounter = 0;
                }
            }
            case 1 -> {
                if (frameCounter >= 180) {
                    frameCounter = 0;
                    planeLineIndex++;
                    if (planeLineIndex >= planeLines.length) {
                        scenePhase = 2;
                    }
                }
            }
            case 2 -> {
                if (frameCounter >= 120) {
                    scenePhase   = 3;
                    frameCounter = 0;
                }
            }
            case 3 -> {
                // Waits for E press in GamePanel.updateCutscene()
            }
        }
    }

    private void updateHitFlash() {
        // Each flash: alpha rises to 0.65 then falls back to 0
        double step = 0.065;
        if (flashRising) {
            redAlpha += step;
            if (redAlpha >= 0.65) {
                redAlpha    = 0.65;
                flashRising = false;
            }
        } else {
            redAlpha -= step;
            if (redAlpha <= 0.0) {
                redAlpha    = 0.0;
                flashRising = true;
                flashCount++;
            }
        }

        // All flashes done — transition to fade phase
        if (flashCount >= MAX_FLASHES) {
            hitSequence  = false;
            scenePhase   = 0;   // now runs the normal fade phase
            fadeAlpha    = 0.0;
            frameCounter = 0;
        }
    }

    public void draw(GraphicsContext gc) {
        int w = gp.screenWidth;
        int h = gp.screenHeight;

        if (hitSequence && scenePhase == 0) {
            drawHitFlash(gc, w, h);
            return;
        }

        switch (scenePhase) {
            case 0 -> {
                gc.setFill(Color.color(0, 0, 0, fadeAlpha));
                gc.fillRect(0, 0, w, h);
            }
            case 1 -> {
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, w, h);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.NORMAL, 22));
                if (planeLineIndex < planeLines.length)
                    drawCentered(gc, planeLines[planeLineIndex], h / 2);
                gc.setFill(Color.DARKGRAY);
                gc.setFont(Font.font("Arial", 14));
                drawCentered(gc, "[ wait... ]", h / 2 + 50);
            }
            case 2 -> {
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, w, h);
            }
            case 3 -> {
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, w, h);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                drawCentered(gc, "You wake up in a dark room...", h / 2 - 20);
                gc.setFont(Font.font("Arial", 16));
                gc.setFill(Color.LIGHTGRAY);
                drawCentered(gc, "[ E ] Continue", h / 2 + 30);
            }
        }
    }

    private void drawHitFlash(GraphicsContext gc, int w, int h) {
        // World still visible underneath
        if (redAlpha > 0) {
            gc.setFill(Color.color(0.8, 0, 0, redAlpha));
            gc.fillRect(0, 0, w, h);
        }

        // Show impact text on peak flash
        if (redAlpha >= 0.55) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 42));
            drawCentered(gc, "★  OW!  ★", h / 2 - 20);
        }
    }

    private void drawCentered(GraphicsContext gc, String text, int y) {
        double textWidth = text.length() * 12;
        gc.fillText(text, (gp.screenWidth - textWidth) / 2.0, y);
    }
}
