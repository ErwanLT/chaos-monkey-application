package fr.eletutour.chaosmonkeyapplication.exception;

public class StreamingException extends ChaosMonkeyApplicationException {

    public enum StreamingError {
        STREAM_INIT_FAILED("Failed to initialize stream", 500),
        PLAYBACK_ERROR("Playback error occurred", 500),
        INVALID_QUALITY("Invalid quality requested", 400);

        private final String message;
        private final int status;

        StreamingError(String message, int status) {
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

    private final StreamingError error;

    public StreamingException(StreamingError error) {
        super(error.getMessage());
        this.error = error;
    }

    public StreamingException(String message) {
        super(message);
        this.error = StreamingError.STREAM_INIT_FAILED; // Default
    }

    public StreamingException(StreamingError error, String details) {
        super(error.getMessage() + ": " + details);
        this.error = error;
    }

    public StreamingError getError() {
        return error;
    }
}
