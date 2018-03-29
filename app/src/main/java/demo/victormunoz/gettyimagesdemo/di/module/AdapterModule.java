package demo.victormunoz.gettyimagesdemo.di.module;

import android.app.Activity;
import android.content.Context;

import com.bumptech.glide.RequestManager;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.gettyimagesdemo.features.search.Adapter;


@Module(includes = {GlideModule.class})
public class AdapterModule {
    private final Adapter.AdapterListener mItemListener;

    public AdapterModule(Activity activity){
        this.mItemListener = (Adapter.AdapterListener) activity;
    }

    @Provides
    Adapter provideUsersAdapter(RequestManager picasso, Context context){
        return new Adapter(picasso, context, mItemListener);
    }
}