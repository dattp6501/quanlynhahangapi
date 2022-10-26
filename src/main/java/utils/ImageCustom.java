package utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;

import controller.dish.DishDAO;
import model.entity.Mon;

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
    public static void main(String[] args) {
        byte[] anh;
        try {
            anh = ImageCustom.toByteArray("C:\\Users\\Administrator\\Downloads\\vitquay.jpg");
            Mon mon = new Mon("vịt quay", "test", "XL", 100000, anh, 2);
            ArrayList<Mon> list = new ArrayList<>();
            list.add(mon);
            DishDAO dishDAO = new DishDAO();
            if(dishDAO.connect()){
                System.out.println(dishDAO.updateDishs(list));
            }else{
                System.out.println("khong ket noi duoc");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}