package com.betterjavacode.productservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {
	@GetMapping("/products")
	public List getAllProducts() {
		List products = new ArrayList<>();
		products.add("Shampoo");
		products.add("Soap");
		products.add("Cleaning Supplies");
		products.add("Dishes");

		return products;
	}
}
