package edu.slu.iot.realdaq;

import io.silverspoon.bulldog.beagleboneblack.pwm.BBBAnalogInput;
import io.silverspoon.bulldog.core.platform.*;
import io.silverspoon.bulldog.core.util.*;
import io.silverspoon.bulldog.beagleboneblack.*;

public class AdcReaderTester{
  public static void main(String[] args){
    AdcReader reader = new AdcReader();
    System.out.println("----------------STARTING AdcReader TESTS---------------------");
    for(int i=0; i < 100; i++){
      double [] samples = reader.read(1,(1.0f));
      for(double sample : samples){
        System.out.println(sample);
      } 
    }
  }
}

