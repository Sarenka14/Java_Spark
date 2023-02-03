import static java.lang.Integer.parseInt;
import static spark.Spark.*;

import com.fasterxml.uuid.Generators;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import spark.Request;
import spark.Response;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class App {
    public static int carid = 1;

    static ArrayList<Car> cars = new ArrayList<>();

    public static void main(String[] args) {
        String projectDir = System.getProperty("user.dir");
        String staticDir = "/src/main/resources/public";
        staticFiles.externalLocation(projectDir + staticDir);

        UUID uuid = Generators.randomBasedGenerator().generate();

        post("/add", App::addCar);

        post("/cars", App::resCars);

        post("/delete", App::delCar);

        post("/update", App::updateCar);

        post("/generate", App::generateCars);

        post("/invoice", App::generateInvoice);

        get("/invoices", App::downloadInvoice);
    }

    static String addCar(Request req, Response res){
        Gson gson = new Gson();
        Car auto = gson.fromJson(req.body(), Car.class);
        auto.setUuid(String.valueOf(Generators.randomBasedGenerator().generate()));
        cars.add(auto);

        res.type("application/json");
        return gson.toJson(cars.get(cars.size()-1));
    }

    static String resCars(Request req, Response res){
        Gson gson = new Gson();

        res.type("application/json");
        return gson.toJson(cars, ArrayList.class);
    }

    static String delCar(Request req, Response res){
        Gson gson = new Gson();

        for(int i = 0; i <= cars.size()-1;i++){
            String temp = req.body().substring(1, req.body().length() - 1);
            if (cars.get(i).getUuid().equals(temp)){
                cars.remove(i);
            }
        }

        res.type("application/json");
        return gson.toJson(cars, ArrayList.class);
    }

    static String updateCar(Request req, Response res){
        Gson gson = new Gson();

        Car updatedCar = gson.fromJson(req.body(), Car.class);

        for(int i = 0; i <= cars.size()-1;i++){
            if (cars.get(i).getUuid().equals(updatedCar.getUuid())){
                cars.get(i).setModel(updatedCar.getModel());
                cars.get(i).setRok(updatedCar.getRok());
            }
        }

        res.type("application/json");
        return gson.toJson(cars, ArrayList.class);
    }

    static String generateCars(Request req, Response res){
        Gson gson = new Gson();
        cars.clear();
        for(int i = 0; i < (Math.random() * 9) + 1; i++){
            Car auto = new Car();
            auto.setUuid(String.valueOf(Generators.randomBasedGenerator().generate()));
            String[] models = {"Fiat", "BMW", "Audi", "Opel"};
            auto.setModel(models[(int) (Math.random() * (4))]);
            int[] years = {2001, 2002, 2003, 2004, 2005};
            auto.setRok(years[(int) (Math.random() * (5))]);
            String[] airbagDescriptions = {"kierowca", "pasażer", "kanapa", "boczne"};

            for(int j = 0; j < 4; j++){
                Car.Airbag poduszkaClass = new Car.Airbag(airbagDescriptions[j], Math.random() < 0.5);
                auto.airbags.add(poduszkaClass);
            }

            Random random = new Random();
            int nextInt = random.nextInt(0xffffff + 1);
            String colorCode = String.format("#%06x", nextInt);

            auto.setKolor(colorCode);
            cars.add(auto);
        }

        res.type("application/json");
        return gson.toJson(cars, ArrayList.class);
    }

    static String generateInvoice(Request req, Response res){
        Gson gson = new Gson();

        int ktoryWLiscie = 0;
        String temp = req.body().substring(1, req.body().length() - 1);

        for(int i = 0; i <= cars.size()-1;i++){
            if (cars.get(i).getUuid().equals(temp)){
                cars.get(i).setGeneratedInvoice(true);
                ktoryWLiscie = i;
            }
        }

        Document document = new Document(); // dokument pdf
        String path = "invoices/" + temp + ".pdf"; // lokalizacja zapisu
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        int rgbRed = Integer.valueOf( cars.get(ktoryWLiscie).getKolor().substring( 1, 3 ), 16 );
        int rgbGreen = Integer.valueOf( cars.get(ktoryWLiscie).getKolor().substring( 3, 5 ), 16 );
        int rgbBlue = Integer.valueOf( cars.get(ktoryWLiscie).getKolor().substring( 5, 7 ), 16 );

        document.open();
        Font fontFaktura = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
        Font fontModel = FontFactory.getFont(FontFactory.HELVETICA, 20, BaseColor.BLACK);
        Font fontKolor = FontFactory.getFont(FontFactory.HELVETICA, 16, new BaseColor(rgbRed, rgbGreen, rgbBlue));
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 16, BaseColor.BLACK);
        /*Chunk chunk = new Chunk("tekst", font); // akapit w ciągłej linii bez <br>
        try {
            document.add(chunk);
            document.add(chunk);
            document.add(chunk);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }*/

        Paragraph faktura = new Paragraph("FAKTURA dla: " + temp, fontFaktura); // paragraf
        Paragraph model = new Paragraph("model: " + cars.get(ktoryWLiscie).getModel(), fontModel); // paragraf
        Paragraph kolor = new Paragraph("kolor: " + cars.get(ktoryWLiscie).getKolor(), fontKolor); // paragraf
        Paragraph rok = new Paragraph("rok: " + cars.get(ktoryWLiscie).getRok(), font); // paragraf

        try {
            document.add(faktura);
            document.add(model);
            document.add(kolor);
            document.add(rok);
            for(int i = 0; i < 4; i++){
                Paragraph poduszka = new Paragraph("poduszka: " + cars.get(ktoryWLiscie).airbags.get(i).description + " - " + cars.get(ktoryWLiscie).airbags.get(i).value, font); // paragraf
                document.add(poduszka);
            }
            Image img = null;
            try {
                img = Image.getInstance("images/" + cars.get(ktoryWLiscie).getModel() +".jpg");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            document.add(img);

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        document.close();

        res.type("application/json");
        return gson.toJson(cars, ArrayList.class);
    }

    static Object downloadInvoice (Request req, Response res) {
        res.type("application/octet-stream");
        res.header("Content-Disposition", "attachment; filename=" + req.queryParams("uuid") + ".pdf");

        OutputStream  outputStream = null;
        try {
            outputStream = res.raw().getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            outputStream.write(Files.readAllBytes(Path.of("invoices/" + req.queryParams("uuid") + ".pdf")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "ok";
    }
}