/*
*  Nokia Data Gathering
*
*  Copyright (C) 2011 Nokia Corporation
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either
*  version 2.1 of the License, or (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/
*/

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
