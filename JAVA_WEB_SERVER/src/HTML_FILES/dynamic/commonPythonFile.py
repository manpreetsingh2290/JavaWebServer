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
			line=line.replace("#StudentResource#",fname+".txt")
			line=line.replace("#StudentGrade#",marks)
			print(line,end='')		


#---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------#
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


#---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------#

resourse=""
for item in request:
    resourse=item.split(" ")[1]
    break

if(resourse.__contains__("studentinfopage")):
    for item in request:
        if item.__contains__("firstname"):
            fullname =item.split("&")[1].split("=")[1] +" "+ item.split("&")[0].split("=")[1]
            #print(fullname,end="")
            postFilewriter(cwd,fullname)
            break;
elif(resourse.__contains__("studentgradesubmit")):
    for item in request:
        if item.__contains__("Grades"):
            updatedMarks=item.split("&")[0].split("=")[1]
            fullname =item.split("&")[1].split("=")[1].split("+")[0]+" "+item.split("&")[1].split("=")[1].split("+")[1]
            #print(updatedMarks+" "+fullname)
            gradeUpdater(cwd,fullname,updatedMarks)
            break;
else:
    print("Invalid Request")
