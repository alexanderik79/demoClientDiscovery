package com.example.democlientdiscovery;


import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@SpringBootApplication
@RestController
public class DemoClientDiscoveryApplication {

    private final WebClient webClient;

    public DemoClientDiscoveryApplication(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8761").build();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(DemoClientDiscoveryApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

//    @GetMapping("/services/{name}")
//    @ResponseStatus(HttpStatus.OK)
//    public String get(@PathVariable String name) {
//        return "Service name is " + name;
//    }

    @GetMapping("/hello/{message}")
    public String hello(@PathVariable String message) {
        WebClient.create("http://localhost:8761")
                .post()
                .uri("/send-message")
                .body(BodyInserters.fromValue(message))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        System.out.println("\u001B[34m" + "Client: Message was sent - message = " + message + "\u001B[0m");
        return "Client: Hello from! Message sent: " + message;
    }

    @PostMapping("/send-client")
    public String sendClient(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String email
    ) {
        String response = webClient.post()
                .uri("/receive-client")
                .body(BodyInserters.fromFormData("name", name)
                        .with("phone", phone)
                        .with("email", email))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("Server response: " + response);
        return "Client: Client sent successfully!";
    }


    @GetMapping("/get-client/{id}")
    public String getClientByName(@PathVariable Long id) {
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/get-client-by-id").queryParam("id", id).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("Server response: " + response);
        return "Client: Data for client with id '" + id + "': " + response;
    }

    @PutMapping("/update-phone/{id}")
    public String updatePhoneById(@PathVariable Long id, @RequestParam String newPhone) {
        String response = webClient.put()
                .uri(uriBuilder -> uriBuilder.path("/update-phone").queryParam("id", id).queryParam("newPhone", newPhone).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("Server response: " + response);
        return "Client: Phone updated for client with id '" + id + "'. New phone: " + newPhone;
    }

    @DeleteMapping("/delete-client/{id}")
    public String deleteClientById(@PathVariable Long id) {
        String response = webClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/delete-client").queryParam("id", id).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (response != null && response.contains("successfully")) {
            return "Client: Client with id '" + id + "' deleted successfully.";
        } else {
            return "Client: Failed to delete client with id '" + id + "'. Server response: " + response;
        }
    }
}



// теж саме тільки на RestTemplate

//
//import org.springframework.boot.WebApplicationType;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//@SpringBootApplication
//@RestController
//
//public class DemoClientDiscoveryApplication {
//    private final RestTemplate restTemplate;
//    public DemoClientDiscoveryApplication(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    public static void main(String[] args) {
//        new SpringApplicationBuilder(DemoClientDiscoveryApplication.class)
//                .web(WebApplicationType.SERVLET).run(args);
//    }
////
////    @GetMapping("/services/{name}")
////    @ResponseStatus(HttpStatus.OK)
////    public String get(@PathVariable String name) {
////        return "Service name is " + name;
////    }
//
//    @GetMapping("/hello/{message}")
//    public String hello(@PathVariable String message) {
//        restTemplate.postForObject("http://localhost:8761/send-message", message, Void.class);
//        System.out.println("\u001B[34m"+"Client: Message was sent - message = "+message+"\u001B[0m");
//        return "Client: Hello from! Message sent: " + message;
//    }
//
//    @PostMapping("/send-client")
//    public String sendClient(
//            @RequestParam String name,
//            @RequestParam String phone,
//            @RequestParam String email
//    ) {
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("name", name);
//        params.add("phone", phone);
//        params.add("email", email);
//        String response = restTemplate.postForObject("http://localhost:8761/receive-client", params, String.class);
//        System.out.println("Server response: " + response);
//        return "Client: Client sent successfully!";
//    }
//
//    @GetMapping("/get-client/{id}")
//    public String getClientByName(@PathVariable Long id) {
//        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8761/get-client-by-id")
//                .queryParam("id", id);
//        String response = restTemplate.getForObject(builder.toUriString(), String.class);
//        System.out.println("Server response: " + response);
//        return "Client: Data for client with id '" + id + "': " + response;
//    }
//
//    @PutMapping("/update-phone/{id}")
//    public String updatePhoneById(@PathVariable Long id, @RequestParam String newPhone) {
//        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8761/update-phone")
//                .queryParam("id", id)
//                .queryParam("newPhone", newPhone);
//        String response = restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, null, String.class).getBody();
//        System.out.println("Server response: " + response);
//        return "Client: Phone updated for client with id '" + id + "'. New phone: " + newPhone;
//    }
//
//    @DeleteMapping("/delete-client/{id}")
//    public String deleteClientById(@PathVariable Long id) {
//        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8761/delete-client")
//                .queryParam("id", id);
//        ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, null, String.class);
//
//        if (responseEntity.getStatusCode().is2xxSuccessful()) {
//            return "Client: Client with id '" + id + "' deleted successfully.";
//        } else {
//            return "Client: Failed to delete client with id '" + id + "'. Server response: " + responseEntity.getBody();
//        }
//    }
//
//}


