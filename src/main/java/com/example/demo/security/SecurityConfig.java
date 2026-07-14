package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsService customUserDetailsService,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // ============================================================
                        // PUBLIC ENDPOINTS - No authentication required
                        // ============================================================
                        // Auth endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/users/register").permitAll()
                        
                        // Swagger UI
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/v3/api-docs").permitAll()
                        .requestMatchers("/api-docs/**", "/api-docs").permitAll()
                        .requestMatchers("/swagger-resources/**", "/webjars/**").permitAll()
                        
                        // Health checks
                        .requestMatchers("/api/health/**", "/actuator/health").permitAll()
                        
                        // H2 Console
                        .requestMatchers("/h2-console/**").permitAll()
                        
                        // Public images (view only) - controller is mapped at /api/v1/images,
                        // and only GET should be open; uploads/deletes stay behind @PreAuthorize
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/images/**").permitAll()
                        
                        // ============================================================
                        // PUBLIC STORE SETTINGS
                        // ============================================================
                        .requestMatchers("/api/v1/store-settings/public/**").permitAll()
                        .requestMatchers("/api/v1/store-settings/public/maintenance").permitAll()
                        .requestMatchers("/api/v1/store-settings/public/currency").permitAll()
                        .requestMatchers("/api/v1/store-settings/public/currency-symbol").permitAll()
                        .requestMatchers("/api/v1/store-settings/public/shipping-cost").permitAll()
                        .requestMatchers("/api/v1/store-settings/public/free-shipping-threshold").permitAll()
                        .requestMatchers("/api/v1/store-settings/public/tax-rate").permitAll()
                        .requestMatchers("/api/v1/store-settings/public/registration-allowed").permitAll()
                        
                        // ============================================================
                        // PUBLIC CATEGORY ENDPOINTS
                        // ============================================================
                        .requestMatchers("/api/v1/categories").permitAll()
                        .requestMatchers("/api/v1/categories/root").permitAll()
                        .requestMatchers("/api/v1/categories/root/active").permitAll()
                        .requestMatchers("/api/v1/categories/{id}").permitAll()
                        .requestMatchers("/api/v1/categories/{parentId}/subcategories").permitAll()
                        .requestMatchers("/api/v1/categories/{parentId}/subcategories/active").permitAll()
                        .requestMatchers("/api/v1/categories/search").permitAll()
                        .requestMatchers("/api/v1/categories/{parentId}/count").permitAll()
                        .requestMatchers("/api/v1/categories/exists/{id}").permitAll()
                        
                        // ============================================================
                        // PUBLIC PRODUCT ENDPOINTS
                        // ============================================================
                        .requestMatchers("/api/v1/products").permitAll()
                        .requestMatchers("/api/v1/products/{id}").permitAll()
                        .requestMatchers("/api/v1/products/{id}/summary").permitAll()
                        .requestMatchers("/api/v1/products/search").permitAll()
                        .requestMatchers("/api/v1/products/search/advanced").permitAll()
                        .requestMatchers("/api/v1/products/category/{categoryId}").permitAll()
                        .requestMatchers("/api/v1/products/category/{categoryId}/active").permitAll()
                        .requestMatchers("/api/v1/products/featured").permitAll()
                        .requestMatchers("/api/v1/products/featured/paginated").permitAll()
                        .requestMatchers("/api/v1/products/in-stock").permitAll()
                        .requestMatchers("/api/v1/products/price-range").permitAll()
                        .requestMatchers("/api/v1/products/on-sale").permitAll()
                        .requestMatchers("/api/v1/products/top-rated").permitAll()
                        .requestMatchers("/api/v1/products/new-arrivals").permitAll()
                        .requestMatchers("/api/v1/products/best-sellers").permitAll()
                        .requestMatchers("/api/v1/products/most-viewed").permitAll()
                        .requestMatchers("/api/v1/products/stats/count").permitAll()
                        .requestMatchers("/api/v1/products/stats/count/active").permitAll()
                        .requestMatchers("/api/v1/products/stats/count/category/{categoryId}").permitAll()
                        
                        // ============================================================
                        // PUBLIC LOCATION ENDPOINTS
                        // ============================================================
                        .requestMatchers("/api/v1/locations/big-areas").permitAll()
                        .requestMatchers("/api/v1/locations/big-areas/active").permitAll()
                        .requestMatchers("/api/v1/locations/big-areas/{id}").permitAll()
                        .requestMatchers("/api/v1/locations/big-areas/search").permitAll()
                        .requestMatchers("/api/v1/locations/big-areas/{id}/town-count").permitAll()
                        .requestMatchers("/api/v1/locations/towns/{id}").permitAll()
                        .requestMatchers("/api/v1/locations/towns/by-big-area/{bigAreaId}").permitAll()
                        .requestMatchers("/api/v1/locations/towns/by-big-area/{bigAreaId}/paginated").permitAll()
                        .requestMatchers("/api/v1/locations/towns/by-big-area/{bigAreaId}/active").permitAll()
                        .requestMatchers("/api/v1/locations/towns/by-big-area/{bigAreaId}/delivery-available").permitAll()
                        .requestMatchers("/api/v1/locations/towns/search").permitAll()
                        .requestMatchers("/api/v1/locations/towns/{id}/address-count").permitAll()
                        
                        // ============================================================
                        // PUBLIC REVIEW ENDPOINTS
                        // ============================================================
                        .requestMatchers("/api/v1/reviews/product/{productId}").permitAll()
                        .requestMatchers("/api/v1/reviews/product/{productId}/rating").permitAll()
                        .requestMatchers("/api/v1/reviews/product/{productId}/count").permitAll()
                        
                        // ============================================================
                        // PUBLIC ORDER ENDPOINTS (Tracking only)
                        // ============================================================
                        .requestMatchers("/api/v1/orders/track/{trackingCode}").permitAll()
                        .requestMatchers("/api/v1/orders/track").permitAll()
                        
                        // ============================================================
                        // AUTHENTICATED ENDPOINTS - All other requests need authentication
                        // The @PreAuthorize annotations will handle specific permissions
                        // ============================================================
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:4200",
                "http://localhost:8080",
                "http://localhost:8081"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}