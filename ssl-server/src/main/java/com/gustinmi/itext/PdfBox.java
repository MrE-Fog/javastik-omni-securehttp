package com.gustinmi.itext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.TransformerException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;
import com.gustinmi.cryptotest.Utils;

public class PdfBox {

	public static void main(String[] args) throws TransformerException, IOException {

		String file = "C:\\Users\\gustin\\workspace\\cryptotest\\src\\main\\resources\\test123.pdf";
		String message = "Hello there";
		String fontfile = "C:\\Users\\gustin\\workspace\\cryptotest\\src\\main\\resources\\FreeSans.ttf";

		try (PDDocument doc = new PDDocument()) {
			PDPage page = new PDPage();
			doc.addPage(page);

			// load the font as this needs to be embedded
			PDFont font = PDType0Font.load(doc, new File(fontfile));

			// A PDF/A file needs to have the font embedded if the font is used
			// for text rendering
			// in rendering modes other than text rendering mode 3.
			//
			// This requirement includes the PDF standard fonts, so don't use
			// their static PDFType1Font classes such as
			// PDFType1Font.HELVETICA.
			//
			// As there are many different font licenses it is up to the
			// developer to check if the license terms for the
			// font loaded allows embedding in the PDF.
			//
			if (!font.isEmbedded()) {
				throw new IllegalStateException("PDF/A compliance requires that all fonts used for" + " text rendering in rendering modes other than rendering mode 3 are embedded.");
			}

			// create a page with the message
			try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
				contents.beginText();
				contents.setFont(font, 12);
				contents.newLineAtOffset(100, 700);
				contents.showText(message);
				contents.endText();
			}

			// add XMP metadata
			XMPMetadata xmp = XMPMetadata.createXMPMetadata();

			try {
				DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
				dc.setTitle(file);

				PDFAIdentificationSchema id = xmp.createAndAddPFAIdentificationSchema();
				id.setPart(1);
				id.setConformance("B");

				XmpSerializer serializer = new XmpSerializer();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				serializer.serialize(xmp, baos, true);

				PDMetadata metadata = new PDMetadata(doc);
				metadata.importXMPMetadata(baos.toByteArray());
				doc.getDocumentCatalog().setMetadata(metadata);
			} catch (BadFieldValueException e) {
				// won't happen here, as the provided value is valid
				throw new IllegalArgumentException(e);
			}

			// sRGB output intent
			InputStream colorProfile = PdfBox.class.getResourceAsStream("/sRGB.icc");
			PDOutputIntent intent = new PDOutputIntent(doc, colorProfile);
			intent.setInfo("sRGB IEC61966-2.1");
			intent.setOutputCondition("sRGB IEC61966-2.1");
			intent.setOutputConditionIdentifier("sRGB IEC61966-2.1");
			intent.setRegistryName("http://www.color.org");
			doc.getDocumentCatalog().addOutputIntent(intent);

			doc.save(file);
			doc.close();
			
			System.out.println(Utils.fileToHex("C:\\Users\\gustin\\workspace\\cryptotest\\src\\main\\resources\\test123.pdf"));
		}
	}

}
