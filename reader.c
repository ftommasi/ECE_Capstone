#define _GNU_SOURCE 1
#include "stdio.h"
#include <termios.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/time.h>
//not very portable ... oops
#include "libpruio-0.2/src/c_wrapper/pruio.h"
#include "libpruio-0.2/src/c_wrapper/pruio_pins.h"
#include <time.h>
#include <signal.h>
#include <stdlib.h>

//"/home/debian/ECE-Capstone-Demo/src/main/c/ECE_Capstone/libpruio-0.2/src/c_examples"

//! The header pin to use.
#define PIN P8_07

#define bool char
#define RESOLUTION 1000000000

static pruIo *io; 

void calculate_sleep_time(float frequency, struct timespec* time, struct timespec* time_elapsed){

  float in_seconds = 1.0f/frequency;
  float partial = in_seconds - (int) in_seconds;
  
  time->tv_sec = 
         (long)(in_seconds - partial);

  time->tv_nsec =
    
     (long)(partial*RESOLUTION);



}




//function to wait for keystroke
int
isleep(unsigned int mseconds)
{
  fd_set set;
  struct timespec timeout;

  /* Initialize the file descriptor set. */
  FD_ZERO(&set);
  FD_SET(STDIN_FILENO, &set);

  /* Initialize the timeout data structure. */
  timeout.tv_sec = 0;
  timeout.tv_nsec = mseconds ;

  return TEMP_FAILURE_RETRY(select(FD_SETSIZE,
                                   &set, NULL, NULL,
                                   &timeout));
}

//handle cleanup if things go south
int sighandler(int signum){
  pruio_destroy(io);       /* destroy driver structure */
  exit(-1);
}

//! The main function.
int main(int argc, char **argv)
{
  signal(SIGINT,sighandler);
  signal(SIGSEGV,sighandler);
  
  struct timespec walltime;
  struct timespec prevwalltime;
  struct timespec time_elapsed;
  struct timespec sleep_time;
  struct timespec delay_time;
  struct timespec delay_start;
  float frequency;
  if(argc == 1){
    frequency = 50000.0f;
  }
  else{
    frequency = atof(argv[1]);
  }

  long i = 0;
  int rc;
  io = pruio_new(PRUIO_DEF_ACTIVE, 0x98, 0, 1); //! create new driver structure
  do {
    if (io->Errr) {
               printf("initialisation failed (%s)\n", io->Errr); break;}

    if (pruio_config(io, 1, 0x1FE, 0, 4)) {
                       printf("config failed (%s)\n", io->Errr); break;}

    struct termios oldt, newt; //             make terminal non-blocking
    tcgetattr( STDIN_FILENO, &oldt );
    newt = oldt;
    newt.c_lflag &= ~( ICANON | ECHO );
    newt.c_cc[VMIN] = 0;
    newt.c_cc[VTIME] = 0;
    
    clockid_t clock_id;
    
    if(clock_getcpuclockid(getpid(),&clock_id) == -1){
      printf("error setting up clock!\n");
      exit(0);
    }

    prevwalltime.tv_sec = 0;
    prevwalltime.tv_nsec =0;

    
    tcsetattr(STDIN_FILENO, TCSANOW, &newt);

    while(1) { //                      run loop until keystroke
      clock_gettime(clock_id,&prevwalltime);
       printf("%lu:%lu %d\n", 
          time_elapsed.tv_sec,
          time_elapsed.tv_nsec,
          io->Adc->Value[1] 
          );
      //known constants for output string of ADC. no need for strlen or memcpy when doing this. speed
      //calculate_sleep_time((float)1/atof(argv[1]), &sleep_time,&time_elapsed);
      
      calculate_sleep_time(frequency, &sleep_time,&time_elapsed);
      //printf("[%f]sleeping for %lu:%lu\n",frequency,sleep_time.tv_sec,sleep_time.tv_nsec);
      nanosleep(&sleep_time,NULL);
      clock_gettime(clock_id,&walltime);
      
      time_elapsed.tv_sec =  walltime.tv_sec - prevwalltime.tv_sec;
      time_elapsed.tv_nsec =  walltime.tv_nsec - prevwalltime.tv_nsec;

      //fflush(STDIN_FILENO);
    }
    tcsetattr(STDIN_FILENO, TCSANOW, &oldt); //           reset terminal

    //printf("\n");
  } while (0);
  pruio_destroy(io);       /* destroy driver structure */
	return 0;
}
