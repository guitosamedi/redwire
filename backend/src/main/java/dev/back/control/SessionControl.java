package dev.back.control;

import dev.back.DTO.LoginDTO;
import dev.back.config.JWTConfig;
import dev.back.entite.Employe;
import dev.back.repository.EmployeRepo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Optional;


@CrossOrigin
@RestController
@RequestMapping("sessions")
public class SessionControl {
    private JWTConfig jwtConfig;

    private EmployeRepo employeRepo;

    private PasswordEncoder passwordEncoder;

    public SessionControl(JWTConfig jwtConfig, EmployeRepo employeRepo, PasswordEncoder passwordEncoder) {
        this.jwtConfig = jwtConfig;
        this.employeRepo = employeRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping()
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO ){
        return this.employeRepo.findByEmail(loginDTO.getEmail())
                .filter( employe ->  passwordEncoder.matches(loginDTO.getPassword(), employe.getPassword()))
                .map( employe -> ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,buildJWTCookie(employe)).build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    private String buildJWTCookie(Employe employe){
        Keys.secretKeyFor(SignatureAlgorithm.HS512);

        String jetonJWT = Jwts.builder()
                .setSubject(employe.getEmail())
                .addClaims(Map.of("roles",employe.getRoles()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpireIn() * 1000))
                .signWith(
                        jwtConfig.getSecretKey()
                ).compact();

        ResponseCookie tokenCookie = ResponseCookie.from(jwtConfig.getCookie(),jetonJWT)
                .httpOnly(true)
                .maxAge(jwtConfig.getExpireIn()*1000)
                .path("/")
                .build();
        return tokenCookie.toString();


    }

}
