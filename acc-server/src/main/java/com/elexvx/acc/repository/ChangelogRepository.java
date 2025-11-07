package com.elexvx.acc.repository;

import com.elexvx.acc.model.Changelog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangelogRepository extends JpaRepository<Changelog, Long> {
}

