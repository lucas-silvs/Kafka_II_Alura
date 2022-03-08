package br.com.alura.ecommerce;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HttpEcommerceService {
    public static void main(String[] args) throws Exception {
        var server = new Server(8080);

        server.join();

        var content = new ServletContextHandler();
        content.setContextPath("/");
        content.addServlet(new ServletHolder(new NewOrderServlet()), "/new");
        server.setHandler(content);

        server.start();

    }

}
