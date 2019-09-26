package com.stackroute.keepnote.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stackroute.keepnote.exception.CategoryDoesNoteExistsException;
import com.stackroute.keepnote.exception.CategoryNotCreatedException;
import com.stackroute.keepnote.exception.CategoryNotFoundException;
import com.stackroute.keepnote.model.Category;
import com.stackroute.keepnote.repository.CategoryRepository;

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
public class CategoryServiceImpl implements CategoryService {

	/*
	 * Autowiring should be implemented for the CategoryRepository. (Use
	 * Constructor-based autowiring) Please note that we should not create any
	 * object using the new keyword.
	 */
	private CategoryRepository categoryRepository;
	@Autowired
	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository=categoryRepository;
	}
	/*
	 * This method should be used to save a new category.Call the corresponding
	 * method of Respository interface.
	 */
	public Category createCategory(Category category) throws CategoryNotCreatedException {
		if(category==null) {
			throw new CategoryNotCreatedException("Category Not created");
		}else {
		Category category1= categoryRepository.insert(category);
		if(category1==null) {
			throw new CategoryNotCreatedException("Category Not created");
		}else
		return category1;
		}
		
	}

	/*
	 * This method should be used to delete an existing category.Call the
	 * corresponding method of Respository interface.
	 */
	public boolean deleteCategory(String categoryId) throws CategoryDoesNoteExistsException {
		boolean status = false;
		Optional<Category> optional = categoryRepository.findById(categoryId);
		if(optional.isPresent()) {
			categoryRepository.delete(optional.get());
			status = true;
		}
		else
			throw new CategoryDoesNoteExistsException("Category doesnot exist");
				
		return status;

	}

	/*
	 * This method should be used to update a existing category.Call the
	 * corresponding method of Respository interface.
	 */
	public Category updateCategory(Category category, String categoryId) {
		Category cat = null;
		Optional<Category> optional = categoryRepository.findById(categoryId);
		if(optional.isPresent()) {
			cat = optional.get();
			cat.setId(category.getId());
			cat.setCategoryName(category.getCategoryName());
			cat.setCategoryDescription(category.getCategoryDescription());
			cat.setCategoryCreationDate(new Date());;
			cat.setCategoryCreatedBy(category.getCategoryCreatedBy());
			categoryRepository.save(cat);
		}
		return cat;
	}

	/*
	 * This method should be used to get a category by categoryId.Call the
	 * corresponding method of Respository interface.
	 */
	public Category getCategoryById(String categoryId) throws CategoryNotFoundException {
		Category cat = null;
		Optional<Category> optional = null;
		try {
		optional = categoryRepository.findById(categoryId);
		}catch(Exception e) {
			throw new CategoryNotFoundException("Category Not found");
		}
		if(optional.isPresent()) {
			cat = optional.get();
		}
		else
			throw new CategoryNotFoundException("Category Not found");
		
		return cat;
}

	/*
	 * This method should be used to get a category by userId.Call the corresponding
	 * method of Respository interface.
	 */
	public List<Category> getAllCategoryByUserId(String userId) {

		return categoryRepository.findAllCategoryByCategoryCreatedBy(userId);
	}


}
