package com.example.invoiceDetailsExtraction;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface InvoiceDetailsService {
	public void createInvoiceDetails(MultipartFile file);
	
	public List<InvoiceDetails> getAllInvoicedetails();
}
