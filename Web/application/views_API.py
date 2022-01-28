from . import app, db
from os.path import abspath, join, dirname
from .models import User, Movie, Schedule, OrderDetail, Hall, get_current_price
from werkzeug.security import generate_password_hash
from urllib.parse import urlencode,quote,unquote
from .forms import RegisterForm,LoginForm,PasswordResetForm,OrderDetailForm
from flask_login import login_user, login_required, logout_user,current_user,LoginManager
from flask import request, Flask,render_template, flash, redirect, session, url_for, request, g, abort,current_app, jsonify
import json
import random
from aliyunsdkcore.client import AcsClient,CommonRequest
import smtplib
from email.mime.multipart import MIMEMultipart
from email.header import Header
from email.mime.text import MIMEText
from email.mime.image import MIMEImage
import os
import numpy as np
from PIL import Image, ImageDraw, ImageFont
import qrcode
import uuid
from datetime import datetime

# pip install aliyun-python-sdk-core

def send_mess(phone,code):
    client = AcsClient('LTAI4FwNJf4UEzPP2JnPi7kw', 'gU8JVd5G3RBer2bm8CEooflXR6mXbS', 'cn-hangzhou')
    request = CommonRequest()
    request.set_accept_format('json')
    request.set_domain('dysmsapi.aliyuncs.com')
    request.set_method('POST')
    request.set_protocol_type('https') # https | http
    request.set_version('2017-05-25')
    request.set_action_name('SendSms')
    request.add_query_param('RegionId', "cn-hangzhou")
    request.add_query_param('PhoneNumbers', phone)
    request.add_query_param('SignName', "呆呆软件")
    request.add_query_param('TemplateCode', "SMS_183790942")
    request.add_query_param('TemplateParam', "{\"code\":\"%s\"}" %code)
    response = client.do_action(request)
    return response


# 生成二维码
def GenerateQRCode(text):
    print(text)
    path = os.path.abspath("..")+"\\static\\qrcode\\" + str(uuid.uuid1()) + ".png"
    code = qrcode.make(text)
    code.save(path)
    return path

def GenerateTicket(movie,hall,date,time,position,price,ticket_uuid):
    # 获取生成的二维码
    qr_path = GenerateQRCode(ticket_uuid)
    qr_img = Image.open(qr_path)
    # 修改二维码尺寸
    qr_resize = qr_img.resize((155,155),Image.ANTIALIAS)

    # PIL读取图片
    pilimg = Image.open(os.path.abspath("..")+'/static/ticket.jpg')
    # 添加二维码
    pilimg.paste(qr_resize,(3, 250, 3+qr_resize.size[0], 250+qr_resize.size[1]))
    # PIL图片上打印汉字
    draw = ImageDraw.Draw(pilimg)  # 图片上打印
    font = ImageFont.truetype(os.path.abspath("..")+"/static/font1.ttf", 20, encoding="utf-8")  # 参数1：字体文件路径，参数2：字体大小
    # 主券
    draw.text((85, 150), movie, (0, 0, 0), font=font)  # 参数1：打印坐标，参数2：文本，参数3：字体颜色，参数4：字体
    draw.text((410, 150), hall, (0, 0, 0), font=font)
    draw.text((85, 215), date, (0, 0, 0), font=font)
    draw.text((410, 215), time, (0, 0, 0), font=font)
    draw.text((220, 285), position, (0, 0, 0), font=font)
    draw.text((220, 355), price, (0, 0, 0), font=font)
    # 副券
    draw.text((595, 105), hall, (0, 0, 0), font=font)
    draw.text((575, 195), date, (0, 0, 0), font=font)
    draw.text((575, 280), time, (0, 0, 0), font=font)
    draw.text((595, 360), position, (0, 0, 0), font=font)

    path2 = os.path.abspath("..")+"/static/tickets/" + ticket_uuid + ".png"
    pilimg.save(path2)
    return path2

def SendEmailWithTicket(mail,subject,body,ticket_path):
    smtpserver = "smtp.qq.com"
    smtpport = 465
    from_mail = "2787697211@qq.com"
    to_mail = [mail]
    password = "idcrnuzeitktddbb"   # 16位授权码

    from_name = "Movie Center"
    msg = MIMEMultipart()

    msgtext = MIMEText(body, "html", "utf-8")
    content = MIMEText('<html><body><img src="cid:imageid" alt="imageid"></body></html>', 'html', 'utf-8')
    msg.attach(msgtext)
    msg.attach(content)
    file = open(ticket_path, "rb")
    img = MIMEImage(file.read())
    file.close()
    img.add_header("Content-ID", "<imageid>")
    msg.attach(img)

    msg["Subject"] = Header(subject, "utf-8")
    msg["From"] = Header(from_name + " <" + from_mail + ">", "utf-8")
    msg["To"] = Header(",".join(to_mail), "utf-8")

    try:
        smtp = smtplib.SMTP_SSL(smtpserver,smtpport)
        smtp.login(from_mail,password)
        smtp.sendmail(from_mail,to_mail,msg.as_string())
    except(smtplib.SMTPException) as e:
        print(e.message)
    finally:
        smtp.quit()

def SendEmailVerificationCode(mail,code):
    smtpserver = "smtp.qq.com"
    smtpport = 465
    from_mail = "2787697211@qq.com"
    to_mail = [mail]
    password = "idcrnuzeitktddbb"   # 16位授权码
    subject = "【Movie Center】账户电子邮箱验证码"
    body = "【Movie Center】您的验证码是：" + str(code) + "，有效期5分钟。请不要把验证码泄露给其他人。如非本人操作，可不用理会！"
    from_name = "Movie Center"
    msg = MIMEMultipart()

    msgtext = MIMEText(body, "html", "utf-8")
    msg.attach(msgtext)

    msg["Subject"] = Header(subject, "utf-8")
    msg["From"] = Header(from_name + " <" + from_mail + ">", "utf-8")
    msg["To"] = Header(",".join(to_mail), "utf-8")

    try:
        smtp = smtplib.SMTP_SSL(smtpserver,smtpport)
        smtp.login(from_mail,password)
        smtp.sendmail(from_mail,to_mail,msg.as_string())
    except(smtplib.SMTPException) as e:
        pass
    finally:
        smtp.quit()


@app.route('/API-Login/',methods=['GET','POST'])
def API_login():
    form = LoginForm()
    if request.method == 'GET':
        return json.dumps({"code":1, "message":"Only support POST method"})
    else:
        if form.validate_on_submit():
            session['remember_me'] = form.remember_me.data
            data = form.data
            user = User.query.filter_by(telephone=data["telephone"]).first()
            if user == None:
                return json.dumps({"code":2, "message":"Account does not exist"})
            elif user != None and not user.check_password(data["password"]): 
                return json.dumps({"code":3, "message":"Wrong password"})
            elif user != None and user.check_password(data["password"]): 
                login_user(user, form.remember_me.data)
                session['is_login'] = 1
                return json.dumps({"code":0, "message":"Login successfully"})
        else:
            return json.dumps({"code":4, "message":"Please check your input again"})

@app.route('/API-Register/',methods=['GET','POST'])
def API_register():
    form = RegisterForm()
    if request.method == 'GET':
        return json.dumps({"code":1, "message":"Only support POST method"})
    else:
        if not form.validate_on_submit():
            return json.dumps({"code":2, "message":"Please check your input again"})
        elif not form.validate_name(form.telephone):
            return json.dumps({"code":3, "message":"The telephone already exist"})
        
        else:
            year = int(form.birthday.data.split("-")[0])
            if year > 2020:
                return json.dumps({"code":4, "message":"Birthday is wrong"})
            user = User(telephone=form.telephone.data, password=form.password1.data)
            user.birthyear = year
            db.session.add(user)
            db.session.commit()
            return json.dumps({"code":0, "message":"Register successfully"})

@app.route('/API-CurrentUser/',methods=['GET','POST'])
def API_currentUser():
    if (not session.get('is_login')) or session['is_login'] != 1:
        return "NEED_LOGIN"
    else:
        return json.dumps({
            "code":0, 
            "message":current_user.telephone, 
            "vip":current_user.vip, 
            "age":2020-current_user.birthyear,
            })
        

@app.route('/API-Logout/')
def API_logout():
    try:
        session['is_login'] = 0
        logout_user()
    except:
        pass
    return json.dumps({"code":0, "message":"Logout successfully"})

@app.route('/API-SendPhoneCode/<telephone>',methods=['GET'])
def API_sendPhoneCode(telephone):
    code = str(random.randint(100000,999999))
    session['telephone_code'] = code
    send_mess(telephone,code)
    return json.dumps({"code":0, "message":"OK"})

@app.route('/API-CheckPhoneCode/<code>',methods=['GET'])
def API_checkPhoneCode(code):
    try:
        if(code == session['telephone_code']):
            return json.dumps({"code":0, "message":"OK"})
        else:
            return json.dumps({"code":1, "message":"Failed"})
    except:
        return json.dumps({"code":2, "message":"Please send phone code first"})

@app.route('/API-AllMovies/',methods=['GET'])
def API_allMovies():
    all_movie = []
    for movie in Movie.query.order_by(Movie.id.desc()).all():
        all_movie.append(movie.to_dict())
    print(all_movie)
    return jsonify(data=all_movie)

@app.route('/API-FindSchedule/<movie_id>',methods=['GET'])
def API_findSchedule(movie_id):
    all_schedule = []
    for schedule in Schedule.query.filter(Schedule.movie_id == movie_id):
        all_schedule.append(schedule.to_dict())
    return jsonify(data=all_schedule)

@app.route('/API-FindOrderDetailByScheduleID/<schedule_id>',methods=['GET'])
def API_findOrderDetailByScheduleID(schedule_id):
    all_detail = []
    for detail in OrderDetail.query.filter(OrderDetail.schedule_id == schedule_id):
        if detail != None:
            all_detail.append(detail.to_dict())
    return jsonify(data=all_detail)


@app.route('/API-NewOrder/',methods=['GET','POST'])
def API_newOrder():
    form = OrderDetailForm()
    if request.method == 'GET':
        return json.dumps({"code":1, "message":"Only support POST method"})
    else:
        if (not session.get('is_login')) or session['is_login'] != 1:
            return json.dumps({"code":2, "message":"Please login first"})
        try:
            ticket_uuid = str(uuid.uuid1())
            order = OrderDetail(schedule_id=form.schedule_id.data,
                            user_id=current_user.id,
                            seat_row=form.seat_row.data,
                            seat_column=form.seat_column.data,
                            pay_method=form.pay_method.data,
                            ticket_uuid=ticket_uuid,
                            unique_identity=str(form.seat_row.data)+"_"+str(form.seat_column.data),
                            )
            schedule = Schedule.query.filter_by(id=form.schedule_id.data).first()
            if schedule.hall_info.is_vip:
                column = int(form.seat_column.data/2) + 1
            else:
                column = form.seat_column.data + 1
            position = str(form.seat_row.data + 1) + "排" + str(column) + "座";
            if schedule.start_time <= "12:00":
                start_time = schedule.start_time + " am"
            else:
                start_time = schedule.start_time + " pm"
                

            ticket_path = GenerateTicket(schedule.movie_info.name,schedule.hall_info.name,schedule.date.strftime('%Y-%m-%d'),start_time,position,get_current_price(schedule.price),ticket_uuid)
            db.session.add(order)
            db.session.commit()
            order = OrderDetail.query.filter_by(ticket_uuid=ticket_uuid).first()
            return json.dumps({"code":0, "message":"Create a new order successfully", "order_id":order.id})
        except Exception as e:
            #print(e)
            pass
            return json.dumps({"code":3, "message":"Failed to generate an new order"})


@app.route('/API-GetUserRecords/',methods=['GET','POST'])
def API_getUserRecords():
    if (not session.get('is_login')) or session['is_login'] != 1:
        return "NEED_LOGIN"
    all_record = []
    for record in OrderDetail.query.filter_by(user_id=current_user.id):
        all_record.append(record.record_to_dict())
    return jsonify(data=all_record)

@app.route('/API-GetUserTickets/',methods=['GET','POST'])
def API_getUserTickets():
    if (not session.get('is_login')) or session['is_login'] != 1:
        return "NEED_LOGIN"
    all_tickets = []
    for ticket in OrderDetail.query.filter_by(user_id=current_user.id).filter_by(paid=1):
        all_tickets.append(ticket.ticket_to_dict())
    return jsonify(data=all_tickets)

@app.route('/API-SearchMovie/<search_info>',methods=['GET'])
def API_SearchMovie(search_info):
    all_movie = []
    for movie in Movie.query.filter_by(name=search_info):
        all_movie.append(movie.to_dict())
    return jsonify(data=all_movie)

@app.route('/API-searchMovieByDate/<date>',methods=['GET'])
def API_SearchMovieByDate(date):
    all_movie = []
    all_movie_ids = []
    for schedule in Schedule.query.filter_by(date = date):
        all_movie_ids.append(int(schedule.movie_id))
    all_movie_ids = list(set(all_movie_ids))
    for id in all_movie_ids:
        movie = Movie.query.filter_by(id=id).first()
        all_movie.append(movie.to_dict())
    return jsonify(data=all_movie)

@app.route('/API-FindHallNameByScheduleID/<schedule_id>',methods=['GET'])
def API_findHallNameByScheduleID(schedule_id):
    schedule = Schedule.query.filter(Schedule.id == schedule_id).first()
    return schedule.hall_info.name

@app.route('/API-IsVIPHall/<schedule_id>',methods=['GET'])
def API_isVIPHall(schedule_id):
    schedule = Schedule.query.filter(Schedule.id == schedule_id).first()
    return str(schedule.hall_info.is_vip)


@app.route('/API-SendEmailCode/<email>',methods=['GET'])
@login_required
def API_sendEmailCode(email):
    code = str(random.randint(100000,999999))
    session['email_code'] = code
    session['email'] = email
    SendEmailVerificationCode(email,code)
    return json.dumps({"code":0, "message":"OK"})

@app.route('/API-CheckEmailCode/<code>',methods=['GET'])
@login_required
def API_checkEmailCode(code):
    try:
        if(code == session['email_code']):
            user = User.query.filter_by(telephone=current_user.telephone).first()
            user.email = session['email']
            db.session.commit()
            return json.dumps({"code":0, "message":"OK"})
        else:
            return json.dumps({"code":1, "message":"Failed"})
    except:
        return json.dumps({"code":2, "message":"Please send email code first"})


@app.route('/API-CurrentUserEmail/',methods=['GET','POST'])
def API_currentUserEmail():
    if (not session.get('is_login')) or session['is_login'] != 1:
        return "NEED_LOGIN"
    else:
        return json.dumps({"code":0, "message":current_user.email})
        
@app.route('/API-ResetEmail/',methods=['GET','POST'])
def API_resetEmail():
    if (not session.get('is_login')) or session['is_login'] != 1:
        return "NEED_LOGIN"
    else:
        user = User.query.filter_by(telephone=current_user.telephone).first()
        user.email = "NO_EMAIL"
        db.session.commit()
        return json.dumps({"code":0, "message":current_user.email})


@app.route('/API-GetRecordByID/<id>',methods=['GET','POST'])
def API_getRecordByID(id):
    if (not session.get('is_login')) or session['is_login'] != 1:
        return "NEED_LOGIN"
    record = OrderDetail.query.filter_by(user_id=current_user.id).filter_by(id=id).first()
    return jsonify(data=record.record_to_dict2())

@app.route('/API-PayOrder/<id>',methods=['GET','POST'])
def API_payOrder(id):
    if (not session.get('is_login')) or session['is_login'] != 1:
        return "NEED_LOGIN"
    record = OrderDetail.query.filter_by(user_id=current_user.id).filter_by(id=id).first()
    record.paid = 1
    record.pay_method = "card"
    ticket_path = "./application/static/tickets/" + record.ticket_uuid + ".png"
    SendEmailWithTicket(
            current_user.email,
            '【Movie Center】电影票','<h3>请保存好您的电影票!</h3><img src="cid:image1"/>',
            ticket_path
            )
    db.session.commit()
    return json.dumps({"code":0, "message":"Pay an order successfully"})


@app.route('/API-CurrentUserCard/',methods=['GET','POST'])
def API_currentUserCard():
    if (not session.get('is_login')) or session['is_login'] != 1:
        return "NEED_LOGIN"
    else:
        return json.dumps({"code":0, "message":current_user.card})

@app.route('/API-SetCard/<card>',methods=['GET','POST'])
def API_setCard(card):
    if (not session.get('is_login')) or session['is_login'] != 1:
        return "NEED_LOGIN"
    user = User.query.filter_by(telephone=current_user.telephone).first()
    user.card = card
    db.session.commit()
    return json.dumps({"code":0, "message":"Set user card successfully"})

@app.route('/API-ResetCard/',methods=['GET','POST'])
def API_resetCard():
    if (not session.get('is_login')) or session['is_login'] != 1:
        return "NEED_LOGIN"
    else:
        user = User.query.filter_by(telephone=current_user.telephone).first()
        user.card = "NO_CARD"
        db.session.commit()
        return json.dumps({"code":0, "message":current_user.card})

@app.route('/API-CurrentUserIsVIP/',methods=['GET','POST'])
def API_currentUserIsVIP():
    if (not session.get('is_login')) or session['is_login'] != 1:
        return "NEED_LOGIN"
    else:
        return json.dumps({"code":0, "message":current_user.vip})

@app.route('/API-SetUserVIP/',methods=['GET','POST'])
def API_setUserVIP():
    if (not session.get('is_login')) or session['is_login'] != 1:
        return "NEED_LOGIN"
    user = User.query.filter_by(telephone=current_user.telephone).first()
    user.vip = 1
    db.session.commit()
    return json.dumps({"code":0, "message":"Set user VIP successfully"})
