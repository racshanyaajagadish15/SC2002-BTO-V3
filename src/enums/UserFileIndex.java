package enums;
public enum UserFileIndex {
	NAME(0),
	NRIC(1),
	AGE(2),
	MARITAL_STATUS(3),
	PASSWORD(4);

	private final int index;

	UserFileIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
}