import sys
import os
cwd = os.path.abspath(os.path.dirname(__file__))
#print(cwd)
#request=sys.stdin.readlines()

#endpoint = ''
# i=0

request=sys.stdin.read().splitlines()
# for line in iter(input,""):
#  	#print(line)
#  	request+=line+"\n"

#---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------#

def gradeUpdater(cwd,fullname,updatedMarks):
	marks=''
	gradefile = open(cwd + "\\studentgrades.txt","r")		# accessing grade file
	for line in gradefile:
		if( line.__contains__(fullname)):
			#print(line)
			list = line.split(",")
			marks = list[1]
			#print(marks)
			break;
			#sys.stdout.flush()
			#print(line)
			#sys.stdout.flush()
			
	if marks == '\n':
		#print("no marks")		
		gradefile = open(cwd + "\\studentgrades.txt","r")		# accessing grade file
		f = gradefile.read()
		f=f.replace(fullname+",",fullname+","+updatedMarks+"\n")
		gradefile.close()
		#print(f)
		#os.remove(cwd + "\\HTML_FILES\\studentgrades.txt")
		gradefile = open(cwd + "\\studentgrades.txt","w")
		gradefile.write(f)
		gradefile.close()
	else:
		#print("marks")
		gradefile = open(cwd + "\\studentgrades.txt","r")		# accessing grade file
		f = gradefile.read()
		gradefile.close()
		#print(f)
		#os.remove(cwd + "\\HTML_FILES\\studentgrades.txt")
		f=f.replace(fullname+","+marks,fullname+","+updatedMarks+"\n")
		gradefile = open(cwd + "\\studentgrades.txt","w")
		gradefile.write(f)	
	file = open(cwd + "\\studentgradesaved.html","r")
	print(file.read())
				#sys.stdout.flush()
				#print(line)
				#sys.stdout.flush()
def cookieCheck(cook):
	loginFile = open(cwd+"\\logindetails")
	for line in loginFile:
		if line.__contains__(cook) and cook == "777":
			return True
	return False	

#---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------#
cook="-1"
for item in request:
	if item.__contains__("Cookie"):
		if(item.__contains__("identity")):
			cook = item.split("identity")[1].split("=")[1].strip()
			#print(cook)

	if item.__contains__("Grades") and cookieCheck(cook):
		updatedMarks=item.split("&")[0].split("=")[1]
		fullname =item.split("&")[1].split("=")[1].split("+")[0]+" "+item.split("&")[1].split("=")[1].split("+")[1]
		#print(updatedMarks+" "+fullname)
		gradeUpdater(cwd,fullname,updatedMarks)
		exit()
for line in open(cwd+"\\login.html","r"):
	print(line,end='')
