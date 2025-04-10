package org.springboot.kitchensink.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springboot.kitchensink.collections.Member;
import org.springboot.kitchensink.config.model.AdminProperties;
import org.springboot.kitchensink.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
	
	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
	
	@Autowired
	private AdminProperties adminProps;
	

    @Bean
    CommandLineRunner initDatabase(MemberRepository memberRepository) {
    	return args -> {
            try {
                if (memberRepository.count() == 0) {
                    memberRepository.save(new Member(adminProps.getName(), adminProps.getEmail(), adminProps.getPhoneNumber(), "ADMIN", adminProps.getSub()));
                    log.info("Inserted default member.");
                } else {
                	log.info("Members already exist! skipping default insert.");
                }
            } catch (Exception e) {
            	log.error("Error initializing default data: " + e.getMessage());
                throw e;
            }
        };
    }
}
