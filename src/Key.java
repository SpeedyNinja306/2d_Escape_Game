public class Key extends Item {

    public Key(int worldX, int worldY) {
        this.name   = "Key";
        this.worldX = worldX;
        this.worldY = worldY;
    }

    @Override
    public void onPickup(Player player) {
        pickedUp = true;
        player.inventory.addItem(this);
        System.out.println("Picked up: " + name);
    }
}
