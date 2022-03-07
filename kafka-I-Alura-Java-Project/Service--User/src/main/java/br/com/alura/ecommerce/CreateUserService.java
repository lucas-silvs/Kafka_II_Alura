package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private final Connection connection;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:users_database.db";
        this.connection = DriverManager.getConnection(url);
        connection.createStatement().execute("create table Users (" +
                "uuid varchar(200) primary key," +
                "email varchar(200)" +
                ");");
    }

    public static void main(String[] args) throws SQLException {
        var createUserService = new CreateUserService();
        var kafkaService = new KafkaService<Order>(CreateUserService.class.getSimpleName(), "ECOMMERCE_NEW_ORDER", createUserService::parse, Order.class, Map.of());
        kafkaService.run();
    }


    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("\n");
        System.out.println("processando pedidos, necessário a validação de novo usuario");
        var order = record.value();
        if(isNewUser(order.getEmail())){
            inserirNovoUsuario(order.getEmail());
        }
        else{
            System.out.println("usuario existente na base de dados");
        }


    }

    private void inserirNovoUsuario(String email) throws SQLException {
        var insercao = connection.prepareStatement("insert into Users (uuid, email) " +
                "values (?,?");
        insercao.setString(1, UUID.randomUUID().toString());
        insercao.setString(2,email);
        insercao.execute();

        System.out.println("usuario com email "+ email + "adicionado com sucesso");
    }


    private boolean isNewUser(String email) throws SQLException {
        var verificarUsuarioExistente = connection.prepareStatement("select into uuid from Users" +
                " where email = ? limit 1");
        verificarUsuarioExistente.setString(1,email);
        var result = verificarUsuarioExistente.executeQuery();

        return !result.next();

    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }
}
