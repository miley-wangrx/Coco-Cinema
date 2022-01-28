from . import app, db
from os.path import abspath, join, dirname
from .models import User, Movie, Hall, Schedule, OrderDetail, get_current_price
from werkzeug.security import generate_password_hash
from urllib.parse import urlencode,quote,unquote
from .forms import RegisterForm,LoginForm,PasswordResetForm,EditMovieForm,HallForm,ScheduleForm,StaticsGraphForm, \
    SearchIdForm, SearchPhoneForm
from flask_login import login_user, login_required, logout_user,current_user,LoginManager
from flask import request, Flask,render_template, flash, redirect, session, url_for, request, g, abort,current_app
import pandas as pd
import numpy as np
import matplotlib as mpl
import matplotlib.pyplot as plt
from matplotlib.pylab import mpl
from sqlalchemy import func
import random
import os
import json
import datetime

# Display Chinese characters
mpl.rcParams['font.sans-serif']=['SimHei']
plt.rcParams['axes.unicode_minus'] = False

login_manager=LoginManager()
login_manager.session_protection='strong'
login_manager.login_view='admin_login'
login_manager.login_message='Please login first'
login_manager.init_app(app)

@login_manager.user_loader
def load_user(id):
    return User.query.get(int(id))


@app.before_request
def before_request():
    user_id = session.get('user_id')
    if user_id:
        user = User.query.filter(User.id == user_id).first()
        if user:
            g.user = user


@app.context_processor
def context_processor():
    if hasattr(g,'user'):
        return {'user': g.user}
    return {}

@app.route('/admin_login/',methods=['GET','POST'])
def admin_login():
    form = LoginForm()
    if request.method == 'GET':
        return render_template('admin_login.html',form=form)
    else:
        if form.validate_on_submit():
            data = form.data
            user = User.query.filter_by(telephone=data["telephone"]).first()
            if user == None:
                flash("Account does not exist!")
                return redirect(url_for('admin_login'))
            elif user != None and not user.check_password(data["password"]):
                flash("Wrong password!")
                return redirect(url_for('admin_login'))
            elif user != None and user.check_password(data["password"]):
                login_user(user, form.remember_me.data)
            return redirect(url_for('admin_index'))
        else:
            flash('Please check your input!')
            return render_template('admin_login.html', form=form)

@app.route('/admin_list_all_movies/')
@login_required
def admin_list_all_movies():
    page = request.args.get('page', 1, type=int)
    pagination = Movie.query.order_by(Movie.id.asc()).paginate(
        page, per_page=current_app.config['BLOG_PER_PAGE'],
        error_out=False)
    allmovies = pagination.items
    return render_template('list_all_movies.html', allmovies=allmovies, pagination=pagination)


@app.route('/admin_add_new_movie/', methods=['GET', 'POST'])
@login_required
def admin_add_new_movie():
    form = EditMovieForm()
    if form.validate_on_submit():
            name = form.name.data
            blurb = form.blurb.data
            certificate = form.certificate.data
            director = form.director.data
            actors = form.actors.data
            picture = request.files['picture']
            poster = request.files['poster']

            base_path = abspath(dirname(__file__))
            picture_path = join(base_path, 'static', 'APP_images', picture.filename)
            picture.save(picture_path)
            poster_path = join(base_path, 'static', 'APP_images', poster.filename)
            poster.save(poster_path)

            movie = Movie(name=name,
                        blurb=blurb,
                        certificate=certificate,
                        director=director,
                        actors=actors,
                        picture=picture.filename,
                        poster=poster.filename)
            db.session.add(movie)
            db.session.commit()
            return redirect(url_for('admin_index'))
    return render_template('edit_movie.html', form=form)

@app.route('/admin_delete_movie/<id>')
@login_required
def admin_delete_movie(id):
    movie = Movie.query.filter_by(id=id).first()
    db.session.delete(movie)
    db.session.commit()
    return redirect(url_for('admin_list_all_movies'))

@app.route('/delete_user/<id>')
@login_required
def delete_user(id):
    user = User.query.filter_by(id=id).first()

    db.session.delete(user)
    db.session.commit()

    return redirect(url_for('list_all_user'))


@app.route('/admin_add_new_hall/',methods=['GET','POST'])
@login_required
def admin_add_new_hall():
    form = HallForm()
    if form.validate_on_submit():
        hall = Hall(name=form.name.data,is_vip=form.is_vip.data)
        db.session.add(hall)
        db.session.commit()
        return redirect(url_for('admin_index'))
    return render_template('edit_hall.html', form=form)


@app.route('/admin_add_new_schedule/',methods=['GET','POST'])
@login_required
def admin_add_new_schedule():
    form = ScheduleForm()
    if form.validate_on_submit():
        schedule = Schedule(movie_id=form.movie_id.data,
                        hall_id=form.hall_id.data,
                        date=form.date.data,
                        start_time=form.start_time.data,
                        end_time=form.end_time.data,
                        price=form.price.data)
        db.session.add(schedule)
        db.session.commit()
        return redirect(url_for('admin_index'))
    else:
        movies = Movie.query.all()
        halls = Hall.query.all()
        return render_template('edit_schedule.html', halls=halls,movies=movies,form=form)
 
@app.route('/',methods=['GET'])
@app.route('/admin_index/',methods=['GET','POST'])
@login_required
def admin_index():
    form = StaticsGraphForm()
    data = []
    names = []
    data_new = {}
    all = []
    data_len = 0
    try:
        if form.validate_on_submit():
            begin_data = form.begin_date.data.replace("/","-")
            begin_data = datetime.datetime.strptime(begin_data,'%Y-%m-%d').date()
            end_date = form.end_date.data.replace("/","-")
            end_date = datetime.datetime.strptime(end_date,'%Y-%m-%d').date()
            # '''
            # for info in db.session.query(Schedule.date).filter(
            #                 Schedule.date >= begin_data).join(
            #                 OrderDetail, (OrderDetail.schedule_id == Schedule.id)).join(
            #                 Movie, (Schedule.movie_id == Movie.id)).all():
            #     data.append(info[1])
            #     names.append(info[0])
            # '''
            # for info in db.session.query(Movie.name,func.count(OrderDetail.id)).filter(
            #                 Schedule.date >= begin_data).filter(
            #                 Schedule.date <= end_date).filter(
            #                 OrderDetail.schedule_id == Schedule.id).filter(
            #                 Schedule.movie_id == Movie.id).group_by(Movie.name).all():
            #     data.append(info[1])
            #     names.append(info[0])
            #     all_info.append(info)
            schedules = Schedule.query.filter(Schedule.date >= begin_data).filter(Schedule.date <= end_date).all()
            print("test")
            for schedule in schedules:
                movieId = schedule.movie_id
                movie = Movie.query.get(movieId)
                for order in schedule.orders:
                    userId = order.user_id
                    user = User.query.get(userId)
                    print("test1")
                    price = schedule.price
                    if user.vip == 1:
                        price = int(int(price) * 0.8)
                    if 2020 - user.birthyear >= 65:
                        price = price * 0.9
                    print(price)
                    # test = int("50")
                    # print(test)
                    if movie.name in data_new.keys():
                        before = float(data_new[movie.name]) + float(price)
                        print(before)
                        data_new[movie.name] = str(before)
                        print(data_new[movie.name])
                    else:
                        data_new[movie.name] = str(price)

            for k, v in data_new.items():
                names.append(k)
                v_float = float(v)
                data.append(int(v_float))
                #data.append(2)
            print(data)
            print(names)
            f = open("temp/data.txt","w")
            f.write(json.dumps(data))
            f.close()
            f = open("temp/names.txt","w")
            f.write(json.dumps(names))
            f.close()
            data_len = len(names)
            if len(data) > 0:
                cmd = "python3 ./generateBar.py %d" %max(data)
                #cmd = "python ./generateBar.py %d" % max(data) #windows�����
                os.system(cmd)
        if len(data) > 0:
            return render_template('admin_index.html',data=data, names=names,data_new=data_new, data_len = data_len, show_picture=1,picture="numTickets.png",random_val=random.randint(0,999))
        else:
            return render_template('admin_index.html',data=data, names=names,data_new=data_new, data_len = data_len, show_picture=0)
    except:
        return render_template('admin_index.html',data=data, names=names,data_new=data_new, data_len = data_len, show_picture=0)


@app.route('/admin_pie_graph/',methods=['GET','POST'])
@login_required
def admin_pie_graph():
    form = StaticsGraphForm()
    all_data = []
    names = []
    all_info = []
    prop = []
    data_num = 0
    try:
        if form.validate_on_submit():
            begin_data = form.begin_date.data.replace("/", "-")
            begin_data = datetime.datetime.strptime(begin_data, '%Y-%m-%d').date()
            end_date = form.end_date.data.replace("/", "-")
            end_date = datetime.datetime.strptime(end_date, '%Y-%m-%d').date()
            for info in db.session.query(Movie.name, func.count(OrderDetail.id)).filter(
                    Schedule.date >= begin_data).filter(
                Schedule.date <= end_date).filter(
                OrderDetail.schedule_id == Schedule.id).filter(
                Schedule.movie_id == Movie.id).group_by(Movie.name).all():
                all_data.append(info[1])
                names.append(info[0])
                all_info.append(info)
                data_num += info[1]
            for data in all_data:
                prop.append(data / data_num)
            print(prop)
            print(names)
            f = open("temp/data2.txt", "w")
            f.write(json.dumps(prop))
            f.close()
            f = open("temp/names2.txt", "w")
            f.write(json.dumps(names))
            f.close()
            if len(prop) > 0:
                cmd = "python3 ./proportion.py"
                #cmd = "python ./proportion.py" #windows
                os.system(cmd)
        if len(all_info) > 0:
            return render_template('admin_pie.html', all_info=all_info, show_picture=1, picture="proportion.png",
                                   random_val=random.randint(0, 999))
        else:
            return render_template('admin_pie.html', all_info=all_info, show_picture=0)
    except:
        return render_template('admin_pie.html', all_info=all_info, show_picture=0)


@app.route('/logout/')
@login_required
def logout():
    logout_user()
    flash('Log out success')
    return redirect(url_for('admin_login'))

@app.route('/list_all_user')
@login_required
def list_all_user():
    page = request.args.get('page', 1, type=int)
    pagination = User.query.order_by(User.id.desc()).paginate(
        page, per_page=current_app.config['BLOG_PER_PAGE'],
        error_out=False)
    allusers = pagination.items
    if allusers is None:
        abort(404)
    return render_template('list_all_user.html', allusers=allusers, pagination=pagination)


@app.route('/search_order', methods=['GET','POST'])
@login_required
def search_order():
    form = SearchIdForm()
    if form.validate_on_submit():
        order_id = form.order_id.data
        user_order = OrderDetail.query.filter_by(id=order_id)
        return redirect(url_for('result_search_order', order_id=order_id))

    return render_template('search_order.html', form=form)


@app.route('/result_search_order/<order_id>', methods=['GET'])
@login_required
def result_search_order(order_id):
    order = OrderDetail.query.get(order_id)
    user_id = order.user_id
    schedule_id = order.schedule.id
    schedule = Schedule.query.get(schedule_id)
    movie_id = schedule.movie_id
    hall_id = schedule.hall_id
    hall = Hall.query.get(hall_id)
    movie = Movie.query.filter_by(id=movie_id).first()
    user = User.query.filter_by(id=user_id).first()
    phone = user.telephone
    movie_name = movie.name
    return render_template('result_search_order.html', phone=phone, movie=movie_name, order=order,
                           schedule=schedule, hall=hall.name)

@app.route('/pay_by_cash/<order_id>', methods=['GET','POST'])
@login_required
def pay_by_cash(order_id):
    print("pay_by_cash")
    order = OrderDetail.query.get(order_id)
    order.paid = 1
    order.pay_method = "cash"
    db.session.commit()
    return redirect(url_for('result_search_order', order_id=order_id))