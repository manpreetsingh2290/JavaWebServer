import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class JavaServer implements Runnable {

	private static final String PATH = "src/HTML_FILES";
	private static final String PATH_EXECUTABLE = "src/HTML_FILES/dynamic";
	private static final int PORT = 8081;
	private static final HashMap<String, String> config = getConfig();
	private static final HashMap<String, String> executableMapping = getExecutableMapping();
	private static String cCompilerLocation = "E:\\Uwindsor_Work\\Softwares_Tools\\C_Compliers\\gcc\\bin\\gcc";
	private static String uploadedFilePath = "\\src\\UploadedFiles\\";
	
	private Socket inboSocket;
	
	public JavaServer(Socket soc) {
		inboSocket = soc;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(config);
		if (config.containsKey("Programming Language"))									
		{
			System.out.println("Starting server");
			ServerSocket serverSocket = new ServerSocket(PORT);
			
			while (true) {
				JavaServer myServer = new JavaServer(serverSocket.accept());
				
				//Create new thread for each new request
				Thread thread = new Thread(myServer);
				thread.start();
			}	
			
		}
		else
		{
			throw new Exception("Programming language is not configured");
		}

	}
	
	@Override
	public void run() {
		
		try
		{
			initServer(PORT);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private  void initServer(int port2) throws Exception {

		
		boolean singleExecuatble = false;
		if (config.get("Single Executable") != null && "True".equalsIgnoreCase(config.get("Single Executable"))) {
			singleExecuatble = true;
		}			
			//Creating data input stream for incoming request data
			DataInputStream in_binary = new DataInputStream(new BufferedInputStream(inboSocket.getInputStream()));
			
			BufferedOutputStream response_stream = new BufferedOutputStream(inboSocket.getOutputStream());
			PrintWriter response = new PrintWriter(new OutputStreamWriter(inboSocket.getOutputStream()));
			
			//Reading and parsing the HTTP request
			RequestData requestData = readHTTPRequest(in_binary);			

			String request=requestData.getRequestStr();
			ArrayList<FileFormDataInfo> fileInfoList= requestData.getFileData();
			ArrayList<String>  formDataInfo= requestData.getFormData();
			String methodType=requestData.getMethodType();
			String resoureName= requestData.getResourceName();
			
			System.out.println("oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");
			String filesStr = "";
			String formFieldStr = "";
			if (fileInfoList != null && fileInfoList.size() > 0) {
				for (FileFormDataInfo fileInfo : fileInfoList) {
					filesStr += fileInfo.getUploadedLocation() + ";";
				}
				request += "\n" + "Uploaded-Files::" + filesStr;
			}
			int count = 0;
			if (formDataInfo != null && formDataInfo.size() > 0) {
				for (String str : formDataInfo) {
					if (count > 0) {
						formFieldStr += "&";
					}
					formFieldStr += str;
					count++;
				}
				request += "\n" + "Form-Data::" + formFieldStr;
			}

			System.out.println("Final Request data to be passed to website:" + request);

			System.out.println("-----------------DONE----------------------");
			if (request != null && !"".equalsIgnoreCase(request) && methodType != null ) {

				// String[] req_line = (new Scanner(request)).nextLine().split(" ");
				// String resourceName=req_line[1];

				System.out.println("----------------------writing to client----------------------");

				if (methodType.equalsIgnoreCase("POST") || methodType.equalsIgnoreCase("GET")) {

					if (resoureName.equalsIgnoreCase("/")) {
						File file = new File(PATH + "/static/index.html");
						int fileLength = (int) file.length();
						response_header_writer("HTTP/1.1 200 OK", "text/html", fileLength, response,null);
						response_file_writer(file, response_stream);
					} else {
						File staticFile = getFileForRequest(resoureName, PATH + "/static");
						if (staticFile != null && !staticFile.getName().equalsIgnoreCase("404.html")) {
							System.out.println("static found");
							int len = (int) staticFile.length();
							response_header_writer("HTTP/1.1 200 OK", "text/html", len, response,null);
							response_file_writer(staticFile, response_stream);
						} else {
							// File dynamicFile = getFileForRequest(req_line[1], PATH+"/dynamic");
							File executableFile = getExecutableFileFromMapping(resoureName, PATH_EXECUTABLE,
									singleExecuatble);
							if (executableFile == null) {
								executableFile = getFileForRequest(resoureName, PATH + "/dynamic");
							}

							if (executableFile != null && !executableFile.getName().equalsIgnoreCase("404.html")) {
								System.out.println("in dynamic");
								if ("Python".equalsIgnoreCase(config.get("Programming Language"))) {

									processRequestByPython(response, request, executableFile);
								} else if ("C".equalsIgnoreCase(config.get("Programming Language"))) {

									processRequestByC(response, request, executableFile);
								}
							} else {
								System.out.println("static & dynamic both not found");
								int len = (int) staticFile.length();
								response_header_writer("HTTP/1.1 200 OK", "text/html", len, response,null);
								response_file_writer(staticFile, response_stream);
							}

						}

					}

				}
				// response_stream.close();
				// response.close();
				inboSocket.close();

			
		}

	}

	private static File getFileForRequest(String resource, String path) {

		System.out.println("resource::::"+resource);
		
		if(resource.contains("%20"))
		{
			resource= resource.replaceAll("%20", " ");
		}
		
		System.out.println("resource after::::"+resource);
		
		//resource = resource.replace("/", "");
		String[] r = resource.split("/");
		if(r.length<=2){
			resource = r[1];
		if (resource.contains("."))
			resource = resource.substring(0, resource.lastIndexOf('.'));
		
		
		
		
		// System.out.println(resource);
		File folder = new File(path);
		File files[] = folder.listFiles();
		for (File file : files) {

			// System.out.println(file.getName());
			String filename = null;
			if (file.getName().contains(".") && file.isFile())
				filename = file.getName().substring(0, file.getName().lastIndexOf('.'));

			if (resource.equalsIgnoreCase(filename)) {
				System.out.println("Found File: " + file.getName());
				return file;
			}

		}
		
		}
		else {
			String p=path;
			for(int i =1;i<r.length-1;i++) {
				File[] folder = new File(p).listFiles();
				for(File f : folder) {
					if(f.isDirectory() && f.getName().equalsIgnoreCase(r[i]) ) {
						
						p+="/"+r[i];
						break;
					}
				}
				System.out.println(p);	
			}
			
			resource=r[r.length-1];
			if (resource.contains("."))
				resource = resource.substring(0, resource.lastIndexOf('.'));
			// System.out.println(resource);
			File folder = new File(p);
			File files[] = folder.listFiles();
			for (File file : files) {

				// System.out.println(file.getName());
				String filename = null;
				if (file.getName().contains(".") && file.isFile())
					filename = file.getName().substring(0, file.getName().lastIndexOf('.'));

				if (resource.equalsIgnoreCase(filename)) {
					System.out.println("Found File: " + file.getName());
					return file;
				}

			}
		}
		
		return new File(PATH + "\\404.html");
	}

	private static void response_header_writer(String status, String content_type, int content_length,
			PrintWriter response,String cookie) {
		if ((status != null) && (content_length != 0) && (content_type != null)) {
			response.println("HTTP/1.1 200 OK");
			response.println("Content-type: " + content_type);
			response.println("Content-length: " + content_length);
			if(cookie!=null)
			response.println(cookie);
			response.println();
			response.flush();

		} else {
			System.out.println("Check parameters for null!");
		}
	}

	private static void response_file_writer(File file, BufferedOutputStream filewriter) {
		try {

			int filelength = (int) file.length();
			System.out.println("filelength:::"+ file.length());

			FileInputStream fileInputStream = new FileInputStream(file);

			byte[] fileData = new byte[filelength];

			fileInputStream.read(fileData);
			filewriter.write(fileData);
			// filewriter.write(fileData, 0, filelength); // WRITING FILE IN FORM OF BYTES
			// IN RESPONSE

			filewriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static HashMap<String, String> getConfig() {
		HashMap<String, String> config = new HashMap<String, String>();

		File f = new File("src/config.txt");
		if (f != null) {
			Scanner sc;
			try {
				sc = new Scanner(f);
				while (sc.hasNextLine()) {
					String line = sc.nextLine();

					if (!line.contains(":")) {
						System.err.println("Problem with Config file");
						sc.close();
						return null;
					}

					String[] excPath = line.split(" : ");
					if (excPath.length == 2) {

						config.put(excPath[0], excPath[1]);

					}

				}
				sc.close();
				return config;
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}

		}

		return null;
	}

	public static void processRequestByPython(PrintWriter response, String request, File dynamicFile) throws Exception {
		String pythonFilePath = "";

		pythonFilePath = System.getProperty("user.dir") + "\\src\\HTML_FILES\\dynamic\\" + dynamicFile.getName();

		// Process execute = Runtime.getRuntime().exec("python
		// "+"\""+System.getProperty("user.dir")+"\\src\\HTML_FILES\\dynamic\\"+dynamicFile.getName());
		Process execute = Runtime.getRuntime().exec("python " + "\"" + pythonFilePath);

		BufferedWriter processin = new BufferedWriter(new OutputStreamWriter(execute.getOutputStream()));
		BufferedReader processResponse = new BufferedReader(new InputStreamReader(execute.getInputStream()));
		// BufferedReader errorresponse = new BufferedReader(new
		// InputStreamReader(execute.getErrorStream()));

		// char[] error = new char[2048];
		// errorresponse.read(error);
		// System.out.println(new String(error).trim());

		processin.write(request);
		processin.close();

		char[] c = new char[4048];
		int len = processResponse.read(c);
		// System.err.println(new String(c).trim());

		BufferedInputStream pr = new BufferedInputStream(execute.getInputStream());
		// byte[] b = new byte[8888];
		// pr.read(b);

		// char[] c = new char[4048];
		// processResponse.read(c);

		String ss = new String(c).trim();
		if( ss.contains("Set-Cookie") )
		{	
		response_header_writer("HTTP/1.1 200 OK", "text/html", ss.substring(ss.indexOf('\n')+1).length(), response,ss.substring(0, ss.indexOf('\n')+1));
		ss=ss.substring(ss.indexOf('\n')+1);
		System.out.println(ss);
		}
		else
			response_header_writer("HTTP/1.1 200 OK", "text/html", ss.length(), response,null);
		response.println(ss);

		response.flush();

		// response_stream.write(b);
		// response_stream.flush();

		System.out.println("-------------------------------------------------------");
		// System.out.println(ss.trim());

	}

	public static void processRequestByC(PrintWriter response, String request, File dynamicFile) throws Exception {

		System.out.println("C");
		System.out.println(System.getProperty("user.dir"));
		Process compile = Runtime.getRuntime()
				.exec(cCompilerLocation + " -o " + "\"" + System.getProperty("user.dir")
						+ "\\src\\HTML_FILES\\dynamic\\abc" + "\" " + "\"" + System.getProperty("user.dir")
						+ "\\src\\HTML_FILES\\dynamic\\" + dynamicFile.getName() + "\"");
		compile.waitFor();

		Process excecute = Runtime.getRuntime()
				.exec("\"" + System.getProperty("user.dir") + "\\src\\HTML_FILES\\dynamic\\abc.exe" + "\"");
		BufferedReader processResponse = new BufferedReader(new InputStreamReader(excecute.getInputStream()));
		BufferedWriter processin = new BufferedWriter(new OutputStreamWriter(excecute.getOutputStream()));

		processin.write(request);
		// //processin.flush();
		processin.close();

		char[] c = new char[4048];
		int len = processResponse.read(c);

		String ss = new String(c).trim();
		System.out.println("C program output:");
		System.out.println(ss);
		response_header_writer("HTTP/1.1 200 OK", "text/html", len, response,null);
		// System.out.println(ss);

		response.println(ss);

		response.flush();

	}

	private static HashMap<String, String> getExecutableMapping() {
		HashMap<String, String> config = new HashMap<String, String>();

		File f = new File("src/executableMapping.txt");
		if (f != null) {
			Scanner sc;
			try {
				sc = new Scanner(f);
				while (sc.hasNextLine()) {
					String line = sc.nextLine();

					if (!line.contains(":")) {
						System.err.println("Problem with executableMapping.txt file");
						sc.close();
						return null;
					}

					String[] excPath = line.split(" : ");
					if (excPath.length == 2) {

						config.put(excPath[0], excPath[1]);

					}

				}
				sc.close();
				return config;
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}

		}

		return null;
	}

	public static File getExecutableFileFromMapping(String resource, String folderPath, boolean singleExecuatble) {
		try {
			System.out.println(resource);
			resource = resource.replace("/", "");
			if (resource.contains("."))
				resource = resource.substring(0, resource.lastIndexOf('.'));
			// System.out.println(resource);
			String executableFileName = "";
			//If only single executable configured
			if (singleExecuatble) {
				executableFileName = config.get("Executable_File_Name");
			} else {
				//Get the executable for the respective request resource
				executableFileName = executableMapping.get(resource);
			}

			File file = new File(folderPath + "/" + executableFileName);
			if (file.exists()) {
				return file;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return null;

	}

	public static RequestData readHTTPRequest(DataInputStream dis) throws Exception {
		
		//Object to store parsed request data
		RequestData requestData = new RequestData();
		ArrayList<String> formData = new ArrayList<String>();
		//List to store uploaded multiple files data
		ArrayList<FileFormDataInfo> fileDataList = new ArrayList<FileFormDataInfo>();
		
		StringBuilder tempFullBodyStr= new StringBuilder("");
		String requestStr = "";
		
		//Read and parse the HTTP request header
		requestData =readHeaderData(dis,requestData);
		
		//Reading the key values pairs HTTP header data
		HashMap<String, String> headerDataMap = requestData.getHeaderDataMap();
		
		//Reading complete Header data to be sent to other executables created by other teams
		requestStr=requestData.getRequestStr();
		
		System.out.println("------------------------------Message Header Start---------------------------");
		System.out.println(requestData.getRequestStr());
		System.out.println("------------------------------Message Header End---------------------------");

		//Setting default content length
		int contentLength = 20048;
		String boundryString = "";
		boolean contiansBoundry = false;
		
		boolean containsMessageBody=false;
		//Reading the content length from the header map
		if (headerDataMap.containsKey("Content-Length")) {
			contentLength = Integer.parseInt(headerDataMap.get("Content-Length"));
			containsMessageBody=true;
		}
		//Reading the content type from the header map
		if (headerDataMap.containsKey("Content-Type")) {
			
			//If content type also includes boundary field
			if (headerDataMap.get("Content-Type").contains("boundary=")) {
				//Reading the boundary separator string value from content type field
				boundryString = headerDataMap.get("Content-Type").split(";")[1].split("=")[1];
				contiansBoundry = true;
			}

		}
		
		
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		byte databyte;
		String multipartDataDescStr="";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int ch;
		
		//Flag to track end of body
		boolean endOfBody=false;
		
		//If request contains message body as well as boundary (signifies it is a multipart data and contains files)
		if(containsMessageBody & contiansBoundry)
		{
				while (!endOfBody) {
					
					//Reading data one byte at a time
					ch = dis.read();
					
					//Writing the read byte to the byte array
					baos.write(ch);
					
					multipartDataDescStr = new String(baos.toByteArray());
					//Two consecutive carriage return ("\r\n\r\n") indicates that subpart's header data has been read
					if(multipartDataDescStr.contains("\r\n\r\n"))
					{
						System.out.println();
						
						//New byte array for body data
						baos = new ByteArrayOutputStream();
						String tempStr="";
						long numBytes=0;
						
						//Parse the header of subpart (of multipart data)
						FileFormDataInfo  fileDataInfo= parseMutipartDataDesc(multipartDataDescStr);
						
						System.out.println(":::::::::::::::::multipartDataDescStr Start :::::::::::::::::");
						System.out.println(multipartDataDescStr);
						System.out.println(":::::::::::::::::multipartDataDescStr End :::::::::::::::::");
						
						tempFullBodyStr.append(multipartDataDescStr);
						
						//If data type is a File
						if(FileFormDataInfo.DATA_TYPE_FILE.equals(fileDataInfo.getDataType()))
						{
								boolean endOfFileData=false;
								boolean isFileReceived=false;
								DataOutputStream fileWriterBin=null;
								System.out.println("Reading File Data for file:"+fileDataInfo.getFileName());
								if(fileDataInfo.getFileName()!=null && !"".equalsIgnoreCase(fileDataInfo.getFileName()))
								{
									isFileReceived=true;
									//Location where file will be uploaded/saved
									String uploadedLocation = System.getProperty("user.dir") + uploadedFilePath + fileDataInfo.getFileName();
									//System.out.println(uploadedLocation);
									fileWriterBin = new DataOutputStream(new FileOutputStream(uploadedLocation));
									fileDataInfo.setUploadedLocation(uploadedLocation);
									fileDataList.add(fileDataInfo);
								}
								
								
								numBytes=0;
								//int tempCount=0;
								boolean isNewLineSkipped=false;
								
								//Read file data byte by byte untill end of file is reached
								while(!endOfFileData)
								{
									//baos.size();
									databyte=dis.readByte();
									
									//Write the read byte to byte array output stream
									baos.write(databyte);
									
									//Byte counter
									numBytes++;
									
									//Convert the byte arry output stream to string to check the 
									//presence of new line character and end of boundary string
									tempStr= new String(baos.toByteArray());
									if(tempStr.contains("\n"))
									{							
										System.out.println(tempStr);
										//Appending to full body string for debugging purposes
										tempFullBodyStr.append(tempStr);
										
										//If end of entire body is reached (body boundary end string is read)
										if(tempStr.contains(boundryString+"--"))
										{
										  	endOfBody=true;
											endOfFileData=true;
											//System.out.println(tempCount);
										}
										//If boundary string encountered that  means end of one subpart
										else if(tempStr.contains(boundryString))
										{
											endOfFileData=true;
											
										}
										
										if(endOfFileData && isFileReceived)
										{
											//Closing the file writer stream
											fileWriterBin.close();
											System.out.println("File uploaded Sucessfully");
										}
										else if(isFileReceived)
										{
											
										//If file contains new line character only then write it to the file on local drive
										  if(isNewLineSkipped)
											{
											    
												fileWriterBin.write("\r\n".getBytes());
												isNewLineSkipped=false;
											}
											
											int len=0;
											//This new line character can be the end of file indicator also so writing only in next iteration
											if(tempStr.endsWith("\r\n"))
											{
												len="\r\n".getBytes().length;
												isNewLineSkipped=true;
											}
											
											//Writing the read file data in file saved at local drive
											fileWriterBin.write(baos.toByteArray(),0,(baos.size()- len));
										}
																		
										baos = new ByteArrayOutputStream();
									}									
									
								}								
							
						}
						//If data type is a form field
						else if(FileFormDataInfo.DATA_TYPE_FORM_FEILD.equals(fileDataInfo.getDataType()))
						{
							System.out.println("Reading Form Field Data for Field: "+fileDataInfo.getName());
							//formData.add(fileDataInfo.getName());
							boolean endOfFormData=false;
							baos = new ByteArrayOutputStream();
							String formFieldData="";
							numBytes=0;
							
							//Read form field data byte by byte untill end of form field is reached
							while(!endOfFormData)
							{
								//baos.size();
								
								databyte=dis.readByte();	
								
								//Write the read byte to byte array output stream
								baos.write(databyte);
								
								//Byte Counter
								numBytes++;
								
								//Convert the byte arry output stream to string to check the 
								//presence of new line character and end of boundary string
								tempStr= new String(baos.toByteArray());
								if(tempStr.contains("\n"))
								{							
									//System.out.println("Received Bytes: "+numBytes);
									System.out.println(tempStr);
									
									//Appending to full body string for debugging purposes
									tempFullBodyStr.append(tempStr);
									
									//If end of entire body is reached (body boundary end string is read)
									if(tempStr.contains(boundryString+"--"))
									{
									  	endOfBody=true;
									  	endOfFormData=true;
									}
									//If boundary string encountered that  means end of one subpart
									else if(tempStr.contains(boundryString))
									{
										endOfFormData=true;
										
									}
									
									if(!endOfFormData)
									{
										//Converting the byte array to form field string 
										formFieldData+=new String(baos.toByteArray());
									}
									else
									{
										//Adding read form field data to the form data list
										formData.add(fileDataInfo.getName()+"="+formFieldData);
										System.out.println("Field Data: "+fileDataInfo.getName()+"="+formFieldData);
									}
								
									//Creating new byte array stream for new form field data
									baos = new ByteArrayOutputStream();
								}
							}
						}
						
						
						//fileWriterBin.close();
						
						//Resetting to read  next subpart
						multipartDataDescStr="";
					}
				}
				
			
		}
		
			
		
			
		System.out.println("-------------------------------------Body data Start-------------------------------------");
		System.out.println(tempFullBodyStr);
		System.out.println("-------------------------------------Body data End-------------------------------------");
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		String bodyDataStr = "";
		byte bodyData[] = new byte[contentLength];
		//If body is present without boundary string that means there is no file data
		if(containsMessageBody && !contiansBoundry)
		{						
			while (bodyDataStr == "") {
				//Reading the entire body of HTTP request
				dis.read(bodyData);
				bodyDataStr = new String(bodyData).trim();
				if (!contiansBoundry) {
					requestStr += bodyDataStr;
				}
			}
		}
		
		
		requestData.setHeaderDataMap(headerDataMap);
		requestData.setFileData(fileDataList);
		requestData.setFormData(formData);
		requestData.setRequestStr(requestStr);

		return requestData;

	}
	
	public static RequestData readHeaderData(DataInputStream dis,RequestData requestData) throws IOException
	{
		String requestStr = "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String headerStr = "";
		HashMap<String, String> headerDataMap = new HashMap<String, String>();
		int ch;
		//HashMap<String, String> headerDataMap = new HashMap<String, String>();
		String methodType = "";
		String resourceName = "";
		int lineCount = 0;
		while ((ch = dis.read()) != -1) {
			baos.write(ch);
			if (ch == '\r') {
				ch = dis.read();
				
				if (ch == '\n') {
					headerStr += new String(baos.toByteArray());
					baos.close();

					//requestStr += headerStr;

					ch = dis.read();
					if (ch == '\r') {
						ch = dis.read();
						if (ch == '\n') {
							lineCount++;

							if (lineCount == 1) {
								if (headerStr.split(" ").length > 1) {
									methodType = headerStr.split(" ")[0];
									resourceName = headerStr.split(" ")[1];
								}

							}

							String headerLines[] = headerStr.split("\r");
							for (String line : headerLines) {
								String headerContent[] = line.split(":");
								if (headerContent != null && headerContent.length > 1) {
									headerDataMap.put(headerContent[0].replace('\n', ' ').trim(),
											headerContent[1].replace('\n', ' ').trim());
								}
							}

							// body part start
							break;
						}
					}

					baos = new ByteArrayOutputStream();
					// break;
				}

				baos.write(ch);
			}
		}
		
		requestData.setHeaderDataMap(headerDataMap);
		requestData.setRequestStr(headerStr);
		requestData.setMethodType(methodType);
		requestData.setResourceName(resourceName);
		return requestData;
	}
	
	public static FileFormDataInfo uploadFile(String fileName, byte[] fileDataByteArray) throws IOException
	{
		String uploadedLocation = uploadedFilePath + fileName;
		DataOutputStream fileWriterBin = new DataOutputStream(new FileOutputStream(uploadedLocation));
		fileWriterBin.write(fileDataByteArray);
		fileWriterBin.close();

		FileFormDataInfo fileInfo = new FileFormDataInfo();
		fileInfo.setFileName(fileName);
		fileInfo.setName(fileName);
		fileInfo.setUploadedLocation(uploadedLocation);
		
		return fileInfo;
	}
	
	//Method to parse the multipart data header information
	public static FileFormDataInfo parseMutipartDataDesc(String dataDescription)
	{
		//Creating object to store parsed data
		FileFormDataInfo dataDescInfo= new FileFormDataInfo();
		dataDescription = dataDescription.trim();

		//Read each header line in array
		String contentDescData[] = dataDescription.split("\r\n");
		String fileName = "";
		String formField = "";

		for (String str : contentDescData) {
			if ("".equalsIgnoreCase(str)) {
				continue;
			}
			String contentDescKeyValue[] = str.split(":");
			
			//If subpart header contains key 'Content-Disposition' and uploaded file's name is present
			if (contentDescKeyValue[0].contains("Content-Disposition")
					&& contentDescKeyValue[1].contains("form-data")
					&& contentDescKeyValue[1].contains("filename")) {
				String strArry[] = contentDescKeyValue[1].split(";");
				for (String strr : strArry) {
					if ("".equalsIgnoreCase(str)) {
						continue;
					}
					if (strr.contains("filename")) {
						
						//Getting the file name and removing the double quotes from name
						fileName = strr.split("=")[1].replace("\"", "");
						//itIsAFile = true;
						
						//Setting data type to file
						dataDescInfo.setDataType(FileFormDataInfo.DATA_TYPE_FILE);
						
						//Saving file name in object
						dataDescInfo.setFileName(fileName);
						break;
					}
				}
			} 
			//Else If subpart header contains key 'Content-Disposition' and only the form data is present
			else if (contentDescKeyValue[0].contains("Content-Disposition")
					&& contentDescKeyValue[1].contains("form-data")) {
				//Splitting the data separated by ';'
				String strArry[] = contentDescKeyValue[1].split(";");
				for (String strr : strArry) {
					if ("".equalsIgnoreCase(str)) {
						continue;
					}
					if (strr.contains("name")) {
						
						//Getting the form field name and removing the double quotes from name
						formField = strr.split("=")[1].replace("\"", "");
						//itIsAFormField = true;
						
						//Setting data type to form field
						dataDescInfo.setDataType(FileFormDataInfo.DATA_TYPE_FORM_FEILD);
						dataDescInfo.setName(formField);
						
						break;
					}
				}
			}

		}
		return dataDescInfo;
	}

}
