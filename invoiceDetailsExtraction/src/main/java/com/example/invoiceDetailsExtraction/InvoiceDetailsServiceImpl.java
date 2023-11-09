package com.example.invoiceDetailsExtraction;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import net.sourceforge.tess4j.Tesseract;



@Service
public class InvoiceDetailsServiceImpl implements InvoiceDetailsService {
	
	private final InvoiceDetailsRepository invoiceDetailsRepository;
	@Autowired
	public InvoiceDetailsServiceImpl(InvoiceDetailsRepository invoiceDetailsRepository){
		this.invoiceDetailsRepository = invoiceDetailsRepository;
	}

	@Override
	public void createInvoiceDetails(MultipartFile file) {
		String outputImagePath = null;
		
		try {
			String outputImageDir = "D:/";
			PDDocument document = PDDocument.load(file.getBytes());
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			for (int page = 0; page < document.getNumberOfPages(); page++) {
				BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300); // 300 DPI
				outputImagePath = outputImageDir + "InvoiceDetails" + (page + 1) + ".jpg";
				ImageIOUtil.writeImage(image, outputImagePath, 300);
			}
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ConvertImageFileToTestFile(outputImagePath);
	
	}

	private void ConvertImageFileToTestFile(String outputImagePath) {
		JSONObject jsonObject = new JSONObject();
		Tesseract tesseract = new Tesseract();
		try {
			tesseract.setDatapath("D:\\tessdata");
			// the path of your tess data folder
			// inside the extracted file
			String text = tesseract.doOCR(new File(outputImagePath));
			FileWriter fw = null;
			try {
				fw = new FileWriter("D:\\test\\testFile.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fw.write(text);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Scanner scanner = new Scanner(Paths.get("D:\\test\\testFile.txt"));
			boolean isSaved =false;
			while (scanner.hasNextLine()) {
				if(isSaved) {
					isSaved =false;
					jsonObject = new JSONObject();
				}
				String line = scanner.nextLine();
				String[] parts = line.trim().split(":");
				String  indexTitle = parts[0];
				String value =parts[1];
				if(indexTitle.trim().equalsIgnoreCase("Invoice Number")) {
					jsonObject.put("Invoice Number", value.trim());
				}else if(indexTitle.trim().equals("Supplier Name")){
					jsonObject.put("Supplier Name", value.trim());
				}else if(indexTitle.trim().equalsIgnoreCase("Invoice Amount")) {
					jsonObject.put("Invoice Amount", value.trim());
				}
				if(jsonObject.has("Invoice Number")&& jsonObject.has("Supplier Name")&&jsonObject.has("Invoice Amount")) {
					InvoiceDetails invoiceDetails = new InvoiceDetails();
					invoiceDetails.setInvoiceNo((String) jsonObject.get("Invoice Number"));
					invoiceDetails.setSupplierName((String) jsonObject.get("Supplier Name"));
					invoiceDetails.setInvoiceAmount((String) jsonObject.get("Invoice Amount"));
					invoiceDetailsRepository.save(invoiceDetails);
					isSaved =true;
				}		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public List<InvoiceDetails> getAllInvoicedetails() {
		
		return (List<InvoiceDetails>) invoiceDetailsRepository.findAll();
	}

}
