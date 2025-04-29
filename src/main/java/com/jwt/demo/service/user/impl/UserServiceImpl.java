package com.jwt.demo.service.user.impl;

import com.jwt.demo.common.exception.ApiException;
import com.jwt.demo.data.dto.UserDTO;
import com.jwt.demo.enums.ErrorCode;
import com.jwt.demo.repository.user.UserRepository;
import com.jwt.demo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO findUserInfo(long userIdx) {

        var userEntity = userRepository.findById(userIdx)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FIND));

        return UserDTO.from(userEntity);
    }
}
