from .models import User
from flask_wtf import FlaskForm
from flask_wtf.file import FileField, FileAllowed, FileRequired
from wtforms import StringField,PasswordField,BooleanField,SubmitField,TextAreaField,IntegerField
from wtforms.validators import Regexp,EqualTo,Length,DataRequired,ValidationError


class SearchIdForm(FlaskForm):
    order_id = IntegerField("order_id", validators=[DataRequired()])

class SearchPhoneForm(FlaskForm):
    search_phone = StringField("search_phone", validators=[DataRequired()])

class StaticsGraphForm(FlaskForm):
    begin_date = StringField()
    end_date = StringField()
    submit = SubmitField(
        'Submit',
        render_kw={
            "class": "btn btn-primary"
        }
    )

class ScheduleForm(FlaskForm):
    movie_id = IntegerField()
    hall_id = IntegerField()
    date = StringField()
    start_time = StringField()
    end_time = StringField()
    price = StringField()
    submit = SubmitField(
        'Submit',
        render_kw={
            "class": "btn btn-primary"
        }
    )

class HallForm(FlaskForm):
    name = StringField()
    is_vip = IntegerField()
    submit = SubmitField(
        'Submit',
        render_kw={
            "class": "btn btn-primary"
        }
    )

class OrderDetailForm(FlaskForm):
    schedule_id = IntegerField()
    seat_row = IntegerField()
    seat_column = IntegerField()
    pay_method = StringField()



class EditMovieForm(FlaskForm):
    name = StringField(
        # 验证器
        validators=[
            DataRequired('Input movie name')
        ],
        # 附加选项,会自动在前端判别
        render_kw={
            "class": "form-control",
            "placeholder": "Input movie name",
            "required": 'required'             #表示输入框不能为空
        }
    )

    blurb = TextAreaField(
        # 验证器
        validators=[
            DataRequired('Input blurb')
        ],
        # 附加选项,会自动在前端判别
        render_kw={
            "class": "form-control",
            "placeholder": "Input blurb",
            "required": 'required'             #表示输入框不能为空
        }
    )

    certificate = StringField(
        # 验证器
        validators=[
            DataRequired('Input certificate')
        ],
        # 附加选项,会自动在前端判别
        render_kw={
            "class": "form-control",
            "placeholder": "Input certificate",
            "required": 'required'             #表示输入框不能为空
        }
    )

    director = StringField(
        # 验证器
        validators=[
            DataRequired('Input director')
        ],
        # 附加选项,会自动在前端判别
        render_kw={
            "class": "form-control",
            "placeholder": "Input director",
            "required": 'required'             #表示输入框不能为空
        }
    )

    actors = StringField(
        # 验证器
        validators=[
            DataRequired('Input actors')
        ],
        # 附加选项,会自动在前端判别
        render_kw={
            "class": "form-control",
            "placeholder": "Input actors",
            "required": 'required'             #表示输入框不能为空
        }
    )

    submit = SubmitField(
        'Submit',
        render_kw={
            "class": "btn btn-primary"
        }
    )


class RegisterForm(FlaskForm):
    telephone = StringField(
        label="telephone",
        # validation
        validators=[
            DataRequired('Please input telephone number'),
            Regexp("1[3578]\d{9}", message="The format for telephone number is wrong")  # use regualer expression to match the telephone
        ],
        description="telephone",
        # identify automatically on front end
        render_kw={
            "class": "form-control",
            "placeholder": "Input telephone",
             "required": 'required'    # the input cannot be empty
        }
    )
    birthday = StringField()
    password1 = PasswordField(
        # 验证器
        validators=[
            DataRequired('Please input the password'),
        ],
        # 附加选项,会自动在前端判别
        render_kw={
            "class": "form-control",
            "placeholder": "Input your password",
            "required": 'required'  # 表示输入框不能为空
        }
    )
    password2 = PasswordField(
        # 验证器
        validators=[
            DataRequired('Please confirm password'),
            EqualTo('password1',message="Two passwords are not same")           #判断两次输入的密码是否一致
        ],
        # 附加选项,会自动在前端判别
        render_kw={
            "class": "form-control",
            "placeholder": "Confirm the password",
            "required": 'required'  # 表示输入框不能为空
        }
    )
    submit = SubmitField(
        render_kw={
            "class": "btn btn-primary btn-block",
        }
    )

    # 账号认证，自定义验证器，判断输入的值是否唯一
    def validate_name(self, filed):
        telephone = filed.data
        telephone = User.query.filter_by(telephone=telephone).count()
        if telephone == 1:
            return 0
        else:
            return 1


class LoginForm(FlaskForm):
    telephone = StringField(
        label="telephone",
        # validation
        validators=[
            DataRequired('Please input telephone number'),
            Regexp("1[3578]\d{9}", message="The format for telephone number is wrong")  # use regualer expression to match the telephone
        ],
        description="telephone",
        # identify automatically on front end
        render_kw={
            "class": "form-control",
            "placeholder": "Input telephone",
             "required": 'required'    # the input cannot be empty
        }
    )

    password = PasswordField(
        # 验证器
        validators=[
            DataRequired('Please input password')
        ],

        # 附加选项(主要是前端样式),会自动在前端判别
        render_kw={
            "class": "form-control",
            "placeholder": "Input password",
            "required": 'required'     # 表示输入框不能为空
        }
    )

    remember_me = BooleanField(
        'remember_me',
        default='checked',
    )

    submit = SubmitField(
        render_kw={
            "class": "btn btn-primary btn-block btn-flat",
        }
    )

class PasswordResetForm(FlaskForm):
    oldpassword = PasswordField(
        # 验证器
        validators=[
            DataRequired('Please input old password')
        ],

        # 附加选项(主要是前端样式),会自动在前端判别
        render_kw={
            "class": "form-control",
            "placeholder": "Input old password",
            "required": 'required'  # 表示输入框不能为空
        }
    )

    password1 = PasswordField(
        # 验证器
        validators=[
            DataRequired('Please input new password'),
            Length(1, 20, message="Your password should be in 1-20 characters")
        ],

        # 附加选项(主要是前端样式),会自动在前端判别
        render_kw={
            "class": "form-control",
            "placeholder": "Input new password",
            "required": 'required'     # 表示输入框不能为空
        }
    )

    password2 = PasswordField(
        # 验证器
        validators=[
            DataRequired('Please confirm the password'),
            EqualTo('password1', message="Two passwords are not same")  # 判断两次输入的密码是否一致

        ],

        # 附加选项(主要是前端样式),会自动在前端判别
        render_kw={
            "class": "form-control",
            "placeholder": "Confirm new password",
            "required": 'required'  # 表示输入框不能为空
        }
    )

    submit = SubmitField(
        render_kw={
            "class": "btn btn-primary btn-block",
        }
    )
