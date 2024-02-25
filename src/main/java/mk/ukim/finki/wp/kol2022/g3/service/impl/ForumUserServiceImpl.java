package mk.ukim.finki.wp.kol2022.g3.service.impl;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.model.ForumUserType;
import mk.ukim.finki.wp.kol2022.g3.model.Interest;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidForumUserIdException;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidInterestIdException;
import mk.ukim.finki.wp.kol2022.g3.repository.ForumUserRepository;
import mk.ukim.finki.wp.kol2022.g3.repository.InterestRepository;
import mk.ukim.finki.wp.kol2022.g3.service.ForumUserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class ForumUserServiceImpl implements ForumUserService,UserDetailsService {

    private final ForumUserRepository forumUserRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;

    public ForumUserServiceImpl(ForumUserRepository forumUserRepository, InterestRepository interestRepository, PasswordEncoder passwordEncoder) {
        this.forumUserRepository = forumUserRepository;
        this.interestRepository = interestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<ForumUser> listAll() {
        return forumUserRepository.findAll();
    }

    @Override
    public ForumUser findById(Long id) {
        return forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
    }

    @Override
    public ForumUser create(String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        String encodedePassword = passwordEncoder.encode(password);
        List<Interest> interests = interestRepository.findAllById(interestId);
        ForumUser forumUser = new ForumUser(name,email,encodedePassword,type,interests,birthday);
        forumUserRepository.save(forumUser);
        return forumUser;
    }

    @Override
    public ForumUser update(Long id, String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        String encodedPassword = passwordEncoder.encode(password);
        List<Interest> interests = interestRepository.findAllById(interestId);
        ForumUser forumUser = forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
        forumUser.setName(name);
        forumUser.setEmail(email);
        forumUser.setPassword(encodedPassword);
        forumUser.setType(type);
        forumUser.setInterests(interests);
        forumUser.setBirthday(birthday);

        forumUserRepository.save(forumUser);

        return forumUser;
    }

    @Override
    public ForumUser delete(Long id) {
        ForumUser forumUser = forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
        forumUserRepository.delete(forumUser);
        return forumUser;
    }

    @Override
    public List<ForumUser> filter(Long interestId, Integer age) {
        if(interestId==null && age == null){
            return forumUserRepository.findAll();
        }else if(interestId != null && age != null){
            LocalDate dateBefore = LocalDate.now().minusYears(age);
            Interest interest = interestRepository.findById(interestId).orElseThrow(InvalidInterestIdException::new);
            return forumUserRepository.findByInterestsAndBirthdayBefore(interest,dateBefore);
        } else if (interestId != null){
            Interest interest = interestRepository.findById(interestId).orElseThrow(InvalidInterestIdException::new);
            return forumUserRepository.findByInterests(interest);
        }else{
            LocalDate dateBefore = LocalDate.now().minusYears(age);
            return forumUserRepository.findByBirthdayBefore(dateBefore);
        }
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ForumUser user = forumUserRepository.findByEmail(email).orElseThrow(InvalidForumUserIdException::new);
        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(user.getType())

        );
    }
}
