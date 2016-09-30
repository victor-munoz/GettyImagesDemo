package demo.victormunoz.gettyimagesdemo.ui.search;

import java.util.List;
import demo.victormunoz.gettyimagesdemo.model.GettyImage;

public interface Contract {

    interface Views {

        void addImages(List<GettyImage> images);

        void removeAllImages();

        void onLoadImagesFail();

        void noImagesFound();

    }

    interface UserActionsListener {

        void loadImagesByPhrase(String phrase);

        void loadMoreImages();

    }
}
