package demo.victormunoz.gettyimagesdemo.injection.component;

import javax.inject.Singleton;
import dagger.Component;
import demo.victormunoz.gettyimagesdemo.injection.module.AdapterModule;
import demo.victormunoz.gettyimagesdemo.injection.module.PresenterModule;
import demo.victormunoz.gettyimagesdemo.ui.search.SearchActivity;


@Singleton
@Component(modules= {PresenterModule.class, AdapterModule.class})
public interface SearchComponent {
    void inject(SearchActivity usersActivity);
}
