package dev.products.users;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAccountSeeder implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.seed-user.username}")
    private String seedUsername;

    @Value("${app.security.seed-user.password}")
    private String seedPassword;

    @Override
    public void run(String... args) {
        if (userAccountRepository.findByUsername(seedUsername).isPresent()) {
            return;
        }

        userAccountRepository.save(
                UserAccount.builder()
                        .username(seedUsername)
                        .password(passwordEncoder.encode(seedPassword))
                        .role("ROLE_USER")
                        .build()
        );
    }
}
