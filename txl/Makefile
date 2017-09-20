
# Increase if you get many failed parsing.  Decrease to increase parsing perforamnce.
SIZE=100

# *** WARNING: larger sizes make parsing significantly slower
#     in the presence of syntax errors.

.SUFFIXES:
.SUFFIXES:.txl .x

SRCS = $(wildcard *.txl)
FILTEREDSRCS = $(filter-out generic-%.txl,$(SRCS))
OBJS = $(patsubst %.txl,%.x,$(FILTEREDSRCS))   

all: $(OBJS)

%.x: %.txl
	txlc -s $(SIZE) $<

clean:
	/bin/rm -f *.x

