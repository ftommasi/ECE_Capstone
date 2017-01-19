#include <unistd.h>
#include <stdio.h>

int main(){
  static const char* ADC_file= "/sys/bus/iio/devices/iio:device0/in_voltage0_raw";
  char buf[BUF_MAX];
  char val[4];
  while(1){
    //read ADC file and spit contents to STDOUT for it to be piped to the network layer
    int bytes_read = snprintf(buf, sizeof(buf),ADC_file);
    fd = open(buf,O_RDONLY);
    //do error checking
    read(fd,&val,4);
    printf("%f\n",val);
  }
  return 0;
}
