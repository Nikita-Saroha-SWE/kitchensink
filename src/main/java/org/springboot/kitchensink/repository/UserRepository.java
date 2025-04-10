package org.springboot.kitchensink.repository;

import java.util.List;
import java.util.Optional;

import org.springboot.kitchensink.collections.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
//    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String idpId);
    List<User> findAllByOrderByUsernameAsc();
	void deleteByUsername(String string);
}
