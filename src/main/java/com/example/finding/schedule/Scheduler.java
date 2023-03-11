package com.example.finding.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component // 추가
@EnableAsync // 추가
public class Scheduler {
    @Scheduled(cron = "0 15 10 15 * ?") // 매월 15일 오전 10시 15분에 실행
// @Scheduled(cron = "0 15 10 15 11 ?") // 11월 15일 오전 10시 15분에 실행
// @Scheduled(cron = "${cron.expression}")
// @Scheduled(cron = "0 15 10 15 * ?", zone = "Europe/Paris") // timezone 설정
    public void scheduleTaskUsingCronExpression() {
        long now = System.currentTimeMillis() / 1000;
        log.info("schedule tasks using cron jobs - {}", now);
    }
}
