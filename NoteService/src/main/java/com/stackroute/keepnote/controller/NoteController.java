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

import com.stackroute.keepnote.exception.NoteNotFoundExeption;
import com.stackroute.keepnote.model.Note;
import com.stackroute.keepnote.service.NoteService;

/*
 * As in this assignment, we are working with creating RESTful web service, hence annotate
 * the class with @RestController annotation.A class annotated with @Controller annotation
 * has handler methods which returns a view. However, if we use @ResponseBody annotation along
 * with @Controller annotation, it will return the data directly in a serialized 
 * format. Starting from Spring 4 and above, we can use @RestController annotation which 
 * is equivalent to using @Controller and @ResposeBody annotation
 * 
 * @CrossOrigin, @EnableFeignClients and @RibbonClient needs to be added 
 */
@RestController
@CrossOrigin
@EnableFeignClients
@RibbonClient(name="note")
@RequestMapping("api/v1")
public class NoteController {

	/*
	 * Autowiring should be implemented for the NoteService. (Use Constructor-based
	 * autowiring) Please note that we should not create any object using the new
	 * keyword
	 */
	private NoteService noteService;
	private ResponseEntity<?> responseEntity;
		
		@Autowired
		public NoteController(NoteService noteService) {
			this.noteService=noteService;
		}
		
		
		/*
		 * Define a handler method which will create a specific note by reading the
		 * Serialized object from request body and save the note details in the
		 * database.This handler method should return any one of the status messages
		 * basis on different situations: 
		 * 1. 201(CREATED) - If the note created successfully. 
		 * 2. 409(CONFLICT) - If the noteId conflicts with any existing user.
		 * 
		 * This handler method should map to the URL "/api/v1/note" using HTTP POST method
		 */
		@PostMapping("note")
		public ResponseEntity<?> createNote(@RequestBody Note note) {
			boolean b= noteService.createNote(note);
			if(b==true) {
				responseEntity = new ResponseEntity<>(note, HttpStatus.CREATED);
			}else {
				responseEntity = new ResponseEntity<>("duplicate noteId", HttpStatus.CONFLICT);
			}
			return responseEntity;
		}
		/*
		 * Define a handler method which will delete a note from a database.
		 * This handler method should return any one of the status messages basis 
		 * on different situations: 
		 * 1. 200(OK) - If the note deleted successfully from database. 
		 * 2. 404(NOT FOUND) - If the note with specified noteId is not found.
		 *
		 * This handler method should map to the URL "/api/v1/note/{id}" using HTTP Delete
		 * method" where "id" should be replaced by a valid noteId without {}
		 */
		@DeleteMapping("note/{userId}/{id}")
		public ResponseEntity<?> deleteNoteFromDB(@PathVariable("userId") String userId,@PathVariable("id") int id) {
			boolean b= noteService.deleteNote(userId, id);
			if(b==true) {
				responseEntity = new ResponseEntity<>(HttpStatus.OK);
			}else {
				responseEntity = new ResponseEntity<>("duplicate noteId", HttpStatus.NOT_FOUND);
			}
			return responseEntity;
		}
		
		@DeleteMapping("note/{userId}")
		public ResponseEntity<?> deleteAllNotes(@PathVariable("userId") String userId) {
			System.out.println("-------------------------->in delete");
			boolean b = false;
			try {
				b = noteService.deleteAllNotes(userId);
			} catch (NoteNotFoundExeption e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("-------------------------->boolean "+b);
			if(b==true) {
				responseEntity = new ResponseEntity<>(HttpStatus.OK);
			}else {
				responseEntity = new ResponseEntity<>("duplicate noteId", HttpStatus.NOT_FOUND);
			}
			return responseEntity;
		}
		/*
		 * Define a handler method which will update a specific note by reading the
		 * Serialized object from request body and save the updated note details in a
		 * database. 
		 * This handler method should return any one of the status messages
		 * basis on different situations: 
		 * 1. 200(OK) - If the note updated successfully.
		 * 2. 404(NOT FOUND) - If the note with specified noteId is not found.
		 * 
		 * This handler method should map to the URL "/api/v1/note/{id}" using HTTP PUT method.
		 */
		@PutMapping("note/{userId}/{id}")
		public ResponseEntity<?> updateNoteToDB(@PathVariable("userId") String userId,@PathVariable("id") int id,@RequestBody Note note) throws NoteNotFoundExeption{
			Note c= null; 
			try {
			c= noteService.updateNote(note, id, userId);
			}catch(NoteNotFoundExeption e) {
				responseEntity = new ResponseEntity<>("Note Not found", HttpStatus.NOT_FOUND);
			}
			if(c == null) {
				responseEntity = new ResponseEntity<>("Note Not found", HttpStatus.NOT_FOUND);	
			}else {
				responseEntity = new ResponseEntity<>(note,HttpStatus.OK);
			}
			return responseEntity;
		}
		/*
		 * Define a handler method which will get us the all notes by a userId.
		 * This handler method should return any one of the status messages basis on
		 * different situations: 
		 * 1. 200(OK) - If the note found successfully. 
		 * 
		 * This handler method should map to the URL "/api/v1/note" using HTTP GET method
		 */
		@GetMapping("note/{userId}/{id}")
		public ResponseEntity<?> getNoteById(@PathVariable("userId") String userId,@PathVariable("id") int id) throws NoteNotFoundExeption{
			Note c= null; 
			try {
			c= noteService.getNoteByNoteId(userId, id);
			}catch(NoteNotFoundExeption e) {
				responseEntity = new ResponseEntity<>("Note Not found", HttpStatus.NOT_FOUND);
			}
			if(c == null) {
				responseEntity = new ResponseEntity<>("Note Not found", HttpStatus.NOT_FOUND);	
			}else {
				responseEntity = new ResponseEntity<>(HttpStatus.OK);
			}
			return responseEntity;
		}
		/*
		 * Define a handler method which will show details of a specific note created by specific 
		 * user. This handler method should return any one of the status messages basis on
		 * different situations: 
		 * 1. 200(OK) - If the note found successfully. 
		 * 2. 404(NOT FOUND) - If the note with specified noteId is not found.
		 * This handler method should map to the URL "/api/v1/note/{userId}/{noteId}" using HTTP GET method
		 * where "id" should be replaced by a valid reminderId without {}
		 * 
		 */
		@GetMapping("note/{userId}")
		public ResponseEntity<?> getAllNotesByUserId(@PathVariable("userId") String userId) throws NoteNotFoundExeption{
			List<Note> noteList=null;
				noteList= noteService.getAllNoteByUserId(userId);
				if(noteList == null) {
					responseEntity = new ResponseEntity<>("Note Not found", HttpStatus.OK);	
				}else {
					responseEntity = new ResponseEntity<>(HttpStatus.OK);
				}
			return responseEntity;
		}

	}