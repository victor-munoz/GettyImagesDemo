package demo.victormunoz.gettyimagesdemo.di.module;

import android.app.Activity;
import android.content.Context;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import demo.victormunoz.gettyimagesdemo.features.search.Adapter;


@Module(includes = {PicassoModule.class})
public class AdapterModule {
    private final Adapter.AdapterListener mItemListener;

    public AdapterModule(Activity activity) {
        this.mItemListener = (Adapter.AdapterListener) activity;
    }

    @Provides
    Adapter provideUsersAdapter(Picasso picasso, Context context) {
        return new Adapter(picasso,context,mItemListener);
    }
}