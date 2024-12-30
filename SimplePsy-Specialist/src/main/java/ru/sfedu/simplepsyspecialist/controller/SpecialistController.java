package ru.sfedu.simplepsyspecialist.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sfedu.simplepsyspecialist.entity.Customer;
import ru.sfedu.simplepsyspecialist.entity.Specialist;
import ru.sfedu.simplepsyspecialist.service.SpecialistService;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
@RequestMapping("/SimplePsy/V1/specialist")
public class SpecialistController {

    @Autowired
    SpecialistService specialistService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<Specialist> getSpecialist(@PathVariable String id) {
        Specialist specialist = specialistService.findById(id);
        if (specialist != null) {
            return ResponseEntity.ok(specialist);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/signup")
    public String getSignUpForm(Model model) {
        model.addAttribute("specialist", new Specialist());
        return "signup";
    }

    @PostMapping("/signup")
    public String createNewSpecialist(@ModelAttribute("specialist") Specialist specialist) {
        System.out.println(specialist.getName());
        System.out.println(specialist.getSurname());
        System.out.println(specialist.getUsername());
        System.out.println(specialist.getPassword());
        specialistService.registerNewSpecialist(specialist);
        return "redirect:/SimplePsy/V1/session/calendar";
    }

    @GetMapping("/login")
    public String getLoginForm(Model model) {
        model.addAttribute("specialist", new Specialist());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("specialist") Specialist specialist) {
        System.out.println("Got the specialist's  username: " + specialist.getUsername());
        System.out.println("Got the specialist's  password: " + specialist.getPassword());
        specialistService.authorizeSpecialist(specialist);
        return "redirect:/SimplePsy/V1/session/calendar";
    }
    @GetMapping("/changePass")
    public String changePass(Model model)
    {
        return "change-pass";
    }
    @PostMapping("/changePass")
    public String changePass(@RequestParam("email") String email)
    {
        System.out.println("In method changePass got the email " + email);
        specialistService.changePassword(email);
        return "redirect:/SimplePsy/V1/specialist/login";
    }
    @GetMapping("/setNewPassword/{specialistId}")
    public String setNewPassword(@PathVariable String specialistId, Model model)
    {
        model.addAttribute("specialistId", specialistId);
        return "set-new-password";
    }
    @PostMapping("/setNewPassword")
    public String setNewPassword(@RequestParam("specialistId") String specialistId,
                                 @RequestParam("password") String password)
    {
        System.out.println("Spec id and pass: " + specialistId + " " + password);
        specialistService.setNewPassword(specialistId, password);
        return "redirect:/SimplePsy/V1/specialist/login";
    }
//@PostMapping("/login")
//public String login(@ModelAttribute("specialist") Specialist specialist)
//{
//    System.out.println(specialist.getEmail());
//    System.out.println(specialist.getPassword());
//    specialistService.authorizeSpecialist(specialist);
//    return "redirect:/SimplePsy/V1/specialist/calendar";
//}


    /*@PostMapping("/search")
    public void handleGetRequest(
            @RequestParam("specialist_id") String specialist_id,
            @RequestParam("start_date") String start_date,
            @RequestParam("end_date") String end_date) {
        specialistService.sendRequestToSession(specialist_id, start_date, end_date);
    }*/

    @GetMapping("/customers")
    public String getCustomersList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Specialist specialist = specialistService.findByUsername(userDetails.getUsername());
        List<Customer> customers = specialistService.getAllCustomers();
        List<Customer> specialistCustomers = new ArrayList<>();
        model.addAttribute("specialist", specialist);

        if (specialist.getCustomerIds() == null) {
            System.out.println("No customers were found for this specialist!");
            model.addAttribute("customers", specialistCustomers);
            return "new-front/customer/customer-list";
        }

        for (int i = 0; i < specialist.getCustomerIds().size(); i++) {
            for (Customer customer : customers) {
                if (Objects.equals(customer.getId(), specialist.getCustomerIds().get(i))) {
                    specialistCustomers.add(customer);
                    System.out.println(customers.get(i).getName());
                    break;
                }
            }
        }
        model.addAttribute("customers", specialistCustomers);
        return "new-front/customer/customer-list";
    }
    @GetMapping("/clients")
    public String getClientsList(@AuthenticationPrincipal UserDetails userDetails, Model model)
    {
        Specialist specialist = specialistService.findByUsername(userDetails.getUsername());
        List<Customer> customers = specialistService.getAllCustomersWithStatusCustomer();
        List<Customer> clients = new ArrayList<>();

        if (specialist.getCustomerIds() == null) {
            System.out.println("No customers were found for this specialist!");
            model.addAttribute("customers", customers);
            return "customer-list";
        }

        for (int i = 0; i < specialist.getCustomerIds().size(); i++) {
            for (Customer customer : customers) {
                if (Objects.equals(customer.getId(), specialist.getCustomerIds().get(i))) {
                    clients.add(customer);
                    System.out.println(customer.getName());
                    break;
                }
            }
        }
        model.addAttribute("clients", clients);
        return "client-list";
    }

    @PostMapping("/customer-card/update")
    public String updateCustomerCard(@ModelAttribute("customer") Customer customerDTO,
                                     @RequestParam("customerId") String customerId) {
        System.out.println("In method updateCustomerCard got the customer with name and surname: " + customerDTO.getName() + " " + customerDTO.getSurname());
        System.out.println("Customer's id: " + customerId);
        customerDTO.setId(customerId);
        specialistService.updateCustomer(customerDTO);
        return "redirect:/SimplePsy/V1/specialist/customer-card/" + customerDTO.getId();
    }

    @PostMapping("/delete-customer/{id}")
    public String deleteResource(@PathVariable("id") String customerId,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        String specialistId = specialistService.findByUsername(userDetails.getUsername()).getId();
        specialistService.deleteCustomerById(customerId, specialistId);
        return "redirect:/SimplePsy/V1/specialist/customers";
    }

    @PostMapping("/customers/new")
    public String createNewCustomer(@RequestBody Customer customer,
                                    @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        //System.out.println("Got the new customer:\n" + name);
        System.out.println(customer.getSurname());
        System.out.println(customer.getDateOfBirth());
        System.out.println(customer.getSex());
        System.out.println(customer.getContact().getPhone());
//        System.out.println(email);
//        System.out.println(tg);
//        System.out.println(problem);
//        Contact contact = new Contact(phone, email, tg);
//        Sex sex = gender.equals("MALE") ? Sex.MALE : Sex.FEMALE;
//        String customerId = specialistService.saveCustomer(new Customer(name, surname, dateOfBirth, sex , contact), problem);
//        Specialist specialist = specialistService.findByUsername(userDetails.getUsername());
//        specialist.addCustomerId(customerId);
//        specialistService.save(specialist);
        return "redirect:/SimplePsy/V1/specialist/customers";
    }

    @GetMapping("/find-customer/byProblemId")
    public ResponseEntity<String> sendEmailToSpecialistByProblemId(@RequestParam("problemId") String problemId) {
        System.out.println("In method sendEmailToSpecialistByProblemId \nGot problemId: " + problemId);
        String customerId = specialistService.findCustomerByProblemId(problemId).getId();
        Specialist specialist = specialistService.findSpecialist(customerId);
        System.out.println("Found the specialist " + specialist.getName() + " who contains provided customerId");
        String customerName = specialistService.findCustomerById(customerId).getName();
        System.out.println("Found the customer : " + customerName);
        System.out.println("Specialist's email: " + specialist.getUsername());
        System.out.println("sending email to the specialist about scoring completion");
        specialistService.sendEmailtoSpecialist(specialist.getUsername(), specialist.getName(), customerName, problemId);
        return ResponseEntity.ok("Success");
    }


    @PostMapping("customer/problem/new")
    public String customerNewProblem(@RequestParam("customerId") String customerId,
                                     @RequestParam("problem") String problem)
    {
        System.out.println("In Post mappping method customerNewProblem \ngot customerId: " + customerId + " and problem: " + problem);
        specialistService.addCustomerProblem(customerId, problem);
        return "redirect:/SimplePsy/V1/specialist/customer/problems/" + customerId;
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/SimplePsy/V1/specialist/login";
    }
    @PostMapping("/scoring/approve/{problemId}")
    public String approveScoring(@PathVariable("problemId") String problemId)
    {
        System.out.println("In method approveScoring got the problemId: " + problemId);
        specialistService.approveScoring(problemId);
        return "redirect:/SimplePsy/V1/specialist/clients";
    }

    @PostMapping("/scoring/cancel/{problemId}")
    public String cancelScoring(@PathVariable("problemId") String problemId)
    {
        System.out.println("In method cancelScoring got the problemId: " + problemId);
        specialistService.cancelScoring(problemId);
        String customerId = specialistService.findCustomerByProblemId(problemId).getId();
        return "redirect:/SimplePsy/V1/specialist/customer/problems/" + customerId;
    }

    @GetMapping("/personal-info")
    public String getPersonalInfo(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Specialist specialist = specialistService.findByUsername(userDetails.getUsername());

        // Debug: Выводим в консоль информацию о специалисте
        if (specialist.getAvatar() != null) {
            System.out.println("Аватарка загружена, размер: " + specialist.getAvatar().length + " байт");
        } else {
            System.out.println("Аватарка не найдена");
        }

        model.addAttribute("specialist", specialist);
        return "new-front/specialist/specialist-home-page";
    }


    @PostMapping("/personal-info/update")
    public String updatePersonalInfo(@AuthenticationPrincipal UserDetails userDetails,
                                     @ModelAttribute("specialist") Specialist specialist,
                                     @RequestParam(value = "diploma", required = false)  List<MultipartFile> multipartFiles) throws IOException {
        String specialistId = specialistService.findByUsername(userDetails.getUsername()).getId();
        specialist.setId(specialistId);
        System.out.println(multipartFiles.size());
        specialistService.updateSpecialist(specialist, multipartFiles);
        return "redirect:/SimplePsy/V1/specialist/personal-info";
    }

    @PostMapping("/personal-info/update-hypotheses")
    public String updateHypotheses(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam("hypotheses") String hypotheses) {
        Specialist specialist = specialistService.findByUsername(userDetails.getUsername());
        specialist.setHypotheses(hypotheses);
        specialistService.save(specialist);
        return "redirect:/SimplePsy/V1/session/sessions";
    }

    @PostMapping(value = "/personal-info/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateAvatar(@AuthenticationPrincipal UserDetails userDetails,
                                                            @RequestParam("avatar") MultipartFile avatarFile) {
        Map<String, Object> response = new HashMap<>();
        Specialist specialist = specialistService.findByUsername(userDetails.getUsername());

        try {
            byte[] avatarBytes = avatarFile.getBytes();
            specialist.setAvatar(avatarBytes);
            specialistService.save(specialist);

            // Предположим, что вы генерируете новый URL для аватарки, который будет возвращен на фронтенд
            String avatarUrl = "/path/to/avatar/" + specialist.getId() + "/avatar.jpg";

            response.put("success", true);
            response.put("avatarUrl", avatarUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/avatar/{id}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable String id) throws IOException {
        Specialist specialist = specialistService.findById(id);
        byte[] avatar = specialist.getAvatar();

        if (avatar == null) {
            InputStream is = new ClassPathResource("static/images/user-logo.jpg").getInputStream();
            avatar = is.readAllBytes();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(avatar, headers, HttpStatus.OK);
    }
    @GetMapping("/diploma/{specialistId}/{diplomaIndex}")
    public ResponseEntity<byte[]> getDiplomaImage(@PathVariable String specialistId, @PathVariable int diplomaIndex) {
        System.out.println("Specialist id and img index: " + specialistId + " " + diplomaIndex);
        Specialist specialist = specialistService.findById(specialistId);
        System.out.println("Specialist id in method getDiplomaImage: " + specialist);
        System.out.println("name: " + specialist.getName());
        byte[] image = specialist.getDiplomas().get(diplomaIndex);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
    @DeleteMapping("/diploma/{specialistId}/{diplomaIndex}")
    public ResponseEntity<Void> deleteDiploma(@PathVariable String specialistId, @PathVariable int diplomaIndex) {
        Specialist specialist = specialistService.findById(specialistId);
        System.out.println("Specialist id and img index: " + specialistId + " " + diplomaIndex);
        System.out.println("Specialist id in method getDiplomaImage: " + specialist);
        if (specialist != null && diplomaIndex >= 0 && diplomaIndex < specialist.getDiplomas().size()) {
            // Удаление диплома по индексу
            specialist.getDiplomas().remove(diplomaIndex);
            specialistService.save(specialist); // Сохранение изменений

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}