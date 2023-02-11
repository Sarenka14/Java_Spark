import static java.lang.Integer.parseInt;
import static spark.Spark.*;

import com.fasterxml.uuid.Generators;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class App {
    public static int carid = 1;
    public static String imageUuid;

    static ArrayList<Car> cars = new ArrayList<>();

    static ArrayList<Invoice> allCarsInvoices = new ArrayList<>();

    static ArrayList<Invoice> yearInvoices = new ArrayList<>();

    static ArrayList<Invoice> rangeInvoices = new ArrayList<>();

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

        get("/thumb", (req, res) -> {
            File file = new File("images/" + req.queryParams("model") + ".jpg");
            res.type("image/jpeg");

            OutputStream outputStream = null;
            outputStream = res.raw().getOutputStream();

            outputStream.write(Files.readAllBytes(Path.of("images/" + req.queryParams("model"))));
            outputStream.flush();

            return outputStream;
        });

        post("/getAllCarsInvoices", App::resAllCarsInvoices);

        post("/generateAllCarsInvoice", App::generateAllCarsInvoice);

        post("/getYearInvoices", App::resYearInvoices);

        post("/generateYearInvoice", App::generateYearInvoice);

        post("/getRangeInvoices", App::resRangeInvoices);

        post("/generateRangeInvoice", App::generateRangeInvoice);

        get("/downloadCarsInvoice", App::downloadCarsInvoice);

        get("/redirect",App::redirect);

        post("/upload", App::getImages);

        post("/savePhotos", App::savePhotos);

        post("/gallery", App::resPhotos);


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
            auto.setRok((int) ((Math.random() * 5) + 2001));
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

    private static Object resAllCarsInvoices(Request req, Response res) {
        Gson gson = new Gson();

        res.type("application/json");
        return gson.toJson(allCarsInvoices, ArrayList.class);
    }

    private static Object generateAllCarsInvoice(Request req, Response res) {
        Gson gson = new Gson();

        Invoice faktura = new Invoice(req.body());

        faktura.list.clear();

        for(int i = 0; i < cars.size(); i++){
            faktura.list.add(cars.get(i));
        }

        allCarsInvoices.add(faktura);

        Invoices.makeInvoice(faktura, "Faktura za wszystkie auta", "invoices/invoice_all_cars_" + faktura.getTime() + ".pdf");

        res.type("application/json");
        return gson.toJson(allCarsInvoices, ArrayList.class);
    }

    private static Object resYearInvoices(Request req, Response res) {
        Gson gson = new Gson();

        res.type("application/json");
        return gson.toJson(yearInvoices, ArrayList.class);
    }

    private static Object generateYearInvoice(Request req, Response res) {
        Gson gson = new Gson();

        JsonObject json = new JsonParser().parse(req.body()).getAsJsonObject();
        JsonElement datetime = json.get("datetime");
        JsonElement year = json.get("rok");

        String dataczas = datetime.getAsString();
        int rok = Integer.parseInt(year.getAsString());

        Invoice faktura = new Invoice(dataczas, rok);

        faktura.list.clear();

        for(int i = 0; i < cars.size(); i++){
            if(cars.get(i).getRok() == rok){
                faktura.list.add(cars.get(i));
            }
        }

        yearInvoices.add(faktura);

        Invoices.makeInvoice(faktura, "Faktura za auta z roku: " + rok, "invoices/invoice_all_cars_from_year_" + faktura.getTime() + ".pdf");

        res.type("application/json");
        return gson.toJson(yearInvoices, ArrayList.class);
    }

    private static Object resRangeInvoices(Request req, Response res) {
        Gson gson = new Gson();

        res.type("application/json");
        return gson.toJson(rangeInvoices, ArrayList.class);
    }

    private static Object generateRangeInvoice(Request req, Response res) {
        Gson gson = new Gson();

        JsonObject json = new JsonParser().parse(req.body()).getAsJsonObject();
        JsonElement datetime = json.get("datetime");
        JsonElement min = json.get("min");
        JsonElement max = json.get("max");

        String dataczas = datetime.getAsString();
        int intMin = Integer.parseInt(min.getAsString());
        int intMax = Integer.parseInt(max.getAsString());

        Invoice faktura = new Invoice(dataczas, intMin, intMax);

        faktura.list.clear();

        for(int i = 0; i < cars.size(); i++){
            if(cars.get(i).getPrice() >= intMin && cars.get(i).getPrice() <= intMax){
                faktura.list.add(cars.get(i));
            }
        }

        rangeInvoices.add(faktura);

        Invoices.makeInvoice(faktura, "Faktura za auta w cenach: " + intMin + "-" + intMax + " PLN", "invoices/invoice_all_cars_by_price_" + faktura.getTime() + ".pdf");

        res.type("application/json");
        return gson.toJson(rangeInvoices, ArrayList.class);
    }

    private static Object downloadCarsInvoice(Request req, Response res) {
        res.type("application/octet-stream");
        res.header("Content-Disposition", "attachment; filename=" + req.queryParams("title") + ".pdf");

        OutputStream  outputStream = null;
        try {
            outputStream = res.raw().getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            outputStream.write(Files.readAllBytes(Path.of("invoices/" + req.queryParams("title") + ".pdf")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "ok";
    }

    public static String redirect(Request req,Response res){
        imageUuid = req.queryParams("uuid");
        imageUuid = imageUuid.substring(1, imageUuid.length() - 1);
        if(req.queryParams("type").equals("upload")){
            res.redirect("/upload.html");
        }else if(req.queryParams("type").equals("gallery")){
            res.redirect("/gallery.html");
        }

        return "ok";
    }

    private static Object getImages(Request req, Response res) {
        Gson gson = new Gson();
        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/images"));

        ArrayList<String> nazwyPlikow = new ArrayList<>();

        try {
            for(Part p : req.raw().getParts()){
                InputStream inputStream = p.getInputStream();
                // inputstream to byte
                byte[] bytes = inputStream.readAllBytes();
                String fileName = String.valueOf(System.currentTimeMillis());
                FileOutputStream fos = new FileOutputStream("images/" + fileName);
                fos.write(bytes);
                fos.close();
                nazwyPlikow.add(fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        return gson.toJson(nazwyPlikow);
    }

    private static Object savePhotos(Request req, Response res) {
        Gson gson = new Gson();

        JsonObject json = new JsonParser().parse(req.body()).getAsJsonObject();
        JsonElement photoArray = json.get("photoArray");
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> yourList = new Gson().fromJson(photoArray, listType);

        int temp = 0;
        for(int i = 0; i <= cars.size()-1;i++){
            if (cars.get(i).getUuid().equals(imageUuid)){
                temp = i;
                for(int j = 0; j <= yourList.size()-1; j++){
                    cars.get(temp).photos.add(yourList.get(j));
                }
            }
        }
        return "ok";
    }

    private static Object resPhotos(Request req, Response res) {
        Gson gson = new Gson();

        int temp = 0;
        for(int i = 0; i <= cars.size()-1;i++){
            if (cars.get(i).getUuid().equals(imageUuid)){
                temp = i;
            }
        }
        return gson.toJson(cars.get(temp).photos);
    }
}