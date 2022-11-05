package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import model.entity.BookingDish;
import model.entity.TableBooking;

public class BookingDishDAO extends DAO{
    public BookingDishDAO(){
        super();
    }
    private boolean add(BookingDish bd, TableBooking table) throws SQLException{
        boolean ok = true;
        String insert = "insert into mondat(soluong,gia,size,mota,monid,bandatid) values(?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(insert,Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, bd.getNumber());
        ps.setFloat(2, bd.getPrice());
        ps.setString(3, bd.getSize());
        ps.setString(4, bd.getNote());
        ps.setInt(5, bd.getDish().getId());
        ps.setInt(6, table.getId());
        ps.executeUpdate();
        ResultSet res = ps.getGeneratedKeys();
        if(!res.next()){
            ok = false;
            return ok;
        }
        bd.setId(res.getInt(1));
        res.close();
        // update number dish
        DishDAO DDAO = new DishDAO(); DDAO.setConnection(connection);
        int numberDishOld = DDAO.getByID(bd.getDish().getId()).getNumber();
        if(numberDishOld<bd.getNumber()){
            ok = false;
            return ok;
        }
        bd.getDish().setNumber(numberDishOld-bd.getNumber());
        if(!DDAO.updateNumber(bd.getDish())){
            ok = false;
        }
        ok = true;
        return ok;
    }

    public boolean add(ArrayList<BookingDish> list, TableBooking table) throws SQLException{
        // connection.setAutoCommit(false);
        boolean ok = false;
        for(BookingDish bd : list){
            if(!add(bd,table)){
                ok = false;
                break;
            }
            // add booking dish success
            ok = true;
        }
        // if(!ok){
        //     connection.rollback();
        // }else{
        //     connection.setAutoCommit(true);
        // }
        return ok;
    }

    public ArrayList<BookingDish> get(TableBooking TB) throws SQLException{
        ArrayList<BookingDish> list = new ArrayList<>();
        String select = "select * from mondat where bandatid=? ";
        PreparedStatement ps = connection.prepareStatement(select);
        ps.setInt(1, TB.getId());
        ResultSet res = ps.executeQuery();
        while(res.next()){
            int id = res.getInt("id");
            int number = res.getInt("soluong");
            float price = res.getFloat("gia");
            String size = res.getString("size");
            String note = res.getString("mota");
            int dishID = res.getInt("monid");
            DishDAO DDAO = new DishDAO();
            DDAO.setConnection(connection);
            BookingDish BD = new BookingDish(id, number, price, size, note, DDAO.getByID(dishID));
            list.add(BD);
            TB.setDishs(list);
        }
        res.close();
        return list;
    }
}
