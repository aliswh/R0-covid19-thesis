# R0(t) visualization tool for Italy

Tool to download data and output the reproduction number estimate using Time-Dependent method for Italy, its regions and its provinces.  
Thesis project of Alice Schiavone, student at Università dell'Insubria.  
Thesis advisor: Prof. Davide Tosi  

## Installation

- Open `mainscript.R`.
- In RStudio, go to Packages and Install. 
- Download `Reticulate` package.
- Decomment `line 11`, run it.

```bash
#py_install("pandas")
```
- When running, it will ask to download `Miniconda`: press 'Y' and enter.
- Comment the previous line. You can now source the file.

## Run

- Open `mainscript.R` and source it. 
- Plots will be made in 'plots' folder, created by the script.
- To make .pdf files to export the last R0(t) value for each area and its plot, run `makeboard.py`.
> `R0(t)_values_plots` includes plots, `R0(t)_values` doesn't.

## Issues

The script doesn't output R0(t) for some areas.

## Project status

Work in progess.

## Further project development

This project will be extended by Alessandro Riva. 
> Link will be available

## Data 
Data is taken from the Department of Italian Civil Protection [GitHub](https://github.com/pcm-dpc/COVID-19) open data set.
