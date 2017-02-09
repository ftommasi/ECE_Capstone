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
  int count = 0;
  while(count < 100000){
    start = clock();
    snprintf(buf,sizeof(buf),ADC_file);
    end = clock();
    int fd = open(buf,0);
    int buflen;
    int sample_count = 0;
    count++;
    while((buflen=read(fd,buf,100))>0){
      //write(1,buf,buflen);
      char tempbuf[5];
      memcpy(tempbuf,&buf[0],4);
      tempbuf[4] ='\0';
      //printf("buf[42]: %s\n",tempbuf);
      fprintf(sample_file,"%s\n",tempbuf);
      //fprintf(sample_file,"%d\n",count);
      //write(fileno(sample_file),count_buf,sizeof(count_buf));
      int in_volts = atoi(buf);
     // snprintf();
     // fprintf(sample_file,buf);
      if(sample_count % 100 == 0){
        //TODO: implement functionality to create a new file every 100 samples
      }

      sample_count++;
      //printf("%f V\n");
    }
  //  printf("\n---TIME: %f---\n",(double)(end-start)/CLOCKS_PER_SEC);
  }
  fclose(sample_file);
  return 0;
}
