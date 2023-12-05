package pl.edu.pw.pap.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, ReviewKey> {
    public Optional<Review> findByCourse_IdAndUser_Username(Long courseId, String username);
}
