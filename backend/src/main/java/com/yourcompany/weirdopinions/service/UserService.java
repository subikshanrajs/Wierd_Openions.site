
package com.yourcompany.weirdopinions.service;

import com.yourcompany.weirdopinions.model.RoleName;
import com.yourcompany.weirdopinions.model.User;
import com.yourcompany.weirdopinions.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final List<String> ADJECTIVES = Arrays.asList(
        "Curious", "Mysterious", "Wandering", "Dancing", "Sleeping", "Flying", 
        "Giggling", "Whispering", "Dreaming", "Bouncing", "Sparkling", "Laughing",
        "Singing", "Floating", "Glowing", "Sneaky", "Cheerful", "Dizzy", "Fuzzy"
    );

    private static final List<String> ANIMALS = Arrays.asList(
        "Otter", "Penguin", "Unicorn", "Dragon", "Butterfly", "Owl", "Fox",
        "Rabbit", "Elephant", "Dolphin", "Panda", "Koala", "Sloth", "Hedgehog",
        "Flamingo", "Llama", "Narwhal", "Octopus", "Peacock", "Turtle"
    );

    private final Random random = new SecureRandom();

    public User createUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already taken!");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User(username, email, passwordEncoder.encode(password));
        user.setAnonymousName(generateAnonymousName());
        user.getRoles().add(RoleName.ROLE_USER);
        
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public String generateAnonymousName() {
        String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
        String animal = ANIMALS.get(random.nextInt(ANIMALS.size()));
        int number = random.nextInt(1000);
        return adjective + " " + animal + " " + number;
    }

    public User regenerateAnonymousName(User user) {
        user.setAnonymousName(generateAnonymousName());
        return userRepository.save(user);
    }
}