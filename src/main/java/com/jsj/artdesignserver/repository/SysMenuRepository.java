package com.jsj.artdesignserver.repository;

import com.jsj.artdesignserver.entity.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SysMenuRepository extends JpaRepository<SysMenu, Long> {
    List<SysMenu> findByParentId(Long parentId);

    @Query("select m from SysMenu m order by m.parentId asc nulls first, m.orderNum asc nulls last, m.id asc")
    List<SysMenu> findAllOrdered();
}

