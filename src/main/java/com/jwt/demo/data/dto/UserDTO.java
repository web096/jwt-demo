package com.jwt.demo.data.dto;

import com.jwt.demo.data.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private long useridx;

    private String userId;

    public static UserDTO from(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();

        userDTO.useridx = userEntity.getIdx();
        userDTO.userId = userEntity.getUserId();

        return userDTO;
    }
}
