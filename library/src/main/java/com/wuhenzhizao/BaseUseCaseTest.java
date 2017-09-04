package com.wuhenzhizao;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.MockRepository;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wuhenzhizao on 2017/9/4.
 */
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "javax.net.ssl.*"})
public class BaseUseCaseTest extends Assert {
    protected OkHttpClientStub clientStub;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    protected void onStart() {
        MockitoAnnotations.initMocks(this);
        initMockServer();
    }

    @After
    protected void onFinish() {
        MockRepository.clear();
    }

    private void initMockServer() {
        clientStub = new OkHttpClientStub();

        String baseUrl = "https://localhost:80";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(clientStub)
                .build();

        Whitebox.setInternalState(ApiManager.instance(), "retrofit", retrofit);
    }

    protected void mockServerRequest(String path, int code, String body) throws Exception {
        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), body);
        Response.Builder mockBuilder = new Response.Builder()
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .body(responseBody);

        clientStub.mockResponse(path, mockBuilder);
    }
}
