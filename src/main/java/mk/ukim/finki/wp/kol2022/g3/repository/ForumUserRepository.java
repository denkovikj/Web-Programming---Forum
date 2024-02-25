package mk.ukim.finki.wp.kol2022.g3.repository;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.model.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ForumUserRepository extends JpaRepository<ForumUser,Long> {

    List<ForumUser> findByInterests(Interest interest);
    List<ForumUser> findByBirthdayBefore(LocalDate date);
    List<ForumUser> findByInterestsAndBirthdayBefore(Interest interest,LocalDate date);
    Optional<ForumUser> findByEmail(String email);
}
