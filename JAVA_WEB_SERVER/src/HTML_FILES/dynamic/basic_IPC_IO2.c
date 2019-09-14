#include <stdio.h>
#include <string.h>
#include <unistd.h>
void dynGen(char outputString[], char inputString[]){
	strcpy(outputString, "<a href=\"./files/download_this.zip\" download=\"zipExample.zip\">Download zip</a> <br> <a href=\"./files/download_this.txt\" download=\"textExample.zip\">Download text</a><br>");
}
void readMainFile(char outputString[]){
	//printf("getting main\n");
	char textFileData[700];
	char buff[500];
	char dynStr[500];
	FILE *fp = NULL;
	char *dynMarker;
	char *dynMarker2;
	int starCount = 0;
	int counter = 0;
	int counter2 = 0;
	
	int dynStringLength = 35;
	char dynString[] = "this text was dynamically generated";
	char buffAlt[500];
	fp = fopen("mainpage.html", "r");
	//printf("aaaaa");
	while (!strstr(buff, "</html>")){
		fgets(buff, 255, (FILE*)fp);
		
		if (strstr(buff, "**classList**")){
			//printf("aaaaa %s:\n", buffAlt);
			dynMarker = strstr(buff, "**classList**");
			//printf("%s\n", buff);
			/*
			while (buff[counter] != *(dynMarker)){
				printf("loop write\n");
				//buffAlt[counter] = buff[counter];
				counter ++;
				
			}
			*/
			dynMarker+=13;
			//strncpy(buffAlt, buff, 13);
			dynGen(dynString, "**dynText**");
			strcat(buffAlt, dynString);
			strcat(buffAlt, dynMarker);
			
			
			//strcat(buffAlt, dynMarker);
			//strcat(buffAlt, dynMarker2);
			//printf("aaaaa %s:\n", buffAlt);
			strcpy(buff, buffAlt);
			
		}
		
		strcat(outputString, buff);
    }
	strcat(outputString,"</html>");
	//printf("3: %s\n", outputString );
	//strcat(outputString,"</html>");
	//printf("3: %s\n", outputString );
	
}

void readDynFile(char outputString[],char fileName[]){
	char textFileData[700];
	char buff[255];
	FILE *fp = NULL;
	char *dynMarker;
	char *dynMarker2;
	int starCount = 0;
	int counter = 0;
	int counter2 = 0;
	
	int dynStringLength = 11;
	//char dynString[] = "this text was dynamically generated";
	char dynString[] = "so was this";
	char buffAlt[255];
	fp = fopen(fileName, "r");
	//printf(" file open\n");
	//printf("opened dyn: %s\n", fileName);
	while (!strstr(buff, "</html>")){
		fgets(buff, 255, (FILE*)fp);
		if (strstr(buff, "**classList**")){
			dynGen(buff, "**classList**");
			//dynMarker = strstr(dynString, "**classList**");
			//printf("aaab\n");
			strcpy(buff, dynString);
			
		}
		strcat(outputString, buff);
    }
	strcat(outputString,"</html>");
	//printf("output:\n %s\n", outputString );
	
}

void readBinFile(char fileName[]){
	FILE *fileTrace = fopen("c__bin_trace.txt", "wb");
	FILE *fileTrace2 = fopen("c_bin_trace_text.txt", "a");
	char textFileData[700];
	char buff[255];
	FILE *fp = NULL;
	fp = fopen(fileName, "rb");
	FILE *fp2 = NULL;
	//fp2 = fopen(stdout, "wb");
	unsigned char readInt[10000];
	unsigned char test[1];
	test[0] = 'a';
	readInt[0]=0;
	
	fread(test, 1, 1, fp);
	/*
	while (!feof(fp)){
		fread(readInt, sizeof(readInt), 1, fp);
	}
	*/
	//puts(readInt);
	fclose(fp);
	fclose(fp2);
	fclose(fileTrace);
}

main(int argc, char **argv) {
	
	
	char input[1000];
	char input2[300];
	char textFileData[700];
	textFileData[0]='\0';
	char buff[255];
	char fileName[30];
	
	char *filePointer;
	int count = 0;
	int fc = 0;
	int temp = 0;
	int a;
	int binCheck = 0;
	char *charPointer;
	char *charPointer2;
	int nameCount = 0;
	
	//gets (input);
	//fprintf(fileTrace,"input: %s.\n", input);
	gets (input);
	
	if (strstr(input,"GET / HTTP/1.1")){
		//printf("main req");
		readMainFile(textFileData);
	}
	else if (strstr(input, "GET /mainpage.html")){
		readMainFile(textFileData);
	}
	else if (strstr(input, "GET /")){
		charPointer = strstr(input, "/");
		charPointer++;
		charPointer2 = strstr(input, "/");
		while(*charPointer2 != ' '){
			charPointer2++;
			nameCount++;
		}
		strncpy(fileName, charPointer2, nameCount);	
		
		if (strstr(input, ".html")){
				readDynFile(textFileData, fileName);
		}
		else {
			
		}
	}
	
	else {
		printf("error\n");
	}
	
	//printf("output file:\n %s\n", textFileData );
	if (binCheck == 0){
		//fprintf(fileTrace, textFileData);
		FILE *fileTrace;
		fileTrace = fopen("c_trace.txt", "a");
		fprintf(fileTrace, textFileData);
		fclose(fileTrace);
		printf("%s\n", textFileData );
	}
	else{
		printf("shit\n");
	}
	
	//printf("err\n");
	count = 0;
	//fclose(fp);
	
	//fprintf(fileTrace,input);
	//fprintf(fileTrace,".\n\n");
	
	//fprintf(fileTrace, textFileData);
	//printf("output\n");
	count = 0;
	//fclose(fp);

}

