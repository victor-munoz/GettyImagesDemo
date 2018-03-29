package demo.victormunoz.gettyimagesdemo.di.module;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
    private final android.content.Context context;

    public ContextModule(android.content.Context context) {
        this.context = context;
    }

    @Provides
    android.content.Context provideContext() {
        return context;
    }
}
