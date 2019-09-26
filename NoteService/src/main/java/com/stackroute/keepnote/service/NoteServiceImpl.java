package com.stackroute.keepnote.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.stackroute.keepnote.exception.NoteNotFoundExeption;
import com.stackroute.keepnote.model.Note;
import com.stackroute.keepnote.model.NoteUser;
import com.stackroute.keepnote.repository.NoteRepository;

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
public class NoteServiceImpl implements NoteService{

	/*
	 * Autowiring should be implemented for the NoteRepository and MongoOperation.
	 * (Use Constructor-based autowiring) Please note that we should not create any
	 * object using the new keyword.
	 */
	private NoteRepository noteRepository;
	private NoteUser noteUser;
	
	@Autowired
	public NoteServiceImpl(NoteRepository noteRepository ) {
		//this.noteUser=noteUser;
		this.noteRepository=noteRepository;
	}
	
	public NoteServiceImpl() {
		
	}
	
	/*
	 * This method should be used to save a new note.
	 */
	public boolean createNote(Note note) {
		List<Note> nList= new ArrayList<Note>();
		nList.add(note);
		NoteUser nu = noteRepository.insert(noteUser);
		if(nu != null) {
		nu.setNotes(nList);
		nu.setUserId(note.getNoteCreatedBy());
		NoteUser note1=noteRepository.save(nu);
		return true;
		}
		else
		return false;
}
	
	
	/* This method should be used to delete an existing note. */

	
	public boolean deleteNote(String userId, int noteId) {
		boolean status = false;
		Optional<NoteUser> optional = noteRepository.findById(userId);
		if(optional.isPresent()) {
			noteRepository.delete(optional.get());
			status = true;
		}
		else
		status= false;
		return status;
	}
	
	/* This method should be used to delete all notes with specific userId. */

	
	public boolean deleteAllNotes(String userId) {
			boolean status = false;
			Optional<NoteUser> optional = noteRepository.findById(userId);
			if(optional.isPresent()) {
				noteRepository.delete(optional.get());
				status = true;
			}
			else
			status= false;
			return status;
	}

	/*
	 * This method should be used to update a existing note.
	 */
	public Note updateNote(Note note, int id, String userId) throws NoteNotFoundExeption {
		NoteUser nu = null;
		List<Note> noteList = new ArrayList<Note>();
		Optional<NoteUser> optional;
		noteList.add(note);
		try {
		optional = noteRepository.findById(userId);
		}catch(Exception e) {
			throw new NoteNotFoundExeption("Note not found");
		}
		if(optional.isPresent()) {
			nu=optional.get();
			nu.setNotes(noteList);
			noteRepository.save(nu);
		}else {
			throw new NoteNotFoundExeption("Note not found");
		}
		return note;
	}

	/*
	 * This method should be used to get a note by noteId created by specific user
	 */
	public Note getNoteByNoteId(String userId, int noteId) throws NoteNotFoundExeption {
		Optional<NoteUser> optional;
		List<Note> noteList;
		NoteUser nu = null;
		Note n=null;
		try {
			optional = noteRepository.findById(userId);
			}catch(Exception e) {
				throw new NoteNotFoundExeption("Note not found");
			}
		if(optional.isPresent()) {
			nu = optional.get();
			noteList=nu.getNotes();
			for(Note n1 :noteList) {
				if(n1.getNoteId()==noteId) {
					n=n1;
				}
			}
			
		}
		return n;
	}

	/*
	 * This method should be used to get all notes with specific userId.
	 */
	public List<Note> getAllNoteByUserId(String userId) {
		List<Note> noteList=null;
		NoteUser nu = null;
		Optional<NoteUser> optional = noteRepository.findById(userId);
		if(optional.isPresent()) {
			nu = optional.get();
			noteList=nu.getNotes();
		}
		return noteList;
	}

}
