package telran.java40.accounting.service;

import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.java40.accounting.dao.AccountRepository;
import telran.java40.accounting.dto.RolesResponseDto;
import telran.java40.accounting.dto.UserAccountResponseDto;
import telran.java40.accounting.dto.UserRegisterDto;
import telran.java40.accounting.dto.UserUpdateDto;
import telran.java40.accounting.exeptions.LoginExistsExeption;
import telran.java40.accounting.exeptions.LoginNotFoundException;
import telran.java40.accounting.model.Account;

@Service
public class UserAccountServiceImpl implements UserAccountService {

	AccountRepository accountRepository;
	ModelMapper modelMapper;

	@Autowired
	public UserAccountServiceImpl(AccountRepository accountRepository, ModelMapper modelMapper) {
		this.accountRepository = accountRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public UserAccountResponseDto addUser(UserRegisterDto userRegisterDto) {
		if (accountRepository.existsById(userRegisterDto.getLogin())) {
			throw new LoginExistsExeption(userRegisterDto.getLogin());
		}
		Account account = modelMapper.map(userRegisterDto, Account.class);
		String password = BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt());
		account.setPassword(password);
		return modelMapper.map(accountRepository.save(account), UserAccountResponseDto.class);
	}

	@Override
	public UserAccountResponseDto getUser(String login) {
		Account account = accountRepository.findById(login).orElseThrow(() -> new LoginNotFoundException(login));
		return modelMapper.map(account, UserAccountResponseDto.class);

	}

	@Override
	public UserAccountResponseDto removeUser(String login) {
		Account account = accountRepository.findById(login).orElseThrow(() -> new LoginNotFoundException(login));
		accountRepository.delete(account);
		return modelMapper.map(account, UserAccountResponseDto.class);
	}

	@Override
	public UserAccountResponseDto editUser(String login, UserUpdateDto userUpdateDto) {
		Account account = accountRepository.findById(login).orElseThrow(() -> new LoginNotFoundException(login));
		if (userUpdateDto.getFirstName() != null) {
			account.setFirstName(userUpdateDto.getFirstName());
		}
		if (userUpdateDto.getLastName() != null) {
			account.setLastName(userUpdateDto.getLastName());
		}
		return modelMapper.map(accountRepository.save(account), UserAccountResponseDto.class);
	}

	@Override
	public RolesResponseDto changeRolesList(String login, String role, boolean isAddRole) {
		Account account = accountRepository.findById(login).orElseThrow(() -> new LoginNotFoundException(login));
		return modelMapper.map(accountRepository.save(account.changeRoles(role, isAddRole)), RolesResponseDto.class);
	}

	@Override
	public void changePassword(String login, String password) {
		Account account = accountRepository.findById(login).orElseThrow(() -> new LoginNotFoundException(login));
		password = BCrypt.hashpw(password, BCrypt.gensalt());
		account.setPassword(password);
		accountRepository.save(account);
	}

}
