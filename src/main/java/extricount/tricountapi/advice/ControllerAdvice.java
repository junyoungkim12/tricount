package extricount.tricountapi.advice;

import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> AuthenticationExceptionHandler(AuthenticationException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);

    }

}
