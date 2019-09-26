package com.stackroute.keepnote.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stackroute.keepnote.exceptions.UserAlreadyExistsException;
import com.stackroute.keepnote.exceptions.UserNotFoundException;
import com.stackroute.keepnote.model.User;
import com.stackroute.keepnote.repository.UserRepository;

/*
* Service classes are used here to implement additional business logic/validation 
* This class has to be annotated with @Service annotation.
* @Service - It is a specialization of the component annotation. It doesn't currently 
* provide any additional behavior over the @Component annotation, but it's a good idea 
* to use @Service over @Component in service-layer classes because it specifies intent 
* better. Additionally, tool support and additional behavior might rely on it in the 
* future.
* */
@Service
public class UserServiceImpl implements UserService {

	/*
	 * Autowiring should be implemented for the UserRepository. (Use
	 * Constructor-based autowiring) Please note that we should not create any
	 * object using the new keyword.
	 */
	private UserRepository userRepository;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository=userRepository;
	}
	/*
	 * This method should be used to save a new user.Call the corresponding method
	 * of Respository interface.
	 */

	public User registerUser(User user) throws UserAlreadyExistsException {
		User u;
		Optional<User> optional = userRepository.findById(user.getUserId());
		if(optional.isPresent()) {
			throw new UserAlreadyExistsException("User already exists");
		}else {
			u=  userRepository.insert(user);
		}
		if(u==null) {
			throw new UserAlreadyExistsException("User already exists");
		}else {
			return u;
		}
		
	}

	/*
	 * This method should be used to update a existing user.Call the corresponding
	 * method of Respository interface.
	 */

	public User updateUser(String userId,User user) throws UserNotFoundException {
		User cat = null;
		Optional<User> optional = userRepository.findById(userId);
		if(optional.isPresent()) {
			cat = optional.get();
			cat.setUserId(user.getUserId());
			cat.setUserName(user.getUserName());
			cat.setUserPassword(user.getUserPassword());
			cat.setUserMobile(user.getUserMobile());
			cat.setUserAddedDate(new Date());
			userRepository.save(cat);
		}
		return cat;
		
	}

	/*
	 * This method should be used to delete an existing user. Call the corresponding
	 * method of Respository interface.
	 */

	public boolean deleteUser(String userId) throws UserNotFoundException {
		boolean status = false;
		Optional<User> optional = userRepository.findById(userId);
		if(optional.isPresent()) {
			userRepository.delete(optional.get());
			status = true;
		}
		else
			throw new UserNotFoundException("User doesnot exist");
				
		return status;
		
	}

	/*
	 * This method should be used to get a user by userId.Call the corresponding
	 * method of Respository interface.
	 */

	public User getUserById(String userId) throws UserNotFoundException {
		User cat = null;
		Optional<User> optional = null;
		try {
		optional = userRepository.findById(userId);
		}catch(Exception e) {
			throw new UserNotFoundException("User Not found");
		}
		if(optional.isPresent()) {
			cat = optional.get();
		}
		else
			throw new UserNotFoundException("User Not found");
		
		return cat;
		
	}

}
