package com.stackroute.keepnote.controller;

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

import com.stackroute.keepnote.exception.CategoryDoesNoteExistsException;
import com.stackroute.keepnote.exception.CategoryNotCreatedException;
import com.stackroute.keepnote.exception.CategoryNotFoundException;
import com.stackroute.keepnote.model.Category;
import com.stackroute.keepnote.service.CategoryService;

/*
 * As in this assignment, we are working with creating RESTful web service, hence annotate
 * the class with @RestController annotation.A class annotated with @Controller annotation
 * has handler methods which returns a view. However, if we use @ResponseBody annotation along
 * with @Controller annotation, it will return the data directly in a serialized 
 * format. Starting from Spring 4 and above, we can use @RestController annotation which 
 * is equivalent to using @Controller and @ResposeBody annotation
 * 
 * @CrossOrigin,@EnableFeignClients and @RibbonClient needs to be added 
 * 
 */
@RestController
@CrossOrigin
@EnableFeignClients
@RibbonClient(name="category")
@RequestMapping("api/v1")
public class CategoryController {

	/*
	 * Autowiring should be implemented for the CategoryService. (Use
	 * Constructor-based autowiring) Please note that we should not create any
	 * object using the new keyword
	 */
	
	private CategoryService categoryService;
	private ResponseEntity<?> responseEntity;
	
	@Autowired
	public CategoryController(CategoryService categoryService) {
		super();
		this.categoryService = categoryService;
	}
	/*
	 * Define a handler method which will create a category by reading the
	 * Serialized category object from request body and save the category in
	 * database. Please note that the careatorId has to be unique.This
	 * handler method should return any one of the status messages basis on
	 * different situations: 
	 * 1. 201(CREATED - In case of successful creation of the category
	 * 2. 409(CONFLICT) - In case of duplicate categoryId
	 *
	 * 
	 * This handler method should map to the URL "/api/v1/category" using HTTP POST
	 * method".
	 */
	@PostMapping("category")
	public ResponseEntity<?> createCategory(@RequestBody Category category) throws CategoryNotCreatedException,CategoryNotFoundException{
		Category c = null;
		try {
		c =  categoryService.getCategoryById(category.getId());
		}catch(CategoryNotFoundException e) {
			responseEntity = new ResponseEntity<>("duplicate categoryId", HttpStatus.CONFLICT);
		}
		if(c != null) {
			responseEntity = new ResponseEntity<>("duplicate categoryId", HttpStatus.CONFLICT);
		}else {
			try {
				Category createdCategory = 	categoryService.createCategory(category);
			responseEntity = new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
			} catch (CategoryNotCreatedException e) {
				responseEntity = new ResponseEntity<>("duplicate categoryId", HttpStatus.CONFLICT);
			}
		}
		return responseEntity;
	}
	/*
	 * Define a handler method which will delete a category from a database.
	 * 
	 * This handler method should return any one of the status messages basis on
	 * different situations: 1. 200(OK) - If the category deleted successfully from
	 * database. 2. 404(NOT FOUND) - If the category with specified categoryId is
	 * not found. 
	 * 
	 * This handler method should map to the URL "/api/v1/category/{id}" using HTTP Delete
	 * method" where "id" should be replaced by a valid categoryId without {}
	 */
	@DeleteMapping("category/{id}")
	public ResponseEntity<?> deleteCategoryFromDB(@PathVariable("id") String id) throws CategoryNotFoundException,CategoryDoesNoteExistsException{
		Category c = null;
		boolean b=false;
		try {
		c =  categoryService.getCategoryById(id);
		}catch(CategoryNotFoundException e) {
			responseEntity = new ResponseEntity<>("Category Not found", HttpStatus.NOT_FOUND);
		}
		
			try {
				b=categoryService.deleteCategory(id);
				
			}catch(CategoryDoesNoteExistsException e) {
				responseEntity = new ResponseEntity<>("Category Not found", HttpStatus.NOT_FOUND);
			}
		if(b==true) {
			responseEntity = new ResponseEntity<>("Category with Id:" +id+ " is successfully Deleted",HttpStatus.OK);
		}else
			responseEntity = new ResponseEntity<>("Category Not found", HttpStatus.NOT_FOUND);
		return responseEntity;
	}
	
	/*
	 * Define a handler method which will update a specific category by reading the
	 * Serialized object from request body and save the updated category details in
	 * database. This handler method should return any one of the status
	 * messages basis on different situations: 1. 200(OK) - If the category updated
	 * successfully. 2. 404(NOT FOUND) - If the category with specified categoryId
	 * is not found. 
	 * This handler method should map to the URL "/api/v1/category/{id}" using HTTP PUT
	 * method.
	 */
	@PutMapping("category/{id}")
	public ResponseEntity<?> updateCategoryToDB(@PathVariable("id") String id,@RequestBody Category category) throws CategoryNotFoundException{
		Category c= null; 
		try {
			categoryService.getCategoryById(id);
			}catch(CategoryNotFoundException e) {
				responseEntity = new ResponseEntity<>("Category Not found", HttpStatus.NOT_FOUND);
		}
		c= categoryService.updateCategory(category, id);
		if(c == null) {
			responseEntity = new ResponseEntity<>("Category Not found", HttpStatus.CONFLICT);	
		}else {
			responseEntity = new ResponseEntity<>(category,HttpStatus.OK);
		}
			
		return responseEntity;
	}

	/*
	 * Define a handler method which will get us the category by a userId.
	 * 
	 * This handler method should return any one of the status messages basis on
	 * different situations: 1. 200(OK) - If the category found successfully. 
	 * 
	 * 
	 * This handler method should map to the URL "/api/v1/category" using HTTP GET method
	 */
	@GetMapping("category/{id}")
	public ResponseEntity<?> getCategoryById(@PathVariable("id") String id) throws CategoryNotFoundException{
		Category c = null;
		try {
			c=categoryService.getCategoryById(id);
			}catch(CategoryNotFoundException e) {
				responseEntity = new ResponseEntity<>("Category Not found", HttpStatus.NOT_FOUND);
		}
		if(c == null) {
			responseEntity = new ResponseEntity<>("Category Not found", HttpStatus.NOT_FOUND);
		}else {
			responseEntity = new ResponseEntity<>(c,HttpStatus.OK);
		}
		return responseEntity;
	}

}
