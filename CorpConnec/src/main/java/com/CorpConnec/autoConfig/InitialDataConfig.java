package com.CorpConnec.autoConfig;

import com.CorpConnec.model.entity.Role;
import com.CorpConnec.model.entity.Status;
import com.CorpConnec.model.entity.User;
import com.CorpConnec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class InitialDataConfig implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitialDataConfig.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminConfig adminConfig;

    @Override
    @Transactional
    public void run(String... args) {
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        if (!userRepository.existsByUsername(adminConfig.getUsername())) {
            User admin = User.builder()
                    .username(adminConfig.getUsername())
                    .password(passwordEncoder.encode(adminConfig.getPassword()))
                    .name("Administrador")
                    .email(adminConfig.getEmail())
                    .status(Status.ACTIVE)
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            logger.info("Usuário admin criado com sucesso.");
        } else {
            logger.info("Usuário admin já existe no sistema.");
        }
    }
}
