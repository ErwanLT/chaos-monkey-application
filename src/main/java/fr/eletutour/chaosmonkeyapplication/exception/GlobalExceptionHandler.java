package fr.eletutour.chaosmonkeyapplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ChaosMonkeyApplicationException.class)
    public ProblemDetail handleChaosMonkeyApplicationException(ChaosMonkeyApplicationException e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String title = "Application Error";
        URI type = URI.create("http://localhost:8080/errors/application-error.html");
        String errorCode = null;

        switch (e) {
            case CatalogException ce -> {
                status = HttpStatus.valueOf(ce.getError().getStatus());
                title = "Catalog Error";
                type = URI.create("http://localhost:8080/errors/catalog-error.html");
                errorCode = ce.getError().name();
            }
            case UserException ue -> {
                status = HttpStatus.valueOf(ue.getError().getStatus());
                title = "User Error";
                type = URI.create("http://localhost:8080/errors/user-error.html");
                errorCode = ue.getError().name();
            }
            case StreamingException se -> {
                status = HttpStatus.valueOf(se.getError().getStatus());
                title = "Streaming Error";
                type = URI.create("http://localhost:8080/errors/streaming-error.html");
                errorCode = se.getError().name();
            }
            case RecommendationException re -> {
                status = HttpStatus.valueOf(re.getError().getStatus());
                title = "Recommendation Error";
                type = URI.create("http://localhost:8080/errors/recommendation-error.html");
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

    @ExceptionHandler({ TimeoutException.class, RequestTimeoutException.class }) // Gère le timeout natif ou custom
    public Object handleTimeoutException(Exception e, jakarta.servlet.http.HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");

        // Si la requête vient d'un navigateur (HTML), on renvoie vers la page d'erreur
        // statique
        if (acceptHeader != null && acceptHeader.contains("text/html")) {
            org.springframework.web.servlet.ModelAndView mav = new org.springframework.web.servlet.ModelAndView();
            mav.setViewName("forward:/errors/timeout.html");
            mav.setStatus(HttpStatus.SERVICE_UNAVAILABLE);
            return mav;
        }

        // Sinon (API/JSON), on renvoie un ProblemDetail
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE, // 503 Service Unavailable
                "Le traitement de la requête a dépassé le délai imparti (Timeout).");

        problemDetail.setTitle("Service Timeout");
        problemDetail.setType(URI.create("http://localhost:8080/errors/timeout.html"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorCode", "TIMEOUT_ERROR");

        return problemDetail;
    }
}
