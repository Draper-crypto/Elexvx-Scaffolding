package com.elexvx.acc.repo;

import com.elexvx.acc.entity.SysOperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SysOperationLogRepository
    extends JpaRepository<SysOperationLog, Long>, JpaSpecificationExecutor<SysOperationLog> {}
