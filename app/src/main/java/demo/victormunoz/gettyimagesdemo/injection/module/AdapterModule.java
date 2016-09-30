package demo.victormunoz.gettyimagesdemo.injection.module;

import android.app.Activity;
import android.content.Context;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import demo.victormunoz.gettyimagesdemo.ui.search.Adapter;


@Module(includes = {PicassoModule.class})
public class AdapterModule {
    private Adapter.AdapterListener mItemListener;

    public AdapterModule(Activity activity) {
        this.mItemListener = (Adapter.AdapterListener) activity;
    }

    @Provides
    Adapter provideUsersAdapter(Picasso picasso, Context context) {
        return new Adapter(picasso,context,mItemListener);
    }
}