package demo.victormunoz.gettyimagesdemo.ui.search;

import java.util.List;

import demo.victormunoz.gettyimagesdemo.injection.module.RetrofitModule.GettyImagesAPI;
import demo.victormunoz.gettyimagesdemo.model.GettyImage;
import demo.victormunoz.gettyimagesdemo.utils.espresso.EspressoIdlingResource;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Presenter implements Contract.UserActionsListener {
    private  Contract.Views activityListener;
    private  GettyImagesAPI gettyImagesAPI;
    private final Integer pageSize;
    private Integer startPage;
    private String phrase;
    private Subscription subscription;

    public Presenter(Contract.Views activityListener, GettyImagesAPI gettyImages, int pageSize) {
        this.activityListener = activityListener;
        this.gettyImagesAPI = gettyImages;
        this.pageSize = pageSize;
    }

    @Override
    public void loadImagesByPhrase(String phrase) {
        this.activityListener.removeAllImages();
        this.startPage = 1;
        this.phrase = phrase;
        loadMoreImages();
    }

    @Override
    public void loadMoreImages() {
        EspressoIdlingResource.increment();
        Observable<List<GettyImage>> observable = gettyImagesAPI.getImages( phrase,startPage, pageSize);
        subscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GettyImage>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        activityListener.onLoadImagesFail();
                        EspressoIdlingResource.decrement();
                    }

                    @Override
                    public void onNext(List<GettyImage> images) {
                        if (images.size() > 0) {
                            startPage++;
                            activityListener.addImages(images);
                        } else {
                            activityListener.noImagesFound();
                        }
                        EspressoIdlingResource.decrement();
                    }
                });
    }
    void destroy() {
        activityListener = null;
        if(subscription !=null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}
