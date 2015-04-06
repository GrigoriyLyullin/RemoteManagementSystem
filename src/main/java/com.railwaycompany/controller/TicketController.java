package com.railwaycompany.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.railwaycompany.model.entities.Passenger;
import com.railwaycompany.model.entities.Ticket;
import com.railwaycompany.model.entities.Train;
import com.railwaycompany.model.interfaces.TicketService;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@ManagedBean
@SessionScoped
public class TicketController implements Serializable {

    private static final Logger LOG = Logger.getLogger(TicketController.class);
    private static final String REPORT_PAGE_REDIRECT = "/private/report_page.xhtml?faces-redirect=true";
    private static final String TOKEN_PARAM = "Rest-Token";

    private StreamedContent pdfContent;
    private Date dateFrom;
    private Date dateTo;
    private List<Ticket> ticketList;

    @EJB
    private TicketService ticketService;

    public String getTickets() {
        if (dateFrom.getTime() <= dateTo.getTime()) {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            String token = (String) session.getAttribute(TOKEN_PARAM);
            ticketList = ticketService.getTickets(dateFrom, dateTo, token);
            return REPORT_PAGE_REDIRECT;
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "End date should be greater than start date", ""));
            return null;
        }
    }

    public void onPrerender(ComponentSystemEvent event) {
        if (ticketList != null && !ticketList.isEmpty()) {
            try {
                Document document = new Document(PageSize.A4, 36.0F, 36.0F, 72.0F, 36.0F);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PdfWriter writer = PdfWriter.getInstance(document, out);
                document.open();
                document.newPage();

                FacesContext facesContext = FacesContext.getCurrentInstance();
                ExternalContext externalContext = facesContext.getExternalContext();
                String relativeWebPath = "/resources/pdf/template.pdf";
                ServletContext servletContext = (ServletContext) externalContext.getContext();
                String absoluteDiskPath = servletContext.getRealPath(relativeWebPath);
                File file = new File(absoluteDiskPath);

                PdfReader reader = new PdfReader(new FileInputStream(file));
                PdfImportedPage page = writer.getImportedPage(reader, 1);
                PdfContentByte cb = writer.getDirectContent();
                cb.addTemplate(page, 0, 0);

                Font fontBig = new Font(Font.getFamily("Arial"), 24.0f, Font.BOLD);
                Font fontMedium = new Font(Font.getFamily("Arial"), 12.0f, Font.NORMAL);

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                document.add(new Paragraph("Ticket sales report", fontBig));
                document.add(new Paragraph(Chunk.NEWLINE));
                document.add(new Paragraph("Start date: " + dateFormat.format(dateFrom), fontMedium));
                document.add(new Paragraph("End date: " + dateFormat.format(dateTo), fontMedium));
                document.add(new Paragraph(Chunk.NEWLINE));

                PdfPTable table = new PdfPTable(8);
                table.setWidthPercentage(100);
                table.setSpacingBefore(5);
                table.setSpacingAfter(5);

                float headBorder = 1f;

                PdfPCell ticketCell = new PdfPCell(new Phrase("Ticket Id", fontMedium));
                ticketCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                ticketCell.setVerticalAlignment(Element.ALIGN_CENTER);
                ticketCell.setBorderWidth(headBorder);
                ticketCell.setRowspan(2);
                table.addCell(ticketCell);
                PdfPCell passengerCell = new PdfPCell(new Phrase("Passenger information" + Chunk.NEWLINE, fontMedium));
                passengerCell.setBorderWidth(headBorder);
                passengerCell.setColspan(4);
                passengerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(passengerCell);
                PdfPCell trainCell = new PdfPCell(new Phrase("Train information", fontMedium));
                trainCell.setBorderWidth(headBorder);
                trainCell.setColspan(3);
                trainCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(trainCell);

                PdfPCell passengerId = new PdfPCell(new Phrase("Id", fontMedium));
                passengerId.setBorderWidth(headBorder);
                table.addCell(passengerId);
                PdfPCell passengerName = new PdfPCell(new Phrase("Name", fontMedium));
                passengerName.setBorderWidth(headBorder);
                table.addCell(passengerName);
                PdfPCell passengerSurname = new PdfPCell(new Phrase("Surname", fontMedium));
                passengerSurname.setBorderWidth(headBorder);
                table.addCell(passengerSurname);
                PdfPCell passengerBirthdate = new PdfPCell(new Phrase("Date of birth", fontMedium));
                passengerBirthdate.setBorderWidth(headBorder);
                table.addCell(passengerBirthdate);
                PdfPCell trainId = new PdfPCell(new Phrase("Id", fontMedium));
                trainId.setBorderWidth(headBorder);
                table.addCell(trainId);
                PdfPCell trainNumber = new PdfPCell(new Phrase("Number", fontMedium));
                trainNumber.setBorderWidth(headBorder);
                table.addCell(trainNumber);
                PdfPCell trainSeats = new PdfPCell(new Phrase("Seats", fontMedium));
                trainSeats.setBorderWidth(headBorder);
                table.addCell(trainSeats);

                for (Ticket t : ticketList) {
                    Passenger passenger = t.getPassenger();
                    Train train = t.getTrain();

                    PdfPCell ticketIdCell = new PdfPCell(new Phrase(String.valueOf(t.getId()), fontMedium));

                    PdfPCell passengerIdCell = new PdfPCell(new Phrase(String.valueOf(passenger.getId()), fontMedium));
                    PdfPCell passengerNameCell = new PdfPCell(new Phrase(String.valueOf(passenger.getName()),
                            fontMedium));
                    PdfPCell passengerSurnameCell = new PdfPCell(new Phrase(String.valueOf(passenger.getSurname()),
                            fontMedium));
                    PdfPCell passengerBirthdateCell = new PdfPCell(
                            new Phrase(dateFormat.format(passenger.getBirthdate()), fontMedium));

                    PdfPCell trainIdCell = new PdfPCell(new Phrase(String.valueOf(train.getId()), fontMedium));
                    PdfPCell trainNumberCell = new PdfPCell(new Phrase(String.valueOf(train.getNumber()), fontMedium));
                    PdfPCell trainSeatsCell = new PdfPCell(new Phrase(String.valueOf(train.getSeats()), fontMedium));

                    table.addCell(ticketIdCell);
                    table.addCell(passengerIdCell);
                    table.addCell(passengerNameCell);
                    table.addCell(passengerSurnameCell);
                    table.addCell(passengerBirthdateCell);
                    table.addCell(trainIdCell);
                    table.addCell(trainNumberCell);
                    table.addCell(trainSeatsCell);
                }
                document.add(table);
                document.close();
                setPdfContent(new DefaultStreamedContent(new ByteArrayInputStream(out.toByteArray()), "application/pdf"));
                out.close();
            } catch (FileNotFoundException e) {
                LOG.warn("Template file not found", e);
            } catch (DocumentException | IOException e) {
                LOG.warn(e);
            }
        }
    }

    public StreamedContent getPdfContent() {
        return pdfContent;
    }

    public void setPdfContent(StreamedContent pdfContent) {
        this.pdfContent = pdfContent;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }
}