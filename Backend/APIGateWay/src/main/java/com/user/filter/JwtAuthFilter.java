package com.user.filter;

import javax.crypto.SecretKey;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.io.Decoders;
@Component
public class JwtAuthFilter implements GlobalFilter {

	private final String SECRET_KEY = "6A5B6E32725373872F415A244872B4620565366D597133743677397A";
	private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		if (request.getMethod() != null && request.getMethod().name().equalsIgnoreCase("OPTIONS")) {
	        return chain.filter(exchange);
	    }
		
		if (request.getURI().getPath().matches(".*?/farmers/crops/allForHomePage$")
				|| request.getURI().getPath().contains("/login") 
				|| request.getURI().getPath().contains("/register")
				|| request.getURI().getPath().contains("/send-otp")
				|| request.getURI().getPath().contains("/reset-password")
			)
		{
		    return chain.filter(exchange);
		}


		
		String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (token == null || !token.startsWith("Bearer ")) {
			return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
		}

		String jwt = token.substring(7);
		
		try {
		    Claims claims = Jwts.parser()
		            .verifyWith(key)
		            .build()
		            .parseSignedClaims(jwt)
		            .getPayload();

		    String role = claims.get("role", String.class);
		    String username = claims.getSubject(); 


		    String path = request.getURI().getPath();
		    System.out.println("Request path: " + path);

		    if (!isAuthorized(path, role)) {
		        return onError(exchange, "Not authorized", HttpStatus.FORBIDDEN);
		    }

		    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
		            .header("x-auth-user-role", role)
		            .header("x-auth-user", username)
		            .build();

		    return chain.filter(exchange.mutate().request(modifiedRequest).build());

		} catch (Exception e) {
		    return onError(exchange, "Invalid token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}

	private boolean isAuthorized(String path, String role) {
	    return (path.startsWith("/admin") && role.equals("ADMIN")) ||
	           (path.startsWith("/dealer") && role.equals("DEALER")) ||
	           (path.startsWith("/payment") && (role.equals("DEALER") || role.equals("FARMER")))||
	           ((path.startsWith("/farmer") || path.startsWith("/crops")) && role.equals("FARMER"));
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(status);
		return response.setComplete();
	}
}
