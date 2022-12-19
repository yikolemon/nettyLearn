package com.yikolemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */

@SpringBootApplication
public class App 
{
    public static void main( String[] args )
    {
        //SpringApplication.run(App.class,args);
        int a=0;
        suck();
        suck();;

        System.out.println(1);
    }

    public static void suck(){
        System.out.println("1");
    }
}
