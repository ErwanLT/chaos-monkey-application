package fr.eletutour.chaosmonkeyapplication.exception;

public class RecommendationException extends ChaosMonkeyApplicationException {

    public enum RecommendationError {
        GENERATION_FAILED("Failed to generate recommendations", 500),
        NO_HISTORY("No watch history available for recommendations", 404),
        SERVICE_UNAVAILABLE("Recommendation service temporarily unavailable", 503);

        private final String message;
        private final int status;

        RecommendationError(String message, int status) {
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

    private final RecommendationError error;

    public RecommendationException(RecommendationError error) {
        super(error.getMessage());
        this.error = error;
    }

    public RecommendationException(String message) {
        super(message);
        this.error = RecommendationError.GENERATION_FAILED; // Default
    }

    public RecommendationException(RecommendationError error, String details) {
        super(error.getMessage() + ": " + details);
        this.error = error;
    }

    public RecommendationError getError() {
        return error;
    }
}
