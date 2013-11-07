package com.daou.book.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daou.book.domain.User;
import com.daou.book.dto.UserDTO;
import com.daou.book.dto.SearchDTO;
import com.daou.book.dto.SearchType;
import com.daou.book.exception.UserNotFoundException;
import com.daou.book.repository.UserRepository;

import javax.annotation.Resource;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Resource
    private UserRepository userRepository;

    @Transactional
    @Override
    public User create(UserDTO created) {
        LOGGER.debug("Creating a new user with information: " + created);
        
        User user = User.getBuilder(created.getFirstName(), created.getLastName()).build();
        
        return userRepository.save(user);
    }

    @Transactional(rollbackFor = UserNotFoundException.class)
    @Override
    public User delete(Long userId) throws UserNotFoundException {
        LOGGER.debug("Deleting user with id: " + userId);
        
        User deleted = userRepository.findOne(userId);
        
        if (deleted == null) {
            LOGGER.debug("No user found with id: " + userId);
            throw new UserNotFoundException();
        }
        
        userRepository.delete(deleted);
        return deleted;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        LOGGER.debug("Finding all users");
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public User findById(Long id) {
        LOGGER.debug("Finding user by id: " + id);
        return userRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> search(SearchDTO searchCriteria) {
        LOGGER.debug("Searching users with search criteria: " + searchCriteria);
        
        String searchTerm = searchCriteria.getSearchTerm();
        SearchType searchType = searchCriteria.getSearchType();
        
        if (searchType == null) {
            throw new IllegalArgumentException();
        }
         
        return findUsersBySearchType(searchTerm, searchType);
    }
    
    private List<User> findUsersBySearchType(String searchTerm, SearchType searchType) {
        List<User> users;

        if (searchType == SearchType.METHOD_NAME) {
            LOGGER.debug("Searching users by using method name query creation.");
            users = userRepository.findByLastName(searchTerm);
        }
        else if (searchType == SearchType.NAMED_QUERY) {
            LOGGER.debug("Searching users by using named query");
            users = userRepository.findByName(searchTerm);
        }
        else {
            LOGGER.debug("Searching users by using query annotation");
            users = userRepository.find(searchTerm);
        }

        return users;
    }

    @Transactional(rollbackFor = UserNotFoundException.class)
    @Override
    public User update(UserDTO updated) throws UserNotFoundException {
        LOGGER.debug("Updating user with information: " + updated);
        
        User user = userRepository.findOne(updated.getId());
        
        if (user == null) {
            LOGGER.debug("No user found with id: " + updated.getId());
            throw new UserNotFoundException();
        }
        
        user.update(updated.getFirstName(), updated.getLastName());

        return user;
    }

    protected void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
