#Loading package
library(R0)
## Data is taken from the Department of Italian Civil Protection for key transmission parameters of an institutional
## outbreak during the 2020 SARS-Cov2 pandemic in Italy)
## ITALY DATASET
#data(Italy.2020) 
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Italy.2020, mGT, begin=1, end=134, nsim=4000)
## REGIONE LOMBARDIA DATASET
data(Lombardia.2020)
mGT<-generation.time("gamma", c(3, 1.5))
TD <- est.R0.TD(Lombardia.2020, mGT, begin=1, end=133, nsim=1450)
## REGIONE VENETO DATASET
#data(Veneto.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Veneto.2020, mGT, begin=1, end=93, nsim=1450)
## REGIONE PIEMONTE DATASET
#data(Piemonte.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Piemonte.2020, mGT, begin=1, end=93, nsim=1450)
## REGIONE LAZIO DATASET
#data(Lazio.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Lazio.2020, mGT, begin=1, end=93, nsim=1450)

## REGIONE EMILIA DATASET
#data(EmiliaRomagna.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(EmiliaRomagna.2020, mGT, begin=1, end=93, nsim=2000)

## REGIONE LIGURIA DATASET
#data(Liguria.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Liguria.2020, mGT, begin=1, end=92, nsim=2000)

## REGIONE MARCHE DATASET
#data(Marche.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Marche.2020, mGT, begin=1, end=92, nsim=1450)

## PROVINCIA MILANO DATASET
#data(Milano.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Milano.2020, mGT, begin=1, end=93, nsim=1450)

## PROVINCIA BERGAMO DATASET
#data(Bergamo.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Bergamo.2020, mGT, begin=1, end=93, nsim=1450)

## PROVINCIA BRESCIA DATASET
#data(Brescia.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Brescia.2020, mGT, begin=1, end=92, nsim=1450)

## PROVINCIA VARESE DATASET
#data(Varese.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Varese.2020, mGT, begin=1, end=93, nsim=1450)

## PROVINCIA MONZA E BRIANZA DATASET
#data(MonzaedellaBrianza.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(MonzaedellaBrianza.2020, mGT, begin=1, end=93, nsim=2050)

## PROVINCIA LODI DATASET
#data(Lodi.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Lodi.2020, mGT, begin=1, end=93, nsim=1450)

## PROVINCIA COMO DATASET
#data(Como.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Como.2020, mGT, begin=1, end=88, nsim=1450)

## PROVINCIA CREMONA DATASET
#data(Cremona.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Cremona.2020, mGT, begin=1, end=92, nsim=3000)

## PROVINCIA PAVIA DATASET
#data(Pavia.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Pavia.2020, mGT, begin=1, end=92, nsim=1450)

## PROVINCIA LECCO DATASET
#data(Lecco.2020)
#mGT<-generation.time("gamma", c(3, 1.5))
#TD <- est.R0.TD(Lecco.2020, mGT, begin=1, end=87, nsim=1450)

## PROVINCIA MANTOVA DATASET
#mGT<-generation.time("gamma", c(3, 1.5))
#data(Mantova.2020)
#TD <- est.R0.TD(Mantova.2020, mGT, begin=1, end=88, nsim=2450)

## PROVINCIA SONDRIO DATASET
#mGT<-generation.time("gamma", c(3, 1.5))
#data(Sondrio.2020)
#TD <- est.R0.TD(Sondrio.2020, mGT, begin=1, end=92, nsim=1450)


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
plot(TD.weekly)
