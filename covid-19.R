#Loading package
library(R0)
library(reticulate) #da installare manualmente reticulate, source su py_install("pandas"), miniconda? yes
## Data is taken from the Department of Italian Civil Protection for key transmission parameters of an institutional
## outbreak during the 2020 SARS-Cov2 pandemic in Italy

#py_install("pandas") #solo una volta
#py_install("fpdf") #perchè non installa?
start_time <- Sys.time()

or_path <- getwd()

est.R0.TD  <- function(){
  source("est.r0.TD.R")
}
est.R0.TD()

py_run_file("getalldata.py")

if (Sys.info()['sysname'] == "Windows") {
  path <- file.path(Sys.getenv("R_USER"), "_R0tdata")
} else {
  path <- file.path(Sys.getenv("~"), "_R0tdata")
}
setwd(path)

dataframe <- read.csv("_dataframe", stringsAsFactors=FALSE)
names <- read.delim("_zones_list")

setwd(or_path)

counter <- dim(dataframe)[1]

createPlot <- function(val){
  # STANDARD SIMULATION 
  area = c(dataframe[val,]) #get first row of dataframe file (Italy)
  zone <- as.numeric(area)
  names(area)<-seq(from=as.Date("2020-02-24"), to=as.Date("2020-09-16"), by=1)
  mGT<-generation.time("gamma", c(3, 1.5))
  TD <- est.R0.TD(zone, mGT, begin=1, end=204, nsim=1450)

  # Warning messages:
  # 1: In est.R0.TD(Italy.2020, mGT) : Simulations may take several minutes.
  # 2: In est.R0.TD(Italy.2020, mGT) : Using initial incidence as initial number of cases.
  TD
  lastTD <- TD[["R"]][length(TD[["R"]])]
  # Reproduction number estimate using  Time-Dependent  method.
  # 2.322239 2.272013 1.998474 1.843703 2.019297 1.867488 1.644993 1.553265 1.553317 1.601317 ...
  ## An interesting way to look at these results is to agregate initial data by longest time unit,
  ## such as weekly incidence. This gives a global overview of the epidemic.
  TD.weekly <- smooth.Rt(TD, 4)
  #print(TD.weekly[["conf.int"]])
  #print(TD.weekly[["R"]])
  print(paste(val, names[val, 1]))
  # Reproduction number estimate using  Time-Dependant  method.
  # 1.878424 1.580976 1.356918 1.131633 0.9615463 0.8118902 0.8045254 0.8395747 0.8542518 0.8258094..
  filepath <- paste(or_path, "/plots/" ,val, "_129_", names[val, 1], ".jpeg", sep="")
  jpeg(file = filepath, width = 1000, height = 400)
  plot(TD.weekly)
  dev.off()
  return(lastTD)
}

sink(file='log.txt')
lastTD = 999
lastTD_vector <- c()
for (val in seq(1, counter))
{
  tryCatch({
    lastTD <- createPlot(val)
  }, error=function(e){
    cat("ERROR :", val, names[val, 1], conditionMessage(e), "\n")
    })
  lastTD_vector = append(lastTD_vector, lastTD)
  lastTD = 999
}
closeAllConnections()
df = data.frame(names, lastTD_vector)
write.csv(df, file='lastTDvector.csv', row.names=FALSE)
colnames(df) <- c("zone", "index")
#write.table(lastTD_vector, file='lastTDvector.csv')
end_time <- Sys.time()
print(df)
print(paste('Operation completed in', end_time - start_time, 'minutes'))

#py_run_file("makeboard.py")
