package com.ar.pckart.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ar.pckart.product.Product;
import com.ar.pckart.product.dto.ProductDetails;
import com.ar.pckart.product.dto.ProductResponse;
import com.ar.pckart.product.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/product")
public class ProductServiceController {

	@Autowired
	private ProductService productService;

	@GetMapping("/byid/{id}")
	public ResponseEntity<ProductResponse> productById(
			@PathVariable Long id){
		return ResponseEntity.ok(productService.getProductById(id));
	}
	
	@PostMapping("/save")
	public ResponseEntity<ProductDetails> saveProduct(
			@RequestParam(name = "file") MultipartFile file,
			@RequestParam(name = "files") MultipartFile[] files,
			@RequestPart(name = "product") Product product,
			HttpServletRequest request
			)  {

		return ResponseEntity.ok(productService.addProduct(file, files, product));
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ProductDetails> updateProduct(
			@PathVariable Long id,
			@RequestParam(name = "file") MultipartFile file,
			@RequestParam(name = "files") MultipartFile[] files,
			@RequestPart(name = "product") Product product,
			HttpServletRequest request
			)  {
		return ResponseEntity.ok(productService.updateProductById(id, file, files, product));
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteProduct(
			@PathVariable("id") Long id, HttpServletRequest request){
		return ResponseEntity.ok(productService.deleteProductById(id));
	}
	
}
