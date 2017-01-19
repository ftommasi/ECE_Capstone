#include <unistd.h>

int main(){
  static const char* ADC_file= "/sys/bus/iio/devices/iio:device0/in_voltage0_raw";
  while(1){
    //read ADC file and spit contents to STDOUT for it to be piped to the network layer

  }
  return 0;
}
