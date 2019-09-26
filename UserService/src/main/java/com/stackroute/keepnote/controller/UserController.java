package com.stackroute.keepnote.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.keepnote.exceptions.UserAlreadyExistsException;
import com.stackroute.keepnote.exceptions.UserNotFoundException;
import com.stackroute.keepnote.model.User;
import com.stackroute.keepnote.service.UserService;

/*
 * As in this assignment, we are working on creating RESTful web service, hence annotate
 * the class with @RestController annotation. A class annotated with the @Controller annotation
 * has handler methods which return a view. However, if we use @ResponseBody annotation along
 * with @Controller annotation, it will return the data directly in a serialized 
 * format. Starting from Spring 4 and above, we can use @RestController annotation which 
 * is equivalent to using @Controller and @ResposeBody annotation
 * 
 * @CrossOrigin,@EnableFeignClients,@RibbonClient needs to be added.
 */
@RestController
@CrossOrigin
@EnableFeignClients
@RibbonClient(name="category")
@RequestMapping("api/v1")
public class UserController {

	/*
	 * Autowiring should be implemented for the UserService. (Use Constructor-based
	 * autowiring) Please note that we should not create an object using the new
	 * keyword
	 */
	private UserService userService;
	private ResponseEntity<?> responseEntity;
	
	@Autowired
	public UserController(UserService userService) {
		this.userService=userService;
	}

	/*
	 * Define a handler method which will create a specific user by reading the
	 * Serialized object from request body and save the user details in the
	 * database. This handler method should return any one of the status messages
	 * basis on different situations:
	 * 1. 201(CREATED) - If the user created successfully. 
	 * 2. 409(CONFLICT) - If the userId conflicts with any existing user
	 * 
	 * This handler method should map to the URL "/user" using HTTP POST method
	 */
	@PostMapping("user")
	public ResponseEntity<?> createUser(@RequestBody User user) throws UserAlreadyExistsException,UserNotFoundException{
		HttpHeaders headers = new HttpHeaders();
		User u=null;
		try {
			 u= userService.registerUser(user);
		} catch (UserAlreadyExistsException e) {
			responseEntity = new ResponseEntity<>(HttpStatus.CONFLICT);
			return responseEntity;
		}
		if(u==null) {
		headers.add("User Created  - ", String.valueOf(user.getUserId()));
		responseEntity = new ResponseEntity<>(user, headers, HttpStatus.CREATED);
		}else {
			responseEntity = new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		return responseEntity;
	}
	/*
	 * Define a handler method which will update a specific user by reading the
	 * Serialized object from request body and save the updated user details in a
	 * database. This handler method should return any one of the status messages
	 * basis on different situations: 
	 * 1. 200(OK) - If the user updated successfully.
	 * 2. 404(NOT FOUND) - If the user with specified userId is not found.
	 * 
	 * This handler method should map to the URL "/api/v1/user/{id}" using HTTP PUT method.
	 */
	@PutMapping("user/{id}")
	public ResponseEntity<?> updateUserToDB(@PathVariable("id") String id,@RequestBody User user) throws UserNotFoundException{
		User user1 = null;
		try {
			userService.getUserById(id);
		} catch (UserNotFoundException e) {
			responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		try {
			user1= userService.updateUser(id, user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(user1 == null) {
			responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);	
		}else {
			responseEntity = new ResponseEntity<>(HttpStatus.OK);
		}
		return responseEntity;
	}
	/*
	 * Define a handler method which will delete a user from a database.
	 * This handler method should return any one of the status messages basis on
	 * different situations: 
	 * 1. 200(OK) - If the user deleted successfully from database. 
	 * 2. 404(NOT FOUND) - If the user with specified userId is not found.
	 *
	 * This handler method should map to the URL "/api/v1/user/{id}" using HTTP Delete
	 * method" where "id" should be replaced by a valid userId without {}
	 */
	@DeleteMapping("user/{id}")
	public ResponseEntity<?> deleteUserFromDB(@PathVariable("id") String id) throws UserNotFoundException{
		boolean deleted=false;
		try {
			userService.getUserById(id);
		} catch (UserNotFoundException e) {
			responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		try {
		deleted=userService.deleteUser(id);
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(deleted == true) {
			responseEntity = new ResponseEntity<>(HttpStatus.OK);
		}else {
			responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return responseEntity;
	}
	/*
	 * Define a handler method which will show details of a specific user. This
	 * handler method should return any one of the status messages basis on
	 * different situations: 
	 * 1. 200(OK) - If the user found successfully. 
	 * 2. 404(NOT FOUND) - If the user with specified userId is not found. 
	 * This handler method should map to the URL "/api/v1/user/{id}" using HTTP GET method where "id" should be
	 * replaced by a valid userId without {}
	 */
	@GetMapping("user/{id}")
	public ResponseEntity<?> getUserById(@PathVariable("id") String id) throws UserNotFoundException{
		User user1 = null;
		try {
			user1= userService.getUserById(id);
		} catch (UserNotFoundException e) {
			responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if (user1 == null) {
			responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}else {
			responseEntity = new ResponseEntity<>(user1,HttpStatus.OK);
		}
		return responseEntity;
	}
}
