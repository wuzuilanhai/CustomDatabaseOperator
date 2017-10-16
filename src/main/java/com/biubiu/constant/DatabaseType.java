package com.biubiu.constant;

/**
 * Created by zhanghaibiao on 2017/9/6.
 */
public enum DatabaseType {
    mysql("mysql", DbDriver.MYSQL_DRIVER, DbDriver.MYSQL_PARAMS),
    pgsql("postgresql", DbDriver.PGSQL_DRIVER, DbDriver.PGSQL_PARAMS);

    DatabaseType(String type, String driver, String params) {
        this.type = type;
        this.driver = driver;
        this.params = params;
    }

    private String type;

    private String driver;

    private String params;

    public String getType() {
        return type;
    }

    public String getDriver() {
        return driver;
    }

    public String getParams() {
        return params;
    }

    public static String getDriver(String type) {
        for (DatabaseType databaseType : DatabaseType.values()) {
            if (databaseType.getType().equals(type)) {
                return databaseType.getDriver();
            }
        }
        return null;
    }

    public static String getParams(String type) {
        for (DatabaseType databaseType : DatabaseType.values()) {
            if (databaseType.getType().equals(type)) {
                return databaseType.getParams();
            }
        }
        return null;
    }

}
