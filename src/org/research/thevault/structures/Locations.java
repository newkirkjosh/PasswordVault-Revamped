package org.research.thevault.structures;

public class Locations {

	private String latitude;
	private String longitude;
	private String address;
	
	public Locations( String latitude, String longitude, String address )
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
	}
	
	public String getLatitude()
	{
		return latitude;
	}
	
	public void setLatitude( String latitude )
	{
		this.latitude = latitude;
	}
	
	public String getLongitude()
	{
		return longitude;
	}
	
	public void setLongitude( String longitude )
	{
		this.longitude = longitude;
	}
	
	public String getAddress()
	{
		return address;
	}
	
	public void setAddress( String address )
	{
		this.address = address;
	}
}
