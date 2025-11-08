package com.elexvx.acc.repo;

import com.elexvx.acc.entity.SysGlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysGlobalSettingRepository extends JpaRepository<SysGlobalSetting, Long> {
  Optional<SysGlobalSetting> findBySettingKey(String settingKey);
}
