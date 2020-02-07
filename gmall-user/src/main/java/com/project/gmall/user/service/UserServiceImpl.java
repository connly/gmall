package com.project.gmall.user.service;

import com.project.gmall.bean.UmsMember;
import com.project.gmall.service.UserService;
import com.project.gmall.user.mapper.UmsMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UmsMemberMapper umsMemberMapper;

    @Override
    public List<UmsMember> getAllUser() {
        return umsMemberMapper.selectAll();
    }
}
