package demo.victormunoz.gettyimagesdemo.di.component;

import javax.inject.Singleton;
import dagger.Component;
import demo.victormunoz.gettyimagesdemo.di.module.AdapterModule;
import demo.victormunoz.gettyimagesdemo.di.module.PresenterModule;
import demo.victormunoz.gettyimagesdemo.features.search.SearchActivity;

@Singleton
@Component(modules= {PresenterModule.class, AdapterModule.class})
public interface SearchComponent {
    void inject(SearchActivity usersActivity);
}
