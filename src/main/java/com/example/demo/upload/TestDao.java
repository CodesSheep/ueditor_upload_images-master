package com.example.demo.upload;

import com.example.Test.Test;
import org.springframework.stereotype.Repository;

@Repository
public interface TestDao {

    int insert(Test test);
}
