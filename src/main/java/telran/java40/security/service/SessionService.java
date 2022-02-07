package telran.java40.security.service;

import telran.java40.accounting.model.Account;

public interface SessionService {
	
	Account addUser(String sessionId, Account userAccount);
	
	Account getUser(String sessionId);
	
	Account removeUser(String sessionId);

}
