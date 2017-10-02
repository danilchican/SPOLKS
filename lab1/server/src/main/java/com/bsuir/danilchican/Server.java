package com.bsuir.danilchican;

import com.bsuir.danilchican.connection.Connection;
import com.bsuir.danilchican.controller.Controller;

public class Server {
    public static void main(String[] args) {
        Controller.getInstance().work(); // build jar: mvn package
    }
}
