package fr.eletutour.chaosmonkeyapplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ChaosMonkeyApplicationException.class)
    public ProblemDetail handleChaosMonkeyApplicationException(ChaosMonkeyApplicationException e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String title = "Application Error";
        URI type = URI.create("https://chaosflix.com/errors/application-error");
        String errorCode = null;

        switch (e) {
            case CatalogException ce -> {
                status = HttpStatus.valueOf(ce.getError().getStatus());
                title = "Catalog Error";
                type = URI.create("https://chaosflix.com/errors/catalog-error");
                errorCode = ce.getError().name();
            }
            case UserException ue -> {
                status = HttpStatus.valueOf(ue.getError().getStatus());
                title = "User Error";
                type = URI.create("https://chaosflix.com/errors/user-error");
                errorCode = ue.getError().name();
            }
            case StreamingException se -> {
                status = HttpStatus.valueOf(se.getError().getStatus());
                title = "Streaming Error";
                type = URI.create("https://chaosflix.com/errors/streaming-error");
                errorCode = se.getError().name();
            }
            case RecommendationException re -> {
                status = HttpStatus.valueOf(re.getError().getStatus());
                title = "Recommendation Error";
                type = URI.create("https://chaosflix.com/errors/recommendation-error");
                errorCode = re.getError().name();
            }
            default -> {
                // Keep default values
            }
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, e.getMessage());
        problemDetail.setTitle(title);
        problemDetail.setType(type);
        problemDetail.setProperty("timestamp", Instant.now());
        if (errorCode != null) {
            problemDetail.setProperty("errorCode", errorCode);
        }
        return problemDetail;
    }
}
