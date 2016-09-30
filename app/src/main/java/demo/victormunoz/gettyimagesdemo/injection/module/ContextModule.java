package demo.victormunoz.gettyimagesdemo.injection.module;
import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
    private android.content.Context context;

    public ContextModule(android.content.Context context) {
        this.context = context;
    }

    @Provides
    android.content.Context provideContext() {
        return context;
    }
}
