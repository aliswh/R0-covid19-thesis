import pandas, datetime
import numpy as np

#data
url_province = ''
np.a = []
np.csv = []

# time

# number of days from today to the day before the first .csv uploaded
today = datetime.date.today() 
n = (today - datetime.date(2020, 2, 22)).days

def getDate(count):
    date = today - datetime.timedelta(days=count)
    date = date.strftime('%Y%m%d')
    return date

#   province

def getProvinceNames():
    df = pandas.read_csv('https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-province/dpc-covid19-ita-province-' + '20200224' + '.csv')
    df = df.drop_duplicates(subset=['denominazione_provincia'])
    return df['denominazione_provincia'].to_numpy()

allProvince = getProvinceNames()

# TODO
# if it is before 18.00, c is 1, if it's after 18.00 it is 0
# because only after 18.00 you have the data of the current today
c = 1
for i in range(c, n-1):
    print(i, getDate(i))
    np.a.append(getDate(i))

for date in np.a:
    url_province = 'https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-province/dpc-covid19-ita-province-' + date + '.csv'
    df = pandas.read_csv(url_province, index_col='denominazione_provincia', usecols=['denominazione_provincia', 'totale_casi'])
    #print(df)
    np.csv.append(df)

print(np.csv)

#for prov in allProvince:
#    x = np.csv.loc[prov].iat[0]
#    print(x)

