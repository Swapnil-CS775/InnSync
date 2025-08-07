package com.innsync.auth.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//Marks this class as a Spring Component, so Spring will manage it as a bean
//and we can inject it into other services.
@Component
public class JwtUtil {

	@Value("${jwt.secret.key}")
	private String secretKey;

    // Generates a JWT for a given user.
    public String generateToken(UserDetails userDetails) {
        // Creates an empty map to hold any extra data (claims) you might want to add to the token.
        Map<String, Object> claims = new HashMap<>();
        
        String role=userDetails.getAuthorities().stream()
        		.findFirst()
        		.map(GrantedAuthority::getAuthority)
        		.orElse("USER");
        
        claims.put("role", role);
        
        // Calls the private helper method to actually build the token string.
        return createToken(claims, userDetails.getUsername());
    }

    // Creates the JWT with claims, subject, issued time, expiration, and signature.
    private String createToken(Map<String, Object> claims, String userName) {
        // Jwts.builder() starts the process of building the token.
        return Jwts.builder()
                // .claims(claims): Adds the custom claims map to the token's payload.
                .claims(claims)
                // .subject(userName): Sets the "subject" of the token. This is a standard claim,
                // and we use it to store the user's principal identifier (their email).
                .subject(userName)
                // .issuedAt(...): Sets the timestamp for when the token was created.
                .issuedAt(new Date(System.currentTimeMillis()))
                // .expiration(...): Sets the expiration date. Here, it's 30 minutes from the creation time.
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // Token valid for 30 minutes
                // .signWith(...): This is the most critical part. It digitally signs the token
                // using our secret key and the HS256 (HMAC-SHA256) algorithm.
                .signWith(getSignKey(), Jwts.SIG.HS256)
                // .compact(): Finalizes the token and serializes it into a compact, URL-safe string.
                .compact();
    }

    // Converts the base64 secret string into a SecretKey object for signing.
    private SecretKey getSignKey() {
    	// Decodes the BASE64 secret string into a byte array.
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // Creates a SecretKey suitable for HMAC-SHA algorithms from the byte array.
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extracts the username (subject) from the token.
    public String extractUsername(String token) {
    	// It delegates the work to the generic extractClaim method.
        // Claims::getSubject is a "method reference" in Java, a shortcut for
        // a function that takes a Claims object and calls its getSubject() method.
        return extractClaim(token, Claims::getSubject);
    }

    // Extracts the expiration date from the token.
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // A generic method to extract a single claim from the token.
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    	// First, parse the token to get the full payload (all claims).
        final Claims claims = extractAllClaims(token);
     // Then, apply the provided function (the "resolver") to get the specific claim we want.
        return claimsResolver.apply(claims);
    }

    // This is the core parsing method. It validates the token's signature.
    private Claims extractAllClaims(String token) {
    	// This process will throw an exception if the token's signature is invalid or if it's malformed.
        return Jwts
                .parser() // Gets a JWT parser instance.
                .verifyWith(getSignKey()) // Tells the parser to use our secret key for verification.
                .build() // Builds the configured parser.
                .parseSignedClaims(token) // Parses the token string and validates the signature.
                .getPayload(); // If successful, returns the payload (the claims).
    }

    // Checks if the token has expired.
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validates the token by checking the username and expiration.
    public Boolean validateToken(String token, UserDetails userDetails) {
    	// Extracts the username from the token.
        final String username = extractUsername(token);
        // The token is valid only if the username matches AND the token is not expired.
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

/*
 * 
 * Concept: Stateless Authentication with JWT 🔑
In a microservices architecture, we need stateless authentication. This means the server does not store 
any information about who is logged in (like a session). Each API request from the client must contain 
all the information necessary for the server to authenticate it. This is where JWT comes in.

JSON Web Token (JWT) is a compact, self-contained standard for securely transmitting information as a 
JSON object. When a user logs in, the server generates a JWT, signs it with a secret key, and sends 
it to the client. The client then includes this token in the Authorization header of every subsequent request.

The server can verify the token's signature to ensure it's authentic without needing to check a 
database or session store, making the system highly scalable.

The Login Flow:

User sends email and password to /api/auth/login.
Server validates credentials against the databas.
If valid, server creates a signed JWT and returns it.
Client stores the JWT and sends it with every future request to protected endpoints.


Concept: Stateful vs. Stateless Authentication
Traditional Session-Based (Stateful) Authentication
In a traditional, single-application (monolithic) setup, session-based authentication works like this:

You log in.

The server creates a session in its memory, assigning it a unique ID (e.g., Session-123).
It sends this Session-123 ID back to your browser as a cookie.
With every new request, your browser sends the cookie. The server looks up Session-123 in its 
memory to see who you are.

The key here is that the server must store the state (the session data).

The Problem with "State" in Microservices
Now, imagine you have a popular application and need multiple copies of your order-service running 
to handle the traffic. A load balancer distributes requests between them.

Your login request goes to Instance A. It creates your session and stores it in its memory.
Your next request (e.g., "add item to cart") is sent to the load balancer, which routes it to 
Instance B to balance the load.

Instance B receives your request and your session ID, but it has no idea who you are. 
The session data is only in the memory of Instance A. To Instance B, you are not logged in.

This completely breaks the user experience. The workaround is to create a shared session 
store (like a Redis server) that all instances connect to. However, this adds complexity, creates a new single point of failure, and goes against the microservice principle of services being independent and decoupled.

Stateless (JWT) Authentication: The Solution
This is why microservices need stateless authentication. With JWT, the server stores nothing 
about the session.

You log in to the auth-service.

It gives you back a JWT. This token is a self-contained, digitally signed piece of data that says, 
"This is Swapnil, and his role is OWNER. This token is valid for 8 hours."

You send this JWT in the header of every request.

Now, it doesn't matter if the load balancer sends your request to Instance A, Instance B, 
or Instance C. Any instance can read the token, verify its digital signature using a shared secret key, 
and know instantly who you are and what your permissions are, without ever needing to talk to a central 
session store.


 * */
