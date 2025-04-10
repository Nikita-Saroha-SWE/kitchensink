package org.springboot.kitchensink.config;

import org.springboot.kitchensink.collections.Member;
import org.springboot.kitchensink.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(MemberRepository memberRepository) {
    	return args -> {
            try {
                if (memberRepository.count() == 0) {
                    memberRepository.save(new Member("Nikita Saroha", "saroha.nikita@gmail.com", "9999303384", "ADMIN","102973542446320471049"));
                    System.out.println("Inserted default member.");
                } else {
                    System.out.println("Members already exist! skipping default insert.");
                }
            } catch (Exception e) {
                System.err.println("Error initializing default data: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
