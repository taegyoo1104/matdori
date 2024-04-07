package com.estsoft13.matdori.service;

import com.estsoft13.matdori.domain.Comment;
import com.estsoft13.matdori.domain.Review;
import com.estsoft13.matdori.domain.User;
import com.estsoft13.matdori.dto.user.UserDto;
import com.estsoft13.matdori.repository.CommentRepository;
import com.estsoft13.matdori.repository.ReviewRepository;
import com.estsoft13.matdori.repository.UserRepository;
import com.estsoft13.matdori.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;

    // 유저 저장
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setRole(Role.ROLE_BEGINNER);

        userRepository.save(user);
    }

    // 관리자 저장
    public void saveAdmin(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setRole(Role.ROLE_ADMIN);

        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).
                orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    public User findByUsernameAndEmail(String email, String username) {
        return userRepository.findByUsernameAndEmail(email, username)
                .orElseThrow(() -> new IllegalArgumentException("User not found by: " + email + " and " + username));
    }


    public boolean isEmailUnique(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    // 비밀번호 변경
    public void resetPassword(User user, String newPassword) {
        user.setPassword(encoder.encode(newPassword));

        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 유저 업그레이드
    public void upgradeRoles(Long userId, Role newRole) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        user.setRole(newRole);

        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        List<Review> reviews = reviewRepository.findAllByUserId(userId);
        for (Review review : reviews) {
            reviewRepository.delete(review);
        }
        List<Comment> comments = commentRepository.findAllByUserId(userId);
        for (Comment comment : comments) {
            commentRepository.delete(comment);
        }

        userRepository.deleteById(userId);
    }

    public User findById(Long userId) {
         return userRepository.findById(userId).orElseThrow(
                 () -> new IllegalArgumentException("Invalid user Id:" + userId));
    }

    public boolean checkPassword(User user, String password) {
        return encoder.matches(password, user.getPassword());
    }
}
