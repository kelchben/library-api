package library;

public abstract class InventoryItem implements Comparable<InventoryItem> {
	protected String id;  // for comparing items -- book: isbn -> id 
	protected Owner owner;
	protected int durability; // item health: 0 means "broken"

	public InventoryItem(String id, int durability, Owner owner) {
		this.id = id;
		this.owner = owner;
		this.durability = durability;
		owner.addToInventory(this);
	}

	@Override
	public int compareTo(InventoryItem otherItem) {
		return this.id.compareTo(otherItem.id);
	}

}

