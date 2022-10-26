package model.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThoiGianTrong implements Comparable<ThoiGianTrong>{
    private int id;
    private Date starttime,endtime;
    private String mota;
    private Ban Table;
    public ThoiGianTrong() {
    }
    public ThoiGianTrong(int id, Date starttime, Date endtime, Ban Table) {
        this.id = id;
        this.starttime = starttime;
        this.endtime = endtime;
        this.Table = Table;
    }
    public ThoiGianTrong(int id, String startTimeStr, String endTimeStr, Ban Table) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        this.id = id;
        try {
            this.starttime = format.parse(startTimeStr);
            this.endtime = format.parse(endTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.Table = Table;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Date getStarttime() {
        return starttime;
    }
    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }
    public Date getEndtime() {
        return endtime;
    }
    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }
    public Ban getTable() {
        return Table;
    }
    public void setTable(Ban Table) {
        this.Table = Table;
    }
    public String getMota() {
        return mota;
    }
    public void setMota(String mota) {
        this.mota = mota;
    }
    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return "ThoiGianTrong [id=" + id + ", starttime=" + format.format(starttime) + ", endtime=" + format.format(endtime) + ", mota=" + mota
                + ", Table=" + Table + "]";
    }
    @Override
    public int compareTo(ThoiGianTrong o) {
        return this.starttime.compareTo(o.starttime);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((starttime == null) ? 0 : starttime.hashCode());
        result = prime * result + ((endtime == null) ? 0 : endtime.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ThoiGianTrong))
            return false;
        ThoiGianTrong other = (ThoiGianTrong) obj;
        if(other.starttime==null||other.endtime==null||starttime==null||endtime==null) return false;
        return starttime.compareTo(other.starttime)>=0&&endtime.compareTo(other.endtime)<=0;
    }
}
