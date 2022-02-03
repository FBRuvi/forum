package telran.java40.accounting.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@ResponseStatus(HttpStatus.NOT_FOUND)
public class LoginNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7198020404872057825L;

	public LoginNotFoundException(String login) {
		super("Account with login " + login + " not found");
	}
}
