#!/usr/bin/env python

import Adafruit_BBIO.ADC as ADC
import time 

ADC.setup()
while True:
  start =time.clock()
  ADC.read_raw("P9_39")
  end = time.clock()
  #print "end - start = ", end-start

  print "raw: ",   ADC.read_raw("P9_39") , "val: ", ADC.read("P9_39")*1.8

