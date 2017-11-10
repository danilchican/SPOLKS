package com.bsuir.danilchican;

import com.bsuir.danilchican.controller.Controller;
import com.bsuir.danilchican.pool.ConnectionPool;

public class Server {
    public static void main(String[] args) {
        ConnectionPool.getInstance().runListeners();
        //Controller.getInstance().work(); // build jar: mvn package
    }
}
