/////////////////////
// Coleman Jackson
// 112790164
// Operating Systems
// Homework 10
// 12/03/2015
/////////////////////

#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define LONG 2000
int memory[LONG];
int freeStart = 0;

// Structure defining a node in my priority queue
typedef struct
{
	int i;
	int pri;
}
node;

// Structure defining a priority queue
typedef struct
{
	node* que;
	int n;
}
priq;

// Create and return a queue
priq* priq_create()
{
	priq* q = (priq*) malloc(sizeof(priq));
	q->que = NULL;
	q->n = 0;

	return q;
}

// Push to queue with data and priority
void qpush(priq* q, int data, int pri)
{
	int target;

	node no;
	no.pri = pri;
	no.i = data;

	int size = q->n;
	if(q->que == NULL)
		q->que = (node*) malloc(sizeof(node) * (size + 1));
	else
		q->que = (node*) realloc(q->que, sizeof(node) * (size + 1));

	int pos = size-1;
	while(pos > -1 && pri < (q->que[pos]).pri)
	{
		q->que[pos+1] = q->que[pos];
		pos--;
	}

	pos++;
	q->que[pos] = no;
	q->n++;
}

// Return top priority
int qpri(priq* q)
{
	if(q->n > 0)
		return q->que[0].pri;
	else 
		return -1;
}

// Return top value
int qpeak(priq* q)
{
	if(q->n > 0)
		return q->que[0].i;
	else
		return -1;
}

// Pop the top priority value
int qpop(priq* q)
{
	int size = q->n;

	if(size > 0)
	{
		int ret = q->que[0].i;

		int i;
		for(i = 1; i < size; i++)
		{
			q->que[i-1] = q->que[i];
		}
	
		q->que = (node*) realloc(q->que, sizeof(node) * (size - 1));

		q->n--;
		return ret;
	}
	else
		return -1;
}

// Print the queue for debugging
void qprint(priq* q)
{
	printf("PRIORITY QUEUE:\n\n");
	
	int size = q->n;
	int i;
	for(i = 0; i < size; i++)
	{
		printf("%i : %i\n", q->que[i].pri, q->que[i].i);
	}
	printf("\n");
}

// Release the memory for the queue
void priq_release(priq* q)
{
	free(q->que);
	free(q);
}

// Method to allocate memory in our system
int allocate(int i)
{
	// Size of our allocation
	int size = i + 1;

	// Minimum 4
	if(size < 4)
		size = 4;

	// Check if there is room for us.
	int freeTest = freeStart;
	while((int) (memory[freeTest] / 4) < (size))
	{

		freeTest = memory[freeTest + 1];
		if(freeTest == freeStart)
		{
			return -1;
		}
	}

	// If there is room then record some data.
	int freeSize = (int) (memory[freeTest] / 4);
	int preUse = (int) ((memory[freeTest] % 4) / 2);

	int offset;
	int newPre = 0;
	// We can't leave orphaned free partitions smaller than 5
	if((offset = freeSize - size) < 5)
	{
		size += offset;
		newPre = 1;

		int tempNext = memory[freeTest + 1];
		int tempLast = memory[freeTest + 2];

		memory[tempNext + 2] = tempLast;
		memory[tempLast + 1] = tempNext;
	}

	// Now actually change the values in memory to represent the allocation
	int newFree = freeSize - size;

	memory[freeTest] = (newFree * 4) + (preUse * 2) + 0;
	memory[freeTest + (freeSize - 1)] = 0;
	memory[freeTest + (newFree - 1)] = (newFree);

	memory[freeTest + newFree] = ((size * 4) + (newPre * 2) + 1);
	memory[freeTest + newFree + 1] = 0;
	memory[freeTest + newFree + 2] = 0;
	memory[freeTest + newFree + 3] = 0;

	int freeStart = memory[freeTest + 1];

	// Remember to set the preuse bit appropriately.
	int next = freeTest + newFree + size;
	if(memory[next] != 1 && (int) ((memory[next] % 4) / 2) == 0)
		memory[next] += 2;

	return (freeTest + newFree);
}

// Method to release memory in our system
void release(int i)
{
	// First gather data on what type of release this is.
	int postUse = 1;
	int preUse = (int) ((memory[i] % 4) / 2);
	
	int size = (int) (memory[i] / 4);

	if(memory[i + size] == 1)
	{		
		postUse = 1;
	}
	else if(memory[i + size] % 2 == 0)
		postUse = 0;

	// These values will hold the beginning and ending locations of the partition before and after ours.
	int X_B = (i - memory[i-1]);
	int X_E = (i - 1);
	int Y_B = (i + size);
	int Y_E = (Y_B + (int) (memory[Y_B] / 4) - 1);
	int B = i;
	int E = (i + size - 1);

	// Case both before and after are allocated
	if(preUse == 1 && postUse == 1)
	{
		memory[B] = (4 * size) + (2 * 1) + 0;
		memory[E] = (size);

		int temp = memory[1];
		memory[B + 1] = temp;
		memory[temp + 2] = B;
		memory[1] = B;
		memory[B + 2] = 0;

		if(memory[Y_B] != 1)
			memory[Y_B] -= 2;
	}
	// Case before, but not after is allocated
	else if(preUse == 1 && postUse == 0)
	{
		memory[B] = 4 * (size + memory[Y_E]) + (2 * 1) + 0;
		memory[Y_E] += size;
		memory[Y_B] = 0;
		int tempNext = memory[Y_B + 1];
		int tempLast = memory[Y_B + 2];
		
		memory[Y_B + 1] = 0;
		memory[Y_B + 2] = 0;

		memory[B + 1] = tempNext;
		memory[B + 2] = tempLast;
		
		memory[tempNext + 2] = B;
		memory[tempLast + 1] = B;
	}
	// Case after, but not before is allocated
	else if(preUse == 0 && postUse == 1)
	{
		int temp = (int) ((memory[X_B] % 4) / 2);
		memory[X_B] = 4 * (size + memory[X_E]) + 2 * temp + 0;
		memory[E] = (size + memory[X_E]);
		memory[X_E] = 0;
		memory[B] = 0;

		if(memory[Y_B] != 1)
			memory[Y_B] -= 2;
	}
	// Case that neither are allocated
	else if(preUse == 0 && postUse == 0)
	{
		int temp = (int) ((memory[X_B] % 4) / 2);
		memory[Y_E] += (size + memory[X_E]);
		memory[X_B] = (4 * memory[Y_E]) + 2 * temp + 0;

		int tempNext = memory[Y_B + 1];
		int tempLast = memory[Y_B + 2];

		memory[X_E] = 0;
		memory[B] = 0;
		memory[E] = 0;
		memory[Y_B] = 0;
		memory[Y_B + 1] = 0;
		memory[Y_B + 2] = 0;

		memory[tempNext + 2] = tempLast;
		memory[tempLast + 1] = tempNext;
	}
	// If none of these are the case there is some error.
	else
	{
		printf("ERROR\n");
	}	
		
}

// Method to provide requested statistics and print them out
void stats(int alloced, int freed, int now)
{
	// Begin by finding the values we will need, none too difficult.
	int currentAlloc = alloced - freed;
	int currentFree = 0;
	int totalFree = 0;

	int freeBlocks[500];
	
	int temp = 0;
	do
	{
		temp = memory[temp + 1];
		freeBlocks[currentFree] = (int) (memory[temp] / 4);
		totalFree += freeBlocks[currentFree];
		currentFree++;
	}
	while (temp != 0);

	currentFree--;

	int avgUsed = (LONG-totalFree)/currentAlloc;
	int avgFree = totalFree/currentFree;

	int fragA = 0;
	double fragB = 0;

	int i;
	for(i = 0; i < currentFree; i++)
	{
		if(freeBlocks[i] >= avgUsed)
		{
			fragA++;
		}

		fragB += (freeBlocks[i] % avgUsed);
	}

	fragB /= LONG;
	fragB *= 100;

	// Now Print them in an attractive fashion.
	printf("---------------------- Stats at time: %i ---------------------\n\n", now);	

	printf("	Number of allocs: %i	Number of releases: %i\n\n", alloced, freed);
	printf("	Current Allocs: %i	Current Free Blocks: %i\n\n", currentAlloc, currentFree);
	printf("	Total Used Space: %i	Total Free Space: %i\n\n", (LONG-totalFree), totalFree);
	printf("	Average Used Size: %i	Average Free Size: %i\n\n", avgUsed, avgFree);
	printf("	Number of Requests of Average Size We Can Meet: %i\n\n", fragA);
	printf("	Percentage of Memory We Cannot Use With Avg. Requests: %f\n\n", fragB);
}

// Main Method
void main()
{
	printf("\nDYNAMIC PARTITION SIMULATION 1.0\n\n");

	// Begin by initializing the memory array to the state required by our system.
	int i;
	for(i = 0; i < LONG; i++)
		memory[i] = 0;
	memory[1] = 4;
	memory[2] = 4;
	memory[3] = 0;
	memory[4] = (4*(LONG - 5) + 2 * 0 + 0);
	memory[LONG - 2] = LONG - 5;
	memory[LONG - 1] = 1;

	int location;

	int now = 0;
	int address;

	int t;
	int s;
	int T;

	int allocs = 0;
	int releases = 0;

	// Seed randomness.
	srand((unsigned)time(NULL));

	// Create and initialize a queue.
	priq* q = priq_create();
	qpush(q, -1, 0);
	
	printf("------------------------- First 40 Allocations and Releases ----------------------------\n\n");	

	// Run until the queue is empty.
	while(q->n != 0)
	{
		// Main program loop described in the project guide.
		now = qpri(q);
		address = qpeak(q);
		qpop(q);
		
		// Stop after 4000 allocations.
		if(allocs > 4000 && address < 0)
			continue;

		// Choose either allocation or release.
		if(address >= 0)
		{
			release(address);
			releases++;
			if(releases <= 40)
				printf("At time: %i we released used space at position %i.\n\n", now, address);
		}
		else
		{
			// Apply random values for an allocation.
			t = now + (rand()%(250)) + 1;
			s = (rand()%(100)) + 1;
			T = now + (rand()%(60)) + 1;
			
			// Return the loaction we allocated to and load that in the queue to be released.
			location = allocate(s);			

			if(location >= 0)
			{
				qpush(q,location,t);
				qpush(q,-1,T);
				allocs++;

				if(allocs <= 40)
					printf("At time: %i, we allocated %i blocks at address %i until time: %i.\n\n", 
						now, s+1, location, t);
				if(allocs%50 == 0)
					stats(allocs, releases, now);
			}
			// If we get a negative location there was some sort of error.
			else
			{
				printf("--------------- Unable to Allocate %i blocks at time %i -------------------\n\n", s+1, now);
				stats(allocs, releases, now);
				break;
			}
		}
		
	}

	// Release the dynamic queue memory.
	priq_release(q);
	printf("------------------- Program Exits ---------------------\n\n");

}
