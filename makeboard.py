import os, sys, pandas
from fpdf import FPDF

def changeDirOut(path):
    dataDir = sys.argv[0]   # current directory
    newpath = os.path.join(dataDir, path)
    newpath = os.path.abspath(os.path.realpath(newpath))
    os.chdir(newpath)
changeDirOut("..")

df = pandas.read_csv('R0t-table.csv', sep=';')
df.columns = range(df.shape[1])
print(df)

changeDirOut('../plots')    #change working directory

pdf = FPDF()        # creates pdf
pdf_values = FPDF() 
pdf.add_page()
pdf_values.add_page()

arr = os.listdir()  # creates array with files name in 'plots' folder

xvar = 10
yvar = 20
count = 0
pagecount = 0

pdf.set_text_color(0, 0, 0)
pdf_values.set_text_color(0, 0, 0)

pdf.set_font('Arial', 'B', 10)
pdf_values.set_font('Arial', 'B', 10)
title = 'Reproduction number estimate using Time-Dependent method'
pdf.multi_cell(0, 5, title)
pdf_values.multi_cell(0, 5, title)

header = 'Data is taken from the Department of Italian Civil Protection for key transmission parameters of an institutional outbreak during the 2020 SARS-Cov2 pandemic in Italy'
pdf.set_font('Arial', '', 10)
pdf_values.set_font('Arial', '', 10)
pdf.multi_cell(0, 5, header)
pdf_values.multi_cell(0, 5, header)
pdf.multi_cell(0, 5, '') #empty line
pdf_values.multi_cell(0, 5, '')

pdf.set_text_color(0, 0, 0)
pdf.set_font('Arial', '', 8)
pdf_values.set_text_color(0, 0, 0)
pdf_values.set_font('Arial', '', 8)

for i in range(0, len(arr)):
    line = df.iloc[i][0]
    value = df.iloc[i][1]
    value = line + " = "+  str(value)
    pdf.cell(w=0, h=5, txt=value, ln=1)
    pdf_values.cell(w=0, h=5, txt=value, ln=1)
pdf.add_page()

for i in arr:
    if(count>1):
        count=0
        yvar += 40
    
    if(pagecount>11):
        pagecount=0
        xvar = 0
        yvar = 20
        pdf.add_page()

    if(count==0):
        xvar = 10
    elif(count==1):
        xvar = 110

    line = str(i).replace('.jpeg', '')
    line = line.replace('_129_', '. ')

    pdf.image(str(i), x = xvar,  y = yvar, w = 90, h = 36, type = 'jpeg')     # add images to pdf

    pdf.text(xvar, yvar, line)  # add clean text
    count += 1
    pagecount +=1

changeDirOut('..')    #change working directory

pdf.output('R0(t)_values_plots.pdf', 'F').encode('latin-1','ignore')
pdf_values.output('R0(t)_values.pdf', 'F').encode('latin-1','ignore')