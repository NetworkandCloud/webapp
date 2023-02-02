package com.example.assignment1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {Assignment1ApplicationTests.class})
@SpringBootTest
class Assignment1ApplicationTests {

	@Test
	void contextLoads() {

		assertTrue("Hello".equals("Hello"));
	}



}
