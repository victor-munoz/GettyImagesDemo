package demo.victormunoz.gettyimagesdemo.injection.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import demo.victormunoz.gettyimagesdemo.model.GettyImage;
import demo.victormunoz.gettyimagesdemo.utils.Retrofit.Deserializer;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;


@Module
public class GettyImagesModule {
    private final static String OBJECT_TO_DESEREALIZE ="images";
    private final static String END_POINT ="https://api.gettyimages.com/v3/search/";

    public interface GettyImagesAPI {
        @Headers("Api-Key: cw7pfqdwgxbv9vp98qmz5bk4")
        @GET("images/")
        Call<List<GettyImage>> getImages(
                @Query("page") int startingPage,
                @Query("phrase") String phrase,
                @Query("page_size") int size);

    }
    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new Deserializer(OBJECT_TO_DESEREALIZE))
                .create();
    }
    @Provides
    @Singleton
    retrofit2.Retrofit provideRetrofit(Gson gson) {
        return new retrofit2.Retrofit.Builder()
                .baseUrl(END_POINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    @Provides
    @Singleton
    GettyImagesAPI providesGettyImagesInterface(retrofit2.Retrofit retrofit) {
        return retrofit.create(GettyImagesAPI.class);
    }
}
