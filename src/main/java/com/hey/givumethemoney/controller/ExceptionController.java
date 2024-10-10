package com.hey.givumethemoney.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;


@Controller
public class ExceptionController implements ErrorController {

    @RequestMapping("/error")
    public String ErrorHandler (HttpServletRequest request, HttpServletResponse response, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        int statusCode = Integer.parseInt(status.toString());
        response.setStatus(statusCode);

        model.addAttribute("code", status.toString());
        model.addAttribute("message", errorMessage.toString());

        return "error/error";
    }
}
