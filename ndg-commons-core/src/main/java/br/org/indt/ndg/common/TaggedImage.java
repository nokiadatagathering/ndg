package br.org.indt.ndg.common;

public class TaggedImage {

	private String m_imageData = null;
	private String m_latitude = null;
	private String m_longitude = null;

	public String getImageData() {
		return m_imageData;
	}
	public void setImageData( String imageData ) {
		this.m_imageData = imageData;
	}
	public String getLatitude() {
		return m_latitude;
	}
	public void setLatitude( String latitude ) {
		this.m_latitude = latitude;
	}
	public String getLongitude() {
		return m_longitude;
	}
	public void setLongitude( String longitude ) {
		this.m_longitude = longitude;
	}
	public boolean hasGeoTag() {
		return ( m_latitude != null & m_longitude != null );
	}

}
