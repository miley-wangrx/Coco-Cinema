from . import db
from datetime import datetime
from werkzeug.security import generate_password_hash,check_password_hash
from flask_login import UserMixin, current_user
from flask import session

def get_current_price(price):
    actual_price = int(price)
    if (not session.get('is_login')) or session['is_login'] != 1:
        actual_price = int(price)
    else:
        if current_user.vip == 1:
            actual_price = int(int(price) * 0.8)
        if 2020-current_user.birthyear >= 65:
            actual_price = actual_price * 0.9
    return actual_price

class OrderDetail(db.Model):
    __tablename__ = 'order_detail'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    schedule_id = db.Column(db.Integer, db.ForeignKey('schedule.id')) #, ondelete='CASCADE')
    user_id = db.Column(db.Integer, db.ForeignKey('users.id')) #, ondelete='CASCADE'
    seat_row = db.Column(db.Integer, nullable=False)
    seat_column = db.Column(db.Integer, nullable=False)
    pay_method = db.Column(db.String(20), nullable=False)
    ticket_uuid = db.Column(db.String(40), nullable=False)
    paid = db.Column(db.Integer, default=0)
    unique_identity = db.Column(db.String(20), nullable=False)


    schedule_info = db.relationship('Schedule', primaryjoin='OrderDetail.schedule_id==foreign(Schedule.id)', uselist=False)
    user_info = db.relationship('User', primaryjoin='OrderDetail.user_id==foreign(User.id)', uselist=False)


    def to_dict(self):
        user_dict = {
            "order_id": self.id,
            "schedule_id": self.schedule_id,
            "user_id": self.user_id,
            "seat_row": self.seat_row,
            "seat_column": self.seat_column,
            "pay_method": self.pay_method,
        }
        return user_dict
    def record_to_dict(self):
        record_dict = {
            "order_id": self.id,
            "movie_name": self.schedule_info.movie_info.name,
            "actors": self.schedule_info.movie_info.actors,
            "picture": self.schedule_info.movie_info.picture,
            "price": get_current_price(self.schedule_info.price),
            "pay_method": self.pay_method,
            "order_number": self.id,
            "paid": self.paid,
        }
        return record_dict
    def ticket_to_dict(self):
        ticket_dict = {
            "ticket_uuid": self.ticket_uuid + ".png",
        }
        return ticket_dict
    def record_to_dict2(self):
        record_dict = {
            "movie_name": self.schedule_info.movie_info.name,
            "date": self.schedule_info.date.strftime('%Y-%m-%d'),
            "start_time": self.schedule_info.start_time,
            "poster": self.schedule_info.movie_info.poster,
            "price": get_current_price(self.schedule_info.price),
        }
        return record_dict


class User(db.Model,UserMixin):
    __tablename__ = "users"
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    telephone = db.Column(db.String(30), nullable=False)
    email = db.Column(db.String(30), default="NO_EMAIL")
    card = db.Column(db.String(20), default="NO_CARD")
    birthyear = db.Column(db.Integer, nullable=False)
    password = db.Column(db.String(100), nullable=False)
    vip = db.Column(db.Integer, default=0)
    privilege = db.Column(db.Integer, default=0)

    orders = db.relationship('OrderDetail', backref='users', lazy='dynamic', passive_deletes=True,
                             cascade='all, delete-orphan', single_parent=True)
    # order = db.relationship('OrderDetail', foreign_keys=[OrderDetail.user_id],
    #                            backref=db.backref('users', lazy='joined'), lazy='dynamic',
    #                            cascade='all, delete-orphan', single_parent=True)

    def is_authenticated(self):
        return True

    def is_active(self):
        return True

    def is_anonymous(self):
        return False

    def get_id(self):
        return self.id

    def __init__(self,*args,**kwargs):
        telephone = kwargs.get('telephone')
        password = kwargs.get('password')

        self.telephone = telephone
        self.password = generate_password_hash(password)

    def check_password(self,raw_password):
        result = check_password_hash(self.password,raw_password)
        return result


class Movie(db.Model):
    __tablename__ = 'movies'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String(20), nullable=False)
    blurb = db.Column(db.String(1000), nullable=False)
    certificate = db.Column(db.String(50), nullable=False)
    director = db.Column(db.String(10), nullable=False)
    actors = db.Column(db.String(30), nullable=False)
    picture = db.Column(db.String(100), nullable=False)
    poster = db.Column(db.String(100), nullable=False)

    schedule_of_movie = db.relationship('Schedule', backref='movies', lazy='dynamic', passive_deletes=True,
                                       cascade='all, delete-orphan', single_parent=True)

    def to_dict(self):
        user_dict = {
            "movie_id": self.id,
            "name": self.name,
            "blurb": self.blurb,
            "certificate": self.certificate,
            "director": self.director,
            "actors": self.actors,
            "picture": self.picture,
            "poster": self.poster,
        }
        return user_dict

class Hall(db.Model):
    __tablename__ = 'hall'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String(50), nullable=False)
    is_vip = db.Column(db.Integer)

    schedule_of_hall = db.relationship('Schedule', backref='hall', lazy='dynamic', passive_deletes=True,
                                cascade='all, delete-orphan', single_parent=True)


class Schedule(db.Model):
    __tablename__ = 'schedule'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    movie_id = db.Column(db.Integer, db.ForeignKey('movies.id'))
    hall_id = db.Column(db.Integer, db.ForeignKey('hall.id'))
    date = db.Column(db.Date, nullable=False)
    start_time = db.Column(db.String(20), nullable=False)
    end_time = db.Column(db.String(20), nullable=False)
    price = db.Column(db.String(10), nullable=False)
    movie_info = db.relationship('Movie', primaryjoin='Schedule.movie_id==foreign(Movie.id)', uselist=False)
    hall_info = db.relationship('Hall', primaryjoin='Schedule.hall_id==foreign(Hall.id)', uselist=False)

    orders = db.relationship('OrderDetail', backref='schedule', lazy='dynamic', passive_deletes=True,
                             cascade='all, delete-orphan', single_parent=True)

    # order_schedule = db.relationship('User', secondary="order_detail", backref='schedule', lazy='joined', passive_deletes=True,
    #                         cascade='all, delete-orphan', single_parent=True)
    # order_schedule = db.relationship('OrderDetail', foreign_keys=[OrderDetail.schedule_id],
    #                         backref=db.backref('schedule', lazy='joined'), lazy='dynamic',
    #                         cascade='all, delete-orphan', single_parent=True)

    def to_dict(self):
        user_dict = {
            "schedule_id": self.id,
            "movie_id": self.movie_id,
            "hall_id": self.hall_id,
            "date": self.date.strftime('%Y-%m-%d'),
            "start_time": self.start_time,
            "end_time": self.end_time,
            "price": get_current_price(self.price),
        }
        return user_dict


