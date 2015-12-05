/////////////////////////////////////////////
//
//   Coleman Jackson
//   Operating Systems Homework 2
//   Due: 9/21/15
//
/////////////////////////////////////////////

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include <sys/types.h>

void main(int argc, char* argv[])
{
   // Check for correct arguments.

   if (argc != 2)
      printf("Incorrect Arguments\n\n");
   else
      printf("Input file: %s\n\n", argv[1]);

   // Open the text file

   int fd;
   fd = open(argv[1], O_RDONLY);

   // Store all input in one buffer for convenience sake

   char buffer[10000];
   buffer[0] = '\0';

   // Read into the buffer and store the buffer size

   int buffsize =  read(fd, &buffer, 10000);
   buffer[buffsize] = '\0';

   // Use a for loop to determine the number of lines in the file.

   int j,lines = 0;
   for(j=0; j < buffsize; j++)
   {
      if(buffer[j] == '\n')
      {
         lines++;
         while(buffer[j] == '\n')
            j++;
      }
   }

   // Create an aarray of file lines

   char token[lines][30];

   j=0;
   int l,m,n;

   // Fill the line array by delimiting using the \n

   for(l=0; l < buffsize; l++)
   {
      if(buffer[l] != '\n')  // when we find a newline
      {
         m = l;
         while(buffer[m] != '\n')  // move up until we find another one
         {
            m++;
         }

         n=0;
         while(l < m)  // Fill one level of the line array
         {
            token[j][n] = buffer[l];
            l++;
            n++;
         }
         token[j][n] = '\0';
         j++;
      }
      else
         l++;
   }

   // Close the file

   close(fd);

   // Decide if a given line is a piping or nonpiping line

   int flag = 0;
   for(j=0; j<lines; j++)
   {
      l=0;
      while(token[j][l] != '\0')
      {
         if(token[j][l] == '|')
         {
            flag = 1;
         }
         l++;
      }

      // Move the the appropriate helper method.

      printf("%s\n\n",token[j]);

      if(flag == 1)
         pipingFork(token[j]);
      else
         simpleFork(token[j]);
   }
}

void simpleFork(char* a)
{

   // Basic string tokenization

   int w = 0;
   char* args[10];
   char* pos;
   pos = strtok(a, " ");
   while(pos != NULL)
   {
      args[w] = pos;
      pos = strtok(NULL, " ");
      w++;
   }
   args[w] = NULL;

   // String fully tokenized

   //char arg[10][10];
   //int i = 0; int j = 0; int k = 0;
   /*while(a[i] != '\0')
   {
      if(a[i] == ' ')
      {
         i++;
      }
      else
      {
         if((i == 0) && (j == 0) && (k == 0))
         {
            arg[j][k] = a[i];
            i++;
            k++;
         }
         else if(a[i+1] == '\0')
         {
            arg[j][k] = a[i];
            arg[j][k+1] = '\0';
            arg[j+1][0] = '\0';
            i++;
         }
         else if(a[i-1] == ' ')
         {
            arg[j][k] = a[i];
            i++;
            k++;
         }
         else if(a[i+1] == ' ')
         {
            arg[j][k] = a[i];
            arg[j][k+1] = '\0';
            i++;
            k=0;
            j++;
         }
         else
         {
            arg[j][k] = a[i];
            i++;
            k++;
         }
      }
   }*/

   int i;
   int k=0;
   for (i=0; args[i] != NULL; i++)
      k++;

   char* argums[k+1];
   argums[k+1] = NULL;

   int m;
   for (i=0; args[i] != NULL; i++)
   {
      argums[i] = args[i];
   }

   // Fork for the command line

   int pid = fork();

   switch(pid)
   {
      case -1: printf("Fork Failed\n"); // failed case
         exit(-1);
      case 0:// printf("I am the child, PID: %i\n", getpid());
         execvp(args[0],args); // execute the command in the child.
         exit(0); // close if the exec() fails
      default: wait();
         printf("\n\n"); // after waiting go ahead and return a couple times
        // printf("I am the parent, PID: %i, CHILD: %i\n", getpid(), pid);
   }
}

void pipingFork(char* a)
{
   printf("%s\n",a);
   printf("Piping Not Implemented\n\n");
}
