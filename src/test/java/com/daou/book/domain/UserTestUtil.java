package com.daou.book.domain;

import com.daou.book.domain.User;
import com.daou.book.dto.UserDTO;

public class UserTestUtil {

    public static UserDTO createDTO(Long id, String firstName, String lastName) {
        UserDTO dto = new UserDTO();

        dto.setId(id);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);

        return dto;
    }

    public static User createModelObject(Long id, String firstName, String lastName) {
        User model = User.getBuilder(firstName, lastName).build();

        model.setId(id);

        return model;
    }
}
