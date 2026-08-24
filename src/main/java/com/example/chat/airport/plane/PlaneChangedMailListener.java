package com.example.chat.airport.plane;

import com.example.chat.airport.plane.event.PlaneChangedEvent;
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

import java.util.List;

// 항공편 변경 이벤트를 받아 해당 항공편을 구독한 회원에게 알림 메일을 발송하는 리스너
@Slf4j
@RequiredArgsConstructor
@Component
public class PlaneChangedMailListener {
    private final PlaneSubscriptionRepository planeSubscriptionRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async(AsyncConfig.EVENT_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPlaneChanged(PlaneChangedEvent event) {
        // 변경된 항공편을 관심 항공편으로 설정한 Member들 조회
        List<Member> subscribers = planeSubscriptionRepository.findMembersByPlaneFlightId(event.getFlightId());

        if (subscribers.isEmpty()) return;

        for (Member member : subscribers) {
            try {
                sendFlightStatusMail(member.getEmail(), event);
            } catch (Exception e) {
                log.error("이메일 발송 실패: email={} flightId={}", member.getEmail(), event.getFlightId(), e);
            }
        }
    }

    private void sendFlightStatusMail(String email, PlaneChangedEvent event) throws Exception {
        Context ctx = getContext(event);

        String html = templateEngine.process("mail/flight-status-changed", ctx);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject("[인천공항] 항공편 " + event.getFlightId() + " 상태 변경 알림");
        helper.setText(html, true);

        mailSender.send(mimeMessage);
        log.info("항공편 변경 이메일 발송 완료: email={} flightId={}", email, event.getFlightId());
    }

    private static Context getContext(PlaneChangedEvent event) {
        Context ctx = new Context();
        ctx.setVariable("flightId", event.getFlightId());
        ctx.setVariable("airLine", event.getAirLine());
        ctx.setVariable("airport", event.getAirport());
        ctx.setVariable("scheduleDateTime", event.getScheduleDateTime());
        ctx.setVariable("prevRemark", event.getPrevRemark());
        ctx.setVariable("remark", event.getNewRemark());
        ctx.setVariable("prevEstimatedDateTime", event.getPrevEstimatedDateTime());
        ctx.setVariable("estimatedDateTime", event.getNewEstimatedDateTime());
        ctx.setVariable("prevGatenumber", event.getPrevGatenumber());
        ctx.setVariable("gatenumber", event.getNewGatenumber());

        return ctx;
    }
}
