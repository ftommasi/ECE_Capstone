#include <unistd.h>
#include <stdio.h>
#include <time.h>

int convert_raw_ADC_to_volts(int raw_adc){

  return (raw_adc*1.8)/4096;

}


int main(){
  static const char* ADC_file= "/sys/bus/iio/devices/iio\:device0/in_voltage0_raw";
  char buf[1024];
  clock_t start,end;
  FILE* sample_file;
  sample_file = fopen("SAMPLE-SESSION","a+");

  while(1){
    start = clock();
    snprintf(buf,sizeof(buf),ADC_file);
    end = clock();
    int fd = open(buf,0);
    int buflen;
    int sample_count = 0;
   
    while((buflen=read(fd,buf,100))>0){
      write(1,buf,buflen);
      write(fileno(sample_file),buf,buflen);
      int in_volts = atoi(buf);
     // snprintf();
     // fprintf(sample_file,buf);
      if(sample_count % 100 == 0){
        //TODO: implement functionality to create a new file every 100 samples
      }

      sample_count++;
      //printf("%f V\n");
    }
    printf("\n---TIME: %f---\n",(double)(end-start)/CLOCKS_PER_SEC);
  }
  return 0;
}
