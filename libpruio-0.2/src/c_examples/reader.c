#define _GNU_SOURCE 1
#include "stdio.h"
#include <termios.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/time.h>
#include "/home/debian/ECE-Capstone-Demo/src/main/c/ECE_Capstone/libpruio-0.2/src/c_wrapper/pruio.h"
#include "/home/debian/ECE-Capstone-Demo/src/main/c/ECE_Capstone/libpruio-0.2/src/c_wrapper/pruio_pins.h"
#include <time.h>


//"/home/debian/ECE-Capstone-Demo/src/main/c/ECE_Capstone/libpruio-0.2/src/c_examples"

//! The header pin to use.
#define PIN P8_07


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
/*

int pid = fork();
  if(pid ==-1){
    printf("error forking\n");
  }
  if(pid == 0){
    execve("py_read.py",NULL,NULL);
    _exit(-1); //execve never returns
  }
  else{

   */

/*! \file button.c
\brief Example: get state of a button.

This file contains an example on how to use libpruio to get the state
of a button connetect to a GPIO pin on the beaglebone board. Here pin 7
on header P8 is used as input with pullup resistor. Connect the button
between P8_07 (GPIO input) and P8_01 (GND).

Licence: GPLv3

Copyright 2014 by Thomas{ dOt ]Freiherr[ At ]gmx[ DoT }net


Compile by: `gcc -Wall -o button button.c -lpruio`

*/


//! Message for the compiler.
/*! \brief Wait for keystroke or timeout.
\param mseconds Timeout value in milliseconds.
\returns 0 if timeout, 1 if input available, -1 on error.

Wait for a keystroke or timeout and return which of the events happened.

*/
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

//! The main function.
int main(int argc, char **argv)
{
  struct timespec walltime;
  struct timespec prevwalltime;
  struct timespec time_elapsed;
  struct timespec sleep_time;
  FILE* adcfile = fopen("PRU-ADC-READ","a+");
  long i = 0;
  pruIo *io = pruio_new(PRUIO_DEF_ACTIVE, 0x98, 0, 1); //! create new driver structure
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
      prevwalltime.tv_sec = walltime.tv_sec;
      prevwalltime.tv_nsec = walltime.tv_nsec;
      clock_gettime(clock_id,&walltime);
      /*
      fprintf(adcfile, "t: %lu | x: %d\n", 
          time_elapsed.tv_nsec,
          io->Adc->Value[1] 
          );
      */
       printf("%lu:%lu %d\n", 
          time_elapsed.tv_sec,
          time_elapsed.tv_nsec,
          io->Adc->Value[1] 
          );
          i++;
      //known constants for output string of ADC. no need for strlen or memcpy when doing this. speed
      time_elapsed.tv_sec =  walltime.tv_sec - prevwalltime.tv_sec;
      time_elapsed.tv_nsec =  walltime.tv_nsec - prevwalltime.tv_nsec;
      //calculate_sleep_time((float)1/atof(argv[1]), &sleep_time,&time_elapsed);
      sleep_time.tv_sec = 0;
      sleep_time.tv_nsec = 20000 - time_elapsed.tv_nsec;
      nanosleep(&sleep_time,NULL);
      //fflush(STDIN_FILENO);
    }
    tcsetattr(STDIN_FILENO, TCSANOW, &oldt); //           reset terminal

    //printf("\n");
  } while (0);
  fclose(adcfile);
  pruio_destroy(io);       /* destroy driver structure */
	return 0;
}
