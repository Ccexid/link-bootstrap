/**
 * 领域工厂包。
 * <p>
 * Factory 集中处理创建和变更前的必填、格式、租户边界等校验，并负责必要的值归一化。
 * 对外服务应优先调用工厂方法，再将实体交给仓储保存。
 * </p>
 */
package me.link.bootstrap.domain.factory;
