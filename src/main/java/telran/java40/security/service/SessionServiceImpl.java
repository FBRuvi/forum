package telran.java40.security.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import telran.java40.accounting.model.Account;

@Service
public class SessionServiceImpl implements SessionService {
	
	Map<String, Account> users = new ConcurrentHashMap<>();

	@Override
	public Account addUser(String sessionId, Account userAccount) {
		return users.put(sessionId, userAccount);
	}

	@Override
	public Account getUser(String sessionId) {
		return users.get(sessionId);
	}

	@Override
	public Account removeUser(String sessionId) {
		return users.remove(sessionId);
	}

}
