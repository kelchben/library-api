package library;

public interface Borrowable {
	void borrowTo(Owner nextOwner);
	void returnTo(Owner nextOwner);
}
