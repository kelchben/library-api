package library;

import java.util.TreeSet;

// TODO use composition (w reusable forwarding class?) instead of inheritance?! 
// TODO ---> MAKE THIS AN INTERFACE + AbstractInventoryItem | AbstractOwned, ...  (mixin)

/* You can, however, combine the advantages of interfaces and abstract classes
 * by providing an abstract skeletal implementation class to go with an interface
*/

// TODO "Don’t provide methods that modify the object’s state" --> holder anders loesen!
// TODO make this generic
public abstract class InventoryItem implements Comparable<InventoryItem> {

	protected static final TreeSet<InventoryItem> INDEX = new TreeSet<InventoryItem>();  // global list of all items

	protected final String id;  // for Comparable - books use isbn as id
	protected final Holder owner;
	protected Holder holder; // breaks immutability
	protected int durability; // breaks immutability... but necessary? use CountDownLatch?
	// TODO requests - more clarity then negative durabilty (which != requests)


	// TODO builder statt constructor
	public InventoryItem(String id, int durability, Holder owner) {
		this.id = id;
		this.owner = owner;
		this.holder = owner;
		this.durability = durability;
		holder.addToInventory(this);
		INDEX.add(this);
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

