package pl.edu.pw.pap.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.review.Review;

import java.util.Set;

@Entity
@Getter
@Setter // Todo: not sure if setter here is a good idea after adding the reviews Set. Might be better to have separate
// ones
@Table(name = "app_user")  // Namespace conflict with default value (I guess?)
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;
    private String email;

    @JsonIgnore
    private String password;
    private String role;
    private Boolean enabled;

    // TODO: figure out how to cascade reviews and comments without repository.deleteAll throwing Cocurrent Modification (if needed)
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Review> reviews;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Comment> comments;

    public User(String username, String email, String password, String role, Boolean enabled) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }


    protected User() {

    }

    public void removeReview ( Review review){
        this.reviews.remove(review);
    }
    public void removeComment(Comment comment){
        this.comments.remove(comment);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
