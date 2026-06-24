package com.eventsphere.controller;

import com.eventsphere.entity.Certificate;
import com.eventsphere.entity.Event;
import com.eventsphere.entity.Student;

import com.eventsphere.repository.CertificateRepository;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.StudentRepository;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@RestController
public class CertificateController {

    // ---- Palette (matches the target design) ----
    private static final Color GOLD       = new Color(0xC9, 0xA2, 0x27);
    private static final Color GOLD_DARK  = new Color(0xA9, 0x7E, 0x10);
    private static final Color NAVY       = new Color(0x1B, 0x2A, 0x5E);
    private static final Color MAROON     = new Color(0x7A, 0x14, 0x1E);
    private static final Color CREAM      = new Color(0xFB, 0xF6, 0xE8);
    private static final Color TEXT_DARK  = new Color(0x22, 0x22, 0x22);

    // ---- Static asset filenames (src/main/resources/static/) ----
    private static final String LOGO_FILE              = "logo.png";
    private static final String COORDINATOR_SIGN_FILE  = "coordinator-sign.png";
    private static final String PRINCIPAL_SIGN_FILE    = "principal-sign-new.png";

    private final CertificateRepository certificateRepository;
    private final StudentRepository studentRepository;
    private final EventRepository eventRepository;

    public CertificateController(
            CertificateRepository certificateRepository,
            StudentRepository studentRepository,
            EventRepository eventRepository) {

        this.certificateRepository = certificateRepository;
        this.studentRepository = studentRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/certificate/{certificateId}")
    public ResponseEntity<byte[]> generateCertificate(
            @PathVariable Long certificateId) throws Exception {

        Certificate certificate =
                certificateRepository.findById(certificateId)
                        .orElse(null);

        if (certificate == null) {
            return ResponseEntity.notFound().build();
        }

        Student student =
                studentRepository.findById(certificate.getStudentId())
                        .orElse(null);

        Event event =
                eventRepository.findById(certificate.getEventId())
                        .orElse(null);

        if (student == null || event == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Landscape A4. Margins: left/right 50, bottom 20, top 28 (slightly
        // more than bottom) so the logo/heading has breathing room below the
        // gold border frame instead of crowding it — matches the target
        // design's spacing.
        Document document = new Document(PageSize.A4.rotate(), 50, 50, 28, 20);

        PdfWriter writer = PdfWriter.getInstance(document, out);
        // Draws ONLY the cream background + gold border frame on every page.
        writer.setPageEvent(new BorderPageEvent());

        document.open();

        // ---- Fonts ----
        Font collegeFont        = FontFactory.getFont(FontFactory.TIMES_BOLD, 20, NAVY);
        Font autonomousFont     = FontFactory.getFont(FontFactory.TIMES_BOLD, 10, TEXT_DARK);
        Font subInfoFont        = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, TEXT_DARK);
        Font symposiumFont      = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, NAVY);
        Font certificateFont    = FontFactory.getFont(FontFactory.TIMES_BOLD, 34, MAROON);
        Font ofParticipationFont= FontFactory.getFont(FontFactory.TIMES_BOLD, 12, TEXT_DARK);
        Font bodyFont           = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, TEXT_DARK);
        Font nameFont           = FontFactory.getFont(FontFactory.TIMES_BOLD, 22, NAVY);
        Font collegeNameFont    = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, TEXT_DARK);
        Font eventNameFont      = FontFactory.getFont(FontFactory.TIMES_BOLD, 16, MAROON);
        Font boldBodyFont       = FontFactory.getFont(FontFactory.TIMES_BOLD, 10, TEXT_DARK);
        Font smallGrayFont      = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Color.GRAY);
        Font signatureLabelFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 10, TEXT_DARK);

        // ---- Header block: logo (if available) + college name ----
        addHeaderWithLogo(document, collegeFont);

        addCentered(document, "(AUTONOMOUS)", autonomousFont, 1);
        addCentered(document, "Approved by AICTE, New Delhi | Affiliated to Anna University, Chennai", subInfoFont, 1);
        addCentered(document, "Accredited by NAAC (A+ Grade) | Pullipalayam, Tamil Nadu", subInfoFont, 4);

        addRuleLine(document);

        addCentered(document, "NATIONAL LEVEL TECHNICAL SYMPOSIUM", symposiumFont, 6);
        addCentered(document, "CERTIFICATE", certificateFont, 0);
        addCentered(document, "OF PARTICIPATION", ofParticipationFont, 8);

        addCentered(document, "This Certificate is Presented To", bodyFont, 5);
        addCentered(document, student.getName(), nameFont, 3);
        addCentered(document, student.getCollege().toUpperCase(), collegeNameFont, 6);

        addCentered(document, "For active participation in the event", bodyFont, 4);
        addCentered(document, event.getName(), eventNameFont, 6);

        addCentered(document, "Organized by Sri Shanmugha College of Engineering and Technology", bodyFont, 5);
        addCentered(document, "Date of Symposium : " + event.getDate(), boldBodyFont, 2);
        addCentered(document, "Certificate No : " + certificate.getCertificateCode(), smallGrayFont, 8);

        // Seal removed per request (was addSealInFlow(document, writer)).

        // ---- Signature row: real signature images + underline + label ----
        addSignatureRow(document, writer, signatureLabelFont);

        document.close();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=certificate.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private void addCentered(Document document, String text, Font font, float spacingAfter) throws Exception {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(spacingAfter);
        document.add(p);
    }

    private void addRuleLine(Document document) throws Exception {
        Chunk line = new Chunk(new com.lowagie.text.pdf.draw.LineSeparator(1f, 90f, GOLD_DARK, Element.ALIGN_CENTER, -2));
        Paragraph p = new Paragraph();
        p.add(line);
        p.setSpacingAfter(4f);
        document.add(p);
    }

    /**
     * Loads an image from src/main/resources/static/{filename} on the classpath.
     * Returns null (instead of throwing) if the file is missing, so a missing
     * asset degrades gracefully instead of breaking certificate generation.
     */
    private Image loadStaticImage(String filename) {
        try {
            ClassPathResource resource = new ClassPathResource("static/" + filename);
            try (InputStream is = resource.getInputStream()) {
                byte[] bytes = is.readAllBytes();
                return Image.getInstance(bytes);
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Header row: logo on the left (if present), college name centered on
     * the FULL page width.
     *
     * FIX (previous bug): the old version used a 2-column table
     * (logo | name). That made the name's "center" be the center of the
     * narrow text column only — which sits to the right of true page-center
     * because the logo column eats space only on the left. The name also
     * wrapped to two lines at 20pt, breaking visual alignment with the
     * (AUTONOMOUS) / AICTE lines below it, which ARE centered on the full
     * page (they're added directly via addCentered, not inside this table).
     *
     * FIX (this version): a 3-column table (logo | name | empty spacer) of
     * matching outer widths (1f / 7f / 1f) balances the logo visually, so
     * the name's center lines up with true page-center. Font size reduced
     * slightly (20pt -> 17pt) and NoWrap forces a single line, matching the
     * target design where the college name is on ONE line.
     */
    private void addHeaderWithLogo(Document document, Font collegeFont) throws Exception {
        Image logo = loadStaticImage(LOGO_FILE);

        if (logo == null) {
            // No logo found — just center the college name, same as before.
            addCentered(document, "SRI SHANMUGHA COLLEGE OF ENGINEERING AND TECHNOLOGY", collegeFont, 2);
            return;
        }

        logo.scaleToFit(55f, 55f);

        PdfPTable headerTable = new PdfPTable(new float[]{1f, 7f, 1f});
        headerTable.setWidthPercentage(100);

        // --- Logo cell (left) ---
        PdfPCell logoCell = new PdfPCell(logo, false);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(logoCell);

        // --- College name cell (center, full-width balanced) ---
        // Slightly smaller than the original 20pt so it reliably fits on
        // ONE line at this column width without wrapping.
        Font nameFontFit = FontFactory.getFont(FontFactory.TIMES_BOLD, 17, NAVY);

        PdfPCell nameCell = new PdfPCell(
                new Phrase("SRI SHANMUGHA COLLEGE OF ENGINEERING AND TECHNOLOGY", nameFontFit));
        nameCell.setBorder(Rectangle.NO_BORDER);
        nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        nameCell.setNoWrap(true);
        headerTable.addCell(nameCell);

        // --- Spacer cell (right) — mirrors the logo column so the name
        // cell's center matches true page-center ---
        PdfPCell spacerCell = new PdfPCell();
        spacerCell.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(spacerCell);

        headerTable.setSpacingAfter(2f);
        document.add(headerTable);
    }

    /**
     * Signature row: coordinator signature image (left), circular gold
     * "CERTIFIED" seal (center), and principal signature image (right).
     * Each signature has its own underline + label beneath it.
     *
     * FIX (previous bug): the table was only 60% width with 2 columns, which
     * squeezed both signature cells close together — close enough that
     * their two separate bottom-border underlines visually merged into one
     * continuous line, and the signatures themselves looked cramped/close
     * together instead of spread toward the page edges like the target.
     *
     * This version uses a 3-column FULL WIDTH table (signature | seal |
     * signature) so the two signature columns sit near the left/right edges
     * with real horizontal separation, and the center column holds the
     * "CERTIFIED" seal that was previously removed.
     *
     * NOTE on the white box behind each signature: that box is the actual
     * background pixel data baked into coordinator-sign.png / principal-sign.png.
     * iText/OpenPDF just paints whatever pixels the PNG contains — it is not
     * adding a white background itself. To make the box disappear, the PNG
     * files themselves need a transparent background (alpha channel) instead
     * of white.
     */
    private void addSignatureRow(Document document, PdfWriter writer, Font signatureLabelFont) throws Exception {
        PdfPTable sigTable = new PdfPTable(new float[]{3f, 2f, 3f});
        sigTable.setWidthPercentage(85);
        sigTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        Image coordinatorSign = loadStaticImage(COORDINATOR_SIGN_FILE);
        Image principalSign   = loadStaticImage(PRINCIPAL_SIGN_FILE);

        // Row 1: signature images, with the seal in the middle spanning
        // down through the underline/label rows visually via a tall cell.
        sigTable.addCell(signatureImageCell(coordinatorSign));
        sigTable.addCell(sealCell(writer));
        sigTable.addCell(signatureImageCell(principalSign));

        sigTable.addCell(signatureUnderlineCell());
        PdfPCell emptyMiddle1 = new PdfPCell();
        emptyMiddle1.setBorder(Rectangle.NO_BORDER);
        sigTable.addCell(emptyMiddle1);
        sigTable.addCell(signatureUnderlineCell());

        sigTable.addCell(signatureLabelCell("Faculty Coordinator", signatureLabelFont));
        PdfPCell emptyMiddle2 = new PdfPCell();
        emptyMiddle2.setBorder(Rectangle.NO_BORDER);
        sigTable.addCell(emptyMiddle2);
        sigTable.addCell(signatureLabelCell("Principal", signatureLabelFont));

        sigTable.setSpacingBefore(4f);
        document.add(sigTable);
    }

    /**
     * Builds the center cell containing the circular gold "CERTIFIED" seal,
     * drawn as vector shapes (no external image file needed, so it can
     * never go missing).
     */
    private PdfPCell sealCell(PdfWriter writer) throws Exception {
        Image sealImage = createCertifiedSealImage(writer);
        PdfPCell cell = new PdfPCell(sealImage, false);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setFixedHeight(34f);
        return cell;
    }

    /**
     * Draws a circular gold medallion with a center star and "CERTIFIED"
     * text, plus a ribbon tail beneath it, onto an in-memory template and
     * returns it as an Image so it can be placed in a table cell like any
     * other image.
     */
    private Image createCertifiedSealImage(PdfWriter writer) throws Exception {
        float w = 90f;
        float h = 110f;

        com.lowagie.text.pdf.PdfTemplate template =
                writer.getDirectContent().createTemplate(w, h);

        float cx = w / 2f;
        float cy = h - 45f; // leave room below the circle for the ribbon tails
        float outerR = 32f;
        float innerR = 27f;

        // Ribbon tails (drawn first, behind the medallion)
        template.saveState();
        template.setColorFill(MAROON);
        template.moveTo(cx - 14f, cy - 5f);
        template.lineTo(cx - 14f, cy - 48f);
        template.lineTo(cx - 4f, cy - 38f);
        template.lineTo(cx + 6f, cy - 48f);
        template.lineTo(cx + 6f, cy - 5f);
        template.closePath();
        template.fill();
        template.restoreState();

        // Outer ring (gold)
        template.saveState();
        template.setColorFill(GOLD);
        template.circle(cx, cy, outerR);
        template.fill();
        template.restoreState();

        // Inner disc (slightly darker gold)
        template.saveState();
        template.setColorFill(GOLD_DARK);
        template.circle(cx, cy, innerR);
        template.fill();
        template.restoreState();

        // Inner cream disc so the ring reads as a double border
        template.saveState();
        template.setColorFill(CREAM);
        template.circle(cx, cy, innerR - 3f);
        template.fill();
        template.restoreState();

        // Five-pointed star in the middle (maroon)
        template.saveState();
        template.setColorFill(MAROON);
        drawStar(template, cx, cy + 6f, 9f, 4f);
        template.fill();
        template.restoreState();

        // "CERTIFIED" text along the lower part of the disc
        template.saveState();
        template.setColorFill(NAVY);
        template.beginText();
        template.setFontAndSize(
                com.lowagie.text.pdf.BaseFont.createFont(
                        com.lowagie.text.pdf.BaseFont.HELVETICA_BOLD,
                        com.lowagie.text.pdf.BaseFont.WINANSI,
                        com.lowagie.text.pdf.BaseFont.NOT_EMBEDDED),
                6f);
        template.showTextAligned(Element.ALIGN_CENTER, "CERTIFIED", cx, cy - 8f, 0);
        template.endText();
        template.restoreState();

        Image sealImage = Image.getInstance(template);
        sealImage.scaleToFit(w, h);
        return sealImage;
    }

    /**
     * Draws a filled five-pointed star path (does not fill — caller fills
     * after calling this) centered at (cx, cy) with the given outer and
     * inner radii.
     */
    private void drawStar(PdfContentByte canvas, float cx, float cy, float outerR, float innerR) {
        int points = 5;
        double angleStep = Math.PI / points;
        double startAngle = Math.PI / 2; // point straight up

        for (int i = 0; i < 2 * points; i++) {
            double angle = startAngle + i * angleStep;
            float r = (i % 2 == 0) ? outerR : innerR;
            float x = cx + (float) (r * Math.cos(angle));
            float y = cy + (float) (r * Math.sin(angle));
            if (i == 0) {
                canvas.moveTo(x, y);
            } else {
                canvas.lineTo(x, y);
            }
        }
        canvas.closePath();
    }

    private PdfPCell signatureImageCell(Image signatureImage) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setFixedHeight(34f);
        if (signatureImage != null) {
            signatureImage.scaleToFit(90f, 30f);
            cell.setImage(signatureImage);
        }
        return cell;
    }

    private PdfPCell signatureUnderlineCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderWidthBottom(1f);
        cell.setBorderColorBottom(TEXT_DARK);
        cell.setFixedHeight(4f);
        return cell;
    }

    private PdfPCell signatureLabelCell(String label, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(label, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingTop(4f);
        return cell;
    }

    /**
     * Draws ONLY the cream background and gold double-border frame on every
     * page.
     */
    private static class BorderPageEvent extends PdfPageEventHelper {

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContentUnder();
            Rectangle pageSize = document.getPageSize();

            float llx = pageSize.getLeft();
            float lly = pageSize.getBottom();
            float urx = pageSize.getRight();
            float ury = pageSize.getTop();

            // Cream background fill
            canvas.saveState();
            canvas.setColorFill(CREAM);
            canvas.rectangle(llx, lly, urx - llx, ury - lly);
            canvas.fill();
            canvas.restoreState();

            // Outer gold border
            canvas.saveState();
            canvas.setColorStroke(GOLD);
            canvas.setLineWidth(6f);
            canvas.rectangle(llx + 14, lly + 14, (urx - llx) - 28, (ury - lly) - 28);
            canvas.stroke();

            // Inner thin gold border
            canvas.setLineWidth(1.2f);
            canvas.setColorStroke(GOLD_DARK);
            canvas.rectangle(llx + 24, lly + 24, (urx - llx) - 48, (ury - lly) - 48);
            canvas.stroke();
            canvas.restoreState();
        }
    }
}