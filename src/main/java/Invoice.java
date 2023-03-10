import java.text.DecimalFormat;
import java.util.ArrayList;

public class Invoice {
    private int time = (int) System.currentTimeMillis();

    private String title;

    private String seller = "Sprzedawca: firma sprzedająca auta";

    private String buyer = "Nabywca: nabywca";

    private int rok;

    private int minPrice;

    private int maxPrice;

    public static ArrayList<Car> list = new ArrayList<>();

    float doZaplaty = 0;
    public double totalPrice(){
        for(int i = 0; i < list.size(); i++){
            doZaplaty += list.get(i).getPrice();
            float vatUlamkowy = (float) list.get(i).getVAT()/100;
            doZaplaty += list.get(i).getPrice() * vatUlamkowy;
        }
        return Math.floor(doZaplaty * 100) / 100;
    }

    public Invoice(String title) {
        this.title = title;
    }

    public Invoice(String title, int rok) {
        this.title = title;
        this.rok = rok;
    }

    public Invoice(String title, int minPrice, int maxPrice) {
        this.title = title;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public int getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getSeller() {
        return seller;
    }

    public String getBuyer() {
        return buyer;
    }

    public int getRok() {
        return rok;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }
}
