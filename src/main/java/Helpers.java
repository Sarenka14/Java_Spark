public class Helpers {
    public static int day(){
        return (int) ((Math.random() * 30) + 1);
    }

    public static int month(){
        return (int) ((Math.random() * 11) + 1);
    }

    public static int year(){
        return (int) ((Math.random() * 5) + 2000);
    }

    public static int price(){
        return (int) ((Math.random() * 20000) + 10000);
    }

    public static int VAT(){
        int[] VAT = {0, 7, 22};
        return VAT[(int) (Math.random() * (3))];
    }
}

