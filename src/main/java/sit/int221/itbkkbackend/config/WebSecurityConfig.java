package sit.int221.itbkkbackend.config;


import org.springframework.beans.factory.annotation.Autowired;
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

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    JwtAuthFilter jwtAuthFilter;
    @Autowired
    AnonymousAuthFilter anonymousAuthFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeRequests(
                        authorize -> authorize.requestMatchers("/login","/error","/token").permitAll()
                                     .requestMatchers(HttpMethod.GET).hasAnyAuthority("PUBLIC_ACCESS","OWNER","COLLABORATOR")
                                     .requestMatchers("/v3/boards/*/collabs","/v3/boards/*/collabs/*").hasAnyAuthority("OWNER","COLLABORATOR")
                                     .requestMatchers("/v3/boards").authenticated()
                                     .requestMatchers("/v3/boards/*").hasAuthority("OWNER")
                                     .requestMatchers("/v3/boards/**").hasAnyAuthority("OWNER","COLLABORATOR")
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
