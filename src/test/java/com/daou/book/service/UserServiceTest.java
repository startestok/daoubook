package com.daou.book.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.daou.book.domain.UserTestUtil;
import com.daou.book.domain.User;
import com.daou.book.dto.UserDTO;
import com.daou.book.dto.SearchDTO;
import com.daou.book.dto.SearchType;
import com.daou.book.exception.UserNotFoundException;
import com.daou.book.repository.UserRepository;
import com.daou.book.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private static final Long USER_ID = Long.valueOf(5);
    private static final String FIRST_NAME = "Foo";
    private static final String FIRST_NAME_UPDATED = "FooUpdated";
    private static final String LAST_NAME = "Bar";
    private static final String LAST_NAME_UPDATED = "BarUpdated";
    
    private UserServiceImpl userService;

    private UserRepository userRepositoryMock;

    @Before
    public void setUp() {
        userService = new UserServiceImpl();

        userRepositoryMock = mock(UserRepository.class);
        userService.setUserRepository(userRepositoryMock);
    }
    
    @Test
    public void create() {
        UserDTO created = UserTestUtil.createDTO(null, FIRST_NAME, LAST_NAME);
        User persisted = UserTestUtil.createModelObject(USER_ID, FIRST_NAME, LAST_NAME);
        
        when(userRepositoryMock.save(any(User.class))).thenReturn(persisted);
        
        User returned = userService.create(created);

        ArgumentCaptor<User> userArgument = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryMock, times(1)).save(userArgument.capture());
        verifyNoMoreInteractions(userRepositoryMock);

        assertUser(created, userArgument.getValue());
        assertEquals(persisted, returned);
    }
    
    @Test
    public void delete() throws UserNotFoundException {
        User deleted = UserTestUtil.createModelObject(USER_ID, FIRST_NAME, LAST_NAME);
        when(userRepositoryMock.findOne(USER_ID)).thenReturn(deleted);
        
        User returned = userService.delete(USER_ID);
        
        verify(userRepositoryMock, times(1)).findOne(USER_ID);
        verify(userRepositoryMock, times(1)).delete(deleted);
        verifyNoMoreInteractions(userRepositoryMock);
        
        assertEquals(deleted, returned);
    }
    
    @Test(expected = UserNotFoundException.class)
    public void deleteWhenUserIsNotFound() throws UserNotFoundException {
        when(userRepositoryMock.findOne(USER_ID)).thenReturn(null);
        
        userService.delete(USER_ID);
        
        verify(userRepositoryMock, times(1)).findOne(USER_ID);
        verifyNoMoreInteractions(userRepositoryMock);
    }
    
    @Test
    public void findAll() {
        List<User> users = new ArrayList<User>();
        when(userRepositoryMock.findAll()).thenReturn(users);
        
        List<User> returned = userService.findAll();
        
        verify(userRepositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(userRepositoryMock);
        
        assertEquals(users, returned);
    }
    
    @Test
    public void findById() {
        User user = UserTestUtil.createModelObject(USER_ID, FIRST_NAME, LAST_NAME);
        when(userRepositoryMock.findOne(USER_ID)).thenReturn(user);
        
        User returned = userService.findById(USER_ID);
        
        verify(userRepositoryMock, times(1)).findOne(USER_ID);
        verifyNoMoreInteractions(userRepositoryMock);
        
        assertEquals(user, returned);
    }
    
    @Test
    public void searchWhenSearchTypeIsMethodName() {
        SearchDTO searchCriteria = createSearchDTO(LAST_NAME, SearchType.METHOD_NAME);
        List<User> expected = new ArrayList<User>();
        when(userRepositoryMock.findByLastName(searchCriteria.getSearchTerm())).thenReturn(expected);
        
        List<User> actual = userService.search(searchCriteria);
        
        verify(userRepositoryMock, times(1)).findByLastName(searchCriteria.getSearchTerm());
        verifyNoMoreInteractions(userRepositoryMock);
        
        assertEquals(expected, actual);
    }

    @Test
    public void searchWhenSearchTypeIsNamedQuery() {
        SearchDTO searchCriteria = createSearchDTO(LAST_NAME, SearchType.NAMED_QUERY);
        List<User> expected = new ArrayList<User>();
        when(userRepositoryMock.findByName(searchCriteria.getSearchTerm())).thenReturn(expected);

        List<User> actual = userService.search(searchCriteria);

        verify(userRepositoryMock, times(1)).findByName(searchCriteria.getSearchTerm());
        verifyNoMoreInteractions(userRepositoryMock);

        assertEquals(expected, actual);
    }

    @Test
    public void searchWhenSearchTypeIsQueryAnnotation() {
        SearchDTO searchCriteria = createSearchDTO(LAST_NAME, SearchType.QUERY_ANNOTATION);
        List<User> expected = new ArrayList<User>();
        when(userRepositoryMock.find(searchCriteria.getSearchTerm())).thenReturn(expected);

        List<User> actual = userService.search(searchCriteria);

        verify(userRepositoryMock, times(1)).find(searchCriteria.getSearchTerm());
        verifyNoMoreInteractions(userRepositoryMock);

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void searchWhenSearchTypeIsNull() {
        SearchDTO searchCriteria = createSearchDTO(LAST_NAME, null);

        userService.search(searchCriteria);

        verifyZeroInteractions(userRepositoryMock);
    }
    
    private SearchDTO createSearchDTO(String searchTerm, SearchType searchType) {
        SearchDTO searchCriteria = new SearchDTO();
        searchCriteria.setSearchTerm(searchTerm);
        searchCriteria.setSearchType(searchType);
        return searchCriteria;
    }
    
    @Test
    public void update() throws UserNotFoundException {
        UserDTO updated = UserTestUtil.createDTO(USER_ID, FIRST_NAME_UPDATED, LAST_NAME_UPDATED);
        User user = UserTestUtil.createModelObject(USER_ID, FIRST_NAME, LAST_NAME);
        
        when(userRepositoryMock.findOne(updated.getId())).thenReturn(user);
        
        User returned = userService.update(updated);
        
        verify(userRepositoryMock, times(1)).findOne(updated.getId());
        verifyNoMoreInteractions(userRepositoryMock);
        
        assertUser(updated, returned);
    }
    
    @Test(expected = UserNotFoundException.class)
    public void updateWhenUserIsNotFound() throws UserNotFoundException {
        UserDTO updated = UserTestUtil.createDTO(USER_ID, FIRST_NAME_UPDATED, LAST_NAME_UPDATED);
        
        when(userRepositoryMock.findOne(updated.getId())).thenReturn(null);

        userService.update(updated);

        verify(userRepositoryMock, times(1)).findOne(updated.getId());
        verifyNoMoreInteractions(userRepositoryMock);
    }

    private void assertUser(UserDTO expected, User actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), expected.getLastName());
    }

}
