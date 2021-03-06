package com.railwaycompany.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.railwaycompany.model.dto.PassengerData;
import com.railwaycompany.model.dto.TicketData;
import com.railwaycompany.model.dto.TrainData;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@ManagedBean
@SessionScoped
public class TicketController implements Serializable {

    private static final Logger LOG = Logger.getLogger(TicketController.class);
    private static final String REPORT_PAGE_REDIRECT = "/private/report_page.xhtml?faces-redirect=true";
    private static final String TOKEN_PARAM = "Rest-Token";

    private final SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private StreamedContent pdfContent;
    private Date dateFrom;
    private Date dateTo;
    private List<TicketData> ticketList;

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

            document.add(new Paragraph("Ticket sales report", fontBig));
            document.add(new Paragraph(Chunk.NEWLINE));
            document.add(new Paragraph("Start date: " + datetimeFormat.format(dateFrom), fontMedium));
            document.add(new Paragraph("End date: " + datetimeFormat.format(dateTo), fontMedium));
            document.add(new Paragraph(Chunk.NEWLINE));

            if (ticketList != null && !ticketList.isEmpty()) {
                PdfPTable table = new PdfPTable(12);
                table.setWidthPercentage(100);
                table.setSpacingBefore(5);
                table.setSpacingAfter(5);
                float[] columnWidths = new float[]{5f, 20f, 15f, 5f, 15f, 15f, 10f, 5f, 15f, 15f, 8f, 8f};
                table.setWidths(columnWidths);
                float headBorder = 1f;

                PdfPCell ticketCell = new PdfPCell(new Phrase("Ticket information", fontMedium));
                ticketCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                ticketCell.setBorderWidth(headBorder);
                ticketCell.setColspan(3);
                table.addCell(ticketCell);
                PdfPCell passengerCell = new PdfPCell(new Phrase("Passenger information" + Chunk.NEWLINE, fontMedium));
                passengerCell.setBorderWidth(headBorder);
                passengerCell.setColspan(4);
                passengerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(passengerCell);
                PdfPCell trainCell = new PdfPCell(new Phrase("Train information", fontMedium));
                trainCell.setBorderWidth(headBorder);
                trainCell.setColspan(5);
                trainCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(trainCell);

                PdfPCell ticketId = new PdfPCell(new Phrase("Id", fontMedium));
                ticketId.setBorderWidth(headBorder);
                table.addCell(ticketId);
                PdfPCell ticketCost = new PdfPCell(new Phrase("Cost", fontMedium));
                ticketCost.setBorderWidth(headBorder);
                table.addCell(ticketCost);
                PdfPCell ticketSaleTime = new PdfPCell(new Phrase("Sale time", fontMedium));
                ticketSaleTime.setBorderWidth(headBorder);
                table.addCell(ticketSaleTime);
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
                PdfPCell trainDepartureStation = new PdfPCell(new Phrase("Departure station", fontMedium));
                trainDepartureStation.setBorderWidth(headBorder);
                table.addCell(trainDepartureStation);
                PdfPCell trainDepartureDate = new PdfPCell(new Phrase("Departure date", fontMedium));
                trainDepartureDate.setBorderWidth(headBorder);
                table.addCell(trainDepartureDate);
                PdfPCell trainNumber = new PdfPCell(new Phrase("Number", fontMedium));
                trainNumber.setBorderWidth(headBorder);
                table.addCell(trainNumber);
                PdfPCell trainSeats = new PdfPCell(new Phrase("Seats", fontMedium));
                trainSeats.setBorderWidth(headBorder);
                table.addCell(trainSeats);

                NumberFormat formatter = new DecimalFormat("#0.00");
                double totalCost = 0;

                for (TicketData t : ticketList) {
                    PassengerData passenger = t.getPassengerData();
                    TrainData train = t.getTrainData();

                    PdfPCell ticketIdCell = new PdfPCell(new Phrase(String.valueOf(t.getTicketId()), fontMedium));
                    PdfPCell ticketCostCell = new PdfPCell(new Phrase(String.valueOf(train.getTicketCost()), fontMedium));
                    PdfPCell ticketSaleCell = new PdfPCell(new Phrase(datetimeFormat.format(t.getSaleTime()), fontMedium));

                    totalCost += train.getTicketCost();

                    PdfPCell passengerIdCell = new PdfPCell(new Phrase(String.valueOf(passenger.getId()), fontMedium));
                    PdfPCell passengerNameCell = new PdfPCell(new Phrase(String.valueOf(passenger.getName()),
                            fontMedium));
                    PdfPCell passengerSurnameCell = new PdfPCell(new Phrase(String.valueOf(passenger.getSurname()),
                            fontMedium));
                    PdfPCell passengerBirthdateCell = new PdfPCell(
                            new Phrase(dateFormat.format(passenger.getBirthdate()), fontMedium));

                    PdfPCell trainIdCell = new PdfPCell(new Phrase(String.valueOf(train.getId()), fontMedium));
                    PdfPCell trainStationCell = new PdfPCell(new Phrase(String.valueOf(t.getStationFrom()), fontMedium));

                    LOG.warn("t.getDepartureDate(): " + t.getDepartureDate());
                    LOG.warn("datetimeFormat.format(t.getDepartureDate(): " + datetimeFormat.format(t.getDepartureDate()));

                    PdfPCell trainDateCell = new PdfPCell(new Phrase(datetimeFormat.format(t.getDepartureDate()), fontMedium));
                    PdfPCell trainNumberCell = new PdfPCell(new Phrase(String.valueOf(train.getNumber()), fontMedium));
                    PdfPCell trainSeatsCell = new PdfPCell(new Phrase(String.valueOf(train.getSeats()), fontMedium));

                    table.addCell(ticketIdCell);
                    table.addCell(ticketCostCell);
                    table.addCell(ticketSaleCell);
                    table.addCell(passengerIdCell);
                    table.addCell(passengerNameCell);
                    table.addCell(passengerSurnameCell);
                    table.addCell(passengerBirthdateCell);
                    table.addCell(trainIdCell);
                    table.addCell(trainStationCell);
                    table.addCell(trainDateCell);
                    table.addCell(trainNumberCell);
                    table.addCell(trainSeatsCell);
                }
                document.add(table);
                document.add(new Paragraph("Total count: " + ticketList.size(), fontMedium));
                document.add(new Paragraph("Total cost: " + formatter.format(totalCost), fontMedium));
            } else {
                document.add(new Paragraph("For a specified period not sold a single ticket!", fontMedium));
            }
            document.close();
            setPdfContent(new DefaultStreamedContent(new ByteArrayInputStream(out.toByteArray()), "application/pdf"));
            out.close();
        } catch (FileNotFoundException e) {
            LOG.warn("Template file not found", e);
        } catch (DocumentException | IOException e) {
            LOG.warn(e);
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