package com.jsj.artdesignserver.repository;

import com.jsj.artdesignserver.entity.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SysMenuRepository extends JpaRepository<SysMenu, Long> {
    List<SysMenu> findByParentId(Long parentId);

    /**
     * 按父节点、排序号、ID排序，确保根节点优先返回
     */
    @Query("select m from SysMenu m order by (case when m.parentId is null then 0 else 1 end) asc, m.parentId asc, m.orderNum asc, m.id asc")
    List<SysMenu> findAllOrdered();
}
