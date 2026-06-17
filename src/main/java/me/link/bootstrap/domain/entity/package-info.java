/**
 * 领域实体包。
 * <p>
 * Entity 表达业务对象的状态和行为，{@code create} 用于新建聚合状态，
 * {@code restore} 用于从持久化数据还原对象。新增或变更业务规则时，优先通过
 * factory 包中的领域工厂进入，避免绕过校验。
 * </p>
 */
package me.link.bootstrap.domain.entity;
