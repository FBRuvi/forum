package telran.java40;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import telran.java40.accounting.dao.AccountRepository;
import telran.java40.accounting.model.Account;

@SpringBootApplication
public class ForumServiceApplication implements CommandLineRunner{
	
	@Autowired
	AccountRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(ForumServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (!repository.existsById("admin")) {
			Account account = new Account("admin", BCrypt.hashpw("admin", BCrypt.gensalt()), "", "");
			account.changeRoles("moderator", true);
			account.changeRoles("administrator", true);
			repository.save(account);
		}
		
	}

}
