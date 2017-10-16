package com.biubiu.util;

import com.biubiu.entity.DbProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhanghaibiao on 2017/9/6.
 */
public class DbUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbUtil.class);

    /**
     * 获取数据库连接
     *
     * @param dbProperty
     * @return
     */
    public static Connection getConnection(DbProperty dbProperty) {
        Connection connection = null;
        try {
            Class.forName(dbProperty.getDriver());
            connection = DriverManager.getConnection(dbProperty.getUrl(), dbProperty.getUsername(), dbProperty.getPassword());
        } catch (ClassNotFoundException e) {
            LOGGER.error("ClassNotFoundException", e);
        } catch (SQLException e) {
            LOGGER.error("SQLException", e);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
        return connection;
    }

    /**
     * 动态加载远程驱动
     *
     * @param jarUrl
     * @param dbProperty
     * @return
     */
    public static Connection dynamicLoadRemoteDriver(String jarUrl, DbProperty dbProperty) {
        Connection connection = null;
        File file = new File(jarUrl);
        try {
            URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, Thread.currentThread().getContextClassLoader());
            Class clazz = loader.loadClass(dbProperty.getDriver());
            Driver driver = (Driver) clazz.newInstance();
            Properties properties = new Properties();
            properties.put("user", dbProperty.getUsername());
            properties.put("password", dbProperty.getPassword());
            connection = driver.connect(dbProperty.getUrl(), properties);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return connection;
    }

    /**
     * 获取所有数据表
     *
     * @param connection
     * @return
     */
    public static List<String> getTables(Connection connection) {
        List<String> tables = Lists.newArrayList();
        DatabaseMetaData metaData = null;
        ResultSet resultSet = null;
        String tableName = null;
        try {
            metaData = connection.getMetaData();
            resultSet = metaData.getTables(null, "%", "%", new String[]{"TABLE"});
            while (resultSet.next()) {
                tableName = resultSet.getString("TABLE_NAME");
                tables.add(tableName);
            }
        } catch (SQLException e) {
            LOGGER.error("SQLException", e);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
        return tables;
    }

    /**
     * 获取所有数据表及表备注
     *
     * @param connection
     * @return
     */
    public static Map<String, String> getTableAndRemark(Connection connection) {
        Map<String, String> tables = Maps.newLinkedHashMap();
        DatabaseMetaData metaData = null;
        ResultSet resultSet = null;
        String tableName = null;
        String tableRemark = null;
        try {
            metaData = connection.getMetaData();
            resultSet = metaData.getTables(null, "%", "%", new String[]{"TABLE"});
            while (resultSet.next()) {
                tableName = resultSet.getString("TABLE_NAME");
                tableRemark = resultSet.getString("REMARKS");
                tables.put(tableName, tableRemark);
            }
        } catch (SQLException e) {
            LOGGER.error("SQLException", e);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
        return tables;
    }

    /**
     * 获取数据表字段
     *
     * @param connection
     * @param tableName
     * @return
     */
    public static List<String> getColumns(Connection connection, String tableName) {
        List<String> columns = Lists.newArrayList();
        DatabaseMetaData metaData = null;
        ResultSet resultSet = null;
        String columnName = null;
        try {
            metaData = connection.getMetaData();
            resultSet = metaData.getColumns(null, "%", tableName, "%");
            while (resultSet.next()) {
                columnName = resultSet.getString("COLUMN_NAME");
                columns.add(columnName);
            }
        } catch (SQLException e) {
            LOGGER.error("SQLException", e);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
        return columns;
    }

    /**
     * 获取数据表字段及其详细信息
     *
     * @param connection
     * @param tableName
     * @return
     */
    public static Map<String, Map<String, String>> getColumnAndDetail(Connection connection, String tableName) {
        Map<String, Map<String, String>> columns = Maps.newLinkedHashMap();
        DatabaseMetaData metaData = null;
        ResultSet resultSet = null;
        String columnName = null;
        String columnRemark = null;
        String columnType = null;
        try {
            metaData = connection.getMetaData();
            resultSet = metaData.getColumns(null, "%", tableName, "%");
            while (resultSet.next()) {
                columnName = resultSet.getString("COLUMN_NAME");
                columnRemark = resultSet.getString("REMARKS");
                columnType = resultSet.getString("TYPE_NAME");
                Map<String, String> columnDetailMap = Maps.newLinkedHashMap();
                if (!Strings.isNullOrEmpty(columnRemark)) {
                    columnDetailMap.put("REMARKS", columnRemark);
                }
                if (!Strings.isNullOrEmpty(columnType)) {
                    columnDetailMap.put("TYPE_NAME", columnType);
                }
                columns.put(columnName, columnDetailMap);
            }
        } catch (SQLException e) {
            LOGGER.error("SQLException", e);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
        return columns;
    }

    /**
     * 查询总记录数
     *
     * @param connection
     * @param sql
     * @param args
     * @return
     */
    public static int count(Connection connection, String sql, Object... args) {
        int count = 0;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    preparedStatement.setObject(i + 1, args[i]);
                }
            }
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);  //对总记录数赋值
            }
            return count;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return count;
    }

    /**
     * 查询结果集
     *
     * @param connection
     * @param sql
     * @param args
     * @return
     */
    public static Object query(Connection connection, String sql, Object... args) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    preparedStatement.setObject(i + 1, args[i]);
                }
            }
            resultSet = preparedStatement.executeQuery();
            connection.commit();
            return convertList(resultSet);
        } catch (Exception e) {
            try {
                connection.rollback();
                LOGGER.error(e.getMessage());
            } catch (SQLException e1) {
                LOGGER.error(e1.getMessage());
            }
        }
        return Collections.EMPTY_LIST;
    }


    /**
     * ResultSet转换为List
     *
     * @param rs
     * @return
     */
    private static List convertList(ResultSet rs) throws SQLException {
        List<Map> result = Lists.newArrayList();
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        while (rs.next()) {
            Map rowData = Maps.newHashMap();
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));
            }
            result.add(rowData);
        }
        return result;
    }

    /**
     * 关闭连接
     *
     * @param rs
     * @param st
     * @param connection
     */
    public static void close(ResultSet rs, Statement st, Connection connection) throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (st != null) {
            st.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

}
