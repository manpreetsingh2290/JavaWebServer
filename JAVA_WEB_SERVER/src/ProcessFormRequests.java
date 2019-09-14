import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;


public class ProcessFormRequests {
	
	private static final String PATH = "src/HTML_FILES";

	public File findResource(String resource, String formData)
	{
		HashMap<String,String> formDataMap= new HashMap<String,String>();
		
		File file=null;
		if(formData!=null)
		{			
			String dataPair[]=null;
			String str[]=formData.split("&");
			for(int i=0;i<str.length;i++)
			{
				dataPair=str[i].split("=");
				
				if(dataPair.length>1)
				{
					formDataMap.put(dataPair[0], dataPair[1]);
				}
				else if (dataPair.length==1)
				{
					formDataMap.put(dataPair[0], null);
				}
				else
				{
					return null;
				}
				
			}
			
			file=processFormData(resource, formDataMap);
			
		}
		return file;
	}
	
	public File processFormData(String resource,HashMap<String,String> formDataMap)
	{
		resource= resource.replace("/", "");
		File file=null;
		File tempFile=null;
		if("studentinfopage".equalsIgnoreCase(resource))
		{
			try {
			StudentData data= new StudentData();			
			
			data.setFirstName(formDataMap.get("firstname"));
			data.setLastName(formDataMap.get("lastname"));
			
			if( checkAndSaveStudentData(data) ) {
				
				file = new File(PATH+"\\InstructorTemplate.html");
				tempFile= new File(PATH+"\\TempFile.html");
				
				file_copy(file, tempFile);
				
				dynamic_html_writer(PATH+"\\TempFile.html",data);
				
				//CHECKING IF SEARCHED STUDENT IS ALREADY GRADED OR NOT
				
				String marks=checkStudentGrades(data);	
				
				if( marks !=null) {
					dynamic_html_writer_value(PATH+"\\TempFile.html", marks);
				}
			}
			else {
				
				tempFile= new File(PATH+"\\studentinfo.html");
				
			}
			
			
			
			}
			catch (Exception e) {
				e.printStackTrace();
			}					
			 
			
			return tempFile;
			
		}
		else if("studentgradesubmit".equalsIgnoreCase(resource))
		{	String marks=null;
			String name=formDataMap.get("StudentName");
			
			StudentData data= new StudentData();						
			data.setFirstName(name);
			data.setLastName("");
			data.setGrades(formDataMap.get("Grades"));
			
			
			saveStudentGrades(data);
			
			
			file= new File(PATH+"\\studentgradesaved.html");
			return file;
		}
		else
		{
			
		}
		return file;
	}
	public boolean checkAndSaveStudentData(StudentData data)
	{
		String studentInfoFile = "src/DatabaseFiles/studentlist.txt";
		boolean entryFound=false;
		try {
		    BufferedReader in = new BufferedReader(new FileReader(studentInfoFile));
		    BufferedWriter out = new BufferedWriter(new FileWriter(studentInfoFile,true));
		    String str;
		    String studentName[];	    
		    while ((str = in.readLine()) != null)		    	
		    {		    	
		    	studentName=str.split(" ");
		    	if(studentName[0].equalsIgnoreCase(data.getFirstName()) 
		    			&& studentName[1].equalsIgnoreCase(data.getLastName()))
		    	{
		    		entryFound= true;
		    		break;
		    	}
		    }
		    in.close();
		    
		    if(!entryFound)
		    {
		    	out.write("\n"+data.getFirstName()+" "+data.getLastName());
		    	
		    }
		    out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return entryFound;
	}
	
	
	public void file_copy(File src, File dest) throws Exception {
		FileChannel source = new FileInputStream(src).getChannel();
		FileChannel destination = new FileOutputStream(dest).getChannel();
		destination.transferFrom(source, 0, source.size());
		source.close();
		destination.close();
	}
	
	public void dynamic_html_writer(String filePath,StudentData data) throws Exception {
		Path path = Paths.get(filePath);
		Charset charset = StandardCharsets.UTF_8;

		String content = new String(Files.readAllBytes(path), charset);
		content = content.replaceAll("#StudentName#", data.getFirstName()+" "+data.getLastName());
		content = content.replaceAll("#StudentResource#",data.getFirstName()+"_"+data.getLastName()+".txt");
		Files.write(path, content.getBytes(charset));
	}
	
	// WRITER VALUE FOR EXISTING GRADES
	public void dynamic_html_writer_value(String filePath,String marks) throws Exception {
		Path path = Paths.get(filePath);
		Charset charset = StandardCharsets.UTF_8;

		String content = new String(Files.readAllBytes(path), charset);
		content = content.replaceAll("#StudentGrade#","value="+ marks);
		
		Files.write(path, content.getBytes(charset));
	}
	
	// CHECK IF STUDENT IS ALREADY GRADED OR NOT
	public String checkStudentGrades(StudentData data) {
		String studentInfoFile = "src/DatabaseFiles/studentgrades.txt";
		
		String studentName = data.getFirstName()+" "+data.getLastName();
		String marks=null;
		try {
		    BufferedReader in = new BufferedReader(new FileReader(studentInfoFile));
		   
		    String str;
		    String student[];	    
		    while ((str = in.readLine()) != null)		    	
		    {		    	
		    	if( ( str.toLowerCase() ).contains( studentName.toLowerCase() ) )
		    	{
		    		marks = str.split(",")[1];
		    		return marks;
		    	}
		    }
		    in.close();
		   
		} catch (Exception e) {
			e.printStackTrace();
		}
		return marks;
	}
	
	
	public boolean saveStudentGrades(StudentData data)
	{
		String studentInfoFile = "src/DatabaseFiles/studentgrades.txt";
		boolean entryFound=false;
		try {
			BufferedReader in = new BufferedReader(new FileReader(studentInfoFile));
		    BufferedWriter out = new BufferedWriter(new FileWriter(studentInfoFile,true));	
		    
		    String str;
		    String studentName = data.getFirstName().replace("+", " ");
		    String student[];	    
		    while ((str = in.readLine()) != null)		    	
		    {		    	
		    	if( ( str.toLowerCase() ).contains( studentName.toLowerCase() ) )
		    	{
		    		in.close();
		    		Path path = Paths.get(studentInfoFile);
		    		Charset charset = StandardCharsets.UTF_8;

		    		String content = new String(Files.readAllBytes(path), charset);
		    		content = content.replaceAll(str, data.getFirstName().replace("+", " ")+","+data.getGrades());
		    		
		    		Files.write(path, content.getBytes(charset));
		    		return true;
		    	}
		    }
		    //in.close();
		    
		    out.write("\n"+data.getFirstName().replace("+", " ")+","+data.getGrades());
		    out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return entryFound;
	}
}
