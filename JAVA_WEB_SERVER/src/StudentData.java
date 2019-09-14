
public class StudentData {

	private String studentId;
	private String firstName;
	private String lastName;
	private String grades;
	private CourseData[] courseData;
	
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public CourseData[] getCourseData() {
		return courseData;
	}
	public void setCourseData(CourseData[] courseData) {
		this.courseData = courseData;
	}
	public String getGrades() {
		return grades;
	}
	public void setGrades(String grades) {
		this.grades = grades;
	}
	
	
	
	
}
