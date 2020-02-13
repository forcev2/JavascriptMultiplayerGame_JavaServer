package core;
import com.sun.net.httpserver.HttpServer;
import  core.web.MyHttpServer;
import core.web.MyHttpServer.connectionCheck;

import java.io.IOException;
import java.net.InetSocketAddress;
//JavascriptMultiplayerGame_JavaServer
public class main {
    public static void main(String  [] arg){
        HttpServer server = null;
        try {
            MyHttpServer myHttp =  new MyHttpServer();
            connectionCheck check = myHttp.new connectionCheck();
            check.start();
            server = HttpServer.create(new InetSocketAddress(8001), 0);
            server.createContext("/", new MyHttpServer());
            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
