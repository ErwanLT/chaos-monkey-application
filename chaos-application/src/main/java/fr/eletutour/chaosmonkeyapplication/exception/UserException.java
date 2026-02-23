package fr.eletutour.chaosmonkeyapplication.exception;

public class UserException extends ChaosMonkeyApplicationException {

    public enum UserError {
        USER_NOT_FOUND("User not found", 404),
        USER_ALREADY_EXISTS("User already exists", 409),
        INVALID_EMAIL("Invalid email format", 400);

        private final String message;
        private final int status;

        UserError(String message, int status) {
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

    private final UserError error;

    public UserException(UserError error) {
        super(error.getMessage());
        this.error = error;
    }

    public UserException(String message) {
        super(message);
        this.error = UserError.USER_NOT_FOUND; // Default
    }

    public UserException(UserError error, String details) {
        super(error.getMessage() + ": " + details);
        this.error = error;
    }

    public UserError getError() {
        return error;
    }
}
