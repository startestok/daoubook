package com.daou.book.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.daou.book.domain.User;
import com.daou.book.dto.UserDTO;
import com.daou.book.dto.SearchDTO;
import com.daou.book.exception.UserNotFoundException;
import com.daou.book.service.UserService;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.List;

@Controller
@SessionAttributes("user")
public class UserController extends AbstractController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    
    protected static final String ERROR_MESSAGE_KEY_DELETED_USER_WAS_NOT_FOUND = "error.message.deleted.not.found";
    protected static final String ERROR_MESSAGE_KEY_EDITED_USER_WAS_NOT_FOUND = "error.message.edited.not.found";
    
    protected static final String FEEDBACK_MESSAGE_KEY_USER_CREATED = "feedback.message.user.created";
    protected static final String FEEDBACK_MESSAGE_KEY_USER_DELETED = "feedback.message.user.deleted";
    protected static final String FEEDBACK_MESSAGE_KEY_USER_EDITED = "feedback.message.user.edited";
    
    protected static final String MODEL_ATTIRUTE_USER = "user";
    protected static final String MODEL_ATTRIBUTE_USERS = "users";
    protected static final String MODEL_ATTRIBUTE_SEARCHCRITERIA = "searchCriteria";
    
    protected static final String USER_ADD_FORM_VIEW = "user/create";
    protected static final String USER_EDIT_FORM_VIEW = "user/edit";
    protected static final String USER_LIST_VIEW = "user/list";
    protected static final String USER_SEARCH_RESULT_VIEW = "user/searchResults";
    
    protected static final String REQUEST_MAPPING_LIST = "/";
    
    @Resource
    private UserService userService;

    /**
     * 유저 삭제 처리
     * 
     * @param id    The id of the deleted user.
     * @param attributes
     * @return
     */
    @RequestMapping(value = "/user/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable("id") Long id, RedirectAttributes attributes) {
        LOGGER.debug("Deleting user with id: " + id);

        try {
            User deleted = userService.delete(id);
            addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_USER_DELETED, deleted.getName());
        } catch (UserNotFoundException e) {
            LOGGER.debug("No user found with id: " + id);
            addErrorMessage(attributes, ERROR_MESSAGE_KEY_DELETED_USER_WAS_NOT_FOUND);
        }

        return createRedirectViewPath(REQUEST_MAPPING_LIST);
    }

    @RequestMapping(value = "/user/search", method = RequestMethod.POST)
    public String search(@ModelAttribute(MODEL_ATTRIBUTE_SEARCHCRITERIA) SearchDTO searchCriteria, Model model) {
        LOGGER.debug("Searching users with search criteria: " + searchCriteria);
        
        List<User> users = userService.search(searchCriteria);
        LOGGER.debug("Found " + users.size() + " users");

        model.addAttribute(MODEL_ATTRIBUTE_USERS, users);
        
        return USER_SEARCH_RESULT_VIEW;
    }
    
    /**
     * 유저 생성 화면
     * 
     * @param model
     * @return  The name of the create user form view.
     */
    @RequestMapping(value = "/user/create", method = RequestMethod.GET) 
    public String showCreateUserForm(Model model) {
        LOGGER.debug("Rendering create user form");
        
        model.addAttribute(MODEL_ATTIRUTE_USER, new UserDTO());

        return USER_ADD_FORM_VIEW;
    }

    /**
     * 유저 생성 프로세스.
     * 
     * @param created   The information of the created users.
     * @param bindingResult
     * @param attributes
     * @return
     */
    @RequestMapping(value = "/user/create", method = RequestMethod.POST)
    public String submitCreateUserForm(@Valid @ModelAttribute(MODEL_ATTIRUTE_USER) UserDTO created, BindingResult bindingResult, RedirectAttributes attributes) {
        LOGGER.debug("Create user form was submitted with information: " + created);

        if (bindingResult.hasErrors()) {
            return USER_ADD_FORM_VIEW;
        }
                
        User user = userService.create(created);

        addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_USER_CREATED, user.getName());

        return createRedirectViewPath(REQUEST_MAPPING_LIST);
    }

    /**
     * 유저 수정 화면
     * 
     * @param id    The id of the edited user.
     * @param model
     * @param attributes
     * @return  The name of the edit user form view.
     */
    @RequestMapping(value = "/user/edit/{id}", method = RequestMethod.GET)
    public String showEditUserForm(@PathVariable("id") Long id, Model model, RedirectAttributes attributes) {
        LOGGER.debug("Rendering edit user form for user with id: " + id);
        
        User user = userService.findById(id);
        if (user == null) {
            LOGGER.debug("No user found with id: " + id);
            addErrorMessage(attributes, ERROR_MESSAGE_KEY_EDITED_USER_WAS_NOT_FOUND);
            return createRedirectViewPath(REQUEST_MAPPING_LIST);            
        }

        model.addAttribute(MODEL_ATTIRUTE_USER, constructFormObject(user));
        
        return USER_EDIT_FORM_VIEW;
    }

    /**
     * 유저 수정 프로세스.
     * 
     * @param updated   The information of the edited user.
     * @param bindingResult
     * @param attributes
     * @return
     */
    @RequestMapping(value = "/user/edit", method = RequestMethod.POST)
    public String submitEditUserForm(@Valid @ModelAttribute(MODEL_ATTIRUTE_USER) UserDTO updated, BindingResult bindingResult, RedirectAttributes attributes) {
        LOGGER.debug("Edit user form was submitted with information: " + updated);
        
        if (bindingResult.hasErrors()) {
            LOGGER.debug("Edit user form contains validation errors. Rendering form view.");
            return USER_EDIT_FORM_VIEW;
        }
        
        try {
            User user = userService.update(updated);
            addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_USER_EDITED, user.getName());
        } catch (UserNotFoundException e) {
            LOGGER.debug("No user was found with id: " + updated.getId());
            addErrorMessage(attributes, ERROR_MESSAGE_KEY_EDITED_USER_WAS_NOT_FOUND);
        }
        
        return createRedirectViewPath(REQUEST_MAPPING_LIST);
    }
    
    private UserDTO constructFormObject(User user) {
        UserDTO formObject = new UserDTO();
        
        formObject.setId(user.getId());
        formObject.setFirstName(user.getFirstName());
        formObject.setLastName(user.getLastName());
        
        return formObject;
    }

    /**
     * 유저 목록 제공.
     * @param model
     * @return  The name of the user list view.
     */
    @RequestMapping(value = REQUEST_MAPPING_LIST, method = RequestMethod.GET)
    public String showList(Model model) {
        LOGGER.debug("Rendering user list page");

        List<User> users = userService.findAll();
        model.addAttribute(MODEL_ATTRIBUTE_USERS, users);
        model.addAttribute(MODEL_ATTRIBUTE_SEARCHCRITERIA, new SearchDTO());

        return USER_LIST_VIEW;
    }

    /**
     * Junit 테스트를 위한 Setter
     * 
     * @param userService
     */
    protected void setUserService(UserService userService) {
        this.userService = userService;
    }
}
