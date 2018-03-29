package demo.victormunoz.gettyimagesdemo.di.module;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import dagger.Module;
import dagger.Provides;

@SuppressWarnings("WeakerAccess")
@Module(includes = {ContextModule.class})
public class GlideModule {
    @Provides
    RequestManager providesPicasso(Context context){
        return Glide.with(context);
    }
}