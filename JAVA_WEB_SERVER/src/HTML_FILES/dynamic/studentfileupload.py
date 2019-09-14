import sys
import os
import codecs
cwd = os.path.abspath(os.path.dirname(__file__))
#print(cwd)
request=sys.stdin.read().splitlines()

def cookieCheck(cook):
	loginFile = open(cwd+"\\logindetails")
	for line in loginFile:
		if line.__contains__(cook):
			return True
	return False	

#---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------#
cook="-1"
for item in request:
	if item.__contains__("Cookie"):
		if(item.__contains__("identity")):
			cook = item.split("identity")[1].split("=")[1].strip()
			if cookieCheck(cook) == False:
				#print("yes")
				for line in open(cwd+"\\login.html","r"):
					print(line,end='')
				exit()
		else:
			#print("yesy")
			for line in open(cwd+"\\login.html","r"):
				print(line,end='')
			exit()		



path = cwd.split("\\")
path=path[:-1]
path = "\\".join(path)+"\\static\\studentfiles"
#print(path)
#request=sys.stdin.readlines()

#endpoint = ''
# i=0



#print(request)

fileLocs={}
sName=''
for line in request:
	
	if(line.__contains__("Uploaded-Files::")):
		fileLocs=line.split("::")[1].split(";")
	if(line.__contains__("Form-Data::")):
		sName=line.split("::")[1].split("=")[1]
		#print(line)
	
# print("Student name= " +sName)
# for f in fileLocs :
# 	if f !='':
# 		print("files= "+f)



flag=0
if not fileLocs:
	print('''<html><body> <p>NO FILES UPLOADED</p> <a href="/">Go To Homepage</a>  </body></html>''')
	exit()
elif sName != '' and fileLocs:
	sName = sName.split(" ")[1]+"_"+sName.split(" ")[0]
	#print(sName)
	for folder in os.listdir(path):
		if sName == folder:
			#p=path+"\\"+sName
			#print(p)
			flag=1
			for f in fileLocs:
				p=path+"\\"+sName
				if f!='':
					fname= f.split("\\")[-1]
					p+="\\"+fname
					print(p)
					nf = open(p,"wb")
					l = open(f,"rb")
					nf.write(l.read())
					nf.close()
			exit()			

	if flag ==0:
		p=path+"\\"+sName
		os.mkdir(p)
		for f in fileLocs:
				p=path+"\\"+sName
				if f!='':
					fname= f.split("\\")[-1]
					p+="\\"+fname
					print(p)
					nf = open(p,"wb")
					l = open(f,"rb")
					nf.write(l.read())
					nf.close()
	

