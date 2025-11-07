package com.elexvx.acc.repository;

import com.elexvx.acc.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByEnabledTrueOrderByOrderNumAsc();
    List<Menu> findByParentIdOrderByOrderNumAsc(Long parentId);
}

