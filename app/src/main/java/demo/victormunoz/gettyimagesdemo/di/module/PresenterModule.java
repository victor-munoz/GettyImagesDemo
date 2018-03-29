package demo.victormunoz.gettyimagesdemo.di.module;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.gettyimagesdemo.R;
import demo.victormunoz.gettyimagesdemo.features.search.Contract;
import demo.victormunoz.gettyimagesdemo.features.search.Presenter;

@Module(includes = {RetrofitModule.class})
public class PresenterModule {
    private final Contract.Views mUsersView;

    @Provides
    @Singleton
    Contract.UserActionsListener providesPresenterInterface(RetrofitModule.GettyImagesAPI github, Context context){
        int pageSize = context.getResources().getInteger(R.integer.page_size);
        return new Presenter(mUsersView, github, pageSize);
    }

    public PresenterModule(@NonNull Activity activity){
        mUsersView = (Contract.Views) activity;
    }
}
