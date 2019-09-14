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
			 strcat(str, "<BR>");
		 }

		 //printf("%s\n",line );
		 count++;
	 }

	printf("%s\n","<html lang=\"en\"><head>    <meta charset=\"UTF-8\">    <title>Page From C</title></head><body><div align=\"left\"><p>" );
	printf("%s\n","<b>Printing HTTP Header recived from server</b><BR>" );
	printf("%s\n",str );
	printf("%s\n","</p></div></body></html>" );




}
