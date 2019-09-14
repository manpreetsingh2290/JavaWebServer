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
path = cwd.split("\\")
path=path[:-1]
path = "\\".join(path)+"\\static\\studentfiles"

#---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------#

def postFilewriter(cwd,fullname):
	marks=''
	gradefile = open(cwd + "\\studentgrades.txt","r")		# accessing grade file
	for line in gradefile:
		if( line.__contains__(fullname)):
			#print(line)
			list = line.split(",")
			marks = list[1]
			#print(marks)
			gradefile.close()
			break;
			#sys.stdout.flush()
			#print(line)
			#sys.stdout.flush()
	if not marks :
		notfound = open(cwd+"\\404.html")				# accessing 404 file if student not found
		for line in notfound:
			print(line,end='')

	else :

		file = open(cwd + "\\InstructorTemplate.html")		#dynamic page writing when student is found
		for line in file:	
			line=line.replace("#StudentName#",fullname)
			fname = fullname.replace(' ','_')
			p=path+"\\"+fname
			files=[]
			if os.path.isdir(p):
				files=os.listdir(p)
			if not files:
				line=line.replace("#StudentResource#","404.html")
			else:	
				line=line.replace("#StudentResource#","\\studentfiles\\"+fname+"\\"+files[0])
			line=line.replace("#StudentGrade#",marks)
			print(line,end='')		

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


	if item.__contains__("firstname") and cookieCheck(cook):
		fullname =item.split("&")[1].split("=")[1] +" "+ item.split("&")[0].split("=")[1]
		#print(fullname,end="")
		postFilewriter(cwd,fullname)
		exit()
for line in open(cwd+"\\login.html","r"):
	print(line,end='') 