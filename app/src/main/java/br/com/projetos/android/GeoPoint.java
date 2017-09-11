package br.com.projetos.android;

import java.io.Serializable;

public class GeoPoint implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3427795577733670103L;

	public static enum GeoPointType{APPROXIMATE, EXACT};
	private Double latitude;
	private Double longitude;
	private Double latAprox;
	private Double lngAprox;
	private GeoPointType geoPointType;
	
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public GeoPointType getGeoPointType() {
		return geoPointType;
	}
	
	public void setGeoPointType(GeoPointType geoPointType) {
		this.geoPointType = geoPointType;
	}
	
	public Double getLatAprox() {
		return latAprox;
	}

	public void setLatAprox(Double latAprox) {
		this.latAprox = latAprox;
	}

	public Double getLngAprox() {
		return lngAprox;
	}

	public void setLngAprox(Double lngAprox) {
		this.lngAprox = lngAprox;
	}

	public GeoPoint(){
		super();
	}

	public GeoPoint(Double latitude, Double longitude, GeoPointType geoPointType) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.geoPointType = geoPointType;
	}


    @Override
    public String toString() {
        return String.valueOf(latitude) + " x " + String.valueOf(longitude);
    }
}
