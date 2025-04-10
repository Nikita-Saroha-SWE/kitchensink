package org.springboot.kitchensink.service;

import java.util.List;
import java.util.Map;

import org.springboot.kitchensink.collections.Member;
import org.springboot.kitchensink.exception.FieldValidationException;
import org.springboot.kitchensink.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }
    
    public Member registerMemberWithUser(String email, String userId, String role) {
    	Member existing = repository.findByEmail(email);
    	if(existing != null) {
    		return existing;
    	}else {
    		Member member = new Member();
        	member.setEmail(email);
        	member.setUserId(userId);
        	member.setRole(role);
        	return repository.save(member);
    	}
    }

    public Member register(Member member) {
    	return repository.save(member);
    }
    
    public Member updateMember(Member input) {
        Member existing = repository.findByEmail(input.getEmail());
        if (existing != null && !existing.getId().equalsIgnoreCase(input.getId())) {
        	throw new FieldValidationException(Map.of("email", "Email already exists"));
        }
        return repository.save(input);
    }

    public List<Member> listMembers() {
        return repository.findAllByOrderByNameAsc();
    }
    
    public Member findById(String id) {
    	return repository.findById(id).orElse(null);
    }

	public Member findByEmail(String email) {
		return repository.findByEmail(email);
	}
	
	public Member findByUserId(String userId) {
		return repository.findByUserId(userId);
	}

	public void deleteById(String id) {
		repository.deleteById(id);
	}
}
