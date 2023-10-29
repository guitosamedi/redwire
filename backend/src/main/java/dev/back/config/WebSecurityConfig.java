package dev.back.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;
import java.util.Map;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@CrossOrigin
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {


    /**
     * méthode qui gère l'authentification
     *
     * @param http
     * @param jwtFilter
     * @param jwtConfig
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, @Qualifier("corsConfiguration") CorsConfigurationSource source, JWTAuthorizationFilter jwtFilter, JWTConfig jwtConfig ) throws Exception {

        http.authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(antMatcher("/sessions")).permitAll()
                                .requestMatchers(antMatcher("/logout")).permitAll()
                                .requestMatchers(antMatcher(HttpMethod.GET,"/departement")).permitAll()
                                .requestMatchers(antMatcher(HttpMethod.GET,"/absence/manager")).hasAuthority("MANAGER")
                                .requestMatchers(antMatcher(HttpMethod.POST,"/employe")).hasAuthority("ADMIN")
                                .requestMatchers(antMatcher(HttpMethod.POST,"/jouroff/**")).hasAuthority("ADMIN")
                                .requestMatchers(antMatcher(HttpMethod.PUT,"/jouroff/**")).hasAuthority("ADMIN")
                                .requestMatchers(antMatcher(HttpMethod.PUT,"/absence/statut/{id}")).hasAuthority("MANAGER")
                                .requestMatchers(antMatcher(HttpMethod.PUT,"/employe/{id}")).hasAuthority("ADMIN")
                                .requestMatchers(antMatcher(HttpMethod.DELETE,"/employe/**")).hasAuthority("ADMIN")
                                .requestMatchers(antMatcher(HttpMethod.DELETE,"/jouroff/**")).hasAuthority("ADMIN")
                                .requestMatchers(antMatcher(HttpMethod.DELETE,"/departement/**")).hasAuthority("ADMIN")
                                .requestMatchers(antMatcher(HttpMethod.POST,"/departement/**")).hasAuthority("ADMIN")
                                .requestMatchers(antMatcher(HttpMethod.GET,"/absence")).hasAnyAuthority("ADMIN","MANAGER")
                                .requestMatchers(antMatcher(HttpMethod.GET,"/employe")).hasAnyAuthority("ADMIN","MANAGER")
                                .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable
                        // .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        //.csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler()::handle)
                        //.ignoringRequestMatchers(new AntPathRequestMatcher("/sessions"))
                )
                .cors(cor-> cor.configurationSource(source))
                .headers( headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout( logout ->{
                    logout
                        .logoutSuccessHandler(((req, resp, auth) ->{
                            resp.setStatus(HttpStatus.OK.value());}))
                        .deleteCookies(jwtConfig.getCookie());});

        return http.build();
    }

    /**
     * nous permet d'encoder le mot de passe de l'employe
     *
     * @return mot de pass encodé
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        String encodingId = "bcrypt";
        return new DelegatingPasswordEncoder(encodingId, Map.of(encodingId, new BCryptPasswordEncoder()));
    }

    @Bean
    CorsConfigurationSource corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type"));
        configuration.setAllowCredentials(true);
//merci lucas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }
}