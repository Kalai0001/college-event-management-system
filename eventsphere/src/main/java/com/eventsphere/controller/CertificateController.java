// package com.eventsphere.controller;

// import com.eventsphere.entity.Certificate;
// import com.eventsphere.entity.Event;
// import com.eventsphere.entity.Student;

// import com.eventsphere.repository.CertificateRepository;
// import com.eventsphere.repository.EventRepository;
// import com.eventsphere.repository.StudentRepository;

// import com.lowagie.text.Chunk;
// import com.lowagie.text.Document;
// import com.lowagie.text.Element;
// import com.lowagie.text.Font;
// import com.lowagie.text.FontFactory;
// import com.lowagie.text.Image;
// import com.lowagie.text.PageSize;
// import com.lowagie.text.Paragraph;
// import com.lowagie.text.Phrase;
// import com.lowagie.text.Rectangle;
// import com.lowagie.text.pdf.PdfContentByte;
// import com.lowagie.text.pdf.PdfPCell;
// import com.lowagie.text.pdf.PdfPTable;
// import com.lowagie.text.pdf.PdfPageEventHelper;
// import com.lowagie.text.pdf.PdfWriter;

// import org.springframework.core.io.ClassPathResource;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RestController;

// import java.awt.Color;
// import java.io.ByteArrayOutputStream;
// import java.io.InputStream;

// @RestController
// public class CertificateController {

//     // ---- Palette (matches the target design) ----
//     private static final Color GOLD       = new Color(0xC9, 0xA2, 0x27);
//     private static final Color GOLD_DARK  = new Color(0xA9, 0x7E, 0x10);
//     private static final Color NAVY       = new Color(0x1B, 0x2A, 0x5E);
//     private static final Color MAROON     = new Color(0x7A, 0x14, 0x1E);
//     private static final Color CREAM      = new Color(0xFB, 0xF6, 0xE8);
//     private static final Color TEXT_DARK  = new Color(0x22, 0x22, 0x22);

//     // ---- Static asset filenames (src/main/resources/static/) ----
//     private static final String LOGO_FILE              = "logo.png";
//     private static final String COORDINATOR_SIGN_FILE  = "coordinator-sign.png";
//     private static final String PRINCIPAL_SIGN_FILE    = "principal-sign.png";

//     private final CertificateRepository certificateRepository;
//     private final StudentRepository studentRepository;
//     private final EventRepository eventRepository;

//     public CertificateController(
//             CertificateRepository certificateRepository,
//             StudentRepository studentRepository,
//             EventRepository eventRepository) {

//         this.certificateRepository = certificateRepository;
//         this.studentRepository = studentRepository;
//         this.eventRepository = eventRepository;
//     }

//     @GetMapping("/certificate/{certificateId}")
//     public ResponseEntity<byte[]> generateCertificate(
//             @PathVariable Long certificateId) throws Exception {

//         Certificate certificate =
//                 certificateRepository.findById(certificateId)
//                         .orElse(null);

//         if (certificate == null) {
//             return ResponseEntity.notFound().build();
//         }

//         Student student =
//                 studentRepository.findById(certificate.getStudentId())
//                         .orElse(null);

//         Event event =
//                 eventRepository.findById(certificate.getEventId())
//                         .orElse(null);

//         if (student == null || event == null) {
//             return ResponseEntity.notFound().build();
//         }

//         ByteArrayOutputStream out = new ByteArrayOutputStream();

//         // Landscape A4, generous margins so the gold border has room.
//         // Margins are intentionally large (top/bottom 30, left/right 50) to keep
//         // the whole certificate inside ONE page only.
//         Document document = new Document(PageSize.A4.rotate(), 50, 50, 30, 30);

//         PdfWriter writer = PdfWriter.getInstance(document, out);
//         // Draws ONLY the cream background + gold border frame on every page.
//         // Deliberately contains no content-dependent drawing (like a seal),
//         // so it can never duplicate or misalign with flowing content.
//         writer.setPageEvent(new BorderPageEvent());

//         document.open();

//         // ---- Fonts ----
//         Font collegeFont        = FontFactory.getFont(FontFactory.TIMES_BOLD, 20, NAVY);
//         Font autonomousFont     = FontFactory.getFont(FontFactory.TIMES_BOLD, 10, TEXT_DARK);
//         Font subInfoFont        = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, TEXT_DARK);
//         Font symposiumFont      = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, NAVY);
//         Font certificateFont    = FontFactory.getFont(FontFactory.TIMES_BOLD, 34, MAROON);
//         Font ofParticipationFont= FontFactory.getFont(FontFactory.TIMES_BOLD, 12, TEXT_DARK);
//         Font bodyFont           = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, TEXT_DARK);
//         Font nameFont           = FontFactory.getFont(FontFactory.TIMES_BOLD, 22, NAVY);
//         Font collegeNameFont    = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, TEXT_DARK);
//         Font eventNameFont      = FontFactory.getFont(FontFactory.TIMES_BOLD, 16, MAROON);
//         Font boldBodyFont       = FontFactory.getFont(FontFactory.TIMES_BOLD, 10, TEXT_DARK);
//         Font smallGrayFont      = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Color.GRAY);
//         Font signatureLabelFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 10, TEXT_DARK);

//         // ---- Header block: logo (if available) + college name ----
//         addHeaderWithLogo(document, collegeFont);

//         addCentered(document, "(AUTONOMOUS)", autonomousFont, 1);
//         addCentered(document, "Approved by AICTE, New Delhi | Affiliated to Anna University, Chennai", subInfoFont, 1);
//         addCentered(document, "Accredited by NAAC (A+ Grade) | Pullipalayam, Tamil Nadu", subInfoFont, 6);

//         addRuleLine(document);

//         addCentered(document, "NATIONAL LEVEL TECHNICAL SYMPOSIUM", symposiumFont, 8);
//         addCentered(document, "CERTIFICATE", certificateFont, 0);
//         addCentered(document, "OF PARTICIPATION", ofParticipationFont, 10);

//         addCentered(document, "This Certificate is Presented To", bodyFont, 6);
//         addCentered(document, student.getName(), nameFont, 4);
//         addCentered(document, student.getCollege().toUpperCase(), collegeNameFont, 8);

//         addCentered(document, "For active participation in the event", bodyFont, 5);
//         addCentered(document, event.getName(), eventNameFont, 8);

//         addCentered(document, "Organized by Sri Shanmugha College of Engineering and Technology", bodyFont, 6);
//         addCentered(document, "Date of Symposium : " + event.getDate(), boldBodyFont, 2);
//         addCentered(document, "Certificate No : " + certificate.getCertificateCode(), smallGrayFont, 10);

//         // ---- Seal/badge (vector, in normal flow — won't overlap or duplicate) ----
//         addSealInFlow(document, writer);

//         // ---- Signature row: real signature images + underline + label ----
//         addSignatureRow(document, signatureLabelFont);

//         document.close();

//         return ResponseEntity.ok()
//                 .header(
//                         HttpHeaders.CONTENT_DISPOSITION,
//                         "attachment; filename=certificate.pdf")
//                 .contentType(MediaType.APPLICATION_PDF)
//                 .body(out.toByteArray());
//     }

//     // ---------------------------------------------------------------------
//     // Helpers
//     // ---------------------------------------------------------------------

//     private void addCentered(Document document, String text, Font font, float spacingAfter) throws Exception {
//         Paragraph p = new Paragraph(text, font);
//         p.setAlignment(Element.ALIGN_CENTER);
//         p.setSpacingAfter(spacingAfter);
//         document.add(p);
//     }

//     private void addRuleLine(Document document) throws Exception {
//         Chunk line = new Chunk(new com.lowagie.text.pdf.draw.LineSeparator(1f, 90f, GOLD_DARK, Element.ALIGN_CENTER, -2));
//         Paragraph p = new Paragraph();
//         p.add(line);
//         p.setSpacingAfter(6f);
//         document.add(p);
//     }

//     /**
//      * Loads an image from src/main/resources/static/{filename} on the classpath.
//      * Returns null (instead of throwing) if the file is missing, so a missing
//      * asset degrades gracefully instead of breaking certificate generation.
//      */
//     private Image loadStaticImage(String filename) {
//         try {
//             ClassPathResource resource = new ClassPathResource("static/" + filename);
//             try (InputStream is = resource.getInputStream()) {
//                 byte[] bytes = is.readAllBytes();
//                 return Image.getInstance(bytes);
//             }
//         } catch (Exception e) {
//             return null;
//         }
//     }

//     /**
//      * Header row: logo on the left (if present), college name block centered.
//      * Uses a 2-column table so the logo doesn't push the heading off-center.
//      */
//     private void addHeaderWithLogo(Document document, Font collegeFont) throws Exception {
//         Image logo = loadStaticImage(LOGO_FILE);

//         if (logo == null) {
//             // No logo found — just center the college name, same as before.
//             addCentered(document, "SRI SHANMUGHA COLLEGE OF ENGINEERING AND TECHNOLOGY", collegeFont, 2);
//             return;
//         }

//         logo.scaleToFit(55f, 55f);

//         PdfPTable headerTable = new PdfPTable(new float[]{1f, 5f});
//         headerTable.setWidthPercentage(100);

//         PdfPCell logoCell = new PdfPCell(logo, false);
//         logoCell.setBorder(Rectangle.NO_BORDER);
//         logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
//         logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//         headerTable.addCell(logoCell);

//         PdfPCell nameCell = new PdfPCell(
//                 new Phrase("SRI SHANMUGHA COLLEGE OF ENGINEERING AND TECHNOLOGY", collegeFont));
//         nameCell.setBorder(Rectangle.NO_BORDER);
//         nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//         nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//         headerTable.addCell(nameCell);

//         headerTable.setSpacingAfter(2f);
//         document.add(headerTable);
//     }

//     /**
//      * Draws the gold/maroon seal as a small inline image-free vector graphic,
//      * but — critically — placed via document flow (a PdfPCell with a custom
//      * PdfPTableEvent-free single cell using a Chunk) so it occupies real space
//      * in the layout and can never land on a different page than the rest of
//      * the certificate, and is only ever drawn once.
//      */
//     private void addSealInFlow(Document document, PdfWriter writer) throws Exception {
//         float size = 46f;
//         Image sealImage = renderSealAsImage(writer, size);

//         Paragraph sealParagraph = new Paragraph();
//         sealParagraph.setAlignment(Element.ALIGN_CENTER);
//         sealParagraph.setSpacingAfter(4f);
//         if (sealImage != null) {
//             sealImage.setAlignment(Image.MIDDLE);
//             Chunk chunk = new Chunk(sealImage, 0, 0);
//             sealParagraph.add(chunk);
//         }
//         document.add(sealParagraph);
//     }

//     /**
//      * Renders the seal/rosette to its own small standalone PDF template
//      * (an OpenPDF "PdfTemplate"), which behaves like an Image and can be
//      * embedded inline in document flow — unlike direct canvas drawing,
//      * which is page-absolute and was the root cause of the earlier
//      * overlap/second-page bug.
//      */
//     private Image renderSealAsImage(PdfWriter writer, float size) {
//         try {
//             com.lowagie.text.pdf.PdfTemplate template = writer.getDirectContent().createTemplate(size, size);
//             float cx = size / 2f;
//             float cy = size / 2f;

//             template.saveState();

//             // Ribbon tails
//             template.setColorFill(MAROON);
//             template.moveTo(cx - 9, cy - 18);
//             template.lineTo(cx - 2, cy - 45 + 18);
//             template.lineTo(cx - 9, cy - 38 + 18);
//             template.lineTo(cx - 16, cy - 45 + 18);
//             template.closePath();
//             template.fillStroke();

//             template.moveTo(cx + 9, cy - 18);
//             template.lineTo(cx + 16, cy - 45 + 18);
//             template.lineTo(cx + 9, cy - 38 + 18);
//             template.lineTo(cx + 2, cy - 45 + 18);
//             template.closePath();
//             template.fillStroke();

//             // Outer gold circle
//             template.setColorFill(GOLD);
//             template.circle(cx, cy, 19);
//             template.fill();

//             // Inner cream circle
//             template.setColorFill(CREAM);
//             template.circle(cx, cy, 14);
//             template.fill();

//             // Center dot
//             template.setColorFill(MAROON);
//             template.circle(cx, cy, 4.5f);
//             template.fill();

//             template.restoreState();

//             return Image.getInstance(template);
//         } catch (Exception e) {
//             return null;
//         }
//     }

//     /**
//      * Signature row: coordinator signature image (left) and principal
//      * signature image (right), each with an underline and label beneath.
//      * Falls back to a blank underline if an image is missing.
//      */
//     private void addSignatureRow(Document document, Font signatureLabelFont) throws Exception {
//         PdfPTable sigTable = new PdfPTable(2);
//         sigTable.setWidthPercentage(60);
//         sigTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

//         Image coordinatorSign = loadStaticImage(COORDINATOR_SIGN_FILE);
//         Image principalSign   = loadStaticImage(PRINCIPAL_SIGN_FILE);

//         sigTable.addCell(signatureImageCell(coordinatorSign));
//         sigTable.addCell(signatureImageCell(principalSign));

//         sigTable.addCell(signatureUnderlineCell());
//         sigTable.addCell(signatureUnderlineCell());

//         sigTable.addCell(signatureLabelCell("Faculty Coordinator", signatureLabelFont));
//         sigTable.addCell(signatureLabelCell("Principal", signatureLabelFont));

//         sigTable.setSpacingBefore(4f);
//         document.add(sigTable);
//     }

//     private PdfPCell signatureImageCell(Image signatureImage) {
//         PdfPCell cell = new PdfPCell();
//         cell.setBorder(Rectangle.NO_BORDER);
//         cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//         cell.setFixedHeight(34f);
//         if (signatureImage != null) {
//             signatureImage.scaleToFit(90f, 30f);
//             cell.setImage(signatureImage);
//         }
//         return cell;
//     }

//     private PdfPCell signatureUnderlineCell() {
//         PdfPCell cell = new PdfPCell();
//         cell.setBorder(Rectangle.NO_BORDER);
//         cell.setBorderWidthBottom(1f);
//         cell.setBorderColorBottom(TEXT_DARK);
//         cell.setFixedHeight(4f);
//         return cell;
//     }

//     private PdfPCell signatureLabelCell(String label, Font font) {
//         PdfPCell cell = new PdfPCell(new Phrase(label, font));
//         cell.setBorder(Rectangle.NO_BORDER);
//         cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//         cell.setPaddingTop(4f);
//         return cell;
//     }

//     /**
//      * Draws ONLY the cream background and gold double-border frame on every
//      * page. Intentionally has no content-aware drawing (no seal, no text),
//      * because anything tied to certificate content must live in document
//      * flow instead — otherwise it can desync from the flowing content and
//      * either duplicate across pages or land on the wrong page, which is
//      * exactly what happened before.
//      */
//     private static class BorderPageEvent extends PdfPageEventHelper {

//         @Override
//         public void onStartPage(PdfWriter writer, Document document) {
//             PdfContentByte canvas = writer.getDirectContentUnder();
//             Rectangle pageSize = document.getPageSize();

//             float llx = pageSize.getLeft();
//             float lly = pageSize.getBottom();
//             float urx = pageSize.getRight();
//             float ury = pageSize.getTop();

//             // Cream background fill
//             canvas.saveState();
//             canvas.setColorFill(CREAM);
//             canvas.rectangle(llx, lly, urx - llx, ury - lly);
//             canvas.fill();
//             canvas.restoreState();

//             // Outer gold border
//             canvas.saveState();
//             canvas.setColorStroke(GOLD);
//             canvas.setLineWidth(6f);
//             canvas.rectangle(llx + 14, lly + 14, (urx - llx) - 28, (ury - lly) - 28);
//             canvas.stroke();

//             // Inner thin gold border
//             canvas.setLineWidth(1.2f);
//             canvas.setColorStroke(GOLD_DARK);
//             canvas.rectangle(llx + 24, lly + 24, (urx - llx) - 48, (ury - lly) - 48);
//             canvas.stroke();
//             canvas.restoreState();
//         }
//     }
// }




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
    private static final String PRINCIPAL_SIGN_FILE    = "principal-sign.png";

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

        // Landscape A4. Margins trimmed slightly (top/bottom 20, left/right 50)
        // vs. before (30) to reclaim vertical room — combined with the seal
        // removal and tighter spacing below, this keeps everything on ONE page.
        Document document = new Document(PageSize.A4.rotate(), 50, 50, 20, 20);

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
        addSignatureRow(document, signatureLabelFont);

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
     * Header row: logo on the left (if present), college name block centered.
     * Uses a 2-column table so the logo doesn't push the heading off-center.
     */
    private void addHeaderWithLogo(Document document, Font collegeFont) throws Exception {
        Image logo = loadStaticImage(LOGO_FILE);

        if (logo == null) {
            // No logo found — just center the college name, same as before.
            addCentered(document, "SRI SHANMUGHA COLLEGE OF ENGINEERING AND TECHNOLOGY", collegeFont, 2);
            return;
        }

        logo.scaleToFit(55f, 55f);

        PdfPTable headerTable = new PdfPTable(new float[]{1f, 5f});
        headerTable.setWidthPercentage(100);

        PdfPCell logoCell = new PdfPCell(logo, false);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(logoCell);

        PdfPCell nameCell = new PdfPCell(
                new Phrase("SRI SHANMUGHA COLLEGE OF ENGINEERING AND TECHNOLOGY", collegeFont));
        nameCell.setBorder(Rectangle.NO_BORDER);
        nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(nameCell);

        headerTable.setSpacingAfter(2f);
        document.add(headerTable);
    }

    /**
     * Signature row: coordinator signature image (left) and principal
     * signature image (right), each with an underline and label beneath.
     * Falls back to a blank underline if an image is missing.
     *
     * NOTE on the white box behind each signature: that box is the actual
     * background pixel data baked into coordinator-sign.png / principal-sign.png.
     * iText/OpenPDF just paints whatever pixels the PNG contains — it is not
     * adding a white background itself. To make the box disappear, the PNG
     * files themselves need a transparent background (alpha channel) instead
     * of white. See chat reply for two ways to do that.
     */
    private void addSignatureRow(Document document, Font signatureLabelFont) throws Exception {
        PdfPTable sigTable = new PdfPTable(2);
        sigTable.setWidthPercentage(60);
        sigTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        Image coordinatorSign = loadStaticImage(COORDINATOR_SIGN_FILE);
        Image principalSign   = loadStaticImage(PRINCIPAL_SIGN_FILE);

        sigTable.addCell(signatureImageCell(coordinatorSign));
        sigTable.addCell(signatureImageCell(principalSign));

        sigTable.addCell(signatureUnderlineCell());
        sigTable.addCell(signatureUnderlineCell());

        sigTable.addCell(signatureLabelCell("Faculty Coordinator", signatureLabelFont));
        sigTable.addCell(signatureLabelCell("Principal", signatureLabelFont));

        sigTable.setSpacingBefore(4f);
        document.add(sigTable);
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