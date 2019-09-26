
package com.stackroute.keepnote.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.keepnote.exception.UserAlreadyExistsException;
import com.stackroute.keepnote.exception.UserNotFoundException;
import com.stackroute.keepnote.model.User;
import com.stackroute.keepnote.service.UserAuthenticationService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/*
 * As in this assignment, we are working on creating RESTful web service, hence annotate
 * the class with @RestController annotation. A class annotated with the @Controller annotation
 * has handler methods which return a view. However, if we use @ResponseBody annotation along
 * with @Controller annotation, it will return the data directly in a serialized 
 * format. Starting from Spring 4 and above, we can use @RestController annotation which 
 * is equivalent to using @Controller and @ResposeBody annotation
 * 
 * @CrossOrigin,@EnableFeignClients,@RibbonClient
 * 
 */
@RestController
@CrossOrigin
@EnableFeignClients
@RibbonClient(name="user-authentication")
@RequestMapping("api/v1")
public class UserAuthenticationController {

	/*
	 * Autowiring should be implemented for the UserAuthenticationService. (Use
	 * Constructor-based autowiring) Please note that we should not create an object
	 * using the new keyword
	 */
	private UserAuthenticationService authicationService;
	private ResponseEntity<?> responseEntity;
	Map<String,String> map = new HashMap<>();
	
	@Autowired
    public UserAuthenticationController(UserAuthenticationService authicationService) {
    	this.authicationService=authicationService;
	}

/*
	 * Define a handler method which will create a specific user by reading the
	 * Serialized object from request body and save the user details in the
	 * database. This handler method should return any one of the status messages
	 * basis on different situations:
	 * 1. 201(CREATED) - If the user created successfully. 
	 * 2. 409(CONFLICT) - If the userId conflicts with any existing user
	 * 
	 * This handler method should map to the URL "/api/v1/auth/register" using HTTP POST method
	 */
	@PostMapping("auth/register")
	public ResponseEntity<?> createCategory(@RequestBody User user) throws UserNotFoundException{
		try {
			authicationService.saveUser(user);
			responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
		} catch (UserAlreadyExistsException e) {
			responseEntity = new ResponseEntity<>(HttpStatus.CONFLICT);
			return responseEntity;
		}
		return responseEntity;
	}



	/* Define a handler method which will authenticate a user by reading the Serialized user
	 * object from request body containing the username and password. The username and password should be validated 
	 * before proceeding ahead with JWT token generation. The user credentials will be validated against the database entries. 
	 * The error should be return if validation is not successful. If credentials are validated successfully, then JWT
	 * token will be generated. The token should be returned back to the caller along with the API response.
	 * This handler method should return any one of the status messages basis on different
	 * situations:
	 * 1. 200(OK) - If login is successful
	 * 2. 401(UNAUTHORIZED) - If login is not successful
	 * 
	 * This handler method should map to the URL "/api/v1/auth/login" using HTTP POST method
	*/
	@PostMapping("auth/login")
	public ResponseEntity<?> doLogin(@RequestBody User user) {
		String jwtToken = "";
		try {
		jwtToken = 	getToken(user.getUserId(),user.getUserPassword());
		map.clear();
		map.put("message", "User Successfully Logged In");
		map.put("token", jwtToken);
		} catch (Exception e) {
			map.clear();
			map.put("message", e.getMessage());
			map.put("token", null);
			return new ResponseEntity<>(map,HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<>(map,HttpStatus.OK);
	}






// Generate JWT token
	public String getToken(String username, String password) throws Exception {
		if(username == null || password == null) {
			throw new ServletException("Please provide proper username and password");
		}
		
				
	   String jwtToken = 	Jwts.builder()
		    .setSubject(username)
		    .setIssuedAt(new Date())
		    .setExpiration(new Date(System.currentTimeMillis() + 30000 ))
		    .signWith(SignatureAlgorithm.HS256, "secretkey").compact();
		
		return jwtToken;

        
        
}


}
