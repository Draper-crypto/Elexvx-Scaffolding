package com.elexvx.acc.repo;

import com.elexvx.acc.entity.SysChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SysChangeLogRepository extends JpaRepository<SysChangeLog, Long>, JpaSpecificationExecutor<SysChangeLog> {}
