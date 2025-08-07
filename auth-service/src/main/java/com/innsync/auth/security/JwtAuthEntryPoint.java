package com.innsync.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innsync.auth.dto.ErrorResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // Create our standard error response DTO
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                "Invalid username or password.", // A clear but secure message
                request.getRequestURI(),
                HttpStatus.UNAUTHORIZED.value()
        );

        // Set the response status and content type
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Write the JSON response to the output stream
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        // The line below is needed to correctly serialize LocalDateTime
        mapper.findAndRegisterModules(); 
        mapper.writeValue(outputStream, errorResponse);
        outputStream.flush();
    }
}