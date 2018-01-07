package library;

import java.util.TreeSet;

public final class Holder {  // refactor "Holder" to "Location" without inventory?

	// TODO builder vs static factory?
	public final static Holder none = new Holder("none"); // container for n/a items 


	private final String name;
	private final TreeSet<InventoryItem> inventory;


	public Holder(String name) {
		this.name = name;
		inventory = new TreeSet<InventoryItem>();
	}

	public String getName() {
		return name;
	}

	public TreeSet<InventoryItem> getInventory() {
		return inventory;		
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
		item.returnToOwner();
	}

	@Override
	public String toString() {
		return name; 
	}
}
