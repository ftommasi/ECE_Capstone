#include <unistd.h>
#include <stdio.h>
#include <time.h>
#include <pthread.h>
#include <fcntl.h>

#define RESOLUTION 10000000

void calculate_sleep_time(float frequency, struct timespec* time, struct timespec* time_elapsed){

  float in_seconds = 1.0f/frequency;
  float partial = in_seconds - (int) in_seconds;
  /*
  time->tv_sec = 
    ((in_seconds - partial) - time_elapsed->tv_sec < 0 ? 
     0  : 
     (in_seconds - partial) - time_elapsed->tv_sec);

  time->tv_nsec =
    ((long)partial*RESOLUTION - time_elapsed->tv_nsec < 0 ?
     0 :
     (long)partial*RESOLUTION - time_elapsed->tv_nsec );
  */
  
  time->tv_sec = 
    (time_elapsed->tv_sec - (in_seconds - partial) < 0 ? 
     0  : 
     (in_seconds - partial) - time_elapsed->tv_sec);

  time->tv_nsec =
    (time_elapsed->tv_nsec - (long)partial*RESOLUTION  < 0 ?
     0 :
     (long)partial*RESOLUTION - time_elapsed->tv_nsec );



}

int main(int argc, char** argv){
  int pid = fork();
  if(pid ==-1){
    printf("error forking\n");
  }
  if(pid == 0){
    execve("py_read.py",NULL,NULL);
    _exit(-1); //execve never returns
  }
  else{
    int status;
    wait(pid,&status,0);
    static const char* ADC_file= "/sys/bus/iio/devices/iio\:device0/in_voltage0_raw";
    char buf[5];
    FILE* sample_file;
    struct timespec walltime;
    struct timespec prevwalltime;
    struct timespec time_elapsed;
    struct timespec sleep_time;

    char flag = 0;
    sample_file = fopen("DAQ-SESSION","a+");
    clockid_t clock_id;
    
    if(clock_getcpuclockid(getpid(),&clock_id) == -1){
      printf("error setting up clock!\n");
      exit(0);
    }

    prevwalltime.tv_sec = 0;
    prevwalltime.tv_nsec =0;
    int ADC_fd;
    int readval = -1;
    ADC_fd=open(ADC_file, O_RDONLY | O_NONBLOCK);
    FILE* ADC;     //fcntl(ADC_fd, F_SETFL, fcntl(ADC_fd,F_GETFL),0);
    //ADC = fopen(ADC_file,"r+");
    while(1){ 
      /*
      char* map = mmap(NULL, sizeof(char)*53, PROT_READ, MAP_SHARED, fd,0);  
      read(ADC_fd,buf,5);
      for (int i = 1; i <=sizeof(char)*53; ++i) {
        printf("%d: %d\n", i, map[i]);
        }
        */
      //snprintf(buf,100,ADC_file);
      read(ADC_fd, &buf, 5); 
      prevwalltime.tv_sec = walltime.tv_sec;
      prevwalltime.tv_nsec = walltime.tv_nsec;
      if(clock_gettime(clock_id,&walltime)==0){
        //known constants for output string of ADC. no need for strlen or memcpy when doing this. speed
        //fprintf(sample_file,"%s %lu\n",buf ,walltime.tv_nsec);
        time_elapsed.tv_sec =  walltime.tv_sec - prevwalltime.tv_sec;
        time_elapsed.tv_nsec =  walltime.tv_nsec - prevwalltime.tv_nsec;
        printf("%s %lu\n",buf,time_elapsed.tv_nsec);
        //printf("%s %d\n",buf,buf);
      }
      else{
        printf("gettime error!!!\n");
      }

      calculate_sleep_time((float)1/atof(argv[1]), &sleep_time,&time_elapsed);
      //nanosleep(&sleep_time,NULL);

   
    }

    close(ADC_fd);
    fclose(sample_file);
    return 0;
  }
}
