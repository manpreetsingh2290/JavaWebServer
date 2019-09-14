#include <stdio.h>
#include <stdlib.h>
#include <string.h>



int main (int argc, char *argv[])
{


	 char str[9999];
	 char line [200];
	 int count=0;
	 while(fgets(line,  sizeof(line), stdin))
	 {
		 if(count==0)
		 {
			 strcpy(str, line);
		 }
		 else
		 {
			 strcat(str, line);
		 }

		 //printf("%s\n",line );
		 count++;
	 }



	printf("%s\n","hello" );
	printf("%s\n",str );



}
