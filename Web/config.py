import os


WTF_CSRF_ENABLED = False # unit test 时设置为 False
SECRET_KEY = os.urandom(24)

basedir = os.path.abspath(os.path.dirname(__file__))

SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(basedir, 'app.db')
SQLALCHEMY_MIGRATE_REPO = os.path.join(basedir, 'db_repository')
SQLALCHEMY_TRACK_MODIFICATIONS = True

UPLOADED_PHOTOS_DEST = os.path.abspath(os.path.join(os.getcwd(),"app/static/images"))
BLOG_PER_PAGE = 8


DEBUG = True