/**
 * 仓储抽象包。
 * <p>
 * Repository 只声明领域层需要的持久化能力，具体 MyBatis-Plus 查询、PO 转换和事务细节
 * 放在 infrastructure 层实现，以保持领域层与数据库技术解耦。
 * </p>
 */
package me.link.bootstrap.domain.repository;
