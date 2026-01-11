//package gr3.workhub.controller;
//
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//
//@RestController
//@RequestMapping("/workhub/api/v1/webhooks")
//@RequiredArgsConstructor
//public class WebhookController {
//
//    @PostMapping("/handle")
//    public ResponseEntity<?> handleWebhook(HttpServletRequest request) throws IOException {
//        String payload = getRequestBody(request);
//
//        // Process the payload (e.g., parse JSON, validate signature, etc.)
//        System.out.println("Received webhook payload: " + payload);
//
//        // Return a response to acknowledge the webhook
//        return ResponseEntity.ok("Webhook received");
//    }
//
//    private String getRequestBody(HttpServletRequest request) throws IOException {
//        StringBuilder payload = new StringBuilder();
//        try (BufferedReader reader = request.getReader()) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                payload.append(line);
//            }
//        }
//        return payload.toString();
//    }
//}