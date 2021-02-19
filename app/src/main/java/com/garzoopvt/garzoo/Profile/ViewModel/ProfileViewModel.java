package com.garzoopvt.garzoo.Profile.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.garzoopvt.garzoo.Dashboard.Model.DashboardList;
import com.garzoopvt.garzoo.Login.data.LoginRepository;
import com.garzoopvt.garzoo.Login.data.LoginResult;
import com.garzoopvt.garzoo.Profile.Model.BlockedPeople;
import com.garzoopvt.garzoo.Profile.Services.ProfileRepository;
import com.garzoopvt.garzoo.RetrofitService.Resource;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ProfileViewModel extends AndroidViewModel {

    private static final String TAG = "ProfileViewModel";
    private ProfileRepository repository;
    private boolean mIsPerformingQuery;
    private boolean cancelRequest;
    private long requestStartTime;


    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = ProfileRepository.getInstance(application);
    }


    public Call<ResponseBody> contactUs(String mobile, String email, String message, String name) {
        return repository.contactUs(mobile, email, message, name);
    }

    public Call<ResponseBody> feedback(String mobile, String email, String message, String name) {
        return repository.feedback(mobile, email, message, name);
    }


    // query extras
    private boolean isQueryExhausted;
    private String query;
    private int pageNumber;
    private boolean isPerformingQuery;
    public static final String QUERY_EXHAUSTED = "Query is exhausted.";
    private MediatorLiveData<Resource<List<DashboardList>>> dashbordlist = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<DashboardList>>> mylist = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<BlockedPeople>>> blockedlist = new MediatorLiveData<>();

    public int getPageNumber() {
        return pageNumber;
    }


    //Start Interested List
    public LiveData<Resource<List<DashboardList>>> getInterested() {
        return dashbordlist;
    }

    public void getInterestedListApi(String user_id, int page_no, String search_name, String latitude, String longitude) {
        if (!isPerformingQuery) {
            if (pageNumber == 0) {
                pageNumber = 1;
            }
            this.pageNumber = page_no;
            this.query = search_name;
            isQueryExhausted = false;
            executeList(user_id, pageNumber, query, latitude, longitude);
        }
    }

    public void searchNextPage(String user_id, String search_name, String latitude, String longitude) {
        if (!isQueryExhausted && !isPerformingQuery) {
            pageNumber++;
            executeList(user_id, pageNumber, search_name, latitude, longitude);
        }
    }

    private void executeList(String user_id, int page_no, String search_name, String latitude, String longitude) {
        requestStartTime = System.currentTimeMillis();
        isPerformingQuery = true;
        cancelRequest = false;

        final LiveData<Resource<List<DashboardList>>> repositorySource = repository.getInterestedListApi(user_id, page_no, search_name, latitude, longitude);
        dashbordlist.addSource(repositorySource, new Observer<Resource<List<DashboardList>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<DashboardList>> listResource) {
                if (!cancelRequest) {
                    if (listResource != null) {
                        dashbordlist.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                    dashbordlist.setValue(new Resource<List<DashboardList>>(
                                            Resource.Status.ERROR,
                                            listResource.data,
                                            QUERY_EXHAUSTED
                                    ));
                                    isPerformingQuery = true;
                                }
                            }
                            // must remove or it will keep listening to repository
                            dashbordlist.removeSource(repositorySource);
                        } else if (listResource.status == Resource.Status.ERROR) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            dashbordlist.removeSource(repositorySource);
                        }
                    } else {
                        dashbordlist.removeSource(repositorySource);
                    }
                } else {
                    dashbordlist.removeSource(repositorySource);
                }
            }
        });

    }

    // End Interested List


    public void cancelSearchRequest(boolean isPerformingQuery) {
        if (isPerformingQuery) {
            Log.d(TAG, "cancelSearchRequest: canceling the search request.");
            cancelRequest = true;
            isPerformingQuery = false;
            pageNumber = 1;
        }
    }


    //  Add  Interest
    public Call<ResponseBody> interest(RequestBody unique_id, RequestBody user_id, RequestBody to_user_id, RequestBody full_name,
                                       RequestBody listing_id, RequestBody type, RequestBody listing_title) {

        return repository.interest(unique_id, user_id, to_user_id, full_name, listing_id, type, listing_title);

    }

    // End Interest



    //Start Block
     public LiveData<Resource<List<BlockedPeople>>> getBlocked() {
        return blockedlist;
    }
    public void getBlockedListApi(String user_id, int page_no) {
        if (!isPerformingQuery) {
            if (pageNumber == 0) {
                pageNumber = 1;
            }
            this.pageNumber = page_no;
            isQueryExhausted = false;
            executeBlokedList(user_id, pageNumber);
        }
    }
    public void NextBlockedListPage(String user_id) {
        if (!isQueryExhausted && !isPerformingQuery) {
            pageNumber++;
            executeBlokedList(user_id, pageNumber);
        }
    }
    private void executeBlokedList(String user_id, int page_no) {
        requestStartTime = System.currentTimeMillis();
        isPerformingQuery = true;
        cancelRequest = false;

        final LiveData<Resource<List<BlockedPeople>>> repositorySource = repository.getBlokedListApi(user_id, page_no,"");
        blockedlist.addSource(repositorySource, new Observer<Resource<List<BlockedPeople>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<BlockedPeople>> listResource) {
                if (!cancelRequest) {
                    if (listResource != null) {
                        blockedlist.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                    blockedlist.setValue(new Resource<List<BlockedPeople>>(
                                            Resource.Status.ERROR,
                                            listResource.data,
                                            QUERY_EXHAUSTED
                                    ));
                                    isPerformingQuery = true;
                                }
                            }
                            // must remove or it will keep listening to repository
                            blockedlist.removeSource(repositorySource);
                        } else if (listResource.status == Resource.Status.ERROR) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            blockedlist.removeSource(repositorySource);
                        }
                    } else {
                        blockedlist.removeSource(repositorySource);
                    }
                } else {
                    blockedlist.removeSource(repositorySource);
                }
            }
        });

    }
    //End Block



    //Start My List
    public LiveData<Resource<List<DashboardList>>> getMyList() {
        return mylist;
    }

    public void getMyListApi(String user_id, int page_no, String search_name, String latitude, String longitude) {
        if (!isPerformingQuery) {
            if (pageNumber == 0) {
                pageNumber = 1;
            }
            this.pageNumber = page_no;
            this.query = search_name;
            isQueryExhausted = false;
            executeListMyList(user_id, pageNumber, query, latitude, longitude);
        }
    }

    public void searchMyListNextPage(String user_id, String search_name, String latitude, String longitude) {
        if (!isQueryExhausted && !isPerformingQuery) {
            pageNumber++;
            executeListMyList(user_id, pageNumber, search_name, latitude, longitude);
        }
    }

    private void executeListMyList(String user_id, int page_no, String search_name, String latitude, String longitude) {
        requestStartTime = System.currentTimeMillis();
        isPerformingQuery = true;
        cancelRequest = false;

        final LiveData<Resource<List<DashboardList>>> repositorySource = repository.getMyListApi(user_id, page_no, search_name, latitude, longitude);
        mylist.addSource(repositorySource, new Observer<Resource<List<DashboardList>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<DashboardList>> listResource) {
                if (!cancelRequest) {
                    if (listResource != null) {
                        mylist.setValue(listResource);
                        if (listResource.status == Resource.Status.SUCCESS) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                    mylist.setValue(new Resource<List<DashboardList>>(
                                            Resource.Status.ERROR,
                                            listResource.data,
                                            QUERY_EXHAUSTED
                                    ));
                                    isPerformingQuery = true;
                                }
                            }
                            // must remove or it will keep listening to repository
                            mylist.removeSource(repositorySource);
                        } else if (listResource.status == Resource.Status.ERROR) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            mylist.removeSource(repositorySource);
                        }
                    } else {
                        mylist.removeSource(repositorySource);
                    }
                } else {
                    mylist.removeSource(repositorySource);
                }
            }
        });

    }

    // End My List










}