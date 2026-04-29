import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;

public class GamePanel {

    final int tileSize     = 32;
    final int maxScreenCol = 24;
    final int maxScreenRow = 18;
    final int screenWidth  = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;
    final int maxWorldCol  = 50;
    final int maxWorldRow  = 50;
    final int FPS          = 60;

    public GameState gameState = GameState.PLAYING;

    public KeyHandler      keyH;
    public CollisionChecker collisionChecker;
    public UIManager        uiManager;
    public CutsceneManager  cutsceneManager;

    Canvas      canvas = new Canvas(screenWidth, screenHeight);
    TileManager tileM;
    Player      player;

    public ArrayList<NPC> npcs           = new ArrayList<>();
    public NPC            currentNPC     = null;
    public int            dialogueLineIndex = 0;
    public Room           currentRoom    = null; // null = overworld

    public GamePanel() {
        keyH             = new KeyHandler();
        collisionChecker = new CollisionChecker(this);
        uiManager        = new UIManager(this);
        cutsceneManager  = new CutsceneManager(this);
        tileM            = new TileManager(this);
        player           = new Player(this, keyH);

        npcs.add(new Jeffrey(this));
    }

    public void setupScene(Stage stage) {
        Pane  root  = new Pane(canvas);
        Scene scene = new Scene(root, screenWidth, screenHeight);

        scene.setOnKeyPressed(e  -> keyH.handlePressed(e.getCode()));
        scene.setOnKeyReleased(e -> keyH.handleReleased(e.getCode()));

        stage.setTitle("My Game");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
        canvas.requestFocus();
    }

    public void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = 0;
            private final long interval = 1_000_000_000L / FPS;

            @Override
            public void handle(long now) {
                if (now - lastTime >= interval) {
                    update();
                    draw(canvas.getGraphicsContext2D());
                    lastTime = now;
                }
            }
        };
        timer.start();
    }

    public void update() {
        switch (gameState) {
            case PLAYING -> {
                player.update();
                if (keyH.interactJustPressed) player.interact();

                if (currentRoom != null) {
                    currentRoom.update();
                    checkRoomExit();
                } else {
                    for (NPC npc : npcs) npc.update();
                }
            }
            case ESCORT -> {
                for (NPC npc : npcs) {
                    if (npc instanceof Jeffrey j) {
                        player.followEntity(j);
                        break;
                    }
                }
                for (NPC npc : npcs) npc.update();
            }
            case DIALOGUE  -> updateDialogue();
            case CUTSCENE  -> updateCutscene();
            case PAUSED    -> {}
            case WIN       -> {}
        }
        keyH.flushJustPressed();
    }

    // Detects when player walks into an open door — advances to next room
    private void checkRoomExit() {
        if (currentRoom == null) return;
        for (Door door : currentRoom.doors) {
            if (!door.open) continue;
            int dx = Math.abs(player.worldX - door.worldX);
            int dy = Math.abs(player.worldY - door.worldY);
            if (dx < tileSize && dy < tileSize) {
                advanceRoom();
                return;
            }
        }
    }

    private void advanceRoom() {
        int nextId = currentRoom.roomId + 1;
        if (nextId > 7) {
            gameState = GameState.WIN;
        } else {
            currentRoom = new Room(this, nextId);
            player.teleportTo(currentRoom.playerSpawnX, currentRoom.playerSpawnY);
            System.out.println("Entered room " + nextId);
        }
    }

    private void updateDialogue() {
        if (currentNPC == null) return;

        if (uiManager.showingChoice) {
            if (keyH.upJustPressed)   uiManager.choiceIndex = 0;
            if (keyH.downJustPressed) uiManager.choiceIndex = 1;

            if (keyH.interactJustPressed) {
                if (uiManager.choiceIndex == 0) {
                    // YES
                    uiManager.showingChoice = false;
                    uiManager.choiceIndex   = 0;
                    dialogueLineIndex       = 0;
                    currentNPC              = null;
                    gameState               = GameState.ESCORT;
                    for (NPC npc : npcs) {
                        if (npc instanceof Jeffrey j) j.startEscort();
                    }
                } else {
                    // NO
                    uiManager.showingChoice = false;
                    uiManager.choiceIndex   = 0;
                    dialogueLineIndex       = 0;
                    currentNPC              = null;
                    gameState               = GameState.PLAYING;
                    Thug.resetTrigger();
                    npcs.add(new Thug(this, player.worldX - tileSize * 4, player.worldY));
                    npcs.add(new Thug(this, player.worldX + tileSize * 4, player.worldY));
                }
            }
        } else {
            if (keyH.interactJustPressed) {
                dialogueLineIndex++;
                if (dialogueLineIndex >= currentNPC.dialogueLines.length) {
                    dialogueLineIndex       = 0;
                    uiManager.showingChoice = true;
                }
            }
        }
    }

    private void updateCutscene() {
        cutsceneManager.update();

        // Phase 3 — E to enter dungeon
        if (cutsceneManager.scenePhase == 3 && keyH.interactJustPressed) {
            currentRoom = new Room(this, 1);
            player.teleportTo(currentRoom.playerSpawnX, currentRoom.playerSpawnY);
            gameState   = GameState.PLAYING;
        }
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        switch (gameState) {
            case PLAYING -> {
                if (currentRoom != null) {
                    currentRoom.draw(gc);
                } else {
                    tileM.draw(gc);
                    for (NPC npc : npcs) npc.draw(gc);
                }
                player.draw(gc);
            }
            case ESCORT -> {
                tileM.draw(gc);
                for (NPC npc : npcs) npc.draw(gc);
                player.draw(gc);
            }
            case DIALOGUE -> {
                tileM.draw(gc);
                for (NPC npc : npcs) npc.draw(gc);
                player.draw(gc);
                if (currentNPC != null) {
                    if (uiManager.showingChoice) {
                        uiManager.drawChoicePrompt(gc, currentNPC, "Would you like to come to my island?");
                    } else {
                        uiManager.drawDialogueBox(gc, currentNPC, currentNPC.dialogueLines[dialogueLineIndex]);
                    }
                }
            }
            case CUTSCENE -> {
                if (cutsceneManager.scenePhase == 0) {
                    if (currentRoom != null) currentRoom.draw(gc);
                    else {
                        tileM.draw(gc);
                        for (NPC npc : npcs) npc.draw(gc);
                    }
                    player.draw(gc);
                }
                cutsceneManager.draw(gc);
            }
            case PAUSED -> {
                if (currentRoom != null) currentRoom.draw(gc);
                else tileM.draw(gc);
                player.draw(gc);
            }
            case WIN -> {
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, screenWidth, screenHeight);
                gc.setFill(Color.WHITE);
                gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 32));
                gc.fillText("You escaped!", screenWidth / 2.0 - 100, screenHeight / 2.0);
            }
        }
    }
}
