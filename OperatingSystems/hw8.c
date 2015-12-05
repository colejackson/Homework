///////////////////////
// Coleman Jackson   //
// Operating Systems //
// Homework 8        //
// Due: Nov. 10      //
///////////////////////

#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <fcntl.h>
#include <pthread.h>
#include <semaphore.h>

int finishedReading = 0;
int nextUp = 0;

// Helper method to read a single word
char* readWord(int fd)
{

   int end;
   int j;

   char str[50];
   int i = 0;
   char c = ' ';

   while(c == ' ' || c == '\n' || c == '\t')
   {
      end = read(fd, &c, 1);
      if(end == 0)
         return "";
   }
   while(c != ' ' && c != '\n' && c != '\t')
   {
      str[i] = c;
      i++;
      read(fd, &c, 1);
   }

   char* ret = malloc(i+1);

   for(j = 0; j < i; j++)
   {
      ret[j] = str[j];
   }

   ret[i] = '\0';

   return ret;
}

// Helper method to turn a string of numeric characters into a integer.
int strToInt(char* c)
{
   int i = 0;
   int res = 0;

   while(c[i] != '\0')
   {
      res *= 10;
      res += (int)(c[i] - '0');
      i++;
   }

   return res;
}

// This method parses the instructions for a given process into tokens.
char** parseInstructions(char* inst, int fd)
{
   int i = 1;
   char** arr;
   arr = malloc(500);

   arr[0] = inst;
   arr[1] = readWord(fd);

   while(strcmp(arr[i], "END") != 0)
   {
      i++;
      arr[i] = readWord(fd);
   }

   return arr;
}

// Constants storing the multiprogramming level, and the max units of resources in the system.
#define MULTI_LEVEL    5
#define R1_UNITS      13
#define R2_UNITS      10
#define R3_UNITS       7
#define R4_UNITS      12

// A matrix created to map process id's to rows of our matrices
int processMap[2][5];
// Heavily related are 2 maps of mutexes and conditions for waiting our bankers queue
pthread_mutex_t mutexMap[5];
pthread_cond_t condMap[5];

// Our mutexes, conditions, and semaphores for the program as a whole
pthread_mutex_t p1;
pthread_mutex_t p2;
pthread_mutex_t p3;
pthread_mutex_t p4;
pthread_mutex_t p5;

pthread_cond_t c1;
pthread_cond_t c2;
pthread_cond_t c3;
pthread_cond_t c4;
pthread_cond_t c5;

sem_t concur;
sem_t crit;

// Structures to hold data for our threads
char*** insArray[500];
int** maxArray[500];
int* idArray[500];

// A 2D matrix storing the max number of our  different resources
int resourceTotal[4];
int resourceRemain[4];

// A set of 3D matrices that hold the allocation matrices
int maxAlloc[5][4];
int currentAlloc[5][4];
int needMatrix[5][4];

// Print a simple output of a matrix to the terminal so we can see fail states.
void printArray(int* arr, int i)
{
   int a;

   printf("[\t");

   for(a = 0; a < i; a++)
   {
      printf("%i,\t", arr[a]);
   }

   printf("]\n");

}

void printMatrix(char* c)
{
   
   if(strcmp(c, "processMap") == 0)
   {
      printArray(processMap[0], 5);
      printArray(processMap[1], 5);
   }
   else if(strcmp(c, "maxAlloc") == 0)
   {
      printArray(maxAlloc[0], 4);
      printArray(maxAlloc[1], 4);
      printArray(maxAlloc[2], 4);
      printArray(maxAlloc[3], 4);
      printArray(maxAlloc[4], 4);
   }
   else if(strcmp(c, "currentAlloc") == 0)
   {
      printArray(currentAlloc[0], 4);
      printArray(currentAlloc[1], 4);
      printArray(currentAlloc[2], 4);
      printArray(currentAlloc[3], 4);
      printArray(currentAlloc[4], 4);
   }
   else if(strcmp(c, "needMatrix") == 0)
   {
      printArray(needMatrix[0], 4);
      printArray(needMatrix[1], 4);
      printArray(needMatrix[2], 4);
      printArray(needMatrix[3], 4);
      printArray(needMatrix[4], 4);
   }
}

// This method runs a bankers algorithm check on the data.
int bankersCheck(int id, int* req)
{
   int row = map(id);

   int maxCpy[5][4];
   int curCpy[5][4];
   int needCpy[5][4];
   int remCpy[4];
   
   int i,j,k;

   for(i = 0; i < 4; i++)
   {
      remCpy[i] = resourceRemain[i];
   }

   for(i = 0; i < 5; i++)
   {
      for(j = 0; j < 4; j++)
      {
         maxCpy[i][j] = maxAlloc[i][j];
         curCpy[i][j] = currentAlloc[i][j];
         needCpy[i][j] = needMatrix[i][j];
      }
   }
 
   for(i = 0; i < 4; i++)
   {
      curCpy[row][i] += req[i];
      needCpy[row][i] -= req[i];
      remCpy[i] -= req[i];
   }

   int flag = 1;
   while(flag)
   {
      flag = 0;
      for(i = 0; i < 5; i++)
      {

         if(maxCpy[i][0] + maxCpy[i][1] + maxCpy[i][2] + maxCpy[i][3] == 0)
            continue;

         for(j = 0; j < 4; j++)
         {
            if(needCpy[i][j] > remCpy[j])
               break;
         }
         
         if(j == 4)
         {
            flag = 1;
            for(k = 0; k < 4; k++)
            {
               remCpy[k] += curCpy[i][k];
               curCpy[i][k] = 0;
               needCpy[i][k] = 0;
               maxCpy[i][k] = 0;
            }
         }
      }

      for(i = 0; i < 4; i++)
      {
         if(remCpy[i] != resourceTotal[i])
            break;
      }

      if(i == 4)
         return 1;
      
   }

   return 0;
   
}

// Return the integer mapping a process to a row of the matrices.
int map(int i)
{
   int id = i;
   if(i < 0)
      id = -1;

   int j = 0;
   while(j < MULTI_LEVEL)
   {
      if(processMap[0][j] == id)
         return j;
      j++;
   }
   return -100;
}

// This method will run through all the processes in our system.
void processInst(void* t)
{
   int index = (int) t;   

   int id = idArray[index];
   int* max = maxArray[index];
   char** ins  = insArray[index];

   while(nextUp != (int) t);
   nextUp++;

   sem_wait(&concur); 
   
   if(!finishedReading)
   { 
      pthread_cond_wait(&condMap[index], &mutexMap[index]);
   }

   sem_wait(&crit); 

   processMap[0][map(-1)] = id;

   int row = map(id);

   int i;
   for(i = 0; i < 4; i++)
   {
      maxAlloc[row][i] = max[i];
      needMatrix[row][i] = max[i];
   }

   sem_post(&crit);

   // index through the instructions file
   i = 0;
   
   while(strcmp(ins[i], "END") != 0)
   {
      //  REQUEST RESOURCES
      if(strcmp(ins[i], "RQ") == 0)
      {
         requestResource(id, ins[i+1], ins[i+2], ins[i+3], ins[i+4]);
         i+=5;
 
      }
      //  RELEASE RESOURCES
      else if(strcmp(ins[i], "RL") == 0)
      {
         if(0 == releaseResource(id, ins[i+1], ins[i+2], ins[i+3], ins[i+4]));
            break;
         i+=5;
      }
      //  SLEEP
      else if(strcmp(ins[i], "SL") == 0)
      {
         sleepT(ins[i+1]);
         i+=2;
      }
   }

   sem_wait(&crit);

   for(i = 0; i < 4; i++)
   {
      maxAlloc[row][i] = 0;
      needMatrix[row][i] = 0;
      resourceRemain[i] += currentAlloc[row][i];
      currentAlloc[row][i] = 0;
   }
   processMap[0][row] = -1;

   sem_post(&crit);

   sem_post(&concur);
}

//  This method requests resources for a given process and runs banker's algorithm.
void requestResource(int id, char* r1, char* r2, char* r3, char* r4)
{
   
   sem_wait(&crit); 

   int req[4];
   req[0] = strToInt(r1);
   req[1] = strToInt(r2);
   req[2] = strToInt(r3);
   req[3] = strToInt(r4);

   int row = map(id);
   int i;

   for(i = 0; i < 4; i++)
   {
      if(req[i] > resourceTotal[i])
      {
         printf("Invalid Request: Not Enough Resources in System\n\n");
         printf("Request Array\n");
         printArray(req, 4);
         printf("\nResource Totals\n");
         printArray(resourceTotal, 4);
         printf("\n");
         return;
      }
      if(req[i] > maxAlloc[row][i])
      {
         printf("Invalid Request: More Than Max Resources\n\n");
         printf("Request Array\n");
         printArray(req, 4);
         printf("\nMax Vector\n");
         printArray(maxAlloc[row], 4);
         printf("\n");
         return;
      }
   }

   int flag = 0;
   while(!bankersCheck(id, req))
   {
      if(!flag)
      {
         printf("\nBanker's Algorithm Check Failed\n\n");
         printf("Process %i in row %i requested an unsafe amount of resources.\n\n", id, row+1);
         printf("\nProcess Map (Shows Which Process Inhabits a Given Index of the Other Matrices)\n");
         printMatrix("processMap");
         printf("\nRequest Array (Request Attempted)\n");
         printArray(req, 4);
         printf("\nResources Remaining\n");
         printArray(resourceRemain, 4);
         printf("\nMax Allocation Matrix\n");
         printMatrix("maxAlloc");
         printf("\nCurrent Allocations Matrix\n");
         printMatrix("currentAlloc");
         printf("\nCurrent Need Matrix\n");
         printMatrix("needMatrix");
         printf("\nThread Waited...\n\n");
         printf("--------------------------------------------------------------------------------------\n");
         flag = 1;
      }

      sem_post(&crit);
      pthread_cond_wait(&condMap[row], &mutexMap[row]);
      sem_wait(&crit);

   }

   if(flag)
   {
      printf("\nProcess %i has left the wait state and will successfully allocate.\n\n", id);
      printf("Request: ");
      printArray(req, 4);
      printf("\n-------------------------------------------------------------------------------------------\n\n");
   }

   for(i = 0; i < 4; i++)
   {
      currentAlloc[row][i] += req[i];
      needMatrix[row][i] -= req[i];
      resourceRemain[i] -= req[i];
   }

   sem_post(&crit);
   
}

//  This method releases resources from a given process.
void releaseResource(int id, char* r1, char* r2, char* r3, char* r4)
{
   sem_wait(&crit);

   int rel[4];
   rel[0] = strToInt(r1);
   rel[1] = strToInt(r2);
   rel[2] = strToInt(r3);
   rel[3] = strToInt(r4);
   int row = map(id);

   int i;
   for(i = 0; i < 4; i++)
   {
      if(rel[i] > currentAlloc[row][i])
      {
         printf("Illegal Release: Not Enough Resources");
         return 0;
      }
   }

   for(i = 0; i < 4; i++)
   {
      currentAlloc[row][i] -= rel[i];
      needMatrix[row][i] += rel[i];
      resourceRemain[i] += rel[i];
   }

   for(i = 0; i < 5; i++)
   {
      pthread_cond_signal(&condMap[i]);
   }

   sem_post(&crit);
   return 1;
}

//  This method sleeps a process for a short period of time.
void sleepT(char* time)
{  
   sleep(strToInt(time));
}

//  MAIN METHOD
void main(int argc, char* argv[])
{
   // BEGIN INTIALIZATION CODE
   sem_init(&concur, 0, 5);
   sem_init(&crit, 0, 1);

   //  And the resource matrix
   resourceTotal[0] = R1_UNITS;
   resourceTotal[1] = R2_UNITS;
   resourceTotal[2] = R3_UNITS;
   resourceTotal[3] = R4_UNITS;

   resourceRemain[0] = R1_UNITS;
   resourceRemain[1] = R2_UNITS;
   resourceRemain[2] = R3_UNITS;
   resourceRemain[3] = R4_UNITS;

   mutexMap[0] = p1;
   mutexMap[1] = p2;
   mutexMap[2] = p3;
   mutexMap[3] = p4;
   mutexMap[4] = p5;

   condMap[0] = c1;
   condMap[1] = c2;
   condMap[2] = c3;
   condMap[3] = c4;
   condMap[4] = c5;

   //  Set all matrices to be arrays of zeroes for the intial upstart
   int i,j;
   for(i = 0; i < 5; i++)
   {
      // Load our maps for containing the running processes and mapping them to Banker's
      processMap[0][i] = -1;
      processMap[1][i] = i;

      // Now load 0's into all the bankers algo arrays
      for(j = 0; j < 4; j++)
      {
         maxAlloc[i][j] = 0;
         currentAlloc[i][j] = 0;
         needMatrix[i][j] = 0;
      }
   }
   // BEGIN PROGRAM CODE
   
   int t = 0;
   pthread_t threads[500];

   // Check arguments
   if(argc != 2)
   {
      printf("Improper Arguments\n");
      return;
   }

   // Open file
   int fd = open(argv[1], O_RDONLY);

   int id;
   int* max;

   char* cmd;
   while(strcmp(cmd = readWord(fd), "") != 0)
   {
      if(strcmp(cmd, "ID") == 0)
      {
         id = strToInt(readWord(fd));
      }
      else if(strcmp(cmd, "MN") == 0)
      {
         max = malloc(4);
         for(i = 0; i < 4; i++)
         {
            max[i] = strToInt(readWord(fd));
         }
      }
      else if(strcmp(cmd, "ID") == strcmp(cmd, "MN"))
      {  
         
         insArray[t] = parseInstructions(cmd, fd);
       
         // Create a process
         idArray[t] = id;
         maxArray[t] = max;

         pthread_create(&threads[t], NULL, processInst, (void*) t);
         t++;
      }
   }

   while(nextUp != t);
   finishedReading = 1;

   for(i = 0; i < MULTI_LEVEL; i++)
   {
      pthread_cond_signal(&condMap[i]);
   }

   pthread_exit(NULL); 
}
