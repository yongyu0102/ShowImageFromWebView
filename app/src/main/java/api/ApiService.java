package api;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Description:
 * Author：pz
 * Date：2016/10/24:14:45
 */
public interface ApiService {

    @GET
    Observable<ResponseBody> downLoadImage(@Url String url);
}
