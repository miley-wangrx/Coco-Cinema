import pytest
from selenium import webdriver
from selenium.webdriver.support import expected_conditions as EC
import time

def test_login01():
    driver = webdriver.Chrome()
    driver.get('http://127.0.0.1:5000')
    dig_confirm = driver.switch_to.alert
    dig_confirm.accept()
    driver.find_element_by_id('x1').clear()
    # 输入用户名
    driver.find_element_by_id('x1').send_keys('13086618316')
    # 清空密码输入
    driver.find_element_by_id('pwd').clear()
    # 输入密码
    driver.find_element_by_id('pwd').send_keys('123')
    driver.find_element_by_id('x2').click()
    a = "http://127.0.0.1:5000/admin_index/"
    b = driver.current_url
    assert a == b
    driver.quit()

def test_login02():
    driver = webdriver.Chrome()
    driver.get('http://127.0.0.1:5000')
    dig_confirm = driver.switch_to.alert
    dig_confirm.accept()
    driver.find_element_by_id('x1').clear()
    # 清空密码输入
    driver.find_element_by_id('pwd').clear()
    # 输入密码
    driver.find_element_by_id('pwd').send_keys('123')
    driver.find_element_by_id('x2').click()
    result = EC.alert_is_present()(driver)
    assert result != False
    driver.quit()

def test_login03():
    driver = webdriver.Chrome()
    driver.get('http://127.0.0.1:5000')
    dig_confirm = driver.switch_to.alert
    dig_confirm.accept()
    driver.find_element_by_id('x1').clear()
    # 输入用户名
    driver.find_element_by_id('x1').send_keys('13086618316')
    # 清空密码输入
    driver.find_element_by_id('pwd').clear()
    # 输入密码
    driver.find_element_by_id('x2').click()
    result = EC.alert_is_present()(driver)
    assert result != False
    driver.quit()

def test_list_all_movie():
    driver = webdriver.Chrome()
    driver.get('http://127.0.0.1:5000')
    dig_confirm = driver.switch_to.alert
    dig_confirm.accept()
    driver.find_element_by_id('x1').clear()
    # 输入用户名
    driver.find_element_by_id('x1').send_keys('13086618316')
    # 清空密码输入
    driver.find_element_by_id('pwd').clear()
    # 输入密码
    driver.find_element_by_id('pwd').send_keys('123')
    driver.find_element_by_id('x2').click()
    driver.get('http://127.0.0.1:5000/admin_list_all_movies/')
    a = "http://127.0.0.1:5000/admin_list_all_movies/"
    b = driver.current_url
    assert a == b
    driver.quit()

def test_add_a_movie01():
    driver = webdriver.Chrome()
    driver.get('http://127.0.0.1:5000')
    dig_confirm = driver.switch_to.alert
    dig_confirm.accept()
    driver.find_element_by_id('x1').clear()
    driver.find_element_by_id('x1').send_keys('13086618316')
    driver.find_element_by_id('pwd').clear()
    driver.find_element_by_id('pwd').send_keys('123')
    driver.find_element_by_id('x2').click()
    driver.get("http://127.0.0.1:5000/admin_add_new_movie/")
    driver.find_element_by_id('name').send_keys('testName')
    driver.find_element_by_id('submit').click()
    a = "http://127.0.0.1:5000/admin_add_new_movie/"
    b = driver.current_url
    assert a == b
    driver.find_element_by_id('blurb').send_keys('testBlurb')
    driver.find_element_by_id('submit').click()
    b = driver.current_url
    assert a == b
    driver.find_element_by_id('certificate').send_keys('testCertificate')
    driver.find_element_by_id('submit').click()
    b = driver.current_url
    assert a == b
    driver.find_element_by_id('director').send_keys('testDirector')
    driver.find_element_by_id('submit').click()
    b = driver.current_url
    assert a == b
    driver.find_element_by_id('actors').send_keys('testActors')
    driver.find_element_by_id('submit').click()
    b = driver.current_url
    assert a == b
    driver.find_element_by_name('picture').send_keys(r'C:\Users\39285\Desktop\testPicture.jpg')
    driver.find_element_by_name('poster').send_keys(r'C:\Users\39285\Desktop\testPicture.jpg')
    driver.find_element_by_id('name').clear()
    driver.find_element_by_id('submit').click()
    assert a == b
    driver.quit()

def test_add_a_movie02():
    driver = webdriver.Chrome()
    driver.get('http://127.0.0.1:5000')
    dig_confirm = driver.switch_to.alert
    dig_confirm.accept()
    driver.find_element_by_id('x1').clear()
    driver.find_element_by_id('x1').send_keys('13086618316')
    driver.find_element_by_id('pwd').clear()
    driver.find_element_by_id('pwd').send_keys('123')
    driver.find_element_by_id('x2').click()
    driver.get("http://127.0.0.1:5000/admin_add_new_movie/")
    driver.find_element_by_id('name').send_keys('testName')
    driver.find_element_by_id('blurb').send_keys('testBlurb')
    driver.find_element_by_id('certificate').send_keys('testCertificate')
    driver.find_element_by_id('director').send_keys('testDirector')
    driver.find_element_by_id('actors').send_keys('testActors')
    driver.find_element_by_name('picture').send_keys(r'C:\Users\39285\Desktop\testPicture.jpg')
    driver.find_element_by_name('poster').send_keys(r'C:\Users\39285\Desktop\testPicture.jpg')
    driver.find_element_by_id('submit').click()
    a = "http://127.0.0.1:5000/admin_index/"
    b = driver.current_url
    assert a == b
    driver.quit()

def test_delete_a_movie():
    driver = webdriver.Chrome()
    driver.get('http://127.0.0.1:5000')
    dig_confirm = driver.switch_to.alert
    dig_confirm.accept()
    driver.find_element_by_id('x1').clear()
    driver.find_element_by_id('x1').send_keys('13086618316')
    driver.find_element_by_id('pwd').clear()
    driver.find_element_by_id('pwd').send_keys('123')
    driver.find_element_by_id('x2').click()
    driver.get("http://127.0.0.1:5000/admin_list_all_movies/")
    hyperlinks_list = driver.find_element_by_class_name("table")
    # get images from the banner_list
    hyperlinks = hyperlinks_list.find_elements_by_tag_name("a")
    i = len(hyperlinks)
    hyperlinks[i-1].click()
    hyperlinks_list0 = driver.find_element_by_class_name("table")
    hyperlinks0 = hyperlinks_list0.find_elements_by_tag_name("a")
    assert len(hyperlinks) == len(hyperlinks0) + 1
    driver.quit()

def test_add_a_hall01():
    driver = webdriver.Chrome()
    driver.get(r'http://127.0.0.1:5000')
    dig_confirm = driver.switch_to.alert
    dig_confirm.accept()
    driver.find_element_by_id('x1').clear()
    driver.find_element_by_id('x1').send_keys('13086618316')
    driver.find_element_by_id('pwd').clear()
    driver.find_element_by_id('pwd').send_keys('123')
    driver.find_element_by_id('x2').click()
    driver.get(r"http://127.0.0.1:5000/admin_add_new_hall/")
    driver.find_element_by_id('submit').click()
    a = "http://127.0.0.1:5000/admin_add_new_hall/"
    b = driver.current_url
    assert a == b
    driver.find_element_by_id('name').send_keys('testName')
    driver.find_element_by_id('submit').click()
    assert a == b
    driver.find_element_by_id('is_vip').send_keys('1')
    driver.find_element_by_id('name').clear()
    driver.find_element_by_id('submit').click()
    b = driver.current_url
    assert a == b
    driver.quit()

def test_add_a_hall02():
    driver = webdriver.Chrome()
    driver.get(r'http://127.0.0.1:5000')
    dig_confirm = driver.switch_to.alert
    dig_confirm.accept()
    driver.find_element_by_id('x1').clear()
    driver.find_element_by_id('x1').send_keys('13086618316')
    driver.find_element_by_id('pwd').clear()
    driver.find_element_by_id('pwd').send_keys('123')
    driver.find_element_by_id('x2').click()
    driver.get(r"http://127.0.0.1:5000/admin_add_new_hall/")
    driver.find_element_by_id('submit').click()
    a = "http://127.0.0.1:5000/admin_index/"
    b = driver.current_url
    driver.find_element_by_id('name').send_keys('testName')
    driver.find_element_by_id('is_vip').send_keys('1')
    driver.find_element_by_id('submit').click()
    b = driver.current_url
    assert a == b
    driver.quit()

def test_add_a_schedule01():
    driver = webdriver.Chrome()
    driver.get('http://127.0.0.1:5000')
    dig_confirm = driver.switch_to.alert
    dig_confirm.accept()
    driver.find_element_by_id('x1').clear()
    driver.find_element_by_id('x1').send_keys('13086618316')
    driver.find_element_by_id('pwd').clear()
    driver.find_element_by_id('pwd').send_keys('123')
    driver.find_element_by_id('x2').click()
    driver.get("http://127.0.0.1:5000/admin_add_new_schedule/")
    driver.find_element_by_id('submit').click()
    a = "http://127.0.0.1:5000/admin_add_new_schedule/"
    b = driver.current_url
    assert a == b
    driver.quit()

def test_add_a_schedule02():
    driver = webdriver.Chrome()
    driver.get('http://127.0.0.1:5000')
    dig_confirm = driver.switch_to.alert
    dig_confirm.accept()
    driver.find_element_by_id('x1').clear()
    driver.find_element_by_id('x1').send_keys('13086618316')
    driver.find_element_by_id('pwd').clear()
    driver.find_element_by_id('pwd').send_keys('123')
    driver.find_element_by_id('x2').click()
    driver.get("http://127.0.0.1:5000/admin_add_new_schedule/")
    a = "http://127.0.0.1:5000/admin_index/"
    driver.find_element_by_id('date').send_keys('1.1.2020')
    driver.find_element_by_id('start_time').send_keys('8:00')
    driver.find_element_by_id('end_time').send_keys('22:00')
    driver.find_element_by_id('price').send_keys('20')
    b = driver.current_url
    assert a == b
    driver.quit()

def test_pie_graph():
    driver = webdriver.Chrome()
    driver.get('http://127.0.0.1:5000')
    dig_confirm = driver.switch_to.alert
    dig_confirm.accept()
    driver.find_element_by_id('x1').clear()
    driver.find_element_by_id('x1').send_keys('13086618316')
    driver.find_element_by_id('pwd').clear()
    driver.find_element_by_id('pwd').send_keys('123')
    driver.find_element_by_id('x2').click()
    driver.get("http://127.0.0.1:5000/admin_pie_graph/")
    a = "http://127.0.0.1:5000/admin_pie_graph/"
    b = driver.current_url
    assert a == b
    driver.quit()

def test_list_all_user():
    driver = webdriver.Chrome()
    driver.get('http://127.0.0.1:5000')
    dig_confirm = driver.switch_to.alert
    dig_confirm.accept()
    driver.find_element_by_id('x1').clear()
    driver.find_element_by_id('x1').send_keys('13086618316')
    driver.find_element_by_id('pwd').clear()
    driver.find_element_by_id('pwd').send_keys('123')
    driver.find_element_by_id('x2').click()
    driver.get("http://127.0.0.1:5000/list_all_user")
    a = "http://127.0.0.1:5000/list_all_user"
    b = driver.current_url
    assert a == b
    driver.quit()

def test_logout():
    driver = webdriver.Chrome()
    driver.get('http://127.0.0.1:5000')
    dig_confirm = driver.switch_to.alert
    dig_confirm.accept()
    driver.find_element_by_id('x1').clear()
    driver.find_element_by_id('x1').send_keys('13086618316')
    driver.find_element_by_id('pwd').clear()
    driver.find_element_by_id('pwd').send_keys('123')
    driver.find_element_by_id('x2').click()
    driver.find_element_by_link_text("Logout").click()
    result = EC.alert_is_present()(driver)
    assert result != False
    driver.quit()

