package enums;

/**
 * Enum representing the indices of user attributes in a user file.
 * Each enum constant corresponds to a specific attribute of a user.
 */

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