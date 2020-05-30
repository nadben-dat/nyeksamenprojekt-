package com.Nadia.demo.controllers;

import com.Nadia.demo.domains.Blog;
import com.Nadia.demo.domains.MyUser;
import com.Nadia.demo.services.BlogService;
import com.Nadia.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class HomeController {

    private static Logger logger = Logger.getLogger(HomeController.class.getName());

    private UserService userService;
    private BlogService blogService;

    private MyUser loggedInUser;

    public HomeController(UserService userService, BlogService blogService) {
        this.userService = userService;
        this.blogService = blogService;
    }

//    @RequestMapping(value = {"","/","index"}, method = RequestMethod.GET)
//    public String index(Model model){
//        logger.info("index method called...");
//
//        model.addAttribute("user", new MyUser());
//        return "login";
//    }

    @RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout,
            /*@ModelAttribute MyUser user,*/ Model model) {
        //logger.info("login method called with: " + user);
        logger.info("login method called with: ");

        ModelAndView modelAndView = new ModelAndView();

        if (error != null) {
            model.addAttribute("error", "Invalid username and password!");
        }

        if (logout != null) {
            model.addAttribute("msg", "You've been logged out successfully.");
        }

        modelAndView.setViewName("login");
        return modelAndView;


//        user = userService.getUser(user.getUsername(), user.getPassword());
//        if(user != null){
//            List<Blog> allBlogs = blogService.getAllBlogs();
//            model.addAttribute("listOfBlogs", allBlogs);
//
//            if(Arrays.asList(user.getRoles()).contains("admin")){
//                model.addAttribute("admin", true);
//            }
//
//            loggedInUser = user;
//            return "home";
//        }
//        model.addAttribute("error", true);
//        model.addAttribute("logout", true);


    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(Model model, HttpServletRequest request) {

        if (request.isUserInRole("ROLE_ADMIN")) {
            model.addAttribute("showSection", true);
        }

        return "home";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout() {
        return "logout";
    }

    @RequestMapping(value = "/sendData", method = RequestMethod.POST)
    public String sendData(@RequestParam String blog, Model model) {
        //logger.info("login sendData called...");

        blogService.addBlog(blog);

        List<Blog> allBlogs = blogService.getAllBlogs();
        model.addAttribute("listOfBlogs", allBlogs);

//        if(Arrays.asList(loggedInUser.getRoles()).contains("admin")){
//            model.addAttribute("admin", true);
//        }

        return "home";
    }

    @RestController
    @RequestMapping("/api")
    public static class TestRestController {

        private static final String RESPONSE_OK = "ok";
        private static Logger logger = Logger.getLogger(TestRestController.class.getName());
        @Autowired
        TestRestService testRestService;

        @RequestMapping("/job")
        public String jobTrigger(
                @RequestParam(value = "jobInstanceId") String jobInstanceId,
                @RequestParam(value = "successUrl") String successUrl,
                @RequestParam(value = "failUrl") String failUrl) throws InterruptedException {

            logger.info("jobInstanceId: " + jobInstanceId + ". successUrl: " + successUrl + ". failUrl: " + failUrl);
            testRestService.asyncJobTrigger(jobInstanceId, successUrl, failUrl);

            logger.info("jobTrigger ran successfully...");
            return RESPONSE_OK;
        }

        @RequestMapping("/jobs")
        public @ResponseBody
        List<TestRestModel> testTrigger(
                @RequestParam(value = "jobInstanceId") String jobInstanceId,
                @RequestParam(value = "successUrl") String successUrl,
                @RequestParam(value = "failUrl") String failUrl) throws InterruptedException {

            List<TestRestModel> testRestModels = testRestService.asyncJobTrigger(jobInstanceId, successUrl, failUrl);
            return testRestModels;
        }

        @RequestMapping(value = "/case-create/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
        @ResponseStatus(HttpStatus.CREATED)
        public NewCaseArgs postData(@PathVariable(value = "id") long id, @RequestBody NewCaseArgs newCaseArgs) {
            logger.info("NewCaseArgs: " + newCaseArgs);
            return newCaseArgs;
        }

        @RequestMapping(value = "/post", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
        public String post(@RequestBody Customer cust) {
            System.out.println("/POST request, cust: " + cust.toString());
            return "/Post Successful!";
        }

        @PostMapping(value = "/testdata")
        public ResponseEntity<PlanCase> sendData(@RequestBody String newCaseArgs) {
            PlanCase planCase = new PlanCase();
            planCase.setExternalId("123");
            planCase.setCprNumber("345");
            return new ResponseEntity<>(planCase, HttpStatus.CREATED);
        }

    }
}
