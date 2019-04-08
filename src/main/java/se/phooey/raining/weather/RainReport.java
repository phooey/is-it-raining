package se.phooey.raining.weather;

import java.util.Locale;

/**
 * Data class representing a weather report for a specific location providing
 * information about if there is precipitation occurring currently and the
 * current probability of precipitation occurring today.
 */
public class RainReport {

	private double latitude;
	private double longitude;
	private String currentPrecipitation;
	private double currentProbability;
	private double currentIntensity;
	private double chanceOfPrecipitationToday;
	private String typeOfPrecipitationToday;

	/**
	 * Creates a new RainReport based on the passed parameters
	 * 
	 * @param latitude                   the latitude of the location the rain
	 *                                   report refers to
	 * @param longitude                  the longitude of the location the rain
	 *                                   report refers to
	 * @param currentPrecipitation       the type of precipitation currently
	 *                                   occurring at the location
	 * @param currentProbability         the probability of precipitation occurring
	 *                                   at the current time
	 * @param currentIntensity           the intensity of the precipitation
	 *                                   currently occurring, in inches/hour
	 * @param chanceOfPrecipitationToday the chance that there precipitation will
	 *                                   occur at the location today
	 * @param typeOfPrecipitationToday   the type of precipitation that is expected
	 *                                   to occur today
	 */
	public RainReport(double latitude, double longitude, String currentPrecipitation, double currentProbability,
			double currentIntensity, double chanceOfPrecipitationToday, String typeOfPrecipitationToday) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.currentPrecipitation = currentPrecipitation;
		this.currentProbability = currentProbability;
		this.currentIntensity = currentIntensity;
		this.chanceOfPrecipitationToday = chanceOfPrecipitationToday;
		this.typeOfPrecipitationToday = typeOfPrecipitationToday;
	}

	/**
	 * Creates a new RainReport with default values; latitude = 0, longitude = 0
	 * (Null Island), currentPrecipitation = "unknown", currentProbability = -1,
	 * currentIntensity = -1, chanceOfPrecipitationToday = -1,
	 * typeOfPrecipitationToday = "unknown"
	 */
	public RainReport() {
		this.latitude = 0;
		this.longitude = 0;
		this.currentPrecipitation = Precipitation.UNKNOWN.toString();
		this.currentProbability = -1;
		this.currentIntensity = -1;
		this.chanceOfPrecipitationToday = -1;
		this.typeOfPrecipitationToday = Precipitation.UNKNOWN.toString();
	}

	@Override
	public final int hashCode() {
		int result = 17;
		result = 31 * result + Double.valueOf(latitude).hashCode();
		result = 31 * result + Double.valueOf(longitude).hashCode();
		result = 31 * result + Double.valueOf(currentProbability).hashCode();
		result = 31 * result + Double.valueOf(currentIntensity).hashCode();
		result = 31 * result + Double.valueOf(chanceOfPrecipitationToday).hashCode();
		if (currentPrecipitation != null) {
			result = 31 * result + currentPrecipitation.hashCode();
		}
		if (typeOfPrecipitationToday != null) {
			result = 31 * result + typeOfPrecipitationToday.hashCode();
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
				&& ((other.getCurrentPrecipitation() != null)
						&& (other.getCurrentPrecipitation().equals(this.currentPrecipitation)))
				&& (other.getCurrentProbability() == this.currentProbability)
				&& (other.getCurrentIntensity() == this.currentIntensity)
				&& ((other.getTypeOfPrecipitationToday() != null)
						&& other.getTypeOfPrecipitationToday().equals(this.typeOfPrecipitationToday))
				&& (other.getChanceOfPrecipitationToday() == this.chanceOfPrecipitationToday);
	}

	@Override
	public String toString() {
		return String.format(Locale.US,
				"latitude: %f%n" + "longitude: %f%n" + "currentPrecipitation: %s%n" + "currentProbability: %f%n"
						+ "currentIntensity: %f%n" + "chanceOfPrecipitationToday: %f%n"
						+ "typeOfPrecipitationToday: %s%n",
				latitude, longitude, currentPrecipitation, currentProbability, currentIntensity,
				chanceOfPrecipitationToday, typeOfPrecipitationToday);
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

	public String getCurrentPrecipitation() {
		return currentPrecipitation;
	}

	public void setCurrentPrecipitation(String currentPrecipitation) {
		this.currentPrecipitation = currentPrecipitation;
	}

	public double getCurrentProbability() {
		return currentProbability;
	}

	public void setCurrentProbability(double currentProbability) {
		this.currentProbability = currentProbability;
	}

	public double getCurrentIntensity() {
		return currentIntensity;
	}

	public void setCurrentIntensity(double currentIntensity) {
		this.currentIntensity = currentIntensity;
	}

	public double getChanceOfPrecipitationToday() {
		return chanceOfPrecipitationToday;
	}

	public void setChanceOfPrecipitationToday(double chanceOfPrecipitationToday) {
		this.chanceOfPrecipitationToday = chanceOfPrecipitationToday;
	}

	public String getTypeOfPrecipitationToday() {
		return typeOfPrecipitationToday;
	}

	public void setTypeOfPrecipitationToday(String typeOfPrecipitationToday) {
		this.typeOfPrecipitationToday = typeOfPrecipitationToday;
	}

}
