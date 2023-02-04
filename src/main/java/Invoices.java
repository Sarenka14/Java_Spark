import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Invoices {
    public static void makeAllCarsInvoice(Invoice faktura){
        Document document = new Document(); // dokument pdf
        String path = "invoices/invoice_all_cars_" + faktura.getTime() + ".pdf"; // lokalizacja zapisu
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        document.open();
        Font fontFaktura = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
        Font fontCzerwony = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.RED);
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 16, BaseColor.BLACK);
        Font fontTabela = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Font fontBialy = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.WHITE);

        Paragraph tytul = new Paragraph("FAKTURA: VAT/" + faktura.getTitle().replace(":","/").replace(" ","/"), fontFaktura); // paragraf
        Paragraph sprzedawca = new Paragraph(faktura.getSeller(), font); // paragraf
        Paragraph nabywca = new Paragraph(faktura.getBuyer(), font); // paragraf
        Paragraph rok = new Paragraph("Faktura za wszystkie auta", fontCzerwony); // paragraf
        Paragraph cena = new Paragraph("DO ZAPŁATY " + faktura.totalPrice(), fontFaktura);
        Paragraph br = new Paragraph(".", fontBialy);

        try {
            document.add(tytul);
            document.add(sprzedawca);
            document.add(nabywca);
            document.add(rok);
            document.add(br);

            PdfPTable tableLegenda = new PdfPTable(5);
            PdfPCell lpTabela = new PdfPCell(new Phrase("lp", fontTabela));
            PdfPCell rokTabela = new PdfPCell(new Phrase("rok", fontTabela));
            PdfPCell cenaTabela = new PdfPCell(new Phrase("cena", fontTabela));
            PdfPCell vatTabela = new PdfPCell(new Phrase("vat", fontTabela));
            PdfPCell wartoscTabela = new PdfPCell(new Phrase("wartosc", fontTabela));
            tableLegenda.addCell(lpTabela);
            tableLegenda.addCell(rokTabela);
            tableLegenda.addCell(cenaTabela);
            tableLegenda.addCell(vatTabela);
            tableLegenda.addCell(wartoscTabela);
            document.add(tableLegenda);

            for(int i = 0; i < faktura.list.size(); i++){
                PdfPTable tableWartosc = new PdfPTable(5);
                PdfPCell lpWartosc = new PdfPCell(new Phrase(String.valueOf(i+1), fontTabela));
                PdfPCell rokWartosc = new PdfPCell(new Phrase(String.valueOf(faktura.list.get(i).getRok()), fontTabela));
                PdfPCell cenaWartosc = new PdfPCell(new Phrase(String.valueOf(faktura.list.get(i).getPrice()), fontTabela));
                PdfPCell vatWartosc = new PdfPCell(new Phrase(String.valueOf(faktura.list.get(i).getVAT()) + "%", fontTabela));

                float cenaZVat = 0;
                cenaZVat += faktura.list.get(i).getPrice();
                float vatUlamkowy = (float) faktura.list.get(i).getVAT()/100;
                cenaZVat += faktura.list.get(i).getPrice() * vatUlamkowy;

                PdfPCell wartoscWartosc = new PdfPCell(new Phrase(String.valueOf(Math.floor(cenaZVat * 100) / 100), fontTabela));
                tableWartosc.addCell(lpWartosc);
                tableWartosc.addCell(rokWartosc);
                tableWartosc.addCell(cenaWartosc);
                tableWartosc.addCell(vatWartosc);
                tableWartosc.addCell(wartoscWartosc);
                document.add(tableWartosc);
            }

            document.add(cena);

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        document.close();
    }
}
