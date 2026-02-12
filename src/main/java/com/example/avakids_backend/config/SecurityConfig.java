package com.example.avakids_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINTS_POST = {
        "/api/v1/auth/login", "/api/v1/auth/introspect", "/api/v1/auth/logout", "/api/v1/auth/refresh", "/api/v1/users/**"
    };
    private final String[] PUBLIC_ENDPOINTS_GET = {
        "/api/v1/banners/**",
        "/api/v1/category/**",
        "/api/v1/products/**",
        "/api/v1/blogs/**",
        "/uploads/**",
        "/api/v1/product-images/**",
        "/api/v1/reviews/**",
        "/api/v1/products/{id}/options/**",
        "/api/payment/vnpay/**"
    };

    //    @Autowired
    private CustomJWTDecoder jwtDecoder;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/avakids/uploads/**");
    }

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS_POST)
                        .permitAll()
                        .requestMatchers("/api/notifications/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/payment/create-payment")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS_GET)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(
                        uauth2 -> uauth2.jwt(jwtConfigurer -> jwtConfigurer
                                        .decoder(jwtDecoder)
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // xử lý auth lỗi
                        )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());
        return httpSecurity.build();
    }

    @Bean // dùng để cho file khác gọi với nhau
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOriginPattern("http://localhost:8080");
        corsConfiguration.addAllowedOriginPattern("http://127.0.0.1:5500");
        corsConfiguration.addAllowedOriginPattern("http://localhost:5173");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
            //            System.out.println("=== Extracted Authorities from JWT ===");
            //            authorities.forEach(a -> System.out.println(" -> " + a.getAuthority()));
            return authorities;
        });
        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
