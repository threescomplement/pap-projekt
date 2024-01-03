package pl.edu.pw.pap.report;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.user.User;


@MappedSuperclass
@Getter
@Setter
public abstract class GeneralReport {

    @Id
    @GeneratedValue
    private Long id;


    private User reportingUser; // TODO model relation with user
    private String reason; // TODO: change to enum

    public GeneralReport(User reportingUser, String reason){
        this.reportingUser = reportingUser;
        this.reason = reason;
    }
    protected GeneralReport(){
    }

}
