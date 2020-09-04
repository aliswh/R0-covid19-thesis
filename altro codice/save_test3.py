import pandas, datetime
import numpy as np

t1_start = datetime.datetime.now()

#data
url_province = ''
np.date = []

# main dataframe
url_province = 'https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-province/dpc-covid19-ita-province-' + '20200224' + '.csv'
dfAll = pandas.read_csv(url_province, index_col=['denominazione_provincia'], usecols=['denominazione_provincia'])
# clear data
dfAll = dfAll.drop('In fase di definizione/aggiornamento')

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
    #df = df.drop('In fase di definizione/aggiornamento', axis='rows')
    
    #df = df.drop(df.loc[df.index=='Fuori Regione / Provincia Autonoma'].index)
    return df['denominazione_provincia'].to_numpy()

allProvince = getProvinceNames()
allProvince = np.delete(allProvince, 4) # 4 is the index of 'In fase di definizione/aggiornamento'
print(allProvince)

# TODO
# if it is before 18.00, c is 1, if it's after 18.00 it is 0
# because only after 18.00 you have the data of the current today
c = 1
print('Number of days: ' + str(n))
for i in range(c, n-1): #c, n-1
    #print(i, getDate(i))
    np.date.append(getDate(i))
np.date = np.date[::-1]

for date in np.date:
    url_province = 'https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-province/dpc-covid19-ita-province-' + date + '.csv'
    df = pandas.read_csv(url_province, index_col=['denominazione_provincia'], usecols=['denominazione_provincia','totale_casi'])
    # rename 'totale_casi' column as the date of the same url
    df = df.rename(columns={'totale_casi': str(date)})
    
    # clear data - keep only provinces names
    df = df.drop('In fase di definizione/aggiornamento', axis='rows')
    df = df.drop(df.loc[df.index=='Fuori Regione / Provincia Autonoma'].index)
    #print(df)
    
    #add new column to the definitive dataframe
    dfAll = dfAll.join(df, on='denominazione_provincia')

print(dfAll)

def getArray_Province(prov):
    x = dfAll.loc[prov]
    x = x.to_numpy()
    return x

def makeFile(x, region):
    
    count = x.size #get number of rows of 
    data = ''   #empty string - will be the data for the .R file

    for i in range(0, count):   #get data from x
        data = data + str(x[i]) + ','

    data = data[:-1]
    data = '#CONTAGI GIORNALIERI DATO PROVINCIA: ' + region +'\n' + region + '.2020 = c(' + data + ')'

    # to get the first and last date, create a dataframe 'date_df' with only the first and last entry of 'df'
    #date_df = df.iloc[[0, -1]]
    #get temporal interval from CVS file
    #startDate = date_df["data"][0]
    #startDate = startDate[: -9]
    #modify this line and delete'#' if you want a specific start date
    startDate = '2020-02-24'

    #lastDate = date_df["data"][1]
    #lastDate = lastDate[: -9]
    #modify this line and delete'#' if you want a specific last date
    lastDate = '2020-08-01'

    data = data + '\nnames(' + region + '.2020)<-seq(from=as.Date("' + startDate + '"), to=as.Date("' + lastDate + '"), by=1)'

    #create new .R file
    filename = region + ".2020.R"
    f = open(filename, "w+")
    f.write(data)
    f.close()

    #testing data content
    print(data)
    return

for prov in allProvince:
    x = getArray_Province(prov)
    makeFile(x, prov)

t1_stop = datetime.datetime.now()  
print("Elapsed time during the whole program in seconds:", t1_stop-t1_start) 