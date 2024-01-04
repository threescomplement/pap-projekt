package pl.edu.pw.pap.teacher;

public class TeacherNotFoundException extends RuntimeException {
    public TeacherNotFoundException(String message){
        super(message);
    }
}
