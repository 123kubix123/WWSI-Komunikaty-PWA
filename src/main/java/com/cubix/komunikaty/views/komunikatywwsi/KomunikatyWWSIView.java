package com.cubix.komunikaty.views.komunikatywwsi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.cubix.komunikaty.backend.KomunikatListener;
import com.cubix.komunikaty.backend.Post;
import com.cubix.komunikaty.backend.RssReader;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.theme.Theme;import com.vaadin.flow.theme.lumo.Lumo;

import com.cubix.komunikaty.views.komunikatywwsi.KomunikatyWWSIView;
import com.cubix.komunikaty.views.komunikaty.KomunikatyView;
import com.cubix.komunikaty.views.dodajwpis.DodajWpisView;

/**
 * The main view is a top-level placeholder for other views.
 */
@Push
@JsModule("./styles/shared-styles.js")
@CssImport(value = "styles/views/komunikatywwsi/komunikaty-wwsi-view.css", themeFor = "vaadin-app-layout")
@PWA(name = "Komunikaty WWSI", shortName = "Komunikaty WWSI")
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
public class KomunikatyWWSIView extends AppLayout implements KomunikatListener {

    private final Tabs menu;

    public KomunikatyWWSIView() {
        menu = createMenuTabs();
        addToNavbar(menu);
    }

    private static Tabs createMenuTabs() {
        final Tabs tabs = new Tabs();
        tabs.getStyle().set("max-width", "100%");
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        final List<Tab> tabs = new ArrayList<>();
        tabs.add(createTab("Komunikaty", KomunikatyView.class));
        tabs.add(createTab("Dodaj Wpis", DodajWpisView.class));
        return tabs.toArray(new Tab[tabs.size()]);
    }

    private static Tab createTab(String title, Class<? extends Component> viewClass) {
        return createTab(populateLink(new RouterLink(null, viewClass), title));
    }

    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        tab.add(content);
        return tab;
    }

    private static <T extends HasComponents> T populateLink(T a, String title) {
        a.add(title);
        return a;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        selectTab();
    }

    private void selectTab() {
        String target = RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
        Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
            Component child = tab.getChildren().findFirst().get();
            return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
        }).findFirst();
        tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
    }

    @Override
    public void Komunikat(final Post p){
        getUI().ifPresent(ui-> ui.access(new Command() {
            @Override
            public void execute() {
                Notification.show(p.getTitle()+"\n"+p.getContent());
            }
        }));
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
}
