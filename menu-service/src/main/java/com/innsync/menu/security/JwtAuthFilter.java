package com.innsync.menu.security;

import com.innsync.menu.tenant.TenantContext;
import com.innsync.menu.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    private final JdbcTemplate jdbcTemplate;

    // We inject the primary datasource and create a JdbcTemplate from it.
    @Autowired
    public JwtAuthFilter(@Qualifier("primaryDataSource") DataSource primaryDataSource) {
        this.jdbcTemplate = new JdbcTemplate(primaryDataSource);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);
        }

        try {
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Long tenantId = jwtUtil.extractTenantId(token);

                // Look up the database name from the primary 'registry' database.
                String dbName = jdbcTemplate.queryForObject(
                        "SELECT db_identifier FROM businesses WHERE id = ?", String.class, tenantId);

                // Set the tenant database for the current request thread.
                TenantContext.setCurrentTenant(dbName);

                if (!jwtUtil.isTokenExpired(token)) {
                    String role = jwtUtil.extractRole(token);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.singletonList(new SimpleGrantedAuthority(role)));
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            // Continue the filter chain. Subsequent DB calls will use the correct tenant DB.
            filterChain.doFilter(request, response);
        } finally {
            // CRUCIAL: Clear the tenant context after the request is complete.
            TenantContext.clear();
        }
    }
}