SELECT * FROM quanlynhahang.nhom;
SELECT * FROM quanlynhahang.quyen;
SELECT * FROM quanlynhahang.quyennhom;

SELECT * FROM quanlynhahang.khachhang;
SELECT * FROM quanlynhahang.thanhvien;
SELECT * FROM quanlynhahang.thanhviennhom;
SELECT * FROM quanlynhahang.thanhviennhom where thanhvienid = 3;


SELECT * FROM quanlynhahang.ban;
SELECT * FROM quanlynhahang.bandat;
SELECT * FROM quanlynhahang.thoigiantrong order by banid;
SELECT * FROM quanlynhahang.lichdat;

SELECT * FROM quanlynhahang.mon;
SELECT * FROM quanlynhahang.mondat;


-- reset free time
TRUNCATE thoigiantrong;
