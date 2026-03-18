package dev.products.auth;

import dev.products.auth.dto.SignupRequest;
import dev.products.auth.dto.TokenRequest;
import dev.products.auth.dto.TokenResponse;
import dev.products.users.UserAccount;
import dev.products.users.UserAccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "회원")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/token")
    @Operation(summary = "로그인 (토큰 발급)", description = "바로 실행 클릭하면 됩니다")
    public ResponseEntity<TokenResponse> issueToken(@Valid @RequestBody TokenRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            String accessToken = jwtTokenProvider.createToken(authentication);
            TokenResponse response = TokenResponse.builder()
                    .tokenType("Bearer")
                    .accessToken(accessToken)
                    .expiresIn(jwtTokenProvider.getExpirationMs())
                    .build();

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "바로 실행 클릭하면 됩니다")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest request) {
        if (userAccountRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        UserAccount user = UserAccount.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        userAccountRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
