package pl.saidora.api.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface DataBase {

    Connection getConnection();


    void sendStatement(PreparedStatement preparedStatement) throws SQLException;


    boolean isConnected();


    void setIdleTimeOut(long timeOut);


    void connect(String host, String username, String password, boolean ssl);

    void close();

}
