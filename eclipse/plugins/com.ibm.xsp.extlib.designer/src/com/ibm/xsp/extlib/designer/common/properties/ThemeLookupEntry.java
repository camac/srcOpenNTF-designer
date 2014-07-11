package com.ibm.xsp.extlib.designer.common.properties;

public class ThemeLookupEntry {

	private String code;
	private String label;
	
	public ThemeLookupEntry(String code, String label) {
		this.code = code;
		this.label = label;
	}
	
	public String getCode() {
		return code;
	}
	public String getLabel() {
		return label;
	}	
	
}
