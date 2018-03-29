package demo.victormunoz.gettyimagesdemo.di.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
    private final android.content.Context context;

    public ContextModule(android.content.Context context){
        this.context = context;
    }

    @Provides
    Context provideContext(){
        return context;
    }
}
