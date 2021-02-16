//package com.garzoopvt.garzoo.Chat.Services;
//
//
//import android.util.Log;
//
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import com.garzoopvt.garzoo.Chat.Model.ChatUser;
//import com.garzoopvt.garzoo.RetrofitService.AppExecutors;
//import com.garzoopvt.garzoo.RetrofitService.ServiceGenerator;
//import com.garzoopvt.garzoo.Util.URLs;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//
//import retrofit2.Call;
//import retrofit2.Response;
//
//import static com.garzoopvt.garzoo.Util.Constants.NETWORK_TIMEOUT;
//
//public class ChatApiClient {
//
//    private static final String TAG = "NotificationApiClient";
//    private static ChatApiClient instance;
//    private MutableLiveData<List<ChatUser>> mChat;
//    private RetrieveChatRunnable mRetrieveChatRunnable;
//
//    public static ChatApiClient getInstance(){
//        if(instance == null){
//            instance = new ChatApiClient();
//        }
//        return instance;
//    }
//
//    private ChatApiClient() {
//        mChat = new MutableLiveData<>();
//    }
//
//    public LiveData<List<ChatUser>> getChat(){
//        return mChat;
//    }
//
//
//    public void getChat(String user_id , int pageNumber){
//
//        if(mRetrieveChatRunnable != null){
//            mRetrieveChatRunnable = null;
//        }
//        mRetrieveChatRunnable = new RetrieveChatRunnable(user_id,pageNumber);
//        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveChatRunnable);
//
//        // Set a timeout for the data refresh
//        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
//            @Override
//            public void run() {
//                // let the user know it timed out
//                handler.cancel(true);
//            }
//        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
//    }
//
//
//    private class RetrieveChatRunnable implements Runnable{
//
//        private int pageNumber;
//        private String user_id;
//        private boolean cancelRequest;
//
//        private RetrieveChatRunnable(String user_id,int pageNumber) {
//            this.pageNumber = pageNumber;
//            this.user_id = user_id;
//            cancelRequest = false;
//        }
//
//        @Override
//        public void run() {
//
//            try {
//                Response response = getChat(user_id,pageNumber).execute();
//                if(cancelRequest){
//                    return;
//                }
//                if(response.code() == 200){
//                    List<ChatUser> list = new ArrayList<>(((ChatGroupResponse)response.body()).getChat());
//
//                    if(pageNumber == 1){
//                        mChat.postValue(list);
//                    }
//                    else{
//                        List<ChatUser> currentRecipes = mChat.getValue();
//                        currentRecipes.addAll(list);
//                        mChat.postValue(currentRecipes);
//                    }
//                }
//                else{
//                    String error = response.errorBody().string();
//                    Log.e(TAG, "run: error: " + error);
//                    mChat.postValue(null);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                mChat.postValue(null);
//            }
//        }
//
//        private Call<ChatGroupResponse> getChat(String user_id, int pageNumber){
//            return ServiceGenerator.getChatApi().getUser(
//                    URLs.unique_id,
//                    user_id,
//                    String.valueOf(pageNumber));
//        }
//
//        private void cancelRequest(){
//            Log.d(TAG, "cancelRequest: canceling the retrieval query");
//            cancelRequest = true;
//        }
//    }
//
//
//    public void cancelRequest(){
//        if(mRetrieveChatRunnable!=null)
//            mRetrieveChatRunnable.cancelRequest();
//    }
//}
