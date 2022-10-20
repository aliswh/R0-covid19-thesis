# Thesis project developed by Alice Schiavone, student at Universit? degli Studi dell'Insubria (July-Sept.2020)
# Thesis advisor: Prof. Davide Tosi

# Data is taken from the Department of Italian Civil Protection for key transmission parameters of an institutional
# outbreak during the 2020 SARS-Cov2 pandemic in Italy

#Loading packages
library(R0)
library(reticulate)
library(rstudioapi)
#py_install("pandas") # uncomment this line only on the first run

# set working directory
start_time <- Sys.time() # get timestamp
setwd(dirname(getActiveDocumentContext()$path))

or_path <- getwd()

est.R0.TD  <- function(){
  source("est.R0.TD.R")
}
est.R0.TD() # load main function

py_run_file("getalldata.py") # download daily data

os <- Sys.info()['sysname']
if ( os == "Windows") {
  path <- file.path(Sys.getenv("R_USER"), "_R0(t)data")
} else {
  path <- file.path(Sys.getenv("HOME"), "_R0(t)data")
}
setwd(path) # access downloaded data directory

# get dataframe with daily new cases per area, each column stores data for one area
dataframe <- read.csv("_dataframe", stringsAsFactors=FALSE)
names <- read.delim("_zones_list") # names of areas
dates <- seq(as.Date("2020-02-24"), Sys.Date(), by="days")
weeklydates <- c()
for (d in seq(0,length(dates),7)){
  weeklydates <- append(weeklydates, dates[d])
}

setwd(or_path) # return to original working directory
if (!dir.exists('plots')){
  dir.create(file.path(or_path, 'plots'))
} else { # directory reset
  unlink("plots", recursive = TRUE) # reset folder
  dir.create(file.path(or_path, 'plots'))
}

createPlot <- function(val){
  zone <- as.numeric(dataframe[val,]) # get values 
  
  mGT<-generation.time("gamma", c(3, 1.5))
  
  # apply function
  TD <- est.R0.TD(zone, mGT, begin=1, end=as.numeric(length(zone)), nsim=200) # STANDARD SIMULATION NUMBER 1450
  
  # Warning messages:
  # 1: In est.R0.TD(Italy.2020, mGT) : Simulations may take several minutes.
  # 2: In est.R0.TD(Italy.2020, mGT) : Using initial incidence as initial number of cases.
  TD
  
  # Reproduction number estimate using  Time-Dependent  method.
  # 2.322239 2.272013 1.998474 1.843703 2.019297 1.867488 1.644993 1.553265 1.553317 1.601317 ...
  
  ## An interesting way to look at these results is to agregate initial data by longest time unit,
  ## such as weekly incidence. This gives a global overview of the epidemic.
  TD.weekly <- smooth.Rt(TD, 7)

  return_TD <- c(TD.weekly[["R"]]) 
  #lastTD <- TD.weekly[["R"]][length(TD.weekly[["R"]])] # get R0(t)
  
  # print which area was successfully calculated 
  print(paste(val, names[val, 1])) # testing purpose, not used
  
  # Reproduction number estimate using  Time-Dependant  method.
  # 1.878424 1.580976 1.356918 1.131633 0.9615463 0.8118902 0.8045254 0.8395747 0.8542518 0.8258094..
  
  # set name of plot
  filepath <- paste(or_path, "/plots/" ,val, "_129_", names[val, 1], ".jpeg", sep="")
  # plot format
  jpeg(file = filepath, width = 1000, height = 400)
  
  r0.weekly <- TD.weekly[["R"]]
  y.low <- TD.weekly[["conf.int"]][["lower"]]
  y.high <- TD.weekly[["conf.int"]][["upper"]]
  
  plot(x=c(weeklydates, rev(weeklydates)), y=c(y.high,rev(y.low)), main=names[val, 1], xaxt="n", ylab="R0(t)", xlab = "", col="white")
  
  axis.Date(1, at=weeklydates, format="%d-%m-%Y", las = 2, cex.axis = .85)
  
  polygon(x=c(weeklydates, rev(weeklydates)), y=c(y.high,rev(y.low)), col ="grey90", border=NA)
  
  abline(1,0, lty=2, col="grey")
  lines(weeklydates, r0.weekly, col="black", type="b", pch=20)
  
  dev.off()
  
  return(return_TD)
}

df<-data.frame()
temp_v <- c()
everyTD <-matrix(NA, ncol=41, nrow=129) # export all R0(t) values

sink(file='log.txt') # open stream to save log, testing purpose, not used
for (val in seq(1, dim(dataframe)[1])){ # 
  tryCatch({
    lastTD <- createPlot(val)
    v <- c(names[val,],lastTD[length(lastTD)]) # append R0(t) to csv
    df = rbind(df, v)
    everyTD[val,] <- lastTD
  }, error=function(e){
    cat("ERROR :", val, names[val, 1], conditionMessage(e), "\n") # testing purpose, not used
  })
}
closeAllConnections() # close log, testing purpose, not used

# set col and row names
everyTD <-  round(everyTD,3)
colnames(everyTD) <- format(as.Date(weeklydates), "%Y-%m-%d")
rownames(everyTD) <- names[,1]

write.csv2(everyTD, file='R0t_history.csv', quote=FALSE, row.names=TRUE, fileEncoding = "UTF-8") 
write.csv2(df, file='R0t-table.csv', quote=FALSE, row.names=FALSE, fileEncoding = "UTF-8") 

# print timestamp
end_time <- Sys.time() # get time
print(paste('Operation completed in', end_time - start_time, 'minutes')) # testing purpose, not used