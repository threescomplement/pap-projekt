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

    private User reportingUser;
    private String reason; // TODO: change to enum


}
