package com.example.assignment1.controller;

import com.example.assignment1.model.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/healthz")
public class TestController {

    @GetMapping()
    public ResponseEntity<?> getHealth() {
        return new ResponseEntity<UserDto>( HttpStatus.OK);
    }
}
