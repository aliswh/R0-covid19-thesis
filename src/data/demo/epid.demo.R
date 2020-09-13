## Demo file
library(R0)

# Generating an epidemic with given parameters
mGT <- generation.time("gamma", c(3,1.5))
mEpid <- sim.epid(epid.nb=1, GT=mGT, epid.length=64, family="poisson", R0=2.67, peak.value=2300)
mEpid <- mEpid[,1]

# Running estimations
est <- estimate.R(epid=mEpid, GT=mGT, methods=c("EG","ML","TD"))

# Model estimates and goodness of fit can be plotted
plot(est)
plotfit(est)

# Sensitivity analysis for the EG estimation; influence of begin/end dates
s.a <- sensitivity.analysis(res=est$estimates$EG, begin=1:32, end=33:64, sa.type="time")

# This sensitivity analysis can be plotted
plot(s.a)
