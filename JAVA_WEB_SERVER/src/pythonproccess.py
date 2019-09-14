import sys
import os
import base64
cwd = os.path.abspath(os.path.dirname(__file__))
#print(cwd)

#---------------------------------------------------------------------------------------------------------------------------------------#
def postFilewriter(cwd,fullname):
	marks=''
	gradefile = open(cwd + "\\HTML_FILES\\studentgrades.txt","r")		# accessing grade file
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
		notfound = open(cwd+"\\HTML_FILES\\404.html")				# accessing 404 file if student not found
		for line in notfound:
			print(line,end='')

	else :

		file = open(cwd + "\\HTML_FILES\\InstructorTemplate.html")		#dynamic page writing when student is found
		for line in file:	
			line=line.replace("#StudentName#",fullname)
			fname = fullname.replace(' ','_')
			line=line.replace("#StudentResource#",fname+".txt")
			line=line.replace("#StudentGrade#",marks)
			print(line,end='')		


#---------------------------------------------------------------------------------------------------------------------------------------#

#---------------------------------------------------------------------------------------------------------------------------------------#


def gradeUpdater(cwd,fullname,updatedMarks):
	marks=''
	gradefile = open(cwd + "\\HTML_FILES\\studentgrades.txt","r")		# accessing grade file
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
		gradefile = open(cwd + "\\HTML_FILES\\studentgrades.txt","r")		# accessing grade file
		f = gradefile.read()
		f=f.replace(fullname+",",fullname+","+updatedMarks+"\n")
		gradefile.close()
		#print(f)
		#os.remove(cwd + "\\HTML_FILES\\studentgrades.txt")
		gradefile = open(cwd + "\\HTML_FILES\\studentgrades.txt","w")
		gradefile.write(f)
		gradefile.close()
	else:
		#print("marks")
		gradefile = open(cwd + "\\HTML_FILES\\studentgrades.txt","r")		# accessing grade file
		f = gradefile.read()
		gradefile.close()
		#print(f)
		#os.remove(cwd + "\\HTML_FILES\\studentgrades.txt")
		f=f.replace(fullname+","+marks,fullname+","+updatedMarks+"\n")
		gradefile = open(cwd + "\\HTML_FILES\\studentgrades.txt","w")
		gradefile.write(f)	
	file = open(cwd + "\\HTML_FILES\\studentgradesaved.html","r")
	print(file.read())
				#sys.stdout.flush()
				#print(line)
				#sys.stdout.flush()


#---------------------------------------------------------------------------------------------------------------------------------------#
#print(len(sys.argv))
#print(type(sys.argv[1]))

requestData = sys.argv[1].split("\\r\\n")

requestLine = requestData[0].split(' ')

#print(len(requestData))
#print(requestLine)

if requestLine[0].lower() == "GET".lower():
	if(requestLine[1].lower() == "/".lower()):
		indexFile = open(cwd+"\\HTML_FILES\\index.html","r")
		#f = indexFile.read()
		#b = bytearray(f)
		#print(b[0])
		for byte in indexFile:
			print(byte,end='')
	else:
		fileList = os.listdir(cwd+"\HTML_FILES")
		name="404.html"
		for files in fileList:
			if( files == (requestLine[1].replace("/","")) ):
				name = files
				break;	
		f = open( cwd + "\\HTML_FILES\\" + name ,"r")
		for byte in f:
			print(byte,end='')
					
elif requestLine[0].lower() == "POST".lower():
	action = requestLine[1].replace("/","")
	fullname=""
	if( action == "studentinfopage" ):
		#print('yes')
		for item in requestData:
			if item.__contains__("firstname"):
				fullname =item.split("&")[1].split("=")[1] +" "+ item.split("&")[0].split("=")[1]
				#print(fullname)
				postFilewriter(cwd,fullname)
				break;
	elif( action == "studentgradesubmit" ):
		#print(requestData)
		for item in requestData:
			if item.__contains__("Grades"):
				updatedMarks=item.split("&")[0].split("=")[1]
				fullname =item.split("&")[1].split("=")[1].split("+")[0]+" "+item.split("&")[1].split("=")[1].split("+")[1]
				#print(updatedMarks+" "+fullname)
				gradeUpdater(cwd,fullname,updatedMarks)
				break;

#'\n'.join(iter(input, abc))

#print(abc)




'''
sys.exit();

fullname = sys.argv[1]+" "+sys.argv[2]
marks=""
gradefile = open("C:\\Users\\vivek\\eclipse-workspace\\java server demo\\src\\HTML_FILES\\studentgrades.txt","r")		# accessing grade file
for line in gradefile:
	if( line.__contains__(fullname)):
		list = line.split(",")
		marks = list[1]
		#print(marks)
		#sys.stdout.flush()
		#print(line)
		#sys.stdout.flush()


elif( requestLine[1].lower() == "/logo.jpg"):
		f = open( cwd + "\HTML_FILES\logo.jpg","r")
		for byte in f:
			print(byte,end='')

if not marks:
	notfound = open("C:\\Users\\vivek\\eclipse-workspace\\java server demo\\src\\HTML_FILES\\404.html")				# accessing 404 file if student not found
	for line in notfound:
		print(line,end='')

else :

	file = open("C:\\Users\\vivek\\eclipse-workspace\\java server demo\\src\\HTML_FILES\\InstructorTemplate.html")		#dynamic page writing when student is found
	for line in file:	
		line=line.replace("#StudentName#",fullname)
		line=line.replace("#StudentGrade#",marks)
		print(line,end='')

'''