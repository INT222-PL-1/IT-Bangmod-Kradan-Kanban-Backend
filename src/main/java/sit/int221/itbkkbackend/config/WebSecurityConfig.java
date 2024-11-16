package sit.int221.itbkkbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sit.int221.itbkkbackend.auth.*;
import sit.int221.itbkkbackend.auth.filters.AnonymousAuthFilter;
import sit.int221.itbkkbackend.auth.filters.JwtAuthFilter;
import sit.int221.itbkkbackend.auth.services.JwtUserDetailsService;
import sit.int221.itbkkbackend.auth.utils.enums.UserAuthority;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final AnonymousAuthFilter anonymousAuthFilter;

    public WebSecurityConfig(JwtUserDetailsService jwtUserDetailsService, JwtAuthFilter jwtAuthFilter, AnonymousAuthFilter anonymousAuthFilter) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.anonymousAuthFilter = anonymousAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        authorize -> authorize.requestMatchers("/login","/error","/token").permitAll()
                                .requestMatchers("v3/boards/*/collabs/*").authenticated()
                                .requestMatchers(HttpMethod.GET).hasAnyAuthority(
                                    UserAuthority.PUBLIC_ACCESS.getAuthority(),
                                    UserAuthority.OWNER.getAuthority(),
                                    UserAuthority.COLLABORATOR.getAuthority()
                                )
                                .requestMatchers("/v3/boards/*/collabs").hasAnyAuthority(
                                    UserAuthority.OWNER.getAuthority(),
                                    UserAuthority.COLLABORATOR.getAuthority()
                                )
                                .requestMatchers("/v3/boards","v3/boards/*/collabs/**").authenticated()
                                .requestMatchers("/v3/boards/*").hasAuthority(
                                    UserAuthority.OWNER.getAuthority()
                                )
                                .requestMatchers("/v3/boards/**").hasAnyAuthority(
                                    UserAuthority.OWNER.getAuthority(),
                                    UserAuthority.COLLABORATOR.getAuthority()
                                )
                )
                .addFilterBefore(jwtAuthFilter,UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(anonymousAuthFilter , JwtAuthFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )
                .httpBasic(withDefaults());
        return httpSecurity.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(jwtUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
