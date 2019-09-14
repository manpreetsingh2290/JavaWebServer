
public class FileFormDataInfo {

	private String dataType;
	private String fileName;
	private String uploadedLocation;
	private String name;
	public static String DATA_TYPE_FORM_FEILD="FORM_FIELD_DATA";
	public static String DATA_TYPE_FILE="FILE_DATA";
	
	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getUploadedLocation() {
		return uploadedLocation;
	}
	public void setUploadedLocation(String uploadedLocation) {
		this.uploadedLocation = uploadedLocation;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
