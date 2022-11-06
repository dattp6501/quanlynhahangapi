create table thanhvien(
	id integer auto_increment,
    tendaydu nvarchar(50) not null,
    tendangnhap varchar(20) not null unique,
    matkhau varchar(16) not null,
    primary key(id,tendangnhap)
);
alter table thanhvien auto_increment = 1;

create table khachhang(
	id integer primary key,
	sdt varchar(10) unique not null,
    diachi nvarchar(100),
    foreign key(id) references thanhvien(id)
);

create table ban(
	id integer auto_increment,
    ten varchar(10) unique not null,
    gia float not null,
    songuoi integer not null,
    mota nvarchar(255),
    primary key(id,ten)
);
alter table ban auto_increment = 1;

create table thoigiantrong(
	id integer primary key auto_increment,
    batdau time not null,
    ketthuc time not null,
    mota nvarchar(255),
    banid integer not null,
    foreign key(banid) references ban(id)
);
alter table thoigiantrong auto_increment = 1;

create table mon(
	id integer primary key auto_increment,
    ten nvarchar(100) not null,
    gia float not null,
    size varchar(5),
    anh binary,
    mota nvarchar(255)
);
alter table mon auto_increment = 1;

create table combo(
	id integer primary key auto_increment,
    ten nvarchar(100) not null,
    gia float not null,
    anh binary,
    mota nvarchar(255)
);
alter table combo auto_increment = 1;

create table moncombo(
	id integer primary key auto_increment,
	gia float not null,
    soluong integer not null,
    size varchar(5),
    mota nvarchar(255),
    monid integer not null,
    comboid integer not null,
    foreign key(monid) references mon(id),
    foreign key(comboid) references combo(id)
);
alter table moncombo auto_increment = 1;

create table lichdat(
	id integer primary key auto_increment,
    ngay datetime not null,
    tiencoc float not null,
    mota nvarchar(255),
    khachhangid integer not null,
    foreign key(khachhangid) references khachhang(id)
);
alter table lichdat auto_increment = 1;

create table bandat(
	id integer primary key auto_increment,
    gia float not null, 
    batdau datetime not null,
    thoiluong integer not null,
    mota nvarchar(255),
    banid integer not null,
    lichdatid integer not null,
    foreign key(banid) references ban(id),
    foreign key(lichdatid) references lichdat(id)
);
alter table bandat auto_increment = 1;

create table mondat(
	id integer primary key auto_increment,
    soluong integer not null,
    gia float not null,
    size varchar(5),
    mota nvarchar(255),
    monid integer not null,
    bandatid integer not null,
    foreign key(monid) references mon(id),
    foreign key(bandatid) references bandat(id)
);
alter table mondat auto_increment = 1;

create table combodat(
	id integer primary key auto_increment,
    soluong integer not null,
    gia float not null,
    mota nvarchar(255),
    comboid integer not null,
    bandatid integer not null,
    foreign key(comboid) references bandat(id),
    foreign key(bandatid) references bandat(id)
);
alter table combodat auto_increment = 1;

############################################################################################
#--------------------------------- phat trien them-----------------------------------------#
create table nhom(
	id integer primary key auto_increment,
    ten varchar(20) not null,
    mota nvarchar(255)
);
alter table nhom auto_increment = 1;

create table thanhviennhom(
	id integer primary key auto_increment,
	thanhvienid integer not null,
    nhomid integer not null,
    foreign key(thanhvienid) references thanhvien(id),
    foreign key(nhomid) references nhom(id)
);
alter table thanhviennhom auto_increment = 1;

create table quyen(
	id integer primary key auto_increment,
    ten varchar(30) not null unique,
    mota nvarchar(255)
);
alter table quyen auto_increment = 1;

create table quyennhom(
	id integer primary key auto_increment,
    nhomid integer not null,
    quyenid integer not null,
    foreign key(nhomid) references nhom(id),
    foreign key(quyenid) references quyen(id)
);
alter table quyennhom auto_increment = 1;



