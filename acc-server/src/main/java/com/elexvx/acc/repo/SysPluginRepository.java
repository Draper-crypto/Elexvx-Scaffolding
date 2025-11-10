package com.elexvx.acc.repo;

import com.elexvx.acc.entity.SysPlugin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysPluginRepository extends JpaRepository<SysPlugin, Long> {
  Optional<SysPlugin> findByPluginKey(String pluginKey);
}
