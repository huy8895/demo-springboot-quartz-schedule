package com.example.demospringbootquartzschedule;

import com.example.demospringbootquartzschedule.dto.ErrorResponse;
import com.example.demospringbootquartzschedule.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleJobAlreadyExistsException() {
        // Thiết lập dữ liệu đầu vào
        ObjectAlreadyExistsException exception = new ObjectAlreadyExistsException("Job already exists");
        WebRequest request = mock(WebRequest.class);
        doReturn("Request description").when(request).getDescription(false); // Sử dụng doReturn

        // Gọi phương thức xử lý
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleJobAlreadyExistsException(exception, request);

        // Kiểm tra phản hồi
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Conflict", response.getBody().getError());
        assertEquals("Job already exists", response.getBody().getMessage());
    }

    @Test
    void testHandleSchedulerException() {
        // Thiết lập dữ liệu đầu vào
        SchedulerException exception = new SchedulerException("Scheduler error");
        WebRequest request = mock(WebRequest.class);
        doReturn("Request description").when(request).getDescription(false); // Sử dụng doReturn

        // Gọi phương thức xử lý
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleSchedulerException(exception, request);

        // Kiểm tra phản hồi
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Scheduler error", response.getBody().getMessage());
    }

    @Test
    void testHandleGenericException() {
        // Thiết lập dữ liệu đầu vào
        Exception exception = new Exception("Generic error");
        WebRequest request = mock(WebRequest.class);
        doReturn("Request description").when(request).getDescription(false); // Sử dụng doReturn

        // Gọi phương thức xử lý
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, request);

        // Kiểm tra phản hồi
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Đã xảy ra lỗi không mong muốn", response.getBody().getMessage());
    }
}