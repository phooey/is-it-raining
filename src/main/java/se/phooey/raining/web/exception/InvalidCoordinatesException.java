package se.phooey.raining.web.exception;

/**
 * Indicates that the geographic coordinates provided to an operation were invalid.
 */
public class InvalidCoordinatesException extends Exception {

	private static final long serialVersionUID = 765565527924670704L;

	public InvalidCoordinatesException() {
        super();
    }

    public InvalidCoordinatesException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidCoordinatesException(final String message) {
        super(message);
    }

    public InvalidCoordinatesException(final Throwable cause) {
        super(cause);
    }
}
