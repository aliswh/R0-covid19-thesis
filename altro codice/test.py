import pandas, datetime
import numpy as np


url_regioni = 'https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-regioni/dpc-covid19-ita-regioni.csv'
url_province = 'https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-province/dpc-covid19-ita-province-' + '20200224' + '.csv'

def getRegionNames():
    dflist = []
    df = pandas.read_csv(url_regioni)
    df = df.drop_duplicates(subset=['denominazione_regione'])
    print(df)
    dflist = df['denominazione_regione'].tolist()
    print(df)
    dflist = [x.replace(' ','') for x in dflist] # remove spaces
    dflist = [x.replace("'",'') for x in dflist] # remove apostrophes
    print(dflist)

def getProvinceNames():
    dflist = []
    df = pandas.read_csv(url_province)
    df = df.drop_duplicates(subset=['denominazione_provincia'])
    dflist = df['denominazione_provincia'].tolist()
    dflist = [x.replace(' ','') for x in dflist] # remove spaces
    dflist = [x.replace("'",'') for x in dflist] # remove apostrophes
    print(dflist)

getRegionNames()
#getProvinceNames()