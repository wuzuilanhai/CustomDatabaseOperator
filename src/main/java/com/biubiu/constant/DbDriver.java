package com.biubiu.constant;

/**
 * Created by zhanghaibiao on 2017/9/6.
 */
public interface DbDriver {

    String MYSQL_DRIVER = "mysql.jdbc.driver";

    String MYSQL_PARAMS = "?serverTimezone=UTC&characterEncoding=utf-8&useSSL=false&useUnicode=true";

    String PGSQL_DRIVER = "postgresql.jdbc.driver";

    String PGSQL_PARAMS = "?characterEncoding#UTF-8";

    String ORACLE_DRIVER="oracle.jdbc.driver";

    String DB2_DRIVER="DB2.jdbc.driver";

    String SYSBASE_DRIVER="sysbase.jdbc.driver";

    String SQLSERVER2000_DRIVER="sqlserver2000.jdbc.driver";

    String SQLSERVER0508_DRIVER="sqlserver2005-2008.jdbc.driver";

}
