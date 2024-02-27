package urfumagistracy.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import urfumagistracy.backend.security.JwtCore;
import urfumagistracy.backend.security.UserRepo;

import java.util.HashMap;

@RestController
@RequestMapping("/auth")
public class SecurityController {
    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;
    @Value("${testing.app.lifetime}")
    private int lifetime;
    @Value("${testing.app.refresh-lifetime}")
    private int refreshLifetime;

    @Autowired
    public void SecurityController(UserRepo userRepo, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtCore jwtCore) {
        this.userRepo = userRepo;
        this.jwtCore = jwtCore;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/signin")
    ResponseEntity<?> signin(@RequestBody SigninRequest signinRequest) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getUsername(), signinRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        //SecurityContextHolder.getContext().setAuthentication(authentication); штука для проверки через bearer
        String access = jwtCore.generateToken(authentication.getName(), lifetime);
        String refresh = jwtCore.generateToken(authentication.getName(), refreshLifetime);
        User user = userRepo.findUserByUsername(signinRequest.getUsername()).get();
        user.setRefresh(refresh);
        userRepo.save(user);

        HashMap<String, String> map = new HashMap<>();
        map.put("access_token", access);
        map.put("refresh_token", refresh);
        return ResponseEntity.ok(map);
    }

    @PostMapping("/signup")
    ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        if (userRepo.existsUserByUsername(signupRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different name");
        }
        if (userRepo.existsUserByEmail(signupRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different email");
        }
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        userRepo.save(user);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signupRequest.getUsername(), signupRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String access = jwtCore.generateToken(authentication.getName(), lifetime);
        String refresh = jwtCore.generateToken(authentication.getName(), refreshLifetime);
        user.setRefresh(refresh);
        userRepo.save(user);

        HashMap<String, String> map = new HashMap<>();
        map.put("access_token", access);
        map.put("refresh_token", refresh);
        return ResponseEntity.ok(map);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String refreshToken) {
        User user = userRepo.findUserByRefresh(refreshToken.substring(7));
        System.out.println(user);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not found");
        }
        String access = jwtCore.generateToken(user.getUsername(), lifetime);
        String refresh = jwtCore.generateToken(user.getUsername(), refreshLifetime);
        user.setRefresh(refresh);
        userRepo.save(user);

        HashMap<String, String> map = new HashMap<>();
        map.put("access_token", access);
        map.put("refresh_token", refresh);
        return ResponseEntity.ok(map);
    }

}
