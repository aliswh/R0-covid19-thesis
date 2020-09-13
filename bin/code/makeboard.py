import os
from fpdf import FPDF

def changeDir(path):
    dataDir = os.path.dirname(os.path.realpath('__file__'))
    # To access the file inside a sibling folder
    newpath = os.path.join(dataDir, path)
    #filename = os.path.join(fileDir, '../[sibling directory]')
    newpath = os.path.abspath(os.path.realpath(newpath))
    os.chdir(newpath)

path = '../data/data/plots'
changeDir(path)    #change working directory

pdf = FPDF()        # creates pdf
pdf.add_page()
arr = os.listdir()  # creates array with files name in 'plot' folder

xvar = 10
yvar = 20
count = 0
pagecount = 0

pdf.set_text_color(0, 0, 0)
pdf.set_font('Arial', '', 8)

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

    line = str(i).replace('.png', '')
    pdf.image(str(i), x = xvar,  y = yvar, w = 90, h = 36, type = 'png')     # add images to pdf
    pdf.text(xvar, yvar, line)
    count += 1
    pagecount +=1

path = '..'
changeDir(path)    #change working directory
pdf.output('_dashboard.pdf', 'F')