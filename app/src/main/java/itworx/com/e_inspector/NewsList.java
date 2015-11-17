package itworx.com.e_inspector;

/**
 * Created by karim on 10/29/2015.
 */
public class NewsList {
    public String ImageRelativeUrl;
    public String PublishingContent;
    public String NewsDate;
    public byte[] Image;
    public Boolean DoesUserLikePost;
    public String ItemFullUrl;
    public String ID;
    public int NumOfLikes;
    public String Title;


    @Override
    public String toString()
    {
        return "NewsList [ImageRelativeUrl = "+ImageRelativeUrl+", PublishingContent = "+PublishingContent+", NewsDate = "+NewsDate+", Image = "+Image+", DoesUserLikePost = "+DoesUserLikePost+", ItemFullUrl = "+ItemFullUrl+", ID = "+ID+", NumOfLikes = "+NumOfLikes+", Title = "+Title+"]";
    }
}
