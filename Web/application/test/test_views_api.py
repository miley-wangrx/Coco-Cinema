import pytest
from application import views_API
import uuid
import requests

""" 
Test: Send messages
views_API.send_mess(phone,code)
Parameter description： 
    phone:  telephone number example：13258181008   
    code:  validation code   example：1234
Test results：
    Success：
     [100%]b'{"Message":"OK","RequestId":"6241DD15-C469-43BF-A2C7-04
     26E9C2739E","BizId":"250911186225795924^0","Co
     de":"OK"}' 
"""


def test_send_message():
    reponse = views_API.send_mess("13258181008", "1234")
    print(reponse)


"""
Test: Generate QRCode
views_API.GenerateQRCode(text)
Parameter description：
    text：QRCode example: http://www.baidu.com
Test results:
            # [100%]D:/QQ/867138760/FileRecv/project/application/static/qrcode/3878cbb7-878e-11ea-ac31-38002599d774.png
"""


def test_GenerateQRCode():
    path = views_API.GenerateQRCode("http://www.baidu.com")
    print(path)


"""
Test: Generate tickets
views_API.GenerateTicket(movie,hall,date,time,position,price,ticket_uuid)
Parameter description：
    movie: The movie customers purchased example: 唐人街探案
    hall:  The hall the movie will be played in example: 3号厅
    date:  The date the movie will be played in example: 2020.4.7
    time:  The time the movie will be played in example: 九点
    position:  The position customers chose example: 4排二号
    price:  The price the tickets cost example: 45
    ticket_uuid 
Test results:
    test_views_api.py::test_GenerateTicket PASSED                            [100%]55b34336-878e-11ea-a159-38002599d774
    D:/QQ/867138760/FileRecv/project/application/static/tickets/55b34336-878e-11ea-a159-38002599d774.png

"""


def test_GenerateTicket():
    path = views_API.GenerateTicket("唐人街探案", '3号厅', '2020.4.7', '九点', '4排二号', '45', str(uuid.uuid1()))
    print(path)


"""
Test: Send tickets by email
views_API.SendEmailWithTicket(mail,subject,body,ticket_path)
Parameter description：
     mail: Email addresses for customers who purchased tickets in the app
     subject： Subject of the email
     body：Content
     ticket_path：The path of tickets generated
Test results:
 test_views_api.py::test_SendEmailWithTicket PASSED                       [100%]
"""
def test_SendEmailWithTicket():
   views_API.SendEmailWithTicket("867138760@qq.com",'Test','Send the voucher of the ticket you purchased ','D:/QQ/867138760/FileRecv/project/application/static/tickets/0532fc0a-8796-11ea-954f-38002599d774.png')


"""
Test: Send validation code by email
    views_API.SendEmailVerificationCode(mail,code):
Parameter description：
    mail：Email addresses for customers who are currently registering into the app
    code：Validation code
Test results:
    test_views_api.py::test_SendEmailVerificationCode PASSED                 [100%]
"""
def test_SendEmailVerificationCode():
    views_API.SendEmailVerificationCode("867138760@qq.com",'123456')


if __name__ == "__main__":
    pytest.main(['test_views_api.py', '-s'])
