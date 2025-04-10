package org.springboot.kitchensink.repository;

import java.util.List;

import org.springboot.kitchensink.collections.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberRepository extends MongoRepository<Member, String> {
    Member findByEmail(String email);
    List<Member> findAllByOrderByNameAsc();
    Member findByUserId(String userId);
	void deleteByEmail(String testEmail);
}
