package com.jwt.demo.service.user;

import com.jwt.demo.data.dto.UserDTO;

public interface UserService {

    public UserDTO findUserInfo(long userIdx);
}
