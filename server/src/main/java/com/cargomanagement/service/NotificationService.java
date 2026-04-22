package com.cargomanagement.service;

import com.cargomanagement.models.Cargo;
import com.cargomanagement.models.Delivery;
import com.cargomanagement.models.Shipment;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final JavaMailSender mailSender;
    private final List<String> defaultRecipients;
    private final String fromAddress;

    public NotificationService(JavaMailSender mailSender,
                               @Value("${notification.email.recipients:}") String recipients,
                               @Value("${notification.email.from:}") String fromAddress) {
        this.mailSender = mailSender;
        this.defaultRecipients = parseRecipients(recipients);
        this.fromAddress = fromAddress;
    }

    @PostConstruct
    void logConfiguration() {
        if (defaultRecipients.isEmpty()) {
            log.warn("Email notifications are configured but no recipients were provided. Set 'notification.email.recipients' to enable outgoing emails.");
        } else {
            log.info("Email notifications enabled for recipients: {}", defaultRecipients);
        }
    }

    public void notifyCargoCreated(Cargo cargo) {
        if (cargo == null) {
            return;
        }
        String subject = "Cargo Created - #" + cargo.getCargoId();
        String body = buildCargoMessage("A new cargo record was created.", cargo);
        sendNotification(null, subject, body);
    }

    public void notifyCargoUpdated(Cargo cargo) {
        if (cargo == null) {
            return;
        }
        String subject = "Cargo Updated - #" + cargo.getCargoId();
        String body = buildCargoMessage("A cargo record was updated.", cargo);
        sendNotification(null, subject, body);
    }

    public void notifyCargoDeleted(Cargo cargo) {
        if (cargo == null) {
            return;
        }
        String subject = "Cargo Deleted - #" + cargo.getCargoId();
        String body = buildCargoMessage("A cargo record was deleted.", cargo);
        sendNotification(null, subject, body);
    }

    public void notifyShipmentCreated(Shipment shipment) {
        if (shipment == null) {
            return;
        }
        String subject = "New Shipment Created - SH" + String.format("%03d", shipment.getShipmentId());
        String body = buildShipmentCreatedEmailHtml(shipment);
        sendHtmlNotification(null, subject, body);
    }

    public void notifyShipmentUpdated(Shipment shipment, String previousStatus) {
        if (shipment == null) {
            return;
        }
        String subject = "Shipment Status Updated - SH" + String.format("%03d", shipment.getShipmentId());
        String body = buildShipmentUpdatedEmailHtml(shipment, previousStatus);
        sendHtmlNotification(null, subject, body);
    }

    public void notifyShipmentDeleted(Shipment shipment) {
        if (shipment == null) {
            return;
        }
        String subject = "Shipment Deleted - SH" + String.format("%03d", shipment.getShipmentId());
        String body = buildShipmentDeletedEmailHtml(shipment);
        sendHtmlNotification(null, subject, body);
    }

    public void notifyDeliveryEvent(Delivery delivery, String action) {
        if (delivery == null) {
            return;
        }
        String subject = "Delivery " + action + " - #" + delivery.getDeliveryId();
        StringBuilder body = new StringBuilder()
            .append("Delivery was ").append(action.toLowerCase()).append(".\n\n")
            .append("Recipient: ").append(delivery.getRecipient()).append('\n');

        if (StringUtils.hasText(delivery.getStatus())) {
            body.append("Status: ").append(delivery.getStatus()).append('\n');
        }

        if (delivery.getActualDeliveryDate() != null) {
            body.append("Completed At: ")
                .append(delivery.getActualDeliveryDate().format(DATE_TIME_FORMATTER))
                .append('\n');
        }

        if (delivery.getShipment() != null) {
            body.append("Shipment Destination: ")
                .append(delivery.getShipment().getDestination())
                .append('\n');

            if (StringUtils.hasText(delivery.getShipment().getStatus())) {
                body.append("Shipment Status: ")
                    .append(delivery.getShipment().getStatus())
                    .append('\n');
            }

            if (StringUtils.hasText(delivery.getShipment().getShipmentCode())) {
                body.append("Shipment Code: ")
                    .append(delivery.getShipment().getShipmentCode())
                    .append('\n');
            }
        }

        sendNotification(null, subject, body.toString());
    }

    public boolean sendCustomEmail(List<String> recipients, String subject, String message) {
        return sendNotification(recipients, subject, message);
    }

    private String buildCargoMessage(String header, Cargo cargo) {
        StringBuilder body = new StringBuilder(header)
            .append('\n').append('\n')
            .append("Type: ").append(cargo.getType()).append('\n')
            .append("Value: $").append(cargo.getValue()).append('\n');

        if (cargo.getWeight() != null) {
            body.append("Weight: ").append(cargo.getWeight());
            if (StringUtils.hasText(cargo.getWeightUnit())) {
                body.append(' ').append(cargo.getWeightUnit());
            }
            body.append('\n');
        }

        if (cargo.getVolume() != null) {
            body.append("Volume: ").append(cargo.getVolume()).append('\n');
        }

        if (StringUtils.hasText(cargo.getDescription())) {
            body.append("Description: ").append(cargo.getDescription()).append('\n');
        }

        if (cargo.getShipment() != null) {
            body.append("Linked Shipment ID: ").append(cargo.getShipment().getShipmentId()).append('\n');
            if (StringUtils.hasText(cargo.getShipment().getShipmentCode())) {
                body.append("Shipment Code: ").append(cargo.getShipment().getShipmentCode()).append('\n');
            }
        }

        return body.toString();
    }

    private boolean sendHtmlNotification(List<String> overrideRecipients, String subject, String htmlBody) {
        List<String> recipients = resolveRecipients(overrideRecipients);
        if (recipients.isEmpty()) {
            log.debug("Skipping email notification '{}' because no recipients are configured.", subject);
            return false;
        }

        if (!StringUtils.hasText(subject) || !StringUtils.hasText(htmlBody)) {
            log.warn("Attempted to send email notification with missing subject or body. Skipping.");
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setTo(recipients.toArray(String[]::new));
            if (StringUtils.hasText(fromAddress)) {
                helper.setFrom(fromAddress);
            }
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML content

            mailSender.send(message);
            log.info("Sent HTML email notification '{}' to {}", subject, recipients);
            return true;
        } catch (Exception ex) {
            log.error("Failed to send email notification '{}': {}", subject, ex.getMessage(), ex);
            return false;
        }
    }

    private boolean sendNotification(List<String> overrideRecipients, String subject, String body) {
        List<String> recipients = resolveRecipients(overrideRecipients);
        if (recipients.isEmpty()) {
            log.debug("Skipping email notification '{}' because no recipients are configured.", subject);
            return false;
        }

        if (!StringUtils.hasText(subject) || !StringUtils.hasText(body)) {
            log.warn("Attempted to send email notification with missing subject or body. Skipping.");
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setTo(recipients.toArray(String[]::new));
            if (StringUtils.hasText(fromAddress)) {
                helper.setFrom(fromAddress);
            }
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(message);
            log.info("Sent email notification '{}' to {}", subject, recipients);
            return true;
        } catch (Exception ex) {
            log.error("Failed to send email notification '{}': {}", subject, ex.getMessage(), ex);
            return false;
        }
    }

    private List<String> resolveRecipients(List<String> overrideRecipients) {
        List<String> parsedOverride = parseRecipients(overrideRecipients);
        if (!parsedOverride.isEmpty()) {
            return parsedOverride;
        }
        return defaultRecipients;
    }

    private List<String> parseRecipients(List<String> recipients) {
        if (recipients == null) {
            return Collections.emptyList();
        }
        List<String> parsed = new ArrayList<>();
        recipients.stream()
            .filter(StringUtils::hasText)
            .map(String::trim)
            .map(String::toLowerCase)
            .forEach(address -> {
                if (!parsed.contains(address)) {
                    parsed.add(address);
                }
            });
        return parsed;
    }

    private List<String> parseRecipients(String csvRecipients) {
        if (!StringUtils.hasText(csvRecipients)) {
            return Collections.emptyList();
        }
        return Stream.of(csvRecipients.split(","))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .map(String::toLowerCase)
            .distinct()
            .toList();
    }

    // HTML Email Template Builders
    
    private String buildShipmentCreatedEmailHtml(Shipment shipment) {
        String shipmentId = String.format("SH%03d", shipment.getShipmentId());
        String origin = StringUtils.hasText(shipment.getOrigin()) ? shipment.getOrigin() : "Not specified";
        String destination = StringUtils.hasText(shipment.getDestination()) ? shipment.getDestination() : "Not specified";
        String status = StringUtils.hasText(shipment.getStatus()) ? shipment.getStatus() : "Pending";
        String estimatedDelivery = shipment.getEstimatedDelivery() != null 
            ? shipment.getEstimatedDelivery().format(DATE_FORMATTER) 
            : "Not scheduled";
        String routeName = (shipment.getAssignedRoute() != null 
            && StringUtils.hasText(shipment.getAssignedRoute().getOriginPort()) 
            && StringUtils.hasText(shipment.getAssignedRoute().getDestinationPort())) 
            ? shipment.getAssignedRoute().getOriginPort() + " → " + shipment.getAssignedRoute().getDestinationPort() 
            : "Not assigned";
        String vendorName = (shipment.getAssignedVendor() != null && StringUtils.hasText(shipment.getAssignedVendor().getName())) 
            ? shipment.getAssignedVendor().getName() 
            : "Not assigned";
        String shipmentCode = StringUtils.hasText(shipment.getShipmentCode()) 
            ? shipment.getShipmentCode() 
            : "Not generated";

        return String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Shipment Created</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5;">
                <table role="presentation" style="width: 100%%; border-collapse: collapse; background-color: #f5f5f5; padding: 20px 0;">
                    <tr>
                        <td align="center">
                            <table role="presentation" style="max-width: 600px; width: 100%%; border-collapse: collapse; background-color: #ffffff; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 40px 30px; text-align: center;">
                                        <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 600; letter-spacing: -0.5px;">New Shipment Created</h1>
                                        <p style="margin: 10px 0 0 0; color: #ffffff; font-size: 16px; opacity: 0.95;">%s</p>
                                    </td>
                                </tr>
                                
                                <!-- Content -->
                                <tr>
                                    <td style="padding: 40px 30px; background-color: #ffffff;">
                                        <p style="margin: 0 0 25px 0; color: #374151; font-size: 16px; line-height: 1.6;">
                                            A new shipment has been successfully created in the system. Please find the details below:
                                        </p>
                                        
                                        <!-- Info Box -->
                                        <table role="presentation" style="width: 100%%; border-collapse: collapse; background-color: #f9fafb; border: 1px solid #e5e7eb; border-radius: 8px; margin-bottom: 25px;">
                                            <tr>
                                                <td style="padding: 25px;">
                                                    <h2 style="margin: 0 0 20px 0; color: #667eea; font-size: 18px; font-weight: 600; border-bottom: 2px solid #667eea; padding-bottom: 10px;">Shipment Information</h2>
                                                    
                                                    <table role="presentation" style="width: 100%%; border-collapse: collapse;">
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Shipment ID:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Shipment Code:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Origin:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Destination:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Current Status:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="display: inline-block; padding: 4px 12px; background-color: #fef3c7; color: #92400e; border-radius: 12px; font-size: 13px; font-weight: 600;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Estimated Delivery:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Assigned Route:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Assigned Vendor:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <p style="margin: 0; color: #6b7280; font-size: 14px; line-height: 1.6;">
                                            This shipment is now active in the system and can be tracked through the cargo management dashboard.
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f3f4f6; padding: 30px; text-align: center; border-top: 1px solid #e5e7eb;">
                                        <p style="margin: 0 0 10px 0; color: #111827; font-size: 14px; font-weight: 600;">Cargo Management System</p>
                                        <p style="margin: 0 0 5px 0; color: #6b7280; font-size: 12px;">This is an automated notification. Please do not reply to this email.</p>
                                        <p style="margin: 0; color: #9ca3af; font-size: 11px;">&copy; 2025 CargoFlow. All rights reserved.</p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """, 
            shipmentId, shipmentId, shipmentCode, origin, destination, status, estimatedDelivery, routeName, vendorName
        );
    }

    private String buildShipmentUpdatedEmailHtml(Shipment shipment, String previousStatus) {
        String shipmentId = String.format("SH%03d", shipment.getShipmentId());
        String origin = StringUtils.hasText(shipment.getOrigin()) ? shipment.getOrigin() : "Not specified";
        String destination = StringUtils.hasText(shipment.getDestination()) ? shipment.getDestination() : "Not specified";
        String currentStatus = StringUtils.hasText(shipment.getStatus()) ? shipment.getStatus() : "Unknown";
        String oldStatus = StringUtils.hasText(previousStatus) ? previousStatus : "Unknown";
        String estimatedDelivery = shipment.getEstimatedDelivery() != null 
            ? shipment.getEstimatedDelivery().format(DATE_FORMATTER) 
            : "Not scheduled";
        String shipmentCode = StringUtils.hasText(shipment.getShipmentCode()) 
            ? shipment.getShipmentCode() 
            : "Not generated";

        return String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Shipment Updated</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5;">
                <table role="presentation" style="width: 100%%; border-collapse: collapse; background-color: #f5f5f5; padding: 20px 0;">
                    <tr>
                        <td align="center">
                            <table role="presentation" style="max-width: 600px; width: 100%%; border-collapse: collapse; background-color: #ffffff; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #f59e0b 0%%, #d97706 100%%); padding: 40px 30px; text-align: center;">
                                        <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 600; letter-spacing: -0.5px;">Shipment Status Updated</h1>
                                        <p style="margin: 10px 0 0 0; color: #ffffff; font-size: 16px; opacity: 0.95;">%s</p>
                                    </td>
                                </tr>
                                
                                <!-- Content -->
                                <tr>
                                    <td style="padding: 40px 30px; background-color: #ffffff;">
                                        <p style="margin: 0 0 25px 0; color: #374151; font-size: 16px; line-height: 1.6;">
                                            The shipment status has been updated. Please review the changes below:
                                        </p>
                                        
                                        <!-- Status Change Box -->
                                        <table role="presentation" style="width: 100%%; border-collapse: collapse; background-color: #dbeafe; border: 1px solid #93c5fd; border-radius: 8px; margin-bottom: 25px;">
                                            <tr>
                                                <td style="padding: 20px; text-align: center;">
                                                    <p style="margin: 0 0 12px 0; color: #1e40af; font-size: 14px; font-weight: 600;">Status Changed</p>
                                                    <div style="display: inline-block;">
                                                        <span style="display: inline-block; padding: 8px 16px; background-color: #fee2e2; color: #991b1b; border-radius: 12px; font-size: 14px; font-weight: 600; margin: 0 8px;">%s</span>
                                                        <span style="color: #6b7280; font-size: 18px; margin: 0 8px;">→</span>
                                                        <span style="display: inline-block; padding: 8px 16px; background-color: #d1fae5; color: #065f46; border-radius: 12px; font-size: 14px; font-weight: 600; margin: 0 8px;">%s</span>
                                                    </div>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <!-- Info Box -->
                                        <table role="presentation" style="width: 100%%; border-collapse: collapse; background-color: #f9fafb; border: 1px solid #e5e7eb; border-radius: 8px; margin-bottom: 25px;">
                                            <tr>
                                                <td style="padding: 25px;">
                                                    <h2 style="margin: 0 0 20px 0; color: #f59e0b; font-size: 18px; font-weight: 600; border-bottom: 2px solid #f59e0b; padding-bottom: 10px;">Current Shipment Details</h2>
                                                    
                                                    <table role="presentation" style="width: 100%%; border-collapse: collapse;">
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Shipment ID:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Shipment Code:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Origin:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Destination:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Current Status:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="display: inline-block; padding: 4px 12px; background-color: #d1fae5; color: #065f46; border-radius: 12px; font-size: 13px; font-weight: 600;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Estimated Delivery:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <p style="margin: 0; color: #6b7280; font-size: 14px; line-height: 1.6;">
                                            You can track this shipment's progress through the cargo management dashboard.
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f3f4f6; padding: 30px; text-align: center; border-top: 1px solid #e5e7eb;">
                                        <p style="margin: 0 0 10px 0; color: #111827; font-size: 14px; font-weight: 600;">Cargo Management System</p>
                                        <p style="margin: 0 0 5px 0; color: #6b7280; font-size: 12px;">This is an automated notification. Please do not reply to this email.</p>
                                        <p style="margin: 0; color: #9ca3af; font-size: 11px;">&copy; 2025 CargoFlow. All rights reserved.</p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """, 
            shipmentId, oldStatus, currentStatus, shipmentId, shipmentCode, origin, destination, currentStatus, estimatedDelivery
        );
    }

    private String buildShipmentDeletedEmailHtml(Shipment shipment) {
        String shipmentId = String.format("SH%03d", shipment.getShipmentId());
        String origin = StringUtils.hasText(shipment.getOrigin()) ? shipment.getOrigin() : "Not specified";
        String destination = StringUtils.hasText(shipment.getDestination()) ? shipment.getDestination() : "Not specified";
        String status = StringUtils.hasText(shipment.getStatus()) ? shipment.getStatus() : "Unknown";
        String shipmentCode = StringUtils.hasText(shipment.getShipmentCode()) 
            ? shipment.getShipmentCode() 
            : "Not generated";

        return String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Shipment Deleted</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5;">
                <table role="presentation" style="width: 100%%; border-collapse: collapse; background-color: #f5f5f5; padding: 20px 0;">
                    <tr>
                        <td align="center">
                            <table role="presentation" style="max-width: 600px; width: 100%%; border-collapse: collapse; background-color: #ffffff; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #dc2626 0%%, #991b1b 100%%); padding: 40px 30px; text-align: center;">
                                        <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 600; letter-spacing: -0.5px;">Shipment Deleted</h1>
                                        <p style="margin: 10px 0 0 0; color: #ffffff; font-size: 16px; opacity: 0.95;">%s has been removed</p>
                                    </td>
                                </tr>
                                
                                <!-- Content -->
                                <tr>
                                    <td style="padding: 40px 30px; background-color: #ffffff;">
                                        <!-- Warning Box -->
                                        <table role="presentation" style="width: 100%%; border-collapse: collapse; background-color: #fef2f2; border: 1px solid #fee2e2; border-radius: 8px; margin-bottom: 25px;">
                                            <tr>
                                                <td style="padding: 20px; text-align: center;">
                                                    <p style="margin: 0; color: #dc2626; font-size: 15px; font-weight: 600; line-height: 1.6;">
                                                        Warning: This shipment has been permanently deleted from the system
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <p style="margin: 0 0 25px 0; color: #374151; font-size: 16px; line-height: 1.6;">
                                            A shipment record has been removed from the cargo management system. Details of the deleted shipment are provided below for your records:
                                        </p>
                                        
                                        <!-- Info Box -->
                                        <table role="presentation" style="width: 100%%; border-collapse: collapse; background-color: #f9fafb; border: 1px solid #e5e7eb; border-radius: 8px; margin-bottom: 25px;">
                                            <tr>
                                                <td style="padding: 25px;">
                                                    <h2 style="margin: 0 0 20px 0; color: #dc2626; font-size: 18px; font-weight: 600; border-bottom: 2px solid #dc2626; padding-bottom: 10px;">Deleted Shipment Details</h2>
                                                    
                                                    <table role="presentation" style="width: 100%%; border-collapse: collapse;">
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Shipment ID:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Shipment Code:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Origin:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Destination:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right;">
                                                                <span style="color: #111827; font-size: 14px; font-weight: 500;">%s</span>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding: 12px 0;">
                                                                <span style="color: #6b7280; font-size: 14px; font-weight: 600;">Last Status:</span>
                                                            </td>
                                                            <td style="padding: 12px 0; text-align: right;">
                                                                <span style="display: inline-block; padding: 4px 12px; background-color: #fee2e2; color: #991b1b; border-radius: 12px; font-size: 13px; font-weight: 600;">%s</span>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <p style="margin: 0; color: #6b7280; font-size: 14px; line-height: 1.6;">
                                            If this deletion was made in error, please contact your system administrator immediately.
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f3f4f6; padding: 30px; text-align: center; border-top: 1px solid #e5e7eb;">
                                        <p style="margin: 0 0 10px 0; color: #111827; font-size: 14px; font-weight: 600;">Cargo Management System</p>
                                        <p style="margin: 0 0 5px 0; color: #6b7280; font-size: 12px;">This is an automated notification. Please do not reply to this email.</p>
                                        <p style="margin: 0; color: #9ca3af; font-size: 11px;">&copy; 2025 CargoFlow. All rights reserved.</p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """, 
            shipmentId, shipmentId, shipmentCode, origin, destination, status
        );
    }
}
