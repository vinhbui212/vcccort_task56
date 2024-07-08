//package thuctap.task4.exception;
//
//
//
//import org.apache.tomcat.util.http.ResponseUtil;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//    @ExceptionHandler(UserException.class)
//    public ResponseEntity<UserException<Object>> handleNotFoundException(UserException ex){
//        return ResponseUtil.wrapResponse(ex.getData(), ex.getMessage(), ex.isStatus(), ex.getCode());
//    }
//}
