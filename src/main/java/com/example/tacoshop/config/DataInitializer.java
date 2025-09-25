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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DiscountCodeRepository discountCodeRepository;
    private final JdbcTemplate jdbcTemplate;
    private final com.example.tacoshop.service.WalletService walletService;
    private final com.example.tacoshop.service.CreditService creditService;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("CREATE SEQUENCE global_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE");
        } catch (DataAccessException ex) {
            System.out.println("Notice: global_seq not created (may already exist or insufficient privilege): " + ex.getMessage());
        }

        if (productRepository.count() == 0) {
            Product taco1 = Product.builder()
                    .name("Classic Beef Taco")
                    .price(BigDecimal.valueOf(15000))
                    .stock(100)
                    .build();
            productRepository.save(taco1);

            Product taco2 = Product.builder()
                    .name("Chicken Taco")
                    .price(BigDecimal.valueOf(14000))
                    .stock(80)
                    .build();
            productRepository.save(taco2);

            Product taco3 = Product.builder()
                    .name("Vegetarian Taco")
                    .price(BigDecimal.valueOf(12000))
                    .stock(60)
                    .build();
            productRepository.save(taco3);

            Product taco4 = Product.builder()
                    .name("Fish Taco")
                    .price(BigDecimal.valueOf(16000))
                    .stock(50)
                    .build();
            productRepository.save(taco4);

            Product taco5 = Product.builder()
                    .name("Supreme Taco")
                    .price(BigDecimal.valueOf(20000))
                    .stock(40)
                    .build();
            productRepository.save(taco5);
        }

        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .role(UserRole.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
            walletService.createWalletForUser(admin);
            creditService.createCreditForUser(admin);

            User seller = User.builder()
                    .username("seller")
                    .password(passwordEncoder.encode("seller"))
                    .role(UserRole.ROLE_CUSTOMER)
                    .build();
            userRepository.save(seller);
            walletService.createWalletForUser(seller);
            creditService.createCreditForUser(seller);
        }

        if (discountCodeRepository.count() == 0) {
            DiscountCode dc1 = DiscountCode.builder()
                    .code("AXFMO")
                    .type(DiscountType.PERCENTAGE)
                    .value(11L)
                    .expiresAt(OffsetDateTime.now().plusDays(10L))
                    .build();
            discountCodeRepository.save(dc1);

            DiscountCode dc2 = DiscountCode.builder()
                    .code("FIXED10K")
                    .type(DiscountType.FIXED_AMOUNT)
                    .value(10000L)
                    .expiresAt(OffsetDateTime.now().plusDays(15L))
                    .build();
            discountCodeRepository.save(dc2);

            DiscountCode dc3 = DiscountCode.builder()
                    .code("PERC_MAX")
                    .type(DiscountType.PERCENTAGE_MAX)
                    .value(20L)
                    .maxAmount(50000L)
                    .expiresAt(OffsetDateTime.now().plusDays(20L))
                    .build();
            discountCodeRepository.save(dc3);
        }
    }

}
