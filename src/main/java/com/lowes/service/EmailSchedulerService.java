//package com.lowes.service;
//
//import com.lowes.entity.Phase;
//import com.lowes.entity.enums.PhaseStatus;
//import com.lowes.repository.PhaseRepository;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//public class EmailSchedulerService {
//    @Autowired
//    PhaseRepository phaseRepository;
//
//    @Autowired
//    JavaMailSender mailSender;
//
//    @Scheduled(cron = "0 0 8 * * *") // Every day at 8 AM
//    public void sendPhaseReminders() {
//        LocalDate today = LocalDate.now();
//
//        List<Phase> upcoming = phaseRepository.findByStartDate(today.plusDays(3));
//        for (Phase phase : upcoming) {
//            String html = buildUpcomingPhaseEmail(phase);
//            sendHtmlEmail(phase.getProject().getUser().getEmail(), "Phase Starts in 3 Days", html);
//        }
//
//        List<Phase> overdue = phaseRepository.findByEndDateBeforeAndPhaseStatusNot(today, PhaseStatus.COMPLETED);
//        for (Phase phase : overdue) {
//            String html = buildOverduePhaseEmail(phase);
//            sendHtmlEmail(phase.getProject().getUser().getEmail(), "Phase Overdue Alert", html);
//        }
//    }
//
//    private void sendHtmlEmail(String to, String subject, String htmlBody) {
//        MimeMessage message = mailSender.createMimeMessage();
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(htmlBody, true);
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            System.out.println("error in sendhtmlemail"+e.getMessage()); // Or use logger
//        }
//    }
//
//    private String buildUpcomingPhaseEmail(Phase phase) {
//        return "<html><body>" +
//                "<h2>Upcoming Phase Reminder</h2>" +
//                "<p>Dear " + phase.getProject().getUser().getName() + ",</p>" +
//                "<p>The phase <strong>" + phase.getPhaseName() + "</strong> for your project <strong>" +
//                phase.getProject().getName() + "</strong> is starting in <strong>3 days</strong>.</p>" +
//                "<p>Start Date: " + phase.getStartDate() + "</p>" +
//                "<br/><p>Thank you,<br/>Renovation Team</p>" +
//                "</body></html>";
//    }
//
//    private String buildOverduePhaseEmail(Phase phase) {
//        return "<html><body>" +
//                "<h2>Phase Overdue</h2>" +
//                "<p>Dear " + phase.getProject().getUser().getName() + ",</p>" +
//                "<p>The phase <strong>" + phase.getPhaseName() + "</strong> for your project <strong>" +
//                phase.getProject().getName() + "</strong> was scheduled to end on <strong>" +
//                phase.getEndDate() + "</strong> and is still <strong>not marked completed</strong>.</p>" +
//                "<p>Please update the status or extend the end date.</p>" +
//                "<br/><p>Regards,<br/>Renovation Team</p>" +
//                "</body></html>";
//    }
//}
