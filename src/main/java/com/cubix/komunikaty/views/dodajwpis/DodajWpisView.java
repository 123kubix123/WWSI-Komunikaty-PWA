package com.cubix.komunikaty.views.dodajwpis;

import com.cubix.komunikaty.backend.Post;
import com.cubix.komunikaty.backend.RssReader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.cubix.komunikaty.views.komunikatywwsi.KomunikatyWWSIView;

@Route(value = "dodaj", layout = KomunikatyWWSIView.class)
@PageTitle("Dodaj Wpis")
@CssImport("styles/views/dodajwpis/dodaj-wpis-view.css")
public class DodajWpisView extends Div  {

    private TextField title = new TextField();
    private TextField date = new TextField();
    private TextArea content = new TextArea();

    private Button save = new Button("Save");

    private Post post = new Post();

    public DodajWpisView() {
        setId("dodaj-wpis-view");
        VerticalLayout wrapper = createWrapper();

        createTitle(wrapper);
        createFormLayout(wrapper);
        createButtonLayout(wrapper);

        Binder<Post> binder = new Binder<>(Post.class);

        // Bind fields.
        binder.forField(title).asRequired().bind("title");
        binder.forField(date).asRequired().bind("date");
        binder.forField(content).asRequired().bind("content");

        binder.readBean(post);
        save.addClickListener(event -> {
            try {
                binder.writeBean(post);
                RssReader.getInstance().addPost(post);
                post = new Post();
                binder.readBean(post);
            } catch (ValidationException e){
                Notification.show("Podano niepoprawne dane!");
            }
        });
        add(wrapper);
    }


    private void createTitle(VerticalLayout wrapper) {
        H1 h1 = new H1("Dodawanie komunikatu");
        wrapper.add(h1);
    }

    private VerticalLayout createWrapper() {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setId("wrapper");
        wrapper.setSpacing(false);
        return wrapper;
    }

    private void createFormLayout(VerticalLayout wrapper) {
        FormLayout formLayout = new FormLayout();

        addFormItem(wrapper, formLayout, title, "Tytuł");
        addFormItem(wrapper, formLayout, date, "Data");
        FormLayout.FormItem descriptionFormItem = addFormItem(wrapper, formLayout,
                content, "Treść");
        formLayout.setColspan(descriptionFormItem, 2);
    }

    private void createButtonLayout(VerticalLayout wrapper) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout
                .setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        wrapper.add(buttonLayout);
    }

    private FormLayout.FormItem addFormItem(VerticalLayout wrapper,
            FormLayout formLayout, Component field, String fieldName) {
        FormLayout.FormItem formItem = formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
        return formItem;
    }

}
