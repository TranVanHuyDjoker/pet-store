package com.hivetech.service;

import com.hivetech.config.exception.NotFoundException;
import com.hivetech.model.dto.UserDTO;
import com.hivetech.model.entity.User;
import com.hivetech.repository.RoleRepo;
import com.hivetech.repository.UserInfoRepo;
import com.hivetech.repository.UserRepo;
import com.hivetech.service.impl.UserServiceImpl;
import com.hivetech.utils.constants.UserConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {
    // 1
//    @InjectMocks
    private UserServiceImpl userService;
    //    @Mock
    private UserRepo userRepo;
    //    @Mock
    private RoleRepo roleRepo;
    //    @Mock
    private UserInfoRepo userInfoRepo;
    //    @Mock
    private SendMailService mailService;
        @Spy
    private ModelMapper modelMapper;
    //    @Spy
    private PasswordEncoder encoder;

    @BeforeEach
    public void setUp() {
        //2
        userRepo = Mockito.mock(UserRepo.class);
        roleRepo = Mockito.mock(RoleRepo.class);
        userInfoRepo = Mockito.mock(UserInfoRepo.class);
        mailService = Mockito.mock(SendMailService.class);
        modelMapper = Mockito.spy(ModelMapper.class);
        encoder = Mockito.spy(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepo, modelMapper, encoder, roleRepo, mailService, userInfoRepo);
    }

    @Test
    public void testFindByUsernameSuccess_withExistUsername() {
        //given
        User user = new User(1, "user", "123", "l@gmail.com", null, 1, null);
        when(userRepo.findByUsername("user")).thenReturn(Optional.of(user));
        //execute
        UserDTO result = userService.findByUsername("user");
        //verify
        assertEquals("user", result.getUsername());
        assertEquals(1, result.getId());
    }

    @Test
    public void testFindByUsernameFail_whenNotExistUsername() {
        //given
        User user = new User(1, "user", "123", "l@gmail.com", null, 1, null);
        when(userRepo.findByUsername("adsasdad")).thenThrow(new NotFoundException(UserConstants.NOT_FOUND));
        //execute
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            userService.findByUsername("adsasdad");
        });
        //verify
        assertEquals(UserConstants.NOT_FOUND, result.getMessage());
    }

    @Test
    public void testDeleteUserSuccess_withExistUser() {
        //given
        User user = new User(1, "user", "123", "l@gmail.com", null, 1, null);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepo).deleteById(1L);
        //execute
        boolean b = userService.deleteUser(1L);
        //verify
        assertEquals(true, b);
    }

    @Test
    public void testDeleteUserFail_whenNotExistUser() {
        //given
        User user = new User(1, "user", "123", "l@gmail.com", null, 1, null);
        when(userRepo.findById(2L)).thenThrow(new NotFoundException(UserConstants.NOT_FOUND));
        //execute
        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(2L);
        });
        //verify
        assertEquals(UserConstants.NOT_FOUND, result.getMessage());
    }
}
