package de.vptr.lpm.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.PageTitle;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Error view for access denied (403 Forbidden).
 */
@PageTitle("Access Denied | LPM")
public class AccessDeniedView extends VerticalLayout implements HasErrorParameter<Exception> {

    /**
     * Initializes the access denied error view.
     */
    public AccessDeniedView() {
        this.setPadding(true);
        this.setSpacing(true);
        this.setAlignItems(FlexComponent.Alignment.CENTER);
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        final var heading = new H1("403 - Access Denied");
        final var message = new Paragraph("You do not have permission to access this page.");

        this.add(heading, message);
    }

    @Override
    public int setErrorParameter(final BeforeEnterEvent event, final ErrorParameter<Exception> parameter) {
        return HttpServletResponse.SC_FORBIDDEN;
    }
}
