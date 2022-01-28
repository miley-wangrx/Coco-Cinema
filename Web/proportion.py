#coding:utf-8
import pandas as pd
import numpy as np
import matplotlib as mpl
import matplotlib.pyplot as plt
from matplotlib.pylab import mpl
import sys
import json

# Display Chinese characters
mpl.rcParams['font.sans-serif']=['SimHei']
plt.rcParams['axes.unicode_minus'] = False


def proportion(num,labels):
    plt.rcParams['font.family'] = 'SimHei'
    explode = [0.03] * len(labels)  #凸出距离
    colors = ['lightsalmon', 'lightskyblue', 'mediumaquamarine', 'lightcoral', 'steelblue','thistle']
    plt.figure(figsize=(8, 6))
    plt.pie(num, explode=explode, labels=labels, colors=colors, \
            autopct='%1.1f%%', shadow=False, pctdistance=0.8, \
            startangle=90, textprops={'fontsize': 16, 'color': 'w'})
    plt.title('Proportional Number of Tickets Sold',fontsize=20)
    plt.axis('equal')
    plt.legend(loc='upper right')
    plt.savefig('./application/static/proportion.png', dpi=600)

#function calls
f = open("temp/data2.txt","r")
data = json.loads(f.read())
f.close()
f = open("temp/names2.txt","r")
names = json.loads(f.read())
f.close()

proportion(data,names)
print("Generate Graph Successfully!")

