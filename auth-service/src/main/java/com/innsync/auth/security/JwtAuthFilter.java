package com.innsync.auth.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.innsync.auth.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter{

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private OwnerUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 1. Get the Authorization header from the request
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. Check if the header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // 3. Extract the token from the header string
            token = authHeader.substring(7);
            // 4. Extract the username from the token
            username = jwtUtil.extractUsername(token);
        }

        // 5. If we have a username and the user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 6. Load the user's details from the database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

         // 7. Validate the token
         // This calls our JwtUtil method. It returns 'true' only if the username in the token
         // matches the user loaded from the database AND the token is not expired.
         if (jwtUtil.validateToken(token, userDetails)) {

             // 8. If the token is valid, create an authentication token.
             // We create a UsernamePasswordAuthenticationToken. This is the standard object Spring Security
             // uses to represent a fully authenticated user inside the security context.
             UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                     userDetails, // The principal: our full, authenticated user object.
                     null,        // The credentials: null, because we used a token, not a password.
                     userDetails.getAuthorities() // The user's roles/permissions.
             );

             // This line is optional but good practice. It adds details about the web request
             // (like IP address) to our authentication object for auditing purposes.
             authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

             // 9. Set the authentication in the security context.
             // THIS IS THE MOST IMPORTANT STEP. We are updating the SecurityContextHolder
             // with the details of the authenticated user.
             SecurityContextHolder.getContext().setAuthentication(authToken);
         }
        }
        // 10. Continue the filter chain
        filterChain.doFilter(request, response);
		
	}
}


/*
 * The SecurityContextHolder is a special storage area where Spring Security keeps the details of the 
 * currently authenticated user for a single request. Think of it as the official record for "who is 
 * logged in right now".

At the start of a request, this context is empty. Our filter's job is to populate it if the user 
provides a valid token.

SecurityContextHolder.getContext().getAuthentication() == null

What it means: This part checks, "Is anyone already authenticated for this request?"

Why we need it: This is a crucial check to ensure that we perform the authentication 
logic only once per request. In a complex application, a request might pass through 
multiple filters. This check prevents us from repeatedly doing the expensive work of 
validating the token and looking up the user in the database if they've already been 
authenticated earlier in the filter chain.
 * */
