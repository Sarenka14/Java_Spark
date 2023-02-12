import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;

public class Imaging {
    public static String Rotate(String path) throws IOException {
        System.out.println(path);
        File sourceFile = new File("images/" + path);
        BufferedImage originalImage = ImageIO.read(sourceFile);

        System.out.println(originalImage.getWidth());
        System.out.println(originalImage.getHeight());

        BufferedImage targetImage = Scalr.rotate(originalImage, Scalr.Rotation.CW_90);

        System.out.println(targetImage.getWidth());
        System.out.println(targetImage.getHeight());

        File targetFile = new File("images/" + path);
        ImageIO.write(targetImage, "jpg", targetFile);

        originalImage.flush();
        targetImage.flush();
        return targetImage.getWidth()+ "," +targetImage.getHeight();
    }
    public static String Flip(String type,String path) throws IOException {
        File sourceFile = new File("images/" + path);
        BufferedImage originalImage = ImageIO.read(sourceFile);
        BufferedImage targetImage = null;
        if(type.equals("v")){
            targetImage = Scalr.rotate(originalImage, Scalr.Rotation.FLIP_VERT);
        }else if (type.equals("h")){
            targetImage = Scalr.rotate(originalImage, Scalr.Rotation.FLIP_HORZ);
        }
        File targetFile = new File("images/" + path);
        ImageIO.write(targetImage, "jpg", targetFile);

        originalImage.flush();
        targetImage.flush();
        return targetImage.getWidth()+ "," +targetImage.getHeight();
    }
    public static String Crop(String path,String x,String y,String w,String h) throws IOException {
        File sourceFile = new File("images/" +path);
        BufferedImage originalImage = ImageIO.read(sourceFile);

        BufferedImage targetImage = Scalr.crop(originalImage, Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(w),Integer.parseInt(h)); // x,y,w,h
        File targetFile = new File("images/" +path);

        ImageIO.write(targetImage, "jpg", targetFile);

        originalImage.flush();
        targetImage.flush();
        return targetImage.getWidth()+ "," +targetImage.getHeight();
    }
    public static String getSize(String path) throws IOException {
        File sourceFile = new File("images/" + path);
        BufferedImage originalImage = ImageIO.read(sourceFile);
        return originalImage.getWidth()+ "," +originalImage.getHeight();
    }
}

class Crop{
    private String id;
    private String x;
    private String y;
    private String w;
    private String h;

    public String getId() {
        return id;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getW() {
        return w;
    }

    public String getH() {
        return h;
    }
}

class Flip{
    private String id;
    private String type;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}

class Id{
    private String id;

    public String getId() {
        return id;
    }
}