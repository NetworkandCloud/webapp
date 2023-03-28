package com.example.assignment1.controller;

import java.awt.PageAttributes.MediaType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;


import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.example.assignment1.constants.UserConstants;
import com.example.assignment1.exeception.BadInputException;
import com.example.assignment1.exeception.DataNotFoundExeception;
import com.example.assignment1.exeception.InvalidInputException;
import com.example.assignment1.model.Image;
import com.example.assignment1.model.Product;
import com.example.assignment1.exeception.UserAuthrizationExeception;
import com.example.assignment1.service.AuthService;
import com.example.assignment1.service.ImageService;
import com.example.assignment1.service.ProductService;
import com.example.assignment1.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController()
@RequestMapping("v1/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @Autowired
    AuthService authservice;

    @Autowired
    ImageService imageService;

	@Autowired
    StatsDClient statsDClient;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

	@RestControllerAdvice
	public class MyExceptionHandler {
		@ExceptionHandler(MethodArgumentNotValidException.class)
		public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
			List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
			String errorMessage = fieldErrors.stream().map(FieldError::getDefaultMessage)
					.collect(Collectors.joining(", "));
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping()
	public ResponseEntity<?> createProduct(@Valid @RequestBody Product product, HttpServletRequest request) {
			try {
				logger.info("This is Product Post method for Creating Product: ");
				statsDClient.incrementCounter("endpoint.productCreate.http.post");
			return new ResponseEntity<Product>(
					productService.createProduct(product,
							authservice.getUserNameFromToken(request.getHeader("Authorization").split(" ")[1])),
					HttpStatus.CREATED);
		} catch (InvalidInputException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{productId}")
	public ResponseEntity<?> getProduct(@PathVariable("productId") Long productId) {
		try {
			logger.info("This is Product  method for Getting a Product: ");
            statsDClient.incrementCounter("endpoint.getProduct.http.get");
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			return new ResponseEntity<Product>(productService.getProduct(productId), HttpStatus.OK);
		} catch (InvalidInputException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (DataNotFoundExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping(value = "/{productId}")
	public ResponseEntity<?> updateProductDetails(@PathVariable("productId") Long productId,
			@Valid @RequestBody Product product, HttpServletRequest request, Errors error) {
			try {
				logger.info("This is Product  method for Updating a Product: ");
				statsDClient.incrementCounter("endpoint.updateProductDetails.http.put");
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			authservice.isAuthorised(productService.getProduct(productId).getOwnerUserId(),
					request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<String>(productService.updateProductDetails(productId, product),
					HttpStatus.NO_CONTENT);
		} catch (InvalidInputException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (DataNotFoundExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			System.out.println(e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PatchMapping(value = "/{productId}")
	public ResponseEntity<?> patchProduct(@PathVariable("productId") Long productId,
			@RequestBody Map<String, Object> updates, HttpServletRequest request) {
		try {
			logger.info("This is Product  method for Patching a Product: ");
            statsDClient.incrementCounter("endpoint.patchProduct.http.patch");
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			authservice.isAuthorised(productService.getProduct(productId).getOwnerUserId(),
					request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<String>(productService.patchProductDetails(productId, updates),
					HttpStatus.NO_CONTENT);
		} catch (InvalidInputException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (DataNotFoundExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@DeleteMapping(value = "/{productId}")
	public ResponseEntity<?> deleteProduct(@PathVariable("productId") Long productId, HttpServletRequest request) {
		try {
			logger.info("This is Product method for Deleting a Product: ");
            statsDClient.incrementCounter("endpoint.deleteProduct.http.delete");
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			authservice.isAuthorised(productService.getProduct(productId).getOwnerUserId(),
					request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<String>(productService.deleteProductDetails(productId), HttpStatus.NO_CONTENT);
		} catch (InvalidInputException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (DataNotFoundExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			System.out.println(e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "/{product_id}/image", produces = "application/json", consumes = "multipart/form-data")
	public ResponseEntity<?> saveImage(@PathVariable("product_id") Long productId,
			@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		try {
			logger.info("This is Image method for Saving an Image: ");
            statsDClient.incrementCounter("endpoint.saveImage.http.post");
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			Long userId = productService.getProduct(productId).getOwnerUserId();
			System.out.println(userId);
			authservice.isAuthorised(userId, request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<Image>(imageService.saveImage(productId, userId, file), HttpStatus.CREATED);
		} catch (InvalidInputException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (DataNotFoundExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			System.out.println(e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/{product_id}/image", produces = "application/json")
	public ResponseEntity<?> getAllImages(@PathVariable("product_id") Long productId, HttpServletRequest request) {
		try {
			logger.info("This is Image method for Getting Info for all Image: ");
            statsDClient.incrementCounter("endpoint.getAllImages.http.get");
			if (productId.toString().isBlank() || productId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id");
			}
			Long userId = productService.getProduct(productId).getOwnerUserId();
			System.out.println(userId);
			authservice.isAuthorised(userId, request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<List<Image>>(imageService.getAllImages(productId, userId), HttpStatus.OK);
		} catch (InvalidInputException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UserAuthrizationExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (DataNotFoundExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			System.out.println(e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/{product_id}/image/{image_id}", produces = "application/json")
	public ResponseEntity<?> getImage(@PathVariable("product_id") Long productId,
			@PathVariable("image_id") Long imageId, HttpServletRequest request) {
		try {
			logger.info("This is Image method for Getting an Image: ");
            statsDClient.incrementCounter("endpoint.getImage.http.get");
			if (productId.toString().isBlank() || productId.toString().isEmpty() || imageId.toString().isBlank()
					|| imageId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id / ImageId");
			}
			Long userId = productService.getProduct(productId).getOwnerUserId();
			System.out.println(userId);
			authservice.isAuthorised(userId, request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<Image>(imageService.getImage(productId, userId, imageId), HttpStatus.OK);
		} catch (UserAuthrizationExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (InvalidInputException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (BadInputException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (DataNotFoundExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			System.out.println(e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping(value = "/{product_id}/image/{image_id}", produces = "application/json")
	public ResponseEntity<?> deleteImage(@PathVariable("product_id") Long productId,
			@PathVariable("image_id") Long imageId, HttpServletRequest request) {
		try {
			logger.info("This is Image Delete method for Image: ");
            statsDClient.incrementCounter("endpoint.deleteImage.http.delete");
			if (productId.toString().isBlank() || productId.toString().isEmpty() || imageId.toString().isBlank()
					|| imageId.toString().isEmpty()) {
				throw new InvalidInputException("Enter Valid Product Id / ImageId");
			}
			Long userId = productService.getProduct(productId).getOwnerUserId();
			System.out.println(userId);
			authservice.isAuthorised(userId, request.getHeader("Authorization").split(" ")[1]);
			return new ResponseEntity<Image>(imageService.deleteImage(productId, userId, imageId), HttpStatus.NO_CONTENT);
		} catch (UserAuthrizationExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
		} catch (InvalidInputException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (BadInputException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (DataNotFoundExeception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			System.out.println(e);
			return new ResponseEntity<String>(UserConstants.InternalErr, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
