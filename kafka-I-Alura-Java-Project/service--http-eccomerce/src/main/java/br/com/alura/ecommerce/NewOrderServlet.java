package br.com.alura.ecommerce;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {
    private final KafkaDispatcher<Order> dispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void destroy() {
        super.destroy();
        dispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        try {
            var valorreq = Integer.parseInt(req.getParameter("valor"));
            var email = req.getParameter("email");
            var orderId = UUID.randomUUID().toString();
            var amount = new BigDecimal(valorreq);


            var order = new Order(orderId, amount, email);
            dispatcher.send("ECOMMERCE_NOVO_PEDIDO", email, order);


            var emailRequest = new Email("Teste@email.com", "Obrigado, estamos processando seu pedido");
            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailRequest);

            System.out.println("Processo da compra encerrado");
            resp.getWriter().println("Processo de compra encerrado");
            resp.setStatus(HttpServletResponse.SC_OK);


        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }


    }
}
