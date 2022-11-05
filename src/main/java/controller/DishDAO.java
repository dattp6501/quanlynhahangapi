package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import model.entity.Dish;
import utils.ImageCustom;

public class DishDAO extends DAO{
    public DishDAO(){
        super();
    }

    private boolean add(Dish dish) throws SQLException{
        boolean ok = false;
        String insert = "insert into mon(ten,gia,size,anh,mota) values(?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(insert,Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, dish.getTen());
        ps.setFloat(2, (float)dish.getGia());
        ps.setString(3, dish.getSize());
        ps.setBytes(4, ImageCustom.Base64ToBytes(dish.getAnh()));
        ps.setString(5, dish.getMota());
        ps.executeUpdate();
        ResultSet res = ps.getGeneratedKeys();
        if(res.next()){
            dish.setId(res.getInt(1));
            ok = true;
        }
        return ok;
    }

    public boolean add(ArrayList<Dish> listDish) throws SQLException{
        boolean ok = false;
        connection.setAutoCommit(false);
        for(Dish d : listDish){
            if(!add(d)){
                connection.rollback();
                ok = false;
                break;
            }
            ok = true;
        }
        if(ok){
            connection.setAutoCommit(true);
        }
        return ok;
    }

    public boolean update(Dish dish) throws SQLException{
        boolean ok = false;
        String update = "update mon set ten=?,gia=?,size=?,anh=?,mota=?,soluong=? where id=?";
        PreparedStatement ps = connection.prepareStatement(update);
        ps.setString(1, dish.getTen());
        ps.setFloat(2, (float)dish.getGia());
        ps.setString(3, dish.getSize());
        ps.setBytes(4, ImageCustom.Base64ToBytes(dish.getAnh()));
        ps.setString(5, dish.getMota());
        ps.setInt(6, dish.getNumber());
        ps.setInt(7, dish.getId());
        ok = ps.executeUpdate()>0;
        return ok;
    }

    public boolean updateNumber(Dish dish) throws SQLException{
        boolean ok = false;
        String update = "update mon set soluong=? where id=?";
        PreparedStatement ps = connection.prepareStatement(update);
        ps.setInt(1, dish.getNumber());
        ps.setInt(2, dish.getId());
        ok = ps.executeUpdate()>0;
        return ok;
    }

    public boolean updateDishs(ArrayList<Dish> listDish) throws SQLException{
        boolean ok = false;
        connection.setAutoCommit(false);
        for(Dish d : listDish){
            if(!update(d)){
                connection.rollback();
                ok = false;
                break;
            }
            ok = true;
        }
        if(ok){
            connection.setAutoCommit(true);
        }
        return ok;
    }

    public ArrayList<Dish> get(String nameDish) throws SQLException{
        ArrayList<Dish> list = new ArrayList<>();
        String select = "select * from mon where ten like ?";
        PreparedStatement ps = connection.prepareStatement(select);
        ps.setString(1, "%"+nameDish+"%");
        ResultSet res = ps.executeQuery();
        while(res.next()){
            Dish d = new Dish();
            d.setId(res.getInt("id"));
            d.setTen(res.getString("ten"));
            d.setGia(res.getFloat("gia"));
            d.setSize(res.getString("size"));
            byte[] anh = res.getBytes("anh");
            if(anh!=null){
                d.setAnh(anh);
            }
            d.setMota(res.getString("mota"));
            d.setNumber(res.getInt("soluong"));
            list.add(d);
        }
        res.close();
        return list;
    }

    public Dish getByID(int id) throws SQLException{
        Dish dish = new Dish();
        String select = "select * from mon where id=?";
        PreparedStatement ps = connection.prepareStatement(select);
        ps.setInt(1, id);
        ResultSet res = ps.executeQuery();
        if(res.next()){
            dish.setId(res.getInt("id"));
            dish.setTen(res.getString("ten"));
            dish.setGia(res.getFloat("gia"));
            dish.setSize(res.getString("size"));
            byte[] anh = res.getBytes("anh");
            if(anh!=null){
                dish.setAnh(anh);
            }
            dish.setMota(res.getString("mota"));
            dish.setNumber(res.getInt("soluong"));
        }
        res.close();
        return dish;
    }
}
