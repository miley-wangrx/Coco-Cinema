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


def generateBar(data,names,max_val):
    data_num = len(data)
    plt.bar(np.arange(data_num), data, alpha=0.65, width=0.55, color='blue')
    plt.ylim(ymin=0,ymax=int(max_val))
    plt.title('Takings of Tickets Sold', fontsize=18)
    plt.xlabel('Name of movies', fontsize=12)
    plt.ylabel('Takings', fontsize=12)
    plt.xticks(np.arange(data_num), names)
    plt.savefig("./application/static/numTickets.png")



#function calls
f = open("temp/data.txt","r")
data = json.loads(f.read())
f.close()
f = open("temp/names.txt","r")
names = json.loads(f.read())
f.close()

generateBar(data,names,sys.argv[1])
print("Generate Graph Successfully!")