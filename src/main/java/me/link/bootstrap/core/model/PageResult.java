package me.link.bootstrap.core.model;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应结果封装类 (基于 Java Record)
 * 适配主流前端分页插件与 MyBatis-Plus
 *
 * @param list       数据列表
 * @param total      总条数
 * @param pageSize   每页条数
 * @param pageIndex  当前页码
 */
public record PageResult<T>(
        List<T> list,
        long total,
        long pageSize,
        long pageIndex
) implements Serializable {

    /**
     * 紧凑型构造函数：确保 list 永远不为 null，避免前端 map 遍历报错
     */
    public PageResult {
        if (list == null) {
            list = Collections.emptyList();
        }
    }

    /**
     * 静态工厂：从 MyBatis-Plus 的 IPage 对象直接转换
     * * @param page IPage 实例
     * @param <T>  数据类型
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getSize(),
                page.getCurrent()
        );
    }

    /**
     * 静态工厂：手动构建（适用于聚合查询或内存分页）
     */
    public static <T> PageResult<T> of(List<T> list, long total, long pageSize, long pageIndex) {
        return new PageResult<>(list, total, pageSize, pageIndex);
    }

    /**
     * 快捷方法：返回空分页数据
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(Collections.emptyList(), 0L, 10L, 1L);
    }

    /**
     * 获取总页数 (逻辑计算)
     */
    public long getTotalPage() {
        if (pageSize <= 0) return 0;
        return (total + pageSize - 1) / pageSize;
    }
}