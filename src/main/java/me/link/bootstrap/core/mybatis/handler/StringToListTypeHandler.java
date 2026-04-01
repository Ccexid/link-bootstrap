package me.link.bootstrap.core.mybatis.handler;

import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字符串与 List 类型转换器
 * <p>
 * 用于 MyBatis 框架中 Java {@code List<String>} 类型与数据库 VARCHAR 类型之间的转换
 * 将 List 以逗号分隔的字符串形式存储到数据库，从数据库读取时再转换回 List
 * </p>
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(List.class)
public class StringToListTypeHandler extends BaseTypeHandler<List<String>> {

    /**
     * 将 List<String> 类型转换为 VARCHAR 类型存入数据库
     *
     * @param ps         PreparedStatement 对象
     * @param i          参数索引位置
     * @param parameter  要转换的 List<String> 参数
     * @param jdbcType   JDBC 类型
     * @throws SQLException SQL 异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        // List 转 String 存入数据库
        ps.setString(i, String.join(",", parameter));
    }

    /**
     * 从 ResultSet 中根据列名获取数据并转换为 List<String>
     *
     * @param rs         ResultSet 结果集对象
     * @param columnName 列名
     * @return 转换后的 List<String>，如果值为空则返回空列表
     * @throws SQLException SQL 异常
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return StringToList(rs.getString(columnName));
    }

    /**
     * 从 ResultSet 中根据列索引获取数据并转换为 List<String>
     *
     * @param rs           ResultSet 结果集对象
     * @param columnIndex  列索引
     * @return 转换后的 List<String>，如果值为空则返回空列表
     * @throws SQLException SQL 异常
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return StringToList(rs.getString(columnIndex));
    }

    /**
     * 从 CallableStatement 中根据列索引获取数据并转换为 List<String>
     *
     * @param cs           CallableStatement 存储过程调用对象
     * @param columnIndex  列索引
     * @return 转换后的 List<String>，如果值为空则返回空列表
     * @throws SQLException SQL 异常
     */
    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return StringToList(cs.getString(columnIndex));
    }

    /**
     * 将数据库中的逗号分隔字符串转换为 List<String>
     * <p>
     * 如果字符串为空或 null，返回空列表；否则按逗号分割并去除每个元素的首尾空格
     * </p>
     *
     * @param columnValue 数据库中的字符串值
     * @return 转换后的 List<String>
     */
    private List<String> StringToList(String columnValue) {
        if (StrUtil.isBlank(columnValue)) {
            return Collections.emptyList();
        }
        return Arrays.stream(columnValue.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
