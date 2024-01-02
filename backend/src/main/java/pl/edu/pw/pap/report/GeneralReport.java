package pl.edu.pw.pap.report;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.user.User;


@MappedSuperclass
public abstract class GeneralReport {

    @Id
    @GeneratedValue
    public Long id;

    protected User reportingUser;
    protected String reason; // TODO: change to enum


}
