package com.creativethoughts.iscore.utility.network;

public interface ResponseManager {
    void onSuccess( String result );
    void onError( String error );
}
