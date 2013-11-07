package com.daou.book.controller;

import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public abstract class AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);

    private static final String FLASH_ERROR_MESSAGE = "errorMessage";
    private static final String FLASH_FEEDBACK_MESSAGE = "feedbackMessage";

    private static final String VIEW_REDIRECT_PREFIX = "redirect:";

    @Resource
    private MessageSource messageSource;

    protected void addErrorMessage(RedirectAttributes model, String code, Object... params) {
        LOGGER.debug("adding error message with code: " + code + " and params: " + params);
        Locale current = LocaleContextHolder.getLocale();
        LOGGER.debug("Current locale is " + current);
        String localizedErrorMessage = messageSource.getMessage(code, params, current);
        LOGGER.debug("Localized message is: " + localizedErrorMessage);
        model.addFlashAttribute(FLASH_ERROR_MESSAGE, localizedErrorMessage);
    }

    protected void addFeedbackMessage(RedirectAttributes model, String code, Object... params) {
        LOGGER.debug("Adding feedback message with code: " + code + " and params: " + params);
        Locale current = LocaleContextHolder.getLocale();
        LOGGER.debug("Current locale is " + current);
        String localizedFeedbackMessage = messageSource.getMessage(code, params, current);
        LOGGER.debug("Localized message is: " + localizedFeedbackMessage);
        model.addFlashAttribute(FLASH_FEEDBACK_MESSAGE, localizedFeedbackMessage);
    }

    protected String createRedirectViewPath(String path) {
        StringBuilder builder = new StringBuilder();
        builder.append(VIEW_REDIRECT_PREFIX);
        builder.append(path);
        return builder.toString();
    }

    protected void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
