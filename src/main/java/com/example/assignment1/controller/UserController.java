package com.example.assignment1.controller;
//import java.util.UUID;

import java.util.stream.Collectors;

import com.example.assignment1.constants.UserConstants;
import com.example.assignment1.exeception.DataNotFoundExeception;
import com.example.assignment1.exeception.InvalidInputException;
import com.example.assignment1.exeception.UserAuthrizationExeception;
import com.example.assignment1.exeception.UserExistException;
import com.example.assignment1.model.User;
import com.example.assignment1.model.UserUpdateRequestModel;
import com.example.assignment1.service.AuthService;
//import constants.UserConstants;
import com.example.assignment1.service.UserService;
import com.example.assignment1.model.UserDto;
import com.timgroup.statsd.StatsDClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("v1/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	AuthService authService;

	@Autowired
	private StatsDClient statsDClient;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping(value = "/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable("userId") Long userId,HttpServletRequest request){
    	try {
			logger.info("Start of UserController.createUser with userId ");
            statsDClient.incrementCounter("endpoint.getUserDetails.http.get");
    		if(userId.toString().isBlank()||userId.toString().isEmpty()) {
            	throw new InvalidInputException("Enter Valid User Id");
            }
    		authService.isAuthorised(userId,request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<UserDto>( userService.getUserDetails(userId),HttpStatus.OK);
		} catch (InvalidInputException e) {
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
		}
    	catch (UserAuthrizationExeception e) {
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.FORBIDDEN);
		}
    	catch (DataNotFoundExeception e) {
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.NOT_FOUND);
		}
    	catch(Exception e) {
    		return new ResponseEntity<String>(UserConstants.InternalErr,HttpStatus.INTERNAL_SERVER_ERROR);
    	}
        
    }
    
    @PutMapping(value = "/{userId}")
    public ResponseEntity<?> updateUserDetails(@PathVariable("userId") Long userId,@Valid @RequestBody UserUpdateRequestModel user,
    		HttpServletRequest request,Errors error){
    	try {
    		if(userId.toString().isBlank()||userId.toString().isEmpty()) {
            	throw new InvalidInputException("Enter Valid User Id");
            }
    		authService.isAuthorised(userId,request.getHeader("Authorization").split(" ")[1]);
    		if(error.hasErrors()) {
    			String response = error.getAllErrors().stream().map(ObjectError::getDefaultMessage)
    					.collect(Collectors.joining(","));
    			throw new InvalidInputException(response);
    		}
			return new ResponseEntity<String>( userService.updateUserDetails(userId,user),HttpStatus.NO_CONTENT);
		} catch (InvalidInputException e) {
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
		}
    	catch (UserAuthrizationExeception e) {
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.FORBIDDEN);
		}
    	catch (DataNotFoundExeception e) {
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.NOT_FOUND);
		}
    	catch(Exception e) {
    		return new ResponseEntity<String>(UserConstants.InternalErr,HttpStatus.INTERNAL_SERVER_ERROR);
    	}
        
    }
    
    @PostMapping()
    public ResponseEntity<?> createUser(@Valid @RequestBody User user,Errors error){
    	try {
			logger.info("Start of UserController.createUser with userId "+user.getId());
            statsDClient.incrementCounter("endpoint.createUser.http.post");
    		if(error.hasErrors()) {
    			String response = error.getAllErrors().stream().map(ObjectError::getDefaultMessage)
    					.collect(Collectors.joining(","));
    			throw new InvalidInputException(response);
    		}
			return new ResponseEntity<UserDto>( userService.createUser(user),HttpStatus.CREATED);
		} catch (InvalidInputException e) {
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
		}
    	catch (UserExistException e) {
			return new ResponseEntity<String>( e.getMessage(),HttpStatus.BAD_REQUEST);
		}
    	catch(Exception e) {
    		return new ResponseEntity<String>(UserConstants.InternalErr,HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
}

