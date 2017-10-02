package com.bsuir.danilchican;

import com.bsuir.danilchican.connection.Connection;
import com.bsuir.danilchican.controller.Controller;

public class Server {
    public static void main(String[] args) {
        Connection c = new Connection();
        if(c.open()) {
            c.listen();
        }
        //Controller.getInstance().work(); // build jar: mvn package
    }
}
