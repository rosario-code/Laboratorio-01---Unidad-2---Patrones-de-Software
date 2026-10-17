package com.globalfintech.reports.infrastructure.builder;

import com.globalfintech.reports.domain.model.FinancialReport;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

@Component("PDFBuilder")
@Scope("prototype")
public class PdfReportBuilder extends ConcreteFinancialReportBuilder {

    @Override
    public FinancialReport build() {
        FinancialReport report = super.build();
        report.setContent(toPdf(report));
        report.setContentType("application/pdf");
        return report;
    }

    private byte[] toPdf(FinancialReport report) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfDocument pdf = new PdfDocument(new PdfWriter(output));
            if (report.getWatermark() != null) {
                pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new WatermarkHandler(report.getWatermark(), bold));
            }
            Document document = new Document(pdf);
            document.add(new Paragraph(report.getHeaderContent()).setFont(bold).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(report.getTitle()).setFont(bold).setFontSize(16));
            document.add(new Paragraph("ID: " + report.getId()));
            for (String section : report.getSections()) {
                document.add(new Paragraph(section));
            }
            document.add(new Paragraph(report.getFooterContent()).setFontSize(8).setMarginTop(20));
            document.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return output.toByteArray();
    }

    private static class WatermarkHandler implements IEventHandler {
        private final String text;
        private final PdfFont font;

        WatermarkHandler(String text, PdfFont font) {
            this.text = text;
            this.font = font;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent documentEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = documentEvent.getDocument();
            PdfPage page = documentEvent.getPage();
            Rectangle size = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdf);
            pdfCanvas.saveState();
            pdfCanvas.setExtGState(new PdfExtGState().setFillOpacity(0.2f));
            Canvas canvas = new Canvas(pdfCanvas, size);
            canvas.showTextAligned(new Paragraph(text).setFont(font).setFontSize(60).setFontColor(ColorConstants.GRAY),
                    size.getWidth() / 2, size.getHeight() / 2, pdf.getPageNumber(page),
                    TextAlignment.CENTER, VerticalAlignment.MIDDLE, (float) Math.toRadians(45));
            canvas.close();
            pdfCanvas.restoreState();
        }
    }
}
