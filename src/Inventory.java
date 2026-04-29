import java.util.ArrayList;

public class Inventory {

    private final ArrayList<Item> items = new ArrayList<>();

    public void addItem(Item item) { items.add(item); }

    public boolean hasItem(String name) {
        return items.stream().anyMatch(i -> i.name.equals(name));
    }

    public void removeItem(String name) {
        items.removeIf(i -> i.name.equals(name));
    }

    public ArrayList<Item> getItems() { return items; }
}
