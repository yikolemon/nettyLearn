package com.zko0.server.service;

public class HelloServiceImpl implements HelloService{
    @Override
    public String sayHi(String msg) {
        int i=1/0;
        return "hi"+msg;
    }
}
