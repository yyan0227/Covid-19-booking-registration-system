package project.controller;

import project.Engine.Database;
import project.springbootadapters.JsonWebToken;
import project.springbootadapters.UserEntity;
import project.user.User;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
/**
 * This class represents the controller for the Login side
 */
public class LoginController {

    /**
     * Reference: https://stackoverflow.com/questions/19556039/how-to-get-access-to-http-header-information-in-spring-mvc-rest-controller
     *
     * This method handles the login feature of the user.
     *
     * @param model Represents the interface that defines a holder for model attributes.
     * @param requestHeader A data structure representing HTTP request or response headers
     *
     * @return Returns the user to the login page
     */
    @GetMapping("/login")
    public String login(Model model, @RequestHeader HttpHeaders requestHeader) {
        if (!requestHeader.containsKey("Authorization")) {
            model.addAttribute("loggedIn", false);
            model.addAttribute("userEntity", new UserEntity());
        }
        return "login";
    }

    /**
     * This method checks to see if the entered details is an admin/facility worker or a resident
     *
     * @param userEntity Entered details by the user
     * @param model Represents the interface that defines a holder for model attributes.
     * @param redirectAttributes
     *
     * @return Returns the page of the type of user depending on the entered details
     */
    @PostMapping(value = "/login")
    public RedirectView validateCredentials(
            @ModelAttribute UserEntity userEntity, Model model, RedirectAttributes redirectAttributes
    ) {
        model.addAttribute("userEntity", new UserEntity());

        // obtaining entered details
        String userName = userEntity.getUserName();
        String password = userEntity.getPassword();

        // verifying entered details
        JsonWebToken jwt = Database.verifyUserLoginCredentials(userName, password);
        if (jwt == null) {
            return new RedirectView("/login");
        }

        // checking to see if the username exists
        User user = Database.getSpecificUserByUserName(userName);
        redirectAttributes.addFlashAttribute("jwt", jwt);
        if (user != null && user.isAdmin()) {
            // https://stackoverflow.com/questions/19266427/what-are-ways-for-pass-parameters-from-controller-after-redirect-in-spring-mvc/19269653#19269653
            // https://stackoverflow.com/questions/24668988/how-do-i-addobject-in-spring-redirectview
            // https://stackoverflow.com/questions/24301114/passing-model-attribute-during-redirect-in-spring-mvc-and-avoiding-the-same-in-u
            return new RedirectView("/admin");
        }
        else {
            redirectAttributes.addFlashAttribute("user", user);
            return new RedirectView("/user");
        }
    }
}
