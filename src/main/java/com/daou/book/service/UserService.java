package com.daou.book.service;

import java.util.List;

import com.daou.book.domain.User;
import com.daou.book.dto.UserDTO;
import com.daou.book.dto.SearchDTO;
import com.daou.book.exception.UserNotFoundException;

public interface UserService {

    public User create(UserDTO created);

    public User delete(Long userId) throws UserNotFoundException;

    public List<User> findAll();

    public User findById(Long id);

    public List<User> search(SearchDTO searchCriteria);

    public User update(UserDTO updated) throws UserNotFoundException;
}
