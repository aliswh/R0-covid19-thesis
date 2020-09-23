# Thesis project developed by Alice Schiavone, student at Università degli Studi dell'Insubria (July-Sept.2020)
# Thesis advisor: Prof. Davide Tosi

# Data is taken from the Department of Italian Civil Protection for key transmission parameters of an institutional
# outbreak during the 2020 SARS-Cov2 pandemic in Italy

import datetime
import os
import pandas
import pathlib
import time

# elapsed time check
t1_start = datetime.datetime.now()

# URLs 
url_nazionale = 'https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-andamento-nazionale/dpc-covid19-ita-andamento-nazionale.csv'
url_regioni = 'https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-regioni/dpc-covid19-ita-regioni.csv'
url_province = 'https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-province/dpc-covid19-ita-province.csv'


# method to make lists of the names of regions and provinces
def getNames(url, denominazione):
    dflist = []
    df = pandas.read_csv(url)
    df = df.drop_duplicates(subset=[denominazione])
    dflist = df[denominazione].tolist()
    dflist = [x.replace(' ', '') for x in dflist]  # remove spaces
    dflist = [x.replace("'", '') for x in dflist]  # remove apostrophes
    dflist = [x.replace("-", '') for x in dflist]  # remove dashes
    dflist = [x.replace('ì', 'i') for x in dflist]  # remove i accent
    return dflist


# variables
data = ''  # string which stores the number of cases per day - ascending chronological order - to create .R files
arrayDaily = []  # matrix that holds data of provinces new cases
allRegion = getNames(url_regioni, 'denominazione_regione')
allProvince = getNames(url_province, 'denominazione_provincia')
del allProvince[4]  # clean (4 is the index of 'In fase di definizione/aggiornamento')
del allProvince[len(allProvince) - 1]  # clean (last item is 'Fuori Regione/Provincia Autonoma')

# data frame which will have index = zone names, columns = dates, cell = number of new cases per day for each zone
# if is filled by three different functions that retrieve and elaborated data based on the area (national, regional, provincial)
main_df = pandas.DataFrame()

# time related variables
today = datetime.date.today()  # get current day
currentTime = time.localtime().tm_hour  # get current hour
n = (today - datetime.date(2020, 2, 22)).days  # number of days from today to the day before the first .csv uploaded
startDate = ''  # first day in the national .cvs file
lastDate = ''  # last day in the national .cvs file
dates_vector = []  # list of dates


def getDate(count):
    date = today - datetime.timedelta(days=count)
    date = date.strftime('%Y%m%d')  # YYYYMMDD
    return date


# Which day to consider as last day based on current time
# Department of Italian Civil Protection uploads new data everyday at 18:00
# if it is before 18:00, 'c' start at 0, else 'c' start at 1

if currentTime > 18:
    c = 0
else:
    c = 1

# get formatted dates from February 24 to current day
for i in range(c, n - 1):
    dates_vector.append(getDate(i))
dates_vector = dates_vector[::-1]  # reverse array

# creates .R files with daily new cases of each area to be sourced if needed
# the directory is in the user's home directory and called '_R0(t)data'
count_makeFile = 1


def makeFile(data, zone):
    global count_makeFile
    if zone in allProvince:
        data = '#[' + str(
            count_makeFile) + '/129]' + 'CONTAGI GIORNALIERI DATO PROVINCIA: ' + zone + '\n' + zone + '.2020 = c(' + data + ')'
    elif zone in allRegion:
        data = '#[' + str(
            count_makeFile) + '/129]' + 'CONTAGI GIORNALIERI DATO REGIONE: ' + zone + '\n' + zone + '.2020 = c(' + data + ')'
    else:
        data = '#[' + str(
            count_makeFile) + '/129]' + 'CONTAGI GIORNALIERI DATO NAZIONALE\n' + zone + '.2020 = c(' + data + ')'

    data = data + '\nnames(' + zone + '.2020)<-seq(from=as.Date("' + startDate + '"), to=as.Date("' + lastDate + '"), by=1)'
    filename = zone + ".2020.R"
    f = open(filename, "w+")
    f.write(data)
    f.close()
    count_makeFile += 1
    # print(data) # check on file


# clear dataframe data
def clear_df(df):
    df.index = df.index.str.replace(' ', '')  # remove spaces
    df.index = df.index.str.replace("'", '')  # remove apostrophes
    df.index = df.index.str.replace("-", '')  # remove dashes
    df.index = df.index.str.replace('ì', 'i')  # remove i accent
    # delete 'Infasedidefinizione/aggiornamento' entry
    if df.index.str.contains('fase').any():
        df = df[~df.index.str.contains("fase")]
    # delete 'FuoriRegione/ProvinciaAutonoma' entry
    if df.index.str.contains('Fuori').any():
        df = df[~df.index.str.contains("Fuori")]
    return df


# Three different functions based on the nation administrative division

#  _   _       _   _             
# | \ | | __ _| |_(_) ___  _ __  
# |  \| |/ _` | __| |/ _ \| '_ \ 
# | |\  | (_| | |_| | (_) | | | |
# |_| \_|\__,_|\__|_|\___/|_| |_|

national_df = pandas.read_csv(url_nazionale, index_col=[], usecols=['data', 'nuovi_positivi'])

# national data doesn't need to be manipulated
national_col_new_positives = national_df['nuovi_positivi']
main_df = main_df.append(national_col_new_positives)  # append main_df first row


def nazioneDaily():
    # create .R files with the numbers of daily new cases
    data = ''
    count = -1  # counter to get lastDate
    for i in national_col_new_positives:  # get new positives data
        if i > 0:
            data = data + str(i) + ','
            count += 1
        else:
            data = data + "0" + ','
            count += 1
    data = data[:-1]  # delete last ','

    # get temporal interval from .csv file - this will be done by the national function but will be used by every file
    global startDate
    startDate = national_df["data"][0]
    startDate = startDate[: -9]  # clean value (delete hour)
    global lastDate
    lastDate = national_df["data"][count]
    lastDate = lastDate[: -9]  # clean value (delete hour)

    makeFile(data, 'Italy')


#  ____            _
# |  _ \ ___  __ _(_) ___  _ __  ___ 
# | |_) / _ \/ _` | |/ _ \| '_ \/ __|
# |  _ <  __/ (_| | | (_) | | | \__ \
# |_| \_\___|\__, |_|\___/|_| |_|___/
#            |___/                   

# get regional data 
r_df = pandas.read_csv(url_regioni, index_col=['denominazione_regione'],
                       usecols=['nuovi_positivi', 'denominazione_regione'])
r_df = clear_df(r_df)


def regioneDaily(region):
    global r_df
    df = r_df.loc[region, 'nuovi_positivi']  # r_df = regional dataframe
    count = len(df.index)  # number of rows

    temp_r_df = r_df.transpose()
    temp_r_df = temp_r_df.filter(like=region)  # get only columns with name=region
    temp_r_df.columns = range(temp_r_df.shape[1])  # reset index as integers

    # df.iat[i] = new positives, if the first number is less than 3, set it at 3
    if df.iat[0] < 3:
        datar = '3,'
        temp_r_df.iat[0, 0] = 3
    else:
        datar = str(df.iat[0]) + ','

    counter = 0
    # starts at 1 because first value df.iat[0] needs to be '3'
    for i in range(1, count):
        if df.iat[i] > 0:
            datar = datar + str(df.iat[i]) + ','
        else:
            datar = datar + "0" + ','
            if counter <= 10:  # set the first 10 numbers at 1 if <=0
                temp_r_df.iat[0, i] = 1  # TODO = 0
                counter += 1
            else:
                temp_r_df.iat[0, i] = 0
    datar = datar[:-1]  # delete last ','

    makeFile(datar, region)

    global main_df
    main_df = main_df.append(temp_r_df, ignore_index=True)  # add new row with region data


#  ____                 _
# |  _ \ _ __ _____   _(_)_ __   ___ ___  ___ 
# | |_) | '__/ _ \ \ / / | '_ \ / __/ _ \/ __|
# |  __/| | | (_) \ V /| | | | | (_|  __/\__ \
# |_|   |_|  \___/ \_/ |_|_| |_|\___\___||___/

# main province dataframe
p_df = pandas.read_csv(url_province, index_col=['denominazione_provincia'],
                       usecols=['denominazione_provincia', 'totale_casi'])
p_df = clear_df(p_df)
p_df = p_df.transpose()  # switch rows/columns
result_df = pandas.DataFrame()  # data frame to append new rows with province data

for p in allProvince:
    temp_p_df = p_df.filter(like=p)  # get only columns with name=region
    temp_p_df = temp_p_df.rename(index={'totale_casi': p})  # rename row index as province
    temp_p_df.columns = range(temp_p_df.shape[1])  # reset index as integers
    result_df = result_df.append(temp_p_df)  # add new row with province data
p_df = clear_df(result_df)  # clear data - keep only provinces names


# provinces .csv files don't have a daily cases column,
# to return a list with these values, this function makes this subtraction:
# 1) total cases _minus_ 2) cases of yesterday
# _minus_
# 3) cases of yesterday _minus_ total cases of the day before yesterday

def totalToDaily(prov):
    sub = 0  # 3) cases of yesterday _minus_ total cases of the day before yesterday - starts at 0
    returnList = []
    for d in range(0, len(dates_vector)):
        dif = p_df.loc[prov, d]  # 1) total cases
        fullsub = dif - sub  # 2) total cases _minus_ cases of yesterday
        if fullsub < 0:  # if it is below zero, set it at 0
            fullsub = 0
        returnList.append(fullsub)
        sub = dif  # sets new sub of the day for the next day
    return returnList


def provinciaDaily(prov):
    daily = totalToDaily(prov)

    if daily[0] < 3:
        datap = '3,'
        daily.insert(0, 3)  # (index, number)
    else:
        datap = str(daily[0]) + ','
        daily.insert(0, daily[0])  # (index, number)

    counter = 0
    for i in range(0, len(dates_vector)):
        if daily[i] > 0:
            datap = datap + str(daily[i]) + ','
        else:
            datap = datap + "0" + ','
            if counter <= 10:  # set the first 10 numbers at 1 if <=0
                daily[i] = 1  # TODO = 0
                counter += 1
            else:
                daily[i] = 0

    datap = datap[:-1]
    makeFile(datap, prov)

    global arrayDaily
    arrayDaily.append(daily)


def changeDir():
    wk = pathlib.Path.home() / "_R0(t)data"
    wk = os.path.abspath(os.path.realpath(wk))
    if not os.path.isdir(wk):
        os.mkdir(wk)
    os.chdir(wk)


changeDir()  # change working directory

# new file with names of all .R files + number of days
f = open('_zones_list', 'w')
f.write(str(len(dates_vector)) + '\n')  # writes number of days of each array
f.write('Italy' + '\n')
for name in allRegion:
    f.write(name + '\n')
for name in allProvince:
    f.write(name + '\n')
f.close()

nazioneDaily()  # funzione che salva il file aggiornato - nazionale

for x in allRegion:  # funzione che salva il file aggiornato - regionale
    regioneDaily(x)

for x in allProvince:  # funzione che salva il file aggiornato - provinciale
    provinciaDaily(x)

prov_df = pandas.DataFrame(arrayDaily)  # create dataframe out of list of all provinces data
main_df = main_df.append(prov_df)  # add new provinces dataframe to main dataframe
main_df.to_csv('_dataframe', index=False, encoding='UTF-8')  # create .csv 

# elapsed time check
t1_stop = datetime.datetime.now()
print("'getalldata.py' runtime elapsed time in seconds: ", t1_stop - t1_start)
