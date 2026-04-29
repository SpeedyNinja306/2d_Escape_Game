public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        if (gp.currentRoom != null) {
            checkRoomTile(entity);
        } else {
            checkOverworldTile(entity);
        }
    }

    private void checkOverworldTile(Entity entity) {
        int solidLeft   = entity.worldX + (int) entity.solidArea.getMinX();
        int solidRight  = solidLeft     + (int) entity.solidArea.getWidth()  - 1;
        int solidTop    = entity.worldY + (int) entity.solidArea.getMinY();
        int solidBottom = solidTop      + (int) entity.solidArea.getHeight() - 1;

        int leftCol   = solidLeft   / gp.tileSize;
        int rightCol  = solidRight  / gp.tileSize;
        int topRow    = solidTop    / gp.tileSize;
        int bottomRow = solidBottom / gp.tileSize;

        switch (entity.direction) {
            case "up", "north" -> {
                topRow = (solidTop - entity.speed) / gp.tileSize;
                if (overworldTileSolid(topRow, leftCol) || overworldTileSolid(topRow, rightCol))
                    entity.collisionOn = true;
            }
            case "down", "south" -> {
                bottomRow = (solidBottom + entity.speed) / gp.tileSize;
                if (overworldTileSolid(bottomRow, leftCol) || overworldTileSolid(bottomRow, rightCol))
                    entity.collisionOn = true;
            }
            case "left", "west" -> {
                leftCol = (solidLeft - entity.speed) / gp.tileSize;
                if (overworldTileSolid(topRow, leftCol) || overworldTileSolid(bottomRow, leftCol))
                    entity.collisionOn = true;
            }
            case "right", "east" -> {
                rightCol = (solidRight + entity.speed) / gp.tileSize;
                if (overworldTileSolid(topRow, rightCol) || overworldTileSolid(bottomRow, rightCol))
                    entity.collisionOn = true;
            }
        }
    }

    private boolean overworldTileSolid(int row, int col) {
        row = Math.max(0, Math.min(gp.maxWorldRow - 1, row));
        col = Math.max(0, Math.min(gp.maxWorldCol - 1, col));
        return gp.tileM.tiles[gp.tileM.mapTileNum[row][col]].collision;
    }

    private void checkRoomTile(Entity entity) {
        Room room = gp.currentRoom;

        int solidLeft   = entity.worldX + (int) entity.solidArea.getMinX();
        int solidRight  = solidLeft     + (int) entity.solidArea.getWidth()  - 1;
        int solidTop    = entity.worldY + (int) entity.solidArea.getMinY();
        int solidBottom = solidTop      + (int) entity.solidArea.getHeight() - 1;

        int leftCol   = solidLeft   / gp.tileSize;
        int rightCol  = solidRight  / gp.tileSize;
        int topRow    = solidTop    / gp.tileSize;
        int bottomRow = solidBottom / gp.tileSize;

        switch (entity.direction) {
            case "up", "north" -> {
                topRow = (solidTop - entity.speed) / gp.tileSize;
                if (roomTileSolid(room, topRow, leftCol) || roomTileSolid(room, topRow, rightCol))
                    entity.collisionOn = true;
            }
            case "down", "south" -> {
                bottomRow = (solidBottom + entity.speed) / gp.tileSize;
                if (roomTileSolid(room, bottomRow, leftCol) || roomTileSolid(room, bottomRow, rightCol))
                    entity.collisionOn = true;
            }
            case "left", "west" -> {
                leftCol = (solidLeft - entity.speed) / gp.tileSize;
                if (roomTileSolid(room, topRow, leftCol) || roomTileSolid(room, bottomRow, leftCol))
                    entity.collisionOn = true;
            }
            case "right", "east" -> {
                rightCol = (solidRight + entity.speed) / gp.tileSize;
                if (roomTileSolid(room, topRow, rightCol) || roomTileSolid(room, bottomRow, rightCol))
                    entity.collisionOn = true;
            }
        }
    }

    private boolean roomTileSolid(Room room, int row, int col) {
        row = Math.max(0, Math.min(room.mapRows - 1, row));
        col = Math.max(0, Math.min(room.mapCols - 1, col));
        return gp.tileM.tiles[room.mapTileNum[row][col]].collision;
    }

    // Called after tile check in Player.update() for dungeon doors
    public void checkDoor(Entity entity) {
        if (gp.currentRoom == null) return;

        int solidLeft   = entity.worldX + (int) entity.solidArea.getMinX();
        int solidRight  = solidLeft     + (int) entity.solidArea.getWidth();
        int solidTop    = entity.worldY + (int) entity.solidArea.getMinY();
        int solidBottom = solidTop      + (int) entity.solidArea.getHeight();

        switch (entity.direction) {
            case "up"    -> solidTop    -= entity.speed;
            case "down"  -> solidBottom += entity.speed;
            case "left"  -> solidLeft   -= entity.speed;
            case "right" -> solidRight  += entity.speed;
        }

        for (Door door : gp.currentRoom.doors) {
            if (door.open) continue;
            if (solidLeft   < door.worldX + gp.tileSize &&
                    solidRight  > door.worldX &&
                    solidTop    < door.worldY + gp.tileSize &&
                    solidBottom > door.worldY) {
                entity.collisionOn = true;
            }
        }
    }
}
