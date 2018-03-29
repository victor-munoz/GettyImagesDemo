package demo.victormunoz.gettyimagesdemo.di.module;

import dagger.Module;
import dagger.Provides;

@SuppressWarnings("WeakerAccess")
@Module(includes = {ContextModule.class})
public class PicassoModule {
    @Provides
    com.squareup.picasso.Picasso providesPicasso(android.content.Context context) {
        return com.squareup.picasso.Picasso.with(context);
    }
}