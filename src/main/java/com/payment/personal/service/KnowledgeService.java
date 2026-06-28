package com.payment.personal.service;

import com.payment.personal.models.ExtractTextResponse;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class KnowledgeService {

    public ExtractTextResponse extractText(MultipartFile file) throws IOException {
        try(PDDocument pdDocument = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdDocument);
            return ExtractTextResponse.builder()
                    .text(text)
                    .characterCount(text.length())
                    .pages(pdDocument.getNumberOfPages())
                    .build();
        }
    }
}
