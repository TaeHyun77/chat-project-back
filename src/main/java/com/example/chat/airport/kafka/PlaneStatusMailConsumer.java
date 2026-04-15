package com.example.chat.airport.kafka;

import com.example.chat.airport.kafka.message.PlaneChangedMessage;
import com.example.chat.member.Member;
import com.example.chat.member.PlaneSubscriptionRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PlaneStatusMailConsumer {

    private final PlaneSubscriptionRepository planeSubscriptionRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @KafkaListener(topics = "airport.plane.changed", groupId = "plane-mail-notifier")
    public void onPlaneChanged(PlaneChangedMessage message) {

        // 변경된 항공편을 관심 항공편으로 설정한 Member들 조회
        List<Member> subscribers = planeSubscriptionRepository.findMembersByPlaneFlightId(message.getFlightId());

        if (subscribers.isEmpty()) return;

        for (Member member : subscribers) {
            try {
                sendFlightStatusMail(member.getEmail(), message);
            } catch (Exception e) {
                log.error("이메일 발송 실패: email={} flightId={}", member.getEmail(), message.getFlightId(), e);
            }
        }
    }

    private void sendFlightStatusMail(String email, PlaneChangedMessage message) throws Exception {
        Context ctx = getContext(message);

        String html = templateEngine.process("mail/flight-status-changed", ctx);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject("[인천공항] 항공편 " + message.getFlightId() + " 상태 변경 알림");
        helper.setText(html, true);

        mailSender.send(mimeMessage);
        log.info("항공편 변경 이메일 발송 완료: email={} flightId={}", email, message.getFlightId());
    }

    private static Context getContext(PlaneChangedMessage message) {
        Context ctx = new Context();
        ctx.setVariable("flightId", message.getFlightId());
        ctx.setVariable("airLine", message.getAirLine());
        ctx.setVariable("airport", message.getAirport());
        ctx.setVariable("scheduleDateTime", message.getScheduleDateTime());
        ctx.setVariable("prevRemark", message.getPrevRemark());
        ctx.setVariable("remark", message.getNewRemark());
        ctx.setVariable("prevEstimatedDateTime", message.getPrevEstimatedDateTime());
        ctx.setVariable("estimatedDateTime", message.getNewEstimatedDateTime());
        ctx.setVariable("prevGatenumber", message.getPrevGatenumber());
        ctx.setVariable("gatenumber", message.getNewGatenumber());

        return ctx;
    }
}
