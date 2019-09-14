import sys
import os
cwd = os.path.abspath(os.path.dirname(__file__))
#print(cwd)
request=sys.stdin.read().splitlines()
#print(request)


def loginCheck(ID,password):
	loginFile = open(cwd+"\\logindetails")
	for line in loginFile:
		if line.__contains__(password):
			#print( str(len(password))+" "+  str(len(line.split(",")[1].split(" ")[0]) ))
			#print(str(len(ID))+" "+str(len(line.split(",")[0])))
			if ID == line.split(",")[0] and password == line.split(",")[1].split(" ")[0]:
				print("Set-Cookie: identity="+ID)
				if(ID!="777"):
					successfile = open(cwd+"\\studentpage.html")
					for l in successfile:
						print(l,end='')
				else:
					for l in open(cwd+"\\instructorpage.html"):
						print(l,end='')		
				return;
	file404 = open(cwd+"\\404.html")
	print("FAILED LOGIN")
	for line in file404:
		print(line,end='')			

for item in request:
	if item.__contains__("UwinID") and item.__contains__("password"):
		ID =item.split("&")[0].split("=")[1] 
		password=item.split("&")[1].split("=")[1].strip()
		#print(ID+" "+password)
		#print("Set-Cookie: identity="+ID)
		loginCheck(ID,password)
		break;
