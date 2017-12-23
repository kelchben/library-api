package library;

public interface Borrowable {
	void borrowTo(Holder nextHolder);
	void returnToOwner();
}
