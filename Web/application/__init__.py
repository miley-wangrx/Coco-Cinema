from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_uploads import UploadSet, configure_uploads, IMAGES, patch_request_class
from flask_moment import Moment
import logging

app = Flask(__name__)
app.config.from_object('config')
db=SQLAlchemy(app)

moment = Moment(app)

from . import views_admin,views_API, models