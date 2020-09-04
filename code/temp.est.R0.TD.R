#Loading package
library(R0)
## Data is taken from the Department of Italian Civil Protection for key transmission parameters of an institutional
## outbreak during the 2020 SARS-Cov2 pandemic in Italy

# STANDARD SIMULATION
data(Sicilia.2020)
mGT<-generation.time("gamma", c(3, 1.5))
TD <- est.R0.TD(Sicilia.2020, mGT, begin=1, end=192, nsim=1450)

# Warning messages:
# 1: In est.R0.TD(Italy.2020, mGT) : Simulations may take several minutes.
# 2: In est.R0.TD(Italy.2020, mGT) : Using initial incidence as initial number of cases.
TD
# Reproduction number estimate using  Time-Dependent  method.
# 2.322239 2.272013 1.998474 1.843703 2.019297 1.867488 1.644993 1.553265 1.553317 1.601317 ...
## An interesting way to look at these results is to agregate initial data by longest time unit,
## such as weekly incidence. This gives a global overview of the epidemic.
TD.weekly <- smooth.Rt(TD, 4)
print(TD.weekly[["conf.int"]])
print(TD.weekly[["R"]])
# Reproduction number estimate using  Time-Dependant  method.
# 1.878424 1.580976 1.356918 1.131633 0.9615463 0.8118902 0.8045254 0.8395747 0.8542518 0.8258094..
png(file = "C:\\Users\\alice\\Desktop\\Tesi\\File\\R PROJECT\\R PROJECT\\R0-master\\data\\plots\\Sicilia.png", width = 1000, height = 400)
plot(TD.weekly)
dev.off()
