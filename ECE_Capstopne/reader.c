#include <unistd.h>
#include <stdio.h>
#include <time.h>


int main(){
  static const char* ADC_file= "/sys/bus/iio/devices/iio\:device0/in_voltage0_raw";
  char buf[1024];
  clock_t start,end;
  while(1){
    start = clock();
    snprintf(buf,sizeof(buf),ADC_file);
    end = clock();
    int fd = open(buf,0);
    int buflen;
    while((buflen=read(fd,buf,100))>0){
      write(1,buf,buflen);
      int in_volts = atoi(buf);
      //printf("%f V\n");
    }
    printf("\n---TIME: %f---\n",(double)(end-start)/CLOCKS_PER_SEC);
  }
  return 0;
}
