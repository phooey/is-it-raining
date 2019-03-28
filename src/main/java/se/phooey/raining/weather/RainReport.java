package se.phooey.raining.weather;

import java.util.Locale;

/**
 * Data class representing a weather report for a specific location providing
 * information about if it is raining currently and the current probability of
 * rain today.
 */
public class RainReport {

	private double latitude;
	private double longitude;
	private String rainingCurrently;
	private double chanceOfRainToday;

	/**
	 * 
	 * @param latitude          the latitude of the location the rain report refers
	 *                          to
	 * @param longitude         the longitude of the location the rain report refers
	 *                          to
	 * @param rainingCurrently  human readable information about if it is raining
	 *                          currently at the location or not
	 * @param chanceOfRainToday a number representing the probability of rain at the
	 *                          location today
	 */
	public RainReport(double latitude, double longitude, String rainingCurrently, double chanceOfRainToday) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.rainingCurrently = rainingCurrently;
		this.chanceOfRainToday = chanceOfRainToday;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getRainingCurrently() {
		return rainingCurrently;
	}

	public void setRainingCurrently(String rainingCurrently) {
		this.rainingCurrently = rainingCurrently;
	}

	public double getChanceOfRainToday() {
		return chanceOfRainToday;
	}

	public void setChanceOfRainToday(double chanceOfRainToday) {
		this.chanceOfRainToday = chanceOfRainToday;
	}

	@Override
	public final int hashCode() {
		int result = 17;
		result = 31 * result + Double.valueOf(latitude).hashCode();
		result = 31 * result + Double.valueOf(longitude).hashCode();
		result = 31 * result + Double.valueOf(chanceOfRainToday).hashCode();
		if (rainingCurrently != null) {
			result = 31 * result + rainingCurrently.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (!(o instanceof RainReport)) {
			return false;
		}
		RainReport other = (RainReport) o;
		return (other.getLatitude() == this.latitude) && (other.getLongitude() == this.longitude)
				&& (other.getRainingCurrently() != null) && (other.getRainingCurrently().equals(this.rainingCurrently)
						&& (other.getChanceOfRainToday() == this.chanceOfRainToday));
	}

	@Override
	public String toString() {
		return String.format(Locale.US, "latitude: %f%nlongitude: %f%nrainingCurrently: %s%nchanceOfRainToday: %f",
				latitude, longitude, rainingCurrently, chanceOfRainToday);
	}

}
