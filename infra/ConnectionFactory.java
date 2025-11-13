package br.com.redemaissocial.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// classe utilitária responsável por criar e gerenciar a conexão com o banco de dados mysql.

public class ConnectionFactory {

    // configurações da conexão com o mysql 

    // url de conexão: protocolo, host (localhost), porta (3306) e nome do schema/banco
    private static final String URL = "jdbc:mysql://localhost:3306/rede_mais_social_db";
    // nome de usuário do banco
    private static final String USER = "root";
    

    // senha do usuário root
    private static final String PASSWORD = "sossego"; 
    
    // método estático que retorna uma nova conexão com o banco
    public static Connection getConnection() throws SQLException {
        try {
            // carrega o driver jdbc do mysql na memória
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // tenta estabelecer a conexão usando url, usuário e senha
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            // exceção lançada se o driver não for encontrad
            System.err.println("erro: driver jdbc não encontrado. verifique se adicionou a dependência no pom.xml.");
            throw new SQLException("driver jdbc não encontrado.", e);
        }
    }
}