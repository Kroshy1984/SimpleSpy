package ru.sfedu.simplepsyspecialist.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(encoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests((request) -> request
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/SimplePsy/V1/scoring/test/{testId}/{customerId}").permitAll()
                        .requestMatchers("/SimplePsy/V1/scoring/done").permitAll()
                        .requestMatchers("/SimplePsy/V1/scoring/submit").permitAll()
                        .requestMatchers("/SimplePsy/V1/specialist/signup").permitAll()
                        .requestMatchers("/SimplePsy/V1/specialist/changePass**").permitAll()
                        .requestMatchers("/SimplePsy/V1/specialist/setNewPassword/**").permitAll()
                        .requestMatchers("/SimplePsy/V1/customer/customer-card/edit/**").permitAll()
                        .requestMatchers("/SimplePsy/V1/customer/customer/**").permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/**/*.css")).permitAll()
                        .requestMatchers("/SimplePsy/V1/client/findAll").permitAll() // Разрешить доступ к этому URL
                        .anyRequest().authenticated())
                .formLogin((form) -> form.loginPage("/SimplePsy/V1/specialist/login").permitAll()
                        .defaultSuccessUrl("/SimplePsy/V1/session/calendar", true)
                        .permitAll())
                .logout((logout) -> logout.logoutUrl("/logout").permitAll())
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable());
//        httpSecurity.authorizeHttpRequests((request) -> request.anyRequest().permitAll()).csrf(csrf -> csrf.disable());
        return httpSecurity.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8081")); // Замените на ваш домен
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}