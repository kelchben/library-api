package library;

import java.util.TreeSet;

public class Owner {  // refactor "Owner" to "Location" without inventory?
	private String name;
	private TreeSet<InventoryItem> inventory;

	public Owner(String name) {
		this.name = name;
		inventory = new TreeSet<InventoryItem>();
	}

	public String getName() {
		return name;
	}

	public boolean removeFromInventory(InventoryItem item) {
		return inventory.remove(item);
	}

	public boolean addToInventory(InventoryItem item) {
		return inventory.add(item);
	}

	public void borrowItem(Borrowable item) {
		item.borrowTo(this);
	}

	public void returnItem(Borrowable item) {
		item.returnTo(Main.LIBRARY);
	}

	@Override
	public String toString() {
		return name;
	}
}
