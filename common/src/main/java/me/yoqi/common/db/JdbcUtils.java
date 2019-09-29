package me.yoqi.common.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lqg 数据库操作类，提供数据增，删，改，查基本操作。
 */
public class JdbcUtils {

    // 数据库用户名
    private String USERNAME = "lyq";
    // 数据库密码
    private String PASSWORD = "123456";
    // 驱动信息
    private String DRIVER = "com.mysql.jdbc.Driver";
    // 数据库地址
    // private static final String URL = "jdbc:mysql://172.16.90.108:3306/";
    private String URL = "jdbc:mysql://localhost:3306/";
    private Connection connection;
    private PreparedStatement pstmt;
    private ResultSet resultSet;
    Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

    // 连接数据库
    public JdbcUtils() {
        try {
            Class.forName(DRIVER);
            logger.info("加载驱动成功！");
        } catch (Exception e) {
        }
    }

    /**
     * 这个函数主要是打印连接数据
     */
    public void getConnStr() {
        String str = "连接字符串为：\n" + "登录名：" + this.USERNAME + "\n密码："
                + this.PASSWORD + "\n数据库地址：" + this.URL;
        logger.info(str);
    }

    /**
     * 设置登录数据库账号，密码，连接什么数据库.set方法
     *
     * @param _userName Mysql账号：root
     * @param _password 密码：123456
     * @param _url      登录数据库名：jdbc:mysql://localhost:3306/
     */
    public void setConnstr(String _userName, String _password, String _url) {
        this.USERNAME = _userName;
        this.PASSWORD = _password;
        this.URL = _url;
    }

    /**
     * 获得数据库的连接
     *
     * @return
     */
    public Connection getConnection(String schemaName) {
        String newURL = this.URL + schemaName;
        logger.info(newURL);
        try {
            connection = DriverManager
                    .getConnection(newURL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public Connection getConnection() {
        String newURL = this.URL;
        logger.info(newURL);
        try {
            connection = DriverManager
                    .getConnection(newURL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 增加、删除、改
     *
     * @param sql    语句
     * @param params 需要传递的变量
     *               ，比如表单提交的数据。
     * @return 这里返回值为成功和失败
     * @throws SQLException
     */
    public boolean updateByPreparedStatement(String sql, List<Object> params)
            throws SQLException {
        boolean flag = false;
        int result = -1;
        pstmt = connection.prepareStatement(sql);
        int index = 1;
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(index++, params.get(i));
            }
        }

        // System.out.println(pstmt.getMetaData().toString());
        // System.out.println(pstmt.toString());
        logger.info(pstmt.toString());
        result = pstmt.executeUpdate();
        flag = result > 0 ? true : false;
        return flag;
    }

    /**
     * 查询单条记录
     *
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public Map<String, Object> findSimpleResult(String sql, List<Object> params)
            throws SQLException {
        Map<String, Object> map = new HashMap<String, Object>();
        int index = 1;
        pstmt = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(index++, params.get(i));
            }
        }
        resultSet = pstmt.executeQuery();// 返回查询结果
        ResultSetMetaData metaData = resultSet.getMetaData();
        int col_len = metaData.getColumnCount();
        while (resultSet.next()) {
            for (int i = 0; i < col_len; i++) {
                String cols_name = metaData.getColumnName(i + 1);
                Object cols_value = resultSet.getObject(cols_name);
                if (cols_value == null) {
                    cols_value = "";
                }
                map.put(cols_name, cols_value);
            }
        }
        return map;
    }

    /**
     * 查询多条记录
     *
     * @param sql    语句
     * @param params （不管，设为null）
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> findModeResult(String sql,
                                                    List<Object> params) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int index = 1;
        pstmt = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(index++, params.get(i));
            }
        }
        // 得到数据集
        resultSet = pstmt.executeQuery();
        // 从数据集中提取数据
        ResultSetMetaData metaData = resultSet.getMetaData();
        // 计算有多少行数据
        int cols_len = metaData.getColumnCount();
        // 把所有数据转换成List格式
        while (resultSet.next()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < cols_len; i++) {
                String cols_name = metaData.getColumnName(i + 1);
                Object cols_value = resultSet.getObject(cols_name);
                if (cols_value == null) {
                    cols_value = "";
                }
                map.put(cols_name, cols_value);
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 释放数据库连接
     */
    public void releaseConn() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.error("releaseConn error:" + e.getMessage());
            }
        }
    }

    public void closeConn() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("closeConn error:" + e.getMessage());
            }
        }
    }
}
