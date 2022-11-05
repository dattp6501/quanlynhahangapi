package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;

import model.entity.Dish;

public class DishDAO extends DAO{
    public DishDAO(){
        super();
    }

    private boolean add(Dish dish){
        boolean ok = false;
        try {
            String insert = "insert into mon(ten,gia,size,anh,mota) values(?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(insert,Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dish.getTen());
            ps.setFloat(2, (float)dish.getGia());
            ps.setString(3, dish.getSize());
            ps.setBytes(4, Base64.getDecoder().decode(dish.getAnh()));
            ps.setString(5, dish.getMota());
            ps.executeUpdate();
            ResultSet res = ps.getGeneratedKeys();
            if(res.next()){
                dish.setId(res.getInt(1));
                ok = true;
            }
        } catch (SQLException e) {
            ok = false;
            e.printStackTrace();
        }
        return ok;
    }

    public boolean add(ArrayList<Dish> listDish){
        boolean ok = false;
        try {
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
        } catch (SQLException e) {
            ok = false;
            e.printStackTrace();
        }
        return ok;
    }

    private boolean update(Dish dish){
        boolean ok = false;
        try {
            String update = "update mon set ten=?,gia=?,size=?,anh=?,mota=? where id=?";
            PreparedStatement ps = connection.prepareStatement(update);
            ps.setString(1, dish.getTen());
            ps.setFloat(2, (float)dish.getGia());
            ps.setString(3, dish.getSize());
            ps.setBytes(4, Base64.getDecoder().decode(dish.getAnh()));
            ps.setString(5, dish.getMota());
            ps.setInt(6, dish.getId());
            ok = ps.executeUpdate()>0;
        } catch (SQLException e) {
            ok = false;
            e.printStackTrace();
        }
        return ok;
    }

    public boolean updateDishs(ArrayList<Dish> listDish){
        boolean ok = false;
        try {
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
        } catch (SQLException e) {
            ok = false;
            e.printStackTrace();
        }
        return ok;
    }

    public ArrayList<Dish> get(String nameDish){
        ArrayList<Dish> list = new ArrayList<>();
        try {
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
                list.add(d);
            }
            res.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
