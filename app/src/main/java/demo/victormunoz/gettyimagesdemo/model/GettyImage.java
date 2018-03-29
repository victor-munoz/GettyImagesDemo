package demo.victormunoz.gettyimagesdemo.model;


public class GettyImage {
    private String id;
    private String title;
    private String caption;

    public String getId(){
        return id;
    }

    @SuppressWarnings("unused")
    public void setId(String id){
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    @SuppressWarnings("unused")
    public void setTitle(String title){
        this.title = title;
    }

    public String getCaption(){
        return caption;
    }

    @SuppressWarnings("unused")
    public void setCaption(String caption){
        this.caption = caption;
    }


}
