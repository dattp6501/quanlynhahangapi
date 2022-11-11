package utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import javax.swing.ImageIcon;
import org.apache.commons.io.FileUtils;

public class ImageCustom {
    public static ImageIcon resize(ImageIcon imageIcon, int width, int height){
        Image image = imageIcon.getImage().getScaledInstance(width,height,Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }
    public static byte[] toByteArray(String path) throws IOException{
        return FileUtils.readFileToByteArray(new File(path));
    }
    public static String toStringBase64(String path) throws IOException{
        return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(new File(path)));
    }
    public static byte[] Base64ToBytes(String base64){
        return Base64.getDecoder().decode(base64);
    }
    public static void main(String[] args) throws IOException {
        System.out.println(ImageCustom.toStringBase64("C:\\Users\\Administrator\\Downloads\\conan.jpg"));
    }
}