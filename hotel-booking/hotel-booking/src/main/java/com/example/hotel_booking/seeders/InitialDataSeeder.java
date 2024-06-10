package com.example.hotel_booking.seeders;

import com.example.hotel_booking.Enum.Role;
import com.example.hotel_booking.model.User;
import com.example.hotel_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitialDataSeeder implements ApplicationListener<ApplicationReadyEvent> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            userRepository.findByEmail("lankahotels@gmail.com")
                    .ifPresentOrElse(user -> log.info("Admin user already exists."),
                            () -> {
                                User user = User.builder()
                                            .email("lankahotels@gmail.com")
                                        .password(passwordEncoder.encode("12345"))
                                        .firstName("admin")
                                        .lastName("admin")
                                        .role(Role.ADMIN_ROLE)
                                        .build();
                                userRepository.save(user);
                                log.info("Admin user created successfully");
                            });
        } catch (Exception e) {
            log.error("Error creating admin user: ", e);
        }
    }
}
