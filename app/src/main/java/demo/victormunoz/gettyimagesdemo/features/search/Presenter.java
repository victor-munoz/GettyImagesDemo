package demo.victormunoz.gettyimagesdemo.features.search;

import java.util.List;

import demo.victormunoz.gettyimagesdemo.di.module.RetrofitModule.GettyImagesAPI;
import demo.victormunoz.gettyimagesdemo.model.GettyImage;
import demo.victormunoz.gettyimagesdemo.utils.espresso.EspressoIdlingResource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Presenter implements Contract.UserActionsListener {
    private final Contract.Views activityListener;
    private final GettyImagesAPI gettyImagesAPI;
    private final int pageSize;
    private int startPage;
    private String phrase;

    public Presenter(Contract.Views activityListener, GettyImagesAPI gettyImages, int pageSize){
        this.activityListener = activityListener;
        this.gettyImagesAPI = gettyImages;
        this.pageSize = pageSize;
    }

    @Override
    public void loadImagesByPhrase(String phrase){
        this.activityListener.removeAllImages();
        this.startPage = 1;
        this.phrase = phrase;
        loadMoreImages();
    }

    @Override
    public void loadMoreImages(){
        EspressoIdlingResource.increment();
        gettyImagesAPI.getImages(startPage, phrase, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GettyImage>>() {
                    @Override
                    public void onSubscribe(Disposable d){

                    }

                    @Override
                    public void onNext(List<GettyImage> images){
                        if (images.size() > 0) {
                            startPage++;
                            activityListener.addImages(images);
                        } else {
                            activityListener.noImagesFound();
                        }
                        EspressoIdlingResource.decrement();
                    }

                    @Override
                    public void onError(Throwable e){
                        EspressoIdlingResource.decrement();
                        activityListener.onLoadImagesFail();
                    }

                    @Override
                    public void onComplete(){
                    }
                }
        );
    }


}
