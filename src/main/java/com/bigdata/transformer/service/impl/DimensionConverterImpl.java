package com.bigdata.transformer.service.impl;

import com.bigdata.transformer.model.dim.base.*;
import com.bigdata.transformer.service.IDimensionConverter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Package com.bigdata.transformer.service.impl
 * @Description:
 * @Author elwyn
 * @Date 2017/8/22 21:36
 * @Email elonyong@163.com
 */
public class DimensionConverterImpl implements IDimensionConverter {
    private static final Logger LOGGER = Logger.getLogger(DimensionConverterImpl.class);
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static String URL = "jdbc:mysql://hadoop-senior.ibeifeng.com:3306/report";
    private static String USERNAME = "root";
    private static String PASSWORD = "123456";
    private Map<String, Integer> cache = new LinkedHashMap<String, Integer>() {
        private static final long serialVersionUID = 8894507016522723685L;

        protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
            return this.size() > 5000;
        }

        ;
    };

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            // nothing
        }
    }

    @Override
    public int getDimensionIdByValue(BaseDimension dimension) throws IOException {
        String cacheKey = this.buildCacheKey(dimension); // 获取cache key
        if (this.cache.containsKey(cacheKey)) {
            return this.cache.get(cacheKey);
        }

        Connection conn = null;
        try {
            // 1. 查看数据库中是否有对应的值，有则返回
            // 2. 如果第一步中，没有值；先插入我们dimension数据， 获取id
            String[] sql = null; // 具体执行sql数组
            if (dimension instanceof DateDimension) {
                sql = this.buildDateSql();
            } else if (dimension instanceof PlatformDimension) {
                sql = this.buildPlatformSql();
            } else if (dimension instanceof BrowserDimension) {
                sql = this.buildBrowserSql();
            } else if (dimension instanceof KpiDimension) {
                sql = this.buildKpiSql();
            } else {
                throw new IOException("不支持此dimensionid的获取:" + dimension.getClass());
            }

            conn = this.getConnection(); // 获取连接
            int id = 0;
            synchronized (this) {
                id = this.executeSql(conn, cacheKey, sql, dimension);
            }
            return id;
        } catch (Throwable e) {
            LOGGER.error("操作数据库出现异常", e);
            throw new IOException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // nothing
                }
            }
        }
    }

    /**
     * 获取数据库connection连接
     *
     * @return
     * @throws SQLException
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * 创建cache key
     *
     * @param dimension
     * @return
     */
    private String buildCacheKey(BaseDimension dimension) {
        StringBuilder sb = new StringBuilder();
        if (dimension instanceof DateDimension) {
            sb.append("date_dimension");
            DateDimension date = (DateDimension) dimension;
            sb.append(date.getYear()).append(date.getSeason()).append(date.getMonth());
            sb.append(date.getWeek()).append(date.getDay()).append(date.getType());
        } else if (dimension instanceof PlatformDimension) {
            sb.append("platform_dimension");
            PlatformDimension platform = (PlatformDimension) dimension;
            sb.append(platform.getPlatformName());
        } else if (dimension instanceof BrowserDimension) {
            sb.append("browser_dimension");
            BrowserDimension browser = (BrowserDimension) dimension;
            sb.append(browser.getBrowserName()).append(browser.getBrowserVersion());
        } else if (dimension instanceof KpiDimension) {
            sb.append("kpi_dimension");
            KpiDimension kpi = (KpiDimension) dimension;
            sb.append(kpi.getKpiName());
        }

        if (sb.length() == 0) {
            throw new RuntimeException("无法创建指定dimension的cachekey：" + dimension.getClass());
        }
        return sb.toString();
    }

    /**
     * 设置参数
     *
     * @param pstmt
     * @param dimension
     * @throws SQLException
     */
    private void setArgs(PreparedStatement pstmt, BaseDimension dimension) throws SQLException {
        int i = 0;
        if (dimension instanceof DateDimension) {
            DateDimension date = (DateDimension) dimension;
            pstmt.setInt(++i, date.getYear());
            pstmt.setInt(++i, date.getSeason());
            pstmt.setInt(++i, date.getMonth());
            pstmt.setInt(++i, date.getWeek());
            pstmt.setInt(++i, date.getDay());
            pstmt.setString(++i, date.getType());
            pstmt.setDate(++i, new Date(date.getCalendar().getTime()));
        } else if (dimension instanceof PlatformDimension) {
            PlatformDimension platform = (PlatformDimension) dimension;
            pstmt.setString(++i, platform.getPlatformName());
        } else if (dimension instanceof BrowserDimension) {
            BrowserDimension browser = (BrowserDimension) dimension;
            pstmt.setString(++i, browser.getBrowserName());
            pstmt.setString(++i, browser.getBrowserVersion());
        } else if (dimension instanceof KpiDimension) {
            KpiDimension kpi = (KpiDimension) dimension;
            pstmt.setString(++i, kpi.getKpiName());
        }
    }

    /**
     * 创建date dimension相关sql
     *
     * @return
     */
    private String[] buildDateSql() {
        String querySql = "SELECT `id` FROM `dimension_date` WHERE `year` = ? AND `season` = ? AND `month` = ? AND `week` = ? AND `day` = ? AND `type` = ? AND `calendar` = ?";
        String insertSql = "INSERT INTO `dimension_date`(`year`, `season`, `month`, `week`, `day`, `type`, `calendar`) VALUES(?, ?, ?, ?, ?, ?, ?)";
        return new String[]{querySql, insertSql};
    }

    /**
     * 创建polatform dimension相关sql
     *
     * @return
     */
    private String[] buildPlatformSql() {
        String querySql = "SELECT `id` FROM `dimension_platform` WHERE `platform_name` = ?";
        String insertSql = "INSERT INTO `dimension_platform`(`platform_name`) VALUES(?)";
        return new String[]{querySql, insertSql};
    }

    /**
     * 创建browser dimension相关sql
     *
     * @return
     */
    private String[] buildBrowserSql() {
        String querySql = "SELECT `id` FROM `dimension_browser` WHERE `browser_name` = ? AND `browser_version` = ?";
        String insertSql = "INSERT INTO `dimension_browser`(`browser_name`, `browser_version`) VALUES(?, ?)";
        return new String[]{querySql, insertSql};
    }

    /**
     * 创建kpi dimension相关sql
     *
     * @return
     */
    private String[] buildKpiSql() {
        String querySql = "SELECT `id` FROM `dimension_kpi` WHERE `kpi_name` = ?";
        String insertSql = "INSERT INTO `dimension_kpi`(`kpi_name`) VALUES(?)";
        return new String[]{querySql, insertSql};
    }


    /**
     * 具体执行sql的方法
     *
     * @param conn
     * @param cacheKey
     * @param sqls
     * @param dimension
     * @return
     * @throws SQLException
     */
    @SuppressWarnings("resource")
    private int executeSql(Connection conn, String cacheKey, String[] sqls, BaseDimension dimension) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sqls[0]); // 创建查询sql的pstmt对象
            // 设置参数
            this.setArgs(pstmt, dimension);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // 返回值
            }
            // 代码运行到这儿，表示该dimension在数据库中不存储，进行插入
            pstmt = conn.prepareStatement(sqls[1], Statement.RETURN_GENERATED_KEYS);
            // 设置参数
            this.setArgs(pstmt, dimension);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys(); // 获取返回的自动生成的id
            if (rs.next()) {
                return rs.getInt(1); // 获取返回值
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Throwable e) {
                    // nothing
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Throwable e) {
                    // nothing
                }
            }
        }
        throw new RuntimeException("从数据库获取id失败");
    }
}
