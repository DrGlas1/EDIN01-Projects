//Checks the De Bruijn sequence generated in Laboratory exercise 4 
//Input: A file where the sequence is stored

#include <stdio.h>
#include <ctype.h>

int readseq(FILE* fp,int* seq);
//read a De Bruijn sequence from file and store in seq
//returns 1 if sequence contaions 10003 digits 0 otherwise

int checkseq(int* seq,int* states);
//check that the sequence contains all states exactly once
//returns 1 if OK 0 otherwise

main(){
    FILE* fp;//pointer to input file
    char filename[40];
    int states[10000];//the possible states
    int sequence[10003];//the De Bruijn sequence to check
    int statusOK=1;
    
    //read file and store in sequence
    printf("Filename with stored sequence: ");
    scanf("%s",filename);
    fp=fopen(filename,"r");
    if(fp==NULL){
	printf("Trouble reading %s\n",filename);
	exit(1);
    }
    statusOK=readseq(fp,sequence);
    if(statusOK)
	statusOK=checkseq(sequence,states);
    else
	printf("Wrong number of symbols in the sequence\n");
    if(statusOK)
	printf("Sequence OK !!!!!\n");
    else{
	printf("Sequence contaions 10003 digits\n");
	printf("but it is not an De Bruijn sequence\n");
    }
}

int readseq(FILE* fp,int* seq){
    //read a De Bruijn sequence from file and store in seq
    //returns 1 if sequence contaions 10003 digits 0 otherwise
    int c;//character from file
    int N=0;//number of read symbols
    
    //read character from file check that not EOF
    while((c=getc(fp))!=EOF){
	//check if c is a digit
	if(isdigit(c)){
	    if(N<10003)
		seq[N]=c-'0';
	    N++;
	}
    }
    //check that exactly 10003 symbols have been read
    if(N==10003)
	return 1;
    else
	return 0;
    return 0;
}


int checkseq(int* seq,int* states){
    //check that the sequence contains all states exactly once
    //returns 1 if OK 0 otherwise
    int i;//loop counter
    int state;
    int seqOK=1;//1 if sequence OK, 0 otherwise
    //reset the state counter
    for(i=0;i<10000;i++)
	states[i]=0;
    //assign the first state
    state=seq[0]*1000+seq[1]*100+seq[2]*10+seq[3];
    states[state]++;
    //run through all states
    for(i=4;i<10003;i++){
	//calculate new state
	state*=10;
	state+=seq[i];
	state=state%10000;
	//update state counter
	states[state]++;
    }
    for(i=0;i<10000;i++){
	if(states[i]!=1)
	    seqOK=0;
    }
    return seqOK;
}

