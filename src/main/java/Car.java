import java.util.ArrayList;

public class Car{
    private int id;
    private String uuid;
    private String model;

    public static class Airbag {
        String description;
        boolean value;

        public Airbag(String description, boolean value) {
            this.description = description;
            this.value = value;
        }
    }

    public ArrayList<Airbag> airbags = new ArrayList<>();

    private int rok;
    private String kolor;

    private boolean generatedInvoice = false;

    CustomDate dateInt = new CustomDate(Helpers.year(), Helpers.month(), Helpers.day());

    String dateString = String.valueOf(dateInt.getYear()) + "/" + String.valueOf(dateInt.getMonth()) + "/" + String.valueOf(dateInt.getDay());

    int price = Helpers.price();

    int VAT = Helpers.VAT();

    public Car(){
        this.id = App.carid++;
    }

    public String getUuid() {
        return uuid;
    }

    public String getModel() {
        return model;
    }

    public int getRok() {
        return rok;
    }

    public String getKolor() {
        return kolor;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setRok(int rok) {
        this.rok = rok;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setKolor(String kolor) {
        this.kolor = kolor;
    }

    public void setGeneratedInvoice(boolean generatedInvoice) {
        this.generatedInvoice = generatedInvoice;
    }

    public int getPrice() {
        return price;
    }

    public int getVAT() {
        return VAT;
    }
}