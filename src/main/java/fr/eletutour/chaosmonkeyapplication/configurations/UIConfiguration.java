package fr.eletutour.chaosmonkeyapplication.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UIConfiguration {

    @Value("${app.ui:v1}")
    private String uiVersion;

    public String getUiVersion() {
        return uiVersion;
    }
}
