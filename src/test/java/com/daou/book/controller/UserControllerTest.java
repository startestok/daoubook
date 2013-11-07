package com.daou.book.controller;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.daou.book.domain.User;
import com.daou.book.domain.UserTestUtil;
import com.daou.book.dto.SearchDTO;
import com.daou.book.dto.SearchType;
import com.daou.book.dto.UserDTO;
import com.daou.book.exception.UserNotFoundException;
import com.daou.book.service.UserService;

public class UserControllerTest extends AbstractTestController {

    private static final String FIELD_NAME_FIRST_NAME = "firstName";
    private static final String FIELD_NAME_LAST_NAME = "lastName";
    
    private static final Long USER_ID = Long.valueOf(5);
    private static final String FIRST_NAME = "Foo";
    private static final String FIRST_NAME_UPDATED = "FooUpdated";
    private static final String LAST_NAME = "Bar";
    private static final String LAST_NAME_UPDATED = "BarUpdated";

    private UserController controller;
    
    private UserService userServiceMock;

    @Override
    public void setUpTest() {
        controller = new UserController();

        controller.setMessageSource(getMessageSourceMock());

        userServiceMock = mock(UserService.class);
        controller.setUserService(userServiceMock);
    }
    
    @Test
    public void delete() throws UserNotFoundException {
        User deleted = UserTestUtil.createModelObject(USER_ID, FIRST_NAME, LAST_NAME);
        when(userServiceMock.delete(USER_ID)).thenReturn(deleted);
        
        initMessageSourceForFeedbackMessage(UserController.FEEDBACK_MESSAGE_KEY_USER_DELETED);
        
        RedirectAttributes attributes = new RedirectAttributesModelMap();
        String view = controller.delete(USER_ID, attributes);
        
        verify(userServiceMock, times(1)).delete(USER_ID);
        verifyNoMoreInteractions(userServiceMock);
        assertFeedbackMessage(attributes, UserController.FEEDBACK_MESSAGE_KEY_USER_DELETED);
        
        String expectedView = createExpectedRedirectViewPath(UserController.REQUEST_MAPPING_LIST);
        assertEquals(expectedView, view);
    }
    
    @Test
    public void deleteWhenUserIsNotFound() throws UserNotFoundException {
        when(userServiceMock.delete(USER_ID)).thenThrow(new UserNotFoundException());
        
        initMessageSourceForErrorMessage(UserController.ERROR_MESSAGE_KEY_DELETED_USER_WAS_NOT_FOUND);
        
        RedirectAttributes attributes = new RedirectAttributesModelMap();
        String view = controller.delete(USER_ID, attributes);
        
        verify(userServiceMock, times(1)).delete(USER_ID);
        verifyNoMoreInteractions(userServiceMock);
        assertErrorMessage(attributes, UserController.ERROR_MESSAGE_KEY_DELETED_USER_WAS_NOT_FOUND);
        
        String expectedView = createExpectedRedirectViewPath(UserController.REQUEST_MAPPING_LIST);
        assertEquals(expectedView, view);
    }
    
    @Test
    public void search() {
        SearchDTO searchCriteria = createSearchCriteria(LAST_NAME, SearchType.METHOD_NAME);
        List<User> expected = new ArrayList<User>();
        when(userServiceMock.search(searchCriteria)).thenReturn(expected);
        
        BindingAwareModelMap model = new BindingAwareModelMap();
        String view = controller.search(searchCriteria, model);
        
        verify(userServiceMock, times(1)).search(searchCriteria);
        verifyNoMoreInteractions(userServiceMock);
        
        assertEquals(UserController.USER_SEARCH_RESULT_VIEW, view);
        List<User> actual = (List<User>) model.asMap().get(UserController.MODEL_ATTRIBUTE_USERS);
        assertEquals(expected, actual);
    }
    
    private SearchDTO createSearchCriteria(String searchTerm, SearchType searchType) {
        SearchDTO searchCriteria = new SearchDTO();
        
        searchCriteria.setSearchTerm(searchTerm);
        searchCriteria.setSearchType(searchType);
        
        return searchCriteria;
    }
    
    @Test
    public void showCreateUserForm() {
        Model model = new BindingAwareModelMap();
        
        String view = controller.showCreateUserForm(model);
        
        verifyZeroInteractions(userServiceMock);
        
        assertEquals(UserController.USER_ADD_FORM_VIEW, view);

        UserDTO added = (UserDTO) model.asMap().get(UserController.MODEL_ATTIRUTE_USER);
        assertNotNull(added);
        
        assertNull(added.getId());
        assertNull(added.getFirstName());
        assertNull(added.getLastName());
    }

    @Test
    public void submitCreateUserForm() {        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("/user/create", "POST");
        
        UserDTO created = UserTestUtil.createDTO(USER_ID, FIRST_NAME, LAST_NAME);
        User model = UserTestUtil.createModelObject(USER_ID, FIRST_NAME, LAST_NAME);
        when(userServiceMock.create(created)).thenReturn(model);

        initMessageSourceForFeedbackMessage(UserController.FEEDBACK_MESSAGE_KEY_USER_CREATED);
        
        RedirectAttributes attributes = new RedirectAttributesModelMap();
        BindingResult result = bindAndValidate(mockRequest, created);
        
        String view = controller.submitCreateUserForm(created, result, attributes);
        
        verify(userServiceMock, times(1)).create(created);
        verifyNoMoreInteractions(userServiceMock);
        
        String expectedViewPath = createExpectedRedirectViewPath(UserController.REQUEST_MAPPING_LIST);
        assertEquals(expectedViewPath, view);
        
        assertFeedbackMessage(attributes, UserController.FEEDBACK_MESSAGE_KEY_USER_CREATED);
        
        verify(userServiceMock, times(1)).create(created);
        verifyNoMoreInteractions(userServiceMock);
    }
    
    @Test
    public void submitEmptyCreateUserForm() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("/user/create", "POST");
        
        UserDTO created = new UserDTO();
        
        RedirectAttributes attributes = new RedirectAttributesModelMap();
        BindingResult result = bindAndValidate(mockRequest, created);
        
        String view = controller.submitCreateUserForm(created, result, attributes);
        
        verifyZeroInteractions(userServiceMock);
        
        assertEquals(UserController.USER_ADD_FORM_VIEW, view);
        assertFieldErrors(result, FIELD_NAME_FIRST_NAME, FIELD_NAME_LAST_NAME);
    }

    @Test
    public void submitCreateUserFormWithEmptyFirstName() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("/user/create", "POST");

        UserDTO created = UserTestUtil.createDTO(null, null, LAST_NAME);

        RedirectAttributes attributes = new RedirectAttributesModelMap();
        BindingResult result = bindAndValidate(mockRequest, created);

        String view = controller.submitCreateUserForm(created, result, attributes);

        verifyZeroInteractions(userServiceMock);

        assertEquals(UserController.USER_ADD_FORM_VIEW, view);
        assertFieldErrors(result, FIELD_NAME_FIRST_NAME);
    }

    @Test
    public void submitCreateUserFormWithEmptyLastName() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("/user/create", "POST");

        UserDTO created = UserTestUtil.createDTO(null, FIRST_NAME, null);

        RedirectAttributes attributes = new RedirectAttributesModelMap();
        BindingResult result = bindAndValidate(mockRequest, created);

        String view = controller.submitCreateUserForm(created, result, attributes);

        verifyZeroInteractions(userServiceMock);

        assertEquals(UserController.USER_ADD_FORM_VIEW, view);
        assertFieldErrors(result, FIELD_NAME_LAST_NAME);
    }
    
    @Test
    public void showEditUserForm() {
        User user = UserTestUtil.createModelObject(USER_ID, FIRST_NAME, LAST_NAME);
        when(userServiceMock.findById(USER_ID)).thenReturn(user);
        
        Model model = new BindingAwareModelMap();
        RedirectAttributes attributes = new RedirectAttributesModelMap();
        
        String view = controller.showEditUserForm(USER_ID, model, attributes);
        
        verify(userServiceMock, times(1)).findById(USER_ID);
        verifyNoMoreInteractions(userServiceMock);
        
        assertEquals(UserController.USER_EDIT_FORM_VIEW, view);
        
        UserDTO formObject = (UserDTO) model.asMap().get(UserController.MODEL_ATTIRUTE_USER);

        assertNotNull(formObject);
        assertEquals(user.getId(), formObject.getId());
        assertEquals(user.getFirstName(), formObject.getFirstName());
        assertEquals(user.getLastName(), formObject.getLastName());
    }
    
    @Test
    public void showEditUserFormWhenUserIsNotFound() {
        when(userServiceMock.findById(USER_ID)).thenReturn(null);
        
        initMessageSourceForErrorMessage(UserController.ERROR_MESSAGE_KEY_EDITED_USER_WAS_NOT_FOUND);
        
        Model model = new BindingAwareModelMap();
        RedirectAttributes attributes = new RedirectAttributesModelMap();
        
        String view = controller.showEditUserForm(USER_ID, model, attributes);
        
        verify(userServiceMock, times(1)).findById(USER_ID);
        verifyNoMoreInteractions(userServiceMock);
        
        String expectedView = createExpectedRedirectViewPath(UserController.REQUEST_MAPPING_LIST);
        assertEquals(expectedView, view);

        assertErrorMessage(attributes, UserController.ERROR_MESSAGE_KEY_EDITED_USER_WAS_NOT_FOUND);
    }
    
    @Test
    public void submitEditUserForm() throws UserNotFoundException {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("/user/edit", "POST");
        UserDTO updated = UserTestUtil.createDTO(USER_ID, FIRST_NAME_UPDATED, LAST_NAME_UPDATED);
        User user = UserTestUtil.createModelObject(USER_ID, FIRST_NAME_UPDATED, LAST_NAME_UPDATED);
        
        when(userServiceMock.update(updated)).thenReturn(user);
        
        initMessageSourceForFeedbackMessage(UserController.FEEDBACK_MESSAGE_KEY_USER_EDITED);
        
        BindingResult bindingResult = bindAndValidate(mockRequest, updated);
        RedirectAttributes attributes = new RedirectAttributesModelMap();
        
        String view = controller.submitEditUserForm(updated, bindingResult, attributes);
        
        verify(userServiceMock, times(1)).update(updated);
        verifyNoMoreInteractions(userServiceMock);
        
        String expectedView = createExpectedRedirectViewPath(UserController.REQUEST_MAPPING_LIST);
        assertEquals(expectedView, view);

        assertFeedbackMessage(attributes, UserController.FEEDBACK_MESSAGE_KEY_USER_EDITED);
        
        assertEquals(updated.getFirstName(), user.getFirstName());
        assertEquals(updated.getLastName(), user.getLastName());
    }
    
    @Test
    public void submitEditUserFormWhenUserIsNotFound() throws UserNotFoundException {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("/user/edit", "POST");
        UserDTO updated = UserTestUtil.createDTO(USER_ID, FIRST_NAME_UPDATED, LAST_NAME_UPDATED);
        
        when(userServiceMock.update(updated)).thenThrow(new UserNotFoundException());
        initMessageSourceForErrorMessage(UserController.ERROR_MESSAGE_KEY_EDITED_USER_WAS_NOT_FOUND);
        
        BindingResult bindingResult = bindAndValidate(mockRequest, updated);
        RedirectAttributes attributes = new RedirectAttributesModelMap();
        
        String view = controller.submitEditUserForm(updated, bindingResult, attributes);
        
        verify(userServiceMock, times(1)).update(updated);
        verifyNoMoreInteractions(userServiceMock);
        
        String expectedView = createExpectedRedirectViewPath(UserController.REQUEST_MAPPING_LIST);
        assertEquals(expectedView, view);

        assertErrorMessage(attributes, UserController.ERROR_MESSAGE_KEY_EDITED_USER_WAS_NOT_FOUND);
    }
    
    @Test
    public void submitEmptyEditUserForm() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("/user/edit", "POST");
        UserDTO updated = UserTestUtil.createDTO(USER_ID, null, null);
        
        BindingResult bindingResult = bindAndValidate(mockRequest, updated);
        RedirectAttributes attributes = new RedirectAttributesModelMap();
        
        String view = controller.submitEditUserForm(updated, bindingResult, attributes);
        
        verifyZeroInteractions(userServiceMock);
        
        assertEquals(UserController.USER_EDIT_FORM_VIEW, view);
        assertFieldErrors(bindingResult, FIELD_NAME_FIRST_NAME, FIELD_NAME_LAST_NAME);
    }

    @Test
    public void submitEditUserFormWhenFirstNameIsEmpty() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("/user/edit", "POST");
        UserDTO updated = UserTestUtil.createDTO(USER_ID, null, LAST_NAME_UPDATED);

        BindingResult bindingResult = bindAndValidate(mockRequest, updated);
        RedirectAttributes attributes = new RedirectAttributesModelMap();

        String view = controller.submitEditUserForm(updated, bindingResult, attributes);

        verifyZeroInteractions(userServiceMock);

        assertEquals(UserController.USER_EDIT_FORM_VIEW, view);
        assertFieldErrors(bindingResult, FIELD_NAME_FIRST_NAME);
    }

    @Test
    public void submitEditUserFormWhenLastNameIsEmpty() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("/user/edit", "POST");
        UserDTO updated = UserTestUtil.createDTO(USER_ID, FIRST_NAME_UPDATED, null);

        BindingResult bindingResult = bindAndValidate(mockRequest, updated);
        RedirectAttributes attributes = new RedirectAttributesModelMap();

        String view = controller.submitEditUserForm(updated, bindingResult, attributes);

        verifyZeroInteractions(userServiceMock);

        assertEquals(UserController.USER_EDIT_FORM_VIEW, view);
        assertFieldErrors(bindingResult, FIELD_NAME_LAST_NAME);
    }
    
    @Test
    public void showList() {
        List<User> users = new ArrayList<User>();
        when(userServiceMock.findAll()).thenReturn(users);
        
        Model model = new BindingAwareModelMap();
        String view = controller.showList(model);
        
        verify(userServiceMock, times(1)).findAll();
        verifyNoMoreInteractions(userServiceMock);
        
        assertEquals(UserController.USER_LIST_VIEW, view);
        assertEquals(users, model.asMap().get(UserController.MODEL_ATTRIBUTE_USERS));

        SearchDTO searchCriteria = (SearchDTO) model.asMap().get(UserController.MODEL_ATTRIBUTE_SEARCHCRITERIA);
        assertNotNull(searchCriteria);
        assertNull(searchCriteria.getSearchTerm());
        assertNull(searchCriteria.getSearchType());
    }
}
