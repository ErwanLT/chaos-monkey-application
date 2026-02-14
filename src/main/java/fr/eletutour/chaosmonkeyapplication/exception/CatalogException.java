package fr.eletutour.chaosmonkeyapplication.exception;

public class CatalogException extends ChaosMonkeyApplicationException {

    public enum CatalogError {
        VIDEO_NOT_FOUND("Video not found", 404),
        GENRE_NOT_FOUND("Genre not found", 404),
        INVALID_VIDEO_TYPE("Invalid video type", 400);

        private final String message;
        private final int status;

        CatalogError(String message, int status) {
            this.message = message;
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public int getStatus() {
            return status;
        }
    }

    private final CatalogError error;

    public CatalogException(CatalogError error) {
        super(error.getMessage());
        this.error = error;
    }

    public CatalogException(String message) {
        super(message);
        this.error = CatalogError.VIDEO_NOT_FOUND; // Default
    }

    public CatalogException(CatalogError error, String details) {
        super(error.getMessage() + ": " + details);
        this.error = error;
    }

    public CatalogError getError() {
        return error;
    }
}
