# Thesis project developed by Alice Schiavone, student at Universit� degli Studi dell'Insubria (July-Sept.2020)
# Thesis advisor: Prof. Davide Tosi

# Data is taken from the Department of Italian Civil Protection for key transmission parameters of an institutional
# outbreak during the 2020 SARS-Cov2 pandemic in Italy

#Loading packages
library(R0)
library(reticulate)
library(rstudioapi)
#py_install("pandas")

# set working directory
start_time <- Sys.time() # get timestamp
setwd(dirname(getActiveDocumentContext()$path))

or_path <- getwd()

est.R0.TD  <- function(){
  source("est.r0.TD.R")
}
est.R0.TD() # load main function

py_run_file("getalldata.py") # download daily data

if (Sys.info()['sysname'] == "Windows") {
  path <- file.path(Sys.getenv("R_USER"), "_R0(t)data")
} else {
  path <- file.path(Sys.getenv("~"), "_R0tdata")
}
setwd(path) # access downloaded data

# get dataframe with daily new cases per area, each column stores data for one area
dataframe <- read.csv("_dataframe", stringsAsFactors=FALSE)
names <- read.delim("_zones_list") # names of areas

setwd(or_path) # return to original working directory
if (!file.exists('plots')){
  dir.create(file.path(or_path, 'plots'), showWarnings = FALSE)
}

counter <- dim(dataframe)[1]

createPlot <- function(val){
  # get parameters
  area <- c(dataframe[val,])
  names(dataframe[val,]) <- seq(from=as.Date("2020-02-24"), to=as.Date("2020-09-16"), by=1) # get names
  zone <- as.numeric(area) # get values 
  
  mGT<-generation.time("gamma", c(3, 1.5))
  
  # apply function
  TD <- est.R0.TD(zone, mGT, begin=1, end=204, nsim=1450) # STANDARD SIMULATION 

  # Warning messages:
  # 1: In est.R0.TD(Italy.2020, mGT) : Simulations may take several minutes.
  # 2: In est.R0.TD(Italy.2020, mGT) : Using initial incidence as initial number of cases.
  TD
  
  # get last R0(t) calculated (yesterday if before 18:00, else today)
  lastTD <- TD[["R"]][length(TD[["R"]])]
  # Reproduction number estimate using  Time-Dependent  method.
  # 2.322239 2.272013 1.998474 1.843703 2.019297 1.867488 1.644993 1.553265 1.553317 1.601317 ...
  
  ## An interesting way to look at these results is to agregate initial data by longest time unit,
  ## such as weekly incidence. This gives a global overview of the epidemic.
  TD.weekly <- smooth.Rt(TD, 4)
  #print(TD.weekly[["conf.int"]])
  #print(TD.weekly[["R"]])
  
  # print which area was successfully calculated
  print(paste(val, names[val, 1]))
  # Reproduction number estimate using  Time-Dependant  method.
  # 1.878424 1.580976 1.356918 1.131633 0.9615463 0.8118902 0.8045254 0.8395747 0.8542518 0.8258094..
  
  # name of plot
  filepath <- paste(or_path, "/plots/" ,val, "_129_", names[val, 1], ".jpeg", sep="")
  
  # plot format
  jpeg(file = filepath, width = 1000, height = 400)
  
  plot(TD.weekly)
  dev.off()
  
  return(lastTD)
}

lastTD = 999 # if data is missing, set to a 'null' value (999)
vTD <- c() # vector of R0(t) values

sink(file='log.txt') # open stream to save log
for (val in seq(1, counter))
{
  tryCatch({
    lastTD <- createPlot(val)
  }, error=function(e){ 
    # if it wasn't possible to create a plot, print it in 'log.txt' for review
    # and append null
    cat("ERROR :", val, names[val, 1], conditionMessage(e), "\n")
    })
  vTD = append(vTD, lastTD)
  lastTD = 999 # null
}
closeAllConnections()


df = data.frame(names, vTD)
colnames(df) <- c("zone", "index")
# export .csv file with R0(t) values
write.table(df, file='R0t-table.csv', quote=FALSE, row.names=FALSE, fileEncoding = "UTF-8", sep=";") 

# print timestamp
end_time <- Sys.time()
print(paste('Operation completed in', end_time - start_time, 'minutes'))