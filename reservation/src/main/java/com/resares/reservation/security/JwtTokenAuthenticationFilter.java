package com.resares.reservation.security;

import com.resares.reservation.web.model.JwtConfig;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    public JwtTokenAuthenticationFilter(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1. get the authentication header. Tokens are supposed to be passed in the authentication header
        String header_http = httpServletRequest.getHeader(jwtConfig.getHeader());

        // 2. validate the header and check the prefix
        if(!header_http.startsWith(jwtConfig.getPrefix())) {
            chain.doFilter(httpServletRequest, response);  		// If not valid, go to the next filter.
            return;
        }
        // 3. Get the token
        String token = header_http.replace(jwtConfig.getPrefix(), "");

        try {	// exceptions might be thrown in creating the claims if for example the token is expired

            // 4. Validate the token
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret().getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();

            if(username != null) {
                @SuppressWarnings("unchecked")
                List<String> authorities = (List<String>) claims.get("authorities");

                // 5. Create auth object
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

                // 6. Authenticate the user
                // Now, user is authenticated
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (ExpiredJwtException exception) {
            // In case of failure. Make sure it's clear; so guarantee user won't be authenticated
            SecurityContextHolder.clearContext();
            logger.warn("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            SecurityContextHolder.clearContext();
            logger.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
        } catch (MalformedJwtException exception) {
            SecurityContextHolder.clearContext();
            logger.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
        } catch (SignatureException exception) {
            logger.warn("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
            SecurityContextHolder.clearContext();
        } catch (IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            logger.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
        }

        // go to the next filter in the filter chain
        chain.doFilter(httpServletRequest, response);
    }
}