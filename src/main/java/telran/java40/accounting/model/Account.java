package telran.java40.accounting.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = { "login" })
@Document(collection = "accounts")
public class Account {
	@Id
	String login;
	String password;
	String firstName;
	String lastName;
	Set<String> roles;
	
	public Account() {
		roles = new HashSet<>();
		roles.add("USER");
	}
	
	public Account(String login, String password, String firstName, String lastName) {
		this();
		this.login = login;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public Account changeRoles(String role, boolean isAdd) {
		if (isAdd) {
			roles.add(role.toUpperCase());
		} else {
			roles.remove(role.toUpperCase());
		}
		return this;
	}
	
}
