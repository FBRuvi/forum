package telran.java40.accounting.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.java40.accounting.model.Account;

public interface AccountRepository extends MongoRepository<Account, String> {
	
}
