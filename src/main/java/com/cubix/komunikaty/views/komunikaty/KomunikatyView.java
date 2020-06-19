package com.cubix.komunikaty.views.komunikaty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cubix.komunikaty.backend.KomunikatListener;
import com.cubix.komunikaty.backend.Post;
import com.cubix.komunikaty.backend.RssReader;
import com.google.common.eventbus.DeadEvent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;import com.cubix.komunikaty.views.komunikatywwsi.KomunikatyWWSIView;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.VaadinRequest;
import javafx.geometry.Pos;


@Route(value = "komunikaty", layout = KomunikatyWWSIView.class)

@RouteAlias(value = "", layout = KomunikatyWWSIView.class)@PageTitle("Komunikaty")
@CssImport(value = "styles/views/komunikaty/komunikaty-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class KomunikatyView extends Div implements AfterNavigationObserver, KomunikatListener {

    Grid<Post> grid = new Grid<>();

    public KomunikatyView() {

        setId("komunikaty-view");
        addClassName("komunikaty-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(post -> createCard(post));

        add(grid);
    }




    private HorizontalLayout createCard(Post post) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        Icon image = new Icon(VaadinIcon.INFO_CIRCLE_O);

        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        Span title = new Span(post.getTitle());

        title.addClassName("name");
        Span date = new Span(post.getDate());
        date.addClassName("date");
        header.add(title, date);

        Span content = new Span();
        content.setText(post.getContent());
        content.addClassName("post");

        description.add(header, content);
        card.add(image, description);
        return card;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Set some data when this view is displayed.
        ArrayList<Post> posts = RssReader.getPosts();
        grid.setItems(posts);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        RssReader.getInstance().register(this);
        super.onAttach(attachEvent);
    }


    @Override
    public void onDetach(DetachEvent deatachEvent) {
        RssReader.getInstance().unregister(this);
        super.onDetach(deatachEvent);
    }

    @Override
    public void Komunikat(Post p) {
        getUI().ifPresent(ui-> ui.access(new Command() {
            @Override
            public void execute() {
                ArrayList<Post> posts = RssReader.getPosts();
                grid.setItems(posts);
            }
        }));
    }
}
