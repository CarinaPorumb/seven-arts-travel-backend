//package ro.sevenartstravel.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import ro.sevenartstravel.service.entity.UserService;
//
//@RequiredArgsConstructor
//@RequestMapping("/api/activation")
//@RestController
//public class ActivationController {
//
//    private final UserService userService;
//
//    @PostMapping("/{token}")
//    public ResponseEntity<String> activateUser(@PathVariable String token) {
//        userService.activateUser(token);
//        return ResponseEntity.ok("Account activated successfully.");
//    }
//}