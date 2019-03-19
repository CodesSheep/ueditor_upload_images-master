package com.example.demo.define;

import com.example.Test.Test;
import com.example.demo.hunter.TestService;
import com.example.demo.upload.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestDao testDao;

    @Override
    public int insert(Test test) {
        return testDao.insert(test);
    }
}
