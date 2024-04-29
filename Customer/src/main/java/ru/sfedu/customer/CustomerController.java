package ru.sfedu.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sfedu.customer.dto.CustomerDTO;
import ru.sfedu.customer.dto.CustomerMapper;
import ru.sfedu.customer.dto.CustomersSearch;
import ru.sfedu.customer.dto.ProblemDTO;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/SimplePsy/V1/customer")
public class CustomerController {

    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.FOUND)
    public List<Customer> getResource(@RequestParam(name = "name", required = false) String name,
                                      @RequestParam(name = "contact", required = false) String contact) {

        return customerService.getCustomers(name, contact);
    }

    @PostMapping("/new-customer")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer createCustomer(@RequestParam("name") String name,
                                   @RequestParam("status") Status status,
                                   @RequestParam("contactPhone") String contactPhone,
                                   @RequestParam("contactEmail") String contactEmail,
                                   @RequestParam("contactTg") String contactTg,
                                   @RequestParam("dateOfFirstCall") String dateOfFirstCall,
                                   @RequestParam("avatar") MultipartFile avatar) throws IOException {
        Contact contact = new Contact(contactPhone, contactEmail, contactTg);
        Customer customer = new Customer(name, status, contact, dateOfFirstCall, avatar);
        return customerService.saveCustomer(customer);
    }

    @PostMapping("/new")
    public ResponseEntity<String> newCustomer(@RequestBody CustomerDTO customer)
    {
        System.out.println("Got the new customer:\n" +customer.getName());
        System.out.println(customer.getContact().getEmail());
        System.out.println(customer.getName());
        System.out.println(customer.getProblemsId());
        String newCustomerId = customerService.saveCustomer(customer).getId();
        System.out.println("CustomerController: " + newCustomerId);
        return ResponseEntity.ok(newCustomerId);
    }
    @PostMapping("/update")
    public ResponseEntity<String> updateCustomer(@RequestBody CustomerDTO customerDTO)
    {
        System.out.println("In method updateCustomer got the customerDTO with id: " + customerDTO.getId());
        Customer customerUpdate = CustomerMapper.INSTANCE.customerDTOToCustomer(customerDTO);
        customerService.updateCustomer(customerUpdate);
        return ResponseEntity.ok("Customer " + customerDTO.getName() + " successfully updated");
    }
    @DeleteMapping("/{id}")
    public boolean deleteResource(@PathVariable("id") String id) {
        customerService.deleteCustomer(id);
        return true;
    }

    @PutMapping("/update")
    public Customer updateResource(@RequestBody Customer customer) {
        return customerService.updateCustomer(customer);
    }

    @GetMapping("/getAllCustomers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers()
    {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/getCustomerById")
    public ResponseEntity<CustomerDTO> getCustomerById(@RequestParam("customerId") String customerId)
    {
        return ResponseEntity.ok(customerService.findById(customerId));
    }
    @GetMapping("/customerToClient")
    public ResponseEntity<CustomerDTO> CustomerToClient(@RequestParam("customerId") String customerId)
    {
        customerService.updateStatus(customerId);
        return ResponseEntity.ok(customerService.findById(customerId));
    }
    @DeleteMapping("/deleteCustomerById")
    public ResponseEntity<String> deleteCustomerById(@RequestParam("customerId") String customerId)
    {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.ok("Customer successfully deleted");
    }

    @GetMapping("/search2")
    public CustomersSearch searchCustomers(@RequestBody CustomersSearch customersSearch) {
        return customerService.searchCustomers(customersSearch.getSpecialistId(), customersSearch.getCustomers());
    }

    @GetMapping("/create2")
    public CustomersSearch createCustomers(@RequestBody CustomersSearch customersSearch) {
        return customerService.createCustomers(customersSearch.getSpecialistId(), customersSearch.getCustomers());
    }

    @GetMapping("/update-status")
    public void createCustomers(@RequestParam String customerId) {
        customerService.updateStatus(customerId);
    }

    @GetMapping("/findCustomerByContactData")
    public ResponseEntity<Boolean> findCustomerByContactData(@RequestParam("data") String data)
    {
        return ResponseEntity.ok(customerService.findByContactData(data));
    }
    @PostMapping("/problem/new")
    public ResponseEntity<String> customerNewProblem(@RequestParam("customerId") String customerId,
                                                     @RequestParam("problemId") String problemId)
    {
        System.out.println("In method customerNewProblem got the customerId " + customerId + " and problemId " + problemId);
        customerService.addProblem(customerId, problemId);
        return ResponseEntity.ok("Problem successfully added");
    }
    @PostMapping("/problems")
    public ResponseEntity<List<ProblemDTO>> customersProblems(@RequestParam("customerId") String customerId)
    {
        System.out.println("In method customersProblems got the customerId " + customerId);
        List<ProblemDTO> problems = customerService.getAllCustomersProblems(customerId);
        return ResponseEntity.ok(problems);
    }
}