package com.opr3.opr3.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opr3.opr3.dto.AuthRequest;
import com.opr3.opr3.dto.AuthResponse;
import com.opr3.opr3.dto.RegisterRequest;
import com.opr3.opr3.dto.TokenInfo;
import com.opr3.opr3.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @Operation(summary = "Authenticate user", description = "Authenticates a user with email and password credentials. Returns an access token and sets a refresh token cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        TokenInfo tokens = authService.authenticate(request);

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, tokens.getRefreshCookie().toString());

        AuthResponse response = new AuthResponse(tokens.getAccessToken());

        log.info("[{}] user logged in", 200);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Register new user", description = "Creates a new user account with the provided email, username, and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input format", content = @Content),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        authService.register(request);
        log.info("[{}] user registered", 201);
        return ResponseEntity.status(201).body("registration successful");
    }

    @Operation(summary = "Refresh access token", description = "Generates new access and refresh tokens using a valid refresh token from cookie. Implements token rotation for enhanced security.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        TokenInfo tokenInfo = authService.refreshToken();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, tokenInfo.getRefreshCookie().toString());
        AuthResponse response = new AuthResponse(tokenInfo.getAccessToken());

        log.info("[{}] token refreshed", 200);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validate access token", description = "Checks if the current access token is valid. Requires Bearer token in Authorization header.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token", content = @Content)
    })
    @GetMapping("/validateToken")
    public ResponseEntity<String> validateToken(HttpServletRequest httpServletRequest) {
        log.info("[{}] token validated", 200);

        return ResponseEntity.ok("valid");
    }

    @Operation(summary = "Logout user", description = "Revokes all valid tokens for the currently authenticated user. Requires Bearer token in Authorization header.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest httpServletRequest) {
        authService.logout();
        log.info("[{}] logout successful", 200);
        return ResponseEntity.ok("logout successful");
    }

}
