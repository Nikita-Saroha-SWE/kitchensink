package org.springboot.kitchensink.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Document(collection = "members")
public class Member {

    @Id
    private String id;

    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    private String name;

    @NotNull
    @Email(message = "Invalid email format")
    private String email;

    @NotNull
    @Size(min = 10, max = 10, message = "Must be 10 digits.")
    @Digits(fraction = 0, integer = 10, message = "Must contain only numbers.")
    private String phoneNumber;
    
    private String role;
    
    private String userId;
    
	public Member() {
		super();
	}
	
	public Member(
			@NotNull @Size(min = 1, max = 25) @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers") String name,
			@NotNull @Email(message = "Invalid email format") String email,
			@NotNull @Size(min = 10, max = 10) @Digits(fraction = 0, integer = 10, message = "Must be numeric with 10 digits.") String phoneNumber,
			String roles) {
		super();
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.role = roles;
	}

	public Member(
			@NotNull @Size(min = 1, max = 25) @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers") String name,
			@NotNull @Email(message = "Invalid email format") String email,
			@NotNull @Size(min = 10, max = 10) @Digits(fraction = 0, integer = 10, message = "Must be numeric with 10 digits.") String phoneNumber,
			String roles, String userId) {
		super();
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.role = roles;
		this.userId = userId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String roles) {
		this.role = roles;
	}
	
}