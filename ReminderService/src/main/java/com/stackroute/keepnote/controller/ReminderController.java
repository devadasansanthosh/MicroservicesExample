package com.stackroute.keepnote.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
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

import com.stackroute.keepnote.exception.ReminderNotCreatedException;
import com.stackroute.keepnote.exception.ReminderNotFoundException;
import com.stackroute.keepnote.model.Reminder;
import com.stackroute.keepnote.service.ReminderService;

/*
 * As in this assignment, we are working with creating RESTful web service, hence annotate
 * the class with @RestController annotation.A class annotated with @Controller annotation
 * has handler methods which returns a view. However, if we use @ResponseBody annotation along
 * with @Controller annotation, it will return the data directly in a serialized 
 * format. Starting from Spring 4 and above, we can use @RestController annotation which 
 * is equivalent to using @Controller and @ResposeBody annotation
 * 
 * @CrossOrigin,@EnableFeignClients and @RibbonClient
 *
 */
@RestController
@CrossOrigin
@EnableFeignClients
@RibbonClient(name="category")
@RequestMapping("api/v1")
public class ReminderController {

	/*
	 * From the problem statement, we can understand that the application requires
	 * us to implement five functionalities regarding reminder. They are as
	 * following:
	 * 
	 * 1. Create a reminder 
	 * 2. Delete a reminder 
	 * 3. Update a reminder 
	 * 4. Get all reminders by userId 
	 * 5. Get a specific reminder by id.
	 * 
	 */
	private ReminderService reminderService;
	private ResponseEntity<?> responseEntity;
	/*
	 * Autowiring should be implemented for the ReminderService. (Use
	 * Constructor-based autowiring) Please note that we should not create any
	 * object using the new keyword
	 */
	@Autowired
	public ReminderController(ReminderService reminderService) {
		super();
		this.reminderService = reminderService;
	}
	
	/*
	 * Define a handler method which will create a reminder by reading the
	 * Serialized reminder object from request body and save the reminder in
	 * database. Please note that the reminderId has to be unique. This handler
	 * method should return any one of the status messages basis on different
	 * situations: 
	 * 1. 201(CREATED - In case of successful creation of the reminder
	 * 2. 409(CONFLICT) - In case of duplicate reminder ID
	 *
	 * This handler method should map to the URL "/api/v1/reminder" using HTTP POST
	 * method".
	 */
	@PostMapping("reminder")
	public ResponseEntity<?> createReminder(@RequestBody Reminder reminder) throws ReminderNotCreatedException,ReminderNotFoundException{
		Reminder c = null;
		try {
		c =  reminderService.getReminderById(reminder.getReminderId());
		}catch(ReminderNotFoundException e) {
			responseEntity = new ResponseEntity<>("duplicate reminderId", HttpStatus.CONFLICT);
		}
		if(c != null) {
			responseEntity = new ResponseEntity<>("duplicate reminderId", HttpStatus.CONFLICT);
		}else {
			try {
				Reminder createdReminder = 	reminderService.createReminder(reminder);
			responseEntity = new ResponseEntity<>(createdReminder, HttpStatus.CREATED);
			} catch (ReminderNotCreatedException e) {
				responseEntity = new ResponseEntity<>("duplicate reminderId", HttpStatus.CONFLICT);
			}
		}
		return responseEntity;
	}
	/*
	 * Define a handler method which will delete a reminder from a database.
	 * 
	 * This handler method should return any one of the status messages basis on
	 * different situations: 
	 * 1. 200(OK) - If the reminder deleted successfully from database. 
	 * 2. 404(NOT FOUND) - If the reminder with specified reminderId is not found.
	 * 
	 * This handler method should map to the URL "/api/v1/reminder/{id}" using HTTP Delete
	 * method" where "id" should be replaced by a valid reminderId without {}
	 */
	@DeleteMapping("reminder/{id}")
	public ResponseEntity<?> deleteReminderFromDB(@PathVariable("id") String id) throws ReminderNotFoundException{
		Reminder c = null;
		boolean b=false;
		try {
		c =  reminderService.getReminderById(id);
		}catch(ReminderNotFoundException e) {
			responseEntity = new ResponseEntity<>("Reminder Not found", HttpStatus.NOT_FOUND);
		}
		
			try {
				b=reminderService.deleteReminder(id);
				
			}catch(ReminderNotFoundException e) {
				responseEntity = new ResponseEntity<>("Reminder Not found", HttpStatus.NOT_FOUND);
			}
		if(b==true) {
			responseEntity = new ResponseEntity<>("Reminder with Id:" +id+ " is successfully Deleted",HttpStatus.OK);
		}else
			responseEntity = new ResponseEntity<>("Reminder Not found", HttpStatus.NOT_FOUND);
		return responseEntity;
	}
	/*
	 * Define a handler method which will update a specific reminder by reading the
	 * Serialized object from request body and save the updated reminder details in
	 * a database. This handler method should return any one of the status messages
	 * basis on different situations: 
	 * 1. 200(OK) - If the reminder updated successfully. 
	 * 2. 404(NOT FOUND) - If the reminder with specified reminderId is not found. 
	 * 
	 * This handler method should map to the URL "/api/v1/reminder/{id}" using HTTP PUT
	 * method.
	 */
	@PutMapping("reminder/{id}")
	public ResponseEntity<?> updateReminderToDB(@PathVariable("id") String id,@RequestBody Reminder reminder) throws ReminderNotFoundException{
		Reminder c= null; 
		try {
			reminderService.getReminderById(id);
			}catch(ReminderNotFoundException e) {
				responseEntity = new ResponseEntity<>("Reminder Not found", HttpStatus.NOT_FOUND);
		}
		try {
		c= reminderService.updateReminder(reminder, id);
		}catch(ReminderNotFoundException e) {
			responseEntity = new ResponseEntity<>("Reminder Not found", HttpStatus.NOT_FOUND);
		}
		if(c == null) {
			responseEntity = new ResponseEntity<>("Reminder Not found", HttpStatus.NOT_FOUND);	
		}else {
			responseEntity = new ResponseEntity<>(reminder,HttpStatus.OK);
		}
			
		return responseEntity;
	}
	/*
	 * Define a handler method which will show details of a specific reminder. This
	 * handler method should return any one of the status messages basis on
	 * different situations: 
	 * 1. 200(OK) - If the reminder found successfully. 
	 * 2. 404(NOT FOUND) - If the reminder with specified reminderId is not found. 
	 * 
	 * This handler method should map to the URL "/api/v1/reminder/{id}" using HTTP GET method
	 * where "id" should be replaced by a valid reminderId without {}
	 */
	@GetMapping("reminder/{id}")
	public ResponseEntity<?> getReminderById(@PathVariable("id") String id) throws ReminderNotFoundException{
		Reminder c = null;
		try {
			c=reminderService.getReminderById(id);
			}catch(ReminderNotFoundException e) {
				responseEntity = new ResponseEntity<>("Reminder Not found", HttpStatus.NOT_FOUND);
		}
		if(c == null) {
			responseEntity = new ResponseEntity<>("Reminder Not found", HttpStatus.NOT_FOUND);
		}else {
			responseEntity = new ResponseEntity<>(c,HttpStatus.OK);
		}
		return responseEntity;
	}
	/*
	 * Define a handler method which will get us the all reminders.
	 * This handler method should return any one of the status messages basis on
	 * different situations: 
	 * 1. 200(OK) - If the reminder found successfully. 
	 * 2. 404(NOT FOUND) - If the reminder with specified reminderId is not found.
	 * 
	 * This handler method should map to the URL "/api/v1/reminder" using HTTP GET method
	 */
	@GetMapping("reminder")
	public ResponseEntity<?> getAllReminders() throws ReminderNotFoundException{
		
		List<Reminder> reminderList = reminderService.getAllReminders();
		responseEntity = new ResponseEntity<>(reminderList, HttpStatus.OK);
		return responseEntity;
	}
}
