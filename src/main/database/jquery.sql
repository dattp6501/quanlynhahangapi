-- add customer
insert into thanhvien(tendaydu,tendangnhap,matkhau) values(?,?,?);
insert into khachhang(thanhvienid,sdt,diachi) values(?,?,?);
-- check customer
select  *
from thanhvien
inner join khachhang
on thanhvien.id = khachhang.id
where thanhvien.tendangnhap = 'dattp' and thanhvien.matkhau = '123456789';
-- check group
select nhom.id, nhom.ten, nhom.mota
from thanhviennhom
inner join nhom
on thanhviennhom.nhomid = nhom.id
where thanhviennhom.thanhvienid = 9;
-- check permission
select quyen.id,quyen.ten,quyen.mota
from quyennhom
inner join quyen
on quyennhom.quyenid = quyen.id
where quyennhom.nhomid = 1;


-- thanh vien nhom
insert into thanhviennhom(thanhvienid,nhomid) values(?,?);
-- --------------------------------BAN-------------------------
-- INSERT TABLE
insert into ban(ten,gia,songuoi,mota) values(?,?,?,?);
insert into thoigiantrong(banid,batdau,ketthuc,mota) values(?,?,?,?);
-- DELETE FREETIME
delete from thoigiantrong where id = ?;
update thoigiantrong
set batdau=?,ketthuc=?
where id=?;
-- ---------------------booking table-----------------
--    Step 1: get id table free
select banid,id,batdau,ketthuc from thoigiantrong 
where batdau<='2022-10-17 16:59:00' and '2022-12-31 23:59:00'<=ketthuc;
select banid,id,batdau,ketthuc from thoigiantrong 
where '2022-12-31 23:59:00'<=ketthuc;
--    Step 2: get table
select * from ban where id = 1;
--    Step 3: get list freetime
select * from thoigiantrong where banid = 1;
--    strp 4: add 
insert into lichdat(ngay,tiencoc,mota,khachhangid) values(?,?,?,?);
insert into bandat(gia,batdau,thoiluong,mota,banid) values(?,?,?,?,?);
insert into mondat() values();
delete from thoigiantrong where id = ?;
-- ----------------------------booking dish -------------------------------
insert into mondat(soluong,gia,size,mota,modid,bandatid) values(?,?,?,?,?,?);

-- ----------------------- dish---------------
insert into mon(ten,gia,size,anh,mota) values(?,?,?,?,?);
select * from mon where ten like '%gà%';
update mon set ten=?,gia=?,size=?,anh=?,mota=? where id=?;




#########################################################################################
-- create group
insert into nhom(ten,mota) values('CUSTOMER','');
insert into nhom(ten,mota) values('ADMIN','');
-- create permission
insert into quyen(ten,mota) values('LOGIN','');
insert into quyen(ten,mota) values('MANAGER_TABLE','');
insert into quyen(ten,mota) values('MANAGER_DISH','');
insert into quyen(ten,mota) values('MANAGER_USER','');
insert into quyen(ten,mota) values('MANAGER_GROUP','');
insert into quyen(ten,mota) values('MANAGER_ALL_GROUP','');

delete from quyen where id = ?;
-- add permission for group
insert into quyennhom(nhomid,quyenid) values(1,1);
insert into quyennhom(nhomid,quyenid) values(2,1);

delete from quyennhom where nhomid=? and quyenid=?;
--                       nhom admin
insert into quyennhom(nhomid,quyenid) values(2,2);
insert into quyennhom(nhomid,quyenid) values(2,3);
insert into quyennhom(nhomid,quyenid) values(2,4);
insert into quyennhom(nhomid,quyenid) values(2,5);
insert into quyennhom(nhomid,quyenid) values(2,6);
-- member in group
insert into thanhviennhom(thanhvienid,nhomid) values(?,?);
update thanhviennhom
set nhomid = 2
where thanhvienid = 3;