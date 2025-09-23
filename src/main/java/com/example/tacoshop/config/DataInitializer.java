package com.example.tacoshop.config;

import com.example.tacoshop.entity.DiscountCode;
import com.example.tacoshop.entity.Product;
import com.example.tacoshop.entity.User;
import com.example.tacoshop.entity.type.DiscountType;
import com.example.tacoshop.entity.type.UserRole;
import com.example.tacoshop.repository.DiscountCodeRepository;
import com.example.tacoshop.repository.ProductRepository;
import com.example.tacoshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DiscountCodeRepository discountCodeRepository;

    @Override
    public void run(String... args) throws Exception {

        if (productRepository.count() == 0) {
            Product taco1 = Product.builder()
                    .name("Classic Beef Taco")
                    .price(15000L)
                    .stock(100)
                    .build();
            productRepository.save(taco1);
            Product taco2 = Product.builder()
                    .name("Chicken Taco")
                    .price(14000L)
                    .stock(80)
                    .build();
            productRepository.save(taco2);
            Product taco3 = Product.builder()
                    .name("Vegetarian Taco")
                    .price(12000L)
                    .stock(60)
                    .build();
            productRepository.save(taco3);
            Product taco4 = Product.builder()
                    .name("Fish Taco")
                    .price(16000L)
                    .stock(50)
                    .build();
            productRepository.save(taco4);
            Product taco5 = Product.builder()
                    .name("Supreme Taco")
                    .price(20000L)
                    .stock(40)
                    .build();
            productRepository.save(taco5);
        }

        userRepository.findByUsername("admin").ifPresentOrElse((u) -> {
        }, () -> {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .role(UserRole.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
        });

        if (discountCodeRepository.count() == 0) {
            DiscountCode dc1 = DiscountCode.builder()
                    .code("AXFMO")
                    .type(DiscountType.PERCENTAGE)
                    .value(11L)
                    .expiresAt(OffsetDateTime.now().plusDays(10L))
                    .build();
            discountCodeRepository.save(dc1);
        }
    }

}
