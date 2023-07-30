package com.aliens.friendship.domain.report.service;

import com.aliens.db.report.entity.ReportEntity;
import com.aliens.db.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;

    @Transactional
    public void register(ReportEntity reportEntity) {
        reportRepository.save(reportEntity);
    }

    public List<ReportEntity> findAll() {
        return reportRepository.findAll();
    }
}