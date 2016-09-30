package demo.victormunoz.gettyimagesdemo.ui.search;
import java.util.List;

import demo.victormunoz.gettyimagesdemo.injection.module.RetrofitModule.GettyImagesAPI;
import demo.victormunoz.gettyimagesdemo.model.GettyImage;
import demo.victormunoz.gettyimagesdemo.utils.espresso.EspressoIdlingResource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Presenter implements Contract.UserActionsListener, Callback<List<GettyImage>> {
    private final Contract.Views activityListener;
    private final GettyImagesAPI gettyImagesAPI;
    private final int pageSize;
    private int startPage;
    private String phrase;

    public Presenter (Contract.Views activityListener,GettyImagesAPI gettyImages, int pageSize){
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
        Call<List<GettyImage>> call = gettyImagesAPI.getImages(startPage, phrase, pageSize);
        call.enqueue( this);
        EspressoIdlingResource.increment();
    }

    @Override
    public void onResponse(Call<List<GettyImage>> call, Response<List<GettyImage>> response) {
        if (response.isSuccessful()) {
            List<GettyImage> images = response.body();
            if(images.size()>0){
                startPage++;
                activityListener.addImages(images);
            }
            else{
                activityListener.noImagesFound();
            }
        }
        EspressoIdlingResource.decrement();
    }

    @Override
    public void onFailure(Call<List<GettyImage>> call, Throwable t) {
        EspressoIdlingResource.decrement();
        activityListener.onLoadImagesFail();
    }
}
