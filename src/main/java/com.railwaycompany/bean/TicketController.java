package com.railwaycompany.bean;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@ManagedBean(name = "ticketController")
public class TicketController {

    @EJB
    private TicketEJB ticketEJB;

    private List<String> ticketList;

    public List<String> getTicketList() {
        ticketList = ticketEJB.getAll();
        return ticketList;
    }

    public void setTicketList(List<String> ticketList) {
        this.ticketList = ticketList;
    }

    public void download() throws IOException {
        // Prepare
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            document.add(new Paragraph("Hello world text"));
            for (String t : ticketList) {
                document.add(new Paragraph(t));
            }
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        byte[] pdfData = baos.toByteArray();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

        // Initialize response.
        response.reset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
        response.setContentType("application/pdf"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ServletContext#getMimeType() for auto-detection based on filename.
        response.setHeader("Content-disposition", "inline; filename=\"name.pdf\""); // The Save As popup magic is done here. You can give it any filename you want, this only won't work in MSIE, it will use current request URL as filename instead.

        // Write file to response.
        OutputStream output = response.getOutputStream();
        output.write(pdfData);
        output.close();

        // Inform JSF to not take the response in hands.
        facesContext.responseComplete(); // Important! Else JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
    }
}
