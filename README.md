# R0(t) visualization tool for Italy

Tool to download data and output the reproduction number estimate using Time-Dependent method for Italy, its regions and its provinces.

## Installation

Open 'covid-19.R'.
In RStudio, go to Packages and Install. 
Download 'Reticulate' package.
Decomment line 5, run it.

```bash
#py_install("pandas")
```
When running, it will ask to download 'Miniconda': press 'Y' and enter.
Comment the previous line. You can now source the file.

## Usage

Open 'covid-19.R' and source it. 
Plots will be made in 'plots' folder.
To make .pdf files to export the last R0(t) value for each area and its plot, run 'makeboard.py'.
'_dashboard.pdf' includes plots, 'R0(t)_values' doesn't.

## Project status

Work in progess.

## Data 
Data is taken from the Department of Italian Civil Protection for key transmission parameters of an institutional outbreak
during the 2020 SARS-Cov2 pandemic in Italy
