package org.springboot.kitchensink.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {

	@Id
	private String id;
	private String username; // login name
	private String userId; // Id assigned by identity provider

	@DBRef
	private Member member;

	// Constructors
	public User() {
	}

	public User(String username, String userId) {
		super();
		this.username = username;
		this.userId = userId;
	}

	// Getters and Setters

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member members) {
		this.member = members;
	}
	
}