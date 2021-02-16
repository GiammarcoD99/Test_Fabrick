package dto;

public class TransactionTypeObj {
	
	private String enumeration;
	private String value;
	
	
	@Override
	public String toString() {
		return "[ \n"
				+ "\t  enumeration = " + enumeration + ",\n"
				+ "\t  value = " + value+"\n"
				+"\t ]";
	}


	public String getEnumeration() {
		return enumeration;
	}
	
	public void setEnumeration(String enumeration) {
		this.enumeration = enumeration;
	}

	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
