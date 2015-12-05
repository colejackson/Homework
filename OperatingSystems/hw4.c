////////////////////////////////////
// COLEMAN JACKSON
// HOMEWORK #4
// OPERATING SYSTEMS
// DUE: 10/15/15
///////////////////////////////////

#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>
#include <fcntl.h>

// Initialize Global Variables for Threads
char buffer[60];
char input = '\0';
int line = 0;

// This flag will signal end of file to all threads in syncronization
int endOfFile = 0;

// We declare a number of mutexes and wait conditions

// one for processing characters
pthread_mutex_t unprocessed_char;
pthread_cond_t PROCESSED;

// another for a new character available to process
pthread_mutex_t no_new_char;
pthread_cond_t NEW_CHAR;

// another still for a line completed
pthread_mutex_t building_line;
pthread_cond_t FINISHED_LINE;

// and another for the printer doing it's business
pthread_mutex_t waiting_on_print;
pthread_cond_t PRINTED;

// the filename as passed into the program arguments
char* filename;

// this method reads the characters into the system
void readChar()
{
   // initialize the file
   int fd = open(filename, O_RDONLY);

   // and until we break
   for(;;)
   {
      // if there is another character and it is our turn
      if(input != '\0' && line == 0)
      {
         pthread_cond_wait(&PROCESSED, &unprocessed_char);
      }

      // then read until the file is empty
      if(read(fd, &input, 1) != 1)
         break;

      // signal to the process thread
      pthread_cond_signal(&NEW_CHAR);

   }

   // at end close the file and inform the other threads to close
   close(fd);
   endOfFile++;
   pthread_cond_signal(&NEW_CHAR);
}

// method (thread) to process the input
void processChar()
{
   // we store the position in the buffer
   int pos = 0;
   buffer[pos] = '\0';

   // the loop around
   for(;;)
   {
      // wait if it isn't our turn
      if(input == '\0' && line == 0)
      {
         pthread_cond_wait(&NEW_CHAR, &no_new_char);
      }

      // break at the end of file
      if(endOfFile == 1)
         break;

      // switch case to hold all our possible actions
      switch(input)
      {
         // case to remove a char
         case '*':
            buffer[pos] == '\0';
            pos--;
            break;
         // case to newline within a line
         case '@':
            buffer[pos] = '\n';
            pos++;
            break;
         // case to delete a word
	 case '$':
	    do
            {
               buffer[pos] == '\0';
               pos--;
            }
            while(buffer[pos] != ' ');
	    break;
         // case to delete unfinished line
	 case '&':
	    pos = 0;
            buffer[pos] = '\0';
	    break;
         // case to signal newline and print the buffer
	 case '\n':
            buffer[pos] = '\n';
	    buffer[pos+1] = '\0';
	    line = 1;
            pos = 0;
            pthread_cond_signal(&FINISHED_LINE);
            pthread_cond_wait(&PRINTED, &waiting_on_print);
	    break;
         // default case to add a char to the buffer
         default:
	    buffer[pos] = input;
            pos++;
	    break;
      }

      // signal processed
      pthread_cond_signal(&PROCESSED);

      // reset the input char
      input = '\0';
   }

   endOfFile++;
   pthread_cond_signal(&FINISHED_LINE);
}

// this thread will print the current line
void printLine()
{
   // while we have a line to print
   for(;;)
   {
      // check for our turn
      if(line != 1)
      {
         pthread_cond_wait(&FINISHED_LINE, &building_line);
      }

      // break at end of file
      if(endOfFile == 2)
         break;

      // print the line
      printf("%s", &buffer);

      buffer[0] = '\0';
      line = 0;
      input = '\0';

      // signal printed
      pthread_cond_signal(&PRINTED);
   }
}

void main(int argc, char* argv[])
{
   // make three threads
   pthread_t threads[3];

   // check the arguments
   if(argc != 2)
   {
      printf("error args\n");
      return;
   }

   filename = argv[1];

   // create and start our threads
   pthread_create(&threads[0], NULL, readChar, NULL);
   pthread_create(&threads[1], NULL, processChar, NULL);
   pthread_create(&threads[2], NULL, printLine, NULL);

   // wait for them to exit
   pthread_exit(NULL);
}
