package com.biubiu.controller;

import com.biubiu.constant.DatabaseType;
import com.biubiu.constant.DbResponse;
import com.biubiu.entity.DbProperty;
import com.biubiu.model.*;
import com.biubiu.util.DbUtil;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhanghaibiao on 2017/9/6.
 */
@RestController
@RequestMapping("/db")
public class DbController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbController.class);

    public static ConcurrentHashMap<String, Map<String, Connection>> connectionCache = new ConcurrentHashMap<>();

    @Autowired
    private Environment environment;

    @ApiOperation("获取数据库连接")
    @RequestMapping(value = "/connect", method = RequestMethod.POST)
    public String connect(@RequestBody @Valid ConnectionRequest request) {
        String type = request.getType();
        String dbType = DatabaseType.getDriver(type);
        String dbParams = DatabaseType.getParams(type);
        if (dbType == null || dbParams == null) {
            return DbResponse.CONNECT_FAIL.getMessage();
        }
        String driver = environment.getProperty(dbType);
        String url = request.getUrl().concat(dbParams);
        DbProperty dbProperty = new DbProperty(driver, url, request.getUsername(), request.getPassword());
        Connection connection = DbUtil.getConnection(dbProperty);
        return handleConnectResult(request, connection);
    }

    @ApiOperation("动态获取数据库连接")
    @RequestMapping(value = "/dynamicConnect", method = RequestMethod.POST)
    public String dynamicConnect(@RequestBody @Valid DynamicConnectionRequest request) {
        String type = request.getType();
        String dbType = DatabaseType.getDriver(type);
        String dbParams = DatabaseType.getParams(type);
        if (dbType == null || dbParams == null) {
            return DbResponse.CONNECT_FAIL.getMessage();
        }
        String driver = environment.getProperty(dbType);
        String url = request.getUrl().concat(dbParams);
        DbProperty dbProperty = new DbProperty(driver, url, request.getUsername(), request.getPassword());
        Connection connection = DbUtil.dynamicLoadRemoteDriver(request.getJarUrl(), dbProperty);
        return handleConnectResult(request, connection);
    }

    @ApiOperation("获取数据库表")
    @RequestMapping(value = "/tables", method = RequestMethod.POST)
    public Map<String, String> listTable(@RequestBody @Valid ListTablesRequest request) {
        Connection connection = getCacheConnection(request.getUserCode(), request.getUniqueConnectionCode());
        if (connection == null) {
            return Collections.EMPTY_MAP;
        }
        return DbUtil.getTableAndRemark(connection);
    }

    @ApiOperation("获取表字段详细信息")
    @RequestMapping(value = "/columns", method = RequestMethod.POST)
    public Map<String, Map<String, String>> listColumns(@RequestBody @Valid ListColumnsRequest request) {
        Connection connection = getCacheConnection(request.getUserCode(), request.getUniqueConnectionCode());
        if (connection == null) {
            return Collections.EMPTY_MAP;
        }
        return DbUtil.getColumnAndDetail(connection, request.getTableName());
    }

    @ApiOperation("查询数据集")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public Object query(@RequestBody @Valid InquiryRequest request) {
        Connection connection = getCacheConnection(request.getUserCode(), request.getUniqueConnectionCode());
        if (connection == null) {
            return Collections.EMPTY_LIST;
        }
        return DbUtil.query(connection, request.getSql(), request.getParams());
    }

    @ApiOperation("查询总记录数")
    @RequestMapping(value = "/count", method = RequestMethod.POST)
    public int count(@RequestBody @Valid InquiryRequest request) {
        Connection connection = getCacheConnection(request.getUserCode(), request.getUniqueConnectionCode());
        if (connection == null) {
            return 0;
        }
        return DbUtil.count(connection, request.getSql(), request.getParams());
    }

    @ApiOperation("关闭数据库连接")
    @RequestMapping(value = "/close", method = RequestMethod.POST)
    public String close(@RequestBody @Valid ClosureRequest request) {
        String userCode = request.getUserCode();
        String uniqueConnectCode = request.getUniqueConnectionCode();
        Connection connection = getCacheConnection(userCode, uniqueConnectCode);
        if (connection == null) {
            return DbResponse.CLOSE_FAIL.getMessage();
        }
        try {
            connectionCache.get(userCode).remove(uniqueConnectCode);
            DbUtil.close(null, null, connection);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            return DbResponse.CLOSE_FAIL.getMessage();
        }
        return DbResponse.CLOSE_SUCCESS.getMessage();
    }

    private Connection getCacheConnection(String userCode, String uniqueConnectionCode) {
        Map<String, Connection> connectionMap = connectionCache.get(userCode);
        if (connectionMap == null) {
            return null;
        }
        return connectionMap.get(uniqueConnectionCode);
    }

    private String handleConnectResult(ConnectionRequest request, Connection connection) {
        if (connection == null) {
            return DbResponse.CONNECT_FAIL.getMessage();
        } else {
            String userCode = request.getUserCode();
            String[] dividedUrl = request.getUrl().split("//");
            if (dividedUrl.length < 2) {
                return DbResponse.CONNECT_FAIL.getMessage();
            }
            String uniqueConnectionCode = dividedUrl[1];
            Map<String, Connection> connections = connectionCache.get(userCode);
            if (connections == null || connections.size() == 0) {
                Map<String, Connection> connectionMap = Maps.newHashMap();
                connectionMap.put(uniqueConnectionCode, connection);
                connectionCache.put(userCode, connectionMap);
            } else {
                connections.put(uniqueConnectionCode, connection);
            }
            return DbResponse.CONNECT_SUCCESS.getMessage();
        }
    }

}
