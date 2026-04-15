package com.example.chat.airport.kafka;

import com.example.chat.airport.kafka.message.CongestionMessage;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CongestionAlertConsumer {

    private final PlaneSubscriptionRepository planeSubscriptionRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    @KafkaListener(topics = "airport.congestion.changed", groupId = "congestion-alerter")
    public void onCongestionChanged(CongestionMessage message) {
        // 혼잡도 알림 ON + 출발 임박(6시간 이내) 항공편 보유 회원 조회
        LocalDateTime now = LocalDateTime.now();
        String start = now.format(DT_FORMATTER);
        String end = now.plusHours(6).format(DT_FORMATTER);

        List<Member> subscribers = planeSubscriptionRepository.findCongestionAlertEnabledMembersWithImminentFlights(start, end);

        if (subscribers.isEmpty()) return;

        for (Member member : subscribers) {
            try {
                sendCongestionAlert(member.getEmail(), message);
            } catch (Exception e) {
                log.error("혼잡도 알림 이메일 발송 실패: email={}", member.getEmail(), e);
            }
        }
    }

    private void sendCongestionAlert(String email, CongestionMessage message) throws Exception {
        Context ctx = new Context();
        ctx.setVariable("date", message.getDate());
        ctx.setVariable("timeZone", message.getTimeZone());
        ctx.setVariable("t1Depart1", message.getT1Depart1());
        ctx.setVariable("t1Depart2", message.getT1Depart2());
        ctx.setVariable("t1Depart3", message.getT1Depart3());
        ctx.setVariable("t1Depart4", message.getT1Depart4());
        ctx.setVariable("t1Depart5", message.getT1Depart5());
        ctx.setVariable("t1Depart6", message.getT1Depart6());
        ctx.setVariable("t2Depart1", message.getT2Depart1());
        ctx.setVariable("t2Depart2", message.getT2Depart2());
        ctx.setVariable("newT1Sum", message.getNewT1Sum());
        ctx.setVariable("newT2Sum", message.getNewT2Sum());

        String html = templateEngine.process("mail/congestion-alert", ctx);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject("[인천공항] 출국장 혼잡도 알림 - " + message.getTimeZone());
        helper.setText(html, true);

        mailSender.send(mimeMessage);
        log.info("혼잡도 알림 이메일 발송 완료: email={}", email);
    }
}
