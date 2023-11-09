package com.example.invoiceDetailsExtraction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/invoicedetails")
public class InvoiceDetailsController {
	@Autowired
	InvoiceDetailsServiceImpl invoiceDetailsServiceImpl;

	@PostMapping
	@CrossOrigin(value="*")
	public ResponseEntity<?> postInvoiceDetails(@RequestParam("file") MultipartFile file) {
		invoiceDetailsServiceImpl.createInvoiceDetails(file);
		return ResponseEntity.ok("File uploaded Successfully");
	}

	@GetMapping
	@CrossOrigin(value="*")
	public List<InvoiceDetails> postInvoiceDetails() {
		return invoiceDetailsServiceImpl.getAllInvoicedetails();
	}

}
