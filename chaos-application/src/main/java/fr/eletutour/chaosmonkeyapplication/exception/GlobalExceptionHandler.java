package fr.eletutour.chaosmonkeyapplication.exception;

import fr.eletutour.chaosmonkeyapplication.configurations.UIConfiguration;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final UIConfiguration uiConfiguration;

    public GlobalExceptionHandler(UIConfiguration uiConfiguration) {
        this.uiConfiguration = uiConfiguration;
    }

    @ExceptionHandler(ChaosMonkeyApplicationException.class)
    public Object handleChaosMonkeyApplicationException(ChaosMonkeyApplicationException e, HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");

        if (acceptHeader != null && acceptHeader.contains("text/html")) {
            String prefix = "v2".equals(uiConfiguration.getUiVersion()) ? "/errors/v2" : "/errors";
            String errorPage;
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

            if (e instanceof CatalogException ce) {
                errorPage = "/catalog-error.html";
                status = HttpStatus.valueOf(ce.getError().getStatus());
            } else if (e instanceof UserException ue) {
                errorPage = "/user-error.html";
                status = HttpStatus.valueOf(ue.getError().getStatus());
            } else if (e instanceof StreamingException se) {
                errorPage = "/streaming-error.html";
                status = HttpStatus.valueOf(se.getError().getStatus());
            } else if (e instanceof RecommendationException re) {
                errorPage = "/recommendation-error.html";
                status = HttpStatus.valueOf(re.getError().getStatus());
            } else {
                errorPage = "/application-error.html";
            }

            ModelAndView mav = new ModelAndView();
            mav.setViewName("forward:" + prefix + errorPage);
            mav.setStatus(status);
            return mav;
        }

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String title = "Application Error";
        URI type = URI.create("http://localhost:8080/errors/application-error.html");
        String errorCode = null;

        if (e instanceof CatalogException ce) {
            status = HttpStatus.valueOf(ce.getError().getStatus());
            title = "Catalog Error";
            type = URI.create("http://localhost:8080/errors/catalog-error.html");
            errorCode = ce.getError().name();
        } else if (e instanceof UserException ue) {
            status = HttpStatus.valueOf(ue.getError().getStatus());
            title = "User Error";
            type = URI.create("http://localhost:8080/errors/user-error.html");
            errorCode = ue.getError().name();
        } else if (e instanceof StreamingException se) {
            status = HttpStatus.valueOf(se.getError().getStatus());
            title = "Streaming Error";
            type = URI.create("http://localhost:8080/errors/streaming-error.html");
            errorCode = se.getError().name();
        } else if (e instanceof RecommendationException re) {
            status = HttpStatus.valueOf(re.getError().getStatus());
            title = "Recommendation Error";
            type = URI.create("http://localhost:8080/errors/recommendation-error.html");
            errorCode = re.getError().name();
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

    @ExceptionHandler({ TimeoutException.class, RequestTimeoutException.class })
    public Object handleTimeoutException(Exception e, HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");

        if (acceptHeader != null && acceptHeader.contains("text/html")) {
            String prefix = "v2".equals(uiConfiguration.getUiVersion()) ? "/errors/v2" : "/errors";
            ModelAndView mav = new ModelAndView();
            mav.setViewName("forward:" + prefix + "/timeout.html");
            mav.setStatus(HttpStatus.SERVICE_UNAVAILABLE);
            return mav;
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Le traitement de la requête a dépassé le délai imparti (Timeout).");
        problemDetail.setTitle("Service Timeout");
        problemDetail.setType(URI.create("http://localhost:8080/errors/timeout.html"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorCode", "TIMEOUT_ERROR");
        return problemDetail;
    }
}
