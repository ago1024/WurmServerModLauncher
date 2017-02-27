package org.gotti.wurmunlimited.modsupport.properties;

public class Property {

	private long id;
	private long created;
	private long expires = Long.MAX_VALUE;
	
	private Long intValue;
	private String strValue;
	private Float numValue;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Long getIntValue() {
		return intValue;
	}
	public void setIntValue(Long intValue) {
		this.intValue = intValue;
	}
	public String getStrValue() {
		return strValue;
	}
	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}
	public Float getNumValue() {
		return numValue;
	}
	public void setNumValue(Float numValue) {
		this.numValue = numValue;
	}
	public long getCreated() {
		return created;
	}
	public void setCreated(long created) {
		this.created = created;
	}
	public long getExpires() {
		return expires;
	}
	public void setExpires(long expires) {
		this.expires = expires;
	}
}
