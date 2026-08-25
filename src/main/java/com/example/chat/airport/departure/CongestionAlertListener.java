package com.example.chat.airport.departure;

import com.example.chat.airport.departure.event.CongestionAlertEvent;
import com.example.chat.common.DateUtils;
import com.example.chat.config.AsyncConfig;
import com.example.chat.member.Member;
import com.example.chat.member.PlaneSubscriptionRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;

// 출국장 혼잡도 진입 이벤트를 받아 조건을 만족하는 회원에게 알림 메일을 발송하는 리스너, 트랜잭션 커밋 이후 별도 스레드에서 실행

@Slf4j
@RequiredArgsConstructor
@Component
public class CongestionAlertListener {
    private final PlaneSubscriptionRepository planeSubscriptionRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async(AsyncConfig.EVENT_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCongestionChanged(CongestionAlertEvent event) {
        // 혼잡도 알림 ON + 출발 임박(6시간 이내) 항공편 보유 회원 조회
        LocalDateTime now = LocalDateTime.now();
        String start = now.format(DateUtils.BASIC_DATE_TIME);
        String end = now.plusHours(6).format(DateUtils.BASIC_DATE_TIME);

        List<Member> subscribers = planeSubscriptionRepository.findCongestionAlertEnabledMembersWithImminentFlights(start, end, event.getBusyTerminal());

        if (subscribers.isEmpty()) return;

        for (Member member : subscribers) {
            try {
                sendCongestionAlert(member.getEmail(), event);
            } catch (Exception e) {
                log.error("혼잡도 알림 이메일 발송 실패: email={}", member.getEmail(), e);
            }
        }
    }

    private void sendCongestionAlert(String email, CongestionAlertEvent event) throws Exception {
        Context ctx = new Context();
        ctx.setVariable("date", event.getDate());
        ctx.setVariable("timeZone", event.getTimeZone());
        ctx.setVariable("t1Gates", event.getT1Gates());
        ctx.setVariable("t2Gates", event.getT2Gates());

        String html = templateEngine.process("mail/congestion-alert", ctx);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject("[인천공항] 출국장 혼잡도 알림 - " + event.getTimeZone());
        helper.setText(html, true);

        mailSender.send(mimeMessage);
        log.info("혼잡도 알림 이메일 발송 완료: email={}", email);
    }
}
