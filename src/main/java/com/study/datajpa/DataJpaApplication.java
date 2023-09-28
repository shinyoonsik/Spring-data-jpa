package com.study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
public class DataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataJpaApplication.class, args);
    }

    /**
     * AuditorAware 인터페이스는 Spring Data JPA의 Auditing 기능과 함께 사용되며,
     * 현재의 auditor, 즉 현재 수행 중인 작업의 주체를 알려주는 역할을 한다.
     * 여기서 auditor는 일반적으로 로그인한 사용자를 의미한다.
     */
    @Bean
    public AuditorAware<String> auditorProvider(){
//        익명클래스에서 메소드가 하나면 람다로 바꿀 수 있다???
//        return new AuditorAware<String>() {
//            @Override
//            public Optional<String> getCurrentAuditor() {
//                return Optional.of(UUID.randomUUID().toString());
//            }
//        };

        // 실제로는 UUID자리에 user_id를 인자로 전달하면 된다.
        return () -> Optional.of(UUID.randomUUID().toString());
    }
}
