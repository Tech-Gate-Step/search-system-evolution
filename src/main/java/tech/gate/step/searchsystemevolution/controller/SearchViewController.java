package tech.gate.step.searchsystemevolution.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
public class SearchViewController {

    @GetMapping("/search")
    public String searchPage(Model model) {

        model.addAttribute("pageTitle", "Search Products");

        return "search";
    }
}
