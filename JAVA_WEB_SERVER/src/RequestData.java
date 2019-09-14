import java.util.ArrayList;
import java.util.HashMap;

public class RequestData {

	private String methodType;
	private String resourceName;
	HashMap<String,String> headerDataMap = null;
	private ArrayList<String> formData=null;
	private ArrayList<FileFormDataInfo> fileData= null;
	private String requestStr;
	
	
	
	public String getRequestStr() {
		return requestStr;
	}
	public void setRequestStr(String requestStr) {
		this.requestStr = requestStr;
	}
	public String getMethodType() {
		return methodType;
	}
	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public ArrayList<String> getFormData() {
		return formData;
	}
	public void setFormData(ArrayList<String> formData) {
		this.formData = formData;
	}
	public ArrayList<FileFormDataInfo> getFileData() {
		return fileData;
	}
	public void setFileData(ArrayList<FileFormDataInfo> fileData) {
		this.fileData = fileData;
	}
	public HashMap<String, String> getHeaderDataMap() {
		return headerDataMap;
	}
	public void setHeaderDataMap(HashMap<String, String> headerDataMap) {
		this.headerDataMap = headerDataMap;
	}
	
	
	
	
}
