package com.aliens.db.report.repository;

import com.aliens.db.report.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {

}
