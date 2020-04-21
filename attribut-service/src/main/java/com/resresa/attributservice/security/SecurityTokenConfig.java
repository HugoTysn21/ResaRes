package com.resresa.attributservice.security;

import com.resresa.attributservice.web.model.JwtConfig;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
@EnableWebSecurity    // Enable security config. This annotation denotes config for spring security.
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {

//    @Value("${security.jwt.uri}")
//    private String Uri;

    @Autowired
    private JwtConfig jwtConfig;

    private static final Logger logger = LoggerFactory.getLogger(SecurityTokenConfig.class);

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                // make sure we use stateless session; session won't be used to store user's state.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // handle an authorized attempts
                .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .addFilterBefore(new JwtTokenAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
                // this disables session creation on Spring Security
                .authorizeRequests()
                // allow all who are accessing "auth" service
                .antMatchers(HttpMethod.GET, "/information").permitAll()
                // must be an admin if trying to access admin area (authentication is also required here)
                // Any other request must be authenticated
                .anyRequest().authenticated();
    }

    public String getRoleFromJWT(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(jwtConfig.getSecret().getBytes())
                .parseClaimsJws(token)
                .getBody();

        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");

        return String.valueOf(authorities);
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtConfig.getHeader());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtConfig.getPrefix())) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtConfig.getSecret().getBytes())
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    @Bean
    public JwtConfig jwtConfig() {
        return new JwtConfig();
    }
}
