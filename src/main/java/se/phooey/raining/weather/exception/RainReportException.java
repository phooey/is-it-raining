package se.phooey.raining.weather.exception;

/**
 * Indicates that a WeatherProvider could not generate a RainReport
 */
public class RainReportException extends Exception {

	private static final long serialVersionUID = -9153819131915631598L;

	public RainReportException() {
        super();
    }

    public RainReportException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RainReportException(final String message) {
        super(message);
    }

    public RainReportException(final Throwable cause) {
        super(cause);
    }
}
