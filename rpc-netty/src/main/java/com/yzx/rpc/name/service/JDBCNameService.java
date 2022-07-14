package com.yzx.rpc.name.service;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author baozi
 * @Description: 以支持jdbc的数据库实现注册中心
 * @Date created on 2022/7/14
 */
public class JDBCNameService implements NameService {

    private static final Logger logger = LoggerFactory.getLogger(JDBCNameService.class);
    private Connection conn = null;
    private final static String TABLE_NAME = "t_provider_registery";
    private final static String PARAM_SERVICE_NAME = "service_name";
    private final static String PARAM_URI = "uri";
    private final static String QUERY_SQL = "SELECT * FROM " + TABLE_NAME + " WHERE service_name = ?";
    private final static String INSERT_SQL = "INSERT INTO " + TABLE_NAME + " (" + PARAM_SERVICE_NAME + ", " + PARAM_URI + ") VALUES(?, ?);";
    private final static String USERNAME = "root";
    private final static String PASSWORD = "Zj305540017.";
    private Collection<String> schemes = Collections.singleton(NameServiceSchemes.JDBC.getDesc());

    @Override
    public void registerService(String serviceName, URI uri) throws IOException {
        // 先查询表中该serviceName是否已有该url
        if (query(serviceName).contains(uri.toString())) {
            return;
        }
        // 如果不存在则插入
        insert(serviceName, uri);
    }

    private List<String> query(String serviceName) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<String> uris = new ArrayList<>();
        try {
            preparedStatement = conn.prepareStatement(QUERY_SQL);
            preparedStatement.setString(1, serviceName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                // 如果已存在则不用重复注册
                uris.add(resultSet.getString(PARAM_URI));
            }
        } catch (SQLException throwables) {
            throw new RuntimeException("register service fail");
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return uris;
    }

    private void insert(String serviceName, URI uri) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(INSERT_SQL);
            // 如果不存在，则插入一条新数据
            preparedStatement.setString(1, serviceName);
            preparedStatement.setString(2, uri.toString());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("register service fail");
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    public URI lookupService(String serviceName) throws IOException {
        // 查询表中serviceName对应的所有uri
        List<String> uris = query(serviceName);
        // 通过随机负载均衡 返回uri
        return java.net.URI.create(uris.get(ThreadLocalRandom.current().nextInt(uris.size())));
    }

    @Override
    public Collection<String> supportedSchemes() {
        return schemes;
    }

    @Override
    public void connect(URI nameServiceUri) {
        if (conn != null) {
            return;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(nameServiceUri.toString(), USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("[JDBCNameService - connect] connect fail", e);
        }
    }
}
