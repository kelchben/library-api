package library;

public abstract class InventoryItem implements Comparable<InventoryItem> {
	protected String id;  // for comparing items -- book: isbn -> id 
	protected Holder holder;
	protected Holder owner;
	protected int durability; // item health: 0 means "broken"

	public InventoryItem(String id, int durability, Holder holder) {
		this.id = id;
		this.owner = holder;
		this.holder = holder;
		this.durability = durability;
		holder.addToInventory(this);
	}

	protected void moveTo(Holder nextHolder) {
			nextHolder.addToInventory(this);  
			this.holder.removeFromInventory(this);
			holder = nextHolder;
	}

	@Override
	public int compareTo(InventoryItem otherItem) {
		return this.id.compareTo(otherItem.id);
	}

}

