import javafx.scene.input.KeyCode;

public class KeyHandler {

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean interactPressed;
    public boolean upJustPressed, downJustPressed, interactJustPressed;

    public void handlePressed(KeyCode code) {
        switch (code) {
            case W, UP    -> { upPressed = true;    upJustPressed = true;       }
            case S, DOWN  -> { downPressed = true;  downJustPressed = true;     }
            case A, LEFT  -> leftPressed = true;
            case D, RIGHT -> rightPressed = true;
            case E, ENTER -> { interactPressed = true; interactJustPressed = true; }
        }
    }

    public void handleReleased(KeyCode code) {
        switch (code) {
            case W, UP    -> upPressed = false;
            case S, DOWN  -> downPressed = false;
            case A, LEFT  -> leftPressed = false;
            case D, RIGHT -> rightPressed = false;
            case E, ENTER -> interactPressed = false;
        }
    }

    public void flushJustPressed() {
        upJustPressed        = false;
        downJustPressed      = false;
        interactJustPressed  = false;
    }
}
