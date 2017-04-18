
package edu.slu.iot.realdaq; 

import io.silverspoon.bulldog.beagleboneblack.pwm.BBBAnalogInput;
import io.silverspoon.bulldog.core.platform.*;
import io.silverspoon.bulldog.core.util.*;
import io.silverspoon.bulldog.core.pwm.*;
import io.silverspoon.bulldog.beagleboneblack.*;
import io.silverspoon.bulldog.beagleboneblack.BBBNames;

public class AdcReader{

  public AdcReader(){
  }

  public double read(){
    //setup ADC 
    Board board = Platform.createBoard();
    AnalogInput in = board.getPin(BBBNames.P9_39).as(AnalogInput.class);

    //BBBAnalogInput adcPin = new BBBAnalogInput(new Pin(39),39);  //Pin 39 is Ain0 for ADC
    return  in.read(); 
  }

  public double[] sampleAt(int samples, float freq){
    //setup ADC 
    Board board = Platform.createBoard();
    AnalogInput in = board.getPin(BBBNames.P9_39).as(AnalogInput.class);

    return in.sample(samples,freq);
 
  }
}
