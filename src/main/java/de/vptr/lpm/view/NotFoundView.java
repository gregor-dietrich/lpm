package de.vptr.lpm.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Error view for not found (404 Not Found).
 */
@PageTitle("Not Found | LPM")
public class NotFoundView extends VerticalLayout implements HasErrorParameter<NotFoundException> {

    /**
     * Initializes the not found error view.
     */
    public NotFoundView() {
        this.setPadding(true);
        this.setSpacing(true);
        this.setAlignItems(FlexComponent.Alignment.CENTER);
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        final var heading = new H1("404 - Page Not Found");
        final var message = new Paragraph("The page you are looking for does not exist.");

        this.add(heading, message);
    }

    @Override
    public int setErrorParameter(final BeforeEnterEvent event, final ErrorParameter<NotFoundException> parameter) {
        return HttpServletResponse.SC_NOT_FOUND;
    }
}