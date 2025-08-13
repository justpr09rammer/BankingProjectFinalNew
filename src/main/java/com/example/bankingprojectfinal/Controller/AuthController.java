package com.example.bankingprojectfinal.Controller;

import com.example.bankingprojectfinal.DTOS.Auth.LoginRequest;
import com.example.bankingprojectfinal.DTOS.Auth.LoginResponse;
import com.example.bankingprojectfinal.DTOS.Auth.RefreshRequest;
import com.example.bankingprojectfinal.DTOS.Auth.TokenPair;
import com.example.bankingprojectfinal.Service.Security.JwtBlacklistService;
import com.example.bankingprojectfinal.Service.Security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final JwtBlacklistService jwtBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream().findFirst().get().getAuthority().replace("ROLE_", "");
        String access = jwtTokenService.generateAccessToken(username, role);
        String refresh = jwtTokenService.generateRefreshToken(username, role);
        return ResponseEntity.status(HttpStatus.OK).body(
                LoginResponse.builder()
                        .tokens(new TokenPair(access, refresh))
                        .build()
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtTokenService.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String jti = jwtTokenService.getJti(refreshToken);
        if (jwtBlacklistService.isBlacklisted(jti)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = jwtTokenService.getUsername(refreshToken);
        String role = jwtTokenService.getRole(refreshToken);
        String newAccess = jwtTokenService.generateAccessToken(username, role);
        String newRefresh = jwtTokenService.generateRefreshToken(username, role);
        jwtBlacklistService.blacklist(jti, jwtTokenService.getSecondsUntilExpiration(refreshToken));
        return ResponseEntity.ok(new TokenPair(newAccess, newRefresh));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                       @RequestBody(required = false) RefreshRequest body) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            jwtBlacklistService.blacklist(jwtTokenService.getJti(token), jwtTokenService.getSecondsUntilExpiration(token));
        }
        if (body != null && body.getRefreshToken() != null) {
            String rt = body.getRefreshToken();
            jwtBlacklistService.blacklist(jwtTokenService.getJti(rt), jwtTokenService.getSecondsUntilExpiration(rt));
        }
        return ResponseEntity.noContent().build();
    }
}