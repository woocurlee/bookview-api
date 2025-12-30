package com.woocurlee.bookview.controller

import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.webmvc.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class CustomErrorController : ErrorController {
    @RequestMapping("/error")
    fun handleError(
        request: HttpServletRequest,
        model: Model,
    ): String {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)

        if (status != null) {
            model.addAttribute("status", status.toString().toIntOrNull())
        }

        return "error"
    }
}
