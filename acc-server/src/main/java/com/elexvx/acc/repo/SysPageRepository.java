package com.elexvx.acc.repo;

import com.elexvx.acc.entity.SysPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysPageRepository extends JpaRepository<SysPage, Long> {}

